import { createHash } from 'node:crypto';
import {
  familyDimensionSpecs,
  renderInputSpaceScenario,
} from './visual-input-space.mjs';
import {
  conformanceCases,
  requiredFeaturesByKind,
} from './production-corpus.mjs';
import { cases as releaseCandidateCases } from './stability-corpus.mjs';

const category = (id, quota, description) => Object.freeze({
  id,
  quota,
  description,
});

export const sharedCoverageCategories = Object.freeze([
  category(
    'official-syntax-atom',
    64,
    'Focused cases anchored by one named upstream syntax or rendering capability.',
  ),
  category(
    'feature-combination',
    64,
    'Pairwise and higher-order combinations across syntax, data, style, and topology.',
  ),
  category(
    'topology-layout-stress',
    48,
    'Scaled structures that exercise family-specific layout and rendering limits.',
  ),
  category(
    'ai-common-valid',
    32,
    'Common valid programs and feature combinations emitted by general-purpose AI models.',
  ),
  category(
    'equivalent-syntax',
    32,
    'Sixteen semantic pairs using implicit and explicit empty theme-variable configuration.',
  ),
  category(
    'historical-regression',
    16,
    'Dense parser, layout, text, style, and routing combinations kept as fixed regressions.',
  ),
]);

const upstreamDirectories = Object.freeze({
  swimlanes: 'swimlanes',
  architecture: 'architecture',
  c4: 'c4',
  railroad: 'railroad',
  treeview: 'treeView',
  xychart: 'xychart',
  quadrant: 'quadrant-chart',
  timeline: 'timeline',
  kanban: 'kanban',
  sequence: 'sequence',
  class: 'class',
  state: 'state',
  er: 'er',
  gantt: 'gantt',
  pie: 'pie',
  journey: 'user-journey',
  requirement: 'requirement',
  gitgraph: 'git',
  mindmap: 'mindmap',
  packet: 'packet',
  radar: 'radar',
  sankey: 'sankey',
  treemap: 'treemap',
  venn: 'venn',
  ishikawa: 'ishikawa',
  cynefin: 'cynefin',
  block: 'block',
  eventmodeling: 'eventmodeling',
  agentflow: 'agentflow',
  usecase: 'usecase',
  wardley: 'wardley',
  zenuml: 'zenuml',
});

const familyKinds = Object.freeze(Object.keys(familyDimensionSpecs));

const conformanceCasesByKind = Object.freeze(
  Object.fromEntries(familyKinds.map((kind) => [
    kind,
    Object.freeze(uniqueBy(
      conformanceCases.filter((entry) => entry.kind === kind),
      (entry) => [...entry.features].sort().join('|'),
    )),
  ])),
);

const releaseCandidateCasesByKind = Object.freeze(
  Object.fromEntries(familyKinds.map((kind) => [
    kind,
    Object.freeze(uniqueBy(
      releaseCandidateCases.filter((entry) => entry.kind === kind),
      (entry) => normalizeSyntaxShape(entry.source),
    )),
  ])),
);

const scheduleContext = Object.freeze({
  domain: Object.freeze({
    code: 'domain',
    name: 'Domain',
    actor: 'Actor',
    platform: 'Platform',
    object: 'Object',
  }),
  goal: Object.freeze({
    code: 'goal',
    name: 'Goal',
    stages: Object.freeze(['Collect', 'Draft', 'Review', 'Publish']),
  }),
  title: 'Coverage Scenario',
  label: (slot) => `Object ${['Collect', 'Draft', 'Review', 'Publish'][slot % 4]}`,
});

