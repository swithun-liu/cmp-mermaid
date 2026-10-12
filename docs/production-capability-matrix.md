# Production Capability Matrix

> [!NOTE]
> This matrix covers all 33 implemented families and feeds the completed Stable
> gate. It defines tested coverage, not support for every possible legal
> Mermaid program. See
> [`full-diagram-roadmap.md`](full-diagram-roadmap.md).

This matrix defines the major Mermaid `12.0.0` capabilities exercised by the
production conformance corpus. It is generated conceptually from
[`requiredFeaturesByKind`](../tools/official-reference/production-corpus.mjs)
and enforced by
[`generate-stability-corpus.mjs`](../tools/official-reference/generate-stability-corpus.mjs).

## Current State

- 33 supported diagram types
- 441 production scenarios
- 274 conformance scenarios created independently from the demo gallery
- 16 required capability points for Flowchart, XY Chart, Timeline, Sequence,
  Class, State, Entity Relationship, Gantt, Pie, and User Journey; 17 for
  Quadrant Chart, Kanban, Requirement, and Packet; 15 for Swimlanes; 18 for
  Architecture; 24 for Railroad; 22 for TreeView; 26 for C4 and ZenUML; 30 for
  Wardley Map; 38 for Use Case; and the per-family counts shown
  below for the remaining families
- 738/738 declared capability points covered
- 8,448 systematic visual-input cases: eight family-specific binary dimensions
  and all 256 combinations per diagram type
- 8,448 published Native/Official same-source pairs
- 8,448 separate text/layout stress inputs derived from the 441 production
  scenarios; no additional structural coverage is claimed for that corpus

## Coverage

