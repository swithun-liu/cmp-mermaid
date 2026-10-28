package com.swithun.cmpmermaid.core.gantt.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 gantt.jison.
 * Upstream gantt.jison SHA-256: 14d325aa626a7fa220c341a75faecc712d0121203e0f88db6c11e68f0bc7e82f
 * Distribution chunk SHA-256: f60134c37d88d5058ba503a2dedf5eab6f280b5d6d1d19aedbb0a85a67be409d
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:gantt-parser
 */
internal object GanttJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "gantt" to 4,
        "document" to 5,
        "EOF" to 6,
        "line" to 7,
        "SPACE" to 8,
        "statement" to 9,
        "NL" to 10,
        "weekday" to 11,
        "weekday_monday" to 12,
        "weekday_tuesday" to 13,
        "weekday_wednesday" to 14,
        "weekday_thursday" to 15,
        "weekday_friday" to 16,
        "weekday_saturday" to 17,
        "weekday_sunday" to 18,
        "weekend" to 19,
        "weekend_friday" to 20,
        "weekend_saturday" to 21,
        "dateFormat" to 22,
        "inclusiveEndDates" to 23,
        "topAxis" to 24,
        "axisFormat" to 25,
        "tickInterval" to 26,
        "excludes" to 27,
        "includes" to 28,
        "todayMarker" to 29,
        "title" to 30,
        "acc_title" to 31,
        "acc_title_value" to 32,
        "acc_descr" to 33,
        "acc_descr_value" to 34,
        "acc_descr_multiline_value" to 35,
        "section" to 36,
        "clickStatement" to 37,
        "taskTxt" to 38,
        "taskData" to 39,
        "click" to 40,
        "callbackname" to 41,
        "callbackargs" to 42,
        "href" to 43,
        "clickStatementDebug" to 44,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "gantt",
        6 to "EOF",
        8 to "SPACE",
        10 to "NL",
        12 to "weekday_monday",
        13 to "weekday_tuesday",
        14 to "weekday_wednesday",
        15 to "weekday_thursday",
        16 to "weekday_friday",
        17 to "weekday_saturday",
        18 to "weekday_sunday",
        20 to "weekend_friday",
        21 to "weekend_saturday",
        22 to "dateFormat",
        23 to "inclusiveEndDates",
        24 to "topAxis",
        25 to "axisFormat",
        26 to "tickInterval",
        27 to "excludes",
        28 to "includes",
        29 to "todayMarker",
        30 to "title",
        31 to "acc_title",
        32 to "acc_title_value",
        33 to "acc_descr",
        34 to "acc_descr_value",
        35 to "acc_descr_multiline_value",
        36 to "section",
        38 to "taskTxt",
        39 to "taskData",
        40 to "click",
        41 to "callbackname",
        42 to "callbackargs",
        43 to "href",
    )

    val productions: Array<GanttJisonProduction> = arrayOf(
        GanttJisonProduction(symbol = 0, length = 0),
        GanttJisonProduction(symbol = 3, length = 3),
        GanttJisonProduction(symbol = 5, length = 0),
        GanttJisonProduction(symbol = 5, length = 2),
        GanttJisonProduction(symbol = 7, length = 2),
        GanttJisonProduction(symbol = 7, length = 1),
        GanttJisonProduction(symbol = 7, length = 1),
        GanttJisonProduction(symbol = 7, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 11, length = 1),
        GanttJisonProduction(symbol = 19, length = 1),
        GanttJisonProduction(symbol = 19, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 2),
        GanttJisonProduction(symbol = 9, length = 2),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 1),
        GanttJisonProduction(symbol = 9, length = 2),
        GanttJisonProduction(symbol = 37, length = 2),
        GanttJisonProduction(symbol = 37, length = 3),
        GanttJisonProduction(symbol = 37, length = 3),
        GanttJisonProduction(symbol = 37, length = 4),
        GanttJisonProduction(symbol = 37, length = 3),
        GanttJisonProduction(symbol = 37, length = 4),
        GanttJisonProduction(symbol = 37, length = 2),
        GanttJisonProduction(symbol = 44, length = 2),
        GanttJisonProduction(symbol = 44, length = 3),
        GanttJisonProduction(symbol = 44, length = 3),
        GanttJisonProduction(symbol = 44, length = 4),
        GanttJisonProduction(symbol = 44, length = 3),
        GanttJisonProduction(symbol = 44, length = 4),
        GanttJisonProduction(symbol = 44, length = 2),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: GanttJisonCell = GanttJisonCell.Goto(1)
    private val action1: GanttJisonCell = GanttJisonCell.Shift(2)
    private val action2: GanttJisonCell = GanttJisonCell.Accept
    private val action3: GanttJisonCell = GanttJisonCell.Goto(3)
    private val action4: GanttJisonCell = GanttJisonCell.Reduce(2)
    private val action5: GanttJisonCell = GanttJisonCell.Shift(4)
    private val action6: GanttJisonCell = GanttJisonCell.Goto(5)
    private val action7: GanttJisonCell = GanttJisonCell.Shift(6)
    private val action8: GanttJisonCell = GanttJisonCell.Goto(7)
    private val action9: GanttJisonCell = GanttJisonCell.Shift(8)
    private val action10: GanttJisonCell = GanttJisonCell.Goto(17)
    private val action11: GanttJisonCell = GanttJisonCell.Shift(26)
    private val action12: GanttJisonCell = GanttJisonCell.Shift(27)
    private val action13: GanttJisonCell = GanttJisonCell.Shift(28)
    private val action14: GanttJisonCell = GanttJisonCell.Shift(29)
    private val action15: GanttJisonCell = GanttJisonCell.Shift(30)
    private val action16: GanttJisonCell = GanttJisonCell.Shift(31)
    private val action17: GanttJisonCell = GanttJisonCell.Shift(32)
    private val action18: GanttJisonCell = GanttJisonCell.Goto(18)
    private val action19: GanttJisonCell = GanttJisonCell.Shift(33)
    private val action20: GanttJisonCell = GanttJisonCell.Shift(34)
    private val action21: GanttJisonCell = GanttJisonCell.Shift(9)
    private val action22: GanttJisonCell = GanttJisonCell.Shift(10)
    private val action23: GanttJisonCell = GanttJisonCell.Shift(11)
    private val action24: GanttJisonCell = GanttJisonCell.Shift(12)
    private val action25: GanttJisonCell = GanttJisonCell.Shift(13)
    private val action26: GanttJisonCell = GanttJisonCell.Shift(14)
    private val action27: GanttJisonCell = GanttJisonCell.Shift(15)
    private val action28: GanttJisonCell = GanttJisonCell.Shift(16)
    private val action29: GanttJisonCell = GanttJisonCell.Shift(19)
    private val action30: GanttJisonCell = GanttJisonCell.Shift(20)
    private val action31: GanttJisonCell = GanttJisonCell.Shift(21)
    private val action32: GanttJisonCell = GanttJisonCell.Shift(22)
    private val action33: GanttJisonCell = GanttJisonCell.Shift(23)
    private val action34: GanttJisonCell = GanttJisonCell.Goto(24)
    private val action35: GanttJisonCell = GanttJisonCell.Shift(25)
    private val action36: GanttJisonCell = GanttJisonCell.Shift(35)
    private val action37: GanttJisonCell = GanttJisonCell.Reduce(1)
    private val action38: GanttJisonCell = GanttJisonCell.Reduce(7)
    private val action39: GanttJisonCell = GanttJisonCell.Reduce(3)
    private val action40: GanttJisonCell = GanttJisonCell.Goto(36)
    private val action41: GanttJisonCell = GanttJisonCell.Reduce(5)
    private val action42: GanttJisonCell = GanttJisonCell.Reduce(6)
    private val action43: GanttJisonCell = GanttJisonCell.Reduce(17)
    private val action44: GanttJisonCell = GanttJisonCell.Reduce(18)
    private val action45: GanttJisonCell = GanttJisonCell.Reduce(19)
    private val action46: GanttJisonCell = GanttJisonCell.Reduce(20)
    private val action47: GanttJisonCell = GanttJisonCell.Reduce(21)
    private val action48: GanttJisonCell = GanttJisonCell.Reduce(22)
    private val action49: GanttJisonCell = GanttJisonCell.Reduce(23)
    private val action50: GanttJisonCell = GanttJisonCell.Reduce(24)
    private val action51: GanttJisonCell = GanttJisonCell.Reduce(25)
    private val action52: GanttJisonCell = GanttJisonCell.Reduce(26)
    private val action53: GanttJisonCell = GanttJisonCell.Reduce(27)
    private val action54: GanttJisonCell = GanttJisonCell.Shift(37)
    private val action55: GanttJisonCell = GanttJisonCell.Shift(38)
    private val action56: GanttJisonCell = GanttJisonCell.Reduce(30)
    private val action57: GanttJisonCell = GanttJisonCell.Reduce(31)
    private val action58: GanttJisonCell = GanttJisonCell.Reduce(32)
    private val action59: GanttJisonCell = GanttJisonCell.Shift(39)
    private val action60: GanttJisonCell = GanttJisonCell.Reduce(8)
    private val action61: GanttJisonCell = GanttJisonCell.Reduce(9)
    private val action62: GanttJisonCell = GanttJisonCell.Reduce(10)
    private val action63: GanttJisonCell = GanttJisonCell.Reduce(11)
    private val action64: GanttJisonCell = GanttJisonCell.Reduce(12)
    private val action65: GanttJisonCell = GanttJisonCell.Reduce(13)
    private val action66: GanttJisonCell = GanttJisonCell.Reduce(14)
    private val action67: GanttJisonCell = GanttJisonCell.Reduce(15)
    private val action68: GanttJisonCell = GanttJisonCell.Reduce(16)
    private val action69: GanttJisonCell = GanttJisonCell.Shift(40)
    private val action70: GanttJisonCell = GanttJisonCell.Shift(41)
    private val action71: GanttJisonCell = GanttJisonCell.Reduce(4)
    private val action72: GanttJisonCell = GanttJisonCell.Reduce(28)
    private val action73: GanttJisonCell = GanttJisonCell.Reduce(29)
    private val action74: GanttJisonCell = GanttJisonCell.Reduce(33)
    private val action75: GanttJisonCell = GanttJisonCell.Reduce(34)
    private val action76: GanttJisonCell = GanttJisonCell.Shift(42)
    private val action77: GanttJisonCell = GanttJisonCell.Shift(43)
    private val action78: GanttJisonCell = GanttJisonCell.Reduce(40)
    private val action79: GanttJisonCell = GanttJisonCell.Shift(44)
    private val action80: GanttJisonCell = GanttJisonCell.Reduce(35)
    private val action81: GanttJisonCell = GanttJisonCell.Shift(45)
    private val action82: GanttJisonCell = GanttJisonCell.Reduce(36)
    private val action83: GanttJisonCell = GanttJisonCell.Reduce(38)
    private val action84: GanttJisonCell = GanttJisonCell.Shift(46)
    private val action85: GanttJisonCell = GanttJisonCell.Reduce(37)
    private val action86: GanttJisonCell = GanttJisonCell.Reduce(39)
    private val symbols0 = intArrayOf(5, 6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40)
    private val symbols1 = intArrayOf(1, 6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40)
    private val symbols2 = intArrayOf(6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40)
    private val symbols3 = intArrayOf(6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40, 42, 43)
    private val symbols4 = intArrayOf(6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40, 41)
    private val symbols5 = intArrayOf(6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40, 43)
    private val symbols6 = intArrayOf(6, 8, 10, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36, 38, 40, 42)

    val states: Array<Map<Int, GanttJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, GanttJisonCell>> = arrayOf(
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
            13 to action12,
            14 to action13,
            15 to action14,
            16 to action15,
            17 to action16,
            18 to action17,
            19 to action18,
            20 to action19,
            21 to action20,
            22 to action21,
            23 to action22,
            24 to action23,
            25 to action24,
            26 to action25,
            27 to action26,
            28 to action27,
            29 to action28,
            30 to action29,
            31 to action30,
            33 to action31,
            35 to action32,
            36 to action33,
            37 to action34,
            38 to action35,
            40 to action36,
        ),
        buildJisonState(symbols1, action38, mapOf(
            1 to action37,
        )),
        buildJisonState(symbols2, action39),
        mapOf(
            9 to action40,
            11 to action10,
            12 to action11,
            13 to action12,
            14 to action13,
            15 to action14,
            16 to action15,
            17 to action16,
            18 to action17,
            19 to action18,
            20 to action19,
            21 to action20,
            22 to action21,
            23 to action22,
            24 to action23,
            25 to action24,
            26 to action25,
            27 to action26,
            28 to action27,
            29 to action28,
            30 to action29,
            31 to action30,
            33 to action31,
            35 to action32,
            36 to action33,
            37 to action34,
            38 to action35,
            40 to action36,
        ),
        buildJisonState(symbols2, action41),
        buildJisonState(symbols2, action42),
        buildJisonState(symbols2, action43),
        buildJisonState(symbols2, action44),
        buildJisonState(symbols2, action45),
        buildJisonState(symbols2, action46),
        buildJisonState(symbols2, action47),
        buildJisonState(symbols2, action48),
        buildJisonState(symbols2, action49),
        buildJisonState(symbols2, action50),
        buildJisonState(symbols2, action51),
        buildJisonState(symbols2, action52),
        buildJisonState(symbols2, action53),
    )

    private fun stateChunk1(): Array<Map<Int, GanttJisonCell>> = arrayOf(
        mapOf(
            32 to action54,
        ),
        mapOf(
            34 to action55,
        ),
        buildJisonState(symbols2, action56),
        buildJisonState(symbols2, action57),
        buildJisonState(symbols2, action58),
        mapOf(
            39 to action59,
        ),
        buildJisonState(symbols2, action60),
        buildJisonState(symbols2, action61),
        buildJisonState(symbols2, action62),
        buildJisonState(symbols2, action63),
        buildJisonState(symbols2, action64),
        buildJisonState(symbols2, action65),
        buildJisonState(symbols2, action66),
        buildJisonState(symbols2, action67),
        buildJisonState(symbols2, action68),
        mapOf(
            41 to action69,
            43 to action70,
        ),
        buildJisonState(symbols2, action71),
        buildJisonState(symbols2, action72),
        buildJisonState(symbols2, action73),
        buildJisonState(symbols2, action74),
    )

    private fun stateChunk2(): Array<Map<Int, GanttJisonCell>> = arrayOf(
        buildJisonState(symbols3, action75, mapOf(
            42 to action76,
            43 to action77,
        )),
        buildJisonState(symbols4, action78, mapOf(
            41 to action79,
        )),
        buildJisonState(symbols5, action80, mapOf(
            43 to action81,
        )),
        buildJisonState(symbols2, action82),
        buildJisonState(symbols6, action83, mapOf(
            42 to action84,
        )),
        buildJisonState(symbols2, action85),
        buildJisonState(symbols2, action86),
    )

    val lexerPatterns: List<GanttJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
    }

    private fun lexerChunk0(): List<GanttJisonLexerPattern> = listOf(
        GanttJisonLexerPattern(0, "^(?:%%\\{)"),
        GanttJisonLexerPattern(1, "^(?:accTitle\\s*:\\s*)"),
        GanttJisonLexerPattern(2, "^(?:(?!\\n||)*[^\\n]*)"),
        GanttJisonLexerPattern(3, "^(?:accDescr\\s*:\\s*)"),
        GanttJisonLexerPattern(4, "^(?:(?!\\n||)*[^\\n]*)"),
        GanttJisonLexerPattern(5, "^(?:accDescr\\s*\\{\\s*)"),
        GanttJisonLexerPattern(6, "^(?:[\\}])"),
        GanttJisonLexerPattern(7, "^(?:[^\\}]*)"),
        GanttJisonLexerPattern(8, "^(?:%%(?!\\{)*[^\\n]*)"),
        GanttJisonLexerPattern(9, "^(?:[^\\}]%%*[^\\n]*)"),
        GanttJisonLexerPattern(10, "^(?:%%*[^\\n]*[\\n]*)"),
        GanttJisonLexerPattern(11, "^(?:[\\n]+)"),
        GanttJisonLexerPattern(12, "^(?:\\s+)"),
        GanttJisonLexerPattern(13, "^(?:%[^\\n]*)"),
        GanttJisonLexerPattern(14, "^(?:href[\\s]+[\"])"),
        GanttJisonLexerPattern(15, "^(?:[\"])"),
        GanttJisonLexerPattern(16, "^(?:[^\"]*)"),
        GanttJisonLexerPattern(17, "^(?:call[\\s]+)"),
        GanttJisonLexerPattern(18, "^(?:\\([\\s]*\\))"),
        GanttJisonLexerPattern(19, "^(?:\\()"),
    )

    private fun lexerChunk1(): List<GanttJisonLexerPattern> = listOf(
        GanttJisonLexerPattern(20, "^(?:[^(]*)"),
        GanttJisonLexerPattern(21, "^(?:\\))"),
        GanttJisonLexerPattern(22, "^(?:[^)]*)"),
        GanttJisonLexerPattern(23, "^(?:click[\\s]+)"),
        GanttJisonLexerPattern(24, "^(?:[\\s\\n])"),
        GanttJisonLexerPattern(25, "^(?:[^\\s\\n]*)"),
        GanttJisonLexerPattern(26, "^(?:gantt\\b)"),
        GanttJisonLexerPattern(27, "^(?:dateFormat\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(28, "^(?:inclusiveEndDates\\b)"),
        GanttJisonLexerPattern(29, "^(?:topAxis\\b)"),
        GanttJisonLexerPattern(30, "^(?:axisFormat\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(31, "^(?:tickInterval\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(32, "^(?:includes\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(33, "^(?:excludes\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(34, "^(?:todayMarker\\s[^\\n;]+)"),
        GanttJisonLexerPattern(35, "^(?:weekday\\s+monday\\b)"),
        GanttJisonLexerPattern(36, "^(?:weekday\\s+tuesday\\b)"),
        GanttJisonLexerPattern(37, "^(?:weekday\\s+wednesday\\b)"),
        GanttJisonLexerPattern(38, "^(?:weekday\\s+thursday\\b)"),
        GanttJisonLexerPattern(39, "^(?:weekday\\s+friday\\b)"),
    )

    private fun lexerChunk2(): List<GanttJisonLexerPattern> = listOf(
        GanttJisonLexerPattern(40, "^(?:weekday\\s+saturday\\b)"),
        GanttJisonLexerPattern(41, "^(?:weekday\\s+sunday\\b)"),
        GanttJisonLexerPattern(42, "^(?:weekend\\s+friday\\b)"),
        GanttJisonLexerPattern(43, "^(?:weekend\\s+saturday\\b)"),
        GanttJisonLexerPattern(44, "^(?:\\d\\d\\d\\d-\\d\\d-\\d\\d\\b)"),
        GanttJisonLexerPattern(45, "^(?:title\\s[^\\n]+)"),
        GanttJisonLexerPattern(46, "^(?:accDescription\\s[^#\\n;]+)"),
        GanttJisonLexerPattern(47, "^(?:section\\s[^\\n]+)"),
        GanttJisonLexerPattern(48, "^(?:[^:\\n]+)"),
        GanttJisonLexerPattern(49, "^(?::[^#\\n;]+)"),
        GanttJisonLexerPattern(50, "^(?::)"),
        GanttJisonLexerPattern(51, "^(?:\$)"),
        GanttJisonLexerPattern(52, "^(?:.)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "acc_descr_multiline" to intArrayOf(6, 7),
        "acc_descr" to intArrayOf(4),
        "acc_title" to intArrayOf(2),
        "callbackargs" to intArrayOf(21, 22),
        "callbackname" to intArrayOf(18, 19, 20),
        "href" to intArrayOf(15, 16),
        "click" to intArrayOf(24, 25),
        "INITIAL" to intArrayOf(0, 1, 3, 5, 8, 9, 10, 11, 12, 13, 14, 17, 23, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52),
    )
}
