package com.swithun.cmpmermaid.core.flowchart

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowLabelType
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidFlowLayoutData
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidFlowLayoutNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FlowchartDataAdapterTest {
    @Test
    fun topLevelSwimlaneTitleUsesCreateTextDefaultMarkdownMode() {
        val result = FlowchartDataAdapter.convert(
            data = MermaidFlowLayoutData(
                nodes = listOf(
                    group(id = "lane", label = "Lane\\nTitle", parentId = null),
                    group(id = "nested", label = "Nested\\nTitle", parentId = "lane"),
                ),
                edges = emptyList(),
            ),
            config = MermaidRenderOptions(layout = "swimlane"),
            directionSource = "LR",
            titleSource = null,
            accessibilityTitleSource = null,
            accessibilityDescriptionSource = null,
        )

        val document = assertIs<GMResult.Ok<FlowchartDocument>>(result).value
        assertEquals("Lane\\nTitle", document.subgraphs.single { it.id == "lane" }.label)
        assertEquals("Nested\nTitle", document.subgraphs.single { it.id == "nested" }.label)
    }

    @Test
    fun discardsInvalidCssPropertyNamesLikeBrowserStyleParsing() {
        val result = FlowStyleAdapter.parse(
            styles = listOf("Line fill:#ffffff", "fill:#123456"),
            owner = "subgraph 'Number'",
        )

        val style = assertIs<GMResult.Ok<FlowNodeStyle>>(result).value
        assertEquals(SceneColor(0xFF123456), style.fill)
    }

    @Test
    fun keepsValidUntranslatedCssPropertiesExplicitlyUnsupported() {
        val result = FlowStyleAdapter.parse(
            styles = listOf("opacity:0.5"),
            owner = "node 'A'",
        )

        val error = assertIs<MermaidError.UnsupportedFeature>(
            assertIs<GMResult.Err<MermaidError>>(result).error,
        )
        assertEquals("CSS opacity", error.feature)
    }

    private fun group(
        id: String,
        label: String,
        parentId: String?,
    ): MermaidFlowLayoutNode = MermaidFlowLayoutNode(
        id = id,
        label = label,
        labelType = FlowLabelType.Text,
        parentId = parentId,
        padding = 8f,
        minWidth = null,
        look = "classic",
        cssStyles = emptyList(),
        cssCompiledStyles = emptyList(),
        cssClasses = "",
        shape = "rect",
        dir = null,
        isGroup = true,
    )
}
