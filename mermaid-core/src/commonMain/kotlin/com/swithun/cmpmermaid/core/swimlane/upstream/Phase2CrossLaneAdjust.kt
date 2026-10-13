package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

// Mermaid.js 12.0.0: swimlanes/phase2.crossLaneAdjust.ts -> adjustCrossLaneSources.
internal fun adjustCrossLaneSources(
    graph: SwimlaneGraph,
    rankOf: MutableMap<String, Int>,
) {
    val topLaneOf = createTopLaneResolver(graph)
    val nodesByRank = graph.nodes.sortedWith { first, second ->
        val rankComparison = (rankOf[first] ?: 0) - (rankOf[second] ?: 0)
        if (rankComparison != 0) rankComparison else compareMermaidIds(first, second)
    }
    for (node in nodesByRank) {
        val nodeLane = topLaneOf(node) ?: continue
        val outgoing = graph.edges.filter { edge -> edge.src == node }
        if (outgoing.isEmpty()) continue
        var hasSameLaneSuccessor = false
        var crossLaneCount = 0
        outgoing.forEach { edge ->
            val destinationLane = topLaneOf(edge.dst)
            if (destinationLane == null || destinationLane == nodeLane) {
                hasSameLaneSuccessor = true
            } else {
                crossLaneCount++
            }
        }
        if (crossLaneCount == 0 || hasSameLaneSuccessor) continue

        var crossLaneIncoming = 0
        var hasSameLanePredecessor = false
        graph.edges.forEach { edge ->
            if (edge.dst != node) return@forEach
            val sourceLane = topLaneOf(edge.src) ?: return@forEach
            if (sourceLane == nodeLane) {
                hasSameLanePredecessor = true
            } else {
                crossLaneIncoming++
            }
        }
        if (crossLaneIncoming > 0 || !hasSameLanePredecessor) continue

        val current = rankOf[node] ?: 0
        val target = current + crossLaneCount
        var lowerBound = 0
        graph.edges.forEach { edge ->
            if (edge.dst == node) lowerBound = max(lowerBound, (rankOf[edge.src] ?: 0) + 1)
        }
        val newRank = max(max(current, lowerBound), target)
        if (newRank != current) rankOf[node] = newRank
    }
}
