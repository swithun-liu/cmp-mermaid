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
| `MermaidContract.kt` | `themes/theme-{default,dark,forest,neutral,base}.js` | Exact `rowOdd`/`rowEven` constructor, `updateColors()`, and `calculate()` order, including theme-specific base variables and explicit overrides |
| `MermaidContract.kt` | `themes/theme-{neo,neo-dark,redux,redux-color,redux-dark,redux-dark-color}.js` | Exact missing `rowOdd`/`rowEven` values retained as nullable fields |
| `MermaidColorMath.kt` | Khroma `adjustChannel`, `Color.stringify`, `lang.round` | HSL adjustment, 10-decimal CSS serialization, and RGB conversion used by derived theme colors |
| `erdiagram/ErLayout.kt` | `erRenderer-unified.ts` and `styles.ts` | Theme colors, identifying and non-identifying lines, subgraph paint order, and ER configuration |
| `erdiagram/ErLayout.kt` | `utils/subGraphTitleMargins.ts -> getSubGraphTitleMargins`, `rendering-elements/clusters.js -> insertCluster` | Configured subgraph title margins, default zero margin, label placement, and cluster frame geometry |
| `flowchart/upstream/mermaid/MermaidTextPort.kt` | `createText.ts` and `handle-markdown-text.ts` | Shared Mermaid/Marked Markdown, HTML spans, sanitization, and structured unsupported detection |
| `flowchart/FlowDagreLayout.kt` | `rendering-util/layout-algorithms/dagre/index.js` `measureDagreGraph()` | Default entity and subgraph placement, recursive parent `nodesep` and `ranksep + 25` propagation, edge routing, and distinct routing for repeated self-relationships |
| `flowchart/FlowElkLayout.kt` | Mermaid's unified ELK path | Retained mapping boundary that returns structured `UnsupportedFeature` |
| `flowchart/upstream/mermaid/MermaidEdgePathPort.kt` | `rendering-elements/edges.js` | Basis curves, endpoint correction, self-relationships, and relationship label anchors |
| `flowchart/upstream/mermaid/MermaidMarkerPort.kt` | `rendering-elements/markers.js` | Marker offset behavior, including ER markers anchored at entity boundaries |
| `SceneGraph.kt` | Mermaid ER relationship marker semantics | Typed crow-foot markers and marker background color |
| `mermaid-compose/.../MermaidDiagram.kt` | `markers.js` and `erMarkers.js` | Native bars, circles, quadratic crow-foot paths, orientation, and Canvas painting |

## Production Renderer Function Checklist

