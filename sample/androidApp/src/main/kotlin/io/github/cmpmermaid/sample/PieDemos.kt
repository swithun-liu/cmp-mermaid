package io.github.cmpmermaid.sample

internal data class PieDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val pieDemos = listOf(
    PieDemo(
        id = "pie_basic",
        title = "Basic distribution",
        category = "Syntax",
        source = """
            pie title Pets adopted by volunteers
                "Dogs" : 386
                "Cats" : 85
                "Rats" : 15
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_show_data",
        title = "Legend values",
        category = "Syntax",
        source = """
            pie showData
                title Support requests
                "Login" : 126
                "Billing" : 74
                "Sync" : 43
                "Other" : 21
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_input_order",
        title = "Input order",
        category = "Syntax",
        source = """
            pie
                title Slices keep source order
                "Small first" : 10
                "Largest second" : 100
                "Middle last" : 50
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_decimals",
        title = "Decimal values",
        category = "Syntax",
        source = """
            pie showData
                title Element composition
                "Calcium" : 42.96
                "Potassium" : 50.05
                "Magnesium" : 10.01
                "Iron" : 5
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_zero_value",
        title = "Zero value",
        category = "Filtering",
        source = """
            pie showData
                title Zero values remain in the legend
                "Rendered" : 70
                "Also rendered" : 30
                "Legend only" : 0
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_tiny_slice",
        title = "Sub-one-percent filtering",
        category = "Filtering",
        source = """
            pie showData
                title Tiny slices stay in the legend
                "Primary" : 70
                "Secondary" : 29.8
                "Below one percent" : 0.2
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_duplicate_label",
        title = "Duplicate labels",
        category = "Filtering",
        source = """
            pie showData
                title First duplicate wins
                "Stable" : 65
                "Stable" : 5
                "Changed" : 35
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_accessibility",
        title = "Accessibility metadata",
        category = "Metadata",
        source = """
            ---
            title: Device distribution
            ---
            pie
                accTitle: Active devices by platform
                accDescr {
                    The chart compares phone, tablet,
                    desktop, and television usage.
                }
                "Phone" : 58
                "Tablet" : 17
                "Desktop" : 21
                "Television" : 4
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_donut",
        title = "Donut chart",
        category = "Donut",
        source = """
            ---
            config:
              pie:
                donutHole: 0.42
            ---
            pie
                title Release channels
                "Stable" : 72
                "Beta" : 20
                "Canary" : 8
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_donut_labels_inward",
        title = "Donut with inward labels",
        category = "Donut",
        source = """
            ---
            config:
              pie:
                donutHole: 0.3
                textPosition: 0.58
            ---
            pie showData
                title Build outcomes
                "Passed" : 84
                "Skipped" : 9
                "Failed" : 7
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_legend_top",
        title = "Legend above",
        category = "Legend",
        source = """
            ---
            config:
              pie:
                legendPosition: top
            ---
            pie
                title Traffic sources
                "Search" : 45
                "Direct" : 30
                "Referral" : 15
                "Campaign" : 10
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_legend_bottom",
        title = "Legend below",
        category = "Legend",
        source = """
            ---
            config:
              pie:
                legendPosition: bottom
            ---
            pie showData
                title Test results
                "Passed" : 128
                "Failed" : 7
                "Skipped" : 13
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_legend_left",
        title = "Legend left",
        category = "Legend",
        source = """
            ---
            config:
              pie:
                legendPosition: left
            ---
            pie
                title Client platforms
                "Android" : 56
                "iOS" : 31
                "Desktop" : 9
                "Web" : 4
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_legend_center",
        title = "Legend centered",
        category = "Legend",
        source = """
            ---
            config:
              pie:
                legendPosition: center
                donutHole: 0.55
            ---
            pie
                title Storage allocation
                "Documents" : 42
                "Media" : 33
                "Applications" : 18
                "Free" : 7
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_highlight",
        title = "Highlighted slice",
        category = "Highlight",
        source = """
            ---
            config:
              pie:
                highlightSlice: Potassium
            ---
            pie
                title Key elements
                "Calcium" : 42.96
                "Potassium" : 50.05
                "Magnesium" : 10.01
                "Iron" : 5
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_text_center",
        title = "Centered percentages",
        category = "Labels",
        source = """
            ---
            config:
              pie:
                textPosition: 0
            ---
            pie
                title Labels at chart center
                "North" : 40
                "South" : 30
                "East" : 20
                "West" : 10
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_custom_theme",
        title = "Theme variables",
        category = "Styling",
        source = """
            ---
            config:
              themeVariables:
                pie1: "#0f766e"
                pie2: "#f59e0b"
                pie3: "#dc2626"
                pie4: "#2563eb"
                pieStrokeColor: "#ffffff"
                pieStrokeWidth: 3px
                pieOuterStrokeColor: "#334155"
                pieOuterStrokeWidth: 5px
                pieOpacity: 0.9
            ---
            pie
                title Custom palette
                "Ready" : 48
                "Waiting" : 22
                "Blocked" : 12
                "Running" : 18
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_twelve_colors",
        title = "Twelve-color palette",
        category = "Stress",
        source = """
            pie
                title Monthly distribution
                "Jan" : 8
                "Feb" : 7
                "Mar" : 9
                "Apr" : 8
                "May" : 10
                "Jun" : 9
                "Jul" : 8
                "Aug" : 7
                "Sep" : 9
                "Oct" : 8
                "Nov" : 9
                "Dec" : 8
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_palette_cycle",
        title = "Palette cycling",
        category = "Stress",
        source = """
            pie showData
                title Fourteen categories
                "Category 01" : 10
                "Category 02" : 10
                "Category 03" : 10
                "Category 04" : 10
                "Category 05" : 10
                "Category 06" : 10
                "Category 07" : 10
                "Category 08" : 10
                "Category 09" : 10
                "Category 10" : 10
                "Category 11" : 10
                "Category 12" : 10
                "Category 13" : 10
                "Category 14" : 10
        """.trimIndent(),
    ),
    PieDemo(
        id = "pie_long_labels",
        title = "Long title and legend",
        category = "Stress",
        source = """
            pie showData
                title Distribution of authentication recovery requests by resolution channel
                "Self-service recovery completed without assistance" : 54
                "Support-assisted identity verification" : 28
                "Administrator-managed account restoration" : 12
                "Unresolved or abandoned requests" : 6
        """.trimIndent(),
    ),
)
