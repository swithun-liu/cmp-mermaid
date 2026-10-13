import { createHash } from 'node:crypto';
import {
  flowchartCoverageContract,
  renderFlowchartCoverageScenario,
} from './flowchart-coverage-corpus.mjs';
import {
  generatedFamilyCoverageContracts,
  renderFamilyCoverageScenario,
} from './family-coverage-corpus.mjs';
import { businessScenarios } from './independent-scenario-catalog.mjs';
import {
  familyDimensionSpecs,
} from './visual-input-space.mjs';

export {
  familyDimensionSpecs,
  flowchartCoverageContract,
  generatedFamilyCoverageContracts,
};

export const kinds = [
  'flowchart',
  'swimlanes',
  'architecture',
  'c4',
  'railroad',
  'treeview',
  'xychart',
  'quadrant',
  'timeline',
  'kanban',
  'sequence',
  'class',
  'state',
  'er',
  'gantt',
  'pie',
  'journey',
  'requirement',
  'gitgraph',
  'mindmap',
  'packet',
  'radar',
  'sankey',
  'treemap',
  'venn',
  'ishikawa',
  'cynefin',
  'block',
  'eventmodeling',
  'agentflow',
  'usecase',
  'wardley',
  'zenuml',
];

export const casesPerKind = 256;

export const familyCoverageContracts = Object.freeze(
  Object.fromEntries(kinds.map((kind) => [
    kind,
    kind === 'flowchart'
      ? flowchartCoverageContract
      : generatedFamilyCoverageContracts[kind],
  ])),
);

const aspectRatios = Object.freeze({
  flowchart: 1.7,
  swimlanes: 1.7,
  architecture: 1.7,
  c4: 1.7,
  railroad: 1.8,
  treeview: 1.3,
  xychart: 1.5,
  quadrant: 1.1,
  timeline: 1.8,
  kanban: 1.6,
  sequence: 1.4,
  class: 1.6,
  state: 1.6,
  er: 1.6,
  gantt: 1.8,
  pie: 1.2,
  journey: 1.7,
  requirement: 1.7,
  gitgraph: 1.8,
  mindmap: 1.4,
  packet: 1.8,
  radar: 1.2,
  sankey: 1.7,
  treemap: 1.4,
  venn: 1.2,
  ishikawa: 1.8,
  cynefin: 1.5,
  block: 1.6,
  eventmodeling: 1.8,
  agentflow: 1.7,
  usecase: 1.6,
  wardley: 1.7,
  zenuml: 1.5,
});

export const scenarioBlueprintsByKind = Object.freeze(
  Object.fromEntries(kinds.map((kind) => [
    kind,
    Object.freeze(Array.from(
      { length: casesPerKind },
      (_, index) => createScenarioBlueprint(kind, index),
    )),
  ])),
);

export const cases = Object.freeze(
  kinds.flatMap((kind) => scenarioBlueprintsByKind[kind]),
);

export const corpusSummary = Object.freeze({
  diagramFamilyCount: kinds.length,
  structuralCaseCount: cases.length,
  structuralCasesPerKind: casesPerKind,
  independentScenarioCount: cases.length,
  countsByKind: Object.freeze(kinds.map((kind) => {
    const kindCases = scenarioBlueprintsByKind[kind];
    return Object.freeze({
      kind,
      independentScenarioCount: kindCases.length,
      uniqueBlueprintCount:
        new Set(kindCases.map((entry) => entry.scenarioBlueprintId)).size,
      normalizedSemanticStructureCount:
        new Set(kindCases.map((entry) => entry.normalizedSemanticSignature)).size,
      structuralSignatureCount:
        new Set(kindCases.map((entry) => entry.structuralSignature)).size,
      structuralProfileCount:
        new Set(kindCases.map((entry) =>
          JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
        )).size,
      coverageContract: familyCoverageContracts[kind],
    });
  })),
});

validateCorpus();

