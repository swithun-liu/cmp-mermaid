package com.swithun.cmpmermaid.core.classdiagram.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 classDiagram.jison.
 * Upstream classDiagram.jison SHA-256: c2ea20022e4adf501dbbcda909a1bdb74e05a2ac53ff5276d09d00fd54a3e21e
 * Distribution chunk SHA-256: 0988351419a58910bffa177fe4a6dd9b9d74d50d65badb726e0044d7f397c84e
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:class-parser
 */
internal object ClassJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "mermaidDoc" to 4,
        "statements" to 5,
        "graphConfig" to 6,
        "CLASS_DIAGRAM" to 7,
        "NEWLINE" to 8,
        "EOF" to 9,
        "statement" to 10,
        "classLabel" to 11,
        "SQS" to 12,
        "STR" to 13,
        "SQE" to 14,
        "namespaceName" to 15,
        "alphaNumToken" to 16,
        "classLiteralName" to 17,
        "DOT" to 18,
        "className" to 19,
        "GENERICTYPE" to 20,
        "relationStatement" to 21,
        "LABEL" to 22,
        "namespaceStatement" to 23,
        "classStatement" to 24,
        "memberStatement" to 25,
        "annotationStatement" to 26,
        "clickStatement" to 27,
        "styleStatement" to 28,
        "cssClassStatement" to 29,
        "noteStatement" to 30,
        "classDefStatement" to 31,
        "direction" to 32,
        "acc_title" to 33,
        "acc_title_value" to 34,
        "acc_descr" to 35,
        "acc_descr_value" to 36,
        "acc_descr_multiline_value" to 37,
        "namespaceIdentifier" to 38,
        "STRUCT_START" to 39,
        "classStatements" to 40,
        "STRUCT_STOP" to 41,
        "NAMESPACE" to 42,
        "classIdentifier" to 43,
        "STYLE_SEPARATOR" to 44,
        "members" to 45,
        "ANNOTATION_START" to 46,
        "ANNOTATION_END" to 47,
        "CLASS" to 48,
        "emptyBody" to 49,
        "SPACE" to 50,
        "MEMBER" to 51,
        "SEPARATOR" to 52,
        "relation" to 53,
        "NOTE_FOR" to 54,
        "noteText" to 55,
        "NOTE" to 56,
        "CLASSDEF" to 57,
        "classList" to 58,
        "stylesOpt" to 59,
        "ALPHA" to 60,
        "COMMA" to 61,
        "direction_tb" to 62,
        "direction_bt" to 63,
        "direction_rl" to 64,
        "direction_lr" to 65,
        "relationType" to 66,
        "lineType" to 67,
        "AGGREGATION" to 68,
        "EXTENSION" to 69,
        "COMPOSITION" to 70,
        "DEPENDENCY" to 71,
        "LOLLIPOP" to 72,
        "LINE" to 73,
        "DOTTED_LINE" to 74,
        "CALLBACK" to 75,
        "LINK" to 76,
        "LINK_TARGET" to 77,
        "CLICK" to 78,
        "CALLBACK_NAME" to 79,
        "CALLBACK_ARGS" to 80,
        "HREF" to 81,
        "STYLE" to 82,
        "CSSCLASS" to 83,
        "style" to 84,
        "styleComponent" to 85,
        "NUM" to 86,
        "COLON" to 87,
        "UNIT" to 88,
        "BRKT" to 89,
        "PCT" to 90,
        "commentToken" to 91,
        "textToken" to 92,
        "graphCodeTokens" to 93,
        "textNoTagsToken" to 94,
        "TAGSTART" to 95,
        "TAGEND" to 96,
        "==" to 97,
        "--" to 98,
        "DEFAULT" to 99,
        "MINUS" to 100,
        "keywords" to 101,
        "UNICODE_TEXT" to 102,
        "BQUOTE_STR" to 103,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        7 to "CLASS_DIAGRAM",
        8 to "NEWLINE",
        9 to "EOF",
        12 to "SQS",
        13 to "STR",
        14 to "SQE",
        18 to "DOT",
        20 to "GENERICTYPE",
        22 to "LABEL",
        33 to "acc_title",
        34 to "acc_title_value",
        35 to "acc_descr",
        36 to "acc_descr_value",
        37 to "acc_descr_multiline_value",
        39 to "STRUCT_START",
        41 to "STRUCT_STOP",
        42 to "NAMESPACE",
        44 to "STYLE_SEPARATOR",
        46 to "ANNOTATION_START",
        47 to "ANNOTATION_END",
        48 to "CLASS",
        50 to "SPACE",
        51 to "MEMBER",
        52 to "SEPARATOR",
        54 to "NOTE_FOR",
        56 to "NOTE",
        57 to "CLASSDEF",
        60 to "ALPHA",
        61 to "COMMA",
        62 to "direction_tb",
        63 to "direction_bt",
        64 to "direction_rl",
        65 to "direction_lr",
        68 to "AGGREGATION",
        69 to "EXTENSION",
        70 to "COMPOSITION",
        71 to "DEPENDENCY",
        72 to "LOLLIPOP",
        73 to "LINE",
        74 to "DOTTED_LINE",
        75 to "CALLBACK",
        76 to "LINK",
        77 to "LINK_TARGET",
        78 to "CLICK",
        79 to "CALLBACK_NAME",
        80 to "CALLBACK_ARGS",
        81 to "HREF",
        82 to "STYLE",
        83 to "CSSCLASS",
        86 to "NUM",
        87 to "COLON",
        88 to "UNIT",
        89 to "BRKT",
        90 to "PCT",
        93 to "graphCodeTokens",
        95 to "TAGSTART",
        96 to "TAGEND",
        97 to "==",
        98 to "--",
        99 to "DEFAULT",
        100 to "MINUS",
        101 to "keywords",
        102 to "UNICODE_TEXT",
        103 to "BQUOTE_STR",
    )

    val productions: Array<ClassJisonProduction> = arrayOf(
        ClassJisonProduction(symbol = 0, length = 0),
        ClassJisonProduction(symbol = 3, length = 1),
        ClassJisonProduction(symbol = 3, length = 1),
        ClassJisonProduction(symbol = 4, length = 1),
        ClassJisonProduction(symbol = 6, length = 4),
        ClassJisonProduction(symbol = 5, length = 1),
        ClassJisonProduction(symbol = 5, length = 2),
        ClassJisonProduction(symbol = 5, length = 3),
        ClassJisonProduction(symbol = 11, length = 3),
        ClassJisonProduction(symbol = 15, length = 1),
        ClassJisonProduction(symbol = 15, length = 1),
        ClassJisonProduction(symbol = 15, length = 3),
        ClassJisonProduction(symbol = 15, length = 2),
        ClassJisonProduction(symbol = 19, length = 1),
        ClassJisonProduction(symbol = 19, length = 3),
        ClassJisonProduction(symbol = 19, length = 1),
        ClassJisonProduction(symbol = 19, length = 2),
        ClassJisonProduction(symbol = 19, length = 2),
        ClassJisonProduction(symbol = 19, length = 2),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 2),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 10, length = 2),
        ClassJisonProduction(symbol = 10, length = 2),
        ClassJisonProduction(symbol = 10, length = 1),
        ClassJisonProduction(symbol = 23, length = 4),
        ClassJisonProduction(symbol = 23, length = 5),
        ClassJisonProduction(symbol = 38, length = 2),
        ClassJisonProduction(symbol = 38, length = 3),
        ClassJisonProduction(symbol = 40, length = 1),
        ClassJisonProduction(symbol = 40, length = 2),
        ClassJisonProduction(symbol = 40, length = 3),
        ClassJisonProduction(symbol = 40, length = 1),
        ClassJisonProduction(symbol = 40, length = 2),
        ClassJisonProduction(symbol = 40, length = 3),
        ClassJisonProduction(symbol = 40, length = 1),
        ClassJisonProduction(symbol = 40, length = 2),
        ClassJisonProduction(symbol = 40, length = 3),
        ClassJisonProduction(symbol = 24, length = 1),
        ClassJisonProduction(symbol = 24, length = 3),
        ClassJisonProduction(symbol = 24, length = 4),
        ClassJisonProduction(symbol = 24, length = 3),
        ClassJisonProduction(symbol = 24, length = 6),
        ClassJisonProduction(symbol = 24, length = 4),
        ClassJisonProduction(symbol = 24, length = 7),
        ClassJisonProduction(symbol = 24, length = 6),
        ClassJisonProduction(symbol = 43, length = 2),
        ClassJisonProduction(symbol = 43, length = 3),
        ClassJisonProduction(symbol = 49, length = 0),
        ClassJisonProduction(symbol = 49, length = 2),
        ClassJisonProduction(symbol = 49, length = 2),
        ClassJisonProduction(symbol = 26, length = 4),
        ClassJisonProduction(symbol = 45, length = 1),
        ClassJisonProduction(symbol = 45, length = 2),
        ClassJisonProduction(symbol = 25, length = 1),
        ClassJisonProduction(symbol = 25, length = 2),
        ClassJisonProduction(symbol = 25, length = 1),
        ClassJisonProduction(symbol = 25, length = 1),
        ClassJisonProduction(symbol = 21, length = 3),
        ClassJisonProduction(symbol = 21, length = 4),
        ClassJisonProduction(symbol = 21, length = 4),
        ClassJisonProduction(symbol = 21, length = 5),
        ClassJisonProduction(symbol = 30, length = 3),
        ClassJisonProduction(symbol = 30, length = 2),
        ClassJisonProduction(symbol = 31, length = 3),
        ClassJisonProduction(symbol = 58, length = 1),
        ClassJisonProduction(symbol = 58, length = 3),
        ClassJisonProduction(symbol = 32, length = 1),
        ClassJisonProduction(symbol = 32, length = 1),
        ClassJisonProduction(symbol = 32, length = 1),
        ClassJisonProduction(symbol = 32, length = 1),
        ClassJisonProduction(symbol = 53, length = 3),
        ClassJisonProduction(symbol = 53, length = 2),
        ClassJisonProduction(symbol = 53, length = 2),
        ClassJisonProduction(symbol = 53, length = 1),
        ClassJisonProduction(symbol = 66, length = 1),
        ClassJisonProduction(symbol = 66, length = 1),
        ClassJisonProduction(symbol = 66, length = 1),
        ClassJisonProduction(symbol = 66, length = 1),
        ClassJisonProduction(symbol = 66, length = 1),
        ClassJisonProduction(symbol = 67, length = 1),
        ClassJisonProduction(symbol = 67, length = 1),
        ClassJisonProduction(symbol = 27, length = 3),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 3),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 5),
        ClassJisonProduction(symbol = 27, length = 3),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 5),
        ClassJisonProduction(symbol = 27, length = 4),
        ClassJisonProduction(symbol = 27, length = 5),
        ClassJisonProduction(symbol = 27, length = 5),
        ClassJisonProduction(symbol = 27, length = 6),
        ClassJisonProduction(symbol = 28, length = 3),
        ClassJisonProduction(symbol = 29, length = 3),
        ClassJisonProduction(symbol = 59, length = 1),
        ClassJisonProduction(symbol = 59, length = 3),
        ClassJisonProduction(symbol = 84, length = 1),
        ClassJisonProduction(symbol = 84, length = 2),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 85, length = 1),
        ClassJisonProduction(symbol = 91, length = 1),
        ClassJisonProduction(symbol = 91, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 92, length = 1),
        ClassJisonProduction(symbol = 94, length = 1),
        ClassJisonProduction(symbol = 94, length = 1),
        ClassJisonProduction(symbol = 94, length = 1),
        ClassJisonProduction(symbol = 94, length = 1),
        ClassJisonProduction(symbol = 16, length = 1),
        ClassJisonProduction(symbol = 16, length = 1),
        ClassJisonProduction(symbol = 16, length = 1),
        ClassJisonProduction(symbol = 16, length = 1),
        ClassJisonProduction(symbol = 17, length = 1),
        ClassJisonProduction(symbol = 55, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: ClassJisonCell = ClassJisonCell.Goto(1)
    private val action1: ClassJisonCell = ClassJisonCell.Goto(2)
    private val action2: ClassJisonCell = ClassJisonCell.Goto(3)
    private val action3: ClassJisonCell = ClassJisonCell.Goto(4)
    private val action4: ClassJisonCell = ClassJisonCell.Shift(6)
    private val action5: ClassJisonCell = ClassJisonCell.Goto(5)
    private val action6: ClassJisonCell = ClassJisonCell.Goto(39)
    private val action7: ClassJisonCell = ClassJisonCell.Goto(40)
    private val action8: ClassJisonCell = ClassJisonCell.Goto(21)
    private val action9: ClassJisonCell = ClassJisonCell.Goto(7)
    private val action10: ClassJisonCell = ClassJisonCell.Goto(8)
    private val action11: ClassJisonCell = ClassJisonCell.Goto(9)
    private val action12: ClassJisonCell = ClassJisonCell.Goto(10)
    private val action13: ClassJisonCell = ClassJisonCell.Goto(11)
    private val action14: ClassJisonCell = ClassJisonCell.Goto(12)
    private val action15: ClassJisonCell = ClassJisonCell.Goto(13)
    private val action16: ClassJisonCell = ClassJisonCell.Goto(14)
    private val action17: ClassJisonCell = ClassJisonCell.Goto(15)
    private val action18: ClassJisonCell = ClassJisonCell.Goto(16)
    private val action19: ClassJisonCell = ClassJisonCell.Goto(17)
    private val action20: ClassJisonCell = ClassJisonCell.Shift(18)
    private val action21: ClassJisonCell = ClassJisonCell.Shift(19)
    private val action22: ClassJisonCell = ClassJisonCell.Shift(20)
    private val action23: ClassJisonCell = ClassJisonCell.Goto(22)
    private val action24: ClassJisonCell = ClassJisonCell.Shift(41)
    private val action25: ClassJisonCell = ClassJisonCell.Goto(23)
    private val action26: ClassJisonCell = ClassJisonCell.Shift(26)
    private val action27: ClassJisonCell = ClassJisonCell.Shift(42)
    private val action28: ClassJisonCell = ClassJisonCell.Shift(24)
    private val action29: ClassJisonCell = ClassJisonCell.Shift(25)
    private val action30: ClassJisonCell = ClassJisonCell.Shift(32)
    private val action31: ClassJisonCell = ClassJisonCell.Shift(33)
    private val action32: ClassJisonCell = ClassJisonCell.Shift(34)
    private val action33: ClassJisonCell = ClassJisonCell.Shift(45)
    private val action34: ClassJisonCell = ClassJisonCell.Shift(35)
    private val action35: ClassJisonCell = ClassJisonCell.Shift(36)
    private val action36: ClassJisonCell = ClassJisonCell.Shift(37)
    private val action37: ClassJisonCell = ClassJisonCell.Shift(38)
    private val action38: ClassJisonCell = ClassJisonCell.Shift(27)
    private val action39: ClassJisonCell = ClassJisonCell.Shift(28)
    private val action40: ClassJisonCell = ClassJisonCell.Shift(29)
    private val action41: ClassJisonCell = ClassJisonCell.Shift(30)
    private val action42: ClassJisonCell = ClassJisonCell.Shift(31)
    private val action43: ClassJisonCell = ClassJisonCell.Shift(44)
    private val action44: ClassJisonCell = ClassJisonCell.Shift(46)
    private val action45: ClassJisonCell = ClassJisonCell.Shift(43)
    private val action46: ClassJisonCell = ClassJisonCell.Shift(47)
    private val action47: ClassJisonCell = ClassJisonCell.Accept
    private val action48: ClassJisonCell = ClassJisonCell.Reduce(1)
    private val action49: ClassJisonCell = ClassJisonCell.Reduce(2)
    private val action50: ClassJisonCell = ClassJisonCell.Reduce(3)
    private val action51: ClassJisonCell = ClassJisonCell.Reduce(5)
    private val action52: ClassJisonCell = ClassJisonCell.Shift(48)
    private val action53: ClassJisonCell = ClassJisonCell.Shift(49)
    private val action54: ClassJisonCell = ClassJisonCell.Reduce(19)
    private val action55: ClassJisonCell = ClassJisonCell.Shift(50)
    private val action56: ClassJisonCell = ClassJisonCell.Reduce(21)
    private val action57: ClassJisonCell = ClassJisonCell.Reduce(22)
    private val action58: ClassJisonCell = ClassJisonCell.Reduce(23)
    private val action59: ClassJisonCell = ClassJisonCell.Reduce(24)
    private val action60: ClassJisonCell = ClassJisonCell.Reduce(25)
    private val action61: ClassJisonCell = ClassJisonCell.Reduce(26)
    private val action62: ClassJisonCell = ClassJisonCell.Reduce(27)
    private val action63: ClassJisonCell = ClassJisonCell.Reduce(28)
    private val action64: ClassJisonCell = ClassJisonCell.Reduce(29)
    private val action65: ClassJisonCell = ClassJisonCell.Reduce(30)
    private val action66: ClassJisonCell = ClassJisonCell.Shift(51)
    private val action67: ClassJisonCell = ClassJisonCell.Shift(52)
    private val action68: ClassJisonCell = ClassJisonCell.Reduce(33)
    private val action69: ClassJisonCell = ClassJisonCell.Reduce(63)
    private val action70: ClassJisonCell = ClassJisonCell.Shift(54)
    private val action71: ClassJisonCell = ClassJisonCell.Shift(55)
    private val action72: ClassJisonCell = ClassJisonCell.Goto(53)
    private val action73: ClassJisonCell = ClassJisonCell.Goto(56)
    private val action74: ClassJisonCell = ClassJisonCell.Goto(57)
    private val action75: ClassJisonCell = ClassJisonCell.Shift(58)
    private val action76: ClassJisonCell = ClassJisonCell.Shift(59)
    private val action77: ClassJisonCell = ClassJisonCell.Shift(60)
    private val action78: ClassJisonCell = ClassJisonCell.Shift(61)
    private val action79: ClassJisonCell = ClassJisonCell.Shift(62)
    private val action80: ClassJisonCell = ClassJisonCell.Shift(63)
    private val action81: ClassJisonCell = ClassJisonCell.Shift(64)
    private val action82: ClassJisonCell = ClassJisonCell.Shift(65)
    private val action83: ClassJisonCell = ClassJisonCell.Reduce(47)
    private val action84: ClassJisonCell = ClassJisonCell.Shift(67)
    private val action85: ClassJisonCell = ClassJisonCell.Shift(66)
    private val action86: ClassJisonCell = ClassJisonCell.Shift(68)
    private val action87: ClassJisonCell = ClassJisonCell.Reduce(65)
    private val action88: ClassJisonCell = ClassJisonCell.Reduce(66)
    private val action89: ClassJisonCell = ClassJisonCell.Goto(69)
    private val action90: ClassJisonCell = ClassJisonCell.Goto(70)
    private val action91: ClassJisonCell = ClassJisonCell.Goto(71)
    private val action92: ClassJisonCell = ClassJisonCell.Goto(72)
    private val action93: ClassJisonCell = ClassJisonCell.Shift(73)
    private val action94: ClassJisonCell = ClassJisonCell.Shift(74)
    private val action95: ClassJisonCell = ClassJisonCell.Goto(75)
    private val action96: ClassJisonCell = ClassJisonCell.Shift(77)
    private val action97: ClassJisonCell = ClassJisonCell.Goto(76)
    private val action98: ClassJisonCell = ClassJisonCell.Goto(78)
    private val action99: ClassJisonCell = ClassJisonCell.Shift(79)
    private val action100: ClassJisonCell = ClassJisonCell.Reduce(76)
    private val action101: ClassJisonCell = ClassJisonCell.Reduce(77)
    private val action102: ClassJisonCell = ClassJisonCell.Reduce(78)
    private val action103: ClassJisonCell = ClassJisonCell.Reduce(79)
    private val action104: ClassJisonCell = ClassJisonCell.Reduce(13)
    private val action105: ClassJisonCell = ClassJisonCell.Shift(80)
    private val action106: ClassJisonCell = ClassJisonCell.Goto(81)
    private val action107: ClassJisonCell = ClassJisonCell.Shift(82)
    private val action108: ClassJisonCell = ClassJisonCell.Reduce(15)
    private val action109: ClassJisonCell = ClassJisonCell.Shift(83)
    private val action110: ClassJisonCell = ClassJisonCell.Goto(84)
    private val action111: ClassJisonCell = ClassJisonCell.Goto(85)
    private val action112: ClassJisonCell = ClassJisonCell.Goto(86)
    private val action113: ClassJisonCell = ClassJisonCell.Goto(87)
    private val action114: ClassJisonCell = ClassJisonCell.Reduce(133)
    private val action115: ClassJisonCell = ClassJisonCell.Reduce(134)
    private val action116: ClassJisonCell = ClassJisonCell.Reduce(135)
    private val action117: ClassJisonCell = ClassJisonCell.Reduce(136)
    private val action118: ClassJisonCell = ClassJisonCell.Reduce(137)
    private val action119: ClassJisonCell = ClassJisonCell.Reduce(6)
    private val action120: ClassJisonCell = ClassJisonCell.Goto(88)
    private val action121: ClassJisonCell = ClassJisonCell.Goto(89)
    private val action122: ClassJisonCell = ClassJisonCell.Reduce(20)
    private val action123: ClassJisonCell = ClassJisonCell.Reduce(31)
    private val action124: ClassJisonCell = ClassJisonCell.Reduce(32)
    private val action125: ClassJisonCell = ClassJisonCell.Shift(91)
    private val action126: ClassJisonCell = ClassJisonCell.Goto(90)
    private val action127: ClassJisonCell = ClassJisonCell.Goto(92)
    private val action128: ClassJisonCell = ClassJisonCell.Reduce(64)
    private val action129: ClassJisonCell = ClassJisonCell.Goto(93)
    private val action130: ClassJisonCell = ClassJisonCell.Reduce(83)
    private val action131: ClassJisonCell = ClassJisonCell.Goto(94)
    private val action132: ClassJisonCell = ClassJisonCell.Reduce(84)
    private val action133: ClassJisonCell = ClassJisonCell.Reduce(85)
    private val action134: ClassJisonCell = ClassJisonCell.Reduce(86)
    private val action135: ClassJisonCell = ClassJisonCell.Reduce(87)
    private val action136: ClassJisonCell = ClassJisonCell.Reduce(88)
    private val action137: ClassJisonCell = ClassJisonCell.Reduce(89)
    private val action138: ClassJisonCell = ClassJisonCell.Reduce(90)
    private val action139: ClassJisonCell = ClassJisonCell.Shift(96)
    private val action140: ClassJisonCell = ClassJisonCell.Goto(99)
    private val action141: ClassJisonCell = ClassJisonCell.Goto(97)
    private val action142: ClassJisonCell = ClassJisonCell.Goto(98)
    private val action143: ClassJisonCell = ClassJisonCell.Goto(95)
    private val action144: ClassJisonCell = ClassJisonCell.Goto(100)
    private val action145: ClassJisonCell = ClassJisonCell.Shift(102)
    private val action146: ClassJisonCell = ClassJisonCell.Goto(101)
    private val action147: ClassJisonCell = ClassJisonCell.Shift(103)
    private val action148: ClassJisonCell = ClassJisonCell.Goto(104)
    private val action149: ClassJisonCell = ClassJisonCell.Shift(105)
    private val action150: ClassJisonCell = ClassJisonCell.Shift(106)
    private val action151: ClassJisonCell = ClassJisonCell.Shift(107)
    private val action152: ClassJisonCell = ClassJisonCell.Shift(108)
    private val action153: ClassJisonCell = ClassJisonCell.Shift(109)
    private val action154: ClassJisonCell = ClassJisonCell.Shift(121)
    private val action155: ClassJisonCell = ClassJisonCell.Shift(117)
    private val action156: ClassJisonCell = ClassJisonCell.Goto(110)
    private val action157: ClassJisonCell = ClassJisonCell.Shift(113)
    private val action158: ClassJisonCell = ClassJisonCell.Shift(119)
    private val action159: ClassJisonCell = ClassJisonCell.Goto(111)
    private val action160: ClassJisonCell = ClassJisonCell.Goto(112)
    private val action161: ClassJisonCell = ClassJisonCell.Shift(114)
    private val action162: ClassJisonCell = ClassJisonCell.Shift(115)
    private val action163: ClassJisonCell = ClassJisonCell.Shift(116)
    private val action164: ClassJisonCell = ClassJisonCell.Shift(118)
    private val action165: ClassJisonCell = ClassJisonCell.Shift(120)
    private val action166: ClassJisonCell = ClassJisonCell.Shift(122)
    private val action167: ClassJisonCell = ClassJisonCell.Goto(123)
    private val action168: ClassJisonCell = ClassJisonCell.Reduce(72)
    private val action169: ClassJisonCell = ClassJisonCell.Reduce(138)
    private val action170: ClassJisonCell = ClassJisonCell.Goto(124)
    private val action171: ClassJisonCell = ClassJisonCell.Shift(125)
    private val action172: ClassJisonCell = ClassJisonCell.Reduce(74)
    private val action173: ClassJisonCell = ClassJisonCell.Goto(126)
    private val action174: ClassJisonCell = ClassJisonCell.Reduce(16)
    private val action175: ClassJisonCell = ClassJisonCell.Reduce(17)
    private val action176: ClassJisonCell = ClassJisonCell.Reduce(18)
    private val action177: ClassJisonCell = ClassJisonCell.Goto(127)
    private val action178: ClassJisonCell = ClassJisonCell.Shift(128)
    private val action179: ClassJisonCell = ClassJisonCell.Reduce(36)
    private val action180: ClassJisonCell = ClassJisonCell.Reduce(9)
    private val action181: ClassJisonCell = ClassJisonCell.Goto(130)
    private val action182: ClassJisonCell = ClassJisonCell.Shift(129)
    private val action183: ClassJisonCell = ClassJisonCell.Reduce(10)
    private val action184: ClassJisonCell = ClassJisonCell.Reduce(55)
    private val action185: ClassJisonCell = ClassJisonCell.Goto(131)
    private val action186: ClassJisonCell = ClassJisonCell.Reduce(7)
    private val action187: ClassJisonCell = ClassJisonCell.Shift(132)
    private val action188: ClassJisonCell = ClassJisonCell.Reduce(67)
    private val action189: ClassJisonCell = ClassJisonCell.Goto(133)
    private val action190: ClassJisonCell = ClassJisonCell.Shift(135)
    private val action191: ClassJisonCell = ClassJisonCell.Goto(134)
    private val action192: ClassJisonCell = ClassJisonCell.Reduce(82)
    private val action193: ClassJisonCell = ClassJisonCell.Goto(136)
    private val action194: ClassJisonCell = ClassJisonCell.Reduce(81)
    private val action195: ClassJisonCell = ClassJisonCell.Shift(137)
    private val action196: ClassJisonCell = ClassJisonCell.Goto(138)
    private val action197: ClassJisonCell = ClassJisonCell.Shift(139)
    private val action198: ClassJisonCell = ClassJisonCell.Reduce(38)
    private val action199: ClassJisonCell = ClassJisonCell.Shift(140)
    private val action200: ClassJisonCell = ClassJisonCell.Reduce(41)
    private val action201: ClassJisonCell = ClassJisonCell.Shift(141)
    private val action202: ClassJisonCell = ClassJisonCell.Reduce(44)
    private val action203: ClassJisonCell = ClassJisonCell.Reduce(48)
    private val action204: ClassJisonCell = ClassJisonCell.Shift(142)
    private val action205: ClassJisonCell = ClassJisonCell.Shift(143)
    private val action206: ClassJisonCell = ClassJisonCell.Reduce(50)
    private val action207: ClassJisonCell = ClassJisonCell.Reduce(61)
    private val action208: ClassJisonCell = ClassJisonCell.Goto(144)
    private val action209: ClassJisonCell = ClassJisonCell.Shift(145)
    private val action210: ClassJisonCell = ClassJisonCell.Goto(146)
    private val action211: ClassJisonCell = ClassJisonCell.Reduce(91)
    private val action212: ClassJisonCell = ClassJisonCell.Shift(147)
    private val action213: ClassJisonCell = ClassJisonCell.Reduce(93)
    private val action214: ClassJisonCell = ClassJisonCell.Shift(149)
    private val action215: ClassJisonCell = ClassJisonCell.Shift(148)
    private val action216: ClassJisonCell = ClassJisonCell.Reduce(97)
    private val action217: ClassJisonCell = ClassJisonCell.Shift(150)
    private val action218: ClassJisonCell = ClassJisonCell.Shift(151)
    private val action219: ClassJisonCell = ClassJisonCell.Shift(152)
    private val action220: ClassJisonCell = ClassJisonCell.Reduce(105)
    private val action221: ClassJisonCell = ClassJisonCell.Shift(153)
    private val action222: ClassJisonCell = ClassJisonCell.Reduce(107)
    private val action223: ClassJisonCell = ClassJisonCell.Goto(154)
    private val action224: ClassJisonCell = ClassJisonCell.Reduce(109)
    private val action225: ClassJisonCell = ClassJisonCell.Reduce(111)
    private val action226: ClassJisonCell = ClassJisonCell.Reduce(112)
    private val action227: ClassJisonCell = ClassJisonCell.Reduce(113)
    private val action228: ClassJisonCell = ClassJisonCell.Reduce(114)
    private val action229: ClassJisonCell = ClassJisonCell.Reduce(115)
    private val action230: ClassJisonCell = ClassJisonCell.Reduce(116)
    private val action231: ClassJisonCell = ClassJisonCell.Reduce(117)
    private val action232: ClassJisonCell = ClassJisonCell.Reduce(118)
    private val action233: ClassJisonCell = ClassJisonCell.Reduce(119)
    private val action234: ClassJisonCell = ClassJisonCell.Reduce(106)
    private val action235: ClassJisonCell = ClassJisonCell.Reduce(71)
    private val action236: ClassJisonCell = ClassJisonCell.Reduce(73)
    private val action237: ClassJisonCell = ClassJisonCell.Shift(155)
    private val action238: ClassJisonCell = ClassJisonCell.Reduce(14)
    private val action239: ClassJisonCell = ClassJisonCell.Reduce(37)
    private val action240: ClassJisonCell = ClassJisonCell.Shift(156)
    private val action241: ClassJisonCell = ClassJisonCell.Goto(157)
    private val action242: ClassJisonCell = ClassJisonCell.Reduce(12)
    private val action243: ClassJisonCell = ClassJisonCell.Reduce(56)
    private val action244: ClassJisonCell = ClassJisonCell.Reduce(4)
    private val action245: ClassJisonCell = ClassJisonCell.Reduce(69)
    private val action246: ClassJisonCell = ClassJisonCell.Reduce(68)
    private val action247: ClassJisonCell = ClassJisonCell.Goto(158)
    private val action248: ClassJisonCell = ClassJisonCell.Reduce(80)
    private val action249: ClassJisonCell = ClassJisonCell.Reduce(34)
    private val action250: ClassJisonCell = ClassJisonCell.Shift(159)
    private val action251: ClassJisonCell = ClassJisonCell.Goto(160)
    private val action252: ClassJisonCell = ClassJisonCell.Reduce(39)
    private val action253: ClassJisonCell = ClassJisonCell.Goto(161)
    private val action254: ClassJisonCell = ClassJisonCell.Reduce(42)
    private val action255: ClassJisonCell = ClassJisonCell.Goto(162)
    private val action256: ClassJisonCell = ClassJisonCell.Reduce(45)
    private val action257: ClassJisonCell = ClassJisonCell.Goto(163)
    private val action258: ClassJisonCell = ClassJisonCell.Reduce(49)
    private val action259: ClassJisonCell = ClassJisonCell.Reduce(62)
    private val action260: ClassJisonCell = ClassJisonCell.Reduce(52)
    private val action261: ClassJisonCell = ClassJisonCell.Shift(164)
    private val action262: ClassJisonCell = ClassJisonCell.Reduce(60)
    private val action263: ClassJisonCell = ClassJisonCell.Reduce(92)
    private val action264: ClassJisonCell = ClassJisonCell.Reduce(94)
    private val action265: ClassJisonCell = ClassJisonCell.Reduce(95)
    private val action266: ClassJisonCell = ClassJisonCell.Shift(165)
    private val action267: ClassJisonCell = ClassJisonCell.Reduce(98)
    private val action268: ClassJisonCell = ClassJisonCell.Reduce(99)
    private val action269: ClassJisonCell = ClassJisonCell.Shift(166)
    private val action270: ClassJisonCell = ClassJisonCell.Reduce(101)
    private val action271: ClassJisonCell = ClassJisonCell.Shift(168)
    private val action272: ClassJisonCell = ClassJisonCell.Shift(167)
    private val action273: ClassJisonCell = ClassJisonCell.Goto(169)
    private val action274: ClassJisonCell = ClassJisonCell.Reduce(110)
    private val action275: ClassJisonCell = ClassJisonCell.Reduce(75)
    private val action276: ClassJisonCell = ClassJisonCell.Shift(170)
    private val action277: ClassJisonCell = ClassJisonCell.Reduce(11)
    private val action278: ClassJisonCell = ClassJisonCell.Reduce(70)
    private val action279: ClassJisonCell = ClassJisonCell.Reduce(35)
    private val action280: ClassJisonCell = ClassJisonCell.Reduce(40)
    private val action281: ClassJisonCell = ClassJisonCell.Reduce(43)
    private val action282: ClassJisonCell = ClassJisonCell.Reduce(46)
    private val action283: ClassJisonCell = ClassJisonCell.Shift(171)
    private val action284: ClassJisonCell = ClassJisonCell.Shift(173)
    private val action285: ClassJisonCell = ClassJisonCell.Goto(172)
    private val action286: ClassJisonCell = ClassJisonCell.Reduce(96)
    private val action287: ClassJisonCell = ClassJisonCell.Reduce(100)
    private val action288: ClassJisonCell = ClassJisonCell.Reduce(102)
    private val action289: ClassJisonCell = ClassJisonCell.Reduce(103)
    private val action290: ClassJisonCell = ClassJisonCell.Shift(174)
    private val action291: ClassJisonCell = ClassJisonCell.Reduce(108)
    private val action292: ClassJisonCell = ClassJisonCell.Reduce(8)
    private val action293: ClassJisonCell = ClassJisonCell.Reduce(51)
    private val action294: ClassJisonCell = ClassJisonCell.Shift(175)
    private val action295: ClassJisonCell = ClassJisonCell.Reduce(54)
    private val action296: ClassJisonCell = ClassJisonCell.Reduce(104)
    private val action297: ClassJisonCell = ClassJisonCell.Reduce(53)
    private val symbols0 = intArrayOf(1, 8, 9, 22)
    private val symbols1 = intArrayOf(1, 8, 9)
    private val symbols2 = intArrayOf(1, 8, 9, 13, 22, 53, 66, 67, 68, 69, 70, 71, 72, 73, 74)
    private val symbols3 = intArrayOf(1, 8, 9, 39, 41, 44, 46)
    private val symbols4 = intArrayOf(1, 8, 9, 12, 13, 16, 17, 18, 19, 20, 22, 39, 41, 44, 46, 60, 68, 69, 70, 71, 72, 73, 74, 79, 81, 86, 100, 102, 103)
    private val symbols5 = intArrayOf(1, 8, 9, 12, 13, 20, 22, 39, 41, 44, 46, 68, 69, 70, 71, 72, 73, 74, 79, 81)
    private val symbols6 = intArrayOf(1, 8, 9, 12, 13, 18, 20, 22, 39, 41, 44, 46, 47, 60, 68, 69, 70, 71, 72, 73, 74, 79, 81, 86, 100, 102, 103)
    private val symbols7 = intArrayOf(13, 60, 66, 68, 69, 70, 71, 72, 86, 100, 102, 103)
    private val symbols8 = intArrayOf(13, 60, 73, 74, 86, 100, 102, 103)
    private val symbols9 = intArrayOf(13, 60, 68, 69, 70, 71, 72, 86, 100, 102, 103)
    private val symbols10 = intArrayOf(1, 8, 9, 41)
    private val symbols11 = intArrayOf(22, 50, 60, 61, 82, 86, 87, 88, 89, 90)
    private val symbols12 = intArrayOf(1, 8, 9, 12, 13, 22, 39, 41, 44, 46, 68, 69, 70, 71, 72, 73, 74, 79, 81)
    private val symbols13 = intArrayOf(1, 8, 9, 11, 12, 39, 41, 44, 46)
    private val symbols14 = intArrayOf(13, 60, 86, 100, 102, 103)
    private val symbols15 = intArrayOf(1, 8, 9, 39, 41)
    private val symbols16 = intArrayOf(1, 8, 9, 13)
    private val symbols17 = intArrayOf(1, 8, 9, 13, 77)
    private val symbols18 = intArrayOf(1, 8, 9, 13, 80)
    private val symbols19 = intArrayOf(1, 8, 9, 61)
    private val symbols20 = intArrayOf(1, 8, 9, 22, 50, 60, 61, 82, 85, 86, 87, 88, 89, 90)
    private val symbols21 = intArrayOf(1, 8, 9, 22, 50, 60, 61, 82, 86, 87, 88, 89, 90)
    private val symbols22 = intArrayOf(1, 8, 9, 77)

    val states: Array<Map<Int, ClassJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
        addAll(stateChunk3())
        addAll(stateChunk4())
        addAll(stateChunk5())
        addAll(stateChunk6())
        addAll(stateChunk7())
        addAll(stateChunk8())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            5 to action2,
            6 to action3,
            7 to action4,
            10 to action5,
            16 to action6,
            17 to action7,
            19 to action8,
            21 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            27 to action14,
            28 to action15,
            29 to action16,
            30 to action17,
            31 to action18,
            32 to action19,
            33 to action20,
            35 to action21,
            37 to action22,
            38 to action23,
            42 to action24,
            43 to action25,
            46 to action26,
            48 to action27,
            51 to action28,
            52 to action29,
            54 to action30,
            56 to action31,
            57 to action32,
            60 to action33,
            62 to action34,
            63 to action35,
            64 to action36,
            65 to action37,
            75 to action38,
            76 to action39,
            78 to action40,
            82 to action41,
            83 to action42,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            1 to action47,
        ),
        mapOf(
            1 to action48,
        ),
        mapOf(
            1 to action49,
        ),
        mapOf(
            1 to action50,
        ),
        mapOf(
            1 to action51,
            8 to action52,
            9 to action51,
        ),
        mapOf(
            8 to action53,
        ),
        buildJisonState(symbols0, action54, mapOf(
            22 to action55,
        )),
        buildJisonState(symbols1, action56),
        buildJisonState(symbols1, action57),
        buildJisonState(symbols1, action58),
        buildJisonState(symbols1, action59),
        buildJisonState(symbols1, action60),
        buildJisonState(symbols1, action61),
        buildJisonState(symbols1, action62),
        buildJisonState(symbols1, action63),
        buildJisonState(symbols1, action64),
        buildJisonState(symbols1, action65),
        mapOf(
            34 to action66,
        ),
        mapOf(
            36 to action67,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        buildJisonState(symbols1, action68),
        buildJisonState(symbols2, action69, mapOf(
            13 to action70,
            22 to action71,
            53 to action72,
            66 to action73,
            67 to action74,
            68 to action75,
            69 to action76,
            70 to action77,
            71 to action78,
            72 to action79,
            73 to action80,
            74 to action81,
        )),
        mapOf(
            39 to action82,
        ),
        buildJisonState(symbols3, action83, mapOf(
            39 to action84,
            44 to action85,
            46 to action86,
        )),
        buildJisonState(symbols1, action87),
        buildJisonState(symbols1, action88),
        mapOf(
            16 to action89,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action90,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action91,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action92,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            60 to action93,
        ),
        mapOf(
            13 to action94,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action95,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            13 to action96,
            55 to action97,
        ),
        mapOf(
            58 to action98,
            60 to action99,
        ),
        buildJisonState(symbols1, action100),
        buildJisonState(symbols1, action101),
        buildJisonState(symbols1, action102),
        buildJisonState(symbols1, action103),
        buildJisonState(symbols4, action104, mapOf(
            16 to action6,
            17 to action7,
            18 to action105,
            19 to action106,
            20 to action107,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        )),
    )

    private fun stateChunk2(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        buildJisonState(symbols5, action108, mapOf(
            20 to action109,
        )),
        mapOf(
            15 to action110,
            16 to action111,
            17 to action112,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action113,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols6, action114),
        buildJisonState(symbols6, action115),
        buildJisonState(symbols6, action116),
        buildJisonState(symbols6, action117),
        buildJisonState(symbols5, action118),
        mapOf(
            1 to action119,
            5 to action120,
            9 to action119,
            10 to action5,
            16 to action6,
            17 to action7,
            19 to action8,
            21 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            27 to action14,
            28 to action15,
            29 to action16,
            30 to action17,
            31 to action18,
            32 to action19,
            33 to action20,
            35 to action21,
            37 to action22,
            38 to action23,
            42 to action24,
            43 to action25,
            46 to action26,
            48 to action27,
            51 to action28,
            52 to action29,
            54 to action30,
            56 to action31,
            57 to action32,
            60 to action33,
            62 to action34,
            63 to action35,
            64 to action36,
            65 to action37,
            75 to action38,
            76 to action39,
            78 to action40,
            82 to action41,
            83 to action42,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            5 to action121,
            10 to action5,
            16 to action6,
            17 to action7,
            19 to action8,
            21 to action9,
            23 to action10,
            24 to action11,
            25 to action12,
            26 to action13,
            27 to action14,
            28 to action15,
            29 to action16,
            30 to action17,
            31 to action18,
            32 to action19,
            33 to action20,
            35 to action21,
            37 to action22,
            38 to action23,
            42 to action24,
            43 to action25,
            46 to action26,
            48 to action27,
            51 to action28,
            52 to action29,
            54 to action30,
            56 to action31,
            57 to action32,
            60 to action33,
            62 to action34,
            63 to action35,
            64 to action36,
            65 to action37,
            75 to action38,
            76 to action39,
            78 to action40,
            82 to action41,
            83 to action42,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols1, action122),
        buildJisonState(symbols1, action123),
        buildJisonState(symbols1, action124),
        mapOf(
            13 to action125,
            16 to action6,
            17 to action7,
            19 to action126,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            53 to action127,
            66 to action73,
            67 to action74,
            68 to action75,
            69 to action76,
            70 to action77,
            71 to action78,
            72 to action79,
            73 to action80,
            74 to action81,
        ),
        buildJisonState(symbols1, action128),
        mapOf(
            67 to action129,
            73 to action80,
            74 to action81,
        ),
        buildJisonState(symbols7, action130, mapOf(
            66 to action131,
            68 to action75,
            69 to action76,
            70 to action77,
            71 to action78,
            72 to action79,
        )),
        buildJisonState(symbols8, action132),
        buildJisonState(symbols8, action133),
    )

    private fun stateChunk3(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        buildJisonState(symbols8, action134),
        buildJisonState(symbols8, action135),
        buildJisonState(symbols8, action136),
        buildJisonState(symbols9, action137),
        buildJisonState(symbols9, action138),
        mapOf(
            8 to action139,
            23 to action140,
            24 to action141,
            30 to action142,
            38 to action23,
            40 to action143,
            42 to action24,
            43 to action25,
            48 to action27,
            54 to action30,
            56 to action31,
        ),
        mapOf(
            16 to action144,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
        ),
        mapOf(
            41 to action145,
            45 to action146,
            51 to action147,
        ),
        mapOf(
            16 to action148,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
        ),
        mapOf(
            47 to action149,
        ),
        mapOf(
            13 to action150,
        ),
        mapOf(
            13 to action151,
        ),
        mapOf(
            79 to action152,
            81 to action153,
        ),
        mapOf(
            22 to action154,
            50 to action155,
            59 to action156,
            60 to action157,
            82 to action158,
            84 to action159,
            85 to action160,
            86 to action161,
            87 to action162,
            88 to action163,
            89 to action164,
            90 to action165,
        ),
        mapOf(
            60 to action166,
        ),
        mapOf(
            13 to action96,
            55 to action167,
        ),
        buildJisonState(symbols10, action168),
        buildJisonState(symbols10, action169),
        mapOf(
            22 to action154,
            50 to action155,
            59 to action170,
            60 to action157,
            61 to action171,
            82 to action158,
            84 to action159,
            85 to action160,
            86 to action161,
            87 to action162,
            88 to action163,
            89 to action164,
            90 to action165,
        ),
        buildJisonState(symbols11, action172),
    )

    private fun stateChunk4(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        mapOf(
            16 to action6,
            17 to action7,
            19 to action173,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols12, action174),
        buildJisonState(symbols12, action175),
        buildJisonState(symbols12, action176),
        mapOf(
            11 to action177,
            12 to action178,
            39 to action179,
        ),
        mapOf(
            12 to action180,
            15 to action181,
            16 to action111,
            17 to action112,
            18 to action182,
            39 to action180,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            12 to action183,
            39 to action183,
        ),
        buildJisonState(symbols13, action184, mapOf(
            11 to action185,
            12 to action178,
        )),
        mapOf(
            1 to action186,
            9 to action186,
        ),
        mapOf(
            9 to action187,
        ),
        buildJisonState(symbols0, action188),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action189,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            13 to action190,
            16 to action6,
            17 to action7,
            19 to action191,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols7, action192, mapOf(
            66 to action193,
            68 to action75,
            69 to action76,
            70 to action77,
            71 to action78,
            72 to action79,
        )),
        buildJisonState(symbols14, action194),
        mapOf(
            41 to action195,
        ),
        mapOf(
            23 to action140,
            24 to action141,
            30 to action142,
            38 to action23,
            40 to action196,
            42 to action24,
            43 to action25,
            48 to action27,
            54 to action30,
            56 to action31,
        ),
        mapOf(
            8 to action197,
            41 to action198,
        ),
        mapOf(
            8 to action199,
            41 to action200,
        ),
        mapOf(
            8 to action201,
            41 to action202,
        ),
    )

    private fun stateChunk5(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        buildJisonState(symbols15, action203, mapOf(
            39 to action204,
        )),
        mapOf(
            41 to action205,
        ),
        buildJisonState(symbols10, action206),
        mapOf(
            41 to action207,
            45 to action208,
            51 to action147,
        ),
        mapOf(
            47 to action209,
        ),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action210,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols16, action211, mapOf(
            13 to action212,
        )),
        buildJisonState(symbols17, action213, mapOf(
            13 to action214,
            77 to action215,
        )),
        buildJisonState(symbols18, action216, mapOf(
            13 to action217,
            80 to action218,
        )),
        mapOf(
            13 to action219,
        ),
        buildJisonState(symbols19, action220, mapOf(
            61 to action221,
        )),
        buildJisonState(symbols20, action222, mapOf(
            22 to action154,
            50 to action155,
            60 to action157,
            82 to action158,
            85 to action223,
            86 to action161,
            87 to action162,
            88 to action163,
            89 to action164,
            90 to action165,
        )),
        buildJisonState(symbols21, action224),
        buildJisonState(symbols21, action225),
        buildJisonState(symbols21, action226),
        buildJisonState(symbols21, action227),
        buildJisonState(symbols21, action228),
        buildJisonState(symbols21, action229),
        buildJisonState(symbols21, action230),
        buildJisonState(symbols21, action231),
    )

    private fun stateChunk6(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        buildJisonState(symbols21, action232),
        buildJisonState(symbols21, action233),
        buildJisonState(symbols1, action234),
        buildJisonState(symbols10, action235),
        buildJisonState(symbols19, action236, mapOf(
            61 to action221,
        )),
        mapOf(
            60 to action237,
        ),
        buildJisonState(symbols12, action238),
        mapOf(
            39 to action239,
        ),
        mapOf(
            13 to action240,
        ),
        mapOf(
            15 to action241,
            16 to action111,
            17 to action112,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        mapOf(
            12 to action242,
            39 to action242,
        ),
        buildJisonState(symbols3, action243),
        mapOf(
            1 to action244,
        ),
        buildJisonState(symbols0, action245),
        buildJisonState(symbols0, action246),
        mapOf(
            16 to action6,
            17 to action7,
            19 to action247,
            60 to action33,
            86 to action43,
            100 to action44,
            102 to action45,
            103 to action46,
        ),
        buildJisonState(symbols14, action248),
        buildJisonState(symbols10, action249),
        mapOf(
            41 to action250,
        ),
        mapOf(
            23 to action140,
            24 to action141,
            30 to action142,
            38 to action23,
            40 to action251,
            41 to action252,
            42 to action24,
            43 to action25,
            48 to action27,
            54 to action30,
            56 to action31,
        ),
    )

    private fun stateChunk7(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        mapOf(
            23 to action140,
            24 to action141,
            30 to action142,
            38 to action23,
            40 to action253,
            41 to action254,
            42 to action24,
            43 to action25,
            48 to action27,
            54 to action30,
            56 to action31,
        ),
        mapOf(
            23 to action140,
            24 to action141,
            30 to action142,
            38 to action23,
            40 to action255,
            41 to action256,
            42 to action24,
            43 to action25,
            48 to action27,
            54 to action30,
            56 to action31,
        ),
        mapOf(
            45 to action257,
            51 to action147,
        ),
        buildJisonState(symbols10, action258),
        mapOf(
            41 to action259,
        ),
        buildJisonState(symbols15, action260, mapOf(
            39 to action261,
        )),
        buildJisonState(symbols1, action262),
        buildJisonState(symbols1, action263),
        buildJisonState(symbols1, action264),
        buildJisonState(symbols22, action265, mapOf(
            77 to action266,
        )),
        buildJisonState(symbols1, action267),
        buildJisonState(symbols16, action268, mapOf(
            13 to action269,
        )),
        buildJisonState(symbols17, action270, mapOf(
            13 to action271,
            77 to action272,
        )),
        mapOf(
            22 to action154,
            50 to action155,
            60 to action157,
            82 to action158,
            84 to action273,
            85 to action160,
            86 to action161,
            87 to action162,
            88 to action163,
            89 to action164,
            90 to action165,
        ),
        buildJisonState(symbols21, action274),
        buildJisonState(symbols11, action275),
        mapOf(
            14 to action276,
        ),
        mapOf(
            12 to action277,
            39 to action277,
        ),
        buildJisonState(symbols0, action278),
        buildJisonState(symbols10, action279),
    )

    private fun stateChunk8(): Array<Map<Int, ClassJisonCell>> = arrayOf(
        mapOf(
            41 to action280,
        ),
        mapOf(
            41 to action281,
        ),
        mapOf(
            41 to action282,
        ),
        mapOf(
            41 to action283,
        ),
        mapOf(
            41 to action284,
            45 to action285,
            51 to action147,
        ),
        buildJisonState(symbols1, action286),
        buildJisonState(symbols1, action287),
        buildJisonState(symbols1, action288),
        buildJisonState(symbols22, action289, mapOf(
            77 to action290,
        )),
        buildJisonState(symbols20, action291, mapOf(
            22 to action154,
            50 to action155,
            60 to action157,
            82 to action158,
            85 to action223,
            86 to action161,
            87 to action162,
            88 to action163,
            89 to action164,
            90 to action165,
        )),
        buildJisonState(symbols3, action292),
        buildJisonState(symbols10, action293),
        mapOf(
            41 to action294,
        ),
        buildJisonState(symbols10, action295),
        buildJisonState(symbols1, action296),
        buildJisonState(symbols10, action297),
    )

    val lexerPatterns: List<ClassJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
    }

    private fun lexerChunk0(): List<ClassJisonLexerPattern> = listOf(
        ClassJisonLexerPattern(0, "^(?:.*direction\\s+TB[^\\n]*)"),
        ClassJisonLexerPattern(1, "^(?:.*direction\\s+BT[^\\n]*)"),
        ClassJisonLexerPattern(2, "^(?:.*direction\\s+RL[^\\n]*)"),
        ClassJisonLexerPattern(3, "^(?:.*direction\\s+LR[^\\n]*)"),
        ClassJisonLexerPattern(4, "^(?:%%(?!\\{)*[^\\n]*(\\r?\\n?)+)"),
        ClassJisonLexerPattern(5, "^(?:%%[^\\n]*(\\r?\\n)*)"),
        ClassJisonLexerPattern(6, "^(?:accTitle\\s*:\\s*)"),
        ClassJisonLexerPattern(7, "^(?:(?!\\n||)*[^\\n]*)"),
        ClassJisonLexerPattern(8, "^(?:accDescr\\s*:\\s*)"),
        ClassJisonLexerPattern(9, "^(?:(?!\\n||)*[^\\n]*)"),
        ClassJisonLexerPattern(10, "^(?:accDescr\\s*\\{\\s*)"),
        ClassJisonLexerPattern(11, "^(?:[\\}])"),
        ClassJisonLexerPattern(12, "^(?:[^\\}]*)"),
        ClassJisonLexerPattern(13, "^(?:\\s*(\\r?\\n)+)"),
        ClassJisonLexerPattern(14, "^(?:\\s+)"),
        ClassJisonLexerPattern(15, "^(?:classDiagram-v2\\b)"),
        ClassJisonLexerPattern(16, "^(?:classDiagram\\b)"),
        ClassJisonLexerPattern(17, "^(?:\\[\\*\\])"),
        ClassJisonLexerPattern(18, "^(?:call[\\s]+)"),
        ClassJisonLexerPattern(19, "^(?:\\([\\s]*\\))"),
    )

    private fun lexerChunk1(): List<ClassJisonLexerPattern> = listOf(
        ClassJisonLexerPattern(20, "^(?:\\()"),
        ClassJisonLexerPattern(21, "^(?:[^(]*)"),
        ClassJisonLexerPattern(22, "^(?:\\))"),
        ClassJisonLexerPattern(23, "^(?:[^)]*)"),
        ClassJisonLexerPattern(24, "^(?:[\"])"),
        ClassJisonLexerPattern(25, "^(?:[^\"]*)"),
        ClassJisonLexerPattern(26, "^(?:[\"])"),
        ClassJisonLexerPattern(27, "^(?:style\\b)"),
        ClassJisonLexerPattern(28, "^(?:classDef\\b)"),
        ClassJisonLexerPattern(29, "^(?:namespace\\b)"),
        ClassJisonLexerPattern(30, "^(?:\\s*(\\r?\\n)+)"),
        ClassJisonLexerPattern(31, "^(?:\\s+)"),
        ClassJisonLexerPattern(32, "^(?:[{])"),
        ClassJisonLexerPattern(33, "^(?:[}])"),
        ClassJisonLexerPattern(34, "^(?:[}])"),
        ClassJisonLexerPattern(35, "^(?:\$)"),
        ClassJisonLexerPattern(36, "^(?:\\s*(\\r?\\n)+)"),
        ClassJisonLexerPattern(37, "^(?:\\s+)"),
        ClassJisonLexerPattern(38, "^(?:\\[\\*\\])"),
        ClassJisonLexerPattern(39, "^(?:class\\b)"),
    )

    private fun lexerChunk2(): List<ClassJisonLexerPattern> = listOf(
        ClassJisonLexerPattern(40, "^(?:\\s*(\\r?\\n)+)"),
        ClassJisonLexerPattern(41, "^(?:\\s+)"),
        ClassJisonLexerPattern(42, "^(?:[}])"),
        ClassJisonLexerPattern(43, "^(?:[{])"),
        ClassJisonLexerPattern(44, "^(?:[}])"),
        ClassJisonLexerPattern(45, "^(?:\$)"),
        ClassJisonLexerPattern(46, "^(?:\\[\\*\\])"),
        ClassJisonLexerPattern(47, "^(?:[{])"),
        ClassJisonLexerPattern(48, "^(?:[\\n])"),
        ClassJisonLexerPattern(49, "^(?:[^{}\\n]*)"),
        ClassJisonLexerPattern(50, "^(?:cssClass\\b)"),
        ClassJisonLexerPattern(51, "^(?:callback\\b)"),
        ClassJisonLexerPattern(52, "^(?:link\\b)"),
        ClassJisonLexerPattern(53, "^(?:click\\b)"),
        ClassJisonLexerPattern(54, "^(?:note for\\b)"),
        ClassJisonLexerPattern(55, "^(?:note\\b)"),
        ClassJisonLexerPattern(56, "^(?:<<)"),
        ClassJisonLexerPattern(57, "^(?:>>)"),
        ClassJisonLexerPattern(58, "^(?:href\\b)"),
        ClassJisonLexerPattern(59, "^(?:[~])"),
    )

    private fun lexerChunk3(): List<ClassJisonLexerPattern> = listOf(
        ClassJisonLexerPattern(60, "^(?:[^~]*)"),
        ClassJisonLexerPattern(61, "^(?:~)"),
        ClassJisonLexerPattern(62, "^(?:[`])"),
        ClassJisonLexerPattern(63, "^(?:[^`]+)"),
        ClassJisonLexerPattern(64, "^(?:[`])"),
        ClassJisonLexerPattern(65, "^(?:_self\\b)"),
        ClassJisonLexerPattern(66, "^(?:_blank\\b)"),
        ClassJisonLexerPattern(67, "^(?:_parent\\b)"),
        ClassJisonLexerPattern(68, "^(?:_top\\b)"),
        ClassJisonLexerPattern(69, "^(?:\\s*<\\|)"),
        ClassJisonLexerPattern(70, "^(?:\\s*\\|>)"),
        ClassJisonLexerPattern(71, "^(?:\\s*>)"),
        ClassJisonLexerPattern(72, "^(?:\\s*<)"),
        ClassJisonLexerPattern(73, "^(?:\\s*\\*)"),
        ClassJisonLexerPattern(74, "^(?:\\s*o\\b)"),
        ClassJisonLexerPattern(75, "^(?:\\s*\\(\\))"),
        ClassJisonLexerPattern(76, "^(?:--)"),
        ClassJisonLexerPattern(77, "^(?:\\.\\.)"),
        ClassJisonLexerPattern(78, "^(?::{1}[^:\\n;]+)"),
        ClassJisonLexerPattern(79, "^(?::{3})"),
    )

    private fun lexerChunk4(): List<ClassJisonLexerPattern> = listOf(
        ClassJisonLexerPattern(80, "^(?:-)"),
        ClassJisonLexerPattern(81, "^(?:\\.)"),
        ClassJisonLexerPattern(82, "^(?:\\+)"),
        ClassJisonLexerPattern(83, "^(?::)"),
        ClassJisonLexerPattern(84, "^(?:,)"),
        ClassJisonLexerPattern(85, "^(?:#)"),
        ClassJisonLexerPattern(86, "^(?:#)"),
        ClassJisonLexerPattern(87, "^(?:%)"),
        ClassJisonLexerPattern(88, "^(?:=)"),
        ClassJisonLexerPattern(89, "^(?:=)"),
        ClassJisonLexerPattern(90, "^(?:\\w+)"),
        ClassJisonLexerPattern(91, "^(?:\\[)"),
        ClassJisonLexerPattern(92, "^(?:\\])"),
        ClassJisonLexerPattern(93, "^(?:[!\"#\$%&'*+,-.`?\\\\/])"),
        ClassJisonLexerPattern(94, "^(?:[0-9]+)"),
        ClassJisonLexerPattern(95, "^(?:[\\u00AA\\u00B5\\u00BA\\u00C0-\\u00D6\\u00D8-\\u00F6]|[\\u00F8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0370-\\u0374\\u0376\\u0377]|[\\u037A-\\u037D\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5]|[\\u03F7-\\u0481\\u048A-\\u0527\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05D0-\\u05EA]|[\\u05F0-\\u05F2\\u0620-\\u064A\\u066E\\u066F\\u0671-\\u06D3\\u06D5\\u06E5\\u06E6\\u06EE]|[\\u06EF\\u06FA-\\u06FC\\u06FF\\u0710\\u0712-\\u072F\\u074D-\\u07A5\\u07B1\\u07CA-\\u07EA]|[\\u07F4\\u07F5\\u07FA\\u0800-\\u0815\\u081A\\u0824\\u0828\\u0840-\\u0858\\u08A0]|[\\u08A2-\\u08AC\\u0904-\\u0939\\u093D\\u0950\\u0958-\\u0961\\u0971-\\u0977]|[\\u0979-\\u097F\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2]|[\\u09B6-\\u09B9\\u09BD\\u09CE\\u09DC\\u09DD\\u09DF-\\u09E1\\u09F0\\u09F1\\u0A05-\\u0A0A]|[\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39]|[\\u0A59-\\u0A5C\\u0A5E\\u0A72-\\u0A74\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8]|[\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABD\\u0AD0\\u0AE0\\u0AE1\\u0B05-\\u0B0C]|[\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3D\\u0B5C]|[\\u0B5D\\u0B5F-\\u0B61\\u0B71\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99]|[\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BD0]|[\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C33\\u0C35-\\u0C39\\u0C3D]|[\\u0C58\\u0C59\\u0C60\\u0C61\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3]|[\\u0CB5-\\u0CB9\\u0CBD\\u0CDE\\u0CE0\\u0CE1\\u0CF1\\u0CF2\\u0D05-\\u0D0C\\u0D0E-\\u0D10]|[\\u0D12-\\u0D3A\\u0D3D\\u0D4E\\u0D60\\u0D61\\u0D7A-\\u0D7F\\u0D85-\\u0D96\\u0D9A-\\u0DB1]|[\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0E01-\\u0E30\\u0E32\\u0E33\\u0E40-\\u0E46\\u0E81]|[\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3]|[\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB0\\u0EB2\\u0EB3\\u0EBD\\u0EC0-\\u0EC4\\u0EC6]|[\\u0EDC-\\u0EDF\\u0F00\\u0F40-\\u0F47\\u0F49-\\u0F6C\\u0F88-\\u0F8C\\u1000-\\u102A]|[\\u103F\\u1050-\\u1055\\u105A-\\u105D\\u1061\\u1065\\u1066\\u106E-\\u1070\\u1075-\\u1081]|[\\u108E\\u10A0-\\u10C5\\u10C7\\u10CD\\u10D0-\\u10FA\\u10FC-\\u1248\\u124A-\\u124D]|[\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0]|[\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310]|[\\u1312-\\u1315\\u1318-\\u135A\\u1380-\\u138F\\u13A0-\\u13F4\\u1401-\\u166C]|[\\u166F-\\u167F\\u1681-\\u169A\\u16A0-\\u16EA\\u1700-\\u170C\\u170E-\\u1711]|[\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176C\\u176E-\\u1770\\u1780-\\u17B3\\u17D7]|[\\u17DC\\u1820-\\u1877\\u1880-\\u18A8\\u18AA\\u18B0-\\u18F5\\u1900-\\u191C]|[\\u1950-\\u196D\\u1970-\\u1974\\u1980-\\u19AB\\u19C1-\\u19C7\\u1A00-\\u1A16]|[\\u1A20-\\u1A54\\u1AA7\\u1B05-\\u1B33\\u1B45-\\u1B4B\\u1B83-\\u1BA0\\u1BAE\\u1BAF]|[\\u1BBA-\\u1BE5\\u1C00-\\u1C23\\u1C4D-\\u1C4F\\u1C5A-\\u1C7D\\u1CE9-\\u1CEC]|[\\u1CEE-\\u1CF1\\u1CF5\\u1CF6\\u1D00-\\u1DBF\\u1E00-\\u1F15\\u1F18-\\u1F1D]|[\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D]|[\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3]|[\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u2071\\u207F]|[\\u2090-\\u209C\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128]|[\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2183\\u2184]|[\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2CE4\\u2CEB-\\u2CEE\\u2CF2\\u2CF3]|[\\u2D00-\\u2D25\\u2D27\\u2D2D\\u2D30-\\u2D67\\u2D6F\\u2D80-\\u2D96\\u2DA0-\\u2DA6]|[\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE]|[\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2E2F\\u3005\\u3006\\u3031-\\u3035\\u303B\\u303C]|[\\u3041-\\u3096\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312D]|[\\u3131-\\u318E\\u31A0-\\u31BA\\u31F0-\\u31FF\\u3400-\\u4DB5\\u4E00-\\u9FCC]|[\\uA000-\\uA48C\\uA4D0-\\uA4FD\\uA500-\\uA60C\\uA610-\\uA61F\\uA62A\\uA62B]|[\\uA640-\\uA66E\\uA67F-\\uA697\\uA6A0-\\uA6E5\\uA717-\\uA71F\\uA722-\\uA788]|[\\uA78B-\\uA78E\\uA790-\\uA793\\uA7A0-\\uA7AA\\uA7F8-\\uA801\\uA803-\\uA805]|[\\uA807-\\uA80A\\uA80C-\\uA822\\uA840-\\uA873\\uA882-\\uA8B3\\uA8F2-\\uA8F7\\uA8FB]|[\\uA90A-\\uA925\\uA930-\\uA946\\uA960-\\uA97C\\uA984-\\uA9B2\\uA9CF\\uAA00-\\uAA28]|[\\uAA40-\\uAA42\\uAA44-\\uAA4B\\uAA60-\\uAA76\\uAA7A\\uAA80-\\uAAAF\\uAAB1\\uAAB5]|[\\uAAB6\\uAAB9-\\uAABD\\uAAC0\\uAAC2\\uAADB-\\uAADD\\uAAE0-\\uAAEA\\uAAF2-\\uAAF4]|[\\uAB01-\\uAB06\\uAB09-\\uAB0E\\uAB11-\\uAB16\\uAB20-\\uAB26\\uAB28-\\uAB2E]|[\\uABC0-\\uABE2\\uAC00-\\uD7A3\\uD7B0-\\uD7C6\\uD7CB-\\uD7FB\\uF900-\\uFA6D]|[\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D\\uFB1F-\\uFB28\\uFB2A-\\uFB36]|[\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D]|[\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE70-\\uFE74\\uFE76-\\uFEFC]|[\\uFF21-\\uFF3A\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF]|[\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC])"),
        ClassJisonLexerPattern(96, "^(?:\\s)"),
        ClassJisonLexerPattern(97, "^(?:\\s)"),
        ClassJisonLexerPattern(98, "^(?:\$)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "namespace-body" to intArrayOf(26, 29, 34, 35, 36, 37, 38, 39, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "namespace" to intArrayOf(26, 29, 30, 31, 32, 33, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "class-body" to intArrayOf(26, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "class" to intArrayOf(26, 40, 41, 42, 43, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "acc_descr_multiline" to intArrayOf(11, 12, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "acc_descr" to intArrayOf(9, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "acc_title" to intArrayOf(7, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "callback_args" to intArrayOf(22, 23, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "callback_name" to intArrayOf(19, 20, 21, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "href" to intArrayOf(26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "struct" to intArrayOf(26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "generic" to intArrayOf(26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "bqstring" to intArrayOf(26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "string" to intArrayOf(24, 25, 26, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 98),
        "INITIAL" to intArrayOf(0, 1, 2, 3, 4, 5, 6, 8, 10, 13, 14, 15, 16, 17, 18, 26, 27, 28, 29, 39, 50, 51, 52, 53, 54, 55, 56, 57, 58, 61, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98),
    )
}
