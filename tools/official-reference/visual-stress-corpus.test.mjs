import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';
import { cases as productionCases } from './production-corpus.mjs';
import {
  corpusSummary,
  samples,
  stressSamplesPerKind,
} from './visual-parity-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');

test('canonicalizes production sources to the exact runtime form', () => {
  assert.ok(productionCases.every((entry) => entry.source === entry.source.trim()));
});

test('separates structural scenarios from text and layout stress samples', () => {
  assert.equal(productionCases.length, 441);
  assert.equal(corpusSummary.structuralScenarioCount, 441);
  assert.equal(corpusSummary.stressSampleCount, 8_448);
  assert.equal(stressSamplesPerKind, 256);
  assert.equal(samples.length, 8_448);
  assert.equal(
    new Set(samples.map((entry) => entry.structuralScenarioId)).size,
    441,
  );
  assert.ok(samples.every((entry) =>
    entry.coverageRole === 'text-layout-stress'
  ));

  for (const family of corpusSummary.countsByKind) {
    const familySamples = samples.filter((entry) => entry.kind === family.kind);
    const expectedStructuralCount = productionCases
      .filter((entry) => entry.kind === family.kind)
      .length;
    assert.equal(family.stressSampleCount, 256);
    assert.equal(familySamples.length, 256);
    assert.equal(family.structuralScenarioCount, expectedStructuralCount);
  }
});

test('publishes machine-readable corpus accounting', () => {
  const metadata = readJson(
    'docs/assets/stability-report/visual-stress-corpus-metadata.json',
  );
  assert.equal(metadata.structuralCoverage.scenarioCount, 441);
  assert.equal(metadata.stressCoverage.sampleCount, 8_448);
  assert.equal(metadata.stressCoverage.addsStructuralCoverage, false);
  assert.deepEqual(metadata.diagramFamilies, corpusSummary.countsByKind);
});

test('public reports keep structural cases and derived stress inputs separate', () => {
  const reports = [
    'README.md',
    'README.zh-CN.md',
    'docs/stability-report.md',
    'docs/assets/stability-report/visual-parity-evidence.md',
  ].map((path) => readText(path));
  const combined = reports.join('\n');

  assert.match(combined, /8,448 independent-scenario visual cases/);
  assert.match(
    combined,
    /all 256 combinations of eight capability axes/,
  );
  assert.match(combined, /family-specific capability axes/);
  assert.match(combined, /paired source effects/);
  assert.match(combined, /256\/256 label-normalized sources/);
  assert.match(combined, /8,448[^\n]{0,48}text\/layout stress inputs/);
  assert.match(combined, /do not add structural or syntax coverage/);
  assert.match(combined, /不计作结构覆盖/);
});

function readJson(path) {
  return JSON.parse(readText(path));
}

function readText(path) {
  return readFileSync(resolve(repositoryRoot, path), 'utf8');
}
