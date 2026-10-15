package com.swithun.cmpmermaid.core.architecture.upstream.cose

import com.swithun.cmpmermaid.core.GMResult

/**
 * cose-base 2.2.0: src/ConstraintHandler.js.
 *
 * Mermaid's incremental Architecture path sets
 * TRANSFORM_ON_CONSTRAINT_HANDLING=false, so the Matrix/SVD Procrustes branch
 * is deliberately absent here. The reached enforcement path is translated in
 * the same fixed -> alignment -> relative-placement order as upstream.
 */
internal object ConstraintHandler {
    fun handleConstraints(layout: CoseLayout): GMResult<Unit, String> {
        val constraints = layout.constraints
        val allNodes = layout.getAllNodes().map { node -> node as CoseNode }
        val leafNodes = allNodes.filter { node -> node.child == null }
        val idToNode = leafNodes.associateByTo(linkedMapOf(), CoseNode::id)
        val xCoords = leafNodes.associateTo(linkedMapOf()) { node -> node.id to node.centerX }
        val yCoords = leafNodes.associateTo(linkedMapOf()) { node -> node.id to node.centerY }

        constraints.relativePlacementConstraint?.forEach { constraint ->
            if (constraint.gap == null) {
                constraint.gap = when {
                    constraint.left != null && constraint.right != null -> {
                        val left = idToNode[constraint.left]
                            ?: return missingNode(constraint.left)
                        val right = idToNode[constraint.right]
                            ?: return missingNode(constraint.right)
                        layout.defaultEdgeLength + left.width / 2.0 + right.width / 2.0
                    }
                    constraint.top != null && constraint.bottom != null -> {
                        val top = idToNode[constraint.top]
                            ?: return missingNode(constraint.top)
                        val bottom = idToNode[constraint.bottom]
                            ?: return missingNode(constraint.bottom)
                        layout.defaultEdgeLength + top.height / 2.0 + bottom.height / 2.0
                    }
                    else -> return GMResult.Err("Malformed relative placement constraint")
                }
            }
        }

        val fixedNodes = constraints.fixedNodeConstraint
            ?.mapTo(linkedSetOf(), CoseFixedNodeConstraint::nodeId)
            .orEmpty()
        val dag = buildConstraintDag(constraints.relativePlacementConstraint.orEmpty())

        if (CoseConstants.ENFORCE_CONSTRAINTS) {
            when (
                val result = enforceFixedNodes(
                    fixedConstraints = constraints.fixedNodeConstraint.orEmpty(),
                    xCoords = xCoords,
                    yCoords = yCoords,
                )
            ) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
            when (
                val result = enforceAlignments(
                    alignment = constraints.alignmentConstraint,
                    fixedNodes = fixedNodes,
                    xCoords = xCoords,
                    yCoords = yCoords,
                )
            ) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
            when (
                val result = enforceRelativePlacements(
                    constraints = constraints,
                    dag = dag,
                    fixedNodes = fixedNodes,
                    xCoords = xCoords,
                    yCoords = yCoords,
                )
            ) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }

        leafNodes.forEach { node ->
            val x = xCoords[node.id] ?: return missingNode(node.id)
            val y = yCoords[node.id] ?: return missingNode(node.id)
            node.setCenter(x, y)
        }
        return GMResult.Ok(Unit)
    }

