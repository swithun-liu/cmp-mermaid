const category = (id, quota, description) => Object.freeze({
  id,
  quota,
  description,
});

export const flowchartCoverageContract = Object.freeze({
  mode: 'coverage-driven',
  caseCount: 256,
  categories: Object.freeze([
    category(
      'official-syntax-atom',
      64,
      'One upstream Flowchart syntax or rendering capability at a time.',
    ),
    category(
      'feature-combination',
      64,
      'Pairwise and three-way combinations across syntax, style, and topology.',
    ),
    category(
      'topology-layout-stress',
      48,
      'Scaled graph structures that stress Dagre routing and compound layout.',
    ),
    category(
      'ai-common-valid',
      32,
      'Common valid Flowchart programs emitted by general-purpose AI models.',
    ),
    category(
      'equivalent-syntax',
      32,
      'Sixteen semantic pairs expressed through different legal syntax.',
    ),
    category(
      'historical-regression',
      16,
      'Previously fragile parser, layout, text, style, and routing structures.',
    ),
  ]),
  requiredCapabilities: Object.freeze([
    'headers',
    'directions',
    'classic-shapes',
    'metadata-shapes',
    'expanded-shape-catalog',
    'solid-edges',
    'open-edges',
    'dotted-edges',
    'thick-edges',
    'invisible-edges',
    'bidirectional-edges',
    'circle-markers',
    'cross-markers',
    'minimum-length',
    'edge-labels',
    'edge-ids',
    'edge-animation',
    'multi-node-links',
    'subgraphs',
    'nested-subgraphs',
    'subgraph-direction',
    'collapsed-subgraphs',
    'classes',
    'inline-styles',
    'link-styles',
    'frontmatter-config',
    'comments',
    'markdown-labels',
    'html-labels',
    'entities',
    'unicode',
    'cycles',
    'self-loops',
    'parallel-edges',
    'disconnected-components',
    'dense-crossings',
  ]),
  minimumStructuralProfiles: 224,
  equivalentSyntaxGroupCount: 16,
});

const metadataShapes = Object.freeze([
  'rect',
  'rounded',
  'stadium',
  'subproc',
  'cyl',
  'circle',
  'odd',
  'diamond',
  'hex',
  'lean-r',
  'lean-l',
  'datastore',
  'trap-b',
  'trap-t',
  'dbl-circ',
  'text',
  'notch-rect',
  'lin-rect',
  'sm-circ',
  'framed-circle',
  'fork',
  'hourglass',
  'comment',
  'brace-r',
  'braces',
  'bolt',
  'doc',
  'delay',
  'das',
  'lin-cyl',
  'curv-trap',
  'div-rect',
  'tri',
  'win-pane',
  'f-circ',
  'lin-doc',
  'notch-pent',
  'flip-tri',
  'sl-rect',
  'docs',
  'processes',
  'flag',
  'bow-rect',
  'cross-circ',
  'tag-doc',
  'tag-rect',
]);

const directionAtoms = Object.freeze(['TB', 'TD', 'BT', 'LR', 'RL']);

const edgeAtoms = Object.freeze([
  ['---', 'open-edges'],
  ['-->', 'solid-edges'],
  ['-.->', 'dotted-edges'],
  ['-.-', 'dotted-edges'],
  ['==>', 'thick-edges'],
  ['===', 'thick-edges'],
  ['~~~', 'invisible-edges'],
  ['<-->', 'bidirectional-edges'],
  ['o--o', 'circle-markers'],
  ['x--x', 'cross-markers'],
  ['o==o', 'circle-markers'],
  ['x-.-x', 'cross-markers'],
  ['---->', 'minimum-length'],
]);

const combinationTopologies = Object.freeze([
  ['linear', [['A', 'B'], ['B', 'C'], ['C', 'D'], ['D', 'E'], ['E', 'F']]],
  ['fan-out', [['A', 'B'], ['A', 'C'], ['A', 'D'], ['B', 'E'], ['C', 'E'], ['D', 'E'], ['E', 'F']]],
  ['fan-in', [['A', 'D'], ['B', 'D'], ['C', 'D'], ['D', 'E'], ['E', 'F']]],
  ['diamond', [['A', 'B'], ['A', 'C'], ['B', 'D'], ['C', 'D'], ['D', 'E'], ['E', 'F']]],
  ['cycle', [['A', 'B'], ['B', 'C'], ['C', 'D'], ['D', 'B'], ['D', 'E'], ['E', 'F']]],
  ['self-loop', [['A', 'A'], ['A', 'B'], ['B', 'C'], ['C', 'D'], ['D', 'E'], ['E', 'F']]],
  ['disconnected', [['A', 'B'], ['B', 'C'], ['D', 'E'], ['E', 'F']]],
  ['many-to-many', [['A', 'D'], ['A', 'E'], ['B', 'D'], ['B', 'F'], ['C', 'E'], ['C', 'F']]],
]);