function createScenarioBlueprint(kind, index) {
  const ordinal = index + 1;
  const businessScenario = businessScenarios[index];
  const domainModel = businessScenario.domain;
  const goalModel = businessScenario.goal;
  const title = businessScenario.title;
  const context = Object.freeze({
    domain: domainModel,
    goal: goalModel,
    title,
    label: (slot) =>
      `${domainModel.object} ${goalModel.stages[slot % goalModel.stages.length]}`,
  });
  const rendered = renderCoverageScenario(kind, index, context);
  const source = `${rendered.source.trim()}\n`;
  const normalizedSemanticStructure = deepFreeze({
    grammar: kind,
    ...(rendered.coverage === undefined
      ? { dimensions: rendered.dimensions }
      : { coverage: rendered.coverage }),
    structuralProfile: rendered.structuralProfile,
    normalizedSource: normalizeSourceWithContext(
      source,
      context,
      rendered.normalizationTexts,
    ),
  });
  const normalizedSemanticSignature = sha256(
    JSON.stringify(normalizedSemanticStructure),
  );
  const sourceSha256 = sha256(source);
  const scenarioBlueprintId = `${kind}/${businessScenario.code}`;
  return Object.freeze({
    id: `visual_${kind}_${String(ordinal).padStart(3, '0')}`,
    kind,
    title,
    scenario:
      `${domainModel.name} ${goalModel.name} is an independently generated ` +
      `${kind} scenario in a fixed coverage-driven release corpus.`,
    layout: rendered.layout ?? 'dagre',
    aspectRatio: aspectRatios[kind],
    features: Object.freeze([
      'independent-scenario-blueprint',
      `scenario-domain:${domainModel.code}`,
      `scenario-goal:${goalModel.code}`,
      ...rendered.features,
      `normalized-semantic-signature:${normalizedSemanticSignature}`,
      `structural-signature:${normalizedSemanticSignature}`,
      `structural-source-sha256:${sourceSha256}`,
    ]),
    expectedTexts: Object.freeze(rendered.expectedTexts),
    coverageRole: 'independent-visual-scenario',
    scenarioBlueprintId,
    normalizedSemanticStructure,
    normalizedSemanticSignature,
    ...(rendered.coverage === undefined
      ? {
          inputDimensions: rendered.dimensions,
          structuralDimensions: rendered.dimensions,
        }
      : {
          coverageCategory: rendered.coverage.category,
          coverageCapabilities: rendered.coverage.capabilities,
          ...(rendered.coverage.focusCapability === undefined
            ? {}
            : {
                coverageFocusCapability:
                  rendered.coverage.focusCapability,
              }),
          ...(rendered.coverage.equivalenceGroup === undefined
            ? {}
            : {
                equivalenceGroup: rendered.coverage.equivalenceGroup,
                syntaxVariant: rendered.coverage.syntaxVariant,
              }),
        }),
    structuralSignature: normalizedSemanticSignature,
    source,
    sourceSha256,
  });
}

export function normalizeScenarioSource(entry) {
  const parts = entry.scenarioBlueprintId.split('/');
  if (parts.length !== 2 || parts[0] !== entry.kind) {
    throw new Error(`Invalid scenario blueprint id: ${entry.scenarioBlueprintId}`);
  }
  const businessScenario = businessScenarios.find(
    (candidate) => candidate.code === parts[1],
  );
  if (businessScenario === undefined) {
    throw new Error(`Unknown scenario blueprint id: ${entry.scenarioBlueprintId}`);
  }
  const domainModel = businessScenario.domain;
  const goalModel = businessScenario.goal;
  const context = {
    domain: domainModel,
    goal: goalModel,
    title: businessScenario.title,
    label: (slot) =>
      `${domainModel.object} ${goalModel.stages[slot % goalModel.stages.length]}`,
  };
  const match = entry.id.match(/_(\d{3})$/);
  if (match === null) {
    throw new Error(`Invalid independent visual case id: ${entry.id}`);
  }
  const index = Number(match[1]) - 1;
  const rendered = renderCoverageScenario(entry.kind, index, context);
  return normalizeSourceWithContext(
    entry.source,
    context,
    rendered.normalizationTexts,
  );
}

function normalizeSourceWithContext(source, context, additionalTexts = []) {
  const scenarioText = new Set([
    context.title,
    context.domain.code,
    context.domain.name,
    context.domain.actor,
    context.domain.platform,
    context.domain.object,
    context.goal.code,
    context.goal.name,
    ...context.goal.stages,
    ...context.goal.stages.map((stage) => stage.toLowerCase()),
    ...context.goal.stages.map(identifierToken),
    ...Array.from({ length: 8 }, (_, index) => context.label(index)),
    ...additionalTexts,
  ]);
  let normalized = source;
  for (const value of [...scenarioText].sort((left, right) =>
    right.length - left.length
  )) {
    normalized = normalized.replaceAll(value, 'TEXT');
  }
  return normalized
    .split(/\r?\n/)
    .map((line) => line.trimEnd())
    .filter((line) => line.trim().length > 0)
    .join('\n');
}

function renderCoverageScenario(kind, index, context) {
  return kind === 'flowchart'
    ? renderFlowchartCoverageScenario(index, context)
    : renderFamilyCoverageScenario(kind, index, context);
}

