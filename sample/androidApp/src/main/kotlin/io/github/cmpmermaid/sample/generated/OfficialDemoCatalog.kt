package io.github.cmpmermaid.sample.generated

import io.github.cmpmermaid.sample.R

internal data class FlowchartDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
    val officialDrawable: Int,
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
    ),
)