const combinationBundles = Object.freeze([
  {
    id: 'classic-labelled',
    capabilities: ['classic-shapes', 'edge-labels'],
  },
  {
    id: 'metadata-dotted',
    capabilities: ['metadata-shapes', 'dotted-edges'],
  },
  {
    id: 'grouped-direction',
    capabilities: ['subgraphs', 'subgraph-direction'],
  },
  {
    id: 'classes-inline-style',
    capabilities: ['classes', 'inline-styles'],
  },
  {
    id: 'edge-id-animation',
    capabilities: ['edge-ids', 'edge-animation'],
  },
  {
    id: 'markdown-html',
    capabilities: ['markdown-labels', 'html-labels'],
  },
  {
    id: 'markers-length',
    capabilities: ['circle-markers', 'cross-markers', 'minimum-length'],
  },
  {
    id: 'configured-unicode',
    capabilities: ['frontmatter-config', 'comments', 'unicode'],
  },
]);

const stressFamilies = Object.freeze([
  'wide-fan-out',
  'deep-chain',
  'layered-mesh',
  'ring-with-chords',
  'parallel-lanes',
  'nested-compounds',
]);

const equivalentSyntaxPairs = Object.freeze([
  {
    id: 'graph-header',
    capabilities: ['headers'],
    variants: (context) => [
      lines(
        'graph LR',
        declarations(context, 4),
        'A --> B',
        'A --> C',
        'B & C --> D',
      ),
      lines(
        'flowchart LR',
        declarations(context, 4),
        'A --> B',
        'A --> C',
        'B & C --> D',
      ),
    ],
  },
  {
    id: 'top-down-alias',
    capabilities: ['directions'],
    variants: (context) => [
      lines(
        'flowchart TB',
        declarations(context, 4),
        'A --> B & C',
        'B & C --> D',
      ),
      lines(
        'flowchart TD',
        declarations(context, 4),
        'A --> B & C',
        'B & C --> D',
      ),
    ],
  },
  {
    id: 'edge-label-form',
    capabilities: ['edge-labels'],
    variants: (context) => [
      lines('flowchart LR', declarations(context, 2), 'A -- accepted --> B'),
      lines('flowchart LR', declarations(context, 2), 'A -->|accepted| B'),
    ],
  },
  shapeSyntaxPair('rectangle-shape', '[', ']', 'rect'),
  shapeSyntaxPair('rounded-shape', '(', ')', 'rounded'),
  shapeSyntaxPair('database-shape', '[(', ')]', 'cyl'),
  shapeSyntaxPair('decision-shape', '{', '}', 'diamond'),
  {
    id: 'class-assignment',
    capabilities: ['classes'],
    variants: (context) => [
      lines(
        'flowchart LR',
        `A["${context.label(0)}"]:::hot --> B["${context.label(1)}"]`,
        'classDef hot fill:#dbeafe,stroke:#2563eb',
      ),
      lines(
        'flowchart LR',
        `A["${context.label(0)}"] --> B["${context.label(1)}"]`,
        'classDef hot fill:#dbeafe,stroke:#2563eb',
        'class A hot',
      ),
    ],
  },
  {
    id: 'chain-expansion',
    capabilities: ['solid-edges'],
    variants: (context) => [
      lines('flowchart LR', declarations(context, 4), 'A --> B --> C --> D'),
      lines(
        'flowchart LR',
        declarations(context, 4),
        'A --> B',
        'B --> C',
        'C --> D',
      ),
    ],
  },
  {
    id: 'grouped-targets',
    capabilities: ['multi-node-links'],
    variants: (context) => [
      lines('flowchart LR', declarations(context, 3), 'A --> B & C'),
      lines('flowchart LR', declarations(context, 3), 'A --> B', 'A --> C'),
    ],
  },
  {
    id: 'grouped-sources',
    capabilities: ['multi-node-links'],
    variants: (context) => [
      lines('flowchart LR', declarations(context, 3), 'A & B --> C'),
      lines('flowchart LR', declarations(context, 3), 'A --> C', 'B --> C'),
    ],
  },
  {
    id: 'quoted-label',
    capabilities: ['classic-shapes'],
    variants: (context) => [
      lines(
        'flowchart LR',
        `A["${context.label(0)}"] --> B["${context.label(1)}"] --> C["${context.label(2)}"]`,
      ),
      lines(
        'flowchart LR',
        `A[${context.label(0)}] --> B[${context.label(1)}] --> C[${context.label(2)}]`,
      ),
    ],
  },
  {
    id: 'subgraph-title-form',
    capabilities: ['subgraphs'],
    variants: (context) => [
      lines(
        'flowchart LR',
        'subgraph G',
        `  A["${context.label(0)}"] --> B["${context.label(1)}"]`,
        'end',
      ),
      lines(
        'flowchart LR',
        'subgraph G [G]',
        `  A["${context.label(0)}"] --> B["${context.label(1)}"]`,
        'end',
      ),
    ],
  },
  {
    id: 'entity-form',
    capabilities: ['entities', 'unicode'],
    variants: (context) => [
      lines(
        'flowchart LR',
        `A["${context.label(0)} #9829;"] --> B["${context.label(1)}"]`,
      ),
      lines(
        'flowchart LR',
        `A["${context.label(0)} \u2665"] --> B["${context.label(1)}"]`,
      ),
    ],
  },
  {
    id: 'style-form',
    capabilities: ['classes', 'inline-styles'],
    variants: (context) => [
      lines(
        'flowchart LR',
        declarations(context, 2),
        'A --> B',
        'style A fill:#dbeafe,stroke:#2563eb',
      ),
      lines(
        'flowchart LR',
        declarations(context, 2),
        'A --> B',
        'classDef hot fill:#dbeafe,stroke:#2563eb',
        'class A hot',
      ),
    ],
  },
  {
    id: 'inline-declaration',
    capabilities: ['classic-shapes', 'solid-edges'],
    variants: (context) => [
      lines(
        'flowchart LR',
        `A["${context.label(0)}"] --> B["${context.label(1)}"]`,
        `B --> C["${context.label(2)}"]`,
      ),
      lines(
        'flowchart LR',
        `A["${context.label(0)}"]`,
        `B["${context.label(1)}"]`,
        `C["${context.label(2)}"]`,
        'A --> B',
        'B --> C',
      ),
    ],
  },
]);

