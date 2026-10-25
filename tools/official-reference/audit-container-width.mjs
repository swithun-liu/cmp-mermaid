import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { compareTopologyManifests } from './topology-audit-core.mjs';

const directory = path.resolve(process.argv[2] ?? 'build/container-width');
const bytes = Object.fromEntries(['official', 'native', 'raster'].map(side =>
  [side, fs.readFileSync(path.join(directory, `${side}.json`))]));
const official = JSON.parse(bytes.official);
const native = JSON.parse(bytes.native);
const raster = JSON.parse(bytes.raster);
if (raster.officialGeneratedAt !== official.generatedAt ||
    raster.nativeGeneratedAt !== native.generatedAt) throw new Error('Raster capture is stale');
const hash = value => crypto.createHash('sha256').update(value).digest('hex');
const normalize = value => value.replace(/\s+/g, ' ').trim();
const near = (a, b, tolerance = 0.02) => Math.abs(a - b) <= tolerance;
const rgb = value => {
  if (value === 'none') return 0;
  const [r, g, b] = value.match(/\d+/g).map(Number);
  return 0xff000000 + r * 65536 + g * 256 + b;
};
const nativeById = new Map(native.cases.map(entry => [entry.id, entry]));
if (nativeById.size !== native.cases.length ||
    official.cases.length !== native.cases.length) throw new Error('Capture coverage differs');

