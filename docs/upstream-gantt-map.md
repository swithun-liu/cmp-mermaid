# Upstream Gantt Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Jison runtime | `0.4.18` |
| kotlinx-datetime | `0.7.1` |
| Native renderer | Compose Multiplatform Canvas |

The production Gantt path is Kotlin in `commonMain`. It does not execute
Mermaid.js and does not require a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `gantt/upstream/mermaid/GanttJisonTables.kt` | `src/diagrams/gantt/parser/gantt.jison` generated parser | Symbols, terminals, productions, LALR states, lexer rules, and conditions |
| `gantt/upstream/mermaid/GanttJisonRuntime.kt` | Jison `0.4.18` generated runtime | Parser stack, lexer states, locations, errors, and Android ICU-compatible matching |
| `gantt/upstream/mermaid/GanttJisonParser.kt` | `gantt.jison` semantic actions | Directives, sections, tasks, links, callbacks, metadata, and parser-to-DB calls by production index |
| `gantt/upstream/mermaid/GanttTypes.kt` | `ganttDb.js` task records | Raw and compiled tasks, flags, start expressions, interactions, and document state |
| `gantt/upstream/mermaid/GanttDb.kt` | `src/diagrams/gantt/ganttDb.js` | Task tags, generated ids, dependency compilation, duration/end resolution, exclusions, interactions, and metadata |
| `gantt/upstream/mermaid/GanttDatePort.kt` | `ganttDb.js` Day.js calls and D3 time ticks/formatting | Date token parsing, duration arithmetic, calendar alignment, automatic tick interval selection, and axis formatting |

`tools/official-reference/generate-gantt-parser.mjs` verifies Mermaid
`12.0.0` and the grammar SHA-256 before regenerating
`GanttJisonTables.kt`. Semantic actions remain indexed by the matching Jison
production so an upstream grammar diff can be translated incrementally.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `gantt/GanttLayout.kt` | `src/diagrams/gantt/ganttRenderer.js` | Width/height calculation, time scale, row assignment, sections, exclusions, grid, tasks, labels, today marker, title, and SceneGraph output |
| `gantt/GanttLayout.kt` | `src/diagrams/gantt/styles.js` | Mermaid 12 section, task-state, text, grid, today, and vertical-marker colors |
| `MermaidPreprocessor.kt` | Mermaid frontmatter/directive config flow and Gantt schema | Gantt options, display mode, secure host precedence, and structured unsupported CSS detection |
| `SceneGraph.kt` | Mermaid Gantt SVG elements | Typed bars, milestone geometry, lines, text spans, metadata, and interactions |
| `mermaid-compose/.../MermaidDiagram.kt` | Mermaid SVG painting behavior | Native Canvas shapes, text, paths, clipping, pan, and zoom |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `gantt.jison` | `14d325aa626a7fa220c341a75faecc712d0121203e0f88db6c11e68f0bc7e82f` |
| `ganttDb.js` | `b185ef0fa9d54977ca623e936b06dc39f4783b640bd3fb33dbf4a9996d90b4f6` |
| `ganttRenderer.js` | `56970417c2a4998fdbb3518522cf4b4cc7f60c464630bb4de367f365ba1d4fd1` |
| `styles.js` | `db7dafa5efd4cdf1f5b283ab93d5fe4bb9eb345ce98cd67abe7d703d2f674579` |
| `gantt.md` | `e477f721f0ab703ee2deb56b7aa87edf268ee978890dab770584620762a18e75` |

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields.
- JavaScript insertion order maps to linked Kotlin collections.
- Expected parser, semantic, date, resource, and layout failures use
  `GMResult.Err`.
- Date parsing and calendar arithmetic use UTC so output is deterministic
  across JVM, Android, and Kotlin/Native.
- Mermaid's D3 automatic time-tick interval table is translated to typed
  Kotlin intervals.
- D3 `interval.every(count)` anchoring is preserved: weeks are selected against
  D3's epoch week index, while day, month, hour, minute, and second intervals
  use the matching calendar-field modulo.
- The generated comment lexer rule
  `\%\%(?!\{)*[^\n]*` is replaced by an equivalent line-prefix scanner because
  Android ICU rejects repetition of a zero-width lookahead.
- Direct `topAxis` calls `enableTopAxis()` because Mermaid `12.0.0`'s generated
  action calls the nonexistent `TopAxis()` method.
- Mermaid's missing-parent-width fallback remains 1200 scene units.
- DOM text measurement is supplied by `TextMetricProvider`; Compose uses the
  same font, size, and line-height inputs for measurement and painting.
- SceneGraph interactions preserve sanitized links and callbacks while the
  host owns their execution.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `GanttJisonParserTest` covers parser directives, task tags, dependencies,
  date formats, durations, interactions, malformed input, and D3 multi-unit
  tick anchoring.
- `GanttLayoutTest` covers sections, task-state colors, milestones, vertical
  markers, exclusions, automatic ticks, compact rows, interactions, title,
  accessibility, and structured unsupported errors.
- `GanttStressTest` renders 256 deterministic random legal diagrams and checks
  finite bounds and retained task counts.
- `OfficialGanttDocumentationCases` executes all 11 examples extracted from
  Mermaid's Gantt documentation.
- The Android gallery compares 20 identical sources against official Mermaid
  `12.0.0` output, including a width-matched mode for label-placement review.
- The Web RC capture rejects an Official Gantt SVG whose viewBox is less than
  75% of its iframe width, preventing a flex-shrunk reference from masking or
  inventing Native layout differences.

## Upgrade Procedure

1. Update the locked Mermaid version, source commit, and hashes.
2. Diff `gantt.jison`, `ganttDb.js`, `ganttRenderer.js`, `styles.js`, and
   `gantt.md`.
3. Regenerate `GanttJisonTables.kt` and translate changed semantic actions,
   DB behavior, date operations, renderer methods, or styles in their mapped
   Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run full JVM tests, Android lint/assembly, and every configured iOS compile
   target.
6. Install the Android sample and capture all 20 Native/Official Gantt pairs.
7. Review the contact sheets at both the default official width and Mermaid's
   1200-unit fallback where label placement is width-sensitive.
8. Update this map and the compatibility matrix before publishing.
