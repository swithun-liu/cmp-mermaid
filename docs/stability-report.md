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
441 hand-authored production scenarios, 8,448 independent-scenario visual
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
| Independent-scenario visual matrix | 8,448 Native/Official pairs: 256 different business scenarios per family |
| Duplicate hard gate | All 33 families have 256/256 unique blueprints, normalized semantic structures, label-normalized sources, text/ID-stripped Native manifests, Native PNGs, and Official PNGs |
| Text/layout stress inputs | 8,448 byte-distinct inputs derived from the 441 production scenarios; no additional structural coverage |
| Source-mutation parity | 8,448/8,448 Official accept/reject outcomes matched: 2,156 accepted and 6,292 rejected; all 6,292 Native rejections were `CONTENT_ERROR`; 16,896 screenshots; 0 crashes or timeouts |
| Native core render results | 441 production scenarios and all 8,448 independent visual scenarios render successfully; the text/layout stress inputs are tracked separately |
| Web Native/Official captures | 16,896 independent-scenario screenshots plus 882 independent-corpus and 16,896 malformed-source screenshots |
| Visual evidence | 528 freshly generated independent-scenario sheets plus 528 source-mutation sheets across all 33 families |
| Independent-scenario detail audit | 8,448/8,448 processed: 8,218 pass, 230 review, 0 fail |
| Automated visual geometry | 8,448/8,448 processed: 0 failure, 0 review |
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
theme, independent-scenario duplicate/detail/geometry, and contact-sheet
generation gates. The supported Mermaid `12.0.0` contract is rated **Stable**.

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

## Completed Independent-Scenario Visual Gate

All 33 families have completed the independent-scenario visual gate. Each has
256 different named business scenarios whose Mermaid structures remain unique
after business labels, titles, IDs, and ordinals are removed. Each family also
has a 256-case geometry result and 16 paged contact sheets. The fresh total is:
`8,218 pass / 230 review / 0 fail`.

The mandatory
[duplicate audit](assets/stability-report/visual-scenario-duplicate-audit.json)
requires `256/256` unique blueprint IDs, normalized semantic structures,
label-normalized Mermaid sources, text-and-ID-stripped Native manifest
fingerprints, Native PNGs, and Official PNGs for every family. Any duplicate
in any dimension fails the gate.

Review findings are retained as explicit representation differences:

- text overlap: 185 cases;
- text position: 45 cases.

These categories may co-occur within one case. No expected-text loss,
structural-manifest error, clipping error, or geometry hard failure was
recorded. All 8,448 geometry cases remain inside the unchanged
source-controlled thresholds.

