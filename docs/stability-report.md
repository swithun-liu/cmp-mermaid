# Mermaid 12.0.0 Stability Test Report

> [!WARNING]
> This report covers the current 29-family implemented subset, not a current
> overall Stable decision. All implemented families have passed replacement
> detail review, but 4 official Mermaid families remain untranslated. See the
> [33-family roadmap](full-diagram-roadmap.md) for the complete status and
> promotion gate.

This report records current evidence for CMP Mermaid's 29 implemented families
and preserves the limitations of the former 12-family **Stable** decision. The
evidence is deliberately separate from the demo gallery and distinguishes
independent production scenarios, systematic visual-matrix variants, and
Native-only randomized stress inputs.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Implemented subset detail-gated; overall Stable withheld** |
| Mermaid compatibility baseline | `12.0.0` |
| Independent production scenarios | 384: 147 release-candidate cases plus 237 additional conformance cases |
| Declared capability coverage | 623/623 points across 29 diagram types |
| Large-scale visual matrix | 7,424 unique Mermaid sources: 256 per diagram type |
| Native core render results | 384 independent plus 7,424 matrix cases passed, 0 failed |
| Web Native/Official captures | 14,848 matrix screenshots plus 768 independent-corpus screenshots, 0 render errors |
| Manual visual review | 464 replacement-gate sheets across all 29 implemented families |
| Automated visual geometry | 7,424/7,424 matrix pairs and 384/384 independent pairs passed |
| Deterministic SceneGraph replay | 384 passed, 0 mismatches |
| Built-in theme matrix | 319/319 renders passed: 29 diagram types by 11 themes |
| Separate deterministic Native stress inputs | 7,424 |
| JVM tests | 694 passed, 0 failed |
| Core production soak | 1,920 renders; 814ms total; 1ms P95; 60,392 bytes retained heap |
| Runtime load matrix | Web passed the current 384-scenario corpus; Android Emulator, iOS Simulator, and Desktop retain the prior 236-scenario baseline |
| Platform build matrix | Android debug/release, Web production, Desktop distributable, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared in debug or release APK |
| Public-source safety scan | No organization-specific endpoint or credential pattern found |

**Current conclusion:** all 29 implemented families pass the shared core,
geometry, determinism, theme, resource, and replacement detail gates. This
evidence does not satisfy the 33-family Stable criteria and must not be used as
an overall Stable decision.

## Current Replacement-Gate Progress

All 29 implemented families have completed the replacement visual gate. Each
has 256 unique same-source Native/Official pairs, a 256/256 geometry result,
and 16 paged contact sheets. The accepted replacement total is
7,424/7,424 pairs across 464 manually reviewed sheets:
`6,618 automatic pass / 806 manually reviewed / 0 unresolved`. The 60 ER reviews are
text-position threshold findings. The 19 Journey reviews are text-segmentation
findings for one long actor label whose two lines break at different words. The
four Requirement reviews are greedy duplicate-label matching findings. The 20
Git Graph reviews are text-overlap threshold findings caused by
browser/Compose text-bound differences. The 107 Mindmap reviews are
text-position findings from CoSE-Bilkent branch rotations or mirrors under
platform text-size perturbations; 67 also cross the foreground-mask threshold.
The 237 Treemap reviews are text-position findings, with 198 also reporting
same-row label/value overlap-topology threshold differences caused by
Canvas/SVG glyph bounds. Venn contributes 216 automatic passes and 40 manual
acceptances. Its 40 reviews are two repeated text-overlap font-box threshold
patterns. Event Modeling contributes 256 manual acceptances for
title/payload `text-segmentation`: Official uses one `foreignObject`, while
Native uses separate bold-title and monospace-payload text elements. Ishikawa,
Cynefin, and Agentflow each contribute 256 automatic passes. Block contributes
253 automatic passes and three manually accepted paint-order occlusion ratio
threshold reviews. Swimlanes contributes 196 automatic passes and 60 manual
acceptances for text-position differences along semantically equivalent
orthogonal routes. Architecture, C4, and Railroad each contribute 256
automatic passes. All reviewed cases
preserve complete text and diagram
semantics, with no unresolved clipping, overlap, or paint-order defect.

- Flowchart content ratios: width `1.026-1.119`, height `0.945-1.047`,
  foreground ink `0.948-1.241`.
- XY Chart content ratios: width `1.008-1.029`, height `0.995-1.041`,
  foreground ink `0.918-1.163`.
- Quadrant content ratios: width `1.007-1.035`, height `1.011-1.022`,
  foreground ink `1.024-1.048`.
- Timeline content ratios: width `1.026-1.053`, height `1.038-1.087`,
  foreground ink `1.052-1.136`.
- Kanban content ratios: width `1.041-1.071`, height `0.864-1.230`,
  foreground ink `0.996-1.110`.
- Sequence content ratios: width `1.023-1.068`, height `0.893-1.045`,
  foreground ink `0.926-1.148`.
- Class content ratios: width `1.030-1.208`, height `0.897-1.055`,
  foreground ink `0.781-1.227`.
- State content ratios: width `0.963-1.224`, height `0.968-1.089`,
  foreground ink `1.031-1.383`.
- Entity Relationship content ratios: width `1.033-1.078`, height
  `0.882-1.043`, foreground ink `0.545-1.150`.
- Gantt content ratios: width `1.035-1.037`, height `0.883-0.961`,
  foreground ink `0.964-1.023`.
- Pie content ratios: width `0.992-1.050`, height `0.994-1.019`,
  foreground ink `0.993-1.042`.
- User Journey content ratios: width `1.020-1.032`, height `1.036-1.057`,
  foreground ink `0.983-1.073`.
- Requirement content ratios: width `1.013-1.072`, height `1.004-1.053`,
  foreground ink `0.974-1.148`.
- Git Graph content ratios: width `1.036-1.154`, height `1.032-1.137`,
  foreground ink `1.027-1.415`.
- Mindmap content ratios: width `0.956-1.155`, height `0.861-1.212`,
  foreground ink `0.771-1.431`.
- Packet content ratios: width `1.011-1.015`, height `1.006-1.056`,
  foreground ink `0.977-1.039`.
- Radar content ratios: width `1.004-1.036`, height `1.007-1.021`,
  foreground ink `1.005-1.036`.
- Sankey content ratios: width `1.048-1.060`, height `1.028-1.059`,
  foreground ink `1.062-1.124`.
- Treemap content ratios: width `1.033-1.076`, height `1.029-1.039`,
  foreground ink `0.945-1.077`.
- Venn content ratios: width `0.997-1.032`, height `0.992-1.026`,
  foreground ink `0.999-1.059`.
- Ishikawa content ratios: width `1.040-1.173`, height `1.023-1.119`,
  foreground ink `1.058-1.311`.
