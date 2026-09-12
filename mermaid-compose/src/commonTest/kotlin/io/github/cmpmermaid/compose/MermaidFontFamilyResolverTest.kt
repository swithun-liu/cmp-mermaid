package io.github.cmpmermaid.compose

import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidFontFamilyResolverTest {
    @Test
    fun parsesMermaidCssFontStackInFallbackOrder() {
        assertEquals(
            listOf("recursive variable", "arial", "sans-serif"),
            parseCssFontFamilies("\"Recursive Variable\", arial, sans-serif"),
        )
    }

    @Test
    fun preservesCommaInsideQuotedFamilyName() {
        assertEquals(
            listOf("custom, family", "monospace"),
            parseCssFontFamilies("\"Custom, Family\", monospace"),
        )
    }
}
