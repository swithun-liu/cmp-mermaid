package com.swithun.cmpmermaid.debugui

internal data class BlockDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val blockDemos = listOf(
    BlockDemo(
        id = "block_grid",
        title = "Grid and spans",
        category = "Official syntax",
        source = """
            block
              columns 4
              intake["Intake"] validate["Validate"]:2 publish["Publish"]
              space:2 retry["Retry"]:2
        """.trimIndent(),
    ),
    BlockDemo(
        id = "block_nested",
        title = "Nested boundaries",
        category = "Composites",
        source = """
            block
              columns 2
              client(["Client"])
              block:platform
                columns 1
                gateway["Gateway"]
                block:data
                  cache[("Cache")]
                  store[("Store")]
                end
              end
        """.trimIndent(),
    ),
    BlockDemo(
        id = "block_shapes",
        title = "Shape catalog",
        category = "Shapes",
        source = """
            block-beta
              columns 4
              start(("Start")) process["Process"] decision{"Decision"} complete((("Complete")))
              database[("Database")] queue(["Queue"]) service[["Service"]] region{{"Region"}}
              input[/"Input"/] output[\"Output"\] wait("Wait") alert>"Alert"]
        """.trimIndent(),
    ),
    BlockDemo(
        id = "block_edges",
        title = "Edges and block arrows",
        category = "Links",
        source = """
            block
              columns 4
              source["Source"] right<["Route"]>(right) target["Target"] down<["Fallback"]>(down)
              archive[("Archive")] space retry["Retry"] done(("Done"))
              source -- "primary" --> target
              target ==> done
              target -.-> retry
              retry --x archive
        """.trimIndent(),
    ),
    BlockDemo(
        id = "block_configured",
        title = "Configured ownership map",
        category = "Configuration and theme",
        source = """
            ---
            title: Ownership map
            config:
              theme: redux-color
              look: classic
              block:
                padding: 16
                useMaxWidth: false
              themeVariables:
                bkgColorArray: ["#dbeafe", "#dcfce7"]
                borderColorArray: ["#1d4ed8", "#15803d"]
            ---
            block
              block:frontend
                web["Web"]
                mobile["Mobile"]
              end
              block:backend
                api["API"]
                data[("Data")]
              end
              classDef active fill:#fef3c7,stroke:#b45309,color:#78350f;
              class api active
        """.trimIndent(),
    ),
)
