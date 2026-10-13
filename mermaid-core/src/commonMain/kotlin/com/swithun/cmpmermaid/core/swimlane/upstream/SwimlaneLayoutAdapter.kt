package com.swithun.cmpmermaid.core.swimlane.upstream

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.flowchart.FlowLayoutPlacement
import com.swithun.cmpmermaid.core.flowchart.FlowRoutedEdge
import com.swithun.cmpmermaid.core.flowchart.FlowchartDocument
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapeIntersectionPort
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapeLayout
import kotlin.math.abs

internal data class SwimlaneAdapterResult(
    val placement: FlowLayoutPlacement,
    val validationIssues: List<SwimlaneValidationIssue>,
)

/**
 * Platform adapter around Mermaid.js's LayoutData-based Swimlanes pipeline.
 *
 * Mermaid.js 12.0.0:
 * - swimlanes/index.ts -> prepareSwimlaneLayout
 * - swimlanes/layoutCore.ts -> runSwimlaneLayoutCore
 * - rendering-util/types.ts -> LayoutData conversion boundary
 */
internal object SwimlaneLayoutAdapter {
    fun layout(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
        edgeLabelSizes: Map<Int, SceneSize>,
        options: MermaidRenderOptions,
    ): GMResult<SwimlaneAdapterResult, MermaidError> {
        if (document.nodes.isEmpty()) {
            return GMResult.Ok(
                SwimlaneAdapterResult(
                    placement = FlowLayoutPlacement(emptyMap(), emptyMap(), emptyMap()),
                    validationIssues = emptyList(),
                ),
            )
        }

        val data = when (
            val converted = toLayoutData(document, nodeSizes, edgeLabelSizes, options)
        ) {
            is GMResult.Ok -> converted.value
            is GMResult.Err -> return converted
        }

        return try {
            prepareLayoutForSwimlanes(data)
            createEdgeLabelNodes(data)
            val coreResult = runSwimlaneLayoutCore(data)
            when (val placement = toPlacement(document, data, nodeShapeLayouts)) {
                is GMResult.Ok -> GMResult.Ok(
                    SwimlaneAdapterResult(
                        placement = placement.value,
                        validationIssues = coreResult.validationIssues,
                    ),
                )
                is GMResult.Err -> placement
            }
        } catch (failure: Exception) {
            GMResult.Err(
                MermaidError.Unexpected.from(failure, "Mermaid Swimlanes layout failed"),
            )
        }
    }

