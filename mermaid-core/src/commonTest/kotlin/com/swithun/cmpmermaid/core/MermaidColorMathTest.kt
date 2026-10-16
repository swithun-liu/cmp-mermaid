package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidColorMathTest {
    @Test
    fun matchesKhromaHslSerializationAtRgbRoundingBoundaries() {
        val source = SceneColor(0xFF102030)

        assertEquals(SceneColor(0xFFCFDFEF), source.mermaidLighten(75.0))
        assertEquals(SceneColor(0xFF29537C), source.mermaidLighten(20.0))
        assertEquals(SceneColor(0xFF162D43), source.mermaidLighten(5.0))
        assertEquals(SceneColor(0xFF112334), source.mermaidLighten(1.0))
        assertEquals(SceneColor(0xFF0A131D), source.mermaidDarken(5.0))
        assertEquals(SceneColor(0xFF03060A), source.mermaidDarken(10.0))
    }
}
