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

    data class Configuration(
        override val message: String,
    ) : MermaidError

    data class UnsupportedFeature(
        val feature: String,
        override val message: String = "Unsupported Mermaid feature: $feature",
    ) : MermaidError
}

data class MermaidRenderOptions(
    val nodeSpacing: Float = 50f,
    val rankSpacing: Float = 50f,
    val diagramPadding: Float = 8f,
    val wrappingWidth: Float = 120f,
    val minNodeWidth: Float = 120f,
    val flowchartPadding: Float = 15f,
    val curve: String = "basis",
    val fontSize: Float = 16f,
    val look: String = "neo",
    val titleTopMargin: Float = 25f,
    val inheritDirection: Boolean = false,
    val htmlLabels: Boolean = true,
    val markdownAutoWrap: Boolean = true,
    val maxEdges: Int = 500,
)

data class MermaidTheme(
    val background: SceneColor = SceneColor(0xFFFFFFFF),
    val nodeFill: SceneColor = SceneColor(0xFFECECFF),
    val nodeStroke: SceneColor = SceneColor(0xFF9370DB),
    val nodeText: SceneColor = SceneColor(0xFF333333),
    val edge: SceneColor = SceneColor(0xFF333333),
    val edgeLabelFill: SceneColor = SceneColor(0xCCE8E8E8),
    val groupFill: SceneColor = SceneColor(0xFFFFFFDE),
    val groupStroke: SceneColor = SceneColor(0xFFAAAA33),
    val groupText: SceneColor = SceneColor(0xFF333333),
) {
    companion object {
        val MermaidDefault = MermaidTheme()

        val Dark = MermaidTheme(
            background = SceneColor(0xFF333333),
            nodeFill = SceneColor(0xFF1F2020),
            nodeStroke = SceneColor(0xFFCCCCCC),
            nodeText = SceneColor(0xFFCCCCCC),
            edge = SceneColor(0xFFD3D3D3),
            edgeLabelFill = SceneColor(0xFF585858),
            groupFill = SceneColor(0xFF474949),
            groupStroke = SceneColor(0x40FFFFFF),
            groupText = SceneColor(0xFFF9FFFE),
        )
    }
}
