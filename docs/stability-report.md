# Mermaid 12.0.0 Release Candidate Test Report

This report records the evidence behind CMP Mermaid's current
**Release Candidate** status. It is deliberately separate from the demo
gallery: every visual case below comes from an independent complex scenario
corpus that is not shown as a product demo.

## Decision

| Item | Result |
| --- | --- |
| Current rating | **Release Candidate** |
| Mermaid compatibility baseline | `12.0.0` |
| Independent complex scenarios | 42 |
| Native core render results | 42 passed, 0 failed |
| Web Native/Official captures | 84 generated, 0 render errors |
| Manual visual review | 37 acceptable, 5 Gantt cases blocked |
| Deterministic generated stress inputs | 2,048 |
| JVM tests | 270 passed, 0 failed |
| Platform build matrix | Android, Web, Desktop, iOS Arm64, iOS Simulator Arm64, iOS X64 passed |
| Android Internet permission | Not declared |

**Conclusion:** the corpus provides strong evidence for Flowchart, XY Chart,
Sequence, Class, State, Entity Relationship, and Pie compatibility. It also
shows that the project must remain a Release Candidate: all five complex Gantt
cases have a substantial horizontal geometry difference from Mermaid.js in the
fixed audit viewport. That gap must be resolved before the eight-diagram set can
be promoted to Stable.

## What This Report Does And Does Not Prove

This report proves that the exact source-controlled corpus:

- compiles through the Kotlin parser, database, layout, and SceneGraph pipeline;
- produces non-empty Native Canvas output;
- is accepted and rendered by the pinned Mermaid.js `12.0.0` reference;
- has been reviewed side by side at the same `1200 x 900` viewport;
- can be regenerated from the repository scripts.

It does **not** prove that every possible legal Mermaid program is supported.
The visual verdict is currently a human review, not an automated perceptual or
semantic image score. Platform font metrics and text wrapping may differ.
Production soak time and a full iOS/Desktop screenshot matrix are also still
missing.

## Independent Complex Corpus

The canonical corpus is
[`tools/official-reference/stability-corpus.mjs`](../tools/official-reference/stability-corpus.mjs).
Generated Kotlin copies are consumed independently by the core tests and the
Web audit screen. Case IDs use the `rc_` prefix and cannot be resolved through
the normal demo gallery.

| Diagram | Complex cases | Scenario examples | Manual visual result |
| --- | ---: | --- | --- |
| Flowchart | 6 | checkout compensation, regional failover, release rollback, incident response | Acceptable |
| XY Chart | 5 | latency percentiles, conversion, capacity, backlog, error budget | Acceptable |
| Sequence | 6 | checkout saga, OAuth race, multipart upload, retry delivery, offline sync | Acceptable; autonumber/activation layering rechecked |
| Class | 5 | commerce, workflow engine, authorization, notifications, editor model | Acceptable |
| State | 5 | fulfillment, payment, rollout, media processing, support lifecycle | Acceptable |
| Entity Relationship | 5 | commerce, learning, messaging, billing, warehouse inventory | Acceptable |
| Gantt | 5 | mobile release, database migration, regional launch, incident hardening | **Blocked** |
| Pie | 5 | cloud cost, acquisition, incidents, subscriptions, storage | Acceptable |

`Acceptable` means the manual review found no missing domain entity, state,
message, series, relationship, or route that changes the meaning of the
diagram. It allows minor font, spacing, and edge-routing differences.

`Blocked` means the comparison is not good enough to support a Stable claim.
It is not counted as a pass.

## Visual Evidence

Each contact sheet uses the same Mermaid source on both sides:

- left: CMP Native, rendered through Kotlin and Compose Canvas;
- right: Mermaid.js `12.0.0`, loaded from the repository's pinned local asset.

The source IDs and scenario descriptions are printed above every pair.

<details open>
<summary><strong>Flowchart: 6 complex scenarios</strong></summary>

![Flowchart complex Native and Official corpus](assets/stability-report/flowchart-complex-corpus.png)

</details>

<details open>
<summary><strong>XY Chart: 5 complex scenarios</strong></summary>

![XY Chart complex Native and Official corpus](assets/stability-report/xychart-complex-corpus.png)

</details>

<details>
<summary><strong>Sequence: 6 complex scenarios</strong></summary>

