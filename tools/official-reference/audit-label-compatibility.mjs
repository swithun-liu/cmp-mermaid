import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import puppeteer from 'puppeteer';
import { compareTopologyManifests } from './topology-audit-core.mjs';

const directory = path.resolve(process.argv[2] ?? 'build/label-compatibility');
const inputBytes = {};
const read = file => {
  inputBytes[file] = fs.readFileSync(path.join(directory, file));
  return JSON.parse(inputBytes[file]);
};
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
const official = read('official.json');
const native = read('native.json');
const raster = read('raster.json');
const normalize = text => text.replace(/\s+/g, ' ').trim();
const nodeId = (id, caseId) => id.replace(/^subgraph_/, '')
  .replace(`label-${caseId}-`, '').replace(/^flowchart-/, '')
  .replace(/^entity-(.+)-\d+$/, '$1');
const edgeId = id => {
  if (id.includes('L_')) return id.slice(id.indexOf('L_'));
  const er = id.match(/id_entity-(.+?)-\d+_entity-(.+?)-\d+_(\d+)$/);
  if (er) return `id${er[1]}-${er[2]}-${Number(er[3]) + 1}`;
  const nativeEr = id.match(/^identity-(.+?)-\d+-entity-(.+?)-\d+-(\d+)$/);
  if (nativeEr) return `id${nativeEr[1]}-${nativeEr[2]}-${nativeEr[3]}`;
  return id.replace(/^label-.+-(edge\d+)$/, '$1');
};
const near = (a, b, tolerance = 0.02) => Math.abs(a - b) <= tolerance;
const nativeById = new Map(native.cases.map(entry => [entry.id, entry]));
const isLabelBackground = node => /[_-]label[_-]background$/.test(node.id);

function port(point, nodes) {
  let best = null;
  for (const node of nodes) {
    const b = node.bounds;
    const sides = [['L', Math.abs(point[0] - b.x)], ['R', Math.abs(point[0] - b.x - b.width)],
      ['U', Math.abs(point[1] - b.y)], ['D', Math.abs(point[1] - b.y - b.height)]];
    for (const [side, distance] of sides) {
      const inside = side === 'L' || side === 'R'
        ? point[1] >= b.y - 0.02 && point[1] <= b.y + b.height + 0.02
        : point[0] >= b.x - 0.02 && point[0] <= b.x + b.width + 0.02;
      if (inside && (!best || distance < best.distance)) best = { side, distance };
    }
  }
  return best?.distance < 0.1 ? best.side : null;
}

function topology(entry) {
  const nodes = entry.nodes.filter(node => !isLabelBackground(node));
  return { elements: entry.edges.map(edge => {
    const points = edge.points.map(([x, y]) => ({ x, y }));
    const directions = points.slice(1).map((p, i) => {
      const a = points[i];
      if (near(a.x, p.x)) return p.y > a.y ? 'D' : 'U';
      if (near(a.y, p.y)) return p.x > a.x ? 'R' : 'L';
      return 'Diagonal';
    });
    const bends = points.filter((_, i) => i > 0 && i < points.length - 1 &&
      directions[i - 1] !== directions[i]);
    return { type: 'path', edgeId: edgeId(edge.id), topologyPoints: points,
      bendPoints: bends, directionSequence: directions,
      sourcePortSide: port(edge.points[0], nodes),
      destinationPortSide: port(edge.points.at(-1), nodes) };
  }) };
}

