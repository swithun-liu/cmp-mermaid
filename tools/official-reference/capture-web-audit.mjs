import {
  existsSync,
  mkdirSync,
  readFileSync,
  statSync,
  writeFileSync,
} from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import {
  auditCaseIdentityPrefix,
  manifestMatchesAuditCase,
  validateAuditCaseIdentity,
} from './audit-source-identity.mjs';
import { invalidSourceCaseMatchesScope } from './audit-source-selection.mjs';
import { cases as flowchartCases } from './cases.mjs';
import { cases as invalidSourceCases } from './invalid-source-corpus.mjs';
import { cases as productionCases } from './production-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';
import { cases as swimlaneDdltCases } from './swimlane-ddlt-corpus.mjs';
import { cases as visualParityCases } from './visual-structure-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const baseUrl = process.env.BASE_URL ?? 'http://127.0.0.1:8093/';
const outputDirectory = resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/web-audit/current',
);
const auditKind = process.env.AUDIT_KIND ?? 'all';
const auditSource = process.env.AUDIT_SOURCE ?? 'gallery';
const invalidSourceScope = process.env.INVALID_SOURCE_SCOPE ?? 'full';
const layoutOverride = process.env.CAPTURE_LAYOUT ?? null;
const themeOverride = process.env.CAPTURE_THEME ?? null;
const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);
const previews = (process.env.CAPTURE_PREVIEWS ?? 'Native Official')
  .split(/[\s,]+/)
  .filter(Boolean);
const capturePreviews = auditSource === 'invalid-source'
  ? [...previews].sort((left, right) => {
      const rank = (value) => value.toLowerCase() === 'official' ? 0 : 1;
      return rank(left) - rank(right);
    })
  : previews;
const captureCombinedPreviews =
  auditSource !== 'invalid-source' &&
  capturePreviews.length === 2 &&
  capturePreviews.some((preview) => preview.toLowerCase() === 'native') &&
  capturePreviews.some((preview) => preview.toLowerCase() === 'official');
const viewportWidth = Number(process.env.VIEWPORT_WIDTH ?? 900);
const viewportHeight = Number(process.env.VIEWPORT_HEIGHT ?? 900);
const pageBatchSize = Number(process.env.CAPTURE_PAGE_BATCH_SIZE ?? 0);
const minimumCaptureBytes = Number(
  process.env.MIN_CAPTURE_BYTES ??
    (auditSource === 'invalid-source' ? 1_000 : 5_000),
);
const skipExisting = (process.env.SKIP_EXISTING ?? 'false') === 'true';

