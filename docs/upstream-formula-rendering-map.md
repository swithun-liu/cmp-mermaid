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

### Mixed-label translation inventory

This inventory was established before enabling mixed-label rendering. The
implementation follows the listed upstream stages; browser formatting is a
declared platform adaptation. It is not a full formula compatibility claim.

| Order | Upstream production file / function / branch | Native translation boundary | Classification |
| ---: | --- | --- | --- |
| 1 | `diagrams/common/common.ts -> katexRegex`, `hasKatex` | Retain the global, non-dotall delimiter pattern, including empty matches | Exact |
| 2 | `rendering-util/createText.ts -> createText`, `inputForKatex` | Replace doubled backslashes in the original label; bypass Markdown conversion for labels containing KaTeX | Exact |
| 3 | `rendering-util/createText.ts -> addHtmlSpan`, `sanitizedLabel` | Replace `common.lineBreakRegex` matches with newline **before** calling the KaTeX stage | Exact |
| 4 | `diagrams/common/common.ts -> renderKatexUnsanitized`, initial `hasKatex` branch | Preserve text that contains no match | Exact |
| 5 | `renderKatexUnsanitized -> isMathMLSupported` and `injected.includeLargeFeatures` | Existing optional provider selection and typed unsupported fallback | Platform adaptation |
| 6 | `renderKatexUnsanitized -> split(lineBreakRegex)`, per-line `hasKatex`, wrapper construction, `join('')` | Retain actual ordering: `createText` has already normalized `<br>` to newline, so newline does **not** create separate rows; browser `nowrap` collapses it | Exact |
| 7 | `renderKatexUnsanitized -> replace(katexRegex)`, `katex.renderToString` | Replace **each** match, retaining ordinary surrounding content; request block mode, intrinsic metrics and drawing content per match | Platform adaptation |
| 8 | `renderKatexUnsanitized`, remove output newlines and `<annotation>` | Provider artifacts are typed drawing content; accessibility source stays separate from painted content | Platform adaptation |
| 9 | `renderKatexSanitized -> sanitizeText -> sanitizeMore / DOMPurify` | Reuse the translated HTML sanitization/styled-text boundary for ordinary content; never parse provider output as HTML | Platform adaptation |
| 10 | `createText.ts -> addHtmlSpan`, span and div `applyStyle` | Preserve ordinary text styles and inherit formula font size and color from the owning label | Platform adaptation |
| 11 | `addHtmlSpan`, `display:table-cell`, `nowrap`, `line-height:1.5`, finite `max-width` | Keep the outer label box separate from intrinsic content; owning diagram retains its width and padding projection | Platform adaptation |
| 12 | `renderKatexUnsanitized`, `display:flex`, `align-items:center`, `justify-content:center`, `white-space:nowrap` | Compose replaces browser layout: anonymous text items collapse surrounding CSS whitespace; intrinsic item widths sum; item cross-axis boxes center in the maximum item height | Platform adaptation |
| 13 | `addHtmlSpan -> fastdom.measure(getBoundingClientRect)`, equality-to-width and `minWidth` branches | Resolved complete-label dimensions enter graph layout before painting; no coordinate compensation | Platform adaptation |
| 14 | Browser `foreignObject` paint and inherited transforms | Paint each ordinary text/formula item in the measured label coordinate system; preserve rotation and clipping | Platform adaptation |
| 15 | Provider exception, invalid output, cancellation | Existing typed per-request failure and cancellation propagation; failed formulas remain readable ordinary text | Platform adaptation |
| 16 | `sequenceRenderer.ts -> calculateMathMLDimensions`, `sequence/svgDraw.js -> drawKatex` | Separate measurement and placement call chain; not covered by shared `createText` | Unsupported |
| 17 | ZenUML comment preflight before `createText` | Separate width/vertical-coordinate stage | Unsupported |
| 18 | Browser legacy HTML-and-MathML selection; native KaTeX parser/macros | Provider-specific behavior; not implemented by the pure Kotlin label translator | Unsupported |

The browser probe `build/formula-label-probe.json` confirms the ordering in
rows 3 and 6 against the locked official runtime. In particular,
`energy<br/>$$E=mc^2$$` has one flex row, and `$$x$$<br/>$$y$$`
has two centered formula items in that row. Native code must retain this
Mermaid 12.0.0 behavior rather than inventing newline-based formula rows.

### Requirement label translation inventory

Established before enabling formula requests for Requirement labels.

