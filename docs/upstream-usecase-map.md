# Mermaid.js 12.0.0 Use Case Source Map

## Upstream Lock

- Repository: `https://github.com/mermaid-js/mermaid`
- Tag: `mermaid@12.0.0`
- Commit: `98a0945418c76238f15df2afaddbba4272656c3b`
- Local reference: `build/upstream/mermaid-src`

## Translation Map

| Upstream Mermaid.js source | Kotlin translation |
| --- | --- |
| `packages/mermaid/src/diagrams/usecase/usecaseDetector.ts` | `UsecasePlugin.kt` header detection and registration |
| `packages/mermaid/src/diagrams/usecase/usecaseAst.ts` | `UsecaseModel.kt`, `UsecaseParser.kt` statement model |
| `packages/mermaid/src/diagrams/usecase/usecaseDb.ts` | `UsecaseModelBuilder.kt` state, aliases, boundaries, styles, notes, and relations |
| `packages/mermaid/src/diagrams/usecase/usecaseTypes.ts` | `UsecaseModel.kt` node, relation, note, style, and configuration types |
| `packages/mermaid/src/diagrams/usecase/usecaseRenderer.ts` | `UsecaseLayout.kt` graph construction, layout adaptation, labels, markers, and scene assembly |
| `packages/mermaid/src/diagrams/usecase/styles.ts` | `UsecaseLayout.kt` theme roles, color rotation, stroke/fill cascade, and typography |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/usecaseActor*.ts` | `UsecaseShapePort.kt`, `UsecaseLayout.kt` actor variants |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/usecaseBusiness.ts` | `UsecaseShapePort.kt` business element |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/usecaseEllipse.ts` | `UsecaseShapePort.kt` ellipse use case |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/usecaseJsonTable.ts` | `UsecaseLayout.kt` structured payload table |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/note.ts` | `UsecaseLayout.kt` note geometry and connector |
| `packages/mermaid/src/docs/syntax/usecase.md` | `OfficialUsecaseDocumentationCases.kt` documentation fixtures |

## Kotlin Adaptations

- The upstream parser is represented as a traceable Kotlin parser and model
  builder rather than executing Mermaid's JavaScript AST pipeline.
- Dagre-compatible graph construction is translated to the repository's pure
  Kotlin layout port; no browser layout dependency enters production.
- Compound SVG actor/table shapes become explicit ordered SceneGraph
  primitives. This can change element counts without changing visible
  semantics.
- Official rich-text groups may become multiple Native text elements. These
  text-segmentation differences are retained in the review evidence.
- CSS cascade behavior is resolved before scene creation. Direct role rules on
  actor glyph primitives override inherited group stroke values exactly where
  Mermaid's generated CSS does so.

## Verification Map

- Parser and database behavior: `UsecaseParserTest.kt`
- Layout, styles, shape variants, tables, and theme cascade:
  `UsecaseLayoutTest.kt`
- Frontmatter and configuration: `UsecaseConfigTest.kt`
- Official documentation fixtures: `OfficialUsecaseDocumentationTest.kt`
- Production and deterministic replay: `ProductionCorpusTest.kt`
- 256-case stress and parity: `StabilityCorpusTest.kt` and the published
  Native/Official evidence
