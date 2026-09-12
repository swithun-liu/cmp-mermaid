package io.github.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidPreprocessorTest {
    @Test
    fun portsFrontmatterExtractionAndFlowchartConfig() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                title: Configured graph
                config:
                  flowchart:
                    padding: 24
                    minNodeWidth: 180
                    nodeSpacing: 72
                    rankSpacing: 84
                    curve: linear
                    inheritDir: true
                ---
                flowchart TB
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value
        assertEquals("Configured graph", processed.title)
        assertEquals("flowchart TB\n  A --> B", processed.code.cleaned)
        assertEquals(11, processed.code.frontmatterLineOffset)
        assertEquals(24f, options.flowchartPadding)
        assertEquals(180f, options.minNodeWidth)
        assertEquals(72f, options.nodeSpacing)
        assertEquals(84f, options.rankSpacing)
        assertEquals("linear", options.curve)
        assertEquals(true, options.inheritDirection)
    }

    @Test
    fun directiveOverridesFrontmatterLikeCleanAndMerge() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    padding: 20
                    curve: basis
                ---
                %%{init: {'flowchart': {'padding': 32, 'curve': 'step'}}}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value
        assertEquals(32f, options.flowchartPadding)
        assertEquals("step", options.curve)
        assertEquals("flowchart LR\n  A --> B", processed.code.cleaned)
    }

    @Test
    fun stripsSecureMaxEdgesFromDiagramConfig() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  maxEdges: 999
                ---
                %%{init: {'maxEdges': 1000}}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions(maxEdges = 7)),
        ).value
        assertEquals(7, options.maxEdges)
    }

    @Test
    fun returnsStructuredErrorForInvalidConfigType() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    padding: wide
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.Configuration>(error)
        assertContains(error.message, "flowchart.padding")
    }

    @Test
    fun rejectsKnownConfigurationWithoutANativePort() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  theme: forest
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.UnsupportedFeature>(error)
        assertContains(error.message, "theme")
    }
}
