# Upstream State Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Marked | `16.4.2` |
| Native renderer | Compose Multiplatform Canvas |

The production State path is Kotlin in `commonMain`. It does not execute
Mermaid.js, require a WebView, or embed a JavaScript engine. Dagre is the
Native default; ELK selectors are recognized and rejected explicitly.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `statediagram/upstream/mermaid/StateJisonTables.kt` | `src/diagrams/state/parser/stateDiagram.jison` generated parser | Symbols, terminals, 49 productions, LALR states, lexer rules, and conditions |
| `statediagram/upstream/mermaid/StateJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, errors, `processId`, `unread`, and ICU-portable metadata rules |
| `statediagram/upstream/mermaid/StateJisonParser.kt` | `stateDiagram.jison` semantic actions | Statement-tree actions translated by production index |
| `statediagram/upstream/mermaid/StateTypes.kt` | State parser statement objects and `stateCommon.ts` | Typed statements, pseudostates, notes, relations, styles, and interactions |
| `statediagram/upstream/mermaid/StateDb.kt` | `src/diagrams/state/stateDb.ts` | `docTranslator`, scoped start/end ids, concurrency region splitting, state merging, relations, styles, links, and metadata |
| `statediagram/upstream/mermaid/StateDataFetcher.kt` | `src/diagrams/state/dataFetcher.ts` | Depth-first node/group projection, note edges, nested direction, color slots, and unified-renderer data |

`tools/official-reference/generate-state-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`StateJisonTables.kt`. Semantic actions remain indexed by the matching Jison
production so an upstream grammar diff can be translated incrementally.

## Layout, Text, And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `statediagram/StateLayout.kt` | `stateRenderer-v3-unified.ts` | Native Dagre selection, SceneGraph orchestration, title, accessibility, interactions, and bounds normalization |
| `statediagram/StateLayout.kt` | `rendering-util/rendering-elements/shapes/note.ts`, `shapes.js`, and `stateCommon.ts` | State boxes, description compartments, start/end, choice, fork/join, rectangular notes, composite groups, and concurrency regions |
| `statediagram/StateLayout.kt` | `dataFetcher.ts` note edges | Left/right note ordering expressed as directed dashed edges and placed by the selected layout engine |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked Markdown, HTML spans, sanitization, and structured unsupported detection |
| `flowchart/FlowDagreLayout.kt` | Mermaid's unified Dagre path | Default State and composite placement |
| `flowchart/FlowElkLayout.kt` | Mermaid's unified ELK path | Retained mapping boundary that returns structured `UnsupportedFeature` |
| `flowchart/upstream/mermaid/MermaidEdgePathPort.kt` | `rendering-elements/edges.js` and `utils/lineWithOffset.ts` | Curves, endpoint correction, self-transitions, and transition labels |
| `SceneGraph.kt` | Mermaid State shape semantics | Typed state, note, group, edge, text, metadata, and interaction output |
| `mermaid-compose/.../MermaidDiagram.kt` | State SVG rendering behavior | Native Canvas geometry and painting |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `stateDiagram.jison` | `94d4715707c3b35192b8586fa271e843f2c084e9ff70dd8db07c510c1a23e231` |
| `stateDb.ts` | `12a61b830057fd1fbb126fb64e6deab7cf8fb9883f4241487e47ed0bd8aa5bc5` |
| `dataFetcher.ts` | `03a1dfa682ca80a7b7742ccd8bdfca6613210a217f378ac362a44f43847d7116` |
| `stateRenderer-v3-unified.ts` | `ceaf69b19b171bdfae0f36b8a3f80cfb0ebc023cca7a391dc332eacd777859a6` |
| `shapes.js` | `9c011094113a0fc05f8b1dcfaf1992001431d4c72769c1f8f3c8ca576c6a6016` |
| `stateCommon.ts` | `7d1ac37f60fa0113b028682d1058244ce301204fee9054dc47fd3b474cba854f` |
| `stateDiagram.md` | `161c0aa290e79ddd5ced601a36dff946083214d8664bc697b86e1aea0989c583` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, text, and layout failures use
  `GMResult.Err`.
- Mermaid's mutable parser statement tree is retained as typed Kotlin data
  until `StateDb` performs the matching document translation and extraction.
- Android-incompatible generated metadata regex rules use equivalent
  line-prefix matchers while retaining their Jison rule indexes.
- Mermaid DOM text maps to shared SceneGraph text spans and host-supplied
  measurement.
- Browser-only label content returns `UnsupportedFeature`.
- SceneGraph interactions preserve sanitized links and tooltips while the host
  owns navigation.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `StateJisonParserTest` covers statement extraction, scoped start/end ids,
  concurrency translation, and every official documentation fixture.
- `StateLayoutTest` covers pseudostates, descriptions, Markdown, nested
  composites, concurrency, note edge direction, styles, interactions, title,
  accessibility, and structured unsupported errors.
- `StateStressTest` renders 256 deterministic random legal diagrams under
  Dagre.
- `OfficialStateDocumentationTest` executes all 22 examples extracted from
  Mermaid's State documentation.
- The Android gallery compares 25 identical sources against official Mermaid
  `12.0.0` Dagre output.
- The replacement Web gate compares 13 independent production scenarios and
  256 systematic same-source matrix cases. The matrix reports
  `256 pass / 0 review / 0 fail`; geometry ratios are `0.963-1.224` for width,
  `0.968-1.089` for height, and `1.031-1.383` for foreground ink.
- All 16 matrix contact sheets were manually reviewed with no unresolved
  state, group-boundary, note, pseudostate, routing, marker, label, clipping,
  overlap, or paint-order defect.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `stateDiagram.jison`, `stateDb.ts`, `dataFetcher.ts`,
   `stateRenderer-v3-unified.ts`, `shapes.js`, and `stateCommon.ts`.
3. Regenerate `StateJisonTables.kt` and translate changed semantic actions,
   document translation, data projection, shape methods, or edge behavior in
   the mapped Kotlin files.
4. Regenerate the 22 documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Install the Android sample and capture all 25 Native/Official State pairs
   with Dagre.
7. Review the contact sheets and update this map and the compatibility matrix
   before publishing.
