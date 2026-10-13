package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/labelAnchoring.ts -> anchorLabelsToPolyline.
 */
private const val LabelEpsilon: Double = 1e-3
private const val MarkerClearanceLength: Double = 10.0
private const val MarkerClearanceHalfWidth: Double = 7.0

private enum class LabelSegmentOrientation {
    Horizontal,
    Vertical,
}

private fun markerClearanceRectFor(
    points: List<SwimlanePoint>,
    atStart: Boolean,
): SwimlaneRect? {
    val terminalIndex = if (atStart) 0 else points.lastIndex
    val step = if (atStart) 1 else -1
    val tip = points.getOrNull(terminalIndex) ?: return null
    val inner = points.getOrNull(terminalIndex + step) ?: return null
    val deltaX = inner.x - tip.x
    val deltaY = inner.y - tip.y
    if (abs(deltaX) + abs(deltaY) < LabelEpsilon) return null
    if (abs(deltaY) <= LabelEpsilon) {
        val endX = tip.x + sign(deltaX) * MarkerClearanceLength
        return SwimlaneRect(
            left = min(tip.x, endX),
            right = max(tip.x, endX),
            top = tip.y - MarkerClearanceHalfWidth,
            bottom = tip.y + MarkerClearanceHalfWidth,
        )
    }
    if (abs(deltaX) <= LabelEpsilon) {
        val endY = tip.y + sign(deltaY) * MarkerClearanceLength
        return SwimlaneRect(
            left = tip.x - MarkerClearanceHalfWidth,
            right = tip.x + MarkerClearanceHalfWidth,
            top = min(tip.y, endY),
            bottom = max(tip.y, endY),
        )
    }
    return SwimlaneRect(
        left = min(tip.x, inner.x),
        right = max(tip.x, inner.x),
        top = min(tip.y, inner.y),
        bottom = max(tip.y, inner.y),
    )
}

private fun normalizeRect(rect: SwimlaneRect): SwimlaneRect = SwimlaneRect(
    left = min(rect.left, rect.right),
    right = max(rect.left, rect.right),
    top = min(rect.top, rect.bottom),
    bottom = max(rect.top, rect.bottom),
)

private fun labelOverlapsOwnMarker(
    rect: SwimlaneRect,
    points: List<SwimlanePoint>,
): Boolean {
    val visiblePoints = dedupeConsecutivePoints(points)
    return listOfNotNull(
        markerClearanceRectFor(visiblePoints, atStart = true),
        markerClearanceRectFor(visiblePoints, atStart = false),
    ).any { marker -> rectsOverlap(rect, normalizeRect(marker)) }
}

