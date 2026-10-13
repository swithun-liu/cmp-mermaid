const iterativePlacementFindingCodes = new Set([
  'paint-order-occlusion',
]);
const textPresenceFindingCodes = new Set(['text-presence']);

export function classifyDetailFindings(findings, auditCase, evidence = {}) {
  const hardFindings = findings.filter((finding) =>
    finding.severity === 'error'
  );
  const iterativePlacementOnly =
    auditCase.layout === 'cose-bilkent' &&
    hardFindings.length > 0 &&
    hardFindings.every((finding) =>
      iterativePlacementFindingCodes.has(finding.code)
    );
  const densePieFontMetricsOnly =
    auditCase.kind === 'pie' &&
    hardFindings.length > 0 &&
    hardFindings.every((finding) =>
      iterativePlacementFindingCodes.has(finding.code)
    ) &&
    hasEquivalentDensePieEvidence(evidence.manifest, evidence.raster);
  const treemapTruncationMetricsOnly =
    auditCase.kind === 'treemap' &&
    hardFindings.length > 0 &&
    hardFindings.every((finding) =>
      textPresenceFindingCodes.has(finding.code)
    ) &&
    hasEquivalentTreemapTruncationEvidence(
      evidence.manifest,
      evidence.raster,
    );
  const cynefinFontBoundsOnly =
    auditCase.kind === 'cynefin' &&
    hardFindings.length > 0 &&
    hardFindings.every((finding) =>
      iterativePlacementFindingCodes.has(finding.code)
    ) &&
    hasEquivalentCynefinFontBoundsEvidence(
      evidence.manifest,
      evidence.raster,
    );
  if (
    !iterativePlacementOnly &&
    !densePieFontMetricsOnly &&
    !treemapTruncationMetricsOnly &&
    !cynefinFontBoundsOnly
  ) {
    return findings;
  }
  const policy = iterativePlacementOnly
    ? 'iterative-layout-placement'
    : densePieFontMetricsOnly
      ? 'dense-pie-font-metrics'
      : treemapTruncationMetricsOnly
        ? 'treemap-dom-font-metrics'
        : 'cynefin-font-bounds';
  const suffix = {
    'iterative-layout-placement':
      'CoSE manifest structure otherwise matches',
    'dense-pie-font-metrics':
      'Pie structure, text, color, and raster evidence otherwise match',
    'treemap-dom-font-metrics':
      'Treemap truncation differs only at the DOM/Compose font-metric boundary',
    'cynefin-font-bounds':
      'Cynefin text, geometry, style, and raster evidence otherwise match',
  }[policy];
  const reviewCodes = policy === 'treemap-dom-font-metrics'
    ? textPresenceFindingCodes
    : iterativePlacementFindingCodes;
  return findings.map((finding) =>
    finding.severity === 'error' &&
      reviewCodes.has(finding.code)
      ? {
          ...finding,
          severity: 'review',
          policy,
          message: `${finding.message}; ${suffix}`,
        }
      : finding
  );
}

function hasEquivalentTreemapTruncationEvidence(manifest, raster) {
  const text = manifest?.textComparison;
  const elements = manifest?.elementComparison?.ratios;
  return (
    raster?.status === 'pass' &&
    raster.maskIou >= 0.97 &&
    raster.edgeF1 >= 0.96 &&
    raster.colorError <= 0.02 &&
    hasEquivalentEllipsisDifferences(text) &&
    text?.multiplicityDifferences?.length === 0 &&
    text.maximumCenterDistance <= 0.03 &&
    matchedTextCountsIncludeDifferences(manifest) &&
    manifest?.native?.textCount === manifest?.official?.textCount &&
    manifest?.native?.clippedTextCount === 0 &&
    manifest?.official?.clippedTextCount === 0 &&
    elements?.text === 1 &&
    elements?.asset === 1 &&
    hasEquivalentStyleEvidence(manifest, false) &&
    hasNoIndependentOverlapDifferences(manifest)
  );
}

function hasEquivalentCynefinFontBoundsEvidence(manifest, raster) {
  const text = manifest?.textComparison;
  return (
    raster?.status === 'pass' &&
    raster.maskIou >= 0.97 &&
    raster.edgeF1 >= 0.98 &&
    raster.colorError <= 0.03 &&
    text?.absentInNative?.length === 0 &&
    text?.absentInOfficial?.length === 0 &&
    text?.multiplicityDifferences?.length === 0 &&
    text.maximumCenterDistance <= 0.005 &&
    text.matchedCount === manifest?.native?.textCount &&
    text.matchedCount === manifest?.official?.textCount &&
    manifest?.native?.clippedTextCount === 0 &&
    manifest?.official?.clippedTextCount === 0 &&
    allExactRatios(manifest?.elementComparison?.ratios) &&
    hasEquivalentStyleEvidence(manifest, true) &&
    hasNoIndependentOverlapDifferences(manifest)
  );
}

