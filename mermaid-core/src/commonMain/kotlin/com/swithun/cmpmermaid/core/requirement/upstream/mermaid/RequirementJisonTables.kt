package com.swithun.cmpmermaid.core.requirement.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 requirementDiagram.jison.
 * Upstream requirementDiagram.jison SHA-256: 32030ae34660128966d31ecc25e5ce224ce9bf947e1e31bd3961f3879ae26ae7
 * Distribution chunk SHA-256: 78077a43ffafbf1f752fe3ea7d7c78a0c7f9af6ad00ab3a619e9c494fc74cb0a
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:requirement-parser
 */
internal object RequirementJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "directive" to 4,
        "NEWLINE" to 5,
        "RD" to 6,
        "diagram" to 7,
        "EOF" to 8,
        "acc_title" to 9,
        "acc_title_value" to 10,
        "acc_descr" to 11,
        "acc_descr_value" to 12,
        "acc_descr_multiline_value" to 13,
        "requirementDef" to 14,
        "elementDef" to 15,
        "relationshipDef" to 16,
        "direction" to 17,
        "styleStatement" to 18,
        "classDefStatement" to 19,
        "classStatement" to 20,
        "direction_tb" to 21,
        "direction_bt" to 22,
        "direction_rl" to 23,
        "direction_lr" to 24,
        "requirementType" to 25,
        "requirementName" to 26,
        "STRUCT_START" to 27,
        "requirementBody" to 28,
        "STYLE_SEPARATOR" to 29,
        "idList" to 30,
        "ID" to 31,
        "COLONSEP" to 32,
        "id" to 33,
        "TEXT" to 34,
        "text" to 35,
        "RISK" to 36,
        "riskLevel" to 37,
        "VERIFYMTHD" to 38,
        "verifyType" to 39,
        "STRUCT_STOP" to 40,
        "REQUIREMENT" to 41,
        "FUNCTIONAL_REQUIREMENT" to 42,
        "INTERFACE_REQUIREMENT" to 43,
        "PERFORMANCE_REQUIREMENT" to 44,
        "PHYSICAL_REQUIREMENT" to 45,
        "DESIGN_CONSTRAINT" to 46,
        "LOW_RISK" to 47,
        "MED_RISK" to 48,
        "HIGH_RISK" to 49,
        "VERIFY_ANALYSIS" to 50,
        "VERIFY_DEMONSTRATION" to 51,
        "VERIFY_INSPECTION" to 52,
        "VERIFY_TEST" to 53,
        "ELEMENT" to 54,
        "elementName" to 55,
        "elementBody" to 56,
        "TYPE" to 57,
        "type" to 58,
        "DOCREF" to 59,
        "ref" to 60,
        "END_ARROW_L" to 61,
        "relationship" to 62,
        "LINE" to 63,
        "END_ARROW_R" to 64,
        "CONTAINS" to 65,
        "COPIES" to 66,
        "DERIVES" to 67,
        "SATISFIES" to 68,
        "VERIFIES" to 69,
        "REFINES" to 70,
        "TRACES" to 71,
        "CLASSDEF" to 72,
        "stylesOpt" to 73,
        "CLASS" to 74,
        "ALPHA" to 75,
        "COMMA" to 76,
        "STYLE" to 77,
        "style" to 78,
        "styleComponent" to 79,
        "NUM" to 80,
        "COLON" to 81,
        "UNIT" to 82,
        "SPACE" to 83,
        "BRKT" to 84,
        "PCT" to 85,
        "MINUS" to 86,
        "LABEL" to 87,
        "SEMICOLON" to 88,
        "unqString" to 89,
        "qString" to 90,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        5 to "NEWLINE",
        6 to "RD",
        8 to "EOF",
        9 to "acc_title",
        10 to "acc_title_value",
        11 to "acc_descr",
        12 to "acc_descr_value",
        13 to "acc_descr_multiline_value",
        21 to "direction_tb",
        22 to "direction_bt",
        23 to "direction_rl",
        24 to "direction_lr",
        27 to "STRUCT_START",
        29 to "STYLE_SEPARATOR",
        31 to "ID",
        32 to "COLONSEP",
        34 to "TEXT",
        36 to "RISK",
        38 to "VERIFYMTHD",
        40 to "STRUCT_STOP",
        41 to "REQUIREMENT",
        42 to "FUNCTIONAL_REQUIREMENT",
        43 to "INTERFACE_REQUIREMENT",
        44 to "PERFORMANCE_REQUIREMENT",
        45 to "PHYSICAL_REQUIREMENT",
        46 to "DESIGN_CONSTRAINT",
        47 to "LOW_RISK",
        48 to "MED_RISK",
        49 to "HIGH_RISK",
        50 to "VERIFY_ANALYSIS",
        51 to "VERIFY_DEMONSTRATION",
        52 to "VERIFY_INSPECTION",
        53 to "VERIFY_TEST",
        54 to "ELEMENT",
        57 to "TYPE",
        59 to "DOCREF",
        61 to "END_ARROW_L",
        63 to "LINE",
        64 to "END_ARROW_R",
        65 to "CONTAINS",
        66 to "COPIES",
        67 to "DERIVES",
        68 to "SATISFIES",
        69 to "VERIFIES",
        70 to "REFINES",
        71 to "TRACES",
        72 to "CLASSDEF",
        74 to "CLASS",
        75 to "ALPHA",
        76 to "COMMA",
        77 to "STYLE",
        80 to "NUM",
        81 to "COLON",
        82 to "UNIT",
        83 to "SPACE",
        84 to "BRKT",
        85 to "PCT",
        86 to "MINUS",
        87 to "LABEL",
        88 to "SEMICOLON",
        89 to "unqString",
        90 to "qString",
    )

    val productions: Array<RequirementJisonProduction> = arrayOf(
        RequirementJisonProduction(symbol = 0, length = 0),
        RequirementJisonProduction(symbol = 3, length = 3),
        RequirementJisonProduction(symbol = 3, length = 2),
        RequirementJisonProduction(symbol = 3, length = 4),
        RequirementJisonProduction(symbol = 4, length = 2),
        RequirementJisonProduction(symbol = 4, length = 2),
        RequirementJisonProduction(symbol = 4, length = 1),
        RequirementJisonProduction(symbol = 7, length = 0),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 7, length = 2),
        RequirementJisonProduction(symbol = 17, length = 1),
        RequirementJisonProduction(symbol = 17, length = 1),
        RequirementJisonProduction(symbol = 17, length = 1),
        RequirementJisonProduction(symbol = 17, length = 1),
        RequirementJisonProduction(symbol = 14, length = 5),
        RequirementJisonProduction(symbol = 14, length = 7),
        RequirementJisonProduction(symbol = 28, length = 5),
        RequirementJisonProduction(symbol = 28, length = 5),
        RequirementJisonProduction(symbol = 28, length = 5),
        RequirementJisonProduction(symbol = 28, length = 5),
        RequirementJisonProduction(symbol = 28, length = 2),
        RequirementJisonProduction(symbol = 28, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 25, length = 1),
        RequirementJisonProduction(symbol = 37, length = 1),
        RequirementJisonProduction(symbol = 37, length = 1),
        RequirementJisonProduction(symbol = 37, length = 1),
        RequirementJisonProduction(symbol = 39, length = 1),
        RequirementJisonProduction(symbol = 39, length = 1),
        RequirementJisonProduction(symbol = 39, length = 1),
        RequirementJisonProduction(symbol = 39, length = 1),
        RequirementJisonProduction(symbol = 15, length = 5),
        RequirementJisonProduction(symbol = 15, length = 7),
        RequirementJisonProduction(symbol = 56, length = 5),
        RequirementJisonProduction(symbol = 56, length = 5),
        RequirementJisonProduction(symbol = 56, length = 2),
        RequirementJisonProduction(symbol = 56, length = 1),
        RequirementJisonProduction(symbol = 16, length = 5),
        RequirementJisonProduction(symbol = 16, length = 5),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 62, length = 1),
        RequirementJisonProduction(symbol = 19, length = 3),
        RequirementJisonProduction(symbol = 20, length = 3),
        RequirementJisonProduction(symbol = 20, length = 3),
        RequirementJisonProduction(symbol = 30, length = 1),
        RequirementJisonProduction(symbol = 30, length = 3),
        RequirementJisonProduction(symbol = 30, length = 1),
        RequirementJisonProduction(symbol = 30, length = 3),
        RequirementJisonProduction(symbol = 18, length = 3),
        RequirementJisonProduction(symbol = 73, length = 1),
        RequirementJisonProduction(symbol = 73, length = 3),
        RequirementJisonProduction(symbol = 78, length = 1),
        RequirementJisonProduction(symbol = 78, length = 2),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 79, length = 1),
        RequirementJisonProduction(symbol = 26, length = 1),
        RequirementJisonProduction(symbol = 26, length = 1),
        RequirementJisonProduction(symbol = 33, length = 1),
        RequirementJisonProduction(symbol = 33, length = 1),
        RequirementJisonProduction(symbol = 35, length = 1),
        RequirementJisonProduction(symbol = 35, length = 1),
        RequirementJisonProduction(symbol = 55, length = 1),
        RequirementJisonProduction(symbol = 55, length = 1),
        RequirementJisonProduction(symbol = 58, length = 1),
        RequirementJisonProduction(symbol = 58, length = 1),
        RequirementJisonProduction(symbol = 60, length = 1),
        RequirementJisonProduction(symbol = 60, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: RequirementJisonCell = RequirementJisonCell.Goto(1)
    private val action1: RequirementJisonCell = RequirementJisonCell.Goto(2)
    private val action2: RequirementJisonCell = RequirementJisonCell.Shift(3)
    private val action3: RequirementJisonCell = RequirementJisonCell.Shift(4)
    private val action4: RequirementJisonCell = RequirementJisonCell.Shift(5)
    private val action5: RequirementJisonCell = RequirementJisonCell.Shift(6)
    private val action6: RequirementJisonCell = RequirementJisonCell.Accept
    private val action7: RequirementJisonCell = RequirementJisonCell.Goto(8)
    private val action8: RequirementJisonCell = RequirementJisonCell.Shift(7)
    private val action9: RequirementJisonCell = RequirementJisonCell.Shift(9)
    private val action10: RequirementJisonCell = RequirementJisonCell.Shift(10)
    private val action11: RequirementJisonCell = RequirementJisonCell.Shift(11)
    private val action12: RequirementJisonCell = RequirementJisonCell.Reduce(6)
    private val action13: RequirementJisonCell = RequirementJisonCell.Goto(12)
    private val action14: RequirementJisonCell = RequirementJisonCell.Reduce(2)
    private val action15: RequirementJisonCell = RequirementJisonCell.Goto(17)
    private val action16: RequirementJisonCell = RequirementJisonCell.Shift(22)
    private val action17: RequirementJisonCell = RequirementJisonCell.Goto(13)
    private val action18: RequirementJisonCell = RequirementJisonCell.Reduce(7)
    private val action19: RequirementJisonCell = RequirementJisonCell.Goto(14)
    private val action20: RequirementJisonCell = RequirementJisonCell.Goto(15)
    private val action21: RequirementJisonCell = RequirementJisonCell.Goto(16)
    private val action22: RequirementJisonCell = RequirementJisonCell.Goto(18)
    private val action23: RequirementJisonCell = RequirementJisonCell.Goto(19)
    private val action24: RequirementJisonCell = RequirementJisonCell.Goto(20)
    private val action25: RequirementJisonCell = RequirementJisonCell.Goto(21)
    private val action26: RequirementJisonCell = RequirementJisonCell.Shift(26)
    private val action27: RequirementJisonCell = RequirementJisonCell.Shift(27)
    private val action28: RequirementJisonCell = RequirementJisonCell.Shift(28)
    private val action29: RequirementJisonCell = RequirementJisonCell.Shift(29)
    private val action30: RequirementJisonCell = RequirementJisonCell.Goto(23)
    private val action31: RequirementJisonCell = RequirementJisonCell.Goto(25)
    private val action32: RequirementJisonCell = RequirementJisonCell.Shift(33)
    private val action33: RequirementJisonCell = RequirementJisonCell.Shift(34)
    private val action34: RequirementJisonCell = RequirementJisonCell.Shift(35)
    private val action35: RequirementJisonCell = RequirementJisonCell.Shift(36)
    private val action36: RequirementJisonCell = RequirementJisonCell.Shift(37)
    private val action37: RequirementJisonCell = RequirementJisonCell.Shift(38)
    private val action38: RequirementJisonCell = RequirementJisonCell.Shift(24)
    private val action39: RequirementJisonCell = RequirementJisonCell.Shift(31)
    private val action40: RequirementJisonCell = RequirementJisonCell.Shift(32)
    private val action41: RequirementJisonCell = RequirementJisonCell.Shift(30)
    private val action42: RequirementJisonCell = RequirementJisonCell.Shift(39)
    private val action43: RequirementJisonCell = RequirementJisonCell.Shift(40)
    private val action44: RequirementJisonCell = RequirementJisonCell.Reduce(4)
    private val action45: RequirementJisonCell = RequirementJisonCell.Reduce(5)
    private val action46: RequirementJisonCell = RequirementJisonCell.Reduce(1)
    private val action47: RequirementJisonCell = RequirementJisonCell.Shift(41)
    private val action48: RequirementJisonCell = RequirementJisonCell.Goto(42)
    private val action49: RequirementJisonCell = RequirementJisonCell.Goto(43)
    private val action50: RequirementJisonCell = RequirementJisonCell.Goto(44)
    private val action51: RequirementJisonCell = RequirementJisonCell.Goto(45)
    private val action52: RequirementJisonCell = RequirementJisonCell.Goto(46)
    private val action53: RequirementJisonCell = RequirementJisonCell.Goto(47)
    private val action54: RequirementJisonCell = RequirementJisonCell.Goto(48)
    private val action55: RequirementJisonCell = RequirementJisonCell.Goto(49)
    private val action56: RequirementJisonCell = RequirementJisonCell.Goto(50)
    private val action57: RequirementJisonCell = RequirementJisonCell.Goto(51)
    private val action58: RequirementJisonCell = RequirementJisonCell.Shift(52)
    private val action59: RequirementJisonCell = RequirementJisonCell.Shift(53)
    private val action60: RequirementJisonCell = RequirementJisonCell.Goto(54)
    private val action61: RequirementJisonCell = RequirementJisonCell.Shift(55)
    private val action62: RequirementJisonCell = RequirementJisonCell.Shift(56)
    private val action63: RequirementJisonCell = RequirementJisonCell.Shift(59)
    private val action64: RequirementJisonCell = RequirementJisonCell.Shift(57)
    private val action65: RequirementJisonCell = RequirementJisonCell.Shift(58)
    private val action66: RequirementJisonCell = RequirementJisonCell.Reduce(17)
    private val action67: RequirementJisonCell = RequirementJisonCell.Reduce(18)
    private val action68: RequirementJisonCell = RequirementJisonCell.Reduce(19)
    private val action69: RequirementJisonCell = RequirementJisonCell.Reduce(20)
    private val action70: RequirementJisonCell = RequirementJisonCell.Goto(60)
    private val action71: RequirementJisonCell = RequirementJisonCell.Goto(62)
    private val action72: RequirementJisonCell = RequirementJisonCell.Shift(61)
    private val action73: RequirementJisonCell = RequirementJisonCell.Goto(63)
    private val action74: RequirementJisonCell = RequirementJisonCell.Goto(64)
    private val action75: RequirementJisonCell = RequirementJisonCell.Reduce(29)
    private val action76: RequirementJisonCell = RequirementJisonCell.Reduce(30)
    private val action77: RequirementJisonCell = RequirementJisonCell.Reduce(31)
    private val action78: RequirementJisonCell = RequirementJisonCell.Reduce(32)
    private val action79: RequirementJisonCell = RequirementJisonCell.Reduce(33)
    private val action80: RequirementJisonCell = RequirementJisonCell.Reduce(34)
    private val action81: RequirementJisonCell = RequirementJisonCell.Reduce(81)
    private val action82: RequirementJisonCell = RequirementJisonCell.Reduce(82)
    private val action83: RequirementJisonCell = RequirementJisonCell.Reduce(3)
    private val action84: RequirementJisonCell = RequirementJisonCell.Reduce(8)
    private val action85: RequirementJisonCell = RequirementJisonCell.Reduce(9)
    private val action86: RequirementJisonCell = RequirementJisonCell.Reduce(10)
    private val action87: RequirementJisonCell = RequirementJisonCell.Reduce(11)
    private val action88: RequirementJisonCell = RequirementJisonCell.Reduce(12)
    private val action89: RequirementJisonCell = RequirementJisonCell.Reduce(13)
    private val action90: RequirementJisonCell = RequirementJisonCell.Reduce(14)
    private val action91: RequirementJisonCell = RequirementJisonCell.Reduce(15)
    private val action92: RequirementJisonCell = RequirementJisonCell.Reduce(16)
    private val action93: RequirementJisonCell = RequirementJisonCell.Shift(65)
    private val action94: RequirementJisonCell = RequirementJisonCell.Shift(66)
    private val action95: RequirementJisonCell = RequirementJisonCell.Reduce(79)
    private val action96: RequirementJisonCell = RequirementJisonCell.Reduce(80)
    private val action97: RequirementJisonCell = RequirementJisonCell.Shift(67)
    private val action98: RequirementJisonCell = RequirementJisonCell.Shift(68)
    private val action99: RequirementJisonCell = RequirementJisonCell.Reduce(85)
    private val action100: RequirementJisonCell = RequirementJisonCell.Reduce(86)
    private val action101: RequirementJisonCell = RequirementJisonCell.Goto(69)
    private val action102: RequirementJisonCell = RequirementJisonCell.Shift(70)
    private val action103: RequirementJisonCell = RequirementJisonCell.Shift(71)
    private val action104: RequirementJisonCell = RequirementJisonCell.Shift(72)
    private val action105: RequirementJisonCell = RequirementJisonCell.Shift(73)
    private val action106: RequirementJisonCell = RequirementJisonCell.Shift(74)
    private val action107: RequirementJisonCell = RequirementJisonCell.Shift(75)
    private val action108: RequirementJisonCell = RequirementJisonCell.Shift(76)
    private val action109: RequirementJisonCell = RequirementJisonCell.Goto(77)
    private val action110: RequirementJisonCell = RequirementJisonCell.Goto(78)
    private val action111: RequirementJisonCell = RequirementJisonCell.Goto(79)
    private val action112: RequirementJisonCell = RequirementJisonCell.Shift(83)
    private val action113: RequirementJisonCell = RequirementJisonCell.Shift(80)
    private val action114: RequirementJisonCell = RequirementJisonCell.Goto(81)
    private val action115: RequirementJisonCell = RequirementJisonCell.Goto(82)
    private val action116: RequirementJisonCell = RequirementJisonCell.Shift(84)
    private val action117: RequirementJisonCell = RequirementJisonCell.Shift(85)
    private val action118: RequirementJisonCell = RequirementJisonCell.Shift(86)
    private val action119: RequirementJisonCell = RequirementJisonCell.Shift(87)
    private val action120: RequirementJisonCell = RequirementJisonCell.Shift(88)
    private val action121: RequirementJisonCell = RequirementJisonCell.Shift(89)
    private val action122: RequirementJisonCell = RequirementJisonCell.Shift(90)
    private val action123: RequirementJisonCell = RequirementJisonCell.Shift(91)
    private val action124: RequirementJisonCell = RequirementJisonCell.Shift(92)
    private val action125: RequirementJisonCell = RequirementJisonCell.Reduce(60)
    private val action126: RequirementJisonCell = RequirementJisonCell.Reduce(62)
    private val action127: RequirementJisonCell = RequirementJisonCell.Goto(93)
    private val action128: RequirementJisonCell = RequirementJisonCell.Goto(94)
    private val action129: RequirementJisonCell = RequirementJisonCell.Shift(95)
    private val action130: RequirementJisonCell = RequirementJisonCell.Goto(96)
    private val action131: RequirementJisonCell = RequirementJisonCell.Shift(97)
    private val action132: RequirementJisonCell = RequirementJisonCell.Goto(98)
    private val action133: RequirementJisonCell = RequirementJisonCell.Shift(99)
    private val action134: RequirementJisonCell = RequirementJisonCell.Reduce(50)
    private val action135: RequirementJisonCell = RequirementJisonCell.Reduce(51)
    private val action136: RequirementJisonCell = RequirementJisonCell.Reduce(52)
    private val action137: RequirementJisonCell = RequirementJisonCell.Reduce(53)
    private val action138: RequirementJisonCell = RequirementJisonCell.Reduce(54)
    private val action139: RequirementJisonCell = RequirementJisonCell.Reduce(55)
    private val action140: RequirementJisonCell = RequirementJisonCell.Reduce(56)
    private val action141: RequirementJisonCell = RequirementJisonCell.Shift(100)
    private val action142: RequirementJisonCell = RequirementJisonCell.Reduce(59)
    private val action143: RequirementJisonCell = RequirementJisonCell.Reduce(64)
    private val action144: RequirementJisonCell = RequirementJisonCell.Shift(101)
    private val action145: RequirementJisonCell = RequirementJisonCell.Goto(103)
    private val action146: RequirementJisonCell = RequirementJisonCell.Shift(102)
    private val action147: RequirementJisonCell = RequirementJisonCell.Reduce(65)
    private val action148: RequirementJisonCell = RequirementJisonCell.Goto(104)
    private val action149: RequirementJisonCell = RequirementJisonCell.Reduce(67)
    private val action150: RequirementJisonCell = RequirementJisonCell.Reduce(69)
    private val action151: RequirementJisonCell = RequirementJisonCell.Reduce(70)
    private val action152: RequirementJisonCell = RequirementJisonCell.Reduce(71)
    private val action153: RequirementJisonCell = RequirementJisonCell.Reduce(72)
    private val action154: RequirementJisonCell = RequirementJisonCell.Reduce(73)
    private val action155: RequirementJisonCell = RequirementJisonCell.Reduce(74)
    private val action156: RequirementJisonCell = RequirementJisonCell.Reduce(75)
    private val action157: RequirementJisonCell = RequirementJisonCell.Reduce(76)
    private val action158: RequirementJisonCell = RequirementJisonCell.Reduce(77)
    private val action159: RequirementJisonCell = RequirementJisonCell.Reduce(78)
    private val action160: RequirementJisonCell = RequirementJisonCell.Reduce(57)
    private val action161: RequirementJisonCell = RequirementJisonCell.Reduce(58)
    private val action162: RequirementJisonCell = RequirementJisonCell.Shift(110)
    private val action163: RequirementJisonCell = RequirementJisonCell.Goto(105)
    private val action164: RequirementJisonCell = RequirementJisonCell.Shift(106)
    private val action165: RequirementJisonCell = RequirementJisonCell.Shift(107)
    private val action166: RequirementJisonCell = RequirementJisonCell.Shift(108)
    private val action167: RequirementJisonCell = RequirementJisonCell.Shift(109)
    private val action168: RequirementJisonCell = RequirementJisonCell.Shift(111)
    private val action169: RequirementJisonCell = RequirementJisonCell.Shift(112)
    private val action170: RequirementJisonCell = RequirementJisonCell.Shift(116)
    private val action171: RequirementJisonCell = RequirementJisonCell.Shift(117)
    private val action172: RequirementJisonCell = RequirementJisonCell.Goto(113)
    private val action173: RequirementJisonCell = RequirementJisonCell.Shift(114)
    private val action174: RequirementJisonCell = RequirementJisonCell.Shift(115)
    private val action175: RequirementJisonCell = RequirementJisonCell.Shift(118)
    private val action176: RequirementJisonCell = RequirementJisonCell.Goto(119)
    private val action177: RequirementJisonCell = RequirementJisonCell.Goto(120)
    private val action178: RequirementJisonCell = RequirementJisonCell.Goto(121)
    private val action179: RequirementJisonCell = RequirementJisonCell.Reduce(61)
    private val action180: RequirementJisonCell = RequirementJisonCell.Reduce(63)
    private val action181: RequirementJisonCell = RequirementJisonCell.Reduce(68)
    private val action182: RequirementJisonCell = RequirementJisonCell.Reduce(21)
    private val action183: RequirementJisonCell = RequirementJisonCell.Shift(122)
    private val action184: RequirementJisonCell = RequirementJisonCell.Shift(123)
    private val action185: RequirementJisonCell = RequirementJisonCell.Shift(124)
    private val action186: RequirementJisonCell = RequirementJisonCell.Shift(125)
    private val action187: RequirementJisonCell = RequirementJisonCell.Goto(126)
    private val action188: RequirementJisonCell = RequirementJisonCell.Reduce(28)
    private val action189: RequirementJisonCell = RequirementJisonCell.Shift(127)
    private val action190: RequirementJisonCell = RequirementJisonCell.Reduce(42)
    private val action191: RequirementJisonCell = RequirementJisonCell.Shift(128)
    private val action192: RequirementJisonCell = RequirementJisonCell.Shift(129)
    private val action193: RequirementJisonCell = RequirementJisonCell.Goto(130)
    private val action194: RequirementJisonCell = RequirementJisonCell.Reduce(47)
    private val action195: RequirementJisonCell = RequirementJisonCell.Shift(131)
    private val action196: RequirementJisonCell = RequirementJisonCell.Reduce(48)
    private val action197: RequirementJisonCell = RequirementJisonCell.Reduce(49)
    private val action198: RequirementJisonCell = RequirementJisonCell.Reduce(66)
    private val action199: RequirementJisonCell = RequirementJisonCell.Goto(132)
    private val action200: RequirementJisonCell = RequirementJisonCell.Goto(133)
    private val action201: RequirementJisonCell = RequirementJisonCell.Shift(134)
    private val action202: RequirementJisonCell = RequirementJisonCell.Shift(135)
    private val action203: RequirementJisonCell = RequirementJisonCell.Goto(136)
    private val action204: RequirementJisonCell = RequirementJisonCell.Shift(137)
    private val action205: RequirementJisonCell = RequirementJisonCell.Shift(138)
    private val action206: RequirementJisonCell = RequirementJisonCell.Shift(139)
    private val action207: RequirementJisonCell = RequirementJisonCell.Goto(140)
    private val action208: RequirementJisonCell = RequirementJisonCell.Shift(141)
    private val action209: RequirementJisonCell = RequirementJisonCell.Shift(142)
    private val action210: RequirementJisonCell = RequirementJisonCell.Shift(143)
    private val action211: RequirementJisonCell = RequirementJisonCell.Shift(144)
    private val action212: RequirementJisonCell = RequirementJisonCell.Reduce(27)
    private val action213: RequirementJisonCell = RequirementJisonCell.Goto(145)
    private val action214: RequirementJisonCell = RequirementJisonCell.Goto(146)
    private val action215: RequirementJisonCell = RequirementJisonCell.Shift(147)
    private val action216: RequirementJisonCell = RequirementJisonCell.Shift(148)
    private val action217: RequirementJisonCell = RequirementJisonCell.Goto(149)
    private val action218: RequirementJisonCell = RequirementJisonCell.Shift(150)
    private val action219: RequirementJisonCell = RequirementJisonCell.Shift(151)
    private val action220: RequirementJisonCell = RequirementJisonCell.Reduce(46)
    private val action221: RequirementJisonCell = RequirementJisonCell.Goto(152)
    private val action222: RequirementJisonCell = RequirementJisonCell.Shift(153)
    private val action223: RequirementJisonCell = RequirementJisonCell.Shift(154)
    private val action224: RequirementJisonCell = RequirementJisonCell.Reduce(83)
    private val action225: RequirementJisonCell = RequirementJisonCell.Reduce(84)
    private val action226: RequirementJisonCell = RequirementJisonCell.Shift(155)
    private val action227: RequirementJisonCell = RequirementJisonCell.Reduce(35)
    private val action228: RequirementJisonCell = RequirementJisonCell.Reduce(36)
    private val action229: RequirementJisonCell = RequirementJisonCell.Reduce(37)
    private val action230: RequirementJisonCell = RequirementJisonCell.Shift(156)
    private val action231: RequirementJisonCell = RequirementJisonCell.Reduce(38)
    private val action232: RequirementJisonCell = RequirementJisonCell.Reduce(39)
    private val action233: RequirementJisonCell = RequirementJisonCell.Reduce(40)
    private val action234: RequirementJisonCell = RequirementJisonCell.Reduce(41)
    private val action235: RequirementJisonCell = RequirementJisonCell.Reduce(22)
    private val action236: RequirementJisonCell = RequirementJisonCell.Shift(157)
    private val action237: RequirementJisonCell = RequirementJisonCell.Reduce(87)
    private val action238: RequirementJisonCell = RequirementJisonCell.Reduce(88)
    private val action239: RequirementJisonCell = RequirementJisonCell.Shift(158)
    private val action240: RequirementJisonCell = RequirementJisonCell.Reduce(89)
    private val action241: RequirementJisonCell = RequirementJisonCell.Reduce(90)
    private val action242: RequirementJisonCell = RequirementJisonCell.Reduce(43)
    private val action243: RequirementJisonCell = RequirementJisonCell.Goto(159)
    private val action244: RequirementJisonCell = RequirementJisonCell.Goto(160)
    private val action245: RequirementJisonCell = RequirementJisonCell.Goto(161)
    private val action246: RequirementJisonCell = RequirementJisonCell.Goto(162)
    private val action247: RequirementJisonCell = RequirementJisonCell.Goto(163)
    private val action248: RequirementJisonCell = RequirementJisonCell.Goto(164)
    private val action249: RequirementJisonCell = RequirementJisonCell.Reduce(23)
    private val action250: RequirementJisonCell = RequirementJisonCell.Reduce(24)
    private val action251: RequirementJisonCell = RequirementJisonCell.Reduce(25)
    private val action252: RequirementJisonCell = RequirementJisonCell.Reduce(26)
    private val action253: RequirementJisonCell = RequirementJisonCell.Reduce(44)
    private val action254: RequirementJisonCell = RequirementJisonCell.Reduce(45)
    private val symbols0 = intArrayOf(5, 6, 8, 9, 11, 13, 21, 22, 23, 24, 41, 42, 43, 44, 45, 46, 54, 72, 74, 77, 89, 90)
    private val symbols1 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 41, 42, 43, 44, 45, 46, 54, 72, 74, 77, 89, 90)
    private val symbols2 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 27, 29, 41, 42, 43, 44, 45, 46, 54, 61, 63, 72, 74, 75, 76, 77, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90)
    private val symbols3 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 27, 41, 42, 43, 44, 45, 46, 54, 72, 74, 75, 76, 77, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90)
    private val symbols4 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 41, 42, 43, 44, 45, 46, 54, 72, 74, 76, 77, 89, 90)
    private val symbols5 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 41, 42, 43, 44, 45, 46, 54, 72, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90)
    private val symbols6 = intArrayOf(5, 8, 9, 11, 13, 21, 22, 23, 24, 41, 42, 43, 44, 45, 46, 54, 72, 74, 75, 76, 77, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90)

    val states: Array<Map<Int, RequirementJisonCell>> = buildList {
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

    private fun stateChunk0(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            3 to action0,
            4 to action1,
            6 to action2,
            9 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            1 to action6,
        ),
        mapOf(
            3 to action7,
            4 to action1,
            5 to action8,
            6 to action2,
            9 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            5 to action9,
        ),
        mapOf(
            10 to action10,
        ),
        mapOf(
            12 to action11,
        ),
        buildJisonState(symbols0, action12),
        mapOf(
            3 to action13,
            4 to action1,
            6 to action2,
            9 to action3,
            11 to action4,
            13 to action5,
        ),
        mapOf(
            1 to action14,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action17,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        buildJisonState(symbols0, action44),
        buildJisonState(symbols0, action45),
        mapOf(
            1 to action46,
        ),
        mapOf(
            8 to action47,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action48,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action49,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action50,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action51,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action52,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action53,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            4 to action15,
            5 to action16,
            7 to action54,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action55,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            4 to action15,
            5 to action16,
            7 to action56,
            8 to action18,
            9 to action3,
            11 to action4,
            13 to action5,
            14 to action19,
            15 to action20,
            16 to action21,
            17 to action22,
            18 to action23,
            19 to action24,
            20 to action25,
            21 to action26,
            22 to action27,
            23 to action28,
            24 to action29,
            25 to action30,
            33 to action31,
            41 to action32,
            42 to action33,
            43 to action34,
            44 to action35,
            45 to action36,
            46 to action37,
            54 to action38,
            72 to action39,
            74 to action40,
            77 to action41,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            26 to action57,
            89 to action58,
            90 to action59,
        ),
        mapOf(
            55 to action60,
            89 to action61,
            90 to action62,
        ),
        mapOf(
            29 to action63,
            61 to action64,
            63 to action65,
        ),
        buildJisonState(symbols1, action66),
        buildJisonState(symbols1, action67),
        buildJisonState(symbols1, action68),
        buildJisonState(symbols1, action69),
        mapOf(
            30 to action70,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            30 to action73,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            30 to action74,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            89 to action75,
            90 to action75,
        ),
        mapOf(
            89 to action76,
            90 to action76,
        ),
        mapOf(
            89 to action77,
            90 to action77,
        ),
        mapOf(
            89 to action78,
            90 to action78,
        ),
        mapOf(
            89 to action79,
            90 to action79,
        ),
        mapOf(
            89 to action80,
            90 to action80,
        ),
        buildJisonState(symbols2, action81),
    )

    private fun stateChunk2(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        buildJisonState(symbols2, action82),
        mapOf(
            1 to action83,
        ),
        mapOf(
            8 to action84,
        ),
        mapOf(
            8 to action85,
        ),
        mapOf(
            8 to action86,
        ),
        mapOf(
            8 to action87,
        ),
        mapOf(
            8 to action88,
        ),
        mapOf(
            8 to action89,
        ),
        mapOf(
            8 to action90,
        ),
        mapOf(
            8 to action91,
        ),
        mapOf(
            8 to action92,
        ),
        mapOf(
            27 to action93,
            29 to action94,
        ),
        mapOf(
            27 to action95,
            29 to action95,
        ),
        mapOf(
            27 to action96,
            29 to action96,
        ),
        mapOf(
            27 to action97,
            29 to action98,
        ),
        mapOf(
            27 to action99,
            29 to action99,
        ),
        mapOf(
            27 to action100,
            29 to action100,
        ),
        mapOf(
            62 to action101,
            65 to action102,
            66 to action103,
            67 to action104,
            68 to action105,
            69 to action106,
            70 to action107,
            71 to action108,
        ),
        mapOf(
            62 to action109,
            65 to action102,
            66 to action103,
            67 to action104,
            68 to action105,
            69 to action106,
            70 to action107,
            71 to action108,
        ),
        mapOf(
            30 to action110,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
    )

    private fun stateChunk3(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            73 to action111,
            75 to action112,
            76 to action113,
            78 to action114,
            79 to action115,
            80 to action116,
            81 to action117,
            82 to action118,
            83 to action119,
            84 to action120,
            85 to action121,
            86 to action122,
            87 to action123,
            88 to action124,
        ),
        buildJisonState(symbols3, action125),
        buildJisonState(symbols3, action126),
        mapOf(
            73 to action127,
            75 to action112,
            76 to action113,
            78 to action114,
            79 to action115,
            80 to action116,
            81 to action117,
            82 to action118,
            83 to action119,
            84 to action120,
            85 to action121,
            86 to action122,
            87 to action123,
            88 to action124,
        ),
        mapOf(
            30 to action128,
            33 to action71,
            75 to action72,
            76 to action113,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            5 to action129,
        ),
        mapOf(
            30 to action130,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            5 to action131,
        ),
        mapOf(
            30 to action132,
            33 to action71,
            75 to action72,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            63 to action133,
        ),
        mapOf(
            63 to action134,
            64 to action134,
        ),
        mapOf(
            63 to action135,
            64 to action135,
        ),
        mapOf(
            63 to action136,
            64 to action136,
        ),
        mapOf(
            63 to action137,
            64 to action137,
        ),
        mapOf(
            63 to action138,
            64 to action138,
        ),
        mapOf(
            63 to action139,
            64 to action139,
        ),
        mapOf(
            63 to action140,
            64 to action140,
        ),
        mapOf(
            64 to action141,
        ),
        buildJisonState(symbols4, action142, mapOf(
            76 to action113,
        )),
        buildJisonState(symbols4, action143, mapOf(
            76 to action144,
        )),
    )

    private fun stateChunk4(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            33 to action145,
            75 to action146,
            89 to action42,
            90 to action43,
        ),
        buildJisonState(symbols5, action147, mapOf(
            75 to action112,
            79 to action148,
            80 to action116,
            81 to action117,
            82 to action118,
            83 to action119,
            84 to action120,
            85 to action121,
            86 to action122,
            87 to action123,
            88 to action124,
        )),
        buildJisonState(symbols6, action149),
        buildJisonState(symbols6, action150),
        buildJisonState(symbols6, action151),
        buildJisonState(symbols6, action152),
        buildJisonState(symbols6, action153),
        buildJisonState(symbols6, action154),
        buildJisonState(symbols6, action155),
        buildJisonState(symbols6, action156),
        buildJisonState(symbols6, action157),
        buildJisonState(symbols6, action158),
        buildJisonState(symbols6, action159),
        buildJisonState(symbols4, action160, mapOf(
            76 to action144,
        )),
        buildJisonState(symbols4, action161, mapOf(
            76 to action113,
        )),
        mapOf(
            5 to action162,
            28 to action163,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            27 to action169,
            76 to action113,
        ),
        mapOf(
            5 to action170,
            40 to action171,
            56 to action172,
            57 to action173,
            59 to action174,
        ),
        mapOf(
            27 to action175,
            76 to action113,
        ),
        mapOf(
            33 to action176,
            89 to action42,
            90 to action43,
        ),
    )

    private fun stateChunk5(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            33 to action177,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            75 to action112,
            78 to action178,
            79 to action115,
            80 to action116,
            81 to action117,
            82 to action118,
            83 to action119,
            84 to action120,
            85 to action121,
            86 to action122,
            87 to action123,
            88 to action124,
        ),
        buildJisonState(symbols3, action179),
        buildJisonState(symbols3, action180),
        buildJisonState(symbols6, action181),
        buildJisonState(symbols1, action182),
        mapOf(
            32 to action183,
        ),
        mapOf(
            32 to action184,
        ),
        mapOf(
            32 to action185,
        ),
        mapOf(
            32 to action186,
        ),
        mapOf(
            5 to action162,
            28 to action187,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        buildJisonState(symbols1, action188),
        mapOf(
            5 to action189,
        ),
        buildJisonState(symbols1, action190),
        mapOf(
            32 to action191,
        ),
        mapOf(
            32 to action192,
        ),
        mapOf(
            5 to action170,
            40 to action171,
            56 to action193,
            57 to action173,
            59 to action174,
        ),
        buildJisonState(symbols1, action194),
        mapOf(
            5 to action195,
        ),
        buildJisonState(symbols1, action196),
    )

    private fun stateChunk6(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        buildJisonState(symbols1, action197),
        buildJisonState(symbols5, action198, mapOf(
            75 to action112,
            79 to action148,
            80 to action116,
            81 to action117,
            82 to action118,
            83 to action119,
            84 to action120,
            85 to action121,
            86 to action122,
            87 to action123,
            88 to action124,
        )),
        mapOf(
            33 to action199,
            89 to action42,
            90 to action43,
        ),
        mapOf(
            35 to action200,
            89 to action201,
            90 to action202,
        ),
        mapOf(
            37 to action203,
            47 to action204,
            48 to action205,
            49 to action206,
        ),
        mapOf(
            39 to action207,
            50 to action208,
            51 to action209,
            52 to action210,
            53 to action211,
        ),
        buildJisonState(symbols1, action212),
        mapOf(
            5 to action162,
            28 to action213,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            58 to action214,
            89 to action215,
            90 to action216,
        ),
        mapOf(
            60 to action217,
            89 to action218,
            90 to action219,
        ),
        buildJisonState(symbols1, action220),
        mapOf(
            5 to action170,
            40 to action171,
            56 to action221,
            57 to action173,
            59 to action174,
        ),
        mapOf(
            5 to action222,
        ),
        mapOf(
            5 to action223,
        ),
        mapOf(
            5 to action224,
        ),
        mapOf(
            5 to action225,
        ),
        mapOf(
            5 to action226,
        ),
        mapOf(
            5 to action227,
        ),
        mapOf(
            5 to action228,
        ),
        mapOf(
            5 to action229,
        ),
    )

    private fun stateChunk7(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        mapOf(
            5 to action230,
        ),
        mapOf(
            5 to action231,
        ),
        mapOf(
            5 to action232,
        ),
        mapOf(
            5 to action233,
        ),
        mapOf(
            5 to action234,
        ),
        buildJisonState(symbols1, action235),
        mapOf(
            5 to action236,
        ),
        mapOf(
            5 to action237,
        ),
        mapOf(
            5 to action238,
        ),
        mapOf(
            5 to action239,
        ),
        mapOf(
            5 to action240,
        ),
        mapOf(
            5 to action241,
        ),
        buildJisonState(symbols1, action242),
        mapOf(
            5 to action162,
            28 to action243,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            5 to action162,
            28 to action244,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            5 to action162,
            28 to action245,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            5 to action162,
            28 to action246,
            31 to action164,
            34 to action165,
            36 to action166,
            38 to action167,
            40 to action168,
        ),
        mapOf(
            5 to action170,
            40 to action171,
            56 to action247,
            57 to action173,
            59 to action174,
        ),
        mapOf(
            5 to action170,
            40 to action171,
            56 to action248,
            57 to action173,
            59 to action174,
        ),
        buildJisonState(symbols1, action249),
    )

    private fun stateChunk8(): Array<Map<Int, RequirementJisonCell>> = arrayOf(
        buildJisonState(symbols1, action250),
        buildJisonState(symbols1, action251),
        buildJisonState(symbols1, action252),
        buildJisonState(symbols1, action253),
        buildJisonState(symbols1, action254),
    )

    val lexerPatterns: List<RequirementJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
    }

    private fun lexerChunk0(): List<RequirementJisonLexerPattern> = listOf(
        RequirementJisonLexerPattern(0, "^(?:title\\s[^#\\n;]+)"),
        RequirementJisonLexerPattern(1, "^(?:accTitle\\s*:\\s*)"),
        RequirementJisonLexerPattern(2, "^(?:(?!\\n||)*[^\\n]*)"),
        RequirementJisonLexerPattern(3, "^(?:accDescr\\s*:\\s*)"),
        RequirementJisonLexerPattern(4, "^(?:(?!\\n||)*[^\\n]*)"),
        RequirementJisonLexerPattern(5, "^(?:accDescr\\s*\\{\\s*)"),
        RequirementJisonLexerPattern(6, "^(?:[\\}])"),
        RequirementJisonLexerPattern(7, "^(?:[^\\}]*)"),
        RequirementJisonLexerPattern(8, "^(?:.*direction\\s+TB[^\\n]*)"),
        RequirementJisonLexerPattern(9, "^(?:.*direction\\s+BT[^\\n]*)"),
        RequirementJisonLexerPattern(10, "^(?:.*direction\\s+RL[^\\n]*)"),
        RequirementJisonLexerPattern(11, "^(?:.*direction\\s+LR[^\\n]*)"),
        RequirementJisonLexerPattern(12, "^(?:(\\r?\\n)+)"),
        RequirementJisonLexerPattern(13, "^(?:\\s+)"),
        RequirementJisonLexerPattern(14, "^(?:#[^\\n]*)"),
        RequirementJisonLexerPattern(15, "^(?:%[^\\n]*)"),
        RequirementJisonLexerPattern(16, "^(?:\$)"),
        RequirementJisonLexerPattern(17, "^(?:requirementDiagram\\b)"),
        RequirementJisonLexerPattern(18, "^(?:\\{)"),
        RequirementJisonLexerPattern(19, "^(?:\\})"),
    )

    private fun lexerChunk1(): List<RequirementJisonLexerPattern> = listOf(
        RequirementJisonLexerPattern(20, "^(?::{3})"),
        RequirementJisonLexerPattern(21, "^(?::)"),
        RequirementJisonLexerPattern(22, "^(?:id\\b)"),
        RequirementJisonLexerPattern(23, "^(?:text\\b)"),
        RequirementJisonLexerPattern(24, "^(?:risk\\b)"),
        RequirementJisonLexerPattern(25, "^(?:verifyMethod\\b)"),
        RequirementJisonLexerPattern(26, "^(?:requirement\\b)"),
        RequirementJisonLexerPattern(27, "^(?:functionalRequirement\\b)"),
        RequirementJisonLexerPattern(28, "^(?:interfaceRequirement\\b)"),
        RequirementJisonLexerPattern(29, "^(?:performanceRequirement\\b)"),
        RequirementJisonLexerPattern(30, "^(?:physicalRequirement\\b)"),
        RequirementJisonLexerPattern(31, "^(?:designConstraint\\b)"),
        RequirementJisonLexerPattern(32, "^(?:low\\b)"),
        RequirementJisonLexerPattern(33, "^(?:medium\\b)"),
        RequirementJisonLexerPattern(34, "^(?:high\\b)"),
        RequirementJisonLexerPattern(35, "^(?:analysis\\b)"),
        RequirementJisonLexerPattern(36, "^(?:demonstration\\b)"),
        RequirementJisonLexerPattern(37, "^(?:inspection\\b)"),
        RequirementJisonLexerPattern(38, "^(?:test\\b)"),
        RequirementJisonLexerPattern(39, "^(?:element\\b)"),
    )

    private fun lexerChunk2(): List<RequirementJisonLexerPattern> = listOf(
        RequirementJisonLexerPattern(40, "^(?:contains\\b)"),
        RequirementJisonLexerPattern(41, "^(?:copies\\b)"),
        RequirementJisonLexerPattern(42, "^(?:derives\\b)"),
        RequirementJisonLexerPattern(43, "^(?:satisfies\\b)"),
        RequirementJisonLexerPattern(44, "^(?:verifies\\b)"),
        RequirementJisonLexerPattern(45, "^(?:refines\\b)"),
        RequirementJisonLexerPattern(46, "^(?:traces\\b)"),
        RequirementJisonLexerPattern(47, "^(?:type\\b)"),
        RequirementJisonLexerPattern(48, "^(?:docref\\b)"),
        RequirementJisonLexerPattern(49, "^(?:style\\b)"),
        RequirementJisonLexerPattern(50, "^(?:\\w+)"),
        RequirementJisonLexerPattern(51, "^(?::)"),
        RequirementJisonLexerPattern(52, "^(?:;)"),
        RequirementJisonLexerPattern(53, "^(?:%)"),
        RequirementJisonLexerPattern(54, "^(?:-)"),
        RequirementJisonLexerPattern(55, "^(?:#)"),
        RequirementJisonLexerPattern(56, "^(?: )"),
        RequirementJisonLexerPattern(57, "^(?:[\"])"),
        RequirementJisonLexerPattern(58, "^(?:\\n)"),
        RequirementJisonLexerPattern(59, "^(?:classDef\\b)"),
    )

    private fun lexerChunk3(): List<RequirementJisonLexerPattern> = listOf(
        RequirementJisonLexerPattern(60, "^(?:class\\b)"),
        RequirementJisonLexerPattern(61, "^(?:<-)"),
        RequirementJisonLexerPattern(62, "^(?:->)"),
        RequirementJisonLexerPattern(63, "^(?:-)"),
        RequirementJisonLexerPattern(64, "^(?:[\"])"),
        RequirementJisonLexerPattern(65, "^(?:[\"])"),
        RequirementJisonLexerPattern(66, "^(?:[^\"]*)"),
        RequirementJisonLexerPattern(67, "^(?:[\\w][^:,\\r\\n\\{\\<\\>\\-\\=]*)"),
        RequirementJisonLexerPattern(68, "^(?:\\w+)"),
        RequirementJisonLexerPattern(69, "^(?:[0-9]+)"),
        RequirementJisonLexerPattern(70, "^(?:,)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "acc_descr_multiline" to intArrayOf(6, 7, 68, 69, 70),
        "acc_descr" to intArrayOf(4, 68, 69, 70),
        "acc_title" to intArrayOf(2, 68, 69, 70),
        "style" to intArrayOf(50, 51, 52, 53, 54, 55, 56, 57, 58, 68, 69, 70),
        "unqString" to intArrayOf(68, 69, 70),
        "token" to intArrayOf(68, 69, 70),
        "string" to intArrayOf(65, 66, 68, 69, 70),
        "INITIAL" to intArrayOf(0, 1, 3, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 59, 60, 61, 62, 63, 64, 67, 68, 69, 70),
    )
}
