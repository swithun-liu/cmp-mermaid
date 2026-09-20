# Quadrant Chart Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `quadrantChart`, frontmatter, comments, semicolon statements, title, and accessibility metadata |
| Axes | Supported | One- or two-sided labels, quoted/Markdown text, top/bottom X position, and left/right Y position |
| Quadrants | Supported | Four fills and labels, centered labels without points, top labels with points |
| Points | Supported | Coordinates in `[0, 1]`, boundary coordinates, radius, fill, stroke color, and stroke width |
| Classes | Supported | `classDef`, point class assignment, and direct style over class style over theme precedence |
| Themes and config | Supported | All 18 Quadrant Chart options and all 15 Quadrant theme variables |
| Metadata and text | Supported | Frontmatter title, `accTitle`, single/multiline `accDescr`, Unicode, HTML entities, and quoted labels |
| Resource controls | Supported | Point count is bounded by host-owned `maxEdges` |

## Validation

- All 3 Mermaid documentation examples render in JVM tests.
- Focused parser/layout tests cover upstream syntax, DB ordering, style
  validation, configuration, themes, metadata, and typed failures.
- 256 deterministic randomized legal inputs render twice with identical,
  finite SceneGraphs.
- 13 independent production cases pass detail audit:
  `13 pass / 0 review / 0 fail`.
- The source-controlled visual matrix contains 256 unique Quadrant sources.
  All 256 same-source Native/Official pairs pass both the detail audit
  (`256 pass / 0 review / 0 fail`) and the geometry gate at a shared
  `1200 x 900` capture viewport.
- Across the 256-pair matrix, Native/Official content ratios are
  `1.007-1.035` for width, `1.011-1.022` for height, and `1.024-1.048`
  for foreground ink.
- Sixteen paged contact sheets cover every pair; representative first,
  middle, and final pages were checked for blank output, clipping, label
  overlap, geometry, color, and text differences.

## Boundaries

`themeCSS` and browser CSS remain unsupported in the native renderer.
Compose uses host-owned Canvas sizing; the Scene viewport expands when
Mermaid's fixed SVG viewBox would otherwise show overflowing labels.

Reproduce the 256-pair visual gate with:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=quadrant \
OUTPUT_DIR=captures/local/quadrant-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=visual-parity \
CORPUS_KIND=quadrant \
INPUT_DIR=captures/local/quadrant-visual-parity \
OUTPUT_FILE=captures/local/quadrant-visual-parity/detail-report.json \
npm run audit:detail

CORPUS_SOURCE=visual-parity \
CORPUS_KIND=quadrant \
INPUT_DIR=captures/local/quadrant-visual-parity \
OUTPUT_FILE=captures/local/quadrant-visual-parity/geometry-report.json \
npm run audit:stability-geometry
```

See [the upstream source map](upstream-quadrant-map.md) for exact files,
hashes, and translation boundaries.