    fun initConstraintVariables(layout: CoseLayout): CoseConstraintState {
        val allNodes = layout.getAllNodes().map { node -> node as CoseNode }
        val idToNodeMap = allNodes.associateByTo(linkedMapOf(), CoseNode::id)
        val fixedNodeSet = layout.constraints.fixedNodeConstraint
            ?.mapTo(linkedSetOf(), CoseFixedNodeConstraint::nodeId)
            ?: linkedSetOf()

        if (fixedNodeSet.isNotEmpty()) {
            fun calculateCompoundWeight(compoundNode: CoseNode): Double {
                var fixedNodeWeight = 0.0
                compoundNode.child?.nodes?.forEach { rawNode ->
                    val node = rawNode as CoseNode
                    fixedNodeWeight += if (node.child == null) {
                        if (node.id in fixedNodeSet) 100.0 else 0.0
                    } else {
                        calculateCompoundWeight(node)
                    }
                }
                return fixedNodeWeight
            }
            allNodes.filter { node -> node.child != null }.forEach { node ->
                val weight = calculateCompoundWeight(node)
                if (weight > 0.0) {
                    node.fixedNodeWeight = weight
                }
            }
        }

        val relative = layout.constraints.relativePlacementConstraint.orEmpty()
        if (relative.isEmpty()) {
            return CoseConstraintState(
                idToNodeMap = idToNodeMap,
                fixedNodeSet = fixedNodeSet,
            )
        }

        val verticalDummies = buildDummies(
            groups = layout.constraints.alignmentConstraint?.vertical.orEmpty(),
            fixedNodes = fixedNodeSet,
        )
        val horizontalDummies = buildDummies(
            groups = layout.constraints.alignmentConstraint?.horizontal.orEmpty(),
            fixedNodes = fixedNodeSet,
        )
        val fixedNodesOnHorizontal = linkedSetOf<String>().apply {
            addAll(fixedNodeSet)
            addAll(verticalDummies.fixedDummies)
        }
        val fixedNodesOnVertical = linkedSetOf<String>().apply {
            addAll(fixedNodeSet)
            addAll(horizontalDummies.fixedDummies)
        }
        val nodesInRelativeHorizontal = mutableListOf<String>()
        val nodesInRelativeVertical = mutableListOf<String>()
        val horizontalRelations = linkedMapOf<String, MutableList<CoseRelaxedRelation>>()
        val verticalRelations = linkedMapOf<String, MutableList<CoseRelaxedRelation>>()
        val horizontalTempPositions = linkedMapOf<String, Double>()
        val verticalTempPositions = linkedMapOf<String, Double>()

        fun register(
            id: String,
            nodes: MutableList<String>,
            relations: MutableMap<String, MutableList<CoseRelaxedRelation>>,
            tempPositions: MutableMap<String, Double>,
            dummyToNodes: Map<String, List<String>>,
            coordinate: (CoseNode) -> Double,
        ) {
            if (id in nodes) {
                return
            }
            nodes += id
            relations[id] = mutableListOf()
            val actualId = dummyToNodes[id]?.firstOrNull() ?: id
            tempPositions[id] = coordinate(idToNodeMap.getValue(actualId))
        }

        relative.forEach { constraint ->
            val gap = constraint.gap ?: 0.0
            if (constraint.left != null && constraint.right != null) {
                val left = verticalDummies.nodeToDummy[constraint.left] ?: constraint.left
                val right = verticalDummies.nodeToDummy[constraint.right] ?: constraint.right
                register(
                    left,
                    nodesInRelativeHorizontal,
                    horizontalRelations,
                    horizontalTempPositions,
                    verticalDummies.dummyToNodes,
                    CoseNode::centerX,
                )
                register(
                    right,
                    nodesInRelativeHorizontal,
                    horizontalRelations,
                    horizontalTempPositions,
                    verticalDummies.dummyToNodes,
                    CoseNode::centerX,
                )
                horizontalRelations.getValue(left) += CoseRelaxedRelation(after = right, gap = gap)
                horizontalRelations.getValue(right) += CoseRelaxedRelation(before = left, gap = gap)
            } else if (constraint.top != null && constraint.bottom != null) {
                val top = horizontalDummies.nodeToDummy[constraint.top] ?: constraint.top
                val bottom = horizontalDummies.nodeToDummy[constraint.bottom] ?: constraint.bottom
                register(
                    top,
                    nodesInRelativeVertical,
                    verticalRelations,
                    verticalTempPositions,
                    horizontalDummies.dummyToNodes,
                    CoseNode::centerY,
                )
                register(
                    bottom,
                    nodesInRelativeVertical,
                    verticalRelations,
                    verticalTempPositions,
                    horizontalDummies.dummyToNodes,
                    CoseNode::centerY,
                )
                verticalRelations.getValue(top) += CoseRelaxedRelation(after = bottom, gap = gap)
                verticalRelations.getValue(bottom) += CoseRelaxedRelation(before = top, gap = gap)
            }
        }

        return CoseConstraintState(
            idToNodeMap = idToNodeMap,
            fixedNodeSet = fixedNodeSet,
            dummyToNodeForVerticalAlignment = verticalDummies.dummyToNodes,
            dummyToNodeForHorizontalAlignment = horizontalDummies.dummyToNodes,
            fixedNodesOnHorizontal = fixedNodesOnHorizontal,
            fixedNodesOnVertical = fixedNodesOnVertical,
            nodesInRelativeHorizontal = nodesInRelativeHorizontal,
            nodesInRelativeVertical = nodesInRelativeVertical,
            horizontalRelations = horizontalRelations,
            verticalRelations = verticalRelations,
            horizontalTempPositions = horizontalTempPositions,
            verticalTempPositions = verticalTempPositions,
        )
    }

