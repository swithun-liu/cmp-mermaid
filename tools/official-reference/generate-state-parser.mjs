import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const STATE_JISON_SHA256 =
  '94d4715707c3b35192b8586fa271e843f2c084e9ff70dd8db07c510c1a23e231';
const STATE_MARKER =
  '// src/diagrams/state/parser/stateDiagram.jison\n';
const STATE_END_MARKER = '\nparser.parser = parser;';
const STATE_CHUNK_SIZE = 20;
const LEXER_CHUNK_SIZE = 20;

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const mermaidRoot = path.join(scriptDirectory, 'node_modules/mermaid');
const mermaidPackage = JSON.parse(
  fs.readFileSync(path.join(mermaidRoot, 'package.json'), 'utf8'),
);

if (mermaidPackage.version !== MERMAID_VERSION) {
  throw new Error(
    `Expected mermaid@${MERMAID_VERSION}, found ${mermaidPackage.version}`,
  );
}

const chunkDirectory = path.join(mermaidRoot, 'dist/chunks/mermaid.core');
const stateEntry = fs
  .readdirSync(chunkDirectory)
  .find((name) => /^stateDiagram-v2-.*\.mjs$/.test(name));

if (!stateEntry) {
  throw new Error('Unable to locate the Mermaid State distribution entry');
}

const stateChunk = fs.readFileSync(
  path.join(chunkDirectory, stateEntry),
  'utf8',
);
const parserStart = stateChunk.indexOf(STATE_MARKER) + STATE_MARKER.length;
if (parserStart < STATE_MARKER.length) {
  throw new Error('Unable to locate the generated Mermaid State Jison parser');
}
const parserEnd = stateChunk.indexOf(STATE_END_MARKER, parserStart);
if (parserEnd < 0) {
  throw new Error('Unable to find the end of the generated State parser');
}

const parserSource = `${stateChunk.slice(parserStart, parserEnd)}\nparser`;
const parser = vm.runInNewContext(parserSource, {
  __name: (value) => value,
});

const kotlinString = (value) =>
  JSON.stringify(value).replaceAll('$', '\\$');

const cellSource = (cell) => {
  if (typeof cell === 'number') {
    return `StateJisonCell.Goto(${cell})`;
  }
  if (!Array.isArray(cell)) {
    throw new Error(`Unsupported Jison table cell: ${JSON.stringify(cell)}`);
  }
  switch (cell[0]) {
    case 1:
      return `StateJisonCell.Shift(${cell[1]})`;
    case 2:
      return `StateJisonCell.Reduce(${cell[1]})`;
    case 3:
      return 'StateJisonCell.Accept';
    default:
      throw new Error(`Unsupported Jison parser action: ${JSON.stringify(cell)}`);
  }
};

const lines = [];
lines.push('package io.github.cmpmermaid.core.statediagram.upstream.mermaid');
lines.push('');
lines.push('/**');
lines.push(
  ` * Generated from Mermaid ${MERMAID_VERSION} stateDiagram.jison.`,
);
lines.push(` * Upstream stateDiagram.jison SHA-256: ${STATE_JISON_SHA256}`);
lines.push(
  ` * Distribution chunk SHA-256: ${crypto.createHash('sha256').update(stateChunk).digest('hex')}`,
);
lines.push(' *');
lines.push(' * Do not edit manually. Run:');
lines.push(' *   cd tools/official-reference && npm run generate:state-parser');
lines.push(' */');
lines.push('internal object StateJisonTables {');
lines.push('    val symbolIds: Map<String, Int> = mapOf(');
for (const [name, id] of Object.entries(parser.symbols_)) {
  lines.push(`        ${kotlinString(name)} to ${id},`);
}
lines.push('    )');
lines.push('');
lines.push('    val terminalNames: Map<Int, String> = mapOf(');
for (const [id, name] of Object.entries(parser.terminals_)) {
  lines.push(`        ${id} to ${kotlinString(name)},`);
}
lines.push('    )');
lines.push('');
lines.push(
  '    val productions: Array<StateJisonProduction> = arrayOf(',
);
for (const production of parser.productions_) {
  if (production === 0) {
    lines.push(
      '        StateJisonProduction(symbol = 0, length = 0),',
    );
  } else {
    lines.push(
      `        StateJisonProduction(symbol = ${production[0]}, length = ${production[1]}),`,
    );
  }
}
lines.push('    )');
lines.push('');
const stateChunks = [];
for (let index = 0; index < parser.table.length; index += STATE_CHUNK_SIZE) {
  stateChunks.push(parser.table.slice(index, index + STATE_CHUNK_SIZE));
}
lines.push(
  '    val states: Array<Map<Int, StateJisonCell>> = buildList {',
);
for (let index = 0; index < stateChunks.length; index += 1) {
  lines.push(`        addAll(stateChunk${index}())`);
}
lines.push('    }.toTypedArray()');
lines.push('');
for (let chunkIndex = 0; chunkIndex < stateChunks.length; chunkIndex += 1) {
  lines.push(
    `    private fun stateChunk${chunkIndex}(): Array<Map<Int, StateJisonCell>> = arrayOf(`,
  );
  for (const state of stateChunks[chunkIndex]) {
    const entries = Object.entries(state);
    if (entries.length === 0) {
      lines.push('        emptyMap(),');
      continue;
    }
    lines.push('        mapOf(');
    for (const [symbol, cell] of entries) {
      lines.push(`            ${symbol} to ${cellSource(cell)},`);
    }
    lines.push('        ),');
  }
  lines.push('    )');
  lines.push('');
}
const lexerChunks = [];
for (
  let index = 0;
  index < parser.lexer.rules.length;
  index += LEXER_CHUNK_SIZE
) {
  lexerChunks.push(
    parser.lexer.rules.slice(index, index + LEXER_CHUNK_SIZE),
  );
}
lines.push(
  '    val lexerPatterns: List<StateJisonLexerPattern> = buildList {',
);
for (let index = 0; index < lexerChunks.length; index += 1) {
  lines.push(`        addAll(lexerChunk${index}())`);
}
lines.push('    }');
lines.push('');
for (let chunkIndex = 0; chunkIndex < lexerChunks.length; chunkIndex += 1) {
  lines.push(
    `    private fun lexerChunk${chunkIndex}(): List<StateJisonLexerPattern> = listOf(`,
  );
  for (
    let ruleIndex = 0;
    ruleIndex < lexerChunks[chunkIndex].length;
    ruleIndex += 1
  ) {
    const absoluteIndex = chunkIndex * LEXER_CHUNK_SIZE + ruleIndex;
    const rule = lexerChunks[chunkIndex][ruleIndex];
    lines.push(
      `        StateJisonLexerPattern(${absoluteIndex}, ${kotlinString(rule.source)}),`,
    );
  }
  lines.push('    )');
  lines.push('');
}
lines.push('    val lexerConditions: Map<String, IntArray> = mapOf(');
for (const [name, condition] of Object.entries(parser.lexer.conditions)) {
  lines.push(
    `        ${kotlinString(name)} to intArrayOf(${condition.rules.join(', ')}),`,
  );
}
lines.push('    )');
lines.push('}');

const output = path.join(
  repositoryRoot,
  'mermaid-core/src/commonMain/kotlin/io/github/cmpmermaid/core/statediagram/upstream/mermaid/StateJisonTables.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${lines.join('\n')}\n`);
console.log(`Generated ${path.relative(repositoryRoot, output)}`);
