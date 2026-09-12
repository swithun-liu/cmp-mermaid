# Pie Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Pie diagram must preserve source order, values, percentages, slice
filtering, colors, legends, titles, accessibility metadata, donut geometry,
and configuration closely enough that a side-by-side comparison does not
expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser interaction unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`; it is not
silently discarded or rendered as a different feature.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `pie`, optional `showData`, frontmatter, comments, title, accessibility metadata, and Pie configuration |
| Parser | Supported | Kotlin translation of Mermaid's `pie.langium` grammar, token rules, and value conversion |
| Labels | Supported | Single/double quotes, escapes, Unicode escapes, entities, inline comments, and insertion order |
| Values | Supported | Integers, decimals, zero, negative zero, structured errors for negative values, and Mermaid-compatible number rejection |
| Duplicate labels | Supported | The first value wins, matching `pieDb.ts` |
| Slice filtering | Supported | Slices below 1% are omitted, remain in the legend, retain their palette index, and visible slices are renormalized |
| Labels and legend | Supported | Rounded percentages, `showData`, `textPosition`, and `top`, `bottom`, `left`, `right`, or `center` legends |
| Donut charts | Supported | `donutHole` from 0 through 0.9, including full-circle ring geometry |
| Highlighting | Partial | A configured label is statically highlighted; `highlightSlice: hover` returns `UnsupportedFeature` |
| Themes and config | Supported | Twelve-color ordinal palette, palette cycling, Pie text/stroke/opacity variables, and all built-in Mermaid themes |
| Resource controls | Supported | Section count is bounded by host-owned `maxEdges` |

## Explicit Unsupported Boundary

`config.pie.highlightSlice: hover` returns
`MermaidError.UnsupportedFeature`. Native SceneGraph output has no browser
pointer-hover state, so silently replacing hover behavior with a static style
would change the feature's meaning.

## Validation Corpus

- Both examples extracted from Mermaid's official Pie documentation run in
  JVM tests.
- Parser and layout tests cover quoting, escapes, comments, metadata,
  duplicates, negative and zero values, one-percent filtering, input order,
  donut geometry, static highlighting, all five legend positions, themes,
  configuration, and structured unsupported errors.
- 256 deterministic random legal Pie diagrams cover 1-20 sections, zero,
  tiny, decimal, and highlighted values, `showData`, donut holes, text
  positions, and every legend position while checking finite geometry and
  retained legend counts.
- 20 curated gallery cases render identical source through Native Compose and
  Mermaid.js `12.0.0`.
- All 20 Native Android screenshots were compared side by side with 20
  Puppeteer-rendered official references.
- Core and Compose compile for JVM, Android, iOS Arm64, iOS Simulator Arm64,
  and iOS X64.

## Reference Workflow

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:pie-doc-fixtures
```

Render the 20 official references:

```bash
cd tools/official-reference
npm run render:pie-gallery
```

Capture the same demo ids with `tools/capture-android-audit.sh`, then generate
side-by-side sheets with `npm run render:pie-contact-sheet`. All screenshots
remain under ignored `captures/local/` paths.
