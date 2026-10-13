package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.multitree.order.ts -> buildMultitreeLayerOrder.
 */
internal fun buildMultitreeLayerOrder(
    graph: SwimlaneGraph,
    rankOf: Map<String, Int>,
    laneOf: (String) -> String?,
): MutableList<MutableList<String>> {
    val tree = buildDrivingTree(
        graph,
        DrivingTreeBuildOptions(
            rankHint = rankOf,
            laneOf = laneOf,
        ),
    )
    val children = tree.children
    graph.nodes.forEach { node -> children.getOrPut(node) { mutableListOf() } }
    val crossCounts = computeSubtreeCrossCounts(graph, rankOf, tree)
    val rootsSorted = tree.roots.sortedWith(compareByRankThenId(rankOf))
    val minimumLayer = annotateMinimumLayers(rootsSorted, children, rankOf)

    fun orderChildren(node: String): List<String> {
        val raw = children[node].orEmpty()
        if (raw.isEmpty()) return emptyList()
        val layer = rankOf[node] ?: 0
        val future = mutableListOf<Pair<String, Int>>()
        val present = mutableListOf<String>()
        val crossMap = crossCounts[node]

        raw.forEach { child ->
            val childMinimum = minimumLayer[child] ?: layer
            if (childMinimum > layer) {
                future += child to childMinimum
            } else {
                present += child
            }
        }
        future.sortWith { first, second ->
            val layerComparison = first.second - second.second
            if (layerComparison != 0) {
                layerComparison
            } else {
                compareMermaidIds(first.first, second.first)
            }
        }
        present.sortWith { first, second ->
            val firstCrossings = crossMap?.get(first) ?: 0
            val secondCrossings = crossMap?.get(second) ?: 0
            when {
                firstCrossings != secondCrossings -> firstCrossings - secondCrossings
                (minimumLayer[first] ?: layer) != (minimumLayer[second] ?: layer) ->
                    (minimumLayer[first] ?: layer) - (minimumLayer[second] ?: layer)
                else -> compareMermaidIds(first, second)
            }
        }
        return future.map(Pair<String, Int>::first) + present
    }

    return deduplicateLayers(
        emitNodesInTreeOrder(
            roots = rootsSorted,
            allNodes = graph.nodes,
            rankOf = rankOf,
            orderChildren = ::orderChildren,
        ),
    )
}
