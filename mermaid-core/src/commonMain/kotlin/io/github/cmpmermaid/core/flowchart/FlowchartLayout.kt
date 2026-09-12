package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.SceneElement
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePathCommand
import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneSize
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricsRequest
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.MermaidEdgePathPort
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapeLayout
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapePort
import kotlin.math.max
import kotlin.math.min

internal class FlowchartLayout {
    fun layout(
        document: FlowchartDocument,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val nodeSizes = linkedMapOf<String, SceneSize>()
        val nodeTextSizes = linkedMapOf<String, SceneSize>()
        val nodeShapeLayouts = linkedMapOf<String, MermaidShapeLayout>()
        for ((id, node) in document.nodes) {
            val style = resolveStyle(node)
            val measured = try {
                context.textMetrics.measure(
                    TextMetricsRequest(
                        text = node.label,
                        fontSize = style.fontSize ?: context.options.fontSize,
                        maxWidth = context.options.wrappingWidth,
                        weight = style.fontWeight ?: SceneTextWeight.Medium,
                        spans = node.labelSpans,
                    ),
                )
            } catch (failure: Throwable) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Text measurement failed for '$id': ${failure.message ?: "unknown error"}",
                    ),
                )
            }
            val textSize = SceneSize(measured.width, measured.height)
            nodeTextSizes[id] = textSize
            val shapeLayout = when (
                val shapeResult = MermaidShapePort.layout(
                    node = node,
                    measuredLabel = textSize,
                    direction = document.direction,
                )
            ) {
                is GMResult.Ok -> shapeResult.value
                is GMResult.Err -> return shapeResult
            }
            nodeShapeLayouts[id] = shapeLayout
            nodeSizes[id] = shapeLayout.size
        }
        val edgeLabelSizes = linkedMapOf<Int, SceneSize>()
        for ((index, edge) in document.edges.withIndex()) {
            val label = edge.label ?: continue
            val edgeStyle = resolveEdgeStyle(edge)
            val measured = try {
                context.textMetrics.measure(
                    TextMetricsRequest(
                        text = label,
                        fontSize = edgeStyle.fontSize ?: 13f,
                        maxWidth = 180f,
                        weight = edgeStyle.fontWeight ?: SceneTextWeight.Normal,
                        spans = edge.labelSpans,
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
                document = document,
                nodeSizes = nodeSizes,
                nodeShapeLayouts = nodeShapeLayouts,
                edgeLabelSizes = edgeLabelSizes,
                options = context.options,
            )
        ) {
            is GMResult.Ok -> dagre.value
            is GMResult.Err -> return dagre
        }
        val nodeBounds = placement.nodeBounds
        if (nodeBounds.size != document.nodes.size) {
            return GMResult.Err(MermaidError.Layout("Not every flowchart node was positioned"))
        }
        val subgraphBounds = placement.subgraphBounds

        val elements = mutableListOf<SceneElement>()
        when (val subgraphs = addSubgraphs(document, subgraphBounds, context, elements)) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return subgraphs
        }
        when (
            val edges = addEdges(
                document = document,
                routedEdges = placement.edges,
                edgeLabelSizes = edgeLabelSizes,
                context = context,
                elements = elements,
            )
        ) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return edges
        }
        addNodes(document, nodeBounds, nodeTextSizes, nodeShapeLayouts, context, elements)
        when (val title = addDiagramTitle(document, context, elements)) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return title
        }

        return GMResult.Ok(normalizeScene(elements, document, context))
    }

    private fun addSubgraphs(
        document: FlowchartDocument,
        subgraphBounds: Map<String, SceneRect>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ): GMResult<Unit, MermaidError> {
        document.subgraphs
            .sortedByDescending { it.nodeIds.size }
            .forEachIndexed { index, subgraph ->
                val bounds = subgraphBounds[subgraph.id]
                    ?: return@forEachIndexed
                val style = subgraph.inlineStyle ?: FlowNodeStyle()
                val measured = try {
                    context.textMetrics.measure(
                        TextMetricsRequest(
                            text = subgraph.label,
                            fontSize = style.fontSize ?: context.options.fontSize,
                            maxWidth = bounds.width.coerceAtLeast(1f),
                            weight = style.fontWeight ?: SceneTextWeight.Normal,
                            spans = subgraph.labelSpans,
                        ),
                    )
                } catch (failure: Throwable) {
                    return GMResult.Err(
                        MermaidError.Layout(
                            "Subgraph label measurement failed for '${subgraph.id}': " +
                                (failure.message ?: "unknown error"),
                        ),
                    )
                }
                val renderedWidth = max(bounds.width, measured.width + subgraph.padding)
                val renderedBounds = SceneRect(
                    left = bounds.center.x - renderedWidth / 2f,
                    top = bounds.top,
                    right = bounds.center.x + renderedWidth / 2f,
                    bottom = bounds.bottom,
                )
                elements += SceneShape(
                    id = "subgraph_${subgraph.id}",
                    bounds = renderedBounds,
                    kind = SceneShapeKind.Rectangle,
                    fill = style.fill ?: context.theme.groupFill,
                    stroke = style.stroke ?: context.theme.groupStroke,
                    strokeWidth = style.strokeWidth ?: 1f,
                    strokePattern = style.strokePattern ?: SceneStrokePattern.Solid,
                    dashIntervals = style.dashIntervals,
                    cornerRadius = 0f,
                    zIndex = index,
                )
                elements += SceneText(
                    text = subgraph.label,
                    bounds = SceneRect(
                        left = renderedBounds.center.x - measured.width / 2f,
                        top = renderedBounds.top,
                        right = renderedBounds.center.x + measured.width / 2f,
                        bottom = renderedBounds.top + measured.height,
                    ),
                    color = style.text ?: context.theme.groupText,
                    fontSize = style.fontSize ?: context.options.fontSize,
                    weight = style.fontWeight ?: SceneTextWeight.Normal,
                    spans = subgraph.labelSpans,
                    zIndex = index + 1,
                )
            }
        return GMResult.Ok(Unit)
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
            val edgeStyle = resolveEdgeStyle(edge)
            val curve = edge.curve ?: context.options.curve
            val commands = when (
                val generatedPath = MermaidEdgePathPort.generate(
                    points = routed.points,
                    curve = curve,
                    arrowStart = edge.arrowStart,
                    arrowEnd = edge.arrowEnd,
                )
            ) {
                is GMResult.Ok -> generatedPath.value
                is GMResult.Err -> return generatedPath
            }
            paths += ScenePath(
                id = edge.id,
                points = routed.points,
                commands = commands,
                color = edgeStyle.stroke ?: context.theme.edge,
                strokeWidth = edgeStyle.strokeWidth ?: edge.thickness,
                strokePattern = if (edge.animated) SceneStrokePattern.Dashed else edgeStyle.strokePattern ?: edge.pattern,
                arrowStart = edge.arrowStart,
                arrowEnd = edge.arrowEnd,
                curve = curve,
                look = edge.look,
                animated = edge.animated,
                dashIntervals = if (edge.animated) {
                    listOf(9f, 5f)
                } else {
                    edgeStyle.dashIntervals
                },
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
                fill = edgeStyle.labelBackground ?: context.theme.edgeLabelFill,
                stroke = edgeStyle.labelBackground ?: context.theme.edgeLabelFill,
                cornerRadius = 4f,
                zIndex = 6,
            )
            labels += SceneText(
                text = label,
                bounds = labelBounds,
                color = edgeStyle.text ?: context.theme.nodeText,
                fontSize = edgeStyle.fontSize ?: 13f,
                weight = edgeStyle.fontWeight ?: SceneTextWeight.Normal,
                spans = edge.labelSpans,
                zIndex = 7,
            )
        }
        elements += paths
        elements += labels
        return GMResult.Ok(Unit)
    }

    private fun addNodes(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
        nodeTextSizes: Map<String, SceneSize>,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ) {
        document.nodes.forEach { (id, node) ->
            val bounds = nodeBounds.getValue(id)
            val style = resolveStyle(node)
            val shapeLayout = nodeShapeLayouts.getValue(id)
            elements += SceneShape(
                id = id,
                bounds = bounds,
                kind = node.shape,
                geometry = shapeLayout.geometry,
                fill = style.fill ?: context.theme.nodeFill,
                stroke = style.stroke ?: context.theme.nodeStroke,
                strokeWidth = style.strokeWidth ?: 1.5f,
                strokePattern = style.strokePattern ?: io.github.cmpmermaid.core.SceneStrokePattern.Solid,
                dashIntervals = style.dashIntervals,
                cornerRadius = 9f,
            )
            if (shapeLayout.showsLabel) {
                val textSize = nodeTextSizes.getValue(id)
                val center = bounds.center
                val labelCenter = ScenePoint(
                    x = center.x + shapeLayout.labelOffset.x,
                    y = center.y + shapeLayout.labelOffset.y,
                )
                elements += SceneText(
                    text = node.label,
                    bounds = SceneRect(
                        left = labelCenter.x - textSize.width / 2f,
                        top = labelCenter.y - textSize.height / 2f,
                        right = labelCenter.x + textSize.width / 2f,
                        bottom = labelCenter.y + textSize.height / 2f,
                    ),
                    color = style.text ?: context.theme.nodeText,
                    fontSize = style.fontSize ?: context.options.fontSize,
                    weight = style.fontWeight ?: SceneTextWeight.Medium,
                    spans = node.labelSpans,
                )
            }
        }
    }

    private fun resolveStyle(node: FlowNode): FlowNodeStyle =
        node.inlineStyle ?: FlowNodeStyle()

    private fun resolveEdgeStyle(edge: FlowEdge): FlowNodeStyle =
        edge.inlineStyle ?: FlowNodeStyle()

    /**
     * Native equivalent of utils.insertTitle() in Mermaid's unified Flowchart
     * renderer. Compose uses top-left text bounds rather than an SVG baseline,
     * so the measured text height is applied above the same top margin.
     */
    private fun addDiagramTitle(
        document: FlowchartDocument,
        context: MermaidRenderContext,
        elements: MutableList<SceneElement>,
    ): GMResult<Unit, MermaidError> {
        val title = document.title?.takeIf(String::isNotBlank)
            ?: return GMResult.Ok(Unit)
        val graphBounds = elements
            .mapNotNull(::elementBounds)
            .reduceOrNull(SceneRect::union)
            ?: return GMResult.Ok(Unit)
        val measured = try {
            context.textMetrics.measure(
                TextMetricsRequest(
                    text = title,
                    fontSize = TITLE_FONT_SIZE,
                    maxWidth = graphBounds.width.coerceAtLeast(1f),
                    weight = SceneTextWeight.Normal,
                ),
            )
        } catch (failure: Throwable) {
            return GMResult.Err(
                MermaidError.Layout(
                    "Title measurement failed: ${failure.message ?: "unknown error"}",
                ),
            )
        }
        val centerX = graphBounds.left + graphBounds.width / 2f
        val bottom = graphBounds.top - context.options.titleTopMargin
        elements += SceneText(
            text = title,
            bounds = SceneRect(
                left = centerX - measured.width / 2f,
                top = bottom - measured.height,
                right = centerX + measured.width / 2f,
                bottom = bottom,
            ),
            color = context.theme.nodeText,
            fontSize = TITLE_FONT_SIZE,
            weight = SceneTextWeight.Normal,
            zIndex = 30,
        )
        return GMResult.Ok(Unit)
    }

    private fun normalizeScene(
        elements: List<SceneElement>,
        document: FlowchartDocument,
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
            title = document.title,
            accessibilityTitle = document.accessibilityTitle,
            accessibilityDescription = document.accessibilityDescription,
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
            commands = commands.map { command -> command.translate(dx, dy) },
        )
    }

    private fun ScenePathCommand.translate(
        dx: Float,
        dy: Float,
    ): ScenePathCommand = when (this) {
        is ScenePathCommand.MoveTo -> copy(point = point.translate(dx, dy))
        is ScenePathCommand.LineTo -> copy(point = point.translate(dx, dy))
        is ScenePathCommand.QuadraticTo -> copy(
            control = control.translate(dx, dy),
            end = end.translate(dx, dy),
        )
        is ScenePathCommand.CubicTo -> copy(
            control1 = control1.translate(dx, dy),
            control2 = control2.translate(dx, dy),
            end = end.translate(dx, dy),
        )
    }

    private fun ScenePoint.translate(dx: Float, dy: Float): ScenePoint =
        ScenePoint(x + dx, y + dy)

    private companion object {
        const val TITLE_FONT_SIZE = 18f
    }
}
