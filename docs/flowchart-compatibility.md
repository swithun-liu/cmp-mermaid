# Flowchart Compatibility

Baseline: Mermaid `12.0.0`.

## Release Gate

Production readiness requires:

- Every supported syntax case parses without fallback or silent data loss.
- Nodes and labels do not overlap or clip at supported font scales.
- Links preserve direction, marker type, label, and minimum rank length.
- Android and iOS render the same SceneGraph within typography tolerance.
- Official comparison fixtures and cloud-device smoke tests pass.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and directions | Supported | `flowchart`, `graph`, TB/TD/BT/LR/RL |
| Classic node syntax | Supported | Rectangle, rounded, stadium, database, decision, circle, hexagon, IO shapes |
| Metadata shape syntax | Partial | All Mermaid 12 aliases parse; specialized geometry is still being completed |
| Links and labels | Supported | Solid, dotted, thick, circle/cross, bidirectional, invisible |
| Edge ids and lengths | Supported | Animation metadata is parsed but rendered statically |
| Multi-node links | Supported | `A & B --> C & D` |
| Classes and inline styles | Supported | Fill, stroke, text color, stroke width |
| `linkStyle` | Partial | Static stroke properties; curve interpolation is not applied |
| Subgraphs | Partial | Nested groups render; collapse and subgraph endpoints need dedicated layout |
| Markdown strings | Partial | Basic emphasis and line breaks; full Markdown layout remains |
| Click and tooltip directives | Parse only | Native callback API is not exposed yet |
| Icon and image nodes | Parse only | Requires an injected KMP asset loader |

## Reference Corpus

`tools/official-reference/cases.mjs` is the single source of truth for the
comparison gallery. Running `npm run render` produces:

- Official Mermaid.js PNG files under the Android sample resources.
- The Android demo catalog.
- JVM test fixtures consumed by `MermaidEngineTest`.

Cloud-device screenshots are stored under `captures/cloud/`.
