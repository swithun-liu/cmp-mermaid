# Use Case Compatibility

## Current State

CMP Mermaid implements Mermaid.js `12.0.0` `usecase-beta` in pure Kotlin
Multiplatform and renders it through Compose Canvas. The production path does
not use Mermaid.js, a browser DOM, a WebView, or a JavaScript engine.

Supported behavior includes:

- all four directions;
- actors, actor aliases, standard/icon/hollow/awesome variants, business
  elements, and stereotypes;
- ellipse and rectangle use cases;
- system and package boundaries;
- associations, endpoint markers, edge labels, minimum length,
  include/extend/generalization, edge IDs, classes, styles, and animation
  metadata;
- notes, Markdown labels, JSON tables, comments, entities, and Unicode;
- class definitions, inline styles, frontmatter title/configuration,
  responsive and intrinsic sizing;
- built-in themes, theme variables, and rotating actor colors.

## Verification

- 18 independent production scenarios cover 38/38 declared capability points.
- Production geometry: 18/18 passed.
- Production detail: 5 automatic passes, 13 manually accepted representation
  differences, 0 failures.
- Visual matrix: 256/256 Native/Official pairs passed geometry.
- Visual detail: 72 automatic passes, 184 manually accepted representation
  differences, 0 failures.
- All 16 visual-matrix contact sheets were manually reviewed.
- JVM core and Compose tests pass as part of the 745-test suite.

The accepted reviews contain only element-count differences caused by compound
SVG shapes versus Native scene primitives, and text-segmentation differences
where Official groups rich text differently. Labels, actor and use-case
semantics, boundaries, notes, tables, markers, styles, colors, clipping, and
paint order remain intact.

Evidence:

- [production contact sheet](assets/stability-report/usecase-complex-corpus.png)
- [production detail](assets/stability-report/usecase-production-detail.json)
- [production geometry](assets/stability-report/usecase-production-geometry.json)
- [visual detail](assets/stability-report/usecase-visual-parity-detail.json)
- [visual geometry](assets/stability-report/usecase-visual-parity-geometry.json)
- [visual contact-sheet index](assets/stability-report/visual-parity-evidence.md)

## Status

Use Case has completed its per-family replacement gate. Overall Mermaid
compatibility remains **Not Stable** until Wardley Map and ZenUML complete the
same gate.
