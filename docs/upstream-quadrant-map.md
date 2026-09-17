# Upstream Quadrant Chart Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Quadrant Chart path is Kotlin in `commonMain`. It does not
execute Mermaid.js and does not require a WebView.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `quadrant/QuadrantParser.kt` | `quadrant-chart/parser/quadrant.jison` | Header, title, axes, quadrant labels, points, classes, comments, metadata, and parse errors |
| `quadrant/QuadrantParser.kt` | `quadrant-chart/quadrantDb.ts` | Point/class state, style validation, entity decoding, and prepend ordering |
| `quadrant/QuadrantLayout.kt` | `quadrant-chart/quadrantBuilder.ts` | Chart-space calculation, quadrants, borders, points, labels, styles, and theme resolution |
| `quadrant/QuadrantLayout.kt` | `quadrant-chart/quadrantRenderer.ts` | Element groups, SVG paint order, text anchors, rotations, and viewport behavior |
| `quadrant/QuadrantPlugin.kt` | `quadrantDetector.ts` and `quadrantDiagram.ts` | Detection and parser/layout orchestration |
| `MermaidPreprocessor.kt` | Mermaid frontmatter flow and config schema | All Quadrant Chart options and theme variables |
| `MermaidColorMath.kt` | `themes/theme-helpers.js` | Khroma-compatible `mkBorder` saturation/lightness adjustment |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `quadrant.jison` | `50ee71fea6f3ee64bbe25e41c1b20a5e93a41b895544f94fca43afe28a7d12bb` |
| `quadrantDb.ts` | `7b551fb6eeff5423b86bc62e1b4f3bc37f8b3e3ce03117b66c6f9be8f93fd159` |
| `quadrantBuilder.ts` | `52a9c6e75c36b68109c2952c289f003ba3cb3b179dca91f4388a8777c5bb0419` |
| `quadrantRenderer.ts` | `684d6c1eac6cfca2109ab84f9f2f9b99e96a2c34b4891a8a3d57bc78c701aaab` |
| `utils.ts` | `e60af1b212aaec0dc9f416b214f05710bfb4acb98989ebcb4807221dfe1dd06c` |
| `theme-default.js` | `bd25bdb136ffa3030c4eddf211643d6f24049c7aef0eced66002732506fb4aa7` |
| `theme-helpers.js` | `27e2ab0edc2f78b92ad22bdc06c3c3bbd785a2098bea28b1055daad482421425` |
| `quadrantChart.md` | `7e685b3c98ca8050847a6173f34fc00549894b1dd3d8a7e57a8c604bb244b3fc` |

## Intentional Kotlin Adaptations

- Expected parser, style, configuration, and resource failures return
  `GMResult.Err`.
- DOM text measurement is supplied by `TextMetricProvider`.
- Mermaid keeps a fixed SVG viewBox while text overflow remains visible.
  Compose clips its Canvas, so only the Scene viewport expands to include
  translated text bounds; configured chart geometry remains unchanged.
- JavaScript map and list insertion order maps to Kotlin linked collections
  and lists, including `QuadrantBuilder.addPoints` prepend behavior.

## Verification

- Three official documentation examples are generated from the pinned source
  document and rendered in JVM tests.
- Focused parser/layout tests cover metadata, entities, comments, classes,
  direct-style precedence, all configuration fields, themes, malformed input,
  and resource limits.
- A separate deterministic generator renders 256 legal charts twice and
  checks finite SceneGraph geometry.
- The production detail audit passes all 13 independent Quadrant cases.
- The large-scale matrix contains 256 unique same-source Native/Official
  Quadrant cases. At a shared `1200 x 900` viewport, all 256 pass the
  manifest/raster detail audit with no review queue and all 256 pass the
  geometry gate.
- Sixteen contact sheets cover the complete matrix. Representative first,
  middle, and final sheets were inspected for clipping, overlap, paint order,
  geometry, color, and missing content.

## Upgrade Procedure

1. Update the baseline version, commit, and locked hashes.
2. Diff every mapped upstream file and `quadrantChart.md`.
3. Translate changed parser, database, builder, renderer, theme, or schema
   behavior in the mapped Kotlin files.
4. Regenerate documentation and stability corpora.
5. Run JVM and platform gates, then capture and review all 256 pairs.