| Order | Upstream production stage | Kotlin stage | Status |
| ---: | --- | --- | --- |
| 1 | `erRenderer-unified.ts -> draw` configuration, `getData()`, layout selection, marker registration, render, title, and viewport | `ErLayout.layout`, `buildScene` | **Platform adaptation**: SVG setup and viewport fitting map to typed SceneGraph bounds while retaining the upstream data and stage order |
| 2 | `erDb.ts -> getData` entity, subgraph, relationship, class, direction, and marker projection | `ErDb`, `ErLayout.buildLayoutDocument` | **Exact / Platform adaptation** |
| 3 | `render.ts -> render`, `createGraph.ts -> createGraphWithElements`, Dagre `measureDagreGraph` | `FlowDagreLayout.layout` and ER SceneGraph assembly | **Platform adaptation**: translated Dagre placement is shared with the unified flow renderer |
| 4 | `subGraphTitleMargins.ts -> getSubGraphTitleMargins`, `clusters.js -> insertCluster` title offsets and frame geometry | `MermaidRenderOptions.subGraphTitle*Margin`, `FlowDagreLayout.applySubgraphTitleMargins`, `ErLayout.addSubGraphs` | **Exact / Platform adaptation**: default margins remain zero, configured margins retain upstream geometry, and SVG label transforms map to SceneText bounds |
| 5 | `erBox.ts -> erBox` entity without attributes through `drawRect` | `measureEntity`, `addEntity` | **Exact / Platform adaptation** |
| 6 | `erBox.ts -> erBox`, `addText`, `lineToPolygon` attribute table sizing, rows, dividers, and text placement | `measureEntity`, `entityGridGeometry`, `addEntity` | **Exact / Platform adaptation**: the SVG-label branch scales both `PADDING` and `TEXT_PADDING` by `1.25`; Canvas text bounds retain the Compose horizontal inset explicitly |
| 7 | `theme-*.js -> constructor`, `updateColors`, `calculate`; `erBox.ts -> erBox` row paint | `MermaidTheme.preset`, `withVariables`, `deriveErRowColors`, `ErLayout.addEntity` | **Exact / Platform adaptation**: classic themes retain their distinct formulas and calculation order; missing Neo/Redux fills map to transparent Canvas row overlays |
| 8 | `styles.ts -> getStyles` entity palette, relationship-label background, and `clusterBkg ?? mainBkg`, `clusterBorder ?? nodeBorder`, `titleColor ?? textColor` fallbacks | `MermaidTheme`, `ErLayout.addEntity`, `addEdges`, `addSubGraphs` | **Exact / Platform adaptation** |
| 9 | `edges.js`, `markers.js`, `erMarkers.js` relationship curves, endpoint correction, and crow-foot markers | `MermaidEdgePathPort`, `MermaidMarkerPort`, `MermaidDiagram` | **Platform adaptation** |

## ER Row Theme Function Checklist

| Upstream source | Production stage | Status |
| --- | --- | --- |
| `theme-default.js -> constructor`, `updateColors`, `calculate` | Clear the `calculated` placeholders, then derive rows from `primaryColor`; restore explicit row overrides last | **Exact** |
| `theme-dark.js -> constructor`, `calculate` | Derive rows once from the constructor's `mainBkg`; later `mainBkg` overrides do not recompute them | **Exact** |
| `theme-forest.js -> updateColors`, `calculate` | Derive rows from the resolved `mainBkg`; restore explicit row overrides last | **Exact** |
| `theme-neutral.js -> constructor`, `calculate` | Derive the odd row and pin the even row in the constructor; later `mainBkg` overrides do not recompute them | **Exact** |
| `theme-base.js -> updateColors`, `calculate` | Derive rows from resolved `mainBkg`, selecting lighten or darken formulas with `darkMode`; restore explicit row overrides last | **Exact** |
| `theme-{neo,neo-dark,redux,redux-color,redux-dark,redux-dark-color}.js -> calculate` | Leave `rowOdd` and `rowEven` undefined unless explicitly supplied | **Exact** |
| Khroma `adjustChannel`, `Color.stringify`, `lang.round` | Serialize adjusted HSL channels to 10 decimal places before converting the CSS color to SceneColor | **Exact** |
| `erBox.ts -> erBox` | Read resolved `rowOdd`/`rowEven` and paint alternating attribute rows | **Platform adaptation**: an undefined SVG/Rough.js fill maps to a transparent SceneShape overlay |