export function renderFlowchartCoverageScenario(index, context) {
  if (!Number.isInteger(index) || index < 0 || index >= 256) {
    throw new Error(`Flowchart coverage index must be in 0..255: ${index}`);
  }
  if (index < 64) return renderAtomic(index, context);
  if (index < 128) return renderCombination(index - 64, context);
  if (index < 176) return renderStress(index - 128, context);
  if (index < 208) return renderAiCommon(index - 176, context);
  if (index < 240) return renderEquivalent(index - 208, context);
  return renderRegression(index - 240, context);
}

function renderAtomic(index, context) {
  if (index < metadataShapes.length) {
    const shape = metadataShapes[index];
    return coverageCase({
      category: 'official-syntax-atom',
      capabilities: ['metadata-shapes', 'expanded-shape-catalog', `shape:${shape}`],
      source: lines(
        'flowchart LR',
        `A@{ shape: ${shape}, label: "${context.label(0)}" }`,
        `A --> B["${context.label(1)}"]`,
      ),
      structuralProfile: {
        family: 'metadata-shape',
        shape,
        nodeCount: 2,
        edgeCount: 1,
      },
      expectedTexts: [context.label(1)],
    });
  }
  const directionIndex = index - metadataShapes.length;
  if (directionIndex < directionAtoms.length) {
    const direction = directionAtoms[directionIndex];
    return coverageCase({
      category: 'official-syntax-atom',
      capabilities: ['directions', `direction:${direction}`],
      source: lines(
        `flowchart ${direction}`,
        declarations(context, 3),
        'A --> B --> C',
      ),
      structuralProfile: {
        family: 'direction',
        direction,
        nodeCount: 3,
        edgeCount: 2,
      },
      expectedTexts: [context.label(0)],
    });
  }
  const [operator, capability] = edgeAtoms[directionIndex - directionAtoms.length];
  return coverageCase({
    category: 'official-syntax-atom',
    capabilities: [capability, `edge-operator:${operator}`],
    source: lines(
      'flowchart LR',
      declarations(context, 2),
      `A ${operator} B`,
    ),
    structuralProfile: {
      family: 'edge-operator',
      operator,
      nodeCount: 2,
      edgeCount: 1,
    },
    expectedTexts: [context.label(0)],
  });
}

function renderCombination(index, context) {
  const [topology, edges] = combinationTopologies[Math.floor(index / 8)];
  const bundle = combinationBundles[index % 8];
  const prefix = [];
  const body = ['flowchart LR'];
  if (bundle.id === 'configured-unicode') {
    prefix.push(
      '---',
      'config:',
      '  flowchart:',
      '    curve: stepAfter',
      '---',
    );
    body.push('%% configuration and Unicode are intentionally combined');
  }
  body.push(...combinationDeclarations(bundle.id, context));
  edges.forEach(([from, to], edgeIndex) => {
    body.push(combinationEdge(bundle.id, from, to, edgeIndex));
  });
  if (bundle.id === 'classes-inline-style') {
    body.push(
      'classDef hot fill:#dbeafe,stroke:#2563eb,color:#172554',
      'class A,C hot',
      'style F fill:#dcfce7,stroke:#15803d',
    );
  } else if (bundle.id === 'edge-id-animation') {
    body.push('e1@{ animation: fast }');
  }
  return coverageCase({
    category: 'feature-combination',
    capabilities: [
      ...bundle.capabilities,
      topologyCapability(topology),
    ],
    source: lines(...prefix, ...body),
    structuralProfile: {
      family: 'feature-combination',
      topology,
      featureBundle: bundle.id,
      nodeCount: 6,
      edgeCount: edges.length,
    },
    expectedTexts: [context.label(0)],
  });
}

