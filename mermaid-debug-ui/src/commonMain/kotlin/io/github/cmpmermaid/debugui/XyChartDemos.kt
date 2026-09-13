package io.github.cmpmermaid.debugui

internal data class XyChartDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val xyChartDemos = listOf(
    XyChartDemo(
        id = "xy_basic_line",
        title = "Basic line",
        category = "Basics",
        source = """
            xychart
                line [1, 4, 3, 7, 5]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_sales",
        title = "Sales revenue",
        category = "Basics",
        source = """
            xychart
                title "Sales Revenue"
                x-axis [Jan, Feb, Mar, Apr, May, Jun]
                y-axis "Revenue" 0 --> 12000
                bar "Actual" [5000, 6000, 7500, 8200, 9500, 10500]
                line "Forecast" [4800, 6200, 7200, 8500, 9300, 11000]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_horizontal",
        title = "Horizontal bars",
        category = "Orientation",
        source = """
            xychart horizontal
                title "Support requests"
                x-axis [Login, Billing, Sync, Other]
                y-axis 0 --> 140
                bar [126, 74, 43, 21]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_numeric_x",
        title = "Numeric X axis",
        category = "Axes",
        source = """
            xychart
                title "Measured response"
                x-axis "Input" -2 --> 8
                y-axis "Output" -10 --> 40
                line [4, -2, 3, 12, 26, 38]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_auto_axes",
        title = "Automatic ranges",
        category = "Axes",
        source = """
            xychart-beta
                title "Automatic domains"
                bar [3, 8, 5, 12, 9]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_named_series",
        title = "Named series legend",
        category = "Legend",
        source = """
            xychart
                title "Latency percentiles"
                x-axis ["90d", "60d", "30d", "7d", "Current"]
                y-axis "Milliseconds" 0 --> 200
                line "p50" [38, 37, 40, 55, 49]
                line "p95" [112, 75, 103, 177, 180]
                bar "Average" [48, 42, 46, 73, 68]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_legend_hidden",
        title = "Hidden legend",
        category = "Legend",
        source = """
            ---
            config:
              xyChart:
                showLegend: false
            ---
            xychart
                title "Series without legend"
                x-axis [A, B, C, D]
                line "First" [3, 6, 4, 8]
                line "Second" [2, 5, 7, 6]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_bar_labels",
        title = "Bar data labels",
        category = "Labels",
        source = """
            ---
            config:
              xyChart:
                showDataLabel: true
            ---
            xychart
                title "Books by genre"
                x-axis [Comedy, Romance, Mystery, Crime, Other]
                y-axis 0 --> 30
                bar [12, 8, 20, 25, 17]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_bar_labels_outside",
        title = "Labels outside bars",
        category = "Labels",
        source = """
            ---
            config:
              xyChart:
                showDataLabel: true
                showDataLabelOutsideBar: true
            ---
            xychart horizontal
                x-axis [A, B, C, D]
                y-axis 0 --> 50
                bar [12, 28, 35, 44]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_point_labels",
        title = "Line point labels",
        category = "Labels",
        source = """
            xychart
                title "Model size milestones"
                x-axis ["Apr 2022", "Feb 2023", "Sep 2023", "Apr 2024"]
                y-axis "Parameters (B)" 0 --> 600
                line [540 "PaLM", 65 "LLaMA", 7 "Mistral", 3.8 "Phi-3"]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_partial_point_labels",
        title = "Partial point labels",
        category = "Labels",
        source = """
            xychart
                title "Quarterly performance"
                x-axis [Q1, Q2, Q3, Q4]
                y-axis 0 --> 100
                line [25 "Launch", 45, 72, 90 "Target"]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_rotated_labels",
        title = "Rotated axis labels",
        category = "Axes",
        source = """
            ---
            config:
              xyChart:
                xAxis:
                  labelRotation: -45
            ---
            xychart
                title "Monthly active users"
                x-axis ["January 2026", "February 2026", "March 2026", "April 2026"]
                y-axis 0 --> 100
                line [52, 63, 78, 91]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_custom_palette",
        title = "Custom plot palette",
        category = "Theme",
        source = """
            ---
            config:
              themeVariables:
                xyChart:
                  plotColorPalette: "#dc2626, #2563eb, #16a34a"
            ---
            xychart
                title "Custom colors"
                x-axis [A, B, C, D]
                line "Red" [5, 9, 7, 12]
                bar "Blue" [4, 6, 10, 8]
                line "Green" [2, 5, 8, 11]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_custom_size",
        title = "Custom dimensions",
        category = "Configuration",
        source = """
            ---
            config:
              xyChart:
                width: 560
                height: 360
                titleFontSize: 24
            ---
            xychart
                title "Compact chart"
                x-axis [One, Two, Three]
                bar [10, 25, 18]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_minimal_axes",
        title = "Minimal axes",
        category = "Configuration",
        source = """
            ---
            config:
              xyChart:
                showTitle: false
                xAxis:
                  showLabel: false
                  showTick: false
                yAxis:
                  showLabel: false
                  showTick: false
            ---
            xychart
                title "Hidden title"
                line [2, 8, 5, 11]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_negative_values",
        title = "Negative range",
        category = "Axes",
        source = """
            xychart
                title "Temperature delta"
                x-axis [Mon, Tue, Wed, Thu, Fri]
                y-axis "Degrees" -10 --> 10
                line [-4, -1, 3, 7, 2]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_decimal_values",
        title = "Decimal values",
        category = "Data",
        source = """
            xychart
                title "Conversion rate"
                x-axis [A, B, C, D, E]
                y-axis "Percent" 0 --> 1
                line [.12, .28, .43, .67, .91]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_overlaid_bars",
        title = "Overlaid bar series",
        category = "Data",
        source = """
            xychart
                title "Planned and actual"
                x-axis [Q1, Q2, Q3, Q4]
                y-axis 0 --> 100
                bar "Planned" [40, 55, 70, 85]
                bar "Actual" [35, 62, 66, 91]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_category_truncation",
        title = "Category truncation",
        category = "Data",
        source = """
            xychart
                title "Visible categories"
                x-axis [A, B, C]
                bar [10, 20, 30, 999, 1000]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_accessibility",
        title = "Accessibility metadata",
        category = "Metadata",
        source = """
            ---
            title: Device growth
            ---
            xychart
                accTitle: Active devices by month
                accDescr {
                    The line compares four monthly totals.
                }
                x-axis [Jan, Feb, Mar, Apr]
                y-axis 0 --> 80
                line [34, 45, 61, 76]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_multi_bar_labels",
        title = "Multiple labeled bar series",
        category = "Stability",
        source = """
            ---
            config:
              xyChart:
                showDataLabel: true
            ---
            xychart
                title "Planned and actual"
                x-axis [Q1, Q2, Q3, Q4, Q5, Q6]
                y-axis 0 --> 120
                bar "Planned" [42, 55, 61, 78, 92, 108]
                bar "Actual" [38, 63, 58, 84, 88, 115]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_horizontal_multi_series",
        title = "Horizontal mixed series",
        category = "Stability",
        source = """
            xychart horizontal
                title "Regional throughput"
                x-axis [North, South, East, West, Central]
                y-axis 0 --> 200
                bar "Current" [120, 88, 156, 110, 142]
                line "Target" [130, 100, 150, 125, 160]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_wide_category_corpus",
        title = "Wide category corpus",
        category = "Stability",
        source = """
            ---
            config:
              xyChart:
                xAxis:
                  labelRotation: -45
            ---
            xychart
                title "Sixteen release cohorts"
                x-axis [R01, R02, R03, R04, R05, R06, R07, R08, R09, R10, R11, R12, R13, R14, R15, R16]
                y-axis 0 --> 100
                bar [22, 28, 31, 37, 42, 49, 53, 57, 61, 66, 70, 74, 79, 83, 88, 93]
                line [18, 24, 30, 35, 39, 45, 50, 54, 59, 63, 68, 72, 76, 81, 86, 90]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_tiny_decimal_domain",
        title = "Tiny decimal domain",
        category = "Stability",
        source = """
            xychart
                title "Error rate"
                x-axis [A, B, C, D, E, F]
                y-axis "Ratio" 0 --> 0.01
                line "Observed" [0.001, 0.0025, 0.0018, 0.0042, 0.0068, 0.0091]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_large_magnitude_domain",
        title = "Large magnitude domain",
        category = "Stability",
        source = """
            xychart
                title "Monthly events"
                x-axis [Jan, Feb, Mar, Apr, May, Jun]
                y-axis "Events" 0 --> 10000000
                bar "Processed" [1200000, 2400000, 3800000, 5100000, 7300000, 9400000]
                line "Capacity" [2000000, 3000000, 4500000, 6000000, 8000000, 10000000]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_single_point",
        title = "Single numeric point",
        category = "Stability",
        source = """
            xychart
                title "Single observation"
                x-axis "Input" 5 --> 5
                y-axis "Output" 10 --> 10
                line [10 "Observed"]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_constant_auto_domain",
        title = "Constant automatic domain",
        category = "Stability",
        source = """
            xychart
                title "Constant measurements"
                x-axis [A, B, C, D, E]
                line "Stable" [7, 7, 7, 7, 7]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_dense_legend",
        title = "Dense legend",
        category = "Stability",
        source = """
            xychart
                title "Service latency"
                x-axis [Mon, Tue, Wed, Thu, Fri, Sat, Sun]
                y-axis 0 --> 300
                line "Gateway p50" [45, 48, 43, 51, 57, 39, 41]
                line "Gateway p95" [140, 155, 132, 168, 180, 110, 121]
                line "Search p50" [61, 66, 59, 72, 70, 55, 58]
                line "Search p95" [188, 205, 176, 230, 218, 160, 172]
                bar "Daily average" [83, 91, 79, 104, 109, 72, 78]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_mixed_signed_values",
        title = "Mixed signed values",
        category = "Stability",
        source = """
            xychart
                title "Signed deltas"
                x-axis [A, B, C, D, E, F, G]
                y-axis -50 --> 50
                bar "Delta" [-35, -12, 8, 31, -6, 22, 45]
                line "Trend" [-28, -18, -4, 12, 19, 27, 38]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_point_label_crowding",
        title = "Dense point labels",
        category = "Stability",
        source = """
            xychart
                title "Release milestones"
                x-axis [R1, R2, R3, R4, R5, R6, R7, R8]
                y-axis 0 --> 100
                line [12 "Alpha", 21 "Beta", 35 "RC1", 48 "RC2", 63 "GA", 72 "Patch 1", 84 "Patch 2", 95 "LTS"]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_compact_rotated",
        title = "Compact rotated chart",
        category = "Stability",
        source = """
            ---
            config:
              xyChart:
                width: 420
                height: 300
                plotReservedSpacePercent: 65
                xAxis:
                  labelRotation: -60
            ---
            xychart
                title "Compact monthly view"
                x-axis [January, February, March, April, May, June]
                y-axis 0 --> 80
                bar [22, 35, 31, 48, 59, 72]
        """.trimIndent(),
    ),
    XyChartDemo(
        id = "xy_partial_series_lengths",
        title = "Partial series lengths",
        category = "Stability",
        source = """
            xychart
                title "Partial observations"
                x-axis [A, B, C, D, E, F]
                y-axis 0 --> 100
                bar "Complete" [15, 30, 45, 60, 75, 90]
                line "Partial" [12, 27, 51]
        """.trimIndent(),
    ),
)
