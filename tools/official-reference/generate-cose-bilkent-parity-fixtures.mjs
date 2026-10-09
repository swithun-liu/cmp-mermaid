import { createRequire } from 'node:module';
import {
  mkdirSync,
  readFileSync,
  writeFileSync,
} from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const CYTOSCAPE_COSE_BILKENT_VERSION = '4.1.0';
const COSE_BASE_VERSION = '1.0.3';
const LAYOUT_BASE_VERSION = '1.0.2';

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(scriptDirectory, '../..');
const require = createRequire(import.meta.url);
const cytoscape = require('cytoscape');
const coseBase = require('cose-base');
const coseBilkent = require('cytoscape-cose-bilkent');
const CoSELayout = coseBase.CoSELayout;

const traceIterations = new Set([0, 1, 2, 3, 4, 5, 10, 20]);
let activeTrace = null;

const originalPositionNodesRadially = CoSELayout.prototype.positionNodesRadially;
CoSELayout.prototype.positionNodesRadially = function positionNodesRadiallyWithTrace(
  forest,
) {
  const result = originalPositionNodesRadially.call(this, forest);
  recordTraceSnapshot(this, 0);
  return result;
};

const originalTick = CoSELayout.prototype.tick;
CoSELayout.prototype.tick = function tickWithTrace() {
  const ended = originalTick.call(this);
  recordTraceSnapshot(this, this.totalIterations);
  return ended;
};

const fixtures = [
  {
    id: 'two-node-tree',
    anchorId: 'root',
    nodes: [
      { id: 'root', width: 80, height: 40 },
      { id: 'child', width: 60, height: 30 },
    ],
    edges: [
      { id: 'root-child', source: 'root', target: 'child' },
    ],
  },
  {
    id: 'branched-tree',
    anchorId: 'root',
    nodes: [
      { id: 'root', width: 72.1875, height: 34 },
      { id: 'branch-a', width: 69.09375, height: 44 },
      { id: 'leaf-a', width: 101.79352, height: 71.85897 },
      { id: 'branch-b', width: 64.10216, height: 64.10216 },
      { id: 'leaf-b', width: 158.33984, height: 80 },
    ],
    edges: [
      { id: 'root-branch-a', source: 'root', target: 'branch-a' },
      { id: 'branch-a-leaf-a', source: 'branch-a', target: 'leaf-a' },
      { id: 'root-branch-b', source: 'root', target: 'branch-b' },
      { id: 'branch-b-leaf-b', source: 'branch-b', target: 'leaf-b' },
    ],
  },
  {
    id: 'wide-mixed-tree',
    anchorId: 'n0',
    compareFinalCoordinates: false,
    nodes: [
      { id: 'n0', width: 174.63452, height: 174.28998 },
      { id: 'n1', width: 141, height: 34 },
      { id: 'n2', width: 116, height: 34 },
      { id: 'n3', width: 152, height: 72 },
      { id: 'n4', width: 152, height: 120 },
      { id: 'n5', width: 150, height: 78 },
      { id: 'n6', width: 150, height: 78 },
      { id: 'n7', width: 112.373535, height: 112.151855 },
      { id: 'n8', width: 132.35205, height: 132.09082 },
      { id: 'n9', width: 82.70972, height: 59.473877 },
      { id: 'n10', width: 174.99994, height: 101.28955 },
      { id: 'n11', width: 110.0105, height: 80 },
      { id: 'n12', width: 156.25, height: 80 },
      { id: 'n13', width: 150.71436, height: 94 },
      { id: 'n14', width: 180.71436, height: 94 },
      { id: 'n15', width: 127, height: 48 },
      { id: 'n16', width: 175.61084, height: 175.2644 },
      { id: 'n17', width: 175.61084, height: 175.2644 },
      { id: 'n18', width: 175.61084, height: 175.2644 },
    ],
    edges: [
      { id: 'e0-1', source: 'n0', target: 'n1' },
      { id: 'e1-2', source: 'n1', target: 'n2' },
      { id: 'e0-3', source: 'n0', target: 'n3' },
      { id: 'e3-4', source: 'n3', target: 'n4' },
      { id: 'e0-5', source: 'n0', target: 'n5' },
      { id: 'e5-6', source: 'n5', target: 'n6' },
      { id: 'e0-7', source: 'n0', target: 'n7' },
      { id: 'e7-8', source: 'n7', target: 'n8' },
      { id: 'e0-9', source: 'n0', target: 'n9' },
      { id: 'e9-10', source: 'n9', target: 'n10' },
      { id: 'e0-11', source: 'n0', target: 'n11' },
      { id: 'e11-12', source: 'n11', target: 'n12' },
      { id: 'e0-13', source: 'n0', target: 'n13' },
      { id: 'e13-14', source: 'n13', target: 'n14' },
      { id: 'e0-15', source: 'n0', target: 'n15' },
      { id: 'e15-16', source: 'n15', target: 'n16' },
      { id: 'e15-17', source: 'n15', target: 'n17' },
      { id: 'e15-18', source: 'n15', target: 'n18' },
    ],
  },
];

