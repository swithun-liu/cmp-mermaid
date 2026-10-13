package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - orthogonalRouter/__tests__/raykov.crossing.spec.ts
 * - orthogonalRouter/__tests__/raykov.detour.spec.ts
 * - orthogonalRouter/__tests__/raykov.repro_crossing.spec.ts
 * - orthogonalRouter/__tests__/raykov.wide_node.spec.ts
 */
class OrthogonalRouterRegressionTest {
    @Test
    fun keepsAlignedDToEAndDToHFromCrossing() {
        val d = node("D", -19.5, 22.5, 42.1171875, 45.0)
        val e = node("E", -19.5, 117.5, 42.1171875, 45.0)
        val h = node("H", -19.5, 212.5, 42.1171875, 45.0)
        val de = edge("eDE", "D", "E")
        val dh = edge("eDH", "D", "H")

        route(listOf(d, e, h), listOf(de, dh))

        assertFalse(polylinesStrictlyCross(de.points.orEmpty(), dh.points.orEmpty()))
    }

    @Test
    fun routesSameRowEdgesWithoutUShapedDetours() {
        val constr = node(
            "Constr",
            -139.77734375,
            70.0,
            100.0,
            190.0,
            isGroup = true,
        )
        val legal = node("Legal", 0.78125, 70.0, 100.0, 190.0, isGroup = true)
        val funLane = node("Fun", 140.55859375, 70.0, 100.0, 190.0, isGroup = true)
        val h = node("H", -139.77734375, 22.5, 41.34375, 45.0, parentId = "Constr")
        val i = node("I", 0.78125, 22.5, 39.0, 45.0, parentId = "Legal")
        val j = node("J", 0.78125, 117.5, 39.0, 45.0, parentId = "Legal")
        val e = node("E", -139.77734375, 117.5, 41.34375, 45.0, parentId = "Constr")
        val f = node("F", 140.55859375, 117.5, 40.5546875, 45.0, parentId = "Fun")
        val hi = edge("eHI", "H", "I")
        val ij = edge("eIJ", "I", "J")
        val je = edge("eJE", "J", "E")
        val ef = edge("eEF", "E", "F")

        route(
            listOf(constr, legal, funLane, h, i, j, e, f),
            listOf(hi, ij, je, ef),
        )

        assertTrue(yRange(je.points.orEmpty()) < 50.0)
        assertTrue(yRange(ef.points.orEmpty()) < 100.0)
    }

    @Test
    fun alignedSiblingRoutesRemainDistinctWithoutCrossing() {
        val d = node("D", -19.5, 22.5)
        val e = node("E", -19.5, 117.5)
        val h = node("H", -19.5, 212.5)
        val de = edge("eDE", "D", "E")
        val dh = edge("eDH", "D", "H")

        route(listOf(d, e, h), listOf(de, dh))

        assertEquals(d.x!!, de.points!!.first().x, 0.00001)
        assertEquals(e.x!!, de.points!!.last().x, 0.00001)
        assertNotEquals(de.points, dh.points)
        assertFalse(polylinesStrictlyCross(de.points.orEmpty(), dh.points.orEmpty()))
    }

    @Test
    fun keepsDirectDToECenteredWhenDToHUsesDistinctRoute() {
        val d = node("D", 50.0, 22.5)
        val e = node("E", 50.0, 117.5, width = 41.34375)
        val h = node("H", 50.0, 212.5)
        val de = edge("eDE2", "D", "E")
        val dh = edge("eDH2", "D", "H")

        route(listOf(d, e, h), listOf(de, dh))

        assertEquals(50.0, de.points!!.first().x, 0.001)
        assertEquals(50.0, de.points!!.last().x, 0.001)
        assertNotEquals(de.points, dh.points)
    }

