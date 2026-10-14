# Upstream Swimlanes Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Swimlanes path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

Status values are mandatory: `Exact` means the upstream function and stage
order are translated directly; `Platform adaptation` means only the runtime or
rendering boundary differs while preserving the upstream contract;
`Unsupported` blocks Stable.

| Upstream source | Kotlin source | Status |
| --- | --- | --- |
| `config.ts` | `upstream/SwimlaneConfig.kt` | Exact |
| `helpers.ts` | `upstream/SwimlaneTypes.kt`, `upstream/Helpers.kt` | Exact |
| `edgeLabelNodes.ts` | `upstream/EdgeLabelNodes.kt` | Exact |
| `phase0.helpers.ts` | `upstream/Phase0Helpers.kt` | Platform adaptation: deterministic KMP translation of host `localeCompare` |
| `phase1.cycles.ts` | `upstream/Phase1Cycles.kt` | Exact |
| `phase2.options.ts` | `upstream/Phase2Options.kt` | Exact |
| `phase2.longestPath.ts` | `upstream/Phase2LongestPath.kt` | Exact |
| `phase2.crossCounts.ts` | `upstream/Phase2CrossCounts.kt` | Exact |
| `phase2.crossLaneAdjust.ts` | `upstream/Phase2CrossLaneAdjust.kt` | Exact |
| `phase2.crossOptimization.ts` | `upstream/Phase2CrossOptimization.kt` | Exact |
| `phase2.dummies.ts` | `upstream/Phase2Dummies.kt` | Exact |
| `phase2.gravity.ts` | `upstream/Phase2Gravity.kt` | Exact |
| `phase2.laneAwareCompact.ts` | `upstream/Phase2LaneAwareCompact.kt` | Exact |
| `phase2.multitree.core.ts` | `upstream/Phase2MultitreeCore.kt` | Exact |
| `phase2.multitree.order.ts` | `upstream/Phase2MultitreeOrder.kt` | Exact |
| `phase3.ordering.ts` | `upstream/Phase3Ordering.kt` | Exact |
| `phase4.coordinates.ts` | `upstream/Phase4Coordinates.kt` | Exact |
| `driving-tree.ts` | `upstream/DrivingTree.kt` | Exact |
| `laneOrdering.ts` | `upstream/LaneOrdering.kt` | Exact |
| `pipeline.ts` | `upstream/Pipeline.kt` | Exact |
| `layoutCore.ts` | `upstream/LayoutCore.kt` | Exact |
| `orthogonalRouter/router.ts` | `upstream/OrthogonalRouter.kt` | Exact |
| `direction/geometry.ts` | `upstream/DirectionGeometry.kt` | Exact |
| `direction/detourSimplification.ts` | `upstream/DetourSimplification.kt` | Exact |
| `direction/endpointClip.ts` | `upstream/EndpointClip.kt` | Exact |
| `direction/labelAnchoring.ts` | `upstream/LabelAnchoring.kt` | Exact |
| `direction/lrTransform.ts` | `upstream/DirectionTransform.kt` | Exact |
| `direction/materializedGeometry.ts` | `upstream/MaterializedGeometry.kt` | Exact |
| `direction/portSwap.ts` | `upstream/PortSwap.kt` | Exact |
| `direction/sharedTrackNudging.ts` | `upstream/SharedTrackNudging.kt` | Exact |
| `direction/siblingSharedFaceRouting.ts` | `upstream/SiblingSharedFaceRouting.kt` | Exact |
| `direction/terminalStub.ts` | `upstream/TerminalStub.kt` | Exact |
| `direction/validation.ts` | `upstream/Validation.kt` | Exact |
| `postProcessing.ts` | `upstream/PostProcessing.kt` | Exact |
| `index.ts` | `upstream/SwimlaneLayoutAdapter.kt` | Platform adaptation: Flowchart model to `LayoutData` |
| `adjustLayout.ts` | shared Compose line-hop rendering | Platform adaptation: Canvas instead of SVG mutation |
| `rendering-elements/edges.js -> insertEdge` | `upstream/SwimlaneLayoutAdapter.kt`, `MermaidShapeIntersectionPort.kt` | Platform adaptation: SceneGraph shape intersection |
| `diagrams/swimlanes/detector.ts`, `swimlanesDiagram.ts` | `swimlane/SwimlanePlugin.kt` | Platform adaptation: translated Flowchart parser/database reuse |
| `clusters/swimlane.js`, `diagrams/swimlanes/styles.ts` | `flowchart/FlowchartLayout.kt` | Platform adaptation: Compose SceneGraph painting |
| `config.type.ts -> look: handDrawn`, RoughJS shape renderer | `MermaidEngine.kt` | Unsupported: returns `MermaidError.UnsupportedFeature("handDrawn look")` |

