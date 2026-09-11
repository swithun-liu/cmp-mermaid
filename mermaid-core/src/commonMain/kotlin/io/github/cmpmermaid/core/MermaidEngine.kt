package io.github.cmpmermaid.core

import io.github.cmpmermaid.core.flowchart.FlowchartPlugin

data class MermaidRenderContext(
    val textMetrics: TextMetricProvider,
    val theme: MermaidTheme = MermaidTheme(),
    val options: MermaidRenderOptions = MermaidRenderOptions(),
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
        val normalized = stripFrontMatter(source)
        val header = normalized
            .lineSequence()
            .map(String::trim)
            .firstOrNull { it.isNotEmpty() && !it.startsWith("%%") }
            ?.substringBefore(' ')
            ?.lowercase()
            .orEmpty()

        val plugin = pluginsByHeader[header]
            ?: return GMResult.Err(MermaidError.UnsupportedDiagram(header.ifEmpty { "<empty>" }))

        return plugin.compile(normalized, context)
    }

    private fun stripFrontMatter(source: String): String {
        val lines = source.lines()
        val firstContent = lines.indexOfFirst { it.isNotBlank() }
        if (firstContent == -1 || lines[firstContent].trim() != "---") {
            return source
        }

        val closing = (firstContent + 1 until lines.size)
            .firstOrNull { lines[it].trim() == "---" }
            ?: return source

        return lines.drop(closing + 1).joinToString("\n")
    }
}