const kotlinGalleryFiles = {
  swimlanes: ['SwimlaneDemos.kt', 'SwimlaneDemo'],
  architecture: ['ArchitectureDemos.kt', 'ArchitectureDemo'],
  c4: ['C4Demos.kt', 'C4Demo'],
  railroad: ['RailroadDemos.kt', 'RailroadDemo'],
  treeview: ['TreeViewDemos.kt', 'TreeViewDemo'],
  xychart: ['XyChartDemos.kt', 'XyChartDemo'],
  quadrant: ['QuadrantDemos.kt', 'QuadrantDemo'],
  timeline: ['TimelineDemos.kt', 'TimelineDemo'],
  kanban: ['KanbanDemos.kt', 'KanbanDemo'],
  sequence: ['SequenceDemos.kt', 'SequenceDemo'],
  class: ['ClassDemos.kt', 'ClassDemo'],
  state: ['StateDemos.kt', 'StateDemo'],
  er: ['ErDemos.kt', 'ErDemo'],
  gantt: ['GanttDemos.kt', 'GanttDemo'],
  pie: ['PieDemos.kt', 'PieDemo'],
  journey: ['JourneyDemos.kt', 'JourneyDemo'],
  requirement: ['RequirementDemos.kt', 'RequirementDemo'],
  gitgraph: ['GitGraphDemos.kt', 'GitGraphDemo'],
  mindmap: ['MindmapDemos.kt', 'MindmapDemo'],
  packet: ['PacketDemos.kt', 'PacketDemo'],
  radar: ['RadarDemos.kt', 'RadarDemo'],
  sankey: ['SankeyDemos.kt', 'SankeyDemo'],
  treemap: ['TreemapDemos.kt', 'TreemapDemo'],
  venn: ['VennDemos.kt', 'VennDemo'],
  ishikawa: ['IshikawaDemos.kt', 'IshikawaDemo'],
  cynefin: ['CynefinDemos.kt', 'CynefinDemo'],
  block: ['BlockDemos.kt', 'BlockDemo'],
  eventmodeling: ['EventModelingDemos.kt', 'EventModelingDemo'],
  agentflow: ['AgentflowDemos.kt', 'AgentflowDemo'],
  usecase: ['UsecaseDemos.kt', 'UsecaseDemo'],
  wardley: ['WardleyDemos.kt', 'WardleyDemo'],
  zenuml: ['ZenUmlDemos.kt', 'ZenUmlDemo'],
};
const supportedAuditKinds = ['all', 'flowchart', ...Object.keys(kotlinGalleryFiles)];
const supportedAuditSources = [
  'gallery',
  'stability',
  'production',
  'swimlane-ddlt',
  'visual-parity',
  'invalid-source',
];
const supportedThemes = [
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

if (!supportedAuditKinds.includes(auditKind)) {
  throw new Error(`AUDIT_KIND must be one of: ${supportedAuditKinds.join(', ')}`);
}
if (!supportedAuditSources.includes(auditSource)) {
  throw new Error(`AUDIT_SOURCE must be one of: ${supportedAuditSources.join(', ')}`);
}
if (!['full', 'smoke'].includes(invalidSourceScope)) {
  throw new Error('INVALID_SOURCE_SCOPE must be full or smoke');
}
if (!Number.isInteger(pageBatchSize) || pageBatchSize < 0) {
  throw new Error('CAPTURE_PAGE_BATCH_SIZE must be a non-negative integer');
}
if (
  auditSource === 'invalid-source' &&
  (
    !capturePreviews.some((preview) => preview.toLowerCase() === 'official') ||
    !capturePreviews.some((preview) => preview.toLowerCase() === 'native')
  )
) {
  throw new Error(
    'AI-mutation audit requires both Official and Native previews',
  );
}
if (
  layoutOverride !== null &&
  !['dagre', 'elk', 'cose-bilkent', 'tidy-tree', 'swimlane'].includes(layoutOverride)
) {
  throw new Error(
    'CAPTURE_LAYOUT must be dagre, elk, cose-bilkent, tidy-tree, or swimlane',
  );
}
if (themeOverride !== null && !supportedThemes.includes(themeOverride)) {
  throw new Error(`CAPTURE_THEME must be one of: ${supportedThemes.join(', ')}`);
}

const corpusCases = {
  stability: stabilityCases,
  production: productionCases,
  'swimlane-ddlt': swimlaneDdltCases,
  'visual-parity': visualParityCases,
  'invalid-source': invalidSourceCases,
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
  (entry) =>
    (selectedIds.size === 0 || selectedIds.has(entry.id)) &&
    (
      auditSource !== 'invalid-source' ||
      invalidSourceCaseMatchesScope(entry, invalidSourceScope)
    ),
);

if (availableCases.length === 0) {
  throw new Error('No matching audit cases were found');
}

mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch(puppeteerLaunchOptions);
try {
  let page = await createCapturePage(browser);
  let casesOnPage = 0;

  for (const auditCase of availableCases) {
    if (pageBatchSize > 0 && casesOnPage >= pageBatchSize) {
      await page.close();
      page = await createCapturePage(browser);
      casesOnPage = 0;
    }
    casesOnPage += 1;
    if (captureCombinedPreviews) {
      await captureCombinedPreviewPair(page, auditCase);
      continue;
    }
    let officialOutcome = null;
    for (const preview of capturePreviews) {
      const renderer = preview.toLowerCase();
      const effectiveAuditCase = auditSource === 'invalid-source'
        ? {
            ...auditCase,
            expectedOutcome:
              renderer === 'official' ? 'oracle' : officialOutcome,
          }
        : auditCase;
      if (
        auditSource === 'invalid-source' &&
        renderer === 'native' &&
        officialOutcome === null
      ) {
        throw new Error(
          `${auditCase.id}/Native capture started before the Official oracle`,
        );
      }
      const suffix = [
        themeOverride,
        renderer,
      ].filter(Boolean).join('_');
      const target = resolve(
        outputDirectory,
        `${auditCase.id}_${suffix}.png`,
      );
      const manifestTarget = target.replace(/\.png$/, '.manifest.json');
      if (
        skipExisting &&
        existsSync(target) &&
        statSync(target).size >= minimumCaptureBytes &&
        existsSync(manifestTarget)
      ) {
        const existingManifest = readReusableManifest(
          manifestTarget,
          auditCase,
        );
        if (existingManifest !== null) {
          if (renderer === 'official' && auditSource === 'invalid-source') {
            officialOutcome = existingManifest.outcome;
          }
          continue;
        }
      }
      const url = new URL(baseUrl);
      const captureLayout = layoutOverride ?? auditCase.layout ?? 'dagre';
      url.searchParams.set('auditDemoId', auditCase.id);
      url.searchParams.set('auditSource', auditSource);
      url.searchParams.set('auditPreview', preview);
      url.searchParams.set('auditLayout', captureLayout);
      if (themeOverride !== null) {
        url.searchParams.set('auditTheme', themeOverride);
      }
      await page.goto(url.href, {
        waitUntil: 'domcontentloaded',
        timeout: 60_000,
      });
      const captureIdentity = await readBrowserAuditIdentity(page, auditCase);
      let manifest = null;
      if (renderer === 'official') {
        manifest = await waitForOfficialSvg(page, effectiveAuditCase);
        if (
          manifest?.outcome !== 'error' &&
          supportedAuditSources.slice(1).includes(auditSource) &&
          auditCase.kind === 'gantt'
        ) {
          await assertOfficialGanttWidth(page, auditCase.id);
        }
      } else {
        manifest = await waitForNativeCanvas(page, effectiveAuditCase);
      }
      await page.evaluate(() => new Promise((resolveFrame) => {
        requestAnimationFrame(() => requestAnimationFrame(resolveFrame));
      }));
      if (renderer === 'official' && manifest === null) {
        manifest = await extractOfficialManifest(page, auditCase.id);
      }
      manifest = {
        ...manifest,
        outcome: manifest.outcome ?? 'success',
        ...captureIdentity,
        ...(auditSource === 'invalid-source'
          ? { sourceFingerprint: auditCase.sourceFingerprint }
          : {}),
      };
      if (renderer === 'official' && auditSource === 'invalid-source') {
        officialOutcome = manifest.outcome;
      }
      if (manifest.outcome !== 'error') {
        assertExpectedTexts(auditCase, preview, manifest);
      }
      writeFileSync(
        manifestTarget,
        `${JSON.stringify(manifest, null, 2)}\n`,
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
  `Captured ${availableCases.length * capturePreviews.length} Web audit images to ` +
    outputDirectory,
);

async function createCapturePage(browser) {
  const page = await browser.newPage();
  await page.setViewport({
    width: viewportWidth,
    height: viewportHeight,
    deviceScaleFactor: 1,
  });
  return page;
}

async function captureCombinedPreviewPair(page, auditCase) {
  const themeSuffix = themeOverride === null ? '' : `_${themeOverride}`;
  const nativeTarget = resolve(
    outputDirectory,
    `${auditCase.id}${themeSuffix}_native.png`,
  );
  const officialTarget = resolve(
    outputDirectory,
    `${auditCase.id}${themeSuffix}_official.png`,
  );
  const nativeManifestTarget = nativeTarget.replace(/\.png$/, '.manifest.json');
  const officialManifestTarget =
    officialTarget.replace(/\.png$/, '.manifest.json');
  if (
    skipExisting &&
    reusableCaptureExists(nativeTarget, nativeManifestTarget, auditCase) &&
    reusableCaptureExists(officialTarget, officialManifestTarget, auditCase)
  ) {
    return;
  }

  const url = new URL(baseUrl);
  const captureLayout = layoutOverride ?? auditCase.layout ?? 'dagre';
  url.searchParams.set('auditDemoId', auditCase.id);
  url.searchParams.set('auditSource', auditSource);
  url.searchParams.set('auditPreview', 'Both');
  url.searchParams.set('auditLayout', captureLayout);
  if (themeOverride !== null) {
    url.searchParams.set('auditTheme', themeOverride);
  }
  await page.goto(url.href, {
    waitUntil: 'domcontentloaded',
    timeout: 60_000,
  });
  const captureIdentity = await readBrowserAuditIdentity(page, auditCase);
  const nativeManifest = await waitForNativeCanvas(page, auditCase);
  let officialManifest = await waitForOfficialSvg(page, auditCase);
  officialManifest ??= await extractOfficialManifest(page, auditCase.id);
  if (officialManifest === null) {
    throw new Error(
      `${auditCase.id}/Official did not expose an audit manifest`,
    );
  }
  assertExpectedTexts(auditCase, 'Native', nativeManifest);
  assertExpectedTexts(auditCase, 'Official', officialManifest);
  writeFileSync(
    nativeManifestTarget,
    `${JSON.stringify({ ...nativeManifest, ...captureIdentity }, null, 2)}\n`,
  );
  writeFileSync(
    officialManifestTarget,
    `${JSON.stringify({ ...officialManifest, ...captureIdentity }, null, 2)}\n`,
  );

  await setOfficialFrameVisibility(page, false);
  await page.screenshot({ path: nativeTarget, omitBackground: false });
  await setOfficialFrameVisibility(page, true);
  await page.screenshot({ path: officialTarget, omitBackground: false });
  for (const [preview, target] of [
    ['Native', nativeTarget],
    ['Official', officialTarget],
  ]) {
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

function reusableCaptureExists(target, manifestTarget, auditCase) {
  if (
    !existsSync(target) ||
    statSync(target).size < minimumCaptureBytes ||
    !existsSync(manifestTarget)
  ) {
    return false;
  }
  return readReusableManifest(manifestTarget, auditCase) !== null;
}

async function setOfficialFrameVisibility(page, visible) {
  const frameCount = await page.evaluate((show) => {
    const roots = [document];
    let count = 0;
    for (let index = 0; index < roots.length; index += 1) {
      const root = roots[index];
      for (
        const frame of root.querySelectorAll?.(
          'iframe[title="Official Mermaid.js rendering"]',
        ) ?? []
      ) {
        frame.style.visibility = show ? 'visible' : 'hidden';
        count += 1;
      }
      root.querySelectorAll?.('*').forEach((element) => {
        if (element.shadowRoot !== null) {
          roots.push(element.shadowRoot);
        }
      });
    }
    return count;
  }, visible);
  if (frameCount === 0) {
    throw new Error('Combined audit page has no Official Mermaid iframe');
  }
  await page.evaluate(() => new Promise((resolveFrame) => {
    requestAnimationFrame(() => requestAnimationFrame(resolveFrame));
  }));
}

function readReusableManifest(path, auditCase) {
  try {
    const manifest = JSON.parse(readFileSync(path, 'utf8'));
    const outcome = manifest.outcome ??
      (typeof manifest.message === 'string' ? 'error' : 'success');
    if (!['success', 'error'].includes(outcome)) {
      return null;
    }
    if (!manifestMatchesAuditCase(manifest, auditCase, auditSource)) {
      return null;
    }
    if (
      auditSource === 'invalid-source' &&
      manifest.sourceFingerprint !== auditCase.sourceFingerprint
    ) {
      return null;
    }
    return { ...manifest, outcome };
  } catch {
    return null;
  }
}

async function readBrowserAuditIdentity(page, auditCase) {
  const identityHandle = await page.waitForFunction(
    (prefix) => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        for (const element of root.querySelectorAll?.('[aria-label]') ?? []) {
          const label = element.getAttribute('aria-label') ?? '';
          if (label.startsWith(prefix) && label !== `${prefix}loading`) {
            return label.slice(prefix.length);
          }
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
    auditCaseIdentityPrefix,
  );
  const encodedIdentity = await identityHandle.jsonValue();
  await identityHandle.dispose();
  let identity;
  try {
    identity = JSON.parse(encodedIdentity);
  } catch (error) {
    throw new Error(
      `${auditCase.id} returned an invalid audit case identity: ${error}`,
    );
  }
  return validateAuditCaseIdentity(identity, auditCase, auditSource);
}

async function waitForNativeCanvas(page, auditCase) {
  const outcomeHandle = await page.waitForFunction(
    () => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        for (const element of root.querySelectorAll?.('[aria-label]') ?? []) {
          const label = element.getAttribute('aria-label') ?? '';
          if (label.startsWith('cmp-mermaid-audit:ready:')) {
            return {
              status: 'ready',
              manifest: label.slice('cmp-mermaid-audit:ready:'.length),
            };
          }
          if (label.startsWith('cmp-mermaid-audit:error:')) {
            return {
              status: 'error',
              message: label.slice('cmp-mermaid-audit:error:'.length),
            };
          }
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
    return expectedErrorManifest(auditCase, 'native', outcome.message);
  }
  if (auditCase.expectedOutcome === 'error') {
    throw new Error(
      `${auditCase.id}/Native unexpectedly rendered malformed source`,
    );
  }
  let manifest;
  try {
    manifest = JSON.parse(outcome.manifest);
  } catch (error) {
    throw new Error(
      `${auditCase.id}/Native returned an invalid audit manifest: ${error}`,
    );
  }
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
  return manifest;
}

async function waitForOfficialSvg(page, auditCase) {
  const outcomeHandle = await page.waitForFunction(
    (expectedOutcome) => {
      const roots = [document];
      let errorMessage = null;
      let ready = false;
      let frame = null;
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        frame ??= root.querySelector?.(
          'iframe[title="Official Mermaid.js rendering"]',
        ) ?? null;
        for (const element of root.querySelectorAll?.('[aria-label]') ?? []) {
          const label = element.getAttribute('aria-label') ?? '';
          if (label.startsWith('cmp-mermaid-audit:error:')) {
            errorMessage = label.slice('cmp-mermaid-audit:error:'.length);
          }
          if (label.startsWith('cmp-mermaid-audit:ready:')) {
            ready = true;
          }
        }
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) {
            roots.push(element.shadowRoot);
          }
        });
      }
      const svg = frame?.contentDocument?.querySelector('#diagram svg');
      if (expectedOutcome === 'error') {
        if (errorMessage !== null) {
          return {
            status: svg == null ? 'error-without-svg' : 'error',
            message: errorMessage,
          };
        }
        return null;
      }
      if (expectedOutcome === 'oracle' && errorMessage !== null) {
        return {
          status: svg == null ? 'error-without-svg' : 'error',
          message: errorMessage,
        };
      }
      if (expectedOutcome === 'oracle') {
        return ready && svg != null ? { status: 'ready' } : null;
      }
      if (svg != null) {
        return { status: 'ready' };
      }
      const error = frame?.contentDocument?.querySelector('#diagram.error');
      if (error != null) {
        return {
          status: 'error',
          message: error.textContent?.trim() ?? 'Unknown rendering error',
        };
      }
      return null;
    },
    { timeout: 60_000 },
    auditCase.expectedOutcome,
  );
  const outcome = await outcomeHandle.jsonValue();
  await outcomeHandle.dispose();
  if (outcome.status === 'error-without-svg') {
    return expectedErrorManifest(
      auditCase,
      'official',
      outcome.message,
      'text',
    );
  }
  if (outcome.status === 'error') {
    return expectedErrorManifest(auditCase, 'official', outcome.message, 'svg');
  }
  if (auditCase.expectedOutcome === 'error') {
    throw new Error(
      `${auditCase.id}/Official Mermaid.js unexpectedly rendered malformed source`,
    );
  }
  return null;
}

function expectedErrorManifest(
  auditCase,
  renderer,
  payload,
  errorPresentation = null,
) {
  if (
    auditCase.expectedOutcome !== 'error' &&
    auditCase.expectedOutcome !== 'oracle'
  ) {
    throw new Error(
      `${auditCase.id}/${renderer} rendering failed: ${payload}`,
    );
  }
  const separator = renderer === 'native' ? payload.indexOf(':') : -1;
  const errorType = separator > 0 ? payload.slice(0, separator) : null;
  const message = (separator > 0 ? payload.slice(separator + 1) : payload).trim();
  if (message.length === 0) {
    throw new Error(`${auditCase.id}/${renderer} returned an empty error`);
  }
  if (
    renderer === 'native' &&
    errorType !== auditCase.expectedNativeErrorType
  ) {
    throw new Error(
      `${auditCase.id}/Native returned ${errorType ?? 'no error type'}; ` +
        `expected ${auditCase.expectedNativeErrorType}`,
    );
  }
  return {
    schemaVersion: 1,
    renderer,
    outcome: 'error',
    ...(errorType === null ? {} : { errorType }),
    ...(errorPresentation === null ? {} : { errorPresentation }),
    message,
  };
}

async function extractOfficialManifest(page, caseId) {
  const manifest = await page.evaluate(() => {
    const roots = [document];
    let frame = null;
    let svg = null;
    for (let index = 0; index < roots.length; index += 1) {
      const root = roots[index];
      frame = root.querySelector?.(
        'iframe[title="Official Mermaid.js rendering"]',
      );
      svg = frame?.contentDocument?.querySelector('#diagram svg');
      if (svg != null) break;
      root.querySelectorAll?.('*').forEach((element) => {
        if (element.shadowRoot !== null) {
          roots.push(element.shadowRoot);
        }
      });
    }
    if (svg == null) return null;

    const viewBox = svg.viewBox.baseVal;
    const width = viewBox.width > 0 ? viewBox.width : svg.clientWidth;
    const height = viewBox.height > 0 ? viewBox.height : svg.clientHeight;
    const inverseScreenMatrix = svg.getScreenCTM()?.inverse() ?? null;
    const excludedAncestors = 'defs,clipPath,mask,marker,pattern,symbol';
    const selector = [
      'path',
      'rect',
      'circle',
      'ellipse',
      'line',
      'polyline',
      'polygon',
      'text',
      'foreignObject',
      'image',
      'use',
    ].join(',');
    // Mermaid.js 12.0.0:
    // rendering-elements/shapes/erBox.ts -> rough.svg(...).
    // Rough.js emits multiple SVG paths for one semantic rectangle or divider.
    // Preserve the explicit wrapper-group identity so the detail audit can
    // compare semantic geometry rather than renderer-specific child counts.
    const roughGroupSelector = [
      'g.outer-path',
      'g.row-rect-even',
      'g.row-rect-odd',
      'g.divider',
    ].join(',');
    const roughGeometryGroups = new Map(
      [...svg.querySelectorAll(roughGroupSelector)]
        .map((group, index) => [group, `rough-${index}`]),
    );

    const number = (value, fallback = 1) => {
      const parsed = Number.parseFloat(value);
      return Number.isFinite(parsed) ? parsed : fallback;
    };
    const color = (value, paintOpacity, elementOpacity) => {
      if (value == null || value === '' || value === 'none') return null;
      if (value.startsWith('url(')) {
        return { reference: value };
      }
      const match = value.match(
        /^rgba?\(\s*([\d.]+)[,\s]+([\d.]+)[,\s]+([\d.]+)(?:\s*[,/]\s*([\d.]+%?))?\s*\)$/i,
      );
      if (match == null) {
        return { reference: value };
      }
      const sourceAlpha = match[4]?.endsWith('%')
        ? Number.parseFloat(match[4]) / 100
        : number(match[4], 1);
      const alpha = Math.max(
        0,
        Math.min(1, sourceAlpha * paintOpacity * elementOpacity),
      );
      return {
        r: Math.round(number(match[1], 0)),
        g: Math.round(number(match[2], 0)),
        b: Math.round(number(match[3], 0)),
        a: Math.round(alpha * 255),
      };
    };
    const bounds = (element) => {
      const rectangle = element.getBoundingClientRect();
      if (inverseScreenMatrix == null) {
        return {
          x: rectangle.left,
          y: rectangle.top,
          width: rectangle.width,
          height: rectangle.height,
        };
      }
      const corners = [
        new DOMPoint(rectangle.left, rectangle.top),
        new DOMPoint(rectangle.right, rectangle.top),
        new DOMPoint(rectangle.right, rectangle.bottom),
        new DOMPoint(rectangle.left, rectangle.bottom),
      ].map((point) => point.matrixTransform(inverseScreenMatrix));
      const xs = corners.map((point) => point.x);
      const ys = corners.map((point) => point.y);
      return {
        x: Math.min(...xs),
        y: Math.min(...ys),
        width: Math.max(...xs) - Math.min(...xs),
        height: Math.max(...ys) - Math.min(...ys),
      };
    };
    // DOM Range measures rendered content, independently of foreignObject's
    // layout box (edges.js -> setTerminalWidth can leave a large blank area).
    const textContentRects = (element, inverseMatrix) => {
      if (element.localName !== 'foreignObject') return null;
      const doc = element.ownerDocument;
      const walker = doc.createTreeWalker(element, NodeFilter.SHOW_TEXT);
      const rectangles = [];
      for (let node = walker.nextNode(); node != null; node = walker.nextNode()) {
        const value = node.textContent ?? '';
        const start = value.search(/\S/);
        if (start < 0 || node.parentElement == null) continue;
        const style = doc.defaultView.getComputedStyle(node.parentElement);
        if (style.display === 'none' || style.visibility === 'hidden') continue;
        const range = doc.createRange();
        range.setStart(node, start);
        range.setEnd(node, value.trimEnd().length);
        for (const rectangle of range.getClientRects()) {
          if (rectangle.width <= 0 || rectangle.height <= 0) continue;
          const corners = [
            new DOMPoint(rectangle.left, rectangle.top),
            new DOMPoint(rectangle.right, rectangle.top),
            new DOMPoint(rectangle.right, rectangle.bottom),
            new DOMPoint(rectangle.left, rectangle.bottom),
          ].map((point) => inverseMatrix == null
            ? point
            : point.matrixTransform(inverseMatrix));
          const xs = corners.map((point) => point.x);
          const ys = corners.map((point) => point.y);
          rectangles.push({
            x: Math.min(...xs), y: Math.min(...ys),
            width: Math.max(...xs) - Math.min(...xs),
            height: Math.max(...ys) - Math.min(...ys),
          });
        }
      }
      return rectangles.length > 0 ? rectangles : null;
    };
    const geometryEndpoints = (element) => {
      if (
        typeof element.getTotalLength !== 'function' ||
        typeof element.getPointAtLength !== 'function'
      ) {
        return { start: null, end: null };
      }
      try {
        const length = element.getTotalLength();
        const screenMatrix = element.getScreenCTM();
        if (
          !Number.isFinite(length) ||
          length <= 0 ||
          screenMatrix == null ||
          inverseScreenMatrix == null
        ) {
          return { start: null, end: null };
        }
        const transform = (point) => {
          const transformed = new DOMPoint(point.x, point.y)
            .matrixTransform(screenMatrix)
            .matrixTransform(inverseScreenMatrix);
          return Number.isFinite(transformed.x) && Number.isFinite(transformed.y)
            ? { x: transformed.x, y: transformed.y }
            : null;
        };
        return {
          start: transform(element.getPointAtLength(0)),
          end: transform(element.getPointAtLength(length)),
        };
      } catch {
        return { start: null, end: null };
      }
    };
    const transformGeometryPoint = (element, point) => {
      const screenMatrix = element.getScreenCTM();
      if (
        screenMatrix == null ||
        inverseScreenMatrix == null ||
        !Number.isFinite(point?.x) ||
        !Number.isFinite(point?.y)
      ) {
        return null;
      }
      const transformed = new DOMPoint(point.x, point.y)
        .matrixTransform(screenMatrix)
        .matrixTransform(inverseScreenMatrix);
      return Number.isFinite(transformed.x) && Number.isFinite(transformed.y)
        ? { x: transformed.x, y: transformed.y }
        : null;
    };
    const geometryOutlinePoints = (element, fill) => {
      if (fill == null) return [];
      const tag = element.localName;
      if (tag === 'polygon' || tag === 'polyline') {
        return [...element.points]
          .map((point) => transformGeometryPoint(element, point))
          .filter((point) => point !== null);
      }
      if (
        !['path', 'rect', 'circle', 'ellipse'].includes(tag) ||
        typeof element.getTotalLength !== 'function' ||
        typeof element.getPointAtLength !== 'function'
      ) {
        return [];
      }
      try {
        const length = element.getTotalLength();
        if (!Number.isFinite(length) || length <= 0) return [];
        const sampleCount = Math.min(
          512,
          Math.max(16, Math.ceil(length / 2)),
        );
        return Array.from(
          { length: sampleCount },
          (_, index) => element.getPointAtLength(length * index / sampleCount),
        )
          .map((point) => transformGeometryPoint(element, point))
          .filter((point) => point !== null);
      } catch {
        return [];
      }
    };
    const textOutlinePoints = (element) => {
      if (typeof element.getBBox !== 'function') return [];
      try {
        const localBounds = element.getBBox();
        if (
          !Number.isFinite(localBounds.x) ||
          !Number.isFinite(localBounds.y) ||
          !Number.isFinite(localBounds.width) ||
          !Number.isFinite(localBounds.height) ||
          localBounds.width <= 0 ||
          localBounds.height <= 0
        ) {
          return [];
        }
        return [
          { x: localBounds.x, y: localBounds.y },
          { x: localBounds.x + localBounds.width, y: localBounds.y },
          {
            x: localBounds.x + localBounds.width,
            y: localBounds.y + localBounds.height,
          },
          { x: localBounds.x, y: localBounds.y + localBounds.height },
        ]
          .map((point) => transformGeometryPoint(element, point))
          .filter((point) => point !== null);
      } catch {
        return [];
      }
    };
    const routePoints = (element) => {
      const encoded = element.getAttribute('data-points');
      if (encoded == null || encoded.length === 0) return [];
      try {
        const decoded = JSON.parse(atob(encoded));
        if (!Array.isArray(decoded)) return [];
        return decoded
          .map((point) => transformGeometryPoint(element, point))
          .filter((point) => point !== null);
      } catch {
        return [];
      }
    };
    const pathTopology = (points) => {
      const epsilon = 1e-4;
      const direction = (start, end) => {
        const dx = end.x - start.x;
        const dy = end.y - start.y;
        if (dx * dx + dy * dy <= epsilon * epsilon) return null;
        if (Math.abs(dy) <= epsilon) return dx > 0 ? 'R' : 'L';
        if (Math.abs(dx) <= epsilon) return dy > 0 ? 'D' : 'U';
        return 'Diagonal';
      };
      const deduplicated = [];
      for (const point of points) {
        const previous = deduplicated.at(-1);
        if (
          previous == null ||
          Math.hypot(point.x - previous.x, point.y - previous.y) > epsilon
        ) {
          deduplicated.push(point);
        }
      }
      const topologyPoints = [];
      for (const point of deduplicated) {
        while (topologyPoints.length >= 2) {
          const firstDirection = direction(
            topologyPoints.at(-2),
            topologyPoints.at(-1),
          );
          const secondDirection = direction(topologyPoints.at(-1), point);
          if (firstDirection == null || firstDirection !== secondDirection) break;
          topologyPoints.pop();
        }
        topologyPoints.push(point);
      }
      const directionSequence = topologyPoints
        .slice(0, -1)
        .map((point, index) => direction(point, topologyPoints[index + 1]))
        .filter((entry) => entry !== null);
      const sourceSide = {
        R: 'Right',
        L: 'Left',
        D: 'Bottom',
        U: 'Top',
      };
      const destinationSide = {
        R: 'Left',
        L: 'Right',
        D: 'Top',
        U: 'Bottom',
      };
      return {
        routePoints: points,
        topologyPoints,
        bendPoints: topologyPoints.slice(1, -1),
        directionSequence,
        sourcePortSide: sourceSide[directionSequence[0]] ?? null,
        destinationPortSide:
          destinationSide[directionSequence.at(-1)] ?? null,
      };
    };
    const normalizeText = (value) => value.replace(/\s+/g, ' ').trim();
    const renderedTextSegments = (element) =>
      element.localName === 'text'
        ? [...element.children]
          .filter((child) => child.localName === 'tspan')
          .map((line) => line.textContent ?? '')
        : [];
    const renderedText = (element) => {
      if (element.localName === 'foreignObject') {
        return element.firstElementChild?.innerText ?? element.textContent ?? '';
      }
      const segments = renderedTextSegments(element);
      if (segments.length > 0) {
        return segments.join(' ');
      }
      return element.textContent ?? '';
    };
    const elements = [];
    for (const element of svg.querySelectorAll(selector)) {
      if (element.closest(excludedAncestors) != null) continue;
      const style = getComputedStyle(element);
      if (
        style.display === 'none' ||
        style.visibility === 'hidden' ||
        number(style.opacity, 1) <= 0
      ) {
        continue;
      }
      const tag = element.localName;
      const textElement = tag === 'text' || tag === 'foreignObject';
      const usecaseIconFallback =
        textElement &&
        element.closest('.usecase-actor-icon-symbol') != null;
      const textSegments = textElement ? renderedTextSegments(element) : [];
      const text = textElement ? normalizeText(renderedText(element)) : null;
      const elementBounds = bounds(element);
      const nonRenderedText =
        textElement &&
        (
          text.length === 0 ||
          elementBounds.width <= 0 ||
          elementBounds.height <= 0
        );
      if (
        nonRenderedText ||
        (
          !textElement &&
          elementBounds.width <= 0 &&
          elementBounds.height <= 0
        )
      ) {
        continue;
      }
      const opacity = number(style.opacity, 1);
      const fill = color(
        style.fill,
        number(style.fillOpacity, 1),
        opacity,
      );
      const stroke = color(
        style.stroke,
        number(style.strokeOpacity, 1),
        opacity,
      );
      const type = usecaseIconFallback
        ? 'asset'
        : textElement
        ? 'text'
        : tag === 'image' || tag === 'use'
          ? 'asset'
          : tag === 'path' || tag === 'line' || tag === 'polyline'
            ? 'path'
            : 'shape';
      const dashArray = style.strokeDasharray === 'none'
        ? []
        : style.strokeDasharray
          .split(/[,\s]+/)
          .map((entry) => Number.parseFloat(entry))
          .filter(Number.isFinite);
      const dashCycle = dashArray.length % 2 === 0
        ? dashArray
        : [...dashArray, ...dashArray];
      const hasVisibleDashGap = dashCycle.some(
        (entry, index) => index % 2 === 1 && Math.abs(entry) > Number.EPSILON,
      );
      const markerStart = style.getPropertyValue('marker-start');
      const markerEnd = style.getPropertyValue('marker-end');
      const hasMarkerStart = markerStart && markerStart !== 'none';
      const hasMarkerEnd = markerEnd && markerEnd !== 'none';
      const markerPoints = hasMarkerStart || hasMarkerEnd
        ? geometryEndpoints(element)
        : { start: null, end: null };
      const topology = type === 'path'
        ? pathTopology(routePoints(element))
        : null;
      const roughGeometryGroup = element.closest(roughGroupSelector);
      const screenMatrix = tag === 'path' ? element.getScreenCTM() : null;
      const pathMatrix = screenMatrix != null && inverseScreenMatrix != null
        ? inverseScreenMatrix.multiply(screenMatrix)
        : null;
      elements.push({
        order: elements.length,
        zIndex: null,
        type,
        role: tag,
        id: element.id || null,
        edgeId: type === 'path'
          ? element.getAttribute('data-id') || null
          : null,
        classes: [...element.classList],
        geometryGroup: roughGeometryGroups.get(roughGeometryGroup) ?? null,
        bounds: elementBounds,
        ...(tag === 'path' ? {
          pathData: element.getAttribute('d'),
          pathTransform: pathMatrix == null ? null : {
            a: pathMatrix.a, b: pathMatrix.b,
            c: pathMatrix.c, d: pathMatrix.d,
          },
        } : {}),
        outlinePoints: textElement
          ? textOutlinePoints(element)
          : geometryOutlinePoints(element, fill),
        ...(textElement && !usecaseIconFallback ? {
          text,
          ...(tag === 'foreignObject' ? {
            textContentRects: textContentRects(element, inverseScreenMatrix),
          } : {}),
          ...(textSegments.length > 0 ? { textSegments } : {}),
          fontSize: number(style.fontSize, null),
          fontFamily: style.fontFamily || null,
          fontWeight: style.fontWeight || null,
        } : {}),
        fill,
        stroke,
        strokeWidth: number(style.strokeWidth, 0),
        strokePattern: hasVisibleDashGap ? 'Dashed' : 'Solid',
        dashIntervals: dashArray,
        arrowStart: hasMarkerStart ? 'Marker' : 'None',
        arrowEnd: hasMarkerEnd ? 'Marker' : 'None',
        markerStartPoint: hasMarkerStart ? markerPoints.start : null,
        markerEndPoint: hasMarkerEnd ? markerPoints.end : null,
        ...(topology ?? {}),
      });
    }
    const bodyStyle = getComputedStyle(frame.contentDocument.body);
    return {
      schemaVersion: 1,
      renderer: 'official',
      viewport: {
        x: viewBox.x,
        y: viewBox.y,
        width,
        height,
        background: color(bodyStyle.backgroundColor, 1, 1),
      },
      title: svg.querySelector(':scope > title')?.textContent ?? null,
      accessibilityTitle: svg.querySelector(':scope > title')?.textContent ?? null,
      accessibilityDescription:
        svg.querySelector(':scope > desc')?.textContent ?? null,
      elements,
    };
  });
  if (manifest == null) {
    throw new Error(`${caseId}/Official SVG disappeared before manifest export`);
  }
  return manifest;
}

function assertExpectedTexts(auditCase, preview, manifest) {
  const renderedText = [
    manifest.title,
    manifest.accessibilityTitle,
    manifest.accessibilityDescription,
    ...manifest.elements
      .filter((element) => element.type === 'text')
      .map((element) => element.text ?? ''),
  ]
    .filter((text) => text != null)
    .join('\n')
    .replace(/\s+/g, ' ')
    .trim();
  for (const expectedText of auditCase.expectedTexts ?? []) {
    const normalizedExpectedText = expectedText.replace(/\s+/g, ' ').trim();
    if (!renderedText.includes(normalizedExpectedText)) {
      throw new Error(
        `${auditCase.id}/${preview} output is missing expected text: ` +
          expectedText,
      );
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
