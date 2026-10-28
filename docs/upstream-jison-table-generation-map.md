# Upstream Jison Table Construction Map

## Scope and frozen baseline

This slice changes the construction of generated parser tables from Mermaid
`12.0.0`. It retains every state, symbol, goto, shift, reduce, and accept
entry. Lexer rules, productions, semantic actions, parser stacks, layout,
rendering, framework exports, and fonts are outside the change.

The published baseline is `v0.1.20`, commit
`47f591768ad4a6d6b4e042d93efac6a8df04c908`. The source inventory was established
before implementation. Release `0.1.21` implements the construction adaptation
and independently verifies all 1,611 states and 20,857 entries. It does not
change diagram stability claims.

## Production source inventory

Grammar paths below are relative to `packages/mermaid/src/diagrams/`.
Kotlin files are under
`mermaid-core/src/commonMain/kotlin/com/swithun/cmpmermaid/core/`.
Each family retains its existing cell types, runtime, and semantic parser.
Agentflow continues to reuse Flowchart's `JisonCell` and `JisonProduction`.

| Family | Upstream grammar | Generator under `tools/official-reference/` | Kotlin table under family package |
| --- | --- | --- | --- |
| Agentflow | `agentflow/parser/agentflow.jison` | `generate-agentflow-parser.mjs` | `agentflow/upstream/mermaid/AgentflowJisonTables.kt` |
| Block | `block/parser/block.jison` | `generate-block-parser.mjs` | `block/upstream/mermaid/BlockJisonTables.kt` |
| Class | `class/parser/classDiagram.jison` | `generate-class-parser.mjs` | `classdiagram/upstream/mermaid/ClassJisonTables.kt` |
| ER | `er/parser/erDiagram.jison` | `generate-er-parser.mjs` | `erdiagram/upstream/mermaid/ErJisonTables.kt` |
| Flowchart | `flowchart/parser/flow.jison` | `generate-flow-parser.mjs` | `flowchart/upstream/mermaid/FlowJisonTables.kt` |
| Gantt | `gantt/parser/gantt.jison` | `generate-gantt-parser.mjs` | `gantt/upstream/mermaid/GanttJisonTables.kt` |
| Journey | `user-journey/parser/journey.jison` | `generate-journey-parser.mjs` | `journey/upstream/mermaid/JourneyJisonTables.kt` |
| Mindmap | `mindmap/parser/mindmap.jison` | `generate-mindmap-parser.mjs` | `mindmap/upstream/mermaid/MindmapJisonTables.kt` |
| Requirement | `requirement/parser/requirementDiagram.jison` | `generate-requirement-parser.mjs` | `requirement/upstream/mermaid/RequirementJisonTables.kt` |
| Sequence | `sequence/parser/sequenceDiagram.jison` | `generate-sequence-parser.mjs` | `sequence/upstream/mermaid/SequenceJisonTables.kt` |
| State | `state/parser/stateDiagram.jison` | `generate-state-parser.mjs` | `statediagram/upstream/mermaid/StateJisonTables.kt` |
| XY chart | `xychart/parser/xychart.jison` | `generate-xychart-parser.mjs` | `xychart/upstream/mermaid/XyJisonTables.kt` |

The npm distribution is generated from these grammars. Each existing generator
records its grammar and distribution SHA-256. The Flowchart distribution
`dist/chunks/mermaid.core/chunk-7M6MHVWA.mjs` demonstrates the shared action
variables and the generated `o(k, v, o2, l)` construction helper.

## Function and stage inventory

| Order | Upstream function or stage | Kotlin or build-time counterpart | Classification |
| ---: | --- | --- | --- |
| 1 | Generated `symbols_`, `terminals_`, `productions_` declarations | Existing `symbolIds`, `terminalNames`, `productions` emission | **Exact**, unchanged |
| 2 | Shared `$V*` action arrays and symbol arrays | `jison-table-construction.mjs -> emitJisonStates`: shared typed actions and ordered symbol arrays | **Platform adaptation**, implemented: immutable typed cells replace numeric JavaScript arrays without changing values |
| 3 | Generated `o(k, v, o2, l)` fills object properties with a repeated action and explicit overrides | `JisonTableConstruction.kt -> buildJisonState` | **Platform adaptation**, implemented: ordered input includes override keys and preserves JavaScript numeric property iteration order |
| 4 | Generated `table` array/object initialization | Existing `states` array and `stateChunk*` boundaries with shared construction | **Platform adaptation**, independently verified: same array indices, keys, actions, arguments, and key order |
| 5 | Generated parser `parse -> table[state][symbol]` | Existing family runtime or semantic parser lookup in `states[state]` | **Exact**, unchanged |
| 6 | Shift, reduce, accept, goto, and semantic `performAction` dispatch | Existing family `*JisonParser` and `*JisonRuntime` methods | **Exact / existing platform adaptation**, unchanged by this slice |
| 7 | Generated lexer `rules`, `conditions`, and lexer actions | Existing lexer patterns, conditions, and runtime actions | **Existing platform adaptation**, unchanged |

No unsupported stage is introduced or concealed by table construction.
The generated keys and cells must remain identical to the upstream parser
before any implementation can be considered verified.

## Adaptation contract

- Shared actions are immutable; parsers only inspect their typed fields.
- Repeated symbol lists are immutable and only read by construction.
- The native helper builds the same read-only Map interface already consumed by
  production parsers.
- All final keys retain the ascending numeric order produced by JavaScript
  `Object.entries`; this also preserves expected-token ordering in diagnostics.
- Construction receives generated integer arrays and typed actions. It does
  not decode external text, access resources, or introduce a new parse-failure
  or asynchronous initialization protocol.
- The generator may choose a repeated action for compact construction, but this
  choice never changes a table entry, parser branch, or input-dependent behavior.

## Verification required

1. Compare every generated state and cell against the frozen upstream parser,
   including goto values, action tags, arguments, missing entries, and key order.
2. Verify shared construction against empty, repeated, overridden, and distinct
   action rows. Verify deterministic regeneration.
3. Run existing parser, official-documentation, production-corpus, invalid-input,
   and engine safety tests for the affected families.
4. Run the repository's native and official visual gates without changing their
   source inputs or promoting existing Review results.
5. Measure baseline and candidate with the same iOS Release compiler, target,
   framework exports, link conditions, and resource inventory. Static archive
   bytes, linked executable bytes, and compressed distribution bytes must be
   reported separately.
6. Treat any older consumer LinkMap as a historical reference unless that exact
   consumer is rebuilt with the candidate. Do not present subtraction estimates
   as a newly measured consumer package size.

## Recorded verification

- `generate-jison-table-oracle.mjs` independently reads Mermaid `12.0.0`
  JavaScript objects. The [oracle manifest](../mermaid-core/src/jvmTest/resources/jison-tables/manifest.json)
  records distribution hashes, state counts and every row fixture's hash.
- `JisonTableOfficialParityTest` compares all 1,611 states / 20,857 entries,
  including numeric key order, goto/shift/reduce arguments and accept actions.
  All 12 family tests pass.
- `JisonTableConstructionTest` covers empty, singleton, repeated and overridden
  rows, shared immutable cells and input preservation. All three tests pass.
- Both emitter tests pass. The full JVM suites pass: Core 1,123 and Compose 75.
- The [size report](jison-table-size-report.md) records identical-linker
  before/after measurements and the separate historical binary comparison.
- Platform and release gates retain their existing mandatory workflows.
  Large historical visual and device matrices are not promoted to fresh
  evidence by table equality or by these size measurements.
