package com.swithun.cmpmermaid.core.block.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 block.jison.
 * Upstream block.jison SHA-256: 323acca843736b66334fa088b97519ef96c22aa5a1a1eed4f468379ebecf09f2
 * Distribution chunk SHA-256: f066a97c89f294242141de2fb3c09fbc9960ae7f60cacf1c67e2b58e39b99a82
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:block-parser
 */
internal object BlockJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "spaceLines" to 3,
        "SPACELINE" to 4,
        "NL" to 5,
        "separator" to 6,
        "SPACE" to 7,
        "EOF" to 8,
        "start" to 9,
        "BLOCK_DIAGRAM_KEY" to 10,
        "document" to 11,
        "stop" to 12,
        "statement" to 13,
        "link" to 14,
        "LINK" to 15,
        "START_LINK" to 16,
        "LINK_LABEL" to 17,
        "STR" to 18,
        "nodeStatement" to 19,
        "columnsStatement" to 20,
        "SPACE_BLOCK" to 21,
        "blockStatement" to 22,
        "classDefStatement" to 23,
        "cssClassStatement" to 24,
        "styleStatement" to 25,
        "node" to 26,
        "SIZE" to 27,
        "COLUMNS" to 28,
        "id-block" to 29,
        "end" to 30,
        "NODE_ID" to 31,
        "nodeShapeNLabel" to 32,
        "dirList" to 33,
        "DIR" to 34,
        "NODE_DSTART" to 35,
        "NODE_DEND" to 36,
        "BLOCK_ARROW_START" to 37,
        "BLOCK_ARROW_END" to 38,
        "classDef" to 39,
        "CLASSDEF_ID" to 40,
        "CLASSDEF_STYLEOPTS" to 41,
        "DEFAULT" to 42,
        "class" to 43,
        "CLASSENTITY_IDS" to 44,
        "STYLECLASS" to 45,
        "style" to 46,
        "STYLE_ENTITY_IDS" to 47,
        "STYLE_DEFINITION_DATA" to 48,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "SPACELINE",
        5 to "NL",
        7 to "SPACE",
        8 to "EOF",
        10 to "BLOCK_DIAGRAM_KEY",
        15 to "LINK",
        16 to "START_LINK",
        17 to "LINK_LABEL",
        18 to "STR",
        21 to "SPACE_BLOCK",
        27 to "SIZE",
        28 to "COLUMNS",
        29 to "id-block",
        30 to "end",
        31 to "NODE_ID",
        34 to "DIR",
        35 to "NODE_DSTART",
        36 to "NODE_DEND",
        37 to "BLOCK_ARROW_START",
        38 to "BLOCK_ARROW_END",
        39 to "classDef",
        40 to "CLASSDEF_ID",
        41 to "CLASSDEF_STYLEOPTS",
        42 to "DEFAULT",
        43 to "class",
        44 to "CLASSENTITY_IDS",
        45 to "STYLECLASS",
        46 to "style",
        47 to "STYLE_ENTITY_IDS",
        48 to "STYLE_DEFINITION_DATA",
    )

    val productions: Array<BlockJisonProduction> = arrayOf(
        BlockJisonProduction(symbol = 0, length = 0),
        BlockJisonProduction(symbol = 3, length = 1),
        BlockJisonProduction(symbol = 3, length = 2),
        BlockJisonProduction(symbol = 3, length = 2),
        BlockJisonProduction(symbol = 6, length = 1),
        BlockJisonProduction(symbol = 6, length = 1),
        BlockJisonProduction(symbol = 6, length = 1),
        BlockJisonProduction(symbol = 9, length = 3),
        BlockJisonProduction(symbol = 12, length = 1),
        BlockJisonProduction(symbol = 12, length = 1),
        BlockJisonProduction(symbol = 12, length = 2),
        BlockJisonProduction(symbol = 12, length = 2),
        BlockJisonProduction(symbol = 11, length = 1),
        BlockJisonProduction(symbol = 11, length = 2),
        BlockJisonProduction(symbol = 14, length = 1),
        BlockJisonProduction(symbol = 14, length = 4),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 13, length = 1),
        BlockJisonProduction(symbol = 19, length = 3),
        BlockJisonProduction(symbol = 19, length = 2),
        BlockJisonProduction(symbol = 19, length = 1),
        BlockJisonProduction(symbol = 20, length = 1),
        BlockJisonProduction(symbol = 22, length = 4),
        BlockJisonProduction(symbol = 22, length = 3),
        BlockJisonProduction(symbol = 26, length = 1),
        BlockJisonProduction(symbol = 26, length = 2),
        BlockJisonProduction(symbol = 33, length = 1),
        BlockJisonProduction(symbol = 33, length = 2),
        BlockJisonProduction(symbol = 32, length = 3),
        BlockJisonProduction(symbol = 32, length = 4),
        BlockJisonProduction(symbol = 23, length = 3),
        BlockJisonProduction(symbol = 23, length = 3),
        BlockJisonProduction(symbol = 24, length = 3),
        BlockJisonProduction(symbol = 25, length = 3),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: BlockJisonCell = BlockJisonCell.Goto(1)
    private val action1: BlockJisonCell = BlockJisonCell.Shift(2)
    private val action2: BlockJisonCell = BlockJisonCell.Accept
    private val action3: BlockJisonCell = BlockJisonCell.Shift(15)
    private val action4: BlockJisonCell = BlockJisonCell.Goto(3)
    private val action5: BlockJisonCell = BlockJisonCell.Goto(4)
    private val action6: BlockJisonCell = BlockJisonCell.Goto(5)
    private val action7: BlockJisonCell = BlockJisonCell.Goto(6)
    private val action8: BlockJisonCell = BlockJisonCell.Shift(7)
    private val action9: BlockJisonCell = BlockJisonCell.Goto(8)
    private val action10: BlockJisonCell = BlockJisonCell.Goto(9)
    private val action11: BlockJisonCell = BlockJisonCell.Goto(10)
    private val action12: BlockJisonCell = BlockJisonCell.Goto(11)
    private val action13: BlockJisonCell = BlockJisonCell.Goto(12)
    private val action14: BlockJisonCell = BlockJisonCell.Shift(13)
    private val action15: BlockJisonCell = BlockJisonCell.Shift(14)
    private val action16: BlockJisonCell = BlockJisonCell.Shift(19)
    private val action17: BlockJisonCell = BlockJisonCell.Shift(16)
    private val action18: BlockJisonCell = BlockJisonCell.Shift(17)
    private val action19: BlockJisonCell = BlockJisonCell.Shift(18)
    private val action20: BlockJisonCell = BlockJisonCell.Shift(20)
    private val action21: BlockJisonCell = BlockJisonCell.Reduce(12)
    private val action22: BlockJisonCell = BlockJisonCell.Goto(21)
    private val action23: BlockJisonCell = BlockJisonCell.Reduce(16)
    private val action24: BlockJisonCell = BlockJisonCell.Goto(22)
    private val action25: BlockJisonCell = BlockJisonCell.Shift(23)
    private val action26: BlockJisonCell = BlockJisonCell.Shift(24)
    private val action27: BlockJisonCell = BlockJisonCell.Reduce(17)
    private val action28: BlockJisonCell = BlockJisonCell.Reduce(18)
    private val action29: BlockJisonCell = BlockJisonCell.Reduce(19)
    private val action30: BlockJisonCell = BlockJisonCell.Reduce(20)
    private val action31: BlockJisonCell = BlockJisonCell.Reduce(21)
    private val action32: BlockJisonCell = BlockJisonCell.Reduce(22)
    private val action33: BlockJisonCell = BlockJisonCell.Reduce(25)
    private val action34: BlockJisonCell = BlockJisonCell.Shift(25)
    private val action35: BlockJisonCell = BlockJisonCell.Reduce(26)
    private val action36: BlockJisonCell = BlockJisonCell.Goto(26)
    private val action37: BlockJisonCell = BlockJisonCell.Goto(27)
    private val action38: BlockJisonCell = BlockJisonCell.Shift(28)
    private val action39: BlockJisonCell = BlockJisonCell.Shift(29)
    private val action40: BlockJisonCell = BlockJisonCell.Shift(30)
    private val action41: BlockJisonCell = BlockJisonCell.Shift(31)
    private val action42: BlockJisonCell = BlockJisonCell.Reduce(29)
    private val action43: BlockJisonCell = BlockJisonCell.Goto(32)
    private val action44: BlockJisonCell = BlockJisonCell.Shift(33)
    private val action45: BlockJisonCell = BlockJisonCell.Shift(34)
    private val action46: BlockJisonCell = BlockJisonCell.Reduce(7)
    private val action47: BlockJisonCell = BlockJisonCell.Reduce(13)
    private val action48: BlockJisonCell = BlockJisonCell.Goto(35)
    private val action49: BlockJisonCell = BlockJisonCell.Reduce(14)
    private val action50: BlockJisonCell = BlockJisonCell.Shift(36)
    private val action51: BlockJisonCell = BlockJisonCell.Reduce(24)
    private val action52: BlockJisonCell = BlockJisonCell.Goto(37)
    private val action53: BlockJisonCell = BlockJisonCell.Shift(38)
    private val action54: BlockJisonCell = BlockJisonCell.Shift(39)
    private val action55: BlockJisonCell = BlockJisonCell.Shift(40)
    private val action56: BlockJisonCell = BlockJisonCell.Shift(41)
    private val action57: BlockJisonCell = BlockJisonCell.Shift(42)
    private val action58: BlockJisonCell = BlockJisonCell.Reduce(30)
    private val action59: BlockJisonCell = BlockJisonCell.Shift(43)
    private val action60: BlockJisonCell = BlockJisonCell.Shift(44)
    private val action61: BlockJisonCell = BlockJisonCell.Reduce(23)
    private val action62: BlockJisonCell = BlockJisonCell.Shift(45)
    private val action63: BlockJisonCell = BlockJisonCell.Shift(46)
    private val action64: BlockJisonCell = BlockJisonCell.Reduce(28)
    private val action65: BlockJisonCell = BlockJisonCell.Reduce(35)
    private val action66: BlockJisonCell = BlockJisonCell.Reduce(36)
    private val action67: BlockJisonCell = BlockJisonCell.Reduce(37)
    private val action68: BlockJisonCell = BlockJisonCell.Reduce(38)
    private val action69: BlockJisonCell = BlockJisonCell.Shift(47)
    private val action70: BlockJisonCell = BlockJisonCell.Goto(48)
    private val action71: BlockJisonCell = BlockJisonCell.Shift(49)
    private val action72: BlockJisonCell = BlockJisonCell.Shift(50)
    private val action73: BlockJisonCell = BlockJisonCell.Reduce(27)
    private val action74: BlockJisonCell = BlockJisonCell.Reduce(33)
    private val action75: BlockJisonCell = BlockJisonCell.Shift(51)
    private val action76: BlockJisonCell = BlockJisonCell.Goto(52)
    private val action77: BlockJisonCell = BlockJisonCell.Reduce(31)
    private val action78: BlockJisonCell = BlockJisonCell.Reduce(15)
    private val action79: BlockJisonCell = BlockJisonCell.Reduce(34)
    private val action80: BlockJisonCell = BlockJisonCell.Reduce(32)
    private val symbols0 = intArrayOf(8, 10, 14, 15, 16, 21, 28, 29, 30, 31, 39, 43, 46)
    private val symbols1 = intArrayOf(8, 10, 21, 28, 29, 30, 31, 39, 43, 46)
    private val symbols2 = intArrayOf(8, 10, 15, 16, 21, 27, 28, 29, 30, 31, 39, 43, 46)
    private val symbols3 = intArrayOf(8, 10, 15, 16, 21, 27, 28, 29, 30, 31, 32, 35, 37, 39, 43, 46)
    private val symbols4 = intArrayOf(8, 10, 15, 16, 21, 28, 29, 30, 31, 39, 43, 46)

    val states: Array<Map<Int, BlockJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, BlockJisonCell>> = arrayOf(
        mapOf(
            9 to action0,
            10 to action1,
        ),
        mapOf(
            1 to action2,
        ),
        mapOf(
            10 to action3,
            11 to action4,
            13 to action5,
            19 to action6,
            20 to action7,
            21 to action8,
            22 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            28 to action14,
            29 to action15,
            31 to action16,
            39 to action17,
            43 to action18,
            46 to action19,
        ),
        mapOf(
            8 to action20,
        ),
        mapOf(
            8 to action21,
            10 to action3,
            11 to action22,
            13 to action5,
            19 to action6,
            20 to action7,
            21 to action8,
            22 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            28 to action14,
            29 to action15,
            30 to action21,
            31 to action16,
            39 to action17,
            43 to action18,
            46 to action19,
        ),
        buildJisonState(symbols0, action23, mapOf(
            14 to action24,
            15 to action25,
            16 to action26,
        )),
        buildJisonState(symbols1, action27),
        buildJisonState(symbols1, action28),
        buildJisonState(symbols1, action29),
        buildJisonState(symbols1, action30),
        buildJisonState(symbols1, action31),
        buildJisonState(symbols1, action32),
        buildJisonState(symbols2, action33, mapOf(
            27 to action34,
        )),
        buildJisonState(symbols1, action35),
        mapOf(
            19 to action36,
            26 to action13,
            31 to action16,
        ),
        mapOf(
            10 to action3,
            11 to action37,
            13 to action5,
            19 to action6,
            20 to action7,
            21 to action8,
            22 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            28 to action14,
            29 to action15,
            31 to action16,
            39 to action17,
            43 to action18,
            46 to action19,
        ),
        mapOf(
            40 to action38,
            42 to action39,
        ),
        mapOf(
            44 to action40,
        ),
        mapOf(
            47 to action41,
        ),
        buildJisonState(symbols3, action42, mapOf(
            32 to action43,
            35 to action44,
            37 to action45,
        )),
    )

    private fun stateChunk1(): Array<Map<Int, BlockJisonCell>> = arrayOf(
        mapOf(
            1 to action46,
        ),
        mapOf(
            8 to action47,
            30 to action47,
        ),
        mapOf(
            26 to action48,
            31 to action16,
        ),
        mapOf(
            31 to action49,
        ),
        mapOf(
            17 to action50,
        ),
        buildJisonState(symbols4, action51),
        mapOf(
            10 to action3,
            11 to action52,
            13 to action5,
            14 to action24,
            15 to action25,
            16 to action26,
            19 to action6,
            20 to action7,
            21 to action8,
            22 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            28 to action14,
            29 to action15,
            31 to action16,
            39 to action17,
            43 to action18,
            46 to action19,
        ),
        mapOf(
            30 to action53,
        ),
        mapOf(
            41 to action54,
        ),
        mapOf(
            41 to action55,
        ),
        mapOf(
            45 to action56,
        ),
        mapOf(
            48 to action57,
        ),
        buildJisonState(symbols2, action58),
        mapOf(
            18 to action59,
        ),
        mapOf(
            18 to action60,
        ),
        buildJisonState(symbols4, action61),
        mapOf(
            18 to action62,
        ),
        mapOf(
            30 to action63,
        ),
        buildJisonState(symbols1, action64),
        buildJisonState(symbols1, action65),
    )

    private fun stateChunk2(): Array<Map<Int, BlockJisonCell>> = arrayOf(
        buildJisonState(symbols1, action66),
        buildJisonState(symbols1, action67),
        buildJisonState(symbols1, action68),
        mapOf(
            36 to action69,
        ),
        mapOf(
            33 to action70,
            34 to action71,
        ),
        mapOf(
            15 to action72,
        ),
        buildJisonState(symbols1, action73),
        buildJisonState(symbols2, action74),
        mapOf(
            38 to action75,
        ),
        mapOf(
            33 to action76,
            34 to action71,
            38 to action77,
        ),
        mapOf(
            31 to action78,
        ),
        buildJisonState(symbols2, action79),
        mapOf(
            38 to action80,
        ),
    )

    val defaultActions: Map<Int, BlockJisonCell> = mapOf(
        20 to BlockJisonCell.Reduce(7),
        23 to BlockJisonCell.Reduce(14),
        50 to BlockJisonCell.Reduce(15),
        52 to BlockJisonCell.Reduce(32),
    )

    val lexerPatterns: List<BlockJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
        addAll(lexerChunk5())
    }

    private fun lexerChunk0(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(0, "^(?:block-beta\\b)"),
        BlockJisonLexerPattern(1, "^(?:block:)"),
        BlockJisonLexerPattern(2, "^(?:block\\b)"),
        BlockJisonLexerPattern(3, "^(?:[\\s]+)"),
        BlockJisonLexerPattern(4, "^(?:[\\n]+)"),
        BlockJisonLexerPattern(5, "^(?:((\\u000D\\u000A)|(\\u000A)))"),
        BlockJisonLexerPattern(6, "^(?:columns\\s+auto\\b)"),
        BlockJisonLexerPattern(7, "^(?:columns\\s+[\\d]+)"),
        BlockJisonLexerPattern(8, "^(?:[\"][`])"),
        BlockJisonLexerPattern(9, "^(?:[^`\"]+)"),
        BlockJisonLexerPattern(10, "^(?:[`][\"])"),
        BlockJisonLexerPattern(11, "^(?:[\"])"),
        BlockJisonLexerPattern(12, "^(?:[\"])"),
        BlockJisonLexerPattern(13, "^(?:[^\"]*)"),
        BlockJisonLexerPattern(14, "^(?:space[:]\\d+)"),
        BlockJisonLexerPattern(15, "^(?:space\\b)"),
        BlockJisonLexerPattern(16, "^(?:default\\b)"),
        BlockJisonLexerPattern(17, "^(?:linkStyle\\b)"),
        BlockJisonLexerPattern(18, "^(?:interpolate\\b)"),
        BlockJisonLexerPattern(19, "^(?:classDef\\s+)"),
    )

    private fun lexerChunk1(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(20, "^(?:DEFAULT\\s+)"),
        BlockJisonLexerPattern(21, "^(?:\\w+\\s+)"),
        BlockJisonLexerPattern(22, "^(?:[^\\n]*)"),
        BlockJisonLexerPattern(23, "^(?:class\\s+)"),
        BlockJisonLexerPattern(24, "^(?:(\\w+)+((,\\s*\\w+)*))"),
        BlockJisonLexerPattern(25, "^(?:[^\\n]*)"),
        BlockJisonLexerPattern(26, "^(?:style\\s+)"),
        BlockJisonLexerPattern(27, "^(?:(\\w+)+((,\\s*\\w+)*))"),
        BlockJisonLexerPattern(28, "^(?:[^\\n]*)"),
        BlockJisonLexerPattern(29, "^(?:accTitle\\s*:\\s*)"),
        BlockJisonLexerPattern(30, "^(?:(?!\\n||)*[^\\n]*)"),
        BlockJisonLexerPattern(31, "^(?:accDescr\\s*:\\s*)"),
        BlockJisonLexerPattern(32, "^(?:(?!\\n||)*[^\\n]*)"),
        BlockJisonLexerPattern(33, "^(?:accDescr\\s*\\{\\s*)"),
        BlockJisonLexerPattern(34, "^(?:[\\}])"),
        BlockJisonLexerPattern(35, "^(?:[^\\}]*)"),
        BlockJisonLexerPattern(36, "^(?:end\\b\\s*)"),
        BlockJisonLexerPattern(37, "^(?:\\(\\(\\()"),
        BlockJisonLexerPattern(38, "^(?:\\)\\)\\))"),
        BlockJisonLexerPattern(39, "^(?:[\\)]\\))"),
    )

    private fun lexerChunk2(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(40, "^(?:\\}\\})"),
        BlockJisonLexerPattern(41, "^(?:\\})"),
        BlockJisonLexerPattern(42, "^(?:\\(-)"),
        BlockJisonLexerPattern(43, "^(?:-\\))"),
        BlockJisonLexerPattern(44, "^(?:\\(\\()"),
        BlockJisonLexerPattern(45, "^(?:\\]\\])"),
        BlockJisonLexerPattern(46, "^(?:\\()"),
        BlockJisonLexerPattern(47, "^(?:\\]\\))"),
        BlockJisonLexerPattern(48, "^(?:\\\\\\])"),
        BlockJisonLexerPattern(49, "^(?:\\/\\])"),
        BlockJisonLexerPattern(50, "^(?:\\)\\])"),
        BlockJisonLexerPattern(51, "^(?:[\\)])"),
        BlockJisonLexerPattern(52, "^(?:\\]>)"),
        BlockJisonLexerPattern(53, "^(?:[\\]])"),
        BlockJisonLexerPattern(54, "^(?:-\\))"),
        BlockJisonLexerPattern(55, "^(?:\\(-)"),
        BlockJisonLexerPattern(56, "^(?:\\)\\))"),
        BlockJisonLexerPattern(57, "^(?:\\))"),
        BlockJisonLexerPattern(58, "^(?:\\(\\(\\()"),
        BlockJisonLexerPattern(59, "^(?:\\(\\()"),
    )

    private fun lexerChunk3(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(60, "^(?:\\{\\{)"),
        BlockJisonLexerPattern(61, "^(?:\\{)"),
        BlockJisonLexerPattern(62, "^(?:>)"),
        BlockJisonLexerPattern(63, "^(?:\\(\\[)"),
        BlockJisonLexerPattern(64, "^(?:\\()"),
        BlockJisonLexerPattern(65, "^(?:\\[\\[)"),
        BlockJisonLexerPattern(66, "^(?:\\[\\|)"),
        BlockJisonLexerPattern(67, "^(?:\\[\\()"),
        BlockJisonLexerPattern(68, "^(?:\\)\\)\\))"),
        BlockJisonLexerPattern(69, "^(?:\\[\\\\)"),
        BlockJisonLexerPattern(70, "^(?:\\[\\/)"),
        BlockJisonLexerPattern(71, "^(?:\\[\\\\)"),
        BlockJisonLexerPattern(72, "^(?:\\[)"),
        BlockJisonLexerPattern(73, "^(?:<\\[)"),
        BlockJisonLexerPattern(74, "^(?:[^\\(\\[\\n\\-\\)\\{\\}\\s\\<\\>:=]+)"),
        BlockJisonLexerPattern(75, "^(?:\$)"),
        BlockJisonLexerPattern(76, "^(?:[\"][`])"),
        BlockJisonLexerPattern(77, "^(?:[\"][`])"),
        BlockJisonLexerPattern(78, "^(?:[^`\"]+)"),
        BlockJisonLexerPattern(79, "^(?:[`][\"])"),
    )

    private fun lexerChunk4(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(80, "^(?:[\"])"),
        BlockJisonLexerPattern(81, "^(?:[\"])"),
        BlockJisonLexerPattern(82, "^(?:[^\"]+)"),
        BlockJisonLexerPattern(83, "^(?:[\"])"),
        BlockJisonLexerPattern(84, "^(?:\\]>\\s*\\()"),
        BlockJisonLexerPattern(85, "^(?:,?\\s*right\\s*)"),
        BlockJisonLexerPattern(86, "^(?:,?\\s*left\\s*)"),
        BlockJisonLexerPattern(87, "^(?:,?\\s*x\\s*)"),
        BlockJisonLexerPattern(88, "^(?:,?\\s*y\\s*)"),
        BlockJisonLexerPattern(89, "^(?:,?\\s*up\\s*)"),
        BlockJisonLexerPattern(90, "^(?:,?\\s*down\\s*)"),
        BlockJisonLexerPattern(91, "^(?:\\)\\s*)"),
        BlockJisonLexerPattern(92, "^(?:\\s*[xo<]?--+[-xo>]\\s*)"),
        BlockJisonLexerPattern(93, "^(?:\\s*[xo<]?==+[=xo>]\\s*)"),
        BlockJisonLexerPattern(94, "^(?:\\s*[xo<]?-?\\.+-[xo>]?\\s*)"),
        BlockJisonLexerPattern(95, "^(?:\\s*~~[\\~]+\\s*)"),
        BlockJisonLexerPattern(96, "^(?:\\s*[xo<]?--\\s*)"),
        BlockJisonLexerPattern(97, "^(?:\\s*[xo<]?==\\s*)"),
        BlockJisonLexerPattern(98, "^(?:\\s*[xo<]?-\\.\\s*)"),
        BlockJisonLexerPattern(99, "^(?:[\"][`])"),
    )

    private fun lexerChunk5(): List<BlockJisonLexerPattern> = listOf(
        BlockJisonLexerPattern(100, "^(?:[\"])"),
        BlockJisonLexerPattern(101, "^(?:\\s*[xo<]?--+[-xo>]\\s*)"),
        BlockJisonLexerPattern(102, "^(?:\\s*[xo<]?==+[=xo>]\\s*)"),
        BlockJisonLexerPattern(103, "^(?:\\s*[xo<]?-?\\.+-[xo>]?\\s*)"),
        BlockJisonLexerPattern(104, "^(?::\\d+)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "STYLE_DEFINITION" to intArrayOf(28),
        "STYLE_STMNT" to intArrayOf(27),
        "CLASSDEFID" to intArrayOf(22),
        "CLASSDEF" to intArrayOf(20, 21),
        "CLASS_STYLE" to intArrayOf(25),
        "CLASS" to intArrayOf(24),
        "LLABEL" to intArrayOf(99, 100, 101, 102, 103),
        "ARROW_DIR" to intArrayOf(85, 86, 87, 88, 89, 90, 91),
        "BLOCK_ARROW" to intArrayOf(76, 81, 84),
        "NODE" to intArrayOf(37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 77, 80),
        "md_string" to intArrayOf(9, 10, 78, 79),
        "space" to intArrayOf(),
        "string" to intArrayOf(12, 13, 82, 83),
        "acc_descr_multiline" to intArrayOf(34, 35),
        "acc_descr" to intArrayOf(32),
        "acc_title" to intArrayOf(30),
        "INITIAL" to intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 14, 15, 16, 17, 18, 19, 23, 26, 29, 31, 33, 36, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 92, 93, 94, 95, 96, 97, 98, 104),
    )
}
