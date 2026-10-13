package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/portSwap.ts -> portSwapToLShape.
 */
internal fun portSwapToLShape(
    edges: List<SwimlaneEdge>,
    nodes: List<SwimlaneNode>,
) {
    val epsilon = 1e-6
    val portShift = 8.0
    val tryDeltas = listOf(0.0, portShift, -portShift, 2.0 * portShift, -2.0 * portShift)
    val (nodeInfoById, realNodeRects) = collectRealNodeBounds(nodes)

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = edge.points ?: continue
        if (points.size < 4) continue
        val route = classifyThreeSegmentRoute(
            dedupeConsecutivePoints(points, epsilon),
            epsilon,
        ) ?: continue
        val nodePair = getNodePairGeometry(edge, nodeInfoById, epsilon) ?: continue
        if (nodePair.collinearX || nodePair.collinearY) continue

        val destinationPort = route.p3
        val sourceRect = nodePair.sourceInfo.rect
        var newPoints: List<SwimlanePoint>? = null
        for (delta in tryDeltas) {
            val newSource: SwimlanePoint
            val corner: SwimlanePoint
            val destination = destinationPort.copy()
            if (route.kind == ThreeSegmentRouteKind.HVH) {
                val destinationBelow =
                    nodePair.destinationInfo.centerY > nodePair.sourceInfo.centerY
                val sourceY = if (destinationBelow) sourceRect.bottom else sourceRect.top
                val sourceX = nodePair.sourceInfo.centerX + delta
                if (
                    sourceX <= sourceRect.left + epsilon ||
                    sourceX >= sourceRect.right - epsilon
                ) {
                    continue
                }
                newSource = SwimlanePoint(sourceX, sourceY)
                corner = SwimlanePoint(sourceX, destination.y)
            } else {
                val destinationEast =
                    nodePair.destinationInfo.centerX > nodePair.sourceInfo.centerX
                val sourceX = if (destinationEast) sourceRect.right else sourceRect.left
                val sourceY = nodePair.sourceInfo.centerY + delta
                if (
                    sourceY <= sourceRect.top + epsilon ||
                    sourceY >= sourceRect.bottom - epsilon
                ) {
                    continue
                }
                newSource = SwimlanePoint(sourceX, sourceY)
                corner = SwimlanePoint(destination.x, sourceY)
            }

            val firstDegenerate = samePoint(newSource, corner, epsilon)
            val secondDegenerate = samePoint(corner, destination, epsilon)
            if (firstDegenerate && secondDegenerate) continue

            if (
                !firstDegenerate &&
                segmentHitsAnyRect(
                    newSource,
                    corner,
                    realNodeRects,
                    listOf(nodePair.sourceId),
                    1.0,
                )
            ) {
                continue
            }
            if (
                !secondDegenerate &&
                segmentHitsAnyRect(
                    corner,
                    destination,
                    realNodeRects,
                    listOf(nodePair.destinationId),
                    1.0,
                )
            ) {
                continue
            }

            val firstConflicts = !firstDegenerate &&
                segmentConflictsWithAnyEdge(
                    newSource,
                    corner,
                    edges,
                    edge,
                    epsilon,
                    skipDegenerateOther = true,
                )
            val secondConflicts = !secondDegenerate &&
                segmentConflictsWithAnyEdge(
                    corner,
                    destination,
                    edges,
                    edge,
                    epsilon,
                    skipDegenerateOther = true,
                )
            if (firstConflicts || secondConflicts) continue

            newPoints = when {
                firstDegenerate -> listOf(corner, destination)
                secondDegenerate -> listOf(newSource, corner)
                else -> listOf(newSource, corner, destination)
            }
            break
        }
        if (newPoints != null) edge.points = newPoints.toMutableList()
    }
}
