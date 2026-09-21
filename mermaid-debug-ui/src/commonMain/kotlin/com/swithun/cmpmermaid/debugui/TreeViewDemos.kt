package com.swithun.cmpmermaid.debugui

internal data class TreeViewDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val treeViewDemos = listOf(
    TreeViewDemo(
        id = "treeview_project",
        title = "Project structure",
        category = "Hierarchy",
        source = """
            treeView-beta
                project/
                    src/
                        main.kt
                        renderer.kt
                    tests/
                        parser-test.kt
                    README.md
        """.trimIndent(),
    ),
    TreeViewDemo(
        id = "treeview_box_drawing",
        title = "Box-drawing input",
        category = "Syntax",
        source = """
            treeView-beta
            ├── packages/
            │   ├── core/
            │   │   └── Engine.kt
            │   └── compose/
            │       └── Diagram.kt
            └── README.md
        """.trimIndent(),
    ),
    TreeViewDemo(
        id = "treeview_annotations",
        title = "Highlights and descriptions",
        category = "Annotations",
        source = """
            treeView-beta
                application/
                    App.kt :::highlight ## main component
                    Routes.kt ## navigation table
                    Theme.kt ## visual tokens
                build.gradle.kts ## build configuration
        """.trimIndent(),
    ),
    TreeViewDemo(
        id = "treeview_icons",
        title = "Built-in icons",
        category = "Icons",
        source = """
            ---
            config:
              treeView:
                showIcons: true
            ---
            treeView-beta
                source/
                    parser.kt
                    renderer.kt icon(file)
                    generated.txt icon(none)
                docs/ icon(folder)
        """.trimIndent(),
    ),
    TreeViewDemo(
        id = "treeview_unicode",
        title = "Accessible multilingual tree",
        category = "Metadata",
        source = """
            treeView-beta
              title Release workspace
              accTitle: Accessible project tree
              accDescr: Source, tests, and localized documentation.
                workspace/
                    source/
                        核心.kt
                    tests/
                        검증.kt
                    documentação/
                        início.md
        """.trimIndent(),
    ),
)
