# Mermaid.js 12.0.0 Wardley Map Source Map

## Upstream Lock

- Repository: `https://github.com/mermaid-js/mermaid`
- Tag: `mermaid@12.0.0`
- Commit: `98a0945418c76238f15df2afaddbba4272656c3b`
- Local reference: `build/upstream/mermaid-src`

## Translation Map

| Upstream Mermaid.js source | SHA-256 | Kotlin translation |
| --- | --- | --- |
| `packages/parser/src/language/wardley/wardley.langium` | `8c8ce9a0df6eefa14aa32a7c2c9c8515933ff67ed703b5400d5f31ea0bff11d5` | `WardleyParser.kt` grammar and statement parsing |
| `packages/parser/src/language/wardley/valueConverter.ts` | `155d78f3136add3a95eaef1a46365ef419c0040424cacded8d9c604f073ec557` | `WardleyParser.kt` quoted-name and coordinate conversion |
| `packages/mermaid/src/diagrams/wardley/wardleyBuilder.ts` | `abacba59af3a5b331dd66a8b21a6b8c5f797bc045b3bcfe909e9aa56d545b433` | `WardleyModel.kt` ordered state builder and ID resolution |
| `packages/mermaid/src/diagrams/wardley/wardleyParser.ts` | `05972b64593d2fb294a26166022c496bbe99eb534ae70baf8d0874e8dfc27a25` | `WardleyParser.kt` database population and percentage conversion |
| `packages/mermaid/src/diagrams/wardley/wardleyRenderer.ts` | `d2103480bcb5b59daa280e6eef2224513ec2e40d9dc485c61518578ac0dc2302` | `WardleyLayout.kt` projection, axes, components, links, pipelines, trends, annotations, and forces |
| `packages/mermaid/src/diagrams/wardley/wardleyTypes.ts` | `3aa25c5f35aae19dfa8d369ceb3bc262f649a5981fb5eff057d80bf94f08dbc2` | `WardleyModel.kt` semantic model |
| `packages/mermaid/src/diagrams/wardley/styles.ts` | `6b7d1b3167b2da267d3460d1f1c910e6f084de4cb2304daa4c5fe42c89d51ae9` | `WardleyLayout.kt` theme roles and stroke/fill behavior |
| `packages/mermaid/src/docs/syntax/wardley.md` | `86010a25b18ecc6b67184463bd81b6868575bb821a897a0d103bacdd01bc8ebe` | `OfficialWardleyDocumentationCases.kt` documentation fixtures |

## Kotlin Adaptations

- Langium parsing and the Mermaid database are translated into a traceable
  Kotlin parser and ordered model builder.
- SVG coordinate projection and drawing are emitted as SceneGraph primitives
  for Compose Canvas.
- Browser text measurement is replaced by the injected multiplatform
  `TextMetricProvider`.
- Mermaid.js `12.0.0` omits `wardley-beta` from `defaultConfig.configKeys`, so
  source frontmatter/directive layout settings are discarded by
  `sanitizeDirective`; CMP Mermaid preserves that behavior while exposing the
  equivalent caller configuration through `MermaidWardleyOptions`.
- Parser, configuration, resource-limit, and layout failures return
  `GMResult.Err`; the public engine retains its final unexpected-error boundary.

## Verification Map

- Parser and builder behavior: `WardleyParserTest.kt`
- Layout and scene semantics: `WardleyLayoutTest.kt`
- Caller configuration, source sanitizer parity, and theme variables:
  `WardleyConfigTest.kt`
- Official documentation fixtures: `OfficialWardleyDocumentationTest.kt`
- Production, determinism, themes, and 256-case Native matrix:
  `ProductionCorpusTest.kt`
