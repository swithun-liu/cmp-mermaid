import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ELKJS_VERSION = '0.9.3';
const CHUNK_LENGTH = 16_000;
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const packageJsonPath = path.join(scriptDirectory, 'node_modules/elkjs/package.json');
const workerPath = path.join(scriptDirectory, 'node_modules/elkjs/lib/elk-worker.min.js');
const outputPath = path.resolve(
  scriptDirectory,
  '../../mermaid-core/src/commonMain/kotlin/io/github/cmpmermaid/core/flowchart/upstream/elk/ElkWorkerSource.kt',
);

const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
if (packageJson.version !== ELKJS_VERSION) {
  throw new Error(
    `Expected elkjs ${ELKJS_VERSION}, resolved ${packageJson.version}. ` +
      'Review the Mermaid dependency before regenerating.',
  );
}

const worker = fs.readFileSync(workerPath, 'utf8');
const workerSha256 = crypto.createHash('sha256').update(worker).digest('hex');
const source = [
  'var global = globalThis;',
  'var self = globalThis;',
  'var console = globalThis.console || {log:function(){},info:function(){},warn:function(){},error:function(){}};',
  worker,
  [
    "SId(['layered','stress','mrtree','radial','force','disco','sporeOverlap','sporeCompaction','rectpacking']);",
    'globalThis.__cmpMermaidElkLayout = function(input) {',
    '  var graph = JSON.parse(input);',
    '  QId(graph, {}, {});',
    '  return JSON.stringify(graph);',
    '};',
    'void 0;',
  ].join('\n'),
].join('\n');

const chunks = [];
for (let offset = 0; offset < source.length; offset += CHUNK_LENGTH) {
  chunks.push(source.slice(offset, offset + CHUNK_LENGTH));
}

const kotlinString = (value) =>
  JSON.stringify(value).replaceAll('$', () => '\\$');

const kotlin = `package io.github.cmpmermaid.core.flowchart.upstream.elk

/**
 * Generated from the elkjs ${ELKJS_VERSION} dependency resolved by Mermaid 12.0.0.
 * elk-worker.min.js SHA-256: ${workerSha256}
 *
 * Do not edit manually. Run:
 *   cd tools/official-reference && npm run generate:elk-worker
 */
internal object ElkWorkerSource {
    const val VERSION: String = "${ELKJS_VERSION}"

    val source: String by lazy {
        buildString(${source.length}) {
${chunks.map((chunk) => `            append(${kotlinString(chunk)})`).join('\n')}
        }
    }
}
`;

fs.mkdirSync(path.dirname(outputPath), { recursive: true });
fs.writeFileSync(outputPath, kotlin);
console.log(
  `Generated ${path.relative(process.cwd(), outputPath)} ` +
    `(${chunks.length} chunks, ${source.length} characters)`,
);
