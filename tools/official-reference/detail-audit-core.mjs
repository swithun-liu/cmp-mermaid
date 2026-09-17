const defaultThresholds = Object.freeze({
  maximumTextCenterDistance: 0.12,
  maximumTextSizeLogRatio: Math.log(1.8),
  maximumElementCountRatio: 1.5,
  minimumPaletteCoverage: 0.35,
  occlusionOverlapRatio: 0.1,
  maximumOcclusionOverlapRatioDifference: 0.1,
  textOverlapRatio: 0.35,
  maximumTextOverlapRatioDifference: 0.08,
  clippingTolerance: 0.01,
  maximumMarkerOcclusionDepthDifference: 0.01,
});

export function manifestViewportBackground(manifest) {
  const background = manifest?.viewport?.background;
  const channels = [
    background?.r,
    background?.g,
    background?.b,
    background?.a,
  ];
  return channels.every(
    (channel) => Number.isFinite(channel) && channel >= 0 && channel <= 255,
  )
    ? channels
    : null;
}

export function compareDetailManifests(
  nativeManifest,
  officialManifest,
  expectedTexts = [],
  overrides = {},
) {
  const thresholds = { ...defaultThresholds, ...overrides };
  const findings = [];
  const native = analyzeManifest(nativeManifest, thresholds, findings, 'Native');
  const official = analyzeManifest(
    officialManifest,
    thresholds,
    findings,
    'Official',
  );

  for (const expectedText of expectedTexts) {
    validateExpectedText(native, expectedText, findings);
    validateExpectedText(official, expectedText, findings);
  }

  const textComparison = compareTexts(native, official, thresholds, findings);
  const elementComparison = compareElementCounts(
    native,
    official,
    thresholds,
    findings,
  );
  const styleComparison = compareStyles(
    native,
    official,
    thresholds,
    findings,
  );
  const markerVisibilityComparison = compareMarkerVisibility(
    native,
    official,
    thresholds,
    findings,
  );
  const paintOrderComparison = comparePaintOrder(
    native,
    official,
    thresholds,
    findings,
  );
  const overlapComparison = compareTextOverlaps(
    native,
    official,
    thresholds,
    findings,
  );

  return {
    thresholds,
    status: findings.length === 0
      ? 'pass'
      : findings.some((finding) => finding.severity === 'error')
        ? 'fail'
        : 'review',
    findings,
    native: native.summary,
    official: official.summary,
    textComparison,
    elementComparison,
    styleComparison,
    markerVisibilityComparison,
    paintOrderComparison,
    overlapComparison,
  };
}

export function compareRasterFeatures(
  native,
  official,
  overrides = {},
) {
  const thresholds = {
    minimumMaskIou: 0.42,
    minimumEdgeF1: 0.38,
    maximumColorError: 0.24,
    ...overrides,
  };
  const exactMaskIou = intersectionOverUnion(native.mask, official.mask);
  const maskIou = tolerantIntersectionOverUnion(
    native.mask,
    official.mask,
    native.gridSize,
    1,
  );
  const edgeF1 = tolerantF1(native.edges, official.edges, native.gridSize, 1);
  const exactColorError = foregroundColorError(native, official);
  const colorError = tolerantForegroundColorError(native, official, 1);
  const findings = [];
  if (maskIou < thresholds.minimumMaskIou) {
    findings.push({
      severity: 'review',
      code: 'raster-mask',
      message:
        `Tolerant foreground-mask IoU ${format(maskIou)} is below ` +
        format(thresholds.minimumMaskIou),
    });
  }
  if (edgeF1 < thresholds.minimumEdgeF1) {
    findings.push({
      severity: 'review',
      code: 'raster-edge',
      message:
        `Tolerant edge F1 ${format(edgeF1)} is below ` +
        format(thresholds.minimumEdgeF1),
    });
  }
  if (colorError > thresholds.maximumColorError) {
    findings.push({
      severity: 'review',
      code: 'raster-color',
      message:
        `Foreground color error ${format(colorError)} exceeds ` +
        format(thresholds.maximumColorError),
    });
  }
  return {
    thresholds,
    status: findings.length === 0 ? 'pass' : 'review',
    findings,
    maskIou,
    exactMaskIou,
    edgeF1,
    colorError,
    exactColorError,
  };
}

