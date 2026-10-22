package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.MermaidFormulaLabelItem
import com.swithun.cmpmermaid.core.MermaidFormulaLabelLayout
import com.swithun.cmpmermaid.core.MermaidFormulaMeasureRequest
import com.swithun.cmpmermaid.core.MermaidFormulaMetrics
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneTextSpan
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import com.swithun.cmpmermaid.core.TextMetricsRequest

/**
 * Mermaid.js 12.0.0: common.ts -> renderKatexUnsanitized.
 *
 * Platform adaptation of the emitted CSS flex container, not a replacement
 * Mermaid layout algorithm. Each direct formula is a flex item; intervening
 * text is an anonymous flex item. CSS whitespace at item edges is removed.
 * nowrap prevents line wrapping; align-items:center and justify-content:center
 * retain intrinsic item boxes. The diagram owns the surrounding table-cell box.
 */
internal object MermaidKatexLabelLayout {
    fun measure(
        request: TextMetricsRequest,
        formulas: List<SceneTextSpan>,
        delegate: TextMetricProvider,
        formulaRequest: (SceneTextSpan) -> MermaidFormulaMeasureRequest,
        formulaMetrics: (MermaidFormulaMeasureRequest) -> MermaidFormulaMetrics?,
        isFallback: (MermaidFormulaMeasureRequest) -> Boolean,
    ): TextMetrics {
        val items = mutableListOf<MermaidFormulaLabelItem>()
        var offset = 0
        formulas.forEach { span ->
            appendTextItems(request, offset, span.start, delegate, items)
            val measureRequest = formulaRequest(span)
            val metrics = formulaMetrics(measureRequest)
            if (isFallback(measureRequest)) {
                appendTextItems(request, span.start, span.end, delegate, items)
            } else {
                val pending = if (metrics == null) {
                    delegate.measure(
                        request.copy(
                            text = request.text.substring(span.start, span.end),
                            spans = emptyList(),
                            maxWidth = Float.POSITIVE_INFINITY,
                        ),
                    )
                } else {
                    TextMetrics(metrics.width, metrics.height)
                }
                items += MermaidFormulaLabelItem(
                    text = request.text.substring(span.start, span.end),
                    spans = sliceSpans(request.spans, span.start, span.end),
                    bounds = SceneRect(0f, 0f, pending.width, pending.height),
                    formula = measureRequest,
                )
            }
            offset = span.end
        }
        appendTextItems(request, offset, request.text.length, delegate, items)
        return alignFlexItems(items)
    }

    private fun appendTextItems(
        request: TextMetricsRequest,
        start: Int,
        end: Int,
        delegate: TextMetricProvider,
        items: MutableList<MermaidFormulaLabelItem>,
    ) {
        if (start >= end) return
        // CSS Flexbox: direct DOM children form items; descendants retain their
        // parent's inline formatting context. Style boundaries do not form items.
        val htmlItems = request.spans.filter { it.htmlFlexItem != null }
        val boundaries = (
            listOf(start, end) + htmlItems
                .flatMap { listOf(it.start.coerceIn(start, end), it.end.coerceIn(start, end)) }
            ).distinct().sorted()
        boundaries.zipWithNext().forEach { (from, to) ->
            val raw = request.text.substring(from, to)
            val leading = raw.indexOfFirst { !it.isCssWhitespace() }
            if (leading < 0) return@forEach
            val trailing = raw.indexOfLast { !it.isCssWhitespace() } + 1
            val indices = mutableListOf<Int>()
            val text = buildString {
                for (index in leading until trailing) {
                    val character = raw[index]
                    if (character.isCssWhitespace()) {
                        if (lastOrNull() != ' ') {
                            append(' ')
                            indices += from + index
                        }
                    } else {
                        append(character)
                        indices += from + index
                    }
                }
            }
            val fontScale = htmlItems.firstOrNull {
                it.start == from && it.end == to
            }?.htmlFlexItem?.fontSizeScale ?: 1f
            val spans = request.spans.filter {
                it.formula == null && it.htmlFlexItem == null
            }.mapNotNull { span ->
                val spanStart = indices.indexOfFirst { it >= span.start }
                val spanEnd = indices.indexOfLast { it < span.end } + 1
                if (spanStart < 0 || spanEnd <= spanStart) null else {
                    span.copy(
                        start = spanStart, end = spanEnd,
                        fontSizeScale = span.fontSizeScale / fontScale,
                    )
                }
            }
            val metrics = delegate.measure(
                request.copy(
                    text = text,
                    spans = spans,
                    fontSize = request.fontSize * fontScale,
                    maxWidth = Float.POSITIVE_INFINITY,
                    // createText.addHtmlSpan sets these CSS values after applying
                    // the caller's label style. No SVG glyph width scale applies.
                    htmlLabel = true,
                    horizontalScale = 1f,
                    lineHeight = 1.5f,
                ),
            )
            // Browser anonymous flex items retain their CSS line box. Compose
            // trims leading from glyph layout; restore the declared line height.
            val height = maxOf(metrics.height, request.fontSize * fontScale * 1.5f)
            items += MermaidFormulaLabelItem(
                text = text,
                spans = spans,
                bounds = SceneRect(0f, 0f, metrics.width, height),
                fontSizeScale = fontScale,
            )
        }
    }

    private fun alignFlexItems(items: List<MermaidFormulaLabelItem>): TextMetrics {
        val width = items.sumOf { it.bounds.width.toDouble() }.toFloat()
        val height = items.maxOfOrNull { it.bounds.height } ?: 0f
        var left = 0f
        val positioned = items.map { item ->
            val top = (height - item.bounds.height) / 2f
            item.copy(
                bounds = SceneRect(
                    left, top, left + item.bounds.width, top + item.bounds.height,
                ),
            ).also { left += item.bounds.width }
        }
        return TextMetrics(width, height, lineCount = 1).withFormulaLayout(
            MermaidFormulaLabelLayout(width, height, positioned),
        )
    }

    private fun sliceSpans(
        spans: List<SceneTextSpan>,
        start: Int,
        end: Int,
    ): List<SceneTextSpan> = spans.filter {
        it.formula == null && it.htmlFlexItem == null
    }.mapNotNull { span ->
        val from = maxOf(start, span.start)
        val to = minOf(end, span.end)
        if (from >= to) null else span.copy(start = from - start, end = to - start)
    }

    private fun Char.isCssWhitespace(): Boolean =
        this == ' ' || this == '\t' || this == '\n' || this == '\r' || this == '\u000C'
}
