# Upstream Treemap Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| d3-hierarchy | `3.1.2` |
| d3-format | `3.1.2` |
| Native renderer | Compose Multiplatform Canvas |

The production Treemap path is pure Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `treemap/TreemapPlugin.kt` | `treemap/detector.ts` and `diagram.ts` | `treemap`/`treemap-beta` detection and parser/layout orchestration |
| `treemap/upstream/mermaid/TreemapParser.kt` | `treemap.langium`, `valueConverter.ts`, and `treemap/parser.ts` `populate` | Quoted sections and leaves, colon/comma values, indentation, class selectors, class definitions, common metadata, and structured parse failures |
| `treemap/upstream/mermaid/TreemapTypes.kt` | `treemap/types.ts` and `utils.ts` `buildHierarchy` | Flat parser items, logical hierarchy construction, synthetic root, node values, and class styles |
| `treemap/upstream/mermaid/TreemapDb.kt` | `treemap/db.ts` `TreeMapDB` | Node insertion, logical levels, class styles, title, accessibility metadata, and hierarchy projection |

## D3 Translation

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `treemap/upstream/d3/D3TreemapLayout.kt` | `d3-hierarchy/src/hierarchy/{index,sum,sort,eachBefore,eachAfter}.js` | Hierarchy nodes, bottom-up sums, descending value sort, depth/height, and traversal order |
| `treemap/upstream/d3/D3TreemapLayout.kt` | `d3-hierarchy/src/treemap/{index,squarify,dice,slice,round}.js` | Squarify tiling, golden-ratio row scoring, inner and section padding, coordinate rounding, and source paint order |
| `treemap/upstream/d3/D3NumberFormat.kt` | `d3-format/src/{formatSpecifier,locale,formatTypes,formatGroup,formatTrim}.js` | D3 specifier parsing, signs, symbols, grouping, precision, fixed/significant/exponential/percentage formats, alignment, and trimming |

