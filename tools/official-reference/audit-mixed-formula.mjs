import fs from 'node:fs';
import path from 'node:path';
import { compareTopologyManifests } from './topology-audit-core.mjs';

const directory = path.resolve(process.argv[2] ?? 'build/mixed-formula-audit');
const official = JSON.parse(fs.readFileSync(path.join(directory, 'official.json')));
const native = JSON.parse(fs.readFileSync(path.join(directory, 'native.json')));
const rasterFile = path.join(directory, 'raster.json');
const raster = fs.existsSync(rasterFile) ? JSON.parse(fs.readFileSync(rasterFile)) : null;
if (raster && (raster.officialGeneratedAt !== official.generatedAt ||
    raster.nativeGeneratedAt !== native.generatedAt)) {
  throw new Error('Raster evidence is stale; regenerate mixed-formula-contact-sheets.py');
}
const rasterById = new Map(raster?.cases.map(entry => [entry.id, entry]) ?? []);
const byId = new Map(native.cases.map(entry => [entry.id, entry]));
const cssText = value => value.replace(/[ \t\r\n\f]+/g, ' ').replace(/^ +| +$/g, '');
const epsilon = 0.08;

function officialItems(label) {
  const items = label.items.map(item => {
    const rect = item.rect ?? {
      x: item.glyphRect.x, width: item.glyphRect.width,
      y: label.row.y + (label.row.height - item.lineHeight) / 2,
      height: item.lineHeight,
    };
    return { kind: item.kind === 'formula' ? 'formula' : 'text',
      text: cssText(item.text), bounds: { ...rect } };
  });
  const left = items[0]?.bounds.x ?? label.row.x;
  return items.map(item => ({ ...item, bounds: {
    ...item.bounds, x: item.bounds.x - left, y: item.bounds.y - label.row.y,
  } }));
}

function topology(edges) {
  return { elements: edges.map((edge, index) => {
    const direction = (a, b) => {
      const dx = b.x - a.x, dy = b.y - a.y;
      if (Math.hypot(dx, dy) < 0.001) return null;
      if (Math.abs(dy) < 0.001) return dx > 0 ? 'R' : 'L';
      if (Math.abs(dx) < 0.001) return dy > 0 ? 'D' : 'U';
      return 'Diagonal';
    };
    const points = [];
    for (const [x, y] of edge.points) {
      const point = { x, y };
      if (points.length && direction(points.at(-1), point) === null) continue;
      while (points.length >= 2 &&
        direction(points.at(-2), points.at(-1)) === direction(points.at(-1), point)) {
        points.pop();
      }
      points.push(point);
    }
    const directions = points.slice(1).map((point, i) => direction(points[i], point));
    return { type: 'path', edgeId: `edge-${index}`, topologyPoints: points,
      bendPoints: points.slice(1, -1), directionSequence: directions,
      sourcePortSide: ({ R: 'Right', L: 'Left', D: 'Bottom', U: 'Top' })[directions[0]] ?? null,
      destinationPortSide: ({ R: 'Left', L: 'Right', D: 'Top', U: 'Bottom' })[directions.at(-1)] ?? null };
  }) };
}

const cases = official.cases.map(reference => {
  const actual = byId.get(reference.id);
  const findings = [];
  let failed = false;
  const fail = message => { findings.push(message); failed = true; };
  let maximumItemDelta = 0;
  let maximumLabelDelta = 0;
  if (!actual || actual.error) fail(actual?.error ?? 'Native case missing');
  if (actual?.providerFailures) fail('Formula provider failures');
  for (const formula of reference.formulas) {
    const channels = formula.color.match(/[\d.]+/g)?.map(Number);
    const expectedColor = channels?.length >= 3
      ? ((0xff000000 | channels[0] << 16 | channels[1] << 8 | channels[2]) >>> 0).toString(16)
      : null;
    if (!actual?.requests.some(request => request.source === formula.source &&
      Math.abs(request.fontSize - formula.fontSize) < epsilon &&
      request.colorArgb === expectedColor)) {
      fail('Formula font/color request differs from official inherited CSS');
    }
  }
  if ((actual?.labels.length ?? 0) !== reference.labels.length) fail('Label count mismatch');
  const labels = reference.labels.map((label, labelIndex) => {
    const expectedItems = officialItems(label);
    const result = actual?.labels[labelIndex];
    const width = expectedItems.reduce((sum, item) => sum + item.bounds.width, 0);
    const height = Math.max(0, ...expectedItems.map(item => item.bounds.height));
    if (result) {
      maximumItemDelta = Math.max(maximumItemDelta,
        Math.abs(result.intrinsicWidth - width), Math.abs(result.intrinsicHeight - height));
      maximumLabelDelta = Math.max(maximumLabelDelta,
        Math.abs(result.bounds.width - label.rect.width),
        Math.abs(result.bounds.height - label.rect.height));
      if (result.items.length !== expectedItems.length) fail(`Label ${labelIndex}: item count mismatch`);
      expectedItems.forEach((item, index) => {
        const measured = result.items[index];
        if (!measured) return;
        if (item.kind !== measured.kind) fail(`Label ${labelIndex}, item ${index}: kind mismatch`);
        if (item.kind === 'text' && item.text !== cssText(measured.text)) {
          fail(`Label ${labelIndex}, item ${index}: visible text mismatch`);
        }
        for (const axis of ['x', 'y', 'width', 'height']) {
          maximumItemDelta = Math.max(maximumItemDelta, Math.abs(item.bounds[axis] - measured.bounds[axis]));
        }
      });
    }
    return { official: { width, height, items: expectedItems }, native: result };
  });
  const nativeTopology = topology(actual?.edges ?? []);
  // Official path data is in SVG user coordinates; Native scene coordinates
  // start at the viewport origin. Normalize only the declared viewBox origin.
  const officialTopology = topology(reference.edges.map(edge => ({
    ...edge, points: edge.points.map(([x, y]) => [
      x - reference.viewBox.x, y - reference.viewBox.y,
    ]),
  })));
  const topologyAudit = compareTopologyManifests(nativeTopology, officialTopology);
  let maximumRouteDelta = 0;
  nativeTopology.elements.forEach((edge, index) => {
    const referenceEdge = officialTopology.elements[index];
    if (!referenceEdge) return;
    edge.topologyPoints.forEach((point, pointIndex) => {
      const referencePoint = referenceEdge.topologyPoints[pointIndex];
      if (referencePoint) maximumRouteDelta = Math.max(maximumRouteDelta,
        Math.abs(point.x - referencePoint.x), Math.abs(point.y - referencePoint.y));
    });
  });
  if (topologyAudit.status === 'fail') fail('Edge topology mismatch');
  else if (topologyAudit.status !== 'pass') findings.push('Missing route evidence');
  if (maximumRouteDelta > epsilon) findings.push(`Route endpoint/bend coordinates differ by ${maximumRouteDelta.toFixed(4)} px`);
  if (maximumItemDelta > epsilon) findings.push(`Intrinsic item geometry differs by ${maximumItemDelta.toFixed(4)} px`);
  if (maximumLabelDelta > epsilon) findings.push(`Outer label geometry differs by ${maximumLabelDelta.toFixed(4)} px`);
  const rasterEvidence = rasterById.get(reference.id);
  if (!rasterEvidence) findings.push('Raster evidence pending');
  else if (rasterEvidence.ssim < 0.97) findings.push(`Raster SSIM is ${rasterEvidence.ssim.toFixed(4)}`);
  const status = failed ? 'fail' : findings.length === 0 ? 'pass' : 'review';
  return { id: reference.id, source: reference.source, status, findings,
    maximumItemDelta, maximumLabelDelta, maximumRouteDelta,
    ssim: rasterEvidence?.ssim ?? null, labels, topology: topologyAudit };
});
const counts = Object.fromEntries(['pass', 'review', 'fail'].map(status =>
  [status, cases.filter(entry => entry.status === status).length]));
