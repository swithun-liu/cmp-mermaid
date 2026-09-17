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

test('treats equivalent wrapped text as a segmentation review', () => {
  const official = manifest([
    text('[Another request owns', 0),
    text('refresh]', 1),
  ]);
  const native = manifest([
    text('[Another request owns refresh]', 0),
  ]);

  const result = compareDetailManifests(native, official);

  assert.equal(result.status, 'review');
  assert.ok(
    result.findings.some((finding) =>
      finding.code === 'text-segmentation'
    ),
  );
  assert.ok(
    result.findings.every((finding) => finding.code !== 'text-presence'),
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
