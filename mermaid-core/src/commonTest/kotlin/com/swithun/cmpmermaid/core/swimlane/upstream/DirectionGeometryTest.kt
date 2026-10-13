package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/geometry.spec.ts.
 */
class DirectionGeometryTest {
    @Test
    fun countsStrictPerpendicularCrossings() {
        assertTrue(cross(-10.0 to 0.0, 10.0 to 0.0, 0.0 to -10.0, 0.0 to 10.0))
    }

    @Test
    fun countsTJunctionsWithOnlyOneSegmentEndpoint() {
        assertTrue(cross(-10.0 to 0.0, 10.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0))
    }

    @Test
    fun doesNotCountSharedEndpointAsCrossing() {
        assertFalse(cross(-10.0 to 0.0, 0.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0))
    }

    @Test
    fun doesNotCountCollinearOverlapOrNonOrthogonalSegments() {
        assertFalse(cross(-10.0 to 0.0, 10.0 to 0.0, -5.0 to 0.0, 5.0 to 0.0))
        assertFalse(cross(-10.0 to 0.0, 10.0 to 10.0, 0.0 to -10.0, 0.0 to 10.0))
    }

    @Test
    fun honorsCallerEpsilonForCrossingDetection() {
        assertFalse(
            orthogonalSegmentsCross(
                SwimlanePoint(-10.0, 0.0),
                SwimlanePoint(10.0, 0.00001),
                SwimlanePoint(0.0, -10.0),
                SwimlanePoint(0.0, 10.0),
                epsilon = 1e-6,
            ),
        )
    }

    @Test
    fun strictCrossingCountsOnlyInteriorIntersections() {
        assertTrue(strictCross(-10.0 to 0.0, 10.0 to 0.0, 0.0 to -10.0, 0.0 to 10.0))
        assertFalse(strictCross(-10.0 to 0.0, 10.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0))
        assertFalse(strictCross(-10.0 to 0.0, 0.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0))
    }

    @Test
    fun strictCrossingRejectsCollinearAndNearEndpointTouches() {
        assertFalse(strictCross(-10.0 to 0.0, 10.0 to 0.0, -5.0 to 0.0, 5.0 to 0.0))
        assertFalse(strictCross(-10.0 to 0.0, 10.0 to 0.0, 9.9995 to -10.0, 9.9995 to 10.0))
    }

    @Test
    fun collectsOnlyRealVisibleNodes() {
        val bounds = collectRealNodeBounds(
            listOf(
                node("A", x = 10.0, y = 20.0, width = 40.0, height = 20.0),
                node("group", x = 10.0, y = 20.0, width = 40.0, height = 20.0, isGroup = true),
                node("label", x = 10.0, y = 20.0, width = 40.0, height = 20.0)
                    .copy(isEdgeLabel = true),
                node("empty", x = 10.0, y = 20.0, width = 0.0, height = 20.0),
            ),
        )

        assertEquals(listOf("A"), bounds.nodeInfoById.keys.toList())
        assertEquals(
            listOf(RectEntry("A", SwimlaneRect(-10.0, 10.0, 30.0, 30.0))),
            bounds.realNodeRects,
        )
    }

    @Test
    fun segmentHitChecksExclusionsAndShrink() {
        val rects = listOf(RectEntry("A", SwimlaneRect(0.0, 0.0, 10.0, 10.0)))
        val start = SwimlanePoint(-5.0, 5.0)
        val end = SwimlanePoint(15.0, 5.0)

        assertTrue(segmentHitsAnyRect(start, end, rects))
        assertFalse(segmentHitsAnyRect(start, end, rects, excludeIds = listOf("A")))
        assertFalse(
            segmentHitsAnyRect(
                SwimlanePoint(-5.0, 0.5),
                SwimlanePoint(15.0, 0.5),
                rects,
                shrink = 1.0,
            ),
        )
    }

