# Upstream Pie Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Pie path is Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime. Mermaid 12's Pie
grammar is Langium-based rather than Jison-based.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `pie/upstream/mermaid/PieParser.kt` | `packages/parser/src/language/pie/pie.langium` | Header, `showData`, statements, sections, title, accessibility metadata, and comments |
| `pie/upstream/mermaid/PieParser.kt` | `packages/parser/src/language/pie/tokenBuilder.ts` | Quoted labels, inline comments, whitespace, and number token boundaries |
| `pie/upstream/mermaid/PieParser.kt` | `packages/parser/src/language/pie/valueConverter.ts` | String escapes, Unicode escapes, and numeric conversion |
| `pie/upstream/mermaid/PieDb.kt` | `packages/mermaid/src/diagrams/pie/pieDb.ts` | Ordered sections, first-duplicate-wins behavior, negative-value validation, title, and accessibility metadata |
| `pie/upstream/mermaid/PieTypes.kt` | `packages/mermaid/src/diagrams/pie/pieTypes.ts` | Typed section data passed from the database to layout |
| `pie/PiePlugin.kt` | `packages/mermaid/src/diagrams/pie/pieParser.ts` | Parser and database orchestration through the common plugin contract |

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `pie/PieLayout.kt` | `packages/mermaid/src/diagrams/pie/pieRenderer.ts` | One-percent filtering, percentages, title, legend transforms, donut/highlight options, bounds, and SceneGraph output |
| `pie/PieLayout.kt` | D3 `pie`, `arc`, and ordinal-scale defaults used by `pieRenderer.ts` | Input-order angles, centroid placement, arc geometry, twelve-color palette indexing, and palette cycling |
| `pie/PieLayout.kt` | `packages/mermaid/src/diagrams/pie/pieStyles.ts` | Slice opacity/strokes and title, section, and legend typography |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/directive config flow and Pie schema | `textPosition`, `donutHole`, `legendPosition`, `highlightSlice`, theme variables, and host precedence |
| `SceneGraph.kt` | Mermaid Pie SVG elements | Typed circles, sampled arc/ring paths, swatches, labels, metadata, and bounds |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas shapes, paths, text, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `pie.langium` | `dc326f2fb756e9d2e171a9f26328d4d106164fea21e2e359d46cb62b9d82f45c` |
| `tokenBuilder.ts` | `57b4f683cd315c25648eeb8e0815bf25b0ace579b1be1919403c191d8ea650c0` |
| `valueConverter.ts` | `a70d893ad1f27fdd98a34ccbf78f2ce1f3946466f26af29544d4e3125f5b97cd` |
| `pieDb.ts` | `a62abce09f68f73aa3d17352a7df667a0d6e6d191431a63524e99d18dfd67549` |
| `pieParser.ts` | `0e575dc79de74fd5e27bf06e35eda9dee961f7280a9f5073a058fa55033de01e` |
| `pieRenderer.ts` | `218999f10e97e8bdd46942ad5ffe756a5b3047a7bf57aad5e02dbc88a40c11d0` |
| `pieStyles.ts` | `0fd877fd0ef35aeb1eaf4838e35ea18996f0b72e0248f6e135486d19ba8c2a3e` |
| `pie.md` | `ca76077badb79e1d2f83ebe4ac7575778425e52f5f6928bdab5024ec70576852` |

## Intentional Kotlin Adaptations

- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, and configuration failures use
  `GMResult.Err`.
- The Langium grammar and converters are expressed as a deterministic Kotlin
  scanner because the production path cannot execute parser JavaScript.
- D3 arc curves are represented by deterministic two-degree point samples in
  SceneGraph.
- A full-circle donut uses a fill-only ring path plus separate inner and outer
  strokes so Canvas does not draw a radial closing seam.
- `highlightSlice: hover` returns `UnsupportedFeature`; a named static slice
  uses Mermaid's scale and opacity rules.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, and line-height inputs for measurement and painting.
- The final scene viewport mirrors the reference harness's Mermaid SVG
  `viewBox`/`getBBox()` union and 12-pixel padding, translating negative bounds
  because `MermaidScene` has no negative viewport origin.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `PieParserTest` covers grammar, quoting, escapes, comments, metadata,
  duplicate labels, negative values, negative zero, and malformed numbers.
- `PieLayoutTest` covers filtering, input order, palette indexing, donut
  geometry, highlighting, legend transforms, themes, metadata, zero-only
  data, and structured unsupported errors.
- `PieStressTest` renders 256 deterministic random legal diagrams and checks
  finite geometry, visible slices, and retained legend counts.
- `OfficialPieDocumentationCases` executes both examples extracted from
  Mermaid's Pie documentation.
- The Android gallery compares 20 identical sources against official Mermaid
  `12.0.0` output.
- The replacement gate compares 256 deterministic same-source matrix cases:
  `256 pass / 0 review / 0 fail`, 256/256 geometry passes, and 16/16 manually
  reviewed contact sheets.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `pie.langium`, `tokenBuilder.ts`, `valueConverter.ts`, `pieDb.ts`,
   `pieParser.ts`, `pieRenderer.ts`, `pieStyles.ts`, and `pie.md`.
3. Translate only changed grammar, conversion, database, renderer, D3, or
   style behavior in the mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Install the Android sample and capture all 20 Native/Official gallery pairs.
7. Capture the 256-case matrix and run detail and geometry audits.
8. Review all matrix contact sheets and update this map, the compatibility
   matrix, and the public stability report before publishing.
