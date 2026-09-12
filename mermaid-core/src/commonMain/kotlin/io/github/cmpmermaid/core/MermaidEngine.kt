package io.github.cmpmermaid.core

import io.github.cmpmermaid.core.classdiagram.ClassPlugin
import io.github.cmpmermaid.core.erdiagram.ErPlugin
import io.github.cmpmermaid.core.flowchart.FlowchartPlugin
import io.github.cmpmermaid.core.sequence.SequencePlugin
import io.github.cmpmermaid.core.statediagram.StatePlugin

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
        val textSize = preprocessed.code.cleaned.length
        if (textSize > resolvedOptions.maxTextSize) {
            return GMResult.Err(
                MermaidError.ResourceLimit(
                    resource = "maxTextSize",
                    actual = textSize,
                    maximum = resolvedOptions.maxTextSize,
                    message = "Maximum text size in diagram exceeded " +
                        "($textSize > ${resolvedOptions.maxTextSize})",
                ),
            )
        }
        val baseTheme = when (val themeName = resolvedOptions.themeName) {
            null -> context.theme
            "null" -> MermaidTheme.MermaidDefault
            else -> MermaidTheme.named(themeName) ?: context.theme
        }
        val resolvedThemeVariables = if (
            "fontFamily" in resolvedOptions.themeVariables ||
            resolvedOptions.fontFamily == null
        ) {
            resolvedOptions.themeVariables
        } else {
            resolvedOptions.themeVariables + ("fontFamily" to resolvedOptions.fontFamily)
        }
        val resolvedTheme = when (
            val result = MermaidTheme.withVariables(
                theme = baseTheme,
                values = resolvedThemeVariables,
                colorArrays = resolvedOptions.themeColorArrays,
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
                options = resolvedOptions,
                diagramTitle = preprocessed.title,
                frontmatterLineOffset = preprocessed.code.frontmatterLineOffset,
            ),
        )
    }
}
