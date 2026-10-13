package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/geometry.ts.
 */
internal const val DirectionEpsilon: Double = 1e-3

internal enum class RectSide {
    Top,
    Bottom,
    Left,
    Right,
}

internal data class RectEntry(
    val id: String,
    val rect: SwimlaneRect,
)

internal data class NodeBoundsInfo(
    val id: String,
    val centerX: Double,
    val centerY: Double,
    val rect: SwimlaneRect,
)

internal data class NodePairGeometry(
    val sourceId: String,
    val destinationId: String,
    val sourceInfo: NodeBoundsInfo,
    val destinationInfo: NodeBoundsInfo,
    val collinearX: Boolean,
    val collinearY: Boolean,
)

internal data class LayoutNodeRect(
    val nodeId: String,
    val rect: SwimlaneRect,
)

internal enum class ThreeSegmentRouteKind {
    HVH,
    VHV,
}

internal data class ThreeSegmentRoute(
    val kind: ThreeSegmentRouteKind,
    val p0: SwimlanePoint,
    val p1: SwimlanePoint,
    val p2: SwimlanePoint,
    val p3: SwimlanePoint,
)

internal data class OrthogonalSegment(
    val index: Int,
    val a: SwimlanePoint,
    val b: SwimlanePoint,
    val horizontal: Boolean,
    val vertical: Boolean,
)

private data class SimplifyPassResult(
    val points: List<SwimlanePoint>,
    val changed: Boolean,
)

private fun measuredNodeRect(node: SwimlaneNode): NodeBoundsInfo? {
    val centerX = node.x ?: 0.0
    val centerY = node.y ?: 0.0
    if (node.width <= 0.0 || node.height <= 0.0) return null
    return NodeBoundsInfo(
        id = node.id,
        centerX = centerX,
        centerY = centerY,
        rect = rectFromCenterSize(centerX, centerY, node.width, node.height),
    )
}

private fun nodeBoundsInfoFor(node: SwimlaneNode): NodeBoundsInfo? =
    if (node.isGroup) null else measuredNodeRect(node)

