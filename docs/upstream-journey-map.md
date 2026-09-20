# Upstream User Journey Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Native renderer | Compose Multiplatform Canvas |

The production User Journey path is Kotlin in `commonMain`. It does not
execute Mermaid.js and does not require a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `journey/upstream/mermaid/JourneyJisonTables.kt` | `src/diagrams/user-journey/parser/journey.jison` generated parser | Symbols, productions, LALR states, lexer rules, and conditions |
| `journey/upstream/mermaid/JourneyJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, errors, and portable regular-expression matching |
| `journey/upstream/mermaid/JourneyJisonParser.kt` | `journey.jison` semantic actions | Title, accessibility, section, and task actions translated by production index |
| `journey/upstream/mermaid/JourneyTypes.kt` | Task records created by `journeyDb.js` | Typed task, section, score, and actor data |
| `journey/upstream/mermaid/JourneyDb.kt` | `src/diagrams/user-journey/journeyDb.js` | Sections, current-section state, tasks, actor ordering, entity decoding, score conversion, and metadata |
| `journey/JourneyPlugin.kt` | `journeyDetector.ts` and `journeyDiagram.ts` | Diagram detection and parser/layout orchestration through the common plugin contract |

`tools/official-reference/generate-journey-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`JourneyJisonTables.kt`. Semantic actions remain indexed by their matching
Jison production so an upstream grammar diff can be translated incrementally.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `journey/JourneyLayout.kt` | `journeyRenderer.ts` | Actor measurement, margins, task coordinates, viewBox sizing, title, accessibility, and SceneGraph orchestration |
| `journey/JourneyLayout.kt` | `svgDraw.js` | Actor legend, sections, tasks, actor markers, score faces, vertical guides, activity line, arrow, and `fo`/`old`/`tspan` text strategies |
| `MermaidPreprocessor.kt` | Journey config schema and Mermaid directive sanitization | Geometry, text placement, palettes, fonts, and registered theme variables |
| `MermaidContract.kt` | Journey configuration and theme variables | Typed `MermaidJourneyOptions` and `MermaidJourneyTheme` |
| `SceneGraph.kt` | Journey SVG primitives | Shapes, paths, text, clipping metadata, and accessibility |
| `mermaid-compose/.../MermaidDiagram.kt` | SVG and `foreignObject` painting behavior | Native Canvas paths, text measurement, wrapping, and task-box clipping |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `journeyDetector.ts` | `584d29fceeb03276728efad7ae127a6d0ce2c3b59ea51ca6c60a049c9c4b5546` |
| `journeyDiagram.ts` | `3fe1736ea83f3e23e97e4400a0dd4a501e6b9392ff4613c2acd4403c1f072aa2` |
| `journeyRenderer.ts` | `9375a2d82076f98d6b5047e925ff8eca0b93dabd9098daaccf68e93abea0a61b` |
| `journeyDb.js` | `3e5e36410990a47ed06bf9bc24bab75341856dd50e2fa7c645b9c182e0ff13fe` |
| `styles.js` | `4a844bbe236231566cb14ee115228c21667a1cfb27dc8d96a01c999b96e57fcb` |
| `svgDraw.js` | `0a4b20cf8312adb00d258f9cff1737be99ebdb8ae72a3c7d965267f3676b9f57` |
| `journey.jison` | `2230b9f958f7c2f2381149b0e484cd203ff919cf43abd065beba7ab6b5a660a5` |
| `userJourney.md` | `85776352a3900fc96997926758edb25484c2137fdb30a5f1d0394ebdfdccaa7e` |

## Intentional Kotlin Adaptations

- JavaScript insertion order maps to Kotlin lists and linked collections.
- Expected parser, configuration, resource, and text-measurement failures use
  `GMResult.Err`.
- JavaScript `Number()` semantics are retained for empty, decimal, and invalid
  scores; non-finite scores do not create non-finite SceneGraph geometry.
- Browser `foreignObject` task labels map to `SceneText` with explicit bounds
  clipping. The shared `clipToBounds` field defaults to `false`, so existing
  diagram types retain their prior behavior.
- Mermaid's alphabetically sorted actor list supplies global color indexes;
  task actor circles retain source order.
- Actor legend labels preserve the upstream `drawActorLegend` x-coordinate plus
  `svgDrawCommon.drawText`'s `2 * boxTextMargin` tspan offset.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `JourneyJisonParserTest` covers grammar actions, sections, task data,
  metadata, JavaScript number conversion, and malformed input.
- `JourneyLayoutTest` covers upstream coordinates, actor color ordering,
  accessibility, long labels, face geometry, and all text-placement modes.
- `JourneyStressTest` renders 256 deterministic random legal diagrams.
- `OfficialJourneyDocumentationTest` executes the official documentation
  example extracted from Mermaid `12.0.0`.
- The replacement large-scale Web audit compares 256 unique same-source cases
  against Mermaid.js `12.0.0`: `237 pass / 19 manually reviewed / 0 fail`.
  All 19 reviews are benign platform-font line-segmentation differences for
  one complete long actor label; all 16 contact sheets were manually reviewed.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `journeyDetector.ts`, `journeyDiagram.ts`, `journeyRenderer.ts`,
   `journeyDb.js`, `styles.js`, `svgDraw.js`, `journey.jison`, and
   `userJourney.md`.
3. Regenerate `JourneyJisonTables.kt` and translate changed semantic actions,
   database behavior, renderer methods, text strategies, or styles in their
   mapped Kotlin files.
4. Regenerate the documentation fixture and review its hash.
5. Run full JVM tests, Android assembly, Web production, Desktop distribution,
   and every configured iOS compile target.
6. Capture and review all 256 Journey Native/Official pairs at the fixed
   `1200 x 900` viewport.
7. Update this map and the compatibility matrix before publishing.
