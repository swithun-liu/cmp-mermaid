package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.SceneBridge
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePoint
import kotlin.math.abs

internal object LineBridgeRouter {
    private const val ENDPOINT_CLEARANCE = 9f

    fun apply(paths: List<ScenePath>): List<ScenePath> {
        val bridges = paths.map { mutableListOf<SceneBridge>() }
        for (lowerIndex in paths.indices) {
            for (upperIndex in lowerIndex + 1 until paths.size) {
                findCrossings(paths[lowerIndex], paths[upperIndex]).forEach { crossing ->
                    if (bridges[upperIndex].none { it.center.isNear(crossing) }) {
                        bridges[upperIndex] += SceneBridge(crossing)
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
    ): List<ScenePoint> = buildList {
        first.points.zipWithNext().forEach { (firstStart, firstEnd) ->
            second.points.zipWithNext().forEach { (secondStart, secondEnd) ->
                val crossing = orthogonalIntersection(
                    firstStart = firstStart,
                    firstEnd = firstEnd,
                    secondStart = secondStart,
                    secondEnd = secondEnd,
                )
                if (crossing != null) {
                    add(crossing)
                }
            }
        }
    }

    private fun orthogonalIntersection(
        firstStart: ScenePoint,
        firstEnd: ScenePoint,
        secondStart: ScenePoint,
        secondEnd: ScenePoint,
    ): ScenePoint? {
        val firstHorizontal = firstStart.y.isCloseTo(firstEnd.y)
        val firstVertical = firstStart.x.isCloseTo(firstEnd.x)
        val secondHorizontal = secondStart.y.isCloseTo(secondEnd.y)
        val secondVertical = secondStart.x.isCloseTo(secondEnd.x)
        val horizontalStart: ScenePoint
        val horizontalEnd: ScenePoint
        val verticalStart: ScenePoint
        val verticalEnd: ScenePoint
        when {
            firstHorizontal && secondVertical -> {
                horizontalStart = firstStart
                horizontalEnd = firstEnd
                verticalStart = secondStart
                verticalEnd = secondEnd
            }
            firstVertical && secondHorizontal -> {
                horizontalStart = secondStart
                horizontalEnd = secondEnd
                verticalStart = firstStart
                verticalEnd = firstEnd
            }
            else -> return null
        }

        val crossing = ScenePoint(verticalStart.x, horizontalStart.y)
        return crossing.takeIf {
            it.x.isStrictlyBetween(horizontalStart.x, horizontalEnd.x) &&
                it.y.isStrictlyBetween(verticalStart.y, verticalEnd.y)
        }
    }

    private fun Float.isStrictlyBetween(first: Float, second: Float): Boolean {
        val minimum = minOf(first, second) + ENDPOINT_CLEARANCE
        val maximum = maxOf(first, second) - ENDPOINT_CLEARANCE
        return this in minimum..maximum
    }

    private fun Float.isCloseTo(other: Float): Boolean = abs(this - other) < 0.01f

    private fun ScenePoint.isNear(other: ScenePoint): Boolean =
        abs(x - other.x) < 1f && abs(y - other.y) < 1f
}
