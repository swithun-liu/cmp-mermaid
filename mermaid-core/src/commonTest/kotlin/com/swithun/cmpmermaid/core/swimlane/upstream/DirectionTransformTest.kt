package com.swithun.cmpmermaid.core.swimlane.upstream

import com.swithun.cmpmermaid.core.SceneShapeKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * swimlanes/__tests__/direction.lr.transform.spec.ts.
 */
class DirectionTransformTest {
    @Test
    fun leavesLayoutUnchangedForTbDirection() {
        val layout = makeTestLayout()
        val snapshot = layout.nodes.associate { node -> node.id to (node.x to node.y) }

        postProcessSwimlaneLayout(layout, SwimlaneDirection.TB)

        assertEquals(snapshot, layout.nodes.associate { node -> node.id to (node.x to node.y) })
    }

    @Test
    fun mapsVerticalLayeringToHorizontalProgressionForLr() {
        val layout = makeTestLayout()

        assertTrue(applyLrDirectionTransform(layout, SwimlaneDirection.LR))

        val nodes = layout.nodes.associateBy(SwimlaneNode::id)
        val a = nodes.getValue("A")
        val b = nodes.getValue("B")
        val c = nodes.getValue("C")
        assertEquals(a.y, b.y)
        assertTrue(b.x!! > a.x!!)
        assertEquals(c.x, a.x)
        assertNotEquals(c.y, a.y)
    }

    @Test
    fun mapsVerticalLayeringToReverseVerticalProgressionForBt() {
        val layout = makeTestLayout()

        assertTrue(applyBtDirectionTransform(layout))

        val nodes = layout.nodes.associateBy(SwimlaneNode::id)
        val a = nodes.getValue("A")
        val b = nodes.getValue("B")
        val c = nodes.getValue("C")
        assertTrue(b.y!! < a.y!!)
        assertEquals(a.x, b.x)
        assertEquals(c.y, a.y)
    }

    @Test
    fun mapsVerticalLayeringToReverseHorizontalProgressionForRl() {
        val layout = makeTestLayout()

        assertTrue(applyLrDirectionTransform(layout, SwimlaneDirection.RL))

        val nodes = layout.nodes.associateBy(SwimlaneNode::id)
        val a = nodes.getValue("A")
        val b = nodes.getValue("B")
        val c = nodes.getValue("C")
        assertEquals(a.y, b.y)
        assertTrue(b.x!! < a.x!!)
        assertEquals(c.x, a.x)
        assertNotEquals(c.y, a.y)
    }

    @Test
    fun positionsLrLanesAsHorizontalStripsWithSharedXSpan() {
        val fixture = graphWithLanesAndNodes()
        writeBackToLayoutData(
            graph = fixture.graph,
            ordered = fixture.ordered,
            coordinates = fixture.coordinates,
            nodeGap = 40.0,
            layerGap = 120.0,
        )
        val before = fixture.graph.layout.nodes
            .filter(SwimlaneNode::isGroup)
            .sortedBy(SwimlaneNode::id)
        assertEquals(before[0].y!!, before[1].y!!, 0.000001)
        assertNotEquals(before[0].x, before[1].x)

        assertTrue(applyLrDirectionTransform(fixture.graph.layout, SwimlaneDirection.LR))

        val after = fixture.graph.layout.nodes
            .filter(SwimlaneNode::isGroup)
            .sortedBy(SwimlaneNode::id)
        assertNotEquals(after[0].y, after[1].y)
        assertEquals(after[0].x!!, after[1].x!!, 0.000001)
    }

