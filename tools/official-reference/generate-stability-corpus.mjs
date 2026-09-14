import { mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { cases as flowchartDemoCases } from './cases.mjs';
import { cases } from './stability-corpus.mjs';

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

validateCases();

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

function validateCases() {
  const ids = new Set();
  const sources = new Set();
  const kindCounts = new Map();
  for (const entry of cases) {
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
  for (const entry of cases) {
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
  const entries = cases.map((entry) => `    StabilityCorpusCase(
        id = ${JSON.stringify(entry.id)},
        diagramId = ${JSON.stringify(entry.kind)},
        title = ${JSON.stringify(entry.title)},
        scenario = ${JSON.stringify(entry.scenario)},
        layout = ${JSON.stringify(entry.layout ?? 'dagre')},
        initialAspectRatio = ${formatFloat(entry.aspectRatio)},
        source = """
${indent(entry.source.trim(), 12)}
        """.trimIndent(),
    ),`).join('\n');

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
)

internal val stabilityCorpusCases: List<StabilityCorpusCase> = listOf(
${entries}
)
`;
}

function indent(value, spaces) {
  const prefix = ' '.repeat(spaces);
  return value.split('\n').map((line) => `${prefix}${line}`).join('\n');
}

function formatFloat(value) {
  return Number.isInteger(value) ? `${value}.0f` : `${value}f`;
}
