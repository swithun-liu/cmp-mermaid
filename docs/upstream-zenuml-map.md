# Mermaid.js 12.0.0 ZenUML Source Map

## Upstream Lock

- Mermaid repository: `https://github.com/mermaid-js/mermaid`
- Mermaid tag: `mermaid@12.0.0`
- Mermaid commit: `98a0945418c76238f15df2afaddbba4272656c3b`
- External plugin: `@mermaid-js/mermaid-zenuml@1.0.0`
- Plugin core resolved by the Mermaid `12.0.0` lockfile:
  `@zenuml/core@3.49.2`
- Local references: `build/upstream/mermaid-src` and the ignored
  `build/upstream/zenuml-core-3.49.2`

## Translation Map

| Upstream source | SHA-256 | Kotlin translation |
| --- | --- | --- |
| `packages/mermaid-zenuml/src/detector.ts` | `3ba1407a7c6f00ed82f281b199ac4365d2d004d2d166b5712b31848020a5082e` | `ZenUmlPlugin.kt` header detection |
| `packages/mermaid-zenuml/src/parser.ts` | `77d635f0bb4749290daaba8ed7bcfa4443d4ffc27052c24ba0ee10a89732eab2` | `ZenUmlParser.kt` adapter semantics |
| `packages/mermaid-zenuml/src/zenuml-definition.ts` | `5baf2e107c063dcb7f2aafb6f73aef3b29289dcaeafa97c08018af416130d7f0` | `ZenUmlPlugin.kt` plugin registration |
| `packages/mermaid-zenuml/src/zenumlRenderer.ts` | `3e106a7cc6501176d3465bba27f97ef3b4ab35e909dff40d37ec69ddc48c2320` | `ZenUmlGeometry.kt` and `ZenUmlLayout.kt` scene construction |
| `packages/mermaid-zenuml/src/types/zenuml-core.d.ts` | `aacc5f39b91740fab1bc7128f9e6f86b49726452d27876d009099d2686539064` | `ZenUmlModel.kt` semantic model |
| `@zenuml/core@3.49.2` `src/g4/sequenceLexer.g4` and `sequenceParser.g4` | Pinned by Mermaid's `pnpm-lock.yaml` | `ZenUmlParser.kt` grammar and statement model |
| `@zenuml/core@3.49.2` `src/positioning/Coordinates.ts` and `david/DavidEisenstat.ts` | Pinned by Mermaid's `pnpm-lock.yaml` | `ZenUmlCoordinates.kt` horizontal constraints and optimizer |
| `@zenuml/core@3.49.2` `src/positioning/VerticalCoordinates.ts` and `src/positioning/vertical/vm` | Pinned by Mermaid's `pnpm-lock.yaml` | `ZenUmlVerticalCoordinates.kt` statement and fragment heights |
| `@zenuml/core@3.49.2` `src/svg/buildGeometry.ts`, `buildParticipantGeometry.ts`, and components | Pinned by Mermaid's `pnpm-lock.yaml` | `ZenUmlGeometry.kt`, `ZenUmlLayout.kt`, and `ZenUmlVectors.kt` |
| `@zenuml/core@3.49.2` participant SVG assets and `src/svg/icons.ts` | Pinned by Mermaid's `pnpm-lock.yaml` | Generated `ZenUmlParticipantVectors.kt` |
| `packages/mermaid/src/docs/syntax/zenuml.md` | `20dd17962a85666205d24d5dbc810ac8451102367af57bd990a3c4115d6cbf81` | `OfficialZenUmlDocumentationCases.kt` documentation fixtures |

## Kotlin Adaptations

- The ANTLR grammar and semantic state are represented by a pure Kotlin lexer,
  parser, and immutable model.
- The upstream HTML/CSS/SVG renderer is translated into explicit participant,
  lifeline, occurrence, message, fragment, comment, divider, and icon
  SceneGraph primitives.
- The David Eisenstat horizontal solver and vertical coordinate rules retain
  upstream constants and ordering.
- Participant and fragment SVG assets are converted to reusable SceneGraph
  vector paths; the generator remains outside production modules.
- Parsing and layout expose structured `GMResult` failures, with the engine
  preserving a final unexpected-error boundary.

## Verification Map

- Parser and model behavior: `ZenUmlParserTest.kt`
- Horizontal and vertical coordinates: `ZenUmlCoordinatesTest.kt` and
  `ZenUmlVerticalCoordinatesTest.kt`
- Geometry and scene rendering: `ZenUmlLayoutTest.kt`
- Official documentation fixtures: `OfficialZenUmlDocumentationTest.kt`
- Production, determinism, themes, and 256-case Native matrix:
  `ProductionCorpusTest.kt`
