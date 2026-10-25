package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.ScenePathCommand
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MermaidClusterEdgePortTest {
    private val box = SceneRect(-10f, -10f, 10f, 10f)

    @Test
    fun boundariesAreOutsideAndAllFourIntersectionSidesAreTranslated() {
        assertTrue(MermaidClusterEdgePort.outsideNode(box, ScenePoint(10f, 0f)))
        listOf(
            // Upstream r === 0 keeps the outside point for a vertical segment.
            ScenePoint(0f, -20f) to ScenePoint(0f, -20f),
            ScenePoint(0f, 20f) to ScenePoint(0f, 20f),
            ScenePoint(-20f, 0f) to ScenePoint(-10f, 0f),
            ScenePoint(20f, 0f) to ScenePoint(10f, 0f),
            ScenePoint(20f, 10f) to ScenePoint(10f, 5f),
            ScenePoint(10f, 20f) to ScenePoint(5f, 10f),
        ).forEach { (outside, expected) ->
            assertEquals(expected, MermaidClusterEdgePort.intersection(box, outside, ScenePoint(0f, 0f)))
        }
    }

    @Test
    fun clippingRetainsOutsidePointsUntilFirstEntryAndDeduplicatesBoundary() {
        val points = listOf(ScenePoint(0f, -20f), ScenePoint(0f, -10f), ScenePoint(0f, 0f), ScenePoint(0f, 20f))
        assertEquals(points.take(2), MermaidClusterEdgePort.cutPathAtIntersect(points, box))
        assertEquals(points.take(2), MermaidClusterEdgePort.cutPathAtIntersect(points.take(2), box))
        assertEquals(emptyList(), MermaidClusterEdgePort.cutPathAtIntersect(emptyList(), box))
    }

    @Test
    fun pathStartingInsideCollapsesAtTheFirstPointLikeUpstream() {
        val start = ScenePoint(1f, 2f)
        assertEquals(listOf(start), MermaidClusterEdgePort.cutPathAtIntersect(
            listOf(start, ScenePoint(30f, 2f)), box,
        ))
        // utils/lineWithOffset.ts: an absent neighbour returns angle/deltas 0.
        val commands = assertIs<GMResult.Ok<List<ScenePathCommand>>>(MermaidEdgePathPort.generate(
            listOf(start), "basis", SceneArrowHead.None, SceneArrowHead.Triangle,
        )).value
        assertEquals(ScenePathCommand.MoveTo(ScenePoint(5f, 2f)), commands.first())
        assertTrue(commands.all { it is ScenePathCommand.MoveTo })
    }
}
