package com.swithun.cmpmermaid.core

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
        val displayMode = root.scalar("displayMode")
        if (displayMode != null && displayMode !in setOf("", "compact")) {
            return configurationError(
                "Mermaid frontmatter 'displayMode' must be empty or compact",
            )
        }
        return GMResult.Ok(
            FrontmatterResult(
                text = source.drop(match.value.length),
                title = root.scalar("title"),
                config = config.copy(
                    ganttDisplayMode = displayMode ?: config.ganttDisplayMode,
                ),
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
        val stateDiagram = map.map("state")
        val erDiagram = map.map("er")
        val gantt = map.map("gantt")
        val journey = map.map("journey")
        val timeline = map.map("timeline")
        val requirement = map.map("requirement")
        val gitGraph = map.map("gitGraph")
        val mindmap = map.map("mindmap")
        val packet = map.map("packet")
        val radar = map.map("radar")
        val sankey = map.map("sankey")
        val ishikawa = map.map("ishikawa")
        val cynefin = map.map("cynefin")
        val treemap = map.map("treemap")
        val venn = map.map("venn")
        val kanban = map.map("kanban")
        val pie = map.map("pie")
        val quadrantChart = map.map("quadrantChart")
        val xyChart = map.map("xyChart")
        val xyXAxis = xyChart?.map("xAxis")
        val xyYAxis = xyChart?.map("yAxis")
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

        fun fontSize(
            owner: YamlMap?,
            key: String,
            path: String,
        ): Float? {
            val scalar = owner?.node(key) ?: return null
            return (scalar as? YamlScalar)
                ?.content
                ?.removeSuffix("px")
                ?.trim()
                ?.toFloatOrNull()
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' must be a number or pixel size",
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

        fun color(
            owner: YamlMap?,
            key: String,
            path: String,
            emptyAsNull: Boolean = false,
        ): SceneColor? {
            val value = string(owner, key, path) ?: return null
            if (emptyAsNull && value.isBlank()) {
                return null
            }
            return CssColorParser.parse(value)
                ?: run {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' has invalid color '$value'",
                    )
                    null
                }
        }

        fun colorList(
            owner: YamlMap?,
            key: String,
            path: String,
        ): List<SceneColor>? {
            val node = owner?.node(key) ?: return null
            val list = node as? YamlList
            if (list == null) {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName '$path' must be a color array",
                )
                return null
            }
            if (list.items.isEmpty()) {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName '$path' must not be empty",
                )
                return null
            }
            val result = mutableListOf<SceneColor>()
            list.items.forEachIndexed { index, item ->
                val raw = (item as? YamlScalar)?.content
                val parsed = raw?.let(CssColorParser::parse)
                if (parsed == null) {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path' has invalid color at index $index",
                    )
                    return null
                }
                result += parsed
            }
            return result
        }

        fun colorMap(
            owner: YamlMap?,
            key: String,
            path: String,
        ): Map<String, SceneColor>? {
            val node = owner?.node(key) ?: return null
            val yamlMap = node as? YamlMap
            if (yamlMap == null) {
                readError = MermaidError.Configuration(
                    "Mermaid $sourceName '$path' must be a color map",
                )
                return null
            }
            val result = linkedMapOf<String, SceneColor>()
            yamlMap.entries.forEach { (entryKey, entryValue) ->
                val raw = (entryValue as? YamlScalar)?.content
                val parsed = raw?.let(CssColorParser::parse)
                if (parsed == null) {
                    readError = MermaidError.Configuration(
                        "Mermaid $sourceName '$path.${entryKey.content}' has invalid color",
                    )
                    return null
                }
                result[entryKey.content] = parsed
            }
            return result
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
                    is YamlMap -> {
                        if (entryKey.content !in setOf("xyChart", "radar", "cynefin")) {
                            readError = MermaidError.Configuration(
                                "Mermaid $sourceName '$path.${entryKey.content}' " +
                                    "does not support nested theme variables",
                            )
                            return null
                        }
                        entryValue.entries.forEach { (nestedKey, nestedValue) ->
                            val scalar = nestedValue as? YamlScalar
                            if (scalar == null) {
                                readError = MermaidError.Configuration(
                                    "Mermaid $sourceName " +
                                        "'$path.${entryKey.content}.${nestedKey.content}' " +
                                        "must be a scalar",
                                )
                                return null
                            }
                            scalars["${entryKey.content}.${nestedKey.content}"] = scalar.content
                        }
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
        val statePadding = float(stateDiagram, "padding", "state.padding")
        val stateWrappingWidth = float(
            stateDiagram,
            "wrappingWidth",
            "state.wrappingWidth",
        )
        val stateMinNodeWidth = float(
            stateDiagram,
            "minNodeWidth",
            "state.minNodeWidth",
        )
        val stateNodeSpacing = float(stateDiagram, "nodeSpacing", "state.nodeSpacing")
        val stateRankSpacing = float(stateDiagram, "rankSpacing", "state.rankSpacing")
        val stateTitleTopMargin = float(
            stateDiagram,
            "titleTopMargin",
            "state.titleTopMargin",
        )
        val erDiagramPadding = float(erDiagram, "diagramPadding", "er.diagramPadding")
        val erEntityPadding = float(erDiagram, "entityPadding", "er.entityPadding")
        val erMinEntityWidth = float(erDiagram, "minEntityWidth", "er.minEntityWidth")
        val erMinEntityHeight = float(erDiagram, "minEntityHeight", "er.minEntityHeight")
        val erNodeSpacing = float(erDiagram, "nodeSpacing", "er.nodeSpacing")
        val erRankSpacing = float(erDiagram, "rankSpacing", "er.rankSpacing")
        val erTitleTopMargin = float(erDiagram, "titleTopMargin", "er.titleTopMargin")
        val ganttTitleTopMargin = float(gantt, "titleTopMargin", "gantt.titleTopMargin")
        val ganttBarHeight = float(gantt, "barHeight", "gantt.barHeight")
        val ganttBarGap = float(gantt, "barGap", "gantt.barGap")
        val ganttTopPadding = float(gantt, "topPadding", "gantt.topPadding")
        val ganttRightPadding = float(gantt, "rightPadding", "gantt.rightPadding")
        val ganttLeftPadding = float(gantt, "leftPadding", "gantt.leftPadding")
        val ganttGridLineStartPadding = float(
            gantt,
            "gridLineStartPadding",
            "gantt.gridLineStartPadding",
        )
        val ganttFontSize = float(gantt, "fontSize", "gantt.fontSize")
        val ganttSectionFontSize = float(gantt, "sectionFontSize", "gantt.sectionFontSize")
        val ganttNumberSectionStyles = int(
            gantt,
            "numberSectionStyles",
            "gantt.numberSectionStyles",
        )
        val ganttAxisFormat = string(gantt, "axisFormat", "gantt.axisFormat")
        val ganttTickInterval = string(gantt, "tickInterval", "gantt.tickInterval")
        val ganttTopAxis = boolean(gantt, "topAxis", "gantt.topAxis")
        val ganttDisplayMode = enumString(
            gantt,
            "displayMode",
            "gantt.displayMode",
            setOf("", "compact"),
        ) ?: enumString(
            map,
            "displayMode",
            "displayMode",
            setOf("", "compact"),
        )
        val ganttWeekday = enumString(
            gantt,
            "weekday",
            "gantt.weekday",
            setOf(
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday",
            ),
        )
        val ganttUseWidth = float(gantt, "useWidth", "gantt.useWidth")
        val journeyDiagramMarginX =
            float(journey, "diagramMarginX", "journey.diagramMarginX")
        val journeyDiagramMarginY =
            float(journey, "diagramMarginY", "journey.diagramMarginY")
        val journeyLeftMargin = float(journey, "leftMargin", "journey.leftMargin")
        val journeyMaxLabelWidth =
            float(journey, "maxLabelWidth", "journey.maxLabelWidth")
        val journeyWidth = float(journey, "width", "journey.width")
        val journeyHeight = float(journey, "height", "journey.height")
        val journeyBoxMargin = float(journey, "boxMargin", "journey.boxMargin")
        val journeyBoxTextMargin =
            float(journey, "boxTextMargin", "journey.boxTextMargin")
        val journeyNoteMargin = float(journey, "noteMargin", "journey.noteMargin")
        val journeyMessageMargin =
            float(journey, "messageMargin", "journey.messageMargin")
        val journeyMessageAlign =
            string(journey, "messageAlign", "journey.messageAlign")
        val journeyBottomMarginAdj =
            float(journey, "bottomMarginAdj", "journey.bottomMarginAdj")
        val journeyRightAngles =
            boolean(journey, "rightAngles", "journey.rightAngles")
        val journeyTaskFontSize =
            float(journey, "taskFontSize", "journey.taskFontSize")
        val journeyTaskFontFamily =
            string(journey, "taskFontFamily", "journey.taskFontFamily")
        val journeyTaskMargin = float(journey, "taskMargin", "journey.taskMargin")
        val journeyActivationWidth =
            float(journey, "activationWidth", "journey.activationWidth")
        val journeyTextPlacement = enumString(
            journey,
            "textPlacement",
            "journey.textPlacement",
            setOf("fo", "old", "tspan"),
        )
        val journeyActorColours =
            colorList(journey, "actorColours", "journey.actorColours")
        val journeySectionFills =
            colorList(journey, "sectionFills", "journey.sectionFills")
        val journeySectionColours =
            colorList(journey, "sectionColours", "journey.sectionColours")
        val journeyTitleColor = color(
            journey,
            "titleColor",
            "journey.titleColor",
            emptyAsNull = true,
        )
        val journeyTitleFontFamily =
            string(journey, "titleFontFamily", "journey.titleFontFamily")
        val journeyTitleFontSize =
            string(journey, "titleFontSize", "journey.titleFontSize")
        val journeyPositiveValues = listOf(
            "journey.maxLabelWidth" to journeyMaxLabelWidth,
            "journey.width" to journeyWidth,
            "journey.height" to journeyHeight,
            "journey.taskFontSize" to journeyTaskFontSize,
        )
        journeyPositiveValues.firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        val journeyNonNegativeValues = listOf(
            "journey.diagramMarginX" to journeyDiagramMarginX,
            "journey.diagramMarginY" to journeyDiagramMarginY,
            "journey.leftMargin" to journeyLeftMargin,
            "journey.boxMargin" to journeyBoxMargin,
            "journey.boxTextMargin" to journeyBoxTextMargin,
            "journey.noteMargin" to journeyNoteMargin,
            "journey.messageMargin" to journeyMessageMargin,
            "journey.bottomMarginAdj" to journeyBottomMarginAdj,
            "journey.taskMargin" to journeyTaskMargin,
            "journey.activationWidth" to journeyActivationWidth,
        )
        journeyNonNegativeValues.firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        if (journeyTitleFontSize != null && journeyTitleFontSize.isBlank()) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'journey.titleFontSize' must not be blank",
            )
        }
        val timelineUseWidth = float(timeline, "useWidth", "timeline.useWidth")
        val timelineUseMaxWidth =
            boolean(timeline, "useMaxWidth", "timeline.useMaxWidth")
        val timelineTheme = appearanceString(timeline, "theme", "timeline.theme")
            ?.takeIf(USABLE_THEMES::contains)
        val timelineLook = appearanceString(timeline, "look", "timeline.look")
            ?.takeIf(USABLE_LOOKS::contains)
        val timelineLayout = string(timeline, "layout", "timeline.layout")
        val timelineDiagramMarginX =
            float(timeline, "diagramMarginX", "timeline.diagramMarginX")
        val timelineDiagramMarginY =
            float(timeline, "diagramMarginY", "timeline.diagramMarginY")
        val timelineLeftMargin = float(timeline, "leftMargin", "timeline.leftMargin")
        val timelineWidth = float(timeline, "width", "timeline.width")
        val timelineHeight = float(timeline, "height", "timeline.height")
        val timelinePadding = float(timeline, "padding", "timeline.padding")
        val timelineBoxMargin = float(timeline, "boxMargin", "timeline.boxMargin")
        val timelineBoxTextMargin =
            float(timeline, "boxTextMargin", "timeline.boxTextMargin")
        val timelineNoteMargin = float(timeline, "noteMargin", "timeline.noteMargin")
        val timelineMessageMargin =
            float(timeline, "messageMargin", "timeline.messageMargin")
        val timelineMessageAlign = enumString(
            timeline,
            "messageAlign",
            "timeline.messageAlign",
            setOf("left", "center", "right"),
        )
        val timelineBottomMarginAdj =
            float(timeline, "bottomMarginAdj", "timeline.bottomMarginAdj")
        val timelineRightAngles =
            boolean(timeline, "rightAngles", "timeline.rightAngles")
        val timelineTaskFontSize =
            fontSize(timeline, "taskFontSize", "timeline.taskFontSize")
        val timelineTaskFontFamily =
            string(timeline, "taskFontFamily", "timeline.taskFontFamily")
        val timelineTaskMargin = float(timeline, "taskMargin", "timeline.taskMargin")
        val timelineActivationWidth =
            float(timeline, "activationWidth", "timeline.activationWidth")
        val timelineTextPlacement =
            string(timeline, "textPlacement", "timeline.textPlacement")
        val timelineActorColours =
            colorList(timeline, "actorColours", "timeline.actorColours")
        val timelineSectionFills =
            colorList(timeline, "sectionFills", "timeline.sectionFills")
        val timelineSectionColours =
            colorList(timeline, "sectionColours", "timeline.sectionColours")
        val timelineDisableMulticolor =
            boolean(timeline, "disableMulticolor", "timeline.disableMulticolor")
        listOf(
            "timeline.diagramMarginX" to timelineDiagramMarginX,
            "timeline.diagramMarginY" to timelineDiagramMarginY,
            "timeline.leftMargin" to timelineLeftMargin,
            "timeline.width" to timelineWidth,
            "timeline.height" to timelineHeight,
            "timeline.padding" to timelinePadding,
            "timeline.boxMargin" to timelineBoxMargin,
            "timeline.boxTextMargin" to timelineBoxTextMargin,
            "timeline.noteMargin" to timelineNoteMargin,
            "timeline.messageMargin" to timelineMessageMargin,
            "timeline.bottomMarginAdj" to timelineBottomMarginAdj,
            "timeline.taskFontSize" to timelineTaskFontSize,
            "timeline.taskMargin" to timelineTaskMargin,
            "timeline.activationWidth" to timelineActivationWidth,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        if (timelineUseWidth != null && !timelineUseWidth.isFinite()) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'timeline.useWidth' must be finite",
            )
        }
        val pieTextPosition = float(pie, "textPosition", "pie.textPosition")
        val pieDonutHole = float(pie, "donutHole", "pie.donutHole")
        val pieLegendPosition = enumString(
            pie,
            "legendPosition",
            "pie.legendPosition",
            setOf("top", "bottom", "left", "right", "center"),
        )
        val pieHighlightSlice = string(pie, "highlightSlice", "pie.highlightSlice")
        if (pieTextPosition != null && (!pieTextPosition.isFinite() || pieTextPosition !in 0f..1f)) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'pie.textPosition' must be between 0 and 1",
            )
        }
        if (pieDonutHole != null && (!pieDonutHole.isFinite() || pieDonutHole !in 0f..0.9f)) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'pie.donutHole' must be between 0 and 0.9",
            )
        }
        val quadrantChartWidth =
            float(quadrantChart, "chartWidth", "quadrantChart.chartWidth")
        val quadrantChartHeight =
            float(quadrantChart, "chartHeight", "quadrantChart.chartHeight")
        val quadrantTitleFontSize =
            float(quadrantChart, "titleFontSize", "quadrantChart.titleFontSize")
        val quadrantTitlePadding =
            float(quadrantChart, "titlePadding", "quadrantChart.titlePadding")
        val quadrantPadding =
            float(quadrantChart, "quadrantPadding", "quadrantChart.quadrantPadding")
        val quadrantXAxisLabelPadding =
            float(quadrantChart, "xAxisLabelPadding", "quadrantChart.xAxisLabelPadding")
        val quadrantYAxisLabelPadding =
            float(quadrantChart, "yAxisLabelPadding", "quadrantChart.yAxisLabelPadding")
        val quadrantXAxisLabelFontSize =
            float(quadrantChart, "xAxisLabelFontSize", "quadrantChart.xAxisLabelFontSize")
        val quadrantYAxisLabelFontSize =
            float(quadrantChart, "yAxisLabelFontSize", "quadrantChart.yAxisLabelFontSize")
        val quadrantLabelFontSize =
            float(quadrantChart, "quadrantLabelFontSize", "quadrantChart.quadrantLabelFontSize")
        val quadrantTextTopPadding =
            float(quadrantChart, "quadrantTextTopPadding", "quadrantChart.quadrantTextTopPadding")
        val quadrantPointTextPadding =
            float(quadrantChart, "pointTextPadding", "quadrantChart.pointTextPadding")
        val quadrantPointLabelFontSize =
            float(quadrantChart, "pointLabelFontSize", "quadrantChart.pointLabelFontSize")
        val quadrantPointRadius =
            float(quadrantChart, "pointRadius", "quadrantChart.pointRadius")
        val quadrantXAxisPosition = enumString(
            quadrantChart,
            "xAxisPosition",
            "quadrantChart.xAxisPosition",
            setOf("top", "bottom"),
        )
        val quadrantYAxisPosition = enumString(
            quadrantChart,
            "yAxisPosition",
            "quadrantChart.yAxisPosition",
            setOf("left", "right"),
        )
        val quadrantInternalBorderStrokeWidth = float(
            quadrantChart,
            "quadrantInternalBorderStrokeWidth",
            "quadrantChart.quadrantInternalBorderStrokeWidth",
        )
        val quadrantExternalBorderStrokeWidth = float(
            quadrantChart,
            "quadrantExternalBorderStrokeWidth",
            "quadrantChart.quadrantExternalBorderStrokeWidth",
        )
        val quadrantUseMaxWidth =
            boolean(quadrantChart, "useMaxWidth", "quadrantChart.useMaxWidth")
        listOf(
            "quadrantChart.chartWidth" to quadrantChartWidth,
            "quadrantChart.chartHeight" to quadrantChartHeight,
            "quadrantChart.titleFontSize" to quadrantTitleFontSize,
            "quadrantChart.titlePadding" to quadrantTitlePadding,
            "quadrantChart.quadrantPadding" to quadrantPadding,
            "quadrantChart.xAxisLabelPadding" to quadrantXAxisLabelPadding,
            "quadrantChart.yAxisLabelPadding" to quadrantYAxisLabelPadding,
            "quadrantChart.xAxisLabelFontSize" to quadrantXAxisLabelFontSize,
            "quadrantChart.yAxisLabelFontSize" to quadrantYAxisLabelFontSize,
            "quadrantChart.quadrantLabelFontSize" to quadrantLabelFontSize,
            "quadrantChart.quadrantTextTopPadding" to quadrantTextTopPadding,
            "quadrantChart.pointTextPadding" to quadrantPointTextPadding,
            "quadrantChart.pointLabelFontSize" to quadrantPointLabelFontSize,
            "quadrantChart.pointRadius" to quadrantPointRadius,
            "quadrantChart.quadrantInternalBorderStrokeWidth" to
                quadrantInternalBorderStrokeWidth,
            "quadrantChart.quadrantExternalBorderStrokeWidth" to
                quadrantExternalBorderStrokeWidth,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        val xyWidth = float(xyChart, "width", "xyChart.width")
        val xyHeight = float(xyChart, "height", "xyChart.height")
        val xyTitleFontSize = float(xyChart, "titleFontSize", "xyChart.titleFontSize")
        val xyTitlePadding = float(xyChart, "titlePadding", "xyChart.titlePadding")
        val xyShowTitle = boolean(xyChart, "showTitle", "xyChart.showTitle")
        val xyShowLegend = boolean(xyChart, "showLegend", "xyChart.showLegend")
        val xyLegendFontSize = float(xyChart, "legendFontSize", "xyChart.legendFontSize")
        val xyLegendPadding = float(xyChart, "legendPadding", "xyChart.legendPadding")
        val xyShowDataLabel = boolean(xyChart, "showDataLabel", "xyChart.showDataLabel")
        val xyShowDataLabelOutsideBar = boolean(
            xyChart,
            "showDataLabelOutsideBar",
            "xyChart.showDataLabelOutsideBar",
        )
        val xyChartOrientation = enumString(
            xyChart,
            "chartOrientation",
            "xyChart.chartOrientation",
            setOf("vertical", "horizontal"),
        )
        val xyPlotReservedSpacePercent = float(
            xyChart,
            "plotReservedSpacePercent",
            "xyChart.plotReservedSpacePercent",
        )
        fun xyAxisOverride(
            axis: YamlMap?,
            path: String,
        ): MermaidXyAxisConfigOverride? {
            if (axis == null) return null
            return MermaidXyAxisConfigOverride(
                showLabel = boolean(axis, "showLabel", "$path.showLabel"),
                labelFontSize = float(axis, "labelFontSize", "$path.labelFontSize"),
                labelPadding = float(axis, "labelPadding", "$path.labelPadding"),
                showTitle = boolean(axis, "showTitle", "$path.showTitle"),
                titleFontSize = float(axis, "titleFontSize", "$path.titleFontSize"),
                titlePadding = float(axis, "titlePadding", "$path.titlePadding"),
                showTick = boolean(axis, "showTick", "$path.showTick"),
                tickLength = float(axis, "tickLength", "$path.tickLength"),
                tickWidth = float(axis, "tickWidth", "$path.tickWidth"),
                showAxisLine = boolean(axis, "showAxisLine", "$path.showAxisLine"),
                axisLineWidth = float(axis, "axisLineWidth", "$path.axisLineWidth"),
                labelRotation = float(axis, "labelRotation", "$path.labelRotation"),
            )
        }
        val xyXAxisOverride = xyAxisOverride(xyXAxis, "xyChart.xAxis")
        val xyYAxisOverride = xyAxisOverride(xyYAxis, "xyChart.yAxis")
        val xyPositiveValues = listOf(
            "xyChart.width" to xyWidth,
            "xyChart.height" to xyHeight,
            "xyChart.titleFontSize" to xyTitleFontSize,
            "xyChart.legendFontSize" to xyLegendFontSize,
            "xyChart.xAxis.labelFontSize" to xyXAxisOverride?.labelFontSize,
            "xyChart.xAxis.titleFontSize" to xyXAxisOverride?.titleFontSize,
            "xyChart.xAxis.tickLength" to xyXAxisOverride?.tickLength,
            "xyChart.xAxis.tickWidth" to xyXAxisOverride?.tickWidth,
            "xyChart.xAxis.axisLineWidth" to xyXAxisOverride?.axisLineWidth,
            "xyChart.yAxis.labelFontSize" to xyYAxisOverride?.labelFontSize,
            "xyChart.yAxis.titleFontSize" to xyYAxisOverride?.titleFontSize,
            "xyChart.yAxis.tickLength" to xyYAxisOverride?.tickLength,
            "xyChart.yAxis.tickWidth" to xyYAxisOverride?.tickWidth,
            "xyChart.yAxis.axisLineWidth" to xyYAxisOverride?.axisLineWidth,
        )
        val invalidPositive = xyPositiveValues.firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 1f)
        }
        if (invalidPositive != null) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalidPositive.first}' must be at least 1",
            )
        }
        val xyNonNegativeValues = listOf(
            "xyChart.titlePadding" to xyTitlePadding,
            "xyChart.legendPadding" to xyLegendPadding,
            "xyChart.xAxis.labelPadding" to xyXAxisOverride?.labelPadding,
            "xyChart.xAxis.titlePadding" to xyXAxisOverride?.titlePadding,
            "xyChart.yAxis.labelPadding" to xyYAxisOverride?.labelPadding,
            "xyChart.yAxis.titlePadding" to xyYAxisOverride?.titlePadding,
        )
        val invalidNonNegative = xyNonNegativeValues.firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }
        if (invalidNonNegative != null) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalidNonNegative.first}' must be non-negative",
            )
        }
        listOf(
            "xyChart.xAxis.labelRotation" to xyXAxisOverride?.labelRotation,
            "xyChart.yAxis.labelRotation" to xyYAxisOverride?.labelRotation,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value !in -90f..90f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be between -90 and 90",
            )
        }
        if (
            xyPlotReservedSpacePercent != null &&
            (!xyPlotReservedSpacePercent.isFinite() || xyPlotReservedSpacePercent < 30f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'xyChart.plotReservedSpacePercent' must be at least 30",
            )
        }
        val curve = string(flowchart, "curve", "flowchart.curve")
        val flowTheme = appearanceString(flowchart, "theme", "flowchart.theme")
            ?.takeIf(USABLE_THEMES::contains)
        val requirementTheme = appearanceString(
            requirement,
            "theme",
            "requirement.theme",
        )?.takeIf(USABLE_THEMES::contains)
        val topTheme = appearanceString(map, "theme", "theme")
            ?.takeIf(USABLE_THEMES::contains)
        val parsedThemeVariables = themeVariables(map, "themeVariables", "themeVariables")
        val flowLook = appearanceString(flowchart, "look", "flowchart.look")
            ?.takeIf(USABLE_LOOKS::contains)
        val requirementLook = appearanceString(
            requirement,
            "look",
            "requirement.look",
        )?.takeIf(USABLE_LOOKS::contains)
        val gitGraphTitleTopMargin =
            float(gitGraph, "titleTopMargin", "gitGraph.titleTopMargin")
        val gitGraphDiagramPadding =
            float(gitGraph, "diagramPadding", "gitGraph.diagramPadding")
        val gitGraphMainBranchName =
            string(gitGraph, "mainBranchName", "gitGraph.mainBranchName")
        val gitGraphMainBranchOrder =
            float(gitGraph, "mainBranchOrder", "gitGraph.mainBranchOrder")
        val gitGraphShowCommitLabel =
            boolean(gitGraph, "showCommitLabel", "gitGraph.showCommitLabel")
        val gitGraphShowBranches =
            boolean(gitGraph, "showBranches", "gitGraph.showBranches")
        val gitGraphRotateCommitLabel =
            boolean(gitGraph, "rotateCommitLabel", "gitGraph.rotateCommitLabel")
        val gitGraphParallelCommits =
            boolean(gitGraph, "parallelCommits", "gitGraph.parallelCommits")
        listOf(
            "gitGraph.titleTopMargin" to gitGraphTitleTopMargin,
            "gitGraph.diagramPadding" to gitGraphDiagramPadding,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        if (gitGraphMainBranchOrder != null && !gitGraphMainBranchOrder.isFinite()) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'gitGraph.mainBranchOrder' must be finite",
            )
        }
        val mindmapPadding = float(mindmap, "padding", "mindmap.padding")
        val mindmapMaxNodeWidth =
            float(mindmap, "maxNodeWidth", "mindmap.maxNodeWidth")
        val mindmapUseMaxWidth =
            boolean(mindmap, "useMaxWidth", "mindmap.useMaxWidth")
        val mindmapLayoutAlgorithm =
            string(mindmap, "layoutAlgorithm", "mindmap.layoutAlgorithm")
        listOf(
            "mindmap.padding" to mindmapPadding,
            "mindmap.maxNodeWidth" to mindmapMaxNodeWidth,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        val packetRowHeight = float(packet, "rowHeight", "packet.rowHeight")
        val packetBitWidth = float(packet, "bitWidth", "packet.bitWidth")
        val packetBitsPerRow = int(packet, "bitsPerRow", "packet.bitsPerRow")
        val packetShowBits = boolean(packet, "showBits", "packet.showBits")
        val packetPaddingX = float(packet, "paddingX", "packet.paddingX")
        val packetPaddingY = float(packet, "paddingY", "packet.paddingY")
        val packetUseMaxWidth = boolean(packet, "useMaxWidth", "packet.useMaxWidth")
        listOf(
            "packet.rowHeight" to packetRowHeight,
            "packet.bitWidth" to packetBitWidth,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 1f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be at least 1",
            )
        }
        if (packetBitsPerRow != null && packetBitsPerRow < 1) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'packet.bitsPerRow' must be at least 1",
            )
        }
        listOf(
            "packet.paddingX" to packetPaddingX,
            "packet.paddingY" to packetPaddingY,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        val radarWidth = float(radar, "width", "radar.width")
        val radarHeight = float(radar, "height", "radar.height")
        val radarMarginTop = float(radar, "marginTop", "radar.marginTop")
        val radarMarginRight = float(radar, "marginRight", "radar.marginRight")
        val radarMarginBottom = float(radar, "marginBottom", "radar.marginBottom")
        val radarMarginLeft = float(radar, "marginLeft", "radar.marginLeft")
        val radarAxisScaleFactor =
            float(radar, "axisScaleFactor", "radar.axisScaleFactor")
        val radarAxisLabelFactor =
            float(radar, "axisLabelFactor", "radar.axisLabelFactor")
        val radarCurveTension = float(radar, "curveTension", "radar.curveTension")
        val radarUseMaxWidth = boolean(radar, "useMaxWidth", "radar.useMaxWidth")
        listOf(
            "radar.width" to radarWidth,
            "radar.height" to radarHeight,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 1f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be at least 1",
            )
        }
        listOf(
            "radar.marginTop" to radarMarginTop,
            "radar.marginRight" to radarMarginRight,
            "radar.marginBottom" to radarMarginBottom,
            "radar.marginLeft" to radarMarginLeft,
            "radar.axisScaleFactor" to radarAxisScaleFactor,
            "radar.axisLabelFactor" to radarAxisLabelFactor,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        if (
            radarCurveTension != null &&
            (!radarCurveTension.isFinite() || radarCurveTension !in 0f..1f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'radar.curveTension' must be between 0 and 1",
            )
        }
        val sankeyWidth = float(sankey, "width", "sankey.width")
        val sankeyHeight = float(sankey, "height", "sankey.height")
        val sankeyLinkColor = string(sankey, "linkColor", "sankey.linkColor")
        val sankeyNodeAlignment =
            string(sankey, "nodeAlignment", "sankey.nodeAlignment")
        val sankeyUseMaxWidth = boolean(sankey, "useMaxWidth", "sankey.useMaxWidth")
        val sankeyShowValues = boolean(sankey, "showValues", "sankey.showValues")
        val sankeyPrefix = string(sankey, "prefix", "sankey.prefix")
        val sankeySuffix = string(sankey, "suffix", "sankey.suffix")
        val sankeyNodeWidth = float(sankey, "nodeWidth", "sankey.nodeWidth")
        val sankeyNodePadding = float(sankey, "nodePadding", "sankey.nodePadding")
        val sankeyLabelStyle = string(sankey, "labelStyle", "sankey.labelStyle")
        val sankeyNodeColors = colorMap(sankey, "nodeColors", "sankey.nodeColors")
        listOf(
            "sankey.width" to sankeyWidth,
            "sankey.height" to sankeyHeight,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        listOf(
            "sankey.nodeWidth" to sankeyNodeWidth,
            "sankey.nodePadding" to sankeyNodePadding,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        if (
            sankeyNodeAlignment != null &&
            sankeyNodeAlignment !in setOf("left", "right", "center", "justify")
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'sankey.nodeAlignment' is invalid",
            )
        }
        if (sankeyLabelStyle != null && sankeyLabelStyle !in setOf("legacy", "outlined")) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'sankey.labelStyle' is invalid",
            )
        }
        if (
            sankeyLinkColor != null &&
            sankeyLinkColor !in setOf("gradient", "source", "target") &&
            CssColorParser.parse(sankeyLinkColor) == null
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'sankey.linkColor' has invalid color '$sankeyLinkColor'",
            )
        }
        val ishikawaDiagramPadding =
            float(ishikawa, "diagramPadding", "ishikawa.diagramPadding")
        val ishikawaUseMaxWidth =
            boolean(ishikawa, "useMaxWidth", "ishikawa.useMaxWidth")
        if (
            ishikawaDiagramPadding != null &&
            (!ishikawaDiagramPadding.isFinite() || ishikawaDiagramPadding < 0f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'ishikawa.diagramPadding' must be non-negative",
            )
        }
        val cynefinWidth = float(cynefin, "width", "cynefin.width")
        val cynefinHeight = float(cynefin, "height", "cynefin.height")
        val cynefinPadding = float(cynefin, "padding", "cynefin.padding")
        val cynefinShowDomainDescriptions = boolean(
            cynefin,
            "showDomainDescriptions",
            "cynefin.showDomainDescriptions",
        )
        val cynefinBoundaryAmplitude = float(
            cynefin,
            "boundaryAmplitude",
            "cynefin.boundaryAmplitude",
        )
        val cynefinSeed = float(cynefin, "seed", "cynefin.seed")
        val cynefinUseMaxWidth =
            boolean(cynefin, "useMaxWidth", "cynefin.useMaxWidth")
        listOf(
            "cynefin.width" to cynefinWidth,
            "cynefin.height" to cynefinHeight,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        if (
            cynefinPadding != null &&
            (!cynefinPadding.isFinite() || cynefinPadding < 0f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'cynefin.padding' must be non-negative",
            )
        }
        if (
            cynefinBoundaryAmplitude != null &&
            (
                !cynefinBoundaryAmplitude.isFinite() ||
                    cynefinBoundaryAmplitude !in 0f..50f
                )
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'cynefin.boundaryAmplitude' " +
                    "must be between 0 and 50",
            )
        }
        if (cynefinSeed != null && !cynefinSeed.isFinite()) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'cynefin.seed' must be finite",
            )
        }
        val treemapUseMaxWidth =
            boolean(treemap, "useMaxWidth", "treemap.useMaxWidth")
        val treemapPadding = float(treemap, "padding", "treemap.padding")
        val treemapDiagramPadding =
            float(treemap, "diagramPadding", "treemap.diagramPadding")
        val treemapShowValues = boolean(treemap, "showValues", "treemap.showValues")
        val treemapNodeWidth = float(treemap, "nodeWidth", "treemap.nodeWidth")
        val treemapNodeHeight = float(treemap, "nodeHeight", "treemap.nodeHeight")
        val treemapBorderWidth = float(treemap, "borderWidth", "treemap.borderWidth")
        val treemapValueFontSize =
            float(treemap, "valueFontSize", "treemap.valueFontSize")
        val treemapLabelFontSize =
            float(treemap, "labelFontSize", "treemap.labelFontSize")
        val treemapValueFormat = string(treemap, "valueFormat", "treemap.valueFormat")
        listOf(
            "treemap.nodeWidth" to treemapNodeWidth,
            "treemap.nodeHeight" to treemapNodeHeight,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        listOf(
            "treemap.padding" to treemapPadding,
            "treemap.diagramPadding" to treemapDiagramPadding,
            "treemap.borderWidth" to treemapBorderWidth,
            "treemap.valueFontSize" to treemapValueFontSize,
            "treemap.labelFontSize" to treemapLabelFontSize,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value < 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be non-negative",
            )
        }
        val vennWidth = float(venn, "width", "venn.width")
        val vennHeight = float(venn, "height", "venn.height")
        val vennPadding = float(venn, "padding", "venn.padding")
        val vennUseDebugLayout =
            boolean(venn, "useDebugLayout", "venn.useDebugLayout")
        val vennUseMaxWidth = boolean(venn, "useMaxWidth", "venn.useMaxWidth")
        listOf(
            "venn.width" to vennWidth,
            "venn.height" to vennHeight,
        ).firstOrNull { (_, value) ->
            value != null && (!value.isFinite() || value <= 0f)
        }?.let { invalid ->
            readError = MermaidError.Configuration(
                "Mermaid $sourceName '${invalid.first}' must be positive",
            )
        }
        if (vennPadding != null && (!vennPadding.isFinite() || vennPadding < 0f)) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'venn.padding' must be non-negative",
            )
        }
        val kanbanPadding = float(kanban, "padding", "kanban.padding")
        val kanbanSectionWidth = float(kanban, "sectionWidth", "kanban.sectionWidth")
        val kanbanTicketBaseUrl =
            string(kanban, "ticketBaseUrl", "kanban.ticketBaseUrl")
        if (
            kanbanPadding != null &&
            (!kanbanPadding.isFinite() || kanbanPadding < 0f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'kanban.padding' must be non-negative",
            )
        }
        if (
            kanbanSectionWidth != null &&
            (!kanbanSectionWidth.isFinite() || kanbanSectionWidth <= 0f)
        ) {
            readError = MermaidError.Configuration(
                "Mermaid $sourceName 'kanban.sectionWidth' must be positive",
            )
        }
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
                statePadding = statePadding,
                stateWrappingWidth = stateWrappingWidth,
                stateMinNodeWidth = stateMinNodeWidth,
                stateNodeSpacing = stateNodeSpacing,
                stateRankSpacing = stateRankSpacing,
                stateTitleTopMargin = stateTitleTopMargin,
                erDiagramPadding = erDiagramPadding,
                erEntityPadding = erEntityPadding,
                erMinEntityWidth = erMinEntityWidth,
                erMinEntityHeight = erMinEntityHeight,
                erNodeSpacing = erNodeSpacing,
                erRankSpacing = erRankSpacing,
                erTitleTopMargin = erTitleTopMargin,
                ganttTitleTopMargin = ganttTitleTopMargin,
                ganttBarHeight = ganttBarHeight,
                ganttBarGap = ganttBarGap,
                ganttTopPadding = ganttTopPadding,
                ganttRightPadding = ganttRightPadding,
                ganttLeftPadding = ganttLeftPadding,
                ganttGridLineStartPadding = ganttGridLineStartPadding,
                ganttFontSize = ganttFontSize,
                ganttSectionFontSize = ganttSectionFontSize,
                ganttNumberSectionStyles = ganttNumberSectionStyles,
                ganttAxisFormat = ganttAxisFormat,
                ganttTickInterval = ganttTickInterval,
                ganttTopAxis = ganttTopAxis,
                ganttDisplayMode = ganttDisplayMode,
                ganttWeekday = ganttWeekday,
                ganttUseWidth = ganttUseWidth,
                journey = journey?.let {
                    MermaidJourneyConfigOverride(
                        diagramMarginX = journeyDiagramMarginX,
                        diagramMarginY = journeyDiagramMarginY,
                        leftMargin = journeyLeftMargin,
                        maxLabelWidth = journeyMaxLabelWidth,
                        width = journeyWidth,
                        height = journeyHeight,
                        boxMargin = journeyBoxMargin,
                        boxTextMargin = journeyBoxTextMargin,
                        noteMargin = journeyNoteMargin,
                        messageMargin = journeyMessageMargin,
                        messageAlign = journeyMessageAlign,
                        bottomMarginAdj = journeyBottomMarginAdj,
                        rightAngles = journeyRightAngles,
                        taskFontSize = journeyTaskFontSize,
                        taskFontFamily = journeyTaskFontFamily,
                        taskMargin = journeyTaskMargin,
                        activationWidth = journeyActivationWidth,
                        textPlacement = journeyTextPlacement,
                        actorColours = journeyActorColours,
                        sectionFills = journeySectionFills,
                        sectionColours = journeySectionColours,
                        titleColor = journeyTitleColor,
                        titleFontFamily = journeyTitleFontFamily,
                        titleFontSize = journeyTitleFontSize,
                    )
                },
                timeline = timeline?.let {
                    MermaidTimelineConfigOverride(
                        useWidth = timelineUseWidth,
                        useMaxWidth = timelineUseMaxWidth,
                        theme = timelineTheme,
                        look = timelineLook,
                        layout = timelineLayout,
                        diagramMarginX = timelineDiagramMarginX,
                        diagramMarginY = timelineDiagramMarginY,
                        leftMargin = timelineLeftMargin,
                        width = timelineWidth,
                        height = timelineHeight,
                        padding = timelinePadding,
                        boxMargin = timelineBoxMargin,
                        boxTextMargin = timelineBoxTextMargin,
                        noteMargin = timelineNoteMargin,
                        messageMargin = timelineMessageMargin,
                        messageAlign = timelineMessageAlign,
                        bottomMarginAdj = timelineBottomMarginAdj,
                        rightAngles = timelineRightAngles,
                        taskFontSize = timelineTaskFontSize,
                        taskFontFamily = timelineTaskFontFamily,
                        taskMargin = timelineTaskMargin,
                        activationWidth = timelineActivationWidth,
                        textPlacement = timelineTextPlacement,
                        actorColours = timelineActorColours,
                        sectionFills = timelineSectionFills,
                        sectionColours = timelineSectionColours,
                        disableMulticolor = timelineDisableMulticolor,
                    )
                },
                pieTextPosition = pieTextPosition,
                pieDonutHole = pieDonutHole,
                pieLegendPosition = pieLegendPosition,
                pieHighlightSlice = pieHighlightSlice,
                quadrantChart = quadrantChart?.let {
                    MermaidQuadrantChartConfigOverride(
                        chartWidth = quadrantChartWidth,
                        chartHeight = quadrantChartHeight,
                        titleFontSize = quadrantTitleFontSize,
                        titlePadding = quadrantTitlePadding,
                        quadrantPadding = quadrantPadding,
                        xAxisLabelPadding = quadrantXAxisLabelPadding,
                        yAxisLabelPadding = quadrantYAxisLabelPadding,
                        xAxisLabelFontSize = quadrantXAxisLabelFontSize,
                        yAxisLabelFontSize = quadrantYAxisLabelFontSize,
                        quadrantLabelFontSize = quadrantLabelFontSize,
                        quadrantTextTopPadding = quadrantTextTopPadding,
                        pointTextPadding = quadrantPointTextPadding,
                        pointLabelFontSize = quadrantPointLabelFontSize,
                        pointRadius = quadrantPointRadius,
                        xAxisPosition = quadrantXAxisPosition,
                        yAxisPosition = quadrantYAxisPosition,
                        quadrantInternalBorderStrokeWidth =
                            quadrantInternalBorderStrokeWidth,
                        quadrantExternalBorderStrokeWidth =
                            quadrantExternalBorderStrokeWidth,
                        useMaxWidth = quadrantUseMaxWidth,
                    )
                },
                xyChart = xyChart?.let {
                    MermaidXyChartConfigOverride(
                        width = xyWidth,
                        height = xyHeight,
                        titleFontSize = xyTitleFontSize,
                        titlePadding = xyTitlePadding,
                        showTitle = xyShowTitle,
                        showLegend = xyShowLegend,
                        legendFontSize = xyLegendFontSize,
                        legendPadding = xyLegendPadding,
                        showDataLabel = xyShowDataLabel,
                        showDataLabelOutsideBar = xyShowDataLabelOutsideBar,
                        chartOrientation = xyChartOrientation,
                        plotReservedSpacePercent = xyPlotReservedSpacePercent,
                        xAxis = xyXAxisOverride,
                        yAxis = xyYAxisOverride,
                    )
                },
                curve = curve,
                fontSize = fontSize,
                fontFamily = fontFamily,
                themeName = flowTheme ?: topTheme,
                requirementThemeName = requirementTheme,
                gitGraph = gitGraph?.let {
                    MermaidGitGraphConfigOverride(
                        titleTopMargin = gitGraphTitleTopMargin,
                        diagramPadding = gitGraphDiagramPadding,
                        mainBranchName = gitGraphMainBranchName,
                        mainBranchOrder = gitGraphMainBranchOrder,
                        showCommitLabel = gitGraphShowCommitLabel,
                        showBranches = gitGraphShowBranches,
                        rotateCommitLabel = gitGraphRotateCommitLabel,
                        parallelCommits = gitGraphParallelCommits,
                    )
                },
                mindmap = if (mindmap != null || topLayout != null) {
                    MermaidMindmapConfigOverride(
                        padding = mindmapPadding,
                        maxNodeWidth = mindmapMaxNodeWidth,
                        useMaxWidth = mindmapUseMaxWidth,
                        layoutAlgorithm = topLayout ?: mindmapLayoutAlgorithm,
                    )
                } else {
                    null
                },
                packet = packet?.let {
                    MermaidPacketConfigOverride(
                        rowHeight = packetRowHeight,
                        bitWidth = packetBitWidth,
                        bitsPerRow = packetBitsPerRow,
                        showBits = packetShowBits,
                        paddingX = packetPaddingX,
                        paddingY = packetPaddingY,
                        useMaxWidth = packetUseMaxWidth,
                    )
                },
                radar = radar?.let {
                    MermaidRadarConfigOverride(
                        width = radarWidth,
                        height = radarHeight,
                        marginTop = radarMarginTop,
                        marginRight = radarMarginRight,
                        marginBottom = radarMarginBottom,
                        marginLeft = radarMarginLeft,
                        axisScaleFactor = radarAxisScaleFactor,
                        axisLabelFactor = radarAxisLabelFactor,
                        curveTension = radarCurveTension,
                        useMaxWidth = radarUseMaxWidth,
                    )
                },
                sankey = sankey?.let {
                    MermaidSankeyConfigOverride(
                        width = sankeyWidth,
                        height = sankeyHeight,
                        linkColor = sankeyLinkColor,
                        nodeAlignment = sankeyNodeAlignment,
                        useMaxWidth = sankeyUseMaxWidth,
                        showValues = sankeyShowValues,
                        prefix = sankeyPrefix,
                        suffix = sankeySuffix,
                        nodeWidth = sankeyNodeWidth,
                        nodePadding = sankeyNodePadding,
                        labelStyle = sankeyLabelStyle,
                        nodeColors = sankeyNodeColors,
                    )
                },
                ishikawa = ishikawa?.let {
                    MermaidIshikawaConfigOverride(
                        diagramPadding = ishikawaDiagramPadding,
                        useMaxWidth = ishikawaUseMaxWidth,
                    )
                },
                cynefin = cynefin?.let {
                    MermaidCynefinConfigOverride(
                        width = cynefinWidth,
                        height = cynefinHeight,
                        padding = cynefinPadding,
                        showDomainDescriptions = cynefinShowDomainDescriptions,
                        boundaryAmplitude = cynefinBoundaryAmplitude,
                        seed = cynefinSeed,
                        useMaxWidth = cynefinUseMaxWidth,
                    )
                },
                treemap = treemap?.let {
                    MermaidTreemapConfigOverride(
                        useMaxWidth = treemapUseMaxWidth,
                        padding = treemapPadding,
                        diagramPadding = treemapDiagramPadding,
                        showValues = treemapShowValues,
                        nodeWidth = treemapNodeWidth,
                        nodeHeight = treemapNodeHeight,
                        borderWidth = treemapBorderWidth,
                        valueFontSize = treemapValueFontSize,
                        labelFontSize = treemapLabelFontSize,
                        valueFormat = treemapValueFormat,
                    )
                },
                venn = venn?.let {
                    MermaidVennConfigOverride(
                        width = vennWidth,
                        height = vennHeight,
                        padding = vennPadding,
                        useDebugLayout = vennUseDebugLayout,
                        useMaxWidth = vennUseMaxWidth,
                    )
                },
                kanban = kanban?.let {
                    MermaidKanbanConfigOverride(
                        padding = kanbanPadding,
                        sectionWidth = kanbanSectionWidth,
                        ticketBaseUrl = kanbanTicketBaseUrl,
                    )
                },
                themeVariables = parsedThemeVariables?.scalars,
                themeColorArrays = parsedThemeVariables?.arrays,
                look = flowLook ?: topLook,
                requirementLook = requirementLook,
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
        """^([^\S\n\r]*)-{3}\s*[\n\r]([\s\S]*?)[\n\r]\1-{3}\s*[\n\r]+""",
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
    val statePadding: Float? = null,
    val stateWrappingWidth: Float? = null,
    val stateMinNodeWidth: Float? = null,
    val stateNodeSpacing: Float? = null,
    val stateRankSpacing: Float? = null,
    val stateTitleTopMargin: Float? = null,
    val erDiagramPadding: Float? = null,
    val erEntityPadding: Float? = null,
    val erMinEntityWidth: Float? = null,
    val erMinEntityHeight: Float? = null,
    val erNodeSpacing: Float? = null,
    val erRankSpacing: Float? = null,
    val erTitleTopMargin: Float? = null,
    val ganttTitleTopMargin: Float? = null,
    val ganttBarHeight: Float? = null,
    val ganttBarGap: Float? = null,
    val ganttTopPadding: Float? = null,
    val ganttRightPadding: Float? = null,
    val ganttLeftPadding: Float? = null,
    val ganttGridLineStartPadding: Float? = null,
    val ganttFontSize: Float? = null,
    val ganttSectionFontSize: Float? = null,
    val ganttNumberSectionStyles: Int? = null,
    val ganttAxisFormat: String? = null,
    val ganttTickInterval: String? = null,
    val ganttTopAxis: Boolean? = null,
    val ganttDisplayMode: String? = null,
    val ganttWeekday: String? = null,
    val ganttUseWidth: Float? = null,
    val journey: MermaidJourneyConfigOverride? = null,
    val timeline: MermaidTimelineConfigOverride? = null,
    val pieTextPosition: Float? = null,
    val pieDonutHole: Float? = null,
    val pieLegendPosition: String? = null,
    val pieHighlightSlice: String? = null,
    val quadrantChart: MermaidQuadrantChartConfigOverride? = null,
    val xyChart: MermaidXyChartConfigOverride? = null,
    val curve: String? = null,
    val fontSize: Float? = null,
    val fontFamily: String? = null,
    val themeName: String? = null,
    val requirementThemeName: String? = null,
    val gitGraph: MermaidGitGraphConfigOverride? = null,
    val mindmap: MermaidMindmapConfigOverride? = null,
    val packet: MermaidPacketConfigOverride? = null,
    val radar: MermaidRadarConfigOverride? = null,
    val sankey: MermaidSankeyConfigOverride? = null,
    val ishikawa: MermaidIshikawaConfigOverride? = null,
    val cynefin: MermaidCynefinConfigOverride? = null,
    val treemap: MermaidTreemapConfigOverride? = null,
    val venn: MermaidVennConfigOverride? = null,
    val kanban: MermaidKanbanConfigOverride? = null,
    val themeVariables: Map<String, String>? = null,
    val themeColorArrays: Map<String, List<String>>? = null,
    val look: String? = null,
    val requirementLook: String? = null,
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
        statePadding = overrides.statePadding ?: statePadding,
        stateWrappingWidth = overrides.stateWrappingWidth ?: stateWrappingWidth,
        stateMinNodeWidth = overrides.stateMinNodeWidth ?: stateMinNodeWidth,
        stateNodeSpacing = overrides.stateNodeSpacing ?: stateNodeSpacing,
        stateRankSpacing = overrides.stateRankSpacing ?: stateRankSpacing,
        stateTitleTopMargin = overrides.stateTitleTopMargin ?: stateTitleTopMargin,
        erDiagramPadding = overrides.erDiagramPadding ?: erDiagramPadding,
        erEntityPadding = overrides.erEntityPadding ?: erEntityPadding,
        erMinEntityWidth = overrides.erMinEntityWidth ?: erMinEntityWidth,
        erMinEntityHeight = overrides.erMinEntityHeight ?: erMinEntityHeight,
        erNodeSpacing = overrides.erNodeSpacing ?: erNodeSpacing,
        erRankSpacing = overrides.erRankSpacing ?: erRankSpacing,
        erTitleTopMargin = overrides.erTitleTopMargin ?: erTitleTopMargin,
        ganttTitleTopMargin = overrides.ganttTitleTopMargin ?: ganttTitleTopMargin,
        ganttBarHeight = overrides.ganttBarHeight ?: ganttBarHeight,
        ganttBarGap = overrides.ganttBarGap ?: ganttBarGap,
        ganttTopPadding = overrides.ganttTopPadding ?: ganttTopPadding,
        ganttRightPadding = overrides.ganttRightPadding ?: ganttRightPadding,
        ganttLeftPadding = overrides.ganttLeftPadding ?: ganttLeftPadding,
        ganttGridLineStartPadding =
            overrides.ganttGridLineStartPadding ?: ganttGridLineStartPadding,
        ganttFontSize = overrides.ganttFontSize ?: ganttFontSize,
        ganttSectionFontSize = overrides.ganttSectionFontSize ?: ganttSectionFontSize,
        ganttNumberSectionStyles =
            overrides.ganttNumberSectionStyles ?: ganttNumberSectionStyles,
        ganttAxisFormat = overrides.ganttAxisFormat ?: ganttAxisFormat,
        ganttTickInterval = overrides.ganttTickInterval ?: ganttTickInterval,
        ganttTopAxis = overrides.ganttTopAxis ?: ganttTopAxis,
        ganttDisplayMode = overrides.ganttDisplayMode ?: ganttDisplayMode,
        ganttWeekday = overrides.ganttWeekday ?: ganttWeekday,
        ganttUseWidth = overrides.ganttUseWidth ?: ganttUseWidth,
        journey = when {
            overrides.journey != null -> journey?.merge(overrides.journey) ?: overrides.journey
            else -> journey
        },
        timeline = when {
            overrides.timeline != null ->
                timeline?.merge(overrides.timeline) ?: overrides.timeline
            else -> timeline
        },
        pieTextPosition = overrides.pieTextPosition ?: pieTextPosition,
        pieDonutHole = overrides.pieDonutHole ?: pieDonutHole,
        pieLegendPosition = overrides.pieLegendPosition ?: pieLegendPosition,
        pieHighlightSlice = overrides.pieHighlightSlice ?: pieHighlightSlice,
        quadrantChart = when {
            overrides.quadrantChart != null ->
                quadrantChart?.merge(overrides.quadrantChart) ?: overrides.quadrantChart
            else -> quadrantChart
        },
        xyChart = when {
            overrides.xyChart != null -> xyChart?.merge(overrides.xyChart) ?: overrides.xyChart
            else -> xyChart
        },
        curve = overrides.curve ?: curve,
        fontSize = overrides.fontSize ?: fontSize,
        fontFamily = overrides.fontFamily ?: fontFamily,
        themeName = overrides.themeName ?: themeName,
        requirementThemeName = overrides.requirementThemeName ?: requirementThemeName,
        gitGraph = when {
            overrides.gitGraph != null ->
                gitGraph?.merge(overrides.gitGraph) ?: overrides.gitGraph
            else -> gitGraph
        },
        mindmap = when {
            overrides.mindmap != null ->
                mindmap?.merge(overrides.mindmap) ?: overrides.mindmap
            else -> mindmap
        },
        packet = when {
            overrides.packet != null ->
                packet?.merge(overrides.packet) ?: overrides.packet
            else -> packet
        },
        radar = when {
            overrides.radar != null ->
                radar?.merge(overrides.radar) ?: overrides.radar
            else -> radar
        },
        sankey = when {
            overrides.sankey != null ->
                sankey?.merge(overrides.sankey) ?: overrides.sankey
            else -> sankey
        },
        ishikawa = when {
            overrides.ishikawa != null ->
                ishikawa?.merge(overrides.ishikawa) ?: overrides.ishikawa
            else -> ishikawa
        },
        cynefin = when {
            overrides.cynefin != null ->
                cynefin?.merge(overrides.cynefin) ?: overrides.cynefin
            else -> cynefin
        },
        treemap = when {
            overrides.treemap != null ->
                treemap?.merge(overrides.treemap) ?: overrides.treemap
            else -> treemap
        },
        venn = when {
            overrides.venn != null -> venn?.merge(overrides.venn) ?: overrides.venn
            else -> venn
        },
        kanban = when {
            overrides.kanban != null ->
                kanban?.merge(overrides.kanban) ?: overrides.kanban
            else -> kanban
        },
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
        requirementLook = overrides.requirementLook ?: requirementLook,
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
                classNotePadding = classPadding ?: options.classNotePadding,
                classHideEmptyMembersBox =
                    classHideEmptyMembersBox ?: options.classHideEmptyMembersBox,
                classHierarchicalNamespaces =
                    classHierarchicalNamespaces ?: options.classHierarchicalNamespaces,
                statePadding = statePadding ?: options.statePadding,
                stateWrappingWidth = stateWrappingWidth ?: options.stateWrappingWidth,
                stateMinNodeWidth = stateMinNodeWidth ?: options.stateMinNodeWidth,
                stateNodeSpacing = stateNodeSpacing ?: options.stateNodeSpacing,
                stateRankSpacing = stateRankSpacing ?: options.stateRankSpacing,
                stateTitleTopMargin = stateTitleTopMargin ?: options.stateTitleTopMargin,
                erDiagramPadding = erDiagramPadding ?: options.erDiagramPadding,
                erEntityPadding = erEntityPadding ?: options.erEntityPadding,
                erMinEntityWidth = erMinEntityWidth ?: options.erMinEntityWidth,
                erMinEntityHeight = erMinEntityHeight ?: options.erMinEntityHeight,
                erNodeSpacing = erNodeSpacing ?: options.erNodeSpacing,
                erRankSpacing = erRankSpacing ?: options.erRankSpacing,
                erTitleTopMargin = erTitleTopMargin ?: options.erTitleTopMargin,
                ganttTitleTopMargin =
                    ganttTitleTopMargin ?: options.ganttTitleTopMargin,
                ganttBarHeight = ganttBarHeight ?: options.ganttBarHeight,
                ganttBarGap = ganttBarGap ?: options.ganttBarGap,
                ganttTopPadding = ganttTopPadding ?: options.ganttTopPadding,
                ganttRightPadding = ganttRightPadding ?: options.ganttRightPadding,
                ganttLeftPadding = ganttLeftPadding ?: options.ganttLeftPadding,
                ganttGridLineStartPadding =
                    ganttGridLineStartPadding ?: options.ganttGridLineStartPadding,
                ganttFontSize = ganttFontSize ?: options.ganttFontSize,
                ganttSectionFontSize =
                    ganttSectionFontSize ?: options.ganttSectionFontSize,
                ganttNumberSectionStyles =
                    ganttNumberSectionStyles ?: options.ganttNumberSectionStyles,
                ganttAxisFormat = ganttAxisFormat ?: options.ganttAxisFormat,
                ganttTickInterval = ganttTickInterval ?: options.ganttTickInterval,
                ganttTopAxis = ganttTopAxis ?: options.ganttTopAxis,
                ganttDisplayMode = ganttDisplayMode ?: options.ganttDisplayMode,
                ganttWeekday = ganttWeekday ?: options.ganttWeekday,
                ganttUseWidth = ganttUseWidth ?: options.ganttUseWidth,
                journey = journey?.applyTo(options.journey) ?: options.journey,
                timeline = timeline?.applyTo(options.timeline) ?: options.timeline,
                pieTextPosition = pieTextPosition ?: options.pieTextPosition,
                pieDonutHole = pieDonutHole ?: options.pieDonutHole,
                pieLegendPosition = pieLegendPosition ?: options.pieLegendPosition,
                pieHighlightSlice = pieHighlightSlice ?: options.pieHighlightSlice,
                quadrantChart =
                    quadrantChart?.applyTo(options.quadrantChart) ?: options.quadrantChart,
                xyChart = xyChart?.applyTo(options.xyChart) ?: options.xyChart,
                curve = curve ?: options.curve,
                fontSize = fontSize ?: options.fontSize,
                fontFamily = fontFamily ?: options.fontFamily,
                themeName = themeName ?: options.themeName,
                requirementThemeName =
                    requirementThemeName ?: options.requirementThemeName,
                gitGraph = gitGraph?.applyTo(options.gitGraph) ?: options.gitGraph,
                mindmap = mindmap?.applyTo(options.mindmap) ?: options.mindmap,
                packet = packet?.applyTo(options.packet) ?: options.packet,
                radar = radar?.applyTo(options.radar) ?: options.radar,
                sankey = sankey?.applyTo(options.sankey) ?: options.sankey,
                ishikawa = ishikawa?.applyTo(options.ishikawa) ?: options.ishikawa,
                cynefin = cynefin?.applyTo(options.cynefin) ?: options.cynefin,
                treemap = treemap?.applyTo(options.treemap) ?: options.treemap,
                venn = venn?.applyTo(options.venn) ?: options.venn,
                kanban = kanban?.applyTo(options.kanban) ?: options.kanban,
                themeVariables = options.themeVariables + themeVariables.orEmpty(),
                themeColorArrays = options.themeColorArrays + themeColorArrays.orEmpty(),
                look = resolvedLook,
                requirementLook = requirementLook ?: options.requirementLook,
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

