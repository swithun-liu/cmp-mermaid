package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.max
import kotlin.math.min

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/lrTransform.ts.
 */
private fun buildDirectionNodeMap(
    nodes: List<SwimlaneNode>,
): Map<String, SwimlaneNode> = nodes.associateBy(SwimlaneNode::id)

private fun resolveTopLevelGroupId(
    node: SwimlaneNode,
    nodeById: Map<String, SwimlaneNode>,
): String? {
    var parentId = node.parentId
    var topLevelGroupId: String? = null
    while (parentId != null) {
        val parent = nodeById[parentId]
        if (parent?.isGroup != true) break
        topLevelGroupId = parent.id
        parentId = parent.parentId
    }
    return topLevelGroupId
}

private fun groupDepth(
    group: SwimlaneNode,
    nodeById: Map<String, SwimlaneNode>,
): Int {
    var depth = 0
    var parentId = group.parentId
    while (parentId != null) {
        val parent = nodeById[parentId]
        if (parent?.isGroup != true) break
        depth++
        parentId = parent.parentId
    }
    return depth
}

private data class ChildBounds(
    val minimumX: Double,
    val maximumX: Double,
    val minimumY: Double,
    val maximumY: Double,
)

private fun boundsForChildren(children: List<SwimlaneNode>): ChildBounds? {
    var minimumX = Double.POSITIVE_INFINITY
    var maximumX = Double.NEGATIVE_INFINITY
    var minimumY = Double.POSITIVE_INFINITY
    var maximumY = Double.NEGATIVE_INFINITY
    for (child in children) {
        val centerX = child.x ?: continue
        val centerY = child.y ?: continue
        minimumX = min(minimumX, centerX - child.width / 2.0)
        maximumX = max(maximumX, centerX + child.width / 2.0)
        minimumY = min(minimumY, centerY - child.height / 2.0)
        maximumY = max(maximumY, centerY + child.height / 2.0)
    }
    if (
        minimumX == Double.POSITIVE_INFINITY ||
        minimumY == Double.POSITIVE_INFINITY
    ) {
        return null
    }
    return ChildBounds(minimumX, maximumX, minimumY, maximumY)
}

private fun applyGroupBounds(
    group: SwimlaneNode,
    bounds: ChildBounds,
) {
    val padding = group.padding
    group.x = (bounds.minimumX + bounds.maximumX) / 2.0
    group.y = (bounds.minimumY + bounds.maximumY) / 2.0
    group.width = max(0.0, bounds.maximumX - bounds.minimumX) + padding
    group.height = max(0.0, bounds.maximumY - bounds.minimumY) + padding
}

private fun recomputeNestedGroupBounds(nodes: List<SwimlaneNode>) {
    val nodeById = buildDirectionNodeMap(nodes)
    val groupsByDepth = nodes
        .filter { node -> node.isGroup && node.parentId != null }
        .sortedByDescending { group -> groupDepth(group, nodeById) }
    for (group in groupsByDepth) {
        val bounds = boundsForChildren(nodes.filter { node -> node.parentId == group.id })
        if (bounds != null) applyGroupBounds(group, bounds)
    }
}

private enum class MirrorAxis {
    X,
    Y,
}

private fun mirrorAxis(
    layout: SwimlaneLayoutData,
    axis: MirrorAxis,
): Boolean {
    val contentNodes = layout.nodes.filterNot(SwimlaneNode::isGroup)
    var minimum = Double.POSITIVE_INFINITY
    var maximum = Double.NEGATIVE_INFINITY
    for (node in contentNodes) {
        val value = if (axis == MirrorAxis.X) node.x else node.y
        if (value == null) continue
        minimum = min(minimum, value)
        maximum = max(maximum, value)
    }
    if (!minimum.isFinite() || !maximum.isFinite()) return false

    fun mirror(value: Double): Double = minimum + maximum - value
    for (node in layout.nodes) {
        if (axis == MirrorAxis.X) {
            node.x = node.x?.let(::mirror)
            node.groupTitleRect = node.groupTitleRect?.let { rect ->
                rect.copy(
                    left = mirror(rect.right),
                    right = mirror(rect.left),
                )
            }
        } else {
            node.y = node.y?.let(::mirror)
            node.groupTitleRect = node.groupTitleRect?.let { rect ->
                rect.copy(
                    top = mirror(rect.bottom),
                    bottom = mirror(rect.top),
                )
            }
        }
    }
    for (edge in layout.edges) {
        edge.points = edge.points?.mapTo(mutableListOf()) { point ->
            if (axis == MirrorAxis.X) {
                point.copy(x = mirror(point.x))
            } else {
                point.copy(y = mirror(point.y))
            }
        }
    }
    return true
}

internal fun applyBtDirectionTransform(layout: SwimlaneLayoutData): Boolean {
    if (layout.nodes.none { node -> !node.isGroup }) return true
    return mirrorAxis(layout, MirrorAxis.Y)
}

