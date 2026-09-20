package com.swithun.cmpmermaid.debugui

internal data class ClassDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val classDemos = listOf(
    ClassDemo(
        id = "class_basic",
        title = "Basic classes",
        category = "Classes",
        source = """
            classDiagram
                Animal <|-- Duck
                Animal <|-- Fish
                Animal : +int age
                Animal : +isMammal()
                Duck : +swim()
                Fish : -int size
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_compartments",
        title = "Members and methods",
        category = "Classes",
        source = """
            classDiagram
                class BankAccount {
                    +String owner
                    -BigDecimal balance
                    +deposit(amount) bool
                    +withdraw(amount) int
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_annotations",
        title = "Annotations",
        category = "Classes",
        source = """
            classDiagram
                class Shape {
                    <<interface>>
                    +draw()
                }
                class Color {
                    <<enumeration>>
                    RED
                    GREEN
                    BLUE
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_aliases",
        title = "Labels and quoted IDs",
        category = "Classes",
        source = """
            classDiagram
                class Animal["Animal with a label"]
                class `Vehicle Type`
                Animal --> `Vehicle Type`
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_generics",
        title = "Generic types",
        category = "Classes",
        source = """
            classDiagram
                class Repository~Entity~ {
                    -List~Entity~ items
                    +findAll() List~Entity~
                    +map(Map~String,Entity~ input)
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_member_classifiers",
        title = "Visibility and classifiers",
        category = "Classes",
        source = """
            classDiagram
                class Service {
                    +publicField
                    -privateField
                    #protectedField
                    ~packageField
                    +staticValue$
                    +abstractCall()*
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_relation_markers",
        title = "Relation markers",
        category = "Relations",
        source = """
            classDiagram
                A o-- B : aggregation
                C *-- D : composition
                E <|-- F : inheritance
                G <.. H : dependency
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_reverse_markers",
        title = "Reverse markers",
        category = "Relations",
        source = """
            classDiagram
                A --o B : aggregation
                C --* D : composition
                E --|> F : inheritance
                G ..> H : dependency
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_two_way_relation",
        title = "Two-way relation",
        category = "Relations",
        source = """
            classDiagram
                Animal <|--|> Zebra
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_lollipop",
        title = "Lollipop interfaces",
        category = "Relations",
        source = """
            classDiagram
                class Service {
                    +execute()
                }
                Service --() Port
                Input ()-- Service
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_line_patterns",
        title = "Solid and dashed links",
        category = "Relations",
        source = """
            classDiagram
                A -- B : solid
                C .. D : dashed
                E --> F : association
                G ..> H : dependency
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_cardinality",
        title = "Cardinality",
        category = "Relations",
        source = """
            classDiagram
                Customer "1" --> "0..*" Order : places
                Order "1" *-- "1..*" LineItem : contains
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_self_relation",
        title = "Self relation",
        category = "Relations",
        source = """
            classDiagram
                Employee "0..1" --> "0..*" Employee : manages
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_notes",
        title = "Notes",
        category = "Notes",
        source = """
            classDiagram
                note "A general note"
                note for Account "Primary account<br/>Audited monthly"
                class Account {
                    +String id
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_namespace",
        title = "Namespace",
        category = "Namespaces",
        source = """
            classDiagram
                namespace BaseShapes {
                    class Triangle
                    class Rectangle {
                        +double width
                        +double height
                    }
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_namespace_label",
        title = "Namespace label",
        category = "Namespaces",
        source = """
            classDiagram
                namespace Auth["Authentication Service"] {
                    class UserService {
                        +login()
                        +logout()
                    }
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_nested_namespaces",
        title = "Nested namespaces",
        category = "Namespaces",
        source = """
            classDiagram
                namespace Platform {
                    namespace Auth {
                        class UserService
                    }
                    namespace Data {
                        class Repository
                    }
                    class Gateway
                }
                Gateway --> UserService : delegates
                Gateway --> Repository : delegates
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_compact_namespaces",
        title = "Compact namespaces",
        category = "Namespaces",
        source = """
            ---
            config:
              class:
                hierarchicalNamespaces: false
            ---
            classDiagram
                namespace Company.Engineering.Backend {
                    class Developer
                }
                namespace Company.Engineering.Frontend {
                    class Designer
                }
                Developer --> Designer : collaborates
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_direction_rl",
        title = "Right-to-left direction",
        category = "Layout",
        source = """
            classDiagram
                direction RL
                Student "1" --o "1" IdCard : carries
                Student "1" --o "1" Bike : rides
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_empty_compartments",
        title = "Empty compartments",
        category = "Configuration",
        source = """
            classDiagram
                class Duck
                class Goose {
                }
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_hide_empty_compartments",
        title = "Hide empty compartments",
        category = "Configuration",
        source = """
            ---
            config:
              class:
                hideEmptyMembersBox: true
            ---
            classDiagram
                class Duck
                class Goose
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_inline_styles",
        title = "Inline styles",
        category = "Styling",
        source = """
            classDiagram
                class Animal
                class Mineral
                style Animal fill:#f9f,stroke:#333,stroke-width:4px
                style Mineral fill:#bbf,stroke:#f66,stroke-width:2px,color:#fff,stroke-dasharray:5 5
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_def_styles",
        title = "Class definitions",
        category = "Styling",
        source = """
            classDiagram
                class Animal:::warm {
                    -int size
                    -canEat()
                }
                class Mineral
                classDef default fill:#f6f7f9,color:#28253d
                classDef warm fill:#ffe4c7,stroke:#b45309,color:#7c2d12
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_links",
        title = "Links and tooltips",
        category = "Interaction",
        source = """
            classDiagram
                class Repository
                link Repository "https://github.com" "Open repository"
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_title_accessibility",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            ---
            title: Service model
            ---
            classDiagram
                accTitle: Service class diagram
                accDescr: A gateway delegates work to a repository
                Gateway --> Repository : delegates
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_unicode",
        title = "Unicode labels",
        category = "Text",
        source = """
            classDiagram
                class 用户 {
                    +名称
                    +登录()
                }
                用户 --> 服务 : 调用 ✓
        """.trimIndent(),
    ),
    ClassDemo(
        id = "class_dense_model",
        title = "Dense service model",
        category = "Stress",
        source = """
            classDiagram
                class Client
                class Gateway {
                    +route()
                    +authenticate()
                }
                class Service {
                    +execute()
                }
                class Cache {
                    +get()
                    +put()
                }
                class Repository {
                    +find()
                    +save()
                }
                Client --> Gateway : requests
                Gateway --> Service : delegates
                Service --> Cache : reads
                Service --> Repository : persists
                Repository --> Cache : invalidates
        """.trimIndent(),
    ),
)
