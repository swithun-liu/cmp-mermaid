package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/endpointClip.ts.
 */
private const val EndpointEpsilon: Double = 1e-3
private const val InsideEpsilon: Double = 0.5
private const val CornerClearance: Double = 4.0

private data class EndpointContext(
    val edge: SwimlaneEdge,
    val points: List<SwimlanePoint>,
    val sourceRect: SwimlaneRect?,
    val destinationRect: SwimlaneRect?,
)

private fun endpointContextFor(
    edge: SwimlaneEdge,
    nodeByIdMap: Map<String, SwimlaneNode>,
    minimumPoints: Int,
): EndpointContext? {
    val points = edge.points
    if (edge.isLayoutOnly || points == null || points.size < minimumPoints) return null
    return EndpointContext(
        edge = edge,
        points = points,
        sourceRect = edge.start?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds),
        destinationRect = edge.end?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds),
    )
}

private fun segmentEnterPoint(
    outside: SwimlanePoint,
    inside: SwimlanePoint,
    rect: SwimlaneRect,
): SwimlanePoint {
    if (sameY(outside, inside, EndpointEpsilon)) {
        return SwimlanePoint(
            x = if (outside.x < rect.left) rect.left else rect.right,
            y = outside.y,
        )
    }
    if (sameX(outside, inside, EndpointEpsilon)) {
        return SwimlanePoint(
            x = outside.x,
            y = if (outside.y < rect.top) rect.top else rect.bottom,
        )
    }
    return SwimlanePoint(
        x = min(rect.right, max(rect.left, outside.x)),
        y = min(rect.bottom, max(rect.top, outside.y)),
    )
}

private fun clipEndpoint(
    points: List<SwimlanePoint>,
    rect: SwimlaneRect,
    atStart: Boolean,
): List<SwimlanePoint> {
    val step = if (atStart) 1 else -1
    var outsideIndex = if (atStart) 0 else points.lastIndex
    while (
        outsideIndex in points.indices &&
        pointInsideRect(points[outsideIndex], rect, InsideEpsilon)
    ) {
        outsideIndex += step
    }
    if (outsideIndex !in points.indices) return points
    val insideIndex = outsideIndex - step
    if (insideIndex !in points.indices) return points
    val entry = segmentEnterPoint(points[outsideIndex], points[insideIndex], rect)
    return if (atStart) {
        listOf(entry) + points.drop(outsideIndex)
    } else {
        points.take(outsideIndex + 1) + entry
    }
}

internal fun clipEdgeEndpointsToNodeBoundaries(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    for (edge in edges) {
        val context = endpointContextFor(edge, nodeByIdMap, 2) ?: continue
        var next = context.points.toList()
        if (context.sourceRect != null) {
            next = clipEndpoint(next, context.sourceRect, atStart = true)
        }
        if (context.destinationRect != null) {
            next = clipEndpoint(next, context.destinationRect, atStart = false)
        }
        next = simplifyPolyline(orthogonalizePolyline(next))
        next = clearStraightEndpointCornerConnections(
            next,
            context.sourceRect,
            context.destinationRect,
        )
        context.edge.points =
            simplifyPolyline(orthogonalizePolyline(next)).toMutableList()
    }
}

private fun snapEndpointToBoundary(
    inner: SwimlanePoint,
    endpoint: SwimlanePoint,
    rect: SwimlaneRect,
    useApproachSide: Boolean = false,
): SwimlanePoint {
    if (sameY(inner, endpoint, EndpointEpsilon)) {
        if (
            endpoint.y < rect.top - EndpointEpsilon ||
            endpoint.y > rect.bottom + EndpointEpsilon
        ) {
            return endpoint
        }
        if (useApproachSide) {
            if (inner.x < rect.left - EndpointEpsilon) {
                return SwimlanePoint(rect.left, inner.y)
            }
            if (inner.x > rect.right + EndpointEpsilon) {
                return SwimlanePoint(rect.right, inner.y)
            }
        }
        val toLeft = abs(endpoint.x - rect.left) <= abs(endpoint.x - rect.right)
        return SwimlanePoint(if (toLeft) rect.left else rect.right, inner.y)
    }
    if (sameX(inner, endpoint, EndpointEpsilon)) {
        if (
            endpoint.x < rect.left - EndpointEpsilon ||
            endpoint.x > rect.right + EndpointEpsilon
        ) {
            return endpoint
        }
        if (useApproachSide) {
            if (inner.y < rect.top - EndpointEpsilon) {
                return SwimlanePoint(inner.x, rect.top)
            }
            if (inner.y > rect.bottom + EndpointEpsilon) {
                return SwimlanePoint(inner.x, rect.bottom)
            }
        }
        val toTop = abs(endpoint.y - rect.top) <= abs(endpoint.y - rect.bottom)
        return SwimlanePoint(inner.x, if (toTop) rect.top else rect.bottom)
    }
    return endpoint
}

