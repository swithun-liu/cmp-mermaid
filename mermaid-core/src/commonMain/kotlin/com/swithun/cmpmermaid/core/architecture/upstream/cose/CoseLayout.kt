package com.swithun.cmpmermaid.core.architecture.upstream.cose

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LEdge
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LFDLayout
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LFDLayoutOptions
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LGraph
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LNode
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

/**
 * cose-base 2.2.0: src/CoSELayout.js reached incremental constrained path.
 *
 * Radial initialization, tree reduction/growth, bendpoints, and tiling are not
 * part of Mermaid Architecture's production call chain: fCoSE forces
 * incremental mode, passes a constraint key, disables tiling, and disables
 * bend creation.
 */
internal class CoseLayout(
    fdOptions: LFDLayoutOptions,
    val random: CoseRandom,
    val defaultEdgeLength: Double = fdOptions.defaultEdgeLength,
) : LFDLayout(fdOptions) {
    var constraints: CoseConstraints = CoseConstraints()
    var idealEdgeLength: Double = max(defaultEdgeLength, 10.0)
    private var coolingCycle: Int = 0
    private var maxCoolingCycle: Double = 0.0
    private var finalTemperature: Double = CoseConstants.FINAL_TEMPERATURE
    private var coolingAdjuster: Double = 1.0
    private var constraintState: CoseConstraintState? = null

    override fun newGraph(): LGraph = CoseGraph(graphManager)

    override fun newNode(): LNode = CoseNode(graphManager, this)

    override fun newEdge(): LEdge = CoseEdge()

    override fun initParameters() {
        super.initParameters()
        idealEdgeLength = max(defaultEdgeLength, 10.0)
        useSmartIdealEdgeLengthCalculation = true
        constraintState = null
    }

    override fun initSpringEmbedder() {
        super.initSpringEmbedder()
        coolingCycle = 0
        maxCoolingCycle = maxIterations.toDouble() / CoseConstants.CONVERGENCE_CHECK_PERIOD
        finalTemperature = CoseConstants.FINAL_TEMPERATURE
        coolingAdjuster = 1.0
    }

    /**
     * layout-base 2.0.1: src/Layout.js -> runLayout/checkLayoutSuccess.
     */
    fun runLayout(): GMResult<Boolean, String> {
        initParameters()
        if (graphManager.rootGraph.nodes.isEmpty()) {
            return GMResult.Ok(false)
        }
        graphManager.removeInvalidEdges()
        return classicLayout()
    }

    /**
     * cose-base 2.2.0: src/CoSELayout.js -> classicLayout.
     */
    private fun classicLayout(): GMResult<Boolean, String> {
        val nodesWithGravity = calculateNodesToApplyGravitationTo()
        graphManager.setAllNodesToApplyGravitation(nodesWithGravity)
        calcNoOfChildrenForAllNodes()
        graphManager.calcLowestCommonAncestors()
        graphManager.calcInclusionTreeDepths()
        graphManager.rootGraph.calcEstimatedSize()
        calcIdealEdgeLengths()

        if (constraints.hasConstraintKey()) {
            when (val result = ConstraintHandler.handleConstraints(this)) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
            constraintState = ConstraintHandler.initConstraintVariables(this)
        }

        initSpringEmbedder()
        if (CoseConstants.APPLY_LAYOUT) {
            when (val result = runSpringEmbedder()) {
                is GMResult.Ok -> Unit
                is GMResult.Err -> return result
            }
        }
        return GMResult.Ok(true)
    }

    /**
     * cose-base 2.2.0: src/CoSELayout.js -> runSpringEmbedder.
     */
    private fun runSpringEmbedder(): GMResult<Unit, String> {
        var layoutEnded = false
        while (!layoutEnded) {
            when (val result = tick()) {
                is GMResult.Ok -> layoutEnded = result.value
                is GMResult.Err -> return result
            }
        }
        graphManager.updateBounds()
        return GMResult.Ok(Unit)
    }

    /**
     * cose-base 2.2.0: src/CoSELayout.js -> tick.
     */
    private fun tick(): GMResult<Boolean, String> {
        totalIterations++
        if (totalIterations == maxIterations) {
            return GMResult.Ok(true)
        }

        if (totalIterations % CoseConstants.CONVERGENCE_CHECK_PERIOD == 0) {
            if (isConverged()) {
                return GMResult.Ok(true)
            }
            coolingCycle++
            val exponent =
                ln(100.0 * (initialCoolingFactor - finalTemperature)) /
                    ln(maxCoolingCycle)
            coolingFactor = max(
                initialCoolingFactor -
                    coolingCycle.toDouble().pow(exponent) / 100.0 * coolingAdjuster,
                finalTemperature,
            )
        }

        totalDisplacement = 0.0
        graphManager.updateBounds()
        calcSpringForces()
        when (val result = calcRepulsionForces()) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return result
        }
        calcGravitationalForces()
        moveNodes()
        return GMResult.Ok(false)
    }

    /**
     * cose-base 2.2.0: src/CoSELayout.js -> moveNodes.
     */
    private fun moveNodes() {
        val nodes = getAllNodes().map { node -> node as CoseNode }
        nodes.forEach(CoseNode::calculateDisplacement)
        constraintState?.let { state ->
            ConstraintHandler.updateDisplacements(this, state)
        }
        nodes.forEach(CoseNode::move)
    }

    /**
     * cose-base 2.2.0:
     * src/CoSELayout.js -> calculateNodesToApplyGravitationTo.
     */
    private fun calculateNodesToApplyGravitationTo(): List<LNode> =
        buildList {
            graphManager.graphs.forEach { graph ->
                graph.updateConnected()
                if (!graph.isConnected) {
                    addAll(graph.nodes)
                }
            }
        }

    /**
     * cose-base 2.2.0: src/CoSELayout.js -> calcRepulsionRange.
     */
    override fun calcRepulsionRange(): Double =
        2.0 * (level + 1.0) * idealEdgeLength
}