const browser = await puppeteer.launch({
  headless: true, executablePath: process.env.PUPPETEER_EXECUTABLE_PATH,
  args: ['--no-sandbox'],
});
const cases = [];
try {
  const page = await browser.newPage();
  await page.setContent('<svg xmlns="http://www.w3.org/2000/svg"></svg>');
  for (const o of official.cases) {
    const n = nativeById.get(o.id);
    if (!n || n.source !== o.source) throw new Error(`${o.id}: source mismatch`);
    const findings = [];
    if (o.error || n.error) findings.push({ code: 'render-error', official: o.error, native: n.error });
    const nativeLabels = (n.labels ?? []).map(label => normalize(label.text)).filter(Boolean).sort();
    const officialLabels = (o.labels ?? []).map(label => normalize(label.text)).filter(Boolean).sort();
    const contentPass = JSON.stringify(nativeLabels) === JSON.stringify(officialLabels);
    if (!contentPass) findings.push({ code: 'label-content', nativeLabels, officialLabels });
    const nodeComparisons = (n.nodes ?? []).filter(node => !isLabelBackground(node)).map(node => {
      const match = o.nodes?.find(candidate => nodeId(candidate.id, o.id) === nodeId(node.id, o.id));
      return { id: node.id, native: node.bounds, official: match?.bounds ?? null,
        matches: Boolean(match) && ['x', 'y', 'width', 'height'].every(key => near(node.bounds[key], match.bounds[key])) };
    });
    const geometryPass = nodeComparisons.every(node => node.matches);
    if (!geometryPass) findings.push({ code: 'node-geometry' });
    const edgeLabelComparisons = (o.labels ?? []).filter(label =>
      label.edgeLabel && normalize(label.text)).map(label => {
      const candidates = (n.labels ?? []).filter(candidate =>
        normalize(candidate.text) === normalize(label.text));
      const center = bounds => [bounds.x + bounds.width / 2, bounds.y + bounds.height / 2];
      const expected = center(label.bounds);
      const actual = candidates.length === 1 ? center(candidates[0].bounds) : null;
      return { text: label.text, official: expected, native: actual,
        matches: actual !== null && actual.every((value, index) => near(value, expected[index])) };
    });
    const edgeLabelsPass = edgeLabelComparisons.every(label => label.matches);
    if (!edgeLabelsPass) findings.push({ code: 'edge-label-anchor' });
    const topologyResult = o.edges && n.edges ? compareTopologyManifests(topology(n), topology(o)) : null;
    if (topologyResult?.status === 'fail') findings.push({ code: 'edge-topology' });
    if (topologyResult?.status === 'review') findings.push({ code: 'edge-topology-review' });
    const pathComparisons = [];
    for (const edge of n.edges ?? []) {
      const match = o.edges.find(candidate => edgeId(candidate.id) === edgeId(edge.id));
      if (!match) {
        findings.push({ code: 'missing-official-edge', id: edge.id });
        continue;
      }
      const metrics = await page.evaluate(ds => ds.map(d => {
        const p = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        p.setAttribute('d', d);
        document.querySelector('svg').appendChild(p);
        const length = p.getTotalLength();
        const point = distance => { const q = p.getPointAtLength(distance); return [q.x, q.y]; };
        const result = { length, start: point(0), end: point(length) };
        p.remove();
        return result;
      }), [edge.d, match.d]);
      const matches = ['start', 'end'].every(key => metrics[0][key].every((v, i) => near(v, metrics[1][key][i]))) &&
        (metrics[0].length < 0.02) === (metrics[1].length < 0.02);
      pathComparisons.push({ id: edgeId(edge.id), native: metrics[0], official: metrics[1], matches });
    }
    const pathsPass = pathComparisons.every(edge => edge.matches);
    if (!pathsPass) findings.push({ code: 'rendered-path-endpoints' });
    if (n.edges?.length !== o.edges?.length) findings.push({ code: 'edge-count' });
    const score = raster.cases.find(entry => entry.id === o.id)?.ssim;
    if (!Number.isFinite(score)) throw new Error(`${o.id}: missing raster evidence`);
    if (score < 0.97) findings.push({ code: 'raster-review', ssim: score });
    const failed = findings.some(finding => ['render-error', 'label-content'].includes(finding.code));
    cases.push({ id: o.id, family: o.family, source: o.source,
      sourceSha256: sha256(o.source), status: failed ? 'fail' : findings.length ? 'review' : 'pass',
      contentPass, geometryPass, pathsPass, edgeLabelsPass, edgeLabelComparisons,
      ssim: score, nodeComparisons,
      topology: topologyResult, renderedPaths: pathComparisons, findings });
  }
} finally { await browser.close(); }

const counts = status => cases.filter(entry => entry.status === status).length;
const report = {
  schemaVersion: 2, generatedAt: new Date().toISOString(), mermaidVersion: official.mermaidVersion,
  inputs: Object.fromEntries([['official', official], ['native', native], ['raster', raster]]
    .map(([name, evidence]) => [name, {
      generatedAt: evidence.generatedAt ?? null, sha256: sha256(inputBytes[`${name}.json`]),
    }])),
  evidence: [...cases.flatMap(entry => ['native', 'official'].map(side => `${entry.id}-${side}.png`)),
    ...raster.contactSheets].map(file => ({
      file, sha256: sha256(fs.readFileSync(path.join(directory, file))),
    })),
  officialGeneratedAt: official.generatedAt, nativeGeneratedAt: native.generatedAt,
  layout: 'dagre', familyCount: new Set(cases.map(entry => entry.family)).size,
  caseCount: cases.length, screenshotCount: cases.length * 2, contactSheets: raster.contactSheets,
  counts: { pass: counts('pass'), review: counts('review'), fail: counts('fail') }, cases,
};
fs.writeFileSync(path.join(directory, 'audit.json'), JSON.stringify(report, null, 2));
console.log(JSON.stringify({ ...report, cases: undefined }, null, 2));
