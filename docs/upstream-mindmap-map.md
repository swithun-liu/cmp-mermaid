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
| `mindmap/upstream/cose/CoseBilkentLayout.kt` | `cytoscape-cose-bilkent/src/index.js`, `cose-base` `CoSELayout`, and `layout-base` force and `IGeometry.getIntersection2` helpers | Mermaid's deterministic flat-tree radial initialization, rectangle clipping, and spring embedder path |
| `mindmap/upstream/dagre/MindmapDagreLayout.kt` | Mermaid unified Dagre adapter | Measured top-to-bottom graph placement, edge routing, and shape intersection |
| `mindmap/upstream/tidytree/MindmapTidyTreeLayout.kt` | `@mermaid-js/layout-tidy-tree/src/layout.ts` | Root splitting, left/right tree placement, bounds, and edge projection |
| `mindmap/upstream/tidytree/NonLayeredTidyTreeLayout.kt` | `non-layered-tidy-tree-layout/src/algorithm.js` and `src/helpers.js` | First walk, second walk, contour threading, spacing, and final bounds |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked text, HTML breaks, entities, sanitization, and unsupported detection |
| `flowchart/upstream/mermaid/MermaidShapePort.kt` | Mermaid `rendering-elements/shapes/*` and Mindmap shape helpers | Default, square, rounded, circle, cloud, bang, and hexagon geometry |
| `SceneGraph.kt` | Mermaid SVG output semantics | Platform-independent shapes, gradients, paths, text, metadata, and bounds |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid Mindmap SVG output | Native Canvas geometry, gradients, paths, and text painting |

`MindmapLayout.kt` maps `createText.ts -> addHtmlSpan` by using the upstream
CSS `line-height: 1.5` for both label measurement and SceneGraph text. Browser
and Compose glyph widths and line breaks can still differ, and those measured
size perturbations can select a different equivalent CoSE-Bilkent rotation or
mirror. The portable resolver maps browser Trebuchet MS to bundled
Arimo-compatible metrics; the refreshed corpus has both narrower and wider
Native labels (`0.888-1.180` across matched single-line labels), so no uniform
width multiplier can remove the residual without regressing other inputs.

For identical fixed node geometry, the JavaScript and Kotlin CoSE translations
match through the recorded radial initialization and iterations
`1, 2, 3, 4, 5, 10, 20` within `0.001`. A 19-node diagnostic tree then becomes
chaotic after a confirmed one-ULP `sin` difference between V8 and the JVM feeds
the iterative force simulation. Therefore the gate compares the shared early
trajectory and requires deterministic Kotlin replay, but does not claim final
coordinate identity for that wide diagnostic tree. The two smaller fixtures
continue to require final relative-coordinate identity.

## Explicit Translation Boundaries

Effective `layout` and `look` must match on both sides of a comparison.
`styles.ts -> genSections/getStyles` deliberately renders Redux Color's
classic root with `git0` fill and colored section edges; its Neo root uses
`mainBkg` with a section-colored border and `nodeBorder` edges. This behavior
does not depend on Dagre versus CoSE. The fresh same-config probes and
remaining geometry reviews are in the
[container-width report](container-width-report.md); no color override was
added to imitate an image rendered with a different `look`.

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
- `CoseBilkentLayoutTest` compares Kotlin output with generated
  `cytoscape-cose-bilkent 4.1.0` fixtures for identical node sizes and edges.
  Relative centers for a minimal tree and a branched tree are checked through
  the final iteration. A 19-node mixed tree checks radial initialization and
  iterations through 20, then requires deterministic Kotlin replay instead of
  cross-runtime final-coordinate identity. These checks do not involve font
  measurement, SVG, Canvas, or raster output. Regenerate the fixtures with
  `npm run generate:cose-bilkent-parity-fixtures` from
  `tools/official-reference`.
- `MindmapDagreLayoutTest` and `NonLayeredTidyTreeLayoutTest` cover their
  translated algorithm boundaries.
- `MindmapStressTest` renders 256 deterministic randomized Native diagrams.
- The current 256-case Native/Official matrix reports `256 pass / 0 review / 0 fail`.
  Geometry has 0 hard failures with width `1.023-1.113`, height `0.809-1.106`,
  and foreground ink `0.927-1.141`.
- The geometry gate keeps the shared numeric thresholds unchanged. A
  CoSE-Bilkent ratio outside those thresholds is queued for review only when
  the Native and Official detail manifests have no structural error. Blank
  output, missing text or shapes, hierarchy mismatches, and every non-CoSE
  ratio violation remain hard failures.

## Upgrade Procedure

1. Update the locked Mermaid and layout-library versions.
2. Diff the mapped grammar, database, renderer, shape, and layout sources.
3. Regenerate `MindmapJisonTables.kt` and translate changed semantic actions.
4. Regenerate the 13 documentation fixtures and review their hashes.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Mindmap Native/Official pairs.
7. Update this map, the compatibility matrix, and third-party notices.
