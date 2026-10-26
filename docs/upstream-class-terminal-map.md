# Class terminal-label repair inventory

Baseline: Mermaid.js **12.0.0**. This inventory was established before the
terminal-label repair. Source paths are relative to `packages/mermaid/src`.

| Order | Upstream file and function / stage | Kotlin boundary | Classification |
| ---: | --- | --- | --- |
| 1 | `diagrams/class/classDb.ts -> getData`, `getArrowMarker` | `ClassLayout.addRelationLabels` | Exact: Class uses start-right and end-left labels; all marker names, including `"none"`, are nonempty strings |
| 2 | `rendering-util/rendering-elements/edges.js -> insertEdgeLabel`, start-right inner transform and unused end-left inner transform | `ClassLayout.addTerminalLabel` | Platform adaptation: measure text through the host; center start-right text, leave end-left text at the terminal origin |
| 3 | `edges.js -> setTerminalWidth` | Scene text versus reference DOM capture | Platform adaptation: the reference HTML container uses `value.length * 9` by `12`; its unused blank area is not painted text |
| 4 | `edges.js -> positionEdgeLabel`, each terminal branch | `ClassLayout.addRelationLabels` | Exact: marker-string truthiness selects a marker size of 10, even for `"none"`; use the final routed polyline |
| 5 | `utils.ts -> calcTerminalLabelPosition`, clone and reverse | `MermaidEdgeLabelPort.calcTerminalLabelPosition` | Exact: start positions retain path order; end positions reverse a copy |
| 6 | `utils.ts -> distance`, `calculatePoint`, `roundNumber` | Existing corresponding methods in `MermaidEdgeLabelPort` | Platform adaptation: preserve duplicate-segment early return, five-decimal interpolation and failure as `GMResult.Err` |
| 7 | `utils.ts -> calcTerminalLabelPosition`, angle and four positional branches | `MermaidEdgeLabelPort.calcTerminalLabelPosition` | Exact: retain marker offset, left/right angle changes and the end-label minus-five offsets |
| 8 | Terminal groups painted before Class nodes | `ClassLayout`, existing z-indices | Platform adaptation: retain official occlusion; no obstacle avoidance or displacement stage exists upstream |

The test-only utility oracle executes unchanged upstream function bodies.
Tests cover all four positions, both marker sizes, path reversal without input
mutation, duplicate points and short/empty/singleton failure paths. Class scene
tests check the mapped terminal coordinates rather than imposing avoidance.

Reference occlusion uses DOM text-range rectangles separately from the HTML
layout container. Real-browser tests distinguish blank-container intersection
from actual text intersection. This repair does not establish full-family
Stable status or resolve platform font and container-size differences.
