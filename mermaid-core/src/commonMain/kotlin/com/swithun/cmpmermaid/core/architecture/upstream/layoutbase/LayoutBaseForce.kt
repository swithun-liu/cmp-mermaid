package com.swithun.cmpmermaid.core.architecture.upstream.layoutbase

import com.swithun.cmpmermaid.core.GMResult
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

/**
 * layout-base 2.0.1:
 * src/fd/FDLayoutConstants.js and src/LayoutConstants.js.
 */
internal data class LFDLayoutOptions(
    val layoutQuality: Int = LLayoutConstants.QUALITY_PROOF,
    val incremental: Boolean = true,
    val uniformLeafNodeSizes: Boolean = false,
    val defaultEdgeLength: Double = 50.0,
    val perLevelIdealEdgeLengthFactor: Double = 0.1,
    val maxIterations: Int = 2500,
    val gravityStrength: Double = 0.25,
    val compoundGravityStrength: Double = 1.0,
    val gravityRangeFactor: Double = 3.8,
    val compoundGravityRangeFactor: Double = 1.5,
    val coolingFactorIncremental: Double = 0.3,
    val minRepulsionDistance: Double = defaultEdgeLength / 10.0,
)

/**
 * layout-base 2.0.1: src/fd/FDLayoutNode.js -> FDLayoutNode.
 */
internal open class LFDNode(
    graphManager: LGraphManager,
    location: LPointD = LPointD(),
    size: LDimensionD = LDimensionD(),
) : LNode(graphManager, location, size) {
    var nodeRepulsion: Double = DEFAULT_REPULSION_STRENGTH
    var springForceX: Double = 0.0
    var springForceY: Double = 0.0
    var repulsionForceX: Double = 0.0
    var repulsionForceY: Double = 0.0
    var gravitationForceX: Double = 0.0
    var gravitationForceY: Double = 0.0
    var displacementX: Double = 0.0
    var displacementY: Double = 0.0
    var startX: Int = 0
    var finishX: Int = 0
    var startY: Int = 0
    var finishY: Int = 0
    var surrounding: List<LFDNode> = emptyList()
    var noOfChildren: Int = 1

    fun setGridCoordinates(
        startX: Int,
        finishX: Int,
        startY: Int,
        finishY: Int,
    ) {
        this.startX = startX
        this.finishX = finishX
        this.startY = startY
        this.finishY = finishY
    }

    companion object {
        const val DEFAULT_REPULSION_STRENGTH = 4500.0
    }
}

/**
 * layout-base 2.0.1: src/fd/FDLayoutEdge.js -> FDLayoutEdge.
 */
internal open class LFDEdge : LEdge() {
    var idealLength: Double = 50.0
    var edgeElasticity: Double = 0.45
}

/**
 * layout-base 2.0.1: src/Layout.js reached lifecycle and
 * src/fd/FDLayout.js.
 */
