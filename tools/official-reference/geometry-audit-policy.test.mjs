import assert from 'node:assert/strict';
import test from 'node:test';
import { classifyGeometryRatio } from './geometry-audit-policy.mjs';

const base = {
  label: 'visual_mindmap_001/height',
  minimum: 0.8,
  maximum: 1.25,
};

test('passes ratios inside the unchanged geometry thresholds', () => {
  assert.deepEqual(
    classifyGeometryRatio({
      ...base,
      value: 1.0,
      iterativeLayoutWithMatchingStructure: false,
    }),
    { status: 'pass', message: null },
  );
});

test('fails ratio mismatches for non-iterative layouts', () => {
  assert.deepEqual(
    classifyGeometryRatio({
      ...base,
      value: 1.3,
      iterativeLayoutWithMatchingStructure: false,
    }),
    {
      status: 'failure',
      message:
        'visual_mindmap_001/height ratio 1.300 is outside [0.800, 1.250]',
    },
  );
});

test('queues ratio mismatches only for structurally matching iterative layouts', () => {
  assert.deepEqual(
    classifyGeometryRatio({
      ...base,
      value: 1.3,
      iterativeLayoutWithMatchingStructure: true,
    }),
    {
      status: 'review',
      message:
        'visual_mindmap_001/height ratio 1.300 is outside [0.800, 1.250]; ' +
        'CoSE manifest structure matches',
    },
  );
});

test('keeps non-finite ratios visible instead of formatting them as numbers', () => {
  assert.deepEqual(
    classifyGeometryRatio({
      ...base,
      value: Number.NaN,
      iterativeLayoutWithMatchingStructure: false,
    }),
    {
      status: 'failure',
      message:
        'visual_mindmap_001/height ratio NaN is outside [0.800, 1.250]',
    },
  );
});
