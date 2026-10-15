import { createHash } from 'node:crypto';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';

const root = path.dirname(fileURLToPath(import.meta.url));
const mermaidBrowserBundle = path.resolve(
  root,
  'node_modules/mermaid/dist/mermaid.min.js',
);

const edgeKinds = ['flow', 'contains'];
const topologyClasses = [
  'single-edge',
  'linear-chain',
  'branching',
  'merging',
  'cyclic',
  'disconnected',
  'compound',
  'parallel-edge',
  'self-loop',
];

export async function extractFlowchartTopologies(entries) {
  const { default: puppeteer } = await import('puppeteer');
  const browser = await puppeteer.launch(puppeteerLaunchOptions);
  try {
    const page = await browser.newPage();
    await page.setContent('<!doctype html><html><body></body></html>');
    await page.addScriptTag({ path: mermaidBrowserBundle });
    await page.evaluate(() => {
      globalThis.mermaid.initialize({
        startOnLoad: false,
        securityLevel: 'strict',
      });
    });
    return await page.evaluate(async (payload) => {
      const results = [];
      for (const entry of payload) {
        const diagram =
          await globalThis.mermaid.mermaidAPI.getDiagramFromText(entry.source);
        results.push({
          id: entry.id,
          direction: diagram.db.getDirection(),
          vertices: [...diagram.db.getVertices().keys()],
          edges: diagram.db.getEdges().map((edge) => ({
            start: edge.start,
            end: edge.end,
          })),
          subgraphs: (diagram.db.getSubGraphs?.() ?? []).map((subgraph) => ({
            id: subgraph.id,
            nodes: [...subgraph.nodes],
          })),
        });
      }
      return results;
    }, entries.map(({ id, source }) => ({ id, source })));
  } finally {
    await browser.close();
  }
}

export function analyzeFlowchartTopologyDiversity(
  entries,
  parsedTopologies,
  requirements,
) {
  const topologyById = new Map(
    parsedTopologies.map((topology) => [topology.id, topology]),
  );
  const cases = entries.map((entry) => {
    const topology = topologyById.get(entry.id);
    if (topology === undefined) {
      throw new Error(`Missing parsed Flowchart topology for ${entry.id}`);
    }
    const summary = summarizeFlowchartTopology(topology);
    return {
      id: entry.id,
      coverageCategory: entry.coverageCategory,
      fingerprint: flowchartTopologyFingerprint(topology),
      ...summary,
    };
  });
  const failures = [];
  const uniqueTopologyCount =
    new Set(cases.map((entry) => entry.fingerprint)).size;
  if (uniqueTopologyCount < requirements.minimumUniqueTopologies) {
    failures.push(
      `expected at least ${requirements.minimumUniqueTopologies} unique ` +
        `parser topologies, found ${uniqueTopologyCount}`,
    );
  }
  const topologyClassCounts = Object.fromEntries(
    topologyClasses.map((topologyClass) => [
      topologyClass,
      cases.filter((entry) => entry.classes.includes(topologyClass)).length,
    ]),
  );
  for (const [topologyClass, minimum] of Object.entries(
    requirements.minimumClassCounts,
  )) {
    const actual = topologyClassCounts[topologyClass];
    if (actual < minimum) {
      failures.push(
        `topology class ${topologyClass} has ${actual}/${minimum} required cases`,
      );
    }
  }
  for (const [topologyClass, maximum] of Object.entries(
    requirements.maximumClassCounts ?? {},
  )) {
    const actual = topologyClassCounts[topologyClass];
    if (actual > maximum) {
      failures.push(
        `topology class ${topologyClass} has ${actual}/${maximum} maximum cases`,
      );
    }
  }
  const categoryReports = Object.entries(
    requirements.minimumUniqueTopologiesByCategory,
  ).map(([category, minimumUniqueTopologies]) => {
    const categoryCases = cases.filter(
      (entry) => entry.coverageCategory === category,
    );
    const actual =
      new Set(categoryCases.map((entry) => entry.fingerprint)).size;
    if (actual < minimumUniqueTopologies) {
      failures.push(
        `${category} has ${actual}/${minimumUniqueTopologies} required ` +
          'unique parser topologies',
      );
    }
    return {
      category,
      caseCount: categoryCases.length,
      uniqueTopologyCount: actual,
      minimumUniqueTopologies,
      status: actual >= minimumUniqueTopologies ? 'pass' : 'fail',
    };
  });
  return {
    status: failures.length === 0 ? 'pass' : 'fail',
    requirements,
    counts: {
      cases: cases.length,
      uniqueTopologies: uniqueTopologyCount,
      topologyClasses: topologyClassCounts,
    },
    categories: categoryReports,
    duplicateTopologies: duplicateTopologyGroups(cases),
    cases,
    failures,
  };
}