export const generatedFamilyCoverageContracts = Object.freeze(
  Object.fromEntries(familyKinds.map((kind) => [
    kind,
    Object.freeze({
      mode: 'coverage-driven',
      caseCount: 256,
      categories: sharedCoverageCategories,
      requiredCapabilities: Object.freeze(
        [
          ...requiredFeaturesByKind[kind],
          ...familyDimensionSpecs[kind].flatMap((dimension) =>
            dimension.options.map((option) =>
              capabilityId(dimension.name, option)
            )
          ),
        ],
      ),
      officialCapabilities: Object.freeze(requiredFeaturesByKind[kind]),
      generatorCapabilities: Object.freeze(
        familyDimensionSpecs[kind].flatMap((dimension) =>
          dimension.options.map((option) =>
            capabilityId(dimension.name, option)
          )
        ),
      ),
      minimumCapabilityOccurrences: 1,
      minimumStructuralProfiles: kind === 'architecture'
        ? 192
        : kind === 'gitgraph'
          ? 224
          : 240,
      equivalentSyntaxGroupCount: 16,
      upstreamReferences: Object.freeze([
        `packages/mermaid/src/diagrams/${upstreamDirectories[kind]}`,
        `demos/${demoName(kind)}.html`,
      ]),
    }),
  ])),
);

const schedulesByKind = Object.freeze(
  Object.fromEntries(familyKinds.map((kind) => [
    kind,
    Object.freeze(buildCoverageSchedule(kind)),
  ])),
);

export function renderFamilyCoverageScenario(kind, index, context) {
  const schedule = schedulesByKind[kind];
  if (schedule === undefined) {
    throw new Error(`Unsupported coverage-driven family: ${kind}`);
  }
  if (!Number.isInteger(index) || index < 0 || index >= 256) {
    throw new Error(`${kind} coverage index must be in 0..255: ${index}`);
  }
  const selection = schedule[index];
  const rendered = selection.referenceCase === undefined
    ? renderInputSpaceScenario(kind, selection.bits, context)
    : renderReferenceScenario(
        selection.referenceCase,
        context,
        selection.referenceRole,
      );
  const capabilities = selection.referenceCase === undefined
    ? rendered.dimensions.map((dimension) =>
        capabilityId(dimension.name, dimension.option)
      )
    : rendered.capabilities;
  const stressed = selection.category === 'topology-layout-stress'
    ? augmentStressScenario(kind, rendered, context, selection.categoryIndex)
    : rendered;
  const source = selection.syntaxVariant === 'second'
    ? withExplicitEmptyThemeVariables(stressed.source)
    : stressed.source;
  return Object.freeze({
    ...stressed,
    source,
    coverage: Object.freeze({
      category: selection.category,
      capabilities: Object.freeze(capabilities),
      ...(selection.focusCapability === undefined
        ? {}
        : { focusCapability: selection.focusCapability }),
      ...(selection.equivalenceGroup === undefined
        ? {}
        : {
            equivalenceGroup: selection.equivalenceGroup,
            syntaxVariant: selection.syntaxVariant,
          }),
    }),
    features: Object.freeze([
      ...stressed.features,
      `coverage-category:${selection.category}`,
      ...(selection.focusCapability === undefined
        ? []
        : [`coverage-focus:${selection.focusCapability}`]),
      ...(selection.syntaxVariant === 'second'
        ? ['explicit-empty-theme-variables']
        : []),
      ...(selection.referenceRole === undefined
        ? []
        : [`curated-${selection.referenceRole}-case`]),
    ]),
  });
}