function analyzeManifest(manifest, thresholds, findings, renderer) {
  const viewport = manifest?.viewport;
  if (
    manifest?.schemaVersion !== 1 ||
    !positiveFinite(viewport?.width) ||
    !positiveFinite(viewport?.height) ||
    !Array.isArray(manifest?.elements)
  ) {
    findings.push({
      severity: 'error',
      code: 'invalid-manifest',
      renderer,
      message: `${renderer} manifest is missing a finite viewport or elements`,
    });
  }
  const safeViewport = {
    x: finite(viewport?.x) ? viewport.x : 0,
    y: finite(viewport?.y) ? viewport.y : 0,
    width: positiveFinite(viewport?.width) ? viewport.width : 1,
    height: positiveFinite(viewport?.height) ? viewport.height : 1,
  };
  const elements = (manifest?.elements ?? [])
    .filter(isRenderedManifestElement)
    .map((element, index) => {
      const bounds = normalizeBounds(element.bounds, safeViewport);
      return {
        ...element,
        order: finite(element.order) ? element.order : index,
        strokePattern: effectiveStrokePattern(element),
        normalizedBounds: bounds,
        normalizedText: element.type === 'text'
          ? normalizeManifestText(element)
          : null,
        normalizedMarkerStartPoint: normalizePoint(
          element.markerStartPoint,
          safeViewport,
        ),
        normalizedMarkerEndPoint: normalizePoint(
          element.markerEndPoint,
          safeViewport,
        ),
        clippedFraction: clippedFraction(bounds),
      };
    });
  const texts = elements.filter(
    (element) => element.type === 'text' && element.normalizedText.length > 0,
  );
  const clippedTexts = texts.filter(
    (element) => element.clippedFraction > thresholds.clippingTolerance,
  );
  const typeCounts = countBy(elements, (element) => element.type);
  const textCounts = countBy(texts, (element) => element.normalizedText);
  const summary = {
    renderer,
    elementCount: elements.length,
    typeCounts,
    textCount: texts.length,
    uniqueTextCount: Object.keys(textCounts).length,
    clippedTextCount: clippedTexts.length,
    textOverlapCount: textOverlapPairs(texts, thresholds.textOverlapRatio).length,
  };
  return {
    renderer,
    manifest,
    viewport: safeViewport,
    elements,
    texts,
    textCounts,
    typeCounts,
    clippedTexts,
    summary,
  };
}

function validateExpectedText(analysis, expectedText, findings) {
  const normalizedExpected = normalizeText(expectedText);
  const found = analysis.texts.some((element) =>
    element.normalizedText.includes(normalizedExpected)
  );
  if (!found) {
    findings.push({
      severity: 'error',
      code: 'missing-expected-text',
      renderer: analysis.renderer,
      text: expectedText,
      message: `${analysis.renderer} is missing expected text "${expectedText}"`,
    });
  }
}

function compareTexts(native, official, thresholds, findings) {
  const allText = new Set([
    ...Object.keys(native.textCounts),
    ...Object.keys(official.textCounts),
  ]);
  const absentInNative = [];
  const absentInOfficial = [];
  const multiplicityDifferences = [];
  for (const text of allText) {
    const nativeCount = native.textCounts[text] ?? 0;
    const officialCount = official.textCounts[text] ?? 0;
    if (nativeCount === 0) {
      absentInNative.push({ text, officialCount });
    } else if (officialCount === 0) {
      absentInOfficial.push({ text, nativeCount });
    } else if (nativeCount !== officialCount) {
      multiplicityDifferences.push({ text, nativeCount, officialCount });
    }
  }
  const segmentationOnly =
    absentInNative.length > 0 &&
    absentInOfficial.length > 0 &&
    compactTextSequence(absentInNative) ===
      compactTextSequence(absentInOfficial);
  if (absentInNative.length > 0 || absentInOfficial.length > 0) {
    findings.push({
      severity: segmentationOnly ? 'review' : 'error',
      code: segmentationOnly ? 'text-segmentation' : 'text-presence',
      message:
        segmentationOnly
          ? 'Rendered text content matches but element segmentation differs'
          : `${absentInNative.length} text values are absent in Native and ` +
            `${absentInOfficial.length} are absent in Official`,
      absentInNative,
      absentInOfficial,
    });
  }
  if (multiplicityDifferences.length > 0) {
    findings.push({
      severity: 'review',
      code: 'text-multiplicity',
      message:
        `${multiplicityDifferences.length} shared text values have different ` +
        'element multiplicities',
      differences: multiplicityDifferences,
    });
  }

  const matches = matchTextElements(native.texts, official.texts);
  const centerDistances = matches.map(({ native: left, official: right }) =>
    centerDistance(left.normalizedBounds, right.normalizedBounds)
  );
  const sizeLogRatios = matches.map(({ native: left, official: right }) =>
    textSizeLogRatio(left, right)
  );
  const maximumCenterDistance = maximum(centerDistances);
  const maximumSizeLogRatio = maximum(sizeLogRatios);
  if (maximumCenterDistance > thresholds.maximumTextCenterDistance) {
    findings.push({
      severity: 'review',
      code: 'text-position',
      message:
        `Maximum normalized text-center distance ` +
        `${format(maximumCenterDistance)} exceeds ` +
        format(thresholds.maximumTextCenterDistance),
    });
  }
  if (maximumSizeLogRatio > thresholds.maximumTextSizeLogRatio) {
    findings.push({
      severity: 'review',
      code: 'text-size',
      message:
        `Maximum text-size ratio ${format(Math.exp(maximumSizeLogRatio))} ` +
        `exceeds ${format(Math.exp(thresholds.maximumTextSizeLogRatio))}`,
    });
  }

  const nativeClipped = new Set(
    native.clippedTexts.map(textOccurrenceKey),
  );
  const officialClipped = new Set(
    official.clippedTexts.map(textOccurrenceKey),
  );
  const clippingMismatch = symmetricDifference(nativeClipped, officialClipped);
  if (clippingMismatch.length > 0) {
    findings.push({
      severity: 'error',
      code: 'text-clipping',
      message: `Text clipping differs for ${clippingMismatch.length} occurrences`,
      occurrences: clippingMismatch,
    });
  }

  return {
    absentInNative,
    absentInOfficial,
    multiplicityDifferences,
    matchedCount: matches.length,
    maximumCenterDistance,
    p95CenterDistance: percentile(centerDistances, 0.95),
    maximumSizeRatio: Math.exp(maximumSizeLogRatio),
  };
}

