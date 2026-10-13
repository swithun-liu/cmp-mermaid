package com.swithun.cmpmermaid.core.swimlane.upstream

import com.swithun.cmpmermaid.core.SceneShapeKind
import com.swithun.cmpmermaid.core.flowchart.FlowDirection

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/helpers.ts.
 *
 * These mutable types mirror Mermaid's LayoutData view. Layout algorithms keep
 * JavaScript number semantics internally by using Double and convert to the
 * public Float SceneGraph only at the adapter boundary.
 */
internal data class SwimlanePoint(
    val x: Double,
    val y: Double,
)

internal data class SwimlaneRect(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
) {
    val width: Double get() = right - left
    val height: Double get() = bottom - top
    val centerX: Double get() = (left + right) / 2.0
    val centerY: Double get() = (top + bottom) / 2.0
}

internal data class SwimlaneNode(
    val id: String,
    var parentId: String? = null,
    var width: Double = 0.0,
    var height: Double = 0.0,
    var isGroup: Boolean = false,
    var isDummy: Boolean = false,
    var isEdgeLabel: Boolean = false,
    var isSwimlane: Boolean = false,
    var padding: Double = 0.0,
    var shape: SceneShapeKind? = null,
    var look: String? = null,
    var colorIndex: Int? = null,
    var x: Double? = null,
    var y: Double? = null,
    var layer: Int? = null,
    var order: Int? = null,
    var direction: SwimlaneDirection? = null,
    var swimlaneContentTop: Double? = null,
    var groupTitleRect: SwimlaneRect? = null,
) {
    fun measuredRect(): SwimlaneRect? {
        val centerX = x ?: return null
        val centerY = y ?: return null
        return SwimlaneRect(
            left = centerX - width / 2.0,
            top = centerY - height / 2.0,
            right = centerX + width / 2.0,
            bottom = centerY + height / 2.0,
        )
    }
}

internal class SwimlaneEdge(
    val id: String,
    var start: String?,
    var end: String?,
    val originalIndex: Int,
    var isLayoutOnly: Boolean = false,
    val hasLabel: Boolean = false,
    var labelNodeId: String? = null,
    var points: MutableList<SwimlanePoint>? = null,
    var x: Double? = null,
    var y: Double? = null,
    var width: Double? = null,
    var height: Double? = null,
)

internal data class SwimlaneLayoutData(
    val nodes: MutableList<SwimlaneNode>,
    val edges: MutableList<SwimlaneEdge>,
    val direction: SwimlaneDirection,
    val nodeGap: Double,
    val layerGap: Double,
    val ignoreCrossLaneEdges: Boolean,
    val optimizeRanksByCrossings: Boolean,
    val automaticLaneOrdering: Boolean,
    val look: String? = null,
)

internal data class SwimlaneEdgeRef(
    val id: String,
    val src: String,
    val dst: String,
    val weight: Double? = null,
    val ref: SwimlaneEdge,
)

internal data class SwimlaneGraph(
    val nodes: List<String>,
    val edges: List<SwimlaneEdgeRef>,
    val layout: SwimlaneLayoutData,
    val nodeById: LinkedHashMap<String, SwimlaneNode>,
)

internal data class SwimlaneLayering(
    val layers: MutableList<MutableList<String>>,
    val rankOf: MutableMap<String, Int>,
    val dummy: MutableSet<String> = linkedSetOf(),
)

internal data class SwimlaneOrderedLayers(
    val layers: MutableList<MutableList<String>>,
)

internal data class SwimlaneCoordinates(
    val x: MutableMap<String, Double>,
    val y: MutableMap<String, Double>,
)

internal data class SwimlaneLayoutResult(
    val acyclic: SwimlaneGraph,
    val reversed: List<SwimlaneEdgeRef>,
    val layering: SwimlaneLayering,
    val ordered: SwimlaneOrderedLayers,
    val coordinates: SwimlaneCoordinates,
)

internal enum class SwimlaneDirection {
    TB,
    LR,
    BT,
    RL,
    ;

    val isHorizontal: Boolean get() = this == LR || this == RL

    companion object {
        fun from(direction: FlowDirection): SwimlaneDirection = when (direction) {
            FlowDirection.TopToBottom -> TB
            FlowDirection.LeftToRight -> LR
            FlowDirection.BottomToTop -> BT
            FlowDirection.RightToLeft -> RL
        }
    }
}

internal data class SwimlaneLayoutOptions(
    val compactSingleInput: Boolean? = null,
    val ignoreCrossLaneEdges: Boolean? = null,
    val optimizeRanksByCrossings: Boolean? = null,
    val automaticLaneOrdering: Boolean? = null,
    val layerGap: Double? = null,
    val nodeGap: Double? = null,
    val direction: SwimlaneDirection? = null,
)

internal data class SwimlaneLayeringOptions(
    val compactSingleInput: Boolean? = null,
    val ignoreCrossLaneEdges: Boolean? = null,
    val optimizeRanksByCrossings: Boolean? = null,
    val direction: SwimlaneDirection? = null,
)
