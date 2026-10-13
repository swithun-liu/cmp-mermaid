export function classifyGeometryRatio({
  label,
  value,
  minimum,
  maximum,
  iterativeLayoutWithMatchingStructure,
  reviewReason = null,
}) {
  if (Number.isFinite(value) && value >= minimum && value <= maximum) {
    return { status: 'pass', message: null };
  }
  const message =
    `${label} ratio ${formatRatio(value)} is outside ` +
    `[${minimum.toFixed(3)}, ${maximum.toFixed(3)}]`;
  const effectiveReviewReason = reviewReason ??
    (iterativeLayoutWithMatchingStructure
      ? 'CoSE manifest structure matches'
      : null);
  return effectiveReviewReason !== null
    ? {
        status: 'review',
        message: `${message}; ${effectiveReviewReason}`,
      }
    : { status: 'failure', message };
}

function formatRatio(value) {
  return Number.isFinite(value) ? value.toFixed(3) : String(value);
}
