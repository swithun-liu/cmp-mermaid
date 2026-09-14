import { mkdirSync, readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const sourcePath = resolve(
  repositoryRoot,
  'mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/ErDemos.kt',
);
const output = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/er-official',
);
const mermaidBundle = resolve(root, 'node_modules/mermaid/dist/mermaid.min.js');
const mermaidPackage = JSON.parse(
  readFileSync(resolve(root, 'node_modules/mermaid/package.json'), 'utf8'),
);
const layout = process.env.ER_LAYOUT ?? 'elk';

if (mermaidPackage.version !== '12.0.0') {
  throw new Error(`Expected mermaid 12.0.0, found ${mermaidPackage.version}`);
}

const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);
const kotlin = readFileSync(sourcePath, 'utf8');
const casePattern =
  /ErDemo\(\s*id = "([^"]+)",\s*title = "([^"]+)",\s*category = "([^"]+)",\s*source = """\n([\s\S]*?)\n\s*"""\.trimIndent\(\),\s*\)/g;
const cases = [...kotlin.matchAll(casePattern)]
  .map((match) => ({
    id: match[1],
    title: match[2],
    category: match[3],
    source: trimIndent(match[4]),
  }))
  .filter(({ id }) => selectedIds.size === 0 || selectedIds.has(id));

if (cases.length === 0) {
  throw new Error('No ER gallery cases matched ErDemos.kt');
}

mkdirSync(output, { recursive: true });
const browser = await puppeteer.launch({ headless: 'shell' });
try {
  const page = await browser.newPage();
  await page.setViewport({ width: 1400, height: 1400, deviceScaleFactor: 1 });
  await page.setContent(
    '<style>body{margin:0;background:white}svg{display:block}</style>' +
      '<div id="container"></div>',
  );
  await page.addScriptTag({ path: mermaidBundle });

  for (const demo of cases) {
    await page.evaluate(
      async ({ id, source, layout }) => {
        mermaid.initialize({
          startOnLoad: false,
          securityLevel: 'strict',
          layout,
        });
        const container = document.querySelector('#container');
        container.replaceChildren();
        const { svg } = await mermaid.render(`official-er-${id}`, source, container);
        container.innerHTML = svg;
        container.querySelector('svg').style.backgroundColor = 'white';
      },
      { ...demo, layout },
    );
    const svg = await page.$('#container > svg');
    if (svg === null) {
      throw new Error(`Official Mermaid produced no SVG for ${demo.id}`);
    }
    await svg.screenshot({
      path: resolve(output, `${demo.id}_official.png`),
      omitBackground: false,
    });
  }
} finally {
  await browser.close();
}

console.log(`Rendered ${cases.length} ER gallery references to ${output}`);

function trimIndent(source) {
  const lines = source.split('\n');
  const nonBlank = lines.filter((line) => line.trim().length > 0);
  const indent = Math.min(...nonBlank.map((line) => line.match(/^\s*/)[0].length));
  return lines.map((line) => line.slice(Math.min(indent, line.length))).join('\n');
}
