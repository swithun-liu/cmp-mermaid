package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Orthogonal routing stage corresponding to Dagre's normalized edge chains and
 * Mermaid's post-layout node-intersection routing.
 */
internal object FlowEdgeRouter {
    fun route(
        document: FlowchartDocument,
        nodeBounds: Map<String, SceneRect>,
        edgeLabelSizes: Map<Int, io.github.cmpmermaid.core.SceneSize>,
    ): Map<Int, RoutedEdge> {
        val outerRight = nodeBounds.values.maxOfOrNull(SceneRect::right) ?: 0f
        val outerBottom = nodeBounds.values.maxOfOrNull(SceneRect::bottom) ?: 0f
        val edgeDirections = FlowDirectionResolver.resolve(document)
        val startPorts = assignPorts(
            edges = document.edges,
            nodeBounds = nodeBounds,
            edgeDirections = edgeDirections,
            outgoing = true,
        )
        val endPorts = assignPorts(
            edges = document.edges,
            nodeBounds = nodeBounds,
            edgeDirections = edgeDirections,
            outgoing = false,
        )
        val parallelLanes = assignParallelLanes(document.edges)
        val routeLanes = assignRouteLanes(
            edges = document.edges,
            nodeBounds = nodeBounds,
            startPorts = startPorts,
            endPorts = endPorts,
            edgeDirections = edgeDirections,
        )

        return buildMap {
            document.edges.forEachIndexed { index, edge ->
                if (edge.invisible) {
                    return@forEachIndexed
                }
                val from = nodeBounds[edge.from] ?: return@forEachIndexed
                val to = nodeBounds[edge.to] ?: return@forEachIndexed
                val direction = edgeDirections[index] ?: document.direction
                val vertical = direction.isVertical()
                val start = startPorts[index] ?: anchor(from, direction, start = true)
                val end = endPorts[index] ?: anchor(to, direction, start = false)
                val forward = direction.isForward(start, end)
                val edgeLabelSize = edgeLabelSizes[index]
                val points = when {
                    edge.from == edge.to -> selfLoop(
                        bounds = from,
                        direction = direction,
                        laneOffset = parallelLanes[index]?.crossOffset ?: 0f,
                    )
                    vertical -> routeVertical(
                        start = start,
                        end = end,
                        forward = forward,
                        outerRight = outerRight,
                        parallelLane = parallelLanes[index],
                        routeLane = routeLanes[index],
                        edgeIndex = index,
                    )
                    else -> routeHorizontal(
                        start = start,
                        end = end,
                        forward = forward,
                        outerBottom = outerBottom,
                        parallelLane = parallelLanes[index],
                        routeLane = routeLanes[index],
                        edgeLabelPrimarySize = edgeLabelSize?.width,
                        edgeIndex = index,
                    )
                }.simplifyOrthogonal()
                put(
                    index,
                    RoutedEdge(
                        points = points,
                        labelAnchor = if (
                            !vertical &&
                            edgeLabelSize != null &&
                            forward &&
                            parallelLanes[index] == null &&
                            edge.from != edge.to
                        ) {
                            ScenePoint(
                                x = (start.x + end.x) / 2f + routeLanes[index].centeredOffset(),
                                y = end.y,
                            )
                        } else {
                            points.halfLengthPoint()
                        },
                    ),
                )
            }
        }
    }

    data class RoutedEdge(
        val points: List<ScenePoint>,
        val labelAnchor: ScenePoint,
    )

    private data class ParallelLane(
        val crossOffset: Float,
    )

    private data class RouteLane(
        val slot: Int,
        val count: Int,
        val crossingSlot: Int?,
        val crossingCount: Int,
    )

