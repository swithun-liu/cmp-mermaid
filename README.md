# CMP Mermaid

Native Mermaid rendering for Kotlin Multiplatform and Compose Multiplatform.
The compatibility baseline is Mermaid `12.0.0`.

The production renderer libraries do not use a WebView and do not execute
Mermaid.js. Mermaid's Flowchart, XY Chart, Sequence, Class, State, Entity
Relationship, Gantt, and Pie parser semantics, diagram databases, layout
preparation, shapes, edges, markers, text handling, and SceneGraph conversion
are translated to Kotlin. ELK layout keeps Mermaid's Kotlin-translated adapter
around the locked `elkjs@0.9.3` worker, which runs in an isolated QuickJS
runtime.

```text
Mermaid 12 diagram source
        |
        v
Kotlin preprocessing + translated parser/runtime + diagram DB
        |
        +--> Flowchart: Kotlin Graphlib/Dagre
        |
        +--> Flowchart: Kotlin Mermaid ELK adapter --> elkjs 0.9.3 in QuickJS
        |
        +--> XY Chart: Kotlin Jison/D3/chartBuilder translation
        |
        +--> Sequence: Kotlin sequenceRenderer/svgDraw/actorBands translation
        |
        +--> Class: Kotlin classDb/classBox/unified renderer translation
        |
        +--> State: Kotlin stateDb/dataFetcher/unified renderer translation
        |
        +--> ER: Kotlin erDb/erBox/unified renderer translation
        |
        +--> Gantt: Kotlin ganttDb/Day.js/D3/renderer translation
        |
        +--> Pie: Kotlin Langium grammar/pieDb/D3/renderer translation
        |
        v
Platform-independent SceneGraph
        |
        v
Compose Canvas
```

## Modules

- `mermaid-core`: common Kotlin Flowchart, XY Chart, Sequence, Class, State,
  ER, Gantt, and Pie semantics, layout adapters, and platform-independent
  SceneGraph.
- `mermaid-compose`: Compose Canvas painting, typography, assets,
  interactions, and bounded two-finger pan/zoom.
- `sample/androidApp`: mobile syntax documentation, an editable Flowchart
  Playground with 45 presets, an on-demand local WebView for live official
  Mermaid.js comparison, a 45-case Flowchart gallery, a 20-case XY Chart
  gallery, a 35-case Sequence gallery, a 27-case Class gallery, a 25-case
  State gallery, a 20-case ER gallery, a 20-case Gantt gallery, and a 20-case
  Pie gallery.
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

## XY Chart Coverage

The supported path includes:

- Mermaid's generated `xychart.jison` grammar and translated `xychartDb.ts`
  state and automatic-domain semantics.
- Vertical and horizontal charts with categorical or numeric axes, negative
  and decimal values, axis titles, labels, ticks, lines, and rotations.
- Multiple bar and line series, declaration-order palettes, named-series
  legends, overlapping bars, and mismatched series lengths.
- Bar labels inside or outside bars and per-point labels on line series.
- Mermaid's translated `chartBuilder` space allocation, D3 linear/band
  scales, D3 tick selection, dimensions, visibility flags, and plot-space
  configuration.
- XY theme variables and palettes across all built-in Mermaid themes.

Browser CSS overrides return `MermaidError.UnsupportedFeature`; SVG-only
responsive sizing is owned by the Compose host. See
[`docs/xychart-compatibility.md`](docs/xychart-compatibility.md) for the full
matrix.

## Sequence Coverage

The supported path includes:

- Mermaid's generated `sequenceDiagram.jison` grammar and translated
  `sequenceDb.ts` state semantics.
- Participants, actors, Mermaid 12 participant types, aliases, boxes,
  create/destroy lifecycle, and top/bottom actor bands.
- All 26 Sequence message forms, including open, filled, cross,
  bidirectional, async, half, stick, dotted, and central-connection markers.
- Activations, self messages, autonumber, side/over notes, explicit wrapping,
  HTML line breaks, Unicode text, title, and accessibility metadata.
- `loop`, `alt`, `opt`, `par`, `par_over`, `critical`, `break`, and `rect`
  control regions, including nesting.

Browser-only participant menus, properties, and DOM `details` references
return `MermaidError.UnsupportedFeature`. See
[`docs/sequence-compatibility.md`](docs/sequence-compatibility.md) for the
full matrix.

## Class Diagram Coverage

The supported path includes:

- Mermaid's generated `classDiagram.jison` grammar and translated
  `classDb.ts` state semantics.
- Classes, aliases, annotations, generic types, member/method compartments,
  visibility, and static/abstract classifiers.
- Association, aggregation, composition, extension, dependency, lollipop,
  reverse, bidirectional, dotted, solid, and self relations.
- Relation labels, marker-aware cardinalities, standalone/attached notes,
  nested/labelled/compact namespaces, and every documented direction.
- Mermaid's default ELK layout and explicit Dagre override.
- `style`, `classDef`, links, callbacks, tooltips, Markdown/HTML text, title,
  accessibility metadata, Unicode, and empty-compartment configuration.

KaTeX, browser-only label content, `handDrawn`, and unmapped CSS return
`MermaidError.UnsupportedFeature`. See
[`docs/class-compatibility.md`](docs/class-compatibility.md) for the full
matrix.

## State Diagram Coverage

The supported path includes:

