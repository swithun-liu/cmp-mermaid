package com.swithun.cmpmermaid.core.journey.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 journey.jison.
 * Upstream journey.jison SHA-256: 2230b9f958f7c2f2381149b0e484cd203ff919cf43abd065beba7ab6b5a660a5
 * Distribution chunk SHA-256: b2a84793cf6fea9b0c5cf7f680bcbfebf648dadf0535d4fdfd7f636c025e3c0d
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:journey-parser
 */
internal object JourneyJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "journey" to 4,
        "document" to 5,
        "EOF" to 6,
        "line" to 7,
        "SPACE" to 8,
        "statement" to 9,
        "NEWLINE" to 10,
        "title" to 11,
        "acc_title" to 12,
        "acc_title_value" to 13,
        "acc_descr" to 14,
        "acc_descr_value" to 15,
        "acc_descr_multiline_value" to 16,
        "section" to 17,
        "taskName" to 18,
        "taskData" to 19,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "journey",
        6 to "EOF",
        8 to "SPACE",
        10 to "NEWLINE",
        11 to "title",
        12 to "acc_title",
        13 to "acc_title_value",
        14 to "acc_descr",
        15 to "acc_descr_value",
        16 to "acc_descr_multiline_value",
        17 to "section",
        18 to "taskName",
        19 to "taskData",
    )

    val productions: Array<JourneyJisonProduction> = arrayOf(
        JourneyJisonProduction(symbol = 0, length = 0),
        JourneyJisonProduction(symbol = 3, length = 3),
        JourneyJisonProduction(symbol = 5, length = 0),
        JourneyJisonProduction(symbol = 5, length = 2),
        JourneyJisonProduction(symbol = 7, length = 2),
        JourneyJisonProduction(symbol = 7, length = 1),
        JourneyJisonProduction(symbol = 7, length = 1),
        JourneyJisonProduction(symbol = 7, length = 1),
        JourneyJisonProduction(symbol = 9, length = 1),
        JourneyJisonProduction(symbol = 9, length = 2),
        JourneyJisonProduction(symbol = 9, length = 2),
        JourneyJisonProduction(symbol = 9, length = 1),
        JourneyJisonProduction(symbol = 9, length = 1),
        JourneyJisonProduction(symbol = 9, length = 2),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: JourneyJisonCell = JourneyJisonCell.Goto(1)
    private val action1: JourneyJisonCell = JourneyJisonCell.Shift(2)
    private val action2: JourneyJisonCell = JourneyJisonCell.Accept
    private val action3: JourneyJisonCell = JourneyJisonCell.Goto(3)
    private val action4: JourneyJisonCell = JourneyJisonCell.Reduce(2)
    private val action5: JourneyJisonCell = JourneyJisonCell.Shift(4)
    private val action6: JourneyJisonCell = JourneyJisonCell.Goto(5)
    private val action7: JourneyJisonCell = JourneyJisonCell.Shift(6)
    private val action8: JourneyJisonCell = JourneyJisonCell.Goto(7)
    private val action9: JourneyJisonCell = JourneyJisonCell.Shift(8)
    private val action10: JourneyJisonCell = JourneyJisonCell.Shift(9)
    private val action11: JourneyJisonCell = JourneyJisonCell.Shift(10)
    private val action12: JourneyJisonCell = JourneyJisonCell.Shift(11)
    private val action13: JourneyJisonCell = JourneyJisonCell.Shift(12)
    private val action14: JourneyJisonCell = JourneyJisonCell.Shift(13)
    private val action15: JourneyJisonCell = JourneyJisonCell.Shift(14)
    private val action16: JourneyJisonCell = JourneyJisonCell.Reduce(1)
    private val action17: JourneyJisonCell = JourneyJisonCell.Reduce(7)
    private val action18: JourneyJisonCell = JourneyJisonCell.Reduce(3)
    private val action19: JourneyJisonCell = JourneyJisonCell.Goto(15)
    private val action20: JourneyJisonCell = JourneyJisonCell.Reduce(5)
    private val action21: JourneyJisonCell = JourneyJisonCell.Reduce(6)
    private val action22: JourneyJisonCell = JourneyJisonCell.Reduce(8)
    private val action23: JourneyJisonCell = JourneyJisonCell.Shift(16)
    private val action24: JourneyJisonCell = JourneyJisonCell.Shift(17)
    private val action25: JourneyJisonCell = JourneyJisonCell.Reduce(11)
    private val action26: JourneyJisonCell = JourneyJisonCell.Reduce(12)
    private val action27: JourneyJisonCell = JourneyJisonCell.Shift(18)
    private val action28: JourneyJisonCell = JourneyJisonCell.Reduce(4)
    private val action29: JourneyJisonCell = JourneyJisonCell.Reduce(9)
    private val action30: JourneyJisonCell = JourneyJisonCell.Reduce(10)
    private val action31: JourneyJisonCell = JourneyJisonCell.Reduce(13)
    private val symbols0 = intArrayOf(5, 6, 8, 10, 11, 12, 14, 16, 17, 18)
    private val symbols1 = intArrayOf(1, 6, 8, 10, 11, 12, 14, 16, 17, 18)
    private val symbols2 = intArrayOf(6, 8, 10, 11, 12, 14, 16, 17, 18)

    val states: Array<Map<Int, JourneyJisonCell>> = buildList {
        addAll(stateChunk0())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, JourneyJisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
        ),
        mapOf(
            1 to action2,
        ),
        buildJisonState(symbols0, action4, mapOf(
            5 to action3,
        )),
        mapOf(
            6 to action5,
            7 to action6,
            8 to action7,
            9 to action8,
            10 to action9,
            11 to action10,
            12 to action11,
            14 to action12,
            16 to action13,
            17 to action14,
            18 to action15,
        ),
        buildJisonState(symbols1, action17, mapOf(
            1 to action16,
        )),
        buildJisonState(symbols2, action18),
        mapOf(
            9 to action19,
            11 to action10,
            12 to action11,
            14 to action12,
            16 to action13,
            17 to action14,
            18 to action15,
        ),
        buildJisonState(symbols2, action20),
        buildJisonState(symbols2, action21),
        buildJisonState(symbols2, action22),
        mapOf(
            13 to action23,
        ),
        mapOf(
            15 to action24,
        ),
        buildJisonState(symbols2, action25),
        buildJisonState(symbols2, action26),
        mapOf(
            19 to action27,
        ),
        buildJisonState(symbols2, action28),
        buildJisonState(symbols2, action29),
        buildJisonState(symbols2, action30),
        buildJisonState(symbols2, action31),
    )

    val lexerPatterns: List<JourneyJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
    }

    private fun lexerChunk0(): List<JourneyJisonLexerPattern> = listOf(
        JourneyJisonLexerPattern(0, "^(?:%(?!\\{)[^\\n]*)"),
        JourneyJisonLexerPattern(1, "^(?:[^\\}]%%[^\\n]*)"),
        JourneyJisonLexerPattern(2, "^(?:[\\n]+)"),
        JourneyJisonLexerPattern(3, "^(?:\\s+)"),
        JourneyJisonLexerPattern(4, "^(?:#[^\\n]*)"),
        JourneyJisonLexerPattern(5, "^(?:journey\\b)"),
        JourneyJisonLexerPattern(6, "^(?:title\\s[^#\\n;]+)"),
        JourneyJisonLexerPattern(7, "^(?:accTitle\\s*:\\s*)"),
        JourneyJisonLexerPattern(8, "^(?:(?!\\n||)*[^\\n]*)"),
        JourneyJisonLexerPattern(9, "^(?:accDescr\\s*:\\s*)"),
        JourneyJisonLexerPattern(10, "^(?:(?!\\n||)*[^\\n]*)"),
        JourneyJisonLexerPattern(11, "^(?:accDescr\\s*\\{\\s*)"),
        JourneyJisonLexerPattern(12, "^(?:[\\}])"),
        JourneyJisonLexerPattern(13, "^(?:[^\\}]*)"),
        JourneyJisonLexerPattern(14, "^(?:section\\s[^#:\\n;]+)"),
        JourneyJisonLexerPattern(15, "^(?:[^#:\\n;]+)"),
        JourneyJisonLexerPattern(16, "^(?::[^#\\n;]+)"),
        JourneyJisonLexerPattern(17, "^(?::)"),
        JourneyJisonLexerPattern(18, "^(?:\$)"),
        JourneyJisonLexerPattern(19, "^(?:.)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "acc_descr_multiline" to intArrayOf(12, 13),
        "acc_descr" to intArrayOf(10),
        "acc_title" to intArrayOf(8),
        "INITIAL" to intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 19),
    )
}
