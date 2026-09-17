# Mermaid 12.0.0 Legacy Stable Test Report

> [!WARNING]
> This is a historical report for the previously implemented 12-family subset,
> not a current overall Stable decision. A Git Graph paint-order defect was
> visible in the 3,072-pair matrix but missed by the coarse geometry gate and
> earlier review. The old matrix-detail completion claim is withdrawn. See the
> [33-family roadmap](full-diagram-roadmap.md) for current status and the new
> detail-level promotion gate.

This report records the evidence behind CMP Mermaid's former **Stable** status
for the previously documented 12-family scope. The evidence is
deliberately separate from the demo gallery and distinguishes independent
production scenarios, systematic visual-matrix variants, and Native-only
randomized stress inputs.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Detail re-audit pending; historical Stable rating withdrawn** |
| Mermaid compatibility baseline | `12.0.0` |
| Independent production scenarios | 158: 62 release-candidate cases plus 96 additional conformance cases |
| Declared capability coverage | 207/207 points across 12 diagram types |
| Large-scale visual matrix | 3,072 unique Mermaid sources: 256 per diagram type |
| Native core render results | 158 independent plus 3,072 matrix cases passed, 0 failed |
| Web Native/Official captures | 6,144 matrix screenshots plus 316 independent-corpus screenshots, 0 render errors |
| Manual visual review | 160 replacement-gate sheets across 10 families; the other implemented families remain pending |
| Automated visual geometry | 3,072/3,072 matrix pairs and 158/158 independent pairs passed |
| Deterministic SceneGraph replay | 158 passed, 0 mismatches |
| Built-in theme matrix | 132/132 renders passed: 12 diagram types by 11 themes |
| Separate deterministic Native stress inputs | 3,072 |
| JVM tests | 376 passed, 0 failed |
| Core production soak | 790 renders; 416ms total; 1ms P95; 25,312 bytes retained heap |
| Runtime load matrix | Android Emulator, iOS Simulator, Desktop, and Web passed |
| Platform build matrix | Android debug/release, Web production, Desktop distributable, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared in debug or release APK |
| Public-source safety scan | No organization-specific endpoint or credential pattern found |

**Historical conclusion:** the 12 implemented families passed the listed
legacy gates. That evidence does not satisfy the new detail-level or 33-family
Stable criteria and must not be used as a current Stable decision.

## Current Replacement-Gate Progress

Flowchart, XY Chart, Quadrant Chart, Timeline, Kanban, Sequence, Class, State,
Entity Relationship, and Gantt are now the first ten families to complete the
replacement visual gate. Each has 256 unique same-source Native/Official
pairs, a 256/256 geometry result, and 16 paged contact sheets. The accepted
replacement total is 2,560/2,560 pairs across 160 manually reviewed sheets:
`2,500 pass / 60 manually reviewed / 0 fail`. All 60 reviews are ER
text-position threshold findings with complete text, no clipping or overlap,
and passing raster checks.

- Flowchart content ratios: width `1.026-1.119`, height `0.945-1.047`,
  foreground ink `0.948-1.241`.
- XY Chart content ratios: width `1.008-1.029`, height `0.995-1.041`,
  foreground ink `0.918-1.163`.
- Quadrant content ratios: width `1.007-1.035`, height `1.011-1.022`,
  foreground ink `1.024-1.048`.
- Timeline content ratios: width `1.026-1.053`, height `1.038-1.087`,
  foreground ink `1.052-1.136`.
- Kanban content ratios: width `1.041-1.071`, height `0.864-1.230`,
  foreground ink `0.996-1.110`.
- Sequence content ratios: width `1.023-1.068`, height `0.893-1.045`,
  foreground ink `0.926-1.148`.
- Class content ratios: width `1.030-1.208`, height `0.897-1.055`,
  foreground ink `0.781-1.227`.
- State content ratios: width `0.963-1.224`, height `0.968-1.089`,
  foreground ink `1.031-1.383`.
- Entity Relationship content ratios: width `1.033-1.078`, height
  `0.882-1.043`, foreground ink `0.545-1.150`.
- Gantt content ratios: width `1.035-1.037`, height `0.883-0.961`,
  foreground ink `0.964-1.023`.
