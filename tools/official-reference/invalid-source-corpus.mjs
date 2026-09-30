import { cases as visualParityCases, kinds } from './visual-parity-corpus.mjs';

const kindTitles = Object.freeze({
  flowchart: 'Flowchart',
  swimlanes: 'Swimlanes',
  architecture: 'Architecture',
  c4: 'C4',
  railroad: 'Railroad',
  treeview: 'TreeView',
  xychart: 'XY Chart',
  quadrant: 'Quadrant Chart',
  timeline: 'Timeline',
  kanban: 'Kanban',
  sequence: 'Sequence',
  class: 'Class',
  state: 'State',
  er: 'Entity Relationship',
  gantt: 'Gantt',
  pie: 'Pie',
  journey: 'User Journey',
  requirement: 'Requirement',
  gitgraph: 'Git Graph',
  mindmap: 'Mindmap',
  packet: 'Packet',
  radar: 'Radar',
  sankey: 'Sankey',
  treemap: 'Treemap',
  venn: 'Venn',
  ishikawa: 'Ishikawa',
  cynefin: 'Cynefin',
  block: 'Block',
  eventmodeling: 'Event Modeling',
  agentflow: 'Agentflow',
  usecase: 'Use Case',
  wardley: 'Wardley Map',
  zenuml: 'ZenUML',
});

export const casesPerKind = 256;
export const casesPerMutation = 16;

/**
 * These mutations model common ways an AI response can stop being canonical
 * Mermaid source. Mermaid.js remains the oracle: a mutation may either be
 * rejected or tolerated, and Native must produce the same outcome.
 *
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/mermaid.ts -> parse
 * packages/mermaid/src/mermaidAPI.ts -> render
 */
export const mutationProfiles = Object.freeze([
  mutation(
    'diagram-keyword-typo',
    'declaration',
    'Diagram declaration contains an inserted character.',
    mutateDiagramKeyword,
  ),
  mutation(
    'markdown-fence-wrapper',
    'response-wrapper',
    'Raw Markdown code fences are passed through with the diagram.',
    (source) => `\`\`\`mermaid\n${source.trimEnd()}\n\`\`\``,
  ),
  mutation(
    'leading-explanation',
    'response-wrapper',
    'Natural-language explanation appears before the diagram.',
    (source, variant) =>
      `Here is the requested Mermaid diagram ${variant + 1}:\n${source}`,
  ),
  mutation(
    'trailing-explanation',
    'response-wrapper',
    'Natural-language explanation appears after the diagram.',
    (source, variant) =>
      `${source.trimEnd()}\nThis diagram shows generated result ${variant + 1}.`,
  ),
  mutation(
    'duplicate-declaration',
    'structure',
    'The diagram declaration is repeated inside the body.',
    duplicateDeclaration,
  ),
  mutation(
    'foreign-diagram-fragment',
    'mixed-syntax',
    'A fragment from another Mermaid grammar is appended.',
    appendForeignDiagramFragment,
  ),
  mutation(
    'truncated-line',
    'truncation',
    'Generation stops partway through a body line.',
    truncateInsideBodyLine,
  ),
  mutation(
    'truncated-document',
    'truncation',
    'Generation stops before the complete document is emitted.',
    truncateDocument,
  ),
  mutation(
    'missing-body-line',
    'omission',
    'One generated body line is omitted.',
    removeBodyLine,
  ),
  mutation(
    'merged-body-lines',
    'line-break',
    'Two adjacent generated lines lose their line break.',
    mergeBodyLines,
  ),
  mutation(
    'unclosed-quote',
    'delimiter',
    'One quote delimiter is omitted.',
    removeQuote,
  ),
  mutation(
    'missing-closing-delimiter',
    'delimiter',
    'One closing bracket, brace, or parenthesis is omitted.',
    removeClosingDelimiter,
  ),
  mutation(
    'mismatched-delimiter',
    'delimiter',
    'One closing delimiter is replaced by a different delimiter.',
    mismatchClosingDelimiter,
  ),
  mutation(
    'missing-separator',
    'operator',
    'One relationship, field, or list separator is omitted.',
    removeSeparator,
  ),
  mutation(
    'fullwidth-punctuation',
    'character-substitution',
    'ASCII syntax punctuation is replaced by a fullwidth character.',
    replaceWithFullwidthPunctuation,
  ),
  mutation(
    'split-body-keyword',
    'tokenization',
    'Whitespace is inserted into a body keyword or identifier.',
    splitBodyKeyword,
  ),
]);

