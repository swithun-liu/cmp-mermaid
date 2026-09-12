import { mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
import { cases } from './cases.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const output = resolve(root, '../../sample/androidApp/src/main/res/drawable-nodpi');
const kotlinOutput = resolve(
  root,
  '../../sample/androidApp/src/main/kotlin/io/github/cmpmermaid/sample/generated/OfficialDemoCatalog.kt',
);
const testOutput = resolve(
  root,
  '../../mermaid-core/src/commonTest/kotlin/io/github/cmpmermaid/core/OfficialFlowchartCases.kt',
);
const temporary = resolve(root, '.cache');
const cli = resolve(root, 'node_modules/.bin/mmdc');
const mermaidPackage = JSON.parse(
  readFileSync(resolve(root, 'node_modules/mermaid/package.json'), 'utf8'),
);

if (mermaidPackage.version !== '12.0.0') {
  throw new Error(`Expected mermaid 12.0.0, found ${mermaidPackage.version}`);
}

mkdirSync(output, { recursive: true });
rmSync(temporary, { recursive: true, force: true });
mkdirSync(temporary, { recursive: true });
const config = resolve(temporary, 'mermaid-config.json');
writeFileSync(
  config,
  JSON.stringify({
    layout: 'dagre',
    flowchart: {
      curve: 'rounded',
    },
  }),
);
const dimensions = new Map();

for (const demo of cases) {
  const input = resolve(temporary, `${demo.id}.mmd`);
  const target = resolve(output, `official_${demo.id}.png`);
  writeFileSync(input, `${demo.source}\n`);
  const result = spawnSync(
    cli,
    [
      '--input',
      input,
      '--output',
      target,
      '--configFile',
      config,
      '--backgroundColor',
      'white',
      '--width',
      '1000',
      '--scale',
      '1',
      '--quiet',
    ],
    { cwd: root, encoding: 'utf8' },
  );
  if (result.status !== 0) {
    throw new Error(
      `Official render failed for ${demo.id}\n${result.stdout}\n${result.stderr}`,
    );
  }
  const png = readFileSync(target);
  dimensions.set(demo.id, {
    width: png.readUInt32BE(16),
    height: png.readUInt32BE(20),
  });
}

const kotlinCases = cases
  .map((demo) => {
    const source = demo.source.replaceAll('$', "${'$'}");
    const size = dimensions.get(demo.id);
    return `    FlowchartDemo(
        id = "${demo.id}",
        title = "${demo.title}",
        category = "${demo.category}",
        source = """
${source}
        """.trimIndent(),
        officialDrawable = R.drawable.official_${demo.id},
        officialAspectRatio = ${size.width}f / ${size.height}f,
    )`;
  })
  .join(',\n');

mkdirSync(dirname(kotlinOutput), { recursive: true });
writeFileSync(
  kotlinOutput,
  `package io.github.cmpmermaid.sample.generated

import io.github.cmpmermaid.sample.R

internal data class FlowchartDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
    val officialDrawable: Int,
    val officialAspectRatio: Float,
)

internal val flowchartDemos: List<FlowchartDemo> = listOf(
${kotlinCases},
)
`,
);

const testCases = cases
  .map((demo) => {
    const source = demo.source.replaceAll('$', "${'$'}");
    return `    OfficialFlowchartCase(
        id = "${demo.id}",
        source = """
${source}
        """.trimIndent(),
    )`;
  })
  .join(',\n');

mkdirSync(dirname(testOutput), { recursive: true });
writeFileSync(
  testOutput,
  `package io.github.cmpmermaid.core

internal data class OfficialFlowchartCase(
    val id: String,
    val source: String,
)

internal val officialFlowchartCases: List<OfficialFlowchartCase> = listOf(
${testCases},
)
`,
);

console.log(`Rendered ${cases.length} Mermaid ${mermaidPackage.version} references.`);
