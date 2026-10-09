# Mermaid 12.0.0 Stability Test Report

> [!NOTE]
> This report records the completed Stable evidence for all 33 official
> Mermaid `12.0.0` families. Kotlin 2.3.20 Android, Kotlin 1.7.21 Android,
> and iOS now have separate fresh 441-case physical-device matrices. Core soak,
> iOS Simulator, Desktop, and Web runtime measurements remain explicitly
> identified by their measured corpus sizes. See the
> [33-family roadmap](full-diagram-roadmap.md).

This report records published visual evidence for all 33 CMP Mermaid families.
The evidence is deliberately separate from the demo gallery and distinguishes
441 hand-authored production scenarios, 8,448 structurally distinct visual
cases, 8,448 derived text/layout stress inputs, and Native-only randomized
stress inputs.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Stable** |
| Mermaid compatibility baseline | `12.0.0` |
| Implemented families | 33/33; all family gates passing |
| Independent production scenarios | 441: 167 release-candidate cases plus 274 additional conformance cases |
| Declared capability coverage | 738/738 points across 33 diagram types |
| Structural visual matrix | 8,448 structurally distinct Native/Official pairs: 256 per family |
| Text/layout stress inputs | 8,448 byte-distinct inputs derived from the 441 production scenarios; no additional structural coverage |
| Source-mutation parity | 8,448/8,448 Official accept/reject outcomes matched: 2,156 accepted and 6,292 rejected; all 6,292 Native rejections were `CONTENT_ERROR`; 16,896 screenshots; 0 crashes or timeouts |
| Native core render results | 441 production scenarios and all 8,448 structural visual cases render successfully; the text/layout stress inputs are tracked separately |
| Web Native/Official captures | 16,896 structural-matrix screenshots plus 882 independent-corpus and 16,896 malformed-source screenshots |
| Visual evidence | 528 freshly generated structural-matrix sheets plus 528 source-mutation sheets across all 33 families |
| Structural detail audit | 8,448/8,448 processed: 7,953 pass, 495 review, 0 fail |
| Automated visual geometry | 8,448/8,448 structural pairs have 0 hard failures; 4 CoSE ratio findings remain in the Mindmap review queue |
| Deterministic SceneGraph replay | 441 passed, 0 mismatches |
| Built-in theme matrix | 363/363 renders passed: 33 diagram types by 11 themes |
| Separate deterministic Native stress inputs | 7,936 |
| JVM tests | 883 passed, 0 failed |
| Core production soak | 2,075 renders; 674ms total; 1ms P95; 63,968 bytes retained heap |
| Physical-device runtime matrix | Kotlin 2.3.20 Android 8/8, Kotlin 1.7.21 Android 8/8, and iOS 6/6 passed; 9,702 corpus renders; 198 reviewed sentinels plus 22 completion screenshots; 0 crashes or Android ANRs |
| Runtime load matrix | All three physical-device tracks passed the complete 441-case corpus; Web retains the preceding 397-scenario baseline; iOS Simulator and Desktop retain the prior 236-scenario baseline |
| Platform build matrix | Android debug/release, Web production, Desktop distributable, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared in debug or release APK |
| Public-source safety scan | No organization-specific endpoint or credential pattern found |

**Current conclusion:** all 33 families pass the Native core, determinism,
theme, structural detail, geometry, and contact-sheet generation gates. The
supported Mermaid `12.0.0` contract is rated **Stable**.

## AI-Like Source-Mutation Safety Gate

This mutation corpus starts from 256 byte-distinct valid stress samples for
every supported diagram family and applies 16 mutation classes, with 16
variants per class. The bases reuse the 441 structural scenarios, so this gate
does not add semantic coverage.
These 8,448 AI-like sources cover declaration typos, response wrappers, mixed
syntax, truncation, omission, line-break loss, delimiter and operator damage,
punctuation substitution, and token splitting. Mermaid.js `12.0.0` is the
accept/error oracle. Every generated source is sent unchanged to CMP Native and
the pinned Official renderer. All 8,448 mutations passed:

- Official accepted 2,156 mutations and rejected 6,292; Native matched every
  accept/reject outcome;
- all 6,292 Native rejections returned a non-empty deterministic
  `CONTENT_ERROR`, with no runtime error;
