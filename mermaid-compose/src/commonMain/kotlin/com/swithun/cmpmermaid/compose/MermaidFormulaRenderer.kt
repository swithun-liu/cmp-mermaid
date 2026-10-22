package com.swithun.cmpmermaid.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidFormulaDisplayMode
import com.swithun.cmpmermaid.core.MermaidFormulaMeasureRequest
import com.swithun.cmpmermaid.core.MermaidFormulaMetrics
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class MermaidFormulaRenderingMode {
    Automatic,
    PlainText,
}

/**
 * Intrinsic formula render request.
 *
 * [maxWidth] and [fontSize] are computed Mermaid CSS pixels expressed as scene logical pixels.
 * [maxWidth] constrains formula content; it does not ask the provider to fill that width.
 * [rasterScale] is physical bitmap pixels per logical pixel and must not change logical metrics.
 */
data class MermaidFormulaRequest(
    val source: String,
    val displayMode: MermaidFormulaDisplayMode,
    val maxWidth: Float,
    val fontSize: Float,
    val color: SceneColor,
    val rasterScale: Float,
    val accessibilityText: String,
)

sealed interface MermaidFormulaError {
    val message: String

    data class Unsupported(
        override val message: String,
    ) : MermaidFormulaError

    data class RenderFailed(
        override val message: String,
    ) : MermaidFormulaError

    data class InvalidArtifact(
        override val message: String,
    ) : MermaidFormulaError

    data class ResolutionLimit(
        override val message: String,
    ) : MermaidFormulaError
}

fun interface MermaidFormulaPainter {
    /**
     * Draws the intrinsic formula content into [bounds].
     *
     * CMP Mermaid supplies a box whose logical width and height equal the
     * [MermaidFormulaArtifact] dimensions. Bitmap-backed implementations must convert their
     * physical pixels by [MermaidFormulaRequest.rasterScale] and preserve the content aspect
     * ratio. Mermaid's wrapping width, edge-label background, and other outer layout are owned
     * by the translated diagram stage and are not part of this paint box.
     */
    fun draw(
        scope: DrawScope,
        bounds: Rect,
    )
}

/**
 * Provider-owned intrinsic formula content.
 *
 * [width], [height], and [baseline] use Mermaid scene logical pixels and describe the
 * provider's own formula layout box, equivalent to the KaTeX `<math>` box rather than the
 * surrounding `createText.ts -> addHtmlSpan` div. CMP Mermaid applies diagram-specific outer
 * width, alignment, and background padding after measurement.
 */
data class MermaidFormulaArtifact(
    val width: Float,
    val height: Float,
    val baseline: Float,
    val accessibilityText: String,
    val painter: MermaidFormulaPainter,
)

/**
 * Platform-neutral formula extension point.
 *
 * Mermaid.js 12.0.0:
 * diagrams/common/common.ts -> renderKatexSanitized and
 * rendering-util/createText.ts -> addHtmlSpan.
 * The host substitutes intrinsic typed Compose drawing content for browser MathML. The
 * translated diagram stage retains responsibility for the surrounding HTML-label layout box.
 */
fun interface MermaidFormulaRenderer {
    suspend fun render(
        request: MermaidFormulaRequest,
    ): GMResult<MermaidFormulaArtifact, MermaidFormulaError>
}

@Composable
internal fun rememberBuiltInMermaidFormulaRenderer(): MermaidFormulaRenderer? = null

internal fun selectMermaidFormulaRenderer(
    hostRenderer: MermaidFormulaRenderer?,
    builtInRenderer: MermaidFormulaRenderer?,
    mode: MermaidFormulaRenderingMode,
): MermaidFormulaRenderer? = when (mode) {
    MermaidFormulaRenderingMode.Automatic -> hostRenderer ?: builtInRenderer
    MermaidFormulaRenderingMode.PlainText -> null
}

internal fun MermaidFormulaRenderer.cached(): MermaidFormulaRenderer =
    CachingMermaidFormulaRenderer(this)

private class CachingMermaidFormulaRenderer(
    private val delegate: MermaidFormulaRenderer,
) : MermaidFormulaRenderer {
    private val mutex = Mutex()
    private val cache =
        linkedMapOf<MermaidFormulaRequest, MermaidFormulaArtifact>()

    override suspend fun render(
        request: MermaidFormulaRequest,
    ): GMResult<MermaidFormulaArtifact, MermaidFormulaError> = mutex.withLock {
        cache[request]?.let { artifact ->
            return@withLock GMResult.Ok(artifact)
        }
        delegate.render(request).also { result ->
            if (result is GMResult.Ok && result.value.hasValidMetrics()) {
                if (cache.size >= MAX_FORMULA_CACHE_ENTRIES) {
                    cache.keys.firstOrNull()?.let(cache::remove)
                }
                cache[request] = result.value
            }
        }
    }
}

internal suspend fun resolveMermaidFormula(
    request: MermaidFormulaRequest,
    renderer: MermaidFormulaRenderer,
): GMResult<MermaidFormulaArtifact, MermaidFormulaError> {
    val result = try {
        renderer.render(request)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        GMResult.Err(
            MermaidFormulaError.RenderFailed(
                failure.message?.takeIf(String::isNotBlank)
                    ?: "Formula renderer failed with ${failure::class.simpleName ?: "Exception"}",
            ),
        )
    }
    return when (result) {
        is GMResult.Err -> result
        is GMResult.Ok -> if (result.value.hasValidMetrics()) {
            result
        } else {
            GMResult.Err(
                MermaidFormulaError.InvalidArtifact(
                    "Formula renderer returned invalid logical dimensions or baseline",
                ),
            )
        }
    }
}

internal fun MermaidFormulaArtifact.metrics(): MermaidFormulaMetrics =
    MermaidFormulaMetrics(
        width = width,
        height = height,
        baseline = baseline,
    )

internal fun SceneText.formulaRequest(): MermaidFormulaRequest? {
    val formula = formula ?: return null
    return formula.toRequest(color)
}

internal fun SceneText.formulaRequests(): List<MermaidFormulaRequest> =
    formulaLayout?.items?.mapNotNull { item ->
        item.formula?.toRequest(item.spans.lastOrNull { it.color != null }?.color ?: color)
    } ?: listOfNotNull(formulaRequest())

internal fun MermaidFormulaMeasureRequest.toRequest(
    color: SceneColor,
): MermaidFormulaRequest = MermaidFormulaRequest(
    source = source,
    displayMode = displayMode,
    maxWidth = maxWidth,
    fontSize = fontSize,
    color = color,
    rasterScale = rasterScale,
    accessibilityText = accessibilityText,
)

private fun MermaidFormulaArtifact.hasValidMetrics(): Boolean =
    width.isFinite() &&
        width > 0f &&
        height.isFinite() &&
        height > 0f &&
        baseline.isFinite() &&
        baseline in 0f..height

private const val MAX_FORMULA_CACHE_ENTRIES = 256
