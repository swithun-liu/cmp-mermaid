package com.swithun.cmpmermaid.core.mindmap

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseBilkentLayout
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseEdgeInput
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseLayoutResult
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseLayoutTraceRequest
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseLayoutTraceSnapshot
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseNodeInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CoseBilkentLayoutTest {
    @Test
    fun laysOutAFlatTreeDeterministically() {
        val nodes = listOf(
            CoseNodeInput("root", SceneSize(80f, 50f)),
            CoseNodeInput("a", SceneSize(70f, 34f)),
            CoseNodeInput("b", SceneSize(90f, 34f)),
            CoseNodeInput("c", SceneSize(60f, 34f)),
        )
        val edges = listOf(
            CoseEdgeInput("root-a", "root", "a"),
            CoseEdgeInput("root-b", "root", "b"),
            CoseEdgeInput("a-c", "a", "c"),
        )

        val first = assertIs<GMResult.Ok<*>>(
            CoseBilkentLayout.layout(nodes, edges),
        ).value
        val second = assertIs<GMResult.Ok<*>>(
            CoseBilkentLayout.layout(nodes, edges),
        ).value

        assertEquals(first, second)
        val result = first as
            com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseLayoutResult
        assertEquals(nodes.map(CoseNodeInput::id).toSet(), result.nodeCenters.keys)
        assertEquals(result.nodeCenters.size, result.nodeCenters.values.toSet().size)
        assertTrue(result.nodeCenters.values.all { point ->
            point.x.isFinite() && point.y.isFinite()
        })
    }

    @Test
    fun matchesMermaid1200CoseBilkentForFixedNodeGeometry() {
        coseBilkentParityFixtures.forEach { fixture ->
            val traceSnapshots = mutableListOf<CoseLayoutTraceSnapshot>()
            val result = assertIs<GMResult.Ok<CoseLayoutResult>>(
                CoseBilkentLayout.layout(
                    nodeInputs = fixture.nodes,
                    edgeInputs = fixture.edges,
                    trace = CoseLayoutTraceRequest(
                        iterations = fixture.expectedTraceSnapshots
                            .mapTo(linkedSetOf(), CoseBilkentExpectedTraceSnapshot::iteration),
                        onSnapshot = traceSnapshots::add,
                    ),
                ),
                fixture.id,
            ).value

            assertEquals(
                fixture.expectedTraceSnapshots.map { snapshot -> snapshot.iteration },
                traceSnapshots.map { snapshot -> snapshot.iteration },
                "${fixture.id} trace iterations",
            )
            val traceDeviations = mutableListOf<String>()
            fixture.expectedTraceSnapshots.zip(traceSnapshots).forEach {
                    (expectedSnapshot, actualSnapshot) ->
                val traceAnchor =
                    actualSnapshot.nodeCenters.getValue(fixture.anchorId)
                var maximumDelta = 0f
                var maximumDeltaLabel = ""
                expectedSnapshot.relativeCenters.forEach { (nodeId, expected) ->
                    val actual = actualSnapshot.nodeCenters.getValue(nodeId)
                    val deltaX = kotlin.math.abs(
                        expected.x - (actual.x - traceAnchor.x),
                    )
                    val deltaY = kotlin.math.abs(
                        expected.y - (actual.y - traceAnchor.y),
                    )
                    if (deltaX > maximumDelta) {
                        maximumDelta = deltaX
                        maximumDeltaLabel = "$nodeId x"
                    }
                    if (deltaY > maximumDelta) {
                        maximumDelta = deltaY
                        maximumDeltaLabel = "$nodeId y"
                    }
                }
                if (maximumDelta > 0.001f) {
                    traceDeviations +=
                        "iteration ${expectedSnapshot.iteration}: " +
                        "$maximumDeltaLabel delta=$maximumDelta"
                }
            }
            assertTrue(
                traceDeviations.isEmpty(),
                "${fixture.id} trace deviations:\n${traceDeviations.joinToString("\n")}",
            )

            if (fixture.compareFinalCoordinates) {
                val anchor = result.nodeCenters.getValue(fixture.anchorId)
                assertEquals(
                    fixture.expectedRelativeCenters.keys,
                    result.nodeCenters.keys,
                    fixture.id,
                )
                fixture.expectedRelativeCenters.forEach { (nodeId, expected) ->
                    val actual = result.nodeCenters.getValue(nodeId)
                    assertEquals(
                        expected.x,
                        actual.x - anchor.x,
                        0.001f,
                        "${fixture.id}/$nodeId x",
                    )
                    assertEquals(
                        expected.y,
                        actual.y - anchor.y,
                        0.001f,
                        "${fixture.id}/$nodeId y",
                    )
                }
            } else {
                val repeated = assertIs<GMResult.Ok<CoseLayoutResult>>(
                    CoseBilkentLayout.layout(fixture.nodes, fixture.edges),
                    "${fixture.id} deterministic replay",
                ).value
                assertEquals(result, repeated, "${fixture.id} deterministic replay")
            }
        }
    }

    @Test
    fun rejectsEmptyAndInvalidNodeInputs() {
        val empty = CoseBilkentLayout.layout(emptyList(), emptyList())
        val duplicate = CoseBilkentLayout.layout(
            nodeInputs = listOf(
                CoseNodeInput("same", SceneSize(10f, 10f)),
                CoseNodeInput("same", SceneSize(20f, 20f)),
            ),
            edgeInputs = emptyList(),
        )
        val invalidSize = CoseBilkentLayout.layout(
            nodeInputs = listOf(CoseNodeInput("node", SceneSize(0f, 10f))),
            edgeInputs = emptyList(),
        )

        assertIs<GMResult.Err<MermaidError.Layout>>(empty)
        assertIs<GMResult.Err<MermaidError.Layout>>(duplicate)
        assertIs<GMResult.Err<MermaidError.Layout>>(invalidSize)
    }

    @Test
    fun rejectsEdgesWithMissingEndpoints() {
        val result = CoseBilkentLayout.layout(
            nodeInputs = listOf(CoseNodeInput("root", SceneSize(10f, 10f))),
            edgeInputs = listOf(CoseEdgeInput("missing", "root", "child")),
        )

        assertIs<GMResult.Err<MermaidError.Layout>>(result)
    }

    @Test
    fun rejectsNonForestGraphsInsteadOfChangingAlgorithms() {
        val result = CoseBilkentLayout.layout(
            nodeInputs = listOf(
                CoseNodeInput("a", SceneSize(10f, 10f)),
                CoseNodeInput("b", SceneSize(10f, 10f)),
                CoseNodeInput("c", SceneSize(10f, 10f)),
            ),
            edgeInputs = listOf(
                CoseEdgeInput("a-b", "a", "b"),
                CoseEdgeInput("b-c", "b", "c"),
                CoseEdgeInput("c-a", "c", "a"),
            ),
        )

        val error = assertIs<GMResult.Err<MermaidError.UnsupportedFeature>>(result).error
        assertNotEquals("", error.message)
    }
}
