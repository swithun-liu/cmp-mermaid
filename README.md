# CMP Mermaid

Native Mermaid Flowchart rendering for Kotlin Multiplatform and Compose
Multiplatform. The compatibility baseline is Mermaid `12.0.0`.

The production renderer libraries do not use a WebView and do not execute
Mermaid.js. Mermaid's Flowchart preprocessing, parser semantics, FlowDB, Dagre
adapter, shapes, edges, markers, text handling, and SceneGraph conversion are
translated to Kotlin. ELK layout keeps Mermaid's Kotlin-translated adapter
around the locked `elkjs@0.9.3` worker, which runs in an isolated QuickJS
runtime.

```text
Mermaid 12 Flowchart source
        |
        v
Kotlin preprocessing + flow.jison runtime + FlowDB
        |
        +--> Kotlin Graphlib/Dagre
        |
        +--> Kotlin Mermaid ELK adapter --> elkjs 0.9.3 in QuickJS
        |
        v
Platform-independent SceneGraph
        |
        v
Compose Canvas
```

## Modules

- `mermaid-core`: common Kotlin Flowchart semantics, layout adapters, and
  platform-independent SceneGraph.
- `mermaid-compose`: Compose Canvas painting, typography, assets,
  interactions, and bounded two-finger pan/zoom.
- `sample/androidApp`: mobile syntax documentation, an editable Flowchart
  Playground with 45 presets, an on-demand local WebView for live official
  Mermaid.js comparison, and a 45-case Native/Official comparison gallery.
- `tools/official-reference`: reproducible Mermaid.js reference and source
  generation tools; these are development-only and are not part of the native
  runtime.

## Flowchart Coverage

The supported path includes:

- `flowchart`, `flowchart-elk`, and `graph`, with all documented directions.
- Mermaid `flow.jison` grammar and FlowDB behavior, including chained and
  multi-node links.
- Classic and metadata node shapes, nested subgraphs, cross-hierarchy edges,
  classes, inline styles, frontmatter, directives, and themes.
- Dagre and Mermaid's ELK algorithms/options, including `arc`, `gap`, and
  disabled ELK crossing line hops.
- Solid, dotted, thick, invisible, animated, bidirectional, circle, and cross
  links, edge ids, labels, and minimum lengths.
- Marked `16.4.2` label tokenization, HTML formatting spans, HTML entities,
  sanitization, and Mermaid HTML/SVG line-height behavior.
- Android bitmap/SVG image nodes from `data:` sources by default, with
  explicitly enabled HTTP/HTTPS loading and intrinsic-size layout feedback.
- Native links, tooltips, and callbacks through `SceneNodeInteraction`, with
  Mermaid security-level behavior.

Features that cannot yet be represented correctly return
`MermaidError.UnsupportedFeature` instead of rendering a misleading
approximation. The main boundaries are KaTeX, FontAwesome/Iconify label
replacement, inline HTML image/link/SVG/MathML content, `handDrawn`,
`themeCSS`, `altFontFamily`, and unsupported CSS properties. See
[`docs/flowchart-compatibility.md`](docs/flowchart-compatibility.md) for the
full matrix.

## Verification

Run the shared JVM tests and build the Android sample:

```bash
./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  :sample:androidApp:assembleDebug
```

Compile every configured iOS architecture:

```bash
./gradlew \
  :mermaid-core:compileKotlinIosArm64 \
  :mermaid-core:compileKotlinIosSimulatorArm64 \
  :mermaid-core:compileKotlinIosX64 \
  :mermaid-compose:compileKotlinIosArm64 \
  :mermaid-compose:compileKotlinIosSimulatorArm64 \
  :mermaid-compose:compileKotlinIosX64
```

Regenerate all 45 official reference images and the shared Kotlin gallery:

```bash
cd tools/official-reference
npm install
npm run render
```

The generator asserts Mermaid `12.0.0` and renders the same source used by the
native side with Mermaid's ELK layout. The separate documentation fixture
generator extracts all 114 Flowchart examples from the locked Mermaid source.

Capture every Native/Official pair from a connected Android device:

```bash
ANDROID_SERIAL=<serial> WAIT_SECONDS=8 tools/capture-android-audit.sh
```

A focused pass can select cases and preview modes:

```bash
CAPTURE_PREVIEWS=Native \
CAPTURE_CASE_IDS=multi_node_links,crossing_routes,line_hops_gap \
CAPTURE_LAYOUT=dagre \
ANDROID_SERIAL=<serial> \
tools/capture-android-audit.sh
```

The source-to-source upgrade map is maintained in
[`docs/upstream-flowchart-map.md`](docs/upstream-flowchart-map.md).

Android hosts that opt into HTTP/HTTPS image loading must also declare the
`android.permission.INTERNET` permission; the library does not add it
implicitly.
