# Upstream Radar Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Radar path is Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `radar/RadarPlugin.kt` | `radar/detector.ts` and `radar/diagram.ts` | `radar-beta` detection plus parser/layout orchestration |
| `radar/upstream/mermaid/RadarParser.kt` | `packages/parser/src/language/radar/radar.langium` | Header, axes, curves, referenced and positional entries, and Radar options |
| `radar/upstream/mermaid/RadarParser.kt` | `common/common.langium` and `common/valueConverter.ts` | Titles, accessibility metadata, comments, quoted strings, numbers, booleans, and escapes |
| `radar/upstream/mermaid/RadarDb.kt` | `radar/parser.ts` `populate` and `radar/db.ts` | Common metadata, axis and curve projection, reference reordering, option precedence, defaults, and tick cap |
| `radar/upstream/mermaid/RadarTypes.kt` | Parser AST and `radar/types.ts` | Typed parser values, axes, curves, graticules, options, and database state |

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `radar/RadarLayout.kt` | `radar/renderer.ts` `draw`, `drawFrame`, `drawGraticule`, `drawAxes`, `drawCurves`, and `drawLegend` | Frame, circular and polygon graticules, axes, angular labels, curves, legend, title, and paint order |
| `radar/RadarLayout.kt` | `radar/renderer.ts` `relativeRadius` and `closedRoundCurve` | Range clipping and closed Catmull-Rom-to-cubic interpolation |
| `radar/RadarLayout.kt` | `radar/styles.ts` | Theme palette, stroke widths, opacities, text, legend, and title styling |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/directive config flow and Radar schema | Radar configuration merge and validation |
| `MermaidContract.kt` | Mermaid default config and theme variables | Typed Radar options and built-in theme values |
| `SceneGraph.kt` | Mermaid Radar SVG elements | Platform-independent circles, polygons, cubic paths, text, metadata, bounds, and paint order |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas cubic paths, shapes, text baselines, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `radar.langium` | `e6bab113778803c5b45969fc3197a9c01f7b8162b9dd4b71d421da20625ec535` |
| `common.langium` | `3c861872811260396786ee50129795274d7ef4208d53fcc3251d15d32a788cd4` |
| `common/valueConverter.ts` | `43b58df0bba9087c8b831cd53a12e8f1f85bf4a7352b388873d793325953ab37` |
| `radar/parser.ts` | `2126961fb2496c7bba6449b3d08f4c33ae9bb5e7f4f86a86fb5cf8884ed07d17` |
| `radar/db.ts` | `414a06aa0cadc5c4ffe9bd4a7df4594f85223efd3d2be1168618a972889fbd06` |
| `radar/renderer.ts` | `974e4b3d3d9ce5c7cdf5f283085a0e7a9ca2d68bffb22eee1577aaa15c34629c` |
| `radar/styles.ts` | `03ea335bbe7f8a1fb05e262f43372c37e1ac497e94ec37c22728e692ea6b2701` |
| `radar/types.ts` | `915192445e16e2bc9b33d37ef960e8a44291f1cc2fc84fe9c875e712a51c132d` |
| `radar/detector.ts` | `1cc2bc3d6b3098a90c2c3c7515e7d3f0d8fc83aa65ddb8041457f3f7ae35fe48` |
| `radar/diagram.ts` | `9b9746b82116159b8a01f55016bc520dec6e78b11104159bbea9a2071a39ef60` |
| `radar.md` | `61f9c4575b76df9f4af264cd29b4987543df227952f6daddfd28ca5f1ab40808` |

## Intentional Kotlin Adaptations

- The Langium grammar and value converter are expressed as a deterministic
  Kotlin scanner because the production path cannot execute parser JavaScript.
- Upstream throws for missing axis-referenced curve entries. Kotlin returns
  `MermaidError.Parse`; invalid configuration and zero value ranges likewise
  return typed `GMResult.Err` values.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, alignment, and SVG baseline semantics for measurement and
  painting.
- The final scene viewport mirrors the reference harness's Mermaid SVG
  `viewBox`/`getBBox()` union and 12-pixel padding so labels and legends outside
  the configured frame remain visible.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `RadarParserTest` covers header forms, metadata, labels, escapes, both entry
  forms, reference reordering, options, last-value precedence, tick capping,
  source locations, state reset, and structured errors.
- `RadarLayoutTest` covers frame geometry, paint order, circular and polygon
  graticules, cubic and linear curves, legend and title placement,
  configuration, theme variables, responsive sizing, SVG text baselines, and
  structured layout errors.
- `RadarStressTest` renders 256 deterministic randomized legal diagrams and
  checks finite geometry and deterministic replay.
- `OfficialRadarDocumentationTest` executes all three active examples
  extracted from Mermaid's Radar documentation.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
  Both corpora report `0 review / 0 fail`, all 269 geometry pairs pass, and all
  16 matrix contact sheets were manually reviewed.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff the mapped grammar, common statements, value converter, database,
   parser, renderer, styles, types, detector, and diagram registration.
3. Translate only changed parser, state, geometry, configuration, or style
   behavior in the mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Radar Native/Official pairs.
7. Update this map, the compatibility matrix, and the public stability report.
