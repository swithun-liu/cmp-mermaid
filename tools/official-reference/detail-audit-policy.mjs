const iterativePlacementFindingCodes = new Set([
  'paint-order-occlusion',
]);

export function classifyDetailFindings(findings, auditCase) {
  const hardFindings = findings.filter((finding) =>
    finding.severity === 'error'
  );
  const iterativePlacementOnly =
    auditCase.layout === 'cose-bilkent' &&
    hardFindings.length > 0 &&
    hardFindings.every((finding) =>
      iterativePlacementFindingCodes.has(finding.code)
    );
  if (!iterativePlacementOnly) {
    return findings;
  }
  return findings.map((finding) =>
    finding.severity === 'error' &&
      iterativePlacementFindingCodes.has(finding.code)
      ? {
          ...finding,
          severity: 'review',
          policy: 'iterative-layout-placement',
          message: `${finding.message}; CoSE manifest structure otherwise matches`,
        }
      : finding
  );
}
