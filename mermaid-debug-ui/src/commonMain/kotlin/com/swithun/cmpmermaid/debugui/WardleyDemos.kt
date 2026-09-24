package com.swithun.cmpmermaid.debugui

internal data class WardleyDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val wardleyDemos = listOf(
    WardleyDemo(
        id = "wardley_tea_shop",
        title = "Tea shop value chain",
        category = "Components and dependencies",
        source = """
            wardley-beta
            title Tea Shop Strategy
            anchor Customer [0.95, 0.63]
            component Cup of Tea [0.79, 0.61]
            component Tea [0.63, 0.81]
            component Hot Water [0.52, 0.80]
            component Kettle [0.43, 0.35]
            component Power [0.10, 0.70]
            Customer -> Cup of Tea
            Cup of Tea -> Tea
            Cup of Tea -> Hot Water
            Hot Water -> Kettle
            Kettle -> Power
        """.trimIndent(),
    ),
    WardleyDemo(
        id = "wardley_sourcing",
        title = "Sourcing strategy",
        category = "Build, buy, outsource, and market",
        source = """
            wardley-beta
            title Platform Sourcing
            anchor User [0.92, 0.90]
            component Custom Experience [0.78, 0.48] (build)
            component Identity Provider [0.62, 0.72] (buy)
            component Support Desk [0.54, 0.40] (outsource)
            component Cloud Runtime [0.30, 0.92] (market)
            User -> Custom Experience
            Custom Experience -> Identity Provider
            Custom Experience -> Support Desk
            Identity Provider -> Cloud Runtime
        """.trimIndent(),
    ),
    WardleyDemo(
        id = "wardley_links_evolution",
        title = "Flow and evolution",
        category = "Link types and movement",
        source = """
            wardley-beta
            title Service Evolution
            component Client [0.90, 0.90]
            component Gateway [0.72, 0.68]
            component Cache [0.58, 0.46]
            component Database [0.35, 0.72] (inertia)
            Client -> Gateway
            Gateway +'reads'> Cache
            Cache +<> Database
            evolve Cache 0.78
            evolve Database 0.62
        """.trimIndent(),
    ),
    WardleyDemo(
        id = "wardley_pipeline",
        title = "Data platform pipeline",
        category = "Pipelines and custom evolution",
        source = """
            wardley-beta
            title Data Platform
            evolution Genesis@0.2 -> Custom@0.45 -> Product@0.75 -> Commodity@1.0
            anchor Analyst [0.90, 0.92]
            component Data Platform [0.58, 0.60]
            Analyst -> Data Platform
            pipeline Data Platform {
              component File Store [0.28]
              component SQL Engine [0.52]
              component Managed Warehouse [0.84]
            }
        """.trimIndent(),
    ),
    WardleyDemo(
        id = "wardley_annotations",
        title = "Strategic annotations",
        category = "Notes, annotations, and forces",
        source = """
            wardley-beta
            title Modernisation Decisions
            component Legacy Core [0.35, 0.34] (inertia)
            component Modern API [0.68, 0.62]
            Legacy Core -> Modern API
            note "Migration requires staged delivery" [0.52, 0.48]
            annotations [0.12, 0.88]
            annotation 1,[0.35, 0.30] "Legacy constraint"
            annotation 2,[0.68, 0.58] "Target capability"
            accelerator "Cloud adoption" [0.25, 0.82]
            deaccelerator "Data migration" [0.46, 0.26]
        """.trimIndent(),
    ),
)
