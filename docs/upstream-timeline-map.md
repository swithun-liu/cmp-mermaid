# Upstream Timeline Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Timeline path is Kotlin in `commonMain`. It does not execute
Mermaid.js, require a WebView, embed a JavaScript engine, or access the
network.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `timeline/TimelineParser.kt` | `timeline/parser/timeline.jison` | Header and direction, title, accessibility metadata, comments, sections, periods, events, and lexer-compatible text splitting |
| `timeline/TimelineParser.kt` | `timeline/timelineDb.js` and `common/commonDb.ts` | Current-section state, source-order task/event records, default direction, and metadata |
| `timeline/TimelineLayout.kt` | `timeline/timelineRenderer.ts` | Left-to-right section/task/event placement, connector lengths, horizontal axis, title, and viewport |
| `timeline/TimelineLayout.kt` | `timeline/timelineRendererVertical.ts` | Top-down sections, task/event columns, spacing, vertical axis, title, and viewport |
| `timeline/TimelineLayout.kt` | `timeline/svgDraw.js` | Text wrapping and measurement, node paths, bottom lines, arrowheads, gradients, shadows, and event brightness |
| `timeline/TimelineLayout.kt` | `timeline/styles.js` | Classic, Redux, color-scale, dark, neutral, and neo styling behavior |
| `timeline/TimelinePlugin.kt` | `timeline/detector.ts` and `timeline/timeline-definition.ts` | Detection, direction-based renderer selection, and parser/layout orchestration |
| `MermaidPreprocessor.kt` and `MermaidContract.kt` | `schemas/config.schema.yaml` and `themes/theme-*.js` | Typed Timeline configuration, all built-in themes, color-scale variables, gradients, and precedence |
| `SceneGraph.kt` and `mermaid-compose/.../MermaidDiagram.kt` | Timeline SVG output and `setupGraphViewbox.js` | Platform-independent paths, text, markers, paint order, viewport sizing, and Canvas painting |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `timeline.jison` | `65a1aaa24f2a9f5416b89b9774847e832cc2e810bb4233d77ad756c94f8baf8f` |
| `timelineDb.js` | `d7f8ff92b7b21c3fb5276a22ad6b848c546e16230092e42c71f768913a8902b6` |
| `timelineRenderer.ts` | `97569e84590735610f8aeda0f4b923c3baf3b8f6e2b6be6c5e37bb9fe0f87386` |
| `timelineRendererVertical.ts` | `db954e44b4d8864447f8553c09b0122e70e4c697d0e9b0b6a37907ecb38900ec` |
| `svgDraw.js` | `e9a0e849fd6172f731e6728f4a1f9dec3b9e88a73bc42a289ac53780b88c63f0` |
| `styles.js` | `c1bcc7d2d46b12b1740639b73c88a0dad034dcc58b57bfcb4dc6e455f0b617ad` |
| `detector.ts` | `90a282b3dc0095612d2c437810cb2165e31497554c1297ba145f761608564172` |
| `timeline-definition.ts` | `41ae285b6d399e9d96146ca4d2432e02d6928f8bb6d854d68f1562e44749e03c` |
| `commonDb.ts` | `cb83451b20ab88b7dbaa931c0c9c7c9ff1aad1b3256b9937f0a0318db9f1c9a8` |
| `setupGraphViewbox.js` | `ad03574ae263ef22138f0c663e8e338d19458c34da5c995f8aa62ba28411b08e` |
| `config.schema.yaml` | `5b59ca5612d5ebfbf1ef9277601224a734c831e16f3d1654ae74481dea4b26b0` |
| `theme-default.js` | `bd25bdb136ffa3030c4eddf211643d6f24049c7aef0eced66002732506fb4aa7` |
| `timeline.md` | `787400fc48f737b77af84d9fef8ab713f52d5892542d6eb4357c7cf2ff93fa46` |

## Intentional Kotlin Adaptations

- Expected parser, configuration, resource, and text-measurement failures
  return `GMResult.Err`.
- Browser text measurement is supplied by `TextMetricProvider`; the SVG
  `getBBox()` line-box relationship is retained when deriving node heights.
- Mermaid passes the whole task object to the virtual-height helper before
  drawing. JavaScript coerces that object to `[object Object]`; Kotlin uses the
  same placeholder so long labels do not incorrectly raise every task row.
- SVG paths, lines, gradients, shadows, and marker arrows map to typed
  SceneGraph elements while preserving the renderer's effective paint order.
- JavaScript arrays and mutable database state map to source-ordered Kotlin
  lists scoped to one parse.
- Upstream Timeline accepts several inherited configuration properties that
  its renderer does not read. Kotlin validates and retains the same config
  shape without inventing behavior for those no-op fields.
- Compose owns final Canvas sizing, so Scene bounds include visible translated
  content instead of relying on browser SVG overflow.

## Verification

- Fourteen official documentation examples are generated from the pinned
  source document and rendered in JVM tests.
- Focused parser/layout tests cover directions, database ordering, continued
  events, metadata, text wrapping, configuration, themes, malformed input,
  paint order, and resource limits.
- A separate deterministic generator renders 256 legal Timelines twice and
  checks finite SceneGraph geometry.
- The production detail audit passes all 13 independent Timeline cases.
- The large-scale matrix contains 256 unique same-source Native/Official
  Timeline cases. At a shared `1200 x 900` viewport, all 256 pass the
  manifest/raster detail audit with no review queue and all 256 pass the
  geometry gate.
- Sixteen contact sheets cover the complete matrix and were all inspected for
  clipping, overlap, paint order, geometry, color, missing content, and LR/TD
  connector differences.

## Upgrade Procedure

1. Update the baseline version, source commit, and locked hashes.
2. Diff every mapped Timeline file, the config schema, relevant theme files,
   `setupGraphViewbox.js`, and `timeline.md`.
3. Translate changed parser, database, renderer, text, style, theme, or config
   behavior in the mapped Kotlin files.
4. Regenerate the 14 documentation fixtures and both production corpora.
5. Run JVM and platform gates, then capture and review all 256 pairs.
6. Update this map and the compatibility matrix before publishing.