## Rendering And Shared Integration

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `treemap/TreemapLayout.kt` | `treemap/renderer.ts` `draw` | Synthetic-root layout, branch/leaf paint order, ordinal theme colors, labels, values, title, metadata, viewport, and sizing |
| `treemap/TreemapLayout.kt` | `treemap/renderer.ts` `renderNode`, `renderLeaf`, `getNodeDimensions`, and adaptive label logic | Branch labels and values, leaf font reduction, visibility, centered labels, and class style precedence |
| `treemap/TreemapLayout.kt` | `treemap/styles.ts` | Branch/leaf opacity, strokes, title, labels, values, and italic section values |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and `config.schema.yaml` | Typed Treemap defaults, frontmatter/init overrides, merge precedence, and validation |
| `MermaidContract.kt` | Mermaid Treemap configuration | Public typed Treemap options |
| `SceneGraph.kt` | Mermaid SVG text styles | Platform-independent italic text state |
| `mermaid-compose/.../MermaidDiagram.kt` | SVG `font-style: italic` | Compose Canvas `FontStyle.Italic` painting |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `treemap.langium` | `09ae0dfbd0cb7747a6c161ada9a529b657c5181aa5e98dc5501b4b6a33d7f909` |
| `valueConverter.ts` | `64df0fac013b1d8d430d50e394f363f7c35affb8b3ee4df887d4788091ca6303` |
| `treemap/parser.ts` | `d8847aef685627cd7749ea29cf50ffcb97142cc61e5891e44e0d0d33e58e0094` |
| `treemap/db.ts` | `8d0da11bb1e01047fddd816d425e20952be7846d76e043b5757b7fea6df2d76f` |
| `treemap/renderer.ts` | `c8d1d9d9a1bd6c8562879d05a340c242e0d9a9ce131019347aac71d1085ce6e8` |
| `treemap/styles.ts` | `3add98ac499294d04dbbc2a3fc3cbf6007d607a2be1b280613530e7b2030c4f9` |
| `treemap/types.ts` | `6a2f6e11e2341291ac57f23c72001b856cf8e7649eecd615cb968bbfa8398b8f` |
| `treemap/utils.ts` | `22d74cd0ae805b94dd222e100cc5811aed2753b270e522640d486974a8ca583d` |
| `treemap/detector.ts` | `b415787e79d99860bfc356eb98c900a103398a5a4d1d7672bbb4d5275241637c` |
| `treemap/diagram.ts` | `5fe728fd2a53df4d889d2d475cd4f1fdccd64764ed2805e32183539374a29503` |
| `treemap.md` | `a32940b952d87164dc18ce9d8ea679e94f4c0de0bf44a5669b40033170c70ce1` |
| `defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `config.schema.yaml` | `5b59ca5612d5ebfbf1ef9277601224a734c831e16f3d1654ae74481dea4b26b0` |
| `d3-hierarchy/src/hierarchy/index.js` | `63bed36dcc1d845ae750b0d377f817d97228e1313d577b52a2753100d781265b` |
| `d3-hierarchy/src/treemap/index.js` | `28b566cdfbf6e87a04eb6ed15d83df2e875544e75ebd904a8275d88922545f8f` |
| `d3-hierarchy/src/treemap/squarify.js` | `b010f697d376eca4ec2e5d4b75f2a079a124df404221a0a2b555d5633c463372` |
| `d3-hierarchy/src/treemap/dice.js` | `d578a63ee720269359cf42a4118d82ed9ce91404b3c7c59081dbba184a81086f` |
| `d3-hierarchy/src/treemap/slice.js` | `832538a72abf4338461bbc6db69759f443225401d0a309d2cacc0a354fe5aaec` |
| `d3-hierarchy/src/treemap/round.js` | `64435a28e03cb13ad999383d78b09c29510b5bcfaadf54d6dd6bea95da800167` |
| `d3-hierarchy/LICENSE` | `e008c5e25a6be382593089c29bfabbc553c6378eee02895aec46ce396cc404ee` |
| `d3-format/src/defaultLocale.js` | `3612cd8bd854f7fdeeb351d89a0a970c46e4d3936191e752b2b7cae4d4572b52` |
| `d3-format/src/locale.js` | `908f14df6119f8da3517d30a409440e3a2ab97fb5d4f8fc6faa07e80407b2dc2` |
| `d3-format/src/formatSpecifier.js` | `ef7eebb08d4b9ea7c2db8c27046b13bbc20f8c3eba993574d943932d872a565d` |
| `d3-format/src/formatTypes.js` | `4a6c01b84235fd84028b7b71059f4f5f07c19642ec3e8467281660b9db728f3e` |
| `d3-format/LICENSE` | `2a6d2d5f32ba0b755ddbc1c833f766e30cdbe6ebe9a6d4e3e24427721f0b63d3` |

## Intentional Kotlin Adaptations

- The Langium grammar is expressed as a deterministic Kotlin parser because
  production modules cannot execute generated JavaScript. Parse, validation,
  and layout failures return `GMResult.Err`.
- Source indentation is converted to the same logical parent hierarchy as
  `buildHierarchy`; database levels record recursive logical depth rather than
  raw whitespace width.
- The generic D3 APIs are narrowed to Mermaid's exact invocation: hierarchy
  sum, descending value sort, squarify, configured inner padding, fixed section
  padding, a `1000 x 600` layout extent, and rounded coordinates.
- Documented Mermaid currency aliases are normalized before invoking the
  translated D3 formatter. Invalid format strings retain Mermaid's comma
  fallback.
- DOM text measurement is supplied by `TextMetricProvider`. Leaf labels follow
  Mermaid's adaptive font reduction and visibility rules before entering the
  SceneGraph.
- Compose has no SVG `getBBox()`, so the viewport is normalized to actual
  painted bounds and then applies `diagramPadding`.

## Parity Gate

- Parser, database, exact D3 coordinate, number-format, configuration,
  class-style, adaptive-label, metadata, and structured-error tests pass.
- All eight active examples extracted from Mermaid's Treemap documentation
  render in the JVM test.
- A separate deterministic randomized test renders 256 Treemap inputs twice
  and verifies finite, stable SceneGraphs.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.

## Upgrade Procedure

1. Update the Mermaid, d3-hierarchy, and d3-format versions, commits, hashes,
   and third-party notices.
2. Diff every mapped parser, database, hierarchy, tiling, format, renderer,
   style, detector, registration, configuration, and documentation file.
3. Translate only changed behavior in the corresponding Kotlin method.
4. Regenerate documentation and stability fixtures.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Treemap Native/Official pairs.
7. Update this map, the compatibility matrix, and the public stability report.