verifyVersion(
  resolve(
    repositoryRoot,
    'build/upstream/mermaid-src/packages/mermaid/package.json',
  ),
  'Mermaid',
  MERMAID_VERSION,
);
verifyVersion(
  resolve(scriptDirectory, 'node_modules/cytoscape-cose-bilkent/package.json'),
  'cytoscape-cose-bilkent',
  CYTOSCAPE_COSE_BILKENT_VERSION,
);
verifyVersion(
  resolve(scriptDirectory, 'node_modules/cose-base/package.json'),
  'cose-base',
  COSE_BASE_VERSION,
);
verifyVersion(
  resolve(scriptDirectory, 'node_modules/layout-base/package.json'),
  'layout-base',
  LAYOUT_BASE_VERSION,
);

cytoscape.use(coseBilkent);

const renderedFixtures = fixtures.map(renderFixture);
const outputPath = resolve(
  repositoryRoot,
  'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/mindmap/' +
    'CoseBilkentParityFixtures.kt',
);

mkdirSync(dirname(outputPath), { recursive: true });
writeFileSync(outputPath, renderKotlin(renderedFixtures));
console.log(`Generated ${outputPath}`);

function verifyVersion(packageJsonPath, packageName, expectedVersion) {
  const packageJson = JSON.parse(readFileSync(packageJsonPath, 'utf8'));
  if (packageJson.version !== expectedVersion) {
    throw new Error(
      `Expected ${packageName}@${expectedVersion}, found ${packageJson.version}`,
    );
  }
}

function renderFixture(fixture) {
  const normalizedNodes = fixture.nodes.map((node) => ({
    ...node,
    width: Math.fround(node.width),
    height: Math.fround(node.height),
  }));
  const elements = [
    ...normalizedNodes.map((node) => ({
      data: {
        id: node.id,
        width: node.width,
        height: node.height,
      },
    })),
    ...fixture.edges.map((edge) => ({
      data: edge,
    })),
  ];
  const cy = cytoscape({
    headless: true,
    styleEnabled: false,
    elements,
  });

  cy.nodes().forEach((node) => {
    node.layoutDimensions = () => ({
      w: node.data('width'),
      h: node.data('height'),
    });
  });
  const traceSnapshots = new Map();
  activeTrace = {
    anchorId: fixture.anchorId,
    nodes: normalizedNodes,
    snapshots: traceSnapshots,
  };
  try {
    cy.layout({
      name: 'cose-bilkent',
      quality: 'proof',
      styleEnabled: false,
      animate: false,
    }).run();
  } finally {
    activeTrace = null;
  }

  const anchor = cy.getElementById(fixture.anchorId).position();
  const expectedRelativeCenters = normalizedNodes.map((node) => {
    const position = cy.getElementById(node.id).position();
    const relativeCenter = {
      id: node.id,
      x: position.x - anchor.x,
      y: position.y - anchor.y,
    };
    if (!Number.isFinite(relativeCenter.x) || !Number.isFinite(relativeCenter.y)) {
      throw new Error(`Non-finite CoSE position for ${fixture.id}/${node.id}`);
    }
    return relativeCenter;
  });
  cy.destroy();

  return {
    ...fixture,
    nodes: normalizedNodes,
    expectedRelativeCenters,
    expectedTraceSnapshots: [...traceSnapshots.values()],
  };
}

