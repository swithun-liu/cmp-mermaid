package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - swimlanes/__tests__/pipeline.hoe.lr.spec.ts
 * - swimlanes/__tests__/pipeline.knsv3.lr.spec.ts
 * - swimlanes/__tests__/pipeline.lr.e2e.spec.ts
 */
class PipelineIntegrationTest {
    @Test
    fun hoeFixtureProducesValidOrthogonalLayout() {
        val layout = runHoeLayout()

        assertEquals(emptyList(), validateSwimlanesLayout(layout))
        assertTrue(renderedEdges(layout).all { edge -> isOrthogonal(edge.points.orEmpty()) })
    }

    @Test
    fun everyHoeLabelCenterLiesOnItsOwnPolyline() {
        val layout = runHoeLayout()
        val nodeById = layout.nodes.associateBy(SwimlaneNode::id)
        val offenders = renderedEdges(layout).filter { edge ->
            val label = edge.labelNodeId?.let(nodeById::get) ?: return@filter false
            !pointLiesOnPolyline(
                SwimlanePoint(label.x ?: 0.0, label.y ?: 0.0),
                edge.points.orEmpty(),
                tolerance = 1.0,
            )
        }

        assertEquals(emptyList(), offenders.map(SwimlaneEdge::id))
    }

    @Test
    fun hoePolylinesHaveNoSpikesCollinearIntermediatesOrZeroProgressLoops() {
        val layout = runHoeLayout()
        val offenders = mutableListOf<String>()
        for (edge in renderedEdges(layout)) {
            val points = edge.points.orEmpty()
            for (index in 1 until points.lastIndex) {
                val previous = points[index - 1]
                val current = points[index]
                val next = points[index + 1]
                if (samePoint(previous, next)) {
                    offenders += "${edge.id}:spike"
                    continue
                }
                val sameColumn = sameX(previous, current) && sameX(current, next)
                val sameRow = sameY(previous, current) && sameY(current, next)
                if (
                    sameColumn &&
                    current.y > min(previous.y, next.y) + DirectionEpsilon &&
                    current.y < max(previous.y, next.y) - DirectionEpsilon
                ) {
                    offenders += "${edge.id}:collinear"
                } else if (
                    sameRow &&
                    current.x > min(previous.x, next.x) + DirectionEpsilon &&
                    current.x < max(previous.x, next.x) - DirectionEpsilon
                ) {
                    offenders += "${edge.id}:collinear"
                }
            }
            if (points.size >= 3 && samePoint(points.first(), points.last(), 1.0)) {
                offenders += "${edge.id}:zeroProgress"
            }
        }

        assertEquals(emptyList(), offenders)
    }

    @Test
    fun hoeFixtureHasNoStrictEdgeCrossings() {
        val layout = runHoeLayout()

        assertEquals(
            0,
            validateSwimlanesLayout(layout).count {
                it.type == SwimlaneValidationIssueType.EdgeEdgeCrossing
            },
        )
    }

    @Test
    fun knsv3FixtureProducesValidOrthogonalLayout() {
        val layout = runKnsv3Layout()

        assertEquals(emptyList(), validateSwimlanesLayout(layout))
        assertTrue(renderedEdges(layout).all { edge -> isOrthogonal(edge.points.orEmpty()) })
    }

    @Test
    fun knsv3IToKPolylineDoesNotIntersectIToJLabelRectangle() {
        val layout = runKnsv3Layout()
        val ij = layout.edges.single { edge -> edge.id == "eI-J" }
        val ik = layout.edges.single { edge -> edge.id == "eI-K" }
        val label = layout.nodes.single { node -> node.id == ij.labelNodeId }
        val labelRect = label.measuredRect()!!

        assertFalse(
            ik.points.orEmpty().zipWithNext().any { (first, second) ->
                segmentBoundsOverlapRect(first, second, labelRect, buffer = -2.0)
            },
        )
    }

