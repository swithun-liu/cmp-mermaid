import { createHash } from 'node:crypto';
import { cases as productionCases } from './production-corpus.mjs';

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
export const profileVariantCount = 4;
export const maximumProfileItemCount = 8;

export const structuralSeedsByKind = Object.freeze(
  Object.fromEntries(kinds.map((kind) => [kind, selectStructuralSeeds(kind)])),
);

export const cases = kinds.flatMap((kind) => buildKindCases(kind));

export const corpusSummary = Object.freeze({
  diagramFamilyCount: kinds.length,
  structuralCaseCount: cases.length,
  structuralCasesPerKind: casesPerKind,
  countsByKind: Object.freeze(kinds.map((kind) => {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    return Object.freeze({
      kind,
      structuralSeedCount: structuralSeedsByKind[kind].length,
      structuralCaseCount: kindCases.length,
      structuralProfileCount:
        new Set(kindCases.map((entry) => entry.structuralProfileId)).size,
      structuralSignatureCount:
        new Set(kindCases.map((entry) => entry.structuralSignature)).size,
    });
  })),
});

validateCorpus();

function selectStructuralSeeds(kind) {
  const kindCases = productionCases.filter((entry) => entry.kind === kind);
  const releaseCandidates = kindCases.filter((entry) => entry.id.startsWith('rc_'));
  const conformanceRepresentatives = [];
  const conformanceFeatureSets = new Set();
  for (const entry of kindCases.filter((candidate) =>
    candidate.id.startsWith('prod_')
  )) {
    const featureSet = [...entry.features].sort().join('|');
    if (conformanceFeatureSets.has(featureSet)) {
      continue;
    }
    conformanceFeatureSets.add(featureSet);
    conformanceRepresentatives.push(entry);
  }
  const seeds = [...releaseCandidates, ...conformanceRepresentatives];
  if (seeds.length < 9) {
    throw new Error(
      `Expected at least 9 structurally distinct ${kind} seeds, found ${seeds.length}`,
    );
  }
  if (Math.ceil(casesPerKind / seeds.length) > maximumProfileItemCount * profileVariantCount) {
    throw new Error(`Not enough structural profiles for ${kind}`);
  }
  return Object.freeze(seeds);
}

function buildKindCases(kind) {
  const seeds = structuralSeedsByKind[kind];
  return Array.from({ length: casesPerKind }, (_, index) => {
    const seed = seeds[index % seeds.length];
    const profileIndex = Math.floor(index / seeds.length);
    const itemCount = Math.floor(profileIndex / profileVariantCount) + 1;
    const variantIndex = profileIndex % profileVariantCount;
    const ordinal = index + 1;
    const profile = addStructuralVariation({
      kind,
      source: seed.source,
      ordinal,
      itemCount,
      variantIndex,
    });
    const structuralDimensions = Object.freeze({
      seedScenarioId: seed.id,
      seedFeatureSet: [...seed.features].sort(),
      itemCount,
      variant: profile.variant,
    });
    const structuralSignature = sha256(JSON.stringify({
      kind,
      ...structuralDimensions,
    }));
    const sourceSha256 = sha256(profile.source);
    return Object.freeze({
      id: `visual_${kind}_${String(ordinal).padStart(3, '0')}`,
      kind,
      title: `${seed.title} - ${profile.variant} ${itemCount}`,
      scenario:
        `Structural visual case ${ordinal} of ${casesPerKind}; combines ` +
        `${seed.id} with the ${profile.variant} profile containing ` +
        `${itemCount} generated structural item(s).`,
      layout: seed.layout ?? 'dagre',
      aspectRatio: seed.aspectRatio,
      features: [
        ...(seed.features ?? []),
        `structural-seed:${seed.id}`,
        `structural-profile:${profile.variant}`,
        `structural-item-count:${itemCount}`,
        `structural-signature:${structuralSignature}`,
        `structural-source-sha256:${sourceSha256}`,
      ],
      expectedTexts: structuralExpectedTexts(kind, ordinal, variantIndex),
      coverageRole: 'structural-visual-case',
      structuralScenarioId: seed.id,
      structuralProfileId:
        `${profile.variant}-${String(itemCount).padStart(2, '0')}`,
      structuralDimensions,
      structuralSignature,
      source: profile.source,
      sourceSha256,
    });
  });
}

