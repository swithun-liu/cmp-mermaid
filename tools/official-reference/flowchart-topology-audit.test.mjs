import assert from 'node:assert/strict';
import test from 'node:test';
import {
  analyzeFlowchartTopologyDiversity,
  flowchartTopologyFingerprint,
  summarizeFlowchartTopology,
} from './flowchart-topology-audit.mjs';

test('topology fingerprint ignores node identifiers', () => {
  const first = topology(
    ['A', 'B', 'C', 'D'],
    [['A', 'B'], ['A', 'C'], ['B', 'D'], ['C', 'D']],
  );
  const renamed = topology(
    ['finish', 'right', 'start', 'left'],
    [
      ['start', 'left'],
      ['start', 'right'],
      ['left', 'finish'],
      ['right', 'finish'],
    ],
  );

  assert.equal(
    flowchartTopologyFingerprint(first),
    flowchartTopologyFingerprint(renamed),
  );
});

test('topology fingerprint distinguishes a branch from a linear chain', () => {
  const chain = topology(
    ['A', 'B', 'C', 'D'],
    [['A', 'B'], ['B', 'C'], ['C', 'D']],
  );
  const branch = topology(
    ['A', 'B', 'C', 'D'],
    [['A', 'B'], ['A', 'C'], ['A', 'D']],
  );

  assert.notEqual(
    flowchartTopologyFingerprint(chain),
    flowchartTopologyFingerprint(branch),
  );
});

test('topology fingerprint retains subgraph containment', () => {
  const plain = topology(['A', 'B'], [['A', 'B']]);
  const compound = {
    ...plain,
    subgraphs: [{ id: 'Group', nodes: ['A', 'B'] }],
  };

  assert.notEqual(
    flowchartTopologyFingerprint(plain),
    flowchartTopologyFingerprint(compound),
  );
});

test('topology summary identifies structural graph classes', () => {
  const summary = summarizeFlowchartTopology({
    id: 'mixed',
    vertices: ['A', 'B', 'C', 'D', 'E'],
    edges: [
      { start: 'A', end: 'B' },
      { start: 'A', end: 'C' },
      { start: 'B', end: 'D' },
      { start: 'C', end: 'D' },
      { start: 'D', end: 'A' },
      { start: 'E', end: 'E' },
      { start: 'E', end: 'E' },
    ],
    subgraphs: [{ id: 'Group', nodes: ['A', 'B'] }],
  });

  assert.deepEqual(summary.classes, [
    'branching',
    'merging',
    'cyclic',
    'disconnected',
    'compound',
    'parallel-edge',
    'self-loop',
  ]);
  assert.equal(summary.weakComponentCount, 2);
});

test('diversity audit rejects an all-chain corpus', () => {
  const entries = [
    { id: 'one', coverageCategory: 'atoms' },
    { id: 'two', coverageCategory: 'atoms' },
  ];
  const parsed = [
    { id: 'one', ...topology(['A', 'B', 'C'], [['A', 'B'], ['B', 'C']]) },
    { id: 'two', ...topology(['X', 'Y', 'Z'], [['X', 'Y'], ['Y', 'Z']]) },
  ];
  const report = analyzeFlowchartTopologyDiversity(entries, parsed, {
    minimumUniqueTopologies: 2,
    minimumClassCounts: {
      branching: 1,
      'linear-chain': 1,
    },
    minimumUniqueTopologiesByCategory: {
      atoms: 2,
    },
  });

  assert.equal(report.status, 'fail');
  assert.equal(report.counts.uniqueTopologies, 1);
  assert.ok(report.failures.some((failure) => failure.includes('branching')));
  assert.ok(
    report.failures.some((failure) => failure.includes('unique parser topologies')),
  );
});

function topology(vertices, edges) {
  return {
    vertices,
    edges: edges.map(([start, end]) => ({ start, end })),
    subgraphs: [],
  };
}
