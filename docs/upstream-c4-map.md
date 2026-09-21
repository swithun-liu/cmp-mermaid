# Upstream C4 Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production C4 path is pure Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `c4/C4Plugin.kt` | `c4Detector.ts` and `c4Diagram.ts` | Five C4 headers, parser lifecycle, layout dispatch, and typed failures |
| `c4/upstream/mermaid/C4Parser.kt` | `parser/c4Diagram.jison` | Lexer states, comments, metadata, macros, positional and named attributes, quoted commas, multiline statements, and boundary braces |
| `c4/upstream/mermaid/C4Db.kt` | `c4Db.ts` | Shape, boundary, relation, style, layout, dynamic-index, and nested-boundary state |
| `c4/upstream/mermaid/C4Types.kt` | `c4Types.ts` and `c4ShapeAdapter.ts` | Diagram, element, boundary, relation, and attribute models plus C4 defaults |
| `c4/C4Layout.kt -> drawInsideBoundary`, `drawShapeArray`, and `Bounds.insert` | `c4Renderer.ts -> drawInsideBoundary`, `drawShapeArray`, and `bounds` | Row placement, nested boundary sizing, margins, padding, and global extents |
| `c4/C4Layout.kt -> measureShape` and `drawShape` | `c4ShapeAdapter.ts`, `svgDraw.ts`, and `rendering-elements/shapes/c4LabelHelper.ts` | Person, rectangle, database, queue, label sections, wrapping, colors, and outlines |
| `c4/C4Layout.kt -> drawBoundary` and `addBoundaryText` | `svgDraw.ts -> drawBoundary` | Boundary rectangles, headings, type or technology text, and nested paint order |
| `c4/C4Layout.kt -> drawRelations` and `drawRelationText` | `c4Renderer.ts -> drawRelation` and `svgDraw.ts -> drawRelationship` | Straight relations, directional endpoint selection, bidirectional arrows, dynamic indexes, labels, technology text, colors, and offsets |
| `c4/C4Layout.kt -> buildScene` | `c4Renderer.ts -> draw` | Upstream global bounds, box dimensions, title margin, viewport, metadata, and SceneGraph ordering |
| `MermaidPreprocessor.kt` and `MermaidContract.kt` | `defaultConfig.ts`, `config.type.ts`, and `styles.js` | Complete typed C4 configuration, merge precedence, validation, element styles, and theme defaults |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/mermaid/src/diagrams/c4/c4Db.ts` | `0dc55e891eb14e08f93b0951a3592ed11bacf34c539141ee978213aacdb25004` |
| `packages/mermaid/src/diagrams/c4/c4Detector.ts` | `0572b81cc0bc3eb898049ac77a24a49a7d212669125d252324adbf3f12d5417b` |
| `packages/mermaid/src/diagrams/c4/c4Diagram.ts` | `c97bfd8527c3e292ff5befc006f768cae72aab8334e878af93be874d438f0997` |
| `packages/mermaid/src/diagrams/c4/c4Renderer.ts` | `1d09b55fa11a8c2c0987e02186495d8ed1d8cdbfeda02d4160f0010e3a653e52` |
| `packages/mermaid/src/diagrams/c4/c4ShapeAdapter.ts` | `e86b2557cd3294e2eee387e4146eb310cfd2277a4046173ae43adfcf297b0595` |
| `packages/mermaid/src/diagrams/c4/c4Types.ts` | `9aaaec849bef814787453958ec02d378ac2eae38ff3e23dd5826666835da38d6` |
| `packages/mermaid/src/diagrams/c4/parser/c4Diagram.jison` | `5a5054b08f05a02b805a8959b78d733bec2c8c74156bfd924be8d37c7df17582` |
| `packages/mermaid/src/diagrams/c4/styles.js` | `e0bcb2e3607e6f2007f734a5174f240299f997834de345ae94243ddbabcecd74` |
| `packages/mermaid/src/diagrams/c4/svgDraw.ts` | `59855ba27570433ca25d6b8a037541cc2553300c6b640d565b28fc6623b2bd2a` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/c4.md` | `8638e33d9c4070b708951ddaed6c3c03e36bf3f10380c87928c8efd630789704` |

## Intentional Kotlin Adaptations

- The Jison-generated JavaScript parser is represented by a portable Kotlin
  scanner and parser. It preserves macro actions and boundary-stack behavior
  while returning `GMResult.Err` for expected failures.
- DOM and SVG measurement are replaced by `TextMetricProvider`. SVG shapes,
  paths, labels, arrows, and paint order become ordered SceneGraph elements
  rendered by Compose Canvas.
- `accTitle:` follows the upstream Jison action and sets the visible diagram
  title. `accDescr` and `accDescription` populate accessibility description.
- Upstream `Deployment_Node(global, ...)` parses but later collides with the
  internal global boundary and crashes during rendering. The corpus avoids
  that reserved alias rather than presenting the upstream crash as supported
  behavior.
- Pure geometry retains upstream floating-point semantics; parser,
  configuration, resource, and layout failures remain typed.

## Parity Gate

- All 6 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused tests cover all headers, element and relation macros, named
  attributes, metadata, nested boundaries, styles, dynamic indexes,
  configuration, malformed input, endpoint validation, and determinism.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry passes `13/13`; detail reports
  `12 pass / 1 reviewed / 0 fail`. The reviewed case is an equivalent
  SceneGraph representation of SVG database and queue subpaths.
- Matrix geometry and detail pass `256/256`, with
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved shape, boundary, relation, label, clipping,
  overlap, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff every mapped detector, grammar, database, type, renderer, shape,
   style, configuration, and documentation file.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 C4 Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
