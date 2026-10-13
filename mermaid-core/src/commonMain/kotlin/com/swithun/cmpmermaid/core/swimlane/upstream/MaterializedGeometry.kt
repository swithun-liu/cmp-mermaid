package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sign

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/materializedGeometry.ts.
 */
private const val MaterializedEpsilon: Double = 1e-3
private const val MaterializedMinimumShared: Double = 8.0

private fun materializedSegmentsFor(
    points: List<SwimlanePoint>,
): List<OrthogonalSegment> = orthogonalSegmentsForPoints(points)

private fun orthogonallyAligned(
    first: SwimlanePoint,
    second: SwimlanePoint,
): Boolean = sameX(first, second, MaterializedEpsilon) ||
    sameY(first, second, MaterializedEpsilon)

internal fun separateSharedRenderedTerminalLanes(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val minimumFaceClearance = 16.0
    val trackShift = 7.0

    data class TerminalLane(
        val edge: SwimlaneEdge,
        val edgeId: String,
        val nodeId: String,
        val atStart: Boolean,
        val horizontal: Boolean,
        val coordinate: Double,
        val minimum: Double,
        val maximum: Double,
        val boundary: SwimlanePoint,
        val railEnd: SwimlanePoint,
        val rect: SwimlaneRect,
    )

    fun rectIntersect(
        node: SwimlaneNode,
        point: SwimlanePoint,
    ): SwimlanePoint {
        val centerX = node.x ?: 0.0
        val centerY = node.y ?: 0.0
        val deltaX = point.x - centerX
        val deltaY = point.y - centerY
        var halfWidth = node.width / 2.0
        var halfHeight = node.height / 2.0
        if (abs(deltaY) * halfWidth > abs(deltaX) * halfHeight) {
            if (deltaY < 0.0) halfHeight = -halfHeight
            return SwimlanePoint(
                x = centerX + if (deltaY == 0.0) 0.0 else halfHeight * deltaX / deltaY,
                y = centerY + halfHeight,
            )
        }
        if (deltaX < 0.0) halfWidth = -halfWidth
        return SwimlanePoint(
            x = centerX + halfWidth,
            y = centerY + if (deltaX == 0.0) 0.0 else halfWidth * deltaY / deltaX,
        )
    }

    fun terminalLaneFor(
        edge: SwimlaneEdge,
        atStart: Boolean,
    ): TerminalLane? {
        val points = dedupeConsecutivePoints(edge.points ?: emptyList())
        if (points.size < 2) return null
        val nodeId = if (atStart) edge.start else edge.end
        val node = nodeId?.let(nodeByIdMap::get) ?: return null
        val rect = rectOfNodeBounds(node) ?: return null
        val endpoint = if (atStart) points.first() else points.last()
        val adjacent = if (atStart) points[1] else points[points.lastIndex - 1]
        val boundary = rectIntersect(node, endpoint)
        val railEnd = if (orthogonallyAligned(adjacent, boundary)) adjacent else endpoint
        if (sameX(boundary, railEnd, MaterializedEpsilon)) {
            return TerminalLane(
                edge = edge,
                edgeId = edge.id,
                nodeId = nodeId,
                atStart = atStart,
                horizontal = false,
                coordinate = boundary.x,
                minimum = min(boundary.y, railEnd.y),
                maximum = max(boundary.y, railEnd.y),
                boundary = boundary,
                railEnd = railEnd,
                rect = rect,
            )
        }
        if (sameY(boundary, railEnd, MaterializedEpsilon)) {
            return TerminalLane(
                edge = edge,
                edgeId = edge.id,
                nodeId = nodeId,
                atStart = atStart,
                horizontal = true,
                coordinate = boundary.y,
                minimum = min(boundary.x, railEnd.x),
                maximum = max(boundary.x, railEnd.x),
                boundary = boundary,
                railEnd = railEnd,
                rect = rect,
            )
        }
        return null
    }

    fun projectedOverlapLength(
        first: TerminalLane,
        second: TerminalLane,
    ): Double = max(
        0.0,
        min(first.maximum, second.maximum) - max(first.minimum, second.minimum),
    )

    fun sameTerminalFace(
        first: TerminalLane,
        second: TerminalLane,
    ): Boolean {
        if (
            first.nodeId != second.nodeId ||
            first.horizontal != second.horizontal
        ) {
            return false
        }
        if (first.horizontal) {
            val firstOnHorizontalFace =
                abs(first.boundary.x - first.rect.left) < 1.0 ||
                    abs(first.boundary.x - first.rect.right) < 1.0
            return firstOnHorizontalFace &&
                sameX(first.boundary, second.boundary, 1.0)
        }
        val firstOnVerticalFace =
            abs(first.boundary.y - first.rect.top) < 1.0 ||
                abs(first.boundary.y - first.rect.bottom) < 1.0
        return firstOnVerticalFace &&
            sameY(first.boundary, second.boundary, 1.0)
    }

    fun exactTerminalLaneConflict(
        first: TerminalLane,
        second: TerminalLane,
    ): Boolean {
        if (
            first.nodeId != second.nodeId ||
            first.horizontal != second.horizontal
        ) {
            return false
        }
        return projectedOverlapLength(first, second) >= MaterializedMinimumShared &&
            abs(first.coordinate - second.coordinate) < 0.5
    }

    fun nearTerminalLaneConflict(
        first: TerminalLane,
        second: TerminalLane,
    ): Boolean {
        if (
            first.nodeId != second.nodeId ||
            first.horizontal != second.horizontal ||
            !first.horizontal ||
            first.atStart == second.atStart
        ) {
            return false
        }
        val shared = projectedOverlapLength(first, second)
        if (shared < MaterializedMinimumShared) return false
        val faceSpan = first.rect.bottom - first.rect.top
        if (shared < faceSpan || shared > 2.0 * faceSpan) return false
        return sameTerminalFace(first, second) &&
            abs(first.coordinate - second.coordinate) < minimumFaceClearance
    }

    fun shiftedCandidate(
        lane: TerminalLane,
        shift: Double,
    ): List<SwimlanePoint>? {
        val points = dedupeConsecutivePoints(lane.edge.points ?: emptyList())
        if (points.size < 2) return null
        val shiftedBoundary = if (lane.horizontal) {
            lane.boundary.copy(y = lane.boundary.y + shift)
        } else {
            lane.boundary.copy(x = lane.boundary.x + shift)
        }
        val shiftedRailEnd = if (lane.horizontal) {
            lane.railEnd.copy(y = lane.railEnd.y + shift)
        } else {
            lane.railEnd.copy(x = lane.railEnd.x + shift)
        }

        val staysOnSameFace = when {
            abs(lane.boundary.y - lane.rect.top) < 1.0 ||
                abs(lane.boundary.y - lane.rect.bottom) < 1.0 ->
                sameY(shiftedBoundary, lane.boundary, MaterializedEpsilon) &&
                    shiftedBoundary.x >= lane.rect.left + 1.0 &&
                    shiftedBoundary.x <= lane.rect.right - 1.0
            abs(lane.boundary.x - lane.rect.left) < 1.0 ||
                abs(lane.boundary.x - lane.rect.right) < 1.0 ->
                sameX(shiftedBoundary, lane.boundary, MaterializedEpsilon) &&
                    shiftedBoundary.y >= lane.rect.top + 1.0 &&
                    shiftedBoundary.y <= lane.rect.bottom - 1.0
            else -> false
        }
        if (!staysOnSameFace) return null

        if (lane.atStart) {
            val adjacent =
                points.size > 1 &&
                    samePoint(points[1], lane.railEnd, MaterializedEpsilon)
            val rest = points.drop(if (adjacent) 2 else 1)
            val next = rest.firstOrNull()
            if (next != null && !orthogonallyAligned(next, shiftedRailEnd)) return null
            return listOf(shiftedBoundary, shiftedRailEnd) + rest
        }
        val adjacent =
            points.size > 1 &&
                samePoint(points[points.lastIndex - 1], lane.railEnd, MaterializedEpsilon)
        val before = points.dropLast(if (adjacent) 2 else 1)
        val previous = before.lastOrNull()
        if (previous != null && !orthogonallyAligned(previous, shiftedRailEnd)) return null
        return before + listOf(shiftedRailEnd, shiftedBoundary)
    }

    fun laneIsStraightCollinearConnector(lane: TerminalLane): Boolean {
        val points = dedupeConsecutivePoints(lane.edge.points ?: emptyList())
        if (points.size != 2) return false
        val start = lane.edge.start?.let(nodeByIdMap::get) ?: return false
        val end = lane.edge.end?.let(nodeByIdMap::get) ?: return false
        val startX = start.x ?: 0.0
        val startY = start.y ?: 0.0
        val endX = end.x ?: 0.0
        val endY = end.y ?: 0.0
        val first = points[0]
        val second = points[1]
        return (
            sameY(first, second, MaterializedEpsilon) &&
                abs(startY - endY) < 1.0 &&
                abs(startX - endX) > 1.0
            ) || (
            sameX(first, second, MaterializedEpsilon) &&
                abs(startX - endX) < 1.0 &&
                abs(startY - endY) > 1.0
            )
    }

    val shifts = listOf(
        -trackShift,
        trackShift,
        -2.0 * trackShift,
        2.0 * trackShift,
        -3.0 * trackShift,
        3.0 * trackShift,
    )
    repeat(8) {
        val lanes = edges
            .filterNot(SwimlaneEdge::isLayoutOnly)
            .flatMap { edge ->
                listOfNotNull(
                    terminalLaneFor(edge, atStart = true),
                    terminalLaneFor(edge, atStart = false),
                )
            }
        var fixed = false
        loop@ for (firstIndex in lanes.indices) {
            for (secondIndex in firstIndex + 1 until lanes.size) {
                val first = lanes[firstIndex]
                val second = lanes[secondIndex]
                if (
                    first.edge === second.edge ||
                    !(
                        exactTerminalLaneConflict(first, second) ||
                            nearTerminalLaneConflict(first, second)
                        )
                ) {
                    continue
                }
                val fixingNearConflict = !exactTerminalLaneConflict(first, second)
                val candidates = listOf(first, second).sortedWith { a, b ->
                    val aPreserves = laneIsStraightCollinearConnector(a)
                    val bPreserves = laneIsStraightCollinearConnector(b)
                    when {
                        aPreserves != bPreserves ->
                            aPreserves.compareTo(bPreserves)
                        else ->
                            (!b.atStart).compareTo(!a.atStart)
                    }
                }
                for (lane in candidates) {
                    for (shift in shifts) {
                        val candidate = shiftedCandidate(lane, shift) ?: continue
                        val candidateEdge = SwimlaneEdge(
                            id = lane.edge.id,
                            start = lane.edge.start,
                            end = lane.edge.end,
                            originalIndex = lane.edge.originalIndex,
                            isLayoutOnly = lane.edge.isLayoutOnly,
                            hasLabel = lane.edge.hasLabel,
                            labelNodeId = lane.edge.labelNodeId,
                            points = candidate.toMutableList(),
                            x = lane.edge.x,
                            y = lane.edge.y,
                            width = lane.edge.width,
                            height = lane.edge.height,
                        )
                        val nextLane =
                            terminalLaneFor(candidateEdge, lane.atStart) ?: continue
                        val conflicts = lanes.any { other ->
                            other.edge !== lane.edge &&
                                (
                                    exactTerminalLaneConflict(nextLane, other) ||
                                        (
                                            fixingNearConflict &&
                                                nearTerminalLaneConflict(nextLane, other)
                                            )
                                    )
                        }
                        if (conflicts) continue
                        lane.edge.points = candidate.toMutableList()
                        fixed = true
                        break@loop
                    }
                }
            }
        }
        if (!fixed) return
    }
}

