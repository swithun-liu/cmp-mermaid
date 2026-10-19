export function invalidSourceCaseMatchesScope(entry, scope) {
  if (scope === 'full') {
    return true;
  }
  if (scope !== 'smoke') {
    throw new Error('Invalid-source scope must be full or smoke');
  }
  return entry.mutationVariant === 1 ||
    (
      entry.mutationVariant === undefined &&
      (entry.profileIndex === 1 || entry.caseIndex === 1)
    );
}
