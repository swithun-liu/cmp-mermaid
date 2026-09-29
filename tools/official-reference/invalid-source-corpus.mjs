const kindTitles = Object.freeze({
  flowchart: 'Flowchart',
  swimlanes: 'Swimlanes',
  architecture: 'Architecture',
  c4: 'C4',
  railroad: 'Railroad',
  treeview: 'TreeView',
  xychart: 'XY Chart',
  quadrant: 'Quadrant Chart',
  timeline: 'Timeline',
  kanban: 'Kanban',
  sequence: 'Sequence',
  class: 'Class',
  state: 'State',
  er: 'Entity Relationship',
  gantt: 'Gantt',
  pie: 'Pie',
  journey: 'User Journey',
  requirement: 'Requirement',
  gitgraph: 'Git Graph',
  mindmap: 'Mindmap',
  packet: 'Packet',
  radar: 'Radar',
  sankey: 'Sankey',
  treemap: 'Treemap',
  venn: 'Venn',
  ishikawa: 'Ishikawa',
  cynefin: 'Cynefin',
  block: 'Block',
  eventmodeling: 'Event Modeling',
  agentflow: 'Agentflow',
  usecase: 'Use Case',
  wardley: 'Wardley Map',
  zenuml: 'ZenUML',
});

const malformedSeeds = Object.freeze([
  ['flowchart', 'flowchart TD\nA['],
  ['swimlanes', 'swimlane-beta TD\nA['],
  [
    'architecture',
    'architecture-beta\n  service app(server)[Application] in missing',
  ],
  ['c4', 'C4Context\nUnknown(a, "A")'],
  ['railroad', 'railroad-beta\nrule = terminal("a")'],
  ['treeview', 'treeView-beta\n"unterminated'],
  ['xychart', 'xychart-beta\nx-axis [a, b]\nline [1,]'],
  ['quadrant', 'quadrantChart\nPoint: [0.2, 1.1]'],
  ['timeline', 'timeline\n  : orphan event'],
  ['kanban', 'kanban\n  root@{ ticket: ['],
  ['sequence', 'sequenceDiagram\n  Alice->>:missing participant'],
  ['class', 'classDiagram\nclass A {'],
  ['state', 'stateDiagram-v2\nstate A {'],
  ['er', 'erDiagram\nA ||--o{'],
  ['gantt', 'gantt\nsection'],
  ['pie', 'pie\n"A" : nope'],
  ['journey', 'journey\n:'],
  [
    'requirement',
    'requirementDiagram\n  requirement broken {\n    risk: impossible\n  }',
  ],
  ['gitgraph', 'gitGraph XX:\ncommit'],
  ['mindmap', 'mindmap\n  Root\nOther root'],
  ['packet', 'packet\n  +0: "zero"'],
  ['radar', 'radar-beta\naxis'],
  ['sankey', 'sankey-beta\nA,B'],
  ['treemap', 'treemap-beta\n"Root"\n  "Leaf": nope'],
  ['venn', 'venn-beta\n  set A\n  union A,B'],
  ['ishikawa', 'ishikawaish\nEffect'],
  ['cynefin', 'cynefin-beta\nunknown'],
  ['block', 'block\nA["unterminated'],
  ['eventmodeling', 'eventmodeling\ntf 1000 ui UI'],
  ['agentflow', 'agentflow-beta TB\n  flow orphan["Orphan"]\n    a --> b'],
  ['usecase', 'usecase-beta\nunknown command'],
  ['wardley', 'wardley-beta\ncomponent A [0, 1]'],
  ['zenuml', 'zenu-ml\nA.m()'],
]);

export const casesPerKind = 256;

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/mermaid.ts -> parse
 * packages/mermaid/src/mermaidAPI.ts -> render
 *
 * Each seed is intentionally malformed. The first profile preserves the seed
 * verbatim for the CI smoke gate. The remaining profiles add deterministic
 * comment, blank-line, and line-ending contexts without changing the malformed
 * syntax. The visual gate requires both renderers to show a non-empty error
 * state and requires Native to classify the result as CONTENT_ERROR.
 */
export const cases = Object.freeze(
  malformedSeeds.flatMap(([kind, seedSource]) =>
    Array.from({ length: casesPerKind }, (_, profileIndex) => {
      const profileNumber = profileIndex + 1;
      const profileId = String(profileNumber).padStart(3, '0');
      return Object.freeze({
        id: `invalid_${kind}_${profileId}`,
        seedId: `invalid_${kind}_seed`,
        profileId: `profile_${profileId}`,
        profileIndex: profileNumber,
        kind,
        diagramTitle: kindTitles[kind],
        title:
          profileNumber === 1
            ? `${kindTitles[kind]} malformed source`
            : `${kindTitles[kind]} malformed source variant ${profileId}`,
        scenario:
          `Malformed ${kindTitles[kind]} syntax under deterministic context ` +
          `${profileId} must report an error without crashing the renderer.`,
        source: buildVariantSource(seedSource, profileNumber),
        layout: 'dagre',
        aspectRatio: 4 / 3,
        expectedTexts: Object.freeze([]),
        features: Object.freeze([
          'expected-error',
          'native-content-error',
          'no-crash',
          'deterministic-context-variant',
        ]),
        expectedOutcome: 'error',
        expectedNativeErrorType: 'CONTENT_ERROR',
      });
    }),
  ),
);

function buildVariantSource(seedSource, profileNumber) {
  if (profileNumber === 1) {
    return seedSource;
  }

  const variantIndex = profileNumber - 1;
  const profileId = String(profileNumber).padStart(3, '0');
  const lineEnding = (variantIndex & 0x40) === 0 ? '\n' : '\r\n';
  const leadingBlankLineCount = variantIndex & 0x03;
  const trailingBlankLineCount = (variantIndex >> 2) & 0x03;
  const contextLineCount = ((variantIndex >> 4) & 0x03) + 1;
  const commentSpacer = (variantIndex & 0x80) === 0 ? ' ' : '  ';
  const contextLines = Array.from(
    { length: contextLineCount },
    (_, index) =>
      index === 0
        ? `%%${commentSpacer}invalid-source-profile:${profileId}`
        : `%%${commentSpacer}invalid-source-context:${index}`,
  );

  return (
    lineEnding.repeat(leadingBlankLineCount) +
    contextLines.join(lineEnding) +
    lineEnding +
    seedSource +
    lineEnding.repeat(trailingBlankLineCount)
  );
}
