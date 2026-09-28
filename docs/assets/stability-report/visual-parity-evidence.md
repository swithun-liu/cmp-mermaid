# Large-Scale Native/Official Visual Evidence

This index contains 8,448 source cases and 16,896 screenshots from the
large-scale visual parity matrix. Every page shows the same Mermaid source on
the left in CMP Native and on the right in Mermaid.js 12.0.0.

The matrix contains 256 cases per supported diagram type. Case IDs, structural
seed IDs, label profiles, feature dimensions, source hashes, image hashes, and
capture sizes are recorded in
[`visual-parity-manifest.json`](visual-parity-manifest.json). Each type
combines at least 13 complex structural seeds with visible text and layout-pressure
profiles. The 256 sources per type are unique, but they are not presented as
256 unrelated topologies.

All 33 families completed the fresh 2026-09-28 replacement detail, geometry,
and manual contact-sheet gates. The accepted total is `8,448/8,448` pairs:
`7,429 automatic pass / 1,019 manually reviewed / 0 unresolved` across 528
contact sheets. The aggregate
[geometry report](visual-parity-geometry.json) has `0` failures, with width
ratios `0.930-1.276`, height ratios `0.852-1.236`, and foreground-ink ratios
`0.567-1.578`. Review findings are limited to the documented text-position,
text-segmentation, text-overlap, raster-mask, element-count, and paint-order
occlusion-ratio representation thresholds; manual review found no missing
semantic content, clipping defect, or incorrect visible paint order.

| Family | Pass | Review | Fail | Width ratio | Height ratio | Ink ratio |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Flowchart | 256 | 0 | 0 | `1.021-1.083` | `0.965-1.048` | `0.957-1.187` |
| Swimlanes | 196 | 60 | 0 | `1.008-1.136` | `0.953-1.070` | `0.963-1.521` |
| Architecture | 256 | 0 | 0 | `1.020-1.232` | `0.998-1.236` | `0.991-1.578` |
| C4 | 256 | 0 | 0 | `0.967-1.071` | `1.028-1.060` | `0.987-1.128` |
| Railroad | 256 | 0 | 0 | `1.024-1.116` | `1.023-1.146` | `1.048-1.338` |
| TreeView | 256 | 0 | 0 | `0.990-1.055` | `0.969-1.063` | `0.853-1.046` |
| Use Case | 72 | 184 | 0 | `1.036-1.160` | `0.971-1.115` | `1.043-1.316` |
| Cynefin | 256 | 0 | 0 | `1.013-1.032` | `1.009-1.019` | `1.020-1.049` |
| Agentflow | 256 | 0 | 0 | `1.040-1.145` | `0.947-1.100` | `0.894-1.244` |
| Block | 252 | 4 | 0 | `0.978-1.189` | `0.922-1.080` | `0.925-1.403` |
| Event Modeling | 0 | 256 | 0 | `1.025-1.037` | `1.008-1.063` | `0.995-1.126` |
| XY Chart | 236 | 20 | 0 | `1.002-1.024` | `0.988-1.033` | `0.895-1.136` |
| Quadrant Chart | 256 | 0 | 0 | `1.007-1.035` | `1.011-1.022` | `1.025-1.048` |
| Timeline | 256 | 0 | 0 | `1.026-1.053` | `1.038-1.087` | `1.052-1.136` |
| Sequence | 246 | 10 | 0 | `1.019-1.237` | `0.919-1.113` | `0.925-1.294` |
| Class | 256 | 0 | 0 | `1.025-1.184` | `0.916-1.056` | `0.776-1.199` |
| State | 256 | 0 | 0 | `0.959-1.131` | `0.963-1.089` | `0.984-1.382` |
| Entity Relationship | 196 | 60 | 0 | `1.010-1.139` | `0.980-1.089` | `0.567-1.253` |
| Gantt | 256 | 0 | 0 | `1.035-1.037` | `0.883-0.964` | `0.963-1.023` |
| Pie | 255 | 1 | 0 | `0.992-1.050` | `0.994-1.019` | `0.993-1.042` |
| User Journey | 237 | 19 | 0 | `1.020-1.032` | `1.041-1.061` | `0.983-1.073` |
| Requirement | 252 | 4 | 0 | `1.013-1.072` | `1.004-1.053` | `0.974-1.148` |
| Git Graph | 235 | 21 | 0 | `1.035-1.141` | `1.032-1.145` | `1.035-1.421` |
| Mindmap | 153 | 103 | 0 | `0.930-1.276` | `0.877-1.104` | `0.794-1.277` |
| Packet | 256 | 0 | 0 | `1.011-1.015` | `1.003-1.065` | `0.977-1.039` |
| Radar | 256 | 0 | 0 | `1.004-1.036` | `1.004-1.020` | `1.006-1.037` |
| Sankey | 256 | 0 | 0 | `1.043-1.053` | `1.026-1.053` | `1.053-1.112` |
| Treemap | 19 | 237 | 0 | `1.033-1.076` | `1.029-1.039` | `0.945-1.077` |
| Venn | 216 | 40 | 0 | `0.995-1.026` | `0.990-1.022` | `0.989-1.048` |
| Ishikawa | 256 | 0 | 0 | `1.046-1.181` | `1.017-1.116` | `1.028-1.313` |
| Kanban | 256 | 0 | 0 | `1.036-1.089` | `0.852-1.088` | `0.992-1.105` |
| Wardley Map | 256 | 0 | 0 | `0.999-1.016` | `1.016-1.019` | `0.960-1.106` |
| ZenUML | 256 | 0 | 0 | `1.031-1.101` | `1.025-1.102` | `0.970-1.264` |

