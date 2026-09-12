# Upstream Flowchart Source Map

## Frozen Baseline

- Mermaid: `12.0.0`
- `dagre-d3-es`: `7.0.14`
- Reference layout: `dagre`
- Reference curve: `rounded`
- Native renderer: Compose Multiplatform Canvas

The runtime does not embed JavaScript or a WebView. Layout and Flowchart
adapter behavior are Kotlin source ports. DOM/SVG painting is replaced by
`SceneGraph` generation and Compose drawing.

Mermaid `12.0.0` defaults its full bundle to ELK. The comparison generator
sets `layout: "dagre"` explicitly because this milestone ports Mermaid's Dagre
Flowchart pipeline. ELK is a separate future layout-engine port.

## Dagre And Graphlib

| Kotlin source | Upstream source | Port status |
| --- | --- | --- |
| `upstream/graphlib/Graph.kt` | `src/graphlib/graph.js` | Production API subset used by Dagre |
| `upstream/graphlib/Algorithms.kt` | `src/graphlib/alg/dfs.js`, `preorder.js`, `postorder.js`, `components.js` | Production API subset |
| `upstream/dagre/DagreLayout.kt` | `src/dagre/layout.js` | Ported layout pipeline |
| `upstream/dagre/DagreUtil.kt` | `src/dagre/util.js` | Ported production helpers |
| `upstream/dagre/Acyclic.kt` | `src/dagre/acyclic.js` | Ported |
| `upstream/dagre/GreedyFas.kt` | `src/dagre/greedy-fas.js`, `src/dagre/data/list.js` | Ported |
| `upstream/dagre/Rank.kt` | `src/dagre/rank/*.js` | Ported |
| `upstream/dagre/Normalize.kt` | `src/dagre/normalize.js` | Ported |
| `upstream/dagre/NestingGraph.kt` | `src/dagre/nesting-graph.js` | Ported |
| `upstream/dagre/Compound.kt` | `src/dagre/parent-dummy-chains.js`, `add-border-segments.js` | Ported |
| `upstream/dagre/Order.kt` | `src/dagre/order/*.js` | Ported |
| `upstream/dagre/Position.kt` | `src/dagre/position/index.js`, `bk.js` | Ported |
| `upstream/dagre/CoordinateSystem.kt` | `src/dagre/coordinate-system.js` | Ported |

`src/dagre/debug.js`, unused Graphlib algorithms, JSON serialization, and the
`src/dagre-js` DOM renderer are not part of the Native runtime. The Compose
adapter owns measurement and painting instead.

## Mermaid Flowchart Adapter

| Kotlin source | Upstream source | Port status |
| --- | --- | --- |
| `upstream/mermaid/MermaidGraphAdapter.kt` | `rendering-util/layout-algorithms/dagre/mermaid-graphlib.js` | Cluster discovery, endpoint anchors, extraction, and recursive graphs ported |
| `FlowDagreLayout.kt` | `rendering-util/layout-algorithms/dagre/index.js` | Graph preparation, self-loop splitting/merging, recursive measurement, result normalization ported |
| `LineBridgeRouter.kt` | `rendering-util/rendering-elements/lineJump.ts` | Segment crossing and hop assignment ported |
| `SceneShapeGeometry.kt` | Mermaid shape intersection functions | Native shape-outline adapter |
| `MermaidDiagram.kt` | Mermaid SVG rendering elements | Compose Canvas platform adapter |

## Flowchart Semantics

`FlowchartParser.kt` and `FlowchartModel.kt` currently implement the supported
Flowchart/FlowDB behavior in Kotlin, but they are not yet a complete
method-for-method port of Mermaid's parser and `flowDb.ts`. Unsupported
behavior remains listed in `flowchart-compatibility.md`; it must not be
described as translated until its upstream mapping and parity tests exist.

## Intentional Kotlin Adaptations

- JavaScript missing properties map to nullable Kotlin fields. They must not be
  replaced with numeric defaults because Dagre distinguishes `undefined` from
  `0`.
- JavaScript object insertion order maps to linked Kotlin collections.
- Expected failures cross public boundaries as `GMResult.Err`.
- Lodash operations map to Kotlin collection operations without changing
  traversal order.
- DOM measurement is supplied by `TextMetricProvider`.
- SVG paths, markers, and line jumps map to `SceneGraph` primitives.

## Parity Gate

`DagreParityTest` compares Kotlin coordinates against the frozen JavaScript
package for:

- horizontal chains;
- branch ordering;
- long labelled edges and minimum lengths;
- parallel edge lanes;
- cycle reversal and restoration;
- single and nested compound bounds.

`MermaidEngineTest` additionally covers all 40 official Flowchart gallery
sources, recursive subgraph directions, cluster endpoints, compact
self-loops, invisible edges, and crossing hops.

## Upgrade Procedure

1. Update the frozen versions in `UpstreamVersions.kt` and
   `tools/official-reference/package.json`.
2. Diff the mapped upstream files between the old and new tags.
3. Port only changed production methods while preserving file and stage
   boundaries.
4. Regenerate official references with `npm run render`.
5. Update or add numeric Dagre fixtures from the new package.
6. Run JVM, Android, and iOS builds, then capture the full device gallery.
7. Update this map and `THIRD_PARTY_NOTICES.md` before publishing.
