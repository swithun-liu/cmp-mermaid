package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/orthogonalRouter/__tests__/raykov.issues.spec.ts.
 */
class OrthogonalRouterIssuesTest {
    @Test
    fun overlappingHorizontalEdgesUseDifferentTracks() {
        val fixture = departmentFixture()
        val first = edge("e1", "Start1", "Stop")
        val second = edge("e2", "Start2", "Stop")

        route(fixture, listOf(first, second))

        val firstSegments = horizontalSegments(first.points.orEmpty())
        val secondSegments = horizontalSegments(second.points.orEmpty())
        val overlapFound = firstSegments.any { a ->
            secondSegments.any { b ->
                abs(a.coordinate - b.coordinate) < 1.0 &&
                    max(a.minimum, b.minimum) < min(a.maximum, b.maximum) - 1.0
            }
        }
        assertFalse(overlapFound)
    }

    @Test
    fun convergingEdgesEndOnDistributedTargetFacePorts() {
        val start1 = node("Start1", -165.078125, 22.5, 69.3515625, 45.0)
        val start2 = node("Start2", 4.2734375, 22.5, 69.3515625, 45.0)
        val stop = node("Stop", 169.3515625, 22.5, 60.8046875, 45.0)
        val first = edge("e1", "Start1", "Stop")
        val second = edge("e2", "Start2", "Stop")

        route(listOf(start1, start2, stop), listOf(first, second))

        val firstEnd = first.points!!.last()
        val secondEnd = second.points!!.last()
        val expectedX = stop.x!! - stop.width / 2.0
        val halfHeight = stop.height / 2.0
        assertEquals(expectedX, firstEnd.x, 1.0)
        assertEquals(expectedX, secondEnd.x, 1.0)
        assertTrue(firstEnd.y in stop.y!! - halfHeight..stop.y!! + halfHeight)
        assertTrue(secondEnd.y in stop.y!! - halfHeight..stop.y!! + halfHeight)
        assertTrue(abs(firstEnd.y - secondEnd.y) > 0.1)
        assertEquals(
            abs(firstEnd.y - stop.y!!),
            abs(secondEnd.y - stop.y!!),
            0.1,
        )
    }

    @Test
    fun adjacentCrossLaneEdgeDoesNotBacktrack() {
        val dep2 = node("Dep2", 4.2734375, 37.5, 169.3515625, 87.0, isGroup = true)
        val dep3 = node("Dep3", 169.3515625, 37.5, 160.8046875, 87.0, isGroup = true)
        val start2 = node(
            "Start2",
            4.2734375,
            22.5,
            69.3515625,
            45.0,
            parentId = "Dep2",
        )
        val stop = node(
            "Stop",
            169.3515625,
            22.5,
            60.8046875,
            45.0,
            parentId = "Dep3",
        )
        val edge = edge("e2", "Start2", "Stop")

        route(listOf(dep2, dep3, start2, stop), listOf(edge))

        val maximumBacktrack = edge.points.orEmpty().zipWithNext()
            .maxOf { (first, second) -> max(0.0, first.x - second.x) }
        assertTrue(maximumBacktrack < 5.0)
    }

    @Test
    fun destinationTerminalDoesNotHookBack() {
        val fixture = departmentFixture()
        val first = edge("e1", "Start1", "Stop")
        val second = edge("e2", "Start2", "Stop")

        route(fixture, listOf(first, second))

        val stop = fixture.single { node -> node.id == "Stop" }
        val points = first.points.orEmpty()
        val previous = points[points.lastIndex - 1]
        val last = points.last()
        assertEquals(stop.x!! - stop.width / 2.0, last.x, 1.0)
        assertTrue(last.x - previous.x >= 0.0)
    }

    @Test
    fun edgeToLowerNodeDoesNotInitiallyTravelUp() {
        val constr = node("Constr", 100.0, 200.0, 150.0, 350.0, isGroup = true)
        val legal = node("Legal", 300.0, 200.0, 200.0, 350.0, isGroup = true)
        val h = node("H", 100.0, 50.0, 60.0, 40.0, parentId = "Constr")
        val e = node("E", 100.0, 150.0, 60.0, 40.0, parentId = "Constr")
        val l = node("L", 100.0, 320.0, 60.0, 40.0, parentId = "Constr")
        val i = node("I", 280.0, 50.0, 80.0, 50.0, parentId = "Legal")
        val j = node("J", 280.0, 150.0, 150.0, 100.0, parentId = "Legal")
        val k = node("K", 280.0, 280.0, 60.0, 40.0, parentId = "Legal")
        val ik = edge("I-K", "I", "K")

        route(listOf(constr, legal, h, e, l, i, j, k), listOf(ik))

        val points = ik.points.orEmpty()
        val first = points.first()
        assertTrue(first.y >= i.y!!)
        assertTrue(points[1].y >= first.y - 5.0)
        assertTrue(points.minOf(SwimlanePoint::y) >= first.y - 10.0)
    }

    @Test
    fun edgeToLowerRightNodeGoesAroundBlockingNode() {
        val constr = node("Constr", 100.0, 200.0, 150.0, 400.0, isGroup = true)
        val legal = node("Legal", 320.0, 200.0, 250.0, 400.0, isGroup = true)
        val i = node("I", 280.0, 30.0, 80.0, 50.0, parentId = "Legal")
        val j = node("J", 280.0, 130.0, 200.0, 120.0, parentId = "Legal")
        val k = node("K", 350.0, 280.0, 60.0, 40.0, parentId = "Legal")
        val l = node("L", 100.0, 280.0, 60.0, 40.0, parentId = "Constr")
        val ik = edge("I-K", "I", "K")

        route(listOf(constr, legal, i, j, k, l), listOf(ik))

        val points = ik.points.orEmpty()
        assertTrue(points.minOf(SwimlanePoint::y) >= i.y!! - 10.0)
        assertFalse(polylinePassesThroughNode(points, j))
    }

