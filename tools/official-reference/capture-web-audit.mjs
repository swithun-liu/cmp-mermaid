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
const themeOverride = process.env.CAPTURE_THEME ?? null;
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
};
const supportedAuditKinds = ['all', 'flowchart', ...Object.keys(kotlinGalleryFiles)];
const supportedAuditSources = [
  'gallery',
  'stability',
  'production',
  'visual-parity',
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
if (
  layoutOverride !== null &&
  !['dagre', 'elk', 'cose-bilkent', 'tidy-tree'].includes(layoutOverride)
) {
  throw new Error(
    'CAPTURE_LAYOUT must be dagre, elk, cose-bilkent, or tidy-tree',
  );
}
if (themeOverride !== null && !supportedThemes.includes(themeOverride)) {
  throw new Error(`CAPTURE_THEME must be one of: ${supportedThemes.join(', ')}`);
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
      const suffix = [
        themeOverride,
        preview.toLowerCase(),
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
        continue;
      }
      const url = new URL(baseUrl);
      const captureLayout = layoutOverride ?? auditCase.layout ?? 'dagre';
      url.searchParams.set('auditDemoId', auditCase.id);
      url.searchParams.set('auditPreview', preview);
      url.searchParams.set('auditLayout', captureLayout);
      if (themeOverride !== null) {
        url.searchParams.set('auditTheme', themeOverride);
      }
      await page.goto(url.href, {
        waitUntil: 'domcontentloaded',
        timeout: 60_000,
      });
      let manifest;
      if (preview.toLowerCase() === 'official') {
        await waitForOfficialSvg(page, auditCase);
        if (
          supportedAuditSources.slice(1).includes(auditSource) &&
          auditCase.kind === 'gantt'
        ) {
          await assertOfficialGanttWidth(page, auditCase.id);
        }
      } else {
        manifest = await waitForNativeCanvas(page, auditCase);
      }
      await page.evaluate(() => new Promise((resolveFrame) => {
        requestAnimationFrame(() => requestAnimationFrame(resolveFrame));
      }));
      if (preview.toLowerCase() === 'official') {
        manifest = await extractOfficialManifest(page, auditCase.id);
      }
      assertExpectedTexts(auditCase, preview, manifest);
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
  `Captured ${availableCases.length * previews.length} Web audit images to ` +
    outputDirectory,
);

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
    throw new Error(
      `${auditCase.id}/Native rendering failed: ${outcome.message}`,
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
      const type = textElement
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
      elements.push({
        order: elements.length,
        zIndex: null,
        type,
        role: tag,
        id: element.id || null,
        classes: [...element.classList],
        bounds: elementBounds,
        ...(textElement ? {
          text,
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
  const renderedText = manifest.elements
    .filter((element) => element.type === 'text')
    .map((element) => element.text ?? '')
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
