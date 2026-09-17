package com.swithun.cmpmermaid.debugui

internal data class QuadrantDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val quadrantDemos = listOf(
    QuadrantDemo(
        id = "quadrant_campaigns",
        title = "Campaign reach and engagement",
        category = "Official syntax",
        source = """
            quadrantChart
                title Reach and engagement of campaigns
                x-axis Low Reach --> High Reach
                y-axis Low Engagement --> High Engagement
                quadrant-1 We should expand
                quadrant-2 Need to promote
                quadrant-3 Re-evaluate
                quadrant-4 May be improved
                Campaign A: [0.3, 0.6]
                Campaign B: [0.45, 0.23]
                Campaign C: [0.57, 0.69]
                Campaign D: [0.78, 0.34]
                Campaign E: [0.40, 0.34]
                Campaign F: [0.35, 0.78]
        """.trimIndent(),
    ),
    QuadrantDemo(
        id = "quadrant_empty_axes",
        title = "Centered labels without points",
        category = "Layout",
        source = """
            quadrantChart
                title Priority framework
                x-axis Low urgency --> High urgency
                y-axis Low impact --> High impact
                quadrant-1 Do now
                quadrant-2 Plan
                quadrant-3 Avoid
                quadrant-4 Delegate
        """.trimIndent(),
    ),
    QuadrantDemo(
        id = "quadrant_frontmatter",
        title = "Configured dimensions and theme",
        category = "Configuration",
        source = """
            ---
            title: Portfolio map
            config:
              quadrantChart:
                chartWidth: 420
                chartHeight: 360
                yAxisPosition: right
                useMaxWidth: false
              themeVariables:
                quadrant1Fill: "#dbeafe"
                quadrant2Fill: "#dcfce7"
                quadrant3Fill: "#fee2e2"
                quadrant4Fill: "#fef3c7"
                quadrantPointFill: "#0f172a"
            ---
            quadrantChart
                x-axis Lower return --> Higher return
                y-axis Lower risk --> Higher risk
                quadrant-1 Strategic bets
                quadrant-2 Hedge
                quadrant-3 Retire
                quadrant-4 Scale
                Platform renewal: [0.72, 0.68]
                Cost controls: [0.55, 0.24]
                Legacy migration: [0.28, 0.81]
        """.trimIndent(),
    ),
    QuadrantDemo(
        id = "quadrant_point_styles",
        title = "Point classes and inline styles",
        category = "Styling",
        source = """
            quadrantChart
                title Initiative portfolio
                x-axis Lower confidence --> Higher confidence
                y-axis Lower value --> Higher value
                quadrant-1 Commit
                quadrant-2 Validate
                quadrant-3 Pause
                quadrant-4 Optimize
                Search:::priority: [0.82, 0.86]
                Checkout:::priority: [0.76, 0.72] color: #ff3300, radius: 12
                Reporting:::candidate: [0.42, 0.63]
                Cleanup: [0.34, 0.22] stroke-color: #334155, stroke-width: 3px
                classDef priority color: #109060, radius: 10, stroke-color: #064e3b
                classDef candidate color: #2563eb, radius: 8
        """.trimIndent(),
    ),
)