<details>
<summary><strong>Flowchart - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.021-1.083`, height `0.965-1.048`, foreground ink `0.957-1.187`. All 16 pages below were manually reviewed.

![Flowchart visual parity page 01](flowchart-visual-parity-01.jpg)

![Flowchart visual parity page 02](flowchart-visual-parity-02.jpg)

![Flowchart visual parity page 03](flowchart-visual-parity-03.jpg)

![Flowchart visual parity page 04](flowchart-visual-parity-04.jpg)

![Flowchart visual parity page 05](flowchart-visual-parity-05.jpg)

![Flowchart visual parity page 06](flowchart-visual-parity-06.jpg)

![Flowchart visual parity page 07](flowchart-visual-parity-07.jpg)

![Flowchart visual parity page 08](flowchart-visual-parity-08.jpg)

![Flowchart visual parity page 09](flowchart-visual-parity-09.jpg)

![Flowchart visual parity page 10](flowchart-visual-parity-10.jpg)

![Flowchart visual parity page 11](flowchart-visual-parity-11.jpg)

![Flowchart visual parity page 12](flowchart-visual-parity-12.jpg)

![Flowchart visual parity page 13](flowchart-visual-parity-13.jpg)

![Flowchart visual parity page 14](flowchart-visual-parity-14.jpg)

![Flowchart visual parity page 15](flowchart-visual-parity-15.jpg)

![Flowchart visual parity page 16](flowchart-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Swimlanes - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `196 pass / 60 review / 0 fail`. The 60 reviews contain only text-position findings along semantically equivalent orthogonal routes. Geometry ratios: width `1.008-1.136`, height `0.953-1.070`, foreground ink `0.963-1.521`. Raw [detail](swimlanes-visual-parity-detail.json) and [geometry](swimlanes-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Swimlanes visual parity page 01](swimlanes-visual-parity-01.jpg)

![Swimlanes visual parity page 02](swimlanes-visual-parity-02.jpg)

![Swimlanes visual parity page 03](swimlanes-visual-parity-03.jpg)

![Swimlanes visual parity page 04](swimlanes-visual-parity-04.jpg)

![Swimlanes visual parity page 05](swimlanes-visual-parity-05.jpg)

![Swimlanes visual parity page 06](swimlanes-visual-parity-06.jpg)

![Swimlanes visual parity page 07](swimlanes-visual-parity-07.jpg)

![Swimlanes visual parity page 08](swimlanes-visual-parity-08.jpg)

![Swimlanes visual parity page 09](swimlanes-visual-parity-09.jpg)

![Swimlanes visual parity page 10](swimlanes-visual-parity-10.jpg)

![Swimlanes visual parity page 11](swimlanes-visual-parity-11.jpg)

![Swimlanes visual parity page 12](swimlanes-visual-parity-12.jpg)

![Swimlanes visual parity page 13](swimlanes-visual-parity-13.jpg)

![Swimlanes visual parity page 14](swimlanes-visual-parity-14.jpg)

![Swimlanes visual parity page 15](swimlanes-visual-parity-15.jpg)

![Swimlanes visual parity page 16](swimlanes-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Architecture - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.020-1.232`, height `0.998-1.236`, foreground ink `0.991-1.578`. Raw [detail](architecture-visual-parity-detail.json) and [geometry](architecture-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Architecture visual parity page 01](architecture-visual-parity-01.jpg)

![Architecture visual parity page 02](architecture-visual-parity-02.jpg)

![Architecture visual parity page 03](architecture-visual-parity-03.jpg)

![Architecture visual parity page 04](architecture-visual-parity-04.jpg)

![Architecture visual parity page 05](architecture-visual-parity-05.jpg)

![Architecture visual parity page 06](architecture-visual-parity-06.jpg)

![Architecture visual parity page 07](architecture-visual-parity-07.jpg)

![Architecture visual parity page 08](architecture-visual-parity-08.jpg)

![Architecture visual parity page 09](architecture-visual-parity-09.jpg)

![Architecture visual parity page 10](architecture-visual-parity-10.jpg)

![Architecture visual parity page 11](architecture-visual-parity-11.jpg)

![Architecture visual parity page 12](architecture-visual-parity-12.jpg)

![Architecture visual parity page 13](architecture-visual-parity-13.jpg)

![Architecture visual parity page 14](architecture-visual-parity-14.jpg)

![Architecture visual parity page 15](architecture-visual-parity-15.jpg)

![Architecture visual parity page 16](architecture-visual-parity-16.jpg)

</details>

