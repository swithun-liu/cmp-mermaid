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

        val placement = if (FlowCompoundLayout.isRequired(preparedDocument)) {
            when (
                val compound = FlowCompoundLayout.layout(
                    document = preparedDocument,
                    nodeSizes = nodeSizes,
                    edgeLabelSizes = edgeLabelSizes,
                    options = context.options,
                )
            ) {
                is GMResult.Ok -> compound.value
                is GMResult.Err -> return compound
            }
        } else {
            val rankingDocument = preparedDocument.copy(
                edges = preparedDocument.edges.map { edge ->
                    edge.copy(
                        from = resolveSubgraphEndpoint(preparedDocument, edge.from, outgoing = true),
                        to = resolveSubgraphEndpoint(preparedDocument, edge.to, outgoing = false),
                    )
                },
            )
            val layering = FlowLayering.build(rankingDocument)
            val nodeBounds = FlowNodePlacer.place(
                direction = preparedDocument.direction,
                layers = layering.layers,
                sizes = nodeSizes,
                edgeLabelSizes = edgeLabelSizes,
                edges = rankingDocument.edges,
                ranks = layering.ranks,
                options = context.options,
            )
            FlowCompoundLayout.Result(
                nodeBounds = nodeBounds,
                subgraphBounds = calculateSubgraphBounds(preparedDocument, nodeBounds),
            )
        }
        val nodeBounds = placement.nodeBounds
        if (nodeBounds.size != preparedDocument.nodes.size) {
            return GMResult.Err(MermaidError.Layout("Not every flowchart node was positioned"))
        }
        val subgraphBounds = placement.subgraphBounds
        val endpointBounds = nodeBounds + subgraphBounds

        val elements = mutableListOf<SceneElement>()
        addSubgraphs(preparedDocument, subgraphBounds, context, elements)
        addEdges(preparedDocument, endpointBounds, edgeLabelSizes, context, elements)
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
        edgeLabelSizes: Map<Int, SceneSize>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        val routedEdges = FlowEdgeRouter.route(document, nodeBounds, edgeLabelSizes)
        val paths = mutableListOf<ScenePath>()
        val labels = mutableListOf<SceneElement>()

        document.edges.forEachIndexed { index, edge ->
            val routed = routedEdges[index] ?: return@forEachIndexed
            val edgeStyle = resolveEdgeStyle(edge, document)
            paths += ScenePath(
                id = edge.id,
                points = routed.points,
                color = edgeStyle.stroke ?: context.theme.edge,
                strokeWidth = edgeStyle.strokeWidth ?: edge.thickness,
                strokePattern = edgeStyle.strokePattern ?: edge.pattern,
                arrowStart = edge.arrowStart,
                arrowEnd = edge.arrowEnd,
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
                    bounds = if (node.shape == SceneShapeKind.CollapsedGroup) {
                        SceneRect(
                            left = bounds.left,
                            top = bounds.top,
                            right = bounds.right,
                            bottom = bounds.top + bounds.height * COLLAPSED_GROUP_LABEL_RATIO,
                        )
                    } else {
                        bounds
                    },
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
            SceneShapeKind.CollapsedGroup -> SceneSize(baseWidth + 20f, baseHeight + 40f)
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
