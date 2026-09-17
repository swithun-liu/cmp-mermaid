package com.swithun.cmpmermaid.debugui

internal data class TreemapDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val treemapDemos = listOf(
    TreemapDemo(
        id = "treemap_product_mix",
        title = "Product mix",
        category = "Official syntax",
        source = """
            treemap-beta
            "Products"
                "Electronics"
                    "Phones": 50
                    "Computers": 30
                    "Accessories": 20
                "Clothing"
                    "Men's": 40
                    "Women's": 40
        """.trimIndent(),
    ),
    TreemapDemo(
        id = "treemap_multiple_roots",
        title = "Regional allocation",
        category = "Multiple roots",
        source = """
            treemap
            "Americas"
                "North": 48
                "South": 22
            "Europe"
                "West": 34
                "Central": 28
            "Asia Pacific"
                "East": 42
                "South": 31
        """.trimIndent(),
    ),
    TreemapDemo(
        id = "treemap_styled",
        title = "Styled priorities",
        category = "Class styling",
        source = """
            treemap-beta
            "Delivery"
                "Critical":::priority
                    "Parser": 32
                    "Renderer": 28
                "Validation": 24:::verified
                "Documentation": 16
            classDef priority fill:#fee2e2,stroke:#dc2626,stroke-width:3px,color:#7f1d1d;
            classDef verified fill:#dcfce7,stroke:#16a34a,color:#14532d,font-style:italic;
        """.trimIndent(),
    ),
    TreemapDemo(
        id = "treemap_currency",
        title = "Budget allocation",
        category = "Value formatting",
        source = """
            ---
            config:
              treemap:
                valueFormat: '${'$'}0,0'
                padding: 6
                diagramPadding: 18
                nodeWidth: 120
                nodeHeight: 48
                useMaxWidth: false
            ---
            treemap-beta
            title Annual budget
            "Operations"
                "Salaries": 700000
                "Equipment": 200000
                "Supplies": 100000
            "Marketing"
                "Advertising": 400000
                "Events": 100000
        """.trimIndent(),
    ),
    TreemapDemo(
        id = "treemap_accessible_unicode",
        title = "International capacity",
        category = "Metadata and Unicode",
        source = """
            ---
            config:
              treemap:
                showValues: false
                labelFontSize: 15
            ---
            treemap
            title 地域 capacity
            accTitle: Accessible regional capacity
            accDescr {
              Capacity grouped by international operating region.
            }
            %% Mixed scripts exercise text measurement.
            "アジア"
                "東京": 44
                "서울": 36
            "Europa"
                "München": 28
                "Zürich": 22
        """.trimIndent(),
    ),
)
