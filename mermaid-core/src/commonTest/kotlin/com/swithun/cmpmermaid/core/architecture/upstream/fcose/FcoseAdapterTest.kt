package com.swithun.cmpmermaid.core.architecture.upstream.fcose

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseAlignmentConstraint
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseRelativePlacementConstraint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FcoseAdapterTest {
    @Test
    fun matchesUpstreamForConstrainedHorizontalPair() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("a"),
                    node("b"),
                ),
                edges = listOf(
                    FcoseEdgeInput(
                        id = "a-b",
                        sourceId = "a",
                        targetId = "b",
                        idealLength = 120.0,
                        elasticity = 0.45,
                    ),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(listOf("a", "b")),
                ),
                relativePlacementConstraint = listOf(
                    CoseRelativePlacementConstraint(
                        left = "a",
                        right = "b",
                        gap = 120.0,
                    ),
                ),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertEquals(-100.34328288059254, positions.getValue("a").centerX, 1e-9)
        assertEquals(0.0, positions.getValue("a").centerY, 1e-9)
        assertEquals(100.34328288059253, positions.getValue("b").centerX, 1e-9)
        assertEquals(0.0, positions.getValue("b").centerY, 1e-9)
    }

    @Test
    fun matchesUpstreamForDisconnectedRootAndCompoundChains() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("g", width = 110.0, height = 110.0, padding = 40.0),
                    node("r1"),
                    node("r2"),
                    node("a", parentId = "g"),
                    node("b", parentId = "g"),
                    node("c", parentId = "g"),
                ),
                edges = listOf(
                    edge("r1", "r2"),
                    edge("a", "b"),
                    edge("b", "c"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(
                        listOf("r1", "r2"),
                        listOf("a", "b", "c"),
                    ),
                ),
                relativePlacementConstraint = listOf(
                    relative("r1", "r2"),
                    relative("a", "b"),
                    relative("b", "c"),
                ),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "g", 0.0, 113.19332762623043)
        assertPosition(positions, "r1", -100.56599271218957, -151.69332762623043)
        assertPosition(positions, "r2", 100.56599271218951, -151.69332762623043)
        assertPosition(positions, "a", -200.68652784647549, 113.19332762623043)
        assertPosition(positions, "b", 0.0, 113.19332762623043)
        assertPosition(positions, "c", 200.68652784647549, 113.19332762623043)
        assertEquals(-283.1865278464755, positions.getValue("g").left, 1e-9)
        assertEquals(566.373055692951, positions.getValue("g").width, 1e-9)
    }

    @Test
    fun matchesUpstreamForNestedCompoundsAndMixedAxisConstraints() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("outer", width = 110.0, height = 110.0, padding = 40.0),
                    node(
                        "inner1",
                        parentId = "outer",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                    ),
                    node(
                        "inner2",
                        parentId = "outer",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                    ),
                    node("a", parentId = "inner1"),
                    node("b", parentId = "inner1"),
                    node("c", parentId = "inner2"),
                    node("d"),
                ),
                edges = listOf(
                    edge("a", "b"),
                    crossEdge("b", "c"),
                    crossEdge("c", "d"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(listOf("a", "b")),
                    vertical = listOf(listOf("b", "c")),
                ),
                relativePlacementConstraint = listOf(
                    relative("a", "b"),
                    CoseRelativePlacementConstraint(top = "b", bottom = "c", gap = 120.0),
                    relative("c", "d"),
                ),
                numIterations = 2500,
                seed = 7f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "outer", -85.83959398834651, 40.45075106253549)
        assertPosition(positions, "inner1", -85.83959398834651, -87.22517557817258)
        assertPosition(positions, "inner2", 14.721126703524602, 168.12667770324356)
        assertPosition(positions, "a", -186.4003146802176, -87.2251755781726)
        assertPosition(positions, "b", 14.7211267035246, -87.2251755781726)
        assertPosition(positions, "c", 14.7211267035246, 168.12667770324356)
        assertPosition(positions, "d", 263.4003146802176, -248.12667770324356)
        assertEquals(-310.4003146802176, positions.getValue("outer").left, 1e-9)
        assertEquals(449.1214413837422, positions.getValue("outer").width, 1e-9)
    }

    @Test
    fun matchesUpstreamForUnconnectedAlignedRootsAroundNestedCompound() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("platform", width = 110.0, height = 110.0, padding = 40.0),
                    node(
                        "runtime",
                        parentId = "platform",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                    ),
                    node(
                        "delivery",
                        parentId = "platform",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                    ),
                    node("s1", parentId = "runtime"),
                    node("s2", parentId = "delivery"),
                    node("s3", parentId = "delivery"),
                    node("s4", parentId = "platform"),
                    node("align_a"),
                    node("align_b"),
                    node("align_c"),
                ),
                edges = listOf(
                    crossEdge("s1", "s2"),
                    edge("s2", "s3"),
                    crossEdge("s3", "s4"),
                    crossEdge("s1", "s4"),
                    crossEdge("s2", "s4"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(
                        listOf("s1", "s2"),
                        listOf("align_a", "align_b", "align_c"),
                    ),
                    vertical = listOf(listOf("s2", "s4")),
                ),
                relativePlacementConstraint = listOf(
                    relative("align_a", "align_b"),
                    relative("align_b", "align_c"),
                    relative("s1", "s2"),
                    CoseRelativePlacementConstraint(
                        top = "s1",
                        bottom = "s3",
                        gap = 120.0,
                    ),
                    CoseRelativePlacementConstraint(
                        top = "s2",
                        bottom = "s4",
                        gap = 120.0,
                    ),
                ),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "s1", -156.47313950324565, -72.45151282831121)
        assertPosition(positions, "s2", 156.4731395032456, -72.45151282831121)
        assertPosition(positions, "s3", 95.90389114469063, 121.69964368945264)
        assertPosition(positions, "s4", 156.4731395032456, 334.96010093414054)
        assertPosition(positions, "align_a", -145.8887506102133, -373.4601009341404)
        assertPosition(positions, "align_b", 11.731573442550541, -373.4601009341404)
        assertPosition(positions, "align_c", 169.35189749531443, -373.4601009341404)
    }

    @Test
    fun matchesUpstreamForLabelSizedTopLevelCompoundsAndAlignedRoots() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("runtime", width = 110.0, height = 110.0, padding = 40.0),
                    node("delivery", width = 110.0, height = 110.0, padding = 40.0),
                    node(
                        "s1",
                        parentId = "runtime",
                        labelWidth = 161.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        "s2",
                        parentId = "delivery",
                        labelWidth = 176.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        "s3",
                        parentId = "delivery",
                        labelWidth = 134.0,
                        labelHeight = 16.0,
                    ),
                    node("s4", labelWidth = 145.0, labelHeight = 16.0),
                    node("align_a", labelWidth = 99.0, labelHeight = 16.0),
                    node("align_b", labelWidth = 92.0, labelHeight = 16.0),
                    node("align_c", labelWidth = 115.0, labelHeight = 16.0),
                    node("split"),
                ),
                edges = listOf(
                    crossEdge("s1", "split"),
                    crossEdge("split", "s2"),
                    crossEdge("split", "s3"),
                    crossEdge("s3", "s4"),
                    crossEdge("s2", "s4"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(
                        listOf("s1", "split"),
                        listOf("s3", "s4"),
                        listOf("align_a", "align_b", "align_c"),
                    ),
                    vertical = listOf(
                        listOf("split", "s3"),
                        listOf("s2", "s4"),
                    ),
                ),
                relativePlacementConstraint = listOf(
                    relative("align_a", "align_b"),
                    relative("align_b", "align_c"),
                    relative("s1", "split"),
                    relative("split", "s2"),
                    CoseRelativePlacementConstraint(
                        top = "split",
                        bottom = "s3",
                        gap = 120.0,
                    ),
                    CoseRelativePlacementConstraint(
                        top = "s2",
                        bottom = "s4",
                        gap = 120.0,
                    ),
                    relative("s3", "s4"),
                ),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "s1", -330.1246567993561, -267.9599179089739)
        assertPosition(positions, "s2", 290.98362582324285, -84.62889188745339)
        assertPosition(positions, "s3", -121.37855252606349, 301.959917908974)
        assertPosition(positions, "s4", 290.98362582324285, 301.959917908974)
        assertPosition(positions, "align_a", 38.338252903738834, -286.11901515780903)
        assertPosition(positions, "align_b", 190.42208755527608, -286.11901515780903)
        assertPosition(positions, "align_c", 346.6246567993561, -286.11901515780903)
        assertPosition(positions, "split", -121.37855252606349, -267.9599179089739)
    }

    @Test
    fun includesServiceLabelsInCompoundVisualBoundsWithoutChangingLayoutGeometry() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node("platform", width = 110.0, height = 110.0, padding = 40.0),
                    node("s1", parentId = "platform", labelWidth = 134.0, labelHeight = 16.0),
                    node("s2", parentId = "platform", labelWidth = 163.0, labelHeight = 16.0),
                    node("s3", parentId = "platform", labelWidth = 154.0, labelHeight = 16.0),
                    node("s4", parentId = "platform", labelWidth = 155.0, labelHeight = 16.0),
                ),
                edges = listOf(
                    edge("s1", "s2"),
                    edge("s2", "s3"),
                    edge("s3", "s4"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(
                    horizontal = listOf(listOf("s1", "s2", "s3", "s4")),
                ),
                relativePlacementConstraint = listOf(
                    relative("s1", "s2"),
                    relative("s2", "s3"),
                    relative("s3", "s4"),
                ),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "platform", 10.500000000000028, 25.5)
        assertPosition(positions, "s1", -295.7502340994687, 17.0)
        assertPosition(positions, "s2", -95.08101614629692, 17.0)
        assertPosition(positions, "s3", 105.581016146297, 17.0)
        assertPosition(positions, "s4", 306.25023409946874, 17.0)

        val firstService = positions.getValue("s1")
        assertEquals(-365.7502340994687, firstService.left, 1e-9)
        assertEquals(-24.0, firstService.top, 1e-9)
        assertEquals(140.0, firstService.width, 1e-9)
        assertEquals(100.0, firstService.height, 1e-9)

        val platform = positions.getValue("platform")
        assertEquals(-406.2502340994687, platform.left, 1e-9)
        assertEquals(-65.5, platform.top, 1e-9)
        assertEquals(833.5004681989374, platform.width, 1e-9)
        assertEquals(182.0, platform.height, 1e-9)
    }

    @Test
    fun matchesUpstreamForNestedCompoundLabelsAcrossBothRuns() {
        val result = FcoseAdapter.runTwice(
            FcoseOptions(
                nodes = listOf(
                    node(
                        id = "platform",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                        labelWidth = 240.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        id = "delivery",
                        parentId = "platform",
                        width = 110.0,
                        height = 110.0,
                        padding = 40.0,
                        labelWidth = 57.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        id = "s1",
                        parentId = "platform",
                        labelWidth = 181.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        id = "s2",
                        parentId = "delivery",
                        labelWidth = 161.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        id = "s3",
                        parentId = "delivery",
                        labelWidth = 173.0,
                        labelHeight = 16.0,
                    ),
                    node(
                        id = "s4",
                        parentId = "platform",
                        labelWidth = 162.0,
                        labelHeight = 16.0,
                    ),
                ),
                edges = listOf(
                    crossEdge("s1", "s2"),
                    edge("s2", "s3"),
                    crossEdge("s3", "s4"),
                    edge("s1", "s4"),
                    crossEdge("s2", "s4"),
                ),
                alignmentConstraint = CoseAlignmentConstraint(),
                relativePlacementConstraint = emptyList(),
                numIterations = 2500,
                seed = 1f,
            ),
        )

        val positions = assertIs<GMResult.Ok<Map<String, FcoseNodeResult>>>(result).value
        assertPosition(positions, "platform", 1.0000000000000284, 69.25000000000003)
        assertPosition(positions, "delivery", 150.9163114755723, 213.6663114755723)
        assertPosition(positions, "s1", -111.31275017140672, -60.06275017140686)
        assertPosition(positions, "s2", 236.41203464999313, 287.66203464999313)
        assertPosition(positions, "s3", 71.42058830115143, 122.67058830115144)
        assertPosition(positions, "s4", -275.4120346499931, -224.16203464999307)

        val platform = positions.getValue("platform")
        assertEquals(-399.9120346499931, platform.left, 1e-9)
        assertEquals(-306.6620346499931, platform.top, 1e-9)
        assertEquals(801.8240692999861, platform.width, 1e-9)
        assertEquals(769.3240692999861, platform.height, 1e-9)
    }

    private fun node(
        id: String,
        parentId: String? = null,
        width: Double = 80.0,
        height: Double = 80.0,
        padding: Double = 0.0,
        labelWidth: Double? = null,
        labelHeight: Double? = null,
    ) = FcoseNodeInput(
        id = id,
        parentId = parentId,
        centerX = 0.0,
        centerY = 0.0,
        width = width,
        height = height,
        padding = padding,
        labelWidth = labelWidth,
        labelHeight = labelHeight,
    )

    private fun edge(
        source: String,
        target: String,
    ) = FcoseEdgeInput(
        id = "$source-$target",
        sourceId = source,
        targetId = target,
        idealLength = 120.0,
        elasticity = 0.45,
    )

    private fun crossEdge(
        source: String,
        target: String,
    ) = FcoseEdgeInput(
        id = "$source-$target",
        sourceId = source,
        targetId = target,
        idealLength = 40.0,
        elasticity = 0.001,
    )

    private fun relative(
        left: String,
        right: String,
    ) = CoseRelativePlacementConstraint(
        left = left,
        right = right,
        gap = 120.0,
    )

    private fun assertPosition(
        positions: Map<String, FcoseNodeResult>,
        id: String,
        x: Double,
        y: Double,
    ) {
        assertEquals(x, positions.getValue(id).centerX, 1e-9, "$id x; positions=$positions")
        assertEquals(y, positions.getValue(id).centerY, 1e-9, "$id y")
    }
}
