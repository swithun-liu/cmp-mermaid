export function classifyGeometryRatio({
  label,
  value,
  minimum,
  maximum,
  iterativeLayoutWithMatchingStructure,
}) {
  if (Number.isFinite(value) && value >= minimum && value <= maximum) {
    return { status: 'pass', message: null };
  }
  const message =
    `${label} ratio ${formatRatio(value)} is outside ` +
    `[${minimum.toFixed(3)}, ${maximum.toFixed(3)}]`;
  return iterativeLayoutWithMatchingStructure
    ? {
        status: 'review',
        message: `${message}; CoSE manifest structure matches`,
      }
    : { status: 'failure', message };
}

function formatRatio(value) {
  return Number.isFinite(value) ? value.toFixed(3) : String(value);
}
