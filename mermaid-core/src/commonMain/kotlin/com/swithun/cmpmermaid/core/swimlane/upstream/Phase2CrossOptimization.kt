package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

private fun countCrossingsBetweenAdjacentRanks(
    upper: List<String>,
    lower: List<String>,
    edges: List<SwimlaneEdgeRef>,
): Int {
    val upperSet = upper.toSet()
    val lowerSet = lower.toSet()
    val lowerIndex = buildLayerIndex(lower)
    val values = buildList {
        edges.forEach { edge ->
            if (edge.src in upperSet && edge.dst in lowerSet) {
                lowerIndex[edge.dst]?.let(::add)
            }
        }
    }
    return countInversions(values)
}

private fun totalRankCrossings(
    layers: List<List<String>>,
    edges: List<SwimlaneEdgeRef>,
    rankOf: Map<String, Int>,
): Int {
    val expanded = mutableListOf<SwimlaneEdgeRef>()
    edges.forEach { edge ->
        val sourceRank = rankOf[edge.src] ?: return@forEach
        val destinationRank = rankOf[edge.dst] ?: return@forEach
        if (sourceRank == destinationRank) return@forEach
        val upper: String
        val lower: String
        val upperRank: Int
        val lowerRank: Int
        if (sourceRank > destinationRank) {
            upper = edge.dst
            lower = edge.src
            upperRank = destinationRank
            lowerRank = sourceRank
        } else {
            upper = edge.src
            lower = edge.dst
            upperRank = sourceRank
            lowerRank = destinationRank
        }
        for (layer in upperRank until lowerRank) {
            expanded += SwimlaneEdgeRef(
                id = "${edge.id}@$layer",
                src = upper,
                dst = lower,
                ref = edge.ref,
            )
        }
    }
    var sum = 0
    for (index in 0 until max(0, layers.size - 1)) {
        sum += countCrossingsBetweenAdjacentRanks(layers[index], layers[index + 1], expanded)
    }
    return sum
}

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.crossOptimization.ts -> optimizeRanksByCrossings.
 */
internal fun optimizeRanksByCrossings(
    graph: SwimlaneGraph,
    initialRank: Map<String, Int>,
): MutableMap<String, Int> {
    val rankOf = initialRank.toMutableMap()
    val predecessors = buildPredecessorSuccessorMaps(graph).predecessors
    val laneOf = createTopLaneResolver(graph)
    var best = totalRankCrossings(
        buildMultitreeLayerOrder(graph, rankOf, laneOf),
        graph.edges,
        rankOf,
    )

    repeat(SwimlaneConfig.Layering.MaximumCrossingOptimizationPasses) {
        var changed = false
        val nodesByRank = graph.nodes.sortedByDescending { node -> rankOf[node] ?: 0 }
        for (node in nodesByRank) {
            val rank = rankOf[node] ?: 0
            if (rank == 0) continue
            var lowerBound = 0
            predecessors[node].orEmpty().forEach { predecessor ->
                lowerBound = max(lowerBound, (rankOf[predecessor] ?: 0) + 1)
            }
            if (lowerBound >= rank) continue

            rankOf[node] = lowerBound
            val score = totalRankCrossings(
                buildMultitreeLayerOrder(graph, rankOf, laneOf),
                graph.edges,
                rankOf,
            )
            if (score < best) {
                best = score
                changed = true
            } else {
                rankOf[node] = rank
            }
        }
        if (!changed) return rankOf
    }
    return rankOf
}