function textSizeLogRatio(left, right) {
  if (positiveFinite(left.fontSize) && positiveFinite(right.fontSize)) {
    return absoluteLogRatio(left.fontSize, right.fontSize);
  }
  return Math.max(
    absoluteLogRatio(left.normalizedBounds.width, right.normalizedBounds.width),
    absoluteLogRatio(left.normalizedBounds.height, right.normalizedBounds.height),
  );
}

function compareElementCounts(native, official, thresholds, findings) {
  const categories = {
    text: (analysis) => analysis.typeCounts.text ?? 0,
    geometry: (analysis) => analysis.elements
      .filter((element) => element.type === 'shape' || element.type === 'path')
      .reduce(
        (sum, element) =>
          sum + Math.max(1, element.geometryPathCount ?? 1),
        0,
      ),
    asset: (analysis) => analysis.typeCounts.asset ?? 0,
  };
  const ratios = {};
  for (const [category, count] of Object.entries(categories)) {
    ratios[category] = ratio(
      Math.max(1, count(native)),
      Math.max(1, count(official)),
    );
    const countRatio = ratios[category];
    if (
      countRatio > thresholds.maximumElementCountRatio ||
      countRatio < 1 / thresholds.maximumElementCountRatio
    ) {
      findings.push({
        severity: 'review',
        code: 'element-count',
        type: category,
        message:
          `${category} count ratio ${format(countRatio)} is outside ` +
          `[${format(1 / thresholds.maximumElementCountRatio)}, ` +
          `${format(thresholds.maximumElementCountRatio)}]`,
      });
    }
  }
  return { ratios };
}

function compareStyles(native, official, thresholds, findings) {
  const nativePalette = colorPalette(native.elements);
  const officialPalette = colorPalette(official.elements);
  const nativePaletteCoverage = paletteCoverage(nativePalette, officialPalette);
  const officialPaletteCoverage = paletteCoverage(officialPalette, nativePalette);
  const minimumCoverage = Math.min(
    nativePaletteCoverage,
    officialPaletteCoverage,
  );
  if (minimumCoverage < thresholds.minimumPaletteCoverage) {
    findings.push({
      severity: 'review',
      code: 'color-palette',
      message:
        `Bidirectional quantized palette coverage ${format(minimumCoverage)} ` +
        `is below ${format(thresholds.minimumPaletteCoverage)}`,
    });
  }

  const nativeStrokePatterns = countStrokePatterns(native.elements);
  const officialStrokePatterns = countStrokePatterns(official.elements);
  const strokePatternMismatch = strokePatternDifference(
    nativeStrokePatterns,
    officialStrokePatterns,
  );
  if (strokePatternMismatch > 0.5) {
    findings.push({
      severity: 'review',
      code: 'stroke-pattern',
      message:
        `Stroke-pattern distribution differs by ` +
        `${format(strokePatternMismatch)}`,
    });
  }

  const nativeMarkers = markerSummary(native.elements);
  const officialMarkers = markerSummary(official.elements);
  if (
    nativeMarkers.start !== officialMarkers.start ||
    nativeMarkers.end !== officialMarkers.end
  ) {
    findings.push({
      severity: 'error',
      code: 'arrow-markers',
      message: 'Native and Official disagree on arrow-marker counts',
      native: nativeMarkers,
      official: officialMarkers,
    });
  }

  return {
    nativePaletteSize: nativePalette.size,
    officialPaletteSize: officialPalette.size,
    nativePaletteCoverage,
    officialPaletteCoverage,
    nativeStrokePatterns,
    officialStrokePatterns,
    nativeMarkers,
    officialMarkers,
  };
}

function compareMarkerVisibility(native, official, thresholds, findings) {
  const nativeProfiles = markerVisibilityProfiles(native);
  const officialProfiles = markerVisibilityProfiles(official);
  const missingAnchors = [
    ...nativeProfiles.filter((profile) => profile.point == null),
    ...officialProfiles.filter((profile) => profile.point == null),
  ];
  if (missingAnchors.length > 0) {
    findings.push({
      severity: 'error',
      code: 'marker-anchor-missing',
      message:
        `${missingAnchors.length} declared markers are missing path anchors`,
      markers: missingAnchors.map(markerDiagnostic),
    });
  }

  const differences = [];
  for (const position of ['start', 'end']) {
    const nativeAtPosition = nativeProfiles.filter(
      (profile) => profile.position === position && profile.point != null,
    );
    const officialAtPosition = officialProfiles.filter(
      (profile) => profile.position === position && profile.point != null,
    );
    const pairCount = Math.min(
      nativeAtPosition.length,
      officialAtPosition.length,
    );
    for (let index = 0; index < pairCount; index += 1) {
      const nativeProfile = nativeAtPosition[index];
      const officialProfile = officialAtPosition[index];
      const depthDifference =
        nativeProfile.occlusionDepth - officialProfile.occlusionDepth;
      if (
        Math.abs(depthDifference) >
          thresholds.maximumMarkerOcclusionDepthDifference
      ) {
        differences.push({
          position,
          occurrence: index,
          depthDifference,
          native: markerDiagnostic(nativeProfile),
          official: markerDiagnostic(officialProfile),
        });
      }
    }
  }
  const nativeDeeper = differences.filter(
    ({ depthDifference }) => depthDifference > 0,
  );
  const officialDeeper = differences.filter(
    ({ depthDifference }) => depthDifference < 0,
  );
  if (nativeDeeper.length > 0) {
    findings.push({
      severity: 'error',
      code: 'marker-occlusion',
      message:
        `${nativeDeeper.length} Native markers are materially deeper beneath ` +
        'later opaque paint than their Official counterparts',
      differences: nativeDeeper,
    });
  }
  if (officialDeeper.length > 0) {
    findings.push({
      severity: 'review',
      code: 'marker-occlusion-reference',
      message:
        `${officialDeeper.length} Official markers are materially deeper ` +
        'beneath later opaque paint than their Native counterparts',
      differences: officialDeeper,
    });
  }
  return {
    native: nativeProfiles.map(markerDiagnostic),
    official: officialProfiles.map(markerDiagnostic),
    differences,
  };
}

