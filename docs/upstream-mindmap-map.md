# Upstream Mindmap Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Marked | `16.4.2` |
| dagre-d3-es | `7.0.14` |
| cytoscape-cose-bilkent | `4.1.0` |
| cose-base | `1.0.3` |
| layout-base | `1.0.2` |
| @mermaid-js/layout-tidy-tree | `1.0.0` |
| non-layered-tidy-tree-layout | `2.0.2` |
| Native renderer | Compose Multiplatform Canvas |

The production Mindmap path is Kotlin in `commonMain`. It does not execute
Mermaid.js, require a WebView, embed a JavaScript engine, or access the
network.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `mindmap/MindmapPlugin.kt` | `mindmapDetector.ts` and `mindmapDiagram.ts` | Diagram detection and parser/layout orchestration |
| `mindmap/upstream/mermaid/MindmapJisonTables.kt` | `src/diagrams/mindmap/parser/mindmap.jison` generated parser | Symbols, productions, LALR states, lexer rules, and conditions |
| `mindmap/upstream/mermaid/MindmapJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, errors, and portable regular-expression matching |
| `mindmap/upstream/mermaid/MindmapJisonParser.kt` | `mindmap.jison` semantic actions | Node, indentation, icon, and class actions translated by production index |
| `mindmap/upstream/mermaid/MindmapDb.kt` | `mindmapDb.ts` | Parent selection, source order, shape decoding, section assignment, sanitization, and edge projection |
| `mindmap/upstream/mermaid/MindmapTypes.kt` | `mindmapTypes.ts` | Typed nodes, shapes, hierarchy, decorations, and renderer data |

`tools/official-reference/generate-mindmap-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`MindmapJisonTables.kt`. Semantic actions remain indexed by their matching
Jison production.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `mindmap/MindmapLayout.kt` | `mindmapRenderer.ts` | Node measurement, layout selection, section colors, curves, title, viewport sizing, and SceneGraph orchestration |
| `mindmap/upstream/cose/CoseBilkentLayout.kt` | `cytoscape-cose-bilkent/src/index.js`, `cose-base` `CoSELayout`, and `layout-base` force helpers | Mermaid's deterministic flat-tree radial initialization and spring embedder path |
| `mindmap/upstream/dagre/MindmapDagreLayout.kt` | Mermaid unified Dagre adapter | Measured top-to-bottom graph placement, edge routing, and shape intersection |
| `mindmap/upstream/tidytree/MindmapTidyTreeLayout.kt` | `@mermaid-js/layout-tidy-tree/src/layout.ts` | Root splitting, left/right tree placement, bounds, and edge projection |
| `mindmap/upstream/tidytree/NonLayeredTidyTreeLayout.kt` | `non-layered-tidy-tree-layout/src/algorithm.js` and `src/helpers.js` | First walk, second walk, contour threading, spacing, and final bounds |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked text, HTML breaks, entities, sanitization, and unsupported detection |
| `flowchart/upstream/mermaid/MermaidShapePort.kt` | Mermaid `rendering-elements/shapes/*` and Mindmap shape helpers | Default, square, rounded, circle, cloud, bang, and hexagon geometry |
| `SceneGraph.kt` | Mermaid SVG output semantics | Platform-independent shapes, gradients, paths, text, metadata, and bounds |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid Mindmap SVG output | Native Canvas geometry, gradients, paths, and text painting |

## Explicit Translation Boundaries

- `::icon(...)` is parsed and retained, then returns
  `UnsupportedFeature("Mindmap icon")` because no browser icon-pack provider
  exists in production.
- `:::className` is parsed and retained, then returns
  `UnsupportedFeature("Mindmap CSS class")`.
- ELK names are recognized, then return structured `UnsupportedFeature`
  without invoking `elkjs` or falling back to another layout.
- CoSE-Bilkent accepts Mermaid Mindmap's flat-tree contract. A non-forest graph
  returns structured `UnsupportedFeature` instead of selecting another
  algorithm.
- Expected parser, configuration, resource, text, and layout failures use
  `GMResult.Err`.

## Locked Upstream Hash

| Upstream file | SHA-256 |
| --- | --- |
| `mindmap.jison` | `1114fbccc641f2ad59a56eec452e21dfb947d78283adf571a6ce24b09dce6993` |

## Parity Gate

- `OfficialMindmapDocumentationTest` covers all 13 official documentation
  examples and the three explicit unsupported cases.
- `MindmapLayoutTest` covers hierarchy, all shapes, text, themes,
  configuration, metadata, unsupported features, and each layout selector.
- `CoseBilkentLayoutTest`, `MindmapDagreLayoutTest`, and
  `NonLayeredTidyTreeLayoutTest` cover the translated algorithm boundaries.
- `MindmapStressTest` renders 256 deterministic randomized Native diagrams.
- The replacement Web audit compares 13 independent production scenarios and
  256 unique same-source matrix cases against Mermaid.js `12.0.0`. Production
  results are `9 pass / 4 manually reviewed / 0 fail`; matrix results are
  `149 pass / 107 manually reviewed / 0 fail`, with 256/256 geometry passes.
  The reviews are CoSE-Bilkent branch rotations or mirrors under platform
  text-size perturbations; expected text, nodes, hierarchy edges, shapes,
  colors, clipping, overlap, and paint order all pass manual review across 16
  contact sheets.

## Upgrade Procedure

1. Update the locked Mermaid and layout-library versions.
2. Diff the mapped grammar, database, renderer, shape, and layout sources.
3. Regenerate `MindmapJisonTables.kt` and translate changed semantic actions.
4. Regenerate the 13 documentation fixtures and review their hashes.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Mindmap Native/Official pairs.
7. Update this map, the compatibility matrix, and third-party notices.
