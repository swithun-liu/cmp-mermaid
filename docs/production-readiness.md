# Production Code Readiness

This document defines the code-level evidence required for CMP Mermaid to be
rated Stable. A successful build alone is not sufficient.

## Current State

- Mermaid compatibility baseline: `12.0.0`
- Native renderers: Flowchart, XY Chart, Quadrant Chart, Timeline, Kanban,
  Sequence, Class, State, Entity Relationship, Gantt, Pie, User Journey,
  Requirement, Git Graph, Mindmap, Packet, Radar, Sankey, Treemap, Venn,
  Ishikawa, Cynefin, Event Modeling, Agentflow, Block, Swimlanes, and
  Architecture, C4, Railroad, TreeView, Use Case, Wardley Map, and ZenUML
- Runtime implementation: Kotlin Multiplatform parser/layout/SceneGraph with
  Compose Canvas rendering
- Official Mermaid.js usage: debug and release evidence only
- Production JavaScript runtime: none; ELK requests return structured
  `UnsupportedFeature`
- Official Mermaid families: 33
- Implemented families: 33; pending translations: 0
- Release status: **Stable**
- Completed replacement detail gates: Flowchart, XY Chart, Quadrant Chart,
  Timeline, Kanban, Sequence, Class, State, Entity Relationship, Gantt, Pie,
  User Journey, Requirement, Git Graph, Mindmap, Packet, Radar, Sankey,
  Treemap, Venn, Ishikawa, Cynefin, Event Modeling, Agentflow, Block,
  Swimlanes, Architecture, C4, Railroad, TreeView, Use Case, Wardley Map, and
  ZenUML
  (`8,448/8,448` accepted;
  `7,458 automatic pass / 990 manually reviewed / 0 unresolved`;
  every listed-family geometry gate passing)
- Pending replacement detail gates: none
- Detailed conformance scope:
  [`production-capability-matrix.md`](production-capability-matrix.md)

## Expected Behavior

Stable means that supported Mermaid input can be rendered in a production
application without WebView, Mermaid.js, or network access in the production
rendering path, with bounded resource use, deterministic results, documented
unsupported features, and no known high-severity defect in the supported
contract.

## Promotion Gates

| Gate | Requirement | Current evidence | Status |
| --- | --- | --- | --- |
| Visual fidelity | No known semantic or major visual mismatch in every family corpus | All 33 families have 8,448/8,448 accepted detail and geometry pairs across 528 manually reviewed contact sheets | Passing |
| Capability coverage | Every Mermaid 12.0.0 family and declared major capability has an independent conformance case | 738/738 points across all 33 families | Passing |
| Determinism | Repeated rendering returns the same SceneGraph | Full 441-case corpus equality test | Passing |
| Theme compatibility | Every implemented diagram family renders with every applicable built-in theme | 33 by 11 matrix: 363/363 renders | Passing |
| Parser/layout robustness | Systematic matrix and deterministic randomized corpus pass resource limits | 8,448 visual-matrix Native renders pass; the separate 7,936-case randomized stress baseline remains published | Passing |
| Core throughput | 2,075 warmed production renders complete within 45s and P95 is at most 500ms | Local baseline: 674ms total, 1ms P95 | Passing; enforced by JVM test |
| Core retained heap | The same soak retains at most 64 MiB after forced GC | Local baseline: 63,968 bytes | Passing; enforced by JVM test |
| Runtime matrix | Android, iOS Simulator, Desktop, and Web render representative complex cases | Web passed the preceding 397-scenario corpus; Android, iOS, and Desktop retain the prior 236-scenario baseline | Passing retained baselines; no fresh 441-case rerun is claimed |
| Runtime load | A scrolling page with many mixed diagrams stays responsive and within a documented memory budget | Preceding 397-diagram Web run and prior 236-diagram native runs recorded below | Passing for the recorded corpus on each platform |
| Production isolation | Core and Compose contain no WebView, JavaScript engine or bundle, network client, or debug-UI dependency | Source, dependency, JVM JAR, and Android AAR scans; debug/release APKs declare no Internet permission | Passing; source boundary and APK permission are enforced by the Quality Gate |
| Public-source safety | Published source and artifacts contain no internal endpoint or credential material | Repository scan plus APK permission audit | Passing |

