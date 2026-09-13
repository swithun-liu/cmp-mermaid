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

data class MermaidXyAxisOptions(
    val showLabel: Boolean = true,
    val labelFontSize: Float = 14f,
    val labelPadding: Float = 5f,
    val showTitle: Boolean = true,
    val titleFontSize: Float = 16f,
    val titlePadding: Float = 5f,
    val showTick: Boolean = true,
    val tickLength: Float = 5f,
    val tickWidth: Float = 2f,
    val showAxisLine: Boolean = true,
    val axisLineWidth: Float = 2f,
    val labelRotation: Float = 0f,
)

data class MermaidXyChartOptions(
    val width: Float = 700f,
    val height: Float = 500f,
    val titleFontSize: Float = 20f,
    val titlePadding: Float = 10f,
    val showTitle: Boolean = true,
    val showLegend: Boolean = true,
    val legendFontSize: Float = 14f,
    val legendPadding: Float = 10f,
    val showDataLabel: Boolean = false,
    val showDataLabelOutsideBar: Boolean = false,
    val chartOrientation: String = "vertical",
    val plotReservedSpacePercent: Float = 50f,
    val xAxis: MermaidXyAxisOptions = MermaidXyAxisOptions(),
    val yAxis: MermaidXyAxisOptions = MermaidXyAxisOptions(),
)

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
    val ganttTitleTopMargin: Float = 25f,
    val ganttBarHeight: Float = 20f,
    val ganttBarGap: Float = 4f,
    val ganttTopPadding: Float = 50f,
    val ganttRightPadding: Float = 75f,
    val ganttLeftPadding: Float = 75f,
    val ganttGridLineStartPadding: Float = 35f,
    val ganttFontSize: Float = 11f,
    val ganttSectionFontSize: Float = 11f,
    val ganttNumberSectionStyles: Int = 4,
    val ganttAxisFormat: String = "%Y-%m-%d",
    val ganttTickInterval: String? = null,
    val ganttTopAxis: Boolean = false,
    val ganttDisplayMode: String = "",
    val ganttWeekday: String = "sunday",
    val ganttUseWidth: Float = 1200f,
    val pieTextPosition: Float = 0.75f,
    val pieDonutHole: Float = 0f,
    val pieLegendPosition: String = "right",
    val pieHighlightSlice: String = "",
    val xyChart: MermaidXyChartOptions = MermaidXyChartOptions(),
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

/**
 * Mermaid 12 built-in theme names, available without parsing diagram configuration.
 */
enum class MermaidThemePreset(
    val configName: String,
) {
    Default("default"),
    Dark("dark"),
    Forest("forest"),
    Neutral("neutral"),
    Base("base"),
    Neo("neo"),
    NeoDark("neo-dark"),
    Redux("redux"),
    ReduxColor("redux-color"),
    ReduxDark("redux-dark"),
    ReduxDarkColor("redux-dark-color"),
}

data class MermaidPieTheme(
    val colors: List<SceneColor> = listOf(
        SceneColor(0xFFECECFF),
        SceneColor(0xFFFFFFDE),
        SceneColor(0xFFB5FF20),
        SceneColor(0xFFB9B9FF),
        SceneColor(0xFFFFFF45),
        SceneColor(0xFFD7FF86),
        SceneColor(0xFFFF86FF),
        SceneColor(0xFF20FFFF),
        SceneColor(0xFFFF2020),
        SceneColor(0xFFFF20FF),
        SceneColor(0xFF20FF90),
        SceneColor(0xFFFF5353),
    ),
    val titleTextSize: Float = 25f,
    val titleTextColor: SceneColor = SceneColor(0xFF000000),
    val sectionTextSize: Float = 17f,
    val sectionTextColor: SceneColor = SceneColor(0xFF333333),
    val legendTextSize: Float = 17f,
    val legendTextColor: SceneColor = SceneColor(0xFF000000),
    val strokeColor: SceneColor = SceneColor(0xFF000000),
    val strokeWidth: Float = 2f,
    val outerStrokeWidth: Float = 2f,
    val outerStrokeColor: SceneColor = SceneColor(0xFF000000),
    val opacity: Float = 0.7f,
)

