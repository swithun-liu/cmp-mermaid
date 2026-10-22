# Upstream Formula Rendering Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production formula extension point is pure Kotlin and Compose
Multiplatform. It does not embed Mermaid.js, a browser DOM, a WebView, or a
JavaScript engine. Formula implementations are optional host dependencies and
are not part of `mermaid-core`.

## Production Call Sequence

| Order | Upstream file and function | Kotlin stage | Status |
| ---: | --- | --- | --- |
| 1 | `diagrams/common/common.ts -> katexRegex`, `hasKatex` | `MermaidTextPort.render` | **Exact**: only Mermaid's non-multiline `$$...$$` syntax enters the formula path |
| 2 | `rendering-util/createText.ts -> createText`: replace `\\\\` with `\\` before formula rendering | `MermaidTextPort.renderKatexPlainText` and formula metadata | **Exact** |
| 3 | `diagrams/common/common.ts -> renderKatexUnsanitized`: `displayMode: true` | `MermaidFormulaSource` | **Platform adaptation**: a provider-neutral block-display request replaces the KaTeX call |
| 4 | `diagrams/common/common.ts -> renderKatexSanitized` | Host `MermaidFormulaRenderer` failure contract | **Platform adaptation**: providers return a typed result; no provider output is interpreted as HTML |
| 5 | `rendering-util/createText.ts -> addHtmlSpan`: measure the KaTeX `<math>` content and surrounding table-cell div before graph layout | formula-aware `TextMetricProvider` plus diagram-owned label-box projection | **Platform adaptation**: the provider returns intrinsic formula metrics; Kotlin reproduces wrapping width and CSS label padding separately |
| 6 | Browser `foreignObject` and MathML paint | formula SceneGraph metadata and the Compose formula painter | **Platform adaptation** for one complete `$$...$$` label; intrinsic provider output is aligned inside the translated outer label box without non-uniform scaling |
| 7 | Missing MathML support or a KaTeX exception | plain-text formula source and `MermaidDiagnostic.UnsupportedFeature` | **Platform adaptation**: one failed formula cannot replace the complete diagram with an error rendering |

## Explicit Unsupported Stages

| Upstream production stage | Current status |
| --- | --- |
| `renderKatexUnsanitized` labels containing ordinary text around a formula or more than one `$$...$$` block | **Unsupported** by the provider path; rendered as documented plain text because native mixed text/formula composition is not translated |
| KaTeX parser, macro expansion, MathML generation, and sanitization | **Unsupported** in this repository; an optional provider owns formula parsing and drawing |
| `sequenceRenderer.ts -> calculateMathMLDimensions` branches in actor, message, note, and spacing preflight | **Unsupported**; Sequence keeps its existing text path until the complete formula-aware layout stage is translated |
| `sequence/svgDraw.js -> drawKatex` placement for notes, messages, and loop labels | **Unsupported**; no approximate coordinate patch is substituted |
| Requirement labels containing formula syntax | **Unsupported** by the whole-label provider path because legal user text is prefixed with field or relation text; it remains the documented mixed-label plain-text fallback |
| ZenUML comment geometry preflight before Markdown text rendering | **Unsupported**; comments keep plain-text fallback until formula dimensions participate in width and vertical-coordinate construction |
| Mermaid `legacyMathML` and `forceLegacyMathML` browser output selection | **Unsupported** because native providers do not consume browser MathML modes |

Formula rendering therefore remains non-Stable. A provider-backed result is
used only when the whole label is one formula block and the provider returns
finite positive logical dimensions. Every other legal formula case remains
explicitly visible through plain-text fallback and diagnostics.

## Connected Native Paths

The shared provider path is connected only where the translated renderer keeps
the same text measurement object through final `SceneText` construction:

- Flowchart and its Agentflow and Swimlanes adapters;
- Block, Class, State, Entity Relationship, Kanban, Mindmap, and Use Case
  shared-text call sites.

This list describes connected call sites, not full-family formula parity.
Event Modeling and Requirement combine user text with structured labels, so
they keep plain-text fallback. ZenUML comments and Sequence remain explicitly
unsupported for provider rendering because their separate geometry,
measurement, and placement stages are listed above.

## Native Provider Boundary

- The request carries formula source, block display mode, logical maximum
  content width, computed CSS font size, color, and raster scale.
- The result carries intrinsic logical width, height, baseline, accessibility
  text, and provider-owned Compose drawing content. These metrics correspond
  to KaTeX's `<math>` box, not the surrounding Mermaid HTML-label div.
- Mermaid wrapping width, background padding, and label alignment remain owned
  by the translated Flowchart, State, Use Case, and other diagram stages.
- The public contract contains no platform View, native image class, DOM node,
  or implementation-specific type.
- Provider selection is deterministic: an explicitly supplied host renderer
  wins over a future built-in renderer. A failure from the selected renderer
  falls back to plain text and does not silently switch implementations.
- Plain-text mode bypasses both host and built-in renderers.
- Formula resolution is asynchronous. Layout reruns with resolved dimensions
  before the final scene is painted.

## Verification Boundary

- Core tests cover Mermaid's formula token selection, whole-label metadata,
  mixed-label fallback, resolved dimensions, failed-renderer fallback, and
  request propagation through Flowchart, Class, State, ER, Kanban, and Use Case
  labels, plus the explicit ZenUML and Sequence fallback boundaries.
- Compose tests cover renderer priority, cache identity, invalid artifacts,
  exception conversion, cancellation, asynchronous relayout, resolution-limit
  fallback, request-scoped failures across width and color variants,
  horizontal alignment, and plain-text fallback.
- `tools/official-reference/formula-metrics.mjs` and the locked
  `fixtures/formula-metrics-mermaid-12.0.0.json` compare the KaTeX content box
  with the Mermaid outer layout box under the matching `redux-color` theme.
  The fixture records Flowchart fixed wrapping width, State's unpadded edge
  label, Kanban's theme-derived font size, and Use Case horizontal CSS padding.
- The existing 8,448 Native/Official visual pairs were generated without a
  provider-backed formula corpus. Their counts and passing status remain valid
  for their recorded sources, but they are not evidence for formula visual
  parity and do not make this capability Stable.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/mermaid/src/diagrams/common/common.ts` | `bf6a0cc85de418bb8171735a7317fd7e84f8ad79f4e975e378130f11728efb67` |
| `packages/mermaid/src/rendering-util/createText.ts` | `711f26bbda94ae9b8020e5aa59e8b41692c8ae569de6fe080000dac4719f5536` |
| `packages/mermaid/src/diagrams/class/shapeUtil.ts` | `ed768affadef0163dce386f6bbeffdc0b49d0e8188d23d4c72a5132da581ed43` |
| `packages/mermaid/src/diagrams/sequence/sequenceRenderer.ts` | `dc715189fee677a89badd2c2400a99718aaff8e189c81fa07dd28ec5f2aa66af` |
| `packages/mermaid/src/diagrams/sequence/svgDraw.js` | `002f90315d0e921a4212cc3a8e02ad4747e5d497243dc8bd83a934a41921fa3c` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
