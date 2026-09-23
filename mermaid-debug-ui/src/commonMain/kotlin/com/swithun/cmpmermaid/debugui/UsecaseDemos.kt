package com.swithun.cmpmermaid.debugui

internal data class UsecaseDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val usecaseDemos = listOf(
    UsecaseDemo(
        id = "usecase_ordering",
        title = "Ordering workflow",
        category = "Official syntax",
        source = """
            usecase-beta
            direction LR
            actor Customer
            systemBoundary Storefront
              Browse("Browse catalogue")
              Checkout("Checkout")
            end
            Customer --> Browse
            Customer --> Checkout
            Checkout ..> : include Browse
        """.trimIndent(),
    ),
    UsecaseDemo(
        id = "usecase_actor_variants",
        title = "Actor variants",
        category = "Actors and business elements",
        source = """
            usecase-beta
            actor Standard("Standard actor")
            actor Hollow("Hollow actor")@{ type: hollow }
            actor Awesome("Awesome actor")@{ type: awesome }
            actor Icon("Icon actor")@{ icon: "fa:user" }
            actor Broker("Business broker")@{ type: hollow, business: true } <<Partner>>
            Manage("Manage account")@{ business: true } <<Core>>
            Standard --> Manage
            Hollow --> Manage
            Awesome --> Manage
            Icon --> Manage
            Broker --> Manage
        """.trimIndent(),
    ),
    UsecaseDemo(
        id = "usecase_boundaries",
        title = "System boundaries",
        category = "Boundaries and styling",
        source = """
            usecase-beta
            direction LR
            systemBoundary identity["Identity service"]@{ type: package }:::system
              actor Operator("Support operator")
              SignIn("Sign in")
              Audit[Review audit trail]
            end
            Operator --> SignIn
            Operator --> Audit
            classDef system fill:#eef4ff,stroke:#36558f,color:#172554
            style Audit stroke:#7c3aed,stroke-width:3px
        """.trimIndent(),
    ),
    UsecaseDemo(
        id = "usecase_relationships",
        title = "Relationship semantics",
        category = "Associations and dependencies",
        source = """
            usecase-beta
            actor Administrator
            actor Person
            Checkout
            Payment
            ApplyCoupon
            Administrator --|> Person
            Administrator starts@-- "starts checkout" ---> Checkout
            Checkout payment@..> : include Payment
            ApplyCoupon ..> : extend Checkout
            ApplyCoupon --|> Checkout
            starts@{ animation: fast }
            style payment stroke:#6b46c1,stroke-width:2px
        """.trimIndent(),
    ),
    UsecaseDemo(
        id = "usecase_notes_json",
        title = "Notes and JSON",
        category = "Annotations and data",
        source = """
            usecase-beta
            accTitle: Payload inspection workflow
            accDescr: An analyst signs in and inspects a structured payload.
            actor Analyst("Regional analyst"):::external
            SignIn("Sign in")
            Inspect[Inspect payload]:::critical
            json Payload@{
              "region": "Tokyo",
              "active": true,
              "items": [{ "name": "Book", "quantity": 2 }]
            }:::data
            note for SignIn "`Requires an **active session**`"
            Analyst --> SignIn
            SignIn --> Inspect
            Inspect --> Payload
            classDef external,critical stroke-width:3px
            classDef data stroke:#3572a5
        """.trimIndent(),
    ),
)
