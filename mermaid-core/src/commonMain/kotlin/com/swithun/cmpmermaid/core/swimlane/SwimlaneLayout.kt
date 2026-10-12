package com.swithun.cmpmermaid.core.swimlane

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShapeKind
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.flowchart.FlowDirection
import com.swithun.cmpmermaid.core.flowchart.FlowEdge
import com.swithun.cmpmermaid.core.flowchart.FlowLayoutPlacement
import com.swithun.cmpmermaid.core.flowchart.FlowRoutedEdge
import com.swithun.cmpmermaid.core.flowchart.FlowSubgraph
import com.swithun.cmpmermaid.core.flowchart.FlowchartDocument
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapeLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/**
 * Mermaid.js 12.0.0:
 * rendering-util/layout-algorithms/swimlanes.
 *
 * This is the platform-neutral translation of the upstream swimlane pipeline:
 * label nodes, cycle removal, lane-aware Sugiyama layering, lane-preserving
 * ordering, coordinate assignment, lane sizing, direction transforms, and
 * obstacle-aware orthogonal routing.
 */
internal object SwimlaneLayout {
    const val DefaultLaneId: String = "__swimlane_default__"

    fun prepareDocument(
        document: FlowchartDocument,
        look: String,
    ): FlowchartDocument {
        val assignedNodeIds = document.subgraphs.flatMapTo(mutableSetOf()) { it.nodeIds }
        val looseNodeIds = document.nodes.keys.filterTo(linkedSetOf()) { it !in assignedNodeIds }
        if (looseNodeIds.isEmpty()) return document

        val colorIndex = (
            document.subgraphs.mapNotNull(FlowSubgraph::colorIndex) +
                document.nodes.values.mapNotNull { it.colorIndex }
            ).maxOrNull()?.plus(1) ?: 0
        return document.copy(
            subgraphs = document.subgraphs + FlowSubgraph(
                id = DefaultLaneId,
                label = "",
                labelSpans = emptyList(),
                nodeIds = looseNodeIds,
                padding = 20f,
                look = look,
                colorIndex = colorIndex,
            ),
        )
    }

    fun layout(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
        edgeLabelSizes: Map<Int, SceneSize>,
        options: MermaidRenderOptions,
    ): GMResult<FlowLayoutPlacement, MermaidError> {
        if (document.nodes.isEmpty()) {
            return GMResult.Ok(FlowLayoutPlacement(emptyMap(), emptyMap(), emptyMap()))
        }
        val model = buildModel(document, nodeSizes, edgeLabelSizes)
        val acyclic = removeCycles(model)
        val laneOrder = if (options.swimlane.automaticLaneOrdering) {
            optimizeLaneOrder(model, document)
        } else {
            sourceLaneOrder(document)
        }
        val layering = if (options.swimlane.ignoreCrossLaneEdges) {
            assignLaneAwareLayers(acyclic, document.direction)
        } else {
            assignGravityLayers(
                graph = acyclic,
                optimizeRanksByCrossings = options.swimlane.optimizeRanksByCrossings,
            )
        }
        val proper = makeProperLayering(acyclic, layering)
        val ordered = orderLayers(proper, laneOrder)
        assignCoordinates(
            graph = proper.graph,
            layers = ordered,
            laneOrder = laneOrder,
            nodeGap = options.nodeSpacing,
            layerGap = options.rankSpacing,
            direction = document.direction,
        )
        writeBackGroups(proper.graph, document)

        val routed = routeEdges(
            graph = proper.graph,
            document = document,
            nodeShapeLayouts = nodeShapeLayouts,
        )
        applyDirection(proper.graph, routed, document.direction)
        portSwapToLShape(proper.graph, routed)
        clipAndSimplifyEdges(proper.graph, routed, nodeShapeLayouts)
        resolveRenderedOrthogonalCrossings(proper.graph, routed)
        adjustLaneTitleBands(
            graph = proper.graph,
            routes = routed,
            direction = document.direction,
        )
        val labelAnchors = anchorEdgeLabels(
            graph = proper.graph,
            routes = routed,
            edgeLabelSizes = edgeLabelSizes,
            direction = document.direction,
        )

        val nodeBounds = document.nodes.keys.associateWith { id ->
            proper.graph.nodes.getValue(id).bounds()
        }
        val subgraphBounds = document.subgraphs.mapNotNull { subgraph ->
            proper.graph.nodes[subgraph.id]?.bounds()?.let { subgraph.id to it }
        }.toMap(linkedMapOf())
        val edges = routed.mapValues { (index, route) ->
            val points = route.points.removeAdjacentDuplicates()
            FlowRoutedEdge(
                points = points,
                labelAnchor = labelAnchors[index] ?: points.halfLengthPoint(),
                curveOverride = "rounded",
            )
        }
        return GMResult.Ok(FlowLayoutPlacement(nodeBounds, subgraphBounds, edges))
    }

    // Mermaid.js 12.0.0: swimlanes/edgeLabelNodes.ts -> createEdgeLabelNodes.
    private fun buildModel(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        edgeLabelSizes: Map<Int, SceneSize>,
    ): WorkGraph {
        val directParent = directParentMap(document)
        val nodes = linkedMapOf<String, WorkNode>()
        document.subgraphs.asReversed().forEach { subgraph ->
            nodes[subgraph.id] = WorkNode(
                id = subgraph.id,
                parentId = subgraph.parentId,
                width = 0f,
                height = 0f,
                isGroup = true,
                padding = subgraph.padding,
            )
        }
        document.nodes.forEach { (id, _) ->
            val size = nodeSizes.getValue(id)
            nodes[id] = WorkNode(
                id = id,
                parentId = directParent[id],
                width = size.width,
                height = size.height,
            )
        }

        val edges = mutableListOf<WorkEdge>()
        document.edges.forEachIndexed { index, edge ->
            val labelSize = edgeLabelSizes[index]
            if (labelSize == null) {
                edges += WorkEdge(edge.id, edge.from, edge.to, index)
                return@forEachIndexed
            }
            val sourceParent = nodes[edge.from]?.parentId
            val targetParent = nodes[edge.to]?.parentId
            val labelNodeId = "edge-label-${edge.from}-${edge.to}-${edge.id}"
            nodes[labelNodeId] = WorkNode(
                id = labelNodeId,
                parentId = if (sourceParent != targetParent) targetParent else sourceParent,
                width = labelSize.width,
                height = labelSize.height,
                isDummy = true,
                isEdgeLabel = true,
            )
            edges += WorkEdge(
                id = "${edge.id}-to-label",
                from = edge.from,
                to = labelNodeId,
                originalIndex = index,
                originalFrom = edge.from,
                originalTo = edge.to,
            )
            edges += WorkEdge(
                id = "${edge.id}-from-label",
                from = labelNodeId,
                to = edge.to,
                originalIndex = index,
                originalFrom = edge.from,
                originalTo = edge.to,
            )
        }
        return WorkGraph(nodes, edges)
    }

    private fun directParentMap(document: FlowchartDocument): Map<String, String> = buildMap {
        document.subgraphs.forEach { group ->
            group.nodeIds.forEach { childId -> put(childId, group.id) }
        }
    }

    private fun sourceLaneOrder(document: FlowchartDocument): List<String> =
        document.subgraphs.filter { it.parentId == null }.map { it.id }.asReversed()

    // Mermaid.js 12.0.0: swimlanes/phase0.helpers.ts and phase1.cycles.ts.
    private fun removeCycles(graph: WorkGraph): WorkGraph {
        val contentIds = graph.nodes.values.filterNot { it.isGroup }.map { it.id }
        val validIds = contentIds.toSet()
        val edges = graph.edges
            .filter { it.from in validIds && it.to in validIds }
            .distinctBy { "${it.id}:${it.from}->${it.to}" }
        val adjacency = contentIds.associateWithTo(linkedMapOf()) { mutableListOf<WorkEdge>() }
        edges.forEach { edge -> adjacency.getValue(edge.from) += edge }
        adjacency.values.forEach { outgoing ->
            outgoing.sortWith(compareBy<WorkEdge> { it.to }.thenBy { it.id })
        }
        val indegree = contentIds.associateWithTo(linkedMapOf()) { 0 }
        edges.forEach { edge -> indegree[edge.to] = indegree.getValue(edge.to) + 1 }
        val colors = contentIds.associateWithTo(mutableMapOf()) { 0 }
        val reversed = mutableSetOf<String>()

        fun visit(id: String) {
            colors[id] = 1
            adjacency[id].orEmpty().forEach { edge ->
                when (colors[edge.to]) {
                    0 -> visit(edge.to)
                    1 -> reversed += edge.key
                }
            }
            colors[id] = 2
        }

        val seeds = contentIds.filter { indegree.getValue(it) == 0 } +
            contentIds.filter { indegree.getValue(it) != 0 }
        seeds.forEach { id -> if (colors[id] == 0) visit(id) }
        return graph.copy(
            edges = edges.map { edge ->
                if (edge.key in reversed) edge.copy(from = edge.to, to = edge.from) else edge
            },
        )
    }

    // Mermaid.js 12.0.0: swimlanes/phase2.laneAwareCompact.ts.
    private fun assignLaneAwareLayers(
        graph: WorkGraph,
        direction: FlowDirection,
    ): Layering {
        val order = if (direction == FlowDirection.LeftToRight) {
            topologicalByGeneration(graph)
        } else {
            topological(graph)
        }
        val rank = mutableMapOf<String, Int>()
        val nextFree = mutableMapOf<String, Int>()
        val incoming = graph.edges.groupBy(WorkEdge::to)
        order.forEach { id ->
            val lane = topLaneOf(id, graph) ?: id
            val base = incoming[id].orEmpty().maxOfOrNull { edge ->
                val predecessorRank = rank[edge.from] ?: 0
                predecessorRank + if (topLaneOf(edge.from, graph) == lane) 1 else 0
            } ?: 0
            val resolved = max(base, nextFree[lane] ?: 0)
            rank[id] = resolved
            nextFree[lane] = resolved + 1
        }
        return Layering(buildLayers(order, rank), rank)
    }

    // Mermaid.js 12.0.0: swimlanes/phase2.longestPath.ts and phase2.gravity.ts.
    private fun assignGravityLayers(
        graph: WorkGraph,
        optimizeRanksByCrossings: Boolean,
    ): Layering {
        val order = topological(graph)
        val incoming = graph.edges.groupBy(WorkEdge::to)
        val outgoing = graph.edges.groupBy(WorkEdge::from)
        val rank = mutableMapOf<String, Int>()
        order.forEach { id ->
            rank[id] = incoming[id].orEmpty().maxOfOrNull { (rank[it.from] ?: 0) + 1 } ?: 0
        }
        repeat(8) {
            var changed = false
            fun relax(ids: List<String>) {
                ids.forEach { id ->
                    val predecessors = incoming[id].orEmpty().map(WorkEdge::from)
                    val successors = outgoing[id].orEmpty().map(WorkEdge::to)
                    if (predecessors.isEmpty() && successors.isEmpty()) return@forEach
                    val lower = predecessors.maxOfOrNull { (rank[it] ?: 0) + 1 } ?: 0
                    val upper = successors.minOfOrNull { (rank[it] ?: 0) - 1 }
                    val predecessorAverage = predecessors
                        .takeIf(List<String>::isNotEmpty)
                        ?.map { (rank[it] ?: 0) + 1 }
                        ?.average()
                        ?: (rank[id] ?: 0).toDouble()
                    val successorAverage = successors
                        .takeIf(List<String>::isNotEmpty)
                        ?.map { (rank[it] ?: 0) - 1 }
                        ?.average()
                        ?: (rank[id] ?: 0).toDouble()
                    val desired = round((predecessorAverage + successorAverage) / 2.0).toInt()
                    val resolved = if (upper == null) max(lower, desired) else {
                        desired.coerceIn(lower, max(lower, upper))
                    }
                    if (resolved != rank[id]) {
                        rank[id] = resolved
                        changed = true
                    }
                }
            }
            relax(order)
            relax(order.asReversed())
            if (!changed) return@repeat
        }
        if (optimizeRanksByCrossings) {
            optimizeRanks(graph, rank)
        }
        return Layering(buildLayers(order, rank), rank)
    }

    private fun optimizeRanks(
        graph: WorkGraph,
        rank: MutableMap<String, Int>,
    ) {
        val predecessors = graph.edges.groupBy(WorkEdge::to)
        var best = crossingCount(buildLayers(graph.contentIds(), rank), graph.edges, rank)
        repeat(4) {
            var changed = false
            graph.contentIds().sortedByDescending { rank[it] ?: 0 }.forEach { id ->
                val current = rank[id] ?: 0
                if (current == 0) return@forEach
                val lower = predecessors[id].orEmpty().maxOfOrNull {
                    (rank[it.from] ?: 0) + 1
                } ?: 0
                if (lower >= current) return@forEach
                rank[id] = lower
                val score = crossingCount(buildLayers(graph.contentIds(), rank), graph.edges, rank)
                if (score < best) {
                    best = score
                    changed = true
                } else {
                    rank[id] = current
                }
            }
            if (!changed) return
        }
    }

