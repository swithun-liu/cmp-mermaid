package com.swithun.cmpmermaid.core.mindmap

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseBilkentLayout
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseEdgeInput
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseLayoutResult
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
            val result = assertIs<GMResult.Ok<CoseLayoutResult>>(
                CoseBilkentLayout.layout(
                    nodeInputs = fixture.nodes,
                    edgeInputs = fixture.edges,
                ),
                fixture.id,
            ).value
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
