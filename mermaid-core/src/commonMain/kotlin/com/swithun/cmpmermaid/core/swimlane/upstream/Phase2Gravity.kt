package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.gravity.ts -> assignLayers_Gravity.
 */
internal fun assignLayersGravity(
    acyclicGraph: SwimlaneGraph,
    options: SwimlaneLayeringOptions = SwimlaneLayeringOptions(),
): SwimlaneLayering {
    val graph = normalizeGraph(acyclicGraph)
    val base = assignLayersLongestPath(
        graph,
        SwimlaneLayeringOptions(
            compactSingleInput = options.compactSingleInput,
            ignoreCrossLaneEdges = options.ignoreCrossLaneEdges,
            optimizeRanksByCrossings = options.optimizeRanksByCrossings,
        ),
    )
    val rankOf = LinkedHashMap(base.rankOf)
    val topLaneOf = createTopLaneResolver(graph)
    val maps = buildPredecessorSuccessorMaps(graph) { edge ->
        if (options.ignoreCrossLaneEdges == true) {
            val sourceLane = topLaneOf(edge.src)
            val destinationLane = topLaneOf(edge.dst)
            sourceLane == null || destinationLane == null || sourceLane == destinationLane
        } else {
            true
        }
    }
    val predecessors = maps.predecessors
    val successors = maps.successors
    val order = topoSortIfAcyclic(graph) ?: graph.nodes.toList()
    val reverseOrder = order.asReversed()

    fun clampFeasible(node: String, desired: Int): Int {
        var lowerBound = 0
        predecessors[node].orEmpty().forEach { predecessor ->
            lowerBound = max(lowerBound, (rankOf[predecessor] ?: 0) + 1)
        }
        val successorList = successors[node].orEmpty()
        var upperBound = if (successorList.isEmpty()) {
            Int.MAX_VALUE
        } else {
            successorList.minOf { successor -> (rankOf[successor] ?: 0) - 1 }
        }
        if (upperBound == Int.MAX_VALUE) upperBound = max(lowerBound, desired)
        return min(max(desired, lowerBound), upperBound)
    }

    fun relaxOrder(nodeOrder: List<String>): Boolean {
        var changed = false
        for (node in nodeOrder) {
            val predecessorList = predecessors[node].orEmpty()
            val successorList = successors[node].orEmpty()
            if (predecessorList.isEmpty() && successorList.isEmpty()) continue
            val predecessorAverage = if (predecessorList.isNotEmpty()) {
                predecessorList.sumOf { predecessor -> (rankOf[predecessor] ?: 0) + 1 }
                    .toDouble() / predecessorList.size
            } else {
                (rankOf[node] ?: 0).toDouble()
            }
            val successorAverage = if (successorList.isNotEmpty()) {
                successorList.sumOf { successor -> (rankOf[successor] ?: 0) - 1 }
                    .toDouble() / successorList.size
            } else {
                (rankOf[node] ?: 0).toDouble()
            }
            val desired = floor((predecessorAverage + successorAverage) / 2.0 + 0.5).toInt()
            val clamped = clampFeasible(node, desired)
            if (clamped != rankOf[node]) {
                rankOf[node] = clamped
                changed = true
            }
        }
        return changed
    }

    for (iteration in 0 until SwimlaneConfig.Layering.GravityIterations) {
        val forwardChanged = relaxOrder(order)
        val backwardChanged = relaxOrder(reverseOrder)
        if (!forwardChanged && !backwardChanged) break
    }

    for (node in order) {
        var lowerBound = 0
        predecessors[node].orEmpty().forEach { predecessor ->
            lowerBound = max(lowerBound, (rankOf[predecessor] ?: 0) + 1)
        }
        if ((rankOf[node] ?: 0) < lowerBound) rankOf[node] = lowerBound
    }
    for (node in reverseOrder) {
        val successorList = successors[node].orEmpty()
        if (successorList.isNotEmpty()) {
            val upperBound = successorList.minOf { successor -> (rankOf[successor] ?: 0) - 1 }
            if ((rankOf[node] ?: 0) > upperBound) rankOf[node] = upperBound
        }
    }

    return SwimlaneLayering(
        layers = buildLayersFromRanks(graph, order, rankOf),
        rankOf = rankOf,
    )
}
