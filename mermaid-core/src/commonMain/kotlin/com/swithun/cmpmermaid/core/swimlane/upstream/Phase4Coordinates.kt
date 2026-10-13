package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.floor
import kotlin.math.max

internal data class CoordinateOptions(
    val layerGap: Double? = null,
    val nodeGap: Double? = null,
    val laneGap: Double? = null,
    val direction: SwimlaneDirection? = null,
    val laneOrder: List<String>? = null,
)

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase4.coordinates.ts -> assignCoordinates.
 */
internal fun assignCoordinates(
    ordered: SwimlaneOrderedLayers,
    graphWithDummies: SwimlaneGraph,
    options: CoordinateOptions = CoordinateOptions(),
): SwimlaneCoordinates {
    val layerGap = options.layerGap ?: SwimlaneConfig.Coordinates.DefaultLayerGap
    val nodeGap = options.nodeGap ?: SwimlaneConfig.Coordinates.DefaultNodeGap
    val laneGap = options.laneGap ?: nodeGap * 2.0
    val direction = options.direction ?: SwimlaneDirection.TB
    val layers = ordered.layers

    val x = linkedMapOf<String, Double>()
    val y = linkedMapOf<String, Double>()
    fun width(id: String): Double = graphWithDummies.nodeById[id]?.width ?: 0.0
    fun height(id: String): Double = graphWithDummies.nodeById[id]?.height ?: 0.0

    val topLaneOf = createTopLaneResolver(graphWithDummies)
    val globalLaneOrder = resolveTopLaneOrder(graphWithDummies, options.laneOrder)
    val layerHeights = layers.map { layer -> layer.maxOfOrNull(::height) ?: 0.0 }
    val extraLayerGaps = mutableListOf<Double>()
    if (direction.isHorizontal) {
        for (index in 0 until (layers.size - 1).coerceAtLeast(0)) {
            val thisMaximumWidth = layers[index].maxOfOrNull(::width) ?: 0.0
            val nextMaximumWidth = layers[index + 1].maxOfOrNull(::width) ?: 0.0
            val normalSpacing = layerHeights[index] / 2.0 + layerHeights[index + 1] / 2.0
            val requiredSpacing = (thisMaximumWidth + nextMaximumWidth) / 2.0
            extraLayerGaps += max(0.0, requiredSpacing - normalSpacing - layerGap)
        }
    }

    val lanesUsedSet = linkedSetOf<String?>()
    layers.forEach { layer -> layer.forEach { id -> lanesUsedSet += topLaneOf(id) } }
    val hasNullLane = null in lanesUsedSet
    val lanesUsed = globalLaneOrder.filter { lane -> lane in lanesUsedSet }
    val laneOrderColumns = buildList<String?> {
        if (hasNullLane) add(null)
        addAll(lanesUsed)
    }

    val laneWidth = mutableMapOf<String?, Double>()
    lanesUsed.forEach { lane -> laneWidth[lane] = 0.0 }
    if (hasNullLane) laneWidth[null] = 0.0
    layers.forEach { layer ->
        val perLane = linkedMapOf<String?, MutableList<String>>()
        layer.forEach { id -> perLane.getOrPut(topLaneOf(id)) { mutableListOf() } += id }
        perLane.forEach { (lane, ids) ->
            val total = ids.sumOf(::width) + nodeGap * max(0, ids.size - 1)
            laneWidth[lane] = max(laneWidth[lane] ?: 0.0, total)
        }
    }

    val centerX = mutableMapOf<String?, Double>()
    val widths = laneOrderColumns.map { lane -> laneWidth[lane] ?: 0.0 }
    val totalWidth = widths.sum() + laneGap * max(0, laneOrderColumns.size - 1)
    var cursor = -totalWidth / 2.0
    laneOrderColumns.forEachIndexed { index, lane ->
        val currentWidth = widths[index]
        centerX[lane] = cursor + currentWidth / 2.0
        cursor += currentWidth
        if (index < laneOrderColumns.lastIndex) cursor += laneGap
    }

    var yOffset = 0.0
    layers.forEachIndexed { layerIndex, layer ->
        val layerHeight = layerHeights[layerIndex]
        val byLane = linkedMapOf<String?, MutableList<String>>()
        layer.forEach { id -> byLane.getOrPut(topLaneOf(id)) { mutableListOf() } += id }
        laneOrderColumns.forEach { lane ->
            val nodesInLane = byLane[lane].orEmpty()
            if (nodesInLane.isEmpty()) return@forEach
            val laneCenterX = centerX.getValue(lane)
            if (nodesInLane.size == 1) {
                val id = nodesInLane[0]
                x[id] = laneCenterX
                y[id] = yOffset + layerHeight / 2.0
            } else {
                val nodeWidths = nodesInLane.map(::width)
                val total = nodeWidths.sum() + nodeGap * (nodesInLane.size - 1)
                var start = laneCenterX - total / 2.0
                nodesInLane.forEachIndexed { index, id ->
                    val nodeWidth = nodeWidths[index]
                    x[id] = start + nodeWidth / 2.0
                    y[id] = yOffset + layerHeight / 2.0
                    start += nodeWidth + nodeGap
                }
            }
        }
        yOffset += layerHeight + layerGap + (extraLayerGaps.getOrNull(layerIndex) ?: 0.0)
    }

    val byReference = linkedMapOf<String, MutableList<SwimlaneEdgeRef>>()
    graphWithDummies.edges.forEach { edge ->
        byReference.getOrPut(edge.ref.id) { mutableListOf() } += edge
    }
    byReference.values.forEach { chainEdges ->
        if (chainEdges.isEmpty()) return@forEach
        val reference = chainEdges[0].ref
        val source = reference.start ?: return@forEach
        val destination = reference.end ?: return@forEach
        val midpoint = floor(((x[source] ?: 0.0) + (x[destination] ?: 0.0)) / 2.0 + 0.5)
        val involved = linkedSetOf<String>()
        chainEdges.forEach { edge ->
            involved += edge.src
            involved += edge.dst
        }
        involved.forEach { id ->
            if (id != source && id != destination && graphWithDummies.nodeById[id]?.isDummy == true) {
                x[id] = midpoint
            }
        }
    }

    return SwimlaneCoordinates(x = x, y = y)
}
