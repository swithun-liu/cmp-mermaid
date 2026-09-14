# Upstream XY Chart Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| d3-array | `3.2.4` |
| d3-scale | `4.0.2` |
| d3-shape | `3.2.0` |
| Native renderer | Compose Multiplatform Canvas |

The production XY Chart path is Kotlin in `commonMain`. It does not execute
Mermaid.js and does not require a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `xychart/upstream/mermaid/XyJisonTables.kt` | `src/diagrams/xychart/parser/xychart.jison` generated parser | Symbols, terminals, productions, LALR states, lexer rules, and conditions |
| `xychart/upstream/mermaid/XyJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, token positions, and errors |
| `xychart/upstream/mermaid/XyJisonParser.kt` | `xychart.jison` semantic actions | Orientation, axes, plots, labels, metadata, and parser-to-DB calls by production index |
| `xychart/upstream/mermaid/XyTypes.kt` | `chartBuilder/interfaces.ts` | Axis, point, plot, orientation, and compiled-chart data |
| `xychart/upstream/mermaid/XyDb.kt` | `xychartDb.ts` | Automatic domains, categorical truncation, palette indexing, series creation, sanitization, and metadata |
| `xychart/XyChartPlugin.kt` | `xychartDiagram.ts` and `xychartRenderer.ts` | Plugin detection, parser/database orchestration, and SceneGraph compilation |

`tools/official-reference/generate-xychart-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`XyJisonTables.kt`. Semantic actions remain indexed by the matching Jison
production so an upstream grammar diff can be translated incrementally.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `xychart/XyChartLayout.kt` | `chartBuilder/orchestrator.ts` | Component space calculation, vertical/horizontal arrangement, positions, and ranges |
| `xychart/XyChartLayout.kt` | `chartBuilder/components/chartTitle.ts` and `legend.ts` | Title and named-series legend measurement and SceneGraph output |
| `xychart/XyChartLayout.kt` | `chartBuilder/components/axis/*.ts` | Band/linear scales, tick generation, labels, titles, rotation, and bar outer padding |
| `xychart/XyChartLayout.kt` | `chartBuilder/components/plot/*.ts` | Bar geometry, straight line paths, point labels, and bar data labels |
| `xychart/upstream/d3/XyScalePort.kt` | d3-scale linear/band scales and d3-array ticks | Interpolation, point placement, descending/equal domains, and 1/2/5/10 tick selection |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/directive config flow and XY Chart schema | Chart/axis options, theme variables, palette parsing, validation, and host precedence |
| `SceneGraph.kt` | Mermaid XY SVG text transforms | Typed chart geometry plus text rotation and explicit pivots |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas shapes, paths, rotated text, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `xychart.jison` | `ce1e4904b3c2036ac29cc62b6e6561058b00e4bc9c9bbf661873451bb55bf0fc` |
| `xychartDb.ts` | `2025f06822a83157bdaca0ca9d8f6a9f44e6b885a42501c72c6cc68d09da08c9` |
| `xychartRenderer.ts` | `8feeacd2b05ee0de6fb962d1558f15d7ea7a5ecb16bfa118c598422344c86153` |
| `chartBuilder/index.ts` | `f4001db57b660426d5fe1fa67bd21cd559e15ebbfb2b855368e1c3ac70c1e0b2` |
| `chartBuilder/orchestrator.ts` | `d81eac5d425ab786d9953d0e7b802eff6883a1090110c43d6f5336701217f31f` |
| `chartBuilder/components/chartTitle.ts` | `9516c3a7d7e2cb69b296aba23966004ee0f7c4e07c398e6abf62b7aaee1fc158` |
| `chartBuilder/components/legend.ts` | `7f992085ef76e4e6443eaffafd2bc4388b5c3bf67d5d3cde9f5ce33d76d2cc88` |
| `chartBuilder/components/axis/baseAxis.ts` | `ae0e0138835dc9c26d0d310291ad9d3bb485f12e58e59343c90bfcb1ab5723d3` |
| `chartBuilder/components/axis/bandAxis.ts` | `e7796b5847e94e358d2771e54f33a5ae444dd216eb0dbaab9dd7f156c64c9ae9` |
| `chartBuilder/components/axis/linearAxis.ts` | `cf9ef534a30fab9460d010443f7dbe427e63967298300efefdb4427af9481aed` |
| `chartBuilder/components/plot/index.ts` | `9bd5faaf96dceb038ed5665bc63c7254e745a24e1ffaf93a6d42a22fa5c2a297` |
| `chartBuilder/components/plot/barPlot.ts` | `93487501e99a76e67f478da7100081766f643fcdd4c48de2c6ca3485152a704b` |
| `chartBuilder/components/plot/linePlot.ts` | `d792f03d349ef1f3da098f84b44b5a8031afe3febfa2e1f11181eb71fb44b54d` |
| `xyChart.md` | `f129b0dae594a86019c88af5f52279300089d5b3bfee50705910fb4d48528f66` |

## Intentional Kotlin Adaptations

- JavaScript insertion order maps to Kotlin lists and linked collections.
- Expected parser, semantic, resource, configuration, and layout failures use
  `GMResult.Err`.
- Categorical points without a corresponding value are omitted instead of
  retaining non-finite SVG coordinates.
- D3 point-scale behavior is represented by a band scale with inner padding
  `1`, outer padding `0`, and center alignment.
- Equal linear domains map values to the range midpoint.
- D3's straight line generator maps directly to `ScenePath` points.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, alignment, rotation, and line-height inputs for measurement
  and painting.
- `xychartRenderer.ts` keeps the configured SVG viewBox while browser SVG text
  remains visible through the default `overflow: visible`. Compose clips its
  Canvas, so `XyChartLayout` expands only the Scene viewport to include
  translated text bounds; plot, axis, title, and legend coordinates remain
  unchanged.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `XyJisonParserTest` covers parser statements, automatic domains,
  categorical truncation, mismatched series lengths, orientation, labels,
  metadata, and structured empty-chart errors.
- `XyChartLayoutTest` covers component layout, horizontal projection, data
  labels, nested configuration, theme variables, rotation, equal domains, and
  structured invalid-configuration errors.
- `XyChartStressTest` renders 256 deterministic random legal charts and checks
  finite geometry.
- `OfficialXyChartDocumentationCases` executes all 8 examples extracted from
  Mermaid's XY Chart documentation.
- The large-scale Web audit compares 256 unique same-source cases against
  Mermaid.js `12.0.0`, including long title and label-pressure profiles, and
  enforces content-bound and foreground-density limits.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, dependency versions, and
   hashes.
2. Diff `xychart.jison`, `xychartDb.ts`, `xychartRenderer.ts`,
   `chartBuilder/**`, theme files, the config schema, and `xyChart.md`.
3. Regenerate `XyJisonTables.kt` and translate changed semantic actions,
   database behavior, D3 operations, builder methods, renderer behavior, or
   themes in their mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Capture all 256 Native/Official XY Chart matrix pairs.
7. Review the contact sheets and update this map and compatibility matrix
   before publishing.