function buildCoverageSchedule(kind) {
  const dimensions = familyDimensionSpecs[kind];
  const candidates = Array.from({ length: 256 }, (_, vector) => {
    const bits = bitsOf(vector);
    const rendered = renderInputSpaceScenario(kind, bits, scheduleContext);
    return {
      vector,
      bits,
      profile: JSON.stringify(rendered.structuralProfile),
      weight: bits.reduce((sum, bit) => sum + bit, 0),
      stressScore: dimensions.reduce((score, dimension, index) =>
        score +
        (bits[index] === 1 &&
          (dimension.category === 'topology' ||
            dimension.category === 'data' ||
            dimension.category === 'layout')
          ? 2
          : bits[index]), 0),
    };
  });
  const omitted = chooseOmittedCandidates(candidates);
  const available = candidates.filter((candidate) =>
    !omitted.has(candidate.vector)
  );
  const unused = new Map(
    available.map((candidate) => [candidate.vector, candidate]),
  );
  const atoms = [];
  for (let repetition = 0; repetition < 4; repetition += 1) {
    for (let dimensionIndex = 0; dimensionIndex < dimensions.length; dimensionIndex += 1) {
      for (let optionIndex = 0; optionIndex < 2; optionIndex += 1) {
        const focusCapability = capabilityId(
          dimensions[dimensionIndex].name,
          dimensions[dimensionIndex].options[optionIndex],
        );
        const candidate = takeBest(
          unused,
          (entry) => entry.bits[dimensionIndex] === optionIndex,
          (entry) =>
            Math.abs(
              entry.weight -
              (optionIndex + repetition + 1),
            ) * 100 +
            entry.vector,
        );
        atoms.push(scheduleEntry(
          candidate,
          'official-syntax-atom',
          atoms.length,
          { focusCapability },
        ));
      }
    }
  }
  assignReferenceCases(
    atoms,
    conformanceCasesByKind[kind],
    'upstream-conformance',
  );
  const combinations = takeMany(
    unused,
    64,
    (entry) => Math.abs(entry.weight - 4) * 100 + entry.vector,
  ).map((candidate, index) =>
    scheduleEntry(candidate, 'feature-combination', index)
  );
  const stresses = takeMany(
    unused,
    48,
    (entry) => -entry.stressScore * 100 + entry.vector,
  ).map((candidate, index) =>
    scheduleEntry(candidate, 'topology-layout-stress', index)
  );
  const aiCommon = takeMany(
    unused,
    32,
    (entry) => Math.abs(entry.weight - 3) * 100 + grayCode(entry.vector),
  ).map((candidate, index) =>
    scheduleEntry(candidate, 'ai-common-valid', index)
  );
  assignReferenceCases(
    aiCommon,
    releaseCandidateCasesByKind[kind],
    'production-workflow',
  );
  const equivalentBases = takeMany(
    unused,
    16,
    (entry) => Math.abs(entry.weight - 4) * 100 + reverseByte(entry.vector),
  );
  const equivalents = equivalentBases.flatMap((candidate, groupIndex) => [
    scheduleEntry(candidate, 'equivalent-syntax', groupIndex * 2, {
      equivalenceGroup: `${kind}-empty-theme-variables-${String(groupIndex + 1).padStart(2, '0')}`,
      syntaxVariant: 'first',
    }),
    scheduleEntry(candidate, 'equivalent-syntax', groupIndex * 2 + 1, {
      equivalenceGroup: `${kind}-empty-theme-variables-${String(groupIndex + 1).padStart(2, '0')}`,
      syntaxVariant: 'second',
    }),
  ]);
  const regressions = takeMany(
    unused,
    16,
    (entry) => -interactionScore(entry.bits) * 100 + entry.vector,
  ).map((candidate, index) =>
    scheduleEntry(candidate, 'historical-regression', index)
  );
  if (unused.size !== 0) {
    throw new Error(`${kind} coverage schedule left ${unused.size} vectors`);
  }
  return [
    ...atoms,
    ...combinations,
    ...stresses,
    ...aiCommon,
    ...equivalents,
    ...regressions,
  ];
}

function chooseOmittedCandidates(candidates) {
  const omitted = new Set();
  const profiles = groupBy(candidates, (candidate) => candidate.profile);
  for (const group of profiles.values()) {
    for (let index = 1; index < group.length && omitted.size < 16; index += 1) {
      omitted.add(group[index].vector);
    }
    if (omitted.size === 16) {
      return omitted;
    }
  }
  for (const candidate of [...candidates].sort(
    (left, right) => right.vector - left.vector,
  )) {
    if (omitted.size === 16) {
      break;
    }
    omitted.add(candidate.vector);
  }
  return omitted;
}

function takeMany(unused, count, rank) {
  const selected = [...unused.values()]
    .sort((left, right) => rank(left) - rank(right))
    .slice(0, count);
  for (const candidate of selected) {
    unused.delete(candidate.vector);
  }
  return selected;
}

function takeBest(unused, predicate, rank) {
  const candidate = [...unused.values()]
    .filter(predicate)
    .sort((left, right) => rank(left) - rank(right))[0];
  if (candidate === undefined) {
    throw new Error('Unable to allocate a focused coverage candidate');
  }
  unused.delete(candidate.vector);
  return candidate;
}

