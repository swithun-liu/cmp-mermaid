import { mkdirSync, readFileSync, statSync, unlinkSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { createHash } from 'node:crypto';
import puppeteer from 'puppeteer';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases } from './stability-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const inputDirectory = resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/rc-stability-corpus',
);
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'docs/assets/stability-report',
);
const kinds = ['flowchart', 'xychart', 'sequence', 'class', 'state', 'er', 'gantt', 'pie'];
const kindTitles = {
  flowchart: 'Flowchart',
  xychart: 'XY Chart',
  sequence: 'Sequence',
  class: 'Class',
  state: 'State',
  er: 'Entity Relationship',
  gantt: 'Gantt',
  pie: 'Pie',
};

mkdirSync(outputDirectory, { recursive: true });
const manifest = [];
const browser = await puppeteer.launch(puppeteerLaunchOptions);
try {
  const page = await browser.newPage();
  await page.setViewport({
    width: 1600,
    height: 900,
    deviceScaleFactor: 1,
  });

  for (const kind of kinds) {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    const records = kindCases.map((entry) => captureRecord(entry));
    const htmlPath = resolve(outputDirectory, `.${kind}-contact-sheet.html`);
    writeFileSync(htmlPath, renderContactSheet(kind, records));
    await page.goto(pathToFileURL(htmlPath).href, {
      waitUntil: 'networkidle0',
      timeout: 60_000,
    });
    await page.waitForFunction(
      () => [...document.images].every((image) => image.complete && image.naturalWidth > 0),
      { timeout: 60_000 },
    );
    const outputPath = resolve(outputDirectory, `${kind}-complex-corpus.png`);
    await page.screenshot({
      path: outputPath,
      fullPage: true,
      omitBackground: false,
    });
    unlinkSync(htmlPath);
    manifest.push(...records);
    console.log(`Generated ${outputPath}`);
  }
} finally {
  await browser.close();
}

writeFileSync(
  resolve(outputDirectory, 'manifest.json'),
  `${JSON.stringify({
    mermaidVersion: '12.0.0',
    generatedAt: new Date().toISOString(),
    caseCount: manifest.length,
    cases: manifest.map(({ nativePath, officialPath, ...entry }) => entry),
  }, null, 2)}\n`,
);

function captureRecord(entry) {
  const nativePath = resolve(inputDirectory, `${entry.id}_native.png`);
  const officialPath = resolve(inputDirectory, `${entry.id}_official.png`);
  return {
    ...entry,
    nativeFile: `${entry.id}_native.png`,
    officialFile: `${entry.id}_official.png`,
    nativePath,
    officialPath,
    nativeBytes: statSync(nativePath).size,
    officialBytes: statSync(officialPath).size,
    nativeSha256: sha256(nativePath),
    officialSha256: sha256(officialPath),
  };
}

function sha256(path) {
  return createHash('sha256').update(readFileSync(path)).digest('hex');
}

function renderContactSheet(kind, records) {
  const rows = records.map((entry) => `
    <article>
      <h2>${escapeHtml(entry.title)}</h2>
      <p><code>${escapeHtml(entry.id)}</code> ${escapeHtml(entry.scenario)}</p>
      <div class="labels"><strong>CMP Native</strong><strong>Mermaid.js 12.0.0</strong></div>
      <div class="pair">
        <img src="${pathToFileURL(entry.nativePath).href}" alt="${escapeHtml(entry.id)} native">
        <img src="${pathToFileURL(entry.officialPath).href}" alt="${escapeHtml(entry.id)} official">
      </div>
    </article>
  `).join('');

  return `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <style>
    * { box-sizing: border-box; }
    body {
      margin: 0;
      padding: 42px;
      color: #20232a;
      background: #f6f8fa;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
    }
    header { margin: 0 auto 28px; max-width: 1516px; }
    h1 { margin: 0 0 8px; font-size: 34px; }
    header p, article p { margin: 0; color: #59636e; font-size: 18px; }
    article {
      max-width: 1516px;
      margin: 0 auto 28px;
      padding: 24px;
      border: 1px solid #d0d7de;
      border-radius: 8px;
      background: white;
    }
    h2 { margin: 0 0 6px; font-size: 24px; }
    code { margin-right: 8px; color: #0969da; }
    .labels, .pair {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
    }
    .labels { margin: 20px 0 8px; text-align: center; font-size: 18px; }
    img {
      display: block;
      width: 100%;
      aspect-ratio: 4 / 3;
      object-fit: contain;
      border: 1px solid #d8dee4;
      background: white;
    }
  </style>
</head>
<body>
  <header>
    <h1>${kindTitles[kind]} complex release-candidate corpus</h1>
    <p>Independent real-world scenarios. Left: CMP Native. Right: Mermaid.js 12.0.0.</p>
  </header>
  ${rows}
</body>
</html>`;
}

function escapeHtml(value) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}
