package com.swithun.cmpmermaid.core.architecture

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneTextWeight
import com.swithun.cmpmermaid.core.TextMetricsRequest
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseAlignmentConstraint
import com.swithun.cmpmermaid.core.architecture.upstream.cose.CoseRelativePlacementConstraint
import com.swithun.cmpmermaid.core.architecture.upstream.fcose.FcoseAdapter
import com.swithun.cmpmermaid.core.architecture.upstream.fcose.FcoseEdgeInput
import com.swithun.cmpmermaid.core.architecture.upstream.fcose.FcoseNodeInput
import com.swithun.cmpmermaid.core.architecture.upstream.fcose.FcoseOptions
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureAlignment
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureAlignmentDirection
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDataStructures
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDb
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureDirection
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureGroup
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitectureLayoutHint
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.ArchitecturePosition
import com.swithun.cmpmermaid.core.architecture.upstream.mermaid.architectureGroupAlignmentKey
import kotlin.math.ceil

internal data class ArchitectureLayoutResult(
    val nodePositions: Map<String, ScenePoint>,
    val groupBounds: Map<String, SceneRect>,
)

/**
 * Mermaid.js 12.0.0:
 * architectureRenderer.ts -> layoutArchitecture/getAlignments/
 * getRelativeConstraints, followed by two seeded cytoscape-fcose layout runs.
 */
