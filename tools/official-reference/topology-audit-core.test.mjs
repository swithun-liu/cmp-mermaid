import assert from 'node:assert/strict';
import test from 'node:test';
import { compareTopologyManifests } from './topology-audit-core.mjs';

test('passes matching per-edge topology and pair relationships', () => {
  const manifest = manifestWithEdges([
    edge('L_A_B_0', [[0, 0], [20, 0], [20, 20]], ['R', 'D']),
    edge('L_C_D_0', [[10, -10], [10, 10]], ['D']),
  ]);

  const result = compareTopologyManifests(manifest, structuredClone(manifest));

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.findings, []);
  assert.deepEqual(
    result.nativeRelationships.strictCrossings,
    ['L_A_B_0|L_C_D_0'],
  );
});

test('passes a matching shape-clipped diagonal segment', () => {
  const manifest = manifestWithEdges([
    edge('L_A_B_0', [[0, 0], [10, 10]], ['Diagonal']),
  ]);

  const result = compareTopologyManifests(manifest, structuredClone(manifest));

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.findings, []);
});

test('visual_swimlanes_011 rejects a wrong N3 to N4 route', () => {
  const official = manifestWithEdges([
    edge('L_N1_N2_0', [[0, 0], [20, 0]], ['R']),
    edge('L_N2_N3_0', [[20, 0], [20, 20]], ['D']),
    edge('L_N3_N4_0', [[20, 20], [40, 20]], ['R']),
    edge('L_N3_N4_2', [[20, 24], [40, 24]], ['R']),
  ]);
  const native = manifestWithEdges([
    edge('L_N1_N2_0', [[0, 0], [20, 0]], ['R']),
    edge('L_N2_N3_0', [[20, 0], [20, 20]], ['D']),
    edge('L_N3_N4_0', [[20, 20], [20, 30], [40, 30]], ['D', 'R']),
    edge('L_N3_N4_2', [[20, 24], [40, 24]], ['R']),
  ]);

  const result = compareTopologyManifests(native, official);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some(
      (finding) =>
        finding.code === 'bend-count-mismatch' &&
        finding.message.includes('L_N3_N4_0'),
    ),
  );
  assert.ok(
    result.findings.some(
      (finding) =>
        finding.code === 'direction-sequence-mismatch' &&
        finding.message.includes('L_N3_N4_0'),
    ),
  );
});

test('rejects shared-track and crossing relationship mismatches', () => {
  const official = manifestWithEdges([
    edge('L_A_B_0', [[0, 0], [20, 0]], ['R']),
    edge('L_C_D_0', [[10, -10], [10, 10]], ['D']),
  ]);
  const native = manifestWithEdges([
    edge('L_A_B_0', [[0, 0], [20, 0]], ['R']),
    edge('L_C_D_0', [[30, -10], [30, 10]], ['D']),
  ]);

  const result = compareTopologyManifests(native, official);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some(
      (finding) => finding.code === 'strict-crossing-mismatch',
    ),
  );
});

function manifestWithEdges(edges) {
  return {
    schemaVersion: 1,
    renderer: 'test',
    elements: edges,
  };
}

function edge(
  edgeId,
  coordinates,
  directionSequence,
) {
  const topologyPoints = coordinates.map(([x, y]) => ({ x, y }));
  return {
    type: 'path',
    edgeId,
    topologyPoints,
    bendPoints: topologyPoints.slice(1, -1),
    directionSequence,
    sourcePortSide: sourceSide(directionSequence[0]),
    destinationPortSide: destinationSide(directionSequence.at(-1)),
  };
}

function sourceSide(direction) {
  return { R: 'Right', L: 'Left', D: 'Bottom', U: 'Top' }[direction] ?? null;
}

function destinationSide(direction) {
  return { R: 'Left', L: 'Right', D: 'Top', U: 'Bottom' }[direction] ?? null;
}
