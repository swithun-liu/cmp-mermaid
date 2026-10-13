package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/detourSimplification.ts
 * -> simplifyDetouredEdges.
 */
internal fun simplifyDetouredEdges(
    edges: List<SwimlaneEdge>,
    nodes: List<SwimlaneNode>,
) {
    val epsilon = 1e-3
    val minimumShared = 8.0
    val anchor = 20.0
    val bendThreshold = 4
    val sides = RectSide.entries
    val (nodeInfoById, realNodeRects) = collectRealNodeBounds(nodes)
    val outsideTracks = mapOf(
        RectSide.Top to (
            realNodeRects.minOfOrNull { entry -> entry.rect.top } ?: Double.POSITIVE_INFINITY
            ) - anchor,
        RectSide.Bottom to (
            realNodeRects.maxOfOrNull { entry -> entry.rect.bottom } ?: Double.NEGATIVE_INFINITY
            ) + anchor,
        RectSide.Left to (
            realNodeRects.minOfOrNull { entry -> entry.rect.left } ?: Double.POSITIVE_INFINITY
            ) - anchor,
        RectSide.Right to (
            realNodeRects.maxOfOrNull { entry -> entry.rect.right } ?: Double.NEGATIVE_INFINITY
            ) + anchor,
    )

    fun buildOrthogonalPathCandidates(
        source: SwimlanePoint,
        sourceSide: RectSide,
        destination: SwimlanePoint,
        destinationSide: RectSide,
    ): List<List<SwimlanePoint>> = buildList {
        buildOrthogonalPortPath(
            source,
            sourceSide,
            destination,
            destinationSide,
            anchor,
            epsilon,
        )?.let(::add)
        if (sourceSide == destinationSide) {
            add(
                buildSameSideTrackPath(
                    source,
                    sourceSide,
                    destination,
                    outsideTracks.getValue(sourceSide),
                ),
            )
        }
    }

    fun pathHitsNode(
        points: List<SwimlanePoint>,
        excludeIds: Collection<String>,
    ): Boolean {
        for (index in 0 until points.lastIndex) {
            if (
                segmentHitsAnyRect(
                    points[index],
                    points[index + 1],
                    realNodeRects,
                    excludeIds,
                    1.0,
                )
            ) {
                return true
            }
        }
        return false
    }

    fun pathConflictCount(
        path: List<SwimlanePoint>,
        currentEdge: SwimlaneEdge,
        includeIncidentEdges: Boolean = false,
    ): Int {
        var conflicts = 0
        val pathSegments = orthogonalSegmentsForPoints(path, epsilon)
        val currentStart = currentEdge.start
        val currentEnd = currentEdge.end
        for (other in edges) {
            if (other === currentEdge || other.isLayoutOnly) continue
            if (
                !includeIncidentEdges &&
                currentStart != null &&
                currentEnd != null &&
                (
                    other.start == currentStart ||
                        other.start == currentEnd ||
                        other.end == currentStart ||
                        other.end == currentEnd
                    )
            ) {
                continue
            }
            val otherPoints = other.points ?: continue
            if (otherPoints.size < 2) continue
            val otherSegments = orthogonalSegmentsForPoints(otherPoints, epsilon)
            for (pathSegment in pathSegments) {
                for (otherSegment in otherSegments) {
                    if (
                        orthogonalSegmentsCross(
                            pathSegment.a,
                            pathSegment.b,
                            otherSegment.a,
                            otherSegment.b,
                            epsilon,
                            epsilon,
                        )
                    ) {
                        conflicts++
                        continue
                    }
                    if (
                        sameAxisSegmentOverlapLength(
                            pathSegment,
                            otherSegment,
                            epsilon,
                        ) >= minimumShared
                    ) {
                        conflicts++
                    }
                }
            }
        }
        return conflicts
    }

    fun nearestSideOfRect(
        point: SwimlanePoint,
        info: NodeBoundsInfo,
    ): RectSide {
        val distances = listOf(
            RectSide.Top to abs(point.y - info.rect.top),
            RectSide.Bottom to abs(point.y - info.rect.bottom),
            RectSide.Left to abs(point.x - info.rect.left),
            RectSide.Right to abs(point.x - info.rect.right),
        )
        return distances.minBy { pair -> pair.second }.first
    }

    data class FaceClaim(
        val side: RectSide,
        val edgeId: String,
    )

    val faceClaims = linkedMapOf<String, MutableList<FaceClaim>>()
    fun addFaceClaim(
        nodeId: String,
        side: RectSide,
        edgeId: String,
    ) {
        faceClaims.getOrPut(nodeId, ::mutableListOf) += FaceClaim(side, edgeId)
    }

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = edge.points ?: continue
        if (points.isEmpty()) continue
        val sourceId = edge.start
        if (sourceId != null) {
            val info = nodeInfoById[sourceId]
            if (info != null) addFaceClaim(sourceId, nearestSideOfRect(points.first(), info), edge.id)
        }
        val destinationId = edge.end
        if (destinationId != null) {
            val info = nodeInfoById[destinationId]
            if (info != null) {
                addFaceClaim(
                    destinationId,
                    nearestSideOfRect(points.last(), info),
                    edge.id,
                )
            }
        }
    }

    fun faceIsClaimed(
        nodeId: String,
        side: RectSide,
        ignoreEdgeId: String,
    ): Boolean = faceClaims[nodeId]
        ?.any { claim -> claim.edgeId != ignoreEdgeId && claim.side == side }
        ?: false

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = edge.points ?: continue
        if (points.size < 2) continue
        val currentBends = countOrthogonalBends(points, epsilon)
        if (currentBends < bendThreshold) continue
        val sourceId = edge.start ?: continue
        val destinationId = edge.end ?: continue
        val sourceInfo = nodeInfoById[sourceId] ?: continue
        val destinationInfo = nodeInfoById[destinationId] ?: continue
        val currentCrossingConflicts =
            pathConflictCount(points, edge, includeIncidentEdges = true)
        val currentNonIncidentConflicts = pathConflictCount(points, edge)

        var bestPath: List<SwimlanePoint>? = null
        var bestCrossingConflicts = currentCrossingConflicts
        var bestBends = currentBends
        for (sourceSide in sides) {
            if (faceIsClaimed(sourceId, sourceSide, edge.id)) continue
            val sourcePort = portForRectSide(sourceInfo, sourceSide)
            for (destinationSide in sides) {
                if (faceIsClaimed(destinationId, destinationSide, edge.id)) continue
                val destinationPort = portForRectSide(destinationInfo, destinationSide)
                val candidates = buildOrthogonalPathCandidates(
                    sourcePort,
                    sourceSide,
                    destinationPort,
                    destinationSide,
                )
                for (path in candidates) {
                    if (pathHitsNode(path, listOf(sourceId, destinationId))) continue
                    val pathBends = countOrthogonalBends(path, epsilon)
                    if (currentCrossingConflicts > 0) {
                        val pathCrossingConflicts =
                            pathConflictCount(path, edge, includeIncidentEdges = true)
                        if (
                            pathCrossingConflicts > bestCrossingConflicts ||
                            (
                                pathCrossingConflicts == bestCrossingConflicts &&
                                    pathBends >= bestBends
                                )
                        ) {
                            continue
                        }
                        bestCrossingConflicts = pathCrossingConflicts
                        bestBends = pathBends
                        bestPath = path
                        continue
                    }
                    if (pathConflictCount(path, edge) > currentNonIncidentConflicts) continue
                    if (pathBends < bestBends) {
                        bestBends = pathBends
                        bestPath = path
                    }
                }
            }
        }

        if (bestPath != null) {
            edge.points = bestPath.toMutableList()
            faceClaims[sourceId] = faceClaims[sourceId]
                .orEmpty()
                .filterTo(mutableListOf()) { claim -> claim.edgeId != edge.id }
            faceClaims[destinationId] = faceClaims[destinationId]
                .orEmpty()
                .filterTo(mutableListOf()) { claim -> claim.edgeId != edge.id }
            addFaceClaim(
                sourceId,
                nearestSideOfRect(bestPath.first(), sourceInfo),
                edge.id,
            )
            addFaceClaim(
                destinationId,
                nearestSideOfRect(bestPath.last(), destinationInfo),
                edge.id,
            )
        }
    }
}
