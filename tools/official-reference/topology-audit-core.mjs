const EPSILON = 1e-3;
const MINIMUM_SHARED_TRACK = 1;

export function compareTopologyManifests(nativeManifest, officialManifest) {
  const nativeEdges = topologyEdges(nativeManifest);
  const officialEdges = topologyEdges(officialManifest);
  const findings = [];
  const nativeIds = [...nativeEdges.keys()].sort();
  const officialIds = [...officialEdges.keys()].sort();

  compareSet(
    nativeIds,
    officialIds,
    'edge-set-mismatch',
    'edge IDs',
    findings,
  );

  const comparisons = [];
  for (const edgeId of nativeIds.filter((id) => officialEdges.has(id))) {
    const nativeEdge = nativeEdges.get(edgeId);
    const officialEdge = officialEdges.get(edgeId);
    const edgeFindings = [];
    if (nativeEdge.topologyPoints.length < 2) {
      edgeFindings.push(finding(
        'native-topology-missing',
        `${edgeId} has no Native topology point sequence`,
      ));
    }
    if (officialEdge.topologyPoints.length < 2) {
      edgeFindings.push(finding(
        'official-topology-missing',
        `${edgeId} has no Official topology point sequence`,
      ));
    }
    compareValue(
      nativeEdge.directionSequence.includes('Diagonal'),
      officialEdge.directionSequence.includes('Diagonal'),
      'diagonal-segment-mismatch',
      `${edgeId} diagonal segment presence`,
      edgeFindings,
    );
    compareValue(
      nativeEdge.bendPoints.length,
      officialEdge.bendPoints.length,
      'bend-count-mismatch',
      `${edgeId} bend count`,
      edgeFindings,
    );
    compareValue(
      nativeEdge.directionSequence,
      officialEdge.directionSequence,
      'direction-sequence-mismatch',
      `${edgeId} direction sequence`,
      edgeFindings,
    );
    compareValue(
      nativeEdge.sourcePortSide,
      officialEdge.sourcePortSide,
      'source-port-side-mismatch',
      `${edgeId} source port side`,
      edgeFindings,
    );
    compareValue(
      nativeEdge.destinationPortSide,
      officialEdge.destinationPortSide,
      'destination-port-side-mismatch',
      `${edgeId} destination port side`,
      edgeFindings,
    );
    findings.push(...edgeFindings);
    comparisons.push({
      edgeId,
      native: edgeSummary(nativeEdge),
      official: edgeSummary(officialEdge),
      findingCodes: edgeFindings.map((entry) => entry.code),
    });
  }

  const nativeRelationships = topologyRelationships(nativeEdges);
  const officialRelationships = topologyRelationships(officialEdges);
  compareSet(
    nativeRelationships.sharedTracks,
    officialRelationships.sharedTracks,
    'shared-track-mismatch',
    'shared track relationships',
    findings,
  );
  compareSet(
    nativeRelationships.strictCrossings,
    officialRelationships.strictCrossings,
    'strict-crossing-mismatch',
    'strict crossing relationships',
    findings,
  );
  compareSet(
    nativeRelationships.sharedEndpoints,
    officialRelationships.sharedEndpoints,
    'shared-endpoint-mismatch',
    'shared endpoint relationships',
    findings,
  );

  return {
    status: findings.length === 0 ? 'pass' : 'fail',
    findings,
    nativeEdgeCount: nativeEdges.size,
    officialEdgeCount: officialEdges.size,
    edges: comparisons,
    nativeRelationships,
    officialRelationships,
  };
}

function topologyEdges(manifest) {
  const edges = new Map();
  for (const element of manifest?.elements ?? []) {
    const edgeId = element.edgeId;
    if (
      element.type !== 'path' ||
      typeof edgeId !== 'string' ||
      edgeId.length === 0
    ) {
      continue;
    }
    edges.set(edgeId, {
      edgeId,
      topologyPoints: validPoints(element.topologyPoints),
      bendPoints: validPoints(element.bendPoints),
      directionSequence: Array.isArray(element.directionSequence)
        ? element.directionSequence.filter((entry) => typeof entry === 'string')
        : [],
      sourcePortSide: element.sourcePortSide ?? null,
      destinationPortSide: element.destinationPortSide ?? null,
    });
  }
  return edges;
}

function validPoints(value) {
  if (!Array.isArray(value)) return [];
  return value.filter(
    (point) => Number.isFinite(point?.x) && Number.isFinite(point?.y),
  );
}

