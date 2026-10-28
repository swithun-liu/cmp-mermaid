package com.swithun.cmpmermaid.core

/**
 * Mermaid.js 12.0.0: generated Jison parsers -> o(k, v, o2, l).
 *
 * The generator supplies all keys in JavaScript numeric-property order, including
 * overrides. Typed immutable cells replace the upstream numeric action arrays.
 * See docs/upstream-jison-table-generation-map.md.
 */
internal fun <T : Any> buildJisonState(
    symbols: IntArray,
    defaultCell: T,
    overrides: Map<Int, T> = emptyMap(),
): Map<Int, T> {
    val result = LinkedHashMap<Int, T>(symbols.size)
    for (symbol in symbols) {
        result[symbol] = overrides[symbol] ?: defaultCell
    }
    return result
}