internal fun collapseRedundantRectangularDoglegs(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val buffer = 2.0
    val maximumIterations = 8
    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)

    fun candidateIsSafe(
        edge: SwimlaneEdge,
        candidate: List<SwimlanePoint>,
    ): Boolean {
        val candidateSegments = materializedSegmentsFor(candidate)
        if (candidateSegments.size != candidate.size - 1) return false
        val endpointIds = listOfNotNull(edge.start, edge.end)
        for (segment in candidateSegments) {
            if (
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    realNodeRects,
                    endpointIds,
                    -buffer,
                ) ||
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    labelNodeRects,
                    shrink = -buffer,
                )
            ) {
                return false
            }
        }
        for (other in edges) {
            if (other === edge || other.isLayoutOnly) continue
            val otherPoints = other.points ?: continue
            if (otherPoints.size < 2) continue
            for (candidateSegment in candidateSegments) {
                for (
                    otherSegment in materializedSegmentsFor(
                        dedupeConsecutivePoints(otherPoints),
                    )
                ) {
                    if (
                        sameAxisSegmentOverlapLength(
                            candidateSegment,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared ||
                        orthogonalSegmentsStrictlyCross(
                            candidateSegment.a,
                            candidateSegment.b,
                            otherSegment.a,
                            otherSegment.b,
                            MaterializedEpsilon,
                        )
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun withoutDogleg(
        points: List<SwimlanePoint>,
        index: Int,
    ): List<SwimlanePoint>? {
        if (index + 4 >= points.size) return null
        val p0 = points[index]
        val p1 = points[index + 1]
        val p2 = points[index + 2]
        val p3 = points[index + 3]
        val p4 = points[index + 4]
        val terminalVerticalDogleg =
            isHorizontalSegment(p0, p1) &&
                isVerticalSegment(p1, p2) &&
                isHorizontalSegment(p2, p3) &&
                isVerticalSegment(p3, p4) &&
                sameX(p0, p3, MaterializedEpsilon) &&
                sameX(p0, p4, MaterializedEpsilon) &&
                sameX(p1, p2, MaterializedEpsilon) &&
                (p1.x - p0.x) * (p3.x - p2.x) < 0.0
        val terminalHorizontalDogleg =
            isVerticalSegment(p0, p1) &&
                isHorizontalSegment(p1, p2) &&
                isVerticalSegment(p2, p3) &&
                isHorizontalSegment(p3, p4) &&
                sameY(p0, p3, MaterializedEpsilon) &&
                sameY(p0, p4, MaterializedEpsilon) &&
                sameY(p1, p2, MaterializedEpsilon) &&
                (p1.y - p0.y) * (p3.y - p2.y) < 0.0
        if (terminalVerticalDogleg || terminalHorizontalDogleg) {
            return dedupeConsecutivePoints(
                points.take(index + 1) + p4 + points.drop(index + 5),
            )
        }
        if (index + 5 >= points.size) return null
        val p5 = points[index + 5]
        val verticalDogleg =
            isVerticalSegment(p0, p1) &&
                isHorizontalSegment(p1, p2) &&
                isVerticalSegment(p2, p3) &&
                isHorizontalSegment(p3, p4) &&
                isVerticalSegment(p4, p5) &&
                sameX(p0, p4, MaterializedEpsilon) &&
                sameX(p0, p5, MaterializedEpsilon) &&
                sameX(p2, p3, MaterializedEpsilon) &&
                (p2.x - p1.x) * (p4.x - p3.x) < 0.0
        val horizontalDogleg =
            isHorizontalSegment(p0, p1) &&
                isVerticalSegment(p1, p2) &&
                isHorizontalSegment(p2, p3) &&
                isVerticalSegment(p3, p4) &&
                isHorizontalSegment(p4, p5) &&
                sameY(p0, p4, MaterializedEpsilon) &&
                sameY(p0, p5, MaterializedEpsilon) &&
                sameY(p2, p3, MaterializedEpsilon) &&
                (p2.y - p1.y) * (p4.y - p3.y) < 0.0
        if (!verticalDogleg && !horizontalDogleg) return null
        return dedupeConsecutivePoints(
            points.take(index + 1) + p5 + points.drop(index + 6),
        )
    }

    repeat(maximumIterations) {
        var fixed = false
        edgeLoop@ for (edge in edges) {
            if (edge.isLayoutOnly) continue
            val points = dedupeConsecutivePoints(edge.points ?: emptyList())
            for (index in 0..points.size - 5) {
                val candidate = withoutDogleg(points, index) ?: continue
                if (!candidateIsSafe(edge, candidate)) continue
                edge.points = candidate.toMutableList()
                fixed = true
                break@edgeLoop
            }
        }
        if (!fixed) return
    }
}

internal fun liftObstacleHuggingSameSideRails(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val buffer = 2.0
    val clearance = 20.0
    val maximumIterations = 8
    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)
    val visibleEdges = edges.filterNot(SwimlaneEdge::isLayoutOnly)

    fun pointsFor(
        edge: SwimlaneEdge,
        replacementEdge: SwimlaneEdge? = null,
        replacement: List<SwimlanePoint>? = null,
    ): List<SwimlanePoint> = dedupeConsecutivePoints(
        if (edge === replacementEdge) replacement.orEmpty() else edge.points.orEmpty(),
    )

    fun strictCrossingCount(
        replacementEdge: SwimlaneEdge? = null,
        replacement: List<SwimlanePoint>? = null,
    ): Int {
        var count = 0
        for (firstIndex in visibleEdges.indices) {
            val firstSegments = materializedSegmentsFor(
                pointsFor(visibleEdges[firstIndex], replacementEdge, replacement),
            )
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val secondSegments = materializedSegmentsFor(
                    pointsFor(visibleEdges[secondIndex], replacementEdge, replacement),
                )
                for (firstSegment in firstSegments) {
                    for (secondSegment in secondSegments) {
                        if (
                            orthogonalSegmentsStrictlyCross(
                                firstSegment.a,
                                firstSegment.b,
                                secondSegment.a,
                                secondSegment.b,
                                MaterializedEpsilon,
                            )
                        ) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun middleRail(points: List<SwimlanePoint>): OrthogonalSegment? {
        val segments = materializedSegmentsFor(points)
        if (segments.size != 3) return null
        val middle = segments[1]
        if (
            segments[0].horizontal == middle.horizontal ||
            segments[2].horizontal == middle.horizontal
        ) {
            return null
        }
        return middle
    }

    fun blockingRectsFor(
        edge: SwimlaneEdge,
        rail: OrthogonalSegment,
    ): List<RectEntry> {
        val endpointIds = listOfNotNull(edge.start, edge.end)
        return realNodeRects.filter { entry ->
            if (entry.id in endpointIds) {
                false
            } else if (rail.horizontal) {
                overlapLength(rail.a.x, rail.b.x, entry.rect.left, entry.rect.right) >=
                    MaterializedMinimumShared &&
                    rail.a.y >= entry.rect.top - buffer &&
                    rail.a.y <= entry.rect.bottom + buffer
            } else {
                overlapLength(rail.a.y, rail.b.y, entry.rect.top, entry.rect.bottom) >=
                    MaterializedMinimumShared &&
                    rail.a.x >= entry.rect.left - buffer &&
                    rail.a.x <= entry.rect.right + buffer
            }
        }
    }

    fun candidateByMovingRail(
        points: List<SwimlanePoint>,
        rail: OrthogonalSegment,
        coordinate: Double,
    ): List<SwimlanePoint>? {
        val candidate = points.toMutableList()
        if (rail.horizontal) {
            candidate[rail.index] = candidate[rail.index].copy(y = coordinate)
            candidate[rail.index + 1] = candidate[rail.index + 1].copy(y = coordinate)
        } else if (rail.vertical) {
            candidate[rail.index] = candidate[rail.index].copy(x = coordinate)
            candidate[rail.index + 1] = candidate[rail.index + 1].copy(x = coordinate)
        } else {
            return null
        }
        val simplified = simplifyPolyline(dedupeConsecutivePoints(candidate))
        return if (
            materializedSegmentsFor(simplified).size == simplified.size - 1
        ) {
            simplified
        } else {
            null
        }
    }

    fun candidateIsSafe(
        edge: SwimlaneEdge,
        candidate: List<SwimlanePoint>,
        currentCrossings: Int,
    ): Boolean {
        val endpointIds = listOfNotNull(edge.start, edge.end)
        val candidateSegments = materializedSegmentsFor(candidate)
        if (candidateSegments.size != candidate.size - 1) return false
        for (segment in candidateSegments) {
            if (
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    realNodeRects,
                    endpointIds,
                    -buffer,
                ) ||
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    labelNodeRects,
                    shrink = -buffer,
                )
            ) {
                return false
            }
        }
        for (other in visibleEdges) {
            if (other === edge) continue
            for (candidateSegment in candidateSegments) {
                for (otherSegment in materializedSegmentsFor(pointsFor(other))) {
                    if (
                        sameAxisSegmentOverlapLength(
                            candidateSegment,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared
                    ) {
                        return false
                    }
                }
            }
        }
        return strictCrossingCount(edge, candidate) <= currentCrossings
    }

    repeat(maximumIterations) {
        val currentCrossings = strictCrossingCount()
        var fixed = false
        edgeLoop@ for (edge in visibleEdges) {
            val points = pointsFor(edge)
            val rail = middleRail(points) ?: continue
            val blockers = blockingRectsFor(edge, rail)
            if (blockers.isEmpty()) continue
            val coordinates = if (rail.horizontal) {
                listOf(
                    blockers.minOf { entry -> entry.rect.top } - clearance,
                    blockers.maxOf { entry -> entry.rect.bottom } + clearance,
                )
            } else {
                listOf(
                    blockers.minOf { entry -> entry.rect.left } - clearance,
                    blockers.maxOf { entry -> entry.rect.right } + clearance,
                )
            }
            for (coordinate in coordinates) {
                val candidate = candidateByMovingRail(points, rail, coordinate) ?: continue
                if (!candidateIsSafe(edge, candidate, currentCrossings)) continue
                edge.points = candidate.toMutableList()
                fixed = true
                break@edgeLoop
            }
        }
        if (!fixed) return
    }
}

private data class MaterializedLaneTitle(
    val node: SwimlaneNode,
    val rect: SwimlaneRect,
)

private fun validMaterializedTitleRect(node: SwimlaneNode): SwimlaneRect? {
    val rect = node.groupTitleRect ?: return null
    if (
        !rect.left.isFinite() ||
        !rect.right.isFinite() ||
        !rect.top.isFinite() ||
        !rect.bottom.isFinite() ||
        rect.right <= rect.left ||
        rect.bottom <= rect.top
    ) {
        return null
    }
    return rect
}

internal fun liftTopLaneTitleBandsAboveRails(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val clearance = 4.0
    val lanes = nodeByIdMap.values.mapNotNull { node ->
        if (
            !node.isGroup ||
            node.parentId != null ||
            node.direction == SwimlaneDirection.LR ||
            node.direction == SwimlaneDirection.RL ||
            node.direction == SwimlaneDirection.BT
        ) {
            return@mapNotNull null
        }
        val rect = validMaterializedTitleRect(node) ?: return@mapNotNull null
        val centerY = node.y ?: return@mapNotNull null
        if (!centerY.isFinite() || !node.height.isFinite() || node.height <= 0.0) {
            return@mapNotNull null
        }
        val titleWidth = rect.right - rect.left
        val titleHeight = rect.bottom - rect.top
        if (titleHeight <= 0.0 || titleWidth < titleHeight) {
            null
        } else {
            MaterializedLaneTitle(node, rect)
        }
    }
    if (lanes.isEmpty()) return

    var topDelta = 0.0
    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = dedupeConsecutivePoints(edge.points ?: emptyList())
        for (segment in materializedSegmentsFor(points)) {
            if (!segment.horizontal) continue
            for (lane in lanes) {
                val y = segment.a.y
                if (
                    y > lane.rect.top + MaterializedEpsilon &&
                    y < lane.rect.bottom - MaterializedEpsilon &&
                    overlapLength(
                        segment.a.x,
                        segment.b.x,
                        lane.rect.left,
                        lane.rect.right,
                    ) >= MaterializedMinimumShared
                ) {
                    topDelta = max(
                        topDelta,
                        lane.rect.bottom - segment.a.y + clearance,
                    )
                }
            }
        }
    }
    if (topDelta <= MaterializedEpsilon) return
    for (lane in lanes) {
        val centerY = lane.node.y ?: continue
        if (!centerY.isFinite() || !lane.node.height.isFinite() || lane.node.height <= 0.0) {
            continue
        }
        lane.node.y = centerY - topDelta / 2.0
        lane.node.height += topDelta
        lane.node.groupTitleRect = lane.rect.copy(
            top = lane.rect.top - topDelta,
            bottom = lane.rect.bottom - topDelta,
        )
    }
}

internal fun shiftLeftLaneTitleBandsLeftOfRails(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val clearance = 4.0
    val lanes = nodeByIdMap.values.mapNotNull { node ->
        if (
            !node.isGroup ||
            node.parentId != null ||
            node.direction != SwimlaneDirection.LR
        ) {
            return@mapNotNull null
        }
        val rect = validMaterializedTitleRect(node) ?: return@mapNotNull null
        val centerX = node.x ?: return@mapNotNull null
        if (!centerX.isFinite() || !node.width.isFinite() || node.width <= 0.0) {
            return@mapNotNull null
        }
        val titleWidth = rect.right - rect.left
        val titleHeight = rect.bottom - rect.top
        if (titleWidth <= 0.0 || titleHeight < titleWidth) {
            null
        } else {
            MaterializedLaneTitle(node, rect)
        }
    }
    if (lanes.isEmpty()) return

    var leftDelta = 0.0
    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = dedupeConsecutivePoints(edge.points ?: emptyList())
        for (segment in materializedSegmentsFor(points)) {
            for (lane in lanes) {
                if (
                    segment.vertical &&
                    segment.a.x > lane.rect.left + MaterializedEpsilon &&
                    segment.a.x < lane.rect.right - MaterializedEpsilon &&
                    overlapLength(
                        segment.a.y,
                        segment.b.y,
                        lane.rect.top,
                        lane.rect.bottom,
                    ) >= MaterializedMinimumShared
                ) {
                    leftDelta = max(
                        leftDelta,
                        lane.rect.right - segment.a.x + clearance,
                    )
                } else if (
                    segment.horizontal &&
                    segment.a.y > lane.rect.top + MaterializedEpsilon &&
                    segment.a.y < lane.rect.bottom - MaterializedEpsilon &&
                    overlapLength(
                        segment.a.x,
                        segment.b.x,
                        lane.rect.left,
                        lane.rect.right,
                    ) >= MaterializedMinimumShared
                ) {
                    leftDelta = max(
                        leftDelta,
                        lane.rect.right - min(segment.a.x, segment.b.x) + clearance,
                    )
                }
            }
        }
    }
    if (leftDelta <= MaterializedEpsilon) return
    for (lane in lanes) {
        val centerX = lane.node.x ?: continue
        if (!centerX.isFinite() || !lane.node.width.isFinite() || lane.node.width <= 0.0) {
            continue
        }
        lane.node.x = centerX - leftDelta / 2.0
        lane.node.width += leftDelta
        lane.node.groupTitleRect = lane.rect.copy(
            left = lane.rect.left - leftDelta,
            right = lane.rect.right - leftDelta,
        )
    }
}

private fun materializedPathKey(points: List<SwimlanePoint>): String =
    points.joinToString("|") { point ->
        "${round(point.x * 1_000.0).toLong()},${round(point.y * 1_000.0).toLong()}"
    }

internal fun swapDestinationTerminalTailsToReduceCrossings(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val buffer = 2.0
    val maximumIterations = 4

    data class TerminalTail(
        val tailStart: SwimlanePoint,
        val terminal: SwimlanePoint,
    )

    val realNodeRects = collectNodeRectEntries(nodeByIdMap.values).realNodeRects
    val visibleEdges = edges.filterNot(SwimlaneEdge::isLayoutOnly)

    fun replacementPointsFor(
        edge: SwimlaneEdge,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): List<SwimlanePoint> = dedupeConsecutivePoints(
        replacements[edge] ?: edge.points.orEmpty(),
    )

    fun crossingCount(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Int {
        var count = 0
        for (firstIndex in visibleEdges.indices) {
            val firstSegments =
                materializedSegmentsFor(replacementPointsFor(visibleEdges[firstIndex], replacements))
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val secondSegments =
                    materializedSegmentsFor(
                        replacementPointsFor(visibleEdges[secondIndex], replacements),
                    )
                for (first in firstSegments) {
                    for (second in secondSegments) {
                        if (
                            orthogonalSegmentsStrictlyCross(
                                first.a,
                                first.b,
                                second.a,
                                second.b,
                                MaterializedEpsilon,
                            )
                        ) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun totalBends(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Int = visibleEdges.sumOf { edge ->
        countOrthogonalBends(replacementPointsFor(edge, replacements))
    }

    fun terminalTailFor(edge: SwimlaneEdge): TerminalTail? {
        val points = replacementPointsFor(edge)
        if (points.size < 4) return null
        val tailStart = points[points.lastIndex - 1]
        val terminal = points.last()
        if (
            !isHorizontalSegment(tailStart, terminal, MaterializedEpsilon) &&
            !isVerticalSegment(tailStart, terminal, MaterializedEpsilon)
        ) {
            return null
        }
        return TerminalTail(tailStart, terminal)
    }

    fun candidateWithDestinationTail(
        edge: SwimlaneEdge,
        tail: TerminalTail,
    ): List<SwimlanePoint>? {
        val points = replacementPointsFor(edge)
        if (points.size < 3) return null
        val start = points[0]
        val firstTurn = points[1]
        val connector = when {
            isHorizontalSegment(start, firstTurn, MaterializedEpsilon) ->
                SwimlanePoint(firstTurn.x, tail.tailStart.y)
            isVerticalSegment(start, firstTurn, MaterializedEpsilon) ->
                SwimlanePoint(tail.tailStart.x, firstTurn.y)
            else -> return null
        }
        val candidate = simplifyPolyline(
            dedupeConsecutivePoints(
                listOf(start, firstTurn, connector, tail.tailStart, tail.terminal),
            ),
        )
        return if (
            materializedSegmentsFor(candidate).size == candidate.size - 1
        ) {
            candidate
        } else {
            null
        }
    }

    fun pathHasNodeHit(
        edge: SwimlaneEdge,
        path: List<SwimlanePoint>,
    ): Boolean {
        val endpointIds = listOfNotNull(edge.start, edge.end)
        return materializedSegmentsFor(path).any { segment ->
            segmentHitsAnyRect(
                segment.a,
                segment.b,
                realNodeRects,
                endpointIds,
                -buffer,
            )
        }
    }

    fun pathHasSharedTrack(
        edge: SwimlaneEdge,
        path: List<SwimlanePoint>,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>>,
    ): Boolean {
        for (other in visibleEdges) {
            if (other === edge) continue
            for (candidateSegment in materializedSegmentsFor(path)) {
                for (
                    otherSegment in materializedSegmentsFor(
                        replacementPointsFor(other, replacements),
                    )
                ) {
                    if (
                        sameAxisSegmentOverlapLength(
                            candidateSegment,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun candidateIsSafe(
        edge: SwimlaneEdge,
        path: List<SwimlanePoint>,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>>,
    ): Boolean = !pathHasNodeHit(edge, path) &&
        !pathHasSharedTrack(edge, path, replacements)

    fun edgesByDestination(): Map<String, List<SwimlaneEdge>> {
        val result = linkedMapOf<String, MutableList<SwimlaneEdge>>()
        for (edge in visibleEdges) {
            val destinationId = edge.end ?: continue
            if (destinationId !in nodeByIdMap || replacementPointsFor(edge).size < 4) continue
            result.getOrPut(destinationId, ::mutableListOf) += edge
        }
        return result
    }

    repeat(maximumIterations) {
        val currentCrossings = crossingCount()
        if (currentCrossings == 0) return
        val currentBends = totalBends()
        var bestReplacements: Map<SwimlaneEdge, List<SwimlanePoint>>? = null
        var bestCrossings = currentCrossings
        var bestBends = currentBends
        for (destinationEdges in edgesByDestination().values) {
            for (firstIndex in destinationEdges.indices) {
                for (secondIndex in firstIndex + 1 until destinationEdges.size) {
                    val first = destinationEdges[firstIndex]
                    val second = destinationEdges[secondIndex]
                    val firstTail = terminalTailFor(first) ?: continue
                    val secondTail = terminalTailFor(second) ?: continue
                    val firstCandidate =
                        candidateWithDestinationTail(first, secondTail) ?: continue
                    val secondCandidate =
                        candidateWithDestinationTail(second, firstTail) ?: continue
                    val replacements = linkedMapOf<SwimlaneEdge, List<SwimlanePoint>>(
                        first to firstCandidate,
                        second to secondCandidate,
                    )
                    if (
                        !candidateIsSafe(first, firstCandidate, replacements) ||
                        !candidateIsSafe(second, secondCandidate, replacements)
                    ) {
                        continue
                    }
                    val candidateCrossings = crossingCount(replacements)
                    val candidateBends = totalBends(replacements)
                    if (candidateCrossings >= currentCrossings) continue
                    if (
                        candidateCrossings > bestCrossings ||
                        (
                            candidateCrossings == bestCrossings &&
                                candidateBends >= bestBends
                            )
                    ) {
                        continue
                    }
                    bestReplacements = replacements
                    bestCrossings = candidateCrossings
                    bestBends = candidateBends
                }
            }
        }
        val replacements = bestReplacements ?: return
        for ((edge, points) in replacements) edge.points = points.toMutableList()
    }
}

private enum class ExternalRailAxis {
    Horizontal,
    Vertical,
}

internal fun reassignCrossingExternalRailChannels(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val buffer = 2.0
    val railChannelGap = 12.0
    val maximumIterations = 4
    val maximumExhaustiveComponent = 6

    data class ExternalRail(
        val edge: SwimlaneEdge,
        val points: List<SwimlanePoint>,
        val segmentIndex: Int,
        val axis: ExternalRailAxis,
        val side: RectSide,
        val coordinate: Double,
        val minimum: Double,
        val maximum: Double,
    )

    data class EndpointRects(
        val source: SwimlaneRect,
        val destination: SwimlaneRect,
    )

    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)
    val visibleEdges = edges.filterNot(SwimlaneEdge::isLayoutOnly)

    fun replacementPointsFor(
        edge: SwimlaneEdge,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): List<SwimlanePoint> = dedupeConsecutivePoints(
        replacements[edge] ?: edge.points.orEmpty(),
    )

    fun strictCrossingCount(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Int {
        var count = 0
        for (firstIndex in visibleEdges.indices) {
            val firstSegments =
                materializedSegmentsFor(replacementPointsFor(visibleEdges[firstIndex], replacements))
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val secondSegments =
                    materializedSegmentsFor(
                        replacementPointsFor(visibleEdges[secondIndex], replacements),
                    )
                for (first in firstSegments) {
                    for (second in secondSegments) {
                        if (
                            orthogonalSegmentsStrictlyCross(
                                first.a,
                                first.b,
                                second.a,
                                second.b,
                                MaterializedEpsilon,
                            )
                        ) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun totalBends(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Int = visibleEdges.sumOf { edge ->
        countOrthogonalBends(replacementPointsFor(edge, replacements))
    }

    fun endpointRectsFor(edge: SwimlaneEdge): EndpointRects? {
        val source = edge.start?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds)
        val destination = edge.end?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds)
        return if (source != null && destination != null) {
            EndpointRects(source, destination)
        } else {
            null
        }
    }

    fun externalRailForSegment(
        edge: SwimlaneEdge,
        points: List<SwimlanePoint>,
        segment: OrthogonalSegment,
    ): ExternalRail? {
        if (segment.index <= 0 || segment.index + 1 >= points.lastIndex) return null
        val endpoints = endpointRectsFor(edge) ?: return null
        if (segment.vertical) {
            val coordinate = segment.a.x
            val leftBound = min(endpoints.source.left, endpoints.destination.left)
            val rightBound = max(endpoints.source.right, endpoints.destination.right)
            val side = when {
                coordinate < leftBound - MaterializedEpsilon -> RectSide.Left
                coordinate > rightBound + MaterializedEpsilon -> RectSide.Right
                else -> return null
            }
            return ExternalRail(
                edge,
                points,
                segment.index,
                ExternalRailAxis.Vertical,
                side,
                coordinate,
                min(segment.a.y, segment.b.y),
                max(segment.a.y, segment.b.y),
            )
        }
        if (segment.horizontal) {
            val coordinate = segment.a.y
            val topBound = min(endpoints.source.top, endpoints.destination.top)
            val bottomBound = max(endpoints.source.bottom, endpoints.destination.bottom)
            val side = when {
                coordinate < topBound - MaterializedEpsilon -> RectSide.Top
                coordinate > bottomBound + MaterializedEpsilon -> RectSide.Bottom
                else -> return null
            }
            return ExternalRail(
                edge,
                points,
                segment.index,
                ExternalRailAxis.Horizontal,
                side,
                coordinate,
                min(segment.a.x, segment.b.x),
                max(segment.a.x, segment.b.x),
            )
        }
        return null
    }

    fun collectExternalRails(): List<ExternalRail> = buildList {
        for (edge in visibleEdges) {
            val points = replacementPointsFor(edge)
            for (segment in materializedSegmentsFor(points)) {
                externalRailForSegment(edge, points, segment)?.let(::add)
            }
        }
    }

    fun railsInteract(
        first: ExternalRail,
        second: ExternalRail,
    ): Boolean = first.edge !== second.edge &&
        first.axis == second.axis &&
        first.side == second.side &&
        overlapLength(
            first.minimum,
            first.maximum,
            second.minimum,
            second.maximum,
        ) >= MaterializedMinimumShared

    fun connectedComponents(rails: List<ExternalRail>): List<List<ExternalRail>> {
        val result = mutableListOf<List<ExternalRail>>()
        val seen = mutableSetOf<ExternalRail>()
        for (rail in rails) {
            if (rail in seen) continue
            val queue = mutableListOf(rail)
            val component = mutableListOf<ExternalRail>()
            seen += rail
            while (queue.isNotEmpty()) {
                val current = queue.removeAt(queue.lastIndex)
                component += current
                for (next in rails) {
                    if (next !in seen && railsInteract(current, next)) {
                        seen += next
                        queue += next
                    }
                }
            }
            if (component.size > 1) result += component
        }
        return result
    }

    fun uniqueCoordinatesFor(component: List<ExternalRail>): List<Double> {
        val coordinates = mutableListOf<Double>()
        for (rail in component) {
            if (
                coordinates.none { coordinate ->
                    abs(coordinate - rail.coordinate) < MaterializedEpsilon
                }
            ) {
                coordinates += rail.coordinate
            }
        }
        while (coordinates.size < component.size) {
            val minimum = coordinates.min()
            val maximum = coordinates.max()
            val distance = railChannelGap * (component.size - coordinates.size)
            coordinates += if (
                component.first().side == RectSide.Left ||
                component.first().side == RectSide.Top
            ) {
                minimum - distance
            } else {
                maximum + distance
            }
        }
        return coordinates
    }

    fun coordinateAssignmentsFor(component: List<ExternalRail>): List<List<Double>> {
        val current = component.map(ExternalRail::coordinate)
        val coordinates = uniqueCoordinatesFor(component)
        val assignments = mutableListOf<List<Double>>()
        if (component.size <= maximumExhaustiveComponent) {
            val used = BooleanArray(coordinates.size)
            val next = mutableListOf<Double>()
            fun visit() {
                if (next.size == component.size) {
                    if (
                        next.indices.any { index ->
                            abs(next[index] - current[index]) >= MaterializedEpsilon
                        }
                    ) {
                        assignments += next.toList()
                    }
                    return
                }
                for (index in coordinates.indices) {
                    if (used[index]) continue
                    used[index] = true
                    next += coordinates[index]
                    visit()
                    next.removeAt(next.lastIndex)
                    used[index] = false
                }
            }
            visit()
            return assignments
        }
        for (firstIndex in current.indices) {
            for (secondIndex in firstIndex + 1 until current.size) {
                val assignment = current.toMutableList()
                val temporary = assignment[firstIndex]
                assignment[firstIndex] = assignment[secondIndex]
                assignment[secondIndex] = temporary
                assignments += assignment
            }
        }
        return assignments
    }

    fun replacementsForAssignment(
        component: List<ExternalRail>,
        assignment: List<Double>,
    ): Map<SwimlaneEdge, List<SwimlanePoint>>? {
        val drafts = linkedMapOf<SwimlaneEdge, MutableList<SwimlanePoint>>()
        for ((index, rail) in component.withIndex()) {
            val coordinate = assignment[index]
            val points = drafts.getOrPut(rail.edge) { rail.points.toMutableList() }
            if (rail.axis == ExternalRailAxis.Vertical) {
                points[rail.segmentIndex] =
                    points[rail.segmentIndex].copy(x = coordinate)
                points[rail.segmentIndex + 1] =
                    points[rail.segmentIndex + 1].copy(x = coordinate)
            } else {
                points[rail.segmentIndex] =
                    points[rail.segmentIndex].copy(y = coordinate)
                points[rail.segmentIndex + 1] =
                    points[rail.segmentIndex + 1].copy(y = coordinate)
            }
        }
        val replacements = linkedMapOf<SwimlaneEdge, List<SwimlanePoint>>()
        for ((edge, points) in drafts) {
            val simplified = simplifyPolyline(dedupeConsecutivePoints(points))
            if (materializedSegmentsFor(simplified).size != simplified.size - 1) {
                return null
            }
            replacements[edge] = simplified
        }
        return replacements
    }

    fun candidateIsSafe(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>>,
    ): Boolean {
        for ((edge, points) in replacements) {
            val endpointIds = listOfNotNull(edge.start, edge.end)
            for (segment in materializedSegmentsFor(points)) {
                if (
                    segmentHitsAnyRect(
                        segment.a,
                        segment.b,
                        realNodeRects,
                        endpointIds,
                        -buffer,
                    ) ||
                    segmentHitsAnyRect(
                        segment.a,
                        segment.b,
                        labelNodeRects,
                        shrink = -buffer,
                    )
                ) {
                    return false
                }
            }
        }
        for (firstIndex in visibleEdges.indices) {
            val first = visibleEdges[firstIndex]
            val firstChanged = first in replacements
            val firstSegments =
                materializedSegmentsFor(replacementPointsFor(first, replacements))
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val second = visibleEdges[secondIndex]
                if (!firstChanged && second !in replacements) continue
                val secondSegments =
                    materializedSegmentsFor(replacementPointsFor(second, replacements))
                for (firstSegment in firstSegments) {
                    for (secondSegment in secondSegments) {
                        if (
                            sameAxisSegmentOverlapLength(
                                firstSegment,
                                secondSegment,
                                0.5,
                            ) >= MaterializedMinimumShared
                        ) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    repeat(maximumIterations) {
        val currentCrossings = strictCrossingCount()
        if (currentCrossings == 0) return
        var bestReplacements: Map<SwimlaneEdge, List<SwimlanePoint>>? = null
        var bestCrossings = currentCrossings
        var bestBends = totalBends()
        var bestDisplacement = Double.POSITIVE_INFINITY
        for (component in connectedComponents(collectExternalRails())) {
            for (assignment in coordinateAssignmentsFor(component)) {
                val replacements =
                    replacementsForAssignment(component, assignment) ?: continue
                if (!candidateIsSafe(replacements)) continue
                val candidateCrossings = strictCrossingCount(replacements)
                if (candidateCrossings >= currentCrossings) continue
                val candidateBends = totalBends(replacements)
                val candidateDisplacement = component.indices.sumOf { index ->
                    abs(assignment[index] - component[index].coordinate)
                }
                if (
                    candidateCrossings > bestCrossings ||
                    (
                        candidateCrossings == bestCrossings &&
                            (
                                candidateBends > bestBends ||
                                    (
                                        candidateBends == bestBends &&
                                            candidateDisplacement >= bestDisplacement
                                        )
                                )
                        )
                ) {
                    continue
                }
                bestReplacements = replacements
                bestCrossings = candidateCrossings
                bestBends = candidateBends
                bestDisplacement = candidateDisplacement
            }
        }
        val replacements = bestReplacements ?: return
        for ((edge, points) in replacements) edge.points = points.toMutableList()
    }
}

internal fun shortcutRedundantOrthogonalJogs(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val buffer = 2.0
    val maximumIterations = 8
    val (realNodeRects, labelNodeRects) = collectNodeRectEntries(nodeByIdMap.values)
    val visibleEdges = edges.filterNot(SwimlaneEdge::isLayoutOnly)

    fun pointsFor(
        edge: SwimlaneEdge,
        replacementEdge: SwimlaneEdge? = null,
        replacement: List<SwimlanePoint>? = null,
    ): List<SwimlanePoint> = dedupeConsecutivePoints(
        if (edge === replacementEdge) replacement.orEmpty() else edge.points.orEmpty(),
    )

    fun pathLength(points: List<SwimlanePoint>): Double =
        materializedSegmentsFor(points).sumOf { segment ->
            hypot(segment.a.x - segment.b.x, segment.a.y - segment.b.y)
        }

    fun strictCrossingCount(
        replacementEdge: SwimlaneEdge? = null,
        replacement: List<SwimlanePoint>? = null,
    ): Int {
        var count = 0
        for (firstIndex in visibleEdges.indices) {
            val firstSegments =
                materializedSegmentsFor(
                    pointsFor(visibleEdges[firstIndex], replacementEdge, replacement),
                )
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val secondSegments =
                    materializedSegmentsFor(
                        pointsFor(visibleEdges[secondIndex], replacementEdge, replacement),
                    )
                for (first in firstSegments) {
                    for (second in secondSegments) {
                        if (
                            orthogonalSegmentsStrictlyCross(
                                first.a,
                                first.b,
                                second.a,
                                second.b,
                                MaterializedEpsilon,
                            )
                        ) {
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    fun segmentRunsAlongRectBorder(
        segment: OrthogonalSegment,
        rect: SwimlaneRect,
    ): Boolean {
        if (segment.horizontal) {
            val onBorder =
                abs(segment.a.y - rect.top) < 1.0 ||
                    abs(segment.a.y - rect.bottom) < 1.0
            return onBorder &&
                overlapLength(segment.a.x, segment.b.x, rect.left, rect.right) >=
                MaterializedMinimumShared
        }
        if (segment.vertical) {
            val onBorder =
                abs(segment.a.x - rect.left) < 1.0 ||
                    abs(segment.a.x - rect.right) < 1.0
            return onBorder &&
                overlapLength(segment.a.y, segment.b.y, rect.top, rect.bottom) >=
                MaterializedMinimumShared
        }
        return false
    }

    fun endpointRectsFor(edge: SwimlaneEdge): List<SwimlaneRect> =
        listOfNotNull(
            edge.start?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds),
            edge.end?.let(nodeByIdMap::get)?.let(::rectOfNodeBounds),
        )

    fun shortcutCandidatesAt(
        points: List<SwimlanePoint>,
        index: Int,
    ): List<List<SwimlanePoint>> {
        if (index + 3 >= points.size) return emptyList()
        val p0 = points[index]
        val p1 = points[index + 1]
        val p2 = points[index + 2]
        val p3 = points[index + 3]
        val horizontalVerticalHorizontal =
            isHorizontalSegment(p0, p1, MaterializedEpsilon) &&
                isVerticalSegment(p1, p2, MaterializedEpsilon) &&
                isHorizontalSegment(p2, p3, MaterializedEpsilon)
        val verticalHorizontalVertical =
            isVerticalSegment(p0, p1, MaterializedEpsilon) &&
                isHorizontalSegment(p1, p2, MaterializedEpsilon) &&
                isVerticalSegment(p2, p3, MaterializedEpsilon)
        if (!horizontalVerticalHorizontal && !verticalHorizontalVertical) {
            return emptyList()
        }
        val outerSegmentsOppose = if (horizontalVerticalHorizontal) {
            sign(p1.x - p0.x) != sign(p3.x - p2.x)
        } else {
            sign(p1.y - p0.y) != sign(p3.y - p2.y)
        }
        if (!outerSegmentsOppose) return emptyList()
        val rawCandidates = if (
            sameX(p0, p3, MaterializedEpsilon) ||
            sameY(p0, p3, MaterializedEpsilon)
        ) {
            listOf(points.take(index + 1) + points.drop(index + 3))
        } else {
            listOf(
                points.take(index + 1) +
                    SwimlanePoint(p0.x, p3.y) +
                    points.drop(index + 3),
                points.take(index + 1) +
                    SwimlanePoint(p3.x, p0.y) +
                    points.drop(index + 3),
            )
        }
        val seen = mutableSetOf<String>()
        return rawCandidates.mapNotNull { raw ->
            val candidate = simplifyPolyline(dedupeConsecutivePoints(raw))
            val key = materializedPathKey(candidate)
            if (
                materializedSegmentsFor(candidate).size != candidate.size - 1 ||
                candidate.none { point -> samePoint(point, p3, MaterializedEpsilon) } ||
                !seen.add(key)
            ) {
                null
            } else {
                candidate
            }
        }
    }

    fun candidateIsSafe(
        edge: SwimlaneEdge,
        candidate: List<SwimlanePoint>,
        currentCrossings: Int,
    ): Boolean {
        val endpointIds = listOfNotNull(edge.start, edge.end)
        val endpointRects = endpointRectsFor(edge)
        for (segment in materializedSegmentsFor(candidate)) {
            if (
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    realNodeRects,
                    endpointIds,
                    -buffer,
                ) ||
                segmentHitsAnyRect(
                    segment.a,
                    segment.b,
                    labelNodeRects,
                    shrink = -buffer,
                ) ||
                endpointRects.any { rect -> segmentRunsAlongRectBorder(segment, rect) }
            ) {
                return false
            }
        }
        for (other in visibleEdges) {
            if (other === edge) continue
            for (candidateSegment in materializedSegmentsFor(candidate)) {
                for (otherSegment in materializedSegmentsFor(pointsFor(other))) {
                    if (
                        sameAxisSegmentOverlapLength(
                            candidateSegment,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared
                    ) {
                        return false
                    }
                }
            }
        }
        return strictCrossingCount(edge, candidate) <= currentCrossings
    }

    repeat(maximumIterations) {
        val currentCrossings = strictCrossingCount()
        var bestEdge: SwimlaneEdge? = null
        var bestPath: List<SwimlanePoint>? = null
        var bestCrossings = currentCrossings
        var bestBends = Int.MAX_VALUE
        var bestLength = Double.POSITIVE_INFINITY
        for (edge in visibleEdges) {
            val currentPoints = pointsFor(edge)
            val currentBends =
                countOrthogonalBends(currentPoints, MaterializedEpsilon)
            val currentLength = pathLength(currentPoints)
            for (index in 0..currentPoints.size - 4) {
                for (candidate in shortcutCandidatesAt(currentPoints, index)) {
                    val candidateBends =
                        countOrthogonalBends(candidate, MaterializedEpsilon)
                    val candidateLength = pathLength(candidate)
                    val improvesShape =
                        candidateBends < currentBends ||
                            (
                                candidateBends == currentBends &&
                                    candidateLength < currentLength - MaterializedEpsilon
                                )
                    if (
                        !improvesShape ||
                        !candidateIsSafe(edge, candidate, currentCrossings)
                    ) {
                        continue
                    }
                    val candidateCrossings = strictCrossingCount(edge, candidate)
                    if (
                        candidateCrossings > bestCrossings ||
                        (
                            candidateCrossings == bestCrossings &&
                                (
                                    candidateBends > bestBends ||
                                        (
                                            candidateBends == bestBends &&
                                                candidateLength >= bestLength
                                            )
                                    )
                            )
                    ) {
                        continue
                    }
                    bestEdge = edge
                    bestPath = candidate
                    bestCrossings = candidateCrossings
                    bestBends = candidateBends
                    bestLength = candidateLength
                }
            }
        }
        val edge = bestEdge ?: return
        val path = bestPath ?: return
        edge.points = path.toMutableList()
    }
}

internal fun resolveRenderedOrthogonalCrossings(
    edges: List<SwimlaneEdge>,
    nodeByIdMap: Map<String, SwimlaneNode>,
) {
    val anchor = 20.0
    val extraChannelCount = 2
    val maximumIterations = 4
    val maximumPairCandidatesPerEdge = 48

    data class CrossingPair(
        val first: SwimlaneEdge,
        val second: SwimlaneEdge,
        val count: Int,
    )

    data class CrossingSnapshot(
        val count: Int,
        val pairs: List<CrossingPair>,
        val edgeSet: Set<SwimlaneEdge>,
        val edges: List<SwimlaneEdge>,
    )

    data class PairCandidate(
        val path: List<SwimlanePoint>,
        val segments: List<OrthogonalSegment>,
        val sharedTrackConflicts: Set<SwimlaneEdge>,
        val totalBends: Int,
        val length: Double,
    )

    data class PairOption(
        val edge: SwimlaneEdge,
        val candidates: List<PairCandidate>,
    )

    data class PairReplacementScore(
        val replacements: Map<SwimlaneEdge, List<SwimlanePoint>>,
        val crossings: Int,
        val bends: Int,
        val length: Double,
    )

    data class PairScoringContext(
        val current: CrossingSnapshot,
        val currentBends: Int,
        val currentLength: Double,
        val baseBendsByEdge: Map<SwimlaneEdge, Int>,
        val baseLengthByEdge: Map<SwimlaneEdge, Double>,
        val baseSegments: Map<SwimlaneEdge, List<OrthogonalSegment>>,
    )

    val realNodes = nodeByIdMap.values.mapNotNull { node ->
        if (node.isGroup || node.isEdgeLabel) return@mapNotNull null
        val rect = rectOfNodeBounds(node) ?: return@mapNotNull null
        NodeBoundsInfo(
            id = node.id,
            centerX = node.x ?: 0.0,
            centerY = node.y ?: 0.0,
            rect = rect,
        )
    }
    if (realNodes.isEmpty()) return

    val nodeInfoById = realNodes.associateBy(NodeBoundsInfo::id)
    val realNodeRects = realNodes.map { node -> RectEntry(node.id, node.rect) }
    val sides = RectSide.entries
    val outsideTracks = mapOf(
        RectSide.Top to realNodes.minOf { node -> node.rect.top } - anchor,
        RectSide.Bottom to realNodes.maxOf { node -> node.rect.bottom } + anchor,
        RectSide.Left to realNodes.minOf { node -> node.rect.left } - anchor,
        RectSide.Right to realNodes.maxOf { node -> node.rect.right } + anchor,
    )
    val visibleEdges = edges.filterNot(SwimlaneEdge::isLayoutOnly)
    val edgeIndex = visibleEdges.withIndex().associate { indexed ->
        indexed.value to indexed.index
    }

    fun outwardTracksForSide(side: RectSide): List<Double> {
        val outward =
            if (side == RectSide.Left || side == RectSide.Top) -1.0 else 1.0
        return (0..extraChannelCount).map { channel ->
            outsideTracks.getValue(side) + outward * anchor * channel
        }
    }

    fun replacementPointsFor(
        edge: SwimlaneEdge,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): List<SwimlanePoint> = dedupeConsecutivePoints(
        replacements[edge] ?: edge.points.orEmpty(),
    )

    fun crossingCountBetweenSegments(
        firstSegments: List<OrthogonalSegment>,
        secondSegments: List<OrthogonalSegment>,
    ): Int {
        var count = 0
        for (first in firstSegments) {
            for (second in secondSegments) {
                if (
                    orthogonalSegmentsStrictlyCross(
                        first.a,
                        first.b,
                        second.a,
                        second.b,
                        MaterializedEpsilon,
                    )
                ) {
                    count++
                }
            }
        }
        return count
    }

    fun crossingCountBetweenPaths(
        first: List<SwimlanePoint>,
        second: List<SwimlanePoint>,
    ): Int = crossingCountBetweenSegments(
        materializedSegmentsFor(first),
        materializedSegmentsFor(second),
    )

    fun crossingSnapshot(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): CrossingSnapshot {
        var count = 0
        val pairs = mutableListOf<CrossingPair>()
        val crossingEdges = linkedSetOf<SwimlaneEdge>()
        for (firstIndex in visibleEdges.indices) {
            val first = visibleEdges[firstIndex]
            val firstPoints = replacementPointsFor(first, replacements)
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val second = visibleEdges[secondIndex]
                val pairCount = crossingCountBetweenPaths(
                    firstPoints,
                    replacementPointsFor(second, replacements),
                )
                if (pairCount > 0) {
                    count += pairCount
                    pairs += CrossingPair(first, second, pairCount)
                    crossingEdges += first
                    crossingEdges += second
                }
            }
        }
        val orderedEdges = crossingEdges.sortedBy { edge -> edgeIndex[edge] ?: 0 }
        return CrossingSnapshot(count, pairs, crossingEdges, orderedEdges)
    }

    fun crossingCountWithReplacements(
        current: CrossingSnapshot,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>>,
    ): Int {
        val changed = replacements.keys
        if (changed.isEmpty()) return current.count
        val currentAffected = current.pairs
            .filter { pair -> pair.first in changed || pair.second in changed }
            .sumOf(CrossingPair::count)
        var replacementAffected = 0
        for (firstIndex in visibleEdges.indices) {
            val first = visibleEdges[firstIndex]
            val firstChanged = first in changed
            val firstPoints = replacementPointsFor(first, replacements)
            for (secondIndex in firstIndex + 1 until visibleEdges.size) {
                val second = visibleEdges[secondIndex]
                if (!firstChanged && second !in changed) continue
                replacementAffected += crossingCountBetweenPaths(
                    firstPoints,
                    replacementPointsFor(second, replacements),
                )
            }
        }
        return current.count - currentAffected + replacementAffected
    }

    fun crossingComponents(snapshot: CrossingSnapshot): List<List<SwimlaneEdge>> {
        val neighbors = linkedMapOf<SwimlaneEdge, MutableSet<SwimlaneEdge>>()
        for (pair in snapshot.pairs) {
            neighbors.getOrPut(pair.first, ::linkedSetOf) += pair.second
            neighbors.getOrPut(pair.second, ::linkedSetOf) += pair.first
        }
        val components = mutableListOf<List<SwimlaneEdge>>()
        val seen = mutableSetOf<SwimlaneEdge>()
        for (edge in snapshot.edges) {
            if (edge in seen) continue
            val queue = mutableListOf(edge)
            val component = mutableListOf<SwimlaneEdge>()
            seen += edge
            while (queue.isNotEmpty()) {
                val current = queue.removeAt(queue.lastIndex)
                component += current
                for (next in neighbors[current].orEmpty()) {
                    if (next !in seen) {
                        seen += next
                        queue += next
                    }
                }
            }
            component.sortBy { item -> edgeIndex[item] ?: 0 }
            if (component.size > 1) components += component
        }
        return components
    }

    fun endpointIdsFor(edge: SwimlaneEdge): List<String> =
        listOfNotNull(edge.start, edge.end)

    fun pairSearchGroups(snapshot: CrossingSnapshot): List<List<SwimlaneEdge>> =
        crossingComponents(snapshot).map { component ->
            val componentSet = component.toSet()
            val componentEndpointIds = component
                .flatMap(::endpointIdsFor)
                .toSet()
            buildList {
                addAll(component)
                addAll(
                    visibleEdges.filter { edge ->
                        edge !in componentSet &&
                            endpointIdsFor(edge).any(componentEndpointIds::contains)
                    },
                )
            }.sortedBy { edge -> edgeIndex[edge] ?: 0 }
        }

    fun crossingCountWithSingleReplacement(
        current: CrossingSnapshot,
        edge: SwimlaneEdge,
        replacement: List<SwimlanePoint>,
    ): Int = crossingCountWithReplacements(current, mapOf(edge to replacement))

    fun currentCrossingsByEdge(
        current: CrossingSnapshot,
    ): Map<SwimlaneEdge, Int> {
        val result = linkedMapOf<SwimlaneEdge, Int>()
        for (pair in current.pairs) {
            result[pair.first] = (result[pair.first] ?: 0) + pair.count
            result[pair.second] = (result[pair.second] ?: 0) + pair.count
        }
        return result
    }

    fun pathLength(points: List<SwimlanePoint>): Double =
        points.drop(1).mapIndexed { index, point ->
            val previous = points[index]
            abs(point.x - previous.x) + abs(point.y - previous.y)
        }.sum()

    fun totalBends(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Int = visibleEdges.sumOf { edge ->
        countOrthogonalBends(replacementPointsFor(edge, replacements))
    }

    fun totalLength(
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Double = visibleEdges.sumOf { edge ->
        pathLength(replacementPointsFor(edge, replacements))
    }

    fun pathHasSegmentConflict(
        edge: SwimlaneEdge,
        path: List<SwimlanePoint>,
        replacements: Map<SwimlaneEdge, List<SwimlanePoint>> = emptyMap(),
    ): Boolean {
        val pathSegments = materializedSegmentsFor(path)
        for (other in visibleEdges) {
            if (other === edge) continue
            for (candidateSegment in pathSegments) {
                for (
                    otherSegment in materializedSegmentsFor(
                        replacementPointsFor(other, replacements),
                    )
                ) {
                    if (
                        sameAxisSegmentOverlapLength(
                            candidateSegment,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun pathHitsNode(
        edge: SwimlaneEdge,
        path: List<SwimlanePoint>,
    ): Boolean {
        val endpointIds = endpointIdsFor(edge)
        return materializedSegmentsFor(path).any { segment ->
            segmentHitsAnyRect(
                segment.a,
                segment.b,
                realNodeRects,
                endpointIds,
                -2.0,
            )
        }
    }

    fun pushOrthogonalCandidate(
        candidates: MutableList<List<SwimlanePoint>>,
        points: List<SwimlanePoint>,
    ) {
        val candidate = simplifyPolyline(dedupeConsecutivePoints(points))
        if (materializedSegmentsFor(candidate).size == candidate.size - 1) {
            candidates += candidate
        }
    }

    fun sideIsHorizontal(side: RectSide): Boolean =
        side == RectSide.Left || side == RectSide.Right

    fun localTrackForSameSide(
        source: SwimlanePoint,
        side: RectSide,
        destination: SwimlanePoint,
    ): Double = when (side) {
        RectSide.Left -> min(source.x, destination.x) - anchor
        RectSide.Right -> max(source.x, destination.x) + anchor
        RectSide.Top -> min(source.y, destination.y) - anchor
        RectSide.Bottom -> max(source.y, destination.y) + anchor
    }

    fun addSameSideCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
    ) {
        val outward =
            if (sourceSide == RectSide.Left || sourceSide == RectSide.Top) -1.0 else 1.0
        val trackSeeds = listOf(
            localTrackForSameSide(source, sourceSide, destination),
            outsideTracks.getValue(sourceSide),
        )
        for (seed in trackSeeds) {
            for (channel in 0..extraChannelCount) {
                pushOrthogonalCandidate(
                    candidates,
                    buildSameSideTrackPath(
                        source,
                        sourceSide,
                        destination,
                        seed + outward * anchor * channel,
                    ),
                )
            }
        }
    }

    fun addHorizontalToVerticalCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ) {
        for (xTrack in outwardTracksForSide(sourceSide)) {
            for (yTrack in outwardTracksForSide(destinationSide)) {
                pushOrthogonalCandidate(
                    candidates,
                    listOf(
                        source,
                        SwimlanePoint(xTrack, source.y),
                        SwimlanePoint(xTrack, yTrack),
                        SwimlanePoint(destination.x, yTrack),
                        destination,
                    ),
                )
            }
        }
    }

    fun addVerticalToHorizontalCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ) {
        for (yTrack in outwardTracksForSide(sourceSide)) {
            for (xTrack in outwardTracksForSide(destinationSide)) {
                pushOrthogonalCandidate(
                    candidates,
                    listOf(
                        source,
                        SwimlanePoint(source.x, yTrack),
                        SwimlanePoint(xTrack, yTrack),
                        SwimlanePoint(xTrack, destination.y),
                        destination,
                    ),
                )
            }
        }
    }

    fun addHorizontalPairCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ) {
        val yTracks = outwardTracksForSide(RectSide.Top) +
            outwardTracksForSide(RectSide.Bottom)
        for (sourceTrack in outwardTracksForSide(sourceSide)) {
            for (destinationTrack in outwardTracksForSide(destinationSide)) {
                for (yTrack in yTracks) {
                    pushOrthogonalCandidate(
                        candidates,
                        listOf(
                            source,
                            SwimlanePoint(sourceTrack, source.y),
                            SwimlanePoint(sourceTrack, yTrack),
                            SwimlanePoint(destinationTrack, yTrack),
                            SwimlanePoint(destinationTrack, destination.y),
                            destination,
                        ),
                    )
                }
            }
        }
    }

    fun addVerticalPairCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ) {
        val xTracks = outwardTracksForSide(RectSide.Left) +
            outwardTracksForSide(RectSide.Right)
        for (sourceTrack in outwardTracksForSide(sourceSide)) {
            for (destinationTrack in outwardTracksForSide(destinationSide)) {
                for (xTrack in xTracks) {
                    pushOrthogonalCandidate(
                        candidates,
                        listOf(
                            source,
                            SwimlanePoint(source.x, sourceTrack),
                            SwimlanePoint(xTrack, sourceTrack),
                            SwimlanePoint(xTrack, destinationTrack),
                            SwimlanePoint(destination.x, destinationTrack),
                            destination,
                        ),
                    )
                }
            }
        }
    }

    fun dedupeCandidatePaths(
        candidates: List<List<SwimlanePoint>>,
    ): List<List<SwimlanePoint>> {
        val seen = mutableSetOf<String>()
        return candidates.mapNotNull { raw ->
            val candidate = dedupeConsecutivePoints(raw)
            val key = materializedPathKey(candidate)
            if (candidate.size < 2 || !seen.add(key)) null else candidate
        }
    }

    fun buildCandidatesForSides(
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ): List<List<SwimlanePoint>> {
        val candidates = mutableListOf<List<SwimlanePoint>>()
        buildOrthogonalPortPath(
            source,
            sourceSide,
            destination,
            destinationSide,
            anchor,
            MaterializedEpsilon,
        )?.let { base -> pushOrthogonalCandidate(candidates, base) }
        if (sourceSide == destinationSide) {
            addSameSideCandidates(candidates, source, sourceSide, destination)
        }
        val sourceHorizontal = sideIsHorizontal(sourceSide)
        val destinationHorizontal = sideIsHorizontal(destinationSide)
        when {
            sourceHorizontal && !destinationHorizontal ->
                addHorizontalToVerticalCandidates(
                    candidates,
                    source,
                    sourceSide,
                    destination,
                    destinationSide,
                )
            !sourceHorizontal && destinationHorizontal ->
                addVerticalToHorizontalCandidates(
                    candidates,
                    source,
                    sourceSide,
                    destination,
                    destinationSide,
                )
            sourceHorizontal ->
                addHorizontalPairCandidates(
                    candidates,
                    source,
                    sourceSide,
                    destination,
                    destinationSide,
                )
            else ->
                addVerticalPairCandidates(
                    candidates,
                    source,
                    sourceSide,
                    destination,
                    destinationSide,
                )
        }
        return dedupeCandidatePaths(candidates)
    }

    fun addVerticalDepartureOuterTrackCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        first: SwimlanePoint,
        departure: SwimlanePoint,
        destinationNode: NodeBoundsInfo,
    ) {
        val externalXTracks = outwardTracksForSide(RectSide.Left) +
            outwardTracksForSide(RectSide.Right)
        val externalYTracks = outwardTracksForSide(RectSide.Top) +
            outwardTracksForSide(RectSide.Bottom)
        for (side in sides) {
            val destination = portForRectSide(destinationNode, side)
            val targetYTracks =
                if (side == RectSide.Top || side == RectSide.Bottom) {
                    outwardTracksForSide(side)
                } else {
                    externalYTracks
                }
            for (track in externalXTracks) {
                pushOrthogonalCandidate(
                    candidates,
                    listOf(
                        first,
                        departure,
                        SwimlanePoint(track, departure.y),
                        SwimlanePoint(track, destination.y),
                        destination,
                    ),
                )
                for (targetTrack in targetYTracks) {
                    pushOrthogonalCandidate(
                        candidates,
                        listOf(
                            first,
                            departure,
                            SwimlanePoint(track, departure.y),
                            SwimlanePoint(track, targetTrack),
                            SwimlanePoint(destination.x, targetTrack),
                            destination,
                        ),
                    )
                }
            }
        }
    }

    fun addHorizontalDepartureOuterTrackCandidates(
        candidates: MutableList<List<SwimlanePoint>>,
        first: SwimlanePoint,
        departure: SwimlanePoint,
        destinationNode: NodeBoundsInfo,
    ) {
        val externalXTracks = outwardTracksForSide(RectSide.Left) +
            outwardTracksForSide(RectSide.Right)
        val externalYTracks = outwardTracksForSide(RectSide.Top) +
            outwardTracksForSide(RectSide.Bottom)
        for (side in sides) {
            val destination = portForRectSide(destinationNode, side)
            val targetXTracks =
                if (side == RectSide.Left || side == RectSide.Right) {
                    outwardTracksForSide(side)
                } else {
                    externalXTracks
                }
            for (track in externalYTracks) {
                pushOrthogonalCandidate(
                    candidates,
                    listOf(
                        first,
                        departure,
                        SwimlanePoint(departure.x, track),
                        SwimlanePoint(destination.x, track),
                        destination,
                    ),
                )
                for (targetTrack in targetXTracks) {
                    pushOrthogonalCandidate(
                        candidates,
                        listOf(
                            first,
                            departure,
                            SwimlanePoint(departure.x, track),
                            SwimlanePoint(targetTrack, track),
                            SwimlanePoint(targetTrack, destination.y),
                            destination,
                        ),
                    )
                }
            }
        }
    }

    fun terminalPreservingOuterTrackCandidates(
        edge: SwimlaneEdge,
    ): List<List<SwimlanePoint>> {
        val sourceId = edge.start ?: return emptyList()
        val destinationNode =
            edge.end?.let(nodeInfoById::get) ?: return emptyList()
        if (sourceId.isEmpty()) return emptyList()
        val points = dedupeConsecutivePoints(edge.points ?: emptyList())
        if (points.size < 4) return emptyList()
        val first = points[0]
        val departure = points[1]
        val candidates = mutableListOf<List<SwimlanePoint>>()
        if (isVerticalSegment(first, departure, MaterializedEpsilon)) {
            addVerticalDepartureOuterTrackCandidates(
                candidates,
                first,
                departure,
                destinationNode,
            )
        } else if (
            isHorizontalSegment(first, departure, MaterializedEpsilon)
        ) {
            addHorizontalDepartureOuterTrackCandidates(
                candidates,
                first,
                departure,
                destinationNode,
            )
        }
        return candidates
    }

    fun candidatePathsFor(edge: SwimlaneEdge): List<List<SwimlanePoint>> {
        val sourceNode = edge.start?.let(nodeInfoById::get) ?: return emptyList()
        val destinationNode = edge.end?.let(nodeInfoById::get) ?: return emptyList()
        val candidates = mutableListOf<List<SwimlanePoint>>()
        for (sourceSide in sides) {
            val sourcePort = portForRectSide(sourceNode, sourceSide)
            for (destinationSide in sides) {
                candidates += buildCandidatesForSides(
                    sourcePort,
                    sourceSide,
                    portForRectSide(destinationNode, destinationSide),
                    destinationSide,
                )
            }
        }
        candidates += terminalPreservingOuterTrackCandidates(edge)
        return candidates
    }

    fun currentSegmentsByEdge(): Map<SwimlaneEdge, List<OrthogonalSegment>> =
        visibleEdges.associateWith { edge ->
            materializedSegmentsFor(replacementPointsFor(edge))
        }

    fun sharedTrackConflictsFor(
        edge: SwimlaneEdge,
        candidateSegments: List<OrthogonalSegment>,
        baseSegments: Map<SwimlaneEdge, List<OrthogonalSegment>>,
    ): Set<SwimlaneEdge> {
        val conflicts = linkedSetOf<SwimlaneEdge>()
        for (other in visibleEdges) {
            if (other === edge) continue
            val otherSegments =
                baseSegments[other] ?: materializedSegmentsFor(replacementPointsFor(other))
            if (
                candidateSegments.any { candidate ->
                    otherSegments.any { otherSegment ->
                        sameAxisSegmentOverlapLength(
                            candidate,
                            otherSegment,
                            0.5,
                        ) >= MaterializedMinimumShared
                    }
                }
            ) {
                conflicts += other
            }
        }
        return conflicts
    }

    fun pairCandidatesFor(
        edge: SwimlaneEdge,
        current: CrossingSnapshot,
        baseSegments: Map<SwimlaneEdge, List<OrthogonalSegment>>,
        crossingCountByEdge: Map<SwimlaneEdge, Int>,
    ): List<PairCandidate> {
        data class RankedCandidate(
            val path: List<SwimlanePoint>,
            val segments: List<OrthogonalSegment>,
            val crossings: Int,
            val bends: Int,
            val length: Double,
        )

        val seen = mutableSetOf<String>()
        val candidates = candidatePathsFor(edge).mapNotNull { raw ->
            val candidate = simplifyPolyline(dedupeConsecutivePoints(raw))
            if (pathHitsNode(edge, candidate)) return@mapNotNull null
            val key = materializedPathKey(candidate)
            if (candidate.size < 2 || !seen.add(key)) return@mapNotNull null
            val candidateSegments = materializedSegmentsFor(candidate)
            var replacementAffected = 0
            for (other in visibleEdges) {
                if (other === edge) continue
                replacementAffected += crossingCountBetweenSegments(
                    candidateSegments,
                    baseSegments[other]
                        ?: materializedSegmentsFor(replacementPointsFor(other)),
                )
            }
            RankedCandidate(
                path = candidate,
                segments = candidateSegments,
                crossings = current.count -
                    (crossingCountByEdge[edge] ?: 0) +
                    replacementAffected,
                bends = countOrthogonalBends(candidate, MaterializedEpsilon),
                length = pathLength(candidate),
            )
        }.filter { candidate -> candidate.crossings <= current.count }
            .sortedWith(
                compareBy<RankedCandidate> { candidate -> candidate.crossings }
                    .thenBy { candidate -> candidate.bends }
                    .thenBy { candidate -> candidate.length },
            )
            .take(maximumPairCandidatesPerEdge)

        return candidates.map { candidate ->
            PairCandidate(
                path = candidate.path,
                segments = candidate.segments,
                sharedTrackConflicts = sharedTrackConflictsFor(
                    edge,
                    candidate.segments,
                    baseSegments,
                ),
                totalBends = countOrthogonalBends(candidate.path),
                length = candidate.length,
            )
        }
    }

    fun pairCrossingCount(
        current: CrossingSnapshot,
        firstEdge: SwimlaneEdge,
        firstCandidate: PairCandidate,
        secondEdge: SwimlaneEdge,
        secondCandidate: PairCandidate,
        baseSegments: Map<SwimlaneEdge, List<OrthogonalSegment>>,
    ): Int {
        val currentAffected = current.pairs
            .filter { pair ->
                pair.first === firstEdge ||
                    pair.second === firstEdge ||
                    pair.first === secondEdge ||
                    pair.second === secondEdge
            }
            .sumOf(CrossingPair::count)
        var replacementAffected = crossingCountBetweenSegments(
            firstCandidate.segments,
            secondCandidate.segments,
        )
        for (other in visibleEdges) {
            if (other === firstEdge || other === secondEdge) continue
            val otherSegments =
                baseSegments[other] ?: materializedSegmentsFor(replacementPointsFor(other))
            replacementAffected +=
                crossingCountBetweenSegments(firstCandidate.segments, otherSegments) +
                crossingCountBetweenSegments(secondCandidate.segments, otherSegments)
        }
        return current.count - currentAffected + replacementAffected
    }

    fun conflictsOnlyWith(
        candidate: PairCandidate,
        edge: SwimlaneEdge,
    ): Boolean = candidate.sharedTrackConflicts.all { conflict -> conflict === edge }

    fun candidatesShareTrack(
        first: PairCandidate,
        second: PairCandidate,
    ): Boolean = first.segments.any { firstSegment ->
        second.segments.any { secondSegment ->
            sameAxisSegmentOverlapLength(
                firstSegment,
                secondSegment,
                0.5,
            ) >= MaterializedMinimumShared
        }
    }

    fun pairCandidatesAreCompatible(
        first: PairOption,
        firstCandidate: PairCandidate,
        second: PairOption,
        secondCandidate: PairCandidate,
    ): Boolean =
        conflictsOnlyWith(firstCandidate, second.edge) &&
            conflictsOnlyWith(secondCandidate, first.edge) &&
            !candidatesShareTrack(firstCandidate, secondCandidate)

    fun scorePairReplacement(
        context: PairScoringContext,
        first: PairOption,
        firstCandidate: PairCandidate,
        second: PairOption,
        secondCandidate: PairCandidate,
    ): PairReplacementScore? {
        val crossings = pairCrossingCount(
            context.current,
            first.edge,
            firstCandidate,
            second.edge,
            secondCandidate,
            context.baseSegments,
        )
        if (crossings >= context.current.count) return null
        return PairReplacementScore(
            replacements = linkedMapOf(
                first.edge to firstCandidate.path,
                second.edge to secondCandidate.path,
            ),
            crossings = crossings,
            bends = context.currentBends -
                (context.baseBendsByEdge[first.edge] ?: 0) -
                (context.baseBendsByEdge[second.edge] ?: 0) +
                firstCandidate.totalBends +
                secondCandidate.totalBends,
            length = context.currentLength -
                (context.baseLengthByEdge[first.edge] ?: 0.0) -
                (context.baseLengthByEdge[second.edge] ?: 0.0) +
                firstCandidate.length +
                secondCandidate.length,
        )
    }

    fun pairScoreIsBetter(
        candidate: PairReplacementScore,
        best: PairReplacementScore,
    ): Boolean = candidate.crossings < best.crossings ||
        (
            candidate.crossings == best.crossings &&
                (
                    candidate.bends < best.bends ||
                        (
                            candidate.bends == best.bends &&
                                candidate.length < best.length
                            )
                    )
            )

    fun bestScoreForOptionPair(
        context: PairScoringContext,
        first: PairOption,
        second: PairOption,
        best: PairReplacementScore,
    ): PairReplacementScore {
        var pairBest = best
        for (firstCandidate in first.candidates) {
            for (secondCandidate in second.candidates) {
                if (
                    !pairCandidatesAreCompatible(
                        first,
                        firstCandidate,
                        second,
                        secondCandidate,
                    )
                ) {
                    continue
                }
                val score = scorePairReplacement(
                    context,
                    first,
                    firstCandidate,
                    second,
                    secondCandidate,
                )
                if (score != null && pairScoreIsBetter(score, pairBest)) {
                    pairBest = score
                }
            }
        }
        return pairBest
    }

    fun bestPairedReplacement(
        current: CrossingSnapshot,
    ): Map<SwimlaneEdge, List<SwimlanePoint>>? {
        val currentBends = totalBends()
        val currentLength = totalLength()
        val baseSegments = currentSegmentsByEdge()
        val crossingCountByEdge = currentCrossingsByEdge(current)
        val baseBendsByEdge = visibleEdges.associateWith { edge ->
            countOrthogonalBends(replacementPointsFor(edge))
        }
        val baseLengthByEdge = visibleEdges.associateWith { edge ->
            pathLength(replacementPointsFor(edge))
        }
        val optionsByEdge = linkedMapOf<SwimlaneEdge, PairOption>()
        val groups = pairSearchGroups(current)
        for (group in groups) {
            for (edge in group) {
                if (edge in optionsByEdge) continue
                val candidates = pairCandidatesFor(
                    edge,
                    current,
                    baseSegments,
                    crossingCountByEdge,
                )
                if (candidates.isNotEmpty()) {
                    optionsByEdge[edge] = PairOption(edge, candidates)
                }
            }
        }

        var best = PairReplacementScore(
            replacements = emptyMap(),
            crossings = current.count,
            bends = currentBends,
            length = currentLength,
        )
        val scoringContext = PairScoringContext(
            current,
            currentBends,
            currentLength,
            baseBendsByEdge,
            baseLengthByEdge,
            baseSegments,
        )
        for (group in groups) {
            val crossingEdgeSet =
                group.filter(current.edgeSet::contains).toSet()
            val options = group.mapNotNull(optionsByEdge::get)
            for (firstIndex in options.indices) {
                val first = options[firstIndex]
                for (secondIndex in firstIndex + 1 until options.size) {
                    val second = options[secondIndex]
                    if (
                        first.edge !in crossingEdgeSet &&
                        second.edge !in crossingEdgeSet
                    ) {
                        continue
                    }
                    best = bestScoreForOptionPair(
                        scoringContext,
                        first,
                        second,
                        best,
                    )
                }
            }
        }
        return best.replacements.ifEmpty { null }
    }

    repeat(maximumIterations) {
        val current = crossingSnapshot()
        if (current.count == 0) return
        var bestEdge: SwimlaneEdge? = null
        var bestPath: List<SwimlanePoint>? = null
        var bestCrossings = current.count
        var bestBends = Int.MAX_VALUE

        for (edge in current.edges) {
            val currentEdgeBends =
                countOrthogonalBends(
                    replacementPointsFor(edge),
                    MaterializedEpsilon,
                )
            for (candidate in candidatePathsFor(edge)) {
                val candidateHitsNode = pathHitsNode(edge, candidate)
                val candidateHasSegmentConflict =
                    !candidateHitsNode && pathHasSegmentConflict(edge, candidate)
                val candidateCrossings =
                    crossingCountWithSingleReplacement(current, edge, candidate)
                val candidateBends =
                    countOrthogonalBends(candidate, MaterializedEpsilon)
                if (candidateHitsNode || candidateHasSegmentConflict) continue
                val improvesCurrentEdge =
                    candidateCrossings < current.count ||
                        (
                            candidateCrossings == current.count &&
                                candidateBends < currentEdgeBends
                            )
                if (!improvesCurrentEdge) continue
                if (
                    candidateCrossings > bestCrossings ||
                    (
                        candidateCrossings == bestCrossings &&
                            candidateBends >= bestBends
                        )
                ) {
                    continue
                }
                bestEdge = edge
                bestPath = candidate
                bestCrossings = candidateCrossings
                bestBends = candidateBends
            }
        }

        if (bestEdge != null && bestPath != null) {
            bestEdge.points = bestPath.toMutableList()
            return@repeat
        }
        val pairedReplacement = bestPairedReplacement(current) ?: return
        for ((edge, points) in pairedReplacement) {
            edge.points = points.toMutableList()
        }
    }
}
