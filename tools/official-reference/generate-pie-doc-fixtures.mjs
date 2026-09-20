import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const mermaidSourceRoot = process.env.MERMAID_SOURCE_DIR;

if (!mermaidSourceRoot) {
  throw new Error('Set MERMAID_SOURCE_DIR to a Mermaid source checkout');
}

const packageFile = path.join(mermaidSourceRoot, 'packages/mermaid/package.json');
const packageMetadata = JSON.parse(fs.readFileSync(packageFile, 'utf8'));
if (packageMetadata.version !== MERMAID_VERSION) {
  throw new Error(
    `Expected Mermaid source ${MERMAID_VERSION}, found ${packageMetadata.version}`,
  );
}

const documentationFile = path.join(
  mermaidSourceRoot,
  'packages/mermaid/src/docs/syntax/pie.md',
);
const documentation = fs.readFileSync(documentationFile, 'utf8');
const documentationHash = crypto
  .createHash('sha256')
  .update(documentation)
  .digest('hex');
const cases = [];
let heading = 'Pie chart diagrams';
let fenceLanguage = '';
let fenceLines = null;

for (const line of documentation.split(/\r?\n/)) {
  if (fenceLines === null) {
    if (/^#{1,4}\s/.test(line)) {
      heading = line.replace(/^#{1,4}\s+/, '').trim();
    }
    if (line.startsWith('```')) {
      fenceLanguage = line.slice(3).trim();
      fenceLines = [];
    }
    continue;
  }

  if (line === '```') {
    const source = fenceLines.join('\n').trim();
    const hasDiagramHeader = source
      .split(/\r?\n/)
      .map((candidate) => candidate.trim())
      .some((candidate) => /^pie\b/.test(candidate));
    if (
      /^(mermaid|mermaid-example)$/.test(fenceLanguage) &&
      hasDiagramHeader
    ) {
      cases.push({
        id: `${String(cases.length + 1).padStart(3, '0')}_${slug(heading)}`,
        title: heading,
        source,
      });
    }
    fenceLanguage = '';
    fenceLines = null;
    continue;
  }

  fenceLines.push(line);
}

const kotlinCases = cases
  .map(({ id, title, source }) => `    MermaidPieDocCase(
        id = ${kotlinString(id)},
        title = ${kotlinString(title)},
        source = ${kotlinRawString(source)},
    )`)
  .join(',\n');

const output = path.join(
  repositoryRoot,
  'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/OfficialPieDocumentationCases.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(
  output,
  `package com.swithun.cmpmermaid.core

/**
 * Generated from Mermaid ${MERMAID_VERSION}
 * packages/mermaid/src/docs/syntax/pie.md.
 * Upstream document SHA-256: ${documentationHash}
 *
 * Do not edit manually. Run:
 *   MERMAID_SOURCE_DIR=/path/to/mermaid npm run generate:pie-doc-fixtures
 */
internal data class MermaidPieDocCase(
    val id: String,
    val title: String,
    val source: String,
)

internal val officialPieDocumentationCases: List<MermaidPieDocCase> = listOf(
${kotlinCases},
)
`,
);

console.log(
  `Generated ${cases.length} Mermaid ${MERMAID_VERSION} Pie documentation fixtures.`,
);

function slug(value) {
  const normalized = value
    .normalize('NFKD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '');
  return normalized || 'example';
}

function kotlinString(value) {
  return JSON.stringify(value).replaceAll('$', '\\$');
}

function kotlinRawString(value) {
  if (value.includes('"""')) {
    throw new Error('Pie fixture contains a Kotlin raw-string delimiter');
  }
  return `"""
${value.replaceAll('$', "${'$'}")}
        """.trimIndent()`;
}
