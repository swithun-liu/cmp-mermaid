import assert from 'node:assert/strict';
import test from 'node:test';
import {
  compareDetailManifests,
  compareRasterFeatures,
  manifestViewportBackground,
} from './detail-audit-core.mjs';

test('reads a valid manifest viewport background', () => {
  assert.deepEqual(
    manifestViewportBackground({
      viewport: {
        background: { r: 255, g: 246, b: 128, a: 255 },
      },
    }),
    [255, 246, 128, 255],
  );
  assert.equal(
    manifestViewportBackground({
      viewport: {
        background: { r: 255, g: 246, b: 128, a: 300 },
      },
    }),
    null,
  );
});

test('accepts expected text from accessibility metadata', () => {
  const native = {
    ...manifest([]),
    accessibilityTitle: 'Regional service access',
  };
  const official = {
    ...manifest([]),
    title: 'Regional service access',
    accessibilityTitle: 'Regional service access',
  };

  const result = compareDetailManifests(native, official, [
    'Regional service access',
  ]);

  assert.equal(result.status, 'pass');
});

test('detects a later opaque label background covering earlier text', () => {
  const official = manifest([
    text('cherry-pick:prepare-follow-up-correction', 0),
    shape('later-label-background', 1),
    text('close-release-window', 2),
  ]);
  const oldNative = manifest([
    shape('later-label-background', 0),
    text('cherry-pick:prepare-follow-up-correction', 1),
    text('close-release-window', 2),
  ]);

  const result = compareDetailManifests(oldNative, official, [
    'close-release-window',
  ]);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'paint-order-occlusion'
    ),
  );
});

test('accepts matching label paint order', () => {
  const official = manifest([
    text('cherry-pick:prepare-follow-up-correction', 0),
    shape('later-label-background', 1),
    text('close-release-window', 2),
  ]);
  const currentNative = manifest([
    text('cherry-pick:prepare-follow-up-correction', 0),
    shape('later-label-background', 1),
    text('close-release-window', 2),
  ]);

  const result = compareDetailManifests(currentNative, official, [
    'close-release-window',
  ]);

  assert.equal(result.status, 'pass');
});

