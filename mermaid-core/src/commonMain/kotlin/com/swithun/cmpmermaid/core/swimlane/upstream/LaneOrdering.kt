package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

internal const val AutomaticLaneOrderingRestarts: Int = 8

internal data class WeightedLaneEdge(
    val first: String,
    val second: String,
    var weight: Int,
)

private data class LaneOrderCandidate(
    val order: List<String>,
    val cost: Int,
    val sourceDistance: Int,
)

// Mermaid.js 12.0.0: swimlanes/laneOrdering.ts -> hashString.
private fun hashString(input: String): Int {
    var hash = 2166136261u.toInt()
    input.forEach { character ->
        hash = hash xor character.code
        hash *= 16777619
    }
    return hash
}

private fun mulberry32(seed: Int): () -> Double {
    var state = seed
    return {
        state += 0x6d2b79f5
        var value = state
        value = (value xor (value ushr 15)) * (value or 1)
        value = value xor (value + (value xor (value ushr 7)) * (value or 61))
        (value xor (value ushr 14)).toUInt().toDouble() / 4294967296.0
    }
}

private fun deterministicShuffle(order: List<String>, seed: Int): List<String> {
    val shuffled = order.toMutableList()
    val random = mulberry32(seed)
    for (index in shuffled.lastIndex downTo 1) {
        val other = floor(random() * (index + 1)).toInt()
        val value = shuffled[index]
        shuffled[index] = shuffled[other]
        shuffled[other] = value
    }
    return shuffled
}

private fun sourceDistance(order: List<String>, sourceIndex: Map<String, Int>): Int =
    order.withIndex().sumOf { indexed -> abs(indexed.index - (sourceIndex[indexed.value] ?: indexed.index)) }

internal fun laneArrangementCost(
    order: List<String>,
    weights: List<WeightedLaneEdge>,
): Int {
    val position = order.withIndex()
        .associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }
    return weights.sumOf { edge ->
        val first = position[edge.first]
        val second = position[edge.second]
        if (first == null || second == null) 0 else edge.weight * abs(first - second)
    }
}

internal fun buildWeightedLaneEdges(graph: SwimlaneGraph): List<WeightedLaneEdge> {
    val sourceOrder = buildTopLaneOrder(graph)
    if (sourceOrder.size < 2) return emptyList()
    val sourceIndex = sourceOrder.withIndex()
        .associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }
    val topLaneOf = createTopLaneResolver(graph)
    val weights = linkedMapOf<String, WeightedLaneEdge>()

    graph.layout.edges.forEach { edge ->
        if (edge.isLayoutOnly) return@forEach
        val source = edge.start ?: return@forEach
        val destination = edge.end ?: return@forEach
        if (source !in graph.nodeById || destination !in graph.nodeById) return@forEach
        val firstLane = topLaneOf(source)
        val secondLane = topLaneOf(destination)
        if (firstLane == null || secondLane == null || firstLane == secondLane) return@forEach
        val firstIndex = sourceIndex[firstLane] ?: return@forEach
        val secondIndex = sourceIndex[secondLane] ?: return@forEach
        val first: String
        val second: String
        if (firstIndex <= secondIndex) {
            first = firstLane
            second = secondLane
        } else {
            first = secondLane
            second = firstLane
        }
        val key = "$first\u0000$second"
        val existing = weights[key]
        if (existing != null) {
            existing.weight++
        } else {
            weights[key] = WeightedLaneEdge(first, second, 1)
        }
    }
    return weights.values.toList()
}

private fun greedySwitch(
    startOrder: List<String>,
    weights: List<WeightedLaneEdge>,
    sourceIndex: Map<String, Int>,
): LaneOrderCandidate {
    val order = startOrder.toMutableList()
    var cost = laneArrangementCost(order, weights)
    var changed = true
    var sweeps = 0
    val maximumSweeps = max(1, order.size)
    while (changed && sweeps < maximumSweeps) {
        changed = false
        sweeps++
        for (index in 0 until order.lastIndex) {
            val value = order[index]
            order[index] = order[index + 1]
            order[index + 1] = value
            val nextCost = laneArrangementCost(order, weights)
            if (nextCost < cost) {
                cost = nextCost
                changed = true
            } else {
                val restore = order[index]
                order[index] = order[index + 1]
                order[index + 1] = restore
            }
        }
    }
    return LaneOrderCandidate(
        order = order,
        cost = cost,
        sourceDistance = sourceDistance(order, sourceIndex),
    )
}

private fun seedForRestart(
    sourceOrder: List<String>,
    weights: List<WeightedLaneEdge>,
    restartIndex: Int,
): Int {
    val weightSignature = weights
        .sortedWith { first, second ->
            val firstComparison = compareMermaidIds(first.first, second.first)
            if (firstComparison != 0) {
                firstComparison
            } else {
                compareMermaidIds(first.second, second.second)
            }
        }
        .joinToString("|") { edge -> "${edge.first}:${edge.second}:${edge.weight}" }
    return hashString("${sourceOrder.joinToString("|")}#$weightSignature#$restartIndex")
}

// Mermaid.js 12.0.0: swimlanes/laneOrdering.ts -> optimizeTopLaneOrder.
internal fun optimizeTopLaneOrder(
    graph: SwimlaneGraph,
    restarts: Int = AutomaticLaneOrderingRestarts,
): List<String> {
    val sourceOrder = buildTopLaneOrder(graph)
    if (sourceOrder.size < 2) return sourceOrder
    val weights = buildWeightedLaneEdges(graph)
    if (weights.isEmpty()) return sourceOrder

    val sourceIndex = sourceOrder.withIndex()
        .associateTo(linkedMapOf()) { indexed -> indexed.value to indexed.index }
    var best = greedySwitch(sourceOrder, weights, sourceIndex)
    repeat(max(0, restarts)) { restartIndex ->
        val candidate = greedySwitch(
            deterministicShuffle(
                sourceOrder,
                seedForRestart(sourceOrder, weights, restartIndex),
            ),
            weights,
            sourceIndex,
        )
        if (
            candidate.cost < best.cost ||
            (candidate.cost == best.cost && candidate.sourceDistance < best.sourceDistance)
        ) {
            best = candidate
        }
    }
    return best.order
}
