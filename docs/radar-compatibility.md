# Radar Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Radar diagram must preserve axis order and labels, positional and
referenced curve values, clipping to the configured value range, graticules,
curve interpolation, legend order, title, accessibility metadata,
configuration, and theme styling closely enough that a side-by-side comparison
does not expose a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `radar-beta`, optional colon, frontmatter, comments, title, accessibility metadata, and Unicode |
| Parser | Supported | Kotlin translation of Mermaid's Radar Langium grammar, common statements, and string value conversion |
| Axes | Supported | Ordered identifiers, optional quoted labels, escapes, entities, and multiple declarations |
| Curves | Supported | Positional values, axis-referenced values, declaration-order reordering, multiple curves, and optional quoted labels |
| Options | Supported | `showLegend`, `ticks`, `max`, `min`, `graticule`, last-value precedence, inferred maximum, and the 32-tick cap |
| Graticules | Supported | Circular ticks and polygon ticks with Mermaid-compatible axis angles |
| Curve geometry | Supported | Value clipping, relative radius, polygon curves, and closed Catmull-Rom-to-cubic curves with configurable tension |
| Legend and title | Supported | Source-order color boxes and labels, optional legend, and diagram title |
| Configuration | Supported | Width, height, four margins, axis scale, axis-label scale, curve tension, and responsive or intrinsic sizing |
| Themes | Supported | All 11 built-in themes, `cScale0..11`, shared text color, and nested Radar theme variables |
| Validation | Supported | Malformed syntax, missing referenced entries, invalid finite/range configuration, and zero value ranges return structured errors |

## Validation Corpus

- All three active examples extracted from Mermaid's official Radar
  documentation run in JVM tests.
- 13 independent production scenarios cover all 25 declared Radar capability
  points.
- The replacement production audit accepted all 13 pairs:
  `13 pass / 0 review / 0 fail`; geometry ratios are width `1.017-1.033`,
  height `1.015-1.021`, and foreground ink `1.035-1.037`.
- The replacement 256-case Native/Official audit accepted all pairs:
  `256 pass / 0 review / 0 fail`; geometry ratios are width `1.004-1.036`,
  height `1.007-1.021`, and foreground ink `1.005-1.036`.
  All 16 contact sheets were manually reviewed with no missing axis, curve,
  graticule, legend, title, clipping, overlap, or paint-order defect.
- 256 deterministic randomized Native inputs exercise both curve entry forms,
  both graticules, configuration, themes, Unicode, finite geometry, and
  deterministic replay.
- All 11 built-in themes are rendered by the shared production theme matrix.

## Reference Workflow

Regenerate official documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:radar-doc-fixtures
```

Capture the 256-case Radar partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=radar \
OUTPUT_DIR=captures/local/radar-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
