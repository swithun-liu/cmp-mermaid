# Production Readiness

This document defines the release gate for promoting CMP Mermaid from Release
Candidate to Stable. A successful build alone is not sufficient.

## Current State

- Mermaid compatibility baseline: `12.0.0`
- Native renderers: Flowchart, XY Chart, Sequence, Class, State, Entity
  Relationship, Gantt, and Pie
- Runtime implementation: Kotlin Multiplatform parser/layout/SceneGraph with
  Compose Canvas rendering
- Official Mermaid.js usage: debug and release evidence only
- Release status: **Release Candidate**
- Detailed conformance scope:
  [`production-capability-matrix.md`](production-capability-matrix.md)

## Expected Behavior

Stable means that supported Mermaid input can be enabled in a production
application without WebView or Mermaid.js in the production rendering path,
with bounded resource use, deterministic results, documented unsupported
features, and a tested rollback path.

## Promotion Gates

| Gate | Requirement | Current evidence | Status |
| --- | --- | --- | --- |
| Visual fidelity | No known semantic or major visual mismatch in the independent production corpus | 106 Native/Official pairs; 106 manual passes; automated content geometry gate | Passing locally; remote CI run pending |
| Capability coverage | Every declared major capability appears in an independent conformance case | 16/16 points for each of 8 diagram types; 128/128 total | Passing; generated-corpus validation enforces coverage |
| Determinism | Repeated rendering returns the same SceneGraph | Full 106-case corpus equality test | Passing |
| Theme compatibility | Every supported diagram type renders with every built-in theme | 8 diagram types by 11 themes; 88/88 renders | Passing |
| Parser/layout robustness | Deterministic randomized corpus and resource limits pass | 2,048 generated stress inputs plus diagram-specific limits | Passing |
| Core throughput | 530 warmed production renders complete within 45s and P95 is at most 500ms | Local baseline: 8.60s total, 66ms P95 | Passing; enforced by JVM test |
| Core retained heap | The same soak retains at most 64 MiB after forced GC | Local baseline: about 20 KiB | Passing; enforced by JVM test |
| Runtime matrix | Android, iOS Simulator, Desktop, and Web render representative complex cases | All four load screens reached the final case; screenshots recorded | Passing locally |
| Runtime load | A scrolling page with many mixed diagrams stays responsive and within a documented memory budget | 106-diagram matrix recorded below | Passing locally; iOS/Desktop CI automation pending |
| Operational rollout | Feature flag, fallback/error UI, metrics, and rollback procedure are documented and exercised | Not yet exercised in a real integration | Pending |
| Production soak | At least one real integration completes a canary period without a renderer severity-1 defect | No canary evidence yet | Pending |

## Visual Gate

The release corpus contains independent production-like scenarios rather than
copies of the documentation gallery. CI must:

1. Build the production Web distribution.
2. Render every case with Native Canvas and Mermaid.js `12.0.0`.
3. Reject blank images and severe content-bound differences.
4. Upload all screenshots, contact sheets, hashes, and geometry metrics.
5. Preserve manual review for text collisions, routing meaning, and semantic
   differences that image geometry cannot prove.

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
| Android Emulator | 106 | 24s list traversal | 216MiB peak PSS; 180MiB final PSS | Passed 300MiB/45s budget |
| iOS Simulator | 106 | Automatic traversal reached final case | About 503MiB final host RSS | Passed without a crash |
| Desktop | 106 | Automatic traversal reached final case | About 428MiB process RSS | Passed |
| Web | 106 | 661ms first content; 5.77s scroll | 8.9MiB retained JS heap | Passed 15s/96MiB budget |

Headless Chromium process-tree RSS is recorded as diagnostic data but is not a
renderer budget because it includes browser infrastructure outside the Web
application's JS heap.

The iOS implementation deliberately executes the Compose-backed render on
`Dispatchers.Main.immediate`. Runtime evidence showed that concurrent
default-queue renders could race Skia's font provider and crash. This preserves
correctness and deterministic ownership at the cost of serial Apple rendering;
the canary must monitor long-frame rate for unusually large diagrams.

## Production Integration Contract

A production adopter must:

- keep the renderer behind a remotely controlled feature flag during canary;
- handle `MermaidError.UnsupportedFeature` without retry loops;
- cap source length, nodes, edges, and text through `MermaidRenderOptions`;
- record render duration, diagram type, success/error category, and fallback
  usage without logging Mermaid source text;
- provide a rollback path to plain source text or another safe representation.

## Stable Promotion Rule

The Stable label can be applied only when every gate above is passing, the
latest full evidence is linked from the stability report, and there are no
open severity-1 correctness, crash, resource-exhaustion, or data-exposure
defects.

The repository is technically ready for a remotely controlled canary, not for
an unconditional Stable declaration. The remaining promotion work belongs to
the adopting application: exercise the feature flag, telemetry, fallback, and
rollback path with real traffic.
