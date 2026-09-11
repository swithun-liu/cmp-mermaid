package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.SceneElement
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneSize
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricsRequest
import kotlin.math.max
import kotlin.math.min

internal class FlowchartLayout {
    fun layout(
        document: FlowchartDocument,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val preparedDocument = collapseSubgraphs(document)
        val nodeSizes = linkedMapOf<String, SceneSize>()
        for ((id, node) in preparedDocument.nodes) {
            val measured = try {
                context.textMetrics.measure(
                    TextMetricsRequest(
                        text = node.label,
                        fontSize = context.options.fontSize,
                        maxWidth = context.options.maxNodeTextWidth,
                    ),
                )
            } catch (failure: Throwable) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Text measurement failed for '$id': ${failure.message ?: "unknown error"}",
                    ),
                )
            }
            nodeSizes[id] = sizeForShape(
                shape = node.shape,
                textSize = SceneSize(measured.width, measured.height),
                horizontalPadding = context.options.nodeHorizontalPadding,
                verticalPadding = context.options.nodeVerticalPadding,
            )
        }

        val rankingDocument = preparedDocument.copy(
            edges = preparedDocument.edges.map { edge ->
                edge.copy(
                    from = resolveSubgraphEndpoint(preparedDocument, edge.from, outgoing = true),
                    to = resolveSubgraphEndpoint(preparedDocument, edge.to, outgoing = false),
                )
            },
        )
        val ranks = calculateRanks(rankingDocument)
        val orderedLayers = orderLayers(rankingDocument, ranks)
        val nodeBounds = placeNodes(preparedDocument.direction, orderedLayers, nodeSizes, context)
        if (nodeBounds.size != preparedDocument.nodes.size) {
            return GMResult.Err(MermaidError.Layout("Not every flowchart node was positioned"))
        }
        val subgraphBounds = calculateSubgraphBounds(preparedDocument, nodeBounds)
        val endpointBounds = nodeBounds + subgraphBounds

        val elements = mutableListOf<SceneElement>()
        addSubgraphs(preparedDocument, subgraphBounds, context, elements)
        addEdges(preparedDocument, endpointBounds, context, elements)
        addNodes(preparedDocument, nodeBounds, context, elements)

        return GMResult.Ok(normalizeScene(elements, context))
    }

    private fun collapseSubgraphs(document: FlowchartDocument): FlowchartDocument {
        val collapsed = document.subgraphs.filter(FlowSubgraph::collapsed)
        if (collapsed.isEmpty()) {
            return document
        }

        fun collapsedAncestor(id: String): FlowSubgraph? = collapsed
            .filter { it.id == id || id in it.nodeIds }
            .maxByOrNull { it.nodeIds.size }

        val hiddenNodeIds = collapsed.flatMapTo(mutableSetOf(), FlowSubgraph::nodeIds)
        val visibleNodes = document.nodes
            .filterKeys { it !in hiddenNodeIds }
            .toMutableMap()
        collapsed.forEach { subgraph ->
            visibleNodes[subgraph.id] = FlowNode(
                id = subgraph.id,
                label = subgraph.label,
                shape = SceneShapeKind.RoundedRectangle,
            )
        }

        val visibleEdges = document.edges.mapNotNull { edge ->
            val from = collapsedAncestor(edge.from)?.id ?: edge.from
            val to = collapsedAncestor(edge.to)?.id ?: edge.to
            if (from == to && edge.from != edge.to) {
                null
            } else {
                edge.copy(from = from, to = to)
            }
        }
        val visibleSubgraphs = document.subgraphs
            .filterNot { subgraph ->
                subgraph.collapsed || collapsed.any { parent -> subgraph.id in parent.nodeIds }
            }
            .map { subgraph ->
                subgraph.copy(
                    nodeIds = subgraph.nodeIds
                        .mapTo(linkedSetOf()) { nodeId -> collapsedAncestor(nodeId)?.id ?: nodeId },
                )
            }
        return document.copy(
            nodes = visibleNodes,
            edges = visibleEdges,
            subgraphs = visibleSubgraphs,
        )
    }

    private fun resolveSubgraphEndpoint(
        document: FlowchartDocument,
        id: String,
        outgoing: Boolean,
    ): String {
        if (id in document.nodes) {
            return id
        }
        val subgraph = document.subgraphs.firstOrNull { it.id == id } ?: return id
        val declarationOrder = document.nodes.keys.withIndex().associate { it.value to it.index }
        val candidates = subgraph.nodeIds.filter { it in document.nodes }
        return if (outgoing) {
            candidates.maxByOrNull { declarationOrder[it] ?: -1 }
        } else {
            candidates.minByOrNull { declarationOrder[it] ?: Int.MAX_VALUE }
        } ?: id
    }

    private fun calculateSubgraphBounds(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
    ): Map<String, SceneRect> = buildMap {
        document.subgraphs
            .sortedBy { it.nodeIds.size }
            .forEach { subgraph ->
                val bounds = subgraph.nodeIds
                    .mapNotNull { nodeId -> nodeBounds[nodeId] ?: get(nodeId) }
                    .reduceOrNull(SceneRect::union)
                    ?.let { SceneRect(it.left - 24f, it.top - 42f, it.right + 24f, it.bottom + 24f) }
                    ?: return@forEach
                put(subgraph.id, bounds)
            }
    }

    private fun calculateRanks(document: FlowchartDocument): Map<String, Int> {
        val incomingCount = document.nodes.keys.associateWith { 0 }.toMutableMap()
        val outgoing = document.nodes.keys.associateWith { mutableListOf<String>() }.toMutableMap()
        document.edges.forEach { edge ->
            incomingCount[edge.to] = (incomingCount[edge.to] ?: 0) + 1
            outgoing.getOrPut(edge.from) { mutableListOf() } += edge.to
        }

        val ranks = document.nodes.keys.associateWith { 0 }.toMutableMap()
        val processed = mutableSetOf<String>()
        val queue = incomingCount
            .filterValues { it == 0 }
            .keys
            .sorted()
            .toMutableList()

        while (processed.size < document.nodes.size) {
            if (queue.isEmpty()) {
                val cycleBreak = document.nodes.keys
                    .filterNot(processed::contains)
                    .minWithOrNull(compareBy<String> { incomingCount[it] ?: 0 }.thenBy { it })
                if (cycleBreak != null) {
                    queue += cycleBreak
                }
            }

            val node = queue.removeFirstOrNull() ?: break
            if (!processed.add(node)) {
                continue
            }
            outgoing[node].orEmpty().forEach { target ->
                if (target !in processed) {
                    val edgeLength = document.edges
                        .filter { it.from == node && it.to == target }
                        .maxOfOrNull(FlowEdge::minimumLength)
                        ?: 1
                    ranks[target] = max(ranks[target] ?: 0, (ranks[node] ?: 0) + edgeLength)
                }
                incomingCount[target] = (incomingCount[target] ?: 1) - 1
                if (incomingCount[target] == 0) {
                    queue += target
                    queue.sort()
                }
            }
        }
        return ranks
    }

    private fun orderLayers(
        document: FlowchartDocument,
        ranks: Map<String, Int>,
    ): List<List<String>> {
        val layers = ranks.entries
            .groupBy({ it.value }, { it.key })
            .toList()
            .sortedBy { it.first }
            .map { (_, nodeIds) -> nodeIds.sorted().toMutableList() }

        repeat(3) {
            for (index in 1 until layers.size) {
                val previousPositions = layers[index - 1]
                    .withIndex()
                    .associate { it.value to it.index }
                layers[index].sortWith(
                    compareBy<String> { node ->
                        val positions = document.edges
                            .asSequence()
                            .filter { it.to == node }
                            .mapNotNull { previousPositions[it.from] }
                            .toList()
                        positions.averageOrMax()
                    }.thenBy { it },
                )
            }
            for (index in layers.lastIndex - 1 downTo 0) {
                val nextPositions = layers[index + 1]
                    .withIndex()
                    .associate { it.value to it.index }
                layers[index].sortWith(
                    compareBy<String> { node ->
                        val positions = document.edges
                            .asSequence()
                            .filter { it.from == node }
                            .mapNotNull { nextPositions[it.to] }
                            .toList()
                        positions.averageOrMax()
                    }.thenBy { it },
                )
            }
        }
        return layers
    }

    private fun List<Int>.averageOrMax(): Double =
        if (isEmpty()) Double.MAX_VALUE else sum().toDouble() / size

    private fun placeNodes(
        direction: FlowDirection,
        layers: List<List<String>>,
        sizes: Map<String, SceneSize>,
        context: MermaidRenderContext,
    ): Map<String, SceneRect> {
        val vertical = direction == FlowDirection.TopToBottom ||
            direction == FlowDirection.BottomToTop
        val layerPrimarySizes = layers.map { layer ->
            layer.maxOf { id -> sizes.getValue(id).primary(vertical) }
        }
        val layerCrossSizes = layers.map { layer ->
            layer.sumOfFloat { id -> sizes.getValue(id).cross(vertical) } +
                context.options.horizontalSpacing * (layer.size - 1).coerceAtLeast(0)
        }
        val maxCross = layerCrossSizes.maxOrNull() ?: 0f
        val primaryLength = layerPrimarySizes.sum() +
            context.options.verticalSpacing * (layers.size - 1).coerceAtLeast(0)
        val origin = 72f
        val result = linkedMapOf<String, SceneRect>()
        var primaryCursor = origin

        layers.forEachIndexed { layerIndex, layer ->
            val layerPrimary = layerPrimarySizes[layerIndex]
            var crossCursor = origin + (maxCross - layerCrossSizes[layerIndex]) / 2f
            layer.forEach { id ->
                val size = sizes.getValue(id)
                val primarySize = size.primary(vertical)
                val crossSize = size.cross(vertical)
                var primary = primaryCursor + (layerPrimary - primarySize) / 2f
                if (direction == FlowDirection.BottomToTop || direction == FlowDirection.RightToLeft) {
                    primary = origin + primaryLength - (primary - origin) - primarySize
                }
                val cross = crossCursor
                result[id] = if (vertical) {
                    SceneRect(cross, primary, cross + size.width, primary + size.height)
                } else {
                    SceneRect(primary, cross, primary + size.width, cross + size.height)
                }
                crossCursor += crossSize + context.options.horizontalSpacing
            }
            primaryCursor += layerPrimary + context.options.verticalSpacing
        }
        return result
    }

    private fun addSubgraphs(
        document: FlowchartDocument,
        subgraphBounds: Map<String, SceneRect>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        document.subgraphs
            .sortedByDescending { it.nodeIds.size }
            .forEachIndexed { index, subgraph ->
                val bounds = subgraphBounds[subgraph.id]
                    ?: return@forEachIndexed
                elements += SceneShape(
                    id = "subgraph_${subgraph.id}",
                    bounds = bounds,
                    kind = SceneShapeKind.RoundedRectangle,
                    fill = context.theme.groupFill,
                    stroke = context.theme.groupStroke,
                    strokeWidth = 1.2f,
                    cornerRadius = 12f,
                    zIndex = index,
                )
                elements += SceneText(
                    text = subgraph.label,
                    bounds = SceneRect(bounds.left + 14f, bounds.top + 9f, bounds.right - 14f, bounds.top + 31f),
                    color = context.theme.groupText,
                    fontSize = 13f,
                    weight = SceneTextWeight.Bold,
                    zIndex = index + 1,
                )
            }
    }

    private fun addEdges(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        val vertical = document.direction == FlowDirection.TopToBottom ||
            document.direction == FlowDirection.BottomToTop
        val outerRight = nodeBounds.values.maxOfOrNull(SceneRect::right) ?: 0f
        val outerBottom = nodeBounds.values.maxOfOrNull(SceneRect::bottom) ?: 0f
        val startPorts = assignPorts(document.edges, nodeBounds, document.direction, outgoing = true)
        val endPorts = assignPorts(document.edges, nodeBounds, document.direction, outgoing = false)
        val parallelLanes = assignParallelLanes(document.edges)
        val primaryChannels = assignPrimaryChannels(document.edges, nodeBounds, document.direction)
        val paths = mutableListOf<ScenePath>()
        val labels = mutableListOf<SceneElement>()

        document.edges.forEachIndexed { index, edge ->
            if (edge.invisible) {
                return@forEachIndexed
            }
            val from = nodeBounds[edge.from] ?: return@forEachIndexed
            val to = nodeBounds[edge.to] ?: return@forEachIndexed
            val edgeStyle = resolveEdgeStyle(edge, document)
            val start = startPorts[index] ?: anchor(from, document.direction, start = true)
            val end = endPorts[index] ?: anchor(to, document.direction, start = false)
            val forward = when (document.direction) {
                FlowDirection.TopToBottom -> end.y > start.y
                FlowDirection.BottomToTop -> end.y < start.y
                FlowDirection.LeftToRight -> end.x > start.x
                FlowDirection.RightToLeft -> end.x < start.x
            }
            val parallelLane = parallelLanes[index]
            val primaryChannel = primaryChannels[index] ?: 0f
            val points = if (edge.from == edge.to) {
                selfLoop(from, document.direction, parallelLane?.crossOffset ?: 0f)
            } else if (vertical) {
                routeVertical(
                    start = start,
                    end = end,
                    forward = forward,
                    outerRight = outerRight,
                    parallelLane = parallelLane,
                    primaryChannel = primaryChannel,
                    edgeIndex = index,
                )
            } else {
                routeHorizontal(
                    start = start,
                    end = end,
                    forward = forward,
                    outerBottom = outerBottom,
                    parallelLane = parallelLane,
                    primaryChannel = primaryChannel,
                    edgeIndex = index,
                )
            }
            paths += ScenePath(
                id = edge.id,
                points = points.distinctAdjacent(),
                color = edgeStyle.stroke ?: context.theme.edge,
                strokeWidth = edgeStyle.strokeWidth ?: edge.thickness,
                strokePattern = edgeStyle.strokePattern ?: edge.pattern,
                arrowStart = edge.arrowStart,
                arrowEnd = edge.arrowEnd,
            )

            val label = edge.label ?: return@forEachIndexed
            val metrics = context.textMetrics.measure(
                TextMetricsRequest(
                    text = label,
                    fontSize = edgeStyle.fontSize ?: 13f,
                    maxWidth = 180f,
                    weight = edgeStyle.fontWeight ?: SceneTextWeight.Normal,
                ),
            )
            val center = points.longestSegmentCenter()
            val labelBounds = SceneRect(
                left = center.x - metrics.width / 2f - 6f,
                top = center.y - metrics.height / 2f - 3f,
                right = center.x + metrics.width / 2f + 6f,
                bottom = center.y + metrics.height / 2f + 3f,
            )
            labels += SceneShape(
                id = "${edge.id}_label_background",
                bounds = labelBounds,
                kind = SceneShapeKind.RoundedRectangle,
                fill = context.theme.edgeLabelFill,
                stroke = context.theme.edgeLabelFill,
                cornerRadius = 4f,
                zIndex = 6,
            )
            labels += SceneText(
                text = label,
                bounds = labelBounds,
                color = edgeStyle.text ?: context.theme.nodeText,
                fontSize = edgeStyle.fontSize ?: 13f,
                weight = edgeStyle.fontWeight ?: SceneTextWeight.Normal,
                zIndex = 7,
            )
        }
        elements += LineBridgeRouter.apply(paths)
        elements += labels
    }

    private fun assignPorts(
        edges: List<FlowEdge>,
        nodeBounds: Map<String, SceneRect>,
        direction: FlowDirection,
        outgoing: Boolean,
    ): Map<Int, ScenePoint> {
        val vertical = direction == FlowDirection.TopToBottom ||
            direction == FlowDirection.BottomToTop
        val indexedEdges = edges.withIndex().filterNot { it.value.invisible }
        val grouped = indexedEdges.groupBy { if (outgoing) it.value.from else it.value.to }
        return buildMap {
            grouped.forEach { (nodeId, group) ->
                val bounds = nodeBounds[nodeId] ?: return@forEach
                val sorted = group.sortedWith(
                    compareBy<IndexedValue<FlowEdge>> { indexed ->
                        val otherId = if (outgoing) indexed.value.to else indexed.value.from
                        val other = nodeBounds[otherId]
                        if (vertical) other?.center?.x else other?.center?.y
                    }.thenBy { it.index },
                )
                sorted.forEachIndexed { slot, indexed ->
                    val ratio = (slot + 1f) / (sorted.size + 1f)
                    put(
                        indexed.index,
                        if (vertical) {
                            ScenePoint(
                                x = bounds.left + bounds.width * ratio,
                                y = when (direction) {
                                    FlowDirection.TopToBottom -> if (outgoing) bounds.bottom else bounds.top
                                    FlowDirection.BottomToTop -> if (outgoing) bounds.top else bounds.bottom
                                },
                            )
                        } else {
                            ScenePoint(
                                x = when (direction) {
                                    FlowDirection.LeftToRight -> if (outgoing) bounds.right else bounds.left
                                    FlowDirection.RightToLeft -> if (outgoing) bounds.left else bounds.right
                                },
                                y = bounds.top + bounds.height * ratio,
                            )
                        },
                    )
                }
            }
        }
    }

    private data class ParallelLane(
        val crossOffset: Float,
    )

    private fun assignParallelLanes(edges: List<FlowEdge>): Map<Int, ParallelLane> = buildMap {
        edges.withIndex()
            .filterNot { it.value.invisible }
            .groupBy { it.value.from to it.value.to }
            .values
            .filter { it.size > 1 }
            .forEach { group ->
                group.forEachIndexed { slot, indexed ->
                    put(
                        indexed.index,
                        ParallelLane(
                            crossOffset = (slot - (group.size - 1) / 2f) * 52f,
                        ),
                    )
                }
            }
    }

    private fun assignPrimaryChannels(
        edges: List<FlowEdge>,
        nodeBounds: Map<String, SceneRect>,
        direction: FlowDirection,
    ): Map<Int, Float> {
        val vertical = direction == FlowDirection.TopToBottom ||
            direction == FlowDirection.BottomToTop
        return buildMap {
            edges.withIndex()
                .filterNot { it.value.invisible || it.value.from == it.value.to }
                .groupBy { indexed ->
                    val from = nodeBounds[indexed.value.from]
                    val to = nodeBounds[indexed.value.to]
                    if (vertical) {
                        from?.center?.y to to?.center?.y
                    } else {
                        from?.center?.x to to?.center?.x
                    }
                }
                .values
                .filter { it.size > 1 }
                .forEach { group ->
                    group.forEachIndexed { slot, indexed ->
                        put(indexed.index, (slot - (group.size - 1) / 2f) * 12f)
                    }
                }
        }
    }

    private fun routeVertical(
        start: ScenePoint,
        end: ScenePoint,
        forward: Boolean,
        outerRight: Float,
        parallelLane: ParallelLane?,
        primaryChannel: Float,
        edgeIndex: Int,
    ): List<ScenePoint> {
        if (!forward) {
            val side = outerRight + 32f + (edgeIndex % 4) * 12f
            return listOf(start, ScenePoint(side, start.y), ScenePoint(side, end.y), end)
        }
        if (parallelLane != null) {
            val direction = if (end.y >= start.y) 1f else -1f
            val stub = min(28f, kotlin.math.abs(end.y - start.y) / 4f)
            val startStub = start.y + stub * direction
            val endStub = end.y - stub * direction
            val lane = (start.x + end.x) / 2f + parallelLane.crossOffset
            return listOf(
                start,
                ScenePoint(start.x, startStub),
                ScenePoint(lane, startStub),
                ScenePoint(lane, endStub),
                ScenePoint(end.x, endStub),
                end,
            )
        }
        val middle = (start.y + end.y) / 2f + primaryChannel
        return listOf(start, ScenePoint(start.x, middle), ScenePoint(end.x, middle), end)
    }

    private fun routeHorizontal(
        start: ScenePoint,
        end: ScenePoint,
        forward: Boolean,
        outerBottom: Float,
        parallelLane: ParallelLane?,
        primaryChannel: Float,
        edgeIndex: Int,
    ): List<ScenePoint> {
        if (!forward) {
            val side = outerBottom + 32f + (edgeIndex % 4) * 12f
            return listOf(start, ScenePoint(start.x, side), ScenePoint(end.x, side), end)
        }
        if (parallelLane != null) {
            val direction = if (end.x >= start.x) 1f else -1f
            val stub = min(28f, kotlin.math.abs(end.x - start.x) / 4f)
            val startStub = start.x + stub * direction
            val endStub = end.x - stub * direction
            val lane = (start.y + end.y) / 2f + parallelLane.crossOffset
            return listOf(
                start,
                ScenePoint(startStub, start.y),
                ScenePoint(startStub, lane),
                ScenePoint(endStub, lane),
                ScenePoint(endStub, end.y),
                end,
            )
        }
        val middle = (start.x + end.x) / 2f + primaryChannel
        return listOf(start, ScenePoint(middle, start.y), ScenePoint(middle, end.y), end)
    }

    private fun selfLoop(
        bounds: SceneRect,
        direction: FlowDirection,
        laneOffset: Float,
    ): List<ScenePoint> = if (
        direction == FlowDirection.TopToBottom ||
        direction == FlowDirection.BottomToTop
    ) {
        val side = bounds.right + 34f + laneOffset
        listOf(
            ScenePoint(bounds.right, bounds.center.y - 8f),
            ScenePoint(side, bounds.center.y - 8f),
            ScenePoint(side, bounds.center.y + 8f),
            ScenePoint(bounds.right, bounds.center.y + 8f),
        )
    } else {
        val side = bounds.bottom + 34f + laneOffset
        listOf(
            ScenePoint(bounds.center.x - 8f, bounds.bottom),
            ScenePoint(bounds.center.x - 8f, side),
            ScenePoint(bounds.center.x + 8f, side),
            ScenePoint(bounds.center.x + 8f, bounds.bottom),
        )
    }

    private fun addNodes(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        document.nodes.forEach { (id, node) ->
            val bounds = nodeBounds.getValue(id)
            val style = resolveStyle(node, document)
            elements += SceneShape(
                id = id,
                bounds = bounds,
                kind = node.shape,
                fill = style.fill ?: context.theme.nodeFill,
                stroke = style.stroke ?: context.theme.nodeStroke,
                strokeWidth = style.strokeWidth ?: 1.5f,
                strokePattern = style.strokePattern ?: io.github.cmpmermaid.core.SceneStrokePattern.Solid,
                cornerRadius = 9f,
            )
            if (node.shape.showsInternalLabel()) {
                elements += SceneText(
                    text = node.label,
                    bounds = bounds,
                    color = style.text ?: context.theme.nodeText,
                    fontSize = style.fontSize ?: context.options.fontSize,
                    weight = style.fontWeight ?: SceneTextWeight.Medium,
                )
            }
        }
    }

    private fun resolveStyle(
        node: FlowNode,
        document: FlowchartDocument,
    ): FlowNodeStyle {
        var resolved = document.classStyles["default"] ?: FlowNodeStyle()
        node.classes.forEach { className ->
            resolved = resolved.merge(document.classStyles[className])
        }
        return resolved.merge(node.inlineStyle)
    }

    private fun resolveEdgeStyle(
        edge: FlowEdge,
        document: FlowchartDocument,
    ): FlowNodeStyle {
        var resolved = FlowNodeStyle()
        edge.classes.forEach { className ->
            resolved = resolved.merge(document.classStyles[className])
        }
        return resolved.merge(edge.inlineStyle)
    }

    private fun FlowNodeStyle.merge(other: FlowNodeStyle?): FlowNodeStyle {
        if (other == null) {
            return this
        }
        return FlowNodeStyle(
            fill = other.fill ?: fill,
            stroke = other.stroke ?: stroke,
            text = other.text ?: text,
            strokeWidth = other.strokeWidth ?: strokeWidth,
            strokePattern = other.strokePattern ?: strokePattern,
            fontSize = other.fontSize ?: fontSize,
            fontWeight = other.fontWeight ?: fontWeight,
        )
    }

    private fun sizeForShape(
        shape: SceneShapeKind,
        textSize: SceneSize,
        horizontalPadding: Float,
        verticalPadding: Float,
    ): SceneSize {
        val baseWidth = max(64f, textSize.width + horizontalPadding * 2f)
        val baseHeight = max(42f, textSize.height + verticalPadding * 2f)
        return when (shape) {
            SceneShapeKind.Circle,
            SceneShapeKind.DoubleCircle,
            -> {
                val diameter = max(baseWidth, baseHeight) + 10f
                SceneSize(diameter, diameter)
            }
            SceneShapeKind.SmallCircle -> SceneSize(22f, 22f)
            SceneShapeKind.FilledCircle -> SceneSize(20f, 20f)
            SceneShapeKind.FramedCircle -> SceneSize(30f, 30f)
            SceneShapeKind.CrossedCircle -> SceneSize(46f, 46f)
            SceneShapeKind.Ellipse -> SceneSize(baseWidth + 20f, baseHeight + 8f)
            SceneShapeKind.Diamond -> SceneSize(baseWidth + 44f, baseHeight + 28f)
            SceneShapeKind.Hexagon -> SceneSize(baseWidth + 30f, baseHeight + 8f)
            SceneShapeKind.Cylinder,
            SceneShapeKind.LinedCylinder,
            -> SceneSize(baseWidth + 8f, baseHeight + 16f)
            SceneShapeKind.DirectAccessStorage,
            SceneShapeKind.CurvedTrapezoid,
            -> SceneSize(baseWidth + 24f, baseHeight + 8f)
            SceneShapeKind.Parallelogram,
            SceneShapeKind.ParallelogramAlt,
            SceneShapeKind.Trapezoid,
            SceneShapeKind.TrapezoidAlt,
            SceneShapeKind.SlopedRectangle,
            -> SceneSize(baseWidth + 28f, baseHeight)
            SceneShapeKind.Asymmetric -> SceneSize(baseWidth + 22f, baseHeight)
            SceneShapeKind.BowTieRectangle,
            SceneShapeKind.NotchedPentagon,
            -> SceneSize(baseWidth + 28f, baseHeight + 8f)
            SceneShapeKind.Hourglass -> SceneSize(58f, 58f)
            SceneShapeKind.Triangle,
            SceneShapeKind.FlippedTriangle,
            -> SceneSize(baseWidth + 18f, baseHeight + 24f)
            SceneShapeKind.Bolt -> SceneSize(52f, 66f)
            SceneShapeKind.BraceLeft,
            SceneShapeKind.BraceRight,
            SceneShapeKind.Braces,
            -> SceneSize(baseWidth + 28f, baseHeight + 12f)
            SceneShapeKind.Document,
            SceneShapeKind.LinedDocument,
            SceneShapeKind.MultiDocument,
            SceneShapeKind.TaggedDocument,
            -> SceneSize(baseWidth + 14f, baseHeight + 16f)
            SceneShapeKind.MultiProcess -> SceneSize(baseWidth + 8f, baseHeight + 8f)
            SceneShapeKind.ForkJoin -> SceneSize(100f, 16f)
            else -> SceneSize(baseWidth, baseHeight)
        }
    }

    private fun anchor(
        bounds: SceneRect,
        direction: FlowDirection,
        start: Boolean,
    ): ScenePoint = when (direction) {
        FlowDirection.TopToBottom -> if (start) {
            ScenePoint(bounds.center.x, bounds.bottom)
        } else {
            ScenePoint(bounds.center.x, bounds.top)
        }
        FlowDirection.BottomToTop -> if (start) {
            ScenePoint(bounds.center.x, bounds.top)
        } else {
            ScenePoint(bounds.center.x, bounds.bottom)
        }
        FlowDirection.LeftToRight -> if (start) {
            ScenePoint(bounds.right, bounds.center.y)
        } else {
            ScenePoint(bounds.left, bounds.center.y)
        }
        FlowDirection.RightToLeft -> if (start) {
            ScenePoint(bounds.left, bounds.center.y)
        } else {
            ScenePoint(bounds.right, bounds.center.y)
        }
    }

    private fun normalizeScene(
        elements: List<SceneElement>,
        context: MermaidRenderContext,
    ): MermaidScene {
        val bounds = elements
            .mapNotNull(::elementBounds)
            .reduceOrNull(SceneRect::union)
            ?: SceneRect(0f, 0f, 1f, 1f)
        val padding = context.options.diagramPadding
        val dx = padding - bounds.left
        val dy = padding - bounds.top
        val translated = elements.map { it.translate(dx, dy) }
        return MermaidScene(
            width = bounds.width + padding * 2f,
            height = bounds.height + padding * 2f,
            background = context.theme.background,
            elements = translated.sortedBy(SceneElement::zIndex),
        )
    }

    private fun elementBounds(element: SceneElement): SceneRect? = when (element) {
        is SceneShape -> element.bounds
        is SceneText -> element.bounds
        is ScenePath -> {
            val first = element.points.firstOrNull() ?: return null
            element.points.drop(1).fold(SceneRect(first.x, first.y, first.x, first.y)) { bounds, point ->
                SceneRect(
                    min(bounds.left, point.x),
                    min(bounds.top, point.y),
                    max(bounds.right, point.x),
                    max(bounds.bottom, point.y),
                )
            }
        }
    }

    private fun SceneElement.translate(dx: Float, dy: Float): SceneElement = when (this) {
        is SceneShape -> copy(bounds = bounds.translate(dx, dy))
        is SceneText -> copy(bounds = bounds.translate(dx, dy))
        is ScenePath -> copy(
            points = points.map { ScenePoint(it.x + dx, it.y + dy) },
            bridges = bridges.map { bridge ->
                bridge.copy(
                    center = ScenePoint(
                        x = bridge.center.x + dx,
                        y = bridge.center.y + dy,
                    ),
                )
            },
        )
    }

    private fun SceneSize.primary(vertical: Boolean): Float = if (vertical) height else width

    private fun SceneSize.cross(vertical: Boolean): Float = if (vertical) width else height

    private fun SceneShapeKind.showsInternalLabel(): Boolean = when (this) {
        SceneShapeKind.SmallCircle,
        SceneShapeKind.FilledCircle,
        SceneShapeKind.FramedCircle,
        SceneShapeKind.ForkJoin,
        SceneShapeKind.Hourglass,
        SceneShapeKind.Bolt,
        SceneShapeKind.CrossedCircle,
        -> false
        else -> true
    }

    private fun <T> Iterable<T>.sumOfFloat(selector: (T) -> Float): Float {
        var total = 0f
        for (item in this) {
            total += selector(item)
        }
        return total
    }

    private fun List<ScenePoint>.distinctAdjacent(): List<ScenePoint> = buildList {
        this@distinctAdjacent.forEach { point ->
            if (lastOrNull() != point) {
                add(point)
            }
        }
    }

    private fun List<ScenePoint>.longestSegmentCenter(): ScenePoint {
        val segment = zipWithNext().maxByOrNull { (start, end) ->
            kotlin.math.abs(end.x - start.x) + kotlin.math.abs(end.y - start.y)
        } ?: return firstOrNull() ?: ScenePoint(0f, 0f)
        return ScenePoint(
            x = (segment.first.x + segment.second.x) / 2f,
            y = (segment.first.y + segment.second.y) / 2f,
        )
    }
}
