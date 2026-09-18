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
import { cases as productionCases } from './production-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';
import { cases as visualParityCases } from './visual-parity-corpus.mjs';

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
const corpusSource = process.env.CORPUS_SOURCE ?? 'stability';
const cases = {
  stability: stabilityCases,
  production: productionCases,
  'visual-parity': visualParityCases,
}[corpusSource];
if (cases === undefined) {
  throw new Error(
    'CORPUS_SOURCE must be stability, production, or visual-parity',
  );
}
const pageSize = corpusSource === 'visual-parity'
  ? Number(process.env.CONTACT_SHEET_PAGE_SIZE ?? 16)
  : Number.POSITIVE_INFINITY;
const corpusKind = process.env.CORPUS_KIND ?? 'all';
const kinds = [
  'flowchart',
  'xychart',
  'quadrant',
  'timeline',
  'sequence',
  'class',
  'state',
  'er',
  'gantt',
  'pie',
  'journey',
  'requirement',
  'gitgraph',
  'mindmap',
  'packet',
  'radar',
  'sankey',
  'treemap',
  'venn',
  'kanban',
  'ishikawa',
  'cynefin',
];
const kindTitles = {
  flowchart: 'Flowchart',
  xychart: 'XY Chart',
  quadrant: 'Quadrant Chart',
  timeline: 'Timeline',
  sequence: 'Sequence',
  class: 'Class',
  state: 'State',
  er: 'Entity Relationship',
  gantt: 'Gantt',
  pie: 'Pie',
  journey: 'User Journey',
  requirement: 'Requirement',
  gitgraph: 'Git Graph',
  mindmap: 'Mindmap',
  packet: 'Packet',
  radar: 'Radar',
  sankey: 'Sankey',
  treemap: 'Treemap',
  venn: 'Venn',
  kanban: 'Kanban',
  ishikawa: 'Ishikawa',
  cynefin: 'Cynefin',
};
const selectedKinds = kinds.filter(
  (kind) => corpusKind === 'all' || corpusKind === kind,
);
if (selectedKinds.length === 0) {
  throw new Error(`Unsupported CORPUS_KIND: ${corpusKind}`);
}

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

  for (const kind of selectedKinds) {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    const kindRecords = kindCases.map((entry) => captureRecord(entry));
    const pages = chunk(kindRecords, pageSize);
    for (const [pageIndex, records] of pages.entries()) {
      const pageNumber = pageIndex + 1;
      const htmlPath = resolve(
        outputDirectory,
        `.${kind}-contact-sheet-${pageNumber}.html`,
      );
      writeFileSync(
        htmlPath,
        renderContactSheet(kind, records, pageNumber, pages.length),
      );
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
        corpusSource === 'visual-parity'
          ? `${kind}-visual-parity-${String(pageNumber).padStart(2, '0')}.jpg`
          : `${kind}-complex-corpus.png`,
      );
      await page.screenshot({
        path: outputPath,
        fullPage: true,
        omitBackground: false,
        ...(corpusSource === 'visual-parity'
          ? { type: 'jpeg', quality: 82 }
          : {}),
      });
      unlinkSync(htmlPath);
      console.log(`Generated ${outputPath}`);
    }
    manifest.push(...kindRecords);
  }
} finally {
  await browser.close();
}

writeFileSync(
  resolve(
    outputDirectory,
    corpusSource === 'visual-parity'
      ? 'visual-parity-manifest.json'
      : 'manifest.json',
  ),
  `${JSON.stringify({
    mermaidVersion: '12.0.0',
    corpusSource,
    generatedAt: new Date().toISOString(),
    caseCount: manifest.length,
    cases: manifest.map(({ nativePath, officialPath, source, ...entry }) => ({
      ...entry,
      sourceSha256: entry.sourceSha256 ?? sha256Value(source),
    })),
  }, null, 2)}\n`,
);
if (corpusSource === 'visual-parity') {
  writeFileSync(
    resolve(outputDirectory, 'visual-parity-evidence.md'),
    renderEvidenceIndex(),
  );
}

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

function renderContactSheet(kind, records, pageNumber, pageCount) {
  const rows = records.map((entry) => `
    <article>
      <h2>${escapeHtml(entry.title)}</h2>
      <p><code>${escapeHtml(entry.id)}</code> ${escapeHtml(entry.scenario)}</p>
      <p class="dimensions">${escapeHtml((entry.features ?? []).join(' · '))}</p>
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
    .dimensions { margin-top: 6px; font-size: 15px; }
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
    <h1>${kindTitles[kind]} ${escapeHtml(corpusSource)} corpus</h1>
    <p>Page ${pageNumber} of ${pageCount}. Left: CMP Native. Right: Mermaid.js 12.0.0.</p>
  </header>
  ${rows}
</body>
</html>`;
}

function renderEvidenceIndex() {
  const caseCount = manifest.length;
  const screenshotCount = caseCount * 2;
  const sections = selectedKinds.map((kind) => {
    const pageCount = Math.ceil(
      cases.filter((entry) => entry.kind === kind).length / pageSize,
    );
    const images = Array.from({ length: pageCount }, (_, index) => {
      const page = String(index + 1).padStart(2, '0');
      const file = `${kind}-visual-parity-${page}.jpg`;
      return `![${kindTitles[kind]} visual parity page ${page}](${file})`;
    }).join('\n\n');
    return `<details>
<summary><strong>${kindTitles[kind]} - 256 Native/Official pairs</strong></summary>

${images}

</details>`;
  }).join('\n\n');
  return `# Large-Scale Native/Official Visual Evidence

This index contains ${caseCount.toLocaleString('en-US')} source cases and
${screenshotCount.toLocaleString('en-US')} screenshots from the large-scale
visual parity matrix. Every page shows the same Mermaid source on the left in
CMP Native and on the right in Mermaid.js 12.0.0.

The matrix contains 256 cases per supported diagram type. Case IDs, structural
seed IDs, label profiles, feature dimensions, source hashes, image hashes, and
capture sizes are recorded in
[\`visual-parity-manifest.json\`](visual-parity-manifest.json).
Each type combines 13 or 14 complex structural seeds with 20 visible text and
layout-pressure profiles. The 256 sources per type are unique, but they are not
presented as 256 unrelated topologies.

${sections}
`;
}

function chunk(values, size) {
  if (!Number.isFinite(size)) return [values];
  if (!Number.isInteger(size) || size <= 0) {
    throw new Error('CONTACT_SHEET_PAGE_SIZE must be a positive integer');
  }
  return Array.from(
    { length: Math.ceil(values.length / size) },
    (_, index) => values.slice(index * size, (index + 1) * size),
  );
}

function escapeHtml(value) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}
