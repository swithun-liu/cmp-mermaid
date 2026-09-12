package io.github.cmpmermaid.core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar

/**
 * Kotlin port of Mermaid 12.0.0 preprocess.ts, frontmatter.ts, comments.ts,
 * detectInit(), and the encodeEntities/decodeEntities parser boundary.
 */
internal object MermaidPreprocessor {
    fun preprocess(source: String): GMResult<MermaidPreprocessResult, MermaidError> {
        val normalized = normalizeText(source)
        val frontmatter = when (val parsed = extractFrontmatter(normalized)) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        val directives = when (val parsed = processDirectives(frontmatter.text)) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        val config = frontmatter.config.merge(directives.config)
        return GMResult.Ok(
            MermaidPreprocessResult(
                code = MermaidDiagramCode(
                    raw = source,
                    cleaned = cleanupComments(directives.text),
                    withComments = directives.text,
                    frontmatterLineOffset = frontmatter.lineOffset,
                ),
                title = frontmatter.title,
                config = config,
            ),
        )
    }

    fun encodeEntities(source: String): String {
        val withoutStyleTerminators = STYLE_WITH_ENTITY.replace(source) { match ->
            match.value.dropLast(1)
        }
        val withoutClassTerminators = CLASS_WITH_ENTITY.replace(withoutStyleTerminators) { match ->
            match.value.dropLast(1)
        }
        return ENTITY.replace(withoutClassTerminators) { match ->
            val body = match.value.substring(1, match.value.lastIndex)
            if (SIGNED_INTEGER.matches(body)) {
                "ﬂ°°$body¶ß"
            } else {
                "ﬂ°$body¶ß"
            }
        }
    }

    fun decodeEntities(source: String): String = source
        .replace("ﬂ°°", "&#")
        .replace("ﬂ°", "&")
        .replace("¶ß", ";")

    private fun normalizeText(source: String): String {
        val normalizedLines = source.replace(CARRIAGE_RETURN, "\n")
        return HTML_TAG.replace(normalizedLines) { match ->
            val tag = match.groupValues[1]
            val attributes = match.groupValues[2].replace(DOUBLE_QUOTED_ATTRIBUTE) {
                "='${it.groupValues[1]}'"
            }
            "<$tag$attributes>"
        }
    }

    private fun extractFrontmatter(
        source: String,
    ): GMResult<FrontmatterResult, MermaidError> {
        val match = FRONT_MATTER.find(source)
            ?: return GMResult.Ok(FrontmatterResult(text = source))
        val indent = match.groupValues[1]
        val body = if (indent.isEmpty()) {
            match.groupValues[2]
        } else {
            match.groupValues[2]
                .lineSequence()
                .joinToString("\n") { line ->
                    if (line.startsWith(indent)) line.drop(indent.length) else line
                }
        }
        val root = when (val parsed = parseYamlMap(body, "frontmatter")) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        val config = when (
            val parsed = parseConfig(
                map = root.map("config"),
                sourceName = "frontmatter.config",
                stripSecureKeys = true,
            )
        ) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        return GMResult.Ok(
            FrontmatterResult(
                text = source.drop(match.value.length),
                title = root.scalar("title"),
                config = config,
                lineOffset = match.value.count { it == '\n' },
            ),
        )
    }

    private fun processDirectives(
        source: String,
    ): GMResult<DirectiveResult, MermaidError> {
        var config = MermaidConfigOverride()
        val directives = findDirectives(source)
        directives.forEach { directive ->
            val type = directive.type.lowercase()
            when (type) {
                "init", "initialize" -> {
                    val raw = directive.value.trim()
                    if (raw.isEmpty()) {
                        return@forEach
                    }
                    val map = when (val parsed = parseYamlMap(raw, "%%{$type}%% directive")) {
                        is GMResult.Ok -> parsed.value
                        is GMResult.Err -> return parsed
                    }
                    val parsedConfig = when (
                        val parsed = parseConfig(
                            map = map,
                            sourceName = "%%{$type}%% directive",
                            stripSecureKeys = true,
                        )
                    ) {
                        is GMResult.Ok -> parsed.value
                        is GMResult.Err -> return parsed
                    }
                    config = config.merge(parsedConfig)
                }
                "wrap" -> Unit
            }
        }
        return GMResult.Ok(
            DirectiveResult(
                text = removeDirectives(source, directives),
                config = config,
            ),
        )
    }

