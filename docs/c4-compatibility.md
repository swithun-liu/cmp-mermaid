# C4 Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported C4 diagram must preserve its diagram level, people, systems,
containers, components, database and queue variants, nested boundaries,
deployment nodes, relations, dynamic indexes, styles, configuration, metadata,
and theme colors.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and preprocessing | Supported | `C4Context`, `C4Container`, `C4Component`, `C4Dynamic`, and `C4Deployment`, plus frontmatter, init directives, comments, metadata, entities, and Unicode |
| Parser | Supported | Upstream Jison macro actions, quoted and multiline arguments, positional and named attributes, aliases, boundary braces, relation variants, and source locations |
| Database semantics | Supported | Element replacement, boundary stack and depth, nested ownership, relation source order, dynamic indexes, style updates, and layout updates |
| Elements | Supported | Internal and external people, systems, containers, and components, including database and queue variants |
| Boundaries | Supported | Generic, enterprise, system, container, deployment, left-node, and right-node boundaries with nested sizing and labels |
| Relations | Supported | Standard, up, down, left, right, back, bidirectional, and explicitly indexed relations with technology labels, colors, and offsets |
| Styling | Supported | `UpdateElementStyle`, `UpdateRelStyle`, theme defaults, per-element configuration, background, border, font, shape, and relation colors |
| Configuration | Supported | Margins, padding, dimensions, wrapping, row counts, boundary typography, message typography, and `useMaxWidth`, with typed validation |
| Metadata | Supported | Visible title, accessibility description, frontmatter title, comments, HTML breaks, entities, and multilingual text |

## Validation Corpus

- All 6 active Mermaid C4 documentation examples are generated from the pinned
  documentation source and rendered in JVM tests.
- Focused parser, database, layout, preprocessing, and engine tests cover all
  five headers, every supported element and relation macro, nested boundaries,
  named attributes, styles, layout configuration, dynamic indexes, malformed
  input, resource limits, unknown endpoints, and deterministic rendering.
- 13 independent production scenarios cover all 26 declared visual capability
  points.
- 256 deterministic same-source cases cover all combinations of eight C4
  capability axes; each axis and every pair are balanced, and every axis
  changes all 128 paired label-normalized sources.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.028-1.118`, `0.884-1.048`, and `1.004-1.203`.
- Production detail passed `13/13`. Geometry counts compare both render
  objects and expanded primitives, so Official's decomposed database and queue
  paths and Native's semantic shapes are validated without a
  representation-only review.
- The fresh 256-case coverage-driven input audit reports
  `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width
  `0.985-1.086`, height `1.044-1.062`, and foreground ink `1.063-1.119`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved element, boundary, relation, dynamic-index, label,
  clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/c4-production-detail.json),
  [production geometry](assets/stability-report/c4-production-geometry.json),
  [matrix detail](assets/stability-report/c4-visual-parity-detail.json), and
  [matrix geometry](assets/stability-report/c4-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:c4-doc-fixtures
```

Capture the 256-case C4 partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=c4 \
OUTPUT_DIR=captures/local/c4-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=900 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
