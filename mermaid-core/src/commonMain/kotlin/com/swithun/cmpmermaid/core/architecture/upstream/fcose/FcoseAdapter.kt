package com.swithun.cmpmermaid.core.architecture.upstream.fcose

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseAlignmentConstraint
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseConstraints
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseEdge
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseLayout
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseNode
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseRandom
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseRelativePlacementConstraint
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LDimensionD
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LFDLayoutOptions
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LGraph
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LPointD
import kotlin.random.Random

internal data class FcoseNodeInput(
    val id: String,
    val parentId: String?,
    val centerX: Double,
    val centerY: Double,
    val width: Double,
    val height: Double,
    val padding: Double,
    val labelWidth: Double? = null,
    val labelHeight: Double? = null,
)

internal data class FcoseEdgeInput(
    val id: String,
    val sourceId: String,
    val targetId: String,
    val idealLength: Double,
    val elasticity: Double,
)

internal data class FcoseOptions(
    val nodes: List<FcoseNodeInput>,
    val edges: List<FcoseEdgeInput>,
    val alignmentConstraint: CoseAlignmentConstraint,
    val relativePlacementConstraint: List<CoseRelativePlacementConstraint>,
    val numIterations: Int,
    val seed: Float,
)

internal data class FcoseNodeResult(
    val id: String,
    val centerX: Double,
    val centerY: Double,
    val left: Double,
    val top: Double,
    val width: Double,
    val height: Double,
)

/**
 * cytoscape-fcose 2.2.0:
 * src/fcose/index.js whole-graph constrained branch,
 * src/fcose/cose.js -> coseLayout/processChildrenList/processEdges,
 * src/fcose/auxiliary.js -> getTopMostNodes/relocateComponent.
 */
internal object FcoseAdapter {
    fun runTwice(
        options: FcoseOptions,
    ): GMResult<Map<String, FcoseNodeResult>, String> {
        if (options.nodes.isEmpty()) {
            return GMResult.Ok(emptyMap())
        }
        if (options.seed == 0f) {
            return runTwiceWithRandom(options) { CoseRandom { Random.Default.nextDouble() } }
        }
        return runTwiceWithRandom(options) {
            val random = Mulberry32(options.seed.toLong().toUInt())
            CoseRandom(random::nextDouble)
        }
    }

    private fun runTwiceWithRandom(
        options: FcoseOptions,
        randomFactory: () -> CoseRandom,
    ): GMResult<Map<String, FcoseNodeResult>, String> {
        val first = when (val result = runOnce(options, randomFactory())) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val secondInputs = options.nodes.map { node ->
            val result = first.getValue(node.id)
            node.copy(
                centerX = result.centerX,
                centerY = result.centerY,
            )
        }
        return runOnce(
            options = options.copy(nodes = secondInputs),
            random = randomFactory(),
        )
    }