<details>
<summary><strong>C4 - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `0.967-1.071`, height `1.028-1.060`, foreground ink `0.987-1.128`. Raw [detail](c4-visual-parity-detail.json) and [geometry](c4-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![C4 visual parity page 01](c4-visual-parity-01.jpg)

![C4 visual parity page 02](c4-visual-parity-02.jpg)

![C4 visual parity page 03](c4-visual-parity-03.jpg)

![C4 visual parity page 04](c4-visual-parity-04.jpg)

![C4 visual parity page 05](c4-visual-parity-05.jpg)

![C4 visual parity page 06](c4-visual-parity-06.jpg)

![C4 visual parity page 07](c4-visual-parity-07.jpg)

![C4 visual parity page 08](c4-visual-parity-08.jpg)

![C4 visual parity page 09](c4-visual-parity-09.jpg)

![C4 visual parity page 10](c4-visual-parity-10.jpg)

![C4 visual parity page 11](c4-visual-parity-11.jpg)

![C4 visual parity page 12](c4-visual-parity-12.jpg)

![C4 visual parity page 13](c4-visual-parity-13.jpg)

![C4 visual parity page 14](c4-visual-parity-14.jpg)

![C4 visual parity page 15](c4-visual-parity-15.jpg)

![C4 visual parity page 16](c4-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Railroad - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.024-1.116`, height `1.023-1.146`, foreground ink `1.048-1.338`. Raw [detail](railroad-visual-parity-detail.json) and [geometry](railroad-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Railroad visual parity page 01](railroad-visual-parity-01.jpg)

![Railroad visual parity page 02](railroad-visual-parity-02.jpg)

![Railroad visual parity page 03](railroad-visual-parity-03.jpg)

![Railroad visual parity page 04](railroad-visual-parity-04.jpg)

![Railroad visual parity page 05](railroad-visual-parity-05.jpg)

![Railroad visual parity page 06](railroad-visual-parity-06.jpg)

![Railroad visual parity page 07](railroad-visual-parity-07.jpg)

![Railroad visual parity page 08](railroad-visual-parity-08.jpg)

![Railroad visual parity page 09](railroad-visual-parity-09.jpg)

![Railroad visual parity page 10](railroad-visual-parity-10.jpg)

![Railroad visual parity page 11](railroad-visual-parity-11.jpg)

![Railroad visual parity page 12](railroad-visual-parity-12.jpg)

![Railroad visual parity page 13](railroad-visual-parity-13.jpg)

![Railroad visual parity page 14](railroad-visual-parity-14.jpg)

![Railroad visual parity page 15](railroad-visual-parity-15.jpg)

![Railroad visual parity page 16](railroad-visual-parity-16.jpg)

</details>

<details>
<summary><strong>TreeView - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `0.990-1.055`, height `0.969-1.063`, foreground ink `0.853-1.046`. Raw [detail](treeview-visual-parity-detail.json) and [geometry](treeview-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![TreeView visual parity page 01](treeview-visual-parity-01.jpg)

![TreeView visual parity page 02](treeview-visual-parity-02.jpg)

![TreeView visual parity page 03](treeview-visual-parity-03.jpg)

![TreeView visual parity page 04](treeview-visual-parity-04.jpg)

![TreeView visual parity page 05](treeview-visual-parity-05.jpg)

![TreeView visual parity page 06](treeview-visual-parity-06.jpg)

![TreeView visual parity page 07](treeview-visual-parity-07.jpg)

![TreeView visual parity page 08](treeview-visual-parity-08.jpg)

![TreeView visual parity page 09](treeview-visual-parity-09.jpg)

![TreeView visual parity page 10](treeview-visual-parity-10.jpg)

![TreeView visual parity page 11](treeview-visual-parity-11.jpg)

![TreeView visual parity page 12](treeview-visual-parity-12.jpg)

![TreeView visual parity page 13](treeview-visual-parity-13.jpg)

![TreeView visual parity page 14](treeview-visual-parity-14.jpg)

![TreeView visual parity page 15](treeview-visual-parity-15.jpg)

![TreeView visual parity page 16](treeview-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Use Case - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `72 pass / 184 review / 0 fail`. The 184 reviews are element-count or text-segmentation representation differences with complete actors, use cases, relationships, labels, styles, clipping, and paint order. Geometry ratios: width `1.036-1.160`, height `0.971-1.115`, foreground ink `1.043-1.316`. Raw [detail](usecase-visual-parity-detail.json) and [geometry](usecase-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Use Case visual parity page 01](usecase-visual-parity-01.jpg)

![Use Case visual parity page 02](usecase-visual-parity-02.jpg)

![Use Case visual parity page 03](usecase-visual-parity-03.jpg)

![Use Case visual parity page 04](usecase-visual-parity-04.jpg)

![Use Case visual parity page 05](usecase-visual-parity-05.jpg)

![Use Case visual parity page 06](usecase-visual-parity-06.jpg)

![Use Case visual parity page 07](usecase-visual-parity-07.jpg)

![Use Case visual parity page 08](usecase-visual-parity-08.jpg)

![Use Case visual parity page 09](usecase-visual-parity-09.jpg)

![Use Case visual parity page 10](usecase-visual-parity-10.jpg)

![Use Case visual parity page 11](usecase-visual-parity-11.jpg)

![Use Case visual parity page 12](usecase-visual-parity-12.jpg)

![Use Case visual parity page 13](usecase-visual-parity-13.jpg)

![Use Case visual parity page 14](usecase-visual-parity-14.jpg)

![Use Case visual parity page 15](usecase-visual-parity-15.jpg)

![Use Case visual parity page 16](usecase-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Cynefin - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.013-1.032`, height `1.009-1.019`, foreground ink `1.020-1.049`. Raw [detail](cynefin-visual-parity-detail.json) and [geometry](cynefin-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Cynefin visual parity page 01](cynefin-visual-parity-01.jpg)

![Cynefin visual parity page 02](cynefin-visual-parity-02.jpg)

![Cynefin visual parity page 03](cynefin-visual-parity-03.jpg)

![Cynefin visual parity page 04](cynefin-visual-parity-04.jpg)

![Cynefin visual parity page 05](cynefin-visual-parity-05.jpg)

![Cynefin visual parity page 06](cynefin-visual-parity-06.jpg)

![Cynefin visual parity page 07](cynefin-visual-parity-07.jpg)

![Cynefin visual parity page 08](cynefin-visual-parity-08.jpg)

![Cynefin visual parity page 09](cynefin-visual-parity-09.jpg)

![Cynefin visual parity page 10](cynefin-visual-parity-10.jpg)

![Cynefin visual parity page 11](cynefin-visual-parity-11.jpg)

![Cynefin visual parity page 12](cynefin-visual-parity-12.jpg)

![Cynefin visual parity page 13](cynefin-visual-parity-13.jpg)

![Cynefin visual parity page 14](cynefin-visual-parity-14.jpg)

![Cynefin visual parity page 15](cynefin-visual-parity-15.jpg)

![Cynefin visual parity page 16](cynefin-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Agentflow - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.040-1.145`, height `0.947-1.100`, foreground ink `0.894-1.244`. Raw [detail](agentflow-visual-parity-detail.json) and [geometry](agentflow-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Agentflow visual parity page 01](agentflow-visual-parity-01.jpg)

![Agentflow visual parity page 02](agentflow-visual-parity-02.jpg)

![Agentflow visual parity page 03](agentflow-visual-parity-03.jpg)

![Agentflow visual parity page 04](agentflow-visual-parity-04.jpg)

![Agentflow visual parity page 05](agentflow-visual-parity-05.jpg)

![Agentflow visual parity page 06](agentflow-visual-parity-06.jpg)

![Agentflow visual parity page 07](agentflow-visual-parity-07.jpg)

![Agentflow visual parity page 08](agentflow-visual-parity-08.jpg)

![Agentflow visual parity page 09](agentflow-visual-parity-09.jpg)

![Agentflow visual parity page 10](agentflow-visual-parity-10.jpg)

![Agentflow visual parity page 11](agentflow-visual-parity-11.jpg)

![Agentflow visual parity page 12](agentflow-visual-parity-12.jpg)

![Agentflow visual parity page 13](agentflow-visual-parity-13.jpg)

![Agentflow visual parity page 14](agentflow-visual-parity-14.jpg)

![Agentflow visual parity page 15](agentflow-visual-parity-15.jpg)

![Agentflow visual parity page 16](agentflow-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Block - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `252 pass / 4 review / 0 fail`. The four reviews are paint-order occlusion-ratio threshold findings on intentional overlaps; visible shapes, labels, edges, markers, clipping, and paint order remain complete. Geometry ratios: width `0.978-1.189`, height `0.922-1.080`, foreground ink `0.925-1.403`. Raw [detail](block-visual-parity-detail.json) and [geometry](block-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Block visual parity page 01](block-visual-parity-01.jpg)

![Block visual parity page 02](block-visual-parity-02.jpg)

![Block visual parity page 03](block-visual-parity-03.jpg)

![Block visual parity page 04](block-visual-parity-04.jpg)

![Block visual parity page 05](block-visual-parity-05.jpg)

![Block visual parity page 06](block-visual-parity-06.jpg)

![Block visual parity page 07](block-visual-parity-07.jpg)

![Block visual parity page 08](block-visual-parity-08.jpg)

![Block visual parity page 09](block-visual-parity-09.jpg)

![Block visual parity page 10](block-visual-parity-10.jpg)

![Block visual parity page 11](block-visual-parity-11.jpg)

![Block visual parity page 12](block-visual-parity-12.jpg)

![Block visual parity page 13](block-visual-parity-13.jpg)

![Block visual parity page 14](block-visual-parity-14.jpg)

![Block visual parity page 15](block-visual-parity-15.jpg)

![Block visual parity page 16](block-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Event Modeling - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `0 pass / 256 review / 0 fail`. All 256 reviews contain only the documented title/payload text-segmentation difference between one Official foreignObject and separate Native title and payload elements. Geometry ratios: width `1.025-1.037`, height `1.008-1.063`, foreground ink `0.995-1.126`. Raw [detail](eventmodeling-visual-parity-detail.json) and [geometry](eventmodeling-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Event Modeling visual parity page 01](eventmodeling-visual-parity-01.jpg)

![Event Modeling visual parity page 02](eventmodeling-visual-parity-02.jpg)

![Event Modeling visual parity page 03](eventmodeling-visual-parity-03.jpg)

![Event Modeling visual parity page 04](eventmodeling-visual-parity-04.jpg)

![Event Modeling visual parity page 05](eventmodeling-visual-parity-05.jpg)

![Event Modeling visual parity page 06](eventmodeling-visual-parity-06.jpg)

![Event Modeling visual parity page 07](eventmodeling-visual-parity-07.jpg)

![Event Modeling visual parity page 08](eventmodeling-visual-parity-08.jpg)

![Event Modeling visual parity page 09](eventmodeling-visual-parity-09.jpg)

![Event Modeling visual parity page 10](eventmodeling-visual-parity-10.jpg)

![Event Modeling visual parity page 11](eventmodeling-visual-parity-11.jpg)

![Event Modeling visual parity page 12](eventmodeling-visual-parity-12.jpg)

![Event Modeling visual parity page 13](eventmodeling-visual-parity-13.jpg)

![Event Modeling visual parity page 14](eventmodeling-visual-parity-14.jpg)

![Event Modeling visual parity page 15](eventmodeling-visual-parity-15.jpg)

![Event Modeling visual parity page 16](eventmodeling-visual-parity-16.jpg)

</details>

<details>
<summary><strong>XY Chart - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `236 pass / 20 review / 0 fail`. The 20 reviews are paint-order occlusion-ratio threshold findings on intentional chart layering; bars, lines, axes, labels, legends, clipping, and visible paint order remain complete. Geometry ratios: width `1.002-1.024`, height `0.988-1.033`, foreground ink `0.895-1.136`. All 16 pages below were manually reviewed.

![XY Chart visual parity page 01](xychart-visual-parity-01.jpg)

![XY Chart visual parity page 02](xychart-visual-parity-02.jpg)

![XY Chart visual parity page 03](xychart-visual-parity-03.jpg)

![XY Chart visual parity page 04](xychart-visual-parity-04.jpg)

![XY Chart visual parity page 05](xychart-visual-parity-05.jpg)

![XY Chart visual parity page 06](xychart-visual-parity-06.jpg)

![XY Chart visual parity page 07](xychart-visual-parity-07.jpg)

![XY Chart visual parity page 08](xychart-visual-parity-08.jpg)

![XY Chart visual parity page 09](xychart-visual-parity-09.jpg)

![XY Chart visual parity page 10](xychart-visual-parity-10.jpg)

![XY Chart visual parity page 11](xychart-visual-parity-11.jpg)

![XY Chart visual parity page 12](xychart-visual-parity-12.jpg)

![XY Chart visual parity page 13](xychart-visual-parity-13.jpg)

![XY Chart visual parity page 14](xychart-visual-parity-14.jpg)

![XY Chart visual parity page 15](xychart-visual-parity-15.jpg)

![XY Chart visual parity page 16](xychart-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Quadrant Chart - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.007-1.035`, height `1.011-1.022`, foreground ink `1.025-1.048`. All 16 pages below were manually reviewed.

![Quadrant Chart visual parity page 01](quadrant-visual-parity-01.jpg)

![Quadrant Chart visual parity page 02](quadrant-visual-parity-02.jpg)

![Quadrant Chart visual parity page 03](quadrant-visual-parity-03.jpg)

![Quadrant Chart visual parity page 04](quadrant-visual-parity-04.jpg)

![Quadrant Chart visual parity page 05](quadrant-visual-parity-05.jpg)

![Quadrant Chart visual parity page 06](quadrant-visual-parity-06.jpg)

![Quadrant Chart visual parity page 07](quadrant-visual-parity-07.jpg)

![Quadrant Chart visual parity page 08](quadrant-visual-parity-08.jpg)

![Quadrant Chart visual parity page 09](quadrant-visual-parity-09.jpg)

![Quadrant Chart visual parity page 10](quadrant-visual-parity-10.jpg)

![Quadrant Chart visual parity page 11](quadrant-visual-parity-11.jpg)

![Quadrant Chart visual parity page 12](quadrant-visual-parity-12.jpg)

![Quadrant Chart visual parity page 13](quadrant-visual-parity-13.jpg)

![Quadrant Chart visual parity page 14](quadrant-visual-parity-14.jpg)

![Quadrant Chart visual parity page 15](quadrant-visual-parity-15.jpg)

![Quadrant Chart visual parity page 16](quadrant-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Timeline - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.026-1.053`, height `1.038-1.087`, foreground ink `1.052-1.136`. All 16 pages below were manually reviewed.

![Timeline visual parity page 01](timeline-visual-parity-01.jpg)

![Timeline visual parity page 02](timeline-visual-parity-02.jpg)

![Timeline visual parity page 03](timeline-visual-parity-03.jpg)

![Timeline visual parity page 04](timeline-visual-parity-04.jpg)

![Timeline visual parity page 05](timeline-visual-parity-05.jpg)

![Timeline visual parity page 06](timeline-visual-parity-06.jpg)

![Timeline visual parity page 07](timeline-visual-parity-07.jpg)

![Timeline visual parity page 08](timeline-visual-parity-08.jpg)

![Timeline visual parity page 09](timeline-visual-parity-09.jpg)

![Timeline visual parity page 10](timeline-visual-parity-10.jpg)

![Timeline visual parity page 11](timeline-visual-parity-11.jpg)

![Timeline visual parity page 12](timeline-visual-parity-12.jpg)

![Timeline visual parity page 13](timeline-visual-parity-13.jpg)

![Timeline visual parity page 14](timeline-visual-parity-14.jpg)

![Timeline visual parity page 15](timeline-visual-parity-15.jpg)

![Timeline visual parity page 16](timeline-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Sequence - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `246 pass / 10 review / 0 fail`. The ten reviews are text-segmentation representation differences; participants, messages, notes, control regions, labels, clipping, and paint order remain complete. Geometry ratios: width `1.019-1.237`, height `0.919-1.113`, foreground ink `0.925-1.294`. All 16 pages below were manually reviewed.

![Sequence visual parity page 01](sequence-visual-parity-01.jpg)

![Sequence visual parity page 02](sequence-visual-parity-02.jpg)

![Sequence visual parity page 03](sequence-visual-parity-03.jpg)

![Sequence visual parity page 04](sequence-visual-parity-04.jpg)

![Sequence visual parity page 05](sequence-visual-parity-05.jpg)

![Sequence visual parity page 06](sequence-visual-parity-06.jpg)

![Sequence visual parity page 07](sequence-visual-parity-07.jpg)

![Sequence visual parity page 08](sequence-visual-parity-08.jpg)

![Sequence visual parity page 09](sequence-visual-parity-09.jpg)

![Sequence visual parity page 10](sequence-visual-parity-10.jpg)

![Sequence visual parity page 11](sequence-visual-parity-11.jpg)

![Sequence visual parity page 12](sequence-visual-parity-12.jpg)

![Sequence visual parity page 13](sequence-visual-parity-13.jpg)

![Sequence visual parity page 14](sequence-visual-parity-14.jpg)

![Sequence visual parity page 15](sequence-visual-parity-15.jpg)

![Sequence visual parity page 16](sequence-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Class - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.025-1.184`, height `0.916-1.056`, foreground ink `0.776-1.199`. All 16 pages below were manually reviewed.

![Class visual parity page 01](class-visual-parity-01.jpg)

![Class visual parity page 02](class-visual-parity-02.jpg)

![Class visual parity page 03](class-visual-parity-03.jpg)

![Class visual parity page 04](class-visual-parity-04.jpg)

![Class visual parity page 05](class-visual-parity-05.jpg)

![Class visual parity page 06](class-visual-parity-06.jpg)

![Class visual parity page 07](class-visual-parity-07.jpg)

![Class visual parity page 08](class-visual-parity-08.jpg)

![Class visual parity page 09](class-visual-parity-09.jpg)

![Class visual parity page 10](class-visual-parity-10.jpg)

![Class visual parity page 11](class-visual-parity-11.jpg)

![Class visual parity page 12](class-visual-parity-12.jpg)

![Class visual parity page 13](class-visual-parity-13.jpg)

![Class visual parity page 14](class-visual-parity-14.jpg)

![Class visual parity page 15](class-visual-parity-15.jpg)

![Class visual parity page 16](class-visual-parity-16.jpg)

</details>

<details>
<summary><strong>State - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `0.959-1.131`, height `0.963-1.089`, foreground ink `0.984-1.382`. All 16 pages below were manually reviewed.

![State visual parity page 01](state-visual-parity-01.jpg)

![State visual parity page 02](state-visual-parity-02.jpg)

![State visual parity page 03](state-visual-parity-03.jpg)

![State visual parity page 04](state-visual-parity-04.jpg)

![State visual parity page 05](state-visual-parity-05.jpg)

![State visual parity page 06](state-visual-parity-06.jpg)

![State visual parity page 07](state-visual-parity-07.jpg)

![State visual parity page 08](state-visual-parity-08.jpg)

![State visual parity page 09](state-visual-parity-09.jpg)

![State visual parity page 10](state-visual-parity-10.jpg)

![State visual parity page 11](state-visual-parity-11.jpg)

![State visual parity page 12](state-visual-parity-12.jpg)

![State visual parity page 13](state-visual-parity-13.jpg)

![State visual parity page 14](state-visual-parity-14.jpg)

![State visual parity page 15](state-visual-parity-15.jpg)

![State visual parity page 16](state-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Entity Relationship - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `196 pass / 60 review / 0 fail`. The 60 reviews contain only text-position findings on large repeated structures; all text, relationships, markers, clipping, overlap, and paint order remain complete. Geometry ratios: width `1.010-1.139`, height `0.980-1.089`, foreground ink `0.567-1.253`. All 16 pages below were manually reviewed.

![Entity Relationship visual parity page 01](er-visual-parity-01.jpg)

![Entity Relationship visual parity page 02](er-visual-parity-02.jpg)

![Entity Relationship visual parity page 03](er-visual-parity-03.jpg)

![Entity Relationship visual parity page 04](er-visual-parity-04.jpg)

![Entity Relationship visual parity page 05](er-visual-parity-05.jpg)

![Entity Relationship visual parity page 06](er-visual-parity-06.jpg)

![Entity Relationship visual parity page 07](er-visual-parity-07.jpg)

![Entity Relationship visual parity page 08](er-visual-parity-08.jpg)

![Entity Relationship visual parity page 09](er-visual-parity-09.jpg)

![Entity Relationship visual parity page 10](er-visual-parity-10.jpg)

![Entity Relationship visual parity page 11](er-visual-parity-11.jpg)

![Entity Relationship visual parity page 12](er-visual-parity-12.jpg)

![Entity Relationship visual parity page 13](er-visual-parity-13.jpg)

![Entity Relationship visual parity page 14](er-visual-parity-14.jpg)

![Entity Relationship visual parity page 15](er-visual-parity-15.jpg)

![Entity Relationship visual parity page 16](er-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Gantt - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.035-1.037`, height `0.883-0.964`, foreground ink `0.963-1.023`. All 16 pages below were manually reviewed.

![Gantt visual parity page 01](gantt-visual-parity-01.jpg)

![Gantt visual parity page 02](gantt-visual-parity-02.jpg)

![Gantt visual parity page 03](gantt-visual-parity-03.jpg)

![Gantt visual parity page 04](gantt-visual-parity-04.jpg)

![Gantt visual parity page 05](gantt-visual-parity-05.jpg)

![Gantt visual parity page 06](gantt-visual-parity-06.jpg)

![Gantt visual parity page 07](gantt-visual-parity-07.jpg)

![Gantt visual parity page 08](gantt-visual-parity-08.jpg)

![Gantt visual parity page 09](gantt-visual-parity-09.jpg)

![Gantt visual parity page 10](gantt-visual-parity-10.jpg)

![Gantt visual parity page 11](gantt-visual-parity-11.jpg)

![Gantt visual parity page 12](gantt-visual-parity-12.jpg)

![Gantt visual parity page 13](gantt-visual-parity-13.jpg)

![Gantt visual parity page 14](gantt-visual-parity-14.jpg)

![Gantt visual parity page 15](gantt-visual-parity-15.jpg)

![Gantt visual parity page 16](gantt-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Pie - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `255 pass / 1 review / 0 fail`. The single review is a text-overlap font-box threshold finding; slices, labels, values, legend, clipping, and paint order remain complete. Geometry ratios: width `0.992-1.050`, height `0.994-1.019`, foreground ink `0.993-1.042`. All 16 pages below were manually reviewed.

![Pie visual parity page 01](pie-visual-parity-01.jpg)

![Pie visual parity page 02](pie-visual-parity-02.jpg)

![Pie visual parity page 03](pie-visual-parity-03.jpg)

![Pie visual parity page 04](pie-visual-parity-04.jpg)

![Pie visual parity page 05](pie-visual-parity-05.jpg)

![Pie visual parity page 06](pie-visual-parity-06.jpg)

![Pie visual parity page 07](pie-visual-parity-07.jpg)

![Pie visual parity page 08](pie-visual-parity-08.jpg)

![Pie visual parity page 09](pie-visual-parity-09.jpg)

![Pie visual parity page 10](pie-visual-parity-10.jpg)

![Pie visual parity page 11](pie-visual-parity-11.jpg)

![Pie visual parity page 12](pie-visual-parity-12.jpg)

![Pie visual parity page 13](pie-visual-parity-13.jpg)

![Pie visual parity page 14](pie-visual-parity-14.jpg)

![Pie visual parity page 15](pie-visual-parity-15.jpg)

![Pie visual parity page 16](pie-visual-parity-16.jpg)

</details>

<details>
<summary><strong>User Journey - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `237 pass / 19 review / 0 fail`. The 19 reviews are benign text-segmentation differences for one complete long actor label. Geometry ratios: width `1.020-1.032`, height `1.041-1.061`, foreground ink `0.983-1.073`. All 16 pages below were manually reviewed.

![User Journey visual parity page 01](journey-visual-parity-01.jpg)

![User Journey visual parity page 02](journey-visual-parity-02.jpg)

![User Journey visual parity page 03](journey-visual-parity-03.jpg)

![User Journey visual parity page 04](journey-visual-parity-04.jpg)

![User Journey visual parity page 05](journey-visual-parity-05.jpg)

![User Journey visual parity page 06](journey-visual-parity-06.jpg)

![User Journey visual parity page 07](journey-visual-parity-07.jpg)

![User Journey visual parity page 08](journey-visual-parity-08.jpg)

![User Journey visual parity page 09](journey-visual-parity-09.jpg)

![User Journey visual parity page 10](journey-visual-parity-10.jpg)

![User Journey visual parity page 11](journey-visual-parity-11.jpg)

![User Journey visual parity page 12](journey-visual-parity-12.jpg)

![User Journey visual parity page 13](journey-visual-parity-13.jpg)

![User Journey visual parity page 14](journey-visual-parity-14.jpg)

![User Journey visual parity page 15](journey-visual-parity-15.jpg)

![User Journey visual parity page 16](journey-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Requirement - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `252 pass / 4 review / 0 fail`. The four reviews are benign duplicate-label text-position cross-matches. Geometry ratios: width `1.013-1.072`, height `1.004-1.053`, foreground ink `0.974-1.148`. All 16 pages below were manually reviewed.

![Requirement visual parity page 01](requirement-visual-parity-01.jpg)

![Requirement visual parity page 02](requirement-visual-parity-02.jpg)

![Requirement visual parity page 03](requirement-visual-parity-03.jpg)

![Requirement visual parity page 04](requirement-visual-parity-04.jpg)

![Requirement visual parity page 05](requirement-visual-parity-05.jpg)

![Requirement visual parity page 06](requirement-visual-parity-06.jpg)

![Requirement visual parity page 07](requirement-visual-parity-07.jpg)

![Requirement visual parity page 08](requirement-visual-parity-08.jpg)

![Requirement visual parity page 09](requirement-visual-parity-09.jpg)

![Requirement visual parity page 10](requirement-visual-parity-10.jpg)

![Requirement visual parity page 11](requirement-visual-parity-11.jpg)

![Requirement visual parity page 12](requirement-visual-parity-12.jpg)

![Requirement visual parity page 13](requirement-visual-parity-13.jpg)

![Requirement visual parity page 14](requirement-visual-parity-14.jpg)

![Requirement visual parity page 15](requirement-visual-parity-15.jpg)

![Requirement visual parity page 16](requirement-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Git Graph - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `235 pass / 21 review / 0 fail`. Twenty reviews are text-overlap font-bound threshold findings and one is a paint-order occlusion-ratio threshold finding; expected labels, clipping, commit order, and raster checks pass. Geometry ratios: width `1.035-1.141`, height `1.032-1.145`, foreground ink `1.035-1.421`. All 16 pages below were manually reviewed.

![Git Graph visual parity page 01](gitgraph-visual-parity-01.jpg)

![Git Graph visual parity page 02](gitgraph-visual-parity-02.jpg)

![Git Graph visual parity page 03](gitgraph-visual-parity-03.jpg)

![Git Graph visual parity page 04](gitgraph-visual-parity-04.jpg)

![Git Graph visual parity page 05](gitgraph-visual-parity-05.jpg)

![Git Graph visual parity page 06](gitgraph-visual-parity-06.jpg)

![Git Graph visual parity page 07](gitgraph-visual-parity-07.jpg)

![Git Graph visual parity page 08](gitgraph-visual-parity-08.jpg)

![Git Graph visual parity page 09](gitgraph-visual-parity-09.jpg)

![Git Graph visual parity page 10](gitgraph-visual-parity-10.jpg)

![Git Graph visual parity page 11](gitgraph-visual-parity-11.jpg)

![Git Graph visual parity page 12](gitgraph-visual-parity-12.jpg)

![Git Graph visual parity page 13](gitgraph-visual-parity-13.jpg)

![Git Graph visual parity page 14](gitgraph-visual-parity-14.jpg)

![Git Graph visual parity page 15](gitgraph-visual-parity-15.jpg)

![Git Graph visual parity page 16](gitgraph-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Mindmap - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `153 pass / 103 review / 0 fail`. All 103 reviews are text-position findings from equivalent CoSE-Bilkent branch rotations or mirrors; 69 also cross the raster-mask threshold. Geometry ratios: width `0.930-1.276`, height `0.877-1.104`, foreground ink `0.794-1.277`. All 16 pages below were manually reviewed.

![Mindmap visual parity page 01](mindmap-visual-parity-01.jpg)

![Mindmap visual parity page 02](mindmap-visual-parity-02.jpg)

![Mindmap visual parity page 03](mindmap-visual-parity-03.jpg)

![Mindmap visual parity page 04](mindmap-visual-parity-04.jpg)

![Mindmap visual parity page 05](mindmap-visual-parity-05.jpg)

![Mindmap visual parity page 06](mindmap-visual-parity-06.jpg)

![Mindmap visual parity page 07](mindmap-visual-parity-07.jpg)

![Mindmap visual parity page 08](mindmap-visual-parity-08.jpg)

![Mindmap visual parity page 09](mindmap-visual-parity-09.jpg)

![Mindmap visual parity page 10](mindmap-visual-parity-10.jpg)

![Mindmap visual parity page 11](mindmap-visual-parity-11.jpg)

![Mindmap visual parity page 12](mindmap-visual-parity-12.jpg)

![Mindmap visual parity page 13](mindmap-visual-parity-13.jpg)

![Mindmap visual parity page 14](mindmap-visual-parity-14.jpg)

![Mindmap visual parity page 15](mindmap-visual-parity-15.jpg)

![Mindmap visual parity page 16](mindmap-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Packet - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.011-1.015`, height `1.003-1.065`, foreground ink `0.977-1.039`. All 16 pages below were manually reviewed.

![Packet visual parity page 01](packet-visual-parity-01.jpg)

![Packet visual parity page 02](packet-visual-parity-02.jpg)

![Packet visual parity page 03](packet-visual-parity-03.jpg)

![Packet visual parity page 04](packet-visual-parity-04.jpg)

![Packet visual parity page 05](packet-visual-parity-05.jpg)

![Packet visual parity page 06](packet-visual-parity-06.jpg)

![Packet visual parity page 07](packet-visual-parity-07.jpg)

![Packet visual parity page 08](packet-visual-parity-08.jpg)

![Packet visual parity page 09](packet-visual-parity-09.jpg)

![Packet visual parity page 10](packet-visual-parity-10.jpg)

![Packet visual parity page 11](packet-visual-parity-11.jpg)

![Packet visual parity page 12](packet-visual-parity-12.jpg)

![Packet visual parity page 13](packet-visual-parity-13.jpg)

![Packet visual parity page 14](packet-visual-parity-14.jpg)

![Packet visual parity page 15](packet-visual-parity-15.jpg)

![Packet visual parity page 16](packet-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Radar - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.004-1.036`, height `1.004-1.020`, foreground ink `1.006-1.037`. All 16 pages below were manually reviewed.

![Radar visual parity page 01](radar-visual-parity-01.jpg)

![Radar visual parity page 02](radar-visual-parity-02.jpg)

![Radar visual parity page 03](radar-visual-parity-03.jpg)

![Radar visual parity page 04](radar-visual-parity-04.jpg)

![Radar visual parity page 05](radar-visual-parity-05.jpg)

![Radar visual parity page 06](radar-visual-parity-06.jpg)

![Radar visual parity page 07](radar-visual-parity-07.jpg)

![Radar visual parity page 08](radar-visual-parity-08.jpg)

![Radar visual parity page 09](radar-visual-parity-09.jpg)

![Radar visual parity page 10](radar-visual-parity-10.jpg)

![Radar visual parity page 11](radar-visual-parity-11.jpg)

![Radar visual parity page 12](radar-visual-parity-12.jpg)

![Radar visual parity page 13](radar-visual-parity-13.jpg)

![Radar visual parity page 14](radar-visual-parity-14.jpg)

![Radar visual parity page 15](radar-visual-parity-15.jpg)

![Radar visual parity page 16](radar-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Sankey - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.043-1.053`, height `1.026-1.053`, foreground ink `1.053-1.112`. All 16 pages below were manually reviewed.

![Sankey visual parity page 01](sankey-visual-parity-01.jpg)

![Sankey visual parity page 02](sankey-visual-parity-02.jpg)

![Sankey visual parity page 03](sankey-visual-parity-03.jpg)

![Sankey visual parity page 04](sankey-visual-parity-04.jpg)

![Sankey visual parity page 05](sankey-visual-parity-05.jpg)

![Sankey visual parity page 06](sankey-visual-parity-06.jpg)

![Sankey visual parity page 07](sankey-visual-parity-07.jpg)

![Sankey visual parity page 08](sankey-visual-parity-08.jpg)

![Sankey visual parity page 09](sankey-visual-parity-09.jpg)

![Sankey visual parity page 10](sankey-visual-parity-10.jpg)

![Sankey visual parity page 11](sankey-visual-parity-11.jpg)

![Sankey visual parity page 12](sankey-visual-parity-12.jpg)

![Sankey visual parity page 13](sankey-visual-parity-13.jpg)

![Sankey visual parity page 14](sankey-visual-parity-14.jpg)

![Sankey visual parity page 15](sankey-visual-parity-15.jpg)

![Sankey visual parity page 16](sankey-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Treemap - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `19 pass / 237 review / 0 fail`. The 237 reviews are text-position findings; 198 also report same-row label/value overlap-threshold differences caused by Canvas/SVG glyph bounds. Geometry ratios: width `1.033-1.076`, height `1.029-1.039`, foreground ink `0.945-1.077`. All 16 pages below were manually reviewed.

![Treemap visual parity page 01](treemap-visual-parity-01.jpg)

![Treemap visual parity page 02](treemap-visual-parity-02.jpg)

![Treemap visual parity page 03](treemap-visual-parity-03.jpg)

![Treemap visual parity page 04](treemap-visual-parity-04.jpg)

![Treemap visual parity page 05](treemap-visual-parity-05.jpg)

![Treemap visual parity page 06](treemap-visual-parity-06.jpg)

![Treemap visual parity page 07](treemap-visual-parity-07.jpg)

![Treemap visual parity page 08](treemap-visual-parity-08.jpg)

![Treemap visual parity page 09](treemap-visual-parity-09.jpg)

![Treemap visual parity page 10](treemap-visual-parity-10.jpg)

![Treemap visual parity page 11](treemap-visual-parity-11.jpg)

![Treemap visual parity page 12](treemap-visual-parity-12.jpg)

![Treemap visual parity page 13](treemap-visual-parity-13.jpg)

![Treemap visual parity page 14](treemap-visual-parity-14.jpg)

![Treemap visual parity page 15](treemap-visual-parity-15.jpg)

![Treemap visual parity page 16](treemap-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Venn - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `216 pass / 40 review / 0 fail`. The 40 reviews repeat text-overlap font-box threshold patterns with complete sets, intersections, labels, and values. Geometry ratios: width `0.995-1.026`, height `0.990-1.022`, foreground ink `0.989-1.048`. Raw [detail](venn-visual-parity-detail.json) and [geometry](venn-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Venn visual parity page 01](venn-visual-parity-01.jpg)

![Venn visual parity page 02](venn-visual-parity-02.jpg)

![Venn visual parity page 03](venn-visual-parity-03.jpg)

![Venn visual parity page 04](venn-visual-parity-04.jpg)

![Venn visual parity page 05](venn-visual-parity-05.jpg)

![Venn visual parity page 06](venn-visual-parity-06.jpg)

![Venn visual parity page 07](venn-visual-parity-07.jpg)

![Venn visual parity page 08](venn-visual-parity-08.jpg)

![Venn visual parity page 09](venn-visual-parity-09.jpg)

![Venn visual parity page 10](venn-visual-parity-10.jpg)

![Venn visual parity page 11](venn-visual-parity-11.jpg)

![Venn visual parity page 12](venn-visual-parity-12.jpg)

![Venn visual parity page 13](venn-visual-parity-13.jpg)

![Venn visual parity page 14](venn-visual-parity-14.jpg)

![Venn visual parity page 15](venn-visual-parity-15.jpg)

![Venn visual parity page 16](venn-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Ishikawa - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.046-1.181`, height `1.017-1.116`, foreground ink `1.028-1.313`. Raw [detail](ishikawa-visual-parity-detail.json) and [geometry](ishikawa-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Ishikawa visual parity page 01](ishikawa-visual-parity-01.jpg)

![Ishikawa visual parity page 02](ishikawa-visual-parity-02.jpg)

![Ishikawa visual parity page 03](ishikawa-visual-parity-03.jpg)

![Ishikawa visual parity page 04](ishikawa-visual-parity-04.jpg)

![Ishikawa visual parity page 05](ishikawa-visual-parity-05.jpg)

![Ishikawa visual parity page 06](ishikawa-visual-parity-06.jpg)

![Ishikawa visual parity page 07](ishikawa-visual-parity-07.jpg)

![Ishikawa visual parity page 08](ishikawa-visual-parity-08.jpg)

![Ishikawa visual parity page 09](ishikawa-visual-parity-09.jpg)

![Ishikawa visual parity page 10](ishikawa-visual-parity-10.jpg)

![Ishikawa visual parity page 11](ishikawa-visual-parity-11.jpg)

![Ishikawa visual parity page 12](ishikawa-visual-parity-12.jpg)

![Ishikawa visual parity page 13](ishikawa-visual-parity-13.jpg)

![Ishikawa visual parity page 14](ishikawa-visual-parity-14.jpg)

![Ishikawa visual parity page 15](ishikawa-visual-parity-15.jpg)

![Ishikawa visual parity page 16](ishikawa-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Kanban - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.036-1.089`, height `0.852-1.088`, foreground ink `0.992-1.105`. All 16 pages below were manually reviewed.

![Kanban visual parity page 01](kanban-visual-parity-01.jpg)

![Kanban visual parity page 02](kanban-visual-parity-02.jpg)

![Kanban visual parity page 03](kanban-visual-parity-03.jpg)

![Kanban visual parity page 04](kanban-visual-parity-04.jpg)

![Kanban visual parity page 05](kanban-visual-parity-05.jpg)

![Kanban visual parity page 06](kanban-visual-parity-06.jpg)

![Kanban visual parity page 07](kanban-visual-parity-07.jpg)

![Kanban visual parity page 08](kanban-visual-parity-08.jpg)

![Kanban visual parity page 09](kanban-visual-parity-09.jpg)

![Kanban visual parity page 10](kanban-visual-parity-10.jpg)

![Kanban visual parity page 11](kanban-visual-parity-11.jpg)

![Kanban visual parity page 12](kanban-visual-parity-12.jpg)

![Kanban visual parity page 13](kanban-visual-parity-13.jpg)

![Kanban visual parity page 14](kanban-visual-parity-14.jpg)

![Kanban visual parity page 15](kanban-visual-parity-15.jpg)

![Kanban visual parity page 16](kanban-visual-parity-16.jpg)

</details>

<details>
<summary><strong>Wardley Map - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `0.999-1.016`, height `1.016-1.019`, foreground ink `0.960-1.106`. Raw [detail](wardley-visual-parity-detail.json) and [geometry](wardley-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![Wardley Map visual parity page 01](wardley-visual-parity-01.jpg)

![Wardley Map visual parity page 02](wardley-visual-parity-02.jpg)

![Wardley Map visual parity page 03](wardley-visual-parity-03.jpg)

![Wardley Map visual parity page 04](wardley-visual-parity-04.jpg)

![Wardley Map visual parity page 05](wardley-visual-parity-05.jpg)

![Wardley Map visual parity page 06](wardley-visual-parity-06.jpg)

![Wardley Map visual parity page 07](wardley-visual-parity-07.jpg)

![Wardley Map visual parity page 08](wardley-visual-parity-08.jpg)

![Wardley Map visual parity page 09](wardley-visual-parity-09.jpg)

![Wardley Map visual parity page 10](wardley-visual-parity-10.jpg)

![Wardley Map visual parity page 11](wardley-visual-parity-11.jpg)

![Wardley Map visual parity page 12](wardley-visual-parity-12.jpg)

![Wardley Map visual parity page 13](wardley-visual-parity-13.jpg)

![Wardley Map visual parity page 14](wardley-visual-parity-14.jpg)

![Wardley Map visual parity page 15](wardley-visual-parity-15.jpg)

![Wardley Map visual parity page 16](wardley-visual-parity-16.jpg)

</details>

<details>
<summary><strong>ZenUML - 256 Native/Official pairs</strong></summary>

Replacement detail audit: `256 pass / 0 review / 0 fail`. Geometry ratios: width `1.031-1.101`, height `1.025-1.102`, foreground ink `0.970-1.264`. Raw [detail](zenuml-visual-parity-detail.json) and [geometry](zenuml-visual-parity-geometry.json) reports are published with the sheets. All 16 pages below were manually reviewed.

![ZenUML visual parity page 01](zenuml-visual-parity-01.jpg)

![ZenUML visual parity page 02](zenuml-visual-parity-02.jpg)

![ZenUML visual parity page 03](zenuml-visual-parity-03.jpg)

![ZenUML visual parity page 04](zenuml-visual-parity-04.jpg)

![ZenUML visual parity page 05](zenuml-visual-parity-05.jpg)

![ZenUML visual parity page 06](zenuml-visual-parity-06.jpg)

![ZenUML visual parity page 07](zenuml-visual-parity-07.jpg)

![ZenUML visual parity page 08](zenuml-visual-parity-08.jpg)

![ZenUML visual parity page 09](zenuml-visual-parity-09.jpg)

![ZenUML visual parity page 10](zenuml-visual-parity-10.jpg)

![ZenUML visual parity page 11](zenuml-visual-parity-11.jpg)

![ZenUML visual parity page 12](zenuml-visual-parity-12.jpg)

![ZenUML visual parity page 13](zenuml-visual-parity-13.jpg)

![ZenUML visual parity page 14](zenuml-visual-parity-14.jpg)

![ZenUML visual parity page 15](zenuml-visual-parity-15.jpg)

![ZenUML visual parity page 16](zenuml-visual-parity-16.jpg)

</details>
