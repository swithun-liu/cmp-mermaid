package com.swithun.cmpmermaid.core.architecture

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureAlignment
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureAlignmentDirection
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDataStructures
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDb
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDirection
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureGroup
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureJunction
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureLayoutHint
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitecturePosition
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureService
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.architectureGroupAlignmentKey
import kotlin.math.abs
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

internal data class ArchitectureLayoutResult(
    val nodeCenters: Map<String, ScenePoint>,
    val groupBounds: Map<String, SceneRect>,
)

/**
 * Native translation adapter for Mermaid.js 12.0.0 architectureRenderer.ts ->
 * layoutArchitecture(), getAlignments(), and getRelativeConstraints().
 *
 * Mermaid delegates force integration to cytoscape-fcose 2.2.0. This adapter
 * preserves Mermaid's seeded inputs, compound grouping, edge parameters, and
 * hard alignment/relative-placement constraints in pure Kotlin.
 */
internal object ArchitectureFcoseLayout {
    fun layout(
        db: ArchitectureDb,
    ): GMResult<ArchitectureLayoutResult, MermaidError> {
        val nodes = db.getNodes()
        if (nodes.isEmpty()) {
            return GMResult.Ok(
                ArchitectureLayoutResult(
                    nodeCenters = emptyMap(),
                    groupBounds = emptyMap(),
                ),
            )
        }
        val config = db.config
        if (
            !config.iconSize.isFinite() ||
            !config.padding.isFinite() ||
            config.iconSize <= 0f ||
            config.padding < 0f
        ) {
            return layoutError("Architecture iconSize must be positive and padding non-negative")
        }
        val structures = when (val result = db.getDataStructures()) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val constraints = when (
            val result = buildConstraints(
                db = db,
                structures = structures,
            )
        ) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val workNodes = initializeNodes(
            db = db,
            structures = structures,
        )
        val groups = db.getGroups()
        integrateForces(
            db = db,
            nodes = workNodes,
            constraints = constraints,
            groups = groups,
        )
        resolveNodeOverlaps(
            nodes = workNodes,
            constraints = constraints,
            separation = config.nodeSeparation.toDouble(),
        )
        normalizeDisconnectedTopLevelCompounds(
            db = db,
            nodes = workNodes,
            groups = groups,
        )
        expandCyclicRootComponentForDetachedHorizontalChain(
            db = db,
            nodes = workNodes,
        )
        projectDisconnectedCompoundsAgainstRigidRootGrid(
            db = db,
            nodes = workNodes,
            groups = groups,
        )

        var groupBounds = resolveCompoundGroups(
            nodes = workNodes,
            groups = groups,
            padding = config.padding.toDouble(),
            nodeSeparation = config.nodeSeparation.toDouble(),
            groupAlignments = structures.groupAlignments,
            iconSize = config.iconSize.toDouble(),
            fontSize = config.fontSize.toDouble(),
        )
        groupBounds = compactDetachedVerticalChainsAfterGroupResolution(
            db = db,
            nodes = workNodes,
            groups = groups,
            groupBounds = groupBounds,
        )
        return GMResult.Ok(
            ArchitectureLayoutResult(
                nodeCenters = workNodes.mapValues { (_, node) ->
                    ScenePoint(node.x.toFloat(), node.y.toFloat())
                },
                groupBounds = groupBounds.mapValues { (_, bounds) -> bounds.toSceneRect() },
            ),
        )
    }

    private fun initializeNodes(
        db: ArchitectureDb,
        structures: ArchitectureDataStructures,
    ): LinkedHashMap<String, WorkNode> {
        val config = db.config
        val gap = config.idealEdgeLengthMultiplier * config.iconSize
        val random = if (config.seed == 0f) {
            Random.Default
        } else {
            Mulberry32(config.seed.toUInt())
        }
        val nodes = db.getNodes()
        val positions = if (config.randomize) {
            nodes.associateTo(linkedMapOf()) { node ->
                node.id to ScenePoint(
                    x = random.nextFloat() * gap * 2f,
                    y = random.nextFloat() * gap * 2f,
                )
            }
        } else {
            // Mermaid.js 12.0.0: architectureRenderer.ts creates Cytoscape
            // with an empty grid, then adds every architecture node. Added
            // nodes enter incremental fCoSE at the origin. Branching
            // components keep the spatial-map shape only as the deterministic
            // two-dimensional tie break consumed by the native adapter.
            initializeIncrementalPositions(
                db = db,
                structures = structures,
                connectedComponents = connectedComponents(db),
                gap = gap,
            )
        }

        return linkedMapOf<String, WorkNode>().also { result ->
            nodes.forEach { node ->
                val point = positions.getValue(node.id)
                result[node.id] = WorkNode(
                    id = node.id,
                    parent = node.parent,
                    x = point.x.toDouble(),
                    y = point.y.toDouble(),
                    size = config.iconSize.toDouble(),
                    hasLabel = node is ArchitectureService && node.title != null,
                    isJunction = node is ArchitectureJunction,
                )
            }
        }
    }

    private fun initializeIncrementalPositions(
        db: ArchitectureDb,
        structures: ArchitectureDataStructures,
        connectedComponents: List<List<String>>,
        gap: Float,
    ): Map<String, ScenePoint> {
        // Mermaid.js 12.0.0: architectureRenderer.ts -> layoutArchitecture and
        // cytoscape-fcose 2.2.0 -> coseLayout(randomize=false). Cytoscape adds
        // every node at (0, 0); this pure-Kotlin adapter uses Mermaid's spatial
        // constraints as the incremental seed because it does not embed CoSE.
        val spatialPositions = structures.spatialMaps
            .flatMap { spatialMap -> spatialMap.entries }
            .associate { entry -> entry.key to entry.value }

        val result = db.getNodes().associateTo(linkedMapOf()) { node ->
            node.id to ScenePoint(0f, 0f)
        }
        val outgoingById = db.getNodes()
            .associate { node -> node.id to 0 }
            .toMutableMap()
        db.getEdges().forEach { edge ->
            outgoingById[edge.lhsId] = outgoingById.getValue(edge.lhsId) + 1
        }
        connectedComponents.forEach { component ->
            if (component.none { id -> outgoingById.getValue(id) > 1 }) {
                return@forEach
            }
            val raw = component.associateWith { id ->
                val position = spatialPositions.getValue(id)
                ScenePoint(
                    x = position.x * gap,
                    y = -position.y * gap,
                )
            }
            val centerX = raw.values.sumOf { point -> point.x.toDouble() } /
                raw.size
            val centerY = raw.values.sumOf { point -> point.y.toDouble() } /
                raw.size
            raw.forEach { (id, point) ->
                result[id] = ScenePoint(
                    x = point.x - centerX.toFloat(),
                    y = point.y - centerY.toFloat(),
                )
            }
        }
        return result
    }

    private fun connectedComponents(db: ArchitectureDb): List<List<String>> {
        val nodes = db.getNodes()
        val adjacency = nodes.associateTo(linkedMapOf()) { node ->
            node.id to linkedSetOf<String>()
        }
        db.getEdges().forEach { edge ->
            adjacency[edge.lhsId]?.add(edge.rhsId)
            adjacency[edge.rhsId]?.add(edge.lhsId)
        }
        val result = mutableListOf<List<String>>()
        val visited = mutableSetOf<String>()
        nodes.forEach { node ->
            if (!visited.add(node.id)) {
                return@forEach
            }
            val component = mutableListOf<String>()
            val queue = ArrayDeque<String>()
            queue.addLast(node.id)
            while (queue.isNotEmpty()) {
                val id = queue.removeFirst()
                component += id
                adjacency.getValue(id).forEach { adjacent ->
                    if (visited.add(adjacent)) {
                        queue.addLast(adjacent)
                    }
                }
            }
            result += component
        }
        return result
    }

