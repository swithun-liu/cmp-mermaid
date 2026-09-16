# Production Code Readiness

This document defines the code-level evidence required for CMP Mermaid to be
rated Stable. A successful build alone is not sufficient.

## Current State

- Mermaid compatibility baseline: `12.0.0`
- Native renderers: Flowchart, XY Chart, Sequence, Class, State, Entity
  Relationship, Gantt, Pie, User Journey, Requirement, Git Graph, and Mindmap
- Runtime implementation: Kotlin Multiplatform parser/layout/SceneGraph with
  Compose Canvas rendering
- Official Mermaid.js usage: debug and release evidence only
- Production JavaScript runtime: none; ELK requests return structured
  `UnsupportedFeature`
- Release status: **Stable**
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
| Visual fidelity | No known semantic or major visual mismatch in the independent production corpus | 158 manually reviewed Native/Official pairs plus 3,072 systematic matrix pairs; all 3,230 pass automated geometry | Passing locally; regular and full-matrix workflows are source controlled |
| Capability coverage | Every declared major capability appears in an independent conformance case | 207/207 points across 12 diagram types | Passing; generated-corpus validation enforces coverage |
| Determinism | Repeated rendering returns the same SceneGraph | Full 158-case corpus equality test | Passing |
| Theme compatibility | Every supported diagram type renders with every built-in theme | 12 diagram types by 11 themes; 132/132 renders | Passing |
| Parser/layout robustness | Systematic matrix and deterministic randomized corpus pass resource limits | 3,072 visual-matrix Native renders plus a separate 3,072 generated stress inputs | Passing |
| Core throughput | 790 warmed production renders complete within 45s and P95 is at most 500ms | Local baseline: 416ms total, 1ms P95 | Passing; enforced by JVM test |
| Core retained heap | The same soak retains at most 64 MiB after forced GC | Local baseline: 25,312 bytes | Passing; enforced by JVM test |
| Runtime matrix | Android, iOS Simulator, Desktop, and Web render representative complex cases | All four load screens reached the final case; platform metrics and available screenshots recorded | Passing locally |
| Runtime load | A scrolling page with many mixed diagrams stays responsive and within a documented memory budget | 158-diagram matrix recorded below | Passing on Android, iOS, Desktop, and Web |
| Production isolation | Core and Compose contain no WebView, JavaScript engine or bundle, network client, or debug-UI dependency | Source, dependency, JVM JAR, and Android AAR scans; debug/release APKs declare no Internet permission | Passing; source boundary and APK permission are enforced by the Quality Gate |
| Public-source safety | Published source and artifacts contain no internal endpoint or credential material | Repository scan plus APK permission audit | Passing |

## Visual Gate

The release corpus contains independent production-like scenarios rather than
copies of the documentation gallery. CI must:

1. Build the production Web distribution.
2. Render every case with Native Canvas and Mermaid.js `12.0.0`.
3. Reject blank images and severe content-bound differences.
4. Upload all screenshots, contact sheets, hashes, and geometry metrics.
5. Preserve manual review for text collisions, routing meaning, and semantic
   differences that image geometry cannot prove.

The regular Quality Gate runs the 158 independent cases on every push and pull
request. The weekly/manual Full Visual Parity workflow adds 256 unique sources
per diagram type, or 3,072 Native/Official pairs total. Those matrix cases are
deterministic combinations of 13 or 14 complex structural seeds per type and
20 visible text/layout-pressure profiles; they are not represented as 256
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
| Android Emulator | 158 | See current machine-readable evidence | See current machine-readable evidence | Passed current budget |
| iOS Simulator | 158 | See current machine-readable evidence | See current machine-readable evidence | Passed without a crash |
| Desktop | 158 | See current machine-readable evidence | See current machine-readable evidence | Passed without a crash |
| Web | 158 | See current machine-readable evidence | See current machine-readable evidence | Passed 15s/96MiB budget |

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
but they belong to the adopting application's deployment process and are not
part of this code-level Stable rating.

## Stable Rating Rule

The Stable label can be applied only when every gate above is passing, the
latest full evidence is linked from the stability report, and there are no
open severity-1 correctness, crash, resource-exhaustion, or data-exposure
defects.

All code-level gates above are passing for the recorded Mermaid `12.0.0`
baseline. CMP Mermaid is therefore rated **Stable** for its documented support
scope and can be used in production. An adopter remains responsible for its
own release strategy and operational monitoring.
