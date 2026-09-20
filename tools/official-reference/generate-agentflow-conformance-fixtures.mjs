import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const MERMAID_VERSION = '12.0.0';
const FIXTURE_SUFFIX = '-agentflow.mmd';
const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const mermaidSourceRoot = process.env.MERMAID_SOURCE_DIR;

if (!mermaidSourceRoot) {
  throw new Error('Set MERMAID_SOURCE_DIR to a Mermaid source checkout');
}

const packageFile = path.join(mermaidSourceRoot, 'packages/mermaid/package.json');
const packageMetadata = JSON.parse(fs.readFileSync(packageFile, 'utf8'));
if (packageMetadata.version !== MERMAID_VERSION) {
  throw new Error(
    `Expected Mermaid source ${MERMAID_VERSION}, found ${packageMetadata.version}`,
  );
}

const fixtureDirectory = path.join(
  mermaidSourceRoot,
  'packages/mermaid/src/diagrams/agentflow/conformance/fixtures',
);
const fixtureNames = fs
  .readdirSync(fixtureDirectory, { withFileTypes: true })
  .filter((entry) => entry.isFile() && entry.name.endsWith(FIXTURE_SUFFIX))
  .map((entry) => entry.name.slice(0, -FIXTURE_SUFFIX.length))
  .sort();

const fixtureHash = crypto.createHash('sha256');
const cases = fixtureNames.map((name) => {
  const sourcePath = path.join(fixtureDirectory, `${name}${FIXTURE_SUFFIX}`);
  const expectationPath = path.join(
    fixtureDirectory,
    `${name}-agentflow.expected.json`,
  );
  const source = fs.readFileSync(sourcePath, 'utf8');
  const expectationSource = fs.readFileSync(expectationPath, 'utf8');
  fixtureHash.update(`${name}\0${source}\0${expectationSource}\0`);
  return {
    name,
    source: source.trimEnd(),
    expected: JSON.parse(expectationSource),
  };
});

const output = path.join(
  repositoryRoot,
  'mermaid-core/src/commonTest/kotlin/com/swithun/cmpmermaid/core/agentflow/upstream/mermaid/AgentflowConformanceCases.kt',
);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(
  output,
  `package com.swithun.cmpmermaid.core.agentflow.upstream.mermaid

/**
 * Generated from Mermaid ${MERMAID_VERSION}
 * packages/mermaid/src/diagrams/agentflow/conformance/fixtures.
 * Combined fixture SHA-256: ${fixtureHash.digest('hex')}
 *
 * Do not edit manually. Run:
 *   MERMAID_SOURCE_DIR=/path/to/mermaid npm run generate:agentflow-conformance-fixtures
 */
internal data class AgentflowExpectedDiagnostic(
    val id: String,
    val nodeId: String? = null,
    val edgeId: String? = null,
    val line: Int? = null,
)

internal data class AgentflowExpectedVertex(
    val id: String,
    val vertexKind: String? = null,
    val metadata: Map<String, Any?>? = null,
)

internal data class AgentflowExpectedEdge(
    val start: String? = null,
    val end: String? = null,
    val id: String? = null,
    val edgeSemantic: String? = null,
)

internal data class AgentflowConformanceCase(
    val name: String,
    val source: String,
    val outcome: String,
    val diagnostics: List<AgentflowExpectedDiagnostic>? = null,
    val allowExtraDiagnostics: Boolean = false,
    val parseErrorContains: String? = null,
    val vertices: List<AgentflowExpectedVertex> = emptyList(),
    val edges: List<AgentflowExpectedEdge> = emptyList(),
)

internal val agentflowConformanceCases: List<AgentflowConformanceCase> = listOf(
${cases.map(kotlinCase).join(',\n')},
)
`,
);

console.log(
  `Generated ${cases.length} Mermaid ${MERMAID_VERSION} Agentflow conformance fixtures.`,
);

function kotlinCase(fixture) {
  const { expected } = fixture;
  const semantic = expected.semanticAssertions ?? {};
  return `    AgentflowConformanceCase(
        name = ${kotlinString(fixture.name)},
        source = ${kotlinRawString(fixture.source)},
        outcome = ${kotlinString(expected.outcome)},
        diagnostics = ${kotlinDiagnostics(expected.diagnostics)},
        allowExtraDiagnostics = ${expected.allowExtraDiagnostics === true},
        parseErrorContains = ${kotlinNullableString(expected.parseErrorContains)},
        vertices = ${kotlinVertices(semantic.vertices)},
        edges = ${kotlinEdges(semantic.edges)},
    )`;
}

function kotlinDiagnostics(diagnostics) {
  if (diagnostics === undefined) {
    return 'null';
  }
  if (diagnostics.length === 0) {
    return 'emptyList()';
  }
  return `listOf(
${diagnostics
  .map(
    (diagnostic) => `            AgentflowExpectedDiagnostic(
                id = ${kotlinString(diagnostic.id)},
                nodeId = ${kotlinNullableString(diagnostic.nodeId)},
                edgeId = ${kotlinNullableString(diagnostic.edgeId)},
                line = ${diagnostic.line ?? 'null'},
            )`,
  )
  .join(',\n')},
        )`;
}

function kotlinVertices(vertices) {
  if (!vertices || vertices.length === 0) {
    return 'emptyList()';
  }
  return `listOf(
${vertices
  .map(
    (vertex) => `            AgentflowExpectedVertex(
                id = ${kotlinString(vertex.id)},
                vertexKind = ${kotlinNullableString(vertex.vertexKind)},
                metadata = ${kotlinLiteral(vertex.metadata)},
            )`,
  )
  .join(',\n')},
        )`;
}

function kotlinEdges(edges) {
  if (!edges || edges.length === 0) {
    return 'emptyList()';
  }
  return `listOf(
${edges
  .map(
    (edge) => `            AgentflowExpectedEdge(
                start = ${kotlinNullableString(edge.start)},
                end = ${kotlinNullableString(edge.end)},
                id = ${kotlinNullableString(edge.id)},
                edgeSemantic = ${kotlinNullableString(edge.edgeSemantic)},
            )`,
  )
  .join(',\n')},
        )`;
}

function kotlinLiteral(value) {
  if (value === undefined || value === null) {
    return 'null';
  }
  if (typeof value === 'string') {
    return kotlinString(value);
  }
  if (typeof value === 'boolean') {
    return String(value);
  }
  if (typeof value === 'number') {
    return Number.isInteger(value) ? `${value}L` : String(value);
  }
  if (Array.isArray(value)) {
    return value.length === 0
      ? 'emptyList()'
      : `listOf(${value.map(kotlinLiteral).join(', ')})`;
  }
  const entries = Object.entries(value);
  return entries.length === 0
    ? 'emptyMap()'
    : `mapOf(${entries
        .map(([key, item]) => `${kotlinString(key)} to ${kotlinLiteral(item)}`)
        .join(', ')})`;
}

function kotlinNullableString(value) {
  return value === undefined || value === null ? 'null' : kotlinString(value);
}

function kotlinString(value) {
  return JSON.stringify(value).replaceAll('$', '\\$');
}

function kotlinRawString(value) {
  if (value.includes('"""')) {
    throw new Error('Agentflow fixture contains a Kotlin raw-string delimiter');
  }
  return `"""
${value.replaceAll('$', () => "${'$'}")}
        """.trimIndent()`;
}
