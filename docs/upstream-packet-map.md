# Upstream Packet Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Packet path is Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `packet/PacketPlugin.kt` | `packet/detector.ts` and `packet/diagram.ts` | `packet` and `packet-beta` detection plus parser/layout orchestration |
| `packet/upstream/mermaid/PacketParser.kt` | `packages/parser/src/language/packet/packet.langium` | Headers, explicit ranges, single bits, counted fields, labels, and statement order |
| `packet/upstream/mermaid/PacketParser.kt` | `common/common.langium` and `common/valueConverter.ts` | Titles, accessibility metadata, comments, quoted strings, and escapes |
| `packet/upstream/mermaid/PacketDb.kt` | `packet/parser.ts` `populate` and `getNextFittingBlock` | Contiguous-field validation, automatic starts, row splitting, and the 10,000-row guard |
| `packet/upstream/mermaid/PacketDb.kt` | `packet/db.ts` | Packet words, metadata, configuration, and effective vertical padding |
| `packet/upstream/mermaid/PacketTypes.kt` | Parser AST and `packet/types.ts` | Source blocks, resolved blocks, and rows |

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `packet/PacketLayout.kt` | `packet/renderer.ts` `draw` and `drawWord` | Grid dimensions, field rectangles, labels, bit numbers, title, and responsive sizing |
| `packet/PacketLayout.kt` | `packet/styles.ts` | Fixed block fill, strokes, text colors, and font sizes |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/directive config flow and Packet schema | Packet configuration merge and validation |
| `SceneGraph.kt` | Mermaid Packet SVG elements | Platform-independent rectangles, text, metadata, bounds, and paint order |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas rectangles, text measurement, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packet.langium` | `05e4541d176be506b9e0b373f6d86ee3b0d3489650a3d027aaf3319dba4c3176` |
| `common.langium` | `3c861872811260396786ee50129795274d7ef4208d53fcc3251d15d32a788cd4` |
| `common/valueConverter.ts` | `43b58df0bba9087c8b831cd53a12e8f1f85bf4a7352b388873d793325953ab37` |
| `packet/parser.ts` | `eca493c104ec640920666ec593a7259919cc83d556cc112142d8a1545dbbc366` |
| `packet/db.ts` | `24b41ca02d7fde498e3f25231dc2643ae86351f7fbff3c7cbcf4e3f8b6646262` |
| `packet/renderer.ts` | `8c65456b487c55433c1c9facf3cb238aaf432f49d0ee09ed915984a6d65df97c` |
| `packet/styles.ts` | `c0dc37a89784e1588df0fdabaa4fe5daaf8b782849a1cec69640c2a355672248` |
| `packet/types.ts` | `f0e242ca08305f716c4c21146cfa6265dbaaae5506957a96cc76d402c5bea3c9` |
| `packet/detector.ts` | `9bcefeef9757e73a2462052b143aec2bef66f9214a64100ac2c0071ccb28c0f6` |
| `packet/diagram.ts` | `03cf061c9f1c7757bcc76fde7ddacf9b53022ed29b131c15e2c6448294b31b79` |
| `packet.md` | `a3e5127a8f21aa9e1575ddccbdda3a01a8ee4fd987550c3b58be5ac7c7342152` |

## Intentional Kotlin Adaptations

- The Langium grammar and value converter are expressed as a deterministic
  Kotlin scanner because the production path cannot execute parser
  JavaScript.
- Mermaid stops generating after 10,000 rows and silently returns a partial
  packet. The Kotlin API preserves the same bound but returns
  `MermaidError.ResourceLimit` so truncation cannot be mistaken for success.
- Expected parser, configuration, integer-range, and resource failures use
  `GMResult.Err`.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, and alignment inputs for measurement and painting.
- The final scene viewport mirrors the reference harness's Mermaid SVG
  `viewBox`/`getBBox()` union and 12-pixel padding.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `PacketParserTest` covers both headers, field forms, row splitting, metadata,
  comments, escapes, malformed input, source locations, overflow, and the row
  limit.
- `PacketLayoutTest` covers fixed geometry and styles, bit numbers, titles,
  configuration, intrinsic sizing, metadata, and structured configuration
  errors.
- `PacketStressTest` renders 256 deterministic random legal diagrams and checks
  finite geometry, element counts, and deterministic replay.
- `OfficialPacketDocumentationTest` executes both active examples extracted
  from Mermaid's Packet documentation.
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
6. Capture and review all 256 Packet Native/Official pairs.
7. Update this map, the compatibility matrix, and the public stability report.