function topology(edges, nodes) {
  const port = point => {
    // Select the nearest node and record the endpoint's side relative to its
    // center. Curved shape intersections need not lie on a rectangle boundary.
    const candidates = nodes.map(node => {
      const x = (point.x - node.x - node.width / 2) / (node.width / 2);
      const y = (point.y - node.y - node.height / 2) / (node.height / 2);
      return { x, y, distance: x * x + y * y };
    }).sort((a, b) => a.distance - b.distance);
    const q = candidates[0];
    return !q ? null : Math.abs(q.x) > Math.abs(q.y)
      ? (q.x > 0 ? 'R' : 'L') : (q.y > 0 ? 'D' : 'U');
  };
  return { elements: edges.map(edge => {
    const points = edge.points;
    const directionSequence = points.slice(1).map((p, i) => {
      const a = points[i];
      if (near(a.x, p.x)) return p.y > a.y ? 'D' : 'U';
      if (near(a.y, p.y)) return p.x > a.x ? 'R' : 'L';
      return 'Diagonal';
    });
    return { type: 'path', edgeId: edge.id, topologyPoints: points,
      bendPoints: points.filter((_, i) => i > 0 && i < points.length - 1 &&
        directionSequence[i - 1] !== directionSequence[i]),
      directionSequence, sourcePortSide: port(points[0]),
      destinationPortSide: port(points.at(-1)) };
  }) };
}
const cases = official.cases.map(o => {
  const n = nativeById.get(o.id);
  if (!n || n.source !== o.source || n.error) throw new Error(`${o.id}: source/render mismatch`);
  const findings = [];
  const officialTexts = (o.kind === 'mindmap' ? o.nodes : o.labels).map(l => normalize(l.text)).sort();
  const nativeTexts = n.labels.map(l => normalize(l.text)).sort();
  const contentPass = JSON.stringify(officialTexts) === JSON.stringify(nativeTexts);
  if (!contentPass) findings.push('label-content');
  const sizeMatches = near(o.width, n.width) && near(o.height, n.height);
  const geometry = o.kind === 'gantt'
    ? o.tasks.map(task => {
        const id = task.id.replace(`width-${o.id}-`, 'gantt-task-');
        const shape = n.shapes.find(s => s.id === id);
        return { id, official: task.bounds, native: shape,
          matches: Boolean(shape) && ['x', 'y', 'width', 'height'].every(k => near(shape[k], task.bounds[k])),
          paintMatches: Boolean(shape) && shape.fill === rgb(task.fill) &&
            shape.stroke === rgb(task.stroke) && shape.strokeWidth === task.strokeWidth };
      })
    : o.nodes.map(node => {
        const shape = n.shapes.find(s => s.id === node.id);
        return { id: node.id, official: node.bounds, native: shape,
          matches: Boolean(shape) && ['x', 'y', 'width', 'height'].every(k => near(shape[k], node.bounds[k])),
          paintMatches: Boolean(shape) && shape.fill === rgb(node.fill) &&
            shape.stroke === rgb(node.stroke) &&
            (node.stroke === 'none' || shape.strokeWidth === node.strokeWidth) };
      });
  if (!sizeMatches || geometry.some(g => !g.matches)) findings.push('geometry-review');
  if (geometry.some(g => !g.paintMatches)) findings.push('paint-mismatch');
  const taskLabels = o.kind !== 'gantt' ? [] : o.labels.filter(l => l.className?.includes('taskText')).map(label => {
    const match = n.labels.find(l => normalize(l.text) === normalize(label.text));
    return { text: label.text, official: label.bounds, native: match,
      matches: Boolean(match) && near(match.x, label.bounds.x, 1) };
  });
  if (taskLabels.some(l => !l.matches)) findings.push('task-label-position');
  const officialEdges = o.edges.map(edge => ({
    ...edge, id: edge.id.slice(edge.id.indexOf('edge_')),
  }));
  const nativeEdges = n.edges.filter(e => e.id.startsWith('edge_')).map(edge => ({
    ...edge, points: edge.points.map(([x, y]) => ({ x, y })),
  }));
  const topologyResult = o.kind === 'mindmap' ? compareTopologyManifests(
    topology(nativeEdges, n.shapes),
    topology(officialEdges, o.nodes.map(node => ({ ...node.bounds }))),
  ) : null;
  if (topologyResult?.status !== 'pass' && topologyResult) findings.push('topology-review');
  const paths = nativeEdges.map(edge => {
    const match = officialEdges.find(e => e.id === edge.id);
    return { id: edge.id, native: edge.points, official: match?.points,
      endpoints: match && { start: match.start, end: match.end },
      pointCountMatches: edge.points.length === match?.points?.length,
      pointsMatch: Boolean(match) && edge.points.length === match.points.length &&
        edge.points.every((p, i) => near(p.x, match.points[i].x) && near(p.y, match.points[i].y)),
      renderedEndpointsMatch: Boolean(match) &&
        near(edge.points[0].x, match.start.x) && near(edge.points[0].y, match.start.y) &&
        near(edge.points.at(-1).x, match.end.x) && near(edge.points.at(-1).y, match.end.y),
      paintMatches: Boolean(match) && edge.stroke === rgb(match.stroke) && edge.strokeWidth === match.strokeWidth };
  });
  if (paths.some(p => !p.pointCountMatches || !p.pointsMatch)) findings.push('edge-points-review');
  if (paths.some(p => !p.renderedEndpointsMatch)) findings.push('rendered-endpoints-review');
  if (paths.some(p => !p.paintMatches)) findings.push('edge-paint-mismatch');
  const score = raster.cases.find(entry => entry.id === o.id)?.ssim;
  if (!Number.isFinite(score)) throw new Error(`${o.id}: raster evidence missing`);
  // Font baseline, title metrics and raster differences remain under review,
  // even when the repaired width/label branches match exactly.
  findings.push('text-raster-review');
  const fail = !contentPass || findings.some(f => f.endsWith('mismatch') || f === 'task-label-position');
  return { id: o.id, kind: o.kind, sourceSha256: hash(o.source),
    status: fail ? 'fail' : 'review', contentPass, sizeMatches, geometry,
    taskLabels, topology: topologyResult, paths, ssim: score, findings };
});
const report = {
  schemaVersion: 1, generatedAt: new Date().toISOString(),
  inputs: Object.fromEntries(Object.entries(bytes).map(([side, value]) => [side, { sha256: hash(value) }])),
  officialGeneratedAt: official.generatedAt, nativeGeneratedAt: native.generatedAt,
  familyCount: new Set(official.cases.map(c => c.kind)).size,
  caseCount: cases.length, uniqueSourceCount: new Set(official.cases.map(c => hash(c.source))).size,
  screenshotCount: cases.length * 2,
  contactSheets: raster.contactSheets,
  counts: Object.fromEntries(['pass', 'review', 'fail'].map(status => [status, cases.filter(c => c.status === status).length])),
  cases,
};
fs.writeFileSync(path.join(directory, 'audit.json'), JSON.stringify(report, null, 2));
console.log(JSON.stringify({ caseCount: report.caseCount, counts: report.counts }, null, 2));
if (report.counts.fail) process.exitCode = 1;
