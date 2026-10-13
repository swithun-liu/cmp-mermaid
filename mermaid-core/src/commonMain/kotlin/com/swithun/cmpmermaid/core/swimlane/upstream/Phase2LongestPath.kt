package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.longestPath.ts -> assignLayers_LongestPath.
 */
internal fun assignLayersLongestPath(
    acyclicGraph: SwimlaneGraph,
    options: SwimlaneLayeringOptions = SwimlaneLayeringOptions(),
): SwimlaneLayering {
    val graph = normalizeGraph(acyclicGraph)
    val order = topoSortIfAcyclic(graph) ?: graph.nodes.sorted()
    val compact = options.compactSingleInput ?: false
    val topLaneOf = createTopLaneResolver(graph)
    var rankOf = linkedMapOf<String, Int>()

    for (node in order) {
        val allIncoming = incoming(graph, node)
        val filteredIncoming = if (options.ignoreCrossLaneEdges == true) {
            allIncoming.filter { edge ->
                val sourceLane = topLaneOf(edge.src)
                val destinationLane = topLaneOf(node)
                sourceLane == null || destinationLane == null || sourceLane == destinationLane
            }
        } else {
            allIncoming
        }

        rankOf[node] = when {
            filteredIncoming.isEmpty() -> 0
            compact && filteredIncoming.size == 1 -> {
                val predecessor = filteredIncoming[0].src
                if (topLaneOf(predecessor) != topLaneOf(node)) {
                    rankOf[predecessor] ?: 0
                } else {
                    (rankOf[predecessor] ?: 0) + 1
                }
            }
            else -> {
                var maximum = Int.MIN_VALUE
                filteredIncoming.forEach { edge ->
                    maximum = max(maximum, (rankOf[edge.src] ?: 0) + 1)
                }
                if (maximum == Int.MIN_VALUE) 0 else maximum
            }
        }
    }

    if (options.optimizeRanksByCrossings == true) {
        rankOf = LinkedHashMap(optimizeRanksByCrossings(graph, rankOf))
    }
    if (options.ignoreCrossLaneEdges == true) {
        adjustCrossLaneSources(graph, rankOf)
    }

    return SwimlaneLayering(
        layers = buildMultitreeLayerOrder(graph, rankOf, topLaneOf),
        rankOf = rankOf,
    )
}
