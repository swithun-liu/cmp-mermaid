import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { mermaidBaseline } from './diagram-inventory.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';

const directory = path.dirname(fileURLToPath(import.meta.url));
const mermaidBundle = path.resolve(
  directory,
  'node_modules/mermaid/dist/mermaid.min.js',
);
const mermaidPackage = JSON.parse(
  fs.readFileSync(
    path.resolve(directory, 'node_modules/mermaid/package.json'),
    'utf8',
  ),
);

export const formulaMetricsTheme = 'redux-color';
export const formulaMetricsMathFont = 'CMP Mermaid Audit Math (Noto Sans Math)';

export const formulaMetricCases = Object.freeze([
  {
    id: 'flowchart-fraction',
    source: String.raw`flowchart LR
  A["$$\frac{a+b}{c+d}$$"] --> B[Done]`,
  },
  {
    id: 'state-transition',
    source: String.raw`stateDiagram-v2
  Pending --> Approved : $$\sum_{i=1}^{n} i$$`,
  },
  {
    id: 'kanban-section',
    source: String.raw`kanban
  todo[$$x^2$$]
    task[Review]`,
  },
  {
    id: 'usecase-relation',
    source: 'usecase-beta\n  A -- "`$$E = mc^2$$`" --> B\n',
  },
  {
    id: 'long-formula',
    source: String.raw`flowchart TD
  A["$$\sum_{k=1}^{50}\frac{k^2+2k+1}{k+1}$$"] --> B[Bounded]`,
  },
]);

export async function captureOfficialFormulaMetrics() {
  if (mermaidPackage.version !== mermaidBaseline.version) {
    throw new Error(
      `Installed Mermaid is ${mermaidPackage.version}; ` +
        `expected ${mermaidBaseline.version}`,
    );
  }

  const browser = await puppeteer.launch(puppeteerLaunchOptions);
  try {
    const page = await browser.newPage();
    await page.setViewport({
      width: 1200,
      height: 900,
      deviceScaleFactor: 1,
    });
    await page.setContent(
      '<!doctype html><html><body><div id="container"></div></body></html>',
    );
    // A system's generic MathML font is not a reproducible fixture input.
    // Bind the existing public font resource only in this reference harness.
    const fontBytes = fs.readFileSync(path.resolve(
      directory,
      '../../mermaid-compose/src/commonMain/composeResources/font/noto_sans_math_regular.ttf',
    ));
    await page.evaluate(async ({ family, data }) => {
      const font = new FontFace(family, `url(data:font/ttf;base64,${data})`);
      document.fonts.add(await font.load());
      const style = document.createElement('style');
      style.textContent = `math { font-family: "${family}" !important; }`;
      document.head.appendChild(style);
    }, { family: formulaMetricsMathFont, data: fontBytes.toString('base64') });
    await page.addScriptTag({ path: mermaidBundle });
    await page.evaluate((theme) => {
      globalThis.mermaid.initialize({
        startOnLoad: false,
        securityLevel: 'strict',
        theme,
      });
    }, formulaMetricsTheme);

    const cases = [];
    for (const entry of formulaMetricCases) {
      cases.push(
        await page.evaluate(async ({ id, source }) => {
          const container = document.querySelector('#container');
          container.replaceChildren();
          const { svg } = await globalThis.mermaid.render(
            `formula-metrics-${id}`,
            source,
            container,
          );
          container.innerHTML = svg;

          const math = container.querySelector('math');
          const foreignObject = math?.closest('foreignObject');
          const layoutBox = foreignObject?.firstElementChild;
          if (!(math instanceof Element) || !(layoutBox instanceof Element)) {
            throw new Error(`Official Mermaid produced no formula box for ${id}`);
          }

          const metric = (element) => {
            const bounds = element.getBoundingClientRect();
            const style = getComputedStyle(element);
            const number = (value) =>
              Number(Number.parseFloat(value).toFixed(6));
            return {
              width: number(bounds.width),
              height: number(bounds.height),
              fontSize: number(style.fontSize),
              lineHeight:
                style.lineHeight === 'normal'
                  ? null
                  : number(style.lineHeight),
              maxWidth:
                style.maxWidth === 'none'
                  ? null
                  : number(style.maxWidth),
              paddingLeft: number(style.paddingLeft),
              paddingRight: number(style.paddingRight),
              display: style.display,
            };
          };

          return {
            id,
            source,
            content: metric(math),
            layout: metric(layoutBox),
          };
        }, entry),
      );
    }

    return {
      schemaVersion: 1,
      mermaidVersion: mermaidBaseline.version,
      mermaidSourceCommit: mermaidBaseline.sourceCommit,
      theme: formulaMetricsTheme,
      mathFont: formulaMetricsMathFont,
      deviceScaleFactor: 1,
      cases,
    };
  } finally {
    await browser.close();
  }
}
