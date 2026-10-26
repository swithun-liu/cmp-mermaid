import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

const current = path.resolve(process.argv[2] ?? 'build/edge-label-019');
const baseline = path.resolve(process.argv[3] ?? 'build/edge-label-018-baseline');
const artifactDirectory = process.argv[4];
if (!artifactDirectory) throw new Error('Supply the published 0.1.18 JVM artifact directory');
const baselineArtifacts = ['mermaid-core-jvm-0.1.18.jar', 'mermaid-compose-jvm-0.1.18.jar']
  .map(file => ({ file, sha256: crypto.createHash('sha256')
    .update(fs.readFileSync(path.join(artifactDirectory, file))).digest('hex') }));
const read = (directory, file) => {
  const bytes = fs.readFileSync(path.join(directory, file));
  return { data: JSON.parse(bytes), sha256: crypto.createHash('sha256').update(bytes).digest('hex') };
};
const previous = read(baseline, 'native.json');
const native = read(current, 'native.json');
const official = read(current, 'official.json');
const oldById = new Map(previous.data.cases.map(entry => [entry.id, entry]));
const officialById = new Map(official.data.cases.map(entry => [entry.id, entry]));
if (oldById.size !== native.data.cases.length) throw new Error('Baseline case coverage differs');
const center = label => [label.bounds.x + label.bounds.width / 2, label.bounds.y + label.bounds.height / 2];
const distance = (a, b) => Math.hypot(a[0] - b[0], a[1] - b[1]);
const cases = native.data.cases.map(entry => {
  const old = oldById.get(entry.id);
  const reference = officialById.get(entry.id);
  if (!old || old.source !== entry.source || reference?.source !== entry.source ||
      old.error || entry.error) throw new Error(`Source/outcome differs: ${entry.id}`);
  if (old.labels.length !== entry.labels.length) throw new Error(`Labels differ: ${entry.id}`);
  const labels = entry.labels.flatMap((label, index) => {
    const before = old.labels[index];
    if (before.text !== label.text) throw new Error(`Label order differs: ${entry.id}`);
    if (JSON.stringify(before) === JSON.stringify(label)) return [];
    const references = reference.labels.filter(candidate => candidate.text.trim() === label.text.trim());
    const expected = references.length === 1 ? center(references[0]) : null;
    return [{ text: label.text, before: center(before), after: center(label), official: expected,
      beforeDistance: expected && distance(center(before), expected),
      afterDistance: expected && distance(center(label), expected) }];
  });
  return { id: entry.id, sourceSha256: crypto.createHash('sha256').update(entry.source).digest('hex'),
    pathsUnchanged: JSON.stringify(old.edges) === JSON.stringify(entry.edges),
    labelsChanged: labels.length, labels };
});
if (cases.some(entry => !entry.pathsUnchanged)) throw new Error('Edge paths changed');
fs.copyFileSync(path.join(baseline, 'native.json'), path.join(current, 'baseline-native.json'));
const comparison = {
  schemaVersion: 1, generatedAt: new Date().toISOString(), baselineVersion: '0.1.18',
  currentVersion: '0.1.19', mermaidVersion: '12.0.0',
  baselineArtifacts,
  inputs: { baselineNative: previous.sha256, native: native.sha256, official: official.sha256 },
  caseCount: cases.length, changedCaseCount: cases.filter(entry => entry.labelsChanged).length,
  unchangedPathCount: cases.filter(entry => entry.pathsUnchanged).length, cases,
};
fs.writeFileSync(path.join(current, 'baseline-comparison.json'), JSON.stringify(comparison, null, 2));
console.log(JSON.stringify({ ...comparison, cases: cases.filter(entry => entry.labelsChanged) }, null, 2));