    @Test
    fun classifiesFourPointHvhAndVhvRoutes() {
        assertEquals(
            ThreeSegmentRouteKind.HVH,
            classifyThreeSegmentRoute(
                points(0.0 to 0.0, 10.0 to 0.0, 10.0 to 20.0, 30.0 to 20.0),
            )?.kind,
        )
        assertEquals(
            ThreeSegmentRouteKind.VHV,
            classifyThreeSegmentRoute(
                points(0.0 to 0.0, 0.0 to 10.0, 20.0 to 10.0, 20.0 to 30.0),
            )?.kind,
        )
        assertNull(
            classifyThreeSegmentRoute(
                points(0.0 to 0.0, 10.0 to 10.0, 20.0 to 10.0, 20.0 to 30.0),
            ),
        )
    }

    @Test
    fun detectsSameAxisSegmentOverlap() {
        assertTrue(overlap(0.0 to 0.0, 10.0 to 0.0, 5.0 to 0.0, 15.0 to 0.0))
        assertFalse(overlap(0.0 to 0.0, 10.0 to 0.0, 10.0 to 0.0, 15.0 to 0.0))
        assertFalse(overlap(0.0 to 0.0, 10.0 to 0.0, 5.0 to 1.0, 15.0 to 1.0))
    }

    @Test
    fun detectsCandidateConflictsAgainstVisibleEdges() {
        val self = edge("self", "A", "B", 0.0 to 0.0, 10.0 to 0.0)
        val crossing = edge("crossing", "C", "D", 5.0 to -5.0, 5.0 to 5.0)
        val overlap = edge("overlap", "E", "F", 20.0 to 0.0, 30.0 to 0.0)
        val layoutOnly = edge("layout", "G", "H", 0.0 to -5.0, 0.0 to 5.0).also {
            it.isLayoutOnly = true
        }

        assertTrue(
            segmentConflictsWithAnyEdge(
                SwimlanePoint(0.0, 0.0),
                SwimlanePoint(10.0, 0.0),
                listOf(self, crossing),
                excludeEdge = self,
            ),
        )
        assertTrue(
            segmentConflictsWithAnyEdge(
                SwimlanePoint(21.0, 0.0),
                SwimlanePoint(25.0, 0.0),
                listOf(self, overlap),
                excludeEdge = self,
            ),
        )
        assertFalse(
            segmentConflictsWithAnyEdge(
                SwimlanePoint(0.0, 0.0),
                SwimlanePoint(10.0, 0.0),
                listOf(self, layoutOnly),
                excludeEdge = self,
            ),
        )
    }

    private fun cross(
        a1: Pair<Double, Double>,
        b1: Pair<Double, Double>,
        a2: Pair<Double, Double>,
        b2: Pair<Double, Double>,
    ): Boolean = orthogonalSegmentsCross(a1.point(), b1.point(), a2.point(), b2.point())

    private fun strictCross(
        a1: Pair<Double, Double>,
        b1: Pair<Double, Double>,
        a2: Pair<Double, Double>,
        b2: Pair<Double, Double>,
    ): Boolean = orthogonalSegmentsStrictlyCross(a1.point(), b1.point(), a2.point(), b2.point())

    private fun overlap(
        a1: Pair<Double, Double>,
        b1: Pair<Double, Double>,
        a2: Pair<Double, Double>,
        b2: Pair<Double, Double>,
    ): Boolean = sameAxisSegmentsOverlap(a1.point(), b1.point(), a2.point(), b2.point())

    private fun node(
        id: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        isGroup: Boolean = false,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        isGroup = isGroup,
    )

    private fun edge(
        id: String,
        start: String,
        end: String,
        vararg coordinates: Pair<Double, Double>,
    ): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = start,
        end = end,
        originalIndex = 0,
        points = points(*coordinates),
    )

    private fun points(
        vararg coordinates: Pair<Double, Double>,
    ): MutableList<SwimlanePoint> = coordinates
        .mapTo(mutableListOf()) { (x, y) -> SwimlanePoint(x, y) }

    private fun Pair<Double, Double>.point(): SwimlanePoint = SwimlanePoint(first, second)
}
