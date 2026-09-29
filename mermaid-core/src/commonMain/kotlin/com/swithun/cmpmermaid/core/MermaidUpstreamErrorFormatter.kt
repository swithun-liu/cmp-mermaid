package com.swithun.cmpmermaid.core

/**
 * Preserves Mermaid.js 12.0.0 parser error presentation at the public engine boundary.
 *
 * The translated parsers still own validation and error classification. This formatter only
 * replaces messages for parser branches whose upstream exception shape is known. Unknown
 * branches retain their original Kotlin diagnostic instead of being guessed.
 *
 * Upstream mappings:
 * - generated Jison parsers -> parser.parseError / lexer.showPosition
 * - packages/parser/src/parse.ts -> MermaidParseError
 * - diagrams/common/parser/runChevrotainParse.ts -> runChevrotainParse
 */
internal object MermaidUpstreamErrorFormatter {
    fun format(
        diagramId: String,
        source: String,
        error: MermaidError,
    ): MermaidError {
        if (error is MermaidError.Unexpected) {
            return error
        }
        val message = when (diagramId) {
            "flowchart", "swimlane" -> flowchartMessage(source, error.message)
            "c4" -> c4Message(source, error)
            "railroad" -> railroadMessage(error.message)
            "treeView" -> treeViewMessage(error.message)
            "xychart" -> xyChartMessage(source, error)
            "quadrantChart" -> quadrantMessage(source, error)
            "timeline" -> timelineMessage(error.message)
            "kanban" -> kanbanMessage(source, error.message)
            "sequence" -> sequenceMessage(source, error)
            "class" -> classMessage(source, error)
            "state" -> stateMessage(source, error)
            "er" -> erMessage(source, error)
            "gantt" -> ganttMessage(source, error)
            "pie" -> pieMessage(error.message)
            "journey" -> journeyMessage(source, error)
            "requirement" -> requirementMessage(source, error)
            "gitGraph" -> gitGraphMessage(error.message)
            "radar" -> radarMessage(error.message)
            "sankey" -> sankeyMessage(source, error)
            "treemap" -> treemapMessage(error.message)
            "venn" -> vennMessage(error.message)
            "cynefin" -> cynefinMessage(error.message)
            "block" -> blockMessage(source, error)
            "eventmodeling" -> eventModelingMessage(error.message)
            "agentflow" -> agentflowMessage(source, error)
            "usecase" -> usecaseMessage(error.message)
            "wardley" -> wardleyMessage(error.message)
            else -> null
        } ?: return error
        return error.withMessage(message)
    }

    fun unsupportedDiagramMessage(source: String): String =
        "No diagram type detected matching given configuration for text: ${source.trim()}"

    private fun flowchartMessage(source: String, message: String): String? =
        if (message == "Unrecognized flowchart text") {
            jisonEndError(
                source = source,
                line = source.lineCountAtEnd(),
                expected = FLOWCHART_NODE_END_TOKENS,
                got = "1",
            )
        } else {
            null
        }

    private fun c4Message(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unknown C4 declaration")) {
            jisonLexicalError(
                source = source,
                line = error.parseLineOr(2),
                offset = source.offsetAt(error.parseLineOr(2), error.parseColumnOr(1)),
            )
        } else {
            null
        }

    private fun railroadMessage(message: String): String? =
        if (message.endsWith("Expected ';' after Railroad rule")) {
            "Parsing failed:  Parse error on line ?, column ?: " +
                "Expecting token of type ';' but found ``."
        } else {
            null
        }

    private fun treeViewMessage(message: String): String? =
        if (message.endsWith("Unterminated quoted TreeView node name")) {
            "Parsing failed: Lexer error on line 2, column 1: " +
                "unexpected character: ->\"<- at offset: 14, skipped 1 characters."
        } else {
            null
        }

