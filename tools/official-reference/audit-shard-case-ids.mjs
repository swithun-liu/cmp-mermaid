import { resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { invalidSourceCaseMatchesScope } from './audit-source-selection.mjs';
import { cases as invalidSourceCases } from './invalid-source-corpus.mjs';
import { cases as productionCases } from './production-corpus.mjs';

export function selectAuditShard({
  source,
  shardIndex,
  shardCount,
  invalidSourceScope = 'full',
}) {
  if (!Number.isInteger(shardCount) || shardCount < 1) {
    throw new Error('Shard count must be a positive integer');
  }
  if (
    !Number.isInteger(shardIndex) ||
    shardIndex < 0 ||
    shardIndex >= shardCount
  ) {
    throw new Error('Shard index must be within the shard count');
  }

  const cases = selectedCases(source, invalidSourceScope);
  requireUniqueIds(cases);
  return cases.filter((_, index) => index % shardCount === shardIndex);
}

function selectedCases(source, invalidSourceScope) {
  switch (source) {
    case 'production':
      return productionCases;
    case 'invalid-source':
      return invalidSourceCases.filter((entry) =>
        invalidSourceCaseMatchesScope(entry, invalidSourceScope)
      );
    default:
      throw new Error(`Unsupported audit source: ${source}`);
  }
}

function requireUniqueIds(cases) {
  const ids = new Set();
  for (const entry of cases) {
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate audit case ID: ${entry.id}`);
    }
    ids.add(entry.id);
  }
}

function main() {
  const [
    source,
    shardIndexValue,
    shardCountValue,
    invalidSourceScope = 'full',
  ] = process.argv.slice(2);
  const selected = selectAuditShard({
    source,
    shardIndex: Number(shardIndexValue),
    shardCount: Number(shardCountValue),
    invalidSourceScope,
  });
  process.stdout.write(`${selected.map(({ id }) => id).join(',')}\n`);
}

const currentFile = fileURLToPath(import.meta.url);
if (process.argv[1] && resolve(process.argv[1]) === currentFile) {
  main();
}