    @Test
    fun exactKnsv3EdgeExitsBottomCenter() {
        val constr = node("Constr", 50.0, 200.0, 100.0, 350.0, isGroup = true)
        val legal = node("Legal", 121.06, 200.0, 250.0, 350.0, isGroup = true)
        val i = node("I", 121.06, 22.5, 35.89, 45.0, parentId = "Legal")
        val j = node("J", 121.06, 170.0, 232.0, 150.0, parentId = "Legal")
        val k = node("K", 121.06, 280.0, 40.0, 40.0, parentId = "Legal")
        val ik = edge("I-K", "I", "K")

        route(listOf(constr, legal, i, j, k), listOf(ik))

        val points = ik.points.orEmpty()
        val first = points.first()
        assertEquals(i.x!!, first.x, 1.0)
        assertEquals(i.y!! + i.height / 2.0, first.y, 1.0)
        assertTrue(points.minOf(SwimlanePoint::y) >= i.y!! - i.height / 2.0 - 5.0)
    }

    @Test
    fun exactKnsv3PortIsNotBottomRightCorner() {
        val constr = node("Constr", 50.0, 200.0, 150.0, 350.0, isGroup = true)
        val legal = node("Legal", 180.0, 200.0, 250.0, 350.0, isGroup = true)
        val i = node("I", 121.06, 22.5, 35.89, 45.0, parentId = "Legal")
        val j = node("J", 121.06, 170.0, 232.0, 150.0, parentId = "Legal")
        val k = node("K", 121.06, 291.0, 60.0, 40.0, parentId = "Legal")
        val l = node("L", 50.0, 291.0, 60.0, 40.0, parentId = "Constr")
        val ik = edge("I-K", "I", "K")

        route(listOf(constr, legal, i, j, k, l), listOf(ik))

        val points = ik.points.orEmpty()
        assertTrue(abs(points.first().x - i.x!!) < 5.0)
        assertTrue(points[1].y >= points.first().y - 5.0)
    }

    @Test
    fun destinationUsesCenterOfCardinalSide() {
        val fixture = departmentFixture()
        val edge = edge("e1", "Start1", "Stop")

        route(fixture, listOf(edge))

        val points = edge.points.orEmpty()
        val previous = points[points.lastIndex - 1]
        val last = points.last()
        assertEquals(138.94921875, last.x, 0.001)
        assertEquals(22.5, last.y, 0.001)
        assertTrue(previous.x <= last.x + 0.1)
    }

    private data class HorizontalSegment(
        val coordinate: Double,
        val minimum: Double,
        val maximum: Double,
    )

    private fun horizontalSegments(points: List<SwimlanePoint>): List<HorizontalSegment> =
        points.zipWithNext().mapNotNull { (first, second) ->
            if (abs(first.y - second.y) < 0.01) {
                HorizontalSegment(
                    coordinate = first.y,
                    minimum = min(first.x, second.x),
                    maximum = max(first.x, second.x),
                )
            } else {
                null
            }
        }

    private fun departmentFixture(): List<SwimlaneNode> = listOf(
        node("Dep1", -165.078125, 37.5, 169.3515625, 87.0, isGroup = true),
        node("Dep2", 4.2734375, 37.5, 169.3515625, 87.0, isGroup = true),
        node("Dep3", 169.3515625, 37.5, 160.8046875, 87.0, isGroup = true),
        node(
            "Start1",
            -165.078125,
            22.5,
            69.3515625,
            45.0,
            parentId = "Dep1",
        ),
        node(
            "Start2",
            4.2734375,
            22.5,
            69.3515625,
            45.0,
            parentId = "Dep2",
        ),
        node(
            "Stop",
            169.3515625,
            22.5,
            60.8046875,
            45.0,
            parentId = "Dep3",
        ),
    )

    private fun route(
        nodes: List<SwimlaneNode>,
        edges: List<SwimlaneEdge>,
    ) {
        routeEdgesOrthogonal(testLayout(nodes, edges))
    }

    private fun node(
        id: String,
        x: Double,
        y: Double,
        width: Double = 100.0,
        height: Double = 80.0,
        isGroup: Boolean = false,
        parentId: String? = null,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        isGroup = isGroup,
        parentId = parentId,
    )

    private fun edge(id: String, start: String, end: String): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = start,
        end = end,
        originalIndex = 0,
    )

    private fun polylinePassesThroughNode(
        points: List<SwimlanePoint>,
        node: SwimlaneNode,
    ): Boolean {
        val rect = node.measuredRect() ?: return false
        return points.zipWithNext().any { (first, second) ->
            if (sameX(first, second, 1.0)) {
                first.x > rect.left &&
                    first.x < rect.right &&
                    min(first.y, second.y) < rect.bottom &&
                    max(first.y, second.y) > rect.top
            } else if (sameY(first, second, 1.0)) {
                first.y > rect.top &&
                    first.y < rect.bottom &&
                    min(first.x, second.x) < rect.right &&
                    max(first.x, second.x) > rect.left
            } else {
                false
            }
        }
    }
}
