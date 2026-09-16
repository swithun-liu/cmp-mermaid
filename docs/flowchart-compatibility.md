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
| Headers and directions | Supported with explicit boundary | `flowchart`, `graph`, TB/TD/BT/LR/RL, and symbolic aliases; `flowchart-elk` is recognized and returns `UnsupportedFeature("ELK layout")` |
| Preprocessing and configuration | Supported with explicit boundaries | Frontmatter, directives, comments, entity handling, config precedence, secure host keys, themes, and Flowchart options; ELK options are parsed for compatibility but cannot activate ELK |
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
| Routing: ELK | Unsupported | Every `elk` and `elk.*` request returns `UnsupportedFeature("ELK layout")`; Native never substitutes Dagre |
| Line hops | Supported | `arc`, `gap`, and disabled crossing treatment on supported Native routes |
| Subgraphs | Supported | Nested groups, collapsed groups, boundary links, local directions, title margins, and cross-hierarchy edges |
| Markdown strings | Supported for Mermaid's label path | Marked `16.4.2` lexer/token behavior used by Mermaid, including emphasis, code, deletion, blocks, escapes, and line breaks |
| HTML labels | Supported with explicit boundaries | Formatting spans, entities, sanitization, color/background, relative font sizes, alignment, decoration, and line height |
| Themes | Supported with explicit boundaries | Mermaid default, dark, forest, neutral, base, neo/redux variants, theme variables, color arrays, and default Flowchart appearance |
| Security levels | Supported | Strict, loose, antiscript, and sandbox URL/callback handling; diagram text cannot override secure host limits |
| Links, callbacks, and tooltips | Supported | Exposed as `SceneNodeInteraction`; callbacks are retained only at Mermaid's loose security level |
| Image nodes | Android embedded sources supported | Cached bitmap/SVG loading from `data:`; the production module contains no HTTP client; 8 MiB input and 4096 px decode bounds |
| Image nodes | External sources require a host provider | The public `MermaidAssetProvider` contract lets the host resolve approved assets; iOS/JVM install no platform default |
| Icon metadata nodes | Provider required | SceneGraph retains pack/name metadata; Android displays an explicit fallback when no icon pack is registered |
| Mobile gestures | Supported | One-finger parent scrolling remains available; two-finger pan/zoom is clipped and bounded |
| Resource controls | Supported | Host-owned `maxTextSize` and `maxEdges`; the production path has no JavaScript runtime |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities currently return
`UnsupportedFeature`:

- KaTeX labels.
- FontAwesome substring replacement and custom Iconify pack registration.
- Inline HTML `<img>`, `<a>`, `<svg>`, and MathML content inside labels.
- HTML layout tags whose DOM box behavior cannot be represented by text spans.
- `look: handDrawn`, because Mermaid implements it through roughjs.
- `layout: elk`, every named `elk.*` algorithm, and the `flowchart-elk`
  header, because production modules cannot execute the upstream JavaScript
  layout engine.
- `themeCSS` and `altFontFamily`.
- Unmapped CSS, including letter spacing, word spacing, text shadow, text
  transform, and white-space/word-breaking behavior.

Additional platform limitations:

- iOS and Desktop compile against the common asset-provider contract but do
  not ship default bitmap/SVG providers; external asset resolution is owned by
  the host application.
- Arbitrary system font-family discovery is host-defined. The Compose adapter
  bundles Arimo as an Arial-compatible default, Droid Sans Mono for HTML code
  spans, and accepts a custom `MermaidFontFamilyResolver`.
- Non-zero browser SVG blur is represented by the closest Compose shadow, not
  browser-filter pixel parity.
- iOS source sets compile for Arm64, Simulator Arm64, and X64; final
  application link/runtime execution still belongs to the consuming iOS app.

## Validation Corpus

- 45 curated gallery cases are rendered from identical source by Native
  Compose and Mermaid.js `12.0.0` with Dagre, then captured on the same Android
  viewport.
- The Android Playground can render its current source and layout on demand
  through either Native Compose or a network-disabled WebView containing the
  locked Mermaid.js `12.0.0` bundle.
- 114 examples extracted from Mermaid's Flowchart documentation run through
  Dagre in JVM tests; focused tests verify that every ELK selector returns the
  structured unsupported error.
- The only expected documentation-level unsupported cases are the two
  FontAwesome label examples, which return structured errors.
- Focused tests cover parser tables, FlowDB, Marked fixtures, HTML entities,
  sanitization, colors, all shapes, markers, D3 curves, numeric Dagre parity,
  compound Dagre hierarchy, line hops, assets, interactions, viewport behavior,
  and resource limits.
- Android rendering has been installed and visually audited on a 1080 x 2280
  device. Core and Compose also compile for all configured iOS architectures.

## Performance Audit

The current JVM audit uses fresh Gradle test workers:

| Scenario | Result |
| --- | --- |
| Production Core JavaScript engines or bundles | None |
| Production corpus soak | 790 renders, 416 ms total, 1 ms P95 |
| Retained heap after forced GC | 25,312 bytes |

The soak enforces a 45-second total budget, a 500 ms P95 budget, and a
64 MiB retained-heap budget.

## Reference Workflow

`tools/official-reference/cases.mjs` is the source of truth for the 45-case
comparison gallery. `npm run render` produces:

- Mermaid.js PNG files under Android `drawable-nodpi`.
- The Android gallery catalog.
- JVM fixtures containing the same source.

`npm run generate:flowchart-doc-fixtures` extracts the 114 documentation
examples. Other generators pin parser tables, Marked rules, and WHATWG
entities by version and SHA-256.

`tools/capture-android-audit.sh` opens each case directly and captures Native
and Official views on the Dagre path. Screenshots are written only to ignored
`captures/local/` paths and are not distributed.