function markerVisibilityProfiles(analysis) {
  const profiles = [];
  for (const element of analysis.elements) {
    if (element.type !== 'path') continue;
    for (const position of ['start', 'end']) {
      const arrow = position === 'start'
        ? element.arrowStart
        : element.arrowEnd;
      if (['None', null, undefined].includes(arrow)) continue;
      const point = position === 'start'
        ? element.normalizedMarkerStartPoint
        : element.normalizedMarkerEndPoint;
      const occluders = point == null
        ? []
        : analysis.elements
          .filter((candidate) =>
            candidate.order > element.order &&
            isOpaqueMarkerOccluder(candidate)
          )
          .map((candidate) => ({
            order: candidate.order,
            id: candidate.id ?? null,
            depth: pointInsetDepth(point, candidate.normalizedBounds),
          }))
          .filter(({ depth }) => depth > 0)
          .sort((left, right) => right.depth - left.depth);
      profiles.push({
        renderer: analysis.renderer,
        pathId: element.id ?? null,
        pathOrder: element.order,
        position,
        point,
        occlusionDepth: occluders[0]?.depth ?? 0,
        occluder: occluders[0] ?? null,
      });
    }
  }
  return profiles;
}

function isOpaqueMarkerOccluder(element) {
  if (element.type !== 'shape' && element.type !== 'path') return false;
  if (element.fill?.reference != null) return true;
  return (element.fill?.a ?? 0) >= 230;
}

function pointInsetDepth(point, bounds) {
  const right = bounds.x + bounds.width;
  const bottom = bounds.y + bounds.height;
  if (
    point.x <= bounds.x ||
    point.x >= right ||
    point.y <= bounds.y ||
    point.y >= bottom
  ) {
    return 0;
  }
  return Math.min(
    point.x - bounds.x,
    right - point.x,
    point.y - bounds.y,
    bottom - point.y,
  );
}

function markerDiagnostic(profile) {
  return {
    renderer: profile.renderer,
    pathId: profile.pathId,
    pathOrder: profile.pathOrder,
    position: profile.position,
    point: profile.point,
    occlusionDepth: round(profile.occlusionDepth, 4),
    occluder: profile.occluder == null
      ? null
      : {
          order: profile.occluder.order,
          id: profile.occluder.id,
          depth: round(profile.occluder.depth, 4),
        },
  };
}

function comparePaintOrder(native, official, thresholds, findings) {
  const nativeProfiles = occlusionProfiles(native, thresholds);
  const officialProfiles = occlusionProfiles(official, thresholds);
  const keys = new Set([
    ...nativeProfiles.keys(),
    ...officialProfiles.keys(),
  ]);
  const mismatches = [];
  const ratioDifferences = [];
  for (const key of keys) {
    const nativeProfile = nativeProfiles.get(key) ?? emptyOcclusionProfile();
    const officialProfile = officialProfiles.get(key) ?? emptyOcclusionProfile();
    const nativeOccluded =
      nativeProfile.coverageRatio >= thresholds.occlusionOverlapRatio;
    const officialOccluded =
      officialProfile.coverageRatio >= thresholds.occlusionOverlapRatio;
    const nativeComparableRatio = nativeProfile.coverageRatio;
    const officialComparableRatio = officialProfile.coverageRatio;
    const commonTextArea = Math.min(
      nativeProfile.textArea,
      officialProfile.textArea,
    );
    const commonAreaDifference = Math.abs(
      ratio(nativeProfile.coveredArea, commonTextArea) -
        ratio(officialProfile.coveredArea, commonTextArea),
    );
    const comparableDifference = Math.abs(
      nativeComparableRatio - officialComparableRatio,
    );
    const materiallyDifferent =
      comparableDifference >
        thresholds.maximumOcclusionOverlapRatioDifference &&
      commonAreaDifference >
        thresholds.maximumOcclusionOverlapRatioDifference;
    if (nativeOccluded !== officialOccluded && materiallyDifferent) {
      mismatches.push({
        textOccurrence: key,
        nativeLaterOpaqueOverlaps: nativeProfile.overlaps,
        officialLaterOpaqueOverlaps: officialProfile.overlaps,
      });
    } else if (nativeOccluded && officialOccluded && materiallyDifferent) {
      ratioDifferences.push({
        textOccurrence: key,
        nativeLaterOpaqueOverlaps: nativeProfile.overlaps,
        officialLaterOpaqueOverlaps: officialProfile.overlaps,
        nativeCoverageRatio: round(nativeComparableRatio, 3),
        officialCoverageRatio: round(officialComparableRatio, 3),
      });
    }
  }
  if (mismatches.length > 0) {
    findings.push({
      severity: 'error',
      code: 'paint-order-occlusion',
      message:
        `Later opaque-element occlusion differs for ` +
        `${mismatches.length} text occurrences`,
      mismatches,
    });
  }
  if (ratioDifferences.length > 0) {
    findings.push({
      severity: 'review',
      code: 'paint-order-occlusion-ratio',
      message:
        `Later opaque-element overlap ratios differ for ` +
        `${ratioDifferences.length} text occurrences`,
      mismatches: ratioDifferences,
    });
  }
  return {
    nativeOccludedTextCount: [...nativeProfiles.values()]
      .filter(
        (profile) =>
          profile.coverageRatio >= thresholds.occlusionOverlapRatio,
      ).length,
    officialOccludedTextCount: [...officialProfiles.values()]
      .filter(
        (profile) =>
          profile.coverageRatio >= thresholds.occlusionOverlapRatio,
      ).length,
    mismatches,
    ratioDifferences,
  };
}

