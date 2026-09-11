package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneTextWeight

internal class FlowchartParser {
    fun parse(source: String): GMResult<FlowchartDocument, MermaidError> {
        val statements = splitStatements(source)
            .filterNot { it.text.isBlank() || it.text.trimStart().startsWith("%%") }
        val header = statements.firstOrNull()
            ?: return parseError(1, 1, "Flowchart source is empty")
        val headerMatch = HEADER.matchEntire(header.text.trim())
            ?: return parseError(header.line, 1, "Expected 'flowchart' or 'graph' header")
        val direction = parseDirection(headerMatch.groupValues[2])
            ?: return parseError(header.line, 1, "Unknown flowchart direction")

        val state = ParseState(direction)
        for (statement in statements.drop(1)) {
            when (val result = parseStatement(statement, state)) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }

        while (state.subgraphStack.isNotEmpty()) {
            val open = state.subgraphStack.last()
            return parseError(open.line, 1, "Subgraph '${open.id}' is missing 'end'")
        }
        if (state.nodes.isEmpty()) {
            return parseError(header.line, 1, "Flowchart contains no nodes")
        }

        val subgraphIds = state.subgraphs.mapTo(mutableSetOf(), FlowSubgraph::id)
        return GMResult.Ok(
            FlowchartDocument(
                direction = state.direction,
                nodes = state.nodes.filterKeys { it !in subgraphIds },
                edges = state.edges,
                subgraphs = state.subgraphs,
                classStyles = state.classStyles,
            ),
        )
    }

    private fun parseStatement(
        statement: SourceStatement,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val text = statement.text.trim()
        if (text == "end") {
            val open = state.subgraphStack.removeLastOrNull()
                ?: return parseError(statement.line, 1, "Unexpected 'end'")
            state.subgraphs += FlowSubgraph(
                id = open.id,
                label = open.label,
                nodeIds = open.nodeIds,
                direction = open.direction,
                parentId = open.parentId,
                collapsed = open.collapsed,
            )
            return GMResult.Ok(Unit)
        }
        if (text.startsWith("subgraph ")) {
            return parseSubgraph(text.removePrefix("subgraph ").trim(), statement.line, state)
        }
        if (text.startsWith("direction ")) {
            val parsed = parseDirection(text.removePrefix("direction ").trim())
                ?: return parseError(statement.line, 1, "Unknown subgraph direction")
            val open = state.subgraphStack.lastOrNull()
                ?: return parseError(statement.line, 1, "'direction' is only valid inside a subgraph")
            open.direction = parsed
            return GMResult.Ok(Unit)
        }
        if (text.startsWith("classDef ")) {
            return parseClassDefinition(text, statement.line, state)
        }
        if (text.startsWith("class ")) {
            return parseClassAssignment(text, statement.line, state)
        }
        if (text.startsWith("style ")) {
            return parseInlineStyle(text, statement.line, state)
        }
        if (text.startsWith("linkStyle ")) {
            return parseLinkStyle(text, statement.line, state)
        }
        if (
            text.startsWith("click ") ||
            text.startsWith("accTitle:") ||
            text.startsWith("accDescr:")
        ) {
            return GMResult.Ok(Unit)
        }
        val metadataId = text.substringBefore("@{").trim()
        if (text.contains("@{")) {
            val metadataBody = text.substringAfter("@{").substringBeforeLast('}')
            val subgraphIndex = state.subgraphs.indexOfFirst { it.id == metadataId }
            if (subgraphIndex >= 0) {
                val values = parseMetadataValues(metadataBody)
                val current = state.subgraphs[subgraphIndex]
                state.subgraphs[subgraphIndex] = current.copy(
                    collapsed = values["view"].equals("collapsed", ignoreCase = true),
                )
                return GMResult.Ok(Unit)
            }
            if (state.edges.any { it.id == metadataId }) {
                return GMResult.Ok(Unit)
            }
        }

        return parseNodeChain(text, statement.line, state)
    }

    private fun parseSubgraph(
        declaration: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        if (declaration.isBlank()) {
            return parseError(line, 1, "Subgraph requires an id or label")
        }

        val bracketStart = declaration.indexOf('[')
        val bracketEnd = declaration.lastIndexOf(']')
        val id: String
        val label: String
        if (bracketStart > 0 && bracketEnd > bracketStart) {
            id = declaration.substring(0, bracketStart).trim()
            label = cleanLabel(declaration.substring(bracketStart + 1, bracketEnd))
        } else {
            label = cleanLabel(declaration)
            id = declaration
                .lowercase()
                .map { char -> if (char.isLetterOrDigit()) char else '_' }
                .joinToString("")
                .trim('_')
                .ifEmpty { "subgraph_${state.subgraphs.size}" }
        }

        state.subgraphStack += MutableSubgraph(
            id = id,
            label = label,
            line = line,
            parentId = state.subgraphStack.lastOrNull()?.id,
        )
        return GMResult.Ok(Unit)
    }

