package com.swithun.cmpmermaid.core.block

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidDiagramPlugin
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.block.upstream.mermaid.BlockJisonParser

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/diagrams/block/blockDetector.ts and blockDiagram.ts.
 */
class BlockPlugin : MermaidDiagramPlugin {
    override val id: String = "block"
    override val headers: Set<String> = setOf("block", "block-beta")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val parsed = try {
            BlockJisonParser(
                config = context.options,
                diagramTitle = context.diagramTitle,
                lineOffset = context.frontmatterLineOffset,
            ).parse(source)
        } catch (failure: Throwable) {
            return GMResult.Err(
                MermaidError.Parse(
                    line = context.frontmatterLineOffset + 1,
                    column = 1,
                    message = "Block parser failed: ${failure.message ?: "Unknown failure"}",
                ),
            )
        }
        return when (parsed) {
            is GMResult.Ok -> try {
                BlockLayout().layout(parsed.value, context)
            } catch (failure: Throwable) {
                GMResult.Err(
                    MermaidError.Layout(
                        "Block layout failed: ${failure.message ?: "Unknown failure"}",
                    ),
                )
            }
            is GMResult.Err -> parsed
        }
    }
}
