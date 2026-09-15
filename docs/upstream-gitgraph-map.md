# Upstream Git Graph Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| `@mermaid-js/parser` | `2.0.0` |
| Langium grammar toolchain | `4.2.1` |
| Native renderer | Compose Multiplatform Canvas |

The production Git Graph path is Kotlin in `commonMain`. It does not execute
Mermaid.js, Langium, or a WebView.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `gitgraph/upstream/mermaid/GitGraphParser.kt` | `packages/parser/src/language/gitGraph/gitGraph.langium`, `reference.langium`, and `tokenBuilder.ts` | Header, statements, attributes, quoted strings, references, comments, and source locations |
| `gitgraph/upstream/mermaid/GitGraphParser.kt` | `src/diagrams/git/gitGraphParser.ts` | Langium AST-to-database action order and parser error boundary |
| `gitgraph/upstream/mermaid/GitGraphTypes.kt` | `src/diagrams/git/gitGraphTypes.ts` | Commit, branch, merge, cherry-pick, symbol, and orientation types |
| `gitgraph/upstream/mermaid/GitGraphDb.kt` | `src/diagrams/git/gitGraphAst.ts` | Commit map, branch heads/order, checkout/switch, merge and cherry-pick validation, metadata, and insertion order |
| `gitgraph/GitGraphPlugin.kt` | `gitGraphDetector.ts` and `gitGraphDiagram.ts` | Diagram detection and parser/layout orchestration through the common plugin contract |

`tools/official-reference/generate-gitgraph-doc-fixtures.mjs` verifies Mermaid
`12.0.0` and the documentation SHA-256 before regenerating all official
documentation examples. The runtime parser is a direct Kotlin translation of
the grammar behavior; it does not bundle the Langium runtime.

## Layout And Rendering

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `gitgraph/GitGraphLayout.kt` | `gitGraphRenderer.ts -> draw`, `setBranchPosition`, `drawCommits`, and `drawArrows` | LR/TB/BT positioning, parallel ranks, branch lanes, rerouted arrows, labels, tags, and commit glyphs |
| `gitgraph/GitGraphLayout.kt` | `styles.js` | Classic, Neo, and Redux geometry; Git colors; inverse colors; label fills/borders; and text styles |
| `MermaidPreprocessor.kt` | Mermaid config schema | Typed `gitGraph` options, title, and theme-variable overrides |
| `MermaidContract.kt` | Mermaid default configuration and theme files | `MermaidGitGraphOptions`, `MermaidGitGraphTheme`, all 11 presets, and custom theme variables |
| `SceneGraph.kt` | Mermaid SVG primitives and `insertLookDefs.ts` | Platform-independent circles, paths, rotated text, shadows, and object-bounding-box linear gradients |
| `mermaid-compose/.../MermaidDiagram.kt` | Browser SVG painting behavior | Compose Canvas circles, paths, gradients, rotated text, and SVG-native text scale |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `gitGraph.langium` | `7983e2cb45fad0c62a81c6ebb40c942637ea7874cb6c324a99e84feae100b514` |
| `reference.langium` | `34cbd8a9066c6560f28ba486f50739c6cdc226be00d764582ee0366aa0fd9d25` |
| `tokenBuilder.ts` | `577f2292cad529c7a68bd11bb49dceb7e0e22adb07bb71ac2ecb7e15fd231a99` |
| `gitGraphDetector.ts` | `d5511b76e278b6a3b2b53c7b185b220806c3e77a8422deae9c2629abec79a76d` |
| `gitGraphDiagram.ts` | `076accdafa1178ee78686e315db4e695beb0e04c3c03a8df45f7dadd60980046` |
| `gitGraphParser.ts` | `46d7b331b9c43e0ce45d2dba71e671b34670c9fe34a248a664786191d917f204` |
| `gitGraphAst.ts` | `6bea9f0f43982992ded7eec927cf543bb02933b305121d69bb78ad5edb0da90c` |
| `gitGraphTypes.ts` | `b023300eb4f4bcb7340f6cc741fefa5662f433e7e3a58097ec724435a06bd76a` |
| `gitGraphRenderer.ts` | `987fbd4fc2eecc534578e74c168767c10cc79c096e3212ac4f7b1d601ed2fdc6` |
| `styles.js` | `df465c755c73e8aae895f3dad8bb7e47c9e64dd62901f0e36c3ae0973e56d311` |
| `theme-neo.js` | `3e7c44e329ccef9c15e12d8606ccdac8196bc6f34f739d5617d14e318ff32e78` |
| `theme-neo-dark.js` | `62397b9e875f5383f5fe437a1fb803398d9c6f38f1df7c809d50c1322da85ef5` |
| `insertLookDefs.ts` | `55bb1d46c232a7f2935d34976a6c1e6cfa7b874b7ae0ba4ac601761cfc660569` |
| `gitgraph.md` | `6afef2eaa11254f051f826fc3a422507d21c8e7ea84c38b27054381566d81cec` |

## Intentional Kotlin Adaptations

- JavaScript `Map` insertion order maps to Kotlin linked collections.
- Mermaid's random generated commit suffix is replaced by a deterministic
  seven-character suffix while retaining the same public ID shape.
- Expected parser, configuration, state, resource, and layout failures use
  `GMResult.Err`.
- Browser SVG text maps to measured Compose text with explicit SVG-native
  width scale, rotation pivot, and line-break behavior.
- SVG gradient definitions map to typed SceneGraph gradients and Compose
  `Brush.linearGradient`.
- SceneGraph output remains platform independent; Compose owns final Canvas
  painting.

## Parity Gate

- `GitGraphParserTest` covers grammar actions, state transitions, generated
  IDs, merge/cherry-pick validation, metadata, and malformed input.
- `GitGraphLayoutTest` covers orientations, parallel ranks, rerouting, labels,
  symbols, theme variables, all built-in theme families, and resource errors.
- `GitGraphStressTest` renders 256 deterministic randomized legal diagrams
  twice and validates structure, finite geometry, and exact replay.
- `OfficialGitGraphDocumentationTest` executes all 35 examples extracted from
  Mermaid `12.0.0` documentation.
- The large-scale Web audit compares 256 unique same-source cases against
  Mermaid.js `12.0.0`; all 16 contact-sheet pages were manually reviewed.

## Upgrade Procedure

1. Update the locked Mermaid, parser, and Langium versions and source commit.
2. Diff every upstream file listed in the hash table.
3. Translate changed grammar, AST/database, renderer, configuration, and style
   behavior into the mapped Kotlin files.
4. Regenerate the documentation fixtures and review their hash.
5. Run full JVM and multiplatform tests plus all platform build targets.
6. Capture and review all 256 Git Graph Native/Official pairs at the fixed
   `1200 x 900` viewport and all 11 built-in themes.
7. Update this map and the compatibility matrix before publishing.