internal fun anchorLabelsToPolyline(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    data class SegmentLite(
        val edgeId: String,
        val first: SwimlanePoint,
        val second: SwimlanePoint,
    )

    data class LaneGroup(
        val id: String,
        val rect: SwimlaneRect,
    )

    data class SegmentCandidate(
        val index: Int,
        val length: Double,
        val orientation: LabelSegmentOrientation,
        val middleX: Double,
        val middleY: Double,
    )

    data class Anchor(
        val middleX: Double,
        val middleY: Double,
    )

    data class Placement(
        val laneId: String,
        val anchor: Anchor,
        val rect: SwimlaneRect,
    )

    data class PlacedLabel(
        val labelId: String,
        val rect: SwimlaneRect,
    )

    val allEdgeSegments = buildList {
        for (other in edges) {
            if (other.isLayoutOnly) continue
            val points = other.points ?: continue
            if (points.size < 2) continue
            for (index in 0 until points.lastIndex) {
                add(SegmentLite(other.id, points[index], points[index + 1]))
            }
        }
    }

    val foreignNodeRects = mutableListOf<LayoutNodeRect>()
    val laneGroups = mutableListOf<LaneGroup>()
    for (node in nodeByIdMap.values) {
        if (node.isGroup && node.parentId == null) {
            rectOfNodeBounds(node)?.let { rect ->
                laneGroups += LaneGroup(node.id, rect)
            }
            continue
        }
        if (node.isGroup || node.isEdgeLabel) continue
        rectOfNodeBounds(node)?.let { rect ->
            foreignNodeRects += LayoutNodeRect(node.id, rect)
        }
    }

    val labelPlacementBuffer = 3.0
    val labelLaneMargin = 1.0
    val labelEndpointClearance = 12.0
    val placedLabelRects = mutableListOf<PlacedLabel>()

    fun labelOverlapsForeignNode(
        labelId: String,
        rect: SwimlaneRect,
    ): Boolean {
        val buffered = inflateRect(rect, labelPlacementBuffer)
        return foreignNodeRects.any { entry ->
            entry.nodeId != labelId && rectsOverlap(buffered, entry.rect)
        }
    }

    fun labelOverlapsForeignEdge(
        edgeId: String,
        rect: SwimlaneRect,
    ): Boolean {
        val buffered = inflateRect(rect, labelPlacementBuffer)
        return allEdgeSegments.any { segment ->
            segment.edgeId != edgeId &&
                segmentBoundsOverlapRect(segment.first, segment.second, buffered)
        }
    }

    fun labelOverlapsAnything(
        labelId: String,
        edgeId: String,
        rect: SwimlaneRect,
    ): Boolean = labelOverlapsForeignNode(labelId, rect) ||
        labelOverlapsForeignEdge(edgeId, rect)

    fun findContainingLane(rect: SwimlaneRect): String? =
        laneGroups.firstOrNull { lane -> rectContainsRect(lane.rect, rect) }?.id

    fun overlapsPlacedLabel(
        labelId: String,
        rect: SwimlaneRect,
    ): Boolean = placedLabelRects.any { placed ->
        placed.labelId != labelId && rectsOverlap(rect, placed.rect)
    }

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val labelId = edge.labelNodeId ?: continue
        val labelNode = nodeByIdMap[labelId] ?: continue
        val points = edge.points ?: continue
        if (points.size < 2 || labelNode.width <= 0.0 || labelNode.height <= 0.0) {
            continue
        }

        val segments = buildList {
            for (index in 0 until points.lastIndex) {
                val first = points[index]
                val second = points[index + 1]
                val deltaX = abs(first.x - second.x)
                val deltaY = abs(first.y - second.y)
                if (deltaX < LabelEpsilon && deltaY < LabelEpsilon) continue
                if (deltaX >= LabelEpsilon && deltaY >= LabelEpsilon) continue
                add(
                    SegmentCandidate(
                        index = index,
                        length = deltaX + deltaY,
                        orientation = if (deltaX >= LabelEpsilon) {
                            LabelSegmentOrientation.Horizontal
                        } else {
                            LabelSegmentOrientation.Vertical
                        },
                        middleX = (first.x + second.x) / 2.0,
                        middleY = (first.y + second.y) / 2.0,
                    ),
                )
            }
        }
        if (segments.isEmpty()) continue
        val middleSegments = if (segments.size >= 3) {
            segments.filter { segment ->
                segment.index > 0 && segment.index < segments.size - 1
            }
        } else {
            segments
        }
        val basePool = middleSegments.ifEmpty { segments }
        val labelLongAxis = if (labelNode.width >= labelNode.height) {
            LabelSegmentOrientation.Horizontal
        } else {
            LabelSegmentOrientation.Vertical
        }

        fun rankSegments(pool: List<SegmentCandidate>): List<SegmentCandidate> =
            pool.sortedWith { first, second ->
                val firstLongAxis = first.orientation == labelLongAxis
                val secondLongAxis = second.orientation == labelLongAxis
                if (firstLongAxis != secondLongAxis) {
                    if (firstLongAxis) -1 else 1
                } else {
                    val firstFits = first.length >= (
                        if (first.orientation == LabelSegmentOrientation.Horizontal) {
                            labelNode.width
                        } else {
                            labelNode.height
                        }
                        ) + 2.0
                    val secondFits = second.length >= (
                        if (second.orientation == LabelSegmentOrientation.Horizontal) {
                            labelNode.width
                        } else {
                            labelNode.height
                        }
                        ) + 2.0
                    when {
                        firstFits != secondFits -> if (firstFits) -1 else 1
                        else -> second.length.compareTo(first.length)
                    }
                }
            }

        val firstVisibleSegment = segments.first()
        val lastVisibleSegment = segments.last()
        val alongSegmentParameters =
            listOf(0.5, 0.25, 0.75, 0.05, 0.95, 0.15, 0.85, 0.1, 0.9)

        fun anchorAt(
            segment: SegmentCandidate,
            parameter: Double,
        ): Anchor {
            val first = points[segment.index]
            val second = points[segment.index + 1]
            return Anchor(
                middleX = first.x + (second.x - first.x) * parameter,
                middleY = first.y + (second.y - first.y) * parameter,
            )
        }

        fun pointInsideRectInclusive(
            anchor: Anchor,
            rect: SwimlaneRect,
        ): Boolean = anchor.middleX >= rect.left - LabelEpsilon &&
            anchor.middleX <= rect.right + LabelEpsilon &&
            anchor.middleY >= rect.top - LabelEpsilon &&
            anchor.middleY <= rect.bottom + LabelEpsilon

        fun placementForAnchor(anchor: Anchor): Placement? {
            val centeredRect = rectFromCenterSize(
                anchor.middleX,
                anchor.middleY,
                labelNode.width,
                labelNode.height,
            )
            val centeredLane = findContainingLane(centeredRect)
            if (centeredLane != null) {
                return Placement(centeredLane, anchor, centeredRect)
            }
            val containingLane =
                laneGroups.firstOrNull { lane -> pointInsideRectInclusive(anchor, lane.rect) }
                    ?: return null
            val minimumX =
                containingLane.rect.left + labelNode.width / 2.0 + labelLaneMargin
            val maximumX =
                containingLane.rect.right - labelNode.width / 2.0 - labelLaneMargin
            val minimumY =
                containingLane.rect.top + labelNode.height / 2.0 + labelLaneMargin
            val maximumY =
                containingLane.rect.bottom - labelNode.height / 2.0 - labelLaneMargin
            if (minimumX > maximumX || minimumY > maximumY) return null
            val clampedAnchor = Anchor(
                middleX = min(maximumX, max(minimumX, anchor.middleX)),
                middleY = min(maximumY, max(minimumY, anchor.middleY)),
            )
            val clampedRect = rectFromCenterSize(
                clampedAnchor.middleX,
                clampedAnchor.middleY,
                labelNode.width,
                labelNode.height,
            )
            return if (pointInsideRectInclusive(anchor, clampedRect)) {
                Placement(containingLane.id, clampedAnchor, clampedRect)
            } else {
                null
            }
        }

        fun distanceAlongSegment(
            segment: SegmentCandidate,
            anchor: Anchor,
            endpoint: SwimlanePoint,
        ): Double = if (segment.orientation == LabelSegmentOrientation.Horizontal) {
            abs(anchor.middleX - endpoint.x)
        } else {
            abs(anchor.middleY - endpoint.y)
        }

        fun labelClearsTerminalEndpoints(
            segment: SegmentCandidate,
            anchor: Anchor,
        ): Boolean {
            val labelHalfExtent =
                if (segment.orientation == LabelSegmentOrientation.Horizontal) {
                    labelNode.width / 2.0
                } else {
                    labelNode.height / 2.0
                }
            val requiredDistance = labelHalfExtent + labelEndpointClearance
            if (
                segment === firstVisibleSegment &&
                distanceAlongSegment(
                    segment,
                    anchor,
                    points[segment.index],
                ) + LabelEpsilon < requiredDistance
            ) {
                return false
            }
            if (
                segment === lastVisibleSegment &&
                distanceAlongSegment(
                    segment,
                    anchor,
                    points[segment.index + 1],
                ) + LabelEpsilon < requiredDistance
            ) {
                return false
            }
            return true
        }

        fun tryPool(pool: List<SegmentCandidate>): Pair<String, Anchor>? {
            for (segment in rankSegments(pool)) {
                for (parameter in alongSegmentParameters) {
                    val anchor = anchorAt(segment, parameter)
                    if (!labelClearsTerminalEndpoints(segment, anchor)) continue
                    val placement = placementForAnchor(anchor) ?: continue
                    if (labelOverlapsOwnMarker(placement.rect, points)) continue
                    if (overlapsPlacedLabel(labelId, placement.rect)) continue
                    if (
                        !labelOverlapsAnything(labelId, edge.id, placement.rect)
                    ) {
                        return placement.laneId to placement.anchor
                    }
                }
            }
            return null
        }

        fun findLaneContainingFallback(
            pool: List<SegmentCandidate>,
            requireEndpointClearance: Boolean,
            allowForeignEdgeOverlap: Boolean = false,
        ): Pair<String, Anchor>? {
            for (segment in rankSegments(pool)) {
                val anchor = Anchor(segment.middleX, segment.middleY)
                if (
                    requireEndpointClearance &&
                    !labelClearsTerminalEndpoints(segment, anchor)
                ) {
                    continue
                }
                val placement = placementForAnchor(anchor) ?: continue
                if (
                    labelOverlapsOwnMarker(placement.rect, points) ||
                    overlapsPlacedLabel(labelId, placement.rect) ||
                    labelOverlapsForeignNode(labelId, placement.rect) ||
                    (
                        !allowForeignEdgeOverlap &&
                            labelOverlapsForeignEdge(edge.id, placement.rect)
                        )
                ) {
                    continue
                }
                return placement.laneId to placement.anchor
            }
            return null
        }

        val chosen =
            tryPool(basePool) ?: (
                if (basePool.size < segments.size) tryPool(segments) else null
                ) ?: findLaneContainingFallback(
                segments,
                requireEndpointClearance = true,
            ) ?: findLaneContainingFallback(
                segments,
                requireEndpointClearance = false,
            ) ?: findLaneContainingFallback(
                segments,
                requireEndpointClearance = false,
                allowForeignEdgeOverlap = true,
            )

        if (chosen != null) {
            val (laneId, anchor) = chosen
            labelNode.x = anchor.middleX
            labelNode.y = anchor.middleY
            labelNode.parentId = laneId
            val chosenRect = rectFromCenterSize(
                anchor.middleX,
                anchor.middleY,
                labelNode.width,
                labelNode.height,
            )
            val priorIndex =
                placedLabelRects.indexOfFirst { placed -> placed.labelId == labelId }
            val placed = PlacedLabel(labelId, chosenRect)
            if (priorIndex >= 0) {
                placedLabelRects[priorIndex] = placed
            } else {
                placedLabelRects += placed
            }
        }
    }
}
