package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern

internal enum class FlowDirection {
    TopToBottom,
    BottomToTop,
    LeftToRight,
    RightToLeft,
}

internal data class FlowNodeStyle(
    val fill: SceneColor? = null,
    val stroke: SceneColor? = null,
    val text: SceneColor? = null,
    val strokeWidth: Float? = null,
)

internal data class FlowNode(
    val id: String,
    val label: String,
    val shape: SceneShapeKind,
    val classes: Set<String> = emptySet(),
    val inlineStyle: FlowNodeStyle? = null,
    val metadata: Map<String, String> = emptyMap(),
)

internal data class FlowEdge(
    val id: String,
    val from: String,
    val to: String,
    val label: String? = null,
    val pattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val arrowStart: SceneArrowHead = SceneArrowHead.None,
    val arrowEnd: SceneArrowHead = SceneArrowHead.Triangle,
    val thickness: Float = 1.7f,
    val minimumLength: Int = 1,
    val invisible: Boolean = false,
    val classes: Set<String> = emptySet(),
    val inlineStyle: FlowNodeStyle? = null,
)

internal data class FlowSubgraph(
    val id: String,
    val label: String,
    val nodeIds: Set<String>,
    val direction: FlowDirection? = null,
    val parentId: String? = null,
    val collapsed: Boolean = false,
)

internal data class FlowchartDocument(
    val direction: FlowDirection,
    val nodes: Map<String, FlowNode>,
    val edges: List<FlowEdge>,
    val subgraphs: List<FlowSubgraph>,
    val classStyles: Map<String, FlowNodeStyle>,
)
