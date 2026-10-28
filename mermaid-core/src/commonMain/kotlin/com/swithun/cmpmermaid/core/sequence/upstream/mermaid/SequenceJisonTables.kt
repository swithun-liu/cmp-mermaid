package com.swithun.cmpmermaid.core.sequence.upstream.mermaid

import com.swithun.cmpmermaid.core.buildJisonState

/**
 * Generated from Mermaid 12.0.0 sequenceDiagram.jison.
 * Upstream sequenceDiagram.jison SHA-256: ff4991942d1cdcc45f78c01bc67e094f0a8bc1ec4cfc8f04f8f13eadabdfe783
 * Distribution chunk SHA-256: 05f2f26e080d2bc490589dd82fc31bff6c24c3d15b9208db2cbd56b9820e9fea
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:sequence-parser
 */
internal object SequenceJisonTables {
    val symbolIds: Map<String, Int> = mapOf(
        "error" to 2,
        "start" to 3,
        "SPACE" to 4,
        "NEWLINE" to 5,
        "SD" to 6,
        "document" to 7,
        "line" to 8,
        "statement" to 9,
        "INVALID" to 10,
        "box_section" to 11,
        "box_line" to 12,
        "participant_statement" to 13,
        "create" to 14,
        "box" to 15,
        "restOfLine" to 16,
        "end" to 17,
        "signal" to 18,
        "autonumber" to 19,
        "NUM" to 20,
        "off" to 21,
        "activate" to 22,
        "actor" to 23,
        "deactivate" to 24,
        "note_statement" to 25,
        "links_statement" to 26,
        "link_statement" to 27,
        "properties_statement" to 28,
        "details_statement" to 29,
        "title" to 30,
        "legacy_title" to 31,
        "acc_title" to 32,
        "acc_title_value" to 33,
        "acc_descr" to 34,
        "acc_descr_value" to 35,
        "acc_descr_multiline_value" to 36,
        "loop" to 37,
        "rect" to 38,
        "opt" to 39,
        "alt" to 40,
        "else_sections" to 41,
        "par" to 42,
        "par_sections" to 43,
        "par_over" to 44,
        "critical" to 45,
        "option_sections" to 46,
        "break" to 47,
        "option" to 48,
        "and" to 49,
        "else" to 50,
        "participant" to 51,
        "AS" to 52,
        "participant_actor" to 53,
        "destroy" to 54,
        "actor_with_config" to 55,
        "note" to 56,
        "placement" to 57,
        "text2" to 58,
        "over" to 59,
        "actor_pair" to 60,
        "links" to 61,
        "link" to 62,
        "properties" to 63,
        "details" to 64,
        "spaceList" to 65,
        "," to 66,
        "left_of" to 67,
        "right_of" to 68,
        "signaltype" to 69,
        "+" to 70,
        "-" to 71,
        "()" to 72,
        "ACTOR" to 73,
        "config_object" to 74,
        "CONFIG_START" to 75,
        "CONFIG_CONTENT" to 76,
        "CONFIG_END" to 77,
        "SOLID_OPEN_ARROW" to 78,
        "DOTTED_OPEN_ARROW" to 79,
        "SOLID_ARROW" to 80,
        "SOLID_ARROW_TOP" to 81,
        "SOLID_ARROW_BOTTOM" to 82,
        "STICK_ARROW_TOP" to 83,
        "STICK_ARROW_BOTTOM" to 84,
        "SOLID_ARROW_TOP_DOTTED" to 85,
        "SOLID_ARROW_BOTTOM_DOTTED" to 86,
        "STICK_ARROW_TOP_DOTTED" to 87,
        "STICK_ARROW_BOTTOM_DOTTED" to 88,
        "SOLID_ARROW_TOP_REVERSE" to 89,
        "SOLID_ARROW_BOTTOM_REVERSE" to 90,
        "STICK_ARROW_TOP_REVERSE" to 91,
        "STICK_ARROW_BOTTOM_REVERSE" to 92,
        "SOLID_ARROW_TOP_REVERSE_DOTTED" to 93,
        "SOLID_ARROW_BOTTOM_REVERSE_DOTTED" to 94,
        "STICK_ARROW_TOP_REVERSE_DOTTED" to 95,
        "STICK_ARROW_BOTTOM_REVERSE_DOTTED" to 96,
        "BIDIRECTIONAL_SOLID_ARROW" to 97,
        "DOTTED_ARROW" to 98,
        "BIDIRECTIONAL_DOTTED_ARROW" to 99,
        "SOLID_CROSS" to 100,
        "DOTTED_CROSS" to 101,
        "SOLID_POINT" to 102,
        "DOTTED_POINT" to 103,
        "TXT" to 104,
        "\$accept" to 0,
        "\$end" to 1,
    )

    val terminalNames: Map<Int, String> = mapOf(
        2 to "error",
        4 to "SPACE",
        5 to "NEWLINE",
        6 to "SD",
        10 to "INVALID",
        14 to "create",
        15 to "box",
        16 to "restOfLine",
        17 to "end",
        19 to "autonumber",
        20 to "NUM",
        21 to "off",
        22 to "activate",
        24 to "deactivate",
        30 to "title",
        31 to "legacy_title",
        32 to "acc_title",
        33 to "acc_title_value",
        34 to "acc_descr",
        35 to "acc_descr_value",
        36 to "acc_descr_multiline_value",
        37 to "loop",
        38 to "rect",
        39 to "opt",
        40 to "alt",
        42 to "par",
        44 to "par_over",
        45 to "critical",
        47 to "break",
        48 to "option",
        49 to "and",
        50 to "else",
        51 to "participant",
        52 to "AS",
        53 to "participant_actor",
        54 to "destroy",
        56 to "note",
        59 to "over",
        61 to "links",
        62 to "link",
        63 to "properties",
        64 to "details",
        66 to ",",
        67 to "left_of",
        68 to "right_of",
        70 to "+",
        71 to "-",
        72 to "()",
        73 to "ACTOR",
        75 to "CONFIG_START",
        76 to "CONFIG_CONTENT",
        77 to "CONFIG_END",
        78 to "SOLID_OPEN_ARROW",
        79 to "DOTTED_OPEN_ARROW",
        80 to "SOLID_ARROW",
        81 to "SOLID_ARROW_TOP",
        82 to "SOLID_ARROW_BOTTOM",
        83 to "STICK_ARROW_TOP",
        84 to "STICK_ARROW_BOTTOM",
        85 to "SOLID_ARROW_TOP_DOTTED",
        86 to "SOLID_ARROW_BOTTOM_DOTTED",
        87 to "STICK_ARROW_TOP_DOTTED",
        88 to "STICK_ARROW_BOTTOM_DOTTED",
        89 to "SOLID_ARROW_TOP_REVERSE",
        90 to "SOLID_ARROW_BOTTOM_REVERSE",
        91 to "STICK_ARROW_TOP_REVERSE",
        92 to "STICK_ARROW_BOTTOM_REVERSE",
        93 to "SOLID_ARROW_TOP_REVERSE_DOTTED",
        94 to "SOLID_ARROW_BOTTOM_REVERSE_DOTTED",
        95 to "STICK_ARROW_TOP_REVERSE_DOTTED",
        96 to "STICK_ARROW_BOTTOM_REVERSE_DOTTED",
        97 to "BIDIRECTIONAL_SOLID_ARROW",
        98 to "DOTTED_ARROW",
        99 to "BIDIRECTIONAL_DOTTED_ARROW",
        100 to "SOLID_CROSS",
        101 to "DOTTED_CROSS",
        102 to "SOLID_POINT",
        103 to "DOTTED_POINT",
        104 to "TXT",
    )