internal data class MermaidPacketConfigOverride(
    val rowHeight: Float? = null,
    val bitWidth: Float? = null,
    val bitsPerRow: Int? = null,
    val showBits: Boolean? = null,
    val paddingX: Float? = null,
    val paddingY: Float? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(overrides: MermaidPacketConfigOverride): MermaidPacketConfigOverride =
        MermaidPacketConfigOverride(
            rowHeight = overrides.rowHeight ?: rowHeight,
            bitWidth = overrides.bitWidth ?: bitWidth,
            bitsPerRow = overrides.bitsPerRow ?: bitsPerRow,
            showBits = overrides.showBits ?: showBits,
            paddingX = overrides.paddingX ?: paddingX,
            paddingY = overrides.paddingY ?: paddingY,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
        )

    fun applyTo(options: MermaidPacketOptions): MermaidPacketOptions = options.copy(
        rowHeight = rowHeight ?: options.rowHeight,
        bitWidth = bitWidth ?: options.bitWidth,
        bitsPerRow = bitsPerRow ?: options.bitsPerRow,
        showBits = showBits ?: options.showBits,
        paddingX = paddingX ?: options.paddingX,
        paddingY = paddingY ?: options.paddingY,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
    )
}

internal data class MermaidRadarConfigOverride(
    val width: Float? = null,
    val height: Float? = null,
    val marginTop: Float? = null,
    val marginRight: Float? = null,
    val marginBottom: Float? = null,
    val marginLeft: Float? = null,
    val axisScaleFactor: Float? = null,
    val axisLabelFactor: Float? = null,
    val curveTension: Float? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(overrides: MermaidRadarConfigOverride): MermaidRadarConfigOverride =
        MermaidRadarConfigOverride(
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            marginTop = overrides.marginTop ?: marginTop,
            marginRight = overrides.marginRight ?: marginRight,
            marginBottom = overrides.marginBottom ?: marginBottom,
            marginLeft = overrides.marginLeft ?: marginLeft,
            axisScaleFactor = overrides.axisScaleFactor ?: axisScaleFactor,
            axisLabelFactor = overrides.axisLabelFactor ?: axisLabelFactor,
            curveTension = overrides.curveTension ?: curveTension,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
        )

    fun applyTo(options: MermaidRadarOptions): MermaidRadarOptions = options.copy(
        width = width ?: options.width,
        height = height ?: options.height,
        marginTop = marginTop ?: options.marginTop,
        marginRight = marginRight ?: options.marginRight,
        marginBottom = marginBottom ?: options.marginBottom,
        marginLeft = marginLeft ?: options.marginLeft,
        axisScaleFactor = axisScaleFactor ?: options.axisScaleFactor,
        axisLabelFactor = axisLabelFactor ?: options.axisLabelFactor,
        curveTension = curveTension ?: options.curveTension,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
    )
}