| Order | Upstream production function / stage | Native boundary | Classification |
| ---: | --- | --- | --- |
| 1 | `requirementBox.ts -> addText`, empty input | Return zero dimensions without creating text | Exact |
| 2 | `addText -> decodeEntities -> sanitizeText -> createText` | Existing entity/sanitization boundary, shared per-match formula projection | Platform adaptation |
| 3 | `utils.ts -> calculateTextWidth -> calculateTextDimensions -> drawSimpleText` | Measure literal input for the configured and sans-serif fonts, split on `lineBreakRegex`, round each line, select the same dimension record; add 50 logical pixels | Platform adaptation |
| 4 | `addText`, HTML branch: `getBoundingClientRect`, alignment, foreignObject width/height | Preserve intrinsic flex composition and outer label dimensions; no extra text line box for formula-only content | Platform adaptation |
| 5 | `addText`, SVG branch: descendant styles, start anchor, `getBBox + 6` | Existing SVG text adapter; formulas remain text with HTML labels disabled | Platform adaptation |
| 6 | `addText`, `translate(-width/2, -height/2 + yOffset)` | Measured dimensions determine each centered label before graph layout | Exact |
| 7 | `requirementBox`, type/name/body order, accumulated heights, gap, group bounds + padding | Existing diagram stages consume the resolved dimensions in the same order | Exact |
| 8 | `requirementBox`, retranslate body labels and insert divider | Preserve diagram-owned placement independently of the flex container's center alignment | Platform adaptation |

Shared mixed HTML composition records sanitized **direct element** ranges.
Nested inline styles stay within that element's formatting context. Formula
children nested inside user HTML and block formatting with margins are explicit
`UnsupportedFeature` results until those browser formatting stages are translated.

| Order | Upstream file and function | Kotlin stage | Status |
| ---: | --- | --- | --- |
| 1 | `diagrams/common/common.ts -> katexRegex`, `hasKatex` | `MermaidTextPort.render` | **Exact**: only Mermaid's non-multiline `$$...$$` syntax enters the formula path |
| 2 | `rendering-util/createText.ts -> createText`: replace `\\\\` with `\\` before formula rendering | `MermaidTextPort.renderKatexPlainText` and formula metadata | **Exact** |
| 3 | `diagrams/common/common.ts -> renderKatexUnsanitized`: `displayMode: true` | `MermaidFormulaSource` | **Platform adaptation**: a provider-neutral block-display request replaces the KaTeX call |
| 4 | `diagrams/common/common.ts -> renderKatexSanitized` | Host `MermaidFormulaRenderer` failure contract | **Platform adaptation**: providers return a typed result; no provider output is interpreted as HTML |
| 5 | `rendering-util/createText.ts -> addHtmlSpan`: measure the KaTeX `<math>` content and surrounding table-cell div before graph layout | formula-aware `TextMetricProvider` plus diagram-owned label-box projection | **Platform adaptation**: the provider returns intrinsic formula metrics; Kotlin reproduces wrapping width and CSS label padding separately |
| 6 | Browser `foreignObject` and MathML paint | formula SceneGraph metadata and the Compose formula painter | **Platform adaptation** for single, mixed, and multiple formula items; intrinsic provider output is aligned inside the translated outer label box without non-uniform scaling |
| 7 | Missing MathML support or a KaTeX exception | plain-text formula source and `MermaidDiagnostic.UnsupportedFeature` | **Platform adaptation**: one failed formula cannot replace the complete diagram with an error rendering |

## Explicit Unsupported Stages

| Upstream production stage | Current status |
| --- | --- |
| KaTeX parser, macro expansion, MathML generation, and sanitization | **Unsupported** in this repository; an optional provider owns formula parsing and drawing |
| `sequenceRenderer.ts -> calculateMathMLDimensions` branches in actor, message, note, and spacing preflight | **Unsupported**; Sequence keeps its existing text path until the complete formula-aware layout stage is translated |
| `sequence/svgDraw.js -> drawKatex` placement for notes, messages, and loop labels | **Unsupported**; no approximate coordinate patch is substituted |
| Formula children nested inside user HTML elements | **Unsupported**; the recursive browser formatting context is not translated |
| Mixed-label HTML block formatting with margins, such as `<p>` | **Unsupported**; no flattened-text substitute is used |
| Event Modeling structured labels | **Unsupported** by the provider path; their separate measurement and placement stages remain untranslated |
| ZenUML comment geometry preflight before Markdown text rendering | **Unsupported**; comments keep plain-text fallback until formula dimensions participate in width and vertical-coordinate construction |
| Mermaid `legacyMathML` and `forceLegacyMathML` browser output selection | **Unsupported** because native providers do not consume browser MathML modes |

Formula rendering therefore remains non-Stable. A provider-backed result is
used for each `$$...$$` match in supported shared-text labels when the provider
returns finite positive logical dimensions. Ordinary content and direct HTML
inline elements retain their own flex items. Unsupported call chains and
provider failures remain explicit through structured results or visible
plain-text fallback diagnostics.

### Kotlin function mapping

