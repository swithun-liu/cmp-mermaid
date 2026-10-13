import {
  existsSync,
  readFileSync,
  readdirSync,
} from 'node:fs';
import { dirname, relative, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const upstreamRepository = resolve(
  repositoryRoot,
  process.env.MERMAID_SOURCE_DIR ?? 'build/upstream/mermaid-src',
);
const upstreamRoot = resolve(
  upstreamRepository,
  'packages/mermaid/src/rendering-util/layout-algorithms/swimlanes',
);
const testRoot = resolve(
  repositoryRoot,
  'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/swimlane/upstream',
);
const mapPath = resolve(root, 'swimlane-upstream-spec-map.json');
const corpusPath = resolve(root, 'swimlane-ddlt-corpus.mjs');

const map = JSON.parse(readFileSync(mapPath, 'utf8'));
const packageJson = JSON.parse(
  readFileSync(resolve(upstreamRepository, 'packages/mermaid/package.json'), 'utf8'),
);
if (map.baseline !== packageJson.version || map.baseline !== '12.0.0') {
  throw new Error(
    `Swimlanes spec map baseline ${map.baseline} does not match Mermaid ${packageJson.version}`,
  );
}

const upstreamSpecs = collectFiles(upstreamRoot)
  .filter((path) => path.endsWith('.spec.ts') || path.endsWith('.test.ts'))
  .map((path) => relative(upstreamRoot, path))
  .sort();
const mappedSpecs = map.mappings.map((entry) => entry.upstream).sort();
assertUnique(mappedSpecs, 'upstream spec mapping');
assertSameList(upstreamSpecs, mappedSpecs, 'upstream Swimlanes specs');

const { cases: ddltCases } = await import(pathToFileURL(corpusPath));
const ddltIds = new Set(ddltCases.map((entry) => entry.id));
const supportedCoverage = new Set([
  'kotlin-unit',
  'ddlt-corpus',
  'structured-unsupported',
]);
for (const entry of map.mappings) {
  if (!supportedCoverage.has(entry.coverage)) {
    throw new Error(`${entry.upstream} has unknown coverage ${entry.coverage}`);
  }
  if (entry.coverage === 'kotlin-unit') {
    const target = resolve(testRoot, entry.target);
    if (!existsSync(target)) {
      throw new Error(`${entry.upstream} maps to missing Kotlin test ${target}`);
    }
    const source = readFileSync(target, 'utf8');
    if (!source.includes('@Test')) {
      throw new Error(`${entry.upstream} maps to ${target} without any @Test`);
    }
  } else if (!ddltIds.has(entry.target)) {
    throw new Error(`${entry.upstream} maps to missing DDLT case ${entry.target}`);
  }
}

const coverageCounts = map.mappings.reduce((counts, entry) => {
  counts[entry.coverage] = (counts[entry.coverage] ?? 0) + 1;
  return counts;
}, {});
console.log(
  `Swimlanes spec map: ${map.mappings.length}/${upstreamSpecs.length} files mapped ` +
    `(${Object.entries(coverageCounts)
      .map(([coverage, count]) => `${coverage}=${count}`)
      .join(', ')})`,
);

function collectFiles(directory) {
  return readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const path = resolve(directory, entry.name);
    return entry.isDirectory() ? collectFiles(path) : [path];
  });
}

function assertUnique(values, label) {
  const duplicates = values.filter((value, index) => values.indexOf(value) !== index);
  if (duplicates.length > 0) {
    throw new Error(`Duplicate ${label}: ${[...new Set(duplicates)].join(', ')}`);
  }
}

function assertSameList(expected, actual, label) {
  const missing = expected.filter((value) => !actual.includes(value));
  const extra = actual.filter((value) => !expected.includes(value));
  if (missing.length > 0 || extra.length > 0) {
    throw new Error(
      `${label} mismatch: missing=[${missing.join(', ')}], extra=[${extra.join(', ')}]`,
    );
  }
}
