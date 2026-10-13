package com.swithun.cmpmermaid.core.swimlane.upstream

import com.swithun.cmpmermaid.core.SceneShapeKind
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

private const val RouterEpsilon: Double = SwimlaneConfig.Precision.Epsilon
private const val NodePadding: Double = 8.0
private const val HorizontalPipeMargin: Double = 15.0
private const val VerticalPipeMargin: Double = 15.0
private const val RoutingMargin: Double = 25.0
private const val AnchorOffset: Double = 20.0
private const val TrackSpacing: Double = 10.0
private const val CrossingPenaltyValue: Double = 1000.0

private enum class OrthogonalSide {
    Top,
    Bottom,
    Left,
    Right,
}

private enum class Orientation {
    Horizontal,
    Vertical,
}

private data class LaneInfo(
    val id: String,
)

private data class SegmentRef(
    val edgeIndex: Int,
    val segmentIndex: Int,
    val from: Double,
    val to: Double,
)

private data class Track(
    val index: Int,
    var coordinate: Double,
    var segments: MutableList<SegmentRef>,
)

private data class Pipe(
    val id: String,
    val orientation: Orientation,
    var coordinate: Double,
    var spanMinimum: Double,
    var spanMaximum: Double,
    val tracks: MutableList<Track>,
)

private data class RoutedSegment(
    val edgeIndex: Int,
    val segmentIndex: Int,
    val orientation: Orientation,
    val pipe: Pipe,
    var trackIndex: Int,
    val from: Double,
    val to: Double,
)

private data class RoutedLine(
    val orientation: Orientation,
    val coordinate: Double,
    val from: Double,
    val to: Double,
)

private data class ObstacleRect(
    val nodeId: String,
    val minimumX: Double,
    val minimumY: Double,
    val maximumX: Double,
    val maximumY: Double,
    val visualXHalfExtent: Double,
)

private data class EdgeSideInfo(
    val edgeIndex: Int,
    val sourceId: String,
    val destinationId: String,
    var sourceSide: OrthogonalSide,
    val destinationSide: OrthogonalSide,
    val absoluteDx: Double,
    val absoluteDy: Double,
    val dxSign: Double,
    val dySign: Double,
)

private data class PortGroupKey(
    val nodeId: String,
    val side: OrthogonalSide,
    val role: EndpointRole,
)

private enum class EndpointRole {
    Source,
    Destination,
}

private data class PortMember(
    val edgeIndex: Int,
    val oppositeCoordinate: Double,
)

private data class EdgePorts(
    val sourcePort: SwimlanePoint,
    val destinationPort: SwimlanePoint,
    val sourceSide: OrthogonalSide,
    val destinationSide: OrthogonalSide,
)

private data class DestinationInfo(
    val destination: Double,
    val deviation: Double,
    val base: Double,
    val delta: Double,
)

/**
 * Mermaid.js 12.0.0:
 * orthogonalRouter/router.ts -> getKey
 *
 * JavaScript's toFixed(1) rounds the exact IEEE-754 value rather than the
 * already-rounded result of value * 10. The explicit half comparison keeps
 * cases such as 1.15 -> "1.1" and 1.25 -> "1.3" aligned with Mermaid.js.
 */
internal fun orthogonalRouterPointKey(point: SwimlanePoint): String =
    "${point.x.toJavaScriptFixedOne()},${point.y.toJavaScriptFixedOne()}"

private fun Double.toJavaScriptFixedOne(): String {
    if (isNaN()) return "NaN"
    if (this == Double.POSITIVE_INFINITY) return "Infinity"
    if (this == Double.NEGATIVE_INFINITY) return "-Infinity"
    if (this == 0.0) return "0.0"
    if (abs(this) >= 1.0e21) return toString()

    val absoluteValue = abs(this)
    val scaled = absoluteValue * 10.0
    val integerPart = floor(scaled).toLong()
    val rounded = when {
        scaled - integerPart.toDouble() < 0.5 -> integerPart
        scaled - integerPart.toDouble() > 0.5 -> integerPart + 1L
        absoluteValueIsAtOrAboveHalfDecimal(absoluteValue, integerPart) -> integerPart + 1L
        else -> integerPart
    }
    val signPrefix = if (this < 0.0) "-" else ""
    return "$signPrefix${rounded / 10L}.${rounded % 10L}"
}

private fun absoluteValueIsAtOrAboveHalfDecimal(
    value: Double,
    integerPart: Long,
): Boolean {
    val bits = value.toBits()
    val exponentBits = ((bits ushr 52) and 0x7ffL).toInt()
    if (exponentBits == 0) return false

    val fractionMask = (1L shl 52) - 1L
    val mantissa = ((1L shl 52) or (bits and fractionMask)).toULong()
    val binaryExponent = exponentBits - 1023 - 52
    val decimalNumerator = (integerPart.toULong() * 2uL) + 1uL
    val scaledMantissa = mantissa * 20uL
    return if (binaryExponent >= 0) {
        (scaledMantissa shl binaryExponent) >= decimalNumerator
    } else {
        scaledMantissa >= (decimalNumerator shl -binaryExponent)
    }
}

private fun chooseOrthogonalSide(
    node: SwimlaneNode,
    target: SwimlanePoint,
    fallback: OrthogonalSide,
): OrthogonalSide {
    val centerX = node.x ?: 0.0
    val centerY = node.y ?: 0.0
    val dx = target.x - centerX
    val dy = target.y - centerY
    val absoluteDx = abs(dx)
    val absoluteDy = abs(dy)
    if (absoluteDx < RouterEpsilon && absoluteDy < RouterEpsilon) return fallback
    if (absoluteDy > RouterEpsilon && absoluteDy * 3.0 >= absoluteDx) {
        return if (dy > 0.0) OrthogonalSide.Bottom else OrthogonalSide.Top
    }
    if (absoluteDx > RouterEpsilon) {
        return if (dx > 0.0) OrthogonalSide.Right else OrthogonalSide.Left
    }
    return fallback
}

private fun sharedLineEndpointCoordinate(
    line: RoutedLine,
    nextLine: RoutedLine,
): Double = if (
    abs(line.to - nextLine.from) < RouterEpsilon ||
    abs(line.to - nextLine.to) < RouterEpsilon
) {
    line.to
} else {
    line.from
}

private fun pointOnLine(line: RoutedLine, along: Double): SwimlanePoint =
    if (line.orientation == Orientation.Vertical) {
        SwimlanePoint(line.coordinate, along)
    } else {
        SwimlanePoint(along, line.coordinate)
    }

/**
 * Mermaid.js 12.0.0:
 * swimlanes/orthogonalRouter/router.ts -> routeEdgesOrthogonal.
 */
