package com.swithun.cmpmermaid.core.mindmap.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 mindmap.jison.
 * Upstream mindmap.jison SHA-256: 1114fbccc641f2ad59a56eec452e21dfb947d78283adf571a6ce24b09dce6993
 * Distribution chunk SHA-256: b2a2870e2bfcddeae16ee210d69b6cef003afbf6c2542d868e80daae73532510
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:mindmap-parser
 */
internal object MindmapJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "mindMap" to 4,
        "spaceLines" to 5,
        "SPACELINE" to 6,
        "NL" to 7,
        "MINDMAP" to 8,
        "document" to 9,
        "stop" to 10,
        "EOF" to 11,
        "statement" to 12,
        "SPACELIST" to 13,
        "node" to 14,
        "ICON" to 15,
        "CLASS" to 16,
        "nodeWithId" to 17,
        "nodeWithoutId" to 18,
        "NODE_DSTART" to 19,
        "NODE_DESCR" to 20,
        "NODE_DEND" to 21,
        "NODE_ID" to 22,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        6 to "SPACELINE",
        7 to "NL",
        8 to "MINDMAP",
        11 to "EOF",
        13 to "SPACELIST",
        15 to "ICON",
        16 to "CLASS",
        19 to "NODE_DSTART",
        20 to "NODE_DESCR",
        21 to "NODE_DEND",
        22 to "NODE_ID",
    )

    val productions: Array<MindmapJisonProduction> = arrayOf(
        MindmapJisonProduction(symbol = 0, length = 0),
        MindmapJisonProduction(symbol = 3, length = 1),
        MindmapJisonProduction(symbol = 3, length = 2),
        MindmapJisonProduction(symbol = 5, length = 1),
        MindmapJisonProduction(symbol = 5, length = 2),
        MindmapJisonProduction(symbol = 5, length = 2),
        MindmapJisonProduction(symbol = 4, length = 2),
        MindmapJisonProduction(symbol = 4, length = 3),
        MindmapJisonProduction(symbol = 10, length = 1),
        MindmapJisonProduction(symbol = 10, length = 1),
        MindmapJisonProduction(symbol = 10, length = 1),
        MindmapJisonProduction(symbol = 10, length = 2),
        MindmapJisonProduction(symbol = 10, length = 2),
        MindmapJisonProduction(symbol = 9, length = 3),
        MindmapJisonProduction(symbol = 9, length = 2),
        MindmapJisonProduction(symbol = 12, length = 2),
        MindmapJisonProduction(symbol = 12, length = 2),
        MindmapJisonProduction(symbol = 12, length = 2),
        MindmapJisonProduction(symbol = 12, length = 1),
        MindmapJisonProduction(symbol = 12, length = 1),
        MindmapJisonProduction(symbol = 12, length = 1),
        MindmapJisonProduction(symbol = 12, length = 1),
        MindmapJisonProduction(symbol = 12, length = 1),
        MindmapJisonProduction(symbol = 14, length = 1),
        MindmapJisonProduction(symbol = 14, length = 1),
        MindmapJisonProduction(symbol = 18, length = 3),
        MindmapJisonProduction(symbol = 17, length = 1),
        MindmapJisonProduction(symbol = 17, length = 4),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: MindmapJisonCell = MindmapJisonCell.Goto(1)
    private val action1: MindmapJisonCell = MindmapJisonCell.Goto(2)
    private val action2: MindmapJisonCell = MindmapJisonCell.Goto(3)
    private val action3: MindmapJisonCell = MindmapJisonCell.Shift(5)
    private val action4: MindmapJisonCell = MindmapJisonCell.Shift(4)
    private val action5: MindmapJisonCell = MindmapJisonCell.Accept
    private val action6: MindmapJisonCell = MindmapJisonCell.Reduce(1)
    private val action7: MindmapJisonCell = MindmapJisonCell.Goto(6)
    private val action8: MindmapJisonCell = MindmapJisonCell.Shift(7)
    private val action9: MindmapJisonCell = MindmapJisonCell.Shift(8)
    private val action10: MindmapJisonCell = MindmapJisonCell.Shift(13)
    private val action11: MindmapJisonCell = MindmapJisonCell.Shift(10)
    private val action12: MindmapJisonCell = MindmapJisonCell.Goto(9)
    private val action13: MindmapJisonCell = MindmapJisonCell.Goto(11)
    private val action14: MindmapJisonCell = MindmapJisonCell.Shift(12)
    private val action15: MindmapJisonCell = MindmapJisonCell.Goto(14)
    private val action16: MindmapJisonCell = MindmapJisonCell.Shift(15)
    private val action17: MindmapJisonCell = MindmapJisonCell.Shift(16)
    private val action18: MindmapJisonCell = MindmapJisonCell.Goto(17)
    private val action19: MindmapJisonCell = MindmapJisonCell.Goto(18)
    private val action20: MindmapJisonCell = MindmapJisonCell.Shift(20)
    private val action21: MindmapJisonCell = MindmapJisonCell.Shift(19)
    private val action22: MindmapJisonCell = MindmapJisonCell.Reduce(3)
    private val action23: MindmapJisonCell = MindmapJisonCell.Reduce(2)
    private val action24: MindmapJisonCell = MindmapJisonCell.Reduce(4)
    private val action25: MindmapJisonCell = MindmapJisonCell.Reduce(5)
    private val action26: MindmapJisonCell = MindmapJisonCell.Reduce(6)
    private val action27: MindmapJisonCell = MindmapJisonCell.Goto(21)
    private val action28: MindmapJisonCell = MindmapJisonCell.Goto(22)
    private val action29: MindmapJisonCell = MindmapJisonCell.Shift(26)
    private val action30: MindmapJisonCell = MindmapJisonCell.Shift(24)
    private val action31: MindmapJisonCell = MindmapJisonCell.Goto(23)
    private val action32: MindmapJisonCell = MindmapJisonCell.Shift(25)
    private val action33: MindmapJisonCell = MindmapJisonCell.Reduce(22)
    private val action34: MindmapJisonCell = MindmapJisonCell.Goto(27)
    private val action35: MindmapJisonCell = MindmapJisonCell.Shift(28)
    private val action36: MindmapJisonCell = MindmapJisonCell.Shift(29)
    private val action37: MindmapJisonCell = MindmapJisonCell.Reduce(18)
    private val action38: MindmapJisonCell = MindmapJisonCell.Reduce(19)
    private val action39: MindmapJisonCell = MindmapJisonCell.Reduce(20)
    private val action40: MindmapJisonCell = MindmapJisonCell.Reduce(21)
    private val action41: MindmapJisonCell = MindmapJisonCell.Reduce(23)
    private val action42: MindmapJisonCell = MindmapJisonCell.Reduce(24)
    private val action43: MindmapJisonCell = MindmapJisonCell.Reduce(26)
    private val action44: MindmapJisonCell = MindmapJisonCell.Shift(30)
    private val action45: MindmapJisonCell = MindmapJisonCell.Shift(31)
    private val action46: MindmapJisonCell = MindmapJisonCell.Goto(32)
    private val action47: MindmapJisonCell = MindmapJisonCell.Reduce(7)
    private val action48: MindmapJisonCell = MindmapJisonCell.Reduce(14)
    private val action49: MindmapJisonCell = MindmapJisonCell.Shift(33)
    private val action50: MindmapJisonCell = MindmapJisonCell.Shift(34)
    private val action51: MindmapJisonCell = MindmapJisonCell.Reduce(8)
    private val action52: MindmapJisonCell = MindmapJisonCell.Reduce(9)
    private val action53: MindmapJisonCell = MindmapJisonCell.Reduce(10)
    private val action54: MindmapJisonCell = MindmapJisonCell.Reduce(15)
    private val action55: MindmapJisonCell = MindmapJisonCell.Reduce(16)
    private val action56: MindmapJisonCell = MindmapJisonCell.Reduce(17)
    private val action57: MindmapJisonCell = MindmapJisonCell.Shift(35)
    private val action58: MindmapJisonCell = MindmapJisonCell.Shift(36)
    private val action59: MindmapJisonCell = MindmapJisonCell.Reduce(13)
    private val action60: MindmapJisonCell = MindmapJisonCell.Reduce(11)
    private val action61: MindmapJisonCell = MindmapJisonCell.Reduce(12)
    private val action62: MindmapJisonCell = MindmapJisonCell.Shift(37)
    private val action63: MindmapJisonCell = MindmapJisonCell.Reduce(25)
    private val action64: MindmapJisonCell = MindmapJisonCell.Reduce(27)
    private val symbols0 = intArrayOf(6, 7, 8)
    private val symbols1 = intArrayOf(6, 7, 11, 14, 15, 16, 17, 18, 19, 22)
    private val symbols2 = intArrayOf(6, 7, 11)
    private val symbols3 = intArrayOf(6, 7, 11, 19)
    private val symbols4 = intArrayOf(1, 6, 7, 11, 13, 15, 16, 19, 22)

    val states: Array<Map<Int, MindmapJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, MindmapJisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            5 to action2,
            6 to action3,
            8 to action4,
        ),
        mapOf(
            1 to action5,
        ),
        mapOf(
            1 to action6,
        ),
        mapOf(
            4 to action7,
            6 to action8,
            7 to action9,
            8 to action4,
        ),
        mapOf(
            6 to action10,
            7 to action11,
            9 to action12,
            12 to action13,
            13 to action14,
            14 to action15,
            15 to action16,
            16 to action17,
            17 to action18,
            18 to action19,
            19 to action20,
            22 to action21,
        ),
        buildJisonState(symbols0, action22),
        mapOf(
            1 to action23,
        ),
        buildJisonState(symbols0, action24),
        buildJisonState(symbols0, action25),
        mapOf(
            1 to action26,
            6 to action10,
            12 to action27,
            13 to action14,
            14 to action15,
            15 to action16,
            16 to action17,
            17 to action18,
            18 to action19,
            19 to action20,
            22 to action21,
        ),
        mapOf(
            6 to action10,
            9 to action28,
            12 to action13,
            13 to action14,
            14 to action15,
            15 to action16,
            16 to action17,
            17 to action18,
            18 to action19,
            19 to action20,
            22 to action21,
        ),
        mapOf(
            6 to action29,
            7 to action30,
            10 to action31,
            11 to action32,
        ),
        buildJisonState(symbols1, action33, mapOf(
            14 to action34,
            15 to action35,
            16 to action36,
            17 to action18,
            18 to action19,
            19 to action20,
            22 to action21,
        )),
        buildJisonState(symbols2, action37),
        buildJisonState(symbols2, action38),
        buildJisonState(symbols2, action39),
        buildJisonState(symbols2, action40),
        buildJisonState(symbols2, action41),
        buildJisonState(symbols2, action42),
        buildJisonState(symbols3, action43, mapOf(
            19 to action44,
        )),
    )

    private fun stateChunk1(): Array<Map<Int, MindmapJisonCell>> = arrayOf(
        mapOf(
            20 to action45,
        ),
        mapOf(
            6 to action29,
            7 to action30,
            10 to action46,
            11 to action32,
        ),
        mapOf(
            1 to action47,
            6 to action10,
            12 to action27,
            13 to action14,
            14 to action15,
            15 to action16,
            16 to action17,
            17 to action18,
            18 to action19,
            19 to action20,
            22 to action21,
        ),
        buildJisonState(symbols4, action48, mapOf(
            7 to action49,
            11 to action50,
        )),
        buildJisonState(symbols4, action51),
        buildJisonState(symbols4, action52),
        buildJisonState(symbols4, action53),
        buildJisonState(symbols2, action54),
        buildJisonState(symbols2, action55),
        buildJisonState(symbols2, action56),
        mapOf(
            20 to action57,
        ),
        mapOf(
            21 to action58,
        ),
        buildJisonState(symbols4, action59, mapOf(
            7 to action49,
            11 to action50,
        )),
        buildJisonState(symbols4, action60),
        buildJisonState(symbols4, action61),
        mapOf(
            21 to action62,
        ),
        buildJisonState(symbols2, action63),
        buildJisonState(symbols2, action64),
    )

    val lexerPatterns: List<MindmapJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
    }

    private fun lexerChunk0(): List<MindmapJisonLexerPattern> = listOf(
        MindmapJisonLexerPattern(0, "^(?:\\s*%%.*)"),
        MindmapJisonLexerPattern(1, "^(?:mindmap\\b)"),
        MindmapJisonLexerPattern(2, "^(?::::)"),
        MindmapJisonLexerPattern(3, "^(?:.+)"),
        MindmapJisonLexerPattern(4, "^(?:\\n)"),
        MindmapJisonLexerPattern(5, "^(?:::icon\\()"),
        MindmapJisonLexerPattern(6, "^(?:[\\s]+[\\n])"),
        MindmapJisonLexerPattern(7, "^(?:[\\n]+)"),
        MindmapJisonLexerPattern(8, "^(?:[^\\)]+)"),
        MindmapJisonLexerPattern(9, "^(?:\\))"),
        MindmapJisonLexerPattern(10, "^(?:-\\))"),
        MindmapJisonLexerPattern(11, "^(?:\\(-)"),
        MindmapJisonLexerPattern(12, "^(?:\\)\\))"),
        MindmapJisonLexerPattern(13, "^(?:\\))"),
        MindmapJisonLexerPattern(14, "^(?:\\(\\()"),
        MindmapJisonLexerPattern(15, "^(?:\\{\\{)"),
        MindmapJisonLexerPattern(16, "^(?:\\()"),
        MindmapJisonLexerPattern(17, "^(?:\\[)"),
        MindmapJisonLexerPattern(18, "^(?:[\\s]+)"),
        MindmapJisonLexerPattern(19, "^(?:[^\\(\\[\\n\\)\\{\\}]+)"),
    )

    private fun lexerChunk1(): List<MindmapJisonLexerPattern> = listOf(
        MindmapJisonLexerPattern(20, "^(?:\$)"),
        MindmapJisonLexerPattern(21, "^(?:[\"][`])"),
        MindmapJisonLexerPattern(22, "^(?:[^`\"]+)"),
        MindmapJisonLexerPattern(23, "^(?:[`][\"])"),
        MindmapJisonLexerPattern(24, "^(?:[\"])"),
        MindmapJisonLexerPattern(25, "^(?:[^\"]+)"),
        MindmapJisonLexerPattern(26, "^(?:[\"])"),
        MindmapJisonLexerPattern(27, "^(?:[\\)]\\))"),
        MindmapJisonLexerPattern(28, "^(?:[\\)])"),
        MindmapJisonLexerPattern(29, "^(?:[\\]])"),
        MindmapJisonLexerPattern(30, "^(?:\\}\\})"),
        MindmapJisonLexerPattern(31, "^(?:\\(-)"),
        MindmapJisonLexerPattern(32, "^(?:-\\))"),
        MindmapJisonLexerPattern(33, "^(?:\\(\\()"),
        MindmapJisonLexerPattern(34, "^(?:\\()"),
        MindmapJisonLexerPattern(35, "^(?:[^\\)\\]\\(\\}]+)"),
        MindmapJisonLexerPattern(36, "^(?:.+(?!\\(\\())"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "CLASS" to intArrayOf(3, 4),
        "ICON" to intArrayOf(8, 9),
        "NSTR2" to intArrayOf(22, 23),
        "NSTR" to intArrayOf(25, 26),
        "NODE" to intArrayOf(21, 24, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36),
        "INITIAL" to intArrayOf(0, 1, 2, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
    )
}
