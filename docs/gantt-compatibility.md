# Gantt Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Gantt diagram must preserve tasks, sections, dates, durations,
dependencies, exclusions, milestones, vertical markers, status styling,
axes, metadata, and interactions closely enough that a side-by-side
comparison does not expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not
silently discarded or rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `gantt`, frontmatter, directives, comments, title, accessibility metadata, and Gantt configuration |
| Parser | Supported | Kotlin runtime for Mermaid's generated `gantt.jison` tables and translated semantic actions |
| Tasks and sections | Supported | Explicit and generated ids, sequential tasks, sections, Unicode descriptions, and source order |
| Task states | Supported | `active`, `done`, `crit`, combined states, and Mermaid 12 default colors |
| Dates and durations | Supported | Day.js-style input tokens used by Mermaid, Unix seconds/milliseconds, years, months, weeks, days, hours, minutes, seconds, and milliseconds |
| Dependencies | Supported | Previous-task shorthand, single/multiple `after` references, and single/multiple `until` references |
| Calendar rules | Supported | Specific-date and weekday exclusions, `weekends`, Friday/Saturday weekends, includes, and inclusive end dates |
| Milestones | Supported | Mermaid's rotated/scaled diamond geometry, italic labels, dependency placement, and status colors |
| Vertical markers | Supported | Full-height lines, labels, ids, dates, durations, and Mermaid's navy default styling |
| Axes | Supported | D3-style automatic intervals, explicit millisecond through month intervals, configurable week start, axis formatting, and top axis |
| Compact mode | Supported | Non-overlapping tasks share rows per section using Mermaid's interval ordering |
| Themes and config | Supported | Gantt dimensions, font sizes, paddings, section styles, axis options, width, and Mermaid 12 theme variables |
| Links and callbacks | Supported | Exposed as `SceneNodeInteraction`; callbacks are retained only at Mermaid's loose security level |
| Resource controls | Supported | Tasks are bounded by host-owned `maxEdges`; exclusion expansion and tick generation are bounded |

The direct `topAxis` statement is accepted by the Kotlin parser. Mermaid
`12.0.0` generates a call to missing `yy.TopAxis()` for that statement even
though its DB API is `enableTopAxis()`. Frontmatter
`config.gantt.topAxis: true` works in both implementations and is used by the
visual parity corpus.

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities return `UnsupportedFeature`:

- `themeCSS`, including DOM selectors that mutate individual Gantt elements.
- `todayMarker` CSS properties other than `stroke`, `stroke-width`, and
  `opacity`.

Links and callbacks are represented as SceneGraph interactions. The host owns
navigation and callback execution; the native renderer does not execute
arbitrary URLs or JavaScript.

The date port uses UTC to keep JVM, Android, and Kotlin/Native output
deterministic. Timezone-free calendar input retains the Mermaid date labels
and ordering. Explicit offsets are normalized to UTC, so DST-dependent local
hour shifts are intentionally not reproduced.

## Validation Corpus

- 11 examples extracted from Mermaid's official Gantt documentation run in
  JVM tests. The one `themeCSS` example must return its declared
  `UnsupportedFeature`.
- Semantic tests cover parser directives, task flags, date formats,
  durations, dependencies, exclusions, milestones, vertical markers,
  interactions, title, and accessibility metadata.
- 256 deterministic random legal Gantt diagrams cover 3-12 tasks, multiple
  sections, compact mode, weekends, top axis, milestones, vertical markers,
  task states, dependencies, and tick intervals while checking finite bounds
  and retained task counts.
- 20 curated gallery cases render identical source through Native Compose and
  Mermaid.js `12.0.0`.
- All 20 Native Android screenshots were compared side by side with 20
  Puppeteer-rendered official references.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate the parser tables:

```bash
cd tools/official-reference
npm run generate:gantt-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:gantt-doc-fixtures
```

Render the 20 official references:

```bash
cd tools/official-reference
npm run render:gantt-gallery
```

`VIEWPORT_WIDTH=<pixels>` can reproduce Mermaid's width-dependent label
placement. Capture the same demo ids with
`tools/capture-android-audit.sh`, then generate side-by-side sheets with
`npm run render:gantt-contact-sheet`. All screenshots remain under ignored
`captures/local/` paths.