internal fun routeEdgesOrthogonal(
    data: SwimlaneLayoutData,
    direction: SwimlaneDirection = data.direction,
): SwimlaneLayoutData {
    val nodes = data.nodes
    val edges = data.edges.filterNot(SwimlaneEdge::isLayoutOnly)
    val nodeById = nodes.associateByTo(linkedMapOf(), SwimlaneNode::id)
    val laneByNodeId = mutableMapOf<String, LaneInfo>()
    val pipes = mutableListOf<Pipe>()
    val isLeftToRight = direction == SwimlaneDirection.LR

    val topLevelGroups = nodes.filter { node -> node.isGroup && node.parentId == null }
    fun assignLane(node: SwimlaneNode, lane: LaneInfo) {
        laneByNodeId[node.id] = lane
        nodes.filter { child -> child.parentId == node.id }.forEach { child ->
            assignLane(child, lane)
        }
    }
    topLevelGroups.forEach { group -> assignLane(group, LaneInfo(group.id)) }

    val obstacles = nodes
        .filter { node -> !node.isGroup && !node.isEdgeLabel }
        .map { node ->
            val centerX = node.x ?: 0.0
            val centerY = node.y ?: 0.0
            ObstacleRect(
                nodeId = node.id,
                minimumX = centerX - node.width / 2.0 - NodePadding,
                maximumX = centerX + node.width / 2.0 + NodePadding,
                minimumY = centerY - node.height / 2.0 - NodePadding,
                maximumY = centerY + node.height / 2.0 + NodePadding,
                visualXHalfExtent = if (isLeftToRight) {
                    node.height / 2.0 + NodePadding
                } else {
                    node.width / 2.0 + NodePadding
                },
            )
        }

    fun getOrAddPipe(
        orientation: Orientation,
        coordinate: Double,
        spanMinimum: Double,
        spanMaximum: Double,
    ): Pipe {
        var pipe = pipes.find { candidate ->
            candidate.orientation == orientation && abs(candidate.coordinate - coordinate) < 1.0
        }
        if (pipe == null) {
            pipe = Pipe(
                id = "pipe-${orientation.name.lowercase()}-${coordinate.toInt()}",
                orientation = orientation,
                coordinate = coordinate,
                spanMinimum = spanMinimum,
                spanMaximum = spanMaximum,
                tracks = mutableListOf(),
            )
            pipes += pipe
        }
        pipe.spanMinimum = min(pipe.spanMinimum, spanMinimum)
        pipe.spanMaximum = max(pipe.spanMaximum, spanMaximum)
        return pipe
    }

    fun portForSide(node: SwimlaneNode, side: OrthogonalSide): SwimlanePoint {
        val centerX = node.x ?: 0.0
        val centerY = node.y ?: 0.0
        return when (side) {
            OrthogonalSide.Top -> SwimlanePoint(centerX, centerY - node.height / 2.0)
            OrthogonalSide.Bottom -> SwimlanePoint(centerX, centerY + node.height / 2.0)
            OrthogonalSide.Left -> SwimlanePoint(centerX - node.width / 2.0, centerY)
            OrthogonalSide.Right -> SwimlanePoint(centerX + node.width / 2.0, centerY)
        }
    }

    fun getOrthogonalPort(
        node: SwimlaneNode,
        target: SwimlanePoint,
        isSource: Boolean,
    ): SwimlanePoint = portForSide(
        node,
        chooseOrthogonalSide(
            node,
            target,
            if (isSource) OrthogonalSide.Bottom else OrthogonalSide.Top,
        ),
    )

    val allRoutedSegments = mutableListOf<RoutedSegment>()
    val edgeSegmentIndices = MutableList(edges.size) { mutableListOf<Int>() }
    val straightIntraLaneEdges = mutableSetOf<Int>()

    fun crossingPenalty(edgeIndex: Int, from: SwimlanePoint, to: SwimlanePoint): Double {
        if (allRoutedSegments.isEmpty()) return 0.0
        val horizontal = abs(from.y - to.y) < RouterEpsilon
        val vertical = abs(from.x - to.x) < RouterEpsilon
        if (!horizontal && !vertical) return 0.0
        var penalties = 0.0
        if (horizontal) {
            val minimumX = min(from.x, to.x) - RouterEpsilon
            val maximumX = max(from.x, to.x) + RouterEpsilon
            if (maximumX <= minimumX) return 0.0
            allRoutedSegments.forEach { segment ->
                if (
                    segment.edgeIndex != edgeIndex &&
                    segment.orientation == Orientation.Vertical &&
                    segment.pipe.coordinate in minimumX..maximumX &&
                    segment.from - RouterEpsilon <= from.y &&
                    segment.to + RouterEpsilon >= from.y
                ) {
                    penalties += CrossingPenaltyValue
                }
            }
        } else {
            val minimumY = min(from.y, to.y) - RouterEpsilon
            val maximumY = max(from.y, to.y) + RouterEpsilon
            if (maximumY <= minimumY) return 0.0
            allRoutedSegments.forEach { segment ->
                if (
                    segment.edgeIndex != edgeIndex &&
                    segment.orientation == Orientation.Horizontal &&
                    segment.pipe.coordinate in minimumY..maximumY &&
                    segment.from - RouterEpsilon <= from.x &&
                    segment.to + RouterEpsilon >= from.x
                ) {
                    penalties += CrossingPenaltyValue
                }
            }
        }
        return penalties
    }

    val routingOrder = edges.indices.sortedWith { first, second ->
        fun score(index: Int): List<Double> {
            val edge = edges[index]
            val source = edge.start?.let(nodeById::get)
            val destination = edge.end?.let(nodeById::get)
            val sourceLane = edge.start?.let(laneByNodeId::get)
            val destinationLane = edge.end?.let(laneByNodeId::get)
            val crossLane = if (
                sourceLane != null &&
                destinationLane != null &&
                sourceLane.id != destinationLane.id
            ) {
                1.0
            } else {
                0.0
            }
            val dx = if (source != null && destination != null) {
                abs((destination.x ?: 0.0) - (source.x ?: 0.0))
            } else {
                0.0
            }
            val dy = if (source != null && destination != null) {
                abs((destination.y ?: 0.0) - (source.y ?: 0.0))
            } else {
                0.0
            }
            return listOf(crossLane, dx + dy)
        }
        val firstScore = score(first)
        val secondScore = score(second)
        when {
            firstScore[0] != secondScore[0] -> secondScore[0].compareTo(firstScore[0])
            abs(firstScore[1] - secondScore[1]) > 1.0 ->
                firstScore[1].compareTo(secondScore[1])
            else -> first - second
        }
    }

    fun isSegmentBlocked(
        first: SwimlanePoint,
        second: SwimlanePoint,
        excludedSource: String? = null,
        excludedDestination: String? = null,
    ): Boolean {
        val minimumX = min(first.x, second.x)
        val maximumX = max(first.x, second.x)
        val minimumY = min(first.y, second.y)
        val maximumY = max(first.y, second.y)
        return obstacles.any { obstacle ->
            if (
                obstacle.nodeId == excludedSource ||
                obstacle.nodeId == excludedDestination
            ) {
                false
            } else if (abs(first.x - second.x) > RouterEpsilon) {
                obstacle.minimumY < first.y &&
                    obstacle.maximumY > first.y &&
                    obstacle.maximumX > minimumX &&
                    obstacle.minimumX < maximumX
            } else {
                obstacle.minimumX < first.x &&
                    obstacle.maximumX > first.x &&
                    obstacle.maximumY > minimumY &&
                    obstacle.minimumY < maximumY
            }
        }
    }

    val portGroups = linkedMapOf<PortGroupKey, MutableList<PortMember>>()
    val incidentEdgeTotals = mutableMapOf<String, Int>()
    edges.forEach { edge ->
        val source = edge.start
        val destination = edge.end
        if (source == null || destination == null || source == destination) return@forEach
        incidentEdgeTotals[source] = (incidentEdgeTotals[source] ?: 0) + 1
        incidentEdgeTotals[destination] = (incidentEdgeTotals[destination] ?: 0) + 1
    }

    fun determineSide(node: SwimlaneNode, target: SwimlanePoint): OrthogonalSide =
        chooseOrthogonalSide(node, target, OrthogonalSide.Bottom)

    val sideInfoByIndex = linkedMapOf<Int, EdgeSideInfo>()
    edges.forEachIndexed { index, edge ->
        val sourceId = edge.start ?: return@forEachIndexed
        val destinationId = edge.end ?: return@forEachIndexed
        if (sourceId == destinationId || !edge.points.isNullOrEmpty()) return@forEachIndexed
        val source = nodeById[sourceId] ?: return@forEachIndexed
        val destination = nodeById[destinationId] ?: return@forEachIndexed
        val dx = (destination.x ?: 0.0) - (source.x ?: 0.0)
        val dy = (destination.y ?: 0.0) - (source.y ?: 0.0)
        sideInfoByIndex[index] = EdgeSideInfo(
            edgeIndex = index,
            sourceId = sourceId,
            destinationId = destinationId,
            sourceSide = determineSide(
                source,
                SwimlanePoint(destination.x ?: 0.0, destination.y ?: 0.0),
            ),
            destinationSide = determineSide(
                destination,
                SwimlanePoint(source.x ?: 0.0, source.y ?: 0.0),
            ),
            absoluteDx = abs(dx),
            absoluteDy = abs(dy),
            dxSign = dx.sign,
            dySign = dy.sign,
        )
    }

    fun preferenceStrength(info: EdgeSideInfo): Double = when (info.sourceSide) {
        OrthogonalSide.Top,
        OrthogonalSide.Bottom,
        -> if (info.absoluteDx == 0.0) Double.POSITIVE_INFINITY else info.absoluteDy / info.absoluteDx
        OrthogonalSide.Left,
        OrthogonalSide.Right,
        -> if (info.absoluteDy == 0.0) Double.POSITIVE_INFINITY else info.absoluteDx / info.absoluteDy
    }

    fun secondarySide(info: EdgeSideInfo): OrthogonalSide = when (info.sourceSide) {
        OrthogonalSide.Top,
        OrthogonalSide.Bottom,
        -> if (info.dxSign >= 0.0) OrthogonalSide.Right else OrthogonalSide.Left
        OrthogonalSide.Left,
        OrthogonalSide.Right,
        -> if (info.dySign >= 0.0) OrthogonalSide.Bottom else OrthogonalSide.Top
    }

    val sourceSideGroups = linkedMapOf<Pair<String, OrthogonalSide>, MutableList<EdgeSideInfo>>()
    sideInfoByIndex.values.forEach { info ->
        sourceSideGroups.getOrPut(info.sourceId to info.sourceSide) { mutableListOf() } += info
    }
    val sideLoad = mutableMapOf<Pair<String, OrthogonalSide>, Int>()
    sideInfoByIndex.values.forEach { info ->
        val sourceKey = info.sourceId to info.sourceSide
        val destinationKey = info.destinationId to info.destinationSide
        sideLoad[sourceKey] = (sideLoad[sourceKey] ?: 0) + 1
        sideLoad[destinationKey] = (sideLoad[destinationKey] ?: 0) + 1
    }
    sourceSideGroups.values.forEach { group ->
        if (group.size < 2) return@forEach
        group.sortWith { first, second ->
            val firstStrength = preferenceStrength(first)
            val secondStrength = preferenceStrength(second)
            if (abs(firstStrength - secondStrength) > 1e-9) {
                secondStrength.compareTo(firstStrength)
            } else {
                first.edgeIndex - second.edgeIndex
            }
        }
        for (index in 1 until group.size) {
            val info = group[index]
            val secondary = secondarySide(info)
            val primaryKey = info.sourceId to info.sourceSide
            val secondaryKey = info.sourceId to secondary
            val primaryLoad = sideLoad[primaryKey] ?: 0
            val secondaryLoad = sideLoad[secondaryKey] ?: 0
            if (secondaryLoad >= primaryLoad) continue
            sideLoad[primaryKey] = primaryLoad - 1
            sideLoad[secondaryKey] = secondaryLoad + 1
            info.sourceSide = secondary
        }
    }

    fun isDiamond(node: SwimlaneNode?): Boolean = node?.shape == SceneShapeKind.Diamond
    val incomingSidesByNode = mutableMapOf<String, MutableSet<OrthogonalSide>>()
    sideInfoByIndex.values.forEach { info ->
        incomingSidesByNode.getOrPut(info.destinationId) { mutableSetOf() } += info.destinationSide
    }
    sideInfoByIndex.values.forEach { info ->
        if (!isDiamond(nodeById[info.sourceId])) return@forEach
        val incomingSides = incomingSidesByNode[info.sourceId] ?: return@forEach
        if (info.sourceSide !in incomingSides) return@forEach
        val secondary = secondarySide(info)
        val secondaryKey = info.sourceId to secondary
        if (secondary in incomingSides || (sideLoad[secondaryKey] ?: 0) > 0) return@forEach
        val primaryKey = info.sourceId to info.sourceSide
        sideLoad[primaryKey] = max(0, (sideLoad[primaryKey] ?: 0) - 1)
        sideLoad[secondaryKey] = 1
        info.sourceSide = secondary
    }

    sideInfoByIndex.values.forEach { info ->
        val source = nodeById.getValue(info.sourceId)
        val destination = nodeById.getValue(info.destinationId)
        val sourceKey = PortGroupKey(
            info.sourceId,
            info.sourceSide,
            EndpointRole.Source,
        )
        val destinationKey = PortGroupKey(
            info.destinationId,
            info.destinationSide,
            EndpointRole.Destination,
        )
        val destinationCoordinate = when (info.sourceSide) {
            OrthogonalSide.Top,
            OrthogonalSide.Bottom,
            -> destination.x ?: 0.0
            else -> destination.y ?: 0.0
        }
        val sourceCoordinate = when (info.destinationSide) {
            OrthogonalSide.Top,
            OrthogonalSide.Bottom,
            -> source.x ?: 0.0
            else -> source.y ?: 0.0
        }
        portGroups.getOrPut(sourceKey) { mutableListOf() } +=
            PortMember(info.edgeIndex, destinationCoordinate)
        portGroups.getOrPut(destinationKey) { mutableListOf() } +=
            PortMember(info.edgeIndex, sourceCoordinate)
    }

    val portOffsets = mutableMapOf<Pair<Int, EndpointRole>, Double>()
    portGroups.forEach { (key, group) ->
        if (group.size < 2) return@forEach
        group.sortBy(PortMember::oppositeCoordinate)
        val node = nodeById[key.nodeId] ?: return@forEach
        val verticalSide = key.side == OrthogonalSide.Left || key.side == OrthogonalSide.Right
        val sideLength = if (verticalSide) node.height else node.width
        val effectiveLength = if (isDiamond(node)) sideLength * 0.3 else sideLength
        val spacing = min(20.0, max(8.0, effectiveLength / (group.size + 1)))
        val startOffset = -(spacing * (group.size - 1)) / 2.0
        group.forEachIndexed { index, member ->
            portOffsets[member.edgeIndex to key.role] = startOffset + index * spacing
        }
    }

    fun edgeHasLabelNode(edgeIndex: Int): Boolean = edges.getOrNull(edgeIndex)?.labelNodeId != null
    fun faceHasLabelNode(nodeId: String?, side: OrthogonalSide): Boolean {
        if (nodeId == null) return false
        return listOf(EndpointRole.Source, EndpointRole.Destination).any { role ->
            portGroups[PortGroupKey(nodeId, side, role)].orEmpty().any { member ->
                edgeHasLabelNode(member.edgeIndex)
            }
        }
    }

    fun applyPortOffset(
        basePort: SwimlanePoint,
        side: OrthogonalSide,
        offset: Double,
    ): SwimlanePoint = when (side) {
        OrthogonalSide.Top,
        OrthogonalSide.Bottom,
        -> basePort.copy(x = basePort.x + offset)
        OrthogonalSide.Left,
        OrthogonalSide.Right,
        -> basePort.copy(y = basePort.y + offset)
    }

    fun portsForEdge(
        edgeIndex: Int,
        source: SwimlaneNode,
        destination: SwimlaneNode,
    ): EdgePorts {
        val sideInfo = sideInfoByIndex[edgeIndex]
        val sourceTarget = SwimlanePoint(destination.x ?: 0.0, destination.y ?: 0.0)
        val destinationTarget = SwimlanePoint(source.x ?: 0.0, source.y ?: 0.0)
        val sourceSide = sideInfo?.sourceSide ?: determineSide(source, sourceTarget)
        val destinationSide =
            sideInfo?.destinationSide ?: determineSide(destination, destinationTarget)
        var sourcePort = if (sideInfo != null) {
            portForSide(source, sideInfo.sourceSide)
        } else {
            getOrthogonalPort(source, sourceTarget, true)
        }
        var destinationPort = if (sideInfo != null) {
            portForSide(destination, sideInfo.destinationSide)
        } else {
            getOrthogonalPort(destination, destinationTarget, false)
        }
        portOffsets[edgeIndex to EndpointRole.Source]?.let { offset ->
            sourcePort = applyPortOffset(sourcePort, sourceSide, offset)
        }
        portOffsets[edgeIndex to EndpointRole.Destination]?.let { offset ->
            destinationPort = applyPortOffset(destinationPort, destinationSide, offset)
        }
        return EdgePorts(sourcePort, destinationPort, sourceSide, destinationSide)
    }

    for (edgeIndex in routingOrder) {
        val edge = edges[edgeIndex]
        val sourceId = edge.start ?: continue
        val destinationId = edge.end ?: continue
        if (!edge.points.isNullOrEmpty() || sourceId == destinationId) continue
        val source = nodeById[sourceId] ?: continue
        val destination = nodeById[destinationId] ?: continue
        val ports = portsForEdge(edgeIndex, source, destination)
        val sourcePort = ports.sourcePort
        val destinationPort = ports.destinationPort
        var sourceAnchor = sourcePort
        var destinationAnchor = destinationPort
        val sourcePortVertical =
            ports.sourceSide == OrthogonalSide.Top || ports.sourceSide == OrthogonalSide.Bottom
        val destinationPortVertical =
            ports.destinationSide == OrthogonalSide.Top ||
                ports.destinationSide == OrthogonalSide.Bottom

        sourceAnchor = if (sourcePortVertical) {
            sourceAnchor.copy(
                y = if (sourcePort.y > (source.y ?: 0.0)) {
                    sourcePort.y + AnchorOffset
                } else {
                    sourcePort.y - AnchorOffset
                },
            )
        } else {
            sourceAnchor.copy(
                x = if (sourcePort.x > (source.x ?: 0.0)) {
                    sourcePort.x + AnchorOffset
                } else {
                    sourcePort.x - AnchorOffset
                },
            )
        }
        destinationAnchor = if (destinationPortVertical) {
            destinationAnchor.copy(
                y = if (destinationPort.y > (destination.y ?: 0.0)) {
                    destinationPort.y + AnchorOffset
                } else {
                    destinationPort.y - AnchorOffset
                },
            )
        } else {
            destinationAnchor.copy(
                x = if (destinationPort.x > (destination.x ?: 0.0)) {
                    destinationPort.x + AnchorOffset
                } else {
                    destinationPort.x - AnchorOffset
                },
            )
        }

        fun pointInObstacle(point: SwimlanePoint): ObstacleRect? = obstacles.find { obstacle ->
            obstacle.nodeId != sourceId &&
                obstacle.nodeId != destinationId &&
                point.x > obstacle.minimumX &&
                point.x < obstacle.maximumX &&
                point.y > obstacle.minimumY &&
                point.y < obstacle.maximumY
        }

        data class Detour(
            val x: Double,
            val y: Double,
            val leavesPositiveSide: Boolean,
        )

        fun obstacleDetour(
            port: SwimlanePoint,
            node: SwimlaneNode,
            opposite: SwimlaneNode,
            obstacle: ObstacleRect,
            portIsVertical: Boolean,
        ): Detour {
            if (portIsVertical) {
                val leavesPositiveSide = port.y > (node.y ?: 0.0)
                val goRight = (opposite.x ?: 0.0) >= port.x
                return Detour(
                    x = if (goRight) {
                        obstacle.maximumX + HorizontalPipeMargin
                    } else {
                        obstacle.minimumX - HorizontalPipeMargin
                    },
                    y = if (leavesPositiveSide) {
                        obstacle.maximumY + VerticalPipeMargin
                    } else {
                        obstacle.minimumY - VerticalPipeMargin
                    },
                    leavesPositiveSide = leavesPositiveSide,
                )
            }
            val leavesPositiveSide = port.x > (node.x ?: 0.0)
            val goDown = (opposite.y ?: 0.0) >= port.y
            return Detour(
                x = if (leavesPositiveSide) {
                    obstacle.maximumX + HorizontalPipeMargin
                } else {
                    obstacle.minimumX - HorizontalPipeMargin
                },
                y = if (goDown) {
                    obstacle.maximumY + VerticalPipeMargin
                } else {
                    obstacle.minimumY - VerticalPipeMargin
                },
                leavesPositiveSide = leavesPositiveSide,
            )
        }

        val sourceHandleWaypoints = mutableListOf<SwimlanePoint>()
        pointInObstacle(sourceAnchor)?.let { obstacle ->
            val detour = obstacleDetour(
                sourcePort,
                source,
                destination,
                obstacle,
                sourcePortVertical,
            )
            sourceAnchor = SwimlanePoint(detour.x, detour.y)
            if (sourcePortVertical) {
                val gapY = if (detour.leavesPositiveSide) {
                    min(obstacle.minimumY - 2.0, sourcePort.y + AnchorOffset)
                } else {
                    max(obstacle.maximumY + 2.0, sourcePort.y - AnchorOffset)
                }
                sourceHandleWaypoints += SwimlanePoint(sourcePort.x, gapY)
                sourceHandleWaypoints += SwimlanePoint(detour.x, gapY)
                sourceHandleWaypoints += SwimlanePoint(detour.x, detour.y)
            } else {
                val gapX = if (detour.leavesPositiveSide) {
                    min(obstacle.minimumX - 2.0, sourcePort.x + AnchorOffset)
                } else {
                    max(obstacle.maximumX + 2.0, sourcePort.x - AnchorOffset)
                }
                sourceHandleWaypoints += SwimlanePoint(gapX, sourcePort.y)
                sourceHandleWaypoints += SwimlanePoint(gapX, detour.y)
                sourceHandleWaypoints += SwimlanePoint(detour.x, detour.y)
            }
        }

        val destinationHandleWaypoints = mutableListOf<SwimlanePoint>()
        pointInObstacle(destinationAnchor)?.let { obstacle ->
            val detour = obstacleDetour(
                destinationPort,
                destination,
                source,
                obstacle,
                destinationPortVertical,
            )
            destinationAnchor = SwimlanePoint(detour.x, detour.y)
            destinationHandleWaypoints += SwimlanePoint(detour.x, detour.y)
            if (destinationPortVertical) {
                destinationHandleWaypoints += SwimlanePoint(destinationPort.x, detour.y)
            } else {
                destinationHandleWaypoints += SwimlanePoint(detour.x, destinationPort.y)
            }
        }

        if (sourceHandleWaypoints.isEmpty() && destinationHandleWaypoints.isEmpty()) {
            val anchorsSameX = abs(sourceAnchor.x - destinationAnchor.x) < HorizontalPipeMargin
            val anchorsSameY = abs(sourceAnchor.y - destinationAnchor.y) < HorizontalPipeMargin
            val hasPortOffset =
                edgeIndex to EndpointRole.Source in portOffsets ||
                edgeIndex to EndpointRole.Destination in portOffsets
            fun faceTotal(nodeId: String, side: OrthogonalSide): Int =
                portGroups[PortGroupKey(nodeId, side, EndpointRole.Source)].orEmpty().size +
                    portGroups[PortGroupKey(nodeId, side, EndpointRole.Destination)].orEmpty().size

            val sourceFaceTotal = faceTotal(sourceId, ports.sourceSide)
            val destinationFaceTotal = faceTotal(destinationId, ports.destinationSide)
            val faceContested = sourceFaceTotal > 1 || destinationFaceTotal > 1
            val contestedFaceHasLabel =
                (sourceFaceTotal > 1 && faceHasLabelNode(sourceId, ports.sourceSide)) ||
                (
                    destinationFaceTotal > 1 &&
                        faceHasLabelNode(destinationId, ports.destinationSide)
                    )
            val simpleContestedStraight =
                faceContested &&
                    !contestedFaceHasLabel &&
                    (sourceFaceTotal <= 1 || (incidentEdgeTotals[sourceId] ?: 0) <= 2) &&
                    (
                        destinationFaceTotal <= 1 ||
                            (incidentEdgeTotals[destinationId] ?: 0) <= 2
                        )
            if (
                (anchorsSameX || anchorsSameY) &&
                !hasPortOffset &&
                (!faceContested || simpleContestedStraight) &&
                !isSegmentBlocked(sourcePort, destinationPort, sourceId, destinationId)
            ) {
                edge.points = mutableListOf(
                    sourcePort,
                    sourceAnchor,
                    destinationAnchor,
                    destinationPort,
                )
                straightIntraLaneEdges += edgeIndex
                val orientation = if (anchorsSameY) Orientation.Horizontal else Orientation.Vertical
                val coordinate = if (anchorsSameY) sourcePort.y else sourcePort.x
                val from = if (anchorsSameY) {
                    min(sourcePort.x, destinationPort.x)
                } else {
                    min(sourcePort.y, destinationPort.y)
                }
                val to = if (anchorsSameY) {
                    max(sourcePort.x, destinationPort.x)
                } else {
                    max(sourcePort.y, destinationPort.y)
                }
                val pipe = Pipe(
                    id = "fast-path-${orientation.name.lowercase()}-${coordinate.toInt()}-$edgeIndex",
                    orientation = orientation,
                    coordinate = coordinate,
                    spanMinimum = from,
                    spanMaximum = to,
                    tracks = mutableListOf(),
                )
                allRoutedSegments += RoutedSegment(
                    edgeIndex = edgeIndex,
                    segmentIndex = 0,
                    orientation = orientation,
                    pipe = pipe,
                    trackIndex = 0,
                    from = from,
                    to = to,
                )
                continue
            }
        }

        val sourcePipe = getOrAddPipe(
            Orientation.Vertical,
            sourceAnchor.x,
            sourceAnchor.y,
            sourceAnchor.y,
        )
        sourceAnchor = sourceAnchor.copy(x = sourcePipe.coordinate)
        val destinationPipe = getOrAddPipe(
            Orientation.Vertical,
            destinationAnchor.x,
            destinationAnchor.y,
            destinationAnchor.y,
        )
        destinationAnchor = destinationAnchor.copy(x = destinationPipe.coordinate)

        var boundingMinimumX = min(sourceAnchor.x, destinationAnchor.x) - 50.0
        var boundingMaximumX = max(sourceAnchor.x, destinationAnchor.x) + 50.0
        var boundingMinimumY = min(sourceAnchor.y, destinationAnchor.y) - 50.0
        var boundingMaximumY = max(sourceAnchor.y, destinationAnchor.y) + 50.0
        obstacles.forEach { obstacle ->
            val blocksPath =
                obstacle.minimumX < max(sourceAnchor.x, destinationAnchor.x) &&
                    obstacle.maximumX > min(sourceAnchor.x, destinationAnchor.x) &&
                    obstacle.minimumY < max(sourceAnchor.y, destinationAnchor.y) &&
                    obstacle.maximumY > min(sourceAnchor.y, destinationAnchor.y)
            if (blocksPath) {
                boundingMinimumX = min(boundingMinimumX, obstacle.minimumX - RoutingMargin)
                boundingMaximumX = max(boundingMaximumX, obstacle.maximumX + RoutingMargin)
                boundingMinimumY = min(boundingMinimumY, obstacle.minimumY - RoutingMargin)
                boundingMaximumY = max(boundingMaximumY, obstacle.maximumY + RoutingMargin)
            }
        }
        obstacles.forEach { obstacle ->
            if (
                obstacle.maximumX < boundingMinimumX ||
                obstacle.minimumX > boundingMaximumX ||
                obstacle.maximumY < boundingMinimumY ||
                obstacle.minimumY > boundingMaximumY
            ) {
                return@forEach
            }
            getOrAddPipe(
                Orientation.Horizontal,
                obstacle.minimumY - HorizontalPipeMargin,
                boundingMinimumX,
                boundingMaximumX,
            )
            getOrAddPipe(
                Orientation.Horizontal,
                obstacle.maximumY + HorizontalPipeMargin,
                boundingMinimumX,
                boundingMaximumX,
            )
            getOrAddPipe(
                Orientation.Vertical,
                obstacle.minimumX - VerticalPipeMargin,
                boundingMinimumY,
                boundingMaximumY,
            )
            getOrAddPipe(
                Orientation.Vertical,
                obstacle.maximumX + VerticalPipeMargin,
                boundingMinimumY,
                boundingMaximumY,
            )
        }
        getOrAddPipe(
            Orientation.Horizontal,
            sourceAnchor.y,
            boundingMinimumX,
            boundingMaximumX,
        )
        getOrAddPipe(
            Orientation.Horizontal,
            destinationAnchor.y,
            boundingMinimumX,
            boundingMaximumX,
        )

        val horizontalPipes = pipes.filter { pipe ->
            pipe.orientation == Orientation.Horizontal &&
                pipe.coordinate >= boundingMinimumY &&
                pipe.coordinate <= boundingMaximumY
        }.toMutableList()
        val verticalPipes = pipes.filter { pipe ->
            pipe.orientation == Orientation.Vertical &&
                pipe.coordinate >= boundingMinimumX &&
                pipe.coordinate <= boundingMaximumX
        }.toMutableList()

        val startKey = orthogonalRouterPointKey(sourceAnchor)
        val endKey = orthogonalRouterPointKey(destinationAnchor)
        val gScore = mutableMapOf<String, Double>()
        val cameFrom = mutableMapOf<String, SwimlanePoint>()
        val arrivalDirection = mutableMapOf<String, Orientation?>()
        val openSet = mutableSetOf<String>()
        data class OpenEntry(
            val key: String,
            var score: Double,
            val point: SwimlanePoint,
        )
        val openList = mutableListOf<OpenEntry>()
        gScore[startKey] = 0.0
        arrivalDirection[startKey] = null
        openList += OpenEntry(
            startKey,
            hypot(destinationAnchor.x - sourceAnchor.x, destinationAnchor.y - sourceAnchor.y),
            sourceAnchor,
        )
        openSet += startKey
        var foundPath = mutableListOf<SwimlanePoint>()

        fun checkSegmentBlocked(first: SwimlanePoint, second: SwimlanePoint): Boolean =
            isSegmentBlocked(first, second, sourceId, destinationId)

        val horizontalVerticalCorner = SwimlanePoint(destinationAnchor.x, sourceAnchor.y)
        val verticalHorizontalCorner = SwimlanePoint(sourceAnchor.x, destinationAnchor.y)
        val horizontalVerticalBlocked =
            checkSegmentBlocked(sourceAnchor, horizontalVerticalCorner) ||
                checkSegmentBlocked(horizontalVerticalCorner, destinationAnchor)
        val verticalHorizontalBlocked =
            checkSegmentBlocked(sourceAnchor, verticalHorizontalCorner) ||
                checkSegmentBlocked(verticalHorizontalCorner, destinationAnchor)
        if (!horizontalVerticalBlocked) {
            foundPath = if (
                abs(sourceAnchor.y - destinationAnchor.y) < RouterEpsilon ||
                abs(sourceAnchor.x - destinationAnchor.x) < RouterEpsilon
            ) {
                mutableListOf(sourceAnchor, destinationAnchor)
            } else {
                mutableListOf(sourceAnchor, horizontalVerticalCorner, destinationAnchor)
            }
        } else if (!verticalHorizontalBlocked) {
            foundPath = if (abs(sourceAnchor.x - destinationAnchor.x) < RouterEpsilon) {
                mutableListOf(sourceAnchor, destinationAnchor)
            } else {
                mutableListOf(sourceAnchor, verticalHorizontalCorner, destinationAnchor)
            }
        }

        if (foundPath.isEmpty()) {
            while (openList.isNotEmpty()) {
                openList.sortBy(OpenEntry::score)
                val current = openList.removeAt(0)
                openSet -= current.key
                if (current.key == endKey) {
                    var currentKey = endKey
                    foundPath = mutableListOf(destinationAnchor)
                    while (currentKey in cameFrom) {
                        val previous = cameFrom.getValue(currentKey)
                        foundPath.add(0, previous)
                        currentKey = orthogonalRouterPointKey(previous)
                    }
                    break
                }

                val centerX = current.point.x
                val centerY = current.point.y
                verticalPipes.sortBy(Pipe::coordinate)
                horizontalPipes.sortBy(Pipe::coordinate)
                val verticalIndex = verticalPipes.indexOfFirst { pipe ->
                    abs(pipe.coordinate - centerX) < 1.0
                }
                val horizontalIndex = horizontalPipes.indexOfFirst { pipe ->
                    abs(pipe.coordinate - centerY) < 1.0
                }
                val neighbors = mutableListOf<SwimlanePoint>()
                if (verticalIndex > 0) {
                    neighbors += SwimlanePoint(verticalPipes[verticalIndex - 1].coordinate, centerY)
                }
                if (verticalIndex >= 0 && verticalIndex < verticalPipes.lastIndex) {
                    neighbors += SwimlanePoint(verticalPipes[verticalIndex + 1].coordinate, centerY)
                }
                if (horizontalIndex > 0) {
                    neighbors += SwimlanePoint(centerX, horizontalPipes[horizontalIndex - 1].coordinate)
                }
                if (horizontalIndex >= 0 && horizontalIndex < horizontalPipes.lastIndex) {
                    neighbors += SwimlanePoint(centerX, horizontalPipes[horizontalIndex + 1].coordinate)
                }

                for (neighbor in neighbors) {
                    if (checkSegmentBlocked(current.point, neighbor)) continue
                    val neighborKey = orthogonalRouterPointKey(neighbor)
                    val distance = abs(neighbor.x - centerX) + abs(neighbor.y - centerY)
                    val crossingPenalty = crossingPenalty(edgeIndex, current.point, neighbor)
                    val destinationDx = destinationAnchor.x - sourceAnchor.x
                    val destinationDy = destinationAnchor.y - sourceAnchor.y
                    val moveDx = neighbor.x - centerX
                    val moveDy = neighbor.y - centerY
                    var directionPenalty = 0.0
                    if (
                        (destinationDy > 10.0 && moveDy < -5.0) ||
                        (destinationDy < -10.0 && moveDy > 5.0)
                    ) {
                        directionPenalty = abs(moveDy) * 100.0
                    }
                    if (
                        (destinationDx > 10.0 && moveDx < -5.0) ||
                        (destinationDx < -10.0 && moveDx > 5.0)
                    ) {
                        directionPenalty += abs(moveDx) * 50.0
                    }
                    val moveDirection =
                        if (abs(moveDx) > RouterEpsilon) {
                            Orientation.Horizontal
                        } else {
                            Orientation.Vertical
                        }
                    val currentDirection = arrivalDirection[current.key]
                    val bendPenalty = if (
                        currentDirection != null &&
                        currentDirection != moveDirection
                    ) {
                        50.0
                    } else {
                        0.0
                    }
                    val tentative = (gScore[current.key] ?: Double.POSITIVE_INFINITY) +
                        distance +
                        crossingPenalty +
                        directionPenalty +
                        bendPenalty
                    val heuristic =
                        abs(destinationAnchor.x - neighbor.x) +
                            abs(destinationAnchor.y - neighbor.y)
                    if (tentative < (gScore[neighborKey] ?: Double.POSITIVE_INFINITY)) {
                        cameFrom[neighborKey] = current.point
                        gScore[neighborKey] = tentative
                        arrivalDirection[neighborKey] = moveDirection
                        if (openSet.add(neighborKey)) {
                            openList += OpenEntry(neighborKey, tentative + heuristic, neighbor)
                        } else {
                            openList.find { entry -> entry.key == neighborKey }?.score =
                                tentative + heuristic
                        }
                    }
                }
            }
        }

        if (foundPath.isEmpty()) {
            foundPath = mutableListOf(
                sourceAnchor,
                SwimlanePoint(sourceAnchor.x, destinationAnchor.y),
                destinationAnchor,
            )
        }

        if (foundPath.size > 4) {
            val start = foundPath.first()
            val end = foundPath.last()
            var minimumX = min(start.x, end.x)
            var maximumX = max(start.x, end.x)
            foundPath.forEach { point ->
                minimumX = min(minimumX, point.x)
                maximumX = max(maximumX, point.x)
            }
            val wentRight = maximumX > max(start.x, end.x)
            val wentLeft = minimumX < min(start.x, end.x)
            if (isLeftToRight) {
                if (wentRight) {
                    val pathX = max(start.x, end.x)
                    val pathMinimumY = min(start.y, end.y)
                    val pathMaximumY = max(start.y, end.y)
                    val detourObstacles = obstacles.filter { obstacle ->
                        obstacle.minimumX < pathX &&
                            obstacle.maximumX > pathX &&
                            obstacle.minimumY < pathMaximumY &&
                            obstacle.maximumY > pathMinimumY
                    }
                    if (detourObstacles.isNotEmpty()) {
                        var visualMaximumX = max(start.x, end.x)
                        detourObstacles.forEach { obstacle ->
                            val center = (obstacle.minimumX + obstacle.maximumX) / 2.0
                            visualMaximumX = max(
                                visualMaximumX,
                                center + obstacle.visualXHalfExtent + VerticalPipeMargin,
                            )
                        }
                        maximumX = visualMaximumX
                    }
                }
                if (wentLeft) {
                    val detourObstacles = obstacles.filter { obstacle ->
                        obstacle.minimumX < min(start.x, end.x) + VerticalPipeMargin &&
                            obstacle.minimumY < max(start.y, end.y) &&
                            obstacle.maximumY > min(start.y, end.y)
                    }
                    if (detourObstacles.isNotEmpty()) {
                        var visualMinimumX = min(start.x, end.x)
                        detourObstacles.forEach { obstacle ->
                            val center = (obstacle.minimumX + obstacle.maximumX) / 2.0
                            visualMinimumX = min(
                                visualMinimumX,
                                center - obstacle.visualXHalfExtent - VerticalPipeMargin,
                            )
                        }
                        minimumX = visualMinimumX
                    }
                }
            }

            fun bestReturnY(detourX: Double): Double {
                val goingDown = end.y > start.y
                val relevant = obstacles.filter { obstacle ->
                    min(start.x, end.x) < obstacle.maximumX &&
                        max(start.x, end.x) > obstacle.minimumX &&
                        min(start.y, end.y) < obstacle.maximumY &&
                        max(start.y, end.y) > obstacle.minimumY
                }
                val filtered = if (isLeftToRight) {
                    relevant.filter { obstacle ->
                        obstacle.minimumX < detourX && obstacle.maximumX > detourX
                    }.takeIf(List<ObstacleRect>::isNotEmpty) ?: relevant
                } else {
                    relevant
                }
                if (filtered.isEmpty()) return end.y
                return if (goingDown) {
                    val value = filtered.maxOf(ObstacleRect::maximumY) + HorizontalPipeMargin
                    if (value < end.y - RouterEpsilon) value else end.y
                } else {
                    val value = filtered.minOf(ObstacleRect::minimumY) - HorizontalPipeMargin
                    if (value > end.y + RouterEpsilon) value else end.y
                }
            }

            fun simplifyWithDetour(detourX: Double): MutableList<SwimlanePoint>? {
                val returnY = bestReturnY(detourX)
                val firstCorner = SwimlanePoint(detourX, start.y)
                val secondCorner = SwimlanePoint(detourX, returnY)
                val thirdCorner = SwimlanePoint(end.x, returnY)
                if (
                    checkSegmentBlocked(start, firstCorner) ||
                    checkSegmentBlocked(firstCorner, secondCorner) ||
                    checkSegmentBlocked(secondCorner, thirdCorner) ||
                    (
                        returnY != end.y &&
                            checkSegmentBlocked(thirdCorner, end)
                        )
                ) {
                    return null
                }
                return if (abs(returnY - end.y) < RouterEpsilon) {
                    mutableListOf(start, firstCorner, secondCorner, end)
                } else {
                    mutableListOf(start, firstCorner, secondCorner, thirdCorner, end)
                }
            }

            val simplified = when {
                wentRight && !wentLeft -> simplifyWithDetour(maximumX)
                wentLeft && !wentRight -> simplifyWithDetour(minimumX)
                else -> null
            }
            if (simplified != null) foundPath = simplified
        }

        val fullPoints = mutableListOf<SwimlanePoint>()
        fullPoints += sourcePort
        fullPoints += sourceHandleWaypoints
        fullPoints += foundPath
        fullPoints += destinationHandleWaypoints.asReversed()
        fullPoints += destinationPort

        if (fullPoints.size >= 3) {
            val third = fullPoints[fullPoints.lastIndex]
            val second = fullPoints[fullPoints.lastIndex - 1]
            val first = fullPoints[fullPoints.lastIndex - 2]
            val horizontal =
                abs(first.y - second.y) < RouterEpsilon &&
                    abs(second.y - third.y) < RouterEpsilon
            val vertical =
                abs(first.x - second.x) < RouterEpsilon &&
                    abs(second.x - third.x) < RouterEpsilon
            val overshot = if (horizontal) {
                val firstSign = (second.x - first.x).sign
                val secondSign = (third.x - first.x).sign
                firstSign != 0.0 &&
                    firstSign == secondSign &&
                    abs(second.x - first.x) > abs(third.x - first.x)
            } else if (vertical) {
                val firstSign = (second.y - first.y).sign
                val secondSign = (third.y - first.y).sign
                firstSign != 0.0 &&
                    firstSign == secondSign &&
                    abs(second.y - first.y) > abs(third.y - first.y)
            } else {
                false
            }
            if (overshot) fullPoints.removeAt(fullPoints.lastIndex - 1)
        }

        val simplified = mutableListOf(fullPoints.first())
        for (index in 1 until fullPoints.lastIndex) {
            if (index == 1) {
                simplified += fullPoints[index]
                continue
            }
            val previous = simplified.last()
            val current = fullPoints[index]
            val next = fullPoints[index + 1]
            if (
                abs(previous.y - current.y) < RouterEpsilon &&
                abs(current.y - next.y) < RouterEpsilon
            ) {
                if ((current.x > previous.x) != (next.x > current.x)) simplified += current
                continue
            }
            if (
                abs(previous.x - current.x) < RouterEpsilon &&
                abs(current.x - next.x) < RouterEpsilon
            ) {
                if ((current.y > previous.y) != (next.y > current.y)) simplified += current
                continue
            }
            simplified += current
        }
        simplified += fullPoints.last()

        for (index in 0 until simplified.lastIndex) {
            val first = simplified[index]
            val second = simplified[index + 1]
            val orientation =
                if (abs(first.x - second.x) < RouterEpsilon) {
                    Orientation.Vertical
                } else {
                    Orientation.Horizontal
                }
            val coordinate = if (orientation == Orientation.Vertical) first.x else first.y
            val from = if (orientation == Orientation.Vertical) {
                min(first.y, second.y)
            } else {
                min(first.x, second.x)
            }
            val to = if (orientation == Orientation.Vertical) {
                max(first.y, second.y)
            } else {
                max(first.x, second.x)
            }
            val pipe = getOrAddPipe(orientation, coordinate, from, to)
            val routedSegment = RoutedSegment(
                edgeIndex = edgeIndex,
                segmentIndex = index,
                orientation = orientation,
                pipe = pipe,
                trackIndex = 0,
                from = from,
                to = to,
            )
            allRoutedSegments += routedSegment
            edgeSegmentIndices[edgeIndex] += allRoutedSegments.lastIndex
            if (pipe.tracks.isEmpty()) {
                pipe.tracks += Track(0, pipe.coordinate, mutableListOf())
            }
            pipe.tracks[0].segments += SegmentRef(edgeIndex, index, from, to)
        }
    }

    fun segmentsOverlap(first: SegmentRef, second: RoutedSegment): Boolean =
        first.from < second.to && second.from < first.to

    fun segmentsOverlap(first: RoutedSegment, second: RoutedSegment): Boolean =
        first.from < second.to && second.from < first.to

    fun trySwapSegmentsAcrossTracks(
        first: RoutedSegment,
        second: RoutedSegment,
        firstTrack: Track,
        secondTrack: Track,
    ): Boolean {
        val firstCanUseSecond = secondTrack.segments.none { reference ->
            (
                reference.edgeIndex != second.edgeIndex ||
                    reference.segmentIndex != second.segmentIndex
                ) &&
                segmentsOverlap(reference, first)
        }
        val secondCanUseFirst = firstTrack.segments.none { reference ->
            (
                reference.edgeIndex != first.edgeIndex ||
                    reference.segmentIndex != first.segmentIndex
                ) &&
                segmentsOverlap(reference, second)
        }
        if (!firstCanUseSecond || !secondCanUseFirst) return false
        first.trackIndex = secondTrack.index
        second.trackIndex = firstTrack.index
        firstTrack.segments = (
            firstTrack.segments.filterNot { reference ->
                reference.edgeIndex == first.edgeIndex &&
                    reference.segmentIndex == first.segmentIndex
            } + SegmentRef(second.edgeIndex, second.segmentIndex, second.from, second.to)
            ).toMutableList()
        secondTrack.segments = (
            secondTrack.segments.filterNot { reference ->
                reference.edgeIndex == second.edgeIndex &&
                    reference.segmentIndex == second.segmentIndex
            } + SegmentRef(first.edgeIndex, first.segmentIndex, first.from, first.to)
            ).toMutableList()
        return true
    }

    fun createNewTrack(pipe: Pipe): Int {
        val index = pipe.tracks.size
        pipe.tracks += Track(index, pipe.coordinate, mutableListOf())
        return index
    }

    fun moveSegmentToTrack(segment: RoutedSegment, trackIndex: Int) {
        val oldTrack = segment.pipe.tracks[segment.trackIndex]
        oldTrack.segments.removeAll { reference ->
            reference.edgeIndex == segment.edgeIndex &&
                reference.segmentIndex == segment.segmentIndex
        }
        segment.trackIndex = trackIndex
        segment.pipe.tracks[trackIndex].segments += SegmentRef(
            segment.edgeIndex,
            segment.segmentIndex,
            segment.from,
            segment.to,
        )
    }

    fun moveSegmentChainToTrack(segment: RoutedSegment, trackIndex: Int) {
        edgeSegmentIndices[segment.edgeIndex].forEach { index ->
            val candidate = allRoutedSegments[index]
            if (candidate.pipe === segment.pipe) moveSegmentToTrack(candidate, trackIndex)
        }
    }

    fun adjacentSegmentsAlongEdge(segment: RoutedSegment): List<RoutedSegment> {
        val indices = edgeSegmentIndices[segment.edgeIndex]
        val indexInList = indices.indexOf(allRoutedSegments.indexOf(segment))
        return buildList {
            if (indexInList > 0) add(allRoutedSegments[indices[indexInList - 1]])
            if (indexInList < indices.lastIndex) add(allRoutedSegments[indices[indexInList + 1]])
        }
    }

    fun haveAnyCrossing(first: RoutedSegment, second: RoutedSegment): Boolean {
        if (first.orientation == second.orientation) return false
        val horizontal = if (first.orientation == Orientation.Horizontal) first else second
        val vertical = if (first.orientation == Orientation.Horizontal) second else first
        return vertical.pipe.coordinate > horizontal.from &&
            vertical.pipe.coordinate < horizontal.to &&
            horizontal.pipe.coordinate > vertical.from &&
            horizontal.pipe.coordinate < vertical.to
    }

    fun findAvailableTrack(pipe: Pipe, segment: RoutedSegment): Int =
        pipe.tracks.firstOrNull { track ->
            track.segments.none { reference ->
                (
                    reference.edgeIndex != segment.edgeIndex ||
                        reference.segmentIndex != segment.segmentIndex
                    ) &&
                    segmentsOverlap(reference, segment)
            }
        }?.index ?: -1

    fun segmentsConflict(first: RoutedSegment, second: RoutedSegment): Boolean {
        if (first.trackIndex == second.trackIndex) return segmentsOverlap(first, second)
        val firstAdjacent = adjacentSegmentsAlongEdge(first)
        val secondAdjacent = adjacentSegmentsAlongEdge(second)
        return firstAdjacent.any { firstSegment ->
            secondAdjacent.any { secondSegment ->
                haveAnyCrossing(firstSegment, secondSegment)
            }
        }
    }

    fun resolveTrackConflict(
        first: RoutedSegment,
        second: RoutedSegment,
        move: (RoutedSegment, Int) -> Unit,
    ) {
        if (
            trySwapSegmentsAcrossTracks(
                first,
                second,
                first.pipe.tracks[first.trackIndex],
                second.pipe.tracks[second.trackIndex],
            )
        ) {
            return
        }
        val available = findAvailableTrack(first.pipe, second)
        move(second, if (available != -1) available else createNewTrack(first.pipe))
    }

    fun resolveHandleConflicts(handles: List<RoutedSegment>): Int {
        var crossings = 0
        for (firstIndex in handles.indices) {
            for (secondIndex in firstIndex + 1 until handles.size) {
                val first = handles[firstIndex]
                val second = handles[secondIndex]
                if (first.pipe !== second.pipe) continue
                if (segmentsConflict(first, second)) {
                    crossings++
                    resolveTrackConflict(first, second, ::moveSegmentChainToTrack)
                }
            }
        }
        return crossings
    }

    val destinationInfoCache = mutableMapOf<Int, DestinationInfo>()
    fun destinationInfo(edgeIndex: Int): DestinationInfo {
        destinationInfoCache[edgeIndex]?.let { return it }
        val indices = edgeSegmentIndices[edgeIndex]
        if (indices.isEmpty()) {
            return DestinationInfo(0.0, 0.0, 0.0, 0.0).also {
                destinationInfoCache[edgeIndex] = it
            }
        }
        val base = allRoutedSegments[indices[0]].pipe.coordinate
        var destination = base
        for (index in 1 until indices.size) {
            val segment = allRoutedSegments[indices[index]]
            if (segment.orientation == Orientation.Horizontal) {
                destination = if (abs(segment.from - base) > abs(segment.to - base)) {
                    segment.from
                } else {
                    segment.to
                }
                break
            }
        }
        return DestinationInfo(
            destination = destination,
            deviation = abs(destination - base),
            base = base,
            delta = destination - base,
        ).also { destinationInfoCache[edgeIndex] = it }
    }

    fun fixSourceHandleCrossings(): Int {
        var crossings = 0
        val edgesBySource = linkedMapOf<String, MutableList<Int>>()
        edges.forEachIndexed { index, edge ->
            val source = edge.start ?: return@forEachIndexed
            if (edgeSegmentIndices[index].isNotEmpty()) {
                edgesBySource.getOrPut(source) { mutableListOf() } += index
            }
        }
        fun edgeDistance(index: Int): Double {
            val edge = edges[index]
            val source = edge.start?.let(nodeById::get) ?: return 0.0
            val destination = edge.end?.let(nodeById::get) ?: return 0.0
            return abs((destination.x ?: 0.0) - (source.x ?: 0.0)) +
                abs((destination.y ?: 0.0) - (source.y ?: 0.0))
        }
        edgesBySource.values.forEach { group ->
            group.sortWith { first, second ->
                val firstInfo = destinationInfo(first)
                val secondInfo = destinationInfo(second)
                when {
                    abs(firstInfo.deviation - secondInfo.deviation) > 1.0 ->
                        firstInfo.deviation.compareTo(secondInfo.deviation)
                    abs(firstInfo.destination - secondInfo.destination) > 1.0 ->
                        firstInfo.destination.compareTo(secondInfo.destination)
                    abs(edgeDistance(first) - edgeDistance(second)) > 1.0 ->
                        edgeDistance(second).compareTo(edgeDistance(first))
                    edgeSegmentIndices[first].size != edgeSegmentIndices[second].size ->
                        edgeSegmentIndices[first].size - edgeSegmentIndices[second].size
                    edgeSegmentIndices[first].size == 1 -> {
                        val firstSegment =
                            allRoutedSegments[edgeSegmentIndices[first].first()]
                        val secondSegment =
                            allRoutedSegments[edgeSegmentIndices[second].first()]
                        val firstLength = abs(firstSegment.to - firstSegment.from)
                        val secondLength = abs(secondSegment.to - secondSegment.from)
                        if (abs(firstLength - secondLength) > 1.0) {
                            firstLength.compareTo(secondLength)
                        } else {
                            0
                        }
                    }
                    else -> 0
                }
            }
            crossings += resolveHandleConflicts(
                group.map { index ->
                    allRoutedSegments[edgeSegmentIndices[index].first()]
                },
            )
        }
        return crossings
    }

    fun fixTargetHandleCrossings(): Int {
        var crossings = 0
        val edgesByTarget = linkedMapOf<String, MutableList<Int>>()
        edges.forEachIndexed { index, edge ->
            val destination = edge.end ?: return@forEachIndexed
            if (edgeSegmentIndices[index].isNotEmpty()) {
                edgesByTarget.getOrPut(destination) { mutableListOf() } += index
            }
        }
        edgesByTarget.values.forEach { group ->
            fun previousLength(index: Int): Double {
                val indices = edgeSegmentIndices[index]
                if (indices.size < 2) return 0.0
                val previous = allRoutedSegments[indices[indices.lastIndex - 1]]
                return abs(previous.to - previous.from)
            }
            group.sortWith { first, second ->
                val difference = previousLength(first) - previousLength(second)
                if (abs(difference) > 0.1) difference.compareTo(0.0) else first - second
            }
            crossings += resolveHandleConflicts(
                group.map { index ->
                    val indices = edgeSegmentIndices[index]
                    allRoutedSegments[indices.last()]
                },
            )
        }
        return crossings
    }

    fun fixPipeCrossings(): Int {
        var crossings = 0
        pipes.forEach { pipe ->
            val pipeSegments = mutableListOf<RoutedSegment>()
            pipe.tracks.forEach { track ->
                track.segments.forEach { reference ->
                    val index = edgeSegmentIndices[reference.edgeIndex].find { segmentIndex ->
                        allRoutedSegments[segmentIndex].segmentIndex == reference.segmentIndex
                    }
                    if (index != null) pipeSegments += allRoutedSegments[index]
                }
            }
            pipeSegments.sortWith(
                compareBy<RoutedSegment> { segment -> segment.edgeIndex }
                    .thenBy { segment -> segment.segmentIndex },
            )
            for (firstIndex in pipeSegments.indices) {
                for (secondIndex in firstIndex + 1 until pipeSegments.size) {
                    val first = pipeSegments[firstIndex]
                    val second = pipeSegments[secondIndex]
                    if (segmentsConflict(first, second)) {
                        crossings++
                        resolveTrackConflict(first, second, ::moveSegmentToTrack)
                    }
                }
            }
        }
        return crossings
    }

    var iterations = 0
    while (iterations < 10) {
        val changed =
            fixSourceHandleCrossings() +
                fixTargetHandleCrossings() +
                fixPipeCrossings()
        if (changed == 0) break
        iterations++
    }

    data class SegmentInfo(
        val edgeIndex: Int,
        val segmentIndex: Int,
        val trackIndex: Int,
        val from: Double,
        val to: Double,
    )

    val segmentCoordinates = mutableMapOf<Pair<Int, Int>, Double>()
    pipes.forEach { pipe ->
        val segments = buildList {
            pipe.tracks.forEach { track ->
                track.segments.forEach { segment ->
                    add(
                        SegmentInfo(
                            edgeIndex = segment.edgeIndex,
                            segmentIndex = segment.segmentIndex,
                            trackIndex = track.index,
                            from = segment.from,
                            to = segment.to,
                        ),
                    )
                }
            }
        }.sortedBy(SegmentInfo::from)
        val clusters = mutableListOf<MutableList<SegmentInfo>>()
        if (segments.isNotEmpty()) {
            var current = mutableListOf(segments[0])
            var clusterEnd = segments[0].to
            for (index in 1 until segments.size) {
                val segment = segments[index]
                if (segment.from < clusterEnd) {
                    current += segment
                    clusterEnd = max(clusterEnd, segment.to)
                } else {
                    clusters += current
                    current = mutableListOf(segment)
                    clusterEnd = segment.to
                }
            }
            clusters += current
        }

        clusters.forEach { cluster ->
            val usedTracks = cluster.mapTo(mutableSetOf(), SegmentInfo::trackIndex)
            val trackScores = mutableMapOf<Int, Double>()
            cluster.forEach { segment ->
                trackScores[segment.trackIndex] =
                    (trackScores[segment.trackIndex] ?: 0.0) +
                    destinationInfo(segment.edgeIndex).delta
            }
            val leftTracks = usedTracks.filterTo(mutableListOf()) { track ->
                (trackScores[track] ?: 0.0) < -1.0
            }
            val rightTracks = usedTracks.filterTo(mutableListOf()) { track ->
                (trackScores[track] ?: 0.0) > 1.0
            }
            val neutralTracks = usedTracks.filterTo(mutableListOf()) { track ->
                abs(trackScores[track] ?: 0.0) <= 1.0
            }
            leftTracks.sortByDescending { track -> trackScores[track] ?: 0.0 }
            rightTracks.sortBy { track -> trackScores[track] ?: 0.0 }

            fun assignCoordinate(trackIndex: Int, coordinate: Double) {
                cluster.filter { segment -> segment.trackIndex == trackIndex }
                    .forEach { segment ->
                        segmentCoordinates[segment.edgeIndex to segment.segmentIndex] =
                            if (segment.edgeIndex in straightIntraLaneEdges) {
                                pipe.coordinate
                            } else {
                                coordinate
                            }
                    }
            }

            leftTracks.forEachIndexed { index, track ->
                assignCoordinate(track, pipe.coordinate - (index + 1) * TrackSpacing)
            }
            if (neutralTracks.isEmpty() && usedTracks.isNotEmpty()) {
                val bestTrack = usedTracks.minBy { track -> abs(trackScores[track] ?: 0.0) }
                leftTracks.remove(bestTrack)
                rightTracks.remove(bestTrack)
                neutralTracks += bestTrack
            }
            neutralTracks.forEachIndexed { index, track ->
                val coordinate = if (index == 0) {
                    pipe.coordinate
                } else {
                    val directionValue = if (index % 2 == 1) 1.0 else -1.0
                    val magnitude = ceil(index / 2.0)
                    pipe.coordinate + directionValue * magnitude * TrackSpacing * 0.5
                }
                assignCoordinate(track, coordinate)
            }
            rightTracks.forEachIndexed { index, track ->
                assignCoordinate(track, pipe.coordinate + (index + 1) * TrackSpacing)
            }
        }
    }

    edges.forEachIndexed { edgeIndex, edge ->
        val indices = edgeSegmentIndices[edgeIndex]
        if (indices.isEmpty()) return@forEachIndexed
        val source = edge.start?.let(nodeById::get) ?: return@forEachIndexed
        val destination = edge.end?.let(nodeById::get) ?: return@forEachIndexed
        val ports = portsForEdge(edgeIndex, source, destination)
        val lines = indices.map { index ->
            val segment = allRoutedSegments[index]
            RoutedLine(
                orientation = segment.orientation,
                coordinate = segmentCoordinates[
                    segment.edgeIndex to segment.segmentIndex
                ] ?: segment.pipe.coordinate,
                from = segment.from,
                to = segment.to,
            )
        }
        val newPoints = mutableListOf(ports.sourcePort)
        lines.forEachIndexed { index, line ->
            val previousPoint = newPoints.last()
            val previousAlong =
                if (line.orientation == Orientation.Vertical) previousPoint.y else previousPoint.x
            val previousTrackCoordinate =
                if (line.orientation == Orientation.Vertical) previousPoint.x else previousPoint.y
            val nextLine = lines.getOrNull(index + 1)
            if (abs(previousTrackCoordinate - line.coordinate) > RouterEpsilon) {
                newPoints += pointOnLine(line, previousAlong)
            }
            if (nextLine != null && nextLine.orientation == line.orientation) {
                if (abs(line.coordinate - nextLine.coordinate) > RouterEpsilon) {
                    val junction = if (line.orientation == Orientation.Vertical) {
                        (previousAlong + nextLine.from) / 2.0
                    } else {
                        sharedLineEndpointCoordinate(line, nextLine)
                    }
                    newPoints += pointOnLine(line, junction)
                    newPoints += pointOnLine(nextLine, junction)
                } else if (index == 0 || index == lines.lastIndex - 1) {
                    newPoints += pointOnLine(
                        line,
                        sharedLineEndpointCoordinate(line, nextLine),
                    )
                }
            } else if (nextLine != null) {
                newPoints += pointOnLine(line, nextLine.coordinate)
            } else {
                val endAlong = if (abs(line.from - previousAlong) < abs(line.to - previousAlong)) {
                    line.to
                } else {
                    line.from
                }
                newPoints += pointOnLine(line, endAlong)
            }
        }
        val last = newPoints.last()
        if (
            abs(last.x - ports.destinationPort.x) > RouterEpsilon ||
            abs(last.y - ports.destinationPort.y) > RouterEpsilon
        ) {
            newPoints += ports.destinationPort
        }
        edge.points = newPoints.fold(mutableListOf()) { filtered, point ->
            if (
                filtered.isEmpty() ||
                abs(point.x - filtered.last().x) > RouterEpsilon ||
                abs(point.y - filtered.last().y) > RouterEpsilon
            ) {
                filtered += point
            }
            filtered
        }
    }

    data.edges.removeAll(SwimlaneEdge::isLayoutOnly)

    fun nodeBoundaryClamp(point: SwimlanePoint, node: SwimlaneNode): SwimlanePoint {
        val centerX = node.x ?: 0.0
        val centerY = node.y ?: 0.0
        if (node.width <= 0.0 || node.height <= 0.0) return point
        val left = centerX - node.width / 2.0
        val right = centerX + node.width / 2.0
        val top = centerY - node.height / 2.0
        val bottom = centerY + node.height / 2.0
        if (point.x < left || point.x > right || point.y < top || point.y > bottom) return point
        val leftDistance = point.x - left
        val rightDistance = right - point.x
        val topDistance = point.y - top
        val bottomDistance = bottom - point.y
        val minimumDistance = min(min(leftDistance, rightDistance), min(topDistance, bottomDistance))
        return when (minimumDistance) {
            leftDistance -> point.copy(x = left)
            rightDistance -> point.copy(x = right)
            topDistance -> point.copy(y = top)
            else -> point.copy(y = bottom)
        }
    }

    data.edges.forEach { edge ->
        val points = edge.points ?: return@forEach
        if (points.size < 2) return@forEach
        edge.start?.let(nodeById::get)?.let { source ->
            points[0] = nodeBoundaryClamp(points[0], source)
        }
        edge.end?.let(nodeById::get)?.let { destination ->
            points[points.lastIndex] = nodeBoundaryClamp(points.last(), destination)
        }
    }
    return data
}