function scheduleEntry(candidate, categoryId, categoryIndex, extra = {}) {
  return Object.freeze({
    bits: Object.freeze(candidate.bits),
    category: categoryId,
    categoryIndex,
    ...extra,
  });
}

function assignReferenceCases(entries, referenceCases, referenceRole) {
  if (referenceCases.length > entries.length) {
    throw new Error(
      `${referenceRole} has ${referenceCases.length} cases for ` +
        `${entries.length} slots`,
    );
  }
  referenceCases.forEach((referenceCase, index) => {
    entries[index] = Object.freeze({
      ...entries[index],
      referenceCase,
      referenceRole,
      ...(referenceRole === 'upstream-conformance'
        ? { focusCapability: referenceCase.features[0] }
        : {}),
    });
  });
  for (let index = referenceCases.length; index < entries.length; index += 1) {
    entries[index] = Object.freeze(entries[index]);
  }
}

function renderReferenceScenario(referenceCase, context, referenceRole) {
  const source = withScenarioTitle(referenceCase.source, context.title);
  const sourceLines = source.trim().split(/\r?\n/);
  const capabilities = referenceRole === 'upstream-conformance'
    ? referenceCase.features
    : Object.freeze([
        'ai-common-valid',
        'production-workflow',
      ]);
  return Object.freeze({
    source,
    capabilities: Object.freeze([...capabilities]),
    structuralProfile: Object.freeze({
      referenceRole,
      sourceLineCount: sourceLines.length,
      maximumIndent: Math.max(
        ...sourceLines.map((line) => line.length - line.trimStart().length),
      ),
      relationOperatorCount: sourceLines.filter((line) =>
        /(?:-->|<-->|->>|-->>|--|<\|--|\|\||\}\|)/.test(line)
      ).length,
      declarationCount: sourceLines.filter((line) =>
        /[A-Za-z0-9_][[(\s{]/.test(line.trim())
      ).length,
      capabilitySet: Object.freeze([...capabilities].sort()),
      syntaxShapeSha256: sha256(normalizeSyntaxShape(source)),
    }),
    features: Object.freeze([...capabilities]),
    expectedTexts: Object.freeze(referenceCase.expectedTexts ?? []),
    normalizationTexts: Object.freeze([
      referenceCase.title,
      ...(referenceCase.expectedTexts ?? []),
    ]),
    layout: referenceCase.layout ?? defaultLayout(referenceCase.kind),
  });
}

function withScenarioTitle(source, title) {
  const lines = source.trim().split(/\r?\n/);
  const titleLine = `title: ${JSON.stringify(title)}`;
  if (lines[0] !== '---') {
    return ['---', titleLine, '---', ...lines].join('\n');
  }
  const closingIndex = lines.indexOf('---', 1);
  if (closingIndex < 0) {
    throw new Error('Unclosed frontmatter in reference coverage source');
  }
  const existingTitleIndex = lines
    .slice(1, closingIndex)
    .findIndex((line) => /^title\s*:/.test(line));
  if (existingTitleIndex >= 0) {
    const absoluteIndex = existingTitleIndex + 1;
    const existingTitle = yamlScalar(
      lines[absoluteIndex].slice(lines[absoluteIndex].indexOf(':') + 1).trim(),
    );
    lines[absoluteIndex] =
      `title: ${JSON.stringify(`${existingTitle} / ${title}`)}`;
  } else {
    lines.splice(closingIndex, 0, titleLine);
  }
  return lines.join('\n');
}

function yamlScalar(value) {
  if (value.startsWith('"') && value.endsWith('"')) {
    try {
      return JSON.parse(value);
    } catch {
      return value.slice(1, -1);
    }
  }
  if (value.startsWith("'") && value.endsWith("'")) {
    return value.slice(1, -1).replaceAll("''", "'");
  }
  return value;
}

function normalizeSyntaxShape(source) {
  return source
    .replace(/^title\s*:.*$/gm, 'title: TEXT')
    .replace(/"(?:\\.|[^"\\])*"/g, '"TEXT"')
    .replace(/'(?:''|[^'])*'/g, "'TEXT'")
    .replace(/\b\d+(?:\.\d+)?\b/g, 'NUMBER')
    .replace(/[\p{L}_][\p{L}\p{N}_-]*/gu, 'ID')
    .split(/\r?\n/)
    .map((line) => line.trimEnd())
    .filter((line) => line.trim().length > 0)
    .join('\n');
}

