package io.github.cmpmermaid.core

internal data class OfficialFlowchartCase(
    val id: String,
    val source: String,
)

internal val officialFlowchartCases: List<OfficialFlowchartCase> = listOf(
    OfficialFlowchartCase(
        id = "basic_syntax",
        source = """
flowchart LR
  A[Hard edge] -->|Link text| B(Round edge)
  B --> C{Decision}
  C -->|One| D[Result one]
  C -->|Two| E[Result two]
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "question_workflow",
        source = """
flowchart TB
  A([Student asks]) --> B{Enough context?}
  B -- No --> C[Request clarification]
  C --> A
  B -- Yes --> D[[Solve and verify]]
  D --> E[(Answer cache)]
  E --> F((Delivered))
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "classic_shapes",
        source = """
flowchart LR
  A[Rectangle] --> B(Rounded)
  B --> C([Stadium])
  C --> D[[Subroutine]]
  D --> E[(Database)]
  E --> F{Decision}
  F --> G{{Hexagon}}
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "input_output_shapes",
        source = """
flowchart TB
  A[/Input/] --> B[\Output\]
  B --> C[/Priority\]
  C --> D[\Manual/]
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "edge_labels",
        source = """
flowchart LR
  A -- text --> B
  A -->|pipe label| C
  B -. dotted .-> D
  C == thick ==> D
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "edge_markers",
        source = """
flowchart LR
  A --- B
  B --> C
  C <--> D
  D o--o E
  E x--x F
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "renderer_pipeline",
        source = """
flowchart LR
  R[/Markdown/] --> P{Diagram type}
  P --> F[Flowchart parser]
  F ==> S[[SceneGraph]]
  S --> C([CMP Canvas])
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "subgraph",
        source = """
flowchart TB
  subgraph client [Native client]
    A[Mermaid source] --> B[Parser]
    B --> C[Layout]
  end
  C --> D[CMP Canvas]
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "nested_subgraphs",
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
    ),
    OfficialFlowchartCase(
        id = "styled_nodes",
        source = """
flowchart LR
  A[Parse] --> B[Layout] --> C[Render]
  classDef parse fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e,stroke-width:2px
  classDef render fill:#dcfce7,stroke:#15803d,color:#14532d,stroke-width:2px
  class A parse
  class C render
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "long_labels",
        source = """
flowchart TB
  A["A longer question that needs readable wrapping"] --> B{Can it be answered safely?}
  B -->|Yes| C["Return a concise and verified response"]
  B -->|No| D["Ask for the missing context"]
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "metadata_shapes",
        source = """
flowchart LR
  A@{ shape: stadium, label: "Start" } --> B@{ shape: hex, label: "Validate" }
  B --> C@{ shape: cyl, label: "Store" }
  C --> D@{ shape: dbl-circ, label: "Stop" }
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "multi_node_links",
        source = """
flowchart LR
  A:::source & B:::source --> C & D
  classDef source fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e
        """.trimIndent(),
    ),
    OfficialFlowchartCase(
        id = "edge_ids_and_length",
        source = """
flowchart LR
  A e1@----> B
  e1@{ animate: true }
  B ~~~ C
  C o--x D
  classDef animated stroke:#dc2626,stroke-width:2px
  class e1 animated
        """.trimIndent(),
    ),
)
