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
import { cases } from './invalid-source-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const inputDirectory = resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/invalid-source',
);
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'docs/assets/invalid-source-report',
);
const pageSize = Number(process.env.CONTACT_SHEET_PAGE_SIZE ?? 11);
if (!Number.isInteger(pageSize) || pageSize <= 0) {
  throw new Error('CONTACT_SHEET_PAGE_SIZE must be a positive integer');
}

mkdirSync(outputDirectory, { recursive: true });
const records = cases.map(captureRecord);
const pages = chunk(records, pageSize);
const browser = await puppeteer.launch(puppeteerLaunchOptions);
try {
  const page = await browser.newPage();
  await page.setViewport({
    width: 1600,
    height: 900,
    deviceScaleFactor: 1,
  });
  for (const [pageIndex, pageRecords] of pages.entries()) {
    const pageNumber = pageIndex + 1;
    const htmlPath = resolve(
      outputDirectory,
      `.invalid-source-contact-sheet-${pageNumber}.html`,
    );
    writeFileSync(
      htmlPath,
      renderContactSheet(pageRecords, pageNumber, pages.length),
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
      `invalid-source-visual-${String(pageNumber).padStart(2, '0')}.jpg`,
    );
    await page.screenshot({
      path: outputPath,
      fullPage: true,
      omitBackground: false,
      type: 'jpeg',
      quality: 86,
    });
    unlinkSync(htmlPath);
    console.log(`Generated ${outputPath}`);
  }
} finally {
  await browser.close();
}

writeFileSync(
  resolve(outputDirectory, 'invalid-source-manifest.json'),
  `${JSON.stringify({
    mermaidVersion: '12.0.0',
    corpusSource: 'invalid-source',
    generatedAt: new Date().toISOString(),
    caseCount: records.length,
    screenshotCount: records.length * 2,
    contactSheetCount: pages.length,
    cases: records.map(({
      nativePath,
      officialPath,
      source,
      ...record
    }) => ({
      ...record,
      sourceSha256: sha256Value(source),
    })),
  }, null, 2)}\n`,
);
writeFileSync(
  resolve(outputDirectory, 'invalid-source-evidence.md'),
  renderEvidenceIndex(records.length, pages.length),
);

function captureRecord(entry) {
  const nativeFile = `${entry.id}_native.png`;
  const officialFile = `${entry.id}_official.png`;
  const nativePath = resolve(inputDirectory, nativeFile);
  const officialPath = resolve(inputDirectory, officialFile);
  const nativeManifest = readErrorManifest(
    resolve(inputDirectory, `${entry.id}_native.manifest.json`),
    entry,
    'native',
  );
  const officialManifest = readErrorManifest(
    resolve(inputDirectory, `${entry.id}_official.manifest.json`),
    entry,
    'official',
  );
  return {
    ...entry,
    nativeFile,
    officialFile,
    nativePath,
    officialPath,
    nativeBytes: statSync(nativePath).size,
    officialBytes: statSync(officialPath).size,
    nativeSha256: sha256File(nativePath),
    officialSha256: sha256File(officialPath),
    nativeErrorType: nativeManifest.errorType,
    nativeErrorMessage: nativeManifest.message,
    officialErrorMessage: officialManifest.message,
  };
}

function readErrorManifest(path, entry, renderer) {
  const manifest = JSON.parse(readFileSync(path, 'utf8'));
  if (
    manifest.renderer !== renderer ||
    manifest.outcome !== 'error' ||
    typeof manifest.message !== 'string' ||
    manifest.message.trim().length === 0
  ) {
    throw new Error(`${entry.id}/${renderer} has an invalid error manifest`);
  }
  if (
    renderer === 'native' &&
    manifest.errorType !== entry.expectedNativeErrorType
  ) {
    throw new Error(
      `${entry.id}/native returned ${manifest.errorType}; ` +
        `expected ${entry.expectedNativeErrorType}`,
    );
  }
  return manifest;
}

