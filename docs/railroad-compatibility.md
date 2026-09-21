# Railroad Diagram Compatibility

Baseline: Mermaid `12.0.0`.

## Compatibility Contract

The target is visual and semantic parity rather than pixel identity. A
supported Railroad diagram must preserve its input notation, rule order,
terminals, non-terminals, sequences, choices, optional and repeated
expressions, notation-specific constructs, configuration, metadata, and theme
colors.

Railroad is beta in Mermaid `12.0.0`; its syntax and rendering behavior may
change in a later upstream release.

## Compatibility Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Headers and preprocessing | Supported | `railroad-beta`, `railroad-ebnf-beta`, `railroad-abnf-beta`, and `railroad-peg-beta`, plus frontmatter, init directives, comments, metadata, entities, and Unicode |
| Railroad IR parser | Supported | Explicit `terminal`, `nonterminal`, `special`, `sequence`, `choice`, `optional`, `zeroOrMore`, and `oneOrMore` constructors |
| EBNF parser | Supported | Alternation, concatenation, groups, postfix repetition, ISO commas, brackets, braces, comments, special sequences, and exceptions |
| ABNF parser | Supported | Alternation, concatenation, quoted strings, rule names, numeric values, groups, optionals, and exact, bounded, and open repetition |
| PEG parser | Supported | Ordered choices, sequences, groups, literals, identifiers, positive and negative lookahead, any-character matching, and suffix repetition |
| Database semantics | Supported | Source-order rules, duplicate-name overwrite lookup, recursive sanitization, titles, and accessibility metadata |
| Symbols and paths | Supported | Rounded terminals, rectangular non-terminals, dashed special nodes, sequences, vertically routed choices, optional bypasses, repetition loops, rule names, and endpoint markers |
| Configuration | Supported | Compact mode, spacing, arc radius, typography, symbol colors, line and marker styles, rule-name colors, responsive sizing, and typed validation |
| Themes | Supported | Theme-derived terminal, non-terminal, special, line, marker, rule-name, and text colors across all built-in themes |

## Validation Corpus

- All 14 active Mermaid Railroad documentation examples are generated from the
  pinned documentation source and rendered in JVM tests.
- Focused parser, database, layout, preprocessing, and engine tests cover all
  four headers, every shared AST node, EBNF, ABNF, and PEG-specific constructs,
  metadata, configuration, comments, entities, Unicode, malformed input,
  resource limits, and deterministic rendering.
- 13 independent conformance scenarios plus 5 release-candidate scenarios
  cover all 25 declared visual capability points.
- 256 deterministic same-source matrix cases exercise 18 structural seeds and
  15 visible text and layout-pressure profiles.
- Production geometry passed `18/18`. Width, height, and foreground-ink ratios
  were `1.038-1.120`, `1.028-1.131`, and `1.078-1.281`.
- Production detail produced `18 pass / 0 review / 0 fail`.
- Matrix geometry passed `256/256`. Ratios were `1.035-1.116`,
  `1.009-1.130`, and `1.023-1.319`.
- Matrix detail produced `256 pass / 0 review / 0 fail`.
- The production contact sheet and all 16 matrix contact sheets were manually
  reviewed. No unresolved rule, symbol, choice, repetition, path, label,
  clipping, overlap, or paint-order defect remains.
- Raw
  [production detail](assets/stability-report/railroad-production-detail.json),
  [production geometry](assets/stability-report/railroad-production-geometry.json),
  [matrix detail](assets/stability-report/railroad-visual-parity-detail.json),
  and
  [matrix geometry](assets/stability-report/railroad-visual-parity-geometry.json)
  reports are published with the evidence.

## Reference Workflow

Regenerate the documentation fixtures:

```bash
cd tools/official-reference
MERMAID_SOURCE_DIR=/path/to/mermaid-12 \
  npm run generate:railroad-doc-fixtures
```

Capture the 256-case Railroad partition:

```bash
cd tools/official-reference
AUDIT_SOURCE=visual-parity \
AUDIT_KIND=railroad \
OUTPUT_DIR=captures/local/railroad-visual-parity \
BASE_URL=http://127.0.0.1:8093/ \
VIEWPORT_WIDTH=1200 \
VIEWPORT_HEIGHT=900 \
npm run capture:web-audit
```
