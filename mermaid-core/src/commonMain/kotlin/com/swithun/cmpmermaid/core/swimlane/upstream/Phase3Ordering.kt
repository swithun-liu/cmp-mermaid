package com.swithun.cmpmermaid.core.swimlane.upstream

private enum class SweepDirection {
    Down,
    Up,
}

private fun median(values: List<Int>): Double {
    if (values.isEmpty()) return Double.POSITIVE_INFINITY
    val sorted = values.sorted()
    return if (sorted.size % 2 == 1) {
        sorted[(sorted.size - 1) / 2].toDouble()
    } else {
        0.5 * (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2])
    }
}

private fun barycenter(values: List<Int>): Double =
    if (values.isEmpty()) Double.POSITIVE_INFINITY else values.sum().toDouble() / values.size

private fun neighborPositionsFor(
    targetNodes: List<String>,
    fixedIndex: Map<String, Int>,
    edges: List<SwimlaneEdgeRef>,
    direction: SweepDirection,
): Map<String, MutableList<Int>> {
    val positions = targetNodes.associateWithTo(linkedMapOf()) { mutableListOf<Int>() }
    edges.forEach { edge ->
        if (direction == SweepDirection.Down) {
            if (edge.src in fixedIndex && edge.dst in positions) {
                positions.getValue(edge.dst) += fixedIndex.getValue(edge.src)
            }
        } else if (edge.dst in fixedIndex && edge.src in positions) {
            positions.getValue(edge.src) += fixedIndex.getValue(edge.dst)
        }
    }
    return positions
}

private fun countAdjacentCrossings(
    upper: List<String>,
    lower: List<String>,
    edges: List<SwimlaneEdgeRef>,
): Int {
    val upperSet = upper.toSet()
    val lowerSet = lower.toSet()
    val upperIndex = buildLayerIndex(upper)
    val lowerIndex = buildLayerIndex(lower)
    val pairs = mutableListOf<Pair<Int, Int>>()
    edges.forEach { edge ->
        if (edge.src in upperSet && edge.dst in lowerSet) {
            pairs += upperIndex.getValue(edge.src) to lowerIndex.getValue(edge.dst)
        }
    }
    pairs.sortWith(compareBy<Pair<Int, Int>> { pair -> pair.first }.thenBy { pair -> pair.second })
    return countInversions(pairs.map(Pair<Int, Int>::second))
}

internal fun totalCrossings(
    layers: List<List<String>>,
    edges: List<SwimlaneEdgeRef>,
): Int = (0 until (layers.size - 1).coerceAtLeast(0)).sumOf { index ->
    countAdjacentCrossings(layers[index], layers[index + 1], edges)
}

private fun sortByHeuristic(
    nodes: List<String>,
    neighborPositions: Map<String, List<Int>>,
    currentLayerIndex: Map<String, Int>,
): List<String> = nodes.sortedWith { first, second ->
    val firstScore = median(neighborPositions[first].orEmpty())
    val secondScore = median(neighborPositions[second].orEmpty())
    when {
        firstScore == secondScore -> {
            val firstIndex = currentLayerIndex[first] ?: 0
            val secondIndex = currentLayerIndex[second] ?: 0
            if (firstIndex != secondIndex) {
                firstIndex - secondIndex
            } else {
                compareMermaidIds(first, second)
            }
        }
        !firstScore.isFinite() -> 1
        !secondScore.isFinite() -> -1
        else -> firstScore.compareTo(secondScore)
    }
}

