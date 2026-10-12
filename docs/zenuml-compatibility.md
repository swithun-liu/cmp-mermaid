# ZenUML Compatibility

## Current State

CMP Mermaid implements the Mermaid.js `12.0.0` external
`@mermaid-js/mermaid-zenuml@1.0.0` family using the upstream lockfile's
`@zenuml/core@3.49.2`. Parsing, layout, and rendering are pure Kotlin
Multiplatform and Compose Canvas; the production path does not embed the
official JavaScript plugin.

Supported behavior includes:

- explicit and inferred participants, aliases, annotators, and named groups;
- asynchronous, synchronous, nested, creation, and return messages;
- typed and untyped assignments plus `@return`/`@reply`;
- `if`/`else`, loop, parallel, optional, critical, section, reference, and
  try/catch/finally fragments;
- comments with Markdown styling, dividers, DSL titles, participant icons,
  emoji, and Unicode;
- accepted frontmatter title metadata that remains visually ignored, matching
  the external Mermaid plugin's empty database adapter;
- upstream horizontal constraint solving, vertical statement coordinates,
  occurrences, sequence numbering, and participant creation timing.

## Verification

- 16 official documentation cases render through the Native engine.
- 13 independent production scenarios cover all 26 declared capability
  points.
- Five additional release-candidate cases exercise combined real-world
  interactions.
- The 256-case generated Native matrix renders with finite deterministic
  SceneGraphs.
- All 256 full-factorial visual-input matrix sources render successfully
  through Mermaid.js `12.0.0` with
  `@mermaid-js/mermaid-zenuml@1.0.0` and `@zenuml/core@3.49.2`.
- All 13 production pairs pass the replacement detail and geometry audits.
  Native/Official content ratios are width `1.042-1.100`, height
  `1.039-1.102`, and foreground ink `1.094-1.238`.
- The fresh 256-case full-factorial input audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.043-1.082`, height `1.042-1.079`, and foreground ink `0.967-1.184`.
- The cached open and filled message-arrow vectors have explicit black
  primitive paint, with a regression test protecting Kotlin object
  initialization order.
- All 16 freshly generated visual-matrix contact sheets were manually reviewed
  with no unresolved participant, occurrence, message arrow, fragment, icon,
  clipping, overlap, or paint-order defect.
- Parser, coordinate, layout, malformed-input, and resource-limit tests are
  included in the JVM suite.

Evidence:

- [production contact sheet](assets/stability-report/zenuml-complex-corpus.png)
- [production detail](assets/stability-report/zenuml-production-detail.json)
- [production geometry](assets/stability-report/zenuml-production-geometry.json)
- [visual detail](assets/stability-report/zenuml-visual-parity-detail.json)
- [visual geometry](assets/stability-report/zenuml-visual-parity-geometry.json)
- [visual contact-sheet index](assets/stability-report/visual-parity-evidence.md)

## Status

ZenUML has completed its per-family replacement gate. All 33 Mermaid `12.0.0`
family gates now pass, so overall supported compatibility is **Stable**.
