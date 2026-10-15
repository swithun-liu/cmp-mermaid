# Upstream Architecture Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Cytoscape | `3.34.3` in the pinned Official reference install |
| Upstream force-layout dependency | `cytoscape-fcose` `2.2.0` |
| Nested solver dependency | `cose-base` `2.2.0` |
| Nested layout dependency | `layout-base` `2.0.1` |
| Native renderer | Compose Multiplatform Canvas |

The production Architecture path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, Cytoscape, fCoSE JavaScript, use a WebView, or depend on a
JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `architecture/ArchitecturePlugin.kt` | `architectureDetector.ts` and `architectureDiagram.ts` | Header detection, parser lifecycle, layout dispatch, and structured failure propagation |
| `architecture/upstream/mermaid/ArchitectureParser.kt` | `architecture.langium`, `arch.langium`, `tokenBuilder.ts`, `valueConverter.ts`, and `architectureParser.ts -> populateDb` | Tokens, declarations, icons, titles, parents, directional edge syntax, arrow modifiers, alignment directives, source positions, and parser errors |
| `architecture/upstream/mermaid/ArchitectureDb.kt` | `architectureDb.ts -> ArchitectureDB` | Declaration ordering, duplicate IDs, parent and edge validation, node/group lookup, adjacency maps, spatial maps, and group alignments |
| `architecture/upstream/mermaid/ArchitectureTypes.kt` | `architectureTypes.ts` | Services, junctions, groups, edges, directions, arrow polygons, spatial positions, and alignment types |
| `architecture/ArchitectureFcoseLayout.kt` | `architectureRenderer.ts -> layoutArchitecture`, `getAlignments`, `flattenAlignments`, and `getRelativeConstraints` | Source-order graph inputs, node and edge options, explicit and inferred constraints, label measurement, two-run dispatch, and structured unsupported handling |
| `architecture/upstream/fcose/FcoseAdapter.kt` | `cytoscape-fcose/src/fcose/index.js`, `cose.js`, reached `auxiliary.js` functions, Cytoscape compound bounds, and `architectureSeed.ts` | Reached whole-graph constrained branch, two seeded runs, graph filtering, recursive compounds, result relocation, body/visual bounds, and Mulberry32 |
| `architecture/upstream/cose/CoseLayout.kt` | `cose-base` `2.2.0` `CoSELayout.js` | Reached incremental constrained lifecycle, cooling loop, force ticks, gravity selection, displacement, and repulsion range |
| `architecture/upstream/cose/CoseModel.kt` | `cose-base` `2.2.0` `CoSEConstants.js`, `CoSEGraph.js`, `CoSEEdge.js`, and `CoSENode.js` | Reached constants, factories, force state, compound displacement propagation, and movement |
| `architecture/upstream/cose/ConstraintHandler.kt` | `cose-base` `2.2.0` `ConstraintHandler.js` | Initial constraint projection, DAG/component helpers, alignment and relative-placement enforcement, and per-tick constrained displacement |
| `architecture/upstream/layoutbase/LayoutBaseForce.kt` | `layout-base` `2.0.1` `Layout.js`, `fd/FDLayout.js`, `FDLayoutNode.js`, `FDLayoutEdge.js`, and constants | Reached force-layout lifecycle, ideal lengths, spring/repulsion/gravity forces, convergence, grid acceleration, and child weights |
| `architecture/upstream/layoutbase/LayoutBaseGraph.kt` | `layout-base` `2.0.1` `LNode.js`, `LEdge.js`, `LGraph.js`, `LGraphManager.js`, and `LayoutConstants.js` | Reached compound graph, caches, bounds, connectivity, ancestry, LCA, inclusion depth, and invalid-edge removal |
| `architecture/upstream/layoutbase/LayoutBaseGeometry.kt` | `layout-base` `2.0.1` geometry helpers | Mutable points, dimensions, rectangles, overlap separation, rectangle intersection, cardinal direction, and sign semantics |
| `architecture/ArchitectureLayout.kt` | `architectureRenderer.ts -> draw` and `svgDraw.ts` | Services, groups, junction gaps, edge routes, labels, explicit arrow polygons, viewport normalization, and SceneGraph paint order |
| `architecture/ArchitectureLayout.kt -> drawIcon` | `architectureIcons.ts` and `svgDraw.ts -> drawServices`, `drawGroups` | Exact built-in icon geometry, icon-text tiles, external icon assets, group icons, and service labels |
| `MermaidPreprocessor.kt` and `MermaidContract.kt` | `architectureDb.ts`, `defaultConfig.ts`, and `config.type.ts` | Typed defaults, frontmatter/init overrides, merge precedence, theme variables, and validation |