    fun updateDisplacements(
        layout: CoseLayout,
        state: CoseConstraintState,
    ) {
        layout.constraints.fixedNodeConstraint?.forEach { fixed ->
            state.idToNodeMap.getValue(fixed.nodeId).apply {
                displacementX = 0.0
                displacementY = 0.0
            }
        }
        layout.constraints.alignmentConstraint?.vertical?.forEach { alignment ->
            if (alignment.isEmpty()) {
                return@forEach
            }
            val displacement = if (alignment.any { id -> id in state.fixedNodeSet }) {
                0.0
            } else {
                alignment.sumOf { id -> state.idToNodeMap.getValue(id).displacementX } /
                    alignment.size
            }
            alignment.forEach { id ->
                state.idToNodeMap.getValue(id).displacementX = displacement
            }
        }
        layout.constraints.alignmentConstraint?.horizontal?.forEach { alignment ->
            if (alignment.isEmpty()) {
                return@forEach
            }
            val displacement = if (alignment.any { id -> id in state.fixedNodeSet }) {
                0.0
            } else {
                alignment.sumOf { id -> state.idToNodeMap.getValue(id).displacementY } /
                    alignment.size
            }
            alignment.forEach { id ->
                state.idToNodeMap.getValue(id).displacementY = displacement
            }
        }

        if (layout.constraints.relativePlacementConstraint.isNullOrEmpty()) {
            return
        }
        if (layout.totalIterations % 10 == 0) {
            shuffleTail(state.nodesInRelativeHorizontal, layout.random)
            shuffleTail(state.nodesInRelativeVertical, layout.random)
        }
        relaxAxis(
            nodes = state.nodesInRelativeHorizontal,
            fixedNodes = state.fixedNodesOnHorizontal,
            dummyToNodes = state.dummyToNodeForVerticalAlignment,
            idToNode = state.idToNodeMap,
            relations = state.horizontalRelations,
            tempPositions = state.horizontalTempPositions,
            getDisplacement = CoseNode::displacementX,
            setDisplacement = { node, value -> node.displacementX = value },
        )
        relaxAxis(
            nodes = state.nodesInRelativeVertical,
            fixedNodes = state.fixedNodesOnVertical,
            dummyToNodes = state.dummyToNodeForHorizontalAlignment,
            idToNode = state.idToNodeMap,
            relations = state.verticalRelations,
            tempPositions = state.verticalTempPositions,
            getDisplacement = CoseNode::displacementY,
            setDisplacement = { node, value -> node.displacementY = value },
        )
    }

    private fun enforceFixedNodes(
        fixedConstraints: List<CoseFixedNodeConstraint>,
        xCoords: MutableMap<String, Double>,
        yCoords: MutableMap<String, Double>,
    ): GMResult<Unit, String> {
        if (fixedConstraints.isEmpty()) {
            return GMResult.Ok(Unit)
        }
        var translationX = 0.0
        var translationY = 0.0
        fixedConstraints.forEach { fixed ->
            val currentX = xCoords[fixed.nodeId] ?: return missingNode(fixed.nodeId)
            val currentY = yCoords[fixed.nodeId] ?: return missingNode(fixed.nodeId)
            translationX += fixed.x - currentX
            translationY += fixed.y - currentY
        }
        translationX /= fixedConstraints.size
        translationY /= fixedConstraints.size
        xCoords.keys.toList().forEach { id -> xCoords[id] = xCoords.getValue(id) + translationX }
        yCoords.keys.toList().forEach { id -> yCoords[id] = yCoords.getValue(id) + translationY }
        fixedConstraints.forEach { fixed ->
            xCoords[fixed.nodeId] = fixed.x
            yCoords[fixed.nodeId] = fixed.y
        }
        return GMResult.Ok(Unit)
    }

