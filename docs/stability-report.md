# Mermaid 12.0.0 Stable Test Report

This report records the evidence behind CMP Mermaid's current
**Stable** status for the documented support scope. The evidence is
deliberately separate from the demo gallery and distinguishes independent
production scenarios, systematic visual-matrix variants, and Native-only
randomized stress inputs.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Stable** |
| Mermaid compatibility baseline | `12.0.0` |
| Independent production scenarios | 119: 42 original stability cases plus 77 additional conformance cases |
| Declared capability coverage | 144/144 points across 9 diagram types |
| Large-scale visual matrix | 2,304 unique Mermaid sources: 256 per diagram type |
| Native core render results | 119 independent plus 2,304 matrix cases passed, 0 failed |
| Web Native/Official captures | 4,608 matrix screenshots plus 238 independent-corpus screenshots, 0 render errors |
| Manual visual review | 119 independent scenarios acceptable, 0 blocked; all 256 Journey matrix pairs reviewed |
| Automated visual geometry | 2,304/2,304 matrix pairs and 119/119 independent pairs passed |
| Deterministic SceneGraph replay | 119 passed, 0 mismatches |
| Built-in theme matrix | 99/99 renders passed: 9 diagram types by 11 themes |
| Separate deterministic Native stress inputs | 2,304 |
| JVM tests | 296 passed, 0 failed |
| Core production soak | 595 renders; 9.81s total; 76ms P95; about 19KiB retained heap |
| Runtime load matrix | Android Emulator, iOS Simulator, Desktop, and Web passed |
| Platform build matrix | Android debug/release, Web production, Desktop distributable, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared in debug or release APK |
| Public-source safety scan | No organization-specific endpoint or credential pattern found |

**Conclusion:** all nine supported diagram types pass the independent
119-case visual corpus, the 2,304-case Native/Official visual matrix,
deterministic replay, separate generated stress tests, core soak, runtime load
matrix, cross-platform build matrix, and the repository Quality Gate. This
satisfies the project's code-level Stable criteria. Canary, feature flags, and
rollback are deployment choices for an adopting application, not prerequisites
for rating the renderer code as Stable.

## What This Report Does And Does Not Prove

This report proves that the exact source-controlled corpus:

- compiles through the Kotlin parser, database, layout, and SceneGraph pipeline;
- produces non-empty Native Canvas output;
- is accepted and rendered by the pinned Mermaid.js `12.0.0` reference;
- has been captured side by side at the same `1200 x 900` viewport;
- passes automated blank-image and severe content-geometry checks;
- can be regenerated from the repository scripts.

It does **not** prove that every possible legal Mermaid program is supported.
The automated image gate measures content bounds and foreground density, not
full semantic or pixel equality, so manual review remains required. Platform
font metrics and text wrapping may differ. An adopter can still use telemetry
and gradual rollout to manage its own release risk, but that operational
choice is outside this code-level rating.

## Evidence Layers

The three large evidence sets answer different questions and are not counted
as substitutes for each other:

1. **119 independent production scenarios.** These are hand-authored,
   production-like structures used for capability coverage, deterministic
   replay, manual review, performance soak, and the regular Quality Gate.
2. **2,304 Native/Official visual-matrix cases.** Each diagram type contributes
   256 unique Mermaid sources, derived deterministically from 13 or 14 complex
   structural seeds and 20 visible text/layout-pressure profiles. This is not a
   claim of 256 unrelated topologies per type.
3. **2,304 Native-only randomized stress inputs.** These separately exercise
   parser and layout robustness. They are not presented as Mermaid.js parity
   evidence.

## Independent Production Corpus

The canonical corpus is
[`tools/official-reference/production-corpus.mjs`](../tools/official-reference/production-corpus.mjs).
It includes 42 original stability cases plus 77 conformance cases that are
also independent from the 241-item demo gallery. Generated Kotlin copies are
consumed independently by core tests and the Web audit screen. The original
cases retain their legacy `rc_` IDs for evidence continuity; additional cases
use `prod_`. Neither set can be resolved through the normal demo gallery.

