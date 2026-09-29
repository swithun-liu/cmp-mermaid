package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class MermaidUpstreamErrorFormatterTest {
    @Test
    fun preservesParseMetadataWhileFormattingKnownUpstreamError() {
        val source = "flowchart TD\nA[\n"
        val original = MermaidError.Parse(
            line = 2,
            column = 2,
            message = "Unrecognized flowchart text",
        )

        val formatted = assertIs<MermaidError.Parse>(
            MermaidUpstreamErrorFormatter.format(
                diagramId = "flowchart",
                source = source,
                error = original,
            ),
        )

        assertEquals(original.line, formatted.line)
        assertEquals(original.column, formatted.column)
        assertEquals(original.renderErrorType, formatted.renderErrorType)
        assertEquals(
            "Parse error on line 3:\nflowchart TDA[\n--------------^\n" +
                "Expecting 'SQE', 'DOUBLECIRCLEEND', 'PE', '-)', 'STADIUMEND', " +
                "'SUBROUTINEEND', 'PIPE', 'CYLINDEREND', 'DIAMOND_STOP', " +
                "'TAGEND', 'TRAPEND', 'INVTRAPEND', 'UNICODE_TEXT', 'TEXT', " +
                "'TAGSTART', got '1'",
            formatted.message,
        )
    }

    @Test
    fun leavesUnknownParserBranchesUntouched() {
        val original = MermaidError.Parse(4, 8, "A new upstream branch")

        val formatted = MermaidUpstreamErrorFormatter.format(
            diagramId = "flowchart",
            source = "flowchart TD\nA --> B\n",
            error = original,
        )

        assertSame(original, formatted)
    }

    @Test
    fun neverRewritesUnexpectedExceptions() {
        val original = MermaidError.Unexpected(
            exceptionType = "IllegalStateException",
            message = "Unrecognized flowchart text",
        )

        val formatted = MermaidUpstreamErrorFormatter.format(
            diagramId = "flowchart",
            source = "flowchart TD\nA[\n",
            error = original,
        )

        assertSame(original, formatted)
        assertEquals(MermaidRenderErrorType.UNEXPECTED_EXCEPTION, formatted.renderErrorType)
    }

    @Test
    fun removesOnlyTheVennLocationPrefix() {
        val formatted = assertIs<MermaidError.Parse>(
            MermaidUpstreamErrorFormatter.format(
                diagramId = "venn",
                source = "venn-beta\nset A\nunion A,Missing\n",
                error = MermaidError.Parse(
                    line = 3,
                    column = 9,
                    message = "Parse error on line 3, column 9: " +
                        "unknown set identifier: Missing",
                ),
            ),
        )

        assertEquals("unknown set identifier: Missing", formatted.message)
        assertEquals(3, formatted.line)
        assertEquals(9, formatted.column)
    }
}