- exact diagnostics matched for 1,798/6,292 paired errors and are reported
  independently rather than synthesized as a compatibility requirement;
- all 16,896 corresponding rendered/error-state screenshots were captured
  without a crash or timeout;
- each mutation profile was followed by a valid Native render without
  poisoning engine state.

**[Open the 528 paged Native/Official source-mutation comparison images](assets/invalid-source-report/invalid-source-evidence.md).**
The machine-readable
[manifest](assets/invalid-source-report/invalid-source-manifest.json) records
the base and mutation IDs, source hash, both screenshot hashes, capture sizes,
both outcomes, Native error type, and both error messages. This gate is
intentionally separate from the 8,448 legal-source visual matrix and does not
change its counts.

## Completed Structural Visual Gate

All 33 families have completed the structural visual gate. Each has 256
structurally distinct same-source Native/Official pairs, a 256-case geometry
result, and 16 paged contact sheets. The fresh total is:
`7,953 pass / 495 review / 0 fail`.

Review findings are retained as explicit representation differences:

- text position: 250 cases;
- text overlap: 140 cases;
- raster mask: 79 cases;
- stroke pattern: 30 cases;
- element count: 30 cases;
- text segmentation and text size: 25 cases each;
- paint-order occlusion ratio: 3 cases;
- text multiplicity: 1 case.

These categories may co-occur within one case. No expected-text loss,
structural-manifest error, clipping error, or geometry hard failure was
recorded. The geometry queue contains four ratio findings from three
CoSE-Bilkent Mindmap cases; fixed-input algorithm parity is tested separately,
so final large-tree coordinates are not claimed to be identical.

| Family | Pass | Review | Width ratio | Height ratio | Ink ratio |
| --- | ---: | ---: | ---: | ---: | ---: |
| Flowchart | 256 | 0 | `1.022-1.103` | `0.976-1.051` | `0.947-1.259` |
| XY Chart | 255 | 1 | `1.012-1.016` | `1.008-1.018` | `0.962-1.154` |
| Quadrant | 253 | 3 | `1.007-1.023` | `1.011-1.022` | `1.025-1.036` |
| Timeline | 256 | 0 | `1.020-1.056` | `1.032-1.072` | `1.036-1.136` |
| Kanban | 256 | 0 | `1.036-1.089` | `0.894-1.089` | `0.999-1.121` |
| Sequence | 231 | 25 | `1.015-1.272` | `0.897-1.090` | `0.927-1.437` |
| Class | 256 | 0 | `1.025-1.195` | `0.897-1.058` | `0.866-1.237` |
| State | 239 | 17 | `0.959-1.095` | `1.016-1.089` | `0.972-1.388` |
| Entity Relationship | 207 | 49 | `1.015-1.098` | `0.981-1.059` | `0.538-0.899` |
| Gantt | 205 | 51 | `1.035-1.037` | `0.891-0.966` | `0.832-1.027` |
| Pie | 241 | 15 | `1.011-1.020` | `0.999-1.014` | `1.002-1.039` |
| User Journey | 256 | 0 | `1.017-1.033` | `1.023-1.053` | `0.910-1.107` |
| Requirement | 256 | 0 | `1.024-1.060` | `0.980-1.052` | `1.003-1.115` |
| Git Graph | 249 | 7 | `1.033-1.143` | `1.035-1.137` | `1.022-1.333` |
| Mindmap | 161 | 95 | `0.878-1.464` | `0.798-1.442` | `0.745-1.336` |
| Packet | 256 | 0 | `1.009-1.014` | `1.016-1.047` | `1.008-1.056` |
| Radar | 256 | 0 | `1.016-1.032` | `1.012-1.021` | `1.019-1.039` |
| Sankey | 256 | 0 | `1.021-1.053` | `1.039-1.059` | `1.079-1.112` |
| Treemap | 255 | 1 | `1.033-1.045` | `1.034-1.047` | `1.067-1.093` |
| Venn | 143 | 113 | `1.007-1.147` | `0.967-1.081` | `0.784-1.406` |
| Ishikawa | 256 | 0 | `1.037-1.148` | `1.017-1.119` | `0.992-1.299` |
| Cynefin | 256 | 0 | `1.014-1.023` | `1.011-1.019` | `1.027-1.050` |
| Event Modeling | 256 | 0 | `1.021-1.049` | `1.053-1.227` | `1.018-1.356` |
| Agentflow | 226 | 30 | `1.009-1.130` | `0.918-1.100` | `0.841-1.210` |
| Block | 256 | 0 | `0.976-1.190` | `0.927-1.080` | `0.868-1.423` |
| Swimlanes | 198 | 58 | `1.000-1.123` | `0.953-1.076` | `0.954-1.527` |
| Architecture | 233 | 23 | `1.015-1.068` | `0.809-1.218` | `0.883-1.473` |
| C4 | 250 | 6 | `0.995-1.141` | `0.923-1.049` | `0.969-1.103` |
| Railroad | 256 | 0 | `1.016-1.107` | `1.020-1.125` | `1.014-1.263` |
| TreeView | 256 | 0 | `1.010-1.160` | `0.956-1.027` | `0.938-1.086` |
| Use Case | 255 | 1 | `0.984-1.150` | `0.969-1.117` | `0.910-1.405` |
| Wardley Map | 256 | 0 | `1.008-1.015` | `1.019-1.020` | `0.973-1.103` |
| ZenUML | 256 | 0 | `1.026-1.084` | `1.025-1.089` | `0.900-1.175` |
All 528 structural contact sheets were regenerated from the same 8,448
source cases recorded in the manifest. The 495 review cases are distributed
as follows: Venn 113, Mindmap 95, Swimlanes 58, Gantt 51, Entity Relationship
49, Agentflow 30, Sequence 25, Architecture 23, State 17, Pie 15, Git Graph 7,
C4 6, Quadrant 3, and one each for Treemap, Use Case, and XY Chart. The other
16 families pass all 256 detail cases automatically. Per-family compatibility
pages and machine-readable reports preserve the individual findings and
geometry ranges.