| Diagram | Production cases | Capability points | Scenario examples | Manual visual result |
| --- | ---: | ---: | --- | --- |
| Flowchart | 14 | 16/16 | orchestration, edge semantics, advanced shapes, nested domains | Acceptable |
| XY Chart | 13 | 16/16 | latency, categorical and numeric axes, horizontal labels, mixed plots | Acceptable |
| Sequence | 14 | 16/16 | checkout saga, lifecycle, self messages, parallel and critical regions | Acceptable; self-message/frame bounds rechecked |
| Class | 13 | 16/16 | commerce, namespaces, generics, relations, annotations | Acceptable |
| State | 13 | 16/16 | fulfillment, nested composites, fork/join, concurrency, notes | Acceptable |
| Entity Relationship | 13 | 16/16 | commerce, aliases, attributes, cardinalities, nested subgraphs | Acceptable |
| Gantt | 13 | 16/16 | release plans, date units, exclusions, top axes, vertical markers | Acceptable |
| Pie | 13 | 16/16 | cost, escaped labels, donut, legends, themes, many slices | Acceptable |
| User Journey | 13 | 16/16 | sections, scores, actor order, metadata, configuration, long text | Acceptable; all 256 matrix pairs manually reviewed |

`Acceptable` means the manual review found no missing domain entity, state,
message, series, relationship, or route that changes the meaning of the
diagram. It allows minor font, spacing, and edge-routing differences.

## Large-Scale Visual Matrix

The large-scale matrix contains 256 unique sources for each of Flowchart,
XY Chart, Sequence, Class, State, Entity Relationship, Gantt, Pie, and User
Journey:

- 2,304 unique Mermaid sources;
- 2,304 CMP Native screenshots;
- 2,304 Mermaid.js `12.0.0` screenshots;
- 144 paged contact sheets, with 16 same-source pairs per page;
- source, seed, profile, feature, screenshot, and SHA-256 metadata;
- per-pair content bounds and foreground-density metrics.

**[Open all 144 paged Native/Official comparison images](assets/stability-report/visual-parity-evidence.md).**

Machine-readable evidence:
[manifest](assets/stability-report/visual-parity-manifest.json) and
[geometry report](assets/stability-report/visual-parity-geometry.json).
The automated gate accepted all 2,304 pairs. Across the complete matrix,
Native/Official width ratios were `1.003-1.261`, height ratios were
`0.878-1.140`, and foreground-ink ratios were `0.585-1.467`, within the
source-controlled thresholds.

## Independent Production Visual Evidence

Each contact sheet uses the same Mermaid source on both sides:

- left: CMP Native, rendered through Kotlin and Compose Canvas;
- right: Mermaid.js `12.0.0`, loaded from the repository's pinned local asset.

The source IDs and scenario descriptions are printed above every pair.

<details open>
<summary><strong>Flowchart: 14 production scenarios</strong></summary>

![Flowchart complex Native and Official corpus](assets/stability-report/flowchart-complex-corpus.png)

</details>

<details open>
<summary><strong>XY Chart: 13 production scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

</details>

<details>
<summary><strong>Sequence: 14 production scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The checkout saga was re-audited at full capture resolution. Sequence numbers
now use Mermaid `12.0.0` activation bounds, the official `6px` marker radius,
message-line clearance, and the upstream draw order that keeps number markers
above activation bars.

</details>

<details>
<summary><strong>Class: 13 production scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

</details>

<details>
<summary><strong>State: 13 production scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

</details>

<details>
<summary><strong>Entity Relationship: 13 production scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

</details>

<details open>
<summary><strong>Gantt: 13 production scenarios</strong></summary>

![Gantt complex Native and Official corpus](assets/stability-report/gantt-complex-corpus.png)

The former width mismatch came from the debug Official iframe shrinking
Mermaid's temporary render container to `300px`, not from the Native timeline.
The comparison host now preserves the full iframe width and rejects a collapsed
Official Gantt viewBox. Multi-unit ticks also follow D3 `interval.every(count)`
epoch and calendar-field anchoring; the `2week` case now starts on the same
weeks as Mermaid.js.

</details>

<details>
<summary><strong>Pie: 13 production scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

</details>

<details open>
<summary><strong>User Journey: 13 production scenarios</strong></summary>

![User Journey complex Native and Official corpus](assets/stability-report/journey-complex-corpus.png)