private fun firstDistinctAdjacent(
    points: List<SwimlanePoint>,
    endpointIndex: Int,
    step: Int,
): SwimlanePoint? {
    val endpoint = points[endpointIndex]
    var index = endpointIndex + step
    while (index in points.indices) {
        val candidate = points[index]
        if (!samePoint(candidate, endpoint, EndpointEpsilon)) return candidate
        index += step
    }
    return points.getOrNull(endpointIndex + step)
}

private data class DirectionRange(
    val lower: Double,
    val upper: Double,
)

private fun cornerClearanceRange(
    minimum: Double,
    maximum: Double,
): DirectionRange {
    val lower = minimum + CornerClearance
    val upper = maximum - CornerClearance
    return if (lower <= upper) {
        DirectionRange(lower, upper)
    } else {
        val middle = (minimum + maximum) / 2.0
        DirectionRange(middle, middle)
    }
}

private fun clampToCornerClearance(
    value: Double,
    minimum: Double,
    maximum: Double,
): Double {
    val range = cornerClearanceRange(minimum, maximum)
    return min(range.upper, max(range.lower, value))
}

private fun intersectRanges(ranges: List<DirectionRange>): DirectionRange? {
    val lower = ranges.maxOf(DirectionRange::lower)
    val upper = ranges.minOf(DirectionRange::upper)
    return if (lower > upper) null else DirectionRange(lower, upper)
}

private fun clearanceRangeForSide(
    rect: SwimlaneRect,
    side: RectSide,
): DirectionRange = if (side == RectSide.Left || side == RectSide.Right) {
    cornerClearanceRange(rect.top, rect.bottom)
} else {
    cornerClearanceRange(rect.left, rect.right)
}

private fun terminalSideForSegment(
    endpoint: SwimlanePoint,
    adjacent: SwimlanePoint,
    rect: SwimlaneRect,
): RectSide? {
    val yWithin =
        endpoint.y >= rect.top - EndpointEpsilon &&
            endpoint.y <= rect.bottom + EndpointEpsilon
    val xWithin =
        endpoint.x >= rect.left - EndpointEpsilon &&
            endpoint.x <= rect.right + EndpointEpsilon
    if (sameY(endpoint, adjacent, EndpointEpsilon) && yWithin) {
        if (abs(endpoint.x - rect.left) < EndpointEpsilon) return RectSide.Left
        if (abs(endpoint.x - rect.right) < EndpointEpsilon) return RectSide.Right
    }
    if (sameX(endpoint, adjacent, EndpointEpsilon) && xWithin) {
        if (abs(endpoint.y - rect.top) < EndpointEpsilon) return RectSide.Top
        if (abs(endpoint.y - rect.bottom) < EndpointEpsilon) return RectSide.Bottom
    }
    return null
}

private fun isHorizontalSide(side: RectSide): Boolean =
    side == RectSide.Left || side == RectSide.Right

private fun straightClearanceRange(
    start: SwimlanePoint,
    end: SwimlanePoint,
    sourceRect: SwimlaneRect?,
    destinationRect: SwimlaneRect?,
    horizontal: Boolean,
): DirectionRange? {
    val ranges = mutableListOf<DirectionRange>()
    val sourceSide = sourceRect?.let {
        terminalSideForSegment(start, end, it)
    }
    val destinationSide = destinationRect?.let {
        terminalSideForSegment(end, start, it)
    }
    if (
        sourceRect != null &&
        sourceSide != null &&
        isHorizontalSide(sourceSide) == horizontal
    ) {
        ranges += clearanceRangeForSide(sourceRect, sourceSide)
    }
    if (
        destinationRect != null &&
        destinationSide != null &&
        isHorizontalSide(destinationSide) == horizontal
    ) {
        ranges += clearanceRangeForSide(destinationRect, destinationSide)
    }
    return if (ranges.isEmpty()) null else intersectRanges(ranges)
}