    private fun topological(graph: WorkGraph): List<String> {
        val nodes = graph.contentIds()
        val indegree = nodes.associateWithTo(mutableMapOf()) { 0 }
        val outgoing = nodes.associateWithTo(mutableMapOf()) { mutableListOf<String>() }
        graph.edges.forEach { edge ->
            indegree[edge.to] = (indegree[edge.to] ?: 0) + 1
            outgoing.getOrPut(edge.from) { mutableListOf() } += edge.to
        }
        outgoing.values.forEach(MutableList<String>::sort)
        val queue = indegree.filterValues { it == 0 }.keys.sorted().toMutableList()
        val result = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val id = queue.removeAt(0)
            result += id
            outgoing[id].orEmpty().forEach { next ->
                indegree[next] = (indegree[next] ?: 0) - 1
                if (indegree[next] == 0) {
                    queue += next
                    queue.sort()
                }
            }
        }
        return if (result.size == nodes.size) result else nodes.sorted()
    }

    private fun topologicalByGeneration(graph: WorkGraph): List<String> {
        val nodes = graph.contentIds()
        val indegree = nodes.associateWithTo(mutableMapOf()) { 0 }
        val outgoing = nodes.associateWithTo(mutableMapOf()) { mutableListOf<String>() }
        graph.edges.forEach { edge ->
            indegree[edge.to] = (indegree[edge.to] ?: 0) + 1
            outgoing.getOrPut(edge.from) { mutableListOf() } += edge.to
        }
        outgoing.values.forEach(MutableList<String>::sort)
        var frontier = indegree.filterValues { it == 0 }.keys.sorted()
        val result = mutableListOf<String>()
        while (frontier.isNotEmpty()) {
            val next = mutableListOf<String>()
            frontier.forEach { id ->
                result += id
                outgoing[id].orEmpty().forEach { child ->
                    indegree[child] = (indegree[child] ?: 0) - 1
                    if (indegree[child] == 0) next += child
                }
            }
            frontier = next.sorted()
        }
        return if (result.size == nodes.size) result else nodes.sorted()
    }

    private fun buildLayers(
        order: List<String>,
        rank: Map<String, Int>,
    ): List<MutableList<String>> {
        val maxRank = order.maxOfOrNull { rank[it] ?: 0 } ?: 0
        val result = List(maxRank + 1) { mutableListOf<String>() }
        order.forEach { id -> result[(rank[id] ?: 0).coerceAtLeast(0)] += id }
        return result
    }

    // Mermaid.js 12.0.0: swimlanes/phase2.dummies.ts.
    private fun makeProperLayering(
        graph: WorkGraph,
        layering: Layering,
    ): ProperLayering {
        val nodes = LinkedHashMap(graph.nodes)
        val layers = layering.layers.mapTo(mutableListOf()) { it.toMutableList() }
        val ranks = layering.rank.toMutableMap()
        val edges = mutableListOf<WorkEdge>()
        var sequence = 0
        graph.edges.sortedWith(
            compareBy<WorkEdge> { it.id }.thenBy { it.from }.thenBy { it.to },
        ).forEach { edge ->
            val sourceRank = ranks[edge.from] ?: 0
            val targetRank = ranks[edge.to] ?: 0
            if (targetRank - sourceRank <= 1) {
                edges += edge
                return@forEach
            }
            var previous = edge.from
            var segment = 0
            for (layer in (sourceRank + 1) until targetRank) {
                val id = "placeholder-${sequence++}"
                nodes[id] = WorkNode(id, null, 0f, 0f, isDummy = true)
                while (layers.size <= layer) layers.add(mutableListOf())
                layers[layer] += id
                ranks[id] = layer
                edges += edge.copy(id = "${edge.id}#${segment++}", from = previous, to = id)
                previous = id
            }
            edges += edge.copy(id = "${edge.id}#$segment", from = previous)
        }
        return ProperLayering(WorkGraph(nodes, edges), layers, ranks)
    }

    // Mermaid.js 12.0.0: swimlanes/phase3.ordering.ts.
    private fun orderLayers(
        proper: ProperLayering,
        laneOrder: List<String>,
    ): List<MutableList<String>> {
        val layers = proper.layers.mapTo(mutableListOf()) { it.toMutableList() }
        repeat(3) {
            for (index in 1 until layers.size) {
                layers[index] = reorderLayer(
                    fixed = layers[index - 1],
                    target = layers[index],
                    edges = proper.graph.edges,
                    down = true,
                    graph = proper.graph,
                    laneOrder = laneOrder,
                )
                transposeImprove(
                    upper = layers[index - 1],
                    current = layers[index],
                    next = layers.getOrNull(index + 1),
                    graph = proper.graph,
                )
            }
            for (index in layers.lastIndex - 1 downTo 0) {
                layers[index] = reorderLayer(
                    fixed = layers[index + 1],
                    target = layers[index],
                    edges = proper.graph.edges,
                    down = false,
                    graph = proper.graph,
                    laneOrder = laneOrder,
                )
                transposeImprove(
                    upper = layers[index + 1],
                    current = layers[index],
                    next = layers.getOrNull(index - 1),
                    graph = proper.graph,
                )
            }
        }
        return layers
    }

    private fun reorderLayer(
        fixed: List<String>,
        target: List<String>,
        edges: List<WorkEdge>,
        down: Boolean,
        graph: WorkGraph,
        laneOrder: List<String>,
    ): MutableList<String> {
        val fixedIndex = fixed.withIndex().associate { it.value to it.index }
        val currentIndex = target.withIndex().associate { it.value to it.index }
        val positions = target.associateWithTo(mutableMapOf()) { mutableListOf<Int>() }
        edges.forEach { edge ->
            val fixedId = if (down) edge.from else edge.to
            val targetId = if (down) edge.to else edge.from
            val position = fixedIndex[fixedId] ?: return@forEach
            positions[targetId]?.add(position)
        }
        fun sorted(ids: List<String>): List<String> = ids.sortedWith { first, second ->
            val firstMedian = median(positions[first].orEmpty())
            val secondMedian = median(positions[second].orEmpty())
            when {
                firstMedian != secondMedian -> firstMedian.compareTo(secondMedian)
                else -> (currentIndex[first] ?: 0).compareTo(currentIndex[second] ?: 0)
                    .takeIf { it != 0 } ?: first.compareTo(second)
            }
        }
        val byLane = target.groupBy { topLaneOf(it, graph) }
        val result = mutableListOf<String>()
        laneOrder.forEach { lane -> result += sorted(byLane[lane].orEmpty()) }
        val unassigned = sorted(byLane[null].orEmpty())
        unassigned.forEach { id ->
            val score = average(positions[id].orEmpty())
            val insertion = result.indexOfFirst { placed ->
                score < average(positions[placed].orEmpty())
            }
            if (insertion < 0) result += id else result.add(insertion, id)
        }
        return result
    }

    private fun transposeImprove(
        upper: List<String>,
        current: MutableList<String>,
        next: List<String>?,
        graph: WorkGraph,
    ) {
        var best = adjacentCrossings(upper, current, graph.edges) +
            (next?.let { adjacentCrossings(current, it, graph.edges) } ?: 0)
        var improved = true
        while (improved) {
            improved = false
            for (index in 0 until current.lastIndex) {
                if (topLaneOf(current[index], graph) != topLaneOf(current[index + 1], graph)) {
                    continue
                }
                current.swap(index, index + 1)
                val score = adjacentCrossings(upper, current, graph.edges) +
                    (next?.let { adjacentCrossings(current, it, graph.edges) } ?: 0)
                if (score < best) {
                    best = score
                    improved = true
                } else {
                    current.swap(index, index + 1)
                }
            }
        }
    }

    // Mermaid.js 12.0.0: swimlanes/phase4.coordinates.ts.
    private fun assignCoordinates(
        graph: WorkGraph,
        layers: List<List<String>>,
        laneOrder: List<String>,
        nodeGap: Float,
        layerGap: Float,
        direction: FlowDirection,
    ) {
        val horizontal = direction == FlowDirection.LeftToRight ||
            direction == FlowDirection.RightToLeft
        val layerHeights = layers.map { layer ->
            layer.maxOfOrNull { graph.nodes.getValue(it).height } ?: 0f
        }
        val extraLayerGaps = layers.dropLast(1).mapIndexed { index, layer ->
            if (!horizontal) return@mapIndexed 0f
            val currentWidth = layer.maxOfOrNull { graph.nodes.getValue(it).width } ?: 0f
            val nextWidth = layers[index + 1].maxOfOrNull {
                graph.nodes.getValue(it).width
            } ?: 0f
            val normalSpacing = layerHeights[index] / 2f + layerHeights[index + 1] / 2f
            max(0f, (currentWidth + nextWidth) / 2f - normalSpacing - layerGap)
        }
        val lanesUsed = layers.flatten().mapTo(linkedSetOf()) { topLaneOf(it, graph) }
        val columns = buildList<String?> {
            if (null in lanesUsed) add(null)
            laneOrder.filterTo(this) { it in lanesUsed }
        }
        val laneWidths = mutableMapOf<String?, Float>()
        columns.forEach { laneWidths[it] = 0f }
        layers.forEach { layer ->
            layer.groupBy { topLaneOf(it, graph) }.forEach { (lane, ids) ->
                val width = ids.sumOf { graph.nodes.getValue(it).width.toDouble() }.toFloat() +
                    nodeGap * max(0, ids.size - 1)
                laneWidths[lane] = max(laneWidths[lane] ?: 0f, width)
            }
        }
        val laneGap = nodeGap * 2f
        val totalWidth = columns.sumOf { (laneWidths[it] ?: 0f).toDouble() }.toFloat() +
            laneGap * max(0, columns.size - 1)
        var cursor = -totalWidth / 2f
        val centers = mutableMapOf<String?, Float>()
        columns.forEachIndexed { index, lane ->
            val width = laneWidths[lane] ?: 0f
            centers[lane] = cursor + width / 2f
            cursor += width + if (index < columns.lastIndex) laneGap else 0f
        }

        var yOffset = 0f
        layers.forEachIndexed { layerIndex, layer ->
            val layerHeight = layerHeights[layerIndex]
            val byLane = layer.groupBy { topLaneOf(it, graph) }
            columns.forEach { lane ->
                val laneNodes = byLane[lane].orEmpty()
                if (laneNodes.isEmpty()) return@forEach
                val center = centers.getValue(lane)
                val width = laneNodes.sumOf {
                    graph.nodes.getValue(it).width.toDouble()
                }.toFloat() + nodeGap * max(0, laneNodes.size - 1)
                var x = center - width / 2f
                laneNodes.forEach { id ->
                    graph.nodes.getValue(id).apply {
                        this.x = x + this.width / 2f
                        this.y = yOffset + layerHeight / 2f
                        this.positioned = true
                        x += this.width + nodeGap
                    }
                }
            }
            yOffset += layerHeight + layerGap + (extraLayerGaps.getOrNull(layerIndex) ?: 0f)
        }

        graph.edges.groupBy(WorkEdge::originalIndex).values.forEach { chain ->
            val first = chain.firstOrNull() ?: return@forEach
            val source = graph.nodes[first.originalFrom] ?: return@forEach
            val target = graph.nodes[first.originalTo] ?: return@forEach
            val midX = round((source.x + target.x) / 2f)
            chain.flatMap { listOf(it.from, it.to) }.distinct().forEach { id ->
                graph.nodes[id]
                    ?.takeIf { node -> node.isDummy && !node.isEdgeLabel }
                    ?.x = midX
            }
        }
    }

    // Mermaid.js 12.0.0: swimlanes/helpers.ts -> writeBackToLayoutData.
    private fun writeBackGroups(
        graph: WorkGraph,
        document: FlowchartDocument,
    ) {
        val topLevelGroups = mutableListOf<WorkNode>()
        val capturedBounds = linkedMapOf<String, SceneRect>()
        document.subgraphs.forEach { group ->
            val workGroup = graph.nodes.getValue(group.id)
            if (group.parentId == null) {
                topLevelGroups += workGroup
            }
            val children = graph.nodes.values.filter { it.parentId == group.id }
            val bounds = boundsOf(children) ?: return@forEach
            // Mermaid.js 12.0.0: swimlanes/helpers.ts ->
            // writeBackToLayoutData. Bounds are captured in source order, so
            // an outer group keeps the current size of a nested group.
            capturedBounds[group.id] = bounds
            workGroup.setBounds(
                bounds.expand(
                    horizontal = if (group.parentId == null) {
                        max(group.padding, 20f)
                    } else {
                        group.padding / 2f
                    },
                    vertical = group.padding / 2f,
                ),
            )
        }

        val contentBounds = topLevelGroups.mapNotNull { lane ->
            capturedBounds[lane.id]?.let { lane to it }
        }
        if (contentBounds.isEmpty()) return
        val globalTop = contentBounds.minOf { it.second.top }
        val globalBottom = contentBounds.maxOf { it.second.bottom }
        val maxPadding = topLevelGroups.maxOfOrNull(WorkNode::padding) ?: 20f
        val verticalMargin = max(maxPadding, 36f)
        val laneHeight = globalBottom - globalTop + verticalMargin * 2f
        val centerY = (globalTop + globalBottom) / 2f

        topLevelGroups.forEach { lane ->
            lane.y = centerY
            lane.height = laneHeight
            lane.contentTop = globalTop
            lane.positioned = true
        }
        contentBounds.forEach { (lane, bounds) ->
            lane.width = bounds.width + max(lane.padding, 20f) * 2f
            lane.x = bounds.center.x
        }
        alignAdjacentLaneWidths(contentBounds.map { it.first })
    }

    private fun alignAdjacentLaneWidths(lanes: List<WorkNode>) {
        val sorted = lanes.sortedBy(WorkNode::x)
        if (sorted.size < 2) return
        val distances = sorted.zipWithNext { first, second -> second.x - first.x }
        val offsets = MutableList(sorted.size) { 0f }
        distances.indices.forEach { index ->
            offsets[index + 1] = 2f * distances[index] - offsets[index]
        }
        var lower = 0f
        var upper = Float.POSITIVE_INFINITY
        sorted.indices.forEach { index ->
            if (index % 2 == 0) {
                lower = max(lower, sorted[index].width - offsets[index])
            } else {
                upper = min(upper, offsets[index] - sorted[index].width)
            }
        }
        val seed = if (lower <= upper) (lower + upper) / 2f else lower
        sorted.indices.forEach { index ->
            val candidate = offsets[index] + if (index % 2 == 0) seed else -seed
            sorted[index].width = max(sorted[index].width, candidate)
        }
    }

    // Mermaid.js 12.0.0: swimlanes/orthogonalRouter/router.ts.
    private fun routeEdges(
        graph: WorkGraph,
        document: FlowchartDocument,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
    ): MutableMap<Int, MutableRoute> {
        val obstacles = document.nodes.keys.map { id ->
            val bounds = graph.nodes.getValue(id).bounds()
            Obstacle(id, bounds.expand(8f, 8f))
        }
        val routeOrder = document.edges.indices.sortedWith(
            compareByDescending<Int> { index ->
                val edge = document.edges[index]
                topLaneOf(edge.from, graph) != topLaneOf(edge.to, graph)
            }.thenBy { index ->
                val edge = document.edges[index]
                val from = graph.nodes.getValue(edge.from)
                val to = graph.nodes.getValue(edge.to)
                abs(to.x - from.x) + abs(to.y - from.y)
            }.thenBy { it },
        )
        val sides = assignPortSides(
            edges = document.edges,
            graph = graph,
            diamondNodeIds = document.nodes.values
                .filter { node -> node.shape == SceneShapeKind.Diamond }
                .mapTo(mutableSetOf()) { node -> node.id },
        )
        val portOffsets = assignPortOffsets(document.edges, graph, sides)
        val routed = linkedMapOf<Int, MutableRoute>()
        routeOrder.forEach { index ->
            val edge = document.edges[index]
            val source = graph.nodes[edge.from] ?: return@forEach
            val target = graph.nodes[edge.to] ?: return@forEach
            if (edge.from == edge.to) {
                routed[index] = MutableRoute(selfLoop(source), edge)
                return@forEach
            }
            val side = sides.getValue(index)
            val sourcePort = port(
                source,
                side.source,
                portOffsets[index to EndpointRole.Source] ?: 0f,
            )
            val targetPort = port(
                target,
                side.target,
                portOffsets[index to EndpointRole.Target] ?: 0f,
            )
            val sourceAnchor = sourcePort.move(side.source, 20f)
            val targetAnchor = targetPort.move(side.target, 20f)
            val middle = orthogonalPath(
                source = sourceAnchor,
                target = targetAnchor,
                sourceId = edge.from,
                targetId = edge.to,
                obstacles = obstacles,
                existing = routed.values.flatMap(MutableRoute::points),
            )
            val points = (listOf(sourcePort) + middle + targetPort)
                .removeAdjacentDuplicates()
                .orthogonalized()
                .simplified()
            routed[index] = MutableRoute(points.toMutableList(), edge)
        }
        return routed
    }

    private fun assignPortSides(
        edges: List<FlowEdge>,
        graph: WorkGraph,
        diamondNodeIds: Set<String>,
    ): Map<Int, EndpointSides> {
        val result = linkedMapOf<Int, EndpointSides>()
        val sideInfoByIndex = linkedMapOf<Int, EdgeSideInfo>()
        edges.forEachIndexed { index, edge ->
            if (edge.from == edge.to) return@forEachIndexed
            val source = graph.nodes[edge.from] ?: return@forEachIndexed
            val target = graph.nodes[edge.to] ?: return@forEachIndexed
            val dx = target.x - source.x
            val dy = target.y - source.y
            sideInfoByIndex[index] = EdgeSideInfo(
                edgeIndex = index,
                sourceId = edge.from,
                targetId = edge.to,
                source = chooseSide(source, target.center(), Side.Bottom),
                target = chooseSide(target, source.center(), Side.Bottom),
                absoluteDx = abs(dx),
                absoluteDy = abs(dy),
                dxSign = dx.compareTo(0f),
                dySign = dy.compareTo(0f),
            )
        }

        // Mermaid.js 12.0.0: swimlanes/orthogonalRouter/router.ts ->
        // Step 6.2 sibling side-splitting. Keep the strongest directional
        // preference on its primary side and move weaker siblings only when
        // their secondary side has strictly less committed load.
        val sideLoad = mutableMapOf<Pair<String, Side>, Int>()
        sideInfoByIndex.values.forEach { info ->
            val sourceKey = info.sourceId to info.source
            val targetKey = info.targetId to info.target
            sideLoad[sourceKey] = (sideLoad[sourceKey] ?: 0) + 1
            sideLoad[targetKey] = (sideLoad[targetKey] ?: 0) + 1
        }
        sideInfoByIndex.values
            .groupBy { info -> info.sourceId to info.source }
            .values
            .filter { group -> group.size >= 2 }
            .forEach { group ->
                val ordered = group.sortedWith(
                    compareByDescending<EdgeSideInfo>(EdgeSideInfo::preferenceStrength)
                        .thenBy(EdgeSideInfo::edgeIndex),
                )
                ordered.drop(1).forEach { info ->
                    val secondary = info.secondarySide()
                    val primaryKey = info.sourceId to info.source
                    val secondaryKey = info.sourceId to secondary
                    val primaryLoad = sideLoad[primaryKey] ?: 0
                    val secondaryLoad = sideLoad[secondaryKey] ?: 0
                    if (secondaryLoad >= primaryLoad) return@forEach
                    sideLoad[primaryKey] = primaryLoad - 1
                    sideLoad[secondaryKey] = secondaryLoad + 1
                    info.source = secondary
                }
            }

        // Mermaid.js 12.0.0: swimlanes/orthogonalRouter/router.ts ->
        // Step 6.2b bimodal in/out de-collision. A diamond side collapses
        // to a narrow pin, so move an outgoing edge to a free secondary
        // side when an incoming edge already occupies its primary side.
        val incomingSidesByNode = mutableMapOf<String, MutableSet<Side>>()
        sideInfoByIndex.values.forEach { info ->
            incomingSidesByNode
                .getOrPut(info.targetId, ::mutableSetOf)
                .add(info.target)
        }
        sideInfoByIndex.values.forEach { info ->
            if (info.sourceId !in diamondNodeIds) return@forEach
            val incomingSides = incomingSidesByNode[info.sourceId] ?: return@forEach
            if (info.source !in incomingSides) return@forEach
            val secondary = info.secondarySide()
            val secondaryKey = info.sourceId to secondary
            if (secondary in incomingSides || (sideLoad[secondaryKey] ?: 0) > 0) {
                return@forEach
            }
            val primaryKey = info.sourceId to info.source
            sideLoad[primaryKey] = max(0, (sideLoad[primaryKey] ?: 0) - 1)
            sideLoad[secondaryKey] = 1
            info.source = secondary
        }
        sideInfoByIndex.forEach { (index, info) ->
            result[index] = EndpointSides(
                source = info.source,
                target = info.target,
            )
        }
        return result
    }

    private fun assignPortOffsets(
        edges: List<FlowEdge>,
        graph: WorkGraph,
        sides: Map<Int, EndpointSides>,
    ): Map<Pair<Int, EndpointRole>, Float> {
        val groups = mutableMapOf<PortGroup, MutableList<PortMember>>()
        edges.forEachIndexed { index, edge ->
            val endpointSides = sides[index] ?: return@forEachIndexed
            val source = graph.nodes[edge.from] ?: return@forEachIndexed
            val target = graph.nodes[edge.to] ?: return@forEachIndexed
            groups.getOrPut(PortGroup(edge.from, endpointSides.source, EndpointRole.Source)) {
                mutableListOf()
            } += PortMember(index, endpointSides.source.oppositeCoordinate(target.center()))
            groups.getOrPut(PortGroup(edge.to, endpointSides.target, EndpointRole.Target)) {
                mutableListOf()
            } += PortMember(index, endpointSides.target.oppositeCoordinate(source.center()))
        }
        val result = mutableMapOf<Pair<Int, EndpointRole>, Float>()
        groups.forEach { (group, members) ->
            if (members.size < 2) return@forEach
            members.sortBy(PortMember::oppositeCoordinate)
            val node = graph.nodes.getValue(group.nodeId)
            val sideLength = if (group.side.isVerticalFace) node.height else node.width
            val spacing = min(20f, max(8f, sideLength / (members.size + 1f)))
            val start = -spacing * (members.size - 1) / 2f
            members.forEachIndexed { memberIndex, member ->
                result[member.edgeIndex to group.role] = start + memberIndex * spacing
            }
        }
        return result
    }

    private fun chooseSide(
        node: WorkNode,
        target: ScenePoint,
        fallback: Side,
    ): Side {
        val dx = target.x - node.x
        val dy = target.y - node.y
        if (abs(dx) < Epsilon && abs(dy) < Epsilon) return fallback
        if (abs(dy) > Epsilon && abs(dy) * 3f >= abs(dx)) {
            return if (dy > 0f) Side.Bottom else Side.Top
        }
        if (abs(dx) > Epsilon) return if (dx > 0f) Side.Right else Side.Left
        return fallback
    }

    private fun port(node: WorkNode, side: Side, offset: Float): ScenePoint = when (side) {
        Side.Top -> ScenePoint(node.x + offset, node.y - node.height / 2f)
        Side.Bottom -> ScenePoint(node.x + offset, node.y + node.height / 2f)
        Side.Left -> ScenePoint(node.x - node.width / 2f, node.y + offset)
        Side.Right -> ScenePoint(node.x + node.width / 2f, node.y + offset)
    }

    private fun orthogonalPath(
        source: ScenePoint,
        target: ScenePoint,
        sourceId: String,
        targetId: String,
        obstacles: List<Obstacle>,
        existing: List<ScenePoint>,
    ): List<ScenePoint> {
        if (abs(source.x - target.x) < Epsilon || abs(source.y - target.y) < Epsilon) {
            if (!blocked(source, target, sourceId, targetId, obstacles)) {
                return listOf(source, target)
            }
        }
        val horizontalCorner = ScenePoint(target.x, source.y)
        val verticalCorner = ScenePoint(source.x, target.y)
        val horizontalClear =
            !blocked(source, horizontalCorner, sourceId, targetId, obstacles) &&
                !blocked(horizontalCorner, target, sourceId, targetId, obstacles)
        val verticalClear =
            !blocked(source, verticalCorner, sourceId, targetId, obstacles) &&
                !blocked(verticalCorner, target, sourceId, targetId, obstacles)
        if (horizontalClear || verticalClear) {
            val horizontalScore = if (horizontalClear) {
                crossingPenalty(listOf(source, horizontalCorner, target), existing)
            } else {
                Int.MAX_VALUE
            }
            val verticalScore = if (verticalClear) {
                crossingPenalty(listOf(source, verticalCorner, target), existing)
            } else {
                Int.MAX_VALUE
            }
            return if (horizontalScore <= verticalScore) {
                listOf(source, horizontalCorner, target)
            } else {
                listOf(source, verticalCorner, target)
            }
        }

        val xs = mutableSetOf(source.x, target.x)
        val ys = mutableSetOf(source.y, target.y)
        obstacles.forEach { obstacle ->
            xs += obstacle.bounds.left - 15f
            xs += obstacle.bounds.right + 15f
            ys += obstacle.bounds.top - 15f
            ys += obstacle.bounds.bottom + 15f
        }
        return gridRoute(
            source = source,
            target = target,
            sourceId = sourceId,
            targetId = targetId,
            obstacles = obstacles,
            xs = xs.sorted(),
            ys = ys.sorted(),
        ) ?: listOf(source, verticalCorner, target)
    }

    private fun gridRoute(
        source: ScenePoint,
        target: ScenePoint,
        sourceId: String,
        targetId: String,
        obstacles: List<Obstacle>,
        xs: List<Float>,
        ys: List<Float>,
    ): List<ScenePoint>? {
        val points = buildList {
            xs.forEach { x -> ys.forEach { y -> add(ScenePoint(x, y)) } }
        }.filter { point ->
            point == source || point == target || obstacles.none { obstacle ->
                obstacle.nodeId !in setOf(sourceId, targetId) &&
                    obstacle.bounds.containsStrictly(point)
            }
        }
        val byX = points.groupBy(ScenePoint::x).mapValues { (_, value) -> value.sortedBy(ScenePoint::y) }
        val byY = points.groupBy(ScenePoint::y).mapValues { (_, value) -> value.sortedBy(ScenePoint::x) }
        val distance = mutableMapOf(source to 0f)
        val previous = mutableMapOf<ScenePoint, ScenePoint>()
        val pending = points.toMutableSet()
        while (pending.isNotEmpty()) {
            val current = pending.minByOrNull { distance[it] ?: Float.POSITIVE_INFINITY }
                ?: return null
            val currentDistance = distance[current] ?: return null
            pending.remove(current)
            if (current == target) {
                val result = mutableListOf(target)
                var cursor = target
                while (cursor != source) {
                    cursor = previous[cursor] ?: return null
                    result += cursor
                }
                return result.asReversed()
            }
            val horizontal = byY[current.y].orEmpty()
            val vertical = byX[current.x].orEmpty()
            val neighbors = buildList {
                val horizontalIndex = horizontal.indexOf(current)
                if (horizontalIndex > 0) add(horizontal[horizontalIndex - 1])
                if (horizontalIndex in 0 until horizontal.lastIndex) add(horizontal[horizontalIndex + 1])
                val verticalIndex = vertical.indexOf(current)
                if (verticalIndex > 0) add(vertical[verticalIndex - 1])
                if (verticalIndex in 0 until vertical.lastIndex) add(vertical[verticalIndex + 1])
            }
            neighbors.filter { it in pending }.forEach { next ->
                if (blocked(current, next, sourceId, targetId, obstacles)) return@forEach
                val candidate = currentDistance +
                    abs(next.x - current.x) + abs(next.y - current.y)
                if (candidate < (distance[next] ?: Float.POSITIVE_INFINITY)) {
                    distance[next] = candidate
                    previous[next] = current
                }
            }
        }
        return null
    }

    private fun blocked(
        first: ScenePoint,
        second: ScenePoint,
        sourceId: String,
        targetId: String,
        obstacles: List<Obstacle>,
    ): Boolean {
        val minX = min(first.x, second.x)
        val maxX = max(first.x, second.x)
        val minY = min(first.y, second.y)
        val maxY = max(first.y, second.y)
        return obstacles.any { obstacle ->
            obstacle.nodeId != sourceId &&
                obstacle.nodeId != targetId &&
                if (abs(first.y - second.y) < Epsilon) {
                    obstacle.bounds.top < first.y &&
                        obstacle.bounds.bottom > first.y &&
                        obstacle.bounds.right > minX &&
                        obstacle.bounds.left < maxX
                } else {
                    obstacle.bounds.left < first.x &&
                        obstacle.bounds.right > first.x &&
                        obstacle.bounds.bottom > minY &&
                        obstacle.bounds.top < maxY
                }
        }
    }

    private fun crossingPenalty(
        candidate: List<ScenePoint>,
        existing: List<ScenePoint>,
    ): Int {
        if (existing.size < 2) return 0
        var crossings = 0
        candidate.zipWithNext().forEach { (first, second) ->
            existing.zipWithNext().forEach { (otherFirst, otherSecond) ->
                if (segmentsCross(first, second, otherFirst, otherSecond)) crossings += 1
            }
        }
        return crossings
    }

    private fun selfLoop(node: WorkNode): MutableList<ScenePoint> {
        val right = node.x + node.width / 2f
        val span = max(36f, min(100f, node.height * 0.8f))
        val depth = max(24f, min(min(node.width, node.height) * 0.45f, 48f))
        return mutableListOf(
            ScenePoint(right, node.y - span / 2f),
            ScenePoint(right + depth, node.y - span / 2f),
            ScenePoint(right + depth, node.y + span / 2f),
            ScenePoint(right, node.y + span / 2f),
        )
    }

    // Mermaid.js 12.0.0: swimlanes/direction/lrTransform.ts.
    private fun applyDirection(
        graph: WorkGraph,
        routes: MutableMap<Int, MutableRoute>,
        direction: FlowDirection,
    ) {
        when (direction) {
            FlowDirection.TopToBottom -> Unit
            FlowDirection.BottomToTop -> mirror(graph, routes, horizontal = false)
            FlowDirection.LeftToRight,
            FlowDirection.RightToLeft,
            -> {
                val content = graph.nodes.values.filterNot { it.isGroup || it.isDummy }
                val minX = content.minOf(WorkNode::x)
                val minY = content.minOf(WorkNode::y)
                val averageWidth = content.map(WorkNode::width).average().toFloat()
                val averageHeight = content.map(WorkNode::height).average().toFloat()
                val scale = if (averageHeight > 0f) max(1f, averageWidth / averageHeight) else 1f
                graph.nodes.values.filterNot(WorkNode::isGroup).forEach { node ->
                    val oldX = node.x
                    node.x = (node.y - minY) * scale + 36f
                    node.y = oldX - minX
                }
                routes.values.forEach { route ->
                    route.points.indices.forEach { index ->
                        val point = route.points[index]
                        route.points[index] = ScenePoint(
                            x = (point.y - minY) * scale + 36f,
                            y = point.x - minX,
                        )
                    }
                }
                recomputeHorizontalGroups(graph)
                if (direction == FlowDirection.RightToLeft) {
                    mirror(graph, routes, horizontal = true)
                }
            }
        }
    }

    private fun recomputeHorizontalGroups(graph: WorkGraph) {
        val groups = graph.nodes.values.filter(WorkNode::isGroup)
        val depth = groups.associate { group ->
            group.id to generateSequence(group.parentId) { graph.nodes[it]?.parentId }.count()
        }
        groups.filter { it.parentId != null }
            .sortedByDescending { depth[it.id] ?: 0 }
            .forEach { group ->
                boundsOf(graph.nodes.values.filter { it.parentId == group.id })
                    ?.expand(group.padding / 2f, group.padding / 2f)
                    ?.let(group::setBounds)
            }
        val lanes = groups.filter { it.parentId == null }
        if (lanes.isEmpty()) return
        val contentByLane = lanes.mapNotNull { lane ->
            val descendants = graph.nodes.values.filter { node ->
                !node.isGroup && topLaneOf(node.id, graph) == lane.id
            }
            boundsOf(descendants)?.let { lane to it }
        }.sortedBy { it.second.center.y }
        if (contentByLane.isEmpty()) return
        val globalLeft = contentByLane.minOf { it.second.left }
        val globalRight = contentByLane.maxOf { it.second.right }
        val maxPadding = lanes.maxOfOrNull(WorkNode::padding) ?: 0f
        val bodyWidth = globalRight - globalLeft + 2f * max(maxPadding, 10f)
        val laneWidth = 36f + bodyWidth
        val bodyCenter = (globalLeft + globalRight) / 2f
        val laneCenterX = bodyCenter - bodyWidth / 2f - 36f + laneWidth / 2f
        val verticalMargin = max(maxPadding, 36f)
        contentByLane.forEachIndexed { index, (lane, bounds) ->
            val top = if (index == 0) {
                bounds.top - verticalMargin
            } else {
                (contentByLane[index - 1].second.bottom + bounds.top) / 2f
            }
            val bottom = if (index == contentByLane.lastIndex) {
                bounds.bottom + verticalMargin
            } else {
                (bounds.bottom + contentByLane[index + 1].second.top) / 2f
            }
            lane.x = laneCenterX
            lane.y = (top + bottom) / 2f
            lane.width = laneWidth
            lane.height = max(0f, bottom - top)
            lane.contentTop = bounds.top
        }
    }

    private fun mirror(
        graph: WorkGraph,
        routes: MutableMap<Int, MutableRoute>,
        horizontal: Boolean,
    ) {
        val content = graph.nodes.values.filterNot { it.isGroup || it.isDummy }
        val minimum = content.minOf { if (horizontal) it.x else it.y }
        val maximum = content.maxOf { if (horizontal) it.x else it.y }
        fun mirrored(value: Float): Float = minimum + maximum - value
        graph.nodes.values.forEach { node ->
            if (horizontal) node.x = mirrored(node.x) else node.y = mirrored(node.y)
        }
        routes.values.forEach { route ->
            route.points.indices.forEach { index ->
                val point = route.points[index]
                route.points[index] = if (horizontal) {
                    ScenePoint(mirrored(point.x), point.y)
                } else {
                    ScenePoint(point.x, mirrored(point.y))
                }
            }
        }
    }

    // Mermaid.js 12.0.0: swimlanes/direction/portSwap.ts ->
    // portSwapToLShape.
    private fun portSwapToLShape(
        graph: WorkGraph,
        routes: MutableMap<Int, MutableRoute>,
    ) {
        val realNodes = graph.nodes.values.filter { node ->
            !node.isGroup && !node.isDummy
        }
        routes.forEach { (routeIndex, route) ->
            val edge = route.edge ?: return@forEach
            val points = route.points.removeAdjacentDuplicates()
            val kind = points.threeSegmentRouteKind() ?: return@forEach
            val source = graph.nodes[edge.from] ?: return@forEach
            val target = graph.nodes[edge.to] ?: return@forEach
            if (abs(source.x - target.x) < Epsilon || abs(source.y - target.y) < Epsilon) {
                return@forEach
            }
            val end = points.last()
            val sourceBounds = source.bounds()
            val candidate = PortSwapDeltas.firstNotNullOfOrNull { delta ->
                val start: ScenePoint
                val corner: ScenePoint
                when (kind) {
                    ThreeSegmentRouteKind.HorizontalVerticalHorizontal -> {
                        val sourceY = if (target.y > source.y) {
                            sourceBounds.bottom
                        } else {
                            sourceBounds.top
                        }
                        val sourceX = source.x + delta
                        if (
                            sourceX <= sourceBounds.left + Epsilon ||
                            sourceX >= sourceBounds.right - Epsilon
                        ) {
                            return@firstNotNullOfOrNull null
                        }
                        start = ScenePoint(sourceX, sourceY)
                        corner = ScenePoint(sourceX, end.y)
                    }
                    ThreeSegmentRouteKind.VerticalHorizontalVertical -> {
                        val sourceX = if (target.x > source.x) {
                            sourceBounds.right
                        } else {
                            sourceBounds.left
                        }
                        val sourceY = source.y + delta
                        if (
                            sourceY <= sourceBounds.top + Epsilon ||
                            sourceY >= sourceBounds.bottom - Epsilon
                        ) {
                            return@firstNotNullOfOrNull null
                        }
                        start = ScenePoint(sourceX, sourceY)
                        corner = ScenePoint(end.x, sourceY)
                    }
                }
                val firstDegenerate = start.samePoint(corner)
                val secondDegenerate = corner.samePoint(end)
                if (firstDegenerate && secondDegenerate) {
                    return@firstNotNullOfOrNull null
                }
                if (
                    !firstDegenerate &&
                    segmentHitsAnyNode(start, corner, realNodes, setOf(source.id))
                ) {
                    return@firstNotNullOfOrNull null
                }
                if (
                    !secondDegenerate &&
                    segmentHitsAnyNode(corner, end, realNodes, setOf(target.id))
                ) {
                    return@firstNotNullOfOrNull null
                }
                if (
                    !firstDegenerate &&
                    segmentConflictsWithRoutes(start, corner, routes, routeIndex)
                ) {
                    return@firstNotNullOfOrNull null
                }
                if (
                    !secondDegenerate &&
                    segmentConflictsWithRoutes(corner, end, routes, routeIndex)
                ) {
                    return@firstNotNullOfOrNull null
                }
                listOf(start, corner, end)
                    .removeAdjacentDuplicates()
                    .toMutableList()
            } ?: return@forEach
            route.points = candidate
        }
    }

    private fun List<ScenePoint>.threeSegmentRouteKind(): ThreeSegmentRouteKind? {
        if (size != 4) return null
        val firstHorizontal = this[0].isHorizontalTo(this[1])
        val firstVertical = this[0].isVerticalTo(this[1])
        val secondHorizontal = this[1].isHorizontalTo(this[2])
        val secondVertical = this[1].isVerticalTo(this[2])
        val thirdHorizontal = this[2].isHorizontalTo(this[3])
        val thirdVertical = this[2].isVerticalTo(this[3])
        return when {
            firstHorizontal && secondVertical && thirdHorizontal ->
                ThreeSegmentRouteKind.HorizontalVerticalHorizontal
            firstVertical && secondHorizontal && thirdVertical ->
                ThreeSegmentRouteKind.VerticalHorizontalVertical
            else -> null
        }
    }

    private fun segmentHitsAnyNode(
        first: ScenePoint,
        second: ScenePoint,
        nodes: List<WorkNode>,
        excludedNodeIds: Set<String>,
        shrink: Float = 1f,
    ): Boolean = nodes.any { node ->
        node.id !in excludedNodeIds &&
            segmentBoundsOverlapRect(first, second, node.bounds(), shrink)
    }

    private fun segmentBoundsOverlapRect(
        first: ScenePoint,
        second: ScenePoint,
        bounds: SceneRect,
        shrink: Float,
    ): Boolean {
        val minimumX = min(first.x, second.x)
        val maximumX = max(first.x, second.x)
        val minimumY = min(first.y, second.y)
        val maximumY = max(first.y, second.y)
        return maximumX > bounds.left + shrink &&
            minimumX < bounds.right - shrink &&
            maximumY > bounds.top + shrink &&
            minimumY < bounds.bottom - shrink
    }

    private fun segmentConflictsWithRoutes(
        first: ScenePoint,
        second: ScenePoint,
        routes: Map<Int, MutableRoute>,
        excludedRouteIndex: Int,
    ): Boolean = routes.any { (routeIndex, route) ->
        routeIndex != excludedRouteIndex &&
            route.points.removeAdjacentDuplicates().zipWithNext().any { (otherFirst, otherSecond) ->
                orthogonalSegmentsCross(
                    first,
                    second,
                    otherFirst,
                    otherSecond,
                ) || sameAxisSegmentsOverlap(
                    first,
                    second,
                    otherFirst,
                    otherSecond,
                )
            }
    }

    private fun orthogonalSegmentsCross(
        first: ScenePoint,
        second: ScenePoint,
        otherFirst: ScenePoint,
        otherSecond: ScenePoint,
    ): Boolean {
        val firstHorizontal = first.isHorizontalTo(second)
        val firstVertical = first.isVerticalTo(second)
        val otherHorizontal = otherFirst.isHorizontalTo(otherSecond)
        val otherVertical = otherFirst.isVerticalTo(otherSecond)
        if (
            !(
                (firstHorizontal && otherVertical) ||
                    (firstVertical && otherHorizontal)
                )
        ) {
            return false
        }
        val horizontalFirst = if (firstHorizontal) first else otherFirst
        val horizontalSecond = if (firstHorizontal) second else otherSecond
        val verticalFirst = if (firstHorizontal) otherFirst else first
        val verticalSecond = if (firstHorizontal) otherSecond else second
        val intersection = ScenePoint(verticalFirst.x, horizontalFirst.y)
        if (
            intersection.x < min(horizontalFirst.x, horizontalSecond.x) ||
            intersection.x > max(horizontalFirst.x, horizontalSecond.x) ||
            intersection.y < min(verticalFirst.y, verticalSecond.y) ||
            intersection.y > max(verticalFirst.y, verticalSecond.y)
        ) {
            return false
        }
        val matchesHorizontalEndpoint =
            intersection.samePoint(horizontalFirst) || intersection.samePoint(horizontalSecond)
        val matchesVerticalEndpoint =
            intersection.samePoint(verticalFirst) || intersection.samePoint(verticalSecond)
        return !(matchesHorizontalEndpoint && matchesVerticalEndpoint)
    }

    private fun sameAxisSegmentsOverlap(
        first: ScenePoint,
        second: ScenePoint,
        otherFirst: ScenePoint,
        otherSecond: ScenePoint,
    ): Boolean = when {
        first.isVerticalTo(second) &&
            otherFirst.isVerticalTo(otherSecond) &&
            abs(first.x - otherFirst.x) < Epsilon ->
            overlapLength(first.y, second.y, otherFirst.y, otherSecond.y) > Epsilon
        first.isHorizontalTo(second) &&
            otherFirst.isHorizontalTo(otherSecond) &&
            abs(first.y - otherFirst.y) < Epsilon ->
            overlapLength(first.x, second.x, otherFirst.x, otherSecond.x) > Epsilon
        else -> false
    }

    private fun ScenePoint.samePoint(other: ScenePoint): Boolean =
        abs(x - other.x) < Epsilon && abs(y - other.y) < Epsilon

    private fun ScenePoint.isHorizontalTo(other: ScenePoint): Boolean =
        abs(y - other.y) < Epsilon && abs(x - other.x) > Epsilon

    private fun ScenePoint.isVerticalTo(other: ScenePoint): Boolean =
        abs(x - other.x) < Epsilon && abs(y - other.y) > Epsilon

    // Mermaid.js 12.0.0: swimlanes/direction/endpointClip.ts and geometry.ts.
    private fun clipAndSimplifyEdges(
        graph: WorkGraph,
        routes: MutableMap<Int, MutableRoute>,
        shapeLayouts: Map<String, MermaidShapeLayout>,
    ) {
        routes.forEach { (_, route) ->
            if (route.points.size < 2) return@forEach
            val edge = route.edge ?: return@forEach
            val source = graph.nodes[edge.from] ?: return@forEach
            val target = graph.nodes[edge.to] ?: return@forEach
            route.points[0] = clipEndpoint(
                shapeLayouts[edge.from],
                source.bounds(),
                route.points[0],
                route.points[1],
            )
            route.points[route.points.lastIndex] = clipEndpoint(
                shapeLayouts[edge.to],
                target.bounds(),
                route.points.last(),
                route.points[route.points.lastIndex - 1],
            )
            route.points = route.points.orthogonalized().simplified().toMutableList()
        }
    }

    private fun clipEndpoint(
        shapeLayout: MermaidShapeLayout?,
        bounds: SceneRect,
        endpoint: ScenePoint,
        toward: ScenePoint,
    ): ScenePoint = if (pointIsOnOutline(shapeLayout, bounds, endpoint)) {
        endpoint
    } else {
        intersect(shapeLayout, bounds, toward)
    }

    private fun pointIsOnOutline(
        shapeLayout: MermaidShapeLayout?,
        bounds: SceneRect,
        point: ScenePoint,
    ): Boolean {
        val center = bounds.center
        val outline = shapeLayout?.geometry?.outline?.map { outlinePoint ->
            ScenePoint(center.x + outlinePoint.x, center.y + outlinePoint.y)
        } ?: listOf(
            ScenePoint(bounds.left, bounds.top),
            ScenePoint(bounds.right, bounds.top),
            ScenePoint(bounds.right, bounds.bottom),
            ScenePoint(bounds.left, bounds.bottom),
        )
        return (outline + outline.first()).zipWithNext().any { (first, second) ->
            val segmentX = second.x - first.x
            val segmentY = second.y - first.y
            val offsetX = point.x - first.x
            val offsetY = point.y - first.y
            val scale = max(1f, abs(segmentX) + abs(segmentY))
            abs(offsetX * segmentY - offsetY * segmentX) <=
                MaterializedGeometryEpsilon * scale &&
                point.x >= min(first.x, second.x) - MaterializedGeometryEpsilon &&
                point.x <= max(first.x, second.x) + MaterializedGeometryEpsilon &&
                point.y >= min(first.y, second.y) - MaterializedGeometryEpsilon &&
                point.y <= max(first.y, second.y) + MaterializedGeometryEpsilon
        }
    }

    // Mermaid.js 12.0.0:
    // swimlanes/direction/materializedGeometry.ts ->
    // resolveRenderedOrthogonalCrossings, single-edge cardinal base candidates.
    // External-channel and paired replacement branches remain untranslated.
    private fun resolveRenderedOrthogonalCrossings(
        graph: WorkGraph,
        routes: MutableMap<Int, MutableRoute>,
    ) {
        val visibleRoutes = routes.entries
            .filter { (_, route) -> route.edge != null }
            .sortedBy { entry -> entry.key }
        val realNodes = graph.nodes.values.filter { node ->
            !node.isGroup && !node.isDummy
        }

        fun pointsFor(
            routeIndex: Int,
            replacements: Map<Int, List<ScenePoint>> = emptyMap(),
        ): List<ScenePoint> = (
            replacements[routeIndex] ?: routes.getValue(routeIndex).points
            ).removeAdjacentDuplicates()

        fun crossingSnapshot(
            replacements: Map<Int, List<ScenePoint>> = emptyMap(),
        ): CrossingSnapshot {
            var count = 0
            val crossingRouteIndices = linkedSetOf<Int>()
            visibleRoutes.indices.forEach { firstIndex ->
                val first = visibleRoutes[firstIndex]
                val firstPoints = pointsFor(first.key, replacements)
                for (secondIndex in firstIndex + 1 until visibleRoutes.size) {
                    val second = visibleRoutes[secondIndex]
                    val secondPoints = pointsFor(second.key, replacements)
                    firstPoints.zipWithNext().forEach { (firstStart, firstEnd) ->
                        secondPoints.zipWithNext().forEach { (secondStart, secondEnd) ->
                            if (
                                orthogonalSegmentsStrictlyCross(
                                    firstStart,
                                    firstEnd,
                                    secondStart,
                                    secondEnd,
                                )
                            ) {
                                count += 1
                                crossingRouteIndices += first.key
                                crossingRouteIndices += second.key
                            }
                        }
                    }
                }
            }
            return CrossingSnapshot(count, crossingRouteIndices)
        }

        fun buildOrthogonalPortPath(
            source: ScenePoint,
            sourceSide: Side,
            target: ScenePoint,
            targetSide: Side,
        ): List<ScenePoint>? {
            val sourceHorizontal = sourceSide.isVerticalFace
            val targetHorizontal = targetSide.isVerticalFace
            if (sourceHorizontal && targetHorizontal) {
                val opposingDirection =
                    (
                        sourceSide == Side.Right &&
                            targetSide == Side.Left &&
                            source.x < target.x
                        ) ||
                        (
                            sourceSide == Side.Left &&
                                targetSide == Side.Right &&
                                source.x > target.x
                            )
                if (opposingDirection) {
                    if (abs(source.y - target.y) < MaterializedGeometryEpsilon) {
                        return listOf(source, target)
                    }
                    val middleX = (source.x + target.x) / 2f
                    return listOf(
                        source,
                        ScenePoint(middleX, source.y),
                        ScenePoint(middleX, target.y),
                        target,
                    )
                }
                if (sourceSide == targetSide) {
                    if (abs(source.y - target.y) < MaterializedGeometryEpsilon) {
                        return null
                    }
                    val trackX = if (sourceSide == Side.Left) {
                        min(source.x, target.x) - CrossingResolutionAnchor
                    } else {
                        max(source.x, target.x) + CrossingResolutionAnchor
                    }
                    return listOf(
                        source,
                        ScenePoint(trackX, source.y),
                        ScenePoint(trackX, target.y),
                        target,
                    )
                }
                return null
            }

            if (!sourceHorizontal && !targetHorizontal) {
                if (sourceSide == targetSide) {
                    if (abs(source.x - target.x) < MaterializedGeometryEpsilon) {
                        return null
                    }
                    val trackY = if (sourceSide == Side.Top) {
                        min(source.y, target.y) - CrossingResolutionAnchor
                    } else {
                        max(source.y, target.y) + CrossingResolutionAnchor
                    }
                    return listOf(
                        source,
                        ScenePoint(source.x, trackY),
                        ScenePoint(target.x, trackY),
                        target,
                    )
                }
                val sameDirection =
                    (
                        sourceSide == Side.Bottom &&
                            targetSide == Side.Top &&
                            source.y < target.y
                        ) ||
                        (
                            sourceSide == Side.Top &&
                                targetSide == Side.Bottom &&
                                source.y > target.y
                            )
                if (!sameDirection) return null
                if (abs(source.x - target.x) < MaterializedGeometryEpsilon) {
                    return listOf(source, target)
                }
                val middleY = (source.y + target.y) / 2f
                return listOf(
                    source,
                    ScenePoint(source.x, middleY),
                    ScenePoint(target.x, middleY),
                    target,
                )
            }

            if (sourceHorizontal) {
                val sourceFacesTarget =
                    (sourceSide == Side.Right && target.x > source.x) ||
                        (sourceSide == Side.Left && target.x < source.x)
                val targetFacesSource =
                    (targetSide == Side.Top && source.y < target.y) ||
                        (targetSide == Side.Bottom && source.y > target.y)
                return if (sourceFacesTarget && targetFacesSource) {
                    listOf(source, ScenePoint(target.x, source.y), target)
                } else {
                    null
                }
            }

            val sourceFacesTarget =
                (sourceSide == Side.Bottom && target.y > source.y) ||
                    (sourceSide == Side.Top && target.y < source.y)
            val targetFacesSource =
                (targetSide == Side.Left && source.x < target.x) ||
                    (targetSide == Side.Right && source.x > target.x)
            return if (sourceFacesTarget && targetFacesSource) {
                listOf(source, ScenePoint(source.x, target.y), target)
            } else {
                null
            }
        }

        fun candidatePathsFor(route: MutableRoute): List<List<ScenePoint>> {
            val edge = route.edge ?: return emptyList()
            val source = graph.nodes[edge.from] ?: return emptyList()
            val target = graph.nodes[edge.to] ?: return emptyList()
            val candidates = mutableListOf<List<ScenePoint>>()
            Side.values().forEach { sourceSide ->
                val sourcePort = port(source, sourceSide, 0f)
                Side.values().forEach targetSideLoop@ { targetSide ->
                    val targetPort = port(target, targetSide, 0f)
                    val candidate = buildOrthogonalPortPath(
                        sourcePort,
                        sourceSide,
                        targetPort,
                        targetSide,
                    ) ?: return@targetSideLoop
                    val simplified = candidate.simplified()
                    if (
                        simplified.size >= 2 &&
                        simplified.zipWithNext().all { (first, second) ->
                            first.isHorizontalTo(second) || first.isVerticalTo(second)
                        } &&
                        simplified !in candidates
                    ) {
                        candidates += simplified
                    }
                }
            }
            return candidates
        }

        fun pathHasNodeHit(
            route: MutableRoute,
            path: List<ScenePoint>,
        ): Boolean {
            val edge = route.edge ?: return true
            val endpointIds = setOf(edge.from, edge.to)
            return path.zipWithNext().any { (first, second) ->
                segmentHitsAnyNode(
                    first = first,
                    second = second,
                    nodes = realNodes,
                    excludedNodeIds = endpointIds,
                    shrink = -CrossingResolutionNodeBuffer,
                )
            }
        }

        fun pathHasSharedTrack(
            routeIndex: Int,
            path: List<ScenePoint>,
        ): Boolean = visibleRoutes.any { other ->
            other.key != routeIndex &&
                path.zipWithNext().any { (first, second) ->
                    pointsFor(other.key).zipWithNext().any {
                            (otherFirst, otherSecond) ->
                        sameAxisSegmentOverlapLength(
                            first,
                            second,
                            otherFirst,
                            otherSecond,
                        ) >= MinimumSharedTrackSpan
                    }
                }
        }

        repeat(CrossingResolutionMaximumIterations) {
            val current = crossingSnapshot()
            if (current.count == 0) return
            var bestRouteIndex: Int? = null
            var bestPath: List<ScenePoint>? = null
            var bestCrossings = current.count
            var bestBends = Int.MAX_VALUE

            current.routeIndices.forEach { routeIndex ->
                val route = routes.getValue(routeIndex)
                val currentEdgeBends = countOrthogonalBends(pointsFor(routeIndex))
                candidatePathsFor(route).forEach candidateLoop@ { candidate ->
                    if (
                        pathHasNodeHit(route, candidate) ||
                        pathHasSharedTrack(routeIndex, candidate)
                    ) {
                        return@candidateLoop
                    }
                    val candidateCrossings = crossingSnapshot(
                        mapOf(routeIndex to candidate),
                    ).count
                    val candidateBends = countOrthogonalBends(candidate)
                    val improvesCurrentEdge =
                        candidateCrossings < current.count ||
                            (
                                candidateCrossings == current.count &&
                                    candidateBends < currentEdgeBends
                                )
                    if (!improvesCurrentEdge) return@candidateLoop
                    if (
                        candidateCrossings > bestCrossings ||
                        (
                            candidateCrossings == bestCrossings &&
                                candidateBends >= bestBends
                            )
                    ) {
                        return@candidateLoop
                    }
                    bestRouteIndex = routeIndex
                    bestPath = candidate
                    bestCrossings = candidateCrossings
                    bestBends = candidateBends
                }
            }

            val routeIndex = bestRouteIndex ?: return
            routes.getValue(routeIndex).points =
                bestPath?.toMutableList() ?: return
        }
    }

    private fun countOrthogonalBends(points: List<ScenePoint>): Int {
        val horizontalSegments = points.zipWithNext().mapNotNull { (first, second) ->
            when {
                first.isHorizontalTo(second) -> true
                first.isVerticalTo(second) -> false
                else -> null
            }
        }
        return horizontalSegments.zipWithNext().count { (first, second) ->
            first != second
        }
    }

    private fun orthogonalSegmentsStrictlyCross(
        first: ScenePoint,
        second: ScenePoint,
        otherFirst: ScenePoint,
        otherSecond: ScenePoint,
    ): Boolean {
        val firstHorizontal = first.isHorizontalTo(second)
        val firstVertical = first.isVerticalTo(second)
        val otherHorizontal = otherFirst.isHorizontalTo(otherSecond)
        val otherVertical = otherFirst.isVerticalTo(otherSecond)
        if (
            !(
                (firstHorizontal && otherVertical) ||
                    (firstVertical && otherHorizontal)
                )
        ) {
            return false
        }
        val horizontalFirst = if (firstHorizontal) first else otherFirst
        val horizontalSecond = if (firstHorizontal) second else otherSecond
        val verticalFirst = if (firstHorizontal) otherFirst else first
        val verticalSecond = if (firstHorizontal) otherSecond else second
        return verticalFirst.x >
            min(horizontalFirst.x, horizontalSecond.x) + MaterializedGeometryEpsilon &&
            verticalFirst.x <
            max(horizontalFirst.x, horizontalSecond.x) - MaterializedGeometryEpsilon &&
            horizontalFirst.y >
            min(verticalFirst.y, verticalSecond.y) + MaterializedGeometryEpsilon &&
            horizontalFirst.y <
            max(verticalFirst.y, verticalSecond.y) - MaterializedGeometryEpsilon
    }

    private fun sameAxisSegmentOverlapLength(
        first: ScenePoint,
        second: ScenePoint,
        otherFirst: ScenePoint,
        otherSecond: ScenePoint,
    ): Float = when {
        first.isHorizontalTo(second) &&
            otherFirst.isHorizontalTo(otherSecond) &&
            abs(first.y - otherFirst.y) < SharedTrackAxisEpsilon ->
            overlapLength(first.x, second.x, otherFirst.x, otherSecond.x)
        first.isVerticalTo(second) &&
            otherFirst.isVerticalTo(otherSecond) &&
            abs(first.x - otherFirst.x) < SharedTrackAxisEpsilon ->
            overlapLength(first.y, second.y, otherFirst.y, otherSecond.y)
        else -> 0f
    }

    // Mermaid.js 12.0.0:
    // swimlanes/direction/materializedGeometry.ts ->
    // liftTopLaneTitleBandsAboveRails.
    private fun adjustLaneTitleBands(
        graph: WorkGraph,
        routes: Map<Int, MutableRoute>,
        direction: FlowDirection,
    ) {
        if (direction != FlowDirection.TopToBottom) return
        repeat(2) {
            liftTopLaneTitleBandsAboveRails(graph, routes)
        }
    }

    private fun liftTopLaneTitleBandsAboveRails(
        graph: WorkGraph,
        routes: Map<Int, MutableRoute>,
    ) {
        val lanes = graph.nodes.values.mapNotNull { node ->
            if (!node.isGroup || node.parentId != null) return@mapNotNull null
            val contentTop = node.contentTop ?: return@mapNotNull null
            val bounds = node.bounds()
            val headerBottom = min(contentTop, bounds.bottom)
            val titleHeight = min(
                TopLaneTitleBandHeight,
                max(0f, headerBottom - bounds.top),
            )
            if (titleHeight <= 0f) return@mapNotNull null
            node to SceneRect(
                left = bounds.left,
                top = bounds.top,
                right = bounds.right,
                bottom = bounds.top + titleHeight,
            )
        }
        if (lanes.isEmpty()) return

        var topDelta = 0f
        routes.values.forEach { route ->
            route.points.removeAdjacentDuplicates().zipWithNext { first, second ->
                if (abs(first.y - second.y) > MaterializedGeometryEpsilon) {
                    return@zipWithNext
                }
                lanes.forEach { (_, title) ->
                    val y = first.y
                    if (
                        y > title.top + MaterializedGeometryEpsilon &&
                        y < title.bottom - MaterializedGeometryEpsilon &&
                        overlapLength(first.x, second.x, title.left, title.right) >=
                        MinimumSharedTitleSpan
                    ) {
                        topDelta = max(topDelta, title.bottom - y + TitleRailClearance)
                    }
                }
            }
        }
        if (topDelta <= MaterializedGeometryEpsilon) return

        lanes.forEach { (lane, _) ->
            lane.y -= topDelta / 2f
            lane.height += topDelta
        }
    }

    private fun overlapLength(
        firstStart: Float,
        firstEnd: Float,
        secondStart: Float,
        secondEnd: Float,
    ): Float = max(
        0f,
        min(max(firstStart, firstEnd), max(secondStart, secondEnd)) -
            max(min(firstStart, firstEnd), min(secondStart, secondEnd)),
    )

    // Mermaid.js 12.0.0: swimlanes/direction/labelAnchoring.ts ->
    // anchorLabelsToPolyline.
    private fun anchorEdgeLabels(
        graph: WorkGraph,
        routes: Map<Int, MutableRoute>,
        edgeLabelSizes: Map<Int, SceneSize>,
        direction: FlowDirection,
    ): Map<Int, ScenePoint> {
        val edgeSegments = routes.flatMap { (edgeIndex, route) ->
            route.points.zipWithNext { first, second ->
                LabelSegmentRef(edgeIndex, first, second)
            }
        }
        val realNodeBounds = graph.nodes.values
            .filter { node -> !node.isGroup && !node.isDummy }
            .map { node -> node.bounds() }
        val laneBounds = graph.nodes.values
            .filter { node -> node.isGroup && node.parentId == null }
            .map { node -> node.id to node.bounds() }
        // Mermaid.js runs label anchoring after its complete materialized-geometry
        // pipeline. The Kotlin port currently translates a smaller routing subset,
        // so preserve the same final no-title-overlap invariant explicitly.
        val laneTitleBounds = graph.nodes.values
            .filter { node -> node.isGroup && node.parentId == null }
            .mapNotNull { node ->
                val bounds = node.bounds()
                when (direction) {
                    FlowDirection.TopToBottom -> SceneRect(
                        left = bounds.left,
                        top = bounds.top,
                        right = bounds.right,
                        bottom = min(bounds.bottom, bounds.top + TopLaneTitleBandHeight),
                    )
                    FlowDirection.LeftToRight -> SceneRect(
                        left = bounds.left,
                        top = bounds.top,
                        right = min(bounds.right, bounds.left + LeftLaneTitleBandWidth),
                        bottom = bounds.bottom,
                    )
                    FlowDirection.BottomToTop,
                    FlowDirection.RightToLeft,
                    -> null
                }
            }
        val placedLabelBounds = mutableMapOf<Int, SceneRect>()
        val result = mutableMapOf<Int, ScenePoint>()

        routes.keys.sorted().forEach { edgeIndex ->
            val size = edgeLabelSizes[edgeIndex] ?: return@forEach
            if (size.width <= 0f || size.height <= 0f) return@forEach
            val points = routes[edgeIndex]?.points.orEmpty().removeAdjacentDuplicates()
            if (points.size < 2) return@forEach

            val segments = points.zipWithNext().mapIndexedNotNull { index, (first, second) ->
                val dx = abs(second.x - first.x)
                val dy = abs(second.y - first.y)
                if (dx < LabelEpsilon && dy < LabelEpsilon) {
                    null
                } else if (dx >= LabelEpsilon && dy >= LabelEpsilon) {
                    null
                } else {
                    LabelSegmentCandidate(
                        index = index,
                        length = dx + dy,
                        orientation = if (dx >= LabelEpsilon) {
                            LabelOrientation.Horizontal
                        } else {
                            LabelOrientation.Vertical
                        },
                    )
                }
            }
            if (segments.isEmpty()) return@forEach

            val middleSegments = if (segments.size >= 3) {
                segments.filter { segment ->
                    segment.index > 0 && segment.index < segments.size - 1
                }
            } else {
                segments
            }
            val basePool = middleSegments.ifEmpty { segments }
            val labelLongAxis = if (size.width >= size.height) {
                LabelOrientation.Horizontal
            } else {
                LabelOrientation.Vertical
            }
            fun ranked(pool: List<LabelSegmentCandidate>): List<LabelSegmentCandidate> =
                pool.sortedWith { first, second ->
                    val firstMatches = first.orientation == labelLongAxis
                    val secondMatches = second.orientation == labelLongAxis
                    if (firstMatches != secondMatches) {
                        if (firstMatches) -1 else 1
                    } else {
                        val firstExtent = if (first.orientation == LabelOrientation.Horizontal) {
                            size.width
                        } else {
                            size.height
                        }
                        val secondExtent = if (second.orientation == LabelOrientation.Horizontal) {
                            size.width
                        } else {
                            size.height
                        }
                        val firstFits = first.length >= firstExtent + 2f
                        val secondFits = second.length >= secondExtent + 2f
                        if (firstFits != secondFits) {
                            if (firstFits) -1 else 1
                        } else {
                            second.length.compareTo(first.length)
                        }
                    }
                }

            val firstVisible = segments.first()
            val lastVisible = segments.last()

            fun anchorAt(
                segment: LabelSegmentCandidate,
                fraction: Float,
            ): ScenePoint {
                val first = points[segment.index]
                val second = points[segment.index + 1]
                return ScenePoint(
                    x = first.x + (second.x - first.x) * fraction,
                    y = first.y + (second.y - first.y) * fraction,
                )
            }

            fun placementFor(anchor: ScenePoint): LabelPlacement? {
                val centered = labelBounds(anchor, size)
                val centeredLane = laneBounds.firstOrNull { (_, bounds) ->
                    bounds.contains(centered)
                }
                if (centeredLane != null) {
                    return LabelPlacement(centeredLane.first, anchor, centered)
                }

                val containingLane = laneBounds.firstOrNull { (_, bounds) ->
                    bounds.containsInclusive(anchor)
                } ?: return null
                val bounds = containingLane.second
                val minimumX = bounds.left + size.width / 2f + LabelLaneMargin
                val maximumX = bounds.right - size.width / 2f - LabelLaneMargin
                val minimumY = bounds.top + size.height / 2f + LabelLaneMargin
                val maximumY = bounds.bottom - size.height / 2f - LabelLaneMargin
                if (minimumX > maximumX || minimumY > maximumY) return null
                val clamped = ScenePoint(
                    x = anchor.x.coerceIn(minimumX, maximumX),
                    y = anchor.y.coerceIn(minimumY, maximumY),
                )
                val clampedBounds = labelBounds(clamped, size)
                return if (clampedBounds.containsInclusive(anchor)) {
                    LabelPlacement(containingLane.first, clamped, clampedBounds)
                } else {
                    null
                }
            }

            fun clearsTerminalEndpoints(
                segment: LabelSegmentCandidate,
                anchor: ScenePoint,
            ): Boolean {
                val halfExtent = if (segment.orientation == LabelOrientation.Horizontal) {
                    size.width / 2f
                } else {
                    size.height / 2f
                }
                val requiredDistance = halfExtent + LabelEndpointClearance
                fun distance(point: ScenePoint): Float =
                    if (segment.orientation == LabelOrientation.Horizontal) {
                        abs(anchor.x - point.x)
                    } else {
                        abs(anchor.y - point.y)
                    }
                if (
                    segment == firstVisible &&
                    distance(points[segment.index]) + LabelEpsilon < requiredDistance
                ) {
                    return false
                }
                if (
                    segment == lastVisible &&
                    distance(points[segment.index + 1]) + LabelEpsilon < requiredDistance
                ) {
                    return false
                }
                return true
            }

            fun overlapsForeignGeometry(bounds: SceneRect): Boolean {
                val buffered = bounds.expand(
                    horizontal = LabelPlacementBuffer,
                    vertical = LabelPlacementBuffer,
                )
                if (realNodeBounds.any { bounds -> buffered.overlaps(bounds) }) return true
                if (laneTitleBounds.any { title -> buffered.overlaps(title) }) return true
                return edgeSegments.any { segment ->
                    segment.edgeIndex != edgeIndex &&
                        segment.intersects(buffered)
                }
            }

            fun isSafe(
                placement: LabelPlacement,
                allowForeignEdgeOverlap: Boolean = false,
            ): Boolean {
                if (labelOverlapsOwnMarker(placement.bounds, points)) return false
                if (placedLabelBounds.any { (placedIndex, bounds) ->
                        placedIndex != edgeIndex && placement.bounds.overlaps(bounds)
                    }
                ) {
                    return false
                }
                val buffered = placement.bounds.expand(
                    horizontal = LabelPlacementBuffer,
                    vertical = LabelPlacementBuffer,
                )
                if (realNodeBounds.any { bounds -> buffered.overlaps(bounds) }) return false
                if (laneTitleBounds.any { title -> buffered.overlaps(title) }) return false
                return allowForeignEdgeOverlap || edgeSegments.none { segment ->
                    segment.edgeIndex != edgeIndex &&
                        segment.intersects(buffered)
                }
            }

            fun tryPool(pool: List<LabelSegmentCandidate>): LabelPlacement? {
                ranked(pool).forEach { segment ->
                    LabelAnchorFractions.forEach { fraction ->
                        val anchor = anchorAt(segment, fraction)
                        if (!clearsTerminalEndpoints(segment, anchor)) return@forEach
                        val placement = placementFor(anchor) ?: return@forEach
                        if (
                            !labelOverlapsOwnMarker(placement.bounds, points) &&
                            placedLabelBounds.values.none { bounds ->
                                placement.bounds.overlaps(bounds)
                            } &&
                            !overlapsForeignGeometry(placement.bounds)
                        ) {
                            return placement
                        }
                    }
                }
                return null
            }

            fun fallback(
                requireEndpointClearance: Boolean,
                allowForeignEdgeOverlap: Boolean = false,
            ): LabelPlacement? {
                ranked(segments).forEach { segment ->
                    val anchor = anchorAt(segment, 0.5f)
                    if (
                        requireEndpointClearance &&
                        !clearsTerminalEndpoints(segment, anchor)
                    ) {
                        return@forEach
                    }
                    val placement = placementFor(anchor) ?: return@forEach
                    if (isSafe(placement, allowForeignEdgeOverlap)) return placement
                }
                return null
            }

            val chosen = tryPool(basePool)
                ?: (
                    if (basePool.size < segments.size) {
                        tryPool(segments)
                    } else {
                        null
                    }
                    )
                ?: fallback(requireEndpointClearance = true)
                ?: fallback(requireEndpointClearance = false)
                ?: fallback(
                    requireEndpointClearance = false,
                    allowForeignEdgeOverlap = true,
                )
            if (chosen != null) {
                result[edgeIndex] = chosen.anchor
                placedLabelBounds[edgeIndex] = chosen.bounds
            }
        }
        return result
    }

    private fun optimizeLaneOrder(
        graph: WorkGraph,
        document: FlowchartDocument,
    ): List<String> {
        val order = sourceLaneOrder(document).toMutableList()
        val positions = order.withIndex().associate { it.value to it.index }
        fun cost(candidate: List<String>): Int {
            val candidatePositions = candidate.withIndex().associate { it.value to it.index }
            return document.edges.sumOf { edge ->
                val first = topLaneOf(edge.from, graph)
                val second = topLaneOf(edge.to, graph)
                if (first == null || second == null || first == second) {
                    0
                } else {
                    abs(
                        (candidatePositions[first] ?: positions[first] ?: 0) -
                            (candidatePositions[second] ?: positions[second] ?: 0),
                    )
                }
            }
        }
        var best = cost(order)
        var changed = true
        while (changed) {
            changed = false
            for (index in 0 until order.lastIndex) {
                order.swap(index, index + 1)
                val candidate = cost(order)
                if (candidate < best) {
                    best = candidate
                    changed = true
                } else {
                    order.swap(index, index + 1)
                }
            }
        }
        return order
    }

    private fun topLaneOf(
        id: String,
        graph: WorkGraph,
    ): String? {
        var parent = graph.nodes[id]?.parentId ?: return null
        var result = parent
        val visited = mutableSetOf<String>()
        while (visited.add(parent)) {
            val next = graph.nodes[parent]?.parentId ?: break
            result = next
            parent = next
        }
        return result
    }

    private fun crossingCount(
        layers: List<List<String>>,
        edges: List<WorkEdge>,
        rank: Map<String, Int>,
    ): Int {
        var result = 0
        edges.forEachIndexed { firstIndex, first ->
            for (secondIndex in firstIndex + 1 until edges.size) {
                val second = edges[secondIndex]
                val firstSourceRank = rank[first.from] ?: continue
                val firstTargetRank = rank[first.to] ?: continue
                val secondSourceRank = rank[second.from] ?: continue
                val secondTargetRank = rank[second.to] ?: continue
                if (
                    firstSourceRank != secondSourceRank ||
                    firstTargetRank != secondTargetRank ||
                    firstSourceRank + 1 != firstTargetRank
                ) {
                    continue
                }
                val upper = layers.getOrNull(firstSourceRank).orEmpty()
                val lower = layers.getOrNull(firstTargetRank).orEmpty()
                val firstSource = upper.indexOf(first.from)
                val secondSource = upper.indexOf(second.from)
                val firstTarget = lower.indexOf(first.to)
                val secondTarget = lower.indexOf(second.to)
                if ((firstSource - secondSource) * (firstTarget - secondTarget) < 0) result += 1
            }
        }
        return result
    }

    private fun adjacentCrossings(
        upper: List<String>,
        lower: List<String>,
        edges: List<WorkEdge>,
    ): Int {
        val upperIndex = upper.withIndex().associate { it.value to it.index }
        val lowerIndex = lower.withIndex().associate { it.value to it.index }
        val pairs = edges.mapNotNull { edge ->
            val first = upperIndex[edge.from] ?: return@mapNotNull null
            val second = lowerIndex[edge.to] ?: return@mapNotNull null
            first to second
        }.sortedWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second })
        var count = 0
        pairs.indices.forEach { first ->
            for (second in first + 1 until pairs.size) {
                if (pairs[first].second > pairs[second].second) count += 1
            }
        }
        return count
    }

    private fun median(values: List<Int>): Double {
        if (values.isEmpty()) return Double.POSITIVE_INFINITY
        val sorted = values.sorted()
        return if (sorted.size % 2 == 1) {
            sorted[sorted.size / 2].toDouble()
        } else {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        }
    }

    private fun average(values: List<Int>): Double =
        if (values.isEmpty()) Double.POSITIVE_INFINITY else values.average()

    private fun boundsOf(nodes: Collection<WorkNode>): SceneRect? {
        val positioned = nodes.filter(WorkNode::positioned)
        if (positioned.isEmpty()) return null
        return positioned.map(WorkNode::bounds).reduce(SceneRect::union)
    }

    private fun SceneRect.expand(
        horizontal: Float,
        vertical: Float,
    ): SceneRect = SceneRect(
        left = left - horizontal,
        top = top - vertical,
        right = right + horizontal,
        bottom = bottom + vertical,
    )

    private fun SceneRect.containsStrictly(point: ScenePoint): Boolean =
        point.x > left && point.x < right && point.y > top && point.y < bottom

    private fun SceneRect.containsInclusive(point: ScenePoint): Boolean =
        point.x >= left - LabelEpsilon &&
            point.x <= right + LabelEpsilon &&
            point.y >= top - LabelEpsilon &&
            point.y <= bottom + LabelEpsilon

    private fun SceneRect.contains(other: SceneRect): Boolean =
        other.left >= left &&
            other.right <= right &&
            other.top >= top &&
            other.bottom <= bottom

    private fun SceneRect.overlaps(other: SceneRect): Boolean =
        left < other.right &&
            right > other.left &&
            top < other.bottom &&
            bottom > other.top

    private fun labelBounds(
        center: ScenePoint,
        size: SceneSize,
    ): SceneRect = SceneRect(
        left = center.x - size.width / 2f,
        top = center.y - size.height / 2f,
        right = center.x + size.width / 2f,
        bottom = center.y + size.height / 2f,
    )

    private fun LabelSegmentRef.intersects(bounds: SceneRect): Boolean {
        val minimumX = min(first.x, second.x)
        val maximumX = max(first.x, second.x)
        val minimumY = min(first.y, second.y)
        val maximumY = max(first.y, second.y)
        return if (abs(first.y - second.y) < LabelEpsilon) {
            first.y >= bounds.top &&
                first.y <= bounds.bottom &&
                maximumX >= bounds.left &&
                minimumX <= bounds.right
        } else if (abs(first.x - second.x) < LabelEpsilon) {
            first.x >= bounds.left &&
                first.x <= bounds.right &&
                maximumY >= bounds.top &&
                minimumY <= bounds.bottom
        } else {
            false
        }
    }

    private fun labelOverlapsOwnMarker(
        bounds: SceneRect,
        points: List<ScenePoint>,
    ): Boolean =
        markerClearanceBounds(points, atStart = true)?.let { marker ->
            bounds.overlaps(marker)
        } == true ||
            markerClearanceBounds(points, atStart = false)?.let { marker ->
                bounds.overlaps(marker)
            } == true

    private fun markerClearanceBounds(
        points: List<ScenePoint>,
        atStart: Boolean,
    ): SceneRect? {
        if (points.size < 2) return null
        val tip = if (atStart) points.first() else points.last()
        val inner = if (atStart) points[1] else points[points.lastIndex - 1]
        val dx = inner.x - tip.x
        val dy = inner.y - tip.y
        if (abs(dx) + abs(dy) < LabelEpsilon) return null
        return if (abs(dy) <= LabelEpsilon) {
            val endX = tip.x + (if (dx < 0f) -1f else 1f) * MarkerClearanceLength
            SceneRect(
                left = min(tip.x, endX),
                top = tip.y - MarkerClearanceHalfWidth,
                right = max(tip.x, endX),
                bottom = tip.y + MarkerClearanceHalfWidth,
            )
        } else if (abs(dx) <= LabelEpsilon) {
            val endY = tip.y + (if (dy < 0f) -1f else 1f) * MarkerClearanceLength
            SceneRect(
                left = tip.x - MarkerClearanceHalfWidth,
                top = min(tip.y, endY),
                right = tip.x + MarkerClearanceHalfWidth,
                bottom = max(tip.y, endY),
            )
        } else {
            SceneRect(
                left = min(tip.x, inner.x),
                top = min(tip.y, inner.y),
                right = max(tip.x, inner.x),
                bottom = max(tip.y, inner.y),
            )
        }
    }

    private fun ScenePoint.move(side: Side, distance: Float): ScenePoint = when (side) {
        Side.Top -> copy(y = y - distance)
        Side.Bottom -> copy(y = y + distance)
        Side.Left -> copy(x = x - distance)
        Side.Right -> copy(x = x + distance)
    }

    private fun intersect(
        shapeLayout: MermaidShapeLayout?,
        bounds: SceneRect,
        toward: ScenePoint,
    ): ScenePoint {
        val center = bounds.center
        val outline = shapeLayout?.geometry?.outline?.map { point ->
            ScenePoint(center.x + point.x, center.y + point.y)
        } ?: listOf(
            ScenePoint(bounds.left, bounds.top),
            ScenePoint(bounds.right, bounds.top),
            ScenePoint(bounds.right, bounds.bottom),
            ScenePoint(bounds.left, bounds.bottom),
        )
        val dx = toward.x - center.x
        val dy = toward.y - center.y
        var bestScale = -1f
        var best = center
        (outline + outline.first()).zipWithNext().forEach { (first, second) ->
            val segmentX = second.x - first.x
            val segmentY = second.y - first.y
            val denominator = dx * segmentY - dy * segmentX
            if (abs(denominator) < Epsilon) return@forEach
            val offsetX = first.x - center.x
            val offsetY = first.y - center.y
            val ray = (offsetX * segmentY - offsetY * segmentX) / denominator
            val segment = (offsetX * dy - offsetY * dx) / denominator
            if (ray in 0f..1f && segment in 0f..1f && ray > bestScale) {
                bestScale = ray
                best = ScenePoint(center.x + dx * ray, center.y + dy * ray)
            }
        }
        return best
    }

    private fun List<ScenePoint>.orthogonalized(): List<ScenePoint> {
        if (size < 2) return this
        val result = mutableListOf(first())
        drop(1).forEach { point ->
            val previous = result.last()
            if (point == previous) return@forEach
            if (abs(previous.x - point.x) >= Epsilon && abs(previous.y - point.y) >= Epsilon) {
                val prior = result.getOrNull(result.lastIndex - 1)
                result += if (prior != null && abs(prior.x - previous.x) < Epsilon) {
                    ScenePoint(previous.x, point.y)
                } else {
                    ScenePoint(point.x, previous.y)
                }
            }
            result += point
        }
        return result
    }

    private fun List<ScenePoint>.simplified(): List<ScenePoint> {
        val result = removeAdjacentDuplicates().toMutableList()
        var changed = true
        while (changed && result.size >= 3) {
            changed = false
            var index = 1
            while (index < result.lastIndex) {
                val previous = result[index - 1]
                val current = result[index]
                val next = result[index + 1]
                val spike = previous == next
                val horizontal = abs(previous.y - current.y) < Epsilon &&
                    abs(current.y - next.y) < Epsilon &&
                    current.x in min(previous.x, next.x)..max(previous.x, next.x)
                val vertical = abs(previous.x - current.x) < Epsilon &&
                    abs(current.x - next.x) < Epsilon &&
                    current.y in min(previous.y, next.y)..max(previous.y, next.y)
                if (spike || horizontal || vertical) {
                    result.removeAt(index)
                    changed = true
                } else {
                    index += 1
                }
            }
        }
        return result
    }

    private fun List<ScenePoint>.removeAdjacentDuplicates(): List<ScenePoint> =
        filterIndexed { index, point -> index == 0 || point != this[index - 1] }

    private fun List<ScenePoint>.halfLengthPoint(): ScenePoint {
        if (isEmpty()) return ScenePoint(0f, 0f)
        if (size == 1) return first()
        val lengths = zipWithNext { first, second ->
            abs(second.x - first.x) + abs(second.y - first.y)
        }
        val target = lengths.sum() / 2f
        var traversed = 0f
        lengths.forEachIndexed { index, length ->
            if (length > 0f && traversed + length >= target) {
                val ratio = (target - traversed) / length
                return ScenePoint(
                    x = this[index].x + (this[index + 1].x - this[index].x) * ratio,
                    y = this[index].y + (this[index + 1].y - this[index].y) * ratio,
                )
            }
            traversed += length
        }
        return last()
    }

    private fun segmentsCross(
        first: ScenePoint,
        second: ScenePoint,
        otherFirst: ScenePoint,
        otherSecond: ScenePoint,
    ): Boolean {
        val horizontal = abs(first.y - second.y) < Epsilon
        val otherHorizontal = abs(otherFirst.y - otherSecond.y) < Epsilon
        if (horizontal == otherHorizontal) return false
        val h1 = if (horizontal) first else otherFirst
        val h2 = if (horizontal) second else otherSecond
        val v1 = if (horizontal) otherFirst else first
        val v2 = if (horizontal) otherSecond else second
        val x = v1.x
        val y = h1.y
        return x > min(h1.x, h2.x) &&
            x < max(h1.x, h2.x) &&
            y > min(v1.y, v2.y) &&
            y < max(v1.y, v2.y)
    }

    private fun <T> MutableList<T>.swap(first: Int, second: Int) {
        val value = this[first]
        this[first] = this[second]
        this[second] = value
    }

    private data class WorkNode(
        val id: String,
        val parentId: String?,
        var width: Float,
        var height: Float,
        val isGroup: Boolean = false,
        val isDummy: Boolean = false,
        val isEdgeLabel: Boolean = false,
        val padding: Float = 0f,
        var x: Float = 0f,
        var y: Float = 0f,
        var contentTop: Float? = null,
        var positioned: Boolean = false,
    ) {
        fun bounds(): SceneRect = SceneRect(
            left = x - width / 2f,
            top = y - height / 2f,
            right = x + width / 2f,
            bottom = y + height / 2f,
        )

        fun center(): ScenePoint = ScenePoint(x, y)

        fun setBounds(bounds: SceneRect) {
            x = bounds.center.x
            y = bounds.center.y
            width = bounds.width
            height = bounds.height
            positioned = true
        }
    }

    private data class WorkEdge(
        val id: String,
        val from: String,
        val to: String,
        val originalIndex: Int,
        val originalFrom: String = from,
        val originalTo: String = to,
    ) {
        val key: String get() = "$id:$from->$to"
    }

    private data class WorkGraph(
        val nodes: LinkedHashMap<String, WorkNode>,
        val edges: List<WorkEdge>,
    ) {
        fun contentIds(): List<String> = nodes.values.filterNot(WorkNode::isGroup).map(WorkNode::id)
    }

    private data class Layering(
        val layers: List<MutableList<String>>,
        val rank: MutableMap<String, Int>,
    )

    private data class ProperLayering(
        val graph: WorkGraph,
        val layers: MutableList<MutableList<String>>,
        val rank: MutableMap<String, Int>,
    )

    private data class MutableRoute(
        var points: MutableList<ScenePoint>,
        var edge: FlowEdge? = null,
    )

    private data class CrossingSnapshot(
        val count: Int,
        val routeIndices: Set<Int>,
    )

    private data class Obstacle(
        val nodeId: String,
        val bounds: SceneRect,
    )

    private data class LabelSegmentRef(
        val edgeIndex: Int,
        val first: ScenePoint,
        val second: ScenePoint,
    )

    private data class LabelSegmentCandidate(
        val index: Int,
        val length: Float,
        val orientation: LabelOrientation,
    )

    private data class LabelPlacement(
        val laneId: String,
        val anchor: ScenePoint,
        val bounds: SceneRect,
    )

    private enum class LabelOrientation {
        Horizontal,
        Vertical,
    }

    private data class EndpointSides(
        val source: Side,
        val target: Side,
    )

    private data class EdgeSideInfo(
        val edgeIndex: Int,
        val sourceId: String,
        val targetId: String,
        var source: Side,
        val target: Side,
        val absoluteDx: Float,
        val absoluteDy: Float,
        val dxSign: Int,
        val dySign: Int,
    ) {
        fun preferenceStrength(): Float = when (source) {
            Side.Top,
            Side.Bottom,
            -> if (absoluteDx == 0f) Float.POSITIVE_INFINITY else absoluteDy / absoluteDx
            Side.Left,
            Side.Right,
            -> if (absoluteDy == 0f) Float.POSITIVE_INFINITY else absoluteDx / absoluteDy
        }

        fun secondarySide(): Side = when (source) {
            Side.Top,
            Side.Bottom,
            -> if (dxSign >= 0) Side.Right else Side.Left
            Side.Left,
            Side.Right,
            -> if (dySign >= 0) Side.Bottom else Side.Top
        }
    }

    private data class PortGroup(
        val nodeId: String,
        val side: Side,
        val role: EndpointRole,
    )

    private data class PortMember(
        val edgeIndex: Int,
        val oppositeCoordinate: Float,
    )

    private enum class EndpointRole {
        Source,
        Target,
    }

    private enum class ThreeSegmentRouteKind {
        HorizontalVerticalHorizontal,
        VerticalHorizontalVertical,
    }

    private enum class Side(
        val isVerticalFace: Boolean,
    ) {
        Top(false),
        Bottom(false),
        Left(true),
        Right(true),
        ;

        fun oppositeCoordinate(point: ScenePoint): Float =
            if (isVerticalFace) point.y else point.x
    }

    private const val Epsilon = 0.000001f
    private const val LabelEpsilon = 0.001f
    private const val LabelPlacementBuffer = 3f
    private const val LabelLaneMargin = 1f
    private const val LabelEndpointClearance = 12f
    private const val MarkerClearanceLength = 10f
    private const val MarkerClearanceHalfWidth = 7f
    private const val TopLaneTitleBandHeight = 21f
    private const val LeftLaneTitleBandWidth = 36f
    private const val TitleRailClearance = 4f
    private const val MinimumSharedTitleSpan = 8f
    private const val MaterializedGeometryEpsilon = 0.001f
    private const val CrossingResolutionNodeBuffer = 2f
    private const val CrossingResolutionAnchor = 20f
    private const val CrossingResolutionMaximumIterations = 4
    private const val MinimumSharedTrackSpan = 8f
    private const val SharedTrackAxisEpsilon = 0.5f
    private val PortSwapDeltas = listOf(0f, 8f, -8f, 16f, -16f)
    private val LabelAnchorFractions = listOf(
        0.5f,
        0.25f,
        0.75f,
        0.05f,
        0.95f,
        0.15f,
        0.85f,
        0.1f,
        0.9f,
    )
}