- Cynefin content ratios: width `1.013-1.034`, height `1.009-1.019`,
  foreground ink `1.020-1.052`.
- Event Modeling content ratios: width `1.025-1.037`, height `1.008-1.061`,
  foreground ink `0.998-1.132`.
- Agentflow content ratios: width `1.040-1.222`, height `0.854-1.086`,
  foreground ink `0.773-1.287`.
- Block content ratios: width `0.970-1.281`, height `0.856-1.078`,
  foreground ink `0.840-1.342`.
- Swimlanes content ratios: width `1.028-1.186`, height `0.883-1.070`,
  foreground ink `0.899-1.551`.
- Architecture content ratios: width `1.020-1.232`, height `0.996-1.236`,
  foreground ink `0.986-1.584`.
- C4 content ratios: width `0.996-1.153`, height `0.992-1.046`,
  foreground ink `1.005-1.252`.
- Railroad content ratios: width `1.035-1.116`, height `1.009-1.130`,
  foreground ink `1.023-1.319`.
- All 16 Flowchart contact sheets and all 256 same-source pairs were manually
  inspected after correcting Bang/Cloud edge intersection bounds. No
  unresolved marker, routing, label, clipping, overlap, or paint-order defect
  remains in that corpus.
- All 16 XY Chart contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's component insertion order and invalid
  SVG font-size fallback. No unresolved chart, axis, plot, legend, label,
  clipping, overlap, or paint-order defect remains in that corpus.
- All 16 Kanban contact sheets and all 256 same-source pairs were manually
  inspected, including the corrected three-line titles in cases 114 and 117.
- All 16 Sequence contact sheets and all 256 same-source pairs were manually
  inspected after translating Mermaid's control-title loop-width preflight and
  tightening created-participant manifest text bounds. No unresolved
  participant, lifecycle, message, marker, note, activation, control-frame,
  clipping, overlap, or paint-order defect remains.
- All 16 Class contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's asymmetric class text-group bbox, empty
  and method-only compartment spacing, note padding, and terminal placement.
  No unresolved class, namespace, relation, marker, cardinality, note, label,
  clipping, overlap, or paint-order defect remains.
- All 16 State contact sheets and all 256 same-source pairs were manually
  inspected after matching nested composite, note, concurrency, start/end,
  choice, fork/join, and transition routing behavior. No unresolved state,
  group boundary, marker, note, label, clipping, overlap, or paint-order defect
  remains.
- All 16 Entity Relationship contact sheets and all 256 same-source pairs were
  manually inspected after translating Mermaid's recursive subgraph spacing.
  The 60 queued cases differ only in isolated label positions on repeated large
  structures; their P95 normalized text-center distance is at most `0.077`,
  mask IoU is at least `0.685`, and edge F1 is at least `0.922`. No unresolved
  entity, attribute, subgraph, relationship, marker, text-loss, clipping,
  overlap, or paint-order defect remains.
- All 16 Gantt contact sheets and all 256 same-source pairs were manually
  inspected at the shared `1200 x 900` viewport required by Mermaid's
  parent-width renderer input. No unresolved task ordering, section band,
  milestone, exclusion, axis, marker, label, clipping, overlap, or paint-order
  defect remains.
- All 16 Quadrant and all 16 Timeline contact sheets were manually inspected.
- All 16 Pie contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's centered viewport normalization and
  legend-aware bounds. No unresolved slice, percentage, legend, title,
  clipping, overlap, or paint-order defect remains.
- All 16 User Journey contact sheets and all 256 same-source pairs were
  manually inspected after matching Mermaid's actor-legend text offset. The 19
  queued cases differ only in line segmentation for the complete
  `Regional Compliance Review Coordination Team` label; both sides retain ten
  text elements with no clipping or overlap. No unresolved section, task,
  actor, score face, guide, label, clipping, overlap, or paint-order defect
  remains.
- All 16 Requirement contact sheets and all 256 same-source pairs were
  manually inspected. Cases 131 and 144 contain duplicate `<<contains>>`
  labels, while cases 183 and 209 contain duplicate `<<satisfies>>` labels;
  the comparator greedily cross-matched equivalent duplicates. All four retain
  44 matching text elements with P95 normalized text-center distance below
  `0.016`, no clipping or overlap, and passing raster checks. No unresolved
  requirement, element, relationship, marker, label, styling, clipping,
  overlap, or paint-order defect remains.
- All 16 Git Graph contact sheets and all 256 same-source pairs were manually
  inspected. The 20 queued cases are text-overlap threshold findings caused by
  browser/Compose text-bound differences; their P95 normalized text-center
  distance is at most `0.052`, mask IoU is at least `0.639`, and edge F1 is at
  least `0.828`. Every expected label is present and unclipped, and the
  corrected `parity_gitgraph_005` now preserves upstream commit-label paint
  order. No unresolved commit, branch, merge, cherry-pick, label, clipping,
  overlap, or paint-order defect remains.
- All 16 Mindmap contact sheets and all 256 same-source pairs were manually
  inspected. The 107 queued cases preserve every expected node, label,
  hierarchy edge, shape, and section color; their differences are
  CoSE-Bilkent branch rotations or mirrors caused by small platform text-size
  changes. All 107 pass clipping, overlap, paint-order, edge, and color checks;
  Dagre and tidy-tree cases pass automatically. No unresolved hierarchy,
  shape, text, clipping, overlap, or paint-order defect remains.
- All 16 Packet contact sheets and all 256 same-source pairs were manually
  inspected. Field boundaries, bit numbers, cross-row splitting, titles,
  compact and responsive configurations, Unicode, clipping, and paint order
  match the Official output. Long labels overflow narrow one-bit fields
  identically on both sides, preserving Mermaid's renderer behavior. No
  unresolved field, row, label, title, clipping, overlap, or paint-order
  defect remains.
- All 16 Radar contact sheets and all 256 same-source pairs were manually
  inspected. Axis order and labels, circular and polygon graticules, linear and
  cubic curves, value scaling, legends, titles, themes, clipping, and paint
  order match the Official output. No unresolved axis, curve, graticule,
  legend, title, clipping, overlap, or paint-order defect remains.
- All 16 Sankey contact sheets and all 256 same-source pairs were manually
  inspected. Node order and alignment, link routing and color modes, legacy
  and outlined labels, value formatting, custom node colors, clipping, and
  paint order match the Official output. No unresolved node, link, label,
  value, color, clipping, overlap, or paint-order defect remains.
- All 16 Treemap contact sheets and all 256 same-source pairs were manually
  inspected. Hierarchy, D3 squarify partitions, classes, colors, borders,
  adaptive labels, values, formats, Unicode, responsive sizing, clipping, and
  paint order match the Official output. The 237 queued cases contain only
  Canvas/SVG text-position findings; 198 also cross the overlap-topology
  threshold for intentionally same-row branch labels and values. No unresolved
  hierarchy, rectangle, style, label, value, clipping, or paint-order defect
  remains.
