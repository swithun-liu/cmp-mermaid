<div align="center">
  <h1>CMP Mermaid</h1>
  <p><strong>Stable native Mermaid rendering for Kotlin and Compose Multiplatform.</strong></p>
  <p>Mermaid <code>12.0.0</code> semantics translated to Kotlin and rendered with Compose Canvas.</p>
  <p>
    <a href="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml">
      <img src="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml/badge.svg?branch=main" alt="Quality Gate">
    </a>
    <a href="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/deploy-pages.yml">
      <img src="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/deploy-pages.yml/badge.svg?branch=main" alt="Web Demo">
    </a>
    <a href="https://mermaid.js.org/">
      <img src="https://img.shields.io/badge/Mermaid-12.0.0-ff3670" alt="Mermaid 12.0.0">
    </a>
    <a href="LICENSE">
      <img src="https://img.shields.io/badge/license-MIT-2f855a" alt="MIT License">
    </a>
  </p>
  <p>
    <a href="https://swithun-liu.github.io/cmp-mermaid/"><strong>Live Web demo</strong></a>
    ·
    <a href="docs/stability-report.md"><strong>Stable test report</strong></a>
    ·
    <a href="docs/production-capability-matrix.md">Capability matrix</a>
    ·
    <a href="#integration">Integration</a>
  </p>
</div>

> [!IMPORTANT]
> **CMP Mermaid is Stable for its documented Mermaid `12.0.0` support
> scope.** All 8 supported diagram types pass the independent production
> corpus, a 2,048-case Native/Official visual matrix, deterministic replay,
> separate randomized stress tests, runtime load tests, and cross-platform
> builds.
>
> Start with the **[full Stable test report](docs/stability-report.md)**.
> It contains the cases, Native/Official contact sheets, performance numbers,
> runtime screenshots, machine-readable metrics, and reproduction commands.

CMP Mermaid is designed for applications that render many diagrams without a
WebView per diagram. The production path does not embed Mermaid.js and does not
require `android.permission.INTERNET`: parsing, diagram state, layout
preparation, SceneGraph generation, and final Compose Canvas painting are
owned by the multiplatform libraries.

## Verified Stable

Stable is based on repository-controlled test evidence, not on the size of the
demo gallery.

| Evidence | Result |
| --- | ---: |
| Supported diagram types | 8 |
| Independent production scenarios | 106 |
| Declared capability coverage | 128/128 |
| Large-scale visual matrix | 2,048 unique sources: 256 per diagram type |
| Native/Official captures | 4,096 matrix screenshots plus 212 independent-corpus screenshots |
| Manual visual review | 106 accepted, 0 blocked |
| Automated visual geometry | 2,048/2,048 matrix pairs and 106/106 independent pairs passed |
| Deterministic SceneGraph replay | 106 passed, 0 mismatches |
| Built-in theme matrix | 88/88 |
| Separate generated Native stress inputs | 2,048 |
| JVM tests | 283 passed, 0 failed |
| Core production soak | 530 renders, 66ms P95 |
| Runtime load matrix | Android, iOS, Desktop, Web passed |

| Evidence document | What it contains |
| --- | --- |
| **[Stable test report](docs/stability-report.md)** | Decision, visual contact sheets, tests, soak metrics, runtime load evidence, and reproduction steps |
| **[All 2,048 Native/Official pairs](docs/assets/stability-report/visual-parity-evidence.md)** | 128 paged contact sheets, with 16 same-source pairs per page |
| [Production capability matrix](docs/production-capability-matrix.md) | The 128 independently exercised capabilities |
| [Production readiness](docs/production-readiness.md) | Code-level Stable criteria, resource budgets, and integration guidance |
| [Quality Gate](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml) | Current automated JVM, build, publication, security, APK, visual, and Web load results |
| [Full Visual Parity](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/full-visual-parity.yml) | Weekly/manual 8-way matrix capture, geometry enforcement, and downloadable evidence |

The Stable rating applies to the documented support contract. It is not a
claim that every legal Mermaid program is implemented. A legal feature that
cannot be represented faithfully returns `MermaidError.UnsupportedFeature`
instead of silently drawing a misleading approximation.

## Native Vs Mermaid.js