function renderContactSheet(pageRecords, pageNumber, pageCount) {
  const rows = pageRecords.map((entry) => `
    <article>
      <h2>${escapeHtml(entry.title)}</h2>
      <p class="scenario"><code>${escapeHtml(entry.id)}</code> ${escapeHtml(entry.scenario)}</p>
      <pre>${escapeHtml(entry.source)}</pre>
      <div class="labels">
        <strong>CMP Native</strong>
        <strong>Mermaid.js 12.0.0</strong>
      </div>
      <div class="pair">
        <img src="${pathToFileURL(entry.nativePath).href}" alt="${escapeHtml(entry.id)} native error">
        <img src="${pathToFileURL(entry.officialPath).href}" alt="${escapeHtml(entry.id)} official error">
      </div>
      <div class="outcomes">
        <p><b>${escapeHtml(entry.nativeErrorType)}</b> ${escapeHtml(shortError(entry.nativeErrorMessage))}</p>
        <p><b>PARSE ERROR</b> ${escapeHtml(shortError(entry.officialErrorMessage))}</p>
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
    header { max-width: 1516px; margin: 0 auto 28px; }
    h1 { margin: 0 0 8px; font-size: 34px; letter-spacing: 0; }
    header p, .scenario { margin: 0; color: #59636e; font-size: 17px; }
    article {
      max-width: 1516px;
      margin: 0 auto 28px;
      padding: 24px;
      border: 1px solid #d0d7de;
      border-radius: 8px;
      background: white;
    }
    h2 { margin: 0 0 6px; font-size: 24px; letter-spacing: 0; }
    code { margin-right: 8px; color: #0969da; }
    pre {
      margin: 16px 0 0;
      padding: 14px 16px;
      overflow-wrap: anywhere;
      border: 1px solid #d8dee4;
      border-radius: 6px;
      background: #f6f8fa;
      color: #24292f;
      font: 15px/1.45 ui-monospace, SFMono-Regular, Menlo, monospace;
      white-space: pre-wrap;
    }
    .labels, .pair, .outcomes {
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
    .outcomes p {
      margin: 8px 0 0;
      color: #59636e;
      font-size: 13px;
      line-height: 1.45;
    }
    .outcomes b { margin-right: 6px; color: #b42318; }
  </style>
</head>
<body>
  <header>
    <h1>Malformed-source Native/Official visual evidence</h1>
    <p>Page ${pageNumber} of ${pageCount}. Both renderers receive the source shown above each pair.</p>
  </header>
  ${rows}
</body>
</html>`;
}

function renderEvidenceIndex(caseCount, pageCount) {
  const images = Array.from({ length: pageCount }, (_, index) => {
    const page = String(index + 1).padStart(2, '0');
    return `![Malformed-source visual evidence page ${page}](invalid-source-visual-${page}.jpg)`;
  }).join('\n\n');
  return `# Malformed-Source Native/Official Visual Evidence

This evidence contains ${caseCount} malformed Mermaid sources, one for every
supported diagram family, and ${caseCount * 2} screenshots. Each source is sent
unchanged to CMP Native and Mermaid.js 12.0.0.

Passing means CMP Native returns \`CONTENT_ERROR\`, Mermaid.js displays a parse
or render error, both messages are non-empty, and neither page crashes or times
out. This is an error-state safety gate, not a pixel-similarity gate.

Case IDs, source hashes, screenshot hashes, capture sizes, and both error
messages are recorded in
[\`invalid-source-manifest.json\`](invalid-source-manifest.json).

${images}
`;
}

function shortError(value) {
  const normalized = value.replace(/\s+/g, ' ').trim();
  return normalized.length <= 180
    ? normalized
    : `${normalized.slice(0, 177)}...`;
}

function chunk(values, size) {
  return Array.from(
    { length: Math.ceil(values.length / size) },
    (_, index) => values.slice(index * size, (index + 1) * size),
  );
}

function sha256File(path) {
  return createHash('sha256').update(readFileSync(path)).digest('hex');
}

function sha256Value(value) {
  return createHash('sha256').update(value).digest('hex');
}

function escapeHtml(value) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}
