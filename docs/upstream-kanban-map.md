# Mermaid 12.0.0 Kanban Source Map

## Baseline

- Version: `12.0.0`
- Commit: `98a0945418c76238f15df2afaddbba4272656c3b`
- License path used by this project: MIT

## Translation Map

| Upstream source | Native translation |
| --- | --- |
| `diagrams/kanban/detector.ts` | `KanbanPlugin.kt`, `MermaidEngine.kt` |
| `diagrams/kanban/parser/kanban.jison` | `KanbanParser.kt` |
| `diagrams/kanban/kanbanDb.ts` | `KanbanParser.kt` (`KanbanDb`) |
| `diagrams/kanban/kanbanTypes.ts` | `KanbanParser.kt` data model |
| `diagrams/kanban/kanbanRenderer.ts` | `KanbanLayout.kt` |
| `diagrams/kanban/styles.ts` | `KanbanLayout.kt` section and item styles |
| `rendering-util/rendering-elements/shapes/kanbanItem.ts` | `KanbanNodeFactory.measureItem` and task paint order |
| `rendering-util/rendering-elements/clusters.js` (`kanbanSection`) | Section geometry and label placement |
| `rendering-util/createText.ts` (`createText`, `addHtmlSpan`) and `handle-markdown-text.ts` | `MermaidTextPort` plus Compose constrained HTML-label measurement |
| `rendering-util/rendering-elements/shapes/util.ts` | Kanban title/ticket/assignee measurement and placement |
| `config.ts` (`getConfig`) | `KanbanNodeFactory` effective HTML-label behavior |
| `config.type.ts` (`KanbanDiagramConfig`) | `MermaidKanbanOptions` |
| `schemas/config.schema.yaml` (`KanbanDiagramConfig`) | `MermaidPreprocessor` Kanban config parsing |
| `packages/mermaid/src/docs/syntax/kanban.md` | Generated official documentation fixtures |

## Production Function Checklist

Status follows the Mermaid `12.0.0` production call order. A stage is not
`Exact` merely because its current output is renderable.

| Order | Upstream production stage | Kotlin stage | Status |
| ---: | --- | --- | --- |
| 1 | `kanbanRenderer.ts -> draw`: read `getData`, call `getConfig`, mutate only that returned copy, create section/item layers, and preserve source order | `KanbanLayout.layout` | **Platform adaptation**: DOM groups become ordered SceneGraph elements while section/item ordering is retained; the local `htmlLabels = false` mutation's lack of effect on fresh `getConfig()` reads is preserved |
| 2 | `kanbanRenderer.ts -> draw`: measure every `kanbanSection` through `insertCluster` before placing items | `KanbanNodeFactory.measure`, section measurement loop | **Platform adaptation**: browser measurement becomes `TextMetricProvider`; the measured maximum label height and fixed section coordinates are retained |
| 3 | `kanbanRenderer.ts -> draw`: set item width, call `insertNode`, read its rendered bounds, and advance the vertical cursor | `KanbanNodeFactory.measureItem`, item placement loop | **Platform adaptation**: the same `sectionWidth - 15`, rendered height, and 5px item gap feed native geometry |
| 4 | `kanbanItem.ts -> kanbanItem`; `util.ts -> labelHelper`; `createText.ts -> createText`, `addHtmlSpan`; `handle-markdown-text.ts -> markdownToHTML` for the title | `KanbanNodeFactory.measure`, `measureItem`, title `SceneText`, and Compose HTML-label measurement | **Unsupported**: HTML `max-width`, `break-spaces`, 1.5 line-height, and subpixel intrinsic-width comparison are translated, but the bundled fallback cannot reproduce proprietary Trebuchet MS metrics exactly |
| 5 | `kanbanItem.ts -> kanbanItem`; `util.ts -> insertLabel`; `createText.ts -> createText`, `addHtmlSpan` for ticket and assignee | `KanbanNodeFactory.measure`, metadata placement, and Compose HTML-label measurement | **Unsupported**: HTML line-height and width comparison are translated, but exact Trebuchet MS glyph metrics are host-dependent |
| 6 | `kanbanItem.ts -> kanbanItem`: total width/height, title/metadata transforms, card rectangle, priority line, and intersection bounds | `KanbanNodeFactory.measureItem` and item paint loop | **Platform adaptation**: SVG transforms become absolute SceneGraph bounds while retaining the upstream formulas and paint order |
| 7 | `clusters.js -> kanbanSection`; `styles.ts -> getStyles` | section paint loop and `sectionColors` | **Platform adaptation**: SVG/CSS becomes explicit SceneGraph paint while retaining the palette selector offset and classic/neo styling |
| 8 | `kanbanRenderer.ts -> setupGraphViewbox` | `normalize` | **Platform adaptation**: the upstream `mindmap.padding` / `mindmap.useMaxWidth` compatibility quirk is retained without a DOM viewBox |

