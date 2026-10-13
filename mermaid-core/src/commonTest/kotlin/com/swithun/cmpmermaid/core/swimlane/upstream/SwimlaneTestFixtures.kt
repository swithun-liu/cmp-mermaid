package com.swithun.cmpmermaid.core.swimlane.upstream

internal fun testLayout(
    nodes: List<SwimlaneNode>,
    edges: List<SwimlaneEdge> = emptyList(),
    direction: SwimlaneDirection = SwimlaneDirection.TB,
    look: String? = null,
): SwimlaneLayoutData = SwimlaneLayoutData(
    nodes = nodes.toMutableList(),
    edges = edges.toMutableList(),
    direction = direction,
    nodeGap = 50.0,
    layerGap = 80.0,
    ignoreCrossLaneEdges = true,
    optimizeRanksByCrossings = true,
    automaticLaneOrdering = false,
    look = look,
)

internal fun testGraph(
    nodeIds: List<String>,
    edgePairs: List<Pair<String, String>> = emptyList(),
): SwimlaneGraph = testGraph(
    nodes = nodeIds.map(::SwimlaneNode),
    graphNodeIds = nodeIds,
    edgePairs = edgePairs,
)

internal fun testGraph(
    nodes: List<SwimlaneNode>,
    graphNodeIds: List<String> = nodes.filterNot(SwimlaneNode::isGroup).map(SwimlaneNode::id),
    edgePairs: List<Pair<String, String>> = emptyList(),
): SwimlaneGraph {
    val edges = edgePairs.mapIndexed { index, (source, destination) ->
        val edge = SwimlaneEdge("e$index", source, destination, index)
        SwimlaneEdgeRef(edge.id, source, destination, ref = edge)
    }
    return SwimlaneGraph(
        nodes = graphNodeIds,
        edges = edges,
        layout = testLayout(
            nodes = nodes,
            edges = edges.map(SwimlaneEdgeRef::ref),
        ),
        nodeById = LinkedHashMap(nodes.associateBy(SwimlaneNode::id)),
    )
}
