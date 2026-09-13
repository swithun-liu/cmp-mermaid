package io.github.cmpmermaid.core.xychart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.xychart.upstream.mermaid.XyJisonParser

class XyChartPlugin : MermaidDiagramPlugin {
    override val id: String = "xychart"
    override val headers: Set<String> = setOf("xychart", "xychart-beta")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val parsed = when (
            val result = XyJisonParser(
                diagramTitle = context.diagramTitle,
                plotColorPalette = context.theme.xyChart.plotColorPalette,
                initialOrientation = context.options.xyChart.chartOrientation,
                lineOffset = context.frontmatterLineOffset,
            ).parse(source)
        ) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val data = when (val result = parsed.compile()) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        return XyChartLayout().layout(data, context)
    }
}
