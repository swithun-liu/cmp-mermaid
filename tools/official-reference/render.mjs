import { mkdirSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { transform } from 'esbuild';
import puppeteer from 'puppeteer';
import { cases } from './cases.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const output = resolve(root, '../../captures/local/flowchart-official');
const kotlinOutput = resolve(
  root,
  '../../mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/generated/OfficialDemoCatalog.kt',
);
const testOutput = resolve(
  root,
  '../../mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/OfficialFlowchartCases.kt',
);
const androidAssetOutput = resolve(
  root,
  '../../mermaid-debug-ui/src/androidMain/assets',
);
const temporary = resolve(root, '.cache');
const mermaidBundle = resolve(root, 'node_modules/mermaid/dist/mermaid.min.js');
const mermaidPackage = JSON.parse(
  readFileSync(resolve(root, 'node_modules/mermaid/package.json'), 'utf8'),
);

if (mermaidPackage.version !== '12.0.0') {
  throw new Error(`Expected mermaid 12.0.0, found ${mermaidPackage.version}`);
}

mkdirSync(output, { recursive: true });
mkdirSync(androidAssetOutput, { recursive: true });
const androidMermaidBundle = await transform(
  readFileSync(mermaidBundle, 'utf8'),
  {
    loader: 'js',
    target: 'chrome87',
    minify: true,
    legalComments: 'inline',
  },
);
writeFileSync(
  resolve(androidAssetOutput, `mermaid-${mermaidPackage.version}.min.js`),
  androidMermaidBundle.code,
);
rmSync(temporary, { recursive: true, force: true });
mkdirSync(temporary, { recursive: true });
const dimensions = new Map();

const browser = await puppeteer.launch({ headless: 'shell' });
try {
  const page = await browser.newPage();
  await page.setViewport({ width: 1000, height: 1000, deviceScaleFactor: 1 });
  await page.setContent(
    '<style>body{margin:0;background:white}svg{display:block}</style>' +
      '<div id="container"></div>',
  );
  await page.addScriptTag({ path: mermaidBundle });
  await page.evaluate(() => {
    mermaid.initialize({
      startOnLoad: false,
      layout: 'elk',
    });
  });

  for (const demo of cases) {
    const target = resolve(output, `official_${demo.id}.png`);
    await page.evaluate(
      async ({ id, source }) => {
        const container = document.querySelector('#container');
        container.replaceChildren();
        const { svg } = await mermaid.render(`official-${id}`, source, container);
        container.innerHTML = svg;
        container.querySelector('svg').style.backgroundColor = 'white';
      },
      demo,
    );
    const svg = await page.$('#container > svg');
    if (svg === null) {
      throw new Error(
        `Official Mermaid ${mermaidPackage.version} produced no SVG for ${demo.id}`,
      );
    }
    await svg.screenshot({ path: target, omitBackground: false });
    const png = readFileSync(target);
    dimensions.set(demo.id, {
      width: png.readUInt32BE(16),
      height: png.readUInt32BE(20),
    });
  }
} finally {
  await browser.close();
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
        officialAspectRatio = ${size.width}f / ${size.height}f,
    )`;
  })
  .join(',\n');

mkdirSync(dirname(kotlinOutput), { recursive: true });
writeFileSync(
  kotlinOutput,
  `package com.swithun.cmpmermaid.debugui.generated

internal data class FlowchartDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
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
  `package com.swithun.cmpmermaid.core

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
