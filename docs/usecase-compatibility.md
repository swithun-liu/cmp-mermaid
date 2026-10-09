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
- Production detail: 18/18 passed.
- Visual matrix: 256/256 Native/Official pairs passed geometry; width
  `1.036-1.160`, height `0.971-1.115`, and foreground ink `1.043-1.316`.
- Visual detail: 256/256 passed.
- All 16 visual-matrix contact sheets were manually reviewed.
- JVM core and Compose tests pass as part of the 859-test suite.

The detail audit compares both render-object and expanded-primitive geometry
counts, preserving review when both representations diverge. Spatially
equivalent rich-text segments are compared by their combined bounds. Labels,
actor and use-case semantics, boundaries, notes, tables, markers, styles,
colors, clipping, paint order, and raster checks remain intact.

Evidence:

- [production contact sheet](assets/stability-report/usecase-complex-corpus.png)
- [production detail](assets/stability-report/usecase-production-detail.json)
- [production geometry](assets/stability-report/usecase-production-geometry.json)
- [visual detail](assets/stability-report/usecase-visual-parity-detail.json)
- [visual geometry](assets/stability-report/usecase-visual-parity-geometry.json)
- [visual contact-sheet index](assets/stability-report/visual-parity-evidence.md)

## Status

Use Case has completed its per-family replacement gate. All 33 Mermaid
`12.0.0` family gates now pass, so overall supported compatibility is
**Stable**.
