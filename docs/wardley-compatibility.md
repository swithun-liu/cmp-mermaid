# Wardley Map Compatibility

## Current State

CMP Mermaid implements Mermaid.js `12.0.0` `wardley-beta` in pure Kotlin
Multiplatform and renders it through Compose Canvas. The production path does
not use Mermaid.js, a browser DOM, a WebView, or a JavaScript engine.

Supported behavior includes:

- anchors, components, coordinates, label offsets, and explicit canvas size;
- plain and dashed dependencies, flow arrows, bidirectional flow, and labels;
- build, buy, outsource, market, and inertia markers;
- evolution movement, custom stage names, and custom stage boundaries;
- pipelines and their ordered alternatives;
- notes, numbered annotations, accelerators, and deaccelerators;
- titles, accessibility metadata, comments, entities, and Unicode;
- caller-provided dimensions, spacing, grid visibility, and responsive sizing;
- source themes and Wardley theme variables.

Mermaid.js `12.0.0` declares `wardley-beta` configuration in its schema but
omits that key from `defaultConfig.configKeys`. Its directive sanitizer
therefore discards source frontmatter/directive layout settings. CMP Mermaid
matches that source behavior; callers can still provide the same settings
through `MermaidWardleyOptions`, matching Mermaid's `initialize()` path.

## Verification

- 16 official documentation cases render through the Native engine.
- 13 independent production scenarios cover all 28 source-visible capability
  points.
- Five additional release-candidate cases exercise combined real-world
  structures.
- The 256-case generated Native matrix renders with finite deterministic
  SceneGraphs.
- All 256 coverage-driven visual-input matrix sources render successfully
  through the isolated Mermaid.js `12.0.0` reference renderer.
- All 13 production pairs pass the replacement detail and geometry audits.
  Native/Official content ratios are width `1.008-1.015`, height
  `1.019-1.020`, and foreground ink `0.970-1.106`.
- The fresh 256-case coverage-driven input audit reports
  `256 pass / 0 review / 0 fail`; geometry has 0 hard failures with width
  `1.008-1.008`, height `1.019-1.020`, and foreground ink `1.016-1.095`.
- All 16 matrix contact sheets are published and source-bound; current automated
  findings remain explicit.
- Parser, layout, configuration, malformed-input, and resource-limit tests are
  included in the JVM suite.

Evidence:

- [production contact sheet](assets/stability-report/wardley-complex-corpus.png)
- [production detail](assets/stability-report/wardley-production-detail.json)
- [production geometry](assets/stability-report/wardley-production-geometry.json)
- [visual detail](assets/stability-report/wardley-visual-parity-detail.json)
- [visual geometry](assets/stability-report/wardley-visual-parity-geometry.json)
- [visual contact-sheet index](assets/stability-report/visual-parity-evidence.md)

## Status

Wardley Map has completed its per-family replacement gate. All current
classic-look family evidence gates pass; overall Mermaid `12.0.0`
compatibility remains **not Stable** because `look: handDrawn` is explicitly
unsupported.
