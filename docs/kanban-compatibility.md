# Kanban Compatibility

## Current State

CMP Mermaid translates Mermaid `12.0.0` Kanban diagrams to a native
`MermaidScene` and Compose Canvas. Production code does not use WebView,
JavaScript, or a network renderer.

The implementation covers:

- `kanban` detection with leading whitespace and comments;
- indentation-based sections and tasks;
- Mermaid's flattening of every level deeper than the section level;
- explicit IDs, anonymous nodes, quoted labels, and all node delimiters accepted
  by the upstream Jison grammar;
- blank rows and full-line or trailing `%%` comments;
- following-line `::icon(...)` and `:::class` declarations;
- inline and multiline `@{ ... }` metadata;
- `label`, `ticket`, `assigned`, `priority`, `icon`, and `shape` metadata parsing;
- fixed-width columns, wrapped task labels, empty sections, ticket and assignee
  rows, and the five upstream priority colors;
- `kanban.sectionWidth`, `kanban.ticketBaseUrl`, built-in themes,
  `themeVariables.cScale*`, `themeVariables.cScaleLabel*`, and `look`;
- external ticket interactions with `#TICKET#` replacement;
- Markdown labels, HTML breaks, entities, and Unicode text.

Mermaid `12.0.0` accepts icon and CSS-class decorations in the parser, but
`kanbanDb.getData()` drops section decorations and `kanbanItem` does not paint
its `icon` field. CMP Mermaid preserves that observable behavior instead of
inventing a native icon or class renderer.

The upstream renderer also reads `mindmap.padding` and
`mindmap.useMaxWidth` when it builds the Kanban viewport. The declared
`kanban.padding` value is parsed and validated but does not affect rendering in
this baseline. This compatibility quirk is covered by a focused regression
test.

## Expected Behavior

- Invalid syntax, malformed metadata, invalid shape names, configuration
  failures, and resource limits return typed `GMResult.Err` values.
- Section rectangles are painted before all task cards, matching the two
  upstream SVG groups.
- The first visible section uses Mermaid's `section-1` rule, which resolves to
  `cScale2` because of the upstream stylesheet's `i - 1` selector offset.
- Task labels are left aligned; ticket labels sit at bottom-left and assignees
  at bottom-right.
- Ticket text is underlined and exposes an `_blank` interaction only when
  `ticketBaseUrl` is configured.
- `Very High`, `High`, `Medium`, `Low`, and `Very Low` preserve Mermaid's red,
  orange, invisible, blue, and light-blue priority marker behavior.

## Verification

- `KanbanParserTest` covers hierarchy, node forms, comments, decorations,
  metadata, and typed failures.
- `KanbanLayoutTest` covers fixed geometry, metadata placement, priorities,
  links, theme slots, Neo styling, configuration, and resource limits.
- `KanbanStressTest` renders 256 deterministic randomized boards twice.
- `OfficialKanbanDocumentationTest` executes all 3 diagrams extracted from the
  Mermaid `12.0.0` syntax document.
- The repository corpus contains 5 independent stability scenarios, 8
  conformance scenarios, and 256 distinct visual-parity sources.
- All 13 independent production pairs pass the detail audit with
  `13 pass / 0 review / 0 fail` and the geometry gate. Their Native/Official
  ratios are width `1.047-1.116`, height `0.879-1.123`, and foreground ink
  `1.018-1.206`.
- All 256 visual-matrix pairs pass the detail audit with
  `256 pass / 0 review / 0 fail` and the geometry gate. Their ratios are width
  `1.041-1.071`, height `0.864-1.230`, and foreground ink `0.996-1.110`.
- All 16 paged contact sheets were manually inspected, including the corrected
  three-line titles in cases 114 and 117.

The Kanban family has completed its per-family Stable gate. Overall Mermaid
`12.0.0` support remains Not Stable until the other implemented families pass
the replacement audit and all 33 official families are translated.
