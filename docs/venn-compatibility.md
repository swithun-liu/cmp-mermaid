# Venn Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Venn diagram must preserve weighted set and intersection areas,
synthetic pairwise constraints, circle placement, intersection paths, labels,
free text, styles, configuration, metadata, and theme colors closely enough
that side-by-side review exposes no functional defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `venn-beta`, frontmatter, init directives, comments, title, accessibility metadata, and Unicode |
| Parser | Supported | Quoted identifiers, quoted and unquoted bracket labels, optional sizes, unions, explicit and indented text, styles, and structured source locations |
| Database | Supported | Default set/intersection sizes, sorted identifiers, known-set validation, indentation state, text nodes, merged style entries, title, and accessibility metadata |
| Pairwise completion | Supported | Multi-set intersections synthesize the pairwise constraints required by `venn.js`, capped by the smallest participating set |
| Venn optimization | Supported | Greedy and constrained-MDS initialization, conjugate-gradient and Nelder-Mead optimization, loss minimization, orientation, disjoint clusters, and viewport scaling |
| Geometry | Supported | Circle overlap and distance, circle intersections, multi-circle intersection arcs, area paths, bounds, and stable set keys |
| Text placement | Supported | Set/intersection labels, area text centers, exclusion constraints, multiple text nodes, line wrapping, and debug placement guides |
| Styling | Supported | Set, intersection, and text styles; fill, stroke, stroke width, fill opacity, text color, hex, RGB, and RGBA colors |
| Configuration | Supported | `useMaxWidth`, `width`, `height`, `padding`, and `useDebugLayout` |
| Themes and metadata | Supported | Eight Venn palette slots where supplied, flat fallback themes, title and set text colors, comments, and mixed-script labels |
| Validation | Supported | Unknown unions, invalid cardinality, malformed statements, invalid numbers, and invalid configuration return typed errors |

## Validation Corpus

- All four active examples extracted from Mermaid's official Venn
  documentation run in JVM tests.
- 13 independent production scenarios cover all 39 declared Venn capability
  points.
- 256 deterministic randomized Native inputs exercise two-to-four-set
  topology, pairwise and multi-set intersections, text, styles, sizing,
  themes, finite geometry, and deterministic replay.
- The production Native/Official geometry audit accepted all 13 pairs: width
  `1.014-1.022`, height `1.010-1.027`, and foreground ink `1.028-1.044`.
- The 256-case matrix geometry audit passed every pair: width `0.997-1.032`,
  height `0.992-1.026`, and foreground ink `0.999-1.059`.
- The replacement detail audit produced `216 pass / 40 review / 0 fail`.
  Manual review accepted all 40 review cases with 0 unresolved defects. The
  reviews are repeated text-overlap threshold findings for two known label
  pairs.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed for circle topology, overlap size, labels, text placement, styles,
  clipping, and paint order. Raw [production detail](assets/stability-report/venn-production-detail.json), [production geometry](assets/stability-report/venn-production-geometry.json), [matrix detail](assets/stability-report/venn-visual-parity-detail.json), and [matrix geometry](assets/stability-report/venn-visual-parity-geometry.json) reports are published with the evidence.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:venn-doc-fixtures
```

Capture the 256-case Venn partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=venn \
OUTPUT_DIR=captures/local/venn-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