| Family | Pass | Review | Width ratio | Height ratio | Ink ratio |
| --- | ---: | ---: | ---: | ---: | ---: |
| Flowchart | 256 | 0 | `1.024-1.027` | `1.000-1.131` | `0.861-1.059` |
| Swimlanes | 255 | 1 | `0.876-1.063` | `1.040-1.046` | `0.982-1.088` |
| Architecture | 256 | 0 | `1.025-1.031` | `0.970-1.032` | `0.989-1.104` |
| C4 | 256 | 0 | `1.027-1.039` | `1.042-1.049` | `1.071-1.100` |
| Railroad | 256 | 0 | `1.028-1.037` | `0.985-1.063` | `1.023-1.093` |
| TreeView | 256 | 0 | `1.000-1.078` | `1.015-1.020` | `0.942-1.041` |
| XY Chart | 256 | 0 | `1.013-1.013` | `1.010-1.012` | `0.983-1.167` |
| Quadrant | 256 | 0 | `1.014-1.014` | `1.016-1.016` | `1.025-1.029` |
| Timeline | 256 | 0 | `1.023-1.059` | `1.008-1.090` | `1.017-1.113` |
| Kanban | 256 | 0 | `0.920-1.094` | `1.008-1.174` | `0.856-1.119` |
| Sequence | 256 | 0 | `0.908-1.086` | `1.120-1.120` | `1.039-1.093` |
| Class | 256 | 0 | `1.025-1.028` | `0.910-0.936` | `0.906-0.958` |
| State | 256 | 0 | `1.039-1.075` | `1.034-1.076` | `1.190-1.330` |
| Entity Relationship | 222 | 34 | `0.987-1.012` | `1.029-1.029` | `0.678-0.722` |
| Gantt | 246 | 10 | `1.035-1.035` | `0.943-0.943` | `0.913-1.015` |
| Pie | 256 | 0 | `1.005-1.031` | `0.994-1.019` | `0.997-1.045` |
| User Journey | 256 | 0 | `1.023-1.026` | `1.033-1.045` | `1.017-1.063` |
| Requirement | 256 | 0 | `1.022-1.022` | `0.987-1.013` | `0.955-1.024` |
| Git Graph | 234 | 22 | `1.061-1.062` | `1.019-1.051` | `1.076-1.213` |
| Mindmap | 256 | 0 | `1.024-1.030` | `0.971-1.047` | `0.943-1.040` |
| Packet | 256 | 0 | `1.010-1.017` | `1.023-1.037` | `1.003-1.037` |
| Radar | 256 | 0 | `1.011-1.037` | `1.018-1.020` | `1.036-1.037` |
| Sankey | 256 | 0 | `1.053-1.053` | `1.053-1.053` | `1.107-1.112` |
| Treemap | 256 | 0 | `1.037-1.037` | `1.034-1.034` | `1.075-1.076` |
| Venn | 93 | 163 | `0.951-1.023` | `1.000-1.009` | `0.982-1.024` |
| Ishikawa | 256 | 0 | `1.032-1.118` | `1.005-1.059` | `1.010-1.131` |
| Cynefin | 256 | 0 | `1.015-1.022` | `1.015-1.015` | `1.018-1.039` |
| Block | 256 | 0 | `1.037-1.057` | `0.978-1.072` | `0.995-1.099` |
| Event Modeling | 256 | 0 | `1.027-1.035` | `1.017-1.052` | `0.994-1.117` |
| Agentflow | 256 | 0 | `1.024-1.028` | `0.903-1.137` | `0.853-1.041` |
| Use Case | 256 | 0 | `0.984-1.148` | `1.035-1.044` | `1.023-1.191` |
| Wardley Map | 256 | 0 | `1.008-1.008` | `1.019-1.020` | `1.046-1.064` |
| ZenUML | 256 | 0 | `1.065-1.092` | `1.076-1.078` | `1.084-1.142` |
All 528 independent-scenario contact sheets were regenerated from the same 8,448
source cases recorded in the manifest. The 230 review cases are distributed
as follows: Venn 163, Entity Relationship 34, Git Graph 22, Gantt 10, and
Swimlanes 1. The other 28 families pass all 256 detail cases automatically.
Per-family compatibility
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
2. **8,448 independent-scenario visual cases.** Each diagram type contributes
   256 explicitly cataloged, named business scenarios. Each scenario has a
   unique blueprint and eight rendered semantic decisions; all 256 Mermaid
   structures remain unique after business labels, titles, IDs, and ordinals
   are removed. The published Native/Official evidence covers every case and
   rejects duplicate normalized manifests or screenshots.
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

