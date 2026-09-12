package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.SceneBridge
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePoint
import kotlin.math.abs

/**
 * Native geometry port of Mermaid 12.0.0:
 * packages/mermaid/src/rendering-util/rendering-elements/lineJump.ts
 */
internal object LineBridgeRouter {
    private const val ENDPOINT_EPSILON = 0.000001f

    fun apply(paths: List<ScenePath>): List<ScenePath> {
        val bridges = paths.map { mutableListOf<SceneBridge>() }
        for (firstIndex in paths.indices) {
            for (secondIndex in firstIndex + 1 until paths.size) {
                findCrossings(paths[firstIndex], paths[secondIndex]).forEach { crossing ->
                    val bridgeIndex = if (crossing.bridgeFirst) firstIndex else secondIndex
                    if (bridges[bridgeIndex].none { it.center.isNear(crossing.point) }) {
                        bridges[bridgeIndex] += SceneBridge(crossing.point)
                    }
                }
            }
        }
        return paths.mapIndexed { index, path ->
            path.copy(bridges = bridges[index])
        }
    }

    private fun findCrossings(
        first: ScenePath,
        second: ScenePath,
    ): List<Crossing> = buildList {
        first.points.zipWithNext().forEach { (firstStart, firstEnd) ->
            second.points.zipWithNext().forEach { (secondStart, secondEnd) ->
                val intersection = segmentIntersection(
                    firstStart = firstStart,
                    firstEnd = firstEnd,
                    secondStart = secondStart,
                    secondEnd = secondEnd,
                ) ?: return@forEach
                val firstHorizontal = isHorizontallyDominant(firstStart, firstEnd)
                val secondHorizontal = isHorizontallyDominant(secondStart, secondEnd)
                add(
                    Crossing(
                        point = intersection,
                        bridgeFirst = firstHorizontal != secondHorizontal && firstHorizontal,
                    ),
                )
            }
        }
    }

    private fun segmentIntersection(
        firstStart: ScenePoint,
        firstEnd: ScenePoint,
        secondStart: ScenePoint,
        secondEnd: ScenePoint,
    ): ScenePoint? {
        val firstDeltaX = firstEnd.x - firstStart.x
        val firstDeltaY = firstEnd.y - firstStart.y
        val secondDeltaX = secondEnd.x - secondStart.x
        val secondDeltaY = secondEnd.y - secondStart.y
        val denominator = firstDeltaX * secondDeltaY - firstDeltaY * secondDeltaX
        if (abs(denominator) < ENDPOINT_EPSILON) return null

        val offsetX = secondStart.x - firstStart.x
        val offsetY = secondStart.y - firstStart.y
        val firstParameter =
            (offsetX * secondDeltaY - offsetY * secondDeltaX) / denominator
        val secondParameter =
            (offsetX * firstDeltaY - offsetY * firstDeltaX) / denominator
        if (firstParameter <= ENDPOINT_EPSILON ||
            firstParameter >= 1f - ENDPOINT_EPSILON ||
            secondParameter <= ENDPOINT_EPSILON ||
            secondParameter >= 1f - ENDPOINT_EPSILON
        ) {
            return null
        }
        return ScenePoint(
            x = firstStart.x + firstParameter * firstDeltaX,
            y = firstStart.y + firstParameter * firstDeltaY,
        )
    }

    private fun isHorizontallyDominant(start: ScenePoint, end: ScenePoint): Boolean =
        abs(end.x - start.x) >= abs(end.y - start.y)

    private fun ScenePoint.isNear(other: ScenePoint): Boolean =
        abs(x - other.x) < 1f && abs(y - other.y) < 1f

    private data class Crossing(
        val point: ScenePoint,
        val bridgeFirst: Boolean,
    )
}