- All 16 Flowchart contact sheets and all 256 same-source pairs were manually
  inspected after correcting Bang/Cloud edge intersection bounds. No
  unresolved marker, routing, label, clipping, overlap, or paint-order defect
  remains in that corpus.
- All 16 XY Chart contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's component insertion order and invalid
  SVG font-size fallback. No unresolved chart, axis, plot, legend, label,
  clipping, overlap, or paint-order defect remains in that corpus.
- All 16 Kanban contact sheets and all 256 same-source pairs were manually
  inspected, including the corrected three-line titles in cases 114 and 117.
- All 16 Sequence contact sheets and all 256 same-source pairs were manually
  inspected after translating Mermaid's control-title loop-width preflight and
  tightening created-participant manifest text bounds. No unresolved
  participant, lifecycle, message, marker, note, activation, control-frame,
  clipping, overlap, or paint-order defect remains.
- All 16 Class contact sheets and all 256 same-source pairs were manually
  inspected after matching Mermaid's asymmetric class text-group bbox, empty
  and method-only compartment spacing, note padding, and terminal placement.
  No unresolved class, namespace, relation, marker, cardinality, note, label,
  clipping, overlap, or paint-order defect remains.
- All 16 State contact sheets and all 256 same-source pairs were manually
  inspected after matching nested composite, note, concurrency, start/end,
  choice, fork/join, and transition routing behavior. No unresolved state,
  group boundary, marker, note, label, clipping, overlap, or paint-order defect
  remains.
- All 16 Entity Relationship contact sheets and all 256 same-source pairs were
  manually inspected after translating Mermaid's recursive subgraph spacing.
  The 60 queued cases differ only in isolated label positions on repeated large
  structures; their P95 normalized text-center distance is at most `0.077`,
  mask IoU is at least `0.685`, and edge F1 is at least `0.922`. No unresolved
  entity, attribute, subgraph, relationship, marker, text-loss, clipping,
  overlap, or paint-order defect remains.
- All 16 Gantt contact sheets and all 256 same-source pairs were manually
  inspected at the shared `1200 x 900` viewport required by Mermaid's
  parent-width renderer input. No unresolved task ordering, section band,
  milestone, exclusion, axis, marker, label, clipping, overlap, or paint-order
  defect remains.
- Every Timeline sheet and representative first, middle, and final Quadrant
  sheets were manually inspected.

This is a per-family result. Overall status remains Not Stable until the other
implemented families complete the replacement audit and all 33 official
families are translated.

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

1. **158 independent production scenarios.** These are hand-authored,
   production-like structures used for capability coverage, deterministic
   replay, manual review, performance soak, and the regular Quality Gate.
2. **3,072 Native/Official visual-matrix cases.** Each diagram type contributes
   256 unique Mermaid sources, derived deterministically from 13 or 14 complex
   structural seeds and 20 visible text/layout-pressure profiles. This is not a
   claim of 256 unrelated topologies per type.
3. **3,072 Native-only randomized stress inputs.** These separately exercise
   parser and layout robustness. They are not presented as Mermaid.js parity
   evidence.

## Independent Production Corpus

The canonical corpus is
[`tools/official-reference/production-corpus.mjs`](../tools/official-reference/production-corpus.mjs).
It includes 62 release-candidate cases plus 96 conformance cases that are
also independent from the 314-item demo gallery. Generated Kotlin copies are
consumed independently by core tests and the Web audit screen. The original
cases retain their legacy `rc_` IDs for evidence continuity; additional cases
use `prod_`. Neither set can be resolved through the normal demo gallery.