- All 16 Venn contact sheets and all 256 same-source pairs were manually
  inspected. Weighted circles, intersections, labels, text nodes, styles,
  configuration, themes, clipping, and paint order match the Official output.
  The 40 review cases repeat two benign text-overlap font-box patterns. No
  unresolved Venn defect remains.
- All 16 Ishikawa contact sheets and all 256 same-source pairs were manually
  inspected. Effect ownership, indentation normalization, recursive branch
  order, alternating upper/lower causes, fish-head geometry, wrapping,
  configuration, themes, clipping, and paint order match the Official output.
  Matrix detail passed automatically with no review queue.
- All 16 Cynefin contact sheets and all 256 same-source pairs were manually
  inspected. Five-domain placement, seeded boundaries, cliff and confusion
  geometry, item overflow, transitions, themes, labels, clipping, and paint
  order match the Official output. Matrix detail passed automatically with no
  review queue.
- All 16 Event Modeling contact sheets and all 256 same-source pairs were
  manually inspected. Every queued case contains only `text-segmentation`:
  Official combines the title and payload in one `foreignObject`, while Native
  emits the same complete visible content as a centered bold title and a
  left-aligned monospace payload. No unresolved swimlane, card, relation,
  marker, color, label, clipping, overlap, or paint-order defect remains.
- All 16 Agentflow contact sheets and all 256 same-source pairs were manually
  inspected with Dagre selected explicitly on both renderers. Typed shapes,
  nested/global/collapsed flows, connectors, sequence/reference/failure edges,
  labels, themes, clipping, and paint order match the Official output. Matrix
  detail passed automatically with no review queue.
- All 16 Block contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's positioned shape sizing. The three queued
  cases contain only paint-order occlusion ratio threshold findings. Grids,
  spans, spaces, composites, shapes, arrows, markers, classes, styles, labels,
  clipping, and visible paint order match the Official output.
- All 16 Swimlanes contact sheets and all 256 same-source pairs were manually
  inspected after matching rotated title bounds and pivots. The 60 queued
  cases contain only text-position findings along semantically equivalent
  orthogonal routes. Lane ownership, nodes, labels, markers, endpoints,
  clipping, overlap, and paint order match the Official output.
- All 16 Architecture contact sheets and all 256 same-source pairs were
  manually inspected. Services, exact built-in icons, icon text, nested
  groups, junctions, directional ports, arrows, labels, boundary modifiers,
  row/column alignments, clipping, overlap, and paint order match the Official
  output. Matrix detail passed automatically with no review queue.
- All 16 C4 contact sheets and all 256 same-source pairs were manually
  inspected. Diagram levels, people, systems, containers, components,
  database and queue shapes, nested boundaries, deployment nodes, relation
  variants, dynamic indexes, styles, labels, clipping, overlap, and paint
  order match the Official output. Matrix detail passed automatically with no
  review queue.
- All 16 Railroad contact sheets and all 256 same-source pairs were manually
  inspected. Railroad IR, EBNF, ABNF, and PEG rules preserve symbol types,
  sequence and branch topology, optional and repetition paths, metadata,
  styles, labels, clipping, overlap, and paint order. Matrix detail passed
  automatically with no review queue.

This is a per-family result. Overall status remains Not Stable until the
remaining 4 official families are translated and all 33 family gates pass.

## What This Report Does And Does Not Prove

This report proves that the exact source-controlled corpus:

- compiles through the Kotlin parser, database, layout, and SceneGraph pipeline;
- produces non-empty Native Canvas output;
- is accepted and rendered by the pinned Mermaid.js `12.0.0` reference;
- has been captured side by side at the same `1200 x 900` viewport;
- passes automated blank-image and severe content-geometry checks;
- can be regenerated from the repository scripts.

It does **not** prove that every possible legal Mermaid program is supported.
The automated image gate measures content bounds and foreground density, not
full semantic or pixel equality, so manual review remains required. Platform
font metrics and text wrapping may differ. An adopter can still use telemetry
and gradual rollout to manage its own release risk, but that operational
choice is outside this code-level rating.

## Evidence Layers

The three large evidence sets answer different questions and are not counted
as substitutes for each other:

1. **384 independent production scenarios.** These are hand-authored,
   production-like structures used for capability coverage, deterministic
   replay, manual review, performance soak, and the regular Quality Gate.
2. **7,424 Native/Official visual-matrix cases.** Each diagram type contributes
   256 unique Mermaid sources, derived deterministically from 13 or 14 complex
   structural seeds and 20 visible text/layout-pressure profiles. This is not a
   claim of 256 unrelated topologies per type.
3. **7,424 Native-only randomized stress inputs.** These separately exercise
   parser and layout robustness. They are not presented as Mermaid.js parity
   evidence.

## Independent Production Corpus

The canonical corpus is
[`tools/official-reference/production-corpus.mjs`](../tools/official-reference/production-corpus.mjs).
It includes 147 release-candidate cases plus 237 conformance cases that are
also independent from the demo gallery. Generated Kotlin copies are
consumed independently by core tests and the Web audit screen. The original
cases retain their legacy `rc_` IDs for evidence continuity; additional cases
use `prod_`. Neither set can be resolved through the normal demo gallery.

