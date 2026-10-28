package com.swithun.cmpmermaid.core.agentflow.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.JisonCell
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.JisonProduction

/**
 * Generated from Mermaid 12.0.0 agentflow.jison.
 * Upstream agentflow.jison SHA-256: 8bb23d09c1dcdf4a3d3139c4ba29b8a823d726b755c69067010a74cb621fd2a6
 * Distribution chunk SHA-256: f7192aef4952840d375a20f2a7bffa4b93c0fdb777c9a22aca1d70897393c0f8
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:agentflow-parser
 */
internal object AgentflowJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "graphConfig" to 4,
        "document" to 5,
        "line" to 6,
        "statement" to 7,
        "COMMENT" to 8,
        "SEMI" to 9,
        "NEWLINE" to 10,
        "SPACE" to 11,
        "EOF" to 12,
        "GRAPH" to 13,
        "NODIR" to 14,
        "DIR" to 15,
        "FirstStmtSeparator" to 16,
        "ending" to 17,
        "endToken" to 18,
        "spaceList" to 19,
        "spaceListNewline" to 20,
        "vertexStatement" to 21,
        "separator" to 22,
        "styleStatement" to 23,
        "linkStyleStatement" to 24,
        "classDefStatement" to 25,
        "classStatement" to 26,
        "clickStatement" to 27,
        "flow" to 28,
        "textNoTags" to 29,
        "SQS" to 30,
        "text" to 31,
        "SQE" to 32,
        "end" to 33,
        "shapeData" to 34,
        "global" to 35,
        "connector" to 36,
        "direction" to 37,
        "acc_title" to 38,
        "acc_title_value" to 39,
        "acc_descr" to 40,
        "acc_descr_value" to 41,
        "acc_descr_multiline_value" to 42,
        "SHAPE_DATA" to 43,
        "link" to 44,
        "node" to 45,
        "styledVertex" to 46,
        "AMP" to 47,
        "vertex" to 48,
        "STYLE_SEPARATOR" to 49,
        "idString" to 50,
        "DOUBLECIRCLESTART" to 51,
        "DOUBLECIRCLEEND" to 52,
        "PS" to 53,
        "PE" to 54,
        "(-" to 55,
        "-)" to 56,
        "STADIUMSTART" to 57,
        "STADIUMEND" to 58,
        "SUBROUTINESTART" to 59,
        "SUBROUTINEEND" to 60,
        "VERTEX_WITH_PROPS_START" to 61,
        "NODE_STRING[field]" to 62,
        "COLON" to 63,
        "NODE_STRING[value]" to 64,
        "PIPE" to 65,
        "CYLINDERSTART" to 66,
        "CYLINDEREND" to 67,
        "DIAMOND_START" to 68,
        "DIAMOND_STOP" to 69,
        "TAGEND" to 70,
        "TRAPSTART" to 71,
        "TRAPEND" to 72,
        "INVTRAPSTART" to 73,
        "INVTRAPEND" to 74,
        "linkStatement" to 75,
        "arrowText" to 76,
        "TESTSTR" to 77,
        "START_LINK" to 78,
        "edgeText" to 79,
        "LINK" to 80,
        "LINK_ID" to 81,
        "edgeTextToken" to 82,
        "STR" to 83,
        "MD_STR" to 84,
        "textToken" to 85,
        "keywords" to 86,
        "STYLE" to 87,
        "LINKSTYLE" to 88,
        "CLASSDEF" to 89,
        "CLASS" to 90,
        "CLICK" to 91,
        "DOWN" to 92,
        "UP" to 93,
        "textNoTagsToken" to 94,
        "stylesOpt" to 95,
        "idString[vertex]" to 96,
        "idString[class]" to 97,
        "CALLBACKNAME" to 98,
        "CALLBACKARGS" to 99,
        "HREF" to 100,
        "LINK_TARGET" to 101,
        "STR[link]" to 102,
        "STR[tooltip]" to 103,
        "alphaNum" to 104,
        "DEFAULT" to 105,
        "numList" to 106,
        "INTERPOLATE" to 107,
        "NUM" to 108,
        "COMMA" to 109,
        "style" to 110,
        "styleComponent" to 111,
        "NODE_STRING" to 112,
        "UNIT" to 113,
        "BRKT" to 114,
        "PCT" to 115,
        "idStringToken" to 116,
        "MINUS" to 117,
        "MULT" to 118,
        "UNICODE_TEXT" to 119,
        "TEXT" to 120,
        "TAGSTART" to 121,
        "EDGE_TEXT" to 122,
        "alphaNumToken" to 123,
        "direction_tb" to 124,
        "direction_bt" to 125,
        "direction_rl" to 126,
        "direction_lr" to 127,
        "direction_td" to 128,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        8 to "COMMENT",
        9 to "SEMI",
        10 to "NEWLINE",
        11 to "SPACE",
        12 to "EOF",
        13 to "GRAPH",
        14 to "NODIR",
        15 to "DIR",
        28 to "flow",
        30 to "SQS",
        32 to "SQE",
        33 to "end",
        35 to "global",
        36 to "connector",
        38 to "acc_title",
        39 to "acc_title_value",
        40 to "acc_descr",
        41 to "acc_descr_value",
        42 to "acc_descr_multiline_value",
        43 to "SHAPE_DATA",
        47 to "AMP",
        49 to "STYLE_SEPARATOR",
        51 to "DOUBLECIRCLESTART",
        52 to "DOUBLECIRCLEEND",
        53 to "PS",
        54 to "PE",
        55 to "(-",
        56 to "-)",
        57 to "STADIUMSTART",
        58 to "STADIUMEND",
        59 to "SUBROUTINESTART",
        60 to "SUBROUTINEEND",
        61 to "VERTEX_WITH_PROPS_START",
        62 to "NODE_STRING[field]",
        63 to "COLON",
        64 to "NODE_STRING[value]",
        65 to "PIPE",
        66 to "CYLINDERSTART",
        67 to "CYLINDEREND",
        68 to "DIAMOND_START",
        69 to "DIAMOND_STOP",
        70 to "TAGEND",
        71 to "TRAPSTART",
        72 to "TRAPEND",
        73 to "INVTRAPSTART",
        74 to "INVTRAPEND",
        77 to "TESTSTR",
        78 to "START_LINK",
        80 to "LINK",
        81 to "LINK_ID",
        83 to "STR",
        84 to "MD_STR",
        87 to "STYLE",
        88 to "LINKSTYLE",
        89 to "CLASSDEF",
        90 to "CLASS",
        91 to "CLICK",
        92 to "DOWN",
        93 to "UP",
        96 to "idString[vertex]",
        97 to "idString[class]",
        98 to "CALLBACKNAME",
        99 to "CALLBACKARGS",
        100 to "HREF",
        101 to "LINK_TARGET",
        102 to "STR[link]",
        103 to "STR[tooltip]",
        105 to "DEFAULT",
        107 to "INTERPOLATE",
        108 to "NUM",
        109 to "COMMA",
        112 to "NODE_STRING",
        113 to "UNIT",
        114 to "BRKT",
        115 to "PCT",
        117 to "MINUS",
        118 to "MULT",
        119 to "UNICODE_TEXT",
        120 to "TEXT",
        121 to "TAGSTART",
        122 to "EDGE_TEXT",
        124 to "direction_tb",
        125 to "direction_bt",
        126 to "direction_rl",
        127 to "direction_lr",
        128 to "direction_td",
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
        JisonProduction(symbol = 6, length = 1),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 2),
        JisonProduction(symbol = 4, length = 3),
        JisonProduction(symbol = 17, length = 2),
        JisonProduction(symbol = 17, length = 1),
        JisonProduction(symbol = 18, length = 1),
        JisonProduction(symbol = 18, length = 1),
        JisonProduction(symbol = 18, length = 1),
        JisonProduction(symbol = 16, length = 1),
        JisonProduction(symbol = 16, length = 1),
        JisonProduction(symbol = 16, length = 2),
        JisonProduction(symbol = 16, length = 1),
        JisonProduction(symbol = 20, length = 2),
        JisonProduction(symbol = 20, length = 2),
        JisonProduction(symbol = 20, length = 2),
        JisonProduction(symbol = 20, length = 1),
        JisonProduction(symbol = 20, length = 1),
        JisonProduction(symbol = 20, length = 1),
        JisonProduction(symbol = 19, length = 2),
        JisonProduction(symbol = 19, length = 1),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 9),
        JisonProduction(symbol = 7, length = 10),
        JisonProduction(symbol = 7, length = 6),
        JisonProduction(symbol = 7, length = 7),
        JisonProduction(symbol = 7, length = 4),
        JisonProduction(symbol = 7, length = 4),
        JisonProduction(symbol = 7, length = 7),
        JisonProduction(symbol = 7, length = 8),
        JisonProduction(symbol = 7, length = 4),
        JisonProduction(symbol = 7, length = 5),
        JisonProduction(symbol = 7, length = 1),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 2),
        JisonProduction(symbol = 7, length = 1),
        JisonProduction(symbol = 22, length = 1),
        JisonProduction(symbol = 22, length = 1),
        JisonProduction(symbol = 22, length = 1),
        JisonProduction(symbol = 22, length = 1),
        JisonProduction(symbol = 34, length = 2),
        JisonProduction(symbol = 34, length = 1),
        JisonProduction(symbol = 21, length = 4),
        JisonProduction(symbol = 21, length = 3),
        JisonProduction(symbol = 21, length = 4),
        JisonProduction(symbol = 21, length = 2),
        JisonProduction(symbol = 21, length = 2),
        JisonProduction(symbol = 21, length = 1),
        JisonProduction(symbol = 45, length = 1),
        JisonProduction(symbol = 45, length = 6),
        JisonProduction(symbol = 45, length = 5),
        JisonProduction(symbol = 46, length = 1),
        JisonProduction(symbol = 46, length = 3),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 6),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 8),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 6),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 4),
        JisonProduction(symbol = 48, length = 1),
        JisonProduction(symbol = 44, length = 2),
        JisonProduction(symbol = 44, length = 3),
        JisonProduction(symbol = 44, length = 3),
        JisonProduction(symbol = 44, length = 1),
        JisonProduction(symbol = 44, length = 3),
        JisonProduction(symbol = 44, length = 4),
        JisonProduction(symbol = 79, length = 1),
        JisonProduction(symbol = 79, length = 2),
        JisonProduction(symbol = 79, length = 1),
        JisonProduction(symbol = 79, length = 1),
        JisonProduction(symbol = 75, length = 1),
        JisonProduction(symbol = 75, length = 2),
        JisonProduction(symbol = 76, length = 3),
        JisonProduction(symbol = 31, length = 1),
        JisonProduction(symbol = 31, length = 2),
        JisonProduction(symbol = 31, length = 1),
        JisonProduction(symbol = 31, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 86, length = 1),
        JisonProduction(symbol = 29, length = 1),
        JisonProduction(symbol = 29, length = 2),
        JisonProduction(symbol = 29, length = 1),
        JisonProduction(symbol = 29, length = 1),
        JisonProduction(symbol = 25, length = 5),
        JisonProduction(symbol = 26, length = 5),
        JisonProduction(symbol = 27, length = 2),
        JisonProduction(symbol = 27, length = 4),
        JisonProduction(symbol = 27, length = 3),
        JisonProduction(symbol = 27, length = 5),
        JisonProduction(symbol = 27, length = 3),
        JisonProduction(symbol = 27, length = 5),
        JisonProduction(symbol = 27, length = 5),
        JisonProduction(symbol = 27, length = 7),
        JisonProduction(symbol = 27, length = 2),
        JisonProduction(symbol = 27, length = 4),
        JisonProduction(symbol = 27, length = 2),
        JisonProduction(symbol = 27, length = 4),
        JisonProduction(symbol = 27, length = 4),
        JisonProduction(symbol = 27, length = 6),
        JisonProduction(symbol = 23, length = 5),
        JisonProduction(symbol = 24, length = 5),
        JisonProduction(symbol = 24, length = 5),
        JisonProduction(symbol = 24, length = 9),
        JisonProduction(symbol = 24, length = 9),
        JisonProduction(symbol = 24, length = 7),
        JisonProduction(symbol = 24, length = 7),
        JisonProduction(symbol = 106, length = 1),
        JisonProduction(symbol = 106, length = 3),
        JisonProduction(symbol = 95, length = 1),
        JisonProduction(symbol = 95, length = 3),
        JisonProduction(symbol = 110, length = 1),
        JisonProduction(symbol = 110, length = 2),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 111, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 116, length = 1),
        JisonProduction(symbol = 85, length = 1),
        JisonProduction(symbol = 85, length = 1),
        JisonProduction(symbol = 85, length = 1),
        JisonProduction(symbol = 85, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 94, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 82, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 123, length = 1),
        JisonProduction(symbol = 50, length = 1),
        JisonProduction(symbol = 50, length = 2),
        JisonProduction(symbol = 104, length = 1),
        JisonProduction(symbol = 104, length = 2),
        JisonProduction(symbol = 37, length = 1),
        JisonProduction(symbol = 37, length = 1),
        JisonProduction(symbol = 37, length = 1),
        JisonProduction(symbol = 37, length = 1),
        JisonProduction(symbol = 37, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: JisonCell = JisonCell.Goto(1)
    private val action1: JisonCell = JisonCell.Goto(2)
    private val action2: JisonCell = JisonCell.Shift(5)
    private val action3: JisonCell = JisonCell.Shift(4)
    private val action4: JisonCell = JisonCell.Shift(3)
    private val action5: JisonCell = JisonCell.Shift(6)
    private val action6: JisonCell = JisonCell.Accept
    private val action7: JisonCell = JisonCell.Reduce(2)
    private val action8: JisonCell = JisonCell.Goto(7)
    private val action9: JisonCell = JisonCell.Goto(8)
    private val action10: JisonCell = JisonCell.Goto(9)
    private val action11: JisonCell = JisonCell.Goto(10)
    private val action12: JisonCell = JisonCell.Shift(11)
    private val action13: JisonCell = JisonCell.Shift(12)
    private val action14: JisonCell = JisonCell.Reduce(1)
    private val action15: JisonCell = JisonCell.Goto(13)
    private val action16: JisonCell = JisonCell.Goto(14)
    private val action17: JisonCell = JisonCell.Shift(15)
    private val action18: JisonCell = JisonCell.Shift(16)
    private val action19: JisonCell = JisonCell.Shift(17)
    private val action20: JisonCell = JisonCell.Shift(18)
    private val action21: JisonCell = JisonCell.Shift(19)
    private val action22: JisonCell = JisonCell.Goto(20)
    private val action23: JisonCell = JisonCell.Goto(21)
    private val action24: JisonCell = JisonCell.Goto(22)
    private val action25: JisonCell = JisonCell.Goto(23)
    private val action26: JisonCell = JisonCell.Goto(24)
    private val action27: JisonCell = JisonCell.Goto(25)
    private val action28: JisonCell = JisonCell.Shift(26)
    private val action29: JisonCell = JisonCell.Shift(27)
    private val action30: JisonCell = JisonCell.Shift(28)
    private val action31: JisonCell = JisonCell.Goto(29)
    private val action32: JisonCell = JisonCell.Shift(30)
    private val action33: JisonCell = JisonCell.Shift(31)
    private val action34: JisonCell = JisonCell.Shift(32)
    private val action35: JisonCell = JisonCell.Goto(33)
    private val action36: JisonCell = JisonCell.Goto(44)
    private val action37: JisonCell = JisonCell.Shift(55)
    private val action38: JisonCell = JisonCell.Goto(45)
    private val action39: JisonCell = JisonCell.Goto(46)
    private val action40: JisonCell = JisonCell.Shift(54)
    private val action41: JisonCell = JisonCell.Shift(34)
    private val action42: JisonCell = JisonCell.Shift(35)
    private val action43: JisonCell = JisonCell.Shift(36)
    private val action44: JisonCell = JisonCell.Shift(37)
    private val action45: JisonCell = JisonCell.Shift(38)
    private val action46: JisonCell = JisonCell.Shift(50)
    private val action47: JisonCell = JisonCell.Shift(52)
    private val action48: JisonCell = JisonCell.Shift(48)
    private val action49: JisonCell = JisonCell.Shift(53)
    private val action50: JisonCell = JisonCell.Shift(49)
    private val action51: JisonCell = JisonCell.Shift(56)
    private val action52: JisonCell = JisonCell.Goto(47)
    private val action53: JisonCell = JisonCell.Shift(51)
    private val action54: JisonCell = JisonCell.Shift(57)
    private val action55: JisonCell = JisonCell.Shift(58)
    private val action56: JisonCell = JisonCell.Shift(39)
    private val action57: JisonCell = JisonCell.Shift(40)
    private val action58: JisonCell = JisonCell.Shift(41)
    private val action59: JisonCell = JisonCell.Shift(42)
    private val action60: JisonCell = JisonCell.Shift(43)
    private val action61: JisonCell = JisonCell.Reduce(10)
    private val action62: JisonCell = JisonCell.Reduce(11)
    private val action63: JisonCell = JisonCell.Reduce(12)
    private val action64: JisonCell = JisonCell.Reduce(13)
    private val action65: JisonCell = JisonCell.Shift(63)
    private val action66: JisonCell = JisonCell.Shift(60)
    private val action67: JisonCell = JisonCell.Shift(61)
    private val action68: JisonCell = JisonCell.Shift(64)
    private val action69: JisonCell = JisonCell.Goto(59)
    private val action70: JisonCell = JisonCell.Goto(62)
    private val action71: JisonCell = JisonCell.Reduce(3)
    private val action72: JisonCell = JisonCell.Reduce(4)
    private val action73: JisonCell = JisonCell.Reduce(5)
    private val action74: JisonCell = JisonCell.Reduce(6)
    private val action75: JisonCell = JisonCell.Reduce(7)
    private val action76: JisonCell = JisonCell.Reduce(8)
    private val action77: JisonCell = JisonCell.Reduce(9)
    private val action78: JisonCell = JisonCell.Shift(70)
    private val action79: JisonCell = JisonCell.Shift(68)
    private val action80: JisonCell = JisonCell.Shift(67)
    private val action81: JisonCell = JisonCell.Shift(69)
    private val action82: JisonCell = JisonCell.Goto(65)
    private val action83: JisonCell = JisonCell.Goto(66)
    private val action84: JisonCell = JisonCell.Goto(71)
    private val action85: JisonCell = JisonCell.Shift(72)
    private val action86: JisonCell = JisonCell.Shift(74)
    private val action87: JisonCell = JisonCell.Shift(73)
    private val action88: JisonCell = JisonCell.Goto(75)
    private val action89: JisonCell = JisonCell.Goto(76)
    private val action90: JisonCell = JisonCell.Goto(77)
    private val action91: JisonCell = JisonCell.Goto(78)
    private val action92: JisonCell = JisonCell.Goto(79)
    private val action93: JisonCell = JisonCell.Shift(80)
    private val action94: JisonCell = JisonCell.Goto(81)
    private val action95: JisonCell = JisonCell.Goto(82)
    private val action96: JisonCell = JisonCell.Shift(83)
    private val action97: JisonCell = JisonCell.Reduce(48)
    private val action98: JisonCell = JisonCell.Shift(84)
    private val action99: JisonCell = JisonCell.Shift(85)
    private val action100: JisonCell = JisonCell.Reduce(51)
    private val action101: JisonCell = JisonCell.Reduce(63)
    private val action102: JisonCell = JisonCell.Goto(86)
    private val action103: JisonCell = JisonCell.Goto(87)
    private val action104: JisonCell = JisonCell.Shift(88)
    private val action105: JisonCell = JisonCell.Shift(89)
    private val action106: JisonCell = JisonCell.Shift(90)
    private val action107: JisonCell = JisonCell.Shift(91)
    private val action108: JisonCell = JisonCell.Shift(92)
    private val action109: JisonCell = JisonCell.Shift(101)
    private val action110: JisonCell = JisonCell.Shift(106)
    private val action111: JisonCell = JisonCell.Shift(105)
    private val action112: JisonCell = JisonCell.Shift(96)
    private val action113: JisonCell = JisonCell.Shift(102)
    private val action114: JisonCell = JisonCell.Shift(93)
    private val action115: JisonCell = JisonCell.Shift(94)
    private val action116: JisonCell = JisonCell.Goto(95)
    private val action117: JisonCell = JisonCell.Shift(98)
    private val action118: JisonCell = JisonCell.Shift(104)
    private val action119: JisonCell = JisonCell.Shift(100)
    private val action120: JisonCell = JisonCell.Shift(107)
    private val action121: JisonCell = JisonCell.Shift(103)
    private val action122: JisonCell = JisonCell.Shift(108)
    private val action123: JisonCell = JisonCell.Shift(99)
    private val action124: JisonCell = JisonCell.Goto(97)
    private val action125: JisonCell = JisonCell.Reduce(200)
    private val action126: JisonCell = JisonCell.Reduce(201)
    private val action127: JisonCell = JisonCell.Reduce(202)
    private val action128: JisonCell = JisonCell.Reduce(203)
    private val action129: JisonCell = JisonCell.Reduce(204)
    private val action130: JisonCell = JisonCell.Reduce(64)
    private val action131: JisonCell = JisonCell.Reduce(67)
    private val action132: JisonCell = JisonCell.Shift(109)
    private val action133: JisonCell = JisonCell.Reduce(85)
    private val action134: JisonCell = JisonCell.Shift(110)
    private val action135: JisonCell = JisonCell.Shift(111)
    private val action136: JisonCell = JisonCell.Shift(112)
    private val action137: JisonCell = JisonCell.Shift(113)
    private val action138: JisonCell = JisonCell.Shift(114)
    private val action139: JisonCell = JisonCell.Shift(115)
    private val action140: JisonCell = JisonCell.Shift(116)
    private val action141: JisonCell = JisonCell.Shift(117)
    private val action142: JisonCell = JisonCell.Shift(118)
    private val action143: JisonCell = JisonCell.Shift(119)
    private val action144: JisonCell = JisonCell.Shift(120)
    private val action145: JisonCell = JisonCell.Shift(121)
    private val action146: JisonCell = JisonCell.Goto(122)
    private val action147: JisonCell = JisonCell.Reduce(196)
    private val action148: JisonCell = JisonCell.Reduce(157)
    private val action149: JisonCell = JisonCell.Reduce(158)
    private val action150: JisonCell = JisonCell.Reduce(159)
    private val action151: JisonCell = JisonCell.Reduce(160)
    private val action152: JisonCell = JisonCell.Reduce(161)
    private val action153: JisonCell = JisonCell.Reduce(162)
    private val action154: JisonCell = JisonCell.Reduce(163)
    private val action155: JisonCell = JisonCell.Reduce(164)
    private val action156: JisonCell = JisonCell.Reduce(165)
    private val action157: JisonCell = JisonCell.Reduce(166)
    private val action158: JisonCell = JisonCell.Reduce(167)
    private val action159: JisonCell = JisonCell.Reduce(14)
    private val action160: JisonCell = JisonCell.Reduce(20)
    private val action161: JisonCell = JisonCell.Reduce(21)
    private val action162: JisonCell = JisonCell.Shift(123)
    private val action163: JisonCell = JisonCell.Reduce(23)
    private val action164: JisonCell = JisonCell.Reduce(31)
    private val action165: JisonCell = JisonCell.Goto(124)
    private val action166: JisonCell = JisonCell.Reduce(32)
    private val action167: JisonCell = JisonCell.Goto(125)
    private val action168: JisonCell = JisonCell.Reduce(52)
    private val action169: JisonCell = JisonCell.Reduce(53)
    private val action170: JisonCell = JisonCell.Reduce(54)
    private val action171: JisonCell = JisonCell.Reduce(55)
    private val action172: JisonCell = JisonCell.Reduce(89)
    private val action173: JisonCell = JisonCell.Shift(128)
    private val action174: JisonCell = JisonCell.Goto(126)
    private val action175: JisonCell = JisonCell.Shift(127)
    private val action176: JisonCell = JisonCell.Goto(129)
    private val action177: JisonCell = JisonCell.Goto(130)
    private val action178: JisonCell = JisonCell.Shift(131)
    private val action179: JisonCell = JisonCell.Shift(132)
    private val action180: JisonCell = JisonCell.Shift(134)
    private val action181: JisonCell = JisonCell.Shift(133)
    private val action182: JisonCell = JisonCell.Shift(135)
    private val action183: JisonCell = JisonCell.Shift(136)
    private val action184: JisonCell = JisonCell.Reduce(96)
    private val action185: JisonCell = JisonCell.Reduce(33)
    private val action186: JisonCell = JisonCell.Reduce(34)
    private val action187: JisonCell = JisonCell.Reduce(35)
    private val action188: JisonCell = JisonCell.Reduce(36)
    private val action189: JisonCell = JisonCell.Reduce(37)
    private val action190: JisonCell = JisonCell.Shift(143)
    private val action191: JisonCell = JisonCell.Shift(157)
    private val action192: JisonCell = JisonCell.Shift(158)
    private val action193: JisonCell = JisonCell.Shift(159)
    private val action194: JisonCell = JisonCell.Goto(137)
    private val action195: JisonCell = JisonCell.Shift(162)
    private val action196: JisonCell = JisonCell.Shift(161)
    private val action197: JisonCell = JisonCell.Shift(160)
    private val action198: JisonCell = JisonCell.Shift(145)
    private val action199: JisonCell = JisonCell.Shift(147)
    private val action200: JisonCell = JisonCell.Shift(151)
    private val action201: JisonCell = JisonCell.Shift(139)
    private val action202: JisonCell = JisonCell.Shift(140)
    private val action203: JisonCell = JisonCell.Goto(150)
    private val action204: JisonCell = JisonCell.Shift(152)
    private val action205: JisonCell = JisonCell.Shift(153)
    private val action206: JisonCell = JisonCell.Shift(154)
    private val action207: JisonCell = JisonCell.Shift(155)
    private val action208: JisonCell = JisonCell.Shift(156)
    private val action209: JisonCell = JisonCell.Shift(163)
    private val action210: JisonCell = JisonCell.Shift(164)
    private val action211: JisonCell = JisonCell.Goto(138)
    private val action212: JisonCell = JisonCell.Shift(141)
    private val action213: JisonCell = JisonCell.Shift(142)
    private val action214: JisonCell = JisonCell.Shift(149)
    private val action215: JisonCell = JisonCell.Shift(144)
    private val action216: JisonCell = JisonCell.Shift(148)
    private val action217: JisonCell = JisonCell.Shift(146)
    private val action218: JisonCell = JisonCell.Goto(165)
    private val action219: JisonCell = JisonCell.Goto(166)
    private val action220: JisonCell = JisonCell.Goto(167)
    private val action221: JisonCell = JisonCell.Reduce(49)
    private val action222: JisonCell = JisonCell.Reduce(50)
    private val action223: JisonCell = JisonCell.Reduce(61)
    private val action224: JisonCell = JisonCell.Shift(168)
    private val action225: JisonCell = JisonCell.Reduce(62)
    private val action226: JisonCell = JisonCell.Goto(169)
    private val action227: JisonCell = JisonCell.Shift(170)
    private val action228: JisonCell = JisonCell.Reduce(57)
    private val action229: JisonCell = JisonCell.Goto(171)
    private val action230: JisonCell = JisonCell.Shift(172)
    private val action231: JisonCell = JisonCell.Goto(173)
    private val action232: JisonCell = JisonCell.Shift(174)
    private val action233: JisonCell = JisonCell.Goto(175)
    private val action234: JisonCell = JisonCell.Goto(176)
    private val action235: JisonCell = JisonCell.Reduce(122)
    private val action236: JisonCell = JisonCell.Shift(177)
    private val action237: JisonCell = JisonCell.Shift(178)
    private val action238: JisonCell = JisonCell.Shift(179)
    private val action239: JisonCell = JisonCell.Reduce(130)
    private val action240: JisonCell = JisonCell.Shift(180)
    private val action241: JisonCell = JisonCell.Goto(181)
    private val action242: JisonCell = JisonCell.Reduce(132)
    private val action243: JisonCell = JisonCell.Shift(182)
    private val action244: JisonCell = JisonCell.Reduce(198)
    private val action245: JisonCell = JisonCell.Reduce(185)
    private val action246: JisonCell = JisonCell.Reduce(186)
    private val action247: JisonCell = JisonCell.Reduce(187)
    private val action248: JisonCell = JisonCell.Reduce(188)
    private val action249: JisonCell = JisonCell.Reduce(189)
    private val action250: JisonCell = JisonCell.Reduce(190)
    private val action251: JisonCell = JisonCell.Reduce(191)
    private val action252: JisonCell = JisonCell.Reduce(192)
    private val action253: JisonCell = JisonCell.Reduce(193)
    private val action254: JisonCell = JisonCell.Reduce(194)
    private val action255: JisonCell = JisonCell.Reduce(195)
    private val action256: JisonCell = JisonCell.Goto(183)
    private val action257: JisonCell = JisonCell.Goto(184)
    private val action258: JisonCell = JisonCell.Shift(190)
    private val action259: JisonCell = JisonCell.Shift(186)
    private val action260: JisonCell = JisonCell.Shift(187)
    private val action261: JisonCell = JisonCell.Goto(185)
    private val action262: JisonCell = JisonCell.Shift(191)
    private val action263: JisonCell = JisonCell.Shift(188)
    private val action264: JisonCell = JisonCell.Shift(189)
    private val action265: JisonCell = JisonCell.Goto(192)
    private val action266: JisonCell = JisonCell.Goto(194)
    private val action267: JisonCell = JisonCell.Shift(193)
    private val action268: JisonCell = JisonCell.Goto(195)
    private val action269: JisonCell = JisonCell.Goto(196)
    private val action270: JisonCell = JisonCell.Goto(197)
    private val action271: JisonCell = JisonCell.Shift(198)
    private val action272: JisonCell = JisonCell.Goto(199)
    private val action273: JisonCell = JisonCell.Goto(200)
    private val action274: JisonCell = JisonCell.Shift(201)
    private val action275: JisonCell = JisonCell.Goto(202)
    private val action276: JisonCell = JisonCell.Goto(203)
    private val action277: JisonCell = JisonCell.Goto(204)
    private val action278: JisonCell = JisonCell.Reduce(197)
    private val action279: JisonCell = JisonCell.Reduce(22)
    private val action280: JisonCell = JisonCell.Reduce(30)
    private val action281: JisonCell = JisonCell.Reduce(59)
    private val action282: JisonCell = JisonCell.Goto(206)
    private val action283: JisonCell = JisonCell.Goto(205)
    private val action284: JisonCell = JisonCell.Shift(207)
    private val action285: JisonCell = JisonCell.Reduce(86)
    private val action286: JisonCell = JisonCell.Shift(208)
    private val action287: JisonCell = JisonCell.Goto(209)
    private val action288: JisonCell = JisonCell.Shift(210)
    private val action289: JisonCell = JisonCell.Goto(211)
    private val action290: JisonCell = JisonCell.Reduce(92)
    private val action291: JisonCell = JisonCell.Reduce(94)
    private val action292: JisonCell = JisonCell.Reduce(95)
    private val action293: JisonCell = JisonCell.Reduce(183)
    private val action294: JisonCell = JisonCell.Reduce(184)
    private val action295: JisonCell = JisonCell.Goto(212)
    private val action296: JisonCell = JisonCell.Reduce(97)
    private val action297: JisonCell = JisonCell.Goto(214)
    private val action298: JisonCell = JisonCell.Shift(213)
    private val action299: JisonCell = JisonCell.Goto(215)
    private val action300: JisonCell = JisonCell.Goto(216)
    private val action301: JisonCell = JisonCell.Reduce(116)
    private val action302: JisonCell = JisonCell.Reduce(118)
    private val action303: JisonCell = JisonCell.Reduce(119)
    private val action304: JisonCell = JisonCell.Reduce(172)
    private val action305: JisonCell = JisonCell.Reduce(173)
    private val action306: JisonCell = JisonCell.Reduce(174)
    private val action307: JisonCell = JisonCell.Reduce(175)
    private val action308: JisonCell = JisonCell.Reduce(176)
    private val action309: JisonCell = JisonCell.Reduce(177)
    private val action310: JisonCell = JisonCell.Reduce(178)
    private val action311: JisonCell = JisonCell.Reduce(179)
    private val action312: JisonCell = JisonCell.Reduce(180)
    private val action313: JisonCell = JisonCell.Reduce(181)
    private val action314: JisonCell = JisonCell.Reduce(182)
    private val action315: JisonCell = JisonCell.Reduce(103)
    private val action316: JisonCell = JisonCell.Reduce(104)
    private val action317: JisonCell = JisonCell.Reduce(105)
    private val action318: JisonCell = JisonCell.Reduce(106)
    private val action319: JisonCell = JisonCell.Reduce(107)
    private val action320: JisonCell = JisonCell.Reduce(108)
    private val action321: JisonCell = JisonCell.Reduce(109)
    private val action322: JisonCell = JisonCell.Reduce(110)
    private val action323: JisonCell = JisonCell.Reduce(111)
    private val action324: JisonCell = JisonCell.Reduce(112)
    private val action325: JisonCell = JisonCell.Reduce(113)
    private val action326: JisonCell = JisonCell.Reduce(114)
    private val action327: JisonCell = JisonCell.Reduce(115)
    private val action328: JisonCell = JisonCell.Shift(217)
    private val action329: JisonCell = JisonCell.Shift(218)
    private val action330: JisonCell = JisonCell.Goto(220)
    private val action331: JisonCell = JisonCell.Shift(219)
    private val action332: JisonCell = JisonCell.Goto(221)
    private val action333: JisonCell = JisonCell.Goto(222)
    private val action334: JisonCell = JisonCell.Shift(223)
    private val action335: JisonCell = JisonCell.Reduce(56)
    private val action336: JisonCell = JisonCell.Shift(224)
    private val action337: JisonCell = JisonCell.Shift(225)
    private val action338: JisonCell = JisonCell.Shift(226)
    private val action339: JisonCell = JisonCell.Shift(227)
    private val action340: JisonCell = JisonCell.Reduce(143)
    private val action341: JisonCell = JisonCell.Shift(228)
    private val action342: JisonCell = JisonCell.Shift(229)
    private val action343: JisonCell = JisonCell.Shift(230)
    private val action344: JisonCell = JisonCell.Reduce(124)
    private val action345: JisonCell = JisonCell.Shift(231)
    private val action346: JisonCell = JisonCell.Reduce(126)
    private val action347: JisonCell = JisonCell.Shift(232)
    private val action348: JisonCell = JisonCell.Shift(233)
    private val action349: JisonCell = JisonCell.Reduce(199)
    private val action350: JisonCell = JisonCell.Shift(234)
    private val action351: JisonCell = JisonCell.Shift(235)
    private val action352: JisonCell = JisonCell.Reduce(68)
    private val action353: JisonCell = JisonCell.Shift(236)
    private val action354: JisonCell = JisonCell.Goto(237)
    private val action355: JisonCell = JisonCell.Reduce(99)
    private val action356: JisonCell = JisonCell.Reduce(101)
    private val action357: JisonCell = JisonCell.Reduce(102)
    private val action358: JisonCell = JisonCell.Reduce(168)
    private val action359: JisonCell = JisonCell.Reduce(169)
    private val action360: JisonCell = JisonCell.Reduce(170)
    private val action361: JisonCell = JisonCell.Reduce(171)
    private val action362: JisonCell = JisonCell.Shift(238)
    private val action363: JisonCell = JisonCell.Goto(239)
    private val action364: JisonCell = JisonCell.Shift(240)
    private val action365: JisonCell = JisonCell.Shift(241)
    private val action366: JisonCell = JisonCell.Shift(242)
    private val action367: JisonCell = JisonCell.Shift(243)
    private val action368: JisonCell = JisonCell.Shift(244)
    private val action369: JisonCell = JisonCell.Shift(245)
    private val action370: JisonCell = JisonCell.Shift(246)
    private val action371: JisonCell = JisonCell.Goto(247)
    private val action372: JisonCell = JisonCell.Shift(248)
    private val action373: JisonCell = JisonCell.Shift(249)
    private val action374: JisonCell = JisonCell.Shift(250)
    private val action375: JisonCell = JisonCell.Shift(252)
    private val action376: JisonCell = JisonCell.Shift(251)
    private val action377: JisonCell = JisonCell.Reduce(58)
    private val action378: JisonCell = JisonCell.Reduce(60)
    private val action379: JisonCell = JisonCell.Reduce(88)
    private val action380: JisonCell = JisonCell.Reduce(87)
    private val action381: JisonCell = JisonCell.Shift(253)
    private val action382: JisonCell = JisonCell.Reduce(90)
    private val action383: JisonCell = JisonCell.Reduce(93)
    private val action384: JisonCell = JisonCell.Shift(254)
    private val action385: JisonCell = JisonCell.Goto(255)
    private val action386: JisonCell = JisonCell.Goto(256)
    private val action387: JisonCell = JisonCell.Goto(257)
    private val action388: JisonCell = JisonCell.Reduce(117)
    private val action389: JisonCell = JisonCell.Reduce(42)
    private val action390: JisonCell = JisonCell.Reduce(43)
    private val action391: JisonCell = JisonCell.Goto(258)
    private val action392: JisonCell = JisonCell.Reduce(46)
    private val action393: JisonCell = JisonCell.Goto(259)
    private val action394: JisonCell = JisonCell.Goto(260)
    private val action395: JisonCell = JisonCell.Goto(261)
    private val action396: JisonCell = JisonCell.Shift(269)
    private val action397: JisonCell = JisonCell.Shift(267)
    private val action398: JisonCell = JisonCell.Shift(271)
    private val action399: JisonCell = JisonCell.Goto(262)
    private val action400: JisonCell = JisonCell.Shift(265)
    private val action401: JisonCell = JisonCell.Goto(263)
    private val action402: JisonCell = JisonCell.Goto(264)
    private val action403: JisonCell = JisonCell.Shift(266)
    private val action404: JisonCell = JisonCell.Shift(268)
    private val action405: JisonCell = JisonCell.Shift(270)
    private val action406: JisonCell = JisonCell.Shift(272)
    private val action407: JisonCell = JisonCell.Goto(273)
    private val action408: JisonCell = JisonCell.Shift(274)
    private val action409: JisonCell = JisonCell.Goto(275)
    private val action410: JisonCell = JisonCell.Shift(276)
    private val action411: JisonCell = JisonCell.Shift(277)
    private val action412: JisonCell = JisonCell.Goto(278)
    private val action413: JisonCell = JisonCell.Goto(279)
    private val action414: JisonCell = JisonCell.Reduce(123)
    private val action415: JisonCell = JisonCell.Shift(280)
    private val action416: JisonCell = JisonCell.Shift(281)
    private val action417: JisonCell = JisonCell.Shift(282)
    private val action418: JisonCell = JisonCell.Reduce(131)
    private val action419: JisonCell = JisonCell.Reduce(133)
    private val action420: JisonCell = JisonCell.Shift(283)
    private val action421: JisonCell = JisonCell.Reduce(134)
    private val action422: JisonCell = JisonCell.Reduce(69)
    private val action423: JisonCell = JisonCell.Reduce(100)
    private val action424: JisonCell = JisonCell.Reduce(70)
    private val action425: JisonCell = JisonCell.Shift(284)
    private val action426: JisonCell = JisonCell.Reduce(77)
    private val action427: JisonCell = JisonCell.Reduce(72)
    private val action428: JisonCell = JisonCell.Reduce(73)
    private val action429: JisonCell = JisonCell.Reduce(74)
    private val action430: JisonCell = JisonCell.Shift(285)
    private val action431: JisonCell = JisonCell.Reduce(76)
    private val action432: JisonCell = JisonCell.Reduce(78)
    private val action433: JisonCell = JisonCell.Shift(286)
    private val action434: JisonCell = JisonCell.Reduce(80)
    private val action435: JisonCell = JisonCell.Reduce(81)
    private val action436: JisonCell = JisonCell.Reduce(83)
    private val action437: JisonCell = JisonCell.Reduce(82)
    private val action438: JisonCell = JisonCell.Reduce(84)
    private val action439: JisonCell = JisonCell.Reduce(98)
    private val action440: JisonCell = JisonCell.Reduce(91)
    private val action441: JisonCell = JisonCell.Shift(287)
    private val action442: JisonCell = JisonCell.Shift(288)
    private val action443: JisonCell = JisonCell.Goto(289)
    private val action444: JisonCell = JisonCell.Shift(290)
    private val action445: JisonCell = JisonCell.Reduce(47)
    private val action446: JisonCell = JisonCell.Reduce(66)
    private val action447: JisonCell = JisonCell.Goto(291)
    private val action448: JisonCell = JisonCell.Reduce(136)
    private val action449: JisonCell = JisonCell.Shift(292)
    private val action450: JisonCell = JisonCell.Reduce(145)
    private val action451: JisonCell = JisonCell.Goto(293)
    private val action452: JisonCell = JisonCell.Reduce(147)
    private val action453: JisonCell = JisonCell.Reduce(149)
    private val action454: JisonCell = JisonCell.Reduce(150)
    private val action455: JisonCell = JisonCell.Reduce(151)
    private val action456: JisonCell = JisonCell.Reduce(152)
    private val action457: JisonCell = JisonCell.Reduce(153)
    private val action458: JisonCell = JisonCell.Reduce(154)
    private val action459: JisonCell = JisonCell.Reduce(155)
    private val action460: JisonCell = JisonCell.Reduce(156)
    private val action461: JisonCell = JisonCell.Reduce(137)
    private val action462: JisonCell = JisonCell.Shift(294)
    private val action463: JisonCell = JisonCell.Reduce(138)
    private val action464: JisonCell = JisonCell.Shift(295)
    private val action465: JisonCell = JisonCell.Reduce(144)
    private val action466: JisonCell = JisonCell.Reduce(120)
    private val action467: JisonCell = JisonCell.Reduce(121)
    private val action468: JisonCell = JisonCell.Reduce(125)
    private val action469: JisonCell = JisonCell.Reduce(127)
    private val action470: JisonCell = JisonCell.Shift(296)
    private val action471: JisonCell = JisonCell.Reduce(128)
    private val action472: JisonCell = JisonCell.Shift(297)
    private val action473: JisonCell = JisonCell.Shift(298)
    private val action474: JisonCell = JisonCell.Shift(299)
    private val action475: JisonCell = JisonCell.Shift(300)
    private val action476: JisonCell = JisonCell.Goto(301)
    private val action477: JisonCell = JisonCell.Goto(302)
    private val action478: JisonCell = JisonCell.Reduce(40)
    private val action479: JisonCell = JisonCell.Shift(303)
    private val action480: JisonCell = JisonCell.Goto(304)
    private val action481: JisonCell = JisonCell.Goto(305)
    private val action482: JisonCell = JisonCell.Reduce(65)
    private val action483: JisonCell = JisonCell.Goto(306)
    private val action484: JisonCell = JisonCell.Reduce(148)
    private val action485: JisonCell = JisonCell.Goto(307)
    private val action486: JisonCell = JisonCell.Goto(308)
    private val action487: JisonCell = JisonCell.Shift(309)
    private val action488: JisonCell = JisonCell.Reduce(135)
    private val action489: JisonCell = JisonCell.Reduce(71)
    private val action490: JisonCell = JisonCell.Goto(310)
    private val action491: JisonCell = JisonCell.Reduce(79)
    private val action492: JisonCell = JisonCell.Goto(311)
    private val action493: JisonCell = JisonCell.Goto(312)
    private val action494: JisonCell = JisonCell.Reduce(41)
    private val action495: JisonCell = JisonCell.Reduce(44)
    private val action496: JisonCell = JisonCell.Goto(313)
    private val action497: JisonCell = JisonCell.Reduce(146)
    private val action498: JisonCell = JisonCell.Reduce(141)
    private val action499: JisonCell = JisonCell.Shift(314)
    private val action500: JisonCell = JisonCell.Reduce(142)
    private val action501: JisonCell = JisonCell.Shift(315)
    private val action502: JisonCell = JisonCell.Reduce(129)
    private val action503: JisonCell = JisonCell.Shift(316)
    private val action504: JisonCell = JisonCell.Shift(317)
    private val action505: JisonCell = JisonCell.Goto(318)
    private val action506: JisonCell = JisonCell.Reduce(45)
    private val action507: JisonCell = JisonCell.Goto(319)
    private val action508: JisonCell = JisonCell.Goto(320)
    private val action509: JisonCell = JisonCell.Reduce(75)
    private val action510: JisonCell = JisonCell.Reduce(38)
    private val action511: JisonCell = JisonCell.Shift(321)
    private val action512: JisonCell = JisonCell.Reduce(139)
    private val action513: JisonCell = JisonCell.Reduce(140)
    private val action514: JisonCell = JisonCell.Reduce(39)
    private val symbols0 = intArrayOf(1, 5, 8, 9, 10, 11, 12, 28, 35, 36, 38, 40, 42, 47, 63, 87, 88, 89, 90, 91, 92, 105, 108, 109, 112, 114, 117, 118, 119, 124, 125, 126, 127, 128)
    private val symbols1 = intArrayOf(1, 8, 9, 10, 11, 12, 28, 35, 36, 38, 40, 42, 47, 63, 87, 88, 89, 90, 91, 92, 105, 108, 109, 112, 114, 117, 118, 119, 124, 125, 126, 127, 128)
    private val symbols2 = intArrayOf(1, 8, 9, 10, 11, 12, 28, 33, 35, 36, 38, 40, 42, 47, 63, 87, 88, 89, 90, 91, 92, 105, 108, 109, 112, 114, 117, 118, 119, 124, 125, 126, 127, 128)
    private val symbols3 = intArrayOf(8, 9, 10, 11, 12, 19, 34, 43, 78, 80, 81)
    private val symbols4 = intArrayOf(8, 9, 10, 11, 12, 43, 78, 80, 81)
    private val symbols5 = intArrayOf(8, 9, 10, 11, 12, 43, 49, 78, 80, 81)
    private val symbols6 = intArrayOf(8, 9, 10, 11, 12, 30, 43, 47, 49, 51, 53, 55, 57, 59, 61, 63, 66, 68, 70, 71, 73, 78, 80, 81, 92, 105, 108, 109, 112, 114, 116, 117, 118, 119)
    private val symbols7 = intArrayOf(8, 9, 10, 11, 12, 30, 43, 47, 49, 51, 53, 55, 57, 59, 61, 63, 66, 68, 70, 71, 73, 78, 80, 81, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols8 = intArrayOf(8, 9, 10, 11, 12, 19, 47, 63, 78, 80, 81, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols9 = intArrayOf(47, 63, 65, 76, 77, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols10 = intArrayOf(47, 63, 65, 77, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols11 = intArrayOf(5, 8, 9, 10, 11, 12, 28, 33, 35, 36, 38, 40, 42, 47, 63, 87, 88, 89, 90, 91, 92, 105, 108, 109, 112, 114, 117, 118, 119, 124, 125, 126, 127, 128)
    private val symbols12 = intArrayOf(8, 9, 10, 12, 47, 78, 80, 81)
    private val symbols13 = intArrayOf(8, 9, 10, 11, 12, 19, 43, 78, 80, 81)
    private val symbols14 = intArrayOf(8, 9, 10, 11, 12, 99)
    private val symbols15 = intArrayOf(8, 9, 10, 11, 12, 15, 47, 63, 92, 108, 109, 112, 114, 117, 118, 119, 123)
    private val symbols16 = intArrayOf(8, 9, 10, 11, 12)
    private val symbols17 = intArrayOf(8, 9, 10, 11, 12, 15, 47, 63, 92, 108, 109, 112, 114, 117, 118, 119)
    private val symbols18 = intArrayOf(8, 9, 10, 12, 47, 63, 78, 80, 81, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols19 = intArrayOf(11, 47, 63, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols20 = intArrayOf(80, 119, 122)
    private val symbols21 = intArrayOf(8, 9, 10, 11, 12, 13, 15, 28, 30, 33, 35, 36, 43, 47, 63, 78, 87, 88, 89, 90, 91, 92, 93, 108, 112, 114, 117, 118, 119)
    private val symbols22 = intArrayOf(8, 9, 10, 11, 12, 43, 47, 63, 78, 80, 81, 92, 105, 108, 109, 112, 114, 116, 117, 118, 119)
    private val symbols23 = intArrayOf(32, 52, 54, 56, 58, 60, 65, 67, 69, 70, 72, 74, 119, 120, 121)
    private val symbols24 = intArrayOf(47, 63, 92, 105, 108, 109, 112, 114, 117, 118, 119)
    private val symbols25 = intArrayOf(8, 9, 10, 12)
    private val symbols26 = intArrayOf(8, 9, 10, 12, 109)
    private val symbols27 = intArrayOf(8, 9, 10, 11, 12, 63, 87, 108, 109, 111, 112, 113, 114, 115)
    private val symbols28 = intArrayOf(8, 9, 10, 11, 12, 63, 87, 108, 109, 112, 113, 114, 115)
    private val symbols29 = intArrayOf(8, 9, 10, 12, 47, 63, 92, 105, 108, 109, 112, 114, 116, 117, 118, 119)

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
        addAll(stateChunk15())
        addAll(stateChunk16())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            8 to action2,
            10 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            1 to action6,
        ),
        buildJisonState(symbols0, action7, mapOf(
            5 to action8,
        )),
        mapOf(
            4 to action9,
            8 to action2,
            10 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            4 to action10,
            8 to action2,
            10 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            4 to action11,
            8 to action2,
            10 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            14 to action12,
            15 to action13,
        ),
        mapOf(
            1 to action14,
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        buildJisonState(symbols1, action61),
        buildJisonState(symbols1, action62),
        buildJisonState(symbols1, action63),
        buildJisonState(symbols1, action64),
        mapOf(
            8 to action65,
            9 to action66,
            10 to action67,
            11 to action68,
            16 to action69,
            19 to action70,
        ),
        buildJisonState(symbols2, action71),
        buildJisonState(symbols2, action72),
        buildJisonState(symbols2, action73),
        buildJisonState(symbols2, action74),
        buildJisonState(symbols2, action75),
        buildJisonState(symbols2, action76),
        buildJisonState(symbols2, action77),
    )

    private fun stateChunk1(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action82,
            44 to action83,
            75 to action84,
            78 to action85,
            80 to action86,
            81 to action87,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action88,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action89,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action90,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action91,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action92,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            11 to action93,
            12 to action81,
            22 to action94,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action95,
        ),
        mapOf(
            11 to action96,
        ),
        buildJisonState(symbols2, action97),
        mapOf(
            39 to action98,
        ),
        mapOf(
            41 to action99,
        ),
        buildJisonState(symbols2, action100),
        buildJisonState(symbols3, action101, mapOf(
            11 to action68,
            19 to action102,
            34 to action103,
            43 to action104,
        )),
        mapOf(
            11 to action105,
        ),
        mapOf(
            11 to action106,
        ),
        mapOf(
            11 to action107,
        ),
        mapOf(
            11 to action108,
        ),
        mapOf(
            15 to action109,
            47 to action110,
            63 to action111,
            83 to action112,
            92 to action113,
            98 to action114,
            100 to action115,
            104 to action116,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action124,
        ),
        buildJisonState(symbols2, action125),
    )

    private fun stateChunk2(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols2, action126),
        buildJisonState(symbols2, action127),
        buildJisonState(symbols2, action128),
        buildJisonState(symbols2, action129),
        buildJisonState(symbols4, action130),
        buildJisonState(symbols5, action131, mapOf(
            49 to action132,
        )),
        buildJisonState(symbols6, action133, mapOf(
            30 to action134,
            47 to action37,
            51 to action135,
            53 to action136,
            55 to action137,
            57 to action138,
            59 to action139,
            61 to action140,
            63 to action40,
            66 to action141,
            68 to action142,
            70 to action143,
            71 to action144,
            73 to action145,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        )),
        buildJisonState(symbols7, action147),
        buildJisonState(symbols7, action148),
        buildJisonState(symbols7, action149),
        buildJisonState(symbols7, action150),
        buildJisonState(symbols7, action151),
        buildJisonState(symbols7, action152),
        buildJisonState(symbols7, action153),
        buildJisonState(symbols7, action154),
        buildJisonState(symbols7, action155),
        buildJisonState(symbols7, action156),
        buildJisonState(symbols7, action157),
        buildJisonState(symbols7, action158),
        buildJisonState(symbols1, action159),
    )

    private fun stateChunk3(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols1, action160),
        buildJisonState(symbols1, action161),
        mapOf(
            10 to action162,
        ),
        buildJisonState(symbols1, action163),
        buildJisonState(symbols8, action164, mapOf(
            11 to action68,
            19 to action165,
        )),
        buildJisonState(symbols2, action166),
        mapOf(
            45 to action167,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        buildJisonState(symbols2, action168),
        buildJisonState(symbols2, action169),
        buildJisonState(symbols2, action170),
        buildJisonState(symbols2, action171),
        buildJisonState(symbols9, action172, mapOf(
            65 to action173,
            76 to action174,
            77 to action175,
        )),
        mapOf(
            79 to action176,
            82 to action177,
            83 to action178,
            84 to action179,
            119 to action180,
            122 to action181,
        ),
        mapOf(
            78 to action182,
            80 to action183,
        ),
        buildJisonState(symbols10, action184),
        buildJisonState(symbols2, action185),
        buildJisonState(symbols2, action186),
        buildJisonState(symbols2, action187),
        buildJisonState(symbols2, action188),
        buildJisonState(symbols2, action189),
    )

    private fun stateChunk4(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            11 to action190,
            13 to action191,
            15 to action192,
            28 to action193,
            29 to action194,
            33 to action195,
            35 to action196,
            36 to action197,
            47 to action198,
            63 to action199,
            78 to action200,
            83 to action201,
            84 to action202,
            86 to action203,
            87 to action204,
            88 to action205,
            89 to action206,
            90 to action207,
            91 to action208,
            92 to action209,
            93 to action210,
            94 to action211,
            108 to action212,
            112 to action213,
            114 to action214,
            117 to action215,
            118 to action216,
            119 to action217,
        ),
        buildJisonState(symbols11, action7, mapOf(
            5 to action218,
        )),
        buildJisonState(symbols11, action7, mapOf(
            5 to action219,
        )),
        mapOf(
            11 to action190,
            13 to action191,
            15 to action192,
            28 to action193,
            29 to action220,
            33 to action195,
            35 to action196,
            36 to action197,
            47 to action198,
            63 to action199,
            78 to action200,
            83 to action201,
            84 to action202,
            86 to action203,
            87 to action204,
            88 to action205,
            89 to action206,
            90 to action207,
            91 to action208,
            92 to action209,
            93 to action210,
            94 to action211,
            108 to action212,
            112 to action213,
            114 to action214,
            117 to action215,
            118 to action216,
            119 to action217,
        ),
        buildJisonState(symbols2, action221),
        buildJisonState(symbols2, action222),
        buildJisonState(symbols12, action223, mapOf(
            47 to action224,
        )),
        buildJisonState(symbols13, action225, mapOf(
            11 to action68,
            19 to action226,
            43 to action227,
        )),
        buildJisonState(symbols4, action228),
        mapOf(
            47 to action37,
            50 to action229,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            105 to action230,
            106 to action231,
            108 to action232,
        ),
        mapOf(
            47 to action37,
            50 to action233,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            47 to action37,
            50 to action234,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        buildJisonState(symbols14, action235, mapOf(
            11 to action236,
            99 to action237,
        )),
        mapOf(
            83 to action238,
        ),
        buildJisonState(symbols15, action239, mapOf(
            11 to action240,
            15 to action109,
            47 to action110,
            63 to action111,
            92 to action113,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action241,
        )),
        buildJisonState(symbols16, action242, mapOf(
            11 to action243,
        )),
        buildJisonState(symbols17, action244),
        buildJisonState(symbols17, action245),
        buildJisonState(symbols17, action246),
    )

    private fun stateChunk5(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols17, action247),
        buildJisonState(symbols17, action248),
        buildJisonState(symbols17, action249),
        buildJisonState(symbols17, action250),
        buildJisonState(symbols17, action251),
        buildJisonState(symbols17, action252),
        buildJisonState(symbols17, action253),
        buildJisonState(symbols17, action254),
        buildJisonState(symbols17, action255),
        mapOf(
            47 to action37,
            50 to action256,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            31 to action257,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action265,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action266,
            53 to action267,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action268,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action269,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action270,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            112 to action271,
        ),
        mapOf(
            31 to action272,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action273,
            68 to action274,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action275,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
    )

    private fun stateChunk6(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            31 to action276,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action277,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols7, action278),
        buildJisonState(symbols1, action279),
        buildJisonState(symbols18, action280),
        buildJisonState(symbols3, action281, mapOf(
            11 to action68,
            19 to action282,
            34 to action283,
            43 to action104,
        )),
        buildJisonState(symbols19, action285, mapOf(
            11 to action284,
        )),
        mapOf(
            11 to action286,
        ),
        mapOf(
            31 to action287,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            80 to action288,
            82 to action289,
            119 to action180,
            122 to action181,
        ),
        buildJisonState(symbols20, action290),
        buildJisonState(symbols20, action291),
        buildJisonState(symbols20, action292),
        buildJisonState(symbols20, action293),
        buildJisonState(symbols20, action294),
        mapOf(
            79 to action295,
            82 to action177,
            83 to action178,
            84 to action179,
            119 to action180,
            122 to action181,
        ),
        buildJisonState(symbols10, action296),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            11 to action190,
            12 to action81,
            13 to action191,
            15 to action192,
            22 to action297,
            28 to action193,
            30 to action298,
            33 to action195,
            34 to action299,
            35 to action196,
            36 to action197,
            43 to action104,
            47 to action198,
            63 to action199,
            78 to action200,
            86 to action203,
            87 to action204,
            88 to action205,
            89 to action206,
            90 to action207,
            91 to action208,
            92 to action209,
            93 to action210,
            94 to action300,
            108 to action212,
            112 to action213,
            114 to action214,
            117 to action215,
            118 to action216,
            119 to action217,
        ),
        buildJisonState(symbols21, action301),
        buildJisonState(symbols21, action302),
    )

    private fun stateChunk7(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols21, action303),
        buildJisonState(symbols21, action304),
        buildJisonState(symbols21, action305),
        buildJisonState(symbols21, action306),
        buildJisonState(symbols21, action307),
        buildJisonState(symbols21, action308),
        buildJisonState(symbols21, action309),
        buildJisonState(symbols21, action310),
        buildJisonState(symbols21, action311),
        buildJisonState(symbols21, action312),
        buildJisonState(symbols21, action313),
        buildJisonState(symbols21, action314),
        buildJisonState(symbols21, action315),
        buildJisonState(symbols21, action316),
        buildJisonState(symbols21, action317),
        buildJisonState(symbols21, action318),
        buildJisonState(symbols21, action319),
        buildJisonState(symbols21, action320),
        buildJisonState(symbols21, action321),
        buildJisonState(symbols21, action322),
    )

    private fun stateChunk8(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols21, action323),
        buildJisonState(symbols21, action324),
        buildJisonState(symbols21, action325),
        buildJisonState(symbols21, action326),
        buildJisonState(symbols21, action327),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action328,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action329,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            11 to action190,
            12 to action81,
            13 to action191,
            15 to action192,
            22 to action330,
            28 to action193,
            30 to action331,
            33 to action195,
            34 to action332,
            35 to action196,
            36 to action197,
            43 to action104,
            47 to action198,
            63 to action199,
            78 to action200,
            86 to action203,
            87 to action204,
            88 to action205,
            89 to action206,
            90 to action207,
            91 to action208,
            92 to action209,
            93 to action210,
            94 to action300,
            108 to action212,
            112 to action213,
            114 to action214,
            117 to action215,
            118 to action216,
            119 to action217,
        ),
        mapOf(
            11 to action68,
            19 to action333,
        ),
        mapOf(
            47 to action334,
        ),
        buildJisonState(symbols4, action335),
        mapOf(
            11 to action336,
            47 to action37,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            11 to action337,
        ),
        mapOf(
            11 to action338,
            109 to action339,
        ),
        mapOf(
            11 to action340,
            109 to action340,
        ),
        mapOf(
            11 to action341,
            47 to action37,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            11 to action342,
            47 to action37,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            83 to action343,
        ),
        buildJisonState(symbols16, action344, mapOf(
            11 to action345,
        )),
        buildJisonState(symbols16, action346, mapOf(
            11 to action347,
        )),
    )

    private fun stateChunk9(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            83 to action348,
        ),
        buildJisonState(symbols17, action349),
        mapOf(
            83 to action350,
            101 to action351,
        ),
        buildJisonState(symbols22, action352, mapOf(
            47 to action37,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        )),
        mapOf(
            32 to action353,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols23, action355),
        buildJisonState(symbols23, action356),
        buildJisonState(symbols23, action357),
        buildJisonState(symbols23, action358),
        buildJisonState(symbols23, action359),
        buildJisonState(symbols23, action360),
        buildJisonState(symbols23, action361),
        mapOf(
            52 to action362,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action363,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            54 to action364,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            56 to action365,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            58 to action366,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            60 to action367,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            63 to action368,
        ),
        mapOf(
            67 to action369,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
    )

    private fun stateChunk10(): Array<Map<Int, JisonCell>> = arrayOf(
        mapOf(
            69 to action370,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            31 to action371,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            32 to action372,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            70 to action258,
            72 to action373,
            74 to action374,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            70 to action258,
            72 to action375,
            74 to action376,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols13, action377, mapOf(
            11 to action68,
            19 to action226,
            43 to action227,
        )),
        buildJisonState(symbols12, action378, mapOf(
            47 to action224,
        )),
        buildJisonState(symbols24, action379),
        buildJisonState(symbols24, action380),
        mapOf(
            65 to action381,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols24, action382),
        buildJisonState(symbols20, action383),
        mapOf(
            80 to action384,
            82 to action289,
            119 to action180,
            122 to action181,
        ),
        mapOf(
            31 to action385,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols11, action7, mapOf(
            5 to action386,
        )),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action387,
            43 to action227,
        ),
        buildJisonState(symbols21, action388),
        buildJisonState(symbols2, action389),
        buildJisonState(symbols2, action390),
        mapOf(
            31 to action391,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
    )

    private fun stateChunk11(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols2, action392),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action393,
            43 to action227,
        ),
        mapOf(
            46 to action394,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        mapOf(
            11 to action68,
            19 to action395,
        ),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action399,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action407,
            107 to action408,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action409,
            107 to action410,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        mapOf(
            108 to action411,
        ),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action412,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        mapOf(
            47 to action37,
            50 to action413,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        buildJisonState(symbols25, action414),
        mapOf(
            83 to action415,
        ),
        mapOf(
            83 to action416,
            101 to action417,
        ),
        buildJisonState(symbols25, action418),
        buildJisonState(symbols16, action419, mapOf(
            11 to action420,
        )),
        buildJisonState(symbols25, action421),
        buildJisonState(symbols5, action422),
        buildJisonState(symbols23, action423),
        buildJisonState(symbols5, action424),
        mapOf(
            54 to action425,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
    )

    private fun stateChunk12(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols5, action426),
        buildJisonState(symbols5, action427),
        buildJisonState(symbols5, action428),
        buildJisonState(symbols5, action429),
        mapOf(
            112 to action430,
        ),
        buildJisonState(symbols5, action431),
        buildJisonState(symbols5, action432),
        mapOf(
            69 to action433,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols5, action434),
        buildJisonState(symbols5, action435),
        buildJisonState(symbols5, action436),
        buildJisonState(symbols5, action437),
        buildJisonState(symbols5, action438),
        buildJisonState(symbols19, action439),
        buildJisonState(symbols24, action440),
        mapOf(
            32 to action441,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action442,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        buildJisonState(symbols11, action7, mapOf(
            5 to action443,
        )),
        mapOf(
            32 to action444,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        buildJisonState(symbols2, action445),
    )

    private fun stateChunk13(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols4, action446),
        mapOf(
            46 to action447,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
        ),
        buildJisonState(symbols26, action448, mapOf(
            109 to action449,
        )),
        buildJisonState(symbols27, action450, mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            108 to action400,
            111 to action451,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        )),
        buildJisonState(symbols28, action452),
        buildJisonState(symbols28, action453),
        buildJisonState(symbols28, action454),
        buildJisonState(symbols28, action455),
        buildJisonState(symbols28, action456),
        buildJisonState(symbols28, action457),
        buildJisonState(symbols28, action458),
        buildJisonState(symbols28, action459),
        buildJisonState(symbols28, action460),
        buildJisonState(symbols26, action461, mapOf(
            109 to action449,
        )),
        mapOf(
            11 to action462,
        ),
        buildJisonState(symbols26, action463, mapOf(
            109 to action449,
        )),
        mapOf(
            11 to action464,
        ),
        mapOf(
            11 to action465,
            109 to action465,
        ),
        buildJisonState(symbols26, action466, mapOf(
            109 to action449,
        )),
        buildJisonState(symbols29, action467, mapOf(
            47 to action37,
            63 to action40,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action146,
            117 to action53,
            118 to action54,
            119 to action55,
        )),
    )

    private fun stateChunk14(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols25, action468),
        buildJisonState(symbols16, action469, mapOf(
            11 to action470,
        )),
        buildJisonState(symbols25, action471),
        mapOf(
            101 to action472,
        ),
        mapOf(
            54 to action473,
        ),
        mapOf(
            65 to action474,
        ),
        mapOf(
            69 to action475,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action476,
            34 to action477,
            43 to action104,
        ),
        buildJisonState(symbols2, action478),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action479,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action480,
            34 to action481,
            43 to action104,
        ),
        buildJisonState(symbols4, action482),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            108 to action400,
            110 to action483,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        buildJisonState(symbols28, action484),
        mapOf(
            15 to action109,
            47 to action110,
            63 to action111,
            92 to action113,
            104 to action485,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action124,
        ),
        mapOf(
            15 to action109,
            47 to action110,
            63 to action111,
            92 to action113,
            104 to action486,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action124,
        ),
        mapOf(
            101 to action487,
        ),
        buildJisonState(symbols25, action488),
        buildJisonState(symbols5, action489),
        mapOf(
            31 to action490,
            70 to action258,
            83 to action259,
            84 to action260,
            85 to action261,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
    )

    private fun stateChunk15(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols5, action491),
        buildJisonState(symbols11, action7, mapOf(
            5 to action492,
        )),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action493,
            43 to action227,
        ),
        buildJisonState(symbols2, action494),
        buildJisonState(symbols2, action495),
        mapOf(
            8 to action78,
            9 to action79,
            10 to action80,
            12 to action81,
            22 to action496,
            43 to action227,
        ),
        buildJisonState(symbols27, action497, mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            108 to action400,
            111 to action451,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        )),
        buildJisonState(symbols15, action498, mapOf(
            11 to action499,
            15 to action109,
            47 to action110,
            63 to action111,
            92 to action113,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action241,
        )),
        buildJisonState(symbols15, action500, mapOf(
            11 to action501,
            15 to action109,
            47 to action110,
            63 to action111,
            92 to action113,
            108 to action117,
            109 to action118,
            112 to action119,
            114 to action120,
            117 to action121,
            118 to action122,
            119 to action123,
            123 to action241,
        )),
        buildJisonState(symbols25, action502),
        mapOf(
            32 to action503,
            70 to action258,
            85 to action354,
            119 to action262,
            120 to action263,
            121 to action264,
        ),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action504,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        buildJisonState(symbols11, action7, mapOf(
            5 to action505,
        )),
        buildJisonState(symbols2, action506),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action507,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        mapOf(
            11 to action396,
            63 to action397,
            87 to action398,
            95 to action508,
            108 to action400,
            110 to action401,
            111 to action402,
            112 to action403,
            113 to action404,
            114 to action405,
            115 to action406,
        ),
        buildJisonState(symbols5, action509),
        buildJisonState(symbols2, action510),
        mapOf(
            6 to action15,
            7 to action16,
            8 to action17,
            9 to action18,
            10 to action19,
            11 to action20,
            12 to action21,
            21 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            33 to action511,
            35 to action29,
            36 to action30,
            37 to action31,
            38 to action32,
            40 to action33,
            42 to action34,
            45 to action35,
            46 to action36,
            47 to action37,
            48 to action38,
            50 to action39,
            63 to action40,
            87 to action41,
            88 to action42,
            89 to action43,
            90 to action44,
            91 to action45,
            92 to action46,
            105 to action47,
            108 to action48,
            109 to action49,
            112 to action50,
            114 to action51,
            116 to action52,
            117 to action53,
            118 to action54,
            119 to action55,
            124 to action56,
            125 to action57,
            126 to action58,
            127 to action59,
            128 to action60,
        ),
        buildJisonState(symbols26, action512, mapOf(
            109 to action449,
        )),
    )

    private fun stateChunk16(): Array<Map<Int, JisonCell>> = arrayOf(
        buildJisonState(symbols26, action513, mapOf(
            109 to action449,
        )),
        buildJisonState(symbols2, action514),
    )

    val lexerPatterns: List<AgentflowJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
        addAll(lexerChunk5())
    }

    private fun lexerChunk0(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(0, "^(?:accTitle\\s*:\\s*)"),
        AgentflowJisonLexerPattern(1, "^(?:(?!\\n||)*[^\\n]*)"),
        AgentflowJisonLexerPattern(2, "^(?:accDescr\\s*:\\s*)"),
        AgentflowJisonLexerPattern(3, "^(?:(?!\\n||)*[^\\n]*)"),
        AgentflowJisonLexerPattern(4, "^(?:accDescr\\s*\\{\\s*)"),
        AgentflowJisonLexerPattern(5, "^(?:[\\}])"),
        AgentflowJisonLexerPattern(6, "^(?:[^\\}]*)"),
        AgentflowJisonLexerPattern(7, "^(?:@\\{)"),
        AgentflowJisonLexerPattern(8, "^(?:[\"])"),
        AgentflowJisonLexerPattern(9, "^(?:[\"])"),
        AgentflowJisonLexerPattern(10, "^(?:[^\\\"]+)"),
        AgentflowJisonLexerPattern(11, "^(?:[^}^\"]+)"),
        AgentflowJisonLexerPattern(12, "^(?:\\})"),
        AgentflowJisonLexerPattern(13, "^(?:call[\\s]+)"),
        AgentflowJisonLexerPattern(14, "^(?:\\([\\s]*\\))"),
        AgentflowJisonLexerPattern(15, "^(?:\\()"),
        AgentflowJisonLexerPattern(16, "^(?:[^(]*)"),
        AgentflowJisonLexerPattern(17, "^(?:\\))"),
        AgentflowJisonLexerPattern(18, "^(?:[^)]*)"),
        AgentflowJisonLexerPattern(19, "^(?:[^`\"]+)"),
    )

    private fun lexerChunk1(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(20, "^(?:[`][\"])"),
        AgentflowJisonLexerPattern(21, "^(?:[\"][`])"),
        AgentflowJisonLexerPattern(22, "^(?:[^\"]+)"),
        AgentflowJisonLexerPattern(23, "^(?:[\"])"),
        AgentflowJisonLexerPattern(24, "^(?:[\"])"),
        AgentflowJisonLexerPattern(25, "^(?:style\\b)"),
        AgentflowJisonLexerPattern(26, "^(?:default\\b)"),
        AgentflowJisonLexerPattern(27, "^(?:linkStyle\\b)"),
        AgentflowJisonLexerPattern(28, "^(?:interpolate\\b)"),
        AgentflowJisonLexerPattern(29, "^(?:classDef\\b)"),
        AgentflowJisonLexerPattern(30, "^(?:class\\b)"),
        AgentflowJisonLexerPattern(31, "^(?:href[\\s])"),
        AgentflowJisonLexerPattern(32, "^(?:click[\\s]+)"),
        AgentflowJisonLexerPattern(33, "^(?:[\\s\\n])"),
        AgentflowJisonLexerPattern(34, "^(?:[^\\s\\n]*)"),
        AgentflowJisonLexerPattern(35, "^(?:agentflow-beta\\b)"),
        AgentflowJisonLexerPattern(36, "^(?:flow\\b)"),
        AgentflowJisonLexerPattern(37, "^(?:connector\\b)"),
        AgentflowJisonLexerPattern(38, "^(?:global\\b[ \\t]*)"),
        AgentflowJisonLexerPattern(39, "^(?:end\\b[ \\t]*)"),
    )

    private fun lexerChunk2(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(40, "^(?:_self\\b)"),
        AgentflowJisonLexerPattern(41, "^(?:_blank\\b)"),
        AgentflowJisonLexerPattern(42, "^(?:_parent\\b)"),
        AgentflowJisonLexerPattern(43, "^(?:_top\\b)"),
        AgentflowJisonLexerPattern(44, "^(?:(\\r?\\n)*\\s*\\n)"),
        AgentflowJisonLexerPattern(45, "^(?:\\s*LR\\b)"),
        AgentflowJisonLexerPattern(46, "^(?:\\s*RL\\b)"),
        AgentflowJisonLexerPattern(47, "^(?:\\s*TB\\b)"),
        AgentflowJisonLexerPattern(48, "^(?:\\s*BT\\b)"),
        AgentflowJisonLexerPattern(49, "^(?:\\s*TD\\b)"),
        AgentflowJisonLexerPattern(50, "^(?:\\s*BR\\b)"),
        AgentflowJisonLexerPattern(51, "^(?:\\s*<)"),
        AgentflowJisonLexerPattern(52, "^(?:\\s*>)"),
        AgentflowJisonLexerPattern(53, "^(?:\\s*\\^)"),
        AgentflowJisonLexerPattern(54, "^(?:\\s*v\\b)"),
        AgentflowJisonLexerPattern(55, "^(?:.*direction\\s+TB[^\\n]*)"),
        AgentflowJisonLexerPattern(56, "^(?:.*direction\\s+BT[^\\n]*)"),
        AgentflowJisonLexerPattern(57, "^(?:.*direction\\s+RL[^\\n]*)"),
        AgentflowJisonLexerPattern(58, "^(?:.*direction\\s+LR[^\\n]*)"),
        AgentflowJisonLexerPattern(59, "^(?:.*direction\\s+TD[^\\n]*)"),
    )

    private fun lexerChunk3(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(60, "^(?:[^\\s\\\"]+@(?=[^\\{\\\"]))"),
        AgentflowJisonLexerPattern(61, "^(?:[0-9]+)"),
        AgentflowJisonLexerPattern(62, "^(?:#)"),
        AgentflowJisonLexerPattern(63, "^(?::::)"),
        AgentflowJisonLexerPattern(64, "^(?::)"),
        AgentflowJisonLexerPattern(65, "^(?:&)"),
        AgentflowJisonLexerPattern(66, "^(?:;)"),
        AgentflowJisonLexerPattern(67, "^(?:,)"),
        AgentflowJisonLexerPattern(68, "^(?:\\*)"),
        AgentflowJisonLexerPattern(69, "^(?:\\s*--+[x>]\\s*)"),
        AgentflowJisonLexerPattern(70, "^(?:\\s*--\\s*)"),
        AgentflowJisonLexerPattern(71, "^(?:[^-]|-(?!-)+)"),
        AgentflowJisonLexerPattern(72, "^(?:\\s*-\\.+-\\s*)"),
        AgentflowJisonLexerPattern(73, "^(?:%%(?!\\{)[^\\n]*)"),
        AgentflowJisonLexerPattern(74, "^(?:[-/\\)][\\)])"),
        AgentflowJisonLexerPattern(75, "^(?:[^\\(\\)\\[\\]\\{\\}]|!\\)+)"),
        AgentflowJisonLexerPattern(76, "^(?:\\(-)"),
        AgentflowJisonLexerPattern(77, "^(?:\\]\\))"),
        AgentflowJisonLexerPattern(78, "^(?:\\(\\[)"),
        AgentflowJisonLexerPattern(79, "^(?:\\]\\])"),
    )

    private fun lexerChunk4(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(80, "^(?:\\[\\[)"),
        AgentflowJisonLexerPattern(81, "^(?:\\[\\|)"),
        AgentflowJisonLexerPattern(82, "^(?:>)"),
        AgentflowJisonLexerPattern(83, "^(?:\\)\\])"),
        AgentflowJisonLexerPattern(84, "^(?:\\[\\()"),
        AgentflowJisonLexerPattern(85, "^(?:\\)\\)\\))"),
        AgentflowJisonLexerPattern(86, "^(?:\\(\\(\\()"),
        AgentflowJisonLexerPattern(87, "^(?:%%(?!\\{)[^\\n]*)"),
        AgentflowJisonLexerPattern(88, "^(?:[\\\\(?=\\])][\\]])"),
        AgentflowJisonLexerPattern(89, "^(?:\\/(?=\\])\\])"),
        AgentflowJisonLexerPattern(90, "^(?:\\/(?!\\])|\\\\(?!\\])|(?:[^\\\\\\[\\]\\(\\)\\{\\}\\/%]+|(?!))+)"),
        AgentflowJisonLexerPattern(91, "^(?:\\[\\/)"),
        AgentflowJisonLexerPattern(92, "^(?:\\[\\\\)"),
        AgentflowJisonLexerPattern(93, "^(?:<)"),
        AgentflowJisonLexerPattern(94, "^(?:>)"),
        AgentflowJisonLexerPattern(95, "^(?:\\^)"),
        AgentflowJisonLexerPattern(96, "^(?:\\\\\\|)"),
        AgentflowJisonLexerPattern(97, "^(?:v\\b)"),
        AgentflowJisonLexerPattern(98, "^(?:\\*)"),
        AgentflowJisonLexerPattern(99, "^(?:#)"),
    )

    private fun lexerChunk5(): List<AgentflowJisonLexerPattern> = listOf(
        AgentflowJisonLexerPattern(100, "^(?:&)"),
        AgentflowJisonLexerPattern(101, "^(?:[ \\t]*%%(?!\\{)[^\\n]*)"),
        AgentflowJisonLexerPattern(102, "^(?:([A-Za-z0-9!\"\\#\$%&'*+\\.`?\\\\_\\/]|-(?=[^\\>\\-\\.])|(?!))+)"),
        AgentflowJisonLexerPattern(103, "^(?:-)"),
        AgentflowJisonLexerPattern(104, "^(?:[\\u00AA\\u00B5\\u00BA\\u00C0-\\u00D6\\u00D8-\\u00F6]|[\\u00F8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0370-\\u0374\\u0376\\u0377]|[\\u037A-\\u037D\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5]|[\\u03F7-\\u0481\\u048A-\\u0527\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05D0-\\u05EA]|[\\u05F0-\\u05F2\\u0620-\\u064A\\u066E\\u066F\\u0671-\\u06D3\\u06D5\\u06E5\\u06E6\\u06EE]|[\\u06EF\\u06FA-\\u06FC\\u06FF\\u0710\\u0712-\\u072F\\u074D-\\u07A5\\u07B1\\u07CA-\\u07EA]|[\\u07F4\\u07F5\\u07FA\\u0800-\\u0815\\u081A\\u0824\\u0828\\u0840-\\u0858\\u08A0]|[\\u08A2-\\u08AC\\u0904-\\u0939\\u093D\\u0950\\u0958-\\u0961\\u0971-\\u0977]|[\\u0979-\\u097F\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2]|[\\u09B6-\\u09B9\\u09BD\\u09CE\\u09DC\\u09DD\\u09DF-\\u09E1\\u09F0\\u09F1\\u0A05-\\u0A0A]|[\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39]|[\\u0A59-\\u0A5C\\u0A5E\\u0A72-\\u0A74\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8]|[\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABD\\u0AD0\\u0AE0\\u0AE1\\u0B05-\\u0B0C]|[\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3D\\u0B5C]|[\\u0B5D\\u0B5F-\\u0B61\\u0B71\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99]|[\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BD0]|[\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C33\\u0C35-\\u0C39\\u0C3D]|[\\u0C58\\u0C59\\u0C60\\u0C61\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3]|[\\u0CB5-\\u0CB9\\u0CBD\\u0CDE\\u0CE0\\u0CE1\\u0CF1\\u0CF2\\u0D05-\\u0D0C\\u0D0E-\\u0D10]|[\\u0D12-\\u0D3A\\u0D3D\\u0D4E\\u0D60\\u0D61\\u0D7A-\\u0D7F\\u0D85-\\u0D96\\u0D9A-\\u0DB1]|[\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0E01-\\u0E30\\u0E32\\u0E33\\u0E40-\\u0E46\\u0E81]|[\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3]|[\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB0\\u0EB2\\u0EB3\\u0EBD\\u0EC0-\\u0EC4\\u0EC6]|[\\u0EDC-\\u0EDF\\u0F00\\u0F40-\\u0F47\\u0F49-\\u0F6C\\u0F88-\\u0F8C\\u1000-\\u102A]|[\\u103F\\u1050-\\u1055\\u105A-\\u105D\\u1061\\u1065\\u1066\\u106E-\\u1070\\u1075-\\u1081]|[\\u108E\\u10A0-\\u10C5\\u10C7\\u10CD\\u10D0-\\u10FA\\u10FC-\\u1248\\u124A-\\u124D]|[\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0]|[\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310]|[\\u1312-\\u1315\\u1318-\\u135A\\u1380-\\u138F\\u13A0-\\u13F4\\u1401-\\u166C]|[\\u166F-\\u167F\\u1681-\\u169A\\u16A0-\\u16EA\\u1700-\\u170C\\u170E-\\u1711]|[\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176C\\u176E-\\u1770\\u1780-\\u17B3\\u17D7]|[\\u17DC\\u1820-\\u1877\\u1880-\\u18A8\\u18AA\\u18B0-\\u18F5\\u1900-\\u191C]|[\\u1950-\\u196D\\u1970-\\u1974\\u1980-\\u19AB\\u19C1-\\u19C7\\u1A00-\\u1A16]|[\\u1A20-\\u1A54\\u1AA7\\u1B05-\\u1B33\\u1B45-\\u1B4B\\u1B83-\\u1BA0\\u1BAE\\u1BAF]|[\\u1BBA-\\u1BE5\\u1C00-\\u1C23\\u1C4D-\\u1C4F\\u1C5A-\\u1C7D\\u1CE9-\\u1CEC]|[\\u1CEE-\\u1CF1\\u1CF5\\u1CF6\\u1D00-\\u1DBF\\u1E00-\\u1F15\\u1F18-\\u1F1D]|[\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D]|[\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3]|[\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u2071\\u207F]|[\\u2090-\\u209C\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128]|[\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2183\\u2184]|[\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2CE4\\u2CEB-\\u2CEE\\u2CF2\\u2CF3]|[\\u2D00-\\u2D25\\u2D27\\u2D2D\\u2D30-\\u2D67\\u2D6F\\u2D80-\\u2D96\\u2DA0-\\u2DA6]|[\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE]|[\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2E2F\\u3005\\u3006\\u3031-\\u3035\\u303B\\u303C]|[\\u3041-\\u3096\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312D]|[\\u3131-\\u318E\\u31A0-\\u31BA\\u31F0-\\u31FF\\u3400-\\u4DB5\\u4E00-\\u9FCC]|[\\uA000-\\uA48C\\uA4D0-\\uA4FD\\uA500-\\uA60C\\uA610-\\uA61F\\uA62A\\uA62B]|[\\uA640-\\uA66E\\uA67F-\\uA697\\uA6A0-\\uA6E5\\uA717-\\uA71F\\uA722-\\uA788]|[\\uA78B-\\uA78E\\uA790-\\uA793\\uA7A0-\\uA7AA\\uA7F8-\\uA801\\uA803-\\uA805]|[\\uA807-\\uA80A\\uA80C-\\uA822\\uA840-\\uA873\\uA882-\\uA8B3\\uA8F2-\\uA8F7\\uA8FB]|[\\uA90A-\\uA925\\uA930-\\uA946\\uA960-\\uA97C\\uA984-\\uA9B2\\uA9CF\\uAA00-\\uAA28]|[\\uAA40-\\uAA42\\uAA44-\\uAA4B\\uAA60-\\uAA76\\uAA7A\\uAA80-\\uAAAF\\uAAB1\\uAAB5]|[\\uAAB6\\uAAB9-\\uAABD\\uAAC0\\uAAC2\\uAADB-\\uAADD\\uAAE0-\\uAAEA\\uAAF2-\\uAAF4]|[\\uAB01-\\uAB06\\uAB09-\\uAB0E\\uAB11-\\uAB16\\uAB20-\\uAB26\\uAB28-\\uAB2E]|[\\uABC0-\\uABE2\\uAC00-\\uD7A3\\uD7B0-\\uD7C6\\uD7CB-\\uD7FB\\uF900-\\uFA6D]|[\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D\\uFB1F-\\uFB28\\uFB2A-\\uFB36]|[\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D]|[\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE70-\\uFE74\\uFE76-\\uFEFC]|[\\uFF21-\\uFF3A\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF]|[\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC])"),
        AgentflowJisonLexerPattern(105, "^(?:\\|)"),
        AgentflowJisonLexerPattern(106, "^(?:\\|)"),
        AgentflowJisonLexerPattern(107, "^(?:\\))"),
        AgentflowJisonLexerPattern(108, "^(?:\\()"),
        AgentflowJisonLexerPattern(109, "^(?:\\])"),
        AgentflowJisonLexerPattern(110, "^(?:\\[)"),
        AgentflowJisonLexerPattern(111, "^(?:(\\}))"),
        AgentflowJisonLexerPattern(112, "^(?:\\{)"),
        AgentflowJisonLexerPattern(113, "^(?:%%(?!\\{)[^\\n]*)"),
        AgentflowJisonLexerPattern(114, "^(?:(?:[^\\[\\]\\(\\)\\{\\}\\|\\\"%]+|(?!))+)"),
        AgentflowJisonLexerPattern(115, "^(?:\")"),
        AgentflowJisonLexerPattern(116, "^(?:(\\r?\\n)+)"),
        AgentflowJisonLexerPattern(117, "^(?:\\s)"),
        AgentflowJisonLexerPattern(118, "^(?:\$)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "shapeDataEndBracket" to intArrayOf(21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "shapeDataStr" to intArrayOf(9, 10, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "shapeData" to intArrayOf(8, 11, 12, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "callbackargs" to intArrayOf(17, 18, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "callbackname" to intArrayOf(14, 15, 16, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "href" to intArrayOf(21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "click" to intArrayOf(21, 24, 33, 34, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "edgeText" to intArrayOf(21, 24, 69, 71, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "trapText" to intArrayOf(21, 24, 76, 78, 80, 84, 86, 87, 88, 89, 90, 91, 92, 106, 108, 110, 112),
        "ellipseText" to intArrayOf(21, 24, 73, 74, 75, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "text" to intArrayOf(21, 24, 76, 77, 78, 79, 80, 83, 84, 85, 86, 91, 92, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114),
        "vertex" to intArrayOf(21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "dir" to intArrayOf(21, 24, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "acc_descr_multiline" to intArrayOf(5, 6, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "acc_descr" to intArrayOf(3, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "acc_title" to intArrayOf(1, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "md_string" to intArrayOf(19, 20, 21, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "string" to intArrayOf(21, 22, 23, 24, 76, 78, 80, 84, 86, 91, 92, 106, 108, 110, 112),
        "INITIAL" to intArrayOf(0, 2, 4, 7, 13, 21, 24, 25, 26, 27, 28, 29, 30, 31, 32, 35, 36, 37, 38, 39, 40, 41, 42, 43, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 72, 76, 78, 80, 81, 82, 84, 86, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 106, 108, 110, 112, 115, 116, 117, 118),
    )
}