private fun clearStraightEndpointCornerAxis(
    start: SwimlanePoint,
    end: SwimlanePoint,
    sourceRect: SwimlaneRect?,
    destinationRect: SwimlaneRect?,
    horizontal: Boolean,
): List<SwimlanePoint>? {
    val range = straightClearanceRange(
        start,
        end,
        sourceRect,
        destinationRect,
        horizontal,
    ) ?: return null
    val current = if (horizontal) start.y else start.x
    val next = min(range.upper, max(range.lower, current))
    if (abs(next - current) < EndpointEpsilon) return null
    return if (horizontal) {
        listOf(SwimlanePoint(start.x, next), SwimlanePoint(end.x, next))
    } else {
        listOf(SwimlanePoint(next, start.y), SwimlanePoint(next, end.y))
    }
}

private fun clearStraightEndpointCornerConnections(
    points: List<SwimlanePoint>,
    sourceRect: SwimlaneRect? = null,
    destinationRect: SwimlaneRect? = null,
): List<SwimlanePoint> {
    if (points.size != 2) return points
    val start = points[0]
    val end = points[1]
    if (sameY(start, end, EndpointEpsilon)) {
        return clearStraightEndpointCornerAxis(
            start,
            end,
            sourceRect,
            destinationRect,
            horizontal = true,
        ) ?: points
    }
    if (sameX(start, end, EndpointEpsilon)) {
        return clearStraightEndpointCornerAxis(
            start,
            end,
            sourceRect,
            destinationRect,
            horizontal = false,
        ) ?: points
    }
    return points
}

private fun cornerClearedEndpoint(
    endpoint: SwimlanePoint,
    rect: SwimlaneRect,
    side: RectSide,
): SwimlanePoint = if (isHorizontalSide(side)) {
    endpoint.copy(y = clampToCornerClearance(endpoint.y, rect.top, rect.bottom))
} else {
    endpoint.copy(x = clampToCornerClearance(endpoint.x, rect.left, rect.right))
}

private fun moveCollinearEndpointRun(
    points: List<SwimlanePoint>,
    endpointIndex: Int,
    step: Int,
    endpoint: SwimlanePoint,
    adjusted: SwimlanePoint,
    horizontalTerminal: Boolean,
): List<SwimlanePoint> {
    val next = points.toMutableList()
    var index = endpointIndex
    while (index in points.indices) {
        val point = points[index]
        if (
            horizontalTerminal &&
            !sameY(point, endpoint, EndpointEpsilon)
        ) {
            break
        }
        if (
            !horizontalTerminal &&
            !sameX(point, endpoint, EndpointEpsilon)
        ) {
            break
        }
        next[index] = if (horizontalTerminal) {
            next[index].copy(y = adjusted.y)
        } else {
            next[index].copy(x = adjusted.x)
        }
        index += step
    }
    return next
}

private fun clearEndpointCornerConnection(
    points: List<SwimlanePoint>,
    rect: SwimlaneRect,
    atStart: Boolean,
): List<SwimlanePoint> {
    if (points.size < 2) return points
    val endpointIndex = if (atStart) 0 else points.lastIndex
    val step = if (atStart) 1 else -1
    val endpoint = points[endpointIndex]
    val adjacent = firstDistinctAdjacent(points, endpointIndex, step) ?: return points
    val side = terminalSideForSegment(endpoint, adjacent, rect) ?: return points
    val horizontalTerminal = isHorizontalSide(side)
    val adjusted = cornerClearedEndpoint(endpoint, rect, side)
    if (samePoint(endpoint, adjusted, EndpointEpsilon)) return points
    return moveCollinearEndpointRun(
        points,
        endpointIndex,
        step,
        endpoint,
        adjusted,
        horizontalTerminal,
    )
}

