# State Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported State diagram must preserve states, transitions, labels, start/end
markers, pseudostates, notes, composite hierarchy, concurrent regions,
direction, styles, links, and metadata closely enough that a side-by-side
comparison does not expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not silently
discarded or rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `stateDiagram`, `stateDiagram-v2`, frontmatter, directives, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `stateDiagram.jison` tables and all 49 semantic productions |
| States and descriptions | Supported | Implicit/explicit states, quoted aliases, colon descriptions, repeated descriptions, spaces through aliases, and Unicode |
| Transitions | Supported | Directed transitions, transition labels, self-transitions, repeated transitions, and implicit endpoint creation |
| Start and end | Supported | Root and nested `[*]` markers with parent-scoped deterministic ids |
| Pseudostates | Supported | Choice, fork, and join declarations with native Mermaid-equivalent shapes |
| Composite states | Supported | Named, nested, sibling, and transition-connected composites |
| Concurrency | Supported | `--` region splitting, nested region hierarchy, and dashed region boundaries |
| Notes | Supported | Single-line and multiline notes, left/right edge direction, folded-note geometry, and dashed connectors |
| Direction | Supported | TB, BT, LR, and RL at root and nested composite levels |
| Layout | Supported | Mermaid's default ELK path, named ELK algorithms, and explicit Dagre override |
| Markdown and HTML text | Supported with explicit boundaries | Shared Mermaid/Marked text path for states, descriptions, transitions, notes, and composite labels |
| Styles | Supported with explicit boundaries | `style`, `classDef`, `class`, inline `:::`, fill, stroke, dash, text, font, decoration, and line-height properties |
| Themes | Supported | Mermaid 12 redux-color palette, note colors, group color slots, neo appearance, and shared native theme variables |
| Links and tooltips | Supported | Sanitized `click`/`href` data is exposed as `SceneNodeInteraction`; the host owns navigation |
| State configuration | Supported | `padding`, `wrappingWidth`, `minNodeWidth`, `nodeSpacing`, `rankSpacing`, and `titleTopMargin` |
| Resource controls | Supported | Transitions are bounded by host-owned `maxEdges` |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities return `UnsupportedFeature`:

- KaTeX labels.
- FontAwesome substring replacement.
- Inline HTML links, images, SVG, MathML, or DOM layout elements inside labels.
- `look: handDrawn`, because Mermaid implements it through roughjs.
- CSS properties that the shared native style adapter cannot represent.
- Layout engines other than the connected Dagre and ELK algorithms.

State links are represented as SceneGraph interactions. The host owns
navigation; the native renderer does not execute arbitrary URLs or JavaScript.

## Validation Corpus

- 22 examples extracted from Mermaid's official State documentation run
  through both the translated parser and native renderer in JVM tests.
- Semantic tests cover descriptions, Markdown, start/end, choice, fork/join,
  composites, concurrency, notes, styles, links, title, and accessibility.
- 256 deterministic random legal State diagrams alternate between Dagre and
  ELK while checking finite bounds, shapes, routed edges, nested composites,
  and concurrent regions.
- 25 curated gallery cases render identical source through Native Compose and
  Mermaid.js `12.0.0` with ELK.
- The 25 Native Android screenshots are compared side by side with 25
  Puppeteer-rendered official references.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate the parser tables:

```bash
cd tools/official-reference
npm run generate:state-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:state-doc-fixtures
```

Render the 25 official ELK references:

```bash
cd tools/official-reference
OUTPUT_DIR=captures/local/state-official \
  npm run render:state-gallery
```

Capture the same demo IDs with `tools/capture-android-audit.sh`, then generate
side-by-side sheets with `npm run render:state-contact-sheet`. All screenshots
remain under ignored `captures/local/` paths.
