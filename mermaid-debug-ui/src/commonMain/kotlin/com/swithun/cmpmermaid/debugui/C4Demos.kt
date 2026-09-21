package com.swithun.cmpmermaid.debugui

internal data class C4Demo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val c4Demos = listOf(
    C4Demo(
        id = "c4_system_context",
        title = "System context",
        category = "Context",
        source = """
            C4Context
              title Online ordering context
              Person(customer, "Customer", "Places and tracks orders")
              System(ordering, "Ordering System", "Accepts and fulfils orders")
              System_Ext(payment, "Payment Provider", "Authorizes card payments")
              Rel(customer, ordering, "Uses")
              Rel(ordering, payment, "Requests authorization", "HTTPS")
        """.trimIndent(),
    ),
    C4Demo(
        id = "c4_container_landscape",
        title = "Container landscape",
        category = "Containers",
        source = """
            C4Container
              Person(user, "User")
              System_Boundary(platform, "Ordering Platform") {
                Container(web, "Web Application", "Kotlin/Wasm", "Serves the user interface")
                Container(api, "API", "Kotlin/JVM", "Coordinates order workflows")
                ContainerDb(database, "Database", "PostgreSQL", "Stores orders")
              }
              Rel(user, web, "Uses", "HTTPS")
              Rel(web, api, "Calls", "JSON/HTTPS")
              Rel(api, database, "Reads and writes", "SQL")
        """.trimIndent(),
    ),
    C4Demo(
        id = "c4_component_services",
        title = "Service components",
        category = "Components",
        source = """
            C4Component
              Container_Boundary(api, "Ordering API") {
                Component(controller, "Order Controller", "HTTP", "Validates requests")
                Component(service, "Order Service", "Kotlin", "Coordinates domain rules")
                ComponentDb(repository, "Order Repository", "SQL", "Persists order state")
              }
              Rel(controller, service, "Invokes")
              Rel(service, repository, "Stores orders")
              UpdateElementStyle(service, ${'$'}bgColor="#0f766e", ${'$'}borderColor="#115e59")
        """.trimIndent(),
    ),
    C4Demo(
        id = "c4_dynamic_checkout",
        title = "Checkout sequence",
        category = "Dynamic",
        source = """
            C4Dynamic
              Person(customer, "Customer")
              Container(web, "Web Application", "Kotlin/Wasm")
              Container(api, "Ordering API", "Kotlin/JVM")
              ContainerDb(database, "Database", "PostgreSQL")
              Rel(customer, web, "Submits order")
              Rel(web, api, "Creates order")
              Rel(api, database, "Persists order")
              Rel(api, web, "Returns confirmation")
              Rel(web, customer, "Shows receipt")
        """.trimIndent(),
    ),
    C4Demo(
        id = "c4_deployment_regions",
        title = "Regional deployment",
        category = "Deployment",
        source = """
            ---
            title: Regional deployment
            config:
              c4:
                c4ShapeInRow: 2
                c4BoundaryInRow: 2
                wrap: true
            ---
            C4Deployment
              accTitle: Regional ordering deployment
              accDescr: Clients reach two application nodes backed by one database.
              Deployment_Node(cloud, "Cloud Region", "Managed infrastructure") {
                Node_L(primary, "Primary Zone", "Linux") {
                  Container(apiA, "API A", "Kotlin/JVM")
                }
                Node_R(secondary, "Secondary Zone", "Linux") {
                  Container(apiB, "API B", "Kotlin/JVM")
                }
                Node(databaseNode, "Database Cluster", "PostgreSQL") {
                  ContainerDb(database, "Orders", "PostgreSQL")
                }
              }
              Rel(apiA, database, "Reads and writes")
              Rel(apiB, database, "Reads and writes")
        """.trimIndent(),
    ),
)