The comparisons below use the same Mermaid source, theme, layout mode, and
fixed viewport. The goal is equivalent meaning and comparable visual quality,
not pixel-identical browser output; platform font metrics may differ.

**Flowchart: multi-region failover**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-flowchart-native.png" alt="CMP Native multi-region failover flowchart" width="700"> | <img src="docs/assets/parity-flowchart-official.png" alt="Official Mermaid.js multi-region failover flowchart" width="700"> |

<details>
<summary><strong>More same-source comparisons</strong></summary>

**XY Chart: mixed bar and line series**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-xy-native.png" alt="CMP Native XY chart" width="700"> | <img src="docs/assets/parity-xy-official.png" alt="Official Mermaid.js XY chart" width="700"> |

**State Diagram: labels, loops, branches, and terminal states**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-state-native.png" alt="CMP Native state diagram" width="700"> | <img src="docs/assets/parity-state-official.png" alt="Official Mermaid.js state diagram" width="700"> |

The report contains the 106 independent production comparisons and a separate
large-scale matrix with 2,048 unique Mermaid sources:
**[open all 128 visual evidence pages](docs/assets/stability-report/visual-parity-evidence.md)**.

</details>

## Supported Diagrams

| Diagram | Status | Production cases | Main coverage | Details |
| --- | :---: | ---: | --- | --- |
| Flowchart | **Stable** | 14 | Jison/FlowDB, Dagre, ELK, shapes, links, Markdown/HTML labels | [Compatibility](docs/flowchart-compatibility.md) |
| XY Chart | **Stable** | 13 | Jison/XY DB, D3 scales and ticks, bar/line plots, labels | [Compatibility](docs/xychart-compatibility.md) |
| Sequence | **Stable** | 14 | Actors, 26 message forms, notes, activations, control regions | [Compatibility](docs/sequence-compatibility.md) |
| Class | **Stable** | 13 | Compartments, generics, namespaces, relations, ELK/Dagre | [Compatibility](docs/class-compatibility.md) |
| State | **Stable** | 13 | Composite states, concurrency, notes, forks/joins, ELK/Dagre | [Compatibility](docs/state-compatibility.md) |
| Entity Relationship | **Stable** | 13 | Attributes, cardinalities, relationships, nested subgraphs | [Compatibility](docs/er-compatibility.md) |
| Gantt | **Stable** | 13 | Dates, dependencies, exclusions, milestones, D3-style ticks | [Compatibility](docs/gantt-compatibility.md) |
| Pie | **Stable** | 13 | Langium grammar, D3 angles, donut, legends, palettes | [Compatibility](docs/pie-compatibility.md) |

All 8 types support Mermaid frontmatter, metadata, Unicode, and the relevant
theme variables within their documented compatibility boundaries.

## Try It

