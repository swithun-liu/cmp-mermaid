package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import kotlin.test.Test
import kotlin.test.assertEquals

class SceneAuditManifestTest {
    @Test
    fun removesHorizontalLayoutPaddingFromStartAndEndTextBounds() {
        val bounds = SceneRect(left = 2f, top = 3f, right = 30f, bottom = 19f)

        listOf(SceneTextAlignment.Start, SceneTextAlignment.End).forEach { alignment ->
            assertEquals(
                SceneRect(left = 6f, top = 3f, right = 26f, bottom = 19f),
                text(bounds, alignment).auditBounds(),
            )
        }
    }

    @Test
    fun preservesCenteredTextBounds() {
        val bounds = SceneRect(left = 2f, top = 3f, right = 30f, bottom = 19f)

        assertEquals(bounds, text(bounds, SceneTextAlignment.Center).auditBounds())
    }

    private fun text(
        bounds: SceneRect,
        alignment: SceneTextAlignment,
    ): SceneText = SceneText(
        text = "label",
        bounds = bounds,
        color = SceneColor(0xFF000000),
        fontSize = 14f,
        horizontalAlignment = alignment,
    )
}
