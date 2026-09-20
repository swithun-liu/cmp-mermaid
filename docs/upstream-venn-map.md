# Upstream Venn Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| @upsetjs/venn.js | `2.0.0` |
| fmin | `0.0.4` |
| Native renderer | Compose Multiplatform Canvas |

The production Venn path is pure Kotlin in `commonMain`. It does not execute
Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Parser And Database

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `venn/VennPlugin.kt` | `vennDetector.ts` and `vennDiagram.ts` | `venn-beta` detection and parser/layout orchestration |
| `venn/upstream/mermaid/VennParser.kt` | `parser/venn.jison` | Set, union, text, style, indentation, quoted identifiers, labels, numeric sizes, and structured parse failures |
| `venn/upstream/mermaid/VennTypes.kt` | `vennTypes.ts` | Subset, text-node, style-entry, and database contracts |
| `venn/upstream/mermaid/VennDb.kt` | `vennDB.ts` | Default sizes, sorted identifiers, known-set validation, indentation state, text normalization, styles, title, and accessibility metadata |

## Venn.js And Fmin Translation

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `venn/upstream/vennjs/VennGeometry.kt` | `@upsetjs/venn.js/src/circleintersection.js` | Circle overlap and distance, circle intersections, multi-circle intersection area and arcs, containment, and centers |
| `venn/upstream/vennjs/VennLayoutEngine.kt` | `@upsetjs/venn.js/src/layout.js` | Missing-area completion, greedy and constrained-MDS initialization, loss function, normalization, disjoint clusters, and scaling |
| `venn/upstream/vennjs/VennLayoutEngine.kt` | `@upsetjs/venn.js/src/diagram.js` | Area paths, text centers, exclusion constraints, circle margins, and stable layout projection |
| `venn/upstream/fmin/Fmin.kt` | `fmin/src/{bisect,nelderMead,conjugateGradient,linesearch,blas1}.js` | Bisection, Nelder-Mead, conjugate gradient, Wolfe line search, and vector operations used by Venn optimization |

## Rendering And Shared Integration

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `venn/VennLayout.kt` | `vennRenderer.ts` `draw`, `ensurePairwiseSubsets`, and `renderTextNodes` | Pairwise completion, layout invocation, circles and intersections, labels, free text, styles, debug guides, viewport, and sizing |
| `venn/VennLayout.kt` | `@upsetjs/venn.js/src/diagram.js` `wrapText` and intersection path helpers | Text wrapping, area paths, and center placement adapted to measured Compose text |
| `venn/VennLayout.kt` | `styles.ts` and Venn theme variables | Fill opacity, strokes, titles, set labels, text nodes, eight palette slots, and flat-theme fallback |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and `schemas/config.schema.yaml` | Typed Venn defaults, frontmatter/init overrides, merge precedence, and validation |
| `MermaidContract.kt` | Mermaid Venn configuration | Public typed Venn options |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `venn/parser/venn.jison` | `63b984882e29432bfcbb99083043d3118c103834a531199a962ef399ebee437f` |
| `venn/vennDB.ts` | `7a9ad9c379974d330545a0f55034d1747406f3c08aed50a4cfaefb954cdd478a` |
| `venn/vennRenderer.ts` | `0b3c2506945c6efc16f28413f53e9c028aeafe05b90966d8e17f1ed43a0dd56d` |
| `venn/styles.ts` | `3ffbe0fd9a7e12d00920d849517a744a84cce28cdc7341811d12fc3c7f533c02` |
| `venn/vennTypes.ts` | `0f818f86072e8fe52e695b4d57423e498685919f85baa038535f8a1041e8e6e9` |
| `venn/vennDetector.ts` | `71eccf1893facc85c178de4a3d7188b93645130d7d57809028265cf7e026f138` |
| `venn/vennDiagram.ts` | `efa2112e11bbc562cfa791bd36b45d887aaa8ce92886e48705e4ea2a7090ada3` |
| `venn.md` | `f12ed068bd3a6f7dd5acafe976570388a95f3c3d242c9e758be2e6128b3f447f` |
| `defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `@upsetjs/venn.js/src/circleintersection.js` | `821c42c116f5f411204bad897c3ae8d15b1770e5d05d700ea552cbf83fdef241` |
| `@upsetjs/venn.js/src/diagram.js` | `4184098f26683c849180e83a5f0617fa8f39bc08e0aa819eb479efa06dc5a7af` |
| `@upsetjs/venn.js/src/layout.js` | `42d5acdbafd01de614b7b7ec99161be710a550ecdd4a8e5f01873c6804bbee49` |
| `fmin/src/bisect.js` | `3ea385de95a919d3424f6e75eb2b79185c2534b4bd7651675510e36ac2820f9b` |
| `fmin/src/nelderMead.js` | `65aa6b8be024638019031ecf85783ed476a9146b46644bd392ef9544d6656f07` |
| `fmin/src/conjugateGradient.js` | `01c9d3f1a9f3e5f779a53fea482e2d7b072998c85848f4f3dc74ccb4faca91fd` |
| `fmin/src/linesearch.js` | `ccad457ae81a70953eee3491f2d9fa31f87eab5db28b32236d955f6f0f12ca9d` |
| `fmin/src/blas1.js` | `e51858992f657a5cc42bfeb4175dfd3c8f5f5879362d3b4f349428a50f6d0218` |
| `fmin/LICENSE` | `e4503e78185bff178d3ee91835f082d05771da1b3a2d795f17e03a40251bab77` |

## Intentional Kotlin Adaptations

- The Jison grammar is expressed as a deterministic Kotlin parser because
  production modules cannot execute generated JavaScript. Parse, validation,
  and layout failures return `GMResult.Err`.
- The generic Venn.js and fmin APIs are narrowed to Mermaid's exact invocation
  and retain JavaScript number semantics for finite optimization inputs.
- Multi-set unions synthesize pairwise overlap entries exactly at Mermaid's
  renderer boundary before entering the translated Venn.js layout.
- Browser DOM and SVG measurement are replaced by `TextMetricProvider` and
  SceneGraph geometry. Text centers and line wrapping are computed before
  Canvas painting.
- Compose has no SVG overflow viewport behavior, so painted bounds are
  normalized with the same padding used by the isolated Official reference.
- SVG percentage title coordinates are re-resolved after viewBox
  normalization; the SceneGraph translation preserves that upstream shift.
- Venn title bounds apply a local Compose/Trebuchet metric correction so the
  256-case matrix has no unresolved clipping mismatch without changing shared
  text rendering.

## Parity Gate

- Parser, database, circle geometry, Venn.js optimization, fmin routines,
  pairwise completion, configuration, styles, metadata, and structured-error
  tests pass.
- All four active examples extracted from Mermaid's Venn documentation render
  in the JVM test.
- A separate deterministic randomized test renders 256 Venn inputs twice and
  verifies finite, stable SceneGraphs.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- All 16 matrix contact sheets were manually reviewed. The final result is
  `216 automatic pass + 40 manual acceptance + 0 unresolved`.

## Upgrade Procedure

1. Update the Mermaid, `@upsetjs/venn.js`, and fmin versions, commits, hashes,
   and third-party notices.
2. Diff every mapped parser, database, optimization, geometry, renderer,
   style, detector, registration, configuration, and documentation file.
3. Translate only changed behavior in the corresponding Kotlin method.
4. Regenerate documentation and stability fixtures.
5. Run JVM, Android, Web, Desktop, and iOS gates.
6. Capture and review all 256 Venn Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
