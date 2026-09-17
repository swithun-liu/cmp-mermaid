# AGENTS.md - CMP Mermaid 工程约定

本文件适用于仓库内全部目录，是跨对话、跨模型、跨 Agent 的不可协商约定。

## 1. 目标与基线

- 本项目以 Mermaid.js `12.0.0` 为固定上游基线。
- 最终目标是实现该版本官方支持的全部 33 个图表族及其相关语法、配置、布局和渲染语义；当前已实现的子集、单个 Demo 或局部测试通过都不代表项目完成。
- 升级基线前，必须先更新版本、源码映射、第三方声明和完整兼容性门禁。

## 2. 忠实翻译路线

- Kotlin 实现必须从 Mermaid.js `12.0.0` 的具体源码忠实翻译，禁止凭记忆重写、仅凭截图猜测行为或自由发挥。
- 开始实现或修复前，必须先定位上游源码文件、函数和调用关系；实现粒度应支持上游某个方法更新后重新翻译对应方法，而不必重新推导整套行为。
- Kotlin 端应在类、方法或关键算法附近标注映射，例如：
  `// Mermaid.js 12.0.0: packages/mermaid/src/.../file.ts -> functionName`
- 类名、方法名、数据结构、处理阶段和算法拆分应尽量贴近上游并保持可追溯。因 Kotlin、KMP 或 Compose 平台约束必须调整时，应以简短注释或 `docs/upstream-*-map.md` 说明上游映射和适配差异。
- 不得用“看起来相近”的替代算法静默改变官方语义；尚未支持的能力必须显式返回结构化的不支持结果，并记录缺口。

## 3. 上游源码

- 默认参考源码目录为 `build/upstream/mermaid-src`，该目录被 Git 忽略，不得提交。
- Mermaid 主源码位于 `build/upstream/mermaid-src/packages/mermaid/src`；使用前核对
  `packages/mermaid/package.json` 的版本为 `12.0.0`。
- 本地缺失时使用公开仓库重新拉取：

```bash
git clone --depth 1 --branch 'mermaid@12.0.0' \
  https://github.com/mermaid-js/mermaid.git \
  build/upstream/mermaid-src
```

- `tools/official-reference` 只用于生成、审计和官方对拍；生产实现不能依赖其中的 JavaScript 运行时。

## 4. 生产架构

- 生产链路必须保持纯 Kotlin Multiplatform：解析、状态、布局和场景构建位于 `mermaid-core`，最终由 `mermaid-compose` 使用 Compose Canvas 渲染。
- `mermaid-core` 和 `mermaid-compose` 禁止引入 WebView、JavaScript 引擎、浏览器 DOM 或内嵌 Mermaid.js。
- 官方 Mermaid.js 仅允许存在于隔离的调试、参考渲染和对拍工具中，生产模块不得依赖这些模块。

## 5. 正确性与完整性

- 每个翻译切片都应维护上游源码映射，并以 Mermaid.js `12.0.0` 的真实行为作为测试和视觉对拍基准。
- 解析、数据库状态、布局、图形、主题、文本和交互等关键语义都属于兼容性范围，不能只验证语法可解析或画面非空。
- 新增图表族或修改共享能力时，应补充与影响范围相称的单元测试、官方样例和 Native/Official 对拍；不得把未覆盖能力标记为完成或 Stable。

## 6. 开源与许可证

- 本仓库及 Mermaid.js 翻译成果按 MIT 许可处理；保留必要的上游版权和许可声明，并同步维护 `THIRD_PARTY_NOTICES.md`。
- 仓库中只能出现公开可获得的 OSS 源码、通用 KMP/Compose 实现、公开依赖和公开文档。
- 严禁写入任何公司内部代码、包名、服务地址、文档、配置、密钥、业务数据、人员信息或其他私有内容；提交前必须检查完整 diff。

## 7. 变更保护

- 默认工作区可能包含用户的未提交改动。修改前先检查目标文件和相关 diff，只做当前任务要求的最小范围变更。
- 禁止覆盖、回退、清理或格式化掉用户已有改动；遇到相关改动时应在其基础上继续，无法安全合并时先询问用户。
- 未经用户明确要求，不得创建提交、推送、重写历史或执行破坏性 Git 操作。