test('recognizes Mermaid node-bkg paths as opaque node backgrounds', () => {
  const native = manifest([
    text('covered-label', 0),
    shape('later-label-background', 1),
  ]);
  const official = manifest([
    text('covered-label', 0),
    nodeBackgroundPath('later-label-background', 1),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.paintOrderComparison.nativeOccludedTextCount, 1);
  assert.equal(result.paintOrderComparison.officialOccludedTextCount, 1);
});

test('recognizes solid SVG rectangle paths without a node-bkg class', () => {
  const result = compareDetailManifests(
    manifest([text('covered-label', 0), shape('background', 1)]),
    manifest([text('covered-label', 0), rectanglePath('background', 1)]),
  );
  assert.equal(result.status, 'pass');
  assert.equal(result.paintOrderComparison.nativeOccludedTextCount, 1);
  assert.equal(result.paintOrderComparison.officialOccludedTextCount, 1);
});

test('keeps true SVG rectangle paint-order differences as failures', () => {
  const result = compareDetailManifests(
    manifest([rectanglePath('background', 0), text('covered-label', 1)]),
    manifest([text('covered-label', 0), rectanglePath('background', 1)]),
  );
  assert.equal(result.status, 'fail');
  assert.ok(result.findings.some(({ code }) => code === 'paint-order-occlusion'));
});

test('rejects transparent, curved and rotated path bounding boxes as occluders', () => {
  for (const override of [
    { fill: color(255, 255, 255, 0) },
    { pathData: 'M30 8 C40 8 60 8 80 8 L80 22 L30 22' },
    {
      pathTransform: { a: 0.7, b: 0.7, c: -0.7, d: 0.7 },
      outlinePoints: [
        { x: 55, y: 8 }, { x: 80, y: 15 },
        { x: 55, y: 22 }, { x: 30, y: 15 },
      ],
    },
  ]) {
    const result = compareDetailManifests(
      manifest([text('covered-label', 0), shape('background', 1)]),
      manifest([
        text('covered-label', 0),
        { ...rectanglePath('background', 1), ...override },
      ]),
    );
    assert.equal(result.paintOrderComparison.officialOccludedTextCount, 0);
    assert.ok(result.findings.some(({ code }) => code === 'paint-order-occlusion'));
  }
});

test('tolerates small occlusion-ratio differences from text box semantics', () => {
  const official = manifest([
    text('covered-label', 0),
    shape('later-label-background', 1),
  ]);
  const native = manifest([
    text('covered-label', 0),
    {
      ...shape('later-label-background', 1),
      bounds: { x: 34.6, y: 8, width: 50, height: 14 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.paintOrderComparison.ratioDifferences, []);
});

test('tolerates small occlusion differences across the detection threshold', () => {
  const official = manifest([
    text('covered-label', 0),
    {
      ...shape('later-label-background', 1),
      bounds: { x: 54.5, y: 8, width: 50, height: 14 },
    },
  ]);
  const native = manifest([
    text('covered-label', 0),
    {
      ...shape('later-label-background', 1),
      bounds: { x: 55.5, y: 8, width: 50, height: 14 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.paintOrderComparison.mismatches, []);
});

test('keeps matching occlusion topology with different extents in review', () => {
  const official = manifest([
    text('covered-label', 0),
    shape('later-label-background', 1),
  ]);
  const native = manifest([
    text('covered-label', 0),
    {
      ...shape('later-label-background', 1),
      bounds: { x: 50, y: 8, width: 50, height: 14 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'paint-order-occlusion-ratio'
    ),
  );
  assert.ok(
    result.findings.every((finding) =>
      finding.code !== 'paint-order-occlusion'
    ),
  );
});

test('compares the union of adjacent later opaque regions', () => {
  const native = manifest([
    text('covered-label', 0),
    {
      ...shape('first-swatch', 1),
      bounds: { x: 30, y: 8, width: 10, height: 14 },
    },
    {
      ...shape('second-swatch', 2),
      bounds: { x: 40, y: 8, width: 10, height: 14 },
    },
  ]);
  const official = manifest([
    text('covered-label', 0),
    {
      ...shape('combined-swatch-area', 1),
      bounds: { x: 30, y: 8, width: 20, height: 14 },
    },
    {
      ...shape('unrelated-swatch', 2),
      bounds: { x: 80, y: 8, width: 10, height: 14 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.paintOrderComparison.nativeOccludedTextCount, 1);
  assert.equal(result.paintOrderComparison.officialOccludedTextCount, 1);
});

test('normalizes occluded area by each matched text box', () => {
  const native = manifest([
    {
      ...text('covered-label', 0),
      bounds: { x: 1, y: 10, width: 98, height: 10 },
      fontSize: 16,
    },
    {
      ...shape('native-swatch', 1),
      bounds: { x: 1, y: 17, width: 98, height: 3 },
    },
  ]);
  const official = manifest([
    {
      ...text('covered-label', 0),
      bounds: { x: 25, y: 10, width: 50, height: 10 },
      fontSize: 16,
    },
    {
      ...shape('official-swatch', 1),
      bounds: { x: 25, y: 17, width: 50, height: 3 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.paintOrderComparison.ratioDifferences, []);
});

test('separates missing text from harmless element multiplicity', () => {
  const official = manifest([
    text('shared', 0),
    text('shared', 1),
    text('official-only', 2),
  ]);
  const native = manifest([
    text('shared', 0),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some((finding) => finding.code === 'text-presence'),
  );
  assert.ok(
    result.findings.some((finding) => finding.code === 'text-multiplicity'),
  );
});

test('accepts spatially equivalent segmented text', () => {
  const official = manifest([
    {
      ...text('[Another request owns', 0),
      bounds: { x: 10, y: 10, width: 80, height: 10 },
    },
    {
      ...text('refresh]', 1),
      bounds: { x: 10, y: 24, width: 80, height: 10 },
    },
  ]);
  const native = manifest([
    {
      ...text('[Another request owns refresh]', 0),
      bounds: { x: 10, y: 10, width: 80, height: 24 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.textComparison.segmentationEquivalent, true);
  assert.equal(result.textComparison.segmentationGroups.length, 1);
});

test('compares overlap topology through equivalent segmented text groups', () => {
  const native = manifest([
    {
      ...text('Delivery', 0),
      bounds: { x: 10, y: 10, width: 50, height: 10 },
    },
    {
      ...text('observes', 1),
      bounds: { x: 40, y: 10, width: 50, height: 10 },
    },
  ]);
  const official = manifest([
    {
      ...text('Delivery', 0),
      bounds: { x: 10, y: 10, width: 50, height: 10 },
    },
    {
      ...text('observ', 1),
      bounds: { x: 40, y: 10, width: 25, height: 10 },
    },
    {
      ...text('es', 2),
      bounds: { x: 65, y: 10, width: 25, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.textComparison.segmentationEquivalent, true);
  assert.deepEqual(result.overlapComparison.mismatches, []);
  assert.deepEqual(result.overlapComparison.ratioDifferences, []);
});

test('reports material overlap differences through segmented text groups', () => {
  const native = manifest([
    {
      ...text('Delivery', 0),
      bounds: { x: 10, y: 10, width: 50, height: 10 },
    },
    {
      ...text('observes', 1),
      bounds: { x: 40, y: 10, width: 50, height: 10 },
    },
  ]);
  const official = manifest([
    {
      ...text('Delivery', 0),
      bounds: { x: 10, y: 10, width: 50, height: 10 },
    },
    {
      ...text('observ', 1),
      bounds: { x: 52, y: 10, width: 19, height: 10 },
    },
    {
      ...text('es', 2),
      bounds: { x: 71, y: 10, width: 19, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.equal(result.textComparison.segmentationEquivalent, true);
  assert.equal(result.overlapComparison.mismatches.length, 1);
  assert.deepEqual(
    result.overlapComparison.ratioDifferences.map(
      ({ nativeOverlap, officialOverlap }) => ({
        nativeOverlap,
        officialOverlap,
      }),
    ),
    [{ nativeOverlap: 0.4, officialOverlap: 0.211 }],
  );
});

test('keeps spatially divergent segmented text in review', () => {
  const official = manifest([
    text('[Another request owns refresh]', 0),
  ]);
  const native = manifest([
    text('[Another request owns', 0),
    {
      ...text('refresh]', 1),
      bounds: { x: 85, y: 85, width: 10, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.equal(result.textComparison.segmentationEquivalent, false);
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'text-segmentation'
    ),
  );
});

test('uses SVG tspan segments to preserve wrapped word boundaries', () => {
  const official = manifest([
    {
      ...text('São Paulogeneral availability', 0),
      textSegments: ['São Paulo', 'general availability'],
    },
  ]);
  const native = manifest([
    text('São Paulo\ngeneral availability', 0),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
});

test('compares font size instead of HTML label container width', () => {
  const native = manifest([
    {
      ...text('Shared label', 0),
      bounds: { x: 40, y: 10, width: 20, height: 10 },
      fontSize: 14,
    },
  ]);
  const official = manifest([
    {
      ...text('Shared label', 0),
      bounds: { x: 0, y: 10, width: 100, height: 10 },
      fontSize: 14,
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
});

test('reports actual font-size divergence', () => {
  const native = manifest([
    {
      ...text('Shared label', 0),
      fontSize: 10,
    },
  ]);
  const official = manifest([
    {
      ...text('Shared label', 0),
      fontSize: 24,
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'text-size'),
  );
});

test('tolerates text-overlap threshold flips with similar overlap ratios', () => {
  const native = manifest([
    text('Left label', 0),
    {
      ...text('Right label', 1),
      bounds: { x: 43, y: 10, width: 50, height: 10 },
    },
  ]);
  const official = manifest([
    text('Left label', 0),
    {
      ...text('Right label', 1),
      bounds: { x: 42.4, y: 10, width: 50, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.overlapComparison.mismatches, []);
  assert.equal(result.overlapComparison.toleratedMismatches.length, 1);
});

test('reports materially different text-overlap ratios', () => {
  const native = manifest([
    text('Left label', 0),
    {
      ...text('Right label', 1),
      bounds: { x: 50, y: 10, width: 50, height: 10 },
    },
  ]);
  const official = manifest([
    text('Left label', 0),
    {
      ...text('Right label', 1),
      bounds: { x: 42.4, y: 10, width: 50, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'text-overlap'),
  );
  assert.deepEqual(
    result.overlapComparison.mismatches,
    ['Left label#0|Right label#0'],
  );
});

test('uses oriented text outlines instead of rotated bounding boxes', () => {
  const left = {
    ...text('Left diagonal', 0),
    bounds: { x: 0, y: 0, width: 50, height: 50 },
    outlinePoints: [
      { x: 0, y: 5 },
      { x: 5, y: 0 },
      { x: 50, y: 45 },
      { x: 45, y: 50 },
    ],
  };
  const right = (offset) => ({
    ...text('Right diagonal', 1),
    bounds: { x: offset, y: 0, width: 50, height: 50 },
    outlinePoints: [
      { x: offset, y: 5 },
      { x: offset + 5, y: 0 },
      { x: offset + 50, y: 45 },
      { x: offset + 45, y: 50 },
    ],
  });

  const result = compareDetailManifests(
    manifest([left, right(20)]),
    manifest([left, right(30)]),
  );

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.overlapComparison.mismatches, []);
  assert.deepEqual(result.overlapComparison.ratioDifferences, []);
});

test('ignores text overlaps hidden behind matching later backgrounds', () => {
  const nativeCovered = {
    ...text('Covered', 0),
    fontSize: 14,
  };
  const officialCovered = {
    ...text('Covered', 0),
    fontSize: 14,
  };
  const nativeVisible = {
    ...text('Visible', 2),
    bounds: { x: 30, y: 10, width: 50, height: 10 },
    fontSize: 14,
  };
  const officialVisible = {
    ...text('Visible', 2),
    bounds: { x: 70, y: 10, width: 20, height: 10 },
    fontSize: 14,
  };
  const native = manifest([
    nativeCovered,
    shape('later-label-background', 1),
    nativeVisible,
  ]);
  const official = manifest([
    officialCovered,
    nodeBackgroundPath('later-label-background', 1),
    officialVisible,
  ]);

  const result = compareDetailManifests(native, official);

  assert.ok(
    result.findings.every((finding) => finding.code !== 'text-overlap'),
  );
});

test('keeps matched text comparable across minor visibility threshold flips', () => {
  const firstLabel = text('Repeated point', 0);
  const secondLabel = text('Repeated point', 2);
  const native = manifest([
    firstLabel,
    {
      ...shape('later-point', 1),
      bounds: { x: 55.5, y: 8, width: 50, height: 14 },
    },
    secondLabel,
  ]);
  const official = manifest([
    firstLabel,
    {
      ...shape('later-point', 1),
      bounds: { x: 54.5, y: 8, width: 50, height: 14 },
    },
    secondLabel,
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.overlapComparison.mismatches, []);
  assert.deepEqual(result.overlapComparison.ratioDifferences, []);
});

test('keeps unique matched text comparable across visibility threshold flips', () => {
  const native = manifest([
    text('Partially covered', 0),
    {
      ...shape('later-point', 1),
      bounds: { x: 55.5, y: 8, width: 50, height: 14 },
    },
    {
      ...text('Adjacent label', 2),
      bounds: { x: 30, y: 10, width: 50, height: 10 },
    },
  ]);
  const official = manifest([
    text('Partially covered', 0),
    {
      ...shape('later-point', 1),
      bounds: { x: 54.5, y: 8, width: 50, height: 14 },
    },
    {
      ...text('Adjacent label', 2),
      bounds: { x: 30, y: 10, width: 50, height: 10 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.overlapComparison.mismatches, []);
  assert.deepEqual(result.overlapComparison.ratioDifferences, []);
});

test('ignores non-rendered zero-size text duplicates', () => {
  const official = manifest([
    text('Evidence', 0),
    {
      ...text('Evidence', 1),
      bounds: { x: -20, y: -30, width: 0, height: 0 },
    },
  ]);
  const native = manifest([
    text('Evidence', 0),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.official.textCount, 1);
  assert.deepEqual(result.textComparison.multiplicityDifferences, []);
});

test('treats zero-gap SVG dash arrays as solid strokes', () => {
  const native = manifest([
    path(0, 'Solid', []),
  ]);
  const official = manifest([
    path(0, 'Dashed', [0, 0, 50, 0]),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.styleComparison.nativeStrokePatterns, { Solid: 1 });
  assert.deepEqual(result.styleComparison.officialStrokePatterns, { Solid: 1 });
});

test('treats Mermaid neo marker clearance on solid edges as solid', () => {
  const native = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Triangle',
      markerEndPoint: { x: 60, y: 15 },
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Dashed', [0, 0, 50, 4]),
      classes: [
        'edge-thickness-normal',
        'edge-pattern-solid',
        'flowchart-link',
      ],
      arrowEnd: 'Marker',
      markerEndPoint: { x: 60, y: 15 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.styleComparison.nativeStrokePatterns, { Solid: 1 });
  assert.deepEqual(result.styleComparison.officialStrokePatterns, { Solid: 1 });
});

test('prefers Mermaid patterned edge classes over the base solid class', () => {
  const native = manifest([
    {
      ...path(0, 'Dotted', []),
      arrowEnd: 'Triangle',
      markerEndPoint: { x: 60, y: 15 },
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Dashed', [0, 0, 2, 2, 2, 2, 4]),
      classes: [
        'edge-thickness-normal',
        'edge-pattern-dotted',
        'edge-pattern-solid',
        'flowchart-link',
      ],
      arrowEnd: 'Marker',
      markerEndPoint: { x: 60, y: 15 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.styleComparison.nativeStrokePatterns, { Dotted: 1 });
  assert.deepEqual(result.styleComparison.officialStrokePatterns, { Dashed: 1 });
});

test('preserves visible Mermaid edge animation dashes on solid edges', () => {
  const native = manifest([
    {
      ...path(0, 'Dashed', [9, 5]),
      animated: true,
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Dashed', [9, 5]),
      classes: [
        'edge-thickness-normal',
        'edge-pattern-solid',
        'flowchart-link',
        'edge-animation-fast',
      ],
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.styleComparison.nativeStrokePatterns, { Dashed: 1 });
  assert.deepEqual(result.styleComparison.officialStrokePatterns, { Dashed: 1 });
});

test('treats Mermaid datastore outline construction as solid', () => {
  const native = manifest([
    {
      ...shape('database', 0),
      bounds: { x: 8, y: 8, width: 180, height: 72 },
    },
  ]);
  const official = manifest([
    {
      ...shape('database', 0),
      role: 'rect',
      classes: ['basic', 'label-container'],
      bounds: { x: 8, y: 8, width: 180, height: 72 },
      strokePattern: 'Dashed',
      dashIntervals: [180, 72],
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.deepEqual(result.styleComparison.nativeStrokePatterns, { Solid: 1 });
  assert.deepEqual(result.styleComparison.officialStrokePatterns, { Solid: 1 });
});

test('detects a declared marker hidden beneath a later opaque node', () => {
  const hiddenNative = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Triangle',
      markerEndPoint: { x: 65, y: 50 },
    },
    {
      ...shape('target-node', 1),
      bounds: { x: 60, y: 30, width: 30, height: 40 },
    },
  ]);
  const visibleOfficial = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Marker',
      markerEndPoint: { x: 60, y: 50 },
    },
    {
      ...shape('target-node', 1),
      bounds: { x: 60, y: 30, width: 30, height: 40 },
    },
  ]);

  const result = compareDetailManifests(hiddenNative, visibleOfficial);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some((finding) => finding.code === 'marker-occlusion'),
  );
  assert.equal(
    result.markerVisibilityComparison.native[0].occlusionDepth,
    0.05,
  );
  assert.equal(
    result.markerVisibilityComparison.official[0].occlusionDepth,
    0,
  );
});

test('keeps moderate marker-anchor depth differences in review', () => {
  const native = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Triangle',
      markerEndPoint: { x: 62, y: 50 },
    },
    {
      ...shape('target-node', 1),
      bounds: { x: 60, y: 30, width: 30, height: 40 },
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Marker',
      markerEndPoint: { x: 60, y: 50 },
    },
    {
      ...shape('target-node', 1),
      bounds: { x: 60, y: 30, width: 30, height: 40 },
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'marker-occlusion-depth'
    ),
  );
});

test('does not treat an ellipse corner as opaque marker coverage', () => {
  const marker = {
    ...path(0, 'Solid', []),
    arrowEnd: 'Triangle',
    markerEndPoint: { x: 62, y: 32 },
  };
  const ellipse = {
    ...shape('target-node', 1),
    role: 'Ellipse',
    bounds: { x: 60, y: 30, width: 30, height: 40 },
  };

  const result = compareDetailManifests(
    manifest([marker, ellipse]),
    manifest([{ ...marker, arrowEnd: 'Marker' }, { ...ellipse, role: 'ellipse' }]),
  );

  assert.equal(result.status, 'pass');
});

test('does not treat a polygon bounding-box corner as marker coverage', () => {
  const polygon = {
    ...shape('target-node', 1),
    role: 'polygon',
    bounds: { x: 60, y: 30, width: 30, height: 40 },
    outlinePoints: [
      { x: 75, y: 30 },
      { x: 90, y: 50 },
      { x: 75, y: 70 },
      { x: 60, y: 50 },
    ],
  };
  const nativeMarker = {
    ...path(0, 'Solid', []),
    arrowEnd: 'Triangle',
    markerEndPoint: { x: 62, y: 32 },
  };
  const officialMarker = {
    ...path(0, 'Solid', []),
    arrowEnd: 'Marker',
    markerEndPoint: { x: 60, y: 50 },
  };

  const result = compareDetailManifests(
    manifest([nativeMarker, polygon]),
    manifest([officialMarker, polygon]),
  );

  assert.equal(result.status, 'pass');
  assert.equal(
    result.markerVisibilityComparison.native[0].occlusionDepth,
    0,
  );
});

test('detects a marker inside a filled polygon outline', () => {
  const polygon = {
    ...shape('target-node', 1),
    role: 'polygon',
    bounds: { x: 60, y: 30, width: 30, height: 40 },
    outlinePoints: [
      { x: 75, y: 30 },
      { x: 90, y: 50 },
      { x: 75, y: 70 },
      { x: 60, y: 50 },
    ],
  };
  const hiddenNative = {
    ...path(0, 'Solid', []),
    arrowEnd: 'Triangle',
    markerEndPoint: { x: 75, y: 50 },
  };
  const visibleOfficial = {
    ...path(0, 'Solid', []),
    arrowEnd: 'Marker',
    markerEndPoint: { x: 60, y: 50 },
  };

  const result = compareDetailManifests(
    manifest([hiddenNative, polygon]),
    manifest([visibleOfficial, polygon]),
  );

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some((finding) => finding.code === 'marker-occlusion'),
  );
  assert.ok(
    result.markerVisibilityComparison.native[0].occlusionDepth > 0.1,
  );
});

test('does not accept matching marker metadata without endpoint anchors', () => {
  const native = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Triangle',
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Solid', []),
      arrowEnd: 'Marker',
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'fail');
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'marker-anchor-missing'
    ),
  );
});

test('preserves real positive-gap dash patterns', () => {
  const native = manifest([
    path(0, 'Solid', []),
  ]);
  const official = manifest([
    path(0, 'Dashed', [3, 3]),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'stroke-pattern'),
  );
});

test('accepts equivalent stroke classes across decomposed SVG geometry', () => {
  const native = manifest([
    {
      ...path(0, 'Solid', []),
      geometryPathCount: 10,
    },
    path(1, 'Dashed', [3, 3]),
  ]);
  const official = manifest([
    ...Array.from(
      { length: 10 },
      (_, index) => path(index, 'Solid', []),
    ),
    path(10, 'Dashed', [3, 3]),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
});

test('accepts equivalent geometry across compound and decomposed objects', () => {
  const native = manifest([
    {
      ...shape('compound-actor', 0),
      geometryPathCount: 6,
    },
  ]);
  const official = manifest([
    path(0, 'Solid', []),
    path(1, 'Solid', []),
    path(2, 'Solid', []),
    path(3, 'Solid', []),
    path(4, 'Solid', []),
    path(5, 'Solid', []),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.elementComparison.ratios.geometryObject, 1 / 6);
  assert.equal(result.elementComparison.ratios.geometryExpanded, 1);
  assert.equal(result.elementComparison.ratios.geometry, 1);
});

test('accepts matching geometry objects when primitive expansion differs', () => {
  const native = manifest([
    path(0, 'Solid', []),
    {
      ...shape('compound-node', 1),
      geometryPathCount: 3,
    },
    shape('plain-node', 2),
  ]);
  const official = manifest([
    path(0, 'Solid', []),
    shape('compound-node', 1),
    shape('plain-node', 2),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.elementComparison.ratios.geometryObject, 1);
  assert.equal(result.elementComparison.ratios.geometryExpanded, 5 / 3);
});

test('ignores zero-path label background geometry', () => {
  const native = manifest([
    path(0, 'Solid', []),
    {
      ...shape('L_A_B_0_label_background', 1),
      geometryPathCount: 0,
    },
  ]);
  const official = manifest([
    path(0, 'Solid', []),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.elementComparison.ratios.geometryObject, 1);
  assert.equal(result.elementComparison.ratios.geometryExpanded, 1);
});

test('counts zero-path subgraph rectangles as semantic geometry', () => {
  const native = manifest([
    path(0, 'Solid', []),
    {
      ...shape('subgraph_G', 1),
      geometryPathCount: 0,
    },
  ]);
  const official = manifest([
    path(0, 'Solid', []),
    shape('subgraph_G', 1),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.elementComparison.ratios.geometryObject, 1);
  assert.equal(result.elementComparison.ratios.geometryExpanded, 1);
});

test('collapses Rough.js child paths by their explicit semantic group', () => {
  const native = manifest([
    shape('entity-box', 0),
    {
      ...shape('entity-grid', 1),
      geometryPathCount: 3,
    },
  ]);
  const official = manifest([
    {
      ...path(0, 'Solid', []),
      geometryGroup: 'rough-outer',
    },
    {
      ...path(1, 'Solid', []),
      geometryGroup: 'rough-outer',
    },
    {
      ...path(2, 'Solid', []),
      geometryGroup: 'rough-divider-1',
    },
    {
      ...path(3, 'Solid', []),
      geometryGroup: 'rough-divider-1',
    },
    {
      ...path(4, 'Solid', []),
      geometryGroup: 'rough-divider-2',
    },
    {
      ...path(5, 'Solid', []),
      geometryGroup: 'rough-divider-2',
    },
    {
      ...path(6, 'Solid', []),
      geometryGroup: 'rough-divider-3',
    },
    {
      ...path(7, 'Solid', []),
      geometryGroup: 'rough-divider-3',
    },
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'pass');
  assert.equal(result.elementComparison.ratios.geometryObject, 1 / 2);
  assert.equal(result.elementComparison.ratios.geometryExpanded, 1);
  assert.equal(result.elementComparison.ratios.geometry, 1);
});

test('reports geometry count when object and expanded counts both diverge', () => {
  const native = manifest([
    {
      ...shape('compound-actor', 0),
      geometryPathCount: 6,
    },
    shape('second-object', 1),
    shape('third-object', 2),
  ]);
  const official = manifest([
    path(0, 'Solid', []),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'element-count'),
  );
});

test('reports raster mask, edge, and color divergence', () => {
  const baseline = {
    gridSize: 2,
    mask: [true, false, false, true],
    edges: [true, false, false, true],
    colors: [0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0],
  };
  const divergent = {
    gridSize: 2,
    mask: [false, true, true, false],
    edges: [false, true, true, false],
    colors: [255, 0, 0, 0, 255, 0, 0, 255, 0, 255, 0, 0],
  };

  assert.equal(compareRasterFeatures(baseline, baseline).status, 'pass');
  assert.equal(
    compareRasterFeatures(baseline, divergent, {
      minimumEdgeF1: 0.9,
    }).status,
    'review',
  );
});

test('accepts one-cell foreground-mask shifts and keeps exact IoU', () => {
  const baseline = raster(5, [6, 11, 16]);
  const shifted = raster(5, [7, 12, 17]);

  const result = compareRasterFeatures(baseline, shifted);

  assert.equal(result.status, 'pass');
  assert.equal(result.exactMaskIou, 0);
  assert.equal(result.maskIou, 1);
  assert.ok(result.exactColorError > 0);
  assert.equal(result.colorError, 0);
});

test('reports foreground masks separated beyond the tolerance radius', () => {
  const result = compareRasterFeatures(
    raster(5, [0]),
    raster(5, [24]),
    {
      minimumEdgeF1: 0,
      maximumColorError: 1,
    },
  );

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'raster-mask'),
  );
  assert.ok(result.exactColorError > 0);
  assert.equal(result.colorError, 0);
});

test('reports foreground color differences within matching geometry', () => {
  const result = compareRasterFeatures(
    raster(5, [6, 11, 16]),
    raster(5, [6, 11, 16], [255, 0, 0]),
  );

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) => finding.code === 'raster-color'),
  );
});

function manifest(elements) {
  return {
    schemaVersion: 1,
    renderer: 'fixture',
    viewport: {
      x: 0,
      y: 0,
      width: 100,
      height: 100,
      background: color(255, 255, 255),
    },
    elements,
  };
}

function path(order, strokePattern, dashIntervals) {
  return {
    order,
    zIndex: 18,
    type: 'path',
    role: 'Path',
    bounds: { x: 10, y: 10, width: 50, height: 10 },
    fill: null,
    stroke: color(0, 0, 0),
    strokeWidth: 1,
    strokePattern,
    dashIntervals,
    arrowStart: 'None',
    arrowEnd: 'None',
  };
}

function text(value, order) {
  return {
    order,
    zIndex: 18,
    type: 'text',
    role: 'text',
    bounds: { x: 10, y: 10, width: 50, height: 10 },
    text: value,
    fill: color(0, 0, 0),
    stroke: null,
  };
}

function shape(id, order) {
  return {
    order,
    zIndex: 18,
    type: 'shape',
    role: 'Rectangle',
    id,
    bounds: { x: 30, y: 8, width: 50, height: 14 },
    fill: color(255, 255, 255),
    stroke: color(0, 0, 0),
    strokeWidth: 1,
    strokePattern: 'Solid',
    arrowStart: 'None',
    arrowEnd: 'None',
  };
}

function nodeBackgroundPath(id, order) {
  return {
    ...shape(id, order),
    type: 'path',
    role: 'path',
    classes: ['node-bkg'],
  };
}

function rectanglePath(id, order) {
  return {
    ...shape(id, order),
    type: 'path',
    role: 'path',
    pathData: 'M30 8 L80 8 L80 22 L30 22',
    pathTransform: { a: 1, b: 0, c: 0, d: 1 },
    outlinePoints: [
      { x: 30, y: 8 }, { x: 80, y: 8 },
      { x: 80, y: 22 }, { x: 30, y: 22 },
    ],
  };
}

function color(r, g, b, a = 255) {
  return { r, g, b, a };
}

function raster(gridSize, foregroundIndices, foreground = [0, 0, 0]) {
  const mask = Array(gridSize * gridSize).fill(false);
  for (const index of foregroundIndices) {
    mask[index] = true;
  }
  const colors = Array.from(
    { length: gridSize * gridSize },
    (_, index) => mask[index] ? foreground : [255, 255, 255],
  ).flat();
  return {
    gridSize,
    mask,
    edges: [...mask],
    colors,
  };
}