function combinationDeclarations(bundleId, context) {
  if (bundleId === 'metadata-dotted') {
    return [
      `A@{ shape: rounded, label: "${context.label(0)}" }`,
      `B@{ shape: doc, label: "${context.label(1)}" }`,
      `C@{ shape: diamond, label: "${context.label(2)}" }`,
      `D@{ shape: subproc, label: "${context.label(3)}" }`,
      `E@{ shape: cyl, label: "${context.label(4)}" }`,
      `F@{ shape: stadium, label: "${context.label(5)}" }`,
    ];
  }
  if (bundleId === 'grouped-direction') {
    return [
      'subgraph G [Delivery]',
      '  direction TB',
      ...declarations(context, 4).split('\n').map((line) => `  ${line}`),
      'end',
      `E["${context.label(4)}"]`,
      `F["${context.label(5)}"]`,
    ];
  }
  if (bundleId === 'markdown-html') {
    return [
      `A["\`**${context.label(0)}**\`"]`,
      `B["<b>${context.label(1)}</b>"]`,
      `C["${context.label(2)}<br/>continued"]`,
      `D["\`_${context.label(3)}_\`"]`,
      `E["<code>${context.label(4)}</code>"]`,
      `F["${context.label(5)}"]`,
    ];
  }
  if (bundleId === 'classic-labelled') {
    return [
      `A["${context.label(0)}"]`,
      `B("${context.label(1)}")`,
      `C{"${context.label(2)}"}`,
      `D[["${context.label(3)}"]]`,
      `E[("${context.label(4)}")]`,
      `F(["${context.label(5)}"])`,
    ];
  }
  if (bundleId === 'configured-unicode') {
    return [
      `A["${context.label(0)} \u2713"]`,
      ...declarations(context, 6).split('\n').slice(1),
    ];
  }
  return declarations(context, 6).split('\n');
}

function combinationEdge(bundleId, from, to, index) {
  if (bundleId === 'metadata-dotted') return `${from} -.-> ${to}`;
  if (bundleId === 'classic-labelled' && index === 0) {
    return `${from} -->|accepted| ${to}`;
  }
  if (bundleId === 'edge-id-animation' && index === 0) {
    return `${from} e1@--> ${to}`;
  }
  if (bundleId === 'markers-length') {
    if (index === 0) return `${from} o--x ${to}`;
    if (index === 1) return `${from} ----> ${to}`;
  }
  return `${from} --> ${to}`;
}

function renderStress(index, context) {
  const family = stressFamilies[Math.floor(index / 8)];
  const scale = index % 8;
  const rendered = {
    'wide-fan-out': stressWideFanOut,
    'deep-chain': stressDeepChain,
    'layered-mesh': stressLayeredMesh,
    'ring-with-chords': stressRing,
    'parallel-lanes': stressParallel,
    'nested-compounds': stressNested,
  }[family](scale, context);
  return coverageCase({
    category: 'topology-layout-stress',
    capabilities: rendered.capabilities,
    source: rendered.source,
    structuralProfile: {
      family,
      scale: scale + 1,
      nodeCount: rendered.nodeCount,
      edgeCount: rendered.edgeCount,
      groupDepth: rendered.groupDepth ?? 0,
    },
    expectedTexts: [context.label(0)],
  });
}

function stressWideFanOut(scale, context) {
  const width = scale + 4;
  const nodes = Array.from({ length: width }, (_, index) => `N${index}`);
  return {
    capabilities: ['dense-crossings', 'multi-node-links'],
    source: lines(
      'flowchart TB',
      `Start["${context.label(0)}"]`,
      ...nodes.map((id, index) => `${id}["${context.label(index + 1)}"]`),
      `Finish["${context.label(2)}"]`,
      `Start --> ${nodes.join(' & ')}`,
      `${nodes.join(' & ')} --> Finish`,
    ),
    nodeCount: width + 2,
    edgeCount: width * 2,
  };
}

function stressDeepChain(scale, context) {
  const count = scale + 7;
  const nodes = Array.from({ length: count }, (_, index) => `N${index}`);
  const chain = nodes.map((node, index) => {
    if (index === 0) return node;
    return `${index === 1 ? ' ----> ' : ' --> '}${node}`;
  }).join('');
  return {
    capabilities: ['solid-edges', 'minimum-length'],
    source: lines(
      `flowchart ${scale % 2 === 0 ? 'LR' : 'TB'}`,
      ...nodes.map((id, index) => `${id}["${context.label(index)}"]`),
      chain,
    ),
    nodeCount: count,
    edgeCount: count - 1,
  };
}

function stressLayeredMesh(scale, context) {
  const width = 2 + (scale % 3);
  const layerCount = 3 + Math.floor(scale / 3);
  const layers = Array.from({ length: layerCount }, (_, layer) =>
    Array.from({ length: width }, (_, column) => `L${layer}N${column}`)
  );
  const edges = layers.slice(0, -1).flatMap((layer, layerIndex) =>
    layer.flatMap((from) =>
      layers[layerIndex + 1].map((to) => `${from} --> ${to}`)
    )
  );
  return {
    capabilities: ['dense-crossings'],
    source: lines(
      'flowchart TB',
      ...layers.flat().map((id, index) => `${id}["${context.label(index)}"]`),
      ...edges,
    ),
    nodeCount: width * layerCount,
    edgeCount: edges.length,
  };
}