function hasEquivalentDensePieEvidence(manifest, raster) {
  const text = manifest?.textComparison;
  const elements = manifest?.elementComparison?.ratios;
  return (
    raster?.status === 'pass' &&
    raster.maskIou >= 0.98 &&
    raster.edgeF1 >= 0.98 &&
    raster.colorError <= 0.05 &&
    text?.absentInNative?.length === 0 &&
    text?.absentInOfficial?.length === 0 &&
    text?.multiplicityDifferences?.length === 0 &&
    text.maximumCenterDistance <= 0.02 &&
    text.matchedCount === manifest?.native?.textCount &&
    text.matchedCount === manifest?.official?.textCount &&
    manifest?.native?.clippedTextCount === 0 &&
    manifest?.official?.clippedTextCount === 0 &&
    allExactRatios(elements) &&
    hasEquivalentStyleEvidence(manifest, true) &&
    hasNoIndependentOverlapDifferences(manifest)
  );
}

function hasEquivalentEllipsisDifferences(text) {
  const officialOnly = expandTextDifferences(
    text?.absentInNative,
    'officialCount',
  );
  const nativeOnly = expandTextDifferences(
    text?.absentInOfficial,
    'nativeCount',
  );
  if (officialOnly.length === 0 || officialOnly.length !== nativeOnly.length) {
    return false;
  }
  const unmatchedOfficial = [...officialOnly];
  return nativeOnly.every((nativeText) => {
    const index = unmatchedOfficial.findIndex((officialText) =>
      equivalentEllipsis(nativeText, officialText)
    );
    if (index < 0) return false;
    unmatchedOfficial.splice(index, 1);
    return true;
  }) && unmatchedOfficial.length === 0;
}

function expandTextDifferences(entries, countKey) {
  return (entries ?? []).flatMap((entry) =>
    Array.from(
      { length: entry[countKey] ?? 0 },
      () => entry.text,
    )
  );
}

function equivalentEllipsis(left, right) {
  if (!left.endsWith('...') || !right.endsWith('...')) return false;
  const leftPrefix = left.slice(0, -3);
  const rightPrefix = right.slice(0, -3);
  return (
    Math.abs(leftPrefix.length - rightPrefix.length) <= 3 &&
    (
      leftPrefix.startsWith(rightPrefix) ||
      rightPrefix.startsWith(leftPrefix)
    )
  );
}

function matchedTextCountsIncludeDifferences(manifest) {
  const text = manifest?.textComparison;
  const nativeOnlyCount = sumDifferenceCount(
    text?.absentInOfficial,
    'nativeCount',
  );
  const officialOnlyCount = sumDifferenceCount(
    text?.absentInNative,
    'officialCount',
  );
  return (
    text?.matchedCount + nativeOnlyCount === manifest?.native?.textCount &&
    text?.matchedCount + officialOnlyCount === manifest?.official?.textCount
  );
}

function sumDifferenceCount(entries, countKey) {
  return (entries ?? []).reduce(
    (total, entry) => total + (entry[countKey] ?? 0),
    0,
  );
}

function hasEquivalentStyleEvidence(manifest, requireFullPaletteCoverage) {
  const styles = manifest?.styleComparison;
  const markers = manifest?.markerVisibilityComparison;
  const paletteCoverageMatches =
    styles?.nativePaletteCoverage === styles?.officialPaletteCoverage &&
    (
      !requireFullPaletteCoverage ||
      styles?.nativePaletteCoverage === 1
    );
  return (
    styles?.nativePaletteSize === styles?.officialPaletteSize &&
    paletteCoverageMatches &&
    sameRecord(
      styles?.nativeStrokePatterns,
      styles?.officialStrokePatterns,
    ) &&
    sameRecord(styles?.nativeMarkers, styles?.officialMarkers) &&
    markers?.differences?.length === 0
  );
}

function hasNoIndependentOverlapDifferences(manifest) {
  const overlaps = manifest?.overlapComparison;
  return (
    overlaps?.mismatches?.length === 0 &&
    overlaps?.ratioDifferences?.length === 0
  );
}

function allExactRatios(ratios) {
  return (
    ratios != null &&
    Object.values(ratios).length > 0 &&
    Object.values(ratios).every((value) => value === 1)
  );
}

function sameRecord(left, right) {
  if (left == null || right == null) return false;
  const leftEntries = Object.entries(left).sort(([leftKey], [rightKey]) =>
    leftKey.localeCompare(rightKey)
  );
  const rightEntries = Object.entries(right).sort(([leftKey], [rightKey]) =>
    leftKey.localeCompare(rightKey)
  );
  return JSON.stringify(leftEntries) === JSON.stringify(rightEntries);
}
