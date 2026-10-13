package com.swithun.cmpmermaid.core.swimlane.upstream

internal data class SwimlaneCoreResult(
    val direction: SwimlaneDirection,
    val validationIssues: List<SwimlaneValidationIssue>,
)

/**
 * Mermaid.js 12.0.0:
 * swimlanes/layoutCore.ts -> runSwimlaneLayoutCore.
 *
 * The upstream function logs validation warnings and returns only direction.
 * Kotlin returns the same diagnostics with the direction so the adapter can
 * expose evidence without coupling pure layout code to a logger.
 */
internal fun runSwimlaneLayoutCore(
    data: SwimlaneLayoutData,
): SwimlaneCoreResult {
    val graph = toGraphView(data)
    val result = sugiyamaLayout(
        graph = graph,
        options = SwimlaneLayoutOptions(
            nodeGap = data.nodeGap,
            layerGap = data.layerGap,
            ignoreCrossLaneEdges = data.ignoreCrossLaneEdges,
            optimizeRanksByCrossings = data.optimizeRanksByCrossings,
            automaticLaneOrdering = data.automaticLaneOrdering,
            direction = data.direction,
        ),
    )
    writeBackToLayoutData(
        graph = graph,
        ordered = result.ordered,
        coordinates = result.coordinates,
        nodeGap = data.nodeGap,
        layerGap = data.layerGap,
    )

    for (edge in data.edges) {
        edge.points = null
    }
    routeEdgesOrthogonal(data, data.direction)
    postProcessSwimlaneLayout(data, data.direction)
    return SwimlaneCoreResult(
        direction = data.direction,
        validationIssues = validateSwimlanesLayout(data),
    )
}
