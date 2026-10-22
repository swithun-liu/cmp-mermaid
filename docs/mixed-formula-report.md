# Mermaid 12.0.0 Mixed Formula Composition Report

The `0.1.16` shared HTML-label translation preserves ordinary text around
every `$$...$$` match and supports multiple formulas in the same label.
It follows the locked upstream `createText`, `addHtmlSpan`, and
`renderKatexUnsanitized` call order. Browser flex formatting is a declared
Compose platform adaptation. Formula support remains **not Stable**.

## Current Evidence

| Item | Result |
| --- | --- |
| Mermaid baseline | `12.0.0`, source `98a0945418c76238f15df2afaddbba4272656c3b` |
| Same-source cases | 28 distinct sources across 9 diagram families |
| Diagram screenshots | 56: 28 Native and 28 Official |
| Separate formula fixture images | 36; not counted as diagram screenshots |
| Reviewed contact sheets | 4/4 |
| Combined geometry, topology, and raster audit | 15 Pass / 13 Review / 0 Fail |
| Native render errors and provider failures | 0 |
| Per-item geometry tolerance | 0.08 logical pixels |
| Raster Pass threshold | SSIM at least 0.97 |
| JVM tests | Core 1,084/1,084; Compose 69/69 |
| Wasm tests | Core 1,082/1,082; Compose 62/62 |
| iOS Simulator Arm64 tests | Core 1,082/1,082; Compose 76/76 |
| Audit tooling regressions | 42 passing test entries: topology, absolute placement, and evidence freshness |
| Official formula metrics fixture | 2/2; existing Noto Sans Math resource bound explicitly |

Open the [Native/Official comparison](assets/mixed-formula-report/index.html),
[source and screenshot manifest](assets/mixed-formula-report/visual-parity-manifest.json),
[geometry and topology audit](assets/mixed-formula-report/geometry.json),
and [raster diagnostics](assets/mixed-formula-report/raster.json).
The [upstream source inventory](upstream-formula-rendering-map.md) records
individual stages, classifications, and locked source hashes.

The nine captured families are Flowchart, State, Class, Entity Relationship,
Block, Kanban, Mindmap, Use Case, and Requirement. Cases cover surrounding
text, multiple and repeated formulas, fraction and sum heights, wide content,
CSS whitespace, entities, `<br>` and newline ordering, direct HTML children,
descendant styles, inherited font size/color, node labels, and edge labels.
Both renderers receive identical source bytes. Manifest entries bind sources
and both screenshots with SHA-256 hashes.

## Review Findings

All captured formula requests, visible text, item counts, and intrinsic item
geometry match the recorded reference within the declared tolerance. No
recorded edge topology mismatch occurred: the audit compares port sides,
bends, direction sequences, shared tracks, and crossings. It normalizes only
the official SVG's declared viewBox origin.

Thirteen cases retain Review status. Flowchart and State edge cases have
recorded route-coordinate differences up to 30 logical pixels; the bottom-up
Flowchart case differs by 10 pixels, and Use Case differs by 34 pixels.
The audit now compares absolute label and item positions as well as intrinsic
geometry: the maximum label/item placement difference is 17 pixels for Use
Case, 15 pixels for Flowchart and State edges, and 10.0078 pixels for the
bottom-up Flowchart. Class differs by 6.75 pixels; Block, Mindmap, and
Requirement differ by approximately 4 pixels. ER title and relation positions
differ by 0.8672 and 0.1484 pixels respectively. These remain Review even when
the raster threshold passes. Other reviews concern font weight, shape borders,
and diagram styling.
All four contact sheets were inspected; the recorded text and formula content
remain visible. Manual inspection does not convert these findings to Pass.

Official, Native, and raster JSON inputs are bound by their exact SHA-256 bytes
and generation timestamps in the published manifest, which also records the
geometry audit generation. The new evidence supersedes
the earlier 16 Pass / 12 Review result, whose checks omitted absolute item
placement and collapsed distinct diagonal bends. No production coordinates
were adjusted to obtain a passing result.

## What This Audit Proves

Native uses official formula images and intrinsic dimensions through the
optional public provider. This isolates Kotlin shared-label composition,
dimension propagation into graph layout, and Compose Canvas placement.
It does not validate a native KaTeX parser, a particular host formula engine,
physical-device formula parity, or complete formula support in every family.

Sequence, ZenUML comments, Event Modeling structured labels, formulas nested
inside user HTML elements, and HTML block formatting with margins remain
explicit unsupported boundaries. A selected provider failure uses the
existing readable per-formula text fallback; it does not silently switch
providers.

The retained 8,448-pair general diagram matrix was captured without a formula
provider and was not regenerated for this change. Its counts remain separate
from this 28-case / 56-screenshot evidence set.

## Reproduction

```bash
node tools/official-reference/capture-mixed-formula.mjs build/mixed-formula-audit
./gradlew :mermaid-compose:captureMixedFormulaAudit --console=plain
python3 tools/official-reference/mixed-formula-contact-sheets.py build/mixed-formula-audit
node tools/official-reference/audit-mixed-formula.mjs build/mixed-formula-audit
node tools/official-reference/publish-mixed-formula-report.mjs \
  build/mixed-formula-audit docs/assets/mixed-formula-report
```

The contact-sheet step requires Pillow, NumPy, and scikit-image. The publisher
rejects stale raster inputs, duplicate sources, source mismatches, missing
captures, or failed audit cases.