    @Test
    fun keepsLaneChildrenInsideTheirBoundsAfterLrTransform() {
        val fixture = graphWithLanesAndNodes()
        writeBackToLayoutData(
            graph = fixture.graph,
            ordered = fixture.ordered,
            coordinates = fixture.coordinates,
            nodeGap = 40.0,
            layerGap = 120.0,
        )

        assertTrue(applyLrDirectionTransform(fixture.graph.layout, SwimlaneDirection.LR))

        val nodes = fixture.graph.layout.nodes
        val contentNodes = nodes.filterNot(SwimlaneNode::isGroup)
        for (lane in nodes.filter(SwimlaneNode::isGroup)) {
            val laneTop = lane.y!! - lane.height / 2.0
            val laneBottom = lane.y!! + lane.height / 2.0
            for (child in contentNodes.filter { node -> node.parentId == lane.id }) {
                val childTop = child.y!! - child.height / 2.0
                val childBottom = child.y!! + child.height / 2.0
                assertTrue(childTop >= laneTop - 0.000001)
                assertTrue(childBottom <= laneBottom + 0.000001)
            }
        }
    }

    @Test
    fun keepsNestedGroupsInsideTopLevelLrLanes() {
        val layout = testLayout(
            nodes = listOf(
                SwimlaneNode("lane1", isGroup = true, padding = 20.0),
                SwimlaneNode("lane2", isGroup = true, padding = 20.0),
                SwimlaneNode(
                    "nested",
                    parentId = "lane1",
                    isGroup = true,
                    padding = 12.0,
                    shape = SceneShapeKind.Rectangle,
                ),
                SwimlaneNode(
                    "A",
                    parentId = "nested",
                    x = 0.0,
                    y = 0.0,
                    width = 80.0,
                    height = 40.0,
                ),
                SwimlaneNode(
                    "B",
                    parentId = "lane2",
                    x = 120.0,
                    y = 0.0,
                    width = 80.0,
                    height = 40.0,
                ),
            ),
            direction = SwimlaneDirection.LR,
        )

        assertTrue(applyLrDirectionTransform(layout, SwimlaneDirection.LR))

        val nodes = layout.nodes.associateBy(SwimlaneNode::id)
        val lane1 = nodes.getValue("lane1")
        val lane2 = nodes.getValue("lane2")
        val nested = nodes.getValue("nested")
        val child = nodes.getValue("A")
        assertEquals("lane1", nested.parentId)
        assertEquals(SceneShapeKind.Rectangle, nested.shape)
        assertEquals(child.x!!, nested.x!!, 0.000001)
        assertTrue(nested.width < lane1.width)
        assertEquals(lane1.x!!, lane2.x!!, 0.000001)

        val nestedLeft = nested.x!! - nested.width / 2.0
        val nestedRight = nested.x!! + nested.width / 2.0
        assertTrue(child.x!! - child.width / 2.0 >= nestedLeft - 0.000001)
        assertTrue(child.x!! + child.width / 2.0 <= nestedRight + 0.000001)
    }

    private fun makeTestLayout(): SwimlaneLayoutData = testLayout(
        nodes = listOf(
            SwimlaneNode("A", x = 0.0, y = 0.0),
            SwimlaneNode("B", x = 0.0, y = 100.0),
            SwimlaneNode("C", x = 50.0, y = 0.0),
        ),
    )

    private data class LaneFixture(
        val graph: SwimlaneGraph,
        val ordered: SwimlaneOrderedLayers,
        val coordinates: SwimlaneCoordinates,
    )

    private fun graphWithLanesAndNodes(): LaneFixture {
        val graph = testGraph(
            nodes = listOf(
                SwimlaneNode("lane1", isGroup = true, padding = 20.0),
                SwimlaneNode("lane2", isGroup = true, padding = 20.0),
                SwimlaneNode("A", parentId = "lane1", width = 80.0, height = 40.0),
                SwimlaneNode("B", parentId = "lane1", width = 80.0, height = 40.0),
                SwimlaneNode("C", parentId = "lane2", width = 80.0, height = 40.0),
            ),
            graphNodeIds = listOf("A", "B", "C"),
        )
        return LaneFixture(
            graph = graph,
            ordered = SwimlaneOrderedLayers(
                mutableListOf(
                    mutableListOf("A", "C"),
                    mutableListOf("B"),
                ),
            ),
            coordinates = SwimlaneCoordinates(
                x = linkedMapOf("A" to -100.0, "C" to 100.0, "B" to -100.0),
                y = linkedMapOf("A" to 0.0, "C" to 0.0, "B" to 120.0),
            ),
        )
    }
}
