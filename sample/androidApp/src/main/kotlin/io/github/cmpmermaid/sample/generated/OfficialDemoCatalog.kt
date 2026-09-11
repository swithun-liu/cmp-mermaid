package io.github.cmpmermaid.sample.generated

import io.github.cmpmermaid.sample.R

internal data class FlowchartDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
    val officialDrawable: Int,
    val officialAspectRatio: Float,
)

internal val flowchartDemos: List<FlowchartDemo> = listOf(
    FlowchartDemo(
        id = "basic_syntax",
        title = "Basic syntax",
        category = "Basics",
        source = """
flowchart LR
  A[Hard edge] -->|Link text| B(Round edge)
  B --> C{Decision}
  C -->|One| D[Result one]
  C -->|Two| E[Result two]
        """.trimIndent(),
        officialDrawable = R.drawable.official_basic_syntax,
        officialAspectRatio = 747f / 166f,
    ),
    FlowchartDemo(
        id = "question_workflow",
        title = "Question workflow",
        category = "Basics",
        source = """
flowchart TB
  A([Student asks]) --> B{Enough context?}
  B -- No --> C[Request clarification]
  C --> A
  B -- Yes --> D[[Solve and verify]]
  D --> E[(Answer cache)]
  E --> F((Delivered))
        """.trimIndent(),
        officialDrawable = R.drawable.official_question_workflow,
        officialAspectRatio = 367f / 749f,
    ),
    FlowchartDemo(
        id = "classic_shapes",
        title = "Classic node shapes",
        category = "Nodes",
        source = """
flowchart LR
  A[Rectangle] --> B(Rounded)
  B --> C([Stadium])
  C --> D[[Subroutine]]
  D --> E[(Database)]
  E --> F{Decision}
  F --> G{{Hexagon}}
        """.trimIndent(),
        officialDrawable = R.drawable.official_classic_shapes,
        officialAspectRatio = 984f / 124f,
    ),
    FlowchartDemo(
        id = "input_output_shapes",
        title = "Input and output shapes",
        category = "Nodes",
        source = """
flowchart TB
  A[/Input/] --> B[\Output\]
  B --> C[/Priority\]
  C --> D[\Manual/]
        """.trimIndent(),
        officialDrawable = R.drawable.official_input_output_shapes,
        officialAspectRatio = 181f / 307f,
    ),
    FlowchartDemo(
        id = "edge_labels",
        title = "Edge labels",
        category = "Links",
        source = """
flowchart LR
  A -- text --> B
  A -->|pipe label| C
  B -. dotted .-> D
  C == thick ==> D
        """.trimIndent(),
        officialDrawable = R.drawable.official_edge_labels,
        officialAspectRatio = 421f / 152f,
    ),
    FlowchartDemo(
        id = "edge_markers",
        title = "Edge markers",
        category = "Links",
        source = """
flowchart LR
  A --- B
  B --> C
  C <--> D
  D o--o E
  E x--x F
        """.trimIndent(),
        officialDrawable = R.drawable.official_edge_markers,
        officialAspectRatio = 463f / 64f,
    ),
    FlowchartDemo(
        id = "renderer_pipeline",
        title = "Renderer pipeline",
        category = "Layouts",
        source = """
flowchart LR
  R[/Markdown/] --> P{Diagram type}
  P --> F[Flowchart parser]
  F ==> S[[SceneGraph]]
  S --> C([CMP Canvas])
        """.trimIndent(),
        officialDrawable = R.drawable.official_renderer_pipeline,
        officialAspectRatio = 884f / 166f,
    ),
    FlowchartDemo(
        id = "subgraph",
        title = "Subgraph",
        category = "Subgraphs",
        source = """
flowchart TB
  subgraph client [Native client]
    A[Mermaid source] --> B[Parser]
    B --> C[Layout]
  end
  C --> D[CMP Canvas]
        """.trimIndent(),
        officialDrawable = R.drawable.official_subgraph,
        officialAspectRatio = 185f / 367f,
    ),
    FlowchartDemo(
        id = "nested_subgraphs",
        title = "Nested subgraphs",
        category = "Subgraphs",
        source = """
flowchart LR
  subgraph outer [Rendering engine]
    subgraph syntax [Syntax]
      A[Lexer] --> B[AST]
    end
    subgraph visual [Visual]
      C[Layout] --> D[Scene]
    end
    B --> C
  end
        """.trimIndent(),
        officialDrawable = R.drawable.official_nested_subgraphs,
        officialAspectRatio = 478f / 190f,
    ),
    FlowchartDemo(
        id = "styled_nodes",
        title = "Styled nodes",
        category = "Styling",
        source = """
flowchart LR
  A[Parse] --> B[Layout] --> C[Render]
  classDef parse fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e,stroke-width:2px
  classDef render fill:#dcfce7,stroke:#15803d,color:#14532d,stroke-width:2px
  class A parse
  class C render
        """.trimIndent(),
        officialDrawable = R.drawable.official_styled_nodes,
        officialAspectRatio = 329f / 64f,
    ),
    FlowchartDemo(
        id = "long_labels",
        title = "Long labels",
        category = "Text",
        source = """
flowchart TB
  A["A longer question that needs readable wrapping"] --> B{Can it be answered safely?}
  B -->|Yes| C["Return a concise and verified response"]
  B -->|No| D["Ask for the missing context"]
        """.trimIndent(),
        officialDrawable = R.drawable.official_long_labels,
        officialAspectRatio = 360f / 622f,
    ),
    FlowchartDemo(
        id = "metadata_shapes",
        title = "Metadata shape syntax",
        category = "Nodes",
        source = """
flowchart LR
  A@{ shape: stadium, label: "Start" } --> B@{ shape: hex, label: "Validate" }
  B --> C@{ shape: cyl, label: "Store" }
  C --> D@{ shape: dbl-circ, label: "Stop" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_metadata_shapes,
        officialAspectRatio = 493f / 110f,
    ),
    FlowchartDemo(
        id = "multi_node_links",
        title = "Multi-node links",
        category = "Links",
        source = """
flowchart LR
  A:::source & B:::source --> C & D
  classDef source fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e
        """.trimIndent(),
        officialDrawable = R.drawable.official_multi_node_links,
        officialAspectRatio = 180f / 152f,
    ),
    FlowchartDemo(
        id = "edge_ids_and_length",
        title = "Edge ids and length",
        category = "Links",
        source = """
flowchart LR
  A e1@----> B
  e1@{ animate: true }
  B ~~~ C
  C o--x D
  classDef animated stroke:#dc2626,stroke-width:2px
  class e1 animated
        """.trimIndent(),
        officialDrawable = R.drawable.official_edge_ids_and_length,
        officialAspectRatio = 302f / 64f,
    ),
    FlowchartDemo(
        id = "direction_bottom_to_top",
        title = "Bottom-to-top direction",
        category = "Directions",
        source = """
flowchart BT
  Start --> Validate --> Finish
        """.trimIndent(),
        officialDrawable = R.drawable.official_direction_bottom_to_top,
        officialAspectRatio = 107f / 240f,
    ),
    FlowchartDemo(
        id = "direction_right_to_left",
        title = "Right-to-left direction",
        category = "Directions",
        source = """
flowchart RL
  Start --> Validate --> Finish
        """.trimIndent(),
        officialDrawable = R.drawable.official_direction_right_to_left,
        officialAspectRatio = 327f / 64f,
    ),
    FlowchartDemo(
        id = "unicode_markdown",
        title = "Unicode and Markdown labels",
        category = "Text",
        source = """
---
config:
  htmlLabels: false
---
flowchart LR
  A["This ❤ Unicode"] --> B["`This **is** _Markdown_`"]
  B --> C["Line one<br/>Line two"]
        """.trimIndent(),
        officialDrawable = R.drawable.official_unicode_markdown,
        officialAspectRatio = 439f / 77f,
    ),
    FlowchartDemo(
        id = "expanded_process_shapes",
        title = "Expanded process shapes",
        category = "Expanded shapes",
        source = """
flowchart LR
  A@{ shape: rect, label: "Process" } --> B@{ shape: rounded, label: "Event" }
  B --> C@{ shape: stadium, label: "Terminal" }
  C --> D@{ shape: subproc, label: "Subprocess" }
  D --> E@{ shape: cyl, label: "Database" }
  E --> F@{ shape: circle, label: "Start" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_process_shapes,
        officialAspectRatio = 796f / 115f,
    ),
    FlowchartDemo(
        id = "expanded_control_shapes",
        title = "Expanded control shapes",
        category = "Expanded shapes",
        source = """
flowchart TB
  A@{ shape: odd, label: "Odd" } --> B@{ shape: diamond, label: "Decision" }
  B --> C@{ shape: hex, label: "Prepare" }
  C --> D@{ shape: trap-b, label: "Priority" }
  D --> E@{ shape: trap-t, label: "Manual" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_control_shapes,
        officialAspectRatio = 181f / 525f,
    ),
    FlowchartDemo(
        id = "expanded_storage_shapes",
        title = "Expanded storage shapes",
        category = "Expanded shapes",
        source = """
flowchart LR
  A@{ shape: lin-rect, label: "Lined process" } --> B@{ shape: lin-cyl, label: "Disk" }
  B --> C@{ shape: h-cyl, label: "Direct access" }
  C --> D@{ shape: bow-rect, label: "Stored data" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_storage_shapes,
        officialAspectRatio = 579f / 113f,
    ),
    FlowchartDemo(
        id = "expanded_document_shapes",
        title = "Expanded document shapes",
        category = "Expanded shapes",
        source = """
flowchart LR
  A@{ shape: doc, label: "Document" } --> B@{ shape: docs, label: "Documents" }
  B --> C@{ shape: lin-doc, label: "Lined document" }
  C --> D@{ shape: tag-doc, label: "Tagged document" }
  D --> E@{ shape: tag-rect, label: "Tagged process" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_document_shapes,
        officialAspectRatio = 887f / 119f,
    ),
    FlowchartDemo(
        id = "expanded_special_shapes",
        title = "Expanded special shapes",
        category = "Expanded shapes",
        source = """
flowchart LR
  A@{ shape: sm-circ, label: "Start" } --> B@{ shape: framed-circle, label: "Stop" }
  B --> C@{ shape: fork, label: "Fork" }
  C --> D@{ shape: hourglass, label: "Collate" }
  D --> E@{ shape: braces, label: "Comment" }
  E --> F@{ shape: bolt, label: "Com link" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_special_shapes,
        officialAspectRatio = 497f / 86f,
    ),
    FlowchartDemo(
        id = "expanded_manual_shapes",
        title = "Expanded manual shapes",
        category = "Expanded shapes",
        source = """
flowchart TB
  A@{ shape: curv-trap, label: "Display" } --> B@{ shape: div-rect, label: "Divided" }
  B --> C@{ shape: tri, label: "Extract" }
  C --> D@{ shape: win-pane, label: "Internal" }
  D --> E@{ shape: f-circ, label: "Junction" }
  E --> F@{ shape: notch-pent, label: "Loop limit" }
  F --> G@{ shape: flip-tri, label: "Manual file" }
  G --> H@{ shape: sl-rect, label: "Manual input" }
  H --> I@{ shape: procs, label: "Processes" }
  I --> J@{ shape: flag, label: "Paper tape" }
  J --> K@{ shape: cross-circ, label: "Summary" }
        """.trimIndent(),
        officialDrawable = R.drawable.official_expanded_manual_shapes,
        officialAspectRatio = 150f / 1137f,
    ),
    FlowchartDemo(
        id = "all_link_types",
        title = "All link types",
        category = "Links",
        source = """
flowchart LR
  A --> B
  A --- C
  A -.-> D
  A -.- E
  A ==> F
  A === G
  A ~~~ H
        """.trimIndent(),
        officialDrawable = R.drawable.official_all_link_types,
        officialAspectRatio = 241f / 592f,
    ),
    FlowchartDemo(
        id = "marker_matrix",
        title = "Arrow marker matrix",
        category = "Links",
        source = """
flowchart LR
  A --o B
  C --x D
  E --> F
  G o--o H
  I x--x J
  K <--> L
        """.trimIndent(),
        officialDrawable = R.drawable.official_marker_matrix,
        officialAspectRatio = 142f / 504f,
    ),
    FlowchartDemo(
        id = "marker_styles",
        title = "Markers on thick and dotted links",
        category = "Links",
        source = """
flowchart LR
  A o==o B
  C x==x D
  E <==> F
  G o-.-o H
  I x-.-x J
  K <-.-> L
        """.trimIndent(),
        officialDrawable = R.drawable.official_marker_styles,
        officialAspectRatio = 142f / 504f,
    ),
    FlowchartDemo(
        id = "minimum_link_lengths",
        title = "Minimum link lengths",
        category = "Links",
        source = """
flowchart TD
  A --> B
  A ---> C
  A ----> D
  B -.-> E
  C -..-> E
  D ====> E
        """.trimIndent(),
        officialDrawable = R.drawable.official_minimum_link_lengths,
        officialAspectRatio = 221f / 260f,
    ),
    FlowchartDemo(
        id = "mixed_link_labels",
        title = "Link label syntaxes",
        category = "Links",
        source = """
flowchart LR
  A -- text --> B
  A -->|pipe label| C
  A -. dotted .-> D
  A == thick ==> E
  A -- open text --- F
        """.trimIndent(),
        officialDrawable = R.drawable.official_mixed_link_labels,
        officialAspectRatio = 311f / 416f,
    ),
    FlowchartDemo(
        id = "node_color_formats",
        title = "Node color formats",
        category = "Styling",
        source = """
flowchart LR
  A[Hex] --> B[Short hex] --> C[Named]
  style A fill:#dbeafe,stroke:#1d4ed8,color:#172554,stroke-width:3px
  style B fill:#fce,stroke:#c06,color:#401
  style C fill:lightgreen,stroke:darkgreen,color:black
        """.trimIndent(),
        officialDrawable = R.drawable.official_node_color_formats,
        officialAspectRatio = 337f / 64f,
    ),
    FlowchartDemo(
        id = "link_styles",
        title = "Link colors and widths",
        category = "Styling",
        source = """
flowchart LR
  A --> B --> C --> D
  linkStyle 0 stroke:#dc2626,stroke-width:4px,color:#991b1b
  linkStyle 1 stroke:#2563eb,stroke-width:3px,color:#1e3a8a
  linkStyle 2 stroke:#16a34a,stroke-width:2px,color:#14532d
        """.trimIndent(),
        officialDrawable = R.drawable.official_link_styles,
        officialAspectRatio = 302f / 64f,
    ),
    FlowchartDemo(
        id = "class_shorthand",
        title = "Class shorthand",
        category = "Styling",
        source = """
flowchart LR
  A:::blue & B:::green --> C:::result
  classDef blue fill:#dbeafe,stroke:#2563eb,color:#172554
  classDef green fill:#dcfce7,stroke:#16a34a,color:#14532d
  classDef result fill:#fef3c7,stroke:#d97706,color:#78350f
        """.trimIndent(),
        officialDrawable = R.drawable.official_class_shorthand,
        officialAspectRatio = 140f / 152f,
    ),
    FlowchartDemo(
        id = "default_class",
        title = "Default class",
        category = "Styling",
        source = """
flowchart LR
  A --> B --> C
  classDef default fill:#f1f5f9,stroke:#475569,color:#0f172a,stroke-width:2px
        """.trimIndent(),
        officialDrawable = R.drawable.official_default_class,
        officialAspectRatio = 221f / 64f,
    ),
    FlowchartDemo(
        id = "comments_entities",
        title = "Comments and entities",
        category = "Text",
        source = """
flowchart LR
  %% This entire line is ignored
  A["A double quote: #quot;"] --> B["Unicode heart: #9829;"]
  B --> C["Parentheses (work) in quotes"]
        """.trimIndent(),
        officialDrawable = R.drawable.official_comments_entities,
        officialAspectRatio = 552f / 88f,
    ),
    FlowchartDemo(
        id = "subgraph_edges",
        title = "Links to subgraphs",
        category = "Subgraphs",
        source = """
flowchart LR
  Start --> one
  subgraph one [First group]
    A --> B
  end
  subgraph two [Second group]
    C --> D
  end
  one --> two
  two --> Finish
        """.trimIndent(),
        officialDrawable = R.drawable.official_subgraph_edges,
        officialAspectRatio = 551f / 115f,
    ),
    FlowchartDemo(
        id = "subgraph_directions",
        title = "Subgraph directions",
        category = "Subgraphs",
        source = """
flowchart LR
  subgraph TOP
    direction TB
    subgraph B1
      direction RL
      i1 --> f1
    end
    subgraph B2
      direction BT
      i2 --> f2
    end
  end
  A --> TOP --> B
  B1 --> B2
        """.trimIndent(),
        officialDrawable = R.drawable.official_subgraph_directions,
        officialAspectRatio = 348f / 373f,
    ),
    FlowchartDemo(
        id = "collapsed_subgraph",
        title = "Collapsed subgraph",
        category = "Subgraphs",
        source = """
flowchart TD
  Start --> one
  subgraph one [My Group]
    A --> B --> C
  end
  one --> End
  one@{ view: collapsed }
        """.trimIndent(),
        officialDrawable = R.drawable.official_collapsed_subgraph,
        officialAspectRatio = 100f / 260f,
    ),
    FlowchartDemo(
        id = "parallel_edges",
        title = "Parallel edges",
        category = "Layouts",
        source = """
flowchart LR
  A -->|primary| B
  A -.->|retry| B
  A ==>|priority| B
        """.trimIndent(),
        officialDrawable = R.drawable.official_parallel_edges,
        officialAspectRatio = 254f / 130f,
    ),
    FlowchartDemo(
        id = "self_loop_and_cycle",
        title = "Self loop and cycle",
        category = "Layouts",
        source = """
flowchart LR
  A --> A
  A --> B --> C --> A
        """.trimIndent(),
        officialDrawable = R.drawable.official_self_loop_and_cycle,
        officialAspectRatio = 221f / 96f,
    ),
    FlowchartDemo(
        id = "crossing_routes",
        title = "Crossing routes and bridges",
        category = "Layouts",
        source = """
flowchart TB
  Start --> A & B & C
  A --> D
  B --> E
  C --> F
  A --> F
  C --> D
  D & E & F --> Finish
        """.trimIndent(),
        officialDrawable = R.drawable.official_crossing_routes,
        officialAspectRatio = 221f / 388f,
    ),
    FlowchartDemo(
        id = "dense_branch_merge",
        title = "Dense branch and merge",
        category = "Layouts",
        source = """
flowchart TB
  Start --> A & B & C
  A --> D & E
  B --> D & F
  C --> E & F
  D & E & F --> Finish
        """.trimIndent(),
        officialDrawable = R.drawable.official_dense_branch_merge,
        officialAspectRatio = 287f / 408f,
    ),
)
