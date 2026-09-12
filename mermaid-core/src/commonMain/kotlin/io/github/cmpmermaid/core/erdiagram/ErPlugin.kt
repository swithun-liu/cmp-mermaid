package io.github.cmpmermaid.core.erdiagram

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.erdiagram.upstream.mermaid.ErJisonParser

class ErPlugin : MermaidDiagramPlugin {
    override val id: String = "er"
    override val headers: Set<String> = setOf("erdiagram")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = ErJisonParser(
            diagramTitle = context.diagramTitle,
        ).parse(source)
    ) {
        is GMResult.Ok -> ErLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