`swimlane/SwimlaneLayout.kt` is intentionally only a thin facade. It does not
contain an alternative layout algorithm.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/mermaid/src/diagrams/swimlanes/detector.ts` | `8199a23c83c782c71b1a619f45b5f0cdeb9a478a4cad72c78a5c5cdef58e1069` |
| `packages/mermaid/src/diagrams/swimlanes/styles.ts` | `385c35e102169c38f9f127a003011ed6899a5b3b27f6e9068317d2aebf57bf7c` |
| `packages/mermaid/src/diagrams/swimlanes/swimlanesDiagram.ts` | `5f6e25dd43c5b7e1b6c6e1342bd8bc2552538815752110723ffab489eac45bc6` |
| `packages/mermaid/src/rendering-util/rendering-elements/clusters/swimlane.js` | `3137424e911676dc60513b859e600f7b6795d671af5045bd69e961d8a0e0a3c3` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/adjustLayout.ts` | `7ba3d817d0e9bd59031f643d3a4d6db63aee9819193f9a27a3494213345b9115` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/config.ts` | `e58c9a19f623c78e1e47878e1bfb1d402ed6cbb395d2436154cac1011c17fcb1` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/driving-tree.ts` | `8a116a1d11685c5ac56ec5e9dc0c6cb373a02ccc907dc5f3b1184823d6a5434b` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/edgeLabelNodes.ts` | `5864611bd8e7aec2bd002c22d61332968eebb14b246bb180aae3567a60db4f1c` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/helpers.ts` | `0a2b67a6e6b1534d0464bb23215a3538894ae99ad35256e74c9151dfc06546d8` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/index.ts` | `6b56fe9441195dc10a8bf2dcba3630ef6e65f68546ac08684a72fec237fb321e` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/laneOrdering.ts` | `313a1ea9fdf8bacb1bcd0c5f29ca80bff9bf0d912a6148a59c7709bdaf5d7c10` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/layoutCore.ts` | `630d0a0d1fa2ab74fcb4c9bef5c2ede7349b2a7a54e0fa3c1c54c87a445e4802` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/pipeline.ts` | `09771f7acc6b6e53523b79dc1c02f248dc32b979371dd259aef5463078bfac3a` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/postProcessing.ts` | `77c3a3e2c1dd287f0ec1521a89bf2435ed1bf282e24490e631da639ac8711e0b` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase0.helpers.ts` | `7a0bf44c99291a837e5f0ba42070837449d165f8f25589cd95545dc78b087400` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase1.cycles.ts` | `8d116f2982068fc2f17b4c7cebd8da888e99bdcb07f8205a866183e3b6e307da` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.crossCounts.ts` | `23a57801c9a60c31d093b1bf401171176510926f099ae85075c2894dff323cd1` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.crossLaneAdjust.ts` | `fd42554a3e150eee5cae4959e3aaaa6ca9d47770648b4dc1f942d0199b5552c1` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.crossOptimization.ts` | `76cbaad69ebb9622545c01abd0a6b1d684f13a02d5cef95f5caa4d93e0b74133` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.dummies.ts` | `cda576d11a92b2f5d1d5b79258335c13602789be089875ad653d3322cd1cda9b` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.gravity.ts` | `6f679386ef0b3a2ad09cf439c1ecb9a0f210e5d608b0e6bba6da81c1672c5a6f` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.laneAwareCompact.ts` | `d7c04357287ad7d54c27ebff0859178b63c7f430f7e085fb32d7c8ccced2bfd2` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.longestPath.ts` | `4c0807bd5951f42ee8d83a9adf9abbbf2d54a98614d4121a0eb37dbda4558c60` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.multitree.core.ts` | `2885d6a16fbb56493efd2871a84b3ed9f3d070a9ce28842798c5e9d04c3861bc` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.multitree.order.ts` | `0e55ba8c26dcf9326eb6959cddc921b29a078dd9c996c63a6218b2dea4d3f169` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase2.options.ts` | `82e7e5d41008f0753c450f8634eaaa2639531a11d8561e7a2f24f28a8e733f4c` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase3.ordering.ts` | `f1bf906f3597b91f7782dcfc66686830c89e8ff40728eeabe0bdbc3cd1b6c638` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/phase4.coordinates.ts` | `97ee67eefd1e35a6b263b2930a0fc1a4de2f8cc2572f0a5864b96ba85fe78a9d` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/orthogonalRouter/router.ts` | `16316716e88c8a684e80f07fb82dbd459bb2215678652dadb27513948c496692` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/detourSimplification.ts` | `dc71adbb110ce9d5cd7b3cfe3a21b8ce71c21c05bbc6abc012981070dda2baf0` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/endpointClip.ts` | `25ffa385a072a867e210155c1a403447c4b10dbcfaaee14a97b74d692a43eca5` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/geometry.ts` | `6b5e5c37c046b6dcf78d078ebd0ece0faf16243a5261514fd783632ffc2e2a2b` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/labelAnchoring.ts` | `67fe854a502db9fcf6b1471eb8c19ddc54066bb4387af7986b8fc76e09c87099` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/lrTransform.ts` | `1ed8c9ffe8af570bbdba3cc7081d9c9df09be624be3d8d8c07d7d548927dd16c` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/materializedGeometry.ts` | `315164346f2f1a90cb4e8cb13f80b1983a3cc4647ef9ee622e794bcae9036902` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/portSwap.ts` | `c8369b220c1d2bd240f57c502ae17b3799dbfd182708c9e8d4bc28f955874597` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/sharedTrackNudging.ts` | `37ea7a8d5b40c5cfd067bc07301b5c0e3476c9f204dfd6abc399a6c7889467ce` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/siblingSharedFaceRouting.ts` | `335276ad8fed3d002a022d2976518c0bd0a12dd2c59c60e923a2de0e409e3c58` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/terminalStub.ts` | `5b59a8f4123355bfe79d76df5f7f06015dbc447d939d157eb7c0bed2185e9202` |
| `packages/mermaid/src/rendering-util/layout-algorithms/swimlanes/direction/validation.ts` | `002fb84109340049e060600886f1c2e13183188fd1d117b6396a8f93e081adbf` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/swimlanes.md` | `3530c6ecba9686953eb37a33af766e17465abc2fb37b725af44ea25cb2a0c4a7` |

## Intentional Kotlin Adaptations

- Mermaid itself treats Swimlanes as a Flowchart layout variant. Kotlin
  preserves that boundary by reusing the translated Flowchart parser,
  database, shapes, edge semantics, and renderer.
- Every upstream layout, direction, routing, and post-processing source file
  has a corresponding file under `swimlane/upstream/`. Kotlin collections
  replace JavaScript collections, but method boundaries and stage order remain
  separate and traceable.
- Upstream graph ordering calls JavaScript `String.localeCompare`, whose
  default locale is selected by the browser host. `compareMermaidIds` provides
  one deterministic KMP ordering for Mermaid's ASCII node and generated IDs,
  including lowercase generated `edge-label-*` IDs before uppercase node IDs.
  Every upstream Swimlanes `localeCompare` call routes through this helper;
  no cycle, lane, or case-specific ordering branch is added.
- SVG measurement is replaced by `TextMetricProvider`; SVG groups, paths,
  markers, rotated labels, and paint order become SceneGraph elements rendered
  by Compose Canvas.
- The renderer-side Swimlanes endpoint clipping branch is translated in the
  adapter because Compose builds `ScenePath` directly instead of mutating an
  SVG path. Shape intersection is shared with Dagre through
  `MermaidShapeIntersectionPort`.
- Parser, configuration, state, and layout failures remain explicit
  `GMResult.Err` values. Pure geometric operations preserve upstream
  floating-point behavior.

## Current Verification State

The direct translation and every classic-look verification gate are complete.
The 47-entry upstream spec map resolves to 33 translated Kotlin unit-test
groups, 13 executable DDLT fixtures, and one structured unsupported case.
`look: handDrawn` depends on Mermaid's RoughJS renderer and returns
`MermaidError.UnsupportedFeature`; that declared gap keeps Swimlanes and the
overall Mermaid contract **not Stable**.

Completed fresh gates:

- all mapped upstream layout, direction, routing, and post-processing tests;
- focused facade, router key, shape-intersection, title-coordinate, HTML-label,
  and topology-manifest tests, including both `N3 -> N4` edges in
  `visual_swimlanes_011`;
- 13 DDLT fixtures: detail and topology `13/13` pass, with no geometry review
  or failure;
- 13 production pairs: detail and topology `13/13` pass, with no geometry
  review or failure;
- The current 256-case Native/Official matrix reports `256 pass / 0 review / 0 fail`.
  Geometry has 0 hard failures with width `1.032-1.057`, height `1.017-1.056`,
  and foreground ink `0.978-1.127`. Per-edge topology is `256/256` pass.
- The production and all 16 matrix contact sheets are published and source-bound;
  current automated findings remain explicit.
- full JVM `1,041/1,041`, Wasm core/Compose `1,031/1,031`, and Wasm debug UI
  `7/7` tests;
- Wasm production build, production isolation, publication coordinate, and
  source-map consistency gates.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff every mapped detector, diagram, style, pipeline, phase, routing,
   direction, configuration, and documentation file.
4. Translate changed behavior in the corresponding Kotlin method.
5. Run translated upstream tests, focused JVM tests, and all platform gates.
6. Capture all 256 Swimlanes Native/Official pairs.
7. Enforce detail, geometry, and per-edge topology audits.
8. Review all contact sheets.
9. Only then update this map, the capability matrix, and the public stability
   report. Mark Stable only when no mapped entry remains `Unsupported`.
