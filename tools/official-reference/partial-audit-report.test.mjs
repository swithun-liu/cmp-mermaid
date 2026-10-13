import assert from 'node:assert/strict';
import test from 'node:test';
import {
  mergedInputDirectoryMetadata,
  replaceCasesForKind,
  replaceMessagesForCaseIds,
} from './partial-audit-report.mjs';

test('replaceCasesForKind replaces one complete family in place', () => {
  const previous = [
    { id: 'flow_001', kind: 'flowchart', value: 'old' },
    { id: 'flow_002', kind: 'flowchart', value: 'old' },
    { id: 'pie_001', kind: 'pie', value: 'kept' },
  ];
  const current = [
    { id: 'flow_001', kind: 'flowchart', value: 'new' },
    { id: 'flow_002', kind: 'flowchart', value: 'new' },
  ];

  assert.deepEqual(
    replaceCasesForKind(previous, current, 'flowchart'),
    [...current, previous[2]],
  );
});

test('replaceCasesForKind rejects duplicate merged case IDs', () => {
  assert.throws(
    () => replaceCasesForKind(
      [{ id: 'shared', kind: 'pie' }],
      [{ id: 'shared', kind: 'flowchart' }],
      'flowchart',
    ),
    /duplicate case ID shared/,
  );
});

test('replaceMessagesForCaseIds removes stale family findings', () => {
  const replacedIds = new Set(['flow_001', 'flow_002']);

  assert.deepEqual(
    replaceMessagesForCaseIds(
      [
        'flow_001/width is stale',
        'pie_001/height is retained',
      ],
      ['flow_002/ink is current'],
      replacedIds,
    ),
    [
      'pie_001/height is retained',
      'flow_002/ink is current',
    ],
  );
});

test('mergedInputDirectoryMetadata records mixed family evidence roots', () => {
  const previous = {
    inputDirectory: 'captures/old',
    cases: [
      { id: 'flow_001', kind: 'flowchart' },
      { id: 'pie_001', kind: 'pie' },
    ],
  };
  const current = {
    inputDirectory: 'captures/new',
    cases: [{ id: 'flow_001', kind: 'flowchart' }],
  };
  const mergedCases = [
    { id: 'flow_001', kind: 'flowchart' },
    { id: 'pie_001', kind: 'pie' },
  ];

  assert.deepEqual(
    mergedInputDirectoryMetadata(
      previous,
      current,
      mergedCases,
      'flowchart',
    ),
    {
      inputDirectory: null,
      inputDirectoriesByKind: {
        flowchart: 'captures/new',
        pie: 'captures/old',
      },
    },
  );
});
