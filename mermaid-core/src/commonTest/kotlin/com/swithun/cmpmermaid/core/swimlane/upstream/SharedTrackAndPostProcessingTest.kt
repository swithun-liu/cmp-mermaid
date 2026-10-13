package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - swimlanes/__tests__/direction.sharedTrackNudging.spec.ts
 * - swimlanes/__tests__/postProcessing.spec.ts
 */
class SharedTrackAndPostProcessingTest {
    @Test
    fun separatesNearParallelInteriorRailsWithLongProjectedOverlap() {
        val edges = listOf(
            edge(
                "detoured",
                null,
                null,
                0.0 to 0.0,
                50.0 to 0.0,
                50.0 to 100.0,
                80.0 to 100.0,
            ),
            edge("nearby", null, null, 52.0 to 0.0, 52.0 to 100.0),
        )

        nudgeSharedInteriorSubpaths(edges, emptyMap())

        val detouredX = verticalSegmentX(edges[0].points.orEmpty())
        assertNotNull(detouredX)
        assertTrue(abs(detouredX - 52.0) >= 7.0)
    }

    @Test
    fun separatesCloseFixtureTwelveExitRails() {
        val edges = listOf(
            edge(
                "L_I_exit_0",
                "I",
                "exit",
                -113.64374732971191 to 955.0,
                220.83124923706055 to 955.0,
                220.83124923706055 to 1083.5643920898438,
                240.546875 to 1083.5643920898438,
            ),
            edge(
                "L_H_I_0",
                null,
                null,
                243.83124923706055 to 973.5,
                -113.64374732971191 to 973.5,
            ),
            edge(
                "L_B_exit_0",
                "B",
                "exit",
                -131.5906219482422 to 192.0,
                -131.5906219482422 to 928.0,
                222.77187538146973 to 928.0,
                222.77187538146973 to 1038.0643920898438,
                258.328125 to 1038.0643920898438,
                258.328125 to 1058.0643920898438,
            ),
        )
        val nodes = listOf(
            node("I", -131.5906219482422, 973.5, 35.89374923706055, 45.0),
            node("B", -131.5906219482422, 146.5, 121.3125, 91.0),
            node("exit", 267.21875, 1083.5643920898438, 53.34375, 51.0),
        ).associateBy(SwimlaneNode::id)

        nudgeSharedInteriorSubpaths(edges, nodes)

        val iExitX = verticalSegmentXOverlappingY(
            edges[0].points.orEmpty(),
            1016.0,
            1038.0643920898438,
        )
        val bExitX = verticalSegmentXOverlappingY(
            edges[2].points.orEmpty(),
            1016.0,
            1038.0643920898438,
        )
        assertNotNull(iExitX)
        assertNotNull(bExitX)
        assertTrue(abs(iExitX - bExitX) >= 7.0)
        assertEquals(SwimlanePoint(-131.5906219482422, 996.0), edges[0].points!!.first())
    }

    @Test
    fun nudgesLabelledEdgeOffSharedTrackAndReanchorsLabel() {
        val label = node(
            id = "edge-label-A-B",
            x = 0.0,
            y = 0.0,
            width = 70.0,
            height = 10.0,
        ).copy(isEdgeLabel = true)
        val labelled = edge(
            "A_B",
            "A",
            "B",
            20.0 to 0.0,
            50.0 to 0.0,
            50.0 to 100.0,
            80.0 to 100.0,
        ).also { it.labelNodeId = label.id }
        val foreign = edge(
            "foreign",
            null,
            null,
            50.0 to 0.0,
            50.0 to 100.0,
        )
        val layout = testLayout(
            nodes = listOf(
                node("lane", 50.0, 50.0, 220.0, 180.0, isGroup = true),
                node("A", 0.0, 0.0, 40.0, 40.0),
                node("B", 100.0, 100.0, 40.0, 40.0),
                label,
            ),
            edges = listOf(labelled, foreign),
        )

        postProcessSwimlaneLayout(layout, SwimlaneDirection.TB)

        var maximumOverlap = 0.0
        val labelledPoints = labelled.points.orEmpty()
        val foreignPoints = foreign.points.orEmpty()
        val labelledSegments = orthogonalSegmentsForPoints(labelledPoints)
        val foreignSegments = orthogonalSegmentsForPoints(foreignPoints)
        for (labelledSegment in labelledSegments) {
            for (foreignSegment in foreignSegments) {
                maximumOverlap = max(
                    maximumOverlap,
                    sameAxisSegmentOverlapLength(labelledSegment, foreignSegment),
                )
            }
        }
        assertTrue(maximumOverlap < 8.0)

        val labelledInterior = labelledPoints
            .zipWithNext()
            .withIndex()
            .firstOrNull { (indexed, pair) ->
                indexed > 0 &&
                    indexed < labelledPoints.lastIndex - 1 &&
                    abs(pair.first.x - pair.second.x) < DirectionEpsilon
            }
            ?.value
            ?.first
        assertNotNull(labelledInterior)
        assertEquals(labelledInterior.x, label.x!!, 0.000001)
    }

    private fun verticalSegmentX(
        points: List<SwimlanePoint>,
        minimumOverlap: Double = 80.0,
    ): Double? = points.zipWithNext().firstNotNullOfOrNull { (first, second) ->
        if (
            abs(first.x - second.x) < DirectionEpsilon &&
            abs(first.y - second.y) >= minimumOverlap
        ) {
            first.x
        } else {
            null
        }
    }

    private fun verticalSegmentXOverlappingY(
        points: List<SwimlanePoint>,
        y1: Double,
        y2: Double,
    ): Double? = points.zipWithNext().firstNotNullOfOrNull { (first, second) ->
        if (abs(first.x - second.x) >= DirectionEpsilon) {
            null
        } else {
            val overlap = max(
                0.0,
                min(max(first.y, second.y), y2) - max(min(first.y, second.y), y1),
            )
            if (overlap > 0.0) first.x else null
        }
    }

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
        start: String?,
        end: String?,
        vararg coordinates: Pair<Double, Double>,
    ): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = start,
        end = end,
        originalIndex = 0,
        points = coordinates.mapTo(mutableListOf()) { (x, y) -> SwimlanePoint(x, y) },
    )
}
