package com.swithun.cmpmermaid.debugui

internal data class SankeyDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val sankeyDemos = listOf(
    SankeyDemo(
        id = "sankey_delivery",
        title = "Delivery flow",
        category = "Official syntax",
        source = """
            sankey
            Intake,Build,80
            Intake,Review,40
            Build,Release,70
            Build,Rework,10
            Review,Release,35
            Review,Rejected,5
        """.trimIndent(),
    ),
    SankeyDemo(
        id = "sankey_quoted_csv",
        title = "Quoted CSV",
        category = "Commas and quotes",
        source = """
            sankey
            "North, region","Reviewed ""ready${"\"\"\""},20
            "North, region",Deferred,12.5
            "South, region","Reviewed ""ready${"\"\"\""},18
        """.trimIndent(),
    ),
    SankeyDemo(
        id = "sankey_alignments",
        title = "Left alignment",
        category = "Node alignment",
        source = """
            ---
            config:
              sankey:
                nodeAlignment: left
                showValues: false
                linkColor: source
            ---
            sankey
            Gateway,Validate,45
            Gateway,Reject,5
            Validate,Publish,38
            Validate,Archive,7
        """.trimIndent(),
    ),
    SankeyDemo(
        id = "sankey_outlined",
        title = "Outlined labels",
        category = "Custom colors",
        source = """
            ---
            config:
              sankey:
                labelStyle: outlined
                nodeWidth: 16
                nodePadding: 8
                nodeColors:
                  Source: "#2563eb"
                  Transform: "#dc2626"
                  Warehouse: "#16a34a"
            ---
            sankey
            Source,Transform,65
            Source,Quarantine,15
            Transform,Warehouse,55
            Transform,Retry,10
        """.trimIndent(),
    ),
    SankeyDemo(
        id = "sankey_compact_beta",
        title = "Compact beta alias",
        category = "Sizing and values",
        source = """
            ---
            title: Compact migration
            config:
              sankey:
                width: 640
                height: 360
                nodeWidth: 7
                nodePadding: 5
                nodeAlignment: center
                prefix: "${'$'}"
                suffix: "k"
                useMaxWidth: false
            ---
            sankey-beta
            Legacy,Bridge,48
            Bridge,Modern,44
            Bridge,Manual review,4
        """.trimIndent(),
    ),
)
