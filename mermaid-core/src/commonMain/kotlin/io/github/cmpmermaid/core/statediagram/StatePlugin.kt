package io.github.cmpmermaid.core.statediagram

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.statediagram.upstream.mermaid.StateJisonParser

class StatePlugin : MermaidDiagramPlugin {
    override val id: String = "state"
    override val headers: Set<String> = setOf("statediagram", "statediagram-v2")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = StateJisonParser(
            diagramTitle = context.diagramTitle,
        ).parse(source)
    ) {
        is GMResult.Ok -> StateLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
