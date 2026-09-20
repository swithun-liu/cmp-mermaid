# Upstream Event Modeling Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Event Modeling path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `eventmodeling/EventModelingPlugin.kt` | `eventmodeling/detector.ts`, `diagram.ts`, and `parser.ts` | Header detection, parser/database lifecycle, and renderer dispatch |
| `eventmodeling/upstream/mermaid/EventModelingParser.kt` | `event-modeling.langium`, `tokenBuilder.ts`, and `event-modeling-validator.ts` | Portable grammar parsing, metadata, declarations, references, source-type validation, and structured parse failures |
| `eventmodeling/upstream/mermaid/EventModelingTypes.kt` | `event-modeling.langium` and `eventmodeling/types.ts` | AST, frame, payload, swimlane, card, relation, and layout-state contracts |
| `eventmodeling/upstream/mermaid/EventModelingDb.kt` | `eventmodeling/db.ts` | Text preparation, frame/relation decide-evolve flow, namespace lanes, source-order positioning, colors, and resource limits |
| `eventmodeling/EventModelingLayout.kt` | `eventmodeling/renderer.ts` | Swimlane, card, relation, arrowhead, padding, and viewport rendering |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and `config.type.ts` | Event Modeling defaults, frontmatter/init overrides, merge precedence, and typed configuration validation |
| `MermaidContract.kt` | Mermaid Event Modeling configuration and theme types | Public typed options and Event Modeling theme values |
| `SceneGraph.kt` and `MermaidDiagram.kt` | `eventmodeling/renderer.ts -> renderD3Relation` | Independent relation-stroke and arrowhead colors in the portable scene and Compose marker painter |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/parser/src/language/eventmodeling/event-modeling.langium` | `9432c3c986e2d2135fbdedbc5b4cf7fe22b9c893e10b4d23cc63332f776bbba4` |
| `packages/parser/src/language/eventmodeling/event-modeling-validator.ts` | `d431b824b7ee2c90ca0fb906b87e2fcc687c63e1477092774c2c7406ba160cab` |
| `packages/parser/src/language/eventmodeling/tokenBuilder.ts` | `9b24da716069629eb75df652789eaaf2b5a236dffa7a0f483405596c5d4515f3` |
| `packages/mermaid/src/diagrams/eventmodeling/types.ts` | `cc272f190c54b651cc4ac0680b1e323966de873ee98cee11e77580467406cfaf` |
| `packages/mermaid/src/diagrams/eventmodeling/db.ts` | `352bbff310519835cb218b17dc366e602128e27666a95f2618a5ac57e31852af` |
| `packages/mermaid/src/diagrams/eventmodeling/parser.ts` | `246b09a223531795f347a6957ba072ba5f423b2c8186c31ef908c0b27f2b68cf` |
| `packages/mermaid/src/diagrams/eventmodeling/renderer.ts` | `651e0f70497d303f659f7838480f120554f5c3d24f8fa6eb65066eddd97d273f` |
| `packages/mermaid/src/diagrams/eventmodeling/diagram.ts` | `2a307917074283263b4daae56e2b39d2436d2801cd032dfe19a4e0d606533356` |
| `packages/mermaid/src/diagrams/eventmodeling/detector.ts` | `1c48e6ce97a57e8dbe76be1b9f3ece575774c4dc840956736509076795af12ff` |
| `packages/mermaid/src/diagrams/eventmodeling/styles.js` | `2c517dd00c0efcb4381d2a86c4c9903c89a0898e64b09cae53d5944ed1c3a43a` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/eventmodeling.md` | `554b655daac92e8162849f428c038e26cb482d7d859297028ba986cfd0159ee5` |

## Intentional Kotlin Adaptations

- The Langium grammar is translated into a portable cursor parser. Parse,
  validation, configuration, layout, and resource-limit failures return
  `GMResult.Err`.
- Mermaid's parser resolves frame, data, and model-entity references through
  Langium services. Kotlin preserves the same typed validation at its parser
  boundary without a JavaScript runtime.
- Langium's hidden whitespace and comment terminals remain valid at every
  grammar-token boundary, including qualified names, data types, blocks,
  frames, and declarations; Kotlin does not impose line boundaries absent
  from the upstream grammar.
- Data-block opening and closing boundaries preserve the `EM_DATA_BLOCK`
  terminal: the opening brace is followed by optional horizontal whitespace
  and a newline, while the closing brace starts a line and is followed by
  whitespace or end-of-input.
- Mermaid `12.0.0` stores model entities, notes, and GWT declarations in the
  AST but does not render them. Kotlin preserves and validates them without
  inventing visible output.
- Mermaid's grammar accepts quoted inline data, while its renderer assumes
  brace-delimited payloads. Kotlin reports quoted payload rendering as
  unsupported rather than copying the upstream malformed substring behavior.
- Brace-delimited inline data preserves `db.ts`' end-exclusive
  `lastIndexOf('}') - 1` extraction, including its compact `{a:1}` truncation.
- Referenced data blocks preserve the paired upstream substring operations,
  including blocks whose opening brace is followed by horizontal whitespace.
- `rowHeight` is part of Mermaid's public Event Modeling configuration but is
  not consumed by the `12.0.0` renderer. Kotlin validates and preserves the
  value without silently changing layout.
- Browser `foreignObject` HTML becomes ordered SceneGraph text. A card payload
  is represented as a centered bold name and a left-aligned monospace payload;
  this causes the documented manifest-only `text-segmentation` review while
  preserving the same visible text and geometry.
- The upstream relation stroke and arrowhead can use different theme colors.
  Kotlin therefore adds optional `arrowColor` metadata to `ScenePath`; all
  existing paths retain their prior behavior when it is absent.
- SVG DOM nodes, paths, markers, groups, and text become ordered SceneGraph
  elements. Browser text measurement is replaced by `TextMetricProvider`.

## Parity Gate

- All 12 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused parser/database/layout tests cover frame aliases, declarations,
  data, notes, GWT, namespaces, validation, reset behavior, paint order,
  configuration, and typed failures.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry passes `13/13`; detail is
  `7 pass / 6 review / 0 fail`.
- Matrix geometry passes `256/256`; detail is
  `0 pass / 256 review / 0 fail`.
- Every review has only `text-segmentation` from the documented
  `foreignObject` to SceneGraph text adaptation. The production contact sheet
  and all 16 matrix contact sheets were manually reviewed with no unresolved
  swimlane, card, relation, marker, color, text, clipping, overlap, or
  paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures from the new source tree.
3. Diff every mapped grammar, token, validator, type, database, parser,
   renderer, style, detector, configuration, and documentation file.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Event Modeling Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