    private fun enforceAlignments(
        alignment: CoseAlignmentConstraint?,
        fixedNodes: Set<String>,
        xCoords: MutableMap<String, Double>,
        yCoords: MutableMap<String, Double>,
    ): GMResult<Unit, String> {
        alignment?.vertical?.forEach { group ->
            if (group.isEmpty()) {
                return@forEach
            }
            val fixed = group.firstOrNull { id -> id in fixedNodes }
            val x = if (fixed != null) {
                xCoords[fixed] ?: return missingNode(fixed)
            } else {
                when (val average = average(group, xCoords)) {
                    is GMResult.Ok -> average.value
                    is GMResult.Err -> return average
                }
            }
            group.filterNot { id -> id in fixedNodes }.forEach { id ->
                if (id !in xCoords) {
                    return missingNode(id)
                }
                xCoords[id] = x
            }
        }
        alignment?.horizontal?.forEach { group ->
            if (group.isEmpty()) {
                return@forEach
            }
            val fixed = group.firstOrNull { id -> id in fixedNodes }
            val y = if (fixed != null) {
                yCoords[fixed] ?: return missingNode(fixed)
            } else {
                when (val average = average(group, yCoords)) {
                    is GMResult.Ok -> average.value
                    is GMResult.Err -> return average
                }
            }
            group.filterNot { id -> id in fixedNodes }.forEach { id ->
                if (id !in yCoords) {
                    return missingNode(id)
                }
                yCoords[id] = y
            }
        }
        return GMResult.Ok(Unit)
    }

    private fun enforceRelativePlacements(
        constraints: CoseConstraints,
        dag: Dag,
        fixedNodes: Set<String>,
        xCoords: MutableMap<String, Double>,
        yCoords: MutableMap<String, Double>,
    ): GMResult<Unit, String> {
        if (constraints.relativePlacementConstraint.isNullOrEmpty()) {
            return GMResult.Ok(Unit)
        }
        val verticalDummies = buildDummies(
            constraints.alignmentConstraint?.vertical.orEmpty(),
            fixedNodes,
        )
        val horizontalDummies = buildDummies(
            constraints.alignmentConstraint?.horizontal.orEmpty(),
            fixedNodes,
        )
        val verticalDummyPositions = verticalDummies.dummyToNodes.mapValuesTo(linkedMapOf()) {
            (_, ids) -> xCoords.getValue(ids.first())
        }
        val horizontalDummyPositions = horizontalDummies.dummyToNodes.mapValuesTo(linkedMapOf()) {
            (_, ids) -> yCoords.getValue(ids.first())
        }
        val fixedNodesOnHorizontal = linkedSetOf<String>().apply {
            addAll(fixedNodes)
            addAll(verticalDummies.fixedDummies)
        }
        val fixedNodesOnVertical = linkedSetOf<String>().apply {
            addAll(fixedNodes)
            addAll(horizontalDummies.fixedDummies)
        }
        val horizontalDag = projectDag(
            dag = dag,
            direction = ConstraintDirection.Horizontal,
            nodeToDummy = verticalDummies.nodeToDummy,
        )
        val verticalDag = projectDag(
            dag = dag,
            direction = ConstraintDirection.Vertical,
            nodeToDummy = horizontalDummies.nodeToDummy,
        )

        val horizontalSources = componentSources(horizontalDag)
        val verticalSources = componentSources(verticalDag)
        val horizontalPositions = when (
            val result = findAppropriatePositionForRelativePlacement(
                graph = horizontalDag,
                coordinates = xCoords,
                fixedNodes = fixedNodesOnHorizontal,
                dummyPositions = verticalDummyPositions,
                componentSources = horizontalSources,
            )
        ) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val verticalPositions = when (
            val result = findAppropriatePositionForRelativePlacement(
                graph = verticalDag,
                coordinates = yCoords,
                fixedNodes = fixedNodesOnVertical,
                dummyPositions = horizontalDummyPositions,
                componentSources = verticalSources,
            )
        ) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }

