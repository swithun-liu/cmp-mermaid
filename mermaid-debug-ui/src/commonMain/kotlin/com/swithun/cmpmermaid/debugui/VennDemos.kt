package com.swithun.cmpmermaid.debugui

internal data class VennDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val vennDemos = listOf(
    VennDemo(
        id = "venn_product_fit",
        title = "Product fit",
        category = "Official syntax",
        source = """
            venn-beta
              title What makes a good feature
              set Desirable
              set Feasible
              set Viable
              union Desirable,Feasible["Buildable"]
              union Feasible,Viable["Sustainable"]
              union Desirable,Viable["Marketable"]
              union Desirable,Feasible,Viable["Ship it"]
        """.trimIndent(),
    ),
    VennDemo(
        id = "venn_weighted_overlap",
        title = "Weighted overlap",
        category = "Area proportions",
        source = """
            venn-beta
              title Platform adoption
              set Mobile["Mobile users"]:30
              set Web["Web users"]:24
              union Mobile,Web["Cross-platform"]:9
        """.trimIndent(),
    ),
    VennDemo(
        id = "venn_text_nodes",
        title = "Capabilities",
        category = "Text nodes",
        source = """
            venn-beta
              set Client["Client"]
                text C1["Compose"]
                text C2["Offline"]
              set Server["Server"]
                text S1["Storage"]
                text S2["Jobs"]
              union Client,Server["Shared"]
                text CS1["Schema"]
                text CS2["Telemetry"]
        """.trimIndent(),
    ),
    VennDemo(
        id = "venn_styled_sets",
        title = "Styled sets",
        category = "Styles",
        source = """
            venn-beta
              title Styled responsibilities
              set Product["Product"]:20
                text P1["Roadmap"]
              set Engineering["Engineering"]:18
                text E1["Delivery"]
              union Product,Engineering["Planning"]:6
              style Product fill:#2563eb,stroke:#1e3a8a,stroke-width:4px,fill-opacity:0.18
              style Engineering fill:#16a34a,stroke:#14532d,fill-opacity:0.18
              style Product,Engineering fill:rgba(250, 204, 21, 0.35),color:#111827
              style P1 color:#dc2626
        """.trimIndent(),
    ),
    VennDemo(
        id = "venn_four_sets",
        title = "Four-team alignment",
        category = "Higher arity",
        source = """
            ---
            config:
              venn:
                width: 880
                height: 500
                padding: 18
                useMaxWidth: false
            ---
            venn-beta
              title Release readiness
              set Product:24
              set Design:20
              set Engineering:28
              set Quality:18
              union Product,Design:8
              union Product,Engineering:7
              union Product,Quality:4
              union Design,Engineering:6
              union Design,Quality:3
              union Engineering,Quality:7
              union Product,Design,Engineering["Build"]:2
              union Product,Design,Engineering,Quality["Ready"]:1
        """.trimIndent(),
    ),
)