function compareTextOverlaps(native, official, thresholds, findings) {
  const nativeVisibleTexts = visibleTextElements(native, thresholds);
  const officialVisibleTexts = visibleTextElements(official, thresholds);
  const nativePairs = textOverlapPairs(
    nativeVisibleTexts,
    thresholds.textOverlapRatio,
  );
  const officialPairs = textOverlapPairs(
    officialVisibleTexts,
    thresholds.textOverlapRatio,
  );
  const nativeKeys = new Set(nativePairs.map((pair) => pair.key));
  const officialKeys = new Set(officialPairs.map((pair) => pair.key));
  const topologyMismatches = symmetricDifference(nativeKeys, officialKeys);
  const nativeRatios = textOverlapRatioMap(nativeVisibleTexts);
  const officialRatios = textOverlapRatioMap(officialVisibleTexts);
  const ratioDifferences = topologyMismatches.map((key) => ({
    key,
    nativeOverlap: nativeRatios.get(key) ?? 0,
    officialOverlap: officialRatios.get(key) ?? 0,
  }));
  const mismatches = ratioDifferences
    .filter(({ nativeOverlap, officialOverlap }) =>
      Math.abs(nativeOverlap - officialOverlap) >
        thresholds.maximumTextOverlapRatioDifference
    )
    .map(({ key }) => key);
  if (mismatches.length > 0) {
    findings.push({
      severity: 'review',
      code: 'text-overlap',
      message: `Text-overlap topology differs for ${mismatches.length} pairs`,
      pairs: mismatches,
    });
  }
  return {
    nativeCount: nativePairs.length,
    officialCount: officialPairs.length,
    mismatches,
    toleratedMismatches: ratioDifferences
      .filter(({ key }) => !mismatches.includes(key)),
    ratioDifferences,
  };
}

function occlusionProfiles(analysis, thresholds) {
  const occurrences = new Map();
  const profiles = new Map();
  for (const text of analysis.texts) {
    const occurrence = occurrences.get(text.normalizedText) ?? 0;
    occurrences.set(text.normalizedText, occurrence + 1);
    const key = `${text.normalizedText}#${occurrence}`;
    const intersections = analysis.elements
      .filter((element) =>
        element.order > text.order &&
        isOpaquePaintElement(element)
      )
      .map((element) =>
        intersectBounds(text.normalizedBounds, element.normalizedBounds)
      )
      .filter((bounds) => bounds != null);
    const textArea = boundsArea(text.normalizedBounds);
    const coveredArea = unionArea(intersections);
    const overlaps = intersections
      .map((bounds) => ratio(boundsArea(bounds), textArea))
      .sort((left, right) => left - right)
      .map((value) => round(value, 3));
    profiles.set(key, {
      overlaps,
      textArea,
      coveredArea,
      coverageRatio: ratio(coveredArea, textArea),
    });
  }
  return profiles;
}

function emptyOcclusionProfile() {
  return {
    overlaps: [],
    textArea: 0,
    coveredArea: 0,
    coverageRatio: 0,
  };
}

function isOpaquePaintElement(element) {
  // A path's axis-aligned bounds do not describe its painted area, so using
  // that box for occlusion would report false coverage across curves and
  // dividers. Mermaid's node-bkg paths and regular shape elements do have
  // meaningful filled-area bounds.
  const classes = Array.isArray(element.classes) ? element.classes : [];
  const isNodeBackgroundPath =
    element.type === 'path' && classes.includes('node-bkg');
  if (element.type !== 'shape' && !isNodeBackgroundPath) return false;
  if (element.fill?.reference != null) return true;
  return (element.fill?.a ?? 0) >= 230;
}

function visibleTextElements(analysis, thresholds) {
  return analysis.texts.filter((text) =>
    !analysis.elements.some((element) =>
      element.order > text.order &&
      isOpaquePaintElement(element) &&
      overlapRatio(text.normalizedBounds, element.normalizedBounds) >=
        thresholds.occlusionOverlapRatio
    )
  );
}

function textOverlapPairs(texts, minimumRatio) {
  const occurrences = occurrenceKeys(texts);
  const pairs = [];
  for (let left = 0; left < texts.length; left += 1) {
    for (let right = left + 1; right < texts.length; right += 1) {
      const overlap = Math.max(
        overlapRatio(texts[left].normalizedBounds, texts[right].normalizedBounds),
        overlapRatio(texts[right].normalizedBounds, texts[left].normalizedBounds),
      );
      if (overlap >= minimumRatio) {
        pairs.push({
          key: [occurrences[left], occurrences[right]].sort().join('|'),
          overlap: round(overlap, 3),
        });
      }
    }
  }
  return pairs;
}

