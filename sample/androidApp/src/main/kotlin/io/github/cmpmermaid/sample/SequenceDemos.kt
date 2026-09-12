package io.github.cmpmermaid.sample

internal data class SequenceDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val sequenceDemos = listOf(
    SequenceDemo(
        id = "sequence_basic",
        title = "Basic exchange",
        category = "Messages",
        source = """
            sequenceDiagram
                Alice->>Bob: Hello Bob
                Bob-->>Alice: Hello Alice
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_participant_aliases",
        title = "Participant aliases",
        category = "Participants",
        source = """
            sequenceDiagram
                participant A as Alice
                actor B as Bob
                A->>B: Request
                B-->>A: Response
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_participant_types",
        title = "Participant types",
        category = "Participants",
        source = """
            sequenceDiagram
                participant P
                actor A
                participant B@{ type: "boundary", alias: "Boundary" }
                participant C@{ type: "control", alias: "Control" }
                participant E@{ type: "entity", alias: "Entity" }
                participant D@{ type: "database", alias: "Database" }
                participant K@{ type: "collections", alias: "Collection" }
                participant Q@{ type: "queue", alias: "Queue" }
                P->>Q: All participant shapes
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_autonumber",
        title = "Autonumber",
        category = "Messages",
        source = """
            sequenceDiagram
                autonumber 3 0.5
                A->>B: First
                B-->>A: Second
                autonumber off
                A->B: Hidden number
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_activation",
        title = "Activation directives",
        category = "Activation",
        source = """
            sequenceDiagram
                A->>B: Request
                activate B
                B->>C: Delegate
                activate C
                C-->>B: Result
                deactivate C
                B-->>A: Response
                deactivate B
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_activation_shortcuts",
        title = "Activation shortcuts",
        category = "Activation",
        source = """
            sequenceDiagram
                Client->>+Service: Open
                Service->>+Database: Query
                Database-->>-Service: Rows
                Service-->>-Client: Result
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_self_message",
        title = "Self messages",
        category = "Messages",
        source = """
            sequenceDiagram
                Worker->>Worker: Validate
                Worker-->>Worker: Retry
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_open_arrows",
        title = "Open arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A->B: Solid open
                B-->A: Dotted open
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_bidirectional_arrows",
        title = "Bidirectional arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A<<->>B: Solid both ways
                B<<-->>A: Dotted both ways
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_cross_point_arrows",
        title = "Cross and point arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A-xB: Solid cross
                B--xA: Dotted cross
                A-)B: Solid point
                B--)A: Dotted point
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_half_arrows",
        title = "Half arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A-|\B: Solid top
                B-|/A: Solid bottom
                A-\\B: Stick top
                B-//A: Stick bottom
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_reverse_half_arrows",
        title = "Reverse half arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A/|-B: Reverse top
                B\|-A: Reverse bottom
                A//-B: Reverse stick top
                B\\-A: Reverse stick bottom
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_dotted_half_arrows",
        title = "Dotted half arrows",
        category = "Arrows",
        source = """
            sequenceDiagram
                A--|\B: Dotted top
                B--|/A: Dotted bottom
                A--\\B: Dotted stick top
                B--//A: Dotted stick bottom
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_note_left_right",
        title = "Side notes",
        category = "Notes",
        source = """
            sequenceDiagram
                participant A
                participant B
                Note left of A: Before
                A->>B: Request
                Note right of B: After
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_note_over",
        title = "Notes over participants",
        category = "Notes",
        source = """
            sequenceDiagram
                participant A
                participant B
                Note over A: Local state
                Note over A,B: Shared state
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_loop",
        title = "Loop",
        category = "Control",
        source = """
            sequenceDiagram
                loop Every minute
                    Client->>Server: Poll
                    Server-->>Client: Status
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_alt",
        title = "Alternative",
        category = "Control",
        source = """
            sequenceDiagram
                Client->>Server: Authenticate
                alt Valid
                    Server-->>Client: Token
                else Invalid
                    Server--xClient: Rejected
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_opt",
        title = "Optional branch",
        category = "Control",
        source = """
            sequenceDiagram
                Client->>Server: Request
                opt Cache miss
                    Server->>Database: Query
                    Database-->>Server: Rows
                end
                Server-->>Client: Response
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_parallel",
        title = "Parallel branches",
        category = "Control",
        source = """
            sequenceDiagram
                par Fetch profile
                    Gateway->>Profile: Read
                and Fetch settings
                    Gateway->>Settings: Read
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_parallel_overlap",
        title = "Overlapping parallel branches",
        category = "Control",
        source = """
            sequenceDiagram
                par_over First branch
                    A->>B: One
                and Second branch
                    A->>C: Two
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_critical",
        title = "Critical region",
        category = "Control",
        source = """
            sequenceDiagram
                critical Establish connection
                    Client->>Server: Connect
                option Timeout
                    Server--xClient: Failed
                option Retry
                    Client->>Server: Connect again
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_break",
        title = "Break region",
        category = "Control",
        source = """
            sequenceDiagram
                Client->>Server: Request
                break Unauthorized
                    Server--xClient: 401
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_rect",
        title = "Background highlight",
        category = "Control",
        source = """
            sequenceDiagram
                rect rgba(240, 80, 80, 0.2)
                    A->>B: Highlighted exchange
                    B-->>A: Result
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_boxes",
        title = "Participant boxes",
        category = "Participants",
        source = """
            sequenceDiagram
                box rgb(235,245,255) Frontend
                    participant App
                    participant Web
                end
                box rgb(240,255,240) Backend
                    participant API
                    participant DB
                end
                App->>API: Request
                API->>DB: Query
                DB-->>API: Rows
                API-->>App: Response
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_create_destroy",
        title = "Create and destroy",
        category = "Lifecycle",
        source = """
            sequenceDiagram
                participant Client
                create participant Worker
                Client->>Worker: Start
                Worker-->>Client: Result
                destroy Worker
                Client-xWorker: Stop
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_create_destroy_actor",
        title = "Create and destroy actor",
        category = "Lifecycle",
        source = """
            sequenceDiagram
                participant Client
                create actor Worker
                Client->>Worker: Start
                destroy Worker
                Client-xWorker: Stop
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_nested_controls",
        title = "Nested controls",
        category = "Control",
        source = """
            sequenceDiagram
                loop Retry
                    A->>B: Request
                    alt Success
                        B-->>A: Result
                    else Failure
                        opt Recoverable
                            A->>B: Retry
                        end
                    end
                end
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_central_connections",
        title = "Central connections",
        category = "Arrows",
        source = """
            sequenceDiagram
                A->>()B: Destination
                A()->>B: Source
                A()->>()B: Both
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_title_accessibility",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            sequenceDiagram
                title Login exchange
                accTitle: Authentication sequence
                accDescr: Client exchanges credentials for a token
                Client->>Server: Login
                Server-->>Client: Token
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_comments",
        title = "Comments and semicolons",
        category = "Syntax",
        source = """
            sequenceDiagram
                %% This line is ignored
                A->>B: First; B-->>A: Second
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_wrapped_text",
        title = "Wrapped text",
        category = "Text",
        source = """
            sequenceDiagram
                participant A as A participant with a long descriptive label
                participant B as Another participant
                A->>B:wrap:This message requests wrapping across the available space
                Note over A,B:wrap:A shared note with enough text to wrap naturally
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_line_breaks",
        title = "Explicit line breaks",
        category = "Text",
        source = """
            sequenceDiagram
                participant A as First<br/>Second
                A->>B: Hello<br>again
                Note over A,B: Shared<br />state
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_unicode",
        title = "Unicode labels",
        category = "Text",
        source = """
            sequenceDiagram
                participant U as User
                participant S as Service
                U->>S: Status ✓
                S-->>U: 完成
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_reverse_direction",
        title = "Messages in both directions",
        category = "Messages",
        source = """
            sequenceDiagram
                A->>C: Forward
                C-->>A: Reverse
                B->>A: Left
                A-->>B: Right
        """.trimIndent(),
    ),
    SequenceDemo(
        id = "sequence_dense_exchange",
        title = "Dense exchange",
        category = "Stress",
        source = """
            sequenceDiagram
                participant Client
                participant Gateway
                participant Service
                participant Cache
                participant Database
                Client->>Gateway: Request
                Gateway->>Service: Validate
                Service->>Cache: Lookup
                Cache-->>Service: Miss
                Service->>Database: Query
                Database-->>Service: Rows
                Service-->>Gateway: Payload
                Gateway-->>Client: Response
        """.trimIndent(),
    ),
)