internal abstract class LFDLayout(
    protected val options: LFDLayoutOptions,
) : LLayoutFactory {
    val graphManager = LGraphManager(this)
    var useSmartIdealEdgeLengthCalculation: Boolean = true
    var gravityConstant: Double = options.gravityStrength
    var compoundGravityConstant: Double = options.compoundGravityStrength
    var gravityRangeFactor: Double = options.gravityRangeFactor
    var compoundGravityRangeFactor: Double = options.compoundGravityRangeFactor
    var displacementThresholdPerNode: Double = 3.0 * options.defaultEdgeLength / 100.0
    var coolingFactor: Double = options.coolingFactorIncremental
    var initialCoolingFactor: Double = options.coolingFactorIncremental
    var totalDisplacement: Double = 0.0
    var oldTotalDisplacement: Double = 0.0
    var maxIterations: Int = options.maxIterations
    var maxNodeDisplacement: Double = MAX_NODE_DISPLACEMENT_INCREMENTAL
    var totalDisplacementThreshold: Double = 0.0
    var repulsionRange: Double = 0.0
    var totalIterations: Int = 0
    var notAnimatedIterations: Int = 0
    var level: Int = 0
    protected var grid: List<List<MutableList<LFDNode>>> = emptyList()

    fun getAllNodes(): List<LFDNode> =
        graphManager.getAllNodes().mapNotNull { node -> node as? LFDNode }

    fun getAllEdges(): List<LFDEdge> =
        graphManager.getAllEdges().mapNotNull { edge -> edge as? LFDEdge }

    open fun initParameters() {
        totalIterations = 0
        notAnimatedIterations = 0
        grid = emptyList()
    }

    open fun initSpringEmbedder() {
        val nodeCount = getAllNodes().size
        if (options.incremental) {
            if (nodeCount > ADAPTATION_LOWER_NODE_LIMIT) {
                coolingFactor = max(
                    coolingFactor * COOLING_ADAPTATION_FACTOR,
                    coolingFactor -
                        (nodeCount - ADAPTATION_LOWER_NODE_LIMIT).toDouble() /
                        (ADAPTATION_UPPER_NODE_LIMIT - ADAPTATION_LOWER_NODE_LIMIT) *
                        coolingFactor *
                        (1.0 - COOLING_ADAPTATION_FACTOR),
                )
            }
            maxNodeDisplacement = MAX_NODE_DISPLACEMENT_INCREMENTAL
        } else {
            coolingFactor = if (nodeCount > ADAPTATION_LOWER_NODE_LIMIT) {
                max(
                    COOLING_ADAPTATION_FACTOR,
                    1.0 -
                        (nodeCount - ADAPTATION_LOWER_NODE_LIMIT).toDouble() /
                        (ADAPTATION_UPPER_NODE_LIMIT - ADAPTATION_LOWER_NODE_LIMIT) *
                        (1.0 - COOLING_ADAPTATION_FACTOR),
                )
            } else {
                1.0
            }
            initialCoolingFactor = coolingFactor
            maxNodeDisplacement = MAX_NODE_DISPLACEMENT
        }
        maxIterations = max(nodeCount * 5, maxIterations)
        displacementThresholdPerNode = 3.0 * options.defaultEdgeLength / 100.0
        totalDisplacementThreshold = displacementThresholdPerNode * nodeCount
        repulsionRange = calcRepulsionRange()
    }

    fun calcIdealEdgeLengths() {
        getAllEdges().forEach { edge ->
            if (!edge.isInterGraph) {
                return@forEach
            }
            val originalIdealLength = edge.idealLength
            val source = edge.source
            val target = edge.target
            val sourceInLca = edge.sourceInLca ?: return@forEach
            val targetInLca = edge.targetInLca ?: return@forEach
            if (useSmartIdealEdgeLengthCalculation) {
                edge.idealLength += sourceInLca.estimatedSize +
                    targetInLca.estimatedSize -
                    2.0 * LLayoutConstants.SIMPLE_NODE_SIZE
            }
            val lcaDepth = edge.lca?.getInclusionTreeDepth() ?: return@forEach
            edge.idealLength += originalIdealLength *
                options.perLevelIdealEdgeLengthFactor *
                (source.inclusionTreeDepth + target.inclusionTreeDepth - 2 * lcaDepth)
        }
    }

    fun calcSpringForces() {
        getAllEdges().forEach { edge ->
            calcSpringForce(edge, edge.idealLength)
        }
    }

    fun calcRepulsionForces(
        gridUpdateAllowed: Boolean = true,
        forceToNodeSurroundingUpdate: Boolean = false,
    ): GMResult<Unit, String> {
        if (totalIterations % GRID_CALCULATION_CHECK_PERIOD == 1 && gridUpdateAllowed) {
            when (val result = updateGrid()) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }
        val processed = linkedSetOf<LFDNode>()
        getAllNodes().forEach { node ->
            calculateRepulsionForceOfANode(
                nodeA = node,
                processedNodeSet = processed,
                gridUpdateAllowed = gridUpdateAllowed,
                forceToNodeSurroundingUpdate = forceToNodeSurroundingUpdate,
            )
            processed += node
        }
        return GMResult.Ok(Unit)
    }

    fun calcGravitationalForces() {
        graphManager.getAllNodesToApplyGravitation().forEach { node ->
            (node as? LFDNode)?.let(::calcGravitationalForce)
        }
    }

    private fun calcSpringForce(
        edge: LFDEdge,
        idealLength: Double,
    ) {
        val sourceNode = edge.source as? LFDNode ?: return
        val targetNode = edge.target as? LFDNode ?: return
        if (
            options.uniformLeafNodeSizes &&
            sourceNode.child == null &&
            targetNode.child == null
        ) {
            edge.updateLengthSimple()
        } else {
            edge.updateLength()
            if (edge.isOverlappingSourceAndTarget) {
                return
            }
        }
        val length = edge.length
        if (length == 0.0) {
            return
        }
        val springForce = edge.edgeElasticity * (length - idealLength)
        val springForceX = springForce * edge.lengthX / length
        val springForceY = springForce * edge.lengthY / length
        sourceNode.springForceX += springForceX
        sourceNode.springForceY += springForceY
        targetNode.springForceX -= springForceX
        targetNode.springForceY -= springForceY
    }

    private fun calcRepulsionForce(
        nodeA: LFDNode,
        nodeB: LFDNode,
    ) {
        val rectA = nodeA.rect
        val rectB = nodeB.rect
        if (rectA.intersects(rectB)) {
            val overlap = LGeometry.calcSeparationAmount(
                rectA = rectA,
                rectB = rectB,
                separationBuffer = options.defaultEdgeLength / 2.0,
            )
            val childrenConstant = nodeA.noOfChildren.toDouble() *
                nodeB.noOfChildren /
                (nodeA.noOfChildren + nodeB.noOfChildren)
            nodeA.repulsionForceX -= childrenConstant * 2.0 * overlap.x
            nodeA.repulsionForceY -= childrenConstant * 2.0 * overlap.y
            nodeB.repulsionForceX += childrenConstant * 2.0 * overlap.x
            nodeB.repulsionForceY += childrenConstant * 2.0 * overlap.y
            return
        }

        val intersection = LGeometry.getIntersection(rectA, rectB)
        var distanceX = intersection.secondX - intersection.firstX
        var distanceY = intersection.secondY - intersection.firstY
        if (abs(distanceX) < options.minRepulsionDistance) {
            distanceX = layoutBaseSign(distanceX) * options.minRepulsionDistance
        }
        if (abs(distanceY) < options.minRepulsionDistance) {
            distanceY = layoutBaseSign(distanceY) * options.minRepulsionDistance
        }
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        val distance = sqrt(distanceSquared)
        if (distanceSquared == 0.0 || distance == 0.0) {
            return
        }
        val repulsionForce =
            (nodeA.nodeRepulsion / 2.0 + nodeB.nodeRepulsion / 2.0) *
                nodeA.noOfChildren *
                nodeB.noOfChildren /
                distanceSquared
        val repulsionForceX = repulsionForce * distanceX / distance
        val repulsionForceY = repulsionForce * distanceY / distance
        nodeA.repulsionForceX -= repulsionForceX
        nodeA.repulsionForceY -= repulsionForceY
        nodeB.repulsionForceX += repulsionForceX
        nodeB.repulsionForceY += repulsionForceY
    }

    private fun calcGravitationalForce(node: LFDNode) {
        val ownerGraph = node.owner ?: return
        val ownerCenterX = (ownerGraph.right + ownerGraph.left) / 2.0
        val ownerCenterY = (ownerGraph.top + ownerGraph.bottom) / 2.0
        val distanceX = node.centerX - ownerCenterX
        val distanceY = node.centerY - ownerCenterY
        val absDistanceX = abs(distanceX) + node.width / 2.0
        val absDistanceY = abs(distanceY) + node.height / 2.0
        if (ownerGraph === graphManager.rootGraph) {
            val estimatedSize = ownerGraph.estimatedSize * gravityRangeFactor
            if (absDistanceX > estimatedSize || absDistanceY > estimatedSize) {
                node.gravitationForceX = -gravityConstant * distanceX
                node.gravitationForceY = -gravityConstant * distanceY
            }
        } else {
            val estimatedSize = ownerGraph.estimatedSize * compoundGravityRangeFactor
            if (absDistanceX > estimatedSize || absDistanceY > estimatedSize) {
                node.gravitationForceX =
                    -gravityConstant * distanceX * compoundGravityConstant
                node.gravitationForceY =
                    -gravityConstant * distanceY * compoundGravityConstant
            }
        }
    }

    fun isConverged(): Boolean {
        val oscillating =
            totalIterations > maxIterations / 3 &&
                abs(totalDisplacement - oldTotalDisplacement) < 2.0
        val converged = totalDisplacement < totalDisplacementThreshold
        oldTotalDisplacement = totalDisplacement
        return converged || oscillating
    }

    fun calcNoOfChildrenForAllNodes() {
        getAllNodes().forEach { node ->
            node.noOfChildren = node.calculateNoOfChildren()
        }
    }

    private fun calcGrid(graph: LGraph): GMResult<List<List<MutableList<LFDNode>>>, String> {
        val rawSizeX = ceil((graph.right - graph.left) / repulsionRange)
        val rawSizeY = ceil((graph.bottom - graph.top) / repulsionRange)
        if (
            !rawSizeX.isFinite() ||
            !rawSizeY.isFinite() ||
            rawSizeX <= 0.0 ||
            rawSizeY <= 0.0 ||
            rawSizeX > MAX_GRID_DIMENSION ||
            rawSizeY > MAX_GRID_DIMENSION
        ) {
            return GMResult.Err(
                "FDLayout.calcGrid produced invalid dimensions " +
                    "($rawSizeX, $rawSizeY)",
            )
        }
        val sizeX = rawSizeX.toInt()
        val sizeY = rawSizeY.toInt()
        return GMResult.Ok(
            List(sizeX) {
                List(sizeY) { mutableListOf() }
            },
        )
    }

    private fun addNodeToGrid(
        node: LFDNode,
        left: Double,
        top: Double,
    ): GMResult<Unit, String> {
        val startX = floor((node.rect.x - left) / repulsionRange).toInt()
        val finishX = floor((node.rect.width + node.rect.x - left) / repulsionRange).toInt()
        val startY = floor((node.rect.y - top) / repulsionRange).toInt()
        val finishY = floor((node.rect.height + node.rect.y - top) / repulsionRange).toInt()
        if (
            startX !in grid.indices ||
            finishX !in grid.indices ||
            grid.isEmpty() ||
            startY !in grid.first().indices ||
            finishY !in grid.first().indices
        ) {
            return GMResult.Err(
                "FDLayout.addNodeToGrid produced out-of-range coordinates " +
                    "[$startX..$finishX, $startY..$finishY]",
            )
        }
        for (x in startX..finishX) {
            for (y in startY..finishY) {
                grid[x][y] += node
                node.setGridCoordinates(startX, finishX, startY, finishY)
            }
        }
        return GMResult.Ok(Unit)
    }

    private fun updateGrid(): GMResult<Unit, String> {
        val root = graphManager.rootGraph
        grid = when (val result = calcGrid(root)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        getAllNodes().forEach { node ->
            when (val result = addNodeToGrid(node, root.left, root.top)) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }
        return GMResult.Ok(Unit)
    }

    private fun calculateRepulsionForceOfANode(
        nodeA: LFDNode,
        processedNodeSet: Set<LFDNode>,
        gridUpdateAllowed: Boolean,
        forceToNodeSurroundingUpdate: Boolean,
    ) {
        if (
            totalIterations % GRID_CALCULATION_CHECK_PERIOD == 1 &&
            gridUpdateAllowed ||
            forceToNodeSurroundingUpdate
        ) {
            val surrounding = linkedSetOf<LFDNode>()
            for (x in nodeA.startX - 1 until nodeA.finishX + 2) {
                for (y in nodeA.startY - 1 until nodeA.finishY + 2) {
                    if (
                        x !in grid.indices ||
                        grid.isEmpty() ||
                        y !in grid.first().indices
                    ) {
                        continue
                    }
                    grid[x][y].forEach { nodeB ->
                        if (
                            nodeA.owner !== nodeB.owner ||
                            nodeA === nodeB ||
                            nodeB in processedNodeSet ||
                            nodeB in surrounding
                        ) {
                            return@forEach
                        }
                        val distanceX = abs(nodeA.centerX - nodeB.centerX) -
                            (nodeA.width / 2.0 + nodeB.width / 2.0)
                        val distanceY = abs(nodeA.centerY - nodeB.centerY) -
                            (nodeA.height / 2.0 + nodeB.height / 2.0)
                        if (distanceX <= repulsionRange && distanceY <= repulsionRange) {
                            surrounding += nodeB
                        }
                    }
                }
            }
            nodeA.surrounding = surrounding.toList()
        }
        nodeA.surrounding.forEach { nodeB ->
            calcRepulsionForce(nodeA, nodeB)
        }
    }

    abstract fun calcRepulsionRange(): Double

    companion object {
        private const val COOLING_ADAPTATION_FACTOR = 0.33
        private const val ADAPTATION_LOWER_NODE_LIMIT = 1000
        private const val ADAPTATION_UPPER_NODE_LIMIT = 5000
        private const val MAX_NODE_DISPLACEMENT_INCREMENTAL = 100.0
        private const val MAX_NODE_DISPLACEMENT = 300.0
        private const val GRID_CALCULATION_CHECK_PERIOD = 10
        private const val MAX_GRID_DIMENSION = 100_000.0
    }
}
