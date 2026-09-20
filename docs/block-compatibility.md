# Block Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Block diagram must preserve explicit and automatic grids, spans,
spaces, nested composites, all documented shapes, block-arrow directions,
links, markers, classes, inline styles, metadata, configuration, and theme
colors closely enough that side-by-side review exposes no functional defect.

Block is beta in Mermaid `12.0.0`; its syntax may change in a later upstream
release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and preprocessing | Supported | `block`, `block-beta`, frontmatter, init directives, comments, title, accessibility metadata, entities, safe HTML labels, and Unicode |
| Parser | Supported | Generated Jison lexer/LALR tables, node declarations, links, labels, all documented shape delimiters, block-arrow directions and aliases, composites, spaces, columns, spans, classes, and styles |
| Database semantics | Supported | Hierarchy construction, automatic IDs, duplicate declarations, class lookup order, edge numbering, width overflow, style and marker classification, and typed resource limits |
| Grid layout | Supported | Explicit and automatic columns, spans, spaces, row wrapping, nested composite grids, child normalization, and overflow clipping semantics |
| Shapes | Supported | Square, round, stadium, subroutine, cylinder, circle, double-circle, decision, asymmetric, hexagon, lean, trapezoid, inverse trapezoid, and directional block arrows |
| Links | Supported | Labelled and unlabelled links, normal/thick strokes, solid/dotted patterns, open/point/circle/cross markers, occurrence IDs, and shape intersections |
| Positioned sizing | Supported | Preserves upstream differences between allocated cell size and final natural, spanning, circular, and cylinder geometry |
| Classes and inline styles | Supported | Fill, stroke, width, dash pattern, text color, font, alignment, composite palette slots, and duplicate declaration composition |
| Configuration | Supported | `padding` and `useMaxWidth`, including frontmatter/init precedence and typed validation |
| Themes | Supported | Default and color-family themes, dark variants, look-specific shape rendering, and composite palette assignment |

## Validation Corpus

- All 30 active Mermaid Block documentation examples are generated from the
  pinned documentation source and rendered in JVM tests.
- Focused parser/database/layout tests cover headers, columns, spans, spaces,
  composites, every documented shape, block arrows, links, markers, classes,
  inline styles, duplicate declarations, metadata, sanitization, positioned
  sizing, malformed input, resource limits, and configuration failures.
- 13 independent production scenarios cover all 31 declared visual capability
  points.
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  20 visible text/layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `0.970-1.116`, `0.880-1.069`, and `0.908-1.388`.
- Production detail produced `13 pass / 0 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `0.970-1.281`,
  `0.856-1.078`, and `0.840-1.342`.
- Matrix detail produced `253 pass / 3 review / 0 fail`. The three reviews are
  paint-order overlap-ratio tolerance notices on otherwise matching,
  intentionally overlapping official layouts.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved shape, grid, edge, marker, color, text, clipping,
  overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/block-production-detail.json),
  [production geometry](assets/stability-report/block-production-geometry.json),
  [matrix detail](assets/stability-report/block-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/block-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the parser and documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 npm run generate:block-parser
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:block-doc-fixtures
```

Capture the 256-case Block partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=block \
OUTPUT_DIR=captures/local/block-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
