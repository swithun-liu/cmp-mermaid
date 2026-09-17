# Upstream Flowchart Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Jison runtime | `0.4.18` |
| dagre-d3-es | `7.0.14` |
| d3-shape | `3.2.0` |
| Marked | `16.4.2` |
| Native renderer | Compose Multiplatform Canvas |

The runtime does not embed Mermaid.js or a WebView. Flowchart behavior around
parsing, data storage, layout preparation, text, shapes, edges, markers, and
result mapping is Kotlin. The production modules contain no JavaScript engine
or bundled JavaScript algorithm.

## Preprocessing And Semantics

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `MermaidPreprocessor.kt` | Mermaid `preprocess.ts`, directive/config cleanup, frontmatter, entity helpers, and `resolveAppearance` inputs | Frontmatter, directives, comments, config precedence, secure host keys, theme/look/layout options |
| `upstream/mermaid/FlowJisonTables.kt` | Mermaid `src/diagrams/flowchart/parser/flow.jison` generated parser | Generated lexer rules, conditions, productions, and LALR states |
| `upstream/mermaid/FlowJisonRuntime.kt` | Jison `0.4.18` generated lexer/parser runtime | Kotlin parser stack, lexer states, locations, error recovery, and semantic dispatch |
| `upstream/mermaid/FlowJisonParser.kt` | Mermaid `flow.jison` semantic actions | Typed Kotlin actions against FlowDB |
| `upstream/mermaid/FlowDb.kt` | Mermaid `diagrams/flowchart/flowDb.ts` | Vertex, edge, class, subgraph, tooltip, link, callback, and renderer-data semantics |
| `FlowchartDataAdapter.kt` | Mermaid FlowDB renderer data and style compilation | Native typed document and CSS projection |

The generator `tools/official-reference/generate-flow-parser.mjs` reads the
locked Mermaid distribution, verifies the upstream SHA-256, and regenerates
the parser tables. Hand-maintained semantic actions stay close to the grammar
production boundaries so an upstream diff can be translated incrementally.

## Text And HTML

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `upstream/marked/MarkedLexer.kt` | Marked `16.4.2` `Lexer.ts` and `Tokenizer.ts` | Token fields and production behavior consumed by Mermaid labels |
| `upstream/marked/MarkedGeneratedRules.kt` | Marked `16.4.2` compiled regex rules | Generated and SHA-256 pinned |
| `upstream/mermaid/MermaidTextPort.kt` | Mermaid `createText.ts`, `handle-markdown-text.ts`, and sanitization flow | Markdown projection, HTML spans, relative font styles, line breaks, and explicit unsupported detection |
| `upstream/mermaid/MermaidHtmlFragmentTokenizer.kt` | Browser HTML fragment tokenization behavior used by Mermaid labels | Typed fragment tokens without a DOM |
| `upstream/mermaid/Html5NamedEntities.kt` | WHATWG named character references | Generated 2,231-entry entity table |
| `upstream/mermaid/MermaidUrlSanitizer.kt` | Mermaid URL sanitization behavior | Native URL scheme filtering |

Marked extensions and parser/renderer fields that Mermaid does not configure
or consume are intentionally outside this translation. DOM-only content that
cannot map to text spans returns `UnsupportedFeature`.

## Dagre And Graphlib

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `upstream/graphlib/Graph.kt` | `src/graphlib/graph.js` | Graph API used by Dagre |
| `upstream/graphlib/Algorithms.kt` | DFS, preorder, postorder, and components | Traversal API used by Dagre |
| `upstream/dagre/DagreLayout.kt` | `src/dagre/layout.js` | Complete layout stage order |
| `upstream/dagre/DagreUtil.kt` | `src/dagre/util.js` | Production helpers |
| `upstream/dagre/Acyclic.kt` | `src/dagre/acyclic.js` | Ported |
| `upstream/dagre/GreedyFas.kt` | `src/dagre/greedy-fas.js`, `src/dagre/data/list.js` | Ported |
| `upstream/dagre/Rank.kt` | `src/dagre/rank/*.js` | Ported |
| `upstream/dagre/Normalize.kt` | `src/dagre/normalize.js` | Ported |
| `upstream/dagre/NestingGraph.kt` | `src/dagre/nesting-graph.js` | Ported |
| `upstream/dagre/Compound.kt` | `parent-dummy-chains.js`, `add-border-segments.js` | Ported |
| `upstream/dagre/Order.kt` | `src/dagre/order/*.js` | Ported |
| `upstream/dagre/Position.kt` | `src/dagre/position/index.js`, `bk.js` | Ported |
| `upstream/dagre/CoordinateSystem.kt` | `src/dagre/coordinate-system.js` | Ported |
| `upstream/mermaid/MermaidGraphAdapter.kt` | Mermaid Dagre `mermaid-graphlib.js` | Recursive clusters and endpoint anchors |
| `FlowDagreLayout.kt` | Mermaid Dagre adapter `index.js` | Measurement, graph preparation, self-loop merge, and result mapping |