internal data class MermaidSankeyConfigOverride(
    val width: Float? = null,
    val height: Float? = null,
    val linkColor: String? = null,
    val nodeAlignment: String? = null,
    val useMaxWidth: Boolean? = null,
    val showValues: Boolean? = null,
    val prefix: String? = null,
    val suffix: String? = null,
    val nodeWidth: Float? = null,
    val nodePadding: Float? = null,
    val labelStyle: String? = null,
    val nodeColors: Map<String, SceneColor>? = null,
) {
    fun merge(overrides: MermaidSankeyConfigOverride): MermaidSankeyConfigOverride =
        MermaidSankeyConfigOverride(
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            linkColor = overrides.linkColor ?: linkColor,
            nodeAlignment = overrides.nodeAlignment ?: nodeAlignment,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
            showValues = overrides.showValues ?: showValues,
            prefix = overrides.prefix ?: prefix,
            suffix = overrides.suffix ?: suffix,
            nodeWidth = overrides.nodeWidth ?: nodeWidth,
            nodePadding = overrides.nodePadding ?: nodePadding,
            labelStyle = overrides.labelStyle ?: labelStyle,
            nodeColors = when {
                overrides.nodeColors != null -> nodeColors.orEmpty() + overrides.nodeColors
                else -> nodeColors
            },
        )

    fun applyTo(options: MermaidSankeyOptions): MermaidSankeyOptions = options.copy(
        width = width ?: options.width,
        height = height ?: options.height,
        linkColor = linkColor ?: options.linkColor,
        nodeAlignment = nodeAlignment ?: options.nodeAlignment,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
        showValues = showValues ?: options.showValues,
        prefix = prefix ?: options.prefix,
        suffix = suffix ?: options.suffix,
        nodeWidth = nodeWidth ?: options.nodeWidth,
        nodePadding = nodePadding ?: options.nodePadding,
        labelStyle = labelStyle ?: options.labelStyle,
        nodeColors = options.nodeColors + nodeColors.orEmpty(),
    )
}

