import assert from 'node:assert/strict';
import fs from 'node:fs';
import test from 'node:test';
import puppeteer from 'puppeteer';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { compareDetailManifests } from './detail-audit-core.mjs';

// Execute the capture's unchanged DOM helper in a real browser, so the test
// exercises Range geometry rather than a fabricated foreignObject rectangle.
const captureSource = fs.readFileSync(
  new URL('./capture-web-audit.mjs', import.meta.url), 'utf8',
);
const begin = captureSource.indexOf('    const textContentRects = ');
const end = captureSource.indexOf('    const geometryEndpoints = ', begin);
assert.ok(begin >= 0 && end > begin);
const helper = captureSource.slice(begin, end);

test('DOM content bounds distinguish blank HTML-container overlap from text occlusion', async () => {
  const browser = await puppeteer.launch(puppeteerLaunchOptions);
  try {
    const page = await browser.newPage();
    await page.setContent(`
      <svg xmlns="http://www.w3.org/2000/svg" width="400" height="120"
           viewBox="0 0 200 60">
        <g transform="translate(10 10)">
          <foreignObject width="90" height="24">
            <div xmlns="http://www.w3.org/1999/xhtml"
                 style="font:16px Arial;white-space:nowrap">1</div>
          </foreignObject>
        </g>
      </svg>`);
    const captured = await page.evaluate((source) => {
      const collect = new Function(`${source}; return textContentRects;`)();
      const svg = document.querySelector('svg');
      const element = svg.querySelector('foreignObject');
      return {
        textContentRects: collect(element, svg.getScreenCTM().inverse()),
        bounds: { x: 10, y: 10, width: 90, height: 24 },
      };
    }, helper);
    assert.equal(captured.textContentRects.length, 1);
    const content = captured.textContentRects[0];
    assert.ok(content.width > 5 && content.width < 12);
    assert.ok(Math.abs(content.x - 10) < 0.01);
    const text = { type: 'text', text: '1', order: 0, ...captured };
    const shape = (x, order) => ({
      type: 'shape', order,
      bounds: { x, y: 10, width: 60, height: 24 },
      fill: { r: 255, g: 255, b: 255, a: 255 },
    });
    const manifest = (elements) => ({
      schemaVersion: 1, viewport: { x: 0, y: 0, width: 200, height: 60 }, elements,
    });
    const nativeText = { ...text, bounds: content, textContentRects: undefined };
    const blankOnly = compareDetailManifests(
      manifest([nativeText, shape(40, 1)]),
      manifest([text, shape(40, 1)]),
    );
    assert.equal(blankOnly.paintOrderComparison.nativeOccludedTextCount, 0);
    assert.equal(blankOnly.paintOrderComparison.officialOccludedTextCount, 0);
    assert.deepEqual(blankOnly.paintOrderComparison.mismatches, []);
    const trueOcclusion = compareDetailManifests(
      manifest([shape(10, 0), { ...nativeText, order: 1 }]),
      manifest([text, shape(10, 1)]),
    );
    assert.equal(trueOcclusion.status, 'fail');
    assert.equal(trueOcclusion.paintOrderComparison.officialOccludedTextCount, 1);
    assert.ok(trueOcclusion.findings.some(({ code }) => code === 'paint-order-occlusion'));
  } finally {
    await browser.close();
  }
});

test('multiline DOM ranges retain separate rows and ignore hidden text', async () => {
  const browser = await puppeteer.launch(puppeteerLaunchOptions);
  try {
    const page = await browser.newPage();
    await page.setContent(`
      <svg xmlns="http://www.w3.org/2000/svg" width="200" height="100">
        <foreignObject width="180" height="90">
          <div xmlns="http://www.w3.org/1999/xhtml" style="font:16px Arial">
            A<br/>B<span style="display:none">hidden</span>
          </div>
        </foreignObject>
      </svg>`);
    const rectangles = await page.evaluate((source) => {
      const collect = new Function(`${source}; return textContentRects;`)();
      const svg = document.querySelector('svg');
      return collect(svg.querySelector('foreignObject'), svg.getScreenCTM().inverse());
    }, helper);
    assert.equal(rectangles.length, 2);
    assert.ok(rectangles.every(({ width }) => width < 15));
    assert.ok(rectangles[1].y > rectangles[0].y);
  } finally {
    await browser.close();
  }
});
