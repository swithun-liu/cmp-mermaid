# Swimlanes Compatibility

Baseline: Mermaid `12.0.0`.

> [!WARNING]
> Status: **revalidated, not Stable**. All translated classic-look behavior
> passed the fresh production, DDLT, 256-case visual, and per-edge topology
> gates. Mermaid's legal RoughJS-backed `look: handDrawn` remains explicitly
> unsupported, so this family and the overall contract are not Stable.

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
| Configuration | Supported with explicit boundary | `layout`, `theme`, `lineHops`, `ignoreCrossLaneEdges`, `optimizeRanksByCrossings`, and `automaticLaneOrdering` use typed validation; effective `look: handDrawn` returns `UnsupportedFeature` |
| Themes | Supported with explicit boundary | Upstream Redux default, all built-in color themes, classic look, and lane palette assignment are supported; the RoughJS-backed hand-drawn look is not approximated |

## Validation Corpus

The direct translation has fresh evidence at three independent levels:

- 13 upstream DDLT fixtures: `13 pass / 0 review / 0 fail` for detail and
  topology, with 0 geometry review or failure;
- 13 production cases: `13 pass / 0 review / 0 fail` for detail and topology,
  with width `1.028-1.074`, height `0.976-1.070`, and foreground ink
  `1.004-1.130`;
- 256 deterministic same-source cases: `255 pass / 1 review / 0 fail` for
  detail, `256/256` topology pass, and 0 geometry review or failure, with width
  `1.023-1.074`, height `0.976-1.078`, and foreground ink `0.982-1.130`.

All 16 regenerated contact sheets were manually reviewed. The sole detail
review is `visual_swimlanes_004`: Native and Official both preserve the
upstream title/author overlap. Raster IoU is `0.9874` and edge F1 is `0.9901`;
the different overlap ratio comes from platform font metrics and is retained
instead of being hidden by a coordinate patch.

The 47-entry upstream spec map is complete: 33 translated Kotlin unit-test
groups, 13 executable DDLT fixtures, and one structured unsupported case for
`handDrawn`. Machine-readable evidence:

- [production detail](assets/stability-report/swimlanes-production-detail.json),
  [geometry](assets/stability-report/swimlanes-production-geometry.json), and
  [topology](assets/stability-report/swimlanes-production-topology.json);
- [256-case detail](assets/stability-report/swimlanes-visual-parity-detail.json),
  [geometry](assets/stability-report/swimlanes-visual-parity-geometry.json), and
  [topology](assets/stability-report/swimlanes-visual-parity-topology.json);
- [DDLT detail](assets/stability-report/swimlanes-ddlt-detail.json),
  [geometry](assets/stability-report/swimlanes-ddlt-geometry.json), and
  [topology](assets/stability-report/swimlanes-ddlt-topology.json).

Stable remains blocked only by the explicit `look: handDrawn` unsupported
boundary.

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
OUTPUT_DIR=captures/local/coverage-driven-v2/swimlanes \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

INPUT_DIR=captures/local/coverage-driven-v2/swimlanes \
npm run audit:topology
```
