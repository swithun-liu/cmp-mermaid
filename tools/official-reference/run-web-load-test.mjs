import fs from 'node:fs';
import path from 'node:path';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { cases } from './stability-corpus.mjs';

const baseUrl = process.env.BASE_URL ?? 'http://127.0.0.1:8093/';
const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);
const outputDirectory = path.resolve(
  repositoryRoot,
  process.env.OUTPUT_DIR ?? 'captures/local/load-test-web',
);
const maximumFirstContentMillis = readNumber(
  'MAXIMUM_FIRST_CONTENT_MILLIS',
  15_000,
);
const maximumScrollMillis = readNumber('MAXIMUM_SCROLL_MILLIS', 15_000);
const maximumRetainedJsHeapBytes = readNumber(
  'MAXIMUM_RETAINED_JS_HEAP_BYTES',
  96 * 1024 * 1024,
);
const lastCase = cases.at(-1);
if (lastCase == null) {
  throw new Error('The stability corpus is empty');
}

fs.mkdirSync(outputDirectory, { recursive: true });
const browser = await puppeteer.launch({ headless: 'shell' });
const errors = [];

try {
  const page = await browser.newPage();
  page.on('pageerror', (error) => errors.push(error.message));
  page.on('console', (message) => {
    if (message.type() === 'error') errors.push(message.text());
  });
  await page.setViewport({ width: 1200, height: 900, deviceScaleFactor: 1 });

  const startedAt = performance.now();
  await page.goto(new URL('?openLoadTest=true', baseUrl).toString(), {
    waitUntil: 'domcontentloaded',
    timeout: 60_000,
  });
  await waitForDeepText(page, cases[0].title);
  const firstContentMillis = Math.round(performance.now() - startedAt);
  await page.screenshot({ path: path.join(outputDirectory, 'top.png') });

  const cdp = await page.createCDPSession();
  await cdp.send('Performance.enable');
  await cdp.send('HeapProfiler.collectGarbage');
  const before = await performanceMetrics(cdp);
  let peakProcessRssBytes = processRssKilobytes(browser.process().pid) * 1024;
  const scrollStartedAt = performance.now();
  await page.mouse.move(600, 700);
  let reachedLastCase = false;
  let scrollEvents = 0;
  while (scrollEvents < 80 && !reachedLastCase) {
    await page.mouse.wheel({ deltaY: 700 });
    await sleep(35);
    scrollEvents += 1;
    if (scrollEvents % 5 === 0) {
      peakProcessRssBytes = Math.max(
        peakProcessRssBytes,
        processRssKilobytes(browser.process().pid) * 1024,
      );
      reachedLastCase = await hasDeepText(page, lastCase.title);
    }
  }
  await sleep(500);
  const scrollMillis = Math.round(performance.now() - scrollStartedAt);
  const afterScroll = await performanceMetrics(cdp);
  await cdp.send('HeapProfiler.collectGarbage');
  const afterGc = await performanceMetrics(cdp);
  const finalProcessRssBytes = processRssKilobytes(browser.process().pid) * 1024;
  await page.screenshot({ path: path.join(outputDirectory, 'bottom.png') });

  const retainedJsHeapBytes = Math.max(
    0,
    afterGc.JSHeapUsedSize - before.JSHeapUsedSize,
  );
  const report = {
    schemaVersion: 1,
    generatedAt: new Date().toISOString(),
    caseCount: cases.length,
    firstCaseId: cases[0].id,
    lastCaseId: lastCase.id,
    firstContentMillis,
    scrollMillis,
    scrollEvents,
    reachedLastCase,
    before,
    afterScroll,
    afterGc,
    retainedJsHeapBytes,
    peakProcessRssBytes,
    finalProcessRssBytes,
    errors,
    thresholds: {
      maximumFirstContentMillis,
      maximumScrollMillis,
      maximumRetainedJsHeapBytes,
    },
  };
  fs.writeFileSync(
    path.join(outputDirectory, 'metrics.json'),
    `${JSON.stringify(report, null, 2)}\n`,
  );
  console.log(JSON.stringify(report, null, 2));

  const failures = [];
  if (!reachedLastCase) {
    failures.push(`Did not reach ${lastCase.id}`);
  }
  if (firstContentMillis > maximumFirstContentMillis) {
    failures.push(
      `First content took ${firstContentMillis}ms; ` +
        `budget is ${maximumFirstContentMillis}ms`,
    );
  }
  if (scrollMillis > maximumScrollMillis) {
    failures.push(
      `Scrolling took ${scrollMillis}ms; budget is ${maximumScrollMillis}ms`,
    );
  }
  if (retainedJsHeapBytes > maximumRetainedJsHeapBytes) {
    failures.push(
      `Retained JS heap was ${retainedJsHeapBytes} bytes; ` +
        `budget is ${maximumRetainedJsHeapBytes} bytes`,
    );
  }
  if (errors.length > 0) {
    failures.push(`${errors.length} browser console/page errors`);
  }
  if (failures.length > 0) {
    throw new Error(`Web load test failed:\n${failures.join('\n')}`);
  }
} finally {
  await browser.close();
}

async function performanceMetrics(cdp) {
  const response = await cdp.send('Performance.getMetrics');
  const included = new Set([
    'JSHeapUsedSize',
    'JSHeapTotalSize',
    'TaskDuration',
    'LayoutCount',
    'RecalcStyleCount',
  ]);
  return Object.fromEntries(
    response.metrics
      .filter((metric) => included.has(metric.name))
      .map((metric) => [metric.name, metric.value]),
  );
}

async function waitForDeepText(page, text) {
  await page.waitForFunction(
    (expected) => {
      const roots = [document];
      for (let index = 0; index < roots.length; index += 1) {
        const root = roots[index];
        if ((root.textContent ?? '').includes(expected)) return true;
        root.querySelectorAll?.('*').forEach((element) => {
          if (element.shadowRoot !== null) roots.push(element.shadowRoot);
        });
      }
      return false;
    },
    { timeout: 60_000 },
    text,
  );
}

async function hasDeepText(page, text) {
  return page.evaluate((expected) => {
    const roots = [document];
    for (let index = 0; index < roots.length; index += 1) {
      const root = roots[index];
      if ((root.textContent ?? '').includes(expected)) return true;
      root.querySelectorAll?.('*').forEach((element) => {
        if (element.shadowRoot !== null) roots.push(element.shadowRoot);
      });
    }
    return false;
  }, text);
}

function processRssKilobytes(rootProcessId) {
  const processes = execFileSync('ps', ['-axo', 'pid=,ppid=,rss='], {
    encoding: 'utf8',
  }).trim().split('\n').map((line) => line.trim().split(/\s+/).map(Number));
  const children = new Map();
  for (const [processId, parentProcessId, rss] of processes) {
    if (!children.has(parentProcessId)) children.set(parentProcessId, []);
    children.get(parentProcessId).push({ processId, rss });
  }
  let total = 0;
  const pending = [rootProcessId];
  const visited = new Set();
  while (pending.length > 0) {
    const processId = pending.pop();
    if (visited.has(processId)) continue;
    visited.add(processId);
    const process = processes.find(([candidate]) => candidate === processId);
    if (process != null) total += process[2];
    for (const child of children.get(processId) ?? []) {
      pending.push(child.processId);
    }
  }
  return total;
}

function readNumber(name, fallback) {
  const source = process.env[name];
  if (source == null || source.length === 0) return fallback;
  const value = Number(source);
  if (!Number.isFinite(value)) {
    throw new Error(`${name} must be a finite number`);
  }
  return value;
}

function sleep(milliseconds) {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
}
