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
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  20 visible text and layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.028-1.118`, `0.884-1.048`, and `1.004-1.203`.
- Production detail produced `12 pass / 1 reviewed / 0 fail`. The sole review
  is `prod_c4_component_boundaries`: Official SVG splits database and queue
  silhouettes into multiple paths, while Native records each visible
  silhouette as one semantic shape. Side-by-side pixels, expected text,
  geometry, colors, and paint order were manually verified.
- Matrix geometry passed `256/256`. Ratios were `0.996-1.153`,
  `0.992-1.046`, and `1.005-1.252`.
- Matrix detail produced `256 pass / 0 review / 0 fail`.
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