function recordTraceSnapshot(layout, iteration) {
  if (activeTrace === null || !traceIterations.has(iteration)) {
    return;
  }
  const positions = layout.getPositionsData();
  const anchor = positions[activeTrace.anchorId];
  if (anchor === undefined) {
    throw new Error(`Missing CoSE trace anchor '${activeTrace.anchorId}'`);
  }
  const relativeCenters = activeTrace.nodes.map((node) => {
    const position = positions[node.id];
    if (position === undefined) {
      throw new Error(`Missing CoSE trace node '${node.id}'`);
    }
    return {
      id: node.id,
      x: position.x - anchor.x,
      y: position.y - anchor.y,
    };
  });
  activeTrace.snapshots.set(iteration, {
    iteration,
    relativeCenters,
  });
}

function renderKotlin(values) {
  const renderedFixtures = values
    .map((fixture) => renderKotlinFixture(fixture))
    .join('\n');

  return `// Generated by tools/official-reference/generate-cose-bilkent-parity-fixtures.mjs.
// Do not edit by hand.
package com.swithun.cmpmermaid.core.mindmap

import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneSize
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseEdgeInput
import com.swithun.cmpmermaid.core.mindmap.upstream.cose.CoseNodeInput

internal data class CoseBilkentParityFixture(
    val id: String,
    val anchorId: String,
    val compareFinalCoordinates: Boolean,
    val nodes: List<CoseNodeInput>,
    val edges: List<CoseEdgeInput>,
    val expectedRelativeCenters: Map<String, ScenePoint>,
    val expectedTraceSnapshots: List<CoseBilkentExpectedTraceSnapshot>,
)

internal data class CoseBilkentExpectedTraceSnapshot(
    val iteration: Int,
    val relativeCenters: Map<String, ScenePoint>,
)

internal val coseBilkentParityFixtures: List<CoseBilkentParityFixture> = listOf(
${renderedFixtures}
)
`;
}

function renderKotlinFixture(fixture) {
  const nodes = fixture.nodes
    .map(
      (node) =>
        `            CoseNodeInput(` +
        `${JSON.stringify(node.id)}, ` +
        `SceneSize(${formatFloat(node.width)}, ${formatFloat(node.height)})` +
        `),`,
    )
    .join('\n');
  const edges = fixture.edges
    .map(
      (edge) =>
        `            CoseEdgeInput(` +
        `${JSON.stringify(edge.id)}, ` +
        `${JSON.stringify(edge.source)}, ` +
        `${JSON.stringify(edge.target)}` +
        `),`,
    )
    .join('\n');
  const centers = fixture.expectedRelativeCenters
    .map(
      (center) =>
        `            ${JSON.stringify(center.id)} to ` +
        `ScenePoint(${formatFloat(center.x)}, ${formatFloat(center.y)}),`,
    )
    .join('\n');
  const traceSnapshots = fixture.expectedTraceSnapshots
    .map((snapshot) => renderKotlinTraceSnapshot(snapshot))
    .join('\n');

  return `    CoseBilkentParityFixture(
        id = ${JSON.stringify(fixture.id)},
        anchorId = ${JSON.stringify(fixture.anchorId)},
        compareFinalCoordinates = ${fixture.compareFinalCoordinates !== false},
        nodes = listOf(
${nodes}
        ),
        edges = listOf(
${edges}
        ),
        expectedRelativeCenters = linkedMapOf(
${centers}
        ),
        expectedTraceSnapshots = listOf(
${traceSnapshots}
        ),
    ),`;
}

function renderKotlinTraceSnapshot(snapshot) {
  const centers = snapshot.relativeCenters
    .map(
      (center) =>
        `                    ${JSON.stringify(center.id)} to ` +
        `ScenePoint(${formatFloat(center.x)}, ${formatFloat(center.y)}),`,
    )
    .join('\n');
  return `            CoseBilkentExpectedTraceSnapshot(
                iteration = ${snapshot.iteration},
                relativeCenters = linkedMapOf(
${centers}
                ),
            ),`;
}

function formatFloat(value) {
  const normalized = Math.abs(value) < 1e-12 ? 0 : value;
  const rendered = Number(normalized.toPrecision(12)).toString();
  return `${rendered.includes('.') ? rendered : `${rendered}.0`}f`;
}