function textOverlapRatioMap(texts) {
  return new Map(
    textOverlapPairs(texts, Number.EPSILON)
      .map(({ key, overlap }) => [key, overlap]),
  );
}

function matchTextElements(nativeTexts, officialTexts) {
  const officialByText = Map.groupBy(
    officialTexts,
    (element) => element.normalizedText,
  );
  const used = new Set();
  const matches = [];
  for (const native of nativeTexts) {
    const candidates = officialByText.get(native.normalizedText) ?? [];
    let best = null;
    for (const official of candidates) {
      if (used.has(official)) continue;
      const distance = centerDistance(
        native.normalizedBounds,
        official.normalizedBounds,
      );
      if (best == null || distance < best.distance) {
        best = { official, distance };
      }
    }
    if (best != null) {
      used.add(best.official);
      matches.push({ native, official: best.official });
    }
  }
  return matches;
}

function occurrenceKeys(elements) {
  const occurrences = new Map();
  return elements.map((element) => {
    const occurrence = occurrences.get(element.normalizedText) ?? 0;
    occurrences.set(element.normalizedText, occurrence + 1);
    return `${element.normalizedText}#${occurrence}`;
  });
}

function textOccurrenceKey(element, index, elements) {
  let occurrence = 0;
  for (let cursor = 0; cursor < index; cursor += 1) {
    if (elements[cursor].normalizedText === element.normalizedText) {
      occurrence += 1;
    }
  }
  return `${element.normalizedText}#${occurrence}`;
}

function normalizeBounds(bounds, viewport) {
  return {
    x: (finite(bounds?.x) ? bounds.x - viewport.x : 0) / viewport.width,
    y: (finite(bounds?.y) ? bounds.y - viewport.y : 0) / viewport.height,
    width: (finite(bounds?.width) ? bounds.width : 0) / viewport.width,
    height: (finite(bounds?.height) ? bounds.height : 0) / viewport.height,
  };
}

function normalizePoint(point, viewport) {
  if (!finite(point?.x) || !finite(point?.y)) return null;
  return {
    x: (point.x - viewport.x) / viewport.width,
    y: (point.y - viewport.y) / viewport.height,
  };
}

function clippedFraction(bounds) {
  const area = Math.max(0, bounds.width) * Math.max(0, bounds.height);
  if (area <= 0) return 0;
  const insideWidth = Math.max(
    0,
    Math.min(1, bounds.x + bounds.width) - Math.max(0, bounds.x),
  );
  const insideHeight = Math.max(
    0,
    Math.min(1, bounds.y + bounds.height) - Math.max(0, bounds.y),
  );
  return 1 - (insideWidth * insideHeight) / area;
}

function colorPalette(elements) {
  const colors = new Set();
  for (const element of elements) {
    for (const paint of [element.fill, element.stroke]) {
      if (
        paint == null ||
        paint.reference != null ||
        !finite(paint.r) ||
        !finite(paint.g) ||
        !finite(paint.b) ||
        (paint.a ?? 255) === 0
      ) {
        continue;
      }
      colors.add(
        `${Math.round(paint.r / 32)},` +
        `${Math.round(paint.g / 32)},` +
        `${Math.round(paint.b / 32)}`,
      );
    }
  }
  return colors;
}

function paletteCoverage(source, target) {
  if (source.size === 0) return target.size === 0 ? 1 : 0;
  let matches = 0;
  for (const color of source) {
    if (target.has(color)) matches += 1;
  }
  return matches / source.size;
}

function countStrokePatterns(elements) {
  return countBy(
    elements.filter((element) =>
      element.stroke != null && (element.stroke.a ?? 255) > 0
    ),
    (element) => element.strokePattern ?? 'Solid',
  );
}

function isRenderedManifestElement(element) {
  if (element?.type !== 'text') return true;
  return (
    normalizeManifestText(element).length > 0 &&
    positiveFinite(element.bounds?.width) &&
    positiveFinite(element.bounds?.height)
  );
}

function effectiveStrokePattern(element) {
  // Mermaid: packages/mermaid/src/rendering-util/rendering-elements/edges.js
  // -> insertEdge. Neo solid edges use stroke-dasharray only to leave marker
  // clearance, so the semantic edge-pattern class must take precedence.
  const classes = Array.isArray(element?.classes) ? element.classes : [];
  if (
    classes.includes('edge-pattern-dotted') ||
    classes.includes('edge-pattern-dashed')
  ) {
    return 'Dashed';
  }
  if (classes.includes('edge-pattern-solid')) {
    return 'Solid';
  }
  const intervals = Array.isArray(element?.dashIntervals)
    ? element.dashIntervals
      .filter(finite)
      .map((value) => Math.abs(value))
    : [];
  if (intervals.length === 0) {
    return element?.strokePattern ?? 'Solid';
  }
  const cycle = intervals.length % 2 === 0
    ? intervals
    : [...intervals, ...intervals];
  const hasVisibleGap = cycle.some(
    (value, index) => index % 2 === 1 && value > Number.EPSILON,
  );
  return hasVisibleGap ? (element?.strokePattern ?? 'Dashed') : 'Solid';
}