| Diagram | Production cases | Capability points | Scenario examples | Manual visual result |
| --- | ---: | ---: | --- | --- |
| Flowchart | 14 | 16/16 | orchestration, edge semantics, advanced shapes, nested domains | Replacement detail pass; 14/14 production and 256/256 matrix; 16/16 sheets manually reviewed |
| XY Chart | 13 | 16/16 | latency, categorical and numeric axes, horizontal labels, mixed plots | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Sequence | 14 | 16/16 | checkout saga, lifecycle, self messages, parallel and critical regions | Replacement detail pass; 14/14 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Class | 13 | 16/16 | commerce, namespaces, generics, relations, annotations | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| State | 13 | 16/16 | fulfillment, nested composites, fork/join, concurrency, notes | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Entity Relationship | 13 | 16/16 | commerce, aliases, attributes, cardinalities, nested subgraphs | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Gantt | 13 | 16/16 | release plans, date units, exclusions, top axes, vertical markers | Replacement detail pass; 13/13 production and 256/256 matrix; 16/16 sheets manually reviewed |
| Pie | 13 | 16/16 | cost, escaped labels, donut, legends, themes, many slices | Historical review; detail re-audit pending |
| User Journey | 13 | 16/16 | sections, scores, actor order, metadata, configuration, long text | Historical review; detail re-audit pending |
| Requirement | 13 | 17/17 | typed requirements, elements, relationships, directions, styling, metadata | Historical review; detail re-audit pending |
| Git Graph | 13 | 24/24 | commits, branches, merges, cherry-picks, orientations, configuration, themes | Historical review; detail re-audit pending |
| Mindmap | 13 | 22/22 | hierarchy, shapes, text, CoSE-Bilkent, Dagre, tidy tree, configuration, themes | Historical review; detail re-audit pending |

Rows still marked "Historical review" preserve the former review record only.
They do not satisfy the new manifest and perceptual checks and therefore are
not current acceptance decisions.

## Large-Scale Visual Matrix

The large-scale matrix contains 256 unique sources for each of Flowchart,
XY Chart, Sequence, Class, State, Entity Relationship, Gantt, Pie, User
Journey, Requirement, Git Graph, and Mindmap:

- 3,072 unique Mermaid sources;
- 3,072 CMP Native screenshots;
- 3,072 Mermaid.js `12.0.0` screenshots;
- 192 paged contact sheets, with 16 same-source pairs per page;
- source, seed, profile, feature, screenshot, and SHA-256 metadata;
- per-pair content bounds and foreground-density metrics.

**[Open all 192 paged Native/Official comparison images](assets/stability-report/visual-parity-evidence.md).**

Machine-readable evidence:
[manifest](assets/stability-report/visual-parity-manifest.json) and
[geometry report](assets/stability-report/visual-parity-geometry.json).
The automated gate accepted all 3,072 pairs. Across the complete matrix,
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

The replacement audit passed all 14 production scenarios and all 256 matrix
pairs with no review queue. The matrix width, height, and foreground-ink ratios
were `1.026-1.119`, `0.945-1.047`, and `0.948-1.241`. All 16 matrix contact
sheets were manually reviewed. During that review, hidden arrows on Bang and
Cloud nodes exposed a mismatch between sampled path bounds and edge
intersection bounds. The translated shapes now follow Mermaid's
`updateNodeBounds` plus `intersect.rect` behavior, and the detail auditor now
requires marker endpoint anchors and compares their later-paint occlusion
depth, so marker metadata alone cannot satisfy the gate.

</details>

<details open>
<summary><strong>XY Chart: 13 production scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.016-1.020`, `1.012-1.015`, and `0.967-1.144`; matrix ratios were
`1.008-1.029`, `0.995-1.041`, and `0.918-1.163`. All 16 matrix contact sheets
were manually reviewed. The final translation preserves Mermaid's component
insertion order and its browser fallback from an invalid negative bar-label
font size to inherited `16px`.

</details>

<details open>
<summary><strong>Sequence: 14 production scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The replacement audit passed all 14 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.019-1.254`, `0.910-1.111`, and `0.939-1.327`; matrix ratios were
`1.023-1.068`, `0.893-1.045`, and `0.926-1.148`. All 16 matrix contact sheets
were manually reviewed. The final translation follows Mermaid's
`calculateLoopBounds`, `adjustLoopHeightForWrap`, and `wrapLabel` behavior for
control titles, preserves marker and activation paint order, and reports
created-participant text at its measured glyph bounds.

</details>

<details open>
<summary><strong>Class: 13 production scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `1.036-1.196`, `0.906-1.055`, and `0.723-1.230`; matrix ratios were
`1.030-1.208`, `0.897-1.055`, and `0.781-1.227`. All 16 matrix contact sheets
were manually reviewed. The final translation follows Mermaid's asymmetric
`textHelper` group bounds for class width, exact empty and method-only
compartment spacing, note padding, and marker-aware terminal placement.

</details>

