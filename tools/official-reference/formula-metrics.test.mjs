import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import test from 'node:test';
import { fileURLToPath } from 'node:url';
import {
  captureOfficialFormulaMetrics,
  formulaMetricCases,
  formulaMetricsMathFont,
  formulaMetricsTheme,
} from './formula-metrics.mjs';
import { mermaidBaseline } from './diagram-inventory.mjs';

const directory = path.dirname(fileURLToPath(import.meta.url));
const fixture = JSON.parse(
  fs.readFileSync(
    path.resolve(
      directory,
      'fixtures/formula-metrics-mermaid-12.0.0.json',
    ),
    'utf8',
  ),
);

test('formula fixture is source-bound to Mermaid 12.0.0', () => {
  assert.equal(fixture.schemaVersion, 1);
  assert.equal(fixture.mermaidVersion, mermaidBaseline.version);
  assert.equal(fixture.mermaidSourceCommit, mermaidBaseline.sourceCommit);
  assert.equal(fixture.theme, formulaMetricsTheme);
  assert.equal(fixture.mathFont, formulaMetricsMathFont);
  assert.deepEqual(
    fixture.cases.map(({ id }) => id),
    formulaMetricCases.map(({ id }) => id),
  );
  assert.deepEqual(
    fixture.cases.map(({ source }) => source),
    formulaMetricCases.map(({ source }) => source),
  );
});

test('live Mermaid formula boxes match the locked fixture', async () => {
  const actual = await captureOfficialFormulaMetrics();
  assert.equal(actual.cases.length, fixture.cases.length);
  assert.equal(actual.mathFont, fixture.mathFont);

  for (const [index, expectedCase] of fixture.cases.entries()) {
    const actualCase = actual.cases[index];
    assert.equal(actualCase.id, expectedCase.id);
    assert.equal(actualCase.source, expectedCase.source);
    assertMetricClose(actualCase.content, expectedCase.content);
    assertMetricClose(actualCase.layout, expectedCase.layout);
  }
});

function assertMetricClose(actual, expected) {
  assert.equal(actual.display, expected.display);
  for (const key of [
    'width',
    'height',
    'fontSize',
    'lineHeight',
    'maxWidth',
    'paddingLeft',
    'paddingRight',
  ]) {
    if (expected[key] === null) {
      assert.equal(actual[key], null, key);
    } else {
      assert.ok(
        Math.abs(actual[key] - expected[key]) <= 0.05,
        `${key}: expected ${expected[key]}, found ${actual[key]}`,
      );
    }
  }
}
