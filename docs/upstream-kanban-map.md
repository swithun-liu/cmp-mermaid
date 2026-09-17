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
| `rendering-util/createText.ts` | `MermaidTextPort` plus Kanban text measurement |
| `rendering-util/rendering-elements/shapes/util.ts` | Kanban title/ticket/assignee measurement and placement |
| `config.type.ts` (`KanbanDiagramConfig`) | `MermaidKanbanOptions` |
| `schemas/config.schema.yaml` (`KanbanDiagramConfig`) | `MermaidPreprocessor` Kanban config parsing |
| `packages/mermaid/src/docs/syntax/kanban.md` | Generated official documentation fixtures |

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
| `shapes/util.ts` | `01976a4ac65c2f91bbd1572467f6a954bb34e5b3c6322b92d3d1bff0c52717ee` |
| `packages/mermaid/src/docs/syntax/kanban.md` | `df8ee76a26c410128f145786a6a73ebefd0a5ff31b3ee845665c833674f64b13` |
| `schemas/config.schema.yaml` | `5b59ca5612d5ebfbf1ef9277601224a734c831e16f3d1654ae74481dea4b26b0` |

## Upgrade Procedure

1. Check out the new Mermaid tag and compare every locked hash.
2. Reconcile grammar and DB changes before changing layout behavior.
3. Recheck shared `kanbanItem`, `kanbanSection`, and text rendering helpers.
4. Regenerate the 3 official documentation fixtures and both production
   corpora.
5. Re-run focused tests, 256-case deterministic stress, 13-case production
   detail/geometry capture, and 256-case visual parity.
6. Review all 16 contact sheets before changing the family status.
