import { mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { cases as flowchartDemoCases } from './cases.mjs';
import {
  cases as productionCases,
  conformanceCases,
  requiredFeaturesByKind,
} from './production-corpus.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const kotlinGalleryFiles = {
  xychart: ['XyChartDemos.kt', 'XyChartDemo'],
  sequence: ['SequenceDemos.kt', 'SequenceDemo'],
  class: ['ClassDemos.kt', 'ClassDemo'],
  state: ['StateDemos.kt', 'StateDemo'],
  er: ['ErDemos.kt', 'ErDemo'],
  gantt: ['GanttDemos.kt', 'GanttDemo'],
  pie: ['PieDemos.kt', 'PieDemo'],
};
const expectedKindCounts = new Map([
  ['flowchart', 6],
  ['xychart', 5],
  ['sequence', 6],
  ['class', 5],
  ['state', 5],
  ['er', 5],
  ['gantt', 5],
  ['pie', 5],
]);
const expectedProductionKindCounts = new Map([
  ['flowchart', 14],
  ['xychart', 13],
  ['sequence', 14],
  ['class', 13],
  ['state', 13],
  ['er', 13],
  ['gantt', 13],
  ['pie', 13],
]);
const supportedKinds = new Set([
  'flowchart',
  'xychart',
  'sequence',
  'class',
  'state',
  'er',
  'gantt',
  'pie',
]);

validateStabilityCases();
validateProductionCases();

const outputs = [
  {
    packageName: 'io.github.cmpmermaid.debugui.generated',
    path: resolve(
      repositoryRoot,
      'mermaid-debug-ui/src/commonMain/kotlin/io/github/cmpmermaid/debugui/generated/' +
        'StabilityCorpus.kt',
    ),
  },
  {
    packageName: 'io.github.cmpmermaid.core.generated',
    path: resolve(
      repositoryRoot,
      'mermaid-core/src/commonTest/kotlin/io/github/cmpmermaid/core/generated/' +
        'StabilityCorpus.kt',
    ),
  },
];

for (const output of outputs) {
  mkdirSync(dirname(output.path), { recursive: true });
  writeFileSync(output.path, renderKotlin(output.packageName));
  console.log(`Generated ${output.path}`);
}

