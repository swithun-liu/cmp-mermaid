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
  normalizeScenarioSource,
  scenarioBlueprintsByKind,
} from './visual-structure-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');

test('provides 256 independent scenarios for every diagram family', () => {
  assert.equal(kinds.length, 33);
  assert.equal(casesPerKind, 256);
  assert.equal(cases.length, 8_448);

  for (const kind of kinds) {
    const familyCases = scenarioBlueprintsByKind[kind];
    assert.equal(familyCases.length, casesPerKind, kind);
    assert.equal(
      new Set(familyCases.map((entry) => entry.scenarioBlueprintId)).size,
      casesPerKind,
      `${kind} blueprint ids`,
    );
    assert.equal(
      new Set(
        familyCases.map((entry) => entry.normalizedSemanticSignature),
      ).size,
      casesPerKind,
      `${kind} normalized semantic structures`,
    );
    assert.equal(
      new Set(familyCases.map((entry) => entry.source)).size,
      casesPerKind,
      `${kind} sources`,
    );
  }
});

test('does not reuse seed scenarios, profiles, labels, or ordinals as uniqueness', () => {
  for (const entry of cases) {
    assert.equal(entry.coverageRole, 'independent-visual-scenario', entry.id);
    assert.ok(!Object.hasOwn(entry, 'structuralScenarioId'), entry.id);
    assert.ok(!Object.hasOwn(entry, 'structuralProfileId'), entry.id);
    assert.ok(!Object.hasOwn(entry, 'seedId'), entry.id);
    assert.ok(!Object.hasOwn(entry, 'originScenarioId'), entry.id);

    assert.deepEqual(
      Object.keys(entry.normalizedSemanticStructure).sort(),
      ['decisions', 'grammar', 'normalizedSource'],
      entry.id,
    );
    assert.equal(entry.normalizedSemanticStructure.decisions.length, 8, entry.id);
    assert.equal(
      entry.normalizedSemanticStructure.normalizedSource,
      normalizeScenarioSource(entry),
      entry.id,
    );
    assert.equal(
      entry.normalizedSemanticSignature,
      sha256(JSON.stringify(entry.normalizedSemanticStructure)),
      entry.id,
    );
    assert.equal(entry.structuralSignature, entry.normalizedSemanticSignature);
    assert.equal(entry.sourceSha256, sha256(entry.source), entry.id);
    assert.ok(
      entry.expectedTexts.every((expectedText) =>
        entry.source.includes(expectedText)
      ),
      entry.id,
    );
  }
});

test('covers 256 named business scenarios without repeating a blueprint', () => {
  for (const kind of kinds) {
    const familyCases = scenarioBlueprintsByKind[kind];
    const domainCounts = new Map();
    const scenarioCodes = new Set();
    const titles = new Set();
    for (const entry of familyCases) {
      const [blueprintKind, scenarioCode] = entry.scenarioBlueprintId.split('/');
      assert.equal(blueprintKind, kind, entry.id);
      assert.ok(scenarioCode, entry.id);
      const domainFeature = entry.features.find((feature) =>
        feature.startsWith('scenario-domain:')
      );
      assert.ok(domainFeature, entry.id);
      const domain = domainFeature.slice('scenario-domain:'.length);
      domainCounts.set(domain, (domainCounts.get(domain) ?? 0) + 1);
      scenarioCodes.add(scenarioCode);
      titles.add(entry.title);
      assert.doesNotMatch(entry.title, /\bcase\s+\d+\b/i, entry.id);
    }
    assert.equal(domainCounts.size, 16, `${kind} domains`);
    assert.equal(scenarioCodes.size, 256, `${kind} scenario codes`);
    assert.equal(titles.size, 256, `${kind} titles`);
    assert.deepEqual(new Set(domainCounts.values()), new Set([16]), kind);
  }
});

test('keeps representative family decisions in the rendered Mermaid source', () => {
  assert.match(caseSource('flowchart', 1), /N1\[Patient Case Register]/);
  assert.match(caseSource('flowchart', 2), /N1\{Patient Case Refer\?\}/);

  assert.match(caseSource('architecture', 1), /s1\(server\)/);
  assert.match(caseSource('architecture', 2), /s1\(database\)/);

  assert.match(caseSource('sequence', 1), /User->>Platform:/);
  assert.match(caseSource('sequence', 2), /Platform-->>Worker:/);

  assert.match(caseSource('pie', 1), /"Patient Case Register" : 4/);
  assert.match(caseSource('pie', 2), /"Patient Case Refer" : 7/);

  assert.match(caseSource('gantt', 1), /:crit, task_1, 2027-01-01, 2d/);
  assert.match(caseSource('gantt', 2), /:active, task_1, 2027-01-01, 2d/);
  assert.match(caseSource('gantt', 256), /^\s*todayMarker off$/m);
  assert.doesNotMatch(caseSource('gantt', 256), /:milestone,/);

  assert.match(caseSource('treemap', 1), /^"Main"$/m);
  assert.match(caseSource('treemap', 2), /^"Alt"$/m);
  assert.match(caseSource('treemap', 1), /"Register 1": 40/);

  assert.doesNotMatch(caseSource('venn', 1), /^\s*union A,B/m);
  assert.match(caseSource('venn', 2), /^\s*union A,B/m);

  assert.match(caseSource('packet', 1), /^\s*0-3:/m);
  assert.match(caseSource('packet', 2), /^\s*0-7:/m);

  assert.doesNotMatch(
    caseSource('cynefin', 1),
    /^\s*complex --> complicated/m,
  );
  assert.match(
    caseSource('cynefin', 2),
    /^\s*complex --> complicated/m,
  );
});

test('publishes machine-readable independent scenario accounting', () => {
  const metadata = JSON.parse(readFileSync(
    resolve(
      repositoryRoot,
      'docs/assets/stability-report/visual-structure-corpus-metadata.json',
    ),
    'utf8',
  ));
  assert.equal(metadata.role, 'independent-scenario-visual-coverage');
  assert.equal(metadata.diagramFamilyCount, 33);
  assert.equal(metadata.caseCount, 8_448);
  assert.equal(metadata.casesPerDiagramFamily, 256);
  assert.deepEqual(metadata.diagramFamilies, corpusSummary.countsByKind);
  assert.deepEqual(metadata.uniquenessBasis, [
    'unique-scenario-blueprint-id',
    'explicit-256-entry-business-scenario-catalog',
    'label-stripped-normalized-mermaid-source',
    'eight-rendered-semantic-decisions',
  ]);
});

function caseSource(kind, ordinal) {
  const entry = cases.find(
    (candidate) =>
      candidate.id === `visual_${kind}_${String(ordinal).padStart(3, '0')}`,
  );
  assert.ok(entry, `${kind} ${ordinal}`);
  return entry.source;
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
