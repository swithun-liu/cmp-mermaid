# Class Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Class diagram must preserve classes, compartments, annotations,
relations, markers, labels, cardinalities, notes, namespaces, styles, links,
direction, and layout closely enough that a side-by-side comparison does not
expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not silently
discarded or rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `classDiagram`, `classDiagram-v2`, frontmatter, directives, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `classDiagram.jison` tables and all 110 semantic productions |
| Classes and aliases | Supported | Implicit/explicit classes, quoted labels, Unicode, annotations, and generic types |
| Compartments | Supported | Attributes, methods, visibility, static/abstract classifiers, and empty-compartment configuration |
| Relations | Supported | Association, aggregation, composition, extension, dependency, lollipop, reverse, bidirectional, dotted, and solid forms |
| Relation labels | Supported | Center labels, start/end cardinalities, self-relations, and marker-aware endpoint placement |
| Notes | Supported | Standalone and class-attached notes with dotted relation routing |
| Namespaces | Supported | Explicit, dotted, nested, labelled, hierarchical, and compact namespace modes |
| Direction | Supported | TB, BT, LR, and RL |
| Layout | Supported | Mermaid's default ELK path, named ELK algorithms, and explicit Dagre override |
| Markdown and HTML text | Supported with explicit boundaries | Mermaid/Marked text path for class labels, members, notes, namespace labels, lollipop labels, and relation labels |
| Styles | Supported with explicit boundaries | `style`, `classDef`, fill, stroke, dash, text, font, decoration, and line-height properties mapped by the shared style adapter |
| Themes | Supported | Mermaid 12 class color slots, note colors, group colors, redux/neo appearance, and shared native theme variables |
| Links, callbacks, and tooltips | Supported | Exposed as `SceneNodeInteraction`; callbacks are retained only at Mermaid's loose security level |
| Resource controls | Supported | Relations are bounded by host-owned `maxEdges` |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities return `UnsupportedFeature`:

- KaTeX labels.
- FontAwesome substring replacement.
- Inline HTML links, images, SVG, MathML, or DOM layout elements inside labels.
- `look: handDrawn`, because Mermaid implements it through roughjs.
- CSS properties that the shared native style adapter cannot represent.

Class links and callbacks are represented as SceneGraph interactions. The host
owns navigation and callback execution; the native renderer does not execute
arbitrary URLs or JavaScript.

## Validation Corpus

- 38 examples extracted from Mermaid's official Class documentation run in JVM
  tests.
- Mermaid's dense relation matrix runs through both Dagre and ELK.
- 256 deterministic random legal Class diagrams alternate between Dagre and
  ELK while checking shape/edge counts, finite bounds, and routed paths.
- 27 curated gallery cases render identical source through Native Compose and
  Mermaid.js `12.0.0` with ELK.
- The 27 Native Android screenshots were compared side by side with 27
  Puppeteer-rendered official references, including markers, cardinalities,
  self-relations, notes, namespaces, styles, title, Unicode, and a dense model.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate the parser tables:

```bash
cd tools/official-reference
npm run generate:class-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:class-doc-fixtures
```

Render the 27 official ELK references:

```bash
cd tools/official-reference
CLASS_LAYOUT=elk OUTPUT_DIR=captures/local/class-official \
  npm run render:class-gallery
```

Capture the same demo IDs with `tools/capture-android-audit.sh`, then generate
side-by-side sheets with `npm run render:class-contact-sheet`. All screenshots
remain under ignored `captures/local/` paths.