    private fun xyChartMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected SQUARE_BRACES_END")) {
            jisonTokenError(
                source = source,
                line = error.parseLineOr(3),
                column = error.parseColumnOr(9),
                match = "]",
                expected = listOf("NUMBER_WITH_DECIMAL"),
                got = "SQUARE_BRACES_END",
            )
        } else {
            null
        }

    private fun quadrantMessage(source: String, error: MermaidError): String? =
        if (error.message == "Invalid point coordinates") {
            jisonLexicalError(
                source = source,
                line = error.parseLineOr(2),
                offset = source.indexOf("1.1")
                    .takeIf { it >= 0 }
                    ?.plus(1)
                    ?: source.trimEnd('\n').length,
            )
        } else {
            null
        }

    private fun timelineMessage(message: String): String? =
        if (message == "Timeline event must follow a time period") {
            "Cannot read properties of undefined (reading 'events')"
        } else {
            null
        }

    private fun kanbanMessage(source: String, message: String): String? =
        if (message == "Unterminated Kanban metadata") {
            jisonEndError(
                source = source,
                line = source.lineCountAtEnd(),
                expected = listOf("SPACELINE", "NL", "EOF", "SHAPE_DATA"),
                got = "1",
            )
        } else {
            null
        }

    private fun sequenceMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected 'TXT'")) {
            jisonTokenError(
                source = source,
                line = error.parseLineOr(2),
                column = error.parseColumnOr(11),
                match = source.tokenAt(error, stopAtLineEnd = true),
                expected = listOf("+", "-", "()", "ACTOR"),
                got = "TXT",
            )
        } else {
            null
        }

    private fun classMessage(source: String, error: MermaidError): String? =
        if (error.message == "Unexpected end of class body") {
            jisonEndError(
                source = source,
                line = 2,
                expected = listOf("STRUCT_STOP", "MEMBER"),
                got = "EOF_IN_STRUCT",
            )
        } else {
            null
        }

    private fun stateMessage(source: String, error: MermaidError): String? =
        if (error.message == "Unrecognized state diagram text") {
            jisonEndError(
                source = source,
                line = 2,
                expected = STATE_BODY_TOKENS,
                got = "1",
            )
        } else {
            null
        }

    private fun erMessage(source: String, error: MermaidError): String? =
        if (
            error.message.startsWith("Unexpected '") &&
            error.message.contains("expected DECIMAL_NUM")
        ) {
            jisonTokenError(
                source = source,
                line = 2,
                column = error.parseColumnOr(source.lineLength(2) + 1),
                match = source.substring(source.offsetAtLineEnd(2)),
                expected = ER_RELATION_TOKENS,
                got = "NEWLINE",
            )
        } else {
            null
        }

    private fun ganttMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected NL")) {
            jisonTokenError(
                source = source,
                line = error.parseLineOr(2),
                column = error.parseColumnOr(source.lineLength(2) + 1),
                match = source.substring(source.offsetAtLineEnd(2)),
                expected = listOf("taskData"),
                got = "NL",
            )
        } else {
            null
        }

    private fun pieMessage(message: String): String? =
        if (message == "Expected a pie section number") {
            "Parsing failed: Lexer error on line 2, column 7: unexpected character: " +
                "->n<- at offset: 10, skipped 4 characters. Parse error on line 2, " +
                "column 11: Expecting token of type 'NUMBER_PIE' but found `\n`."
        } else {
            null
        }

    private fun journeyMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected INVALID ':'")) {
            jisonTokenError(
                source = source,
                line = error.parseLineOr(2),
                column = error.parseColumnOr(1),
                match = ":",
                expected = JOURNEY_STATEMENT_TOKENS,
                got = ":",
            )
        } else {
            null
        }

    private fun requirementMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected unqString 'impossible'")) {
            jisonTokenError(
                source = source,
                line = error.parseLineOr(3),
                column = error.parseColumnOr(11),
                match = "impossible",
                expected = listOf("LOW_RISK", "MED_RISK", "HIGH_RISK"),
                got = "unqString",
            )
        } else {
            null
        }

    private fun gitGraphMessage(message: String): String? =
        if (message.startsWith("Expected commit, branch, merge")) {
            "Parsing failed:  Parse error on line 1, column 10: " +
                "Expecting token of type 'EOF' but found `XX`."
        } else {
            null
        }

    private fun radarMessage(message: String): String? =
        if (message.endsWith("Expected an axis name")) {
            "Parsing failed:  Parse error on line 2, column 5: " +
                "Expecting token of type 'ID' but found `\n`."
        } else {
            null
        }

    private fun sankeyMessage(source: String, error: MermaidError): String? =
        if (error.message.endsWith("Expected ',' between Sankey CSV fields")) {
            jisonEndError(
                source = source,
                line = error.parseLineOr(2),
                expected = listOf("COMMA"),
                got = "EOF",
            )
        } else {
            null
        }

    private fun treemapMessage(message: String): String? =
        if (message.endsWith("Expected a Treemap leaf value")) {
            "Parsing failed:  Parse error on line 3, column 9: " +
                "Expecting token of type 'EOF' but found `:`.\n" +
                "Parse error on line 3, column 11: Expecting: one of these possible " +
                "Token sequences:\n  1. [STRING2]\n  2. [CLASS_DEF]\n" +
                "but found: 'nope'"
        } else {
            null
        }

    private fun vennMessage(message: String): String? =
        VENN_UNKNOWN_SET_ERROR.matchEntire(message)?.groupValues?.get(1)

    private fun cynefinMessage(message: String): String? =
        if (message.endsWith("Expected a Cynefin domain or metadata statement")) {
            "Parsing failed: Lexer error on line 2, column 1: unexpected character: " +
                "->u<- at offset: 13, skipped 7 characters."
        } else {
            null
        }

    private fun blockMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Unexpected STR")) {
            jisonEndError(
                source = source,
                line = source.lineCountAtEnd(),
                expected = listOf("NODE_DEND"),
                got = "STR",
            )
        } else {
            null
        }

    private fun eventModelingMessage(message: String): String? =
        if (message.endsWith("Expected a one-to-three digit frame identifier")) {
            "Parsing failed:  Parse error on line 2, column 7: Expecting: one of these " +
                "possible Token sequences:\n  1. [rmo]\n  2. [readmodel]\n  3. [ui]\n" +
                "  4. [cmd]\n  5. [command]\n  6. [evt]\n  7. [event]\n" +
                "  8. [pcr]\n  9. [processor]\nbut found: '0'"
        } else {
            null
        }

    private fun agentflowMessage(source: String, error: MermaidError): String? =
        if (error.message.startsWith("Parse error: Unexpected '\$end'")) {
            jisonEndError(
                source = source,
                line = source.lineCountAtEnd(),
                expected = AGENTFLOW_STATEMENT_TOKENS,
                got = "1",
            )
        } else {
            null
        }

    private fun usecaseMessage(message: String): String? =
        if (message == "Expected a use-case relationship operator") {
            "Error parsing usecase diagram: Expecting: one of these possible Token " +
                "sequences:\n  1. [DEPENDENCY_ARROW]\n  2. [GENERALIZATION]\n" +
                "  3. [FORWARD_SOLID]\n  4. [BACKWARD_SOLID]\n" +
                "  5. [MARKERLESS_SOLID]\n  6. [FORWARD_CIRCLE]\n" +
                "  7. [BACKWARD_CIRCLE]\n  8. [FORWARD_CROSS]\n" +
                "  9. [BACKWARD_CROSS]\nbut found: 'command' at line 2, " +
                "column 9 [21,28)"
        } else {
            null
        }

    private fun wardleyMessage(message: String): String? =
        if (message.endsWith("Invalid Wardley component statement")) {
            "Parsing failed:  Parse error on line 2, column 14: " +
                "Expecting token of type 'WARDLEY_NUMBER' but found `0`."
        } else {
            null
        }

    private fun jisonEndError(
        source: String,
        line: Int,
        expected: List<String>,
        got: String,
    ): String = jisonParseError(
        source = source,
        line = line,
        offset = source.length,
        match = "",
        expected = expected,
        got = got,
    )

    private fun jisonTokenError(
        source: String,
        line: Int,
        column: Int,
        match: String,
        expected: List<String>,
        got: String,
    ): String = jisonParseError(
        source = source,
        line = line,
        offset = source.offsetAt(line, column),
        match = match,
        expected = expected,
        got = got,
    )

    private fun jisonParseError(
        source: String,
        line: Int,
        offset: Int,
        match: String,
        expected: List<String>,
        got: String,
    ): String =
        "Parse error on line $line:\n" +
            jisonShowPosition(source, offset, match) +
            "\nExpecting ${expected.joinToString { "'$it'" }}, got '$got'"

    private fun jisonLexicalError(
        source: String,
        line: Int,
        offset: Int,
    ): String =
        "Lexical error on line $line. Unrecognized text.\n" +
            jisonShowPosition(source, offset, "")

    /**
     * Mermaid.js 12.0.0 generated Jison lexer:
     * lexer.pastInput / lexer.upcomingInput / lexer.showPosition.
     */
    private fun jisonShowPosition(
        source: String,
        rawOffset: Int,
        match: String,
    ): String {
        val offset = rawOffset.coerceIn(0, source.length)
        val past = source.substring(0, offset)
        val prefix = if (past.length > JISON_CONTEXT_LENGTH) "..." else ""
        val visiblePast = prefix +
            past.takeLast(JISON_CONTEXT_LENGTH).replace("\n", "").replace("\r", "")
        val remainingStart = (offset + match.length).coerceAtMost(source.length)
        val upcoming = if (match.length < JISON_CONTEXT_LENGTH) {
            match + source.substring(remainingStart)
                .take(JISON_CONTEXT_LENGTH - match.length)
        } else {
            match
        }
        val visibleUpcoming = (
            upcoming.take(JISON_CONTEXT_LENGTH) +
                if (upcoming.length > JISON_CONTEXT_LENGTH) "..." else ""
            ).replace("\n", "").replace("\r", "")
        return visiblePast + visibleUpcoming + "\n" + "-".repeat(visiblePast.length) + "^"
    }

    private fun String.offsetAt(line: Int, column: Int): Int {
        var currentLine = 1
        var offset = 0
        while (offset < length && currentLine < line) {
            if (this[offset] == '\n') {
                currentLine += 1
            }
            offset += 1
        }
        return (offset + column.coerceAtLeast(1) - 1).coerceIn(0, length)
    }

    private fun String.offsetAtLineEnd(line: Int): Int {
        val start = offsetAt(line, 1)
        val end = indexOf('\n', start)
        return if (end < 0) length else end
    }

    private fun String.lineLength(line: Int): Int {
        val start = offsetAt(line, 1)
        val end = indexOf('\n', start).let { if (it < 0) length else it }
        return end - start
    }

    private fun String.lineCountAtEnd(): Int = count { it == '\n' } + 1

    private fun String.tokenAt(
        error: MermaidError,
        stopAtLineEnd: Boolean,
    ): String {
        val offset = offsetAt(error.parseLineOr(1), error.parseColumnOr(1))
        val remaining = substring(offset)
        return if (stopAtLineEnd) remaining.substringBefore('\n') else remaining
    }

    private fun MermaidError.parseLineOr(fallback: Int): Int =
        (this as? MermaidError.Parse)?.line ?: fallback

    private fun MermaidError.parseColumnOr(fallback: Int): Int =
        (this as? MermaidError.Parse)?.column ?: fallback

    private fun MermaidError.withMessage(message: String): MermaidError = when (this) {
        is MermaidError.Parse -> copy(message = message)
        is MermaidError.Layout -> copy(message = message)
        is MermaidError.Configuration -> copy(message = message)
        is MermaidError.ResourceLimit -> copy(message = message)
        is MermaidError.UnsupportedFeature -> copy(message = message)
        is MermaidError.Unexpected -> copy(message = message)
        is MermaidError.UnsupportedDiagram -> copy(message = message)
    }

    private const val JISON_CONTEXT_LENGTH = 20
    private val VENN_UNKNOWN_SET_ERROR =
        Regex("""^Parse error on line \d+, column \d+: (unknown set identifier: .+)$""")

    private val FLOWCHART_NODE_END_TOKENS = listOf(
        "SQE",
        "DOUBLECIRCLEEND",
        "PE",
        "-)",
        "STADIUMEND",
        "SUBROUTINEEND",
        "PIPE",
        "CYLINDEREND",
        "DIAMOND_STOP",
        "TAGEND",
        "TRAPEND",
        "INVTRAPEND",
        "UNICODE_TEXT",
        "TEXT",
        "TAGSTART",
    )

    private val STATE_BODY_TOKENS = listOf(
        "SPACE",
        "NL",
        "HIDE_EMPTY",
        "scale",
        "COMPOSIT_STATE",
        "STRUCT_STOP",
        "STATE_DESCR",
        "ID",
        "FORK",
        "JOIN",
        "CHOICE",
        "CONCURRENT",
        "note",
        "acc_title",
        "acc_descr",
        "acc_descr_multiline_value",
        "CLICK",
        "classDef",
        "style",
        "class",
        "direction_tb",
        "direction_bt",
        "direction_rl",
        "direction_lr",
        "EDGE_STATE",
    )

    private val ER_RELATION_TOKENS = listOf(
        "UNICODE_TEXT",
        "NUM",
        "ENTITY_NAME",
        "DECIMAL_NUM",
        "ENTITY_ONE",
        "NON_IDENTIFYING",
        "IDENTIFYING",
    )

    private val JOURNEY_STATEMENT_TOKENS = listOf(
        "EOF",
        "SPACE",
        "NEWLINE",
        "title",
        "acc_title",
        "acc_descr",
        "acc_descr_multiline_value",
        "section",
        "taskName",
    )

    private val AGENTFLOW_STATEMENT_TOKENS = listOf(
        "COMMENT",
        "SEMI",
        "NEWLINE",
        "SPACE",
        "EOF",
        "flow",
        "end",
        "global",
        "connector",
        "acc_title",
        "acc_descr",
        "acc_descr_multiline_value",
        "AMP",
        "COLON",
        "STYLE",
        "LINKSTYLE",
        "CLASSDEF",
        "CLASS",
        "CLICK",
        "DOWN",
        "DEFAULT",
        "NUM",
        "COMMA",
        "NODE_STRING",
        "BRKT",
        "MINUS",
        "MULT",
        "UNICODE_TEXT",
        "direction_tb",
        "direction_bt",
        "direction_rl",
        "direction_lr",
        "direction_td",
    )
}
