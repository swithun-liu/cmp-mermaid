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
  kinds,
  normalizeScenarioSource,
} from './visual-structure-corpus.mjs';

const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);

const omittedManifestKeys = new Set([
  'accessibilityDescription',
  'accessibilityTitle',
  'id',
  'renderer',
  'text',
  'title',
]);

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

export function auditVisualScenarioDuplicates({
  inputDirectory,
  outputFile,
  generatedAt = new Date().toISOString(),
  selectedKinds = kinds,
}) {
  const failures = [];
  const familyReports = selectedKinds.map((kind) => {
    const familyCases = cases.filter((entry) => entry.kind === kind);
    const caseReports = familyCases.map((entry) => {
      const prefix = capturePrefix(inputDirectory, entry);
      const nativeManifest = readJson(`${prefix}_native.manifest.json`);
      return {
        id: entry.id,
        scenarioBlueprintId: entry.scenarioBlueprintId,
        normalizedSemanticSignature: entry.normalizedSemanticSignature,
        normalizedSourceSha256: sha256(normalizeScenarioSource(entry)),
        nativeManifestFingerprint:
          manifestVisualStructureFingerprint(nativeManifest),
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
      uniqueNativeManifestFingerprints:
        caseReports.length - duplicateEntryCount(duplicateNativeManifests),
      uniqueNativePngs:
        caseReports.length - duplicateEntryCount(duplicateNativePngs),
      uniqueOfficialPngs:
        caseReports.length - duplicateEntryCount(duplicateOfficialPngs),
    };
    const duplicateEvidence = {
      blueprints: duplicateBlueprints,
      normalizedSemanticStructures: duplicateSemanticStructures,
      normalizedSources: duplicateNormalizedSources,
      nativeManifestFingerprints: duplicateNativeManifests,
      nativePngs: duplicateNativePngs,
      officialPngs: duplicateOfficialPngs,
    };
    for (const [name, count] of Object.entries(counts)) {
      if (count !== casesPerKind) {
        failures.push(
          `${kind} has ${count}/${casesPerKind} ${name}`,
        );
      }
    }
    return {
      kind,
      status: Object.values(counts).every(
        (count) => count === casesPerKind,
      )
        ? 'pass'
        : 'fail',
      counts,
      duplicates: duplicateEvidence,
      cases: caseReports,
    };
  });
  const report = {
    schemaVersion: 1,
    generatedAt,
    inputDirectory: path.relative(repositoryRoot, inputDirectory),
    requirements: {
      diagramFamilyCount: selectedKinds.length,
      casesPerFamily: casesPerKind,
      totalCaseCount: selectedKinds.length * casesPerKind,
      manifestNormalization:
        'Remove all rendered text, identifiers, titles, accessibility text, ' +
        'and renderer identity; retain ordered visual properties and geometry.',
      exactRasterDuplicatesAllowed: 0,
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
  writeFileSync(outputFile, `${JSON.stringify(report, null, 2)}\n`);
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

function runFromEnvironment() {
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
  const report = auditVisualScenarioDuplicates({
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
        `Native manifests ` +
        `${counts.uniqueNativeManifestFingerprints}/${casesPerKind}; ` +
        `Native PNGs ${counts.uniqueNativePngs}/${casesPerKind}; ` +
        `Official PNGs ${counts.uniqueOfficialPngs}/${casesPerKind}`,
    );
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
  runFromEnvironment();
}
