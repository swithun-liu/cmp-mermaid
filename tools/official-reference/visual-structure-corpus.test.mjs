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
  familyCoverageContracts,
  familyDimensionSpecs,
  flowchartCoverageContract,
  kinds,
  normalizeScenarioSource,
  scenarioBlueprintsByKind,
} from './visual-structure-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const fullFactorialKinds = kinds.filter(
  (kind) => familyCoverageContracts[kind].mode === 'full-factorial',
);

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

test('defines eight named orthogonal dimensions for full-factorial families', () => {
  assert.equal(fullFactorialKinds.length, 32);
  for (const kind of fullFactorialKinds) {
    const dimensions = familyDimensionSpecs[kind];
    assert.equal(dimensions.length, 8, `${kind} dimension count`);
    assert.equal(
      new Set(dimensions.map((dimension) => dimension.name)).size,
      8,
      `${kind} dimension names`,
    );
    assert.ok(
      new Set(dimensions.map((dimension) => dimension.category)).size >= 3,
      `${kind} dimension categories`,
    );
    assert.ok(
      dimensions.filter((dimension) =>
        dimension.category === 'topology' ||
        dimension.category === 'data'
      ).length >= 3,
      `${kind} topology/data dimensions`,
    );
    for (const dimension of dimensions) {
      assert.match(dimension.name, /^[a-z][a-z0-9-]+$/, `${kind} dimension`);
      assert.equal(dimension.options.length, 2, `${kind}/${dimension.name}`);
      assert.equal(
        new Set(dimension.options).size,
        2,
        `${kind}/${dimension.name} options`,
      );
    }
  }
});

test('covers every dimension and every dimension pair with a balanced matrix', () => {
  for (const kind of fullFactorialKinds) {
    const familyCases = scenarioBlueprintsByKind[kind];
    const dimensions = familyDimensionSpecs[kind];
    for (const dimension of dimensions) {
      const counts = new Map(dimension.options.map((option) => [option, 0]));
      for (const entry of familyCases) {
        const selection = entry.inputDimensions.find(
          (candidate) => candidate.name === dimension.name,
        );
        assert.ok(selection, `${entry.id}/${dimension.name}`);
        counts.set(selection.option, (counts.get(selection.option) ?? 0) + 1);
      }
      assert.deepEqual(
        [...counts.values()],
        [128, 128],
        `${kind}/${dimension.name}`,
      );
    }
    for (let left = 0; left < dimensions.length; left += 1) {
      for (let right = left + 1; right < dimensions.length; right += 1) {
        const combinations = new Map();
        for (const entry of familyCases) {
          const leftOption = entry.inputDimensions[left].option;
          const rightOption = entry.inputDimensions[right].option;
          const key = `${leftOption}|${rightOption}`;
          combinations.set(key, (combinations.get(key) ?? 0) + 1);
        }
        assert.equal(combinations.size, 4, `${kind}/${left}/${right} variants`);
        assert.deepEqual(
          new Set(combinations.values()),
          new Set([64]),
          `${kind}/${left}/${right} balance`,
        );
      }
    }
  }
});

test('every named dimension changes the label-normalized Mermaid input', () => {
  for (const kind of fullFactorialKinds) {
    const familyCases = scenarioBlueprintsByKind[kind];
    for (let dimensionIndex = 0; dimensionIndex < 8; dimensionIndex += 1) {
      const mask = 1 << dimensionIndex;
      for (let index = 0; index < casesPerKind; index += 1) {
        if ((index & mask) !== 0) {
          continue;
        }
        assert.notEqual(
          normalizeScenarioSource(familyCases[index]),
          normalizeScenarioSource(familyCases[index ^ mask]),
          `${kind}/${familyDimensionSpecs[kind][dimensionIndex].name}/${index}`,
        );
      }
    }
  }
});

