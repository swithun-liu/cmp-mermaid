package com.swithun.cmpmermaid.core.swimlane.upstream

internal data class CycleRemovalResult(
    val acyclic: SwimlaneGraph,
    val reversed: List<SwimlaneEdgeRef>,
)

// Mermaid.js 12.0.0: swimlanes/phase1.cycles.ts -> removeCycles_DFS.
internal fun removeCyclesDfs(graph: SwimlaneGraph): CycleRemovalResult {
    val normalized = normalizeGraph(graph)
    val adjacency = linkedMapOf<String, MutableList<SwimlaneEdgeRef>>()
    normalized.nodes.forEach { node -> adjacency[node] = mutableListOf() }
    normalized.edges.forEach { edge -> adjacency.getValue(edge.src) += edge }
    adjacency.values.forEach { edges ->
        edges.sortWith { first, second ->
            if (first.dst == second.dst) {
                compareMermaidIds(first.id, second.id)
            } else {
                compareMermaidIds(first.dst, second.dst)
            }
        }
    }

    val color = normalized.nodes.associateWithTo(mutableMapOf()) { 0 }
    val reversed = mutableListOf<SwimlaneEdgeRef>()
    fun visit(node: String) {
        color[node] = 1
        adjacency[node].orEmpty().forEach { edge ->
            when (color[edge.dst]) {
                0 -> visit(edge.dst)
                1 -> reversed += edge
            }
        }
        color[node] = 2
    }

    val inDegree = buildInDegreeMap(normalized)
    val seeds = normalized.nodes.filter { node -> (inDegree[node] ?: 0) == 0 } +
        normalized.nodes.filter { node -> (inDegree[node] ?: 0) != 0 }
    seeds.forEach { node -> if (color[node] == 0) visit(node) }

    val reversedKeys = reversed.mapTo(mutableSetOf()) { edge ->
        "${edge.id}:${edge.src}->${edge.dst}"
    }
    val acyclicEdges = normalized.edges.map { edge ->
        if ("${edge.id}:${edge.src}->${edge.dst}" in reversedKeys) {
            edge.copy(src = edge.dst, dst = edge.src)
        } else {
            edge
        }
    }
    return CycleRemovalResult(
        acyclic = normalized.copy(
            nodes = normalized.nodes.toList(),
            edges = acyclicEdges,
            nodeById = LinkedHashMap(normalized.nodeById),
        ),
        reversed = reversed,
    )
}
