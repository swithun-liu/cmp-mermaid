package com.swithun.cmpmermaid.core.architecture.upstream.layoutbase

import kotlin.math.sqrt

internal object LLayoutConstants {
    const val QUALITY_PROOF = 2
    const val DEFAULT_GRAPH_MARGIN = 15.0
    const val SIMPLE_NODE_SIZE = 40.0
    const val EMPTY_COMPOUND_NODE_SIZE = 40.0
}

/**
 * layout-base 2.0.1: src/Layout.js factories used by LGraphManager.
 */
internal interface LLayoutFactory {
    fun newGraph(): LGraph
    fun newNode(): LNode
    fun newEdge(): LEdge
}

/**
 * layout-base 2.0.1: src/LNode.js -> LNode.
 */
internal open class LNode(
    val graphManager: LGraphManager,
    location: LPointD = LPointD(),
    size: LDimensionD = LDimensionD(),
) {
    var id: String = ""
    val rect = LRectangleD(location.x, location.y, size.width, size.height)
    val edges = mutableListOf<LEdge>()
    var child: LGraph? = null
    var owner: LGraph? = null
    var estimatedSize: Double = Int.MIN_VALUE.toDouble()
    var inclusionTreeDepth: Int = Int.MAX_VALUE
    var paddingLeft: Double? = null
    var paddingTop: Double? = null
    var paddingRight: Double? = null
    var paddingBottom: Double? = null

    val width: Double get() = rect.width
    val height: Double get() = rect.height
    val centerX: Double get() = rect.centerX
    val centerY: Double get() = rect.centerY
    val left: Double get() = rect.x
    val right: Double get() = rect.right
    val top: Double get() = rect.y
    val bottom: Double get() = rect.bottom
    val diagonal: Double get() = sqrt(rect.width * rect.width + rect.height * rect.height)
    val halfTheDiagonal: Double get() = diagonal / 2.0
    val parent: LNode? get() = owner?.parent

    fun setRect(
        upperLeft: LPointD,
        dimension: LDimensionD,
    ) {
        rect.x = upperLeft.x
        rect.y = upperLeft.y
        rect.width = dimension.width
        rect.height = dimension.height
    }

    fun setCenter(
        x: Double,
        y: Double,
    ) {
        rect.setCenter(x, y)
    }

    fun setLocation(
        x: Double,
        y: Double,
    ) {
        rect.x = x
        rect.y = y
    }

    fun moveBy(
        dx: Double,
        dy: Double,
    ) {
        rect.x += dx
        rect.y += dy
    }

    fun getEdgeListToNode(node: LNode): List<LEdge> =
        edges.filter { edge -> edge.source === this && edge.target === node }

    fun getEdgesBetween(node: LNode): List<LEdge> =
        edges.filter { edge ->
            (edge.source === this || edge.target === this) &&
                (edge.target === node || edge.source === node)
        }

    fun getNeighborsList(): Set<LNode> =
        edges.mapTo(linkedSetOf()) { edge ->
            if (edge.source === this) edge.target else edge.source
        }

    fun withChildren(): Set<LNode> = linkedSetOf<LNode>().also { result ->
        result += this
        child?.nodes?.forEach { childNode ->
            result += childNode.withChildren()
        }
    }

    /**
     * LNode.getNoOfChildren. The Kotlin name avoids colliding with
     * FDLayoutNode.noOfChildren's generated JVM getter.
     */
    fun calculateNoOfChildren(): Int {
        val childGraph = child ?: return 1
        val count = childGraph.nodes.sumOf(LNode::calculateNoOfChildren)
        return count.coerceAtLeast(1)
    }

    fun calcEstimatedSize(): Double {
        val childGraph = child
        estimatedSize = if (childGraph == null) {
            (rect.width + rect.height) / 2.0
        } else {
            childGraph.calcEstimatedSize().also { size ->
                rect.width = size
                rect.height = size
            }
        }
        return estimatedSize
    }

    fun updateBounds() {
        val childGraph = child ?: return
        if (childGraph.nodes.isEmpty()) {
            return
        }
        childGraph.updateBounds(recursive = true)
        rect.x = childGraph.left
        rect.y = childGraph.top
        rect.width = childGraph.right - childGraph.left
        rect.height = childGraph.bottom - childGraph.top
    }
}

/**
 * layout-base 2.0.1: src/LEdge.js -> LEdge.
 */
internal open class LEdge {
    var id: String = ""
    lateinit var source: LNode
    lateinit var target: LNode
    var isInterGraph: Boolean = false
    var isOverlappingSourceAndTarget: Boolean = false
    var length: Double = 0.0
    var lengthX: Double = 0.0
    var lengthY: Double = 0.0
    var lca: LGraph? = null
    var sourceInLca: LNode? = null
    var targetInLca: LNode? = null

    fun getOtherEnd(node: LNode): LNode =
        if (source === node) target else source

    fun getOtherEndInGraph(
        node: LNode,
        graph: LGraph,
    ): LNode? {
        var otherEnd = getOtherEnd(node)
        val root = graph.graphManager.rootGraph
        while (true) {
            if (otherEnd.owner === graph) {
                return otherEnd
            }
            if (otherEnd.owner === root) {
                break
            }
            otherEnd = otherEnd.owner?.parent ?: break
        }
        return null
    }