data class MermaidXyChartTheme(
    val backgroundColor: SceneColor = SceneColor(0xFFFFFFFF),
    val titleColor: SceneColor = SceneColor(0xFF333333),
    val dataLabelColor: SceneColor = SceneColor(0xFF333333),
    val legendTextColor: SceneColor = SceneColor(0xFF333333),
    val xAxisLabelColor: SceneColor = SceneColor(0xFF333333),
    val xAxisTitleColor: SceneColor = SceneColor(0xFF333333),
    val xAxisTickColor: SceneColor = SceneColor(0xFF333333),
    val xAxisLineColor: SceneColor = SceneColor(0xFF333333),
    val yAxisLabelColor: SceneColor = SceneColor(0xFF333333),
    val yAxisTitleColor: SceneColor = SceneColor(0xFF333333),
    val yAxisTickColor: SceneColor = SceneColor(0xFF333333),
    val yAxisLineColor: SceneColor = SceneColor(0xFF333333),
    val plotColorPalette: List<SceneColor> = listOf(
        SceneColor(0xFFECECFF),
        SceneColor(0xFF8493A6),
        SceneColor(0xFFFFC3A0),
        SceneColor(0xFFDCDDE1),
        SceneColor(0xFFB8E994),
        SceneColor(0xFFD1A36F),
        SceneColor(0xFFC3CDE6),
        SceneColor(0xFFFFB6C1),
        SceneColor(0xFF496078),
        SceneColor(0xFFF8F3E3),
    ),
)

data class MermaidGanttTheme(
    val sectionBackground: SceneColor = SceneColor(0x7D6666FF),
    val alternateSectionBackground: SceneColor = SceneColor(0xFFFFFFFF),
    val secondSectionBackground: SceneColor = SceneColor(0xFFFFF400),
    val excludedBackground: SceneColor = SceneColor(0xFFEEEEEE),
    val taskFill: SceneColor = SceneColor(0xFF8A90DD),
    val taskStroke: SceneColor = SceneColor(0xFF534FBC),
    val activeTaskFill: SceneColor = SceneColor(0xFFBFC7FF),
    val activeTaskStroke: SceneColor = SceneColor(0xFF534FBC),
    val doneTaskFill: SceneColor = SceneColor(0xFFD3D3D3),
    val doneTaskStroke: SceneColor = SceneColor(0xFF808080),
    val criticalTaskFill: SceneColor = SceneColor(0xFFFF0000),
    val criticalTaskStroke: SceneColor = SceneColor(0xFFFF8888),
    val taskText: SceneColor = SceneColor(0xFFFFFFFF),
    val darkTaskText: SceneColor = SceneColor(0xFF000000),
    val outsideTaskText: SceneColor = SceneColor(0xFF000000),
    val clickableTaskText: SceneColor = SceneColor(0xFF003163),
    val text: SceneColor = SceneColor(0xFF333333),
    val grid: SceneColor = SceneColor(0xFFD3D3D3),
    val todayLine: SceneColor = SceneColor(0xFFFF0000),
    val verticalLine: SceneColor = SceneColor(0xFF000080),
    val title: SceneColor = SceneColor(0xFF333333),
)

