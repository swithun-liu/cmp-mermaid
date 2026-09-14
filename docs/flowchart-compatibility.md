# Flowchart Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Flowchart must preserve its content, hierarchy, direction, routing,
markers, labels, styles, and interactions closely enough that a side-by-side
comparison does not expose a functional rendering defect.

Legal Mermaid 12 input that cannot be represented by the native SceneGraph
must return a structured `MermaidError.UnsupportedFeature`; it must not be
silently dropped or approximated as another feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and directions | Supported | `flowchart`, `flowchart-elk`, `graph`, TB/TD/BT/LR/RL, and symbolic aliases |
| Preprocessing and configuration | Supported with explicit boundaries | Frontmatter, directives, comments, entity handling, config precedence, secure host keys, themes, Flowchart and ELK options |
| Flowchart parser and FlowDB | Supported | Kotlin runtime for Mermaid's generated `flow.jison` tables and translated FlowDB semantics |
| Classic node syntax | Supported | Rectangle, rounded, stadium, database, decision, circle, hexagon, IO, and asymmetric forms |
| Metadata shape syntax | Supported | Mermaid 12 shape aliases, geometry, labels, constraints, icon nodes, and image nodes |
| Links and labels | Supported | Solid, dotted, thick, open, invisible, animated, labelled, and bidirectional links |
| Edge markers | Supported | Triangle, circle, and cross markers at either or both ends |
| Edge ids and lengths | Supported | Explicit ids, classes, animation metadata, and minimum rank lengths |
| Multi-node links | Supported | Chained links and `A & B --> C & D` expansion preserve FlowDB order |
| Classes and inline styles | Supported with explicit boundaries | Fill, stroke, background, border, dash, animation, font, line height, alignment, and decoration; unknown CSS returns `UnsupportedFeature` |
| Colors | Supported | Hex, RGB(A), HSL(A), transparent, and CSS named colors |
| Routing: Dagre | Supported | Translated Graphlib/Dagre pipeline, compound graphs, cycles, self-loops, parallel lanes, and Mermaid D3 curves |
| Routing: ELK | Supported | Mermaid adapter translated to Kotlin; locked `elkjs@0.9.3` executes in QuickJS |
| ELK line hops | Supported | `arc`, `gap`, and disabled crossing treatment |
| Subgraphs | Supported | Nested groups, collapsed groups, boundary links, local directions, title margins, and cross-hierarchy edges |
| Markdown strings | Supported for Mermaid's label path | Marked `16.4.2` lexer/token behavior used by Mermaid, including emphasis, code, deletion, blocks, escapes, and line breaks |
| HTML labels | Supported with explicit boundaries | Formatting spans, entities, sanitization, color/background, relative font sizes, alignment, decoration, and line height |
| Themes | Supported with explicit boundaries | Mermaid default, dark, forest, neutral, base, neo/redux variants, theme variables, color arrays, and default Flowchart appearance |
| Security levels | Supported | Strict, loose, antiscript, and sandbox URL/callback handling; diagram text cannot override secure host limits |
| Links, callbacks, and tooltips | Supported | Exposed as `SceneNodeInteraction`; callbacks are retained only at Mermaid's loose security level |
| Image nodes | Android supported | Cached bitmap/SVG loading from `data:` by default; HTTP/HTTPS requires host `INTERNET` permission plus `AndroidMermaidAssetProvider(MermaidNetworkAccess.HttpAndHttps)`; 8 MiB input and 4096 px decode bounds |
| Image nodes | iOS/JVM host provider required | The public `MermaidAssetProvider` contract is available; no platform default is installed yet |
| Icon metadata nodes | Provider required | SceneGraph retains pack/name metadata; Android displays an explicit fallback when no icon pack is registered |
| Mobile gestures | Supported | One-finger parent scrolling remains available; two-finger pan/zoom is clipped and bounded |
| Resource controls | Supported | Host-owned `maxTextSize`, `maxEdges`, 10-second ELK timeout, and QuickJS memory/stack limits |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities currently return
`UnsupportedFeature`:

- KaTeX labels.
- FontAwesome substring replacement and custom Iconify pack registration.
- Inline HTML `<img>`, `<a>`, `<svg>`, and MathML content inside labels.
- HTML layout tags whose DOM box behavior cannot be represented by text spans.
- `look: handDrawn`, because Mermaid implements it through roughjs.
- `themeCSS` and `altFontFamily`.
- Unmapped CSS, including letter spacing, word spacing, text shadow, text
  transform, and white-space/word-breaking behavior.

Additional platform limitations:

- iOS and Desktop compile against the common asset-provider contract but do
  not yet ship default network/bitmap/SVG providers.
- Arbitrary system font-family discovery is host-defined. The Compose adapter
  bundles Arimo as an Arial-compatible default, Droid Sans Mono for HTML code
  spans, and accepts a custom `MermaidFontFamilyResolver`.
- Non-zero browser SVG blur is represented by the closest Compose shadow, not
  browser-filter pixel parity.
- iOS source sets compile for Arm64, Simulator Arm64, and X64; final
  application link/runtime execution still belongs to the consuming iOS app.

## Validation Corpus

- 45 curated gallery cases are rendered from identical source by Native
  Compose and Mermaid.js `12.0.0` with ELK, then captured on the same Android
  viewport.
- The Android Playground can render its current source and layout on demand
  through either Native Compose or a network-disabled WebView containing the
  locked Mermaid.js `12.0.0` bundle.
- 114 examples extracted from Mermaid's Flowchart documentation run through
  both Dagre and ELK in JVM tests.
- The only expected documentation-level unsupported cases are the two
  FontAwesome label examples, which return structured errors.
- Focused tests cover parser tables, FlowDB, Marked fixtures, HTML entities,
  sanitization, colors, all shapes, markers, D3 curves, numeric Dagre parity,
  nested ELK hierarchy, line hops, assets, interactions, viewport behavior,
  and resource limits.
- Android rendering has been installed and visually audited on a 1080 x 2280
  device. Core and Compose also compile for all configured iOS architectures.

## Performance Audit

The 2026-09-12 JVM audit used fresh Gradle test workers:

| Scenario | Result |
| --- | --- |
| QuickJS creation, 1.6 MiB elkjs worker load, and first layout | 0.964 s |
| 114 official Flowchart examples through ELK in one process | 1.745 s total |
| 500-edge chain at the default `maxEdges` boundary, including cold start | 1.908 s |

These values are observations, not CI thresholds. The runtime is initialized
once, cached for subsequent layouts, serialized by a coroutine `Mutex`, capped
at 256 MiB with a 2 MiB stack, and guarded by a 10-second layout timeout.

## Reference Workflow

`tools/official-reference/cases.mjs` is the source of truth for the 45-case
comparison gallery. `npm run render` produces:

- Mermaid.js PNG files under Android `drawable-nodpi`.
- The Android gallery catalog.
- JVM fixtures containing the same source.

`npm run generate:flowchart-doc-fixtures` extracts the 114 documentation
examples. Other generators pin parser tables, Marked rules, WHATWG entities,
and the elkjs worker by version and SHA-256.

`tools/capture-android-audit.sh` opens each case directly and captures Native
and Official views. It defaults to ELK; set `CAPTURE_LAYOUT=dagre` to compare
both renderers on the Dagre path. Screenshots are written only to ignored
`captures/local/` paths and are not distributed.