## Production Layout Function Checklist

Status is evaluated against the production call sequence used by Mermaid
`12.0.0`; a rendered result is not sufficient evidence for `Exact`.

| Order | Upstream production stage | Kotlin stage | Status |
| ---: | --- | --- | --- |
| 1 | `architectureRenderer.ts -> layoutArchitecture`: create the Cytoscape compound graph and add groups, services, junctions, and edges in source order | `ArchitectureFcoseLayout.layout` and typed `FcoseNodeInput`/`FcoseEdgeInput` construction | **Platform adaptation**: Cytoscape collections and style callbacks become typed Kotlin inputs while preserving source order, ownership, dimensions, edge parameters, and validation |
| 2 | `architectureRenderer.ts -> getAlignments / flattenAlignments` | `buildConstraints`, `flattenAlignments` | **Exact** |
| 3 | `architectureRenderer.ts -> getRelativeConstraints` | `buildConstraints`, `ArchitectureDirection.toRelativeConstraint` | **Exact** |
| 4 | `architectureSeed.ts -> withSeededRandom / mulberry32`; two `layout.run()` calls | `FcoseAdapter.runTwice`, `Mulberry32` | **Exact**: each run receives a newly seeded random stream, matching the renderer lifecycle |
| 5 | Cytoscape label projection, `layoutDimensions`, `updateCompoundBounds`, and `boundingBoxImpl` | `measureCytoscapeLabelDimensions`, `resolveCytoscapeNodeGeometries` | **Platform adaptation**: DOM/renderer measurement is supplied by `TextMetricProvider`; body, label-expanded visual, and outer visual bounds retain observed Cytoscape semantics |
| 6 | `cytoscape-fcose/src/fcose/index.js -> layout / run` whole-graph constrained branch in `quality: "proof", randomize: false` mode | `FcoseAdapter.runOnce` | **Exact** for the reached branch; Cytoscape event and collection APIs are adapted to direct Kotlin calls |
| 7 | `cytoscape-fcose/src/fcose/auxiliary.js -> getTopMostNodes`, `relocateComponent`, `calcParentsWithoutChildren` | top-most selection, result relocation, compound geometry resolution | **Exact** for the reached branch |
| 8 | `cytoscape-fcose/src/fcose/cose.js -> coseLayout`, `processChildrenList`, `processEdges`, `processConstraints` | `FcoseAdapter`, `CoseLayout`, and constraint models | **Exact** for the reached branch |
| 9 | `cose-base` `2.2.0` reached `CoSELayout`, `CoSENode`, model, and `ConstraintHandler` stages | `CoseLayout.kt`, `CoseModel.kt`, `ConstraintHandler.kt` | **Exact** for the reached incremental constrained branch |
| 10 | `layout-base` `2.0.1` reached lifecycle, graph, force, grid, and geometry stages | `LayoutBaseForce.kt`, `LayoutBaseGraph.kt`, `LayoutBaseGeometry.kt` | **Exact** for the reached branch |
| 11 | `architectureRenderer.ts -> layoutstop / getSegmentWeights` | `ArchitectureLayout` orthogonal edge construction | **Platform adaptation**: SceneGraph builds the same directional bend semantics without Cytoscape style storage |
| 12 | `architectureRenderer.ts -> draw`; `svgDraw.ts` draw stages | `ArchitectureLayout` | **Platform adaptation**: SVG DOM operations and text measurement become ordered SceneGraph elements and `TextMetricProvider` calls |

The default `randomize: false` production path no longer contains the former
custom force solver. Architecture remains non-Stable because the legal
`randomize: true` path is structurally rejected until its spectral, Matrix,
and SVD stages are translated, and because five current visual cases retain
genuine text-overlap review findings.

## fCoSE Translation Inventory

This inventory is the required translation checklist for the layout stack.
`Reached` means the function is executed by Mermaid's default
`quality: "proof", randomize: false` Architecture path. Mermaid always passes
an `alignmentConstraint` object, including when both arrays are empty, so
fCoSE disables tiling and component packing on this path.