    private fun assignPorts(
        edges: List<FlowEdge>,
        nodeBounds: Map<String, SceneRect>,
        edgeDirections: Map<Int, FlowDirection>,
        outgoing: Boolean,
    ): Map<Int, ScenePoint> {
        val indexedEdges = edges.withIndex().filterNot { indexed -> indexed.value.invisible }
        val grouped = indexedEdges.groupBy { indexed ->
            val nodeId = if (outgoing) indexed.value.from else indexed.value.to
            nodeId to edgeDirections.getValue(indexed.index)
        }
        return buildMap {
            grouped.forEach { (key, group) ->
                val (nodeId, direction) = key
                val bounds = nodeBounds[nodeId] ?: return@forEach
                val vertical = direction.isVertical()
                val sorted = group.sortedWith(
                    compareBy<IndexedValue<FlowEdge>> { indexed ->
                        val otherId = if (outgoing) indexed.value.to else indexed.value.from
                        val other = nodeBounds[otherId]
                        if (vertical) other?.center?.x else other?.center?.y
                    }.thenBy { indexed -> indexed.index },
                )
                sorted.forEachIndexed { slot, indexed ->
                    val ratio = (slot + 1f) / (sorted.size + 1f)
                    put(
                        indexed.index,
                        if (vertical) {
                            ScenePoint(
                                x = bounds.left + bounds.width * ratio,
                                y = when (direction) {
                                    FlowDirection.TopToBottom -> if (outgoing) bounds.bottom else bounds.top
                                    FlowDirection.BottomToTop -> if (outgoing) bounds.top else bounds.bottom
                                    else -> bounds.center.y
                                },
                            )
                        } else {
                            ScenePoint(
                                x = when (direction) {
                                    FlowDirection.LeftToRight -> if (outgoing) bounds.right else bounds.left
                                    FlowDirection.RightToLeft -> if (outgoing) bounds.left else bounds.right
                                    else -> bounds.center.x
                                },
                                y = bounds.top + bounds.height * ratio,
                            )
                        },
                    )
                }
            }
        }
    }

    private fun assignParallelLanes(edges: List<FlowEdge>): Map<Int, ParallelLane> = buildMap {
        edges.withIndex()
            .filterNot { indexed -> indexed.value.invisible }
            .groupBy { indexed -> indexed.value.from to indexed.value.to }
            .values
            .filter { group -> group.size > 1 }
            .forEach { group ->
                group.forEachIndexed { slot, indexed ->
                    put(
                        indexed.index,
                        ParallelLane(
                            crossOffset = (slot - (group.size - 1) / 2f) * PARALLEL_LANE_SPACING,
                        ),
                    )
                }
            }
    }

    private fun assignRouteLanes(
        edges: List<FlowEdge>,
        nodeBounds: Map<String, SceneRect>,
        startPorts: Map<Int, ScenePoint>,
        endPorts: Map<Int, ScenePoint>,
        edgeDirections: Map<Int, FlowDirection>,
    ): Map<Int, RouteLane> {
        val visibleEdges = edges.withIndex().filter { indexed ->
            !indexed.value.invisible &&
                indexed.value.from != indexed.value.to &&
                indexed.value.from in nodeBounds &&
                indexed.value.to in nodeBounds
        }
        return buildMap {
            visibleEdges
                .groupBy { indexed ->
                    val from = nodeBounds.getValue(indexed.value.from).center
                    val to = nodeBounds.getValue(indexed.value.to).center
                    val direction = edgeDirections.getValue(indexed.index)
                    val vertical = direction.isVertical()
                    if (vertical) {
                        Triple(direction, from.y.roundToInt(), to.y.roundToInt())
                    } else {
                        Triple(direction, from.x.roundToInt(), to.x.roundToInt())
                    }
                }
                .values
                .forEach { group ->
                    val vertical = edgeDirections.getValue(group.first().index).isVertical()
                    val sorted = group.sortedWith(
                        compareBy<IndexedValue<FlowEdge>> { indexed ->
                            startPorts.getValue(indexed.index).cross(vertical)
                        }.thenBy { indexed ->
                            endPorts.getValue(indexed.index).cross(vertical)
                        }.thenBy { indexed -> indexed.index },
                    )
                    val crossingIndexes = sorted
                        .filter { candidate ->
                            sorted.any { other ->
                                candidate.index != other.index &&
                                    routesCross(
                                        firstStart = startPorts.getValue(candidate.index).cross(vertical),
                                        firstEnd = endPorts.getValue(candidate.index).cross(vertical),
                                        secondStart = startPorts.getValue(other.index).cross(vertical),
                                        secondEnd = endPorts.getValue(other.index).cross(vertical),
                                    )
                            }
                        }
                        .map(IndexedValue<FlowEdge>::index)
                    sorted.forEachIndexed { slot, indexed ->
                        val crossingSlot = crossingIndexes.indexOf(indexed.index).takeIf { it >= 0 }
                        put(
                            indexed.index,
                            RouteLane(
                                slot = slot,
                                count = sorted.size,
                                crossingSlot = crossingSlot,
                                crossingCount = crossingIndexes.size,
                            ),
                        )
                    }
                }
        }
    }