internal data class MermaidIshikawaConfigOverride(
    val diagramPadding: Float? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(overrides: MermaidIshikawaConfigOverride): MermaidIshikawaConfigOverride =
        MermaidIshikawaConfigOverride(
            diagramPadding = overrides.diagramPadding ?: diagramPadding,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
        )

    fun applyTo(options: MermaidIshikawaOptions): MermaidIshikawaOptions = options.copy(
        diagramPadding = diagramPadding ?: options.diagramPadding,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
    )
}

internal data class MermaidCynefinConfigOverride(
    val width: Float? = null,
    val height: Float? = null,
    val padding: Float? = null,
    val showDomainDescriptions: Boolean? = null,
    val boundaryAmplitude: Float? = null,
    val seed: Float? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(overrides: MermaidCynefinConfigOverride): MermaidCynefinConfigOverride =
        MermaidCynefinConfigOverride(
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            padding = overrides.padding ?: padding,
            showDomainDescriptions =
                overrides.showDomainDescriptions ?: showDomainDescriptions,
            boundaryAmplitude = overrides.boundaryAmplitude ?: boundaryAmplitude,
            seed = overrides.seed ?: seed,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
        )

    fun applyTo(options: MermaidCynefinOptions): MermaidCynefinOptions = options.copy(
        width = width ?: options.width,
        height = height ?: options.height,
        padding = padding ?: options.padding,
        showDomainDescriptions =
            showDomainDescriptions ?: options.showDomainDescriptions,
        boundaryAmplitude = boundaryAmplitude ?: options.boundaryAmplitude,
        seed = seed ?: options.seed,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
    )
}

