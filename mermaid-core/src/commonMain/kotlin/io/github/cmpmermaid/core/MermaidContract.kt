package io.github.cmpmermaid.core

private const val MERMAID_CLASSIC_FONT_FAMILY =
    "\"trebuchet ms\", verdana, arial, sans-serif"
private const val MERMAID_REDUX_FONT_FAMILY =
    "\"Recursive Variable\", arial, sans-serif"
private const val MERMAID_NEO_FONT_FAMILY = "arial, sans-serif"

object MermaidCompatibility {
    const val BASELINE_VERSION: String = "12.0.0"
}

sealed interface MermaidError {
    val message: String

    data class UnsupportedDiagram(
        val header: String,
    ) : MermaidError {
        override val message: String = "Unsupported Mermaid diagram: $header"
    }

    data class Parse(
        val line: Int,
        val column: Int,
        override val message: String,
    ) : MermaidError

    data class Layout(
        override val message: String,
    ) : MermaidError

    data class Configuration(
        override val message: String,
    ) : MermaidError

    data class ResourceLimit(
        val resource: String,
        val actual: Int,
        val maximum: Int,
        override val message: String =
            "Mermaid $resource limit exceeded: $actual > $maximum",
    ) : MermaidError

    data class UnsupportedFeature(
        val feature: String,
        override val message: String = "Unsupported Mermaid feature: $feature",
    ) : MermaidError
}

data class MermaidRenderOptions(
    val layout: String = "elk",
    val classLayout: String? = null,
    val stateLayout: String? = null,
    val elk: MermaidElkOptions = MermaidElkOptions(),
    val nodeSpacing: Float = 50f,
    val rankSpacing: Float = 50f,
    val diagramPadding: Float = 8f,
    val wrappingWidth: Float = 120f,
    val minNodeWidth: Float = 120f,
    val flowchartPadding: Float = 15f,
    val classPadding: Float = 12f,
    val classHideEmptyMembersBox: Boolean = false,
    val classHierarchicalNamespaces: Boolean = true,
    val statePadding: Float = 8f,
    val stateWrappingWidth: Float = 120f,
    val stateMinNodeWidth: Float = 120f,
    val stateNodeSpacing: Float? = null,
    val stateRankSpacing: Float? = null,
    val stateTitleTopMargin: Float = 25f,
    val erDiagramPadding: Float = 20f,
    val erEntityPadding: Float = 15f,
    val erMinEntityWidth: Float = 100f,
    val erMinEntityHeight: Float = 75f,
    val erNodeSpacing: Float = 140f,
    val erRankSpacing: Float = 80f,
    val erTitleTopMargin: Float = 25f,
    val curve: String = "basis",
    val fontSize: Float? = null,
    val fontFamily: String? = null,
    val look: String = "neo",
    val titleTopMargin: Float = 25f,
    val subGraphTitleTopMargin: Float = 0f,
    val subGraphTitleBottomMargin: Float = 0f,
    val inheritDirection: Boolean = false,
    val htmlLabels: Boolean = true,
    val markdownAutoWrap: Boolean = true,
    val securityLevel: MermaidSecurityLevel = MermaidSecurityLevel.Strict,
    val maxTextSize: Int = 50_000,
    val maxEdges: Int = 500,
    val themeName: String? = null,
    val themeVariables: Map<String, String> = emptyMap(),
    val themeColorArrays: Map<String, List<String>> = emptyMap(),
)

internal val MermaidRenderOptions.subGraphTitleTotalMargin: Float
    get() = subGraphTitleTopMargin + subGraphTitleBottomMargin

data class MermaidElkOptions(
    val mergeEdges: Boolean = false,
    val nodePlacementStrategy: String? = null,
    val nodePlacementAlignment: String? = null,
    val preset: String = "default",
    val straightenEdges: Boolean = true,
    val lineHops: MermaidElkLineHops = MermaidElkLineHops.Arc,
    val layeringStrategy: String? = null,
    val layeringLayerBound: Int = 4,
    val cycleBreakingStrategy: String? = null,
    val forceNodeModelOrder: Boolean = false,
    val considerModelOrder: String = "NODES_AND_EDGES",
    val keepEntryNodeOnTop: Boolean = false,
)