| Upstream file | Production functions and stages | Default path | Current status |
| --- | --- | --- | --- |
| `architectureRenderer.ts` | `addGroups`, `addServices`, `addJunctions`, `addEdges`, `getAlignments`, `getRelativeConstraints`, `layoutArchitecture` | Reached | **Platform adaptation** for Cytoscape graph/style APIs; constraint semantics and two-run lifecycle are translated |
| `architectureSeed.ts` | `withSeededRandom`, embedded `mulberry32` | Reached once per `layout.run()` | **Exact** |
| `cytoscape-fcose/src/fcose/index.js` | constraint normalization, `constraintExist`, non-packed whole-graph branch, `coseLayout`, `relocateComponent`, `getPositions` | Reached | **Exact** for the reached branch, with direct-call adaptation at the Cytoscape event boundary |
| `cytoscape-fcose/src/fcose/cose.js` | `coseLayout`, `processChildrenList`, `processEdges`, `processConstraints`, option-to-constant transfer | Reached | **Exact** |
| `cytoscape-fcose/src/fcose/auxiliary.js` | `getTopMostNodes`, `relocateComponent`, `calcParentsWithoutChildren` | Reached | **Exact** |
| `cytoscape-fcose/src/fcose/auxiliary.js` | `connectComponents`, draft bounding boxes and packing helpers | Not reached because constraints disable packing | **Unsupported** |
| `cytoscape-fcose/src/fcose/spectral.js` | `spectralLayout`, sampling, Laplacian, power iteration, QR, SVD input | Not reached when `randomize=false` | **Unsupported**; `randomize=true` must return `UnsupportedFeature` until translated |
| `cose-base/src/ConstraintHandler.js` | initial coordinate extraction, DAG/component helpers, alignment enforcement, relative-placement enforcement, final coordinate assignment | Reached; Procrustes/SVD transform is skipped because incremental mode sets `TRANSFORM_ON_CONSTRAINT_HANDLING=false` | **Exact** for the reached branch |
| `cose-base/src/CoSELayout.js` | constructor, factories, `initParameters`, `layout`, `classicLayout`, `tick`, `runSpringEmbedder`, `moveNodes`, `initConstraintVariables`, `updateDisplacements`, `calculateNodesToApplyGravitationTo`, `calcRepulsionRange` | Reached | **Exact** |
| `cose-base/src/CoSELayout.js` | radial initialization, bendpoints, tree reduction/growth, tiling | Not reached: incremental + constraints, bends disabled, tile disabled | **Unsupported** |
| `cose-base/src/CoSENode.js` | `calculateDisplacement`, `propogateDisplacementToChildren`, `move` | Reached | **Exact** |
| `cose-base/src/CoSEEdge.js`, `CoSEGraph.js`, `CoSEGraphManager.js` | exact subclass constructors and graph factories | Reached | **Exact** |
| `cose-base/src/CoSEConstants.js` | proof/incremental/constraint constants used by the adapter and solver | Reached | **Exact** |
| `layout-base/src/Layout.js` | constructor state, graph factories, `checkLayoutSuccess`, `runLayout`, and `initParameters` portions reached by the synchronous headless call | Reached | **Exact** for the reached lifecycle |
| `layout-base/src/LGraphManager.js` | graph/edge add, all-node/all-edge caches, bounds, gravity list, root, ancestor checks, LCA, inclusion depths, invalid-edge removal | Reached | **Exact** |
| `layout-base/src/LGraph.js` | node/edge add, bounds, inclusion depth, estimated size, connectivity | Reached | **Exact** |
| `layout-base/src/LNode.js` | geometry accessors, edge/neighbour lookup, descendants, child count, estimated size, compound bounds | Reached | **Exact** |
| `layout-base/src/LEdge.js` | endpoints, inter-graph metadata, LCA metadata, graph-relative endpoint, `updateLength` | Reached | **Exact** |
| `layout-base/src/fd/FDLayout.js` | initialization, ideal lengths, spring/repulsion/gravity forces, convergence, child weights, FR grid and repulsion range hooks | Reached | **Exact** |
| `layout-base/src/fd/FDLayoutNode.js`, `FDLayoutEdge.js` | force/displacement/grid state and edge parameters | Reached | **Exact** |
| `layout-base/src/LayoutConstants.js`, `fd/FDLayoutConstants.js` | proof, incremental, force, cooling, grid, and geometry constants | Reached | **Exact** |
| `layout-base/src/util/PointD.js`, `DimensionD.js`, `RectangleD.js` | mutable geometry values and rectangle intersection/accessors | Reached | **Exact** |
| `layout-base/src/util/IGeometry.js` | `calcSeparationAmount`, `decideDirectionsForOverlappingNodes`, rectangle `getIntersection`, `getCardinalDirection` | Reached | **Exact** |
| `layout-base/src/util/IMath.js`, `Integer.js`, `LinkedList.js` | sign, sentinel values, and FIFO behavior | Reached | **Exact** where used; Kotlin collections replace container-only APIs |
| `layout-base/src/util/Matrix.js`, `SVD.js` | Procrustes transform and randomized spectral layout | Not reached on default incremental path | **Unsupported** |