    private fun runOnce(
        options: FcoseOptions,
        random: CoseRandom,
    ): GMResult<Map<String, FcoseNodeResult>, String> {
        val cytoscapeGeometries = when (val result = resolveCytoscapeNodeGeometries(options.nodes)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val originalBounds = unionBounds(
            cytoscapeGeometries.values.map(ResolvedCytoscapeNodeGeometry::outerVisualBounds),
        )
            ?: return GMResult.Err("fCoSE received no node geometry")
        val acceptedEdges = filterEdges(options.edges, options.nodes.mapTo(linkedSetOf()) { it.id })
        val defaultEdgeLength = if (acceptedEdges.isEmpty()) {
            options.edges.firstOrNull()?.idealLength ?: 50.0
        } else {
            acceptedEdges.sumOf(FcoseEdgeInput::idealLength) / acceptedEdges.size
        }
        val layout = CoseLayout(
            fdOptions = LFDLayoutOptions(
                incremental = true,
                uniformLeafNodeSizes = false,
                defaultEdgeLength = defaultEdgeLength,
                perLevelIdealEdgeLengthFactor = 0.1,
                maxIterations = options.numIterations,
                gravityStrength = 0.25,
                compoundGravityStrength = 1.0,
                gravityRangeFactor = 3.8,
                compoundGravityRangeFactor = 1.5,
                coolingFactorIncremental = 0.3,
                minRepulsionDistance = defaultEdgeLength / 10.0,
            ),
            random = random,
            defaultEdgeLength = defaultEdgeLength,
        )
        val graphManager = layout.graphManager
        val root = graphManager.addRoot()
        val childrenByParent = options.nodes.groupByTo(linkedMapOf(), FcoseNodeInput::parentId)
        val nodeIds = options.nodes.mapTo(linkedSetOf(), FcoseNodeInput::id)
        val topMost = options.nodes.filter { node ->
            node.parentId == null || node.parentId !in nodeIds
        }
        val idToNode = linkedMapOf<String, CoseNode>()
        processChildrenList(
            parent = root,
            children = topMost,
            childrenByParent = childrenByParent,
            geometries = cytoscapeGeometries,
            layout = layout,
            idToNode = idToNode,
        )
        acceptedEdges.forEach { edge ->
            val source = idToNode[edge.sourceId] ?: return GMResult.Err(
                "fCoSE edge ${edge.id} references unknown source ${edge.sourceId}",
            )
            val target = idToNode[edge.targetId] ?: return GMResult.Err(
                "fCoSE edge ${edge.id} references unknown target ${edge.targetId}",
            )
            val layoutEdge = graphManager.add(CoseEdge(), source, target) as CoseEdge
            layoutEdge.id = edge.id
            layoutEdge.idealLength = edge.idealLength
            layoutEdge.edgeElasticity = edge.elasticity
        }
        layout.constraints = CoseConstraints(
            alignmentConstraint = options.alignmentConstraint,
            relativePlacementConstraint = options.relativePlacementConstraint
                .takeIf(List<CoseRelativePlacementConstraint>::isNotEmpty),
        )

        when (val result = layout.runLayout()) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return result
        }
        val laidOutBounds = unionBounds(
            idToNode.values.map { node ->
                FcoseBounds(
                    left = node.left,
                    top = node.top,
                    right = node.right,
                    bottom = node.bottom,
                )
            },
        ) ?: return GMResult.Err("fCoSE produced no node geometry")
        val shiftX = originalBounds.centerX - laidOutBounds.centerX
        val shiftY = originalBounds.centerY - laidOutBounds.centerY
        idToNode.values.forEach { node ->
            node.setCenter(node.centerX + shiftX, node.centerY + shiftY)
        }
        val finalInputs = options.nodes.map { input ->
            val node = idToNode.getValue(input.id)
            input.copy(centerX = node.centerX, centerY = node.centerY)
        }
        val finalGeometries = when (val result = resolveCytoscapeNodeGeometries(finalInputs)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val compoundIds = options.nodes
            .mapNotNullTo(linkedSetOf(), FcoseNodeInput::parentId)
        return GMResult.Ok(
            idToNode.mapValues { (id, node) ->
                val geometry = finalGeometries.getValue(id)
                val bounds = geometry.outerVisualBounds
                FcoseNodeResult(
                    id = id,
                    // Cytoscape updateCompoundBounds derives a parent's
                    // position from its final auto body bounds. Its own label
                    // can make boundingBox() asymmetric, so that visual
                    // bounding-box center must not feed the second layout run.
                    centerX = if (id in compoundIds) {
                        geometry.bodyBounds.centerX
                    } else {
                        node.centerX
                    },
                    centerY = if (id in compoundIds) {
                        geometry.bodyBounds.centerY
                    } else {
                        node.centerY
                    },
                    left = bounds.left,
                    top = bounds.top,
                    width = bounds.width,
                    height = bounds.height,
                )
            },
        )
    }

    /**
     * cytoscape-fcose/src/fcose/cose.js -> processChildrenList.
     */
    private fun processChildrenList(
        parent: LGraph,
        children: List<FcoseNodeInput>,
        childrenByParent: Map<String?, List<FcoseNodeInput>>,
        geometries: Map<String, ResolvedCytoscapeNodeGeometry>,
        layout: CoseLayout,
        idToNode: MutableMap<String, CoseNode>,
    ) {
        children.forEach { child ->
            val resolved = geometries.getValue(child.id)
            val geometry = if (childrenByParent[child.id].isNullOrEmpty()) {
                ResolvedGeometry(
                    left = child.centerX - child.width / 2.0,
                    top = child.centerY - child.height / 2.0,
                    width = child.width,
                    height = child.height,
                )
            } else {
                val bounds = resolved.bodyBounds.inset(LAYOUT_BOUNDS_INSET)
                ResolvedGeometry(
                    left = bounds.left,
                    top = bounds.top,
                    width = bounds.width,
                    height = bounds.height,
                )
            }
            val node = CoseNode(
                graphManager = layout.graphManager,
                layout = layout,
                location = LPointD(geometry.left, geometry.top),
                size = LDimensionD(geometry.width, geometry.height),
            )
            node.id = child.id
            node.nodeRepulsion = 4500.0
            node.paddingLeft = child.padding
            node.paddingTop = child.padding
            node.paddingRight = child.padding
            node.paddingBottom = child.padding
            parent.add(node)
            idToNode[child.id] = node

            val nested = childrenByParent[child.id].orEmpty()
            if (nested.isNotEmpty()) {
                val childGraph = layout.graphManager.add(layout.newGraph(), node)
                processChildrenList(
                    parent = childGraph,
                    children = nested,
                    childrenByParent = childrenByParent,
                    geometries = geometries,
                    layout = layout,
                    idToNode = idToNode,
                )
            }
        }
    }

    /**
     * Cytoscape 3.34.x:
     * collection/dimensions/bounds.mjs -> updateCompoundBounds/boundingBoxImpl,
     * collection/layout.mjs -> layoutDimensions, and
     * renderer/base/coord-ele-math/labels.mjs ->
     * recalculateNodeLabelProjection.
     */
    private fun resolveCytoscapeNodeGeometries(
        nodes: List<FcoseNodeInput>,
    ): GMResult<Map<String, ResolvedCytoscapeNodeGeometry>, String> {
        val byId = nodes.associateByTo(linkedMapOf(), FcoseNodeInput::id)
        if (byId.size != nodes.size) {
            return GMResult.Err("fCoSE received duplicate node ids")
        }
        val childrenByParent = nodes.groupByTo(linkedMapOf(), FcoseNodeInput::parentId)
        val resolved = linkedMapOf<String, ResolvedCytoscapeNodeGeometry>()
        val active = linkedSetOf<String>()

        fun resolve(node: FcoseNodeInput): GMResult<ResolvedCytoscapeNodeGeometry, String> {
            resolved[node.id]?.let { return GMResult.Ok(it) }
            if (!active.add(node.id)) {
                return GMResult.Err("Cytoscape compound hierarchy contains a cycle at ${node.id}")
            }
            val children = childrenByParent[node.id].orEmpty()
            val geometry = if (children.isEmpty()) {
                val bodyBounds = resolveNodeBodyBounds(node)
                ResolvedCytoscapeNodeGeometry(
                    bodyBounds = bodyBounds,
                    visualBounds = resolveNodeVisualBounds(
                        node = node,
                        bodyBounds = bodyBounds,
                        labelExpansion = LABEL_BOUNDS_EXPANSION,
                    ),
                    outerVisualBounds = resolveNodeVisualBounds(
                        node = node,
                        bodyBounds = bodyBounds,
                        labelExpansion = OUTER_LABEL_BOUNDS_EXPANSION,
                    ),
                )
            } else {
                val childVisualBounds = mutableListOf<FcoseBounds>()
                children.forEach { child ->
                    when (val childResult = resolve(child)) {
                        is GMResult.Ok -> childVisualBounds += childResult.value.visualBounds
                        is GMResult.Err -> return childResult
                    }
                }
                val union = unionBounds(childVisualBounds)
                    ?: return GMResult.Err("Compound ${node.id} has no visual child bounds")
                val expansion = node.padding + COMPOUND_BOUNDS_EXPANSION
                val bodyBounds = FcoseBounds(
                    left = union.left - expansion,
                    top = union.top - expansion,
                    right = union.right + expansion,
                    bottom = union.bottom + expansion,
                )
                ResolvedCytoscapeNodeGeometry(
                    bodyBounds = bodyBounds,
                    visualBounds = resolveCompoundVisualBounds(
                        node = node,
                        bodyBounds = bodyBounds,
                        labelExpansion = LABEL_BOUNDS_EXPANSION,
                    ),
                    outerVisualBounds = resolveCompoundVisualBounds(
                        node = node,
                        bodyBounds = bodyBounds,
                        labelExpansion = OUTER_LABEL_BOUNDS_EXPANSION,
                    ),
                )
            }
            active.remove(node.id)
            resolved[node.id] = geometry
            return GMResult.Ok(geometry)
        }

        nodes.forEach { node ->
            when (val result = resolve(node)) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }
        return GMResult.Ok(resolved)
    }

    private fun resolveNodeBodyBounds(node: FcoseNodeInput): FcoseBounds =
        FcoseBounds(
            left = node.centerX - node.width / 2.0 - SIMPLE_BOUNDS_EXPANSION,
            top = node.centerY - node.height / 2.0 - SIMPLE_BOUNDS_EXPANSION,
            right = node.centerX + node.width / 2.0 + SIMPLE_BOUNDS_EXPANSION,
            bottom = node.centerY + node.height / 2.0 + SIMPLE_BOUNDS_EXPANSION,
        )

    private fun resolveNodeVisualBounds(
        node: FcoseNodeInput,
        bodyBounds: FcoseBounds,
        labelExpansion: Double,
    ): FcoseBounds {
        val labelWidth = node.labelWidth
        val labelHeight = node.labelHeight
        if (labelWidth == null || labelHeight == null) {
            return bodyBounds
        }
        // Cytoscape 3.34.x:
        // renderer/base/coord-ele-math/labels.mjs ->
        // recalculateNodeLabelProjection, and
        // collection/dimensions/bounds.mjs -> updateBoundsFromLabel.
        // Architecture labels are centered below the node.
        return bodyBounds.union(
            FcoseBounds(
                left = node.centerX - labelWidth / 2.0 - labelExpansion,
                top = node.centerY + node.height / 2.0 - labelExpansion,
                right = node.centerX + labelWidth / 2.0 + labelExpansion,
                bottom = node.centerY +
                    node.height / 2.0 +
                    labelHeight +
                    labelExpansion,
            ),
        )
    }

    private fun resolveCompoundVisualBounds(
        node: FcoseNodeInput,
        bodyBounds: FcoseBounds,
        labelExpansion: Double,
    ): FcoseBounds {
        val labelWidth = node.labelWidth
        val labelHeight = node.labelHeight
        if (labelWidth == null || labelHeight == null) {
            return bodyBounds
        }
        // A compound's label anchor is at the bottom of its auto body plus
        // padding. In Cytoscape's cached geometry that is 1.5px above the
        // expanded body bound produced by boundingBoxImpl.
        val labelX = bodyBounds.centerX
        val labelY = bodyBounds.bottom - COMPOUND_BOUNDS_EXPANSION
        return bodyBounds.union(
            FcoseBounds(
                left = labelX - labelWidth / 2.0 - labelExpansion,
                top = labelY - labelExpansion,
                right = labelX + labelWidth / 2.0 + labelExpansion,
                bottom = labelY + labelHeight + labelExpansion,
            ),
        )
    }

    private fun filterEdges(
        edges: List<FcoseEdgeInput>,
        nodeIds: Set<String>,
    ): List<FcoseEdgeInput> {
        val pairs = linkedSetOf<Set<String>>()
        return edges.filter { edge ->
            edge.sourceId in nodeIds &&
                edge.targetId in nodeIds &&
                edge.sourceId != edge.targetId &&
                pairs.add(setOf(edge.sourceId, edge.targetId))
        }
    }

    private fun unionBounds(bounds: List<FcoseBounds>): FcoseBounds? {
        if (bounds.isEmpty()) {
            return null
        }
        return FcoseBounds(
            left = bounds.minOf(FcoseBounds::left),
            top = bounds.minOf(FcoseBounds::top),
            right = bounds.maxOf(FcoseBounds::right),
            bottom = bounds.maxOf(FcoseBounds::bottom),
        )
    }

    private data class ResolvedGeometry(
        val left: Double,
        val top: Double,
        val width: Double,
        val height: Double,
    )

    private data class ResolvedCytoscapeNodeGeometry(
        val bodyBounds: FcoseBounds,
        val visualBounds: FcoseBounds,
        val outerVisualBounds: FcoseBounds,
    )

    private data class FcoseBounds(
        val left: Double,
        val top: Double,
        val right: Double,
        val bottom: Double,
    ) {
        val width: Double get() = right - left
        val height: Double get() = bottom - top
        val centerX: Double get() = (left + right) / 2.0
        val centerY: Double get() = (top + bottom) / 2.0

        fun union(other: FcoseBounds): FcoseBounds = FcoseBounds(
            left = minOf(left, other.left),
            top = minOf(top, other.top),
            right = maxOf(right, other.right),
            bottom = maxOf(bottom, other.bottom),
        )

        fun inset(amount: Double): FcoseBounds = FcoseBounds(
            left = left + amount,
            top = top + amount,
            right = right - amount,
            bottom = bottom - amount,
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * diagrams/architecture/architectureSeed.ts -> mulberry32.
     */
    private class Mulberry32(
        seed: UInt,
    ) {
        private var state: UInt = seed

        fun nextDouble(): Double {
            state += 0x6D2B79F5u
            var value = state
            value = (value xor (value shr 15)) * (value or 1u)
            value = value xor (value + (value xor (value shr 7)) * (value or 61u))
            val result = value xor (value shr 14)
            return result.toDouble() / UINT_RANGE
        }
    }

    private const val UINT_RANGE = 4_294_967_296.0
    private const val SIMPLE_BOUNDS_EXPANSION = 1.0
    private const val LABEL_BOUNDS_EXPANSION = 2.0
    private const val OUTER_LABEL_BOUNDS_EXPANSION = 3.0
    private const val COMPOUND_BOUNDS_EXPANSION = 1.5
    private const val LAYOUT_BOUNDS_INSET = 1.0
}