    private fun findDirectives(source: String): List<DirectiveToken> {
        val directives = mutableListOf<DirectiveToken>()
        var cursor = 0
        while (cursor < source.length) {
            val start = source.indexOf(DIRECTIVE_OPEN, cursor)
            if (start < 0) {
                break
            }
            val endMarker = source.indexOf(DIRECTIVE_CLOSE, start + DIRECTIVE_OPEN.length)
            if (endMarker < 0) {
                break
            }
            val end = endMarker + DIRECTIVE_CLOSE.length
            val body = source.substring(start + DIRECTIVE_OPEN.length, endMarker).trim()
            val separator = body.indexOf(':')
            val type = if (separator >= 0) {
                body.substring(0, separator).trim()
            } else {
                body.substringBefore(' ').trim()
            }
            val value = if (separator >= 0) {
                body.substring(separator + 1)
            } else {
                body.removePrefix(type).trim()
            }
            if (type.isNotEmpty()) {
                directives += DirectiveToken(start, end, type, value)
            }
            cursor = end
        }
        return directives
    }

    private fun removeDirectives(
        source: String,
        directives: List<DirectiveToken>,
    ): String {
        if (directives.isEmpty()) {
            return source
        }
        return buildString(source.length) {
            var cursor = 0
            directives.forEach { directive ->
                append(source, cursor, directive.start)
                cursor = directive.endExclusive
            }
            append(source, cursor, source.length)
        }
    }

    private fun parseYamlMap(
        source: String,
        sourceName: String,
    ): GMResult<YamlMap, MermaidError> {
        val parsed = try {
            Yaml.default.parseToYamlNode(source)
        } catch (failure: Throwable) {
            return configurationError(
                "Invalid Mermaid $sourceName: ${failure.message ?: "invalid YAML/JSON"}",
            )
        }
        return when (parsed) {
            is YamlMap -> GMResult.Ok(parsed)
            else -> configurationError("Mermaid $sourceName must be a map")
        }
    }

