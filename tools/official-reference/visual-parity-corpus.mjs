import { createHash } from 'node:crypto';
import { cases as productionCases } from './production-corpus.mjs';

export const kinds = [
  'flowchart',
  'swimlanes',
  'architecture',
  'c4',
  'xychart',
  'quadrant',
  'timeline',
  'kanban',
  'sequence',
  'class',
  'state',
  'er',
  'gantt',
  'pie',
  'journey',
  'requirement',
  'gitgraph',
  'mindmap',
  'packet',
  'radar',
  'sankey',
  'treemap',
  'venn',
  'ishikawa',
  'cynefin',
  'block',
  'eventmodeling',
  'agentflow',
];

export const casesPerKind = 256;

export const labelProfiles = [
  'A',
  'Short Label',
  'Compact Review Label',
  'Regional Validation Label',
  'Cross Region Validation Label',
  'Primary Production Evidence Label',
  'Secondary Production Evidence Label',
  'Fallback And Recovery Evidence Label',
  'Authorization And Policy Evaluation Label',
  'Long Running Background Verification Label',
  'Multi Stage Delivery Coordination Evidence Label',
  'Customer Facing Result Confirmation Evidence Label',
  'Asynchronous Replication Completion Evidence Label',
  'Privacy Preserving Data Processing Evidence Label',
  'Operational Readiness And Rollback Evidence Label',
  'Globally Distributed Service Coordination Evidence Label',
  'Deterministic Rendering Verification Evidence Label',
  'Resource Budget And Performance Validation Evidence Label',
  'Accessibility Metadata And Text Measurement Evidence Label',
  'End To End Production Compatibility Verification Evidence Label',
];

export const cases = kinds.flatMap((kind) => buildKindCases(kind));

validateCorpus();

function buildKindCases(kind) {
  const seeds = productionCases.filter((entry) => entry.kind === kind);
  if (seeds.length < 13) {
    throw new Error(`Expected at least 13 ${kind} production seeds, found ${seeds.length}`);
  }
  return Array.from({ length: casesPerKind }, (_, index) => {
    const seed = seeds[index % seeds.length];
    const profileIndex = Math.floor(index / seeds.length);
    const profileLabel = labelProfiles[profileIndex];
    if (profileLabel === undefined) {
      throw new Error(`Missing profile ${profileIndex} for ${kind}`);
    }
    const ordinal = index + 1;
    const evidenceId = `ParityEvidence${String(ordinal).padStart(3, '0')}`;
    const visibleLabel = `${profileLabel} ${String(ordinal).padStart(3, '0')}`;
    const source = addVisibleVariation(
      kind,
      seed.source,
      evidenceId,
      visibleLabel,
      ordinal,
    );
    return {
      id: `parity_${kind}_${String(ordinal).padStart(3, '0')}`,
      kind,
      title: `${seed.title} - profile ${String(profileIndex + 1).padStart(2, '0')}`,
      scenario:
        `Structural seed ${seed.id}; visible label profile ${profileIndex + 1}; ` +
        `matrix case ${ordinal} of ${casesPerKind}.`,
      layout: seed.layout ?? 'dagre',
      aspectRatio: seed.aspectRatio,
      features: [
        ...(seed.features ?? []),
        `seed:${seed.id}`,
        `label-profile:${profileIndex + 1}`,
      ],
      expectedTexts: [...(seed.expectedTexts ?? []), visibleLabel],
      seedId: seed.id,
      profileId: `label-${String(profileIndex + 1).padStart(2, '0')}`,
      source,
      sourceSha256: sha256(source),
    };
  });
}

