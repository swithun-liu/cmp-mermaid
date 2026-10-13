package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class SceneAuditManifestTest {
    @Test
    fun includesViewportPaddingInAuditedCoordinates() {
        val manifest = MermaidScene(
            width = 700f,
            height = 500f,
            background = SceneColor(0xFFFFFFFF),
            elements = emptyList(),
            viewportPadding = 12f,
        ).toAuditManifestJson()

        assertContains(
            manifest,
            "\"viewport\":{\"x\":-12.0,\"y\":-12.0,\"width\":724.0,\"height\":524.0",
        )
    }

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

    @Test
    fun preservesNarrowTextWidthWhenApplyingStartAndEndInsets() {
        val bounds = SceneRect(left = 2f, top = 3f, right = 8f, bottom = 19f)
        val insetBoundaryBounds = SceneRect(left = 2f, top = 3f, right = 10f, bottom = 19f)

        assertEquals(
            SceneRect(left = 6f, top = 3f, right = 12f, bottom = 19f),
            text(bounds, SceneTextAlignment.Start).auditBounds(),
        )
        assertEquals(
            SceneRect(left = -2f, top = 3f, right = 4f, bottom = 19f),
            text(bounds, SceneTextAlignment.End).auditBounds(),
        )
        assertEquals(
            SceneRect(left = 6f, top = 3f, right = 14f, bottom = 19f),
            text(insetBoundaryBounds, SceneTextAlignment.Start).auditBounds(),
        )
    }

    @Test
    fun exportsNormalizedPathTopologyForEdgeAudits() {
        val manifest = MermaidScene(
            width = 100f,
            height = 100f,
            background = SceneColor(0xFFFFFFFF),
            elements = listOf(
                ScenePath(
                    id = "L_A_B_0",
                    points = listOf(
                        ScenePoint(10f, 20f),
                        ScenePoint(10f, 20f),
                        ScenePoint(30f, 20f),
                        ScenePoint(50f, 20f),
                        ScenePoint(50f, 60f),
                    ),
                    commands = emptyList(),
                    color = SceneColor(0xFF000000),
                    strokeWidth = 1f,
                    look = "classic",
                    animated = false,
                ),
            ),
        ).toAuditManifestJson()

        assertContains(
            manifest,
            "\"topologyPoints\":[{\"x\":10.0,\"y\":20.0}," +
                "{\"x\":50.0,\"y\":20.0},{\"x\":50.0,\"y\":60.0}]",
        )
        assertContains(manifest, "\"bendPoints\":[{\"x\":50.0,\"y\":20.0}]")
        assertContains(manifest, "\"directionSequence\":[\"R\",\"D\"]")
        assertContains(manifest, "\"sourcePortSide\":\"Right\"")
        assertContains(manifest, "\"destinationPortSide\":\"Top\"")
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