data class MermaidTheme(
    val background: SceneColor = SceneColor(0xFFFFFFFF),
    val nodeFill: SceneColor = SceneColor(0xFFECECFF),
    val nodeStroke: SceneColor = SceneColor(0xFF9370DB),
    val nodeText: SceneColor = SceneColor(0xFF131300),
    val edge: SceneColor = SceneColor(0xFF333333),
    val edgeLabelFill: SceneColor = SceneColor(0xCCE8E8E8),
    val groupFill: SceneColor = SceneColor(0xFFFFFFDE),
    val groupStroke: SceneColor = SceneColor(0xFFAAAA33),
    val groupText: SceneColor = SceneColor(0xFF333333),
    val noteFill: SceneColor = SceneColor(0xFFFFF5AD),
    val noteStroke: SceneColor = SceneColor(0xFFAAAA33),
    val noteText: SceneColor = SceneColor(0xFF000000),
    val fontSize: Float = 16f,
    val fontFamily: String = MERMAID_CLASSIC_FONT_FAMILY,
    val strokeWidth: Float = 1f,
    val bkgColorArray: List<SceneColor> = emptyList(),
    val borderColorArray: List<SceneColor> = emptyList(),
    val pie: MermaidPieTheme = MermaidPieTheme(),
    val xyChart: MermaidXyChartTheme = MermaidXyChartTheme(),
    val gantt: MermaidGanttTheme = MermaidGanttTheme(),
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
            nodeText = SceneColor(0xFFE0DFDF),
            edge = SceneColor(0xFFD3D3D3),
            edgeLabelFill = SceneColor(0xFF585858),
            groupFill = SceneColor(0xFF474949),
            groupStroke = SceneColor(0x40FFFFFF),
            groupText = SceneColor(0xFFF9FFFE),
            noteFill = SceneColor(0xFF474949),
            noteStroke = SceneColor(0xFF2F2F2F),
            noteText = SceneColor(0xFFB8B6B6),
            gantt = darkGanttTheme(),
            pie = darkPieTheme(),
            xyChart = xyChartTheme(
                background = 0xFF333333,
                text = 0xFFCCCCCC,
                palette = listOf(
                    0xFF3498DB,
                    0xFF2ECC71,
                    0xFFE74C3C,
                    0xFFF1C40F,
                    0xFFBDC3C7,
                    0xFFFFFFFF,
                    0xFF34495E,
                    0xFF9B59B6,
                    0xFF1ABC9C,
                    0xFFE67E22,
                ),
            ),
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
            noteFill = SceneColor(0xFFFFF5AD),
            noteStroke = SceneColor(0xFFFACC15),
            noteText = SceneColor(0xFF28253D),
            gantt = reduxColorGanttTheme(),
            fontSize = 14f,
            fontFamily = MERMAID_REDUX_FONT_FAMILY,
            strokeWidth = 2f,
            bkgColorArray = reduxColorBackgrounds(),
            borderColorArray = reduxColorBorders(),
            pie = reduxColorPieTheme(dark = false),
            xyChart = reduxXyChartTheme(background = 0xFFFFFFFF, text = 0xFF28253D),
            dropShadow = reduxShadow(dark = false),
        )

        /**
         * Resolves a Mermaid 12 built-in theme for host-controlled runtime switching.
         */
        fun preset(preset: MermaidThemePreset): MermaidTheme =
            named(preset.configName) ?: MermaidDefault

        /**
         * Resolves a Mermaid 12 theme name without throwing on persisted or remote input.
         */
        fun fromName(name: String): GMResult<MermaidTheme, MermaidError> =
            named(name.trim())?.let { theme -> GMResult.Ok(theme) } ?: GMResult.Err(
                MermaidError.Configuration(
                    "Unsupported Mermaid theme '$name'",
                ),
            )

        internal fun named(name: String): MermaidTheme? = when (name.lowercase()) {
            "default" -> MermaidDefault
            "dark" -> Dark
            "forest" -> MermaidTheme(
                background = SceneColor(0xFFFFFFFF),
                nodeFill = SceneColor(0xFFCDE498),
                nodeStroke = SceneColor(0xFF13540C),
                nodeText = SceneColor(0xFF321B67),
                edge = SceneColor(0xFF000000),
                edgeLabelFill = SceneColor(0xFFE8E8E8),
                groupFill = SceneColor(0xFFCDFFB2),
                groupStroke = SceneColor(0xFF6EAA49),
                groupText = SceneColor(0xFF333333),
                noteFill = SceneColor(0xFFFFF5AD),
                noteStroke = SceneColor(0xFF6EAA49),
                noteText = SceneColor(0xFF000000),
                gantt = forestGanttTheme(),
                pie = forestPieTheme(),
                xyChart = xyChartTheme(
                    background = 0xFFFFFFFF,
                    text = 0xFF000000,
                    palette = listOf(
                        0xFFCDE498,
                        0xFFFF6B6B,
                        0xFFA0D2DB,
                        0xFFD7BDE2,
                        0xFFF0F0F0,
                        0xFFFFC3A0,
                        0xFF7FD8BE,
                        0xFFFF9A8B,
                        0xFFFAF3E0,
                        0xFFFFF176,
                    ),
                ),
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
                noteFill = SceneColor(0xFF666666),
                noteStroke = SceneColor(0xFF999999),
                noteText = SceneColor(0xFFFFFFFF),
                gantt = neutralGanttTheme(),
                pie = neutralPieTheme(),
                xyChart = xyChartTheme(
                    background = 0xFFFFFFFF,
                    text = 0xFF333333,
                    palette = listOf(
                        0xFFEEEEEE,
                        0xFF6BB8E4,
                        0xFF8ACB88,
                        0xFFC7ACD6,
                        0xFFE8DCC2,
                        0xFFFFB2A8,
                        0xFFFFF380,
                        0xFF7E8D91,
                        0xFFFFD8B1,
                        0xFFFAF3E0,
                    ),
                ),
            )
            "base" -> MermaidTheme(
                background = SceneColor(0xFFF4F4F4),
                nodeFill = SceneColor(0xFFFFF4DD),
                nodeStroke = SceneColor(0xFFEEDEBB),
                nodeText = SceneColor(0xFF333333),
                edge = SceneColor(0xFF0B0B0B),
                edgeLabelFill = SceneColor(0xFFF4DDFF),
                groupFill = SceneColor(0xFFF7F9FF),
                groupStroke = SceneColor(0xFFCFDBF3),
                groupText = SceneColor(0xFF090600),
                noteFill = SceneColor(0xFFFFF5AD),
                noteStroke = SceneColor(0xFFE4DB95),
                noteText = SceneColor(0xFF333333),
                gantt = baseGanttTheme(),
                pie = basePieTheme(),
                xyChart = reduxXyChartTheme(background = 0xFFF4F4F4, text = 0xFF333333),
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
                noteFill = SceneColor(0xFFFFF5AD),
                noteStroke = SceneColor(0xFFE4DB95),
                noteText = SceneColor(0xFF333333),
                gantt = neoGanttTheme(),
                fontSize = 14f,
                fontFamily = MERMAID_NEO_FONT_FAMILY,
                strokeWidth = 2f,
                pie = neoPieTheme(dark = false),
                xyChart = reduxXyChartTheme(background = 0xFFFFFFFF, text = 0xFF333333),
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
                noteFill = SceneColor(0xFFFFF5AD),
                noteStroke = SceneColor(0xFFE4DB95),
                noteText = SceneColor(0xFF333333),
                gantt = neoDarkGanttTheme(),
                fontSize = 14f,
                fontFamily = MERMAID_NEO_FONT_FAMILY,
                pie = neoPieTheme(dark = true),
                xyChart = reduxXyChartTheme(background = 0xFF333333, text = 0xFFE0DFDF),
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
                noteFill = SceneColor(0xFFFFF5AD),
                noteStroke = SceneColor(0xFFFACC15),
                noteText = SceneColor(0xFF28253D),
                gantt = reduxGanttTheme(),
                fontSize = 14f,
                fontFamily = MERMAID_REDUX_FONT_FAMILY,
                strokeWidth = 2f,
                pie = reduxPieTheme(dark = false),
                xyChart = reduxXyChartTheme(background = 0xFFFFFFFF, text = 0xFF28253D),
                dropShadow = reduxShadow(dark = false),
            )
            "redux-color" -> FlowchartDefault
            "redux-dark" -> reduxDark()
            "redux-dark-color" -> reduxDark().copy(
                borderColorArray = reduxColorBorders(),
                gantt = reduxDarkColorGanttTheme(),
                pie = reduxColorPieTheme(dark = true),
                xyChart = reduxXyChartTheme(background = 0xFF333333, text = 0xFFE0DFDF),
            )
            else -> null
        }

        /**
         * Applies Mermaid themeVariables to a base theme for application brand styling.
         */
        fun withVariables(
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
            val pieColors = MutableList(12) { index ->
                theme.pie.colors.getOrElse(index) { SceneColor(0xFF000000) }
            }
            pieColors.indices.forEach { index ->
                color("pie${index + 1}")?.let { parsed ->
                    pieColors[index] = parsed
                }
            }
            val pieStrokeWidth = number("pieStrokeWidth") ?: theme.pie.strokeWidth
            val pieOuterStrokeWidth =
                number("pieOuterStrokeWidth") ?: theme.pie.outerStrokeWidth
            val pieOpacity = number("pieOpacity") ?: theme.pie.opacity
            if (
                pieStrokeWidth < 0f ||
                pieOuterStrokeWidth < 0f ||
                pieOpacity !in 0f..1f
            ) {
                val invalidName = when {
                    pieStrokeWidth < 0f -> "pieStrokeWidth"
                    pieOuterStrokeWidth < 0f -> "pieOuterStrokeWidth"
                    else -> "pieOpacity"
                }
                invalidVariable = invalidName to values[invalidName].orEmpty()
            }
            val pie = theme.pie.copy(
                colors = pieColors,
                titleTextSize = number("pieTitleTextSize") ?: theme.pie.titleTextSize,
                titleTextColor =
                    color("pieTitleTextColor") ?: theme.pie.titleTextColor,
                sectionTextSize =
                    number("pieSectionTextSize") ?: theme.pie.sectionTextSize,
                sectionTextColor =
                    color("pieSectionTextColor") ?: theme.pie.sectionTextColor,
                legendTextSize =
                    number("pieLegendTextSize") ?: theme.pie.legendTextSize,
                legendTextColor =
                    color("pieLegendTextColor") ?: theme.pie.legendTextColor,
                strokeColor = color("pieStrokeColor") ?: theme.pie.strokeColor,
                strokeWidth = pieStrokeWidth,
                outerStrokeWidth = pieOuterStrokeWidth,
                outerStrokeColor =
                    color("pieOuterStrokeColor") ?: theme.pie.outerStrokeColor,
                opacity = pieOpacity,
            )
            val gantt = theme.gantt.copy(
                sectionBackground =
                    color("sectionBkgColor") ?: theme.gantt.sectionBackground,
                alternateSectionBackground =
                    color("altSectionBkgColor") ?: theme.gantt.alternateSectionBackground,
                secondSectionBackground =
                    color("sectionBkgColor2") ?: theme.gantt.secondSectionBackground,
                excludedBackground =
                    color("excludeBkgColor") ?: theme.gantt.excludedBackground,
                taskFill = color("taskBkgColor") ?: theme.gantt.taskFill,
                taskStroke = color("taskBorderColor") ?: theme.gantt.taskStroke,
                activeTaskFill =
                    color("activeTaskBkgColor") ?: theme.gantt.activeTaskFill,
                activeTaskStroke =
                    color("activeTaskBorderColor") ?: theme.gantt.activeTaskStroke,
                doneTaskFill =
                    color("doneTaskBkgColor") ?: theme.gantt.doneTaskFill,
                doneTaskStroke =
                    color("doneTaskBorderColor") ?: theme.gantt.doneTaskStroke,
                criticalTaskFill =
                    color("critBkgColor") ?: theme.gantt.criticalTaskFill,
                criticalTaskStroke =
                    color("critBorderColor") ?: theme.gantt.criticalTaskStroke,
                taskText = color("taskTextColor") ?: theme.gantt.taskText,
                darkTaskText =
                    color("taskTextDarkColor") ?: theme.gantt.darkTaskText,
                outsideTaskText =
                    color("taskTextOutsideColor") ?: theme.gantt.outsideTaskText,
                clickableTaskText =
                    color("taskTextClickableColor") ?: theme.gantt.clickableTaskText,
                text = color("textColor") ?: theme.gantt.text,
                grid = color("gridColor") ?: theme.gantt.grid,
                todayLine = color("todayLineColor") ?: theme.gantt.todayLine,
                verticalLine = color("vertLineColor") ?: theme.gantt.verticalLine,
                title = color("titleColor") ?: theme.gantt.title,
            )
            val xyPaletteSource = values["xyChart.plotColorPalette"]
            val xyPalette = if (xyPaletteSource == null) {
                theme.xyChart.plotColorPalette
            } else {
                val sourceColors = xyPaletteSource
                    .split(',')
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                val parsedColors = sourceColors.map(CssColorParser::parse)
                val invalidIndex = parsedColors.indexOfFirst { parsed -> parsed == null }
                when {
                    sourceColors.isEmpty() -> {
                        invalidVariable = "xyChart.plotColorPalette" to xyPaletteSource
                        emptyList()
                    }
                    invalidIndex >= 0 -> {
                        invalidVariable = "xyChart.plotColorPalette" to sourceColors[invalidIndex]
                        emptyList()
                    }
                    else -> parsedColors.filterNotNull()
                }
            }
            val xyChart = theme.xyChart.copy(
                backgroundColor =
                    color("xyChart.backgroundColor") ?: theme.xyChart.backgroundColor,
                titleColor = color("xyChart.titleColor") ?: theme.xyChart.titleColor,
                dataLabelColor =
                    color("xyChart.dataLabelColor") ?: theme.xyChart.dataLabelColor,
                legendTextColor =
                    color("xyChart.legendTextColor") ?: theme.xyChart.legendTextColor,
                xAxisLabelColor =
                    color("xyChart.xAxisLabelColor") ?: theme.xyChart.xAxisLabelColor,
                xAxisTitleColor =
                    color("xyChart.xAxisTitleColor") ?: theme.xyChart.xAxisTitleColor,
                xAxisTickColor =
                    color("xyChart.xAxisTickColor") ?: theme.xyChart.xAxisTickColor,
                xAxisLineColor =
                    color("xyChart.xAxisLineColor") ?: theme.xyChart.xAxisLineColor,
                yAxisLabelColor =
                    color("xyChart.yAxisLabelColor") ?: theme.xyChart.yAxisLabelColor,
                yAxisTitleColor =
                    color("xyChart.yAxisTitleColor") ?: theme.xyChart.yAxisTitleColor,
                yAxisTickColor =
                    color("xyChart.yAxisTickColor") ?: theme.xyChart.yAxisTickColor,
                yAxisLineColor =
                    color("xyChart.yAxisLineColor") ?: theme.xyChart.yAxisLineColor,
                plotColorPalette = xyPalette,
            )
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
                nodeFill = color("mainBkg", "primaryColor") ?: theme.nodeFill,
                nodeStroke = color("nodeBorder", "primaryBorderColor") ?: theme.nodeStroke,
                nodeText = color("nodeTextColor", "primaryTextColor", "textColor") ?: theme.nodeText,
                edge = color("defaultLinkColor", "lineColor") ?: theme.edge,
                edgeLabelFill = color("edgeLabelBackground") ?: theme.edgeLabelFill,
                groupFill = color("clusterBkg") ?: theme.groupFill,
                groupStroke = color("clusterBorder") ?: theme.groupStroke,
                groupText = color("clusterText", "titleColor") ?: theme.groupText,
                noteFill = color("noteBkgColor") ?: theme.noteFill,
                noteStroke = color("noteBorderColor") ?: theme.noteStroke,
                noteText = color("noteTextColor") ?: theme.noteText,
                fontSize = number("fontSize") ?: theme.fontSize,
                fontFamily = values["fontFamily"] ?: theme.fontFamily,
                strokeWidth = number("strokeWidth") ?: theme.strokeWidth,
                bkgColorArray = bkgColorArray,
                borderColorArray = borderColorArray,
                pie = pie,
                xyChart = xyChart,
                gantt = gantt,
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
            noteFill = SceneColor(0xFFFEF9C3),
            noteStroke = SceneColor(0xFFFACC15),
            noteText = SceneColor(0xFF28253D),
            gantt = reduxDarkGanttTheme(),
            fontSize = 14f,
            fontFamily = MERMAID_REDUX_FONT_FAMILY,
            strokeWidth = 2f,
            pie = reduxPieTheme(dark = true),
            xyChart = reduxXyChartTheme(background = 0xFF333333, text = 0xFFE0DFDF),
            dropShadow = reduxShadow(dark = true),
        )

        private fun darkGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFFB4AC76),
            alternateSectionBackground = SceneColor(0xFF333333),
            secondSectionBackground = SceneColor(0xFFEAE8D9),
            excludedBackground = SceneColor(0xFF9F9758),
            taskFill = SceneColor(0xFF595C5C),
            taskStroke = SceneColor(0xFFFFFFFF),
            activeTaskFill = SceneColor(0xFF81B1DB),
            activeTaskStroke = SceneColor(0xFFFFFFFF),
            criticalTaskFill = SceneColor(0xFFE83737),
            criticalTaskStroke = SceneColor(0xFFE83737),
            taskText = SceneColor(0xFFE2DCD6),
            darkTaskText = SceneColor(0xFF2C2C2C),
            outsideTaskText = SceneColor(0xFFD3D3D3),
            text = SceneColor(0xFFCCCCCC),
            todayLine = SceneColor(0xFFDB5757),
            verticalLine = SceneColor(0xFF00BFFF),
            title = SceneColor(0xFFF9FFFE),
        )

        private fun forestGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFF6EAA49),
            secondSectionBackground = SceneColor(0xFF6EAA49),
            taskFill = SceneColor(0xFF487E3A),
            taskStroke = SceneColor(0xFF13540C),
            activeTaskFill = SceneColor(0xFFCDE498),
            activeTaskStroke = SceneColor(0xFF13540C),
            text = SceneColor(0xFF000000),
            verticalLine = SceneColor(0xFF00BFFF),
        )

        private fun neutralGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFFBDBDBD),
            secondSectionBackground = SceneColor(0xFFBDBDBD),
            taskFill = SceneColor(0xFF707070),
            taskStroke = SceneColor(0xFF575757),
            activeTaskFill = SceneColor(0xFFEEEEEE),
            activeTaskStroke = SceneColor(0xFF575757),
            doneTaskFill = SceneColor(0xFFBBBBBB),
            doneTaskStroke = SceneColor(0xFF666666),
            criticalTaskFill = SceneColor(0xFFDD4422),
            criticalTaskStroke = SceneColor(0xFFB1361B),
            darkTaskText = SceneColor(0xFF333333),
            outsideTaskText = SceneColor(0xFF333333),
            text = SceneColor(0xFF000000),
            grid = SceneColor(0xFFE6E6E6),
            todayLine = SceneColor(0xFFDD4422),
            verticalLine = SceneColor(0xFFDD4422),
        )

        private fun baseGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFFF7F9FF),
            secondSectionBackground = SceneColor(0xFFFFF4DD),
            taskFill = SceneColor(0xFFFFF4DD),
            taskStroke = SceneColor(0xFFEEDEBB),
            activeTaskFill = SceneColor(0xFFFFFFFF),
            activeTaskStroke = SceneColor(0xFFFFF4DD),
            taskText = SceneColor(0xFF333333),
            darkTaskText = SceneColor(0xFF333333),
            outsideTaskText = SceneColor(0xFF333333),
            text = SceneColor(0xFF333333),
            title = SceneColor(0xFF090600),
        )

        private fun neoGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFFFFFFFF),
            secondSectionBackground = SceneColor(0xFFECECFE),
            taskFill = SceneColor(0xFFECECFE),
            taskStroke = SceneColor(0xFFB3B3B3),
            activeTaskFill = SceneColor(0xFFFFFFFF),
            activeTaskStroke = SceneColor(0xFFECECFE),
            taskText = SceneColor(0xFF333333),
            darkTaskText = SceneColor(0xFF333333),
            outsideTaskText = SceneColor(0xFF333333),
            verticalLine = SceneColor(0xFFB3B3B3),
            title = SceneColor(0xFF000000),
        )

        private fun neoDarkGanttTheme(): MermaidGanttTheme = MermaidGanttTheme(
            sectionBackground = SceneColor(0xFF201F1F),
            secondSectionBackground = SceneColor(0xFF1F2020),
            taskFill = SceneColor(0xFF1F2020),
            taskStroke = SceneColor(0xFFCCCCCC),
            activeTaskFill = SceneColor(0xFF595C5C),
            activeTaskStroke = SceneColor(0xFF1F2020),
            doneTaskFill = SceneColor(0xFF584343),
            taskText = SceneColor(0xFFCCCCCC),
            darkTaskText = SceneColor(0xFFCCCCCC),
            outsideTaskText = SceneColor(0xFFCCCCCC),
            text = SceneColor(0xFFCCCCCC),
            verticalLine = SceneColor(0xFFCCCCCC),
            title = SceneColor(0xFFDFE0E0),
        )

        private fun reduxGanttTheme(): MermaidGanttTheme = neoGanttTheme().copy(
            taskStroke = SceneColor(0xFF181818),
            activeTaskStroke = SceneColor(0xFFECECFE),
            taskText = SceneColor(0xFF28253D),
            darkTaskText = SceneColor(0xFF28253D),
            outsideTaskText = SceneColor(0xFF28253D),
            text = SceneColor(0xFF28253D),
            verticalLine = SceneColor(0xFF181818),
        )

        private fun reduxColorGanttTheme(): MermaidGanttTheme = reduxGanttTheme().copy(
            sectionBackground = SceneColor(0xFFF4A8FF),
            secondSectionBackground = SceneColor(0xFF46ECD5),
        )

        private fun reduxDarkGanttTheme(): MermaidGanttTheme = neoDarkGanttTheme().copy(
            doneTaskFill = SceneColor(0xFF38383E),
        )

        private fun reduxDarkColorGanttTheme(): MermaidGanttTheme =
            reduxDarkGanttTheme().copy(
                sectionBackground = SceneColor(0xFFF4A8FF),
                alternateSectionBackground = SceneColor(0xFF333333),
                secondSectionBackground = SceneColor(0xFF46ECD5),
            )

        private fun reduxXyChartTheme(
            background: Long,
            text: Long,
        ): MermaidXyChartTheme = xyChartTheme(
            background = background,
            text = text,
            palette = listOf(
                0xFFFFF4DD,
                0xFFFFD8B1,
                0xFFFFA07A,
                0xFFECEFF1,
                0xFFD6DBDF,
                0xFFC3E0A8,
                0xFFFFB6A4,
                0xFFFFD74D,
                0xFF738FA7,
                0xFFFFFFF0,
            ),
        )

        private fun xyChartTheme(
            background: Long,
            text: Long,
            palette: List<Long>,
        ): MermaidXyChartTheme {
            val textColor = SceneColor(text)
            return MermaidXyChartTheme(
                backgroundColor = SceneColor(background),
                titleColor = textColor,
                dataLabelColor = textColor,
                legendTextColor = textColor,
                xAxisLabelColor = textColor,
                xAxisTitleColor = textColor,
                xAxisTickColor = textColor,
                xAxisLineColor = textColor,
                yAxisLabelColor = textColor,
                yAxisTitleColor = textColor,
                yAxisTickColor = textColor,
                yAxisLineColor = textColor,
                plotColorPalette = palette.map(::SceneColor),
            )
        }

        private fun reduxShadow(dark: Boolean): SceneShadow = SceneShadow(
            color = if (dark) SceneColor(0x0FFFFFFF) else SceneColor(0x0F000000),
            offsetX = 4f,
            offsetY = 4f,
        )

        private fun darkPieTheme(): MermaidPieTheme = pieTheme(
            colors = listOf(
                0xFF0B0000,
                0xFF4D1037,
                0xFF3F5258,
                0xFF4F2F1B,
                0xFF6E0A0A,
                0xFF3B0048,
                0xFF995A01,
                0xFF154706,
                0xFF161722,
                0xFF00296F,
                0xFF01629C,
                0xFF000000,
            ),
            title = 0xFFD3D3D3,
            section = 0xFFCCCCCC,
            legend = 0xFFD3D3D3,
        )

        private fun forestPieTheme(): MermaidPieTheme = pieTheme(
            colors = listOf(
                0xFFCDE498,
                0xFFCDFFB2,
                0xFFE1EFC0,
                0xFF8CB42F,
                0xFF6AFF19,
                0xFF33B42F,
                0xFF70D990,
                0xFFD99070,
                0xFF98CDE4,
                0xFF1A6330,
                0xFF63301A,
                0xFF1A4D63,
            ),
            title = 0xFF000000,
            section = 0xFF000000,
            legend = 0xFF000000,
        )

        private fun neutralPieTheme(): MermaidPieTheme = pieTheme(
            colors = listOf(
                0xFFF4F4F4,
                0xFF555555,
                0xFFBBBBBB,
                0xFF777777,
                0xFF999999,
                0xFFDDDDDD,
                0xFFFFFFFF,
                0xFFDDDDDD,
                0xFFBBBBBB,
                0xFF999999,
                0xFF777777,
                0xFF555555,
            ),
            title = 0xFF333333,
            section = 0xFF000000,
            legend = 0xFF333333,
        )

        private fun basePieTheme(): MermaidPieTheme = pieTheme(
            colors = listOf(
                0xFFFFF4DD,
                0xFFF4DDFF,
                0xFFF7F9FF,
                0xFFFFE4AA,
                0xFFE4AAFF,
                0xFFC4D7FF,
                0xFFC6FFAA,
                0xFFFFAAC6,
                0xFFDDFFF4,
                0xFFA3FF77,
                0xFFFF77A3,
                0xFFAAFFE4,
            ),
            title = 0xFF333333,
            section = 0xFF333333,
            legend = 0xFF333333,
        )

        private fun neoPieTheme(dark: Boolean): MermaidPieTheme = if (dark) {
            pieTheme(
                colors = listOf(
                    0xFF1F2020,
                    0xFF474949,
                    0xFF201F1F,
                    0xFF060606,
                    0xFF2E3030,
                    0xFF060606,
                    0xFF060606,
                    0xFF060606,
                    0xFF201F20,
                    0xFF000000,
                    0xFF000000,
                    0xFF060606,
                ),
                title = 0xFFCCCCCC,
                section = 0xFFCCCCCC,
                legend = 0xFFCCCCCC,
            )
        } else {
            pieTheme(
                colors = listOf(
                    0xFFECECFE,
                    0xFFE9E9F1,
                    0xFFFFFFFF,
                    0xFFBCBCFB,
                    0xFFCACADD,
                    0xFFFCFCCF,
                    0xFFFBBCFB,
                    0xFFBCFBFB,
                    0xFFFEECEC,
                    0xFFF98BF9,
                    0xFF8BF9F9,
                    0xFFFBBCBC,
                ),
                title = 0xFF333333,
                section = 0xFF333333,
                legend = 0xFF333333,
            )
        }

        private fun reduxPieTheme(dark: Boolean): MermaidPieTheme =
            neoPieTheme(dark).copy(
                titleTextColor = if (dark) SceneColor(0xFFCCCCCC) else SceneColor(0xFF28253D),
                sectionTextColor = if (dark) SceneColor(0xFFCCCCCC) else SceneColor(0xFF28253D),
                legendTextColor = if (dark) SceneColor(0xFFCCCCCC) else SceneColor(0xFF28253D),
            )

        private fun reduxColorPieTheme(dark: Boolean): MermaidPieTheme = pieTheme(
            colors = listOf(
                0xFFF4A8FF,
                0xFF46ECD5,
                0xFFFFB86A,
                0xFFDAB2FF,
                0xFF7BF1A8,
                0xFFC4B4FF,
                0xFFFFA2A2,
                0xFFFFDF20,
                0xFFA3B3FF,
                0xFFBBF451,
                0xFF74D4FF,
                0xFFFFA1AD,
            ),
            title = if (dark) 0xFFCCCCCC else 0xFF28253D,
            section = 0xFF28253D,
            legend = if (dark) 0xFFCCCCCC else 0xFF28253D,
        )

        private fun pieTheme(
            colors: List<Long>,
            title: Long,
            section: Long,
            legend: Long,
        ): MermaidPieTheme = MermaidPieTheme(
            colors = colors.map(::SceneColor),
            titleTextColor = SceneColor(title),
            sectionTextColor = SceneColor(section),
            legendTextColor = SceneColor(legend),
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
