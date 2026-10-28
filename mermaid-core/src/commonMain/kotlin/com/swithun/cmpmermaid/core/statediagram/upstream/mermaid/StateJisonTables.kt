package com.swithun.cmpmermaid.core.statediagram.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 stateDiagram.jison.
 * Upstream stateDiagram.jison SHA-256: 94d4715707c3b35192b8586fa271e843f2c084e9ff70dd8db07c510c1a23e231
 * Distribution chunk SHA-256: 33ba302a233f6a2b43efd12b2ba42cd1878d637bf79d351d5901812728c9318d
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:state-parser
 */
internal object StateJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "SPACE" to 4,
        "NL" to 5,
        "SD" to 6,
        "document" to 7,
        "line" to 8,
        "statement" to 9,
        "classDefStatement" to 10,
        "styleStatement" to 11,
        "cssClassStatement" to 12,
        "idStatement" to 13,
        "DESCR" to 14,
        "-->" to 15,
        "HIDE_EMPTY" to 16,
        "scale" to 17,
        "WIDTH" to 18,
        "COMPOSIT_STATE" to 19,
        "STRUCT_START" to 20,
        "STRUCT_STOP" to 21,
        "STATE_DESCR" to 22,
        "AS" to 23,
        "ID" to 24,
        "FORK" to 25,
        "JOIN" to 26,
        "CHOICE" to 27,
        "CONCURRENT" to 28,
        "note" to 29,
        "notePosition" to 30,
        "NOTE_TEXT" to 31,
        "direction" to 32,
        "acc_title" to 33,
        "acc_title_value" to 34,
        "acc_descr" to 35,
        "acc_descr_value" to 36,
        "acc_descr_multiline_value" to 37,
        "CLICK" to 38,
        "STRING" to 39,
        "HREF" to 40,
        "classDef" to 41,
        "CLASSDEF_ID" to 42,
        "CLASSDEF_STYLEOPTS" to 43,
        "DEFAULT" to 44,
        "style" to 45,
        "STYLE_IDS" to 46,
        "STYLEDEF_STYLEOPTS" to 47,
        "class" to 48,
        "CLASSENTITY_IDS" to 49,
        "STYLECLASS" to 50,
        "direction_tb" to 51,
        "direction_bt" to 52,
        "direction_rl" to 53,
        "direction_lr" to 54,
        "eol" to 55,
        ";" to 56,
        "EDGE_STATE" to 57,
        "STYLE_SEPARATOR" to 58,
        "left_of" to 59,
        "right_of" to 60,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "SPACE",
        5 to "NL",
        6 to "SD",
        14 to "DESCR",
        15 to "-->",
        16 to "HIDE_EMPTY",
        17 to "scale",
        18 to "WIDTH",
        19 to "COMPOSIT_STATE",
        20 to "STRUCT_START",
        21 to "STRUCT_STOP",
        22 to "STATE_DESCR",
        23 to "AS",
        24 to "ID",
        25 to "FORK",
        26 to "JOIN",
        27 to "CHOICE",
        28 to "CONCURRENT",
        29 to "note",
        31 to "NOTE_TEXT",
        33 to "acc_title",
        34 to "acc_title_value",
        35 to "acc_descr",
        36 to "acc_descr_value",
        37 to "acc_descr_multiline_value",
        38 to "CLICK",
        39 to "STRING",
        40 to "HREF",
        41 to "classDef",
        42 to "CLASSDEF_ID",
        43 to "CLASSDEF_STYLEOPTS",
        44 to "DEFAULT",
        45 to "style",
        46 to "STYLE_IDS",
        47 to "STYLEDEF_STYLEOPTS",
        48 to "class",
        49 to "CLASSENTITY_IDS",
        50 to "STYLECLASS",
        51 to "direction_tb",
        52 to "direction_bt",
        53 to "direction_rl",
        54 to "direction_lr",
        56 to ";",
        57 to "EDGE_STATE",
        58 to "STYLE_SEPARATOR",
        59 to "left_of",
        60 to "right_of",
    )

    val productions: Array<StateJisonProduction> = arrayOf(
        StateJisonProduction(symbol = 0, length = 0),
        StateJisonProduction(symbol = 3, length = 2),
        StateJisonProduction(symbol = 3, length = 2),
        StateJisonProduction(symbol = 3, length = 2),
        StateJisonProduction(symbol = 7, length = 0),
        StateJisonProduction(symbol = 7, length = 2),
        StateJisonProduction(symbol = 8, length = 2),
        StateJisonProduction(symbol = 8, length = 1),
        StateJisonProduction(symbol = 8, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 2),
        StateJisonProduction(symbol = 9, length = 3),
        StateJisonProduction(symbol = 9, length = 4),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 2),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 4),
        StateJisonProduction(symbol = 9, length = 3),
        StateJisonProduction(symbol = 9, length = 6),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 4),
        StateJisonProduction(symbol = 9, length = 4),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 2),
        StateJisonProduction(symbol = 9, length = 2),
        StateJisonProduction(symbol = 9, length = 1),
        StateJisonProduction(symbol = 9, length = 5),
        StateJisonProduction(symbol = 9, length = 5),
        StateJisonProduction(symbol = 10, length = 3),
        StateJisonProduction(symbol = 10, length = 3),
        StateJisonProduction(symbol = 11, length = 3),
        StateJisonProduction(symbol = 12, length = 3),
        StateJisonProduction(symbol = 32, length = 1),
        StateJisonProduction(symbol = 32, length = 1),
        StateJisonProduction(symbol = 32, length = 1),
        StateJisonProduction(symbol = 32, length = 1),
        StateJisonProduction(symbol = 55, length = 1),
        StateJisonProduction(symbol = 55, length = 1),
        StateJisonProduction(symbol = 13, length = 1),
        StateJisonProduction(symbol = 13, length = 1),
        StateJisonProduction(symbol = 13, length = 3),
        StateJisonProduction(symbol = 13, length = 3),
        StateJisonProduction(symbol = 30, length = 1),
        StateJisonProduction(symbol = 30, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: StateJisonCell = StateJisonCell.Goto(1)
    private val action1: StateJisonCell = StateJisonCell.Shift(2)
    private val action2: StateJisonCell = StateJisonCell.Shift(3)
    private val action3: StateJisonCell = StateJisonCell.Shift(4)
    private val action4: StateJisonCell = StateJisonCell.Accept
    private val action5: StateJisonCell = StateJisonCell.Goto(5)
    private val action6: StateJisonCell = StateJisonCell.Goto(6)
    private val action7: StateJisonCell = StateJisonCell.Reduce(4)
    private val action8: StateJisonCell = StateJisonCell.Goto(7)
    private val action9: StateJisonCell = StateJisonCell.Reduce(1)
    private val action10: StateJisonCell = StateJisonCell.Reduce(2)
    private val action11: StateJisonCell = StateJisonCell.Reduce(3)
    private val action12: StateJisonCell = StateJisonCell.Shift(9)
    private val action13: StateJisonCell = StateJisonCell.Shift(11)
    private val action14: StateJisonCell = StateJisonCell.Goto(8)
    private val action15: StateJisonCell = StateJisonCell.Goto(10)
    private val action16: StateJisonCell = StateJisonCell.Goto(12)
    private val action17: StateJisonCell = StateJisonCell.Goto(13)
    private val action18: StateJisonCell = StateJisonCell.Goto(14)
    private val action19: StateJisonCell = StateJisonCell.Goto(15)
    private val action20: StateJisonCell = StateJisonCell.Shift(16)
    private val action21: StateJisonCell = StateJisonCell.Shift(17)
    private val action22: StateJisonCell = StateJisonCell.Shift(18)
    private val action23: StateJisonCell = StateJisonCell.Shift(19)
    private val action24: StateJisonCell = StateJisonCell.Shift(33)
    private val action25: StateJisonCell = StateJisonCell.Shift(20)
    private val action26: StateJisonCell = StateJisonCell.Shift(21)
    private val action27: StateJisonCell = StateJisonCell.Shift(22)
    private val action28: StateJisonCell = StateJisonCell.Shift(23)
    private val action29: StateJisonCell = StateJisonCell.Shift(24)
    private val action30: StateJisonCell = StateJisonCell.Goto(25)
    private val action31: StateJisonCell = StateJisonCell.Shift(26)
    private val action32: StateJisonCell = StateJisonCell.Shift(27)
    private val action33: StateJisonCell = StateJisonCell.Shift(28)
    private val action34: StateJisonCell = StateJisonCell.Shift(29)
    private val action35: StateJisonCell = StateJisonCell.Shift(30)
    private val action36: StateJisonCell = StateJisonCell.Shift(31)
    private val action37: StateJisonCell = StateJisonCell.Shift(32)
    private val action38: StateJisonCell = StateJisonCell.Shift(35)
    private val action39: StateJisonCell = StateJisonCell.Shift(36)
    private val action40: StateJisonCell = StateJisonCell.Shift(37)
    private val action41: StateJisonCell = StateJisonCell.Shift(38)
    private val action42: StateJisonCell = StateJisonCell.Shift(34)
    private val action43: StateJisonCell = StateJisonCell.Reduce(5)
    private val action44: StateJisonCell = StateJisonCell.Goto(39)
    private val action45: StateJisonCell = StateJisonCell.Reduce(7)
    private val action46: StateJisonCell = StateJisonCell.Reduce(8)
    private val action47: StateJisonCell = StateJisonCell.Reduce(9)
    private val action48: StateJisonCell = StateJisonCell.Reduce(10)
    private val action49: StateJisonCell = StateJisonCell.Reduce(11)
    private val action50: StateJisonCell = StateJisonCell.Reduce(12)
    private val action51: StateJisonCell = StateJisonCell.Shift(40)
    private val action52: StateJisonCell = StateJisonCell.Shift(41)
    private val action53: StateJisonCell = StateJisonCell.Reduce(16)
    private val action54: StateJisonCell = StateJisonCell.Shift(42)
    private val action55: StateJisonCell = StateJisonCell.Reduce(18)
    private val action56: StateJisonCell = StateJisonCell.Shift(43)
    private val action57: StateJisonCell = StateJisonCell.Shift(44)
    private val action58: StateJisonCell = StateJisonCell.Reduce(22)
    private val action59: StateJisonCell = StateJisonCell.Reduce(23)
    private val action60: StateJisonCell = StateJisonCell.Reduce(24)
    private val action61: StateJisonCell = StateJisonCell.Reduce(25)
    private val action62: StateJisonCell = StateJisonCell.Goto(45)
    private val action63: StateJisonCell = StateJisonCell.Shift(46)
    private val action64: StateJisonCell = StateJisonCell.Shift(47)
    private val action65: StateJisonCell = StateJisonCell.Shift(48)
    private val action66: StateJisonCell = StateJisonCell.Reduce(28)
    private val action67: StateJisonCell = StateJisonCell.Shift(49)
    private val action68: StateJisonCell = StateJisonCell.Shift(50)
    private val action69: StateJisonCell = StateJisonCell.Reduce(31)
    private val action70: StateJisonCell = StateJisonCell.Goto(51)
    private val action71: StateJisonCell = StateJisonCell.Shift(52)
    private val action72: StateJisonCell = StateJisonCell.Shift(53)
    private val action73: StateJisonCell = StateJisonCell.Shift(54)
    private val action74: StateJisonCell = StateJisonCell.Shift(55)
    private val action75: StateJisonCell = StateJisonCell.Reduce(44)
    private val action76: StateJisonCell = StateJisonCell.Shift(56)
    private val action77: StateJisonCell = StateJisonCell.Reduce(45)
    private val action78: StateJisonCell = StateJisonCell.Shift(57)
    private val action79: StateJisonCell = StateJisonCell.Reduce(38)
    private val action80: StateJisonCell = StateJisonCell.Reduce(39)
    private val action81: StateJisonCell = StateJisonCell.Reduce(40)
    private val action82: StateJisonCell = StateJisonCell.Reduce(41)
    private val action83: StateJisonCell = StateJisonCell.Reduce(6)
    private val action84: StateJisonCell = StateJisonCell.Reduce(13)
    private val action85: StateJisonCell = StateJisonCell.Goto(58)
    private val action86: StateJisonCell = StateJisonCell.Reduce(17)
    private val action87: StateJisonCell = StateJisonCell.Goto(59)
    private val action88: StateJisonCell = StateJisonCell.Shift(60)
    private val action89: StateJisonCell = StateJisonCell.Shift(61)
    private val action90: StateJisonCell = StateJisonCell.Shift(62)
    private val action91: StateJisonCell = StateJisonCell.Reduce(48)
    private val action92: StateJisonCell = StateJisonCell.Reduce(49)
    private val action93: StateJisonCell = StateJisonCell.Reduce(29)
    private val action94: StateJisonCell = StateJisonCell.Reduce(30)
    private val action95: StateJisonCell = StateJisonCell.Shift(63)
    private val action96: StateJisonCell = StateJisonCell.Shift(64)
    private val action97: StateJisonCell = StateJisonCell.Shift(65)
    private val action98: StateJisonCell = StateJisonCell.Shift(66)
    private val action99: StateJisonCell = StateJisonCell.Shift(67)
    private val action100: StateJisonCell = StateJisonCell.Shift(68)
    private val action101: StateJisonCell = StateJisonCell.Shift(69)
    private val action102: StateJisonCell = StateJisonCell.Shift(70)
    private val action103: StateJisonCell = StateJisonCell.Reduce(14)
    private val action104: StateJisonCell = StateJisonCell.Shift(71)
    private val action105: StateJisonCell = StateJisonCell.Shift(72)
    private val action106: StateJisonCell = StateJisonCell.Reduce(20)
    private val action107: StateJisonCell = StateJisonCell.Shift(73)
    private val action108: StateJisonCell = StateJisonCell.Shift(74)
    private val action109: StateJisonCell = StateJisonCell.Shift(75)
    private val action110: StateJisonCell = StateJisonCell.Shift(76)
    private val action111: StateJisonCell = StateJisonCell.Shift(77)
    private val action112: StateJisonCell = StateJisonCell.Reduce(34)
    private val action113: StateJisonCell = StateJisonCell.Reduce(35)
    private val action114: StateJisonCell = StateJisonCell.Reduce(36)
    private val action115: StateJisonCell = StateJisonCell.Reduce(37)
    private val action116: StateJisonCell = StateJisonCell.Reduce(46)
    private val action117: StateJisonCell = StateJisonCell.Reduce(47)
    private val action118: StateJisonCell = StateJisonCell.Reduce(15)
    private val action119: StateJisonCell = StateJisonCell.Reduce(19)
    private val action120: StateJisonCell = StateJisonCell.Goto(78)
    private val action121: StateJisonCell = StateJisonCell.Reduce(26)
    private val action122: StateJisonCell = StateJisonCell.Reduce(27)
    private val action123: StateJisonCell = StateJisonCell.Shift(79)
    private val action124: StateJisonCell = StateJisonCell.Shift(80)
    private val action125: StateJisonCell = StateJisonCell.Shift(81)
    private val action126: StateJisonCell = StateJisonCell.Reduce(32)
    private val action127: StateJisonCell = StateJisonCell.Reduce(33)
    private val action128: StateJisonCell = StateJisonCell.Reduce(21)
    private val symbols0 = intArrayOf(1, 4, 5, 7, 16, 17, 19, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols1 = intArrayOf(1, 4, 5, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols2 = intArrayOf(1, 4, 5, 14, 15, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols3 = intArrayOf(1, 4, 5, 16, 17, 19, 20, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols4 = intArrayOf(1, 4, 5, 14, 15, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 39, 40, 41, 45, 48, 51, 52, 53, 54, 57, 58)
    private val symbols5 = intArrayOf(4, 5, 7, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols6 = intArrayOf(1, 4, 5, 14, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 41, 45, 48, 51, 52, 53, 54, 57)
    private val symbols7 = intArrayOf(1, 4, 5, 14, 15, 16, 17, 19, 21, 22, 24, 25, 26, 27, 28, 29, 33, 35, 37, 38, 39, 40, 41, 45, 48, 51, 52, 53, 54, 57)

    val states: Array<Map<Int, StateJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
        addAll(stateChunk3())
        addAll(stateChunk4())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, StateJisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            5 to action2,
            6 to action3,
        ),
        mapOf(
            1 to action4,
        ),
        mapOf(
            3 to action5,
            4 to action1,
            5 to action2,
            6 to action3,
        ),
        mapOf(
            3 to action6,
            4 to action1,
            5 to action2,
            6 to action3,
        ),
        buildJisonState(symbols0, action7, mapOf(
            7 to action8,
        )),
        mapOf(
            1 to action9,
        ),
        mapOf(
            1 to action10,
        ),
        mapOf(
            1 to action11,
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            11 to action17,
            12 to action18,
            13 to action19,
            16 to action20,
            17 to action21,
            19 to action22,
            22 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            32 to action30,
            33 to action31,
            35 to action32,
            37 to action33,
            38 to action34,
            41 to action35,
            45 to action36,
            48 to action37,
            51 to action38,
            52 to action39,
            53 to action40,
            54 to action41,
            57 to action42,
        ),
        buildJisonState(symbols1, action43),
        mapOf(
            9 to action44,
            10 to action16,
            11 to action17,
            12 to action18,
            13 to action19,
            16 to action20,
            17 to action21,
            19 to action22,
            22 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            32 to action30,
            33 to action31,
            35 to action32,
            37 to action33,
            38 to action34,
            41 to action35,
            45 to action36,
            48 to action37,
            51 to action38,
            52 to action39,
            53 to action40,
            54 to action41,
            57 to action42,
        ),
        buildJisonState(symbols1, action45),
        buildJisonState(symbols1, action46),
        buildJisonState(symbols1, action47),
        buildJisonState(symbols1, action48),
        buildJisonState(symbols1, action49),
        buildJisonState(symbols2, action50, mapOf(
            14 to action51,
            15 to action52,
        )),
        buildJisonState(symbols1, action53),
        mapOf(
            18 to action54,
        ),
        buildJisonState(symbols3, action55, mapOf(
            20 to action56,
        )),
        mapOf(
            23 to action57,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, StateJisonCell>> = arrayOf(
        buildJisonState(symbols1, action58),
        buildJisonState(symbols1, action59),
        buildJisonState(symbols1, action60),
        buildJisonState(symbols1, action61),
        mapOf(
            30 to action62,
            31 to action63,
            59 to action64,
            60 to action65,
        ),
        buildJisonState(symbols1, action66),
        mapOf(
            34 to action67,
        ),
        mapOf(
            36 to action68,
        ),
        buildJisonState(symbols1, action69),
        mapOf(
            13 to action70,
            24 to action24,
            57 to action42,
        ),
        mapOf(
            42 to action71,
            44 to action72,
        ),
        mapOf(
            46 to action73,
        ),
        mapOf(
            49 to action74,
        ),
        buildJisonState(symbols4, action75, mapOf(
            58 to action76,
        )),
        buildJisonState(symbols4, action77, mapOf(
            58 to action78,
        )),
        buildJisonState(symbols1, action79),
        buildJisonState(symbols1, action80),
        buildJisonState(symbols1, action81),
        buildJisonState(symbols1, action82),
        buildJisonState(symbols1, action83),
    )

    private fun stateChunk2(): Array<Map<Int, StateJisonCell>> = arrayOf(
        buildJisonState(symbols1, action84),
        mapOf(
            13 to action85,
            24 to action24,
            57 to action42,
        ),
        buildJisonState(symbols1, action86),
        buildJisonState(symbols5, action7, mapOf(
            7 to action87,
        )),
        mapOf(
            24 to action88,
        ),
        mapOf(
            24 to action89,
        ),
        mapOf(
            23 to action90,
        ),
        mapOf(
            24 to action91,
        ),
        mapOf(
            24 to action92,
        ),
        buildJisonState(symbols1, action93),
        buildJisonState(symbols1, action94),
        mapOf(
            39 to action95,
            40 to action96,
        ),
        mapOf(
            43 to action97,
        ),
        mapOf(
            43 to action98,
        ),
        mapOf(
            47 to action99,
        ),
        mapOf(
            50 to action100,
        ),
        mapOf(
            24 to action101,
        ),
        mapOf(
            24 to action102,
        ),
        buildJisonState(symbols6, action103, mapOf(
            14 to action104,
        )),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            11 to action17,
            12 to action18,
            13 to action19,
            16 to action20,
            17 to action21,
            19 to action22,
            21 to action105,
            22 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            32 to action30,
            33 to action31,
            35 to action32,
            37 to action33,
            38 to action34,
            41 to action35,
            45 to action36,
            48 to action37,
            51 to action38,
            52 to action39,
            53 to action40,
            54 to action41,
            57 to action42,
        ),
    )

    private fun stateChunk3(): Array<Map<Int, StateJisonCell>> = arrayOf(
        buildJisonState(symbols3, action106, mapOf(
            20 to action107,
        )),
        mapOf(
            31 to action108,
        ),
        mapOf(
            24 to action109,
        ),
        mapOf(
            39 to action110,
        ),
        mapOf(
            39 to action111,
        ),
        buildJisonState(symbols1, action112),
        buildJisonState(symbols1, action113),
        buildJisonState(symbols1, action114),
        buildJisonState(symbols1, action115),
        buildJisonState(symbols7, action116),
        buildJisonState(symbols7, action117),
        buildJisonState(symbols1, action118),
        buildJisonState(symbols1, action119),
        buildJisonState(symbols5, action7, mapOf(
            7 to action120,
        )),
        buildJisonState(symbols1, action121),
        buildJisonState(symbols1, action122),
        mapOf(
            5 to action123,
        ),
        mapOf(
            5 to action124,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            11 to action17,
            12 to action18,
            13 to action19,
            16 to action20,
            17 to action21,
            19 to action22,
            21 to action125,
            22 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            32 to action30,
            33 to action31,
            35 to action32,
            37 to action33,
            38 to action34,
            41 to action35,
            45 to action36,
            48 to action37,
            51 to action38,
            52 to action39,
            53 to action40,
            54 to action41,
            57 to action42,
        ),
        buildJisonState(symbols1, action126),
    )

    private fun stateChunk4(): Array<Map<Int, StateJisonCell>> = arrayOf(
        buildJisonState(symbols1, action127),
        buildJisonState(symbols1, action128),
    )

    val lexerPatterns: List<StateJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
    }

    private fun lexerChunk0(): List<StateJisonLexerPattern> = listOf(
        StateJisonLexerPattern(0, "^(?:click\\b)"),
        StateJisonLexerPattern(1, "^(?:href\\b)"),
        StateJisonLexerPattern(2, "^(?:\"[^\"]*\")"),
        StateJisonLexerPattern(3, "^(?:default\\b)"),
        StateJisonLexerPattern(4, "^(?:.*direction\\s+TB[^\\n]*)"),
        StateJisonLexerPattern(5, "^(?:.*direction\\s+BT[^\\n]*)"),
        StateJisonLexerPattern(6, "^(?:.*direction\\s+RL[^\\n]*)"),
        StateJisonLexerPattern(7, "^(?:.*direction\\s+LR[^\\n]*)"),
        StateJisonLexerPattern(8, "^(?:[\\n]+)"),
        StateJisonLexerPattern(9, "^(?:[\\s]+)"),
        StateJisonLexerPattern(10, "^(?:((?!\\n)\\s)+)"),
        StateJisonLexerPattern(11, "^(?:#[^\\n]*)"),
        StateJisonLexerPattern(12, "^(?:%%(?!\\{)[^\\n]*)"),
        StateJisonLexerPattern(13, "^(?:scale\\s+)"),
        StateJisonLexerPattern(14, "^(?:\\d+)"),
        StateJisonLexerPattern(15, "^(?:\\s+width\\b)"),
        StateJisonLexerPattern(16, "^(?:accTitle\\s*:\\s*)"),
        StateJisonLexerPattern(17, "^(?:(?!\\n||)*[^\\n]*)"),
        StateJisonLexerPattern(18, "^(?:accDescr\\s*:\\s*)"),
        StateJisonLexerPattern(19, "^(?:(?!\\n||)*[^\\n]*)"),
    )

    private fun lexerChunk1(): List<StateJisonLexerPattern> = listOf(
        StateJisonLexerPattern(20, "^(?:accDescr\\s*\\{\\s*)"),
        StateJisonLexerPattern(21, "^(?:[\\}])"),
        StateJisonLexerPattern(22, "^(?:[^\\}]*)"),
        StateJisonLexerPattern(23, "^(?:classDef\\s+)"),
        StateJisonLexerPattern(24, "^(?:DEFAULT\\s+)"),
        StateJisonLexerPattern(25, "^(?:\\w+\\s+)"),
        StateJisonLexerPattern(26, "^(?:[^\\n]*)"),
        StateJisonLexerPattern(27, "^(?:class\\s+)"),
        StateJisonLexerPattern(28, "^(?:(\\w+)+((,\\s*\\w+)*))"),
        StateJisonLexerPattern(29, "^(?:[^\\n]*)"),
        StateJisonLexerPattern(30, "^(?:style\\s+)"),
        StateJisonLexerPattern(31, "^(?:[\\w,]+\\s+)"),
        StateJisonLexerPattern(32, "^(?:[^\\n]*)"),
        StateJisonLexerPattern(33, "^(?:scale\\s+)"),
        StateJisonLexerPattern(34, "^(?:\\d+)"),
        StateJisonLexerPattern(35, "^(?:\\s+width\\b)"),
        StateJisonLexerPattern(36, "^(?:state\\s+)"),
        StateJisonLexerPattern(37, "^(?:.*<<fork>>)"),
        StateJisonLexerPattern(38, "^(?:.*<<join>>)"),
        StateJisonLexerPattern(39, "^(?:.*<<choice>>)"),
    )

    private fun lexerChunk2(): List<StateJisonLexerPattern> = listOf(
        StateJisonLexerPattern(40, "^(?:.*\\[\\[fork\\]\\])"),
        StateJisonLexerPattern(41, "^(?:.*\\[\\[join\\]\\])"),
        StateJisonLexerPattern(42, "^(?:.*\\[\\[choice\\]\\])"),
        StateJisonLexerPattern(43, "^(?:.*direction\\s+TB[^\\n]*)"),
        StateJisonLexerPattern(44, "^(?:.*direction\\s+BT[^\\n]*)"),
        StateJisonLexerPattern(45, "^(?:.*direction\\s+RL[^\\n]*)"),
        StateJisonLexerPattern(46, "^(?:.*direction\\s+LR[^\\n]*)"),
        StateJisonLexerPattern(47, "^(?:[\"])"),
        StateJisonLexerPattern(48, "^(?:\\s*as\\s+)"),
        StateJisonLexerPattern(49, "^(?:[^\\n\\{]*)"),
        StateJisonLexerPattern(50, "^(?:[\"])"),
        StateJisonLexerPattern(51, "^(?:[^\"]*)"),
        StateJisonLexerPattern(52, "^(?:\\w+\\s+\\w+.*?\\{)"),
        StateJisonLexerPattern(53, "^(?:[^\\n\\s\\{]+)"),
        StateJisonLexerPattern(54, "^(?:\\n)"),
        StateJisonLexerPattern(55, "^(?:\\{)"),
        StateJisonLexerPattern(56, "^(?:\\})"),
        StateJisonLexerPattern(57, "^(?:[\\n])"),
        StateJisonLexerPattern(58, "^(?:note\\s+)"),
        StateJisonLexerPattern(59, "^(?:left of\\b)"),
    )

    private fun lexerChunk3(): List<StateJisonLexerPattern> = listOf(
        StateJisonLexerPattern(60, "^(?:right of\\b)"),
        StateJisonLexerPattern(61, "^(?:\")"),
        StateJisonLexerPattern(62, "^(?:\\s*as\\s*)"),
        StateJisonLexerPattern(63, "^(?:[\"])"),
        StateJisonLexerPattern(64, "^(?:[^\"]*)"),
        StateJisonLexerPattern(65, "^(?:[^\\n]*)"),
        StateJisonLexerPattern(66, "^(?:\\s*[^:\\n\\s\\-]+)"),
        StateJisonLexerPattern(67, "^(?:\\s*:[^:\\n;]+)"),
        StateJisonLexerPattern(68, "^(?:[\\s\\S]*?\\n\\s*end note\\b)"),
        StateJisonLexerPattern(69, "^(?:stateDiagram\\s+)"),
        StateJisonLexerPattern(70, "^(?:stateDiagram-v2\\s+)"),
        StateJisonLexerPattern(71, "^(?:hide empty description\\b)"),
        StateJisonLexerPattern(72, "^(?:\\[\\*\\])"),
        StateJisonLexerPattern(73, "^(?:[^:\\n\\s\\-\\{]+)"),
        StateJisonLexerPattern(74, "^(?:\\s*:(?:[^:\\n;]|:[^:\\n;])+)"),
        StateJisonLexerPattern(75, "^(?:-->)"),
        StateJisonLexerPattern(76, "^(?:--)"),
        StateJisonLexerPattern(77, "^(?::::)"),
        StateJisonLexerPattern(78, "^(?:\$)"),
        StateJisonLexerPattern(79, "^(?:.)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "LINE" to intArrayOf(10, 11, 12),
        "struct" to intArrayOf(10, 11, 12, 23, 27, 30, 36, 43, 44, 45, 46, 56, 57, 58, 72, 73, 74, 75, 76, 77),
        "FLOATING_NOTE_ID" to intArrayOf(65),
        "FLOATING_NOTE" to intArrayOf(62, 63, 64),
        "NOTE_TEXT" to intArrayOf(67, 68),
        "NOTE_ID" to intArrayOf(66),
        "NOTE" to intArrayOf(59, 60, 61),
        "STYLEDEF_STYLEOPTS" to intArrayOf(),
        "STYLEDEF_STYLES" to intArrayOf(32),
        "STYLE_IDS" to intArrayOf(),
        "STYLE" to intArrayOf(31),
        "CLASS_STYLE" to intArrayOf(29),
        "CLASS" to intArrayOf(28),
        "CLASSDEFID" to intArrayOf(26),
        "CLASSDEF" to intArrayOf(24, 25),
        "acc_descr_multiline" to intArrayOf(21, 22),
        "acc_descr" to intArrayOf(19),
        "acc_title" to intArrayOf(17),
        "SCALE" to intArrayOf(14, 15, 34, 35),
        "ALIAS" to intArrayOf(),
        "STATE_ID" to intArrayOf(49),
        "STATE_STRING" to intArrayOf(50, 51),
        "FORK_STATE" to intArrayOf(),
        "STATE" to intArrayOf(10, 11, 12, 37, 38, 39, 40, 41, 42, 47, 48, 52, 53, 54, 55),
        "ID" to intArrayOf(10, 11, 12),
        "INITIAL" to intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 16, 18, 20, 23, 27, 30, 33, 36, 55, 58, 69, 70, 71, 72, 73, 74, 75, 77, 78, 79),
    )
}
