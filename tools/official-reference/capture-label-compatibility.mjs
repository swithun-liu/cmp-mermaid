import fs from 'node:fs';
import path from 'node:path';
import puppeteer from 'puppeteer';
import { labelCompatibilityCases } from './label-compatibility-corpus.mjs';
import { mermaidBaseline } from './diagram-inventory.mjs';

const output = path.resolve(process.argv[2] ?? 'build/label-compatibility');
fs.mkdirSync(output, { recursive: true });
const installed = JSON.parse(fs.readFileSync('tools/official-reference/node_modules/mermaid/package.json'));
if (installed.version !== mermaidBaseline.version) throw new Error('Mermaid baseline mismatch');
const browser = await puppeteer.launch({
  headless: true, executablePath: process.env.PUPPETEER_EXECUTABLE_PATH,
  args: ['--no-sandbox'],
});
try {
  const page = await browser.newPage();
  await page.setViewport({ width: 2400, height: 1800, deviceScaleFactor: 1 });
  await page.setContent('<!doctype html><style>body{margin:0;background:white}</style><div id="container"></div>');
  for (const [suffix, weight, style] of [
    ['regular', '400', 'normal'], ['bold', '700', 'normal'],
    ['italic', '400', 'italic'], ['bolditalic', '700', 'italic'],
  ]) {
    const bytes = fs.readFileSync(`mermaid-compose/src/commonMain/composeResources/font/arimo_${suffix}.ttf`);
    await page.evaluate(async ({ data, weight, style }) => {
      const font = new FontFace('Arial', `url(data:font/ttf;base64,${data})`, { weight, style });
      document.fonts.add(await font.load());
    }, { data: bytes.toString('base64'), weight, style });
  }
  await page.addScriptTag({ path: 'tools/official-reference/node_modules/mermaid/dist/mermaid.min.js' });
  const cases = [];
  for (const entry of labelCompatibilityCases) {
    const captured = await page.evaluate(async entry => {
      const { id, source } = entry;
      mermaid.initialize({ startOnLoad: false, securityLevel: 'strict',
        layout: 'dagre', theme: 'redux-color', fontFamily: 'Arial',
        themeVariables: { fontFamily: 'Arial' } });
      const container = document.querySelector('#container');
      try {
        container.innerHTML = (await mermaid.render(`label-${id}`, source, container)).svg;
      } catch (error) {
        return { ...entry, error: String(error) };
      }
      await document.fonts.ready;
      const svg = container.querySelector('svg');
      const viewBox = svg.viewBox.baseVal;
      Object.assign(svg.style, { maxWidth: 'none', width: `${viewBox.width}px`, height: `${viewBox.height}px` });
      const rect = node => {
        const r = node.getBoundingClientRect();
        return { x: r.x, y: r.y, width: r.width, height: r.height };
      };
      const nodeId = node => node.getAttribute('data-id') ||
        node.id.replace(/^flowchart-/, '').replace(/-\d+$/, '');
      const nodes = [...svg.querySelectorAll('g.node, g.cluster')].map(node => {
        const shape = [...node.children].find(child =>
          ['rect', 'circle', 'ellipse', 'polygon', 'path'].includes(child.tagName));
        return { id: nodeId(node), bounds: rect(shape ?? node),
          radius: shape?.getAttribute('rx'), fill: shape && getComputedStyle(shape).fill };
      });
      const labels = [...svg.querySelectorAll('foreignObject')].map(node => ({
        text: node.firstElementChild?.innerText ?? node.textContent,
        bounds: rect(node.firstElementChild),
        owner: node.closest('g.node, g.cluster') && nodeId(node.closest('g.node, g.cluster')),
      }));
      const edges = [...svg.querySelectorAll('path[data-points]')].map(node => {
        const matrix = node.getScreenCTM();
        const inverse = svg.getScreenCTM().inverse();
        const transform = point => {
          const p = new DOMPoint(point.x, point.y).matrixTransform(matrix).matrixTransform(inverse);
          return [p.x, p.y];
        };
        return { id: node.id, d: node.getAttribute('d'),
          points: JSON.parse(atob(node.getAttribute('data-points'))).map(transform),
          start: transform(node.getPointAtLength(0)),
          end: transform(node.getPointAtLength(node.getTotalLength())),
          markerStart: node.getAttribute('marker-start'), markerEnd: node.getAttribute('marker-end') };
      });
      return { ...entry, layout: mermaid.mermaidAPI.getConfig().layout,
        nodes, labels, edges, svgRect: rect(svg),
        viewBox: { x: viewBox.x, y: viewBox.y, width: viewBox.width, height: viewBox.height },
        svg: svg.outerHTML };
    }, entry);
    if (!captured.error) {
      await (await page.$('#container > svg')).screenshot({ path: path.join(output, `${entry.id}-official.png`) });
      fs.writeFileSync(path.join(output, `${entry.id}-official.svg`), captured.svg);
      delete captured.svg;
    }
    cases.push(captured);
    console.log(`${entry.id}: ${captured.error ?? `${captured.nodes.length} nodes, ${captured.labels.length} labels`}`);
  }
  fs.writeFileSync(path.join(output, 'official.json'), JSON.stringify({
    schemaVersion: 1, mermaidVersion: mermaidBaseline.version,
    mermaidSourceCommit: mermaidBaseline.sourceCommit, generatedAt: new Date().toISOString(),
    fontFamily: 'Arial (bundled Arimo)', deviceScaleFactor: 1, cases,
  }, null, 2));
} finally {
  await browser.close();
}