    private fun routesCross(
        firstStart: Float,
        firstEnd: Float,
        secondStart: Float,
        secondEnd: Float,
    ): Boolean = (firstStart - secondStart) * (firstEnd - secondEnd) < 0f

    private fun routeVertical(
        start: ScenePoint,
        end: ScenePoint,
        forward: Boolean,
        outerRight: Float,
        parallelLane: ParallelLane?,
        routeLane: RouteLane?,
        edgeIndex: Int,
    ): List<ScenePoint> {
        if (!forward) {
            val side = outerRight + OUTER_ROUTE_MARGIN + (edgeIndex % 4) * OUTER_ROUTE_SPACING
            return listOf(start, ScenePoint(side, start.y), ScenePoint(side, end.y), end)
        }
        if (parallelLane != null) {
            val direction = if (end.y >= start.y) 1f else -1f
            val stub = min(28f, abs(end.y - start.y) / 4f)
            val startStub = start.y + stub * direction
            val endStub = end.y - stub * direction
            val lane = (start.x + end.x) / 2f + parallelLane.crossOffset
            return listOf(
                start,
                ScenePoint(start.x, startStub),
                ScenePoint(lane, startStub),
                ScenePoint(lane, endStub),
                ScenePoint(end.x, endStub),
                end,
            )
        }
        if (start.x.isCloseTo(end.x)) {
            return listOf(start, end)
        }
        val middle = (start.y + end.y) / 2f + routeLane.centeredOffset()
        if (routeLane?.usesDogleg() != true) {
            return listOf(start, ScenePoint(start.x, middle), ScenePoint(end.x, middle), end)
        }
        val direction = if (end.y >= start.y) 1f else -1f
        val spread = routeSpread(abs(end.y - start.y))
        val startChannel = middle - spread * direction
        val endChannel = middle + spread * direction
        val cross = (start.x + end.x) / 2f + routeLane.crossingOffset()
        return listOf(
            start,
            ScenePoint(start.x, startChannel),
            ScenePoint(cross, startChannel),
            ScenePoint(cross, endChannel),
            ScenePoint(end.x, endChannel),
            end,
        )
    }

