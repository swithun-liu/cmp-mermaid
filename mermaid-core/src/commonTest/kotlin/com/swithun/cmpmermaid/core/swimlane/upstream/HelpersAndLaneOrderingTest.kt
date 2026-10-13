package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - swimlanes/__tests__/helpers.prepareLayout.spec.ts
 * - swimlanes/__tests__/helpers.laneHeights.spec.ts
 * - swimlanes/__tests__/laneOrdering.spec.ts
 */
class HelpersAndLaneOrderingTest {
    @Test
    fun marksOnlyTopLevelGroupsAsSwimlanes() {
        val lane = SwimlaneNode("lane", isGroup = true)
        val nested = SwimlaneNode("nested", parentId = "lane", isGroup = true)
        val layout = testLayout(listOf(lane, nested, SwimlaneNode("child", parentId = "nested")))

        prepareLayoutForSwimlanes(layout)

        assertTrue(lane.isSwimlane)
        assertEquals(false, nested.isSwimlane)
        assertEquals(SwimlaneDirection.TB, lane.direction)
    }

    @Test
    fun assignsLooseNodesToDefaultLaneWithLookAndFreeColourSlot() {
        val grouped = SwimlaneNode("grouped", parentId = "lane")
        val loose = SwimlaneNode("loose")
        val layout = testLayout(
            nodes = listOf(
                SwimlaneNode("lane", isGroup = true, colorIndex = 0, look = "handDrawn"),
                SwimlaneNode("nested", parentId = "lane", isGroup = true, colorIndex = 1),
                SwimlaneNode("lane2", isGroup = true, colorIndex = 2, look = "handDrawn"),
                grouped,
                loose,
            ),
            look = "handDrawn",
        )

        prepareLayoutForSwimlanes(layout)

        val defaultLane = layout.nodes.single { node -> node.id == DefaultSwimlaneId }
        assertTrue(defaultLane.isGroup)
        assertTrue(defaultLane.isSwimlane)
        assertEquals("handDrawn", defaultLane.look)
        assertEquals(3, defaultLane.colorIndex)
        assertEquals(DefaultSwimlaneId, loose.parentId)
        assertEquals("lane", grouped.parentId)
    }

    @Test
    fun defaultLaneStartsAtColourSlotZero() {
        val layout = testLayout(listOf(SwimlaneNode("loose")))

        prepareLayoutForSwimlanes(layout)

        val defaultLane = layout.nodes.single { node -> node.id == DefaultSwimlaneId }
        assertEquals(0, defaultLane.colorIndex)
        assertEquals(null, defaultLane.look)
    }

    @Test
    fun writeBackEqualizesTopLevelLaneHeightsAndPreservesHorizontalPadding() {
        val lane1 = SwimlaneNode("lane1", isGroup = true, padding = 20.0)
        val lane2 = SwimlaneNode("lane2", isGroup = true, padding = 20.0)
        val a = SwimlaneNode("A", parentId = "lane1", width = 80.0, height = 40.0)
        val b = SwimlaneNode("B", parentId = "lane1", width = 80.0, height = 40.0)
        val c = SwimlaneNode("C", parentId = "lane2", width = 80.0, height = 40.0)
        val graph = testGraph(listOf(lane1, lane2, a, b, c))

        writeBackToLayoutData(
            graph = graph,
            ordered = SwimlaneOrderedLayers(
                mutableListOf(
                    mutableListOf("A", "C"),
                    mutableListOf("B"),
                ),
            ),
            coordinates = SwimlaneCoordinates(
                x = linkedMapOf("A" to -100.0, "C" to 100.0, "B" to -100.0),
                y = linkedMapOf("A" to 0.0, "C" to 0.0, "B" to 120.0),
            ),
            nodeGap = 40.0,
            layerGap = 120.0,
        )

        assertTrue(lane1.height > 0.0)
        assertEquals(lane1.height, lane2.height, 0.000001)
        assertEquals(lane1.y, lane2.y)
        assertTrue(lane1.width >= a.width + 40.0)
    }

    @Test
    fun weightedLaneEdgesIgnoreLayoutOnlyAndSameLaneEdges() {
        val graph = laneGraph(
            lanes = listOf("lane1", "lane2", "lane3"),
            edgePairs = listOf(
                "lane1" to "lane2",
                "lane2" to "lane1",
                "lane1" to "lane3",
                "lane1" to "lane1",
            ),
        )
        graph.layout.edges += SwimlaneEdge(
            id = "layout-only",
            start = "lane1-node",
            end = "lane2-node",
            originalIndex = 4,
            isLayoutOnly = true,
        )

        assertEquals(
            listOf(
                WeightedLaneEdge("lane1", "lane2", 2),
                WeightedLaneEdge("lane1", "lane3", 1),
            ),
            buildWeightedLaneEdges(graph),
        )
    }

    @Test
    fun automaticLaneOrderingReducesCostDeterministically() {
        val graph = laneGraph(
            lanes = listOf("laneA", "laneB", "laneC", "laneD"),
            edgePairs = List(4) { "laneA" to "laneD" },
        )
        val weights = buildWeightedLaneEdges(graph)
        val sourceOrder = buildTopLaneOrder(graph)
        val optimized = optimizeTopLaneOrder(graph, restarts = 8)

        assertTrue(laneArrangementCost(optimized, weights) < laneArrangementCost(sourceOrder, weights))
        assertEquals(optimized, optimizeTopLaneOrder(graph, restarts = 8))
    }

    @Test
    fun automaticLaneOrderingPreservesSourceOrderOnCostTie() {
        val graph = laneGraph(
            lanes = listOf("laneA", "laneB", "laneC"),
            edgePairs = listOf(
                "laneA" to "laneB",
                "laneA" to "laneC",
                "laneB" to "laneC",
            ),
        )

        assertEquals(
            listOf("laneA", "laneB", "laneC"),
            optimizeTopLaneOrder(graph, restarts = 8),
        )
    }

    private fun laneGraph(
        lanes: List<String>,
        edgePairs: List<Pair<String, String>>,
    ): SwimlaneGraph {
        val laneNodes = lanes.reversed().map { lane ->
            SwimlaneNode(lane, isGroup = true)
        }
        val contentNodes = lanes.map { lane ->
            SwimlaneNode("$lane-node", parentId = lane)
        }
        return testGraph(
            nodes = laneNodes + contentNodes,
            graphNodeIds = (laneNodes + contentNodes).map(SwimlaneNode::id),
            edgePairs = edgePairs.map { (source, destination) ->
                "$source-node" to "$destination-node"
            },
        )
    }
}
