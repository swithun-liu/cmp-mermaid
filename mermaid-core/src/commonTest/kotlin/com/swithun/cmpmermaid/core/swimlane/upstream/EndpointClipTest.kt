package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Mermaid.js 12.0.0:
 * swimlanes/__tests__/direction.endpointClip.spec.ts.
 */
class EndpointClipTest {
    @Test
    fun clipsBuriedEndpointsToNodeBoundaries() {
        val nodes = nodes(
            node("A", 0.0, 0.0, 10.0, 10.0),
            node("B", 20.0, 0.0, 10.0, 10.0),
        )
        val route = edge("A_B", 0.0 to 0.0, 20.0 to 0.0)

        clipEdgeEndpointsToNodeBoundaries(listOf(route), nodes)

        assertEquals(points(5.0 to 0.0, 15.0 to 0.0), route.points)
    }

    @Test
    fun movesStraightSideToSideEndpointsAwayFromNodeCorners() {
        val nodes = nodes(
            node("A", 0.0, 0.0, 20.0, 20.0),
            node("B", 40.0, 0.0, 20.0, 40.0),
        )
        val route = edge("A_B", 10.0 to 8.0, 30.0 to 8.0)

        clipEdgeEndpointsToNodeBoundaries(listOf(route), nodes)

        assertEquals(points(10.0 to 6.0, 30.0 to 6.0), route.points)
    }

    @Test
    fun duplicatesSnappedEndpointsAndIsIdempotent() {
        val nodes = nodes(
            node("A", 0.0, 0.0, 10.0, 10.0),
            node("B", 20.0, 0.0, 10.0, 10.0),
        )
        val route = edge("A_B", -5.0 to 0.0, 10.0 to 0.0, 15.0 to 0.0)

        prepareEdgeEndpointsForRenderer(listOf(route), nodes)

        val expected = points(
            -5.0 to 0.0,
            -5.0 to 0.0,
            10.0 to 0.0,
            15.0 to 0.0,
            15.0 to 0.0,
        )
        assertEquals(expected, route.points)
        prepareEdgeEndpointsForRenderer(listOf(route), nodes)
        assertEquals(expected, route.points)
    }

    @Test
    fun keepsStraightRendererEdgesTwoPointWhileClearingCornerPorts() {
        val nodes = nodes(
            node("A", 0.0, 0.0, 20.0, 20.0),
            node("B", 40.0, 0.0, 20.0, 40.0),
        )
        val route = edge("A_B", 10.0 to 8.0, 30.0 to 8.0)

        prepareEdgeEndpointsForRenderer(listOf(route), nodes)

        assertEquals(points(10.0 to 6.0, 30.0 to 6.0), route.points)
    }

    @Test
    fun snapsRendererEndpointsToBoundaryEnteredByApproachSegment() {
        val nodes = nodes(
            node("A", 0.0, 0.0, 10.0, 10.0),
            node("B", 40.0, 40.0, 20.0, 20.0),
        )
        val route = edge(
            "A_B",
            5.0 to 0.0,
            40.0 to 0.0,
            40.0 to 50.0,
        )

        prepareEdgeEndpointsForRenderer(listOf(route), nodes)

        assertEquals(
            points(
                5.0 to 0.0,
                5.0 to 0.0,
                40.0 to 0.0,
                40.0 to 30.0,
                40.0 to 30.0,
            ),
            route.points,
        )
    }

    private fun nodes(vararg nodes: SwimlaneNode): Map<String, SwimlaneNode> =
        nodes.associateBy(SwimlaneNode::id)

    private fun node(
        id: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
    )

    private fun edge(
        id: String,
        vararg coordinates: Pair<Double, Double>,
    ): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = "A",
        end = "B",
        originalIndex = 0,
        points = points(*coordinates),
    )

    private fun points(
        vararg coordinates: Pair<Double, Double>,
    ): MutableList<SwimlanePoint> = coordinates
        .mapTo(mutableListOf()) { (x, y) -> SwimlanePoint(x, y) }
}
