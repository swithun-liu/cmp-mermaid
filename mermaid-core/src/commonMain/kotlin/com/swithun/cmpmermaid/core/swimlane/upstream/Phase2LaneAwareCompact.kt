package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

private fun topoSortByGenerationIfAcyclic(graph: SwimlaneGraph): List<String>? {
    val inDegree = buildInDegreeMap(graph)
    val adjacency = buildSortedSuccessorMap(graph)
    var frontier = sortedZeroInDegreeNodes(inDegree)
    val order = mutableListOf<String>()

    while (frontier.isNotEmpty()) {
        val nextFrontier = mutableListOf<String>()
        for (node in frontier) {
            order += node
            adjacency[node].orEmpty().forEach { successor ->
                val degree = (inDegree[successor] ?: 0) - 1
                inDegree[successor] = degree
                if (degree == 0) nextFrontier += successor
            }
        }
        frontier = nextFrontier.sortedWith(MermaidIdComparator).toMutableList()
    }
    return order.takeIf { it.size == graph.nodes.size }
}

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.laneAwareCompact.ts -> assignLayers_LaneAwareCompact.
 */
internal fun assignLayersLaneAwareCompact(
    acyclicGraph: SwimlaneGraph,
    options: SwimlaneLayeringOptions = SwimlaneLayeringOptions(),
): SwimlaneLayering {
    val graph = normalizeGraph(acyclicGraph)
    val order = if (options.direction == SwimlaneDirection.LR) {
        topoSortByGenerationIfAcyclic(graph) ?: graph.nodes.sortedWith(MermaidIdComparator)
    } else {
        topoSortIfAcyclic(graph) ?: graph.nodes.sortedWith(MermaidIdComparator)
    }
    val topLaneOf = createTopLaneResolver(graph)
    val laneOf: (String) -> String = { id -> topLaneOf(id) ?: id }
    val rankOf = linkedMapOf<String, Int>()
    val nextFree = mutableMapOf<String, Int>()

    fun edgeWeight(source: String, destination: String): Int =
        if (
            (options.ignoreCrossLaneEdges ?: true) &&
            laneOf(source) != laneOf(destination)
        ) {
            0
        } else {
            1
        }

    for (nodeId in order) {
        if (graph.nodeById[nodeId]?.isGroup == true) continue
        var base = 0
        incoming(graph, nodeId).forEach { edge ->
            base = max(base, (rankOf[edge.src] ?: 0) + edgeWeight(edge.src, nodeId))
        }
        val lane = laneOf(nodeId)
        val layer = max(base, nextFree[lane] ?: 0)
        rankOf[nodeId] = layer
        nextFree[lane] = layer + 1
    }

    return SwimlaneLayering(
        layers = buildLayersFromRanks(
            graph = graph,
            order = order,
            rankOf = rankOf,
            skipGroups = true,
        ),
        rankOf = rankOf,
    )
}
