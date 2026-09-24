import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { build } from 'esbuild';

const ZENUML_PLUGIN_VERSION = '1.0.0';
const ZENUML_CORE_VERSION = '3.49.2';
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const pluginRoot = path.join(
  scriptDirectory,
  'node_modules/@mermaid-js/mermaid-zenuml',
);
const pluginPackage = JSON.parse(
  fs.readFileSync(path.join(pluginRoot, 'package.json'), 'utf8'),
);
const corePackage = JSON.parse(
  fs.readFileSync(
    path.join(scriptDirectory, 'node_modules/@zenuml/core/package.json'),
    'utf8',
  ),
);

if (pluginPackage.version !== ZENUML_PLUGIN_VERSION) {
  throw new Error(
    `Expected @mermaid-js/mermaid-zenuml@${ZENUML_PLUGIN_VERSION}, ` +
      `found ${pluginPackage.version}`,
  );
}
if (corePackage.version !== ZENUML_CORE_VERSION) {
  throw new Error(
    `Expected @zenuml/core@${ZENUML_CORE_VERSION}, found ${corePackage.version}`,
  );
}

const outputPath = path.join(
  repositoryRoot,
  'mermaid-debug-ui/src/androidMain/assets/' +
    `mermaid-zenuml-${ZENUML_PLUGIN_VERSION}.min.js`,
);

await build({
  stdin: {
    contents: `
      import zenUmlDiagram from '@mermaid-js/mermaid-zenuml';
      globalThis.cmpMermaidZenUmlDiagram = zenUmlDiagram;
    `,
    resolveDir: scriptDirectory,
    sourcefile: 'register-zenuml.mjs',
  },
  bundle: true,
  format: 'iife',
  platform: 'browser',
  target: ['es2020'],
  minify: true,
  legalComments: 'inline',
  outfile: outputPath,
});

const output = fs.readFileSync(outputPath);
const sha256 = crypto.createHash('sha256').update(output).digest('hex');
console.log(
  `Generated ${path.relative(repositoryRoot, outputPath)} ` +
    `(${output.length} bytes, SHA-256 ${sha256})`,
);
