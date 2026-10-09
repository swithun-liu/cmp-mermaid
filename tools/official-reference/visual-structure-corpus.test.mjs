import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';
import {
  cases,
  casesPerKind,
  corpusSummary,
  kinds,
  structuralSeedsByKind,
} from './visual-structure-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');

test('provides 256 structurally distinct cases for every diagram family', () => {
  assert.equal(kinds.length, 33);
  assert.equal(casesPerKind, 256);
  assert.equal(cases.length, 8_448);

  for (const kind of kinds) {
    const familyCases = cases.filter((entry) => entry.kind === kind);
    assert.equal(familyCases.length, casesPerKind, kind);
    assert.equal(
      new Set(familyCases.map((entry) => entry.structuralSignature)).size,
      casesPerKind,
      `${kind} structural signatures`,
    );
    assert.equal(
      new Set(familyCases.map((entry) => entry.source)).size,
      casesPerKind,
      `${kind} sources`,
    );
    assert.deepEqual(
      new Set(familyCases.map((entry) => entry.structuralScenarioId)),
      new Set(structuralSeedsByKind[kind].map((entry) => entry.id)),
      `${kind} curated seeds`,
    );
  }
});

test('derives uniqueness from structural dimensions rather than labels', () => {
  for (const entry of cases) {
    const expectedSignature = sha256(JSON.stringify({
      kind: entry.kind,
      ...entry.structuralDimensions,
    }));
    assert.equal(entry.structuralSignature, expectedSignature, entry.id);
    assert.equal(entry.sourceSha256, sha256(entry.source), entry.id);
    assert.ok(entry.structuralDimensions.seedScenarioId.length > 0, entry.id);
    assert.ok(Array.isArray(entry.structuralDimensions.seedFeatureSet), entry.id);
    assert.ok(entry.structuralDimensions.itemCount > 0, entry.id);
    assert.ok(entry.structuralDimensions.variant.length > 0, entry.id);
    assert.ok(
      entry.expectedTexts.every((expectedText) =>
        entry.source.includes(expectedText)
      ),
      entry.id,
    );
    assert.ok(
      !Object.hasOwn(entry.structuralDimensions, 'ordinal') &&
        !Object.hasOwn(entry.structuralDimensions, 'label'),
      entry.id,
    );
  }
});

test('keeps generated structures away from platform-sensitive layout boundaries', () => {
  const architecture = cases.find(
    (entry) => entry.id === 'visual_architecture_069',
  );
  assert.ok(architecture);
  assert.doesNotMatch(architecture.source, /structure_group_/);
  assert.match(
    architecture.source,
    /database:R <-- L:structure_service69_1/,
  );
  assert.doesNotMatch(
    architecture.source,
    /(?:structure_service69_\d+:[BT]|[BT]:structure_service69_\d+)/,
  );

  const sequence = cases.find((entry) => entry.id === 'visual_sequence_039');
  assert.ok(sequence);
  assert.match(sequence.source, /^\s*and ack$/m);
  assert.doesNotMatch(sequence.source, /^\s*and confirmation$/m);

  const requirement = cases.find(
    (entry) => entry.id === 'visual_requirement_002',
  );
  assert.ok(requirement);
  assert.match(
    requirement.source,
    /alarm_contract - traces -> structure_requirement2_1/,
  );

  const treemap = cases.find((entry) => entry.id === 'visual_treemap_256');
  assert.ok(treemap);
  assert.match(treemap.source, /^\s*"G1"$/m);
  assert.doesNotMatch(treemap.source, /^\s*"Group 1"$/m);
  assert.doesNotMatch(treemap.source, /:\s+-?\d+\.\d{3}\s*$/m);

  const pairedTreemap = cases.find(
    (entry) => entry.id === 'visual_treemap_207',
  );
  assert.ok(pairedTreemap);
  assert.match(pairedTreemap.source, /^\s*"A":\s+/m);
  assert.match(pairedTreemap.source, /^\s*"B":\s+/m);
  assert.doesNotMatch(pairedTreemap.source, /^\s*"Secondary":\s+/m);
});

test('publishes machine-readable structural corpus accounting', () => {
  const metadata = JSON.parse(readFileSync(
    resolve(
      repositoryRoot,
      'docs/assets/stability-report/visual-structure-corpus-metadata.json',
    ),
    'utf8',
  ));
  assert.equal(metadata.role, 'structural-and-syntax-visual-coverage');
  assert.equal(metadata.diagramFamilyCount, 33);
  assert.equal(metadata.caseCount, 8_448);
  assert.equal(metadata.casesPerDiagramFamily, 256);
  assert.deepEqual(metadata.diagramFamilies, corpusSummary.countsByKind);
  assert.ok(metadata.uniquenessBasis.includes('family-specific-structural-variant'));
});

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
