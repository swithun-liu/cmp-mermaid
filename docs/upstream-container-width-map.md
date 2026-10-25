# Container-width repair inventory

Baseline: Mermaid.js **12.0.0**. Established before changing production code.
Paths below are relative to `packages/mermaid/src`; the existing Gantt and
Mindmap maps cover the remaining stages.

| Order | Upstream file / function / stage | Kotlin boundary | Classification |
| ---: | --- | --- | --- |
| 1 | `mermaidAPI.ts -> render`, SVG parent creation; `diagrams/gantt/ganttRenderer.js -> draw`, `elem.parentElement.offsetWidth` | `MermaidDiagram`, `MermaidRenderContext.containerWidth` | **Platform adaptation**: bounded Compose content width in logical pixels supplies the browser-parent input; no viewport means unavailable |
| 2 | `ganttRenderer.js -> draw`, undefined parent width falls back to `1200` | `GanttLayout.buildScene` | **Exact**: absence, rather than a small or zero width, selects the fallback |
| 3 | `ganttRenderer.js -> draw`, defined `conf.useWidth` overrides parent width | `MermaidRenderOptions.ganttUseWidth`, preprocessing, `GanttLayout.buildScene` | **Exact / Platform adaptation**: nullable Kotlin configuration preserves undefined versus explicit values, including explicit `1200` |
| 4 | `ganttRenderer.js -> draw`, D3 `scaleTime().domain().rangeRound([0, w-leftPadding-rightPadding])` | `TimeScale` | **Exact / Platform adaptation**: interpolate and round the relative range before adding the left offset; a degenerate domain maps to the midpoint |
| 5 | `ganttRenderer.js -> drawRects`, text `x` and `class` width-dependent branches | `labelPlacement` | **Exact / Platform adaptation**: compare relative end coordinates with `textWidth + 1.5*leftPadding` before adding the rendering offset; retain inside, outside-left and outside-right branches |
| 6 | `diagrams/mindmap/mindmapRenderer.ts -> draw`; `rendering-util/render.ts -> getRegisteredLayoutAlgorithm` | existing Mindmap selector and source-config projection | **Platform adaptation**, unchanged: comparisons must align the effective layout; Native's independent default is CoSE |
| 7 | `diagrams/mindmap/styles.ts -> genSections`, `getStyles`, `genGradient`; node/edge `data-look` | existing `MindmapLayout` style projection | **Platform adaptation**, unchanged: classic Redux Color has a filled root and colored edges; Neo Redux Color has a background-filled root and dark edges |

The last two rows are investigation boundaries, not proposed fixes. Official
DOM probes with the same theme and explicit classic look produce the same
paint under Dagre and CoSE. Different effective `look` and layout values must
not be treated as evidence for changing CSS semantics.

Core callers without a parent width retain the `1200` fallback. A configured
width still wins over the parent. Compose acquires the parent width before
scene preparation and rebuilds the scene when that width changes. Scene
coordinates and text sizes remain logical pixels, independent of density.
Widths that cannot produce a finite, positive native time domain retain the
structured configuration failure; they are not silently enlarged.

This is a targeted repair inventory. It does not establish family completion
or Stable status.
