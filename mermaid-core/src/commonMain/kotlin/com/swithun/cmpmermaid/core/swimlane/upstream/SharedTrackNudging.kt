package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/sharedTrackNudging.ts
 * -> nudgeSharedInteriorSubpaths.
 */
internal fun nudgeSharedInteriorSubpaths(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val epsilon = 1e-3
    val minimumShared = 8.0
    val trackShift = 7.0
    val minimumTrackGap = trackShift
    val sourceDetourStub = 20.0
    val buffer = 2.0
    val maximumIterations = 12

    data class SegmentLite(
        val index: Int,
        val a: SwimlanePoint,
        val b: SwimlanePoint,
        val horizontal: Boolean,
        val vertical: Boolean,
        val edge: SwimlaneEdge,
        val interior: Boolean,
    )

    data class SourceDetourContext(
        val sourceCenter: SwimlanePoint,
        val targetCenter: SwimlanePoint,
        val sourceRect: SwimlaneRect,
        val tail: List<SwimlanePoint>,
    )

    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)

    fun segmentsFor(
        edge: SwimlaneEdge,
        points: List<SwimlanePoint>,
    ): List<SegmentLite> = orthogonalSegmentsForPoints(points, epsilon).map { segment ->
        SegmentLite(
            index = segment.index,
            a = segment.a,
            b = segment.b,
            horizontal = segment.horizontal,
            vertical = segment.vertical,
            edge = edge,
            interior = segment.index >= 1 && segment.index <= points.size - 3,
        )
    }

    fun allSegments(): List<SegmentLite> = buildList {
        for (edge in edges) {
            if (edge.isLayoutOnly) continue
            val points = edge.points ?: continue
            if (points.size < 2) continue
            addAll(segmentsFor(edge, dedupeConsecutivePoints(points)))
        }
    }

    fun hasCrowdedParallelTrack(
        first: SegmentLite,
        second: SegmentLite,
    ): Boolean {
        if (first.horizontal && second.horizontal) {
            return overlapLength(first.a.x, first.b.x, second.a.x, second.b.x) >=
                minimumShared &&
                abs(first.a.y - second.a.y) < minimumTrackGap
        }
        if (first.vertical && second.vertical) {
            return overlapLength(first.a.y, first.b.y, second.a.y, second.b.y) >=
                minimumShared &&
                abs(first.a.x - second.a.x) < minimumTrackGap
        }
        return false
    }

    fun candidateIsSafe(
        edge: SwimlaneEdge,
        candidate: List<SwimlanePoint>,
    ): Boolean {
        val candidateSegments = segmentsFor(edge, candidate)
        if (candidateSegments.size != candidate.size - 1) return false
        val endpointIds = listOfNotNull(edge.start, edge.end)
        val ownLabelIds = listOfNotNull(edge.labelNodeId)
        for (segment in candidateSegments) {
            if (
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    realNodeRects,
                    endpointIds,
                    -buffer,
                )
            ) {
                return false
            }
            if (
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    labelNodeRects,
                    ownLabelIds,
                    -buffer,
                )
            ) {
                return false
            }
        }

        for (other in edges) {
            if (other === edge || other.isLayoutOnly) continue
            val otherPoints = other.points ?: continue
            if (otherPoints.size < 2) continue
            val otherSegments = segmentsFor(
                other,
                dedupeConsecutivePoints(otherPoints),
            )
            for (candidateSegment in candidateSegments) {
                for (otherSegment in otherSegments) {
                    if (hasCrowdedParallelTrack(candidateSegment, otherSegment)) {
                        return false
                    }
                    if (
                        orthogonalSegmentsStrictlyCross(
                            candidateSegment.a,
                            candidateSegment.b,
                            otherSegment.a,
                            otherSegment.b,
                            epsilon,
                        )
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun shiftedCandidate(
        segment: SegmentLite,
        shift: Double,
    ): List<SwimlanePoint>? {
        val points = dedupeConsecutivePoints(segment.edge.points ?: emptyList())
        if (points.size < 4 || segment.index >= points.lastIndex) return null
        val candidate = points.toMutableList()
        if (segment.horizontal) {
            candidate[segment.index] =
                candidate[segment.index].copy(y = candidate[segment.index].y + shift)
            candidate[segment.index + 1] =
                candidate[segment.index + 1].copy(y = candidate[segment.index + 1].y + shift)
        } else if (segment.vertical) {
            candidate[segment.index] =
                candidate[segment.index].copy(x = candidate[segment.index].x + shift)
            candidate[segment.index + 1] =
                candidate[segment.index + 1].copy(x = candidate[segment.index + 1].x + shift)
        } else {
            return null
        }
        return if (segmentsFor(segment.edge, candidate).size == candidate.size - 1) {
            candidate
        } else {
            null
        }
    }

    fun nodeCenter(
        node: SwimlaneNode,
        rect: SwimlaneRect,
    ): SwimlanePoint = SwimlanePoint(
        x = node.x ?: (rect.left + rect.right) / 2.0,
        y = node.y ?: (rect.top + rect.bottom) / 2.0,
    )

    fun sourceDetourContextFor(segment: SegmentLite): SourceDetourContext? {
        val edge = segment.edge
        val points = dedupeConsecutivePoints(edge.points ?: emptyList())
        if (points.size != 4 || segment.index != 1) return null
        val sourceNode = edge.start?.let(nodeByIdMap::get) ?: return null
        val targetNode = edge.end?.let(nodeByIdMap::get) ?: return null
        val sourceRect = rectOfNodeBounds(sourceNode) ?: return null
        val targetRect = rectOfNodeBounds(targetNode) ?: return null
        val tail = points.drop(segment.index + 2)
        if (tail.isEmpty()) return null
        return SourceDetourContext(
            sourceCenter = nodeCenter(sourceNode, sourceRect),
            targetCenter = nodeCenter(targetNode, targetRect),
            sourceRect = sourceRect,
            tail = tail,
        )
    }

    fun verticalSourceDetour(
        segment: SegmentLite,
        shift: Double,
        context: SourceDetourContext,
    ): List<SwimlanePoint>? {
        val targetBelow = context.targetCenter.y >= context.sourceCenter.y
        val sourcePortY =
            if (targetBelow) context.sourceRect.bottom else context.sourceRect.top
        val stubY = sourcePortY + if (targetBelow) sourceDetourStub else -sourceDetourStub
        if (
            (targetBelow && segment.b.y <= stubY + epsilon) ||
            (!targetBelow && segment.b.y >= stubY - epsilon)
        ) {
            return null
        }
        val railX = segment.a.x + shift
        return dedupeConsecutivePoints(
            listOf(
                SwimlanePoint(context.sourceCenter.x, sourcePortY),
                SwimlanePoint(context.sourceCenter.x, stubY),
                SwimlanePoint(railX, stubY),
                SwimlanePoint(railX, segment.b.y),
            ) + context.tail,
            epsilon,
        )
    }

    fun horizontalSourceDetour(
        segment: SegmentLite,
        shift: Double,
        context: SourceDetourContext,
    ): List<SwimlanePoint>? {
        val targetRight = context.targetCenter.x >= context.sourceCenter.x
        val sourcePortX =
            if (targetRight) context.sourceRect.right else context.sourceRect.left
        val stubX = sourcePortX + if (targetRight) sourceDetourStub else -sourceDetourStub
        if (
            (targetRight && segment.b.x <= stubX + epsilon) ||
            (!targetRight && segment.b.x >= stubX - epsilon)
        ) {
            return null
        }
        val railY = segment.a.y + shift
        return dedupeConsecutivePoints(
            listOf(
                SwimlanePoint(sourcePortX, context.sourceCenter.y),
                SwimlanePoint(stubX, context.sourceCenter.y),
                SwimlanePoint(stubX, railY),
                SwimlanePoint(segment.b.x, railY),
            ) + context.tail,
            epsilon,
        )
    }

    fun sourceDetourCandidate(
        segment: SegmentLite,
        shift: Double,
    ): List<SwimlanePoint>? {
        val context = sourceDetourContextFor(segment) ?: return null
        return when {
            segment.vertical -> verticalSourceDetour(segment, shift, context)
            segment.horizontal -> horizontalSourceDetour(segment, shift, context)
            else -> null
        }
    }

    val shifts = listOf(
        -trackShift,
        trackShift,
        -2.0 * trackShift,
        2.0 * trackShift,
        -3.0 * trackShift,
        3.0 * trackShift,
    )

    repeat(maximumIterations) {
        val segments = allSegments()
        var fixed = false
        loop@ for (firstIndex in segments.indices) {
            for (secondIndex in firstIndex + 1 until segments.size) {
                val first = segments[firstIndex]
                val second = segments[secondIndex]
                if (
                    first.edge === second.edge ||
                    !hasCrowdedParallelTrack(first, second)
                ) {
                    continue
                }
                for (segment in listOf(first, second).filter(SegmentLite::interior)) {
                    for (shift in shifts) {
                        val direct = shiftedCandidate(segment, shift)
                        if (direct != null && candidateIsSafe(segment.edge, direct)) {
                            segment.edge.points = direct.toMutableList()
                            fixed = true
                            break@loop
                        }
                        val detoured = sourceDetourCandidate(segment, shift)
                        if (detoured != null && candidateIsSafe(segment.edge, detoured)) {
                            segment.edge.points = detoured.toMutableList()
                            fixed = true
                            break@loop
                        }
                    }
                }
            }
        }
        if (!fixed) return
    }
}
