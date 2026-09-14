import { existsSync, mkdirSync, readFileSync } from 'node:fs';
import { dirname, extname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { cases } from './cases.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const inputDirectory = resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/web-audit/current',
);
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/flowchart-contact-sheets',
);
const casesPerSheet = Number.parseInt(process.env.CASES_PER_SHEET ?? '4', 10);

if (!Number.isInteger(casesPerSheet) || casesPerSheet <= 0) {
  throw new Error(`CASES_PER_SHEET must be a positive integer, found ${casesPerSheet}`);
}

const availableCases = cases.filter(({ id }) =>
  existsSync(resolve(inputDirectory, `${id}_native.png`)) &&
  existsSync(resolve(inputDirectory, `${id}_official.png`)),
);

if (availableCases.length === 0) {
  throw new Error('No matching Native/Official Flowchart screenshots were found');
}

mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch({ headless: 'shell' });
try {
  for (let start = 0; start < availableCases.length; start += casesPerSheet) {
    const page = await browser.newPage();
    const currentCases = availableCases.slice(start, start + casesPerSheet);
    await page.setViewport({
      width: 1600,
      height: currentCases.length * 720,
      deviceScaleFactor: 1,
    });
    await page.setContent(renderPage(currentCases), { waitUntil: 'load' });
    await page.evaluate(async () => {
      await Promise.all([...document.images].map((image) => image.decode()));
    });
    const sheetNumber = Math.floor(start / casesPerSheet) + 1;
    await page.screenshot({
      path: resolve(
        outputDirectory,
        `flowchart-detail-${String(sheetNumber).padStart(2, '0')}.png`,
      ),
      fullPage: true,
      omitBackground: false,
    });
    await page.close();
  }
} finally {
  await browser.close();
}

console.log(
  `Rendered ${availableCases.length} Flowchart comparison pairs to ${outputDirectory}`,
);

function renderPage(entries) {
  const rows = entries.map(({ id, title }) => {
    const native = imageData(resolve(inputDirectory, `${id}_native.png`));
    const official = imageData(resolve(inputDirectory, `${id}_official.png`));
    return `
      <section class="case">
        <h2>${escapeHtml(title)} <code>${escapeHtml(id)}</code></h2>
        <div class="pair">
          <figure>
            <figcaption>CMP Native</figcaption>
            <div class="frame"><img src="${native}" alt=""></div>
          </figure>
          <figure>
            <figcaption>Mermaid.js 12.0.0</figcaption>
            <div class="frame"><img src="${official}" alt=""></div>
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
        font: 16px Arial, sans-serif;
      }
      .case {
        width: 1600px;
        height: 720px;
        padding: 16px;
        background: white;
        border-bottom: 2px solid #9ca3af;
      }
      h2 {
        height: 24px;
        margin: 0 0 12px;
        font-size: 20px;
        line-height: 24px;
      }
      code {
        color: #2563eb;
        font-size: 15px;
      }
      .pair {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
        height: 652px;
      }
      figure {
        min-width: 0;
        margin: 0;
        border: 1px solid #d1d5db;
        background: white;
      }
      figcaption {
        height: 34px;
        padding: 7px 10px;
        border-bottom: 1px solid #d1d5db;
        background: #f3f4f6;
        font-weight: 600;
      }
      .frame {
        display: flex;
        height: 616px;
        align-items: center;
        justify-content: center;
        overflow: hidden;
      }
      img {
        width: 100%;
        height: 616px;
        object-fit: contain;
        background: white;
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
