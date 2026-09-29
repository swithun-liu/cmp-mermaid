import { createHash } from 'node:crypto';
import {
  existsSync,
  mkdirSync,
  readFileSync,
  readdirSync,
  statSync,
  unlinkSync,
  writeFileSync,
} from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import puppeteer from 'puppeteer';
import { cases, casesPerKind } from './invalid-source-corpus.mjs';
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
const corpusKind = process.env.CORPUS_KIND ?? 'all';
const corpusScope = process.env.INVALID_SOURCE_SCOPE ?? 'full';
if (!['full', 'smoke'].includes(corpusScope)) {
  throw new Error('INVALID_SOURCE_SCOPE must be full or smoke');
}
const kinds = [...new Set(cases.map((entry) => entry.kind))];
const selectedKinds = kinds.filter(
  (kind) => corpusKind === 'all' || corpusKind === kind,
);
if (selectedKinds.length === 0) {
  throw new Error(`Unsupported CORPUS_KIND: ${corpusKind}`);
}
const selectedCases = cases.filter(
  (entry) =>
    selectedKinds.includes(entry.kind) &&
    (corpusScope === 'full' || entry.profileIndex === 1),
);
const pageSize = Number(process.env.CONTACT_SHEET_PAGE_SIZE ?? 16);
if (!Number.isInteger(pageSize) || pageSize <= 0) {
  throw new Error('CONTACT_SHEET_PAGE_SIZE must be a positive integer');
}

mkdirSync(outputDirectory, { recursive: true });
const records = selectedCases.map(captureRecord);
const errorMessageParityCount = records.filter(
  (entry) => entry.nativeErrorMessage === entry.officialErrorMessage,
).length;
if (errorMessageParityCount !== records.length) {
  throw new Error(
    `Detailed error message parity failed: ` +
      `${errorMessageParityCount}/${records.length}`,
  );
}
removeStaleContactSheets();
const pageGroups = corpusScope === 'full'
  ? selectedKinds.map((kind) => ({
      kind,
      pages: chunk(
        records.filter((entry) => entry.kind === kind),
        pageSize,
      ),
    }))
  : [{ kind: null, pages: chunk(records, pageSize) }];