internal fun applyLrDirectionTransform(
    layout: SwimlaneLayoutData,
    direction: SwimlaneDirection = SwimlaneDirection.LR,
): Boolean {
    val contentNodes = layout.nodes.filterNot(SwimlaneNode::isGroup)
    var minimumX = Double.POSITIVE_INFINITY
    var minimumY = Double.POSITIVE_INFINITY
    for (node in contentNodes) {
        minimumX = min(minimumX, node.x ?: 0.0)
        minimumY = min(minimumY, node.y ?: 0.0)
    }
    if (!minimumX.isFinite() || !minimumY.isFinite()) return false

    val titleBandSize = 36.0
    val totalWidth = contentNodes.sumOf(SwimlaneNode::width)
    val totalHeight = contentNodes.sumOf(SwimlaneNode::height)
    val averageWidth = totalWidth / contentNodes.size
    val averageHeight = totalHeight / contentNodes.size
    val horizontalScaleFactor =
        if (averageHeight > 0.0) max(1.0, averageWidth / averageHeight) else 1.0

    for (node in contentNodes) {
        val originalX = node.x ?: 0.0
        val originalY = node.y ?: 0.0
        node.x = (originalY - minimumY) * horizontalScaleFactor + titleBandSize
        node.y = originalX - minimumX
    }
    for (edge in layout.edges) {
        edge.points = edge.points?.mapTo(mutableListOf()) { point ->
            SwimlanePoint(
                x = (point.y - minimumY) * horizontalScaleFactor + titleBandSize,
                y = point.x - minimumX,
            )
        }
    }

    recomputeNestedGroupBounds(layout.nodes)
    val laneNodes = layout.nodes.filter { node -> node.isGroup && node.parentId == null }
    if (laneNodes.isEmpty()) {
        if (direction == SwimlaneDirection.RL) mirrorAxis(layout, MirrorAxis.X)
        return true
    }

    val nodeById = buildDirectionNodeMap(layout.nodes)
    val childrenByLane = linkedMapOf<String, MutableList<SwimlaneNode>>()
    for (node in layout.nodes) {
        if (node.isGroup) continue
        val laneId = resolveTopLevelGroupId(node, nodeById) ?: continue
        childrenByLane.getOrPut(laneId, ::mutableListOf) += node
    }

    val maximumPadding = laneNodes.maxOfOrNull(SwimlaneNode::padding) ?: 0.0
    data class LaneBounds(
        val lane: SwimlaneNode,
        val contentTop: Double,
        val contentBottom: Double,
        val centerY: Double,
    )

    val laneBounds = mutableListOf<LaneBounds>()
    var globalMinimumChildX = Double.POSITIVE_INFINITY
    var globalMaximumChildX = Double.NEGATIVE_INFINITY
    for (lane in laneNodes) {
        val bounds = boundsForChildren(childrenByLane[lane.id].orEmpty()) ?: continue
        globalMinimumChildX = min(globalMinimumChildX, bounds.minimumX)
        globalMaximumChildX = max(globalMaximumChildX, bounds.maximumX)
        laneBounds += LaneBounds(
            lane = lane,
            contentTop = bounds.minimumY,
            contentBottom = bounds.maximumY,
            centerY = (bounds.minimumY + bounds.maximumY) / 2.0,
        )
    }
    if (
        globalMinimumChildX == Double.POSITIVE_INFINITY ||
        globalMaximumChildX == Double.NEGATIVE_INFINITY
    ) {
        return true
    }

    val fullContentWidth = max(0.0, globalMaximumChildX - globalMinimumChildX)
    val horizontalMargin = max(maximumPadding, 10.0)
    val bodyWidth = fullContentWidth + 2.0 * horizontalMargin
    val laneWidth = titleBandSize + bodyWidth
    val bodyCenter = (globalMinimumChildX + globalMaximumChildX) / 2.0
    val bodyLeft = bodyCenter - bodyWidth / 2.0
    val laneLeft = bodyLeft - titleBandSize
    val centerX = laneLeft + laneWidth / 2.0
    val verticalMargin = max(maximumPadding, titleBandSize)
    laneBounds.sortBy(LaneBounds::centerY)

    for (index in laneBounds.indices) {
        val current = laneBounds[index]
        val laneTop = if (index == 0) {
            current.contentTop - verticalMargin
        } else {
            (laneBounds[index - 1].contentBottom + current.contentTop) / 2.0
        }
        val laneBottom = if (index == laneBounds.lastIndex) {
            current.contentBottom + verticalMargin
        } else {
            (current.contentBottom + laneBounds[index + 1].contentTop) / 2.0
        }
        current.lane.x = centerX
        current.lane.y = (laneTop + laneBottom) / 2.0
        current.lane.width = laneWidth
        current.lane.height = max(0.0, laneBottom - laneTop)
        current.lane.swimlaneContentTop = current.contentTop
        current.lane.groupTitleRect = SwimlaneRect(
            left = laneLeft,
            right = laneLeft + titleBandSize,
            top = laneTop,
            bottom = laneBottom,
        )
    }

    if (direction == SwimlaneDirection.RL) mirrorAxis(layout, MirrorAxis.X)
    return true
}
