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

const malformedSources = Object.freeze([
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

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/mermaid.ts -> parse
 * packages/mermaid/src/mermaidAPI.ts -> render
 *
 * Each source is intentionally malformed. The visual gate requires both
 * renderers to show a non-empty error state and requires Native to classify
 * the result as CONTENT_ERROR.
 */
export const cases = Object.freeze(
  malformedSources.map(([kind, source]) => Object.freeze({
    id: `invalid_${kind}_001`,
    kind,
    title: `${kindTitles[kind]} malformed source`,
    scenario:
      `Malformed ${kindTitles[kind]} syntax must report an error without ` +
      'crashing the renderer.',
    source,
    layout: 'dagre',
    aspectRatio: 4 / 3,
    expectedTexts: Object.freeze([]),
    features: Object.freeze([
      'expected-error',
      'native-content-error',
      'no-crash',
    ]),
    expectedOutcome: 'error',
    expectedNativeErrorType: 'CONTENT_ERROR',
  })),
);
