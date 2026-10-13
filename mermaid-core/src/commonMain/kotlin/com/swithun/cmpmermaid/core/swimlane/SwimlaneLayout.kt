package com.swithun.cmpmermaid.core.swimlane

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.flowchart.FlowLayoutPlacement
import com.swithun.cmpmermaid.core.flowchart.FlowSubgraph
import com.swithun.cmpmermaid.core.flowchart.FlowchartDocument
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidShapeLayout
import com.swithun.cmpmermaid.core.swimlane.upstream.DefaultSwimlaneId
import com.swithun.cmpmermaid.core.swimlane.upstream.SwimlaneLayoutAdapter

/**
 * Thin Flowchart boundary for the Mermaid.js 12.0.0 Swimlanes translation.
 *
 * The production algorithm lives under [com.swithun.cmpmermaid.core.swimlane.upstream]
 * with one Kotlin stage per upstream source file. This facade only prepares
 * Flowchart data and maps the structured adapter result back to the renderer.
 */
internal object SwimlaneLayout {
    const val DefaultLaneId: String = DefaultSwimlaneId

    // Mermaid.js 12.0.0: swimlanes/helpers.ts -> prepareLayoutForSwimlanes.
    fun prepareDocument(
        document: FlowchartDocument,
        look: String,
    ): FlowchartDocument {
        val assignedNodeIds = document.subgraphs.flatMapTo(mutableSetOf()) { subgraph ->
            subgraph.nodeIds
        }
        val looseNodeIds = document.nodes.keys.filterTo(linkedSetOf()) { id ->
            id !in assignedNodeIds
        }
        if (looseNodeIds.isEmpty()) return document

        val colorIndex = (
            document.subgraphs.mapNotNull(FlowSubgraph::colorIndex) +
                document.nodes.values.mapNotNull { node -> node.colorIndex }
            ).maxOrNull()?.plus(1) ?: 0
        return document.copy(
            subgraphs = document.subgraphs + FlowSubgraph(
                id = DefaultLaneId,
                label = "",
                labelSpans = emptyList(),
                nodeIds = looseNodeIds,
                padding = 20f,
                look = look,
                colorIndex = colorIndex,
            ),
        )
    }

    fun layout(
        document: FlowchartDocument,
        nodeSizes: Map<String, SceneSize>,
        nodeShapeLayouts: Map<String, MermaidShapeLayout>,
        edgeLabelSizes: Map<Int, SceneSize>,
        options: MermaidRenderOptions,
    ): GMResult<FlowLayoutPlacement, MermaidError> = when (
        val result = SwimlaneLayoutAdapter.layout(
            document = document,
            nodeSizes = nodeSizes,
            nodeShapeLayouts = nodeShapeLayouts,
            edgeLabelSizes = edgeLabelSizes,
            options = options,
        )
    ) {
        is GMResult.Ok -> GMResult.Ok(result.value.placement)
        is GMResult.Err -> result
    }
}
