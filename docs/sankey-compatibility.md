# Sankey Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Sankey diagram must preserve CSV records, first-seen node order,
directed flow values, D3 Sankey node and link geometry, node alignment, label
placement, value formatting, link and node colors, paint order, configuration,
and theme styling closely enough that a side-by-side comparison does not expose
a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `sankey`, `sankey-beta`, frontmatter, comments, blank-line normalization, frontmatter title, and quoted Unicode |
| CSV parser | Supported | Exactly three source/target/value fields, quoted commas, escaped double quotes, embedded quoted newlines, and JavaScript `parseFloat` value semantics |
| Database | Supported | First-seen node order, decoded node IDs, link insertion order, graph projection, and reset behavior |
| Node geometry | Supported | D3 Sankey `0.12.3` values, depths, heights, layers, collision resolution, six relaxation passes, node width, and node padding |
| Alignment | Supported | `left`, `right`, `center`, and `justify` |
| Link geometry | Supported | Horizontal cubic paths, source/target breadth ordering, minimum one-pixel stroke, 0.5 opacity, and multiply blend mode |
| Link colors | Supported | Source-to-target gradients, source colors, target colors, and fixed CSS colors |
| Labels | Supported | Legacy position-based labels, outlined central-layer labels, optional values, two-decimal JavaScript rounding, prefix, and suffix |
| Node colors | Supported | Tableau 10 fallback palette plus node-specific CSS colors |
| Configuration | Supported | Width, height, responsive or intrinsic sizing, link color, alignment, values, prefix/suffix, node width/padding, label style, and node colors |
| Validation | Supported | Malformed CSV, invalid configuration, non-finite or negative values, cycles, missing nodes, and non-finite layout output return structured errors |

Sankey's upstream Jison grammar does not include Mermaid common `accTitle` or
`accDescr` statements. Frontmatter `title` is preserved as scene metadata; an
accessibility directive is not silently accepted as a CSV record.

## Validation Corpus

- All eight active examples extracted from Mermaid's official Sankey
  documentation run in JVM tests.
- 13 independent production scenarios cover all 27 declared Sankey capability
  points.
- The production audit accepted all 13 pairs:
  `13 pass / 0 review / 0 fail`; geometry ratios are width `1.050-1.060`,
  height `1.045-1.059`, and foreground ink `1.098-1.123`.
- The 256-case Native/Official audit accepted all pairs:
  `256 pass / 0 review / 0 fail`; geometry ratios are width `1.048-1.060`,
  height `1.028-1.059`, and foreground ink `1.062-1.124`.
  All 16 contact sheets were manually reviewed with no missing node, link,
  label, value, color, clipping, overlap, alignment, or paint-order defect.
- 256 deterministic randomized Native inputs exercise DAG topology, quoted
  labels, all alignments and link colors, dimensions, value labels, themes,
  finite geometry, and deterministic replay.
- All 11 built-in themes are rendered by the shared production theme matrix.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:sankey-doc-fixtures
```

Capture the 256-case Sankey partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=sankey \
OUTPUT_DIR=captures/local/sankey-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
