# Production Capability Matrix

This matrix defines the major Mermaid `12.0.0` capabilities exercised by the
production conformance corpus. It is generated conceptually from
[`requiredFeaturesByKind`](../tools/official-reference/production-corpus.mjs)
and enforced by
[`generate-stability-corpus.mjs`](../tools/official-reference/generate-stability-corpus.mjs).

## Current State

- 8 supported diagram types
- 106 production scenarios
- 64 conformance scenarios created independently from the demo gallery
- 16 required capability points per diagram type
- 128/128 declared capability points covered
- 2,048 additional visual-matrix sources: 256 per diagram type
- 2,048 separate Native-only randomized stress inputs

## Coverage

| Diagram | Cases | Covered capability points |
| --- | ---: | --- |
| Flowchart | 14 | directions, classic shapes, advanced shapes, subgraphs, nested subgraphs, subgraph direction, solid edges, dotted/thick edges, edge labels, circle/cross markers, bidirectional edges, minimum length, classes/styles, Markdown/HTML, frontmatter config, Unicode |
| XY Chart | 13 | vertical, horizontal, categorical axis, numeric axis, explicit domain, automatic domain, bars, lines, mixed plots, legend, data labels, outside labels, point labels, axis rotation, theme palette, component visibility |
| Sequence | 14 | participants, actor types, aliases, autonumber, activations, arrow families, notes, loops, alt/opt, parallel, critical, break, rect, boxes, create/destroy, self messages |
| Class | 13 | members, visibility, generics, annotations, relation markers, two-way relations, relation labels, cardinalities, lollipop, namespaces, nested namespaces, direction, notes, classes/styles, Markdown, metadata |
| State | 13 | simple states, descriptions, aliases, start/end, composites, nested composites, choice, fork/join, concurrency, direction, notes, classes/styles, links, Markdown, metadata, Unicode |
| Entity Relationship | 13 | entities, aliases, Unicode, attributes, optional types, keys, comments, cardinalities, identifying, non-identifying, direction, subgraphs, nested subgraphs, classes/styles, layout config, metadata |
| Gantt | 13 | date formats, duration units, dependencies, task states, milestones, excludes, weekends, axis format, tick interval, sections, compact mode, top axis, vertical markers, links, frontmatter config, Unicode |
| Pie | 13 | basic slices, show data, title, escaped labels, duplicate labels, zero values, decimal values, donut, legend right, legend center, legend transforms, static highlight, theme colors, many slices, metadata, Unicode |

## Enforcement

The corpus generator fails when:

- a diagram type does not have its expected number of cases;
- one of the 128 required capability points has no conformance case;
- a case reuses a demo or prior RC source;
- a case has no semantic text expectation;
- a case declares an unknown capability point.

`ProductionCorpusTest` then requires every source to render with finite,
bounded geometry and expected semantic text, compares two complete SceneGraphs
for determinism, and renders all 8 diagram types across all 11 built-in themes.
The Web audit captures Native and Mermaid.js output for every case and enforces
blank-image and content-geometry limits.

The large-scale visual matrix adds 256 unique sources per type by combining 13
or 14 complex production structures with 20 visible text and layout-pressure
profiles. All 2,048 sources render in the Native core test and all 2,048
Native/Official screenshot pairs pass the geometry gate. This systematic
matrix broadens layout and text-pressure coverage, but it is not counted as
2,048 independent topologies. The separate randomized stress corpus remains
Native-only robustness evidence and is not presented as Official parity.

This matrix does not claim exhaustive support for every legal Mermaid program.
Unsupported legal features must return `MermaidError.UnsupportedFeature`
instead of rendering a misleading approximation.
