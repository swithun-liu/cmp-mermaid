package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.flowchart.upstream.dagre.DagreEdge
import com.swithun.cmpmermaid.core.flowchart.upstream.dagre.DagreGraphLabel
import com.swithun.cmpmermaid.core.flowchart.upstream.dagre.DagreNode
import com.swithun.cmpmermaid.core.flowchart.upstream.graphlib.Graph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class MermaidGraphAdapterTest {
    @Test
    fun extractedClusterPreservesUpstreamEdgeInsertionOrder() {
        val graph = Graph<DagreNode, DagreEdge, DagreGraphLabel>(
            multigraph = true,
            compound = true,
        ).setGraph(DagreGraphLabel(rankdir = "LR"))
        graph.setNode("team", DagreNode(originalId = "team"))
        listOf("A", "B", "C", "D").forEach { id ->
            graph.setNode(id, DagreNode(originalId = id))
            graph.setParent(id, "team")
        }
        graph.setEdge("A", "B", DagreEdge(), "L_A_B_0")
        graph.setEdge("B", "C", DagreEdge(), "L_B_C_0")
        graph.setEdge("C", "D", DagreEdge(), "L_C_D_0")
        graph.setEdge("B", "D", DagreEdge(), "L_B_D_0")
        graph.setEdge("C", "D", DagreEdge(), "L_C_D_2")

        assertIs<GMResult.Ok<Unit>>(MermaidGraphAdapter.adjustClustersAndEdges(graph))

        val extracted = assertNotNull(graph.nodeOrThrow("team").graph)
        assertEquals(listOf("A", "B", "C", "D"), extracted.nodes())
        assertEquals(
            listOf(
                "L_A_B_0",
                "L_B_C_0",
                "L_C_D_0",
                "L_B_D_0",
                "L_C_D_2",
            ),
            extracted.edges().map { it.name },
        )
    }
}