`randomize=true` is rejected before layout dispatch. No fallback solver,
packing heuristic, or coordinate-specific correction runs behind that error.

## Cytoscape Geometry Boundary

Mermaid creates one fCoSE layout object and runs it twice. Kotlin mirrors that
lifecycle in `FcoseAdapter.runTwice`; each run starts a fresh Mulberry32 stream
and the second run starts from the first run's published node positions.

Cytoscape exposes three relevant geometry layers:

- `bodyBounds`: the node body or recursively computed compound body;
- `visualBounds`: `bodyBounds` expanded by labels and used while recursively
  sizing parent compounds;
- `outerVisualBounds`: the final published bounding box, one pixel larger per
  side than `visualBounds` for the reached headless renderer path.

For the second run, a compound center comes from its auto `bodyBounds`.
Published group extents come from `outerVisualBounds`. This distinction avoids
feeding an asymmetric label-expanded bounding-box center back into fCoSE.

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `packages/parser/src/language/architecture/arch.langium` | `69755c428b2409c874786f5e40c69f85f543496e8929b54f0af30fd97a6c345d` |
| `packages/parser/src/language/architecture/architecture.langium` | `08b6acbfa6f48b3dc6b590e3a5339a3fe554c394a0307d6cbfebb75c175b5147` |
| `packages/parser/src/language/architecture/tokenBuilder.ts` | `055ce9bc374b3aa944e0d393b817f5a5def15f6255a4bbf248991eed7168aebc` |
| `packages/parser/src/language/architecture/valueConverter.ts` | `7f11ebdd878c758d4a6e0ee7eaa5c813159760292db9932db4be4790d3069392` |
| `packages/mermaid/src/diagrams/architecture/architectureDb.ts` | `28416d74513bd16aa7cd86a80450fab72a17f14a16ec6570b00af5af840c22f7` |
| `packages/mermaid/src/diagrams/architecture/architectureDetector.ts` | `6c61635c3f8b9e532083adf465c13ca31d430ee939c7821b8bc18c4ea8b2ba6c` |
| `packages/mermaid/src/diagrams/architecture/architectureDiagram.ts` | `81a47a4f0b96b9547f9297a5df934e16f17fabab6d446a5307223862f87e1300` |
| `packages/mermaid/src/diagrams/architecture/architectureIcons.ts` | `8c32188821db16344fd5ad751d7877c78c654999c0b454c4d554edcde7eb9968` |
| `packages/mermaid/src/diagrams/architecture/architectureParser.ts` | `ec72a0d5b9f8527a7283e3018e794f049ac80b32398b9a426794a0b54b75b700` |
| `packages/mermaid/src/diagrams/architecture/architectureRenderer.ts` | `155ba1c94377ca6ba29124b492fcdadb0ca5aee2fa9438b8a665dad6c7c5ab65` |
| `packages/mermaid/src/diagrams/architecture/architectureSeed.ts` | `1a7c74a50cfbfdc92ec49476c96030c18ec00a1d893b28bc7d3ab7dc95571da6` |
| `packages/mermaid/src/diagrams/architecture/architectureStyles.ts` | `62a345924376a9b2e9b9ff044afbccf44fc2bb4b3ebbab20512221159509393e` |
| `packages/mermaid/src/diagrams/architecture/architectureTypes.ts` | `b18c2bbb5b196e636821fdb692e87b33bebcad3a46b6b4956d01dad363f0654c` |
| `packages/mermaid/src/diagrams/architecture/svgDraw.ts` | `31b828618aa76242c4f45349176797989971820884c5f66965f43fa031f95950` |
| `packages/mermaid/src/defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `packages/mermaid/src/config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `packages/mermaid/src/docs/syntax/architecture.md` | `c3d8b2ff1df5854814a17f523f39effa8b8dbf2894189f47f34efd79bbfb616d` |

