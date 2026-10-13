package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max
import kotlin.math.min

internal const val DefaultSwimlaneId: String = "__swimlane_default__"

private const val TopLaneTitleBandHeight: Double = 21.0
private const val MinimumTopLaneHorizontalPadding: Double = 20.0

// Mermaid.js 12.0.0: swimlanes/helpers.ts -> topLaneHorizontalPadding.
private fun topLaneHorizontalPadding(lane: SwimlaneNode): Double =
    max(lane.padding, MinimumTopLaneHorizontalPadding)

// Mermaid.js 12.0.0: swimlanes/helpers.ts -> assignTopLaneTitleRect.
private fun assignTopLaneTitleRect(lane: SwimlaneNode) {
    val centerX = lane.x
    val centerY = lane.y
    val contentTop = lane.swimlaneContentTop
    if (
        centerX == null ||
        centerY == null ||
        contentTop == null ||
        !centerX.isFinite() ||
        !centerY.isFinite() ||
        !lane.width.isFinite() ||
        !lane.height.isFinite() ||
        !contentTop.isFinite() ||
        lane.width <= 0.0 ||
        lane.height <= 0.0
    ) {
        lane.groupTitleRect = null
        return
    }

    val top = centerY - lane.height / 2.0
    val headerBottom = min(contentTop, centerY + lane.height / 2.0)
    val titleHeight = min(TopLaneTitleBandHeight, max(0.0, headerBottom - top))
    val bottom = top + titleHeight
    if (bottom <= top) {
        lane.groupTitleRect = null
        return
    }

    lane.groupTitleRect = SwimlaneRect(
        left = centerX - lane.width / 2.0,
        right = centerX + lane.width / 2.0,
        top = top,
        bottom = bottom,
    )
}

// Mermaid.js 12.0.0: swimlanes/helpers.ts -> prepareLayoutForSwimlanes.
internal fun prepareLayoutForSwimlanes(layout: SwimlaneLayoutData) {
    for (node in layout.nodes) {
        if (node.isGroup && node.parentId == null) {
            node.isSwimlane = true
            node.direction = layout.direction
        }
    }

    val looseNodes = layout.nodes.filter { node -> !node.isGroup && node.parentId == null }
    if (looseNodes.isEmpty()) return

    var defaultLane = layout.nodes.find { node -> node.id == DefaultSwimlaneId }
    if (defaultLane == null) {
        defaultLane = SwimlaneNode(
            id = DefaultSwimlaneId,
            isGroup = true,
            isSwimlane = true,
            padding = 20.0,
            look = layout.look,
            colorIndex = layout.nodes.maxOfOrNull { node -> node.colorIndex ?: -1 }?.plus(1) ?: 0,
            direction = layout.direction,
        )
        layout.nodes += defaultLane
    } else if (defaultLane.isGroup) {
        defaultLane.isSwimlane = true
        defaultLane.direction = layout.direction
    }
    for (node in looseNodes) {
        node.parentId = DefaultSwimlaneId
    }
}

// Mermaid.js 12.0.0: swimlanes/helpers.ts -> toGraphView.
internal fun toGraphView(layout: SwimlaneLayoutData): SwimlaneGraph {
    val nodeById = linkedMapOf<String, SwimlaneNode>()
    layout.nodes.forEach { node -> nodeById[node.id] = node }

    val edges = buildList {
        layout.edges.forEach { edge ->
            val src = edge.start ?: return@forEach
            val dst = edge.end ?: return@forEach
            if (edge.labelNodeId != null) return@forEach
            add(
                SwimlaneEdgeRef(
                    id = edge.id,
                    src = src,
                    dst = dst,
                    ref = edge,
                ),
            )
        }
    }

    val groupNodes = layout.nodes.filter(SwimlaneNode::isGroup)
    val nonGroupNodes = layout.nodes.filterNot(SwimlaneNode::isGroup)
    val nodes = (groupNodes.asReversed() + nonGroupNodes).map(SwimlaneNode::id)
    return SwimlaneGraph(
        nodes = nodes,
        edges = edges,
        layout = layout,
        nodeById = nodeById,
    )
}

/**
 * Mermaid.js 12.0.0:
 * swimlanes/helpers.ts -> writeBackToLayoutData.
 */
