package io.github.cmpmermaid.core.classdiagram

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidDiagramPlugin
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.classdiagram.upstream.mermaid.ClassJisonParser

class ClassPlugin : MermaidDiagramPlugin {
    override val id: String = "class"
    override val headers: Set<String> = setOf("classdiagram", "classdiagram-v2")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = ClassJisonParser(
            securityLevel = context.options.securityLevel,
            diagramTitle = context.diagramTitle,
        ).parse(source)
    ) {
        is GMResult.Ok -> ClassLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }
}
