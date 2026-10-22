import fs from 'node:fs';
import path from 'node:path';
import puppeteer from 'puppeteer';
import { mixedFormulaCases } from './mixed-formula-corpus.mjs';
import { mermaidBaseline } from './diagram-inventory.mjs';

const output = path.resolve(process.argv[2] ?? 'build/mixed-formula-audit');
fs.mkdirSync(output, { recursive: true });
const browser = await puppeteer.launch({
  headless: true,
  executablePath: process.env.PUPPETEER_EXECUTABLE_PATH,
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
  const installed = JSON.parse(fs.readFileSync('tools/official-reference/node_modules/mermaid/package.json'));
  if (installed.version !== mermaidBaseline.version) throw new Error('Mermaid version mismatch');
  const cases = [];
  for (const entry of mixedFormulaCases) {
    const captured = await page.evaluate(async ({ id, source }) => {
      mermaid.initialize({ startOnLoad: false, securityLevel: 'strict',
        theme: 'redux-color', fontFamily: 'Arial',
        themeVariables: { fontFamily: 'Arial' } });
      const container = document.querySelector('#container');
      container.innerHTML = (await mermaid.render(`mixed-${id}`, source, container)).svg;
      await document.fonts.ready;
      // Audit scene coordinates independently of responsive SVG viewport scaling.
      const svg = container.querySelector('svg');
      const viewBox = svg.viewBox.baseVal;
      Object.assign(svg.style, { maxWidth: 'none',
        width: `${viewBox.width}px`, height: `${viewBox.height}px` });
      const rect = node => {
        const r = node.getBoundingClientRect();
        return { x: r.x, y: r.y, width: r.width, height: r.height };
      };
      const math = [...container.querySelectorAll('math')];
      const sources = [...source.matchAll(/\$\$(.*?)\$\$/g)].map(match => match[1].replace(/\\\\/g, '\\'));
      if (sources.length !== math.length) throw new Error(`${id}: formula count mismatch`);
      const formulas = math.map((node, index) => ({
        source: sources[index], rect: rect(node),
        fontSize: parseFloat(getComputedStyle(node).fontSize),
        color: getComputedStyle(node).color,
      }));
      const boxes = [...container.querySelectorAll('foreignObject')].filter(node => node.querySelector('math'));
      const edges = [...container.querySelectorAll('path[data-points]')].map(node => {
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
          markerStart: node.getAttribute('marker-start'),
          markerEnd: node.getAttribute('marker-end') };
      });
      const labels = boxes.map(box => {
        const div = box.firstElementChild;
        const row = [...div.querySelectorAll('div')].find(node => getComputedStyle(node).display === 'flex');
        return {
          rect: rect(div), row: rect(row),
          items: [...row.childNodes].map(node => {
            if (node.nodeType === Node.TEXT_NODE) {
              const text = node.textContent.replace(/[ \t\r\n\f]+/g, ' ').replace(/^ +| +$/g, '');
              if (!text) return null;
              const range = document.createRange();
              range.selectNodeContents(node);
              return { kind: 'text', text, glyphRect: rect(range),
                lineHeight: parseFloat(getComputedStyle(row).lineHeight) };
            }
            return { kind: node.querySelector('math') ? 'formula' : 'html',
              text: node.textContent, rect: rect(node),
              lineHeight: parseFloat(getComputedStyle(node).lineHeight) };
          }).filter(Boolean),
        };
      });
      return { id, source, svgRect: rect(svg), viewBox: {
        x: viewBox.x, y: viewBox.y, width: viewBox.width, height: viewBox.height,
      }, formulas, labels, edges };
    }, entry);
    await page.$eval('#container > svg', node => {
      const r = node.getBoundingClientRect();
      node.style.maxWidth = 'none';
      node.style.width = `${r.width}px`;
      node.style.height = `${r.height}px`;
    });
    await (await page.$('#container > svg')).screenshot({ path: path.join(output, `${entry.id}-official.png`) });
    await page.$eval('#container', node => { node.style.visibility = 'hidden'; });
    await page.evaluate(() => { document.body.style.background = 'transparent'; });
    for (let i = 0; i < captured.formulas.length; i++) {
      const imageBox = await page.evaluate(index => {
        const node = document.querySelectorAll('#container math')[index];
        const css = getComputedStyle(node);
        const holder = document.createElement('div');
        holder.id = 'formula-artifact';
        Object.assign(holder.style, { position: 'absolute', left: '0', top: '0',
          fontSize: css.fontSize, fontFamily: css.fontFamily, color: css.color,
          padding: '8px', background: 'transparent', width: 'max-content' });
        holder.appendChild(node.parentElement.cloneNode(true));
        document.body.appendChild(holder);
        const r = holder.querySelector('math').getBoundingClientRect();
        const clip = { x: Math.floor(r.x) - 8, y: Math.floor(r.y) - 8,
          width: Math.ceil(r.width) + 16, height: Math.ceil(r.height) + 16 };
        return { clip, offsetX: r.x - clip.x, offsetY: r.y - clip.y };
      }, i);
      await page.screenshot({ path: path.join(output, `${entry.id}-formula-${i}.png`),
        clip: imageBox.clip, omitBackground: true });
      await page.$eval('#formula-artifact', node => node.remove());
      captured.formulas[i].image = `${entry.id}-formula-${i}.png`;
      captured.formulas[i].imageOffsetX = imageBox.offsetX;
      captured.formulas[i].imageOffsetY = imageBox.offsetY;
    }
    await page.evaluate(() => { document.body.style.background = 'white'; });
    await page.$eval('#container', node => { node.style.visibility = ''; });
    cases.push(captured);
    console.log(`${entry.id}: ${captured.formulas.length} formulas, ${captured.labels.length} labels`);
  }
  fs.writeFileSync(path.join(output, 'official.json'), JSON.stringify({
    schemaVersion: 1, mermaidVersion: mermaidBaseline.version,
    mermaidSourceCommit: mermaidBaseline.sourceCommit,
    generatedAt: new Date().toISOString(), theme: 'redux-color',
    fontFamily: 'Arial (bundled Arimo)', deviceScaleFactor: 1, cases,
  }, null, 2));
} finally {
  await browser.close();
}
