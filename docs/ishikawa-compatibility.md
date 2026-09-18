# Ishikawa Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Ishikawa diagram must preserve the effect, indentation-derived cause
hierarchy, alternating upper and lower branches, recursive descendant order,
fish-head geometry, labels, configuration, and theme colors closely enough
that side-by-side review exposes no functional defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `ishikawa`, `ishikawa-beta`, frontmatter, init directives, comments, metadata, and Unicode |
| Parser | Supported | Effect statement, cause statements, blank lines, comments, arbitrary leading indentation, and structured source locations |
| Database | Supported | First-cause indentation baseline, relative nesting levels, recursive cause ownership, sanitized labels, and edge resource limits |
| Branch layout | Supported | Alternating top-level sides, side statistics, spine allocation, recursive descendant spacing, leaf and deep cause branches |
| Fish head | Supported | Quadratic upper and lower head curves, effect label wrapping, fill, stroke, and text color |
| Cause rendering | Supported | Label rectangles, connecting bones, arrowheads, multiline wrapping, deterministic flattening, and paint order |
| Configuration | Supported | `diagramPadding` and `useMaxWidth`, including frontmatter/init precedence and non-negative padding validation |
| Themes and sizing | Supported | Mermaid theme variables, responsive and intrinsic sizing, padded bounds, long labels, and mixed-script text |
| Sanitization | Supported | HTML is sanitized without decoding entity references; real `<br/>` tags split lines while encoded tags remain literal |
| Validation | Supported | Missing effects, invalid headers, invalid configuration, and resource exhaustion return typed errors |
| Hand-drawn look | Explicitly unsupported | Effective `look: handDrawn` returns `MermaidError.UnsupportedFeature` instead of silently rendering classic output |

## Validation Corpus

- The active example extracted from Mermaid's official Ishikawa documentation
  runs in a JVM test.
- 13 independent production scenarios cover all 21 declared Ishikawa
  capability points.
- 256 deterministic same-source matrix cases exercise both headers,
  root-only and deep trees, alternating branches, indentation normalization,
  comments, entities, HTML breaks, configuration, themes, Unicode, and long
  wrapping.
- The production Native/Official geometry audit accepted all 13 pairs: width
  `1.040-1.188`, height `1.059-1.119`, and foreground ink `1.067-1.326`.
- The production detail audit produced `12 pass / 1 review / 0 fail`. The
  root-only review is a manifest-only element-count difference: Native retains
  the zero-length spine while Official omits it from visible geometry. Raster
  output and effect semantics match.
- The 256-case matrix geometry audit passed every pair: width `1.040-1.173`,
  height `1.023-1.119`, and foreground ink `1.058-1.311`.
- The matrix detail audit produced `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed for hierarchy, branch direction, fish-head geometry, labels,
  wrapping, themes, clipping, and paint order. Raw
  [production detail](assets/stability-report/ishikawa-production-detail.json),
  [production geometry](assets/stability-report/ishikawa-production-geometry.json),
  [matrix detail](assets/stability-report/ishikawa-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/ishikawa-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:ishikawa-doc-fixtures
```

Capture the 256-case Ishikawa partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=ishikawa \
OUTPUT_DIR=captures/local/ishikawa-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
