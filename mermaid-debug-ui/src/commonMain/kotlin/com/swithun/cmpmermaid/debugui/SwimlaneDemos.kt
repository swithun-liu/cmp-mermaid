package com.swithun.cmpmermaid.debugui

internal data class SwimlaneDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val swimlaneDemos = listOf(
    SwimlaneDemo(
        id = "swimlane_customer_onboarding",
        title = "Customer onboarding",
        category = "Official syntax",
        source = """
            swimlane-beta LR
              subgraph Customer
                register[Register account]
                activate[Activate access]
              end
              subgraph Operations
                review[Review profile]
                provision[Provision workspace]
              end
              subgraph Security
                approve{Approve access?}
              end
              register --> review --> approve
              approve -->|Yes| provision --> activate
              approve -->|No| review
        """.trimIndent(),
    ),
    SwimlaneDemo(
        id = "swimlane_vertical_incident",
        title = "Vertical incident response",
        category = "Directions and cycles",
        source = """
            swimlane-beta TB
              subgraph Detection
                alert[Receive alert]
                classify{Classify impact}
              end
              subgraph Response
                contain[Contain issue]
                recover[Recover service]
              end
              subgraph Review
                verify[Verify recovery]
              end
              alert --> classify --> contain --> recover --> verify
              verify -.->|Regression| classify
        """.trimIndent(),
    ),
    SwimlaneDemo(
        id = "swimlane_default_nested",
        title = "Default and nested ownership",
        category = "Containers",
        source = """
            swimlane-beta LR
              intake([External intake])
              subgraph Fulfillment
                subgraph Warehouse
                  reserve[(Reserve stock)]
                  pack[Pack shipment]
                end
                dispatch[Dispatch carrier]
              end
              intake --> reserve --> pack --> dispatch
        """.trimIndent(),
    ),
    SwimlaneDemo(
        id = "swimlane_configured_delivery",
        title = "Configured delivery route",
        category = "Configuration and styling",
        source = """
            ---
            title: Delivery ownership
            config:
              swimlane:
                lineHops: gap
                ignoreCrossLaneEdges: false
                automaticLaneOrdering: true
            ---
            swimlane-beta LR
              subgraph Product
                define[Define scope]
              end
              subgraph Engineering
                implement[Implement]
              end
              subgraph Quality
                certify[Certify]
              end
              define --> implement --> certify
              define --> certify
              classDef focus fill:#fef3c7,stroke:#b45309,color:#78350f;
              class certify focus;
        """.trimIndent(),
    ),
    SwimlaneDemo(
        id = "swimlane_reverse_release",
        title = "Reverse release confirmation",
        category = "Reverse direction",
        source = """
            swimlane-beta RL
              accTitle: Reverse release confirmation
              accDescr: Publication confirmation travels back to the author.
              subgraph Author
                submit[Submit release]
                confirm[Confirm publication]
              end
              subgraph Reviewer
                inspect[Inspect package]
              end
              subgraph Publisher
                publish((Publish))
              end
              submit --> inspect --> publish --> confirm
        """.trimIndent(),
    ),
)
