import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import mermaid from 'mermaid';
import {
  diagramFamilies,
  implementedDiagramFamilies,
  internalRegistryIds,
  mermaidBaseline,
  pendingDiagramFamilies,
} from './diagram-inventory.mjs';

const root = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(root, '../..');
const explicitSourceDirectory = process.env.MERMAID_SOURCE_DIR;
const sourceDirectory = path.resolve(
  repositoryRoot,
  explicitSourceDirectory ?? 'build/upstream/mermaid-src',
);

const failures = [];
const packageVersion = JSON.parse(
  fs.readFileSync(path.resolve(root, 'node_modules/mermaid/package.json'), 'utf8'),
).version;
if (packageVersion !== mermaidBaseline.version) {
  failures.push(
    `Installed Mermaid is ${packageVersion}; expected ${mermaidBaseline.version}`,
  );
}

mermaid.initialize({ startOnLoad: false });
const runtimeRegistryIds = mermaid
  .getRegisteredDiagramsMetadata()
  .map(({ id }) => id)
  .sort();
const expectedRegistryIds = [
  ...internalRegistryIds,
  ...diagramFamilies.flatMap(({ registryIds }) => registryIds),
].sort();
compareSets('Mermaid registry IDs', runtimeRegistryIds, expectedRegistryIds);

const familyIds = diagramFamilies.map(({ id }) => id);
validateUnique('diagram family ID', familyIds);
validateUnique(
  'diagram documentation file',
  diagramFamilies.map(({ documentation }) => documentation),
);
if (diagramFamilies.length !== 33) {
  failures.push(`Expected 33 diagram families, found ${diagramFamilies.length}`);
}
if (implementedDiagramFamilies.length !== 21) {
  failures.push(
    `Expected 21 implemented diagram families, found ${implementedDiagramFamilies.length}`,
  );
}
if (pendingDiagramFamilies.length !== 12) {
  failures.push(
    `Expected 12 pending diagram families, found ${pendingDiagramFamilies.length}`,
  );
}

for (const family of diagramFamilies) {
  for (const probe of family.headers) {
    if (probe.registryId === null) continue;
    let detected;
    try {
      detected = mermaid.detectType(probe.source);
    } catch (error) {
      failures.push(
        `${family.id} header ${JSON.stringify(probe.source)} was not detected: ` +
          `${error instanceof Error ? error.message : String(error)}`,
      );
      continue;
    }
    if (detected !== probe.registryId) {
      failures.push(
        `${family.id} header ${JSON.stringify(probe.source)} detected as ` +
          `${detected}; expected ${probe.registryId}`,
      );
    }
  }
}

if (fs.existsSync(sourceDirectory)) {
  validateSourceTree(sourceDirectory);
} else if (explicitSourceDirectory !== undefined) {
  failures.push(`MERMAID_SOURCE_DIR does not exist: ${sourceDirectory}`);
}

console.log(
  `Mermaid ${mermaidBaseline.version}: ${diagramFamilies.length} official ` +
    `families, ${implementedDiagramFamilies.length} implemented, ` +
    `${pendingDiagramFamilies.length} pending`,
);
console.log(
  `Registry: ${runtimeRegistryIds.length} IDs mapped; ` +
    `${internalRegistryIds.length} internal IDs excluded from family count`,
);
if (fs.existsSync(sourceDirectory)) {
  console.log(`Source documentation verified in ${sourceDirectory}`);
}

if (failures.length > 0) {
  failures.forEach((failure) => console.error(`- ${failure}`));
  process.exitCode = 1;
}

function validateSourceTree(directory) {
  const mermaidPackage = readJson(
    path.join(directory, 'packages/mermaid/package.json'),
    'Mermaid source package',
  );
  if (mermaidPackage?.version !== mermaidBaseline.version) {
    failures.push(
      `Mermaid source package is ${mermaidPackage?.version ?? 'missing'}; ` +
        `expected ${mermaidBaseline.version}`,
    );
  }

  const syntaxDirectory = path.join(
    directory,
    'packages/mermaid/src/docs/syntax',
  );
  if (!fs.existsSync(syntaxDirectory)) {
    failures.push(`Missing Mermaid syntax directory: ${syntaxDirectory}`);
  } else {
    const actualDocuments = fs.readdirSync(syntaxDirectory)
      .filter((name) => name.endsWith('.md') && name !== 'examples.md')
      .sort();
    const expectedDocuments = diagramFamilies
      .map(({ documentation }) => documentation)
      .sort();
    compareSets(
      'Mermaid syntax documents',
      actualDocuments,
      expectedDocuments,
    );
  }

  const zenUmlPackage = readJson(
    path.join(directory, 'packages/mermaid-zenuml/package.json'),
    'ZenUML source package',
  );
  if (zenUmlPackage?.version !== mermaidBaseline.zenUmlVersion) {
    failures.push(
      `ZenUML source package is ${zenUmlPackage?.version ?? 'missing'}; ` +
        `expected ${mermaidBaseline.zenUmlVersion}`,
    );
  }
}

function readJson(fileName, label) {
  if (!fs.existsSync(fileName)) {
    failures.push(`Missing ${label}: ${fileName}`);
    return null;
  }
  return JSON.parse(fs.readFileSync(fileName, 'utf8'));
}

function validateUnique(label, values) {
  const duplicates = values.filter(
    (value, index) => values.indexOf(value) !== index,
  );
  if (duplicates.length > 0) {
    failures.push(`Duplicate ${label}: ${[...new Set(duplicates)].join(', ')}`);
  }
}

function compareSets(label, actual, expected) {
  const actualSet = new Set(actual);
  const expectedSet = new Set(expected);
  const unexpected = [...actualSet].filter((value) => !expectedSet.has(value));
  const missing = [...expectedSet].filter((value) => !actualSet.has(value));
  if (unexpected.length > 0) {
    failures.push(`${label} contain unexpected entries: ${unexpected.join(', ')}`);
  }
  if (missing.length > 0) {
    failures.push(`${label} are missing entries: ${missing.join(', ')}`);
  }
  if (actual.length !== actualSet.size) {
    failures.push(`${label} contain duplicate entries`);
  }
}
