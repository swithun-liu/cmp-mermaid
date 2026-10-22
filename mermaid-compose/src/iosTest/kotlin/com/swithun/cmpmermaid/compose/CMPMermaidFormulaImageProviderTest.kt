package com.swithun.cmpmermaid.compose

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidFormulaDisplayMode
import com.swithun.cmpmermaid.core.SceneColor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CMPMermaidFormulaImageProviderTest {
    @Test
    fun forwardsRequestAndMapsProviderError() = runTest {
        var capturedRequest: CMPMermaidFormulaImageRequest? = null
        val provider = CMPMermaidFormulaImageProvider { request, completion ->
            capturedRequest = request
            completion(
                null,
                CMPMermaidFormulaProviderError(
                    code = CMPMermaidFormulaProviderErrorCode.Unsupported,
                    message = "unsupported formula",
                ),
            )
        }

        val result = provider.toMermaidFormulaRenderer().render(sampleRequest())

        val error = assertIs<GMResult.Err<MermaidFormulaError>>(result).error
        assertIs<MermaidFormulaError.Unsupported>(error)
        assertEquals("unsupported formula", error.message)
        with(requireNotNull(capturedRequest)) {
            assertEquals("x + 1", source)
            assertEquals(CMPMermaidFormulaDisplayMode.Block, displayMode)
            assertEquals(320.0, maxWidth)
            assertEquals(16.0, fontSize)
            assertEquals(0xff123456, colorArgb)
            assertEquals(2.0, rasterScale)
            assertEquals("x plus one", accessibilityText)
        }
    }

    @Test
    fun rejectsMissingArtifactAndError() = runTest {
        val provider = CMPMermaidFormulaImageProvider { _, completion ->
            completion(null, null)
        }

        val result = provider.toMermaidFormulaRenderer().render(sampleRequest())

        val error = assertIs<GMResult.Err<MermaidFormulaError>>(result).error
        assertIs<MermaidFormulaError.InvalidArtifact>(error)
    }

    private fun sampleRequest(): MermaidFormulaRequest =
        MermaidFormulaRequest(
            source = "x + 1",
            displayMode = MermaidFormulaDisplayMode.Block,
            maxWidth = 320f,
            fontSize = 16f,
            color = SceneColor(0xff123456),
            rasterScale = 2f,
            accessibilityText = "x plus one",
        )
}
