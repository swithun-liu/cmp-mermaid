# Upstream Requirement Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Native renderer | Compose Multiplatform Canvas |

The production Requirement path is Kotlin in `commonMain`. It does not execute
Mermaid.js and does not require a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `requirement/upstream/mermaid/RequirementJisonTables.kt` | `src/diagrams/requirement/parser/requirementDiagram.jison` generated parser | Symbols, productions, LALR states, lexer rules, and conditions |
| `requirement/upstream/mermaid/RequirementJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, errors, and portable regular-expression matching |
| `requirement/upstream/mermaid/RequirementJisonParser.kt` | `requirementDiagram.jison` semantic actions | Metadata, direction, requirement, element, relationship, style, and class actions translated by production index |
| `requirement/upstream/mermaid/RequirementTypes.kt` | `src/diagrams/requirement/types.ts` | Typed requirements, elements, relationships, risks, verification methods, classes, and styles |
| `requirement/upstream/mermaid/RequirementDb.kt` | `src/diagrams/requirement/requirementDb.ts` | Pending records, insertion order, relationships, direct styles, classes, direction, and metadata |
| `requirement/RequirementPlugin.kt` | `requirementDetector.ts` and `requirementDiagram.ts` | Diagram detection and parser/layout orchestration through the common plugin contract |

`tools/official-reference/generate-requirement-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`RequirementJisonTables.kt`. Semantic actions remain indexed by their matching
Jison production so an upstream grammar diff can be translated incrementally.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `requirement/RequirementLayout.kt` | `requirementDb.ts -> getData()` | Unified-layout nodes, edges, labels, classes, directions, and spacing |
| `requirement/RequirementLayout.kt` | `requirementRenderer.ts -> draw()` | Layout selection, markers, title, padding, and SceneGraph orchestration |
| `requirement/RequirementLayout.kt` | `requirementBox.ts -> requirementBox()` and `addText()` | Node measurement, type/name/body rows, HTML line boxes, divider, final label translation, and intersection bounds |
| `requirement/RequirementLayout.kt` | `rendering-elements/edges.js` | Relationship labels, dotted paths, marker assignment, and routed edge conversion |
| `requirement/RequirementLayout.kt` | `rendering-elements/markers.js` | Requirement arrow and contains marker semantics |
| `MermaidPreprocessor.kt` | Mermaid config schema and directive sanitization | Scoped Requirement theme/look options and registered theme variables |
| `MermaidContract.kt` | Mermaid theme variables | Typed `MermaidRequirementTheme` and built-in theme presets |
| `SceneGraph.kt` | Mermaid unified-renderer primitives | Requirement marker types and platform-independent geometry |
| `mermaid-compose/.../MermaidDiagram.kt` | SVG path and marker painting | Native Canvas paths, text, dividers, and Requirement marker geometry |

Requirement diagrams reuse the existing translations of Mermaid's unified
Dagre and ELK layout adapters. They do not introduce a separate approximate
layout algorithm.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `requirementDetector.ts` | `ccbee75e98d046928da580b5c80923bd82468ec81b830f8700d84b5aa1e8325a` |
| `requirementDiagram.ts` | `6124e0038c3f2b08deee0816ca3c01ae12718b2f06ba4dea48988ec5d5e3f299` |
| `requirementRenderer.ts` | `22683abc82e2809c03c37d40925b9f822fc79e9ab140becde9dc34dc5c73a1d1` |
| `requirementDb.ts` | `0672c40b4e56c7bb8fbda617e962c581f5428f748078d51bd3b1e9fedb0975a5` |
| `types.ts` | `c0330e0a556984cb869a5f5f00c3ff15657c46137c78427f2d5d15fdfe69c549` |
| `styles.js` | `555601d76fac63f5e0c30ca4bad12432ebdadcd9eac8e8536cd1377ea7b14003` |
| `requirementDiagram.jison` | `32030ae34660128966d31ecc25e5ce224ce9bf947e1e31bd3961f3879ae26ae7` |
| `requirementBox.ts` | `e6137cf2dcd3cec06309e21142e147fa28ef16e414d95befc67e95d2cf05c23e` |
| `edges.js` | `c65b94839cded66fc155279bc120df9e7efa0e342f34a4afc12743aa16a4b9ec` |
| `markers.js` | `e495d9d84cdf51ee2ead29e3656f98554e7a3a57d20a2873884a32522c4d8f35` |
| `requirementDiagram.md` | `f3f2f2724836103af18861a0399dedd6b9260841033f3c8c138995c05f90bf57` |

## Intentional Kotlin Adaptations

- JavaScript `Map` insertion order maps to Kotlin linked collections.
- Expected parser, configuration, resource, layout, and text-measurement
  failures use `GMResult.Err`.
- Browser HTML labels map to measured Compose text. The Kotlin translation
  explicitly preserves Mermaid's `14px`/`21px` line box before calculating
  node height.
- Mermaid initially chooses body alignment from the layout, then
  `requirementBox()` re-translates every body label to the left padding. Kotlin
  emits the final left-aligned SceneGraph position directly.
- Classic and Neo marker gaps follow Mermaid's rendered path behavior.
  RoughJS-backed `handDrawn` returns `MermaidError.UnsupportedFeature`.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `RequirementJisonParserTest` covers grammar actions, typed fields,
  relationships, directions, metadata, styling, and malformed input.
- `RequirementLayoutTest` covers upstream node geometry, text line boxes,
  final label alignment, layouts, markers, styles, themes, and accessibility.
- `RequirementStressTest` renders 256 deterministic random legal diagrams.
- `OfficialRequirementDocumentationTest` executes the examples extracted from
  Mermaid `12.0.0` documentation.
- The large-scale Web audit compares 256 unique same-source cases against
  Mermaid.js `12.0.0`; all 16 contact-sheet pages were manually reviewed.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff every upstream file listed in the hash table.
3. Regenerate `RequirementJisonTables.kt` and translate changed semantic
   actions, database behavior, renderer methods, shapes, markers, or styles in
   their mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run full JVM tests, Android assembly, Web production, Desktop distribution,
   and every configured iOS compile target.
6. Capture and review all 256 Requirement Native/Official pairs at the fixed
   `1200 x 900` viewport.
7. Update this map and the compatibility matrix before publishing.
