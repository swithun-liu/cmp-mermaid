package com.swithun.cmpmermaid.core.swimlane.upstream

/**
 * Mermaid.js 12.0.0:
 * swimlanes/pipeline.ts -> sugiyamaLayout.
 */
internal fun sugiyamaLayout(
    graph: SwimlaneGraph,
    options: SwimlaneLayoutOptions = SwimlaneLayoutOptions(),
): SwimlaneLayoutResult {
    val ignoreCrossLaneEdges = options.ignoreCrossLaneEdges ?: true
    val optimizeRanksByCrossings = options.optimizeRanksByCrossings ?: true
    val normalized = normalizeGraph(graph)
    val laneOrder = if (options.automaticLaneOrdering == true) {
        optimizeTopLaneOrder(normalized)
    } else {
        null
    }

    val cycleResult = removeCyclesDfs(normalized)
    val acyclic = cycleResult.acyclic
    val layering = if (ignoreCrossLaneEdges) {
        assignLayersLaneAwareCompact(
            acyclic,
            SwimlaneLayeringOptions(
                compactSingleInput = options.compactSingleInput
                    ?: SwimlaneConfig.Layering.DefaultCompactSingleInput,
                ignoreCrossLaneEdges = true,
                direction = options.direction,
            ),
        )
    } else {
        assignLayersGravity(
            acyclic,
            SwimlaneLayeringOptions(
                compactSingleInput = options.compactSingleInput
                    ?: SwimlaneConfig.Layering.DefaultCompactSingleInput,
                ignoreCrossLaneEdges = false,
                optimizeRanksByCrossings = optimizeRanksByCrossings,
            ),
        )
    }
    val proper = makeProperLayering(layering, acyclic)
    val ordered = orderLayers(
        layering = proper.layering,
        graphWithDummies = proper.graphWithDummies,
        laneOrderOverride = laneOrder,
    )
    val coordinates = assignCoordinates(
        ordered = ordered,
        graphWithDummies = proper.graphWithDummies,
        options = CoordinateOptions(
            layerGap = options.layerGap,
            nodeGap = options.nodeGap,
            direction = options.direction,
            laneOrder = laneOrder,
        ),
    )
    return SwimlaneLayoutResult(
        acyclic = acyclic,
        reversed = cycleResult.reversed,
        layering = proper.layering,
        ordered = ordered,
        coordinates = coordinates,
    )
}