    private fun routeHorizontal(
        start: ScenePoint,
        end: ScenePoint,
        forward: Boolean,
        outerBottom: Float,
        parallelLane: ParallelLane?,
        routeLane: RouteLane?,
        edgeLabelPrimarySize: Float?,
        edgeIndex: Int,
    ): List<ScenePoint> {
        if (!forward) {
            val side = outerBottom + OUTER_ROUTE_MARGIN + (edgeIndex % 4) * OUTER_ROUTE_SPACING
            return listOf(start, ScenePoint(start.x, side), ScenePoint(end.x, side), end)
        }
        if (parallelLane != null) {
            val direction = if (end.x >= start.x) 1f else -1f
            val stub = min(28f, abs(end.x - start.x) / 4f)
            val startStub = start.x + stub * direction
            val endStub = end.x - stub * direction
            val lane = (start.y + end.y) / 2f + parallelLane.crossOffset
            return listOf(
                start,
                ScenePoint(startStub, start.y),
                ScenePoint(startStub, lane),
                ScenePoint(endStub, lane),
                ScenePoint(endStub, end.y),
                end,
            )
        }
        if (start.y.isCloseTo(end.y)) {
            return listOf(start, end)
        }
        val middle = (start.x + end.x) / 2f + routeLane.centeredOffset()
        if (edgeLabelPrimarySize != null) {
            val direction = if (end.x >= start.x) 1f else -1f
            val availableHalf = (abs(end.x - start.x) / 2f - LABEL_ROUTE_MARGIN)
                .coerceAtLeast(0f)
            val labelHalf = min(
                edgeLabelPrimarySize / 2f + LABEL_ROUTE_PADDING,
                availableHalf,
            )
            val startChannel = middle - labelHalf * direction
            return listOf(
                start,
                ScenePoint(startChannel, start.y),
                ScenePoint(startChannel, end.y),
                end,
            )
        }
        if (routeLane?.usesDogleg() != true) {
            return listOf(start, ScenePoint(middle, start.y), ScenePoint(middle, end.y), end)
        }
        val direction = if (end.x >= start.x) 1f else -1f
        val spread = routeSpread(abs(end.x - start.x))
        val startChannel = middle - spread * direction
        val endChannel = middle + spread * direction
        val cross = (start.y + end.y) / 2f + routeLane.crossingOffset()
        return listOf(
            start,
            ScenePoint(startChannel, start.y),
            ScenePoint(startChannel, cross),
            ScenePoint(endChannel, cross),
            ScenePoint(endChannel, end.y),
            end,
        )
    }

    private fun RouteLane?.centeredOffset(): Float {
        if (this == null || count < 2) {
            return 0f
        }
        return (slot - (count - 1) / 2f) * PRIMARY_LANE_SPACING
    }

    private fun RouteLane.usesDogleg(): Boolean =
        crossingSlot != null && crossingSlot % 2 == 1

    private fun RouteLane.crossingOffset(): Float {
        val slot = crossingSlot ?: return 0f
        return (slot - (crossingCount - 1) / 2f) * CROSSING_LANE_SPACING
    }

    private fun routeSpread(primaryDistance: Float): Float =
        min(DOGLEG_MAX_SPREAD, (primaryDistance * 0.18f).coerceAtLeast(DOGLEG_MIN_SPREAD))

    private fun selfLoop(
        bounds: SceneRect,
        direction: FlowDirection,
        laneOffset: Float,
    ): List<ScenePoint> = if (direction.isVertical()) {
        val span = selfLoopSpan(bounds.height)
        val depth = selfLoopDepth(bounds)
        val side = bounds.right + depth + laneOffset
        listOf(
            ScenePoint(bounds.right, bounds.center.y - span / 2f),
            ScenePoint(side, bounds.center.y - span / 2f),
            ScenePoint(side, bounds.center.y + span / 2f),
            ScenePoint(bounds.right, bounds.center.y + span / 2f),
        )
    } else {
        val span = selfLoopSpan(bounds.width)
        val depth = selfLoopDepth(bounds)
        val side = bounds.top - depth - laneOffset
        listOf(
            ScenePoint(bounds.center.x - span / 2f, bounds.top),
            ScenePoint(bounds.center.x - span / 2f, side),
            ScenePoint(bounds.center.x + span / 2f, side),
            ScenePoint(bounds.center.x + span / 2f, bounds.top),
        )
    }

    private fun selfLoopSpan(primarySize: Float): Float =
        max(SELF_LOOP_MIN_SPAN, primarySize * 0.35f)
            .coerceAtMost(min(SELF_LOOP_MAX_SPAN, primarySize * 0.8f))

    private fun selfLoopDepth(bounds: SceneRect): Float =
        (min(bounds.width, bounds.height) * 0.45f)
            .coerceIn(SELF_LOOP_MIN_DEPTH, SELF_LOOP_MAX_DEPTH)

