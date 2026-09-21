# TreeView Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported TreeView diagram must preserve source-order hierarchy, indentation,
box-drawing structure, file and directory semantics, annotations, descriptions,
icons, configuration, metadata, and theme colors.

TreeView is beta in Mermaid `12.0.0`; its syntax and rendering behavior may
change in a later upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and preprocessing | Supported | `treeView-beta`, frontmatter, init directives, comments, title, accessibility metadata, entities, and Unicode |
| Indentation syntax | Supported | Bare and quoted names, files, trailing-slash directories, irregular depth changes, and source-order IDs |
| Box-drawing syntax | Supported | Standard and heavy branch/continuation glyphs with original source-line mapping |
| Database semantics | Supported | Synthetic `/` root, stack-based hierarchy construction, metadata, sanitization, and typed malformed-input failures |
| Annotations | Supported | `:::class`, `:::highlight`, `icon(name)`, `icon(none)`, and `## description` |
| Icon resolution | Supported | Explicit icons, suppression, exact filename maps, case-insensitive extension maps, default file/folder icons, and external `SceneAsset` references |
| Layout and paths | Supported | Preorder rows, indentation, horizontal/vertical connectors, aligned descriptions, directory emphasis, and full-row highlights |
| Configuration | Supported | Responsive sizing, row indent, horizontal/vertical padding, line thickness, icon visibility, default icon pack, filename maps, extension maps, and typed validation |
| Themes | Supported | TreeView-scoped label, line, icon, description, and highlight colors plus label font size |

## Validation Corpus

- All 13 active Mermaid TreeView documentation examples are generated from the
  pinned documentation source and rendered in JVM tests.
- Focused parser, database, layout, preprocessing, and engine tests cover
  indentation, both box-drawing styles, quoted and bare names, metadata,
  classes, descriptions, icon precedence and suppression, configuration,
  themes, malformed input, and source-line preservation.
- 8 independent conformance scenarios plus 5 release-candidate scenarios cover
  all 22 declared visual capability points.
- 256 deterministic same-source matrix cases exercise 13 structural seeds and
  visible text and layout-pressure profiles.
- Production geometry passed `13/13`. Width, height, and foreground-ink ratios
  were `1.049-1.279`, `1.101-1.229`, and `1.089-1.411`.
- Production detail produced `13 pass / 0 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `1.033-1.141`,
  `1.006-1.138`, and `0.954-1.229`.
- Matrix detail produced `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved hierarchy, connector, icon, highlight, description,
  text, clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/treeview-production-detail.json),
  [production geometry](assets/stability-report/treeview-production-geometry.json),
  [matrix detail](assets/stability-report/treeview-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/treeview-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:treeview-doc-fixtures
```

Capture the 256-case TreeView partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=treeview \
OUTPUT_DIR=captures/local/treeview-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
