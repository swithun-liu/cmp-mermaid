import fs from 'node:fs';
import cytoscape from 'cytoscape';
import fcose from 'cytoscape-fcose';

cytoscape.use(fcose);

const input = JSON.parse(fs.readFileSync(0, 'utf8'));
const cy = cytoscape({
  headless: true,
  styleEnabled: true,
  style: [
    {
      selector: 'node',
      style: {
        'compound-sizing-wrt-labels': 'include',
      },
    },
    {
      selector: 'node[label]',
      style: {
        'text-valign': 'bottom',
        'text-halign': 'center',
        'font-size': `${input.fontSize ?? 16}px`,
      },
    },
    {
      selector: '.leaf',
      style: {
        label: 'data(label)',
        width: 'data(width)',
        height: 'data(height)',
      },
    },
    {
      selector: '.group',
      style: {
        padding: 'data(padding)',
      },
    },
  ],
  layout: {
    name: 'grid',
    boundingBox: { x1: 0, x2: 100, y1: 0, y2: 100 },
  },
});

for (const node of input.nodes.filter((item) => item.kind === 'group')) {
  cy.add({ group: 'nodes', data: node, classes: 'group' });
}
for (const node of input.nodes.filter((item) => item.kind !== 'group')) {
  cy.add({ group: 'nodes', data: node, classes: 'leaf' });
}
for (const edge of input.edges) {
  cy.add({ group: 'edges', data: edge });
}

const parentById = new Map(input.nodes.map((node) => [node.id, node.parent]));
const options = {
  name: 'fcose',
  quality: 'proof',
  randomize: false,
  nodeSeparation: input.nodeSeparation ?? 75,
  numIter: input.numIter ?? 2500,
  styleEnabled: false,
  animate: false,
  nodeDimensionsIncludeLabels: false,
  idealEdgeLength(edge) {
    return parentById.get(edge.source().id()) === parentById.get(edge.target().id())
      ? input.sameGroupIdealLength
      : input.crossGroupIdealLength;
  },
  edgeElasticity(edge) {
    return parentById.get(edge.source().id()) === parentById.get(edge.target().id())
      ? input.sameGroupElasticity
      : 0.001;
  },
  alignmentConstraint: input.alignmentConstraint,
  relativePlacementConstraint: input.relativePlacementConstraint,
};

function withSeededRandom(seed, fn) {
  if (seed === 0) return fn();
  const original = Math.random;
  let state = seed >>> 0;
  Math.random = () => {
    state = (state + 0x6d2b79f5) >>> 0;
    let value = state;
    value = Math.imul(value ^ (value >>> 15), value | 1);
    value ^= value + Math.imul(value ^ (value >>> 7), value | 61);
    return ((value ^ (value >>> 14)) >>> 0) / 4294967296;
  };
  try {
    return fn();
  } finally {
    Math.random = original;
  }
}

const layout = cy.layout(options);
const snapshots = [];

function snapshot(stage) {
  const nodes = {};
  for (const node of cy.nodes()) {
    const position = node.position();
    const bounds = node.boundingBox();
    const dimensions = node.layoutDimensions({
      nodeDimensionsIncludeLabels: false,
    });
    nodes[node.id()] = {
      x: position.x,
      y: position.y,
      left: bounds.x1,
      top: bounds.y1,
      width: bounds.w,
      height: bounds.h,
      layoutWidth: dimensions.w,
      layoutHeight: dimensions.h,
      outerWidth: node.outerWidth(),
      outerHeight: node.outerHeight(),
      padding: node.padding(),
    };
  }
  snapshots.push({ stage, nodes });
}

if (input.includeRuns) {
  snapshot('initial');
  layout.on('layoutstop', () => {
    snapshot(`run-${snapshots.length}`);
  });
}
layout.one('layoutstop', () => {
  withSeededRandom(input.seed ?? 1, () => layout.run());
});
withSeededRandom(input.seed ?? 1, () => layout.run());

const result = {};
for (const node of cy.nodes()) {
  const position = node.position();
  const bounds = node.boundingBox();
  result[node.id()] = {
    x: position.x,
    y: position.y,
    left: bounds.x1,
    top: bounds.y1,
    width: bounds.w,
    height: bounds.h,
  };
}
process.stdout.write(
  `${JSON.stringify(input.includeRuns ? { result, snapshots } : result, null, 2)}\n`,
);
cy.destroy();
