package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - swimlanes/__tests__/phase2.layering.spec.ts
 * - swimlanes/__tests__/phase2.compact.spec.ts
 * - swimlanes/__tests__/phase2.compact.lanes.spec.ts
 * - swimlanes/__tests__/phase3.ordering.spec.ts
 * - swimlanes/__tests__/phase4.coordinates.spec.ts
 * - swimlanes/__tests__/phase4.lanes.columns.spec.ts
 * - swimlanes/__tests__/phase4.lanes.spec.ts
 */
class LayeringOrderingCoordinatesTest {
    @Test
    fun chainUsesConsecutiveLayersWithoutDummies() {
        val layering = assignLayersLongestPath(
            testGraph(
                nodeIds = listOf("A", "B", "C", "D"),
                edgePairs = listOf("A" to "B", "B" to "C", "C" to "D"),
            ),
            SwimlaneLayeringOptions(compactSingleInput = false),
        )

        assertEquals(
            listOf(listOf("A"), listOf("B"), listOf("C"), listOf("D")),
            layering.layers,
        )
        assertTrue(layering.dummy.isEmpty())
    }

    @Test
    fun longEdgeCreatesOneDummyPerSkippedLayer() {
        val graph = testGraph(
            nodeIds = listOf("A", "B", "C", "D"),
            edgePairs = listOf("A" to "B", "B" to "C", "C" to "D", "A" to "D"),
        )
        val layering = assignLayersLongestPath(
            graph,
            SwimlaneLayeringOptions(compactSingleInput = false),
        )

        val proper = makeProperLayering(layering, graph)
        val dummies = proper.layering.dummy.toList()

        assertEquals(2, dummies.size)
        assertEquals(1, proper.layering.layers[1].count(dummies::contains))
        assertEquals(1, proper.layering.layers[2].count(dummies::contains))
        assertEquals(
            3,
            proper.graphWithDummies.edges.count { edge -> edge.id.startsWith("e3#") },
        )
    }

    @Test
    fun diamondUsesMinimalThreeLayerHeight() {
        val layering = assignLayersLongestPath(
            testGraph(
                nodeIds = listOf("A", "B", "C", "D"),
                edgePairs = listOf(
                    "A" to "B",
                    "A" to "C",
                    "B" to "D",
                    "C" to "D",
                ),
            ),
            SwimlaneLayeringOptions(compactSingleInput = false),
        )

        assertEquals(3, layering.layers.size)
        assertEquals(listOf("A"), layering.layers[0])
        assertEquals(setOf("B", "C"), layering.layers[1].toSet())
        assertEquals(listOf("D"), layering.layers[2])
    }

    @Test
    fun disconnectedComponentsStartAtLayerZero() {
        val layering = assignLayersLongestPath(
            testGraph(
                nodeIds = listOf("A", "B", "C", "D"),
                edgePairs = listOf("A" to "B", "C" to "D"),
            ),
            SwimlaneLayeringOptions(compactSingleInput = false),
        )

        assertEquals(0, layering.rankOf["A"])
        assertEquals(0, layering.rankOf["C"])
        assertEquals(1, layering.rankOf["B"])
        assertEquals(1, layering.rankOf["D"])
    }

    @Test
    fun longestPathOrdersSameRankChildrenByCrossCountsThenId() {
        val layering = assignLayersLongestPath(
            testGraph(
                nodeIds = listOf("A", "B", "C", "X"),
                edgePairs = listOf("A" to "B", "A" to "C", "X" to "B"),
            ),
            SwimlaneLayeringOptions(compactSingleInput = false),
        )

        assertEquals(listOf("A", "X"), layering.layers[0])
        assertEquals(listOf("B", "C"), layering.layers[1])
    }

    @Test
    fun compactSingleInputOnlyCompactsAcrossDifferentLanes() {
        val graph = testGraph(
            nodes = listOf(
                SwimlaneNode("lane1", isGroup = true),
                SwimlaneNode("lane2", isGroup = true),
                SwimlaneNode("A", parentId = "lane1"),
                SwimlaneNode("B", parentId = "lane1"),
                SwimlaneNode("C", parentId = "lane2"),
            ),
            graphNodeIds = listOf("A", "B", "C"),
            edgePairs = listOf("A" to "B", "A" to "C"),
        )

        val layering = assignLayersLongestPath(
            graph,
            SwimlaneLayeringOptions(compactSingleInput = true),
        )

        assertEquals(0, layering.rankOf["A"])
        assertEquals(1, layering.rankOf["B"])
        assertEquals(0, layering.rankOf["C"])
    }

