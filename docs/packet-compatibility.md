# Packet Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Packet diagram must preserve contiguous bit ranges, automatic field
placement, row splitting, source order, labels, bit numbers, title,
accessibility metadata, configuration, and fixed Packet styling closely enough
that a side-by-side comparison does not expose a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `packet`, `packet-beta`, frontmatter, init directives, comments, title, accessibility metadata, and Unicode |
| Parser | Supported | Kotlin translation of Mermaid's Packet Langium grammar, common statements, and string value conversion |
| Field syntax | Supported | Explicit ranges, single-bit fields, `+bits` fields, single/double quoted labels, escapes, and entities |
| Validation | Supported | Contiguous fields, `end >= start`, positive counted widths, integer overflow, and malformed syntax return structured errors |
| Row splitting | Supported | Fields spanning one or more configured row boundaries are split with Mermaid-compatible source labels and bit indices |
| Geometry | Supported | Fixed bit grid, row spacing, horizontal field padding, bit-number placement, and title placement |
| Styling | Supported | Mermaid's fixed black strokes/text, `#efefef` field fill, and 10/12/14px typography |
| Configuration | Supported | `rowHeight`, `bitWidth`, `bitsPerRow`, `showBits`, `paddingX`, `paddingY`, and `useMaxWidth` |
| Themes | Supported | All 11 built-in themes render; Packet field paint remains fixed as in Mermaid `12.0.0` |
| Resource controls | Supported | Mermaid's 10,000 generated-row limit returns `MermaidError.ResourceLimit` instead of a partial diagram |

## Validation Corpus

- Both active examples extracted from Mermaid's official Packet documentation
  run in JVM tests. The theme-variable example inside an upstream HTML comment
  is intentionally excluded.
- 13 independent production scenarios cover all 17 declared Packet capability
  points.
- The replacement production audit accepted all 13 pairs:
  `13 pass / 0 review / 0 fail`; geometry ratios are width `1.010-1.014`,
  height `1.016-1.049`, and foreground ink `1.008-1.023`.
- The replacement 256-case Native/Official audit accepted all pairs:
  `256 pass / 0 review / 0 fail`; geometry ratios are width `1.011-1.015`,
  height `1.006-1.056`, and foreground ink `0.977-1.039`.
  All 16 contact sheets were manually reviewed with no missing fields,
  incorrect row split, bit-number mismatch, clipping, overlap, or paint-order
  defect.
- 256 deterministic randomized Native inputs exercise explicit and counted
  fields, row splitting, configuration, themes, Unicode, finite geometry, and
  deterministic replay.
- All 11 built-in themes are rendered by the shared production theme matrix.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:packet-doc-fixtures
```

Capture the 256-case Packet partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=packet \
OUTPUT_DIR=captures/local/packet-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
