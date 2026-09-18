# Upstream Ishikawa Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Ishikawa path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `ishikawa/IshikawaPlugin.kt` | `ishikawaDetector.ts` and `ishikawaDiagram.ts` | `ishikawa`/`ishikawa-beta` detection and parser/layout orchestration |
| `ishikawa/upstream/mermaid/IshikawaParser.kt` | `parser/ishikawa.jison` | Header, comments, whitespace, indentation levels, statements, and structured failures |
| `ishikawa/upstream/mermaid/IshikawaTypes.kt` | `ishikawaTypes.ts` | Recursive effect/cause node contract |
| `ishikawa/upstream/mermaid/IshikawaDb.kt` | `ishikawaDb.ts` | Effect ownership, first-cause base level, relative indentation, stack projection, and sanitized labels |
| `ishikawa/IshikawaLayout.kt` | `ishikawaRenderer.ts` | Side statistics, spine allocation, fish head, alternating branches, flattened descendants, label boxes, arrows, wrapping, and padded viewport |
| `ishikawa/IshikawaLayout.kt` | `ishikawaStyles.ts` | Line widths, fills, strokes, text colors, font sizes, anchors, and paint order |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and Mermaid config types | `diagramPadding`/`useMaxWidth` defaults, frontmatter/init overrides, merge precedence, and validation |
| `MermaidContract.kt` | Mermaid Ishikawa configuration | Public typed Ishikawa options |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `ishikawa/parser/ishikawa.jison` | `d830768515552cb082bf94743930808b478800ae3491565fc6d00d35f66460d9` |
| `ishikawa/ishikawaDb.ts` | `b51081b9adadd1f042a950c415b1544b6ddf94f848dabe03d09a81a586e4c39a` |
| `ishikawa/ishikawaRenderer.ts` | `4ce750782e4a75601a35667c0383eb5b06e691cc77f22fc8e91114d94abd134a` |
| `ishikawa/ishikawaStyles.ts` | `2b2589e02c8631d5374f15245ad53eb36f7c197143369c7ca31f60618e3612a6` |
| `ishikawa/ishikawaTypes.ts` | `3c0dad55b038887a5794a67d17c9f471f33c1d3617ee7b6cbe7b3ba2b7cdd33e` |
| `ishikawa/ishikawaDetector.ts` | `564afd2eee5536fdeab268090352c66ef4297cf4c1aa6194fc5a7386490e8e92` |
| `ishikawa/ishikawaDiagram.ts` | `d864a85c35f78bfda2595749fe4d68fe89f13985ccef24c2570bb030d6658a49` |
| `ishikawa.md` | `7279caf0c50428cfbcd92366e6fd7e88d724a8902be545787376c559bff4c35d` |

## Intentional Kotlin Adaptations

- The Jison grammar is expressed as a deterministic Kotlin parser. Parse,
  validation, resource-limit, and layout failures return `GMResult.Err`.
- Mermaid's `DOMPurify` boundary uses the existing platform-independent HTML
  tokenizer. Entity references remain encoded before SceneGraph text
  projection, matching the upstream SVG renderer's `textContent` behavior.
- SVG `getBBox()` is replaced by `TextMetricProvider`. Explicit upstream
  character wrapping remains authoritative, including the head label's fixed
  `14px` glyph size with line spacing derived from global `fontSize`, and
  measurements use a finite Compose-compatible unwrapped width.
- SVG groups, markers, lines, paths, rectangles, and text become ordered
  SceneGraph elements. The rough.js `handDrawn` path is not approximated:
  the shared engine returns `MermaidError.UnsupportedFeature`.
- `configureSvgSize` and the padded SVG viewBox become intrinsic or
  responsive SceneGraph sizing with content-bound normalization.

## Parity Gate

- The official documentation fixture, parser/database/layout tests, structured
  failures, configuration precedence, entity preservation, and portable text
  measurement constraints pass on JVM.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry passes 13/13. Its detail result is
  `12 pass / 1 review / 0 fail`; the root-only review is the Native manifest's
  zero-length spine, which Official omits from visible geometry.
- Matrix geometry passes 256/256 and matrix detail is
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed with no unresolved hierarchy, branch-direction, fish-head, label,
  wrapping, theme, clipping, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Diff every mapped parser, database, renderer, style, detector,
   registration, configuration, and documentation file.
3. Translate changed behavior in the corresponding Kotlin method.
4. Regenerate official documentation and corpus fixtures.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Ishikawa Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
