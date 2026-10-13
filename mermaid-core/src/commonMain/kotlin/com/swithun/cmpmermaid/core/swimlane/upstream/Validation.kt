package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs

internal enum class SwimlaneValidationIssueType {
    EdgeNodeOverlap,
    EdgeEdgeCrossing,
}

internal data class SwimlaneValidationIssue(
    val type: SwimlaneValidationIssueType,
    val edgeId: String,
    val targetId: String,
    val detail: String,
)

private data class ValidationEdgeSegment(
    val edgeId: String,
    val start: String?,
    val end: String?,
    val first: SwimlanePoint,
    val second: SwimlanePoint,
)

// Mermaid.js 12.0.0: swimlanes/direction/validation.ts -> segmentsIntersect.
private fun segmentsIntersect(
    firstStart: SwimlanePoint,
    firstEnd: SwimlanePoint,
    secondStart: SwimlanePoint,
    secondEnd: SwimlanePoint,
): Boolean {
    val firstDeltaX = firstEnd.x - firstStart.x
    val firstDeltaY = firstEnd.y - firstStart.y
    val secondDeltaX = secondEnd.x - secondStart.x
    val secondDeltaY = secondEnd.y - secondStart.y

    val cross = firstDeltaX * secondDeltaY - firstDeltaY * secondDeltaX
    if (abs(cross) < 1e-10) return false

    val deltaX = secondStart.x - firstStart.x
    val deltaY = secondStart.y - firstStart.y
    val firstParameter = (deltaX * secondDeltaY - deltaY * secondDeltaX) / cross
    val secondParameter = (deltaX * firstDeltaY - deltaY * firstDeltaX) / cross
    val epsilon = 0.01
    return firstParameter > epsilon &&
        firstParameter < 1.0 - epsilon &&
        secondParameter > epsilon &&
        secondParameter < 1.0 - epsilon
}

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/validation.ts -> validateSwimlanesLayout.
 *
 * Mermaid logs these diagnostics. The Kotlin translation returns them to the
 * adapter so validation remains observable without introducing a logger into
 * the pure layout package.
 */
internal fun validateSwimlanesLayout(
    layout: SwimlaneLayoutData,
): List<SwimlaneValidationIssue> {
    val nodes = layout.nodes
    val edges = layout.edges
    if (edges.isEmpty() || nodes.isEmpty()) return emptyList()

    val issues = mutableListOf<SwimlaneValidationIssue>()
    val nodeRects = collectLayoutNodeRects(nodes)
    val epsilon = 1.0
    val edgeSegments = mutableListOf<ValidationEdgeSegment>()

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = edge.points ?: continue
        if (points.size < 2) continue

        for (rect in nodeRects) {
            if (rect.nodeId == edge.start || rect.nodeId == edge.end) continue
            if (edge.labelNodeId != null && rect.nodeId == edge.labelNodeId) continue
            for (index in 0 until points.lastIndex) {
                if (
                    segmentBoundsOverlapRect(
                        points[index],
                        points[index + 1],
                        rect.rect,
                        -epsilon,
                    )
                ) {
                    issues += SwimlaneValidationIssue(
                        type = SwimlaneValidationIssueType.EdgeNodeOverlap,
                        edgeId = edge.id,
                        targetId = rect.nodeId,
                        detail = "segment $index passes through node \"${rect.nodeId}\"",
                    )
                    break
                }
            }
        }

        for (index in 0 until points.lastIndex) {
            edgeSegments += ValidationEdgeSegment(
                edgeId = edge.id,
                start = edge.start,
                end = edge.end,
                first = points[index],
                second = points[index + 1],
            )
        }
    }

    val crossingPairs = mutableSetOf<String>()
    for (firstIndex in edgeSegments.indices) {
        for (secondIndex in firstIndex + 1 until edgeSegments.size) {
            val first = edgeSegments[firstIndex]
            val second = edgeSegments[secondIndex]
            if (first.edgeId == second.edgeId) continue
            if (
                first.start == second.start ||
                first.start == second.end ||
                first.end == second.start ||
                first.end == second.end
            ) {
                continue
            }
            if (
                !segmentsIntersect(
                    first.first,
                    first.second,
                    second.first,
                    second.second,
                )
            ) {
                continue
            }

            val pairKey = if (first.edgeId < second.edgeId) {
                "${first.edgeId}|${second.edgeId}"
            } else {
                "${second.edgeId}|${first.edgeId}"
            }
            if (!crossingPairs.add(pairKey)) continue
            issues += SwimlaneValidationIssue(
                type = SwimlaneValidationIssueType.EdgeEdgeCrossing,
                edgeId = first.edgeId,
                targetId = second.edgeId,
                detail = "edges \"${first.edgeId}\" and \"${second.edgeId}\" cross",
            )
        }
    }
    return issues
}
