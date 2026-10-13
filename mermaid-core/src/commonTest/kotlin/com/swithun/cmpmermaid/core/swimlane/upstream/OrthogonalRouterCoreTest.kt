package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/orthogonalRouter/__tests__/raykov.router.spec.ts.
 *
 * The two upstream tests inside `describe.skip("Edge label obstacle
 * avoidance")` are intentionally not translated because Mermaid 12.0.0 skips
 * them after edge-label nodes stopped being routing obstacles.
 */
class OrthogonalRouterCoreTest {
    @Test
    fun placesPortsOnRightAndLeftEdgesForHorizontalRouting() {
        val a = node("A", 100.0, 150.0)
        val b = node("B", 300.0, 150.0)
        val edge = edge("e1", "A", "B")

        route(listOf(a, b), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(points.size >= 2)
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(points.first().x > a.x!!)
        assertTrue(isOnBoundary(points.last(), b))
        assertTrue(points.last().x < b.x!!)
        assertOrthogonal(points)
    }

    @Test
    fun placesPortsOnBottomAndTopEdgesForVerticalRouting() {
        val a = node("A", 150.0, 100.0)
        val b = node("B", 150.0, 300.0)
        val edge = edge("e1", "A", "B")

        route(listOf(a, b), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(points.first().y > a.y!!)
        assertTrue(isOnBoundary(points.last(), b))
        assertTrue(points.last().y < b.y!!)
        assertOrthogonal(points)
    }

    @Test
    fun placesDiagonalPortsOnCardinalNodeBoundaries() {
        val a = node("A", 100.0, 100.0)
        val b = node("B", 300.0, 300.0)
        val edge = edge("e1", "A", "B")

        route(listOf(a, b), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(isOnBoundary(points.last(), b))
        assertTrue(points.first().x != a.x || points.first().y != a.y)
        assertTrue(points.last().x != b.x || points.last().y != b.y)
        assertOrthogonal(points)
    }

    @Test
    fun routesAlignedHorizontalEdgeAsStraightLine() {
        val a = node("A", 100.0, 150.0)
        val b = node("B", 300.0, 150.0)
        val edge = edge("e1", "A", "B")

        route(listOf(a, b), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(points.size >= 2)
        assertTrue(abs(points.first().y - points.last().y) < 1.0)
        assertTrue(points.all { point -> abs(point.y - points.first().y) < DirectionEpsilon })
    }

    @Test
    fun detoursAroundObstacleBetweenEndpoints() {
        val a = node("A", 100.0, 150.0)
        val blocker = node("B", 200.0, 150.0)
        val c = node("C", 300.0, 150.0)
        val edge = edge("e1", "A", "C")

        route(listOf(a, blocker, c), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(points.size >= 4)
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(isOnBoundary(points.last(), c))
        assertTrue(!polylineHitsNodeInterior(points, blocker))
        assertOrthogonal(points)
    }

    @Test
    fun routesCrossLaneEdgeWithValidPortsAndOrthogonalSegments() {
        val lane1 = node("lane1", 250.0, 150.0, 400.0, 220.0, isGroup = true)
        val lane2 = node("lane2", 700.0, 150.0, 220.0, 220.0, isGroup = true)
        val d = node("D", 360.0, 150.0, parentId = "lane1")
        val c = node("C", 700.0, 150.0, parentId = "lane2")
        val edge = edge("e1", "C", "D")

        route(listOf(lane1, lane2, d, c), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(points.size >= 2)
        assertTrue(isOnBoundary(points.first(), c))
        assertTrue(isOnBoundary(points.last(), d))
        assertOrthogonal(points)
    }

    @Test
    fun includesAnchorPointOffsetFromSourcePort() {
        val a = node("A", 100.0, 150.0)
        val b = node("B", 400.0, 150.0)
        val edge = edge("e1", "A", "B")

        route(listOf(a, b), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(points.size >= 3)
        val offset = hypot(
            points[1].x - points[0].x,
            points[1].y - points[0].y,
        )
        assertTrue(offset > 15.0)
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(isOnBoundary(points.last(), b))
    }

    @Test
    fun completeRoutingKeepsPortsOnBoundsWhileAvoidingObstacle() {
        val a = node("A", 100.0, 150.0)
        val blocker = node("B", 200.0, 150.0)
        val c = node("C", 300.0, 150.0)
        val edge = edge("e1", "A", "C")

        route(listOf(a, blocker, c), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(isOnBoundary(points.first(), a))
        assertTrue(isOnBoundary(points.last(), c))
        assertTrue(!polylineHitsNodeInterior(points, blocker))
        assertOrthogonal(points)
    }

    @Test
    fun routesMultipleEdgesWithPortsOnEveryEndpointBoundary() {
        val a = node("A", 100.0, 150.0)
        val b = node("B", 300.0, 150.0)
        val c = node("C", 100.0, 300.0)
        val e1 = edge("e1", "A", "B")
        val e2 = edge("e2", "A", "C")

        route(listOf(a, b, c), listOf(e1, e2))

        assertTrue(isOnBoundary(e1.points!!.first(), a))
        assertTrue(isOnBoundary(e1.points!!.last(), b))
        assertTrue(isOnBoundary(e2.points!!.first(), a))
        assertTrue(isOnBoundary(e2.points!!.last(), c))
        assertOrthogonal(e1.points.orEmpty())
        assertOrthogonal(e2.points.orEmpty())
    }

    @Test
    fun assignsDifferentTracksToParallelEdges() {
        val a = node("A", 100.0, 150.0)
        val b = node("B", 300.0, 150.0)
        val edges = listOf(
            edge("e1", "A", "B"),
            edge("e2", "A", "B"),
            edge("e3", "A", "B"),
        )

        route(listOf(a, b), edges)

        val trackYs = edges.map(::longestHorizontalSegmentY)
        assertTrue(trackYs.all(Double::isFinite))
        assertTrue(trackYs.map { value -> (value * 2.0).toInt() }.toSet().size > 1)
    }

    @Test
    fun routesAroundExactReportedStraightLineObstacle() {
        val start1 = node(
            "Start1",
            -165.078125,
            22.5,
            width = 69.3515625,
            height = 45.0,
        )
        val start2 = node(
            "Start2",
            4.2734375,
            22.5,
            width = 69.3515625,
            height = 45.0,
        )
        val stop = node(
            "Stop",
            169.3515625,
            22.5,
            width = 60.8046875,
            height = 45.0,
        )
        val edge = edge("e1", "Start1", "Stop")

        route(listOf(start1, start2, stop), listOf(edge))

        val points = edge.points.orEmpty()
        assertTrue(!polylineHitsNodeInterior(points, start2))
        assertTrue(isOnBoundary(points.first(), start1))
        assertTrue(isOnBoundary(points.last(), stop))
        assertOrthogonal(points)
    }

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

    private fun isOnBoundary(
        point: SwimlanePoint,
        node: SwimlaneNode,
        tolerance: Double = 1.0,
    ): Boolean {
        val centerX = node.x!!
        val centerY = node.y!!
        val left = centerX - node.width / 2.0
        val right = centerX + node.width / 2.0
        val top = centerY - node.height / 2.0
        val bottom = centerY + node.height / 2.0
        val onVertical =
            (abs(point.x - left) < tolerance || abs(point.x - right) < tolerance) &&
                point.y >= top - tolerance &&
                point.y <= bottom + tolerance
        val onHorizontal =
            (abs(point.y - top) < tolerance || abs(point.y - bottom) < tolerance) &&
                point.x >= left - tolerance &&
                point.x <= right + tolerance
        return onVertical || onHorizontal
    }

    private fun assertOrthogonal(points: List<SwimlanePoint>) {
        assertTrue(points.size >= 2)
        for ((first, second) in points.zipWithNext()) {
            assertTrue(
                sameX(first, second) || sameY(first, second),
                "Non-orthogonal segment: $first -> $second",
            )
        }
    }

    private fun polylineHitsNodeInterior(
        points: List<SwimlanePoint>,
        node: SwimlaneNode,
    ): Boolean {
        val rect = node.measuredRect() ?: return false
        return points.zipWithNext().any { (first, second) ->
            val minimumX = min(first.x, second.x)
            val maximumX = max(first.x, second.x)
            val minimumY = min(first.y, second.y)
            val maximumY = max(first.y, second.y)
            maximumX > rect.left + 0.001 &&
                minimumX < rect.right - 0.001 &&
                maximumY > rect.top + 0.001 &&
                minimumY < rect.bottom - 0.001
        }
    }

    private fun longestHorizontalSegmentY(edge: SwimlaneEdge): Double {
        var longestLength = -1.0
        var longestY = Double.NaN
        for ((first, second) in edge.points.orEmpty().zipWithNext()) {
            if (abs(first.y - second.y) >= 0.1) continue
            val length = abs(first.x - second.x)
            if (length > longestLength) {
                longestLength = length
                longestY = first.y
            }
        }
        return longestY
    }
}
