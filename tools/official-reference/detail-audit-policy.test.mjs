import assert from 'node:assert/strict';
import test from 'node:test';
import { classifyDetailFindings } from './detail-audit-policy.mjs';

test('queues placement-only CoSE occlusion differences for review', () => {
  const result = classifyDetailFindings(
    [
      {
        severity: 'error',
        code: 'paint-order-occlusion',
        message: 'Placement-dependent overlap differs',
      },
      {
        severity: 'review',
        code: 'text-position',
        message: 'Text positions differ',
      },
    ],
    { layout: 'cose-bilkent' },
  );

  assert.equal(result[0].severity, 'review');
  assert.equal(result[0].policy, 'iterative-layout-placement');
  assert.equal(result[1].severity, 'review');
});

test('keeps deterministic-layout occlusion differences hard', () => {
  const finding = {
    severity: 'error',
    code: 'paint-order-occlusion',
    message: 'Paint order differs',
  };

  assert.deepEqual(
    classifyDetailFindings([finding], { layout: 'dagre' }),
    [finding],
  );
});

test('does not soften CoSE cases with independent structural failures', () => {
  const findings = [
    {
      severity: 'error',
      code: 'paint-order-occlusion',
      message: 'Placement-dependent overlap differs',
    },
    {
      severity: 'error',
      code: 'text-presence',
      message: 'Text is missing',
    },
  ];

  assert.deepEqual(
    classifyDetailFindings(findings, { layout: 'cose-bilkent' }),
    findings,
  );
});
