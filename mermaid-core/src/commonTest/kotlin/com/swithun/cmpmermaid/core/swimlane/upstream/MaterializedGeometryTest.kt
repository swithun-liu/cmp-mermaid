package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/__tests__/direction.materializedGeometry.spec.ts.
 */
class MaterializedGeometryTest {
    @Test
    fun separatesSharedVisibleTerminalRailsOnTheSameNodeFace() {
        val nodes = nodes(
            node("A", -40.0, -30.0, 10.0, 10.0),
            node("B", 0.0, 0.0, 10.0, 80.0),
        )
        val edges = listOf(
            edge("A_B_1", "A", "B", -30.0 to 0.0, -5.0 to 0.0),
            edge("A_B_2", "A", "B", -30.0 to 0.0, -5.0 to 0.0),
        )

        separateSharedRenderedTerminalLanes(edges, nodes)

        val terminalYs = edges.map { it.points!!.last().y }.sorted()
        assertNotEquals(terminalYs[0], terminalYs[1])
        assertTrue(0.0 in terminalYs)
    }

    @Test
    fun separatesNearParallelTerminalRailsOnTheSameNodeFace() {
        val nodes = nodes(
            node("A", -40.0, -30.0, 10.0, 10.0),
            node("B", 0.0, 0.0, 10.0, 60.0),
            node("C", -40.0, 30.0, 10.0, 10.0),
        )
        val edges = listOf(
            edge("A_B", "A", "B", -90.0 to -4.0, -5.0 to -4.0),
            edge("B_C", "B", "C", -5.0 to 4.0, -90.0 to 4.0),
        )

        separateSharedRenderedTerminalLanes(edges, nodes)

        val terminalYs = listOf(
            edges[0].points!!.last().y,
            edges[1].points!!.first().y,
        ).sorted()
        assertTrue(terminalYs[1] - terminalYs[0] >= 16.0)
    }

    @Test
    fun collapsesAProvablyRedundantRectangularDogleg() {
        val route = edge(
            "A_B",
            "A",
            "B",
            0.0 to 0.0,
            10.0 to 0.0,
            10.0 to 10.0,
            0.0 to 10.0,
            0.0 to 20.0,
        )

        collapseRedundantRectangularDoglegs(listOf(route), emptyMap())

        assertEquals(points(0.0 to 0.0, 0.0 to 20.0), route.points)
    }

    @Test
    fun liftsASameSideRailAwayFromAnInterveningNodeBorder() {
        val nodes = nodes(
            node("26", -166.0, 54.0, 232.0, 66.0),
            node("27", 830.0, 54.0, 232.0, 66.0),
            node("28", 166.0, 54.0, 232.0, 108.0),
        )
        val route = edge(
            "L_26_27_0",
            "26",
            "27",
            -166.0 to 21.0,
            -166.0 to 1.0,
            830.0 to 1.0,
            830.0 to 21.0,
        )

        liftObstacleHuggingSameSideRails(listOf(route), nodes)

        assertEquals(
            points(
                -166.0 to 21.0,
                -166.0 to -20.0,
                830.0 to -20.0,
                830.0 to 21.0,
            ),
            route.points,
        )
    }

    @Test
    fun raisesTopLaneTitleBandsAboveAClearSameSideRail() {
        val nodes = nodes(
            node("26", -166.0, 54.0, 232.0, 66.0),
            node("27", 830.0, 54.0, 232.0, 66.0),
            node("28", 166.0, 54.0, 232.0, 108.0),
            node(
                id = "General_Manager",
                x = 166.0,
                y = 54.0,
                width = 996.0,
                height = 180.0,
                isGroup = true,
                direction = SwimlaneDirection.TB,
                titleRect = SwimlaneRect(-332.0, -36.0, 664.0, -15.0),
            ),
        )
        val route = edge(
            "L_26_27_0",
            "26",
            "27",
            -166.0 to 21.0,
            -166.0 to 1.0,
            830.0 to 1.0,
            830.0 to 21.0,
        )

        liftObstacleHuggingSameSideRails(listOf(route), nodes)
        liftTopLaneTitleBandsAboveRails(listOf(route), nodes)

        assertEquals(
            points(
                -166.0 to 21.0,
                -166.0 to -20.0,
                830.0 to -20.0,
                830.0 to 21.0,
            ),
            route.points,
        )
        val lane = nodes.getValue("General_Manager")
        assertEquals(49.5, lane.y)
        assertEquals(189.0, lane.height)
        assertEquals(
            SwimlaneRect(-332.0, -45.0, 664.0, -24.0),
            lane.groupTitleRect,
        )
    }

