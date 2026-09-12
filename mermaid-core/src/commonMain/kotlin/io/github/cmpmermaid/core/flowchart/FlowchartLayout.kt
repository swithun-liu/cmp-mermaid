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
import io.github.cmpmermaid.core.SceneStrokePattern
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
        val nodeTextSizes = linkedMapOf<String, SceneSize>()
        for ((id, node) in preparedDocument.nodes) {
            val style = resolveStyle(node, preparedDocument)
            val measured = try {
                context.textMetrics.measure(
                    TextMetricsRequest(
                        text = node.label,
                        fontSize = style.fontSize ?: context.options.fontSize,
                        maxWidth = context.options.maxNodeTextWidth,
                        weight = style.fontWeight ?: SceneTextWeight.Medium,
                    ),
                )
            } catch (failure: Throwable) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Text measurement failed for '$id': ${failure.message ?: "unknown error"}",
                    ),
                )
            }
            nodeTextSizes[id] = SceneSize(measured.width, measured.height)
            nodeSizes[id] = sizeForShape(
                shape = node.shape,
                textSize = SceneSize(measured.width, measured.height),
                horizontalPadding = context.options.nodeHorizontalPadding,
                verticalPadding = context.options.nodeVerticalPadding,
            )
        }
        val edgeLabelSizes = linkedMapOf<Int, SceneSize>()
        for ((index, edge) in preparedDocument.edges.withIndex()) {
            val label = edge.label ?: continue
            val edgeStyle = resolveEdgeStyle(edge, preparedDocument)
            val measured = try {
                context.textMetrics.measure(
                    TextMetricsRequest(
                        text = label,
                        fontSize = edgeStyle.fontSize ?: 13f,
                        maxWidth = 180f,
                        weight = edgeStyle.fontWeight ?: SceneTextWeight.Normal,
                    ),
                )
            } catch (failure: Throwable) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Edge label measurement failed for '${edge.id}': " +
                            (failure.message ?: "unknown error"),
                    ),
                )
            }
            edgeLabelSizes[index] = SceneSize(measured.width, measured.height)
        }

        val placement = when (
            val dagre = FlowDagreLayout.layout(
                document = preparedDocument,
                nodeSizes = nodeSizes,
                edgeLabelSizes = edgeLabelSizes,
                options = context.options,
            )
        ) {
            is GMResult.Ok -> dagre.value
            is GMResult.Err -> return dagre
        }
        val nodeBounds = placement.nodeBounds
        if (nodeBounds.size != preparedDocument.nodes.size) {
            return GMResult.Err(MermaidError.Layout("Not every flowchart node was positioned"))
        }
        val subgraphBounds = placement.subgraphBounds

        val elements = mutableListOf<SceneElement>()
        addSubgraphs(preparedDocument, subgraphBounds, context, elements)
        when (
            val edges = addEdges(
                document = preparedDocument,
                routedEdges = placement.edges,
                edgeLabelSizes = edgeLabelSizes,
                context = context,
                elements = elements,
            )
        ) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return edges
        }
        addNodes(preparedDocument, nodeBounds, nodeTextSizes, context, elements)

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
                shape = SceneShapeKind.CollapsedGroup,
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
                    kind = SceneShapeKind.Rectangle,
                    fill = context.theme.groupFill,
                    stroke = context.theme.groupStroke,
                    strokeWidth = 1.2f,
                    cornerRadius = 12f,
                    zIndex = index,
                )
                elements += SceneText(
                    text = subgraph.label,
                    bounds = SceneRect(bounds.left + 14f, bounds.top, bounds.right - 14f, bounds.top + 22f),
                    color = context.theme.groupText,
                    fontSize = 13f,
                    weight = SceneTextWeight.Bold,
                    zIndex = index + 1,
                )
            }
    }

    private fun addEdges(
        document: FlowchartDocument,
        routedEdges: Map<Int, FlowDagreLayout.RoutedEdge>,
        edgeLabelSizes: Map<Int, SceneSize>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ): GMResult<Unit, MermaidError> {
        val paths = mutableListOf<ScenePath>()
        val labels = mutableListOf<SceneElement>()

        document.edges.forEachIndexed { index, edge ->
            if (edge.invisible) return@forEachIndexed
            val routed = routedEdges[index] ?: return@forEachIndexed
            val edgeStyle = resolveEdgeStyle(edge, document)
            paths += ScenePath(
                id = edge.id,
                points = routed.points,
                color = edgeStyle.stroke ?: context.theme.edge,
                strokeWidth = edgeStyle.strokeWidth ?: edge.thickness,
                strokePattern = if (edge.animated) SceneStrokePattern.Dashed else edgeStyle.strokePattern ?: edge.pattern,
                arrowStart = edge.arrowStart,
                arrowEnd = edge.arrowEnd,
                dashIntervals = if (edge.animated) listOf(9f, 5f) else emptyList(),
            )

            val label = edge.label ?: return@forEachIndexed
            val labelSize = edgeLabelSizes[index] ?: return@forEachIndexed
            val center = routed.labelAnchor
            val labelBounds = SceneRect(
                left = center.x - labelSize.width / 2f - 6f,
                top = center.y - labelSize.height / 2f - 3f,
                right = center.x + labelSize.width / 2f + 6f,
                bottom = center.y + labelSize.height / 2f + 3f,
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
        return GMResult.Ok(Unit)
    }

    private fun addNodes(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
        nodeTextSizes: Map<String, SceneSize>,
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
                val textSize = nodeTextSizes.getValue(id)
                elements += SceneText(
                    text = node.label,
                    bounds = nodeLabelBounds(node.shape, bounds, textSize, context.options.nodeVerticalPadding),
                    color = style.text ?: context.theme.nodeText,
                    fontSize = style.fontSize ?: context.options.fontSize,
                    weight = style.fontWeight ?: SceneTextWeight.Medium,
                )
            }
        }
    }

    private fun nodeLabelBounds(
        shape: SceneShapeKind,
        bounds: SceneRect,
        text: SceneSize,
        padding: Float,
    ): SceneRect {
        val centerY = when (shape) {
            SceneShapeKind.CollapsedGroup -> bounds.top + bounds.height * COLLAPSED_GROUP_LABEL_RATIO / 2f
            SceneShapeKind.DividedRectangle -> bounds.top + bounds.height / 6f + (bounds.height * 5f / 6f) / 2f
            SceneShapeKind.Triangle -> bounds.bottom - padding - text.height / 2f
            SceneShapeKind.FlippedTriangle -> bounds.top + padding + text.height / 2f
            SceneShapeKind.SlopedRectangle -> bounds.top + bounds.height / 3f + (bounds.height * 2f / 3f) / 2f
            SceneShapeKind.WindowPane -> bounds.center.y + 6.5f
            SceneShapeKind.MultiProcess -> bounds.center.y + 6f
            SceneShapeKind.MultiDocument -> bounds.center.y + 2f
            SceneShapeKind.Document,
            SceneShapeKind.LinedDocument, SceneShapeKind.TaggedDocument -> bounds.center.y - 4f
            else -> bounds.center.y
        }
        val centerX = when (shape) {
            SceneShapeKind.WindowPane -> bounds.center.x + 6.5f
            SceneShapeKind.MultiProcess, SceneShapeKind.MultiDocument -> bounds.center.x - 6f
            else -> bounds.center.x
        }
        return SceneRect(
            centerX - text.width / 2f, centerY - text.height / 2f,
            centerX + text.width / 2f, centerY + text.height / 2f,
        )
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
            SceneShapeKind.CollapsedGroup -> SceneSize(baseWidth + 20f, baseHeight + 40f)
            SceneShapeKind.Ellipse -> SceneSize(baseWidth + 20f, baseHeight + 8f)
            SceneShapeKind.Diamond -> {
                val side = baseWidth + baseHeight
                SceneSize(side, side)
            }
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
            -> SceneSize(baseWidth + 28f, baseHeight)
            SceneShapeKind.SlopedRectangle -> SceneSize(baseWidth, baseHeight * 1.5f)
            SceneShapeKind.DividedRectangle -> SceneSize(baseWidth, baseHeight * 1.2f)
            SceneShapeKind.Asymmetric -> SceneSize(baseWidth + 22f, baseHeight)
            SceneShapeKind.BowTieRectangle,
            SceneShapeKind.NotchedPentagon,
            -> SceneSize(baseWidth + 28f, baseHeight + 8f)
            SceneShapeKind.Hourglass -> SceneSize(30f, 30f)
            SceneShapeKind.Triangle,
            SceneShapeKind.FlippedTriangle,
            -> {
                val side = baseWidth + textSize.height
                SceneSize(side, side)
            }
            SceneShapeKind.Bolt -> SceneSize(52f, 66f)
            SceneShapeKind.BraceLeft,
            SceneShapeKind.BraceRight,
            SceneShapeKind.Braces,
            -> SceneSize(baseWidth + 28f, baseHeight + 12f)
            SceneShapeKind.Document,
            SceneShapeKind.LinedDocument,
            SceneShapeKind.TaggedDocument,
            -> SceneSize(baseWidth + 14f, baseHeight + 16f)
            SceneShapeKind.MultiDocument -> SceneSize(baseWidth + 26f, baseHeight + 28f)
            SceneShapeKind.MultiProcess -> SceneSize(baseWidth + 12f, baseHeight + 12f)
            SceneShapeKind.WindowPane -> SceneSize(baseWidth + 13f, baseHeight + 13f)
            SceneShapeKind.PaperTape -> SceneSize(baseWidth, baseHeight + 36f)
            SceneShapeKind.ForkJoin -> SceneSize(100f, 16f)
            else -> SceneSize(baseWidth, baseHeight)
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

    private companion object {
        const val COLLAPSED_GROUP_LABEL_RATIO = 0.55f
    }
}
