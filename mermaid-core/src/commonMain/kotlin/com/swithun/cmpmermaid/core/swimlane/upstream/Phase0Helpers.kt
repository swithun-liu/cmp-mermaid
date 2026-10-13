package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

/**
 * Mermaid.js 12.0.0 uses String.localeCompare for deterministic graph ordering.
 *
 * ECMAScript delegates the exact locale to the host. The layout identifiers
 * are ASCII in Mermaid's generated paths, so case-folded lexical ordering
 * preserves the observable ordering used by the upstream algorithm while
 * remaining deterministic across KMP targets.
 */
internal fun compareMermaidIds(first: String, second: String): Int {
    val primary = first.lowercase().compareTo(second.lowercase())
    if (primary != 0) return primary
    if (first == second) return 0

    for (index in 0 until minOf(first.length, second.length)) {
        val firstCharacter = first[index]
        val secondCharacter = second[index]
        if (firstCharacter == secondCharacter) continue
        if (firstCharacter.lowercaseChar() == secondCharacter.lowercaseChar()) {
            val firstIsLower = firstCharacter.isLowerCase()
            val secondIsLower = secondCharacter.isLowerCase()
            if (firstIsLower != secondIsLower) return if (firstIsLower) -1 else 1
        }
        return firstCharacter.compareTo(secondCharacter)
    }
    return first.length.compareTo(second.length)
}

internal val MermaidIdComparator: Comparator<String> =
    Comparator(::compareMermaidIds)

// Mermaid.js 12.0.0: swimlanes/phase0.helpers.ts -> normalizeGraph.
internal fun normalizeGraph(graph: SwimlaneGraph): SwimlaneGraph {
    val nodeById = LinkedHashMap(graph.nodeById)
    val seen = mutableSetOf<String>()
    val edges = buildList {
        graph.edges.forEach { edge ->
            if (edge.src !in nodeById || edge.dst !in nodeById) return@forEach
            val key = "${edge.id}:${edge.src}->${edge.dst}"
            if (seen.add(key)) add(edge)
        }
    }
    return SwimlaneGraph(
        nodes = nodeById.keys.toList(),
        edges = edges,
        layout = graph.layout,
        nodeById = nodeById,
    )
}

internal fun incoming(graph: SwimlaneGraph, nodeId: String): List<SwimlaneEdgeRef> =
    graph.edges.filter { edge -> edge.dst == nodeId }

internal fun outgoing(graph: SwimlaneGraph, nodeId: String): List<SwimlaneEdgeRef> =
    graph.edges.filter { edge -> edge.src == nodeId }

internal fun buildSuccessorMap(graph: SwimlaneGraph): LinkedHashMap<String, MutableList<String>> {
    val successors = linkedMapOf<String, MutableList<String>>()
    graph.nodes.forEach { node -> successors[node] = mutableListOf() }
    graph.edges.forEach { edge -> successors.getValue(edge.src) += edge.dst }
    return successors
}

internal fun buildSortedSuccessorMap(
    graph: SwimlaneGraph,
): LinkedHashMap<String, MutableList<String>> = buildSuccessorMap(graph).also { successors ->
    successors.values.forEach { values -> values.sortWith(MermaidIdComparator) }
}

internal fun buildInDegreeMap(graph: SwimlaneGraph): LinkedHashMap<String, Int> {
    val inDegree = linkedMapOf<String, Int>()
    graph.nodes.forEach { node -> inDegree[node] = 0 }
    graph.edges.forEach { edge -> inDegree[edge.dst] = (inDegree[edge.dst] ?: 0) + 1 }
    return inDegree
}

internal fun sortedZeroInDegreeNodes(inDegree: Map<String, Int>): MutableList<String> =
    inDegree.entries
        .filter { (_, degree) -> degree == 0 }
        .map { entry -> entry.key }
        .sortedWith(MermaidIdComparator)
        .toMutableList()

