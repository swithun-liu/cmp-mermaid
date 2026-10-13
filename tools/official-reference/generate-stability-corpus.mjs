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
import {
  corpusSummary as visualStressCorpusSummary,
  labelProfiles as visualStressLabelProfiles,
  samples as visualStressSamples,
  stressSamplesPerKind as visualStressSamplesPerKind,
} from './visual-parity-corpus.mjs';
import {
  cases as visualStructureCases,
  casesPerKind as visualStructureCasesPerKind,
  corpusSummary as visualStructureCorpusSummary,
} from './visual-structure-corpus.mjs';

const root = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(root, '../..');
const kotlinGalleryFiles = {
  swimlanes: ['SwimlaneDemos.kt', 'SwimlaneDemo'],
  architecture: ['ArchitectureDemos.kt', 'ArchitectureDemo'],
  c4: ['C4Demos.kt', 'C4Demo'],
  railroad: ['RailroadDemos.kt', 'RailroadDemo'],
  treeview: ['TreeViewDemos.kt', 'TreeViewDemo'],
  xychart: ['XyChartDemos.kt', 'XyChartDemo'],
  quadrant: ['QuadrantDemos.kt', 'QuadrantDemo'],
  timeline: ['TimelineDemos.kt', 'TimelineDemo'],
  kanban: ['KanbanDemos.kt', 'KanbanDemo'],
  sequence: ['SequenceDemos.kt', 'SequenceDemo'],
  class: ['ClassDemos.kt', 'ClassDemo'],
  state: ['StateDemos.kt', 'StateDemo'],
  er: ['ErDemos.kt', 'ErDemo'],
  gantt: ['GanttDemos.kt', 'GanttDemo'],
  pie: ['PieDemos.kt', 'PieDemo'],
  journey: ['JourneyDemos.kt', 'JourneyDemo'],
  requirement: ['RequirementDemos.kt', 'RequirementDemo'],
  gitgraph: ['GitGraphDemos.kt', 'GitGraphDemo'],
  mindmap: ['MindmapDemos.kt', 'MindmapDemo'],
  packet: ['PacketDemos.kt', 'PacketDemo'],
  radar: ['RadarDemos.kt', 'RadarDemo'],
  sankey: ['SankeyDemos.kt', 'SankeyDemo'],
  treemap: ['TreemapDemos.kt', 'TreemapDemo'],
  venn: ['VennDemos.kt', 'VennDemo'],
  ishikawa: ['IshikawaDemos.kt', 'IshikawaDemo'],
  cynefin: ['CynefinDemos.kt', 'CynefinDemo'],
  block: ['BlockDemos.kt', 'BlockDemo'],
  eventmodeling: ['EventModelingDemos.kt', 'EventModelingDemo'],
  agentflow: ['AgentflowDemos.kt', 'AgentflowDemo'],
  usecase: ['UsecaseDemos.kt', 'UsecaseDemo'],
  wardley: ['WardleyDemos.kt', 'WardleyDemo'],
  zenuml: ['ZenUmlDemos.kt', 'ZenUmlDemo'],
};
const expectedKindCounts = new Map([
  ['flowchart', 6],
  ['swimlanes', 5],
  ['architecture', 5],
  ['c4', 5],
  ['railroad', 5],
  ['treeview', 5],
  ['xychart', 5],
  ['quadrant', 5],
  ['timeline', 5],
  ['kanban', 5],
  ['sequence', 6],
  ['class', 5],
  ['state', 5],
  ['er', 5],
  ['gantt', 5],
  ['pie', 5],
  ['journey', 5],
  ['requirement', 5],
  ['gitgraph', 5],
  ['mindmap', 5],
  ['packet', 5],
  ['radar', 5],
  ['sankey', 5],
  ['treemap', 5],
  ['venn', 5],
  ['ishikawa', 5],
  ['cynefin', 5],
  ['block', 5],
  ['eventmodeling', 5],
  ['agentflow', 5],
  ['usecase', 5],
  ['wardley', 5],
  ['zenuml', 5],
]);
const expectedProductionKindCounts = new Map([
  ['flowchart', 14],
  ['swimlanes', 13],
  ['architecture', 13],
  ['c4', 13],
  ['railroad', 18],
  ['treeview', 13],
  ['xychart', 13],
  ['quadrant', 13],
  ['timeline', 13],
  ['kanban', 13],
  ['sequence', 14],
  ['class', 13],
  ['state', 13],
  ['er', 13],
  ['gantt', 13],
  ['pie', 13],
  ['journey', 13],
  ['requirement', 13],
  ['gitgraph', 13],
  ['mindmap', 13],
  ['packet', 13],
  ['radar', 13],
  ['sankey', 13],
  ['treemap', 13],
  ['venn', 13],
  ['ishikawa', 13],
  ['cynefin', 13],
  ['block', 13],
  ['eventmodeling', 13],
  ['agentflow', 13],
  ['usecase', 18],
  ['wardley', 13],
  ['zenuml', 13],
]);
const supportedKinds = new Set([
  'flowchart',
  'swimlanes',
  'architecture',
  'c4',
  'railroad',
  'treeview',
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
  'usecase',
  'wardley',
  'zenuml',
]);

