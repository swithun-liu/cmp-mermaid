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
    fun draw(
        scope: DrawScope,
        bounds: Rect,
    )
}

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
 * diagrams/common/common.ts -> renderKatexSanitized/calculateMathMLDimensions.
 * The host substitutes typed Compose drawing content for browser MathML.
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