    @Test
    fun shiftsLeftToRightLaneTitleBandsLeftOfAClearLeftSideRail() {
        val nodes = nodes(
            node(
                "BOD",
                0.0,
                100.0,
                200.0,
                200.0,
                isGroup = true,
                direction = SwimlaneDirection.LR,
                titleRect = SwimlaneRect(-100.0, 0.0, -64.0, 200.0),
            ),
            node(
                "Finance_Head",
                0.0,
                300.0,
                200.0,
                200.0,
                isGroup = true,
                direction = SwimlaneDirection.LR,
                titleRect = SwimlaneRect(-100.0, 200.0, -64.0, 400.0),
            ),
        )
        val route = edge(
            "L_27_28_0",
            "27",
            "28",
            -80.0 to 350.0,
            -80.0 to 300.0,
            -90.0 to 300.0,
            -90.0 to 50.0,
            -80.0 to 50.0,
        )

        shiftLeftLaneTitleBandsLeftOfRails(listOf(route), nodes)

        val bod = nodes.getValue("BOD")
        assertEquals(-15.0, bod.x)
        assertEquals(230.0, bod.width)
        assertEquals(
            SwimlaneRect(-130.0, 0.0, -94.0, 200.0),
            bod.groupTitleRect,
        )
        val finance = nodes.getValue("Finance_Head")
        assertEquals(-15.0, finance.x)
        assertEquals(230.0, finance.width)
        assertEquals(
            SwimlaneRect(-130.0, 200.0, -94.0, 400.0),
            finance.groupTitleRect,
        )
    }

    @Test
    fun swapsSharedDestinationTerminalTailsWhenBothSwapsRemoveACrossing() {
        val cExitTopPort = SwimlanePoint(297.553125, 1171.1287841796875)
        val dExitRightPort = SwimlanePoint(318.890625, 1196.6287841796875)
        val nodes = nodes(
            node("C", 292.21875, 287.5, 150.05624389648438, 91.0),
            node("D", 292.21875, 570.5, 94.11250305175781, 91.0),
            node("exit", 292.21875, 1196.6287841796875, 53.34375, 51.0),
        )
        val edges = listOf(
            edge(
                "L_C_exit_0",
                "C",
                "exit",
                367.2468719482422 to 287.5,
                387.2468719482422 to 287.5,
                387.2468719482422 to 1151.1287841796875,
                297.553125 to 1151.1287841796875,
                cExitTopPort.x to cExitTopPort.y,
            ),
            edge(
                "L_D_exit_0",
                "D",
                "exit",
                339.2750015258789 to 570.5,
                359.2750015258789 to 570.5,
                359.2750015258789 to 1196.6287841796875,
                dExitRightPort.x to dExitRightPort.y,
            ),
        )

        assertEquals(1, strictCrossingCount(edges))
        swapDestinationTerminalTailsToReduceCrossings(edges, nodes)

        assertEquals(0, strictCrossingCount(edges))
        assertEquals(dExitRightPort, edges[0].points!!.last())
        assertEquals(cExitTopPort, edges[1].points!!.last())
    }

