import fs from 'node:fs';
import path from 'node:path';
import puppeteer from 'puppeteer';
import { mermaidBaseline } from './diagram-inventory.mjs';

const output = path.resolve(process.argv[2] ?? 'build/container-width');
fs.mkdirSync(output, { recursive: true });
const installed = JSON.parse(fs.readFileSync('tools/official-reference/node_modules/mermaid/package.json'));
if (installed.version !== mermaidBaseline.version) throw new Error('Mermaid baseline mismatch');
const gantt = `gantt
  title Release schedule
  dateFormat YYYY-MM-DD
  axisFormat %b %d
  todayMarker off
  section Build
  Implementation checks :build, 2025-01-01, 4d
  Review :review, after build, 2d
  section Release
  Publish :publish, after review, 1d`;
const mindmap = 'mindmap\n  root((Root))\n    Alpha\n      Child\n    Beta';
const cases = [
  ...[344, 768, 1200].map(containerWidth => ({
    id: `gantt-parent-${containerWidth}`, kind: 'gantt', source: gantt, containerWidth,
  })),
  { id: 'gantt-source-1200', kind: 'gantt', source: `---\nconfig:\n  gantt:\n    useWidth: 1200\n---\n${gantt}`, containerWidth: 344 },
  { id: 'gantt-host-900', kind: 'gantt', source: gantt, containerWidth: 344, useWidth: 900 },
  ...['dagre', 'cose-bilkent'].flatMap(layout => ['classic', 'neo'].map(look => ({
    id: `mindmap-${layout}-${look}`, kind: 'mindmap', source: mindmap,
    containerWidth: 768, layout, look,
  }))),
];
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
    const data = fs.readFileSync(`mermaid-compose/src/commonMain/composeResources/font/arimo_${suffix}.ttf`).toString('base64');
    await page.evaluate(async ({ data, weight, style }) => {
      const font = new FontFace('Arial', `url(data:font/ttf;base64,${data})`, { weight, style });
      document.fonts.add(await font.load());
    }, { data, weight, style });
  }
  await page.addScriptTag({ path: 'tools/official-reference/node_modules/mermaid/dist/mermaid.min.js' });
  const results = [];
  for (const entry of cases) {
    const result = await page.evaluate(async entry => {
      const layout = entry.layout ?? 'dagre';
      const look = entry.look ?? 'classic';
      mermaid.initialize({
        startOnLoad: false, securityLevel: 'strict', theme: 'redux-color',
        layout, look, fontFamily: 'Arial', themeVariables: { fontFamily: 'Arial' },
        ...(entry.useWidth === undefined ? {} : { gantt: { useWidth: entry.useWidth } }),
      });
      const container = document.querySelector('#container');
      container.style.width = `${entry.containerWidth}px`;
      container.innerHTML = (await mermaid.render(`width-${entry.id}`, entry.source, container)).svg;
      await document.fonts.ready;
      const svg = container.querySelector('svg');
      const box = svg.viewBox.baseVal;
      Object.assign(svg.style, { maxWidth: 'none', width: `${box.width}px`, height: `${box.height}px` });
      const bounds = node => {
        const r = node.getBBox();
        return { x: r.x, y: r.y, width: r.width, height: r.height };
      };
      const point = (node, p) => {
        const r = new DOMPoint(p.x, p.y).matrixTransform(node.getScreenCTM())
          .matrixTransform(svg.getScreenCTM().inverse());
        return { x: r.x - box.x, y: r.y - box.y };
      };
      const sceneBounds = node => {
        const r = node.getBBox();
        const start = point(node, { x: r.x, y: r.y });
        const end = point(node, { x: r.x + r.width, y: r.y + r.height });
        return { x: start.x, y: start.y, width: end.x - start.x, height: end.y - start.y };
      };
      const paint = node => {
        const css = getComputedStyle(node);
        return { fill: css.fill, stroke: css.stroke, strokeWidth: parseFloat(css.strokeWidth) };
      };
      const tasks = [...svg.querySelectorAll('rect.task')].map(node => ({
        id: node.id, bounds: bounds(node), ...paint(node),
      }));
      const labels = [...svg.querySelectorAll('text')].map(node => ({
        text: node.textContent.trim(), bounds: sceneBounds(node),
        anchorX: Number(node.getAttribute('x')), anchor: getComputedStyle(node).textAnchor,
        className: node.getAttribute('class'),
      }));
      const nodes = [...svg.querySelectorAll('g.mindmap-node')].map((node, index) => {
        const shape = [...node.children].find(child => ['rect', 'path', 'circle', 'polygon'].includes(child.tagName));
        return { id: `node-${index}`, text: node.textContent, bounds: sceneBounds(shape),
          ...paint(shape), look: node.getAttribute('data-look') };
      });
      const edges = [...svg.querySelectorAll('path.edge')].map(node => {
        const points = node.getAttribute('data-points');
        return { id: node.id, ...paint(node), d: node.getAttribute('d'),
          start: point(node, node.getPointAtLength(0)),
          end: point(node, node.getPointAtLength(node.getTotalLength())),
          points: points ? JSON.parse(atob(points)).map(p => point(node, p)) : null };
      });
      return { ...entry, layout, look, theme: 'redux-color',
        width: box.width, height: box.height, tasks, labels, nodes, edges };
    }, entry);
    await (await page.$('#container > svg')).screenshot({ path: path.join(output, `${entry.id}-official.png`) });
    results.push(result);
    console.log(`${entry.id}: ${result.width} x ${result.height}`);
  }
  fs.writeFileSync(path.join(output, 'official.json'), JSON.stringify({
    schemaVersion: 1, mermaidVersion: mermaidBaseline.version,
    mermaidSourceCommit: mermaidBaseline.sourceCommit, generatedAt: new Date().toISOString(),
    fontFamily: 'Arial (bundled Arimo)', cases: results,
  }, null, 2));
} finally {
  await browser.close();
}
