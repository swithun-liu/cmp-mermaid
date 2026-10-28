package com.swithun.cmpmermaid.core.erdiagram.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 erDiagram.jison.
 * Upstream erDiagram.jison SHA-256: 4f7c96b05d15bcc9c0e753bb106a5b8a67cfa4c8b2e13faab1d7098b92f9e5be
 * Distribution chunk SHA-256: 1437bfbd601358cd7f2e54d540410bdebc9bdd38131300d16c49705811f9de49
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:er-parser
 */
internal object ErJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "ER_DIAGRAM" to 4,
        "document" to 5,
        "EOF" to 6,
        "line" to 7,
        "statement" to 8,
        "NEWLINE" to 9,
        "entityName" to 10,
        "relSpec" to 11,
        "COLON" to 12,
        "role" to 13,
        "STYLE_SEPARATOR" to 14,
        "idList" to 15,
        "BLOCK_START" to 16,
        "attributes" to 17,
        "BLOCK_STOP" to 18,
        "SQS" to 19,
        "SQE" to 20,
        "title" to 21,
        "title_value" to 22,
        "acc_title" to 23,
        "acc_title_value" to 24,
        "acc_descr" to 25,
        "acc_descr_value" to 26,
        "acc_descr_multiline_value" to 27,
        "direction" to 28,
        "classDefStatement" to 29,
        "classStatement" to 30,
        "styleStatement" to 31,
        "subgraphHeader" to 32,
        "END" to 33,
        "SUBGRAPH" to 34,
        "separator" to 35,
        "subgraphTitle" to 36,
        "direction_tb" to 37,
        "direction_bt" to 38,
        "direction_rl" to 39,
        "direction_lr" to 40,
        "CLASSDEF" to 41,
        "stylesOpt" to 42,
        "UNICODE_TEXT" to 43,
        "STYLE_TEXT" to 44,
        "COMMA" to 45,
        "CLASS" to 46,
        "STYLE" to 47,
        "style" to 48,
        "styleComponent" to 49,
        "SEMI" to 50,
        "NUM" to 51,
        "BRKT" to 52,
        "ENTITY_NAME" to 53,
        "DECIMAL_NUM" to 54,
        "ENTITY_ONE" to 55,
        "attribute" to 56,
        "attributeType" to 57,
        "attributeName" to 58,
        "attributeKeyTypeList" to 59,
        "attributeComment" to 60,
        "ATTRIBUTE_WORD" to 61,
        "?" to 62,
        "attributeKeyType" to 63,
        "," to 64,
        "ATTRIBUTE_KEY" to 65,
        "COMMENT" to 66,
        "cardinality" to 67,
        "relType" to 68,
        "ZERO_OR_ONE" to 69,
        "ZERO_OR_MORE" to 70,
        "ONE_OR_MORE" to 71,
        "ONLY_ONE" to 72,
        "MD_PARENT" to 73,
        "NON_IDENTIFYING" to 74,
        "IDENTIFYING" to 75,
        "WORD" to 76,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "ER_DIAGRAM",
        6 to "EOF",
        9 to "NEWLINE",
        12 to "COLON",
        14 to "STYLE_SEPARATOR",
        16 to "BLOCK_START",
        18 to "BLOCK_STOP",
        19 to "SQS",
        20 to "SQE",
        21 to "title",
        22 to "title_value",
        23 to "acc_title",
        24 to "acc_title_value",
        25 to "acc_descr",
        26 to "acc_descr_value",
        27 to "acc_descr_multiline_value",
        33 to "END",
        34 to "SUBGRAPH",
        37 to "direction_tb",
        38 to "direction_bt",
        39 to "direction_rl",
        40 to "direction_lr",
        41 to "CLASSDEF",
        43 to "UNICODE_TEXT",
        44 to "STYLE_TEXT",
        45 to "COMMA",
        46 to "CLASS",
        47 to "STYLE",
        50 to "SEMI",
        51 to "NUM",
        52 to "BRKT",
        53 to "ENTITY_NAME",
        54 to "DECIMAL_NUM",
        55 to "ENTITY_ONE",
        61 to "ATTRIBUTE_WORD",
        62 to "?",
        64 to ",",
        65 to "ATTRIBUTE_KEY",
        66 to "COMMENT",
        69 to "ZERO_OR_ONE",
        70 to "ZERO_OR_MORE",
        71 to "ONE_OR_MORE",
        72 to "ONLY_ONE",
        73 to "MD_PARENT",
        74 to "NON_IDENTIFYING",
        75 to "IDENTIFYING",
        76 to "WORD",
    )

    val productions: Array<ErJisonProduction> = arrayOf(
        ErJisonProduction(symbol = 0, length = 0),
        ErJisonProduction(symbol = 3, length = 3),
        ErJisonProduction(symbol = 5, length = 0),
        ErJisonProduction(symbol = 5, length = 2),
        ErJisonProduction(symbol = 7, length = 1),
        ErJisonProduction(symbol = 7, length = 1),
        ErJisonProduction(symbol = 7, length = 1),
        ErJisonProduction(symbol = 8, length = 5),
        ErJisonProduction(symbol = 8, length = 9),
        ErJisonProduction(symbol = 8, length = 7),
        ErJisonProduction(symbol = 8, length = 7),
        ErJisonProduction(symbol = 8, length = 4),
        ErJisonProduction(symbol = 8, length = 6),
        ErJisonProduction(symbol = 8, length = 3),
        ErJisonProduction(symbol = 8, length = 5),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 3),
        ErJisonProduction(symbol = 8, length = 7),
        ErJisonProduction(symbol = 8, length = 9),
        ErJisonProduction(symbol = 8, length = 6),
        ErJisonProduction(symbol = 8, length = 8),
        ErJisonProduction(symbol = 8, length = 4),
        ErJisonProduction(symbol = 8, length = 6),
        ErJisonProduction(symbol = 8, length = 2),
        ErJisonProduction(symbol = 8, length = 2),
        ErJisonProduction(symbol = 8, length = 2),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 1),
        ErJisonProduction(symbol = 8, length = 3),
        ErJisonProduction(symbol = 32, length = 3),
        ErJisonProduction(symbol = 32, length = 6),
        ErJisonProduction(symbol = 36, length = 1),
        ErJisonProduction(symbol = 36, length = 2),
        ErJisonProduction(symbol = 28, length = 1),
        ErJisonProduction(symbol = 28, length = 1),
        ErJisonProduction(symbol = 28, length = 1),
        ErJisonProduction(symbol = 28, length = 1),
        ErJisonProduction(symbol = 29, length = 4),
        ErJisonProduction(symbol = 15, length = 1),
        ErJisonProduction(symbol = 15, length = 1),
        ErJisonProduction(symbol = 15, length = 3),
        ErJisonProduction(symbol = 15, length = 3),
        ErJisonProduction(symbol = 30, length = 3),
        ErJisonProduction(symbol = 31, length = 4),
        ErJisonProduction(symbol = 42, length = 1),
        ErJisonProduction(symbol = 42, length = 3),
        ErJisonProduction(symbol = 48, length = 1),
        ErJisonProduction(symbol = 48, length = 2),
        ErJisonProduction(symbol = 35, length = 1),
        ErJisonProduction(symbol = 35, length = 1),
        ErJisonProduction(symbol = 35, length = 1),
        ErJisonProduction(symbol = 49, length = 1),
        ErJisonProduction(symbol = 49, length = 1),
        ErJisonProduction(symbol = 49, length = 1),
        ErJisonProduction(symbol = 49, length = 1),
        ErJisonProduction(symbol = 10, length = 1),
        ErJisonProduction(symbol = 10, length = 1),
        ErJisonProduction(symbol = 10, length = 1),
        ErJisonProduction(symbol = 10, length = 1),
        ErJisonProduction(symbol = 10, length = 1),
        ErJisonProduction(symbol = 17, length = 1),
        ErJisonProduction(symbol = 17, length = 2),
        ErJisonProduction(symbol = 56, length = 2),
        ErJisonProduction(symbol = 56, length = 3),
        ErJisonProduction(symbol = 56, length = 3),
        ErJisonProduction(symbol = 56, length = 4),
        ErJisonProduction(symbol = 57, length = 1),
        ErJisonProduction(symbol = 57, length = 2),
        ErJisonProduction(symbol = 58, length = 1),
        ErJisonProduction(symbol = 59, length = 1),
        ErJisonProduction(symbol = 59, length = 3),
        ErJisonProduction(symbol = 63, length = 1),
        ErJisonProduction(symbol = 60, length = 1),
        ErJisonProduction(symbol = 11, length = 3),
        ErJisonProduction(symbol = 67, length = 1),
        ErJisonProduction(symbol = 67, length = 1),
        ErJisonProduction(symbol = 67, length = 1),
        ErJisonProduction(symbol = 67, length = 1),
        ErJisonProduction(symbol = 67, length = 1),
        ErJisonProduction(symbol = 68, length = 1),
        ErJisonProduction(symbol = 68, length = 1),
        ErJisonProduction(symbol = 13, length = 1),
        ErJisonProduction(symbol = 13, length = 1),
        ErJisonProduction(symbol = 13, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: ErJisonCell = ErJisonCell.Goto(1)
    private val action1: ErJisonCell = ErJisonCell.Shift(2)
    private val action2: ErJisonCell = ErJisonCell.Accept
    private val action3: ErJisonCell = ErJisonCell.Goto(3)
    private val action4: ErJisonCell = ErJisonCell.Reduce(2)
    private val action5: ErJisonCell = ErJisonCell.Shift(4)
    private val action6: ErJisonCell = ErJisonCell.Goto(5)
    private val action7: ErJisonCell = ErJisonCell.Goto(6)
    private val action8: ErJisonCell = ErJisonCell.Shift(7)
    private val action9: ErJisonCell = ErJisonCell.Goto(8)
    private val action10: ErJisonCell = ErJisonCell.Shift(9)
    private val action11: ErJisonCell = ErJisonCell.Shift(10)
    private val action12: ErJisonCell = ErJisonCell.Shift(11)
    private val action13: ErJisonCell = ErJisonCell.Shift(12)
    private val action14: ErJisonCell = ErJisonCell.Goto(13)
    private val action15: ErJisonCell = ErJisonCell.Goto(14)
    private val action16: ErJisonCell = ErJisonCell.Goto(15)
    private val action17: ErJisonCell = ErJisonCell.Goto(16)
    private val action18: ErJisonCell = ErJisonCell.Goto(17)
    private val action19: ErJisonCell = ErJisonCell.Shift(30)
    private val action20: ErJisonCell = ErJisonCell.Shift(23)
    private val action21: ErJisonCell = ErJisonCell.Shift(24)
    private val action22: ErJisonCell = ErJisonCell.Shift(25)
    private val action23: ErJisonCell = ErJisonCell.Shift(26)
    private val action24: ErJisonCell = ErJisonCell.Shift(27)
    private val action25: ErJisonCell = ErJisonCell.Shift(19)
    private val action26: ErJisonCell = ErJisonCell.Shift(28)
    private val action27: ErJisonCell = ErJisonCell.Shift(29)
    private val action28: ErJisonCell = ErJisonCell.Shift(20)
    private val action29: ErJisonCell = ErJisonCell.Shift(18)
    private val action30: ErJisonCell = ErJisonCell.Shift(21)
    private val action31: ErJisonCell = ErJisonCell.Shift(22)
    private val action32: ErJisonCell = ErJisonCell.Reduce(1)
    private val action33: ErJisonCell = ErJisonCell.Reduce(6)
    private val action34: ErJisonCell = ErJisonCell.Reduce(3)
    private val action35: ErJisonCell = ErJisonCell.Reduce(4)
    private val action36: ErJisonCell = ErJisonCell.Reduce(5)
    private val action37: ErJisonCell = ErJisonCell.Reduce(15)
    private val action38: ErJisonCell = ErJisonCell.Goto(31)
    private val action39: ErJisonCell = ErJisonCell.Shift(32)
    private val action40: ErJisonCell = ErJisonCell.Shift(33)
    private val action41: ErJisonCell = ErJisonCell.Shift(34)
    private val action42: ErJisonCell = ErJisonCell.Goto(35)
    private val action43: ErJisonCell = ErJisonCell.Shift(36)
    private val action44: ErJisonCell = ErJisonCell.Shift(37)
    private val action45: ErJisonCell = ErJisonCell.Shift(38)
    private val action46: ErJisonCell = ErJisonCell.Shift(39)
    private val action47: ErJisonCell = ErJisonCell.Shift(40)
    private val action48: ErJisonCell = ErJisonCell.Shift(41)
    private val action49: ErJisonCell = ErJisonCell.Shift(42)
    private val action50: ErJisonCell = ErJisonCell.Shift(43)
    private val action51: ErJisonCell = ErJisonCell.Reduce(26)
    private val action52: ErJisonCell = ErJisonCell.Reduce(27)
    private val action53: ErJisonCell = ErJisonCell.Reduce(28)
    private val action54: ErJisonCell = ErJisonCell.Reduce(29)
    private val action55: ErJisonCell = ErJisonCell.Reduce(30)
    private val action56: ErJisonCell = ErJisonCell.Goto(44)
    private val action57: ErJisonCell = ErJisonCell.Reduce(58)
    private val action58: ErJisonCell = ErJisonCell.Reduce(59)
    private val action59: ErJisonCell = ErJisonCell.Reduce(60)
    private val action60: ErJisonCell = ErJisonCell.Reduce(61)
    private val action61: ErJisonCell = ErJisonCell.Reduce(62)
    private val action62: ErJisonCell = ErJisonCell.Reduce(36)
    private val action63: ErJisonCell = ErJisonCell.Reduce(37)
    private val action64: ErJisonCell = ErJisonCell.Reduce(38)
    private val action65: ErJisonCell = ErJisonCell.Reduce(39)
    private val action66: ErJisonCell = ErJisonCell.Goto(45)
    private val action67: ErJisonCell = ErJisonCell.Shift(46)
    private val action68: ErJisonCell = ErJisonCell.Shift(47)
    private val action69: ErJisonCell = ErJisonCell.Goto(48)
    private val action70: ErJisonCell = ErJisonCell.Goto(49)
    private val action71: ErJisonCell = ErJisonCell.Goto(50)
    private val action72: ErJisonCell = ErJisonCell.Goto(51)
    private val action73: ErJisonCell = ErJisonCell.Goto(52)
    private val action74: ErJisonCell = ErJisonCell.Goto(53)
    private val action75: ErJisonCell = ErJisonCell.Shift(54)
    private val action76: ErJisonCell = ErJisonCell.Goto(55)
    private val action77: ErJisonCell = ErJisonCell.Goto(56)
    private val action78: ErJisonCell = ErJisonCell.Shift(57)
    private val action79: ErJisonCell = ErJisonCell.Goto(58)
    private val action80: ErJisonCell = ErJisonCell.Goto(59)
    private val action81: ErJisonCell = ErJisonCell.Shift(60)
    private val action82: ErJisonCell = ErJisonCell.Shift(61)
    private val action83: ErJisonCell = ErJisonCell.Reduce(77)
    private val action84: ErJisonCell = ErJisonCell.Reduce(78)
    private val action85: ErJisonCell = ErJisonCell.Reduce(79)
    private val action86: ErJisonCell = ErJisonCell.Reduce(80)
    private val action87: ErJisonCell = ErJisonCell.Reduce(81)
    private val action88: ErJisonCell = ErJisonCell.Reduce(23)
    private val action89: ErJisonCell = ErJisonCell.Reduce(24)
    private val action90: ErJisonCell = ErJisonCell.Reduce(25)
    private val action91: ErJisonCell = ErJisonCell.Shift(63)
    private val action92: ErJisonCell = ErJisonCell.Shift(62)
    private val action93: ErJisonCell = ErJisonCell.Shift(70)
    private val action94: ErJisonCell = ErJisonCell.Goto(64)
    private val action95: ErJisonCell = ErJisonCell.Shift(68)
    private val action96: ErJisonCell = ErJisonCell.Shift(65)
    private val action97: ErJisonCell = ErJisonCell.Goto(66)
    private val action98: ErJisonCell = ErJisonCell.Goto(67)
    private val action99: ErJisonCell = ErJisonCell.Shift(69)
    private val action100: ErJisonCell = ErJisonCell.Shift(71)
    private val action101: ErJisonCell = ErJisonCell.Reduce(41)
    private val action102: ErJisonCell = ErJisonCell.Reduce(42)
    private val action103: ErJisonCell = ErJisonCell.Goto(72)
    private val action104: ErJisonCell = ErJisonCell.Goto(73)
    private val action105: ErJisonCell = ErJisonCell.Shift(78)
    private val action106: ErJisonCell = ErJisonCell.Shift(77)
    private val action107: ErJisonCell = ErJisonCell.Shift(75)
    private val action108: ErJisonCell = ErJisonCell.Goto(74)
    private val action109: ErJisonCell = ErJisonCell.Shift(76)
    private val action110: ErJisonCell = ErJisonCell.Shift(79)
    private val action111: ErJisonCell = ErJisonCell.Shift(80)
    private val action112: ErJisonCell = ErJisonCell.Reduce(16)
    private val action113: ErJisonCell = ErJisonCell.Goto(81)
    private val action114: ErJisonCell = ErJisonCell.Shift(82)
    private val action115: ErJisonCell = ErJisonCell.Shift(83)
    private val action116: ErJisonCell = ErJisonCell.Reduce(13)
    private val action117: ErJisonCell = ErJisonCell.Goto(84)
    private val action118: ErJisonCell = ErJisonCell.Reduce(63)
    private val action119: ErJisonCell = ErJisonCell.Goto(85)
    private val action120: ErJisonCell = ErJisonCell.Shift(86)
    private val action121: ErJisonCell = ErJisonCell.Reduce(69)
    private val action122: ErJisonCell = ErJisonCell.Shift(87)
    private val action123: ErJisonCell = ErJisonCell.Shift(88)
    private val action124: ErJisonCell = ErJisonCell.Goto(89)
    private val action125: ErJisonCell = ErJisonCell.Reduce(82)
    private val action126: ErJisonCell = ErJisonCell.Reduce(83)
    private val action127: ErJisonCell = ErJisonCell.Reduce(31)
    private val action128: ErJisonCell = ErJisonCell.Goto(90)
    private val action129: ErJisonCell = ErJisonCell.Shift(91)
    private val action130: ErJisonCell = ErJisonCell.Shift(92)
    private val action131: ErJisonCell = ErJisonCell.Shift(93)
    private val action132: ErJisonCell = ErJisonCell.Reduce(47)
    private val action133: ErJisonCell = ErJisonCell.Goto(94)
    private val action134: ErJisonCell = ErJisonCell.Reduce(49)
    private val action135: ErJisonCell = ErJisonCell.Reduce(54)
    private val action136: ErJisonCell = ErJisonCell.Reduce(55)
    private val action137: ErJisonCell = ErJisonCell.Reduce(56)
    private val action138: ErJisonCell = ErJisonCell.Reduce(57)
    private val action139: ErJisonCell = ErJisonCell.Reduce(45)
    private val action140: ErJisonCell = ErJisonCell.Goto(95)
    private val action141: ErJisonCell = ErJisonCell.Reduce(32)
    private val action142: ErJisonCell = ErJisonCell.Goto(97)
    private val action143: ErJisonCell = ErJisonCell.Goto(96)
    private val action144: ErJisonCell = ErJisonCell.Reduce(51)
    private val action145: ErJisonCell = ErJisonCell.Reduce(52)
    private val action146: ErJisonCell = ErJisonCell.Reduce(53)
    private val action147: ErJisonCell = ErJisonCell.Goto(98)
    private val action148: ErJisonCell = ErJisonCell.Shift(101)
    private val action149: ErJisonCell = ErJisonCell.Shift(100)
    private val action150: ErJisonCell = ErJisonCell.Shift(99)
    private val action151: ErJisonCell = ErJisonCell.Goto(102)
    private val action152: ErJisonCell = ErJisonCell.Goto(103)
    private val action153: ErJisonCell = ErJisonCell.Goto(104)
    private val action154: ErJisonCell = ErJisonCell.Shift(105)
    private val action155: ErJisonCell = ErJisonCell.Reduce(11)
    private val action156: ErJisonCell = ErJisonCell.Reduce(64)
    private val action157: ErJisonCell = ErJisonCell.Reduce(65)
    private val action158: ErJisonCell = ErJisonCell.Goto(106)
    private val action159: ErJisonCell = ErJisonCell.Goto(107)
    private val action160: ErJisonCell = ErJisonCell.Goto(108)
    private val action161: ErJisonCell = ErJisonCell.Shift(110)
    private val action162: ErJisonCell = ErJisonCell.Shift(109)
    private val action163: ErJisonCell = ErJisonCell.Reduce(71)
    private val action164: ErJisonCell = ErJisonCell.Reduce(70)
    private val action165: ErJisonCell = ErJisonCell.Reduce(21)
    private val action166: ErJisonCell = ErJisonCell.Shift(112)
    private val action167: ErJisonCell = ErJisonCell.Shift(111)
    private val action168: ErJisonCell = ErJisonCell.Reduce(76)
    private val action169: ErJisonCell = ErJisonCell.Reduce(40)
    private val action170: ErJisonCell = ErJisonCell.Goto(113)
    private val action171: ErJisonCell = ErJisonCell.Reduce(43)
    private val action172: ErJisonCell = ErJisonCell.Reduce(44)
    private val action173: ErJisonCell = ErJisonCell.Reduce(50)
    private val action174: ErJisonCell = ErJisonCell.Reduce(46)
    private val action175: ErJisonCell = ErJisonCell.Goto(115)
    private val action176: ErJisonCell = ErJisonCell.Shift(114)
    private val action177: ErJisonCell = ErJisonCell.Reduce(34)
    private val action178: ErJisonCell = ErJisonCell.Reduce(7)
    private val action179: ErJisonCell = ErJisonCell.Reduce(84)
    private val action180: ErJisonCell = ErJisonCell.Reduce(85)
    private val action181: ErJisonCell = ErJisonCell.Reduce(86)
    private val action182: ErJisonCell = ErJisonCell.Shift(116)
    private val action183: ErJisonCell = ErJisonCell.Shift(118)
    private val action184: ErJisonCell = ErJisonCell.Shift(117)
    private val action185: ErJisonCell = ErJisonCell.Shift(119)
    private val action186: ErJisonCell = ErJisonCell.Reduce(14)
    private val action187: ErJisonCell = ErJisonCell.Reduce(66)
    private val action188: ErJisonCell = ErJisonCell.Goto(120)
    private val action189: ErJisonCell = ErJisonCell.Shift(121)
    private val action190: ErJisonCell = ErJisonCell.Reduce(67)
    private val action191: ErJisonCell = ErJisonCell.Reduce(72)
    private val action192: ErJisonCell = ErJisonCell.Reduce(75)
    private val action193: ErJisonCell = ErJisonCell.Reduce(74)
    private val action194: ErJisonCell = ErJisonCell.Goto(122)
    private val action195: ErJisonCell = ErJisonCell.Shift(123)
    private val action196: ErJisonCell = ErJisonCell.Goto(124)
    private val action197: ErJisonCell = ErJisonCell.Reduce(48)
    private val action198: ErJisonCell = ErJisonCell.Goto(125)
    private val action199: ErJisonCell = ErJisonCell.Reduce(35)
    private val action200: ErJisonCell = ErJisonCell.Goto(126)
    private val action201: ErJisonCell = ErJisonCell.Goto(127)
    private val action202: ErJisonCell = ErJisonCell.Goto(128)
    private val action203: ErJisonCell = ErJisonCell.Reduce(12)
    private val action204: ErJisonCell = ErJisonCell.Reduce(68)
    private val action205: ErJisonCell = ErJisonCell.Goto(129)
    private val action206: ErJisonCell = ErJisonCell.Shift(130)
    private val action207: ErJisonCell = ErJisonCell.Reduce(19)
    private val action208: ErJisonCell = ErJisonCell.Reduce(22)
    private val action209: ErJisonCell = ErJisonCell.Shift(131)
    private val action210: ErJisonCell = ErJisonCell.Reduce(33)
    private val action211: ErJisonCell = ErJisonCell.Reduce(10)
    private val action212: ErJisonCell = ErJisonCell.Shift(132)
    private val action213: ErJisonCell = ErJisonCell.Reduce(9)
    private val action214: ErJisonCell = ErJisonCell.Reduce(73)
    private val action215: ErJisonCell = ErJisonCell.Reduce(17)
    private val action216: ErJisonCell = ErJisonCell.Goto(133)
    private val action217: ErJisonCell = ErJisonCell.Shift(134)
    private val action218: ErJisonCell = ErJisonCell.Goto(135)
    private val action219: ErJisonCell = ErJisonCell.Shift(136)
    private val action220: ErJisonCell = ErJisonCell.Reduce(20)
    private val action221: ErJisonCell = ErJisonCell.Reduce(8)
    private val action222: ErJisonCell = ErJisonCell.Reduce(18)
    private val symbols0 = intArrayOf(5, 6, 9, 21, 23, 25, 27, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55)
    private val symbols1 = intArrayOf(1, 6, 9, 21, 23, 25, 27, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55)
    private val symbols2 = intArrayOf(6, 9, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55)
    private val symbols3 = intArrayOf(6, 9, 11, 14, 16, 19, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55, 67, 69, 70, 71, 72, 73)
    private val symbols4 = intArrayOf(5, 6, 9, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55)
    private val symbols5 = intArrayOf(6, 9, 12, 14, 16, 19, 20, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 46, 47, 50, 51, 53, 54, 55, 69, 70, 71, 72, 73)
    private val symbols6 = intArrayOf(43, 51, 53, 54, 55, 74, 75)
    private val symbols7 = intArrayOf(6, 9, 12, 16, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 51, 52, 53, 54, 55, 69, 70, 71, 72, 73)
    private val symbols8 = intArrayOf(6, 9, 11, 16, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 45, 46, 47, 51, 53, 54, 55, 67, 69, 70, 71, 72, 73)
    private val symbols9 = intArrayOf(69, 70, 71, 72, 73)
    private val symbols10 = intArrayOf(6, 9, 12, 44, 45, 49, 50, 51, 52)
    private val symbols11 = intArrayOf(6, 9, 12, 44, 45, 50, 51, 52)
    private val symbols12 = intArrayOf(6, 9, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 45, 46, 47, 51, 53, 54, 55)
    private val symbols13 = intArrayOf(18, 61, 65, 66)
    private val symbols14 = intArrayOf(6, 9, 14, 16, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 46, 47, 51, 53, 54, 55)
    private val symbols15 = intArrayOf(43, 51, 53, 54, 55)
    private val symbols16 = intArrayOf(20, 43, 51, 53, 54, 55)
    private val symbols17 = intArrayOf(18, 61, 64, 66)
    private val symbols18 = intArrayOf(6, 9, 16, 21, 23, 25, 27, 33, 34, 37, 38, 39, 40, 41, 43, 45, 46, 47, 51, 53, 54, 55)

    val states: Array<Map<Int, ErJisonCell>> = buildList {
        addAll(stateChunk0())
        addAll(stateChunk1())
        addAll(stateChunk2())
        addAll(stateChunk3())
        addAll(stateChunk4())
        addAll(stateChunk5())
        addAll(stateChunk6())
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, ErJisonCell>> = arrayOf(
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
            21 to action10,
            23 to action11,
            25 to action12,
            27 to action13,
            28 to action14,
            29 to action15,
            30 to action16,
            31 to action17,
            32 to action18,
            34 to action19,
            37 to action20,
            38 to action21,
            39 to action22,
            40 to action23,
            41 to action24,
            43 to action25,
            46 to action26,
            47 to action27,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        buildJisonState(symbols1, action33, mapOf(
            1 to action32,
        )),
        buildJisonState(symbols2, action34),
        buildJisonState(symbols2, action35),
        buildJisonState(symbols2, action36),
        buildJisonState(symbols3, action37, mapOf(
            11 to action38,
            14 to action39,
            16 to action40,
            19 to action41,
            67 to action42,
            69 to action43,
            70 to action44,
            71 to action45,
            72 to action46,
            73 to action47,
        )),
        mapOf(
            22 to action48,
        ),
        mapOf(
            24 to action49,
        ),
        mapOf(
            26 to action50,
        ),
        buildJisonState(symbols2, action51),
        buildJisonState(symbols2, action52),
        buildJisonState(symbols2, action53),
        buildJisonState(symbols2, action54),
        buildJisonState(symbols2, action55),
        buildJisonState(symbols4, action4, mapOf(
            5 to action56,
        )),
        buildJisonState(symbols5, action57),
        buildJisonState(symbols5, action58),
    )

    private fun stateChunk1(): Array<Map<Int, ErJisonCell>> = arrayOf(
        buildJisonState(symbols5, action59),
        buildJisonState(symbols5, action60),
        buildJisonState(symbols5, action61),
        buildJisonState(symbols2, action62),
        buildJisonState(symbols2, action63),
        buildJisonState(symbols2, action64),
        buildJisonState(symbols2, action65),
        mapOf(
            15 to action66,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            15 to action69,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            15 to action70,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            10 to action71,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        mapOf(
            10 to action72,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        mapOf(
            15 to action73,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            17 to action74,
            18 to action75,
            56 to action76,
            57 to action77,
            61 to action78,
        ),
        mapOf(
            10 to action79,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        mapOf(
            68 to action80,
            74 to action81,
            75 to action82,
        ),
        buildJisonState(symbols6, action83),
        buildJisonState(symbols6, action84),
        buildJisonState(symbols6, action85),
        buildJisonState(symbols6, action86),
    )

    private fun stateChunk2(): Array<Map<Int, ErJisonCell>> = arrayOf(
        buildJisonState(symbols6, action87),
        buildJisonState(symbols2, action88),
        buildJisonState(symbols2, action89),
        buildJisonState(symbols2, action90),
        mapOf(
            6 to action91,
            7 to action6,
            8 to action7,
            9 to action8,
            10 to action9,
            21 to action10,
            23 to action11,
            25 to action12,
            27 to action13,
            28 to action14,
            29 to action15,
            30 to action16,
            31 to action17,
            32 to action18,
            33 to action92,
            34 to action19,
            37 to action20,
            38 to action21,
            39 to action22,
            40 to action23,
            41 to action24,
            43 to action25,
            46 to action26,
            47 to action27,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        mapOf(
            12 to action93,
            42 to action94,
            44 to action95,
            45 to action96,
            48 to action97,
            49 to action98,
            51 to action99,
            52 to action100,
        ),
        buildJisonState(symbols7, action101),
        buildJisonState(symbols7, action102),
        mapOf(
            15 to action103,
            43 to action67,
            44 to action68,
            45 to action96,
        ),
        mapOf(
            12 to action93,
            42 to action104,
            44 to action95,
            45 to action96,
            48 to action97,
            49 to action98,
            51 to action99,
            52 to action100,
        ),
        mapOf(
            6 to action105,
            9 to action106,
            19 to action107,
            35 to action108,
            50 to action109,
        ),
        mapOf(
            12 to action110,
            14 to action111,
        ),
        buildJisonState(symbols8, action112, mapOf(
            11 to action113,
            16 to action114,
            45 to action96,
            67 to action42,
            69 to action43,
            70 to action44,
            71 to action45,
            72 to action46,
            73 to action47,
        )),
        mapOf(
            18 to action115,
        ),
        buildJisonState(symbols2, action116),
        mapOf(
            17 to action117,
            18 to action118,
            56 to action76,
            57 to action77,
            61 to action78,
        ),
        mapOf(
            58 to action119,
            61 to action120,
        ),
        mapOf(
            61 to action121,
            62 to action122,
        ),
        mapOf(
            20 to action123,
        ),
        mapOf(
            67 to action124,
            69 to action43,
            70 to action44,
            71 to action45,
            72 to action46,
            73 to action47,
        ),
    )

    private fun stateChunk3(): Array<Map<Int, ErJisonCell>> = arrayOf(
        buildJisonState(symbols9, action125),
        buildJisonState(symbols9, action126),
        buildJisonState(symbols2, action127),
        buildJisonState(symbols2, action33),
        mapOf(
            6 to action105,
            9 to action106,
            35 to action128,
            45 to action129,
            50 to action109,
        ),
        mapOf(
            43 to action130,
            44 to action131,
        ),
        buildJisonState(symbols10, action132, mapOf(
            12 to action93,
            44 to action95,
            49 to action133,
            51 to action99,
            52 to action100,
        )),
        buildJisonState(symbols11, action134),
        buildJisonState(symbols11, action135),
        buildJisonState(symbols11, action136),
        buildJisonState(symbols11, action137),
        buildJisonState(symbols11, action138),
        buildJisonState(symbols12, action139, mapOf(
            45 to action96,
        )),
        mapOf(
            6 to action105,
            9 to action106,
            35 to action140,
            45 to action129,
            50 to action109,
        ),
        buildJisonState(symbols2, action141),
        mapOf(
            10 to action142,
            36 to action143,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        buildJisonState(symbols2, action144),
        buildJisonState(symbols2, action145),
        buildJisonState(symbols2, action146),
        mapOf(
            13 to action147,
            43 to action148,
            53 to action149,
            76 to action150,
        ),
    )

    private fun stateChunk4(): Array<Map<Int, ErJisonCell>> = arrayOf(
        mapOf(
            15 to action151,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            10 to action152,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        mapOf(
            17 to action153,
            18 to action154,
            56 to action76,
            57 to action77,
            61 to action78,
        ),
        buildJisonState(symbols2, action155),
        mapOf(
            18 to action156,
        ),
        mapOf(
            18 to action157,
            59 to action158,
            60 to action159,
            61 to action157,
            63 to action160,
            65 to action161,
            66 to action162,
        ),
        buildJisonState(symbols13, action163),
        mapOf(
            61 to action164,
        ),
        buildJisonState(symbols14, action165, mapOf(
            14 to action166,
            16 to action167,
        )),
        buildJisonState(symbols15, action168),
        buildJisonState(symbols2, action169),
        mapOf(
            12 to action93,
            44 to action95,
            48 to action170,
            49 to action98,
            51 to action99,
            52 to action100,
        ),
        buildJisonState(symbols7, action171),
        buildJisonState(symbols7, action172),
        buildJisonState(symbols11, action173),
        buildJisonState(symbols2, action174),
        mapOf(
            10 to action175,
            20 to action176,
            43 to action25,
            51 to action28,
            53 to action29,
            54 to action30,
            55 to action31,
        ),
        buildJisonState(symbols16, action177),
        buildJisonState(symbols2, action178),
        buildJisonState(symbols2, action179),
    )

    private fun stateChunk5(): Array<Map<Int, ErJisonCell>> = arrayOf(
        buildJisonState(symbols2, action180),
        buildJisonState(symbols2, action181),
        mapOf(
            12 to action182,
            45 to action96,
        ),
        mapOf(
            12 to action183,
            14 to action184,
        ),
        mapOf(
            18 to action185,
        ),
        buildJisonState(symbols2, action186),
        mapOf(
            18 to action187,
            60 to action188,
            61 to action187,
            64 to action189,
            66 to action162,
        ),
        mapOf(
            18 to action190,
            61 to action190,
        ),
        buildJisonState(symbols17, action191),
        mapOf(
            18 to action192,
            61 to action192,
        ),
        buildJisonState(symbols17, action193),
        mapOf(
            17 to action194,
            18 to action195,
            56 to action76,
            57 to action77,
            61 to action78,
        ),
        mapOf(
            15 to action196,
            43 to action67,
            44 to action68,
        ),
        buildJisonState(symbols10, action197, mapOf(
            12 to action93,
            44 to action95,
            49 to action133,
            51 to action99,
            52 to action100,
        )),
        mapOf(
            6 to action105,
            9 to action106,
            35 to action198,
            50 to action109,
        ),
        buildJisonState(symbols16, action199),
        mapOf(
            13 to action200,
            43 to action148,
            53 to action149,
            76 to action150,
        ),
        mapOf(
            15 to action201,
            43 to action67,
            44 to action68,
        ),
        mapOf(
            13 to action202,
            43 to action148,
            53 to action149,
            76 to action150,
        ),
        buildJisonState(symbols2, action203),
    )

    private fun stateChunk6(): Array<Map<Int, ErJisonCell>> = arrayOf(
        mapOf(
            18 to action204,
            61 to action204,
        ),
        mapOf(
            63 to action205,
            65 to action161,
        ),
        mapOf(
            18 to action206,
        ),
        buildJisonState(symbols2, action207),
        buildJisonState(symbols18, action208, mapOf(
            16 to action209,
            45 to action96,
        )),
        buildJisonState(symbols2, action210),
        buildJisonState(symbols2, action211),
        mapOf(
            12 to action212,
            45 to action96,
        ),
        buildJisonState(symbols2, action213),
        buildJisonState(symbols17, action214),
        buildJisonState(symbols2, action215),
        mapOf(
            17 to action216,
            18 to action217,
            56 to action76,
            57 to action77,
            61 to action78,
        ),
        mapOf(
            13 to action218,
            43 to action148,
            53 to action149,
            76 to action150,
        ),
        mapOf(
            18 to action219,
        ),
        buildJisonState(symbols2, action220),
        buildJisonState(symbols2, action221),
        buildJisonState(symbols2, action222),
    )

    val lexerPatterns: List<ErJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
    }

    private fun lexerChunk0(): List<ErJisonLexerPattern> = listOf(
        ErJisonLexerPattern(0, "^(?:accTitle\\s*:\\s*)"),
        ErJisonLexerPattern(1, "^(?:(?!\\n||)*[^\\n]*)"),
        ErJisonLexerPattern(2, "^(?:accDescr\\s*:\\s*)"),
        ErJisonLexerPattern(3, "^(?:(?!\\n||)*[^\\n]*)"),
        ErJisonLexerPattern(4, "^(?:accDescr\\s*\\{\\s*)"),
        ErJisonLexerPattern(5, "^(?:[\\}])"),
        ErJisonLexerPattern(6, "^(?:[^\\}]*)"),
        ErJisonLexerPattern(7, "^(?:.*direction\\s+TB[^\\n]*)"),
        ErJisonLexerPattern(8, "^(?:.*direction\\s+BT[^\\n]*)"),
        ErJisonLexerPattern(9, "^(?:.*direction\\s+RL[^\\n]*)"),
        ErJisonLexerPattern(10, "^(?:.*direction\\s+LR[^\\n]*)"),
        ErJisonLexerPattern(11, "^(?:[ \\t\\r]+)"),
        ErJisonLexerPattern(12, "^(?:[\\n]+)"),
        ErJisonLexerPattern(13, "^(?:\"[^\"%\\r\\n\\v\\b\\\\]+\")"),
        ErJisonLexerPattern(14, "^(?:\"[^\"]*\")"),
        ErJisonLexerPattern(15, "^(?:erDiagram\\b)"),
        ErJisonLexerPattern(16, "^(?:\\{)"),
        ErJisonLexerPattern(17, "^(?:#)"),
        ErJisonLexerPattern(18, "^(?:#)"),
        ErJisonLexerPattern(19, "^(?:,)"),
    )

    private fun lexerChunk1(): List<ErJisonLexerPattern> = listOf(
        ErJisonLexerPattern(20, "^(?::::)"),
        ErJisonLexerPattern(21, "^(?::)"),
        ErJisonLexerPattern(22, "^(?:\\s+)"),
        ErJisonLexerPattern(23, "^(?:\\b((?:PK)|(?:FK)|(?:UK))\\b)"),
        ErJisonLexerPattern(24, "^(?:([^\\s]*)[~].*[~]([^\\s]*))"),
        ErJisonLexerPattern(25, "^(?:([\\*A-Za-z_\\u00C0-\\uFFFF][A-Za-z0-9\\-\\_\\[\\]\\(\\)\\.,\\u00C0-\\uFFFF\\*]*))"),
        ErJisonLexerPattern(26, "^(?:[`])"),
        ErJisonLexerPattern(27, "^(?:[^`]+)"),
        ErJisonLexerPattern(28, "^(?:[`])"),
        ErJisonLexerPattern(29, "^(?:\"[^\"]*\")"),
        ErJisonLexerPattern(30, "^(?:[\\n]+)"),
        ErJisonLexerPattern(31, "^(?:\\})"),
        ErJisonLexerPattern(32, "^(?:.)"),
        ErJisonLexerPattern(33, "^(?:\\[)"),
        ErJisonLexerPattern(34, "^(?:\\])"),
        ErJisonLexerPattern(35, "^(?:style\\b)"),
        ErJisonLexerPattern(36, "^(?:[\\n]+)"),
        ErJisonLexerPattern(37, "^(?:\\s+)"),
        ErJisonLexerPattern(38, "^(?::)"),
        ErJisonLexerPattern(39, "^(?:,)"),
    )

    private fun lexerChunk2(): List<ErJisonLexerPattern> = listOf(
        ErJisonLexerPattern(40, "^(?:#)"),
        ErJisonLexerPattern(41, "^(?:classDef\\b)"),
        ErJisonLexerPattern(42, "^(?:class\\b)"),
        ErJisonLexerPattern(43, "^(?:subgraph\\b)"),
        ErJisonLexerPattern(44, "^(?:end\\b\\s*)"),
        ErJisonLexerPattern(45, "^(?:one or zero\\b)"),
        ErJisonLexerPattern(46, "^(?:one or more\\b)"),
        ErJisonLexerPattern(47, "^(?:one or many\\b)"),
        ErJisonLexerPattern(48, "^(?:1\\+)"),
        ErJisonLexerPattern(49, "^(?:\\|o\\b)"),
        ErJisonLexerPattern(50, "^(?:zero or one\\b)"),
        ErJisonLexerPattern(51, "^(?:zero or more\\b)"),
        ErJisonLexerPattern(52, "^(?:zero or many\\b)"),
        ErJisonLexerPattern(53, "^(?:0\\+)"),
        ErJisonLexerPattern(54, "^(?:\\}o\\b)"),
        ErJisonLexerPattern(55, "^(?:many\\(0\\))"),
        ErJisonLexerPattern(56, "^(?:many\\(1\\))"),
        ErJisonLexerPattern(57, "^(?:many\\b)"),
        ErJisonLexerPattern(58, "^(?:\\}\\|)"),
        ErJisonLexerPattern(59, "^(?:one\\b)"),
    )

    private fun lexerChunk3(): List<ErJisonLexerPattern> = listOf(
        ErJisonLexerPattern(60, "^(?:only one\\b)"),
        ErJisonLexerPattern(61, "^(?:[0-9]+\\.[0-9]+)"),
        ErJisonLexerPattern(62, "^(?:1(?=\\s+[A-Za-z_\"']))"),
        ErJisonLexerPattern(63, "^(?:1(?=\\s+[0-9]))"),
        ErJisonLexerPattern(64, "^(?:1(?=(--|\\.\\.|\\.-|-\\.)))"),
        ErJisonLexerPattern(65, "^(?:1\\b)"),
        ErJisonLexerPattern(66, "^(?:[0-9]+)"),
        ErJisonLexerPattern(67, "^(?:\\|\\|)"),
        ErJisonLexerPattern(68, "^(?:o\\|)"),
        ErJisonLexerPattern(69, "^(?:o\\{)"),
        ErJisonLexerPattern(70, "^(?:\\|\\{)"),
        ErJisonLexerPattern(71, "^(?:u(?=[\\.\\-\\|]))"),
        ErJisonLexerPattern(72, "^(?:\\.\\.)"),
        ErJisonLexerPattern(73, "^(?:--)"),
        ErJisonLexerPattern(74, "^(?:to\\b)"),
        ErJisonLexerPattern(75, "^(?:optionally to\\b)"),
        ErJisonLexerPattern(76, "^(?:\\.-)"),
        ErJisonLexerPattern(77, "^(?:-\\.)"),
        ErJisonLexerPattern(78, "^(?:([^\\x00-\\x7F]|\\w|-|\\*)+)"),
        ErJisonLexerPattern(79, "^(?:;)"),
    )

    private fun lexerChunk4(): List<ErJisonLexerPattern> = listOf(
        ErJisonLexerPattern(80, "^(?:([^\\x00-\\x7F]|\\w|-|\\*|\\.)+)"),
        ErJisonLexerPattern(81, "^(?:.)"),
        ErJisonLexerPattern(82, "^(?:\$)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "style" to intArrayOf(36, 37, 38, 39, 40, 78, 79),
        "acc_descr_multiline" to intArrayOf(5, 6),
        "acc_descr" to intArrayOf(3),
        "acc_title" to intArrayOf(1),
        "block_bq" to intArrayOf(27, 28),
        "block" to intArrayOf(22, 23, 24, 25, 26, 29, 30, 31, 32),
        "INITIAL" to intArrayOf(0, 2, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 33, 34, 35, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 80, 81, 82),
    )
}
