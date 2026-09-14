import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const MARKED_VERSION = '16.4.2';
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const markedRoot = path.join(
  scriptDirectory,
  'node_modules/mermaid/node_modules/marked',
);
const markedPackage = JSON.parse(
  fs.readFileSync(path.join(markedRoot, 'package.json'), 'utf8'),
);
if (markedPackage.version !== MARKED_VERSION) {
  throw new Error(
    `Expected marked@${MARKED_VERSION}, found ${markedPackage.version}`,
  );
}

const { marked } = await import(
  pathToFileURL(path.join(markedRoot, 'lib/marked.esm.js'))
);

const cases = [
  ['basic', 'This **is** _Markdown_'],
  ['intraword_underscore', 'a_b_c foo_bar'],
  ['nested_emphasis', '***both*** and __a _b_ c__'],
  [
    'links_and_image',
    'before [label](https://example.com/a_(b) "title") and ![alt](image.png) after',
  ],
  ['code_and_delete', '`code` and ~~gone~~'],
  ['inline_html', '<strong>bold</strong> <em>x</em><br/>next'],
  ['block_tokens', '# heading\n\n- one\n- two\n\nparagraph'],
  ['escape_and_entity', '\\*literal\\* &amp;'],
  ['line_breaks', 'first  \nsecond\nthird'],
  ['reference_link', '[foo][id]\n\n[id]: /url "title"'],
  ['punctuation_emphasis', 'foo***bar***baz ___x___ #*z*'],
];

const kotlinString = (value) =>
  JSON.stringify(value).replaceAll('$', () => '\\$');

const projectedChildren = (token) => {
  if (!['paragraph', 'strong', 'em'].includes(token.type)) {
    return [];
  }
  return (token.tokens ?? []).map(projectToken);
};

const projectToken = (token) => ({
  type: token.type,
  raw: token.raw,
  text: token.text ?? '',
  tokens: projectedChildren(token),
});

const tokenSource = (token, indent) => {
  const padding = ' '.repeat(indent);
  const lines = [
    `${padding}MarkedFixtureToken(`,
    `${padding}    type = ${kotlinString(token.type)},`,
    `${padding}    raw = ${kotlinString(token.raw)},`,
    `${padding}    text = ${kotlinString(token.text)},`,
  ];
  if (token.tokens.length > 0) {
    lines.push(`${padding}    tokens = listOf(`);
    token.tokens.forEach((child) => {
      lines.push(tokenSource(child, indent + 8));
    });
    lines.push(`${padding}    ),`);
  }
  lines.push(`${padding}),`);
  return lines.join('\n');
};

const lines = [
  'package com.swithun.cmpmermaid.core.flowchart.upstream.marked',
  '',
  '/**',
  ` * Generated with Marked ${MARKED_VERSION}.`,
  ' *',
  ' * Do not edit manually. Run:',
  ' *   cd tools/official-reference && npm run generate:marked-fixtures',
  ' */',
  'internal object MarkedFixtures {',
  '    val cases: List<MarkedFixtureCase> = listOf(',
];

for (const [id, source] of cases) {
  const tokens = marked.lexer(source).map(projectToken);
  lines.push('        MarkedFixtureCase(');
  lines.push(`            id = ${kotlinString(id)},`);
  lines.push(`            source = ${kotlinString(source)},`);
  lines.push('            tokens = listOf(');
  tokens.forEach((token) => {
    lines.push(tokenSource(token, 16));
  });
  lines.push('            ),');
  lines.push('        ),');
}

lines.push('    )');
lines.push('}');
lines.push('');
lines.push('internal data class MarkedFixtureCase(');
lines.push('    val id: String,');
lines.push('    val source: String,');
lines.push('    val tokens: List<MarkedFixtureToken>,');
lines.push(')');
lines.push('');
lines.push('internal data class MarkedFixtureToken(');
lines.push('    val type: String,');
lines.push('    val raw: String,');
lines.push('    val text: String,');
lines.push('    val tokens: List<MarkedFixtureToken> = emptyList(),');
lines.push(')');

const output = path.join(
  repositoryRoot,
  'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/flowchart/upstream/marked/MarkedFixtures.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${lines.join('\n')}\n`);
console.log(`Generated ${path.relative(repositoryRoot, output)}`);
