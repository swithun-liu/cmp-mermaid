package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene

class FlowchartPlugin : MermaidDiagramPlugin {
    override val id: String = "flowchart"
    override val headers: Set<String> = setOf("flowchart", "flowchart-elk", "graph")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (val parsed = FlowchartParser().parse(source)) {
        is GMResult.Ok -> FlowchartLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
