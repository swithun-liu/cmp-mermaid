package io.github.cmpmermaid.core.pie

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.pie.upstream.mermaid.PieParser

class PiePlugin : MermaidDiagramPlugin {
    override val id: String = "pie"
    override val headers: Set<String> = setOf("pie")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = PieParser(
            diagramTitle = context.diagramTitle,
            lineOffset = context.frontmatterLineOffset,
        ).parse(source)
    ) {
        is GMResult.Ok -> PieLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