validateStabilityCases();
validateProductionCases();
validateVisualStructureCases();
validateVisualStressSamples();

const outputs = [
  {
    packageName: 'com.swithun.cmpmermaid.debugui.generated',
    path: resolve(
      repositoryRoot,
      'mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/generated/' +
        'StabilityCorpus.kt',
    ),
  },
  {
    packageName: 'com.swithun.cmpmermaid.core.generated',
    path: resolve(
      repositoryRoot,
      'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/generated/' +
        'StabilityCorpus.kt',
    ),
  },
];

for (const output of outputs) {
  mkdirSync(dirname(output.path), { recursive: true });
  writeFileSync(output.path, renderKotlin(output.packageName));
  console.log(`Generated ${output.path}`);
}

const visualStressMetadataPath = resolve(
  repositoryRoot,
  'docs/assets/stability-report/visual-stress-corpus-metadata.json',
);
writeFileSync(
  visualStressMetadataPath,
  `${JSON.stringify(renderVisualStressCorpusMetadata(), null, 2)}\n`,
);
console.log(`Generated ${visualStressMetadataPath}`);

const visualStructureMetadataPath = resolve(
  repositoryRoot,
  'docs/assets/stability-report/visual-structure-corpus-metadata.json',
);
writeFileSync(
  visualStructureMetadataPath,
  `${JSON.stringify(renderVisualStructureCorpusMetadata(), null, 2)}\n`,
);
console.log(`Generated ${visualStructureMetadataPath}`);

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
  if (demoCases.length !== 418) {
    throw new Error(`Expected 418 demo cases, found ${demoCases.length}`);
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
  if (productionCases.length !== 441) {
    throw new Error(
      `Expected 441 production cases, found ${productionCases.length}`,
    );
  }
  if (conformanceCases.length !== 274) {
    throw new Error(
      `Expected 274 independent conformance cases, found ${conformanceCases.length}`,
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
    const expectedConformanceCount =
      expectedCount - (expectedKindCounts.get(kind) ?? 0);
    if (actualConformanceCount !== expectedConformanceCount) {
      throw new Error(
        `Expected ${expectedConformanceCount} ${kind} conformance cases, ` +
          `found ${actualConformanceCount}`,
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

function validateVisualStructureCases() {
  const expectedCount = supportedKinds.size * visualStructureCasesPerKind;
  if (visualStructureCases.length !== expectedCount) {
    throw new Error(
      `Expected ${expectedCount} visual structure cases, ` +
        `found ${visualStructureCases.length}`,
    );
  }
  const ids = new Set();
  const sources = new Set();
  const blueprintIds = new Set();
  for (const entry of visualStructureCases) {
    if (!/^visual_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid visual structure case id: ${entry.id}`);
    }
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate visual structure case id: ${entry.id}`);
    }
    if (sources.has(entry.source)) {
      throw new Error(`Duplicate visual structure source: ${entry.id}`);
    }
    if (!supportedKinds.has(entry.kind)) {
      throw new Error(`Unsupported visual structure kind: ${entry.kind}`);
    }
    if (
      entry.coverageRole !== 'independent-visual-scenario' ||
      entry.scenarioBlueprintId.length === 0 ||
      entry.normalizedSemanticSignature.length !== 64 ||
      entry.structuralSignature.length !== 64 ||
      entry.sourceSha256.length !== 64
    ) {
      throw new Error(`Invalid visual structure metadata: ${entry.id}`);
    }
    if (blueprintIds.has(entry.scenarioBlueprintId)) {
      throw new Error(
        `Duplicate visual scenario blueprint: ${entry.scenarioBlueprintId}`,
      );
    }
    ids.add(entry.id);
    sources.add(entry.source);
    blueprintIds.add(entry.scenarioBlueprintId);
  }
  for (const kind of supportedKinds) {
    const kindCases = visualStructureCases.filter((entry) => entry.kind === kind);
    const normalizedSignatureCount = new Set(
      kindCases.map((entry) => entry.normalizedSemanticSignature),
    ).size;
    const blueprintCount = new Set(
      kindCases.map((entry) => entry.scenarioBlueprintId),
    ).size;
    if (
      kindCases.length !== visualStructureCasesPerKind ||
      normalizedSignatureCount !== visualStructureCasesPerKind ||
      blueprintCount !== visualStructureCasesPerKind
    ) {
      throw new Error(
        `${kind} has ${kindCases.length} cases and ` +
          `${normalizedSignatureCount} normalized structures and ` +
          `${blueprintCount} independent blueprints`,
      );
    }
  }
  if (
    visualStructureCorpusSummary.structuralCaseCount !==
      visualStructureCases.length ||
    visualStructureCorpusSummary.structuralCasesPerKind !==
      visualStructureCasesPerKind
  ) {
    throw new Error('Visual structure corpus summary is inconsistent');
  }
}

function validateVisualStressSamples() {
  const expectedCount = supportedKinds.size * visualStressSamplesPerKind;
  if (visualStressSamples.length !== expectedCount) {
    throw new Error(
      `Expected ${expectedCount} visual stress samples, ` +
        `found ${visualStressSamples.length}`,
    );
  }
  const ids = new Set();
  const sources = new Set();
  const structuralScenarioIds = new Set();
  for (const entry of visualStressSamples) {
    if (!/^parity_[a-z0-9]+_\d{3}$/.test(entry.id)) {
      throw new Error(`Invalid visual stress sample id: ${entry.id}`);
    }
    if (ids.has(entry.id)) {
      throw new Error(`Duplicate visual stress sample id: ${entry.id}`);
    }
    if (sources.has(normalizeMermaidSource(entry.source))) {
      throw new Error(`Duplicate visual stress source: ${entry.id}`);
    }
    if (!supportedKinds.has(entry.kind)) {
      throw new Error(`Unsupported visual stress kind: ${entry.kind}`);
    }
    if (!Array.isArray(entry.expectedTexts) || entry.expectedTexts.length === 0) {
      throw new Error(`Missing visual stress text expectations for ${entry.id}`);
    }
    if (!Array.isArray(entry.features) || entry.features.length === 0) {
      throw new Error(`Missing visual stress dimensions for ${entry.id}`);
    }
    if (entry.sourceSha256.length !== 64) {
      throw new Error(`Invalid source hash for ${entry.id}`);
    }
    if (
      entry.coverageRole !== 'text-layout-stress' ||
      !productionCases.some((candidate) =>
        candidate.id === entry.structuralScenarioId &&
        candidate.kind === entry.kind
      )
    ) {
      throw new Error(`Invalid structural scenario metadata for ${entry.id}`);
    }
    ids.add(entry.id);
    sources.add(normalizeMermaidSource(entry.source));
    structuralScenarioIds.add(entry.structuralScenarioId);
  }
  for (const kind of supportedKinds) {
    const count = visualStressSamples.filter((entry) => entry.kind === kind).length;
    if (count !== visualStressSamplesPerKind) {
      throw new Error(
        `Expected ${visualStressSamplesPerKind} ${kind} visual stress samples, ` +
          `found ${count}`,
      );
    }
  }
  if (structuralScenarioIds.size !== productionCases.length) {
    throw new Error(
      `Expected ${productionCases.length} structural scenarios in the stress ` +
        `matrix, found ${structuralScenarioIds.size}`,
    );
  }
  if (
    visualStressCorpusSummary.structuralScenarioCount !== productionCases.length ||
    visualStressCorpusSummary.stressSampleCount !== visualStressSamples.length
  ) {
    throw new Error('Visual stress corpus summary is inconsistent');
  }
}

function readDemoCases() {
  const demoCases = flowchartDemoCases.map(({ id, source }) => ({ id, source }));
  for (const [fileName, constructorName] of Object.values(kotlinGalleryFiles)) {
    const sourcePath = resolve(
      repositoryRoot,
      `mermaid-debug-ui/src/commonMain/kotlin/com/swithun/cmpmermaid/debugui/${fileName}`,
    );
    const kotlin = readFileSync(sourcePath, 'utf8');
    const casePattern = new RegExp(
      `${constructorName}\\(\\s*` +
        'id = "([^"]+)",\\s*' +
        'title = "[^"]+",\\s*' +
        'category = "[^"]+",\\s*' +
        'source = """\\n([\\s\\S]*?)\\n\\s*"""\\.trimIndent\\(\\),',
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

function renderVisualStressCorpusMetadata() {
  return {
    mermaidVersion: '12.0.0',
    structuralCoverage: {
      role: 'semantic-and-structural-coverage',
      source: 'tools/official-reference/production-corpus.mjs',
      scenarioCount: visualStressCorpusSummary.structuralScenarioCount,
    },
    stressCoverage: {
      role: 'text-layout-stress',
      source: 'tools/official-reference/visual-parity-corpus.mjs',
      sampleCount: visualStressCorpusSummary.stressSampleCount,
      samplesPerDiagramFamily: visualStressCorpusSummary.stressSamplesPerKind,
      availableTextLayoutProfileCount:
        visualStressCorpusSummary.availableTextLayoutProfileCount,
      addsStructuralCoverage: false,
    },
    diagramFamilies: visualStressCorpusSummary.countsByKind,
  };
}

function renderVisualStructureCorpusMetadata() {
  return {
    mermaidVersion: '12.0.0',
    role: 'independent-scenario-visual-coverage',
    source: 'tools/official-reference/visual-structure-corpus.mjs',
    diagramFamilyCount: visualStructureCorpusSummary.diagramFamilyCount,
    caseCount: visualStructureCorpusSummary.structuralCaseCount,
    casesPerDiagramFamily:
      visualStructureCorpusSummary.structuralCasesPerKind,
    uniquenessBasis: [
      'unique-scenario-blueprint-id',
      'explicit-256-entry-business-scenario-catalog',
      'label-stripped-normalized-mermaid-source',
      'family-specific-coverage-contract',
      'six-category-coverage-driven-corpus-for-all-families',
      'family-specific-capability-axes',
      'family-specific-stress-augmentation',
      'sixteen-equivalent-syntax-pairs-per-family',
      'family-specific-structural-profile',
    ],
    diagramFamilies: visualStructureCorpusSummary.countsByKind,
  };
}

function renderKotlin(packageName) {
  const stabilityEntries = renderEntries(stabilityCases);
  const productionEntries = renderEntries(productionCases);
  const visualStructureRecordChunks = renderVisualStructureRecordChunks();
  const structuralScenarioCounts = visualStressCorpusSummary.countsByKind
    .map(({ kind, structuralScenarioCount }) =>
      `${JSON.stringify(kind)} to ${structuralScenarioCount},`
    )
    .join('\n');

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

internal const val visualStructureCaseCount: Int =
    ${visualStructureCorpusSummary.structuralCaseCount}
internal const val visualStructureCasesPerDiagram: Int =
    ${visualStructureCasesPerKind}

private val visualStructureCorpusRecordChunks: List<String> = listOf(
${visualStructureRecordChunks}
)

private const val base64Alphabet: String =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

private val base64DecodeTable: IntArray = IntArray(128) { -1 }.apply {
    base64Alphabet.forEachIndexed { value, character ->
        this[character.code] = value
    }
}

private fun decodeBase64Utf8(encoded: String): String {
    if (encoded.isEmpty()) {
        return ""
    }
    val padding = when {
        encoded.endsWith("==") -> 2
        encoded.endsWith("=") -> 1
        else -> 0
    }
    val decoded = ByteArray(encoded.length / 4 * 3 - padding)
    var inputIndex = 0
    var outputIndex = 0
    while (inputIndex < encoded.length) {
        val first = base64Value(encoded[inputIndex])
        val second = base64Value(encoded[inputIndex + 1])
        val third = base64Value(encoded[inputIndex + 2])
        val fourth = base64Value(encoded[inputIndex + 3])
        decoded[outputIndex++] = ((first shl 2) or (second shr 4)).toByte()
        if (outputIndex < decoded.size) {
            decoded[outputIndex++] = ((second shl 4) or (third shr 2)).toByte()
        }
        if (outputIndex < decoded.size) {
            decoded[outputIndex++] = ((third shl 6) or fourth).toByte()
        }
        inputIndex += 4
    }
    return decoded.decodeToString()
}

private fun base64Value(character: Char): Int =
    if (character == '=') {
        0
    } else {
        base64DecodeTable[character.code]
    }

internal val visualStructureCorpusCases: List<StabilityCorpusCase> by lazy {
    visualStructureCorpusRecordChunks
        .asSequence()
        .flatMap(String::lineSequence)
        .filter(String::isNotBlank)
        .map { record ->
            val fields = record.split('|', limit = 9)
            val expectedTextPayload = decodeBase64Utf8(fields[6])
            val featurePayload = decodeBase64Utf8(fields[7])
            val payload = decodeBase64Utf8(fields[8])
            StabilityCorpusCase(
                id = fields[0],
                diagramId = fields[1],
                title = decodeBase64Utf8(fields[2]),
                scenario = decodeBase64Utf8(fields[3]),
                layout = fields[4],
                initialAspectRatio = fields[5].toFloat(),
                source = payload,
                expectedTexts = if (expectedTextPayload.isEmpty()) {
                    emptyList()
                } else {
                    expectedTextPayload.split('\\u0000')
                },
                features = if (featurePayload.isEmpty()) {
                    emptySet()
                } else {
                    featurePayload.split('\\u0000').toSet()
                },
            )
        }
        .toList()
}

internal const val visualStressStructuralScenarioCount: Int =
    ${visualStressCorpusSummary.structuralScenarioCount}
internal const val visualStressSampleCount: Int =
    ${visualStressCorpusSummary.stressSampleCount}
internal const val visualStressSamplesPerDiagram: Int =
    ${visualStressSamplesPerKind}

internal val visualStressStructuralScenarioCountsByDiagram: Map<String, Int> =
    mapOf(
${indent(structuralScenarioCounts, 8)}
    )

private val visualStressLabelProfiles: List<String> = listOf(
${indent(visualStressLabelProfiles.map((value) => `${JSON.stringify(value)},`).join('\n'), 4)}
)

internal val visualStressCorpusSamples: List<StabilityCorpusCase> by lazy {
    buildList {
        val kinds = listOf(
            "flowchart",
            "swimlanes",
            "architecture",
            "c4",
            "railroad",
            "treeview",
            "xychart",
            "quadrant",
            "timeline",
            "kanban",
            "sequence",
            "class",
            "state",
            "er",
            "gantt",
            "pie",
            "journey",
            "requirement",
            "gitgraph",
            "mindmap",
            "packet",
            "radar",
            "sankey",
            "treemap",
            "venn",
            "ishikawa",
            "cynefin",
            "block",
            "eventmodeling",
            "agentflow",
            "usecase",
            "wardley",
            "zenuml",
        )
        kinds.forEach { kind ->
            val seeds = productionCorpusCases.filter { case ->
                case.diagramId == kind
            }
            repeat(${visualStressSamplesPerKind}) { index ->
                val seed = seeds[index % seeds.size]
                val profileIndex = index / seeds.size
                val ordinal = index + 1
                val suffix = ordinal.toString().padStart(3, '0')
                val evidenceId = "ParityEvidence\$suffix"
                val visibleLabel =
                    "\${visualStressLabelProfiles[profileIndex]} \$suffix"
                add(
                    seed.copy(
                        id = "parity_\${kind}_\$suffix",
                        title = "\${seed.title} - profile " +
                            (profileIndex + 1).toString().padStart(2, '0'),
                        scenario = "Text/layout stress sample \$ordinal of " +
                            "${visualStressSamplesPerKind}; derived from structural " +
                            "scenario \${seed.id}; visible label profile " +
                            "\${profileIndex + 1}. This sample does not add " +
                            "structural coverage.",
                        source = addVisualParityVariation(
                            kind = kind,
                            source = seed.source,
                            evidenceId = evidenceId,
                            label = visibleLabel,
                            ordinal = ordinal,
                        ),
                        expectedTexts = seed.expectedTexts + visibleLabel,
                        features = seed.features + setOf(
                            "seed:\${seed.id}",
                            "label-profile:\${profileIndex + 1}",
                        ),
                    ),
                )
            }
        }
    }
}

private fun addVisualParityVariation(
    kind: String,
    source: String,
    evidenceId: String,
    label: String,
    ordinal: Int,
): String = when (kind) {
    "flowchart" -> appendFlowchartEvidence(source, evidenceId, label)
    "swimlanes" -> appendFlowchartEvidence(source, evidenceId, label)
    "architecture" -> replaceArchitectureVisualParityServiceIcon(source, label)
    "c4" -> "\${source.trimEnd()}\\n" +
        "  Person(\$evidenceId, " +
        "\\"\${escapeQuotedVisualParityLabel(label)}\\", " +
        "\\"Visual parity evidence\\")\\n"
    "railroad" -> appendRailroadVisualParityEvidence(
        source = source,
        evidenceId = evidenceId,
        label = label,
    )
    "treeview" -> "\${source.trimEnd()}\\n" +
        "\\"\${escapeQuotedVisualParityLabel(label)}\\" ## \$evidenceId\\n"
    "xychart" -> replaceOrInsertVisualParityTitle(source, "xychart", label)
    "quadrant" -> {
        val x = ((ordinal % 8) + 1) / 10.0
        val y = (((ordinal * 3) % 8) + 1) / 10.0
        "\${source.trimEnd()}\\n  \\"\$label\\": [" +
            "\$x, \$y]\\n"
    }
    "timeline" -> appendTimelineEvidence(source, label, ordinal)
    "kanban" -> appendKanbanEvidence(source, evidenceId, label, ordinal)
    "sequence" -> insertAfterDeclaration(
        source = source,
        declaration = "sequenceDiagram",
        line = "  participant \$evidenceId as \$label",
    )
    "class" -> appendClassEvidence(source, evidenceId, label)
    "state" -> "\${source.trimEnd()}\\n  state \\"\$label\\" as \$evidenceId\\n"
    "er" -> "\${source.trimEnd()}\\n  \$evidenceId[\\"\$label\\"]\\n"
    "gantt" -> replaceOrInsertVisualParityTitle(source, "gantt", label)
    "pie" -> "\${source.trimEnd()}\\n  \\"\$label\\" : \${(ordinal % 17) + 3}\\n"
    "journey" -> "\${source.trimEnd()}\\n  \$label: \${(ordinal % 5) + 1}: " +
        "Parity Actor \$ordinal\\n"
    "requirement" -> "\${source.trimEnd()}\\n" +
        "  requirement \$evidenceId {\\n" +
        "    id: \\"PARITY-\${ordinal.toString().padStart(3, '0')}\\"\\n" +
        "    text: \\"\$label\\"\\n" +
        "    risk: low\\n" +
        "    verifyMethod: inspection\\n" +
        "  }\\n"
    "gitgraph" -> "\${source.trimEnd()}\\n" +
        "  commit id: \\"\$evidenceId\\" tag: \\"\$label\\"\\n"
    "mindmap" -> "\${source.trimEnd()}\\n    \$evidenceId[\\"\$label\\"]\\n"
    "packet" -> "\${source.trimEnd()}\\n  +1: \\"\${escapeQuotedVisualParityLabel(label)}\\"\\n"
    "radar" -> replaceOrInsertVisualParityTitle(source, "radar-beta", label)
    "sankey" -> "\${source.trimEnd()}\\n\\"\$label\\",\$evidenceId,\${(ordinal % 17) + 3}\\n"
    "treemap" -> replaceOrInsertTreemapVisualParityTitle(source, label)
    "venn" -> replaceOrInsertVennVisualParityTitle(source, label)
    "ishikawa" -> "\${source.trimEnd()}\\n\$label\\n"
    "cynefin" -> insertCynefinVisualParityEvidence(source, label)
    "block" -> "\${source.trimEnd()}\\n" +
        "  \$evidenceId[(\\"\${escapeQuotedVisualParityLabel(label)}\\")]\\n"
    "eventmodeling" -> "\${source.trimEnd()}\\n" +
        "  rf \${700 + ordinal} evt \$evidenceId { label: \$label }\\n"
    "agentflow" -> appendAgentflowVisualParityEvidence(
        source = source,
        evidenceId = evidenceId,
        label = label,
    )
    "usecase" -> "\${source.trimEnd()}\\n" +
        "\$evidenceId(\\"\${escapeQuotedVisualParityLabel(label)}\\")\\n"
    "wardley" -> "\${source.trimEnd()}\\n" +
        "note \\"\${escapeQuotedVisualParityLabel(label)}\\" [0.08, 0.92]\\n"
    "zenuml" -> insertZenUmlVisualParityParticipant(
        source = source,
        evidenceId = evidenceId,
        label = label,
    )
    else -> source
}

private fun insertZenUmlVisualParityParticipant(
    source: String,
    evidenceId: String,
    label: String,
): String {
    val lines = source.lines().toMutableList()
    val declarationIndex = lines.indexOfFirst { line -> line.trim() == "zenuml" }
    if (declarationIndex < 0) return source
    val titleIndex = lines.withIndex()
        .firstOrNull { (index, line) ->
            index > declarationIndex && line.trimStart().startsWith("title ")
        }
        ?.index
        ?: -1
    val insertionIndex = if (titleIndex >= 0) titleIndex + 1 else declarationIndex + 1
    lines.add(
        insertionIndex,
        "  \$evidenceId as \\"\${escapeQuotedVisualParityLabel(label)}\\"",
    )
    return lines.joinToString("\\n")
}

private fun appendRailroadVisualParityEvidence(
    source: String,
    evidenceId: String,
    label: String,
): String {
    val declaration = source.lineSequence()
        .map(String::trim)
        .firstOrNull { line ->
            line == "railroad-beta" ||
                line == "railroad-ebnf-beta" ||
                line == "railroad-abnf-beta" ||
                line == "railroad-peg-beta"
        }
        ?: return source
    val operator = if (declaration == "railroad-peg-beta") "<-" else "="
    val escapedLabel = escapeQuotedVisualParityLabel(label)
    val expression = if (declaration == "railroad-beta") {
        "terminal(\\"\$escapedLabel\\")"
    } else {
        "\\"\$escapedLabel\\""
    }
    return "\${source.trimEnd()}\\n  \$evidenceId \$operator \$expression ;\\n"
}

private fun replaceArchitectureVisualParityServiceIcon(
    source: String,
    label: String,
): String {
    val serviceIconPattern =
        Regex("""(?m)^(\\s*service\\s+[A-Za-z_][\\w-]*)\\([^)]+\\)""")
    val match = serviceIconPattern.find(source) ?: return source
    val replacement =
        "\${match.groupValues[1]} \\"\${escapeQuotedVisualParityLabel(label)}\\""
    return source.replaceRange(match.range, replacement)
}

private fun appendAgentflowVisualParityEvidence(
    source: String,
    evidenceId: String,
    label: String,
): String {
    val anchorId = findFirstDiagramIdentifier(
        source = source,
        expectedRestPrefixes = listOf("[", "@", "-"),
    )
    return "\${source.trimEnd()}\\n" +
        "  \$evidenceId[\\"\${escapeQuotedVisualParityLabel(label)}\\"]" +
        "@{ shape: refdoc }\\n" +
        "  \$anchorId -.- \$evidenceId\\n"
}

private fun insertCynefinVisualParityEvidence(
    source: String,
    label: String,
): String {
    val lines = source.trimEnd().lines().toMutableList()
    val domainIndex = lines.indexOfLast { line ->
        line.trim() in setOf(
            "complex",
            "complicated",
            "clear",
            "chaotic",
        )
    }
    val escapedLabel = escapeQuotedVisualParityLabel(label)
    if (domainIndex >= 0) {
        val indent = lines[domainIndex].takeWhile(Char::isWhitespace)
        lines.add(domainIndex + 1, "\${indent}  \\"\$escapedLabel\\"")
        return lines.joinToString("\\n") + "\\n"
    }

    val confusionLine = lines.firstOrNull { line -> line.trim() == "confusion" }
        ?: return source
    val indent = confusionLine.takeWhile(Char::isWhitespace)
    lines += "\${indent}complex"
    lines += "\${indent}  \\"\$escapedLabel\\""
    return lines.joinToString("\\n") + "\\n"
}

private fun appendTimelineEvidence(
    source: String,
    label: String,
    ordinal: Int,
): String = "\${source.trimEnd()}\\n  \$label : Evidence " +
    ordinal.toString().padStart(3, '0') + "\\n"

private fun appendKanbanEvidence(
    source: String,
    evidenceId: String,
    label: String,
    ordinal: Int,
): String = "\${source.trimEnd()}\\n" +
    "  \${evidenceId}Stage[Parity stage \${ordinal.toString().padStart(3, '0')}]\\n" +
    "    \$evidenceId[\$label]\\n"

private fun appendFlowchartEvidence(
    source: String,
    evidenceId: String,
    label: String,
): String {
    val anchorId = findFirstDiagramIdentifier(
        source = source,
        expectedRestPrefixes = listOf("[", "(", "{", "@", "-", "o-", "x-", "<", "="),
    ) ?: return source
    val escapedLabel = escapeQuotedVisualParityLabel(label)
    return "\${source.trimEnd()}\\n  \$anchorId -.-> \$evidenceId[\\"\$escapedLabel\\"]\\n"
}

private fun appendClassEvidence(
    source: String,
    evidenceId: String,
    label: String,
): String {
    var anchorId: String? = null
    for (line in source.lines()) {
        val trimmed = line.trimStart()
        if (!trimmed.startsWith("class ")) continue
        val identifier = trimmed.removePrefix("class ")
            .trimStart()
            .takeWhile { character ->
                character.isLetterOrDigit() || character == '_' || character == '-'
            }
        if (identifier.isNotEmpty()) {
            anchorId = identifier
            break
        }
    }
    val resolvedAnchorId = anchorId ?: return source
    val escapedLabel = escapeQuotedVisualParityLabel(label)
    return "\${source.trimEnd()}\\n" +
        "  class \$evidenceId[\\"\$escapedLabel\\"]\\n" +
        "  \$resolvedAnchorId ..> \$evidenceId : parity\\n"
}

private fun findFirstDiagramIdentifier(
    source: String,
    expectedRestPrefixes: List<String>,
): String? {
    for (line in source.lines()) {
        val trimmed = line.trimStart()
        val identifier = trimmed.takeWhile { character ->
            character.isLetterOrDigit() || character == '_' || character == '-'
        }
        if (
            identifier.isEmpty() ||
            (!identifier.first().isLetter() && identifier.first() != '_')
        ) {
            continue
        }
        val rest = trimmed.drop(identifier.length).trimStart()
        if (expectedRestPrefixes.any(rest::startsWith)) {
            return identifier
        }
    }
    return null
}

private fun escapeQuotedVisualParityLabel(label: String): String =
    label.replace("\\\\", "\\\\\\\\").replace("\\"", "\\\\\\"")

private fun replaceOrInsertVisualParityTitle(
    source: String,
    declaration: String,
    suffix: String,
): String {
    val lines = source.lines().toMutableList()
    val titleIndex = lines.indexOfFirst { line ->
        line.trimStart().startsWith("title ")
    }
    if (titleIndex >= 0) {
        val line = lines[titleIndex]
        val indent = line.takeWhile(Char::isWhitespace)
        val title = line.trimStart()
            .removePrefix("title ")
            .trim()
            .removeSurrounding("\\"")
        lines[titleIndex] = "\${indent}title \\"\$title - \$suffix\\""
        return lines.joinToString("\\n")
    }
    val declarationIndex = lines.indexOfFirst { line ->
        val value = line.trim()
        value == declaration ||
            (declaration == "xychart" && value == "xychart horizontal")
    }
    if (declarationIndex < 0) return source
    lines.add(declarationIndex + 1, "  title \\"\$suffix\\"")
    return lines.joinToString("\\n")
}

private fun replaceOrInsertTreemapVisualParityTitle(
    source: String,
    suffix: String,
): String {
    val lines = source.lines().toMutableList()
    val sourceTitleIndex = lines.indexOfFirst { line ->
        line.trimStart().startsWith("title ")
    }
    if (sourceTitleIndex >= 0) {
        val line = lines[sourceTitleIndex]
        val indent = line.takeWhile(Char::isWhitespace)
        val title = line.trimStart()
            .removePrefix("title ")
            .trim()
            .removeSurrounding("\\"")
        lines[sourceTitleIndex] = "\${indent}title \\"\$title - \$suffix\\""
        return lines.joinToString("\\n")
    }
    val frontmatterTitleIndex = lines.indexOfFirst { line ->
        line.trimStart().startsWith("title:")
    }
    if (frontmatterTitleIndex >= 0) {
        val line = lines[frontmatterTitleIndex]
        val indent = line.takeWhile(Char::isWhitespace)
        val title = line.trimStart()
            .removePrefix("title:")
            .trim()
            .removeSurrounding("\\"")
            .removeSurrounding("'")
        lines[frontmatterTitleIndex] = "\${indent}title: \\"\$title - \$suffix\\""
        return lines.joinToString("\\n")
    }
    val declarationIndex = lines.indexOfFirst { line ->
        line.trim() == "treemap" || line.trim() == "treemap-beta"
    }
    if (declarationIndex < 0) return source
    lines.add(declarationIndex + 1, "  title \\"\$suffix\\"")
    return lines.joinToString("\\n")
}

private fun replaceOrInsertVennVisualParityTitle(
    source: String,
    suffix: String,
): String {
    val lines = source.lines().toMutableList()
    val sourceTitleIndex = lines.indexOfFirst { line ->
        line.trimStart().startsWith("title ")
    }
    if (sourceTitleIndex >= 0) {
        val line = lines[sourceTitleIndex]
        val indent = line.takeWhile(Char::isWhitespace)
        val title = line.trimStart()
            .removePrefix("title ")
            .trim()
            .removeSurrounding("\\"")
        lines[sourceTitleIndex] = "\${indent}title \$title - \$suffix"
        return lines.joinToString("\\n")
    }
    val frontmatterTitleIndex = lines.indexOfFirst { line ->
        line.trimStart().startsWith("title:")
    }
    if (frontmatterTitleIndex >= 0) {
        val line = lines[frontmatterTitleIndex]
        val indent = line.takeWhile(Char::isWhitespace)
        val title = line.trimStart()
            .removePrefix("title:")
            .trim()
            .removeSurrounding("\\"")
            .removeSurrounding("'")
        lines[frontmatterTitleIndex] = "\${indent}title: \\"\$title - \$suffix\\""
        return lines.joinToString("\\n")
    }
    val declarationIndex = lines.indexOfFirst { line ->
        line.trim() == "venn-beta"
    }
    if (declarationIndex < 0) return source
    lines.add(declarationIndex + 1, "  title \$suffix")
    return lines.joinToString("\\n")
}

private fun insertAfterDeclaration(
    source: String,
    declaration: String,
    line: String,
): String {
    val lines = source.lines().toMutableList()
    val declarationIndex = lines.indexOfFirst { value ->
        value.trim() == declaration
    }
    if (declarationIndex < 0) return source
    lines.add(declarationIndex + 1, line)
    return lines.joinToString("\\n")
}
`;
}

function renderVisualStructureRecordChunks() {
  const records = visualStructureCases.map((entry) => {
    return [
      entry.id,
      entry.kind,
      Buffer.from(entry.title, 'utf8').toString('base64'),
      Buffer.from(entry.scenario, 'utf8').toString('base64'),
      entry.layout,
      entry.aspectRatio,
      Buffer.from(entry.expectedTexts.join('\u0000'), 'utf8').toString('base64'),
      Buffer.from(entry.features.join('\u0000'), 'utf8').toString('base64'),
      Buffer.from(entry.source, 'utf8').toString('base64'),
    ].join('|');
  });
  const chunks = [];
  let current = [];
  let currentLength = 0;
  for (const record of records) {
    if (current.length > 0 && currentLength + record.length + 1 > 48_000) {
      chunks.push(current);
      current = [];
      currentLength = 0;
    }
    current.push(record);
    currentLength += record.length + 1;
  }
  if (current.length > 0) {
    chunks.push(current);
  }
  return chunks.map((chunk) => `    """
${indent(chunk.join('\n'), 8)}
    """.trimIndent(),`).join('\n');
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
${indent(escapeKotlinRawString(entry.source.trim()), 12)}
        """.trimIndent(),
        expectedTexts = ${renderStringCollection(entry.expectedTexts, 'listOf')},
        features = ${renderStringCollection(entry.features, 'setOf')},
    ),`).join('\n');
}

function escapeKotlinRawString(value) {
  return value
    .replaceAll('$', () => "${'$'}")
    .replaceAll('"""', '${"\\"\\"\\""}');
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
