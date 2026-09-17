# Git Graph Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Git Graph must preserve commit order and identity, branch ownership,
parent relationships, merge and cherry-pick semantics, labels, tags,
orientation, configuration, and theme styling closely enough that a
side-by-side comparison does not expose a functional rendering defect.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `gitGraph`, optional `LR`/`TB`/`BT`, frontmatter, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin translation of Mermaid's Langium Git Graph grammar and parser adapter |
| Database | Supported | Source-ordered commits, generated/custom IDs, current head, branches, checkout/switch, merges, cherry-picks, and metadata |
| Commits | Supported | Messages, multiple tags, NORMAL, REVERSE, and HIGHLIGHT glyphs |
| Branches | Supported | Bare or quoted names, explicit order, custom main branch name/order, and nested histories |
| Merges | Supported | Two-parent merge commits, custom IDs, tags, and glyph overrides |
| Cherry-picks | Supported | Normal commits and merge commits with an immediate parent |
| Layout | Supported | LR, TB, and BT orientations, sequential or parallel commit ranks, routed arrows, and lane avoidance |
| Visibility | Supported | `showBranches`, `showCommitLabel`, and `rotateCommitLabel` |
| Themes | Supported | All 11 built-in themes, Git colors/inverse colors, branch labels, commit/tag variables, and Neo gradients |
| Resource controls | Supported | Text and edge limits return structured `MermaidError` values |

## Intentional Native Boundaries

- Mermaid generates a random seven-character suffix for commits without an
  explicit ID. CMP Mermaid preserves the public `<sequence>-<suffix>` shape but
  derives the suffix deterministically so SceneGraph replay and screenshots
  remain reproducible.
- Browser SVG text maps to measured Compose text. Font rasterization can differ
  by platform, while explicit line breaks, rotation, alignment, and Mermaid's
  SVG-native horizontal scale are preserved.
- SceneGraph output is static. Git Graph defines no diagram-specific browser
  interaction contract to execute.
- Unsupported legal Mermaid features return
  `MermaidError.UnsupportedFeature`; malformed syntax and invalid repository
  state return structured parse errors.

## Validation Corpus

- All 35 Git Graph examples extracted from Mermaid's official documentation
  run in JVM and multiplatform tests.
- Focused parser, database, layout, routing, symbol, theme, metadata,
  configuration, and error tests cover the translated upstream behavior.
- 256 deterministic randomized legal Git Graphs exercise all three
  orientations, one to four branches, commit types, multiple tags, merges,
  cherry-picks, parallel ranks, visibility controls, and finite deterministic
  SceneGraphs.
- 13 independent production scenarios cover all 24 declared Git Graph
  capability points.
- 256 same-source Native/Official visual cases were captured at `1200 x 900`.
  The replacement audit accepted all 256:
  `236 pass / 20 manually reviewed / 0 fail`. All 20 reviews are
  text-overlap threshold findings caused by browser/Compose text-bound
  differences; expected labels, clipping, paint order, and raster checks pass.
  Geometry ratios are width `1.036-1.154`, height `1.032-1.137`, and
  foreground ink `1.027-1.415`. All 16 contact sheets were manually reviewed.
- The 13 production pairs are also accepted:
  `12 pass / 1 manually reviewed / 0 fail`. The single review has the same
  text-bound cause and passes semantic, clipping, paint-order, and raster
  checks. Production geometry ratios are width `1.048-1.129`, height
  `1.047-1.146`, and foreground ink `1.074-1.342`.
- The replacement evidence includes the corrected `parity_gitgraph_005`;
  commit labels are emitted in upstream commit order instead of legacy global
  label layers, eliminating the missed cherry-pick label paint-order defect.
- Android Emulator, iOS Simulator, Desktop, and Web load tests traverse the
  complete 158-case mixed corpus.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:gitgraph-doc-fixtures
```

Capture the 256-case Git Graph partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=gitgraph \
OUTPUT_DIR=captures/local/gitgraph-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
