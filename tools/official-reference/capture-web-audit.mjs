import { existsSync, mkdirSync, readFileSync, statSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { cases as flowchartCases } from './cases.mjs';
import { cases as productionCases } from './production-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';
import { cases as visualParityCases } from './visual-parity-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const baseUrl = process.env.BASE_URL ?? 'http://127.0.0.1:8093/';
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/web-audit/current',
);
const auditKind = process.env.AUDIT_KIND ?? 'all';
const auditSource = process.env.AUDIT_SOURCE ?? 'gallery';
const layoutOverride = process.env.CAPTURE_LAYOUT ?? null;
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
const skipExisting = (process.env.SKIP_EXISTING ?? 'false') === 'true';

const kotlinGalleryFiles = {
  xychart: ['XyChartDemos.kt', 'XyChartDemo'],
  sequence: ['SequenceDemos.kt', 'SequenceDemo'],
  class: ['ClassDemos.kt', 'ClassDemo'],
  state: ['StateDemos.kt', 'StateDemo'],
  er: ['ErDemos.kt', 'ErDemo'],
  gantt: ['GanttDemos.kt', 'GanttDemo'],
  pie: ['PieDemos.kt', 'PieDemo'],
  journey: ['JourneyDemos.kt', 'JourneyDemo'],
  requirement: ['RequirementDemos.kt', 'RequirementDemo'],
};
const supportedAuditKinds = ['all', 'flowchart', ...Object.keys(kotlinGalleryFiles)];
const supportedAuditSources = [
  'gallery',
  'stability',
  'production',
  'visual-parity',
];

if (!supportedAuditKinds.includes(auditKind)) {
  throw new Error(`AUDIT_KIND must be one of: ${supportedAuditKinds.join(', ')}`);
}
if (!supportedAuditSources.includes(auditSource)) {
  throw new Error(`AUDIT_SOURCE must be one of: ${supportedAuditSources.join(', ')}`);
}
if (layoutOverride !== null && !['dagre', 'elk'].includes(layoutOverride)) {
  throw new Error('CAPTURE_LAYOUT must be dagre or elk');
}

const corpusCases = {
  stability: stabilityCases,
  production: productionCases,
  'visual-parity': visualParityCases,
}[auditSource] ?? [];
const sourceCases = supportedAuditSources.slice(1).includes(auditSource)
  ? corpusCases.filter(({ kind }) => auditKind === 'all' || auditKind === kind)
  : [
      ...(auditKind === 'all' || auditKind === 'flowchart'
        ? flowchartCases.map((entry) => ({ ...entry, kind: 'flowchart' }))
        : []),
      ...Object.entries(kotlinGalleryFiles).flatMap(
        ([kind, [fileName, constructorName]]) =>
          auditKind === 'all' || auditKind === kind
            ? readKotlinCases(fileName, constructorName).map((entry) => ({ ...entry, kind }))
            : [],
      ),
    ];
const availableCases = sourceCases.filter(
  ({ id }) => selectedIds.size === 0 || selectedIds.has(id),
);

if (availableCases.length === 0) {
  throw new Error('No matching audit cases were found');
}

mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch(puppeteerLaunchOptions);
try {
  const page = await browser.newPage();
  await page.setViewport({
    width: viewportWidth,
    height: viewportHeight,
    deviceScaleFactor: 1,
  });

  for (const auditCase of availableCases) {
    for (const preview of previews) {
      const suffix = preview.toLowerCase();
      const target = resolve(
        outputDirectory,
        `${auditCase.id}_${suffix}.png`,
      );
      if (
        skipExisting &&
        existsSync(target) &&
        statSync(target).size >= minimumCaptureBytes
      ) {
        continue;
      }
      const url = new URL(baseUrl);
      const captureLayout = layoutOverride ?? auditCase.layout ?? 'elk';
      url.searchParams.set('auditDemoId', auditCase.id);
      url.searchParams.set('auditPreview', preview);
      url.searchParams.set('auditLayout', captureLayout);
      await page.goto(url.href, {
        waitUntil: 'domcontentloaded',
        timeout: 60_000,
      });
      if (preview.toLowerCase() === 'official') {
        await waitForOfficialSvg(page, auditCase);
        if (
          supportedAuditSources.slice(1).includes(auditSource) &&
          auditCase.kind === 'gantt'
        ) {
          await assertOfficialGanttWidth(page, auditCase.id);
        }
      } else {
        await waitForNativeCanvas(page);
      }
      await page.evaluate(() => new Promise((resolveFrame) => {
        requestAnimationFrame(() => requestAnimationFrame(resolveFrame));
      }));

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

async function waitForOfficialSvg(page, auditCase) {
  const outcomeHandle = await page.waitForFunction(
    () => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        const frame = root.querySelector?.(
          'iframe[title="Official Mermaid.js rendering"]',
        );
        if (frame?.contentDocument?.querySelector('#diagram svg') != null) {
          return { status: 'ready' };
        }
        const error = frame?.contentDocument?.querySelector('#diagram.error');
        if (error != null) {
          return {
            status: 'error',
            message: error.textContent?.trim() ?? 'Unknown rendering error',
          };
        }
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) {
            roots.push(element.shadowRoot);
          }
        });
      }
      return null;
    },
    { timeout: 60_000 },
  );
  const outcome = await outcomeHandle.jsonValue();
  await outcomeHandle.dispose();
  if (outcome.status === 'error') {
    throw new Error(
      `${auditCase.id}/Official Mermaid.js rendering failed: ${outcome.message}`,
    );
  }
  const expectedTexts = auditCase.expectedTexts ?? [];
  if (expectedTexts.length > 0) {
    const officialText = await page.evaluate(() => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        const frame = root.querySelector?.(
          'iframe[title="Official Mermaid.js rendering"]',
        );
        const svg = frame?.contentDocument?.querySelector('#diagram svg');
        if (svg != null) {
          return svg.textContent ?? '';
        }
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) {
            roots.push(element.shadowRoot);
          }
        });
      }
      return '';
    });
    for (const expectedText of expectedTexts) {
      if (!officialText.includes(expectedText)) {
        throw new Error(
          `${auditCase.id}/Official output is missing expected text: ${expectedText}`,
        );
      }
    }
  }
}

async function assertOfficialGanttWidth(page, caseId) {
  const dimensions = await page.evaluate(() => {
    const roots = [document];
    for (let index = 0; index < roots.length; index += 1) {
      const root = roots[index];
      const frame = root.querySelector?.(
        'iframe[title="Official Mermaid.js rendering"]',
      );
      const svg = frame?.contentDocument?.querySelector('#diagram svg');
      if (frame != null && svg != null) {
        return {
          frameWidth: frame.clientWidth,
          viewBoxWidth: svg.viewBox.baseVal.width,
        };
      }
      root.querySelectorAll?.('*').forEach((element) => {
        if (element.shadowRoot !== null) {
          roots.push(element.shadowRoot);
        }
      });
    }
    return null;
  });
  if (
    dimensions == null ||
    dimensions.frameWidth <= 0 ||
    dimensions.viewBoxWidth < dimensions.frameWidth * 0.75
  ) {
    throw new Error(
      `${caseId}/Official Gantt width collapsed: ` +
        `${dimensions?.viewBoxWidth ?? 'missing'} viewBox units for ` +
        `${dimensions?.frameWidth ?? 'missing'} frame pixels`,
    );
  }
}

function readKotlinCases(fileName, constructorName) {
  const sourcePath = resolve(
    repositoryRoot,
    `mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/${fileName}`,
  );
  const kotlin = readFileSync(sourcePath, 'utf8');
  const casePattern = new RegExp(
    `${constructorName}\\(\\s*` +
      'id = "([^"]+)",\\s*' +
      'title = "([^"]+)",\\s*' +
      'category = "([^"]+)",',
    'g',
  );
  return [...kotlin.matchAll(casePattern)].map((match) => ({
    id: match[1],
    title: match[2],
    category: match[3],
  }));
}
