import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const FLOW_JISON_SHA256 = 'e9891eb4f646140e5110e72ef5f79b099677a265ba1a84884b39a573b56d1c68';
const FLOW_MARKER = '// src/diagrams/flowchart/parser/flow.jison\n';
const FLOW_END_MARKER = '\nparser.parser = parser;';
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
const flowEntry = fs
  .readdirSync(chunkDirectory)
  .find((name) => /^flowDiagram-.*\.mjs$/.test(name));

if (!flowEntry) {
  throw new Error('Unable to locate the Mermaid Flowchart distribution entry');
}

const entrySource = fs.readFileSync(path.join(chunkDirectory, flowEntry), 'utf8');
const chunkNames = [...entrySource.matchAll(/from "\.\/(chunk-[^"]+\.mjs)"/g)].map(
  (match) => match[1],
);
const flowChunk = chunkNames
  .map((name) => ({
    name,
    source: fs.readFileSync(path.join(chunkDirectory, name), 'utf8'),
  }))
  .find(({ source }) => source.includes(FLOW_MARKER));

if (!flowChunk) {
  throw new Error('Unable to locate the generated Mermaid Flowchart Jison parser');
}

const parserStart = flowChunk.source.indexOf(FLOW_MARKER) + FLOW_MARKER.length;
const parserEnd = flowChunk.source.indexOf(FLOW_END_MARKER, parserStart);
if (parserEnd < 0) {
  throw new Error('Unable to find the end of the generated Flowchart parser');
}

const parserSource = `${flowChunk.source.slice(parserStart, parserEnd)}\nparser`;
const parser = vm.runInNewContext(parserSource, {
  __name: (value) => value,
});

const kotlinString = (value) =>
  JSON.stringify(value).replaceAll('$', '\\$');

const cellSource = (cell) => {
  if (typeof cell === 'number') {
    return `JisonCell.Goto(${cell})`;
  }
  if (!Array.isArray(cell)) {
    throw new Error(`Unsupported Jison table cell: ${JSON.stringify(cell)}`);
  }
  switch (cell[0]) {
    case 1:
      return `JisonCell.Shift(${cell[1]})`;
    case 2:
      return `JisonCell.Reduce(${cell[1]})`;
    case 3:
      return 'JisonCell.Accept';
    default:
      throw new Error(`Unsupported Jison parser action: ${JSON.stringify(cell)}`);
  }
};

const lines = [];
lines.push('package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid');
lines.push('');
lines.push('/**');
lines.push(` * Generated from Mermaid ${MERMAID_VERSION} flow.jison.`);
lines.push(` * Upstream flow.jison SHA-256: ${FLOW_JISON_SHA256}`);
lines.push(` * Distribution chunk SHA-256: ${crypto.createHash('sha256').update(flowChunk.source).digest('hex')}`);
lines.push(' *');
lines.push(' * Do not edit manually. Run:');
lines.push(' *   cd tools/official-reference && npm run generate:flow-parser');
lines.push(' */');
lines.push('internal object FlowJisonTables {');
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
lines.push('    val productions: Array<JisonProduction> = arrayOf(');
for (const production of parser.productions_) {
  if (production === 0) {
    lines.push('        JisonProduction(symbol = 0, length = 0),');
  } else {
    lines.push(
      `        JisonProduction(symbol = ${production[0]}, length = ${production[1]}),`,
    );
  }
}
lines.push('    )');
lines.push('');
const stateChunks = [];
for (let index = 0; index < parser.table.length; index += STATE_CHUNK_SIZE) {
  stateChunks.push(parser.table.slice(index, index + STATE_CHUNK_SIZE));
}
lines.push('    val states: Array<Map<Int, JisonCell>> = buildList {');
for (let index = 0; index < stateChunks.length; index += 1) {
  lines.push(`        addAll(stateChunk${index}())`);
}
lines.push('    }.toTypedArray()');
lines.push('');
for (let chunkIndex = 0; chunkIndex < stateChunks.length; chunkIndex += 1) {
  lines.push(
    `    private fun stateChunk${chunkIndex}(): Array<Map<Int, JisonCell>> = arrayOf(`,
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
for (let index = 0; index < parser.lexer.rules.length; index += LEXER_CHUNK_SIZE) {
  lexerChunks.push(parser.lexer.rules.slice(index, index + LEXER_CHUNK_SIZE));
}
lines.push('    val lexerPatterns: List<JisonLexerPattern> = buildList {');
for (let index = 0; index < lexerChunks.length; index += 1) {
  lines.push(`        addAll(lexerChunk${index}())`);
}
lines.push('    }');
lines.push('');
for (let chunkIndex = 0; chunkIndex < lexerChunks.length; chunkIndex += 1) {
  lines.push(`    private fun lexerChunk${chunkIndex}(): List<JisonLexerPattern> = listOf(`);
  for (let ruleIndex = 0; ruleIndex < lexerChunks[chunkIndex].length; ruleIndex += 1) {
    const absoluteIndex = chunkIndex * LEXER_CHUNK_SIZE + ruleIndex;
    const rule = lexerChunks[chunkIndex][ruleIndex];
    lines.push(`        JisonLexerPattern(${absoluteIndex}, ${kotlinString(rule.source)}),`);
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
  'mermaid-core/src/commonMain/kotlin/com/swithun/cmpmermaid/core/flowchart/upstream/mermaid/FlowJisonTables.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${lines.join('\n')}\n`);
console.log(`Generated ${path.relative(repositoryRoot, output)}`);
