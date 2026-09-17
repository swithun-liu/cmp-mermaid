# User Journey Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported User Journey diagram must preserve sections, task order, scores,
actors, actor colors, task markers, satisfaction faces, labels, titles, and
accessibility metadata closely enough that a side-by-side comparison does not
expose a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `journey`, frontmatter, directives, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `journey.jison` symbols, productions, LALR states, lexer rules, and conditions |
| Database | Supported | Section tracking, source-order tasks, JavaScript `Number()` score conversion, entity decoding, and alphabetical actor collection |
| Sections and tasks | Supported | Multiple sections, actorless tasks, repeated actors, decimal scores, and source-order task placement |
| Actor legend | Supported | Alphabetical actor order, Mermaid actor palette assignment, long-label measurement, and wrapping |
| Task actors | Supported | Single and multiple actor markers retain source order while resolving colors through the global actor ordering |
| Score rendering | Supported | Mermaid score-to-height formula plus happy, neutral, and sad face geometry |
| Text placement | Supported | Mermaid `fo`, `old`, and `tspan` strategies, including foreign-object-style wrapping and clipping |
| Themes and config | Supported | All 11 built-in themes, `fillType0..7`, `textColor`, Journey geometry, fonts, palettes, and typed Kotlin Journey theme overrides |
| Resource controls | Supported | Task count is bounded by host-owned `maxEdges`; invalid configuration returns a structured error |

Mermaid `12.0.0` removes unregistered `actor0..actor5` and `faceColor`
frontmatter variables during directive sanitization. CMP Mermaid matches that
behavior. Native callers can still provide actor and face colors through the
typed `MermaidTheme.journey` API.

## Intentional Native Boundaries

- Browser `foreignObject` task labels map to clipped Compose text with the
  same task-box bounds. Font metrics can differ by platform.
- A non-finite score retains its task but omits the face, keeping SceneGraph
  geometry finite instead of emitting invalid coordinates.
- SceneGraph output is static; the upstream Journey renderer defines no
  Journey-specific browser interaction contract to execute.

## Validation Corpus

- The example extracted from Mermaid's official User Journey documentation
  runs in JVM tests.
- Focused parser and layout tests cover sections, task data, actor ordering,
  score positions, face paths, accessibility, long actor labels, and all three
  text-placement strategies.
- 256 deterministic random legal Journey diagrams cover 1-7 sections, 1-9
  actors, actorless and multi-actor tasks, frontmatter, metadata, geometry
  options, and finite SceneGraph validation.
- 13 independent production scenarios cover all 16 declared Journey
  capability points.
- 256 same-source Native/Official visual cases were captured at `1200 x 900`.
  The replacement audit reports `237 pass / 19 manually reviewed / 0 fail`;
  all 256 pass geometry with width ratios `1.020-1.032`, height ratios
  `1.036-1.057`, and foreground-ink ratios `0.983-1.073`. The 19 reviews are
  benign platform-font line-segmentation differences for the complete
  `Regional Compliance Review Coordination Team` actor label. Both sides
  retain ten text elements with no clipping or overlap. All 16 contact sheets
  were manually reviewed.
- Android Emulator, iOS Simulator, Desktop, and Web load tests traverse the
  complete 236-case mixed corpus.

## Reference Workflow

Regenerate parser tables:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:journey-parser
```

Regenerate the documentation fixture:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:journey-doc-fixtures
```

Capture the 256-case Journey partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=journey \
OUTPUT_DIR=captures/local/journey-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