    private fun parseClassDefinition(
        statement: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val body = statement.removePrefix("classDef ").trim()
        val split = body.indexOf(' ')
        if (split <= 0 || split == body.lastIndex) {
            return parseError(line, 1, "classDef requires a name and style declarations")
        }
        val names = body.substring(0, split)
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
        return when (val style = parseStyle(body.substring(split + 1), line)) {
            is GMResult.Ok -> {
                names.forEach { name -> state.classStyles[name] = style.value }
                GMResult.Ok(Unit)
            }
            is GMResult.Err -> style
        }
    }

    private fun parseClassAssignment(
        statement: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val body = statement.removePrefix("class ").trim()
        val split = body.lastIndexOf(' ')
        if (split <= 0 || split == body.lastIndex) {
            return parseError(line, 1, "class requires node ids and a class name")
        }
        val ids = body.substring(0, split).split(',').map(String::trim).filter(String::isNotEmpty)
        val className = body.substring(split + 1).trim()
        ids.forEach { id ->
            val edgeIndexes = state.edges.indices.filter { state.edges[it].id == id }
            if (edgeIndexes.isNotEmpty()) {
                edgeIndexes.forEach { edgeIndex ->
                    val current = state.edges[edgeIndex]
                    state.edges[edgeIndex] = current.copy(classes = current.classes + className)
                }
            } else {
                val current = state.nodes[id] ?: FlowNode(id, id, SceneShapeKind.Rectangle)
                state.nodes[id] = current.copy(classes = current.classes + className)
                state.recordNode(id)
            }
        }
        return GMResult.Ok(Unit)
    }

    private fun parseLinkStyle(
        statement: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val body = statement.removePrefix("linkStyle ").trim()
        val split = body.indexOf(' ')
        if (split <= 0 || split == body.lastIndex) {
            return parseError(line, 1, "linkStyle requires edge indexes and declarations")
        }
        val selector = body.substring(0, split)
        val styleSource = body.substring(split + 1)
            .substringAfterLast(" interpolate ", missingDelimiterValue = body.substring(split + 1))
        return when (val style = parseStyle(styleSource, line)) {
            is GMResult.Ok -> {
                val indexes = if (selector == "default") {
                    state.edges.indices.toList()
                } else {
                    selector.split(',').mapNotNull { it.trim().toIntOrNull() }
                }
                indexes.forEach { index ->
                    if (index in state.edges.indices) {
                        state.edges[index] = state.edges[index].copy(inlineStyle = style.value)
                    }
                }
                GMResult.Ok(Unit)
            }
            is GMResult.Err -> style
        }
    }

    private fun parseInlineStyle(
        statement: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val body = statement.removePrefix("style ").trim()
        val split = body.indexOf(' ')
        if (split <= 0 || split == body.lastIndex) {
            return parseError(line, 1, "style requires a node id and declarations")
        }
        val id = body.substring(0, split)
        return when (val style = parseStyle(body.substring(split + 1), line)) {
            is GMResult.Ok -> {
                val current = state.nodes[id] ?: FlowNode(id, id, SceneShapeKind.Rectangle)
                state.nodes[id] = current.copy(inlineStyle = style.value)
                state.recordNode(id)
                GMResult.Ok(Unit)
            }
            is GMResult.Err -> style
        }
    }