    fun updateLength() {
        val intersection = LGeometry.getIntersection(target.rect, source.rect)
        isOverlappingSourceAndTarget = intersection.overlaps
        if (intersection.overlaps) {
            return
        }
        lengthX = intersection.firstX - intersection.secondX
        lengthY = intersection.firstY - intersection.secondY
        if (kotlin.math.abs(lengthX) < 1.0) {
            lengthX = layoutBaseSign(lengthX)
        }
        if (kotlin.math.abs(lengthY) < 1.0) {
            lengthY = layoutBaseSign(lengthY)
        }
        length = sqrt(lengthX * lengthX + lengthY * lengthY)
    }

    fun updateLengthSimple() {
        lengthX = target.centerX - source.centerX
        lengthY = target.centerY - source.centerY
        if (kotlin.math.abs(lengthX) < 1.0) {
            lengthX = layoutBaseSign(lengthX)
        }
        if (kotlin.math.abs(lengthY) < 1.0) {
            lengthY = layoutBaseSign(lengthY)
        }
        length = sqrt(lengthX * lengthX + lengthY * lengthY)
    }
}

/**
 * layout-base 2.0.1: src/LGraph.js -> LGraph.
 */
internal open class LGraph(
    val graphManager: LGraphManager,
) {
    var estimatedSize: Double = Int.MIN_VALUE.toDouble()
    var margin: Double = LLayoutConstants.DEFAULT_GRAPH_MARGIN
    val edges = mutableListOf<LEdge>()
    val nodes = mutableListOf<LNode>()
    var isConnected: Boolean = false
    lateinit var parent: LNode
    var left: Double = 0.0
    var right: Double = 0.0
    var top: Double = 0.0
    var bottom: Double = 0.0

    fun add(node: LNode): LNode {
        node.owner = this
        nodes += node
        return node
    }

    fun add(
        edge: LEdge,
        sourceNode: LNode,
        targetNode: LNode,
    ): LEdge {
        edge.source = sourceNode
        edge.target = targetNode
        edge.isInterGraph = false
        edges += edge
        sourceNode.edges += edge
        if (targetNode !== sourceNode) {
            targetNode.edges += edge
        }
        return edge
    }

    fun updateLeftTop(): LPointD? {
        if (nodes.isEmpty()) {
            return null
        }
        val margin = nodes.first().parent?.paddingLeft ?: this.margin
        top = nodes.minOf(LNode::top) - margin
        left = nodes.minOf(LNode::left) - margin
        return LPointD(left, top)
    }

    fun updateBounds(recursive: Boolean) {
        if (nodes.isEmpty()) {
            left = parent.left
            right = parent.right
            top = parent.top
            bottom = parent.bottom
            return
        }

        var nodeLeft = Double.MAX_VALUE
        var nodeRight = -Double.MAX_VALUE
        var nodeTop = Double.MAX_VALUE
        var nodeBottom = -Double.MAX_VALUE
        nodes.forEach { node ->
            if (recursive && node.child != null) {
                node.updateBounds()
            }
            if (nodeLeft > node.left) {
                nodeLeft = node.left
            }
            if (nodeRight < node.right) {
                nodeRight = node.right
            }
            if (nodeTop > node.top) {
                nodeTop = node.top
            }
            if (nodeBottom < node.bottom) {
                nodeBottom = node.bottom
            }
        }
        val boundingRect = LRectangleD(
            x = nodeLeft,
            y = nodeTop,
            width = nodeRight - nodeLeft,
            height = nodeBottom - nodeTop,
        )
        val margin = nodes.first().parent?.paddingLeft ?: this.margin
        left = boundingRect.x - margin
        right = boundingRect.x + boundingRect.width + margin
        top = boundingRect.y - margin
        bottom = boundingRect.y + boundingRect.height + margin
    }

    fun getInclusionTreeDepth(): Int =
        if (this === graphManager.rootGraph) 1 else parent.inclusionTreeDepth

    fun calcEstimatedSize(): Double {
        val size = nodes.sumOf(LNode::calcEstimatedSize)
        estimatedSize = if (size == 0.0) {
            LLayoutConstants.EMPTY_COMPOUND_NODE_SIZE
        } else {
            size / sqrt(nodes.size.toDouble())
        }
        return estimatedSize
    }

    fun updateConnected() {
        if (nodes.isEmpty()) {
            isConnected = true
            return
        }
        val queue = ArrayDeque<LNode>()
        val visited = linkedSetOf<LNode>()
        nodes.first().withChildren().forEach { node ->
            queue.addLast(node)
            visited += node
        }
        while (queue.isNotEmpty()) {
            val currentNode = queue.removeFirst()
            currentNode.edges.forEach { edge ->
                val neighbor = edge.getOtherEndInGraph(currentNode, this)
                if (neighbor != null && neighbor !in visited) {
                    neighbor.withChildren().forEach { child ->
                        queue.addLast(child)
                        visited += child
                    }
                }
            }
        }
        isConnected = visited.count { node -> node.owner === this } == nodes.size
    }
}

