package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.hypot
import kotlin.math.round

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/terminalStub.ts -> collapseShortTerminalStub.
 */
internal fun collapseShortTerminalStub(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val minimumStub = 10.0
    val epsilon = 1e-3
    val buffer = 2.0
    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)

    data class SegmentKey(
        val ax: Long,
        val ay: Long,
        val bx: Long,
        val by: Long,
    )

    fun segmentKey(
        a: SwimlanePoint,
        b: SwimlanePoint,
    ): SegmentKey = SegmentKey(
        ax = round(a.x * 1_000.0).toLong(),
        ay = round(a.y * 1_000.0).toLong(),
        bx = round(b.x * 1_000.0).toLong(),
        by = round(b.y * 1_000.0).toLong(),
    )

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val rawPoints = edge.points ?: continue
        if (rawPoints.size < 4) continue
        val points = dedupeConsecutivePoints(rawPoints, epsilon)
        if (points.size < 4) continue

        val lastIndex = points.lastIndex
        val endPoint = points[lastIndex]
        val penultimatePoint = points[lastIndex - 1]
        val previousPoint = points[lastIndex - 2]
        val lastDeltaX = endPoint.x - penultimatePoint.x
        val lastDeltaY = endPoint.y - penultimatePoint.y
        val lastLength = hypot(lastDeltaX, lastDeltaY)
        if (lastLength >= minimumStub || lastLength < epsilon) continue

        val penultimateDeltaX = penultimatePoint.x - previousPoint.x
        val penultimateDeltaY = penultimatePoint.y - previousPoint.y
        val penultimateLength = hypot(penultimateDeltaX, penultimateDeltaY)
        if (penultimateLength < epsilon) continue

        val lastHorizontal =
            isHorizontalSegment(penultimatePoint, endPoint, epsilon)
        val lastVertical =
            isVerticalSegment(penultimatePoint, endPoint, epsilon)
        val penultimateHorizontal =
            isHorizontalSegment(previousPoint, penultimatePoint, epsilon)
        val penultimateVertical =
            isVerticalSegment(previousPoint, penultimatePoint, epsilon)
        if (
            !(
                (lastHorizontal && penultimateVertical) ||
                    (lastVertical && penultimateHorizontal)
                )
        ) {
            continue
        }

        val destinationId = edge.end
        val sourceId = edge.start
        val destination = destinationId?.let(nodeByIdMap::get) ?: continue
        val destinationCenterX = destination.x ?: 0.0
        val destinationCenterY = destination.y ?: 0.0
        val destinationRect = rectOfNodeBounds(destination) ?: continue

        val newPrevious: SwimlanePoint
        val newEnd: SwimlanePoint
        if (penultimateVertical) {
            val approachFromBelow = penultimateDeltaY < 0.0
            newPrevious = SwimlanePoint(destinationCenterX, previousPoint.y)
            newEnd = SwimlanePoint(
                destinationCenterX,
                if (approachFromBelow) {
                    destinationRect.bottom
                } else {
                    destinationRect.top
                },
            )
        } else {
            val approachFromLeft = penultimateDeltaX > 0.0
            newPrevious = SwimlanePoint(previousPoint.x, destinationCenterY)
            newEnd = SwimlanePoint(
                if (approachFromLeft) {
                    destinationRect.right
                } else {
                    destinationRect.left
                },
                destinationCenterY,
            )
        }

        if (
            segmentHitsAnyRect(
                newPrevious,
                newEnd,
                realNodeRects,
                listOfNotNull(destinationId),
                -buffer,
            )
        ) {
            continue
        }
        if (
            segmentHitsAnyRect(
                newPrevious,
                newEnd,
                labelNodeRects,
                shrink = -buffer,
            )
        ) {
            continue
        }
        if (sourceId != null) {
            val sourceRect = nodeByIdMap[sourceId]?.let(::rectOfNodeBounds)
            if (sourceRect != null && pointInsideRect(newPrevious, sourceRect, buffer)) {
                continue
            }
        }

        val selfSegments = buildSet {
            for (index in 0 until points.lastIndex) {
                add(segmentKey(points[index], points[index + 1]))
            }
        }

        fun segmentCrossesOtherEdge(
            from: SwimlanePoint,
            to: SwimlanePoint,
        ): Boolean {
            for (other in edges) {
                if (other === edge || other.isLayoutOnly) continue
                val otherPoints = other.points ?: continue
                if (otherPoints.size < 2) continue
                for (index in 0 until otherPoints.lastIndex) {
                    val a = otherPoints[index]
                    val b = otherPoints[index + 1]
                    if (segmentKey(a, b) in selfSegments) continue
                    if (orthogonalSegmentsStrictlyCross(from, to, a, b, epsilon)) {
                        return true
                    }
                }
            }
            return false
        }

        if (segmentCrossesOtherEdge(newPrevious, newEnd)) continue
        if (lastIndex - 3 >= 0) {
            val beforePrevious = points[lastIndex - 3]
            if (
                segmentHitsAnyRect(
                    beforePrevious,
                    newPrevious,
                    realNodeRects,
                    listOfNotNull(sourceId, destinationId),
                    -buffer,
                )
            ) {
                continue
            }
            if (segmentCrossesOtherEdge(beforePrevious, newPrevious)) continue
        }

        val newPoints = points.take(lastIndex - 2) + listOf(newPrevious, newEnd)
        edge.points = newPoints.toMutableList()

        val labelNode = edge.labelNodeId?.let(nodeByIdMap::get) ?: continue
        if (labelNode.width <= 0.0 || labelNode.height <= 0.0) continue
        var bestMidpoint: SwimlanePoint? = null
        var bestLength = -1.0
        for (index in 0 until newPoints.lastIndex) {
            val a = newPoints[index]
            val b = newPoints[index + 1]
            val segmentLength = hypot(b.x - a.x, b.y - a.y)
            val horizontal = sameY(a, b, epsilon)
            val vertical = sameX(a, b, epsilon)
            val fits =
                (horizontal && segmentLength >= labelNode.width + 2.0) ||
                    (vertical && segmentLength >= labelNode.height + 2.0)
            if (fits && segmentLength > bestLength) {
                bestLength = segmentLength
                bestMidpoint = SwimlanePoint(
                    x = (a.x + b.x) / 2.0,
                    y = (a.y + b.y) / 2.0,
                )
            }
        }
        if (bestMidpoint != null) {
            labelNode.x = bestMidpoint.x
            labelNode.y = bestMidpoint.y
        }
    }
}