    private fun parseStyle(
        source: String,
        line: Int,
    ): GMResult<FlowNodeStyle, MermaidError> {
        var fill: SceneColor? = null
        var stroke: SceneColor? = null
        var text: SceneColor? = null
        var strokeWidth: Float? = null
        var strokePattern: SceneStrokePattern? = null
        var fontSize: Float? = null
        var fontWeight: SceneTextWeight? = null

        splitStyleDeclarations(source).forEach { declaration ->
            val pair = declaration.split(':', limit = 2)
            if (pair.size != 2) {
                return@forEach
            }
            val key = pair[0].trim()
            val value = pair[1].trim()
            when (key) {
                "fill", "background" -> when (val color = parseColor(value, line)) {
                    is GMResult.Ok -> fill = color.value
                    is GMResult.Err -> return color
                }
                "stroke" -> when (val color = parseColor(value, line)) {
                    is GMResult.Ok -> stroke = color.value
                    is GMResult.Err -> return color
                }
                "color" -> when (val color = parseColor(value, line)) {
                    is GMResult.Ok -> text = color.value
                    is GMResult.Err -> return color
                }
                "stroke-width" -> {
                    strokeWidth = value.removeSuffix("px").toFloatOrNull()
                        ?: return parseError(line, 1, "Invalid stroke width '$value'")
                }
                "stroke-dasharray" -> {
                    strokePattern = SceneStrokePattern.Dashed
                }
                "font-size" -> {
                    fontSize = value.removeSuffix("px").removeSuffix("pt").toFloatOrNull()
                        ?: return parseError(line, 1, "Invalid font size '$value'")
                }
                "font-weight" -> {
                    fontWeight = when (value.lowercase()) {
                        "bold", "600", "700", "800", "900" -> SceneTextWeight.Bold
                        "normal", "400", "500" -> SceneTextWeight.Normal
                        else -> return parseError(line, 1, "Invalid font weight '$value'")
                    }
                }
                "border" -> {
                    val parts = value.split(Regex("""\s+"""))
                    strokeWidth = parts.firstOrNull()
                        ?.removeSuffix("px")
                        ?.toFloatOrNull()
                        ?: strokeWidth
                    val borderColor = parts.lastOrNull()
                    if (borderColor != null) {
                        when (val color = parseColor(borderColor, line)) {
                            is GMResult.Ok -> stroke = color.value
                            is GMResult.Err -> return color
                        }
                    }
                }
            }
        }
        return GMResult.Ok(
            FlowNodeStyle(
                fill = fill,
                stroke = stroke,
                text = text,
                strokeWidth = strokeWidth,
                strokePattern = strokePattern,
                fontSize = fontSize,
                fontWeight = fontWeight,
            ),
        )
    }

    private fun splitStyleDeclarations(source: String): List<String> = buildList {
        val current = StringBuilder()
        var escaped = false
        var parenthesesDepth = 0
        source.forEach { char ->
            when {
                escaped -> {
                    current.append(char)
                    escaped = false
                }
                char == '\\' -> escaped = true
                char == '(' -> {
                    parenthesesDepth += 1
                    current.append(char)
                }
                char == ')' -> {
                    parenthesesDepth = (parenthesesDepth - 1).coerceAtLeast(0)
                    current.append(char)
                }
                char == ',' && parenthesesDepth == 0 -> {
                    add(current.toString())
                    current.clear()
                }
                else -> current.append(char)
            }
        }
        if (escaped) {
            current.append('\\')
        }
        add(current.toString())
    }

    private fun parseColor(
        source: String,
        line: Int,
    ): GMResult<SceneColor, MermaidError> =
        CssColorParser.parse(source)
            ?.let { GMResult.Ok(it) }
            ?: parseError(line, 1, "Unsupported color '$source'")

    private fun parseNodeChain(
        text: String,
        line: Int,
        state: ParseState,
    ): GMResult<Unit, MermaidError> {
        val first = when (val parsed = parseNodeGroup(text, 0, line)) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        first.nodes.forEach(state::upsert)
        var previous = first.nodes.map { it.node.id }
        var index = first.nextIndex
        var edgeCount = 0

        while (index < text.length) {
            index = skipSpaces(text, index)
            if (index >= text.length) {
                break
            }
            val edge = parseEdge(text, index, line)
                ?: return parseError(line, index + 1, "Expected a supported flowchart edge")
            val next = when (val parsed = parseNodeGroup(text, edge.nextIndex, line)) {
                is GMResult.Ok -> parsed.value
                is GMResult.Err -> return parsed
            }
            next.nodes.forEach(state::upsert)
            val targets = next.nodes.map { it.node.id }
            previous.forEach { from ->
                targets.forEach { to ->
                    state.edges += FlowEdge(
                        id = edge.id ?: "edge_${state.edges.size}",
                        from = from,
                        to = to,
                        label = edge.label,
                        pattern = edge.pattern,
                        arrowStart = edge.arrowStart,
                        arrowEnd = edge.arrowEnd,
                        thickness = edge.thickness,
                        minimumLength = edge.minimumLength,
                        invisible = edge.invisible,
                    )
                }
            }
            previous = targets
            index = next.nextIndex
            edgeCount += 1
        }

        if (edgeCount == 0 && index < text.length) {
            return parseError(line, index + 1, "Unexpected flowchart syntax")
        }
        return GMResult.Ok(Unit)
    }

    private fun parseNodeGroup(
        text: String,
        start: Int,
        line: Int,
    ): GMResult<ParsedNodeGroup, MermaidError> {
        val nodes = mutableListOf<ParsedNode>()
        var index = start
        while (true) {
            val parsed = when (val result = parseStyledNode(text, index, line)) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
            nodes += parsed
            index = skipSpaces(text, parsed.nextIndex)
            if (index >= text.length || text[index] != '&') {
                break
            }
            index += 1
        }
        return GMResult.Ok(ParsedNodeGroup(nodes, index))
    }

