package io.github.cmpmermaid.core

import io.github.cmpmermaid.core.flowchart.FlowchartPlugin

data class MermaidRenderContext(
    val textMetrics: TextMetricProvider,
    val theme: MermaidTheme = MermaidTheme(),
    val options: MermaidRenderOptions = MermaidRenderOptions(),
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
    plugins: List<MermaidDiagramPlugin> = listOf(FlowchartPlugin()),
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
        val resolvedOptions = when (val result = preprocessed.config.applyTo(context.options)) {
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

        val plugin = pluginsByHeader[header]
            ?: return GMResult.Err(MermaidError.UnsupportedDiagram(header.ifEmpty { "<empty>" }))

        val parserSource = MermaidPreprocessor.encodeEntities(preprocessed.code.cleaned) + "\n"
        return plugin.compile(
            source = parserSource,
            context = context.copy(
                options = resolvedOptions,
                diagramTitle = preprocessed.title,
                frontmatterLineOffset = preprocessed.code.frontmatterLineOffset,
            ),
        )
    }
}