![Sequence complex Native and Official corpus](assets/stability-report/sequence-complex-corpus.png)

The checkout saga was re-audited at full capture resolution. Sequence numbers
now use Mermaid `12.0.0` activation bounds, the official `6px` marker radius,
message-line clearance, and the upstream draw order that keeps number markers
above activation bars.

</details>

<details>
<summary><strong>Class: 5 complex scenarios</strong></summary>

![Class complex Native and Official corpus](assets/stability-report/class-complex-corpus.png)

</details>

<details>
<summary><strong>State: 5 complex scenarios</strong></summary>

![State complex Native and Official corpus](assets/stability-report/state-complex-corpus.png)

</details>

<details>
<summary><strong>Entity Relationship: 5 complex scenarios</strong></summary>

![Entity Relationship complex Native and Official corpus](assets/stability-report/er-complex-corpus.png)

</details>

<details open>
<summary><strong>Gantt: 5 blocked complex scenarios</strong></summary>

![Gantt complex Native and Official corpus with open geometry differences](assets/stability-report/gantt-complex-corpus.png)

The Native output uses the available horizontal timeline while Mermaid.js is
compressed into a narrow central region in the same fixed viewport. Whether
the remaining defect belongs to the Gantt layout translation or the Official
comparison framing must be isolated before this category can pass.

</details>

<details>
<summary><strong>Pie: 5 complex scenarios</strong></summary>

![Pie complex Native and Official corpus](assets/stability-report/pie-complex-corpus.png)

</details>

The generated capture metadata, byte sizes, and SHA-256 values are available in
[`manifest.json`](assets/stability-report/manifest.json).

## Automated Test Evidence

The repository-level
[`Quality Gate`](../.github/workflows/quality.yml) repeats the JVM tests,
cross-platform builds, generated-corpus check, and APK permission audit on
every push to `main` and every pull request.

The full verification command completed successfully:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  :sample:androidApp:assembleDebug \
  :sample:webApp:wasmJsBrowserDistribution \
  :sample:desktopApp:assemble \
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
mermaid-core: 253 tests
mermaid-compose: 17 tests
total: 270 tests
failures: 0
errors: 0
```

The independent corpus test is
[`StabilityCorpusTest`](../mermaid-core/src/commonTest/kotlin/io/github/cmpmermaid/core/StabilityCorpusTest.kt).
It compiles all 42 sources and rejects parser errors, invalid scene dimensions,
or empty SceneGraphs.

The existing deterministic stress suites generate 256 inputs for each of the
eight diagram types, for 2,048 generated inputs in total. Flowchart exercises
both Dagre and ELK for every generated source.

## APK Permission Audit

Audited artifact:

```text
sample/androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Declared permissions:

```text
io.github.cmpmermaid.sample.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
```

`android.permission.INTERNET` is not declared.

## Reproduce The Report

```bash
cd tools/official-reference
npm run generate:stability-corpus

cd ../..
JAVA_HOME=$(/usr/libexec/java_home -v 17) \
  ./gradlew :mermaid-core:jvmTest :sample:webApp:wasmJsBrowserDistribution

python3 -m http.server 8093 \
  --directory sample/webApp/build/dist/wasmJs/productionExecutable

cd tools/official-reference
AUDIT_SOURCE=stability \
OUTPUT_DIR=captures/local/rc-stability-corpus \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit

npm run generate:stability-contact-sheets
```

The capture command fails if a requested Native canvas or Official SVG does not
appear, if Mermaid reports an error, or if a screenshot is below the minimum
size. The contact-sheet generator verifies that every expected pair exists and
records its SHA-256.

## Promotion Gate For Stable

The project should only restore the Stable label when all of these are true:

- the independent complex corpus has no blocked visual category;
- Native/Official comparison includes automated semantic or perceptual
  thresholds with reviewed exceptions;
- the Quality Gate workflow is required and green on `main`;
- Android, Web, iOS, and Desktop have runtime smoke evidence, not compile-only
  evidence;
- bulk rendering has repeatable memory, latency, and long-running soak limits;
- no open high-severity correctness defect exists for the supported contract;
- a Release Candidate has been exercised by at least one real integration.

Until then, the public status remains **Release Candidate**.
