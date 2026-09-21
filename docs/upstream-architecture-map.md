# Upstream Architecture Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Upstream force-layout dependency | `cytoscape-fcose` `2.2.0` |
| Native renderer | Compose Multiplatform Canvas |

The production Architecture path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, Cytoscape, fCoSE JavaScript, use a WebView, or depend on a
JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `architecture/ArchitecturePlugin.kt` | `architectureDetector.ts` and `architectureDiagram.ts` | Header detection, parser lifecycle, layout dispatch, and structured failure propagation |
| `architecture/upstream/mermaid/ArchitectureParser.kt` | `architecture.langium`, `arch.langium`, `tokenBuilder.ts`, `valueConverter.ts`, and `architectureParser.ts -> populateDb` | Tokens, declarations, icons, titles, parents, directional edge syntax, arrow modifiers, alignment directives, source positions, and parser errors |
| `architecture/upstream/mermaid/ArchitectureDb.kt` | `architectureDb.ts -> ArchitectureDB` | Declaration ordering, duplicate IDs, parent and edge validation, node/group lookup, adjacency maps, spatial maps, and group alignments |
| `architecture/upstream/mermaid/ArchitectureTypes.kt` | `architectureTypes.ts` | Services, junctions, groups, edges, directions, arrow polygons, spatial positions, and alignment types |
| `architecture/ArchitectureFcoseLayout.kt -> initializeNodes`, `buildConstraints`, and `integrateForces` | `architectureRenderer.ts -> layoutArchitecture`, `getAlignments`, and `getRelativeConstraints`; `cytoscape-fcose` `2.2.0` | Seeded initial placement, compound ownership, edge forces, alignment constraints, relative placement, iteration count, and deterministic integration |
| `architecture/ArchitectureFcoseLayout.kt -> resolveNodeOverlaps` and `resolveCompoundGroups` | fCoSE overlap handling plus `architectureRenderer.ts` compound-node setup | Constraint-preserving collision removal, group padding, nested compound bounds, and disconnected-component packing |
| `architecture/ArchitectureFcoseLayout.kt -> Mulberry32` | `architectureSeed.ts -> withSeededRandom` | Deterministic seeded random sequence |
| `architecture/ArchitectureLayout.kt` | `architectureRenderer.ts -> draw` and `svgDraw.ts` | Services, groups, junction gaps, edge routes, labels, explicit arrow polygons, viewport normalization, and SceneGraph paint order |
| `architecture/ArchitectureLayout.kt -> drawIcon` | `architectureIcons.ts` and `svgDraw.ts -> drawServices`, `drawGroups` | Exact built-in icon geometry, icon-text tiles, external icon assets, group icons, and service labels |
| `MermaidPreprocessor.kt` and `MermaidContract.kt` | `architectureDb.ts`, `defaultConfig.ts`, and `config.type.ts` | Typed defaults, frontmatter/init overrides, merge precedence, theme variables, and validation |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/parser/src/language/architecture/arch.langium` | `69755c428b2409c874786f5e40c69f85f543496e8929b54f0af30fd97a6c345d` |
| `packages/parser/src/language/architecture/architecture.langium` | `08b6acbfa6f48b3dc6b590e3a5339a3fe554c394a0307d6cbfebb75c175b5147` |
| `packages/parser/src/language/architecture/tokenBuilder.ts` | `055ce9bc374b3aa944e0d393b817f5a5def15f6255a4bbf248991eed7168aebc` |
| `packages/parser/src/language/architecture/valueConverter.ts` | `7f11ebdd878c758d4a6e0ee7eaa5c813159760292db9932db4be4790d3069392` |
| `packages/mermaid/src/diagrams/architecture/architectureDb.ts` | `28416d74513bd16aa7cd86a80450fab72a17f14a16ec6570b00af5af840c22f7` |
| `packages/mermaid/src/diagrams/architecture/architectureDetector.ts` | `6c61635c3f8b9e532083adf465c13ca31d430ee939c7821b8bc18c4ea8b2ba6c` |
| `packages/mermaid/src/diagrams/architecture/architectureDiagram.ts` | `81a47a4f0b96b9547f9297a5df934e16f17fabab6d446a5307223862f87e1300` |
| `packages/mermaid/src/diagrams/architecture/architectureIcons.ts` | `8c32188821db16344fd5ad751d7877c78c654999c0b454c4d554edcde7eb9968` |
| `packages/mermaid/src/diagrams/architecture/architectureParser.ts` | `ec72a0d5b9f8527a7283e3018e794f049ac80b32398b9a426794a0b54b75b700` |
| `packages/mermaid/src/diagrams/architecture/architectureRenderer.ts` | `155ba1c94377ca6ba29124b492fcdadb0ca5aee2fa9438b8a665dad6c7c5ab65` |
| `packages/mermaid/src/diagrams/architecture/architectureSeed.ts` | `1a7c74a50cfbfdc92ec49476c96030c18ec00a1d893b28bc7d3ab7dc95571da6` |
| `packages/mermaid/src/diagrams/architecture/architectureStyles.ts` | `62a345924376a9b2e9b9ff044afbccf44fc2bb4b3ebbab20512221159509393e` |
| `packages/mermaid/src/diagrams/architecture/architectureTypes.ts` | `b18c2bbb5b196e636821fdb692e87b33bebcad3a46b6b4956d01dad363f0654c` |
| `packages/mermaid/src/diagrams/architecture/svgDraw.ts` | `31b828618aa76242c4f45349176797989971820884c5f66965f43fa031f95950` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/architecture.md` | `c3d8b2ff1df5854814a17f523f39effa8b8dbf2894189f47f34efd79bbfb616d` |

## Intentional Kotlin Adaptations

- The Langium-generated TypeScript parser is represented by a portable Kotlin
  scanner and parser. It preserves the grammar's token boundaries, AST
  population order, source locations, and validation behavior while returning
  `GMResult.Err` for expected failures.
- Mermaid delegates force integration to Cytoscape and
  `cytoscape-fcose` `2.2.0`. Kotlin keeps Mermaid's node sizes, compound
  ownership, seeded initialization, alignment and relative-placement
  constraints, force parameters, overlap handling, and iteration budget in a
  platform-neutral solver. No generic Dagre or ELK substitution is used.
- DOM and SVG measurement are replaced by `TextMetricProvider`. SVG groups,
  paths, polygons, labels, icons, and paint order become ordered SceneGraph
  elements rendered by Compose Canvas.
- The six Mermaid-provided Architecture icons are translated to matching
  SceneGraph paths and shapes. Non-built-in icon names remain external
  `SceneAsset` references resolved by the Compose layer.
- Pure geometry retains upstream floating-point semantics; parser,
  configuration, database, resource, and layout failures remain typed.

## Parity Gate

- All 6 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused tests cover grammar and database state, declaration ordering,
  validation, disconnected components, groups, junctions, directional edges,
  arrows, alignment, icons, metadata, configuration, and deterministic seeds.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry and detail pass `13/13`.
- Matrix geometry and detail pass `256/256`, with
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved service, icon, group, junction, edge, arrow,
  label, alignment, clipping, overlap, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff every mapped grammar, detector, parser, database, type, seed, icon,
   renderer, style, configuration, and documentation file.
4. Check the pinned Cytoscape and fCoSE versions and translate changed layout
   constraints or solver behavior.
5. Translate changed behavior in the corresponding Kotlin method.
6. Run focused JVM tests and all platform build gates.
7. Capture and review all 256 Architecture Native/Official pairs.
8. Update this map, the capability matrix, and the public stability report.