`getConfig()` returns a copy of the effective current configuration.
`kanbanRenderer.draw` sets `htmlLabels = false` only on that local copy.
`labelHelper` and `insertLabel` subsequently read fresh `getConfig()` copies,
so titles, tickets, assignees, and section labels follow `createText`'s
`addHtmlSpan` / SVG `foreignObject` path with CSS `max-width`,
`white-space: break-spaces`, and line-height `1.5`. The SVG
`splitLineToFitWidth` path is not in the effective Kanban production call
chain.

Mermaid's classic theme starts with proprietary Trebuchet MS. The public
Compose adapter cannot bundle that font and its cross-platform font API cannot
select a same-named host font on every target. Arimo remains the documented
portable fallback; callers that own a licensed Trebuchet installation can
provide it through `MermaidFontFamilyResolver`. A survey of 435 public Sans
families and weight variants found no legal fallback that preserved all 2,240
task-label wrap boundaries. The retained Arimo fallback uses the measured
`0.98` width scale; the larger replacement-font experiment was reverted.

The final production build was recaptured across all 256 Native/Official
cases. Detail reports `254 pass / 2 review / 0 fail`; geometry reports no
failures or reviews. In cases 242 and 250, the bundled Arimo fallback wraps
`Program Case Schedule` and `Program Case Respond`, while Mermaid's Trebuchet
renderer keeps each label on one line.

Kanban must not be described as complete or Stable while exact Trebuchet
metrics remain unsupported. The `254/256` detail result records the current
evidence; it does not waive that platform gap.

## Locked Source Hashes

| File | SHA-256 |
| --- | --- |
| `detector.ts` | `b4dd23e8c9866752e250a95d7315873b46592c9d3e971370423039f542263012` |
| `kanban-definition.ts` | `fda98e4656cc1f93ae0a8ca4b3da18a14634d700e7e2ffd4dc0ab46e9ececd03` |
| `kanbanDb.ts` | `d26473103e8b12917944a97f47f86b7e3785b22784224c66ab0ef1a3a926742b` |
| `kanbanRenderer.ts` | `2902d13a6e03398dc2bd8c889d150e3dfcc3576143994689a86ea0f8457f7a36` |
| `kanbanTypes.ts` | `9c25e4d52563a0f60768120e7f067f3fb582cbf48510af239f94e31f9205faef` |
| `parser/kanban.jison` | `e9e8bb8c6938e9abf7f47b496f57cafa6b6c5f1288e1aeeae2951e9efb096b81` |
| `styles.ts` | `4d0060c73c147e98f8891ee48be5367598737cd31e88bea46a5d8a09047bd2b4` |
| `shapes/kanbanItem.ts` | `bda78e289624e01576e9cdcc15bd9d5acdaa707e280f6ad7a6fa16e52ff9b0fc` |
| `rendering-elements/clusters.js` | `4a73517c6b85450b99cfe1bb6a6d0478055751d1e15c7feb6ed159fadd8fc733` |
| `createText.ts` | `711f26bbda94ae9b8020e5aa59e8b41692c8ae569de6fe080000dac4719f5536` |
| `handle-markdown-text.ts` | `6d8fc409c5401de7aa1f7c8ca2e2f1384af8d9f96690102cfe88646ee7971b28` |
| `shapes/util.ts` | `01976a4ac65c2f91bbd1572467f6a954bb34e5b3c6322b92d3d1bff0c52717ee` |
| `config.ts` | `a2dcc81b3764fec09626dc5a13f97c2bdd860378d6c06e9894357b4af2e87d9e` |
| `packages/mermaid/src/docs/syntax/kanban.md` | `df8ee76a26c410128f145786a6a73ebefd0a5ff31b3ee845665c833674f64b13` |
| `schemas/config.schema.yaml` | `5b59ca5612d5ebfbf1ef9277601224a734c831e16f3d1654ae74481dea4b26b0` |

## Upgrade Procedure

1. Check out the new Mermaid tag and compare every locked hash.
2. Reconcile grammar and DB changes before changing layout behavior.
3. Recheck shared `kanbanItem`, `kanbanSection`, and text rendering helpers.
4. Regenerate the 3 official documentation fixtures and both production
   corpora.
5. Re-run focused tests, 256-sample deterministic stress, 13-case production
   detail/geometry capture, and the 256-case coverage-driven input matrix.
6. Review all 16 contact sheets before changing the family status.
