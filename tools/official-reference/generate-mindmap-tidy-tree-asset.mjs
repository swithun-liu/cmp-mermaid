import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { build } from 'esbuild';

const TIDY_TREE_VERSION = '1.0.0';
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const packageRoot = path.join(
  scriptDirectory,
  'node_modules/@mermaid-js/layout-tidy-tree',
);
const packageJson = JSON.parse(
  fs.readFileSync(path.join(packageRoot, 'package.json'), 'utf8'),
);

if (packageJson.version !== TIDY_TREE_VERSION) {
  throw new Error(
    `Expected @mermaid-js/layout-tidy-tree@${TIDY_TREE_VERSION}, ` +
      `found ${packageJson.version}`,
  );
}

const outputPath = path.join(
  repositoryRoot,
  'mermaid-debug-ui/src/androidMain/assets/' +
    `mermaid-layout-tidy-tree-${TIDY_TREE_VERSION}.min.js`,
);

await build({
  stdin: {
    contents: `
      import layouts from '@mermaid-js/layout-tidy-tree';
      globalThis.cmpMermaidTidyTreeLayouts = layouts;
    `,
    resolveDir: scriptDirectory,
    sourcefile: 'register-mindmap-tidy-tree.mjs',
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