    private fun anchor(
        bounds: SceneRect,
        direction: FlowDirection,
        start: Boolean,
    ): ScenePoint = when (direction) {
        FlowDirection.TopToBottom -> if (start) {
            ScenePoint(bounds.center.x, bounds.bottom)
        } else {
            ScenePoint(bounds.center.x, bounds.top)
        }
        FlowDirection.BottomToTop -> if (start) {
            ScenePoint(bounds.center.x, bounds.top)
        } else {
            ScenePoint(bounds.center.x, bounds.bottom)
        }
        FlowDirection.LeftToRight -> if (start) {
            ScenePoint(bounds.right, bounds.center.y)
        } else {
            ScenePoint(bounds.left, bounds.center.y)
        }
        FlowDirection.RightToLeft -> if (start) {
            ScenePoint(bounds.left, bounds.center.y)
        } else {
            ScenePoint(bounds.right, bounds.center.y)
        }
    }

    private fun FlowDirection.isVertical(): Boolean =
        this == FlowDirection.TopToBottom || this == FlowDirection.BottomToTop

    private fun FlowDirection.isForward(start: ScenePoint, end: ScenePoint): Boolean = when (this) {
        FlowDirection.TopToBottom -> end.y > start.y
        FlowDirection.BottomToTop -> end.y < start.y
        FlowDirection.LeftToRight -> end.x > start.x
        FlowDirection.RightToLeft -> end.x < start.x
    }

    private fun ScenePoint.cross(vertical: Boolean): Float = if (vertical) x else y

    private fun Float.isCloseTo(other: Float): Boolean = abs(this - other) < 0.5f

    private fun List<ScenePoint>.simplifyOrthogonal(): List<ScenePoint> {
        val distinct = buildList {
            this@simplifyOrthogonal.forEach { point ->
                if (lastOrNull() != point) {
                    add(point)
                }
            }
        }
        if (distinct.size < 3) {
            return distinct
        }
        return buildList {
            distinct.forEach { point ->
                while (size >= 2 && isCollinear(this[size - 2], last(), point)) {
                    removeAt(lastIndex)
                }
                add(point)
            }
        }
    }

    private fun isCollinear(first: ScenePoint, middle: ScenePoint, last: ScenePoint): Boolean =
        (first.x.isCloseTo(middle.x) && middle.x.isCloseTo(last.x)) ||
            (first.y.isCloseTo(middle.y) && middle.y.isCloseTo(last.y))

    private fun List<ScenePoint>.halfLengthPoint(): ScenePoint {
        if (isEmpty()) {
            return ScenePoint(0f, 0f)
        }
        val lengths = zipWithNext().map { (start, end) ->
            abs(end.x - start.x) + abs(end.y - start.y)
        }
        var remaining = lengths.sum() / 2f
        zipWithNext().forEachIndexed { index, (start, end) ->
            val length = lengths[index]
            if (remaining <= length && length > 0f) {
                val ratio = remaining / length
                return ScenePoint(
                    x = start.x + (end.x - start.x) * ratio,
                    y = start.y + (end.y - start.y) * ratio,
                )
            }
            remaining -= length
        }
        return last()
    }

    private const val PARALLEL_LANE_SPACING = 44f
    private const val PRIMARY_LANE_SPACING = 10f
    private const val CROSSING_LANE_SPACING = 12f
    private const val DOGLEG_MIN_SPREAD = 24f
    private const val DOGLEG_MAX_SPREAD = 44f
    private const val OUTER_ROUTE_MARGIN = 32f
    private const val OUTER_ROUTE_SPACING = 12f
    private const val SELF_LOOP_MIN_SPAN = 36f
    private const val SELF_LOOP_MAX_SPAN = 100f
    private const val SELF_LOOP_MIN_DEPTH = 24f
    private const val SELF_LOOP_MAX_DEPTH = 48f
    private const val LABEL_ROUTE_MARGIN = 12f
    private const val LABEL_ROUTE_PADDING = 12f
}