private fun borderSideForSegment(
    a: SwimlanePoint,
    b: SwimlanePoint,
    rect: SwimlaneRect,
): RectSide? {
    val xWithin =
        min(a.x, b.x) >= rect.left - EndpointEpsilon &&
            max(a.x, b.x) <= rect.right + EndpointEpsilon
    val yWithin =
        min(a.y, b.y) >= rect.top - EndpointEpsilon &&
            max(a.y, b.y) <= rect.bottom + EndpointEpsilon
    if (
        abs(a.y - rect.top) < EndpointEpsilon &&
        abs(b.y - rect.top) < EndpointEpsilon &&
        xWithin
    ) {
        return RectSide.Top
    }
    if (
        abs(a.y - rect.bottom) < EndpointEpsilon &&
        abs(b.y - rect.bottom) < EndpointEpsilon &&
        xWithin
    ) {
        return RectSide.Bottom
    }
    if (
        abs(a.x - rect.left) < EndpointEpsilon &&
        abs(b.x - rect.left) < EndpointEpsilon &&
        yWithin
    ) {
        return RectSide.Left
    }
    if (
        abs(a.x - rect.right) < EndpointEpsilon &&
        abs(b.x - rect.right) < EndpointEpsilon &&
        yWithin
    ) {
        return RectSide.Right
    }
    return null
}

private fun leavesOutward(
    side: RectSide,
    from: SwimlanePoint,
    to: SwimlanePoint,
    rect: SwimlaneRect,
): Boolean = when (side) {
    RectSide.Top ->
        sameX(from, to, EndpointEpsilon) && to.y < rect.top - EndpointEpsilon
    RectSide.Bottom ->
        sameX(from, to, EndpointEpsilon) && to.y > rect.bottom + EndpointEpsilon
    RectSide.Left ->
        sameY(from, to, EndpointEpsilon) && to.x < rect.left - EndpointEpsilon
    RectSide.Right ->
        sameY(from, to, EndpointEpsilon) && to.x > rect.right + EndpointEpsilon
}

private fun collapseOwnBorderStub(
    points: List<SwimlanePoint>,
    rect: SwimlaneRect,
    atStart: Boolean,
): List<SwimlanePoint> {
    if (points.size < 3) return points
    if (atStart) {
        val side = borderSideForSegment(points[0], points[1], rect)
        return if (
            side != null &&
            leavesOutward(side, points[1], points[2], rect)
        ) {
            points.drop(1)
        } else {
            points
        }
    }
    val last = points.lastIndex
    val side = borderSideForSegment(points[last - 1], points[last], rect)
    return if (
        side != null &&
        leavesOutward(side, points[last - 1], points[last - 2], rect)
    ) {
        points.take(last)
    } else {
        points
    }
}

private fun snapAndCollapseEndpoints(
    points: List<SwimlanePoint>,
    sourceRect: SwimlaneRect?,
    destinationRect: SwimlaneRect?,
): List<SwimlanePoint> {
    var next = points
    if (sourceRect != null) {
        val adjacent = firstDistinctAdjacent(next, 0, 1)
        if (adjacent != null) {
            val snapped = snapEndpointToBoundary(adjacent, next[0], sourceRect)
            if (snapped !== next[0]) next = listOf(snapped) + next.drop(1)
        }
        next = collapseOwnBorderStub(next, sourceRect, atStart = true)
    }
    if (destinationRect != null) {
        val last = next.lastIndex
        val adjacent = firstDistinctAdjacent(next, last, -1)
        if (adjacent != null) {
            val snapped = snapEndpointToBoundary(
                adjacent,
                next[last],
                destinationRect,
                useApproachSide = true,
            )
            if (snapped !== next[last]) next = next.take(last) + snapped
        }
        next = collapseOwnBorderStub(next, destinationRect, atStart = false)
    }

    val straightCleared = clearStraightEndpointCornerConnections(
        next,
        sourceRect,
        destinationRect,
    )
    if (straightCleared !== next || next.size == 2) return straightCleared
    if (sourceRect != null) {
        next = clearEndpointCornerConnection(next, sourceRect, atStart = true)
    }
    if (destinationRect != null) {
        next = clearEndpointCornerConnection(next, destinationRect, atStart = false)
    }
    return next
}

internal fun prepareEdgeEndpointsForRenderer(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    for (edge in edges) {
        val context = endpointContextFor(edge, nodeByIdMap, 2) ?: continue
        val input = dedupeConsecutivePoints(context.points, EndpointEpsilon)
        val newPoints = snapAndCollapseEndpoints(
            input,
            context.sourceRect,
            context.destinationRect,
        )
        if (newPoints.size < 3) {
            context.edge.points = newPoints.toMutableList()
            continue
        }
        context.edge.points = buildList {
            add(newPoints.first())
            add(newPoints.first().copy())
            addAll(newPoints.subList(1, newPoints.lastIndex))
            add(newPoints.last())
            add(newPoints.last().copy())
        }.toMutableList()
    }
}
