package com.swithun.cmpmermaid.core.treemap.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidTreemapOptions

/**
 * Kotlin translation of Mermaid.js 12.0.0:
 * packages/parser/src/language/treemap/treemap.langium,
 * valueConverter.ts, and packages/mermaid/src/diagrams/treemap/parser.ts -> populate.
 */
internal class TreemapParser(
    private val config: MermaidTreemapOptions,
    private val diagramTitle: String?,
    private val lineOffset: Int,
) {
    fun parse(source: String): GMResult<TreemapDb, MermaidError> {
        val lines = source
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .lines()
        val headerIndex = lines.indexOfFirst { line -> stripComment(line).isNotBlank() }
        if (headerIndex < 0) {
            return parseError(1, 1, "Expected 'treemap' or 'treemap-beta' diagram header")
        }
        val header = stripComment(lines[headerIndex]).trim()
        if (header != TREEMAP && header != TREEMAP_BETA) {
            return parseError(
                headerIndex + 1,
                1,
                "Expected 'treemap' or 'treemap-beta' diagram header",
            )
        }
        // Mermaid.js 12.0.0:
        // packages/parser/src/language/treemap/treemap.langium -> INDENTATION, TreemapRow.
        // A trailing horizontal-whitespace token starts a row and must be followed by an item.
        val trailingContentIndex = lines.indexOfLast(String::isNotEmpty)
        if (
            trailingContentIndex > headerIndex &&
            lines[trailingContentIndex].all(::isHorizontalWhitespace)
        ) {
            return parseError(
                trailingContentIndex + 1,
                1,
                "Expected a quoted Treemap item or classDef after indentation",
            )
        }

        val rows = mutableListOf<TreemapRow>()
        var title: String? = null
        var accessibilityTitle: String? = null
        var accessibilityDescription: String? = null
        var lineIndex = headerIndex + 1
        while (lineIndex < lines.size) {
            val sourceLine = lines[lineIndex]
            val content = stripComment(sourceLine)
            if (content.isBlank()) {
                lineIndex += 1
                continue
            }
            val indentation = content.takeWhile(::isHorizontalWhitespace).length
            val statement = content.drop(indentation).trimEnd()
            when {
                statement.hasKeyword(TITLE_KEYWORD) -> {
                    title = normalizeInline(statement.drop(TITLE_KEYWORD.length))
                }
                statement.hasKeyword(ACCESSIBILITY_TITLE_KEYWORD) -> {
                    val separator = statement.indexOf(':')
                    if (separator < 0) {
                        return parseError(
                            lineIndex + 1,
                            indentation + ACCESSIBILITY_TITLE_KEYWORD.length + 1,
                            "Expected ':' after accTitle",
                        )
                    }
                    accessibilityTitle = normalizeInline(statement.substring(separator + 1))
                }
                statement.hasKeyword(ACCESSIBILITY_DESCRIPTION_KEYWORD) -> {
                    val afterKeyword = statement
                        .drop(ACCESSIBILITY_DESCRIPTION_KEYWORD.length)
                        .trimStart()
                    when {
                        afterKeyword.startsWith(':') -> {
                            accessibilityDescription =
                                normalizeInline(afterKeyword.drop(1))
                        }
                        afterKeyword.startsWith('{') -> {
                            val parsed = when (
                                val block = parseAccessibilityDescriptionBlock(
                                    lines = lines,
                                    lineIndex = lineIndex,
                                    initial = afterKeyword.drop(1),
                                )
                            ) {
                                is GMResult.Ok -> block.value
                                is GMResult.Err -> return block
                            }
                            accessibilityDescription = parsed.value
                            lineIndex = parsed.lastLineIndex
                        }
                        else -> return parseError(
                            lineIndex + 1,
                            indentation + ACCESSIBILITY_DESCRIPTION_KEYWORD.length + 1,
                            "Expected ':' or '{' after accDescr",
                        )
                    }
                }
                statement.startsWith(CLASS_DEF_KEYWORD) ||
                    statement.firstOrNull() == '"' ||
                    statement.firstOrNull() == '\'' -> {
                    val parsedRows = when (
                        val parsed = parseRows(
                            statement = statement,
                            initialIndentation = indentation,
                            line = lineIndex + 1,
                            column = indentation + 1,
                        )
                    ) {
                        is GMResult.Ok -> parsed.value
                        is GMResult.Err -> return parsed
                    }
                    rows += parsedRows
                }
                else -> return parseError(
                    lineIndex + 1,
                    indentation + 1,
                    "Expected a quoted Treemap item or classDef",
                )
            }
            lineIndex += 1
        }

        val db = TreemapDb(config = config, diagramTitle = diagramTitle)
        rows.filterIsInstance<TreemapRow.ClassDefinition>().forEach { row ->
            db.addClass(row.id, row.style)
        }
        val flatItems = rows.filterIsInstance<TreemapRow.Item>().map { row ->
            val styles = row.item.classSelector
                ?.let(db::getStylesForClass)
                ?.takeIf(List<String>::isNotEmpty)
            TreemapFlatItem(
                level = row.indentation,
                name = row.item.name,
                type = row.item.type,
                value = row.item.value,
                classSelector = row.item.classSelector,
                cssCompiledStyles = styles,
            )
        }
        addNodesRecursively(db, buildTreemapHierarchy(flatItems), level = 0)
        title?.takeIf(String::isNotEmpty)?.let(db::setDiagramTitle)
        accessibilityTitle?.takeIf(String::isNotEmpty)?.let(db::setAccessibilityTitle)
        accessibilityDescription
            ?.takeIf(String::isNotEmpty)
            ?.let(db::setAccessibilityDescription)
        return GMResult.Ok(db)
    }

    private fun parseRows(
        statement: String,
        initialIndentation: Int,
        line: Int,
        column: Int,
    ): GMResult<List<TreemapRow>, MermaidError> {
        val rows = mutableListOf<TreemapRow>()
        var cursor = 0
        var indentation = initialIndentation
        while (cursor < statement.length) {
            when {
                statement[cursor] == '"' || statement[cursor] == '\'' -> {
                    val parsed = when (
                        val result = parseItemPrefix(statement, cursor, line, column)
                    ) {
                        is GMResult.Ok -> result.value
                        is GMResult.Err -> return result
                    }
                    rows += TreemapRow.Item(indentation = indentation, item = parsed.item)
                    cursor = parsed.nextItemIndex ?: break
                    indentation = parsed.nextItemIndentation
                }
                statement.startsWith(CLASS_DEF_KEYWORD, startIndex = cursor) -> {
                    val parsed = when (
                        val result = parseClassDefinitionPrefix(
                            statement,
                            cursor,
                            line,
                            column,
                        )
                    ) {
                        is GMResult.Ok -> result.value
                        is GMResult.Err -> return result
                    }
                    rows += parsed.row
                    val nextIndex = statement.skipHorizontalWhitespace(parsed.endIndex)
                    if (nextIndex == statement.length) {
                        break
                    }
                    indentation = nextIndex - parsed.endIndex
                    cursor = nextIndex
                }
                else -> return parseError(
                    line,
                    column + cursor,
                    "Expected a quoted Treemap item or classDef",
                )
            }
        }
        return GMResult.Ok(rows)
    }

    private fun parseItemPrefix(
        statement: String,
        startIndex: Int,
        line: Int,
        column: Int,
    ): GMResult<ParsedItemPrefix, MermaidError> {
        val quote = statement.getOrNull(startIndex)
            ?: return parseError(line, column + startIndex, "Expected a quoted Treemap item")
        if (quote != '"' && quote != '\'') {
            return parseError(line, column + startIndex, "Expected a quoted Treemap item")
        }
        val closing = statement.indexOf(quote, startIndex = startIndex + 1)
        if (closing < 0) {
            return parseError(
                line,
                column + startIndex,
                "Unterminated quoted Treemap item",
            )
        }
        val name = statement.substring(startIndex + 1, closing)
        var cursor = closing + 1
        cursor = statement.skipHorizontalWhitespace(cursor)
        if (statement.startsWith(STYLE_SEPARATOR, startIndex = cursor)) {
            val parsedClass = when (
                val result = parseClassSelectorPrefix(statement, cursor, line, column)
            ) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
            return finishItemPrefix(
                statement = statement,
                cursor = parsedClass.endIndex,
                line = line,
                column = column,
                item = ParsedItem(
                    name = name,
                    type = TreemapItemType.Section,
                    classSelector = parsedClass.value,
                ),
            )
        }
        if (cursor == statement.length) {
            return GMResult.Ok(
                ParsedItemPrefix(
                    item = ParsedItem(
                        name = name,
                        type = TreemapItemType.Section,
                    ),
                ),
            )
        }
        if (statement[cursor] == '"' || statement[cursor] == '\'') {
            return GMResult.Ok(
                ParsedItemPrefix(
                    item = ParsedItem(name = name, type = TreemapItemType.Section),
                    nextItemIndex = cursor,
                    nextItemIndentation = cursor - closing - 1,
                ),
            )
        }
        if (statement[cursor] != ':' && statement[cursor] != ',') {
            return parseError(
                line,
                column + cursor,
                "Expected ':', ',', or ':::' after Treemap item name",
            )
        }
        cursor += 1
        cursor = statement.skipHorizontalWhitespace(cursor)
        val numberMatch = NUMBER.find(statement, startIndex = cursor)
            ?.takeIf { match -> match.range.first == cursor }
            ?: return parseError(line, column + cursor, "Expected a Treemap leaf value")
        val numberSource = numberMatch.value
        cursor = numberMatch.range.last + 1
        val valueEnd = cursor
        val selectorStart = statement.skipHorizontalWhitespace(cursor)
        val classSelector = if (
            statement.startsWith(STYLE_SEPARATOR, startIndex = selectorStart)
        ) {
            when (
                val parsed = parseClassSelectorPrefix(
                    statement,
                    selectorStart,
                    line,
                    column,
                )
            ) {
                is GMResult.Ok -> {
                    cursor = parsed.value.endIndex
                    parsed.value.value
                }
                is GMResult.Err -> return parsed
            }
        } else {
            cursor = valueEnd
            null
        }
        return finishItemPrefix(
            statement = statement,
            cursor = cursor,
            line = line,
            column = column,
            item = ParsedItem(
                name = name,
                type = TreemapItemType.Leaf,
                value = javascriptParseFloat(numberSource.replace(",", "")),
                classSelector = classSelector,
            ),
        )
    }

    private fun finishItemPrefix(
        statement: String,
        cursor: Int,
        line: Int,
        column: Int,
        item: ParsedItem,
    ): GMResult<ParsedItemPrefix, MermaidError> {
        val nextItemIndex = statement.skipHorizontalWhitespace(cursor)
        if (nextItemIndex == statement.length) {
            return GMResult.Ok(ParsedItemPrefix(item = item))
        }
        if (
            statement[nextItemIndex] != '"' &&
            statement[nextItemIndex] != '\'' &&
            !statement.startsWith(CLASS_DEF_KEYWORD, startIndex = nextItemIndex)
        ) {
            return parseError(
                line,
                column + nextItemIndex,
                "Unexpected text after Treemap item",
            )
        }
        return GMResult.Ok(
            ParsedItemPrefix(
                item = item,
                nextItemIndex = nextItemIndex,
                nextItemIndentation = nextItemIndex - cursor,
            ),
        )
    }

    private fun parseClassDefinitionPrefix(
        statement: String,
        startIndex: Int,
        line: Int,
        column: Int,
    ): GMResult<ParsedClassDefinitionPrefix, MermaidError> {
        val match = CLASS_DEF.find(statement, startIndex)
            ?.takeIf { result -> result.range.first == startIndex }
            ?: return parseError(
                line,
                column + startIndex,
                "Invalid Treemap classDef statement",
            )
        return GMResult.Ok(
            ParsedClassDefinitionPrefix(
                row = TreemapRow.ClassDefinition(
                    id = match.groupValues[1],
                    style = match.groupValues.getOrElse(2) { "" },
                ),
                endIndex = match.range.last + 1,
            ),
        )
    }

    private fun parseClassSelectorPrefix(
        statement: String,
        separatorIndex: Int,
        line: Int,
        column: Int,
    ): GMResult<ParsedClassSelector, MermaidError> {
        val selectorStart = separatorIndex + STYLE_SEPARATOR.length
        val match = IDENTIFIER.find(statement, startIndex = selectorStart)
            ?.takeIf { result -> result.range.first == selectorStart }
            ?: return parseError(
                line,
                column + selectorStart,
                "Expected a Treemap class selector",
            )
        val end = match.range.last + 1
        return GMResult.Ok(ParsedClassSelector(value = match.value, endIndex = end))
    }

    private fun parseAccessibilityDescriptionBlock(
        lines: List<String>,
        lineIndex: Int,
        initial: String,
    ): GMResult<ParsedAccessibilityDescription, MermaidError> {
        val body = StringBuilder()
        var currentLine = lineIndex
        var current = initial
        while (true) {
            val close = current.indexOf('}')
            if (close >= 0) {
                body.append(current.substring(0, close))
                if (stripComment(current.substring(close + 1)).isNotBlank()) {
                    return parseError(
                        currentLine + 1,
                        close + 2,
                        "Unexpected content after accDescr block",
                    )
                }
                return GMResult.Ok(
                    ParsedAccessibilityDescription(
                        value = normalizeMultiline(body.toString()),
                        lastLineIndex = currentLine,
                    ),
                )
            }
            body.append(current).append('\n')
            currentLine += 1
            if (currentLine >= lines.size) {
                return parseError(
                    lineIndex + 1,
                    1,
                    "Unterminated accDescr block",
                )
            }
            current = lines[currentLine]
        }
    }

    private fun addNodesRecursively(
        db: TreemapDb,
        nodes: List<TreemapNode>,
        level: Int,
    ) {
        nodes.forEach { node ->
            db.addNode(node, level)
            node.children?.takeIf(List<TreemapNode>::isNotEmpty)?.let { children ->
                addNodesRecursively(db, children, level + 1)
            }
        }
    }

    private fun stripComment(line: String): String {
        var quote: Char? = null
        var index = 0
        while (index < line.lastIndex) {
            val character = line[index]
            when {
                character == quote -> quote = null
                quote == null && (character == '"' || character == '\'') -> quote = character
                quote == null && character == '%' && line[index + 1] == '%' ->
                    return line.substring(0, index)
            }
            index += 1
        }
        return line
    }

    private fun javascriptParseFloat(source: String): Double {
        val token = JAVASCRIPT_FLOAT.find(source)?.value ?: return Double.NaN
        return token.toDoubleOrNull() ?: Double.NaN
    }

    private fun String.hasKeyword(keyword: String): Boolean {
        if (!startsWith(keyword)) {
            return false
        }
        val next = getOrNull(keyword.length)
        return next == null || isHorizontalWhitespace(next) || next == ':' || next == '{'
    }

    private fun String.skipHorizontalWhitespace(start: Int): Int {
        var index = start
        while (getOrNull(index)?.let(::isHorizontalWhitespace) == true) {
            index += 1
        }
        return index
    }

    private fun normalizeInline(value: String): String =
        value.trim().replace(HORIZONTAL_WHITESPACE_RUN, " ")

    private fun normalizeMultiline(value: String): String =
        value
            .lineSequence()
            .map { line -> normalizeInline(line) }
            .dropWhile(String::isEmpty)
            .toList()
            .dropLastWhile(String::isEmpty)
            .joinToString("\n")
            .replace(MULTIPLE_NEWLINES, "\n")

    private fun <T> parseError(
        line: Int,
        column: Int,
        message: String,
    ): GMResult<T, MermaidError> = GMResult.Err(
        MermaidError.Parse(
            line = line + lineOffset,
            column = column,
            message = "Parse error on line ${line + lineOffset}, column $column: $message",
        ),
    )

    private sealed interface TreemapRow {
        data class Item(
            val indentation: Int,
            val item: ParsedItem,
        ) : TreemapRow

        data class ClassDefinition(
            val id: String,
            val style: String,
        ) : TreemapRow
    }

    private data class ParsedItem(
        val name: String,
        val type: TreemapItemType,
        val value: Double? = null,
        val classSelector: String? = null,
    )

    private data class ParsedItemPrefix(
        val item: ParsedItem,
        val nextItemIndex: Int? = null,
        val nextItemIndentation: Int = 0,
    )

    private data class ParsedClassSelector(
        val value: String,
        val endIndex: Int,
    )

    private data class ParsedClassDefinitionPrefix(
        val row: TreemapRow.ClassDefinition,
        val endIndex: Int,
    )

    private data class ParsedAccessibilityDescription(
        val value: String,
        val lastLineIndex: Int,
    )

    private companion object {
        const val TREEMAP = "treemap"
        const val TREEMAP_BETA = "treemap-beta"
        const val TITLE_KEYWORD = "title"
        const val ACCESSIBILITY_TITLE_KEYWORD = "accTitle"
        const val ACCESSIBILITY_DESCRIPTION_KEYWORD = "accDescr"
        const val CLASS_DEF_KEYWORD = "classDef"
        const val STYLE_SEPARATOR = ":::"
        val CLASS_DEF = Regex(
            """classDef\s+([A-Z_a-z]\w+)(?:\s+([^\n\r;]*))?;?""",
        )
        val IDENTIFIER = Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")
        val NUMBER = Regex("""[0-9_.,]+""")
        val JAVASCRIPT_FLOAT = Regex("""^(?:\d+\.?\d*|\.\d+)""")
        val HORIZONTAL_WHITESPACE_RUN = Regex("""[\t ]{2,}""")
        val MULTIPLE_NEWLINES = Regex("""[\n\r]{2,}""")

        fun isHorizontalWhitespace(character: Char): Boolean =
            character == ' ' || character == '\t'
    }
}
