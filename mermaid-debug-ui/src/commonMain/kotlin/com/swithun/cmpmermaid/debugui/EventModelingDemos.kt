package com.swithun.cmpmermaid.debugui

internal data class EventModelingDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val eventModelingDemos = listOf(
    EventModelingDemo(
        id = "eventmodeling_state_change",
        title = "State change",
        category = "Official syntax",
        source = """
            eventmodeling
              tf 01 ui OrderForm
              tf 02 cmd SubmitOrder { orderId: O-42 }
              tf 03 evt OrderSubmitted
        """.trimIndent(),
    ),
    EventModelingDemo(
        id = "eventmodeling_state_view",
        title = "State view",
        category = "Read model",
        source = """
            eventmodeling
              rf 01 evt Sales.OrderSubmitted
              tf 02 rmo Sales.OrderSummary ->> 01
              tf 03 ui Sales.OrderStatus ->> 02
        """.trimIndent(),
    ),
    EventModelingDemo(
        id = "eventmodeling_automation",
        title = "Automation",
        category = "Processor",
        source = """
            eventmodeling
              rf 01 evt External.InventoryChanged
              tf 02 rmo Inventory.ExternalInventory ->> 01
              tf 03 pcr Inventory.Projector ->> 02
              tf 04 cmd Inventory.ApplyChange ->> 03
              tf 05 evt Inventory.ChangeApplied ->> 04
        """.trimIndent(),
    ),
    EventModelingDemo(
        id = "eventmodeling_projection",
        title = "Multi-event projection",
        category = "Explicit relations",
        source = """
            eventmodeling
              rf 01 evt OrderCreated
              rf 02 evt ItemAdded
              rf 03 evt OrderCancelled
              tf 04 readmodel OrderHistory ->> 01 ->> 02 ->> 03
              tf 05 ui OrderHistoryPage ->> 04
        """.trimIndent(),
    ),
    EventModelingDemo(
        id = "eventmodeling_configured",
        title = "Configured regional flow",
        category = "Configuration and theme",
        source = """
            ---
            title: Regional request lifecycle
            config:
              theme: dark
              eventmodeling:
                padding: 20
                rowHeight: 40
                useMaxWidth: false
              themeVariables:
                emUiFill: "#164e63"
                emCommandFill: "#1d4ed8"
                emEventFill: "#b45309"
            ---
            eventmodeling
              %% Mixed-script content remains inside the payload.
              tf 01 ui RegionalRequest { region: 東京 &amp; 서울 }
              tf 02 command QueueRequest
              tf 03 event RequestQueued
        """.trimIndent(),
    ),
)