Rows 5-6 intentionally represent one native shape or geometry path for an
upstream rough-svg group that can contain multiple SVG paths even when
`roughness=0`. Raw SVG child count is therefore not a semantic rendering
contract; bounds, paint, dividers, and topology remain the required parity
signals.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `erDiagram.jison` | `4f7c96b05d15bcc9c0e753bb106a5b8a67cfa4c8b2e13faab1d7098b92f9e5be` |
| `erTypes.ts` | `645a96b7717f27582f5623da5a12cb9334ce0c41966169633249e18c935746ca` |
| `erDb.ts` | `c048254a2fe1f3a294f41b901d8b9f73d0f6bbb1b999e8c9f82f7ba5b736aa69` |
| `erRenderer-unified.ts` | `e41c45a44665ca346aa22e9e427e584932edacdaaa8e9f0265dd8d7275a81371` |
| `erBox.ts` | `661b2d73c63686bd3c2f1407c1319f11d47481bb8f9a5b96874db7eaf29b0520` |
| `subGraphTitleMargins.ts` | `245ada172ff8d0c231822a585f47d6850e898de690bd0003aaa6c8eadcc17aa0` |
| `clusters.js` | `4a73517c6b85450b99cfe1bb6a6d0478055751d1e15c7feb6ed159fadd8fc733` |
| `markers.js` | `e495d9d84cdf51ee2ead29e3656f98554e7a3a57d20a2873884a32522c4d8f35` |
| `erMarkers.js` | `afcae8780d19f8a0465fbbd785bd909e875cc5df7b7354432f006e3a02d509e4` |
| `styles.ts` | `8a090f8401285cf5256d6c113fa5c2cc9d206e6940f33bb7601da044a8c932c4` |
| `theme-default.js` | `bd25bdb136ffa3030c4eddf211643d6f24049c7aef0eced66002732506fb4aa7` |
| `theme-dark.js` | `fd03ff4a32d0f3eda6ac47e7f2a27027d8cc30e8581ec11f4c55e15ba1e54d9f` |
| `theme-forest.js` | `948244e4805c146c21c42db669119e3af7690fa7d74c5be61d39a7076eff5364` |
| `theme-neutral.js` | `524e8b79ba225fc585b1552fbf563709f3f4f932e509b0125c706b4043f4a882` |
| `theme-base.js` | `dee4c92a557572d4857a5b188fff50e77ca336259101961ec89195dc83d1fa40` |
| `theme-neo.js` | `3e7c44e329ccef9c15e12d8606ccdac8196bc6f34f739d5617d14e318ff32e78` |
| `theme-neo-dark.js` | `62397b9e875f5383f5fe437a1fb803398d9c6f38f1df7c809d50c1322da85ef5` |
| `theme-redux.js` | `86bedbf4126091c17ca9bfabcdd89a5a5625fcbbe5be0048a9f66bfaf7363de5` |
| `theme-redux-color.js` | `54108c0e2d02eb55dca3046f0dd55c6eca8c79c63ce7bf373a364c6846650698` |
| `theme-redux-dark.js` | `77b617007349cfa9e209c696db408e694e65f3a3f2a79571c14011b95b201770` |
| `theme-redux-dark-color.js` | `14ad5214a0e57244dd452fc53d9ccfbd8b7d2f27399e816fe69ef3034985bee7` |
| `rendering-util/layout-algorithms/dagre/index.js` | `9981c5e6418078fc81fa5b2c8accd2e954827ae683c6adfeba81c521126c7337` |
| `entityRelationshipDiagram.md` | `0be2f34ff578b2d86d0429ce288e8681ef46a27d645841a5162c78d501e5441b` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- Undefined Neo/Redux ER row fills map to transparent SceneShape overlays so
  the entity or palette fill remains visible beneath them.
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
- Extracted Dagre subgraphs inherit the parent graph's `nodesep` and
  `ranksep + 25` immediately before recursive layout, matching
  `measureDagreGraph()`.
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
- The large-scale Web audit compares all 256 coverage-driven same-source input
  cases against Mermaid.js `12.0.0`, including long aliases and unbroken entity identifiers,
  and enforces content-bound and foreground-density limits.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `erDiagram.jison`, `erTypes.ts`, `erDb.ts`,
   `erRenderer-unified.ts`, `erBox.ts`, `markers.js`, `erMarkers.js`, and
   `styles.ts`, plus every `theme-*.js` file listed above.
3. Regenerate `ErJisonTables.kt` and translate changed semantic actions, DB
   behavior, data projection, entity geometry, marker methods, or styling in
   the mapped Kotlin files.
4. Regenerate the 24 documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Capture all 256 Native/Official ER matrix pairs with Dagre.
7. Review the contact sheets and update this map and the compatibility matrix
   before publishing.