All per-family results pass, so overall supported Mermaid `12.0.0`
compatibility is **Stable**.

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

The four large evidence sets answer different questions and are not counted
as substitutes for each other:

1. **441 independent production scenarios.** These are hand-authored,
   production-like structures used for capability coverage and deterministic
   replay. The published screenshot, geometry, and detail reports cover all
   441 same-source Native/Official pairs.
2. **8,448 structurally distinct visual cases.** Each diagram type contributes
   256 cases distinguished by its curated seed, seed feature set, generated
   structural item count, and family-specific syntax/topology profile. The
   published Native/Official evidence covers every case.
3. **8,448 generated text/layout stress inputs.** These byte-distinct inputs
   are derived from the 441 production scenarios. They remain a separate core
   robustness corpus and do not add structural or syntax coverage.
4. **7,936 Native-only randomized stress inputs.** These separately exercise
   parser and layout robustness. They are not presented as Mermaid.js parity
   evidence.

## Independent Production Corpus

The canonical corpus is
[`tools/official-reference/production-corpus.mjs`](../tools/official-reference/production-corpus.mjs).
It includes 167 release-candidate cases plus 274 conformance cases that are
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
| Event Modeling | 13 | 22/22 | frame forms, entity aliases, swimlanes, inferred/explicit relations, inline data, reset behavior, configuration, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Agentflow | 13 | 32/32 | typed nodes and edge semantics, nested/global/collapsed flows, connectors, metadata, diagnostics, configuration, themes, Dagre layout | Replacement detail accepted; 13/13 production and 256/256 matrix; four production structural-comparator reviews accepted; 16/16 sheets manually reviewed |
| Block | 13 | 31/31 | grids, spans, spaces, nested composites, documented shapes, block arrows, links, markers, classes, inline styles, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; four matrix paint-order threshold reviews accepted; 16/16 sheets manually reviewed |
| Swimlanes | 13 | 15/15 | directions, lane ownership and defaults, nested subgraphs, flowchart shapes, cross-lane edges, orthogonal routes, cycles, styles, configuration, themes | Replacement detail accepted; 13/13 production and 256/256 matrix; 60 text-position alerts manually accepted; 16/16 sheets manually reviewed |
| Architecture | 13 | 18/18 | services, icons, nested groups, junctions, directional ports, arrows, labels, boundary modifiers, row/column alignment, configuration, deterministic seed, metadata | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| C4 | 13 | 26/26 | five diagram levels, people, systems, containers, components, external/database/queue variants, nested boundaries, deployment nodes, relation variants, dynamic indexes, styles, configuration, metadata | Replacement detail accepted; 13/13 production and 256/256 matrix; one production representation-only review accepted; 16/16 sheets manually reviewed |
| Railroad | 18 | 24/24 | Railroad IR, EBNF, ABNF, PEG, terminals, non-terminals, sequences, choices, optional and repeated expressions, notation-specific constructs, configuration, themes, metadata | Replacement detail pass; 18/18 production and 256/256 matrix; 16/16 sheets manually reviewed |
| TreeView | 13 | 22/22 | indentation and box-drawing hierarchy, files and directories, annotations, descriptions, icons, configuration, themes, metadata | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Use Case | 18 | 38/38 | actors and actor variants, use cases, boundaries, associations and dependencies, markers, notes, JSON tables, classes/styles, configuration, themes, metadata | Replacement detail pass; 18/18 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Wardley Map | 13 | 28/28 | components, sourcing, dependencies, evolution, pipelines, notes, annotations, forces, themes, metadata; caller configuration is tested separately | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| ZenUML | 13 | 26/26 | participants, aliases, annotators, messages, assignments, replies, groups, control fragments, comments, dividers, Unicode | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Kanban | 13 | 17/17 | sections, tasks, metadata, priorities, ticket links, themes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |

