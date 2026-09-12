package io.github.cmpmermaid.core.gantt

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttJisonParser

class GanttPlugin : MermaidDiagramPlugin {
    override val id: String = "gantt"
    override val headers: Set<String> = setOf("gantt")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = GanttJisonParser(
            diagramTitle = context.diagramTitle,
            securityLevel = context.options.securityLevel,
            defaultWeekday = context.options.ganttWeekday,
        ).parse(source)
    ) {
        is GMResult.Ok -> GanttLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