internal fun samePoint(
    a: SwimlanePoint,
    b: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean = abs(a.x - b.x) < epsilon && abs(a.y - b.y) < epsilon

internal fun sameX(
    a: SwimlanePoint,
    b: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean = abs(a.x - b.x) < epsilon

internal fun sameY(
    a: SwimlanePoint,
    b: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean = abs(a.y - b.y) < epsilon

internal fun isHorizontalSegment(
    a: SwimlanePoint,
    b: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean = sameY(a, b, epsilon) && abs(a.x - b.x) > epsilon

internal fun isVerticalSegment(
    a: SwimlanePoint,
    b: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean = sameX(a, b, epsilon) && abs(a.y - b.y) > epsilon

internal fun overlapLength(
    a1: Double,
    a2: Double,
    b1: Double,
    b2: Double,
): Double = max(
    0.0,
    min(max(a1, a2), max(b1, b2)) - max(min(a1, a2), min(b1, b2)),
)

internal fun sameAxisSegmentOverlapLength(
    a: OrthogonalSegment,
    b: OrthogonalSegment,
    epsilon: Double = DirectionEpsilon,
): Double {
    if (a.horizontal && b.horizontal && sameY(a.a, b.a, epsilon)) {
        return overlapLength(a.a.x, a.b.x, b.a.x, b.b.x)
    }
    if (a.vertical && b.vertical && sameX(a.a, b.a, epsilon)) {
        return overlapLength(a.a.y, a.b.y, b.a.y, b.b.y)
    }
    return 0.0
}

internal fun orthogonalSegmentsForPoints(
    points: List<SwimlanePoint>,
    epsilon: Double = DirectionEpsilon,
): List<OrthogonalSegment> = buildList {
    for (index in 0 until points.lastIndex) {
        val a = points[index]
        val b = points[index + 1]
        val horizontal = isHorizontalSegment(a, b, epsilon)
        val vertical = isVerticalSegment(a, b, epsilon)
        if (horizontal || vertical) {
            add(
                OrthogonalSegment(
                    index = index,
                    a = a,
                    b = b,
                    horizontal = horizontal,
                    vertical = vertical,
                ),
            )
        }
    }
}

internal fun countOrthogonalBends(
    points: List<SwimlanePoint>,
    epsilon: Double = DirectionEpsilon,
): Int {
    val segments = orthogonalSegmentsForPoints(points, epsilon)
    var bends = 0
    for (index in 1 until segments.size) {
        if (segments[index - 1].horizontal != segments[index].horizontal) bends++
    }
    return bends
}

internal fun dedupeConsecutivePoints(
    points: List<SwimlanePoint>,
    epsilon: Double = DirectionEpsilon,
): List<SwimlanePoint> = buildList {
    for (point in points) {
        if (isEmpty() || !samePoint(last(), point, epsilon)) {
            add(point.copy())
        }
    }
}

internal fun classifyThreeSegmentRoute(
    points: List<SwimlanePoint>?,
    epsilon: Double = DirectionEpsilon,
): ThreeSegmentRoute? {
    if (points == null || points.size != 4) return null
    val (p0, p1, p2, p3) = points
    if (
        isHorizontalSegment(p0, p1, epsilon) &&
        isVerticalSegment(p1, p2, epsilon) &&
        isHorizontalSegment(p2, p3, epsilon)
    ) {
        return ThreeSegmentRoute(ThreeSegmentRouteKind.HVH, p0, p1, p2, p3)
    }
    if (
        isVerticalSegment(p0, p1, epsilon) &&
        isHorizontalSegment(p1, p2, epsilon) &&
        isVerticalSegment(p2, p3, epsilon)
    ) {
        return ThreeSegmentRoute(ThreeSegmentRouteKind.VHV, p0, p1, p2, p3)
    }
    return null
}

internal fun segmentBoundsOverlapRect(
    a: SwimlanePoint,
    b: SwimlanePoint,
    rect: SwimlaneRect,
    buffer: Double = 0.0,
): Boolean {
    val segmentMinimumX = min(a.x, b.x)
    val segmentMaximumX = max(a.x, b.x)
    val segmentMinimumY = min(a.y, b.y)
    val segmentMaximumY = max(a.y, b.y)
    return segmentMaximumX > rect.left - buffer &&
        segmentMinimumX < rect.right + buffer &&
        segmentMaximumY > rect.top - buffer &&
        segmentMinimumY < rect.bottom + buffer
}

internal fun pointInsideRect(
    point: SwimlanePoint,
    rect: SwimlaneRect,
    buffer: Double = 0.0,
): Boolean = point.x > rect.left + buffer &&
    point.x < rect.right - buffer &&
    point.y > rect.top + buffer &&
    point.y < rect.bottom - buffer

internal fun rectContainsRect(
    outer: SwimlaneRect,
    inner: SwimlaneRect,
): Boolean = outer.left <= inner.left &&
    outer.right >= inner.right &&
    outer.top <= inner.top &&
    outer.bottom >= inner.bottom

internal fun rectsOverlap(
    a: SwimlaneRect,
    b: SwimlaneRect,
): Boolean = a.left < b.right &&
    a.right > b.left &&
    a.top < b.bottom &&
    a.bottom > b.top

internal fun inflateRect(
    rect: SwimlaneRect,
    margin: Double,
): SwimlaneRect = SwimlaneRect(
    left = rect.left - margin,
    right = rect.right + margin,
    top = rect.top - margin,
    bottom = rect.bottom + margin,
)

internal fun rectFromCenterSize(
    centerX: Double,
    centerY: Double,
    width: Double,
    height: Double,
): SwimlaneRect = SwimlaneRect(
    left = centerX - width / 2.0,
    right = centerX + width / 2.0,
    top = centerY - height / 2.0,
    bottom = centerY + height / 2.0,
)

internal fun rectOfNodeBounds(node: SwimlaneNode): SwimlaneRect? =
    measuredNodeRect(node)?.rect

internal fun portForRectSide(
    node: NodeBoundsInfo,
    side: RectSide,
): SwimlanePoint = when (side) {
    RectSide.Top -> SwimlanePoint(node.centerX, node.rect.top)
    RectSide.Bottom -> SwimlanePoint(node.centerX, node.rect.bottom)
    RectSide.Left -> SwimlanePoint(node.rect.left, node.centerY)
    RectSide.Right -> SwimlanePoint(node.rect.right, node.centerY)
}

internal fun buildOrthogonalPortPath(
    source: SwimlanePoint,
    sourceSide: RectSide,
    destination: SwimlanePoint,
    destinationSide: RectSide,
    anchor: Double,
    epsilon: Double = DirectionEpsilon,
): List<SwimlanePoint>? {
    val sourceHorizontal = sourceSide == RectSide.Left || sourceSide == RectSide.Right
    val destinationHorizontal =
        destinationSide == RectSide.Left || destinationSide == RectSide.Right

    if (sourceHorizontal && destinationHorizontal) {
        val opposingDirection =
            (sourceSide == RectSide.Right &&
                destinationSide == RectSide.Left &&
                source.x < destination.x) ||
                (sourceSide == RectSide.Left &&
                    destinationSide == RectSide.Right &&
                    source.x > destination.x)
        if (opposingDirection) {
            if (sameY(source, destination, epsilon)) return listOf(source, destination)
            val middleX = (source.x + destination.x) / 2.0
            return listOf(
                source,
                SwimlanePoint(middleX, source.y),
                SwimlanePoint(middleX, destination.y),
                destination,
            )
        }
        if (sourceSide == destinationSide) {
            if (sameY(source, destination, epsilon)) return null
            val intermediateX = if (sourceSide == RectSide.Left) {
                min(source.x, destination.x) - anchor
            } else {
                max(source.x, destination.x) + anchor
            }
            return listOf(
                source,
                SwimlanePoint(intermediateX, source.y),
                SwimlanePoint(intermediateX, destination.y),
                destination,
            )
        }
        return null
    }

    if (!sourceHorizontal && !destinationHorizontal) {
        if (sourceSide == destinationSide) {
            if (sameX(source, destination, epsilon)) return null
            val intermediateY = if (sourceSide == RectSide.Top) {
                min(source.y, destination.y) - anchor
            } else {
                max(source.y, destination.y) + anchor
            }
            return listOf(
                source,
                SwimlanePoint(source.x, intermediateY),
                SwimlanePoint(destination.x, intermediateY),
                destination,
            )
        }
        val sameDirection =
            (sourceSide == RectSide.Bottom &&
                destinationSide == RectSide.Top &&
                source.y < destination.y) ||
                (sourceSide == RectSide.Top &&
                    destinationSide == RectSide.Bottom &&
                    source.y > destination.y)
        if (!sameDirection) return null
        if (sameX(source, destination, epsilon)) return listOf(source, destination)
        val middleY = (source.y + destination.y) / 2.0
        return listOf(
            source,
            SwimlanePoint(source.x, middleY),
            SwimlanePoint(destination.x, middleY),
            destination,
        )
    }

    if (sourceHorizontal) {
        val sameDirectionSource =
            (sourceSide == RectSide.Right && destination.x > source.x) ||
                (sourceSide == RectSide.Left && destination.x < source.x)
        val sameDirectionDestination =
            (destinationSide == RectSide.Top && source.y < destination.y) ||
                (destinationSide == RectSide.Bottom && source.y > destination.y)
        return if (sameDirectionSource && sameDirectionDestination) {
            listOf(source, SwimlanePoint(destination.x, source.y), destination)
        } else {
            null
        }
    }

    val sameDirectionSource =
        (sourceSide == RectSide.Bottom && destination.y > source.y) ||
            (sourceSide == RectSide.Top && destination.y < source.y)
    val sameDirectionDestination =
        (destinationSide == RectSide.Left && source.x < destination.x) ||
            (destinationSide == RectSide.Right && source.x > destination.x)
    return if (sameDirectionSource && sameDirectionDestination) {
        listOf(source, SwimlanePoint(source.x, destination.y), destination)
    } else {
        null
    }
}

internal fun buildSameSideTrackPath(
    source: SwimlanePoint,
    side: RectSide,
    destination: SwimlanePoint,
    track: Double,
): List<SwimlanePoint> = if (side == RectSide.Left || side == RectSide.Right) {
    listOf(
        source,
        SwimlanePoint(track, source.y),
        SwimlanePoint(track, destination.y),
        destination,
    )
} else {
    listOf(
        source,
        SwimlanePoint(source.x, track),
        SwimlanePoint(destination.x, track),
        destination,
    )
}

internal data class RealNodeBounds(
    val nodeInfoById: Map<String, NodeBoundsInfo>,
    val realNodeRects: List<RectEntry>,
)

internal fun collectRealNodeBounds(nodes: Iterable<SwimlaneNode>): RealNodeBounds {
    val nodeInfoById = linkedMapOf<String, NodeBoundsInfo>()
    val realNodeRects = mutableListOf<RectEntry>()
    for (node in nodes) {
        if (node.isEdgeLabel) continue
        val info = nodeBoundsInfoFor(node) ?: continue
        nodeInfoById[info.id] = info
        realNodeRects += RectEntry(info.id, info.rect)
    }
    return RealNodeBounds(nodeInfoById, realNodeRects)
}

internal data class NodeRectEntries(
    val realNodeRects: List<RectEntry>,
    val labelNodeRects: List<RectEntry>,
)

internal fun collectNodeRectEntries(nodes: Iterable<SwimlaneNode>): NodeRectEntries {
    val realNodeRects = mutableListOf<RectEntry>()
    val labelNodeRects = mutableListOf<RectEntry>()
    for (node in nodes) {
        val info = nodeBoundsInfoFor(node) ?: continue
        val entry = RectEntry(info.id, info.rect)
        if (node.isEdgeLabel) {
            labelNodeRects += entry
        } else {
            realNodeRects += entry
        }
    }
    return NodeRectEntries(realNodeRects, labelNodeRects)
}

internal fun collectLayoutNodeRects(
    nodes: Iterable<SwimlaneNode>,
    includeEdgeLabels: Boolean = true,
): List<LayoutNodeRect> = buildList {
    for (node in nodes) {
        if (node.isGroup || (!includeEdgeLabels && node.isEdgeLabel)) continue
        add(
            LayoutNodeRect(
                nodeId = node.id,
                rect = rectFromCenterSize(
                    centerX = node.x ?: 0.0,
                    centerY = node.y ?: 0.0,
                    width = node.width,
                    height = node.height,
                ),
            ),
        )
    }
}

internal fun getNodePairGeometry(
    edge: SwimlaneEdge,
    nodeInfoById: Map<String, NodeBoundsInfo>,
    epsilon: Double = DirectionEpsilon,
): NodePairGeometry? {
    val sourceId = edge.start ?: return null
    val destinationId = edge.end ?: return null
    val sourceInfo = nodeInfoById[sourceId] ?: return null
    val destinationInfo = nodeInfoById[destinationId] ?: return null
    return NodePairGeometry(
        sourceId = sourceId,
        destinationId = destinationId,
        sourceInfo = sourceInfo,
        destinationInfo = destinationInfo,
        collinearX = abs(sourceInfo.centerX - destinationInfo.centerX) < epsilon,
        collinearY = abs(sourceInfo.centerY - destinationInfo.centerY) < epsilon,
    )
}

internal fun segmentHitsAnyRect(
    a: SwimlanePoint,
    b: SwimlanePoint,
    rects: List<RectEntry>,
    excludeIds: Collection<String> = emptyList(),
    shrink: Double = 0.0,
): Boolean {
    for (entry in rects) {
        if (entry.id in excludeIds) continue
        if (segmentBoundsOverlapRect(a, b, entry.rect, -shrink)) return true
    }
    return false
}

internal fun orthogonalSegmentsCross(
    a1: SwimlanePoint,
    b1: SwimlanePoint,
    a2: SwimlanePoint,
    b2: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
    endpointTolerance: Double = 1e-6,
): Boolean {
    val firstHorizontal = sameY(a1, b1, epsilon)
    val firstVertical = sameX(a1, b1, epsilon)
    val secondHorizontal = sameY(a2, b2, epsilon)
    val secondVertical = sameX(a2, b2, epsilon)
    if (
        (firstHorizontal && secondHorizontal) ||
        (firstVertical && secondVertical)
    ) {
        return false
    }
    if (
        !(firstHorizontal || firstVertical) ||
        !(secondHorizontal || secondVertical)
    ) {
        return false
    }

    val horizontalA = if (firstHorizontal) a1 else a2
    val horizontalB = if (firstHorizontal) b1 else b2
    val verticalA = if (firstVertical) a1 else a2
    val verticalB = if (firstVertical) b1 else b2
    val horizontalY = horizontalA.y
    val horizontalMinimumX = min(horizontalA.x, horizontalB.x)
    val horizontalMaximumX = max(horizontalA.x, horizontalB.x)
    val verticalX = verticalA.x
    val verticalMinimumY = min(verticalA.y, verticalB.y)
    val verticalMaximumY = max(verticalA.y, verticalB.y)
    if (
        verticalX < horizontalMinimumX ||
        verticalX > horizontalMaximumX ||
        horizontalY < verticalMinimumY ||
        horizontalY > verticalMaximumY
    ) {
        return false
    }

    val matchesHorizontalEndpoint =
        (abs(verticalX - horizontalA.x) < endpointTolerance &&
            abs(horizontalY - horizontalA.y) < endpointTolerance) ||
            (abs(verticalX - horizontalB.x) < endpointTolerance &&
                abs(horizontalY - horizontalB.y) < endpointTolerance)
    val matchesVerticalEndpoint =
        (abs(verticalX - verticalA.x) < endpointTolerance &&
            abs(horizontalY - verticalA.y) < endpointTolerance) ||
            (abs(verticalX - verticalB.x) < endpointTolerance &&
                abs(horizontalY - verticalB.y) < endpointTolerance)
    return !(matchesHorizontalEndpoint && matchesVerticalEndpoint)
}

internal fun sameAxisSegmentsOverlap(
    a1: SwimlanePoint,
    b1: SwimlanePoint,
    a2: SwimlanePoint,
    b2: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean {
    val firstHorizontal = sameY(a1, b1, epsilon)
    val firstVertical = sameX(a1, b1, epsilon)
    val secondHorizontal = sameY(a2, b2, epsilon)
    val secondVertical = sameX(a2, b2, epsilon)
    if (firstVertical && secondVertical && sameX(a1, a2, epsilon)) {
        return overlapLength(a1.y, b1.y, a2.y, b2.y) > epsilon
    }
    if (firstHorizontal && secondHorizontal && sameY(a1, a2, epsilon)) {
        return overlapLength(a1.x, b1.x, a2.x, b2.x) > epsilon
    }
    return false
}

internal fun segmentConflictsWithAnyEdge(
    a: SwimlanePoint,
    b: SwimlanePoint,
    edges: Iterable<SwimlaneEdge>,
    excludeEdge: SwimlaneEdge? = null,
    epsilon: Double = DirectionEpsilon,
    skipDegenerateOther: Boolean = false,
): Boolean {
    for (other in edges) {
        if (other === excludeEdge || other.isLayoutOnly) continue
        val points = other.points ?: continue
        if (points.size < 2) continue
        for (index in 0 until points.lastIndex) {
            val otherA = points[index]
            val otherB = points[index + 1]
            if (skipDegenerateOther && samePoint(otherA, otherB, epsilon)) continue
            if (
                orthogonalSegmentsCross(a, b, otherA, otherB, epsilon) ||
                sameAxisSegmentsOverlap(a, b, otherA, otherB, epsilon)
            ) {
                return true
            }
        }
    }
    return false
}

internal fun orthogonalSegmentsStrictlyCross(
    a1: SwimlanePoint,
    b1: SwimlanePoint,
    a2: SwimlanePoint,
    b2: SwimlanePoint,
    epsilon: Double = DirectionEpsilon,
): Boolean {
    val firstHorizontal = sameY(a1, b1, epsilon)
    val firstVertical = sameX(a1, b1, epsilon)
    val secondHorizontal = sameY(a2, b2, epsilon)
    val secondVertical = sameX(a2, b2, epsilon)
    if (
        !(
            (firstHorizontal && secondVertical) ||
                (firstVertical && secondHorizontal)
            )
    ) {
        return false
    }

    val horizontalA = if (firstHorizontal) a1 else a2
    val horizontalB = if (firstHorizontal) b1 else b2
    val verticalA = if (firstHorizontal) a2 else a1
    val verticalB = if (firstHorizontal) b2 else b1
    val horizontalMinimumX = min(horizontalA.x, horizontalB.x)
    val horizontalMaximumX = max(horizontalA.x, horizontalB.x)
    val verticalMinimumY = min(verticalA.y, verticalB.y)
    val verticalMaximumY = max(verticalA.y, verticalB.y)
    return verticalA.x > horizontalMinimumX + epsilon &&
        verticalA.x < horizontalMaximumX - epsilon &&
        horizontalA.y > verticalMinimumY + epsilon &&
        horizontalA.y < verticalMaximumY - epsilon
}

private fun strictlyBetween(
    value: Double,
    a: Double,
    b: Double,
): Boolean = value > min(a, b) + DirectionEpsilon &&
    value < max(a, b) - DirectionEpsilon

private fun isCollinearIntermediate(
    previous: SwimlanePoint,
    current: SwimlanePoint,
    next: SwimlanePoint,
): Boolean {
    if (sameX(previous, current) && sameX(current, next)) {
        return strictlyBetween(current.y, previous.y, next.y)
    }
    if (sameY(previous, current) && sameY(current, next)) {
        return strictlyBetween(current.x, previous.x, next.x)
    }
    return false
}

private fun simplifyPolylineOnce(points: List<SwimlanePoint>): SimplifyPassResult {
    var changed = false
    val output = mutableListOf<SwimlanePoint>()
    var index = 0
    while (index < points.size) {
        val previous = output.lastOrNull()
        val current = points[index]
        val next = points.getOrNull(index + 1)
        if (previous != null && next != null) {
            if (samePoint(previous, next)) {
                index += 2
                changed = true
                continue
            }
            if (isCollinearIntermediate(previous, current, next)) {
                index++
                changed = true
                continue
            }
        }
        output += current
        index++
    }
    return SimplifyPassResult(output, changed)
}

internal fun orthogonalizePolyline(points: List<SwimlanePoint>): List<SwimlanePoint> {
    if (points.isEmpty()) return emptyList()
    val cleaned = mutableListOf(points.first())
    for (index in 1 until points.size) {
        val previous = cleaned.last()
        val current = points[index]
        if (!sameX(previous, current) && !sameY(previous, current)) {
            val previousPrevious = cleaned.getOrNull(cleaned.lastIndex - 1)
            val incomingVertical =
                previousPrevious != null && sameX(previousPrevious, previous)
            cleaned += if (incomingVertical) {
                SwimlanePoint(previous.x, current.y)
            } else {
                SwimlanePoint(current.x, previous.y)
            }
        }
        cleaned += current
    }
    return dedupeConsecutivePoints(cleaned)
}

internal fun simplifyPolyline(points: List<SwimlanePoint>): List<SwimlanePoint> {
    if (points.size < 3) return points
    var work = points.toList()
    repeat(32) {
        val result = simplifyPolylineOnce(work)
        work = result.points
        if (!result.changed) return work
    }
    return work
}
