const tokenPattern = /[MLZmlz]|[-+]?(?:\d+\.?\d*|\.\d+)(?:e[-+]?\d+)?/gi;

// Rough.js 4.6.6 generator.toPaths emits a solid rectangle as M/L vertices.
// SVG fills implicitly close its single open subpath. Curves, multiple
// subpaths and other polygons must not be treated as their bounding boxes.
export function isSvgRectanglePath(pathData) {
  if (typeof pathData !== 'string') return false;
  const tokens = pathData.match(tokenPattern) ?? [];
  if (pathData.replace(tokenPattern, '').replace(/[\s,]/g, '') !== '') {
    return false;
  }
  const vertices = [];
  for (let index = 0; index < tokens.length;) {
    const command = tokens[index++];
    if (command === 'Z' || command === 'z') {
      if (index !== tokens.length) return false;
      break;
    }
    if (command !== (vertices.length === 0 ? 'M' : 'L')) return false;
    const x = Number(tokens[index++]);
    const y = Number(tokens[index++]);
    if (!Number.isFinite(x) || !Number.isFinite(y)) return false;
    vertices.push({ x, y });
  }
  if (vertices.length === 5) {
    const first = vertices[0];
    const last = vertices.pop();
    if (first.x !== last.x || first.y !== last.y) return false;
  }
  if (vertices.length !== 4) return false;
  const xs = new Set(vertices.map(({ x }) => x));
  const ys = new Set(vertices.map(({ y }) => y));
  if (xs.size !== 2 || ys.size !== 2) return false;
  return vertices.every((point, index) => {
    const next = vertices[(index + 1) % vertices.length];
    return (point.x === next.x) !== (point.y === next.y);
  });
}