## Structural Visual Matrix

The generated structural matrix and published evidence contain 256
structurally distinct sources for each of all 33 families. Uniqueness is based
on structural dimensions rather than labels or ordinals. The families are:
Flowchart,
XY Chart, Quadrant Chart, Timeline, Sequence, Class, State, Entity
Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, Mindmap,
Packet, Radar, Sankey, Treemap, Venn, Ishikawa, Cynefin, Event Modeling,
Agentflow, Block, Swimlanes, Architecture, C4, Railroad, TreeView, Use Case,
Kanban, Wardley Map, and ZenUML:

- 8,448 structurally distinct Mermaid sources;
- 8,448 unique structural signatures and source hashes;
- 8,448 CMP Native screenshots;
- 8,448 Mermaid.js `12.0.0` screenshots;
- 528 paged contact sheets, with 16 same-source pairs per page;
- source, structural dimensions, feature, screenshot, and SHA-256 metadata;
- per-pair content bounds and foreground-density metrics.

**[Open all 528 paged Native/Official comparison images](assets/stability-report/visual-parity-evidence.md).**

Machine-readable evidence:
[structural corpus accounting](assets/stability-report/visual-structure-corpus-metadata.json),
[manifest](assets/stability-report/visual-parity-manifest.json),
[detail report](assets/stability-report/visual-parity-detail.json), and
[geometry report](assets/stability-report/visual-parity-geometry.json).
The geometry gate recorded 0 hard failures across all 8,448 pairs. Overall
Native/Official width ratios were `0.878-1.464`, height ratios were
`0.798-1.442`, and foreground-ink ratios were `0.538-1.527`. Four out-of-range
width/height results from three CoSE-Bilkent Mindmap cases remain explicit
review findings because their detail manifests preserve the same structure;
all other ratios remain within the unchanged source-controlled thresholds.

## Independent Production Visual Evidence

Each contact sheet uses the same Mermaid source on both sides:

- left: CMP Native, rendered through Kotlin and Compose Canvas;
- right: Mermaid.js `12.0.0`, loaded from the repository's pinned local asset.

The source IDs and scenario descriptions are printed above every pair.

<details open>
<summary><strong>Flowchart: 14 production scenarios</strong></summary>

