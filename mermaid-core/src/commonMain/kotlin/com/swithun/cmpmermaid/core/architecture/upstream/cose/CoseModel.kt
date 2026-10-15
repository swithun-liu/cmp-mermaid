package com.swithun.cmpmermaid.core.architecture.upstream.cose

import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LDimensionD
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LFDEdge
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LFDNode
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LGraph
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LGraphManager
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.LPointD
import com.swithun.cmpmermaid.core.architecture.upstream.layoutbase.layoutBaseSign
import kotlin.math.abs

/**
 * cose-base 2.2.0:
 * src/CoSEConstants.js reached constants for the incremental constrained path.
 */
internal object CoseConstants {
    const val RELAX_MOVEMENT_ON_CONSTRAINTS = true
    const val TRANSFORM_ON_CONSTRAINT_HANDLING = false
    const val ENFORCE_CONSTRAINTS = true
    const val APPLY_LAYOUT = true
    const val CONVERGENCE_CHECK_PERIOD = 100
    const val FINAL_TEMPERATURE = 0.04
}

internal data class CoseFixedNodeConstraint(
    val nodeId: String,
    val x: Double,
    val y: Double,
)

internal data class CoseAlignmentConstraint(
    val vertical: List<List<String>> = emptyList(),
    val horizontal: List<List<String>> = emptyList(),
)

internal data class CoseRelativePlacementConstraint(
    val left: String? = null,
    val right: String? = null,
    val top: String? = null,
    val bottom: String? = null,
    var gap: Double? = null,
)

internal data class CoseConstraints(
    val fixedNodeConstraint: List<CoseFixedNodeConstraint>? = null,
    val alignmentConstraint: CoseAlignmentConstraint? = null,
    val relativePlacementConstraint: List<CoseRelativePlacementConstraint>? = null,
) {
    /**
     * cose-base checks Object.keys(layout.constraints). Mermaid always installs
     * the alignmentConstraint key, even when both alignment arrays are empty.
     */
    fun hasConstraintKey(): Boolean =
        fixedNodeConstraint != null ||
            alignmentConstraint != null ||
            relativePlacementConstraint != null
}

internal fun interface CoseRandom {
    fun nextDouble(): Double
}

/**
 * cose-base 2.2.0: src/CoSEGraph.js -> CoSEGraph.
 */
internal class CoseGraph(
    graphManager: LGraphManager,
) : LGraph(graphManager)

/**
 * cose-base 2.2.0: src/CoSEEdge.js -> CoSEEdge.
 */
internal class CoseEdge : LFDEdge()

/**
 * cose-base 2.2.0: src/CoSENode.js -> CoSENode.
 */
internal class CoseNode(
    graphManager: LGraphManager,
    private val layout: CoseLayout,
    location: LPointD = LPointD(),
    size: LDimensionD = LDimensionD(),
) : LFDNode(graphManager, location, size) {
    var fixedNodeWeight: Double? = null

    fun calculateDisplacement() {
        val denominator =
            if (child != null && fixedNodeWeight != null) {
                fixedNodeWeight!!
            } else {
                noOfChildren.toDouble()
            }
        displacementX += layout.coolingFactor *
            (springForceX + repulsionForceX + gravitationForceX) /
            denominator
        displacementY += layout.coolingFactor *
            (springForceY + repulsionForceY + gravitationForceY) /
            denominator

        val maximum = layout.coolingFactor * layout.maxNodeDisplacement
        if (abs(displacementX) > maximum) {
            displacementX = maximum * layoutBaseSign(displacementX)
        }
        if (abs(displacementY) > maximum) {
            displacementY = maximum * layoutBaseSign(displacementY)
        }

        if (child?.nodes?.isNotEmpty() == true) {
            propagateDisplacementToChildren(displacementX, displacementY)
        }
    }

    /**
     * Upstream keeps the misspelled name propogateDisplacementToChildren.
     * The Kotlin spelling differs only to avoid propagating that typo.
     */
    private fun propagateDisplacementToChildren(
        deltaX: Double,
        deltaY: Double,
    ) {
        child?.nodes?.forEach { childNode ->
            val coseChild = childNode as CoseNode
            if (coseChild.child == null) {
                coseChild.displacementX += deltaX
                coseChild.displacementY += deltaY
            } else {
                coseChild.propagateDisplacementToChildren(deltaX, deltaY)
            }
        }
    }

    fun move() {
        if (child == null || child?.nodes?.isEmpty() == true) {
            moveBy(displacementX, displacementY)
            layout.totalDisplacement += abs(displacementX) + abs(displacementY)
        }
        springForceX = 0.0
        springForceY = 0.0
        repulsionForceX = 0.0
        repulsionForceY = 0.0
        gravitationForceX = 0.0
        gravitationForceY = 0.0
        displacementX = 0.0
        displacementY = 0.0
    }
}