    private fun toLayoutData(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        edgeLabelSizes: Map<Int, SceneSize>,
        options: MermaidRenderOptions,
    ): GMResult<SwimlaneLayoutData, MermaidError> {
        val directParentByNodeId = buildMap {
            for (subgraph in document.subgraphs) {
                for (nodeId in subgraph.nodeIds) {
                    put(nodeId, subgraph.id)
                }
            }
        }
        val nodes = mutableListOf<SwimlaneNode>()
        for (subgraph in document.subgraphs) {
            nodes += SwimlaneNode(
                id = subgraph.id,
                parentId = subgraph.parentId,
                isGroup = true,
                padding = subgraph.padding.toDouble(),
                look = subgraph.look,
                colorIndex = subgraph.colorIndex,
            )
        }
        for ((id, node) in document.nodes) {
            val size = nodeSizes[id] ?: return GMResult.Err(
                MermaidError.Layout("Swimlane node '$id' has no measured size"),
            )
            if (!size.width.isFinite() || !size.height.isFinite()) {
                return GMResult.Err(
                    MermaidError.Layout("Swimlane node '$id' has a non-finite measured size"),
                )
            }
            nodes += SwimlaneNode(
                id = id,
                parentId = directParentByNodeId[id],
                width = size.width.toDouble(),
                height = size.height.toDouble(),
                shape = node.shape,
                look = node.look,
                colorIndex = node.colorIndex,
            )
        }

        val edges = mutableListOf<SwimlaneEdge>()
        for ((index, edge) in document.edges.withIndex()) {
            val hasLabel = !edge.label.isNullOrEmpty()
            val labelSize = if (hasLabel) {
                edgeLabelSizes[index] ?: return GMResult.Err(
                    MermaidError.Layout("Swimlane edge '${edge.id}' has no measured label size"),
                )
            } else {
                null
            }
            if (
                labelSize != null &&
                (!labelSize.width.isFinite() || !labelSize.height.isFinite())
            ) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${edge.id}' has a non-finite measured label size",
                    ),
                )
            }
            edges += SwimlaneEdge(
                id = edge.id,
                start = edge.from,
                end = edge.to,
                originalIndex = index,
                hasLabel = hasLabel,
                width = labelSize?.width?.toDouble(),
                height = labelSize?.height?.toDouble(),
            )
        }

        return GMResult.Ok(
            SwimlaneLayoutData(
                nodes = nodes,
                edges = edges,
                direction = SwimlaneDirection.from(document.direction),
                nodeGap = options.nodeSpacing.toDouble(),
                layerGap = options.rankSpacing.toDouble(),
                ignoreCrossLaneEdges = options.swimlane.ignoreCrossLaneEdges,
                optimizeRanksByCrossings = options.swimlane.optimizeRanksByCrossings,
                automaticLaneOrdering = options.swimlane.automaticLaneOrdering,
                look = options.look,
            ),
        )
    }

    private fun toPlacement(
        document: FlowchartDocument,
        data: SwimlaneLayoutData,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
    ): GMResult<FlowLayoutPlacement, MermaidError> {
        val nodeById = data.nodes.associateBy(SwimlaneNode::id)
        val nodeBounds = linkedMapOf<String, SceneRect>()
        for (id in document.nodes.keys) {
            val node = nodeById[id] ?: return GMResult.Err(
                MermaidError.Layout("Swimlane layout lost node '$id'"),
            )
            when (val bounds = sceneRect(node, "node '$id'")) {
                is GMResult.Ok -> nodeBounds[id] = bounds.value
                is GMResult.Err -> return bounds
            }
        }

        val subgraphBounds = linkedMapOf<String, SceneRect>()
        for (subgraph in document.subgraphs) {
            val node = nodeById[subgraph.id] ?: return GMResult.Err(
                MermaidError.Layout("Swimlane layout lost subgraph '${subgraph.id}'"),
            )
            when (val bounds = sceneRect(node, "subgraph '${subgraph.id}'")) {
                is GMResult.Ok -> subgraphBounds[subgraph.id] = bounds.value
                is GMResult.Err -> return bounds
            }
        }

        val originalEdges = data.edges
            .filterNot(SwimlaneEdge::isLayoutOnly)
            .associateBy(SwimlaneEdge::originalIndex)
        val routedEdges = linkedMapOf<Int, FlowRoutedEdge>()
        for ((index, sourceEdge) in document.edges.withIndex()) {
            val edge = originalEdges[index] ?: return GMResult.Err(
                MermaidError.Layout("Swimlane layout lost edge '${sourceEdge.id}'"),
            )
            val rawPoints = edge.points ?: return GMResult.Err(
                MermaidError.Layout("Swimlane edge '${sourceEdge.id}' was not routed"),
            )
            if (rawPoints.size < 2) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' has fewer than two routed points",
                    ),
                )
            }
            val layoutPoints = mutableListOf<ScenePoint>()
            for (point in rawPoints) {
                val scenePoint = scenePoint(point) ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' has a non-finite routed point",
                    ),
                )
                layoutPoints += scenePoint
            }
            val sourceBounds = nodeBounds[sourceEdge.from]
                ?: subgraphBounds[sourceEdge.from]
                ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' lost source '${sourceEdge.from}'",
                    ),
                )
            val destinationBounds = nodeBounds[sourceEdge.to]
                ?: subgraphBounds[sourceEdge.to]
                ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' lost destination '${sourceEdge.to}'",
                    ),
                )
            val points = clipEdgeEndpointsForRenderer(
                points = layoutPoints,
                sourceShapeLayout = nodeShapeLayouts[sourceEdge.from],
                sourceBounds = sourceBounds,
                destinationShapeLayout = nodeShapeLayouts[sourceEdge.to],
                destinationBounds = destinationBounds,
            )
            if (points.size < 2) {
                return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' collapsed below two routed points",
                    ),
                )
            }

            val labelNodeId = edge.labelNodeId
            val labelAnchor = if (labelNodeId == null) {
                halfLengthPoint(points)
            } else {
                val labelNode = nodeById[labelNodeId] ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' lost label node '$labelNodeId'",
                    ),
                )
                val x = labelNode.x ?: return GMResult.Err(
                        MermaidError.Layout(
                            "Swimlane edge '${sourceEdge.id}' label has no x coordinate",
                        ),
                    )
                val y = labelNode.y ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' label has no y coordinate",
                    ),
                )
                scenePoint(SwimlanePoint(x, y)) ?: return GMResult.Err(
                    MermaidError.Layout(
                        "Swimlane edge '${sourceEdge.id}' has a non-finite label anchor",
                    ),
                )
            }
            routedEdges[index] = FlowRoutedEdge(
                points = points,
                labelAnchor = labelAnchor,
                curveOverride = if (sourceEdge.curve == null || sourceEdge.curve == "basis") {
                    "rounded"
                } else {
                    null
                },
            )
        }

        return GMResult.Ok(
            FlowLayoutPlacement(
                nodeBounds = nodeBounds,
                subgraphBounds = subgraphBounds,
                edges = routedEdges,
            ),
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * rendering-util/rendering-elements/edges.js -> insertEdge, swimlane branch.
     */
    private fun clipEdgeEndpointsForRenderer(
        points: List<ScenePoint>,
        sourceShapeLayout: MermaidShapeLayout?,
        sourceBounds: SceneRect,
        destinationShapeLayout: MermaidShapeLayout?,
        destinationBounds: SceneRect,
    ): List<ScenePoint> {
        if (points.size == 2) {
            return listOf(
                MermaidShapeIntersectionPort.intersect(
                    shapeLayout = sourceShapeLayout,
                    bounds = sourceBounds,
                    toward = points[0],
                ),
                MermaidShapeIntersectionPort.intersect(
                    shapeLayout = destinationShapeLayout,
                    bounds = destinationBounds,
                    toward = points[1],
                ),
            )
        }

        val innerPoints = points.subList(1, points.lastIndex)
        val firstInner = innerPoints.first()
        val lastInner = innerPoints.last()
        val lastIsPinned = points.last().samePoint(lastInner, RendererIntersectionTolerance)
        val newFirst = MermaidShapeIntersectionPort.intersect(
            shapeLayout = sourceShapeLayout,
            bounds = sourceBounds,
            toward = firstInner,
        )
        val newLast = if (lastIsPinned) {
            lastInner
        } else {
            MermaidShapeIntersectionPort.intersect(
                shapeLayout = destinationShapeLayout,
                bounds = destinationBounds,
                toward = lastInner,
            )
        }

        return buildList {
            if (!newFirst.samePoint(firstInner, RendererIntersectionTolerance)) {
                add(newFirst)
            }
            addAll(innerPoints)
            if (!newLast.samePoint(lastInner, RendererIntersectionTolerance)) {
                add(newLast)
            }
        }
    }

    private fun ScenePoint.samePoint(other: ScenePoint, tolerance: Float): Boolean =
        abs(x - other.x) < tolerance && abs(y - other.y) < tolerance

    private fun sceneRect(
        node: SwimlaneNode,
        owner: String,
    ): GMResult<SceneRect, MermaidError> {
        val x = node.x ?: return GMResult.Err(
            MermaidError.Layout("Swimlane $owner has no x coordinate"),
        )
        val y = node.y ?: return GMResult.Err(
            MermaidError.Layout("Swimlane $owner has no y coordinate"),
        )
        val values = listOf(x, y, node.width, node.height)
        if (values.any { value -> !value.isFinite() }) {
            return GMResult.Err(
                MermaidError.Layout("Swimlane $owner has non-finite geometry"),
            )
        }
        val left = (x - node.width / 2.0).toFloat()
        val top = (y - node.height / 2.0).toFloat()
        val right = (x + node.width / 2.0).toFloat()
        val bottom = (y + node.height / 2.0).toFloat()
        if (!left.isFinite() || !top.isFinite() || !right.isFinite() || !bottom.isFinite()) {
            return GMResult.Err(
                MermaidError.Layout("Swimlane $owner exceeds SceneGraph coordinate range"),
            )
        }
        return GMResult.Ok(SceneRect(left, top, right, bottom))
    }

    private fun scenePoint(point: SwimlanePoint): ScenePoint? {
        if (!point.x.isFinite() || !point.y.isFinite()) return null
        val x = point.x.toFloat()
        val y = point.y.toFloat()
        return if (x.isFinite() && y.isFinite()) ScenePoint(x, y) else null
    }

    private fun halfLengthPoint(points: List<ScenePoint>): ScenePoint {
        val lengths = points.zipWithNext { first, second ->
            kotlin.math.abs(second.x - first.x) + kotlin.math.abs(second.y - first.y)
        }
        val target = lengths.sum() / 2f
        var traversed = 0f
        for ((index, length) in lengths.withIndex()) {
            if (length > 0f && traversed + length >= target) {
                val ratio = (target - traversed) / length
                return ScenePoint(
                    x = points[index].x + (points[index + 1].x - points[index].x) * ratio,
                    y = points[index].y + (points[index + 1].y - points[index].y) * ratio,
                )
            }
            traversed += length
        }
        return points.last()
    }

    private const val RendererIntersectionTolerance = 0.5f
}
