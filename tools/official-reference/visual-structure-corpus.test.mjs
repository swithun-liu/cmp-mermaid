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

test('keeps eight named family-specific capability axes as generator inputs', () => {
  for (const kind of kinds.filter((candidate) => candidate !== 'flowchart')) {
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

test('non-flowchart families use complete 256-source input spaces', () => {
  for (const kind of kinds.filter((candidate) => candidate !== 'flowchart')) {
    const familyCases = scenarioBlueprintsByKind[kind];
    const contract = familyCoverageContracts[kind];
    assert.equal(contract.mode, 'full-factorial', kind);
    assert.equal(contract.caseCount, 256, kind);
    assert.equal(contract.dimensionCount, 8, kind);
    assert.equal(
      new Set(familyCases.map((entry) => normalizeScenarioSource(entry))).size,
      256,
      `${kind} label-normalized sources`,
    );
    assert.equal(
      new Set(familyCases.map((entry) =>
        JSON.stringify(entry.inputDimensions.map(({ name, option }) => [
          name,
          option,
        ]))
      )).size,
      256,
      `${kind} input vectors`,
    );
    assert.ok(
      new Set(familyCases.map((entry) =>
        JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
      )).size >= contract.minimumStructuralProfiles,
      `${kind} structural profile diversity`,
    );
    for (const dimension of familyDimensionSpecs[kind]) {
      const counts = familyCases.reduce((result, entry) => {
        const selection = entry.inputDimensions.find(
          (candidate) => candidate.name === dimension.name,
        );
        result.set(
          selection.option,
          (result.get(selection.option) ?? 0) + 1,
        );
        return result;
      }, new Map());
      assert.deepEqual(
        Object.fromEntries(counts),
        Object.fromEntries(dimension.options.map((option) => [option, 128])),
        `${kind}/${dimension.name}`,
      );
    }
  }
});

test('every non-flowchart capability axis changes the actual Mermaid source', () => {
  for (const kind of kinds.filter((candidate) => candidate !== 'flowchart')) {
    const familyCases = scenarioBlueprintsByKind[kind];
    for (
      let dimensionIndex = 0;
      dimensionIndex < familyDimensionSpecs[kind].length;
      dimensionIndex += 1
    ) {
      for (let vector = 0; vector < 256; vector += 1) {
        if (((vector >> dimensionIndex) & 1) === 1) {
          continue;
        }
        const counterpart = vector | (1 << dimensionIndex);
        assert.notEqual(
          normalizeScenarioSource(familyCases[vector]),
          normalizeScenarioSource(familyCases[counterpart]),
          `${kind}/${familyDimensionSpecs[kind][dimensionIndex].name} ` +
            `${familyCases[vector].id} vs ${familyCases[counterpart].id}`,
        );
      }
    }
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
      assert.deepEqual(
        entry.normalizedSemanticStructure.dimensions,
        entry.inputDimensions,
        entry.id,
      );
      assert.ok(!Object.hasOwn(entry, 'coverageCategory'), entry.id);
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

  assert.match(
    caseSourceWithCapability('architecture', 'groups:ungrouped'),
    /s1\(server\)/,
  );
  assert.match(
    caseSourceWithCapability('architecture', 'groups:platform-group'),
    /group platform\(cloud\)/,
  );

  assert.match(
    caseSourceWithCapability('sequence', 'autonumber:manual'),
    /User->>Platform:/,
  );
  assert.match(
    caseSourceWithCapability('sequence', 'autonumber:autonumber'),
    /^  autonumber$/m,
  );

  assert.match(
    caseSourceWithCapability('pie', 'show-data:legend-only'),
    /^pie$/m,
  );
  assert.match(
    caseSourceWithCapability('pie', 'show-data:show-data'),
    /^pie showData$/m,
  );

  assert.match(
    caseSourceWithCapability('gantt', 'task-states:critical'),
    /:crit, task_1, 2027-01-01, 2d/,
  );
  assert.match(
    caseSourceWithCapability('gantt', 'sections:three-sections'),
    /section Validation/,
  );
  assert.match(
    caseSourceWithCapability('gantt', 'vertical-marker:vertical-marker'),
    /^\s*todayMarker off$/m,
  );
  assert.match(
    caseSourceWithCapability('gantt', 'milestone:milestone'),
    /:milestone,/,
  );

  const erLrSubgraph = caseSourceWithCapabilities('er', [
    'direction:left-to-right',
    'subgraph:subgraph',
  ]);
  assert.match(
    erLrSubgraph,
    /^  direction LR\n  subgraph MASTER_DATA\n    direction TB$/m,
  );
  assert.match(
    caseSourceWithCapabilities('er', [
      'direction:top-to-bottom',
      'subgraph:subgraph',
    ]),
    /^  direction TB\n  subgraph MASTER_DATA\n    direction TB$/m,
  );
  assert.equal(
    [...erLrSubgraph.matchAll(/^  subgraph /gm)].length,
    2,
  );

  assert.match(
    caseSourceWithCapability('treemap', 'header:treemap'),
    /^treemap$/m,
  );
  assert.match(
    caseSourceWithCapability('treemap', 'header:treemap-beta'),
    /^treemap-beta$/m,
  );
  assert.match(
    caseSourceWithCapability('treemap', 'root-count:single-root'),
    /"Main"/,
  );

  assert.doesNotMatch(
    caseSourceWithCapability('venn', 'pairwise-unions:none'),
    /^\s*union A,B/m,
  );
  assert.match(
    caseSourceWithCapability('venn', 'set-count:four-sets'),
    /^\s*set D/m,
  );

  assert.match(
    caseSourceWithCapability('packet', 'header:packet'),
    /^\s*0-3:/m,
  );
  assert.match(
    caseSourceWithCapability('packet', 'header:packet-beta'),
    /^packet-beta$/m,
  );

  assert.doesNotMatch(
    caseSourceWithCapability('cynefin', 'transitions:single-transition'),
    /^\s*complex --> complicated/m,
  );
  assert.match(
    caseSourceWithCapability('cynefin', 'transitions:cross-domain'),
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
    'flowchart-coverage-driven-corpus',
    'non-flowchart-full-factorial-input-space',
    'paired-source-effect-for-every-capability-axis',
    'family-specific-capability-axes',
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

function caseSourceWithCapability(kind, capability) {
  return caseSourceWithCapabilities(kind, [capability]);
}

function caseSourceWithCapabilities(kind, capabilities) {
  const entry = cases.find(
    (candidate) =>
      candidate.kind === kind &&
      capabilities.every((capability) =>
        candidate.inputDimensions?.some(
          ({ name, option }) => `${name}:${option}` === capability,
        )
      ),
  );
  assert.ok(entry, `${kind} ${capabilities.join(', ')}`);
  return entry.source;
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
