import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import { fileURLToPath } from 'node:url';

// Independent oracle: read official JavaScript objects, without importing the
// Kotlin emitter or deriving expected values from generated Kotlin sources.
const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../..');
const mermaid = path.join(directory, 'node_modules/mermaid');
const version = JSON.parse(fs.readFileSync(path.join(mermaid, 'package.json'))).version;
if (version !== '12.0.0') throw new Error(`Unexpected Mermaid version ${version}`);
const grammars = {
  agentflow: 'agentflow/parser/agentflow.jison',
  block: 'block/parser/block.jison',
  class: 'class/parser/classDiagram.jison',
  er: 'er/parser/erDiagram.jison',
  flow: 'flowchart/parser/flow.jison',
  gantt: 'gantt/parser/gantt.jison',
  journey: 'user-journey/parser/journey.jison',
  mindmap: 'mindmap/parser/mindmap.jison',
  requirement: 'requirement/parser/requirementDiagram.jison',
  sequence: 'sequence/parser/sequenceDiagram.jison',
  state: 'state/parser/stateDiagram.jison',
  xychart: 'xychart/parser/xychart.jison',
};
const chunkDirectory = path.join(mermaid, 'dist/chunks/mermaid.core');
const chunks = fs.readdirSync(chunkDirectory).filter((name) => name.endsWith('.mjs'))
  .map((name) => ({ name, source: fs.readFileSync(path.join(chunkDirectory, name), 'utf8') }));
const output = path.join(root, 'mermaid-core/src/jvmTest/resources/jison-tables');
fs.mkdirSync(output, { recursive: true });
const manifest = { mermaidVersion: version, families: [] };
for (const [family, grammar] of Object.entries(grammars)) {
  const marker = `// src/diagrams/${grammar}\n`;
  const matches = chunks.filter(({ source }) => source.includes(marker));
  if (matches.length !== 1) throw new Error(`${family}: expected one official parser`);
  const { name, source } = matches[0];
  const start = source.indexOf(marker) + marker.length;
  const end = source.indexOf('\nparser.parser = parser;', start);
  if (end < 0) throw new Error(`${family}: missing parser end`);
  const parser = vm.runInNewContext(`${source.slice(start, end)}\nparser`, {
    __name: (value) => value,
  });
  let entries = 0;
  const rows = parser.table.map((state) => Object.entries(state).map(([symbol, cell]) => {
    entries += 1;
    if (typeof cell === 'number') return `${symbol}:g:${cell}`;
    if (!Array.isArray(cell)) throw new Error(`${family}: unknown cell`);
    if (cell[0] === 1) return `${symbol}:s:${cell[1]}`;
    if (cell[0] === 2) return `${symbol}:r:${cell[1]}`;
    if (cell[0] === 3) return `${symbol}:a:0`;
    throw new Error(`${family}: unknown action ${cell}`);
  }).join(';'));
  const text = `${rows.join('\n')}\n`;
  fs.writeFileSync(path.join(output, `${family}.rows`), text);
  manifest.families.push({
    family, grammar, distributionChunk: name,
    distributionSha256: crypto.createHash('sha256').update(source).digest('hex'),
    states: rows.length, entries,
    oracleSha256: crypto.createHash('sha256').update(text).digest('hex'),
  });
}
fs.writeFileSync(path.join(output, 'manifest.json'), `${JSON.stringify(manifest, null, 2)}\n`);
console.log(JSON.stringify(manifest.families.map(({ family, states, entries }) => ({
  family, states, entries,
}))));
