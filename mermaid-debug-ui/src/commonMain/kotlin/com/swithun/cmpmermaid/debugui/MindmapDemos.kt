package com.swithun.cmpmermaid.debugui

internal data class MindmapDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
    val initialAspectRatio: Float = 1.35f,
)

internal val mindmapDemos = listOf(
    MindmapDemo(
        id = "mindmap_basic_hierarchy",
        title = "Basic hierarchy",
        category = "Syntax",
        source = """
            mindmap
              Root
                Planning
                  Scope
                  Risks
                Delivery
                  Build
                  Verify
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_all_shapes",
        title = "All supported shapes",
        category = "Shapes",
        source = """
            mindmap
              root((Mindmap))
                default
                square[Square]
                rounded(Rounded)
                circle((Circle))
                cloud)Cloud(
                bang))Bang((
                hex{{Hexagon}}
        """.trimIndent(),
        initialAspectRatio = 1.55f,
    ),
    MindmapDemo(
        id = "mindmap_irregular_indentation",
        title = "Irregular indentation",
        category = "Syntax",
        source = """
            mindmap
              Root
                  Parent
                        First child
                    Second child
                          Grandchild
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_markdown",
        title = "Markdown labels",
        category = "Text",
        source = """
            mindmap
              root((**Product strategy**))
                *Customer* outcomes
                  **Fast** onboarding
                  Reliable delivery
                *Business* outcomes
                  Sustainable growth
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_multiline_text",
        title = "Wrapped and multiline labels",
        category = "Text",
        source = """
            ---
            config:
              mindmap:
                maxNodeWidth: 120
            ---
            mindmap
              Root
                A deliberately long label that wraps across multiple lines
                  Another long description for text measurement
                Explicit<br/>line break
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_unicode",
        title = "Unicode labels",
        category = "Text",
        source = """
            mindmap
              root((全球计划))
                日本語
                  品質確認
                한국어
                  출시 준비
                Español
                  Revisión final
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_entities",
        title = "Escaped entities",
        category = "Text",
        source = """
            mindmap
              Root &amp; context
                Input &lt; validation
                Output &gt; delivery
                Quoted &quot;result&quot;
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_comments",
        title = "Comments",
        category = "Syntax",
        source = """
            mindmap
              Root
                Discovery
                %% This comment is removed before parsing.
                  Interviews
                  Research
                Delivery
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_frontmatter_title",
        title = "Frontmatter title",
        category = "Metadata",
        source = """
            ---
            title: Platform roadmap
            ---
            mindmap
              Roadmap
                Foundation
                Adoption
                Scale
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_deep_tree",
        title = "Deep hierarchy",
        category = "Hierarchy",
        source = """
            mindmap
              Root
                Program
                  Workstream
                    Initiative
                      Milestone
                        Deliverable
                          Verification
        """.trimIndent(),
        initialAspectRatio = 1.7f,
    ),
    MindmapDemo(
        id = "mindmap_wide_tree",
        title = "Twelve primary sections",
        category = "Hierarchy",
        source = """
            mindmap
              Root
                Strategy
                Research
                Design
                Architecture
                Security
                Privacy
                Development
                Testing
                Operations
                Analytics
                Support
                Governance
        """.trimIndent(),
        initialAspectRatio = 1.7f,
    ),
    MindmapDemo(
        id = "mindmap_balanced_tree",
        title = "Balanced delivery plan",
        category = "Hierarchy",
        source = """
            mindmap
              root((Delivery))
                Discover
                  Users
                  Constraints
                  Measures
                Design
                  Flows
                  Components
                  Content
                Build
                  Client
                  Service
                  Data
                Operate
                  Deploy
                  Observe
                  Improve
        """.trimIndent(),
        initialAspectRatio = 1.55f,
    ),
    MindmapDemo(
        id = "mindmap_mixed_shapes",
        title = "Nested mixed shapes",
        category = "Shapes",
        source = """
            mindmap
              root((Release))
                plan[Plan]
                  scope(Scope)
                  risks{{Risks}}
                execute)Execute(
                  build[Build]
                  verify((Verify))
                decide))Decision((
                  ship(Ship)
                  hold[Hold]
        """.trimIndent(),
        initialAspectRatio = 1.5f,
    ),
    MindmapDemo(
        id = "mindmap_single_node",
        title = "Single concept",
        category = "Hierarchy",
        source = """
            mindmap
              root((One idea))
        """.trimIndent(),
        initialAspectRatio = 1f,
    ),
    MindmapDemo(
        id = "mindmap_cose_layout",
        title = "CoSE-Bilkent layout",
        category = "Layout",
        source = """
            ---
            config:
              layout: cose-bilkent
            ---
            mindmap
              Root
                Explore
                  Evidence
                  Alternatives
                Decide
                  Tradeoffs
                  Commitment
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_dagre_layout",
        title = "Dagre layout",
        category = "Layout",
        source = """
            ---
            config:
              layout: dagre
            ---
            mindmap
              Root
                Input
                  Validate
                  Normalize
                Process
                  Execute
                  Record
                Output
                  Notify
        """.trimIndent(),
        initialAspectRatio = 1.1f,
    ),
    MindmapDemo(
        id = "mindmap_tidy_tree_layout",
        title = "Tidy-tree layout",
        category = "Layout",
        source = """
            ---
            config:
              layout: tidy-tree
            ---
            mindmap
              Root
                Left branch
                  Alpha
                  Beta
                Right branch
                  Gamma
                  Delta
        """.trimIndent(),
        initialAspectRatio = 1.65f,
    ),
    MindmapDemo(
        id = "mindmap_sizing_config",
        title = "Sizing configuration",
        category = "Configuration",
        source = """
            ---
            config:
              mindmap:
                padding: 18
                maxNodeWidth: 110
                useMaxWidth: false
            ---
            mindmap
              Root
                Compact nodes use explicit padding
                Long labels wrap at the configured maximum node width
        """.trimIndent(),
    ),
    MindmapDemo(
        id = "mindmap_theme_variables",
        title = "Theme variables",
        category = "Styling",
        source = """
            ---
            config:
              theme: base
              themeVariables:
                mainBkg: "#f8fafc"
                nodeBorder: "#334155"
                git0: "#0f766e"
                gitBranchLabel0: "#ffffff"
                cScale0: "#ccfbf1"
                cScale1: "#dbeafe"
                cScale2: "#fef3c7"
                cScaleInv0: "#134e4a"
                cScaleInv1: "#1e3a8a"
                cScaleInv2: "#78350f"
                cScaleLabel0: "#134e4a"
                cScaleLabel1: "#1e3a8a"
                cScaleLabel2: "#78350f"
            ---
            mindmap
              root((Custom theme))
                Product
                  Experience
                Platform
                  Reliability
                Operations
                  Readiness
        """.trimIndent(),
        initialAspectRatio = 1.5f,
    ),
    MindmapDemo(
        id = "mindmap_complex_program",
        title = "Complex program map",
        category = "Complex",
        source = """
            mindmap
              root((Modernization program))
                Outcomes
                  Faster delivery
                  Lower operating cost
                  Better reliability
                Workstreams
                  Client migration
                    Android
                    iOS
                    Web
                  Service decomposition
                    APIs
                    Data ownership
                    Event contracts
                  Operational readiness
                    Metrics
                    Alerts
                    Runbooks
                Governance
                  Architecture reviews
                  Security reviews
                  Release criteria
                Risks
                  Dependency timing
                  Capacity constraints
                  Adoption gaps
        """.trimIndent(),
        initialAspectRatio = 1.7f,
    ),
)