All 256 Journey matrix pairs were manually reviewed. The Journey-only geometry
ratios were `1.020-1.032` for width, `1.031-1.050` for height, and
`0.999-1.080` for foreground ink.

</details>

The generated capture metadata, byte sizes, and SHA-256 values are available in
[`manifest.json`](assets/stability-report/manifest.json). Per-case Native versus
Official content bounds, foreground density, ratios, thresholds, and failures
are recorded in
[`geometry-report.json`](assets/stability-report/geometry-report.json).

## Automated Test Evidence

The repository-level
[`Quality Gate`](../.github/workflows/quality.yml) repeats the JVM tests,
cross-platform builds, generated-corpus and capability-coverage checks, source
and credential scan, debug/release APK permission audit, 238-image capture,
visual geometry gate, and Web load test on every push to `main` and every pull
request. The
[`Full Visual Parity`](../.github/workflows/full-visual-parity.yml) workflow
runs the 2,304-pair matrix weekly and on demand in nine parallel diagram jobs.

The full verification command completed successfully:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  :sample:androidApp:assembleDebug \
  :sample:androidApp:assembleRelease \
  :sample:webApp:wasmJsBrowserDistribution \
  :sample:desktopApp:createDistributable \
  :mermaid-core:compileKotlinIosArm64 \
  :mermaid-core:compileKotlinIosSimulatorArm64 \
  :mermaid-core:compileKotlinIosX64 \
  :mermaid-compose:compileKotlinIosArm64 \
  :mermaid-compose:compileKotlinIosSimulatorArm64 \
  :mermaid-compose:compileKotlinIosX64 \
  :mermaid-debug-ui:compileKotlinIosArm64 \
  :mermaid-debug-ui:compileKotlinIosSimulatorArm64 \
  :mermaid-debug-ui:compileKotlinIosX64
```

Result:

```text
BUILD SUCCESSFUL
mermaid-core: 279 tests
mermaid-compose: 17 tests
total: 296 tests
failures: 0
errors: 0
```

The independent corpus test is
[`ProductionCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/ProductionCorpusTest.kt).
It compiles all 119 sources and rejects parser errors, invalid or non-finite
geometry, scenes outside the 20,000-unit and 20,000-element limits, empty
SceneGraphs, and non-deterministic replay. The 64 conformance additions also
require selected semantic text to survive parsing and layout. The test renders
a representative of all nine diagram types with each of the 11 built-in
themes.

`ProductionCorpusTest` additionally renders all 2,304 visual-matrix sources,
checks the expected visible text, and rejects empty, invalid, or non-finite
SceneGraphs. The separate deterministic stress suites generate another 256
Native-only inputs for each of the nine diagram types, for 2,304 stress inputs
in total. Flowchart exercises both Dagre and ELK for every generated stress
source.

## Determinism And Performance Evidence

Every one of the 119 independent scenarios is rendered twice and compared as a
complete `MermaidScene`, including dimensions, elements, paths, text, styles,
metadata, and z-order.

The JVM production soak performs two warmup rounds followed by five measured
rounds over all 119 scenarios:

```text
renders=595
totalMs=9806
p95Ms=76
retainedHeapBytes=19824
```

Enforced budgets are 45 seconds total, 500ms P95, and 64MiB retained heap after
forced GC.

## Runtime Load Matrix

The shared load screen renders the 119 mixed scenarios in a `LazyColumn` and
walks from the first Flowchart to the final User Journey chart.

| Platform | Result | Local evidence |
| --- | --- | --- |
| Android Emulator | Passed | 29s scroll; 231MiB peak PSS; 189MiB final PSS; final case reached |
| iOS Simulator | Passed | Automatic traversal reached final case; about 245MiB final host RSS; no crash |
| Desktop | Passed | Automatic traversal reached final case; about 88MiB process RSS |
| Web | Passed | 1.32s first content; 6.36s scroll; 8.8MiB retained JS heap; no browser errors |

Machine-readable measurements:
[Android](assets/runtime-load/android-emulator-metrics.json),
[iOS](assets/runtime-load/ios-simulator-metrics.json),
[Desktop](assets/runtime-load/desktop-metrics.json), and
[Web](assets/runtime-load/web-metrics.json).

All four screenshots show the final corpus case after traversing the same mixed
list:

