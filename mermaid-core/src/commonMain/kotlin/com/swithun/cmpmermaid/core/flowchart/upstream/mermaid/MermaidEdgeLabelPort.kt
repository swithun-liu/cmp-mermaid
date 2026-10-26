package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.ScenePathCommand
import com.swithun.cmpmermaid.core.ScenePoint
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/** Mermaid.js 12.0.0: edges.js final insertEdge stage and utils.ts label utilities. */
internal object MermaidEdgeLabelPort {
    // edges.js -> insertEdge (midIndex/updatedPath), then positionEdgeLabel.
    fun positionEdgeLabel(
        layoutAnchor: ScenePoint,
        points: List<ScenePoint>,
        commands: List<ScenePathCommand>,
        curve: String,
        pathUpdated: Boolean = false,
    ): GMResult<ScenePoint, MermaidError> {
        val middlePoint = points.getOrNull(points.size / 2)
            ?: return GMResult.Err(MermaidError.Layout("Mermaid edge label requires a path point"))
        val d = when (val serialized = pathData(commands, curve)) {
            is GMResult.Ok -> serialized.value
            is GMResult.Err -> return serialized
        }
        return if (pathUpdated || !isLabelCoordinateInPath(middlePoint, d)) {
            calcLabelPosition(points)
        } else {
            GMResult.Ok(layoutAnchor)
        }
    }

    // Mermaid.js 12.0.0: utils.ts -> isLabelCoordinateInPath.
    fun isLabelCoordinateInPath(point: ScenePoint, d: String): Boolean {
        val roundedX = numberString(floor(point.x.toDouble() + 0.5))
        val roundedY = numberString(floor(point.y.toDouble() + 0.5))
        val sanitized = DECIMAL.replace(d) { match ->
            numberString(floor(match.value.toDouble() + 0.5))
        }
        return sanitized.contains(roundedX) || sanitized.contains(roundedY)
    }

    // Mermaid.js 12.0.0: utils.ts -> calcLabelPosition.
    fun calcLabelPosition(points: List<ScenePoint>): GMResult<ScenePoint, MermaidError> =
        if (points.size == 1) GMResult.Ok(points[0]) else traverseEdge(points)

    // Mermaid.js 12.0.0: utils.ts -> traverseEdge.
    private fun traverseEdge(points: List<ScenePoint>): GMResult<ScenePoint, MermaidError> {
        var previous: ScenePoint? = null
        var totalDistance = 0.0
        points.forEach { point ->
            totalDistance += distance(point, previous)
            previous = point
        }
        return calculatePoint(points, totalDistance / 2)
    }

    // Mermaid.js 12.0.0: utils.ts -> distance.
    private fun distance(point: ScenePoint, previous: ScenePoint?): Double {
        if (previous == null) return 0.0
        val dx = point.x.toDouble() - previous.x
        val dy = point.y.toDouble() - previous.y
        return sqrt(dx * dx + dy * dy)
    }

    // Mermaid.js 12.0.0: utils.ts -> calculatePoint.
    // Its final throw is represented by the lower-level safe result contract.
    fun calculatePoint(
        points: List<ScenePoint>,
        distanceToTraverse: Double,
    ): GMResult<ScenePoint, MermaidError> {
        var previous: ScenePoint? = null
        var remainingDistance = distanceToTraverse
        for (point in points) {
            val prev = previous
            if (prev != null) {
                val vectorDistance = distance(point, prev)
                if (vectorDistance == 0.0) return GMResult.Ok(prev)
                if (vectorDistance < remainingDistance) {
                    remainingDistance -= vectorDistance
                } else {
                    val ratio = remainingDistance / vectorDistance
                    if (ratio <= 0) return GMResult.Ok(prev)
                    if (ratio >= 1) return GMResult.Ok(point)
                    if (ratio > 0 && ratio < 1) {
                        return GMResult.Ok(ScenePoint(
                            roundNumber((1 - ratio) * prev.x + ratio * point.x, 5).toFloat(),
                            roundNumber((1 - ratio) * prev.y + ratio * point.y, 5).toFloat(),
                        ))
                    }
                }
            }
            previous = point
        }
        return GMResult.Err(
            MermaidError.Layout("Could not find a suitable point for the given distance"),
        )
    }

    // Mermaid.js 12.0.0: utils.ts -> roundNumber; Math.round ties toward +infinity.
    private fun roundNumber(value: Double, precision: Int): Double {
        val factor = 10.0.pow(precision)
        return floor(value * factor + 0.5) / factor
    }

    // d3-shape/src/path.js -> withPath and d3-path/src/path.js -> appendRound.
    // Rounded Mermaid paths concatenate raw numbers instead of using D3.
    fun pathData(
        commands: List<ScenePathCommand>,
        curve: String,
    ): GMResult<String, MermaidError> {
        fun number(value: Float): String = numberString(
            if (curve == "rounded") value.toDouble() else roundNumber(value.toDouble(), 3),
        )
        fun ScenePoint.xy(): String = "${number(x)},${number(y)}"
        val data = StringBuilder()
        commands.forEach { command ->
            when (command) {
                is ScenePathCommand.MoveTo -> data.append("M${command.point.xy()}")
                is ScenePathCommand.LineTo -> data.append("L${command.point.xy()}")
                is ScenePathCommand.QuadraticTo ->
                    data.append("Q${command.control.xy()} ${command.end.xy()}")
                is ScenePathCommand.CubicTo ->
                    data.append("C${command.control1.xy()},${command.control2.xy()},${command.end.xy()}")
                is ScenePathCommand.ArcTo -> return GMResult.Err(
                    MermaidError.UnsupportedFeature("Arc commands in Mermaid edge label paths"),
                )
            }
        }
        return GMResult.Ok(data.toString())
    }

    // Platform adaptation of JS Number.toString for the floating-point path values.
    private fun numberString(value: Double): String {
        if (value == 0.0) return "0"
        val raw = value.toString().lowercase()
        if ('e' !in raw) return raw.removeSuffix(".0")
        val exponent = raw.substringAfter('e').toInt()
        val mantissa = raw.substringBefore('e').removeSuffix(".0")
        if (abs(value) < 1e-6 || abs(value) >= 1e21) {
            return "$mantissa" + "e" + (if (exponent >= 0) "+" else "") + exponent
        }
        val sign = if (mantissa.startsWith('-')) "-" else ""
        val unsigned = mantissa.removePrefix("-")
        val digits = unsigned.replace(".", "")
        val decimal = unsigned.substringBefore('.').length + exponent
        return sign + when {
            decimal <= 0 -> "0." + "0".repeat(-decimal) + digits
            decimal >= digits.length -> digits + "0".repeat(decimal - digits.length)
            else -> digits.substring(0, decimal) + "." + digits.substring(decimal)
        }
    }

    private val DECIMAL = Regex("(\\d+\\.\\d+)")
}
