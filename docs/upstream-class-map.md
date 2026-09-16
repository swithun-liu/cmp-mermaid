# Upstream Class Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| Marked | `16.4.2` |
| Native renderer | Compose Multiplatform Canvas |

The production Class path is Kotlin in `commonMain`. It does not execute
Mermaid.js, require a WebView, or embed a JavaScript engine. Dagre is the
Native default; ELK selectors are recognized and rejected explicitly.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `classdiagram/upstream/mermaid/ClassJisonTables.kt` | `src/diagrams/class/parser/classDiagram.jison` generated parser | Symbols, terminals, productions, LALR states, lexer rules, and conditions |
| `classdiagram/upstream/mermaid/ClassJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, error recovery, and ICU-portable lexer rules |
| `classdiagram/upstream/mermaid/ClassJisonParser.kt` | `classDiagram.jison` semantic actions | All 110 productions translated to typed Kotlin actions |
| `classdiagram/upstream/mermaid/ClassDb.kt` | `src/diagrams/class/classDb.ts` | Classes, members, relations, notes, namespaces, styles, links, callbacks, metadata, and lollipop interfaces |
| `classdiagram/upstream/mermaid/ClassTypes.kt` | `src/diagrams/class/classTypes.ts` | Relation constants, class/member models, classifiers, and generic parsing |

`tools/official-reference/generate-class-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`ClassJisonTables.kt`. Semantic actions remain indexed by the matching Jison
production so an upstream grammar diff can be translated incrementally.

## Layout, Text, And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `classdiagram/ClassLayout.kt` | `classDb.getData()` and `classRenderer-v3-unified.ts` | Unified renderer data, Native Dagre selection, SceneGraph orchestration, title, interactions, and normalization |
| `classdiagram/ClassLayout.kt` | `rendering-elements/shapes/classBox.ts` and `diagrams/class/shapeUtil.ts` | Class measurement, annotations, compartments, dividers, empty boxes, members, and methods |
| `classdiagram/ClassLayout.kt` | `rendering-util/rendering-elements/shapes/note.ts` | Rectangular notes, attached note edges, and note colors |
| `classdiagram/ClassLayout.kt` | `rendering-elements/edges.js`, `utils/lineWithOffset.ts` | Relations, center labels, terminal cardinalities, self-relations, and marker-aware offsets |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked Markdown, HTML spans, sanitization, and structured unsupported detection |
| `flowchart/FlowDagreLayout.kt` | Mermaid's unified Dagre path | Default Class and namespace placement using translated Graphlib/Dagre |
| `flowchart/FlowElkLayout.kt` | Mermaid's unified ELK path | Retained mapping boundary that returns structured `UnsupportedFeature` |
| `flowchart/upstream/mermaid/MermaidEdgePathPort.kt` | `edges.js` and `lineWithOffset.ts` | Curves and endpoint correction |
| `flowchart/upstream/mermaid/MermaidMarkerPort.kt` | `rendering-elements/markers.js` | Class marker endpoint offsets |
| `SceneGraph.kt` | Mermaid Class marker names | Typed aggregation, composition, extension, dependency, and lollipop variants |
| `mermaid-compose/.../MermaidDiagram.kt` | `markers.js` | Native Canvas marker geometry and painting |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `classDiagram.jison` | `c2ea20022e4adf501dbbcda909a1bdb74e05a2ac53ff5276d09d00fd54a3e21e` |
| `classDb.ts` | `acae7a027b5696314b8f523545410585d4bca29e0586936c22156cc3c969d3b1` |
| `classTypes.ts` | `647859e7d44385ebb73a6689360ca6f9e4091faf7ae0b412735472d2e2106efc` |
| `classRenderer-v3-unified.ts` | `db93bb9728f91e0546c9a273167e95c313d05ced253b2effb14220f724677cc7` |
| `classBox.ts` | `6abab6a811aaaec670524112cfa9093ab954c3bea3d65eb65332fd648c094721` |
| `shapeUtil.ts` | `ed768affadef0163dce386f6bbeffdc0b49d0e8188d23d4c72a5132da581ed43` |
| `classDiagram.md` | `bb594432ec833b06a4f81395dc26dc17be99a9e07ae8620346e023a0a6b2ad3b` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, resource, text, and layout failures use
  `GMResult.Err`.
- Mermaid DOM text maps to shared SceneGraph text spans and host-supplied
  measurement.
- Browser-only label content returns `UnsupportedFeature`.
- SceneGraph interactions preserve links, callbacks, and tooltips while the
  host owns their execution.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `ClassJisonParserTest` covers lexer states, parser semantics, namespaces,
  notes, members, relations, styles, and malformed input.
- `ClassLayoutTest` covers compartments, classifiers, every marker family,
  labels, cardinalities, notes, namespaces, interactions, Markdown, structured
  unsupported errors, layout selection, title, and accessibility metadata.
- `ClassStressTest` covers Mermaid's relation matrix and 256 deterministic
  random legal diagrams under Dagre.
- `OfficialClassDocumentationTest` executes all 38 examples extracted from
  Mermaid's Class documentation.
- The Android gallery compares 27 identical sources against official Mermaid
  `12.0.0` Dagre output.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `classDiagram.jison`, `classDb.ts`, `classTypes.ts`,
   `classRenderer-v3-unified.ts`, `classBox.ts`, `shapeUtil.ts`, `markers.js`,
   `edges.js`, and `lineWithOffset.ts`.
3. Regenerate `ClassJisonTables.kt` and translate changed semantic actions,
   DB methods, shape methods, or marker behavior in the mapped Kotlin files.
4. Regenerate the 38 documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Install the Android sample and capture all 27 Native/Official Class pairs
   with Dagre.
7. Review the contact sheets and update this map and the compatibility matrix
   before publishing.
