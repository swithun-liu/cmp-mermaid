import { mkdirSync, readFileSync, statSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { cases as flowchartCases } from './cases.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const baseUrl = process.env.BASE_URL ?? 'http://127.0.0.1:8093/';
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/web-audit/current',
);
const auditKind = process.env.AUDIT_KIND ?? 'all';
const layout = process.env.CAPTURE_LAYOUT ?? 'elk';
const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);
const previews = (process.env.CAPTURE_PREVIEWS ?? 'Native Official')
  .split(/[\s,]+/)
  .filter(Boolean);
const viewportWidth = Number(process.env.VIEWPORT_WIDTH ?? 900);
const viewportHeight = Number(process.env.VIEWPORT_HEIGHT ?? 900);
const minimumCaptureBytes = Number(process.env.MIN_CAPTURE_BYTES ?? 5_000);

if (!['all', 'flowchart', 'xychart'].includes(auditKind)) {
  throw new Error('AUDIT_KIND must be all, flowchart, or xychart');
}
if (!['dagre', 'elk'].includes(layout)) {
  throw new Error('CAPTURE_LAYOUT must be dagre or elk');
}

const availableCases = [
  ...(auditKind === 'all' || auditKind === 'flowchart'
    ? flowchartCases.map((entry) => ({ ...entry, kind: 'flowchart' }))
    : []),
  ...(auditKind === 'all' || auditKind === 'xychart'
    ? readXyChartCases().map((entry) => ({ ...entry, kind: 'xychart' }))
    : []),
].filter(({ id }) => selectedIds.size === 0 || selectedIds.has(id));

if (availableCases.length === 0) {
  throw new Error('No matching audit cases were found');
}

mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch({ headless: 'shell' });
try {
  const page = await browser.newPage();
  await page.setViewport({
    width: viewportWidth,
    height: viewportHeight,
    deviceScaleFactor: 1,
  });

  for (const auditCase of availableCases) {
    for (const preview of previews) {
      const url = new URL(baseUrl);
      url.searchParams.set('auditDemoId', auditCase.id);
      url.searchParams.set('auditPreview', preview);
      url.searchParams.set('auditLayout', layout);
      await page.goto(url.href, {
        waitUntil: 'domcontentloaded',
        timeout: 60_000,
      });
      if (preview.toLowerCase() === 'official') {
        await waitForOfficialSvg(page);
      } else {
        await waitForNativeCanvas(page);
      }
      await page.evaluate(() => new Promise((resolveFrame) => {
        requestAnimationFrame(() => requestAnimationFrame(resolveFrame));
      }));

      const suffix = preview.toLowerCase();
      const target = resolve(
        outputDirectory,
        `${auditCase.id}_${suffix}.png`,
      );
      await page.screenshot({
        path: target,
        omitBackground: false,
      });
      const captureBytes = statSync(target).size;
      if (captureBytes < minimumCaptureBytes) {
        throw new Error(
          `${auditCase.id}/${preview} produced only ${captureBytes} bytes`,
        );
      }
      console.log(
        `${auditCase.kind}/${auditCase.id}/${preview}: ${captureBytes} bytes`,
      );
    }
  }
} finally {
  await browser.close();
}

console.log(
  `Captured ${availableCases.length * previews.length} Web audit images to ` +
    outputDirectory,
);

async function waitForNativeCanvas(page) {
  await page.waitForFunction(
    () => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        const canvas = root.querySelector?.('canvas');
        if (canvas?.width > 0 && canvas?.height > 0) {
          return true;
        }
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) {
            roots.push(element.shadowRoot);
          }
        });
      }
      return false;
    },
    { timeout: 60_000 },
  );
  await new Promise((resolveWait) => setTimeout(resolveWait, 750));
}

async function waitForOfficialSvg(page) {
  await page.waitForFunction(
    () => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        const frame = root.querySelector?.(
          'iframe[title="Official Mermaid.js rendering"]',
        );
        if (frame?.contentDocument?.querySelector('#diagram svg') != null) {
          return true;
        }
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) {
            roots.push(element.shadowRoot);
          }
        });
      }
      return false;
    },
    { timeout: 60_000 },
  );
}

function readXyChartCases() {
  const sourcePath = resolve(
    repositoryRoot,
    'mermaid-debug-ui/src/commonMain/kotlin/io/github/cmpmermaid/debugui/XyChartDemos.kt',
  );
  const kotlin = readFileSync(sourcePath, 'utf8');
  const casePattern =
    /XyChartDemo\(\s*id = "([^"]+)",\s*title = "([^"]+)",\s*category = "([^"]+)",\s*source = """\n([\s\S]*?)\n\s*"""\.trimIndent\(\),\s*\)/g;
  return [...kotlin.matchAll(casePattern)].map((match) => ({
    id: match[1],
    title: match[2],
    category: match[3],
  }));
}
