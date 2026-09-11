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
| Headers and directions | Supported | `flowchart`, `flowchart-elk`, `graph`, TB/TD/BT/LR/RL and symbolic aliases |
| Classic node syntax | Supported | Rectangle, rounded, stadium, database, decision, circle, hexagon and IO shapes |
| Metadata shape syntax | Supported with exceptions | Mermaid 12 aliases and native geometry are covered; icon/image assets still need an injected loader |
| Links and labels | Supported | Solid, dotted, thick, open, invisible, labelled and bidirectional links |
| Edge markers | Supported | Triangle, circle and cross markers at either or both ends |
| Edge ids and lengths | Supported | Minimum rank length is applied; animation metadata renders statically |
| Multi-node links | Supported | `A & B --> C & D` |
| Classes and inline styles | Supported | Fill, stroke, text color, stroke width/pattern, font size/weight; Hex, RGB(A), HSL(A) and common named colors |
| `linkStyle` | Supported with exceptions | Static stroke/text properties are applied; Mermaid curve interpolation names are not |
| Routing | Supported | Layered placement, ordered ports, parallel lanes, cycles, compact self-loops, rounded orthogonal corners and horizontal-priority crossing bridges |
| Subgraphs | Supported | Nested groups, boundary links, collapsed view and independently laid out local directions |
| Markdown strings | Partial | Basic emphasis and line breaks; full Markdown layout remains |
| Click and tooltip directives | Ignored safely | Native callback and tooltip APIs are not exposed yet |
| Icon and image nodes | Parse only | Requires an injected KMP asset loader |
| Mobile gestures | Supported | Single-finger gestures remain available to the parent scroll container; two-finger pan/zoom is clipped and bounded |

## Current State

- The Android documentation app contains 18 syntax lessons and 40 generated
  comparison cases.
- Each comparison case uses identical Mermaid source for the native renderer
  and the Mermaid.js `12.0.0` PNG.
- JVM tests cover parsing, colors, markers, minimum link lengths, multi-node
  routing, parallel-label separation, local subgraph directions, compact
  self-loops, viewport constraints, and crossing bridge generation.
- Android APK and iOS Simulator targets compile from the same common source.

## Mermaid And Dagre Mapping

The native pipeline follows Mermaid `12.0.0` and `dagre-d3-es` stage
boundaries so upstream behavior can be compared without copying JavaScript
runtime code:

| Native stage | Upstream reference |
| --- | --- |
| `FlowchartParser` | Mermaid `flowDb.ts` edge expansion, ids, subgraphs and styles |
| `FlowLayering` | Dagre cycle removal, rank assignment, normalization and ordering |
| `FlowNodePlacer` | Dagre coordinate assignment with half-ranks for edge labels |
| `FlowCompoundLayout` | Mermaid Dagre recursive cluster measurement and local `dir` |
| `FlowDirectionResolver` | Nearest cluster direction inheritance |
| `FlowEdgeRouter` | Ordered ports, normalized edge chains, parallel lanes and compact self-loops |
| `LineBridgeRouter` | Mermaid `lineJump.ts`, with horizontal segments receiving bridges |
| Compose renderer | Mermaid flowchart stroke widths, dash patterns, markers and collapsed-group decoration |

## Remaining Gaps

- Rich Markdown measurement does not yet match Mermaid's HTML label renderer.
- Icon and image nodes need a public KMP asset-provider contract.
- Click, link, callback, and tooltip directives need native interaction APIs.
- Very large or adversarial graphs still need performance and layout stress
  testing before claiming complete Mermaid Flowchart compatibility.

## Reference Corpus

`tools/official-reference/cases.mjs` is the single source of truth for the
comparison gallery. Running `npm run render` produces:

- Official Mermaid.js PNG files under the Android sample resources.
- The Android demo catalog.
- JVM test fixtures consumed by `MermaidEngineTest`.

`tools/capture-android-audit.sh` opens each gallery case directly and captures
Native and Official views from the same Android viewport. Screenshots are
stored locally under the ignored `captures/local/` directory.