enum class MermaidElkLineHops {
    Disabled,
    Arc,
    Gap,
}

enum class MermaidSecurityLevel {
    Strict,
    Loose,
    Antiscript,
    Sandbox,
}

data class MermaidTheme(
    val background: SceneColor = SceneColor(0xFFFFFFFF),
    val nodeFill: SceneColor = SceneColor(0xFFECECFF),
    val nodeStroke: SceneColor = SceneColor(0xFF9370DB),
    val nodeText: SceneColor = SceneColor(0xFF333333),
    val edge: SceneColor = SceneColor(0xFF333333),
    val edgeLabelFill: SceneColor = SceneColor(0xCCE8E8E8),
    val groupFill: SceneColor = SceneColor(0xFFFFFFDE),
    val groupStroke: SceneColor = SceneColor(0xFF9370DB),
    val groupText: SceneColor = SceneColor(0xFF333333),
    val fontSize: Float = 16f,
    val fontFamily: String = MERMAID_CLASSIC_FONT_FAMILY,
    val strokeWidth: Float = 1f,
    val bkgColorArray: List<SceneColor> = emptyList(),
    val borderColorArray: List<SceneColor> = emptyList(),
    val dropShadow: SceneShadow? = SceneShadow(
        color = SceneColor(0xFFB9B9B9),
        offsetX = 1f,
        offsetY = 2f,
        blurRadius = 2f,
    ),
) {
    internal fun colorFill(index: Int?): SceneColor =
        paletteColor(bkgColorArray, index) ?: groupFill

    internal fun colorStroke(index: Int?): SceneColor =
        paletteColor(borderColorArray, index) ?: groupStroke

    companion object {
        val MermaidDefault = MermaidTheme()

        val Dark = MermaidTheme(
            background = SceneColor(0xFF333333),
            nodeFill = SceneColor(0xFF1F2020),
            nodeStroke = SceneColor(0xFFCCCCCC),
            nodeText = SceneColor(0xFFCCCCCC),
            edge = SceneColor(0xFFD3D3D3),
            edgeLabelFill = SceneColor(0xFF585858),
            groupFill = SceneColor(0xFF474949),
            groupStroke = SceneColor(0x40FFFFFF),
            groupText = SceneColor(0xFFF9FFFE),
        )

        val FlowchartDefault = MermaidTheme(
            background = SceneColor(0xFFFFFFFF),
            nodeFill = SceneColor(0xFFFFFFFF),
            nodeStroke = SceneColor(0xFF28253D),
            nodeText = SceneColor(0xFF28253D),
            edge = SceneColor(0xFF000000),
            edgeLabelFill = SceneColor(0xFFCCCCCC),
            groupFill = SceneColor(0xFFF9F9FB),
            groupStroke = SceneColor(0xFFBDBCCC),
            groupText = SceneColor(0xFF000000),
            fontSize = 14f,
            fontFamily = MERMAID_REDUX_FONT_FAMILY,
            strokeWidth = 2f,
            bkgColorArray = reduxColorBackgrounds(),
            borderColorArray = reduxColorBorders(),
            dropShadow = reduxShadow(dark = false),
        )

        internal fun named(name: String): MermaidTheme? = when (name.lowercase()) {
            "default" -> MermaidDefault
            "dark" -> Dark
            "forest" -> MermaidTheme(
                background = SceneColor(0xFFFFFFFF),
                nodeFill = SceneColor(0xFFCDE498),
                nodeStroke = SceneColor(0xFF13540C),
                nodeText = SceneColor(0xFF000000),
                edge = SceneColor(0xFF000000),
                edgeLabelFill = SceneColor(0xFFE8E8E8),
                groupFill = SceneColor(0xFFCDFFB2),
                groupStroke = SceneColor(0xFF6EAA49),
                groupText = SceneColor(0xFF333333),
                dropShadow = SceneShadow(
                    color = SceneColor(0x80B9B9B9),
                    offsetX = 1f,
                    offsetY = 2f,
                    blurRadius = 2f,
                ),
            )
            "neutral" -> MermaidTheme(
                background = SceneColor(0xFFFFFFFF),
                nodeFill = SceneColor(0xFFEEEEEE),
                nodeStroke = SceneColor(0xFF999999),
                nodeText = SceneColor(0xFF111111),
                edge = SceneColor(0xFF666666),
                edgeLabelFill = SceneColor(0xFFFFFFFF),
                groupFill = SceneColor(0xFFFCFCFC),
                groupStroke = SceneColor(0xFF707070),
                groupText = SceneColor(0xFF333333),
            )
            "base" -> MermaidTheme(
                background = SceneColor(0xFFF4F4F4),
                nodeFill = SceneColor(0xFFFFF4DD),
                nodeStroke = SceneColor(0xFFE3D7C1),
                nodeText = SceneColor(0xFF333333),
                edge = SceneColor(0xFF0B0B0B),
                edgeLabelFill = SceneColor(0xFFFFDDEE),
                groupFill = SceneColor(0xFFF7FAFF),
                groupStroke = SceneColor(0xFFD7DFED),
                groupText = SceneColor(0xFF333333),
            )
            "neo" -> MermaidTheme(
                background = SceneColor(0xFFFFFFFF),
                nodeFill = SceneColor(0xFFFFFFFF),
                nodeStroke = SceneColor(0xFF000000),
                nodeText = SceneColor(0xFF333333),
                edge = SceneColor(0xFF000000),
                edgeLabelFill = SceneColor(0xFFCCCCCC),
                groupFill = SceneColor(0xFFFFFFFF),
                groupStroke = SceneColor(0xFFE6E6E6),
                groupText = SceneColor(0xFF000000),
                fontSize = 14f,
                fontFamily = MERMAID_NEO_FONT_FAMILY,
                strokeWidth = 2f,
                dropShadow = SceneShadow(
                    color = SceneColor(0x40000000),
                    offsetX = 0f,
                    offsetY = 1f,
                    blurRadius = 2f,
                ),
            )
            "neo-dark" -> MermaidTheme(
                background = SceneColor(0xFF333333),
                nodeFill = SceneColor(0xFF2A2020),
                nodeStroke = SceneColor(0xFFCCCCCC),
                nodeText = SceneColor(0xFFE0DFDF),
                edge = SceneColor(0xFFCCCCCC),
                edgeLabelFill = SceneColor(0xFF585858),
                groupFill = SceneColor(0xFF201F1F),
                groupStroke = SceneColor(0xFF060606),
                groupText = SceneColor(0xFFDFE0E0),
                fontSize = 14f,
                fontFamily = MERMAID_NEO_FONT_FAMILY,
                dropShadow = SceneShadow(
                    color = SceneColor(0x33B9B9B9),
                    offsetX = 1f,
                    offsetY = 2f,
                    blurRadius = 2f,
                ),
            )
            "redux" -> MermaidTheme(
                background = SceneColor(0xFFFFFFFF),
                nodeFill = SceneColor(0xFFFFFFFF),
                nodeStroke = SceneColor(0xFF28253D),
                nodeText = SceneColor(0xFF28253D),
                edge = SceneColor(0xFF000000),
                edgeLabelFill = SceneColor(0xFFCCCCCC),
                groupFill = SceneColor(0xFFF9F9FB),
                groupStroke = SceneColor(0xFFBDBCCC),
                groupText = SceneColor(0xFF000000),
                fontSize = 14f,
                fontFamily = MERMAID_REDUX_FONT_FAMILY,
                strokeWidth = 2f,
                dropShadow = reduxShadow(dark = false),
            )
            "redux-color" -> FlowchartDefault
            "redux-dark" -> reduxDark()
            "redux-dark-color" -> reduxDark().copy(
                borderColorArray = reduxColorBorders(),
            )
            else -> null
        }

        internal fun withVariables(
            theme: MermaidTheme,
            values: Map<String, String>,
            colorArrays: Map<String, List<String>> = emptyMap(),
        ): GMResult<MermaidTheme, MermaidError> {
            var invalidVariable: Pair<String, String>? = null

            fun color(vararg names: String): SceneColor? {
                val entry = names.firstNotNullOfOrNull { name ->
                    values[name]?.let { value -> name to value }
                } ?: return null
                return CssColorParser.parse(entry.second).also { parsed ->
                    if (parsed == null && invalidVariable == null) {
                        invalidVariable = entry
                    }
                }
            }

            fun number(name: String): Float? {
                val value = values[name] ?: return null
                val parsed = value.removeSuffix("px").trim().toFloatOrNull()
                if (parsed == null && invalidVariable == null) {
                    invalidVariable = name to value
                }
                return parsed
            }

            fun colorArray(name: String): GMResult<List<SceneColor>?, MermaidError> {
                val source = colorArrays[name] ?: return GMResult.Ok(null)
                val parsed = source.map(CssColorParser::parse)
                val invalidIndex = parsed.indexOfFirst { color -> color == null }
                if (invalidIndex >= 0) {
                    return GMResult.Err(
                        MermaidError.Configuration(
                            "Mermaid theme variable '$name' has invalid color " +
                                "'${source[invalidIndex]}' at index $invalidIndex",
                        ),
                    )
                }
                return GMResult.Ok(parsed.filterNotNull())
            }

            val bkgColorArray = when (val parsed = colorArray("bkgColorArray")) {
                is GMResult.Ok -> parsed.value ?: theme.bkgColorArray
                is GMResult.Err -> return parsed
            }
            val borderColorArray = when (val parsed = colorArray("borderColorArray")) {
                is GMResult.Ok -> parsed.value ?: theme.borderColorArray
                is GMResult.Err -> return parsed
            }
            val dropShadow = when (
                val parsed = parseDropShadow(
                    source = values["dropShadow"],
                    inherited = theme.dropShadow,
                )
            ) {
                is GMResult.Ok -> parsed.value
                is GMResult.Err -> return GMResult.Err(parsed.error)
            }
            val resolved = theme.copy(
                background = color("background") ?: theme.background,
                nodeFill = color("mainBkg") ?: theme.nodeFill,
                nodeStroke = color("nodeBorder", "primaryBorderColor") ?: theme.nodeStroke,
                nodeText = color("nodeTextColor", "primaryTextColor", "textColor") ?: theme.nodeText,
                edge = color("defaultLinkColor", "lineColor") ?: theme.edge,
                edgeLabelFill = color("edgeLabelBackground") ?: theme.edgeLabelFill,
                groupFill = color("clusterBkg") ?: theme.groupFill,
                groupStroke = color("clusterBorder") ?: theme.groupStroke,
                groupText = color("clusterText", "titleColor") ?: theme.groupText,
                fontSize = number("fontSize") ?: theme.fontSize,
                fontFamily = values["fontFamily"] ?: theme.fontFamily,
                strokeWidth = number("strokeWidth") ?: theme.strokeWidth,
                bkgColorArray = bkgColorArray,
                borderColorArray = borderColorArray,
                dropShadow = dropShadow,
            )
            val invalid = invalidVariable
            return if (invalid == null) {
                GMResult.Ok(resolved)
            } else {
                GMResult.Err(
                    MermaidError.Configuration(
                        "Mermaid theme variable '${invalid.first}' has invalid value " +
                            "'${invalid.second}'",
                    ),
                )
            }
        }

        private fun reduxDark(): MermaidTheme = MermaidTheme(
            background = SceneColor(0xFF333333),
            nodeFill = SceneColor(0xFF111113),
            nodeStroke = SceneColor(0xFFFFFFFF),
            nodeText = SceneColor(0xFFE0DFDF),
            edge = SceneColor(0xFFCCCCCC),
            edgeLabelFill = SceneColor(0xFF585858),
            groupFill = SceneColor(0xFF1E1A2E),
            groupStroke = SceneColor(0xFFBDBCCC),
            groupText = SceneColor(0xFFDFE0E0),
            fontSize = 14f,
                fontFamily = MERMAID_REDUX_FONT_FAMILY,
            strokeWidth = 2f,
            dropShadow = reduxShadow(dark = true),
        )

        private fun reduxShadow(dark: Boolean): SceneShadow = SceneShadow(
            color = if (dark) SceneColor(0x0FFFFFFF) else SceneColor(0x0F000000),
            offsetX = 4f,
            offsetY = 4f,
        )

        private fun parseDropShadow(
            source: String?,
            inherited: SceneShadow?,
        ): GMResult<SceneShadow?, MermaidError> {
            val value = source?.trim() ?: return GMResult.Ok(inherited)
            if (value.equals("none", ignoreCase = true)) {
                return GMResult.Ok(null)
            }
            if (value.equals("url(#drop-shadow)", ignoreCase = true)) {
                return GMResult.Ok(inherited)
            }
            val match = DROP_SHADOW_PATTERN.matchEntire(value)
                ?: return GMResult.Err(
                    MermaidError.Configuration(
                        "Mermaid theme variable 'dropShadow' uses an unsupported CSS filter '$source'",
                    ),
                )
            val offsetX = match.groupValues[1].toFloatOrNull()
            val offsetY = match.groupValues[2].toFloatOrNull()
            val blurRadius = match.groupValues[3].toFloatOrNull()
            val color = CssColorParser.parse(match.groupValues[4])
            if (offsetX == null || offsetY == null || blurRadius == null || blurRadius < 0f || color == null) {
                return GMResult.Err(
                    MermaidError.Configuration(
                        "Mermaid theme variable 'dropShadow' has invalid value '$source'",
                    ),
                )
            }
            return GMResult.Ok(
                SceneShadow(
                    color = color,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    blurRadius = blurRadius,
                ),
            )
        }

        private fun paletteColor(
            colors: List<SceneColor>,
            index: Int?,
        ): SceneColor? =
            index
                ?.takeIf { colors.isNotEmpty() }
                ?.let { value -> colors[value.mod(colors.size)] }

        private fun reduxColorBackgrounds(): List<SceneColor> = listOf(
            SceneColor(0xFFFDF4FF),
            SceneColor(0xFFF0FDFA),
            SceneColor(0xFFFFF7ED),
            SceneColor(0xFFECFEFF),
            SceneColor(0xFFF0FDF4),
            SceneColor(0xFFF5F3FF),
            SceneColor(0xFFFEF2F2),
            SceneColor(0xFFFEFCE8),
            SceneColor(0xFFEEF2FF),
            SceneColor(0xFFF7FEE7),
            SceneColor(0xFFF0F9FF),
            SceneColor(0xFFFFF1F2),
        )

        private fun reduxColorBorders(): List<SceneColor> = listOf(
            SceneColor(0xFFE879F9),
            SceneColor(0xFF2DD4BF),
            SceneColor(0xFFFB923C),
            SceneColor(0xFF22D3EE),
            SceneColor(0xFF4ADE80),
            SceneColor(0xFFA78BFA),
            SceneColor(0xFFF87171),
            SceneColor(0xFFFACC15),
            SceneColor(0xFF818CF8),
            SceneColor(0xFFA3E635),
            SceneColor(0xFF38BDF8),
            SceneColor(0xFFFB7185),
        )

        private val DROP_SHADOW_PATTERN = Regex(
            pattern = """^drop-shadow\(\s*(-?(?:\d+(?:\.\d+)?|\.\d+))px\s+""" +
                """(-?(?:\d+(?:\.\d+)?|\.\d+))px\s+""" +
                """((?:\d+(?:\.\d+)?|\.\d+))px\s+(.+)\s*\)\s*;?\s*$""",
            option = RegexOption.IGNORE_CASE,
        )
    }
}
