package com.swithun.cmpmermaid.debugui

internal data class PacketDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val packetDemos = listOf(
    PacketDemo(
        id = "packet_tcp",
        title = "TCP header",
        category = "Official syntax",
        source = """
            ---
            title: TCP Packet
            ---
            packet
              0-15: "Source Port"
              16-31: "Destination Port"
              32-63: "Sequence Number"
              64-95: "Acknowledgment Number"
              96-99: "Data Offset"
              100-105: "Reserved"
              106: "URG"
              107: "ACK"
              108: "PSH"
              109: "RST"
              110: "SYN"
              111: "FIN"
              112-127: "Window"
        """.trimIndent(),
    ),
    PacketDemo(
        id = "packet_udp_counts",
        title = "UDP bit counts",
        category = "Count syntax",
        source = """
            packet
              title UDP Packet
              +16: "Source Port"
              +16: "Destination Port"
              32-47: "Length"
              +16: "Checksum"
              +32: "Data"
        """.trimIndent(),
    ),
    PacketDemo(
        id = "packet_flags",
        title = "Control flags",
        category = "Single bits",
        source = """
            packet
              0-3: "Version"
              4-7: "Priority"
              8: "URG"
              9: "ACK"
              10: "PSH"
              11: "RST"
              12: "SYN"
              13: "FIN"
              14-31: "Reserved"
        """.trimIndent(),
    ),
    PacketDemo(
        id = "packet_compact",
        title = "Compact frame",
        category = "Configuration",
        source = """
            ---
            config:
              packet:
                rowHeight: 28
                bitWidth: 20
                bitsPerRow: 16
                showBits: false
                paddingX: 2
                paddingY: 4
                useMaxWidth: false
            ---
            packet
              title Compact frame
              +4: "Type"
              +4: "Flags"
              +8: "Length"
              +32: "Payload"
        """.trimIndent(),
    ),
    PacketDemo(
        id = "packet_accessible_unicode",
        title = "International frame",
        category = "Metadata and Unicode",
        source = """
            packet-beta
              title 地域 frame
              accTitle: International frame structure
              accDescr: Regional fields and payload ranges
              +8: "種類 &amp; mode"
              +8: "東京"
              +16: "서울"
              +32: "São Paulo"
        """.trimIndent(),
    ),
)
