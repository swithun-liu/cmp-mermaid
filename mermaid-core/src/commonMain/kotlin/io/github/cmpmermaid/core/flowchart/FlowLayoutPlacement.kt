package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect

/**
 * Platform-neutral output shared by Mermaid's translated layout adapters.
 */
internal data class FlowLayoutPlacement(
    val nodeBounds: Map<String, SceneRect>,
    val subgraphBounds: Map<String, SceneRect>,
    val edges: Map<Int, FlowRoutedEdge>,
)

internal data class FlowRoutedEdge(
    val points: List<ScenePoint>,
    val labelAnchor: ScenePoint,
    val curveOverride: String? = null,
)
