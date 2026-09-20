# Swimlanes Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Swimlanes diagram must preserve flowchart syntax and state while
using Mermaid's lane-aware layout, lane containers, direction transforms,
orthogonal routing, labels, markers, styles, configuration, and theme colors.

Swimlanes is beta in Mermaid `12.0.0`; its syntax and layout options may change
in a later upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `swimlane-beta`, frontmatter, init directives, comments, title, accessibility metadata, entities, safe HTML labels, and Unicode |
| Parser and database | Supported | Reuses the translated Flowchart Jison parser and database exactly as upstream reuses `createFlowDiagram` |
| Lane ownership | Supported | Top-level lanes, nested subgraphs, a synthesized default lane for loose nodes, source lane order, and automatic lane-order optimization |
| Layering | Supported | Cycle removal, lane-aware compact layering, gravity layering, optional crossing optimization, proper-layer dummy nodes, and rank ordering |
| Coordinates | Supported | Lane-preserving node placement, aligned lane extents, nested group write-back, and configurable node/rank spacing |
| Directions | Supported | `TB`, `TD`, `BT`, `LR`, and `RL`, including rotated horizontal-lane titles |
| Routing | Supported | Obstacle-aware orthogonal routes, cross-lane edges, cycles, self-loops, edge-label nodes, endpoint clipping, and line hops |
| Flowchart semantics | Supported | Flowchart node shapes, labels, marker families, line patterns, classes, inline styles, and Markdown/HTML text |
| Configuration | Supported | `layout`, `theme`, `look`, `lineHops`, `ignoreCrossLaneEdges`, `optimizeRanksByCrossings`, and `automaticLaneOrdering` with typed validation |
| Themes | Supported | Upstream Redux default, all built-in color themes, classic/hand-drawn look selection, and lane palette assignment |

## Validation Corpus

- All 13 active Mermaid Swimlanes documentation examples are generated from
  the pinned documentation source and rendered in JVM tests.
- Focused tests cover vertical and horizontal lanes, rotated titles, default
  lanes, labelled orthogonal edges, scoped Dagre fallback, all Swimlanes
  options, and accessibility metadata.
- 13 independent production scenarios cover all 15 declared visual capability
  points.
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  20 visible text/layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.007-1.191`, `0.912-1.070`, and `0.928-1.603`.
- Production detail produced `10 pass / 3 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `1.028-1.186`,
  `0.883-1.070`, and `0.899-1.551`.
- Matrix detail produced `196 pass / 60 review / 0 fail`. Every review contains
  only `text-position`, caused by browser/Compose label placement along
  semantically equivalent orthogonal routes.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved lane, node, shape, edge, marker, label, color,
  clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/swimlanes-production-detail.json),
  [production geometry](assets/stability-report/swimlanes-production-geometry.json),
  [matrix detail](assets/stability-report/swimlanes-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/swimlanes-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:swimlane-doc-fixtures
```

Capture the 256-case Swimlanes partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=swimlanes \
OUTPUT_DIR=captures/local/swimlanes-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
