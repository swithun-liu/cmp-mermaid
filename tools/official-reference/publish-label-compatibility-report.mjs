import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { labelCompatibilityCases } from './label-compatibility-corpus.mjs';

const source = path.resolve(process.argv[2] ?? 'build/label-compatibility');
const destination = path.resolve(process.argv[3] ?? 'docs/assets/label-compatibility-report');
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
const inputBytes = {};
const read = file => {
  inputBytes[file] = fs.readFileSync(path.join(source, file));
  return JSON.parse(inputBytes[file]);
};
const official = read('official.json');
const native = read('native.json');
const audit = read('audit.json');
const raster = read('raster.json');
const baseline = read('baseline-native.json');
const comparison = read('baseline-comparison.json');
if (comparison.caseCount !== native.cases.length ||
    comparison.unchangedPathCount !== native.cases.length ||
    comparison.inputs.baselineNative !== sha256(inputBytes['baseline-native.json']) ||
    comparison.inputs.native !== sha256(inputBytes['native.json']) ||
    comparison.inputs.official !== sha256(inputBytes['official.json'])) {
  throw new Error('Edge-label baseline comparison is stale or paths changed');
}
const baselineById = new Map(baseline.cases.map(entry => [entry.id, entry]));
if (baselineById.size !== native.cases.length || native.cases.some(entry =>
    baselineById.get(entry.id)?.source !== entry.source)) {
  throw new Error('Edge-label baseline source coverage differs');
}
for (const [name, evidence] of [['official', official], ['native', native], ['raster', raster]]) {
  if (audit.inputs?.[name]?.sha256 !== sha256(inputBytes[`${name}.json`]) ||
      audit.inputs[name].generatedAt !== (evidence.generatedAt ?? null)) {
    throw new Error(`Label audit is stale: ${name}`);
  }
}
if (official.mermaidVersion !== '12.0.0' ||
    !official.generatedAt || !native.generatedAt ||
    audit.officialGeneratedAt !== official.generatedAt ||
    audit.nativeGeneratedAt !== native.generatedAt ||
    raster.officialGeneratedAt !== official.generatedAt ||
    raster.nativeGeneratedAt !== native.generatedAt) {
  throw new Error('Label capture versions or timestamps differ');
}
const expected = new Map(labelCompatibilityCases.map(entry => [entry.id, entry]));
for (const [name, evidence] of [['official', official], ['native', native], ['audit', audit], ['raster', raster]]) {
  const ids = evidence.cases?.map(entry => entry.id);
  if (!ids || ids.length !== expected.size || new Set(ids).size !== ids.length ||
      ids.some(id => !expected.has(id))) {
    throw new Error(`Label ${name} case coverage differs`);
  }
}
const counts = { pass: 0, review: 0, fail: 0 };
const nativeById = new Map(native.cases.map(entry => [entry.id, entry]));
const auditById = new Map(audit.cases.map(entry => [entry.id, entry]));
const sources = new Set();
const records = official.cases.map(entry => {
  const actual = nativeById.get(entry.id);
  const checked = auditById.get(entry.id);
  const fixture = expected.get(entry.id);
  if (entry.source !== fixture.source || entry.family !== fixture.family ||
      actual.source !== entry.source || checked.source !== entry.source ||
      checked.sourceSha256 !== sha256(entry.source)) {
    throw new Error(`Label source identity differs: ${entry.id}`);
  }
  if (entry.error || actual.error || !checked.contentPass ||
      !Object.hasOwn(counts, checked.status)) {
    throw new Error(`Label evidence failed: ${entry.id}`);
  }
  counts[checked.status]++;
  const sourceSha256 = sha256(entry.source);
  if (sources.has(sourceSha256)) throw new Error(`Duplicate label source: ${entry.id}`);
  sources.add(sourceSha256);
  return { id: entry.id, family: entry.family, source: entry.source, sourceSha256,
    status: checked.status, screenshots: ['native', 'official'].map(side => ({
      side, file: `${entry.id}-${side}.png`,
    })) };
});
if (counts.fail || Object.keys(counts).some(key => counts[key] !== audit.counts[key]) ||
    audit.caseCount !== records.length || audit.screenshotCount !== records.length * 2 ||
    raster.caseCount !== records.length || raster.screenshotCount !== records.length * 2 ||
    audit.familyCount !== new Set(records.map(entry => entry.family)).size ||
    JSON.stringify(audit.contactSheets) !== JSON.stringify(raster.contactSheets)) {
  throw new Error('Label evidence counts differ or contain failures');
}
const files = [...records.flatMap(entry => entry.screenshots.map(item => item.file)),
  ...raster.contactSheets];
