package io.github.cmpmermaid.sample

internal data class ErDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val erDemos = listOf(
    ErDemo(
        id = "er_basic",
        title = "Basic relationship",
        category = "Entities",
        source = """
            erDiagram
                CUSTOMER ||--o{ ORDER : places
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_attribute_table",
        title = "Attribute table",
        category = "Attributes",
        source = """
            erDiagram
                CUSTOMER {
                    int id PK
                    string name
                    string email UK
                    string? nickname
                }
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_keys_comments",
        title = "Keys and comments",
        category = "Attributes",
        source = """
            erDiagram
                ORDER_ITEM {
                    int order_id PK, FK "Parent order"
                    int product_id PK, FK "Catalog item"
                    decimal unit_price "Price at purchase"
                    int quantity
                }
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_alias_unicode",
        title = "Aliases and Unicode",
        category = "Entities",
        source = """
            erDiagram
                customer["Customer Account"] {
                    string customer_name "Display name"
                }
                address["Shipping Address"] {
                    string city
                }
                customer ||--o{ address : "配送先"
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_cardinality_matrix",
        title = "Cardinality matrix",
        category = "Relationships",
        source = """
            erDiagram
                A ||--|| B : "exactly one"
                C ||--o| D : "zero or one"
                E ||--|{ F : "one or more"
                G ||--o{ H : "zero or more"
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_identification",
        title = "Identifying and non-identifying",
        category = "Relationships",
        source = """
            erDiagram
                ORDER ||--|{ ORDER_ITEM : contains
                PRODUCT ||..o{ ORDER_ITEM : references
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_md_parent",
        title = "MD parent marker",
        category = "Relationships",
        source = """
            erDiagram
                PARENT u--o{ CHILD : owns
                PARENT ||--|| PROFILE : has
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_self_relationship",
        title = "Self relationship",
        category = "Relationships",
        source = """
            erDiagram
                EMPLOYEE {
                    int id PK
                    int manager_id FK
                }
                EMPLOYEE o|--o{ EMPLOYEE : manages
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_multiple_relationships",
        title = "Multiple relationships",
        category = "Relationships",
        source = """
            erDiagram
                PERSON ||--o{ ADDRESS : lives_at
                PERSON ||--o{ ADDRESS : bills_to
                PERSON ||..o{ ADDRESS : visits
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_direction_lr",
        title = "Left to right",
        category = "Direction",
        source = """
            erDiagram
                direction LR
                CUSTOMER ||--o{ ORDER : places
                ORDER ||--|{ LINE_ITEM : contains
                PRODUCT ||--o{ LINE_ITEM : appears_in
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_direction_rl",
        title = "Right to left",
        category = "Direction",
        source = """
            erDiagram
                direction RL
                AUTHOR ||--o{ BOOK : writes
                BOOK }o--o{ CATEGORY : belongs_to
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_direction_bt",
        title = "Bottom to top",
        category = "Direction",
        source = """
            erDiagram
                direction BT
                REGION ||--o{ STORE : contains
                STORE ||--o{ EMPLOYEE : employs
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_class_style",
        title = "Class styles",
        category = "Styling",
        source = """
            erDiagram
                classDef primary fill:#dbeafe,stroke:#2563eb,color:#172554
                CUSTOMER:::primary ||--o{ ORDER : places
                CUSTOMER {
                    int id PK
                }
                ORDER {
                    int id PK
                }
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_direct_style",
        title = "Direct styles",
        category = "Styling",
        source = """
            erDiagram
                USER ||--o{ SESSION : opens
                style USER fill:#dcfce7,stroke:#16a34a,color:#14532d
                style SESSION fill:#fee2e2,stroke:#dc2626,color:#7f1d1d
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_subgraphs",
        title = "Subgraphs",
        category = "Subgraphs",
        source = """
            erDiagram
                subgraph sales [Sales]
                    CUSTOMER
                    ORDER
                    CUSTOMER ||--o{ ORDER : places
                end
                subgraph catalog [Catalog]
                    PRODUCT
                    CATEGORY
                    CATEGORY ||--o{ PRODUCT : groups
                end
                ORDER }o--o{ PRODUCT : contains
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_nested_subgraphs",
        title = "Nested subgraphs",
        category = "Subgraphs",
        source = """
            erDiagram
                subgraph commerce [Commerce]
                    direction LR
                    CUSTOMER
                    subgraph fulfillment [Fulfillment]
                        direction TB
                        ORDER
                        SHIPMENT
                        ORDER ||--o| SHIPMENT : creates
                    end
                end
                CUSTOMER ||--o{ ORDER : places
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_subgraph_relationships",
        title = "Subgraph relationships",
        category = "Subgraphs",
        source = """
            erDiagram
                subgraph frontend [Frontend]
                    WEB
                    MOBILE
                end
                subgraph backend [Backend]
                    API
                    DATABASE
                    API ||--|| DATABASE : reads
                end
                frontend ||--o{ backend : calls
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_title_accessibility",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            ---
            title: Commerce data model
            ---
            erDiagram
                accTitle: Commerce entities
                accDescr: Customers place orders containing products
                CUSTOMER ||--o{ ORDER : places
                ORDER ||--|{ PRODUCT : contains
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_long_labels",
        title = "Long labels",
        category = "Stress",
        source = """
            erDiagram
                customer_account["Enterprise Customer Account"] {
                    bigint customer_account_identifier PK "Stable account identifier"
                    string primary_contact_email UK "Primary notification destination"
                    timestamp last_successful_authentication_at
                }
                subscription_contract["Subscription Contract"] {
                    bigint contract_identifier PK
                    bigint customer_account_identifier FK
                }
                customer_account ||--o{ subscription_contract : "owns subscription contracts"
        """.trimIndent(),
    ),
    ErDemo(
        id = "er_dense_commerce",
        title = "Dense commerce model",
        category = "Stress",
        source = """
            erDiagram
                CUSTOMER {
                    int id PK
                    string email UK
                }
                ADDRESS {
                    int id PK
                    int customer_id FK
                }
                ORDER {
                    int id PK
                    int customer_id FK
                    int address_id FK
                }
                LINE_ITEM {
                    int order_id PK, FK
                    int product_id PK, FK
                    int quantity
                }
                PRODUCT {
                    int id PK
                    int category_id FK
                }
                CATEGORY {
                    int id PK
                    string name UK
                }
                PAYMENT {
                    int id PK
                    int order_id FK
                }
                SHIPMENT {
                    int id PK
                    int order_id FK
                }
                CUSTOMER ||--o{ ADDRESS : owns
                CUSTOMER ||--o{ ORDER : places
                ADDRESS ||--o{ ORDER : fulfills
                ORDER ||--|{ LINE_ITEM : contains
                PRODUCT ||--o{ LINE_ITEM : appears_in
                CATEGORY ||--o{ PRODUCT : groups
                ORDER ||--o{ PAYMENT : receives
                ORDER ||--o| SHIPMENT : creates
        """.trimIndent(),
    ),
)
