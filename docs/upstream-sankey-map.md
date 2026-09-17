# Upstream Sankey Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| d3-sankey | `0.12.3` |
| Native renderer | Compose Multiplatform Canvas |

The production Sankey path is Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `sankey/SankeyPlugin.kt` | `sankeyDetector.ts` and `sankeyDiagram.ts` | `sankey`/`sankey-beta` detection plus parser/layout orchestration |
| `sankey/upstream/mermaid/SankeyParser.kt` | `parser/sankey.jison` | Header, exactly three CSV fields, escaped fields, records, and link population |
| `sankey/upstream/mermaid/SankeyParser.kt` | `sankeyUtils.ts` `prepareTextForParsing` | Per-line whitespace and repeated-newline normalization |
| `sankey/upstream/mermaid/SankeyDb.kt` | `sankeyDB.ts` | First-seen node identity and order, links, graph projection, title, and reset state |
| `sankey/upstream/mermaid/SankeyTypes.kt` | `sankeyDB.ts` data objects | Nodes, links, and graph input values |

## D3 Layout Translation

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `sankey/upstream/d3/SankeyLayout.kt` | `d3-sankey/src/sankey.js` `computeNodeLinks`, `computeNodeValues`, `computeNodeDepths`, and `computeNodeHeights` | Graph resolution, node values, cycle detection, depths, and heights |
| `sankey/upstream/d3/SankeyLayout.kt` | `d3-sankey/src/sankey.js` `computeNodeLayers`, `initializeNodeBreadths`, and `computeNodeBreadths` | Layers, scale, initial breadths, padding, and six relaxation iterations |
| `sankey/upstream/d3/SankeyLayout.kt` | `d3-sankey/src/sankey.js` relaxation, collision, reordering, and top helpers | Weighted movement, collision resolution, stable link order, and link breadths |
| `sankey/upstream/d3/SankeyLayout.kt` | `d3-sankey/src/align.js` | Left, right, center, and justify node alignment |

## Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `sankey/SankeyLayout.kt` | `sankeyRenderer.ts` `draw` | Configuration, D3 invocation, node rectangles, Tableau 10 colors, labels, links, and paint order |
| `sankey/SankeyLayout.kt` | `sankeyRenderer.ts` `findCentralNodeLayer`, `getNodeColor`, `getText`, and `getLabelPosition` | Central layer, custom colors, value formatting, legacy labels, and outlined labels |
| `sankey/SankeyLayout.kt` | `d3-sankey/src/sankeyLinkHorizontal.js` | Horizontal cubic link control points |
| `sankey/SankeyLayout.kt` | `styles.js` | Label font, four-pixel outline, 0.5 link opacity, and multiply blending |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/config flow, `defaultConfig.ts`, and `config.schema.yaml` | Sankey defaults, typed overrides, color maps, merge precedence, and validation |
| `MermaidContract.kt` | Mermaid Sankey configuration | Typed Sankey options |
| `SceneGraph.kt` | Mermaid Sankey SVG elements | Platform-independent rectangles, cubic paths, gradients, blend mode, labels, metadata, bounds, and paint order |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas gradients, blend mode, paths, outlined text, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `parser/sankey.jison` | `1f5b7bf55f5cf1c9149543678aa827f5ab5340998dd33fbdeaaf73921b49cff0` |
| `sankeyDB.ts` | `24916eb04e0a20f7aef6e73d544dd0f718c391a76506289c323d53c1a6472333` |
| `sankeyDetector.ts` | `5482554e87d73fb6ac1fe5ce930b9c88b74900501262ff96c59eccc97b0de816` |
| `sankeyDiagram.ts` | `9a936516f14221487cdc8127c0d54343ecc44d28811f9dddb936bf908630099a` |
| `sankeyRenderer.ts` | `7c0a3cf95a2222ddb80fddcd920fa90f1ddee13e7ad16126720ddbe3e1ccf6e7` |
| `sankeyUtils.ts` | `5edd06293c179d03898f9bf0ae244e12fcbdc796bb3b015550aaa65770cc73a2` |
| `styles.js` | `6f4bde8057d914e1bc6026b88b4b82930e5d053c0bfa6cb4b468093d8b8090e4` |
| `defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `config.schema.yaml` | `5b59ca5612d5ebfbf1ef9277601224a734c831e16f3d1654ae74481dea4b26b0` |
| `packages/mermaid/src/docs/syntax/sankey.md` | `a5db0eddff8cafef874d4ada853c5d842c89c8ba05a969568f433a80ee1d653a` |
| `d3-sankey/src/align.js` | `519ae8524bdc6e4379929ed8cb99e49581400b6600a95158e669b97600e4cb8f` |
| `d3-sankey/src/sankey.js` | `14eeab99922cac50331b84f2efe34becba8e1305183ae79f48a08943a03b7f83` |
| `d3-sankey/src/sankeyLinkHorizontal.js` | `4a5f8d61ffa2c8a9734e4308c11e79fb20a4cfb126c433229a1321ac6820c4af` |
| `d3-sankey/LICENSE` | `2bf785e778d67a4f5266cffcd4f2cc5bb98cde73791666e7efeb8002ba32dfa5` |

## Intentional Kotlin Adaptations

- The Jison grammar is expressed as a deterministic Kotlin CSV scanner because
  the production path cannot execute generated JavaScript.
- JavaScript `parseFloat` token behavior is preserved at the database boundary.
  Invalid, non-finite, and negative values are converted to typed
  `GMResult.Err` layout failures instead of allowing D3 to throw or emit
  non-finite geometry.
- The generic d3-sankey API is narrowed to the fixed path Mermaid invokes:
  first-seen node IDs, default stable ordering, six iterations, configured
  width/padding/alignment, and the full configured extent.
- D3's ordinal Tableau 10 assignment is represented by first-seen node index,
  which is equivalent to Mermaid's first call to the ordinal scale for each
  node ID.
- SVG whitespace collapses the value label's newline to a visual space. The
  SceneGraph stores that visual text directly so text measurement and painting
  use the same content.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, alignment, baseline, outline, and paint order for
  measurement and painting.
- The scene viewport is normalized to painted bounds because Compose has no
  SVG `getBBox()`. The calculation includes the four-pixel text alignment
  adjustment used by the Canvas painter and audit manifest.

## Parity Gate

- `SankeyParserTest` covers both headers, first-seen order, quoted commas,
  escaped quotes, quoted newlines, JavaScript value parsing, source locations,
  and structured errors.
- `D3SankeyLayoutTest` locks representative d3-sankey `0.12.3` coordinates and
  verifies structured cycle and invalid-value failures.
- `SankeyLayoutTest` covers geometry, paint order, link gradients and blend
  mode, all major configuration classes, legacy label placement, frontmatter
  title propagation, responsive sizing, and structured errors.
- `SankeyStressTest` renders 256 deterministic randomized DAGs and checks
  finite geometry and deterministic replay.
- `OfficialSankeyDocumentationTest` executes all eight active examples
  extracted from Mermaid's Sankey documentation.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
  Both corpora report `0 review / 0 fail`, all 269 geometry pairs pass, and all
  16 matrix contact sheets were manually reviewed.

## Upgrade Procedure

1. Update the locked Mermaid and d3-sankey versions, source commit, hashes, and
   third-party notice.
2. Diff the mapped grammar, preprocessing, database, renderer, styles,
   detector, diagram registration, D3 layout, alignment, and link path files.
3. Translate only changed parser, state, geometry, configuration, or style
   behavior in the mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Sankey Native/Official pairs.
7. Update this map, the compatibility matrix, and the public stability report.