function validateStabilityCases() {
  const ids = new Set();
  const sources = new Set();
  const kindCounts = new Map();
  for (const entry of stabilityCases) {
    if (!/^rc_[a-z0-9_]+$/.test(entry.id)) {
      throw new Error(`Invalid stability case id: ${entry.id}`);
    }
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate stability case id: ${entry.id}`);
    }
    if (!supportedKinds.has(entry.kind)) {
      throw new Error(`Unsupported diagram kind for ${entry.id}: ${entry.kind}`);
    }
    if (entry.source.trim().length === 0) {
      throw new Error(`Empty Mermaid source for ${entry.id}`);
    }
    if (entry.title.trim().length === 0 || entry.scenario.trim().length === 0) {
      throw new Error(`Missing real-world description for ${entry.id}`);
    }
    if (!Number.isFinite(entry.aspectRatio) || entry.aspectRatio <= 0) {
      throw new Error(`Invalid aspect ratio for ${entry.id}`);
    }
    const normalizedSource = normalizeMermaidSource(entry.source);
    if (sources.has(normalizedSource)) {
      throw new Error(`Duplicate stability source for ${entry.id}`);
    }
    ids.add(entry.id);
    sources.add(normalizedSource);
    kindCounts.set(entry.kind, (kindCounts.get(entry.kind) ?? 0) + 1);
  }

  for (const [kind, expectedCount] of expectedKindCounts) {
    const actualCount = kindCounts.get(kind) ?? 0;
    if (actualCount !== expectedCount) {
      throw new Error(
        `Expected ${expectedCount} ${kind} stability cases, found ${actualCount}`,
      );
    }
  }

  const demoCases = readDemoCases();
  if (demoCases.length !== 241) {
    throw new Error(`Expected 241 demo cases, found ${demoCases.length}`);
  }
  const demoIds = new Set(demoCases.map((entry) => entry.id));
  const demoSources = new Map(
    demoCases.map((entry) => [normalizeMermaidSource(entry.source), entry.id]),
  );
  for (const entry of stabilityCases) {
    if (demoIds.has(entry.id)) {
      throw new Error(`Stability case reuses demo id: ${entry.id}`);
    }
    const duplicateDemoId = demoSources.get(normalizeMermaidSource(entry.source));
    if (duplicateDemoId !== undefined) {
      throw new Error(
        `Stability case ${entry.id} reuses source from demo ${duplicateDemoId}`,
      );
    }
  }
}

function validateProductionCases() {
  if (productionCases.length !== 106) {
    throw new Error(
      `Expected 106 production cases, found ${productionCases.length}`,
    );
  }
  if (conformanceCases.length !== 64) {
    throw new Error(
      `Expected 64 independent conformance cases, found ${conformanceCases.length}`,
    );
  }

  const allIds = new Set(stabilityCases.map((entry) => entry.id));
  const allSources = new Set(
    stabilityCases.map((entry) => normalizeMermaidSource(entry.source)),
  );
  const kindCounts = new Map();
  const featureCoverage = new Map(
    Object.keys(requiredFeaturesByKind).map((kind) => [kind, new Set()]),
  );
  const demoCases = readDemoCases();
  const demoIds = new Set(demoCases.map((entry) => entry.id));
  const demoSources = new Map(
    demoCases.map((entry) => [normalizeMermaidSource(entry.source), entry.id]),
  );

  for (const entry of conformanceCases) {
    if (!/^prod_[a-z0-9_]+$/.test(entry.id)) {
      throw new Error(`Invalid production case id: ${entry.id}`);
    }
    if (allIds.has(entry.id) || demoIds.has(entry.id)) {
      throw new Error(`Duplicate production case id: ${entry.id}`);
    }
    if (!supportedKinds.has(entry.kind)) {
      throw new Error(`Unsupported diagram kind for ${entry.id}: ${entry.kind}`);
    }
    if (entry.source.trim().length === 0) {
      throw new Error(`Empty Mermaid source for ${entry.id}`);
    }
    if (entry.title.trim().length === 0 || entry.scenario.trim().length === 0) {
      throw new Error(`Missing real-world description for ${entry.id}`);
    }
    if (!Number.isFinite(entry.aspectRatio) || entry.aspectRatio <= 0) {
      throw new Error(`Invalid aspect ratio for ${entry.id}`);
    }
    if (!Array.isArray(entry.features) || entry.features.length === 0) {
      throw new Error(`Missing feature coverage for ${entry.id}`);
    }
    if (!Array.isArray(entry.expectedTexts) || entry.expectedTexts.length === 0) {
      throw new Error(`Missing semantic text expectations for ${entry.id}`);
    }
    for (const expectedText of entry.expectedTexts) {
      if (!entry.source.includes(expectedText)) {
        throw new Error(
          `${entry.id} expected text is absent from its source: ${expectedText}`,
        );
      }
    }

    const normalizedSource = normalizeMermaidSource(entry.source);
    const duplicateDemoId = demoSources.get(normalizedSource);
    if (duplicateDemoId !== undefined) {
      throw new Error(
        `Production case ${entry.id} reuses source from demo ${duplicateDemoId}`,
      );
    }
    if (allSources.has(normalizedSource)) {
      throw new Error(`Duplicate production source for ${entry.id}`);
    }

    const requiredFeatures = new Set(requiredFeaturesByKind[entry.kind]);
    for (const feature of entry.features) {
      if (!requiredFeatures.has(feature)) {
        throw new Error(
          `${entry.id} declares unknown ${entry.kind} feature: ${feature}`,
        );
      }
      featureCoverage.get(entry.kind).add(feature);
    }
    allIds.add(entry.id);
    allSources.add(normalizedSource);
    kindCounts.set(entry.kind, (kindCounts.get(entry.kind) ?? 0) + 1);
  }

  for (const [kind, expectedCount] of expectedProductionKindCounts) {
    const actualCount = productionCases.filter((entry) => entry.kind === kind).length;
    if (actualCount !== expectedCount) {
      throw new Error(
        `Expected ${expectedCount} ${kind} production cases, found ${actualCount}`,
      );
    }
    const actualConformanceCount = kindCounts.get(kind) ?? 0;
    if (actualConformanceCount !== 8) {
      throw new Error(
        `Expected 8 ${kind} conformance cases, found ${actualConformanceCount}`,
      );
    }
    const missingFeatures = requiredFeaturesByKind[kind].filter(
      (feature) => !featureCoverage.get(kind).has(feature),
    );
    if (missingFeatures.length > 0) {
      throw new Error(
        `Missing ${kind} feature coverage: ${missingFeatures.join(', ')}`,
      );
    }
  }
}

function readDemoCases() {
  const demoCases = flowchartDemoCases.map(({ id, source }) => ({ id, source }));
  for (const [fileName, constructorName] of Object.values(kotlinGalleryFiles)) {
    const sourcePath = resolve(
      repositoryRoot,
      `mermaid-debug-ui/src/commonMain/kotlin/io/github/cmpmermaid/debugui/${fileName}`,
    );
    const kotlin = readFileSync(sourcePath, 'utf8');
    const casePattern = new RegExp(
      `${constructorName}\\(\\s*` +
        'id = "([^"]+)",\\s*' +
        'title = "[^"]+",\\s*' +
        'category = "[^"]+",\\s*' +
        'source = """\\n([\\s\\S]*?)\\n\\s*"""\\.trimIndent\\(\\),\\s*\\)',
      'g',
    );
    demoCases.push(...[...kotlin.matchAll(casePattern)].map((match) => ({
      id: match[1],
      source: match[2],
    })));
  }
  return demoCases;
}

function normalizeMermaidSource(value) {
  const lines = value.trim().replaceAll('\r\n', '\n').split('\n');
  const indents = lines
    .filter((line) => line.trim().length > 0)
    .map((line) => line.match(/^\s*/)[0].length);
  const commonIndent = Math.min(...indents);
  return lines.map((line) => line.slice(commonIndent)).join('\n');
}

function renderKotlin(packageName) {
  const stabilityEntries = renderEntries(stabilityCases);
  const productionEntries = renderEntries(productionCases);

  return `// Generated by tools/official-reference/generate-stability-corpus.mjs.
// Do not edit by hand.
package ${packageName}

internal data class StabilityCorpusCase(
    val id: String,
    val diagramId: String,
    val title: String,
    val scenario: String,
    val layout: String,
    val initialAspectRatio: Float,
    val source: String,
    val expectedTexts: List<String>,
    val features: Set<String>,
)

internal val stabilityCorpusCases: List<StabilityCorpusCase> = listOf(
${stabilityEntries}
)

internal val productionCorpusCases: List<StabilityCorpusCase> = listOf(
${productionEntries}
)
`;
}

function renderEntries(entries) {
  return entries.map((entry) => `    StabilityCorpusCase(
        id = ${JSON.stringify(entry.id)},
        diagramId = ${JSON.stringify(entry.kind)},
        title = ${JSON.stringify(entry.title)},
        scenario = ${JSON.stringify(entry.scenario)},
        layout = ${JSON.stringify(entry.layout ?? 'dagre')},
        initialAspectRatio = ${formatFloat(entry.aspectRatio)},
        source = """
${indent(entry.source.trim(), 12)}
        """.trimIndent(),
        expectedTexts = ${renderStringCollection(entry.expectedTexts, 'listOf')},
        features = ${renderStringCollection(entry.features, 'setOf')},
    ),`).join('\n');
}

function renderStringCollection(values = [], constructorName) {
  if (values.length === 0) {
    return `${constructorName}()`;
  }
  return `${constructorName}(${values.map((value) => JSON.stringify(value)).join(', ')})`;
}

function indent(value, spaces) {
  const prefix = ' '.repeat(spaces);
  return value
    .split('\n')
    .map((line) => line.length === 0 ? '' : `${prefix}${line}`)
    .join('\n');
}

function formatFloat(value) {
  return Number.isInteger(value) ? `${value}.0f` : `${value}f`;
}
