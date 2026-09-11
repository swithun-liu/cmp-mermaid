# CMP Mermaid

Native Mermaid rendering for Kotlin Multiplatform and Compose Multiplatform.
The compatibility baseline is Mermaid `12.0.0`; the current implementation
focuses exclusively on flowcharts.

## Modules

- `mermaid-core`: parser, flowchart model, layered layout, and SceneGraph.
- `mermaid-compose`: Compose Canvas rendering, text measurement, pan, and zoom.
- `sample/androidApp`: mobile documentation and official comparison gallery.
- `tools/official-reference`: reproducible Mermaid.js reference image generator.

## Verify

```bash
./gradlew :mermaid-core:jvmTest :sample:androidApp:assembleDebug
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

## Current Flowchart Coverage

Supported:

- `flowchart` and `graph` declarations with `TB`, `TD`, `BT`, `LR`, and `RL`.
- Classic node delimiters and Mermaid v11+ metadata shape declarations.
- Solid, dotted, thick, invisible, bidirectional, circle, and cross links.
- Edge labels, edge ids, minimum lengths, and static edge class styling.
- Chained and `&` multi-node links.
- Nested subgraphs, class definitions, node classes, and inline styles.
- Front matter, comments, semicolon-separated statements, and line breaks.

Production gaps are tracked in
[`docs/flowchart-compatibility.md`](docs/flowchart-compatibility.md).
