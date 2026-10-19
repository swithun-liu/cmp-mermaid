package com.swithun.cmpmermaid.compose

internal actual val useMermaidCssBreakSpacesLayout: Boolean = false

internal actual fun cssBreakSpacesLineBreakPositions(
    text: String,
    start: Int,
    end: Int,
): List<Int> = emptyList()