function topologyRelationships(edges) {
  const edgeList = [...edges.values()].sort(
    (left, right) => left.edgeId.localeCompare(right.edgeId),
  );
  const sharedTracks = new Set();
  const strictCrossings = new Set();
  for (let firstIndex = 0; firstIndex < edgeList.length; firstIndex += 1) {
    const first = edgeList[firstIndex];
    const firstSegments = segments(first);
    for (
      let secondIndex = firstIndex + 1;
      secondIndex < edgeList.length;
      secondIndex += 1
    ) {
      const second = edgeList[secondIndex];
      const secondSegments = segments(second);
      for (const firstSegment of firstSegments) {
        for (const secondSegment of secondSegments) {
          if (strictlyCross(firstSegment, secondSegment)) {
            strictCrossings.add(pairKey(first.edgeId, second.edgeId));
          }
          const sharedAxis = sharedTrackAxis(firstSegment, secondSegment);
          if (sharedAxis !== null) {
            sharedTracks.add(
              `${pairKey(first.edgeId, second.edgeId)}:${sharedAxis}`,
            );
          }
        }
      }
    }
  }

  const endpoints = edgeList.flatMap((edge) => [
    { id: `${edge.edgeId}:S`, point: edge.topologyPoints[0] },
    { id: `${edge.edgeId}:D`, point: edge.topologyPoints.at(-1) },
  ]).filter((entry) => entry.point !== undefined);
  const sharedEndpoints = new Set();
  for (let firstIndex = 0; firstIndex < endpoints.length; firstIndex += 1) {
    for (
      let secondIndex = firstIndex + 1;
      secondIndex < endpoints.length;
      secondIndex += 1
    ) {
      const first = endpoints[firstIndex];
      const second = endpoints[secondIndex];
      if (samePoint(first.point, second.point)) {
        sharedEndpoints.add(pairKey(first.id, second.id));
      }
    }
  }

  return {
    sharedTracks: [...sharedTracks].sort(),
    strictCrossings: [...strictCrossings].sort(),
    sharedEndpoints: [...sharedEndpoints].sort(),
  };
}

function segments(edge) {
  return edge.topologyPoints.slice(0, -1).map((start, index) => {
    const end = edge.topologyPoints[index + 1];
    const direction = edge.directionSequence[index] ?? null;
    return {
      start,
      end,
      horizontal: direction === 'L' || direction === 'R',
      vertical: direction === 'U' || direction === 'D',
    };
  });
}

function strictlyCross(first, second) {
  if (
    !(
      (first.horizontal && second.vertical) ||
      (first.vertical && second.horizontal)
    )
  ) {
    return false;
  }
  const horizontal = first.horizontal ? first : second;
  const vertical = first.horizontal ? second : first;
  const minimumX = Math.min(horizontal.start.x, horizontal.end.x);
  const maximumX = Math.max(horizontal.start.x, horizontal.end.x);
  const minimumY = Math.min(vertical.start.y, vertical.end.y);
  const maximumY = Math.max(vertical.start.y, vertical.end.y);
  return vertical.start.x > minimumX + EPSILON &&
    vertical.start.x < maximumX - EPSILON &&
    horizontal.start.y > minimumY + EPSILON &&
    horizontal.start.y < maximumY - EPSILON;
}

function sharedTrackAxis(first, second) {
  if (
    first.horizontal &&
    second.horizontal &&
    Math.abs(first.start.y - second.start.y) <= EPSILON &&
    overlapLength(
      first.start.x,
      first.end.x,
      second.start.x,
      second.end.x,
    ) >= MINIMUM_SHARED_TRACK
  ) {
    return 'H';
  }
  if (
    first.vertical &&
    second.vertical &&
    Math.abs(first.start.x - second.start.x) <= EPSILON &&
    overlapLength(
      first.start.y,
      first.end.y,
      second.start.y,
      second.end.y,
    ) >= MINIMUM_SHARED_TRACK
  ) {
    return 'V';
  }
  return null;
}

function overlapLength(firstStart, firstEnd, secondStart, secondEnd) {
  return Math.max(
    0,
    Math.min(Math.max(firstStart, firstEnd), Math.max(secondStart, secondEnd)) -
      Math.max(Math.min(firstStart, firstEnd), Math.min(secondStart, secondEnd)),
  );
}

function samePoint(first, second) {
  return Math.hypot(first.x - second.x, first.y - second.y) <= EPSILON;
}

function pairKey(first, second) {
  return first < second ? `${first}|${second}` : `${second}|${first}`;
}

function compareSet(nativeValues, officialValues, code, label, findings) {
  if (JSON.stringify(nativeValues) === JSON.stringify(officialValues)) return;
  findings.push(finding(
    code,
    `${label} differ: Native=${JSON.stringify(nativeValues)}; ` +
      `Official=${JSON.stringify(officialValues)}`,
  ));
}

function compareValue(nativeValue, officialValue, code, label, findings) {
  if (JSON.stringify(nativeValue) === JSON.stringify(officialValue)) return;
  findings.push(finding(
    code,
    `${label} differs: Native=${JSON.stringify(nativeValue)}; ` +
      `Official=${JSON.stringify(officialValue)}`,
  ));
}

function finding(code, message) {
  return { severity: 'error', code, message };
}

function edgeSummary(edge) {
  return {
    pointCount: edge.topologyPoints.length,
    bendCount: edge.bendPoints.length,
    directionSequence: edge.directionSequence,
    sourcePortSide: edge.sourcePortSide,
    destinationPortSide: edge.destinationPortSide,
  };
}
