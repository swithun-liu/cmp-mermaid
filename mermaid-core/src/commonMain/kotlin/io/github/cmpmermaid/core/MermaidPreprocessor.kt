package io.github.cmpmermaid.core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
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
        DIRECTIVE.findAll(source).forEach { match ->
            val type = (match.groupValues[1].ifEmpty { match.groupValues[2] }).lowercase()
            when (type) {
                "init", "initialize" -> {
                    val raw = match.groupValues[4].ifEmpty { match.groupValues[3] }.trim()
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
                text = DIRECTIVE.replace(source, ""),
                config = config,
            ),
        )
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

        val nodeSpacing = float(flowchart, "nodeSpacing", "flowchart.nodeSpacing")
        val rankSpacing = float(flowchart, "rankSpacing", "flowchart.rankSpacing")
        val diagramPadding = float(flowchart, "diagramPadding", "flowchart.diagramPadding")
        val wrappingWidth = float(flowchart, "wrappingWidth", "flowchart.wrappingWidth")
        val minNodeWidth = float(flowchart, "minNodeWidth", "flowchart.minNodeWidth")
        val padding = float(flowchart, "padding", "flowchart.padding")
        val curve = string(flowchart, "curve", "flowchart.curve")
        val flowLook = string(flowchart, "look", "flowchart.look")
        val topLook = string(map, "look", "look")
        val titleTopMargin = float(flowchart, "titleTopMargin", "flowchart.titleTopMargin")
        val inheritDirection = boolean(flowchart, "inheritDir", "flowchart.inheritDir")
        val flowHtmlLabels = boolean(flowchart, "htmlLabels", "flowchart.htmlLabels")
        val topHtmlLabels = boolean(map, "htmlLabels", "htmlLabels")
        val markdownAutoWrap = boolean(map, "markdownAutoWrap", "markdownAutoWrap")
        val fontSize = float(map, "fontSize", "fontSize")
        val layout = string(flowchart, "layout", "flowchart.layout")
            ?: string(map, "layout", "layout")
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
                curve = curve,
                fontSize = fontSize,
                look = flowLook ?: topLook,
                titleTopMargin = titleTopMargin,
                inheritDirection = inheritDirection,
                htmlLabels = topHtmlLabels ?: flowHtmlLabels,
                markdownAutoWrap = markdownAutoWrap,
                maxEdges = maxEdges,
                layout = layout,
            ),
        )
    }

    private fun cleanupComments(source: String): String =
        COMMENT_LINE.replace(source, "").trimStart()

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
        "fontFamily",
        "theme",
        "themeCSS",
        "themeVariables",
    )
    private val FLOWCHART_UNTRANSLATED_KEYS = setOf(
        "arrowMarkerAbsolute",
        "theme",
        "useMaxWidth",
    )
    private val CARRIAGE_RETURN = Regex("""\r\n?""")
    private val HTML_TAG = Regex("""<(\w+)([^>]*)>""")
    private val DOUBLE_QUOTED_ATTRIBUTE = Regex("""="([^"]*)"""")
    private val FRONT_MATTER = Regex(
        pattern = """^([^\S\n\r]*)-{3}\s*[\n\r](.*?)[\n\r]\1-{3}\s*[\n\r]+""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
    private val DIRECTIVE = Regex(
        pattern = """%%\{\s*(?:(\w+)\s*:|(\w+))\s*(?:(\w+)|((?:(?!}%%).|\r?\n)*))?\s*(?:}%%)?""",
        options = setOf(RegexOption.IGNORE_CASE),
    )
    private val COMMENT_LINE = Regex("""(?m)^\s*%%(?!\{)[^\n]+\n?""")
    private val STYLE_WITH_ENTITY = Regex("""style.*:\S*#.*;""")
    private val CLASS_WITH_ENTITY = Regex("""classDef.*:\S*#.*;""")
    private val ENTITY = Regex("""#\w+;""")
    private val SIGNED_INTEGER = Regex("""^\+?\d+$""")
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

internal data class MermaidConfigOverride(
    val nodeSpacing: Float? = null,
    val rankSpacing: Float? = null,
    val diagramPadding: Float? = null,
    val wrappingWidth: Float? = null,
    val minNodeWidth: Float? = null,
    val flowchartPadding: Float? = null,
    val curve: String? = null,
    val fontSize: Float? = null,
    val look: String? = null,
    val titleTopMargin: Float? = null,
    val inheritDirection: Boolean? = null,
    val htmlLabels: Boolean? = null,
    val markdownAutoWrap: Boolean? = null,
    val maxEdges: Int? = null,
    val layout: String? = null,
) {
    fun merge(overrides: MermaidConfigOverride): MermaidConfigOverride = MermaidConfigOverride(
        nodeSpacing = overrides.nodeSpacing ?: nodeSpacing,
        rankSpacing = overrides.rankSpacing ?: rankSpacing,
        diagramPadding = overrides.diagramPadding ?: diagramPadding,
        wrappingWidth = overrides.wrappingWidth ?: wrappingWidth,
        minNodeWidth = overrides.minNodeWidth ?: minNodeWidth,
        flowchartPadding = overrides.flowchartPadding ?: flowchartPadding,
        curve = overrides.curve ?: curve,
        fontSize = overrides.fontSize ?: fontSize,
        look = overrides.look ?: look,
        titleTopMargin = overrides.titleTopMargin ?: titleTopMargin,
        inheritDirection = overrides.inheritDirection ?: inheritDirection,
        htmlLabels = overrides.htmlLabels ?: htmlLabels,
        markdownAutoWrap = overrides.markdownAutoWrap ?: markdownAutoWrap,
        maxEdges = overrides.maxEdges ?: maxEdges,
        layout = overrides.layout ?: layout,
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
        val resolvedLayout = layout ?: "dagre"
        if (resolvedLayout != "dagre") {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = "$resolvedLayout layout",
                    message = "Native Flowchart currently translates Mermaid's dagre layout only",
                ),
            )
        }
        return GMResult.Ok(
            options.copy(
                nodeSpacing = nodeSpacing ?: options.nodeSpacing,
                rankSpacing = rankSpacing ?: options.rankSpacing,
                diagramPadding = diagramPadding ?: options.diagramPadding,
                wrappingWidth = wrappingWidth ?: options.wrappingWidth,
                minNodeWidth = minNodeWidth ?: options.minNodeWidth,
                flowchartPadding = flowchartPadding ?: options.flowchartPadding,
                curve = curve ?: options.curve,
                fontSize = fontSize ?: options.fontSize,
                look = resolvedLook,
                titleTopMargin = titleTopMargin ?: options.titleTopMargin,
                inheritDirection = inheritDirection ?: options.inheritDirection,
                htmlLabels = htmlLabels ?: options.htmlLabels,
                markdownAutoWrap = markdownAutoWrap ?: options.markdownAutoWrap,
                maxEdges = maxEdges ?: options.maxEdges,
            ),
        )
    }
}

private data class FrontmatterResult(
    val text: String,
    val title: String? = null,
    val config: MermaidConfigOverride = MermaidConfigOverride(),
    val lineOffset: Int = 0,
)

private data class DirectiveResult(
    val text: String,
    val config: MermaidConfigOverride,
)
