# Upstream Block Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Block path is pure Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `block/BlockPlugin.kt` | `blockDetector.ts`, `blockDiagram.ts`, and `blockRenderer.ts` | Header detection, parser lifecycle, layout dispatch, and structured failure boundaries |
| `block/upstream/mermaid/BlockJisonParser.kt`, `BlockJisonRuntime.kt`, and `BlockJisonTables.kt` | `parser/block.jison` and Jison `0.4.18` runtime tables | Lexer states, LALR actions, reductions, source positions, and parser errors |
| `block/upstream/mermaid/BlockDb.kt` | `blockDB.ts` and `blockUtils.ts` | Hierarchy construction, columns, duplicate declarations, edges, classes, styles, colors, metadata, and validation |
| `block/upstream/mermaid/BlockTypes.kt` | `blockTypes.ts` | Blocks, links, shapes, parser values, layout state, and positioned render sizes |
| `block/BlockLayout.kt` | `layout.ts`, `renderHelpers.ts`, and `blockRenderer.ts` | Two-pass measurement, cell allocation, nested positioning, shape insertion, edges, labels, and viewport sizing |
| `block/BlockLayout.kt` | shared rendering-element shape handlers | Positioned block-arrow, circle, double-circle, stadium, decision, cylinder, polygon, and composite sizing semantics |
| `MermaidPreprocessor.kt` | `utils.ts -> encodeEntities`, `defaultConfig.ts`, and `config.type.ts` | Entity preprocessing plus Block `padding` and `useMaxWidth` configuration |
| `MermaidContract.kt` | Mermaid Block configuration types | Public typed Block options |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/mermaid/src/diagrams/block/blockDB.ts` | `debd112b4c0382ac1b5f6a73015d2a1aa8e8d13af297d470de3427f75fe82e68` |
| `packages/mermaid/src/diagrams/block/blockDetector.ts` | `e3ea60c2be729749673daeb02a63faf1c714d819fbfd06f5a03a1d13c16fadb0` |
| `packages/mermaid/src/diagrams/block/blockDiagram.ts` | `08ca3aaa0e9ec8775ed4a59c3ce8be0c86dbcee555f0f8a0a327fe7cc415bf09` |
| `packages/mermaid/src/diagrams/block/blockRenderer.ts` | `0887c0fd2c900fd85403f0bcb483c4ab29de299c15b55ff54740d49043f1c7e0` |
| `packages/mermaid/src/diagrams/block/blockTypes.ts` | `7dc2473f2beb6f9ab392d1cd9c5bfd45ab04ad215e583cf291dcfff24327014e` |
| `packages/mermaid/src/diagrams/block/blockUtils.ts` | `5edd06293c179d03898f9bf0ae244e12fcbdc796bb3b015550aaa65770cc73a2` |
| `packages/mermaid/src/diagrams/block/layout.ts` | `1591740fd874cabc118a4fa87b38461d06db853f74b5de02d7bb7ce6e2d61b8c` |
| `packages/mermaid/src/diagrams/block/renderHelpers.ts` | `50ce641d6eb07580e7728d823e1b44e2a8d941a64cca536ff935fe432de6f47b` |
| `packages/mermaid/src/diagrams/block/styles.ts` | `c71ea579e003553b6d0217232d802ab47edbf613532d027fd80d0cfec904e74a` |
| `packages/mermaid/src/diagrams/block/parser/block.jison` | `323acca843736b66334fa088b97519ef96c22aa5a1a1eed4f468379ebecf09f2` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/blockArrow.ts` | `1e2e91d9d4a5a6f9680e5ad5942c989d405051d1a2fb387e355ee3827a4ff3c7` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/circle.ts` | `63409912fd7fbe8a17e05315f7d8fa05829d3bc987d77dd08f254fc8907f1b17` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/doubleCircle.ts` | `b55e170ccb2f8462398859921a0b09e73caf8ebcd5ef13a4419d8495896bb19b` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/cylinder.ts` | `0dfc8d226de322efd7de0c22bd6fa3ade1e1bad98ee534813185c9e95ca759f4` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/question.ts` | `54d769048857df58072c5d3216da8ad2a61e8a1ed3cbd594f9491bd9e6ff4bf8` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/stadium.ts` | `d107c7137961c2ed54f4cc84fe0bdf16c4d281226dff6ecdede112a893ecc021` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/squareRect.ts` | `f865b17f90ea0303245ec08cd1d967239031b1a297d2c32a79842df94f56421d` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/leanRight.ts` | `ac665b4276a67904e8aa6f59148e85fe0a0dfef87abe24e0ab271e6190acf1af` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/leanLeft.ts` | `7a70703a32ee7900eab683f0e280e086cc75d6b9710e12be8b1ddad092f1b79a` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/trapezoid.ts` | `b2b0a1bd828a1c2b9a35572d4949a21d3e9a8a76a3c959d6f15b882430b3d1e7` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/invertedTrapezoid.ts` | `c05edf7746571f35c89d41add0f9c6655f34b8104615e551cca1ece3df521bee` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/hexagon.ts` | `e1a1d19d261d59892a483eb4cbcde09d91b42cb7c999d34edbfb8b086fa844a0` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/subroutine.ts` | `70253bd1627a652f301159855ccd2397933d2db2943aac1dc2aff011c8ccb9e3` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/rectLeftInvArrow.ts` | `5a7c4121cab806e73135746c60a97e40f1e95d460cd0a65e5f6f8aa6ebf45c31` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/composite.ts` | `7798490929bd25d6e88a76f71280c7e5680519b96b3e47d9ede3299fde452892` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/block.md` | `3779f5db8f6da14abfa77bb7389854c3f6c9fe472c5ade0635dee9028449f630` |

## Intentional Kotlin Adaptations

- The generated Jison tables are translated into immutable Kotlin data and a
  portable lexer/LALR runtime. Lexing, parsing, configuration, and layout
  failures return `GMResult.Err`.
- Mermaid's `encodeEntities` preprocessing is implemented as a deterministic
  scan. This preserves its replacement order without relying on
  platform-specific regular-expression replacement ranges.
- DOM measurement is replaced by `TextMetricProvider`. Layout still preserves
  Mermaid's two stages: common grid-cell allocation followed by each shape
  handler's positioned sizing behavior.
- Positioned block arrows remain at natural size unless they explicitly span
  columns. Circle, decision, ellipse, and stadium nodes also remain natural;
  double-circle and cylinder nodes recalculate their final geometry from the
  allocated width exactly as their upstream handlers do.
- SVG groups, polygons, paths, markers, labels, and paint order become ordered
  SceneGraph elements rendered by Compose Canvas.

## Parity Gate

- All 30 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused parser/database/layout tests cover headers, columns, spans, spaces,
  nested composites, shapes, arrows, edges, duplicate declarations, classes,
  inline styles, metadata, configuration, and typed failures.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry and detail pass `13/13`.
- Matrix geometry passes `256/256`; detail is
  `253 pass / 3 review / 0 fail`.
- The three matrix reviews are paint-order occlusion ratio threshold notices.
  The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved shape, grid, edge, marker, color, text, clipping,
  overlap, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the Jison parser tables and documentation fixtures.
3. Diff every mapped detector, parser, database, layout, renderer, shape,
   style, configuration, and documentation file.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Block Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
