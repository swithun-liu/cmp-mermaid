# Label compatibility repair inventory

Baseline: Mermaid.js **12.0.0**. This inventory was established before changing
the production stages below. Paths are relative to `packages/mermaid/src`.
The existing family maps still apply to the rest of the production chain.

| Order | Upstream file and function/stage | Kotlin boundary | Classification and repair |
| ---: | --- | --- | --- |
| 1 | `diagram-api/comments.ts -> cleanupComments` | `MermaidPreprocessor.cleanupComments` | **Exact**: translate the complete regular expression, including bare comment markers and directive exclusion; line filtering currently removes more than upstream |
| 2 | `diagrams/flowchart/parser/flow.jison -> lexer actions for "^" and "v"` | `FlowJisonLexer.performAction` | **Exact**: retain generated rules and return the corresponding UP and DOWN tokens; the handwritten actions currently swap them |
| 3 | `diagrams/common/common.ts -> sanitizeText`; `rendering-util/createText.ts -> addHtmlSpan` assigns sanitized HTML to `span.innerHTML` | `MermaidHtmlFragmentTokenizer.tokenize/parseTag` | **Platform adaptation**: HTML tag-open and end-tag-open states only enter a tag name on an ASCII letter; a space or digit after `<` remains text |
| 4 | `rendering-util/createText.ts -> addHtmlSpan`, browser intrinsic and wrapped HTML measurement | `measureMermaidText`, prepared-scene metrics, `mermaidTextMaxLines` | **Platform adaptation**: no implicit eight-line truncation; an empty inline span has a zero-height box, while explicit line breaks retain their layout |
| 5 | `diagrams/flowchart/flowDb.ts -> getData`; `rendering-util/layout-algorithms/dagre/index.js -> measureDagreGraph/paintDagreLayoutCore`; `rendering-elements/nodes.ts -> insertNode` | `FlowchartDataAdapter.convert` | **Platform adaptation**: a group with no graph children is measured and painted through the ordinary rect-node path, including its padding and label; populated groups remain clusters |
| 6 | `themes/theme-*.js -> Theme.constructor`; `rendering-util/rendering-elements/shapes/util.ts -> labelHelper`; `roundedRect.ts -> roundedRect`; `drawRect.ts -> drawRect` | `MermaidTheme`, Flowchart metrics and `MermaidShapePort` | **Exact / Platform adaptation**: theme radius defaults (classic 5, Neo 3, Redux 12) and explicit overrides feed the rounded path; the same geometry consumes corrected empty-label dimensions; no input-specific shape substitution |
| 7 | `styles.ts` SVG filter, browser `SourceAlpha` for drop shadows | `drawSceneShapeShadow` | **Platform adaptation**: only painted fill/stroke contributes alpha; transparent fill and stroke cannot cast a solid shadow |
| 8 | `diagrams/class/classDb.ts -> addMember/addMembers`; `classTypes.ts -> ClassMember.constructor/parseMember`; `shapeUtil.ts -> textHelper/addText` | `ClassDb`, member parsing, `ClassJisonParser` | **Exact / Platform adaptation**: sanitize member input before parsing, then preserve Mermaid's generic formatting and rendering stages; expected sanitization failures propagate through `GMResult` |

The cluster-boundary investigation confirmed that the current graph preparation
matches `dagre/mermaid-graphlib.js -> adjustClustersAndEdges` for the reproduction.
The divergence starts in the following omitted edge clipping stage. This
additional inventory was established before changing that stage:

| Order | Upstream boundary | Kotlin boundary | Classification |
| ---: | --- | --- | --- |
| 9 | `rendering-elements/edges.js -> outsideNode` | `MermaidClusterEdgePort.outsideNode` | **Exact**: points on the rectangular boundary count as outside |
| 10 | `edges.js -> intersection` | `MermaidClusterEdgePort.intersection` | **Exact**: all top/bottom, side, and zero-delta branches |
| 11 | `edges.js -> cutPathAtIntersect` | `MermaidClusterEdgePort.cutPathAtIntersect` | **Exact**: retain points until the first inside point, deduplicate the intersection, then suppress the remainder |
| 12 | `edges.js -> insertEdge` non-swimlane endpoint clipping, then `toCluster` and reversed `fromCluster` clipping | `FlowDagreLayout.addRoutedEdge` | **Platform adaptation**: preserve stage order and original versus updated point lists |
| 13 | `edges.js -> positionEdgeLabel`; `utils.ts -> calcLabelPosition` | cluster updated-path label anchor | **Exact**: an updated single-point path anchors its label at that point |
| 14 | `utils/lineWithOffset.ts -> calculateDeltaAndAngle` | `MermaidEdgePathPort.deltaAndLineAngle` | **Exact**: absent neighbours yield zero angle/deltas; a single-point D3 path is valid |

No route is declared fixed from a screenshot alone; fresh path and topology
evidence accompanies the report.

This is a repair scope, not a claim of full family completion or Stable status.
Fresh verification and remaining differences are recorded separately.
