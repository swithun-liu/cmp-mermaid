import {
  existsSync,
  mkdirSync,
  readFileSync,
  readdirSync,
  statSync,
  writeFileSync,
} from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { compareTopologyManifests } from './topology-audit-core.mjs';

const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);
const inputDirectory = path.resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/coverage-driven/swimlanes',
);
const outputFile = path.resolve(
  repositoryRoot,
  process.env.OUTPUT_FILE ?? path.join(inputDirectory, 'topology-audit.json'),
);
const casePrefix = process.env.TOPOLOGY_CASE_PREFIX ?? 'visual_swimlanes_';
const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);

if (!existsSync(inputDirectory)) {
  throw new Error(`Missing topology audit input directory: ${inputDirectory}`);
}

const nativeSuffix = '_native.manifest.json';
const nativeFiles = walkFiles(inputDirectory)
  .filter((fileName) => fileName.endsWith(nativeSuffix))
  .map((fileName) => ({
    id: path.basename(fileName, nativeSuffix),
    nativeFile: fileName,
  }))
  .filter(({ id }) => id.startsWith(casePrefix))
  .filter(({ id }) => selectedIds.size === 0 || selectedIds.has(id))
  .sort((left, right) => left.id.localeCompare(right.id));

if (nativeFiles.length === 0) {
  throw new Error(
    `No ${casePrefix}* Native topology manifests found in ${inputDirectory}`,
  );
}

const cases = nativeFiles.map(({ id, nativeFile }) => {
  const officialFile = nativeFile.replace(nativeSuffix, '_official.manifest.json');
  if (!existsSync(officialFile)) {
    return {
      id,
      status: 'fail',
      findings: [{
        severity: 'error',
        code: 'official-manifest-missing',
        message: `Missing ${path.relative(repositoryRoot, officialFile)}`,
      }],
    };
  }
  const nativeManifest = readJson(nativeFile);
  const officialManifest = readJson(officialFile);
  return {
    id,
    ...compareTopologyManifests(nativeManifest, officialManifest),
  };
});

const counts = countBy(cases, (entry) => entry.status);
const report = {
  schemaVersion: 1,
  generatedAt: new Date().toISOString(),
  inputDirectory: path.relative(repositoryRoot, inputDirectory),
  casePrefix,
  caseCount: cases.length,
  counts,
  failures: cases
    .filter((entry) => entry.status === 'fail')
    .map((entry) => ({
      id: entry.id,
      findingCodes: [...new Set(entry.findings.map((finding) => finding.code))],
    })),
  cases,
};

mkdirSync(path.dirname(outputFile), { recursive: true });
writeFileSync(outputFile, `${JSON.stringify(report, null, 2)}\n`);

console.log(
  `Topology audit: ${counts.pass ?? 0} pass, ${counts.fail ?? 0} fail`,
);
console.log(`Report: ${path.relative(repositoryRoot, outputFile)}`);
for (const entry of cases.filter((candidate) => candidate.status === 'fail')) {
  for (const finding of entry.findings) {
    console.error(`- ${entry.id}/${finding.code}: ${finding.message}`);
  }
}
if ((counts.fail ?? 0) > 0) {
  process.exitCode = 1;
}

function walkFiles(directory) {
  const files = [];
  for (const name of readdirSync(directory)) {
    const candidate = path.join(directory, name);
    if (statSync(candidate).isDirectory()) {
      files.push(...walkFiles(candidate));
    } else {
      files.push(candidate);
    }
  }
  return files;
}

function readJson(fileName) {
  return JSON.parse(readFileSync(fileName, 'utf8'));
}

function countBy(entries, keyOf) {
  return entries.reduce((counts, entry) => {
    const key = keyOf(entry);
    counts[key] = (counts[key] ?? 0) + 1;
    return counts;
  }, {});
}