    private fun parseConfig(
        map: YamlMap?,
        sourceName: String,
        stripSecureKeys: Boolean = false,
    ): GMResult<MermaidConfigOverride, MermaidError> {
        if (map == null) {
            return GMResult.Ok(MermaidConfigOverride())
        }
        val flowchart = map.map("flowchart") ?: map.map("config")
        val classDiagram = map.map("class")
        val elk = map.map("elk")
        val unsupported = buildSet {
            TOP_LEVEL_UNTRANSLATED_KEYS.filterTo(this) { map.node(it) != null }
            FLOWCHART_UNTRANSLATED_KEYS.filterTo(this) { flowchart?.node(it) != null }
        }
        if (unsupported.isNotEmpty()) {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = unsupported.sorted().joinToString(),
                    message = "Native Mermaid has not translated configuration: " +
                        unsupported.sorted().joinToString(),
                ),
            )
        }

        var readError: MermaidError? = null

        fun float(
            owner: YamlMap?,
            key: String,
            path: String,
        ): Float? {
            val scalar = owner?.node(key) ?: return null
            return (scalar as? YamlScalar)?.content?.toFloatOrNull()
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be a number",
                    )
                    null
                }
        }

        fun int(
            owner: YamlMap?,
            key: String,
            path: String,
        ): Int? {
            val scalar = owner?.node(key) ?: return null
            return (scalar as? YamlScalar)?.content?.toIntOrNull()
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be an integer",
                    )
                    null
                }
        }

        fun boolean(
            owner: YamlMap?,
            key: String,
            path: String,
        ): Boolean? {
            val scalar = owner?.node(key) ?: return null
            return (scalar as? YamlScalar)
                ?.content
                ?.lowercase()
                ?.let { raw ->
                    when (raw) {
                        "true" -> true
                        "false" -> false
                        else -> null
                    }
                }
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be a boolean",
                    )
                    null
                }
        }

        fun string(
            owner: YamlMap?,
            key: String,
            path: String,
        ): String? {
            val node = owner?.node(key) ?: return null
            return (node as? YamlScalar)?.content
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be a string",
                    )
                    null
                }
        }

        fun appearanceString(
            owner: YamlMap?,
            key: String,
            path: String,
        ): String? {
            return when (val node = owner?.node(key)) {
                null, is YamlNull -> null
                is YamlScalar -> node.content
                else -> {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be a string",
                    )
                    null
                }
            }
        }

        fun enumString(
            owner: YamlMap?,
            key: String,
            path: String,
            allowed: Set<String>,
        ): String? {
            val value = string(owner, key, path) ?: return null
            if (value !in allowed) {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName '$path' must be one of ${allowed.joinToString()}",
                )
                return null
            }
            return value
        }

        fun lineHops(): MermaidElkLineHops? {
            val scalar = elk?.node("lineHops") ?: return null
            val value = (scalar as? YamlScalar)?.content?.lowercase()
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName 'elk.lineHops' must be a boolean, 'arc', or 'gap'",
                    )
                    return null
                }
            return when (value) {
                "true", "arc" -> MermaidElkLineHops.Arc
                "false" -> MermaidElkLineHops.Disabled
                "gap" -> MermaidElkLineHops.Gap
                else -> {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName 'elk.lineHops' must be a boolean, 'arc', or 'gap'",
                    )
                    null
                }
            }
        }

        fun themeVariables(
            owner: YamlMap?,
            key: String,
            path: String,
        ): ParsedThemeVariables? {
            val node = owner?.node(key) ?: return null
            val yamlMap = node as? YamlMap
            if (yamlMap == null) {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName '$path' must be a map",
                )
                return null
            }
            val scalars = linkedMapOf<String, String>()
            val arrays = linkedMapOf<String, List<String>>()
            yamlMap.entries.forEach { (entryKey, entryValue) ->
                when (entryValue) {
                    is YamlScalar -> scalars[entryKey.content] = entryValue.content
                    is YamlList -> {
                        val items = entryValue.items.map { item -> (item as? YamlScalar)?.content }
                        if (items.any { item -> item == null }) {
                            readError = MermaidError.Configuration(
                                "Mermaid $sourceName '$path.${entryKey.content}' " +
                                    "must contain only scalar values",
                            )
                            return null
                        }
                        arrays[entryKey.content] = items.filterNotNull()
                    }
                    else -> {
                        readError = MermaidError.Configuration(
                            "Mermaid $sourceName '$path.${entryKey.content}' " +
                                "must be a scalar or scalar array",
                        )
                        return null
                    }
                }
            }
            return ParsedThemeVariables(scalars = scalars, arrays = arrays)
        }

        val nodeSpacing = float(flowchart, "nodeSpacing", "flowchart.nodeSpacing")
        val rankSpacing = float(flowchart, "rankSpacing", "flowchart.rankSpacing")
        val diagramPadding = float(flowchart, "diagramPadding", "flowchart.diagramPadding")
        val wrappingWidth = float(flowchart, "wrappingWidth", "flowchart.wrappingWidth")
        val minNodeWidth = float(flowchart, "minNodeWidth", "flowchart.minNodeWidth")
        val padding = float(flowchart, "padding", "flowchart.padding")
        val classPadding = float(classDiagram, "padding", "class.padding")
        val classHideEmptyMembersBox = boolean(
            classDiagram,
            "hideEmptyMembersBox",
            "class.hideEmptyMembersBox",
        )
        val classHierarchicalNamespaces = boolean(
            classDiagram,
            "hierarchicalNamespaces",
            "class.hierarchicalNamespaces",
        )
        val curve = string(flowchart, "curve", "flowchart.curve")
        val flowTheme = appearanceString(flowchart, "theme", "flowchart.theme")
            ?.takeIf(USABLE_THEMES::contains)
        val topTheme = appearanceString(map, "theme", "theme")
            ?.takeIf(USABLE_THEMES::contains)
        val parsedThemeVariables = themeVariables(map, "themeVariables", "themeVariables")
        val flowLook = appearanceString(flowchart, "look", "flowchart.look")
            ?.takeIf(USABLE_LOOKS::contains)
        val topLook = appearanceString(map, "look", "look")
            ?.takeIf(USABLE_LOOKS::contains)
        val titleTopMargin = float(flowchart, "titleTopMargin", "flowchart.titleTopMargin")
        val subGraphTitleMarginNode = flowchart?.node("subGraphTitleMargin")
        val subGraphTitleMargin = when (subGraphTitleMarginNode) {
            null -> null
            is YamlMap -> subGraphTitleMarginNode
            else -> {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName 'flowchart.subGraphTitleMargin' must be a map",
                )
                null
            }
        }
        val subGraphTitleTopMargin = int(
            subGraphTitleMargin,
            "top",
            "flowchart.subGraphTitleMargin.top",
        )
        val subGraphTitleBottomMargin = int(
            subGraphTitleMargin,
            "bottom",
            "flowchart.subGraphTitleMargin.bottom",
        )
        if (subGraphTitleTopMargin != null && subGraphTitleTopMargin < 0) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'flowchart.subGraphTitleMargin.top' must be non-negative",
            )
        }
        if (subGraphTitleBottomMargin != null && subGraphTitleBottomMargin < 0) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'flowchart.subGraphTitleMargin.bottom' must be non-negative",
            )
        }
        val inheritDirection = boolean(flowchart, "inheritDir", "flowchart.inheritDir")
        val flowHtmlLabels = boolean(flowchart, "htmlLabels", "flowchart.htmlLabels")
        val topHtmlLabels = boolean(map, "htmlLabels", "htmlLabels")
        val markdownAutoWrap = boolean(map, "markdownAutoWrap", "markdownAutoWrap")
        val fontSize = float(map, "fontSize", "fontSize")
        val fontFamily = string(map, "fontFamily", "fontFamily")
        val topLayout = string(map, "layout", "layout")
        val layout = string(flowchart, "layout", "flowchart.layout") ?: topLayout
        val classLayout = string(classDiagram, "layout", "class.layout")
        val elkMergeEdges = boolean(elk, "mergeEdges", "elk.mergeEdges")
        val elkNodePlacementStrategy = enumString(
            elk,
            "nodePlacementStrategy",
            "elk.nodePlacementStrategy",
            ELK_NODE_PLACEMENT_STRATEGIES,
        )
        val elkNodePlacementAlignment = enumString(
            elk,
            "nodePlacementAlignment",
            "elk.nodePlacementAlignment",
            ELK_NODE_PLACEMENT_ALIGNMENTS,
        )
        val elkPreset = enumString(elk, "preset", "elk.preset", ELK_PRESETS)
        val elkStraightenEdges = boolean(elk, "straightenEdges", "elk.straightenEdges")
        val elkLineHops = lineHops()
        val elkLayeringStrategy = enumString(
            elk,
            "layeringStrategy",
            "elk.layeringStrategy",
            ELK_LAYERING_STRATEGIES,
        )
        val elkLayeringLayerBound = int(elk, "layeringLayerBound", "elk.layeringLayerBound")
        val elkCycleBreakingStrategy = enumString(
            elk,
            "cycleBreakingStrategy",
            "elk.cycleBreakingStrategy",
            ELK_CYCLE_BREAKING_STRATEGIES,
        )
        val elkForceNodeModelOrder = boolean(
            elk,
            "forceNodeModelOrder",
            "elk.forceNodeModelOrder",
        )
        val elkConsiderModelOrder = enumString(
            elk,
            "considerModelOrder",
            "elk.considerModelOrder",
            ELK_MODEL_ORDER_STRATEGIES,
        )
        val elkKeepEntryNodeOnTop = boolean(
            elk,
            "keepEntryNodeOnTop",
            "elk.keepEntryNodeOnTop",
        )
        val maxEdges = if (stripSecureKeys) {
            null
        } else {
            int(map, "maxEdges", "maxEdges")
        }
        readError?.let { error ->
            return GMResult.Err(error)
        }

        return GMResult.Ok(
            MermaidConfigOverride(
                nodeSpacing = nodeSpacing,
                rankSpacing = rankSpacing,
                diagramPadding = diagramPadding,
                wrappingWidth = wrappingWidth,
                minNodeWidth = minNodeWidth,
                flowchartPadding = padding,
                classPadding = classPadding,
                classHideEmptyMembersBox = classHideEmptyMembersBox,
                classHierarchicalNamespaces = classHierarchicalNamespaces,
                curve = curve,
                fontSize = fontSize,
                fontFamily = fontFamily,
                themeName = flowTheme ?: topTheme,
                themeVariables = parsedThemeVariables?.scalars,
                themeColorArrays = parsedThemeVariables?.arrays,
                look = flowLook ?: topLook,
                titleTopMargin = titleTopMargin,
                subGraphTitleTopMargin = subGraphTitleTopMargin?.toFloat(),
                subGraphTitleBottomMargin = subGraphTitleBottomMargin?.toFloat(),
                inheritDirection = inheritDirection,
                htmlLabels = topHtmlLabels ?: flowHtmlLabels,
                markdownAutoWrap = markdownAutoWrap,
                maxEdges = maxEdges,
                layout = layout,
                classLayout = classLayout,
                elk = elk?.let {
                    MermaidElkConfigOverride(
                        mergeEdges = elkMergeEdges,
                        nodePlacementStrategy = elkNodePlacementStrategy,
                        nodePlacementAlignment = elkNodePlacementAlignment,
                        preset = elkPreset,
                        straightenEdges = elkStraightenEdges,
                        lineHops = elkLineHops,
                        layeringStrategy = elkLayeringStrategy,
                        layeringLayerBound = elkLayeringLayerBound,
                        cycleBreakingStrategy = elkCycleBreakingStrategy,
                        forceNodeModelOrder = elkForceNodeModelOrder,
                        considerModelOrder = elkConsiderModelOrder,
                        keepEntryNodeOnTop = elkKeepEntryNodeOnTop,
                    )
                },
            ),
        )
    }

    private fun cleanupComments(source: String): String =
        source
            .lineSequence()
            .filterNot { line -> line.trimStart().startsWith(COMMENT_PREFIX) }
            .joinToString("\n")
            .trimStart()

    private fun YamlMap.node(key: String): YamlNode? =
        entries.entries.firstOrNull { it.key.content == key }?.value

    private fun YamlMap.scalar(key: String): String? =
        (node(key) as? YamlScalar)?.content

    private fun YamlMap.map(key: String): YamlMap? =
        node(key) as? YamlMap

    private fun <T> configurationError(message: String): GMResult<T, MermaidError> =
        GMResult.Err(MermaidError.Configuration(message))

    private val TOP_LEVEL_UNTRANSLATED_KEYS = setOf(
        "altFontFamily",
        "themeCSS",
    )
    private val FLOWCHART_UNTRANSLATED_KEYS = emptySet<String>()
    private val USABLE_THEMES = setOf(
        "base",
        "dark",
        "default",
        "forest",
        "neo",
        "neo-dark",
        "neutral",
        "null",
        "redux",
        "redux-color",
        "redux-dark",
        "redux-dark-color",
    )
    private val USABLE_LOOKS = setOf("classic", "handDrawn", "neo")
    private val ELK_NODE_PLACEMENT_STRATEGIES = setOf(
        "SIMPLE",
        "NETWORK_SIMPLEX",
        "LINEAR_SEGMENTS",
        "BRANDES_KOEPF",
    )
    private val ELK_NODE_PLACEMENT_ALIGNMENTS = setOf(
        "NONE",
        "LEFTUP",
        "LEFTDOWN",
        "RIGHTUP",
        "RIGHTDOWN",
        "BALANCED",
    )
    private val ELK_PRESETS = setOf("default", "legacy", "modelOrder", "depthFirst")
    private val ELK_LAYERING_STRATEGIES = setOf(
        "NETWORK_SIMPLEX",
        "LONGEST_PATH",
        "LONGEST_PATH_SOURCE",
        "COFFMAN_GRAHAM",
        "MIN_WIDTH",
        "STRETCH_WIDTH",
        "INTERACTIVE",
    )
    private val ELK_CYCLE_BREAKING_STRATEGIES = setOf(
        "GREEDY",
        "DEPTH_FIRST",
        "INTERACTIVE",
        "MODEL_ORDER",
        "GREEDY_MODEL_ORDER",
    )
    private val ELK_MODEL_ORDER_STRATEGIES = setOf(
        "NONE",
        "NODES_AND_EDGES",
        "PREFER_EDGES",
        "PREFER_NODES",
    )
    private val CARRIAGE_RETURN = Regex("""\r\n?""")
    private val HTML_TAG = Regex("""<(\w+)([^>]*)>""")
    private val DOUBLE_QUOTED_ATTRIBUTE = Regex("""="([^"]*)"""")
    private val FRONT_MATTER = Regex(
        pattern = """^([^\S\n\r]*)-{3}\s*[\n\r](.*?)[\n\r]\1-{3}\s*[\n\r]+""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
    private val STYLE_WITH_ENTITY = Regex("""style.*:\S*#.*;""")
    private val CLASS_WITH_ENTITY = Regex("""classDef.*:\S*#.*;""")
    private val ENTITY = Regex("""#\w+;""")
    private val SIGNED_INTEGER = Regex("""^\+?\d+$""")
    private const val DIRECTIVE_OPEN = "%%{"
    private const val DIRECTIVE_CLOSE = "}%%"
    private const val COMMENT_PREFIX = "%%"
}

internal data class MermaidDiagramCode(
    val raw: String,
    val cleaned: String,
    val withComments: String,
    val frontmatterLineOffset: Int,
)

internal data class MermaidPreprocessResult(
    val code: MermaidDiagramCode,
    val title: String? = null,
    val config: MermaidConfigOverride = MermaidConfigOverride(),
)

private data class ParsedThemeVariables(
    val scalars: Map<String, String>,
    val arrays: Map<String, List<String>>,
)

internal data class MermaidConfigOverride(
    val nodeSpacing: Float? = null,
    val rankSpacing: Float? = null,
    val diagramPadding: Float? = null,
    val wrappingWidth: Float? = null,
    val minNodeWidth: Float? = null,
    val flowchartPadding: Float? = null,
    val classPadding: Float? = null,
    val classHideEmptyMembersBox: Boolean? = null,
    val classHierarchicalNamespaces: Boolean? = null,
    val curve: String? = null,
    val fontSize: Float? = null,
    val fontFamily: String? = null,
    val themeName: String? = null,
    val themeVariables: Map<String, String>? = null,
    val themeColorArrays: Map<String, List<String>>? = null,
    val look: String? = null,
    val titleTopMargin: Float? = null,
    val subGraphTitleTopMargin: Float? = null,
    val subGraphTitleBottomMargin: Float? = null,
    val inheritDirection: Boolean? = null,
    val htmlLabels: Boolean? = null,
    val markdownAutoWrap: Boolean? = null,
    val maxEdges: Int? = null,
    val layout: String? = null,
    val classLayout: String? = null,
    val elk: MermaidElkConfigOverride? = null,
) {
    fun merge(overrides: MermaidConfigOverride): MermaidConfigOverride = MermaidConfigOverride(
        nodeSpacing = overrides.nodeSpacing ?: nodeSpacing,
        rankSpacing = overrides.rankSpacing ?: rankSpacing,
        diagramPadding = overrides.diagramPadding ?: diagramPadding,
        wrappingWidth = overrides.wrappingWidth ?: wrappingWidth,
        minNodeWidth = overrides.minNodeWidth ?: minNodeWidth,
        flowchartPadding = overrides.flowchartPadding ?: flowchartPadding,
        classPadding = overrides.classPadding ?: classPadding,
        classHideEmptyMembersBox =
            overrides.classHideEmptyMembersBox ?: classHideEmptyMembersBox,
        classHierarchicalNamespaces =
            overrides.classHierarchicalNamespaces ?: classHierarchicalNamespaces,
        curve = overrides.curve ?: curve,
        fontSize = overrides.fontSize ?: fontSize,
        fontFamily = overrides.fontFamily ?: fontFamily,
        themeName = overrides.themeName ?: themeName,
        themeVariables = when {
            overrides.themeVariables != null ->
                themeVariables.orEmpty() + overrides.themeVariables
            else -> themeVariables
        },
        themeColorArrays = when {
            overrides.themeColorArrays != null ->
                themeColorArrays.orEmpty() + overrides.themeColorArrays
            else -> themeColorArrays
        },
        look = overrides.look ?: look,
        titleTopMargin = overrides.titleTopMargin ?: titleTopMargin,
        subGraphTitleTopMargin =
            overrides.subGraphTitleTopMargin ?: subGraphTitleTopMargin,
        subGraphTitleBottomMargin =
            overrides.subGraphTitleBottomMargin ?: subGraphTitleBottomMargin,
        inheritDirection = overrides.inheritDirection ?: inheritDirection,
        htmlLabels = overrides.htmlLabels ?: htmlLabels,
        markdownAutoWrap = overrides.markdownAutoWrap ?: markdownAutoWrap,
        maxEdges = overrides.maxEdges ?: maxEdges,
        layout = overrides.layout ?: layout,
        classLayout = overrides.classLayout ?: classLayout,
        elk = when {
            overrides.elk != null -> elk?.merge(overrides.elk) ?: overrides.elk
            else -> elk
        },
    )

    fun applyTo(options: MermaidRenderOptions): GMResult<MermaidRenderOptions, MermaidError> {
        val resolvedLook = look ?: options.look
        if (resolvedLook == "handDrawn") {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = "handDrawn look",
                    message = "Native Mermaid has not translated Mermaid's roughjs handDrawn renderer",
                ),
            )
        }
        return GMResult.Ok(
            options.copy(
                layout = layout ?: options.layout,
                elk = elk?.applyTo(options.elk) ?: options.elk,
                nodeSpacing = nodeSpacing ?: options.nodeSpacing,
                rankSpacing = rankSpacing ?: options.rankSpacing,
                diagramPadding = diagramPadding ?: options.diagramPadding,
                wrappingWidth = wrappingWidth ?: options.wrappingWidth,
                minNodeWidth = minNodeWidth ?: options.minNodeWidth,
                flowchartPadding = flowchartPadding ?: options.flowchartPadding,
                classPadding = classPadding ?: options.classPadding,
                classHideEmptyMembersBox =
                    classHideEmptyMembersBox ?: options.classHideEmptyMembersBox,
                classHierarchicalNamespaces =
                    classHierarchicalNamespaces ?: options.classHierarchicalNamespaces,
                curve = curve ?: options.curve,
                fontSize = fontSize ?: options.fontSize,
                fontFamily = fontFamily ?: options.fontFamily,
                themeName = themeName ?: options.themeName,
                themeVariables = options.themeVariables + themeVariables.orEmpty(),
                themeColorArrays = options.themeColorArrays + themeColorArrays.orEmpty(),
                look = resolvedLook,
                titleTopMargin = titleTopMargin ?: options.titleTopMargin,
                subGraphTitleTopMargin =
                    subGraphTitleTopMargin ?: options.subGraphTitleTopMargin,
                subGraphTitleBottomMargin =
                    subGraphTitleBottomMargin ?: options.subGraphTitleBottomMargin,
                inheritDirection = inheritDirection ?: options.inheritDirection,
                htmlLabels = htmlLabels ?: options.htmlLabels,
                markdownAutoWrap = markdownAutoWrap ?: options.markdownAutoWrap,
                maxEdges = maxEdges ?: options.maxEdges,
                classLayout = classLayout ?: options.classLayout,
            ),
        )
    }
}

