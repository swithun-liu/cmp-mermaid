package com.swithun.cmpmermaid.debugui

internal data class JourneyDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val journeyDemos = listOf(
    JourneyDemo(
        id = "journey_official_basic",
        title = "Working day",
        category = "Syntax",
        source = """
            journey
                title My working day
                section Go to work
                  Make tea: 5: Me
                  Go upstairs: 3: Me
                  Do work: 1: Me, Cat
                section Go home
                  Go downstairs: 5: Me
                  Sit down: 5: Me
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_all_scores",
        title = "All score levels",
        category = "Scores",
        source = """
            journey
                title Score progression
                section Experience
                  Very difficult: 1: Visitor
                  Difficult: 2: Visitor
                  Acceptable: 3: Visitor
                  Good: 4: Visitor
                  Excellent: 5: Visitor
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_multiple_actors",
        title = "Shared tasks",
        category = "Actors",
        source = """
            journey
                title Collaborative release
                section Prepare
                  Define scope: 4: Product, Design
                  Build feature: 3: Android, iOS, Web
                section Validate
                  Review behavior: 4: Quality, Product
                  Approve release: 5: Product, Operations
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_actor_order",
        title = "Alphabetical actor legend",
        category = "Actors",
        source = """
            journey
                title Actor order
                section Handoff
                  Start review: 4: Zoe, Alice
                  Verify result: 5: Mallory, Bob
                  Publish: 5: Zoe, Bob, Alice
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_repeated_actor",
        title = "Repeated actors",
        category = "Actors",
        source = """
            journey
                title Repeated participation
                section Discover
                  Read request: 3: Analyst, Analyst
                  Clarify details: 4: Analyst, Requester
                section Deliver
                  Confirm outcome: 5: Requester, Analyst
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_no_actor",
        title = "Task without actor",
        category = "Actors",
        source = """
            journey
                title Anonymous workflow
                section Background
                  Wait for schedule: 3
                  Complete automatically: 5
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_decimal_score",
        title = "Decimal scores",
        category = "Scores",
        source = """
            journey
                title Gradual satisfaction
                section Evaluation
                  Initial impression: 1.5: Reviewer
                  Working result: 2.75: Reviewer
                  Final result: 4.5: Reviewer
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_many_sections",
        title = "Multi-stage journey",
        category = "Sections",
        source = """
            journey
                title End-to-end delivery
                section Discover
                  Gather context: 4: Research
                  Confirm goals: 5: Research, Product
                section Design
                  Explore options: 3: Design
                  Review proposal: 4: Design, Product
                section Build
                  Implement solution: 3: Engineering
                  Integrate changes: 2: Engineering, Quality
                section Release
                  Verify production: 4: Quality, Operations
                  Confirm outcome: 5: Product, Operations
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_long_tasks",
        title = "Long task labels",
        category = "Text",
        source = """
            journey
                title Detailed customer workflow
                section Account recovery
                  Request a secure account recovery link from the sign-in screen: 3: Customer
                  Verify the recovery request through the registered email address: 2: Customer
                  Choose a new password and confirm access to the account: 5: Customer
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_long_actor",
        title = "Long actor labels",
        category = "Text",
        source = """
            journey
                title Operational approval
                section Review
                  Inspect evidence: 3: Regional Compliance Review Team
                  Resolve findings: 2: Platform Reliability Engineering
                  Approve rollout: 5: Global Release Coordination
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_comments",
        title = "Comments and blank lines",
        category = "Syntax",
        source = """
            journey
                %% Comments are ignored by the Journey lexer.
                title Comment handling

                section Preparation
                  Read requirements: 4: Developer
                  %% This comment separates two tasks.
                  Confirm assumptions: 5: Developer, Reviewer
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_accessibility",
        title = "Accessibility metadata",
        category = "Metadata",
        source = """
            journey
                accTitle: Checkout experience
                accDescr {
                  The diagram follows a customer from product discovery
                  through payment and order confirmation.
                }
                title Checkout journey
                section Discover
                  Compare products: 4: Customer
                section Purchase
                  Enter payment details: 2: Customer
                  Receive confirmation: 5: Customer
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_frontmatter_title",
        title = "Frontmatter title",
        category = "Metadata",
        source = """
            ---
            title: Support request journey
            ---
            journey
                section Report
                  Describe the problem: 3: Customer
                  Attach evidence: 2: Customer
                section Resolve
                  Investigate cause: 3: Support
                  Confirm the fix: 5: Customer, Support
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_custom_geometry",
        title = "Journey configuration",
        category = "Configuration",
        source = """
            ---
            config:
              journey:
                leftMargin: 180
                width: 170
                height: 56
                taskMargin: 64
                taskFontSize: 13
            ---
            journey
                title Configured dimensions
                section Plan
                  Define milestones: 4: Planner
                  Assign ownership: 3: Planner, Engineer
                section Execute
                  Deliver changes: 4: Engineer
                  Review outcome: 5: Reviewer
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_custom_palette",
        title = "Theme variables",
        category = "Styling",
        source = """
            ---
            config:
              themeVariables:
                fillType0: "#e0f2fe"
                fillType1: "#dcfce7"
                fillType2: "#fef3c7"
                textColor: "#1f2937"
            ---
            journey
                title Custom palette
                section Explore
                  Find options: 4: Visitor
                section Decide
                  Compare results: 3: Visitor, Advisor
                section Complete
                  Confirm selection: 5: Visitor
        """.trimIndent(),
    ),
    JourneyDemo(
        id = "journey_many_actors",
        title = "Six actor palette",
        category = "Actors",
        source = """
            journey
                title Cross-functional workflow
                section Coordinate
                  Define scope: 4: Product, Design
                  Build clients: 3: Android, Desktop, iOS, Web
                  Validate release: 4: Android, Design, Desktop, iOS, Product, Web
        """.trimIndent(),
    ),
)