<details>
<summary><strong>State: 13 production scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with no review queue. Production width, height, and foreground-ink
ratios were `0.995-1.206`, `1.030-1.093`, and `1.066-1.408`; matrix ratios
were `0.963-1.224`, `0.968-1.089`, and `1.031-1.383`. All 16 matrix contact
sheets were manually reviewed. The final translation preserves scoped
start/end markers, nested composite and concurrency boundaries, note
placement, pseudostates, and marker-aware transition routing.

</details>

<details open>
<summary><strong>Entity Relationship: 13 production scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

The replacement audit accepted all 13 production scenarios and all 256 matrix
pairs with no failures. Production detail results were
`10 pass / 3 manually reviewed / 0 fail`; matrix results were
`196 pass / 60 manually reviewed / 0 fail`. All reviews were isolated
text-position threshold findings on large repeated structures, while text
presence, clipping, overlap, paint order, marker checks, and raster checks
passed. Production width, height, and foreground-ink ratios were
`1.033-1.231`, `0.892-1.085`, and `0.519-1.373`; matrix ratios were
`1.033-1.078`, `0.882-1.043`, and `0.545-1.150`. All 16 matrix contact sheets
were manually reviewed. The final translation propagates each parent Dagre
graph's `nodesep` and `ranksep + 25` into recursively extracted subgraphs,
matching Mermaid.js `12.0.0` `measureDagreGraph`.

</details>

<details open>
<summary><strong>Gantt: 13 production scenarios</strong></summary>

![Gantt complex Native and Official corpus](assets/stability-report/gantt-complex-corpus.png)

The replacement audit passed all 13 production scenarios and all 256 matrix
pairs with `0 review / 0 fail`. Production width, height, and foreground-ink
ratios were `1.035-1.037`, `0.883-0.961`, and `1.003-1.027`; matrix ratios
were `1.035-1.037`, `0.883-0.961`, and `0.964-1.023`. All 16 matrix contact
sheets were manually reviewed. The formal gate uses a shared `1200 x 900`
viewport because Mermaid derives Gantt width from
`elem.parentElement.offsetWidth` and uses `1200` only when that width is
unavailable; the Kotlin scene uses the same `1200` default in the absence of
a browser parent. Multi-unit ticks preserve D3 `interval.every(count)` epoch
and calendar-field anchoring.

</details>

<details>
<summary><strong>Pie: 13 production scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

</details>

<details open>
<summary><strong>User Journey: 13 production scenarios</strong></summary>

![User Journey complex Native and Official corpus](assets/stability-report/journey-complex-corpus.png)

The historical review recorded all 256 Journey pairs as acceptable. That
conclusion is pending the new detail re-audit. The legacy geometry ratios were
`1.020-1.032` for width, `1.031-1.050` for height, and `0.999-1.080` for
foreground ink.

</details>

<details open>
<summary><strong>Requirement: 13 production scenarios</strong></summary>

![Requirement complex Native and Official corpus](assets/stability-report/requirement-complex-corpus.png)

The historical review recorded all 256 Requirement pairs as acceptable. That
conclusion is pending the new detail re-audit. The legacy geometry ratios were
`1.019-1.072` for width, `1.004-1.053` for height, and `0.975-1.148` for
foreground ink.

</details>

<details open>
<summary><strong>Git Graph: 13 production scenarios</strong></summary>

![Git Graph complex Native and Official corpus](assets/stability-report/gitgraph-complex-corpus.png)

The historical review recorded all 256 Git Graph pairs as acceptable, but
`parity_gitgraph_005` visibly contradicted that conclusion. The legacy geometry
ratios were `1.036-1.154` for width, `1.032-1.137` for height, and
`1.044-1.427` for foreground ink.

</details>

<details open>
<summary><strong>Mindmap: 13 production scenarios</strong></summary>

![Mindmap complex Native and Official corpus](assets/stability-report/mindmap-complex-corpus.png)

