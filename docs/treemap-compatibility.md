# Treemap Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Treemap must preserve its indentation hierarchy, values, D3
hierarchy sums and squarify geometry, section and leaf paint order, class
styles, adaptive labels, value formats, metadata, configuration, and theme
colors closely enough that side-by-side review exposes no functional defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `treemap`, documented `treemap-beta`, frontmatter, init directives, comments, title, accessibility metadata, and Unicode |
| Parser | Supported | Quoted section/leaf names, colon and comma values, decimals, grouped thousands, class selectors, class definitions, and structured source locations |
| Hierarchy | Supported | Multiple outer roots, irregular indentation widths, arbitrary logical depth, source order, synthetic root, and recursive database levels |
| D3 hierarchy | Supported | d3-hierarchy `3.1.2` node depth/height, bottom-up sums, descending value sort, and traversal order |
| Treemap layout | Supported | Mermaid's `1000 x 600` extent, D3 squarify rows, golden-ratio scoring, configured inner padding, fixed section padding/header, and coordinate rounding |
| Branch rendering | Supported | Nested rectangles, depth colors, 0.3 fill opacity, labels, italic values, border width, section layout, and paint order |
| Leaf rendering | Supported | Depth colors, 0.7 fill opacity, centered labels/values, adaptive font reduction, minimum visibility, and border width |
| Class styling | Supported | `fill`, `stroke`, `stroke-width`, `color`, font size/weight/style, selectors on branches and leaves, and source precedence |
| Value formatting | Supported | Default grouping, currency aliases, fixed and significant precision, exponential, integer/radix, SI, percentage, sign, symbol, alignment, width, zero padding, and trim behavior used by d3-format `3.1.2` |
| Configuration | Supported | `useMaxWidth`, `padding`, `diagramPadding`, `showValues`, `nodeWidth`, `nodeHeight`, `borderWidth`, `valueFontSize`, `labelFontSize`, and `valueFormat` |
| Themes and metadata | Supported | Built-in color scales, light/dark label colors, title, accessibility title/description, comments, entities, and mixed-script labels |
| Validation | Supported | Malformed statements and invalid configuration return typed errors; invalid value formats follow Mermaid's comma fallback |

## Validation Corpus

- All eight active examples extracted from Mermaid's official Treemap
  documentation run in JVM tests.
- 13 independent production scenarios cover all declared Treemap capability
  points.
- 256 deterministic randomized Native inputs exercise hierarchy depth and
  breadth, both headers, class styles, visibility, spacing, sizing, formats,
  themes, finite geometry, and deterministic replay.
- The production Native/Official geometry audit accepted all 13 pairs: width
  `1.033-1.044`, height `1.029-1.039`, and foreground ink `0.945-1.079`.
- Manual production contact-sheet review found matching hierarchy, rectangle
  geometry, labels, values, formats, styles, colors, clipping, and paint order.
  Automated review flags on branch title/value pairs are caused by Canvas/SVG
  glyph bounds around Mermaid's intentional same-row placement, not visible
  text overlap.
- The shared production test renders all 13 cases deterministically and renders
  a Treemap representative under every built-in theme.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:treemap-doc-fixtures
```

Capture the 256-case Treemap partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=treemap \
OUTPUT_DIR=captures/local/treemap-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