function addStructuralVariation({
  kind,
  source,
  ordinal,
  itemCount,
  variantIndex,
}) {
  switch (kind) {
    case 'flowchart':
    case 'swimlanes':
      return appendFlowNodes(source, ordinal, itemCount, variantIndex);
    case 'architecture':
      return appendArchitectureServices(source, ordinal, itemCount, variantIndex);
    case 'c4':
      return appendC4Elements(source, ordinal, itemCount, variantIndex);
    case 'railroad':
      return appendRailroadRules(source, ordinal, itemCount, variantIndex);
    case 'treeview':
      return appendTreeEntries(source, ordinal, itemCount, variantIndex);
    case 'xychart':
      return appendXySeries(source, ordinal, itemCount, variantIndex);
    case 'quadrant':
      return appendQuadrantPoints(source, ordinal, itemCount, variantIndex);
    case 'timeline':
      return appendTimelineStructure(source, ordinal, itemCount, variantIndex);
    case 'kanban':
      return appendKanbanStructure(source, ordinal, itemCount, variantIndex);
    case 'sequence':
      return appendSequenceStructure(source, ordinal, itemCount, variantIndex);
    case 'class':
      return appendClassStructure(source, ordinal, itemCount, variantIndex);
    case 'state':
      return appendStateStructure(source, ordinal, itemCount, variantIndex);
    case 'er':
      return appendErStructure(source, ordinal, itemCount, variantIndex);
    case 'gantt':
      return appendGanttStructure(source, ordinal, itemCount, variantIndex);
    case 'pie':
      return appendPieStructure(source, ordinal, itemCount, variantIndex);
    case 'journey':
      return appendJourneyStructure(source, ordinal, itemCount, variantIndex);
    case 'requirement':
      return appendRequirementStructure(source, ordinal, itemCount, variantIndex);
    case 'gitgraph':
      return appendGitGraphStructure(source, ordinal, itemCount, variantIndex);
    case 'mindmap':
      return appendMindmapStructure(source, ordinal, itemCount, variantIndex);
    case 'packet':
      return appendPacketStructure(source, ordinal, itemCount, variantIndex);
    case 'radar':
      return appendRadarStructure(source, ordinal, itemCount, variantIndex);
    case 'sankey':
      return appendSankeyStructure(source, ordinal, itemCount, variantIndex);
    case 'treemap':
      return appendTreemapStructure(source, ordinal, itemCount, variantIndex);
    case 'venn':
      return appendVennStructure(source, ordinal, itemCount, variantIndex);
    case 'ishikawa':
      return appendIshikawaStructure(source, ordinal, itemCount, variantIndex);
    case 'cynefin':
      return appendCynefinStructure(source, ordinal, itemCount, variantIndex);
    case 'block':
      return appendBlockStructure(source, ordinal, itemCount, variantIndex);
    case 'eventmodeling':
      return appendEventModelingStructure(source, ordinal, itemCount, variantIndex);
    case 'agentflow':
      return appendAgentflowStructure(source, ordinal, itemCount, variantIndex);
    case 'usecase':
      return appendUseCaseStructure(source, ordinal, itemCount, variantIndex);
    case 'wardley':
      return appendWardleyStructure(source, ordinal, itemCount, variantIndex);
    case 'zenuml':
      return appendZenUmlStructure(source, ordinal, itemCount, variantIndex);
    default:
      throw new Error(`Unsupported structural visual kind: ${kind}`);
  }
}

function appendFlowNodes(source, ordinal, itemCount, variantIndex) {
  const anchor = findFirstIdentifier(source, ['[', '(', '{', '@', '-']);
  const ids = generatedIds('StructureNode', ordinal, itemCount);
  const shapes = [
    (id, label) => `${id}["${label}"]`,
    (id, label) => `${id}("${label}")`,
    (id, label) => `${id}{${label}}`,
    (id, label) => `${id}[("${label}")]`,
  ];
  const nodes = ids.map((id, index) =>
    `  ${shapes[variantIndex](id, generatedLabel(ordinal, index))}`
  );
  const edges = [];
  if (variantIndex === 3) {
    ids.forEach((id) => edges.push(`  ${anchor} --> ${id}`));
  } else {
    const operator = ['-->', '-.->', '==>'][variantIndex];
    let previous = anchor;
    ids.forEach((id) => {
      edges.push(`  ${previous} ${operator} ${id}`);
      previous = id;
    });
  }
  return variation(
    ['solid-chain', 'dotted-chain', 'thick-chain', 'star'][variantIndex],
    source,
    [...nodes, ...edges],
  );
}

function appendArchitectureServices(source, ordinal, itemCount, variantIndex) {
  const serviceDeclarations = [...source.matchAll(
    /^\s*service\s+([A-Za-z_][A-Za-z0-9_-]*)\b([^\r\n]*)$/gm,
  )];
  const anchorDeclaration = serviceDeclarations.at(-1);
  if (anchorDeclaration === undefined) {
    throw new Error('Missing Architecture service while adding structural variation');
  }
  const anchor = anchorDeclaration[1];
  const parent = anchorDeclaration[2].match(
    /\s+in\s+([A-Za-z_][A-Za-z0-9_-]*)\s*$/,
  )?.[1];
  const parentSuffix = parent === undefined ? '' : ` in ${parent}`;
  const ids = generatedIds('structure_service', ordinal, itemCount);
  const icons = ['server', 'internet', 'disk', 'database'];
  const lines = ids.map((id, index) =>
    `  service ${id}(${icons[variantIndex]})` +
      `[${generatedLabel(ordinal, index)}]${parentSuffix}`
  );
  let previous = anchor;
  ids.forEach((id) => {
    const ports = [
      `${previous}:R --> L:${id}`,
      `${previous}:R <-- L:${id}`,
      `${previous}:R <--> L:${id}`,
      `${previous}:R --> L:${id}`,
    ][variantIndex];
    lines.push(`  ${ports}`);
    previous = id;
  });
  return variation(
    [
      'server-forward-chain',
      'internet-reverse-chain',
      'disk-bidirectional-chain',
      'database-forward-chain',
    ][variantIndex],
    source,
    lines,
  );
}

function appendC4Elements(source, ordinal, itemCount, variantIndex) {
  const constructors = ['Person', 'System', 'SystemDb', 'SystemQueue'];
  const ids = generatedIds('StructureElement', ordinal, itemCount);
  const lines = ids.map((id, index) =>
    `  ${constructors[variantIndex]}(${id}, "${generatedLabel(ordinal, index)}")`
  );
  for (let index = 1; index < ids.length; index += 1) {
    lines.push(`  Rel(${ids[index - 1]}, ${ids[index]}, "continues")`);
  }
  return variation(
    ['people-chain', 'system-chain', 'database-chain', 'queue-chain'][variantIndex],
    source,
    lines,
  );
}