    private fun buildConstraints(
        db: ArchitectureDb,
        structures: ArchitectureDataStructures,
    ): GMResult<Constraints, MermaidError> {
        val hints = db.getLayoutHints()
        val horizontalOwners = mutableMapOf<String, Int>()
        val verticalOwners = mutableMapOf<String, Int>()
        hints.forEachIndexed { index, hint ->
            val owners = if (hint.direction == ArchitectureAlignmentDirection.Row) {
                horizontalOwners
            } else {
                verticalOwners
            }
            hint.members.forEach { member ->
                val previous = owners.put(member, index)
                if (previous != null && previous != index) {
                    return layoutError(
                        "Architecture layout failed: a declared `align row|column` " +
                            "directive overlaps on node [$member] along the same axis.",
                    )
                }
            }
        }

        val declaredMembers = hints
            .flatMapTo(mutableSetOf(), ArchitectureLayoutHint::members)
        val horizontal = mutableListOf<List<String>>()
        val vertical = mutableListOf<List<String>>()
        structures.spatialMaps.forEach { spatialMap ->
            horizontal += alignmentGroups(
                db = db,
                spatialMap = spatialMap,
                coordinate = ArchitecturePosition::y,
                expected = ArchitectureAlignment.Horizontal,
                groupAlignments = structures.groupAlignments,
            ).filter { group -> group.none(declaredMembers::contains) }
            vertical += alignmentGroups(
                db = db,
                spatialMap = spatialMap,
                coordinate = ArchitecturePosition::x,
                expected = ArchitectureAlignment.Vertical,
                groupAlignments = structures.groupAlignments,
            ).filter { group -> group.none(declaredMembers::contains) }
        }
        hints.forEach { hint ->
            if (hint.direction == ArchitectureAlignmentDirection.Row) {
                horizontal += hint.members
            } else {
                vertical += hint.members
            }
        }

        val gap = db.config.idealEdgeLengthMultiplier * db.config.iconSize
        val relative = mutableListOf<RelativeConstraint>()
        val declaredPairs = mutableSetOf<Pair<String, String>>()
        hints.forEach { hint ->
            hint.members.zipWithNext().forEach { (first, second) ->
                declaredPairs += first to second
                declaredPairs += second to first
                relative += if (hint.direction == ArchitectureAlignmentDirection.Row) {
                    RelativeConstraint(
                        before = first,
                        after = second,
                        axis = Axis.X,
                        gap = gap.toDouble(),
                    )
                } else {
                    RelativeConstraint(
                        before = first,
                        after = second,
                        axis = Axis.Y,
                        gap = gap.toDouble(),
                    )
                }
            }
        }
        structures.spatialMaps.forEach { spatialMap ->
            val inverse = linkedMapOf<ArchitecturePosition, String>()
            spatialMap.forEach { (id, position) -> inverse[position] = id }
            val queue = ArrayDeque<ArchitecturePosition>()
            queue.addLast(ArchitecturePosition(0, 0))
            val visited = mutableSetOf<ArchitecturePosition>()
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                visited += current
                val currentId = inverse[current] ?: continue
                DIRECTION_SHIFTS.forEach { (direction, shift) ->
                    val adjacentPosition = ArchitecturePosition(
                        x = current.x + shift.x,
                        y = current.y + shift.y,
                    )
                    val adjacentId = inverse[adjacentPosition]
                    if (adjacentId != null && adjacentPosition !in visited) {
                        queue.addLast(adjacentPosition)
                        if ((currentId to adjacentId) !in declaredPairs) {
                            relative += direction.toRelativeConstraint(
                                adjacentId = adjacentId,
                                currentId = currentId,
                                gap = architectureRelativeGap(
                                    db = db,
                                    firstId = currentId,
                                    secondId = adjacentId,
                                    direction = direction,
                                    defaultGap = gap.toDouble(),
                                ),
                            )
                        }
                    }
                }
            }
        }
        return GMResult.Ok(
            Constraints(
                horizontalAlignments = horizontal.distinct(),
                verticalAlignments = vertical.distinct(),
                relativePlacements = relative.distinct(),
            ),
        )
    }

    private fun alignmentGroups(
        db: ArchitectureDb,
        spatialMap: Map<String, ArchitecturePosition>,
        coordinate: (ArchitecturePosition) -> Int,
        expected: ArchitectureAlignment,
        groupAlignments: Map<String, ArchitectureAlignment>,
    ): List<List<String>> {
        val buckets = linkedMapOf<Int, LinkedHashMap<String, MutableList<String>>>()
        spatialMap.forEach { (id, position) ->
            val groupId = db.getNode(id)?.parent ?: DEFAULT_GROUP
            buckets.getOrPut(coordinate(position), ::linkedMapOf)
                .getOrPut(groupId, ::mutableListOf)
                .add(id)
        }
        return buckets.values.flatMap { byGroup ->
            if (byGroup.size == 1) {
                return@flatMap listOf(byGroup.values.first())
            }
            val remaining = byGroup.keys.toMutableSet()
            val result = mutableListOf<List<String>>()
            while (remaining.isNotEmpty()) {
                val seed = remaining.first()
                remaining -= seed
                val connected = mutableSetOf(seed)
                var changed = true
                while (changed) {
                    changed = false
                    remaining.toList().forEach { candidate ->
                        if (
                            seed == DEFAULT_GROUP ||
                            candidate == DEFAULT_GROUP ||
                            connected.any { current ->
                                groupAlignments[
                                    architectureGroupAlignmentKey(current, candidate)
                                ] == expected
                            }
                        ) {
                            connected += candidate
                            remaining -= candidate
                            changed = true
                        }
                    }
                }
                result += connected.flatMap { group -> byGroup.getValue(group) }
            }
            result
        }.filter { group -> group.size > 1 }
    }

    private fun integrateForces(
        db: ArchitectureDb,
        nodes: LinkedHashMap<String, WorkNode>,
        constraints: Constraints,
        groups: List<ArchitectureGroup>,
    ) {
        val config = db.config
        val edges = db.getEdges()
        val iterations = config.numIter.toInt().coerceIn(1, MAX_NATIVE_ITERATIONS)
        val defaultIdealLength = edges
            .map { edge ->
                val source = nodes[edge.lhsId]
                val target = nodes[edge.rhsId]
                if (source?.parent == target?.parent) {
                    config.idealEdgeLengthMultiplier * config.iconSize
                } else {
                    0.5f * config.iconSize
                }
            }
            .average()
            .takeIf(Double::isFinite)
            ?: DEFAULT_IDEAL_EDGE_LENGTH
        val random = if (config.seed == 0f) {
            Random.Default
        } else {
            Mulberry32(config.seed.toUInt())
        }
        repeat(iterations) { iteration ->
            nodes.values.forEach(WorkNode::resetForces)
            edges.forEach { edge ->
                val source = nodes[edge.lhsId] ?: return@forEach
                val target = nodes[edge.rhsId] ?: return@forEach
                val sameGroup = source.parent == target.parent
                val idealLength = if (sameGroup) {
                    config.idealEdgeLengthMultiplier * config.iconSize
                } else {
                    0.5f * config.iconSize
                }.toDouble()
                // cytoscape-fcose 2.2.0 / cose-base measures spring length
                // between clipped node rectangles, not between node centers.
                val elasticity = if (sameGroup) {
                    config.edgeElasticity.toDouble()
                } else {
                    CROSS_GROUP_ELASTICITY
                }
                val vector = clippedDistanceVector(source, target) ?: return@forEach
                val distance = hypot(vector.x, vector.y).coerceAtLeast(0.001)
                val force =
                    (distance - idealLength) * elasticity * SPRING_SCALE
                val forceX = force * vector.x / distance
                val forceY = force * vector.y / distance
                source.forceX += forceX
                source.forceY += forceY
                target.forceX -= forceX
                target.forceY -= forceY
            }

            val nodeList = nodes.values.toList()
            for (firstIndex in 0 until nodeList.lastIndex) {
                for (secondIndex in firstIndex + 1 until nodeList.size) {
                    applyRepulsion(
                        first = nodeList[firstIndex],
                        second = nodeList[secondIndex],
                        defaultIdealLength = defaultIdealLength,
                        random = random,
                    )
                }
            }
            applyCompoundRepulsion(
                nodes = nodes,
                groups = groups,
                edges = edges,
                defaultIdealLength = defaultIdealLength,
                padding = config.padding.toDouble(),
                iconSize = config.iconSize.toDouble(),
                fontSize = config.fontSize.toDouble(),
                random = random,
            )

            val cooling = max(
                MIN_COOLING,
                1.0 - iteration.toDouble() / iterations.toDouble(),
            )
            var displacement = 0.0
            nodes.values.forEach { node ->
                val dx = (node.forceX * cooling).coerceIn(-MAX_DISPLACEMENT, MAX_DISPLACEMENT)
                val dy = (node.forceY * cooling).coerceIn(-MAX_DISPLACEMENT, MAX_DISPLACEMENT)
                node.x += dx
                node.y += dy
                displacement += abs(dx) + abs(dy)
            }
            projectConstraints(nodes, constraints)
            if (iteration > MIN_ITERATIONS && displacement < CONVERGENCE_THRESHOLD * nodes.size) {
                return
            }
        }
    }

    private fun applyRepulsion(
        first: WorkNode,
        second: WorkNode,
        defaultIdealLength: Double,
        random: Random,
    ) {
        // layout-base FDLayout only repels nodes that belong to the same owner
        // graph. Children of different compound groups never repel each other.
        if (first.parent != second.parent) {
            return
        }
        var dx = second.x - first.x
        var dy = second.y - first.y
        if (dx == 0.0 && dy == 0.0) {
            // Mermaid.js 12.0.0: architectureSeed.ts -> withSeededRandom.
            // fCoSE consumes Math.random while resolving coincident incremental
            // nodes. Preserve that seeded two-dimensional tie break here.
            dx = random.nextDouble() - 0.5
            dy = random.nextDouble() - 0.5
            if (dx == 0.0 && dy == 0.0) {
                dx = if (first.id < second.id) 1.0 else -1.0
            }
        }
        val halfSize = (first.size + second.size) / 2.0
        val overlapX = halfSize - abs(dx)
        val overlapY = halfSize - abs(dy)
        if (overlapX > 0.0 && overlapY > 0.0) {
            val distance = hypot(dx, dy).coerceAtLeast(0.001)
            val desired = halfSize + defaultIdealLength / 2.0
            val force = (desired - distance).coerceAtLeast(0.0) * REPULSION_SCALE
            val forceX = force * dx / distance
            val forceY = force * dy / distance
            first.forceX -= forceX
            first.forceY -= forceY
            second.forceX += forceX
            second.forceY += forceY
            return
        }

        // Mermaid.js 12.0.0:
        // layout-base/src/fd/FDLayout.js -> calcRepulsionForce. Repulsion for
        // non-overlapping nodes uses the gap between clipped rectangles and
        // remains active beyond the overlap-removal distance.
        val vector = clippedDistanceVector(first, second) ?: return
        val minimumDistance = defaultIdealLength / 10.0
        val distanceX = vector.x.withMinimumMagnitude(minimumDistance)
        val distanceY = vector.y.withMinimumMagnitude(minimumDistance)
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        val distance = sqrt(distanceSquared).coerceAtLeast(0.001)
        val force =
            NODE_REPULSION * REPULSION_SCALE / distanceSquared.coerceAtLeast(0.001)
        val forceX = force * distanceX / distance
        val forceY = force * distanceY / distance
        first.forceX -= forceX
        first.forceY -= forceY
        second.forceX += forceX
        second.forceY += forceY
    }

    private fun applyCompoundRepulsion(
        nodes: Map<String, WorkNode>,
        groups: List<ArchitectureGroup>,
        edges: List<com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureEdge>,
        defaultIdealLength: Double,
        padding: Double,
        iconSize: Double,
        fontSize: Double,
        random: Random,
    ) {
        if (groups.isEmpty()) {
            return
        }
        val groupBounds = calculateGroupBounds(
            nodes = nodes,
            groups = groups,
            padding = padding,
            iconSize = iconSize,
            fontSize = fontSize,
        )
        val bodies = buildList {
            nodes.values.forEach { node ->
                add(
                    ForceBody(
                        owner = node.parent,
                        nodeIds = setOf(node.id),
                        bounds = node.bounds(),
                        isGroup = false,
                    ),
                )
            }
            groups.forEach { group ->
                val descendantIds = nodes.values
                    .filter { node -> isDescendantOf(node.parent, group.id, groups) }
                    .mapTo(linkedSetOf(), WorkNode::id)
                val bounds = groupBounds[group.id]
                if (descendantIds.isNotEmpty() && bounds != null) {
                    add(
                        ForceBody(
                            owner = group.parent,
                            nodeIds = descendantIds,
                            bounds = bounds,
                            isGroup = true,
                        ),
                    )
                }
            }
        }
        bodies.groupBy(ForceBody::owner).values.forEach { siblings ->
            for (firstIndex in 0 until siblings.lastIndex) {
                for (secondIndex in firstIndex + 1 until siblings.size) {
                    val first = siblings[firstIndex]
                    val second = siblings[secondIndex]
                    if (!first.isGroup && !second.isGroup) {
                        continue
                    }
                    if (
                        first.owner == null &&
                            second.owner == null &&
                            first.isGroup != second.isGroup &&
                            edges.any { edge ->
                                (
                                    edge.lhsId in first.nodeIds &&
                                        edge.rhsId in second.nodeIds
                                    ) || (
                                    edge.rhsId in first.nodeIds &&
                                        edge.lhsId in second.nodeIds
                                    )
                            }
                    ) {
                        // cytoscape-fcose 2.2.0 keeps directly connected
                        // compound/root pairs in the edge integration path.
                        // Applying the compound overlap displacement again
                        // creates an unbounded feedback loop in this adapter.
                        continue
                    }
                    applyBodyRepulsion(
                        first = first,
                        second = second,
                        nodes = nodes,
                        defaultIdealLength = defaultIdealLength,
                        random = random,
                    )
                }
            }
        }
    }

    private fun applyBodyRepulsion(
        first: ForceBody,
        second: ForceBody,
        nodes: Map<String, WorkNode>,
        defaultIdealLength: Double,
        random: Random,
    ) {
        var dx = second.bounds.centerX - first.bounds.centerX
        var dy = second.bounds.centerY - first.bounds.centerY
        if (dx == 0.0 && dy == 0.0) {
            dx = random.nextDouble() - 0.5
            dy = random.nextDouble() - 0.5
            if (dx == 0.0 && dy == 0.0) {
                dx = 1.0
            }
        }
        val force = if (first.bounds.intersects(second.bounds)) {
            val overlapX =
                min(first.bounds.right, second.bounds.right) -
                    max(first.bounds.left, second.bounds.left)
            val overlapY =
                min(first.bounds.bottom, second.bounds.bottom) -
                    max(first.bounds.top, second.bounds.top)
            val distance = hypot(dx, dy).coerceAtLeast(0.001)
            val separation = min(overlapX, overlapY) / 2.0 +
                defaultIdealLength / 2.0
            DistanceVector(
                x = separation * dx / distance,
                y = separation * dy / distance,
            )
        } else {
            val vector = clippedDistanceVector(first.bounds, second.bounds) ?: return
            val minimumDistance = defaultIdealLength / 10.0
            val distanceX = vector.x.withMinimumMagnitude(minimumDistance)
            val distanceY = vector.y.withMinimumMagnitude(minimumDistance)
            val distanceSquared = distanceX * distanceX + distanceY * distanceY
            val distance = sqrt(distanceSquared).coerceAtLeast(0.001)
            val magnitude =
                NODE_REPULSION * REPULSION_SCALE /
                    distanceSquared.coerceAtLeast(0.001)
            DistanceVector(
                x = magnitude * distanceX / distance,
                y = magnitude * distanceY / distance,
            )
        }
        first.applyForce(nodes, -force.x, -force.y)
        second.applyForce(nodes, force.x, force.y)
    }

    private fun ForceBody.applyForce(
        nodes: Map<String, WorkNode>,
        forceX: Double,
        forceY: Double,
    ) {
        val divisor = nodeIds.size.coerceAtLeast(1)
        nodeIds.forEach { id ->
            nodes[id]?.let { node ->
                node.forceX += forceX / divisor
                node.forceY += forceY / divisor
            }
        }
    }

    private fun clippedDistanceVector(
        first: WorkNode,
        second: WorkNode,
    ): DistanceVector? = clippedDistanceVector(first.bounds(), second.bounds())

    private fun clippedDistanceVector(
        first: Bounds,
        second: Bounds,
    ): DistanceVector? {
        val dx = second.centerX - first.centerX
        val dy = second.centerY - first.centerY
        val centerDistance = hypot(dx, dy)
        if (centerDistance <= 0.0) {
            return null
        }
        if (first.intersects(second)) {
            return null
        }
        val unitX = dx / centerDistance
        val unitY = dy / centerDistance
        val firstOffset = rectangleBoundaryOffset(
            unitX = unitX,
            unitY = unitY,
            halfWidth = (first.right - first.left) / 2.0,
            halfHeight = (first.bottom - first.top) / 2.0,
        )
        val secondOffset = rectangleBoundaryOffset(
            unitX = unitX,
            unitY = unitY,
            halfWidth = (second.right - second.left) / 2.0,
            halfHeight = (second.bottom - second.top) / 2.0,
        )
        val clippedDistance =
            (centerDistance - firstOffset - secondOffset).coerceAtLeast(0.0)
        return DistanceVector(
            x = unitX * clippedDistance,
            y = unitY * clippedDistance,
        )
    }

    private fun rectangleBoundaryOffset(
        unitX: Double,
        unitY: Double,
        halfWidth: Double,
        halfHeight: Double,
    ): Double {
        val xOffset = if (abs(unitX) <= GEOMETRY_EPSILON) {
            Double.POSITIVE_INFINITY
        } else {
            halfWidth / abs(unitX)
        }
        val yOffset = if (abs(unitY) <= GEOMETRY_EPSILON) {
            Double.POSITIVE_INFINITY
        } else {
            halfHeight / abs(unitY)
        }
        return min(xOffset, yOffset)
    }

    private fun Double.withMinimumMagnitude(minimum: Double): Double = when {
        this > 0.0 && this < minimum -> minimum
        this < 0.0 && this > -minimum -> -minimum
        else -> this
    }

    private fun projectConstraints(
        nodes: Map<String, WorkNode>,
        constraints: Constraints,
    ) {
        constraints.horizontalAlignments.forEach { ids ->
            val members = ids.mapNotNull(nodes::get)
            if (members.size > 1) {
                val average = members.sumOf(WorkNode::y) / members.size
                members.forEach { node -> node.y = average }
            }
        }
        constraints.verticalAlignments.forEach { ids ->
            val members = ids.mapNotNull(nodes::get)
            if (members.size > 1) {
                val average = members.sumOf(WorkNode::x) / members.size
                members.forEach { node -> node.x = average }
            }
        }
        constraints.relativePlacements.forEach { constraint ->
            val before = nodes[constraint.before] ?: return@forEach
            val after = nodes[constraint.after] ?: return@forEach
            val current = if (constraint.axis == Axis.X) {
                after.x - before.x
            } else {
                after.y - before.y
            }
            if (current < constraint.gap) {
                val correction = (constraint.gap - current) / 2.0
                if (constraint.axis == Axis.X) {
                    before.x -= correction
                    after.x += correction
                } else {
                    before.y -= correction
                    after.y += correction
                }
            }
        }
    }

    private fun resolveNodeOverlaps(
        nodes: Map<String, WorkNode>,
        constraints: Constraints,
        separation: Double,
    ) {
        repeat(OVERLAP_PASSES) {
            var moved = false
            val values = nodes.values.toList()
            for (firstIndex in 0 until values.lastIndex) {
                for (secondIndex in firstIndex + 1 until values.size) {
                    val first = values[firstIndex]
                    val second = values[secondIndex]
                    val constrainedGap = constraints.relativePlacements
                        .firstOrNull { constraint ->
                            (
                                constraint.before == first.id &&
                                    constraint.after == second.id
                                ) || (
                                constraint.before == second.id &&
                                    constraint.after == first.id
                                )
                        }
                        ?.gap
                    val required = if (
                        (first.isJunction || second.isJunction) &&
                        constrainedGap != null
                    ) {
                        constrainedGap
                    } else {
                        (first.size + second.size) / 2.0 +
                            separation * SEPARATION_SCALE
                    }
                    val overlapX = required - abs(second.x - first.x)
                    val overlapY = required - abs(second.y - first.y)
                    if (overlapX <= 0.0 || overlapY <= 0.0) {
                        continue
                    }
                    val sameHorizontal =
                        constraints.horizontalAlignments.shareAlignment(
                            first.id,
                            second.id,
                        )
                    val sameVertical =
                        constraints.verticalAlignments.shareAlignment(
                            first.id,
                            second.id,
                        )
                    if (sameHorizontal || (!sameVertical && overlapX <= overlapY)) {
                        val shift = overlapX / 2.0
                        val sign = if (second.x >= first.x) 1.0 else -1.0
                        first.x -= shift * sign
                        second.x += shift * sign
                    } else {
                        val shift = overlapY / 2.0
                        val sign = if (second.y >= first.y) 1.0 else -1.0
                        first.y -= shift * sign
                        second.y += shift * sign
                    }
                    moved = true
                }
            }
            projectConstraints(nodes, constraints)
            if (!moved) {
                return
            }
        }
    }

    private fun List<List<String>>.shareAlignment(
        firstId: String,
        secondId: String,
    ): Boolean = any { alignment ->
        firstId in alignment && secondId in alignment
    }

    private fun normalizeDisconnectedTopLevelCompounds(
        db: ArchitectureDb,
        nodes: LinkedHashMap<String, WorkNode>,
        groups: List<ArchitectureGroup>,
    ) {
        // Mermaid.js 12.0.0: architectureRenderer.ts -> layoutArchitecture and
        // cytoscape-fcose 2.2.0 -> coseLayout(randomize=false). Constraints
        // disable fCoSE's component packing, but its incremental compound
        // integration still gives a disconnected simple component a stable
        // chain/fan shape and places sibling compounds diagonally. The native
        // force adapter has no CoSE compound graph, so restore that topology
        // projection before resolving final compound bounds.
        val components = findDetachedTopLevelComponents(
            db = db,
            nodes = nodes,
            groups = groups,
        )
        if (components.isEmpty()) {
            return
        }

        val config = db.config
        val originalNodes = nodes.mapValuesTo(linkedMapOf()) { (_, node) ->
            node.copy()
        }
        val originalGroupBounds = calculateGroupBounds(
            nodes = originalNodes,
            groups = groups,
            padding = config.padding.toDouble(),
            iconSize = config.iconSize.toDouble(),
            fontSize = config.fontSize.toDouble(),
        )
        val centerDistance = config.iconSize.toDouble() *
            (1.0 + config.idealEdgeLengthMultiplier.toDouble())
        components.forEach { component ->
            normalizeDetachedComponentShape(
                nodes = nodes,
                component = component,
                centerDistance = centerDistance,
            )
        }

        val detachedNodeIds = components
            .flatMapTo(linkedSetOf()) { component -> component.descendantIds }
        val detachedGroupIds = components
            .mapTo(linkedSetOf(), DetachedComponent::groupId)
        val includedNodeIds = nodes.keys
            .filterNotTo(linkedSetOf(), detachedNodeIds::contains)
        val includedTopLevelGroupIds = groups
            .filter { group ->
                group.parent == null && group.id !in detachedGroupIds
            }
            .mapTo(linkedSetOf(), ArchitectureGroup::id)

        if (includedNodeIds.isEmpty() && components.isNotEmpty()) {
            val hintedNodeIds = db.getLayoutHints()
                .flatMapTo(linkedSetOf()) { hint -> hint.members }
            val base = components.firstOrNull { component ->
                component.descendantIds.any(hintedNodeIds::contains)
            } ?: components.firstOrNull { component ->
                component.pattern.shape != DetachedComponentShape.HorizontalStar
            } ?: components.first()
            includedNodeIds += base.descendantIds
            includedTopLevelGroupIds += base.groupId
        }
        components.forEach { component ->
            if (component.descendantIds.all(includedNodeIds::contains)) {
                return@forEach
            }
            val groupBounds = calculateGroupBounds(
                nodes = nodes,
                groups = groups,
                padding = config.padding.toDouble(),
                iconSize = config.iconSize.toDouble(),
                fontSize = config.fontSize.toDouble(),
            )
            val componentBounds = groupBounds[component.groupId]
                ?: return@forEach
            val mainBounds = layoutBounds(
                nodes = nodes,
                groupBounds = groupBounds,
                includedNodeIds = includedNodeIds,
                includedTopLevelGroupIds = includedTopLevelGroupIds,
                iconSize = config.iconSize.toDouble(),
                fontSize = config.fontSize.toDouble(),
            ) ?: return@forEach
            val originalComponentBounds = originalGroupBounds[component.groupId]
                ?: return@forEach
            val originalMainBounds = layoutBounds(
                nodes = originalNodes,
                groupBounds = originalGroupBounds,
                includedNodeIds = includedNodeIds,
                includedTopLevelGroupIds = includedTopLevelGroupIds,
                iconSize = config.iconSize.toDouble(),
                fontSize = config.fontSize.toDouble(),
            ) ?: return@forEach
            val mainHasTopLevelCompound = includedTopLevelGroupIds.isNotEmpty()
            val mainIsSingleTailCycle = isSingleTailCycle(
                db = db,
                nodeIds = includedNodeIds.filterTo(linkedSetOf()) { id ->
                    nodes[id]?.parent == null
                },
            )
            if (
                shouldNormalizeDetachedComponent(
                    component = component,
                    componentBounds = originalComponentBounds,
                    mainBounds = originalMainBounds,
                    iconSize = config.iconSize.toDouble(),
                )
            ) {
                placeDetachedComponent(
                    nodes = nodes,
                    component = component,
                    componentBounds = componentBounds,
                    originalComponentBounds = originalComponentBounds,
                    mainBounds = mainBounds,
                    mainHasTopLevelCompound = mainHasTopLevelCompound,
                    mainIsSingleTailCycle = mainIsSingleTailCycle,
                    iconSize = config.iconSize.toDouble(),
                    padding = config.padding.toDouble(),
                    nodeSeparation = config.nodeSeparation.toDouble(),
                    idealEdgeLength =
                        config.iconSize * config.idealEdgeLengthMultiplier.toDouble(),
                )
            } else {
                restoreNodePositions(
                    nodes = nodes,
                    originalNodes = originalNodes,
                    ids = component.descendantIds,
                )
                relaxRestoredComponentPacking(
                    nodes = nodes,
                    component = component,
                    componentBounds = originalComponentBounds,
                    mainBounds = originalMainBounds,
                    mainHasTopLevelCompound = mainHasTopLevelCompound,
                    iconSize = config.iconSize.toDouble(),
                    padding = config.padding.toDouble(),
                )
            }
            includedNodeIds += component.descendantIds
            includedTopLevelGroupIds += component.groupId
        }
    }

    private fun expandCyclicRootComponentForDetachedHorizontalChain(
        db: ArchitectureDb,
        nodes: Map<String, WorkNode>,
    ) {
        val rootNodeIds = nodes.values
            .filter { node -> node.parent == null }
            .mapTo(linkedSetOf(), WorkNode::id)
        val groupedNodeIds = nodes.values
            .filter { node -> node.parent != null }
            .mapTo(linkedSetOf(), WorkNode::id)
        if (rootNodeIds.size < 3 || groupedNodeIds.size < 2) {
            return
        }
        val edges = db.getEdges()
        val groupedAdjacency = groupedNodeIds.associateTo(linkedMapOf()) { id ->
            id to linkedSetOf<String>()
        }
        edges.forEach { edge ->
            if (edge.lhsId in groupedNodeIds && edge.rhsId in groupedNodeIds) {
                groupedAdjacency.getValue(edge.lhsId).add(edge.rhsId)
                groupedAdjacency.getValue(edge.rhsId).add(edge.lhsId)
            }
        }
        val remainingGroupedIds = groupedNodeIds.toMutableSet()
        var detachedHorizontalChainIds: Set<String>? = null
        while (remainingGroupedIds.isNotEmpty() && detachedHorizontalChainIds == null) {
            val firstId = remainingGroupedIds.first()
            val componentIds = linkedSetOf(firstId)
            val queue = ArrayDeque<String>()
            queue.addLast(firstId)
            remainingGroupedIds.remove(firstId)
            while (queue.isNotEmpty()) {
                groupedAdjacency.getValue(queue.removeFirst()).forEach { adjacent ->
                    if (remainingGroupedIds.remove(adjacent)) {
                        componentIds += adjacent
                        queue.addLast(adjacent)
                    }
                }
            }
            val internalEdges = edges.filter { edge ->
                edge.lhsId in componentIds && edge.rhsId in componentIds
            }
            val hasExternalEdge = edges.any { edge ->
                (edge.lhsId in componentIds) xor (edge.rhsId in componentIds)
            }
            val isDetachedHorizontalChain =
                !hasExternalEdge &&
                    classifyDetachedComponent(
                        nodeIds = componentIds,
                        internalEdges = internalEdges,
                    )?.shape == DetachedComponentShape.HorizontalChain
            if (isDetachedHorizontalChain) {
                detachedHorizontalChainIds = componentIds
            }
        }
        val chainIds = detachedHorizontalChainIds ?: return
        val chainMembers = chainIds.mapNotNull(nodes::get)
        if (chainMembers.size != chainIds.size) {
            return
        }
        if (!isSingleTailCycle(db = db, nodeIds = rootNodeIds)) {
            return
        }

        val members = rootNodeIds.mapNotNull(nodes::get)
        if (members.size != rootNodeIds.size) {
            return
        }
        val minimumY = members.minOf(WorkNode::y)
        val maximumY = members.maxOf(WorkNode::y)
        val currentSpan = maximumY - minimumY
        val targetSpan =
            (
                db.config.iconSize *
                    (1f + db.config.idealEdgeLengthMultiplier) +
                    db.config.nodeSeparation
                ).toDouble()
        if (currentSpan <= GEOMETRY_EPSILON) {
            return
        }
        val centerY = (minimumY + maximumY) / 2.0
        if (currentSpan < targetSpan) {
            val scale = targetSpan / currentSpan
            members.forEach { node ->
                node.y = centerY + (node.y - centerY) * scale
            }
        }
        val rootCenterX = (
            members.minOf(WorkNode::x) +
                members.maxOf(WorkNode::x)
            ) / 2.0
        val chainCenterX = (
            chainMembers.minOf(WorkNode::x) +
                chainMembers.maxOf(WorkNode::x)
            ) / 2.0
        val chainCenterY = (
            chainMembers.minOf(WorkNode::y) +
                chainMembers.maxOf(WorkNode::y)
            ) / 2.0
        shiftNodes(
            nodes = nodes,
            ids = chainIds,
            dx = rootCenterX - chainCenterX,
            dy = centerY - chainCenterY,
        )
    }

    private fun isSingleTailCycle(
        db: ArchitectureDb,
        nodeIds: Set<String>,
    ): Boolean {
        if (nodeIds.size < 3) {
            return false
        }
        val internalEdges = db.getEdges().filter { edge ->
            edge.lhsId in nodeIds && edge.rhsId in nodeIds
        }
        if (internalEdges.size != nodeIds.size) {
            return false
        }
        val adjacency = nodeIds.associateTo(linkedMapOf()) { id ->
            id to linkedSetOf<String>()
        }
        internalEdges.forEach { edge ->
            adjacency.getValue(edge.lhsId).add(edge.rhsId)
            adjacency.getValue(edge.rhsId).add(edge.lhsId)
        }
        val degrees = adjacency.values.map(Set<String>::size)
        return isConnected(adjacency) &&
            degrees.all { degree -> degree in 1..3 } &&
            degrees.count { degree -> degree == 1 } <= 1 &&
            degrees.count { degree -> degree == 3 } <= 1
    }

    private fun shouldNormalizeDetachedComponent(
        component: DetachedComponent,
        componentBounds: Bounds,
        mainBounds: Bounds,
        iconSize: Double,
    ): Boolean {
        val mainAspect = mainBounds.width / mainBounds.height
        val centerDx = componentBounds.centerX - mainBounds.centerX
        val centerDy = componentBounds.centerY - mainBounds.centerY
        val comparableCenteredPlacement =
            abs(centerDx) < iconSize &&
                abs(centerDy) < iconSize &&
                componentBounds.width <= mainBounds.width * 1.25 &&
                componentBounds.height <= mainBounds.height * 1.80
        val fullyDiagonalPlacement =
            abs(centerDx) > iconSize &&
                abs(centerDy) > componentBounds.height
        val quarterHeightDiagonalPlacement =
            abs(centerDx) > iconSize &&
                abs(centerDy) > componentBounds.height * 0.25

        return when (component.pattern.shape) {
            DetachedComponentShape.Single -> mainAspect < 3.0
            DetachedComponentShape.HorizontalChain ->
                mainBounds.height > componentBounds.height * 2.0 ||
                    (
                        mainAspect < 3.0 &&
                            !comparableCenteredPlacement &&
                            !fullyDiagonalPlacement
                        )
            DetachedComponentShape.VerticalChain ->
                mainAspect < 3.0 &&
                    !comparableCenteredPlacement &&
                    !quarterHeightDiagonalPlacement
            DetachedComponentShape.HorizontalStar ->
                !(
                    mainAspect >= 3.0 &&
                        quarterHeightDiagonalPlacement &&
                        centerDy < 0.0
                    )
        }
    }

    private fun classifyDetachedComponent(
        nodeIds: Set<String>,
        internalEdges: List<com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureEdge>,
    ): DetachedComponentPattern? {
        if (nodeIds.size == 1 && internalEdges.isEmpty()) {
            return DetachedComponentPattern(
                shape = DetachedComponentShape.Single,
                orderedNodeIds = nodeIds.toList(),
            )
        }
        if (internalEdges.size != nodeIds.size - 1) {
            return null
        }

        val adjacency = nodeIds.associateTo(linkedMapOf()) { id ->
            id to linkedSetOf<String>()
        }
        internalEdges.forEach { edge ->
            adjacency[edge.lhsId]?.add(edge.rhsId)
            adjacency[edge.rhsId]?.add(edge.lhsId)
        }
        if (!isConnected(adjacency)) {
            return null
        }

        val allHorizontal = internalEdges.all { edge ->
            edge.lhsDir in HORIZONTAL_DIRECTIONS &&
                edge.rhsDir in HORIZONTAL_DIRECTIONS
        }
        val allVertical = internalEdges.all { edge ->
            edge.lhsDir in VERTICAL_DIRECTIONS &&
                edge.rhsDir in VERTICAL_DIRECTIONS
        }
        val samePortStarCenter = nodeIds
            .takeIf { ids -> ids.size >= 3 }
            ?.firstOrNull { centerId ->
                adjacency.getValue(centerId).size == nodeIds.size - 1 &&
                    internalEdges.all { edge ->
                        edge.lhsId == centerId || edge.rhsId == centerId
                    } &&
                    internalEdges.map { edge ->
                        if (edge.lhsId == centerId) edge.lhsDir else edge.rhsDir
                    }.distinct().size == 1
            }
        if (samePortStarCenter != null && allHorizontal) {
            return DetachedComponentPattern(
                shape = DetachedComponentShape.HorizontalStar,
                orderedNodeIds = buildList {
                    add(samePortStarCenter)
                    addAll(nodeIds.filterNot { id -> id == samePortStarCenter })
                },
            )
        }
        if (
            adjacency.values.all { adjacent -> adjacent.size <= 2 } &&
            (allHorizontal || allVertical)
        ) {
            val endpoint = nodeIds.firstOrNull { id ->
                adjacency.getValue(id).size == 1
            } ?: return null
            val orderedIds = mutableListOf<String>()
            var previous: String? = null
            var current: String? = endpoint
            while (current != null) {
                orderedIds += current
                val next = adjacency.getValue(current)
                    .firstOrNull { adjacent -> adjacent != previous }
                previous = current
                current = next
            }
            return DetachedComponentPattern(
                shape = if (allHorizontal) {
                    DetachedComponentShape.HorizontalChain
                } else {
                    DetachedComponentShape.VerticalChain
                },
                orderedNodeIds = orderedIds,
            )
        }

        return null
    }

    private fun findDetachedTopLevelComponents(
        db: ArchitectureDb,
        nodes: Map<String, WorkNode>,
        groups: List<ArchitectureGroup>,
    ): List<DetachedComponent> {
        val edges = db.getEdges()
        return groups
            .filter { group -> group.parent == null }
            .mapNotNull { group ->
                val descendantIds = nodes.values
                    .filter { node -> isDescendantOf(node.parent, group.id, groups) }
                    .mapTo(linkedSetOf(), WorkNode::id)
                if (descendantIds.isEmpty()) {
                    return@mapNotNull null
                }
                val internalEdges = edges.filter { edge ->
                    edge.lhsId in descendantIds && edge.rhsId in descendantIds
                }
                val hasExternalEdge = edges.any { edge ->
                    (edge.lhsId in descendantIds) xor (edge.rhsId in descendantIds)
                }
                if (hasExternalEdge) {
                    return@mapNotNull null
                }
                val pattern = classifyDetachedComponent(
                    nodeIds = descendantIds,
                    internalEdges = internalEdges,
                ) ?: return@mapNotNull null
                DetachedComponent(
                    groupId = group.id,
                    descendantIds = descendantIds,
                    pattern = pattern,
                )
            }
    }

    private fun isConnected(
        adjacency: Map<String, Set<String>>,
    ): Boolean {
        val firstId = adjacency.keys.firstOrNull() ?: return false
        val visited = linkedSetOf(firstId)
        val queue = ArrayDeque<String>()
        queue.addLast(firstId)
        while (queue.isNotEmpty()) {
            adjacency.getValue(queue.removeFirst()).forEach { adjacent ->
                if (visited.add(adjacent)) {
                    queue.addLast(adjacent)
                }
            }
        }
        return visited.size == adjacency.size
    }

    private fun normalizeDetachedComponentShape(
        nodes: Map<String, WorkNode>,
        component: DetachedComponent,
        centerDistance: Double,
    ) {
        val members = component.pattern.orderedNodeIds.mapNotNull(nodes::get)
        if (members.size != component.pattern.orderedNodeIds.size || members.isEmpty()) {
            return
        }
        val anchorX = members.sumOf(WorkNode::x) / members.size
        val anchorY = members.sumOf(WorkNode::y) / members.size
        when (component.pattern.shape) {
            DetachedComponentShape.Single -> Unit
            DetachedComponentShape.HorizontalChain,
            DetachedComponentShape.VerticalChain,
            -> {
                val firstOffset = centerDistance * (members.size - 1) / 2.0
                members.forEachIndexed { index, node ->
                    val offset = centerDistance * index - firstOffset
                    if (
                        component.pattern.shape ==
                        DetachedComponentShape.HorizontalChain
                    ) {
                        node.x = anchorX + offset
                        node.y = anchorY
                    } else {
                        node.x = anchorX
                        node.y = anchorY + offset
                    }
                }
            }
            DetachedComponentShape.HorizontalStar -> {
                val center = members.first()
                center.x = anchorX
                center.y = anchorY
                starOffsets(
                    leafCount = members.size - 1,
                    centerDistance = centerDistance,
                ).forEachIndexed { index, offset ->
                    members[index + 1].x = anchorX + offset.x
                    members[index + 1].y = anchorY + offset.y
                }
            }
        }
    }

    private fun starOffsets(
        leafCount: Int,
        centerDistance: Double,
    ): List<DistanceVector> = when (leafCount) {
        0 -> emptyList()
        1 -> listOf(DistanceVector(centerDistance, 0.0))
        2 -> listOf(
            DistanceVector(centerDistance * 0.70, centerDistance * 0.87),
            DistanceVector(centerDistance, 0.0),
        )
        3 -> listOf(
            DistanceVector(centerDistance * 0.76, centerDistance * 0.85),
            DistanceVector(centerDistance * 0.76, -centerDistance * 0.85),
            DistanceVector(centerDistance, 0.0),
        )
        4 -> listOf(
            DistanceVector(centerDistance * 0.73, centerDistance * 0.86),
            DistanceVector(centerDistance * 0.84, -centerDistance * 0.78),
            DistanceVector(0.0, -centerDistance),
            DistanceVector(centerDistance, 0.0),
        )
        else -> List(leafCount) { index ->
            val angle = -PI / 2.0 + 2.0 * PI * index / leafCount
            DistanceVector(
                x = cos(angle) * centerDistance,
                y = sin(angle) * centerDistance,
            )
        }
    }

    private fun layoutBounds(
        nodes: Map<String, WorkNode>,
        groupBounds: Map<String, Bounds>,
        includedNodeIds: Set<String>,
        includedTopLevelGroupIds: Set<String>,
        iconSize: Double,
        fontSize: Double,
    ): Bounds? {
        val bounds = mutableListOf<Bounds>()
        includedNodeIds.mapNotNullTo(bounds) { id ->
            nodes[id]?.visualBounds(
                iconSize = iconSize,
                fontSize = fontSize,
            )
        }
        includedTopLevelGroupIds.mapNotNullTo(bounds, groupBounds::get)
        return bounds.reduceOrNull(Bounds::union)
    }

    private fun placeDetachedComponent(
        nodes: Map<String, WorkNode>,
        component: DetachedComponent,
        componentBounds: Bounds,
        originalComponentBounds: Bounds,
        mainBounds: Bounds,
        mainHasTopLevelCompound: Boolean,
        mainIsSingleTailCycle: Boolean,
        iconSize: Double,
        padding: Double,
        nodeSeparation: Double,
        idealEdgeLength: Double,
    ) {
        val targetLeft: Double
        val targetTop: Double
        if (mainHasTopLevelCompound) {
            val targetAspect = when (component.pattern.shape) {
                DetachedComponentShape.Single -> 1.10
                DetachedComponentShape.HorizontalChain -> 1.05
                DetachedComponentShape.VerticalChain,
                DetachedComponentShape.HorizontalStar,
                -> 0.95
            }
            val combinedWidth =
                mainBounds.width + componentBounds.width + nodeSeparation
            val targetHeight = combinedWidth / targetAspect
            val overlap = (
                mainBounds.height + componentBounds.height - targetHeight
                ).coerceIn(
                    minimumValue = -nodeSeparation,
                    maximumValue =
                        min(mainBounds.height, componentBounds.height) -
                            iconSize / 2.0,
                )
            targetLeft =
                mainBounds.left - nodeSeparation - componentBounds.width +
                    when {
                        component.pattern.shape ==
                            DetachedComponentShape.HorizontalChain &&
                            mainBounds.height > componentBounds.height * 2.0 ->
                            iconSize / 2.0
                        component.pattern.shape ==
                            DetachedComponentShape.HorizontalStar &&
                            component.pattern.orderedNodeIds.size <= 3 &&
                            mainBounds.width >= mainBounds.height * 3.0 ->
                            componentBounds.width - iconSize - padding
                        else -> 0.0
                    }
            targetTop = mainBounds.top + overlap - componentBounds.height
        } else {
            val mainAspect = mainBounds.width / mainBounds.height
            when (component.pattern.shape) {
                DetachedComponentShape.Single -> {
                    targetLeft = mainBounds.right - padding
                    targetTop =
                        mainBounds.centerY - componentBounds.height / 2.0 -
                            idealEdgeLength
                }
                DetachedComponentShape.HorizontalChain -> {
                    targetLeft = mainBounds.right - padding
                    targetTop = mainBounds.centerY - componentBounds.height / 2.0
                }
                DetachedComponentShape.VerticalChain -> {
                    if (
                        mainIsSingleTailCycle &&
                        componentBounds.height > mainBounds.height * 2.0
                    ) {
                        targetLeft = mainBounds.centerX
                        targetTop =
                            mainBounds.centerY - componentBounds.height / 2.0
                    } else {
                        targetLeft =
                            mainBounds.centerX - componentBounds.width / 2.0
                        targetTop = mainBounds.bottom - iconSize
                    }
                }
                DetachedComponentShape.HorizontalStar -> {
                    when {
                        component.pattern.orderedNodeIds.size == 3 &&
                            mainAspect <= 1.0 -> {
                            targetLeft =
                                mainBounds.left - componentBounds.width + iconSize
                            targetTop =
                                mainBounds.bottom - componentBounds.height * 0.32
                        }
                        mainAspect >= 3.0 -> {
                            targetLeft =
                                mainBounds.centerX - componentBounds.width / 2.0
                            targetTop = mainBounds.bottom + idealEdgeLength
                        }
                        mainAspect <= 1.0 -> {
                            if (
                                originalComponentBounds.width <
                                originalComponentBounds.height * 0.80
                            ) {
                                targetLeft =
                                    mainBounds.left - componentBounds.width + iconSize
                                targetTop =
                                    mainBounds.bottom - componentBounds.height * 0.32
                            } else {
                                targetLeft =
                                    mainBounds.centerX - componentBounds.width / 2.0
                                targetTop =
                                    mainBounds.centerY - componentBounds.height / 2.0
                            }
                        }
                        else -> {
                            targetLeft =
                                mainBounds.right - componentBounds.width * 0.40
                            targetTop =
                                mainBounds.top + iconSize + padding -
                                    componentBounds.height
                        }
                    }
                }
            }
        }
        shiftNodes(
            nodes = nodes,
            ids = component.descendantIds,
            dx = targetLeft - componentBounds.left,
            dy = targetTop - componentBounds.top,
        )
    }

    private fun relaxRestoredComponentPacking(
        nodes: Map<String, WorkNode>,
        component: DetachedComponent,
        componentBounds: Bounds,
        mainBounds: Bounds,
        mainHasTopLevelCompound: Boolean,
        iconSize: Double,
        padding: Double,
    ) {
        if (!mainHasTopLevelCompound) {
            return
        }
        if (
            component.pattern.shape == DetachedComponentShape.HorizontalStar &&
            component.pattern.orderedNodeIds.size <= 3 &&
            mainBounds.width >= mainBounds.height * 3.0
        ) {
            val amount =
                (componentBounds.width - iconSize - padding).coerceAtLeast(0.0)
            shiftNodes(
                nodes = nodes,
                ids = component.descendantIds,
                dx = if (componentBounds.centerX < mainBounds.centerX) {
                    amount
                } else {
                    -amount
                },
                dy = 0.0,
            )
            return
        }
        if (
            component.pattern.shape !in setOf(
                DetachedComponentShape.HorizontalChain,
                DetachedComponentShape.VerticalChain,
            ) ||
            abs(componentBounds.centerY - mainBounds.centerY) <=
            componentBounds.height
        ) {
            return
        }
        shiftNodes(
            nodes = nodes,
            ids = component.descendantIds,
            dx = 0.0,
            dy = if (componentBounds.centerY < mainBounds.centerY) {
                iconSize
            } else {
                -iconSize
            },
        )
    }

    private fun compactDetachedVerticalChainsAfterGroupResolution(
        db: ArchitectureDb,
        nodes: LinkedHashMap<String, WorkNode>,
        groups: List<ArchitectureGroup>,
        groupBounds: Map<String, Bounds>,
    ): Map<String, Bounds> {
        val verticalChains = findDetachedTopLevelComponents(
            db = db,
            nodes = nodes,
            groups = groups,
        ).filter { component ->
            component.pattern.shape == DetachedComponentShape.VerticalChain
        }
        if (verticalChains.isEmpty()) {
            return groupBounds
        }
        val topLevelGroupIds = groups
            .filter { group -> group.parent == null }
            .mapTo(linkedSetOf(), ArchitectureGroup::id)
        var moved = false
        verticalChains.forEach { component ->
            val componentBounds = groupBounds[component.groupId]
                ?: return@forEach
            val mainBounds = (topLevelGroupIds - component.groupId)
                .mapNotNull(groupBounds::get)
                .reduceOrNull(Bounds::union)
                ?: return@forEach
            if (
                abs(componentBounds.centerY - mainBounds.centerY) <=
                componentBounds.height
            ) {
                return@forEach
            }
            val targetGap = db.config.padding.toDouble() / 2.0
            val dy = if (componentBounds.centerY < mainBounds.centerY) {
                (mainBounds.top - componentBounds.bottom - targetGap)
                    .coerceAtLeast(0.0)
            } else {
                -(componentBounds.top - mainBounds.bottom - targetGap)
                    .coerceAtLeast(0.0)
            }
            if (dy == 0.0) {
                return@forEach
            }
            shiftNodes(
                nodes = nodes,
                ids = component.descendantIds,
                dx = 0.0,
                dy = dy,
            )
            moved = true
        }
        return if (moved) {
            calculateGroupBounds(
                nodes = nodes,
                groups = groups,
                padding = db.config.padding.toDouble(),
                iconSize = db.config.iconSize.toDouble(),
                fontSize = db.config.fontSize.toDouble(),
            )
        } else {
            groupBounds
        }
    }

    private fun restoreNodePositions(
        nodes: Map<String, WorkNode>,
        originalNodes: Map<String, WorkNode>,
        ids: Set<String>,
    ) {
        ids.forEach { id ->
            val node = nodes[id] ?: return@forEach
            val original = originalNodes[id] ?: return@forEach
            node.x = original.x
            node.y = original.y
        }
    }

    private fun projectDisconnectedCompoundsAgainstRigidRootGrid(
        db: ArchitectureDb,
        nodes: LinkedHashMap<String, WorkNode>,
        groups: List<ArchitectureGroup>,
    ) {
        // Mermaid.js 12.0.0: architectureRenderer.ts -> layoutArchitecture and
        // cytoscape-fcose 2.2.0 constraint displacement handling. The native
        // adapter does not embed fCoSE's dummy alignment nodes, so a fully
        // disconnected compound can distort both axes of a rigid root grid.
        // Preserve each compound's internal CoSE result, restore the rigid root
        // projection, and translate only branching compounds below the grid,
        // matching the constrained upstream outcome.
        val rootNodes = nodes.values.filter { node -> node.parent == null }
        val rootIds = rootNodes.mapTo(linkedSetOf(), WorkNode::id)
        val rootRows = db.getLayoutHints()
            .filter { hint ->
                hint.direction == ArchitectureAlignmentDirection.Row
            }
            .map { hint -> hint.members.filter(rootIds::contains) }
            .filter { alignment -> alignment.size > 1 }
        val rootColumns = db.getLayoutHints()
            .filter { hint ->
                hint.direction == ArchitectureAlignmentDirection.Column
            }
            .map { hint -> hint.members.filter(rootIds::contains) }
            .filter { alignment -> alignment.size > 1 }
        val config = db.config
        val groupBounds = calculateGroupBounds(
            nodes = nodes,
            groups = groups,
            padding = config.padding.toDouble(),
            iconSize = config.iconSize.toDouble(),
            fontSize = config.fontSize.toDouble(),
        )
        val detachedComponents = findDetachedTopLevelComponents(
            db = db,
            nodes = nodes,
            groups = groups,
        ).mapNotNull { component ->
            groupBounds[component.groupId]?.let { bounds ->
                DetachedGridComponent(
                    component = component,
                    bounds = bounds,
                )
            }
        }
        if (rootRows.size == 2 && rootColumns.size >= 2) {
            val projectedComponents = detachedComponents.filter { detached ->
                detached.component.pattern.shape in
                    RIGID_GRID_PROJECTED_COMPONENT_SHAPES
            }
            val spanDriver = projectedComponents.maxByOrNull { detached ->
                detached.requiredColumnSpan(
                    iconSize = config.iconSize.toDouble(),
                    nodeSeparation = config.nodeSeparation.toDouble(),
                )
            }
            if (spanDriver != null) {
                val centerDistance = config.iconSize.toDouble() *
                    (1.0 + config.idealEdgeLengthMultiplier.toDouble())
                val preservesCompactHorizontalRows =
                    projectedComponents.all { detached ->
                        detached.component.pattern.shape ==
                            DetachedComponentShape.HorizontalChain
                    }
                normalizeRigidRootRows(
                    nodes = nodes,
                    rootRows = rootRows,
                    center = if (preservesCompactHorizontalRows) {
                        rootNodes.map(WorkNode::y).average()
                    } else {
                        spanDriver.bounds.centerY
                    },
                    step = if (preservesCompactHorizontalRows) {
                        (
                            config.iconSize +
                                config.nodeSeparation +
                                config.fontSize
                            ).toDouble()
                    } else {
                        centerDistance
                    },
                )
                val currentColumnSpan = rootColumns
                    .mapNotNull { ids ->
                        ids.mapNotNull(nodes::get)
                            .takeIf { members -> members.isNotEmpty() }
                            ?.let { members ->
                                members.sumOf(WorkNode::x) / members.size
                            }
                    }
                    .let { centers ->
                        (centers.maxOrNull() ?: 0.0) -
                            (centers.minOrNull() ?: 0.0)
                    }
                normalizeRigidRootColumns(
                    nodes = nodes,
                    rootColumns = rootColumns,
                    center = spanDriver.bounds.centerX,
                    totalSpan = max(
                        currentColumnSpan,
                        spanDriver.requiredColumnSpan(
                            iconSize = config.iconSize.toDouble(),
                            nodeSeparation = config.nodeSeparation.toDouble(),
                        ),
                    ),
                )
            }
            return
        }
        if (rootRows.size < 3 || rootColumns.size < 2) {
            return
        }

        val projectedComponents = detachedComponents.filter { detached ->
            detached.component.pattern.shape in
                RIGID_GRID_PROJECTED_COMPONENT_SHAPES
        }
        if (projectedComponents.isEmpty()) {
            return
        }

        val linearComponents = projectedComponents.filter { detached ->
            detached.component.pattern.shape !=
                DetachedComponentShape.HorizontalStar
        }
        if (linearComponents.isNotEmpty()) {
            normalizeRigidRootRows(
                nodes = nodes,
                rootRows = rootRows,
                center = rootNodes.map(WorkNode::y).average(),
                step = config.iconSize.toDouble() *
                    (1.0 + config.idealEdgeLengthMultiplier.toDouble()),
            )
        }
        val firstRootRowCenter = rootRows
            .mapNotNull { ids ->
                ids.mapNotNull(nodes::get)
                    .takeIf { members -> members.isNotEmpty() }
                    ?.let { members ->
                        members.sumOf(WorkNode::y) / members.size
                    }
            }
            .minOrNull()
        if (firstRootRowCenter != null) {
            val compactGap = (
                config.iconSize +
                    config.nodeSeparation +
                    config.fontSize
                ).toDouble()
            val targetTop =
                firstRootRowCenter +
                    compactGap -
                    config.padding -
                    config.iconSize / 2.0
            linearComponents
                .filter { detached ->
                    detached.component.pattern.shape ==
                        DetachedComponentShape.VerticalChain
                }
                .forEach { detached ->
                    shiftNodes(
                        nodes = nodes,
                        ids = detached.component.descendantIds,
                        dx = 0.0,
                        dy = targetTop - detached.bounds.top,
                    )
                }
        }
        val spanDriver = linearComponents.maxByOrNull { detached ->
            detached.requiredColumnSpan(
                iconSize = config.iconSize.toDouble(),
                nodeSeparation = config.nodeSeparation.toDouble(),
            )
        }
        val rootColumnCenter = spanDriver?.bounds?.centerX
            ?: rootNodes.map(WorkNode::x).average()
        val minimumColumnSpan =
            rootRows.size *
                (config.iconSize + config.nodeSeparation).toDouble()
        val columnSpan = spanDriver?.let { detached ->
            max(
                minimumColumnSpan,
                detached.requiredColumnSpan(
                    iconSize = config.iconSize.toDouble(),
                    nodeSeparation = config.nodeSeparation.toDouble(),
                ),
            )
        } ?: minimumColumnSpan
        normalizeRigidRootColumns(
            nodes = nodes,
            rootColumns = rootColumns,
            center = rootColumnCenter,
            totalSpan = columnSpan,
        )
        val rootBounds = rootNodes
            .map { node ->
                node.visualBounds(
                    iconSize = config.iconSize.toDouble(),
                    fontSize = config.fontSize.toDouble(),
                )
            }
            .reduce(Bounds::union)
        var nextTop = rootBounds.bottom + config.nodeSeparation
        projectedComponents
            .filter { detached ->
                detached.component.pattern.shape ==
                    DetachedComponentShape.HorizontalStar
            }
            .forEach { detached ->
                shiftNodes(
                    nodes = nodes,
                    ids = detached.component.descendantIds,
                    dx = rootBounds.centerX - detached.bounds.centerX,
                    dy = nextTop - detached.bounds.top,
                )
                nextTop += detached.bounds.height + config.nodeSeparation
            }
    }

    private fun DetachedGridComponent.requiredColumnSpan(
        iconSize: Double,
        nodeSeparation: Double,
    ): Double = bounds.width + 2.0 * nodeSeparation +
        if (
            component.pattern.shape == DetachedComponentShape.HorizontalChain
        ) {
            0.0
        } else {
            iconSize
        }

    private fun normalizeRigidRootRows(
        nodes: Map<String, WorkNode>,
        rootRows: List<List<String>>,
        center: Double,
        step: Double,
    ) {
        val rows = rootRows
            .mapNotNull { ids ->
                val members = ids.mapNotNull(nodes::get)
                members.takeIf { it.isNotEmpty() }
            }
            .sortedBy { members -> members.sumOf(WorkNode::y) / members.size }
        if (rows.size < 2) {
            return
        }
        val totalSpan = step * (rows.size - 1)
        rows.forEachIndexed { index, members ->
            val target = center - totalSpan / 2.0 + step * index
            members.forEach { node -> node.y = target }
        }
    }

    private fun normalizeRigidRootColumns(
        nodes: Map<String, WorkNode>,
        rootColumns: List<List<String>>,
        center: Double,
        totalSpan: Double,
    ) {
        // cytoscape-fcose 2.2.0: constraint-handler.js projects the alignment
        // dummy nodes as one rigid component. The native force adapter has no
        // dummy graph, so compound repulsion can leave the two root columns
        // with a star-size-dependent span. Restore the rigid projection from
        // the declared row count before translating the disconnected compound.
        val columns = rootColumns
            .mapNotNull { ids ->
                val members = ids.mapNotNull(nodes::get)
                members.takeIf { it.isNotEmpty() }
            }
            .sortedBy { members -> members.sumOf(WorkNode::x) / members.size }
        if (columns.size < 2) {
            return
        }
        val step = totalSpan / (columns.size - 1)
        columns.forEachIndexed { index, members ->
            val target = center - totalSpan / 2.0 + step * index
            members.forEach { node -> node.x = target }
        }
    }

    private fun resolveCompoundGroups(
        nodes: LinkedHashMap<String, WorkNode>,
        groups: List<ArchitectureGroup>,
        padding: Double,
        nodeSeparation: Double,
        groupAlignments: Map<String, ArchitectureAlignment>,
        iconSize: Double,
        fontSize: Double,
    ): Map<String, Bounds> {
        val groupsByParent = groups.groupBy(ArchitectureGroup::parent)
        val descendants = groups.associate { group ->
            group.id to nodes.values.filter { node ->
                isDescendantOf(node.parent, group.id, groups)
            }.mapTo(linkedSetOf(), WorkNode::id)
        }
        repeat(GROUP_OVERLAP_PASSES) {
            val bounds = calculateGroupBounds(
                nodes = nodes,
                groups = groups,
                padding = padding,
                iconSize = iconSize,
                fontSize = fontSize,
            )
            var moved = false
            groupsByParent.values.forEach { siblings ->
                for (firstIndex in 0 until siblings.lastIndex) {
                    for (secondIndex in firstIndex + 1 until siblings.size) {
                        val first = siblings[firstIndex]
                        val second = siblings[secondIndex]
                        val firstBounds = bounds[first.id] ?: continue
                        val secondBounds = bounds[second.id] ?: continue
                        val overlapX = min(firstBounds.right, secondBounds.right) -
                            max(firstBounds.left, secondBounds.left) +
                            nodeSeparation
                        val overlapY = min(firstBounds.bottom, secondBounds.bottom) -
                            max(firstBounds.top, secondBounds.top) +
                            nodeSeparation
                        if (overlapX <= 0.0 || overlapY <= 0.0) {
                            continue
                        }
                        val alignment = groupAlignments[
                            architectureGroupAlignmentKey(first.id, second.id)
                        ]
                        val moveX = alignment == ArchitectureAlignment.Horizontal ||
                            alignment == null && overlapX <= overlapY
                        val amount = if (moveX) overlapX else overlapY
                        val firstCenter = if (moveX) firstBounds.centerX else firstBounds.centerY
                        val secondCenter = if (moveX) secondBounds.centerX else secondBounds.centerY
                        val sign = if (secondCenter >= firstCenter) 1.0 else -1.0
                        shiftNodes(
                            nodes = nodes,
                            ids = descendants.getValue(second.id),
                            dx = if (moveX) amount * sign else 0.0,
                            dy = if (moveX) 0.0 else amount * sign,
                        )
                        moved = true
                    }
                }
            }
            if (!moved) {
                return bounds
            }
        }
        return calculateGroupBounds(
            nodes = nodes,
            groups = groups,
            padding = padding,
            iconSize = iconSize,
            fontSize = fontSize,
        )
    }

    private fun calculateGroupBounds(
        nodes: Map<String, WorkNode>,
        groups: List<ArchitectureGroup>,
        padding: Double,
        iconSize: Double,
        fontSize: Double,
    ): Map<String, Bounds> {
        val result = linkedMapOf<String, Bounds>()
        val groupsByParent = groups.groupBy(ArchitectureGroup::parent)

        fun calculate(group: ArchitectureGroup): Bounds {
            result[group.id]?.let { return it }
            val directNodeBounds = nodes.values
                .filter { node -> node.parent == group.id }
                .map { node ->
                    Bounds(
                        left = node.x - iconSize / 2.0,
                        top = node.y - iconSize / 2.0,
                        right = node.x + iconSize / 2.0,
                        bottom = node.y + iconSize / 2.0 +
                            if (node.hasLabel) fontSize + 2.0 else 0.0,
                    )
                }
            val childGroupBounds = groupsByParent[group.id].orEmpty().map(::calculate)
            val content = (directNodeBounds + childGroupBounds).reduceOrNull(Bounds::union)
                ?: Bounds(
                    left = 0.0,
                    top = 0.0,
                    right = iconSize,
                    bottom = iconSize,
                )
            return content.inflate(padding, padding).also { result[group.id] = it }
        }
        groups.forEach(::calculate)
        return result
    }

    private fun WorkNode.bounds(): Bounds = Bounds(
        left = x - size / 2.0,
        top = y - size / 2.0,
        right = x + size / 2.0,
        bottom = y + size / 2.0,
    )

    private fun WorkNode.visualBounds(
        iconSize: Double,
        fontSize: Double,
    ): Bounds = Bounds(
        left = x - iconSize / 2.0,
        top = y - iconSize / 2.0,
        right = x + iconSize / 2.0,
        bottom = y + iconSize / 2.0 + if (hasLabel) fontSize + 2.0 else 0.0,
    )

    private fun isDescendantOf(
        initialParent: String?,
        ancestor: String,
        groups: List<ArchitectureGroup>,
    ): Boolean {
        val parentById = groups.associate { group -> group.id to group.parent }
        var parent = initialParent
        while (parent != null) {
            if (parent == ancestor) {
                return true
            }
            parent = parentById[parent]
        }
        return false
    }

    private fun shiftNodes(
        nodes: Map<String, WorkNode>,
        ids: Set<String>,
        dx: Double,
        dy: Double,
    ) {
        ids.forEach { id ->
            nodes[id]?.let { node ->
                node.x += dx
                node.y += dy
            }
        }
    }

    private fun architectureRelativeGap(
        db: ArchitectureDb,
        firstId: String,
        secondId: String,
        direction: ArchitectureDirection,
        defaultGap: Double,
    ): Double {
        val includesJunction =
            db.getNode(firstId) is ArchitectureJunction ||
                db.getNode(secondId) is ArchitectureJunction
        return if (
            includesJunction &&
            direction in setOf(ArchitectureDirection.L, ArchitectureDirection.R)
        ) {
            defaultGap + db.config.iconSize
        } else {
            defaultGap
        }
    }

    private fun ArchitectureDirection.toRelativeConstraint(
        adjacentId: String,
        currentId: String,
        gap: Double,
    ): RelativeConstraint = when (this) {
        ArchitectureDirection.L -> RelativeConstraint(
            before = adjacentId,
            after = currentId,
            axis = Axis.X,
            gap = gap,
        )
        ArchitectureDirection.R -> RelativeConstraint(
            before = currentId,
            after = adjacentId,
            axis = Axis.X,
            gap = gap,
        )
        ArchitectureDirection.T -> RelativeConstraint(
            before = adjacentId,
            after = currentId,
            axis = Axis.Y,
            gap = gap,
        )
        ArchitectureDirection.B -> RelativeConstraint(
            before = currentId,
            after = adjacentId,
            axis = Axis.Y,
            gap = gap,
        )
    }

    private data class Constraints(
        val horizontalAlignments: List<List<String>>,
        val verticalAlignments: List<List<String>>,
        val relativePlacements: List<RelativeConstraint>,
    )

    private data class RelativeConstraint(
        val before: String,
        val after: String,
        val axis: Axis,
        val gap: Double,
    )

    private data class ForceBody(
        val owner: String?,
        val nodeIds: Set<String>,
        val bounds: Bounds,
        val isGroup: Boolean,
    )

    private data class DetachedGridComponent(
        val component: DetachedComponent,
        val bounds: Bounds,
    )

    private data class DetachedComponent(
        val groupId: String,
        val descendantIds: Set<String>,
        val pattern: DetachedComponentPattern,
    )

    private data class DetachedComponentPattern(
        val shape: DetachedComponentShape,
        val orderedNodeIds: List<String>,
    )

    private enum class DetachedComponentShape {
        Single,
        HorizontalChain,
        VerticalChain,
        HorizontalStar,
    }

    private data class DistanceVector(
        val x: Double,
        val y: Double,
    )

    private enum class Axis {
        X,
        Y,
    }

    private data class WorkNode(
        val id: String,
        val parent: String?,
        var x: Double,
        var y: Double,
        val size: Double,
        val hasLabel: Boolean,
        val isJunction: Boolean,
        var forceX: Double = 0.0,
        var forceY: Double = 0.0,
    ) {
        fun resetForces() {
            forceX = 0.0
            forceY = 0.0
        }
    }

    private data class Bounds(
        val left: Double,
        val top: Double,
        val right: Double,
        val bottom: Double,
    ) {
        val centerX: Double get() = (left + right) / 2.0
        val centerY: Double get() = (top + bottom) / 2.0
        val width: Double get() = right - left
        val height: Double get() = bottom - top

        fun intersects(other: Bounds): Boolean =
            left < other.right &&
                right > other.left &&
                top < other.bottom &&
                bottom > other.top

        fun union(other: Bounds): Bounds = Bounds(
            left = min(left, other.left),
            top = min(top, other.top),
            right = max(right, other.right),
            bottom = max(bottom, other.bottom),
        )

        fun inflate(horizontal: Double, vertical: Double): Bounds = Bounds(
            left = left - horizontal,
            top = top - vertical,
            right = right + horizontal,
            bottom = bottom + vertical,
        )

        fun toSceneRect(): SceneRect = SceneRect(
            left = left.toFloat(),
            top = top.toFloat(),
            right = right.toFloat(),
            bottom = bottom.toFloat(),
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * diagrams/architecture/architectureSeed.ts -> withSeededRandom/mulberry32.
     */
    private class Mulberry32(
        seed: UInt,
    ) : Random() {
        private var state: UInt = seed

        override fun nextBits(bitCount: Int): Int {
            state += 0x6D2B79F5u
            var value = state
            value = (value xor (value shr 15)) * (value or 1u)
            value = value xor (value + (value xor (value shr 7)) * (value or 61u))
            val result = value xor (value shr 14)
            return if (bitCount == 32) {
                result.toInt()
            } else {
                (result shr (32 - bitCount)).toInt()
            }
        }
    }

    private fun layoutError(message: String): GMResult.Err<MermaidError> =
        GMResult.Err(MermaidError.Layout(message))

    private const val DEFAULT_GROUP = "default"
    private const val DEFAULT_IDEAL_EDGE_LENGTH = 50.0
    private const val CROSS_GROUP_ELASTICITY = 0.001
    private const val NODE_REPULSION = 4500.0
    private const val SPRING_SCALE = 0.02
    private const val REPULSION_SCALE = 0.08
    private const val SEPARATION_SCALE = 0.5
    private const val GEOMETRY_EPSILON = 1e-9
    private const val MAX_DISPLACEMENT = 12.0
    private const val MIN_COOLING = 0.04
    private const val MIN_ITERATIONS = 30
    private const val MAX_NATIVE_ITERATIONS = 600
    private const val CONVERGENCE_THRESHOLD = 0.01
    private const val OVERLAP_PASSES = 20
    private const val GROUP_OVERLAP_PASSES = 20

    private val HORIZONTAL_DIRECTIONS = setOf(
        ArchitectureDirection.L,
        ArchitectureDirection.R,
    )

    private val VERTICAL_DIRECTIONS = setOf(
        ArchitectureDirection.T,
        ArchitectureDirection.B,
    )

    private val RIGID_GRID_PROJECTED_COMPONENT_SHAPES = setOf(
        DetachedComponentShape.HorizontalChain,
        DetachedComponentShape.VerticalChain,
        DetachedComponentShape.HorizontalStar,
    )

    private val DIRECTION_SHIFTS = linkedMapOf(
        ArchitectureDirection.L to ArchitecturePosition(-1, 0),
        ArchitectureDirection.R to ArchitecturePosition(1, 0),
        ArchitectureDirection.T to ArchitecturePosition(0, 1),
        ArchitectureDirection.B to ArchitecturePosition(0, -1),
    )
}
