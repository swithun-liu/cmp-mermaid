# Event Modeling Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Event Modeling diagram must preserve time and reset frames, entity
type aliases, namespaced swimlanes, inferred and explicit relations, inline
and referenced data, metadata, configuration, and theme colors closely enough
that side-by-side review exposes no functional defect.

Event Modeling is beta in Mermaid `12.0.0`; its syntax may change in a later
upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `eventmodeling`, frontmatter, init directives, comments, title, accessibility metadata, entities, and Unicode |
| Parser | Supported | Compact and relaxed frame forms, grammar-hidden whitespace/comments between tokens, same-line declarations, time/reset frames, all entity aliases, qualified names, source references, inline/reference data, model entities, notes, GWT declarations, and structured failures |
| Validation | Supported | Frame/data/entity reference resolution and Mermaid's command, event, read-model, processor, and UI source-type rules |
| Database and semantics | Supported | Source-order frame evolution, inferred and explicit relations, reset behavior, namespace lane allocation, text wrapping, payload measurement, and typed resource limits |
| Layout and rendering | Supported | Upstream swimlane, card, relation, marker, label, payload, padding, and responsive/intrinsic viewport behavior |
| Configuration | Supported | `padding`, `rowHeight`, and `useMaxWidth`, including frontmatter/init precedence and typed validation; `rowHeight` is retained for upstream configuration compatibility even though Mermaid `12.0.0` does not consume it in layout |
| Themes | Supported | All Event Modeling fill/stroke, swimlane, relation, and arrowhead variables, including Mermaid's dark-theme defaults |
| Brace-delimited inline data | Supported with upstream semantics | Preserves Mermaid `12.0.0`'s end-exclusive extraction: `{a:1}` renders `a:`, while `{ a:1 }` renders `a:1` |
| Referenced data blocks | Supported with upstream semantics | Preserves the renderer's substring boundaries, including horizontal whitespace after the opening brace |
| Quoted inline data | Explicitly unsupported for rendering | Mermaid's grammar accepts quoted inline data, but its `12.0.0` renderer assumes braces. Kotlin returns `MermaidError.UnsupportedFeature` instead of reproducing malformed substring behavior |

## Parser And Browser-Runtime Boundary

The pinned grammar accepts model `entity` declarations, referenced `data`
blocks, `note` and `gwt` declarations, and Event Modeling accessibility
metadata. Kotlin parses and validates those constructs in focused tests.
Mermaid.js `12.0.0`'s browser rendering path does not reliably accept every
one of those grammar-level constructs, so the same-source visual corpus uses
only inputs that the pinned Official runtime renders. Parser-only coverage is
kept explicit instead of deleting legal syntax from the Kotlin contract or
weakening the Official visual gate.

## Validation Corpus

- All 12 active Mermaid Event Modeling documentation examples are generated
  from the pinned documentation source and rendered in JVM tests.
- Focused parser/database tests cover declarations, data references, notes,
  GWT statements, aliases, namespaces, reference validation, malformed input,
  frontmatter offsets, and typed resource/configuration failures.
- 13 independent production scenarios cover all 23 declared visual capability
  points.
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  20 visible text/layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.029-1.044`, `1.013-1.044`, and `1.029-1.115`.
- Production detail produced `7 pass / 6 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `1.025-1.037`,
  `1.008-1.061`, and `0.998-1.132`.
- Matrix detail produced `0 pass / 256 review / 0 fail`. Every review has only
  `text-segmentation`: Official represents a card title and payload in one
  `foreignObject`, while Native preserves the same visible content as a
  centered bold title plus a left-aligned monospace payload.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved swimlane, card, relation, marker, color, label,
  clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/eventmodeling-production-detail.json),
  [production geometry](assets/stability-report/eventmodeling-production-geometry.json),
  [matrix detail](assets/stability-report/eventmodeling-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/eventmodeling-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:eventmodeling-doc-fixtures
```

Capture the 256-case Event Modeling partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=eventmodeling \
OUTPUT_DIR=captures/local/eventmodeling-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