function addVisibleVariation(kind, source, evidenceId, label, ordinal) {
  switch (kind) {
    case 'flowchart':
      return appendFlowchartEvidence(source, evidenceId, label);
    case 'swimlanes':
      return appendFlowchartEvidence(source, evidenceId, label);
    case 'architecture':
      return replaceArchitectureServiceIcon(source, label);
    case 'c4':
      return `${source.trimEnd()}
  Person(${evidenceId}, "${escapeQuotedLabel(label)}", "Visual parity evidence")
`;
    case 'xychart':
      return replaceOrInsertTitle(source, 'xychart', label);
    case 'quadrant': {
      const x = ((ordinal % 8) + 1) / 10;
      const y = (((ordinal * 3) % 8) + 1) / 10;
      return `${source.trimEnd()}\n  "${label}": [${x.toFixed(1)}, ${y.toFixed(1)}]\n`;
    }
    case 'timeline':
      return `${source.trimEnd()}\n  ${label} : Evidence ${String(ordinal).padStart(3, '0')}\n`;
    case 'kanban':
      return `${source.trimEnd()}
  ${evidenceId}Stage[Parity stage ${String(ordinal).padStart(3, '0')}]
    ${evidenceId}[${label}]
`;
    case 'sequence':
      return source.replace(
        /^(\s*sequenceDiagram\s*)$/m,
        `$1\n  participant ${evidenceId} as ${label}`,
      );
    case 'class':
      return appendClassEvidence(source, evidenceId, label);
    case 'state':
      return `${source.trimEnd()}\n  state "${label}" as ${evidenceId}\n`;
    case 'er':
      return `${source.trimEnd()}\n  ${evidenceId}["${label}"]\n`;
    case 'gantt':
      return replaceOrInsertTitle(source, 'gantt', label);
    case 'pie':
      return `${source.trimEnd()}\n  "${label}" : ${(ordinal % 17) + 3}\n`;
    case 'journey':
      return `${source.trimEnd()}\n  ${label}: ${(ordinal % 5) + 1}: Parity Actor ${ordinal}\n`;
    case 'requirement':
      return `${source.trimEnd()}
  requirement ${evidenceId} {
    id: "PARITY-${String(ordinal).padStart(3, '0')}"
    text: "${label}"
    risk: low
    verifyMethod: inspection
  }
`;
    case 'gitgraph':
      return `${source.trimEnd()}
  commit id: "${evidenceId}" tag: "${label}"
`;
    case 'mindmap':
      return `${source.trimEnd()}
    ${evidenceId}["${label}"]
`;
    case 'packet':
      return `${source.trimEnd()}
  +1: "${escapeQuotedLabel(label)}"
`;
    case 'radar':
      return replaceOrInsertTitle(source, 'radar-beta', label);
    case 'sankey':
      return `${source.trimEnd()}\n"${label}",${evidenceId},${(ordinal % 17) + 3}\n`;
    case 'treemap':
      return replaceOrInsertTreemapTitle(source, label);
    case 'venn':
      return replaceOrInsertVennTitle(source, label);
    case 'ishikawa':
      return `${source.trimEnd()}\n${label}\n`;
    case 'cynefin':
      return insertCynefinEvidence(source, label);
    case 'block':
      return `${source.trimEnd()}
  ${evidenceId}[("${escapeQuotedLabel(label)}")]
`;
    case 'eventmodeling':
      return `${source.trimEnd()}
  rf ${700 + ordinal} evt ${evidenceId} { label: ${label} }
`;
    case 'agentflow':
      return appendAgentflowEvidence(source, evidenceId, label);
    default:
      throw new Error(`Unsupported visual parity kind: ${kind}`);
  }
}

function appendAgentflowEvidence(source, evidenceId, label) {
  const anchorId = findFirstDiagramIdentifier(source, ['[', '@', '-']);
  return `${source.trimEnd()}
  ${evidenceId}["${escapeQuotedLabel(label)}"]@{ shape: refdoc }
  ${anchorId} -.- ${evidenceId}
`;
}

function replaceArchitectureServiceIcon(source, label) {
  const serviceIconPattern = /^(\s*service\s+[A-Za-z_][\w-]*)\([^)]+\)/m;
  const result = source.replace(
    serviceIconPattern,
    `$1 "${escapeQuotedLabel(label)}"`,
  );
  if (result === source) {
    throw new Error('Missing built-in Architecture service icon for visual evidence');
  }
  return result;
}

function insertCynefinEvidence(source, label) {
  const lines = source.trimEnd().split(/\r?\n/);
  let domainIndex = -1;
  lines.forEach((line, index) => {
    if (/^\s*(?:complex|complicated|clear|chaotic)\s*$/.test(line)) {
      domainIndex = index;
    }
  });
  const escapedLabel = escapeQuotedLabel(label);
  if (domainIndex >= 0) {
    const indent = lines[domainIndex].match(/^\s*/)[0];
    lines.splice(domainIndex + 1, 0, `${indent}  "${escapedLabel}"`);
    return `${lines.join('\n')}\n`;
  }

  const confusionLine = lines.find((line) => /^\s*confusion\s*$/.test(line));
  if (confusionLine == null) {
    throw new Error('Missing domain while adding Cynefin evidence');
  }
  const indent = confusionLine.match(/^\s*/)[0];
  lines.push(`${indent}complex`, `${indent}  "${escapedLabel}"`);
  return `${lines.join('\n')}\n`;
}

function appendFlowchartEvidence(source, evidenceId, label) {
  const anchorId = findFirstDiagramIdentifier(source, [
    '[',
    '(',
    '{',
    '@',
    '-',
    'o-',
    'x-',
    '<',
    '=',
  ]);
  return `${source.trimEnd()}
  ${anchorId} -.-> ${evidenceId}["${escapeQuotedLabel(label)}"]
`;
}

function appendClassEvidence(source, evidenceId, label) {
  const classPattern = /^\s*class\s+([A-Za-z_][A-Za-z0-9_-]*)/m;
  const anchorId = source.match(classPattern)?.[1];
  if (anchorId === undefined) {
    throw new Error('Missing class declaration while adding visual parity evidence');
  }
  return `${source.trimEnd()}
  class ${evidenceId}["${escapeQuotedLabel(label)}"]
  ${anchorId} ..> ${evidenceId} : parity
`;
}