internal object ArchitectureFcoseLayout {
    fun layout(
        db: ArchitectureDb,
        context: MermaidRenderContext,
    ): GMResult<ArchitectureLayoutResult, MermaidError> {
        val architectureNodes = db.getNodes()
        val groups = db.getGroups()
        if (architectureNodes.isEmpty() && groups.isEmpty()) {
            return GMResult.Ok(ArchitectureLayoutResult(emptyMap(), emptyMap()))
        }
        val config = db.config
        if (
            !config.iconSize.isFinite() ||
            !config.padding.isFinite() ||
            !config.numIter.isFinite() ||
            !config.seed.isFinite() ||
            config.iconSize <= 0f ||
            config.padding < 0f ||
            config.numIter < 1f ||
            config.numIter > Int.MAX_VALUE.toFloat()
        ) {
            return layoutError(
                "Architecture iconSize/numIter must be positive, padding non-negative, " +
                    "and seed finite",
            )
        }
        if (config.randomize) {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = "Architecture fCoSE spectral initialization",
                ),
            )
        }
        val structures = when (val result = db.getDataStructures()) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val constraints = when (val result = buildConstraints(db, structures)) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        val groupLabelDimensions = linkedMapOf<String, CytoscapeLabelDimensions>()
        groups.forEach { group ->
            val title = group.title?.takeIf(String::isNotEmpty) ?: return@forEach
            when (
                val result = measureCytoscapeLabelDimensions(
                    text = title,
                    owner = "group '${group.id}'",
                    config = config,
                    context = context,
                )
            ) {
                is GMResult.Ok -> groupLabelDimensions[group.id] = result.value
                is GMResult.Err -> return result
            }
        }
        val serviceLabelDimensions = linkedMapOf<String, CytoscapeLabelDimensions>()
        db.getServices().forEach { service ->
            val title = service.title?.takeIf(String::isNotEmpty) ?: return@forEach
            when (
                val result = measureCytoscapeLabelDimensions(
                    text = title,
                    owner = "service '${service.id}'",
                    config = config,
                    context = context,
                )
            ) {
                is GMResult.Ok -> serviceLabelDimensions[service.id] = result.value
                is GMResult.Err -> return result
            }
        }
        val fcoseNodes = buildList {
            groups.forEach { group ->
                val labelDimensions = groupLabelDimensions[group.id]
                add(
                    FcoseNodeInput(
                        id = group.id,
                        parentId = group.parent,
                        centerX = 0.0,
                        centerY = 0.0,
                        width = DEFAULT_CYTOSCAPE_NODE_SIZE + 2.0 * config.padding,
                        height = DEFAULT_CYTOSCAPE_NODE_SIZE + 2.0 * config.padding,
                        padding = config.padding.toDouble(),
                        labelWidth = labelDimensions?.width,
                        labelHeight = labelDimensions?.height,
                    ),
                )
            }
            db.getServices().forEach { service ->
                val labelDimensions = serviceLabelDimensions[service.id]
                add(
                    FcoseNodeInput(
                        id = service.id,
                        parentId = service.parent,
                        centerX = 0.0,
                        centerY = 0.0,
                        width = config.iconSize.toDouble(),
                        height = config.iconSize.toDouble(),
                        padding = 0.0,
                        labelWidth = labelDimensions?.width,
                        labelHeight = labelDimensions?.height,
                    ),
                )
            }
            db.getJunctions().forEach { junction ->
                add(
                    FcoseNodeInput(
                        id = junction.id,
                        parentId = junction.parent,
                        centerX = 0.0,
                        centerY = 0.0,
                        width = config.iconSize.toDouble(),
                        height = config.iconSize.toDouble(),
                        padding = 0.0,
                    ),
                )
            }
        }
        val parentById = buildMap<String, String?> {
            groups.forEach { group -> put(group.id, group.parent) }
            architectureNodes.forEach { node -> put(node.id, node.parent) }
        }
        val sameGroupIdealLength =
            config.idealEdgeLengthMultiplier.toDouble() * config.iconSize
        val crossGroupIdealLength = 0.5 * config.iconSize
        val fcoseEdges = db.getEdges().map { edge ->
            val sameGroup = parentById[edge.lhsId] == parentById[edge.rhsId]
            FcoseEdgeInput(
                id = "${edge.lhsId}-${edge.rhsId}",
                sourceId = edge.lhsId,
                targetId = edge.rhsId,
                idealLength = if (sameGroup) {
                    sameGroupIdealLength
                } else {
                    crossGroupIdealLength
                },
                elasticity = if (sameGroup) {
                    config.edgeElasticity.toDouble()
                } else {
                    CROSS_GROUP_ELASTICITY
                },
            )
        }
        val relativeConstraints = constraints.relativePlacements.map { constraint ->
            when (constraint.axis) {
                Axis.X -> CoseRelativePlacementConstraint(
                    left = constraint.before,
                    right = constraint.after,
                    gap = constraint.gap,
                )
                Axis.Y -> CoseRelativePlacementConstraint(
                    top = constraint.before,
                    bottom = constraint.after,
                    gap = constraint.gap,
                )
            }
        }
        val result = when (
            val layoutResult = FcoseAdapter.runTwice(
                FcoseOptions(
                    nodes = fcoseNodes,
                    edges = fcoseEdges,
                    alignmentConstraint = CoseAlignmentConstraint(
                        vertical = constraints.verticalAlignments,
                        horizontal = constraints.horizontalAlignments,
                    ),
                    relativePlacementConstraint = relativeConstraints,
                    numIterations = config.numIter.toInt(),
                    seed = config.seed,
                ),
            )
        ) {
            is GMResult.Ok -> layoutResult.value
            is GMResult.Err -> return layoutError(layoutResult.error)
        }

        val groupIds = groups.mapTo(linkedSetOf(), ArchitectureGroup::id)
        val groupBounds = groups.associate { group ->
            val node = result[group.id]
                ?: return layoutError("fCoSE omitted Architecture group ${group.id}")
            // Mermaid svgDraw.ts -> drawGroups offsets the Cytoscape bounding
            // box by half the service icon size without changing its extent.
            val drawLeft = node.left + config.iconSize / 2.0
            val drawTop = node.top + config.iconSize / 2.0
            group.id to SceneRect(
                left = drawLeft.toFloat(),
                top = drawTop.toFloat(),
                right = (drawLeft + node.width).toFloat(),
                bottom = (drawTop + node.height).toFloat(),
            )
        }
        val nodePositions = architectureNodes.associate { node ->
            val positioned = result[node.id]
                ?: return layoutError("fCoSE omitted Architecture node ${node.id}")
            node.id to ScenePoint(
                x = positioned.centerX.toFloat(),
                y = positioned.centerY.toFloat(),
            )
        }
        if (result.keys.any { id -> id !in groupIds && id !in nodePositions }) {
            return layoutError("fCoSE returned a node outside the Architecture graph")
        }
        return GMResult.Ok(
            ArchitectureLayoutResult(
                nodePositions = nodePositions,
                groupBounds = groupBounds,
            ),
        )
    }

    /**
     * Cytoscape 3.34.x:
     * renderer/base/coord-ele-math/labels.mjs ->
     * calculateLabelDimensions/applyPrefixedLabelDimensions.
     */
    private fun measureCytoscapeLabelDimensions(
        text: String,
        owner: String,
        config: com.swithun.cmpmermaid.core.MermaidArchitectureOptions,
        context: MermaidRenderContext,
    ): GMResult<CytoscapeLabelDimensions, MermaidError> {
        val lines = text.split('\n')
        val lineWidths = lines.map { line ->
            context.textMetrics.measure(
                TextMetricsRequest(
                    text = line,
                    fontSize = config.fontSize,
                    maxWidth = UNWRAPPED_LABEL_MAX_WIDTH,
                    fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                    weight = SceneTextWeight.Normal,
                ),
            ).width
        }
        if (lineWidths.any { width -> !width.isFinite() || width < 0f }) {
            return layoutError("Architecture $owner produced invalid label metrics")
        }
        return GMResult.Ok(
            CytoscapeLabelDimensions(
                // CanvasRenderer.calculateLabelDimensions rounds each measured
                // line width up to the next integer.
                width = lineWidths.maxOf { width -> ceil(width.toDouble()) },
                height = lines.size * config.fontSize.toDouble(),
            ),
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * architectureRenderer.ts -> getAlignments/getRelativeConstraints.
     */
    private fun buildConstraints(
        db: ArchitectureDb,
        structures: ArchitectureDataStructures,
    ): GMResult<Constraints, MermaidError> {
        val hints = db.getLayoutHints()
        val alignments = structures.spatialMaps.map { spatialMap ->
            val horizontal = linkedMapOf<Int, LinkedHashMap<String, MutableList<String>>>()
            val vertical = linkedMapOf<Int, LinkedHashMap<String, MutableList<String>>>()
            spatialMap.forEach { (id, position) ->
                val nodeGroup = db.getNode(id)?.parent ?: DEFAULT_GROUP
                horizontal.getOrPut(position.y, ::linkedMapOf)
                    .getOrPut(nodeGroup, ::mutableListOf)
                    .add(id)
                vertical.getOrPut(position.x, ::linkedMapOf)
                    .getOrPut(nodeGroup, ::mutableListOf)
                    .add(id)
            }
            Pair(
                flattenAlignments(
                    alignmentMap = horizontal,
                    alignmentDirection = ArchitectureAlignment.Horizontal,
                    groupAlignments = structures.groupAlignments,
                ),
                flattenAlignments(
                    alignmentMap = vertical,
                    alignmentDirection = ArchitectureAlignment.Vertical,
                    groupAlignments = structures.groupAlignments,
                ),
            )
        }
        val declaredMembers = hints.flatMapTo(linkedSetOf(), ArchitectureLayoutHint::members)
        val horizontal = alignments
            .flatMap { (groups, _) -> groups }
            .filter { group -> group.none(declaredMembers::contains) }
            .toMutableList()
        val vertical = alignments
            .flatMap { (_, groups) -> groups }
            .filter { group -> group.none(declaredMembers::contains) }
            .toMutableList()
        hints.forEach { hint ->
            if (hint.members.size < 2) {
                return@forEach
            }
            if (hint.direction == ArchitectureAlignmentDirection.Row) {
                horizontal += hint.members.toList()
            } else {
                vertical += hint.members.toList()
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
                    RelativeConstraint(first, second, Axis.X, gap.toDouble())
                } else {
                    RelativeConstraint(first, second, Axis.Y, gap.toDouble())
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
                                gap = gap.toDouble(),
                            )
                        }
                    }
                }
            }
        }
        return GMResult.Ok(
            Constraints(
                horizontalAlignments = horizontal,
                verticalAlignments = vertical,
                relativePlacements = relative,
            ),
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * architectureRenderer.ts -> getAlignments/flattenAlignments.
     */
    private fun flattenAlignments(
        alignmentMap: Map<Int, Map<String, List<String>>>,
        alignmentDirection: ArchitectureAlignment,
        groupAlignments: Map<String, ArchitectureAlignment>,
    ): List<List<String>> {
        val flattened = linkedMapOf<String, List<String>>()
        alignmentMap.forEach { (numericDirection, alignments) ->
            val direction = numericDirection.toString()
            var count = 0
            val entries = alignments.entries.toList()
            if (entries.size == 1) {
                flattened[direction] = entries.first().value
                return@forEach
            }
            for (firstIndex in 0 until entries.lastIndex) {
                for (secondIndex in firstIndex + 1 until entries.size) {
                    val first = entries[firstIndex]
                    val second = entries[secondIndex]
                    val alignment = groupAlignments[
                        architectureGroupAlignmentKey(first.key, second.key)
                    ]
                    if (
                        alignment == alignmentDirection ||
                        first.key == DEFAULT_GROUP ||
                        second.key == DEFAULT_GROUP
                    ) {
                        flattened[direction] =
                            flattened[direction].orEmpty() +
                                first.value +
                                second.value
                    } else {
                        flattened["$direction-${count++}"] = first.value
                        flattened["$direction-${count++}"] = second.value
                    }
                }
            }
        }
        return flattened.values.filter { group -> group.size > 1 }
    }

    private fun ArchitectureDirection.toRelativeConstraint(
        adjacentId: String,
        currentId: String,
        gap: Double,
    ): RelativeConstraint = when (this) {
        ArchitectureDirection.L -> RelativeConstraint(adjacentId, currentId, Axis.X, gap)
        ArchitectureDirection.R -> RelativeConstraint(currentId, adjacentId, Axis.X, gap)
        ArchitectureDirection.T -> RelativeConstraint(adjacentId, currentId, Axis.Y, gap)
        ArchitectureDirection.B -> RelativeConstraint(currentId, adjacentId, Axis.Y, gap)
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

    private data class CytoscapeLabelDimensions(
        val width: Double,
        val height: Double,
    )

    private enum class Axis {
        X,
        Y,
    }

    private fun layoutError(message: String): GMResult.Err<MermaidError> =
        GMResult.Err(MermaidError.Layout(message))

    private const val DEFAULT_GROUP = "default"
    private const val DEFAULT_CYTOSCAPE_NODE_SIZE = 30.0
    private const val CROSS_GROUP_ELASTICITY = 0.001
    private const val UNWRAPPED_LABEL_MAX_WIDTH = 100_000f

    private val DIRECTION_SHIFTS = linkedMapOf(
        ArchitectureDirection.L to ArchitecturePosition(-1, 0),
        ArchitectureDirection.R to ArchitecturePosition(1, 0),
        ArchitectureDirection.T to ArchitecturePosition(0, 1),
        ArchitectureDirection.B to ArchitecturePosition(0, -1),
    )
}
