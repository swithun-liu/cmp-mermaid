package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max

internal data class ProperLayeringResult(
    val layering: SwimlaneLayering,
    val graphWithDummies: SwimlaneGraph,
)

// Mermaid.js 12.0.0: swimlanes/phase2.dummies.ts -> makeProperLayering.
internal fun makeProperLayering(
    layering: SwimlaneLayering,
    acyclicGraph: SwimlaneGraph,
): ProperLayeringResult {
    val graph = normalizeGraph(acyclicGraph)
    val rankOf = layering.rankOf
    val layers = layering.layers.mapTo(mutableListOf()) { layer -> layer.toMutableList() }
    val dummy = layering.dummy.toMutableSet()
    var dummySequence = 0
    val nodeById = LinkedHashMap(graph.nodeById)

    fun addDummyAt(layer: Int): String {
        val id = "placeholder-${dummySequence++}"
        nodeById[id] = SwimlaneNode(
            id = id,
            isDummy = true,
        )
        dummy += id
        while (layers.size <= layer) layers += mutableListOf<String>()
        layers[layer] += id
        rankOf[id] = layer
        return id
    }

    val sortedEdges = graph.edges.sortedWith { first, second ->
        val idComparison = compareMermaidIds(first.id, second.id)
        if (idComparison != 0) {
            idComparison
        } else {
            val sourceComparison = compareMermaidIds(first.src, second.src)
            if (sourceComparison != 0) {
                sourceComparison
            } else {
                compareMermaidIds(first.dst, second.dst)
            }
        }
    }
    val newEdges = mutableListOf<SwimlaneEdgeRef>()
    for (edge in sortedEdges) {
        val sourceRank = rankOf[edge.src] ?: 0
        val destinationRank = rankOf[edge.dst] ?: 0
        if (destinationRank - sourceRank <= 1) {
            newEdges += edge
            continue
        }
        var previous = edge.src
        var chainIndex = 0
        for (layer in sourceRank + 1 until destinationRank) {
            val dummyId = addDummyAt(layer)
            newEdges += SwimlaneEdgeRef(
                id = "${edge.id}#$chainIndex",
                src = previous,
                dst = dummyId,
                weight = edge.weight,
                ref = edge.ref,
            )
            previous = dummyId
            chainIndex++
        }
        val lastIndex = destinationRank - sourceRank - 2
        newEdges += SwimlaneEdgeRef(
            id = "${edge.id}#${max(lastIndex + 1, 0)}",
            src = previous,
            dst = edge.dst,
            weight = edge.weight,
            ref = edge.ref,
        )
    }

    val nodes = graph.nodes + dummy.filter { id -> id !in graph.nodes }
    val properLayering = SwimlaneLayering(
        layers = layers,
        rankOf = rankOf,
        dummy = dummy,
    )
    return ProperLayeringResult(
        layering = properLayering,
        graphWithDummies = SwimlaneGraph(
            nodes = nodes,
            edges = newEdges,
            layout = graph.layout,
            nodeById = nodeById,
        ),
    )
}
