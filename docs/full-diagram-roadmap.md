# Mermaid 12.0.0 Full Diagram Roadmap

## Current State

CMP Mermaid now implements all 33 user-visible diagram families documented by
Mermaid `12.0.0`. The source-controlled inventory is
[`diagram-inventory.mjs`](../tools/official-reference/diagram-inventory.mjs);
CI verifies it against Mermaid's 39 registered IDs and, when the pinned source
tree is available, all 33 syntax documents.

Mermaid's registry count is not the public family count:

- `error`, `---`, and `info` are internal or diagnostic entries.
- `flowchart-v2` and `flowchart-elk` belong to the Flowchart family. ELK
  remains unsupported until it has a pure Kotlin translation.
- `railroad`, `railroadEbnf`, `railroadAbnf`, and `railroadPeg` are four
  official inputs of one Railroad family.
- ZenUML is an official external plugin and documentation family.

| State | Diagram families |
| --- | --- |
| Implemented, 256-case detail gate passing | Flowchart, XY Chart, Quadrant Chart, Timeline, Kanban, Sequence, Class, State, Entity Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, Mindmap, Packet, Radar, Sankey, Treemap, Venn, Ishikawa, Cynefin, Event Modeling, Agentflow, Block, Swimlanes, Architecture, C4, Railroad, TreeView, Use Case, Wardley Map, ZenUML |

The generated source matrix now contains 8,448 cases across all 33 families,
and all cases pass the Native SceneGraph, Native/Official geometry, replacement
detail, and manual contact-sheet gates. A Git Graph label paint-order defect
was visible in the original 3,072-pair matrix and was not identified during
the earlier review. That defect is corrected; no family uses a legacy geometry
result alone to satisfy the Stable gate.

The replacement audit now emits a JSON manifest beside every Native and
Official PNG. Native manifests preserve actual SceneGraph paint order;
Official manifests preserve visible SVG DOM paint order. The detail comparator
checks expected text on both sides, normalized text geometry, element and paint
counts, colors, stroke patterns, marker counts and endpoint anchors, clipping,
text overlap, later opaque element occlusion, foreground masks, tolerant
edges, and foreground color difference. Every non-passing case is written to
an explicit review queue with a diff heatmap. Text-overlap threshold flips are
ignored only when the two measured overlap ratios differ by at most `0.08`;
materially different overlap remains a review. Paint-order occlusion compares
coverage against a `0.10` tolerance both when both renderers cross the
detection threshold and when only one does, so a small threshold flip cannot
be mistaken for missing paint. A marker cannot pass from metadata alone:
missing anchors fail, and materially deeper coverage by later opaque paint is
compared against the Official endpoint.

`parity_gitgraph_005` is the regression proof for this gate. Replaying the old
global label layers reports a `paint-order-occlusion` mismatch for
`cherry-pick:prepare-follow-up-correction` (Official overlap `0.32`, legacy
Native overlap `0`). The corrected commit-order scene reports one occluded text
on both sides and passes with no findings.

All 33 families have completed the replacement gate. Together they have 8,448
accepted same-source pairs:
`7,429 automatic pass / 1,019 manually reviewed / 0 unresolved` across 528
reviewed contact sheets. The review queue contains only the documented
text-position, text-segmentation, text-overlap, raster-mask, element-count,
and paint-order occlusion-ratio representation thresholds. Manual review
confirmed complete expected text, hierarchy, shapes, colors, edges, clipping,
and visible paint order.

The fresh per-family pass/review counts and width, height, and foreground-ink
ranges are published in the
[8,448-case visual evidence index](assets/stability-report/visual-parity-evidence.md).
Across the complete matrix, width ratios are `0.930-1.276`, height ratios are
`0.852-1.236`, and foreground-ink ratios are `0.567-1.578`.

## Expected Behavior

The target is all 33 Mermaid `12.0.0` diagram families translated to pure
Kotlin Multiplatform and rendered by Compose Canvas:

- no WebView, embedded JavaScript engine, or production network dependency;
- parser, database, layout, style, and renderer behavior source-mapped to the
  pinned upstream implementation;
- unsupported behavior returns a typed error instead of a silent substitute;
- the same source drives Native and Official reference rendering;
- every family has 256 distinct acceptance sources plus independent
  documentation, production, randomized, and cross-platform coverage.

## Translation Batches

Batch order may change when upstream dependency analysis shows a larger shared
benefit, but a family is never marked Stable merely because its batch is done.

1. Shared chart and partition foundations: Quadrant Chart, Timeline, Kanban,
   Sankey, Packet, Radar, Treemap, Venn.
2. Shared graph and domain foundations: Agentflow and Event Modeling complete;
   Block, Swimlanes, Architecture, C4, TreeView, and Use Case complete.
3. Specialized renderers and grammars: Ishikawa, Cynefin, Railroad
   (IR, EBNF, ABNF, PEG), Wardley Map, and ZenUML translations and visual
   gates complete.

## Per-Family Stable Gate

Each of the 33 families must pass all of the following:

1. **Source map:** pinned upstream files, versions, translated functions, and
   intentional Kotlin adaptations are documented.
2. **Syntax and state:** official documentation fixtures, focused parser/DB
   tests, malformed input, and typed resource failures pass.
3. **Scene semantics:** expected text, element types and counts, paint order,
   normalized bounds and centers, colors, strokes, markers, and visibility
   are compared where the Official SVG exposes them.
4. **Raster detail:** foreground masks, edges, color distribution, multiscale
   perceptual difference, clipping, and overlap diagnostics are recorded.
   Font rasterization receives explicit tolerance; missing or wrongly layered
   content does not.
5. **Visual review:** all 256 Native/Official pairs are reviewed from paged
   contact sheets and every anomaly is resolved or documented as an allowed
   platform-font difference.
6. **Stress and replay:** at least 256 separately generated legal inputs render
   finite output twice with identical SceneGraphs.
7. **Platforms:** JVM tests and Android, iOS, Desktop, and Web builds/load
   tests pass with production runtime isolation intact.

## Overall Stable Gate

Overall Mermaid `12.0.0` support is **Stable**: all 33 family gates pass and
the report contains 8,448 same-source Native/Official pairs. Future changes
must keep every family gate passing; a regression returns the affected family
and the overall rating to a pending state.
