package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max
import kotlin.math.min

// Mermaid.js 12.0.0: swimlanes/phase2.multitree.core.ts.
internal fun annotateMinimumLayers(
    nodes: List<String>,
    children: Map<String, List<String>>,
    rankOf: Map<String, Int>,
): Map<String, Int> {
    val minimumLayer = mutableMapOf<String, Int>()

    fun annotate(node: String) {
        var minimum = rankOf[node] ?: 0
        val childList = children[node].orEmpty().sortedWith(compareByRankThenId(rankOf))
        childList.forEach { child ->
            annotate(child)
            minimumLayer[child]?.let { childMinimum -> minimum = min(minimum, childMinimum) }
        }
        minimumLayer[node] = minimum
    }

    nodes.forEach(::annotate)
    return minimumLayer
}

internal fun compareByRankThenId(rankOf: Map<String, Int>): Comparator<String> =
    Comparator { first, second ->
        val firstRank = rankOf[first] ?: 0
        val secondRank = rankOf[second] ?: 0
        if (firstRank == secondRank) {
            compareMermaidIds(first, second)
        } else {
            firstRank - secondRank
        }
    }

internal fun emitNodesInTreeOrder(
    roots: List<String>,
    allNodes: List<String>,
    rankOf: Map<String, Int>,
    orderChildren: (String) -> List<String>,
): MutableList<MutableList<String>> {
    var maximumRank = 0
    allNodes.forEach { node -> maximumRank = max(maximumRank, rankOf[node] ?: 0) }
    val layers = MutableList(maximumRank + 1) { mutableListOf<String>() }
    val emitted = mutableSetOf<String>()

    fun emit(node: String) {
        if (!emitted.add(node)) return
        val layer = rankOf[node] ?: 0
        while (layers.size <= layer) layers += mutableListOf<String>()
        layers[layer] += node
        orderChildren(node).forEach(::emit)
    }

    roots.forEach(::emit)
    allNodes.forEach { node ->
        if (emitted.add(node)) {
            val layer = rankOf[node] ?: 0
            while (layers.size <= layer) layers += mutableListOf<String>()
            layers[layer] += node
        }
    }
    return layers
}

internal fun deduplicateLayers(
    layers: List<List<String>>,
): MutableList<MutableList<String>> = layers.mapTo(mutableListOf()) { layer ->
    layer.distinct().toMutableList()
}
