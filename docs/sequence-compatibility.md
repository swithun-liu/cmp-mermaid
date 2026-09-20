# Sequence Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Sequence diagram must preserve participant order and type, message
direction and marker, activation state, notes, lifecycle, control nesting,
labels, and colors closely enough that a side-by-side comparison does not
expose a functional rendering defect.

Legal Mermaid 12 input that depends on browser behavior unavailable to the
native SceneGraph returns `MermaidError.UnsupportedFeature`.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Header and preprocessing | Supported | `sequenceDiagram`, frontmatter, directives, comments, title, and accessibility metadata |
| Parser | Supported | Kotlin runtime for Mermaid's generated `sequenceDiagram.jison` tables and all 105 semantic productions |
| Participants | Supported | Implicit participants, `participant`, `actor`, aliases, declaration order, and explicit wrapping |
| Participant types | Supported | Participant, actor, boundary, control, entity, database, collections, and queue |
| Participant boxes | Supported | Named, colored, nested source ordering, and actor membership validation |
| Messages | Supported | All 26 Mermaid 12 solid/dotted, open, filled, async, cross, bidirectional, half, and stick forms |
| Central connections | Supported | Source, destination, and dual central connection circles |
| Self messages | Supported | Solid and dotted self-loop routing with marker placement |
| Activations | Supported | Explicit activate/deactivate and `+`/`-` message shortcuts, including nesting |
| Autonumber | Supported | Default, custom start/step, decimals, and `off` |
| Notes | Supported | Left, right, over one participant, and over participant ranges |
| Lifecycle | Supported | `create` and `destroy` for participant and actor glyphs |
| Control regions | Supported | `loop`, `alt`, `else`, `opt`, `par`, `and`, `par_over`, `critical`, `option`, `break`, and `rect` |
| Text | Supported | Unicode, HTML entities, explicit `<br>` variants, and `wrap:`/`nowrap:` |
| Themes | Supported | Mermaid 12 redux-color participant palette and Sequence note/control colors through the shared native theme |
| Resource controls | Supported | Sequence messages are bounded by host-owned `maxEdges` |

## Explicit Unsupported Boundaries

- Participant `links` menus require browser click-menu behavior.
- Participant `properties` depend on the same browser menu surface.
- Participant `details` is a browser DOM element reference.

These cases return a structured `MermaidError.UnsupportedFeature`; they are
not silently discarded.

## Validation Corpus

- 38 examples extracted from Mermaid's official Sequence documentation run in
  JVM tests.
- The two browser menu documentation examples are the only expected
  unsupported cases.
- 256 deterministic random legal Sequence diagrams cover 2-7 participants,
  every message marker family, self messages, autonumber, notes, activations,
  and nested control regions while checking finite bounds, actor bands,
  labels, and routed message paths.
- Focused tests cover Jison lexer/parser behavior, SequenceDB state,
  participant shapes, every message marker family, notes, wrapping, HTML line
  breaks, activations, control nesting, lifecycle endpoints, and resource
  limits.
- 14 independent production scenarios render identical source through Native
  Compose and Mermaid.js `12.0.0`: detail audit
  `14 pass / 0 review / 0 fail`; geometry width `1.019-1.254`, height
  `0.910-1.111`, and foreground ink `0.939-1.327`.
- 256 same-source visual-parity cases pass both the replacement detail and
  geometry gates: `256 pass / 0 review / 0 fail`; width `1.023-1.068`, height
  `0.893-1.045`, and foreground ink `0.926-1.148`.
- All 16 matrix contact sheets and all 256 pairs were manually reviewed. No
  unresolved blank, clipping, participant, lifecycle, message, marker, note,
  activation, control-frame, overlap, or paint-order defect remains.
- Core and Compose compile for JVM, Android, Desktop, and all configured iOS
  architectures.

## Reference Workflow

Regenerate the parser tables:

```bash
cd tools/official-reference
npm run generate:sequence-parser
```

Regenerate documentation fixtures from a Mermaid `12.0.0` source checkout:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:sequence-doc-fixtures
```

Capture the independent production corpus:

```bash
cd tools/official-reference
AUDIT_SOURCE=production AUDIT_KIND=sequence \
OUTPUT_DIR=captures/local/sequence-production-detail \
npm run capture:web-audit
```

Capture the 256-case matrix by changing `AUDIT_SOURCE` to `visual-parity`. Run
`npm run audit:detail` and `npm run audit:stability-geometry`, then generate
the 16 review sheets with `npm run generate:stability-contact-sheets`. Local
captures remain under ignored `captures/local/` paths. Reviewed production
and matrix contact sheets are published in
[`docs/assets/stability-report/`](assets/stability-report/).
