# Cynefin Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Cynefin diagram must preserve the five domains, domain items,
transitions, seeded boundaries, confusion overflow, labels, configuration,
and theme colors closely enough that side-by-side review exposes no
functional defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `cynefin-beta`, `cynefin-beta:`, frontmatter, init directives, comments, metadata, entities, and Unicode |
| Parser | Supported | Five domain names, quoted items, labelled and unlabelled `-->` transitions, title, `accTitle`, and single-line or block `accDescr` |
| Database | Supported | Source-order domains and transitions, duplicate-domain replacement, entity decoding, and self-loop filtering |
| Domain layout | Supported | Fixed quadrants, central confusion ellipse, labels, optional model/practice descriptions, and source-order item badges |
| Boundaries | Supported | Mulberry32-compatible seeded fold and horizontal paths, optional straight boundaries, central cliff, and deterministic cubic commands |
| Items and overflow | Supported | Measured item widths, domain placement, three visible confusion items, and `+N more` overflow |
| Transitions | Supported | Quadratic routes, triangle arrowheads, labels, source order, and foreground paint order |
| Configuration | Supported | `width`, `height`, `padding`, `useMaxWidth`, `showDomainDescriptions`, `boundaryAmplitude`, and `seed` |
| Themes and sizing | Supported | Cynefin theme block, partial nested overrides, responsive/intrinsic sizing, content-bound viewBox normalization, and mixed-script text |
| Validation | Supported | Invalid headers, malformed strings/transitions, invalid configuration, and resource exhaustion return typed errors |

## Validation Corpus

- The active example extracted from Mermaid's official Cynefin documentation
  runs in a JVM test.
- 13 independent production scenarios cover all 30 declared Cynefin
  capability points.
- 256 deterministic same-source matrix cases exercise both header forms,
  every domain, empty and duplicate domains, transitions, confusion overflow,
  descriptions, seeded boundaries, sizing, themes, accessibility, entities,
  Unicode, and long labels.
- The production geometry and detail audits passed all 13 pairs. Width,
  height, and foreground-ink ratios were `1.013-1.021`, `1.011-1.019`, and
  `1.019-1.041`; detail was `13 pass / 0 review / 0 fail`.
- The matrix geometry and detail audits passed all 256 pairs. Ratios were
  `1.013-1.034`, `1.009-1.019`, and `1.020-1.052`; detail was
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed for domains, boundaries, cliff and confusion geometry, item
  placement, overflow, transitions, themes, labels, clipping, and paint
  order. Raw
  [production detail](assets/stability-report/cynefin-production-detail.json),
  [production geometry](assets/stability-report/cynefin-production-geometry.json),
  [matrix detail](assets/stability-report/cynefin-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/cynefin-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:cynefin-doc-fixtures
```

Capture the 256-case Cynefin partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=cynefin \
OUTPUT_DIR=captures/local/cynefin-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