### Layout Dependency Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `cytoscape-fcose/src/fcose/index.js` | `40c0f0c51959a9ccc7f1b39b3a9d25e46b6865f3cd281026821521baae66e2d6` |
| `cytoscape-fcose/src/fcose/cose.js` | `297f674e0b55a0c2dbc0a9c665414772dc8179fa5eb9d60431ef11089613cee7` |
| `cytoscape-fcose/src/fcose/auxiliary.js` | `c6470c57e258b379545aefa24a4369c8cec926c31864bfed010515c9311f7578` |
| `cytoscape-fcose/src/fcose/spectral.js` | `e76efa8c7e8d1dccc4baaec4eeaf509b8a517208a5f20bb0d6302b6d00897215` |
| `cose-base/src/ConstraintHandler.js` | `1cbb203d1e4079a52968bce322027a62cd0239801e650fbbd439639fe438999a` |
| `cose-base/src/CoSEConstants.js` | `47fe48b69db84d49d80664f638632ec20e0b751ec3324fa2ece96fdebe5645e5` |
| `cose-base/src/CoSEEdge.js` | `32ab9e074f92e35d80658244d9d7ab3fab91ee421b08a6e213f86641c2fac37e` |
| `cose-base/src/CoSEGraph.js` | `5dfc363115df512d93fd2e0ea9cf0e1c4fc9deae11d8b9ac2b9f58207f67e49b` |
| `cose-base/src/CoSEGraphManager.js` | `8a3a1d6e05abdb8d4b8867b89e431919373624448411ba7ee60656e37d08b2d5` |
| `cose-base/src/CoSELayout.js` | `5b4b3ff585665a50a2bc81682b046dbd1f9a2949c3f2390ce5e446d1896494fe` |
| `cose-base/src/CoSENode.js` | `b11c95643eefe241d3d3c51fd6baaefa30304ce470ea9f843d51ecaf1b680820` |
| `layout-base/src/Layout.js` | `60b52960899d8ff9bebaa42ef1566bab8e1d020653c9e69a76d8ab83e3e6e6b4` |
| `layout-base/src/LayoutConstants.js` | `d3edb1480609dd9617107cae290a43cb80edd395fdb488be9cd4b929a3d38651` |
| `layout-base/src/LEdge.js` | `04a69989b17776ab9e50d2694c7a0b164a602850a3f17893c6956c77a91756e0` |
| `layout-base/src/LGraph.js` | `e65aa685abafcbe3fea72d72ebe8f1e9c22e8c1588bcfd913f8df154ad765346` |
| `layout-base/src/LGraphManager.js` | `09436c5452073f5a9a3b462e5e7ce72565d7273f3b122d6287150465ea8dfd49` |
| `layout-base/src/LGraphObject.js` | `12dd3054bf21e646066d17ad2419de156f9d5914fdc8379da06b0fea646229f1` |
| `layout-base/src/LNode.js` | `fd9a420ea4f92719c25f4bebb68a51255a369ed1afb1ed0a4157db7a4856857b` |
| `layout-base/src/fd/FDLayout.js` | `00bb7c36dfc279a457ebfd35f5dfb7b04123419f5457e65cc85344361946ca45` |
| `layout-base/src/fd/FDLayoutConstants.js` | `365a2e54dc82be15703440a954f8d5dff6d0414e426acb444ae5f18b49e2510b` |
| `layout-base/src/fd/FDLayoutEdge.js` | `5aabdaaac5768e2980937cdc54fe45d3cac495f3bffdb71bd1f639f28984a543` |
| `layout-base/src/fd/FDLayoutNode.js` | `68cfd2ec4062eed71422c0f109627e29986ede13a4b5da1f34d7074608d57bb7` |
| `layout-base/src/util/DimensionD.js` | `4e3365f294a6bc592a977c5886a63320911058d0caa5a070820494aee0b70e01` |
| `layout-base/src/util/IGeometry.js` | `3fb8a7ddd916641755ef826412d26491647d2a7e94b7ab0775545ed692b99e02` |
| `layout-base/src/util/IMath.js` | `33f4f491ee9b483f0c7860276ab5c83b690bf4749698090a1658e3a6dfeef40d` |
| `layout-base/src/util/Integer.js` | `cd3e6ac417aafb85bb157c1c126530763c9c3e918dd32b7ca16bb91dadbc50db` |
| `layout-base/src/util/LinkedList.js` | `f35df80e755d850efdaac9b924ff57d33c8026b3a93957427b717d61bb947048` |
| `layout-base/src/util/Matrix.js` | `bec3df45809a9fb7bc42cb53307588e780296a1c7b56902d1994acb8bf57166a` |
| `layout-base/src/util/Point.js` | `70670e31d8cda1b2ad7d54fb649dcb02f16f73525a954f3f3f685006581c6ae2` |
| `layout-base/src/util/PointD.js` | `024202d658514fff01f18ec40d3a2d927664937df2c07cb2d6e8d75387a74aa4` |
| `layout-base/src/util/RectangleD.js` | `d175bf35848ca186321c146c80699d8fc6f6dc88ffd8a0c7c010cd7bfadb9d7f` |
| `layout-base/src/util/SVD.js` | `d06c9621bb69efd84181590ab0f5a83dd8fa4ce3885f31a4ff5e6663b368577e` |
| `layout-base/src/util/Transform.js` | `441a66aeae1257be23f02e44ba2a9b87f5d3bc52f5454a730362e4c2114da7e4` |

