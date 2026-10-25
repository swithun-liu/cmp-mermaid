package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import kotlin.math.abs

/** Mermaid.js 12.0.0: rendering-util/rendering-elements/edges.js. */
internal object MermaidClusterEdgePort {
    // edges.js -> outsideNode: the boundary itself is outside.
    fun outsideNode(node: SceneRect, point: ScenePoint): Boolean =
        abs(point.x - node.center.x) >= node.width / 2f ||
            abs(point.y - node.center.y) >= node.height / 2f

    // edges.js -> intersection; retain every axis and zero-delta branch.
    fun intersection(
        node: SceneRect,
        outsidePoint: ScenePoint,
        insidePoint: ScenePoint,
    ): ScenePoint {
        val x = node.center.x
        val y = node.center.y
        val dx = abs(x - insidePoint.x)
        val w = node.width / 2f
        var r = if (insidePoint.x < outsidePoint.x) w - dx else w + dx
        val h = node.height / 2f
        val qDelta = abs(outsidePoint.y - insidePoint.y)
        val rDelta = abs(outsidePoint.x - insidePoint.x)
        var resultX: Float
        var resultY: Float
        if (abs(y - outsidePoint.y) * w > abs(x - outsidePoint.x) * h) {
            val q = if (insidePoint.y < outsidePoint.y) outsidePoint.y - h - y else y - h - outsidePoint.y
            r = rDelta * q / qDelta
            resultX = if (insidePoint.x < outsidePoint.x) insidePoint.x + r else insidePoint.x - rDelta + r
            resultY = if (insidePoint.y < outsidePoint.y) insidePoint.y + qDelta - q else insidePoint.y - qDelta + q
        } else {
            r = if (insidePoint.x < outsidePoint.x) outsidePoint.x - w - x else x - w - outsidePoint.x
            val q = qDelta * r / rDelta
            resultX = if (insidePoint.x < outsidePoint.x) insidePoint.x + rDelta - r else insidePoint.x - rDelta + r
            resultY = if (insidePoint.y < outsidePoint.y) insidePoint.y + q else insidePoint.y - q
        }
        if (r == 0f) {
            resultX = outsidePoint.x
            resultY = outsidePoint.y
        }
        if (rDelta == 0f) resultX = outsidePoint.x
        if (qDelta == 0f) resultY = outsidePoint.y
        return ScenePoint(resultX, resultY)
    }

    // edges.js -> cutPathAtIntersect, including the first-point-inside case.
    fun cutPathAtIntersect(points: List<ScenePoint>, boundaryNode: SceneRect): List<ScenePoint> {
        val result = mutableListOf<ScenePoint>()
        var lastPointOutside = points.firstOrNull() ?: return result
        var isInside = false
        points.forEach { point ->
            if (!outsideNode(boundaryNode, point) && !isInside) {
                val intersect = intersection(boundaryNode, lastPointOutside, point)
                if (result.none { it.x == intersect.x && it.y == intersect.y }) {
                    result += intersect
                }
                isInside = true
            } else {
                lastPointOutside = point
                if (!isInside) result += point
            }
        }
        return result
    }
}
