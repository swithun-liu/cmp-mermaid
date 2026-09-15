package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.classdiagram.ClassPlugin
import com.swithun.cmpmermaid.core.erdiagram.ErPlugin
import com.swithun.cmpmermaid.core.flowchart.FlowchartPlugin
import com.swithun.cmpmermaid.core.gantt.GanttPlugin
import com.swithun.cmpmermaid.core.gitgraph.GitGraphPlugin
import com.swithun.cmpmermaid.core.journey.JourneyPlugin
import com.swithun.cmpmermaid.core.pie.PiePlugin
import com.swithun.cmpmermaid.core.requirement.RequirementPlugin
import com.swithun.cmpmermaid.core.sequence.SequencePlugin
import com.swithun.cmpmermaid.core.statediagram.StatePlugin
import com.swithun.cmpmermaid.core.xychart.XyChartPlugin

data class MermaidRenderContext(
    val textMetrics: TextMetricProvider,
    val theme: MermaidTheme = MermaidTheme.FlowchartDefault,
    val options: MermaidRenderOptions = MermaidRenderOptions(),
    val assetMetrics: Map<String, SceneSize> = emptyMap(),
    internal val diagramTitle: String? = null,
    internal val frontmatterLineOffset: Int = 0,
)

interface MermaidDiagramPlugin {
    val id: String
    val headers: Set<String>

    fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError>
}

class MermaidEngine(
    plugins: List<MermaidDiagramPlugin> = listOf(
        FlowchartPlugin(),
        SequencePlugin(),
        ClassPlugin(),
        StatePlugin(),
        ErPlugin(),
        GanttPlugin(),
        PiePlugin(),
        XyChartPlugin(),
        JourneyPlugin(),
        RequirementPlugin(),
        GitGraphPlugin(),
    ),
) {
    private val pluginsByHeader: Map<String, MermaidDiagramPlugin> = buildMap {
        plugins.forEach { plugin ->
            plugin.headers.forEach { header ->
                put(header.lowercase(), plugin)
            }
        }
    }

    fun render(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val preprocessed = when (val result = MermaidPreprocessor.preprocess(source)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val header = preprocessed.code.cleaned
            .lineSequence()
            .map(String::trim)
            .firstOrNull(String::isNotEmpty)
            ?.substringBefore(' ')
            ?.lowercase()
            .orEmpty()
        val detectedOptions = when (header) {
            "flowchart-elk" -> context.options.copy(layout = "elk")
            else -> context.options
        }
        val resolvedOptions = when (val result = preprocessed.config.applyTo(detectedOptions)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val diagramOptions = if (header == "requirementdiagram" || header == "requirement") {
            resolvedOptions.copy(
                themeName = resolvedOptions.requirementThemeName ?: resolvedOptions.themeName,
                look = resolvedOptions.requirementLook ?: resolvedOptions.look,
            )
        } else {
            resolvedOptions
        }
        if (diagramOptions.look == "handDrawn") {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = "handDrawn look",
                    message = "Native Mermaid has not translated Mermaid's roughjs handDrawn renderer",
                ),
            )
        }
        val textSize = preprocessed.code.cleaned.length
        if (textSize > diagramOptions.maxTextSize) {
            return GMResult.Err(
                MermaidError.ResourceLimit(
                    resource = "maxTextSize",
                    actual = textSize,
                    maximum = diagramOptions.maxTextSize,
                    message = "Maximum text size in diagram exceeded " +
                        "($textSize > ${diagramOptions.maxTextSize})",
                ),
            )
        }
        val baseTheme = when (val themeName = diagramOptions.themeName) {
            null -> context.theme
            "null" -> MermaidTheme.MermaidDefault
            else -> MermaidTheme.named(themeName) ?: context.theme
        }
        val resolvedThemeVariables = if (
            "fontFamily" in diagramOptions.themeVariables ||
            diagramOptions.fontFamily == null
        ) {
            diagramOptions.themeVariables
        } else {
            diagramOptions.themeVariables + ("fontFamily" to diagramOptions.fontFamily)
        }
        val resolvedTheme = when (
            val result = MermaidTheme.withVariables(
                theme = baseTheme,
                values = resolvedThemeVariables,
                colorArrays = diagramOptions.themeColorArrays,
                themeName = diagramOptions.themeName,
            )
        ) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }

        val plugin = pluginsByHeader[header]
            ?: return GMResult.Err(MermaidError.UnsupportedDiagram(header.ifEmpty { "<empty>" }))

        val parserSource = MermaidPreprocessor.encodeEntities(preprocessed.code.cleaned) + "\n"
        return plugin.compile(
            source = parserSource,
            context = context.copy(
                theme = resolvedTheme,
                options = diagramOptions,
                diagramTitle = preprocessed.title,
                frontmatterLineOffset = preprocessed.code.frontmatterLineOffset,
            ),
        )
    }
}
