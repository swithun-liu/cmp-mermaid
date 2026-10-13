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

test('queues dense Pie font-bound occlusion differences for review', () => {
  const finding = {
    severity: 'error',
    code: 'paint-order-occlusion',
    message: 'Font bounds cross the occlusion threshold',
  };
  const result = classifyDetailFindings(
    [finding],
    { kind: 'pie', layout: 'pie' },
    equivalentDensePieEvidence(),
  );

  assert.equal(result[0].severity, 'review');
  assert.equal(result[0].policy, 'dense-pie-font-metrics');
});

test('keeps dense Pie occlusion hard without strict matching evidence', () => {
  const finding = {
    severity: 'error',
    code: 'paint-order-occlusion',
    message: 'Paint order differs',
  };
  const evidence = equivalentDensePieEvidence();
  evidence.raster.maskIou = 0.97;

  assert.deepEqual(
    classifyDetailFindings(
      [finding],
      { kind: 'pie', layout: 'pie' },
      evidence,
    ),
    [finding],
  );
});

test('queues Treemap ellipsis differences caused by renderer font metrics', () => {
  const finding = {
    severity: 'error',
    code: 'text-presence',
    message: 'Truncated text differs',
  };
  const result = classifyDetailFindings(
    [finding],
    { kind: 'treemap', layout: 'treemap' },
    equivalentTreemapEvidence(),
  );

  assert.equal(result[0].severity, 'review');
  assert.equal(result[0].policy, 'treemap-dom-font-metrics');
});

test('keeps Treemap semantic text differences hard', () => {
  const finding = {
    severity: 'error',
    code: 'text-presence',
    message: 'Text differs',
  };
  const evidence = equivalentTreemapEvidence();
  evidence.manifest.textComparison.absentInNative[0].text = 'Other';

  assert.deepEqual(
    classifyDetailFindings(
      [finding],
      { kind: 'treemap', layout: 'treemap' },
      evidence,
    ),
    [finding],
  );
});

test('queues Cynefin font-bound occlusion differences with strict evidence', () => {
  const finding = {
    severity: 'error',
    code: 'paint-order-occlusion',
    message: 'Font bounds cross an opaque path bound',
  };
  const result = classifyDetailFindings(
    [finding],
    { kind: 'cynefin', layout: 'cynefin' },
    equivalentCynefinEvidence(),
  );

  assert.equal(result[0].severity, 'review');
  assert.equal(result[0].policy, 'cynefin-font-bounds');
});

test('keeps Cynefin occlusion hard when raster similarity is insufficient', () => {
  const finding = {
    severity: 'error',
    code: 'paint-order-occlusion',
    message: 'Paint order differs',
  };
  const evidence = equivalentCynefinEvidence();
  evidence.raster.maskIou = 0.96;

  assert.deepEqual(
    classifyDetailFindings(
      [finding],
      { kind: 'cynefin', layout: 'cynefin' },
      evidence,
    ),
    [finding],
  );
});

function equivalentDensePieEvidence() {
  return {
    manifest: {
      native: { textCount: 17, clippedTextCount: 0 },
      official: { textCount: 17, clippedTextCount: 0 },
      textComparison: {
        absentInNative: [],
        absentInOfficial: [],
        multiplicityDifferences: [],
        matchedCount: 17,
        maximumCenterDistance: 0.012,
      },
      elementComparison: {
        ratios: {
          text: 1,
          asset: 1,
          geometry: 1,
          geometryObject: 1,
          geometryExpanded: 1,
        },
      },
      styleComparison: {
        nativePaletteSize: 10,
        officialPaletteSize: 10,
        nativePaletteCoverage: 1,
        officialPaletteCoverage: 1,
        nativeStrokePatterns: { Solid: 17 },
        officialStrokePatterns: { Solid: 17 },
        nativeMarkers: { start: 0, end: 0 },
        officialMarkers: { start: 0, end: 0 },
      },
      markerVisibilityComparison: { differences: [] },
      overlapComparison: { mismatches: [], ratioDifferences: [] },
    },
    raster: {
      status: 'pass',
      maskIou: 0.991,
      edgeF1: 0.998,
      colorError: 0.021,
    },
  };
}

function equivalentTreemapEvidence() {
  return {
    manifest: {
      native: { textCount: 21, clippedTextCount: 0 },
      official: { textCount: 21, clippedTextCount: 0 },
      textComparison: {
        absentInNative: [{ text: 'S...', officialCount: 1 }],
        absentInOfficial: [{ text: 'Str...', nativeCount: 1 }],
        multiplicityDifferences: [],
        matchedCount: 20,
        maximumCenterDistance: 0.024,
      },
      elementComparison: {
        ratios: {
          text: 1,
          asset: 1,
          geometry: 0.8,
          geometryObject: 0.8,
          geometryExpanded: 0.8,
        },
      },
      styleComparison: {
        nativePaletteSize: 11,
        officialPaletteSize: 11,
        nativePaletteCoverage: 0.64,
        officialPaletteCoverage: 0.64,
        nativeStrokePatterns: { Solid: 10 },
        officialStrokePatterns: { Solid: 10 },
        nativeMarkers: { start: 0, end: 0 },
        officialMarkers: { start: 0, end: 0 },
      },
      markerVisibilityComparison: { differences: [] },
      overlapComparison: { mismatches: [], ratioDifferences: [] },
    },
    raster: {
      status: 'pass',
      maskIou: 0.978,
      edgeF1: 0.965,
      colorError: 0.011,
    },
  };
}

function equivalentCynefinEvidence() {
  return {
    manifest: {
      native: { textCount: 37, clippedTextCount: 0 },
      official: { textCount: 37, clippedTextCount: 0 },
      textComparison: {
        absentInNative: [],
        absentInOfficial: [],
        multiplicityDifferences: [],
        matchedCount: 37,
        maximumCenterDistance: 0.0028,
      },
      elementComparison: {
        ratios: {
          text: 1,
          asset: 1,
          geometry: 1,
          geometryObject: 1,
          geometryExpanded: 1,
        },
      },
      styleComparison: {
        nativePaletteSize: 8,
        officialPaletteSize: 8,
        nativePaletteCoverage: 1,
        officialPaletteCoverage: 1,
        nativeStrokePatterns: { Dashed: 4, Solid: 22 },
        officialStrokePatterns: { Dashed: 4, Solid: 22 },
        nativeMarkers: { start: 0, end: 1 },
        officialMarkers: { start: 0, end: 1 },
      },
      markerVisibilityComparison: { differences: [] },
      overlapComparison: { mismatches: [], ratioDifferences: [] },
    },
    raster: {
      status: 'pass',
      maskIou: 0.973,
      edgeF1: 0.981,
      colorError: 0.018,
    },
  };
}