Open the **[live Kotlin/Wasm demo](https://swithun-liu.github.io/cmp-mermaid/)**
to browse syntax, render the galleries, switch all 11 themes, and compare CMP
Native output with the pinned Mermaid.js reference. The Flowchart Playground
accepts editable source and can switch between ELK and Dagre.

<a href="https://swithun-liu.github.io/cmp-mermaid/">
  <img src="docs/assets/web-playground.png" alt="CMP Mermaid Kotlin Wasm Playground" width="900">
</a>

The official comparison renderer belongs to `mermaid-debug-ui` only.
`mermaid-core` and `mermaid-compose` never use Mermaid.js.

## Architecture

```text
Mermaid source
    -> Kotlin preprocessor and translated parser
    -> translated diagram database and layout preparation
    -> platform-independent MermaidScene
    -> Compose Canvas
```

- `mermaid-core` owns parsing, diagram state, layout adapters, typed errors,
  themes, and the platform-independent SceneGraph.
- `mermaid-compose` owns Canvas painting, text measurement, assets,
  interactions, and pan/zoom behavior.
- `mermaid-debug-ui` owns documentation, galleries, Playground, official
  comparison, and load-test screens. It is optional and should remain outside
  production release variants.
- `sample/*` contains thin Android, iOS, Desktop, and Web launchers around the
  shared debug UI.

The ELK path uses Mermaid's adapter around a pinned `elkjs@0.9.3` worker.
Android and Desktop isolate it in QuickJS, iOS uses JavaScriptCore, and Web
uses the browser runtime. This is a layout implementation detail; Mermaid.js
is not part of production rendering.

## Integration

The stable publication coordinates are:

| Module | Maven coordinate |
| --- | --- |
| Core renderer | `com.swithun:mermaid-core:0.1.0` |
| Compose renderer | `com.swithun:mermaid-compose:0.1.0` |
| Debug and comparison UI | `com.swithun:mermaid-debug-ui:0.1.0` |

```kotlin
dependencies {
    implementation("com.swithun:mermaid-compose:0.1.0")
    debugImplementation("com.swithun:mermaid-debug-ui:0.1.0")
}
```

> [!NOTE]
> The coordinates and POMs are ready, but the first public artifact repository
> release has not been uploaded yet. Until then, consume the repository modules
> directly or publish them to a local/internal Maven repository.

For a source checkout:

```kotlin
dependencies {
    implementation(project(":mermaid-compose"))
    debugImplementation(project(":mermaid-debug-ui"))
}
```

Basic Compose usage:

```kotlin
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swithun.cmpmermaid.compose.MermaidDiagram
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.MermaidThemePreset

@Composable
fun Diagram(source: String) {
    MermaidDiagram(
        source = source,
        modifier = Modifier.fillMaxWidth(),
        theme = MermaidTheme.preset(MermaidThemePreset.Default),
        contentDescription = "Mermaid diagram",
    )
}
```

Generate all KMP publications under `build/maven-repository`:

```bash
./gradlew \
  :mermaid-core:publishAllPublicationsToBuildRepository \
  :mermaid-compose:publishAllPublicationsToBuildRepository \
  :mermaid-debug-ui:publishAllPublicationsToBuildRepository
```

## Themes

All 11 Mermaid `12.0.0` presets are included:
`default`, `dark`, `forest`, `neutral`, `base`, `neo`, `neo-dark`, `redux`,
`redux-color`, `redux-dark`, and `redux-dark-color`.

Business themes can start from a preset with Kotlin `copy`, or consume
Mermaid-compatible `themeVariables` through `MermaidTheme.withVariables(...)`.
Invalid external values are returned as `GMResult.Err`.

```kotlin
val brandTheme = MermaidTheme.preset(MermaidThemePreset.ReduxColor).copy(
    background = SceneColor(0xFF101820),
    nodeFill = SceneColor(0xFFF2AA4C),
    nodeText = SceneColor(0xFF101820),
    edge = SceneColor(0xFFF2AA4C),
)
```

Arbitrary `themeCSS` depends on browser DOM/CSS semantics and returns
`MermaidError.UnsupportedFeature`. Portable styling uses typed Kotlin theme
objects and `MermaidFontFamilyResolver`.

## Following Mermaid Releases

CMP Mermaid follows a source-mapped translation workflow rather than
reimplementing behavior from screenshots:

1. Pin Mermaid, Jison, D3, Marked, Dagre, and ELK versions.
2. Map every Kotlin parser, DB, layout, shape, theme, and renderer boundary to
   its upstream Mermaid source.
3. Generate parser tables, rules, entities, fixtures, and workers from pinned
   inputs.
4. Translate only the upstream delta for a Mermaid upgrade.
5. Re-run the complete parity and platform gates.

Source maps:
[Flowchart](docs/upstream-flowchart-map.md) ·
[XY Chart](docs/upstream-xychart-map.md) ·
[Sequence](docs/upstream-sequence-map.md) ·
[Class](docs/upstream-class-map.md) ·
[State](docs/upstream-state-map.md) ·
[ER](docs/upstream-er-map.md) ·
[Gantt](docs/upstream-gantt-map.md) ·
[Pie](docs/upstream-pie-map.md)

## Verification

Run the JVM and publication gates:

```bash
./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  verifyPublicationCoordinates
```

Run the samples:

```bash
./gradlew :sample:androidApp:installDebug
./gradlew :sample:desktopApp:run
./gradlew :sample:webApp:wasmJsBrowserDevelopmentRun
```

The **[Stable test report](docs/stability-report.md#reproduce-the-report)**
contains the complete cross-platform build and Native/Official visual
reproduction commands.

## License

CMP Mermaid is released under the [MIT License](LICENSE).
Translated Mermaid behavior and development-only reference assets retain their
upstream notices in [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
