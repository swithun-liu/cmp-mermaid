# Label compatibility repair report

Release: **0.1.17**. Upstream baseline: **Mermaid.js 12.0.0**.

The repair follows the [upstream stage inventory](upstream-label-compatibility-fixes.md).
It restores comparison text, complete long labels, zero-child groups, theme
radius overrides, transparent-paint shadows, Class member sanitization, and
cluster-boundary edge clipping. Direction lexer actions and comment cleanup
now preserve their upstream meanings.

## Fresh evidence

The [Native/Official comparison](assets/label-compatibility-report/evidence.md)
uses 23 unique synthetic sources across Flowchart and Class, with 46 screenshots
and four reviewed contact sheets. Both renderers receive identical input and
explicit **Dagre** layout, using bundled Arimo/Arial and `redux-color` unless
the source overrides the theme.

Result: **13 Pass / 10 Review / 0 Fail**. All label content checks pass. The
cluster-boundary reproduction passes node geometry, rendered path endpoints,
per-edge topology and raster checks. Every case retains its geometry,
endpoint, direction-sequence, bend, port, shared-track and crossing findings
in the [detail audit](assets/label-compatibility-report/audit.json).
The [manifest](assets/label-compatibility-report/visual-parity-manifest.json)
binds source identity, input JSON hashes and screenshot hashes; publication
rejects changed, missing, duplicated or failed evidence.

## Remaining differences

The ten Review cases retain long-prose wrapping, explicit-break line boxes,
empty-circle outline height, empty-group stroke rasterization, Class
compartment height, and theme/color/antialiasing differences. They were
inspected and left for later work; they are not classified as Pass or hidden
by input-specific adjustments. Browser blur remains a declared Compose
adaptation.

This targeted audit does not regenerate the historical 33-family / 8,448-pair
matrix, does not represent a 1,000-case mobile rerun, and does not establish
Stable status. ELK remains structured unsupported; comparison runs must
explicitly align layouts before attributing layout differences to Dagre.
Mobile verification belongs to the consuming applications.
