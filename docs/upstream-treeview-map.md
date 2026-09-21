# Upstream TreeView Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production TreeView path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `treeview/TreeViewPlugin.kt` | `treeView/detector.ts` and `diagram.ts` | Header detection, parser lifecycle, layout dispatch, and typed failures |
| `treeview/upstream/mermaid/TreeViewParser.kt` | `treeView.langium`, `tokenBuilder.ts`, `valueConverter.ts`, and `treeView/parser.ts` | Metadata, indentation, quoted and bare names, annotations, directory detection, source positions, and DB population |
| `treeview/upstream/mermaid/TreeViewBoxDrawingPreprocessor.kt` | `treeView/boxDrawingPreprocessor.ts` | Standard and heavy box-drawing detection, segment-width inference, normalization, line mapping, and mixed-format errors |
| `treeview/upstream/mermaid/TreeViewDb.kt` | `treeView/db.ts` | Synthetic root, stack-based hierarchy construction, node IDs, sanitization, title, and accessibility state |
| `treeview/upstream/mermaid/TreeViewTypes.kt` | `treeView/types.ts` | File and directory node types, annotations, descriptions, and child hierarchy |
| `treeview/upstream/mermaid/TreeViewIcons.kt` | `treeView/icons.ts` | Explicit icon precedence, suppression, filename and extension maps, built-in qualification, and default pack resolution |
| `treeview/TreeViewLayout.kt -> processNode` and `positionLabel` | `treeView/renderer.ts -> drawTree` and `positionLabel` | Preorder row layout, indentation, text measurement, connectors, node bounds, and responsive sizing |
| `treeview/TreeViewLayout.kt -> renderIcon` | `treeView/icons.ts` and `renderer.ts -> resolveNodeIcons` | Built-in file and folder vectors plus external Iconify scene assets |
| `treeview/TreeViewLayout.kt -> TreeViewStyle.resolve` and `MermaidPreprocessor.kt` | `treeView/styles.ts`, `defaultConfig.ts`, and `config.type.ts` | Scoped spacing, icon maps, typography, colors, highlights, validation, and frontmatter merging |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/parser/src/language/treeView/treeView.langium` | `05e6901b3931203420e45d754bb521a69623445f168c1124205c2b35a110e92b` |
| `packages/parser/src/language/treeView/tokenBuilder.ts` | `c422c5df4141cacb1eb28ed552d57da183c897cfb2a44617a9243b776c72763b` |
| `packages/parser/src/language/treeView/valueConverter.ts` | `fddddb34d256698a6c2b25bbff416c5dc949ddc9eac8f935a9a80ad02123d0cf` |
| `packages/parser/src/language/treeView/module.ts` | `ed13707f9c261f61811a00011bfc1c8916daeced9084c82ac8d5953667aa0da9` |
| `packages/mermaid/src/diagrams/treeView/detector.ts` | `4eae838575acd42bfba956cce6528cab73d224746f0253e2b634f97908f823e2` |
| `packages/mermaid/src/diagrams/treeView/diagram.ts` | `a1113bfb351a5156e46daef030943b91525a67e7244c770677cca483928af7e8` |
| `packages/mermaid/src/diagrams/treeView/parser.ts` | `1f6459e4d8a6925f6350ff80de96be062f0f0975e36268d85c09994aa738c609` |
| `packages/mermaid/src/diagrams/treeView/db.ts` | `b26f2ed39ff572b5de20969d8d3aad45b23782d1c8159d472b4fd3f97e028214` |
| `packages/mermaid/src/diagrams/treeView/types.ts` | `58eb08f14c097645a7cc8d0b9e5ad1d0d999d203dc1e452ffdf899f21657f6a4` |
| `packages/mermaid/src/diagrams/treeView/icons.ts` | `1e7c69822b88f07783717b49b69db4af38ba925a027498da3b4d14534834de6a` |
| `packages/mermaid/src/diagrams/treeView/renderer.ts` | `dd30254a9d43c7213d8062be3f07f3628dbd95c821a5e2edd9f9c65fb10539d0` |
| `packages/mermaid/src/diagrams/treeView/styles.ts` | `1b831916c99ffd5aeff9665c7c7f78ade8a51c9c89c51d81b809890cc398b63a` |
| `packages/mermaid/src/diagrams/treeView/boxDrawingPreprocessor.ts` | `cd7c54a23e845621de87813152b7d3cc7097e0371b4cf6b3e1f15ee871f4ed2b` |
| `packages/mermaid/src/docs/syntax/treeView.md` | `d79f6af9cf8fe48418037582b86b5aa226d99db325b207ba2a866e9c10fb2f7f` |

## Intentional Kotlin Adaptations

- Langium tokenization and value conversion are represented by a portable
  line parser that preserves the same token boundaries and returns typed
  `GMResult.Err` values.
- JavaScript exceptions from box-drawing preprocessing become structured
  parse errors while retaining original input line numbers.
- DOM `getBBox()` is replaced by `TextMetricProvider`.
- Built-in file and folder SVGs become SceneGraph paths. Registered external
  icon references remain `SceneAsset` values resolved by the host asset
  provider.
- SVG groups, text, rectangles, and lines become ordered SceneGraph elements
  rendered by Compose Canvas.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff the grammar, token/value converters, parser, preprocessor, database,
   types, icons, renderer, styles, configuration, and documentation.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 TreeView Native/Official pairs.
7. Update this map, the compatibility matrix, and the public stability report.