export function flowchartTopologyFingerprint(topology) {
  const graph = topologyGraph(topology);
  const canonical = canonicalGraphCode(graph);
  return createHash('sha256').update(canonical).digest('hex');
}

export function summarizeFlowchartTopology(topology) {
  const vertices = new Set(topology.vertices);
  for (const edge of topology.edges) {
    vertices.add(edge.start);
    vertices.add(edge.end);
  }
  const indegree = new Map([...vertices].map((id) => [id, 0]));
  const outdegree = new Map([...vertices].map((id) => [id, 0]));
  const edgeCounts = new Map();
  for (const edge of topology.edges) {
    outdegree.set(edge.start, (outdegree.get(edge.start) ?? 0) + 1);
    indegree.set(edge.end, (indegree.get(edge.end) ?? 0) + 1);
    const key = JSON.stringify([edge.start, edge.end]);
    edgeCounts.set(key, (edgeCounts.get(key) ?? 0) + 1);
  }
  const weakComponentCount = countWeakComponents(vertices, topology.edges);
  const cyclic = containsDirectedCycle(vertices, topology.edges);
  const branching = [...outdegree.values()].some((degree) => degree > 1);
  const merging = [...indegree.values()].some((degree) => degree > 1);
  const parallelEdge = [...edgeCounts.values()].some((count) => count > 1);
  const selfLoop = topology.edges.some((edge) => edge.start === edge.end);
  const compound = topology.subgraphs.length > 0;
  const linearChain =
    vertices.size >= 2 &&
    topology.edges.length === vertices.size - 1 &&
    weakComponentCount === 1 &&
    !cyclic &&
    !branching &&
    !merging;
  const classes = [
    ...(topology.edges.length === 1 ? ['single-edge'] : []),
    ...(linearChain ? ['linear-chain'] : []),
    ...(branching ? ['branching'] : []),
    ...(merging ? ['merging'] : []),
    ...(cyclic ? ['cyclic'] : []),
    ...(weakComponentCount > 1 ? ['disconnected'] : []),
    ...(compound ? ['compound'] : []),
    ...(parallelEdge ? ['parallel-edge'] : []),
    ...(selfLoop ? ['self-loop'] : []),
  ];
  return {
    nodeCount: vertices.size,
    edgeCount: topology.edges.length,
    subgraphCount: topology.subgraphs.length,
    weakComponentCount,
    classes,
  };
}

function topologyGraph(topology) {
  const vertexIds = new Set(topology.vertices);
  for (const edge of topology.edges) {
    vertexIds.add(edge.start);
    vertexIds.add(edge.end);
  }
  const groupIds = new Set(topology.subgraphs.map((subgraph) => subgraph.id));
  const nodes = [
    ...[...vertexIds].map((id) => ({ key: `vertex:${id}`, type: 'vertex' })),
    ...[...groupIds].map((id) => ({ key: `group:${id}`, type: 'group' })),
  ];
  const edges = topology.edges.map((edge) => ({
    start: `vertex:${edge.start}`,
    end: `vertex:${edge.end}`,
    kind: 'flow',
  }));
  for (const subgraph of topology.subgraphs) {
    for (const member of subgraph.nodes) {
      edges.push({
        start: `group:${subgraph.id}`,
        end: groupIds.has(member) ? `group:${member}` : `vertex:${member}`,
        kind: 'contains',
      });
    }
  }
  return { nodes, edges };
}

function canonicalGraphCode(graph) {
  const indexByKey = new Map(
    graph.nodes.map((node, index) => [node.key, index]),
  );
  const matrices = Object.fromEntries(edgeKinds.map((kind) => [
    kind,
    Array.from(
      { length: graph.nodes.length },
      () => Array(graph.nodes.length).fill(0),
    ),
  ]));
  for (const edge of graph.edges) {
    const start = indexByKey.get(edge.start);
    const end = indexByKey.get(edge.end);
    if (start === undefined || end === undefined) {
      throw new Error(`Topology edge references an unknown node: ${edge.kind}`);
    }
    matrices[edge.kind][start][end] += 1;
  }
  const initialGroups = new Map();
  graph.nodes.forEach((node, index) => {
    const signature = JSON.stringify([
      node.type,
      ...edgeKinds.flatMap((kind) => [
        sum(matrices[kind][index]),
        sum(matrices[kind].map((row) => row[index])),
        matrices[kind][index][index],
      ]),
    ]);
    const group = initialGroups.get(signature) ?? [];
    group.push(index);
    initialGroups.set(signature, group);
  });
  const partition = [...initialGroups.entries()]
    .sort(([left], [right]) => left.localeCompare(right))
    .map(([, members]) => members);
  return canonicalSearch(
    graph.nodes.map((node) => node.type),
    matrices,
    partition,
    new Map(),
  );
}