function stressRing(scale, context) {
  const count = scale + 6;
  const nodes = Array.from({ length: count }, (_, index) => `N${index}`);
  const ring = nodes.map((from, index) =>
    `${from} --> ${nodes[(index + 1) % count]}`
  );
  const chords = nodes
    .filter((_, index) => index % 2 === 0)
    .map((from, index) => `${from} -.-> ${nodes[(index * 2 + 3) % count]}`);
  return {
    capabilities: ['cycles', 'dotted-edges'],
    source: lines(
      'flowchart LR',
      ...nodes.map((id, index) => `${id}["${context.label(index)}"]`),
      ...ring,
      ...chords,
    ),
    nodeCount: count,
    edgeCount: ring.length + chords.length,
  };
}

function stressParallel(scale, context) {
  const count = scale + 2;
  const operators = ['-->', '-.->', '==>', 'o--o'];
  return {
    capabilities: ['parallel-edges', 'edge-labels'],
    source: lines(
      'flowchart LR',
      declarations(context, 2),
      ...Array.from(
        { length: count },
        (_, index) => `A ${operators[index % operators.length]}|lane ${index + 1}| B`,
      ),
    ),
    nodeCount: 2,
    edgeCount: count,
  };
}

function stressNested(scale, context) {
  const depth = scale + 1;
  const source = ['flowchart LR'];
  for (let index = 0; index < depth; index += 1) {
    source.push(
      `${'  '.repeat(index)}subgraph G${index} [Group]`,
      `${'  '.repeat(index + 1)}direction ${index % 2 === 0 ? 'TB' : 'LR'}`,
    );
  }
  source.push(
    `${'  '.repeat(depth)}A["${context.label(0)}"] --> B["${context.label(1)}"]`,
  );
  for (let index = depth - 1; index >= 0; index -= 1) {
    source.push(`${'  '.repeat(index)}end`);
  }
  source.push(`Start["${context.label(2)}"] --> A`, `B --> Finish["${context.label(3)}"]`);
  return {
    capabilities: ['subgraphs', 'nested-subgraphs', 'subgraph-direction'],
    source: source.join('\n'),
    nodeCount: 4,
    edgeCount: 3,
    groupDepth: depth,
  };
}

function renderAiCommon(index, context) {
  const pattern = Math.floor(index / 4);
  const complexity = index % 4;
  const source = ['flowchart TB'];
  const capabilities = ['classic-shapes', 'solid-edges'];
  switch (pattern) {
    case 0:
      source.push(
        `A(["${context.label(0)}"]) --> B{"${context.label(1)}?"}`,
        `B -->|Yes| C["${context.label(2)}"]`,
        `B -->|No| D["${context.label(3)}"]`,
      );
      if (complexity >= 1) source.push('D --> A');
      if (complexity >= 2) source.push('C --> E[Audit]', 'D --> E');
      if (complexity >= 3) source.push('E --> F((Done))');
      capabilities.push('edge-labels', complexity >= 1 ? 'cycles' : 'branching');
      break;
    case 1:
      source.push(
        declarations(context, 4),
        'A --> B --> C --> D',
      );
      if (complexity >= 1) source.push('B -. retry .-> A');
      if (complexity >= 2) source.push('B --> E[Cache]', 'E --> C');
      if (complexity >= 3) source.push('C ==> F[(Store)]', 'F --> D');
      capabilities.push('cycles', 'dotted-edges');
      break;
    case 2: {
      const workerCount = complexity + 2;
      const workers = Array.from({ length: workerCount }, (_, value) => `W${value}`);
      source.push(
        `A["${context.label(0)}"]`,
        ...workers.map((id, value) => `${id}["${context.label(value + 1)}"]`),
        `Z["${context.label(3)}"]`,
        `A --> ${workers.join(' & ')}`,
        `${workers.join(' & ')} --> Z`,
      );
      capabilities.push('multi-node-links', 'dense-crossings');
      break;
    }
    case 3:
      source.push(
        `A["${context.label(0)}"] --> B{"${context.label(1)}?"}`,
        `B --> C["${context.label(2)}"] --> D["${context.label(3)}"]`,
      );
      if (complexity >= 1) source.push('B --> E[Fallback]', 'E --> D');
      if (complexity >= 2) source.push('D -. refresh .-> A');
      if (complexity >= 3) source.push('C --> C');
      capabilities.push('cycles', complexity >= 3 ? 'self-loops' : 'branching');
      break;
    case 4:
      source.push(
        'subgraph G [Service]',
        '  direction LR',
        `  A["${context.label(0)}"] --> B["${context.label(1)}"]`,
      );
      if (complexity >= 2) {
        source.push(
          '  subgraph Inner [Worker]',
          `    C["${context.label(2)}"]`,
          '  end',
          '  B --> C',
        );
      }
      source.push('end', `Start["${context.label(3)}"] --> A`, 'B --> Finish');
      if (complexity % 2 === 1) source.push('Finish -.-> Start');
      capabilities.push('subgraphs', complexity >= 2 ? 'nested-subgraphs' : 'subgraph-direction');
      break;
    case 5: {
      const left = complexity + 2;
      const right = 2 + (complexity % 2);
      const sources = Array.from({ length: left }, (_, value) => `S${value}`);
      const targets = Array.from({ length: right }, (_, value) => `T${value}`);
      source.push(
        ...sources.map((id, value) => `${id}["${context.label(value)}"]`),
        ...targets.map((id, value) => `${id}["${context.label(value + 2)}"]`),
        `${sources.join(' & ')} --> ${targets.join(' & ')}`,
      );
      capabilities.push('multi-node-links', 'dense-crossings');
      break;
    }
    case 6:
      source.push(declarations(context, 2));
      Array.from({ length: complexity + 2 }, (_, value) => value).forEach(
        (value) => source.push(
          value % 3 === 0
            ? `A -->|route ${value + 1}| B`
            : value % 3 === 1
              ? 'A -.-> B'
              : 'A ==> B',
        ),
      );
      capabilities.push('parallel-edges', 'edge-labels');
      break;
    default:
      source.push(
        `A1["${context.label(0)}"] --> A2["${context.label(1)}"]`,
        `B1{"${context.label(2)}?"} --> B2["${context.label(3)}"]`,
      );
      if (complexity >= 1) source.push('B1 --> B3[Alternate]');
      if (complexity >= 2) source.push('C1((Start)) --> C2[(Store)]');
      if (complexity >= 3) source.push('A2 --> B1');
      capabilities.push(
        complexity >= 3 ? 'branching' : 'disconnected-components',
        'classic-shapes',
      );
  }
  if (complexity === 3 && pattern % 2 === 0) {
    source.push(
      'classDef active fill:#dcfce7,stroke:#15803d',
      'class A active',
    );
    capabilities.push('classes');
  }
  return coverageCase({
    category: 'ai-common-valid',
    capabilities,
    source: source.join('\n'),
    structuralProfile: {
      family: 'ai-common-valid',
      pattern,
      complexity: complexity + 1,
      statementCount: source.length - 1,
    },
    expectedTexts: [context.label(0)],
  });
}

