package io.github.cmpmermaid.core

object MermaidCompatibility {
    const val BASELINE_VERSION: String = "12.0.0"
}

sealed interface MermaidError {
    val message: String

    data class UnsupportedDiagram(
        val header: String,
    ) : MermaidError {
        override val message: String = "Unsupported Mermaid diagram: $header"
    }

    data class Parse(
        val line: Int,
        val column: Int,
        override val message: String,
    ) : MermaidError

    data class Layout(
        override val message: String,
    ) : MermaidError
}

data class MermaidRenderOptions(
    val horizontalSpacing: Float = 52f,
    val verticalSpacing: Float = 72f,
    val diagramPadding: Float = 28f,
    val nodeHorizontalPadding: Float = 18f,
    val nodeVerticalPadding: Float = 12f,
    val maxNodeTextWidth: Float = 220f,
    val fontSize: Float = 16f,
)

data class MermaidTheme(
    val background: SceneColor = SceneColor(0xFFF8FAFC),
    val nodeFill: SceneColor = SceneColor(0xFFFFFFFF),
    val nodeStroke: SceneColor = SceneColor(0xFF334155),
    val nodeText: SceneColor = SceneColor(0xFF0F172A),
    val edge: SceneColor = SceneColor(0xFF64748B),
    val edgeLabelFill: SceneColor = SceneColor(0xFFF8FAFC),
    val groupFill: SceneColor = SceneColor(0xFFF1F5F9),
    val groupStroke: SceneColor = SceneColor(0xFFCBD5E1),
    val groupText: SceneColor = SceneColor(0xFF475569),
) {
    companion object {
        val MermaidDefault = MermaidTheme(
            background = SceneColor(0xFFFFFFFF),
            nodeFill = SceneColor(0xFFECECFF),
            nodeStroke = SceneColor(0xFF9370DB),
            nodeText = SceneColor(0xFF131300),
            edge = SceneColor(0xFF333333),
            edgeLabelFill = SceneColor(0xFFFFFFFF),
            groupFill = SceneColor(0xFFFFFFDE),
            groupStroke = SceneColor(0xFFAAAA33),
            groupText = SceneColor(0xFF333333),
        )

        val Dark = MermaidTheme(
            background = SceneColor(0xFF111827),
            nodeFill = SceneColor(0xFF1F2937),
            nodeStroke = SceneColor(0xFF94A3B8),
            nodeText = SceneColor(0xFFF8FAFC),
            edge = SceneColor(0xFF94A3B8),
            edgeLabelFill = SceneColor(0xFF111827),
            groupFill = SceneColor(0xFF182231),
            groupStroke = SceneColor(0xFF475569),
            groupText = SceneColor(0xFFCBD5E1),
        )
    }
}