function canonicalSearch(nodeTypes, matrices, candidatePartition, memo) {
  const partition = refinePartition(matrices, candidatePartition);
  const memoKey = JSON.stringify(partition);
  const cached = memo.get(memoKey);
  if (cached !== undefined) return cached;
  if (partition.every((cell) => cell.length === 1)) {
    const order = partition.flat();
    const code = JSON.stringify({
      nodeTypes: order.map((index) => nodeTypes[index]),
      matrices: edgeKinds.map((kind) =>
        order.map((start) => order.map((end) => matrices[kind][start][end]))
      ),
    });
    memo.set(memoKey, code);
    return code;
  }
  const targetCellIndex = partition.findIndex((cell) => cell.length > 1);
  const targetCell = partition[targetCellIndex];
  const representatives = targetCell.filter((candidate, index) =>
    !targetCell.slice(0, index).some((previous) =>
      areExactTwins(matrices, candidate, previous)
    )
  );
  let best = null;
  for (const candidate of representatives) {
    const individualized = partition.map((cell, index) =>
      index === targetCellIndex
        ? [[candidate], cell.filter((entry) => entry !== candidate)]
        : [cell]
    ).flat();
    const code = canonicalSearch(nodeTypes, matrices, individualized, memo);
    if (best === null || code < best) best = code;
  }
  memo.set(memoKey, best);
  return best;
}

function refinePartition(matrices, candidatePartition) {
  let partition = candidatePartition.map((cell) => [...cell]);
  while (true) {
    let changed = false;
    const refined = [];
    for (const cell of partition) {
      const buckets = new Map();
      for (const vertex of cell) {
        const signature = JSON.stringify(partition.flatMap((targetCell) =>
          edgeKinds.flatMap((kind) => [
            sum(targetCell.map((target) => matrices[kind][vertex][target])),
            sum(targetCell.map((source) => matrices[kind][source][vertex])),
          ])
        ));
        const bucket = buckets.get(signature) ?? [];
        bucket.push(vertex);
        buckets.set(signature, bucket);
      }
      const splitCells = [...buckets.entries()]
        .sort(([left], [right]) => left.localeCompare(right))
        .map(([, members]) => members);
      if (splitCells.length > 1) changed = true;
      refined.push(...splitCells);
    }
    partition = refined;
    if (!changed) return partition;
  }
}

function areExactTwins(matrices, left, right) {
  for (const kind of edgeKinds) {
    const matrix = matrices[kind];
    if (
      matrix[left][left] !== matrix[right][right] ||
      matrix[left][right] !== matrix[right][left]
    ) {
      return false;
    }
    for (let other = 0; other < matrix.length; other += 1) {
      if (other === left || other === right) continue;
      if (
        matrix[left][other] !== matrix[right][other] ||
        matrix[other][left] !== matrix[other][right]
      ) {
        return false;
      }
    }
  }
  return true;
}

function countWeakComponents(vertices, edges) {
  if (vertices.size === 0) return 0;
  const adjacent = new Map([...vertices].map((id) => [id, new Set()]));
  for (const edge of edges) {
    adjacent.get(edge.start)?.add(edge.end);
    adjacent.get(edge.end)?.add(edge.start);
  }
  const remaining = new Set(vertices);
  let count = 0;
  while (remaining.size > 0) {
    count += 1;
    const pending = [remaining.values().next().value];
    while (pending.length > 0) {
      const current = pending.pop();
      if (!remaining.delete(current)) continue;
      pending.push(...(adjacent.get(current) ?? []));
    }
  }
  return count;
}

function containsDirectedCycle(vertices, edges) {
  const adjacent = new Map([...vertices].map((id) => [id, []]));
  for (const edge of edges) {
    adjacent.get(edge.start)?.push(edge.end);
  }
  const visiting = new Set();
  const visited = new Set();
  const visit = (vertex) => {
    if (visiting.has(vertex)) return true;
    if (visited.has(vertex)) return false;
    visiting.add(vertex);
    for (const target of adjacent.get(vertex) ?? []) {
      if (visit(target)) return true;
    }
    visiting.delete(vertex);
    visited.add(vertex);
    return false;
  };
  return [...vertices].some(visit);
}

function duplicateTopologyGroups(cases) {
  const groups = new Map();
  for (const entry of cases) {
    const group = groups.get(entry.fingerprint) ?? [];
    group.push(entry.id);
    groups.set(entry.fingerprint, group);
  }
  return [...groups.entries()]
    .filter(([, ids]) => ids.length > 1)
    .map(([fingerprint, ids]) => ({ fingerprint, ids }));
}

function sum(values) {
  return values.reduce((total, value) => total + value, 0);
}
