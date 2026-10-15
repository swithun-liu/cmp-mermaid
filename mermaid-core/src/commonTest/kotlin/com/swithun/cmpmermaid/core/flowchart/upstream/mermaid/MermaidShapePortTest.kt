package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneShapeKind
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.flowchart.FlowDirection
import com.swithun.cmpmermaid.core.flowchart.FlowNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidShapePortTest {
    @Test
    fun usesActualBoundsForBangAndCloudRectIntersections() {
        listOf(SceneShapeKind.Bang, SceneShapeKind.Cloud).forEach { kind ->
            val result = MermaidShapePort.layout(
                node = node(kind),
                measuredLabel = SceneSize(width = 80f, height = 20f),
                direction = FlowDirection.LeftToRight,
                defaultNodeStroke = SceneColor(0xFF333333),
            )
            val layout = assertIs<GMResult.Ok<MermaidShapeLayout>>(result).value
            val pathPoints = layout.geometry.paths.single().points
            val outline = layout.geometry.outline

            assertEquals(pathPoints.width(), outline.width(), 0.001f, "$kind width")
            assertEquals(pathPoints.height(), outline.height(), 0.001f, "$kind height")
            assertEquals(layout.size.width, outline.width(), 0.001f, "$kind layout width")
            assertEquals(layout.size.height, outline.height(), 0.001f, "$kind layout height")
        }
    }

    @Test
    fun translatesCurlyBraceGeometryAndLabelPlacementFromMermaid12() {
        data class Expected(
            val kind: SceneShapeKind,
            val look: String,
            val width: Float,
            val height: Float,
            val labelX: Float,
            val labelY: Float,
            val pathPointCounts: List<Int>,
        )

        listOf(
            Expected(SceneShapeKind.BraceLeft, "classic", 148.5f, 46f, -0.75f, 0f, listOf(92, 86)),
            Expected(SceneShapeKind.BraceLeft, "neo", 151.8f, 43f, -2.1f, 1.5f, listOf(92, 86)),
            Expected(SceneShapeKind.BraceRight, "neo", 166f, 55f, -10.5f, -4.5f, listOf(82, 86)),
            Expected(SceneShapeKind.Braces, "neo", 174.1f, 55f, -7.7f, -4.5f, listOf(92, 92, 178)),
        ).forEach { expected ->
            val result = MermaidShapePort.layout(
                node = node(expected.kind, look = expected.look),
                measuredLabel = SceneSize(width = 120f, height = 21f),
                direction = FlowDirection.LeftToRight,
                defaultNodeStroke = SceneColor(0xFF333333),
            )
            val layout = assertIs<GMResult.Ok<MermaidShapeLayout>>(result).value

            assertEquals(expected.width, layout.size.width, 0.001f, "${expected.kind} width")
            assertEquals(expected.height, layout.size.height, 0.001f, "${expected.kind} height")
            assertEquals(expected.labelX, layout.labelOffset.x, 0.001f, "${expected.kind} label x")
            assertEquals(expected.labelY, layout.labelOffset.y, 0.001f, "${expected.kind} label y")
            assertEquals(
                expected.pathPointCounts,
                layout.geometry.paths.map { path -> path.points.size },
                "${expected.kind} path points",
            )
            assertEquals(
                expected.pathPointCounts.last(),
                layout.geometry.outline.size,
                "${expected.kind} intersection outline",
            )
        }
    }

    private fun node(
        kind: SceneShapeKind,
        look: String = "classic",
    ) = FlowNode(
        id = kind.name,
        label = kind.name,
        labelSpans = emptyList(),
        labelType = FlowLabelType.Text,
        shape = kind,
        padding = 15f,
        minWidth = null,
        look = look,
    )

    private fun List<ScenePoint>.width(): Float =
        maxOf(ScenePoint::x) - minOf(ScenePoint::x)

    private fun List<ScenePoint>.height(): Float =
        maxOf(ScenePoint::y) - minOf(ScenePoint::y)
}
