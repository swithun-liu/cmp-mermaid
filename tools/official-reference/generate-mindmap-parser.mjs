import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const MINDMAP_JISON_SHA256 =
  '1114fbccc641f2ad59a56eec452e21dfb947d78283adf571a6ce24b09dce6993';
const MINDMAP_MARKER = '// src/diagrams/mindmap/parser/mindmap.jison\n';
const MINDMAP_END_MARKER = '\nparser.parser = parser;';
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

const sourceRoot = process.env.MERMAID_SOURCE_DIR;
if (sourceRoot) {
  const grammarPath = path.join(
    sourceRoot,
    'packages/mermaid/src/diagrams/mindmap/parser/mindmap.jison',
  );
  const grammarHash = crypto
    .createHash('sha256')
    .update(fs.readFileSync(grammarPath))
    .digest('hex');
  if (grammarHash !== MINDMAP_JISON_SHA256) {
    throw new Error(
      `Expected Mindmap grammar ${MINDMAP_JISON_SHA256}, found ${grammarHash}`,
    );
  }
}

const chunkDirectory = path.join(mermaidRoot, 'dist/chunks/mermaid.core');
const mindmapEntry = fs
  .readdirSync(chunkDirectory)
  .find((name) => /^mindmap-definition-.*\.mjs$/.test(name));

if (!mindmapEntry) {
  throw new Error('Unable to locate the Mermaid Mindmap distribution entry');
}

const mindmapChunk = fs.readFileSync(
  path.join(chunkDirectory, mindmapEntry),
  'utf8',
);
const parserStart =
  mindmapChunk.indexOf(MINDMAP_MARKER) + MINDMAP_MARKER.length;
if (parserStart < MINDMAP_MARKER.length) {
  throw new Error('Unable to locate the generated Mermaid Mindmap Jison parser');
}
const parserEnd = mindmapChunk.indexOf(MINDMAP_END_MARKER, parserStart);
if (parserEnd < 0) {
  throw new Error('Unable to find the end of the generated Mindmap parser');
}

const parserSource = `${mindmapChunk.slice(parserStart, parserEnd)}\nparser`;
const parser = vm.runInNewContext(parserSource, {
  __name: (value) => value,
});

const kotlinString = (value) =>
  JSON.stringify(value).replaceAll('$', '\\$');

const cellSource = (cell) => {
  if (typeof cell === 'number') {
    return `MindmapJisonCell.Goto(${cell})`;
  }
  if (!Array.isArray(cell)) {
    throw new Error(`Unsupported Jison table cell: ${JSON.stringify(cell)}`);
  }
  switch (cell[0]) {
    case 1:
      return `MindmapJisonCell.Shift(${cell[1]})`;
    case 2:
      return `MindmapJisonCell.Reduce(${cell[1]})`;
    case 3:
      return 'MindmapJisonCell.Accept';
    default:
      throw new Error(`Unsupported Jison parser action: ${JSON.stringify(cell)}`);
  }
};

const lines = [];
lines.push('package com.swithun.cmpmermaid.core.mindmap.upstream.mermaid');
lines.push('');
lines.push('/**');
lines.push(` * Generated from Mermaid ${MERMAID_VERSION} mindmap.jison.`);
lines.push(` * Upstream mindmap.jison SHA-256: ${MINDMAP_JISON_SHA256}`);
lines.push(
  ` * Distribution chunk SHA-256: ${crypto.createHash('sha256').update(mindmapChunk).digest('hex')}`,
);
lines.push(' *');
lines.push(' * Do not edit manually. Run:');
lines.push(' *   cd tools/official-reference && npm run generate:mindmap-parser');
lines.push(' */');
lines.push('internal object MindmapJisonTables {');
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
lines.push('    val productions: Array<MindmapJisonProduction> = arrayOf(');
for (const production of parser.productions_) {
  if (production === 0) {
    lines.push('        MindmapJisonProduction(symbol = 0, length = 0),');
  } else {
    lines.push(
      `        MindmapJisonProduction(symbol = ${production[0]}, length = ${production[1]}),`,
    );
  }
}
lines.push('    )');
lines.push('');
const stateChunks = [];
for (let index = 0; index < parser.table.length; index += STATE_CHUNK_SIZE) {
  stateChunks.push(parser.table.slice(index, index + STATE_CHUNK_SIZE));
}
lines.push('    val states: Array<Map<Int, MindmapJisonCell>> = buildList {');
for (let index = 0; index < stateChunks.length; index += 1) {
  lines.push(`        addAll(stateChunk${index}())`);
}
lines.push('    }.toTypedArray()');
lines.push('');
for (let chunkIndex = 0; chunkIndex < stateChunks.length; chunkIndex += 1) {
  lines.push(
    `    private fun stateChunk${chunkIndex}(): Array<Map<Int, MindmapJisonCell>> = arrayOf(`,
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
  lexerChunks.push(parser.lexer.rules.slice(index, index + LEXER_CHUNK_SIZE));
}
lines.push('    val lexerPatterns: List<MindmapJisonLexerPattern> = buildList {');
for (let index = 0; index < lexerChunks.length; index += 1) {
  lines.push(`        addAll(lexerChunk${index}())`);
}
lines.push('    }');
lines.push('');
for (let chunkIndex = 0; chunkIndex < lexerChunks.length; chunkIndex += 1) {
  lines.push(
    `    private fun lexerChunk${chunkIndex}(): List<MindmapJisonLexerPattern> = listOf(`,
  );
  for (
    let ruleIndex = 0;
    ruleIndex < lexerChunks[chunkIndex].length;
    ruleIndex += 1
  ) {
    const absoluteIndex = chunkIndex * LEXER_CHUNK_SIZE + ruleIndex;
    const rule = lexerChunks[chunkIndex][ruleIndex];
    lines.push(
      `        MindmapJisonLexerPattern(${absoluteIndex}, ${kotlinString(rule.source)}),`,
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
  'mermaid-core/src/commonMain/kotlin/com/swithun/cmpmermaid/core/mindmap/upstream/mermaid/MindmapJisonTables.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${lines.join('\n')}\n`);
console.log(`Generated ${path.relative(repositoryRoot, output)}`);
