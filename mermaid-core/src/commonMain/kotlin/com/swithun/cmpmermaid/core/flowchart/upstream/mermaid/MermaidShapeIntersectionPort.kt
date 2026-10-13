package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import kotlin.math.abs

internal object MermaidShapeIntersectionPort {
    fun intersect(
        shapeLayout: MermaidShapeLayout?,
        bounds: SceneRect,
        toward: ScenePoint,
    ): ScenePoint {
        val center = bounds.center
        if (shapeLayout == null || hasRectangularOutline(shapeLayout, bounds)) {
            return intersectRect(bounds, toward)
        }
        return intersectPolygon(bounds, shapeLayout.geometry.outline, toward)
    }

    /**
     * Mermaid.js 12.0.0:
     * rendering-util/rendering-elements/intersect/intersect-rect.js
     * -> intersectRect.
     */
    private fun intersectRect(
        bounds: SceneRect,
        point: ScenePoint,
    ): ScenePoint {
        val center = bounds.center
        val dx = point.x.toDouble() - center.x.toDouble()
        val dy = point.y.toDouble() - center.y.toDouble()
        var halfWidth = bounds.width.toDouble() / 2.0
        var halfHeight = bounds.height.toDouble() / 2.0
        val offsetX: Double
        val offsetY: Double
        if (abs(dy) * halfWidth > abs(dx) * halfHeight) {
            if (dy < 0.0) halfHeight = -halfHeight
            offsetX = if (dy == 0.0) 0.0 else halfHeight * dx / dy
            offsetY = halfHeight
        } else {
            if (dx < 0.0) halfWidth = -halfWidth
            offsetX = halfWidth
            offsetY = if (dx == 0.0) 0.0 else halfWidth * dy / dx
        }
        return ScenePoint(
            x = (center.x.toDouble() + offsetX).toFloat(),
            y = (center.y.toDouble() + offsetY).toFloat(),
        )
    }

    /**
     * Mermaid.js 12.0.0:
     * rendering-util/rendering-elements/intersect/intersect-polygon.js
     * -> intersectPolygon.
     */
    private fun intersectPolygon(
        bounds: SceneRect,
        outline: List<ScenePoint>,
        point: ScenePoint,
    ): ScenePoint {
        val center = bounds.center
        if (outline.isEmpty()) return center
        val minX = outline.minOf(ScenePoint::x)
        val minY = outline.minOf(ScenePoint::y)
        val left = center.x - bounds.width / 2f - minX
        val top = center.y - bounds.height / 2f - minY
        val translatedOutline = outline.map { polygonPoint ->
            ScenePoint(
                x = left + polygonPoint.x,
                y = top + polygonPoint.y,
            )
        }
        val intersections = (translatedOutline + translatedOutline.first())
            .zipWithNext()
            .mapNotNull { (first, second) ->
                intersectLine(center, point, first, second)
            }
        return intersections.minByOrNull { intersection ->
            val dx = intersection.x.toDouble() - point.x.toDouble()
            val dy = intersection.y.toDouble() - point.y.toDouble()
            dx * dx + dy * dy
        } ?: center
    }

    /**
     * Mermaid.js 12.0.0:
     * rendering-util/rendering-elements/intersect/intersect-line.js
     * -> intersectLine.
     */
    private fun intersectLine(
        firstStart: ScenePoint,
        firstEnd: ScenePoint,
        secondStart: ScenePoint,
        secondEnd: ScenePoint,
    ): ScenePoint? {
        val firstA = firstEnd.y.toDouble() - firstStart.y.toDouble()
        val firstB = firstStart.x.toDouble() - firstEnd.x.toDouble()
        val firstC =
            firstEnd.x.toDouble() * firstStart.y.toDouble() -
                firstStart.x.toDouble() * firstEnd.y.toDouble()
        val secondStartSide =
            firstA * secondStart.x.toDouble() +
                firstB * secondStart.y.toDouble() +
                firstC
        val secondEndSide =
            firstA * secondEnd.x.toDouble() +
                firstB * secondEnd.y.toDouble() +
                firstC
        if (
            secondStartSide != 0.0 &&
            secondEndSide != 0.0 &&
            sameSign(secondStartSide, secondEndSide)
        ) {
            return null
        }

        val secondA = secondEnd.y.toDouble() - secondStart.y.toDouble()
        val secondB = secondStart.x.toDouble() - secondEnd.x.toDouble()
        val secondC =
            secondEnd.x.toDouble() * secondStart.y.toDouble() -
                secondStart.x.toDouble() * secondEnd.y.toDouble()
        val firstStartSide =
            secondA * firstStart.x.toDouble() +
                secondB * firstStart.y.toDouble() +
                secondC
        val firstEndSide =
            secondA * firstEnd.x.toDouble() +
                secondB * firstEnd.y.toDouble() +
                secondC
        if (
            abs(firstStartSide) < LineEpsilon &&
            abs(firstEndSide) < LineEpsilon &&
            sameSign(firstStartSide, firstEndSide)
        ) {
            return null
        }

        val denominator = firstA * secondB - secondA * firstB
        if (denominator == 0.0) return null
        return ScenePoint(
            x = ((firstB * secondC - secondB * firstC) / denominator).toFloat(),
            y = ((secondA * firstC - firstA * secondC) / denominator).toFloat(),
        )
    }

    private fun hasRectangularOutline(
        shapeLayout: MermaidShapeLayout,
        bounds: SceneRect,
    ): Boolean {
        val outline = shapeLayout.geometry.outline
        if (outline.size != 4) return false
        val halfWidth = bounds.width / 2f
        val halfHeight = bounds.height / 2f
        val expected = listOf(
            ScenePoint(-halfWidth, -halfHeight),
            ScenePoint(halfWidth, -halfHeight),
            ScenePoint(halfWidth, halfHeight),
            ScenePoint(-halfWidth, halfHeight),
        )
        return outline.zip(expected).all { (actual, corner) ->
            abs(actual.x - corner.x) < OutlineEpsilon &&
                abs(actual.y - corner.y) < OutlineEpsilon
        }
    }

    private fun sameSign(first: Double, second: Double): Boolean = first * second > 0.0

    private const val LineEpsilon = 1e-6
    private const val OutlineEpsilon = 1e-3f
}
