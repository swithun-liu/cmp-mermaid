# Upstream Entity Relationship Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Marked | `16.4.2` |
| Native renderer | Compose Multiplatform Canvas |

The production ER path is Kotlin in `commonMain`. It does not execute
Mermaid.js, require a WebView, or embed a JavaScript engine. Dagre is the
Native default; ELK selectors are recognized and rejected explicitly.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `erdiagram/upstream/mermaid/ErJisonTables.kt` | `src/diagrams/er/parser/erDiagram.jison` generated parser | Symbols, terminals, 86 semantic productions, LALR states, 83 lexer rules, and conditions |
| `erdiagram/upstream/mermaid/ErJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, errors, entity and attribute tokenization, and Android ICU-compatible matching |
| `erdiagram/upstream/mermaid/ErJisonParser.kt` | `erDiagram.jison` semantic actions | Entity, attribute, relationship, direction, style, subgraph, title, and accessibility actions translated by production index |
| `erdiagram/upstream/mermaid/ErTypes.kt` | `src/diagrams/er/erTypes.ts` | Typed entities, attributes, keys, relationships, cardinalities, classes, subgraphs, and statement values |
| `erdiagram/upstream/mermaid/ErDb.kt` | `src/diagrams/er/erDb.ts` | Entity identity, aliases, attributes, relationships, style classes, nested subgraphs, directions, and metadata |

`tools/official-reference/generate-er-parser.mjs` verifies Mermaid `12.0.0`
and the grammar SHA-256 before regenerating `ErJisonTables.kt`. Semantic
actions remain indexed by the matching Jison production so an upstream grammar
diff can be translated incrementally.

## Layout, Text, And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `erdiagram/ErLayout.kt` | `erDb.ts` `getData()` and `erRenderer-unified.ts` | Entity/subgraph projection, layout selection, relationship direction, labels, title, accessibility, and SceneGraph orchestration |
| `erdiagram/ErLayout.kt` | `rendering-elements/shapes/erBox.ts` | Entity header, alternating attribute rows, type/name/key/comment columns, dividers, padding, and minimum dimensions |
| `erdiagram/ErLayout.kt` | `erRenderer-unified.ts` and `styles.ts` | Theme colors, identifying and non-identifying lines, subgraph paint order, and ER configuration |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked Markdown, HTML spans, sanitization, and structured unsupported detection |
| `flowchart/FlowDagreLayout.kt` | Mermaid's unified Dagre path | Default entity and subgraph placement, edge routing, and distinct routing for repeated self-relationships |
| `flowchart/FlowElkLayout.kt` | Mermaid's unified ELK path | Retained mapping boundary that returns structured `UnsupportedFeature` |
| `flowchart/upstream/mermaid/MermaidEdgePathPort.kt` | `rendering-elements/edges.js` | Basis curves, endpoint correction, self-relationships, and relationship label anchors |
| `flowchart/upstream/mermaid/MermaidMarkerPort.kt` | `rendering-elements/markers.js` | Marker offset behavior, including ER markers anchored at entity boundaries |
| `SceneGraph.kt` | Mermaid ER relationship marker semantics | Typed crow-foot markers and marker background color |
| `mermaid-compose/.../MermaidDiagram.kt` | `markers.js` and `erMarkers.js` | Native bars, circles, quadratic crow-foot paths, orientation, and Canvas painting |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `erDiagram.jison` | `4f7c96b05d15bcc9c0e753bb106a5b8a67cfa4c8b2e13faab1d7098b92f9e5be` |
| `erTypes.ts` | `645a96b7717f27582f5623da5a12cb9334ce0c41966169633249e18c935746ca` |
| `erDb.ts` | `c048254a2fe1f3a294f41b901d8b9f73d0f6bbb1b999e8c9f82f7ba5b736aa69` |
| `erRenderer-unified.ts` | `e41c45a44665ca346aa22e9e427e584932edacdaaa8e9f0265dd8d7275a81371` |
| `erBox.ts` | `661b2d73c63686bd3c2f1407c1319f11d47481bb8f9a5b96874db7eaf29b0520` |
| `markers.js` | `e495d9d84cdf51ee2ead29e3656f98554e7a3a57d20a2873884a32522c4d8f35` |
| `erMarkers.js` | `afcae8780d19f8a0465fbbd785bd909e875cc5df7b7354432f006e3a02d509e4` |
| `styles.ts` | `8a090f8401285cf5256d6c113fa5c2cc9d206e6940f33bb7601da044a8c932c4` |
| `entityRelationshipDiagram.md` | `0be2f34ff578b2d86d0429ce288e8681ef46a27d645841a5162c78d501e5441b` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, text, and layout failures use
  `GMResult.Err`.
- Generated JavaScript lexer patterns that are not portable to Android ICU use
  equivalent Kotlin token scanners while retaining their Jison rule indexes.
- Mermaid DOM text maps to shared SceneGraph text spans and host-supplied
  measurement.
- Entity aliases containing browser wrap opportunities use Mermaid's configured
  `wrappingWidth` for both measurement and `SceneText.softWrap`. Unbroken entity
  identifiers remain single-line, matching browser `break-spaces` behavior
  instead of Compose's default character-level hard wrapping.
- Browser-only label content returns `UnsupportedFeature`.
- `MD_PARENT` remains in the translated grammar and DB, but maps to no
  SceneGraph marker because Mermaid 12's unified ER renderer does not register
  the legacy diamond marker from `erMarkers.js`.
- Parent subgraphs paint before nested children, matching `ErDB.getData()`'s
  reversed group projection.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `ErJisonParserTest` covers entity, attribute, relationship, style, nested
  subgraph, and every official documentation fixture.
- `ErLayoutTest` covers tables, keys, comments, cardinalities, identification,
  `MD_PARENT`, styles, nested subgraph ordering, configuration, title, and
  accessibility.
- `ErStressTest` renders 256 deterministic random legal diagrams under Dagre.
- `MermaidEngineTest` protects distinct Dagre routes for repeated
  self-relationships.
- `OfficialErDocumentationCases` executes all 24 examples extracted from
  Mermaid's ER documentation.
- The large-scale Web audit compares 256 unique same-source cases against
  Mermaid.js `12.0.0`, including long aliases and unbroken entity identifiers,
  and enforces content-bound and foreground-density limits.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `erDiagram.jison`, `erTypes.ts`, `erDb.ts`,
   `erRenderer-unified.ts`, `erBox.ts`, `markers.js`, `erMarkers.js`, and
   `styles.ts`.
3. Regenerate `ErJisonTables.kt` and translate changed semantic actions, DB
   behavior, data projection, entity geometry, marker methods, or styling in
   the mapped Kotlin files.
4. Regenerate the 24 documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Capture all 256 Native/Official ER matrix pairs with Dagre.
7. Review the contact sheets and update this map and the compatibility matrix
   before publishing.
