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
  equal domains, metadata, and structured errors.
- 256 deterministic random legal charts cover 2-12 points, 1-4 mixed series,
  both orientations, categorical and numeric axes, negative values, legends,
  labels, and plot-space configuration while checking finite SceneGraph
  bounds and path points.
- 20 curated gallery cases render identical source through Native Compose and
  on-device Mermaid.js `12.0.0`.
- All 20 Native/Official pairs were captured and reviewed on Android.
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

Render the 20 browser references:

```bash
cd tools/official-reference
npm run render:xychart-gallery
```

Capture matching Native and on-device Official views:

```bash
CAPTURE_CASE_IDS="$(rg 'id = \"xy_' \
  mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/XyChartDemos.kt |
  sed -E 's/.*id = \"([^\"]+)\".*/\1/' | paste -sd, -)" \
CAPTURE_LAYOUT=dagre \
ANDROID_SERIAL=<serial> \
tools/capture-android-audit.sh
```

Generate review sheets with `npm run render:xychart-contact-sheet`. All
screenshots remain under ignored `captures/local/` paths.