| Diagram | Cases | Covered capability points |
| --- | ---: | --- |
| Flowchart | 14 | directions, classic shapes, advanced shapes, subgraphs, nested subgraphs, subgraph direction, solid edges, dotted/thick edges, edge labels, circle/cross markers, bidirectional edges, minimum length, classes/styles, Markdown/HTML, frontmatter config, Unicode |
| Swimlanes | 13 | swimlane-beta header, all directions, top-level and default lanes, nested subgraphs, flowchart node shapes, cross-lane edges, edge labels and patterns, cycles, classes/styles, frontmatter config, line hops, accessibility, and Unicode |
| Architecture | 13 | architecture-beta header, services, built-in icons, icon text, groups, nested groups, junctions, directional ports, arrowheads, bidirectional edges, edge labels, group-boundary modifiers, row/column alignment, frontmatter configuration, deterministic seed, accessibility, and Unicode |
| C4 | 13 | all five C4 headers, people, systems, containers, components, external elements, database and queue shapes, nested boundaries, deployment nodes, standard/directional/back/bidirectional relations, dynamic indexes, element and relation styles, layout/frontmatter configuration, accessibility, named attributes, comments, and Unicode |
| Railroad | 18 | Railroad IR, EBNF, ABNF, and PEG headers; terminals, non-terminals, sequences, choices, optionals, zero-or-more and one-or-more repetition, special nodes, ISO EBNF and exceptions, bounded ABNF repetition and numeric values, PEG predicates and any-character matching, comments, accessibility, frontmatter configuration, themes, Unicode, and entities |
| TreeView | 13 | treeView-beta header, indentation, standard and heavy box-drawing syntax, files, directories, quoted and bare labels, classes, highlights, descriptions, explicit/default/suppressed icons, filename and extension icon maps, frontmatter configuration, themes, accessibility, comments, Unicode, and entities |
| Use Case | 18 | usecase-beta header, directions, actor variants and icons, business elements, stereotypes, ellipse and rectangle use cases, system and package boundaries, association markers and labels, minimum length, include/extend/generalization, edge IDs/classes/styles/animation, notes, JSON tables, classes, inline styles, frontmatter title reset and configuration, accessibility, comments, entities, Unicode, Markdown, responsive/intrinsic sizing, themes, theme variables, and color rotation |
| Wardley Map | 13 | wardley-beta header, titles, size, anchors, components, coordinates, label offsets, inertia, build/buy/outsource/market sourcing, plain/dashed/directional/bidirectional dependencies, labels, evolution trends, custom stages and widths, pipelines, notes, annotations, accelerators/deaccelerators, accessibility, comments, entities, Unicode, themes, theme variables, and responsive sizing; caller configuration is covered separately because Mermaid.js 12.0.0 sanitizes `wardley-beta` source configuration |
| ZenUML | 13 | zenuml header, source/frontmatter titles, explicit and inferred participants, aliases, annotators, groups, asynchronous/synchronous/nested/creation/return messages, assignments, return annotators, comments, Markdown, dividers, if/else, loop, parallel, optional, critical, section, reference, try/catch/finally, and Unicode |
| XY Chart | 13 | vertical, horizontal, categorical axis, numeric axis, explicit domain, automatic domain, bars, lines, mixed plots, legend, data labels, outside labels, point labels, axis rotation, theme palette, component visibility |
| Quadrant Chart | 13 | title, axes, quadrant labels, points, boundary points, empty charts, point radius/fill/stroke, classes, direct-style precedence, frontmatter config, theme colors, metadata, Unicode, comments |
| Timeline | 13 | LR, TD, title, periods, events, continued events, sections, sectionless colors, disabled multicolor, HTML breaks, frontmatter config, theme colors, Redux themes, metadata, Unicode, comments |
| Kanban | 13 | sections, tasks, anonymous items, explicit IDs, node forms, deeper indentation, comments, wrapped labels, empty sections, tickets, assignees, priorities, ticket links, section width, theme colors, Markdown, Unicode |
| Sequence | 14 | participants, actor types, aliases, autonumber, activations, arrow families, notes, loops, alt/opt, parallel, critical, break, rect, boxes, create/destroy, self messages |
| Class | 13 | members, visibility, generics, annotations, relation markers, two-way relations, relation labels, cardinalities, lollipop, namespaces, nested namespaces, direction, notes, classes/styles, Markdown, metadata |
| State | 13 | simple states, descriptions, aliases, start/end, composites, nested composites, choice, fork/join, concurrency, direction, notes, classes/styles, links, Markdown, metadata, Unicode |
| Entity Relationship | 13 | entities, aliases, Unicode, attributes, optional types, keys, comments, cardinalities, identifying, non-identifying, direction, subgraphs, nested subgraphs, classes/styles, layout config, metadata |
| Gantt | 13 | date formats, duration units, dependencies, task states, milestones, excludes, weekends, axis format, tick interval, sections, compact mode, top axis, vertical markers, links, frontmatter config, Unicode |
| Pie | 13 | basic slices, show data, title, escaped labels, duplicate labels, zero values, decimal values, donut, legend right, legend center, legend transforms, static highlight, theme colors, many slices, metadata, Unicode |
| User Journey | 13 | sections, scores, decimal scores, single actor, multiple actors, actor order, actor reuse, actorless tasks, title, frontmatter title, accessibility, comments, configuration, theme colors, long task text, long actor text |
| Requirement | 13 | requirement types, fields, risk levels, verification methods, elements, relationship types, reverse relationships, directions, Dagre, frontmatter title, accessibility, Markdown, direct styles, classes, theme variables, Unicode, comments |
| Git Graph | 13 | commits, custom IDs, messages, tags, commit types, branches, quoted branches, checkout, switch, branch order, main branch config, merges, merge customization, cherry-pick, merge cherry-pick, orientations, parallel commits, visibility config, frontmatter config, title, accessibility, theme variables, Unicode, comments |
| Mindmap | 13 | hierarchy, irregular indentation, deep hierarchy, wide hierarchy, default/square/rounded/circle/cloud/bang/hexagon shapes, Markdown, HTML breaks, entities, comments, frontmatter title, CoSE-Bilkent, Dagre, tidy tree, sizing config, theme variables, Unicode |
| Packet | 13 | packet and packet-beta headers, explicit ranges, single-bit fields, bit-count fields, mixed addressing, row splitting, title, frontmatter title, accessibility, comments, escaped labels, configuration, show/hide bits, responsive sizing, Unicode |
| Radar | 13 | axis declarations and labels, positional and referenced entries, reference reordering, multiple curves, inferred and explicit ranges, circular and polygon graticules, legend visibility, ticks, title, metadata, comments, escapes, configuration, responsive sizing, theme variables, Unicode, option precedence, tick cap, curve tension |
| Sankey | 13 | sankey and sankey-beta headers, three-field CSV records, quoted commas, escaped quotes, blank lines, branching, merging, all four alignments, gradient/source/target/fixed link colors, value labels, prefix/suffix, node width/padding, outlined labels, custom node colors, frontmatter title, responsive sizing |
| Treemap | 13 | treemap and treemap-beta headers, hierarchy, multiple roots, irregular indentation, class styles, metadata, Unicode, value visibility and grouping/currency/fixed/percentage formats, padding, dimensions, borders, fonts, responsive and intrinsic sizing |
| Venn | 13 | venn-beta header, weighted sets and pairwise/multi-set unions, synthetic pairwise constraints, quoted identifiers, bracket labels, indented and explicit text nodes, set/intersection/text styles, dimensions, padding, debug layout, responsive and intrinsic sizing, themes, comments, and Unicode |
| Ishikawa | 13 | ishikawa and ishikawa-beta headers, effect and root-only diagrams, alternating top-level causes, recursive nested and leaf causes, first-cause indentation normalization, irregular indentation, comments, entities, HTML breaks, configuration, responsive and intrinsic sizing, themes, Unicode, and long wrapping |
| Cynefin | 13 | cynefin-beta and colon headers, five domains, fixed layout, quoted items, empty and duplicate domains, labelled and unlabelled transitions, self-loop filtering, confusion overflow, descriptions, seeded wavy and straight boundaries, dimensions, padding, responsive and intrinsic sizing, themes, metadata, entities, and Unicode |
| Event Modeling | 13 | eventmodeling header, compact and relaxed syntax, time/reset frames, entity aliases, default and namespaced swimlanes, inferred/explicit/multiple relations, inline data, frontmatter title, comments, entities, Unicode, padding, row height, responsive/intrinsic sizing, themes, and theme variables |
| Agentflow | 13 | agentflow-beta header, all directions, six shape aliases and canonical shapes, sequence/reference/failure edges, chains and fan-out, nested/global/collapsed flows, connectors and connector references, single-line/multiline/custom metadata, frontmatter title, accessibility, comments, entities, Unicode, configuration, themes, classic/neo look, and responsive/intrinsic sizing |
| Block | 13 | block and block-beta headers, explicit and automatic columns, spans, spaces, nested composites, documented shapes, directional block arrows and aliases, links, labels, markers, line patterns, classes, inline styles, duplicate declarations, metadata, frontmatter configuration, themes, responsive sizing, and Unicode |

