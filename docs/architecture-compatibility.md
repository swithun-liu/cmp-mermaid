# Architecture Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Architecture diagram must preserve services, groups, nested groups,
junctions, directional ports, arrow directions, edge labels, group-boundary
modifiers, explicit row and column alignment, icons, configuration, metadata,
and theme colors.

Architecture is beta in Mermaid `12.0.0`; its syntax and layout behavior may
change in a later upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `architecture-beta`, detector alias handling, frontmatter, init directives, comments, title, accessibility metadata, entities, and Unicode |
| Parser | Supported | Langium grammar semantics for services, groups, junctions, icons or icon text, parent groups, directional edges, arrows, labels, group-boundary modifiers, and alignment directives |
| Database semantics | Supported | Upstream declaration ordering, duplicate-ID checks, parent validation, edge endpoint validation, boundary-modifier validation, adjacency maps, disconnected components, spatial maps, and group alignment |
| Services and icons | Supported | Built-in `blank`, `cloud`, `database`, `disk`, `internet`, and `server` icons, quoted icon text, external icon assets, and service labels |
| Groups and junctions | Supported | Compound and nested group bounds, group icons and labels, junction fan-out, and group-boundary endpoints |
| Edges | Supported | `L`, `R`, `T`, and `B` ports, straight and orthogonal bends, source and target arrowheads, bidirectional arrows, edge labels, and group modifiers |
| Constraint layout | Supported | Seeded initial positions, Mermaid spatial constraints, upstream row/column alignment hints, relative placement, force integration, overlap removal, compound bounds, and disconnected-component packing |
| Configuration | Supported | `useMaxWidth`, `padding`, `iconSize`, `fontSize`, `randomize`, `nodeSeparation`, `idealEdgeLengthMultiplier`, `edgeElasticity`, `numIter`, and `seed` with typed validation |
| Themes | Supported | Architecture edge, arrow, group-border, icon, and text colors across all built-in themes |

## Validation Corpus

- All 6 active Mermaid Architecture documentation examples are generated from
  the pinned documentation source and rendered in JVM tests.
- Focused parser/database/layout tests cover declaration ordering, numeric IDs,
  disconnected components, nested groups, directional edges, arrows,
  junctions, row/column alignment, built-in and external icons, metadata,
  configuration, deterministic seeds, malformed input, and typed failures.
- 13 independent production scenarios cover all 18 declared visual capability
  points.
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  20 visible text/layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.020-1.232`, `0.996-1.236`, and `0.985-1.561`.
- Production detail produced `13 pass / 0 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `1.020-1.232`,
  `0.996-1.236`, and `0.986-1.584`.
- Matrix detail produced `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved service, icon, group, junction, edge, arrow, label,
  alignment, clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/architecture-production-detail.json),
  [production geometry](assets/stability-report/architecture-production-geometry.json),
  [matrix detail](assets/stability-report/architecture-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/architecture-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:architecture-doc-fixtures
```

Capture the 256-case Architecture partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=architecture \
OUTPUT_DIR=captures/local/architecture-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
