<div align="center">
  <h1>CMP Mermaid</h1>
  <p><a href="README.md">English</a> · <strong>简体中文</strong></p>
  <p><strong>面向 Kotlin 与 Compose Multiplatform 的原生 Mermaid 渲染器。</strong></p>
  <p>将 Mermaid <code>12.0.0</code> 语义翻译为 Kotlin，并通过 Compose Canvas 渲染。</p>
  <p>
    <a href="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml">
      <img src="https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml/badge.svg?branch=main" alt="质量门禁">
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
    <a href="https://swithun-liu.github.io/cmp-mermaid/"><strong>在线 Web Demo</strong></a>
    ·
    <a href="docs/assets/stability-report/visual-parity-evidence.md"><strong>4,096 案例视觉报告</strong></a>
    ·
    <a href="docs/stability-report.md">完整 Stable 报告</a>
    ·
    <a href="docs/full-diagram-roadmap.md">33 类图路线</a>
    ·
    <a href="#接入">接入</a>
  </p>
</div>

> [!IMPORTANT]
> **CMP Mermaid 当前实现了 Mermaid `12.0.0` 官方 33 个图表家族中的
> 16 个。** 剩余 17 个家族见
> **[全图表路线](docs/full-diagram-roadmap.md)**。
>
> 当前 **[4,096 案例 Native/Official 视觉报告](docs/assets/stability-report/visual-parity-evidence.md)**
> 已覆盖全部 16 个已实现家族。Flowchart、XY Chart、Quadrant Chart、Timeline、
> Kanban、Sequence、Class、State、Entity Relationship、Gantt、Pie、User Journey、
> Requirement、Git Graph、Mindmap 与 Packet 已完成新的 256 案例细节门禁。由于仍有
> 17 个官方家族待翻译，该报告不代表 Mermaid 已完整兼容。

CMP Mermaid 面向需要在同一页面渲染多个图表的应用，无需为每个图表创建
WebView。生产渲染链路不内嵌 Mermaid.js、不包含网络客户端，也不需要
`android.permission.INTERNET`：解析、图表状态、布局准备、SceneGraph
生成和最终 Compose Canvas 绘制均由多平台库完成。

## 当前验证状态

已实现的 16 个家族仍有仓库内可复现的测试与截图。Flowchart、XY Chart、Quadrant Chart、
Timeline、Kanban、Sequence、Class、State、Entity Relationship、Gantt、Pie、User Journey、
Requirement、Git Graph、Mindmap 与 Packet 已通过新的细节门禁。

| 证据 | 结果 |
| --- | ---: |
| Mermaid 官方图表家族 | 33 |
| 已实现图表家族 | 16/33 |
| 待翻译 | 17 |
| 独立生产场景 | 210 |
| 已声明能力覆盖率 | 274/274 |
| 大规模视觉矩阵 | 4,096 个唯一源码，每个已实现家族 256 个 |
| Native/Official 截图 | 8,192 张矩阵截图，另有 420 张独立语料截图 |
| 矩阵细节审查 | 全部 16 个已实现家族：4,096/4,096 验收；其中 3,886 个自动通过，210 个 ER/Journey/Requirement/Git Graph/Mindmap 告警经人工复核通过 |
| 自动视觉检查 | 4,096/4,096 几何通过；全部 16 个已实现家族的细节门禁通过 |
| 确定性 SceneGraph 重放 | 210 个通过，0 个不一致 |
| 内置主题矩阵 | 176/176 |
| 独立生成的 Native 压力输入 | 4,096 |
| JVM 测试 | 464 个通过，0 个失败 |
| Core 生产场景压力测试 | 1,050 次渲染，总耗时 437ms，P95 为 1ms，保留堆 33,080 bytes |
| 运行时负载矩阵 | Android、iOS、Desktop、Web 全部通过 |

