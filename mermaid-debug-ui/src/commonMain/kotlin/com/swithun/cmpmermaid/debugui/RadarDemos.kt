package com.swithun.cmpmermaid.debugui

internal data class RadarDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val radarDemos = listOf(
    RadarDemo(
        id = "radar_grades",
        title = "Student grades",
        category = "Official syntax",
        source = """
            ---
            title: Grades
            ---
            radar-beta
              axis math["Math"], science["Science"], english["English"]
              axis history["History"], geography["Geography"], art["Art"]
              curve alice["Alice"] { 85, 90, 80, 70, 75, 90 }
              curve bob["Bob"] { 70, 75, 85, 80, 90, 85 }
              max 100
              min 0
        """.trimIndent(),
    ),
    RadarDemo(
        id = "radar_restaurants",
        title = "Restaurant comparison",
        category = "Polygon graticule",
        source = """
            radar-beta
              title Restaurant comparison
              axis food["Food quality"], service["Service"], price["Price"], ambience["Ambience"]
              curve north["North"] { 4, 3, 2, 4 }
              curve south["South"] { 3, 4, 3, 3 }
              curve central["Central"] { 2, 3, 4, 2 }
              graticule polygon
              max 5
        """.trimIndent(),
    ),
    RadarDemo(
        id = "radar_detailed_entries",
        title = "Detailed entries",
        category = "Axis references",
        source = """
            radar-beta:
              axis reliability["Reliability"], speed["Speed"], quality["Quality"], cost["Cost"]
              curve current["Current"] { quality: 72, reliability: 94, cost: 61, speed: 83 }
              curve target["Target"] { cost 78, speed 91, reliability 97, quality 89 }
              min 50
              max 100
              ticks 5
        """.trimIndent(),
    ),
    RadarDemo(
        id = "radar_configured_theme",
        title = "Configured theme",
        category = "Configuration",
        source = """
            ---
            config:
              radar:
                width: 520
                height: 420
                marginTop: 42
                marginRight: 56
                marginBottom: 42
                marginLeft: 56
                axisScaleFactor: 0.88
                axisLabelFactor: 0.98
                curveTension: 0.08
                useMaxWidth: false
              theme: base
              themeVariables:
                cScale0: "#2563eb"
                cScale1: "#dc2626"
                radar:
                  axisColor: "#334155"
                  graticuleColor: "#94a3b8"
                  curveOpacity: 0.35
            ---
            radar-beta
              axis latency["Latency"], throughput["Throughput"], resilience["Resilience"]
              curve observed["Observed"] { 62, 84, 76 }
              curve target["Target"] { 80, 90, 92 }
              max 100
        """.trimIndent(),
    ),
    RadarDemo(
        id = "radar_accessible_unicode",
        title = "International scorecard",
        category = "Metadata and Unicode",
        source = """
            radar-beta
              title 地域 scorecard
              accTitle: Accessible regional scorecard
              accDescr {
                Comparison across regional quality indicators.
              }
              %% Detailed entries are resolved in declared axis order.
              axis tokyo["東京"], seoul["서울"], sao["São Paulo"], quality["Quality \"index\""]
              curve current["Current &amp; verified"] { quality: 88, tokyo: 92, sao: 74, seoul: 81 }
              showLegend false
              graticule polygon
              ticks 4
              max 100
        """.trimIndent(),
    ),
)
