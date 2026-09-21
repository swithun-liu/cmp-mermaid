# Upstream Railroad Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Railroad path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `railroad/RailroadPlugin.kt` | `railroadDetector.ts`, `ebnfDetector.ts`, `abnfDetector.ts`, `pegDetector.ts`, and the four diagram modules | Header detection, parser lifecycle, layout dispatch, and typed failures |
| `railroad/upstream/mermaid/RailroadParser.kt` | Four `packages/parser/src/language/railroad*` Langium grammars and token/value converters | Tokens, comments, metadata, rule boundaries, escaped strings, identifiers, source positions, and parser errors |
| `railroad/upstream/mermaid/RailroadParser.kt -> parseIrExpression` | `parser/railroadParser.ts -> transformExpression` | Explicit terminal, non-terminal, special, sequence, choice, optional, and repetition constructors |
| `railroad/upstream/mermaid/RailroadParser.kt -> parseEbnfChoice` through `parseEbnfSpecial` | `parser/ebnfParser.ts` | EBNF alternation, sequences, groups, postfixes, ISO forms, special sequences, and exception lowering |
| `railroad/upstream/mermaid/RailroadParser.kt -> parseAbnfAlternation` through `parseAbnfNumericValue` | `parser/abnfParser.ts` | ABNF alternatives, concatenation, exact and bounded repetition, optionals, numeric values, terminals, and rule names |
| `railroad/upstream/mermaid/RailroadParser.kt -> parsePegOrderedChoice` through `parsePegPrimary` | `parser/pegParser.ts` | PEG ordered choices, sequences, prefixes, suffixes, predicates, literals, identifiers, groups, and any-character matching |
| `railroad/upstream/mermaid/RailroadDb.kt` | `railroadDb.ts` | Rule ordering, duplicate-name lookup, recursive sanitization, title, and accessibility state |
| `railroad/upstream/mermaid/RailroadTypes.kt` | `railroadTypes.ts` | Shared AST nodes, rule model, notation headers, and renderer configuration contract |
| `railroad/RailroadLayout.kt -> renderExpression`, `renderSequence`, `renderChoice`, `renderOptional`, and `renderRepetition` | `railroadRenderer.ts` | Symbol measurement, horizontal sequences, vertically routed alternatives, bypasses, loops, separators, and dimensions |
| `railroad/RailroadLayout.kt -> PathBuilder` and SceneGraph assembly | `railroadRenderer.ts -> PathBuilder`, symbol renderers, and `draw` | Arc and line paths, markers, rule labels, viewport sizing, and paint order |
| `railroad/RailroadLayout.kt -> RailroadStyle.resolve` and `MermaidPreprocessor.kt` | `styles.ts`, `railroadTypes.ts -> DEFAULT_RAILROAD_CONFIG`, `defaultConfig.ts`, and `config.type.ts` | Theme defaults, scoped overrides, typed validation, colors, typography, spacing, markers, and responsive sizing |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/parser/src/language/railroad/railroad.langium` | `6a9a9a1793b98c3dbc04d922867681a1663cd1d14d70f29098fb3dda018f625e` |
| `packages/parser/src/language/railroad/tokenBuilder.ts` | `48ac35fd9b17f7a7170b9b92cb8f9fddbf5a0dd9e423ee13680cea4ad7157189` |
| `packages/parser/src/language/railroad/valueConverter.ts` | `5c7c6453aa62f6692614d87aee20cfabeb0418326faed24ae72d1be77edeef6e` |
| `packages/parser/src/language/railroad-ebnf/railroad-ebnf.langium` | `9cf1a6d19bbe0452a79f87480775e418dbff6664eb95cc32d270c6acdd02f221` |
| `packages/parser/src/language/railroad-ebnf/tokenBuilder.ts` | `3385cf0b22fd97af81c4376194bbe1c9034a0274a8312da8d0e278009a61e19c` |
| `packages/parser/src/language/railroad-ebnf/valueConverter.ts` | `1047d625be31a7dd1eb4cd8f42b4df8ab0b02303fbf82c3d81876d9d243d2d4d` |
| `packages/parser/src/language/railroad-abnf/railroad-abnf.langium` | `2b4a3993ba3df8e43e7719437f40ba5c36d03a229f671441bcb2771136017b41` |
| `packages/parser/src/language/railroad-abnf/tokenBuilder.ts` | `c37c76a6ae8f8f6a64ce3d1920dcdab5aa663d971b92739fb75e2ded9cd5e38a` |
| `packages/parser/src/language/railroad-abnf/valueConverter.ts` | `c66263d2c97fbc12d93d91b98ad221158cc1c4f8676c291077cbc7f4890a6e3b` |
| `packages/parser/src/language/railroad-peg/railroad-peg.langium` | `90494f697158eddd41b2a60c42cb96a40b5f9d665392bcc4efe489b529e178cb` |
| `packages/parser/src/language/railroad-peg/tokenBuilder.ts` | `982324781222664862199f9598e57eb8630c9d953bb1a726faade0989098aa98` |
| `packages/parser/src/language/railroad-peg/valueConverter.ts` | `f7edc75c5e4c34919f1dd5e1b8f5e45d74754c278535f2ba352f81e545b434ab` |
| `packages/mermaid/src/diagrams/railroad/parser/railroadParser.ts` | `2e55bfeb1c541fc5c2b30bac3b55f79a0f465b81aa4af8fd85d2043f87ab8599` |
| `packages/mermaid/src/diagrams/railroad/parser/ebnfParser.ts` | `2c05ac63eda4ebec9ddb93cd3a4bf1136494b5ca22b4be8bbe911c9b581a5033` |
| `packages/mermaid/src/diagrams/railroad/parser/abnfParser.ts` | `28f795c47be9d9e0e2c61fd3a96c3710d1639013b2f66725ebbeb7ce8c29fcaf` |
| `packages/mermaid/src/diagrams/railroad/parser/pegParser.ts` | `405354273b866c889495d72c6be660f33b52de89acf58ae8026ee9826023769f` |
| `packages/mermaid/src/diagrams/railroad/railroadDetector.ts` | `780ed885480359e2f03d4d36e5ecdf715e63c1199fe47569520cc95434ddb91a` |
| `packages/mermaid/src/diagrams/railroad/ebnfDetector.ts` | `cbc14a69184d7eb5341ae41b30a45201996f04d665a9f884d8309252387b52b0` |
| `packages/mermaid/src/diagrams/railroad/abnfDetector.ts` | `df85b7ee0403bc41077be7fea0126e9eec01aa49d94c071aa91a4ad43d11ed3b` |
| `packages/mermaid/src/diagrams/railroad/pegDetector.ts` | `ed1908e8f6ca613a5470330aaa9793650552affa5d0ee5c733ec2644d6a7acf0` |
| `packages/mermaid/src/diagrams/railroad/railroadDiagram.ts` | `a98ff4fc37760c1af5d2be0b3dad96b9041dea4aba48255c025d11d4c798edeb` |
| `packages/mermaid/src/diagrams/railroad/ebnfDiagram.ts` | `48612dd35b40a3d4c5a50ebbde78a80fa0a69ab3673ba7a14057b216464dc531` |
| `packages/mermaid/src/diagrams/railroad/abnfDiagram.ts` | `15c518fb238b0a3c51a6995b1ba0e9d77c0efd6e66ba1e2bbc01dc4630d5d42d` |
| `packages/mermaid/src/diagrams/railroad/pegDiagram.ts` | `bb5aa491bcb334c8a3505d5771e7ace676b00f3a7689313fad0819ebd04cde54` |
| `packages/mermaid/src/diagrams/railroad/railroadDb.ts` | `e3be13100d4e7b996def16b4e41eb9f5676059f287e7ce33aa5a168dc40eb307` |
| `packages/mermaid/src/diagrams/railroad/railroadRenderer.ts` | `af7e6ab81ed962f8dd3e170e5e98a4bb5682ed2aeb2f87c2856405fe38b82c11` |
| `packages/mermaid/src/diagrams/railroad/railroadTypes.ts` | `aeec31ffd2e50c7ca726c0a03516ad9c0a5dc49a72ad8580f104fae5819bb00e` |
| `packages/mermaid/src/diagrams/railroad/styles.ts` | `67e6d23275153453310099d9b1444208dfcfcd03f282a8fd741eae3d0af3b321` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/railroad.md` | `d496f3f2d473898424f0cb86c617aacc463dfb9c4ef7a06c420890f9019d8349` |

