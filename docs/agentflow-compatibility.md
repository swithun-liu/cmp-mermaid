# Agentflow Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Agentflow diagram must preserve typed work nodes, sequence/reference/
failure edges, nested and collapsed flows, global nodes, connectors, metadata,
diagnostics, configuration, and theme colors closely enough that side-by-side
review exposes no functional defect.

Agentflow is beta in Mermaid `12.0.0`; its `agentflow-beta` syntax may change
in a later upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `agentflow-beta`, five directions, frontmatter, init directives, comments, title, accessibility metadata, entities, and Unicode |
| Parser | Supported | Generated Jison tables, nodes and labels, chained and fan-out edges, `flow`/`global`/`connector` blocks, metadata, classes/styles, and structured source locations |
| Database and semantics | Supported | Source mappings, typed semantic nodes and edges, connector references, metadata attachment, duplicate handling, containment protection, and structured diagnostics |
| Shapes | Supported | Six domain aliases plus the Mermaid 12.0.0 Agentflow canonical catalogue, deterministic kind classification, and removed/unsupported-shape fallback diagnostics |
| Edges | Supported | Sequence `-->`, reference `-.-`, failure `--x`, labels, chains, fan-out, minimum length, classes, interpolation metadata, markers, and collapsed-boundary redirection |
| Containers | Supported | Nested flows, local directions, global membership, declaration-order palette slots, collapsed summaries, hidden descendants, and boundary edge preservation |
| Connectors and metadata | Supported | Connector declarations, bare/dotted/URL `connectorRef` values, single-line and multiline metadata, unknown-key preservation, and prototype-key filtering |
| Dagre layout and rendering | Supported | Agentflow data is projected into the translated unified Flowchart Dagre layout and Compose SceneGraph renderer; the same Dagre mode drives Native and Official visual evidence |
| Configuration | Supported | `theme`, `look`, `titleTopMargin`, `diagramPadding`, `nodeSpacing`, `rankSpacing`, `wrappingWidth`, `minNodeWidth`, and `useMaxWidth`, including frontmatter/init precedence |
| Themes and sizing | Supported | Mermaid's Agentflow theme selection, kind/container color slots, `flowContainerStroke`, classic/neo appearance, responsive/intrinsic sizing, and mixed-script text |
| ELK layout | Explicitly unsupported | Mermaid's Agentflow default is ELK. Until ELK has a pure Kotlin translation, an effective `layout: elk` returns `MermaidError.UnsupportedFeature`; evidence sources explicitly select Dagre |
| Hand-drawn look | Explicitly unsupported | Effective `look: handDrawn` returns `MermaidError.UnsupportedFeature` instead of approximating rough.js output |

## Validation Corpus

- All 12 active examples extracted from Mermaid's official Agentflow
  documentation run in JVM tests. Eleven render through Dagre; the documented
  ELK example verifies the explicit unsupported result.
- All eight upstream Agentflow conformance fixtures are regenerated from the
  pinned source tree and checked for diagnostics, source positions, semantic
  vertices, edges, containers, and connectors.
- 13 independent production scenarios cover all 32 declared Agentflow
  capability points.
- 256 deterministic same-source matrix cases exercise all structural seeds,
  20 visible text/layout-pressure profiles, directions, node kinds, edge
  semantics, nested/global/collapsed flows, connectors, metadata,
  configuration, themes, accessibility, entities, Unicode, and long labels.
- The production geometry audit passed all 13 pairs. Width, height, and
  foreground-ink ratios were `1.041-1.186`, `0.820-1.095`, and
  `0.766-1.206`.
- The production detail audit produced `9 pass / 4 review / 0 fail`. Two
  reviews are collapsed compound-shape stroke-pattern classifications; two
  are compound-shape element-count classifications. Manual review confirmed
  matching visible structure, labels, edge semantics, and paint order.
- The matrix geometry and detail audits passed all 256 pairs. Ratios were
  `1.040-1.222`, `0.854-1.086`, and `0.773-1.287`; detail was
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed for shapes, nested and collapsed containers, connectors, edge
  semantics, routing, markers, themes, labels, clipping, and paint order. Raw
  [production detail](assets/stability-report/agentflow-production-detail.json),
  [production geometry](assets/stability-report/agentflow-production-geometry.json),
  [matrix detail](assets/stability-report/agentflow-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/agentflow-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the parser, conformance fixtures, and documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 npm run generate:agentflow-parser
MERMAID_SOURCE_DIR=/path/to/mermaid-12 npm run generate:agentflow-conformance-fixtures
MERMAID_SOURCE_DIR=/path/to/mermaid-12 npm run generate:agentflow-doc-fixtures
```

Capture the 256-case Agentflow partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=agentflow \
OUTPUT_DIR=captures/local/agentflow-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
