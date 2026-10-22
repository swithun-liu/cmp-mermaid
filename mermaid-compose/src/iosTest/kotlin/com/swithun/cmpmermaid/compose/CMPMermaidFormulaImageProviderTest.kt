@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidFormulaDisplayMode
import com.swithun.cmpmermaid.core.SceneColor
import kotlinx.cinterop.COpaquePointer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSData
import platform.Foundation.create
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

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

    @Test
    fun preservesEveryProviderErrorCodeAndGivesErrorsPriorityOverArtifacts() = runTest {
        for (code in CMPMermaidFormulaProviderErrorCode.entries) {
            val provider = CMPMermaidFormulaImageProvider { _, completion ->
                completion(
                    imageArtifact(),
                    CMPMermaidFormulaProviderError(code, "provider-$code"),
                )
            }
            val error = assertIs<GMResult.Err<MermaidFormulaError>>(
                provider.toMermaidFormulaRenderer().render(sampleRequest()),
            ).error
            when (code) {
                CMPMermaidFormulaProviderErrorCode.Unsupported -> assertIs<MermaidFormulaError.Unsupported>(error)
                CMPMermaidFormulaProviderErrorCode.RenderFailed -> assertIs<MermaidFormulaError.RenderFailed>(error)
                CMPMermaidFormulaProviderErrorCode.InvalidArtifact -> assertIs<MermaidFormulaError.InvalidArtifact>(error)
                CMPMermaidFormulaProviderErrorCode.ResolutionLimit -> assertIs<MermaidFormulaError.ResolutionLimit>(error)
            }
            assertEquals("provider-$code", error.message)
        }
    }

    @Test
    fun rejectsInvalidDoubleMetricsBeforeImageDecoding() = runTest {
        val original = imageArtifact()
        val invalid = listOf(
            original.copy(width = Double.NaN),
            original.copy(width = Double.POSITIVE_INFINITY),
            original.copy(width = Double.MAX_VALUE),
            original.copy(width = 0.0),
            original.copy(width = -1.0),
            original.copy(height = Double.NaN),
            original.copy(height = Double.MAX_VALUE),
            original.copy(height = 0.0),
            original.copy(baseline = Double.NaN),
            original.copy(baseline = -1.0),
            original.copy(baseline = 21.0),
        )
        for (artifact in invalid) {
            val provider = CMPMermaidFormulaImageProvider { _, completion -> completion(artifact, null) }
            val error = assertIs<MermaidFormulaError.InvalidArtifact>(
                assertIs<GMResult.Err<MermaidFormulaError>>(
                    provider.toMermaidFormulaRenderer().render(sampleRequest()),
                ).error,
            )
            assertEquals("Formula provider returned invalid logical dimensions or baseline", error.message)
        }
    }

    @Test
    fun invalidEncodedImageReturnsStructuredFailure() = runTest {
        val provider = CMPMermaidFormulaImageProvider { _, completion ->
            completion(imageArtifact(), null)
        }
        val error = assertIs<GMResult.Err<MermaidFormulaError>>(
            provider.toMermaidFormulaRenderer().render(sampleRequest()),
        ).error

        assertIs<MermaidFormulaError.InvalidArtifact>(error)
        assertTrue(error.message.isNotBlank())
    }

    @Test
    fun rejectsOversizedEncodedDimensionsBeforeDecodingPixels() = runTest {
        // The IHDR advertises 8192x8192 with a valid CRC; IDAT still holds two pixels.
        val provider = CMPMermaidFormulaImageProvider { _, completion ->
            completion(imageArtifact(pngData(OVERSIZED_PNG)), null)
        }

        val error = assertIs<MermaidFormulaError.ResolutionLimit>(
            assertIs<GMResult.Err<MermaidFormulaError>>(
                provider.toMermaidFormulaRenderer().render(sampleRequest()),
            ).error,
        )
        assertEquals("Formula image exceeds the 4000000 pixel limit", error.message)
    }

    @Test
    fun corruptPixelDataReturnsInvalidArtifactAndIsNotCached() = runTest {
        var calls = 0
        val renderer = CMPMermaidFormulaImageProvider { _, completion ->
            calls++
            completion(imageArtifact(pngData(CORRUPT_PNG)), null)
        }.toMermaidFormulaRenderer().cached()

        repeat(2) {
            val error = assertIs<MermaidFormulaError.InvalidArtifact>(
                assertIs<GMResult.Err<MermaidFormulaError>>(
                    renderer.render(sampleRequest()),
                ).error,
            )
            assertTrue(error.message.isNotBlank())
        }
        assertEquals(2, calls)
    }

    @Test
    fun validPngRetainsMetricsAndPixelsForRepeatedPainting() = runTest {
        val provider = CMPMermaidFormulaImageProvider { _, completion ->
            completion(imageArtifact(pngData(VALID_PNG)), null)
        }
        val artifact = assertIs<GMResult.Ok<MermaidFormulaArtifact>>(
            provider.toMermaidFormulaRenderer().render(sampleRequest()),
        ).value

        assertEquals(40f, artifact.width)
        assertEquals(20f, artifact.height)
        assertEquals(15f, artifact.baseline)
        assertEquals("x plus one", artifact.accessibilityText)
        repeat(2) {
            val target = ImageBitmap(2, 1)
            try {
                CanvasDrawScope().draw(
                    density = Density(1f),
                    layoutDirection = LayoutDirection.Ltr,
                    canvas = Canvas(target),
                    size = Size(2f, 1f),
                ) {
                    artifact.painter.draw(this, Rect(0f, 0f, 2f, 1f))
                }
                val pixels = IntArray(2)
                target.readPixels(pixels)
                assertEquals(listOf(0xff123456.toInt(), 0xffabcdef.toInt()), pixels.toList())
            } finally {
                target.asSkiaBitmap().close()
            }
        }
    }

    @Test
    fun lateCompletionAfterCancellationCannotPublishAResult() = runTest {
        var callback: ((CMPMermaidFormulaImageArtifact?, CMPMermaidFormulaProviderError?) -> Unit)? = null
        val renderer = CMPMermaidFormulaImageProvider { _, completion ->
            callback = completion
        }.toMermaidFormulaRenderer()
        val job = async(start = CoroutineStart.UNDISPATCHED) { renderer.render(sampleRequest()) }
        val completion = assertNotNull(callback)
        job.cancel()
        job.join()
        completion(null, CMPMermaidFormulaProviderError(
            CMPMermaidFormulaProviderErrorCode.RenderFailed, "late result",
        ))

        assertTrue(job.isCancelled)
    }

    @Test
    fun cancelledLateArtifactSkipsEncodedDataAccess() = runTest {
        var callback: ((CMPMermaidFormulaImageArtifact?, CMPMermaidFormulaProviderError?) -> Unit)? = null
        var byteReads = 0
        val data = object : NSData() {
            override fun length(): ULong = 1u

            override fun bytes(): COpaquePointer? {
                byteReads++
                throw IllegalStateException("cancelled artifact must not be decoded")
            }
        }
        val renderer = CMPMermaidFormulaImageProvider { _, completion ->
            callback = completion
        }.toMermaidFormulaRenderer()
        val job = async(start = CoroutineStart.UNDISPATCHED) { renderer.render(sampleRequest()) }
        val completion = assertNotNull(callback)
        job.cancel()
        job.join()

        completion(imageArtifact(data), null)

        assertTrue(job.isCancelled)
        assertEquals(0, byteReads)
    }

    @Test
    fun encodedDataExceptionsPreserveCancellationAndFatalErrors() = runTest {
        fun renderer(failure: Throwable): MermaidFormulaRenderer =
            CMPMermaidFormulaImageProvider { _, completion ->
                val data = object : NSData() {
                    override fun length(): ULong = 1u

                    override fun bytes(): COpaquePointer? = throw failure
                }
                completion(imageArtifact(data), null)
            }.toMermaidFormulaRenderer()

        val error = assertIs<MermaidFormulaError.InvalidArtifact>(
            assertIs<GMResult.Err<MermaidFormulaError>>(
                renderer(IllegalArgumentException("encoded data failure")).render(sampleRequest()),
            ).error,
        )
        assertEquals("encoded data failure", error.message)
        val cancelled = CancellationException("encoded data cancelled")
        assertSame(cancelled, assertFailsWith<CancellationException> {
            renderer(cancelled).render(sampleRequest())
        })
        val fatal = AssertionError("encoded data fatal")
        assertSame(fatal, assertFailsWith<AssertionError> {
            renderer(fatal).render(sampleRequest())
        })
    }

    @Test
    fun rejectsInvalidEncodedLengthsBeforeReadingBytes() = runTest {
        for (length in listOf(0uL, Int.MAX_VALUE.toULong() + 1u)) {
            var byteReads = 0
            val data = object : NSData() {
                override fun length(): ULong = length

                override fun bytes(): COpaquePointer? {
                    byteReads++
                    throw IllegalStateException("invalid length must not be read")
                }
            }
            val provider = CMPMermaidFormulaImageProvider { _, completion ->
                completion(imageArtifact(data), null)
            }
            val error = assertIs<MermaidFormulaError.InvalidArtifact>(
                assertIs<GMResult.Err<MermaidFormulaError>>(
                    provider.toMermaidFormulaRenderer().render(sampleRequest()),
                ).error,
            )
            assertEquals("Formula provider returned invalid PNG data length", error.message)
            assertEquals(0, byteReads)
        }
    }

    @Test
    fun synchronousProviderExceptionsUseTheResolverSafetyBoundary() = runTest {
        val failed = CMPMermaidFormulaImageProvider { _, _ ->
            throw IllegalArgumentException("provider failure")
        }.toMermaidFormulaRenderer()
        val error = assertIs<GMResult.Err<MermaidFormulaError>>(
            resolveMermaidFormula(sampleRequest(), failed),
        ).error
        assertIs<MermaidFormulaError.RenderFailed>(error)
        assertEquals("provider failure", error.message)
        assertFailsWith<CancellationException> {
            resolveMermaidFormula(sampleRequest(), CMPMermaidFormulaImageProvider { _, _ ->
                throw CancellationException("cancelled")
            }.toMermaidFormulaRenderer())
        }
    }

    private fun imageArtifact(data: NSData = NSData()) = CMPMermaidFormulaImageArtifact(
        pngData = data,
        width = 40.0,
        height = 20.0,
        baseline = 15.0,
        accessibilityText = "x plus one",
    )

    private fun pngData(encoded: String): NSData =
        assertNotNull(NSData.create(base64EncodedString = encoded, options = 0u))

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

    private companion object {
        const val VALID_PNG =
            "iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR4nGMQMgn7v/rs+/8AEPQFAtA9uzwAAAAASUVORK5CYII="
        const val OVERSIZED_PNG =
            "iVBORw0KGgoAAAANSUhEUgAAIAAAACAACAYAAAByqspZAAAAEUlEQVR4nGMQMgn7v/rs+/8AEPQFAtA9uzwAAAAASUVORK5CYII="
        // Zeroed compressed IDAT bytes with recomputed CRC; the remaining chunks are valid.
        const val CORRUPT_PNG =
            "iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVQAAAAAAAAAAAAAAAAAAAAAAHG03tMAAAAASUVORK5CYII="
    }
}
