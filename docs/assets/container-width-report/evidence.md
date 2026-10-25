# Container-width evidence

Mermaid.js **12.0.0**, Native Compose JVM, identical source, effective layout,
look, Redux Color theme and bundled Arimo/Arial. Captures were regenerated
after the production repair.

**2 families / 3 unique sources / 9 configuration scenarios / 18 screenshots /
3 contact sheets. 0 Pass / 9 Review / 0 Fail.** All content checks pass.
Gantt width, rounded task geometry and label sides match; text rendering
remains Review. Mindmap paint and topology agree under the same configuration;
small shape, coordinate, endpoint and raster differences remain Review.

- [Source/configuration and screenshot manifest](visual-parity-manifest.json)
- [Detail, geometry, endpoints and per-edge topology audit](audit.json)
- [Raster diagnostics](raster.json)
- [Official capture](official.json)
- [Native capture](native.json)

The sheets show Official on the left and Native on the right. Preview scaling
does not change the original audit coordinates or screenshot pixels.

![Gantt parent widths](contact-sheet-1.png)

![Explicit widths and classic Dagre Mindmap](contact-sheet-2.png)

![Same-config Neo and CoSE Mindmap](contact-sheet-3.png)

| Scenario | Result | Official | Native |
| --- | --- | --- | --- |
| Gantt parent 344 | Review | [PNG](gantt-parent-344-official.png) | [PNG](gantt-parent-344-native.png) |
| Gantt parent 768 | Review | [PNG](gantt-parent-768-official.png) | [PNG](gantt-parent-768-native.png) |
| Gantt parent 1200 | Review | [PNG](gantt-parent-1200-official.png) | [PNG](gantt-parent-1200-native.png) |
| Gantt source useWidth 1200 | Review | [PNG](gantt-source-1200-official.png) | [PNG](gantt-source-1200-native.png) |
| Gantt host useWidth 900 | Review | [PNG](gantt-host-900-official.png) | [PNG](gantt-host-900-native.png) |
| Mindmap Dagre classic | Review | [PNG](mindmap-dagre-classic-official.png) | [PNG](mindmap-dagre-classic-native.png) |
| Mindmap Dagre Neo | Review | [PNG](mindmap-dagre-neo-official.png) | [PNG](mindmap-dagre-neo-native.png) |
| Mindmap CoSE classic | Review | [PNG](mindmap-cose-bilkent-classic-official.png) | [PNG](mindmap-cose-bilkent-classic-native.png) |
| Mindmap CoSE Neo | Review | [PNG](mindmap-cose-bilkent-neo-official.png) | [PNG](mindmap-cose-bilkent-neo-native.png) |