const report = { schemaVersion: 1, generatedAt: new Date().toISOString(),
  mermaidVersion: official.mermaidVersion, mermaidSourceCommit: official.mermaidSourceCommit,
  counts, caseCount: cases.length, screenshotCount: cases.length * 2,
  coordinateTolerancePx: epsilon,
  boundary: 'Native uses official intrinsic formula metrics/pixels through the optional provider. This tests shared label composition and Canvas placement, not native KaTeX parsing or full-family stability.',
  cases };
fs.writeFileSync(path.join(directory, 'geometry.json'), JSON.stringify(report, null, 2));
const escape = value => String(value).replace(/[&<>"']/g, c => ({
  '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
})[c]);
fs.writeFileSync(path.join(directory, 'index.html'), `<!doctype html>
<html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width">
<title>Mermaid 12.0.0 mixed formula audit</title>
<style>body{font:15px system-ui;margin:32px;background:#f5f7fa;color:#202936}
main{max-width:1200px;margin:auto}article{background:white;border:1px solid #dce1e8;
border-radius:10px;margin:20px 0;padding:20px}.pair{display:grid;grid-template-columns:1fr 1fr;
gap:20px;overflow:auto}img{max-width:100%;height:auto;image-rendering:auto}
pre{overflow:auto;font:13px ui-monospace;background:#eef1f5;padding:14px}
.pass{color:#08754e}.review{color:#956100}small{color:#5b6878}
@media(max-width:700px){.pair{grid-template-columns:1fr}body{margin:12px}}</style>
<main><h1>Mixed text and formula labels</h1>
<p>Mermaid ${escape(report.mermaidVersion)} · ${report.caseCount} shared sources ·
${report.screenshotCount} screenshots · ${counts.pass} Pass / ${counts.review} Review / ${counts.fail} Fail</p>
<p>${escape(report.boundary)}</p><p>Per-item geometry tolerance: ${epsilon} px.
Edge checks compare ports, bends, direction sequences, shared tracks and crossings.
Review remains required for raster/font rendering and any missing topology evidence.</p>
<p><a href="official.json">Official geometry</a> · <a href="native.json">Native geometry</a> ·
<a href="geometry.json">Detailed comparison</a></p>
<p>${raster?.contactSheets.map(file => `<a href="${escape(file)}">${escape(file)}</a>`).join(' · ') ?? ''}</p>
${cases.map(entry => `<article><h2 class="${entry.status}">${escape(entry.id)} — ${entry.status.toUpperCase()}</h2>
<p>${entry.findings.length ? escape(entry.findings.join('; ')) : 'Item and outer-label geometry match; recorded edge topology matches.'}</p>
<div class="pair"><div><strong>Native</strong><br><img src="${entry.id}-native.png" alt="Native ${entry.id}"></div>
<div><strong>Official</strong><br><img src="${entry.id}-official.png" alt="Official ${entry.id}"></div></div>
<details><summary>Shared Mermaid source and geometry</summary><pre>${escape(entry.source)}</pre>
<pre>${escape(JSON.stringify({ maximumItemDelta: entry.maximumItemDelta,
maximumLabelDelta: entry.maximumLabelDelta, topology: entry.topology }, null, 2))}</pre></details></article>`).join('')}
</main></html>`);
console.log(JSON.stringify({ counts, caseCount: cases.length, screenshotCount: report.screenshotCount,
  reviews: cases.filter(entry => entry.status !== 'pass').map(entry => ({
    id: entry.id, findings: entry.findings,
  })) }, null, 2));
