package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneTextSpan
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.FlowLabelType

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
    val labelBackground: SceneColor? = null,
    val strokeWidth: Float? = null,
    val strokePattern: SceneStrokePattern? = null,
    val dashIntervals: List<Float> = emptyList(),
    val fontSize: Float? = null,
    val fontWeight: SceneTextWeight? = null,
)

internal data class FlowNode(
    val id: String,
    val label: String,
    val labelSpans: List<SceneTextSpan>,
    val labelType: FlowLabelType,
    val shape: SceneShapeKind,
    val padding: Float,
    val minWidth: Float?,
    val look: String,
    val inlineStyle: FlowNodeStyle? = null,
    val metadata: Map<String, String> = emptyMap(),
    val link: String? = null,
    val linkTarget: String? = null,
    val tooltip: String? = null,
    val icon: String? = null,
    val position: String? = null,
    val image: String? = null,
    val assetWidth: Float? = null,
    val assetHeight: Float? = null,
    val constraint: String? = null,
)

internal data class FlowEdge(
    val id: String,
    val from: String,
    val to: String,
    val label: String? = null,
    val labelSpans: List<SceneTextSpan> = emptyList(),
    val pattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val arrowStart: SceneArrowHead = SceneArrowHead.None,
    val arrowEnd: SceneArrowHead = SceneArrowHead.Triangle,
    val thickness: Float = 1f,
    val minimumLength: Int = 1,
    val invisible: Boolean = false,
    val inlineStyle: FlowNodeStyle? = null,
    val animated: Boolean = false,
    val curve: String? = null,
    val look: String,
)

internal data class FlowSubgraph(
    val id: String,
    val label: String,
    val labelSpans: List<SceneTextSpan>,
    val nodeIds: Set<String>,
    val direction: FlowDirection? = null,
    val parentId: String? = null,
    val padding: Float,
    val look: String,
    val inlineStyle: FlowNodeStyle? = null,
)

internal data class FlowchartDocument(
    val direction: FlowDirection,
    val nodes: Map<String, FlowNode>,
    val edges: List<FlowEdge>,
    val subgraphs: List<FlowSubgraph>,
    val title: String? = null,
    val accessibilityTitle: String? = null,
    val accessibilityDescription: String? = null,
)