export const cases = Object.freeze(
  kinds.flatMap((kind) => {
    const baseCases = visualParityCases.filter((entry) => entry.kind === kind);
    if (baseCases.length !== casesPerKind) {
      throw new Error(
        `Expected ${casesPerKind} ${kind} base cases, found ${baseCases.length}`,
      );
    }
    return baseCases.map((baseCase, caseOffset) => {
      const caseIndex = caseOffset + 1;
      const mutationIndex = Math.floor(caseOffset / casesPerMutation);
      const mutationVariant = caseOffset % casesPerMutation;
      const profile = mutationProfiles[mutationIndex];
      const normalizedBaseSource = normalizeMermaidSource(baseCase.source);
      const source = profile.apply(normalizedBaseSource, mutationVariant);
      if (source === normalizedBaseSource) {
        throw new Error(
          `${kind}/${profile.id}/${mutationVariant + 1} did not change ` +
            baseCase.id,
        );
      }
      return Object.freeze({
        id: `invalid_${kind}_${String(caseIndex).padStart(3, '0')}`,
        baseCaseId: baseCase.id,
        mutationId: profile.id,
        mutationCategory: profile.category,
        mutationIndex: mutationIndex + 1,
        mutationVariant: mutationVariant + 1,
        caseIndex,
        kind,
        diagramTitle: kindTitles[kind],
        title: `${kindTitles[kind]} ${profile.id} ${String(
          mutationVariant + 1,
        ).padStart(2, '0')}`,
        scenario: profile.scenario,
        source,
        sourceFingerprint: sourceFingerprint(source),
        layout: baseCase.layout ?? 'dagre',
        aspectRatio: 4 / 3,
        expectedTexts: Object.freeze([]),
        features: Object.freeze([
          'ai-like-source-mutation',
          `mutation:${profile.id}`,
          `base:${baseCase.id}`,
          'official-outcome-oracle',
          'no-crash',
        ]),
        expectedOutcome: 'oracle',
        expectedNativeErrorType: 'CONTENT_ERROR',
      });
    });
  }),
);

validateCases();

function mutation(id, category, scenario, apply) {
  return Object.freeze({ id, category, scenario, apply });
}

function mutateDiagramKeyword(source, variant) {
  const lines = source.split('\n');
  const index = declarationLineIndex(lines);
  const match = lines[index].match(/^(\s*)(\S+)(.*)$/);
  if (match === null) {
    return `${source}x`;
  }
  const token = match[2];
  const insertionIndex = Math.min(
    Math.max(1, variant % Math.max(1, token.length - 1)),
    token.length - 1,
  );
  lines[index] =
    match[1] +
    token.slice(0, insertionIndex) +
    'x' +
    token.slice(insertionIndex) +
    match[3];
  return lines.join('\n');
}

function duplicateDeclaration(source) {
  const lines = source.split('\n');
  const index = declarationLineIndex(lines);
  lines.splice(index + 1, 0, lines[index]);
  return lines.join('\n');
}

function appendForeignDiagramFragment(source, variant) {
  const fragments = [
    'sequenceDiagram\n  Alice->>Bob: incomplete',
    'classDiagram\n  class Broken {',
    'erDiagram\n  A ||--o{',
    'gantt\n  section',
    'pie\n  "Broken" : nope',
  ];
  return `${source.trimEnd()}\n${fragments[variant % fragments.length]}`;
}

function truncateInsideBodyLine(source, variant) {
  const lines = source.split('\n');
  const index = pick(bodyLineIndices(lines), variant);
  if (index === null) {
    return source.slice(0, Math.max(1, Math.floor(source.length / 2)));
  }
  const line = lines[index];
  const cut = line.length <= 1
    ? 0
    : Math.min(
        line.length - 1,
        Math.max(1, Math.floor(line.length * ((variant % 5) + 2) / 7)),
      );
  return [...lines.slice(0, index), line.slice(0, cut)].join('\n');
}

function truncateDocument(source, variant) {
  const removedCharacterCount = Math.min(source.length - 1, variant + 1);
  return source.slice(0, source.length - removedCharacterCount);
}

function removeBodyLine(source, variant) {
  const lines = source.split('\n');
  const index = pick(bodyLineIndices(lines), variant);
  if (index === null) {
    return source.slice(0, -1);
  }
  lines.splice(index, 1);
  return lines.join('\n');
}

function mergeBodyLines(source, variant) {
  const lines = source.split('\n');
  const candidates = bodyLineIndices(lines).filter(
    (index) => index + 1 < lines.length,
  );
  const index = pick(candidates, variant);
  if (index === null) {
    return source.replace('\n', '');
  }
  lines.splice(
    index,
    2,
    `${lines[index].trimEnd()} ${lines[index + 1].trimStart()}`,
  );
  return lines.join('\n');
}

function removeQuote(source, variant) {
  const matches = [...source.matchAll(/["']/g)];
  const match = pick(matches, variant);
  return match === null
    ? `${source}\n"unterminated`
    : replaceAt(source, match.index, 1, '');
}

function removeClosingDelimiter(source, variant) {
  const matches = [...source.matchAll(/[\]})]/g)];
  const match = pick(matches, variant);
  return match === null
    ? `${source}\nBroken[`
    : replaceAt(source, match.index, 1, '');
}

function mismatchClosingDelimiter(source, variant) {
  const matches = [...source.matchAll(/[\]})]/g)];
  const match = pick(matches, variant);
  if (match === null) {
    return `${source}\nBroken[}`;
  }
  const replacement = { ']': '}', '}': ')', ')': ']' }[match[0]];
  return replaceAt(source, match.index, 1, replacement);
}

