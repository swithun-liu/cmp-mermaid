# Entity Relationship Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Entity Relationship diagram must preserve entities, aliases,
attributes, keys, comments, relationship labels, cardinalities,
identification, direction, subgraphs, styles, and metadata closely enough that
a side-by-side comparison does not expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not silently
discarded or rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `erDiagram`, frontmatter, directives, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `erDiagram.jison` tables, 86 semantic productions, and 83 lexer rules |
| Entities | Supported | Bare, quoted, numeric, Unicode, hyphenated, aliased, repeated, and relationship-created entities |
| Attributes | Supported | Types, generic types, nullable types, names, quoted comments, and empty attribute blocks |
| Attribute keys | Supported | `PK`, `FK`, `UK`, comma-separated combinations, and table columns |
| Relationships | Supported | Identifying and non-identifying relationships, labels, self-relationships, and repeated relationships |
| Cardinalities | Supported | Only one, zero or one, one or more, and zero or more with Mermaid 12 crow-foot geometry |
| `MD_PARENT` | Upstream-equivalent | The grammar and DB retain it; Mermaid 12's unified ER renderer does not register the legacy diamond marker, so no marker is painted |
| Subgraphs | Supported | Root, nested, labelled, relationship-connected, and direction-aware subgraphs with parent-before-child paint order |
| Direction | Supported | TB, BT, LR, and RL at root and nested subgraph levels |
| Layout | Supported with explicit boundary | Pure Kotlin Dagre is the Native default; every ELK selector returns `UnsupportedFeature("ELK layout")` |
| Markdown and HTML text | Supported with explicit boundaries | Shared Mermaid/Marked text path for entity aliases, attributes, relationship labels, and subgraph labels |
| Styles | Supported with explicit boundaries | `style`, `classDef`, `class`, inline `:::`, fill, stroke, dash, text, font, decoration, and line-height properties |
| Themes | Supported | Mermaid 12 redux-color palette, alternating attribute rows, edge-label backgrounds, neo shadows, and shared native theme variables |
| ER configuration | Supported | `diagramPadding`, `entityPadding`, `minEntityWidth`, `minEntityHeight`, `nodeSpacing`, `rankSpacing`, and `titleTopMargin` |
| Resource controls | Supported | Relationships are bounded by host-owned `maxEdges` |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities return `UnsupportedFeature`:

- KaTeX labels.
- FontAwesome substring replacement.
- Inline HTML links, images, SVG, MathML, or DOM layout elements inside labels.
- `look: handDrawn`, because Mermaid implements it through roughjs.
- CSS properties that the shared native style adapter cannot represent.
- Layout engines other than Dagre, including `elk` and every named `elk.*`
  algorithm.

## Validation Corpus

- 24 examples extracted from Mermaid's official ER documentation run through
  both the translated parser and native renderer in JVM tests.
- Semantic tests cover aliases, attributes, nullable and generic types, keys,
  comments, every cardinality, identification, styles, nested subgraphs,
  configuration, title, and accessibility.
- 256 deterministic random legal ER diagrams use Dagre while checking finite
  bounds, entity and relationship retention, marker
  validity, self-relationships, and repeated relationships.
- 20 curated gallery cases render identical source through Native Compose and
  Mermaid.js `12.0.0` with Dagre.
- The 20 Native Android screenshots are compared side by side with 20
  Puppeteer-rendered official references.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate the parser tables:

```bash
cd tools/official-reference
npm run generate:er-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:er-doc-fixtures
```

Render the 20 official Dagre references:

```bash
cd tools/official-reference
OUTPUT_DIR=captures/local/er-official \
  npm run render:er-gallery
```

Capture the same demo IDs with `tools/capture-android-audit.sh`, then generate
side-by-side sheets with `npm run render:er-contact-sheet`. All screenshots
remain under ignored `captures/local/` paths.
