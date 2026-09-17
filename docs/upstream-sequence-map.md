# Upstream Sequence Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
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
| `sequence/SequenceLayout.kt` | Mermaid `sequenceRenderer.ts` -> `calculateLoopBounds` and `adjustLoopHeightForWrap`; `src/utils.ts` -> `wrapLabel` | Two-pass control-frame width preflight, title wrapping, line height, and explicit title-line elements |
| `SceneGraph.kt` | Mermaid Sequence marker names | Typed marker variants shared with the Compose painter |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid Sequence SVG marker definitions | Native Canvas drawing for async, cross, half, stick, and standard arrowheads |

Mermaid browser text measurement is supplied by the shared
`TextMetricProvider`. HTML break tags are translated through Mermaid's
`splitBreaks` behavior before native measurement and painting.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `sequenceDiagram.jison` | `8771148ef56c2b98b021597a53585e45acc088cb321a3d457d265eca352fb40f` |
| `sequenceDb.ts` | `55b87176caa3e548be11a1582bd1c30317014f7d135ad34deff66a725b40b8e1` |
| `sequenceRenderer.ts` | `dc715189fee677a89badd2c2400a99718aaff8e189c81fa07dd28ec5f2aa66af` |
| `svgDraw.js` | `002f90315d0e921a4212cc3a8e02ad4747e5d497243dc8bd83a934a41921fa3c` |
| `actorBands.ts` | `8dbe6f979238850123cf74d93910323fab82394ac87af0ab089a3afee7b2fdc4` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, and layout failures use
  `GMResult.Err`.
- Browser-only participant menus, properties, and DOM `details` references
  return `MermaidError.UnsupportedFeature`.
- Control-title wrapping uses Mermaid's loop-width preflight and word-breaking
  behavior. A calibrated `1.1` SVG-to-Compose measurement factor preserves the
  upstream wrap boundary while native glyph metrics own final placement.
- Non-wrapped participant text reports measured glyph bounds instead of the
  containing actor box so manifest overlap checks describe visible paint.
- SceneGraph output remains platform independent; Compose owns the final
  Canvas painting.

## Parity Gate

- `SequenceJisonParserTest` covers lexer states, parser semantics, all message
  families, lifecycle, metadata, and malformed input.
- `SequenceLayoutTest` covers participant geometry, notes, controls,
  activations, endpoint offsets, markers, wrapping, line breaks, control-title
  preflight, created-participant text bounds, and resource limits.
- `OfficialSequenceDocumentationTest` executes all 38 examples extracted from
  Mermaid's Sequence documentation. The two browser menu examples must return
  their declared `UnsupportedFeature`.
- The independent production audit passes
  `14 pass / 0 review / 0 fail`. Geometry ratios are width `1.019-1.254`,
  height `0.910-1.111`, and foreground ink `0.939-1.327`.
- The large-scale Web audit compares 256 unique same-source cases against
  Mermaid.js `12.0.0`. It passes the replacement detail and geometry gates
  with `256 pass / 0 review / 0 fail`; width `1.023-1.068`, height
  `0.893-1.045`, and foreground ink `0.926-1.148`.
- All 16 matrix contact sheets were manually reviewed with no unresolved
  visual defect.

## Upgrade Procedure

1. Update the locked Mermaid version and grammar/distribution hashes.
2. Diff `sequenceDiagram.jison`, `sequenceDb.ts`, `sequenceRenderer.ts`,
   `svgDraw.js`, and `actorBands.ts` between Mermaid versions.
3. Regenerate `SequenceJisonTables.kt` and translate changed semantic actions
   or renderer methods in their corresponding Kotlin files.
4. Regenerate the 38 documentation fixtures and review the fixture hash.
5. Run JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Capture the independent production corpus and all 256 same-source matrix
   pairs.
7. Run detail and geometry audits, review all 16 contact sheets, and update
   this map and the Sequence compatibility matrix before publishing.
