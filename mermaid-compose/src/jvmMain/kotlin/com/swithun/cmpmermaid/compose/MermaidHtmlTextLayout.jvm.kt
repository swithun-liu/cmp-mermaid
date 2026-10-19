package com.swithun.cmpmermaid.compose

import java.text.BreakIterator
import java.util.Locale

internal actual val useMermaidCssBreakSpacesLayout: Boolean = false

internal actual fun cssBreakSpacesLineBreakPositions(
    text: String,
    start: Int,
    end: Int,
): List<Int> {
    val segment = text.substring(start, end)
    val iterator = BreakIterator.getLineInstance(Locale.ROOT).apply {
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
