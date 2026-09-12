import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const MARKED_VERSION = '16.4.2';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const mermaidRoot = path.join(scriptDirectory, 'node_modules/mermaid');
const markedRoot = path.join(mermaidRoot, 'node_modules/marked');
const mermaidPackage = JSON.parse(
  fs.readFileSync(path.join(mermaidRoot, 'package.json'), 'utf8'),
);
const markedPackage = JSON.parse(
  fs.readFileSync(path.join(markedRoot, 'package.json'), 'utf8'),
);

if (mermaidPackage.version !== MERMAID_VERSION) {
  throw new Error(
    `Expected mermaid@${MERMAID_VERSION}, found ${mermaidPackage.version}`,
  );
}
if (markedPackage.version !== MARKED_VERSION) {
  throw new Error(
    `Expected marked@${MARKED_VERSION}, found ${markedPackage.version}`,
  );
}

const markedEntry = path.join(markedRoot, 'lib/marked.esm.js');
const { Lexer } = await import(pathToFileURL(markedEntry));
const markedSource = fs.readFileSync(markedEntry, 'utf8');

const kotlinString = (value) =>
  `"""${value.replaceAll('$', () => "${'$'}")}"""`;

const emitRules = (lines, name, rules) => {
  lines.push(`    val ${name}: Map<String, MarkedRegexSource> = mapOf(`);
  for (const [ruleName, regex] of Object.entries(rules)) {
    const ignoreCase = regex.flags.includes('i');
    lines.push(
      `        ${JSON.stringify(ruleName)} to MarkedRegexSource(${kotlinString(regex.source)}, ignoreCase = ${ignoreCase}),`,
    );
  }
  lines.push('    )');
};

const lines = [
  'package io.github.cmpmermaid.core.flowchart.upstream.marked',
  '',
  '/**',
  ` * Generated from the Marked ${MARKED_VERSION} dependency resolved by Mermaid ${MERMAID_VERSION}.`,
  ` * marked.esm.js SHA-256: ${crypto.createHash('sha256').update(markedSource).digest('hex')}`,
  ' *',
  ' * Do not edit manually. Run:',
  ' *   cd tools/official-reference && npm run generate:marked-rules',
  ' */',
  'internal object MarkedGeneratedRules {',
  `    const val VERSION: String = "${MARKED_VERSION}"`,
  '',
];
emitRules(lines, 'block', Lexer.rules.block.gfm);
lines.push('');
emitRules(lines, 'inline', Lexer.rules.inline.gfm);
lines.push('}');

const output = path.join(
  repositoryRoot,
  'mermaid-core/src/commonMain/kotlin/io/github/cmpmermaid/core/flowchart/upstream/marked/MarkedGeneratedRules.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${lines.join('\n')}\n`);
console.log(`Generated ${path.relative(repositoryRoot, output)}`);
