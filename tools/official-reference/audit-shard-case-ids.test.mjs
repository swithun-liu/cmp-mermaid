import assert from 'node:assert/strict';
import test from 'node:test';
import { selectAuditShard } from './audit-shard-case-ids.mjs';
import { invalidSourceCaseMatchesScope } from './audit-source-selection.mjs';
import { cases as invalidSourceCases } from './invalid-source-corpus.mjs';
import { cases as productionCases } from './production-corpus.mjs';

test('six production shards are complete, disjoint, and balanced', () => {
  assert.equal(productionCases.length, 441);
  assert.equal(new Set(productionCases.map(({ kind }) => kind)).size, 33);
  assertCompletePartition({
    source: 'production',
    expected: productionCases,
    shardCount: 6,
  });
});

test('six invalid-source smoke shards match the capture scope exactly', () => {
  const expected = invalidSourceCases.filter((entry) =>
    invalidSourceCaseMatchesScope(entry, 'smoke')
  );
  assert.equal(expected.length, 528);
  assert.equal(new Set(expected.map(({ kind }) => kind)).size, 33);

  assertCompletePartition({
    source: 'invalid-source',
    expected,
    shardCount: 6,
    invalidSourceScope: 'smoke',
  });
});

test('rejects invalid shard coordinates and source names', () => {
  assert.throws(
    () => selectAuditShard({
      source: 'production',
      shardIndex: 6,
      shardCount: 6,
    }),
    /Shard index/,
  );
  assert.throws(
    () => selectAuditShard({
      source: 'unknown',
      shardIndex: 0,
      shardCount: 1,
    }),
    /Unsupported audit source/,
  );
});

function assertCompletePartition({
  source,
  expected,
  shardCount,
  invalidSourceScope = 'full',
}) {
  const shards = Array.from({ length: shardCount }, (_, shardIndex) =>
    selectAuditShard({
      source,
      shardIndex,
      shardCount,
      invalidSourceScope,
    })
  );
  const actualIds = shards.flatMap((shard) => shard.map(({ id }) => id));
  const expectedIds = expected.map(({ id }) => id);
  const sizes = shards.map(({ length }) => length);

  assert.deepEqual(new Set(actualIds), new Set(expectedIds));
  assert.equal(actualIds.length, expectedIds.length);
  assert.equal(new Set(actualIds).size, actualIds.length);
  assert.ok(Math.max(...sizes) - Math.min(...sizes) <= 1);
}
