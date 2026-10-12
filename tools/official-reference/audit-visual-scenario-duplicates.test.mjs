import assert from 'node:assert/strict';
import test from 'node:test';
import {
  analyzeInputSpaceCoverage,
  duplicateGroups,
  manifestVisualStructureFingerprint,
} from './audit-visual-scenario-duplicates.mjs';
import {
  familyDimensionSpecs,
  scenarioBlueprintsByKind,
} from './visual-structure-corpus.mjs';

test('manifest fingerprint ignores rendered text and identifiers', () => {
  const first = manifest({
    id: 'node-a',
    text: 'Healthcare Emergency Triage',
    bounds: { x: 10, y: 20, width: 100, height: 40 },
  });
  const second = manifest({
    id: 'unrelated-id',
    text: 'Retail Cart Recovery',
    bounds: { x: 10, y: 20, width: 100, height: 40 },
  });

  assert.equal(
    manifestVisualStructureFingerprint(first),
    manifestVisualStructureFingerprint(second),
  );
});

test('manifest fingerprint retains rendered geometry and visual roles', () => {
  const baseline = manifest({
    id: 'node-a',
    text: 'Scenario',
    bounds: { x: 10, y: 20, width: 100, height: 40 },
  });
  const moved = manifest({
    id: 'node-b',
    text: 'Scenario',
    bounds: { x: 30, y: 20, width: 100, height: 40 },
  });
  const changedRole = manifest({
    id: 'node-c',
    text: 'Scenario',
    role: 'Diamond',
    bounds: { x: 10, y: 20, width: 100, height: 40 },
  });

  assert.notEqual(
    manifestVisualStructureFingerprint(baseline),
    manifestVisualStructureFingerprint(moved),
  );
  assert.notEqual(
    manifestVisualStructureFingerprint(baseline),
    manifestVisualStructureFingerprint(changedRole),
  );
});

test('duplicate groups list every case sharing the same evidence value', () => {
  const entries = [
    { id: 'case-1', value: 'a' },
    { id: 'case-2', value: 'b' },
    { id: 'case-3', value: 'a' },
  ];

  assert.deepEqual(
    duplicateGroups(entries, (entry) => entry.value),
    [{ value: 'a', ids: ['case-1', 'case-3'] }],
  );
});

test('input-space coverage proves the full balanced flowchart matrix', () => {
  const report = analyzeInputSpaceCoverage(
    scenarioBlueprintsByKind.flowchart,
    familyDimensionSpecs.flowchart,
  );

  assert.equal(report.status, 'pass');
  assert.deepEqual(report.failures, []);
  assert.deepEqual(report.counts, {
    cases: 256,
    namedDimensions: 8,
    dimensionPairs: 28,
    uniqueDimensionVectors: 256,
    uniqueTopologyAndDataVectors: 32,
    uniqueStructuralProfiles: 256,
  });
  assert.ok(
    report.dimensions.every((dimension) =>
      dimension.status === 'pass' &&
      Object.values(dimension.optionCounts).every((count) => count === 128)
    ),
  );
  assert.ok(
    report.dimensionPairs.every((pair) =>
      pair.status === 'pass' &&
      pair.combinations.every((combination) => combination.count === 64)
    ),
  );
  assert.ok(
    report.topologyAndDataEffects.every((effect) =>
      effect.status === 'pass' &&
      effect.comparisonCount === 128 &&
      effect.unchangedPairs.length === 0
    ),
  );
  assert.deepEqual(report.repeatedDimensionVectors, []);
  assert.deepEqual(report.nearIsomorphicStructuralProfiles, []);
});

test('input-space coverage rejects duplicated dimension vectors', () => {
  const entries = scenarioBlueprintsByKind.flowchart.map((entry, index) =>
    index === 1
      ? {
          ...entry,
          inputDimensions:
            scenarioBlueprintsByKind.flowchart[0].inputDimensions,
        }
      : entry
  );
  const report = analyzeInputSpaceCoverage(
    entries,
    familyDimensionSpecs.flowchart,
  );

  assert.equal(report.status, 'fail');
  assert.equal(report.counts.uniqueDimensionVectors, 255);
  assert.ok(report.repeatedDimensionVectors.length > 0);
  assert.ok(
    report.failures.some((failure) =>
      failure.includes('full-factorial dimension vectors')
    ),
  );
});

function manifest({
  id,
  text,
  role = 'Rectangle',
  bounds,
}) {
  return {
    schemaVersion: 1,
    renderer: 'native',
    viewport: {
      x: 0,
      y: 0,
      width: 200,
      height: 100,
    },
    title: text,
    accessibilityTitle: text,
    accessibilityDescription: text,
    elements: [
      {
        order: 0,
        zIndex: 1,
        type: 'shape',
        role,
        id,
        bounds,
        fill: { r: 255, g: 255, b: 255, a: 255 },
        stroke: { r: 0, g: 0, b: 0, a: 255 },
        strokeWidth: 1,
      },
      {
        order: 1,
        zIndex: 2,
        type: 'text',
        role: 'text',
        id: `${id}-label`,
        text,
        bounds,
        fill: { r: 0, g: 0, b: 0, a: 255 },
      },
    ],
  };
}