    private fun parseStyledNode(
        text: String,
        start: Int,
        line: Int,
    ): GMResult<ParsedNode, MermaidError> {
        val parsed = when (val result = parseNode(text, start, line)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        var index = skipSpaces(text, parsed.nextIndex)
        if (!text.startsWith(":::", index)) {
            return GMResult.Ok(parsed.copy(nextIndex = index))
        }
        index += 3
        val classStart = index
        while (index < text.length) {
            val char = text[index]
            if (char.isWhitespace() || char == '&' || startsWithEdge(text, index)) {
                break
            }
            index += 1
        }
        val className = text.substring(classStart, index)
        if (className.isEmpty()) {
            return parseError(line, classStart + 1, "Expected a class name after ':::'")
        }
        return GMResult.Ok(
            parsed.copy(
                node = parsed.node.copy(classes = parsed.node.classes + className),
                nextIndex = index,
            ),
        )
    }

    private fun parseNode(
        text: String,
        start: Int,
        line: Int,
    ): GMResult<ParsedNode, MermaidError> {
        var index = skipSpaces(text, start)
        val idStart = index
        while (index < text.length && isIdCharacter(text, index, idStart)) {
            index += 1
        }
        if (index == idStart) {
            return parseError(line, index + 1, "Expected a node id")
        }
        val id = text.substring(idStart, index)
        index = skipSpaces(text, index)
        if (index >= text.length || startsWithEdge(text, index)) {
            return GMResult.Ok(
                ParsedNode(
                    node = FlowNode(id, id, SceneShapeKind.Rectangle),
                    nextIndex = index,
                    explicitDefinition = false,
                ),
            )
        }

        if (text.startsWith("@{", index)) {
            val closing = text.indexOf('}', index + 2)
            if (closing == -1) {
                return parseError(line, index + 1, "Unclosed node metadata")
            }
            return parseMetadataNode(id, text.substring(index + 2, closing), closing + 1, line)
        }

        val matchingShapes = SHAPE_DELIMITERS.filter { text.startsWith(it.open, index) }
        val shapeMatch = matchingShapes
            .mapNotNull { delimiter ->
                val closing = text.indexOf(delimiter.close, index + delimiter.open.length)
                if (closing >= 0) delimiter to closing else null
            }
            .minByOrNull { it.second }
        if (shapeMatch == null && matchingShapes.isNotEmpty()) {
            return parseError(line, index + 1, "Unclosed node label for '$id'")
        }
        val shape = shapeMatch?.first
        shape
            ?: return GMResult.Ok(
                ParsedNode(
                    node = FlowNode(id, id, SceneShapeKind.Rectangle),
                    nextIndex = index,
                    explicitDefinition = false,
                ),
            )
        val labelStart = index + shape.open.length
        val closing = shapeMatch.second
        val label = cleanLabel(text.substring(labelStart, closing))
        return GMResult.Ok(
            ParsedNode(
                node = FlowNode(id, label.ifEmpty { id }, shape.kind),
                nextIndex = closing + shape.close.length,
                explicitDefinition = true,
            ),
        )
    }

    private fun parseMetadataNode(
        id: String,
        metadata: String,
        nextIndex: Int,
        line: Int,
    ): GMResult<ParsedNode, MermaidError> {
        val values = parseMetadataValues(metadata)
        val shapeName = values["shape"]?.lowercase() ?: "rect"
        val shape = METADATA_SHAPES[shapeName]
            ?: return parseError(line, 1, "Unsupported flowchart shape '$shapeName'")
        val label = cleanLabel(values["label"] ?: values["icon"] ?: id)
        return GMResult.Ok(
            ParsedNode(
                node = FlowNode(id, label, shape, metadata = values),
                nextIndex = nextIndex,
                explicitDefinition = true,
            ),
        )
    }

    private fun parseMetadataValues(metadata: String): Map<String, String> = metadata
        .split(',')
        .mapNotNull { entry ->
            val pair = entry.split(':', limit = 2)
            if (pair.size == 2) pair[0].trim() to pair[1].trim().trim('"', '\'') else null
        }
        .toMap()

    private fun parseEdge(
        text: String,
        start: Int,
        line: Int,
    ): ParsedEdge? {
        var index = skipSpaces(text, start)
        var edgeId: String? = null
        val edgeIdMatch = EDGE_ID_PREFIX.find(text.substring(index))
        if (edgeIdMatch != null && edgeIdMatch.range.first == 0) {
            edgeId = edgeIdMatch.groupValues[1]
            index += edgeIdMatch.value.length
        }
        val remaining = text.substring(index)
        for (syntax in GENERIC_LABELED_EDGES) {
            val match = syntax.regex.find(remaining)
            if (match != null && match.range.first == 0) {
                val startMarker = match.groupValues[1].firstOrNull()
                    ?.takeIf { it == 'x' || it == 'o' || it == '<' }
                val endToken = match.groupValues[3]
                val token = when (syntax.kind) {
                    LabeledEdgeKind.Normal -> "${startMarker ?: ""}$endToken"
                    LabeledEdgeKind.Thick -> "${startMarker ?: ""}$endToken"
                    LabeledEdgeKind.Dotted -> "${startMarker ?: ""}-$endToken"
                }
                return edgeFromToken(
                    id = edgeId,
                    token = token,
                    label = cleanLabel(match.groupValues[2]),
                    nextIndex = index + match.value.length,
                )
            }
        }
        for (pattern in LABELED_EDGE_PATTERNS) {
            val match = pattern.regex.find(remaining)
            if (match != null && match.range.first == 0) {
                return pattern.edge(
                    id = edgeId,
                    label = cleanLabel(match.groupValues[1]),
                    nextIndex = index + match.value.length,
                )
            }
        }

        val token = EDGE_TOKEN.find(remaining)
            ?.takeIf { it.range.first == 0 }
            ?.value
            ?: return null
        index += token.length
        index = skipSpaces(text, index)
        var label: String? = null
        if (index < text.length && text[index] == '|') {
            val closing = text.indexOf('|', index + 1)
            if (closing == -1) {
                return null
            }
            label = cleanLabel(text.substring(index + 1, closing))
            index = closing + 1
        }
        return edgeFromToken(edgeId, token, label, index)
    }

    private fun edgeFromToken(
        id: String?,
        token: String,
        label: String?,
        nextIndex: Int,
    ): ParsedEdge {
        val startMarker = token.firstOrNull()?.takeIf { it == 'x' || it == 'o' || it == '<' }
        val endMarker = token.lastOrNull()?.takeIf { it == 'x' || it == 'o' || it == '>' }
        val coreStart = if (startMarker == null) 0 else 1
        val coreEnd = token.length - if (endMarker == null) 0 else 1
        val core = token.substring(coreStart, coreEnd)
        val invisible = core.firstOrNull() == '~'
        val pattern = if (core.contains('.')) SceneStrokePattern.Dotted else SceneStrokePattern.Solid
        val thickness = if (core.contains('=')) 3f else 1.7f
        val minimumLength = when {
            invisible -> (core.count { it == '~' } - 2).coerceAtLeast(1)
            core.contains('.') -> core.count { it == '.' }.coerceAtLeast(1)
            core.contains('=') -> (
                core.count { it == '=' } - if (endMarker == null) 2 else 1
            ).coerceAtLeast(1)
            else -> (
                core.count { it == '-' } - if (endMarker == null) 2 else 1
            ).coerceAtLeast(1)
        }
        return ParsedEdge(
            id = id,
            label = label,
            pattern = pattern,
            arrowStart = when (startMarker) {
                '<' -> SceneArrowHead.Triangle
                'o' -> SceneArrowHead.Circle
                'x' -> SceneArrowHead.Cross
                else -> SceneArrowHead.None
            },
            arrowEnd = when (endMarker) {
                '>' -> SceneArrowHead.Triangle
                'o' -> SceneArrowHead.Circle
                'x' -> SceneArrowHead.Cross
                else -> SceneArrowHead.None
            },
            thickness = thickness,
            minimumLength = minimumLength,
            invisible = invisible,
            nextIndex = nextIndex,
        )
    }

    private fun splitStatements(source: String): List<SourceStatement> = buildList {
        source.lines().forEachIndexed { lineIndex, line ->
            var start = 0
            var depth = 0
            var quote: Char? = null
            line.forEachIndexed { index, char ->
                when {
                    quote != null && char == quote -> quote = null
                    quote != null -> Unit
                    char == '"' || char == '\'' -> quote = char
                    char == '[' || char == '(' || char == '{' -> depth += 1
                    char == ']' || char == ')' || char == '}' -> depth = (depth - 1).coerceAtLeast(0)
                    char == ';' && depth == 0 -> {
                        add(SourceStatement(line.substring(start, index), lineIndex + 1))
                        start = index + 1
                    }
                }
            }
            add(SourceStatement(line.substring(start), lineIndex + 1))
        }
    }

    private fun parseDirection(value: String): FlowDirection? = when (value.uppercase()) {
        "TB", "TD", "V", "" -> FlowDirection.TopToBottom
        "BT", "^" -> FlowDirection.BottomToTop
        "LR", ">" -> FlowDirection.LeftToRight
        "RL", "<" -> FlowDirection.RightToLeft
        else -> null
    }

    private fun cleanLabel(value: String): String {
        val plain = value
            .trim()
            .trim('"')
            .removeSurrounding("`")
            .replace("**", "")
            .replace("__", "")
            .replace("*", "")
            .replace("<br/>", "\n", ignoreCase = true)
            .replace("<br>", "\n", ignoreCase = true)
            .replace("#quot;", "\"")
            .replace("#amp;", "&")
            .replace("#lt;", "<")
            .replace("#gt;", ">")
        return DECIMAL_ENTITY.replace(plain) { match ->
            match.groupValues[1].toIntOrNull()?.takeIf { it in 0..0xFFFF }?.toChar()?.toString()
                ?: match.value
        }
    }

    private fun isIdCharacter(
        text: String,
        index: Int,
        idStart: Int,
    ): Boolean {
        val char = text[index]
        if (char == ':') {
            return !text.startsWith(":::", index)
        }
        if (char.isLetterOrDigit() || char == '_' || char == '.') {
            return true
        }
        if (char != '-') {
            return false
        }
        val next = text.getOrNull(index + 1)
        return index > idStart && next != '-'
    }

    private fun startsWithEdge(text: String, index: Int): Boolean {
        val remaining = text.substring(index)
        return EDGE_TOKEN.find(remaining)?.range?.first == 0 ||
            EDGE_ID_PREFIX.find(remaining)?.range?.first == 0 ||
            GENERIC_LABELED_EDGES.any { it.regex.containsMatchIn(remaining) } ||
            LABELED_EDGE_PATTERNS.any { it.regex.containsMatchIn(remaining) }
    }

    private fun skipSpaces(text: String, start: Int): Int {
        var index = start
        while (index < text.length && text[index].isWhitespace()) {
            index += 1
        }
        return index
    }

    private fun <T> parseError(
        line: Int,
        column: Int,
        message: String,
    ): GMResult<T, MermaidError> = GMResult.Err(MermaidError.Parse(line, column, message))

    private data class SourceStatement(
        val text: String,
        val line: Int,
    )

    private data class ParsedNode(
        val node: FlowNode,
        val nextIndex: Int,
        val explicitDefinition: Boolean,
    )

    private data class ParsedNodeGroup(
        val nodes: List<ParsedNode>,
        val nextIndex: Int,
    )

    private data class ParsedEdge(
        val id: String?,
        val label: String?,
        val pattern: SceneStrokePattern,
        val arrowStart: SceneArrowHead,
        val arrowEnd: SceneArrowHead,
        val thickness: Float,
        val minimumLength: Int,
        val invisible: Boolean,
        val nextIndex: Int,
    )

    private data class LabeledEdgePattern(
        val regex: Regex,
        val pattern: SceneStrokePattern,
        val arrowEnd: SceneArrowHead,
        val thickness: Float,
    ) {
        fun edge(id: String?, label: String, nextIndex: Int): ParsedEdge = ParsedEdge(
            id = id,
            label = label,
            pattern = pattern,
            arrowStart = SceneArrowHead.None,
            arrowEnd = arrowEnd,
            thickness = thickness,
            minimumLength = 1,
            invisible = false,
            nextIndex = nextIndex,
        )
    }

    private enum class LabeledEdgeKind {
        Normal,
        Thick,
        Dotted,
    }

    private data class GenericLabeledEdge(
        val regex: Regex,
        val kind: LabeledEdgeKind,
    )

    private data class ShapeDelimiter(
        val open: String,
        val close: String,
        val kind: SceneShapeKind,
    )

    private data class MutableSubgraph(
        val id: String,
        val label: String,
        val line: Int,
        val parentId: String?,
        val nodeIds: MutableSet<String> = linkedSetOf(),
        var direction: FlowDirection? = null,
        var collapsed: Boolean = false,
    )

    private class ParseState(
        val direction: FlowDirection,
        val nodes: MutableMap<String, FlowNode> = linkedMapOf(),
        val edges: MutableList<FlowEdge> = mutableListOf(),
        val subgraphs: MutableList<FlowSubgraph> = mutableListOf(),
        val classStyles: MutableMap<String, FlowNodeStyle> = linkedMapOf(),
        val subgraphStack: MutableList<MutableSubgraph> = mutableListOf(),
    ) {
        fun upsert(parsed: ParsedNode) {
            val current = nodes[parsed.node.id]
            nodes[parsed.node.id] = when {
                current == null -> parsed.node
                parsed.explicitDefinition -> parsed.node.copy(
                    classes = current.classes,
                    inlineStyle = current.inlineStyle,
                )
                else -> current
            }
            recordNode(parsed.node.id)
        }

        fun recordNode(id: String) {
            subgraphStack.forEach { it.nodeIds += id }
        }
    }

    private companion object {
        val HEADER = Regex("""^(flowchart(?:-elk)?|graph)(?:\s+(TB|TD|BT|LR|RL|>|<|\^|v))?\s*$""", RegexOption.IGNORE_CASE)
        val DECIMAL_ENTITY = Regex("""#(\d+);""")
        val EDGE_ID_PREFIX = Regex("""^([A-Za-z_][A-Za-z0-9_:.-]*)@(?=[xo<~=.-])""")
        val EDGE_TOKEN = Regex("""^[xo<]?(?:-{2,}[xo>]?|={2,}[xo>]?|-\.+-[xo>]?|~{3,})""")
        val GENERIC_LABELED_EDGES = listOf(
            GenericLabeledEdge(
                regex = Regex("""^([xo<]?--)\s+(.+?)\s+(--+[xo>]?)\s*"""),
                kind = LabeledEdgeKind.Normal,
            ),
            GenericLabeledEdge(
                regex = Regex("""^([xo<]?==)\s+(.+?)\s+(==+[xo>]?)\s*"""),
                kind = LabeledEdgeKind.Thick,
            ),
            GenericLabeledEdge(
                regex = Regex("""^([xo<]?-\.)\s+(.+?)\s+(\.+-[xo>]?)\s*"""),
                kind = LabeledEdgeKind.Dotted,
            ),
        )

        val LABELED_EDGE_PATTERNS = listOf(
            LabeledEdgePattern(
                Regex("""^--\s+(.+?)\s+-->\s*"""),
                SceneStrokePattern.Solid,
                SceneArrowHead.Triangle,
                1.7f,
            ),
            LabeledEdgePattern(
                Regex("""^-\.\s+(.+?)\s+\.->\s*"""),
                SceneStrokePattern.Dotted,
                SceneArrowHead.Triangle,
                1.7f,
            ),
            LabeledEdgePattern(
                Regex("""^==\s+(.+?)\s+==>\s*"""),
                SceneStrokePattern.Solid,
                SceneArrowHead.Triangle,
                3f,
            ),
            LabeledEdgePattern(
                Regex("""^--\s+(.+?)\s+---+\s*"""),
                SceneStrokePattern.Solid,
                SceneArrowHead.None,
                1.7f,
            ),
        )

        val SHAPE_DELIMITERS = listOf(
            ShapeDelimiter("(((", ")))", SceneShapeKind.DoubleCircle),
            ShapeDelimiter("((", "))", SceneShapeKind.Circle),
            ShapeDelimiter("([", "])", SceneShapeKind.Stadium),
            ShapeDelimiter("[[", "]]", SceneShapeKind.Subroutine),
            ShapeDelimiter("[(", ")]", SceneShapeKind.Cylinder),
            ShapeDelimiter("{{", "}}", SceneShapeKind.Hexagon),
            ShapeDelimiter("[/", "\\]", SceneShapeKind.Trapezoid),
            ShapeDelimiter("[\\", "/]", SceneShapeKind.TrapezoidAlt),
            ShapeDelimiter("[/", "/]", SceneShapeKind.Parallelogram),
            ShapeDelimiter("[\\", "\\]", SceneShapeKind.ParallelogramAlt),
            ShapeDelimiter(">", "]", SceneShapeKind.Asymmetric),
            ShapeDelimiter("(", ")", SceneShapeKind.RoundedRectangle),
            ShapeDelimiter("[", "]", SceneShapeKind.Rectangle),
            ShapeDelimiter("{", "}", SceneShapeKind.Diamond),
        )

        val METADATA_SHAPES = mapOf(
            "rect" to SceneShapeKind.Rectangle,
            "rounded" to SceneShapeKind.RoundedRectangle,
            "stadium" to SceneShapeKind.Stadium,
            "pill" to SceneShapeKind.Stadium,
            "subproc" to SceneShapeKind.Subroutine,
            "subroutine" to SceneShapeKind.Subroutine,
            "cyl" to SceneShapeKind.Cylinder,
            "cylinder" to SceneShapeKind.Cylinder,
            "database" to SceneShapeKind.Cylinder,
            "circle" to SceneShapeKind.Circle,
            "dbl-circ" to SceneShapeKind.DoubleCircle,
            "diam" to SceneShapeKind.Diamond,
            "diamond" to SceneShapeKind.Diamond,
            "hex" to SceneShapeKind.Hexagon,
            "lean-r" to SceneShapeKind.Parallelogram,
            "lean-l" to SceneShapeKind.ParallelogramAlt,
            "trap-b" to SceneShapeKind.Trapezoid,
            "trap-t" to SceneShapeKind.TrapezoidAlt,
            "odd" to SceneShapeKind.Asymmetric,
            "ellipse" to SceneShapeKind.Ellipse,
            "text" to SceneShapeKind.TextBlock,
            "notch-rect" to SceneShapeKind.NotchedRectangle,
            "notched-rectangle" to SceneShapeKind.NotchedRectangle,
            "card" to SceneShapeKind.NotchedRectangle,
            "lin-rect" to SceneShapeKind.LinedRectangle,
            "lined-rectangle" to SceneShapeKind.LinedRectangle,
            "lined-process" to SceneShapeKind.LinedRectangle,
            "sm-circ" to SceneShapeKind.SmallCircle,
            "small-circle" to SceneShapeKind.SmallCircle,
            "start" to SceneShapeKind.SmallCircle,
            "fr-circ" to SceneShapeKind.FramedCircle,
            "framed-circle" to SceneShapeKind.FramedCircle,
            "stop" to SceneShapeKind.FramedCircle,
            "fork" to SceneShapeKind.ForkJoin,
            "join" to SceneShapeKind.ForkJoin,
            "bow-rect" to SceneShapeKind.Hourglass,
            "hourglass" to SceneShapeKind.Hourglass,
            "collate" to SceneShapeKind.Hourglass,
            "brace-l" to SceneShapeKind.BraceLeft,
            "l-brace" to SceneShapeKind.BraceLeft,
            "brace-r" to SceneShapeKind.BraceRight,
            "r-brace" to SceneShapeKind.BraceRight,
            "braces" to SceneShapeKind.Braces,
            "bolt" to SceneShapeKind.Bolt,
            "lightning-bolt" to SceneShapeKind.Bolt,
            "doc" to SceneShapeKind.Document,
            "document" to SceneShapeKind.Document,
            "delay" to SceneShapeKind.Delay,
            "half-rounded-rectangle" to SceneShapeKind.Delay,
            "h-cyl" to SceneShapeKind.DirectAccessStorage,
            "das" to SceneShapeKind.DirectAccessStorage,
            "horizontal-cylinder" to SceneShapeKind.DirectAccessStorage,
            "lin-cyl" to SceneShapeKind.LinedCylinder,
            "lined-cylinder" to SceneShapeKind.LinedCylinder,
            "disk" to SceneShapeKind.LinedCylinder,
            "curv-trap" to SceneShapeKind.CurvedTrapezoid,
            "curved-trapezoid" to SceneShapeKind.CurvedTrapezoid,
            "display" to SceneShapeKind.CurvedTrapezoid,
            "div-rect" to SceneShapeKind.DividedRectangle,
            "divided-rectangle" to SceneShapeKind.DividedRectangle,
            "div-proc" to SceneShapeKind.DividedRectangle,
            "tri" to SceneShapeKind.Triangle,
            "triangle" to SceneShapeKind.Triangle,
            "extract" to SceneShapeKind.Triangle,
            "win-pane" to SceneShapeKind.WindowPane,
            "window-pane" to SceneShapeKind.WindowPane,
            "internal-storage" to SceneShapeKind.WindowPane,
            "f-circ" to SceneShapeKind.FilledCircle,
            "filled-circle" to SceneShapeKind.FilledCircle,
            "junction" to SceneShapeKind.FilledCircle,
            "lin-doc" to SceneShapeKind.LinedDocument,
            "lined-document" to SceneShapeKind.LinedDocument,
            "notch-pent" to SceneShapeKind.NotchedPentagon,
            "notched-pentagon" to SceneShapeKind.NotchedPentagon,
            "loop-limit" to SceneShapeKind.NotchedPentagon,
            "flip-tri" to SceneShapeKind.FlippedTriangle,
            "flipped-triangle" to SceneShapeKind.FlippedTriangle,
            "manual-file" to SceneShapeKind.FlippedTriangle,
            "sl-rect" to SceneShapeKind.SlopedRectangle,
            "sloped-rectangle" to SceneShapeKind.SlopedRectangle,
            "manual-input" to SceneShapeKind.SlopedRectangle,
            "docs" to SceneShapeKind.MultiDocument,
            "documents" to SceneShapeKind.MultiDocument,
            "st-doc" to SceneShapeKind.MultiDocument,
            "procs" to SceneShapeKind.MultiProcess,
            "processes" to SceneShapeKind.MultiProcess,
            "st-rect" to SceneShapeKind.MultiProcess,
            "flag" to SceneShapeKind.PaperTape,
            "paper-tape" to SceneShapeKind.PaperTape,
            "bow-tie-rect" to SceneShapeKind.BowTieRectangle,
            "stored-data" to SceneShapeKind.BowTieRectangle,
            "cross-circ" to SceneShapeKind.CrossedCircle,
            "crossed-circle" to SceneShapeKind.CrossedCircle,
            "summary" to SceneShapeKind.CrossedCircle,
            "tag-doc" to SceneShapeKind.TaggedDocument,
            "tagged-document" to SceneShapeKind.TaggedDocument,
            "tag-rect" to SceneShapeKind.TaggedRectangle,
            "tagged-rectangle" to SceneShapeKind.TaggedRectangle,
            "icon" to SceneShapeKind.Icon,
            "image" to SceneShapeKind.Image,
        )

    }
}