| Android Emulator | iOS Simulator |
| :---: | :---: |
| <img src="assets/runtime-load/android-emulator-bottom.png" alt="Android load test final case" width="360"> | <img src="assets/runtime-load/ios-simulator-bottom.png" alt="iOS load test final case" width="360"> |

| Desktop | Web |
| :---: | :---: |
| <img src="assets/runtime-load/desktop-bottom.png" alt="Desktop load test final case" width="700"> | <img src="assets/runtime-load/web-bottom.png" alt="Web load test final case" width="700"> |

The iOS run also identified and fixed a real concurrency defect: concurrent
background renders were mutating Compose/Skia's font provider from multiple
default-queue threads and could crash with `EXC_BAD_ACCESS`. The Apple Compose
render path now serializes text measurement and rendering on
`Dispatchers.Main.immediate`; Android, Desktop, and Web retain
`Dispatchers.Default`.

## APK Permission Audit

Audited artifacts:

```text
sample/androidApp/build/outputs/apk/debug/androidApp-debug.apk
sample/androidApp/build/outputs/apk/release/androidApp-release-unsigned.apk
```

Declared permissions:

```text
com.swithun.cmpmermaid.sample.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
```

`android.permission.INTERNET` is not declared.

## Reproduce The Report

```bash
cd tools/official-reference
npm run generate:stability-corpus

cd ../..
JAVA_HOME=$(/usr/libexec/java_home -v 17) \
  ./gradlew \
    :mermaid-core:jvmTest \
    :mermaid-compose:jvmTest \
    :sample:androidApp:assembleDebug \
    :sample:androidApp:assembleRelease \
    :sample:webApp:wasmJsBrowserDistribution \
    :sample:desktopApp:createDistributable

python3 -m http.server 8093 \
  --directory sample/webApp/build/dist/wasmJs/productionExecutable \
  >/tmp/cmp-mermaid-web.log 2>&1 &
WEB_SERVER_PID=$!
trap 'kill "$WEB_SERVER_PID"' EXIT

cd tools/official-reference
AUDIT_SOURCE=production \
OUTPUT_DIR=captures/local/production-corpus \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=production \
INPUT_DIR=captures/local/production-corpus \
OUTPUT_FILE=captures/local/production-corpus/geometry-report.json \
npm run audit:stability-geometry

BASE_URL=http://127.0.0.1:8093/ \
OUTPUT_DIR=captures/local/load-test-web \
npm run test:web-load

CORPUS_SOURCE=production \
INPUT_DIR=captures/local/production-corpus \
OUTPUT_DIR=captures/local/production-contact-sheets \
npm run generate:stability-contact-sheets

AUDIT_SOURCE=visual-parity \
OUTPUT_DIR=captures/local/visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_FILE=docs/assets/stability-report/visual-parity-geometry.json \
npm run audit:stability-geometry

CORPUS_SOURCE=visual-parity \
INPUT_DIR=captures/local/visual-parity \
OUTPUT_DIR=docs/assets/stability-report \
CONTACT_SHEET_PAGE_SIZE=16 \
npm run generate:stability-contact-sheets
```

The capture command fails if a requested Native canvas or Official SVG does not
appear, if Mermaid reports an error, if an Official Gantt viewBox collapses, or
if a screenshot is below the minimum size. The geometry gate rejects blank
images and severe width, height, or foreground-density differences. The
contact-sheet generator verifies every expected pair and records its SHA-256.
Set `AUDIT_KIND` and `CORPUS_KIND` to one of the nine diagram IDs to reproduce
a single 256-case partition instead of the complete matrix.

## Stable Acceptance Criteria

The Stable label requires all of these code-level gates:

- the independent complex corpus has no blocked visual category;
- Native/Official comparison includes automated semantic or perceptual
  thresholds with reviewed exceptions;
- the Quality Gate workflow is required and green on `main`;
- Android, Web, iOS, and Desktop have runtime smoke evidence, not compile-only
  evidence;
- bulk rendering has repeatable memory, latency, and long-running soak limits;
- no open high-severity correctness, crash, resource-exhaustion, or
  data-exposure defect exists for the supported contract.

Every criterion above passes for the recorded Mermaid `12.0.0` evidence, so the
public code status is **Stable**. Production canaries and rollback controls
remain good release practices for adopters, but do not change this code rating.