function markerSummary(elements) {
  const paths = elements.filter((element) => element.type === 'path');
  return {
    start: paths.filter((element) =>
      !['None', null, undefined].includes(element.arrowStart)
    ).length,
    end: paths.filter((element) =>
      !['None', null, undefined].includes(element.arrowEnd)
    ).length,
  };
}

function strokePatternDifference(left, right) {
  const leftCoarse = coarseStrokePatterns(left);
  const rightCoarse = coarseStrokePatterns(right);
  if (
    (leftCoarse.Patterned > 0) !== (rightCoarse.Patterned > 0)
  ) {
    return 1;
  }
  return normalizedCountDifference(leftCoarse, rightCoarse);
}

function coarseStrokePatterns(patterns) {
  return Object.entries(patterns).reduce(
    (counts, [pattern, count]) => {
      const key = pattern === 'Solid' ? 'Solid' : 'Patterned';
      counts[key] += count;
      return counts;
    },
    { Solid: 0, Patterned: 0 },
  );
}

function normalizedCountDifference(left, right) {
  const keys = new Set([...Object.keys(left), ...Object.keys(right)]);
  const leftTotal = Object.values(left).reduce((sum, count) => sum + count, 0);
  const rightTotal = Object.values(right).reduce((sum, count) => sum + count, 0);
  if (leftTotal === 0 || rightTotal === 0) {
    return leftTotal === rightTotal ? 0 : 1;
  }
  return [...keys].reduce(
    (sum, key) =>
      sum +
      Math.abs(
        (left[key] ?? 0) / leftTotal -
        (right[key] ?? 0) / rightTotal,
      ),
    0,
  ) / 2;
}

function overlapRatio(subject, occluder) {
  const intersection = intersectBounds(subject, occluder);
  return intersection == null
    ? 0
    : ratio(boundsArea(intersection), boundsArea(subject));
}

function intersectBounds(left, right) {
  const x = Math.max(left.x, right.x);
  const y = Math.max(left.y, right.y);
  const rightEdge = Math.min(
    left.x + left.width,
    right.x + right.width,
  );
  const bottomEdge = Math.min(
    left.y + left.height,
    right.y + right.height,
  );
  if (rightEdge <= x || bottomEdge <= y) return null;
  return {
    x,
    y,
    width: rightEdge - x,
    height: bottomEdge - y,
  };
}

function boundsArea(bounds) {
  return Math.max(0, bounds.width) * Math.max(0, bounds.height);
}

function unionArea(boundsList) {
  if (boundsList.length === 0) return 0;
  const xEdges = [...new Set(
    boundsList.flatMap((bounds) => [
      bounds.x,
      bounds.x + bounds.width,
    ]),
  )].sort((left, right) => left - right);
  let area = 0;
  for (let index = 0; index + 1 < xEdges.length; index += 1) {
    const left = xEdges[index];
    const right = xEdges[index + 1];
    const width = right - left;
    if (width <= 0) continue;
    const intervals = boundsList
      .filter(
        (bounds) =>
          bounds.x < right && bounds.x + bounds.width > left,
      )
      .map((bounds) => [bounds.y, bounds.y + bounds.height])
      .sort((first, second) => first[0] - second[0]);
    let coveredHeight = 0;
    let start = null;
    let end = null;
    for (const [top, bottom] of intervals) {
      if (start == null || top > end) {
        if (start != null) coveredHeight += end - start;
        start = top;
        end = bottom;
      } else {
        end = Math.max(end, bottom);
      }
    }
    if (start != null) coveredHeight += end - start;
    area += width * coveredHeight;
  }
  return area;
}

function centerDistance(left, right) {
  const dx = left.x + left.width / 2 - right.x - right.width / 2;
  const dy = left.y + left.height / 2 - right.y - right.height / 2;
  return Math.hypot(dx, dy);
}

function absoluteLogRatio(left, right) {
  if (left <= 0 && right <= 0) return 0;
  if (left <= 0 || right <= 0) return Number.POSITIVE_INFINITY;
  return Math.abs(Math.log(left / right));
}

function countBy(values, selector) {
  const counts = {};
  for (const value of values) {
    const key = selector(value);
    counts[key] = (counts[key] ?? 0) + 1;
  }
  return counts;
}

function symmetricDifference(left, right) {
  return [
    ...[...left].filter((value) => !right.has(value)),
    ...[...right].filter((value) => !left.has(value)),
  ].sort();
}

function intersectionOverUnion(left, right) {
  let intersection = 0;
  let union = 0;
  const length = Math.min(left.length, right.length);
  for (let index = 0; index < length; index += 1) {
    if (left[index] || right[index]) union += 1;
    if (left[index] && right[index]) intersection += 1;
  }
  return union === 0 ? 1 : intersection / union;
}

function tolerantIntersectionOverUnion(left, right, gridSize, radius) {
  const leftCount = left.filter(Boolean).length;
  const rightCount = right.filter(Boolean).length;
  if (leftCount === 0 || rightCount === 0) {
    return leftCount === rightCount ? 1 : 0;
  }
  const intersection = Math.min(
    matchedPixels(left, right, gridSize, radius),
    matchedPixels(right, left, gridSize, radius),
  );
  const union = leftCount + rightCount - intersection;
  return union === 0 ? 1 : intersection / union;
}

