import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

const source = path.resolve(process.argv[2] ?? 'build/mixed-formula-audit');
const destination = path.resolve(process.argv[3] ?? 'docs/assets/mixed-formula-report');
const inputBytes = {};
const read = file => {
  inputBytes[file] = fs.readFileSync(path.join(source, file));
  return JSON.parse(inputBytes[file]);
};
const official = read('official.json');
const native = read('native.json');
const geometry = read('geometry.json');
const raster = read('raster.json');
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
for (const [name, evidence] of [['official', official], ['native', native], ['raster', raster]]) {
  const binding = geometry.inputs?.[name];
  if (binding?.generatedAt !== (evidence.generatedAt ?? null) ||
      binding?.sha256 !== sha256(inputBytes[`${name}.json`])) {
    throw new Error(`Mixed formula geometry is stale: ${name} input differs`);
  }
}
if (typeof official.generatedAt !== 'string' || !official.generatedAt ||
    typeof native.generatedAt !== 'string' || !native.generatedAt ||
    geometry.inputs.raster.officialGeneratedAt !== official.generatedAt ||
    geometry.inputs.raster.nativeGeneratedAt !== native.generatedAt ||
    raster.officialGeneratedAt !== official.generatedAt ||
    raster.nativeGeneratedAt !== native.generatedAt) {
  throw new Error('Mixed formula evidence is failed, incomplete, or stale');
}
const officialIds = new Set(official.cases.map(entry => entry.id));
for (const [name, evidence] of [
  ['Official', official], ['Native', native], ['geometry', geometry], ['raster', raster],
]) {
  const ids = evidence.cases?.map(entry => entry.id);
  if (!ids || ids.length !== official.cases.length ||
      new Set(ids).size !== ids.length ||
      ids.some(id => typeof id !== 'string' || !id || !officialIds.has(id))) {
    throw new Error(`Mixed formula ${name} case IDs are incomplete or mismatched`);
  }
}
const counts = { pass: 0, review: 0, fail: 0 };
for (const entry of geometry.cases) {
  if (!['pass', 'review', 'fail'].includes(entry.status)) {
    throw new Error(`Mixed formula geometry status mismatch: ${entry.id}`);
  }
  counts[entry.status] += 1;
}
if (counts.fail || Object.keys(counts).some(status => geometry.counts?.[status] !== counts[status]) ||
    geometry.caseCount !== official.cases.length ||
    geometry.screenshotCount !== official.cases.length * 2 ||
    raster.caseCount !== official.cases.length ||
    raster.screenshotCount !== official.cases.length * 2) {
  throw new Error('Mixed formula evidence is failed or incomplete: counts mismatch');
}
const nativeById = new Map(native.cases.map(entry => [entry.id, entry]));
const geometryById = new Map(geometry.cases.map(entry => [entry.id, entry]));
const sources = new Set();
const records = official.cases.map(entry => {
  const actual = nativeById.get(entry.id);
  if (actual.source !== entry.source || geometryById.get(entry.id).source !== entry.source) {
    throw new Error(`Native/Official/geometry source mismatch: ${entry.id}`);
  }
  if (entry.error || actual.error || actual.providerFailures) {
    throw new Error(`Mixed formula evidence is failed: ${entry.id}`);
  }
  const sourceSha256 = sha256(entry.source);
  if (sources.has(sourceSha256)) throw new Error(`Duplicate source: ${entry.id}`);
  sources.add(sourceSha256);
  return { id: entry.id, source: entry.source, sourceSha256,
    screenshots: ['native', 'official'].map(side => {
      const file = `${entry.id}-${side}.png`;
      return { side, file, sha256: sha256(fs.readFileSync(path.join(source, file))) };
    }),
  };
});
const formulaImages = official.cases.flatMap(entry => entry.formulas.map(item => item.image));
const files = [...records.flatMap(entry => entry.screenshots.map(item => item.file)),
  ...formulaImages, ...raster.contactSheets,
  'official.json', 'native.json', 'geometry.json', 'raster.json', 'index.html'];
fs.mkdirSync(destination, { recursive: true });
for (const file of files) {
  // Publish the exact JSON bytes validated above, even if capture files change.
  if (inputBytes[file]) fs.writeFileSync(path.join(destination, file), inputBytes[file]);
  else fs.copyFileSync(path.join(source, file), path.join(destination, file));
}
const manifest = { schemaVersion: 2, generatedAt: new Date().toISOString(),
  officialGeneratedAt: official.generatedAt, nativeGeneratedAt: native.generatedAt,
  geometryGeneratedAt: geometry.generatedAt, rasterGeneratedAt: raster.generatedAt ?? null,
  inputs: geometry.inputs,
  mermaidVersion: official.mermaidVersion, mermaidSourceCommit: official.mermaidSourceCommit,
  familyCount: new Set(records.map(entry => entry.id.split('-')[0])).size,
  caseCount: records.length, screenshotCount: records.length * 2,
  formulaFixtureImageCount: formulaImages.length, contactSheets: raster.contactSheets,
  counts, boundary: geometry.boundary, records };
fs.writeFileSync(path.join(destination, 'visual-parity-manifest.json'),
  JSON.stringify(manifest, null, 2));
console.log(JSON.stringify({ destination: path.relative(process.cwd(), destination),
  caseCount: manifest.caseCount, screenshotCount: manifest.screenshotCount,
  formulaFixtureImageCount: formulaImages.length, counts: manifest.counts }, null, 2));
