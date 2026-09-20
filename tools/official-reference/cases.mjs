export const cases = [
  {
    id: 'basic_syntax',
    title: 'Basic syntax',
    category: 'Basics',
    source: `flowchart LR
  A[Hard edge] -->|Link text| B(Round edge)
  B --> C{Decision}
  C -->|One| D[Result one]
  C -->|Two| E[Result two]`,
  },
  {
    id: 'question_workflow',
    title: 'Question workflow',
    category: 'Basics',
    source: `flowchart TB
  A([Student asks]) --> B{Enough context?}
  B -- No --> C[Request clarification]
  C --> A
  B -- Yes --> D[[Solve and verify]]
  D --> E[(Answer cache)]
  E --> F((Delivered))`,
  },
  {
    id: 'classic_shapes',
    title: 'Classic node shapes',
    category: 'Nodes',
    source: `flowchart LR
  A[Rectangle] --> B(Rounded)
  B --> C([Stadium])
  C --> D[[Subroutine]]
  D --> E[(Database)]
  E --> F{Decision}
  F --> G{{Hexagon}}`,
  },
  {
    id: 'input_output_shapes',
    title: 'Input and output shapes',
    category: 'Nodes',
    source: `flowchart TB
  A[/Input/] --> B[\\Output\\]
  B --> C[/Priority\\]
  C --> D[\\Manual/]`,
  },
  {
    id: 'edge_labels',
    title: 'Edge labels',
    category: 'Links',
    source: `flowchart LR
  A -- text --> B
  A -->|pipe label| C
  B -. dotted .-> D
  C == thick ==> D`,
  },
  {
    id: 'edge_markers',
    title: 'Edge markers',
    category: 'Links',
    source: `flowchart LR
  A --- B
  B --> C
  C <--> D
  D o--o E
  E x--x F`,
  },
  {
    id: 'renderer_pipeline',
    title: 'Renderer pipeline',
    category: 'Layouts',
    source: `flowchart LR
  R[/Markdown/] --> P{Diagram type}
  P --> F[Flowchart parser]
  F ==> S[[SceneGraph]]
  S --> C([CMP Canvas])`,
  },
  {
    id: 'subgraph',
    title: 'Subgraph',
    category: 'Subgraphs',
    source: `flowchart TB
  subgraph client [Native client]
    A[Mermaid source] --> B[Parser]
    B --> C[Layout]
  end
  C --> D[CMP Canvas]`,
  },
  {
    id: 'nested_subgraphs',
    title: 'Nested subgraphs',
    category: 'Subgraphs',
    source: `flowchart LR
  subgraph outer [Rendering engine]
    subgraph syntax [Syntax]
      A[Lexer] --> B[AST]
    end
    subgraph visual [Visual]
      C[Layout] --> D[Scene]
    end
    B --> C
  end`,
  },
  {
    id: 'styled_nodes',
    title: 'Styled nodes',
    category: 'Styling',
    source: `flowchart LR
  A[Parse] --> B[Layout] --> C[Render]
  classDef parse fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e,stroke-width:2px
  classDef render fill:#dcfce7,stroke:#15803d,color:#14532d,stroke-width:2px
  class A parse
  class C render`,
  },
  {
    id: 'long_labels',
    title: 'Long labels',
    category: 'Text',
    source: `flowchart TB
  A["A longer question that needs readable wrapping"] --> B{Can it be answered safely?}
  B -->|Yes| C["Return a concise and verified response"]
  B -->|No| D["Ask for the missing context"]`,
  },
  {
    id: 'metadata_shapes',
    title: 'Metadata shape syntax',
    category: 'Nodes',
    source: `flowchart LR
  A@{ shape: stadium, label: "Start" } --> B@{ shape: hex, label: "Validate" }
  B --> C@{ shape: cyl, label: "Store" }
  C --> D@{ shape: dbl-circ, label: "Stop" }`,
  },
  {
    id: 'multi_node_links',
    title: 'Multi-node links',
    category: 'Links',
    source: `flowchart LR
  A:::source & B:::source --> C & D
  classDef source fill:#e0f2fe,stroke:#0369a1,color:#0c4a6e`,
  },
  {
    id: 'edge_ids_and_length',
    title: 'Edge ids and length',
    category: 'Links',
    source: `flowchart LR
  A e1@----> B
  e1@{ animate: true }
  B ~~~ C
  C o--x D
  classDef animated stroke:#dc2626,stroke-width:2px
  class e1 animated`,
  },
  {
    id: 'direction_bottom_to_top',
    title: 'Bottom-to-top direction',
    category: 'Directions',
    source: `flowchart BT
  Start --> Validate --> Finish`,
  },
  {
    id: 'direction_right_to_left',
    title: 'Right-to-left direction',
    category: 'Directions',
    source: `flowchart RL
  Start --> Validate --> Finish`,
  },
  {
    id: 'unicode_markdown',
    title: 'Unicode and Markdown labels',
    category: 'Text',
    source: `---
config:
  htmlLabels: false
---
flowchart LR
  A["This ❤ Unicode"] --> B["\`This **is** _Markdown_\`"]
  B --> C["Line one<br/>Line two"]`,
  },
  {
    id: 'cjk_labels',
    title: 'Chinese labels',
    category: 'Text',
    source: `flowchart LR
  A["用户提交问题"] --> B{"信息完整吗？"}
  B -- "否" --> C["请求补充信息"]
  C --> A
  B -- "是" --> D["生成并验证答案"]`,
  },
  {
    id: 'expanded_process_shapes',
    title: 'Expanded process shapes',
    category: 'Expanded shapes',
    source: `flowchart LR
  A@{ shape: rect, label: "Process" } --> B@{ shape: rounded, label: "Event" }
  B --> C@{ shape: stadium, label: "Terminal" }
  C --> D@{ shape: subproc, label: "Subprocess" }
  D --> E@{ shape: cyl, label: "Database" }
  E --> F@{ shape: circle, label: "Start" }`,
  },
  {
    id: 'expanded_control_shapes',
    title: 'Expanded control shapes',
    category: 'Expanded shapes',
    source: `flowchart TB
  A@{ shape: odd, label: "Odd" } --> B@{ shape: diamond, label: "Decision" }
  B --> C@{ shape: hex, label: "Prepare" }
  C --> D@{ shape: trap-b, label: "Priority" }
  D --> E@{ shape: trap-t, label: "Manual" }`,
  },
  {
    id: 'expanded_storage_shapes',
    title: 'Expanded storage shapes',
    category: 'Expanded shapes',
    source: `flowchart LR
  A@{ shape: lin-rect, label: "Lined process" } --> B@{ shape: lin-cyl, label: "Disk" }
  B --> C@{ shape: h-cyl, label: "Direct access" }
  C --> D@{ shape: bow-rect, label: "Stored data" }`,
  },
  {
    id: 'expanded_document_shapes',
    title: 'Expanded document shapes',
    category: 'Expanded shapes',
    source: `flowchart LR
  A@{ shape: doc, label: "Document" } --> B@{ shape: docs, label: "Documents" }
  B --> C@{ shape: lin-doc, label: "Lined document" }
  C --> D@{ shape: tag-doc, label: "Tagged document" }
  D --> E@{ shape: tag-rect, label: "Tagged process" }`,
  },
  {
    id: 'expanded_special_shapes',
    title: 'Expanded special shapes',
    category: 'Expanded shapes',
    source: `flowchart LR
  A@{ shape: sm-circ, label: "Start" } --> B@{ shape: framed-circle, label: "Stop" }
  B --> C@{ shape: fork, label: "Fork" }
  C --> D@{ shape: hourglass, label: "Collate" }
  D --> E@{ shape: braces, label: "Comment" }
  E --> F@{ shape: bolt, label: "Com link" }`,
  },
  {
    id: 'expanded_manual_shapes',
    title: 'Expanded manual shapes',
    category: 'Expanded shapes',
    source: `flowchart TB
  A@{ shape: curv-trap, label: "Display" } --> B@{ shape: div-rect, label: "Divided" }
  B --> C@{ shape: tri, label: "Extract" }
  C --> D@{ shape: win-pane, label: "Internal" }
  D --> E@{ shape: f-circ, label: "Junction" }
  E --> F@{ shape: notch-pent, label: "Loop limit" }
  F --> G@{ shape: flip-tri, label: "Manual file" }
  G --> H@{ shape: sl-rect, label: "Manual input" }
  H --> I@{ shape: procs, label: "Processes" }
  I --> J@{ shape: flag, label: "Paper tape" }
  J --> K@{ shape: cross-circ, label: "Summary" }`,
  },
  {
    id: 'all_link_types',
    title: 'All link types',
    category: 'Links',
    source: `flowchart LR
  A --> B
  A --- C
  A -.-> D
  A -.- E
  A ==> F
  A === G
  A ~~~ H`,
  },
  {
    id: 'marker_matrix',
    title: 'Arrow marker matrix',
    category: 'Links',
    source: `flowchart LR
  A --o B
  C --x D
  E --> F
  G o--o H
  I x--x J
  K <--> L`,
  },
  {
    id: 'marker_styles',
    title: 'Markers on thick and dotted links',
    category: 'Links',
    source: `flowchart LR
  A o==o B
  C x==x D
  E <==> F
  G o-.-o H
  I x-.-x J
  K <-.-> L`,
  },
  {
    id: 'minimum_link_lengths',
    title: 'Minimum link lengths',
    category: 'Links',
    source: `flowchart TD
  A --> B
  A ---> C
  A ----> D
  B -.-> E
  C -..-> E
  D ====> E`,
  },
  {
    id: 'mixed_link_labels',
    title: 'Link label syntaxes',
    category: 'Links',
    source: `flowchart LR
  A -- text --> B
  A -->|pipe label| C
  A -. dotted .-> D
  A == thick ==> E
  A -- open text --- F`,
  },
  {
    id: 'node_color_formats',
    title: 'Node color formats',
    category: 'Styling',
    source: `flowchart LR
  A[Hex] --> B[Short hex] --> C[Named]
  style A fill:#dbeafe,stroke:#1d4ed8,color:#172554,stroke-width:3px
  style B fill:#fce,stroke:#c06,color:#401
  style C fill:lightgreen,stroke:darkgreen,color:black`,
  },
  {
    id: 'link_styles',
    title: 'Link colors and widths',
    category: 'Styling',
    source: `flowchart LR
  A --> B --> C --> D
  linkStyle 0 stroke:#dc2626,stroke-width:4px,color:#991b1b
  linkStyle 1 stroke:#2563eb,stroke-width:3px,color:#1e3a8a
  linkStyle 2 stroke:#16a34a,stroke-width:2px,color:#14532d`,
  },
  {
    id: 'class_shorthand',
    title: 'Class shorthand',
    category: 'Styling',
    source: `flowchart LR
  A:::blue & B:::green --> C:::result
  classDef blue fill:#dbeafe,stroke:#2563eb,color:#172554
  classDef green fill:#dcfce7,stroke:#16a34a,color:#14532d
  classDef result fill:#fef3c7,stroke:#d97706,color:#78350f`,
  },
  {
    id: 'default_class',
    title: 'Default class',
    category: 'Styling',
    source: `flowchart LR
  A --> B --> C
  classDef default fill:#f1f5f9,stroke:#475569,color:#0f172a,stroke-width:2px`,
  },
  {
    id: 'comments_entities',
    title: 'Comments and entities',
    category: 'Text',
    source: `flowchart LR
  %% This entire line is ignored
  A["A double quote: #quot;"] --> B["Unicode heart: #9829;"]
  B --> C["Parentheses (work) in quotes"]`,
  },
  {
    id: 'subgraph_edges',
    title: 'Links to subgraphs',
    category: 'Subgraphs',
    source: `flowchart LR
  Start --> one
  subgraph one [First group]
    A --> B
  end
  subgraph two [Second group]
    C --> D
  end
  one --> two
  two --> Finish`,
  },
  {
    id: 'subgraph_directions',
    title: 'Subgraph directions',
    category: 'Subgraphs',
    source: `flowchart LR
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
  B1 --> B2`,
  },
  {
    id: 'collapsed_subgraph',
    title: 'Collapsed subgraph',
    category: 'Subgraphs',
    source: `flowchart TD
  Start --> one
  subgraph one [My Group]
    A --> B --> C
  end
  one --> End
  one@{ view: collapsed }`,
  },
  {
    id: 'parallel_edges',
    title: 'Parallel edges',
    category: 'Layouts',
    source: `flowchart LR
  A -->|primary| B
  A -.->|retry| B
  A ==>|priority| B`,
  },
  {
    id: 'self_loop_and_cycle',
    title: 'Self loop and cycle',
    category: 'Layouts',
    source: `flowchart LR
  A --> A
  A --> B --> C --> A`,
  },
  {
    id: 'crossing_routes',
    title: 'Crossing routes and bridges',
    category: 'Layouts',
    source: `flowchart TB
  Start --> A & B & C
  A --> D
  B --> E
  C --> F
  A --> F
  C --> D
  D & E & F --> Finish`,
  },
  {
    id: 'dense_branch_merge',
    title: 'Dense branch and merge',
    category: 'Layouts',
    source: `flowchart TB
  Start --> A & B & C
  A --> D & E
  B --> D & F
  C --> E & F
  D & E & F --> Finish`,
  },
  {
    id: 'icon_shape',
    title: 'Icon shape fallback',
    category: 'Assets',
    source: `flowchart LR
  A@{ icon: "fa:user", form: "square", label: "User Icon", pos: "t", h: 60 }
  A --> B[Continue]`,
  },
  {
    id: 'image_shape',
    title: 'Embedded bitmap image',
    category: 'Assets',
    source: `flowchart LR
  A@{ img: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAAA8CAYAAACQPx/OAAAABmJLR0QA/wD/AP+gvaeTAAAEb0lEQVR4nO3cX0xbVRwH8O/vVErLKAiyZWw6QGMcNgsuLpqwRIGxRBenyXT4J1EfRvRpL4uvjLvEV6MxmkxdE3U+4J9EkpqZzLGNICEucW4EurqHURgRZjtWCrTdZbfHB4R0C7Cu99x7z+j5PBHKPecbvmnvvaenJdzB3dG7zcV4O+d8F4BaAOvu/BvFlDkAEQJOGQzHdK15KPtBWvrp4Ilib4XnYw68DxCzO2WBMgg4mmLRQ9DadGCxkIMnij0V3l8BNDuZrmBxOp12/fsitDadAYCn0vsJVBnOId7i5es/AgByd/RuY2RcUC9TjjMynDcwF+PtqgwpuIhwgHGOVqeTKAsItJsBfIvTQZQlNQxAqdMplCU+de6QjCpEMqoQyahCJKMKkYwqRDKqEMk8YPUERMBT1T7s3VqFnTXl2FjqxuayYgDAeOImrs3q+D0SRzB8HRcmZqyOIz3yHD7DLRmYgH3+DehsqcXjD5XkdMzlWBJazwi6L0XBLUklP0sKebTSi29eq8eOzWV5HX9uPIF3fwohciMtOJn8hJ9DGreU42z79rzLAIBnHi5D/3tPo6muQmCy+4PQZ8jzdQ8i+HYDily07OPezrPL/j51pGnZ3+tGBi99O4i+SFxQQvkJe4bUVnjw3X7/imXkw+1i6Hrdj8cqvcLGlJ2QQoiA4/ufRNW6IhHD3aaypAiBffUgcT1LTUghr/o3mDpn3M2zj5Thlfr1lo0vE9OFEAGHW2oFRFmdtqvO8jlkYLqQ7dW+nO8zzHiiqgQN1Wv/vTTThezdWiUih3RzOcV0IY015SJy5GSnjXM5xXQhm3zFInJIN5dTTC8ubvS5Aax805eLXI5NHWnCprK1X4jpZ4idi4CZAlhxNF3I5OxNETlyMjGj2zaXU0wX8k/Cvn/SxIx95TvFdCH9o/Yt/PVFpm2byymmCwmGr4vIkZNfwjHb5nKK6ausi5MzuBxLrriEnu1el9+zhaNJDE7O3lu4+5CQqyytZ0REllV19lyxfA4ZCFnt7b4UxbnxhIihljUwNo1gAbxcAYIK4Rx458cQYnPzIoa7zVRyHu0/hwtm04OwdwxH42m89cMwdCMjakjoRgZtXUO4MpUSNqbshG5y6IvE8cLXFxGdM39vMpWcx8vHB9E/uvYvdbMJ33UyMDaN5746jz+u5n9OGRibRuMXf6J3pHA2NyyyZCtp5EYazYHzePP7YfwdS+Z8XDiaxBtdw2gJ/IXReOHtyQIs3ErKOdAdiqI7FEVDdemqby59eCaCYDhWEPcZd2PZVlIlP2r3u2RUIZJRhUhGFSIZVYhkVCGSUYVIhgFQH+yTBiUYQFedjqEs4mOMwH9zOoaygBOdZAbDMQCG02EUGJxuBZiuNQ8RcNTpNIWOiD7XtdYQA4AUix4C0ONwpsLFcSo1UfoBsHjZq7XpaRbdQ4TPoF6+7GQQ0afpa749+HLHPJD9zdb/c3ec9hPhAIF2Y+Grxtf+x5bsNQsgwolOcroV0LXWUPaD/wHzh0LfoJOJBgAAAABJRU5ErkJggg==", label: "Bitmap", pos: "t", h: 60, constraint: "on" }
  A --> B[Rendered natively]`,
  },
  {
    id: 'embedded_image_shape',
    title: 'Embedded SVG image',
    category: 'Assets',
    source: `flowchart LR
  A@{ img: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgNjAiPjxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iNjAiIHJ4PSI4IiBmaWxsPSIjZmYzNjcwIi8+PHBhdGggZD0iTTI1IDE1YzE4IDAgMjUgMTAgMjUgMjAgMC0xMCA3LTIwIDI1LTIwLTEwIDgtMTUgMTctMTUgMzBINDBjMC0xMy01LTIyLTE1LTMwWiIgZmlsbD0id2hpdGUiLz48L3N2Zz4=", label: "Embedded", pos: "t", h: 60, constraint: "on" }
  A --> B[No network required]`,
  },
  {
    id: 'html_label_styles',
    title: 'HTML label styles',
    category: 'Text',
    source: `flowchart LR
  A["<b>Bold</b> and <i>italic</i><br/><u>underlined</u>"]
  A --> B["<code>monospace</code> H<sub>2</sub>O x<sup>2</sup>"]`,
  },
  {
    id: 'dense_crossing_routes_dagre',
    title: 'Dagre crossing routes',
    category: 'Layouts',
    source: `---
config:
  layout: dagre
---
flowchart TB
  A --> D
  B --> C
  A --> C
  B --> D`,
  },
  {
    id: 'dense_fanout_fanin',
    title: 'Dense fan-out and fan-in',
    category: 'Stability',
    source: `flowchart TB
  Start([Start]) --> A & B & C & D
  A --> E & F
  B --> E & G
  C --> F & H
  D --> G & H
  E & F & G & H --> Finish([Finish])`,
  },
  {
    id: 'nested_deployment_feedback',
    title: 'Nested deployment feedback',
    category: 'Stability',
    source: `flowchart LR
  subgraph client [Client]
    UI[Compose UI] --> Core[Shared core]
  end
  subgraph cloud [Cloud]
    subgraph api [API tier]
      Gateway --> Service
    end
    subgraph data [Data tier]
      Cache --> Database[(Database)]
    end
    Service --> Cache
    Cache -. miss .-> Database
  end
  Core --> Gateway
  Service -->|response| Core
  Database -. invalidation .-> Cache`,
  },
  {
    id: 'parallel_protocol_edges',
    title: 'Parallel protocol edges',
    category: 'Stability',
    source: `flowchart LR
  Client -->|request| Server
  Client -.->|retry| Server
  Client ==>|priority| Server
  Client o--o Server
  Server -->|response| Client`,
  },
  {
    id: 'cross_subgraph_feedback',
    title: 'Cross-subgraph feedback',
    category: 'Stability',
    source: `flowchart TB
  subgraph ingest [Ingestion]
    direction BT
    Decode@{ shape: hex, label: "Decode" }
    Validate@{ shape: doc, label: "Validate" }
    Decode --> Validate
  end
  Normalize@{ shape: notch-rect, label: "Normalize" }
  Store@{ shape: cyl, label: "Store" }
  Publish@{ shape: stadium, label: "Publish" }
  Validate ==> Normalize --> Store --> Publish
  Decode -. bypass .-> Normalize
  Store -. retry .-> Validate`,
  },
  {
    id: 'deep_validation_pipeline',
    title: 'Deep validation pipeline',
    category: 'Stability',
    source: `flowchart LR
  Input[/Input/] --> Parse[Parse]
  Parse --> Schema{Schema valid?}
  Schema -- No --> Reject[Reject]
  Schema -- Yes --> Auth{Authorized?}
  Auth -- No --> Audit[Audit denial] --> Reject
  Auth -- Yes --> Enrich[Enrich]
  Enrich --> Rules{Rules pass?}
  Rules -- No --> Review[[Manual review]]
  Review --> Rules
  Rules -- Yes --> Persist[(Persist)]
  Persist --> Notify[Notify] --> Done((Done))`,
  },
  {
    id: 'many_to_many_routes',
    title: 'Many-to-many routes',
    category: 'Stability',
    source: `flowchart TB
  A & B & C --> X & Y & Z
  X --> Result
  Y --> Result
  Z --> Result`,
  },
  {
    id: 'styled_decision_tree',
    title: 'Styled decision tree',
    category: 'Stability',
    source: `flowchart TB
  Root{Risk level} -->|Low| Fast[Fast path]
  Root -->|Medium| Review[Review]
  Root -->|High| Block[Block]
  Review -->|Pass| Fast
  Review -->|Fail| Block
  Fast --> Done([Done])
  classDef decision fill:#fef3c7,stroke:#d97706,color:#78350f,stroke-width:3px
  classDef success fill:#dcfce7,stroke:#15803d,color:#14532d
  classDef danger fill:#fee2e2,stroke:#dc2626,color:#7f1d1d
  class Root decision
  class Fast,Done success
  class Block danger
  linkStyle 0,3 stroke:#16a34a,stroke-width:3px
  linkStyle 2,4 stroke:#dc2626,stroke-width:3px`,
  },
  {
    id: 'long_cjk_workflow',
    title: 'Long Chinese workflow',
    category: 'Stability',
    source: `flowchart TB
  A["用户提交包含上下文的复杂问题"] --> B{"输入信息是否足够完整？"}
  B -- "否" --> C["生成澄清问题并等待用户补充信息"]
  C --> A
  B -- "是" --> D["检索相关资料并生成候选答案"]
  D --> E{"答案是否通过事实与安全校验？"}
  E -- "否" --> D
  E -- "是" --> F["返回简洁、准确、可执行的最终答案"]`,
  },
  {
    id: 'mixed_shape_operations',
    title: 'Mixed operational shapes',
    category: 'Stability',
    source: `flowchart LR
  A@{ shape: rounded, label: "Receive" } --> B@{ shape: doc, label: "Document" }
  B --> C@{ shape: diamond, label: "Inspect" }
  C --> D@{ shape: subproc, label: "Transform" }
  D --> E@{ shape: cyl, label: "Store" }
  E --> F@{ shape: lin-cyl, label: "Archive" }
  F --> G@{ shape: docs, label: "Replicate" }
  G --> H@{ shape: hourglass, label: "Collate" }
  H --> I@{ shape: stadium, label: "Complete" }`,
  },
  {
    id: 'cycle_with_shortcuts',
    title: 'Cycles, shortcuts, and self-loops',
    category: 'Stability',
    source: `flowchart LR
  A --> B --> C --> D --> E
  E --> A
  B --> D
  C --> C
  E -. retry .-> C
  A == priority ==> D`,
  },
  {
    id: 'dense_minimum_lengths',
    title: 'Dense minimum lengths',
    category: 'Stability',
    source: `flowchart TD
  Start --> A
  Start ---> B
  Start ----> C
  A -..-> D
  B ===> D
  C ====> D
  A --> E
  B ---> E
  C ----> E
  D & E --> Finish`,
  },
  {
    id: 'nested_direction_matrix',
    title: 'Nested direction matrix',
    category: 'Stability',
    source: `flowchart LR
  subgraph outer [Outer LR]
    direction LR
    subgraph top [Top TB]
      direction TB
      A --> B --> C
    end
    subgraph bottom [Bottom RL]
      direction RL
      D --> E --> F
    end
    C --> D
  end
  Start --> A
  F --> Finish`,
  },
  {
    id: 'disconnected_components',
    title: 'Disconnected components',
    category: 'Stability',
    source: `flowchart TB
  A1 --> A2 --> A3
  B1{Choice} --> B2
  B1 --> B3
  C1((Start)) --> C2[(Store)]
  D1@{ shape: doc, label: "Document" }`,
  },
  {
    id: 'rich_text_matrix',
    title: 'Rich text matrix',
    category: 'Stability',
    source: `flowchart TB
  A["<b>Bold</b> and <i>italic</i>"] --> B["<u>Underlined</u><br/>second line"]
  B --> C["H<sub>2</sub>O and x<sup>2</sup>"]
  C --> D["\`Markdown **bold** and _italic_\`"]
  D --> E["Symbols: &lt; &gt; &amp; #9829;"]`,
  },
  {
    id: 'animated_edge_matrix',
    title: 'Animated edge matrix',
    category: 'Stability',
    source: `flowchart LR
  A fast@--> B
  B slow@-.-> C
  C thick@==> D
  fast@{ animate: true, animation: fast }
  slow@{ animate: true, animation: slow }
  thick@{ animate: true }
  classDef pulse stroke:#dc2626,stroke-width:3px
  class fast,slow,thick pulse`,
  },
  {
    id: 'collapsed_group_feedback',
    title: 'Collapsed group feedback',
    category: 'Stability',
    source: `flowchart LR
  Start --> pipeline
  subgraph pipeline [Processing pipeline]
    Parse --> Validate --> Transform --> Persist
  end
  pipeline --> Done
  Done -. retry .-> pipeline
  pipeline@{ view: collapsed }`,
  },
];