    @Test
    fun routesIToKRightOfJWhenConstrLaneIsLeftOfLegal() {
        val constrX = 0.0
        val legalX = 150.0
        val rowGap = 95.0
        val h = node("H", constrX, rowGap * 0.0)
        val i = node("I", legalX, rowGap * 0.0)
        val j = node("J", legalX, rowGap * 1.0)
        val k = node("K", legalX, rowGap * 2.0)
        val e = node("E", constrX, rowGap * 1.0)
        val l = node("L", constrX, rowGap * 2.0)
        val hi = edge("eHI", "H", "I")
        val ik = edge("eIK", "I", "K")
        val ij = edge("eIJ", "I", "J")
        val je = edge("eJE", "J", "E")
        val kl = edge("eKL", "K", "L")

        route(listOf(h, i, j, k, e, l), listOf(hi, ik, ij, je, kl))

        assertTrue(ik.points.orEmpty().minOf(SwimlanePoint::x) >= legalX - 1.0)
        assertFalse(polylinesStrictlyCross(ik.points.orEmpty(), je.points.orEmpty()))
    }

    @Test
    fun routesIToKAroundWideJ() {
        val i = node("I", 150.0, 0.0, 50.0, 90.0)
        val j = node("J", 150.0, 120.0, 300.0, 90.0)
        val k = node("K", 150.0, 240.0, 50.0, 90.0)
        val ij = edge("eIJ", "I", "J")
        val ik = edge("eIK", "I", "K")

        route(listOf(i, j, k), listOf(ij, ik))

        assertFalse(polylinePassesThroughNode(ik.points.orEmpty(), j))
    }

    @Test
    fun placesIToJEndpointOnWideNodeBoundary() {
        val i = node("I", 150.0, 0.0, 50.0, 90.0)
        val j = node("J", 150.0, 120.0, 300.0, 90.0)
        val ij = edge("eIJ", "I", "J")

        route(listOf(i, j), listOf(ij))

        assertTrue(isOnBoundary(ij.points!!.last(), j))
    }

    @Test
    fun routesAroundExplicitWideObstacle() {
        val a = node("A", 100.0, 0.0, 50.0, 50.0)
        val b = node("B", 100.0, 100.0, 250.0, 50.0)
        val c = node("C", 100.0, 200.0, 50.0, 50.0)
        val ac = edge("eAC", "A", "C")

        route(listOf(a, b, c), listOf(ac))

        assertFalse(polylinePassesThroughNode(ac.points.orEmpty(), b))
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
        width: Double = 42.11,
        height: Double = 45.0,
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

    private fun polylinesStrictlyCross(
        first: List<SwimlanePoint>,
        second: List<SwimlanePoint>,
    ): Boolean {
        val firstSegments = orthogonalSegmentsForPoints(first)
        val secondSegments = orthogonalSegmentsForPoints(second)
        return firstSegments.any { firstSegment ->
            secondSegments.any { secondSegment ->
                orthogonalSegmentsStrictlyCross(
                    firstSegment.a,
                    firstSegment.b,
                    secondSegment.a,
                    secondSegment.b,
                )
            }
        }
    }

    private fun yRange(points: List<SwimlanePoint>): Double =
        points.maxOf(SwimlanePoint::y) - points.minOf(SwimlanePoint::y)

    private fun polylinePassesThroughNode(
        points: List<SwimlanePoint>,
        node: SwimlaneNode,
    ): Boolean {
        val rect = node.measuredRect() ?: return false
        val margin = 2.0
        return points.zipWithNext().any { (first, second) ->
            if (sameX(first, second)) {
                first.x > rect.left + margin &&
                    first.x < rect.right - margin &&
                    min(first.y, second.y) < rect.bottom - margin &&
                    max(first.y, second.y) > rect.top + margin
            } else if (sameY(first, second)) {
                first.y > rect.top + margin &&
                    first.y < rect.bottom - margin &&
                    min(first.x, second.x) < rect.right - margin &&
                    max(first.x, second.x) > rect.left + margin
            } else {
                false
            }
        }
    }

    private fun isOnBoundary(
        point: SwimlanePoint,
        node: SwimlaneNode,
        tolerance: Double = 1.0,
    ): Boolean {
        val rect = node.measuredRect() ?: return false
        val onLeftOrRight =
            (abs(point.x - rect.left) < tolerance || abs(point.x - rect.right) < tolerance) &&
                point.y >= rect.top - tolerance &&
                point.y <= rect.bottom + tolerance
        val onTopOrBottom =
            (abs(point.y - rect.top) < tolerance || abs(point.y - rect.bottom) < tolerance) &&
                point.x >= rect.left - tolerance &&
                point.x <= rect.right + tolerance
        return onLeftOrRight || onTopOrBottom
    }
}