    @Test
    fun knsv3CrossingCountDoesNotRegressAboveUpstreamBaseline() {
        val layout = runKnsv3Layout()

        val crossings = validateSwimlanesLayout(layout).count {
            it.type == SwimlaneValidationIssueType.EdgeEdgeCrossing
        }
        assertTrue(crossings <= 3)
    }

    @Test
    fun simpleChainSpreadsHorizontallyAfterLrTransform() {
        val tb = runChainLayout(SwimlaneDirection.TB)
        val lr = runChainLayout(SwimlaneDirection.LR)
        val tbNodes = tb.nodes.filterNot(SwimlaneNode::isGroup)
        val lrNodes = lr.nodes.filterNot(SwimlaneNode::isGroup)
        val xSpanTb = span(tbNodes.mapNotNull(SwimlaneNode::x))
        val ySpanTb = span(tbNodes.mapNotNull(SwimlaneNode::y))
        val xSpanLr = span(lrNodes.mapNotNull(SwimlaneNode::x))
        val ySpanLr = span(lrNodes.mapNotNull(SwimlaneNode::y))

        assertTrue(xSpanTb < ySpanTb)
        assertTrue(xSpanLr > xSpanTb)
        assertTrue(ySpanLr <= ySpanTb)
    }

    private fun runHoeLayout(): SwimlaneLayoutData {
        val layout = testLayout(
            nodes = listOf(
                group("HoE", 420.0, 180.0),
                group("StatusSeeker", 320.0, 180.0),
                group("TechLead", 300.0, 180.0),
                node("HE_CA", "HoE", 110.0, 70.0),
                node("HE_Ans", "HoE", 90.0, 45.0),
                node("HE_Inv", "HoE", 110.0, 45.0),
                node("HE_Ans2", "HoE", 90.0, 45.0),
                node("A", "StatusSeeker", 110.0, 45.0),
                node("B", "StatusSeeker", 120.0, 70.0),
                node("TL_CA", "TechLead", 110.0, 70.0),
                node("TL_Ans", "TechLead", 90.0, 45.0),
            ),
            edges = listOf(
                edge("eHE_CA-HE_Ans", "HE_CA", "HE_Ans", hasLabel = true),
                edge("eHE_CA-HE_Inv", "HE_CA", "HE_Inv", hasLabel = true),
                edge("eHE_Inv-HE_Ans2", "HE_Inv", "HE_Ans2"),
                edge("eA-B", "A", "B"),
                edge("eTL_CA-TL_Ans", "TL_CA", "TL_Ans", hasLabel = true),
                edge("eB-TL_CA", "B", "TL_CA", hasLabel = true),
                edge("eB-HE_CA", "B", "HE_CA", hasLabel = true),
                edge("eTL_CA-HE_CA", "TL_CA", "HE_CA", hasLabel = true),
            ),
            direction = SwimlaneDirection.LR,
        ).copy(nodeGap = 40.0, layerGap = 90.0)
        createEdgeLabelNodes(layout)
        sizeLabel(layout, "eHE_CA-HE_Ans", 42.0, 22.0)
        sizeLabel(layout, "eHE_CA-HE_Inv", 38.0, 22.0)
        sizeLabel(layout, "eTL_CA-TL_Ans", 42.0, 22.0)
        sizeLabel(layout, "eB-TL_CA", 42.0, 22.0)
        sizeLabel(layout, "eB-HE_CA", 38.0, 22.0)
        sizeLabel(layout, "eTL_CA-HE_CA", 38.0, 22.0)
        runSwimlaneLayoutCore(layout)
        return layout
    }

