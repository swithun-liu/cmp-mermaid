package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.SceneTextWeight
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import com.swithun.cmpmermaid.core.TextMetricsRequest
import kotlinx.coroutines.CancellationException
import kotlin.math.floor

/** Mermaid.js 12.0.0: utils.ts -> calculateTextWidth/calculateTextDimensions. */
internal object MermaidTextDimensionsPort {
    private val lineBreakRegex = Regex("""</?br\s*/?>""", RegexOption.IGNORE_CASE)
    private val svgWhitespace = Regex("""[ \t\r\n\u000C]+""")

    fun calculateTextWidth(
        text: String,
        config: MermaidRenderOptions,
        metrics: TextMetricProvider,
    ): GMResult<Float, MermaidError> = when (
        val result = calculateTextDimensions(text, config, metrics)
    ) {
        is GMResult.Ok -> GMResult.Ok(result.value.width)
        is GMResult.Err -> result
    }

    private fun calculateTextDimensions(
        text: String,
        config: MermaidRenderOptions,
        metrics: TextMetricProvider,
    ): GMResult<TextDimensions, MermaidError> {
        if (text.isEmpty()) return GMResult.Ok(TextDimensions(0f, 0f, 0f))
        val fontSize = config.fontSize ?: 12f
        val fontFamily = config.fontFamily ?: "Arial"
        val lines = text.split(lineBreakRegex)
        val dimensions = mutableListOf<TextDimensions>()
        return try {
            for (family in listOf("sans-serif", fontFamily)) {
                var width = 0f
                var height = 0f
                var lineHeight = 0f
                for (line in lines) {
                    val measured = when (
                        val result = drawSimpleText(
                            line.ifEmpty { "\u200B" }, fontSize, family, metrics,
                        )
                    ) {
                        is GMResult.Ok -> result.value
                        is GMResult.Err -> return result
                    }
                    width = jsRound(maxOf(width, measured.width))
                    val currentHeight = jsRound(measured.height)
                    height += currentHeight
                    lineHeight = jsRound(maxOf(lineHeight, currentHeight))
                }
                dimensions += TextDimensions(width, height, lineHeight)
            }
            val first = dimensions[0]
            val second = dimensions[1]
            val useFirst = first.height > second.height &&
                first.width > second.width &&
                first.lineHeight > second.lineHeight
            GMResult.Ok(if (useFirst) first else second)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            GMResult.Err(MermaidError.Unexpected.from(failure, "SVG text dimension measurement failed"))
        }
    }

    // Mermaid.js 12.0.0: utils.ts -> drawSimpleText; browser SVG whitespace
    // normalization is projected before the native glyph measurement boundary.
    private fun drawSimpleText(
        text: String,
        fontSize: Float,
        fontFamily: String,
        metrics: TextMetricProvider,
    ): GMResult<TextMetrics, MermaidError> = try {
        val measured = metrics.measure(TextMetricsRequest(
            text = text.replace(lineBreakRegex, " ").replace(svgWhitespace, " ")
                .trim(' ', '\t', '\r', '\n', '\u000C'),
            fontSize = fontSize,
            maxWidth = Float.POSITIVE_INFINITY,
            lineHeight = 1f,
            fontFamily = fontFamily,
            weight = SceneTextWeight.Normal,
            horizontalScale = 1f,
            // Request subpixel glyph metrics at this platform boundary; the SVG
            // whitespace semantics have already been applied above.
            htmlLabel = true,
        ))
        if (
            !measured.width.isFinite() || !measured.height.isFinite() ||
            measured.width < 0f || measured.height < 0f ||
            (measured.width == 0f && measured.height == 0f)
        ) {
            GMResult.Err(MermaidError.Layout(
                "Text dimension adapter returned an invalid SVG text box",
            ))
        } else {
            GMResult.Ok(measured)
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        GMResult.Err(MermaidError.Unexpected.from(failure, "SVG text dimension measurement failed"))
    }

    private fun jsRound(value: Float): Float = floor(value + 0.5f)

    private data class TextDimensions(val width: Float, val height: Float, val lineHeight: Float)
}
