package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneSize

/**
 * Recursive compound-graph layout for subgraphs with explicit local directions.
 *
 * Mermaid's Dagre adapter recursively measures directional clusters, then treats
 * each measured cluster as one node in its parent's graph. This stage preserves
 * that contract without coupling the native renderer to Dagre's graphlib types.
 */
internal object FlowCompoundLayout {
    fun isRequired(document: FlowchartDocument): Boolean =
        document.subgraphs.any { subgraph -> subgraph.direction != null }

    fun layout(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        edgeLabelSizes: Map<Int, SceneSize>,
        options: MermaidRenderOptions,
    ): GMResult<Result, MermaidError> =
        State(document, nodeSizes, edgeLabelSizes, options).layout()

    data class Result(
        val nodeBounds: Map<String, SceneRect>,
        val subgraphBounds: Map<String, SceneRect>,
    )

    private data class ContainerLayout(
        val size: SceneSize,
        val nodeBounds: Map<String, SceneRect>,
        val subgraphBounds: Map<String, SceneRect>,
    )

    private class State(
        private val document: FlowchartDocument,
        private val nodeSizes: Map<String, SceneSize>,
        private val edgeLabelSizes: Map<Int, SceneSize>,
        private val options: MermaidRenderOptions,
    ) {
        private val subgraphById = document.subgraphs.associateBy(FlowSubgraph::id)
        private val childrenByParent = document.subgraphs.groupBy(FlowSubgraph::parentId)
        private val nodeOrder = document.nodes.keys
            .withIndex()
            .associate { indexed -> indexed.value to indexed.index }
        private val subgraphOrder = document.subgraphs
            .withIndex()
            .associate { indexed -> indexed.value.id to indexed.index }
        private val subgraphDepth = document.subgraphs.associate { subgraph ->
            subgraph.id to calculateDepth(subgraph.id)
        }
        private val nodeParent = document.nodes.keys.associateWith { nodeId ->
            document.subgraphs
                .filter { subgraph -> nodeId in subgraph.nodeIds }
                .maxByOrNull { subgraph -> subgraphDepth[subgraph.id] ?: 0 }
                ?.id
        }

        fun layout(): GMResult<Result, MermaidError> =
            when (
                val root = layoutContainer(
                    containerId = null,
                    inheritedDirection = document.direction,
                    ancestry = emptySet(),
                )
            ) {
                is GMResult.Ok -> GMResult.Ok(
                    Result(
                        nodeBounds = root.value.nodeBounds,
                        subgraphBounds = root.value.subgraphBounds,
                    ),
                )
                is GMResult.Err -> root
            }

        private fun layoutContainer(
            containerId: String?,
            inheritedDirection: FlowDirection,
            ancestry: Set<String>,
        ): GMResult<ContainerLayout, MermaidError> {
            if (containerId != null && containerId in ancestry) {
                return GMResult.Err(
                    MermaidError.Layout("Subgraph hierarchy contains a cycle at '$containerId'"),
                )
            }
            val direction = containerId
                ?.let(subgraphById::get)
                ?.direction
                ?: inheritedDirection
            val directGroups = childrenByParent[containerId].orEmpty()
                .sortedBy { subgraph -> itemOrder(subgraph.id) }
            val directNodes = document.nodes.keys
                .filter { nodeId -> nodeParent[nodeId] == containerId }
            val itemIds = (directNodes + directGroups.map(FlowSubgraph::id))
                .distinct()
                .sortedBy(::itemOrder)
            val nextAncestry = if (containerId == null) ancestry else ancestry + containerId
            val childLayouts = linkedMapOf<String, ContainerLayout>()
            for (subgraph in directGroups) {
                when (
                    val child = layoutContainer(
                        containerId = subgraph.id,
                        inheritedDirection = direction,
                        ancestry = nextAncestry,
                    )
                ) {
                    is GMResult.Ok -> childLayouts[subgraph.id] = child.value
                    is GMResult.Err -> return child
                }
            }

            val itemSizes = linkedMapOf<String, SceneSize>()
            for (itemId in itemIds) {
                val size = nodeSizes[itemId] ?: childLayouts[itemId]?.size
                if (size == null) {
                    return GMResult.Err(
                        MermaidError.Layout("Missing measured size for compound item '$itemId'"),
                    )
                }
                itemSizes[itemId] = size
            }

            if (itemIds.isEmpty()) {
                return GMResult.Ok(emptyContainer(containerId))
            }

            val mappedEdges = mutableListOf<FlowEdge>()
            val mappedLabelSizes = linkedMapOf<Int, SceneSize>()
            val itemSet = itemIds.toSet()
            document.edges.forEachIndexed { edgeIndex, edge ->
                val from = directItem(edge.from, containerId, itemSet)
                val to = directItem(edge.to, containerId, itemSet)
                if (from == null || to == null || from == to) {
                    return@forEachIndexed
                }
                val mappedIndex = mappedEdges.size
                mappedEdges += edge.copy(from = from, to = to)
                edgeLabelSizes[edgeIndex]?.let { size -> mappedLabelSizes[mappedIndex] = size }
            }
            val layoutDocument = FlowchartDocument(
                direction = direction,
                nodes = itemIds.associateWith { id ->
                    FlowNode(
                        id = id,
                        label = id,
                        shape = SceneShapeKind.Rectangle,
                    )
                },
                edges = mappedEdges,
                subgraphs = emptyList(),
                classStyles = emptyMap(),
            )
            val layering = FlowLayering.build(layoutDocument)
            val itemBounds = FlowNodePlacer.place(
                direction = direction,
                layers = layering.layers,
                sizes = itemSizes,
                edgeLabelSizes = mappedLabelSizes,
                edges = mappedEdges,
                ranks = layering.ranks,
                options = options,
            )

            val placedNodes = linkedMapOf<String, SceneRect>()
            val placedSubgraphs = linkedMapOf<String, SceneRect>()
            itemIds.forEach { itemId ->
                val bounds = itemBounds[itemId] ?: return@forEach
                val child = childLayouts[itemId]
                if (child == null) {
                    placedNodes[itemId] = bounds
                } else {
                    placedNodes.putAll(child.nodeBounds.translate(bounds.left, bounds.top))
                    placedSubgraphs.putAll(child.subgraphBounds.translate(bounds.left, bounds.top))
                }
            }

            if (placedNodes.size != directNodes.size + childLayouts.values.sumOf { it.nodeBounds.size }) {
                return GMResult.Err(
                    MermaidError.Layout("Not every compound item in '${containerId ?: "<root>"}' was positioned"),
                )
            }

            val contentBounds = itemBounds.values.reduceOrNull(SceneRect::union)
                ?: return GMResult.Ok(emptyContainer(containerId))
            if (containerId == null) {
                return GMResult.Ok(
                    ContainerLayout(
                        size = SceneSize(contentBounds.width, contentBounds.height),
                        nodeBounds = placedNodes,
                        subgraphBounds = placedSubgraphs,
                    ),
                )
            }

            val frame = SceneRect(
                left = contentBounds.left - SUBGRAPH_HORIZONTAL_PADDING,
                top = contentBounds.top - SUBGRAPH_TITLE_PADDING,
                right = contentBounds.right + SUBGRAPH_HORIZONTAL_PADDING,
                bottom = contentBounds.bottom + SUBGRAPH_BOTTOM_PADDING,
            )
            val dx = -frame.left
            val dy = -frame.top
            return GMResult.Ok(
                ContainerLayout(
                    size = SceneSize(frame.width, frame.height),
                    nodeBounds = placedNodes.translate(dx, dy),
                    subgraphBounds = buildMap {
                        putAll(placedSubgraphs.translate(dx, dy))
                        put(containerId, SceneRect(0f, 0f, frame.width, frame.height))
                    },
                ),
            )
        }

        private fun directItem(
            endpoint: String,
            containerId: String?,
            directItems: Set<String>,
        ): String? {
            if (endpoint in directItems) {
                return endpoint
            }
            var groupId = when {
                endpoint in document.nodes -> nodeParent[endpoint]
                endpoint in subgraphById -> endpoint
                else -> null
            }
            while (groupId != null) {
                val group = subgraphById[groupId] ?: return null
                if (group.parentId == containerId && group.id in directItems) {
                    return group.id
                }
                groupId = group.parentId
            }
            return null
        }

        private fun itemOrder(id: String): Int {
            nodeOrder[id]?.let { return it }
            val subgraph = subgraphById[id]
            val firstNode = subgraph?.nodeIds?.mapNotNull(nodeOrder::get)?.minOrNull()
            return firstNode ?: nodeOrder.size + (subgraphOrder[id] ?: Int.MAX_VALUE / 2)
        }

        private fun calculateDepth(id: String): Int {
            var depth = 0
            var current = subgraphById[id]?.parentId
            val visited = mutableSetOf<String>()
            while (current != null && visited.add(current)) {
                depth += 1
                current = subgraphById[current]?.parentId
            }
            return depth
        }

        private fun emptyContainer(containerId: String?): ContainerLayout {
            if (containerId == null) {
                return ContainerLayout(SceneSize(0f, 0f), emptyMap(), emptyMap())
            }
            val bounds = SceneRect(0f, 0f, EMPTY_SUBGRAPH_WIDTH, EMPTY_SUBGRAPH_HEIGHT)
            return ContainerLayout(
                size = SceneSize(bounds.width, bounds.height),
                nodeBounds = emptyMap(),
                subgraphBounds = mapOf(containerId to bounds),
            )
        }
    }

    private fun Map<String, SceneRect>.translate(dx: Float, dy: Float): Map<String, SceneRect> =
        mapValues { (_, bounds) -> bounds.translate(dx, dy) }

    private const val SUBGRAPH_HORIZONTAL_PADDING = 24f
    private const val SUBGRAPH_TITLE_PADDING = 42f
    private const val SUBGRAPH_BOTTOM_PADDING = 24f
    private const val EMPTY_SUBGRAPH_WIDTH = 96f
    private const val EMPTY_SUBGRAPH_HEIGHT = 66f
}
