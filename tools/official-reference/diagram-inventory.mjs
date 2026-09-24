export const mermaidBaseline = Object.freeze({
  version: '12.0.0',
  sourceCommit: '98a0945418c76238f15df2afaddbba4272656c3b',
  zenUmlVersion: '1.0.0',
  zenUmlCoreVersion: '3.49.2',
});

export const internalRegistryIds = Object.freeze(['error', '---', 'info']);

export const diagramFamilies = Object.freeze([
  family('agentflow', 'Agentflow', 'agentflow.md', ['agentflow'], [
    header('agentflow-beta', 'agentflow'),
  ], true),
  family('architecture', 'Architecture', 'architecture.md', ['architecture'], [
    header('architecture-beta', 'architecture'),
    header('architecture', 'architecture'),
  ], true),
  family('block', 'Block', 'block.md', ['block'], [
    header('block-beta', 'block'),
    header('block', 'block'),
  ], true),
  family('c4', 'C4', 'c4.md', ['c4'], [
    header('C4Context', 'c4'),
    header('C4Container', 'c4'),
    header('C4Component', 'c4'),
    header('C4Dynamic', 'c4'),
    header('C4Deployment', 'c4'),
  ], true),
  family('class', 'Class', 'classDiagram.md', ['classDiagram'], [
    header('classDiagram', 'classDiagram'),
    header('classDiagram-v2', 'classDiagram'),
  ], true),
  family('cynefin', 'Cynefin', 'cynefin.md', ['cynefin'], [
    header('cynefin-beta', 'cynefin'),
  ], true),
  family('er', 'Entity Relationship', 'entityRelationshipDiagram.md', ['er'], [
    header('erDiagram', 'er'),
  ], true),
  family('eventmodeling', 'Event Modeling', 'eventmodeling.md', ['eventmodeling'], [
    header('eventmodeling', 'eventmodeling'),
  ], true),
  family('flowchart', 'Flowchart', 'flowchart.md', ['flowchart-v2', 'flowchart-elk'], [
    header('flowchart LR', 'flowchart-v2'),
    header('graph LR', 'flowchart-v2'),
  ], true),
  family('gantt', 'Gantt', 'gantt.md', ['gantt'], [
    header('gantt', 'gantt'),
  ], true),
  family('gitgraph', 'Git Graph', 'gitgraph.md', ['gitGraph'], [
    header('gitGraph', 'gitGraph'),
  ], true),
  family('ishikawa', 'Ishikawa', 'ishikawa.md', ['ishikawa'], [
    header('ishikawa-beta', 'ishikawa'),
    header('ishikawa', 'ishikawa'),
  ], true),
  family('kanban', 'Kanban', 'kanban.md', ['kanban'], [
    header('kanban', 'kanban'),
  ], true),
  family('mindmap', 'Mindmap', 'mindmap.md', ['mindmap'], [
    header('mindmap', 'mindmap'),
  ], true),
  family('packet', 'Packet', 'packet.md', ['packet'], [
    header('packet-beta', 'packet'),
    header('packet', 'packet'),
  ], true),
  family('pie', 'Pie', 'pie.md', ['pie'], [
    header('pie', 'pie'),
  ], true),
  family('quadrant', 'Quadrant Chart', 'quadrantChart.md', ['quadrantChart'], [
    header('quadrantChart', 'quadrantChart'),
  ], true),
  family('radar', 'Radar', 'radar.md', ['radar'], [
    header('radar-beta', 'radar'),
  ], true),
  family(
    'railroad',
    'Railroad',
    'railroad.md',
    ['railroad', 'railroadEbnf', 'railroadAbnf', 'railroadPeg'],
    [
      header('railroad-beta', 'railroad'),
      header('railroad-ebnf-beta', 'railroadEbnf'),
      header('railroad-abnf-beta', 'railroadAbnf'),
      header('railroad-peg-beta', 'railroadPeg'),
    ],
    true,
  ),
  family('requirement', 'Requirement', 'requirementDiagram.md', ['requirement'], [
    header('requirementDiagram', 'requirement'),
    header('requirement', 'requirement'),
  ], true),
  family('sankey', 'Sankey', 'sankey.md', ['sankey'], [
    header('sankey-beta', 'sankey'),
    header('sankey', 'sankey'),
  ], true),
  family('sequence', 'Sequence', 'sequenceDiagram.md', ['sequence'], [
    header('sequenceDiagram', 'sequence'),
  ], true),
  family('state', 'State', 'stateDiagram.md', ['stateDiagram'], [
    header('stateDiagram', 'stateDiagram'),
    header('stateDiagram-v2', 'stateDiagram'),
  ], true),
  family('swimlanes', 'Swimlanes', 'swimlanes.md', ['swimlane'], [
    header('swimlane-beta', 'swimlane'),
  ], true),
  family('timeline', 'Timeline', 'timeline.md', ['timeline'], [
    header('timeline', 'timeline'),
  ], true),
  family('treemap', 'Treemap', 'treemap.md', ['treemap'], [
    header('treemap-beta', 'treemap'),
    header('treemap', 'treemap'),
  ], true),
  family('treeview', 'TreeView', 'treeView.md', ['treeView'], [
    header('treeView-beta', 'treeView'),
  ], true),
  family('usecase', 'Use Case', 'usecase.md', ['usecase'], [
    header('usecase-beta', 'usecase'),
  ], true),
  family('journey', 'User Journey', 'userJourney.md', ['journey'], [
    header('journey', 'journey'),
  ], true),
  family('venn', 'Venn', 'venn.md', ['venn'], [
    header('venn-beta', 'venn'),
  ], true),
  family('wardley', 'Wardley Map', 'wardley.md', ['wardley'], [
    header('wardley-beta', 'wardley'),
  ], true),
  family('xychart', 'XY Chart', 'xyChart.md', ['xychart'], [
    header('xychart', 'xychart'),
    header('xychart-beta', 'xychart'),
  ], true),
  Object.freeze({
    id: 'zenuml',
    title: 'ZenUML',
    documentation: 'zenuml.md',
    registryIds: Object.freeze([]),
    headers: Object.freeze([header('zenuml', null)]),
    implemented: true,
    officialExternalPlugin: Object.freeze({
      packageName: '@mermaid-js/mermaid-zenuml',
      version: mermaidBaseline.zenUmlVersion,
      registryId: 'zenuml',
    }),
  }),
]);

export const implementedDiagramFamilies = Object.freeze(
  diagramFamilies.filter(({ implemented }) => implemented),
);

export const pendingDiagramFamilies = Object.freeze(
  diagramFamilies.filter(({ implemented }) => !implemented),
);

function family(
  id,
  title,
  documentation,
  registryIds,
  headers,
  implemented = false,
) {
  return Object.freeze({
    id,
    title,
    documentation,
    registryIds: Object.freeze(registryIds),
    headers: Object.freeze(headers),
    implemented,
    officialExternalPlugin: null,
  });
}

function header(source, registryId) {
  return Object.freeze({ source, registryId });
}
