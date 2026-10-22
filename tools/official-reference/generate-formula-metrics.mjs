import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { captureOfficialFormulaMetrics } from './formula-metrics.mjs';

const directory = path.dirname(fileURLToPath(import.meta.url));
const output = path.resolve(
  directory,
  'fixtures/formula-metrics-mermaid-12.0.0.json',
);
const metrics = await captureOfficialFormulaMetrics();

fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${JSON.stringify(metrics, null, 2)}\n`);
console.log(`Wrote ${metrics.cases.length} formula metric cases to ${output}`);
