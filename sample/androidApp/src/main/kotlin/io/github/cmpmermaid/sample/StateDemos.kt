package io.github.cmpmermaid.sample

internal data class StateDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val stateDemos = listOf(
    StateDemo(
        id = "state_basic",
        title = "Basic lifecycle",
        category = "States",
        source = """
            stateDiagram-v2
                [*] --> Still
                Still --> Moving
                Moving --> Still
                Moving --> Crash
                Crash --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_legacy_header",
        title = "Legacy header",
        category = "States",
        source = """
            stateDiagram
                [*] --> Ready
                Ready --> Running
                Running --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_alias",
        title = "State alias",
        category = "Labels",
        source = """
            stateDiagram-v2
                state "Waiting for payment" as pending
                [*] --> pending
                pending --> Completed
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_description",
        title = "State descriptions",
        category = "Labels",
        source = """
            stateDiagram-v2
                Service : Ready
                Service : Waiting for work
                Service --> Complete : finish
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_markdown",
        title = "Markdown labels",
        category = "Labels",
        source = """
            stateDiagram-v2
                Draft : **Draft**
                Review : _Needs approval_
                Draft --> Review : **submit**
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_transition_labels",
        title = "Transition labels",
        category = "Transitions",
        source = """
            stateDiagram-v2
                Idle --> Loading : open
                Loading --> Ready : success
                Loading --> Error : timeout
                Error --> Loading : retry
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_self_loop",
        title = "Self transition",
        category = "Transitions",
        source = """
            stateDiagram-v2
                Active --> Active : refresh
                Active --> [*] : close
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_choice",
        title = "Choice",
        category = "Pseudostates",
        source = """
            stateDiagram-v2
                state decision <<choice>>
                [*] --> Check
                Check --> decision
                decision --> Accepted : valid
                decision --> Rejected : invalid
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_fork_join",
        title = "Fork and join",
        category = "Pseudostates",
        source = """
            stateDiagram-v2
                state split <<fork>>
                state merge <<join>>
                [*] --> split
                split --> LoadProfile
                split --> LoadSettings
                LoadProfile --> merge
                LoadSettings --> merge
                merge --> Ready
                Ready --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_note_right",
        title = "Right note",
        category = "Notes",
        source = """
            stateDiagram-v2
                Pending : Awaiting confirmation
                note right of Pending : Expires after 15 minutes
                Pending --> Confirmed
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_notes_both_sides",
        title = "Notes on both sides",
        category = "Notes",
        source = """
            stateDiagram-v2
                Draft : Editable
                note left of Draft : Created by author
                Review : Locked
                note right of Review
                    Requires two approvals
                    before publication.
                end note
                Draft --> Review
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_composite",
        title = "Composite state",
        category = "Composite",
        source = """
            stateDiagram-v2
                [*] --> Review
                state Review {
                    [*] --> Screening
                    Screening --> Decision
                    Decision --> [*]
                }
                Review --> Published
                Published --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_named_composite",
        title = "Named composite",
        category = "Composite",
        source = """
            stateDiagram-v2
                NamedComposite : Processing request
                state NamedComposite {
                    [*] --> Validate
                    Validate --> Persist
                    Persist --> [*]
                }
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_nested_composite",
        title = "Nested composites",
        category = "Composite",
        source = """
            stateDiagram-v2
                state Checkout {
                    [*] --> Payment
                    state Payment {
                        [*] --> Authorize
                        Authorize --> Capture
                        Capture --> [*]
                    }
                    Payment --> Receipt
                    Receipt --> [*]
                }
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_sibling_composites",
        title = "Sibling composites",
        category = "Composite",
        source = """
            stateDiagram-v2
                state First {
                    [*] --> One
                    One --> [*]
                }
                state Second {
                    [*] --> Two
                    Two --> [*]
                }
                First --> Second
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_concurrency",
        title = "Concurrent regions",
        category = "Composite",
        source = """
            stateDiagram-v2
                [*] --> Active
                state Active {
                    [*] --> NumLockOff
                    NumLockOff --> NumLockOn
                    --
                    [*] --> CapsLockOff
                    CapsLockOff --> CapsLockOn
                    --
                    [*] --> ScrollLockOff
                    ScrollLockOff --> ScrollLockOn
                }
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_direction_lr",
        title = "Left to right",
        category = "Direction",
        source = """
            stateDiagram-v2
                direction LR
                [*] --> Draft
                Draft --> Review
                Review --> Published
                Published --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_direction_rl",
        title = "Right to left",
        category = "Direction",
        source = """
            stateDiagram-v2
                direction RL
                [*] --> Draft
                Draft --> Review
                Review --> Published
                Published --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_nested_direction",
        title = "Nested direction",
        category = "Direction",
        source = """
            stateDiagram-v2
                direction LR
                [*] --> Parent
                state Parent {
                    direction LR
                    One --> Two
                    Two --> Three
                }
                Parent --> [*]
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_class_styles",
        title = "Class definitions",
        category = "Styling",
        source = """
            stateDiagram-v2
                classDef active fill:#dcfce7,stroke:#16a34a,color:#14532d
                classDef failed fill:#fee2e2,stroke:#dc2626,color:#7f1d1d
                [*] --> Running
                Running --> Failed
                class Running active
                class Failed failed
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_inline_class",
        title = "Inline classes",
        category = "Styling",
        source = """
            stateDiagram-v2
                classDef warm fill:#ffedd5,stroke:#ea580c,color:#7c2d12
                [*] --> Pending:::warm
                Pending --> Complete
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_direct_style",
        title = "Direct styles",
        category = "Styling",
        source = """
            stateDiagram-v2
                Draft --> Review
                Review --> Done
                style Review fill:#dbeafe,stroke:#2563eb,stroke-width:3px
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_link",
        title = "Link and tooltip",
        category = "Interaction",
        source = """
            stateDiagram-v2
                Draft --> Review
                click Review href "https://github.com"
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_title_accessibility",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            ---
            title: Order lifecycle
            ---
            stateDiagram-v2
                accTitle: Order states
                accDescr: Tracks an order from draft to completion
                Draft --> Complete
        """.trimIndent(),
    ),
    StateDemo(
        id = "state_dense_workflow",
        title = "Dense workflow",
        category = "Stress",
        source = """
            stateDiagram-v2
                [*] --> Intake
                Intake --> Validate : submit
                Validate --> Enrich : valid
                Validate --> Rejected : invalid
                Enrich --> Route
                Route --> Manual : complex
                Route --> Automatic : simple
                Manual --> Complete : approved
                Manual --> Rejected : denied
                Automatic --> Complete : success
                Automatic --> Manual : fallback
                Rejected --> Intake : retry
                Complete --> [*]
        """.trimIndent(),
    ),
)