internal fun writeBackToLayoutData(
    graph: SwimlaneGraph,
    ordered: SwimlaneOrderedLayers,
    coordinates: SwimlaneCoordinates,
    layerGap: Double = 100.0,
    nodeGap: Double = 40.0,
) {
    val layout = graph.layout
    val nodeMap = graph.nodeById

    ordered.layers.forEachIndexed { layerIndex, layer ->
        layer.forEachIndexed { orderIndex, id ->
            val node = nodeMap[id] ?: return@forEachIndexed
            node.layer = layerIndex
            node.order = orderIndex
            node.x = coordinates.x[id] ?: orderIndex * nodeGap
            node.y = coordinates.y[id] ?: layerIndex * layerGap
        }
    }

    data class GroupBounds(
        val minimumX: Double,
        val maximumX: Double,
        val minimumY: Double,
        val maximumY: Double,
    )

    val allNodes = layout.nodes
    val groupBounds = linkedMapOf<String, GroupBounds>()
    val topLevelGroups = mutableListOf<SwimlaneNode>()
    for (group in allNodes) {
        if (!group.isGroup) continue
        if (group.parentId == null) topLevelGroups += group

        val children = allNodes.filter { node -> node.parentId == group.id }
        var minimumX = Double.POSITIVE_INFINITY
        var maximumX = Double.NEGATIVE_INFINITY
        var minimumY = Double.POSITIVE_INFINITY
        var maximumY = Double.NEGATIVE_INFINITY
        for (child in children) {
            val childX = child.x ?: coordinates.x[child.id]
            val childY = child.y ?: coordinates.y[child.id]
            if (childX != null && childY != null) {
                minimumX = min(minimumX, childX - child.width / 2.0)
                maximumX = max(maximumX, childX + child.width / 2.0)
                minimumY = min(minimumY, childY - child.height / 2.0)
                maximumY = max(maximumY, childY + child.height / 2.0)
            }
        }

        if (
            minimumX == Double.POSITIVE_INFINITY ||
            minimumY == Double.POSITIVE_INFINITY
        ) {
            group.x = group.x ?: 0.0
            group.y = group.y ?: 0.0
        } else {
            val horizontalPadding = if (group.parentId != null) {
                group.padding
            } else {
                2.0 * topLaneHorizontalPadding(group)
            }
            val width = max(0.0, maximumX - minimumX) + horizontalPadding
            val height = max(0.0, maximumY - minimumY) + group.padding
            group.x = (minimumX + maximumX) / 2.0
            group.y = (minimumY + maximumY) / 2.0
            group.width = width
            group.height = height
            groupBounds[group.id] = GroupBounds(
                minimumX = minimumX,
                maximumX = maximumX,
                minimumY = minimumY,
                maximumY = maximumY,
            )
        }
    }

    if (topLevelGroups.isEmpty() || groupBounds.isEmpty()) return

    var globalMinimumY = Double.POSITIVE_INFINITY
    var globalMaximumY = Double.NEGATIVE_INFINITY
    var maximumPadding = 0.0
    for (lane in topLevelGroups) {
        maximumPadding = max(maximumPadding, lane.padding)
        val bounds = groupBounds[lane.id] ?: continue
        globalMinimumY = min(globalMinimumY, bounds.minimumY)
        globalMaximumY = max(globalMaximumY, bounds.maximumY)
    }
    if (
        globalMinimumY == Double.POSITIVE_INFINITY ||
        globalMaximumY == Double.NEGATIVE_INFINITY
    ) {
        return
    }

    val contentHeight = max(0.0, globalMaximumY - globalMinimumY)
    val verticalMargin = max(maximumPadding, 36.0)
    val laneHeight = contentHeight + 2.0 * verticalMargin
    val centerY = (globalMinimumY + globalMaximumY) / 2.0
    for (lane in topLevelGroups) {
        lane.y = centerY
        lane.height = laneHeight
        lane.swimlaneContentTop = globalMinimumY
    }

    val sortedLanes = topLevelGroups.sortedBy { lane -> lane.x ?: 0.0 }
    val laneIds = mutableListOf<String>()
    val centers = mutableListOf<Double>()
    val baseWidths = mutableListOf<Double>()
    for (lane in sortedLanes) {
        val bounds = groupBounds[lane.id] ?: continue
        val contentWidth = max(0.0, bounds.maximumX - bounds.minimumX) +
            2.0 * topLaneHorizontalPadding(lane)
        laneIds += lane.id
        centers += (bounds.minimumX + bounds.maximumX) / 2.0
        baseWidths += contentWidth
    }

    val count = laneIds.size
    if (count == 0) return

    val laneWidths = linkedMapOf<String, Double>()
    if (count == 1) {
        laneWidths[laneIds[0]] = baseWidths[0]
    } else {
        val distances = MutableList(count - 1) { index -> centers[index + 1] - centers[index] }
        val offsets = MutableList(count) { 0.0 }
        for (index in 0 until count - 1) {
            offsets[index + 1] = 2.0 * distances[index] - offsets[index]
        }

        var lowerBound = 0.0
        var upperBound = Double.POSITIVE_INFINITY
        for (index in 0 until count) {
            if (index % 2 == 0) {
                lowerBound = max(lowerBound, baseWidths[index] - offsets[index])
            } else {
                upperBound = min(upperBound, offsets[index] - baseWidths[index])
            }
        }

        val x = if (lowerBound <= upperBound) {
            (lowerBound + upperBound) / 2.0
        } else {
            lowerBound
        }
        for (index in 0 until count) {
            val width = offsets[index] + if (index % 2 == 0) x else -x
            laneWidths[laneIds[index]] = max(baseWidths[index], width)
        }
    }

    for (lane in topLevelGroups) {
        laneWidths[lane.id]?.let { width -> lane.width = width }
        assignTopLaneTitleRect(lane)
    }
}