internal data class MermaidTreemapConfigOverride(
    val useMaxWidth: Boolean? = null,
    val padding: Float? = null,
    val diagramPadding: Float? = null,
    val showValues: Boolean? = null,
    val nodeWidth: Float? = null,
    val nodeHeight: Float? = null,
    val borderWidth: Float? = null,
    val valueFontSize: Float? = null,
    val labelFontSize: Float? = null,
    val valueFormat: String? = null,
) {
    fun merge(overrides: MermaidTreemapConfigOverride): MermaidTreemapConfigOverride =
        MermaidTreemapConfigOverride(
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
            padding = overrides.padding ?: padding,
            diagramPadding = overrides.diagramPadding ?: diagramPadding,
            showValues = overrides.showValues ?: showValues,
            nodeWidth = overrides.nodeWidth ?: nodeWidth,
            nodeHeight = overrides.nodeHeight ?: nodeHeight,
            borderWidth = overrides.borderWidth ?: borderWidth,
            valueFontSize = overrides.valueFontSize ?: valueFontSize,
            labelFontSize = overrides.labelFontSize ?: labelFontSize,
            valueFormat = overrides.valueFormat ?: valueFormat,
        )

    fun applyTo(options: MermaidTreemapOptions): MermaidTreemapOptions = options.copy(
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
        padding = padding ?: options.padding,
        diagramPadding = diagramPadding ?: options.diagramPadding,
        showValues = showValues ?: options.showValues,
        nodeWidth = nodeWidth ?: options.nodeWidth,
        nodeHeight = nodeHeight ?: options.nodeHeight,
        borderWidth = borderWidth ?: options.borderWidth,
        valueFontSize = valueFontSize ?: options.valueFontSize,
        labelFontSize = labelFontSize ?: options.labelFontSize,
        valueFormat = valueFormat ?: options.valueFormat,
    )
}

