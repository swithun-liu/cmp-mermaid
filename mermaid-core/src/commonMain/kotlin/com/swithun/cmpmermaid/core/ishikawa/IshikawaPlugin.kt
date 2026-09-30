package com.swithun.cmpmermaid.core.ishikawa

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidDiagramPlugin
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.ishikawa.upstream.mermaid.IshikawaParser

class IshikawaPlugin : MermaidDiagramPlugin {
    override val id: String = "ishikawa"
    override val headers: Set<String> = setOf("ishikawa", "ishikawa-beta")

    // Mermaid.js 12.0.0: diagrams/ishikawa/ishikawaDetector.ts -> detector.
    override fun detect(source: String): Boolean = DETECTOR.containsMatchIn(source)

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = IshikawaParser(
            options = context.options,
            diagramTitle = context.diagramTitle,
            lineOffset = context.frontmatterLineOffset,
        ).parse(source)
    ) {
        is GMResult.Ok -> IshikawaLayout().layout(parsed.value, context)
        is GMResult.Err -> parsed
    }

    private companion object {
        val DETECTOR = Regex("""^\s*ishikawa(?:-beta)?\b""", RegexOption.IGNORE_CASE)
    }
}