| Diagram | Production cases | Capability points | Scenario examples | Current visual result |
| --- | ---: | ---: | --- | --- |
| Flowchart | 14 | 16/16 | orchestration, edge semantics, advanced shapes, nested domains | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| XY Chart | 13 | 16/16 | latency, categorical and numeric axes, horizontal labels, mixed plots | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Quadrant Chart | 13 | 17/17 | axes, quadrant labels, boundary points, styles, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Timeline | 13 | 16/16 | directions, sections, periods, events, color scales, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Sequence | 14 | 16/16 | checkout saga, lifecycle, self messages, parallel and critical regions | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Class | 13 | 16/16 | commerce, namespaces, generics, relations, annotations | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| State | 13 | 16/16 | fulfillment, nested composites, fork/join, concurrency, notes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Entity Relationship | 13 | 16/16 | commerce, aliases, attributes, cardinalities, nested subgraphs | Production evidence retained; independent-scenario matrix 222 pass / 34 review / 0 fail; 16/16 sheets regenerated |
| Gantt | 13 | 16/16 | release plans, date units, exclusions, top axes, vertical markers | Production evidence retained; independent-scenario matrix 246 pass / 10 review / 0 fail; 16/16 sheets regenerated |
| Pie | 13 | 16/16 | cost, escaped labels, donut, legends, themes, many slices | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| User Journey | 13 | 16/16 | sections, scores, actor order, metadata, configuration, long text | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Requirement | 13 | 17/17 | typed requirements, elements, relationships, directions, styling, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Git Graph | 13 | 24/24 | commits, branches, merges, cherry-picks, orientations, configuration, themes | Production evidence retained; independent-scenario matrix 234 pass / 22 review / 0 fail; 16/16 sheets regenerated |
| Mindmap | 13 | 22/22 | hierarchy, shapes, text, CoSE-Bilkent, Dagre, tidy tree, configuration, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Packet | 13 | 17/17 | explicit and counted fields, packet-beta, row splitting, metadata, configuration, responsive sizing | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Radar | 13 | 25/25 | axes, curve entry forms, value ranges, graticules, legends, metadata, configuration, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Sankey | 13 | 27/27 | CSV records, D3 Sankey layout, all alignments, value labels, link and node colors, configuration | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Treemap | 13 | 39/39 | hierarchy, multiple roots, D3 squarify layout, classes, values and formats, metadata, dimensions, fonts, responsive sizing | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Venn | 13 | 38/38 | weighted sets and intersections, Venn.js/fmin optimization, pairwise completion, text nodes, styles, dimensions, debug layout, themes | Production evidence retained; independent-scenario matrix 93 pass / 163 review / 0 fail; 16/16 sheets regenerated |
| Ishikawa | 13 | 21/21 | headers, effect and root-only forms, indentation hierarchy, alternating branches, recursive descendants, fish-head geometry, wrapping, entities, configuration, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Cynefin | 13 | 30/30 | headers, five domains, fixed layout, items, transitions, confusion overflow, seeded boundaries, dimensions, themes, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Event Modeling | 13 | 22/22 | frame forms, entity aliases, swimlanes, inferred/explicit relations, inline data, reset behavior, configuration, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Agentflow | 13 | 32/32 | typed nodes and edge semantics, nested/global/collapsed flows, connectors, metadata, diagnostics, configuration, themes, Dagre layout | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Block | 13 | 31/31 | grids, spans, spaces, nested composites, documented shapes, block arrows, links, markers, classes, inline styles, configuration, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Swimlanes | 13 | 15/15 | directions, lane ownership and defaults, nested subgraphs, flowchart shapes, cross-lane edges, orthogonal routes, cycles, styles, configuration, themes | Production evidence retained; independent-scenario matrix 255 pass / 1 review / 0 fail; 16/16 sheets regenerated |
| Architecture | 13 | 18/18 | services, icons, nested groups, junctions, directional ports, arrows, labels, boundary modifiers, row/column alignment, configuration, deterministic seed, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| C4 | 13 | 26/26 | five diagram levels, people, systems, containers, components, external/database/queue variants, nested boundaries, deployment nodes, relation variants, dynamic indexes, styles, configuration, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Railroad | 18 | 24/24 | Railroad IR, EBNF, ABNF, PEG, terminals, non-terminals, sequences, choices, optional and repeated expressions, notation-specific constructs, configuration, themes, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| TreeView | 13 | 22/22 | indentation and box-drawing hierarchy, files and directories, annotations, descriptions, icons, configuration, themes, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Use Case | 18 | 38/38 | actors and actor variants, use cases, boundaries, associations and dependencies, markers, notes, JSON tables, classes/styles, configuration, themes, metadata | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Wardley Map | 13 | 28/28 | components, sourcing, dependencies, evolution, pipelines, notes, annotations, forces, themes, metadata; caller configuration is tested separately | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| ZenUML | 13 | 26/26 | participants, aliases, annotators, messages, assignments, replies, groups, control fragments, comments, dividers, Unicode | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |
| Kanban | 13 | 17/17 | sections, tasks, metadata, priorities, ticket links, themes | Production evidence retained; independent-scenario matrix 256 pass / 0 review / 0 fail; 16/16 sheets regenerated |