The historical review recorded all 256 Mindmap pairs as acceptable across
CoSE-Bilkent, Dagre, and tidy-tree inputs. That conclusion is pending the new
detail re-audit. Icons and arbitrary CSS classes remain explicit unsupported
boundaries.

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
and credential scan, production runtime-isolation check, debug/release APK
permission audit, 394-image capture, visual geometry gate, and Web load test
on every push to `main` and every pull request. The
[`Full Visual Parity`](../.github/workflows/full-visual-parity.yml) workflow
runs the 3,840-pair matrix weekly and on demand in 15 parallel diagram jobs.

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
mermaid-core: 417 tests
mermaid-compose: 22 tests
total: 439 tests
failures: 0
errors: 0
```

The current independent corpus test is
[`ProductionCorpusTest`](../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/ProductionCorpusTest.kt).
It compiles all 197 sources and rejects parser errors, invalid or non-finite
geometry, scenes outside the 20,000-unit and 20,000-element limits, empty
SceneGraphs, and non-deterministic replay. The 120 conformance cases also
require selected semantic text to survive parsing and layout. The test renders
a representative of all 15 implemented diagram types with each of the 11 built-in
themes.

`ProductionCorpusTest` additionally renders all 3,840 visual-matrix sources,
checks the expected visible text, and rejects empty, invalid, or non-finite
SceneGraphs. The separate deterministic stress suites generate another 256
Native-only inputs for each of the 15 implemented diagram types, for 3,840
stress inputs in total. Unified graph diagrams exercise Dagre, while Mindmap
exercises CoSE-Bilkent, Dagre, and tidy tree.

## Determinism And Performance Evidence

Every one of the 197 independent scenarios is rendered twice and compared as a
complete `MermaidScene`, including dimensions, elements, paths, text, styles,
metadata, and z-order.

The JVM production soak performs two warmup rounds followed by five measured
rounds over all 197 scenarios:

```text
renders=985
totalMs=419
p95Ms=1
retainedHeapBytes=29144
```

Enforced budgets are 45 seconds total, 500ms P95, and 64MiB retained heap after
forced GC.

## Runtime Load Matrix

The shared load screen renders the 158 mixed scenarios in a `LazyColumn` and
walks from the first Flowchart to the final Mindmap case.

| Platform | Result | Local evidence |
| --- | --- | --- |
| Android Emulator | Passed | 30s scroll; 190MiB peak PSS; 154MiB final PSS; final case reached |
| iOS Simulator | Passed | Completion marker at 17s; 321MiB final host RSS; no crash |
| Desktop | Passed | Completion marker in three consecutive runs; latest 15s and 418MiB RSS |
| Web | Passed | 0.74s first content; 7.71s scroll; 9.4MiB retained JS heap; no browser errors |

Machine-readable measurements:
[Android](assets/runtime-load/android-emulator-metrics.json),
[iOS](assets/runtime-load/ios-simulator-metrics.json),
[Desktop](assets/runtime-load/desktop-metrics.json), and
[Web](assets/runtime-load/web-metrics.json).

Android, iOS, and Web screenshots show the final corpus case after traversing
the same mixed list:

| Android Emulator | iOS Simulator |
| :---: | :---: |
| <img src="assets/runtime-load/android-emulator-bottom.png" alt="Android load test final case" width="360"> | <img src="assets/runtime-load/ios-simulator-bottom.png" alt="iOS load test final case" width="360"> |

| Web |
| :---: |
| <img src="assets/runtime-load/web-bottom.png" alt="Web load test final case" width="700"> |

The current Desktop run is represented by its machine-readable process,
window, timing, and RSS record. macOS screen-recording permission prevented a
current screenshot, so the older 132-case Desktop image is intentionally not
used as Git Graph evidence.

The iOS and Desktop runs identified and fixed the same class of native font
concurrency defect: background text measurement could race Compose/Skia glyph
drawing and crash in CoreText. iOS now uses `Dispatchers.Main.immediate`;
Desktop uses an explicit Swing EDT dispatcher. Android and Web retain
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
OUTPUT_FILE=captures/local/visual-parity/detail-audit.json \
HEATMAP_DIR=captures/local/visual-parity/detail-diffs \
npm run audit:detail

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
Set `AUDIT_KIND` and `CORPUS_KIND` to one of the 15 diagram IDs to reproduce
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

These criteria are not currently satisfied. The semantic, paint-order, and
perceptual re-audit is in progress, and 18 official Mermaid families are not
yet implemented. The public code status therefore remains **Not Stable** until
the per-family gates in
[`full-diagram-roadmap.md`](full-diagram-roadmap.md) pass.
