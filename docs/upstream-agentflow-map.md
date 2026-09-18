# Upstream Agentflow Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Agentflow path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `agentflow/AgentflowPlugin.kt` | `afDetector.ts`, `diagram.ts`, and `renderer.ts` | `agentflow-beta` detection, parser/database lifecycle, unified-layout projection, title, padding, and viewport orchestration |
| `agentflow/upstream/mermaid/AgentflowJisonTables.kt` | `parser/agentflow.jison` and generated `agentflowParser.ts` | Generated LALR productions, parse states, lexer conditions, terminals, and regular-expression sources |
| `agentflow/upstream/mermaid/AgentflowJisonRuntime.kt` | Jison `0.4.18` runtime plus `parser/agentflow.jison` lexer actions | Portable longest-match lexer, state stack, locations, token actions, and structured lexical failures |
| `agentflow/upstream/mermaid/AgentflowJisonParser.kt` | `parser/agentflow.jison` semantic actions and `agentflowParser.ts` | Nodes, edges, labels, flows, globals, connectors, metadata, styles/classes, accessibility, and source mappings |
| `agentflow/upstream/mermaid/AgentflowDb.kt` | `agentflowDb.ts`, `transformData.ts`, and `diagnostics.ts` | Graph state, containment, metadata, connector references, semantic model, diagnostics, collapsed-flow projection, and unified layout data |
| `agentflow/upstream/mermaid/AgentflowShapes.kt` | `shapes.ts` and `colorSlots.ts` | Domain aliases, allowed/removed shapes, fallback diagnostics, semantic kinds, and node/container palette slots |
| `agentflow/upstream/mermaid/AgentflowTypes.kt` | `types.ts` and `diagnostics.ts` | Parser, database, source-position, semantic-model, and diagnostic contracts |
| `flowchart/FlowchartDataAdapter.kt`, `FlowchartLayout.kt`, and `FlowDagreLayout.kt` | `renderer.ts` and `rendering-util/render.ts` | Agentflow's unified layout data is rendered through the existing translated Dagre graph pipeline |
| `flowchart/upstream/mermaid/MermaidShapePort.kt` | `collapsedGroup.ts`, `clusters.js`, and `styles.ts` | Expanded flow frames, collapsed summaries, indicator separator, container stroke, and ordered SceneGraph paint |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and `config.type.ts` | Agentflow defaults, frontmatter/init overrides, merge precedence, and typed configuration validation |
| `MermaidContract.kt` | Mermaid Agentflow configuration and theme types | Public typed options and `flowContainerStroke` theme value |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `agentflow/parser/agentflow.jison` | `8bb23d09c1dcdf4a3d3139c4ba29b8a823d726b755c69067010a74cb621fd2a6` |
| `agentflow/parser/agentflowParser.ts` | `f3da18456e72197b108285941632596dfae757c8a447b726dbdaa76c2dae78dd` |
| `agentflow/agentflowDb.ts` | `dac0e049a8add766e3430a770863dd92054c4b7907555f117c66ef247aaff279` |
| `agentflow/transformData.ts` | `af9d67b5d513218a31350fa4cd209fa57bcb1fc5cc854c9993da3fd0529ac51b` |
| `agentflow/renderer.ts` | `53ad5e8814575dd7a0cc2abc8c3000f400c931ada2d7752b44e86a54701f029f` |
| `agentflow/styles.ts` | `0846f6971df6662cb8b941aeed91edfef11c4ef9dbe62582f3b5434d5b086ce8` |
| `agentflow/shapes.ts` | `b9cf5934f896725da470d5493cc54c8e3750d195a9b890ba7a560677d90bbc54` |
| `agentflow/colorSlots.ts` | `722bd3dd0dd726cc954690bce67675a921dd8e1f0f0977a9713d444308c01ff4` |
| `agentflow/diagnostics.ts` | `6d0fcbb7a836006a741c0d9782c95850196012a1e8876883faa32c7c5c8ddb9f` |
| `agentflow/types.ts` | `e22cbc8a67de5b5a6d422b5ce0db0a69d0e92498e7b9e7fd2cf8d44b7fa82f08` |
| `agentflow/afDetector.ts` | `dd45296cfdb92ab010abe91bd48c58dfcca1129e1e407c463d5a37fe80ffabaf` |
| `agentflow/diagram.ts` | `ef0afae561345fa69412e6181635992e45e7e2c939dca7b6d1279e0717774926` |
| `agentflow/conformance/runner.ts` | `54713f2710a6b22b857343d29db54c85a175b466112cf81b9093b7f73877ee23` |
| `common/colorThemeGate.ts` | `01d2f95921caf0b8cd76427092789323c3dbaec7f2fb56fa20ca1bbc13bfd5e3` |
| `rendering-util/render.ts` | `9d900493575ce1dd9c834c6ea56d37a2e21b70818be8dcb2b175373330ee1cf2` |
| `rendering-util/rendering-elements/shapes/collapsedGroup.ts` | `1fbca9ec8405703ebe99746d3380e92c0791b2f0101e7a9bdbecd01ae3420206` |
| `rendering-util/rendering-elements/clusters.js` | `4a73517c6b85450b99cfe1bb6a6d0478055751d1e15c7feb6ed159fadd8fc733` |
| `defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `agentflow.md` | `02a247ee27d90edfb16c965141e2bc05777710320aeccee4f85441af1fa8b6d4` |

## Intentional Kotlin Adaptations

- The Jison grammar's generated tables are checked in as Kotlin data and
  executed by a portable Kotlin LALR/lexer runtime. Parse, validation, and
  resource-limit failures return `GMResult.Err`.
- Upstream metadata parsing is represented by the shared Kotlin YAML subset.
  Scalar types, multiline blocks, unknown nested keys, trailing commas, and
  prototype-key filtering retain the tested Mermaid behavior.
- Upstream projects Agentflow into its unified renderer. Kotlin mirrors that
  boundary by adapting `AgentflowDb.getData()` into the translated Flowchart
  Dagre pipeline rather than duplicating graph layout and shape rendering.
- Mermaid `12.0.0` selects ELK by default for Agentflow. Production modules
  cannot use Mermaid's JavaScript ELK package, so Kotlin defaults to Dagre and
  returns `MermaidError.UnsupportedFeature` for an effective `layout: elk`.
  Native/Official visual evidence explicitly selects Dagre on both sides; it
  does not claim that Dagre is equivalent to ELK.
- Mermaid's rough.js `handDrawn` path is not approximated. An effective
  hand-drawn look returns `MermaidError.UnsupportedFeature`.
- Expanded and collapsed flow containers reuse the translated unified
  renderer. The Agentflow-only collapsed indicator uses
  `flowContainerStroke`; ordinary Flowchart and State rendering retain their
  prior theme behavior.
- SVG DOM nodes, paths, markers, groups, and text become ordered SceneGraph
  elements. Browser text measurement is replaced by `TextMetricProvider`.

## Parity Gate

- All 12 official documentation fixtures and all eight upstream conformance
  fixtures are regenerated from the pinned source and tested on JVM.
- Parser/database/layout tests cover source mappings, semantic models,
  diagnostics, metadata, shape normalization, containment cycles, globals,
  nested/collapsed flows, connector references, edge semantics, color slots,
  and structured failures.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`,
  with Dagre selected explicitly for both renderers.
- Production geometry passes 13/13. Its detail result is
  `9 pass / 4 review / 0 fail`; the four structural comparator findings were
  manually accepted with no visible semantic, clipping, overlap, or paint-order
  defect.
- Matrix geometry passes 256/256 and matrix detail is
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved node-kind, container, connector, edge, marker,
  metadata-driven appearance, label, clipping, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the Jison tables, documentation fixtures, and conformance
   fixtures from the new source tree.
3. Diff every mapped parser, database, transformation, diagnostic, shape,
   color-slot, renderer, style, detector, registration, configuration, and
   documentation file.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Agentflow Native/Official Dagre pairs.
7. Update this map, the capability matrix, and the public stability report.
