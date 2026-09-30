package com.swithun.cmpmermaid.core.statediagram.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StateDataFetcherTest {
    @Test
    fun wrapsNoteInUpstreamInvisibleNoteGroup() {
        val parsed = StateJisonParser().parse(
            """
            stateDiagram-v2
              [*] --> Pending
              note right of Pending : Requires review
              Pending --> Done
            """.trimIndent() + "\n",
        )
        val data = StateDataFetcher.fetch(
            assertIs<GMResult.Ok<StateDb>>(parsed).value,
        )
        val renderData = assertIs<GMResult.Ok<StateRenderData>>(data).value

        val group = renderData.groups.single { it.note }
        val note = renderData.nodes.values.single { it.noteOwner == "Pending" }

        assertEquals("Pending----parent", group.id)
        assertEquals("Requires review", group.label)
        assertNull(group.parentId)
        assertNull(group.direction)
        assertEquals("right of", group.position)
        assertTrue(note.parentId == group.id)
        assertNull(renderData.nodes.getValue("Pending").parentId)
    }

    @Test
    fun rejectsNoteThatPrecedesItsStateLikeUpstreamUndefinedShape() {
        val parsed = StateJisonParser().parse(
            """
            stateDiagram-v2
              note right of Pending
                Requires review
              end note
              Pending --> Done
            """.trimIndent() + "\n",
        )
        val data = StateDataFetcher.fetch(
            assertIs<GMResult.Ok<StateDb>>(parsed).value,
        )

        val error = assertIs<MermaidError.Layout>(
            assertIs<GMResult.Err<MermaidError>>(data).error,
        )
        assertEquals("No such shape: undefined. Please check your syntax.", error.message)
    }

    @Test
    fun sanitizesMalformedSplitKeywordIdsLikeUpstreamDataFetcher() {
        val parsed = StateJisonParser().parse(
            """
            stateDiagram-v2
              s tate split <<fork>>
            """.trimIndent() + "\n",
        )
        val data = StateDataFetcher.fetch(
            assertIs<GMResult.Ok<StateDb>>(parsed).value,
        )
        val renderData = assertIs<GMResult.Ok<StateRenderData>>(data).value

        assertEquals(setOf("s", "tate", "split", "<<fork>>"), renderData.nodes.keys)
        assertEquals(listOf("<>"), renderData.nodes.getValue("<<fork>>").labels)
    }
}
