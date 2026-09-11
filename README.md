# CMP Mermaid

Native Mermaid rendering for Kotlin Multiplatform and Compose Multiplatform.
The compatibility baseline is Mermaid `12.0.0`; the current implementation
focuses exclusively on flowcharts.

## Modules

- `mermaid-core`: parser, flowchart model, layered layout, and SceneGraph.
- `mermaid-compose`: Compose Canvas rendering, text measurement, bounded two-finger pan, and zoom.
- `sample/androidApp`: mobile documentation and a 40-case official comparison gallery.
- `tools/official-reference`: reproducible Mermaid.js reference image generator.

## Verify

```bash
./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  :sample:androidApp:assembleDebug
```

Regenerate every official reference image and the shared Kotlin demo catalog:

```bash
cd tools/official-reference
npm install
npm run render
```

The generator asserts that the installed Mermaid package is exactly `12.0.0`.
Each gallery item therefore uses the same source for the native renderer and
the official Mermaid.js PNG.

Capture the complete Native/Official gallery from a connected Android device:

```bash
ANDROID_SERIAL=<serial> WAIT_SECONDS=5 tools/capture-android-audit.sh
```

The same script can restrict a regression pass without changing its default
40-case behavior:

```bash
CAPTURE_PREVIEWS=Native \
CAPTURE_CASE_IDS=multi_node_links,crossing_routes \
ANDROID_SERIAL=<serial> \
tools/capture-android-audit.sh
```

## Current Flowchart Coverage

Supported:

- `flowchart` and `graph` declarations with `TB`, `TD`, `BT`, `LR`, and `RL`.
- Classic node delimiters and Mermaid v11+ metadata shape declarations.
- Solid, dotted, thick, invisible, bidirectional, circle, and cross links.
- Edge labels, edge ids, minimum lengths, and static edge class styling.
- Chained and `&` multi-node links.
- Nested/collapsed subgraphs, local subgraph directions, class definitions,
  node classes, and inline styles.
- Hex, RGB(A), HSL(A), and common CSS named colors.
- Port-aware orthogonal routing, parallel lanes, self-loops, rounded corners,
  and crossing bridges.
- Front matter, comments, semicolon-separated statements, and line breaks.

Production gaps are tracked in
[`docs/flowchart-compatibility.md`](docs/flowchart-compatibility.md).