        horizontalPositions.forEach { (id, position) ->
            val actual = verticalDummies.dummyToNodes[id] ?: listOf(id)
            actual.forEach { nodeId -> xCoords[nodeId] = position }
        }
        verticalPositions.forEach { (id, position) ->
            val actual = horizontalDummies.dummyToNodes[id] ?: listOf(id)
            actual.forEach { nodeId -> yCoords[nodeId] = position }
        }
        return GMResult.Ok(Unit)
    }

    private fun findAppropriatePositionForRelativePlacement(
        graph: Dag,
        coordinates: Map<String, Double>,
        fixedNodes: Set<String>,
        dummyPositions: Map<String, Double>,
        componentSources: List<List<String>>,
    ): GMResult<Map<String, Double>, String> {
        if (graph.isEmpty()) {
            return GMResult.Ok(emptyMap())
        }
        val inDegrees = graph.keys.associateWithTo(linkedMapOf()) { 0 }
        graph.values.flatten().forEach { adjacent ->
            inDegrees[adjacent.id] = inDegrees.getValue(adjacent.id) + 1
        }
        val positions = linkedMapOf<String, Double>()
        val past = linkedMapOf<String, MutableSet<String>>()
        val queue = ArrayDeque<String>()
        inDegrees.forEach { (id, degree) ->
            if (degree == 0) {
                queue.addLast(id)
            } else {
                positions[id] = Double.NEGATIVE_INFINITY
            }
            past[id] = linkedSetOf(id)
        }

        componentSources.forEach { component ->
            val fixedIds = component.filter { id -> id in fixedNodes }
            val position = if (fixedIds.isNotEmpty()) {
                fixedIds.sumOf { id -> coordinateOf(id, coordinates, dummyPositions) } /
                    fixedIds.size
            } else {
                component.sumOf { id -> coordinateOf(id, coordinates, dummyPositions) } /
                    component.size
            }
            component.forEach { id -> positions[id] = position }
        }

        var processed = 0
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            processed++
            val currentPosition = positions[current]
                ?: return GMResult.Err("Constraint source $current has no position")
            graph.getValue(current).forEach { adjacent ->
                val required = currentPosition + adjacent.gap
                if (positions.getValue(adjacent.id) < required) {
                    if (adjacent.id in fixedNodes) {
                        val fixedPosition = coordinateOf(
                            adjacent.id,
                            coordinates,
                            dummyPositions,
                        )
                        positions[adjacent.id] = fixedPosition
                        if (fixedPosition < required) {
                            val difference = required - fixedPosition
                            past.getValue(current).forEach { id ->
                                positions[id] = positions.getValue(id) - difference
                            }
                        }
                    } else {
                        positions[adjacent.id] = required
                    }
                }
                inDegrees[adjacent.id] = inDegrees.getValue(adjacent.id) - 1
                if (inDegrees.getValue(adjacent.id) == 0) {
                    queue.addLast(adjacent.id)
                }
                past[adjacent.id] = linkedSetOf<String>().apply {
                    addAll(past.getValue(current))
                    addAll(past.getValue(adjacent.id))
                }
            }
        }
        if (processed != graph.size) {
            return GMResult.Err("Relative placement constraints contain a cycle")
        }

        val sinkNodes = graph.filterValues(List<Adjacent>::isEmpty).keys
        val components = mutableListOf<MutableSet<String>>()
        past.forEach { (id, values) ->
            if (id !in sinkNodes || values.any { nodeId -> nodeId in fixedNodes }) {
                return@forEach
            }
            val existing = components.indexOfFirst { component ->
                values.firstOrNull() in component
            }
            if (existing < 0) {
                components += values.toMutableSet()
            } else {
                components[existing].addAll(values)
            }
        }
        components.forEach { component ->
            val before = component.map { id -> coordinateOf(id, coordinates, dummyPositions) }
            val after = component.map(positions::getValue)
            val difference =
                (before.min() + before.max()) / 2.0 -
                    (after.min() + after.max()) / 2.0
            component.forEach { id -> positions[id] = positions.getValue(id) + difference }
        }
        return GMResult.Ok(positions)
    }

    private fun buildConstraintDag(
        constraints: List<CoseRelativePlacementConstraint>,
    ): Dag {
        val dag = linkedMapOf<String, MutableList<Adjacent>>()
        constraints.forEach { constraint ->
            if (constraint.left != null && constraint.right != null) {
                dag.getOrPut(constraint.left) { mutableListOf() } += Adjacent(
                    id = constraint.right,
                    gap = constraint.gap ?: 0.0,
                    direction = ConstraintDirection.Horizontal,
                )
                dag.getOrPut(constraint.right) { mutableListOf() }
            } else if (constraint.top != null && constraint.bottom != null) {
                dag.getOrPut(constraint.top) { mutableListOf() } += Adjacent(
                    id = constraint.bottom,
                    gap = constraint.gap ?: 0.0,
                    direction = ConstraintDirection.Vertical,
                )
                dag.getOrPut(constraint.bottom) { mutableListOf() }
            }
        }
        return dag
    }

    private fun projectDag(
        dag: Dag,
        direction: ConstraintDirection,
        nodeToDummy: Map<String, String>,
    ): Dag {
        val projected = linkedMapOf<String, MutableList<Adjacent>>()
        dag.forEach { (id, adjacentNodes) ->
            adjacentNodes.filter { adjacent -> adjacent.direction == direction }.forEach { adjacent ->
                val source = nodeToDummy[id] ?: id
                val target = nodeToDummy[adjacent.id] ?: adjacent.id
                projected.getOrPut(source) { mutableListOf() } += adjacent.copy(id = target)
                projected.getOrPut(target) { mutableListOf() }
            }
        }
        return projected
    }

    private fun componentSources(dag: Dag): List<List<String>> {
        val components = findComponents(toUndirected(dag))
        val reversed = reverse(dag)
        return components.map { component ->
            component.filter { id -> reversed.getValue(id).isEmpty() }
        }
    }

    private fun findComponents(graph: Dag): List<List<String>> {
        val components = mutableListOf<List<String>>()
        val visited = linkedSetOf<String>()
        graph.keys.forEach { first ->
            if (first in visited) {
                return@forEach
            }
            val component = mutableListOf<String>()
            val queue = ArrayDeque<String>()
            queue.addLast(first)
            visited += first
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                component += current
                graph.getValue(current).forEach { adjacent ->
                    if (visited.add(adjacent.id)) {
                        queue.addLast(adjacent.id)
                    }
                }
            }
            components += component
        }
        return components
    }

    private fun toUndirected(dag: Dag): Dag {
        val result = dag.keys.associateWithTo(linkedMapOf()) { mutableListOf<Adjacent>() }
        dag.forEach { (id, adjacentNodes) ->
            adjacentNodes.forEach { adjacent ->
                result.getValue(id) += adjacent
                result.getValue(adjacent.id) += adjacent.copy(id = id)
            }
        }
        return result
    }

    private fun reverse(dag: Dag): Dag {
        val result = dag.keys.associateWithTo(linkedMapOf()) { mutableListOf<Adjacent>() }
        dag.forEach { (id, adjacentNodes) ->
            adjacentNodes.forEach { adjacent ->
                result.getValue(adjacent.id) += adjacent.copy(id = id)
            }
        }
        return result
    }

    private fun buildDummies(
        groups: List<List<String>>,
        fixedNodes: Set<String>,
    ): DummyMaps {
        val nodeToDummy = linkedMapOf<String, String>()
        val dummyToNodes = linkedMapOf<String, List<String>>()
        val fixedDummies = linkedSetOf<String>()
        groups.forEachIndexed { index, nodes ->
            val dummy = "dummy$index"
            dummyToNodes[dummy] = nodes
            nodes.forEach { id ->
                nodeToDummy[id] = dummy
                if (id in fixedNodes) {
                    fixedDummies += dummy
                }
            }
        }
        return DummyMaps(nodeToDummy, dummyToNodes, fixedDummies)
    }

    private fun relaxAxis(
        nodes: List<String>,
        fixedNodes: Set<String>,
        dummyToNodes: Map<String, List<String>>,
        idToNode: Map<String, CoseNode>,
        relations: Map<String, List<CoseRelaxedRelation>>,
        tempPositions: MutableMap<String, Double>,
        getDisplacement: (CoseNode) -> Double,
        setDisplacement: (CoseNode, Double) -> Unit,
    ) {
        nodes.forEach { id ->
            if (id in fixedNodes) {
                return@forEach
            }
            val actualIds = dummyToNodes[id] ?: listOf(id)
            var displacement = getDisplacement(idToNode.getValue(actualIds.first()))
            relations.getValue(id).forEach { relation ->
                if (relation.after != null) {
                    val difference =
                        tempPositions.getValue(relation.after) -
                            tempPositions.getValue(id) -
                            displacement
                    if (difference < relation.gap) {
                        displacement -= relation.gap - difference
                    }
                } else if (relation.before != null) {
                    val difference =
                        tempPositions.getValue(id) -
                            tempPositions.getValue(relation.before) +
                            displacement
                    if (difference < relation.gap) {
                        displacement += relation.gap - difference
                    }
                }
            }
            tempPositions[id] = tempPositions.getValue(id) + displacement
            actualIds.forEach { actualId ->
                setDisplacement(idToNode.getValue(actualId), displacement)
            }
        }
    }

    private fun shuffleTail(
        array: MutableList<String>,
        random: CoseRandom,
    ) {
        var index = array.lastIndex
        val lowerBound = 2.0 * array.size / 3.0
        while (index >= lowerBound) {
            val other = kotlin.math.floor(random.nextDouble() * (index + 1)).toInt()
            val value = array[index]
            array[index] = array[other]
            array[other] = value
            index--
        }
    }

    private fun coordinateOf(
        id: String,
        coordinates: Map<String, Double>,
        dummyPositions: Map<String, Double>,
    ): Double = coordinates[id] ?: dummyPositions.getValue(id)

    private fun average(
        ids: Collection<String>,
        coordinates: Map<String, Double>,
    ): GMResult<Double, String> {
        var total = 0.0
        ids.forEach { id ->
            total += coordinates[id] ?: return missingNode(id)
        }
        return GMResult.Ok(total / ids.size)
    }

    private fun missingNode(id: String): GMResult.Err<String> =
        GMResult.Err("Constraint references unknown node $id")

    private typealias Dag = LinkedHashMap<String, MutableList<Adjacent>>

    private data class Adjacent(
        val id: String,
        val gap: Double,
        val direction: ConstraintDirection,
    )

    private enum class ConstraintDirection {
        Horizontal,
        Vertical,
    }

    private data class DummyMaps(
        val nodeToDummy: Map<String, String>,
        val dummyToNodes: Map<String, List<String>>,
        val fixedDummies: Set<String>,
    )

}

internal data class CoseConstraintState(
    val idToNodeMap: Map<String, CoseNode>,
    val fixedNodeSet: Set<String>,
    val dummyToNodeForVerticalAlignment: Map<String, List<String>> = emptyMap(),
    val dummyToNodeForHorizontalAlignment: Map<String, List<String>> = emptyMap(),
    val fixedNodesOnHorizontal: Set<String> = emptySet(),
    val fixedNodesOnVertical: Set<String> = emptySet(),
    val nodesInRelativeHorizontal: MutableList<String> = mutableListOf(),
    val nodesInRelativeVertical: MutableList<String> = mutableListOf(),
    val horizontalRelations: Map<String, List<CoseRelaxedRelation>> = emptyMap(),
    val verticalRelations: Map<String, List<CoseRelaxedRelation>> = emptyMap(),
    val horizontalTempPositions: MutableMap<String, Double> = linkedMapOf(),
    val verticalTempPositions: MutableMap<String, Double> = linkedMapOf(),
)

internal data class CoseRelaxedRelation(
    val before: String? = null,
    val after: String? = null,
    val gap: Double,
)
