import { createHash } from 'node:crypto';
import {
  existsSync,
  mkdirSync,
  readFileSync,
  writeFileSync,
} from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import {
  cases,
  casesPerKind,
  familyCoverageContracts,
  familyDimensionSpecs,
  kinds,
  normalizeScenarioSource,
} from './visual-structure-corpus.mjs';
import {
  analyzeFlowchartTopologyDiversity,
  extractFlowchartTopologies,
} from './flowchart-topology-audit.mjs';
import { manifestMatchesAuditCase } from './audit-source-identity.mjs';

const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);

const omittedManifestKeys = new Set([
  'accessibilityDescription',
  'accessibilityTitle',
  'auditCaseId',
  'auditSource',
  'id',
  'renderer',
  'sourceSha256',
  'text',
  'title',
]);

const requiredInputDimensionCount = 8;
const minimumStructuralProfileCount = 192;

export function manifestVisualStructureFingerprint(manifest) {
  return sha256(JSON.stringify(normalizeManifestValue(manifest)));
}

export function duplicateGroups(entries, valueOf) {
  const values = new Map();
  for (const entry of entries) {
    const value = valueOf(entry);
    const group = values.get(value) ?? [];
    group.push(entry.id);
    values.set(value, group);
  }
  return [...values.entries()]
    .filter(([, ids]) => ids.length > 1)
    .map(([value, ids]) => ({ value, ids }));
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

export function analyzeInputSpaceCoverage(
  entries,
  dimensions,
  { minimumStructuralProfiles = minimumStructuralProfileCount } = {},
) {
  const failures = [];
  const selectionsByEntry = entries.map((entry) => ({
    entry,
    selections: new Map(
      entry.inputDimensions.map((selection) => [
        selection.name,
        selection.option,
      ]),
    ),
  }));
  if (dimensions.length !== requiredInputDimensionCount) {
    failures.push(
      `expected ${requiredInputDimensionCount} named dimensions, found ` +
        dimensions.length,
    );
  }

  const dimensionReports = dimensions.map((dimension) => {
    const optionCounts = new Map(
      dimension.options.map((option) => [option, 0]),
    );
    const unexpectedOptionCounts = new Map();
    let missingCount = 0;
    for (const { selections } of selectionsByEntry) {
      const option = selections.get(dimension.name);
      if (option === undefined) {
        missingCount += 1;
      } else if (optionCounts.has(option)) {
        optionCounts.set(option, optionCounts.get(option) + 1);
      } else {
        unexpectedOptionCounts.set(
          option,
          (unexpectedOptionCounts.get(option) ?? 0) + 1,
        );
      }
    }
    const expectedCount = entries.length / dimension.options.length;
    const status =
      missingCount === 0 &&
      unexpectedOptionCounts.size === 0 &&
      [...optionCounts.values()].every((count) => count === expectedCount)
        ? 'pass'
        : 'fail';
    if (status === 'fail') {
      failures.push(`${dimension.name} is not evenly balanced`);
    }
    return {
      name: dimension.name,
      category: dimension.category,
      expectedCountPerOption: expectedCount,
      optionCounts: Object.fromEntries(optionCounts),
      unexpectedOptionCounts: Object.fromEntries(unexpectedOptionCounts),
      missingCount,
      status,
    };
  });

  const pairwiseReports = [];
  for (let leftIndex = 0; leftIndex < dimensions.length; leftIndex += 1) {
    for (
      let rightIndex = leftIndex + 1;
      rightIndex < dimensions.length;
      rightIndex += 1
    ) {
      const left = dimensions[leftIndex];
      const right = dimensions[rightIndex];
      const expectedCount =
        entries.length / (left.options.length * right.options.length);
      const combinations = left.options.flatMap((leftOption) =>
        right.options.map((rightOption) => ({
          leftOption,
          rightOption,
          count: selectionsByEntry.filter(({ selections }) =>
            selections.get(left.name) === leftOption &&
            selections.get(right.name) === rightOption
          ).length,
        }))
      );
      const status = combinations.every(
        (combination) => combination.count === expectedCount,
      )
        ? 'pass'
        : 'fail';
      if (status === 'fail') {
        failures.push(`${left.name} x ${right.name} is not evenly balanced`);
      }
      pairwiseReports.push({
        left: left.name,
        right: right.name,
        expectedCountPerCombination: expectedCount,
        combinations,
        status,
      });
    }
  }

  const dimensionVector = ({ selections }) =>
    JSON.stringify(dimensions.map((dimension) =>
      selections.get(dimension.name)
    ));
  const dimensionVectorEntries = selectionsByEntry.map((entry) => ({
    id: entry.entry.id,
    value: dimensionVector(entry),
  }));
  const uniqueDimensionVectorCount =
    new Set(dimensionVectorEntries.map((entry) => entry.value)).size;
  const expectedDimensionVectorCount = 2 ** dimensions.length;
  if (
    entries.length !== expectedDimensionVectorCount ||
    uniqueDimensionVectorCount !== expectedDimensionVectorCount
  ) {
    failures.push(
      `expected ${expectedDimensionVectorCount} full-factorial dimension ` +
        `vectors, found ${uniqueDimensionVectorCount} across ${entries.length} cases`,
    );
  }

  const profileEntries = entries.map((entry) => ({
    id: entry.id,
    value: sha256(JSON.stringify(
      entry.normalizedSemanticStructure.structuralProfile,
    )),
  }));
  const uniqueStructuralProfileCount =
    new Set(profileEntries.map((entry) => entry.value)).size;
  if (uniqueStructuralProfileCount < minimumStructuralProfiles) {
    failures.push(
      `expected at least ${minimumStructuralProfiles} structural profiles, ` +
        `found ${uniqueStructuralProfileCount}`,
    );
  }

  const entriesByVector = new Map(
    selectionsByEntry.map((entry) => [dimensionVector(entry), entry]),
  );
  const topologyAndDataEffects = dimensions
    .filter((dimension) =>
      dimension.category === 'topology' || dimension.category === 'data'
    )
    .map((dimension) => {
      const dimensionIndex = dimensions.indexOf(dimension);
      const unchangedPairs = [];
      let missingCounterpartCount = 0;
      let comparisonCount = 0;
      for (const candidate of selectionsByEntry) {
        if (
          candidate.selections.get(dimension.name) !== dimension.options[0]
        ) {
          continue;
        }
        const counterpartVector = dimensions.map((vectorDimension, index) =>
          index === dimensionIndex
            ? dimension.options[1]
            : candidate.selections.get(vectorDimension.name)
        );
        const counterpart = entriesByVector.get(
          JSON.stringify(counterpartVector),
        );
        if (counterpart === undefined) {
          missingCounterpartCount += 1;
          continue;
        }
        comparisonCount += 1;
        const profile = JSON.stringify(
          candidate.entry.normalizedSemanticStructure.structuralProfile,
        );
        const counterpartProfile = JSON.stringify(
          counterpart.entry.normalizedSemanticStructure.structuralProfile,
        );
        if (profile === counterpartProfile) {
          unchangedPairs.push([
            candidate.entry.id,
            counterpart.entry.id,
          ]);
        }
      }
      const expectedComparisonCount = entries.length / 2;
      const status =
        comparisonCount === expectedComparisonCount &&
        missingCounterpartCount === 0 &&
        unchangedPairs.length === 0
          ? 'pass'
          : 'fail';
      if (status === 'fail') {
        failures.push(
          `${dimension.name} does not change every paired structural profile`,
        );
      }
      return {
        name: dimension.name,
        category: dimension.category,
        expectedComparisonCount,
        comparisonCount,
        missingCounterpartCount,
        unchangedPairs,
        status,
      };
    });

  const topologyAndDataDimensions = dimensions.filter((dimension) =>
    dimension.category === 'topology' || dimension.category === 'data'
  );
  const uniqueTopologyAndDataVectorCount = new Set(
    selectionsByEntry.map(({ selections }) =>
      JSON.stringify(topologyAndDataDimensions.map((dimension) =>
        selections.get(dimension.name)
      ))
    ),
  ).size;

  return {
    status: failures.length === 0 ? 'pass' : 'fail',
    counts: {
      cases: entries.length,
      namedDimensions: dimensions.length,
      dimensionPairs: pairwiseReports.length,
      uniqueDimensionVectors: uniqueDimensionVectorCount,
      uniqueTopologyAndDataVectors: uniqueTopologyAndDataVectorCount,
      uniqueStructuralProfiles: uniqueStructuralProfileCount,
    },
    requirements: {
      namedDimensions: requiredInputDimensionCount,
      fullFactorialDimensionVectors: expectedDimensionVectorCount,
      minimumStructuralProfiles,
    },
    dimensions: dimensionReports,
    dimensionPairs: pairwiseReports,
    topologyAndDataEffects,
    repeatedDimensionVectors: duplicateGroups(
      dimensionVectorEntries,
      (entry) => entry.value,
    ),
    nearIsomorphicStructuralProfiles: duplicateGroups(
      profileEntries,
      (entry) => entry.value,
    ),
    failures,
  };
}

export function analyzeCoverageDrivenCoverage(entries, contract) {
  const failures = [];
  const categoryReports = contract.categories.map((category) => {
    const count = entries.filter(
      (entry) => entry.coverageCategory === category.id,
    ).length;
    const status = count === category.quota ? 'pass' : 'fail';
    if (status === 'fail') {
      failures.push(
        `${category.id} has ${count}/${category.quota} required cases`,
      );
    }
    return {
      id: category.id,
      expectedCount: category.quota,
      count,
      status,
    };
  });
  const unexpectedCategories = entries
    .map((entry) => entry.coverageCategory)
    .filter((value) =>
      !contract.categories.some((category) => category.id === value)
    );
  if (unexpectedCategories.length > 0) {
    failures.push(
      `unexpected categories: ${[...new Set(unexpectedCategories)].join(', ')}`,
    );
  }

  const capabilityCounts = new Map();
  for (const entry of entries) {
    for (const capability of entry.coverageCapabilities ?? []) {
      capabilityCounts.set(
        capability,
        (capabilityCounts.get(capability) ?? 0) + 1,
      );
    }
  }
  const missingCapabilities = contract.requiredCapabilities.filter(
    (capability) => !capabilityCounts.has(capability),
  );
  if (missingCapabilities.length > 0) {
    failures.push(
      `missing required capabilities: ${missingCapabilities.join(', ')}`,
    );
  }

  const structuralProfiles = entries.map((entry) =>
    sha256(JSON.stringify(
      entry.normalizedSemanticStructure.structuralProfile,
    ))
  );
  const uniqueStructuralProfileCount = new Set(structuralProfiles).size;
  if (uniqueStructuralProfileCount < contract.minimumStructuralProfiles) {
    failures.push(
      `expected at least ${contract.minimumStructuralProfiles} structural ` +
        `profiles, found ${uniqueStructuralProfileCount}`,
    );
  }

  const equivalentEntries = entries.filter(
    (entry) => entry.coverageCategory === 'equivalent-syntax',
  );
  const equivalenceGroups = groupBy(
    equivalentEntries,
    (entry) => entry.equivalenceGroup,
  );
  const invalidEquivalenceGroups = [...equivalenceGroups.entries()]
    .filter(([, group]) =>
      group.length !== 2 ||
      new Set(group.map((entry) => entry.syntaxVariant)).size !== 2 ||
      new Set(group.map((entry) =>
        JSON.stringify(entry.normalizedSemanticStructure.structuralProfile)
      )).size !== 1
    )
    .map(([group]) => group);
  if (
    equivalenceGroups.size !== contract.equivalentSyntaxGroupCount ||
    invalidEquivalenceGroups.length > 0
  ) {
    failures.push(
      `expected ${contract.equivalentSyntaxGroupCount} valid equivalent ` +
        `syntax pairs, found ${equivalenceGroups.size}; invalid ` +
        `${invalidEquivalenceGroups.join(', ') || 'none'}`,
    );
  }

  return {
    mode: contract.mode,
    status: failures.length === 0 ? 'pass' : 'fail',
    counts: {
      cases: entries.length,
      categories: categoryReports.length,
      requiredCapabilities: contract.requiredCapabilities.length,
      coveredRequiredCapabilities:
        contract.requiredCapabilities.length - missingCapabilities.length,
      uniqueCapabilities: capabilityCounts.size,
      uniqueStructuralProfiles: uniqueStructuralProfileCount,
      equivalentSyntaxGroups: equivalenceGroups.size,
    },
    requirements: {
      cases: contract.caseCount,
      categories: Object.fromEntries(
        contract.categories.map((category) => [category.id, category.quota]),
      ),
      requiredCapabilities: contract.requiredCapabilities,
      minimumStructuralProfiles: contract.minimumStructuralProfiles,
      equivalentSyntaxGroups: contract.equivalentSyntaxGroupCount,
    },
    categories: categoryReports,
    capabilityCounts: Object.fromEntries(
      [...capabilityCounts.entries()].sort(([left], [right]) =>
        left.localeCompare(right)
      ),
    ),
    missingCapabilities,
    unexpectedCategories: [...new Set(unexpectedCategories)],
    invalidEquivalenceGroups,
    failures,
  };
}

export async function auditVisualScenarioDuplicates({
  inputDirectory,
  outputFile,
  generatedAt = new Date().toISOString(),
  selectedKinds = kinds,
}) {
  const failures = [];
  const topologyAudits = new Map();
  if (selectedKinds.includes('flowchart')) {
    const flowchartCases = cases.filter((entry) => entry.kind === 'flowchart');
    const parsedTopologies = await extractFlowchartTopologies(flowchartCases);
    topologyAudits.set(
      'flowchart',
      analyzeFlowchartTopologyDiversity(
        flowchartCases,
        parsedTopologies,
        familyCoverageContracts.flowchart.topologyDiversity,
      ),
    );
  }
  const familyReports = selectedKinds.map((kind) => {
    const familyFailures = [];
    const familyCases = cases.filter((entry) => entry.kind === kind);
    const coverageContract = familyCoverageContracts[kind];
    const coverageAudit = coverageContract.mode === 'coverage-driven'
      ? analyzeCoverageDrivenCoverage(familyCases, coverageContract)
      : analyzeInputSpaceCoverage(
          familyCases,
          familyDimensionSpecs[kind],
        );
    const caseReports = familyCases.map((entry) => {
      const prefix = capturePrefix(inputDirectory, entry);
      const nativeManifest = readJson(`${prefix}_native.manifest.json`);
      const officialManifest = readJson(`${prefix}_official.manifest.json`);
      return {
        id: entry.id,
        scenarioBlueprintId: entry.scenarioBlueprintId,
        ...(entry.inputDimensions === undefined
          ? {
              coverageCategory: entry.coverageCategory,
              coverageCapabilities: entry.coverageCapabilities,
              equivalenceGroup: entry.equivalenceGroup,
              syntaxVariant: entry.syntaxVariant,
            }
          : { inputDimensions: entry.inputDimensions }),
        normalizedSemanticSignature: entry.normalizedSemanticSignature,
        normalizedSourceSha256: sha256(normalizeScenarioSource(entry)),
        structuralProfileFingerprint: sha256(JSON.stringify(
          entry.normalizedSemanticStructure.structuralProfile,
        )),
        nativeManifestFingerprint:
          manifestVisualStructureFingerprint(nativeManifest),
        nativeSourceIdentityMatches:
          manifestMatchesAuditCase(nativeManifest, entry, 'visual-parity'),
        officialSourceIdentityMatches:
          manifestMatchesAuditCase(officialManifest, entry, 'visual-parity'),
        nativePngSha256: sha256(readFileSync(`${prefix}_native.png`)),
        officialPngSha256: sha256(readFileSync(`${prefix}_official.png`)),
      };
    });
    const duplicateBlueprints = duplicateGroups(
      caseReports,
      (entry) => entry.scenarioBlueprintId,
    );
    const duplicateSemanticStructures = duplicateGroups(
      caseReports,
      (entry) => entry.normalizedSemanticSignature,
    );
    const duplicateNormalizedSources = duplicateGroups(
      caseReports,
      (entry) => entry.normalizedSourceSha256,
    );
    const duplicateStructuralProfiles = duplicateGroups(
      caseReports,
      (entry) => entry.structuralProfileFingerprint,
    );
    const duplicateNativeManifests = duplicateGroups(
      caseReports,
      (entry) => entry.nativeManifestFingerprint,
    );
    const duplicateNativePngs = duplicateGroups(
      caseReports,
      (entry) => entry.nativePngSha256,
    );
    const duplicateOfficialPngs = duplicateGroups(
      caseReports,
      (entry) => entry.officialPngSha256,
    );
    const counts = {
      cases: caseReports.length,
      uniqueBlueprints:
        caseReports.length - duplicateEntryCount(duplicateBlueprints),
      uniqueNormalizedSemanticStructures:
        caseReports.length - duplicateEntryCount(duplicateSemanticStructures),
      uniqueNormalizedSources:
        caseReports.length - duplicateEntryCount(duplicateNormalizedSources),
      uniqueStructuralProfiles:
        caseReports.length - duplicateEntryCount(duplicateStructuralProfiles),
      uniqueNativeManifestFingerprints:
        caseReports.length - duplicateEntryCount(duplicateNativeManifests),
      uniqueNativePngs:
        caseReports.length - duplicateEntryCount(duplicateNativePngs),
      uniqueOfficialPngs:
        caseReports.length - duplicateEntryCount(duplicateOfficialPngs),
      sourceBoundNativeManifests:
        caseReports.filter((entry) => entry.nativeSourceIdentityMatches).length,
      sourceBoundOfficialManifests:
        caseReports.filter((entry) => entry.officialSourceIdentityMatches).length,
    };
    const duplicateEvidence = {
      blueprints: duplicateBlueprints,
      normalizedSemanticStructures: duplicateSemanticStructures,
      normalizedSources: duplicateNormalizedSources,
      structuralProfiles: duplicateStructuralProfiles,
      nativeManifestFingerprints: duplicateNativeManifests,
      nativePngs: duplicateNativePngs,
      officialPngs: duplicateOfficialPngs,
    };
    for (const [name, count] of Object.entries({
      cases: counts.cases,
      uniqueBlueprints: counts.uniqueBlueprints,
      uniqueNormalizedSemanticStructures:
        counts.uniqueNormalizedSemanticStructures,
      uniqueNormalizedSources: counts.uniqueNormalizedSources,
      sourceBoundNativeManifests: counts.sourceBoundNativeManifests,
      sourceBoundOfficialManifests: counts.sourceBoundOfficialManifests,
    })) {
      if (count !== casesPerKind) {
        familyFailures.push(
          `${kind} has ${count}/${casesPerKind} ${name}`,
        );
      }
    }
    const minimumProfiles = coverageContract.mode === 'coverage-driven'
      ? coverageContract.minimumStructuralProfiles
      : minimumStructuralProfileCount;
    if (counts.uniqueStructuralProfiles < minimumProfiles) {
      familyFailures.push(
        `${kind} has ${counts.uniqueStructuralProfiles}/` +
          `${minimumProfiles} required structural profiles`,
      );
    }
    familyFailures.push(
      ...coverageAudit.failures.map(
        (failure) => `${kind} coverage: ${failure}`,
      ),
    );
    const topologyAudit = topologyAudits.get(kind);
    if (topologyAudit !== undefined) {
      familyFailures.push(
        ...topologyAudit.failures.map(
          (failure) => `${kind} parser topology: ${failure}`,
        ),
      );
    }
    failures.push(...familyFailures);
    return {
      kind,
      status: familyFailures.length === 0 ? 'pass' : 'fail',
      counts,
      coverageContract,
      coverageAudit,
      ...(coverageContract.mode === 'full-factorial'
        ? { inputSpaceCoverage: coverageAudit }
        : { coverageDrivenCoverage: coverageAudit }),
      ...(topologyAudit === undefined
        ? {}
        : { parserTopologyDiversity: topologyAudit }),
      duplicates: duplicateEvidence,
      cases: caseReports,
    };
  });
  const report = {
    schemaVersion: 4,
    generatedAt,
    inputDirectory: path.relative(repositoryRoot, inputDirectory),
    requirements: {
      diagramFamilyCount: selectedKinds.length,
      casesPerFamily: casesPerKind,
      totalCaseCount: selectedKinds.length * casesPerKind,
      manifestNormalization:
        'Remove all rendered text, identifiers, titles, accessibility text, ' +
        'and renderer identity; retain ordered visual properties and geometry.',
      captureSourceIdentityPolicy:
        'Both Native and Official capture manifests must bind the requested ' +
        'case ID and visual-parity corpus to the exact expected source SHA-256.',
      coverageContractsByFamily: Object.fromEntries(
        selectedKinds.map((kind) => [kind, familyCoverageContracts[kind]]),
      ),
      nativeVisualStructurePolicy:
        'Diagnostic only. Distinct valid inputs can intentionally render the ' +
        'same geometry, including long-form grammar aliases and reset frames ' +
        'without predecessors.',
      flowchartTopologyPolicy:
        'Parse every source through Mermaid.js 12.0.0 FlowDB, then compute an ' +
        'identifier-, label-, and shape-independent canonical directed graph ' +
        'fingerprint that retains multiedges and subgraph containment.',
      rasterDuplicatePolicy:
        'Diagnostic only. Raster hashes can differ because labels differ and ' +
        'can match when distinct valid inputs are visually equivalent.',
    },
    summary: {
      diagramFamilyCount: familyReports.length,
      caseCount: familyReports.reduce(
        (sum, family) => sum + family.counts.cases,
        0,
      ),
      passingFamilyCount:
        familyReports.filter((family) => family.status === 'pass').length,
      failingFamilyCount:
        familyReports.filter((family) => family.status === 'fail').length,
      failureCount: failures.length,
    },
    failures,
    families: familyReports,
  };
  mkdirSync(path.dirname(outputFile), { recursive: true });
  const persistedReport = mergePartialAuditReport(
    outputFile,
    report,
    selectedKinds,
  );
  writeFileSync(outputFile, `${JSON.stringify(persistedReport, null, 2)}\n`);
  return report;
}

function normalizeManifestValue(value) {
  if (value === null || typeof value !== 'object') {
    return typeof value === 'number'
      ? Math.round(value * 10_000) / 10_000
      : value;
  }
  if (Array.isArray(value)) {
    return value.map((entry) => normalizeManifestValue(entry));
  }
  return Object.fromEntries(
    Object.entries(value)
      .filter(([key]) => !omittedManifestKeys.has(key))
      .sort(([left], [right]) => left.localeCompare(right))
      .map(([key, child]) => [key, normalizeManifestValue(child)]),
  );
}

function duplicateEntryCount(groups) {
  return groups.reduce((sum, group) => sum + group.ids.length - 1, 0);
}

function mergePartialAuditReport(outputFile, current, selectedKinds) {
  if (selectedKinds.length === kinds.length || !existsSync(outputFile)) {
    return current;
  }
  const previous = readJson(outputFile);
  const currentFamilies = new Map(
    current.families.map((family) => [family.kind, family]),
  );
  const previousFamilies = new Map(
    previous.families.map((family) => [family.kind, family]),
  );
  const families = kinds.map((kind) =>
    currentFamilies.get(kind) ?? previousFamilies.get(kind)
  );
  if (families.some((family) => family === undefined)) {
    throw new Error('Partial duplicate audit cannot merge an incomplete report');
  }
  const failures = families
    .filter((family) => family.status === 'fail')
    .map((family) => `${family.kind} failed its duplicate audit`);
  return {
    ...previous,
    schemaVersion: 5,
    generatedAt: current.generatedAt,
    ...mergePartialInputDirectories(
      previous,
      current,
      selectedKinds,
    ),
    requirements: {
      ...previous.requirements,
      ...current.requirements,
      diagramFamilyCount: kinds.length,
      totalCaseCount: kinds.length * casesPerKind,
      coverageContractsByFamily: Object.fromEntries(
        kinds.map((kind) => [kind, familyCoverageContracts[kind]]),
      ),
    },
    summary: {
      diagramFamilyCount: families.length,
      caseCount: families.reduce(
        (sum, family) => sum + family.counts.cases,
        0,
      ),
      passingFamilyCount:
        families.filter((family) => family.status === 'pass').length,
      failingFamilyCount:
        families.filter((family) => family.status === 'fail').length,
      failureCount: failures.length,
    },
    failures,
    families,
  };
}

function mergePartialInputDirectories(previous, current, selectedKinds) {
  const previousDirectories = previous.inputDirectoriesByKind ?? {};
  const directoriesByKind = new Map(
    kinds.map((kind) => [
      kind,
      previousDirectories[kind] ?? previous.inputDirectory,
    ]),
  );
  for (const kind of selectedKinds) {
    directoriesByKind.set(kind, current.inputDirectory);
  }
  for (const [kind, directory] of directoriesByKind) {
    if (typeof directory !== 'string' || directory.length === 0) {
      throw new Error(`Missing duplicate-audit input directory for ${kind}`);
    }
  }
  const uniqueDirectories = new Set(directoriesByKind.values());
  return {
    inputDirectory:
      uniqueDirectories.size === 1
        ? directoriesByKind.values().next().value
        : null,
    inputDirectoriesByKind: Object.fromEntries(directoriesByKind),
  };
}

function readJson(fileName) {
  return JSON.parse(readFileSync(fileName, 'utf8'));
}

function capturePrefix(inputDirectory, entry) {
  const directPrefix = path.join(inputDirectory, entry.id);
  if (existsSync(`${directPrefix}_native.manifest.json`)) {
    return directPrefix;
  }
  return path.join(inputDirectory, entry.kind, entry.id);
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}

async function runFromEnvironment() {
  const inputDirectory = path.resolve(
    repositoryRoot,
    process.env.INPUT_DIR ??
      'captures/local/independent-visual-parity-v2',
  );
  const outputFile = path.resolve(
    repositoryRoot,
    process.env.OUTPUT_FILE ??
      'docs/assets/stability-report/visual-scenario-duplicate-audit.json',
  );
  const corpusKind = process.env.CORPUS_KIND ?? 'all';
  const selectedKinds = kinds.filter(
    (kind) => corpusKind === 'all' || kind === corpusKind,
  );
  if (selectedKinds.length === 0) {
    throw new Error(`Unsupported CORPUS_KIND: ${corpusKind}`);
  }
  const report = await auditVisualScenarioDuplicates({
    inputDirectory,
    outputFile,
    selectedKinds,
  });
  for (const family of report.families) {
    const counts = family.counts;
    console.log(
      `${family.kind}: blueprints ${counts.uniqueBlueprints}/${casesPerKind}; ` +
        `semantic structures ` +
        `${counts.uniqueNormalizedSemanticStructures}/${casesPerKind}; ` +
        `normalized sources ${counts.uniqueNormalizedSources}/${casesPerKind}; ` +
        `structural profiles ` +
        `${counts.uniqueStructuralProfiles}/${casesPerKind}; ` +
        `Native manifests ` +
        `${counts.uniqueNativeManifestFingerprints}/${casesPerKind}; ` +
        `source-bound manifests Native ` +
        `${counts.sourceBoundNativeManifests}/${casesPerKind}, Official ` +
        `${counts.sourceBoundOfficialManifests}/${casesPerKind}; ` +
        `Native PNGs ${counts.uniqueNativePngs}/${casesPerKind}; ` +
        `Official PNGs ${counts.uniqueOfficialPngs}/${casesPerKind}`,
    );
    console.log(
      family.coverageContract.mode === 'coverage-driven'
        ? `${family.kind}: coverage categories ` +
          `${family.coverageAudit.counts.categories}; capabilities ` +
          `${family.coverageAudit.counts.coveredRequiredCapabilities}/` +
          `${family.coverageAudit.counts.requiredCapabilities}; profiles ` +
          `${family.coverageAudit.counts.uniqueStructuralProfiles}`
        : `${family.kind}: input dimensions ` +
          `${family.coverageAudit.counts.namedDimensions}/` +
          `${requiredInputDimensionCount}; vectors ` +
          `${family.coverageAudit.counts.uniqueDimensionVectors}/` +
          `${casesPerKind}; pair matrices ` +
          `${family.coverageAudit.counts.dimensionPairs}`,
    );
    if (family.parserTopologyDiversity !== undefined) {
      console.log(
        `${family.kind}: parser topologies ` +
          `${family.parserTopologyDiversity.counts.uniqueTopologies}/` +
          `${family.parserTopologyDiversity.counts.cases}; classes ` +
          JSON.stringify(
            family.parserTopologyDiversity.counts.topologyClasses,
          ),
      );
    }
  }
  console.log(
    `Duplicate audit: ${report.summary.passingFamilyCount}/` +
      `${report.summary.diagramFamilyCount} families pass; report ` +
      `${path.relative(repositoryRoot, outputFile)}`,
  );
  if (report.failures.length > 0) {
    for (const failure of report.failures) {
      console.error(`- ${failure}`);
    }
    process.exitCode = 1;
  }
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  await runFromEnvironment();
}