Unused Graphlib algorithms, JSON serialization, debug modules, and the
upstream DOM/SVG painter are not part of the native runtime.

## ELK

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `FlowElkLayout.kt` | Mermaid `rendering-util/layout-algorithms/elk/index.js` and ELK adapter helpers | Retained source mapping and option projection; execution stops at the unsupported boundary |
| `upstream/elk/ElkJsRuntime.kt` | Mermaid's `elkjs` invocation boundary | Returns `MermaidError.UnsupportedFeature("ELK layout")` without executing JavaScript |
| `upstream/mermaid/MermaidLineJumpPort.kt` | Mermaid `rendering-elements/lineJump.ts` | Arc/gap crossing detection and path commands |

ELK names and `flowchart-elk` remain recognizable so valid upstream input
fails explicitly instead of falling back to Dagre. `elkjs` may still appear as
a transitive dependency of the debug-only Mermaid.js reference renderer; it is
not a production runtime dependency.

## Shapes, Edges, And Compose

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `upstream/mermaid/MermaidShapePort.kt` | Mermaid `rendering-elements/shapes/*` | Sizing, geometry, label offsets, intersections, assets |
| `upstream/mermaid/MermaidEdgePathPort.kt` | Mermaid `rendering-elements/edges.js`, `lineWithOffset.ts` | Curves, endpoint correction, and marker offsets |
| `upstream/mermaid/MermaidMarkerPort.kt` | Mermaid marker definitions | Triangle, circle, cross, and open endpoints |
| `upstream/mermaid/D3CurvePort.kt` | d3-shape `3.2.0` `src/curve/*` | Line-only curves used by Mermaid |
| `FlowchartLayout.kt` | Mermaid Flowchart drawing orchestration | Platform-independent `SceneGraph` |
| `MermaidDiagram.kt` | Mermaid SVG output behavior | Compose Canvas paths, multi-contour trim, text, shadows, assets, and hit testing |
| `AndroidMermaidAssetProvider.kt` | Browser image/SVG loading role | Android `data:` loading, BitmapFactory, and AndroidSVG adapter; external sources require a host provider |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields; they are not
  replaced with numeric defaults where upstream distinguishes `undefined`.
- JavaScript object insertion order maps to linked Kotlin collections.
- Expected parsing, configuration, resource, layout, and asset failures cross
  owned boundaries as `GMResult.Err`.
- Lodash operations map to Kotlin collections while preserving traversal
  order.
- Browser text measurement is supplied by `TextMetricProvider`; Compose uses
  the same font and line-height inputs for measurement and painting.
- SVG paths, markers, assets, spans, shadows, and interactions map to typed
  SceneGraph primitives.
- Bang and Cloud use the extrema of their sampled rendered paths for
  rectangular edge intersection. This mirrors each upstream shape's
  `updateNodeBounds` followed by `intersect.rect`, rather than intersecting
  against the smaller nominal label envelope.

## Parity Gate

- `DagreParityTest` compares Kotlin coordinates with frozen JavaScript fixtures
  for chains, branches, labels, minimum lengths, parallel edges, cycles, and
  compound bounds.
- `OfficialFlowchartDocumentationTest` executes all 114 Mermaid Flowchart
  documentation examples under Dagre.
- Focused ELK boundary tests cover `flowchart-elk`, `elk`, and every accepted
  `elk.*` algorithm name.
- The Android gallery compares 45 identical sources against official Mermaid
  `12.0.0` Dagre PNGs on the same device viewport.
- Focused tests cover every translated parser, text, shape, edge, marker,
  line-hop, interaction, asset, and resource-limit boundary.
- The replacement Web audit passes 14/14 production scenarios and 256/256
  matrix pairs. Its manifests record actual marker endpoint anchors and compare
  their occlusion by later opaque nodes; all 16 matrix contact sheets were
  manually reviewed.

## Upgrade Procedure

1. Update the version constants and locked tool dependencies.
2. Diff each mapped upstream file between Mermaid versions.
3. Translate changed production methods within the same Kotlin boundary.
4. Regenerate parser tables, Marked rules/fixtures, and HTML entities; review
   version and SHA-256 changes.
5. Regenerate the 45 reference images and 114 documentation fixtures.
6. Run JVM tests, Android assembly, and every configured iOS compile target.
7. Install the Android sample and capture the complete Native/Official gallery.
8. Update this map, the compatibility matrix, and third-party notices before
   publishing.
