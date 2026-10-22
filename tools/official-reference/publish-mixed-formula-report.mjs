import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

const source = path.resolve(process.argv[2] ?? 'build/mixed-formula-audit');
const destination = path.resolve(process.argv[3] ?? 'docs/assets/mixed-formula-report');
const read = file => JSON.parse(fs.readFileSync(path.join(source, file)));
const official = read('official.json');
const native = read('native.json');
const geometry = read('geometry.json');
const raster = read('raster.json');
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
if (geometry.counts.fail || official.cases.length !== native.cases.length ||
    geometry.caseCount !== official.cases.length ||
    raster.officialGeneratedAt !== official.generatedAt ||
    raster.nativeGeneratedAt !== native.generatedAt) {
  throw new Error('Mixed formula evidence is failed, incomplete, or stale');
}
const nativeById = new Map(native.cases.map(entry => [entry.id, entry]));
const sources = new Set();
const records = official.cases.map(entry => {
  if (nativeById.get(entry.id)?.source !== entry.source) {
    throw new Error(`Native/Official source mismatch: ${entry.id}`);
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
for (const file of files) fs.copyFileSync(path.join(source, file), path.join(destination, file));
const manifest = { schemaVersion: 1, generatedAt: new Date().toISOString(),
  officialGeneratedAt: official.generatedAt, nativeGeneratedAt: native.generatedAt,
  mermaidVersion: official.mermaidVersion, mermaidSourceCommit: official.mermaidSourceCommit,
  familyCount: new Set(records.map(entry => entry.id.split('-')[0])).size,
  caseCount: records.length, screenshotCount: records.length * 2,
  formulaFixtureImageCount: formulaImages.length, contactSheets: raster.contactSheets,
  counts: geometry.counts, boundary: geometry.boundary, records };
fs.writeFileSync(path.join(destination, 'visual-parity-manifest.json'),
  JSON.stringify(manifest, null, 2));
console.log(JSON.stringify({ destination: path.relative(process.cwd(), destination),
  caseCount: manifest.caseCount, screenshotCount: manifest.screenshotCount,
  formulaFixtureImageCount: formulaImages.length, counts: manifest.counts }, null, 2));
