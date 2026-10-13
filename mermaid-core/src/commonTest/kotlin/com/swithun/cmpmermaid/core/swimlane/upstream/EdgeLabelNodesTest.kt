package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/__tests__/edgeLabelNodes.spec.ts.
 */
class EdgeLabelNodesTest {
    @Test
    fun doesNotModifyEdgesWithoutLabels() {
        val data = testLayout(
            nodes = listOf(SwimlaneNode("A"), SwimlaneNode("B")),
            edges = listOf(edge("e1", "A", "B")),
        )

        val result = createEdgeLabelNodes(data)

        assertEquals(listOf("A", "B"), result.nodes.map(SwimlaneNode::id))
        assertEquals(listOf("e1"), result.edges.map(SwimlaneEdge::id))
        assertNull(result.edges.single().labelNodeId)
    }

    @Test
    fun createsLabelNodeAndLayoutOnlyVirtualEdges() {
        val original = edge(
            id = "e1",
            start = "A",
            end = "B",
            hasLabel = true,
        )
        val data = testLayout(
            nodes = listOf(
                SwimlaneNode("A", parentId = "lane1"),
                SwimlaneNode("B", parentId = "lane1"),
                SwimlaneNode("lane1", isGroup = true),
            ),
            edges = listOf(original),
        )

        val result = createEdgeLabelNodes(data)

        assertEquals(4, result.nodes.size)
        val labelNode = result.nodes.single(SwimlaneNode::isEdgeLabel)
        assertEquals("edge-label-A-B-e1", labelNode.id)
        assertEquals("lane1", labelNode.parentId)
        assertEquals(0.0, labelNode.width)
        assertEquals(0.0, labelNode.height)
        assertTrue(labelNode.isDummy)

        assertEquals(3, result.edges.size)
        assertEquals("edge-label-A-B-e1", original.labelNodeId)
        assertEquals("A", original.start)
        assertEquals("B", original.end)
        assertFalse(original.isLayoutOnly)

        val toLabel = result.edges.single { it.id == "e1-to-label" }
        assertEquals("A", toLabel.start)
        assertEquals("edge-label-A-B-e1", toLabel.end)
        assertTrue(toLabel.isLayoutOnly)

        val fromLabel = result.edges.single { it.id == "e1-from-label" }
        assertEquals("edge-label-A-B-e1", fromLabel.start)
        assertEquals("B", fromLabel.end)
        assertTrue(fromLabel.isLayoutOnly)
    }

    @Test
    fun createsLabelNodesForMultipleLabelledEdges() {
        val data = testLayout(
            nodes = listOf(
                SwimlaneNode("A"),
                SwimlaneNode("B"),
                SwimlaneNode("C"),
            ),
            edges = listOf(
                edge("e1", "A", "B", hasLabel = true, width = 40.0, height = 20.0),
                edge("e2", "B", "C", hasLabel = true, width = 48.0, height = 20.0),
            ),
        )

        val result = createEdgeLabelNodes(data)

        assertEquals(5, result.nodes.size)
        assertEquals(2, result.nodes.count(SwimlaneNode::isEdgeLabel))
        assertEquals(6, result.edges.size)
        assertEquals(4, result.edges.count(SwimlaneEdge::isLayoutOnly))
    }

    @Test
    fun preservesOriginalEdgeWhenStampingLabelNodeId() {
        val original = edge(
            id = "e1",
            start = "A",
            end = "B",
            hasLabel = true,
            width = 40.0,
            height = 20.0,
        )
        val data = testLayout(
            nodes = listOf(SwimlaneNode("A"), SwimlaneNode("B")),
            edges = listOf(original),
        )

        val result = createEdgeLabelNodes(data)

        assertTrue(result.edges.first() === original)
        assertEquals("A", original.start)
        assertEquals("B", original.end)
        assertEquals(0, original.originalIndex)
        assertEquals("edge-label-A-B-e1", original.labelNodeId)
    }

    @Test
    fun skipsEdgesAlreadyStampedWithLabelNodeId() {
        val original = edge(
            id = "e1",
            start = "A",
            end = "B",
            hasLabel = true,
            width = 40.0,
            height = 20.0,
        ).also { it.labelNodeId = "pre-existing-label" }
        val data = testLayout(
            nodes = listOf(SwimlaneNode("A"), SwimlaneNode("B")),
            edges = listOf(original),
        )

        val result = createEdgeLabelNodes(data)

        assertEquals(2, result.nodes.size)
        assertEquals(listOf(original), result.edges)
        assertEquals("pre-existing-label", original.labelNodeId)
    }

    private fun edge(
        id: String,
        start: String,
        end: String,
        hasLabel: Boolean = false,
        width: Double? = null,
        height: Double? = null,
    ): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = start,
        end = end,
        originalIndex = 0,
        hasLabel = hasLabel,
        width = width,
        height = height,
    )
}