    @Test
    fun compactSingleInputDoesNotCompactChainWithoutLaneInformation() {
        val layering = assignLayersLongestPath(
            testGraph(
                nodeIds = listOf("A", "B", "C"),
                edgePairs = listOf("A" to "B", "B" to "C"),
            ),
            SwimlaneLayeringOptions(compactSingleInput = true),
        )

        assertEquals(
            listOf(listOf("A"), listOf("B"), listOf("C")),
            layering.layers,
        )
    }

    @Test
    fun ignoreCrossLaneEdgesChangesOnlyCrossLaneRankPressure() {
        val baseline = assignLayersLongestPath(
            crossLaneFixture(),
            SwimlaneLayeringOptions(compactSingleInput = false),
        )
        val ignored = assignLayersLongestPath(
            crossLaneFixture(),
            SwimlaneLayeringOptions(
                compactSingleInput = false,
                ignoreCrossLaneEdges = true,
            ),
        )

        assertTrue(baseline.rankOf.getValue("E") > ignored.rankOf.getValue("E"))
        assertTrue(ignored.rankOf.getValue("H") > ignored.rankOf.getValue("E"))
    }

    @Test
    fun longestPathCountsLongEdgesWhenOrderingSameLayerChildren() {
        val graph = testGraph(
            nodes = listOf(
                SwimlaneNode("laneA", isGroup = true),
                SwimlaneNode("laneB", isGroup = true),
                SwimlaneNode("laneC", isGroup = true),
                SwimlaneNode("A", parentId = "laneA"),
                SwimlaneNode("B", parentId = "laneB"),
                SwimlaneNode("C", parentId = "laneC"),
                SwimlaneNode("X", parentId = "laneB"),
                SwimlaneNode("Y", parentId = "laneB"),
                SwimlaneNode("Z", parentId = "laneC"),
            ),
            graphNodeIds = listOf("A", "B", "C", "X", "Y", "Z"),
            edgePairs = listOf(
                "A" to "B",
                "A" to "C",
                "B" to "X",
                "B" to "Y",
                "X" to "Y",
                "C" to "Z",
            ),
        )

        val layering = assignLayersLongestPath(
            graph,
            SwimlaneLayeringOptions(compactSingleInput = true),
        )

        assertEquals(0, layering.rankOf["A"])
        assertEquals(0, layering.rankOf["B"])
        assertEquals(0, layering.rankOf["C"])
        assertEquals(
            listOf("A", "C", "B"),
            layering.layers[0].filter { id -> graph.nodeById[id]?.isGroup != true },
        )
    }

    @Test
    fun laneAwareCompactKeepsSiblingCrossLaneBranchBeforeDownstreamBranch() {
        val graph = testGraph(
            nodes = listOf(
                "S",
                "T",
                "Tester",
                "D",
            ).map { id -> SwimlaneNode(id, isGroup = true) } + listOf(
                SwimlaneNode("T5", parentId = "S"),
                SwimlaneNode("T4", parentId = "T"),
                SwimlaneNode("T6", parentId = "T"),
                SwimlaneNode("Te2", parentId = "Tester"),
                SwimlaneNode("D4", parentId = "Tester"),
                SwimlaneNode("D1", parentId = "D"),
                SwimlaneNode("D2", parentId = "D"),
                SwimlaneNode("D3", parentId = "D"),
                SwimlaneNode(
                    "label-Te2-D1",
                    parentId = "D",
                    isEdgeLabel = true,
                    isDummy = true,
                ),
                SwimlaneNode(
                    "label-Te2-T4",
                    parentId = "T",
                    isEdgeLabel = true,
                    isDummy = true,
                ),
                SwimlaneNode(
                    "label-D4-T6",
                    parentId = "T",
                    isEdgeLabel = true,
                    isDummy = true,
                ),
            ),
            graphNodeIds = listOf(
                "T5",
                "T4",
                "T6",
                "Te2",
                "D4",
                "D1",
                "D2",
                "D3",
                "label-Te2-D1",
                "label-Te2-T4",
                "label-D4-T6",
            ),
            edgePairs = listOf(
                "Te2" to "label-Te2-D1",
                "label-Te2-D1" to "D1",
                "D1" to "D2",
                "D2" to "D3",
                "D3" to "D4",
                "Te2" to "label-Te2-T4",
                "label-Te2-T4" to "T4",
                "D4" to "label-D4-T6",
                "label-D4-T6" to "T6",
                "T6" to "T5",
            ),
        )

        val layering = assignLayersLaneAwareCompact(
            graph,
            SwimlaneLayeringOptions(
                ignoreCrossLaneEdges = true,
                direction = SwimlaneDirection.LR,
            ),
        )

        assertTrue(
            layering.rankOf.getValue("label-Te2-T4") <
                layering.rankOf.getValue("label-D4-T6"),
        )
        assertTrue(layering.rankOf.getValue("T4") < layering.rankOf.getValue("T6"))
    }

