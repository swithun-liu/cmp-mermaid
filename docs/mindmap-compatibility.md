# Mindmap Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Mindmap must preserve hierarchy, sibling order, node shapes, text,
branch colors, edge curves, title, layout selection, and sizing closely enough
that a side-by-side comparison does not expose a functional rendering defect.

Legal Mermaid input that depends on unavailable browser or plugin behavior
returns `MermaidError.UnsupportedFeature`; it is not silently discarded or
rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `mindmap`, frontmatter, comments, title, config precedence, and Unicode |
| Parser | Supported | Kotlin Jison `0.4.18` runtime with generated Mermaid `mindmap.jison` tables and semantic actions |
| Hierarchy | Supported | Indentation-based parent selection, irregular indentation, deep and wide trees, and deterministic source order |
| Shapes | Supported | Default, square, rounded rectangle, circle, cloud, bang, and hexagon |
| Text | Supported with explicit boundaries | Mermaid/Marked emphasis, entities, explicit HTML breaks, wrapping, and measured node sizing |
| CoSE-Bilkent | Supported | Pure Kotlin translation of Mermaid's deterministic flat-tree CoSE path |
| Dagre | Supported | Pure Kotlin Graphlib/Dagre translation with top-to-bottom ranks and routed edges |
| Tidy tree | Supported | Pure Kotlin translation of Mermaid's bidirectional non-layered tidy-tree adapter |
| Themes | Supported | All 11 built-in themes, section palettes, root styling, gradients, and Mindmap theme-variable overrides |
| Configuration | Supported | `padding`, `maxNodeWidth`, `useMaxWidth`, and `layoutAlgorithm` |
| Resource controls | Supported | Text and edge limits return structured `MermaidError` values |

## Explicit Unsupported Boundaries

The following legal Mermaid capabilities return `UnsupportedFeature`:

- `::icon(...)`, because the production runtime has no registered browser icon
  pack.
- `:::className`, because arbitrary CSS classes cannot be represented by the
  typed SceneGraph.
- `layout: elk` and every named `elk.*` algorithm. Production modules contain
  no JavaScript engine and never substitute another layout.
- Unknown Mindmap layout engines.
- Browser-only label content and unsupported CSS inherited from the shared
  text and style contract.

## Validation Corpus

- All 13 examples extracted from Mermaid's official Mindmap documentation are
  exercised. Ten render natively; two icon examples and one CSS-class example
  assert the exact structured unsupported boundary.
- 13 independent production scenarios cover all 22 declared Mindmap
  capability points.
- The replacement production audit accepted all 13 pairs:
  `9 pass / 4 manually reviewed / 0 fail`; geometry ratios are width
  `0.930-1.079`, height `0.856-1.049`, and foreground ink `0.759-1.037`.
- The replacement 256-case Native/Official audit accepted all pairs:
  `149 pass / 107 manually reviewed / 0 fail`; geometry ratios are width
  `0.956-1.155`, height `0.861-1.212`, and foreground ink `0.771-1.431`.
  The reviews are CoSE-Bilkent branch rotations or mirrors under platform
  text-size perturbations; every expected node, label, hierarchy edge, shape,
  and section color is preserved, with no clipping, overlap, or paint-order
  mismatch. All 16 contact sheets were manually reviewed.
- 256 deterministic randomized Native inputs exercise CoSE-Bilkent, Dagre, and
  tidy-tree layouts with finite geometry and deterministic replay.
- All 11 built-in themes are rendered by the shared production theme matrix.

## Reference Workflow

Regenerate parser tables from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:mindmap-parser
```

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:mindmap-doc-fixtures
```

Capture the 256-case Mindmap partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=mindmap \
OUTPUT_DIR=captures/local/mindmap-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
