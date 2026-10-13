package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DirectionAndPipelineTest {
    @Test
    fun orthogonalizesADiagonalSegmentWithAnLBend() {
        assertEquals(
            points(0.0 to 0.0, 10.0 to 0.0, 10.0 to 10.0),
            orthogonalizePolyline(points(0.0 to 0.0, 10.0 to 10.0)),
        )
    }

    @Test
    fun preservesIncomingVerticalOrientationWhenInsertingAnLBend() {
        assertEquals(
            points(0.0 to 0.0, 0.0 to 10.0, 0.0 to 20.0, 10.0 to 20.0),
            orthogonalizePolyline(
                points(0.0 to 0.0, 0.0 to 10.0, 10.0 to 20.0),
            ),
        )
    }

    @Test
    fun dedupesConsecutivePointsDuringOrthogonalization() {
        assertEquals(
            points(0.0 to 0.0, 0.0 to 10.0),
            orthogonalizePolyline(
                points(0.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0),
            ),
        )
    }

    @Test
    fun simplifyRemovesSpikesAndStrictlyIntermediatePoints() {
        assertEquals(
            points(0.0 to 0.0, 0.0 to 10.0),
            simplifyPolyline(
                points(0.0 to 0.0, 10.0 to 0.0, 0.0 to 0.0, 0.0 to 10.0),
            ),
        )
        assertEquals(
            points(0.0 to 0.0, 10.0 to 0.0, 10.0 to 5.0),
            simplifyPolyline(
                points(
                    0.0 to 0.0,
                    5.0 to 0.0,
                    10.0 to 0.0,
                    10.0 to 5.0,
                ),
            ),
        )
    }

    @Test
    fun collapsesShortPerpendicularDestinationStub() {
        val destination = node("B", x = 100.0, y = 110.0, width = 40.0, height = 40.0)
        val route = edge(
            "A_B",
            "A",
            "B",
            0.0 to 120.0,
            70.0 to 120.0,
            70.0 to 100.0,
            78.0 to 100.0,
        )

        collapseShortTerminalStub(listOf(route), mapOf("B" to destination))

        assertEquals(
            points(0.0 to 120.0, 100.0 to 120.0, 100.0 to 130.0),
            route.points,
        )
    }

    @Test
    fun rewritesNonCollinearHvhDetourToThreePointLShape() {
        val nodes = listOf(
            node("A", x = 0.0, y = 0.0, width = 40.0, height = 40.0),
            node("B", x = 100.0, y = 100.0, width = 40.0, height = 40.0),
        )
        val route = edge(
            "A_B",
            "A",
            "B",
            20.0 to 0.0,
            60.0 to 0.0,
            60.0 to 80.0,
            100.0 to 80.0,
        )

        portSwapToLShape(listOf(route), nodes)

        assertEquals(
            points(0.0 to 20.0, 0.0 to 80.0, 100.0 to 80.0),
            route.points,
        )
    }

    @Test
    fun longestPathDoesNotCompactAChainWithoutLaneInformation() {
        val graph = graph(
            nodeIds = listOf("A", "B", "C"),
            edgePairs = listOf("A" to "B", "B" to "C"),
        )

        val layering = assignLayersLongestPath(
            graph,
            SwimlaneLayeringOptions(compactSingleInput = true),
        )

        assertEquals(
            listOf(listOf("A"), listOf("B"), listOf("C")),
            layering.layers,
        )
    }

    @Test
    fun longestPathOnlyCompactsSingleInputAcrossDifferentLanes() {
        val lane1 = SwimlaneNode(id = "lane1", isGroup = true)
        val lane2 = SwimlaneNode(id = "lane2", isGroup = true)
        val graph = graph(
            nodes = listOf(
                lane1,
                lane2,
                SwimlaneNode(id = "A", parentId = "lane1"),
                SwimlaneNode(id = "B", parentId = "lane1"),
                SwimlaneNode(id = "C", parentId = "lane2"),
            ),
            graphNodeIds = listOf("A", "B", "C"),
            edgePairs = listOf("A" to "B", "A" to "C"),
        )

        val layering = assignLayersLongestPath(
            graph,
            SwimlaneLayeringOptions(compactSingleInput = true),
        )

        assertEquals(0, layering.rankOf.getValue("A"))
        assertEquals(1, layering.rankOf.getValue("B"))
        assertEquals(0, layering.rankOf.getValue("C"))
    }

    @Test
    fun coordinatesCenterSingleLayerAndSeparateLaneColumns() {
        val plain = graph(nodeIds = listOf("A", "B", "C"))
        val plainCoordinates = assignCoordinates(
            SwimlaneOrderedLayers(mutableListOf(mutableListOf("A", "B", "C"))),
            plain,
            CoordinateOptions(nodeGap = 50.0, layerGap = 80.0),
        )
        assertEquals(listOf(-50.0, 0.0, 50.0), listOf(
            plainCoordinates.x.getValue("A"),
            plainCoordinates.x.getValue("B"),
            plainCoordinates.x.getValue("C"),
        ))
        assertEquals(setOf(0.0), plainCoordinates.y.values.toSet())

        val laneGraph = graph(
            nodes = listOf(
                SwimlaneNode(id = "lane2", isGroup = true),
                SwimlaneNode(id = "lane1", isGroup = true),
                node("A", width = 120.0, height = 50.0, parentId = "lane1"),
                node("B", width = 160.0, height = 50.0, parentId = "lane1"),
                node("C", width = 200.0, height = 50.0, parentId = "lane2"),
                node("D", width = 100.0, height = 50.0, parentId = "lane1"),
            ),
            graphNodeIds = listOf("A", "B", "C", "D"),
        )
        val laneCoordinates = assignCoordinates(
            SwimlaneOrderedLayers(
                mutableListOf(
                    mutableListOf("A"),
                    mutableListOf("B", "C"),
                    mutableListOf("D"),
                ),
            ),
            laneGraph,
            CoordinateOptions(nodeGap = 40.0, laneGap = 120.0, layerGap = 100.0),
        )
        assertEquals(laneCoordinates.x["A"], laneCoordinates.x["B"])
        assertEquals(laneCoordinates.x["A"], laneCoordinates.x["D"])
        assertTrue(laneCoordinates.x.getValue("C") > laneCoordinates.x.getValue("B"))
    }

    @Test
    fun sugiyamaPipelineCoversChainCycleAndBipartiteOrdering() {
        val chain = sugiyamaLayout(
            graph(
                nodeIds = listOf("A", "B", "C", "D"),
                edgePairs = listOf("A" to "B", "B" to "C", "C" to "D"),
            ),
            SwimlaneLayoutOptions(
                nodeGap = 40.0,
                layerGap = 80.0,
                ignoreCrossLaneEdges = false,
            ),
        )
        assertTrue(chain.reversed.isEmpty())
        assertEquals(
            listOf(0.0, 80.0, 160.0, 240.0),
            listOf("A", "B", "C", "D").map(chain.coordinates.y::getValue),
        )

        val cycle = sugiyamaLayout(
            graph(
                nodeIds = listOf("A", "B"),
                edgePairs = listOf("A" to "B", "B" to "A"),
            ),
        )
        assertEquals(1, cycle.reversed.size)

        val bipartiteGraph = graph(
            nodeIds = listOf("A", "B", "C", "X", "Y", "Z"),
            edgePairs = listOf("A" to "X", "B" to "Y", "C" to "Z"),
        )
        val bipartite = sugiyamaLayout(bipartiteGraph)
        assertEquals(0, totalCrossings(bipartite.ordered.layers, bipartiteGraph.edges))
    }

    @Test
    fun labelledReturnLoopMatchesUpstreamCycleRemovalAndRanks() {
        val groups = listOf("Closure", "Execution", "Assessment", "Intake").map { id ->
            SwimlaneNode(id = id, isGroup = true, padding = 8.0)
        }
        val data = testLayout(
            nodes = groups + listOf(
                node("N1", parentId = "Intake"),
                node("N2", parentId = "Assessment"),
                node("N3", parentId = "Execution"),
                node("N4", parentId = "Closure"),
            ),
            edges = mutableListOf(
                SwimlaneEdge("L_N1_N2_0", "N1", "N2", 0),
                SwimlaneEdge("L_N2_N3_0", "N2", "N3", 1),
                SwimlaneEdge("L_N3_N4_0", "N3", "N4", 2),
                SwimlaneEdge("L_N4_N2_0", "N4", "N2", 3),
                SwimlaneEdge(
                    "L_N1_N3_0",
                    "N1",
                    "N3",
                    4,
                    hasLabel = true,
                    width = 62.0,
                    height = 25.0,
                ),
                SwimlaneEdge(
                    "L_N4_N1_0",
                    "N4",
                    "N1",
                    5,
                    hasLabel = true,
                    width = 42.0,
                    height = 25.0,
                ),
            ),
        )
        createEdgeLabelNodes(data)
        val result = sugiyamaLayout(
            toGraphView(data),
            SwimlaneLayoutOptions(
                nodeGap = 40.0,
                layerGap = 50.0,
                ignoreCrossLaneEdges = true,
                optimizeRanksByCrossings = true,
                automaticLaneOrdering = false,
                direction = SwimlaneDirection.TB,
            ),
        )

        assertEquals(
            listOf(
                "edge-label-N4-N1-L_N4_N1_0->N1",
                "N2->N3",
            ),
            result.reversed.map { edge -> "${edge.src}->${edge.dst}" },
        )
        assertEquals(
            listOf(0, 1, 1, 1),
            listOf("N1", "N2", "N3", "N4").map(result.layering.rankOf::getValue),
        )
    }

    private fun graph(
        nodeIds: List<String>,
        edgePairs: List<Pair<String, String>> = emptyList(),
    ): SwimlaneGraph = graph(
        nodes = nodeIds.map(::SwimlaneNode),
        graphNodeIds = nodeIds,
        edgePairs = edgePairs,
    )

    private fun graph(
        nodes: List<SwimlaneNode>,
        graphNodeIds: List<String>,
        edgePairs: List<Pair<String, String>> = emptyList(),
    ): SwimlaneGraph {
        val edges = edgePairs.mapIndexed { index, (source, destination) ->
            val edge = SwimlaneEdge("e$index", source, destination, index)
            SwimlaneEdgeRef(edge.id, source, destination, ref = edge)
        }
        val nodeById = LinkedHashMap(nodes.associateBy(SwimlaneNode::id))
        return SwimlaneGraph(
            nodes = graphNodeIds,
            edges = edges,
            layout = SwimlaneLayoutData(
                nodes = nodes.toMutableList(),
                edges = edges.map(SwimlaneEdgeRef::ref).toMutableList(),
                direction = SwimlaneDirection.TB,
                nodeGap = 50.0,
                layerGap = 80.0,
                ignoreCrossLaneEdges = true,
                optimizeRanksByCrossings = true,
                automaticLaneOrdering = false,
            ),
            nodeById = nodeById,
        )
    }

    private fun node(
        id: String,
        x: Double? = null,
        y: Double? = null,
        width: Double = 0.0,
        height: Double = 0.0,
        parentId: String? = null,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        x = x,
        y = y,
        width = width,
        height = height,
        parentId = parentId,
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
