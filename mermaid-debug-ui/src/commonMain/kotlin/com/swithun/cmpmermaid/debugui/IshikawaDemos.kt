package com.swithun.cmpmermaid.debugui

internal data class IshikawaDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val ishikawaDemos = listOf(
    IshikawaDemo(
        id = "ishikawa_blurry_photo",
        title = "Blurry photo",
        category = "Official syntax",
        source = """
            ishikawa-beta
                Blurry Photo
                Process
                    Out of focus
                    Shutter speed too slow
                    Protective film not removed
                    Beautification filter applied
                User
                    Shaky hands
                Equipment
                    LENS
                        Inappropriate lens
                        Damaged lens
                        Dirty lens
                    SENSOR
                        Damaged sensor
                        Dirty sensor
                Environment
                    Subject moved too quickly
                    Too dark
        """.trimIndent(),
    ),
    IshikawaDemo(
        id = "ishikawa_root_only",
        title = "Root cause",
        category = "Minimal syntax",
        source = """
            ishikawa
            Delayed launch
        """.trimIndent(),
    ),
    IshikawaDemo(
        id = "ishikawa_release_quality",
        title = "Release quality",
        category = "Alternating causes",
        source = """
            ishikawa-beta
            Release regression
              Process
                Missing review
                Incomplete checklist
              People
                Ownership gap
              Platform
                Capacity limit
                  Saturated worker pool
              Environment
                Regional dependency
        """.trimIndent(),
    ),
    IshikawaDemo(
        id = "ishikawa_indentation_entities",
        title = "Irregular indentation",
        category = "Parser normalization",
        source = """
            %% The first cause establishes the indentation baseline.
            ishikawa
                Checkout &amp; payment failure
            Client
               Stale cache
                   Missing&lt;br/&gt;refresh
            Service
              Timeout
                 Retry storm
        """.trimIndent(),
    ),
    IshikawaDemo(
        id = "ishikawa_responsive_operations",
        title = "Responsive operations",
        category = "Configuration",
        source = """
            ---
            title: Regional incident analysis
            config:
              ishikawa:
                diagramPadding: 32
                useMaxWidth: true
            ---
            ishikawa-beta
            顧客向けサービスの応答時間悪化
              Application
                Long running background verification
                Synchronous dependency fan-out
              Data
                東京 replica lag
              Network
                São Paulo routing detour
              Operations
                서울 escalation delay
        """.trimIndent(),
    ),
)
