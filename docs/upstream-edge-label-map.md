# Final edge label placement repair

Baseline: Mermaid.js **12.0.0**. This inventory precedes production changes.
Paths below are relative to `packages/mermaid/src`.

| Order | Upstream file and function | Kotlin boundary | Classification |
| ---: | --- | --- | --- |
| 1 | `rendering-util/rendering-elements/edges.js -> insertEdge`, endpoint clipping and `toCluster` / reversed `fromCluster` clipping | `FlowDagreLayout.addRoutedEdge` | Platform adaptation: preserve layout anchor and whether cluster clipping changed the path |
| 2 | `edges.js -> insertEdge`, `fixCorners`, marker offsets and curve generation | `MermaidEdgePathPort.generate` | Existing translated geometry; this repair does not change edge routing |
| 3 | `d3-shape/src/path.js -> withPath`; `d3-path/src/path.js -> appendRound`, `moveTo`, `lineTo`, `quadraticCurveTo`, `bezierCurveTo`, `closePath` | edge-label path serialization | Platform adaptation: serialize the generated commands with D3's default three decimal places; rounded paths use unrounded numbers |
| 4 | `utils.ts -> isLabelCoordinateInPath` | `MermaidEdgeLabelPort.isLabelCoordinateInPath` | Exact: round the point, replace positive decimal substrings in the path string, then test substring inclusion of either coordinate |
| 5 | `edges.js -> insertEdge`, final `midIndex` / `pointsHasChanged` / `paths.updatedPath` stage | `MermaidEdgeLabelPort.positionEdgeLabel` | Exact: an absent middle coordinate or cluster clipping marks the path updated |
| 6 | `utils.ts -> distance` | `MermaidEdgeLabelPort.distance` | Exact: Euclidean distance; a missing previous point contributes zero |
| 7 | `utils.ts -> traverseEdge` | `MermaidEdgeLabelPort.traverseEdge` | Exact: sum segment distances and traverse half the total |
| 8 | `utils.ts -> calcLabelPosition` | `MermaidEdgeLabelPort.calcLabelPosition` | Exact: a singleton anchors at the sole point |
| 9 | `utils.ts -> roundNumber` | `MermaidEdgeLabelPort.roundNumber` | Exact: JavaScript rounding with configurable precision |
| 10 | `utils.ts -> calculatePoint` | `MermaidEdgeLabelPort.calculatePoint` | Platform adaptation: retain zero-length early return, strict distance comparison and five-decimal interpolation; upstream's failure becomes `GMResult.Err` |
| 11 | `edges.js -> positionEdgeLabel`, main label branch | Flowchart, ER, State and Requirement scene builders | Platform adaptation: retain the layout anchor unless the path was updated, then consume the calculated anchor; keep each renderer's existing title-margin handling |

The relevant `utils.spec.ts -> calculatePoint` tests are translated alongside
normal, singleton, duplicate-point, signed-rounding and failure cases. A test-only
oracle executes extracted, unchanged upstream function bodies and D3 path
serialization. Production remains pure Kotlin.

Terminal labels are covered by the separate
[Class terminal-label inventory](upstream-class-terminal-map.md).
Hand-drawn path mutation and other unrelated renderer stages
are outside this repair. This inventory does not establish full-family Stable
status. Fresh same-source evidence and unresolved differences are recorded in
the repair report.
