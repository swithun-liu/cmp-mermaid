package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneSize
import kotlin.math.max

/**
 * Coordinate-assignment stage shared by flat and recursively laid out compound graphs.
 */
internal object FlowNodePlacer {
    fun place(
        direction: FlowDirection,
        layers: List<List<String>>,
        sizes: Map<String, SceneSize>,
        edgeLabelSizes: Map<Int, SceneSize>,
        edges: List<FlowEdge>,
        ranks: Map<String, Int>,
        options: MermaidRenderOptions,
    ): Map<String, SceneRect> {
        val vertical = direction == FlowDirection.TopToBottom ||
            direction == FlowDirection.BottomToTop
        val labelPrimarySizes = mutableMapOf<Int, Float>()
        edgeLabelSizes.forEach { (edgeIndex, labelSize) ->
            val edge = edges.getOrNull(edgeIndex) ?: return@forEach
            val fromRank = ranks[edge.from] ?: return@forEach
            val toRank = ranks[edge.to] ?: return@forEach
            val labelRank = (fromRank + toRank) / 2
            labelPrimarySizes[labelRank] = max(
                labelPrimarySizes[labelRank] ?: 0f,
                labelSize.primary(vertical) + EDGE_LABEL_PRIMARY_PADDING,
            )
        }
        val layerPrimarySizes = layers.mapIndexed { rank, layer ->
            max(
                layer.maxOfOrNull { id -> sizes.getValue(id).primary(vertical) } ?: 0f,
                labelPrimarySizes[rank] ?: 0f,
            )
        }
        val layerCrossSizes = layers.map { layer ->
            layer.sumOfFloat { id -> sizes.getValue(id).cross(vertical) } +
                options.horizontalSpacing * (layer.size - 1).coerceAtLeast(0)
        }
        val maxCross = layerCrossSizes.maxOrNull() ?: 0f
        val rankSpacing = options.verticalSpacing / RANK_SCALE
        val primaryLength = layerPrimarySizes.sum() +
            rankSpacing * (layers.size - 1).coerceAtLeast(0)
        val result = linkedMapOf<String, SceneRect>()
        var primaryCursor = ORIGIN

        layers.forEachIndexed { layerIndex, layer ->
            val layerPrimary = layerPrimarySizes[layerIndex]
            var crossCursor = ORIGIN + (maxCross - layerCrossSizes[layerIndex]) / 2f
            layer.forEach { id ->
                val size = sizes.getValue(id)
                val primarySize = size.primary(vertical)
                val crossSize = size.cross(vertical)
                var primary = primaryCursor + (layerPrimary - primarySize) / 2f
                if (direction == FlowDirection.BottomToTop || direction == FlowDirection.RightToLeft) {
                    primary = ORIGIN + primaryLength - (primary - ORIGIN) - primarySize
                }
                val cross = crossCursor
                result[id] = if (vertical) {
                    SceneRect(cross, primary, cross + size.width, primary + size.height)
                } else {
                    SceneRect(primary, cross, primary + size.width, cross + size.height)
                }
                crossCursor += crossSize + options.horizontalSpacing
            }
            primaryCursor += layerPrimary + rankSpacing
        }
        return result
    }

    private fun SceneSize.primary(vertical: Boolean): Float = if (vertical) height else width

    private fun SceneSize.cross(vertical: Boolean): Float = if (vertical) width else height

    private fun <T> Iterable<T>.sumOfFloat(selector: (T) -> Float): Float {
        var total = 0f
        for (item in this) {
            total += selector(item)
        }
        return total
    }

    private const val ORIGIN = 72f
    private const val RANK_SCALE = 2f
    private const val EDGE_LABEL_PRIMARY_PADDING = 24f
}
