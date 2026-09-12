package io.github.cmpmermaid.core.sequence

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.sequence.upstream.mermaid.SequenceJisonParser

class SequencePlugin : MermaidDiagramPlugin {
    override val id: String = "sequence"
    override val headers: Set<String> = setOf("sequencediagram")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = SequenceJisonParser(
            diagramTitle = context.diagramTitle,
        ).parse(source)
    ) {
        is GMResult.Ok -> SequenceLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