    private fun runKnsv3Layout(): SwimlaneLayoutData {
        val layout = testLayout(
            nodes = listOf(
                group("Constr", 200.0, 400.0),
                group("Legal", 260.0, 400.0),
                node("C", "Constr", 80.0, 50.0),
                node("D", "Constr", 80.0, 50.0),
                node("E", "Constr", 80.0, 50.0),
                node("H", "Constr", 80.0, 50.0),
                node("L", "Constr", 80.0, 50.0),
                node("N", "Constr", 80.0, 50.0),
                node("I", "Legal", 60.0, 50.0),
                node("J", "Legal", 232.0, 150.0),
                node("K", "Legal", 60.0, 40.0),
            ),
            edges = listOf(
                edge("eC-D", "C", "D"),
                edge("eD-E", "D", "E"),
                edge("eD-H", "D", "H"),
                edge("eK-L", "K", "L"),
                edge("eL-N", "L", "N"),
                edge("eJ-E", "J", "E"),
                edge("eH-I", "H", "I"),
                edge("eI-J", "I", "J", hasLabel = true),
                edge("eI-K", "I", "K", hasLabel = true),
            ),
            direction = SwimlaneDirection.LR,
        ).copy(nodeGap = 40.0, layerGap = 100.0)
        createEdgeLabelNodes(layout)
        sizeLabel(layout, "eI-J", 200.0, 42.0)
        sizeLabel(layout, "eI-K", 80.0, 40.0)
        runSwimlaneLayoutCore(layout)
        return layout
    }

    private fun runChainLayout(direction: SwimlaneDirection): SwimlaneLayoutData {
        val layout = testLayout(
            nodes = listOf(
                SwimlaneNode("A", width = 100.0, height = 50.0),
                SwimlaneNode("B", width = 100.0, height = 50.0),
                SwimlaneNode("C", width = 100.0, height = 50.0),
            ),
            edges = listOf(
                edge("eAB", "A", "B"),
                edge("eBC", "B", "C"),
            ),
            direction = direction,
        ).copy(
            nodeGap = 40.0,
            layerGap = 80.0,
            ignoreCrossLaneEdges = false,
        )
        runSwimlaneLayoutCore(layout)
        return layout
    }

    private fun group(id: String, width: Double, height: Double): SwimlaneNode =
        SwimlaneNode(id, width = width, height = height, isGroup = true)

    private fun node(
        id: String,
        parentId: String,
        width: Double,
        height: Double,
    ): SwimlaneNode = SwimlaneNode(
        id = id,
        parentId = parentId,
        width = width,
        height = height,
    )

    private fun edge(
        id: String,
        start: String,
        end: String,
        hasLabel: Boolean = false,
    ): SwimlaneEdge = SwimlaneEdge(
        id = id,
        start = start,
        end = end,
        originalIndex = 0,
        hasLabel = hasLabel,
    )

    private fun sizeLabel(
        layout: SwimlaneLayoutData,
        edgeId: String,
        width: Double,
        height: Double,
    ) {
        val labelNodeId = layout.edges.single { edge -> edge.id == edgeId }.labelNodeId!!
        val labelNode = layout.nodes.single { node -> node.id == labelNodeId }
        labelNode.width = width
        labelNode.height = height
    }

    private fun renderedEdges(layout: SwimlaneLayoutData): List<SwimlaneEdge> =
        layout.edges.filterNot(SwimlaneEdge::isLayoutOnly)

    private fun isOrthogonal(points: List<SwimlanePoint>): Boolean =
        points.size >= 2 && points.zipWithNext().all { (first, second) ->
            sameX(first, second) || sameY(first, second)
        }

    private fun pointLiesOnPolyline(
        point: SwimlanePoint,
        points: List<SwimlanePoint>,
        tolerance: Double,
    ): Boolean = points.zipWithNext().any { (first, second) ->
        if (sameY(first, second)) {
            abs(point.y - first.y) < tolerance &&
                point.x >= min(first.x, second.x) - tolerance &&
                point.x <= max(first.x, second.x) + tolerance
        } else if (sameX(first, second)) {
            abs(point.x - first.x) < tolerance &&
                point.y >= min(first.y, second.y) - tolerance &&
                point.y <= max(first.y, second.y) + tolerance
        } else {
            false
        }
    }

    private fun span(values: List<Double>): Double =
        if (values.isEmpty()) 0.0 else values.max() - values.min()
}