## Independent-Scenario Visual Matrix

The generated visual matrix and published evidence contain 256 explicitly
cataloged business scenarios for each of all 33 families. Within every family,
the 256 Mermaid sources remain different after business labels, titles, IDs,
and ordinals are normalized away. The families are:
Flowchart,
XY Chart, Quadrant Chart, Timeline, Sequence, Class, State, Entity
Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, Mindmap,
Packet, Radar, Sankey, Treemap, Venn, Ishikawa, Cynefin, Event Modeling,
Agentflow, Block, Swimlanes, Architecture, C4, Railroad, TreeView, Use Case,
Kanban, Wardley Map, and ZenUML:

- 8,448 independent scenario blueprints;
- 8,448 unique normalized semantic structures and label-normalized sources;
- 8,448 unique text-and-ID-stripped Native manifest fingerprints;
- 8,448 CMP Native screenshots;
- 8,448 Mermaid.js `12.0.0` screenshots;
- 528 paged contact sheets, with 16 same-source pairs per page;
- source, structural dimensions, feature, screenshot, and SHA-256 metadata;
- per-pair content bounds and foreground-density metrics.

**[Open all 528 paged Native/Official comparison images](assets/stability-report/visual-parity-evidence.md).**

Machine-readable evidence:
[independent scenario accounting](assets/stability-report/visual-structure-corpus-metadata.json),
[duplicate audit](assets/stability-report/visual-scenario-duplicate-audit.json),
[manifest](assets/stability-report/visual-parity-manifest.json),
[detail report](assets/stability-report/visual-parity-detail.json), and
[geometry report](assets/stability-report/visual-parity-geometry.json).
The geometry gate recorded 0 failures and 0 review findings across all 8,448
pairs. Overall Native/Official width ratios were `0.876-1.148`, height ratios
were `0.903-1.174`, and foreground-ink ratios were `0.678-1.330`; every value
remains inside the unchanged source-controlled thresholds.

## Independent Production Visual Evidence

Each contact sheet uses the same Mermaid source on both sides:

- left: CMP Native, rendered through Kotlin and Compose Canvas;
- right: Mermaid.js `12.0.0`, loaded from the repository's pinned local asset.

The source IDs and scenario descriptions are printed above every pair.

<details open>
<summary><strong>Flowchart: 14 production scenarios</strong></summary>