    val productions: Array<SequenceJisonProduction> = arrayOf(
        SequenceJisonProduction(symbol = 0, length = 0),
        SequenceJisonProduction(symbol = 3, length = 2),
        SequenceJisonProduction(symbol = 3, length = 2),
        SequenceJisonProduction(symbol = 3, length = 2),
        SequenceJisonProduction(symbol = 7, length = 0),
        SequenceJisonProduction(symbol = 7, length = 2),
        SequenceJisonProduction(symbol = 8, length = 2),
        SequenceJisonProduction(symbol = 8, length = 1),
        SequenceJisonProduction(symbol = 8, length = 1),
        SequenceJisonProduction(symbol = 8, length = 1),
        SequenceJisonProduction(symbol = 11, length = 0),
        SequenceJisonProduction(symbol = 11, length = 2),
        SequenceJisonProduction(symbol = 12, length = 2),
        SequenceJisonProduction(symbol = 12, length = 1),
        SequenceJisonProduction(symbol = 12, length = 1),
        SequenceJisonProduction(symbol = 9, length = 1),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 3),
        SequenceJisonProduction(symbol = 9, length = 3),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 3),
        SequenceJisonProduction(symbol = 9, length = 3),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 1),
        SequenceJisonProduction(symbol = 9, length = 1),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 2),
        SequenceJisonProduction(symbol = 9, length = 1),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 9, length = 4),
        SequenceJisonProduction(symbol = 46, length = 1),
        SequenceJisonProduction(symbol = 46, length = 4),
        SequenceJisonProduction(symbol = 43, length = 1),
        SequenceJisonProduction(symbol = 43, length = 4),
        SequenceJisonProduction(symbol = 41, length = 1),
        SequenceJisonProduction(symbol = 41, length = 4),
        SequenceJisonProduction(symbol = 13, length = 5),
        SequenceJisonProduction(symbol = 13, length = 3),
        SequenceJisonProduction(symbol = 13, length = 5),
        SequenceJisonProduction(symbol = 13, length = 3),
        SequenceJisonProduction(symbol = 13, length = 3),
        SequenceJisonProduction(symbol = 13, length = 5),
        SequenceJisonProduction(symbol = 13, length = 3),
        SequenceJisonProduction(symbol = 13, length = 5),
        SequenceJisonProduction(symbol = 13, length = 3),
        SequenceJisonProduction(symbol = 25, length = 4),
        SequenceJisonProduction(symbol = 25, length = 4),
        SequenceJisonProduction(symbol = 26, length = 3),
        SequenceJisonProduction(symbol = 27, length = 3),
        SequenceJisonProduction(symbol = 28, length = 3),
        SequenceJisonProduction(symbol = 29, length = 3),
        SequenceJisonProduction(symbol = 65, length = 2),
        SequenceJisonProduction(symbol = 65, length = 1),
        SequenceJisonProduction(symbol = 60, length = 3),
        SequenceJisonProduction(symbol = 60, length = 1),
        SequenceJisonProduction(symbol = 57, length = 1),
        SequenceJisonProduction(symbol = 57, length = 1),
        SequenceJisonProduction(symbol = 18, length = 5),
        SequenceJisonProduction(symbol = 18, length = 5),
        SequenceJisonProduction(symbol = 18, length = 5),
        SequenceJisonProduction(symbol = 18, length = 5),
        SequenceJisonProduction(symbol = 18, length = 6),
        SequenceJisonProduction(symbol = 18, length = 4),
        SequenceJisonProduction(symbol = 55, length = 2),
        SequenceJisonProduction(symbol = 74, length = 3),
        SequenceJisonProduction(symbol = 23, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 69, length = 1),
        SequenceJisonProduction(symbol = 58, length = 1),
    )

    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).
    private val action0: SequenceJisonCell = SequenceJisonCell.Goto(1)
    private val action1: SequenceJisonCell = SequenceJisonCell.Shift(2)
    private val action2: SequenceJisonCell = SequenceJisonCell.Shift(3)
    private val action3: SequenceJisonCell = SequenceJisonCell.Shift(4)
    private val action4: SequenceJisonCell = SequenceJisonCell.Accept
    private val action5: SequenceJisonCell = SequenceJisonCell.Goto(5)
    private val action6: SequenceJisonCell = SequenceJisonCell.Goto(6)
    private val action7: SequenceJisonCell = SequenceJisonCell.Reduce(4)
    private val action8: SequenceJisonCell = SequenceJisonCell.Goto(7)
    private val action9: SequenceJisonCell = SequenceJisonCell.Reduce(1)
    private val action10: SequenceJisonCell = SequenceJisonCell.Reduce(2)
    private val action11: SequenceJisonCell = SequenceJisonCell.Reduce(3)
    private val action12: SequenceJisonCell = SequenceJisonCell.Shift(9)
    private val action13: SequenceJisonCell = SequenceJisonCell.Shift(11)
    private val action14: SequenceJisonCell = SequenceJisonCell.Goto(8)
    private val action15: SequenceJisonCell = SequenceJisonCell.Goto(10)
    private val action16: SequenceJisonCell = SequenceJisonCell.Shift(12)
    private val action17: SequenceJisonCell = SequenceJisonCell.Goto(13)
    private val action18: SequenceJisonCell = SequenceJisonCell.Shift(14)
    private val action19: SequenceJisonCell = SequenceJisonCell.Shift(15)
    private val action20: SequenceJisonCell = SequenceJisonCell.Goto(16)
    private val action21: SequenceJisonCell = SequenceJisonCell.Shift(17)
    private val action22: SequenceJisonCell = SequenceJisonCell.Shift(18)
    private val action23: SequenceJisonCell = SequenceJisonCell.Goto(41)
    private val action24: SequenceJisonCell = SequenceJisonCell.Shift(19)
    private val action25: SequenceJisonCell = SequenceJisonCell.Goto(20)
    private val action26: SequenceJisonCell = SequenceJisonCell.Goto(21)
    private val action27: SequenceJisonCell = SequenceJisonCell.Goto(22)
    private val action28: SequenceJisonCell = SequenceJisonCell.Goto(23)
    private val action29: SequenceJisonCell = SequenceJisonCell.Goto(24)
    private val action30: SequenceJisonCell = SequenceJisonCell.Shift(25)
    private val action31: SequenceJisonCell = SequenceJisonCell.Shift(26)
    private val action32: SequenceJisonCell = SequenceJisonCell.Shift(27)
    private val action33: SequenceJisonCell = SequenceJisonCell.Shift(28)
    private val action34: SequenceJisonCell = SequenceJisonCell.Shift(29)
    private val action35: SequenceJisonCell = SequenceJisonCell.Shift(30)
    private val action36: SequenceJisonCell = SequenceJisonCell.Shift(31)
    private val action37: SequenceJisonCell = SequenceJisonCell.Shift(32)
    private val action38: SequenceJisonCell = SequenceJisonCell.Shift(33)
    private val action39: SequenceJisonCell = SequenceJisonCell.Shift(34)
    private val action40: SequenceJisonCell = SequenceJisonCell.Shift(35)
    private val action41: SequenceJisonCell = SequenceJisonCell.Shift(36)
    private val action42: SequenceJisonCell = SequenceJisonCell.Shift(37)
    private val action43: SequenceJisonCell = SequenceJisonCell.Shift(38)
    private val action44: SequenceJisonCell = SequenceJisonCell.Shift(39)
    private val action45: SequenceJisonCell = SequenceJisonCell.Shift(40)
    private val action46: SequenceJisonCell = SequenceJisonCell.Shift(42)
    private val action47: SequenceJisonCell = SequenceJisonCell.Shift(43)
    private val action48: SequenceJisonCell = SequenceJisonCell.Shift(44)
    private val action49: SequenceJisonCell = SequenceJisonCell.Shift(45)
    private val action50: SequenceJisonCell = SequenceJisonCell.Shift(46)
    private val action51: SequenceJisonCell = SequenceJisonCell.Shift(47)
    private val action52: SequenceJisonCell = SequenceJisonCell.Reduce(5)
    private val action53: SequenceJisonCell = SequenceJisonCell.Goto(48)
    private val action54: SequenceJisonCell = SequenceJisonCell.Reduce(7)
    private val action55: SequenceJisonCell = SequenceJisonCell.Reduce(8)
    private val action56: SequenceJisonCell = SequenceJisonCell.Reduce(9)
    private val action57: SequenceJisonCell = SequenceJisonCell.Reduce(15)
    private val action58: SequenceJisonCell = SequenceJisonCell.Goto(49)
    private val action59: SequenceJisonCell = SequenceJisonCell.Shift(50)
    private val action60: SequenceJisonCell = SequenceJisonCell.Shift(51)
    private val action61: SequenceJisonCell = SequenceJisonCell.Shift(54)
    private val action62: SequenceJisonCell = SequenceJisonCell.Shift(52)
    private val action63: SequenceJisonCell = SequenceJisonCell.Shift(53)
    private val action64: SequenceJisonCell = SequenceJisonCell.Goto(55)
    private val action65: SequenceJisonCell = SequenceJisonCell.Goto(56)
    private val action66: SequenceJisonCell = SequenceJisonCell.Shift(57)
    private val action67: SequenceJisonCell = SequenceJisonCell.Shift(58)
    private val action68: SequenceJisonCell = SequenceJisonCell.Shift(59)
    private val action69: SequenceJisonCell = SequenceJisonCell.Shift(60)
    private val action70: SequenceJisonCell = SequenceJisonCell.Shift(61)
    private val action71: SequenceJisonCell = SequenceJisonCell.Reduce(30)
    private val action72: SequenceJisonCell = SequenceJisonCell.Reduce(31)
    private val action73: SequenceJisonCell = SequenceJisonCell.Shift(62)
    private val action74: SequenceJisonCell = SequenceJisonCell.Shift(63)
    private val action75: SequenceJisonCell = SequenceJisonCell.Reduce(34)
    private val action76: SequenceJisonCell = SequenceJisonCell.Shift(64)
    private val action77: SequenceJisonCell = SequenceJisonCell.Shift(65)
    private val action78: SequenceJisonCell = SequenceJisonCell.Shift(66)
    private val action79: SequenceJisonCell = SequenceJisonCell.Shift(67)
    private val action80: SequenceJisonCell = SequenceJisonCell.Shift(68)
    private val action81: SequenceJisonCell = SequenceJisonCell.Shift(69)
    private val action82: SequenceJisonCell = SequenceJisonCell.Shift(70)
    private val action83: SequenceJisonCell = SequenceJisonCell.Shift(71)
    private val action84: SequenceJisonCell = SequenceJisonCell.Goto(72)
    private val action85: SequenceJisonCell = SequenceJisonCell.Goto(73)
    private val action86: SequenceJisonCell = SequenceJisonCell.Shift(74)
    private val action87: SequenceJisonCell = SequenceJisonCell.Goto(75)
    private val action88: SequenceJisonCell = SequenceJisonCell.Goto(76)
    private val action89: SequenceJisonCell = SequenceJisonCell.Goto(77)
    private val action90: SequenceJisonCell = SequenceJisonCell.Goto(78)
    private val action91: SequenceJisonCell = SequenceJisonCell.Shift(79)
    private val action92: SequenceJisonCell = SequenceJisonCell.Shift(80)
    private val action93: SequenceJisonCell = SequenceJisonCell.Shift(81)
    private val action94: SequenceJisonCell = SequenceJisonCell.Shift(82)
    private val action95: SequenceJisonCell = SequenceJisonCell.Shift(83)
    private val action96: SequenceJisonCell = SequenceJisonCell.Shift(84)
    private val action97: SequenceJisonCell = SequenceJisonCell.Shift(85)
    private val action98: SequenceJisonCell = SequenceJisonCell.Shift(86)
    private val action99: SequenceJisonCell = SequenceJisonCell.Shift(87)
    private val action100: SequenceJisonCell = SequenceJisonCell.Shift(88)
    private val action101: SequenceJisonCell = SequenceJisonCell.Shift(89)
    private val action102: SequenceJisonCell = SequenceJisonCell.Shift(90)
    private val action103: SequenceJisonCell = SequenceJisonCell.Shift(91)
    private val action104: SequenceJisonCell = SequenceJisonCell.Shift(92)
    private val action105: SequenceJisonCell = SequenceJisonCell.Shift(93)
    private val action106: SequenceJisonCell = SequenceJisonCell.Shift(94)
    private val action107: SequenceJisonCell = SequenceJisonCell.Shift(95)
    private val action108: SequenceJisonCell = SequenceJisonCell.Shift(96)
    private val action109: SequenceJisonCell = SequenceJisonCell.Shift(97)
    private val action110: SequenceJisonCell = SequenceJisonCell.Shift(98)
    private val action111: SequenceJisonCell = SequenceJisonCell.Shift(99)
    private val action112: SequenceJisonCell = SequenceJisonCell.Shift(100)
    private val action113: SequenceJisonCell = SequenceJisonCell.Shift(101)
    private val action114: SequenceJisonCell = SequenceJisonCell.Shift(102)
    private val action115: SequenceJisonCell = SequenceJisonCell.Shift(103)
    private val action116: SequenceJisonCell = SequenceJisonCell.Shift(104)
    private val action117: SequenceJisonCell = SequenceJisonCell.Shift(105)
    private val action118: SequenceJisonCell = SequenceJisonCell.Goto(106)
    private val action119: SequenceJisonCell = SequenceJisonCell.Shift(107)
    private val action120: SequenceJisonCell = SequenceJisonCell.Shift(108)
    private val action121: SequenceJisonCell = SequenceJisonCell.Shift(109)
    private val action122: SequenceJisonCell = SequenceJisonCell.Goto(110)
    private val action123: SequenceJisonCell = SequenceJisonCell.Goto(111)
    private val action124: SequenceJisonCell = SequenceJisonCell.Goto(112)
    private val action125: SequenceJisonCell = SequenceJisonCell.Goto(113)
    private val action126: SequenceJisonCell = SequenceJisonCell.Reduce(78)
    private val action127: SequenceJisonCell = SequenceJisonCell.Reduce(6)
    private val action128: SequenceJisonCell = SequenceJisonCell.Reduce(16)
    private val action129: SequenceJisonCell = SequenceJisonCell.Reduce(10)
    private val action130: SequenceJisonCell = SequenceJisonCell.Goto(114)
    private val action131: SequenceJisonCell = SequenceJisonCell.Reduce(18)
    private val action132: SequenceJisonCell = SequenceJisonCell.Shift(116)
    private val action133: SequenceJisonCell = SequenceJisonCell.Shift(115)
    private val action134: SequenceJisonCell = SequenceJisonCell.Shift(117)
    private val action135: SequenceJisonCell = SequenceJisonCell.Reduce(22)
    private val action136: SequenceJisonCell = SequenceJisonCell.Shift(118)
    private val action137: SequenceJisonCell = SequenceJisonCell.Shift(119)
    private val action138: SequenceJisonCell = SequenceJisonCell.Reduce(25)
    private val action139: SequenceJisonCell = SequenceJisonCell.Reduce(26)
    private val action140: SequenceJisonCell = SequenceJisonCell.Reduce(27)
    private val action141: SequenceJisonCell = SequenceJisonCell.Reduce(28)
    private val action142: SequenceJisonCell = SequenceJisonCell.Reduce(29)
    private val action143: SequenceJisonCell = SequenceJisonCell.Reduce(32)
    private val action144: SequenceJisonCell = SequenceJisonCell.Reduce(33)
    private val action145: SequenceJisonCell = SequenceJisonCell.Goto(120)
    private val action146: SequenceJisonCell = SequenceJisonCell.Goto(121)
    private val action147: SequenceJisonCell = SequenceJisonCell.Goto(122)
    private val action148: SequenceJisonCell = SequenceJisonCell.Goto(124)
    private val action149: SequenceJisonCell = SequenceJisonCell.Goto(123)
    private val action150: SequenceJisonCell = SequenceJisonCell.Goto(126)
    private val action151: SequenceJisonCell = SequenceJisonCell.Goto(125)
    private val action152: SequenceJisonCell = SequenceJisonCell.Goto(127)
    private val action153: SequenceJisonCell = SequenceJisonCell.Goto(129)
    private val action154: SequenceJisonCell = SequenceJisonCell.Goto(128)
    private val action155: SequenceJisonCell = SequenceJisonCell.Goto(130)
    private val action156: SequenceJisonCell = SequenceJisonCell.Shift(132)
    private val action157: SequenceJisonCell = SequenceJisonCell.Shift(131)
    private val action158: SequenceJisonCell = SequenceJisonCell.Shift(134)
    private val action159: SequenceJisonCell = SequenceJisonCell.Shift(133)
    private val action160: SequenceJisonCell = SequenceJisonCell.Goto(135)
    private val action161: SequenceJisonCell = SequenceJisonCell.Shift(136)
    private val action162: SequenceJisonCell = SequenceJisonCell.Shift(138)
    private val action163: SequenceJisonCell = SequenceJisonCell.Shift(137)
    private val action164: SequenceJisonCell = SequenceJisonCell.Shift(140)
    private val action165: SequenceJisonCell = SequenceJisonCell.Shift(139)
    private val action166: SequenceJisonCell = SequenceJisonCell.Shift(141)
    private val action167: SequenceJisonCell = SequenceJisonCell.Goto(145)
    private val action168: SequenceJisonCell = SequenceJisonCell.Shift(142)
    private val action169: SequenceJisonCell = SequenceJisonCell.Shift(143)
    private val action170: SequenceJisonCell = SequenceJisonCell.Shift(144)
    private val action171: SequenceJisonCell = SequenceJisonCell.Goto(146)
    private val action172: SequenceJisonCell = SequenceJisonCell.Reduce(79)
    private val action173: SequenceJisonCell = SequenceJisonCell.Reduce(80)
    private val action174: SequenceJisonCell = SequenceJisonCell.Reduce(81)
    private val action175: SequenceJisonCell = SequenceJisonCell.Reduce(82)
    private val action176: SequenceJisonCell = SequenceJisonCell.Reduce(83)
    private val action177: SequenceJisonCell = SequenceJisonCell.Reduce(84)
    private val action178: SequenceJisonCell = SequenceJisonCell.Reduce(85)
    private val action179: SequenceJisonCell = SequenceJisonCell.Reduce(86)
    private val action180: SequenceJisonCell = SequenceJisonCell.Reduce(87)
    private val action181: SequenceJisonCell = SequenceJisonCell.Reduce(88)
    private val action182: SequenceJisonCell = SequenceJisonCell.Reduce(89)
    private val action183: SequenceJisonCell = SequenceJisonCell.Reduce(90)
    private val action184: SequenceJisonCell = SequenceJisonCell.Reduce(91)
    private val action185: SequenceJisonCell = SequenceJisonCell.Reduce(92)
    private val action186: SequenceJisonCell = SequenceJisonCell.Reduce(93)
    private val action187: SequenceJisonCell = SequenceJisonCell.Reduce(94)
    private val action188: SequenceJisonCell = SequenceJisonCell.Reduce(95)
    private val action189: SequenceJisonCell = SequenceJisonCell.Reduce(96)
    private val action190: SequenceJisonCell = SequenceJisonCell.Reduce(97)
    private val action191: SequenceJisonCell = SequenceJisonCell.Reduce(98)
    private val action192: SequenceJisonCell = SequenceJisonCell.Reduce(99)
    private val action193: SequenceJisonCell = SequenceJisonCell.Reduce(100)
    private val action194: SequenceJisonCell = SequenceJisonCell.Reduce(101)
    private val action195: SequenceJisonCell = SequenceJisonCell.Reduce(102)
    private val action196: SequenceJisonCell = SequenceJisonCell.Reduce(103)
    private val action197: SequenceJisonCell = SequenceJisonCell.Reduce(104)
    private val action198: SequenceJisonCell = SequenceJisonCell.Goto(147)
    private val action199: SequenceJisonCell = SequenceJisonCell.Goto(149)
    private val action200: SequenceJisonCell = SequenceJisonCell.Goto(148)
    private val action201: SequenceJisonCell = SequenceJisonCell.Reduce(68)
    private val action202: SequenceJisonCell = SequenceJisonCell.Reduce(69)
    private val action203: SequenceJisonCell = SequenceJisonCell.Goto(150)
    private val action204: SequenceJisonCell = SequenceJisonCell.Shift(151)
    private val action205: SequenceJisonCell = SequenceJisonCell.Goto(152)
    private val action206: SequenceJisonCell = SequenceJisonCell.Goto(153)
    private val action207: SequenceJisonCell = SequenceJisonCell.Goto(154)
    private val action208: SequenceJisonCell = SequenceJisonCell.Shift(157)
    private val action209: SequenceJisonCell = SequenceJisonCell.Shift(159)
    private val action210: SequenceJisonCell = SequenceJisonCell.Goto(156)
    private val action211: SequenceJisonCell = SequenceJisonCell.Goto(158)
    private val action212: SequenceJisonCell = SequenceJisonCell.Shift(155)
    private val action213: SequenceJisonCell = SequenceJisonCell.Shift(160)
    private val action214: SequenceJisonCell = SequenceJisonCell.Reduce(20)
    private val action215: SequenceJisonCell = SequenceJisonCell.Reduce(21)
    private val action216: SequenceJisonCell = SequenceJisonCell.Reduce(23)
    private val action217: SequenceJisonCell = SequenceJisonCell.Reduce(24)
    private val action218: SequenceJisonCell = SequenceJisonCell.Shift(161)
    private val action219: SequenceJisonCell = SequenceJisonCell.Shift(162)
    private val action220: SequenceJisonCell = SequenceJisonCell.Shift(163)
    private val action221: SequenceJisonCell = SequenceJisonCell.Shift(164)
    private val action222: SequenceJisonCell = SequenceJisonCell.Reduce(47)
    private val action223: SequenceJisonCell = SequenceJisonCell.Shift(165)
    private val action224: SequenceJisonCell = SequenceJisonCell.Shift(166)
    private val action225: SequenceJisonCell = SequenceJisonCell.Reduce(45)
    private val action226: SequenceJisonCell = SequenceJisonCell.Shift(167)
    private val action227: SequenceJisonCell = SequenceJisonCell.Shift(168)
    private val action228: SequenceJisonCell = SequenceJisonCell.Shift(169)
    private val action229: SequenceJisonCell = SequenceJisonCell.Reduce(43)
    private val action230: SequenceJisonCell = SequenceJisonCell.Shift(170)
    private val action231: SequenceJisonCell = SequenceJisonCell.Shift(171)
    private val action232: SequenceJisonCell = SequenceJisonCell.Shift(172)
    private val action233: SequenceJisonCell = SequenceJisonCell.Reduce(50)
    private val action234: SequenceJisonCell = SequenceJisonCell.Shift(173)
    private val action235: SequenceJisonCell = SequenceJisonCell.Reduce(55)
    private val action236: SequenceJisonCell = SequenceJisonCell.Reduce(76)
    private val action237: SequenceJisonCell = SequenceJisonCell.Shift(174)
    private val action238: SequenceJisonCell = SequenceJisonCell.Shift(175)
    private val action239: SequenceJisonCell = SequenceJisonCell.Reduce(52)
    private val action240: SequenceJisonCell = SequenceJisonCell.Shift(176)
    private val action241: SequenceJisonCell = SequenceJisonCell.Reduce(57)
    private val action242: SequenceJisonCell = SequenceJisonCell.Reduce(53)
    private val action243: SequenceJisonCell = SequenceJisonCell.Goto(177)
    private val action244: SequenceJisonCell = SequenceJisonCell.Goto(178)
    private val action245: SequenceJisonCell = SequenceJisonCell.Goto(179)
    private val action246: SequenceJisonCell = SequenceJisonCell.Goto(180)
    private val action247: SequenceJisonCell = SequenceJisonCell.Goto(181)
    private val action248: SequenceJisonCell = SequenceJisonCell.Shift(182)
    private val action249: SequenceJisonCell = SequenceJisonCell.Goto(183)
    private val action250: SequenceJisonCell = SequenceJisonCell.Goto(184)
    private val action251: SequenceJisonCell = SequenceJisonCell.Shift(185)
    private val action252: SequenceJisonCell = SequenceJisonCell.Reduce(67)
    private val action253: SequenceJisonCell = SequenceJisonCell.Reduce(60)
    private val action254: SequenceJisonCell = SequenceJisonCell.Reduce(105)
    private val action255: SequenceJisonCell = SequenceJisonCell.Reduce(61)
    private val action256: SequenceJisonCell = SequenceJisonCell.Reduce(62)
    private val action257: SequenceJisonCell = SequenceJisonCell.Reduce(63)
    private val action258: SequenceJisonCell = SequenceJisonCell.Reduce(17)
    private val action259: SequenceJisonCell = SequenceJisonCell.Reduce(11)
    private val action260: SequenceJisonCell = SequenceJisonCell.Goto(186)
    private val action261: SequenceJisonCell = SequenceJisonCell.Reduce(13)
    private val action262: SequenceJisonCell = SequenceJisonCell.Reduce(14)
    private val action263: SequenceJisonCell = SequenceJisonCell.Reduce(19)
    private val action264: SequenceJisonCell = SequenceJisonCell.Reduce(35)
    private val action265: SequenceJisonCell = SequenceJisonCell.Reduce(36)
    private val action266: SequenceJisonCell = SequenceJisonCell.Reduce(37)
    private val action267: SequenceJisonCell = SequenceJisonCell.Reduce(38)
    private val action268: SequenceJisonCell = SequenceJisonCell.Shift(187)
    private val action269: SequenceJisonCell = SequenceJisonCell.Reduce(39)
    private val action270: SequenceJisonCell = SequenceJisonCell.Shift(188)
    private val action271: SequenceJisonCell = SequenceJisonCell.Reduce(40)
    private val action272: SequenceJisonCell = SequenceJisonCell.Reduce(41)
    private val action273: SequenceJisonCell = SequenceJisonCell.Shift(189)
    private val action274: SequenceJisonCell = SequenceJisonCell.Reduce(42)
    private val action275: SequenceJisonCell = SequenceJisonCell.Shift(190)
    private val action276: SequenceJisonCell = SequenceJisonCell.Shift(191)
    private val action277: SequenceJisonCell = SequenceJisonCell.Shift(192)
    private val action278: SequenceJisonCell = SequenceJisonCell.Shift(193)
    private val action279: SequenceJisonCell = SequenceJisonCell.Shift(194)
    private val action280: SequenceJisonCell = SequenceJisonCell.Goto(195)
    private val action281: SequenceJisonCell = SequenceJisonCell.Goto(196)
    private val action282: SequenceJisonCell = SequenceJisonCell.Goto(197)
    private val action283: SequenceJisonCell = SequenceJisonCell.Reduce(75)
    private val action284: SequenceJisonCell = SequenceJisonCell.Goto(198)
    private val action285: SequenceJisonCell = SequenceJisonCell.Goto(199)
    private val action286: SequenceJisonCell = SequenceJisonCell.Reduce(58)
    private val action287: SequenceJisonCell = SequenceJisonCell.Reduce(59)
    private val action288: SequenceJisonCell = SequenceJisonCell.Goto(200)
    private val action289: SequenceJisonCell = SequenceJisonCell.Reduce(12)
    private val action290: SequenceJisonCell = SequenceJisonCell.Goto(201)
    private val action291: SequenceJisonCell = SequenceJisonCell.Goto(202)
    private val action292: SequenceJisonCell = SequenceJisonCell.Goto(203)
    private val action293: SequenceJisonCell = SequenceJisonCell.Reduce(49)
    private val action294: SequenceJisonCell = SequenceJisonCell.Reduce(54)
    private val action295: SequenceJisonCell = SequenceJisonCell.Reduce(77)
    private val action296: SequenceJisonCell = SequenceJisonCell.Reduce(51)
    private val action297: SequenceJisonCell = SequenceJisonCell.Reduce(56)
    private val action298: SequenceJisonCell = SequenceJisonCell.Reduce(70)
    private val action299: SequenceJisonCell = SequenceJisonCell.Reduce(71)
    private val action300: SequenceJisonCell = SequenceJisonCell.Reduce(72)
    private val action301: SequenceJisonCell = SequenceJisonCell.Reduce(73)
    private val action302: SequenceJisonCell = SequenceJisonCell.Goto(204)
    private val action303: SequenceJisonCell = SequenceJisonCell.Reduce(66)
    private val action304: SequenceJisonCell = SequenceJisonCell.Reduce(48)
    private val action305: SequenceJisonCell = SequenceJisonCell.Reduce(46)
    private val action306: SequenceJisonCell = SequenceJisonCell.Reduce(44)
    private val action307: SequenceJisonCell = SequenceJisonCell.Reduce(74)
    private val symbols0 = intArrayOf(1, 4, 5, 7, 10, 14, 15, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 42, 44, 45, 47, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols1 = intArrayOf(1, 4, 5, 10, 14, 15, 17, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 42, 44, 45, 47, 48, 49, 50, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols2 = intArrayOf(5, 66, 72, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104)
    private val symbols3 = intArrayOf(4, 5, 11, 17, 51, 53, 54)
    private val symbols4 = intArrayOf(4, 5, 7, 10, 14, 15, 17, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 42, 44, 45, 47, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols5 = intArrayOf(4, 5, 7, 10, 14, 15, 17, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 41, 42, 44, 45, 47, 50, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols6 = intArrayOf(4, 5, 7, 10, 14, 15, 17, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 42, 43, 44, 45, 47, 49, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols7 = intArrayOf(4, 5, 7, 10, 14, 15, 17, 19, 22, 24, 30, 31, 32, 34, 36, 37, 38, 39, 40, 42, 44, 45, 46, 47, 48, 51, 53, 54, 56, 61, 62, 63, 64, 73)
    private val symbols8 = intArrayOf(70, 71, 72, 73)
    private val symbols9 = intArrayOf(4, 5, 17, 51, 53, 54)

    val states: Array<Map<Int, SequenceJisonCell>> = buildList {
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
    }.toTypedArray()

    private fun stateChunk0(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
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
            13 to action17,
            14 to action18,
            15 to action19,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        buildJisonState(symbols1, action52),
        mapOf(
            9 to action53,
            13 to action17,
            14 to action18,
            15 to action19,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        buildJisonState(symbols1, action54),
        buildJisonState(symbols1, action55),
        buildJisonState(symbols1, action56),
        buildJisonState(symbols1, action57),
        mapOf(
            13 to action58,
            51 to action43,
            53 to action44,
            54 to action45,
        ),
        mapOf(
            16 to action59,
        ),
        mapOf(
            5 to action60,
        ),
        mapOf(
            5 to action61,
            20 to action62,
            21 to action63,
        ),
        mapOf(
            23 to action64,
            73 to action51,
        ),
        mapOf(
            23 to action65,
            73 to action51,
        ),
    )

    private fun stateChunk1(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        mapOf(
            5 to action66,
        ),
        mapOf(
            5 to action67,
        ),
        mapOf(
            5 to action68,
        ),
        mapOf(
            5 to action69,
        ),
        mapOf(
            5 to action70,
        ),
        buildJisonState(symbols1, action71),
        buildJisonState(symbols1, action72),
        mapOf(
            33 to action73,
        ),
        mapOf(
            35 to action74,
        ),
        buildJisonState(symbols1, action75),
        mapOf(
            16 to action76,
        ),
        mapOf(
            16 to action77,
        ),
        mapOf(
            16 to action78,
        ),
        mapOf(
            16 to action79,
        ),
        mapOf(
            16 to action80,
        ),
        mapOf(
            16 to action81,
        ),
        mapOf(
            16 to action82,
        ),
        mapOf(
            16 to action83,
        ),
        mapOf(
            23 to action84,
            55 to action85,
            73 to action86,
        ),
        mapOf(
            23 to action87,
            55 to action88,
            73 to action86,
        ),
    )

    private fun stateChunk2(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        mapOf(
            23 to action89,
            73 to action51,
        ),
        mapOf(
            69 to action90,
            72 to action91,
            78 to action92,
            79 to action93,
            80 to action94,
            81 to action95,
            82 to action96,
            83 to action97,
            84 to action98,
            85 to action99,
            86 to action100,
            87 to action101,
            88 to action102,
            89 to action103,
            90 to action104,
            91 to action105,
            92 to action106,
            93 to action107,
            94 to action108,
            95 to action109,
            96 to action110,
            97 to action111,
            98 to action112,
            99 to action113,
            100 to action114,
            101 to action115,
            102 to action116,
            103 to action117,
        ),
        mapOf(
            57 to action118,
            59 to action119,
            67 to action120,
            68 to action121,
        ),
        mapOf(
            23 to action122,
            73 to action51,
        ),
        mapOf(
            23 to action123,
            73 to action51,
        ),
        mapOf(
            23 to action124,
            73 to action51,
        ),
        mapOf(
            23 to action125,
            73 to action51,
        ),
        buildJisonState(symbols2, action126),
        buildJisonState(symbols1, action127),
        buildJisonState(symbols1, action128),
        buildJisonState(symbols3, action129, mapOf(
            11 to action130,
        )),
        buildJisonState(symbols1, action131),
        mapOf(
            5 to action132,
            20 to action133,
        ),
        mapOf(
            5 to action134,
        ),
        buildJisonState(symbols1, action135),
        mapOf(
            5 to action136,
        ),
        mapOf(
            5 to action137,
        ),
        buildJisonState(symbols1, action138),
        buildJisonState(symbols1, action139),
        buildJisonState(symbols1, action140),
    )

    private fun stateChunk3(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        buildJisonState(symbols1, action141),
        buildJisonState(symbols1, action142),
        buildJisonState(symbols1, action143),
        buildJisonState(symbols1, action144),
        buildJisonState(symbols4, action7, mapOf(
            7 to action145,
        )),
        buildJisonState(symbols4, action7, mapOf(
            7 to action146,
        )),
        buildJisonState(symbols4, action7, mapOf(
            7 to action147,
        )),
        buildJisonState(symbols5, action7, mapOf(
            7 to action148,
            41 to action149,
        )),
        buildJisonState(symbols6, action7, mapOf(
            7 to action150,
            43 to action151,
        )),
        buildJisonState(symbols6, action7, mapOf(
            7 to action150,
            43 to action152,
        )),
        buildJisonState(symbols7, action7, mapOf(
            7 to action153,
            46 to action154,
        )),
        buildJisonState(symbols4, action7, mapOf(
            7 to action155,
        )),
        mapOf(
            5 to action156,
            52 to action157,
        ),
        mapOf(
            5 to action158,
            52 to action159,
        ),
        mapOf(
            5 to action126,
            52 to action126,
            74 to action160,
            75 to action161,
        ),
        mapOf(
            5 to action162,
            52 to action163,
        ),
        mapOf(
            5 to action164,
            52 to action165,
        ),
        mapOf(
            5 to action166,
        ),
        mapOf(
            23 to action167,
            70 to action168,
            71 to action169,
            72 to action170,
            73 to action51,
        ),
        mapOf(
            69 to action171,
            78 to action92,
            79 to action93,
            80 to action94,
            81 to action95,
            82 to action96,
            83 to action97,
            84 to action98,
            85 to action99,
            86 to action100,
            87 to action101,
            88 to action102,
            89 to action103,
            90 to action104,
            91 to action105,
            92 to action106,
            93 to action107,
            94 to action108,
            95 to action109,
            96 to action110,
            97 to action111,
            98 to action112,
            99 to action113,
            100 to action114,
            101 to action115,
            102 to action116,
            103 to action117,
        ),
    )

    private fun stateChunk4(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        buildJisonState(symbols8, action172),
        buildJisonState(symbols8, action173),
        buildJisonState(symbols8, action174),
        buildJisonState(symbols8, action175),
        buildJisonState(symbols8, action176),
        buildJisonState(symbols8, action177),
        buildJisonState(symbols8, action178),
        buildJisonState(symbols8, action179),
        buildJisonState(symbols8, action180),
        buildJisonState(symbols8, action181),
        buildJisonState(symbols8, action182),
        buildJisonState(symbols8, action183),
        buildJisonState(symbols8, action184),
        buildJisonState(symbols8, action185),
        buildJisonState(symbols8, action186),
        buildJisonState(symbols8, action187),
        buildJisonState(symbols8, action188),
        buildJisonState(symbols8, action189),
        buildJisonState(symbols8, action190),
        buildJisonState(symbols8, action191),
    )

    private fun stateChunk5(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        buildJisonState(symbols8, action192),
        buildJisonState(symbols8, action193),
        buildJisonState(symbols8, action194),
        buildJisonState(symbols8, action195),
        buildJisonState(symbols8, action196),
        buildJisonState(symbols8, action197),
        mapOf(
            23 to action198,
            73 to action51,
        ),
        mapOf(
            23 to action199,
            60 to action200,
            73 to action51,
        ),
        mapOf(
            73 to action201,
        ),
        mapOf(
            73 to action202,
        ),
        mapOf(
            58 to action203,
            104 to action204,
        ),
        mapOf(
            58 to action205,
            104 to action204,
        ),
        mapOf(
            58 to action206,
            104 to action204,
        ),
        mapOf(
            58 to action207,
            104 to action204,
        ),
        mapOf(
            4 to action208,
            5 to action209,
            12 to action210,
            13 to action211,
            17 to action212,
            51 to action43,
            53 to action44,
            54 to action45,
        ),
        mapOf(
            5 to action213,
        ),
        buildJisonState(symbols1, action214),
        buildJisonState(symbols1, action215),
        buildJisonState(symbols1, action216),
        buildJisonState(symbols1, action217),
    )

    private fun stateChunk6(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action218,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action219,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action220,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            17 to action221,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action222,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            50 to action223,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            17 to action224,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action225,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            49 to action226,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            17 to action227,
        ),
        mapOf(
            17 to action228,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action229,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            48 to action230,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            4 to action12,
            5 to action13,
            8 to action14,
            9 to action15,
            10 to action16,
            13 to action17,
            14 to action18,
            15 to action19,
            17 to action231,
            18 to action20,
            19 to action21,
            22 to action22,
            23 to action23,
            24 to action24,
            25 to action25,
            26 to action26,
            27 to action27,
            28 to action28,
            29 to action29,
            30 to action30,
            31 to action31,
            32 to action32,
            34 to action33,
            36 to action34,
            37 to action35,
            38 to action36,
            39 to action37,
            40 to action38,
            42 to action39,
            44 to action40,
            45 to action41,
            47 to action42,
            51 to action43,
            53 to action44,
            54 to action45,
            56 to action46,
            61 to action47,
            62 to action48,
            63 to action49,
            64 to action50,
            73 to action51,
        ),
        mapOf(
            16 to action232,
        ),
        buildJisonState(symbols1, action233),
        mapOf(
            16 to action234,
        ),
        buildJisonState(symbols1, action235),
        mapOf(
            5 to action236,
            52 to action236,
        ),
        mapOf(
            76 to action237,
        ),
        mapOf(
            16 to action238,
        ),
        buildJisonState(symbols1, action239),
        mapOf(
            16 to action240,
        ),
    )

    private fun stateChunk7(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        buildJisonState(symbols1, action241),
        buildJisonState(symbols1, action242),
        mapOf(
            23 to action243,
            73 to action51,
        ),
        mapOf(
            23 to action244,
            73 to action51,
        ),
        mapOf(
            23 to action245,
            73 to action51,
        ),
        mapOf(
            58 to action246,
            104 to action204,
        ),
        mapOf(
            23 to action247,
            72 to action248,
            73 to action51,
        ),
        mapOf(
            58 to action249,
            104 to action204,
        ),
        mapOf(
            58 to action250,
            104 to action204,
        ),
        mapOf(
            66 to action251,
            104 to action252,
        ),
        mapOf(
            5 to action253,
        ),
        mapOf(
            5 to action254,
        ),
        mapOf(
            5 to action255,
        ),
        mapOf(
            5 to action256,
        ),
        mapOf(
            5 to action257,
        ),
        buildJisonState(symbols1, action258),
        buildJisonState(symbols9, action259),
        mapOf(
            13 to action260,
            51 to action43,
            53 to action44,
            54 to action45,
        ),
        buildJisonState(symbols9, action261),
        buildJisonState(symbols9, action262),
    )

    private fun stateChunk8(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        buildJisonState(symbols1, action263),
        buildJisonState(symbols1, action264),
        buildJisonState(symbols1, action265),
        buildJisonState(symbols1, action266),
        buildJisonState(symbols1, action267),
        mapOf(
            16 to action268,
        ),
        buildJisonState(symbols1, action269),
        mapOf(
            16 to action270,
        ),
        buildJisonState(symbols1, action271),
        buildJisonState(symbols1, action272),
        mapOf(
            16 to action273,
        ),
        buildJisonState(symbols1, action274),
        mapOf(
            5 to action275,
        ),
        mapOf(
            5 to action276,
        ),
        mapOf(
            77 to action277,
        ),
        mapOf(
            5 to action278,
        ),
        mapOf(
            5 to action279,
        ),
        mapOf(
            58 to action280,
            104 to action204,
        ),
        mapOf(
            58 to action281,
            104 to action204,
        ),
        mapOf(
            58 to action282,
            104 to action204,
        ),
    )

    private fun stateChunk9(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        mapOf(
            5 to action283,
        ),
        mapOf(
            58 to action284,
            104 to action204,
        ),
        mapOf(
            23 to action285,
            73 to action51,
        ),
        mapOf(
            5 to action286,
        ),
        mapOf(
            5 to action287,
        ),
        mapOf(
            23 to action288,
            73 to action51,
        ),
        buildJisonState(symbols9, action289),
        buildJisonState(symbols5, action7, mapOf(
            7 to action148,
            41 to action290,
        )),
        buildJisonState(symbols6, action7, mapOf(
            7 to action150,
            43 to action291,
        )),
        buildJisonState(symbols7, action7, mapOf(
            7 to action153,
            46 to action292,
        )),
        buildJisonState(symbols1, action293),
        buildJisonState(symbols1, action294),
        mapOf(
            5 to action295,
            52 to action295,
        ),
        buildJisonState(symbols1, action296),
        buildJisonState(symbols1, action297),
        mapOf(
            5 to action298,
        ),
        mapOf(
            5 to action299,
        ),
        mapOf(
            5 to action300,
        ),
        mapOf(
            5 to action301,
        ),
        mapOf(
            58 to action302,
            104 to action204,
        ),
    )

    private fun stateChunk10(): Array<Map<Int, SequenceJisonCell>> = arrayOf(
        mapOf(
            104 to action303,
        ),
        mapOf(
            17 to action304,
        ),
        mapOf(
            17 to action305,
        ),
        mapOf(
            17 to action306,
        ),
        mapOf(
            5 to action307,
        ),
    )

    val lexerPatterns: List<SequenceJisonLexerPattern> = buildList {
        addAll(lexerChunk0())
        addAll(lexerChunk1())
        addAll(lexerChunk2())
        addAll(lexerChunk3())
        addAll(lexerChunk4())
    }

    private fun lexerChunk0(): List<SequenceJisonLexerPattern> = listOf(
        SequenceJisonLexerPattern(0, "^(?:[\\n]+)"),
        SequenceJisonLexerPattern(1, "^(?:\\s+)"),
        SequenceJisonLexerPattern(2, "^(?:((?!\\n)\\s)+)"),
        SequenceJisonLexerPattern(3, "^(?:#[^\\n]*)"),
        SequenceJisonLexerPattern(4, "^(?:%(?!\\{)[^\\n]*)"),
        SequenceJisonLexerPattern(5, "^(?:[^\\}]%%[^\\n]*)"),
        SequenceJisonLexerPattern(6, "^(?:([0-9]+(\\.[0-9]{1,2})?|\\.[0-9]{1,2})(?=[ \\n]+))"),
        SequenceJisonLexerPattern(7, "^(?:@\\{)"),
        SequenceJisonLexerPattern(8, "^(?:[^\\}]+)"),
        SequenceJisonLexerPattern(9, "^(?:\\}(?=\\s+as\\s))"),
        SequenceJisonLexerPattern(10, "^(?:\\})"),
        SequenceJisonLexerPattern(11, "^(?:[^\\<->\\->:\\n,;@\\s]+(?=@\\{))"),
        SequenceJisonLexerPattern(12, "^(?:[^<>:\\n,;@\\s]+(?=\\s+as\\s))"),
        SequenceJisonLexerPattern(13, "^(?:[^<>:\\n,;@]+(?=\\s*[\\n;#]|\$))"),
        SequenceJisonLexerPattern(14, "^(?:[^<>:\\n,;@]*<[^\\n]*)"),
        SequenceJisonLexerPattern(15, "^(?:[^\\n]+)"),
        SequenceJisonLexerPattern(16, "^(?:box\\b)"),
        SequenceJisonLexerPattern(17, "^(?:participant\\b)"),
        SequenceJisonLexerPattern(18, "^(?:actor\\b)"),
        SequenceJisonLexerPattern(19, "^(?:create\\b)"),
    )

    private fun lexerChunk1(): List<SequenceJisonLexerPattern> = listOf(
        SequenceJisonLexerPattern(20, "^(?:destroy\\b)"),
        SequenceJisonLexerPattern(21, "^(?:as\\b)"),
        SequenceJisonLexerPattern(22, "^(?:(?:))"),
        SequenceJisonLexerPattern(23, "^(?:loop\\b)"),
        SequenceJisonLexerPattern(24, "^(?:rect\\b)"),
        SequenceJisonLexerPattern(25, "^(?:opt\\b)"),
        SequenceJisonLexerPattern(26, "^(?:alt\\b)"),
        SequenceJisonLexerPattern(27, "^(?:else\\b)"),
        SequenceJisonLexerPattern(28, "^(?:par\\b)"),
        SequenceJisonLexerPattern(29, "^(?:par_over\\b)"),
        SequenceJisonLexerPattern(30, "^(?:and\\b)"),
        SequenceJisonLexerPattern(31, "^(?:critical\\b)"),
        SequenceJisonLexerPattern(32, "^(?:option\\b)"),
        SequenceJisonLexerPattern(33, "^(?:break\\b)"),
        SequenceJisonLexerPattern(34, "^(?:(?:[:]?(?:no)?wrap)?[^#\\n;]*)"),
        SequenceJisonLexerPattern(35, "^(?:end\\b)"),
        SequenceJisonLexerPattern(36, "^(?:left of\\b)"),
        SequenceJisonLexerPattern(37, "^(?:right of\\b)"),
        SequenceJisonLexerPattern(38, "^(?:links\\b)"),
        SequenceJisonLexerPattern(39, "^(?:link\\b)"),
    )

    private fun lexerChunk2(): List<SequenceJisonLexerPattern> = listOf(
        SequenceJisonLexerPattern(40, "^(?:properties\\b)"),
        SequenceJisonLexerPattern(41, "^(?:details\\b)"),
        SequenceJisonLexerPattern(42, "^(?:over\\b)"),
        SequenceJisonLexerPattern(43, "^(?:note\\b)"),
        SequenceJisonLexerPattern(44, "^(?:activate\\b)"),
        SequenceJisonLexerPattern(45, "^(?:deactivate\\b)"),
        SequenceJisonLexerPattern(46, "^(?:title\\s[^#\\n;]+)"),
        SequenceJisonLexerPattern(47, "^(?:title:\\s[^#\\n;]+)"),
        SequenceJisonLexerPattern(48, "^(?:accTitle\\s*:\\s*)"),
        SequenceJisonLexerPattern(49, "^(?:(?!\\n||)*[^\\n]*)"),
        SequenceJisonLexerPattern(50, "^(?:accDescr\\s*:\\s*)"),
        SequenceJisonLexerPattern(51, "^(?:(?!\\n||)*[^\\n]*)"),
        SequenceJisonLexerPattern(52, "^(?:accDescr\\s*\\{\\s*)"),
        SequenceJisonLexerPattern(53, "^(?:[\\}])"),
        SequenceJisonLexerPattern(54, "^(?:[^\\}]*)"),
        SequenceJisonLexerPattern(55, "^(?:sequenceDiagram\\b)"),
        SequenceJisonLexerPattern(56, "^(?:autonumber\\b)"),
        SequenceJisonLexerPattern(57, "^(?:off\\b)"),
        SequenceJisonLexerPattern(58, "^(?:,)"),
        SequenceJisonLexerPattern(59, "^(?:;)"),
    )

    private fun lexerChunk3(): List<SequenceJisonLexerPattern> = listOf(
        SequenceJisonLexerPattern(60, "^(?:[^\\/\\\\\\+\\()\\+<\\->\\->:\\n,;]+((?!(-x|--x|-\\)|--\\)|-\\|\\\\|-\\\\|-\\/|-\\/\\/|-\\|\\/|\\/\\|-|\\\\\\|-|\\/\\/-|\\\\\\\\-|\\/\\|-|--\\|\\\\|--|\\(\\)))[\\-]*[^\\+<\\->\\->:\\n,;]+)*)"),
        SequenceJisonLexerPattern(61, "^(?:->>)"),
        SequenceJisonLexerPattern(62, "^(?:<<->>)"),
        SequenceJisonLexerPattern(63, "^(?:-->>)"),
        SequenceJisonLexerPattern(64, "^(?:<<-->>)"),
        SequenceJisonLexerPattern(65, "^(?:->)"),
        SequenceJisonLexerPattern(66, "^(?:-->)"),
        SequenceJisonLexerPattern(67, "^(?:-[x])"),
        SequenceJisonLexerPattern(68, "^(?:--[x])"),
        SequenceJisonLexerPattern(69, "^(?:-[\\)])"),
        SequenceJisonLexerPattern(70, "^(?:--[\\)])"),
        SequenceJisonLexerPattern(71, "^(?:--\\|\\\\)"),
        SequenceJisonLexerPattern(72, "^(?:--\\|\\/)"),
        SequenceJisonLexerPattern(73, "^(?:--\\\\\\\\)"),
        SequenceJisonLexerPattern(74, "^(?:--\\/\\/)"),
        SequenceJisonLexerPattern(75, "^(?:\\/\\|--)"),
        SequenceJisonLexerPattern(76, "^(?:\\\\\\|--)"),
        SequenceJisonLexerPattern(77, "^(?:\\/\\/--)"),
        SequenceJisonLexerPattern(78, "^(?:\\\\\\\\--)"),
        SequenceJisonLexerPattern(79, "^(?:-\\|\\\\)"),
    )

    private fun lexerChunk4(): List<SequenceJisonLexerPattern> = listOf(
        SequenceJisonLexerPattern(80, "^(?:-\\|\\/)"),
        SequenceJisonLexerPattern(81, "^(?:-\\\\\\\\)"),
        SequenceJisonLexerPattern(82, "^(?:-\\/\\/)"),
        SequenceJisonLexerPattern(83, "^(?:\\/\\|-)"),
        SequenceJisonLexerPattern(84, "^(?:\\\\\\|-)"),
        SequenceJisonLexerPattern(85, "^(?:\\/\\/-)"),
        SequenceJisonLexerPattern(86, "^(?:\\\\\\\\-)"),
        SequenceJisonLexerPattern(87, "^(?::(?:(?:no)?wrap)?[^#\\n;]*)"),
        SequenceJisonLexerPattern(88, "^(?::)"),
        SequenceJisonLexerPattern(89, "^(?:\\+)"),
        SequenceJisonLexerPattern(90, "^(?:-)"),
        SequenceJisonLexerPattern(91, "^(?:\\(\\))"),
        SequenceJisonLexerPattern(92, "^(?:\$)"),
        SequenceJisonLexerPattern(93, "^(?:.)"),
    )

    val lexerConditions: Map<String, IntArray> = mapOf(
        "acc_descr_multiline" to intArrayOf(53, 54),
        "acc_descr" to intArrayOf(51),
        "acc_title" to intArrayOf(49),
        "ID" to intArrayOf(2, 3, 7, 11, 12, 13, 14, 15),
        "ALIAS" to intArrayOf(2, 3, 21, 22),
        "LINE" to intArrayOf(2, 3, 34),
        "CONFIG" to intArrayOf(8, 9, 10),
        "CONFIG_DATA" to intArrayOf(),
        "INITIAL" to intArrayOf(0, 1, 3, 4, 5, 6, 16, 17, 18, 19, 20, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 50, 52, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93),
    )
}