- Mermaid's generated `stateDiagram.jison` grammar, translated `stateDb.ts`
  document semantics, and translated `dataFetcher.ts` renderer projection.
- States, aliases, repeated descriptions, transitions, labels, self-loops,
  root/nested start and end markers, choice, fork, and join pseudostates.
- Named, nested, and sibling composite states, concurrency regions, and
  root/nested TB, BT, LR, and RL directions.
- Single-line and multiline folded notes with Mermaid's directed dashed-edge
  placement semantics.
- Mermaid's default ELK layout, named ELK algorithms, and explicit Dagre
  override.
- `style`, `classDef`, `class`, inline `:::`, links, tooltips, Markdown/HTML
  text, title, accessibility metadata, Unicode, and State layout configuration.

KaTeX, browser-only label content, `handDrawn`, unmapped CSS, and unconnected
layout engines return `MermaidError.UnsupportedFeature`. See
[`docs/state-compatibility.md`](docs/state-compatibility.md) for the full
matrix.

## Entity Relationship Diagram Coverage

The supported path includes:

- Mermaid's generated `erDiagram.jison` grammar and translated `erDb.ts`
  entity, attribute, relationship, style, subgraph, and metadata semantics.
- Aliases, Unicode names, nullable and generic attribute types, comments, and
  `PK`, `FK`, and `UK` key combinations.
- All four visible crow-foot cardinalities, identifying and non-identifying
  relationships, self-relationships, repeated relationships, and labels.
- Root and nested subgraphs, subgraph relationships, and TB, BT, LR, and RL
  directions.
- Mermaid's default ELK layout, named ELK algorithms, and explicit Dagre
  override.
- `style`, `classDef`, `class`, inline `:::`, Markdown/HTML text, title,
  accessibility metadata, Unicode, and ER layout configuration.

The `MD_PARENT` token is parsed but has no marker, matching Mermaid 12's
unified renderer, which does not register the legacy diamond definition.
KaTeX, browser-only label content, `handDrawn`, unmapped CSS, and unconnected
layout engines return `MermaidError.UnsupportedFeature`. See
[`docs/er-compatibility.md`](docs/er-compatibility.md) for the full matrix.

## Gantt Diagram Coverage

The supported path includes:

- Mermaid's generated `gantt.jison` grammar and translated `ganttDb.js`
  task, section, dependency, exclusion, interaction, and metadata semantics.
- Day.js-style input formats, all Mermaid duration units, Unix timestamps,
  previous-task shorthand, multiple `after` dependencies, and `until`.
- Weekday/date exclusions, includes, configurable weekends, inclusive end
  dates, milestones, vertical markers, and compact rows.
- D3-style automatic time ticks, explicit tick intervals, week starts, axis
  formatting, top axis, and Mermaid's 1200-unit width fallback.
- Mermaid 12 task-state, section, exclusion, grid, today-marker, title, and
  vertical-marker styling.
- Links and callbacks through `SceneNodeInteraction`, with Mermaid
  security-level behavior.

`themeCSS` and unsupported `todayMarker` CSS properties return
`MermaidError.UnsupportedFeature`. See
[`docs/gantt-compatibility.md`](docs/gantt-compatibility.md) for the full
matrix.

## Pie Diagram Coverage

The supported path includes:

- Mermaid's `pie.langium` grammar, token/value converters, and translated
  `pieDb.ts` section, duplicate-label, title, and accessibility semantics.
- Input-order D3 angles, one-percent filtering, percentage rounding, the
  twelve-color ordinal palette, and palette cycling.
- `showData`, zero and decimal values, quoted/escaped labels, inline comments,
  and all-zero data.
- `textPosition`, donut holes, named static slice highlighting, and `top`,
  `bottom`, `left`, `right`, or `center` legends.
- Mermaid 12 Pie colors, opacity, stroke, title, section, and legend theme
  variables across all built-in themes.

`highlightSlice: hover` returns `MermaidError.UnsupportedFeature` because
SceneGraph has no browser pointer-hover state. See
[`docs/pie-compatibility.md`](docs/pie-compatibility.md) for the full matrix.

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
native side with Mermaid's ELK layout. The documentation fixture generators
extract all 114 Flowchart examples, all 8 XY Chart examples, all 38 Sequence
examples, all 38 Class examples, all 22 State examples, all 24 ER examples,
all 11 Gantt examples, and both Pie examples from the locked Mermaid source.

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

The source-to-source upgrade maps are maintained in
[`docs/upstream-flowchart-map.md`](docs/upstream-flowchart-map.md) and
[`docs/upstream-xychart-map.md`](docs/upstream-xychart-map.md), with the
Sequence translation in
[`docs/upstream-sequence-map.md`](docs/upstream-sequence-map.md) and the Class
translation in
[`docs/upstream-class-map.md`](docs/upstream-class-map.md) and the State
translation in
[`docs/upstream-state-map.md`](docs/upstream-state-map.md). The ER translation
is mapped in [`docs/upstream-er-map.md`](docs/upstream-er-map.md), and the
Gantt translation in
[`docs/upstream-gantt-map.md`](docs/upstream-gantt-map.md). The Pie
translation is mapped in
[`docs/upstream-pie-map.md`](docs/upstream-pie-map.md).

Android hosts that opt into HTTP/HTTPS image loading must also declare the
`android.permission.INTERNET` permission; the library does not add it
implicitly.