## Enforcement

The corpus generator fails when:

- a diagram type does not have its expected number of cases;
- one of the 738 required capability points has no conformance case;
- a case reuses a demo or prior RC source;
- a case has no semantic text expectation;
- a case declares an unknown capability point.

`ProductionCorpusTest` then requires every source to render with finite,
bounded geometry and expected semantic text, compares two complete SceneGraphs
for determinism, and renders all 33 diagram types across all 11 built-in
themes.
The legacy Web audit captures Native and Mermaid.js output for every case and
enforces blank-image and content-geometry limits. The replacement detail audit
also exports renderer manifests and checks semantic text, normalized element
geometry, style categories, markers, clipping, overlap, paint-order occlusion,
foreground masks, edges, colors, and review heatmaps.

The large-scale input matrix defines eight named binary dimensions per family
and generates the complete `2^8 = 256` combinations. Every option appears 128
times, every option pair for any two dimensions appears 64 times, and each
family retains 256 distinct normalized semantic structures and
label-normalized Mermaid sources. Business labels, ordinals, image hashes, and
rendered manifest uniqueness do not establish input coverage. All 8,448 cases
render in the Native core test and have fresh Native/Official captures.

The replacement detail gate reports
`7,741 pass / 707 review / 0 fail`. Review findings are retained for text
position/overlap, raster mask/edge/color, equivalent element-count
representation, marker occlusion, and paint-order occlusion. No expected-text
loss, clipping error, or hard detail failure was recorded. The geometry gate
has 0 hard failures and retains 132 review findings across 108 Architecture
cases. Architecture uses seeded iterative fCoSE layout on both sides; final
cross-runtime coordinate equality is not claimed, and the differences remain
visible rather than being hidden by broader thresholds.

Machine-readable structural counts are published in
[`visual-structure-corpus-metadata.json`](assets/stability-report/visual-structure-corpus-metadata.json).
The independent text/layout stress corpus still derives 8,448 byte-distinct
inputs from the 441 production scenarios and is accounted for in
[`visual-stress-corpus-metadata.json`](assets/stability-report/visual-stress-corpus-metadata.json).
It does not add structural coverage. The prior randomized stress corpus remains
Native-only robustness evidence and is not presented as Official parity.

This matrix does not claim exhaustive support for every legal Mermaid program.
Unsupported legal features must return `MermaidError.UnsupportedFeature`
instead of rendering a misleading approximation.
