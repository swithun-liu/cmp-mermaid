# Upstream Sequence Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Jison runtime | `0.4.18` |
| Native renderer | Compose Multiplatform Canvas |

The production Sequence path is Kotlin in `commonMain`. It does not execute
Mermaid.js and does not require a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `sequence/upstream/mermaid/SequenceJisonTables.kt` | Mermaid `src/diagrams/sequence/parser/sequenceDiagram.jison` generated parser | Generated symbols, productions, LALR states, lexer rules, and conditions |
| `sequence/upstream/mermaid/SequenceJisonRuntime.kt` | Jison `0.4.18` generated lexer/parser runtime | Parser stack, lexer states, locations, and portable translations for ICU-incompatible regular expressions |
| `sequence/upstream/mermaid/SequenceJisonParser.kt` | Mermaid `sequenceDiagram.jison` semantic actions | All 105 productions translated to typed Kotlin actions |
| `sequence/upstream/mermaid/SequenceDb.kt` | Mermaid `src/diagrams/sequence/sequenceDb.ts` | Actors, boxes, messages, activation, lifecycle, autonumber, metadata, and validation state |
| `sequence/upstream/mermaid/SequenceTypes.kt` | Mermaid Sequence types and line constants | Typed parser actions and diagram model |

`tools/official-reference/generate-sequence-parser.mjs` reads the locked
Mermaid distribution, verifies the upstream grammar SHA-256, and regenerates
`SequenceJisonTables.kt`. Semantic actions remain grouped by the corresponding
Jison production so an upstream grammar diff can be translated incrementally.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `sequence/SequenceLayout.kt` | Mermaid `sequenceRenderer.ts` | Actor spacing, message placement, activations, notes, controls, lifecycle, boxes, and SceneGraph conversion |
| `sequence/SequenceLayout.kt` | Mermaid `svgDraw.js` | Participant geometry, messages, self-loops, actor bands, markers, and endpoint offsets |
| `sequence/SequenceLayout.kt` | Mermaid `actorBands.ts` | Participant type glyphs and top/bottom bands |
| `SceneGraph.kt` | Mermaid Sequence marker names | Typed marker variants shared with the Compose painter |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid Sequence SVG marker definitions | Native Canvas drawing for async, cross, half, stick, and standard arrowheads |

Mermaid browser text measurement is supplied by the shared
`TextMetricProvider`. HTML break tags are translated through Mermaid's
`splitBreaks` behavior before native measurement and painting.

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, and layout failures use
  `GMResult.Err`.
- Browser-only participant menus, properties, and DOM `details` references
  return `MermaidError.UnsupportedFeature`.
- SceneGraph output remains platform independent; Compose owns the final
  Canvas painting.

## Parity Gate

- `SequenceJisonParserTest` covers lexer states, parser semantics, all message
  families, lifecycle, metadata, and malformed input.
- `SequenceLayoutTest` covers participant geometry, notes, controls,
  activations, endpoint offsets, markers, wrapping, line breaks, and resource
  limits.
- `OfficialSequenceDocumentationTest` executes all 38 examples extracted from
  Mermaid's Sequence documentation. The two browser menu examples must return
  their declared `UnsupportedFeature`.
- The Android gallery captures 35 identical Native/Official sources on the
  same device viewport for visual inspection.

## Upgrade Procedure

1. Update the locked Mermaid version and grammar/distribution hashes.
2. Diff `sequenceDiagram.jison`, `sequenceDb.ts`, `sequenceRenderer.ts`,
   `svgDraw.js`, and `actorBands.ts` between Mermaid versions.
3. Regenerate `SequenceJisonTables.kt` and translate changed semantic actions
   or renderer methods in their corresponding Kotlin files.
4. Regenerate the 38 documentation fixtures and review the fixture hash.
5. Run JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Install the Android sample and capture the complete 35-case
   Native/Official Sequence gallery.
7. Update this map and the Sequence compatibility matrix before publishing.
