package io.github.cmpmermaid.core.flowchart

/**
 * Resolves the direction that owns each edge. The deepest containing subgraph
 * wins, then inherits from its ancestors before falling back to the diagram.
 */
internal object FlowDirectionResolver {
    fun resolve(document: FlowchartDocument): Map<Int, FlowDirection> {
        if (document.subgraphs.isEmpty()) {
            return document.edges.indices.associateWith { document.direction }
        }
        val subgraphById = document.subgraphs.associateBy(FlowSubgraph::id)
        val depth = document.subgraphs.associate { subgraph ->
            subgraph.id to depthOf(subgraph.id, subgraphById)
        }

        fun isAncestor(ancestorId: String, descendantId: String): Boolean {
            var current = subgraphById[descendantId]?.parentId
            val visited = mutableSetOf<String>()
            while (current != null && visited.add(current)) {
                if (current == ancestorId) {
                    return true
                }
                current = subgraphById[current]?.parentId
            }
            return false
        }

        fun contains(subgraph: FlowSubgraph, endpoint: String): Boolean =
            endpoint == subgraph.id ||
                endpoint in subgraph.nodeIds ||
                (endpoint in subgraphById && isAncestor(subgraph.id, endpoint))

        fun inheritedDirection(subgraph: FlowSubgraph): FlowDirection {
            var current: FlowSubgraph? = subgraph
            val visited = mutableSetOf<String>()
            while (current != null && visited.add(current.id)) {
                current.direction?.let { return it }
                current = current.parentId?.let(subgraphById::get)
            }
            return document.direction
        }

        return document.edges.indices.associateWith { edgeIndex ->
            val edge = document.edges[edgeIndex]
            document.subgraphs
                .filter { subgraph ->
                    contains(subgraph, edge.from) && contains(subgraph, edge.to)
                }
                .maxByOrNull { subgraph -> depth[subgraph.id] ?: 0 }
                ?.let(::inheritedDirection)
                ?: document.direction
        }
    }

    private fun depthOf(
        id: String,
        subgraphById: Map<String, FlowSubgraph>,
    ): Int {
        var depth = 0
        var current = subgraphById[id]?.parentId
        val visited = mutableSetOf<String>()
        while (current != null && visited.add(current)) {
            depth += 1
            current = subgraphById[current]?.parentId
        }
        return depth
    }
}
