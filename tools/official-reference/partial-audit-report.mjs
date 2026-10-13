export function replaceCasesForKind(previousCases, currentCases, kind) {
  requireCaseArray(previousCases, 'previousCases');
  requireCaseArray(currentCases, 'currentCases');
  if (currentCases.some((entry) => entry.kind !== kind)) {
    throw new Error(`Partial audit for ${kind} contains another diagram kind`);
  }
  requireUniqueCaseIds(currentCases, `partial ${kind} audit`);

  const merged = [];
  let inserted = false;
  for (const entry of previousCases) {
    if (entry.kind !== kind) {
      merged.push(entry);
      continue;
    }
    if (!inserted) {
      merged.push(...currentCases);
      inserted = true;
    }
  }
  if (!inserted) {
    merged.push(...currentCases);
  }
  requireUniqueCaseIds(merged, 'merged audit');
  return merged;
}

export function replaceMessagesForCaseIds(
  previousMessages,
  currentMessages,
  replacedCaseIds,
) {
  if (!Array.isArray(previousMessages) || !Array.isArray(currentMessages)) {
    throw new Error('Audit findings must be arrays');
  }
  return [
    ...previousMessages.filter(
      (message) => !replacedCaseIds.has(caseIdFromMessage(message)),
    ),
    ...currentMessages,
  ];
}

export function mergedInputDirectoryMetadata(
  previousReport,
  currentReport,
  mergedCases,
  replacedKind,
) {
  const directoriesByKind = new Map();
  const previousDirectories = previousReport.inputDirectoriesByKind ?? {};
  for (const entry of previousReport.cases) {
    const directory =
      previousDirectories[entry.kind] ?? previousReport.inputDirectory;
    if (typeof directory !== 'string' || directory.length === 0) {
      throw new Error(`Missing input directory for ${entry.kind}`);
    }
    directoriesByKind.set(entry.kind, directory);
  }
  if (
    typeof currentReport.inputDirectory !== 'string' ||
    currentReport.inputDirectory.length === 0
  ) {
    throw new Error(`Missing current input directory for ${replacedKind}`);
  }
  directoriesByKind.set(replacedKind, currentReport.inputDirectory);

  const orderedEntries = [];
  const emittedKinds = new Set();
  for (const entry of mergedCases) {
    if (emittedKinds.has(entry.kind)) continue;
    const directory = directoriesByKind.get(entry.kind);
    if (directory === undefined) {
      throw new Error(`Missing merged input directory for ${entry.kind}`);
    }
    orderedEntries.push([entry.kind, directory]);
    emittedKinds.add(entry.kind);
  }
  const uniqueDirectories = new Set(
    orderedEntries.map(([, directory]) => directory),
  );
  return {
    inputDirectory:
      uniqueDirectories.size === 1 ? orderedEntries[0][1] : null,
    inputDirectoriesByKind: Object.fromEntries(orderedEntries),
  };
}

function caseIdFromMessage(message) {
  if (typeof message !== 'string') {
    throw new Error('Audit finding must be a string');
  }
  return message.split('/', 1)[0];
}

function requireCaseArray(value, label) {
  if (!Array.isArray(value)) {
    throw new Error(`${label} must be an array`);
  }
  for (const entry of value) {
    if (
      entry === null ||
      typeof entry !== 'object' ||
      typeof entry.id !== 'string' ||
      typeof entry.kind !== 'string'
    ) {
      throw new Error(`${label} contains an invalid audit case`);
    }
  }
}

function requireUniqueCaseIds(cases, label) {
  const ids = new Set();
  for (const entry of cases) {
    if (ids.has(entry.id)) {
      throw new Error(`${label} contains duplicate case ID ${entry.id}`);
    }
    ids.add(entry.id);
  }
}