if (new Set(files).size !== files.length || audit.evidence?.length !== files.length) {
  throw new Error('Label image inventory is incomplete');
}
const evidenceBytes = new Map(files.map(file => {
  if (path.basename(file) !== file) throw new Error('Label evidence path must be local');
  const bytes = fs.readFileSync(path.join(source, file));
  if (audit.evidence.find(item => item.file === file)?.sha256 !== sha256(bytes)) {
    throw new Error(`Label image is stale: ${file}`);
  }
  return [file, bytes];
}));
for (const record of records) {
  record.screenshots.forEach(item => { item.sha256 = sha256(evidenceBytes.get(item.file)); });
}
fs.mkdirSync(destination, { recursive: true });
for (const [file, bytes] of [...Object.entries(inputBytes), ...evidenceBytes]) {
  fs.writeFileSync(path.join(destination, file), bytes);
}
const manifest = {
  schemaVersion: 1, generatedAt: new Date().toISOString(),
  mermaidVersion: official.mermaidVersion, mermaidSourceCommit: official.mermaidSourceCommit,
  layout: audit.layout, officialGeneratedAt: official.generatedAt,
  nativeGeneratedAt: native.generatedAt, auditGeneratedAt: audit.generatedAt,
  inputs: { ...audit.inputs, audit: { sha256: sha256(inputBytes['audit.json']) } },
  baselineComparison: {
    baselineVersion: comparison.baselineVersion, currentVersion: comparison.currentVersion,
    sha256: sha256(inputBytes['baseline-comparison.json']),
    baselineNativeSha256: sha256(inputBytes['baseline-native.json']),
    changedCaseCount: comparison.changedCaseCount, unchangedPathCount: comparison.unchangedPathCount,
  },
  familyCount: audit.familyCount, caseCount: records.length,
  screenshotCount: records.length * 2, contactSheets: raster.contactSheets,
  counts, records,
};
fs.writeFileSync(path.join(destination, 'visual-parity-manifest.json'), JSON.stringify(manifest, null, 2));
const evidence = `# Label compatibility evidence

Mermaid.js **12.0.0**, explicit **Dagre**, bundled Arimo/Arial, default
\`redux-color\` with source-defined theme overrides. Native uses Compose JVM.

${manifest.familyCount} families, ${manifest.caseCount} unique same-source inputs,
${manifest.screenshotCount} screenshots, ${manifest.contactSheets.length} contact sheets:
**${counts.pass} Pass / ${counts.review} Review / ${counts.fail} Fail**.
All label content checks pass. Review findings remain visible in the audit;
they are not promoted to Pass. This evidence does not establish mobile parity
or full-family Stable status.

- [Source and screenshot manifest](visual-parity-manifest.json)
- [Detail, node geometry, rendered endpoints and per-edge topology](audit.json)
- [Raster scores](raster.json)
- [Native capture](native.json)
- [Official capture](official.json)
- [0.1.18 to 0.1.19 label comparison](baseline-comparison.json)
- [0.1.18 Native capture](baseline-native.json)

The baseline comparison uses the published 0.1.18 JVM artifacts with identical
sources and font resources. ${comparison.changedCaseCount} cases change label
anchors; all ${comparison.unchangedPathCount} edge-path sets remain identical.
This numerical comparison adds no screenshots to the current capture count.

## Contact sheets

${raster.contactSheets.map(file => `![Native and Official comparison](${file})`).join('\n\n')}

## Individual captures

| Case | Family | Result | Native | Official |
| --- | --- | --- | --- | --- |
${records.map(record => `| ${record.id} | ${record.family} | ${record.status} | [PNG](${record.id}-native.png) | [PNG](${record.id}-official.png) |`).join('\n')}
`;
fs.writeFileSync(path.join(destination, 'evidence.md'), evidence);
console.log(JSON.stringify({ destination: path.relative(process.cwd(), destination),
  familyCount: manifest.familyCount, caseCount: manifest.caseCount,
  screenshotCount: manifest.screenshotCount, contactSheetCount: manifest.contactSheets.length,
  counts }, null, 2));
