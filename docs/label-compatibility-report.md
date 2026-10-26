# Label compatibility repair report

Release: **0.1.20**. Upstream baseline: **Mermaid.js 12.0.0**.

The edge-label production code and its 32-source evidence were captured at
0.1.19 and are unchanged in 0.1.20. The 0.1.19 publication was withheld after
a geometry audit false positive. The
[SVG rectangle occlusion audit repair](rectangle-occlusion-audit.md)
records the cause, corrected detection and a separate two-image reproduction.
The separate 0.1.20 [Class terminal repair](class-terminal-report.md) translates
terminal positions and removes non-upstream obstacle avoidance; its 13 sources,
26 screenshots and one contact sheet are additional evidence. Existing
center-label code and the 32-source evidence below remain unchanged.

The current repair follows the [final edge-label source inventory](upstream-edge-label-map.md).
Flowchart and ER now execute `insertEdge`'s final SVG-path coordinate check;
State and Requirement use the same translated utilities. An updated path
anchors its label at the upstream polyline midpoint, including five-decimal
interpolation and the original zero-length-segment behavior. The substring
check preserves upstream signed-decimal rounding, even where it looks unusual.
No routing or font-measurement algorithm is changed.

The [earlier repair inventory](upstream-label-compatibility-fixes.md) retains
comparison text, complete long labels, zero-child groups, theme radii,
transparent-paint shadows, Class sanitization and cluster-boundary clipping.

## Fresh evidence

The [Native/Official comparison](assets/label-compatibility-report/evidence.md)
uses 32 unique synthetic sources across Flowchart, Class, ER and State, with
64 screenshots and five reviewed contact sheets. Both renderers receive identical input and
explicit **Dagre** layout, using bundled Arimo/Arial and `redux-color` unless
the source overrides the theme.

Result: **14 Pass / 18 Review / 0 Fail**. All label content checks pass. The
cluster-boundary reproduction passes node geometry, rendered path endpoints,
per-edge topology and raster checks. Every case retains its geometry,
endpoint, direction-sequence, bend, port, shared-track and crossing findings
in the [detail audit](assets/label-compatibility-report/audit.json).
The [manifest](assets/label-compatibility-report/visual-parity-manifest.json)
binds source identity, input JSON hashes and screenshot hashes; publication
rejects changed, missing, duplicated or failed evidence.

The [published 0.1.18 artifact comparison](assets/label-compatibility-report/baseline-comparison.json)
uses the same 32 sources and font resources. Two generic cases change six
label anchors; all 32 edge-path sets remain identical. The merged-decision
case's eight label anchors match Official, including Official's overlapping
labels. It remains Review for independent node geometry differences. The
cluster-return case's two labels move close to Official but retains subpixel
geometry Review. The other 30 scenes retain their prior label positions.
The comparison records the baseline JAR hashes and both capture hashes;
it contributes no additional screenshots to the 64-image count.

The [upstream utility oracle](assets/label-compatibility-report/edge-label-oracle.json)
executes unchanged Mermaid source bodies for 17 distance cases, five path
substring cases and a fixed-coordinate D3 Basis reproduction. Common tests
translate all five upstream `calculatePoint` tests and exercise normal,
singleton, duplicate-segment, signed-rounding and safe-failure branches.

## Remaining differences

The 18 Review cases retain long-prose wrapping, explicit-break line boxes,
empty-circle outline height, empty-group stroke rasterization, Class
compartment height, theme/color/antialiasing differences and ER/State or
subpixel node/endpoint geometry differences. Label-anchor checks have a
0.02-pixel tolerance; raster similarity cannot override a geometry or topology
Review. These findings remain explicit, with no input-specific adjustments.
Browser blur remains a declared Compose adaptation.

This targeted audit does not regenerate the historical 33-family / 8,448-pair
matrix, does not represent a 1,000-case mobile rerun, and does not establish
Stable status. ELK remains structured unsupported; comparison runs must
explicitly align layouts before attributing layout differences to Dagre.
Mobile verification belongs to the consuming applications.
