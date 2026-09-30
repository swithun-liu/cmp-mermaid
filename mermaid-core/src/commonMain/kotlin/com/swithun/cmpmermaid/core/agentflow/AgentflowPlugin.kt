package com.swithun.cmpmermaid.core.agentflow

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidDiagramPlugin
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidSceneViewportSizing
import com.swithun.cmpmermaid.core.agentflow.upstream.mermaid.AgentflowJisonParser
import com.swithun.cmpmermaid.core.flowchart.FlowchartDataAdapter
import com.swithun.cmpmermaid.core.flowchart.FlowchartLayout
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidFlowLayoutData

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/diagrams/agentflow/afDetector.ts
 * packages/mermaid/src/diagrams/agentflow/renderer.ts
 */
class AgentflowPlugin : MermaidDiagramPlugin {
    override val id: String = "agentflow"
    override val headers: Set<String> = setOf("agentflow-beta")

    override fun compile(
        source: String,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> = when (
        val parsed = AgentflowJisonParser(
            config = context.options,
            diagramTitle = context.diagramTitle,
            frontmatterLineOffset = context.frontmatterLineOffset,
        ).parse(source)
    ) {
        is GMResult.Ok -> {
            val db = parsed.value
            val data = db.getData(
                paletteLength = context.theme.borderColorArray.size,
                colorTheme = context.options.themeName?.lowercase() in COLOR_THEMES,
                hasBackgroundPalette = context.theme.bkgColorArray.isNotEmpty(),
            )
            when (val validated = validateDagreLeafGroups(data)) {
                is GMResult.Ok -> when (
                    val document = FlowchartDataAdapter.convert(
                        data = data,
                        config = db.config(),
                        directionSource = db.getDirection(),
                        titleSource = db.diagramTitle,
                        accessibilityTitleSource = db.accessibilityTitle,
                        accessibilityDescriptionSource = db.accessibilityDescription,
                    )
                ) {
                    is GMResult.Ok -> when (
                        val scene = FlowchartLayout().layout(document.value, context)
                    ) {
                        is GMResult.Ok -> GMResult.Ok(
                            scene.value.copy(
                                // Mermaid.js 12.0.0:
                                // diagrams/agentflow/renderer.ts -> setupViewPortForSVG.
                                viewportSizing = if (context.options.agentflow.useMaxWidth) {
                                    MermaidSceneViewportSizing.ResponsiveMaxWidth
                                } else {
                                    MermaidSceneViewportSizing.Intrinsic
                                },
                            ),
                        )
                        is GMResult.Err -> scene
                    }
                    is GMResult.Err -> document
                }
                is GMResult.Err -> validated
            }
        }
        is GMResult.Err -> parsed
    }

    private fun validateDagreLeafGroups(
        data: MermaidFlowLayoutData,
    ): GMResult<Unit, MermaidError> {
        val visibleParentIds = data.nodes.mapNotNullTo(mutableSetOf()) { node -> node.parentId }
        val leafGroup = data.nodes.firstOrNull { node ->
            node.isGroup &&
                node.id !in visibleParentIds &&
                node.shape == AGENTFLOW_GROUP_SHAPE
        }
        return if (leafGroup == null) {
            GMResult.Ok(Unit)
        } else {
            // Mermaid.js 12.0.0:
            // rendering-util/layout-algorithms/dagre/index.js -> isCluster
            // rendering-util/rendering-elements/nodes.ts -> insertNode.
            // Dagre paints a group without visible children as a regular node,
            // but flowGroup is only a cluster shape and has no node handler.
            GMResult.Err(
                MermaidError.Layout(
                    "No such shape: ${leafGroup.shape}. Please check your syntax.",
                ),
            )
        }
    }

    private companion object {
        private const val AGENTFLOW_GROUP_SHAPE = "flowGroup"

        // Mermaid.js 12.0.0:
        // packages/mermaid/src/diagrams/common/colorThemeGate.ts -> COLOR_THEMES.
        val COLOR_THEMES = setOf("redux-color", "redux-dark-color")
    }
}
