# Mermaid 12.0.0 Full Diagram Roadmap

## Current State

CMP Mermaid implements 26 of the 33 user-visible diagram families documented
by Mermaid `12.0.0`. The source-controlled inventory is
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
| Implemented, 256-case detail gate passing | Flowchart, XY Chart, Quadrant Chart, Timeline, Kanban, Sequence, Class, State, Entity Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, Mindmap, Packet, Radar, Sankey, Treemap, Venn, Ishikawa, Cynefin, Event Modeling, Agentflow, Block, Swimlanes |
| Translation pending | Architecture, C4, Railroad, TreeView, Use Case, Wardley Map, ZenUML |

The current 6,656-pair matrix covers all 26 implemented families with fresh
replacement evidence. A Git Graph label paint-order defect was visible in the
original 3,072-pair matrix and was not identified during the earlier review.
That defect is now corrected and every implemented family has passed the
replacement gate; no family can use a legacy geometry result alone to satisfy
the new Stable gate.

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

All 26 implemented families have completed the replacement gate. Together they
have 6,656 accepted same-source pairs:
`5,850 automatic pass / 806 manually reviewed / 0 unresolved` across 416 reviewed contact
sheets. The 60 ER reviews are text-position threshold findings, while the 19
Journey reviews are benign line-segmentation differences for one complete long
actor label. The four Requirement reviews are benign greedy cross-matches
between duplicate `<<contains>>` or `<<satisfies>>` labels. The 20 Git Graph
reviews are text-overlap threshold findings caused by browser/Compose
text-bound differences. The 107 Mindmap reviews are CoSE-Bilkent branch
rotation or mirror differences under platform text-size perturbations; 67 also
cross the foreground-mask threshold. The 237 Treemap reviews are
text-position findings, with 198 also reporting label/value overlap-topology
threshold differences caused by Canvas/SVG glyph bounds. Venn contributes 216
automatic passes and 40 manual acceptances for two repeated text-overlap
font-box threshold patterns. Event Modeling contributes 256 manual acceptances
for title/payload `text-segmentation`: Official uses one `foreignObject`, while
Native uses separate bold-title and monospace-payload text elements. Block
contributes 253 automatic passes and three manually accepted paint-order
occlusion threshold reviews. Ishikawa, Cynefin, and Agentflow each contribute
256 automatic passes. Swimlanes contributes 196 automatic passes and 60
manual acceptances for text-position differences along semantically
equivalent orthogonal routes. All
reviewed cases preserve expected text, hierarchy, shapes, colors, and edges,
avoid clipping and paint-order defects, and were accepted by manual side-by-side review.
Flowchart geometry ratios are `1.026-1.119` for width, `0.945-1.047` for height, and
`0.948-1.241` for foreground ink. XY Chart ratios are `1.008-1.029`,
`0.995-1.041`, and `0.918-1.163`, respectively. Quadrant ratios are
`1.007-1.035`, `1.011-1.022`, and `1.024-1.048`, respectively. Timeline
ratios are `1.026-1.053`, `1.038-1.087`, and `1.052-1.136`, respectively.
Kanban ratios are `1.041-1.071`, `0.864-1.230`, and `0.996-1.110`,
respectively. Sequence ratios are `1.023-1.068`, `0.893-1.045`, and
`0.926-1.148`, respectively. Class ratios are `1.030-1.208`,
`0.897-1.055`, and `0.781-1.227`, respectively. State ratios are
`0.963-1.224`, `0.968-1.089`, and `1.031-1.383`, respectively. Entity
Relationship ratios are `1.033-1.078`, `0.882-1.043`, and `0.545-1.150`,
respectively. Gantt ratios are `1.035-1.037`, `0.883-0.961`, and
`0.964-1.023`, respectively. Pie ratios are `0.992-1.050`, `0.994-1.019`,
and `0.993-1.042`, respectively. User Journey ratios are `1.020-1.032`,
`1.036-1.057`, and `0.983-1.073`, respectively. Requirement ratios are
`1.013-1.072`, `1.004-1.053`, and `0.974-1.148`, respectively.
Git Graph ratios are `1.036-1.154`, `1.032-1.137`, and `1.027-1.415`,
respectively. Mindmap ratios are `0.956-1.155`, `0.861-1.212`, and
`0.771-1.431`, respectively. Packet ratios are `1.011-1.015`,
`1.006-1.056`, and `0.977-1.039`, respectively. Radar ratios are
`1.004-1.036`, `1.007-1.021`, and `1.005-1.036`, respectively. Sankey ratios
are `1.048-1.060`, `1.028-1.059`, and `1.062-1.124`, respectively. Treemap
ratios are `1.033-1.076`, `1.029-1.039`, and `0.945-1.077`, respectively. Venn
ratios are `0.997-1.032`, `0.992-1.026`, and `0.999-1.059`, respectively.
Ishikawa ratios are `1.040-1.173`, `1.023-1.119`, and `1.058-1.311`,
respectively. Cynefin ratios are `1.013-1.034`, `1.009-1.019`, and
`1.020-1.052`, respectively. Agentflow ratios are `1.040-1.222`,
`0.854-1.086`, and `0.773-1.287`, respectively.
Event Modeling ratios are `1.025-1.037`, `1.008-1.061`, and `0.998-1.132`,
respectively.
Block ratios are `0.970-1.281`, `0.856-1.078`, and `0.840-1.342`,
respectively.
Swimlanes ratios are `1.028-1.186`, `0.883-1.070`, and `0.899-1.551`,
respectively.

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
   Block and Swimlanes complete; Architecture, C4, TreeView, and Use Case pending.
3. Specialized renderers and grammars: Ishikawa and Cynefin complete;
   Wardley Map, Railroad (IR, EBNF, ABNF, PEG), and ZenUML pending.

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

Overall Mermaid `12.0.0` support reaches Stable only after all 33 family gates
pass, producing at least 8,448 same-source Native/Official pairs. Until then,
status must be reported as `implemented`, `translation pending`, or
`detail re-audit pending`, never as complete Mermaid compatibility.
