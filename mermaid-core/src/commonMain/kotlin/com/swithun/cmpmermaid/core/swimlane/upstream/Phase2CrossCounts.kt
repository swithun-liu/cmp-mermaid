package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

/**
 * Mermaid.js 12.0.0:
 * swimlanes/phase2.crossCounts.ts -> computeSubtreeCrossCounts.
 */
internal fun computeSubtreeCrossCounts(
    graph: SwimlaneGraph,
    rankOf: Map<String, Int>,
    tree: DrivingTree,
): Map<String, Map<String, Int>> {
    val nodes = graph.nodes.toList()
    val indexOf = nodes.withIndex()
        .associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }
    val nodeCount = nodes.size
    val parentIndex = MutableList(nodeCount) { -1 }
    val depth = MutableList(nodeCount) { 0 }
    val queue = mutableListOf<String>()
    val seen = mutableSetOf<String>()

    for (node in nodes) {
        val parentId = tree.parent[node]
        val index = indexOf[node] ?: continue
        if (parentId == null) {
            parentIndex[index] = -1
            depth[index] = 0
            if (seen.add(node)) queue += node
        }
    }

    while (queue.isNotEmpty()) {
        val current = queue.removeAt(0)
        val currentIndex = indexOf[current] ?: continue
        for (child in tree.children[current].orEmpty()) {
            if (!seen.add(child)) continue
            val childIndex = indexOf[child] ?: continue
            parentIndex[childIndex] = currentIndex
            depth[childIndex] = depth[currentIndex] + 1
            queue += child
        }
    }

    for (node in nodes) {
        if (seen.add(node)) {
            val index = indexOf[node] ?: continue
            parentIndex[index] = -1
            depth[index] = 0
        }
    }

    val maximumLog = max(1, ceil(log2(max(1, nodeCount).toDouble())).toInt() + 1)
    val up = MutableList(maximumLog) { MutableList(nodeCount) { -1 } }
    for (index in 0 until nodeCount) up[0][index] = parentIndex[index]
    for (level in 1 until maximumLog) {
        for (index in 0 until nodeCount) {
            val previous = up[level - 1][index]
            up[level][index] = if (previous == -1) -1 else up[level - 1][previous]
        }
    }

    fun lowestCommonAncestor(firstInput: Int, secondInput: Int): Int {
        if (firstInput == -1 || secondInput == -1) return -1
        var first = firstInput
        var second = secondInput
        if (depth[first] < depth[second]) {
            val swap = first
            first = second
            second = swap
        }
        val difference = depth[first] - depth[second]
        for (level in 0 until maximumLog) {
            if (((difference shr level) and 1) != 0) {
                first = up[level][first]
                if (first == -1) return -1
            }
        }
        if (first == second) return first
        for (level in maximumLog - 1 downTo 0) {
            val firstAncestor = up[level][first]
            val secondAncestor = up[level][second]
            if (firstAncestor == -1 || secondAncestor == -1) continue
            if (firstAncestor != secondAncestor) {
                first = firstAncestor
                second = secondAncestor
            }
        }
        return up[0][first]
    }

    val ownCounts = MutableList(nodeCount) { mutableMapOf<Int, Int>() }
    for (edge in graph.edges) {
        var source = edge.src
        var destination = edge.dst
        var sourceRank = rankOf[source] ?: continue
        var destinationRank = rankOf[destination] ?: continue
        if (sourceRank > destinationRank) {
            val nodeSwap = source
            source = destination
            destination = nodeSwap
            val rankSwap = sourceRank
            sourceRank = destinationRank
            destinationRank = rankSwap
        }
        if (sourceRank == destinationRank) continue
        val upperIndex = indexOf[source] ?: continue
        val lowerIndex = indexOf[destination] ?: continue
        val ancestor = lowestCommonAncestor(upperIndex, lowerIndex)
        if (ancestor == -1) continue
        val bucket = ownCounts[ancestor]
        for (layer in sourceRank until destinationRank) {
            bucket[layer] = (bucket[layer] ?: 0) + 1
        }
    }

    val crossCounts = linkedMapOf<String, MutableMap<String, Int>>()
    fun mergeInto(target: MutableMap<Int, Int>, source: Map<Int, Int>) {
        source.forEach { (layer, value) -> target[layer] = (target[layer] ?: 0) + value }
    }

    val visited = mutableSetOf<String>()
    fun visit(node: String): MutableMap<Int, Int> {
        val index = indexOf[node]
        visited += node
        val accumulator = index?.let { ownCounts[it].toMutableMap() } ?: mutableMapOf()
        for (child in tree.children[node].orEmpty()) {
            val childMap = visit(child)
            val parentLayer = rankOf[node]
            if (parentLayer != null) {
                val map = crossCounts.getOrPut(node) { linkedMapOf() }
                var value = childMap[parentLayer] ?: 0
                val childLayer = rankOf[child]
                if (childLayer != null && childLayer > parentLayer) value++
                map[child] = value
            }
            mergeInto(accumulator, childMap)
        }
        return accumulator
    }

    tree.roots.forEach { root -> if (root !in visited) visit(root) }
    nodes.forEach { node -> if (node !in visited) visit(node) }
    return crossCounts
}
