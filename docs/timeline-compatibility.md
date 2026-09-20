# Timeline Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `timeline`, explicit `LR`/`TD`, frontmatter, comments, title, and accessibility metadata |
| Sections and periods | Supported | Section tracking, sectionless periods, source order, and color rotation |
| Events | Supported | Same-line events, continued event lines, multiple events, and colons inside event text |
| Left-to-right layout | Supported | Section bands, task/event stacks, dashed connectors, horizontal axis, arrowheads, and upstream paint order |
| Top-down layout | Supported | Sections, tasks left of the vertical axis, event stacks on the right, connectors, and arrowheads |
| Text | Supported | Unicode, entity-preserving upstream parser behavior, `<br>` wrapping, measured node heights, and titles |
| Themes | Supported | All 11 built-in themes, classic/neo looks, Redux variants, color scales, gradients, and shadows |
| Configuration | Supported | The complete Timeline config shape is parsed and validated; active renderer fields follow upstream behavior, while upstream no-op fields remain no-ops |
| Metadata | Supported | Frontmatter title, `accTitle`, and single- or multiline `accDescr` |
| Resource controls | Supported | Period and event count is bounded by host-owned `maxEdges`; invalid configuration returns typed errors |

## Validation

- All 14 Mermaid documentation examples render in JVM tests.
- Focused parser and layout tests cover directions, sections, continued
  events, metadata, comments, semicolons, wrapping, configuration, themes,
  resource limits, and typed failures.
- 256 deterministic randomized legal inputs render twice with identical,
  finite SceneGraphs.
- 13 independent production cases pass detail audit:
  `13 pass / 0 review / 0 fail`.
- The source-controlled visual matrix contains 256 unique Timeline sources.
  All 256 same-source Native/Official pairs pass both the detail audit
  (`256 pass / 0 review / 0 fail`) and the geometry gate at a shared
  `1200 x 900` capture viewport.
- Across the 256-pair matrix, Native/Official content ratios are
  `1.026-1.053` for width, `1.038-1.087` for height, and `1.052-1.136`
  for foreground ink.
- Sixteen paged contact sheets cover every pair. All pages were checked for
  blank output, clipping, overlap, geometry, color, text, and LR/TD connector
  differences.

## Boundaries

Timeline text follows Mermaid's plain SVG text behavior; arbitrary browser
HTML, CSS, and `themeCSS` are not executed by the native renderer. Platform
font rasterization may differ, while text and node geometry remain within the
recorded detail and geometry gates.

The production path emits a static SceneGraph. Mermaid `12.0.0` defines no
Timeline-specific interaction contract that requires browser execution.

Reproduce the 256-pair visual gate with:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=timeline \
OUTPUT_DIR=captures/local/timeline-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=visual-parity \
CORPUS_KIND=timeline \
INPUT_DIR=captures/local/timeline-visual-parity \
OUTPUT_FILE=captures/local/timeline-visual-parity/detail-report.json \
npm run audit:detail

CORPUS_SOURCE=visual-parity \
CORPUS_KIND=timeline \
INPUT_DIR=captures/local/timeline-visual-parity \
OUTPUT_FILE=captures/local/timeline-visual-parity/geometry-report.json \
npm run audit:stability-geometry
```

See [the upstream source map](upstream-timeline-map.md) for exact files,
hashes, translation boundaries, and intentional Kotlin adaptations.