## Intentional Kotlin Adaptations

- The four Langium-generated TypeScript parsers are represented by one
  portable notation-aware Kotlin parser. It preserves token boundaries, AST
  lowering, comments, metadata, and source locations while returning
  `GMResult.Err` for expected failures.
- JavaScript `Infinity` repetition maxima are represented as nullable Kotlin
  maxima. `null` means unbounded; finite ABNF bounds retain their exact values.
- PEG lookahead operators are rendered as the same special-node labels emitted
  by upstream. They describe grammar predicates and do not execute recognition.
- DOM and SVG text measurement are replaced by `TextMetricProvider`. SVG
  groups, paths, rectangles, labels, markers, and paint order become ordered
  SceneGraph elements rendered by Compose Canvas.
- Pure geometry retains upstream floating-point semantics; parser,
  configuration, database, and resource failures remain typed.

## Parity Gate

- All 14 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused tests cover all four notations, shared AST nodes, notation-specific
  constructs, metadata, configuration, malformed input, limits, and
  deterministic rendering.
- The replacement Web gate compares 18 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry and detail pass `18/18`.
- Matrix geometry and detail pass `256/256`, with
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved rule, symbol, path, label, clipping, overlap, or
  paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff all four grammars, token/value converters, parser adapters, detectors,
   diagram modules, database, types, renderer, styles, configuration, and
   documentation files.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Railroad Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