/**
 * layout-base 2.0.1: src/LGraphManager.js -> LGraphManager.
 */
internal open class LGraphManager(
    private val layout: LLayoutFactory,
) {
    val graphs = mutableListOf<LGraph>()
    val interGraphEdges = mutableListOf<LEdge>()
    lateinit var rootGraph: LGraph
    private var allNodesCache: List<LNode>? = null
    private var allEdgesCache: List<LEdge>? = null
    private var allNodesToApplyGravitation: List<LNode>? = null

    fun addRoot(): LGraph {
        val graph = layout.newGraph()
        val node = layout.newNode()
        add(graph, node)
        rootGraph = graph
        return graph
    }

    fun add(
        graph: LGraph,
        parentNode: LNode,
    ): LGraph {
        graphs += graph
        graph.parent = parentNode
        parentNode.child = graph
        return graph
    }

    fun add(
        edge: LEdge,
        sourceNode: LNode,
        targetNode: LNode,
    ): LEdge {
        val sourceGraph = sourceNode.owner
        val targetGraph = targetNode.owner
        if (sourceGraph === targetGraph && sourceGraph != null) {
            edge.isInterGraph = false
            return sourceGraph.add(edge, sourceNode, targetNode)
        }
        edge.isInterGraph = true
        edge.source = sourceNode
        edge.target = targetNode
        interGraphEdges += edge
        sourceNode.edges += edge
        targetNode.edges += edge
        return edge
    }

    fun getAllNodes(): List<LNode> =
        allNodesCache ?: graphs.flatMap(LGraph::nodes).also { allNodesCache = it }

    fun getAllEdges(): List<LEdge> =
        allEdgesCache
            ?: (graphs.flatMap(LGraph::edges) + interGraphEdges).also { allEdgesCache = it }

    fun resetAllNodes() {
        allNodesCache = null
    }

    fun resetAllEdges() {
        allEdgesCache = null
    }

    fun resetAllNodesToApplyGravitation() {
        allNodesToApplyGravitation = null
    }

    fun getAllNodesToApplyGravitation(): List<LNode> =
        allNodesToApplyGravitation.orEmpty()

    fun setAllNodesToApplyGravitation(nodes: List<LNode>) {
        allNodesToApplyGravitation = nodes
    }

    fun updateBounds() {
        rootGraph.updateBounds(recursive = true)
    }

    fun isOneAncestorOfOther(
        firstNode: LNode,
        secondNode: LNode,
    ): Boolean {
        if (firstNode === secondNode) {
            return true
        }
        if (isAncestor(ancestor = secondNode, descendant = firstNode)) {
            return true
        }
        return isAncestor(ancestor = firstNode, descendant = secondNode)
    }

    private fun isAncestor(
        ancestor: LNode,
        descendant: LNode,
    ): Boolean {
        var ownerGraph = descendant.owner
        while (ownerGraph != null) {
            val parentNode = ownerGraph.parent
            if (parentNode === ancestor) {
                return true
            }
            ownerGraph = parentNode.owner
        }
        return false
    }

    fun calcLowestCommonAncestors() {
        getAllEdges().forEach { edge ->
            val sourceNode = edge.source
            val targetNode = edge.target
            edge.lca = null
            edge.sourceInLca = sourceNode
            edge.targetInLca = targetNode
            if (sourceNode === targetNode) {
                edge.lca = sourceNode.owner
                return@forEach
            }

            var sourceAncestorGraph = sourceNode.owner
            while (edge.lca == null && sourceAncestorGraph != null) {
                edge.targetInLca = targetNode
                var targetAncestorGraph = targetNode.owner
                while (edge.lca == null && targetAncestorGraph != null) {
                    if (targetAncestorGraph === sourceAncestorGraph) {
                        edge.lca = targetAncestorGraph
                        break
                    }
                    if (targetAncestorGraph === rootGraph) {
                        break
                    }
                    val targetInLca = targetAncestorGraph.parent
                    edge.targetInLca = targetInLca
                    targetAncestorGraph = targetInLca.owner
                }
                if (sourceAncestorGraph === rootGraph) {
                    break
                }
                if (edge.lca == null) {
                    val sourceInLca = sourceAncestorGraph.parent
                    edge.sourceInLca = sourceInLca
                    sourceAncestorGraph = sourceInLca.owner
                }
            }
        }
    }

    fun calcInclusionTreeDepths(
        graph: LGraph = rootGraph,
        depth: Int = 1,
    ) {
        graph.nodes.forEach { node ->
            node.inclusionTreeDepth = depth
            node.child?.let { child ->
                calcInclusionTreeDepths(child, depth + 1)
            }
        }
    }

    fun removeInvalidEdges() {
        val invalidEdges = interGraphEdges.filter { edge ->
            isOneAncestorOfOther(edge.source, edge.target)
        }
        invalidEdges.forEach { edge ->
            edge.source.edges.remove(edge)
            edge.target.edges.remove(edge)
            interGraphEdges.remove(edge)
        }
        if (invalidEdges.isNotEmpty()) {
            resetAllEdges()
        }
    }
}