function renderEquivalent(index, context) {
  const pairIndex = Math.floor(index / 2);
  const variant = index % 2;
  const pair = equivalentSyntaxPairs[pairIndex];
  return coverageCase({
    category: 'equivalent-syntax',
    capabilities: pair.capabilities,
    source: pair.variants(context)[variant],
    structuralProfile: {
      family: 'equivalent-syntax',
      equivalenceGroup: pair.id,
    },
    expectedTexts: [context.label(0)],
    equivalenceGroup: pair.id,
    syntaxVariant: variant === 0 ? 'first' : 'second',
  });
}

function renderRegression(index, context) {
  const regressions = [
    regressionNestedFeedback,
    regressionCrossSubgraph,
    regressionDeepValidation,
    regressionParallelProtocol,
    regressionStyledDecision,
    regressionLongUnicode,
    regressionMixedShapes,
    regressionCycleShortcuts,
    regressionDenseLengths,
    regressionNestedDirections,
    regressionDisconnected,
    regressionRichText,
    regressionAnimatedEdges,
    regressionCollapsedGroup,
    regressionDenseFanInOut,
    regressionDecisionEndpoints,
  ];
  const rendered = regressions[index](context);
  return coverageCase({
    category: 'historical-regression',
    capabilities: rendered.capabilities,
    source: rendered.source,
    structuralProfile: {
      family: 'historical-regression',
      regression: rendered.id,
      nodeCount: rendered.nodeCount,
      edgeCount: rendered.edgeCount,
    },
    expectedTexts: rendered.expectedTexts ?? [context.label(0)],
  });
}

function regressionNestedFeedback(context) {
  return regression(
    'nested-feedback',
    ['nested-subgraphs', 'cycles'],
    6,
    7,
    lines(
      'flowchart LR',
      'subgraph Outer [Platform]',
      '  subgraph Inner [Runtime]',
      `    A["${context.label(0)}"] --> B["${context.label(1)}"]`,
      '  end',
      `  C["${context.label(2)}"] --> D["${context.label(3)}"]`,
      '  B --> C',
      'end',
      'Start --> A',
      'D --> Finish',
      'D -. retry .-> B',
    ),
  );
}

function regressionCrossSubgraph(context) {
  return regression(
    'cross-subgraph',
    ['subgraphs', 'subgraph-direction', 'dense-crossings'],
    6,
    7,
    lines(
      'flowchart TB',
      'subgraph G [Ingestion]',
      '  direction BT',
      `  A@{ shape: hex, label: "${context.label(0)}" }`,
      `  B@{ shape: doc, label: "${context.label(1)}" }`,
      '  A --> B',
      'end',
      `C@{ shape: notch-rect, label: "${context.label(2)}" }`,
      `D@{ shape: cyl, label: "${context.label(3)}" }`,
      'B ==> C --> D',
      'A -. bypass .-> C',
      'D -. retry .-> B',
    ),
  );
}

