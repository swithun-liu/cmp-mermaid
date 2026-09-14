# Mermaid 12.0.0 Stable Test Report

This report records the evidence behind CMP Mermaid's current
**Stable** status for the documented support scope. It is deliberately
separate from the demo gallery: every visual case below comes from an
independent complex scenario corpus that is not shown as a product demo.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Stable** |
| Mermaid compatibility baseline | `12.0.0` |
| Independent production scenarios | 106: 42 original stability cases plus 64 additional conformance cases |
| Declared capability coverage | 128/128 points across 8 diagram types |
| Native core render results | 106 passed, 0 failed |
| Web Native/Official captures | 212 generated, 0 render errors |
| Manual visual review | 106 acceptable, 0 blocked |
| Automated visual geometry | 106 passed, 0 failed |
| Deterministic SceneGraph replay | 106 passed, 0 mismatches |
| Built-in theme matrix | 88/88 renders passed: 8 diagram types by 11 themes |
| Deterministic generated stress inputs | 2,048 |
| JVM tests | 279 passed, 0 failed |
| Core production soak | 530 renders; 8.60s total; 66ms P95; about 20KiB retained heap |
| Runtime load matrix | Android Emulator, iOS Simulator, Desktop, and Web passed |
| Platform build matrix | Android debug/release, Web production, Desktop distributable, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared in debug or release APK |
| Public-source safety scan | No organization-specific endpoint or credential pattern found |

**Conclusion:** all eight supported diagram types pass the independent
106-case visual corpus, deterministic replay, generated stress tests, core
soak, runtime load matrix, cross-platform build matrix, and the repository
Quality Gate. This satisfies the project's code-level Stable criteria.
Canary, feature flags, and rollback are deployment choices for an adopting
application, not prerequisites for rating the renderer code as Stable.

## What This Report Does And Does Not Prove

This report proves that the exact source-controlled corpus:

- compiles through the Kotlin parser, database, layout, and SceneGraph pipeline;
- produces non-empty Native Canvas output;
- is accepted and rendered by the pinned Mermaid.js `12.0.0` reference;
- has been reviewed side by side at the same `1200 x 900` viewport;
- passes automated blank-image and severe content-geometry checks;
- can be regenerated from the repository scripts.

It does **not** prove that every possible legal Mermaid program is supported.
The automated image gate measures content bounds and foreground density, not
full semantic or pixel equality, so manual review remains required. Platform
font metrics and text wrapping may differ. An adopter can still use telemetry
and gradual rollout to manage its own release risk, but that operational
choice is outside this code-level rating.

## Independent Production Corpus

The canonical corpus is
[`tools/official-reference/production-corpus.mjs`](../tools/official-reference/production-corpus.mjs).
It includes 42 original stability cases plus 64 conformance cases that are
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

`Acceptable` means the manual review found no missing domain entity, state,
message, series, relationship, or route that changes the meaning of the
diagram. It allows minor font, spacing, and edge-routing differences.

## Visual Evidence

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

The generated capture metadata, byte sizes, and SHA-256 values are available in
[`manifest.json`](assets/stability-report/manifest.json). Per-case Native versus
Official content bounds, foreground density, ratios, thresholds, and failures
are recorded in
[`geometry-report.json`](assets/stability-report/geometry-report.json).

## Automated Test Evidence

The repository-level
[`Quality Gate`](../.github/workflows/quality.yml) repeats the JVM tests,
cross-platform builds, generated-corpus and capability-coverage checks, source
and credential scan, debug/release APK permission audit, 212-image capture,
visual geometry gate, and Web load test on every push to `main` and every pull
request.

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
mermaid-core: 262 tests
mermaid-compose: 17 tests
total: 279 tests
failures: 0
errors: 0
```

The independent corpus test is
[`ProductionCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/ProductionCorpusTest.kt).
It compiles all 106 sources and rejects parser errors, invalid or non-finite
geometry, scenes outside the 20,000-unit and 20,000-element limits, empty
SceneGraphs, and non-deterministic replay. The 64 conformance additions also
require selected semantic text to survive parsing and layout. The test renders
a representative of all eight diagram types with each of the 11 built-in
themes.

The existing deterministic stress suites generate 256 inputs for each of the
eight diagram types, for 2,048 generated inputs in total. Flowchart exercises
both Dagre and ELK for every generated source.

## Determinism And Performance Evidence

Every one of the 106 independent scenarios is rendered twice and compared as a
complete `MermaidScene`, including dimensions, elements, paths, text, styles,
metadata, and z-order.

The JVM production soak performs two warmup rounds followed by five measured
rounds over all 106 scenarios:

```text
renders=530
totalMs=8600
p95Ms=66
retainedHeapBytes=20624
```

Enforced budgets are 45 seconds total, 500ms P95, and 64MiB retained heap after
forced GC.

## Runtime Load Matrix

The shared load screen renders the 106 mixed scenarios in a `LazyColumn` and
walks from the first Flowchart to the final Pie chart.

| Platform | Result | Local evidence |
| --- | --- | --- |
| Android Emulator | Passed | 24s scroll; 216MiB peak PSS; 180MiB final PSS; final case reached |
| iOS Simulator | Passed | Automatic traversal reached final case; about 503MiB final host RSS; no crash |
| Desktop | Passed | Automatic traversal reached final case; about 428MiB process RSS |
| Web | Passed | 661ms first content; 5.77s scroll; 8.9MiB retained JS heap; no browser errors |

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
```

The capture command fails if a requested Native canvas or Official SVG does not
appear, if Mermaid reports an error, if an Official Gantt viewBox collapses, or
if a screenshot is below the minimum size. The geometry gate rejects blank
images and severe width, height, or foreground-density differences. The
contact-sheet generator verifies every expected pair and records its SHA-256.

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