function renderFamilyScenario(kind, bits, context) {
  switch (kind) {
    case 'flowchart':
      return renderFlowchart(bits, context);
    case 'swimlanes':
      return renderSwimlanes(bits, context);
    case 'architecture':
      return renderArchitecture(bits, context);
    case 'c4':
      return renderC4(bits, context);
    case 'railroad':
      return renderRailroad(bits, context);
    case 'treeview':
      return renderTreeView(bits, context);
    case 'xychart':
      return renderXyChart(bits, context);
    case 'quadrant':
      return renderQuadrant(bits, context);
    case 'timeline':
      return renderTimeline(bits, context);
    case 'kanban':
      return renderKanban(bits, context);
    case 'sequence':
      return renderSequence(bits, context);
    case 'class':
      return renderClass(bits, context);
    case 'state':
      return renderState(bits, context);
    case 'er':
      return renderEr(bits, context);
    case 'gantt':
      return renderGantt(bits, context);
    case 'pie':
      return renderPie(bits, context);
    case 'journey':
      return renderJourney(bits, context);
    case 'requirement':
      return renderRequirement(bits, context);
    case 'gitgraph':
      return renderGitGraph(bits, context);
    case 'mindmap':
      return renderMindmap(bits, context);
    case 'packet':
      return renderPacket(bits, context);
    case 'radar':
      return renderRadar(bits, context);
    case 'sankey':
      return renderSankey(bits, context);
    case 'treemap':
      return renderTreemap(bits, context);
    case 'venn':
      return renderVenn(bits, context);
    case 'ishikawa':
      return renderIshikawa(bits, context);
    case 'cynefin':
      return renderCynefin(bits, context);
    case 'block':
      return renderBlock(bits, context);
    case 'eventmodeling':
      return renderEventModeling(bits, context);
    case 'agentflow':
      return renderAgentflow(bits, context);
    case 'usecase':
      return renderUseCase(bits, context);
    case 'wardley':
      return renderWardley(bits, context);
    case 'zenuml':
      return renderZenUml(bits, context);
    default:
      throw new Error(`Unsupported independent visual kind: ${kind}`);
  }
}

function renderFlowchart(bits, context) {
  const lines = ['flowchart LR', `  Start([${context.title}])`];
  bits.forEach((bit, index) => {
    const id = `N${index + 1}`;
    const label = context.label(index);
    lines.push(bit === 0 ? `  ${id}[${label}]` : `  ${id}{${label}?}`);
  });
  lines.push('  Start --> N1');
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  N${index} --> N${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'process-node',
    'decision-node',
    context,
    ['flow-topology', 'node-shapes'],
  );
}

function renderSwimlanes(bits, context) {
  const lanes = [
    ['Intake', []],
    ['Assessment', []],
    ['Execution', []],
    ['Closure', []],
  ];
  bits.forEach((bit, index) => {
    const id = `N${index + 1}`;
    const label = context.label(index);
    const node = bit === 0 ? `${id}[${label}]` : `${id}{${label}?}`;
    lanes[index % lanes.length][1].push(`    ${node}`);
  });
  const lines = ['swimlane-beta LR'];
  lanes.forEach(([name, nodes]) => {
    lines.push(`  subgraph ${name}`, ...nodes, '  end');
  });
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  N${index} --> N${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'lane-process',
    'lane-decision',
    context,
    ['lane-ownership', 'cross-lane-flow'],
    [context.label(0)],
  );
}

function renderArchitecture(bits, context) {
  const lines = [
    'architecture-beta',
    `  group platform(cloud)[${context.title}]`,
  ];
  bits.forEach((bit, index) => {
    const icon = bit === 0 ? 'server' : 'database';
    lines.push(
      `  service s${index + 1}(${icon})[${context.label(index)}] in platform`,
    );
  });
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  s${index}:R --> L:s${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'compute-service',
    'data-service',
    context,
    ['services', 'directional-ports'],
  );
}

