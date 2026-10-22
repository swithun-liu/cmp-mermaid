package com.swithun.cmpmermaid.compose

import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidFormulaDisplayMode
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Image
import org.jetbrains.skia.makeFromEncoded
import platform.Foundation.NSData
import kotlin.coroutines.resume

enum class CMPMermaidFormulaDisplayMode {
    Block,
}

enum class CMPMermaidFormulaProviderErrorCode {
    Unsupported,
    RenderFailed,
    InvalidArtifact,
    ResolutionLimit,
}

data class CMPMermaidFormulaImageRequest(
    val source: String,
    val displayMode: CMPMermaidFormulaDisplayMode,
    val maxWidth: Double,
    val fontSize: Double,
    val colorArgb: Long,
    val rasterScale: Double,
    val accessibilityText: String,
)

data class CMPMermaidFormulaImageArtifact(
    val pngData: NSData,
    val width: Double,
    val height: Double,
    val baseline: Double,
    val accessibilityText: String,
)

data class CMPMermaidFormulaProviderError(
    val code: CMPMermaidFormulaProviderErrorCode,
    val message: String,
)

/**
 * Objective-C/Swift bridge for host-provided formula rendering on iOS.
 *
 * Geometry uses Mermaid scene logical pixels. [CMPMermaidFormulaImageArtifact.pngData] contains
 * the provider's intrinsic formula image at the requested raster scale, without diagram-specific
 * outer padding or background. The provider must invoke [completion] exactly once.
 */
fun interface CMPMermaidFormulaImageProvider {
    fun render(
        request: CMPMermaidFormulaImageRequest,
        completion: (
            artifact: CMPMermaidFormulaImageArtifact?,
            error: CMPMermaidFormulaProviderError?,
        ) -> Unit,
    )
}

internal fun CMPMermaidFormulaImageProvider.toMermaidFormulaRenderer(): MermaidFormulaRenderer =
    MermaidFormulaRenderer { request ->
        suspendCancellableCoroutine { continuation ->
            render(request.toImageRequest()) { artifact, error ->
                val result = when {
                    error != null -> GMResult.Err(error.toMermaidFormulaError())
                    artifact == null -> GMResult.Err(
                        MermaidFormulaError.InvalidArtifact(
                            "Formula provider returned neither an artifact nor an error",
                        ),
                    )
                    else -> artifact.toMermaidFormulaArtifact()
                }
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }
        }
    }

private fun MermaidFormulaRequest.toImageRequest(): CMPMermaidFormulaImageRequest =
    CMPMermaidFormulaImageRequest(
        source = source,
        displayMode = when (displayMode) {
            MermaidFormulaDisplayMode.Block -> CMPMermaidFormulaDisplayMode.Block
        },
        maxWidth = maxWidth.toDouble(),
        fontSize = fontSize.toDouble(),
        colorArgb = color.argb,
        rasterScale = rasterScale.toDouble(),
        accessibilityText = accessibilityText,
    )

private fun CMPMermaidFormulaImageArtifact.toMermaidFormulaArtifact():
    GMResult<MermaidFormulaArtifact, MermaidFormulaError> {
    val logicalWidth = width.toFloat()
    val logicalHeight = height.toFloat()
    val logicalBaseline = baseline.toFloat()
    if (
        !logicalWidth.isFinite() || logicalWidth <= 0f ||
        !logicalHeight.isFinite() || logicalHeight <= 0f ||
        !logicalBaseline.isFinite() || logicalBaseline !in 0f..logicalHeight
    ) {
        return GMResult.Err(
            MermaidFormulaError.InvalidArtifact(
                "Formula provider returned invalid logical dimensions or baseline",
            ),
        )
    }
    val image = try {
        Image.makeFromEncoded(pngData).toComposeImageBitmap()
    } catch (failure: Exception) {
        return GMResult.Err(
            MermaidFormulaError.InvalidArtifact(
                failure.message?.takeIf(String::isNotBlank)
                    ?: "Formula provider returned invalid PNG data",
            ),
        )
    }
    if (image.width.toLong() * image.height.toLong() > MAX_FORMULA_IMAGE_PIXELS) {
        return GMResult.Err(
            MermaidFormulaError.ResolutionLimit(
                "Formula image exceeds the $MAX_FORMULA_IMAGE_PIXELS pixel limit",
            ),
        )
    }
    return GMResult.Ok(
        MermaidFormulaArtifact(
            width = logicalWidth,
            height = logicalHeight,
            baseline = logicalBaseline,
            accessibilityText = accessibilityText,
            painter = MermaidFormulaPainter { scope, bounds ->
                scope.drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(bounds.left, bounds.top)
                    canvas.scale(
                        bounds.width / image.width,
                        bounds.height / image.height,
                    )
                    canvas.drawImage(
                        image = image,
                        topLeftOffset = androidx.compose.ui.geometry.Offset.Zero,
                        paint = androidx.compose.ui.graphics.Paint(),
                    )
                    canvas.restore()
                }
            },
        ),
    )
}

private fun CMPMermaidFormulaProviderError.toMermaidFormulaError(): MermaidFormulaError =
    when (code) {
        CMPMermaidFormulaProviderErrorCode.Unsupported ->
            MermaidFormulaError.Unsupported(message)
        CMPMermaidFormulaProviderErrorCode.RenderFailed ->
            MermaidFormulaError.RenderFailed(message)
        CMPMermaidFormulaProviderErrorCode.InvalidArtifact ->
            MermaidFormulaError.InvalidArtifact(message)
        CMPMermaidFormulaProviderErrorCode.ResolutionLimit ->
            MermaidFormulaError.ResolutionLimit(message)
    }

private const val MAX_FORMULA_IMAGE_PIXELS = 4_000_000L
