package com.swithun.cmpmermaid.core.xychart.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 xychart.jison.
 * Upstream xychart.jison SHA-256: ce1e4904b3c2036ac29cc62b6e6561058b00e4bc9c9bbf661873451bb55bf0fc
 * Distribution chunk SHA-256: d602c86e3513b44777118606f7190dbc4d260ecaba6f26ed3adb56f7375b2b7a
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:xychart-parser
 */
internal object XyJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "eol" to 4,
        "XYCHART" to 5,
        "chartConfig" to 6,
        "document" to 7,
        "CHART_ORIENTATION" to 8,
        "statement" to 9,
        "title" to 10,
        "text" to 11,
        "X_AXIS" to 12,
        "parseXAxis" to 13,
        "Y_AXIS" to 14,
        "parseYAxis" to 15,
        "LINE" to 16,
        "plotData" to 17,
        "BAR" to 18,
        "acc_title" to 19,
        "acc_title_value" to 20,
        "acc_descr" to 21,
        "acc_descr_value" to 22,
        "acc_descr_multiline_value" to 23,
        "SQUARE_BRACES_START" to 24,
        "dataPoints" to 25,
        "SQUARE_BRACES_END" to 26,
        "dataPoint" to 27,
        "COMMA" to 28,
        "NUMBER_WITH_DECIMAL" to 29,
        "STR" to 30,
        "xAxisData" to 31,
        "bandData" to 32,
        "ARROW_DELIMITER" to 33,
        "commaSeparatedTexts" to 34,
        "yAxisData" to 35,
        "NEWLINE" to 36,
        "SEMI" to 37,
        "EOF" to 38,
        "alphaNum" to 39,
        "MD_STR" to 40,
        "alphaNumToken" to 41,
        "AMP" to 42,
        "NUM" to 43,
        "ALPHA" to 44,
        "PLUS" to 45,
        "EQUALS" to 46,
        "MULT" to 47,
        "DOT" to 48,
        "BRKT" to 49,
        "MINUS" to 50,
        "UNDERSCORE" to 51,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        5 to "XYCHART",
        8 to "CHART_ORIENTATION",
        10 to "title",
        12 to "X_AXIS",
        14 to "Y_AXIS",
        16 to "LINE",
        18 to "BAR",
        19 to "acc_title",
        20 to "acc_title_value",
        21 to "acc_descr",
        22 to "acc_descr_value",
        23 to "acc_descr_multiline_value",
        24 to "SQUARE_BRACES_START",
        26 to "SQUARE_BRACES_END",
        28 to "COMMA",
        29 to "NUMBER_WITH_DECIMAL",
        30 to "STR",
        33 to "ARROW_DELIMITER",
        36 to "NEWLINE",
        37 to "SEMI",
        38 to "EOF",
        40 to "MD_STR",
        42 to "AMP",
        43 to "NUM",
        44 to "ALPHA",
        45 to "PLUS",
        46 to "EQUALS",
        47 to "MULT",
        48 to "DOT",
        49 to "BRKT",
        50 to "MINUS",
        51 to "UNDERSCORE",
    )

    val productions: Array<XyJisonProduction> = arrayOf(
        XyJisonProduction(symbol = 0, length = 0),
        XyJisonProduction(symbol = 3, length = 2),
        XyJisonProduction(symbol = 3, length = 3),
        XyJisonProduction(symbol = 3, length = 2),
        XyJisonProduction(symbol = 3, length = 1),
        XyJisonProduction(symbol = 6, length = 1),
        XyJisonProduction(symbol = 7, length = 0),
        XyJisonProduction(symbol = 7, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 3),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 3),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 2),
        XyJisonProduction(symbol = 9, length = 1),
        XyJisonProduction(symbol = 17, length = 3),
        XyJisonProduction(symbol = 25, length = 3),
        XyJisonProduction(symbol = 25, length = 1),
        XyJisonProduction(symbol = 27, length = 2),
        XyJisonProduction(symbol = 27, length = 1),
        XyJisonProduction(symbol = 13, length = 1),
        XyJisonProduction(symbol = 13, length = 2),
        XyJisonProduction(symbol = 13, length = 1),
        XyJisonProduction(symbol = 31, length = 1),
        XyJisonProduction(symbol = 31, length = 3),
        XyJisonProduction(symbol = 32, length = 3),
        XyJisonProduction(symbol = 34, length = 3),
        XyJisonProduction(symbol = 34, length = 1),
        XyJisonProduction(symbol = 15, length = 1),
        XyJisonProduction(symbol = 15, length = 2),
        XyJisonProduction(symbol = 15, length = 1),
        XyJisonProduction(symbol = 35, length = 3),
        XyJisonProduction(symbol = 4, length = 1),
        XyJisonProduction(symbol = 4, length = 1),
        XyJisonProduction(symbol = 4, length = 1),
        XyJisonProduction(symbol = 11, length = 1),
        XyJisonProduction(symbol = 11, length = 1),
        XyJisonProduction(symbol = 11, length = 1),
        XyJisonProduction(symbol = 39, length = 1),
        XyJisonProduction(symbol = 39, length = 2),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
        XyJisonProduction(symbol = 41, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: XyJisonCell = XyJisonCell.Reduce(6)
    private val action1: XyJisonCell = XyJisonCell.Goto(1)
    private val action2: XyJisonCell = XyJisonCell.Goto(2)
    private val action3: XyJisonCell = XyJisonCell.Shift(3)
    private val action4: XyJisonCell = XyJisonCell.Goto(4)
    private val action5: XyJisonCell = XyJisonCell.Shift(5)
    private val action6: XyJisonCell = XyJisonCell.Shift(6)
    private val action7: XyJisonCell = XyJisonCell.Shift(7)
    private val action8: XyJisonCell = XyJisonCell.Accept
    private val action9: XyJisonCell = XyJisonCell.Goto(8)
    private val action10: XyJisonCell = XyJisonCell.Goto(10)
    private val action11: XyJisonCell = XyJisonCell.Goto(9)
    private val action12: XyJisonCell = XyJisonCell.Shift(11)
    private val action13: XyJisonCell = XyJisonCell.Reduce(4)
    private val action14: XyJisonCell = XyJisonCell.Goto(12)
    private val action15: XyJisonCell = XyJisonCell.Shift(13)
    private val action16: XyJisonCell = XyJisonCell.Shift(14)
    private val action17: XyJisonCell = XyJisonCell.Shift(15)
    private val action18: XyJisonCell = XyJisonCell.Shift(16)
    private val action19: XyJisonCell = XyJisonCell.Shift(17)
    private val action20: XyJisonCell = XyJisonCell.Shift(18)
    private val action21: XyJisonCell = XyJisonCell.Shift(19)
    private val action22: XyJisonCell = XyJisonCell.Shift(20)
    private val action23: XyJisonCell = XyJisonCell.Reduce(36)
    private val action24: XyJisonCell = XyJisonCell.Reduce(37)
    private val action25: XyJisonCell = XyJisonCell.Reduce(38)
    private val action26: XyJisonCell = XyJisonCell.Reduce(1)
    private val action27: XyJisonCell = XyJisonCell.Goto(21)
    private val action28: XyJisonCell = XyJisonCell.Reduce(3)
    private val action29: XyJisonCell = XyJisonCell.Reduce(5)
    private val action30: XyJisonCell = XyJisonCell.Reduce(7)
    private val action31: XyJisonCell = XyJisonCell.Goto(22)
    private val action32: XyJisonCell = XyJisonCell.Goto(23)
    private val action33: XyJisonCell = XyJisonCell.Shift(25)
    private val action34: XyJisonCell = XyJisonCell.Goto(24)
    private val action35: XyJisonCell = XyJisonCell.Shift(26)
    private val action36: XyJisonCell = XyJisonCell.Goto(27)
    private val action37: XyJisonCell = XyJisonCell.Shift(28)
    private val action38: XyJisonCell = XyJisonCell.Shift(29)
    private val action39: XyJisonCell = XyJisonCell.Shift(30)
    private val action40: XyJisonCell = XyJisonCell.Shift(31)
    private val action41: XyJisonCell = XyJisonCell.Shift(32)
    private val action42: XyJisonCell = XyJisonCell.Shift(33)
    private val action43: XyJisonCell = XyJisonCell.Shift(34)
    private val action44: XyJisonCell = XyJisonCell.Shift(35)
    private val action45: XyJisonCell = XyJisonCell.Shift(36)
    private val action46: XyJisonCell = XyJisonCell.Shift(37)
    private val action47: XyJisonCell = XyJisonCell.Goto(39)
    private val action48: XyJisonCell = XyJisonCell.Goto(38)
    private val action49: XyJisonCell = XyJisonCell.Shift(43)
    private val action50: XyJisonCell = XyJisonCell.Shift(42)
    private val action51: XyJisonCell = XyJisonCell.Goto(40)
    private val action52: XyJisonCell = XyJisonCell.Goto(41)
    private val action53: XyJisonCell = XyJisonCell.Goto(45)
    private val action54: XyJisonCell = XyJisonCell.Goto(44)
    private val action55: XyJisonCell = XyJisonCell.Shift(47)
    private val action56: XyJisonCell = XyJisonCell.Goto(46)
    private val action57: XyJisonCell = XyJisonCell.Goto(49)
    private val action58: XyJisonCell = XyJisonCell.Goto(48)
    private val action59: XyJisonCell = XyJisonCell.Shift(50)
    private val action60: XyJisonCell = XyJisonCell.Goto(52)
    private val action61: XyJisonCell = XyJisonCell.Goto(51)
    private val action62: XyJisonCell = XyJisonCell.Shift(53)
    private val action63: XyJisonCell = XyJisonCell.Shift(54)
    private val action64: XyJisonCell = XyJisonCell.Reduce(18)
    private val action65: XyJisonCell = XyJisonCell.Reduce(2)
    private val action66: XyJisonCell = XyJisonCell.Reduce(8)
    private val action67: XyJisonCell = XyJisonCell.Reduce(9)
    private val action68: XyJisonCell = XyJisonCell.Reduce(39)
    private val action69: XyJisonCell = XyJisonCell.Goto(55)
    private val action70: XyJisonCell = XyJisonCell.Reduce(40)
    private val action71: XyJisonCell = XyJisonCell.Reduce(41)
    private val action72: XyJisonCell = XyJisonCell.Reduce(42)
    private val action73: XyJisonCell = XyJisonCell.Reduce(44)
    private val action74: XyJisonCell = XyJisonCell.Reduce(45)
    private val action75: XyJisonCell = XyJisonCell.Reduce(46)
    private val action76: XyJisonCell = XyJisonCell.Reduce(47)
    private val action77: XyJisonCell = XyJisonCell.Reduce(48)
    private val action78: XyJisonCell = XyJisonCell.Reduce(49)
    private val action79: XyJisonCell = XyJisonCell.Reduce(50)
    private val action80: XyJisonCell = XyJisonCell.Reduce(51)
    private val action81: XyJisonCell = XyJisonCell.Reduce(52)
    private val action82: XyJisonCell = XyJisonCell.Reduce(53)
    private val action83: XyJisonCell = XyJisonCell.Reduce(10)
    private val action84: XyJisonCell = XyJisonCell.Reduce(24)
    private val action85: XyJisonCell = XyJisonCell.Goto(56)
    private val action86: XyJisonCell = XyJisonCell.Reduce(26)
    private val action87: XyJisonCell = XyJisonCell.Reduce(27)
    private val action88: XyJisonCell = XyJisonCell.Shift(57)
    private val action89: XyJisonCell = XyJisonCell.Goto(59)
    private val action90: XyJisonCell = XyJisonCell.Goto(58)
    private val action91: XyJisonCell = XyJisonCell.Reduce(11)
    private val action92: XyJisonCell = XyJisonCell.Reduce(32)
    private val action93: XyJisonCell = XyJisonCell.Goto(60)
    private val action94: XyJisonCell = XyJisonCell.Reduce(34)
    private val action95: XyJisonCell = XyJisonCell.Shift(61)
    private val action96: XyJisonCell = XyJisonCell.Reduce(12)
    private val action97: XyJisonCell = XyJisonCell.Goto(62)
    private val action98: XyJisonCell = XyJisonCell.Goto(63)
    private val action99: XyJisonCell = XyJisonCell.Goto(64)
    private val action100: XyJisonCell = XyJisonCell.Shift(65)
    private val action101: XyJisonCell = XyJisonCell.Reduce(14)
    private val action102: XyJisonCell = XyJisonCell.Goto(66)
    private val action103: XyJisonCell = XyJisonCell.Reduce(16)
    private val action104: XyJisonCell = XyJisonCell.Reduce(17)
    private val action105: XyJisonCell = XyJisonCell.Reduce(43)
    private val action106: XyJisonCell = XyJisonCell.Reduce(25)
    private val action107: XyJisonCell = XyJisonCell.Shift(67)
    private val action108: XyJisonCell = XyJisonCell.Shift(68)
    private val action109: XyJisonCell = XyJisonCell.Reduce(31)
    private val action110: XyJisonCell = XyJisonCell.Shift(69)
    private val action111: XyJisonCell = XyJisonCell.Reduce(33)
    private val action112: XyJisonCell = XyJisonCell.Shift(70)
    private val action113: XyJisonCell = XyJisonCell.Reduce(13)
    private val action114: XyJisonCell = XyJisonCell.Shift(71)
    private val action115: XyJisonCell = XyJisonCell.Reduce(21)
    private val action116: XyJisonCell = XyJisonCell.Shift(72)
    private val action117: XyJisonCell = XyJisonCell.Reduce(23)
    private val action118: XyJisonCell = XyJisonCell.Shift(73)
    private val action119: XyJisonCell = XyJisonCell.Reduce(15)
    private val action120: XyJisonCell = XyJisonCell.Reduce(28)
    private val action121: XyJisonCell = XyJisonCell.Reduce(29)
    private val action122: XyJisonCell = XyJisonCell.Goto(74)
    private val action123: XyJisonCell = XyJisonCell.Reduce(35)
    private val action124: XyJisonCell = XyJisonCell.Reduce(19)
    private val action125: XyJisonCell = XyJisonCell.Goto(75)
    private val action126: XyJisonCell = XyJisonCell.Reduce(22)
    private val action127: XyJisonCell = XyJisonCell.Reduce(30)
    private val action128: XyJisonCell = XyJisonCell.Reduce(20)
    private val symbols0 = intArrayOf(1, 3, 4, 5, 7, 10, 12, 14, 16, 18, 19, 21, 23, 36, 37, 38)
    private val symbols1 = intArrayOf(1, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 18, 19, 21, 23, 36, 37, 38)
    private val symbols2 = intArrayOf(1, 5, 10, 12, 14, 16, 18, 19, 21, 23, 36, 37, 38)
    private val symbols3 = intArrayOf(1, 4, 10, 12, 14, 16, 18, 19, 21, 23, 36, 37, 38)
    private val symbols4 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 36, 37, 38)
    private val symbols5 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 24, 26, 28, 29, 36, 37, 38, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51)
    private val symbols6 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 24, 26, 28, 29, 36, 37, 38)
    private val symbols7 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 24, 26, 28, 29, 36, 37, 38, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51)
    private val symbols8 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 24, 29, 31, 32, 36, 37, 38)
    private val symbols9 = intArrayOf(1, 10, 12, 14, 16, 18, 19, 21, 23, 29, 35, 36, 37, 38)

    val states: Array<Map<Int, XyJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
        addAll(stateChunk3())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, XyJisonCell>> = arrayOf(
        buildJisonState(symbols0, action0, mapOf(
            3 to action1,
            4 to action2,
            5 to action3,
            7 to action4,
            36 to action5,
            37 to action6,
            38 to action7,
        )),
        mapOf(
            1 to action8,
        ),
        buildJisonState(symbols0, action0, mapOf(
            3 to action9,
            4 to action2,
            5 to action3,
            7 to action4,
            36 to action5,
            37 to action6,
            38 to action7,
        )),
        buildJisonState(symbols1, action0, mapOf(
            3 to action10,
            4 to action2,
            5 to action3,
            6 to action11,
            7 to action4,
            8 to action12,
            36 to action5,
            37 to action6,
            38 to action7,
        )),
        mapOf(
            1 to action13,
            9 to action14,
            10 to action15,
            12 to action16,
            14 to action17,
            16 to action18,
            18 to action19,
            19 to action20,
            21 to action21,
            23 to action22,
        ),
        buildJisonState(symbols2, action23),
        buildJisonState(symbols2, action24),
        buildJisonState(symbols2, action25),
        mapOf(
            1 to action26,
        ),
        buildJisonState(symbols0, action0, mapOf(
            3 to action27,
            4 to action2,
            5 to action3,
            7 to action4,
            36 to action5,
            37 to action6,
            38 to action7,
        )),
        mapOf(
            1 to action28,
        ),
        buildJisonState(symbols2, action29),
        buildJisonState(symbols3, action30, mapOf(
            4 to action31,
            36 to action5,
            37 to action6,
            38 to action7,
        )),
        mapOf(
            11 to action32,
            30 to action33,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        mapOf(
            11 to action47,
            13 to action48,
            24 to action49,
            29 to action50,
            30 to action33,
            31 to action51,
            32 to action52,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        mapOf(
            11 to action53,
            15 to action54,
            29 to action55,
            30 to action33,
            35 to action56,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        mapOf(
            11 to action57,
            17 to action58,
            24 to action59,
            30 to action33,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        mapOf(
            11 to action60,
            17 to action61,
            24 to action59,
            30 to action33,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        mapOf(
            20 to action62,
        ),
        mapOf(
            22 to action63,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, XyJisonCell>> = arrayOf(
        buildJisonState(symbols4, action64),
        mapOf(
            1 to action65,
        ),
        buildJisonState(symbols4, action66),
        buildJisonState(symbols4, action67),
        buildJisonState(symbols5, action68, mapOf(
            41 to action69,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        )),
        buildJisonState(symbols6, action70),
        buildJisonState(symbols6, action71),
        buildJisonState(symbols7, action72),
        buildJisonState(symbols7, action73),
        buildJisonState(symbols7, action74),
        buildJisonState(symbols7, action75),
        buildJisonState(symbols7, action76),
        buildJisonState(symbols7, action77),
        buildJisonState(symbols7, action78),
        buildJisonState(symbols7, action79),
        buildJisonState(symbols7, action80),
        buildJisonState(symbols7, action81),
        buildJisonState(symbols7, action82),
        buildJisonState(symbols4, action83),
        buildJisonState(symbols8, action84, mapOf(
            24 to action49,
            29 to action50,
            31 to action85,
            32 to action52,
        )),
    )

    private fun stateChunk2(): Array<Map<Int, XyJisonCell>> = arrayOf(
        buildJisonState(symbols4, action86),
        buildJisonState(symbols4, action87),
        mapOf(
            33 to action88,
        ),
        mapOf(
            11 to action89,
            30 to action33,
            34 to action90,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        buildJisonState(symbols4, action91),
        buildJisonState(symbols9, action92, mapOf(
            29 to action55,
            35 to action93,
        )),
        buildJisonState(symbols4, action94),
        mapOf(
            33 to action95,
        ),
        buildJisonState(symbols4, action96),
        mapOf(
            17 to action97,
            24 to action59,
        ),
        mapOf(
            25 to action98,
            27 to action99,
            29 to action100,
        ),
        buildJisonState(symbols4, action101),
        mapOf(
            17 to action102,
            24 to action59,
        ),
        buildJisonState(symbols4, action103),
        buildJisonState(symbols4, action104),
        buildJisonState(symbols7, action105),
        buildJisonState(symbols4, action106),
        mapOf(
            29 to action107,
        ),
        mapOf(
            26 to action108,
        ),
        mapOf(
            26 to action109,
            28 to action110,
        ),
    )

    private fun stateChunk3(): Array<Map<Int, XyJisonCell>> = arrayOf(
        buildJisonState(symbols4, action111),
        mapOf(
            29 to action112,
        ),
        buildJisonState(symbols4, action113),
        mapOf(
            26 to action114,
        ),
        mapOf(
            26 to action115,
            28 to action116,
        ),
        mapOf(
            26 to action117,
            28 to action117,
            30 to action118,
        ),
        buildJisonState(symbols4, action119),
        buildJisonState(symbols4, action120),
        buildJisonState(symbols4, action121),
        mapOf(
            11 to action89,
            30 to action33,
            34 to action122,
            39 to action34,
            40 to action35,
            41 to action36,
            42 to action37,
            43 to action38,
            44 to action39,
            45 to action40,
            46 to action41,
            47 to action42,
            48 to action43,
            49 to action44,
            50 to action45,
            51 to action46,
        ),
        buildJisonState(symbols4, action123),
        buildJisonState(symbols4, action124),
        mapOf(
            25 to action125,
            27 to action99,
            29 to action100,
        ),
        mapOf(
            26 to action126,
            28 to action126,
        ),
        mapOf(
            26 to action127,
        ),
        mapOf(
            26 to action128,
        ),
    )

    val lexerPatterns: List<XyJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
    }

    private fun lexerChunk0(): List<XyJisonLexerPattern> = listOf(
        XyJisonLexerPattern(0, "^(?:%%(?!\\{)[^\\n]*)"),
        XyJisonLexerPattern(1, "^(?:[^\\}]%%[^\\n]*)"),
        XyJisonLexerPattern(2, "^(?:(\\r?\\n))"),
        XyJisonLexerPattern(3, "^(?:(\\r?\\n))"),
        XyJisonLexerPattern(4, "^(?:[\\n\\r]+)"),
        XyJisonLexerPattern(5, "^(?:%%[^\\n]*)"),
        XyJisonLexerPattern(6, "^(?:title\\b)"),
        XyJisonLexerPattern(7, "^(?:accTitle\\s*:\\s*)"),
        XyJisonLexerPattern(8, "^(?:(?!\\n||)*[^\\n]*)"),
        XyJisonLexerPattern(9, "^(?:accDescr\\s*:\\s*)"),
        XyJisonLexerPattern(10, "^(?:(?!\\n||)*[^\\n]*)"),
        XyJisonLexerPattern(11, "^(?:accDescr\\s*\\{\\s*)"),
        XyJisonLexerPattern(12, "^(?:\\})"),
        XyJisonLexerPattern(13, "^(?:[^\\}]*)"),
        XyJisonLexerPattern(14, "^(?:xychart-beta\\b)"),
        XyJisonLexerPattern(15, "^(?:xychart\\b)"),
        XyJisonLexerPattern(16, "^(?:(?:vertical|horizontal))"),
        XyJisonLexerPattern(17, "^(?:x-axis\\b)"),
        XyJisonLexerPattern(18, "^(?:y-axis\\b)"),
        XyJisonLexerPattern(19, "^(?:\\[)"),
    )

    private fun lexerChunk1(): List<XyJisonLexerPattern> = listOf(
        XyJisonLexerPattern(20, "^(?:-->)"),
        XyJisonLexerPattern(21, "^(?:line\\b)"),
        XyJisonLexerPattern(22, "^(?:bar\\b)"),
        XyJisonLexerPattern(23, "^(?:\\[)"),
        XyJisonLexerPattern(24, "^(?:[+-]?(?:\\d+(?:\\.\\d+)?|\\.\\d+))"),
        XyJisonLexerPattern(25, "^(?:\\])"),
        XyJisonLexerPattern(26, "^(?:(?:`\\)                                    \\{ this\\.pushState\\(md_string\\); \\}\\n<md_string>\\(\\?:\\(\\?!`\"\\)\\.\\)\\+                  \\{ return MD_STR; \\}\\n<md_string>\\(\\?:`))"),
        XyJisonLexerPattern(27, "^(?:[\"])"),
        XyJisonLexerPattern(28, "^(?:[\"])"),
        XyJisonLexerPattern(29, "^(?:[^\"]*)"),
        XyJisonLexerPattern(30, "^(?:\\[)"),
        XyJisonLexerPattern(31, "^(?:\\])"),
        XyJisonLexerPattern(32, "^(?:[A-Za-z]+)"),
        XyJisonLexerPattern(33, "^(?::)"),
        XyJisonLexerPattern(34, "^(?:\\+)"),
        XyJisonLexerPattern(35, "^(?:,)"),
        XyJisonLexerPattern(36, "^(?:=)"),
        XyJisonLexerPattern(37, "^(?:\\*)"),
        XyJisonLexerPattern(38, "^(?:#)"),
        XyJisonLexerPattern(39, "^(?:[\\_])"),
    )

    private fun lexerChunk2(): List<XyJisonLexerPattern> = listOf(
        XyJisonLexerPattern(40, "^(?:\\.)"),
        XyJisonLexerPattern(41, "^(?:&)"),
        XyJisonLexerPattern(42, "^(?:-)"),
        XyJisonLexerPattern(43, "^(?:[0-9]+)"),
        XyJisonLexerPattern(44, "^(?:\\s+)"),
        XyJisonLexerPattern(45, "^(?:;)"),
        XyJisonLexerPattern(46, "^(?:\$)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "data_inner" to intArrayOf(0, 1, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 21, 22, 24, 25, 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46),
        "data" to intArrayOf(0, 1, 3, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 21, 22, 23, 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46),
        "axis_band_data" to intArrayOf(0, 1, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 21, 22, 25, 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46),
        "axis_data" to intArrayOf(0, 1, 2, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24, 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46),
        "acc_descr_multiline" to intArrayOf(12, 13),
        "acc_descr" to intArrayOf(10),
        "acc_title" to intArrayOf(8),
        "title" to intArrayOf(),
        "md_string" to intArrayOf(),
        "string" to intArrayOf(28, 29),
        "INITIAL" to intArrayOf(0, 1, 4, 5, 6, 7, 9, 11, 14, 15, 16, 17, 18, 21, 22, 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46),
    )
}
