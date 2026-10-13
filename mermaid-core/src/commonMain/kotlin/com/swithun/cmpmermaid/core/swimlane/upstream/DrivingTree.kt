package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.min

internal data class DrivingTreeBlock(
    val id: Int,
    val nodes: List<String>,
    val edges: List<Pair<String, String>>,
)

internal data class DrivingTree(
    val parent: MutableMap<String, String?>,
    val children: MutableMap<String, MutableList<String>>,
    val roots: List<String>,
    val componentOf: Map<String, Int>,
    val blocks: List<DrivingTreeBlock>,
    val nodeBlocks: Map<String, MutableList<Int>>,
    val adjacency: Map<String, List<String>>,
    val preorder: List<String>,
    val postorder: List<String>,
    val topologicalOrder: List<String>,
)

internal data class DrivingTreeBuildOptions(
    val rankHint: Map<String, Int>? = null,
    val laneOf: ((String) -> String?)? = null,
)

// Mermaid.js 12.0.0: swimlanes/driving-tree.ts -> buildDrivingTree.
internal fun buildDrivingTree(
    graph: SwimlaneGraph,
    options: DrivingTreeBuildOptions = DrivingTreeBuildOptions(),
): DrivingTree {
    val normalized = normalizeGraph(graph)
    val laneOf = options.laneOf ?: { null }
    val predecessors = buildPredecessorSuccessorMaps(normalized).predecessors
    predecessors.values.forEach { values -> values.sortWith(MermaidIdComparator) }

    val topologicalOrder = topoSortIfAcyclic(normalized)
        ?: normalized.nodes.sortedWith(MermaidIdComparator)
    val topologicalIndex = topologicalOrder.withIndex()
        .associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }
    val parent = linkedMapOf<String, String?>()
    val children = linkedMapOf<String, MutableList<String>>()
    normalized.nodes.forEach { node -> children[node] = mutableListOf() }

    for (node in topologicalOrder) {
        val candidates = predecessors[node].orEmpty().filter { predecessor -> predecessor in parent }
        if (candidates.isNotEmpty()) {
            val chosen = chooseParent(
                node = node,
                candidates = candidates,
                laneOf = laneOf,
                rankHint = options.rankHint,
                topologicalIndex = topologicalIndex,
            )
            parent[node] = chosen
            children.getValue(chosen) += node
        } else if (node !in parent) {
            parent[node] = null
        }
    }
    normalized.nodes.forEach { node -> if (node !in parent) parent[node] = null }

    val roots = normalized.nodes
        .filter { node -> parent[node] == null }
        .distinct()
        .sortedWith(
            compareBy<String> { node -> topologicalIndex[node] ?: 0 }
                .thenComparator(::compareMermaidIds),
        )

    val adjacencySets = buildAdjacency(normalized)
    val adjacency = adjacencySets.mapValuesTo(linkedMapOf()) { (_, nodes) ->
        nodes.sortedWith(MermaidIdComparator)
    }
    val componentOf = assignComponents(adjacency)
    val blocks = computeBlocks(adjacency)
    val nodeBlocks = normalized.nodes.associateWithTo(linkedMapOf()) { mutableListOf<Int>() }
    blocks.forEach { block ->
        block.nodes.forEach { node -> nodeBlocks.getOrPut(node) { mutableListOf() } += block.id }
    }

    val preorder = mutableListOf<String>()
    val postorder = mutableListOf<String>()
    val seen = mutableSetOf<String>()
    fun walk(node: String) {
        if (!seen.add(node)) return
        preorder += node
        children[node].orEmpty().forEach(::walk)
        postorder += node
    }
    roots.forEach(::walk)
    topologicalOrder.forEach(::walk)

    return DrivingTree(
        parent = parent,
        children = children,
        roots = roots,
        componentOf = componentOf,
        blocks = blocks,
        nodeBlocks = nodeBlocks,
        adjacency = adjacency,
        preorder = preorder,
        postorder = postorder,
        topologicalOrder = topologicalOrder,
    )
}

