package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneShapeGeometry
import com.swithun.cmpmermaid.core.SceneShapeKind
import com.swithun.cmpmermaid.core.SceneShapePath
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SceneAuditManifestTest {
    @Test
    fun auditCaseIdentityEscapesSourceAsJson() {
        val identity = auditCaseIdentityJson(
            caseId = "visual_flowchart_129",
            auditSource = "visual-parity",
            source = "flowchart LR\n  A[\"quoted\"] --> B\\C",
        )

        assertEquals(
            "{\"caseId\":\"visual_flowchart_129\"," +
                "\"auditSource\":\"visual-parity\"," +
                "\"source\":\"flowchart LR\\n  A[\\\"quoted\\\"] --> B\\\\C\"}",
            identity,
        )
    }

    @Test
    fun resolvesCaseIdsOnlyWithinRequestedAuditSource() {
        assertNull(
            resolveAuditCase(
                requestedId = "visual_flowchart_129",
                auditSource = MermaidDebugAuditSource.Gallery,
            ),
        )
        val visualParity = assertNotNull(
            resolveAuditCase(
                requestedId = "visual_flowchart_129",
                auditSource = MermaidDebugAuditSource.VisualParity,
            ),
        )

        assertContains(visualParity.second.source, "Start --> N0")
        assertContains(visualParity.second.source, "N3 --> Finish")
    }

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
    fun translatesStartAndEndTextBoundsWithoutChangingGlyphWidth() {
        val bounds = SceneRect(left = 2f, top = 3f, right = 30f, bottom = 19f)

        assertEquals(
            SceneRect(left = 6f, top = 3f, right = 34f, bottom = 19f),
            text(bounds, SceneTextAlignment.Start).auditBounds(),
        )
        assertEquals(
            SceneRect(left = -2f, top = 3f, right = 26f, bottom = 19f),
            text(bounds, SceneTextAlignment.End).auditBounds(),
        )
    }

    @Test
    fun preservesCenteredTextBounds() {
        val bounds = SceneRect(left = 2f, top = 3f, right = 30f, bottom = 19f)

        assertEquals(bounds, text(bounds, SceneTextAlignment.Center).auditBounds())
    }

    @Test
    fun exportsRotatedTextOutlineInPaintOrder() {
        val rotated = SceneText(
            text = "label",
            bounds = SceneRect(0f, 0f, 10f, 20f),
            color = SceneColor(0xFF000000),
            fontSize = 14f,
            rotationDegrees = 90f,
            rotationPivot = ScenePoint(0f, 0f),
        )

        val outline = rotated.auditOutlinePoints()
        assertEquals(4, outline.size)
        assertEquals(0f, outline[0].x, 0.0001f)
        assertEquals(0f, outline[0].y, 0.0001f)
        assertEquals(0f, outline[1].x, 0.0001f)
        assertEquals(10f, outline[1].y, 0.0001f)
        assertEquals(-20f, outline[2].x, 0.0001f)
        assertEquals(10f, outline[2].y, 0.0001f)
        assertEquals(-20f, outline[3].x, 0.0001f)
        assertEquals(0f, outline[3].y, 0.0001f)
        val auditBounds = rotated.auditBounds()
        assertEquals(-20f, auditBounds.left, 0.0001f)
        assertEquals(0f, auditBounds.top, 0.0001f)
        assertEquals(0f, auditBounds.right, 0.0001f)
        assertEquals(10f, auditBounds.bottom, 0.0001f)
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
        assertEquals(
            SceneRect(left = -2f, top = 3f, right = 6f, bottom = 19f),
            text(insetBoundaryBounds, SceneTextAlignment.End).auditBounds(),
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

    @Test
    fun exportsShapeOutlineInAbsoluteSceneCoordinates() {
        val outline = listOf(
            ScenePoint(-20f, -30f),
            ScenePoint(20f, -30f),
            ScenePoint(20f, 30f),
            ScenePoint(-20f, 30f),
        )
        val manifest = MermaidScene(
            width = 100f,
            height = 100f,
            background = SceneColor(0xFFFFFFFF),
            elements = listOf(
                SceneShape(
                    id = "node",
                    bounds = SceneRect(10f, 20f, 50f, 80f),
                    kind = SceneShapeKind.Rectangle,
                    geometry = SceneShapeGeometry(
                        paths = listOf(SceneShapePath(points = outline)),
                        outline = outline,
                    ),
                    fill = SceneColor(0xFFFFFFFF),
                    stroke = SceneColor(0xFF000000),
                ),
            ),
        ).toAuditManifestJson()

        assertContains(
            manifest,
            "\"outlinePoints\":[{\"x\":10.0,\"y\":20.0}," +
                "{\"x\":50.0,\"y\":20.0},{\"x\":50.0,\"y\":80.0}," +
                "{\"x\":10.0,\"y\":80.0}]",
        )
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
