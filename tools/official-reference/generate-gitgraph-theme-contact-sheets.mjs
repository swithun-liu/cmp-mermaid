import { createHash } from 'node:crypto';
import {
  mkdirSync,
  readFileSync,
  statSync,
  unlinkSync,
  writeFileSync,
} from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import puppeteer from 'puppeteer';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as productionCases } from './production-corpus.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const inputDirectory = resolve(
  repositoryRoot,
  process.env.INPUT_DIR ??
    'captures/local/gitgraph/theme-matrix-20260915',
);
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ??
    'captures/local/gitgraph/theme-matrix-contact-sheets',
);
const themes = [
  'default',
  'dark',
  'forest',
  'neutral',
  'base',
  'neo',
  'neo-dark',
  'redux',
  'redux-color',
  'redux-dark',
  'redux-dark-color',
];
const selectedCases = [
  findCase(stabilityCases, 'rc_gitgraph_release_train'),
  findCase(productionCases, 'prod_gitgraph_commit_metadata_release'),
];

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

  for (const theme of themes) {
    const records = selectedCases.map((entry) => captureRecord(entry, theme));
    const htmlPath = resolve(outputDirectory, `.${theme}-contact-sheet.html`);
    writeFileSync(htmlPath, renderContactSheet(theme, records));
    await page.goto(pathToFileURL(htmlPath).href, {
      waitUntil: 'networkidle0',
      timeout: 60_000,
    });
    await page.waitForFunction(
      () => [...document.images]
        .every((image) => image.complete && image.naturalWidth > 0),
      { timeout: 60_000 },
    );
    const outputPath = resolve(
      outputDirectory,
      `gitgraph-theme-${theme}.png`,
    );
    await page.screenshot({
      path: outputPath,
      fullPage: true,
      omitBackground: false,
    });
    unlinkSync(htmlPath);
    console.log(`Generated ${outputPath}`);
    manifest.push(...records);
  }
} finally {
  await browser.close();
}

writeFileSync(
  resolve(outputDirectory, 'manifest.json'),
  `${JSON.stringify({
    mermaidVersion: '12.0.0',
    generatedAt: new Date().toISOString(),
    themeCount: themes.length,
    caseCount: selectedCases.length,
    screenshotCount: manifest.length * 2,
    themes,
    cases: manifest.map(({
      nativePath,
      officialPath,
      source,
      ...entry
    }) => ({
      ...entry,
      sourceSha256: sha256Value(source),
    })),
  }, null, 2)}\n`,
);

function findCase(cases, id) {
  const entry = cases.find((candidate) => candidate.id === id);
  if (entry === undefined) {
    throw new Error(`Missing Git Graph theme matrix case: ${id}`);
  }
  return entry;
}

function captureRecord(entry, theme) {
  const nativeFile = `${entry.id}_${theme}_native.png`;
  const officialFile = `${entry.id}_${theme}_official.png`;
  const nativePath = resolve(inputDirectory, nativeFile);
  const officialPath = resolve(inputDirectory, officialFile);
  return {
    ...entry,
    theme,
    nativeFile,
    officialFile,
    nativePath,
    officialPath,
    nativeBytes: statSync(nativePath).size,
    officialBytes: statSync(officialPath).size,
    nativeSha256: sha256File(nativePath),
    officialSha256: sha256File(officialPath),
  };
}

function sha256File(path) {
  return createHash('sha256').update(readFileSync(path)).digest('hex');
}

function sha256Value(value) {
  return createHash('sha256').update(value).digest('hex');
}

function renderContactSheet(theme, records) {
  const rows = records.map((entry) => `
    <article>
      <h2>${escapeHtml(entry.title)}</h2>
      <p><code>${escapeHtml(entry.id)}</code> ${escapeHtml(entry.scenario)}</p>
      <p class="features">${escapeHtml((entry.features ?? []).join(' · '))}</p>
      <div class="labels">
        <strong>CMP Native</strong>
        <strong>Mermaid.js 12.0.0</strong>
      </div>
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
    .features { margin-top: 6px; font-size: 15px; }
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
    <h1>Git Graph theme: ${escapeHtml(theme)}</h1>
    <p>Left: CMP Native. Right: Mermaid.js 12.0.0.</p>
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
