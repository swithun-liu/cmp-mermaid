package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/direction/siblingSharedFaceRouting.ts
 * -> straightenCollinearSiblingDetours.
 */
internal fun straightenCollinearSiblingDetours(
    edges: List<SwimlaneEdge>,
    nodes: List<SwimlaneNode>,
) {
    val epsilon = 1e-6
    val portShift = 4.0
    val labelClearanceBuffer = 3.0
    val (nodeInfoById, realNodeRects) = collectRealNodeBounds(nodes)
    val labelDimensions = nodes
        .asSequence()
        .filter { node -> !node.isGroup && node.isEdgeLabel }
        .associate { node -> node.id to Pair(node.width, node.height) }

    fun pairKey(first: String, second: String): String =
        if (first < second) "$first::$second" else "$second::$first"

    fun labelClearanceFor(
        edge: SwimlaneEdge,
        sourceId: String,
        destinationId: String,
        axis: Char,
    ): Double {
        val targetPair = pairKey(sourceId, destinationId)
        var maximumHalf = 0.0

        fun consider(labelId: String?) {
            val dimension = labelId?.let(labelDimensions::get) ?: return
            val half = if (axis == 'x') dimension.first / 2.0 else dimension.second / 2.0
            if (half > maximumHalf) maximumHalf = half
        }

        consider(edge.labelNodeId)
        for (other in edges) {
            if (other === edge || other.isLayoutOnly) continue
            val otherSource = other.start ?: continue
            val otherDestination = other.end ?: continue
            if (pairKey(otherSource, otherDestination) == targetPair) {
                consider(other.labelNodeId)
            }
        }
        return if (maximumHalf > 0.0) maximumHalf + labelClearanceBuffer else 0.0
    }

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        if (classifyThreeSegmentRoute(edge.points, epsilon) == null) continue
        val pair = getNodePairGeometry(edge, nodeInfoById, epsilon) ?: continue
        if (pair.collinearX == pair.collinearY) continue

        val targetSource: SwimlanePoint
        val targetDestination: SwimlanePoint
        if (pair.collinearX) {
            val destinationBelow =
                pair.destinationInfo.centerY > pair.sourceInfo.centerY
            targetSource = SwimlanePoint(
                x = pair.sourceInfo.centerX,
                y = if (destinationBelow) {
                    pair.sourceInfo.rect.bottom
                } else {
                    pair.sourceInfo.rect.top
                },
            )
            targetDestination = SwimlanePoint(
                x = pair.destinationInfo.centerX,
                y = if (destinationBelow) {
                    pair.destinationInfo.rect.top
                } else {
                    pair.destinationInfo.rect.bottom
                },
            )
        } else {
            val destinationEast =
                pair.destinationInfo.centerX > pair.sourceInfo.centerX
            targetSource = SwimlanePoint(
                x = if (destinationEast) {
                    pair.sourceInfo.rect.right
                } else {
                    pair.sourceInfo.rect.left
                },
                y = pair.sourceInfo.centerY,
            )
            targetDestination = SwimlanePoint(
                x = if (destinationEast) {
                    pair.destinationInfo.rect.left
                } else {
                    pair.destinationInfo.rect.right
                },
                y = pair.destinationInfo.centerY,
            )
        }

        if (
            segmentHitsAnyRect(
                targetSource,
                targetDestination,
                realNodeRects,
                listOf(pair.sourceId, pair.destinationId),
                1.0,
            )
        ) {
            continue
        }

        val shiftAxis = if (pair.collinearX) 'x' else 'y'
        val labelShift = labelClearanceFor(
            edge,
            pair.sourceId,
            pair.destinationId,
            shiftAxis,
        )
        val effectiveShift = maxOf(labelShift, portShift)
        for (delta in listOf(0.0, effectiveShift, -effectiveShift)) {
            val shiftedSource = if (pair.collinearX) {
                targetSource.copy(x = targetSource.x + delta)
            } else {
                targetSource.copy(y = targetSource.y + delta)
            }
            val shiftedDestination = if (pair.collinearX) {
                targetDestination.copy(x = targetDestination.x + delta)
            } else {
                targetDestination.copy(y = targetDestination.y + delta)
            }

            if (pair.collinearX) {
                if (
                    shiftedSource.x <= pair.sourceInfo.rect.left ||
                    shiftedSource.x >= pair.sourceInfo.rect.right ||
                    shiftedDestination.x <= pair.destinationInfo.rect.left ||
                    shiftedDestination.x >= pair.destinationInfo.rect.right
                ) {
                    continue
                }
            } else if (
                shiftedSource.y <= pair.sourceInfo.rect.top ||
                shiftedSource.y >= pair.sourceInfo.rect.bottom ||
                shiftedDestination.y <= pair.destinationInfo.rect.top ||
                shiftedDestination.y >= pair.destinationInfo.rect.bottom
            ) {
                continue
            }

            if (
                segmentHitsAnyRect(
                    shiftedSource,
                    shiftedDestination,
                    realNodeRects,
                    listOf(pair.sourceId, pair.destinationId),
                    1.0,
                )
            ) {
                continue
            }
            if (
                segmentConflictsWithAnyEdge(
                    shiftedSource,
                    shiftedDestination,
                    edges,
                    edge,
                    epsilon,
                )
            ) {
                continue
            }
            edge.points = mutableListOf(shiftedSource, shiftedDestination)
            break
        }
    }
}
