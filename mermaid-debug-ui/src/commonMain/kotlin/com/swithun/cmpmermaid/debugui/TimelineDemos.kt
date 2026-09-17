package com.swithun.cmpmermaid.debugui

internal data class TimelineDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val timelineDemos = listOf(
    TimelineDemo(
        id = "timeline_social_media",
        title = "Social media history",
        category = "Official syntax",
        source = """
            timeline
              title History of Social Media Platform
              2002 : LinkedIn
              2004 : Facebook : Google
              2005 : YouTube
              2006 : Twitter
        """.trimIndent(),
    ),
    TimelineDemo(
        id = "timeline_industrial_revolution",
        title = "Industrial revolution sections",
        category = "Sections",
        source = """
            timeline
              title History of Industrial Revolution
              section 17th-20th century
                Industry 1.0 : Machinery, Water power, Steam <br> power
                Industry 2.0 : Electricity, Internal combustion engine, Mass production
                Industry 3.0 : Electronics, Computers, Automation
              section 21st century
                Industry 4.0 : Internet, Robotics, Internet of Things
                Industry 5.0 : Artificial intelligence, Big data, 3D printing
        """.trimIndent(),
    ),
    TimelineDemo(
        id = "timeline_vertical_release",
        title = "Vertical release plan",
        category = "Direction",
        source = """
            timeline TD
              title Product release plan
              section 2026 Q1
                Foundation : Architecture review : API contract
                Beta : Internal rollout : Feedback
              section 2026 Q2
                General availability : Documentation : Monitoring
        """.trimIndent(),
    ),
    TimelineDemo(
        id = "timeline_multiline_events",
        title = "Continued event list",
        category = "Events",
        source = """
            timeline
              section Delivery
                Design : Scope agreed
                Build : Core implementation : Integration
                      : Accessibility review : Release checks
        """.trimIndent(),
    ),
    TimelineDemo(
        id = "timeline_configured_theme",
        title = "Configured timeline theme",
        category = "Configuration",
        source = """
            ---
            config:
              theme: base
              look: classic
              timeline:
                leftMargin: 180
                padding: 32
                useMaxWidth: false
                disableMulticolor: false
              themeVariables:
                cScale0: "#dbeafe"
                cScale1: "#dcfce7"
                cScaleLabel0: "#1e3a8a"
                cScaleLabel1: "#14532d"
            ---
            timeline
              title Delivery milestones
              accTitle: Delivery milestones
              accDescr: Two release phases with validation events
              section Build
                Prototype : API complete : UI complete
              section Validate
                Release candidate : Automated checks : Manual review
        """.trimIndent(),
    ),
)
