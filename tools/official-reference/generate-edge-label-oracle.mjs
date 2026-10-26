// Test-only execution of unchanged Mermaid.js 12.0.0 utility bodies.
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import crypto from 'node:crypto';
import { transform } from 'esbuild';
import { line, curveBasis } from 'd3-shape';

const root = path.resolve('build/upstream/mermaid-src/packages/mermaid');
if (JSON.parse(fs.readFileSync(path.join(root, 'package.json'))).version !== '12.0.0') {
  throw new Error('Mermaid source baseline mismatch');
}
const source = fs.readFileSync(path.join(root, 'src/utils.ts'), 'utf8');
const bodies = [
  source.slice(source.indexOf('function distance('), source.indexOf('const calcCardinalityPosition')),
  source.slice(source.indexOf('function calcTerminalLabelPosition('),
    source.indexOf('\n/**', source.indexOf('function calcTerminalLabelPosition('))),
  source.slice(source.indexOf('export function isLabelCoordinateInPath(')),
];
// The final function ends before the default export, if present.
bodies[2] = bodies[2].slice(0, bodies[2].indexOf('\n}') + 2);
if (bodies.some(body => !body)) throw new Error('Upstream function extraction failed');
const { code } = await transform(bodies.join('\n').replaceAll('export ', ''), { loader: 'ts' });
const upstream = vm.runInNewContext(
  `${code}\n({calculatePoint, calcLabelPosition, isLabelCoordinateInPath, calcTerminalLabelPosition})`,
  { structuredClone, log: { info() {} } },
);
const point = (x, y) => ({ x, y });
const samples = [
  ['straight', [point(0, 0), point(0, 10), point(0, 20)], [0, 5, 10]],
  ['slope', [point(0, 0), point(10, 10), point(20, 20)], [0, 5, 10]],
  ['negative-slope', [point(20, 20), point(10, 10), point(15, 15), point(0, 0)], [0, 5, 10]],
  ['bent', [point(0, 0), point(10, 10), point(20, 0)], [0, 15, 20]],
  ['duplicate', [point(3, 7), point(3, 7), point(103, 7)], [0, 50]],
  ['signed', [point(-5, -10), point(5, 10)], [5]],
  ['too-far', [point(0, 0), point(10, 10), point(20, 20)], [30]],
  ['empty', [], [0]],
];
const calculatePoint = samples.flatMap(([id, points, distances]) => distances.map(distance => {
  try { return { id, points, distance, expected: upstream.calculatePoint(points, distance) }; }
  catch (error) { return { id, points, distance, error: error.message }; }
}));
const pathChecks = [
  [point(-1.5, 42), 'M-1.5,18L13,19'],
  [point(-1.5, 42), 'M-1,18L13,19'],
  [point(12, 999), 'M312,8L100,20'],
  [point(97, 57), 'M1,2L3,4'],
  [point(97, 57), 'M97.4,2L3,4'],
].map(([point, d]) => ({ point, d, expected: upstream.isLabelCoordinateInPath(point, d) }));
const points = [point(23, 19), point(97, 57), point(163, 149)];
const d = line().x(p => p.x).y(p => p.y).curve(curveBasis)(points);
const basis = { points, d, coordinatePresent: upstream.isLabelCoordinateInPath(points[1], d),
  expected: upstream.calcLabelPosition(points) };
const singleton = { points: [point(17, 29)], expected: upstream.calcLabelPosition([point(17, 29)]) };
const terminals = [
  ['horizontal', [point(0, 0), point(100, 0)]],
  ['bent', [point(-12, 7), point(16, 25), point(90, 60)]],
  ['duplicate', [point(3, 7), point(3, 7), point(103, 7)]],
  ['short', [point(0, 0), point(10, 0)]],
  ['empty', []],
  ['singleton', [point(17, 29)]],
].flatMap(([id, points]) =>
  [0, 10].flatMap(markerSize =>
    ['start_left', 'start_right', 'end_left', 'end_right'].map(position => {
      const input = structuredClone(points);
      try {
        const expected = upstream.calcTerminalLabelPosition(markerSize, position, input);
        if (JSON.stringify(input) !== JSON.stringify(points)) throw new Error('Input mutated');
        return { id, points, markerSize, position, expected };
      } catch (error) {
        return { id, points, markerSize, position, error: error.message };
      }
    })));
const output = process.argv[2] ?? 'build/edge-label-oracle.json';
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, JSON.stringify({
  mermaidVersion: '12.0.0', generatedAt: new Date().toISOString(),
  sourceSha256: crypto.createHash('sha256').update(bodies.join('\n')).digest('hex'),
  calculatePoint, pathChecks, basis, singleton, terminals,
}, null, 2));
console.log(JSON.stringify({ output, calculatePointCases: calculatePoint.length, pathChecks, basis }, null, 2));
