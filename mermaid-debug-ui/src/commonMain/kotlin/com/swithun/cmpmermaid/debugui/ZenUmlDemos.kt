package com.swithun.cmpmermaid.debugui

internal data class ZenUmlDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val zenUmlDemos = listOf(
    ZenUmlDemo(
        id = "zenuml_conversation",
        title = "Service conversation",
        category = "Participants and messages",
        source = """
            zenuml
            title Service Conversation
            Client as "Mobile Client"
            API as "API Gateway"
            Client->API: Submit request
            API->Client: Return response
        """.trimIndent(),
    ),
    ZenUmlDemo(
        id = "zenuml_nested_calls",
        title = "Nested synchronous calls",
        category = "Calls and replies",
        source = """
            zenuml
            title Nested Calls
            result = Gateway.authorize(token) {
              SessionStore.load(token)
              Policy.evaluate(result)
              return decision
            }
        """.trimIndent(),
    ),
    ZenUmlDemo(
        id = "zenuml_conditional",
        title = "Conditional checkout",
        category = "Alternative and optional flows",
        source = """
            zenuml
            title Conditional Checkout
            Customer->Checkout: Place order
            if(in_stock) {
              Checkout.reserve()
              opt(payment_required) {
                Payment.authorize()
              }
            } else {
              Checkout->Customer: Item unavailable
            }
        """.trimIndent(),
    ),
    ZenUmlDemo(
        id = "zenuml_parallel_recovery",
        title = "Parallel work and recovery",
        category = "Parallel and exception fragments",
        source = """
            zenuml
            title Parallel Delivery
            par {
              Coordinator->Inventory: Reserve stock
              Coordinator->Billing: Authorize payment
            }
            try {
              Coordinator.confirm()
            } catch(error) {
              Coordinator->Customer: Report failure
            } finally {
              Coordinator.cleanup()
            }
        """.trimIndent(),
    ),
    ZenUmlDemo(
        id = "zenuml_grouped_creation",
        title = "Grouped service creation",
        category = "Groups, annotators, and creation",
        source = """
            zenuml
            title Grouped Services
            @Actor Customer
            group "Back End" {
              @EC2 Gateway
              @Database Store
            }
            session = new Session(region="eu")
            Customer->Gateway: Start session
            Gateway.persist() {
              Store.save(session)
            }
        """.trimIndent(),
    ),
)
