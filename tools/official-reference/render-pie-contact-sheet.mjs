import { existsSync, mkdirSync, readFileSync, readdirSync } from 'node:fs';
import { basename, dirname, extname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const nativeDirectory = resolve(
  repositoryRoot,
  process.env.NATIVE_DIR ?? 'captures/local/pie-native',
);
const officialDirectory = resolve(
  repositoryRoot,
  process.env.OFFICIAL_DIR ?? 'captures/local/pie-official',
);
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/pie-contact-sheets',
);
const casesPerSheet = Number.parseInt(process.env.CASES_PER_SHEET ?? '8', 10);

if (!Number.isInteger(casesPerSheet) || casesPerSheet <= 0) {
  throw new Error(`CASES_PER_SHEET must be a positive integer, found ${casesPerSheet}`);
}

const caseIds = readdirSync(nativeDirectory)
  .filter((name) => name.endsWith('_native.png'))
  .map((name) => basename(name, '_native.png'))
  .filter((id) => existsSync(resolve(officialDirectory, `${id}_official.png`)))
  .sort();

if (caseIds.length === 0) {
  throw new Error('No matching Native/Official Pie screenshots were found');
}

mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch({ headless: 'shell' });
try {
  for (let start = 0; start < caseIds.length; start += casesPerSheet) {
    const page = await browser.newPage();
    const currentCases = caseIds.slice(start, start + casesPerSheet);
    await page.setViewport({
      width: 980,
      height: currentCases.length * 500,
      deviceScaleFactor: 1,
    });
    await page.setContent(renderPage(currentCases), { waitUntil: 'load' });
    await page.evaluate(async () => {
      await Promise.all([...document.images].map((image) => image.decode()));
    });
    const sheetNumber = Math.floor(start / casesPerSheet) + 1;
    await page.screenshot({
      path: resolve(outputDirectory, `pie-contact-${sheetNumber}.png`),
      fullPage: true,
      omitBackground: false,
    });
    await page.close();
  }
} finally {
  await browser.close();
}

console.log(`Rendered ${caseIds.length} Pie comparison pairs to ${outputDirectory}`);

function renderPage(ids) {
  const rows = ids.map((id) => {
    const native = imageData(resolve(nativeDirectory, `${id}_native.png`));
    const official = imageData(resolve(officialDirectory, `${id}_official.png`));
    return `
      <section class="case">
        <h2>${escapeHtml(id)}</h2>
        <div class="pair">
          <figure>
            <figcaption>Native Compose</figcaption>
            <div class="frame">
              <img class="native" src="${native}" alt="">
            </div>
          </figure>
          <figure>
            <figcaption>Mermaid 12.0.0</figcaption>
            <div class="frame">
              <img class="official" src="${official}" alt="">
            </div>
          </figure>
        </div>
      </section>
    `;
  });
  return `
    <style>
      * { box-sizing: border-box; }
      body {
        margin: 0;
        background: #e5e7eb;
        color: #111827;
        font: 14px Arial, sans-serif;
      }
      .case {
        width: 980px;
        height: 500px;
        padding: 12px 16px 16px;
        background: white;
        border-bottom: 2px solid #9ca3af;
      }
      h2 {
        height: 24px;
        margin: 0 0 8px;
        font-size: 18px;
        line-height: 24px;
      }
      .pair {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
        height: 438px;
      }
      figure {
        min-width: 0;
        margin: 0;
        border: 1px solid #d1d5db;
        background: white;
      }
      figcaption {
        height: 28px;
        padding: 5px 8px;
        border-bottom: 1px solid #d1d5db;
        background: #f3f4f6;
        font-weight: 600;
      }
      .frame {
        display: flex;
        height: 408px;
        align-items: center;
        justify-content: center;
        overflow: hidden;
      }
      img.native {
        width: 450px;
        height: auto;
      }
      img.official {
        max-width: 450px;
        max-height: 390px;
        width: auto;
        height: auto;
      }
    </style>
    ${rows.join('\n')}
  `;
}

function imageData(path) {
  const extension = extname(path).slice(1);
  return `data:image/${extension};base64,${readFileSync(path).toString('base64')}`;
}

function escapeHtml(value) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;');
}
