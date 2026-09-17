# Requirement Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Requirement diagram must preserve requirement and element identity,
typed fields, relationship direction and type, node styling, labels, titles,
and accessibility metadata closely enough that a side-by-side comparison does
not expose a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `requirementDiagram`, `requirement`, frontmatter, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `requirementDiagram.jison` symbols, productions, LALR states, lexer rules, and conditions |
| Database | Supported | Source-order requirements, elements, relationships, classes, direct styles, direction, and metadata |
| Requirement types | Supported | Requirement, Functional Requirement, Interface Requirement, Performance Requirement, Physical Requirement, and Design Constraint |
| Requirement fields | Supported | Identifier, text, Low/Medium/High risk, and Analysis/Demonstration/Inspection/Test verification |
| Elements | Supported | Element name, type, document reference, classes, and direct styles |
| Relationships | Supported | Contains, copies, derives, satisfies, verifies, refines, and traces, including reverse-arrow syntax |
| Layout | Supported with explicit boundary | TB, BT, LR, and RL directions through the translated Dagre adapter; every ELK selector returns `UnsupportedFeature("ELK layout")` |
| Requirement boxes | Supported | Type/name headers, body fields, dividers, HTML-label line-box sizing, and final left-aligned body translation |
| Markers and edges | Supported | Requirement arrow and contains markers, labels, dotted relationships, and Neo marker spacing |
| Markdown and styling | Supported | Strong/emphasis labels, `style`, `classDef`, `class`, `:::`, and registered Requirement theme variables |
| Themes and look | Supported | All 11 built-in themes plus Classic and Neo looks |
| Resource controls | Supported | Text, node, and edge limits return structured `MermaidError` values |

## Intentional Native Boundaries

- Mermaid's RoughJS-backed `handDrawn` look is not translated. It returns
  `MermaidError.UnsupportedFeature` instead of silently drawing a Classic
  approximation.
- `layout: elk` and every named `elk.*` algorithm return
  `MermaidError.UnsupportedFeature`; Native never substitutes Dagre.
- Browser HTML labels map to measured Compose text. CMP Mermaid preserves the
  Mermaid `14px`/`21px` line box and label placement, while platform glyph
  metrics can still differ.
- SceneGraph output is static. Requirement diagrams define no
  Requirement-specific browser interaction contract to execute.
- Unsupported legal Mermaid features must return
  `MermaidError.UnsupportedFeature`; malformed Requirement syntax returns a
  structured parse error.

## Validation Corpus

- Every Requirement example extracted from Mermaid's official documentation
  runs in JVM tests.
- Focused parser, database, layout, marker, theme, metadata, styling, and error
  tests cover the translated upstream behavior.
- 256 deterministic random legal Requirement diagrams exercise types, fields,
  elements, relationships, directions, styles, metadata, and finite SceneGraph
  validation.
- 13 independent production scenarios cover all 18 declared Requirement
  capability points.
- 256 same-source Native/Official visual cases were captured at `1200 x 900`.
  The replacement audit accepted all pairs:
  `252 pass / 4 manually reviewed / 0 fail`, with 256/256 geometry passes.
  The four reviews are benign greedy cross-matches between duplicate
  `<<contains>>` or `<<satisfies>>` labels; both renderers retain all 44 text
  elements with no clipping or overlap and pass raster checks. All 16 contact
  sheets were manually reviewed.
- The 13 production scenarios pass the replacement detail audit with no review
  queue. Production width, height, and foreground-ink ratios are
  `1.011-1.078`, `1.003-1.055`, and `0.955-1.162`; matrix ratios are
  `1.013-1.072`, `1.004-1.053`, and `0.974-1.148`.
- Android Emulator, iOS Simulator, Desktop, and Web load tests traverse the
  complete mixed production corpus.

## Reference Workflow

Regenerate parser tables:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:requirement-parser
```

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:requirement-doc-fixtures
```

Capture the 256-case Requirement partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=requirement \
OUTPUT_DIR=captures/local/requirement-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