| Diagram | Production cases | Capability points | Scenario examples | Manual visual result |
| --- | ---: | ---: | --- | --- |
| Flowchart | 14 | 16/16 | orchestration, edge semantics, advanced shapes, nested domains | Replacement detail pass; 14/14 production and 256/256 matrix; 16/16 sheets manually reviewed |
| XY Chart | 13 | 16/16 | latency, categorical and numeric axes, horizontal labels, mixed plots | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Quadrant Chart | 13 | 17/17 | axes, quadrant labels, boundary points, styles, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Timeline | 13 | 16/16 | directions, sections, periods, events, color scales, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Sequence | 14 | 16/16 | checkout saga, lifecycle, self messages, parallel and critical regions | Replacement detail pass; 14/14 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Class | 13 | 16/16 | commerce, namespaces, generics, relations, annotations | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| State | 13 | 16/16 | fulfillment, nested composites, fork/join, concurrency, notes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Entity Relationship | 13 | 16/16 | commerce, aliases, attributes, cardinalities, nested subgraphs | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Gantt | 13 | 16/16 | release plans, date units, exclusions, top axes, vertical markers | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Pie | 13 | 16/16 | cost, escaped labels, donut, legends, themes, many slices | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| User Journey | 13 | 16/16 | sections, scores, actor order, metadata, configuration, long text | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Requirement | 13 | 17/17 | typed requirements, elements, relationships, directions, styling, metadata | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Git Graph | 13 | 24/24 | commits, branches, merges, cherry-picks, orientations, configuration, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Mindmap | 13 | 22/22 | hierarchy, shapes, text, CoSE-Bilkent, Dagre, tidy tree, configuration, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Packet | 13 | 17/17 | explicit and counted fields, packet-beta, row splitting, metadata, configuration, responsive sizing | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Radar | 13 | 25/25 | axes, curve entry forms, value ranges, graticules, legends, metadata, configuration, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Sankey | 13 | 27/27 | CSV records, D3 Sankey layout, all alignments, value labels, link and node colors, configuration | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Treemap | 13 | 39/39 | hierarchy, multiple roots, D3 squarify layout, classes, values and formats, metadata, dimensions, fonts, responsive sizing | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Venn | 13 | 38/38 | weighted sets and intersections, Venn.js/fmin optimization, pairwise completion, text nodes, styles, dimensions, debug layout, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; 40 matrix alerts manually accepted; 16/16 sheets manually reviewed |
| Ishikawa | 13 | 21/21 | headers, effect and root-only forms, indentation hierarchy, alternating branches, recursive descendants, fish-head geometry, wrapping, entities, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; one production manifest-only review accepted; 16/16 sheets manually reviewed |
| Cynefin | 13 | 30/30 | headers, five domains, fixed layout, items, transitions, confusion overflow, seeded boundaries, dimensions, themes, metadata | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Event Modeling | 13 | 22/22 | frame forms, entity aliases, swimlanes, inferred/explicit relations, inline data, reset behavior, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; title/payload segmentation reviews accepted; 16/16 sheets manually reviewed |
| Agentflow | 13 | 32/32 | typed nodes and edge semantics, nested/global/collapsed flows, connectors, metadata, diagnostics, configuration, themes, Dagre layout | Replacement detail accepted; 13/13 production and 256/256 matrix; four production structural-comparator reviews accepted; 16/16 sheets manually reviewed |
| Block | 13 | 31/31 | grids, spans, spaces, nested composites, documented shapes, block arrows, links, markers, classes, inline styles, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; three matrix paint-order threshold reviews accepted; 16/16 sheets manually reviewed |
| Swimlanes | 13 | 15/15 | directions, lane ownership and defaults, nested subgraphs, flowchart shapes, cross-lane edges, orthogonal routes, cycles, styles, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; 63 text-position alerts manually accepted; 16/16 sheets manually reviewed |
| Architecture | 13 | 18/18 | services, icons, nested groups, junctions, directional ports, arrows, labels, boundary modifiers, row/column alignment, configuration, deterministic seed, metadata | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| C4 | 13 | 26/26 | five diagram levels, people, systems, containers, components, external/database/queue variants, nested boundaries, deployment nodes, relation variants, dynamic indexes, styles, configuration, metadata | Replacement detail accepted; 13/13 production and 256/256 matrix; one production representation-only review accepted; 16/16 sheets manually reviewed |
| Railroad | 18 | 25/25 | Railroad IR, EBNF, ABNF, PEG, terminals, non-terminals, sequences, choices, optional and repeated expressions, notation-specific constructs, configuration, themes, metadata | Replacement detail pass; 18/18 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Kanban | 13 | 17/17 | sections, tasks, metadata, priorities, ticket links, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |

## Large-Scale Visual Matrix

The large-scale matrix contains 256 unique sources for each of Flowchart,
XY Chart, Quadrant Chart, Timeline, Sequence, Class, State, Entity
Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, Mindmap,
Packet, Radar, Sankey, Treemap, Venn, Ishikawa, Cynefin, Event Modeling,
Agentflow, Block, Swimlanes, Architecture, C4, Railroad, and Kanban:

- 7,424 unique Mermaid sources;
- 7,424 CMP Native screenshots;
- 7,424 Mermaid.js `12.0.0` screenshots;
- 464 paged contact sheets, with 16 same-source pairs per page;
- source, seed, profile, feature, screenshot, and SHA-256 metadata;
- per-pair content bounds and foreground-density metrics.

**[Open all 464 paged Native/Official comparison images](assets/stability-report/visual-parity-evidence.md).**

Machine-readable evidence:
[manifest](assets/stability-report/visual-parity-manifest.json) and
[geometry report](assets/stability-report/visual-parity-geometry.json).
The geometry gate accepted all 7,424 pairs. Across the complete matrix,
Native/Official width ratios were `0.956-1.281`, height ratios were
`0.815-1.236`, and foreground-ink ratios were `0.545-1.584`, within the
source-controlled thresholds.

## Independent Production Visual Evidence

Each contact sheet uses the same Mermaid source on both sides:

- left: CMP Native, rendered through Kotlin and Compose Canvas;
- right: Mermaid.js `12.0.0`, loaded from the repository's pinned local asset.

The source IDs and scenario descriptions are printed above every pair.

<details open>
<summary><strong>Flowchart: 14 production scenarios</strong></summary>

![Flowchart complex Native and Official corpus](assets/stability-report/flowchart-complex-corpus.png)

The replacement audit passed all 14 production scenarios and all 256 matrix
pairs with no review queue. The matrix width, height, and foreground-ink ratios
were `1.026-1.119`, `0.945-1.047`, and `0.948-1.241`. All 16 matrix contact
sheets were manually reviewed. During that review, hidden arrows on Bang and
Cloud nodes exposed a mismatch between sampled path bounds and edge
intersection bounds. The translated shapes now follow Mermaid's
`updateNodeBounds` plus `intersect.rect` behavior, and the detail auditor now
requires marker endpoint anchors and compares their later-paint occlusion
depth, so marker metadata alone cannot satisfy the gate.

</details>

<details open>
<summary><strong>Cynefin: 13 production scenarios</strong></summary>

