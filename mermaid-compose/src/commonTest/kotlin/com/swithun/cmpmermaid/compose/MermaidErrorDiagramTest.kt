package com.swithun.cmpmermaid.compose

import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidErrorDiagramTest {
    @Test
    fun matchesMermaid12ErrorRendererStructure() {
        assertEquals("Syntax error in text", MERMAID_ERROR_TITLE)
        assertEquals("mermaid version 12.0.0", MERMAID_ERROR_VERSION)
        assertEquals(2412f, ERROR_VIEWBOX_WIDTH)
        assertEquals(512f, ERROR_VIEWBOX_HEIGHT)
        assertEquals(1440f, ERROR_TITLE_X)
        assertEquals(250f, ERROR_TITLE_Y)
        assertEquals(1250f, ERROR_VERSION_X)
        assertEquals(400f, ERROR_VERSION_Y)
        assertEquals(150f, ERROR_TITLE_FONT_SIZE)
        assertEquals(100f, ERROR_VERSION_FONT_SIZE)
        assertEquals(6, ERROR_ICON_PATH_DATA.size)
    }
}
