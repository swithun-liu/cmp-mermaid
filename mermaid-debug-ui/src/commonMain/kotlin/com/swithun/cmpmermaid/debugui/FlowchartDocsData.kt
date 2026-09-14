package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.debugui.generated.flowchartDemos

private val flowchartSyntaxLessons = listOf(
    DiagramSyntaxLesson(
        title = "A node (default)",
        source = "flowchart LR\n  id",
        note = "The node id is displayed when no separate label is provided.",
        initialAspectRatio = 360f / 130f,
    ),
    DiagramSyntaxLesson(
        title = "A node with text",
        source = "flowchart LR\n  id1[This is the text in the box]",
        note = "A label enclosed in square brackets replaces the visible node id.",
        initialAspectRatio = 360f / 140f,
    ),
    DiagramSyntaxLesson(
        title = "Unicode and line breaks",
        source = "flowchart LR\n  A[\"Unicode text\"] --> B[\"Line one<br/>Line two\"]",
        note = "Quoted labels preserve punctuation; br tags create explicit line breaks.",
        initialAspectRatio = 360f / 160f,
    ),
    DiagramSyntaxLesson(
        title = "Direction",
        source = "flowchart LR\n  Start --> Finish",
        note = "Use TB, TD, BT, LR, or RL to control the primary layout direction.",
        initialAspectRatio = 360f / 150f,
    ),
    DiagramSyntaxLesson(
        title = "Classic node shapes",
        source = """
            flowchart LR
              A[Process] --> B(Rounded) --> C([Terminal])
              C --> D[[Subroutine]] --> E[(Database)]
              E --> F{Decision} --> G{{Prepare}}
        """.trimIndent(),
        note = "Classic delimiters select the node geometry without metadata.",
        initialAspectRatio = 360f / 210f,
    ),
    DiagramSyntaxLesson(
        title = "Expanded shape syntax",
        source = """
            flowchart LR
              A@{ shape: doc, label: "Document" }
              A --> B@{ shape: lin-cyl, label: "Disk" }
              B --> C@{ shape: cross-circ, label: "Summary" }
        """.trimIndent(),
        note = "The metadata form supports Mermaid 12 semantic shape names and aliases.",
        initialAspectRatio = 2f,
    ),
    DiagramSyntaxLesson(
        title = "Arrow and open links",
        source = """
            flowchart LR
              A --> B
              A --- C
        """.trimIndent(),
        note = "Three hyphens create an open link; an ending angle bracket adds an arrow.",
        initialAspectRatio = 360f / 170f,
    ),
    DiagramSyntaxLesson(
        title = "Text on links",
        source = """
            flowchart LR
              A -- text --> B
              A -->|pipe label| C
        """.trimIndent(),
        note = "Both middle text and pipe-delimited edge labels are supported.",
        initialAspectRatio = 2f,
    ),
    DiagramSyntaxLesson(
        title = "Dotted, thick, and invisible links",
        source = """
            flowchart LR
              A -. dotted .-> B
              A == thick ==> C
              B ~~~ C
        """.trimIndent(),
        note = "Invisible links affect layout without drawing a visible edge.",
        initialAspectRatio = 360f / 190f,
    ),
    DiagramSyntaxLesson(
        title = "Circle and cross markers",
        source = """
            flowchart LR
              A --o B
              C --x D
              E o--o F
              G x--x H
        """.trimIndent(),
        note = "Circle and cross markers can be attached to either end.",
        initialAspectRatio = 360f / 190f,
    ),
    DiagramSyntaxLesson(
        title = "Multi-directional arrows",
        source = """
            flowchart LR
              A <--> B
              C <==> D
              E <-.-> F
        """.trimIndent(),
        note = "Normal, thick, and dotted links can carry markers at both ends.",
        initialAspectRatio = 360f / 190f,
    ),
    DiagramSyntaxLesson(
        title = "Minimum link length",
        source = """
            flowchart TD
              A --> B
              A ---> C
              A ----> D
        """.trimIndent(),
        note = "Extra dashes request additional ranks between connected nodes.",
        initialAspectRatio = 360f / 260f,
    ),
    DiagramSyntaxLesson(
        title = "Chaining and multiple nodes",
        source = """
            flowchart TB
              A & B --> C & D
              C --> E --> F
        """.trimIndent(),
        note = "Ampersands create compact fan-out and fan-in declarations.",
        initialAspectRatio = 360f / 260f,
    ),
    DiagramSyntaxLesson(
        title = "Edge ids",
        source = """
            flowchart LR
              A e1@--> B
              classDef highlight stroke:#dc2626,stroke-width:3px
              class e1 highlight
        """.trimIndent(),
        note = "An edge id allows later class and metadata assignments.",
        initialAspectRatio = 360f / 160f,
    ),
    DiagramSyntaxLesson(
        title = "Subgraphs",
        source = """
            flowchart TB
              subgraph group [Validation]
                A --> B
              end
              B --> C
        """.trimIndent(),
        note = "Subgraphs group related nodes and may be nested.",
        initialAspectRatio = 360f / 250f,
    ),
    DiagramSyntaxLesson(
        title = "Node styles",
        source = """
            flowchart LR
              A[Start] --> B[Finish]
              style A fill:#dbeafe,stroke:#2563eb,color:#172554,stroke-width:3px
        """.trimIndent(),
        note = "Fill, stroke, text color, width, dash pattern, and font styles are native.",
        initialAspectRatio = 360f / 160f,
    ),
    DiagramSyntaxLesson(
        title = "Classes",
        source = """
            flowchart LR
              A:::source --> B:::result
              classDef source fill:#dbeafe,stroke:#2563eb
              classDef result fill:#dcfce7,stroke:#16a34a
        """.trimIndent(),
        note = "Class definitions can be attached directly with the triple-colon syntax.",
        initialAspectRatio = 360f / 160f,
    ),
    DiagramSyntaxLesson(
        title = "Link styles",
        source = """
            flowchart LR
              A --> B --> C
              linkStyle 0 stroke:#dc2626,stroke-width:4px
              linkStyle 1 stroke:#2563eb,stroke-width:3px
        """.trimIndent(),
        note = "Link indexes follow source declaration order, beginning at zero.",
        initialAspectRatio = 360f / 160f,
    ),
)

internal val flowchartDiagramDocsSpec = DiagramDocsSpec(
    id = "flowchart",
    title = "Flowchart",
    syntaxTitle = "Flowcharts - Basic Syntax",
    description = "A flowchart is composed of nodes and links. Declare its direction after " +
        "the flowchart keyword.",
    documentationUrl = "https://mermaid.js.org/syntax/flowchart.html",
    galleryTitle = "Flowchart demo gallery",
    cases = flowchartDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = demo.officialAspectRatio,
        )
    },
    syntaxLessons = flowchartSyntaxLessons,
    nativeOptions = MermaidRenderOptions(layout = "elk"),
    officialLayout = "elk",
)
