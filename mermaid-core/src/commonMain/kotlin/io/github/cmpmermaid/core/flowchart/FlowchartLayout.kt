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
        val nodeSizes = linkedMapOf<String, SceneSize>()
        for ((id, node) in document.nodes) {
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

        val ranks = calculateRanks(document)
        val orderedLayers = orderLayers(document, ranks)
        val nodeBounds = placeNodes(document.direction, orderedLayers, nodeSizes, context)
        if (nodeBounds.size != document.nodes.size) {
            return GMResult.Err(MermaidError.Layout("Not every flowchart node was positioned"))
        }

        val elements = mutableListOf<SceneElement>()
        addSubgraphs(document, nodeBounds, context, elements)
        addEdges(document, nodeBounds, context, elements)
        addNodes(document, nodeBounds, context, elements)

        return GMResult.Ok(normalizeScene(elements, context))
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
        nodeBounds: Map<String, SceneRect>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        document.subgraphs
            .sortedByDescending { it.nodeIds.size }
            .forEachIndexed { index, subgraph ->
                val bounds = subgraph.nodeIds
                    .mapNotNull(nodeBounds::get)
                    .reduceOrNull(SceneRect::union)
                    ?.let { SceneRect(it.left - 24f, it.top - 42f, it.right + 24f, it.bottom + 24f) }
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

        document.edges.forEachIndexed { index, edge ->
            if (edge.invisible) {
                return@forEachIndexed
            }
            val from = nodeBounds[edge.from] ?: return@forEachIndexed
            val to = nodeBounds[edge.to] ?: return@forEachIndexed
            val edgeStyle = resolveEdgeStyle(edge, document)
            val start = anchor(from, document.direction, start = true)
            val end = anchor(to, document.direction, start = false)
            val forward = when (document.direction) {
                FlowDirection.TopToBottom -> end.y > start.y
                FlowDirection.BottomToTop -> end.y < start.y
                FlowDirection.LeftToRight -> end.x > start.x
                FlowDirection.RightToLeft -> end.x < start.x
            }
            val points = if (vertical) {
                if (forward) {
                    val middle = (start.y + end.y) / 2f
                    listOf(start, ScenePoint(start.x, middle), ScenePoint(end.x, middle), end)
                } else {
                    val side = outerRight + 32f + (index % 4) * 12f
                    listOf(start, ScenePoint(side, start.y), ScenePoint(side, end.y), end)
                }
            } else {
                if (forward) {
                    val middle = (start.x + end.x) / 2f
                    listOf(start, ScenePoint(middle, start.y), ScenePoint(middle, end.y), end)
                } else {
                    val side = outerBottom + 32f + (index % 4) * 12f
                    listOf(start, ScenePoint(start.x, side), ScenePoint(end.x, side), end)
                }
            }
            elements += ScenePath(
                id = edge.id,
                points = points.distinctAdjacent(),
                color = edgeStyle.stroke ?: context.theme.edge,
                strokeWidth = edgeStyle.strokeWidth ?: edge.thickness,
                strokePattern = edge.pattern,
                arrowStart = edge.arrowStart,
                arrowEnd = edge.arrowEnd,
            )

            val label = edge.label ?: return@forEachIndexed
            val metrics = context.textMetrics.measure(
                TextMetricsRequest(
                    text = label,
                    fontSize = 13f,
                    maxWidth = 180f,
                    weight = SceneTextWeight.Normal,
                ),
            )
            val center = points[points.size / 2]
            val labelBounds = SceneRect(
                left = center.x - metrics.width / 2f - 6f,
                top = center.y - metrics.height / 2f - 3f,
                right = center.x + metrics.width / 2f + 6f,
                bottom = center.y + metrics.height / 2f + 3f,
            )
            elements += SceneShape(
                id = "${edge.id}_label_background",
                bounds = labelBounds,
                kind = SceneShapeKind.RoundedRectangle,
                fill = context.theme.edgeLabelFill,
                stroke = context.theme.edgeLabelFill,
                cornerRadius = 4f,
                zIndex = 6,
            )
            elements += SceneText(
                text = label,
                bounds = labelBounds,
                color = edgeStyle.text ?: context.theme.nodeText,
                fontSize = 13f,
                weight = SceneTextWeight.Normal,
                zIndex = 7,
            )
        }
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
                cornerRadius = 9f,
            )
            elements += SceneText(
                text = node.label,
                bounds = bounds,
                color = style.text ?: context.theme.nodeText,
                fontSize = context.options.fontSize,
            )
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
            SceneShapeKind.Diamond -> SceneSize(baseWidth + 44f, baseHeight + 28f)
            SceneShapeKind.Hexagon -> SceneSize(baseWidth + 30f, baseHeight + 8f)
            SceneShapeKind.Cylinder -> SceneSize(baseWidth + 8f, baseHeight + 16f)
            SceneShapeKind.Parallelogram,
            SceneShapeKind.ParallelogramAlt,
            SceneShapeKind.Trapezoid,
            SceneShapeKind.TrapezoidAlt,
            -> SceneSize(baseWidth + 28f, baseHeight)
            SceneShapeKind.Asymmetric -> SceneSize(baseWidth + 22f, baseHeight)
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
        is ScenePath -> copy(points = points.map { ScenePoint(it.x + dx, it.y + dy) })
    }

    private fun SceneSize.primary(vertical: Boolean): Float = if (vertical) height else width

    private fun SceneSize.cross(vertical: Boolean): Float = if (vertical) width else height

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
}
