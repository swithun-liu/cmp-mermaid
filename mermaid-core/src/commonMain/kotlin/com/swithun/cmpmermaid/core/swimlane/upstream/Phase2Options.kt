package com.swithun.cmpmermaid.core.swimlane.upstream

// Mermaid.js 12.0.0: swimlanes/phase2.options.ts -> buildTopLaneMap.
internal fun buildTopLaneMap(graph: SwimlaneGraph): Map<String, String?> {
    val cache = mutableMapOf<String, String?>()

    fun resolve(id: String): String? {
        if (id in cache) return cache[id]
        val node = graph.nodeById[id]
        if (node == null) {
            cache[id] = null
            return null
        }
        val parentId = node.parentId
        if (parentId == null) {
            cache[id] = null
            return null
        }
        val parentLane = resolve(parentId)
        val lane = parentLane ?: parentId
        cache[id] = lane
        return lane
    }

    graph.nodes.forEach(::resolve)
    return cache
}

internal fun createTopLaneResolver(graph: SwimlaneGraph): (String) -> String? {
    val topLaneMap = buildTopLaneMap(graph)
    return { id -> topLaneMap[id] }
}

internal fun buildTopLaneOrder(graph: SwimlaneGraph): List<String> =
    graph.layout.nodes
        .filter { node -> node.isGroup && node.parentId == null }
        .map(SwimlaneNode::id)
        .distinct()
        .asReversed()

internal fun resolveTopLaneOrder(
    graph: SwimlaneGraph,
    preferredOrder: List<String>? = null,
): List<String> {
    val sourceOrder = buildTopLaneOrder(graph)
    if (preferredOrder.isNullOrEmpty()) return sourceOrder

    val sourceLaneIds = sourceOrder.toSet()
    val seen = mutableSetOf<String>()
    val resolved = mutableListOf<String>()
    preferredOrder.forEach { laneId ->
        if (laneId in sourceLaneIds && seen.add(laneId)) resolved += laneId
    }
    sourceOrder.forEach { laneId ->
        if (seen.add(laneId)) resolved += laneId
    }
    return resolved
}
