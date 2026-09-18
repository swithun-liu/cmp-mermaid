# Production Capability Matrix

> [!WARNING]
> This matrix covers the 21 currently implemented
> families. It is not the Mermaid 12.0.0 full-family matrix and does not confer
> Stable status. See
> [`full-diagram-roadmap.md`](full-diagram-roadmap.md).

This matrix defines the major Mermaid `12.0.0` capabilities exercised by the
production conformance corpus. It is generated conceptually from
[`requiredFeaturesByKind`](../tools/official-reference/production-corpus.mjs)
and enforced by
[`generate-stability-corpus.mjs`](../tools/official-reference/generate-stability-corpus.mjs).

## Current State

- 21 supported diagram types
- 275 production scenarios
- 168 conformance scenarios created independently from the demo gallery
- 16 required capability points for the original chart families, 17 for
  Quadrant Chart, Kanban, Requirement, and Packet, 24 for Git Graph, and 22
  for Mindmap, 25 for Radar, 27 for Sankey, 39 each for Treemap and Venn,
  and 21 for Ishikawa
- 424/424 declared capability points covered
- 5,376 additional visual-matrix sources: 256 per diagram type
- 5,376 separate Native-only randomized stress inputs

## Coverage

| Diagram | Cases | Covered capability points |
| --- | ---: | --- |
| Flowchart | 14 | directions, classic shapes, advanced shapes, subgraphs, nested subgraphs, subgraph direction, solid edges, dotted/thick edges, edge labels, circle/cross markers, bidirectional edges, minimum length, classes/styles, Markdown/HTML, frontmatter config, Unicode |
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

## Enforcement

The corpus generator fails when:

- a diagram type does not have its expected number of cases;
- one of the 424 required capability points has no conformance case;
- a case reuses a demo or prior RC source;
- a case has no semantic text expectation;
- a case declares an unknown capability point.

`ProductionCorpusTest` then requires every source to render with finite,
bounded geometry and expected semantic text, compares two complete SceneGraphs
for determinism, and renders all 21 diagram types across all 11 built-in
themes.
The legacy Web audit captures Native and Mermaid.js output for every case and
enforces blank-image and content-geometry limits. The replacement detail audit
also exports renderer manifests and checks semantic text, normalized element
geometry, style categories, markers, clipping, overlap, paint-order occlusion,
foreground masks, edges, colors, and review heatmaps.

The large-scale visual matrix adds 256 unique sources per type by combining 13
or 14 complex production structures with 20 visible text and layout-pressure
profiles. All 5,376 sources render in the Native core test and all 5,376
Native/Official pairs pass the geometry gate. The original 12-family report
used a legacy coarse gate that did not catch a visible Git Graph paint-order
defect and is not a detail-parity pass by itself. All 21 implemented families
contribute 5,376 Native/Official pairs accepted by the replacement detail
gate. Fourteen families contribute `3,584 pass / 0 review / 0 fail`; ER contributes
`196 pass / 60 manually reviewed / 0 fail`; Journey contributes
`237 pass / 19 manually reviewed / 0 fail`; Requirement contributes
`252 pass / 4 manually reviewed / 0 fail`; Git Graph contributes
`236 pass / 20 manually reviewed / 0 fail`; Mindmap contributes
`149 pass / 107 manually reviewed / 0 fail`; Treemap contributes
`19 pass / 237 manually reviewed / 0 fail`; Venn contributes
`216 automatic pass / 40 manually accepted / 0 unresolved`. Journey reviews are benign
platform-font line-segmentation differences for one complete long actor label.
Requirement reviews are benign greedy cross-matches between duplicate
relationship labels. Git Graph reviews are text-overlap threshold findings
caused by browser/Compose text-bound differences; expected text, clipping,
paint order, and raster checks pass. Mindmap reviews are CoSE-Bilkent branch
rotation or mirror differences under platform text-size perturbations; all
expected text, nodes, hierarchy edges, shapes, and colors are preserved. This
systematic matrix broadens layout and text-pressure coverage. Treemap reviews
are Canvas/SVG text-position and same-row label/value overlap-threshold
differences; all expected hierarchy, rectangles, styles, values, clipping, and
paint order were manually verified. Venn reviews are two repeated
text-overlap font-box patterns. All 40 were manually verified. The matrix is not
counted as 5,376 independent topologies. The separate randomized stress corpus
remains Native-only robustness evidence and is not presented as Official
parity.

This matrix does not claim exhaustive support for every legal Mermaid program.
Unsupported legal features must return `MermaidError.UnsupportedFeature`
instead of rendering a misleading approximation.