internal data class PredecessorSuccessorMaps(
    val predecessors: LinkedHashMap<String, MutableList<String>>,
    val successors: LinkedHashMap<String, MutableList<String>>,
)

internal fun buildPredecessorSuccessorMaps(
    graph: SwimlaneGraph,
    includeEdge: (SwimlaneEdgeRef) -> Boolean = { true },
): PredecessorSuccessorMaps {
    val predecessors = linkedMapOf<String, MutableList<String>>()
    val successors = linkedMapOf<String, MutableList<String>>()
    graph.nodes.forEach { node ->
        predecessors[node] = mutableListOf()
        successors[node] = mutableListOf()
    }
    graph.edges.forEach { edge ->
        if (includeEdge(edge)) {
            successors.getValue(edge.src) += edge.dst
            predecessors.getValue(edge.dst) += edge.src
        }
    }
    return PredecessorSuccessorMaps(predecessors, successors)
}

internal fun buildLayersFromRanks(
    graph: SwimlaneGraph,
    order: List<String>,
    rankOf: Map<String, Int>,
    skipGroups: Boolean = false,
): MutableList<MutableList<String>> {
    var maximumRank = 0
    graph.nodes.forEach { nodeId ->
        if (skipGroups && graph.nodeById[nodeId]?.isGroup == true) return@forEach
        maximumRank = max(maximumRank, rankOf[nodeId] ?: 0)
    }
    val layers = MutableList(maximumRank + 1) { mutableListOf<String>() }
    order.forEach { nodeId ->
        if (skipGroups && graph.nodeById[nodeId]?.isGroup == true) return@forEach
        layers[max(0, rankOf[nodeId] ?: 0)] += nodeId
    }
    return layers
}

internal fun isAcyclic(graph: SwimlaneGraph): Boolean {
    val color = graph.nodes.associateWithTo(mutableMapOf()) { 0 }
    val adjacency = buildSuccessorMap(graph)

    fun visit(node: String): Boolean {
        color[node] = 1
        for (successor in adjacency[node].orEmpty()) {
            if (color[successor] == 0 && !visit(successor)) return false
            if (color[successor] == 1) return false
        }
        color[node] = 2
        return true
    }

    return graph.nodes.all { node -> color[node] != 0 || visit(node) }
}

internal fun topoSortIfAcyclic(graph: SwimlaneGraph): List<String>? {
    val inDegree = buildInDegreeMap(graph)
    val queue = sortedZeroInDegreeNodes(inDegree)
    val order = mutableListOf<String>()
    val adjacency = buildSortedSuccessorMap(graph)

    while (queue.isNotEmpty()) {
        val node = queue.removeAt(0)
        order += node
        adjacency[node].orEmpty().forEach { successor ->
            val degree = (inDegree[successor] ?: 0) - 1
            inDegree[successor] = degree
            if (degree == 0) {
                var index = 0
                while (index < queue.size && queue[index] < successor) index++
                queue.add(index, successor)
            }
        }
    }
    return order.takeIf { it.size == graph.nodes.size }
}

internal fun buildLayerIndex(layer: List<String>): Map<String, Int> =
    layer.withIndex().associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }

internal fun countInversions(input: List<Int>): Int {
    val values = input.toMutableList()
    val temporary = MutableList(values.size) { 0 }

    fun count(left: Int, right: Int): Int {
        if (right - left <= 1) return 0
        val middle = (left + right) shr 1
        var inversions = count(left, middle) + count(middle, right)
        var first = left
        var second = middle
        var output = left
        while (first < middle || second < right) {
            if (second >= right || (first < middle && values[first] <= values[second])) {
                temporary[output++] = values[first++]
            } else {
                temporary[output++] = values[second++]
                inversions += middle - first
            }
        }
        for (index in left until right) values[index] = temporary[index]
        return inversions
    }

    return count(0, values.size)
}
