# Production Code Readiness

This document defines the code-level evidence required for CMP Mermaid to be
rated Stable. A successful build alone is not sufficient.

## Current State

- Mermaid compatibility baseline: `12.0.0`
- Native renderers: Flowchart, XY Chart, Quadrant Chart, Timeline, Kanban,
  Sequence, Class, State, Entity Relationship, Gantt, Pie, User Journey,
  Requirement, Git Graph, Mindmap, Packet, Radar, Sankey, Treemap, Venn,
  Ishikawa, Cynefin, Event Modeling, Agentflow, Block, Swimlanes, and
  Architecture, C4, and Railroad
- Runtime implementation: Kotlin Multiplatform parser/layout/SceneGraph with
  Compose Canvas rendering
- Official Mermaid.js usage: debug and release evidence only
- Production JavaScript runtime: none; ELK requests return structured
  `UnsupportedFeature`
- Official Mermaid families: 33
- Implemented families: 29; pending families: 4
- Release status: **Not Stable; full-family translation in progress**
- Completed replacement detail gates: Flowchart, XY Chart, Quadrant Chart,
  Timeline, Kanban, Sequence, Class, State, Entity Relationship, Gantt, Pie,
  User Journey, Requirement, Git Graph, Mindmap, Packet, Radar, Sankey,
  Treemap, Venn, Ishikawa, Cynefin, Event Modeling, Agentflow, Block,
  Swimlanes, Architecture, C4, and Railroad (`7,424/7,424` accepted;
  `6,618 automatic pass / 806 manually reviewed / 0 unresolved`;
  every implemented-family geometry gate passing)
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
| Visual fidelity | No known semantic or major visual mismatch in every family corpus | All 29 implemented families have 7,424/7,424 accepted detail and geometry pairs: 6,618 automatic passes plus 806 manually accepted ER/Journey/Requirement/Git Graph/Mindmap/Treemap/Venn/Event Modeling/Block/Swimlanes reviews | **Implemented subset passing; 4 families remain untranslated** |
| Capability coverage | Every Mermaid 12.0.0 family and declared major capability has an independent conformance case | 623/623 points across 29 implemented families; 4 families remain | **Incomplete** |
| Determinism | Repeated rendering returns the same SceneGraph | Full 384-case corpus equality test for the implemented subset | Passing for the implemented subset |
| Theme compatibility | Every implemented diagram family renders with every applicable built-in theme | 29 by 11 matrix: 319/319 renders | Passing for the implemented subset |
| Parser/layout robustness | Systematic matrix and deterministic randomized corpus pass resource limits | 7,424 visual-matrix Native renders plus a separate 7,424 generated stress inputs | Passing for the implemented subset |
| Core throughput | 1,920 warmed production renders complete within 45s and P95 is at most 500ms | Local baseline: 814ms total, 1ms P95 | Passing; enforced by JVM test |
| Core retained heap | The same soak retains at most 64 MiB after forced GC | Local baseline: 60,392 bytes | Passing; enforced by JVM test |
| Runtime matrix | Android, iOS Simulator, Desktop, and Web render representative complex cases | Web passed the current 384-scenario corpus; Android, iOS, and Desktop retain the prior 236-scenario baseline | Current Web and historical native baselines passing |
| Runtime load | A scrolling page with many mixed diagrams stays responsive and within a documented memory budget | Current 384-diagram Web run and prior 236-diagram native runs recorded below | Passing for the recorded corpus on each platform |
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

The regular Quality Gate runs the 384 independent cases on every push and pull
request. The weekly/manual Full Visual Parity workflow adds 256 unique sources
per diagram type, or 7,424 Native/Official pairs total. Those matrix cases are
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
| Web | 384 | 1.22s first content; 25.59s scroll | 18.1MiB retained JS heap | Passed the current complete implemented-family corpus |

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

The full rule is not currently satisfied. All 29 implemented families have
passed the replacement detail gate. The former coarse geometry check missed a
visible Git Graph paint-order defect; that defect is now corrected. No family
is promoted back to Stable until its 256-case detail queue is empty or every
explicit exception is reviewed and justified; overall Stable additionally
requires all 33 official families.