    @Test
    fun reassignsOverlappingExternalRailChannelsAsACrossingMinimizingBundle() {
        val nodes = nodes(
            node("26", -22.0, 664.0, 116.0, 64.0),
            node("27", -22.0, 1602.0, 116.0, 116.0),
            node("28", -22.0, 1050.0, 116.0, 80.0),
        )
        val edges = listOf(
            edge(
                "L_26_27_0",
                "26",
                "27",
                -80.0 to 664.0,
                -100.0 to 664.0,
                -100.0 to 1660.0,
                -80.0 to 1660.0,
            ),
            edge(
                "L_27_28_0",
                "27",
                "28",
                36.0 to 1544.0,
                65.0 to 1544.0,
                65.0 to 1467.0,
                -126.4 to 1467.0,
                -126.4 to 1132.0,
                36.0 to 1132.0,
                36.0 to 1050.0,
            ),
        )

        assertEquals(2, strictCrossingCount(edges))
        reassignCrossingExternalRailChannels(edges, nodes)

        assertEquals(0, strictCrossingCount(edges))
        assertEquals(-126.4, edges[0].points!![1].x)
        assertEquals(-100.0, edges[1].points!![3].x)
    }

    @Test
    fun shortcutsADominatedOrthogonalJogWhenClearanceIsPreserved() {
        val nodes = nodes(
            node("23", 36.0, 1328.0, 232.0, 108.0),
            node("27", 36.0, 1660.0, 232.0, 66.0),
            node("28", 36.0, 996.0, 232.0, 108.0),
        )
        val route = edge(
            "L_27_28_0",
            "27",
            "28",
            36.0 to 1544.0,
            65.0 to 1544.0,
            65.0 to 1467.0,
            -100.0 to 1467.0,
            -100.0 to 1132.0,
            36.0 to 1132.0,
            36.0 to 1050.0,
        )

        shortcutRedundantOrthogonalJogs(listOf(route), nodes)

        assertEquals(
            points(
                36.0 to 1544.0,
                36.0 to 1467.0,
                -100.0 to 1467.0,
                -100.0 to 1132.0,
                36.0 to 1132.0,
                36.0 to 1050.0,
            ),
            route.points,
        )
    }

    @Test
    fun shortcutsARedundantJogEvenWhenEndpointNodesAreUnavailable() {
        val route = edge(
            "missing_endpoint_edge",
            "missing-start",
            "missing-end",
            36.0 to 1544.0,
            65.0 to 1544.0,
            65.0 to 1467.0,
            -100.0 to 1467.0,
            -100.0 to 1132.0,
            36.0 to 1132.0,
            36.0 to 1050.0,
        )

        shortcutRedundantOrthogonalJogs(listOf(route), emptyMap())

        assertEquals(
            points(
                36.0 to 1544.0,
                65.0 to 1544.0,
                65.0 to 1132.0,
                36.0 to 1132.0,
                36.0 to 1050.0,
            ),
            route.points,
        )
    }

    private fun strictCrossingCount(edges: List<SwimlaneEdge>): Int {
        var count = 0
        for (firstIndex in edges.indices) {
            val firstSegments = orthogonalSegmentsForPoints(edges[firstIndex].points.orEmpty())
            for (secondIndex in firstIndex + 1 until edges.size) {
                val secondSegments =
                    orthogonalSegmentsForPoints(edges[secondIndex].points.orEmpty())
                for (first in firstSegments) {
                    for (second in secondSegments) {
                        if (
                            orthogonalSegmentsStrictlyCross(
                                first.a,
                                first.b,
                                second.a,
                                second.b,
                            )
                        ) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    private fun nodes(vararg nodes: SwimlaneNode): Map<String, SwimlaneNode> =
        nodes.associateBy(SwimlaneNode::id)

    private fun node(
        id: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        isGroup: Boolean = false,
        direction: SwimlaneDirection? = null,
        titleRect: SwimlaneRect? = null,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        isGroup = isGroup,
        direction = direction,
        groupTitleRect = titleRect,
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
}