internal data class MermaidElkConfigOverride(
    val mergeEdges: Boolean? = null,
    val nodePlacementStrategy: String? = null,
    val nodePlacementAlignment: String? = null,
    val preset: String? = null,
    val straightenEdges: Boolean? = null,
    val lineHops: MermaidElkLineHops? = null,
    val layeringStrategy: String? = null,
    val layeringLayerBound: Int? = null,
    val cycleBreakingStrategy: String? = null,
    val forceNodeModelOrder: Boolean? = null,
    val considerModelOrder: String? = null,
    val keepEntryNodeOnTop: Boolean? = null,
) {
    fun merge(overrides: MermaidElkConfigOverride): MermaidElkConfigOverride =
        MermaidElkConfigOverride(
            mergeEdges = overrides.mergeEdges ?: mergeEdges,
            nodePlacementStrategy =
                overrides.nodePlacementStrategy ?: nodePlacementStrategy,
            nodePlacementAlignment =
                overrides.nodePlacementAlignment ?: nodePlacementAlignment,
            preset = overrides.preset ?: preset,
            straightenEdges = overrides.straightenEdges ?: straightenEdges,
            lineHops = overrides.lineHops ?: lineHops,
            layeringStrategy = overrides.layeringStrategy ?: layeringStrategy,
            layeringLayerBound = overrides.layeringLayerBound ?: layeringLayerBound,
            cycleBreakingStrategy =
                overrides.cycleBreakingStrategy ?: cycleBreakingStrategy,
            forceNodeModelOrder =
                overrides.forceNodeModelOrder ?: forceNodeModelOrder,
            considerModelOrder = overrides.considerModelOrder ?: considerModelOrder,
            keepEntryNodeOnTop =
                overrides.keepEntryNodeOnTop ?: keepEntryNodeOnTop,
        )

    fun applyTo(options: MermaidElkOptions): MermaidElkOptions = options.copy(
        mergeEdges = mergeEdges ?: options.mergeEdges,
        nodePlacementStrategy = nodePlacementStrategy ?: options.nodePlacementStrategy,
        nodePlacementAlignment = nodePlacementAlignment ?: options.nodePlacementAlignment,
        preset = preset ?: options.preset,
        straightenEdges = straightenEdges ?: options.straightenEdges,
        lineHops = lineHops ?: options.lineHops,
        layeringStrategy = layeringStrategy ?: options.layeringStrategy,
        layeringLayerBound = layeringLayerBound ?: options.layeringLayerBound,
        cycleBreakingStrategy = cycleBreakingStrategy ?: options.cycleBreakingStrategy,
        forceNodeModelOrder = forceNodeModelOrder ?: options.forceNodeModelOrder,
        considerModelOrder = considerModelOrder ?: options.considerModelOrder,
        keepEntryNodeOnTop = keepEntryNodeOnTop ?: options.keepEntryNodeOnTop,
    )
}

private data class FrontmatterResult(
    val text: String,
    val title: String? = null,
    val config: MermaidConfigOverride = MermaidConfigOverride(),
    val lineOffset: Int = 0,
)

private data class DirectiveToken(
    val start: Int,
    val endExclusive: Int,
    val type: String,
    val value: String,
)

private data class DirectiveResult(
    val text: String,
    val config: MermaidConfigOverride,
)
