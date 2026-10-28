/**
 * Mermaid 12.0.0 generated Jison parsers: shared $V actions and o(k, v, o2, l).
 * This emitter retains every cell and JavaScript numeric-property key order.
 */
export function emitJisonStates(table, cellSource, chunkSize) {
  const cellType = cellSource(0).split('.')[0];
  const actions = new Map();
  const symbolSets = new Map();
  const rows = table.map((state) => {
    const entries = Object.entries(state);
    const counts = new Map();
    for (const [, cell] of entries) {
      const source = cellSource(cell);
      if (!actions.has(source)) actions.set(source, `action${actions.size}`);
      counts.set(source, (counts.get(source) ?? 0) + 1);
    }
    const repeated = [...counts.entries()].sort((a, b) => b[1] - a[1])[0];
    // Small or distinct rows stay explicit. No input-dependent parser decision
    // is introduced: this only chooses how identical table data is constructed.
    if (!repeated || repeated[1] < 3) return { entries };
    const key = entries.map(([symbol]) => symbol).join(', ');
    if (!symbolSets.has(key)) symbolSets.set(key, `symbols${symbolSets.size}`);
    return { entries, defaultSource: repeated[0], symbols: symbolSets.get(key) };
  });
  const lines = [
    '    // Mermaid.js 12.0.0: generated shared action arrays ($V*) and o(k, v, o2, l).',
  ];
  for (const [source, name] of actions) {
    lines.push(`    private val ${name}: ${cellType} = ${source}`);
  }
  for (const [symbols, name] of symbolSets) {
    lines.push(`    private val ${name} = intArrayOf(${symbols})`);
  }
  lines.push('');
  lines.push(`    val states: Array<Map<Int, ${cellType}>> = buildList {`);
  for (let start = 0; start < rows.length; start += chunkSize) {
    lines.push(`        addAll(stateChunk${start / chunkSize}())`);
  }
  lines.push('    }.toTypedArray()', '');
  for (let start = 0; start < rows.length; start += chunkSize) {
    lines.push(
      `    private fun stateChunk${start / chunkSize}(): Array<Map<Int, ${cellType}>> = arrayOf(`,
    );
    for (const row of rows.slice(start, start + chunkSize)) {
      if (row.entries.length === 0) {
        lines.push('        emptyMap(),');
        continue;
      }
      if (row.defaultSource) {
        const overrides = row.entries.filter(
          ([, cell]) => cellSource(cell) !== row.defaultSource,
        );
        const prefix = `buildJisonState(${row.symbols}, ${actions.get(row.defaultSource)}`;
        if (overrides.length === 0) {
          lines.push(`        ${prefix}),`);
          continue;
        }
        lines.push(`        ${prefix}, mapOf(`);
        for (const [symbol, cell] of overrides) {
          lines.push(`            ${symbol} to ${actions.get(cellSource(cell))},`);
        }
        lines.push('        )),');
      } else {
        lines.push('        mapOf(');
        for (const [symbol, cell] of row.entries) {
          lines.push(`            ${symbol} to ${actions.get(cellSource(cell))},`);
        }
        lines.push('        ),');
      }
    }
    lines.push('    )', '');
  }
  return lines;
}
