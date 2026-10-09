import { resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import puppeteer from 'puppeteer';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as visualStructureCases } from './visual-structure-corpus.mjs';

const root = fileURLToPath(new URL('.', import.meta.url));
const officialRenderer = resolve(
  root,
  '../../mermaid-debug-ui/src/androidMain/assets/official-mermaid.html',
);
const selectedKind = process.env.OFFICIAL_VALIDATION_KIND ?? 'all';
const concurrency = readPositiveInteger('OFFICIAL_VALIDATION_CONCURRENCY', 4);
const cases = visualStructureCases.filter((entry) =>
  selectedKind === 'all' || entry.kind === selectedKind
);
if (cases.length === 0) {
  throw new Error(`No visual structure cases found for ${selectedKind}`);
}

const browser = await puppeteer.launch({
  ...puppeteerLaunchOptions,
  protocolTimeout: 900_000,
});
const failures = [];
try {
  const workers = Array.from(
    { length: Math.min(concurrency, cases.length) },
    (_, workerIndex) => cases.filter((_, index) => index % concurrency === workerIndex),
  );
  const workerFailures = await Promise.all(
    workers.map(async (workerCases, workerIndex) => {
      const page = await browser.newPage();
      try {
        await page.goto(pathToFileURL(officialRenderer).href, {
          waitUntil: 'networkidle0',
          timeout: 60_000,
        });
        const result = await page.evaluate(async (entries) => {
          const failures = [];
          for (const entry of entries) {
            let outcome = null;
            window.CmpMermaidBridge = {
              onRenderReady: (width, height) => {
                outcome = {
                  status: 'ready',
                  width: Number(width),
                  height: Number(height),
                };
              },
              onRenderError: (message) => {
                outcome = { status: 'error', message };
              },
            };
            await window.renderDiagram(entry.source, entry.layout);
            const svg = document.querySelector('#diagram svg');
            if (
              outcome?.status !== 'ready' ||
              svg === null ||
              !Number.isFinite(outcome.width) ||
              !Number.isFinite(outcome.height) ||
              outcome.width <= 0 ||
              outcome.height <= 0
            ) {
              failures.push({
                id: entry.id,
                message:
                  outcome?.message ??
                  `Invalid Official render outcome: ${JSON.stringify(outcome)}`,
              });
            }
          }
          return failures;
        }, workerCases.map((entry) => ({
          id: entry.id,
          source: entry.source,
          layout: entry.layout ?? 'dagre',
        })));
        console.log(
          `Official worker ${workerIndex + 1}: ` +
            `${workerCases.length - result.length}/${workerCases.length} passed`,
        );
        return result;
      } finally {
        await page.close();
      }
    }),
  );
  failures.push(...workerFailures.flat());
} finally {
  await browser.close();
}

if (failures.length > 0) {
  for (const failure of failures) {
    console.error(`${failure.id}: ${failure.message}`);
  }
  throw new Error(
    `${failures.length}/${cases.length} Official Mermaid.js renders failed`,
  );
}
console.log(
  `Official Mermaid.js rendered ${cases.length} structural cases successfully`,
);

function readPositiveInteger(name, fallback) {
  const value = Number(process.env[name] ?? fallback);
  if (!Number.isInteger(value) || value <= 0) {
    throw new Error(`${name} must be a positive integer`);
  }
  return value;
}