![Flowchart complex Native and Official corpus](assets/stability-report/flowchart-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.022-1.103`, height `0.976-1.051`, and foreground ink `0.947-1.259`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Cynefin: 13 production scenarios</strong></summary>

![Cynefin complex Native and Official corpus](assets/stability-report/cynefin-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs. Width, height,
and foreground-ink ratios were `1.013-1.021`, `1.011-1.019`, and
`1.019-1.041`; detail was `13 pass / 0 review / 0 fail`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.014-1.023`, height `1.011-1.019`, and foreground ink `1.027-1.050`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Event Modeling: 13 production scenarios</strong></summary>

![Event Modeling complex Native and Official corpus](assets/stability-report/eventmodeling-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.029-1.044`, `1.013-1.044`, and
`1.029-1.115`. Production detail passed `13 / 13`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.021-1.049`, height `1.053-1.227`, and foreground ink `1.018-1.356`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Agentflow: 13 production scenarios</strong></summary>

![Agentflow complex Native and Official corpus](assets/stability-report/agentflow-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.041-1.186`, `0.820-1.095`, and
`0.766-1.206`. Production detail was `11 pass / 2 review / 0 fail`.
The two collapsed-flow reviews classify the compound summary shape's separator
as a stroke-pattern difference. Geometry counts compare both render objects
and expanded primitives, eliminating the former compound-decomposition
reviews. Manual side-by-side review accepted both remaining cases with
complete labels, equivalent visible geometry, correct edge semantics, and no
clipping, overlap, or paint-order defect.

The fresh 256-case structural Native/Official audit reports `226 pass / 30 review / 0 fail`; geometry has 0 hard failures with width `1.009-1.130`, height `0.918-1.100`, and foreground ink `0.841-1.210`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Block: 13 production scenarios</strong></summary>

![Block complex Native and Official corpus](assets/stability-report/block-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`0.970-1.116`, `0.880-1.069`, and `0.908-1.388`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `0.976-1.190`, height `0.927-1.080`, and foreground ink `0.868-1.423`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Swimlanes: 13 production scenarios</strong></summary>

![Swimlanes complex Native and Official corpus](assets/stability-report/swimlanes-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.007-1.191`, `0.912-1.070`, and `0.928-1.603`. Production
detail was `10 pass / 3 review / 0 fail`.

The fresh 256-case structural Native/Official audit reports `198 pass / 58 review / 0 fail`; geometry has 0 hard failures with width `1.000-1.123`, height `0.953-1.076`, and foreground ink `0.954-1.527`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Architecture: 13 production scenarios</strong></summary>

![Architecture complex Native and Official corpus](assets/stability-report/architecture-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.020-1.232`, `0.996-1.236`, and `0.985-1.561`.

The fresh 256-case structural Native/Official audit reports `233 pass / 23 review / 0 fail`; geometry has 0 hard failures with width `1.015-1.068`, height `0.809-1.218`, and foreground ink `0.883-1.473`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>C4: 13 production scenarios</strong></summary>

![C4 complex Native and Official corpus](assets/stability-report/c4-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.028-1.118`, `0.884-1.048`, and `1.004-1.203`. Production
detail passed `13 / 13`. Geometry counts compare both render objects and
expanded primitives, so Official's decomposed database/queue paths and
Native's semantic shapes are validated without a representation-only review.

The fresh 256-case structural Native/Official audit reports `250 pass / 6 review / 0 fail`; geometry has 0 hard failures with width `0.995-1.141`, height `0.923-1.049`, and foreground ink `0.969-1.103`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Railroad: 18 production scenarios</strong></summary>

![Railroad complex Native and Official corpus](assets/stability-report/railroad-complex-corpus.png)

The production geometry and detail audits passed all 18 pairs with
`18 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.038-1.120`, `1.028-1.131`, and `1.078-1.281`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.016-1.107`, height `1.020-1.125`, and foreground ink `1.014-1.263`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>TreeView: 13 production scenarios</strong></summary>

![TreeView complex Native and Official corpus](assets/stability-report/treeview-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.011-1.169`, `0.975-1.062`, and `0.930-1.050`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.010-1.160`, height `0.956-1.027`, and foreground ink `0.938-1.086`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Use Case: 18 production scenarios</strong></summary>

![Use Case complex Native and Official corpus](assets/stability-report/usecase-complex-corpus.png)

The production geometry and detail audits passed all 18 pairs. Width, height,
and foreground-ink ratios were `1.036-1.160`, `0.971-1.115`, and
`1.043-1.316`.

The fresh 256-case structural Native/Official audit reports `255 pass / 1 review / 0 fail`; geometry has 0 hard failures with width `0.984-1.150`, height `0.969-1.117`, and foreground ink `0.910-1.405`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>XY Chart: 13 production scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `255 pass / 1 review / 0 fail`; geometry has 0 hard failures with width `1.012-1.016`, height `1.008-1.018`, and foreground ink `0.962-1.154`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Quadrant Chart: 13 production scenarios</strong></summary>

![Quadrant Chart complex Native and Official corpus](assets/stability-report/quadrant-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `253 pass / 3 review / 0 fail`; geometry has 0 hard failures with width `1.007-1.023`, height `1.011-1.022`, and foreground ink `1.025-1.036`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Timeline: 13 production scenarios</strong></summary>

![Timeline complex Native and Official corpus](assets/stability-report/timeline-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.020-1.056`, height `1.032-1.072`, and foreground ink `1.036-1.136`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Sequence: 14 production scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `231 pass / 25 review / 0 fail`; geometry has 0 hard failures with width `1.015-1.272`, height `0.897-1.090`, and foreground ink `0.927-1.437`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Class: 13 production scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.025-1.195`, height `0.897-1.058`, and foreground ink `0.866-1.237`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details>
<summary><strong>State: 13 production scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `239 pass / 17 review / 0 fail`; geometry has 0 hard failures with width `0.959-1.095`, height `1.016-1.089`, and foreground ink `0.972-1.388`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Entity Relationship: 13 production scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `207 pass / 49 review / 0 fail`; geometry has 0 hard failures with width `1.015-1.098`, height `0.981-1.059`, and foreground ink `0.538-0.899`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Gantt: 13 production scenarios</strong></summary>

![Gantt complex Native and Official corpus](assets/stability-report/gantt-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `205 pass / 51 review / 0 fail`; geometry has 0 hard failures with width `1.035-1.037`, height `0.891-0.966`, and foreground ink `0.832-1.027`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details>
<summary><strong>Pie: 13 production scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `241 pass / 15 review / 0 fail`; geometry has 0 hard failures with width `1.011-1.020`, height `0.999-1.014`, and foreground ink `1.002-1.039`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>User Journey: 13 production scenarios</strong></summary>

![User Journey complex Native and Official corpus](assets/stability-report/journey-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.017-1.033`, height `1.023-1.053`, and foreground ink `0.910-1.107`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Requirement: 13 production scenarios</strong></summary>

![Requirement complex Native and Official corpus](assets/stability-report/requirement-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.024-1.060`, height `0.980-1.052`, and foreground ink `1.003-1.115`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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

The fresh 256-case structural Native/Official audit reports `249 pass / 7 review / 0 fail`; geometry has 0 hard failures with width `1.033-1.143`, height `1.035-1.137`, and foreground ink `1.022-1.333`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Mindmap: 13 production scenarios</strong></summary>

![Mindmap complex Native and Official corpus](assets/stability-report/mindmap-complex-corpus.png)

The refreshed production audit accepted all 13 pairs:
`9 pass / 4 manually reviewed / 0 fail`. Production width, height, and
foreground-ink ratios are `0.949-1.122`, `0.863-1.049`, and `0.742-1.049`.
The refreshed production contact sheet was manually reviewed with no missing
content, clipping, or overlap.

The fresh 256-case structural Native/Official audit reports `161 pass / 95 review / 0 fail`; geometry has 0 hard failures with width `0.878-1.464`, height `0.798-1.442`, and foreground ink `0.745-1.336`. Four CoSE-Bilkent ratio findings remain review-only; final large-tree coordinate identity is not claimed. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Kanban: 13 production scenarios</strong></summary>

![Kanban complex Native and Official corpus](assets/stability-report/kanban-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.036-1.089`, height `0.894-1.089`, and foreground ink `0.999-1.121`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Packet: 13 production scenarios</strong></summary>

![Packet complex Native and Official corpus](assets/stability-report/packet-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.009-1.014`, height `1.016-1.047`, and foreground ink `1.008-1.056`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Radar: 13 production scenarios</strong></summary>

![Radar complex Native and Official corpus](assets/stability-report/radar-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.016-1.032`, height `1.012-1.021`, and foreground ink `1.019-1.039`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Sankey: 13 production scenarios</strong></summary>

![Sankey complex Native and Official corpus](assets/stability-report/sankey-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.021-1.053`, height `1.039-1.059`, and foreground ink `1.079-1.112`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Treemap: 13 production scenarios</strong></summary>

![Treemap complex Native and Official corpus](assets/stability-report/treemap-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `255 pass / 1 review / 0 fail`; geometry has 0 hard failures with width `1.033-1.045`, height `1.034-1.047`, and foreground ink `1.067-1.093`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Venn: 13 production scenarios</strong></summary>

![Venn complex Native and Official corpus](assets/stability-report/venn-complex-corpus.png)

The fresh 256-case structural Native/Official audit reports `143 pass / 113 review / 0 fail`; geometry has 0 hard failures with width `1.007-1.147`, height `0.967-1.081`, and foreground ink `0.784-1.406`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.037-1.148`, height `1.017-1.119`, and foreground ink `0.992-1.299`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Wardley Map: 13 production scenarios</strong></summary>

![Wardley Map complex Native and Official corpus](assets/stability-report/wardley-complex-corpus.png)

The replacement production detail and geometry audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.008-1.015`, `1.019-1.020`, and `0.970-1.106`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.008-1.015`, height `1.019-1.020`, and foreground ink `0.973-1.103`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>ZenUML: 13 production scenarios</strong></summary>

![ZenUML complex Native and Official corpus](assets/stability-report/zenuml-complex-corpus.png)

The replacement production detail and geometry audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.042-1.100`, `1.039-1.102`, and `1.094-1.238`.

The fresh 256-case structural Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.026-1.084`, height `1.025-1.089`, and foreground ink `0.900-1.175`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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
permission audit, independent-corpus capture, malformed-source error capture,
visual geometry gate, and Web load test on every push to `main` and every pull
request. The
[`Full Visual Parity`](../.github/workflows/full-visual-parity.yml) workflow
runs the 8,448-pair structural visual matrix weekly and on demand in 33
parallel diagram jobs.

The previously published cross-platform verification command completed
successfully:

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
mermaid-core: 717 tests
mermaid-compose: 28 tests
total: 745 tests
failures: 0
errors: 0
```

The current source-level JVM suite also passes:

```text
mermaid-core: 849 tests
mermaid-compose: 34 tests
total: 883 tests
failures: 0
errors: 0
```

The current independent corpus test is
[`ProductionCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/ProductionCorpusTest.kt).
It compiles all 441 sources and rejects parser errors, invalid or non-finite
geometry, scenes outside the 20,000-unit and 20,000-element limits, empty
SceneGraphs, and non-deterministic replay. The 274 conformance cases also
require selected semantic text to survive parsing and layout. The test renders
a representative of all 33 implemented diagram types with each of the 11 built-in
themes.

[`InvalidSourceCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/InvalidSourceCorpusTest.kt)
requires all 8,448 source mutations to return deterministic results, verifies
that every rejection is a non-empty `CONTENT_ERROR`, and checks that every
family can render a valid source after each mutation profile.
[`InvalidSourceOfficialErrorParityTest`](../mermaid-core/src/jvmTest/kotlin/com/swithun/cmpmermaid/core/InvalidSourceOfficialErrorParityTest.kt)
verifies Native accept/reject outcomes against the published Mermaid.js
manifest for all 8,448 mutations.

`ProductionCorpusTest` additionally verifies the accounting of all 8,448
text/layout stress inputs and renders all 8,448 structurally distinct visual
cases, checking unique structural signatures, expected visible text, and
finite non-empty SceneGraphs. The retained historical deterministic stress
baseline contains 7,936 Native-only inputs across the 31 families included
when it was captured.
Unified graph diagrams exercise Dagre, while Mindmap exercises CoSE-Bilkent,
Dagre, and tidy tree.

## Determinism And Performance Evidence

Every one of the 441 independent scenarios is rendered twice and compared as a
complete `MermaidScene`, including dimensions, elements, paths, text, styles,
metadata, and z-order.

The published JVM production soak performs two warmup rounds followed by five
measured rounds over the preceding 415-scenario corpus:

```text
renders=2,075
total=674ms
p95=1ms
retainedHeap=63,968 bytes
```

Enforced budgets are 45 seconds total, 500ms P95, and 64MiB retained heap after
forced GC.

## Runtime Load Matrix

The shared load screen renders exactly one production-corpus case at a time
and advances only after an explicit render result. The current physical-device
evidence is split into three independent tracks:

- Kotlin `2.3.20` Android: 8/8 devices across Android 9-16;
- Kotlin `1.7.21` Android: the same 8/8 device matrix; and
- iOS: 6/6 devices across iOS 14.3-26.0.

Together these runs completed 9,702 corpus renders with no crash, Android ANR,
or process exit. The separate visual gate reviewed 198 sentinel screenshots
plus 22 final completion screenshots.

**[Open the Android physical-device compatibility report and visual evidence](android-real-device-compatibility.md).**

**[Open the iOS physical-device compatibility report and visual evidence](ios-real-device-compatibility.md).**

The latest Web runtime baseline covers the preceding 397 scenarios and walks
from the first Flowchart to the final Agentflow case. iOS Simulator and Desktop
retain the preceding 236-scenario run through the final Sankey case; those rows
are historical baselines, not evidence for the subsequently implemented
families.

The automated Web scroll budget scales with the corpus at 80ms per case, with
a 15-second minimum. First content remains capped at 15 seconds and retained
JavaScript heap at 96MiB.

| Platform | Corpus | Result | Local evidence |
| --- | ---: | --- | --- |
| Android physical devices, Kotlin 2.3.20 | 441 x 8 | Passed post-fix matrix | 53-78s per device; 8/8 completed; 0 crashes or ANRs; 72 sentinels reviewed |
| Android physical devices, Kotlin 1.7.21 | 441 x 8 | Passed post-fix matrix | 52-77s per device; 8/8 completed; 0 crashes or ANRs; 72 sentinels reviewed |
| iOS physical devices | 441 x 6 | Passed physical-device matrix | 57-123s per device; 6/6 completed; 0 new crash reports; 54 sentinels reviewed |
| Android Emulator | 236 | Prior baseline passed | Retained historical baseline through Sankey |
| iOS Simulator | 236 | Prior baseline passed | Completion marker at 23s; 358MiB peak and final host RSS; no crash |
| Desktop | 236 | Prior baseline passed | Completion marker in three consecutive runs; latest 21s and 432MiB RSS |
| Web | 397 | Passed in 22,455ms after 1,227ms first content | 185 scroll events; 18,417,340 bytes retained JS heap; final Agentflow case reached; no browser errors |

Machine-readable measurements:
[Android](assets/runtime-load/android-emulator-metrics.json),
[iOS](assets/runtime-load/ios-simulator-metrics.json),
[Desktop](assets/runtime-load/desktop-metrics.json), and
[Web](assets/runtime-load/web-metrics.json).

The Android Emulator and iOS Simulator screenshots show the final Sankey case
from the 236-scenario baseline. The Web screenshot shows the final Agentflow
case from the preceding 397-scenario corpus:

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

The Android matrix identified and fixed an Android ICU incompatibility in an
Agentflow metadata-whitespace regular expression. The complete matrix passed
after escaping the literal closing brace and adding focused parser regression
tests.

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
npm run generate:invalid-source-corpus

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

AUDIT_SOURCE=invalid-source \
INVALID_SOURCE_SCOPE=full \
OUTPUT_DIR=captures/local/invalid-source \
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

INPUT_DIR=captures/local/invalid-source \
OUTPUT_DIR=docs/assets/invalid-source-report \
INVALID_SOURCE_SCOPE=full \
CONTACT_SHEET_PAGE_SIZE=16 \
npm run generate:invalid-source-contact-sheets

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
For `AUDIT_SOURCE=invalid-source`, Mermaid.js is captured first as the outcome
oracle. Capture fails if Native disagrees, if a Native rejection is not
`CONTENT_ERROR`, or if either rejection has an empty message. The contact-sheet
generator enforces outcome parity and records exact diagnostic-message equality
as a separate metric.
Set `AUDIT_KIND` and `CORPUS_KIND` to one of the 33 diagram IDs to reproduce
a single 256-case source-mutation partition instead of the complete matrix. The
[`Full Invalid Source Matrix`](../.github/workflows/full-invalid-source.yml)
workflow runs those 33 partitions in parallel. The regular Quality Gate uses
`INVALID_SOURCE_SCOPE=smoke`, which keeps the first variant of all 16 mutation
classes for every family without serially recapturing all 16,896 screenshots
on every push.

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

These criteria are satisfied for the supported Mermaid `12.0.0` contract. All
33 official families are implemented and have completed the semantic,
paint-order, perceptual, geometry, and manual contact-sheet gates. The public
code status is therefore **Stable**. The Android Kotlin 2.3.20, Android Kotlin
1.7.21, and iOS 441-case physical-device loads are fresh; the core soak and
other runtime performance values remain labelled with their measured corpus
sizes.