function tolerantF1(left, right, gridSize, radius) {
  const leftMatches = matchedPixels(left, right, gridSize, radius);
  const rightMatches = matchedPixels(right, left, gridSize, radius);
  const leftCount = left.filter(Boolean).length;
  const rightCount = right.filter(Boolean).length;
  const precision = leftCount === 0 ? (rightCount === 0 ? 1 : 0) : leftMatches / leftCount;
  const recall = rightCount === 0 ? (leftCount === 0 ? 1 : 0) : rightMatches / rightCount;
  return precision + recall === 0
    ? 0
    : (2 * precision * recall) / (precision + recall);
}

function matchedPixels(source, target, gridSize, radius) {
  let matches = 0;
  for (let index = 0; index < source.length; index += 1) {
    if (!source[index]) continue;
    const x = index % gridSize;
    const y = Math.floor(index / gridSize);
    let matched = false;
    for (let dy = -radius; dy <= radius && !matched; dy += 1) {
      for (let dx = -radius; dx <= radius; dx += 1) {
        const candidateX = x + dx;
        const candidateY = y + dy;
        if (
          candidateX >= 0 &&
          candidateX < gridSize &&
          candidateY >= 0 &&
          candidateY < gridSize &&
          target[candidateY * gridSize + candidateX]
        ) {
          matched = true;
          break;
        }
      }
    }
    if (matched) matches += 1;
  }
  return matches;
}

function foregroundColorError(native, official) {
  let difference = 0;
  let count = 0;
  const length = Math.min(native.colors.length, official.colors.length);
  for (let index = 0; index < length; index += 1) {
    if (!native.mask[index] && !official.mask[index]) continue;
    const nativeOffset = index * 3;
    difference +=
      Math.abs(native.colors[nativeOffset] - official.colors[nativeOffset]) +
      Math.abs(native.colors[nativeOffset + 1] - official.colors[nativeOffset + 1]) +
      Math.abs(native.colors[nativeOffset + 2] - official.colors[nativeOffset + 2]);
    count += 3;
  }
  return count === 0 ? 0 : difference / count / 255;
}

function tolerantForegroundColorError(native, official, radius) {
  const nativeResult = directionalForegroundColorError(
    native,
    official,
    radius,
  );
  const officialResult = directionalForegroundColorError(
    official,
    native,
    radius,
  );
  const count = nativeResult.count + officialResult.count;
  return count === 0
    ? 0
    : (nativeResult.difference + officialResult.difference) /
      count /
      3 /
      255;
}

function directionalForegroundColorError(source, target, radius) {
  let difference = 0;
  let count = 0;
  for (let index = 0; index < source.mask.length; index += 1) {
    if (!source.mask[index]) continue;
    const x = index % source.gridSize;
    const y = Math.floor(index / source.gridSize);
    let minimumDifference = Number.POSITIVE_INFINITY;
    for (let dy = -radius; dy <= radius; dy += 1) {
      for (let dx = -radius; dx <= radius; dx += 1) {
        const candidateX = x + dx;
        const candidateY = y + dy;
        if (
          candidateX < 0 ||
          candidateX >= target.gridSize ||
          candidateY < 0 ||
          candidateY >= target.gridSize
        ) {
          continue;
        }
        const candidate = candidateY * target.gridSize + candidateX;
        if (!target.mask[candidate]) continue;
        minimumDifference = Math.min(
          minimumDifference,
          pixelColorDifference(source.colors, index, target.colors, candidate),
        );
      }
    }
    if (!Number.isFinite(minimumDifference)) continue;
    difference += minimumDifference;
    count += 1;
  }
  return { difference, count };
}

function pixelColorDifference(left, leftIndex, right, rightIndex) {
  const leftOffset = leftIndex * 3;
  const rightOffset = rightIndex * 3;
  return (
    Math.abs(left[leftOffset] - right[rightOffset]) +
    Math.abs(left[leftOffset + 1] - right[rightOffset + 1]) +
    Math.abs(left[leftOffset + 2] - right[rightOffset + 2])
  );
}

function percentile(values, fraction) {
  if (values.length === 0) return 0;
  const sorted = [...values].sort((left, right) => left - right);
  return sorted[Math.min(
    sorted.length - 1,
    Math.floor((sorted.length - 1) * fraction),
  )];
}

function maximum(values) {
  return values.length === 0 ? 0 : Math.max(...values);
}

function ratio(numerator, denominator) {
  return denominator === 0 ? Number.POSITIVE_INFINITY : numerator / denominator;
}

function positiveFinite(value) {
  return finite(value) && value > 0;
}

function finite(value) {
  return typeof value === 'number' && Number.isFinite(value);
}

function normalizeText(value) {
  return value.replace(/\s+/g, ' ').trim();
}

function normalizeManifestText(element) {
  const segments = Array.isArray(element?.textSegments)
    ? element.textSegments.filter((segment) => typeof segment === 'string')
    : [];
  return normalizeText(
    segments.length > 0 ? segments.join(' ') : (element?.text ?? ''),
  );
}

function compactTextSequence(entries) {
  return entries
    .map((entry) => entry.text)
    .join('')
    .replace(/\s+/g, '');
}

function round(value, digits) {
  const scale = 10 ** digits;
  return Math.round(value * scale) / scale;
}

function format(value) {
  return Number.isFinite(value) ? value.toFixed(3) : String(value);
}
