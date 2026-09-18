package com.swithun.cmpmermaid.debugui

internal data class CynefinDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val cynefinDemos = listOf(
    CynefinDemo(
        id = "cynefin_incident_response",
        title = "Incident response",
        category = "Official syntax",
        source = """
            cynefin-beta
              title Incident Response

              complex
                "Investigate root cause"
                "Run chaos experiment"

              complicated
                "Analyze performance data"
                "Expert review needed"

              clear
                "Restart service"
                "Apply known fix"

              chaotic
                "Page on-call immediately"

              confusion
                "Unknown failure mode"
        """.trimIndent(),
    ),
    CynefinDemo(
        id = "cynefin_strategy_transitions",
        title = "Strategy transitions",
        category = "Transitions",
        source = """
            cynefin-beta
              title Strategy Categorization
              complex
                "Market research"
              complicated
                "Competitive analysis"
              clear
                "Standard pricing"
              chaotic
                "Crisis management"
              complex --> complicated : "Pattern identified"
              complicated --> clear : "Best practice codified"
              clear --> chaotic : "Complacency"
              chaotic --> complex : "Stabilized"
        """.trimIndent(),
    ),
    CynefinDemo(
        id = "cynefin_empty_framework",
        title = "Empty framework",
        category = "Worksheet",
        source = """
            cynefin-beta
              title Cynefin Framework
              complex
              complicated
              clear
              chaotic
        """.trimIndent(),
    ),
    CynefinDemo(
        id = "cynefin_confusion_overflow",
        title = "Confusion triage",
        category = "Overflow behavior",
        source = """
            cynefin-beta
              confusion
                "Unclassified outage"
                "New market signal"
                "Unknown dependency"
                "Emerging regulation"
                "Unowned workflow"
              confusion --> chaotic : "Immediate harm"
              confusion --> complex : "Safe to probe"
        """.trimIndent(),
    ),
    CynefinDemo(
        id = "cynefin_configured_regions",
        title = "Configured regions",
        category = "Configuration and theme",
        source = """
            ---
            title: Regional decision map
            config:
              theme: forest
              themeVariables:
                cynefin:
                  boundaryColor: "#14532d"
                  arrowColor: "#166534"
                  complexBg: "#dcfce7"
                  confusionBg: "#fef3c7"
              cynefin:
                width: 720
                height: 520
                padding: 32
                boundaryAmplitude: 4
                seed: 42
                useMaxWidth: false
            ---
            cynefin-beta
              accTitle: Regional operating decisions
              accDescr: Work categorized by certainty and urgency
              complex
                "東京 discovery"
              complicated
                "München analysis"
              clear
                "서울 runbook"
              chaotic
                "São Paulo incident"
        """.trimIndent(),
    ),
)