function renderC4(bits, context) {
  const lines = ['C4Context', `  title ${context.title}`];
  bits.forEach((bit, index) => {
    const constructor = bit === 0 ? 'Person' : 'System';
    lines.push(
      `  ${constructor}(e${index + 1}, "${context.label(index)}", ` +
        `"${context.goal.stages[index % 4]} responsibility")`,
    );
  });
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  Rel(e${index}, e${index + 1}, "hands off")`);
  }
  return scenario(
    lines,
    bits,
    'person-element',
    'system-element',
    context,
    ['context-elements', 'relationships'],
  );
}

function renderRailroad(bits, context) {
  const expressions = bits.map((bit, index) => {
    const token = context.goal.stages[index % 4].toLowerCase();
    return bit === 0
      ? `    terminal("${token}-${index + 1}")`
      : `    optional(nonterminal("${token}-${index + 1}"))`;
  });
  const lines = ['railroad-beta', '  process = sequence('];
  expressions.forEach((expression, index) => {
    lines.push(`${expression}${index === expressions.length - 1 ? '' : ','}`);
  });
  lines.push('  ) ;');
  return scenario(
    lines,
    bits,
    'required-terminal',
    'optional-nonterminal',
    context,
    ['sequence', 'optional'],
    [context.goal.stages[0].toLowerCase()],
  );
}

function renderTreeView(bits, context) {
  const lines = ['treeView-beta', `    ${context.domain.code}/`];
  bits.forEach((bit, index) => {
    const name = `${context.goal.code}-${index + 1}`;
    if (bit === 0) {
      lines.push(`        ${name}.md`);
    } else {
      lines.push(`        ${name}/`, '            index.kt');
    }
  });
  return scenario(
    lines,
    bits,
    'leaf-file',
    'nested-directory',
    context,
    ['indentation', 'files-and-directories'],
    [context.goal.code],
  );
}

function renderXyChart(bits, context) {
  const categories = context.goal.stages
    .flatMap((stage) => [`${stage} A`, `${stage} B`]);
  const lines = [
    'xychart',
    `  title "${context.title}"`,
    `  x-axis [${categories.map((value) => `"${value}"`).join(', ')}]`,
    '  y-axis "Signal" 0 --> 100',
  ];
  bits.forEach((bit, index) => {
    const values = Array.from(
      { length: categories.length },
      (_, point) => 12 + index * 7 + ((point * 9 + index * 3) % 31),
    );
    const plot = bit === 0 ? 'line' : 'bar';
    lines.push(`  ${plot} "Signal ${index + 1}" [${values.join(', ')}]`);
  });
  return scenario(
    lines,
    bits,
    'line-series',
    'bar-series',
    context,
    ['mixed-plots', 'categorical-axis'],
  );
}

function renderQuadrant(bits, context) {
  const lines = [
    'quadrantChart',
    `  title ${context.title}`,
    '  x-axis Earlier --> Later',
    '  y-axis Lower impact --> Higher impact',
    '  quadrant-1 Prioritize',
    '  quadrant-2 Prepare',
    '  quadrant-3 Observe',
    '  quadrant-4 Execute',
  ];
  bits.forEach((bit, index) => {
    const x = bit === 0 ? 0.22 + (index % 2) * 0.08 : 0.68 + (index % 2) * 0.08;
    const y = 0.14 + index * 0.1;
    lines.push(
      `  "${context.label(index)}": [${x.toFixed(2)}, ${y.toFixed(2)}]`,
    );
  });
  return scenario(
    lines,
    bits,
    'left-half-point',
    'right-half-point',
    context,
    ['points', 'quadrant-placement'],
  );
}

function renderTimeline(bits, context) {
  const lines = ['timeline', `  title ${context.title}`, `  Baseline : ${context.domain.object}`];
  bits.forEach((bit, index) => {
    const event = `${context.goal.stages[index % 4]} phase ${index + 1}`;
    lines.push(
      bit === 0
        ? `  Period ${index + 1} : ${event}`
        : `       : ${event}`,
    );
  });
  return scenario(
    lines,
    bits,
    'new-period',
    'continued-period',
    context,
    ['periods', 'continued-events'],
  );
}

function renderKanban(bits, context) {
  const intake = [];
  const review = [];
  bits.forEach((bit, index) => {
    const task = `    task${index + 1}[${context.label(index)}]`;
    (bit === 0 ? intake : review).push(task);
  });
  const lines = [
    'kanban',
    '  intake[Intake]',
    ...intake,
    '  review[Review]',
    ...review,
  ];
  return scenario(
    lines,
    bits,
    'intake-lane',
    'review-lane',
    context,
    ['sections', 'task-placement'],
    [context.label(0)],
  );
}

function renderSequence(bits, context) {
  const lines = [
    'sequenceDiagram',
    `  actor User as ${context.domain.actor}`,
    `  participant Platform as ${context.domain.platform}`,
    '  participant Worker as Specialist',
  ];
  bits.forEach((bit, index) => {
    lines.push(
      bit === 0
        ? `  User->>Platform: ${context.label(index)}`
        : `  Platform-->>Worker: ${context.label(index)}`,
    );
  });
  return scenario(
    lines,
    bits,
    'user-request',
    'platform-delegation',
    context,
    ['participants', 'message-direction'],
    [context.label(0)],
  );
}

function renderClass(bits, context) {
  const lines = ['classDiagram', '  direction LR'];
  for (let index = 0; index <= bits.length; index += 1) {
    lines.push(
      `  class C${index} {`,
      `    +String ${context.goal.stages[index % 4].toLowerCase()}`,
      '  }',
    );
  }
  bits.forEach((bit, index) => {
    const relation = bit === 0 ? '*--' : '..>';
    lines.push(`  C${index} ${relation} C${index + 1} : step`);
  });
  return scenario(
    lines,
    bits,
    'composition-relation',
    'dependency-relation',
    context,
    ['classes', 'relation-types'],
    ['C0'],
  );
}

function renderState(bits, context) {
  const lines = ['stateDiagram-v2', '  [*] --> S0'];
  bits.forEach((bit, index) => {
    const from = bit === 0 ? `S${index}` : `S${index + 1}`;
    const to = bit === 0 ? `S${index + 1}` : `S${index}`;
    lines.push(`  ${from} --> ${to} : ${context.goal.stages[index % 4]}`);
  });
  lines.push('  S8 --> [*]');
  return scenario(
    lines,
    bits,
    'forward-transition',
    'recovery-transition',
    context,
    ['states', 'transition-direction'],
    ['S0'],
  );
}

function renderEr(bits, context) {
  const lines = ['erDiagram'];
  for (let index = 0; index <= bits.length; index += 1) {
    lines.push(
      `  ENTITY_${index} {`,
      '    bigint id PK',
      `    string ${context.goal.stages[index % 4].toLowerCase()}`,
      '  }',
    );
  }
  bits.forEach((bit, index) => {
    const relation = bit === 0 ? '||--o{' : '}o--||';
    lines.push(
      `  ENTITY_${index} ${relation} ENTITY_${index + 1} : relates`,
    );
  });
  return scenario(
    lines,
    bits,
    'one-to-many',
    'many-to-one',
    context,
    ['entities', 'cardinality-direction'],
    ['ENTITY_0'],
  );
}

function renderGantt(bits, context) {
  const lines = [
    'gantt',
    `  title ${context.title}`,
    '  dateFormat YYYY-MM-DD',
    '  todayMarker off',
    '  section Delivery',
  ];
  let previous = null;
  bits.forEach((bit, index) => {
    const id = `task_${index + 1}`;
    const schedule = previous === null
      ? '2027-01-01'
      : `after ${previous}`;
    const state = bit === 0 ? 'crit' : 'active';
    lines.push(
      `  ${context.label(index)} :${state}, ${id}, ${schedule}, 2d`,
    );
    previous = id;
  });
  return scenario(
    lines,
    bits,
    'critical-task',
    'active-task',
    context,
    ['tasks', 'task-states'],
  );
}

function renderPie(bits, context) {
  const lines = ['pie showData', `  title ${context.title}`];
  bits.forEach((bit, index) => {
    const value = (index + 1) * 3 + (bit === 0 ? 1 : index + 4);
    lines.push(`  "${context.label(index)}" : ${value}`);
  });
  return scenario(
    lines,
    bits,
    'baseline-allocation',
    'expanded-allocation',
    context,
    ['slices', 'non-proportional-values'],
  );
}

function renderJourney(bits, context) {
  const lines = ['journey', `  title ${context.title}`, '  section Experience'];
  bits.forEach((bit, index) => {
    const actors = bit === 0
      ? context.domain.actor
      : `${context.domain.actor}, ${context.domain.platform}`;
    const score = bit === 0 ? 2 : 5;
    lines.push(`    ${context.label(index)}: ${score}: ${actors}`);
  });
  return scenario(
    lines,
    bits,
    'single-actor-low-score',
    'shared-high-score',
    context,
    ['journey-tasks', 'actor-participation'],
  );
}

function renderRequirement(bits, context) {
  const lines = ['requirementDiagram', '  direction LR'];
  bits.forEach((bit, index) => {
    const type = bit === 0 ? 'functionalRequirement' : 'performanceRequirement';
    lines.push(
      `  ${type} R${index + 1} {`,
      `    id: "REQ-${index + 1}"`,
      `    text: "${context.label(index)}"`,
      `    risk: ${bit === 0 ? 'medium' : 'high'}`,
      `    verifyMethod: ${bit === 0 ? 'test' : 'analysis'}`,
      '  }',
    );
  });
  for (let index = 1; index < bits.length; index += 1) {
    const relation = bits[index] === 0 ? 'contains' : 'derives';
    lines.push(`  R${index} - ${relation} -> R${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'functional-requirement',
    'performance-requirement',
    context,
    ['requirement-types', 'requirement-relations'],
    ['REQ-1'],
  );
}

function renderGitGraph(bits, context) {
  const lines = ['gitGraph LR:', '  commit id: "baseline"'];
  bits.forEach((bit, index) => {
    const suffix = bit === 0 ? '' : ' type: HIGHLIGHT';
    lines.push(
      `  commit id: "${context.goal.code}-${index + 1}"${suffix}`,
    );
  });
  return scenario(
    lines,
    bits,
    'normal-commit',
    'highlighted-commit',
    context,
    ['commits', 'commit-types'],
    [context.goal.code],
  );
}

function renderMindmap(bits, context) {
  const lines = ['mindmap', `  root((${context.title}))`];
  bits.forEach((bit, index) => {
    if (bit === 0) {
      lines.push(`    ${context.label(index)}`);
    } else {
      lines.push(
        `    ${context.goal.stages[index % 4]} branch ${index + 1}`,
        `      ${context.label(index)}`,
      );
    }
  });
  return scenario(
    lines,
    bits,
    'root-child',
    'nested-child',
    context,
    ['hierarchy', 'branch-depth'],
  );
}

function renderPacket(bits, context) {
  const lines = [
    '---',
    `title: ${context.title}`,
    '---',
    'packet',
  ];
  let offset = 0;
  bits.forEach((bit, index) => {
    const width = bit === 0 ? 4 : 8;
    const end = offset + width - 1;
    lines.push(`  ${offset}-${end}: "${context.label(index)}"`);
    offset = end + 1;
  });
  return scenario(
    lines,
    bits,
    'four-bit-field',
    'eight-bit-field',
    context,
    ['field-widths', 'contiguous-ranges'],
  );
}

function renderRadar(bits, context) {
  const axes = bits.map((_, index) => `axis${index + 1}`);
  const values = bits.map(
    (bit, index) => 25 + index * 6 + (bit === 0 ? 0 : index + 9),
  );
  const lines = [
    'radar-beta',
    `  title ${context.title}`,
    `  axis ${axes.map((id, index) =>
      `${id}["${context.goal.stages[index % 4]} ${index + 1}"]`
    ).join(', ')}`,
    `  curve profile["${context.domain.object}"] { ${values.join(', ')} }`,
    '  min 0',
    '  max 100',
    '  ticks 5',
  ];
  return scenario(
    lines,
    bits,
    'baseline-axis-value',
    'elevated-axis-value',
    context,
    ['axes', 'curve-values'],
  );
}

function renderSankey(bits, context) {
  const lines = ['sankey'];
  bits.forEach((bit, index) => {
    const target = bit === 0 ? 'Standard Path' : 'Escalation Path';
    lines.push(
      `${context.goal.stages[index % 4]} ${index + 1},${target},${index + 2}`,
    );
  });
  return scenario(
    lines,
    bits,
    'standard-flow',
    'escalation-flow',
    context,
    ['flow-destinations', 'weighted-links'],
    [context.goal.stages[0]],
  );
}

function renderTreemap(bits, context) {
  const primary = [];
  const secondary = [];
  bits.forEach((bit, index) => {
    const stage = context.goal.stages[index % context.goal.stages.length];
    const line = `    "${stage} ${index + 1}": 40`;
    (bit === 0 ? primary : secondary).push(line);
  });
  const lines = ['treemap-beta', `title ${context.title}`];
  if (primary.length > 0) {
    lines.push('"Main"', ...primary);
  }
  if (secondary.length > 0) {
    lines.push('"Alt"', ...secondary);
  }
  return scenario(
    lines,
    bits,
    'primary-branch',
    'secondary-branch',
    context,
    ['hierarchy', 'branch-membership'],
  );
}

function renderVenn(bits, context) {
  const lines = [
    'venn-beta',
    `  title ${context.title}`,
    '  set A["Need"]:30',
    '  set B["Value"]:30',
    '  set C["Feasibility"]:30',
    '  set D["Safety"]:30',
  ];
  const unions = [
    ['A,B', 'Need and value', 3],
    ['A,C', 'Need and feasibility', 3],
    ['A,D', 'Need and safety', 3],
    ['B,C', 'Value and feasibility', 3],
    ['B,D', 'Value and safety', 3],
    ['C,D', 'Feasibility and safety', 3],
    ['A,B,C', 'Validated value', 1],
    ['B,C,D', 'Deliverable safety', 1],
  ];
  bits.forEach((bit, index) => {
    if (bit === 1) {
      const [members, label, value] = unions[index];
      lines.push(`  union ${members}["${label}"]:${value}`);
    }
  });
  return scenario(
    lines,
    bits,
    'absent-intersection',
    'present-intersection',
    context,
    ['sets', 'intersection-presence'],
    ['Need'],
  );
}

function renderIshikawa(bits, context) {
  const lines = ['ishikawa-beta', context.title];
  bits.forEach((bit, index) => {
    if (bit === 0) {
      lines.push(`  ${context.label(index)}`);
    } else {
      lines.push(
        `  ${context.goal.stages[index % 4]} cause ${index + 1}`,
        `    ${context.label(index)}`,
      );
    }
  });
  return scenario(
    lines,
    bits,
    'direct-cause',
    'nested-cause',
    context,
    ['causes', 'cause-depth'],
  );
}

function renderCynefin(bits, context) {
  const transitions = [
    'complex --> complicated',
    'complicated --> clear',
    'clear --> chaotic',
    'chaotic --> complex',
    'confusion --> complex',
    'confusion --> complicated',
    'confusion --> clear',
    'confusion --> chaotic',
  ];
  const lines = [
    'cynefin-beta',
    `  title ${context.title}`,
    '  clear',
    `    "${context.label(0)}"`,
    '  complicated',
    `    "${context.label(1)}"`,
    '  complex',
    `    "${context.label(2)}"`,
    '  chaotic',
    `    "${context.label(3)}"`,
    '  confusion',
    `    "${context.label(4)}"`,
  ];
  bits.forEach((bit, index) => {
    if (bit === 1) {
      lines.push(`  ${transitions[index]}`);
    }
  });
  return scenario(
    lines,
    bits,
    'domain-transition-absent',
    'domain-transition-present',
    context,
    ['domains', 'cross-domain-transitions'],
  );
}

function renderBlock(bits, context) {
  const lines = ['block', '  columns 4'];
  bits.forEach((bit, index) => {
    const id = `B${index + 1}`;
    lines.push(
      bit === 0
        ? `  ${id}["${context.label(index)}"]`
        : `  ${id}("${context.label(index)}")`,
    );
  });
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  B${index} --> B${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'square-block',
    'rounded-block',
    context,
    ['grid', 'block-shapes'],
    [context.label(0)],
  );
}

function renderEventModeling(bits, context) {
  const lines = ['eventmodeling'];
  bits.forEach((bit, index) => {
    const type = bit === 0 ? 'cmd' : 'evt';
    lines.push(
      `  tf ${String(index + 1).padStart(2, '0')} ${type} ` +
        `${identifierToken(context.goal.stages[index % 4])}${index + 1}`,
    );
  });
  return scenario(
    lines,
    bits,
    'command-frame',
    'event-frame',
    context,
    ['frames', 'frame-types'],
    [context.goal.stages[0]],
  );
}

function renderAgentflow(bits, context) {
  const lines = ['agentflow-beta LR'];
  bits.forEach((bit, index) => {
    const shape = bit === 0 ? 'task' : 'decision';
    lines.push(
      `  A${index + 1}["${context.label(index)}"]@{ shape: ${shape} }`,
    );
  });
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`  A${index} --> A${index + 1}`);
  }
  return scenario(
    lines,
    bits,
    'task-node',
    'decision-node',
    context,
    ['typed-nodes', 'edge-chain'],
    [context.label(0)],
  );
}

function renderUseCase(bits, context) {
  const lines = [
    'usecase-beta',
    'direction LR',
    `actor Primary("${context.domain.actor}")`,
    `systemBoundary platform["${context.domain.platform}"]`,
  ];
  bits.forEach((bit, index) => {
    lines.push(
      bit === 0
        ? `  U${index + 1}("${context.label(index)}")`
        : `  U${index + 1}[${context.label(index)}]`,
    );
  });
  lines.push('end');
  bits.forEach((_, index) => lines.push(`Primary --> U${index + 1}`));
  return scenario(
    lines,
    bits,
    'ellipse-use-case',
    'rectangle-use-case',
    context,
    ['actors', 'use-case-forms'],
    [context.domain.actor],
  );
}

function renderWardley(bits, context) {
  const lines = [
    'wardley-beta',
    `title ${context.title}`,
    'anchor User [0.95, 0.50]',
  ];
  bits.forEach((bit, index) => {
    const visibility = 0.86 - index * 0.08;
    const evolution = bit === 0 ? 0.28 : 0.74;
    lines.push(
      `component "Capability ${index + 1}" ` +
        `[${visibility.toFixed(2)}, ${evolution.toFixed(2)}]`,
    );
  });
  lines.push('User -> "Capability 1"');
  for (let index = 1; index < bits.length; index += 1) {
    lines.push(`"Capability ${index}" -> "Capability ${index + 1}"`);
  }
  return scenario(
    lines,
    bits,
    'custom-evolution',
    'product-evolution',
    context,
    ['components', 'evolution-position'],
    ['Capability 1'],
  );
}

function renderZenUml(bits, context) {
  const lines = [
    'zenuml',
    `title ${context.title}`,
    `Actor as "${context.domain.actor}"`,
    `Platform as "${context.domain.platform}"`,
    'Worker as "Specialist"',
  ];
  bits.forEach((bit, index) => {
    lines.push(
      bit === 0
        ? `Actor->Platform: ${context.label(index)}`
        : `Platform->Worker: ${context.label(index)}`,
    );
  });
  return scenario(
    lines,
    bits,
    'actor-platform-message',
    'platform-worker-message',
    context,
    ['participants', 'message-target'],
  );
}

function scenario(
  lines,
  bits,
  zeroDecision,
  oneDecision,
  context,
  features,
  expectedTexts = [context.title],
) {
  return Object.freeze({
    source: lines.join('\n'),
    decisions: Object.freeze(bits.map(
      (bit, index) =>
        `slot-${index + 1}:${bit === 0 ? zeroDecision : oneDecision}`,
    )),
    features: Object.freeze(features),
    expectedTexts: Object.freeze(expectedTexts),
  });
}

function validateCorpus() {
  const expectedCount = kinds.length * casesPerKind;
  if (cases.length !== expectedCount) {
    throw new Error(
      `Expected ${expectedCount} independent scenarios, found ${cases.length}`,
    );
  }
  const allIds = new Set();
  const allSources = new Set();
  for (const kind of kinds) {
    const kindCases = scenarioBlueprintsByKind[kind];
    assertUnique(kindCases, 'scenarioBlueprintId', casesPerKind, kind);
    assertUnique(kindCases, 'normalizedSemanticSignature', casesPerKind, kind);
    assertUnique(kindCases, 'structuralSignature', casesPerKind, kind);
    assertUnique(kindCases, 'source', casesPerKind, kind);
    if (kindCases.length !== casesPerKind) {
      throw new Error(
        `Expected ${casesPerKind} ${kind} scenarios, found ${kindCases.length}`,
      );
    }
    for (const entry of kindCases) {
      const normalizedCoverage =
        entry.normalizedSemanticStructure.coverage;
      if (
        normalizedCoverage?.category?.length === 0 ||
        normalizedCoverage?.capabilities?.length === 0 ||
        Object.hasOwn(entry, 'inputDimensions')
      ) {
        throw new Error(`${entry.id} has invalid coverage-driven metadata`);
      }
      if (
        [
          ...Object.keys(entry.normalizedSemanticStructure),
          ...collectObjectKeys(normalizedCoverage),
        ].some((key) =>
          /seed|itemCount|ordinal|scenarioBlueprintId|title/i
            .test(key)
        )
      ) {
        throw new Error(
          `${entry.id} normalized structure contains identity or label data`,
        );
      }
      if (
        entry.structuralSignature !==
          sha256(JSON.stringify(entry.normalizedSemanticStructure))
      ) {
        throw new Error(`${entry.id} has a stale structural signature`);
      }
      if (
        entry.normalizedSemanticStructure.normalizedSource !==
          normalizeScenarioSource(entry)
      ) {
        throw new Error(`${entry.id} has a stale normalized source`);
      }
      if (
        entry.expectedTexts.some((expectedText) =>
          !entry.source.includes(expectedText)
        )
      ) {
        throw new Error(`${entry.id} has an expected text absent from its source`);
      }
      if (
        Object.hasOwn(entry, 'structuralScenarioId') ||
        Object.hasOwn(entry, 'structuralProfileId') ||
        Object.hasOwn(entry, 'seedId') ||
        Object.hasOwn(entry, 'originScenarioId')
      ) {
        throw new Error(`${entry.id} reuses a seed or profile identity`);
      }
    }
  }
  for (const entry of cases) {
    if (!/^visual_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid independent visual case id: ${entry.id}`);
    }
    if (allIds.has(entry.id)) {
      throw new Error(`Duplicate independent visual case id: ${entry.id}`);
    }
    if (allSources.has(entry.source)) {
      throw new Error(`Duplicate independent visual source: ${entry.id}`);
    }
    if (
      entry.coverageRole !== 'independent-visual-scenario' ||
      entry.structuralSignature.length !== 64 ||
      entry.normalizedSemanticSignature.length !== 64 ||
      entry.sourceSha256 !== sha256(entry.source)
    ) {
      throw new Error(`Invalid independent scenario metadata: ${entry.id}`);
    }
    allIds.add(entry.id);
    allSources.add(entry.source);
  }
}

function assertUnique(entries, field, expectedCount, kind) {
  const groups = new Map();
  for (const entry of entries) {
    const value = entry[field];
    groups.set(value, [...(groups.get(value) ?? []), entry.id]);
  }
  const count = groups.size;
  if (count !== expectedCount) {
    const duplicates = [...groups.values()]
      .filter((ids) => ids.length > 1)
      .map((ids) => ids.join(', '))
      .join('; ');
    throw new Error(
      `${kind} has only ${count} unique ${field} values; duplicates: ` +
        duplicates,
    );
  }
}

function collectObjectKeys(value) {
  if (value === null || typeof value !== 'object') {
    return [];
  }
  if (Array.isArray(value)) {
    return value.flatMap(collectObjectKeys);
  }
  return Object.entries(value).flatMap(([key, child]) => [
    key,
    ...collectObjectKeys(child),
  ]);
}

function deepFreeze(value) {
  Object.freeze(value);
  for (const child of Object.values(value)) {
    if (child !== null && typeof child === 'object' && !Object.isFrozen(child)) {
      deepFreeze(child);
    }
  }
  return value;
}

function identifierToken(value) {
  return value.replace(/[^A-Za-z0-9_]/g, '');
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