## Visual Gate

The release corpus contains independent production-like scenarios rather than
copies of the documentation gallery. CI must:

1. Build the production Web distribution.
2. Render every case with Native Canvas and Mermaid.js `12.0.0`.
3. Export a Native SceneGraph manifest and an Official SVG DOM manifest for
   text, shapes, paths, marker endpoint anchors, styles, bounds, and actual
   paint order.
4. Reject semantic text loss, clipping mismatches, and paint-order occlusion
   mismatches; queue geometry, style, overlap, mask, edge, and color deviations
   for review.
5. Upload screenshots, manifests, heatmaps, contact sheets, hashes, and both
   coarse and detail reports.
6. Complete manual review for routing meaning and intentional renderer
   differences that automated metrics cannot prove.

The regular Quality Gate runs the 441 independent cases on every push and pull
request. The weekly/manual Full Visual Parity workflow now defines 256 unique
sources per diagram type, or 8,448 Native/Official pairs total. The published
accepted evidence covers all 8,448 pairs. Those matrix cases are
deterministic combinations of at least 13 complex structural seeds per type
and visible text/layout-pressure profiles; they are not represented as 256
unrelated topologies per type.

## Performance Gate

The core soak gate measures parser, layout, and SceneGraph generation after two
warmup rounds. Canvas/runtime measurements are separate because Compose, Skia,
device density, and GPU behavior are platform-specific.

The runtime load gate must record:

- first-content and complete-list traversal latency;
- peak and retained process memory;
- behavior while scrolling a mixed list of diagrams;
- cancellation and disposal behavior after leaving the screen.

## Local Runtime Baseline

| Platform | Corpus | Latency | Memory | Outcome |
| --- | ---: | --- | --- | --- |
| Android Emulator | 236 | See machine-readable prior baseline | See machine-readable prior baseline | Passed prior budget through Sankey; Treemap, Venn, Ishikawa, Cynefin, Event Modeling, Agentflow, Block, Swimlanes, and Architecture not included |
| iOS Simulator | 236 | See machine-readable prior baseline | See machine-readable prior baseline | Passed without a crash through Sankey; later implemented families not included |
| Desktop | 236 | See machine-readable prior baseline | See machine-readable prior baseline | Passed without a crash through Sankey; later implemented families not included |
| Web | 397 | 1,227ms first content; 22,455ms traversal across 185 scroll events | 18,417,340 bytes retained JS heap after forced GC | Passed; 397/397 rendered, final Agentflow case reached, no browser errors |

Headless Chromium process-tree RSS is recorded as diagnostic data but is not a
renderer budget because it includes browser infrastructure outside the Web
application's JS heap.

The iOS and Desktop implementations deliberately execute Compose-backed text
measurement on their UI dispatchers: `Dispatchers.Main.immediate` on iOS and
the Swing EDT on Desktop. Runtime evidence showed that concurrent background
measurement could race Skia glyph drawing and crash in CoreText. This preserves
correctness and deterministic ownership at the cost of serial rendering on
those platforms; adopters should monitor long-frame rate for unusually large
diagrams.

## Production Integration Guidance

A production adopter should:

- handle `MermaidError.UnsupportedFeature` without retry loops;
- cap source length, nodes, edges, and text through `MermaidRenderOptions`;
- record render duration, diagram type, success/error category, and fallback
  usage without logging Mermaid source text;
- provide a rollback path to plain source text or another safe representation.

Feature flags, canaries, and gradual rollout remain useful release controls,
but they belong to the adopting application's deployment process and do not
replace the repository's Stable gates.

## Stable Rating Rule

The Stable label can be applied only when every gate above is passing, the
latest full evidence is linked from the stability report, and there are no
open severity-1 correctness, crash, resource-exhaustion, or data-exposure
defects.

The full rule is satisfied for the supported Mermaid `12.0.0` contract. All 33
families are implemented and have passed the replacement detail, geometry, and
manual review gates. The former coarse geometry check missed a visible Git
Graph paint-order defect; that defect is corrected. The load and soak values
above remain explicitly identified as retained historical baselines and are
not presented as a fresh 441-case performance rerun.