| Upstream stage | Native file / function |
| --- | --- |
| `createText`, `addHtmlSpan`, per-match KaTeX replacement | `MermaidTextPort.kt -> render`, `renderKatexPlainText` |
| Sanitized DOM direct-child formatting context | `MermaidTextPort.kt -> renderHtmlFragment`, `HtmlStyleStack` |
| Emitted flex container layout | `MermaidKatexLabelLayout.kt -> measure`, `appendTextItems`, `alignFlexItems` |
| Intrinsic formula request and diagram-owned outer box | `MermaidEngine.kt -> FormulaAwareTextMetricProvider.measure`; `TextMetrics.formulaLayout` |
| `calculateTextWidth`, `calculateTextDimensions`, `drawSimpleText` | `MermaidTextDimensionsPort.kt`, same named stages; typed measurement failures and cancellation propagation |
| Requirement `addText` and field accumulation | `RequirementLayout.kt -> measureText` and existing box-building stages |
| Browser label measurement and paint | `MermaidDiagram.kt -> rememberMermaidPreparedScene`, `drawSceneFormulaLabel`, `formulaLabelOrigin` |

Paths above are relative to their existing `core/flowchart/upstream/mermaid`,
`core`, `core/requirement`, or `compose` source package. Tests cover delimiter
selection, normalized line-break ordering, repeated formulas, CSS whitespace,
direct HTML children, descendant styles, font inheritance, failed items,
unsupported nested formatting, and asynchronous relayout.

## Connected Native Paths

The shared provider path is connected only where the translated renderer keeps
the same text measurement object through final `SceneText` construction:

- Flowchart and its Agentflow and Swimlanes adapters;
- Block, Class, State, Entity Relationship, Kanban, Mindmap, Use Case, and Requirement
  shared-text call sites.

This list describes connected call sites, not full-family formula parity.
Event Modeling keeps plain-text fallback. ZenUML comments and Sequence remain explicitly
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
  mixed/multiple formula composition, resolved dimensions, per-item failed-renderer fallback, and
  request propagation through Flowchart, Class, State, ER, Kanban, and Use Case
  labels, plus the explicit ZenUML and Sequence fallback boundaries.
- Compose tests cover renderer priority, cache identity, invalid artifacts,
  exception conversion, cancellation, asynchronous relayout, resolution-limit
  fallback, request-scoped failures across width and color variants,
  horizontal alignment, and plain-text fallback.
- `tools/official-reference/formula-metrics.mjs` and the locked
  `fixtures/formula-metrics-mermaid-12.0.0.json` compare the KaTeX content box
  with the Mermaid outer layout box under the matching `redux-color` theme.
  The fixture binds the existing Noto Sans Math resource for reproducible
  MathML glyph metrics; its earlier system-font measurements are superseded.
  The fixture records Flowchart fixed wrapping width, State's unpadded edge
  label, Kanban's theme-derived font size, and Use Case horizontal CSS padding.
- The existing 8,448 Native/Official visual pairs were generated without a
  provider-backed formula corpus. Their counts and passing status remain valid
  for their recorded sources, but they are not evidence for formula visual
  parity and do not make this capability Stable.
- A dedicated [shared-label audit](assets/mixed-formula-report/index.html)
  uses 28 identical sources, 56 Native/Official diagram screenshots, four
  contact sheets, and per-item/label geometry plus per-edge topology checks.
  Native receives official intrinsic formula images and dimensions through
  the public provider to isolate Kotlin composition and Canvas placement.
  This is not evidence for a native KaTeX parser, an arbitrary host formula
  engine, or physical-device formula parity. Remaining Review findings are
  retained in the machine-readable report.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/mermaid/src/diagrams/common/common.ts` | `bf6a0cc85de418bb8171735a7317fd7e84f8ad79f4e975e378130f11728efb67` |
| `packages/mermaid/src/rendering-util/createText.ts` | `711f26bbda94ae9b8020e5aa59e8b41692c8ae569de6fe080000dac4719f5536` |
| `packages/mermaid/src/utils.ts` | `bf89cad0bbb940a84c1588b4ec2a778edd38ca14a8a5c3248c850697c574ae25` |
| `packages/mermaid/src/rendering-util/rendering-elements/shapes/requirementBox.ts` | `e6137cf2dcd3cec06309e21142e147fa28ef16e414d95befc67e95d2cf05c23e` |
| `packages/mermaid/src/rendering-util/rendering-elements/edges.js` | `c65b94839cded66fc155279bc120df9e7efa0e342f34a4afc12743aa16a4b9ec` |
| `packages/mermaid/src/diagrams/class/shapeUtil.ts` | `ed768affadef0163dce386f6bbeffdc0b49d0e8188d23d4c72a5132da581ed43` |
| `packages/mermaid/src/diagrams/sequence/sequenceRenderer.ts` | `dc715189fee677a89badd2c2400a99718aaff8e189c81fa07dd28ec5f2aa66af` |
| `packages/mermaid/src/diagrams/sequence/svgDraw.js` | `002f90315d0e921a4212cc3a8e02ad4747e5d497243dc8bd83a934a41921fa3c` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
