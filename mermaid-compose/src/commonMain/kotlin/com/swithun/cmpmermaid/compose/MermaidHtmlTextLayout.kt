package com.swithun.cmpmermaid.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/rendering-util/createText.ts -> addHtmlSpan.
 *
 * The browser's `white-space: break-spaces` follows Unicode line-break
 * opportunities, while `overflow-wrap: normal` does not create emergency
 * breaks inside an otherwise unbreakable segment. Android StaticLayout still
 * creates those emergency character breaks, so Android uses the explicit
 * CSS-compatible line construction below.
 */
internal expect val useMermaidCssBreakSpacesLayout: Boolean

internal expect fun cssBreakSpacesLineBreakPositions(
    text: String,
    start: Int,
    end: Int,
): List<Int>

internal fun AnnotatedString.withCssBreakSpacesLines(
    maximumWidth: Float,
    measureWidth: (AnnotatedString) -> Float,
): AnnotatedString {
    if (!maximumWidth.isFinite() || maximumWidth <= 0f || isEmpty()) return this

    val lineBreaks = mutableSetOf<Int>()
    var paragraphStart = 0
    for (index in 0..length) {
        if (index == length || text[index] == '\n') {
            collectCssBreakSpacesLineBreaks(
                start = paragraphStart,
                end = index,
                maximumWidth = maximumWidth,
                measureWidth = measureWidth,
                output = lineBreaks,
            )
            paragraphStart = index + 1
        }
    }
    if (lineBreaks.isEmpty()) return this

    return buildAnnotatedString {
        var copiedUntil = 0
        lineBreaks.sorted().forEach { lineBreak ->
            append(this@withCssBreakSpacesLines.subSequence(copiedUntil, lineBreak))
            append('\n')
            copiedUntil = lineBreak
        }
        append(
            this@withCssBreakSpacesLines.subSequence(
                copiedUntil,
                this@withCssBreakSpacesLines.length,
            ),
        )
    }
}

private fun AnnotatedString.collectCssBreakSpacesLineBreaks(
    start: Int,
    end: Int,
    maximumWidth: Float,
    measureWidth: (AnnotatedString) -> Float,
    output: MutableSet<Int>,
) {
    if (start >= end) return

    val opportunities = cssBreakSpacesLineBreakPositions(text, start, end)
        .asSequence()
        .filter { position -> position > start && position < end }
        .distinct()
        .sorted()
        .toList()
    if (opportunities.isEmpty()) return

    var lineStart = start
    var previousOpportunity = start
    for (candidateEnd in opportunities + end) {
        val candidateWidth = measureWidth(subSequence(lineStart, candidateEnd))
        if (candidateWidth > maximumWidth && previousOpportunity > lineStart) {
            output += previousOpportunity
            lineStart = previousOpportunity
        }
        if (candidateEnd < end) {
            previousOpportunity = candidateEnd
        }
    }
}