function regressionDeepValidation(context) {
  return regression(
    'deep-validation',
    ['classic-shapes', 'cycles', 'edge-labels'],
    9,
    11,
    lines(
      'flowchart LR',
      `A[/"${context.label(0)}"/] --> B["${context.label(1)}"]`,
      `B --> C{"${context.label(2)}?"}`,
      'C -->|No| Reject[Reject]',
      `C -->|Yes| D{"${context.label(3)}?"}`,
      'D -->|No| Reject',
      'D -->|Yes| E[Enrich] --> F{Rules pass?}',
      'F -->|No| Review[[Review]] --> F',
      'F -->|Yes| Store[(Store)] --> Done((Done))',
    ),
  );
}

function regressionParallelProtocol(context) {
  return regression(
    'parallel-protocol',
    ['parallel-edges', 'edge-labels', 'circle-markers'],
    2,
    5,
    lines(
      'flowchart LR',
      declarations(context, 2),
      'A -->|request| B',
      'A -.->|retry| B',
      'A ==>|priority| B',
      'A o--o B',
      'B -->|response| A',
    ),
  );
}

function regressionStyledDecision(context) {
  return regression(
    'styled-decision',
    ['classes', 'link-styles', 'edge-labels'],
    5,
    6,
    lines(
      'flowchart TB',
      `A{"${context.label(0)}?"} -->|Low| B["${context.label(1)}"]`,
      `A -->|Medium| C["${context.label(2)}"]`,
      `A -->|High| D["${context.label(3)}"]`,
      'C -->|Pass| B',
      'C -->|Fail| D',
      'B --> E([Done])',
      'classDef success fill:#dcfce7,stroke:#15803d',
      'classDef danger fill:#fee2e2,stroke:#dc2626',
      'class B,E success',
      'class D danger',
      'linkStyle 0,3 stroke:#16a34a,stroke-width:3px',
      'linkStyle 2,4 stroke:#dc2626,stroke-width:3px',
    ),
  );
}

function regressionLongUnicode(context) {
  return regression(
    'long-unicode',
    ['unicode', 'cycles', 'edge-labels'],
    5,
    6,
    lines(
      'flowchart TB',
      `A["${context.label(0)} \u63d0\u4ea4\u4e0a\u4e0b\u6587"] --> B{"${context.label(1)}?"}`,
      `B -->|\u5426| C["${context.label(2)} \u7b49\u5f85\u8865\u5145"]`,
      'C --> A',
      `B -->|\u662f| D["${context.label(3)} \u751f\u6210\u7b54\u6848"]`,
      'D --> E{Verified?}',
      'E -->|No| D',
    ),
  );
}

function regressionMixedShapes(context) {
  const shapes = ['rounded', 'doc', 'diamond', 'subproc', 'cyl', 'lin-cyl', 'docs', 'hourglass'];
  return regression(
    'mixed-shapes',
    ['metadata-shapes', 'expanded-shape-catalog'],
    shapes.length,
    shapes.length - 1,
    lines(
      'flowchart LR',
      ...shapes.map(
        (shape, value) =>
          `N${value}@{ shape: ${shape}, label: "${context.label(value)}" }`,
      ),
      shapes.map((_, value) => `N${value}`).join(' --> '),
    ),
  );
}

function regressionCycleShortcuts(context) {
  return regression(
    'cycle-shortcuts',
    ['cycles', 'self-loops', 'dotted-edges', 'thick-edges'],
    5,
    9,
    lines(
      'flowchart LR',
      declarations(context, 5),
      'A --> B --> C --> D --> E',
      'E --> A',
      'B --> D',
      'C --> C',
      'E -. retry .-> C',
      'A ==>|priority| D',
    ),
  );
}

function regressionDenseLengths(context) {
  return regression(
    'dense-lengths',
    ['minimum-length', 'dense-crossings'],
    7,
    11,
    lines(
      'flowchart TD',
      declarations(context, 6),
      'Start --> A',
      'Start ---> B',
      'Start ----> C',
      'A -..-> D',
      'B ===> D',
      'C ====> D',
      'A --> E',
      'B ---> E',
      'C ----> E',
      'D & E --> Finish',
    ),
  );
}

function regressionNestedDirections(context) {
  return regression(
    'nested-directions',
    ['nested-subgraphs', 'subgraph-direction'],
    8,
    7,
    lines(
      'flowchart LR',
      'subgraph Outer [Outer]',
      '  direction LR',
      '  subgraph Top [Top]',
      '    direction TB',
      `    A["${context.label(0)}"] --> B["${context.label(1)}"] --> C["${context.label(2)}"]`,
      '  end',
      '  subgraph Bottom [Bottom]',
      '    direction RL',
      `    D["${context.label(3)}"] --> E["${context.label(4)}"] --> F["${context.label(5)}"]`,
      '  end',
      '  C --> D',
      'end',
      'Start --> A',
      'F --> Finish',
    ),
  );
}

function regressionDisconnected(context) {
  return regression(
    'disconnected',
    ['disconnected-components', 'classic-shapes', 'metadata-shapes'],
    8,
    5,
    lines(
      'flowchart TB',
      `A1["${context.label(0)}"] --> A2["${context.label(1)}"] --> A3`,
      `B1{"${context.label(2)}?"} --> B2`,
      'B1 --> B3',
      'C1((Start)) --> C2[(Store)]',
      `D1@{ shape: doc, label: "${context.label(3)}" }`,
    ),
  );
}

