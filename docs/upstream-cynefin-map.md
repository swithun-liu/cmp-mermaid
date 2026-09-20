# Upstream Cynefin Diagram Source Map

## Frozen Baseline

| Component | Version |
| --- | --- |
| Mermaid | `12.0.0` |
| Mermaid source commit | `98a0945418c76238f15df2afaddbba4272656c3b` |
| Native renderer | Compose Multiplatform Canvas |

The production Cynefin path is pure Kotlin in `commonMain`. It does not
execute Mermaid.js, use a WebView, or depend on a JavaScript runtime.

## Translation Map

| Kotlin source | Upstream source | Translation boundary |
| --- | --- | --- |
| `cynefin/CynefinPlugin.kt` | `cynefinDetector.ts` and `cynefinDiagram.ts` | `cynefin-beta` detection and parser/layout orchestration |
| `cynefin/upstream/mermaid/CynefinParser.kt` | `cynefin.langium`, `common.langium`, `valueConverter.ts`, and `cynefinParser.ts` | Headers, metadata, domains, quoted items, transitions, comments, source locations, and structured failures |
| `cynefin/upstream/mermaid/CynefinTypes.kt` | `types.ts` | Domain, item, and transition contracts |
| `cynefin/upstream/mermaid/CynefinDb.kt` | `cynefinDb.ts` | Domain replacement, transition filtering, metadata, and entity decoding |
| `cynefin/upstream/mermaid/CynefinBoundaries.kt` | `cynefinBoundaries.ts` | Mulberry32-compatible PRNG, string hashing, seed resolution, folds, horizontal boundary, cliff, and confusion ellipse |
| `cynefin/CynefinLayout.kt` | `cynefinRenderer.ts` | Fixed domain geometry, descriptions, badges, confusion overflow, transitions, title, draw order, and viewport |
| `cynefin/CynefinLayout.kt` | `styles.ts` | Backgrounds, strokes, text colors, font sizes, opacity, and dashed patterns |
| `MermaidPreprocessor.kt` | `defaultConfig.ts` and `config.type.ts` | Cynefin defaults, frontmatter/init overrides, merge precedence, and validation |
| `MermaidContract.kt` | Mermaid Cynefin configuration and theme types | Public typed options and theme block |

## Locked Upstream Hashes

| Upstream file | SHA-256 |
| --- | --- |
| `parser/language/cynefin/cynefin.langium` | `e39c321b5a98b7160270c4275d4dfc309769478fca6e8f93fa67973431b66881` |
| `parser/language/common/common.langium` | `3c861872811260396786ee50129795274d7ef4208d53fcc3251d15d32a788cd4` |
| `parser/language/common/valueConverter.ts` | `43b58df0bba9087c8b831cd53a12e8f1f85bf4a7352b388873d793325953ab37` |
| `cynefin/cynefinParser.ts` | `6a7777457e6b66fd0832f3215f809b42575af494dbf1d496badf9a13fbe04afe` |
| `cynefin/cynefinDb.ts` | `92363b9b8db48986cc06a8daf78732c1b274c65233613b0d4ae25f6b2f9fbe03` |
| `cynefin/cynefinRenderer.ts` | `db5e792e5268b420cf42e2399b219a3049af9060fd36ec222ada0cb302a0610b` |
| `cynefin/cynefinBoundaries.ts` | `1b1081452e450e2539d4f54810b668696ef9b9b59a3cebac7470f4008d72a6b5` |
| `cynefin/styles.ts` | `f36148e048c8a91e84c142d29ae8cf5ec5c1efb8054278674a5e4fba7a4941b7` |
| `cynefin/types.ts` | `fdca16360a7faac0253d59c4088aaa8a3494de58879e2aedd13a0bf71df0adb5` |
| `cynefin/cynefinDetector.ts` | `9386deb3bdba2c42071d542f98f723d7c47460acab80c1055ed481d0de687a46` |
| `cynefin/cynefinDiagram.ts` | `198b3c5528517fb73b7cf4460e3015117481d166e102a11c053148d8284b22d7` |
| `defaultConfig.ts` | `530c0df1c6225b193163e06a349ed8234b7d244c7122c7826b50a6be0d605ee4` |
| `config.type.ts` | `10696c90890c208dc1c180165c8891180d356b20dcfcf46333355be44278c17f` |
| `cynefin.md` | `5b0434fe85f0cef69d84e5c96d75ef13741fa77ca81f96e201748a084bc3b3c0` |

## Intentional Kotlin Adaptations

- The Langium grammar is expressed as a deterministic Kotlin parser. Parse,
  validation, and resource-limit failures return `GMResult.Err`.
- Mermaid's DOM entity boundary uses the existing platform-independent
  decoder before SceneGraph text projection.
- Upstream uses the generated SVG element ID when `seed` is absent or zero.
  Production SceneGraph generation has no DOM ID, so Kotlin hashes the
  normalized post-preprocessing source instead. Explicit non-zero seeds retain
  exact upstream precedence and Mulberry32 arithmetic. The adaptation keeps
  the fallback source-specific and deterministic across platforms.
- A partial nested `themeVariables.cynefin` override starts from Mermaid's
  default Cynefin block before applying supplied keys, matching upstream
  runtime merge behavior. With no nested override, the selected built-in
  theme's complete Cynefin block remains active.
- SVG groups, paths, ellipses, rectangles, markers, and text become ordered
  SceneGraph elements. Browser `getBBox()` text measurements use
  `TextMetricProvider`.
- The renderer's fixed SVG viewBox is unioned with content bounds and padded
  by 12 px to match the isolated Official reference normalization and prevent
  long intrinsic labels or zero-padding titles from being clipped.

## Parity Gate

- Official documentation fixtures, parser/database/layout tests, structured
  failures, configuration precedence, entity decoding, seed arithmetic, and
  portable text measurement pass on JVM.
- The replacement Web gate compares 13 independent production scenarios and
  256 deterministic same-source matrix cases against Mermaid.js `12.0.0`.
- Production geometry and detail pass 13/13 with
  `13 pass / 0 review / 0 fail`.
- Matrix geometry and detail pass 256/256 with
  `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix sheets were manually
  reviewed with no unresolved domain, boundary, transition, overflow, theme,
  label, clipping, or paint-order defect.

## Upgrade Procedure

1. Update the Mermaid version, source commit, hashes, and third-party notices.
2. Diff every mapped grammar, parser, database, boundary, renderer, style,
   detector, registration, configuration, and documentation file.
3. Translate changed behavior in the corresponding Kotlin method.
4. Regenerate official documentation and corpus fixtures.
5. Run focused JVM tests and all platform build gates.
6. Capture and review all 256 Cynefin Native/Official pairs.
7. Update this map, the capability matrix, and the public stability report.
