package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 flow.jison.
 * Upstream flow.jison SHA-256: e9891eb4f646140e5110e72ef5f79b099677a265ba1a84884b39a573b56d1c68
 * Distribution chunk SHA-256: d276e4b92a7cc5b39bb1b619648f9fc3eb3993ff288429fcba27c2994c794234
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:flow-parser
 */
internal object FlowJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "graphConfig" to 4,
        "document" to 5,
        "line" to 6,
        "statement" to 7,
        "SEMI" to 8,
        "NEWLINE" to 9,
        "SPACE" to 10,
        "EOF" to 11,
        "GRAPH" to 12,
        "NODIR" to 13,
        "DIR" to 14,
        "FirstStmtSeparator" to 15,
        "ending" to 16,
        "endToken" to 17,
        "spaceList" to 18,
        "spaceListNewline" to 19,
        "vertexStatement" to 20,
        "separator" to 21,
        "styleStatement" to 22,
        "linkStyleStatement" to 23,
        "classDefStatement" to 24,
        "classStatement" to 25,
        "clickStatement" to 26,
        "subgraph" to 27,
        "textNoTags" to 28,
        "SQS" to 29,
        "text" to 30,
        "SQE" to 31,
        "end" to 32,
        "direction" to 33,
        "acc_title" to 34,
        "acc_title_value" to 35,
        "acc_descr" to 36,
        "acc_descr_value" to 37,
        "acc_descr_multiline_value" to 38,
        "shapeData" to 39,
        "SHAPE_DATA" to 40,
        "link" to 41,
        "node" to 42,
        "styledVertex" to 43,
        "AMP" to 44,
        "vertex" to 45,
        "STYLE_SEPARATOR" to 46,
        "idString" to 47,
        "DOUBLECIRCLESTART" to 48,
        "DOUBLECIRCLEEND" to 49,
        "PS" to 50,
        "PE" to 51,
        "(-" to 52,
        "-)" to 53,
        "STADIUMSTART" to 54,
        "STADIUMEND" to 55,
        "SUBROUTINESTART" to 56,
        "SUBROUTINEEND" to 57,
        "VERTEX_WITH_PROPS_START" to 58,
        "NODE_STRING[field]" to 59,
        "COLON" to 60,
        "NODE_STRING[value]" to 61,
        "PIPE" to 62,
        "CYLINDERSTART" to 63,
        "CYLINDEREND" to 64,
        "DIAMOND_START" to 65,
        "DIAMOND_STOP" to 66,
        "TAGEND" to 67,
        "TRAPSTART" to 68,
        "TRAPEND" to 69,
        "INVTRAPSTART" to 70,
        "INVTRAPEND" to 71,
        "linkStatement" to 72,
        "arrowText" to 73,
        "TESTSTR" to 74,
        "START_LINK" to 75,
        "edgeText" to 76,
        "LINK" to 77,
        "LINK_ID" to 78,
        "edgeTextToken" to 79,
        "STR" to 80,
        "MD_STR" to 81,
        "textToken" to 82,
        "keywords" to 83,
        "STYLE" to 84,
        "LINKSTYLE" to 85,
        "CLASSDEF" to 86,
        "CLASS" to 87,
        "CLICK" to 88,
        "DOWN" to 89,
        "UP" to 90,
        "textNoTagsToken" to 91,
        "stylesOpt" to 92,
        "idString[vertex]" to 93,
        "idString[class]" to 94,
        "CALLBACKNAME" to 95,
        "CALLBACKARGS" to 96,
        "HREF" to 97,
        "LINK_TARGET" to 98,
        "STR[link]" to 99,
        "STR[tooltip]" to 100,
        "alphaNum" to 101,
        "DEFAULT" to 102,
        "numList" to 103,
        "INTERPOLATE" to 104,
        "NUM" to 105,
        "COMMA" to 106,
        "style" to 107,
        "styleComponent" to 108,
        "NODE_STRING" to 109,
        "UNIT" to 110,
        "BRKT" to 111,
        "PCT" to 112,
        "idStringToken" to 113,
        "MINUS" to 114,
        "MULT" to 115,
        "UNICODE_TEXT" to 116,
        "TEXT" to 117,
        "TAGSTART" to 118,
        "EDGE_TEXT" to 119,
        "alphaNumToken" to 120,
        "direction_tb" to 121,
        "direction_bt" to 122,
        "direction_rl" to 123,
        "direction_lr" to 124,
        "direction_td" to 125,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        8 to "SEMI",
        9 to "NEWLINE",
        10 to "SPACE",
        11 to "EOF",
        12 to "GRAPH",
        13 to "NODIR",
        14 to "DIR",
        27 to "subgraph",
        29 to "SQS",
        31 to "SQE",
        32 to "end",
        34 to "acc_title",
        35 to "acc_title_value",
        36 to "acc_descr",
        37 to "acc_descr_value",
        38 to "acc_descr_multiline_value",
        40 to "SHAPE_DATA",
        44 to "AMP",
        46 to "STYLE_SEPARATOR",
        48 to "DOUBLECIRCLESTART",
        49 to "DOUBLECIRCLEEND",
        50 to "PS",
        51 to "PE",
        52 to "(-",
        53 to "-)",
        54 to "STADIUMSTART",
        55 to "STADIUMEND",
        56 to "SUBROUTINESTART",
        57 to "SUBROUTINEEND",
        58 to "VERTEX_WITH_PROPS_START",
        59 to "NODE_STRING[field]",
        60 to "COLON",
        61 to "NODE_STRING[value]",
        62 to "PIPE",
        63 to "CYLINDERSTART",
        64 to "CYLINDEREND",
        65 to "DIAMOND_START",
        66 to "DIAMOND_STOP",
        67 to "TAGEND",
        68 to "TRAPSTART",
        69 to "TRAPEND",
        70 to "INVTRAPSTART",
        71 to "INVTRAPEND",
        74 to "TESTSTR",
        75 to "START_LINK",
        77 to "LINK",
        78 to "LINK_ID",
        80 to "STR",
        81 to "MD_STR",
        84 to "STYLE",
        85 to "LINKSTYLE",
        86 to "CLASSDEF",
        87 to "CLASS",
        88 to "CLICK",
        89 to "DOWN",
        90 to "UP",
        93 to "idString[vertex]",
        94 to "idString[class]",
        95 to "CALLBACKNAME",
        96 to "CALLBACKARGS",
        97 to "HREF",
        98 to "LINK_TARGET",
        99 to "STR[link]",
        100 to "STR[tooltip]",
        102 to "DEFAULT",
        104 to "INTERPOLATE",
        105 to "NUM",
        106 to "COMMA",
        109 to "NODE_STRING",
        110 to "UNIT",
        111 to "BRKT",
        112 to "PCT",
        114 to "MINUS",
        115 to "MULT",
        116 to "UNICODE_TEXT",
        117 to "TEXT",
        118 to "TAGSTART",
        119 to "EDGE_TEXT",
        121 to "direction_tb",
        122 to "direction_bt",
        123 to "direction_rl",
        124 to "direction_lr",
        125 to "direction_td",
    )

    val productions: Array<JisonProduction> = arrayOf(
        JisonProduction(symbol = 0, length = 0),
        JisonProduction(symbol = 3, length = 2),
        JisonProduction(symbol = 5, length = 0),
        JisonProduction(symbol = 5, length = 2),
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 3),
        JisonProduction(symbol = 16, length = 2),
        JisonProduction(symbol = 16, length = 1),
        JisonProduction(symbol = 17, length = 1),
        JisonProduction(symbol = 17, length = 1),
        JisonProduction(symbol = 17, length = 1),
        JisonProduction(symbol = 15, length = 1),
        JisonProduction(symbol = 15, length = 1),
        JisonProduction(symbol = 15, length = 2),
        JisonProduction(symbol = 19, length = 2),
        JisonProduction(symbol = 19, length = 2),
        JisonProduction(symbol = 19, length = 1),
        JisonProduction(symbol = 19, length = 1),
        JisonProduction(symbol = 18, length = 2),
        JisonProduction(symbol = 18, length = 1),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 9),
        JisonProduction(symbol = 7, length = 6),
        JisonProduction(symbol = 7, length = 4),
        JisonProduction(symbol = 7, length = 1),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 1),
        JisonProduction(symbol = 21, length = 1),
        JisonProduction(symbol = 21, length = 1),
        JisonProduction(symbol = 21, length = 1),
        JisonProduction(symbol = 39, length = 2),
        JisonProduction(symbol = 39, length = 1),
        JisonProduction(symbol = 20, length = 4),
        JisonProduction(symbol = 20, length = 3),
        JisonProduction(symbol = 20, length = 4),
        JisonProduction(symbol = 20, length = 2),
        JisonProduction(symbol = 20, length = 2),
        JisonProduction(symbol = 20, length = 1),
        JisonProduction(symbol = 42, length = 1),
        JisonProduction(symbol = 42, length = 6),
        JisonProduction(symbol = 42, length = 5),
        JisonProduction(symbol = 43, length = 1),
        JisonProduction(symbol = 43, length = 3),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 6),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 8),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 6),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 4),
        JisonProduction(symbol = 45, length = 1),
        JisonProduction(symbol = 41, length = 2),
        JisonProduction(symbol = 41, length = 3),
        JisonProduction(symbol = 41, length = 3),
        JisonProduction(symbol = 41, length = 1),
        JisonProduction(symbol = 41, length = 3),
        JisonProduction(symbol = 41, length = 4),
        JisonProduction(symbol = 76, length = 1),
        JisonProduction(symbol = 76, length = 2),
        JisonProduction(symbol = 76, length = 1),
        JisonProduction(symbol = 76, length = 1),
        JisonProduction(symbol = 72, length = 1),
        JisonProduction(symbol = 72, length = 2),
        JisonProduction(symbol = 73, length = 3),
        JisonProduction(symbol = 30, length = 1),
        JisonProduction(symbol = 30, length = 2),
        JisonProduction(symbol = 30, length = 1),
        JisonProduction(symbol = 30, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 83, length = 1),
        JisonProduction(symbol = 28, length = 1),
        JisonProduction(symbol = 28, length = 2),
        JisonProduction(symbol = 28, length = 1),
        JisonProduction(symbol = 28, length = 1),
        JisonProduction(symbol = 24, length = 5),
        JisonProduction(symbol = 25, length = 5),
        JisonProduction(symbol = 26, length = 2),
        JisonProduction(symbol = 26, length = 4),
        JisonProduction(symbol = 26, length = 3),
        JisonProduction(symbol = 26, length = 5),
        JisonProduction(symbol = 26, length = 3),
        JisonProduction(symbol = 26, length = 5),
        JisonProduction(symbol = 26, length = 5),
        JisonProduction(symbol = 26, length = 7),
        JisonProduction(symbol = 26, length = 2),
        JisonProduction(symbol = 26, length = 4),
        JisonProduction(symbol = 26, length = 2),
        JisonProduction(symbol = 26, length = 4),
        JisonProduction(symbol = 26, length = 4),
        JisonProduction(symbol = 26, length = 6),
        JisonProduction(symbol = 22, length = 5),
        JisonProduction(symbol = 23, length = 5),
        JisonProduction(symbol = 23, length = 5),
        JisonProduction(symbol = 23, length = 9),
        JisonProduction(symbol = 23, length = 9),
        JisonProduction(symbol = 23, length = 7),
        JisonProduction(symbol = 23, length = 7),
        JisonProduction(symbol = 103, length = 1),
        JisonProduction(symbol = 103, length = 3),
        JisonProduction(symbol = 92, length = 1),
        JisonProduction(symbol = 92, length = 3),
        JisonProduction(symbol = 107, length = 1),
        JisonProduction(symbol = 107, length = 2),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 108, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 113, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 91, length = 1),
        JisonProduction(symbol = 79, length = 1),
        JisonProduction(symbol = 79, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 120, length = 1),
        JisonProduction(symbol = 47, length = 1),
        JisonProduction(symbol = 47, length = 2),
        JisonProduction(symbol = 101, length = 1),
        JisonProduction(symbol = 101, length = 2),
        JisonProduction(symbol = 33, length = 1),
        JisonProduction(symbol = 33, length = 1),
        JisonProduction(symbol = 33, length = 1),
        JisonProduction(symbol = 33, length = 1),
        JisonProduction(symbol = 33, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: JisonCell = JisonCell.Goto(1)
    private val action1: JisonCell = JisonCell.Goto(2)
    private val action2: JisonCell = JisonCell.Shift(4)
    private val action3: JisonCell = JisonCell.Shift(3)
    private val action4: JisonCell = JisonCell.Shift(5)
    private val action5: JisonCell = JisonCell.Accept
    private val action6: JisonCell = JisonCell.Reduce(2)
    private val action7: JisonCell = JisonCell.Goto(6)
    private val action8: JisonCell = JisonCell.Goto(7)
    private val action9: JisonCell = JisonCell.Goto(8)
    private val action10: JisonCell = JisonCell.Shift(9)
    private val action11: JisonCell = JisonCell.Shift(10)
    private val action12: JisonCell = JisonCell.Reduce(1)
    private val action13: JisonCell = JisonCell.Goto(11)
    private val action14: JisonCell = JisonCell.Goto(12)
    private val action15: JisonCell = JisonCell.Shift(13)
    private val action16: JisonCell = JisonCell.Shift(14)
    private val action17: JisonCell = JisonCell.Shift(15)
    private val action18: JisonCell = JisonCell.Shift(16)
    private val action19: JisonCell = JisonCell.Goto(17)
    private val action20: JisonCell = JisonCell.Goto(18)
    private val action21: JisonCell = JisonCell.Goto(19)
    private val action22: JisonCell = JisonCell.Goto(20)
    private val action23: JisonCell = JisonCell.Goto(21)
    private val action24: JisonCell = JisonCell.Goto(22)
    private val action25: JisonCell = JisonCell.Shift(23)
    private val action26: JisonCell = JisonCell.Goto(24)
    private val action27: JisonCell = JisonCell.Shift(25)
    private val action28: JisonCell = JisonCell.Shift(26)
    private val action29: JisonCell = JisonCell.Shift(27)
    private val action30: JisonCell = JisonCell.Goto(28)
    private val action31: JisonCell = JisonCell.Goto(39)
    private val action32: JisonCell = JisonCell.Shift(50)
    private val action33: JisonCell = JisonCell.Goto(40)
    private val action34: JisonCell = JisonCell.Goto(41)
    private val action35: JisonCell = JisonCell.Shift(49)
    private val action36: JisonCell = JisonCell.Shift(29)
    private val action37: JisonCell = JisonCell.Shift(30)
    private val action38: JisonCell = JisonCell.Shift(31)
    private val action39: JisonCell = JisonCell.Shift(32)
    private val action40: JisonCell = JisonCell.Shift(33)
    private val action41: JisonCell = JisonCell.Shift(45)
    private val action42: JisonCell = JisonCell.Shift(47)
    private val action43: JisonCell = JisonCell.Shift(43)
    private val action44: JisonCell = JisonCell.Shift(48)
    private val action45: JisonCell = JisonCell.Shift(44)
    private val action46: JisonCell = JisonCell.Shift(51)
    private val action47: JisonCell = JisonCell.Goto(42)
    private val action48: JisonCell = JisonCell.Shift(46)
    private val action49: JisonCell = JisonCell.Shift(52)
    private val action50: JisonCell = JisonCell.Shift(53)
    private val action51: JisonCell = JisonCell.Shift(34)
    private val action52: JisonCell = JisonCell.Shift(35)
    private val action53: JisonCell = JisonCell.Shift(36)
    private val action54: JisonCell = JisonCell.Shift(37)
    private val action55: JisonCell = JisonCell.Shift(38)
    private val action56: JisonCell = JisonCell.Reduce(9)
    private val action57: JisonCell = JisonCell.Reduce(10)
    private val action58: JisonCell = JisonCell.Reduce(11)
    private val action59: JisonCell = JisonCell.Shift(55)
    private val action60: JisonCell = JisonCell.Shift(56)
    private val action61: JisonCell = JisonCell.Shift(58)
    private val action62: JisonCell = JisonCell.Goto(54)
    private val action63: JisonCell = JisonCell.Goto(57)
    private val action64: JisonCell = JisonCell.Reduce(3)
    private val action65: JisonCell = JisonCell.Reduce(4)
    private val action66: JisonCell = JisonCell.Reduce(5)
    private val action67: JisonCell = JisonCell.Reduce(6)
    private val action68: JisonCell = JisonCell.Reduce(7)
    private val action69: JisonCell = JisonCell.Reduce(8)
    private val action70: JisonCell = JisonCell.Shift(62)
    private val action71: JisonCell = JisonCell.Shift(61)
    private val action72: JisonCell = JisonCell.Shift(63)
    private val action73: JisonCell = JisonCell.Goto(59)
    private val action74: JisonCell = JisonCell.Goto(60)
    private val action75: JisonCell = JisonCell.Goto(64)
    private val action76: JisonCell = JisonCell.Shift(65)
    private val action77: JisonCell = JisonCell.Shift(67)
    private val action78: JisonCell = JisonCell.Shift(66)
    private val action79: JisonCell = JisonCell.Goto(68)
    private val action80: JisonCell = JisonCell.Goto(69)
    private val action81: JisonCell = JisonCell.Goto(70)
    private val action82: JisonCell = JisonCell.Goto(71)
    private val action83: JisonCell = JisonCell.Goto(72)
    private val action84: JisonCell = JisonCell.Shift(73)
    private val action85: JisonCell = JisonCell.Goto(74)
    private val action86: JisonCell = JisonCell.Reduce(36)
    private val action87: JisonCell = JisonCell.Shift(75)
    private val action88: JisonCell = JisonCell.Shift(76)
    private val action89: JisonCell = JisonCell.Reduce(39)
    private val action90: JisonCell = JisonCell.Reduce(50)
    private val action91: JisonCell = JisonCell.Goto(77)
    private val action92: JisonCell = JisonCell.Goto(78)
    private val action93: JisonCell = JisonCell.Shift(79)
    private val action94: JisonCell = JisonCell.Shift(80)
    private val action95: JisonCell = JisonCell.Shift(81)
    private val action96: JisonCell = JisonCell.Shift(82)
    private val action97: JisonCell = JisonCell.Shift(83)
    private val action98: JisonCell = JisonCell.Shift(92)
    private val action99: JisonCell = JisonCell.Shift(97)
    private val action100: JisonCell = JisonCell.Shift(96)
    private val action101: JisonCell = JisonCell.Shift(87)
    private val action102: JisonCell = JisonCell.Shift(93)
    private val action103: JisonCell = JisonCell.Shift(84)
    private val action104: JisonCell = JisonCell.Shift(85)
    private val action105: JisonCell = JisonCell.Goto(86)
    private val action106: JisonCell = JisonCell.Shift(89)
    private val action107: JisonCell = JisonCell.Shift(95)
    private val action108: JisonCell = JisonCell.Shift(91)
    private val action109: JisonCell = JisonCell.Shift(98)
    private val action110: JisonCell = JisonCell.Shift(94)
    private val action111: JisonCell = JisonCell.Shift(99)
    private val action112: JisonCell = JisonCell.Shift(90)
    private val action113: JisonCell = JisonCell.Goto(88)
    private val action114: JisonCell = JisonCell.Reduce(185)
    private val action115: JisonCell = JisonCell.Reduce(186)
    private val action116: JisonCell = JisonCell.Reduce(187)
    private val action117: JisonCell = JisonCell.Reduce(188)
    private val action118: JisonCell = JisonCell.Reduce(189)
    private val action119: JisonCell = JisonCell.Reduce(51)
    private val action120: JisonCell = JisonCell.Reduce(54)
    private val action121: JisonCell = JisonCell.Shift(100)
    private val action122: JisonCell = JisonCell.Reduce(72)
    private val action123: JisonCell = JisonCell.Shift(101)
    private val action124: JisonCell = JisonCell.Shift(102)
    private val action125: JisonCell = JisonCell.Shift(103)
    private val action126: JisonCell = JisonCell.Shift(104)
    private val action127: JisonCell = JisonCell.Shift(105)
    private val action128: JisonCell = JisonCell.Shift(106)
    private val action129: JisonCell = JisonCell.Shift(107)
    private val action130: JisonCell = JisonCell.Shift(108)
    private val action131: JisonCell = JisonCell.Shift(109)
    private val action132: JisonCell = JisonCell.Shift(110)
    private val action133: JisonCell = JisonCell.Shift(111)
    private val action134: JisonCell = JisonCell.Shift(112)
    private val action135: JisonCell = JisonCell.Goto(113)
    private val action136: JisonCell = JisonCell.Reduce(181)
    private val action137: JisonCell = JisonCell.Reduce(142)
    private val action138: JisonCell = JisonCell.Reduce(143)
    private val action139: JisonCell = JisonCell.Reduce(144)
    private val action140: JisonCell = JisonCell.Reduce(145)
    private val action141: JisonCell = JisonCell.Reduce(146)
    private val action142: JisonCell = JisonCell.Reduce(147)
    private val action143: JisonCell = JisonCell.Reduce(148)
    private val action144: JisonCell = JisonCell.Reduce(149)
    private val action145: JisonCell = JisonCell.Reduce(150)
    private val action146: JisonCell = JisonCell.Reduce(151)
    private val action147: JisonCell = JisonCell.Reduce(152)
    private val action148: JisonCell = JisonCell.Reduce(12)
    private val action149: JisonCell = JisonCell.Reduce(18)
    private val action150: JisonCell = JisonCell.Reduce(19)
    private val action151: JisonCell = JisonCell.Shift(114)
    private val action152: JisonCell = JisonCell.Reduce(26)
    private val action153: JisonCell = JisonCell.Goto(115)
    private val action154: JisonCell = JisonCell.Reduce(27)
    private val action155: JisonCell = JisonCell.Goto(116)
    private val action156: JisonCell = JisonCell.Reduce(40)
    private val action157: JisonCell = JisonCell.Reduce(41)
    private val action158: JisonCell = JisonCell.Reduce(42)
    private val action159: JisonCell = JisonCell.Reduce(76)
    private val action160: JisonCell = JisonCell.Shift(119)
    private val action161: JisonCell = JisonCell.Goto(117)
    private val action162: JisonCell = JisonCell.Shift(118)
    private val action163: JisonCell = JisonCell.Goto(120)
    private val action164: JisonCell = JisonCell.Goto(121)
    private val action165: JisonCell = JisonCell.Shift(122)
    private val action166: JisonCell = JisonCell.Shift(123)
    private val action167: JisonCell = JisonCell.Shift(125)
    private val action168: JisonCell = JisonCell.Shift(124)
    private val action169: JisonCell = JisonCell.Shift(126)
    private val action170: JisonCell = JisonCell.Shift(127)
    private val action171: JisonCell = JisonCell.Reduce(83)
    private val action172: JisonCell = JisonCell.Reduce(28)
    private val action173: JisonCell = JisonCell.Reduce(29)
    private val action174: JisonCell = JisonCell.Reduce(30)
    private val action175: JisonCell = JisonCell.Reduce(31)
    private val action176: JisonCell = JisonCell.Reduce(32)
    private val action177: JisonCell = JisonCell.Shift(134)
    private val action178: JisonCell = JisonCell.Shift(148)
    private val action179: JisonCell = JisonCell.Shift(149)
    private val action180: JisonCell = JisonCell.Shift(150)
    private val action181: JisonCell = JisonCell.Goto(128)
    private val action182: JisonCell = JisonCell.Shift(151)
    private val action183: JisonCell = JisonCell.Shift(136)
    private val action184: JisonCell = JisonCell.Shift(138)
    private val action185: JisonCell = JisonCell.Shift(142)
    private val action186: JisonCell = JisonCell.Shift(130)
    private val action187: JisonCell = JisonCell.Shift(131)
    private val action188: JisonCell = JisonCell.Goto(141)
    private val action189: JisonCell = JisonCell.Shift(143)
    private val action190: JisonCell = JisonCell.Shift(144)
    private val action191: JisonCell = JisonCell.Shift(145)
    private val action192: JisonCell = JisonCell.Shift(146)
    private val action193: JisonCell = JisonCell.Shift(147)
    private val action194: JisonCell = JisonCell.Shift(152)
    private val action195: JisonCell = JisonCell.Shift(153)
    private val action196: JisonCell = JisonCell.Goto(129)
    private val action197: JisonCell = JisonCell.Shift(132)
    private val action198: JisonCell = JisonCell.Shift(133)
    private val action199: JisonCell = JisonCell.Shift(140)
    private val action200: JisonCell = JisonCell.Shift(135)
    private val action201: JisonCell = JisonCell.Shift(139)
    private val action202: JisonCell = JisonCell.Shift(137)
    private val action203: JisonCell = JisonCell.Goto(154)
    private val action204: JisonCell = JisonCell.Reduce(37)
    private val action205: JisonCell = JisonCell.Reduce(38)
    private val action206: JisonCell = JisonCell.Reduce(48)
    private val action207: JisonCell = JisonCell.Shift(155)
    private val action208: JisonCell = JisonCell.Reduce(49)
    private val action209: JisonCell = JisonCell.Goto(156)
    private val action210: JisonCell = JisonCell.Shift(157)
    private val action211: JisonCell = JisonCell.Reduce(44)
    private val action212: JisonCell = JisonCell.Goto(158)
    private val action213: JisonCell = JisonCell.Shift(159)
    private val action214: JisonCell = JisonCell.Goto(160)
    private val action215: JisonCell = JisonCell.Shift(161)
    private val action216: JisonCell = JisonCell.Goto(162)
    private val action217: JisonCell = JisonCell.Goto(163)
    private val action218: JisonCell = JisonCell.Reduce(107)
    private val action219: JisonCell = JisonCell.Shift(164)
    private val action220: JisonCell = JisonCell.Shift(165)
    private val action221: JisonCell = JisonCell.Shift(166)
    private val action222: JisonCell = JisonCell.Reduce(115)
    private val action223: JisonCell = JisonCell.Shift(167)
    private val action224: JisonCell = JisonCell.Goto(168)
    private val action225: JisonCell = JisonCell.Reduce(117)
    private val action226: JisonCell = JisonCell.Shift(169)
    private val action227: JisonCell = JisonCell.Reduce(183)
    private val action228: JisonCell = JisonCell.Reduce(170)
    private val action229: JisonCell = JisonCell.Reduce(171)
    private val action230: JisonCell = JisonCell.Reduce(172)
    private val action231: JisonCell = JisonCell.Reduce(173)
    private val action232: JisonCell = JisonCell.Reduce(174)
    private val action233: JisonCell = JisonCell.Reduce(175)
    private val action234: JisonCell = JisonCell.Reduce(176)
    private val action235: JisonCell = JisonCell.Reduce(177)
    private val action236: JisonCell = JisonCell.Reduce(178)
    private val action237: JisonCell = JisonCell.Reduce(179)
    private val action238: JisonCell = JisonCell.Reduce(180)
    private val action239: JisonCell = JisonCell.Goto(170)
    private val action240: JisonCell = JisonCell.Goto(171)
    private val action241: JisonCell = JisonCell.Shift(177)
    private val action242: JisonCell = JisonCell.Shift(173)
    private val action243: JisonCell = JisonCell.Shift(174)
    private val action244: JisonCell = JisonCell.Goto(172)
    private val action245: JisonCell = JisonCell.Shift(178)
    private val action246: JisonCell = JisonCell.Shift(175)
    private val action247: JisonCell = JisonCell.Shift(176)
    private val action248: JisonCell = JisonCell.Goto(179)
    private val action249: JisonCell = JisonCell.Goto(181)
    private val action250: JisonCell = JisonCell.Shift(180)
    private val action251: JisonCell = JisonCell.Goto(182)
    private val action252: JisonCell = JisonCell.Goto(183)
    private val action253: JisonCell = JisonCell.Goto(184)
    private val action254: JisonCell = JisonCell.Shift(185)
    private val action255: JisonCell = JisonCell.Goto(186)
    private val action256: JisonCell = JisonCell.Goto(187)
    private val action257: JisonCell = JisonCell.Shift(188)
    private val action258: JisonCell = JisonCell.Goto(189)
    private val action259: JisonCell = JisonCell.Goto(190)
    private val action260: JisonCell = JisonCell.Goto(191)
    private val action261: JisonCell = JisonCell.Reduce(182)
    private val action262: JisonCell = JisonCell.Reduce(20)
    private val action263: JisonCell = JisonCell.Reduce(25)
    private val action264: JisonCell = JisonCell.Reduce(46)
    private val action265: JisonCell = JisonCell.Goto(193)
    private val action266: JisonCell = JisonCell.Goto(192)
    private val action267: JisonCell = JisonCell.Shift(194)
    private val action268: JisonCell = JisonCell.Reduce(73)
    private val action269: JisonCell = JisonCell.Shift(195)
    private val action270: JisonCell = JisonCell.Goto(196)
    private val action271: JisonCell = JisonCell.Shift(197)
    private val action272: JisonCell = JisonCell.Goto(198)
    private val action273: JisonCell = JisonCell.Reduce(79)
    private val action274: JisonCell = JisonCell.Reduce(81)
    private val action275: JisonCell = JisonCell.Reduce(82)
    private val action276: JisonCell = JisonCell.Reduce(168)
    private val action277: JisonCell = JisonCell.Reduce(169)
    private val action278: JisonCell = JisonCell.Goto(199)
    private val action279: JisonCell = JisonCell.Reduce(84)
    private val action280: JisonCell = JisonCell.Goto(201)
    private val action281: JisonCell = JisonCell.Shift(200)
    private val action282: JisonCell = JisonCell.Goto(202)
    private val action283: JisonCell = JisonCell.Reduce(101)
    private val action284: JisonCell = JisonCell.Reduce(103)
    private val action285: JisonCell = JisonCell.Reduce(104)
    private val action286: JisonCell = JisonCell.Reduce(157)
    private val action287: JisonCell = JisonCell.Reduce(158)
    private val action288: JisonCell = JisonCell.Reduce(159)
    private val action289: JisonCell = JisonCell.Reduce(160)
    private val action290: JisonCell = JisonCell.Reduce(161)
    private val action291: JisonCell = JisonCell.Reduce(162)
    private val action292: JisonCell = JisonCell.Reduce(163)
    private val action293: JisonCell = JisonCell.Reduce(164)
    private val action294: JisonCell = JisonCell.Reduce(165)
    private val action295: JisonCell = JisonCell.Reduce(166)
    private val action296: JisonCell = JisonCell.Reduce(167)
    private val action297: JisonCell = JisonCell.Reduce(90)
    private val action298: JisonCell = JisonCell.Reduce(91)
    private val action299: JisonCell = JisonCell.Reduce(92)
    private val action300: JisonCell = JisonCell.Reduce(93)
    private val action301: JisonCell = JisonCell.Reduce(94)
    private val action302: JisonCell = JisonCell.Reduce(95)
    private val action303: JisonCell = JisonCell.Reduce(96)
    private val action304: JisonCell = JisonCell.Reduce(97)
    private val action305: JisonCell = JisonCell.Reduce(98)
    private val action306: JisonCell = JisonCell.Reduce(99)
    private val action307: JisonCell = JisonCell.Reduce(100)
    private val action308: JisonCell = JisonCell.Shift(203)
    private val action309: JisonCell = JisonCell.Goto(204)
    private val action310: JisonCell = JisonCell.Shift(205)
    private val action311: JisonCell = JisonCell.Reduce(43)
    private val action312: JisonCell = JisonCell.Shift(206)
    private val action313: JisonCell = JisonCell.Shift(207)
    private val action314: JisonCell = JisonCell.Shift(208)
    private val action315: JisonCell = JisonCell.Shift(209)
    private val action316: JisonCell = JisonCell.Reduce(128)
    private val action317: JisonCell = JisonCell.Shift(210)
    private val action318: JisonCell = JisonCell.Shift(211)
    private val action319: JisonCell = JisonCell.Shift(212)
    private val action320: JisonCell = JisonCell.Reduce(109)
    private val action321: JisonCell = JisonCell.Shift(213)
    private val action322: JisonCell = JisonCell.Reduce(111)
    private val action323: JisonCell = JisonCell.Shift(214)
    private val action324: JisonCell = JisonCell.Shift(215)
    private val action325: JisonCell = JisonCell.Reduce(184)
    private val action326: JisonCell = JisonCell.Shift(216)
    private val action327: JisonCell = JisonCell.Shift(217)
    private val action328: JisonCell = JisonCell.Reduce(55)
    private val action329: JisonCell = JisonCell.Shift(218)
    private val action330: JisonCell = JisonCell.Goto(219)
    private val action331: JisonCell = JisonCell.Reduce(86)
    private val action332: JisonCell = JisonCell.Reduce(88)
    private val action333: JisonCell = JisonCell.Reduce(89)
    private val action334: JisonCell = JisonCell.Reduce(153)
    private val action335: JisonCell = JisonCell.Reduce(154)
    private val action336: JisonCell = JisonCell.Reduce(155)
    private val action337: JisonCell = JisonCell.Reduce(156)
    private val action338: JisonCell = JisonCell.Shift(220)
    private val action339: JisonCell = JisonCell.Goto(221)
    private val action340: JisonCell = JisonCell.Shift(222)
    private val action341: JisonCell = JisonCell.Shift(223)
    private val action342: JisonCell = JisonCell.Shift(224)
    private val action343: JisonCell = JisonCell.Shift(225)
    private val action344: JisonCell = JisonCell.Shift(226)
    private val action345: JisonCell = JisonCell.Shift(227)
    private val action346: JisonCell = JisonCell.Shift(228)
    private val action347: JisonCell = JisonCell.Goto(229)
    private val action348: JisonCell = JisonCell.Shift(230)
    private val action349: JisonCell = JisonCell.Shift(231)
    private val action350: JisonCell = JisonCell.Shift(232)
    private val action351: JisonCell = JisonCell.Shift(234)
    private val action352: JisonCell = JisonCell.Shift(233)
    private val action353: JisonCell = JisonCell.Reduce(45)
    private val action354: JisonCell = JisonCell.Reduce(47)
    private val action355: JisonCell = JisonCell.Reduce(75)
    private val action356: JisonCell = JisonCell.Reduce(74)
    private val action357: JisonCell = JisonCell.Shift(235)
    private val action358: JisonCell = JisonCell.Reduce(77)
    private val action359: JisonCell = JisonCell.Reduce(80)
    private val action360: JisonCell = JisonCell.Shift(236)
    private val action361: JisonCell = JisonCell.Goto(237)
    private val action362: JisonCell = JisonCell.Goto(238)
    private val action363: JisonCell = JisonCell.Reduce(102)
    private val action364: JisonCell = JisonCell.Reduce(35)
    private val action365: JisonCell = JisonCell.Goto(239)
    private val action366: JisonCell = JisonCell.Goto(240)
    private val action367: JisonCell = JisonCell.Shift(248)
    private val action368: JisonCell = JisonCell.Shift(246)
    private val action369: JisonCell = JisonCell.Shift(250)
    private val action370: JisonCell = JisonCell.Goto(241)
    private val action371: JisonCell = JisonCell.Shift(244)
    private val action372: JisonCell = JisonCell.Goto(242)
    private val action373: JisonCell = JisonCell.Goto(243)
    private val action374: JisonCell = JisonCell.Shift(245)
    private val action375: JisonCell = JisonCell.Shift(247)
    private val action376: JisonCell = JisonCell.Shift(249)
    private val action377: JisonCell = JisonCell.Shift(251)
    private val action378: JisonCell = JisonCell.Goto(252)
    private val action379: JisonCell = JisonCell.Shift(253)
    private val action380: JisonCell = JisonCell.Goto(254)
    private val action381: JisonCell = JisonCell.Shift(255)
    private val action382: JisonCell = JisonCell.Shift(256)
    private val action383: JisonCell = JisonCell.Goto(257)
    private val action384: JisonCell = JisonCell.Goto(258)
    private val action385: JisonCell = JisonCell.Reduce(108)
    private val action386: JisonCell = JisonCell.Shift(259)
    private val action387: JisonCell = JisonCell.Shift(260)
    private val action388: JisonCell = JisonCell.Shift(261)
    private val action389: JisonCell = JisonCell.Reduce(116)
    private val action390: JisonCell = JisonCell.Reduce(118)
    private val action391: JisonCell = JisonCell.Shift(262)
    private val action392: JisonCell = JisonCell.Reduce(119)
    private val action393: JisonCell = JisonCell.Reduce(56)
    private val action394: JisonCell = JisonCell.Reduce(87)
    private val action395: JisonCell = JisonCell.Reduce(57)
    private val action396: JisonCell = JisonCell.Shift(263)
    private val action397: JisonCell = JisonCell.Reduce(64)
    private val action398: JisonCell = JisonCell.Reduce(59)
    private val action399: JisonCell = JisonCell.Reduce(60)
    private val action400: JisonCell = JisonCell.Reduce(61)
    private val action401: JisonCell = JisonCell.Shift(264)
    private val action402: JisonCell = JisonCell.Reduce(63)
    private val action403: JisonCell = JisonCell.Reduce(65)
    private val action404: JisonCell = JisonCell.Shift(265)
    private val action405: JisonCell = JisonCell.Reduce(67)
    private val action406: JisonCell = JisonCell.Reduce(68)
    private val action407: JisonCell = JisonCell.Reduce(70)
    private val action408: JisonCell = JisonCell.Reduce(69)
    private val action409: JisonCell = JisonCell.Reduce(71)
    private val action410: JisonCell = JisonCell.Reduce(85)
    private val action411: JisonCell = JisonCell.Reduce(78)
    private val action412: JisonCell = JisonCell.Shift(266)
    private val action413: JisonCell = JisonCell.Shift(267)
    private val action414: JisonCell = JisonCell.Reduce(53)
    private val action415: JisonCell = JisonCell.Goto(268)
    private val action416: JisonCell = JisonCell.Reduce(121)
    private val action417: JisonCell = JisonCell.Shift(269)
    private val action418: JisonCell = JisonCell.Reduce(130)
    private val action419: JisonCell = JisonCell.Goto(270)
    private val action420: JisonCell = JisonCell.Reduce(132)
    private val action421: JisonCell = JisonCell.Reduce(134)
    private val action422: JisonCell = JisonCell.Reduce(135)
    private val action423: JisonCell = JisonCell.Reduce(136)
    private val action424: JisonCell = JisonCell.Reduce(137)
    private val action425: JisonCell = JisonCell.Reduce(138)
    private val action426: JisonCell = JisonCell.Reduce(139)
    private val action427: JisonCell = JisonCell.Reduce(140)
    private val action428: JisonCell = JisonCell.Reduce(141)
    private val action429: JisonCell = JisonCell.Reduce(122)
    private val action430: JisonCell = JisonCell.Shift(271)
    private val action431: JisonCell = JisonCell.Reduce(123)
    private val action432: JisonCell = JisonCell.Shift(272)
    private val action433: JisonCell = JisonCell.Reduce(129)
    private val action434: JisonCell = JisonCell.Reduce(105)
    private val action435: JisonCell = JisonCell.Reduce(106)
    private val action436: JisonCell = JisonCell.Reduce(110)
    private val action437: JisonCell = JisonCell.Reduce(112)
    private val action438: JisonCell = JisonCell.Shift(273)
    private val action439: JisonCell = JisonCell.Reduce(113)
    private val action440: JisonCell = JisonCell.Shift(274)
    private val action441: JisonCell = JisonCell.Shift(275)
    private val action442: JisonCell = JisonCell.Shift(276)
    private val action443: JisonCell = JisonCell.Shift(277)
    private val action444: JisonCell = JisonCell.Goto(278)
    private val action445: JisonCell = JisonCell.Reduce(34)
    private val action446: JisonCell = JisonCell.Reduce(52)
    private val action447: JisonCell = JisonCell.Goto(279)
    private val action448: JisonCell = JisonCell.Reduce(133)
    private val action449: JisonCell = JisonCell.Goto(280)
    private val action450: JisonCell = JisonCell.Goto(281)
    private val action451: JisonCell = JisonCell.Shift(282)
    private val action452: JisonCell = JisonCell.Reduce(120)
    private val action453: JisonCell = JisonCell.Reduce(58)
    private val action454: JisonCell = JisonCell.Goto(283)
    private val action455: JisonCell = JisonCell.Reduce(66)
    private val action456: JisonCell = JisonCell.Goto(284)
    private val action457: JisonCell = JisonCell.Reduce(131)
    private val action458: JisonCell = JisonCell.Reduce(126)
    private val action459: JisonCell = JisonCell.Shift(285)
    private val action460: JisonCell = JisonCell.Reduce(127)
    private val action461: JisonCell = JisonCell.Shift(286)
    private val action462: JisonCell = JisonCell.Reduce(114)
    private val action463: JisonCell = JisonCell.Shift(287)
    private val action464: JisonCell = JisonCell.Shift(288)
    private val action465: JisonCell = JisonCell.Goto(289)
    private val action466: JisonCell = JisonCell.Goto(290)
    private val action467: JisonCell = JisonCell.Reduce(62)
    private val action468: JisonCell = JisonCell.Reduce(33)
    private val action469: JisonCell = JisonCell.Reduce(124)
    private val action470: JisonCell = JisonCell.Reduce(125)
    private val symbols0 = intArrayOf(1, 5, 8, 9, 10, 11, 27, 34, 36, 38, 44, 60, 84, 85, 86, 87, 88, 89, 102, 105, 106, 109, 111, 114, 115, 116, 121, 122, 123, 124, 125)
    private val symbols1 = intArrayOf(1, 8, 9, 10, 11, 27, 34, 36, 38, 44, 60, 84, 85, 86, 87, 88, 89, 102, 105, 106, 109, 111, 114, 115, 116, 121, 122, 123, 124, 125)
    private val symbols2 = intArrayOf(1, 8, 9, 10, 11, 27, 32, 34, 36, 38, 44, 60, 84, 85, 86, 87, 88, 89, 102, 105, 106, 109, 111, 114, 115, 116, 121, 122, 123, 124, 125)
    private val symbols3 = intArrayOf(8, 9, 10, 11, 18, 39, 40, 75, 77, 78)
    private val symbols4 = intArrayOf(8, 9, 10, 11, 40, 75, 77, 78)
    private val symbols5 = intArrayOf(8, 9, 10, 11, 40, 46, 75, 77, 78)
    private val symbols6 = intArrayOf(8, 9, 10, 11, 29, 40, 44, 46, 48, 50, 52, 54, 56, 58, 60, 63, 65, 67, 68, 70, 75, 77, 78, 89, 102, 105, 106, 109, 111, 113, 114, 115, 116)
    private val symbols7 = intArrayOf(8, 9, 10, 11, 29, 40, 44, 46, 48, 50, 52, 54, 56, 58, 60, 63, 65, 67, 68, 70, 75, 77, 78, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols8 = intArrayOf(8, 9, 10, 11, 18, 44, 60, 75, 77, 78, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols9 = intArrayOf(44, 60, 62, 73, 74, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols10 = intArrayOf(44, 60, 62, 74, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols11 = intArrayOf(5, 8, 9, 10, 11, 27, 32, 34, 36, 38, 44, 60, 84, 85, 86, 87, 88, 89, 102, 105, 106, 109, 111, 114, 115, 116, 121, 122, 123, 124, 125)
    private val symbols12 = intArrayOf(8, 9, 11, 44, 75, 77, 78)
    private val symbols13 = intArrayOf(8, 9, 10, 11, 18, 40, 75, 77, 78)
    private val symbols14 = intArrayOf(8, 9, 10, 11, 96)
    private val symbols15 = intArrayOf(8, 9, 10, 11, 14, 44, 60, 89, 105, 106, 109, 111, 114, 115, 116, 120)
    private val symbols16 = intArrayOf(8, 9, 10, 11)
    private val symbols17 = intArrayOf(8, 9, 10, 11, 14, 44, 60, 89, 105, 106, 109, 111, 114, 115, 116)
    private val symbols18 = intArrayOf(8, 9, 11, 44, 60, 75, 77, 78, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols19 = intArrayOf(10, 44, 60, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols20 = intArrayOf(77, 116, 119)
    private val symbols21 = intArrayOf(8, 9, 10, 11, 12, 14, 27, 29, 32, 44, 60, 75, 84, 85, 86, 87, 88, 89, 90, 105, 109, 111, 114, 115, 116)
    private val symbols22 = intArrayOf(8, 9, 10, 11, 40, 44, 60, 75, 77, 78, 89, 102, 105, 106, 109, 111, 113, 114, 115, 116)
    private val symbols23 = intArrayOf(31, 49, 51, 53, 55, 57, 62, 64, 66, 67, 69, 71, 116, 117, 118)
    private val symbols24 = intArrayOf(44, 60, 89, 102, 105, 106, 109, 111, 114, 115, 116)
    private val symbols25 = intArrayOf(8, 9, 11)
    private val symbols26 = intArrayOf(8, 9, 11, 106)
    private val symbols27 = intArrayOf(8, 9, 10, 11, 60, 84, 105, 106, 108, 109, 110, 111, 112)
    private val symbols28 = intArrayOf(8, 9, 10, 11, 60, 84, 105, 106, 109, 110, 111, 112)
    private val symbols29 = intArrayOf(8, 9, 11, 44, 60, 89, 102, 105, 106, 109, 111, 113, 114, 115, 116)

    val states: Array<Map<Int, JisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
        addAll(stateChunk3())
        addAll(stateChunk4())
        addAll(stateChunk5())
        addAll(stateChunk6())
        addAll(stateChunk7())
        addAll(stateChunk8())
        addAll(stateChunk9())
        addAll(stateChunk10())
        addAll(stateChunk11())
        addAll(stateChunk12())
        addAll(stateChunk13())
        addAll(stateChunk14())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            9 to action2,
            10 to action3,
            12 to action4,
        ),
        mapOf(
            1 to action5,
        ),
        buildJisonState(symbols0, action6, mapOf(
            5 to action7,
        )),
        mapOf(
            4 to action8,
            9 to action2,
            10 to action3,
            12 to action4,
        ),
        mapOf(
            4 to action9,
            9 to action2,
            10 to action3,
            12 to action4,
        ),
        mapOf(
            13 to action10,
            14 to action11,
        ),
        mapOf(
            1 to action12,
            6 to action13,
            7 to action14,
            8 to action15,
            9 to action16,
            10 to action17,
            11 to action18,
            20 to action19,
            22 to action20,
            23 to action21,
            24 to action22,
            25 to action23,
            26 to action24,
            27 to action25,
            33 to action26,
            34 to action27,
            36 to action28,
            38 to action29,
            42 to action30,
            43 to action31,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            84 to action36,
            85 to action37,
            86 to action38,
            87 to action39,
            88 to action40,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
            121 to action51,
            122 to action52,
            123 to action53,
            124 to action54,
            125 to action55,
        ),
        buildJisonState(symbols1, action56),
        buildJisonState(symbols1, action57),
        buildJisonState(symbols1, action58),
        mapOf(
            8 to action59,
            9 to action60,
            10 to action61,
            15 to action62,
            18 to action63,
        ),
        buildJisonState(symbols2, action64),
        buildJisonState(symbols2, action65),
        buildJisonState(symbols2, action66),
        buildJisonState(symbols2, action67),
        buildJisonState(symbols2, action68),
        buildJisonState(symbols2, action69),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action73,
            41 to action74,
            72 to action75,
            75 to action76,
            77 to action77,
            78 to action78,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action79,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action80,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action81,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action82,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action83,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            10 to action84,
            11 to action72,
            21 to action85,
        ),
        buildJisonState(symbols2, action86),
        mapOf(
            35 to action87,
        ),
        mapOf(
            37 to action88,
        ),
        buildJisonState(symbols2, action89),
        buildJisonState(symbols3, action90, mapOf(
            10 to action61,
            18 to action91,
            39 to action92,
            40 to action93,
        )),
        mapOf(
            10 to action94,
        ),
        mapOf(
            10 to action95,
        ),
        mapOf(
            10 to action96,
        ),
        mapOf(
            10 to action97,
        ),
        mapOf(
            14 to action98,
            44 to action99,
            60 to action100,
            80 to action101,
            89 to action102,
            95 to action103,
            97 to action104,
            101 to action105,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action113,
        ),
        buildJisonState(symbols2, action114),
        buildJisonState(symbols2, action115),
        buildJisonState(symbols2, action116),
        buildJisonState(symbols2, action117),
        buildJisonState(symbols2, action118),
        buildJisonState(symbols4, action119),
    )

    private fun stateChunk2(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols5, action120, mapOf(
            46 to action121,
        )),
        buildJisonState(symbols6, action122, mapOf(
            29 to action123,
            44 to action32,
            48 to action124,
            50 to action125,
            52 to action126,
            54 to action127,
            56 to action128,
            58 to action129,
            60 to action35,
            63 to action130,
            65 to action131,
            67 to action132,
            68 to action133,
            70 to action134,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        )),
        buildJisonState(symbols7, action136),
        buildJisonState(symbols7, action137),
        buildJisonState(symbols7, action138),
        buildJisonState(symbols7, action139),
        buildJisonState(symbols7, action140),
        buildJisonState(symbols7, action141),
        buildJisonState(symbols7, action142),
        buildJisonState(symbols7, action143),
        buildJisonState(symbols7, action144),
        buildJisonState(symbols7, action145),
        buildJisonState(symbols7, action146),
        buildJisonState(symbols7, action147),
        buildJisonState(symbols1, action148),
        buildJisonState(symbols1, action149),
        buildJisonState(symbols1, action150),
        mapOf(
            9 to action151,
        ),
        buildJisonState(symbols8, action152, mapOf(
            10 to action61,
            18 to action153,
        )),
        buildJisonState(symbols2, action154),
    )

    private fun stateChunk3(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            42 to action155,
            43 to action31,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        buildJisonState(symbols2, action156),
        buildJisonState(symbols2, action157),
        buildJisonState(symbols2, action158),
        buildJisonState(symbols9, action159, mapOf(
            62 to action160,
            73 to action161,
            74 to action162,
        )),
        mapOf(
            76 to action163,
            79 to action164,
            80 to action165,
            81 to action166,
            116 to action167,
            119 to action168,
        ),
        mapOf(
            75 to action169,
            77 to action170,
        ),
        buildJisonState(symbols10, action171),
        buildJisonState(symbols2, action172),
        buildJisonState(symbols2, action173),
        buildJisonState(symbols2, action174),
        buildJisonState(symbols2, action175),
        buildJisonState(symbols2, action176),
        mapOf(
            10 to action177,
            12 to action178,
            14 to action179,
            27 to action180,
            28 to action181,
            32 to action182,
            44 to action183,
            60 to action184,
            75 to action185,
            80 to action186,
            81 to action187,
            83 to action188,
            84 to action189,
            85 to action190,
            86 to action191,
            87 to action192,
            88 to action193,
            89 to action194,
            90 to action195,
            91 to action196,
            105 to action197,
            109 to action198,
            111 to action199,
            114 to action200,
            115 to action201,
            116 to action202,
        ),
        buildJisonState(symbols11, action6, mapOf(
            5 to action203,
        )),
        buildJisonState(symbols2, action204),
        buildJisonState(symbols2, action205),
        buildJisonState(symbols12, action206, mapOf(
            44 to action207,
        )),
        buildJisonState(symbols13, action208, mapOf(
            10 to action61,
            18 to action209,
            40 to action210,
        )),
        buildJisonState(symbols4, action211),
    )

    private fun stateChunk4(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            44 to action32,
            47 to action212,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            102 to action213,
            103 to action214,
            105 to action215,
        ),
        mapOf(
            44 to action32,
            47 to action216,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            44 to action32,
            47 to action217,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        buildJisonState(symbols14, action218, mapOf(
            10 to action219,
            96 to action220,
        )),
        mapOf(
            80 to action221,
        ),
        buildJisonState(symbols15, action222, mapOf(
            10 to action223,
            14 to action98,
            44 to action99,
            60 to action100,
            89 to action102,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action224,
        )),
        buildJisonState(symbols16, action225, mapOf(
            10 to action226,
        )),
        buildJisonState(symbols17, action227),
        buildJisonState(symbols17, action228),
        buildJisonState(symbols17, action229),
        buildJisonState(symbols17, action230),
        buildJisonState(symbols17, action231),
        buildJisonState(symbols17, action232),
        buildJisonState(symbols17, action233),
        buildJisonState(symbols17, action234),
        buildJisonState(symbols17, action235),
        buildJisonState(symbols17, action236),
        buildJisonState(symbols17, action237),
        buildJisonState(symbols17, action238),
    )

    private fun stateChunk5(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            44 to action32,
            47 to action239,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            30 to action240,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action248,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action249,
            50 to action250,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action251,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action252,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action253,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            109 to action254,
        ),
        mapOf(
            30 to action255,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action256,
            65 to action257,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action258,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action259,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action260,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols7, action261),
        buildJisonState(symbols1, action262),
        buildJisonState(symbols18, action263),
        buildJisonState(symbols3, action264, mapOf(
            10 to action61,
            18 to action265,
            39 to action266,
            40 to action93,
        )),
        buildJisonState(symbols19, action268, mapOf(
            10 to action267,
        )),
        mapOf(
            10 to action269,
        ),
        mapOf(
            30 to action270,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
    )

    private fun stateChunk6(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            77 to action271,
            79 to action272,
            116 to action167,
            119 to action168,
        ),
        buildJisonState(symbols20, action273),
        buildJisonState(symbols20, action274),
        buildJisonState(symbols20, action275),
        buildJisonState(symbols20, action276),
        buildJisonState(symbols20, action277),
        mapOf(
            76 to action278,
            79 to action164,
            80 to action165,
            81 to action166,
            116 to action167,
            119 to action168,
        ),
        buildJisonState(symbols10, action279),
        mapOf(
            8 to action70,
            9 to action71,
            10 to action177,
            11 to action72,
            12 to action178,
            14 to action179,
            21 to action280,
            27 to action180,
            29 to action281,
            32 to action182,
            44 to action183,
            60 to action184,
            75 to action185,
            83 to action188,
            84 to action189,
            85 to action190,
            86 to action191,
            87 to action192,
            88 to action193,
            89 to action194,
            90 to action195,
            91 to action282,
            105 to action197,
            109 to action198,
            111 to action199,
            114 to action200,
            115 to action201,
            116 to action202,
        ),
        buildJisonState(symbols21, action283),
        buildJisonState(symbols21, action284),
        buildJisonState(symbols21, action285),
        buildJisonState(symbols21, action286),
        buildJisonState(symbols21, action287),
        buildJisonState(symbols21, action288),
        buildJisonState(symbols21, action289),
        buildJisonState(symbols21, action290),
        buildJisonState(symbols21, action291),
        buildJisonState(symbols21, action292),
        buildJisonState(symbols21, action293),
    )

    private fun stateChunk7(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols21, action294),
        buildJisonState(symbols21, action295),
        buildJisonState(symbols21, action296),
        buildJisonState(symbols21, action297),
        buildJisonState(symbols21, action298),
        buildJisonState(symbols21, action299),
        buildJisonState(symbols21, action300),
        buildJisonState(symbols21, action301),
        buildJisonState(symbols21, action302),
        buildJisonState(symbols21, action303),
        buildJisonState(symbols21, action304),
        buildJisonState(symbols21, action305),
        buildJisonState(symbols21, action306),
        buildJisonState(symbols21, action307),
        mapOf(
            6 to action13,
            7 to action14,
            8 to action15,
            9 to action16,
            10 to action17,
            11 to action18,
            20 to action19,
            22 to action20,
            23 to action21,
            24 to action22,
            25 to action23,
            26 to action24,
            27 to action25,
            32 to action308,
            33 to action26,
            34 to action27,
            36 to action28,
            38 to action29,
            42 to action30,
            43 to action31,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            84 to action36,
            85 to action37,
            86 to action38,
            87 to action39,
            88 to action40,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
            121 to action51,
            122 to action52,
            123 to action53,
            124 to action54,
            125 to action55,
        ),
        mapOf(
            10 to action61,
            18 to action309,
        ),
        mapOf(
            44 to action310,
        ),
        buildJisonState(symbols4, action311),
        mapOf(
            10 to action312,
            44 to action32,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            10 to action313,
        ),
    )

    private fun stateChunk8(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            10 to action314,
            106 to action315,
        ),
        mapOf(
            10 to action316,
            106 to action316,
        ),
        mapOf(
            10 to action317,
            44 to action32,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            10 to action318,
            44 to action32,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            80 to action319,
        ),
        buildJisonState(symbols16, action320, mapOf(
            10 to action321,
        )),
        buildJisonState(symbols16, action322, mapOf(
            10 to action323,
        )),
        mapOf(
            80 to action324,
        ),
        buildJisonState(symbols17, action325),
        mapOf(
            80 to action326,
            98 to action327,
        ),
        buildJisonState(symbols22, action328, mapOf(
            44 to action32,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        )),
        mapOf(
            31 to action329,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols23, action331),
        buildJisonState(symbols23, action332),
        buildJisonState(symbols23, action333),
        buildJisonState(symbols23, action334),
        buildJisonState(symbols23, action335),
        buildJisonState(symbols23, action336),
        buildJisonState(symbols23, action337),
        mapOf(
            49 to action338,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
    )

    private fun stateChunk9(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            30 to action339,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            51 to action340,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            53 to action341,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            55 to action342,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            57 to action343,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            60 to action344,
        ),
        mapOf(
            64 to action345,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            66 to action346,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            30 to action347,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            31 to action348,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            67 to action241,
            69 to action349,
            71 to action350,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            67 to action241,
            69 to action351,
            71 to action352,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols13, action353, mapOf(
            10 to action61,
            18 to action209,
            40 to action210,
        )),
        buildJisonState(symbols12, action354, mapOf(
            44 to action207,
        )),
        buildJisonState(symbols24, action355),
        buildJisonState(symbols24, action356),
        mapOf(
            62 to action357,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols24, action358),
        buildJisonState(symbols20, action359),
        mapOf(
            77 to action360,
            79 to action272,
            116 to action167,
            119 to action168,
        ),
    )

    private fun stateChunk10(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            30 to action361,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols11, action6, mapOf(
            5 to action362,
        )),
        buildJisonState(symbols21, action363),
        buildJisonState(symbols2, action364),
        mapOf(
            43 to action365,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        mapOf(
            10 to action61,
            18 to action366,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action370,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action378,
            104 to action379,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action380,
            104 to action381,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        mapOf(
            105 to action382,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action383,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        mapOf(
            44 to action32,
            47 to action384,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        buildJisonState(symbols25, action385),
        mapOf(
            80 to action386,
        ),
        mapOf(
            80 to action387,
            98 to action388,
        ),
        buildJisonState(symbols25, action389),
        buildJisonState(symbols16, action390, mapOf(
            10 to action391,
        )),
        buildJisonState(symbols25, action392),
        buildJisonState(symbols5, action393),
        buildJisonState(symbols23, action394),
    )

    private fun stateChunk11(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols5, action395),
        mapOf(
            51 to action396,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols5, action397),
        buildJisonState(symbols5, action398),
        buildJisonState(symbols5, action399),
        buildJisonState(symbols5, action400),
        mapOf(
            109 to action401,
        ),
        buildJisonState(symbols5, action402),
        buildJisonState(symbols5, action403),
        mapOf(
            66 to action404,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols5, action405),
        buildJisonState(symbols5, action406),
        buildJisonState(symbols5, action407),
        buildJisonState(symbols5, action408),
        buildJisonState(symbols5, action409),
        buildJisonState(symbols19, action410),
        buildJisonState(symbols24, action411),
        mapOf(
            31 to action412,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            6 to action13,
            7 to action14,
            8 to action15,
            9 to action16,
            10 to action17,
            11 to action18,
            20 to action19,
            22 to action20,
            23 to action21,
            24 to action22,
            25 to action23,
            26 to action24,
            27 to action25,
            32 to action413,
            33 to action26,
            34 to action27,
            36 to action28,
            38 to action29,
            42 to action30,
            43 to action31,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            84 to action36,
            85 to action37,
            86 to action38,
            87 to action39,
            88 to action40,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
            121 to action51,
            122 to action52,
            123 to action53,
            124 to action54,
            125 to action55,
        ),
        buildJisonState(symbols4, action414),
    )

    private fun stateChunk12(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            43 to action415,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
        ),
        buildJisonState(symbols26, action416, mapOf(
            106 to action417,
        )),
        buildJisonState(symbols27, action418, mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            105 to action371,
            108 to action419,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        )),
        buildJisonState(symbols28, action420),
        buildJisonState(symbols28, action421),
        buildJisonState(symbols28, action422),
        buildJisonState(symbols28, action423),
        buildJisonState(symbols28, action424),
        buildJisonState(symbols28, action425),
        buildJisonState(symbols28, action426),
        buildJisonState(symbols28, action427),
        buildJisonState(symbols28, action428),
        buildJisonState(symbols26, action429, mapOf(
            106 to action417,
        )),
        mapOf(
            10 to action430,
        ),
        buildJisonState(symbols26, action431, mapOf(
            106 to action417,
        )),
        mapOf(
            10 to action432,
        ),
        mapOf(
            10 to action433,
            106 to action433,
        ),
        buildJisonState(symbols26, action434, mapOf(
            106 to action417,
        )),
        buildJisonState(symbols29, action435, mapOf(
            44 to action32,
            60 to action35,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action135,
            114 to action48,
            115 to action49,
            116 to action50,
        )),
        buildJisonState(symbols25, action436),
    )

    private fun stateChunk13(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols16, action437, mapOf(
            10 to action438,
        )),
        buildJisonState(symbols25, action439),
        mapOf(
            98 to action440,
        ),
        mapOf(
            51 to action441,
        ),
        mapOf(
            62 to action442,
        ),
        mapOf(
            66 to action443,
        ),
        mapOf(
            8 to action70,
            9 to action71,
            11 to action72,
            21 to action444,
        ),
        buildJisonState(symbols2, action445),
        buildJisonState(symbols4, action446),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            105 to action371,
            107 to action447,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        buildJisonState(symbols28, action448),
        mapOf(
            14 to action98,
            44 to action99,
            60 to action100,
            89 to action102,
            101 to action449,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action113,
        ),
        mapOf(
            14 to action98,
            44 to action99,
            60 to action100,
            89 to action102,
            101 to action450,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action113,
        ),
        mapOf(
            98 to action451,
        ),
        buildJisonState(symbols25, action452),
        buildJisonState(symbols5, action453),
        mapOf(
            30 to action454,
            67 to action241,
            80 to action242,
            81 to action243,
            82 to action244,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        buildJisonState(symbols5, action455),
        buildJisonState(symbols11, action6, mapOf(
            5 to action456,
        )),
        buildJisonState(symbols27, action457, mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            105 to action371,
            108 to action419,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        )),
    )

    private fun stateChunk14(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols15, action458, mapOf(
            10 to action459,
            14 to action98,
            44 to action99,
            60 to action100,
            89 to action102,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action224,
        )),
        buildJisonState(symbols15, action460, mapOf(
            10 to action461,
            14 to action98,
            44 to action99,
            60 to action100,
            89 to action102,
            105 to action106,
            106 to action107,
            109 to action108,
            111 to action109,
            114 to action110,
            115 to action111,
            116 to action112,
            120 to action224,
        )),
        buildJisonState(symbols25, action462),
        mapOf(
            31 to action463,
            67 to action241,
            82 to action330,
            116 to action245,
            117 to action246,
            118 to action247,
        ),
        mapOf(
            6 to action13,
            7 to action14,
            8 to action15,
            9 to action16,
            10 to action17,
            11 to action18,
            20 to action19,
            22 to action20,
            23 to action21,
            24 to action22,
            25 to action23,
            26 to action24,
            27 to action25,
            32 to action464,
            33 to action26,
            34 to action27,
            36 to action28,
            38 to action29,
            42 to action30,
            43 to action31,
            44 to action32,
            45 to action33,
            47 to action34,
            60 to action35,
            84 to action36,
            85 to action37,
            86 to action38,
            87 to action39,
            88 to action40,
            89 to action41,
            102 to action42,
            105 to action43,
            106 to action44,
            109 to action45,
            111 to action46,
            113 to action47,
            114 to action48,
            115 to action49,
            116 to action50,
            121 to action51,
            122 to action52,
            123 to action53,
            124 to action54,
            125 to action55,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action465,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        mapOf(
            10 to action367,
            60 to action368,
            84 to action369,
            92 to action466,
            105 to action371,
            107 to action372,
            108 to action373,
            109 to action374,
            110 to action375,
            111 to action376,
            112 to action377,
        ),
        buildJisonState(symbols5, action467),
        buildJisonState(symbols2, action468),
        buildJisonState(symbols26, action469, mapOf(
            106 to action417,
        )),
        buildJisonState(symbols26, action470, mapOf(
            106 to action417,
        )),
    )

    val lexerPatterns: List<JisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
        addAll(lexerChunk5())
        addAll(lexerChunk6())
    }

    private fun lexerChunk0(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(0, "^(?:accTitle\\s*:\\s*)"),
        JisonLexerPattern(1, "^(?:(?!\\n||)*[^\\n]*)"),
        JisonLexerPattern(2, "^(?:accDescr\\s*:\\s*)"),
        JisonLexerPattern(3, "^(?:(?!\\n||)*[^\\n]*)"),
        JisonLexerPattern(4, "^(?:accDescr\\s*\\{\\s*)"),
        JisonLexerPattern(5, "^(?:[\\}])"),
        JisonLexerPattern(6, "^(?:[^\\}]*)"),
        JisonLexerPattern(7, "^(?:@\\{)"),
        JisonLexerPattern(8, "^(?:[\"])"),
        JisonLexerPattern(9, "^(?:[\"])"),
        JisonLexerPattern(10, "^(?:[^\\\"]+)"),
        JisonLexerPattern(11, "^(?:[^}^\"]+)"),
        JisonLexerPattern(12, "^(?:\\})"),
        JisonLexerPattern(13, "^(?:call[\\s]+)"),
        JisonLexerPattern(14, "^(?:\\([\\s]*\\))"),
        JisonLexerPattern(15, "^(?:\\()"),
        JisonLexerPattern(16, "^(?:[^(]*)"),
        JisonLexerPattern(17, "^(?:\\))"),
        JisonLexerPattern(18, "^(?:[^)]*)"),
        JisonLexerPattern(19, "^(?:[^`\"]+)"),
    )

    private fun lexerChunk1(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(20, "^(?:[`][\"])"),
        JisonLexerPattern(21, "^(?:[\"][`])"),
        JisonLexerPattern(22, "^(?:[^\"]+)"),
        JisonLexerPattern(23, "^(?:[\"])"),
        JisonLexerPattern(24, "^(?:[\"])"),
        JisonLexerPattern(25, "^(?:style\\b)"),
        JisonLexerPattern(26, "^(?:default\\b)"),
        JisonLexerPattern(27, "^(?:linkStyle\\b)"),
        JisonLexerPattern(28, "^(?:interpolate\\b)"),
        JisonLexerPattern(29, "^(?:classDef\\b)"),
        JisonLexerPattern(30, "^(?:class\\b)"),
        JisonLexerPattern(31, "^(?:href[\\s])"),
        JisonLexerPattern(32, "^(?:click[\\s]+)"),
        JisonLexerPattern(33, "^(?:[\\s\\n])"),
        JisonLexerPattern(34, "^(?:[^\\s\\n]*)"),
        JisonLexerPattern(35, "^(?:flowchart-elk\\b)"),
        JisonLexerPattern(36, "^(?:swimlane-beta\\b)"),
        JisonLexerPattern(37, "^(?:graph\\b)"),
        JisonLexerPattern(38, "^(?:flowchart\\b)"),
        JisonLexerPattern(39, "^(?:subgraph\\b)"),
    )

    private fun lexerChunk2(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(40, "^(?:end\\b\\s*)"),
        JisonLexerPattern(41, "^(?:_self\\b)"),
        JisonLexerPattern(42, "^(?:_blank\\b)"),
        JisonLexerPattern(43, "^(?:_parent\\b)"),
        JisonLexerPattern(44, "^(?:_top\\b)"),
        JisonLexerPattern(45, "^(?:(\\r?\\n)*\\s*\\n)"),
        JisonLexerPattern(46, "^(?:\\s*LR\\b)"),
        JisonLexerPattern(47, "^(?:\\s*RL\\b)"),
        JisonLexerPattern(48, "^(?:\\s*TB\\b)"),
        JisonLexerPattern(49, "^(?:\\s*BT\\b)"),
        JisonLexerPattern(50, "^(?:\\s*TD\\b)"),
        JisonLexerPattern(51, "^(?:\\s*BR\\b)"),
        JisonLexerPattern(52, "^(?:\\s*<)"),
        JisonLexerPattern(53, "^(?:\\s*>)"),
        JisonLexerPattern(54, "^(?:\\s*\\^)"),
        JisonLexerPattern(55, "^(?:\\s*v\\b)"),
        JisonLexerPattern(56, "^(?:.*direction\\s+TB[^\\n]*)"),
        JisonLexerPattern(57, "^(?:.*direction\\s+BT[^\\n]*)"),
        JisonLexerPattern(58, "^(?:.*direction\\s+RL[^\\n]*)"),
        JisonLexerPattern(59, "^(?:.*direction\\s+LR[^\\n]*)"),
    )

    private fun lexerChunk3(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(60, "^(?:.*direction\\s+TD[^\\n]*)"),
        JisonLexerPattern(61, "^(?:[^\\s\\\"]+@(?=[^\\{\\\"]))"),
        JisonLexerPattern(62, "^(?:[0-9]+)"),
        JisonLexerPattern(63, "^(?:#)"),
        JisonLexerPattern(64, "^(?::::)"),
        JisonLexerPattern(65, "^(?::)"),
        JisonLexerPattern(66, "^(?:&)"),
        JisonLexerPattern(67, "^(?:;)"),
        JisonLexerPattern(68, "^(?:,)"),
        JisonLexerPattern(69, "^(?:\\*)"),
        JisonLexerPattern(70, "^(?:\\s*[xo<]?--+[-xo>]\\s*)"),
        JisonLexerPattern(71, "^(?:\\s*[xo<]?--\\s*)"),
        JisonLexerPattern(72, "^(?:[^-]|-(?!-)+)"),
        JisonLexerPattern(73, "^(?:\\s*[xo<]?==+[=xo>]\\s*)"),
        JisonLexerPattern(74, "^(?:\\s*[xo<]?==\\s*)"),
        JisonLexerPattern(75, "^(?:[^=]|=(?!))"),
        JisonLexerPattern(76, "^(?:\\s*[xo<]?-?\\.+-[xo>]?\\s*)"),
        JisonLexerPattern(77, "^(?:\\s*[xo<]?-\\.\\s*)"),
        JisonLexerPattern(78, "^(?:[^\\.]|\\.(?!))"),
        JisonLexerPattern(79, "^(?:\\s*~~[\\~]+\\s*)"),
    )

    private fun lexerChunk4(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(80, "^(?:[-/\\)][\\)])"),
        JisonLexerPattern(81, "^(?:[^\\(\\)\\[\\]\\{\\}]|!\\)+)"),
        JisonLexerPattern(82, "^(?:\\(-)"),
        JisonLexerPattern(83, "^(?:\\]\\))"),
        JisonLexerPattern(84, "^(?:\\(\\[)"),
        JisonLexerPattern(85, "^(?:\\]\\])"),
        JisonLexerPattern(86, "^(?:\\[\\[)"),
        JisonLexerPattern(87, "^(?:\\[\\|)"),
        JisonLexerPattern(88, "^(?:>)"),
        JisonLexerPattern(89, "^(?:\\)\\])"),
        JisonLexerPattern(90, "^(?:\\[\\()"),
        JisonLexerPattern(91, "^(?:\\)\\)\\))"),
        JisonLexerPattern(92, "^(?:\\(\\(\\()"),
        JisonLexerPattern(93, "^(?:[\\\\(?=\\])][\\]])"),
        JisonLexerPattern(94, "^(?:\\/(?=\\])\\])"),
        JisonLexerPattern(95, "^(?:\\/(?!\\])|\\\\(?!\\])|[^\\\\\\[\\]\\(\\)\\{\\}\\/]+)"),
        JisonLexerPattern(96, "^(?:\\[\\/)"),
        JisonLexerPattern(97, "^(?:\\[\\\\)"),
        JisonLexerPattern(98, "^(?:<)"),
        JisonLexerPattern(99, "^(?:>)"),
    )

    private fun lexerChunk5(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(100, "^(?:\\^)"),
        JisonLexerPattern(101, "^(?:\\\\\\|)"),
        JisonLexerPattern(102, "^(?:v\\b)"),
        JisonLexerPattern(103, "^(?:\\*)"),
        JisonLexerPattern(104, "^(?:#)"),
        JisonLexerPattern(105, "^(?:&)"),
        JisonLexerPattern(106, "^(?:([A-Za-z0-9!\"\\#\$%&'*+\\.`?\\\\_\\/]|-(?=[^\\>\\-\\.])|(?!))+)"),
        JisonLexerPattern(107, "^(?:-)"),
        JisonLexerPattern(108, "^(?:[\\u00AA\\u00B5\\u00BA\\u00C0-\\u00D6\\u00D8-\\u00F6]|[\\u00F8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0370-\\u0374\\u0376\\u0377]|[\\u037A-\\u037D\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5]|[\\u03F7-\\u0481\\u048A-\\u0527\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05D0-\\u05EA]|[\\u05F0-\\u05F2\\u0620-\\u064A\\u066E\\u066F\\u0671-\\u06D3\\u06D5\\u06E5\\u06E6\\u06EE]|[\\u06EF\\u06FA-\\u06FC\\u06FF\\u0710\\u0712-\\u072F\\u074D-\\u07A5\\u07B1\\u07CA-\\u07EA]|[\\u07F4\\u07F5\\u07FA\\u0800-\\u0815\\u081A\\u0824\\u0828\\u0840-\\u0858\\u08A0]|[\\u08A2-\\u08AC\\u0904-\\u0939\\u093D\\u0950\\u0958-\\u0961\\u0971-\\u0977]|[\\u0979-\\u097F\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2]|[\\u09B6-\\u09B9\\u09BD\\u09CE\\u09DC\\u09DD\\u09DF-\\u09E1\\u09F0\\u09F1\\u0A05-\\u0A0A]|[\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39]|[\\u0A59-\\u0A5C\\u0A5E\\u0A72-\\u0A74\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8]|[\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABD\\u0AD0\\u0AE0\\u0AE1\\u0B05-\\u0B0C]|[\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3D\\u0B5C]|[\\u0B5D\\u0B5F-\\u0B61\\u0B71\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99]|[\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BD0]|[\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C33\\u0C35-\\u0C39\\u0C3D]|[\\u0C58\\u0C59\\u0C60\\u0C61\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3]|[\\u0CB5-\\u0CB9\\u0CBD\\u0CDE\\u0CE0\\u0CE1\\u0CF1\\u0CF2\\u0D05-\\u0D0C\\u0D0E-\\u0D10]|[\\u0D12-\\u0D3A\\u0D3D\\u0D4E\\u0D60\\u0D61\\u0D7A-\\u0D7F\\u0D85-\\u0D96\\u0D9A-\\u0DB1]|[\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0E01-\\u0E30\\u0E32\\u0E33\\u0E40-\\u0E46\\u0E81]|[\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3]|[\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB0\\u0EB2\\u0EB3\\u0EBD\\u0EC0-\\u0EC4\\u0EC6]|[\\u0EDC-\\u0EDF\\u0F00\\u0F40-\\u0F47\\u0F49-\\u0F6C\\u0F88-\\u0F8C\\u1000-\\u102A]|[\\u103F\\u1050-\\u1055\\u105A-\\u105D\\u1061\\u1065\\u1066\\u106E-\\u1070\\u1075-\\u1081]|[\\u108E\\u10A0-\\u10C5\\u10C7\\u10CD\\u10D0-\\u10FA\\u10FC-\\u1248\\u124A-\\u124D]|[\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0]|[\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310]|[\\u1312-\\u1315\\u1318-\\u135A\\u1380-\\u138F\\u13A0-\\u13F4\\u1401-\\u166C]|[\\u166F-\\u167F\\u1681-\\u169A\\u16A0-\\u16EA\\u1700-\\u170C\\u170E-\\u1711]|[\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176C\\u176E-\\u1770\\u1780-\\u17B3\\u17D7]|[\\u17DC\\u1820-\\u1877\\u1880-\\u18A8\\u18AA\\u18B0-\\u18F5\\u1900-\\u191C]|[\\u1950-\\u196D\\u1970-\\u1974\\u1980-\\u19AB\\u19C1-\\u19C7\\u1A00-\\u1A16]|[\\u1A20-\\u1A54\\u1AA7\\u1B05-\\u1B33\\u1B45-\\u1B4B\\u1B83-\\u1BA0\\u1BAE\\u1BAF]|[\\u1BBA-\\u1BE5\\u1C00-\\u1C23\\u1C4D-\\u1C4F\\u1C5A-\\u1C7D\\u1CE9-\\u1CEC]|[\\u1CEE-\\u1CF1\\u1CF5\\u1CF6\\u1D00-\\u1DBF\\u1E00-\\u1F15\\u1F18-\\u1F1D]|[\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D]|[\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3]|[\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u2071\\u207F]|[\\u2090-\\u209C\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128]|[\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2183\\u2184]|[\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2CE4\\u2CEB-\\u2CEE\\u2CF2\\u2CF3]|[\\u2D00-\\u2D25\\u2D27\\u2D2D\\u2D30-\\u2D67\\u2D6F\\u2D80-\\u2D96\\u2DA0-\\u2DA6]|[\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE]|[\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2E2F\\u3005\\u3006\\u3031-\\u3035\\u303B\\u303C]|[\\u3041-\\u3096\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312D]|[\\u3131-\\u318E\\u31A0-\\u31BA\\u31F0-\\u31FF\\u3400-\\u4DB5\\u4E00-\\u9FCC]|[\\uA000-\\uA48C\\uA4D0-\\uA4FD\\uA500-\\uA60C\\uA610-\\uA61F\\uA62A\\uA62B]|[\\uA640-\\uA66E\\uA67F-\\uA697\\uA6A0-\\uA6E5\\uA717-\\uA71F\\uA722-\\uA788]|[\\uA78B-\\uA78E\\uA790-\\uA793\\uA7A0-\\uA7AA\\uA7F8-\\uA801\\uA803-\\uA805]|[\\uA807-\\uA80A\\uA80C-\\uA822\\uA840-\\uA873\\uA882-\\uA8B3\\uA8F2-\\uA8F7\\uA8FB]|[\\uA90A-\\uA925\\uA930-\\uA946\\uA960-\\uA97C\\uA984-\\uA9B2\\uA9CF\\uAA00-\\uAA28]|[\\uAA40-\\uAA42\\uAA44-\\uAA4B\\uAA60-\\uAA76\\uAA7A\\uAA80-\\uAAAF\\uAAB1\\uAAB5]|[\\uAAB6\\uAAB9-\\uAABD\\uAAC0\\uAAC2\\uAADB-\\uAADD\\uAAE0-\\uAAEA\\uAAF2-\\uAAF4]|[\\uAB01-\\uAB06\\uAB09-\\uAB0E\\uAB11-\\uAB16\\uAB20-\\uAB26\\uAB28-\\uAB2E]|[\\uABC0-\\uABE2\\uAC00-\\uD7A3\\uD7B0-\\uD7C6\\uD7CB-\\uD7FB\\uF900-\\uFA6D]|[\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D\\uFB1F-\\uFB28\\uFB2A-\\uFB36]|[\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D]|[\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE70-\\uFE74\\uFE76-\\uFEFC]|[\\uFF21-\\uFF3A\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF]|[\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC])"),
        JisonLexerPattern(109, "^(?:\\|)"),
        JisonLexerPattern(110, "^(?:\\|)"),
        JisonLexerPattern(111, "^(?:\\))"),
        JisonLexerPattern(112, "^(?:\\()"),
        JisonLexerPattern(113, "^(?:\\])"),
        JisonLexerPattern(114, "^(?:\\[)"),
        JisonLexerPattern(115, "^(?:(\\}))"),
        JisonLexerPattern(116, "^(?:\\{)"),
        JisonLexerPattern(117, "^(?:[^\\[\\]\\(\\)\\{\\}\\|\\\"]+)"),
        JisonLexerPattern(118, "^(?:\")"),
        JisonLexerPattern(119, "^(?:(\\r?\\n)+)"),
    )

    private fun lexerChunk6(): List<JisonLexerPattern> = listOf(
        JisonLexerPattern(120, "^(?:[^\\S\\n\\r]+)"),
        JisonLexerPattern(121, "^(?:\$)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "shapeDataEndBracket" to intArrayOf(21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "shapeDataStr" to intArrayOf(9, 10, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "shapeData" to intArrayOf(8, 11, 12, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "callbackargs" to intArrayOf(17, 18, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "callbackname" to intArrayOf(14, 15, 16, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "href" to intArrayOf(21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "click" to intArrayOf(21, 24, 33, 34, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "dottedEdgeText" to intArrayOf(21, 24, 76, 78, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "thickEdgeText" to intArrayOf(21, 24, 73, 75, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "edgeText" to intArrayOf(21, 24, 70, 72, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "trapText" to intArrayOf(21, 24, 79, 82, 84, 86, 90, 92, 93, 94, 95, 96, 97, 110, 112, 114, 116),
        "ellipseText" to intArrayOf(21, 24, 79, 80, 81, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "text" to intArrayOf(21, 24, 79, 82, 83, 84, 85, 86, 89, 90, 91, 92, 96, 97, 109, 110, 111, 112, 113, 114, 115, 116, 117),
        "vertex" to intArrayOf(21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "dir" to intArrayOf(21, 24, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "acc_descr_multiline" to intArrayOf(5, 6, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "acc_descr" to intArrayOf(3, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "acc_title" to intArrayOf(1, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "md_string" to intArrayOf(19, 20, 21, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "string" to intArrayOf(21, 22, 23, 24, 79, 82, 84, 86, 90, 92, 96, 97, 110, 112, 114, 116),
        "INITIAL" to intArrayOf(0, 2, 4, 7, 13, 21, 24, 25, 26, 27, 28, 29, 30, 31, 32, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 73, 74, 76, 77, 79, 82, 84, 86, 87, 88, 90, 92, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 110, 112, 114, 116, 118, 119, 120, 121),
    )
}