function regressionRichText(context) {
  return regression(
    'rich-text',
    ['markdown-labels', 'html-labels', 'entities'],
    5,
    4,
    lines(
      'flowchart TB',
      `A["<b>${context.label(0)}</b>"] --> B["<u>${context.label(1)}</u><br/>line"]`,
      `B --> C["H<sub>2</sub>O ${context.label(2)}"]`,
      `C --> D["\`**${context.label(3)}**\`"]`,
      'D --> E["Symbols: &lt; &gt; &amp; #9829;"]',
    ),
  );
}

function regressionAnimatedEdges(context) {
  return regression(
    'animated-edges',
    ['edge-ids', 'edge-animation', 'classes'],
    4,
    3,
    lines(
      'flowchart LR',
      declarations(context, 4),
      'A fast@--> B',
      'B slow@-.-> C',
      'C thick@==> D',
      'fast@{ animation: fast }',
      'slow@{ animation: slow }',
      'thick@{ animate: true }',
      'classDef pulse stroke:#dc2626,stroke-width:3px',
      'class fast,slow,thick pulse',
    ),
  );
}

function regressionCollapsedGroup(context) {
  return {
    ...regression(
    'collapsed-group',
    ['collapsed-subgraphs', 'cycles'],
    6,
    6,
    lines(
      'flowchart LR',
      'Start --> G',
      'subgraph G [Pipeline]',
      `  A["${context.label(0)}"] --> B["${context.label(1)}"] --> C["${context.label(2)}"]`,
      'end',
      `G --> Done["${context.label(3)}"]`,
      'Done -. retry .-> G',
      'G@{ view: collapsed }',
    ),
    ),
    expectedTexts: [context.label(3)],
  };
}

function regressionDenseFanInOut(context) {
  return regression(
    'dense-fan-in-out',
    ['multi-node-links', 'dense-crossings'],
    10,
    20,
    lines(
      'flowchart TB',
      `Start(["${context.label(0)}"]) --> A & B & C & D`,
      'A --> E & F',
      'B --> E & G',
      'C --> F & H',
      'D --> G & H',
      `E & F & G & H --> Finish(["${context.label(1)}"])`,
    ),
  );
}

function regressionDecisionEndpoints(context) {
  return regression(
    'decision-endpoints',
    ['classic-shapes', 'dense-crossings', 'cycles'],
    7,
    8,
    lines(
      'flowchart RL',
      `A{"${context.label(0)}?"} --> B["${context.label(1)}"] & C["${context.label(2)}"]`,
      'B --> D',
      'C --> D',
      'D --> E{Continue?}',
      'E --> F',
      'E --> G',
      'G -.-> A',
    ),
  );
}

function regression(id, capabilities, nodeCount, edgeCount, source) {
  return { id, capabilities, nodeCount, edgeCount, source };
}

function coverageCase({
  category,
  capabilities,
  source,
  structuralProfile,
  expectedTexts,
  equivalenceGroup,
  syntaxVariant,
}) {
  const uniqueCapabilities = [...new Set(capabilities)].sort();
  return Object.freeze({
    source,
    coverage: Object.freeze({
      category,
      capabilities: Object.freeze(uniqueCapabilities),
      ...(equivalenceGroup === undefined ? {} : { equivalenceGroup }),
      ...(syntaxVariant === undefined ? {} : { syntaxVariant }),
    }),
    structuralProfile: Object.freeze(structuralProfile),
    features: Object.freeze([
      `coverage-category:${category}`,
      ...uniqueCapabilities.map((capability) => `capability:${capability}`),
      ...(equivalenceGroup === undefined
        ? []
        : [`equivalent-syntax-group:${equivalenceGroup}`]),
    ]),
    expectedTexts: Object.freeze(expectedTexts),
  });
}

function declarations(context, count) {
  return Array.from(
    { length: count },
    (_, index) =>
      `${String.fromCharCode(65 + index)}["${context.label(index)}"]`,
  ).join('\n');
}

function shapeSyntaxPair(id, open, close, shape) {
  return {
    id,
    capabilities: ['classic-shapes', 'metadata-shapes'],
    variants: (context) => [
      lines(
        'flowchart LR',
        `A${open}${context.label(0)}${close} --> B["${context.label(1)}"]`,
      ),
      lines(
        'flowchart LR',
        `A@{ shape: ${shape}, label: "${context.label(0)}" } --> ` +
          `B["${context.label(1)}"]`,
      ),
    ],
  };
}

function topologyCapability(topology) {
  return {
    'linear': 'solid-edges',
    'fan-out': 'branching',
    'fan-in': 'merging',
    'diamond': 'dense-crossings',
    'cycle': 'cycles',
    'self-loop': 'self-loops',
    'disconnected': 'disconnected-components',
    'many-to-many': 'dense-crossings',
  }[topology];
}

function lines(...values) {
  return values.flat().join('\n');
}