![Cynefin complex Native and Official corpus](assets/stability-report/cynefin-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs. Width, height,
and foreground-ink ratios were `1.013-1.021`, `1.011-1.019`, and
`1.019-1.041`; detail was `13 pass / 0 review / 0 fail`.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `1.013-1.034`,
`1.009-1.019`, and `1.020-1.052`. The raw
[production detail](assets/stability-report/cynefin-production-detail.json),
[production geometry](assets/stability-report/cynefin-production-geometry.json),
[matrix detail](assets/stability-report/cynefin-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/cynefin-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Event Modeling: 13 production scenarios</strong></summary>

![Event Modeling complex Native and Official corpus](assets/stability-report/eventmodeling-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.029-1.044`, `1.013-1.044`, and
`1.029-1.115`. Production detail was `7 pass / 6 review / 0 fail`.
The six reviews contain only `text-segmentation`: Official combines each card
title and payload in one `foreignObject`, while Native represents the same
visible content as a centered bold title and left-aligned monospace payload.

The matrix geometry passed all 256 pairs with ratios `1.025-1.037`,
`1.008-1.061`, and `0.998-1.132`. Matrix detail was
`0 pass / 256 review / 0 fail`, with the same text-segmentation finding in
every case and no other finding code. The raw
[production detail](assets/stability-report/eventmodeling-production-detail.json),
[production geometry](assets/stability-report/eventmodeling-production-geometry.json),
[matrix detail](assets/stability-report/eventmodeling-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/eventmodeling-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Agentflow: 13 production scenarios</strong></summary>

![Agentflow complex Native and Official corpus](assets/stability-report/agentflow-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.041-1.186`, `0.820-1.095`, and
`0.766-1.206`. Production detail was `9 pass / 4 review / 0 fail`.
The two collapsed-flow reviews classify the compound summary shape's separator
as a stroke-pattern difference; the connector and canonical-shape reviews
classify compound SVG/SceneGraph decomposition as an element-count
difference. Manual side-by-side review accepted all four with complete labels,
equivalent visible geometry, correct edge semantics, and no clipping, overlap,
or paint-order defect.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `1.040-1.222`,
`0.854-1.086`, and `0.773-1.287`. The raw
[production detail](assets/stability-report/agentflow-production-detail.json),
[production geometry](assets/stability-report/agentflow-production-geometry.json),
[matrix detail](assets/stability-report/agentflow-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/agentflow-visual-parity-geometry.json)
reports are published beside the screenshots. Dagre was selected explicitly
for both renderers; this evidence does not claim equivalence with Mermaid's
default ELK layout. The production contact sheet and all 16 matrix contact
sheets were manually reviewed.

</details>

<details open>
<summary><strong>Block: 13 production scenarios</strong></summary>

![Block complex Native and Official corpus](assets/stability-report/block-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`0.970-1.116`, `0.880-1.069`, and `0.908-1.388`.

The matrix geometry passed all 256 pairs with ratios `0.970-1.281`,
`0.856-1.078`, and `0.840-1.342`. Matrix detail was
`253 pass / 3 review / 0 fail`; the three reviews contain only
paint-order occlusion ratio threshold findings, with complete visible shapes,
labels, edges, markers, clipping, and paint order. The raw
[production detail](assets/stability-report/block-production-detail.json),
[production geometry](assets/stability-report/block-production-geometry.json),
[matrix detail](assets/stability-report/block-visual-parity-detail.json), and
[matrix geometry](assets/stability-report/block-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Swimlanes: 13 production scenarios</strong></summary>

![Swimlanes complex Native and Official corpus](assets/stability-report/swimlanes-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.007-1.191`, `0.912-1.070`, and `0.928-1.603`. Production
detail was `10 pass / 3 review / 0 fail`.

The matrix geometry passed all 256 pairs with ratios `1.028-1.186`,
`0.883-1.070`, and `0.899-1.551`. Matrix detail was
`196 pass / 60 review / 0 fail`. All 63 production and matrix reviews contain
only `text-position`; manual review confirmed complete labels placed along
semantically equivalent orthogonal routes, with matching lanes, nodes,
markers, endpoints, clipping, overlap, and paint order. The raw
[production detail](assets/stability-report/swimlanes-production-detail.json),
[production geometry](assets/stability-report/swimlanes-production-geometry.json),
[matrix detail](assets/stability-report/swimlanes-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/swimlanes-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Architecture: 13 production scenarios</strong></summary>

![Architecture complex Native and Official corpus](assets/stability-report/architecture-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.020-1.232`, `0.996-1.236`, and `0.985-1.561`.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `1.020-1.232`,
`0.996-1.236`, and `0.986-1.584`. The raw
[production detail](assets/stability-report/architecture-production-detail.json),
[production geometry](assets/stability-report/architecture-production-geometry.json),
[matrix detail](assets/stability-report/architecture-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/architecture-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>C4: 13 production scenarios</strong></summary>

![C4 complex Native and Official corpus](assets/stability-report/c4-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.028-1.118`, `0.884-1.048`, and `1.004-1.203`. Production
detail was `12 pass / 1 review / 0 fail`. The sole review is an equivalent
manifest representation: Official SVG records database and queue silhouettes
as multiple paths, while Native records each visible silhouette as one
semantic shape. Side-by-side pixels, expected text, geometry, colors, and
paint order were manually verified.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `0.996-1.153`,
`0.992-1.046`, and `1.005-1.252`. The raw
[production detail](assets/stability-report/c4-production-detail.json),
[production geometry](assets/stability-report/c4-production-geometry.json),
[matrix detail](assets/stability-report/c4-visual-parity-detail.json), and
[matrix geometry](assets/stability-report/c4-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Railroad: 18 production scenarios</strong></summary>

![Railroad complex Native and Official corpus](assets/stability-report/railroad-complex-corpus.png)

The production geometry and detail audits passed all 18 pairs with
`18 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.038-1.120`, `1.028-1.131`, and `1.078-1.281`.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `1.035-1.116`,
`1.009-1.130`, and `1.023-1.319`. The raw
[production detail](assets/stability-report/railroad-production-detail.json),
[production geometry](assets/stability-report/railroad-production-geometry.json),
[matrix detail](assets/stability-report/railroad-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/railroad-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>XY Chart: 13 production scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.016-1.020`, `1.012-1.015`, and `0.967-1.144`; matrix ratios were
`1.008-1.029`, `0.995-1.041`, and `0.918-1.163`. All 16 matrix contact sheets
were manually reviewed. The final translation preserves Mermaid's component
insertion order and its browser fallback from an invalid negative bar-label
font size to inherited `16px`.

</details>

<details open>
<summary><strong>Quadrant Chart: 13 production scenarios</strong></summary>

![Quadrant Chart complex Native and Official corpus](assets/stability-report/quadrant-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.007-1.023`, `1.011-1.022`, and `1.025-1.036`; matrix ratios
were `1.007-1.035`, `1.011-1.022`, and `1.024-1.048`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Timeline: 13 production scenarios</strong></summary>

![Timeline complex Native and Official corpus](assets/stability-report/timeline-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.029-1.051`, `1.042-1.078`, and `1.062-1.131`; matrix ratios
were `1.026-1.053`, `1.038-1.087`, and `1.052-1.136`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Sequence: 14 production scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The replacement audit passed all 14 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.019-1.254`, `0.910-1.111`, and `0.939-1.327`; matrix ratios were
`1.023-1.068`, `0.893-1.045`, and `0.926-1.148`. All 16 matrix contact sheets
were manually reviewed. The final translation follows Mermaid's
`calculateLoopBounds`, `adjustLoopHeightForWrap`, and `wrapLabel` behavior for
control titles, preserves marker and activation paint order, and reports
created-participant text at its measured glyph bounds.

</details>

<details open>
<summary><strong>Class: 13 production scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.036-1.196`, `0.906-1.055`, and `0.723-1.230`; matrix ratios were
`1.030-1.208`, `0.897-1.055`, and `0.781-1.227`. All 16 matrix contact sheets
were manually reviewed. The final translation follows Mermaid's asymmetric
`textHelper` group bounds for class width, exact empty and method-only
compartment spacing, note padding, and marker-aware terminal placement.

</details>

<details>
<summary><strong>State: 13 production scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `0.995-1.206`, `1.030-1.093`, and `1.066-1.408`; matrix ratios
were `0.963-1.224`, `0.968-1.089`, and `1.031-1.383`. All 16 matrix contact
sheets were manually reviewed. The final translation preserves scoped
start/end markers, nested composite and concurrency boundaries, note
placement, pseudostates, and marker-aware transition routing.

</details>

<details open>
<summary><strong>Entity Relationship: 13 production scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

The replacement audit accepted all 13 production scenarios and all 256 matrix
pairs with no failures. Production detail results were
`10 pass / 3 manually reviewed / 0 fail`; matrix results were
`196 pass / 60 manually reviewed / 0 fail`. All reviews were isolated
text-position threshold findings on large repeated structures, while text
presence, clipping, overlap, paint order, marker checks, and raster checks
passed. Production width, height, and foreground-ink ratios were
`1.033-1.231`, `0.892-1.085`, and `0.519-1.373`; matrix ratios were
`1.033-1.078`, `0.882-1.043`, and `0.545-1.150`. All 16 matrix contact sheets
were manually reviewed. The final translation propagates each parent Dagre
graph's `nodesep` and `ranksep + 25` into recursively extracted subgraphs,
matching Mermaid.js `12.0.0` `measureDagreGraph`.

</details>

<details open>
<summary><strong>Gantt: 13 production scenarios</strong></summary>

![Gantt complex Native and Official corpus](assets/stability-report/gantt-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with `0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.035-1.037`, `0.883-0.961`, and `1.003-1.027`; matrix ratios
were `1.035-1.037`, `0.883-0.961`, and `0.964-1.023`. All 16 matrix contact
sheets were manually reviewed. The formal gate uses a shared `1200 x 900`
viewport because Mermaid derives Gantt width from
`elem.parentElement.offsetWidth` and uses `1200` only when that width is
unavailable; the Kotlin scene uses the same `1200` default in the absence of
a browser parent. Multi-unit ticks preserve D3 `interval.every(count)` epoch
and calendar-field anchoring.

</details>

<details>
<summary><strong>Pie: 13 production scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Matrix width, height, and foreground-ink ratios
were `0.992-1.050`, `0.994-1.019`, and `0.993-1.042`. All 16 matrix contact
sheets were manually reviewed after matching Mermaid's centered viewport
normalization and legend-aware bounds.

</details>

<details open>
<summary><strong>User Journey: 13 production scenarios</strong></summary>

![User Journey complex Native and Official corpus](assets/stability-report/journey-complex-corpus.png)

The historical review recorded all 256 Journey pairs as acceptable. That
result has now been replaced by a manifest-backed detail audit:
`237 pass / 19 manually reviewed / 0 fail`. All 19 reviews are the same benign
platform-font line-break difference for the complete
`Regional Compliance Review Coordination Team` actor label; both sides retain
ten text elements with no clipping or overlap. Matrix width, height, and
foreground-ink ratios are `1.020-1.032`, `1.036-1.057`, and `0.983-1.073`.
All 16 contact sheets were manually reviewed after matching Mermaid's
actor-legend text offset.

</details>

<details open>
<summary><strong>Requirement: 13 production scenarios</strong></summary>

![Requirement complex Native and Official corpus](assets/stability-report/requirement-complex-corpus.png)

The historical review recorded all 256 Requirement pairs as acceptable. That
result has now been replaced by a manifest-backed detail audit:
`252 pass / 4 manually reviewed / 0 fail`. The four reviews are false-positive
text-position findings caused by greedy matching of duplicate
`<<contains>>` or `<<satisfies>>` labels. All four retain 44 matching text
elements with no clipping or overlap and pass raster checks. Production detail
results are `13 pass / 0 review / 0 fail`; production width, height, and
foreground-ink ratios are `1.011-1.078`, `1.003-1.055`, and `0.955-1.162`.
Matrix ratios are `1.013-1.072`, `1.004-1.053`, and `0.974-1.148`. All 16
contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Git Graph: 13 production scenarios</strong></summary>

![Git Graph complex Native and Official corpus](assets/stability-report/gitgraph-complex-corpus.png)

The replacement detail audit accepted all 13 production pairs:
`12 pass / 1 manually reviewed / 0 fail`. The single review is a
text-overlap threshold finding caused by browser/Compose text-bound
differences; all four expected text elements match, no text is clipped,
P95 normalized text-center distance is `0.052`, mask IoU is `0.742`, and edge
F1 is `0.823`. Production width, height, and foreground-ink ratios are
`1.048-1.129`, `1.047-1.146`, and `1.074-1.342`.

The replacement matrix audit accepted all 256 pairs:
`236 pass / 20 manually reviewed / 0 fail`. All 20 reviews have the same
text-bound cause; expected text, clipping, paint order, and raster checks pass.
Matrix ratios are `1.036-1.154`, `1.032-1.137`, and `1.027-1.415`. All 16
contact sheets were manually reviewed, including the corrected
`parity_gitgraph_005` commit-label paint order.

</details>

<details open>
<summary><strong>Mindmap: 13 production scenarios</strong></summary>

![Mindmap complex Native and Official corpus](assets/stability-report/mindmap-complex-corpus.png)

The replacement production audit accepted all 13 pairs:
`9 pass / 4 manually reviewed / 0 fail`. Production width, height, and
foreground-ink ratios are `0.930-1.079`, `0.856-1.049`, and `0.759-1.037`.

The replacement matrix audit accepted all 256 pairs:
`149 pass / 107 manually reviewed / 0 fail`. The queued cases are
text-position findings from CoSE-Bilkent branch rotations or mirrors under
platform text-size perturbations; 67 also cross the foreground-mask threshold.
Every expected text, node, hierarchy edge, shape, and section color is
preserved, with no clipping, overlap, or paint-order mismatch. Matrix ratios
are `0.956-1.155`, `0.861-1.212`, and `0.771-1.431`. All 16 contact sheets
were manually reviewed. Icons and arbitrary CSS classes remain explicit
unsupported boundaries.

</details>

<details open>
<summary><strong>Kanban: 13 production scenarios</strong></summary>

![Kanban complex Native and Official corpus](assets/stability-report/kanban-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.041-1.127`, `0.870-1.134`, and `0.998-1.229`; matrix ratios
were `1.041-1.071`, `0.864-1.230`, and `0.996-1.110`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Packet: 13 production scenarios</strong></summary>

![Packet complex Native and Official corpus](assets/stability-report/packet-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.010-1.014`, `1.016-1.049`, and `1.008-1.023`; matrix ratios
were `1.011-1.015`, `1.006-1.056`, and `0.977-1.039`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Radar: 13 production scenarios</strong></summary>

![Radar complex Native and Official corpus](assets/stability-report/radar-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.017-1.033`, `1.015-1.021`, and `1.035-1.037`; matrix ratios
were `1.004-1.036`, `1.007-1.021`, and `1.005-1.036`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Sankey: 13 production scenarios</strong></summary>

![Sankey complex Native and Official corpus](assets/stability-report/sankey-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.050-1.060`, `1.045-1.059`, and `1.098-1.123`; matrix ratios
were `1.048-1.060`, `1.028-1.059`, and `1.062-1.124`. The production contact
sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Treemap: 13 production scenarios</strong></summary>

![Treemap complex Native and Official corpus](assets/stability-report/treemap-complex-corpus.png)

The replacement production audit accepted all 13 pairs:
`1 pass / 12 manually reviewed / 0 fail`. Production width, height, and
foreground-ink ratios were `1.033-1.044`, `1.029-1.039`, and `0.945-1.079`.
The replacement matrix audit accepted all 256 pairs:
`19 pass / 237 manually reviewed / 0 fail`; matrix ratios were
`1.033-1.076`, `1.029-1.039`, and `0.945-1.077`. All reviews contain only
Canvas/SVG text-position findings, with same-row branch label/value
overlap-topology threshold differences in 198 matrix cases. The production
contact sheet and all 16 matrix contact sheets were manually reviewed;
hierarchy, squarify geometry, styles, values, clipping, and paint order match
the pinned Mermaid.js `12.0.0` output.

</details>

<details open>
<summary><strong>Venn: 13 production scenarios</strong></summary>

![Venn complex Native and Official corpus](assets/stability-report/venn-complex-corpus.png)

The replacement production audit accepted all 13 pairs:
`11 pass / 2 manually reviewed / 0 fail`. Production width, height, and
foreground-ink ratios were `1.014-1.022`, `1.010-1.027`, and `1.028-1.044`.
The two reviews are the same benign text-overlap font-box patterns seen in the
matrix.

The matrix geometry audit passed all 256 pairs. Its raw detail result was
`216 pass / 40 review / 0 fail`; all 40 review cases were manually reviewed
and accepted with 0 unresolved defects. The reviews repeat only
`Engineering delivery`/`Shared roadmap` and `Client`/`Compose UI` overlap
threshold patterns. Matrix ratios were `0.997-1.032`, `0.992-1.026`,
and `0.999-1.059`. The raw [production detail](assets/stability-report/venn-production-detail.json), [production geometry](assets/stability-report/venn-production-geometry.json), [matrix detail](assets/stability-report/venn-visual-parity-detail.json), and [matrix geometry](assets/stability-report/venn-visual-parity-geometry.json) reports are published beside the screenshots. The production contact sheet and all 16 matrix contact sheets were manually reviewed.

</details>

<details open>
<summary><strong>Ishikawa: 13 production scenarios</strong></summary>

![Ishikawa complex Native and Official corpus](assets/stability-report/ishikawa-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.040-1.188`, `1.059-1.119`, and `1.067-1.326`.
The production detail result was `12 pass / 1 review / 0 fail`. The root-only
review is a manifest-only element-count difference: Native retains the
zero-length spine while Official omits it from visible geometry. Raster output
and effect semantics match.

The matrix geometry and detail audits passed all 256 pairs with
`256 pass / 0 review / 0 fail`; matrix ratios were `1.040-1.173`,
`1.023-1.119`, and `1.058-1.311`. The raw
[production detail](assets/stability-report/ishikawa-production-detail.json),
[production geometry](assets/stability-report/ishikawa-production-geometry.json),
[matrix detail](assets/stability-report/ishikawa-visual-parity-detail.json),
and
[matrix geometry](assets/stability-report/ishikawa-visual-parity-geometry.json)
reports are published beside the screenshots. The production contact sheet
and all 16 matrix contact sheets were manually reviewed.

</details>

The generated capture metadata, byte sizes, and SHA-256 values are available in

[`manifest.json`](assets/stability-report/manifest.json). Per-case Native versus
Official content bounds, foreground density, ratios, thresholds, and failures
are recorded in
[`geometry-report.json`](assets/stability-report/geometry-report.json).

## Automated Test Evidence

The repository-level
[`Quality Gate`](../.github/workflows/quality.yml) repeats the JVM tests,
cross-platform builds, generated-corpus and capability-coverage checks, source
and credential scan, production runtime-isolation check, debug/release APK
permission audit, 768-image capture, visual geometry gate, and Web load test
on every push to `main` and every pull request. The
[`Full Visual Parity`](../.github/workflows/full-visual-parity.yml) workflow
runs the 7,424-pair matrix weekly and on demand in 29 parallel diagram jobs.

The full verification command completed successfully:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  :sample:androidApp:assembleDebug \
  :sample:androidApp:assembleRelease \
  :sample:webApp:wasmJsBrowserDistribution \
  :sample:desktopApp:createDistributable \
  :mermaid-core:compileKotlinIosArm64 \
  :mermaid-core:compileKotlinIosSimulatorArm64 \
  :mermaid-core:compileKotlinIosX64 \
  :mermaid-compose:compileKotlinIosArm64 \
  :mermaid-compose:compileKotlinIosSimulatorArm64 \
  :mermaid-compose:compileKotlinIosX64 \
  :mermaid-debug-ui:compileKotlinIosArm64 \
  :mermaid-debug-ui:compileKotlinIosSimulatorArm64 \
  :mermaid-debug-ui:compileKotlinIosX64
```

Result:

```text
BUILD SUCCESSFUL
mermaid-core: 670 tests
mermaid-compose: 24 tests
total: 694 tests
failures: 0
errors: 0
```

The current independent corpus test is
[`ProductionCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/ProductionCorpusTest.kt).
It compiles all 384 sources and rejects parser errors, invalid or non-finite
geometry, scenes outside the 20,000-unit and 20,000-element limits, empty
SceneGraphs, and non-deterministic replay. The 237 conformance cases also
require selected semantic text to survive parsing and layout. The test renders
a representative of all 29 implemented diagram types with each of the 11 built-in
themes.

`ProductionCorpusTest` additionally renders all 7,424 visual-matrix sources,
checks the expected visible text, and rejects empty, invalid, or non-finite
SceneGraphs. The separate deterministic stress suites generate another 256
Native-only inputs for each of the 29 implemented diagram types, for 7,424
stress inputs in total. Unified graph diagrams exercise Dagre, while Mindmap
exercises CoSE-Bilkent, Dagre, and tidy tree.

## Determinism And Performance Evidence

Every one of the 384 independent scenarios is rendered twice and compared as a
complete `MermaidScene`, including dimensions, elements, paths, text, styles,
metadata, and z-order.

The JVM production soak performs two warmup rounds followed by five measured
rounds over all 384 scenarios:

```text
renders=1920
totalMs=814
p95Ms=1
retainedHeapBytes=60392
```

Enforced budgets are 45 seconds total, 500ms P95, and 64MiB retained heap after
forced GC.

## Runtime Load Matrix

The shared load screen renders the production corpus in a `LazyColumn`. The
current recorded Web run covers all 384 scenarios and walks from the first
Flowchart to the final Agentflow case. Android, iOS, and Desktop retain the
preceding 236-scenario run through the final Sankey case; those rows are
historical baselines, not evidence for the subsequently implemented families.

The automated Web scroll budget scales with the corpus at 80ms per case, with
a 15-second minimum. First content remains capped at 15 seconds and retained
JavaScript heap at 96MiB.

| Platform | Corpus | Result | Local evidence |
| --- | ---: | --- | --- |
| Android Emulator | 236 | Prior baseline passed | 21s auto-run; 230MiB peak PSS; 184MiB final PSS; final Sankey case reached |
| iOS Simulator | 236 | Prior baseline passed | Completion marker at 23s; 358MiB peak and final host RSS; no crash |
| Desktop | 236 | Prior baseline passed | Completion marker in three consecutive runs; latest 21s and 432MiB RSS |
| Web | 384 | Passed | 1.22s first content; 25.59s scroll; 18.1MiB retained JS heap; final Agentflow case reached |

Machine-readable measurements:
[Android](assets/runtime-load/android-emulator-metrics.json),
[iOS](assets/runtime-load/ios-simulator-metrics.json),
[Desktop](assets/runtime-load/desktop-metrics.json), and
[Web](assets/runtime-load/web-metrics.json).

Android and iOS screenshots show the final Sankey case from the 236-scenario
baseline. The Web screenshot shows the final Agentflow case from the current
384-scenario corpus:

| Android Emulator | iOS Simulator |
| :---: | :---: |
| <img src="assets/runtime-load/android-emulator-bottom.png" alt="Android load test final case" width="360"> | <img src="assets/runtime-load/ios-simulator-bottom.png" alt="iOS load test final case" width="360"> |

| Web |
| :---: |
| <img src="assets/runtime-load/web-bottom.png" alt="Web load test final case" width="700"> |

The current Desktop run is represented by its machine-readable process,
completion-marker, timing, and RSS record. The automated macOS session could
not capture the application window, so the older 132-case Desktop image is
intentionally not used as current Sankey evidence.

The iOS and Desktop runs identified and fixed the same class of native font
concurrency defect: background text measurement could race Compose/Skia glyph
drawing and crash in CoreText. iOS now uses `Dispatchers.Main.immediate`;
Desktop uses an explicit Swing EDT dispatcher. Android and Web retain
`Dispatchers.Default`.

## APK Permission Audit

Audited artifacts:

```text
sample/androidApp/build/outputs/apk/debug/androidApp-debug.apk
sample/androidApp/build/outputs/apk/release/androidApp-release-unsigned.apk
```

Declared permissions:

```text
com.swithun.cmpmermaid.sample.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
```

`android.permission.INTERNET` is not declared.

## Reproduce The Report

```bash
cd tools/official-reference
npm run generate:stability-corpus

cd ../..
JAVA_HOME=$(/usr/libexec/java_home -v 17) \
  ./gradlew \
    :mermaid-core:jvmTest \
    :mermaid-compose:jvmTest \
    :sample:androidApp:assembleDebug \
    :sample:androidApp:assembleRelease \
    :sample:webApp:wasmJsBrowserDistribution \
    :sample:desktopApp:createDistributable

python3 -m http.server 8093 \
  --directory sample/webApp/build/dist/wasmJs/productionExecutable \
  >/tmp/cmp-mermaid-web.log 2>&1 &
WEB_SERVER_PID=$!
trap 'kill "$WEB_SERVER_PID"' EXIT

cd tools/official-reference
AUDIT_SOURCE=production \
OUTPUT_DIR=captures/local/production-corpus \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=production \
INPUT_DIR=captures/local/production-corpus \
OUTPUT_FILE=captures/local/production-corpus/geometry-report.json \
npm run audit:stability-geometry

BASE_URL=http://127.0.0.1:8093/ \
OUTPUT_DIR=captures/local/load-test-web \
npm run test:web-load

CORPUS_SOURCE=production \
INPUT_DIR=captures/local/production-corpus \
OUTPUT_DIR=captures/local/production-contact-sheets \
npm run generate:stability-contact-sheets

AUDIT_SOURCE=visual-parity \
OUTPUT_DIR=captures/local/visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=docs/assets/stability-report/visual-parity-geometry.json \
npm run audit:stability-geometry

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=captures/local/visual-parity/detail-audit.json \
HEATMAP_DIR=captures/local/visual-parity/detail-diffs \
npm run audit:detail

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_DIR=docs/assets/stability-report \
CONTACT_SHEET_PAGE_SIZE=16 \
npm run generate:stability-contact-sheets
```

The capture command fails if a requested Native canvas or Official SVG does not
appear, if Mermaid reports an error, if an Official Gantt viewBox collapses, or
if a screenshot is below the minimum size. The geometry gate rejects blank
images and severe width, height, or foreground-density differences. The
contact-sheet generator verifies every expected pair and records its SHA-256.
Set `AUDIT_KIND` and `CORPUS_KIND` to one of the 29 diagram IDs to reproduce
a single 256-case partition instead of the complete matrix.

## Stable Acceptance Criteria

The Stable label requires all of these code-level gates:

- the independent complex corpus has no blocked visual category;
- Native/Official comparison includes automated semantic or perceptual
  thresholds with reviewed exceptions;
- the Quality Gate workflow is required and green on `main`;
- Android, Web, iOS, and Desktop have runtime smoke evidence, not compile-only
  evidence;
- bulk rendering has repeatable memory, latency, and long-running soak limits;
- no open high-severity correctness, crash, resource-exhaustion, or
  data-exposure defect exists for the supported contract.

These criteria are not currently satisfied. The current 29 implemented
families have completed the semantic, paint-order, and perceptual re-audit,
but 4 official Mermaid families are not yet implemented. The public code
status therefore remains **Not Stable** until the per-family gates in
[`full-diagram-roadmap.md`](full-diagram-roadmap.md) pass.
