package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/edgeLabelNodes.ts -> createEdgeLabelNodes.
 *
 * The original labelled edge remains the only renderable edge. Two virtual
 * edges constrain Sugiyama placement through the label node.
 */
internal fun createEdgeLabelNodes(data: SwimlaneLayoutData): SwimlaneLayoutData {
    val nodesToAdd = mutableListOf<SwimlaneNode>()
    val layoutOnlyEdges = mutableListOf<SwimlaneEdge>()
    val nodeById = data.nodes.associateByTo(linkedMapOf(), SwimlaneNode::id)

    for (edge in data.edges.toList()) {
        if (!edge.hasLabel) continue
        if (edge.isLayoutOnly || edge.labelNodeId != null) continue

        val start = edge.start ?: continue
        val end = edge.end ?: continue
        val sourceNode = nodeById[start] ?: continue
        val targetNode = nodeById[end] ?: continue
        val labelNodeId = "edge-label-$start-$end-${edge.id}"
        val crossLane = sourceNode.parentId != targetNode.parentId
        val labelLane = if (crossLane) targetNode.parentId else sourceNode.parentId

        nodesToAdd += SwimlaneNode(
            id = labelNodeId,
            parentId = labelLane,
            width = edge.width ?: 0.0,
            height = edge.height ?: 0.0,
            isDummy = true,
            isEdgeLabel = true,
        )
        edge.labelNodeId = labelNodeId
        layoutOnlyEdges += SwimlaneEdge(
            id = "${edge.id}-to-label",
            start = start,
            end = labelNodeId,
            originalIndex = edge.originalIndex,
            isLayoutOnly = true,
        )
        layoutOnlyEdges += SwimlaneEdge(
            id = "${edge.id}-from-label",
            start = labelNodeId,
            end = end,
            originalIndex = edge.originalIndex,
            isLayoutOnly = true,
        )
    }

    data.nodes += nodesToAdd
    data.edges += layoutOnlyEdges
    return data
}