function defaultLayout(kind) {
  const layouts = {
    architecture: 'cose-bilkent',
    swimlanes: 'swimlane',
  };
  return layouts[kind] ?? 'dagre';
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}

function augmentStressScenario(kind, rendered, context, categoryIndex) {
  const extraCount = 2 + (categoryIndex % 4);
  const lines = rendered.source.trimEnd().split('\n');
  const label = (index) => `Stress ${index + 1}`;
  switch (kind) {
    case 'swimlanes':
      lines.push(
        '  subgraph Expansion',
        ...Array.from({ length: extraCount }, (_, index) =>
          `    SX${index + 1}[${label(index)}]`
        ),
        '  end',
        '  N4 --> SX1',
        ...chain('SX', extraCount),
      );
      break;
    case 'architecture':
      for (let index = 0; index < extraCount; index += 1) {
        lines.push(`  service sx${index + 1}(server)[${label(index)}]`);
      }
      lines.push('  s4:R --> L:sx1', ...portedChain('sx', extraCount));
      break;
    case 'c4':
      insertBefore(lines, /^\s*(?:Rel|BiRel|UpdateRelStyle)\(/, [
        ...Array.from({ length: extraCount }, (_, index) =>
          `  Container(sx${index + 1}, "${label(index)}", "Kotlin", "Stress worker")`
        ),
      ]);
      lines.push(
        '  Rel(api, sx1, "delegates")',
        ...Array.from({ length: Math.max(0, extraCount - 1) }, (_, index) =>
          `  Rel(sx${index + 1}, sx${index + 2}, "continues")`
        ),
      );
      break;
    case 'railroad':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  stress${index + 1} = sequence(terminal("step${index + 1}"), ` +
        `nonterminal("value${index + 1}")) ;`
      ));
      break;
    case 'treeview':
      if (rendered.structuralProfile.boxDrawing) {
        lines.push(
          '├── stress/',
          ...Array.from({ length: extraCount }, (_, index) =>
            `${index === extraCount - 1 ? '│   └──' : '│   ├──'} stress-${index + 1}.md`
          ),
        );
      } else {
        lines.push(
          '    stress/',
          ...Array.from({ length: extraCount }, (_, index) =>
            `        stress-${index + 1}.md`
          ),
        );
      }
      break;
    case 'xychart': {
      const values = Array.from(
        { length: rendered.structuralProfile.pointCount },
        (_, index) => 22 + index * 7,
      );
      lines.push(`  line "Stress" [${values.join(', ')}]`);
      break;
    }
    case 'quadrant':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  "${label(index)}": [${(0.12 + index * 0.13).toFixed(2)}, ` +
        `${(0.86 - index * 0.11).toFixed(2)}]`
      ));
      break;
    case 'timeline':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `    Stress ${index + 1} : ${context.goal.stages[index % 4]} checkpoint`
      ));
      break;
    case 'kanban':
      lines.push(
        '  stress[Stress]',
        ...Array.from({ length: extraCount }, (_, index) =>
          `    sx${index + 1}[${label(index)}]`
        ),
      );
      break;
    case 'sequence':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  Platform->>Worker: ${label(index)}`
      ));
      break;
    case 'class':
      for (let index = 0; index < extraCount; index += 1) {
        lines.push(
          `  class Stress${index + 1} {`,
          '    +String id',
          '  }',
        );
      }
      lines.push('  Service --> Stress1', ...relationChain('Stress', extraCount));
      break;
    case 'state':
      lines.push('  Archived --> Stress1', ...relationChain('Stress', extraCount));
      break;
    case 'er':
      for (let index = 0; index < extraCount; index += 1) {
        lines.push(
          `  STRESS_${index + 1} {`,
          '    uuid id PK',
          '  }',
        );
      }
      lines.push(
        '  ACCOUNT ||--o{ STRESS_1 : tracks',
        ...Array.from({ length: Math.max(0, extraCount - 1) }, (_, index) =>
          `  STRESS_${index + 1} ||--o{ STRESS_${index + 2} : continues`
        ),
      );
      break;
    case 'gantt':
      lines.push(
        '  section Stress',
        ...Array.from({ length: extraCount }, (_, index) =>
          `  ${label(index)} :sx_${index + 1}, 2027-02-${String(index + 1).padStart(2, '0')}, 1d`
        ),
      );
      break;
    case 'pie':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  "${label(index)}" : ${index + 3}`
      ));
      break;
    case 'journey':
      lines.push(
        '  section Stress',
        ...Array.from({ length: extraCount }, (_, index) =>
          `    ${label(index)}: ${2 + (index % 4)}: Specialist`
        ),
      );
      break;
    case 'requirement':
      for (let index = 0; index < extraCount; index += 1) {
        lines.push(
          `  requirement SX${index + 1} {`,
          `    id: "STRESS-${index + 1}"`,
          `    text: "${label(index)}"`,
          '    risk: medium',
          '    verifyMethod: test',
          '  }',
        );
      }
      lines.push(
        '  R1 - contains -> SX1',
        ...Array.from({ length: Math.max(0, extraCount - 1) }, (_, index) =>
          `  SX${index + 1} - derives -> SX${index + 2}`
        ),
      );
      break;
    case 'gitgraph':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  commit id: "stress-${categoryIndex + 1}-${index + 1}"`
      ));
      break;
    case 'mindmap':
      lines.push(
        '    Stress',
        ...Array.from({ length: extraCount }, (_, index) =>
          `      ${label(index)}`
        ),
      );
      break;
    case 'packet':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  +8: "${label(index)}"`
      ));
      break;
    case 'radar': {
      const entries = Array.from(
        { length: rendered.structuralProfile.axisCount },
        (_, index) => `a${index + 1}: ${30 + ((index * 9) % 60)}`,
      );
      insertBefore(lines, /^\s*(?:min|max|graticule|showLegend|ticks)\b/, [
        `  curve stress["Stress"] { ${entries.join(', ')} }`,
      ]);
      break;
    }
    case 'sankey':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `Stress ${index + 1},Delivery,${index + 3}`
      ));
      break;
    case 'treemap':
      lines.push(
        '"Stress"',
        ...Array.from({ length: extraCount }, (_, index) =>
          `    "${label(index)}": ${index + 5}`
        ),
      );
      break;
    case 'venn':
      lines.push(
        ...Array.from({ length: extraCount }, (_, index) =>
          `  set S${index + 1}["${label(index)}"]:${12 + index}`
        ),
        '  union A,S1["Stress overlap"]:2',
      );
      break;
    case 'ishikawa':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  ${label(index)}\n    Contributing stress factor ${index + 1}`
      ));
      break;
    case 'cynefin':
      insertBefore(lines, /^\s*complicated\s*$/, Array.from(
        { length: extraCount },
        (_, index) => `    "${label(index)}"`,
      ));
      break;
    case 'block':
      lines.push(
        ...Array.from({ length: extraCount }, (_, index) =>
          `  Stress${index + 1}["${label(index)}"]`
        ),
        '  D --> Stress1',
        ...relationChain('Stress', extraCount),
      );
      break;
    case 'eventmodeling':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `  tf ${80 + index} evt StressEvent${index + 1}`
      ));
      break;
    case 'agentflow':
      lines.push(
        ...Array.from({ length: extraCount }, (_, index) =>
          `  S${index + 1}["${label(index)}"]@{ shape: task }`
        ),
        '  D --> S1',
        ...relationChain('S', extraCount),
      );
      break;
    case 'usecase':
      lines.push(
        'actor StressActor("Stress Actor")',
        'systemBoundary stress["Stress"]',
        ...Array.from({ length: extraCount }, (_, index) =>
          `  SX${index + 1}("${label(index)}")`
        ),
        'end',
        'StressActor --> SX1',
        ...relationChain('SX', extraCount),
      );
      break;
    case 'wardley':
      lines.push(
        ...Array.from({ length: extraCount }, (_, index) =>
          `component "${label(index)}" ` +
          `[${(0.30 + index * 0.06).toFixed(2)}, ${(0.18 + index * 0.12).toFixed(2)}]`
        ),
        '"Capability 1" -> "Stress 1"',
        ...quotedRelationChain('Stress', extraCount),
      );
      break;
    case 'zenuml':
      lines.push(...Array.from({ length: extraCount }, (_, index) =>
        `Gateway->Store: ${label(index)}`
      ));
      break;
    default:
      throw new Error(`Missing stress augmentation for ${kind}`);
  }
  return Object.freeze({
    ...rendered,
    source: lines.join('\n'),
    structuralProfile: Object.freeze({
      ...rendered.structuralProfile,
      stressExtraCount: extraCount,
    }),
  });
}

function withExplicitEmptyThemeVariables(source) {
  const lines = source.trimEnd().split('\n');
  if (lines[0] !== '---') {
    return [
      '---',
      'config:',
      '  themeVariables: {}',
      '---',
      ...lines,
    ].join('\n');
  }
  const closingIndex = lines.indexOf('---', 1);
  if (closingIndex < 0) {
    throw new Error('Unclosed frontmatter in coverage source');
  }
  const configIndex = lines.findIndex(
    (line, index) =>
      index > 0 &&
      index < closingIndex &&
      line === 'config:',
  );
  if (configIndex >= 0) {
    lines.splice(configIndex + 1, 0, '  themeVariables: {}');
  } else {
    lines.splice(closingIndex, 0, 'config:', '  themeVariables: {}');
  }
  return lines.join('\n');
}

function insertBefore(lines, pattern, additions) {
  const index = lines.findIndex((line) => pattern.test(line));
  lines.splice(index < 0 ? lines.length : index, 0, ...additions);
}

function relationChain(prefix, count) {
  return Array.from({ length: Math.max(0, count - 1) }, (_, index) =>
    `  ${prefix}${index + 1} --> ${prefix}${index + 2}`
  );
}

function quotedRelationChain(prefix, count) {
  return Array.from({ length: Math.max(0, count - 1) }, (_, index) =>
    `"${prefix} ${index + 1}" -> "${prefix} ${index + 2}"`
  );
}

function portedChain(prefix, count) {
  return Array.from({ length: Math.max(0, count - 1) }, (_, index) =>
    `  ${prefix}${index + 1}:R --> L:${prefix}${index + 2}`
  );
}

function chain(prefix, count) {
  return Array.from({ length: Math.max(0, count - 1) }, (_, index) =>
    `  ${prefix}${index + 1} --> ${prefix}${index + 2}`
  );
}

function capabilityId(name, option) {
  return `${name}:${option}`;
}

function bitsOf(vector) {
  return Array.from({ length: 8 }, (_, bit) => (vector >> bit) & 1);
}

function interactionScore(bits) {
  let score = 0;
  for (let left = 0; left < bits.length; left += 1) {
    for (let right = left + 1; right < bits.length; right += 1) {
      if (bits[left] !== bits[right]) {
        score += 1;
      }
    }
  }
  return score;
}

function grayCode(value) {
  return value ^ (value >> 1);
}

function reverseByte(value) {
  let reversed = 0;
  for (let bit = 0; bit < 8; bit += 1) {
    reversed |= ((value >> bit) & 1) << (7 - bit);
  }
  return reversed;
}

function groupBy(entries, keyOf) {
  const groups = new Map();
  for (const entry of entries) {
    const key = keyOf(entry);
    const group = groups.get(key) ?? [];
    group.push(entry);
    groups.set(key, group);
  }
  return groups;
}

function uniqueBy(entries, keyOf) {
  const unique = new Map();
  for (const entry of entries) {
    const key = keyOf(entry);
    if (!unique.has(key)) {
      unique.set(key, entry);
    }
  }
  return [...unique.values()];
}

function demoName(kind) {
  const names = {
    swimlanes: 'flowchart',
    treeview: 'treeView',
    xychart: 'xychart',
    quadrant: 'quadrantchart',
    class: 'classchart',
    requirement: 'requirements',
    gitgraph: 'git',
    eventmodeling: 'eventmodeling',
    agentflow: 'dataflowchart',
  };
  return names[kind] ?? kind;
}
