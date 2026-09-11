package io.github.cmpmermaid.core.flowchart

import kotlin.math.max

/**
 * Mirrors Mermaid's Dagre layering stages at the data-contract level:
 * cycle removal, rank assignment, rank normalization, then barycentric order.
 */
internal object FlowLayering {
    fun build(document: FlowchartDocument): Result {
        val declarationOrder = document.nodes.keys
            .withIndex()
            .associate { indexed -> indexed.value to indexed.index }
        val edges = makeAcyclic(document, declarationOrder)
        val topologicalOrder = topologicalOrder(document.nodes.keys, edges, declarationOrder)
        val earliest = assignEarliestRanks(document.nodes.keys, edges, topologicalOrder)
        val latest = assignLatestRanks(document.nodes.keys, edges, topologicalOrder, earliest)
        val ranks = balanceRanks(earliest, latest, edges, topologicalOrder)
        val layers = orderLayers(document, ranks, declarationOrder)
        return Result(ranks = ranks, layers = layers)
    }

    internal data class Result(
        val ranks: Map<String, Int>,
        val layers: List<List<String>>,
    )

    private data class RankEdge(
        val from: String,
        val to: String,
        val minimumLength: Int,
    )

    private fun makeAcyclic(
        document: FlowchartDocument,
        declarationOrder: Map<String, Int>,
    ): List<RankEdge> {
        val edgesBySource = document.edges
            .filter { edge ->
                edge.from != edge.to &&
                    edge.from in document.nodes &&
                    edge.to in document.nodes
            }
            .groupBy(FlowEdge::from)
        val state = mutableMapOf<String, Int>()
        val result = mutableListOf<RankEdge>()

        fun visit(nodeId: String) {
            state[nodeId] = VISITING
            edgesBySource[nodeId].orEmpty().forEach { edge ->
                val minimumLength = edge.minimumLength.coerceAtLeast(1) * RANK_SCALE
                if (state[edge.to] == VISITING) {
                    result += RankEdge(edge.to, edge.from, minimumLength)
                } else {
                    result += RankEdge(edge.from, edge.to, minimumLength)
                    if (state[edge.to] == null) {
                        visit(edge.to)
                    }
                }
            }
            state[nodeId] = VISITED
        }

        document.nodes.keys
            .sortedBy { declarationOrder[it] ?: Int.MAX_VALUE }
            .forEach { nodeId ->
                if (state[nodeId] == null) {
                    visit(nodeId)
                }
            }
        return result
    }

    private fun topologicalOrder(
        nodeIds: Set<String>,
        edges: List<RankEdge>,
        declarationOrder: Map<String, Int>,
    ): List<String> {
        val incomingCount = nodeIds.associateWith { 0 }.toMutableMap()
        val outgoing = edges.groupBy(RankEdge::from)
        edges.forEach { edge ->
            incomingCount[edge.to] = (incomingCount[edge.to] ?: 0) + 1
        }
        val queue = nodeIds
            .filter { incomingCount[it] == 0 }
            .sortedBy { declarationOrder[it] ?: Int.MAX_VALUE }
            .toMutableList()
        val result = mutableListOf<String>()

        while (queue.isNotEmpty()) {
            val nodeId = queue.removeAt(0)
            result += nodeId
            outgoing[nodeId].orEmpty().forEach { edge ->
                incomingCount[edge.to] = (incomingCount[edge.to] ?: 1) - 1
                if (incomingCount[edge.to] == 0) {
                    queue += edge.to
                    queue.sortBy { declarationOrder[it] ?: Int.MAX_VALUE }
                }
            }
        }

        nodeIds
            .filterNot(result::contains)
            .sortedBy { declarationOrder[it] ?: Int.MAX_VALUE }
            .forEach(result::add)
        return result
    }

    private fun assignEarliestRanks(
        nodeIds: Set<String>,
        edges: List<RankEdge>,
        topologicalOrder: List<String>,
    ): MutableMap<String, Int> {
        val ranks = nodeIds.associateWith { 0 }.toMutableMap()
        val outgoing = edges.groupBy(RankEdge::from)
        topologicalOrder.forEach { nodeId ->
            outgoing[nodeId].orEmpty().forEach { edge ->
                ranks[edge.to] = max(
                    ranks[edge.to] ?: 0,
                    (ranks[nodeId] ?: 0) + edge.minimumLength,
                )
            }
        }
        return ranks
    }

