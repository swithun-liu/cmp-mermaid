package com.swithun.cmpmermaid.debugui

internal data class KanbanDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val kanbanDemos = listOf(
    KanbanDemo(
        id = "kanban_delivery_board",
        title = "Delivery board",
        category = "Official syntax",
        source = """
            kanban
              backlog[Backlog]
                scope[Define scope]
                research[Interview users]
              progress[In progress]
                build[Implement renderer]
              done[Done]
                review[Architecture review]
        """.trimIndent(),
    ),
    KanbanDemo(
        id = "kanban_task_metadata",
        title = "Task metadata",
        category = "Metadata",
        source = """
            ---
            config:
              kanban:
                ticketBaseUrl: "https://issues.example/#TICKET#"
            ---
            kanban
              todo[Todo]
                parser[Translate parser]@{ ticket: KB-101, assigned: Ada, priority: 'Very High' }
                layout[Match layout]@{ ticket: KB-102, assigned: Lin, priority: High }
              verify[Verify]
                screenshots[Review screenshots]@{ priority: Low }
        """.trimIndent(),
    ),
    KanbanDemo(
        id = "kanban_wrapped_labels",
        title = "Wrapped labels and empty stage",
        category = "Layout",
        source = """
            kanban
              planned[Planned work]
                long[Implement a faithful renderer with deterministic text measurement and wrapping]
              waiting[Waiting for approval]
              complete[Complete]
                shipped[Publish the verified release]
        """.trimIndent(),
    ),
    KanbanDemo(
        id = "kanban_indentation_and_comments",
        title = "Flattened indentation",
        category = "Syntax",
        source = """
            kanban
              discover[Discover]
                interviews[Customer interviews]
                  synthesis[Research synthesis]
                %% Deeper indentation remains in the current section.
                    findings[Validated findings]
              deliver[Deliver]
                release[Release candidate]
        """.trimIndent(),
    ),
    KanbanDemo(
        id = "kanban_configured_unicode",
        title = "Configured international board",
        category = "Configuration",
        source = """
            ---
            config:
              theme: base
              look: classic
              kanban:
                sectionWidth: 230
              themeVariables:
                cScale2: "#dbeafe"
                cScale3: "#dcfce7"
                cScaleLabel2: "#1e3a8a"
                cScaleLabel3: "#14532d"
            ---
            kanban
              todo[準備]
                tokyo[東京で確認]@{ assigned: "品質", priority: High }
              done[완료]
                seoul[서울 출시 준비]@{ ticket: KB-서울 }
        """.trimIndent(),
    ),
)