private fun chooseParent(
    node: String,
    candidates: List<String>,
    laneOf: (String) -> String?,
    rankHint: Map<String, Int>?,
    topologicalIndex: Map<String, Int>,
): String {
    val nodeLane = laneOf(node)
    return candidates.sortedWith { first, second ->
        val firstLane = laneOf(first)
        val secondLane = laneOf(second)
        val firstSameLane = firstLane != null && firstLane == nodeLane
        val secondSameLane = secondLane != null && secondLane == nodeLane
        val firstRank = rankHint?.get(first)
        val secondRank = rankHint?.get(second)
        when {
            firstSameLane != secondSameLane -> if (firstSameLane) -1 else 1
            firstRank != null && secondRank != null && firstRank != secondRank ->
                secondRank - firstRank
            (topologicalIndex[first] ?: 0) != (topologicalIndex[second] ?: 0) ->
                (topologicalIndex[first] ?: 0) - (topologicalIndex[second] ?: 0)
            else -> compareMermaidIds(first, second)
        }
    }.first()
}

private fun buildAdjacency(graph: SwimlaneGraph): Map<String, LinkedHashSet<String>> {
    val adjacency = graph.nodes.associateWithTo(linkedMapOf()) { linkedSetOf<String>() }
    graph.edges.forEach { edge ->
        adjacency.getValue(edge.src) += edge.dst
        adjacency.getValue(edge.dst) += edge.src
    }
    return adjacency
}

private fun assignComponents(adjacency: Map<String, List<String>>): Map<String, Int> {
    val componentOf = linkedMapOf<String, Int>()
    var componentId = 0
    for (node in adjacency.keys) {
        if (node in componentOf) continue
        val stack = mutableListOf(node)
        while (stack.isNotEmpty()) {
            val current = stack.removeAt(stack.lastIndex)
            if (current in componentOf) continue
            componentOf[current] = componentId
            adjacency[current].orEmpty().forEach { next ->
                if (next !in componentOf) stack += next
            }
        }
        componentId++
    }
    return componentOf
}

private fun computeBlocks(adjacency: Map<String, List<String>>): List<DrivingTreeBlock> {
    val discovery = mutableMapOf<String, Int>()
    val low = mutableMapOf<String, Int>()
    val edgeStack = mutableListOf<Pair<String, String>>()
    val blocks = mutableListOf<DrivingTreeBlock>()
    var time = 0

    fun visit(node: String, parent: String?) {
        time++
        discovery[node] = time
        low[node] = time
        for (next in adjacency[node].orEmpty()) {
            if (next == parent) continue
            if (next !in discovery) {
                edgeStack += node to next
                visit(next, node)
                low[node] = min(low[node] ?: time, low[next] ?: time)
                if ((low[next] ?: 0) >= (discovery[node] ?: 0)) {
                    blocks += popBlock(node, next, edgeStack, blocks.size)
                }
            } else if ((discovery[next] ?: 0) < (discovery[node] ?: 0)) {
                edgeStack += node to next
                low[node] = min(low[node] ?: time, discovery[next] ?: time)
            }
        }
    }

    adjacency.keys.forEach { node -> if (node !in discovery) visit(node, null) }
    return blocks
}

private fun popBlock(
    first: String,
    second: String,
    stack: MutableList<Pair<String, String>>,
    id: Int,
): DrivingTreeBlock {
    val edges = mutableListOf<Pair<String, String>>()
    val nodes = linkedSetOf<String>()
    while (stack.isNotEmpty()) {
        val edge = stack.removeAt(stack.lastIndex)
        edges += edge
        nodes += edge.first
        nodes += edge.second
        if (
            (edge.first == first && edge.second == second) ||
            (edge.first == second && edge.second == first)
        ) {
            break
        }
    }
    return DrivingTreeBlock(id = id, edges = edges, nodes = nodes.toList())
}