## Intentional Kotlin Adaptations

- The Langium-generated TypeScript parser is represented by a portable Kotlin
  scanner and parser. It preserves the grammar's token boundaries, AST
  population order, source locations, and validation behavior while returning
  `GMResult.Err` for expected failures.
- Mermaid delegates force integration to Cytoscape and
  `cytoscape-fcose` `2.2.0`. Kotlin translates the reached
  `randomize: false` fCoSE, `cose-base` `2.2.0`, and `layout-base` `2.0.1`
  stages. Cytoscape collections, events, style callbacks, and renderer caches
  are the platform boundary and become typed Kotlin inputs and geometry data.
- DOM and SVG measurement are replaced by `TextMetricProvider`. SVG groups,
  paths, polygons, labels, icons, and paint order become ordered SceneGraph
  elements rendered by Compose Canvas.
- The six Mermaid-provided Architecture icons are translated to matching
  SceneGraph paths and shapes. Non-built-in icon names remain external
  `SceneAsset` references resolved by the Compose layer.
- Pure geometry retains upstream floating-point semantics; parser,
  configuration, database, resource, and layout failures remain typed.

## Parity Gate

- All 6 active documentation fixtures are regenerated from the pinned source
  and rendered in JVM tests.
- Focused tests cover grammar and database state, declaration ordering,
  validation, disconnected components, groups, junctions, directional edges,
  arrows, alignment, icons, metadata, configuration, and deterministic seeds.
- Seven focused coordinate-oracle cases compare the Kotlin adapter with the
  pinned Cytoscape/fCoSE stack, including disconnected roots, nested compounds,
  mixed-axis constraints, label-sized bounds, and both layout runs.
- The replacement Web gate compares 13 independent production scenarios and
  256 coverage-driven same-source input cases against Mermaid.js `12.0.0`.
- Production geometry and detail pass `13/13`.
- The current 256-case Native/Official matrix reports
  `251 pass / 5 review / 0 fail`. The five reviews are genuine text-overlap
  findings in cases `042`, `178`, `186`, `242`, and `250`.
- Geometry has `0 fail / 0 review`, with width `0.990-1.090`, height
  `0.959-1.173`, and foreground ink `0.978-1.184`.
- These results cover the default path only. Architecture remains explicitly
  non-Stable while legal `randomize: true` input returns
  `UnsupportedFeature`.
- The production and all 16 matrix contact sheets are published and source-bound;
  current automated findings remain explicit.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Regenerate the documentation fixtures.
3. Diff every mapped grammar, detector, parser, database, type, seed, icon,
   renderer, style, configuration, and documentation file.
4. Check the pinned Cytoscape and fCoSE versions and translate changed layout
   constraints or solver behavior.
5. Translate changed behavior in the corresponding Kotlin method.
6. Run focused JVM tests and all platform build gates.
7. Capture and review all 256 Architecture Native/Official pairs.
8. Update this map, the capability matrix, and the public stability report.