internal data class MermaidVennConfigOverride(
    val width: Float? = null,
    val height: Float? = null,
    val padding: Float? = null,
    val useDebugLayout: Boolean? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(overrides: MermaidVennConfigOverride): MermaidVennConfigOverride =
        MermaidVennConfigOverride(
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            padding = overrides.padding ?: padding,
            useDebugLayout = overrides.useDebugLayout ?: useDebugLayout,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
        )

    fun applyTo(options: MermaidVennOptions): MermaidVennOptions = options.copy(
        width = width ?: options.width,
        height = height ?: options.height,
        padding = padding ?: options.padding,
        useDebugLayout = useDebugLayout ?: options.useDebugLayout,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
    )
}

internal data class MermaidMindmapConfigOverride(
    val padding: Float? = null,
    val maxNodeWidth: Float? = null,
    val useMaxWidth: Boolean? = null,
    val layoutAlgorithm: String? = null,
) {
    fun merge(overrides: MermaidMindmapConfigOverride): MermaidMindmapConfigOverride =
        MermaidMindmapConfigOverride(
            padding = overrides.padding ?: padding,
            maxNodeWidth = overrides.maxNodeWidth ?: maxNodeWidth,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
            layoutAlgorithm = overrides.layoutAlgorithm ?: layoutAlgorithm,
        )

    fun applyTo(options: MermaidMindmapOptions): MermaidMindmapOptions = options.copy(
        padding = padding ?: options.padding,
        maxNodeWidth = maxNodeWidth ?: options.maxNodeWidth,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
        layoutAlgorithm = layoutAlgorithm ?: options.layoutAlgorithm,
    )
}

