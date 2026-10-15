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
| Constraint layout | Supported for `randomize: false` | Reached Mermaid, Cytoscape, fCoSE, CoSE, and layout-base stages are translated, including the two-run lifecycle, row/column alignment, relative placement, force integration, compound bounds, and constrained disconnected graphs |
| Configuration | Partial | `useMaxWidth`, `padding`, `iconSize`, `fontSize`, `nodeSeparation`, `idealEdgeLengthMultiplier`, `edgeElasticity`, `numIter`, and `seed` are supported with typed validation; legal `randomize: true` input returns `UnsupportedFeature` until spectral/Matrix/SVD initialization is translated |
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
- 256 deterministic same-source cases cover all combinations of eight
  Architecture capability axes; each axis and every pair are balanced, and
  every axis changes all 128 paired label-normalized sources.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.020-1.232`, `0.996-1.236`, and `0.985-1.561`.
- Production detail produced `13 pass / 0 review / 0 fail`.
- Seven coordinate-oracle fixtures compare the Kotlin fCoSE adapter with the
  pinned JavaScript stack, including disconnected roots, nested compounds,
  mixed-axis constraints, label-expanded bounds, and both seeded layout runs.
- The fresh 256-case coverage-driven input audit reports
  `251 pass / 5 review / 0 fail`; the remaining reviews are genuine
  text-overlap findings in cases `042`, `178`, `186`, `242`, and `250`.
  Geometry has `0 fail / 0 review`, with width `0.990-1.090`, height
  `0.959-1.173`, and foreground ink `0.978-1.184`.
- The production contact sheet and all 16 matrix contact sheets are published;
  source identity is verified, and the automated review findings remain
  explicit for manual inspection.
- Raw
  [production detail](assets/stability-report/architecture-production-detail.json),
  [production geometry](assets/stability-report/architecture-production-geometry.json),
  [matrix detail](assets/stability-report/architecture-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/architecture-visual-parity-geometry.json)
  reports are published with the evidence.

Architecture remains non-Stable because `randomize: true` is a legal Mermaid
configuration that is still structurally unsupported, and because the five
detail-review cases remain unresolved.

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