function findFirstDiagramIdentifier(source, expectedRestPrefixes) {
  for (const line of source.split(/\r?\n/)) {
    const match = line.match(/^\s*([A-Za-z_][A-Za-z0-9_-]*)\s*(.*)$/);
    if (match === null) {
      continue;
    }
    const [, identifier, rest] = match;
    if (expectedRestPrefixes.some((prefix) => rest.startsWith(prefix))) {
      return identifier;
    }
  }
  throw new Error('Missing diagram identifier while adding visual parity evidence');
}

function escapeQuotedLabel(label) {
  return label.replaceAll('\\', '\\\\').replaceAll('"', '\\"');
}

function replaceOrInsertTitle(source, declaration, suffix) {
  const titlePattern = /^(\s*title\s+)(?:"([^"]*)"|(.+))$/m;
  if (titlePattern.test(source)) {
    return source.replace(titlePattern, (_, prefix, quoted, plain) => {
      const title = (quoted ?? plain).trim();
      return `${prefix}"${title} - ${suffix}"`;
    });
  }
  const declarationPattern = new RegExp(`^(\\s*${declaration}(?:\\s+horizontal)?\\s*)$`, 'm');
  if (!declarationPattern.test(source)) {
    throw new Error(`Missing ${declaration} declaration while adding title`);
  }
  return source.replace(
    declarationPattern,
    `$1\n  title "${suffix}"`,
  );
}

function replaceOrInsertTreemapTitle(source, suffix) {
  const sourceTitlePattern = /^(\s*title\s+)(?:"([^"]*)"|(.+))$/m;
  if (sourceTitlePattern.test(source)) {
    return source.replace(sourceTitlePattern, (_, prefix, quoted, plain) => {
      const title = (quoted ?? plain).trim();
      return `${prefix}"${title} - ${suffix}"`;
    });
  }
  const frontmatterTitlePattern = /^(\s*title:\s*)(?:"([^"]*)"|'([^']*)'|(.+))$/m;
  if (frontmatterTitlePattern.test(source)) {
    return source.replace(
      frontmatterTitlePattern,
      (_, prefix, doubleQuoted, singleQuoted, plain) => {
        const title = (doubleQuoted ?? singleQuoted ?? plain).trim();
        return `${prefix}"${title} - ${suffix}"`;
      },
    );
  }
  const declarationPattern = /^(\s*treemap(?:-beta)?\s*)$/m;
  if (!declarationPattern.test(source)) {
    throw new Error('Missing treemap declaration while adding title');
  }
  return source.replace(
    declarationPattern,
    `$1\n  title "${suffix}"`,
  );
}

function replaceOrInsertVennTitle(source, suffix) {
  const sourceTitlePattern = /^(\s*title\s+)(?:"([^"]*)"|(.+))$/m;
  if (sourceTitlePattern.test(source)) {
    return source.replace(sourceTitlePattern, (_, prefix, quoted, plain) => {
      const title = (quoted ?? plain).trim();
      return `${prefix}${title} - ${suffix}`;
    });
  }
  const frontmatterTitlePattern = /^(\s*title:\s*)(?:"([^"]*)"|'([^']*)'|(.+))$/m;
  if (frontmatterTitlePattern.test(source)) {
    return source.replace(
      frontmatterTitlePattern,
      (_, prefix, doubleQuoted, singleQuoted, plain) => {
        const title = (doubleQuoted ?? singleQuoted ?? plain).trim();
        return `${prefix}"${title} - ${suffix}"`;
      },
    );
  }
  const declarationPattern = /^(\s*venn-beta\s*)$/m;
  if (!declarationPattern.test(source)) {
    throw new Error('Missing venn-beta declaration while adding title');
  }
  return source.replace(
    declarationPattern,
    `$1\n  title ${suffix}`,
  );
}

function validateCorpus() {
  const ids = new Set();
  const sources = new Set();
  if (cases.length !== kinds.length * casesPerKind) {
    throw new Error(`Expected ${kinds.length * casesPerKind} cases, found ${cases.length}`);
  }
  for (const kind of kinds) {
    const kindCases = cases.filter((entry) => entry.kind === kind);
    if (kindCases.length !== casesPerKind) {
      throw new Error(`Expected ${casesPerKind} ${kind} cases, found ${kindCases.length}`);
    }
    const seedCount = new Set(kindCases.map((entry) => entry.seedId)).size;
    if (seedCount < 13) {
      throw new Error(`${kind} matrix only has ${seedCount} structural seeds`);
    }
  }
  for (const entry of cases) {
    if (!/^parity_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid visual parity id: ${entry.id}`);
    }
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate visual parity id: ${entry.id}`);
    }
    if (sources.has(entry.source)) {
      throw new Error(`Duplicate visual parity source: ${entry.id}`);
    }
    if (!entry.source.includes(entry.expectedTexts.at(-1))) {
      throw new Error(`${entry.id} is missing its visible profile label`);
    }
    ids.add(entry.id);
    sources.add(entry.source);
  }
}

function sha256(value) {
  return createHash('sha256').update(value).digest('hex');
}
