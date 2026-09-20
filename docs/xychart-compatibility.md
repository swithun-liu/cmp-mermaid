# XY Chart Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported XY Chart must preserve orientation, axes, domains, bars, lines,
series names, labels, colors, titles, accessibility metadata, and
configuration closely enough that a side-by-side comparison does not expose
a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not
silently rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `xychart`, `xychart-beta`, frontmatter, comments, title, accessibility metadata, and XY Chart configuration |
| Parser | Supported | Kotlin runtime for Mermaid's generated `xychart.jison` tables and translated semantic actions |
| Orientation | Supported | Vertical and horizontal source modifiers plus `chartOrientation` configuration |
| X axis | Supported | Categorical bands, numeric ranges, automatic `1..N` ranges, title, labels, ticks, line, padding, font sizes, and rotation |
| Y axis | Supported | Explicit and automatic numeric ranges, negative and decimal values, title, labels, ticks, line, padding, font sizes, and rotation |
| Plots | Supported | Multiple line and bar series, declaration-order colors, overlapping bars, and mismatched series lengths |
| Legends | Supported | Named-series entries, line/bar markers, visibility, font size, and padding |
| Data labels | Supported | Bar labels inside or outside bars and per-point line labels |
| Themes and config | Supported | Dimensions, title controls, plot reservation, all XY theme colors, comma-separated palettes, and built-in Mermaid themes |
| Resource controls | Supported | Plot count is bounded by host-owned `maxEdges` |

When a categorical axis contains more labels than a series contains values,
the missing points are omitted. This matches the effective Mermaid SVG
behavior while keeping SceneGraph geometry finite.

## Explicit Unsupported Boundary

`themeCSS` and `altFontFamily` return `MermaidError.UnsupportedFeature`
because the native renderer does not execute browser CSS. Canvas sizing is
owned by the host `Modifier`; Mermaid's SVG-only `useWidth` and `useMaxWidth`
presentation flags do not alter the fixed intrinsic SceneGraph dimensions.

## Validation Corpus

- All 8 examples extracted from Mermaid's official XY Chart documentation run
  in JVM tests.
- Parser and layout tests cover categorical and numeric axes, automatic
  domains, horizontal orientation, named series, legends, bar labels, point
  labels, rotations, nested configuration, theme overrides, missing values,
  equal domains, metadata, structured errors, Mermaid component paint order,
  and the browser fallback for invalid negative data-label font sizes.
- 256 deterministic random legal charts cover 2-12 points, 1-4 mixed series,
  both orientations, categorical and numeric axes, negative values, legends,
  labels, and plot-space configuration while checking finite SceneGraph
  bounds and path points.
- 13 independent production scenarios render identical source through Native
  Compose and Mermaid.js `12.0.0`: detail audit `13 pass / 0 review / 0 fail`;
  geometry width `1.016-1.020`, height `1.012-1.015`, and foreground ink
  `0.967-1.144`.
- 256 same-source visual-parity cases pass both the replacement detail and
  geometry gates: `256 pass / 0 review / 0 fail`; width `1.008-1.029`, height
  `0.995-1.041`, and foreground ink `0.918-1.163`.
- All 16 matrix contact sheets and all 256 pairs were manually reviewed. No
  unresolved blank, clipping, geometry, label, legend, line, bar, overlap, or
  paint-order defect remains. Browser/Compose font rasterization differences,
  point labels touching the left axis, and legend coverage of a horizontal
  data label were retained only where the pinned Mermaid result exhibits the
  same behavior.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate parser tables:

```bash
cd tools/official-reference
npm run generate:xychart-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:xychart-doc-fixtures
```

Capture the independent production corpus:

```bash
AUDIT_SOURCE=production AUDIT_KIND=xychart \
OUTPUT_DIR=captures/local/xychart-production-detail-final \
npm run capture:web-audit
```

Capture the 256-case matrix by changing `AUDIT_SOURCE` to `visual-parity`. Run
`npm run audit:detail` and `npm run audit:stability-geometry`, then generate
the 16 review sheets with `npm run generate:stability-contact-sheets`. Local
screenshots remain under ignored `captures/local/` paths; reviewed evidence
pages are published under `docs/assets/stability-report/`.