internal data class MermaidKanbanConfigOverride(
    val padding: Float? = null,
    val sectionWidth: Float? = null,
    val ticketBaseUrl: String? = null,
) {
    fun merge(overrides: MermaidKanbanConfigOverride): MermaidKanbanConfigOverride =
        MermaidKanbanConfigOverride(
            padding = overrides.padding ?: padding,
            sectionWidth = overrides.sectionWidth ?: sectionWidth,
            ticketBaseUrl = overrides.ticketBaseUrl ?: ticketBaseUrl,
        )

    fun applyTo(options: MermaidKanbanOptions): MermaidKanbanOptions = options.copy(
        padding = padding ?: options.padding,
        sectionWidth = sectionWidth ?: options.sectionWidth,
        ticketBaseUrl = ticketBaseUrl ?: options.ticketBaseUrl,
    )
}

internal data class MermaidGitGraphConfigOverride(
    val titleTopMargin: Float? = null,
    val diagramPadding: Float? = null,
    val mainBranchName: String? = null,
    val mainBranchOrder: Float? = null,
    val showCommitLabel: Boolean? = null,
    val showBranches: Boolean? = null,
    val rotateCommitLabel: Boolean? = null,
    val parallelCommits: Boolean? = null,
) {
    fun merge(overrides: MermaidGitGraphConfigOverride): MermaidGitGraphConfigOverride =
        MermaidGitGraphConfigOverride(
            titleTopMargin = overrides.titleTopMargin ?: titleTopMargin,
            diagramPadding = overrides.diagramPadding ?: diagramPadding,
            mainBranchName = overrides.mainBranchName ?: mainBranchName,
            mainBranchOrder = overrides.mainBranchOrder ?: mainBranchOrder,
            showCommitLabel = overrides.showCommitLabel ?: showCommitLabel,
            showBranches = overrides.showBranches ?: showBranches,
            rotateCommitLabel = overrides.rotateCommitLabel ?: rotateCommitLabel,
            parallelCommits = overrides.parallelCommits ?: parallelCommits,
        )

    fun applyTo(options: MermaidGitGraphOptions): MermaidGitGraphOptions = options.copy(
        titleTopMargin = titleTopMargin ?: options.titleTopMargin,
        diagramPadding = diagramPadding ?: options.diagramPadding,
        mainBranchName = mainBranchName ?: options.mainBranchName,
        mainBranchOrder = mainBranchOrder ?: options.mainBranchOrder,
        showCommitLabel = showCommitLabel ?: options.showCommitLabel,
        showBranches = showBranches ?: options.showBranches,
        rotateCommitLabel = rotateCommitLabel ?: options.rotateCommitLabel,
        parallelCommits = parallelCommits ?: options.parallelCommits,
    )
}

internal data class MermaidJourneyConfigOverride(
    val diagramMarginX: Float? = null,
    val diagramMarginY: Float? = null,
    val leftMargin: Float? = null,
    val maxLabelWidth: Float? = null,
    val width: Float? = null,
    val height: Float? = null,
    val boxMargin: Float? = null,
    val boxTextMargin: Float? = null,
    val noteMargin: Float? = null,
    val messageMargin: Float? = null,
    val messageAlign: String? = null,
    val bottomMarginAdj: Float? = null,
    val rightAngles: Boolean? = null,
    val taskFontSize: Float? = null,
    val taskFontFamily: String? = null,
    val taskMargin: Float? = null,
    val activationWidth: Float? = null,
    val textPlacement: String? = null,
    val actorColours: List<SceneColor>? = null,
    val sectionFills: List<SceneColor>? = null,
    val sectionColours: List<SceneColor>? = null,
    val titleColor: SceneColor? = null,
    val titleFontFamily: String? = null,
    val titleFontSize: String? = null,
) {
    fun merge(overrides: MermaidJourneyConfigOverride): MermaidJourneyConfigOverride =
        MermaidJourneyConfigOverride(
            diagramMarginX = overrides.diagramMarginX ?: diagramMarginX,
            diagramMarginY = overrides.diagramMarginY ?: diagramMarginY,
            leftMargin = overrides.leftMargin ?: leftMargin,
            maxLabelWidth = overrides.maxLabelWidth ?: maxLabelWidth,
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            boxMargin = overrides.boxMargin ?: boxMargin,
            boxTextMargin = overrides.boxTextMargin ?: boxTextMargin,
            noteMargin = overrides.noteMargin ?: noteMargin,
            messageMargin = overrides.messageMargin ?: messageMargin,
            messageAlign = overrides.messageAlign ?: messageAlign,
            bottomMarginAdj = overrides.bottomMarginAdj ?: bottomMarginAdj,
            rightAngles = overrides.rightAngles ?: rightAngles,
            taskFontSize = overrides.taskFontSize ?: taskFontSize,
            taskFontFamily = overrides.taskFontFamily ?: taskFontFamily,
            taskMargin = overrides.taskMargin ?: taskMargin,
            activationWidth = overrides.activationWidth ?: activationWidth,
            textPlacement = overrides.textPlacement ?: textPlacement,
            actorColours = overrides.actorColours ?: actorColours,
            sectionFills = overrides.sectionFills ?: sectionFills,
            sectionColours = overrides.sectionColours ?: sectionColours,
            titleColor = overrides.titleColor ?: titleColor,
            titleFontFamily = overrides.titleFontFamily ?: titleFontFamily,
            titleFontSize = overrides.titleFontSize ?: titleFontSize,
        )

    fun applyTo(options: MermaidJourneyOptions): MermaidJourneyOptions = options.copy(
        diagramMarginX = diagramMarginX ?: options.diagramMarginX,
        diagramMarginY = diagramMarginY ?: options.diagramMarginY,
        leftMargin = leftMargin ?: options.leftMargin,
        maxLabelWidth = maxLabelWidth ?: options.maxLabelWidth,
        width = width ?: options.width,
        height = height ?: options.height,
        boxMargin = boxMargin ?: options.boxMargin,
        boxTextMargin = boxTextMargin ?: options.boxTextMargin,
        noteMargin = noteMargin ?: options.noteMargin,
        messageMargin = messageMargin ?: options.messageMargin,
        messageAlign = messageAlign ?: options.messageAlign,
        bottomMarginAdj = bottomMarginAdj ?: options.bottomMarginAdj,
        rightAngles = rightAngles ?: options.rightAngles,
        taskFontSize = taskFontSize ?: options.taskFontSize,
        taskFontFamily = taskFontFamily ?: options.taskFontFamily,
        taskMargin = taskMargin ?: options.taskMargin,
        activationWidth = activationWidth ?: options.activationWidth,
        textPlacement = textPlacement ?: options.textPlacement,
        actorColours = actorColours ?: options.actorColours,
        sectionFills = sectionFills ?: options.sectionFills,
        sectionColours = sectionColours ?: options.sectionColours,
        titleColor = titleColor ?: options.titleColor,
        titleFontFamily = titleFontFamily ?: options.titleFontFamily,
        titleFontSize = titleFontSize ?: options.titleFontSize,
    )
}

internal data class MermaidTimelineConfigOverride(
    val useWidth: Float? = null,
    val useMaxWidth: Boolean? = null,
    val theme: String? = null,
    val look: String? = null,
    val layout: String? = null,
    val diagramMarginX: Float? = null,
    val diagramMarginY: Float? = null,
    val leftMargin: Float? = null,
    val width: Float? = null,
    val height: Float? = null,
    val padding: Float? = null,
    val boxMargin: Float? = null,
    val boxTextMargin: Float? = null,
    val noteMargin: Float? = null,
    val messageMargin: Float? = null,
    val messageAlign: String? = null,
    val bottomMarginAdj: Float? = null,
    val rightAngles: Boolean? = null,
    val taskFontSize: Float? = null,
    val taskFontFamily: String? = null,
    val taskMargin: Float? = null,
    val activationWidth: Float? = null,
    val textPlacement: String? = null,
    val actorColours: List<SceneColor>? = null,
    val sectionFills: List<SceneColor>? = null,
    val sectionColours: List<SceneColor>? = null,
    val disableMulticolor: Boolean? = null,
) {
    fun merge(overrides: MermaidTimelineConfigOverride): MermaidTimelineConfigOverride =
        MermaidTimelineConfigOverride(
            useWidth = overrides.useWidth ?: useWidth,
            useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
            theme = overrides.theme ?: theme,
            look = overrides.look ?: look,
            layout = overrides.layout ?: layout,
            diagramMarginX = overrides.diagramMarginX ?: diagramMarginX,
            diagramMarginY = overrides.diagramMarginY ?: diagramMarginY,
            leftMargin = overrides.leftMargin ?: leftMargin,
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            padding = overrides.padding ?: padding,
            boxMargin = overrides.boxMargin ?: boxMargin,
            boxTextMargin = overrides.boxTextMargin ?: boxTextMargin,
            noteMargin = overrides.noteMargin ?: noteMargin,
            messageMargin = overrides.messageMargin ?: messageMargin,
            messageAlign = overrides.messageAlign ?: messageAlign,
            bottomMarginAdj = overrides.bottomMarginAdj ?: bottomMarginAdj,
            rightAngles = overrides.rightAngles ?: rightAngles,
            taskFontSize = overrides.taskFontSize ?: taskFontSize,
            taskFontFamily = overrides.taskFontFamily ?: taskFontFamily,
            taskMargin = overrides.taskMargin ?: taskMargin,
            activationWidth = overrides.activationWidth ?: activationWidth,
            textPlacement = overrides.textPlacement ?: textPlacement,
            actorColours = overrides.actorColours ?: actorColours,
            sectionFills = overrides.sectionFills ?: sectionFills,
            sectionColours = overrides.sectionColours ?: sectionColours,
            disableMulticolor = overrides.disableMulticolor ?: disableMulticolor,
        )

    fun applyTo(options: MermaidTimelineOptions): MermaidTimelineOptions = options.copy(
        useWidth = useWidth ?: options.useWidth,
        useMaxWidth = useMaxWidth ?: options.useMaxWidth,
        theme = theme ?: options.theme,
        look = look ?: options.look,
        layout = layout ?: options.layout,
        diagramMarginX = diagramMarginX ?: options.diagramMarginX,
        diagramMarginY = diagramMarginY ?: options.diagramMarginY,
        leftMargin = leftMargin ?: options.leftMargin,
        width = width ?: options.width,
        height = height ?: options.height,
        padding = padding ?: options.padding,
        boxMargin = boxMargin ?: options.boxMargin,
        boxTextMargin = boxTextMargin ?: options.boxTextMargin,
        noteMargin = noteMargin ?: options.noteMargin,
        messageMargin = messageMargin ?: options.messageMargin,
        messageAlign = messageAlign ?: options.messageAlign,
        bottomMarginAdj = bottomMarginAdj ?: options.bottomMarginAdj,
        rightAngles = rightAngles ?: options.rightAngles,
        taskFontSize = taskFontSize ?: options.taskFontSize,
        taskFontFamily = taskFontFamily ?: options.taskFontFamily,
        taskMargin = taskMargin ?: options.taskMargin,
        activationWidth = activationWidth ?: options.activationWidth,
        textPlacement = textPlacement ?: options.textPlacement,
        actorColours = actorColours ?: options.actorColours,
        sectionFills = sectionFills ?: options.sectionFills,
        sectionColours = sectionColours ?: options.sectionColours,
        disableMulticolor = disableMulticolor ?: options.disableMulticolor,
    )
}

