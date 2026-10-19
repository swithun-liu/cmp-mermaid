package com.swithun.cmpmermaid.compose

import android.icu.text.BreakIterator
import android.icu.util.ULocale

internal actual val useMermaidCssBreakSpacesLayout: Boolean = true

internal actual fun cssBreakSpacesLineBreakPositions(
    text: String,
    start: Int,
    end: Int,
): List<Int> {
    val segment = text.substring(start, end)
    val iterator = BreakIterator.getLineInstance(ULocale.ROOT).apply {
        setText(segment)
    }
    return buildSet {
        var boundary = iterator.first()
        while (boundary != BreakIterator.DONE) {
            if (boundary > 0 && boundary < segment.length) {
                add(start + boundary)
            }
            boundary = iterator.next()
        }
        segment.forEachIndexed { index, character ->
            if (character == ' ' || character == '\u200B') {
                add(start + index + 1)
            }
        }
    }.sorted()
}