function appendRailroadRules(source, ordinal, itemCount, variantIndex) {
  const declaration = source
    .split(/\r?\n/)
    .map((line) => line.trim())
    .find((line) => /^railroad(?:-(?:ebnf|abnf|peg))?-beta$/.test(line));
  if (declaration === undefined) {
    throw new Error('Missing Railroad declaration');
  }
  const lines = Array.from({ length: itemCount }, (_, index) => {
    const id = `structure-rule-${ordinal}-${index + 1}`;
    const token = `token-${ordinal}-${index + 1}`;
    if (declaration === 'railroad-beta') {
      const expressions = [
        `terminal("${token}")`,
        `sequence(terminal("${token}"), optional(nonterminal("tail")))`,
        `choice(terminal("${token}"), special("fallback"))`,
        `oneOrMore(nonterminal("${token}"))`,
      ];
      return `  ${id} = ${expressions[variantIndex]} ;`;
    }
    if (declaration === 'railroad-ebnf-beta') {
      const expressions = [
        `"${token}"`,
        `"${token}", [ identifier ]`,
        `"${token}" | identifier`,
        `{ "${token}" }`,
      ];
      return `  ${id} = ${expressions[variantIndex]} ;`;
    }
    if (declaration === 'railroad-abnf-beta') {
      const expressions = [
        `"${token}"`,
        `1*2ALPHA`,
        `"${token}" / DIGIT`,
        `[ "${token}" ]`,
      ];
      return `  ${id} = ${expressions[variantIndex]} ;`;
    }
    const expressions = [
      `"${token}"`,
      `"${token}"+`,
      `"${token}" / .`,
      `!"deny" "${token}"`,
    ];
    return `  ${id} <- ${expressions[variantIndex]} ;`;
  });
  return variation(
    ['terminal-rules', 'sequence-rules', 'choice-rules', 'repetition-rules'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendTreeEntries(source, ordinal, itemCount, variantIndex) {
  if (/[├└┣┗]/u.test(source)) {
    return appendBoxDrawingTreeEntries(
      source,
      ordinal,
      itemCount,
      variantIndex,
    );
  }
  const root = `    structure-${String(ordinal).padStart(3, '0')}/`;
  const lines = [root];
  for (let index = 0; index < itemCount; index += 1) {
    const label = `case-${String(index + 1).padStart(2, '0')}`;
    if (variantIndex === 0) {
      lines.push(`        ${label}.md`);
    } else if (variantIndex === 1) {
      lines.push(`        ${label}/`, `            index.kt`);
    } else if (variantIndex === 2) {
      lines.push(`        ${label}.kt :::highlight ## generated branch`);
    } else {
      lines.push(`        "${label} folder/"`, `            data.json icon(file)`);
    }
  }
  return variation(
    ['flat-files', 'nested-directories', 'highlighted-files', 'quoted-icon-tree'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendBoxDrawingTreeEntries(
  source,
  ordinal,
  itemCount,
  variantIndex,
) {
  const heavy = /[┣┗]/u.test(source);
  const branch = heavy ? '┣━━ ' : '├── ';
  const last = heavy ? '┗━━ ' : '└── ';
  const vertical = heavy ? '┃   ' : '│   ';
  const indent = '    ';
  const lines = [
    `${last}structure-${String(ordinal).padStart(3, '0')}/`,
  ];
  for (let index = 0; index < itemCount; index += 1) {
    const label = `case-${String(index + 1).padStart(2, '0')}`;
    const connector = index === itemCount - 1 ? last : branch;
    const childPrefix = `${indent}${connector}`;
    if (variantIndex === 0) {
      lines.push(`${childPrefix}${label}.md`);
    } else if (variantIndex === 1) {
      lines.push(
        `${childPrefix}${label}/`,
        `${indent}${index === itemCount - 1 ? indent : vertical}${last}index.kt`,
      );
    } else if (variantIndex === 2) {
      lines.push(`${childPrefix}${label}.kt :::highlight ## generated branch`);
    } else {
      lines.push(
        `${childPrefix}"${label} folder/"`,
        `${indent}${index === itemCount - 1 ? indent : vertical}${last}` +
          'data.json icon(file)',
      );
    }
  }
  return variation(
    ['flat-files', 'nested-directories', 'highlighted-files', 'quoted-icon-tree'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendXySeries(source, ordinal, itemCount, variantIndex) {
  const pointCount = xyPointCount(source);
  const labelledBarOverflow =
    variantIndex === 1 &&
    itemCount >= 5 &&
    source.includes('showDataLabelOutsideBar: true');
  const lines = [];
  for (let index = 0; index < itemCount; index += 1) {
    const plotType =
      !labelledBarOverflow &&
      (variantIndex === 1 || (variantIndex === 2 && index % 2 === 0))
      ? 'bar'
      : 'line';
    const values = Array.from({ length: pointCount }, (_, valueIndex) =>
      xyValue(variantIndex, index, valueIndex, pointCount)
    );
    lines.push(
      `  ${plotType} "Structure ${ordinal}-${index + 1}" [${values.join(', ')}]`,
    );
  }
  return variation(
    labelledBarOverflow
      ? 'labelled-line-series'
      : ['rising-lines', 'grouped-bars', 'mixed-series', 'oscillating-lines'][
        variantIndex
      ],
    source,
    lines,
  );
}

function appendQuadrantPoints(source, ordinal, itemCount, variantIndex) {
  const lines = Array.from({ length: itemCount }, (_, index) => {
    const x = (((ordinal + index * 2) % 9) + 1) / 10;
    const y = (((ordinal * 3 + index) % 9) + 1) / 10;
    const style = [
      '',
      ` radius: ${7 + (index % 4)}`,
      ` color: #2563eb`,
      ` radius: ${8 + (index % 3)}, color: #16a34a, stroke-width: 2px`,
    ][variantIndex];
    return `  "Structure ${ordinal}-${index + 1}": ` +
      `[${x.toFixed(1)}, ${y.toFixed(1)}]${style}`;
  });
  return variation(
    ['plain-points', 'radius-points', 'colored-points', 'fully-styled-points'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendTimelineStructure(source, ordinal, itemCount, variantIndex) {
  const lines = [];
  if (variantIndex !== 2) {
    lines.push(`  section Structure ${ordinal}`);
  }
  for (let index = 0; index < itemCount; index += 1) {
    const event = `Milestone ${ordinal}-${index + 1}`;
    if (variantIndex === 0) {
      lines.push(`    ${event} : Planned`);
    } else if (variantIndex === 1) {
      lines.push(`    ${event} : Planned : Verified`);
    } else if (variantIndex === 2) {
      lines.push(`  ${event} : Sectionless event`);
    } else {
      lines.push(`    ${event} : Draft : Review : Release`);
    }
  }
  return variation(
    ['section-events', 'multi-events', 'sectionless-events', 'three-stage-events'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendKanbanStructure(source, ordinal, itemCount, variantIndex) {
  const stageId = `StructureStage${ordinal}`;
  const lines = [`  ${stageId}[Structure stage ${ordinal}]`];
  for (let index = 0; index < itemCount; index += 1) {
    const id = `StructureTask${ordinal}_${index + 1}`;
    const label = generatedLabel(ordinal, index);
    const task = [
      `${id}[${label}]`,
      `${id}(${label})`,
      `${id}((` + `${label}` + '))',
      `${id}[${label}]@{ priority: High, assigned: Owner }`,
    ][variantIndex];
    lines.push(`    ${task}`);
  }
  return variation(
    ['square-tasks', 'rounded-tasks', 'circle-tasks', 'metadata-tasks'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendSequenceStructure(source, ordinal, itemCount, variantIndex) {
  const participants = sequenceParticipantIds(source);
  const messages = Array.from({ length: itemCount }, (_, index) => {
    const from = participants[index % participants.length];
    const to = participants[(index + 1) % participants.length];
    return `    ${from}->>${to}: ${generatedLabel(ordinal, index)}`;
  });
  const lines = [
    messages.map((line) => line.trimStart()),
    [`  loop structure ${ordinal}`, ...messages, '  end'],
    [`  opt structure ${ordinal}`, ...messages, '  end'],
    [
      `  par structure ${ordinal}`,
      ...messages,
      '  and ack',
      `    ${participants.at(-1)}-->>${participants[0]}: complete`,
      '  end',
    ],
  ][variantIndex];
  return {
    variant: ['message-chain', 'loop-region', 'optional-region', 'parallel-region'][
      variantIndex
    ],
    source: `${source.trimEnd()}\n${lines.join('\n')}\n`,
  };
}

function appendClassStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('StructureClass', ordinal, itemCount);
  const lines = [];
  ids.forEach((id, index) => {
    lines.push(`  class ${id} {`, `    +String value${index + 1}`, '  }');
  });
  const operators = ['-->', '*--', '..>', '<|--'];
  for (let index = 1; index < ids.length; index += 1) {
    lines.push(
      `  ${ids[index - 1]} ${operators[variantIndex]} ${ids[index]} : step`,
    );
  }
  return variation(
    ['associations', 'compositions', 'dependencies', 'inheritance'][variantIndex],
    source,
    lines,
  );
}

function appendStateStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('StructureState', ordinal, itemCount);
  const lines = ids.map((id, index) =>
    `  state "${generatedLabel(ordinal, index)}" as ${id}`
  );
  for (let index = 1; index < ids.length; index += 1) {
    const label = ['advance', 'approved', 'retry', 'complete'][variantIndex];
    if (variantIndex === 2 && index % 2 === 0) {
      lines.push(`  ${ids[index]} --> ${ids[index - 1]} : ${label}`);
    } else {
      lines.push(`  ${ids[index - 1]} --> ${ids[index]} : ${label}`);
    }
  }
  if (variantIndex === 3 && ids.length > 0) {
    lines.push(`  ${ids.at(-1)} --> [*] : done`);
  }
  return variation(
    ['linear-states', 'labelled-states', 'retry-states', 'terminal-states'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendErStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('STRUCTURE_ENTITY', ordinal, itemCount);
  const lines = [];
  ids.forEach((id, index) => {
    lines.push(
      `  ${id} {`,
      `    bigint id PK`,
      variantIndex >= 2 ? `    string value_${index + 1} UK` : `    string value`,
      '  }',
    );
  });
  const cardinalities = ['||--o{', '||--|{', '}o--o{', '|o..o|'];
  for (let index = 1; index < ids.length; index += 1) {
    lines.push(
      `  ${ids[index - 1]} ${cardinalities[variantIndex]} ` +
        `${ids[index]} : relates`,
    );
  }
  return variation(
    ['optional-many', 'required-many', 'many-to-many', 'optional-one'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendGanttStructure(source, ordinal, itemCount, variantIndex) {
  const lines = [`  section Structure ${ordinal}`];
  let previousId = null;
  for (let index = 0; index < itemCount; index += 1) {
    const id = `structure_${ordinal}_${index + 1}`;
    const state = ['', 'done, ', 'active, ', index === itemCount - 1 ? 'milestone, ' : 'crit, '][
      variantIndex
    ];
    const schedule = previousId === null
      ? `2030-01-${String((ordinal % 20) + 1).padStart(2, '0')}, 1d`
      : `after ${previousId}, ${variantIndex === 3 && index === itemCount - 1 ? '0d' : '1d'}`;
    lines.push(
      `  Structure task ${ordinal}-${index + 1} :${state}${id}, ${schedule}`,
    );
    previousId = id;
  }
  return variation(
    ['scheduled-tasks', 'completed-tasks', 'active-tasks', 'critical-milestone'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendPieStructure(source, ordinal, itemCount, variantIndex) {
  const lines = Array.from({ length: itemCount }, (_, index) => {
    const value = [
      index + 1,
      (index + 1) * (index + 1),
      itemCount - index,
      index === 0 ? itemCount * 5 : 1,
    ][variantIndex];
    return `  "Structure ${ordinal}-${index + 1}" : ${value}`;
  });
  return variation(
    ['ascending-slices', 'quadratic-slices', 'descending-slices', 'skewed-slices'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendJourneyStructure(source, ordinal, itemCount, variantIndex) {
  const lines = [`  section Structure ${ordinal}`];
  for (let index = 0; index < itemCount; index += 1) {
    const score = variantIndex === 3
      ? `${((index % 5) + 1) - 0.5}`
      : `${(index % 5) + 1}`;
    const actors = [
      '',
      ': Operator',
      ': Operator, Reviewer',
      index % 2 === 0 ? '' : ': Reliability Engineer',
    ][variantIndex];
    lines.push(
      `    Structure task ${ordinal}-${index + 1}: ${score}${actors}`,
    );
  }
  return variation(
    ['actorless-tasks', 'single-actor-tasks', 'multi-actor-tasks', 'mixed-decimal-tasks'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendRequirementStructure(source, ordinal, itemCount, variantIndex) {
  const anchor = lastMatch(
    source,
    /^\s*(?:requirement|functionalRequirement|performanceRequirement|interfaceRequirement|physicalRequirement|designConstraint)\s+([A-Za-z_][A-Za-z0-9_-]*)\s*\{/m,
  );
  const types = [
    'requirement',
    'functionalRequirement',
    'performanceRequirement',
    'interfaceRequirement',
  ];
  const ids = generatedIds('structure_requirement', ordinal, itemCount);
  const lines = [];
  ids.forEach((id, index) => {
    lines.push(
      `  ${types[variantIndex]} ${id} {`,
      `    id: "STRUCT-${ordinal}-${index + 1}"`,
      `    text: "${generatedLabel(ordinal, index)}"`,
      `    risk: ${['low', 'medium', 'high', 'medium'][variantIndex]}`,
      `    verifyMethod: ${['inspection', 'test', 'analysis', 'demonstration'][variantIndex]}`,
      '  }',
    );
  });
  lines.push(`  ${anchor} - traces -> ${ids[0]}`);
  for (let index = 1; index < ids.length; index += 1) {
    lines.push(
      `  ${ids[index - 1]} - ${['contains', 'derives', 'refines', 'traces'][variantIndex]} -> ${ids[index]}`,
    );
  }
  return variation(
    ['base-requirements', 'functional-requirements', 'performance-requirements', 'interface-requirements'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendGitGraphStructure(source, ordinal, itemCount, variantIndex) {
  const lines = Array.from({ length: itemCount }, (_, index) => {
    const id = `structure-${ordinal}-${index + 1}`;
    const suffix = [
      '',
      ` type: HIGHLIGHT`,
      ` type: REVERSE`,
      ` tag: "profile-${index + 1}"`,
    ][variantIndex];
    return `  commit id: "${id}"${suffix}`;
  });
  return variation(
    ['plain-commits', 'highlighted-commits', 'reverse-commits', 'tagged-commits'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendMindmapStructure(source, ordinal, itemCount, variantIndex) {
  const branchId = `structure_${ordinal}`;
  const lines = [`    ${branchId}[Structure ${ordinal}]`];
  for (let index = 0; index < itemCount; index += 1) {
    const id = `${branchId}_${index + 1}`;
    const label = generatedLabel(ordinal, index);
    if (variantIndex === 0) {
      lines.push(`      ${id}[${label}]`);
    } else if (variantIndex === 1) {
      lines.push(`${'      '.repeat(index + 1)}${id}(${label})`);
    } else if (variantIndex === 2) {
      lines.push(`      ${id}((${label}))`);
    } else {
      lines.push(`      ${id}{{${label}}}`, `        ${id}_child[Evidence]`);
    }
  }
  return variation(
    ['wide-square-branch', 'deep-rounded-branch', 'circle-branch', 'hexagon-pairs'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendPacketStructure(source, ordinal, itemCount, variantIndex) {
  const widths = [1, 4, 8, 16];
  const lines = Array.from({ length: itemCount }, (_, index) =>
    `  +${widths[(variantIndex + index) % widths.length]}: ` +
      `"Structure ${ordinal}-${index + 1}"`
  );
  return variation(
    ['single-bit-fields', 'nibble-fields', 'byte-fields', 'word-fields'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendRadarStructure(source, ordinal, itemCount, variantIndex) {
  const axisIds = radarAxisIds(source);
  const lines = [];
  for (let index = 0; index < itemCount; index += 1) {
    const values = axisIds.map((_, axisIndex) =>
      20 + ((ordinal + index * 11 + axisIndex * (variantIndex + 3)) % 75)
    );
    if (variantIndex === 1) {
      lines.push(
        `  curve structure_${ordinal}_${index + 1} { ` +
          axisIds.map((id, axisIndex) => `${id}: ${values[axisIndex]}`).join(', ') +
          ' }',
      );
    } else {
      lines.push(
        `  curve structure_${ordinal}_${index + 1}["Structure ${ordinal}-${index + 1}"] ` +
          `{ ${values.join(', ')} }`,
      );
    }
  }
  lines.push([
    '  graticule circle',
    '  graticule polygon',
    '  showLegend false',
    `  ticks ${Math.min(10, itemCount + 3)}`,
  ][variantIndex]);
  return variation(
    ['circle-positional-curves', 'polygon-detailed-curves', 'hidden-legend-curves', 'custom-tick-curves'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendSankeyStructure(source, ordinal, itemCount, variantIndex) {
  const nodes = generatedIds('Structure', ordinal, itemCount + 1);
  const lines = [];
  for (let index = 0; index < itemCount; index += 1) {
    const value = index + 1;
    if (variantIndex === 0) {
      lines.push(`${nodes[index]},${nodes[index + 1]},${value}`);
    } else if (variantIndex === 1) {
      lines.push(`${nodes[0]},${nodes[index + 1]},${value}`);
    } else if (variantIndex === 2) {
      lines.push(`${nodes[index]},${nodes.at(-1)},${value}`);
    } else {
      const middle = `StructureMiddle${ordinal}_${index + 1}`;
      lines.push(`${nodes[index]},${middle},${value}`);
      lines.push(`${middle},${nodes[index + 1]},${value}`);
    }
  }
  return variation(
    ['flow-chain', 'source-fanout', 'target-merge', 'two-stage-flow'][variantIndex],
    source,
    lines,
  );
}

function appendTreemapStructure(source, ordinal, itemCount, variantIndex) {
  const existingTotal = treemapLeafTotal(source);
  const generatedLeafCount = itemCount * (variantIndex === 3 ? 2 : 1);
  const generatedLeafValue = existingTotal * 0.4 / generatedLeafCount;
  const lines = [`"Structure ${ordinal}"`];
  for (let index = 0; index < itemCount; index += 1) {
    const value = formatTreemapValue(
      generatedLeafValue * (0.9 + index * 0.05),
    );
    if (variantIndex === 0) {
      lines.push(`    "Leaf ${index + 1}": ${value}`);
    } else if (variantIndex === 1) {
      lines.push(`    "G${index + 1}"`, `        "Leaf": ${value}`);
    } else if (variantIndex === 2) {
      lines.push(`    "Leaf ${index + 1}", ${value}`);
    } else {
      lines.push(
        `    "G${index + 1}"`,
        `        "A": ${formatTreemapValue(value * 0.9)}`,
        `        "B": ${formatTreemapValue(value * 1.1)}`,
      );
    }
  }
  const variant = [
    'flat-colon-leaves',
    'nested-colon-leaves',
    'flat-comma-leaves',
    'paired-nested-leaves',
  ][variantIndex];
  const sizedSource = insertBeforeMatchingDeclaration(
    source,
    /^\s*treemap(?:-beta|:)?\s*$/,
    [
      '%%{init: {"treemap": {"nodeWidth": 160, "nodeHeight": 90}}}%%',
    ],
  );
  const sourceLines = sizedSource.trim().split(/\r?\n/);
  const classDefinitionIndex = sourceLines.findIndex((line) =>
    line.trimStart().startsWith('classDef ')
  );
  if (classDefinitionIndex < 0) {
    return variation(variant, source, lines);
  }
  sourceLines.splice(classDefinitionIndex, 0, ...lines);
  return {
    variant,
    source: `${sourceLines.join('\n')}\n`,
  };
}

function appendVennStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('StructureSet', ordinal, itemCount);
  const lines = ids.map((id, index) =>
    `  set ${id}["Structure ${ordinal}-${index + 1}"]:${12 + index}`
  );
  if (variantIndex === 1) {
    ids.forEach((id, index) =>
      lines.push(`    text StructureText${ordinal}_${index + 1}["Evidence"]`)
    );
  } else if (variantIndex === 2 && ids.length > 1) {
    for (let index = 1; index < ids.length; index += 1) {
      lines.push(
        `  union ${ids[index - 1]},${ids[index]}["Overlap ${index}"]:${index + 1}`,
      );
    }
  } else if (variantIndex === 3) {
    ids.forEach((id, index) =>
      lines.push(
        `  style ${id} fill:${index % 2 === 0 ? '#dbeafe' : '#dcfce7'},` +
          'stroke:#334155,fill-opacity:0.24',
      )
    );
  }
  return variation(
    ['independent-sets', 'sets-with-text', 'pairwise-unions', 'styled-sets'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendIshikawaStructure(source, ordinal, itemCount, variantIndex) {
  const lines = [];
  for (let index = 0; index < itemCount; index += 1) {
    const label = `Structure cause ${ordinal}-${index + 1}`;
    if (variantIndex === 0) {
      lines.push(`  ${label}`);
    } else if (variantIndex === 1) {
      lines.push(`  ${label}`, `    Evidence ${index + 1}`);
    } else if (variantIndex === 2) {
      lines.push(`  ${label}`, `    Evidence ${index + 1}`, `      Root ${index + 1}`);
    } else {
      lines.push(
        `  ${label}`,
        `    Primary ${index + 1}`,
        `    Secondary ${index + 1}`,
      );
    }
  }
  return variation(
    ['top-level-causes', 'nested-causes', 'deep-causes', 'branched-causes'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendCynefinStructure(source, ordinal, itemCount, variantIndex) {
  const domain = ['complex', 'complicated', 'clear', 'chaotic'][variantIndex];
  const lines = [`  ${domain}`];
  for (let index = 0; index < itemCount; index += 1) {
    lines.push(`    "Structure ${ordinal}-${index + 1}"`);
  }
  return variation(
    ['complex-items', 'complicated-items', 'clear-items', 'chaotic-items'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendBlockStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('structure_block', ordinal, itemCount);
  const shapes = [
    (id, label) => `${id}["${label}"]`,
    (id, label) => `${id}("${label}")`,
    (id, label) => `${id}[("${label}")]`,
    (id, label) => `${id}{{"${label}"}}`,
  ];
  const lines = ids.map((id, index) =>
    `  ${shapes[variantIndex](id, generatedLabel(ordinal, index))}`
  );
  for (let index = 1; index < ids.length; index += 1) {
    lines.push(`  ${ids[index - 1]} --> ${ids[index]}`);
  }
  return variation(
    ['square-blocks', 'rounded-blocks', 'database-blocks', 'hexagon-blocks'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendEventModelingStructure(source, ordinal, itemCount, variantIndex) {
  const types = ['ui', 'cmd', 'evt', 'rmo'];
  const lines = [];
  for (let index = 0; index < itemCount; index += 1) {
    const frame = 700 + index;
    const relation = index === 0 ? '' : ` ->> ${frame - 1}`;
    lines.push(
      `  tf ${frame} ${types[variantIndex]} Structure${ordinal}_${index + 1}` +
        `${relation}`,
    );
  }
  return variation(
    ['ui-flow', 'command-flow', 'event-flow', 'read-model-flow'][variantIndex],
    source,
    lines,
  );
}

function appendAgentflowStructure(source, ordinal, itemCount, variantIndex) {
  const anchor = findFirstIdentifier(source, ['[', '@', '-']);
  const ids = generatedIds('structure_agent', ordinal, itemCount);
  const shapes = ['task', 'tool', 'decision', 'action'];
  const lines = ids.map((id, index) =>
    `  ${id}["${generatedLabel(ordinal, index)}"]@{ shape: ${shapes[variantIndex]} }`
  );
  if (variantIndex === 2) {
    ids.forEach((id) => lines.push(`  ${anchor} --> ${id}`));
  } else {
    ids.forEach((id, index) => {
      const previous = index === 0 ? anchor : ids[index - 1];
      const operator = variantIndex === 1 ? '-.-' : variantIndex === 3 ? '--x' : '-->';
      lines.push(`  ${previous} ${operator} ${id}`);
    });
  }
  return variation(
    ['task-chain', 'tool-reference-chain', 'decision-fanout', 'failure-action-chain'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendUseCaseStructure(source, ordinal, itemCount, variantIndex) {
  const actor = `StructureActor${ordinal}`;
  const ids = generatedIds('StructureUseCase', ordinal, itemCount);
  const lines = [`actor ${actor}("Structure actor ${ordinal}")`];
  ids.forEach((id, index) => {
    lines.push(`${id}("${generatedLabel(ordinal, index)}")`);
  });
  if (variantIndex === 0) {
    ids.forEach((id) => lines.push(`${actor} --> ${id}`));
  } else if (variantIndex === 1) {
    for (let index = 1; index < ids.length; index += 1) {
      lines.push(`${ids[index - 1]} ..> : include ${ids[index]}`);
    }
  } else if (variantIndex === 2) {
    for (let index = 1; index < ids.length; index += 1) {
      lines.push(`${ids[index]} ..> : extend ${ids[index - 1]}`);
    }
  } else {
    ids.forEach((id, index) => {
      lines.push(`${actor} --> ${id}`);
      lines.push(`note for ${id} "Evidence ${index + 1}"`);
    });
  }
  return variation(
    ['actor-associations', 'include-chain', 'extend-chain', 'noted-associations'][
      variantIndex
    ],
    source,
    lines,
  );
}

function appendWardleyStructure(source, ordinal, itemCount, variantIndex) {
  const names = Array.from(
    { length: itemCount },
    (_, index) => `"Structure ${ordinal}-${index + 1}"`,
  );
  const lines = names.map((name, index) => {
    const visibility = Math.max(0.1, 0.84 - index * 0.06);
    const evolution = Math.min(0.95, 0.2 + index * 0.08);
    const sourcing = ['', ' (build)', ' (buy)', ' (inertia)'][variantIndex];
    return `component ${name} [${visibility.toFixed(2)}, ${evolution.toFixed(2)}]${sourcing}`;
  });
  for (let index = 1; index < names.length; index += 1) {
    lines.push(`${names[index - 1]} -> ${names[index]}`);
  }
  return variation(
    ['component-chain', 'build-chain', 'buy-chain', 'inertia-chain'][variantIndex],
    source,
    lines,
  );
}

function appendZenUmlStructure(source, ordinal, itemCount, variantIndex) {
  const ids = generatedIds('StructureParticipant', ordinal, itemCount);
  const withParticipants = insertAfterDeclaration(
    source,
    'zenuml',
    ids.map((id, index) => `${id} as "${generatedLabel(ordinal, index)}"`),
  );
  const calls = ids.map((id, index) => {
    const previous = index === 0 ? ids.at(-1) : ids[index - 1];
    return `  ${previous}->${id}: step ${index + 1}`;
  });
  const lines = [
    calls.map((line) => line.trimStart()),
    ['loop(structure) {', ...calls, '}'],
    ['opt(structure) {', ...calls, '}'],
    ['par {', ...calls, '}'],
  ][variantIndex];
  return {
    variant: ['participant-messages', 'loop-messages', 'optional-messages', 'parallel-messages'][
      variantIndex
    ],
    source: `${withParticipants.trimEnd()}\n${lines.join('\n')}\n`,
  };
}

function variation(variant, source, lines) {
  return {
    variant,
    source: `${source.trim()}\n${lines.join('\n')}\n`,
  };
}

function generatedIds(prefix, ordinal, count) {
  return Array.from(
    { length: count },
    (_, index) => `${prefix}${ordinal}_${index + 1}`,
  );
}

function generatedLabel(ordinal, index) {
  return `Structure ${ordinal}-${index + 1}`;
}

function structuralExpectedTexts(kind, ordinal, variantIndex) {
  switch (kind) {
    case 'railroad':
      return [`structure-rule-${ordinal}-1`];
    case 'treeview':
      return [[
        'case-01.md',
        'case-01',
        'case-01.kt',
        'case-01 folder',
      ][variantIndex]];
    case 'xychart':
    case 'radar':
      return [];
    case 'class':
      return [`StructureClass${ordinal}_1`];
    case 'er':
      return [`STRUCTURE_ENTITY${ordinal}_1`];
    case 'timeline':
      return [`Milestone ${ordinal}-1`];
    case 'gantt':
    case 'journey':
      return [`Structure task ${ordinal}-1`];
    case 'gitgraph':
      return [`structure-${ordinal}-1`];
    case 'sankey':
      return [`Structure${ordinal}_1`];
    case 'treemap':
      return [];
    case 'eventmodeling':
      return [`Structure${ordinal}_1`];
    case 'ishikawa':
      return [`Structure cause ${ordinal}-1`];
    default:
      return [generatedLabel(ordinal, 0)];
  }
}

function findFirstIdentifier(source, expectedRestPrefixes) {
  for (const line of source.split(/\r?\n/)) {
    const match = line.match(/^\s*([A-Za-z_][A-Za-z0-9_-]*)\s*(.*)$/);
    if (match === null) {
      continue;
    }
    const [, identifier, rest] = match;
    if (expectedRestPrefixes.some((prefix) => rest.startsWith(prefix))) {
      return identifier;
    }
  }
  throw new Error('Missing diagram identifier while adding structural variation');
}

function firstMatch(source, pattern) {
  const value = source.match(pattern)?.[1];
  if (value === undefined) {
    throw new Error(`Missing source match for ${pattern}`);
  }
  return value;
}

function lastMatch(source, pattern) {
  const flags = pattern.flags.includes('g') ? pattern.flags : `${pattern.flags}g`;
  const matches = [...source.matchAll(new RegExp(pattern.source, flags))];
  const value = matches.at(-1)?.[1];
  if (value === undefined) {
    throw new Error(`Missing source match for ${pattern}`);
  }
  return value;
}

function insertAfterDeclaration(source, declaration, lines) {
  const sourceLines = source.trim().split(/\r?\n/);
  const declarationIndex = sourceLines.findIndex(
    (line) => line.trim() === declaration,
  );
  if (declarationIndex < 0) {
    throw new Error(`Missing ${declaration} declaration`);
  }
  sourceLines.splice(declarationIndex + 1, 0, ...lines);
  return sourceLines.join('\n');
}

function insertBeforeMatchingDeclaration(source, declarationPattern, lines) {
  const sourceLines = source.trim().split(/\r?\n/);
  const declarationIndex = sourceLines.findIndex(
    (line) => declarationPattern.test(line),
  );
  if (declarationIndex < 0) {
    throw new Error(`Missing declaration matching ${declarationPattern}`);
  }
  sourceLines.splice(declarationIndex, 0, ...lines);
  return sourceLines.join('\n');
}

function sequenceParticipantIds(source) {
  const ids = [];
  for (const match of source.matchAll(
    /^\s*(?:participant|actor)\s+([A-Za-z_][A-Za-z0-9_-]*)/gm,
  )) {
    if (!ids.includes(match[1])) {
      ids.push(match[1]);
    }
  }
  if (ids.length === 0) {
    throw new Error('Missing Sequence participant while adding structural variation');
  }
  return ids;
}

function treemapLeafTotal(source) {
  const values = [...source.matchAll(
    /^\s*"[^"]+"\s*(?::|,)\s*(-?(?:\d+(?:\.\d+)?|\.\d+))/gm,
  )]
    .map((match) => Number(match[1]))
    .filter((value) => Number.isFinite(value) && value > 0);
  return values.reduce((total, value) => total + value, 0) || 100;
}

function formatTreemapValue(value) {
  return Number(value.toFixed(2));
}

function xyPointCount(source) {
  const values = source.match(
    /^\s*(?:line|bar)(?:\s+"[^"]*")?\s+\[([^\]]+)]/m,
  )?.[1];
  if (values === undefined) {
    throw new Error('Missing XY plot values');
  }
  return values.split(',').length;
}

function xyValue(variantIndex, seriesIndex, pointIndex, pointCount) {
  if (variantIndex === 0) {
    return 10 + seriesIndex * 6 + pointIndex * 4;
  }
  if (variantIndex === 1) {
    return 20 + seriesIndex * 8 + (pointCount - pointIndex) * 3;
  }
  if (variantIndex === 2) {
    return 15 + seriesIndex * 7 + ((pointIndex * 5) % 23);
  }
  return 35 + seriesIndex * 5 + (pointIndex % 2 === 0 ? 18 : -12);
}

function radarAxisIds(source) {
  const ids = [];
  for (const line of source.split(/\r?\n/)) {
    if (!line.trimStart().startsWith('axis ')) {
      continue;
    }
    for (const match of line.matchAll(/([A-Za-z_][A-Za-z0-9_-]*)\s*\[/g)) {
      ids.push(match[1]);
    }
  }
  if (ids.length < 3) {
    throw new Error('Missing Radar axes');
  }
  return ids;
}

function validateCorpus() {
  const expectedCount = kinds.length * casesPerKind;
  if (cases.length !== expectedCount) {
    throw new Error(`Expected ${expectedCount} structural cases, found ${cases.length}`);
  }
  const allIds = new Set();
  const allSources = new Set();
  for (const kind of kinds) {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    if (kindCases.length !== casesPerKind) {
      throw new Error(
        `Expected ${casesPerKind} ${kind} structural cases, found ${kindCases.length}`,
      );
    }
    const signatures = new Set(
      kindCases.map((entry) => entry.structuralSignature),
    );
    if (signatures.size !== casesPerKind) {
      throw new Error(
        `${kind} has only ${signatures.size} unique structural signatures`,
      );
    }
    const representedSeeds = new Set(
      kindCases.map((entry) => entry.structuralScenarioId),
    );
    const expectedSeeds = structuralSeedsByKind[kind];
    if (
      representedSeeds.size !== expectedSeeds.length ||
      expectedSeeds.some((seed) => !representedSeeds.has(seed.id))
    ) {
      throw new Error(`${kind} does not cover every curated structural seed`);
    }
    if (
      kindCases.some((entry) =>
        entry.structuralDimensions.itemCount < 1 ||
        entry.structuralDimensions.itemCount > maximumProfileItemCount ||
        entry.structuralDimensions.variant.length === 0
      )
    ) {
      throw new Error(`${kind} has an empty or invalid structural dimension`);
    }
  }
  for (const entry of cases) {
    if (!/^visual_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid structural visual case id: ${entry.id}`);
    }
    if (allIds.has(entry.id)) {
      throw new Error(`Duplicate structural visual case id: ${entry.id}`);
    }
    if (allSources.has(entry.source)) {
      throw new Error(`Duplicate structural visual source: ${entry.id}`);
    }
    if (
      entry.coverageRole !== 'structural-visual-case' ||
      entry.structuralSignature.length !== 64 ||
      entry.sourceSha256.length !== 64
    ) {
      throw new Error(`Invalid structural metadata: ${entry.id}`);
    }
    allIds.add(entry.id);
    allSources.add(entry.source);
  }
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
