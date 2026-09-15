package com.swithun.cmpmermaid.debugui

internal data class RequirementDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val requirementDemos = listOf(
    RequirementDemo(
        id = "requirement_official_basic",
        title = "Requirement and element",
        category = "Syntax",
        source = """
            requirementDiagram

            requirement test_req {
              id: 1
              text: the test text.
              risk: high
              verifymethod: test
            }

            element test_entity {
              type: simulation
            }

            test_entity - satisfies -> test_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_all_types",
        title = "All requirement types",
        category = "Requirements",
        source = """
            requirementDiagram
              direction LR
              requirement base_req {
                id: 1
                text: Base capability
                risk: low
                verifyMethod: analysis
              }
              functionalRequirement functional_req {
                id: 1.1
                text: Functional behavior
                risk: medium
                verifyMethod: test
              }
              interfaceRequirement interface_req {
                id: 1.2
                text: External interface
                risk: high
                verifyMethod: inspection
              }
              performanceRequirement performance_req {
                id: 1.3
                text: Performance target
                risk: medium
                verifyMethod: demonstration
              }
              physicalRequirement physical_req {
                id: 1.4
                text: Physical constraint
                risk: low
                verifyMethod: inspection
              }
              designConstraint design_req {
                id: 1.5
                text: Design restriction
                risk: high
                verifyMethod: analysis
              }
              base_req - contains -> functional_req
              base_req - contains -> interface_req
              functional_req - derives -> performance_req
              interface_req - traces -> physical_req
              performance_req - refines -> design_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_all_relations",
        title = "All relationship types",
        category = "Relationships",
        source = """
            requirementDiagram
              direction LR
              requirement source_req {
                id: SRC
              }
              requirement target_req {
                id: DST
              }
              element verifier {
                type: "test suite"
              }
              source_req - contains -> target_req
              source_req - copies -> target_req
              source_req - derives -> target_req
              verifier - satisfies -> target_req
              verifier - verifies -> target_req
              source_req - refines -> target_req
              source_req - traces -> target_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_reverse_relation",
        title = "Reverse relationship syntax",
        category = "Relationships",
        source = """
            requirementDiagram
              requirement account_req {
                id: "ACCOUNT-1"
                text: Recover account access
                risk: high
                verifyMethod: test
              }
              element recovery_flow {
                type: workflow
                docRef: docs/recovery
              }
              account_req <- satisfies - recovery_flow
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_direction_tb",
        title = "Top to bottom",
        category = "Direction",
        source = """
            requirementDiagram
              direction TB
              requirement parent_req {
                id: "DIR-1"
                text: Parent requirement
                risk: medium
                verifyMethod: analysis
              }
              requirement child_req {
                id: "DIR-1.1"
                text: Child requirement
                risk: low
                verifyMethod: test
              }
              parent_req - contains -> child_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_direction_bt",
        title = "Bottom to top",
        category = "Direction",
        source = """
            requirementDiagram
              direction BT
              requirement parent_req {
                id: "DIR-1"
                text: Parent requirement
                risk: medium
                verifyMethod: analysis
              }
              requirement child_req {
                id: "DIR-1.1"
                text: Child requirement
                risk: low
                verifyMethod: test
              }
              parent_req - contains -> child_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_direction_lr",
        title = "Left to right",
        category = "Direction",
        source = """
            requirementDiagram
              direction LR
              requirement parent_req {
                id: "DIR-1"
                text: Parent requirement
                risk: medium
                verifyMethod: analysis
              }
              requirement child_req {
                id: "DIR-1.1"
                text: Child requirement
                risk: low
                verifyMethod: test
              }
              parent_req - contains -> child_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_direction_rl",
        title = "Right to left",
        category = "Direction",
        source = """
            requirementDiagram
              direction RL
              requirement parent_req {
                id: "DIR-1"
                text: Parent requirement
                risk: medium
                verifyMethod: analysis
              }
              requirement child_req {
                id: "DIR-1.1"
                text: Child requirement
                risk: low
                verifyMethod: test
              }
              parent_req - contains -> child_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_markdown",
        title = "Markdown labels",
        category = "Text",
        source = """
            requirementDiagram
              requirement "**Secure checkout**" {
                id: "SEC-1"
                text: "*Encrypt* payment details and **verify** every response"
                risk: high
                verifyMethod: inspection
              }
              element "__Gateway adapter__" {
                type: service
                docRef: "docs/**gateway**"
              }
              "__Gateway adapter__" - satisfies -> "**Secure checkout**"
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_direct_styles",
        title = "Direct styles",
        category = "Styling",
        source = """
            requirementDiagram
              requirement styled_req {
                id: "STYLE-1"
                text: Directly styled requirement
                risk: medium
                verifyMethod: demonstration
              }
              element styled_component {
                type: component
              }
              style styled_req fill:#dcfce7,stroke:#15803d,color:#14532d,stroke-width:3px
              style styled_component fill:#dbeafe,stroke:#1d4ed8,color:#1e3a8a
              styled_component - satisfies -> styled_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_classes",
        title = "Reusable classes",
        category = "Styling",
        source = """
            requirementDiagram
              requirement auth_req:::critical {
                id: "AUTH-1"
                text: Authenticate every protected request
                risk: high
                verifyMethod: test
              }
              element api_gateway {
                type: gateway
              }
              classDef critical fill:#fee2e2,stroke:#b91c1c,color:#7f1d1d,stroke-width:3px
              class api_gateway critical
              api_gateway - satisfies -> auth_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_default_class",
        title = "Default class",
        category = "Styling",
        source = """
            requirementDiagram
              requirement availability {
                id: "SLO-1"
                text: Maintain service availability
                risk: high
                verifyMethod: analysis
              }
              element monitor {
                type: observability
              }
              classDef default fill:#f8fafc,stroke:#475569,color:#0f172a
              monitor - verifies -> availability
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_accessibility",
        title = "Accessibility metadata",
        category = "Metadata",
        source = """
            requirementDiagram
              accTitle: Checkout requirement model
              accDescr {
                Payment requirements are connected to implementation
                and verification evidence.
              }
              requirement checkout {
                id: "PAY-1"
                text: Accept online payments
                risk: high
                verifyMethod: test
              }
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_frontmatter_title",
        title = "Frontmatter title",
        category = "Metadata",
        source = """
            ---
            title: Account recovery requirements
            ---
            requirementDiagram
              requirement recovery {
                id: "REC-1"
                text: Restore access after identity verification
                risk: high
                verifyMethod: test
              }
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_classic_dagre",
        title = "Classic Dagre appearance",
        category = "Configuration",
        source = """
            ---
            config:
              layout: dagre
              theme: default
              look: classic
            ---
            requirementDiagram
              direction LR
              requirement checkout {
                id: "PAY-1"
                text: Accept online payment
                risk: high
                verifyMethod: test
              }
              element payment_service {
                type: service
              }
              payment_service - satisfies -> checkout
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_theme_variables",
        title = "Theme variables",
        category = "Styling",
        source = """
            ---
            config:
              themeVariables:
                requirementBackground: "#ecfeff"
                requirementBorderColor: "#0e7490"
                requirementTextColor: "#164e63"
                relationColor: "#be123c"
                relationLabelColor: "#881337"
                requirementEdgeLabelBackground: "#fff1f2"
            ---
            requirementDiagram
              requirement themed_req {
                id: "THEME-1"
                text: Match the host visual language
                risk: medium
                verifyMethod: inspection
              }
              element themed_component {
                type: component
              }
              themed_component - traces -> themed_req
        """.trimIndent(),
    ),
    RequirementDemo(
        id = "requirement_large_system",
        title = "System requirement network",
        category = "Complex",
        source = """
            requirementDiagram
              direction LR
              requirement platform {
                id: "SYS-1"
                text: Provide a resilient transaction platform
                risk: high
                verifyMethod: demonstration
              }
              functionalRequirement checkout {
                id: "SYS-1.1"
                text: Complete checkout without duplicate orders
                risk: high
                verifyMethod: test
              }
              performanceRequirement latency {
                id: "SYS-1.2"
                text: Respond within the agreed latency budget
                risk: medium
                verifyMethod: analysis
              }
              interfaceRequirement gateway_contract {
                id: "SYS-1.3"
                text: Preserve the payment gateway contract
                risk: high
                verifyMethod: inspection
              }
              designConstraint encryption {
                id: "SYS-1.4"
                text: Encrypt sensitive data in transit
                risk: high
                verifyMethod: inspection
              }
              element checkout_service {
                type: service
                docRef: docs/checkout
              }
              element load_suite {
                type: "test suite"
                docRef: tests/load
              }
              element contract_suite {
                type: "test suite"
                docRef: tests/contracts
              }
              platform - contains -> checkout
              platform - contains -> latency
              platform - contains -> gateway_contract
              checkout - derives -> encryption
              checkout_service - satisfies -> checkout
              load_suite - verifies -> latency
              contract_suite - verifies -> gateway_contract
              gateway_contract - traces -> encryption
        """.trimIndent(),
    ),
)