private fun reorderLayer(
    fixedLayer: List<String>,
    targetLayer: List<String>,
    edges: List<SwimlaneEdgeRef>,
    direction: SweepDirection,
    topLaneOf: ((String) -> String?)? = null,
    laneOrder: List<String>? = null,
): MutableList<String> {
    val fixedIndex = buildLayerIndex(fixedLayer)
    val currentIndex = buildLayerIndex(targetLayer)
    val neighborPositions = neighborPositionsFor(targetLayer, fixedIndex, edges, direction)
    if (topLaneOf == null || laneOrder.isNullOrEmpty()) {
        return sortByHeuristic(targetLayer, neighborPositions, currentIndex).toMutableList()
    }

    val byLane = linkedMapOf<String?, MutableList<String>>()
    targetLayer.forEach { id -> byLane.getOrPut(topLaneOf(id)) { mutableListOf() } += id }
    val result = mutableListOf<String>()
    laneOrder.forEach { lane ->
        val nodesInLane = byLane[lane].orEmpty()
        if (nodesInLane.isNotEmpty()) {
            result += sortByHeuristic(nodesInLane, neighborPositions, currentIndex)
        }
    }

    val nullNodes = byLane[null].orEmpty()
    if (nullNodes.isNotEmpty()) {
        val sorted = sortByHeuristic(nullNodes, neighborPositions, currentIndex)
        for (node in sorted) {
            val nodeBarycenter = barycenter(neighborPositions[node].orEmpty())
            var bestIndex = result.size
            if (nodeBarycenter.isFinite()) {
                for ((index, placed) in result.withIndex()) {
                    val placedBarycenter = barycenter(neighborPositions[placed].orEmpty())
                    if (nodeBarycenter < placedBarycenter) {
                        bestIndex = index
                        break
                    }
                }
            }
            result.add(bestIndex, node)
        }
    }
    return result
}

private fun transposeImprove(
    upper: List<String>,
    current: List<String>,
    edges: List<SwimlaneEdgeRef>,
    next: List<String>? = null,
    topLaneOf: ((String) -> String?)? = null,
): MutableList<String> {
    val best = current.toMutableList()
    val upperSet = upper.toSet()
    val layerSet = current.toSet()
    val nextSet = next?.toSet()
    val incomingEdges = edges.filter { edge -> edge.src in upperSet && edge.dst in layerSet }
    val outgoingEdges = nextSet?.let { set ->
        edges.filter { edge -> edge.src in layerSet && edge.dst in set }
    }

    fun crossingScore(order: List<String>): Int {
        var score = countAdjacentCrossings(upper, order, incomingEdges)
        next?.let { nextLayer ->
            outgoingEdges?.let { outgoing ->
                score += countAdjacentCrossings(order, nextLayer, outgoing)
            }
        }
        return score
    }

    val laneOf = topLaneOf?.let { resolver ->
        current.associateWith(resolver)
    }
    var improved = true
    var bestScore = crossingScore(best)
    while (improved) {
        improved = false
        for (index in 0 until (best.size - 1).coerceAtLeast(0)) {
            if (laneOf != null && laneOf[best[index]] != laneOf[best[index + 1]]) continue
            val previousScore = bestScore
            val value = best[index]
            best[index] = best[index + 1]
            best[index + 1] = value
            val nextScore = crossingScore(best)
            if (nextScore < previousScore) {
                bestScore = nextScore
                improved = true
            } else {
                val restore = best[index]
                best[index] = best[index + 1]
                best[index + 1] = restore
            }
        }
    }
    return best
}

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase3.ordering.ts -> orderLayers.
 */
internal fun orderLayers(
    layering: SwimlaneLayering,
    graphWithDummies: SwimlaneGraph,
    laneOrderOverride: List<String>? = null,
): SwimlaneOrderedLayers {
    val layers = layering.layers.mapTo(mutableListOf()) { layer -> layer.toMutableList() }
    val edges = graphWithDummies.edges
    val topLaneOf = createTopLaneResolver(graphWithDummies)
    val laneOrder = resolveTopLaneOrder(graphWithDummies, laneOrderOverride)

    repeat(3) {
        for (index in 1 until layers.size) {
            layers[index] = reorderLayer(
                fixedLayer = layers[index - 1],
                targetLayer = layers[index],
                edges = edges,
                direction = SweepDirection.Down,
                topLaneOf = topLaneOf,
                laneOrder = laneOrder,
            )
            layers[index] = transposeImprove(
                upper = layers[index - 1],
                current = layers[index],
                edges = edges,
                next = layers.getOrNull(index + 1),
                topLaneOf = topLaneOf,
            )
        }
        for (index in layers.size - 2 downTo 0) {
            layers[index] = reorderLayer(
                fixedLayer = layers[index + 1],
                targetLayer = layers[index],
                edges = edges,
                direction = SweepDirection.Up,
                topLaneOf = topLaneOf,
                laneOrder = laneOrder,
            )
            layers[index] = transposeImprove(
                upper = layers[index + 1],
                current = layers[index],
                edges = edges,
                next = layers.getOrNull(index - 1),
                topLaneOf = topLaneOf,
            )
        }
    }
    return SwimlaneOrderedLayers(layers)
}
