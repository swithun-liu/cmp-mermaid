package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/postProcessing.ts -> postProcessSwimlaneLayout.
 */
internal fun postProcessSwimlaneLayout(
    layout: SwimlaneLayoutData,
    direction: SwimlaneDirection = layout.direction,
) {
    val nodes = layout.nodes
    val edges = layout.edges
    val contentNodes = nodes.filterNot(SwimlaneNode::isGroup)

    if (
        (direction == SwimlaneDirection.LR || direction == SwimlaneDirection.RL) &&
        contentNodes.isNotEmpty() &&
        !applyLrDirectionTransform(layout, direction)
    ) {
        return
    }
    if (
        direction == SwimlaneDirection.BT &&
        contentNodes.isNotEmpty() &&
        !applyBtDirectionTransform(layout)
    ) {
        return
    }

    for (edge in edges) {
        if (edge.isLayoutOnly) continue
        val points = edge.points ?: continue
        if (points.size < 2) continue
        edge.points = simplifyPolyline(orthogonalizePolyline(points)).toMutableList()
    }

    simplifyDetouredEdges(edges, nodes)
    straightenCollinearSiblingDetours(edges, nodes)
    portSwapToLShape(edges, nodes)

    val nodeByIdMap = nodes.associateByTo(linkedMapOf(), SwimlaneNode::id)
    anchorLabelsToPolyline(edges, nodeByIdMap)
    clipEdgeEndpointsToNodeBoundaries(edges, nodeByIdMap)
    collapseShortTerminalStub(edges, nodeByIdMap)
    nudgeSharedInteriorSubpaths(edges, nodeByIdMap)
    separateSharedRenderedTerminalLanes(edges, nodeByIdMap)
    collapseRedundantRectangularDoglegs(edges, nodeByIdMap)
    liftObstacleHuggingSameSideRails(edges, nodeByIdMap)
    swapDestinationTerminalTailsToReduceCrossings(edges, nodeByIdMap)

    fun finalizeRenderedEdges() {
        resolveRenderedOrthogonalCrossings(edges, nodeByIdMap)
        reassignCrossingExternalRailChannels(edges, nodeByIdMap)
        shortcutRedundantOrthogonalJogs(edges, nodeByIdMap)
        anchorLabelsToPolyline(edges, nodeByIdMap)
        prepareEdgeEndpointsForRenderer(edges, nodeByIdMap)
        liftObstacleHuggingSameSideRails(edges, nodeByIdMap)
        anchorLabelsToPolyline(edges, nodeByIdMap)
        prepareEdgeEndpointsForRenderer(edges, nodeByIdMap)
    }

    finalizeRenderedEdges()
    nudgeSharedInteriorSubpaths(edges, nodeByIdMap)
    finalizeRenderedEdges()

    liftTopLaneTitleBandsAboveRails(edges, nodeByIdMap)
    shiftLeftLaneTitleBandsLeftOfRails(edges, nodeByIdMap)
    liftTopLaneTitleBandsAboveRails(edges, nodeByIdMap)
    shiftLeftLaneTitleBandsLeftOfRails(edges, nodeByIdMap)
}