![Flowchart complex Native and Official corpus](assets/stability-report/flowchart-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.024-1.027`, height `1.000-1.131`, and foreground ink `0.861-1.059`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Cynefin: 13 production scenarios</strong></summary>

![Cynefin complex Native and Official corpus](assets/stability-report/cynefin-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs. Width, height,
and foreground-ink ratios were `1.013-1.021`, `1.011-1.019`, and
`1.019-1.041`; detail was `13 pass / 0 review / 0 fail`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.015-1.022`, height `1.015-1.015`, and foreground ink `1.018-1.039`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Event Modeling: 13 production scenarios</strong></summary>

![Event Modeling complex Native and Official corpus](assets/stability-report/eventmodeling-complex-corpus.png)

The production geometry audit passed all 13 pairs. Width, height, and
foreground-ink ratios were `1.029-1.044`, `1.013-1.044`, and
`1.029-1.115`. Production detail passed `13 / 13`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.027-1.035`, height `1.017-1.052`, and foreground ink `0.994-1.117`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.024-1.028`, height `0.903-1.137`, and foreground ink `0.853-1.041`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Block: 13 production scenarios</strong></summary>

![Block complex Native and Official corpus](assets/stability-report/block-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`0.970-1.116`, `0.880-1.069`, and `0.908-1.388`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.037-1.057`, height `0.978-1.072`, and foreground ink `0.995-1.099`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Swimlanes: 13 production scenarios</strong></summary>

![Swimlanes complex Native and Official corpus](assets/stability-report/swimlanes-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.007-1.191`, `0.912-1.070`, and `0.928-1.603`. Production
detail was `10 pass / 3 review / 0 fail`.

The fresh 256-case independent-scenario Native/Official audit reports `255 pass / 1 review / 0 fail`; geometry has 0 hard failures with width `0.876-1.063`, height `1.040-1.046`, and foreground ink `0.982-1.088`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Architecture: 13 production scenarios</strong></summary>

![Architecture complex Native and Official corpus](assets/stability-report/architecture-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.020-1.232`, `0.996-1.236`, and `0.985-1.561`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.025-1.031`, height `0.970-1.032`, and foreground ink `0.989-1.104`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>C4: 13 production scenarios</strong></summary>

![C4 complex Native and Official corpus](assets/stability-report/c4-complex-corpus.png)

The production geometry passed all 13 pairs. Width, height, and foreground-ink
ratios were `1.028-1.118`, `0.884-1.048`, and `1.004-1.203`. Production
detail passed `13 / 13`. Geometry counts compare both render objects and
expanded primitives, so Official's decomposed database/queue paths and
Native's semantic shapes are validated without a representation-only review.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.027-1.039`, height `1.042-1.049`, and foreground ink `1.071-1.100`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Railroad: 18 production scenarios</strong></summary>

![Railroad complex Native and Official corpus](assets/stability-report/railroad-complex-corpus.png)

The production geometry and detail audits passed all 18 pairs with
`18 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.038-1.120`, `1.028-1.131`, and `1.078-1.281`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.028-1.037`, height `0.985-1.063`, and foreground ink `1.023-1.093`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>TreeView: 13 production scenarios</strong></summary>

![TreeView complex Native and Official corpus](assets/stability-report/treeview-complex-corpus.png)

The production geometry and detail audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Width, height, and foreground-ink ratios were
`1.011-1.169`, `0.975-1.062`, and `0.930-1.050`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.000-1.078`, height `1.015-1.020`, and foreground ink `0.942-1.041`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Use Case: 18 production scenarios</strong></summary>

![Use Case complex Native and Official corpus](assets/stability-report/usecase-complex-corpus.png)

The production geometry and detail audits passed all 18 pairs. Width, height,
and foreground-ink ratios were `1.036-1.160`, `0.971-1.115`, and
`1.043-1.316`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `0.984-1.148`, height `1.035-1.044`, and foreground ink `1.023-1.191`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>XY Chart: 13 production scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.013-1.013`, height `1.010-1.012`, and foreground ink `0.983-1.167`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Quadrant Chart: 13 production scenarios</strong></summary>

![Quadrant Chart complex Native and Official corpus](assets/stability-report/quadrant-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.014-1.014`, height `1.016-1.016`, and foreground ink `1.025-1.029`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Timeline: 13 production scenarios</strong></summary>

![Timeline complex Native and Official corpus](assets/stability-report/timeline-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.023-1.059`, height `1.008-1.090`, and foreground ink `1.017-1.113`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Sequence: 14 production scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `0.908-1.086`, height `1.120-1.120`, and foreground ink `1.039-1.093`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Class: 13 production scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.025-1.028`, height `0.910-0.936`, and foreground ink `0.906-0.958`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details>
<summary><strong>State: 13 production scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.039-1.075`, height `1.034-1.076`, and foreground ink `1.190-1.330`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Entity Relationship: 13 production scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `222 pass / 34 review / 0 fail`; geometry has 0 hard failures with width `0.987-1.012`, height `1.029-1.029`, and foreground ink `0.678-0.722`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Gantt: 13 production scenarios</strong></summary>

![Gantt complex Native and Official corpus](assets/stability-report/gantt-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `246 pass / 10 review / 0 fail`; geometry has 0 hard failures with width `1.035-1.035`, height `0.943-0.943`, and foreground ink `0.913-1.015`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details>
<summary><strong>Pie: 13 production scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.005-1.031`, height `0.994-1.019`, and foreground ink `0.997-1.045`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>User Journey: 13 production scenarios</strong></summary>

![User Journey complex Native and Official corpus](assets/stability-report/journey-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.023-1.026`, height `1.033-1.045`, and foreground ink `1.017-1.063`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Requirement: 13 production scenarios</strong></summary>

![Requirement complex Native and Official corpus](assets/stability-report/requirement-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.022-1.022`, height `0.987-1.013`, and foreground ink `0.955-1.024`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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

The fresh 256-case independent-scenario Native/Official audit reports `234 pass / 22 review / 0 fail`; geometry has 0 hard failures with width `1.061-1.062`, height `1.019-1.051`, and foreground ink `1.076-1.213`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Mindmap: 13 production scenarios</strong></summary>

![Mindmap complex Native and Official corpus](assets/stability-report/mindmap-complex-corpus.png)

The refreshed production audit accepted all 13 pairs:
`9 pass / 4 manually reviewed / 0 fail`. Production width, height, and
foreground-ink ratios are `0.949-1.122`, `0.863-1.049`, and `0.742-1.049`.
The refreshed production contact sheet was manually reviewed with no missing
content, clipping, or overlap.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.024-1.030`, height `0.971-1.047`, and foreground ink `0.943-1.040`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Kanban: 13 production scenarios</strong></summary>

![Kanban complex Native and Official corpus](assets/stability-report/kanban-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `0.920-1.094`, height `1.008-1.174`, and foreground ink `0.856-1.119`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Packet: 13 production scenarios</strong></summary>

![Packet complex Native and Official corpus](assets/stability-report/packet-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.010-1.017`, height `1.023-1.037`, and foreground ink `1.003-1.037`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Radar: 13 production scenarios</strong></summary>

![Radar complex Native and Official corpus](assets/stability-report/radar-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.011-1.037`, height `1.018-1.020`, and foreground ink `1.036-1.037`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Sankey: 13 production scenarios</strong></summary>

![Sankey complex Native and Official corpus](assets/stability-report/sankey-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.053-1.053`, height `1.053-1.053`, and foreground ink `1.107-1.112`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Treemap: 13 production scenarios</strong></summary>

![Treemap complex Native and Official corpus](assets/stability-report/treemap-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.037-1.037`, height `1.034-1.034`, and foreground ink `1.075-1.076`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Venn: 13 production scenarios</strong></summary>

![Venn complex Native and Official corpus](assets/stability-report/venn-complex-corpus.png)

The fresh 256-case independent-scenario Native/Official audit reports `93 pass / 163 review / 0 fail`; geometry has 0 hard failures with width `0.951-1.023`, height `1.000-1.009`, and foreground ink `0.982-1.024`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.032-1.118`, height `1.005-1.059`, and foreground ink `1.010-1.131`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>Wardley Map: 13 production scenarios</strong></summary>

![Wardley Map complex Native and Official corpus](assets/stability-report/wardley-complex-corpus.png)

The replacement production detail and geometry audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.008-1.015`, `1.019-1.020`, and `0.970-1.106`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.008-1.008`, height `1.019-1.020`, and foreground ink `1.046-1.064`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

</details>

<details open>
<summary><strong>ZenUML: 13 production scenarios</strong></summary>

![ZenUML complex Native and Official corpus](assets/stability-report/zenuml-complex-corpus.png)

The replacement production detail and geometry audits passed all 13 pairs with
`13 pass / 0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.042-1.100`, `1.039-1.102`, and `1.094-1.238`.

The fresh 256-case independent-scenario Native/Official audit reports `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width `1.065-1.092`, height `1.076-1.078`, and foreground ink `1.084-1.142`. See the [aggregate detail](assets/stability-report/visual-parity-detail.json) and [aggregate geometry](assets/stability-report/visual-parity-geometry.json) reports.

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
runs the 8,448-pair independent-scenario visual matrix weekly and on demand in
33 parallel diagram jobs. Each job rejects duplicate normalized sources,
text-and-ID-stripped Native manifests, or Native/Official PNGs before the
geometry and detail gates run.

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
text/layout stress inputs and renders all 8,448 independent visual scenarios,
checking the 256-per-family accounting, unique scenario titles, normalized
semantic signatures, expected visible text, and finite non-empty SceneGraphs.
The retained historical deterministic stress
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

INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=docs/assets/stability-report/visual-scenario-duplicate-audit.json \
npm run audit:visual-scenario-duplicates

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=docs/assets/stability-report/visual-parity-geometry.json \
npm run audit:stability-geometry

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=docs/assets/stability-report/visual-parity-detail.json \
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