test('every topology and data dimension changes the structural profile', () => {
  for (const kind of fullFactorialKinds) {
    const familyCases = scenarioBlueprintsByKind[kind];
    for (let dimensionIndex = 0; dimensionIndex < 8; dimensionIndex += 1) {
      const dimension = familyDimensionSpecs[kind][dimensionIndex];
      if (dimension.category !== 'topology' && dimension.category !== 'data') {
        continue;
      }
      const mask = 1 << dimensionIndex;
      for (let index = 0; index < casesPerKind; index += 1) {
        if ((index & mask) !== 0) {
          continue;
        }
        assert.notDeepEqual(
          familyCases[index].normalizedSemanticStructure.structuralProfile,
          familyCases[index ^ mask].normalizedSemanticStructure.structuralProfile,
          `${kind}/${dimension.name}/${index}`,
        );
      }
    }
    assert.ok(
      new Set(familyCases.map((entry) =>
        JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
      )).size >= 192,
      `${kind} structural profile diversity`,
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

    const expectedStructureKeys = entry.kind === 'flowchart'
      ? ['coverage', 'grammar', 'normalizedSource', 'structuralProfile']
      : ['dimensions', 'grammar', 'normalizedSource', 'structuralProfile'];
    assert.deepEqual(
      Object.keys(entry.normalizedSemanticStructure).sort(),
      expectedStructureKeys,
      entry.id,
    );
    if (entry.kind === 'flowchart') {
      assert.ok(!Object.hasOwn(entry, 'inputDimensions'), entry.id);
      assert.equal(
        entry.normalizedSemanticStructure.coverage.category,
        entry.coverageCategory,
        entry.id,
      );
      assert.deepEqual(
        entry.normalizedSemanticStructure.coverage.capabilities,
        entry.coverageCapabilities,
        entry.id,
      );
    } else {
      assert.equal(entry.inputDimensions.length, 8, entry.id);
      assert.deepEqual(
        entry.normalizedSemanticStructure.dimensions,
        entry.inputDimensions,
        entry.id,
      );
    }
    assert.ok(
      Object.keys(entry.normalizedSemanticStructure.structuralProfile).length > 0,
      entry.id,
    );
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

test('flowchart uses the fixed six-category coverage-driven contract', () => {
  const familyCases = scenarioBlueprintsByKind.flowchart;
  assert.equal(flowchartCoverageContract.mode, 'coverage-driven');
  assert.equal(flowchartCoverageContract.caseCount, 256);
  assert.deepEqual(
    categoryCounts(familyCases),
    Object.fromEntries(
      flowchartCoverageContract.categories.map((entry) => [
        entry.id,
        entry.quota,
      ]),
    ),
  );
  assert.equal(
    new Set(familyCases.map((entry) => normalizeScenarioSource(entry))).size,
    256,
  );
  assert.equal(
    new Set(familyCases.map((entry) =>
      JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
    )).size,
    240,
  );
  const capabilities = new Set(
    familyCases.flatMap((entry) => entry.coverageCapabilities),
  );
  assert.deepEqual(
    flowchartCoverageContract.requiredCapabilities.filter(
      (capability) => !capabilities.has(capability),
    ),
    [],
  );

  const equivalentCases = familyCases.filter(
    (entry) => entry.coverageCategory === 'equivalent-syntax',
  );
  const equivalentGroups = groupBy(
    equivalentCases,
    (entry) => entry.equivalenceGroup,
  );
  assert.equal(equivalentGroups.size, 16);
  for (const [group, entries] of equivalentGroups) {
    assert.equal(entries.length, 2, group);
    assert.deepEqual(
      new Set(entries.map((entry) => entry.syntaxVariant)),
      new Set(['first', 'second']),
      group,
    );
    assert.equal(
      new Set(entries.map((entry) =>
        JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
      )).size,
      1,
      group,
    );
  }
});

test('keeps representative family decisions in the rendered Mermaid source', () => {
  assert.match(caseSource('flowchart', 1), /shape: rect/);
  assert.match(caseSource('flowchart', 47), /^flowchart TB$/m);
  assert.match(caseSource('flowchart', 48), /^flowchart TD$/m);

  assert.match(caseSource('architecture', 1), /s1\(server\)/);
  assert.match(caseSource('architecture', 2), /group platform\(cloud\)/);

  assert.match(caseSource('sequence', 1), /User->>Platform:/);
  assert.match(caseSource('sequence', 2), /^  autonumber$/m);

  assert.match(caseSource('pie', 1), /^pie$/m);
  assert.match(caseSource('pie', 2), /^pie showData$/m);

  assert.match(caseSource('gantt', 1), /:crit, task_1, 2027-01-01, 2d/);
  assert.match(caseSource('gantt', 2), /section Validation/);
  assert.match(caseSource('gantt', 256), /^\s*todayMarker off$/m);
  assert.match(caseSource('gantt', 256), /:milestone,/);

  assert.match(
    caseSource('er', 65),
    /^  direction LR\n  subgraph MASTER_DATA\n    direction TB$/m,
  );
  assert.match(
    caseSource('er', 66),
    /^  direction TB\n  subgraph MASTER_DATA\n    direction TB$/m,
  );
  assert.equal(
    [...caseSource('er', 65).matchAll(/^  subgraph /gm)].length,
    2,
  );

  assert.match(caseSource('treemap', 1), /^treemap$/m);
  assert.match(caseSource('treemap', 2), /^treemap-beta$/m);
  assert.match(caseSource('treemap', 1), /"Main"/);

  assert.doesNotMatch(caseSource('venn', 1), /^\s*union A,B/m);
  assert.match(caseSource('venn', 2), /^\s*set D/m);

  assert.match(caseSource('packet', 1), /^\s*0-3:/m);
  assert.match(caseSource('packet', 2), /^packet-beta$/m);

  assert.doesNotMatch(
    caseSource('cynefin', 1),
    /^\s*complex --> complicated/m,
  );
  assert.match(
    caseSource('cynefin', 5),
    /^\s*complex --> complicated/m,
  );
});

test('records the actual layout engine used by layout-specific families', () => {
  assert.deepEqual(
    new Set(scenarioBlueprintsByKind.swimlanes.map((entry) => entry.layout)),
    new Set(['swimlane']),
  );
  assert.deepEqual(
    new Set(scenarioBlueprintsByKind.architecture.map((entry) => entry.layout)),
    new Set(['cose-bilkent']),
  );
  assert.deepEqual(
    new Set(scenarioBlueprintsByKind.mindmap.map((entry) => entry.layout)),
    new Set(['dagre', 'tidy-tree']),
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
    'family-specific-coverage-contract',
    'flowchart-six-category-coverage-driven-corpus',
    'full-factorial-2x2x2x2x2x2x2x2-matrix-for-other-families',
    'family-specific-structural-profile',
  ]);
});

function categoryCounts(entries) {
  const counts = new Map();
  for (const entry of entries) {
    counts.set(
      entry.coverageCategory,
      (counts.get(entry.coverageCategory) ?? 0) + 1,
    );
  }
  return Object.fromEntries(counts);
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