| 证据文档 | 内容 |
| --- | --- |
| **[全图表路线](docs/full-diagram-roadmap.md)** | 官方 33 家族清单、当前 16/33 状态、缺失 17 类和新 Stable 门禁 |
| **[Stable 测试报告](docs/stability-report.md)** | Stable 判定、视觉对比图、测试、压力指标、运行时负载证据和复现步骤 |
| **[全部 4,096 个 Native/Official 对比](docs/assets/stability-report/visual-parity-evidence.md)** | 256 页分页对比图，每页包含 16 组同源码结果 |
| [生产能力矩阵](docs/production-capability-matrix.md) | 274 项被独立验证的能力 |
| [生产就绪说明](docs/production-readiness.md) | 代码级 Stable 标准、资源预算和接入指引 |
| [Quality Gate](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/quality.yml) | 当前 JVM、构建、发布、安全、APK、视觉和 Web 负载自动化结果 |
| [Full Visual Parity](https://github.com/swithun-liu/cmp-mermaid/actions/workflows/full-visual-parity.yml) | 已实现子集的每周/手动截图与细节门禁 |

只有 33 个家族都通过新门禁后，整体 Mermaid `12.0.0` 支持才能达到 Stable。
对于无法忠实表达的合法特性，系统会返回
`MermaidError.UnsupportedFeature`，而不是静默绘制可能误导用户的近似结果。

## Native 与 Mermaid.js 对比

以下对比使用相同的 Mermaid 源码、主题、布局模式和固定 viewport。目标是语义等价、
视觉质量相当，而不是与浏览器输出像素级一致；不同平台的字体度量可能存在差异。

**Flowchart：多区域故障转移**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-flowchart-native.png" alt="CMP Native 多区域故障转移 Flowchart" width="700"> | <img src="docs/assets/parity-flowchart-official.png" alt="Official Mermaid.js 多区域故障转移 Flowchart" width="700"> |

<details>
<summary><strong>更多同源码对比</strong></summary>

**XY Chart：柱线混合序列**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-xy-native.png" alt="CMP Native XY Chart" width="700"> | <img src="docs/assets/parity-xy-official.png" alt="Official Mermaid.js XY Chart" width="700"> |

**State Diagram：标签、循环、分支和终止状态**

| CMP Native - Compose Canvas | Official - Mermaid.js `12.0.0` |
| :---: | :---: |
| <img src="docs/assets/parity-state-native.png" alt="CMP Native State Diagram" width="700"> | <img src="docs/assets/parity-state-official.png" alt="Official Mermaid.js State Diagram" width="700"> |

测试报告包含 210 个独立生产场景对比，以及由 4,096 个唯一 Mermaid 源码组成的
独立大规模矩阵：
**[查看全部 256 页视觉证据](docs/assets/stability-report/visual-parity-evidence.md)**。

</details>

## 支持的图表

| 图表 | 状态 | 生产案例 | 主要覆盖能力 | 详情 |
| --- | :---: | ---: | --- | --- |
| Flowchart | 细节门禁通过 | 14 | Jison/FlowDB、Dagre、形状、连线、Markdown/HTML 标签 | [兼容性](docs/flowchart-compatibility.md) |
| XY Chart | 细节门禁通过 | 13 | Jison/XY DB、D3 比例尺和刻度、柱状图/折线图、标签 | [兼容性](docs/xychart-compatibility.md) |
| Quadrant Chart | 细节门禁通过 | 13 | 坐标轴、象限、点、共享类、直接样式、主题 | [兼容性](docs/quadrant-compatibility.md) |
| Timeline | 细节门禁通过 | 13 | LR/TD 布局、分区、时期、事件、色阶与主题 | [兼容性](docs/timeline-compatibility.md) |
| Kanban | 细节门禁通过 | 13 | 分区、任务、元数据、优先级、工单链接与主题 | [兼容性](docs/kanban-compatibility.md) |
| Sequence | 细节门禁通过 | 14 | 参与者、26 种消息形式、注释、激活、控制区域 | [兼容性](docs/sequence-compatibility.md) |
| Class | 细节门禁通过 | 13 | 分区、泛型、命名空间、关系、Dagre | [兼容性](docs/class-compatibility.md) |
| State | 细节门禁通过 | 13 | 复合状态、并发、注释、分叉/汇合、Dagre | [兼容性](docs/state-compatibility.md) |
| Entity Relationship | 细节门禁通过 | 13 | 属性、基数、关系、嵌套子图 | [兼容性](docs/er-compatibility.md) |
| Gantt | 细节门禁通过 | 13 | 日期、依赖、排除日期、里程碑、D3 风格刻度 | [兼容性](docs/gantt-compatibility.md) |
| Pie | 细节门禁通过 | 13 | Langium 语法、D3 角度、环形图、图例、调色板 | [兼容性](docs/pie-compatibility.md) |
| User Journey | 细节门禁通过 | 13 | 分区、评分、参与者、满意度表情和文本策略 | [兼容性](docs/journey-compatibility.md) |
| Requirement | 细节门禁通过 | 13 | SysML 类型与字段、元素、七类关系、Dagre、样式 | [兼容性](docs/requirement-compatibility.md) |
| Git Graph | 细节门禁通过 | 13 | Langium 语法、分支、合并、cherry-pick、方向与主题 | [兼容性](docs/gitgraph-compatibility.md) |
| Mindmap | 细节门禁通过 | 13 | Jison/Mindmap DB、CoSE-Bilkent、Dagre、tidy tree、形状与主题 | [兼容性](docs/mindmap-compatibility.md) |
| Packet | 细节门禁通过 | 13 | Langium 语法、显式/计数字段、跨行拆分与固定网格渲染 | [兼容性](docs/packet-compatibility.md) |

在各自文档声明的兼容范围内，已实现的 16 类图均支持 Mermaid frontmatter、metadata、
Unicode 和相关主题变量。

## 在线体验

打开 **[Kotlin/Wasm 在线 Demo](https://swithun-liu.github.io/cmp-mermaid/)**，
可以浏览语法、渲染 Gallery、切换全部 11 个主题，并比较 CMP Native 与锁定版本
Mermaid.js 的结果。每种受支持的图表都有可编辑的 Playground，并支持切换
Native/Official 预览；Mindmap 可在 CoSE-Bilkent、Dagre 与 tidy-tree
布局间切换。

<a href="https://swithun-liu.github.io/cmp-mermaid/">
  <img src="docs/assets/web-playground.png" alt="CMP Mermaid Kotlin Wasm Playground" width="900">
</a>

Official 对比渲染器只存在于 `mermaid-debug-ui`。
`mermaid-core` 和 `mermaid-compose` 从不使用 Mermaid.js。

## 架构

```text
Mermaid source
    -> Kotlin preprocessor and translated parser
    -> translated diagram database and layout preparation
    -> platform-independent MermaidScene
    -> Compose Canvas
```

- `mermaid-core` 负责解析、图表状态、布局适配器、类型化错误、主题和平台无关
  SceneGraph。
- `mermaid-compose` 负责 Canvas 绘制、文本测量、资源、交互和拖动/缩放行为。
- `mermaid-debug-ui` 负责文档、Gallery、Playground、Official 对比和负载测试页面。
  它是可选模块，应与生产 release variant 隔离。
- `sample/*` 包含 Android、iOS、Desktop 和 Web 的轻量启动壳，共享同一套
  debug UI。

生产库不包含 JavaScript 引擎，也不打包 JavaScript 算法。统一布局默认使用纯
Kotlin Dagre。ELK 名称和 `flowchart-elk` 仍会作为上游输入被识别，但会返回
`MermaidError.UnsupportedFeature("ELK layout")`，绝不会静默替换为其他布局。

## 接入

当前发布坐标如下：

| 模块 | Maven 坐标 |
| --- | --- |
| Core 渲染器 | `com.swithun:mermaid-core:0.1.0` |
| Compose 渲染器 | `com.swithun:mermaid-compose:0.1.0` |
| Debug 与对比 UI | `com.swithun:mermaid-debug-ui:0.1.0` |

```kotlin
dependencies {
    implementation("com.swithun:mermaid-compose:0.1.0")
    debugImplementation("com.swithun:mermaid-debug-ui:0.1.0")
}
```

> [!NOTE]
> 发布坐标与 POM 已准备就绪，但首个公开制品仓库版本尚未上传。在此之前，
> 请直接依赖仓库模块，或将其发布到本地/内部 Maven 仓库。

直接依赖源码仓库模块：

```kotlin
dependencies {
    implementation(project(":mermaid-compose"))
    debugImplementation(project(":mermaid-debug-ui"))
}
```

Compose 基本用法：

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

在 `build/maven-repository` 下生成全部 KMP 发布制品：
```bash
./gradlew \
  :mermaid-core:publishAllPublicationsToBuildRepository \
  :mermaid-compose:publishAllPublicationsToBuildRepository \
  :mermaid-debug-ui:publishAllPublicationsToBuildRepository
```

## 主题

包含 Mermaid `12.0.0` 的全部 11 个预设主题：
`default`、`dark`、`forest`、`neutral`、`base`、`neo`、`neo-dark`、
`redux`、`redux-color`、`redux-dark` 和 `redux-dark-color`。

业务主题可以通过 Kotlin `copy` 从预设主题派生，也可以通过
`MermaidTheme.withVariables(...)` 使用 Mermaid 兼容的 `themeVariables`。
无效外部值会返回 `GMResult.Err`。

```kotlin
val brandTheme = MermaidTheme.preset(MermaidThemePreset.ReduxColor).copy(
    background = SceneColor(0xFF101820),
    nodeFill = SceneColor(0xFFF2AA4C),
    nodeText = SceneColor(0xFF101820),
    edge = SceneColor(0xFFF2AA4C),
)
```

任意 `themeCSS` 依赖浏览器 DOM/CSS 语义，因此会返回
`MermaidError.UnsupportedFeature`。跨平台样式应使用类型安全的 Kotlin 主题对象
和 `MermaidFontFamilyResolver`。

## 跟随 Mermaid 版本

CMP Mermaid 采用带源码映射的翻译工作流，而不是根据截图重新实现行为：

1. 锁定 Mermaid、Jison、D3、Marked、Dagre、CoSE-Bilkent 和 tidy-tree 版本。
2. 将每个 Kotlin parser、DB、layout、shape、theme 和 renderer 边界映射到
   Mermaid 上游源码。
3. 根据锁定的输入生成 parser table、rule、entity、fixture 和仅用于调试的参考资源。
4. Mermaid 升级时只翻译上游增量。
5. 重新运行完整视觉对拍和平台门禁。

源码映射：
[Flowchart](docs/upstream-flowchart-map.md) ·
[XY Chart](docs/upstream-xychart-map.md) ·
[Quadrant Chart](docs/upstream-quadrant-map.md) ·
[Timeline](docs/upstream-timeline-map.md) ·
[Kanban](docs/upstream-kanban-map.md) ·
[Sequence](docs/upstream-sequence-map.md) ·
[Class](docs/upstream-class-map.md) ·
[State](docs/upstream-state-map.md) ·
[ER](docs/upstream-er-map.md) ·
[Gantt](docs/upstream-gantt-map.md) ·
[Pie](docs/upstream-pie-map.md) ·
[User Journey](docs/upstream-journey-map.md) ·
[Requirement](docs/upstream-requirement-map.md) ·
[Git Graph](docs/upstream-gitgraph-map.md) ·
[Mindmap](docs/upstream-mindmap-map.md)

## 验证

运行 JVM 与发布门禁：

```bash
./gradlew \
  :mermaid-core:jvmTest \
  :mermaid-compose:jvmTest \
  verifyPublicationCoordinates
```

运行示例应用：

```bash
./gradlew :sample:androidApp:installDebug
./gradlew :sample:desktopApp:run
./gradlew :sample:webApp:wasmJsBrowserDevelopmentRun
```

**[Stable 测试报告](docs/stability-report.md#reproduce-the-report)**
包含完整的跨平台构建和 Native/Official 视觉证据复现命令。

## 许可证

CMP Mermaid 采用 [MIT License](LICENSE) 发布。
翻译后的 Mermaid 行为和仅用于开发的参考资源保留其上游声明，详见
[THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