    private fun assignLatestRanks(
        nodeIds: Set<String>,
        edges: List<RankEdge>,
        topologicalOrder: List<String>,
        earliest: Map<String, Int>,
    ): MutableMap<String, Int> {
        val componentByNode = weakComponents(nodeIds, edges)
        val componentMaximum = earliest.entries
            .groupBy { componentByNode.getValue(it.key) }
            .mapValues { (_, entries) -> entries.maxOf(Map.Entry<String, Int>::value) }
        val latest = nodeIds.associateWith { nodeId ->
            componentMaximum.getValue(componentByNode.getValue(nodeId))
        }.toMutableMap()
        val outgoing = edges.groupBy(RankEdge::from)

        topologicalOrder.asReversed().forEach { nodeId ->
            outgoing[nodeId].orEmpty().forEach { edge ->
                latest[nodeId] = minOf(
                    latest[nodeId] ?: Int.MAX_VALUE,
                    (latest[edge.to] ?: 0) - edge.minimumLength,
                )
            }
            latest[nodeId] = max(latest[nodeId] ?: 0, earliest[nodeId] ?: 0)
        }
        return latest
    }

    private fun weakComponents(
        nodeIds: Set<String>,
        edges: List<RankEdge>,
    ): Map<String, Int> {
        val neighbors = nodeIds.associateWith { mutableSetOf<String>() }.toMutableMap()
        edges.forEach { edge ->
            neighbors.getValue(edge.from) += edge.to
            neighbors.getValue(edge.to) += edge.from
        }
        val result = mutableMapOf<String, Int>()
        var component = 0
        nodeIds.forEach { start ->
            if (start in result) {
                return@forEach
            }
            val queue = mutableListOf(start)
            result[start] = component
            while (queue.isNotEmpty()) {
                val nodeId = queue.removeAt(0)
                neighbors[nodeId].orEmpty().forEach { neighbor ->
                    if (neighbor !in result) {
                        result[neighbor] = component
                        queue += neighbor
                    }
                }
            }
            component += 1
        }
        return result
    }

    private fun balanceRanks(
        earliest: Map<String, Int>,
        latest: Map<String, Int>,
        edges: List<RankEdge>,
        topologicalOrder: List<String>,
    ): Map<String, Int> {
        val ranks = earliest.mapValues { (nodeId, earliestRank) ->
            earliestRank + ((latest[nodeId] ?: earliestRank) - earliestRank) / 2
        }.toMutableMap()
        val outgoing = edges.groupBy(RankEdge::from)
        topologicalOrder.forEach { nodeId ->
            outgoing[nodeId].orEmpty().forEach { edge ->
                ranks[edge.to] = max(
                    ranks[edge.to] ?: 0,
                    (ranks[nodeId] ?: 0) + edge.minimumLength,
                )
            }
        }
        return ranks
    }

    private fun orderLayers(
        document: FlowchartDocument,
        ranks: Map<String, Int>,
        declarationOrder: Map<String, Int>,
    ): List<List<String>> {
        val maximumRank = ranks.values.maxOrNull() ?: 0
        val degree = document.nodes.keys.associateWith { nodeId ->
            document.edges.count { edge -> edge.from == nodeId || edge.to == nodeId }
        }
        val layers = MutableList(maximumRank + 1) { mutableListOf<String>() }
        ranks.forEach { (nodeId, rank) ->
            layers[rank] += nodeId
        }
        layers.forEach { layer ->
            layer.sortWith(
                compareBy<String> { degree[it] ?: 0 }
                    .thenBy { declarationOrder[it] ?: Int.MAX_VALUE },
            )
        }

        repeat(ORDER_SWEEPS) {
            for (rank in 1..maximumRank) {
                sortByNeighbors(
                    layer = layers[rank],
                    neighbors = { nodeId ->
                        document.edges
                            .asSequence()
                            .filter { edge -> edge.to == nodeId }
                            .map(FlowEdge::from)
                            .toList()
                    },
                    layers = layers,
                    ranks = ranks,
                )
            }
            for (rank in maximumRank - 1 downTo 0) {
                sortByNeighbors(
                    layer = layers[rank],
                    neighbors = { nodeId ->
                        document.edges
                            .asSequence()
                            .filter { edge -> edge.from == nodeId }
                            .map(FlowEdge::to)
                            .toList()
                    },
                    layers = layers,
                    ranks = ranks,
                )
            }
        }
        return layers
    }

    private fun sortByNeighbors(
        layer: MutableList<String>,
        neighbors: (String) -> List<String>,
        layers: List<List<String>>,
        ranks: Map<String, Int>,
    ) {
        if (layer.size < 2) {
            return
        }
        val stableOrder = layer.withIndex().associate { indexed -> indexed.value to indexed.index }
        layer.sortWith(
            compareBy<String> { nodeId ->
                val positions = neighbors(nodeId).mapNotNull { neighbor ->
                    val neighborRank = ranks[neighbor] ?: return@mapNotNull null
                    val neighborLayer = layers.getOrNull(neighborRank).orEmpty()
                    val position = neighborLayer.indexOf(neighbor)
                    position.takeIf { it >= 0 }?.toDouble()
                }
                if (positions.isEmpty()) {
                    Double.MAX_VALUE
                } else {
                    positions.average()
                }
            }.thenBy { stableOrder[it] ?: Int.MAX_VALUE },
        )
    }

    private const val RANK_SCALE = 2
    private const val ORDER_SWEEPS = 4
    private const val VISITING = 1
    private const val VISITED = 2
}