const browser = await puppeteer.launch(puppeteerLaunchOptions);
try {
  const page = await browser.newPage();
  await page.setViewport({
    width: 1600,
    height: 900,
    deviceScaleFactor: 1,
  });
  for (const group of pageGroups) {
    for (const [pageIndex, pageRecords] of group.pages.entries()) {
      const pageNumber = pageIndex + 1;
      const pageLabel = String(pageNumber).padStart(2, '0');
      const filePrefix = corpusScope === 'full'
        ? `${group.kind}-invalid-source`
        : 'invalid-source-smoke';
      const htmlPath = resolve(
        outputDirectory,
        `.${filePrefix}-contact-sheet-${pageLabel}.html`,
      );
      writeFileSync(
        htmlPath,
        renderContactSheet(
          group.kind,
          pageRecords,
          pageNumber,
          group.pages.length,
        ),
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
        `${filePrefix}-${pageLabel}.jpg`,
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
  }
} finally {
  await browser.close();
}

const manifestPath = resolve(outputDirectory, 'invalid-source-manifest.json');
const persistedRecords = mergePartialManifest(manifestPath, records);
writeFileSync(
  manifestPath,
  `${JSON.stringify({
    mermaidVersion: '12.0.0',
    corpusSource: 'invalid-source',
    scope: corpusScope,
    diagramFamilyCount: new Set(
      persistedRecords.map((entry) => entry.kind),
    ).size,
    casesPerDiagramFamily:
      corpusScope === 'full' ? casesPerKind : 1,
    variantModel:
      'one malformed seed per diagram family with deterministic context variants',
    generatedAt: new Date().toISOString(),
    caseCount: persistedRecords.length,
    screenshotCount: persistedRecords.length * 2,
    contactSheetCount: contactSheetCount(persistedRecords),
    detailedErrorMessageParityCount: persistedRecords.filter(
      (entry) => entry.nativeErrorMessage === entry.officialErrorMessage,
    ).length,
    cases: persistedRecords.map(({
      nativePath,
      officialPath,
      source,
      ...record
    }) => ({
      ...record,
      sourceSha256: record.sourceSha256 ?? sha256Value(source),
    })),
  }, null, 2)}\n`,
);
writeFileSync(
  resolve(outputDirectory, 'invalid-source-evidence.md'),
  renderEvidenceIndex(persistedRecords),
);

function removeStaleContactSheets() {
  for (const file of readdirSync(outputDirectory)) {
    const isLegacySheet =
      corpusScope === 'full' &&
      corpusKind === 'all' &&
      /^invalid-source-visual-\d+\.jpg$/.test(file);
    const isSmokeSheet =
      corpusScope === 'smoke' &&
      /^invalid-source-smoke-\d+\.jpg$/.test(file);
    const isSelectedFullSheet =
      corpusScope === 'full' &&
      selectedKinds.some(
        (kind) => new RegExp(`^${kind}-invalid-source-\\d+\\.jpg$`).test(file),
      );
    if (isLegacySheet || isSmokeSheet || isSelectedFullSheet) {
      unlinkSync(resolve(outputDirectory, file));
    }
  }
}

function mergePartialManifest(path, currentRecords) {
  if (corpusKind === 'all' || !existsSync(path)) {
    return currentRecords;
  }
  const previous = JSON.parse(readFileSync(path, 'utf8'));
  if (
    previous.scope !== corpusScope ||
    previous.casesPerDiagramFamily !==
      (corpusScope === 'full' ? casesPerKind : 1)
  ) {
    return currentRecords;
  }
  if (!Array.isArray(previous.cases)) {
    throw new Error(`Existing manifest has no cases array: ${path}`);
  }

  const recordsById = new Map(
    previous.cases
      .filter((entry) => !selectedKinds.includes(entry.kind))
      .map((entry) => [entry.id, entry]),
  );
  for (const entry of currentRecords) {
    recordsById.set(entry.id, entry);
  }
  return cases
    .filter(
      (entry) =>
        (corpusScope === 'full' || entry.profileIndex === 1) &&
        recordsById.has(entry.id),
    )
    .map((entry) => recordsById.get(entry.id));
}

function contactSheetCount(records) {
  if (corpusScope === 'smoke') {
    return Math.ceil(records.length / pageSize);
  }
  return kinds.reduce(
    (total, kind) =>
      total +
      Math.ceil(
        records.filter((entry) => entry.kind === kind).length / pageSize,
      ),
    0,
  );
}

function captureRecord(entry) {
  const nativeFile = `${entry.id}_native.png`;
  const officialFile = `${entry.id}_official.png`;
  const nativePath = resolveCapturePath(entry, nativeFile);
  const officialPath = resolveCapturePath(entry, officialFile);
  const nativeManifest = readErrorManifest(
    resolveCapturePath(entry, `${entry.id}_native.manifest.json`),
    entry,
    'native',
  );
  const officialManifest = readErrorManifest(
    resolveCapturePath(entry, `${entry.id}_official.manifest.json`),
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

function resolveCapturePath(entry, fileName) {
  const directPath = resolve(inputDirectory, fileName);
  return existsSync(directPath)
    ? directPath
    : resolve(inputDirectory, entry.kind, fileName);
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

function renderContactSheet(kind, pageRecords, pageNumber, pageCount) {
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
    <h1>${kind === null
      ? 'Malformed-source smoke evidence'
      : `${escapeHtml(pageRecords[0].diagramTitle)} malformed-source evidence`}</h1>
    <p>Page ${pageNumber} of ${pageCount}. Both renderers receive the source shown above each pair.</p>
  </header>
  ${rows}
</body>
</html>`;
}

function renderEvidenceIndex(records) {
  const caseCount = records.length;
  const screenshotCount = caseCount * 2;
  if (corpusScope === 'smoke') {
    const pageCount = Math.ceil(caseCount / pageSize);
    const images = Array.from({ length: pageCount }, (_, index) => {
      const page = String(index + 1).padStart(2, '0');
      return `![Malformed-source smoke evidence page ${page}](invalid-source-smoke-${page}.jpg)`;
    }).join('\n\n');
    return `# Malformed-Source Native/Official Smoke Evidence

This CI smoke evidence contains ${caseCount} malformed Mermaid sources, one
seed for every supported diagram family, and ${screenshotCount} screenshots.

Passing means CMP Native returns \`CONTENT_ERROR\`, Mermaid.js displays a parse
or render error, both detailed messages match exactly, both renderers preserve
their error UI, and neither page crashes or times out. This is an error-state
safety and diagnostic-parity gate, not a pixel-similarity gate.

${images}
`;
  }

  const presentKinds = kinds.filter((kind) =>
    records.some((entry) => entry.kind === kind),
  );
  const sections = presentKinds.map((kind) => {
    const kindRecords = records.filter((entry) => entry.kind === kind);
    const pageCount = Math.ceil(kindRecords.length / pageSize);
    const images = Array.from({ length: pageCount }, (_, index) => {
      const page = String(index + 1).padStart(2, '0');
      const file = `${kind}-invalid-source-${page}.jpg`;
      return `![${kindRecords[0].diagramTitle} malformed-source evidence page ${page}](${file})`;
    }).join('\n\n');
    return `<details>
<summary><strong>${kindRecords[0].diagramTitle} - ${kindRecords.length} Native/Official error pairs</strong></summary>

${images}

</details>`;
  }).join('\n\n');

  return `# Malformed-Source Native/Official Visual Evidence

This evidence contains ${caseCount.toLocaleString('en-US')} malformed Mermaid
sources across ${presentKinds.length} supported diagram families and
${screenshotCount.toLocaleString('en-US')} screenshots. Each family starts from
one verified malformed seed and expands it into ${casesPerKind} deterministic
comment, blank-line, and line-ending contexts. These are systematic parser
safety variants, not ${caseCount.toLocaleString('en-US')} unrelated error root
causes. Each generated source is sent unchanged to CMP Native and Mermaid.js
12.0.0.

Passing means CMP Native returns \`CONTENT_ERROR\`, Mermaid.js displays a parse
or render error, all ${caseCount.toLocaleString('en-US')} detailed messages
match exactly, both renderers display Mermaid.js 12.0.0's standard error
diagram, and neither page crashes or times out. This is an error-state safety,
diagnostic-parity, and visual-review gate, not a pixel-similarity gate.

Case IDs, source hashes, screenshot hashes, capture sizes, and both error
messages are recorded in
[\`invalid-source-manifest.json\`](invalid-source-manifest.json).

${sections}
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