function removeSeparator(source, variant) {
  const patterns = [/--?>/g, /:/g, /,/g, /=|<-/g, /\.\.>/g];
  for (const pattern of patterns) {
    const match = pick([...source.matchAll(pattern)], variant);
    if (match !== null) {
      return replaceAt(source, match.index, match[0].length, ' ');
    }
  }
  return `${source}\nBroken separator`;
}

function replaceWithFullwidthPunctuation(source, variant) {
  const matches = [...source.matchAll(/[:,>\-[\]]/g)];
  const match = pick(matches, variant);
  if (match === null) {
    return `${source}?`;
  }
  const replacement = {
    ':': '\uff1a',
    ',': '\uff0c',
    '>': '\uff1e',
    '-': '\uff0d',
    '[': '\uff3b',
    ']': '\uff3d',
  }[match[0]];
  return replaceAt(source, match.index, 1, replacement);
}

function splitBodyKeyword(source, variant) {
  const lines = source.split('\n');
  const index = pick(bodyLineIndices(lines), variant);
  const match = index === null
    ? null
    : lines[index].match(/^(\s*)([A-Za-z]{2,})(.*)$/);
  if (match === null) {
    return `${source}\ninvalid keyword`;
  }
  const splitIndex = Math.max(
    1,
    Math.min(match[2].length - 1, variant % match[2].length),
  );
  lines[index] =
    match[1] +
    match[2].slice(0, splitIndex) +
    ' ' +
    match[2].slice(splitIndex) +
    match[3];
  return lines.join('\n');
}

function declarationLineIndex(lines) {
  let index = 0;
  while (index < lines.length && lines[index].trim() === '') {
    index += 1;
  }
  if (lines[index]?.trim() === '---') {
    index += 1;
    while (index < lines.length && lines[index].trim() !== '---') {
      index += 1;
    }
    index += 1;
  }
  while (index < lines.length && lines[index].trim() === '') {
    index += 1;
  }
  return Math.min(index, lines.length - 1);
}

function bodyLineIndices(lines) {
  const declarationIndex = declarationLineIndex(lines);
  return lines
    .map((line, index) =>
      index > declarationIndex &&
      line.trim() !== '' &&
      !line.trim().startsWith('%%')
        ? index
        : -1,
    )
    .filter((index) => index >= 0);
}

function pick(values, variant) {
  return values.length === 0 ? null : values[variant % values.length];
}

function replaceAt(source, index, length, replacement) {
  return (
    source.slice(0, index) +
    replacement +
    source.slice(index + length)
  );
}

function normalizeMermaidSource(value) {
  const lines = value.trim().replaceAll('\r\n', '\n').split('\n');
  const indents = lines
    .filter((line) => line.trim().length > 0)
    .map((line) => line.match(/^\s*/)[0].length);
  const commonIndent = Math.min(...indents);
  return lines.map((line) => line.slice(commonIndent)).join('\n');
}

function sourceFingerprint(value) {
  let fingerprint = 0x811c9dc5;
  for (let index = 0; index < value.length; index += 1) {
    fingerprint ^= value.charCodeAt(index);
    fingerprint = Math.imul(fingerprint, 0x01000193);
  }
  return fingerprint | 0;
}

function validateCases() {
  if (mutationProfiles.length * casesPerMutation !== casesPerKind) {
    throw new Error(
      `${mutationProfiles.length} mutations x ${casesPerMutation} cases ` +
        `does not equal ${casesPerKind}`,
    );
  }
  const ids = new Set();
  const sources = new Set();
  for (const kind of kinds) {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    if (kindCases.length !== casesPerKind) {
      throw new Error(
        `Expected ${casesPerKind} ${kind} mutation cases, found ${kindCases.length}`,
      );
    }
    if (new Set(kindCases.map((entry) => entry.baseCaseId)).size !== casesPerKind) {
      throw new Error(`${kind} does not use ${casesPerKind} unique base cases`);
    }
    for (const profile of mutationProfiles) {
      const count = kindCases.filter(
        (entry) => entry.mutationId === profile.id,
      ).length;
      if (count !== casesPerMutation) {
        throw new Error(
          `Expected ${casesPerMutation} ${kind}/${profile.id} cases, found ${count}`,
        );
      }
    }
  }
  for (const entry of cases) {
    if (!/^invalid_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid AI-mutation case id: ${entry.id}`);
    }
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate AI-mutation case id: ${entry.id}`);
    }
    if (entry.source.trim().length === 0) {
      throw new Error(`Empty AI-mutation source for ${entry.id}`);
    }
    if (sources.has(entry.source)) {
      throw new Error(`Duplicate AI-mutation source for ${entry.id}`);
    }
    if (
      entry.expectedOutcome !== 'oracle' ||
      entry.expectedNativeErrorType !== 'CONTENT_ERROR'
    ) {
      throw new Error(`Invalid expected outcome metadata for ${entry.id}`);
    }
    ids.add(entry.id);
    sources.add(entry.source);
  }
  const expectedCaseCount = kinds.length * casesPerKind;
  if (cases.length !== expectedCaseCount) {
    throw new Error(
      `Expected ${expectedCaseCount} AI-mutation cases, found ${cases.length}`,
    );
  }
}