    @Test
    fun laneAwareCompactAssignsExpectedCarSalesLayers() {
        val layering = assignLayersLaneAwareCompact(
            crossLaneFixture(),
            SwimlaneLayeringOptions(ignoreCrossLaneEdges = true),
        )

        assertEquals(
            linkedMapOf(
                "A" to 0,
                "B" to 0,
                "C" to 0,
                "D" to 1,
                "E" to 3,
                "F" to 3,
                "G" to 4,
                "H" to 2,
                "I" to 2,
                "J" to 3,
                "L" to 4,
                "M" to 4,
                "N" to 5,
            ),
            linkedMapOf<String, Int>().also { expected ->
                for (id in listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "L", "M", "N")) {
                    expected[id] = layering.rankOf.getValue(id)
                }
            },
        )
    }

    @Test
    fun bipartiteOrderingReachesZeroCrossings() {
        val graph = testGraph(
            nodeIds = listOf("U1", "U2", "L1", "L2"),
            edgePairs = listOf("U1" to "L1", "U2" to "L2"),
        )
        val layering = SwimlaneLayering(
            layers = mutableListOf(
                mutableListOf("U1", "U2"),
                mutableListOf("L2", "L1"),
            ),
            rankOf = linkedMapOf("U1" to 0, "U2" to 0, "L1" to 1, "L2" to 1),
        )

        val ordered = orderLayers(layering, graph)

        assertEquals(listOf("U1", "U2"), ordered.layers[0])
        assertEquals(listOf("L1", "L2"), ordered.layers[1])
        assertEquals(0, totalCrossings(ordered.layers, graph.edges))
    }

    @Test
    fun orderingIsDeterministicAndNeverIncreasesCraftedCrossings() {
        val graph = testGraph(
            nodeIds = listOf("A", "B", "C", "d", "e", "f"),
            edgePairs = listOf("A" to "e", "B" to "d", "C" to "f"),
        )
        val layering = SwimlaneLayering(
            layers = mutableListOf(
                mutableListOf("A", "B", "C"),
                mutableListOf("d", "e", "f"),
            ),
            rankOf = linkedMapOf(
                "A" to 0,
                "B" to 0,
                "C" to 0,
                "d" to 1,
                "e" to 1,
                "f" to 1,
            ),
        )
        val before = totalCrossings(layering.layers, graph.edges)

        val first = orderLayers(layering, graph)
        val second = orderLayers(layering, graph)

        assertTrue(totalCrossings(first.layers, graph.edges) <= before)
        assertEquals(first, second)
    }

    @Test
    fun transposeConsidersTheNextLayer() {
        val graph = testGraph(
            nodeIds = listOf("I", "J", "K", "L", "E"),
            edgePairs = listOf("I" to "J", "I" to "K", "J" to "E", "K" to "L"),
        )
        val layering = SwimlaneLayering(
            layers = mutableListOf(
                mutableListOf("I"),
                mutableListOf("J", "K"),
                mutableListOf("L", "E"),
            ),
            rankOf = linkedMapOf("I" to 0, "J" to 1, "K" to 1, "L" to 2, "E" to 2),
        )
        val before = totalCrossings(layering.layers, graph.edges)

        val ordered = orderLayers(layering, graph)

        assertEquals(listOf("K", "J"), ordered.layers[1])
        assertTrue(totalCrossings(ordered.layers, graph.edges) < before)
    }

    @Test
    fun singleLayerCoordinatesAreCenteredAndMonotone() {
        val coordinates = assignCoordinates(
            SwimlaneOrderedLayers(mutableListOf(mutableListOf("A", "B", "C"))),
            testGraph(listOf("A", "B", "C")),
            CoordinateOptions(nodeGap = 50.0, layerGap = 80.0),
        )

        assertEquals(listOf(-50.0, 0.0, 50.0), listOf(
            coordinates.x["A"],
            coordinates.x["B"],
            coordinates.x["C"],
        ))
        assertEquals(setOf(0.0), coordinates.y.values.toSet())
    }

    @Test
    fun laneColumnsAlignAcrossLayersAndSeparateDifferentLanes() {
        val graph = testGraph(
            nodes = listOf(
                SwimlaneNode("lane2", isGroup = true),
                SwimlaneNode("lane1", isGroup = true),
                SwimlaneNode("A", parentId = "lane1", width = 120.0, height = 50.0),
                SwimlaneNode("B", parentId = "lane1", width = 160.0, height = 50.0),
                SwimlaneNode("C", parentId = "lane2", width = 200.0, height = 50.0),
                SwimlaneNode("D", parentId = "lane1", width = 100.0, height = 50.0),
            ),
            graphNodeIds = listOf("A", "B", "C", "D"),
        )
        val coordinates = assignCoordinates(
            SwimlaneOrderedLayers(
                mutableListOf(
                    mutableListOf("A"),
                    mutableListOf("B", "C"),
                    mutableListOf("D"),
                ),
            ),
            graph,
            CoordinateOptions(nodeGap = 40.0, laneGap = 120.0, layerGap = 100.0),
        )

        assertEquals(coordinates.x["A"], coordinates.x["B"])
        assertEquals(coordinates.x["A"], coordinates.x["D"])
        assertTrue(coordinates.x.getValue("C") > coordinates.x.getValue("B"))
    }

    @Test
    fun laneGapIncludesBothNodeHalfWidths() {
        val graph = testGraph(
            nodes = listOf(
                SwimlaneNode("lane2", isGroup = true),
                SwimlaneNode("lane1", isGroup = true),
                SwimlaneNode("B", parentId = "lane1", width = 120.0, height = 50.0),
                SwimlaneNode("C", parentId = "lane2", width = 180.0, height = 50.0),
            ),
            graphNodeIds = listOf("B", "C"),
        )
        val coordinates = assignCoordinates(
            SwimlaneOrderedLayers(mutableListOf(mutableListOf("B", "C"))),
            graph,
            CoordinateOptions(nodeGap = 40.0, laneGap = 100.0, layerGap = 80.0),
        )

        assertTrue(coordinates.x.getValue("C") - coordinates.x.getValue("B") >= 250.0)
    }

    private fun crossLaneFixture(): SwimlaneGraph {
        val lanes = listOf("Car", "Sales", "Constr", "Legal", "Fun").map { id ->
            SwimlaneNode(id, isGroup = true)
        }
        val parents = linkedMapOf(
            "A" to "Car",
            "B" to "Sales",
            "C" to "Constr",
            "D" to "Constr",
            "E" to "Constr",
            "F" to "Fun",
            "G" to "Fun",
            "H" to "Constr",
            "I" to "Legal",
            "J" to "Legal",
            "K" to "Legal",
            "L" to "Constr",
            "M" to "Sales",
            "N" to "Constr",
        )
        return testGraph(
            nodes = lanes + parents.map { (id, parent) -> SwimlaneNode(id, parentId = parent) },
            graphNodeIds = parents.keys.toList(),
            edgePairs = listOf(
                "A" to "B",
                "B" to "C",
                "C" to "D",
                "D" to "E",
                "E" to "F",
                "F" to "G",
                "D" to "H",
                "H" to "I",
                "I" to "J",
                "J" to "E",
                "I" to "K",
                "K" to "L",
                "L" to "M",
                "L" to "N",
            ),
        )
    }
}
