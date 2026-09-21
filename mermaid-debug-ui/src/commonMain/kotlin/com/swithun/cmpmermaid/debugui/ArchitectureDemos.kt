package com.swithun.cmpmermaid.debugui

internal data class ArchitectureDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val architectureDemos = listOf(
    ArchitectureDemo(
        id = "architecture_web_platform",
        title = "Web platform",
        category = "Official syntax",
        source = """
            architecture-beta
              group cloud(cloud)[Cloud]
              service gateway(internet)[Gateway] in cloud
              service app(server)[Application] in cloud
              service database(database)[Database] in cloud
              gateway:R --> L:app
              app:R --> L:database
        """.trimIndent(),
    ),
    ArchitectureDemo(
        id = "architecture_nested_runtime",
        title = "Nested runtime",
        category = "Compound groups",
        source = """
            architecture-beta
              group platform(cloud)[Platform]
              group compute(server)[Compute] in platform
              group storage(disk)[Storage] in platform
              service api(server)[API] in compute
              service worker(server)[Worker] in compute
              service primary(database)[Primary] in storage
              api:R --> L:worker
              worker{group}:R --> L:primary{group}
        """.trimIndent(),
    ),
    ArchitectureDemo(
        id = "architecture_junction_fanout",
        title = "Junction fan-out",
        category = "Junctions",
        source = """
            architecture-beta
              group edge(cloud)[Edge]
              service gateway(internet)[Gateway] in edge
              junction split in edge
              service api(server)[API] in edge
              service jobs(server)[Jobs] in edge
              gateway:B --> T:split
              split:R --> L:api
              split:B --> T:jobs
        """.trimIndent(),
    ),
    ArchitectureDemo(
        id = "architecture_direction_matrix",
        title = "Directional ports",
        category = "Edges and labels",
        source = """
            architecture-beta
              service client(internet)[Client]
              service api(server)[API]
              service cache(disk)[Cache]
              service database(database)[Database]
              client:R --> L:api
              api:B -[cached read]-> T:cache
              cache:R <--> L:database
        """.trimIndent(),
    ),
    ArchitectureDemo(
        id = "architecture_aligned_delivery",
        title = "Aligned delivery",
        category = "Configuration and accessibility",
        source = """
            ---
            title: Delivery architecture
            config:
              architecture:
                padding: 32
                iconSize: 72
                nodeSeparation: 90
                idealEdgeLengthMultiplier: 1.7
                seed: 7
            ---
            architecture-beta
              accTitle: Delivery architecture
              accDescr: Three services share one aligned delivery row.
              group delivery(cloud)[Delivery]
              service source "SRC"[Source] in delivery
              service build(server)[Build] in delivery
              service publish(internet)[Publish] in delivery
              source:R --> L:build
              build:R --> L:publish
              align row source build publish
        """.trimIndent(),
    ),
)