internal data class MermaidQuadrantChartConfigOverride(
    val chartWidth: Float? = null,
    val chartHeight: Float? = null,
    val titleFontSize: Float? = null,
    val titlePadding: Float? = null,
    val quadrantPadding: Float? = null,
    val xAxisLabelPadding: Float? = null,
    val yAxisLabelPadding: Float? = null,
    val xAxisLabelFontSize: Float? = null,
    val yAxisLabelFontSize: Float? = null,
    val quadrantLabelFontSize: Float? = null,
    val quadrantTextTopPadding: Float? = null,
    val pointTextPadding: Float? = null,
    val pointLabelFontSize: Float? = null,
    val pointRadius: Float? = null,
    val xAxisPosition: String? = null,
    val yAxisPosition: String? = null,
    val quadrantInternalBorderStrokeWidth: Float? = null,
    val quadrantExternalBorderStrokeWidth: Float? = null,
    val useMaxWidth: Boolean? = null,
) {
    fun merge(
        overrides: MermaidQuadrantChartConfigOverride,
    ): MermaidQuadrantChartConfigOverride = MermaidQuadrantChartConfigOverride(
        chartWidth = overrides.chartWidth ?: chartWidth,
        chartHeight = overrides.chartHeight ?: chartHeight,
        titleFontSize = overrides.titleFontSize ?: titleFontSize,
        titlePadding = overrides.titlePadding ?: titlePadding,
        quadrantPadding = overrides.quadrantPadding ?: quadrantPadding,
        xAxisLabelPadding = overrides.xAxisLabelPadding ?: xAxisLabelPadding,
        yAxisLabelPadding = overrides.yAxisLabelPadding ?: yAxisLabelPadding,
        xAxisLabelFontSize = overrides.xAxisLabelFontSize ?: xAxisLabelFontSize,
        yAxisLabelFontSize = overrides.yAxisLabelFontSize ?: yAxisLabelFontSize,
        quadrantLabelFontSize = overrides.quadrantLabelFontSize ?: quadrantLabelFontSize,
        quadrantTextTopPadding =
            overrides.quadrantTextTopPadding ?: quadrantTextTopPadding,
        pointTextPadding = overrides.pointTextPadding ?: pointTextPadding,
        pointLabelFontSize = overrides.pointLabelFontSize ?: pointLabelFontSize,
        pointRadius = overrides.pointRadius ?: pointRadius,
        xAxisPosition = overrides.xAxisPosition ?: xAxisPosition,
        yAxisPosition = overrides.yAxisPosition ?: yAxisPosition,
        quadrantInternalBorderStrokeWidth =
            overrides.quadrantInternalBorderStrokeWidth ?: quadrantInternalBorderStrokeWidth,
        quadrantExternalBorderStrokeWidth =
            overrides.quadrantExternalBorderStrokeWidth ?: quadrantExternalBorderStrokeWidth,
        useMaxWidth = overrides.useMaxWidth ?: useMaxWidth,
    )

    fun applyTo(options: MermaidQuadrantChartOptions): MermaidQuadrantChartOptions =
        options.copy(
            chartWidth = chartWidth ?: options.chartWidth,
            chartHeight = chartHeight ?: options.chartHeight,
            titleFontSize = titleFontSize ?: options.titleFontSize,
            titlePadding = titlePadding ?: options.titlePadding,
            quadrantPadding = quadrantPadding ?: options.quadrantPadding,
            xAxisLabelPadding = xAxisLabelPadding ?: options.xAxisLabelPadding,
            yAxisLabelPadding = yAxisLabelPadding ?: options.yAxisLabelPadding,
            xAxisLabelFontSize = xAxisLabelFontSize ?: options.xAxisLabelFontSize,
            yAxisLabelFontSize = yAxisLabelFontSize ?: options.yAxisLabelFontSize,
            quadrantLabelFontSize =
                quadrantLabelFontSize ?: options.quadrantLabelFontSize,
            quadrantTextTopPadding =
                quadrantTextTopPadding ?: options.quadrantTextTopPadding,
            pointTextPadding = pointTextPadding ?: options.pointTextPadding,
            pointLabelFontSize = pointLabelFontSize ?: options.pointLabelFontSize,
            pointRadius = pointRadius ?: options.pointRadius,
            xAxisPosition = xAxisPosition ?: options.xAxisPosition,
            yAxisPosition = yAxisPosition ?: options.yAxisPosition,
            quadrantInternalBorderStrokeWidth =
                quadrantInternalBorderStrokeWidth
                    ?: options.quadrantInternalBorderStrokeWidth,
            quadrantExternalBorderStrokeWidth =
                quadrantExternalBorderStrokeWidth
                    ?: options.quadrantExternalBorderStrokeWidth,
            useMaxWidth = useMaxWidth ?: options.useMaxWidth,
        )
}

internal data class MermaidXyChartConfigOverride(
    val width: Float? = null,
    val height: Float? = null,
    val titleFontSize: Float? = null,
    val titlePadding: Float? = null,
    val showTitle: Boolean? = null,
    val showLegend: Boolean? = null,
    val legendFontSize: Float? = null,
    val legendPadding: Float? = null,
    val showDataLabel: Boolean? = null,
    val showDataLabelOutsideBar: Boolean? = null,
    val chartOrientation: String? = null,
    val plotReservedSpacePercent: Float? = null,
    val xAxis: MermaidXyAxisConfigOverride? = null,
    val yAxis: MermaidXyAxisConfigOverride? = null,
) {
    fun merge(overrides: MermaidXyChartConfigOverride): MermaidXyChartConfigOverride =
        MermaidXyChartConfigOverride(
            width = overrides.width ?: width,
            height = overrides.height ?: height,
            titleFontSize = overrides.titleFontSize ?: titleFontSize,
            titlePadding = overrides.titlePadding ?: titlePadding,
            showTitle = overrides.showTitle ?: showTitle,
            showLegend = overrides.showLegend ?: showLegend,
            legendFontSize = overrides.legendFontSize ?: legendFontSize,
            legendPadding = overrides.legendPadding ?: legendPadding,
            showDataLabel = overrides.showDataLabel ?: showDataLabel,
            showDataLabelOutsideBar =
                overrides.showDataLabelOutsideBar ?: showDataLabelOutsideBar,
            chartOrientation = overrides.chartOrientation ?: chartOrientation,
            plotReservedSpacePercent =
                overrides.plotReservedSpacePercent ?: plotReservedSpacePercent,
            xAxis = when {
                overrides.xAxis != null -> xAxis?.merge(overrides.xAxis) ?: overrides.xAxis
                else -> xAxis
            },
            yAxis = when {
                overrides.yAxis != null -> yAxis?.merge(overrides.yAxis) ?: overrides.yAxis
                else -> yAxis
            },
        )

    fun applyTo(options: MermaidXyChartOptions): MermaidXyChartOptions = options.copy(
        width = width ?: options.width,
        height = height ?: options.height,
        titleFontSize = titleFontSize ?: options.titleFontSize,
        titlePadding = titlePadding ?: options.titlePadding,
        showTitle = showTitle ?: options.showTitle,
        showLegend = showLegend ?: options.showLegend,
        legendFontSize = legendFontSize ?: options.legendFontSize,
        legendPadding = legendPadding ?: options.legendPadding,
        showDataLabel = showDataLabel ?: options.showDataLabel,
        showDataLabelOutsideBar =
            showDataLabelOutsideBar ?: options.showDataLabelOutsideBar,
        chartOrientation = chartOrientation ?: options.chartOrientation,
        plotReservedSpacePercent =
            plotReservedSpacePercent ?: options.plotReservedSpacePercent,
        xAxis = xAxis?.applyTo(options.xAxis) ?: options.xAxis,
        yAxis = yAxis?.applyTo(options.yAxis) ?: options.yAxis,
    )
}

internal data class MermaidXyAxisConfigOverride(
    val showLabel: Boolean? = null,
    val labelFontSize: Float? = null,
    val labelPadding: Float? = null,
    val showTitle: Boolean? = null,
    val titleFontSize: Float? = null,
    val titlePadding: Float? = null,
    val showTick: Boolean? = null,
    val tickLength: Float? = null,
    val tickWidth: Float? = null,
    val showAxisLine: Boolean? = null,
    val axisLineWidth: Float? = null,
    val labelRotation: Float? = null,
) {
    fun merge(overrides: MermaidXyAxisConfigOverride): MermaidXyAxisConfigOverride =
        MermaidXyAxisConfigOverride(
            showLabel = overrides.showLabel ?: showLabel,
            labelFontSize = overrides.labelFontSize ?: labelFontSize,
            labelPadding = overrides.labelPadding ?: labelPadding,
            showTitle = overrides.showTitle ?: showTitle,
            titleFontSize = overrides.titleFontSize ?: titleFontSize,
            titlePadding = overrides.titlePadding ?: titlePadding,
            showTick = overrides.showTick ?: showTick,
            tickLength = overrides.tickLength ?: tickLength,
            tickWidth = overrides.tickWidth ?: tickWidth,
            showAxisLine = overrides.showAxisLine ?: showAxisLine,
            axisLineWidth = overrides.axisLineWidth ?: axisLineWidth,
            labelRotation = overrides.labelRotation ?: labelRotation,
        )

    fun applyTo(options: MermaidXyAxisOptions): MermaidXyAxisOptions = options.copy(
        showLabel = showLabel ?: options.showLabel,
        labelFontSize = labelFontSize ?: options.labelFontSize,
        labelPadding = labelPadding ?: options.labelPadding,
        showTitle = showTitle ?: options.showTitle,
        titleFontSize = titleFontSize ?: options.titleFontSize,
        titlePadding = titlePadding ?: options.titlePadding,
        showTick = showTick ?: options.showTick,
        tickLength = tickLength ?: options.tickLength,
        tickWidth = tickWidth ?: options.tickWidth,
        showAxisLine = showAxisLine ?: options.showAxisLine,
        axisLineWidth = axisLineWidth ?: options.axisLineWidth,
        labelRotation = labelRotation ?: options.labelRotation,
    )
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
