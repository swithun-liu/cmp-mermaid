package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneAsset
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneElement
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.ScenePathCommand
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Debug-only interchange format used by the Native/Official parity audit.
 *
 * Element order is intentionally the SceneGraph list order because the Compose
 * renderer paints that list sequentially.
 */
internal fun MermaidScene.toAuditManifestJson(): String = buildString {
    append('{')
    append("\"schemaVersion\":1,")
    append("\"renderer\":\"native\",")
    append("\"viewport\":")
    appendViewport(this@toAuditManifestJson)
    append(',')
    append("\"title\":")
    appendJsonStringOrNull(title)
    append(',')
    append("\"accessibilityTitle\":")
    appendJsonStringOrNull(accessibilityTitle)
    append(',')
    append("\"accessibilityDescription\":")
    appendJsonStringOrNull(accessibilityDescription)
    append(',')
    append("\"elements\":[")
    elements.forEachIndexed { index, element ->
        if (index > 0) append(',')
        appendElement(index, element)
    }
    append("]}")
}

private fun StringBuilder.appendViewport(scene: MermaidScene) {
    append('{')
    append("\"x\":0,\"y\":0,\"width\":")
    appendFiniteNumber(scene.width)
    append(',')
    append("\"height\":")
    appendFiniteNumber(scene.height)
    append(',')
    append("\"background\":")
    appendColor(scene.background)
    append('}')
}

private fun StringBuilder.appendElement(
    order: Int,
    element: SceneElement,
) {
    append('{')
    append("\"order\":")
    append(order)
    append(',')
    append("\"zIndex\":")
    append(element.zIndex)
    when (element) {
        is SceneShape -> appendShape(element)
        is ScenePath -> appendPath(element)
        is SceneText -> appendText(element)
        is SceneAsset -> appendAsset(element)
    }
    append('}')
}

private fun StringBuilder.appendShape(shape: SceneShape) {
    append(",\"type\":\"shape\",\"role\":")
    appendJsonString(shape.kind.name)
    append(",\"id\":")
    appendJsonString(shape.id)
    append(",\"bounds\":")
    appendRect(shape.bounds)
    append(",\"fill\":")
    appendColor(shape.fill)
    append(",\"stroke\":")
    appendColor(shape.stroke)
    append(",\"strokeWidth\":")
    appendFiniteNumber(shape.strokeWidth)
    append(",\"strokePattern\":")
    appendJsonString(shape.strokePattern.name)
    append(",\"dashIntervals\":")
    appendNumberArray(shape.dashIntervals)
    append(",\"geometryPathCount\":")
    append(shape.geometry?.paths?.size ?: 0)
    append(",\"strokeGradient\":")
    val gradient = shape.strokeGradient
    if (gradient == null) {
        append("null")
    } else {
        append('{')
        append("\"startColor\":")
        appendColor(gradient.startColor)
        append(",\"endColor\":")
        appendColor(gradient.endColor)
        append('}')
    }
}

private fun StringBuilder.appendPath(path: ScenePath) {
    val markerAnchors = path.auditMarkerAnchors()
    append(",\"type\":\"path\",\"role\":\"path\",\"id\":")
    appendJsonString(path.id)
    append(",\"bounds\":")
    appendRect(path.auditBounds())
    append(",\"fill\":null,\"stroke\":")
    appendColor(path.color)
    append(",\"strokeWidth\":")
    appendFiniteNumber(path.strokeWidth)
    append(",\"strokePattern\":")
    appendJsonString(path.strokePattern.name)
    append(",\"dashIntervals\":")
    appendNumberArray(path.dashIntervals)
    append(",\"arrowStart\":")
    appendJsonString(path.arrowStart.name)
    append(",\"arrowEnd\":")
    appendJsonString(path.arrowEnd.name)
    append(",\"markerStartPoint\":")
    appendPointOrNull(markerAnchors.first)
    append(",\"markerEndPoint\":")
    appendPointOrNull(markerAnchors.second)
    append(",\"curve\":")
    appendJsonString(path.curve)
    append(",\"animated\":")
    append(path.animated)
}

private fun StringBuilder.appendText(text: SceneText) {
    append(",\"type\":\"text\",\"role\":\"text\",\"id\":null,\"bounds\":")
    appendRect(text.auditBounds())
    append(",\"text\":")
    appendJsonString(text.text)
    append(",\"fill\":")
    appendColor(text.color)
    append(",\"stroke\":null,\"fontSize\":")
    appendFiniteNumber(text.fontSize)
    append(",\"fontFamily\":")
    appendJsonStringOrNull(text.fontFamily)
    append(",\"fontWeight\":")
    appendJsonString(text.weight.name)
    append(",\"rotationDegrees\":")
    appendFiniteNumber(text.rotationDegrees)
    append(",\"clipToBounds\":")
    append(text.clipToBounds)
}

private fun SceneText.auditBounds(): SceneRect {
    // MermaidDiagram.drawSceneText applies the same 4px horizontal inset.
    // Report the painted glyph box rather than the SceneText layout box.
    val paintedBounds = when (horizontalAlignment) {
        SceneTextAlignment.Start -> bounds.translateX(4f)
        SceneTextAlignment.Center -> bounds
        SceneTextAlignment.End -> bounds.translateX(-4f)
    }
    if (rotationDegrees == 0f) return paintedBounds
    val pivot = rotationPivot ?: bounds.center
    val radians = rotationDegrees * PI / 180.0
    val cosine = cos(radians).toFloat()
    val sine = sin(radians).toFloat()
    val corners = listOf(
        ScenePoint(paintedBounds.left, paintedBounds.top),
        ScenePoint(paintedBounds.right, paintedBounds.top),
        ScenePoint(paintedBounds.right, paintedBounds.bottom),
        ScenePoint(paintedBounds.left, paintedBounds.bottom),
    ).map { point ->
        val x = point.x - pivot.x
        val y = point.y - pivot.y
        ScenePoint(
            x = pivot.x + x * cosine - y * sine,
            y = pivot.y + x * sine + y * cosine,
        )
    }
    return SceneRect(
        left = corners.minOf(ScenePoint::x),
        top = corners.minOf(ScenePoint::y),
        right = corners.maxOf(ScenePoint::x),
        bottom = corners.maxOf(ScenePoint::y),
    )
}

private fun SceneRect.translateX(offset: Float): SceneRect = SceneRect(
    left = left + offset,
    top = top,
    right = right + offset,
    bottom = bottom,
)

private fun StringBuilder.appendAsset(asset: SceneAsset) {
    append(",\"type\":\"asset\",\"role\":")
    appendJsonString(asset.kind.name)
    append(",\"id\":")
    appendJsonString(asset.id)
    append(",\"bounds\":")
    appendRect(asset.bounds)
    append(",\"fill\":")
    val tint = asset.tint
    if (tint == null) {
        append("null")
    } else {
        appendColor(tint)
    }
    append(",\"stroke\":null")
}

private fun ScenePath.auditBounds(): SceneRect {
    val auditPoints = buildList {
        addAll(points)
        commands.forEach { command ->
            when (command) {
                is ScenePathCommand.MoveTo -> add(command.point)
                is ScenePathCommand.LineTo -> add(command.point)
                is ScenePathCommand.QuadraticTo -> {
                    add(command.control)
                    add(command.end)
                }
                is ScenePathCommand.CubicTo -> {
                    add(command.control1)
                    add(command.control2)
                    add(command.end)
                }
                is ScenePathCommand.ArcTo -> add(command.end)
            }
        }
    }.filter(ScenePoint::isFinite)
    if (auditPoints.isEmpty()) {
        return SceneRect(0f, 0f, 0f, 0f)
    }
    return SceneRect(
        left = auditPoints.minOf(ScenePoint::x),
        top = auditPoints.minOf(ScenePoint::y),
        right = auditPoints.maxOf(ScenePoint::x),
        bottom = auditPoints.maxOf(ScenePoint::y),
    )
}

/**
 * Mirrors MermaidDiagram.markerTangent anchors without exporting renderer
 * implementation details into the production SceneGraph.
 */
private fun ScenePath.auditMarkerAnchors(): Pair<ScenePoint?, ScenePoint?> {
    var current: ScenePoint? = null
    var start: ScenePoint? = null
    var end: ScenePoint? = null
    commands.forEach { command ->
        val from = current
        when (command) {
            is ScenePathCommand.MoveTo -> current = command.point
            is ScenePathCommand.LineTo -> {
                if (from != null && from.isDistinctFrom(command.point)) {
                    if (start == null) start = from
                    end = command.point
                }
                current = command.point
            }
            is ScenePathCommand.QuadraticTo -> {
                if (
                    from != null &&
                    (
                        from.isDistinctFrom(command.control) ||
                            from.isDistinctFrom(command.end)
                        )
                ) {
                    if (start == null) start = from
                    end = command.end
                }
                current = command.end
            }
            is ScenePathCommand.CubicTo -> {
                if (
                    from != null &&
                    (
                        from.isDistinctFrom(command.control1) ||
                            from.isDistinctFrom(command.end)
                        )
                ) {
                    if (start == null) start = from
                }
                if (
                    from != null &&
                    (
                        command.control2.isDistinctFrom(command.end) ||
                            from.isDistinctFrom(command.end)
                        )
                ) {
                    end = command.end
                }
                current = command.end
            }
            is ScenePathCommand.ArcTo -> {
                if (from != null && from.isDistinctFrom(command.end)) {
                    if (start == null) start = from
                    end = command.end
                }
                current = command.end
            }
        }
    }
    return Pair(
        start.takeIf { pathHasStartMarker() },
        end.takeIf { pathHasEndMarker() },
    )
}

private fun ScenePath.pathHasStartMarker(): Boolean =
    arrowStart != SceneArrowHead.None

private fun ScenePath.pathHasEndMarker(): Boolean =
    arrowEnd != SceneArrowHead.None

private fun ScenePoint.isDistinctFrom(other: ScenePoint): Boolean {
    val deltaX = other.x - x
    val deltaY = other.y - y
    return deltaX * deltaX + deltaY * deltaY > 0.0001f * 0.0001f
}

private fun ScenePoint.isFinite(): Boolean = x.isFinite() && y.isFinite()

private fun StringBuilder.appendPointOrNull(point: ScenePoint?) {
    if (point == null || !point.isFinite()) {
        append("null")
        return
    }
    append('{')
    append("\"x\":")
    appendFiniteNumber(point.x)
    append(",\"y\":")
    appendFiniteNumber(point.y)
    append('}')
}

private fun StringBuilder.appendRect(rect: SceneRect) {
    append('{')
    append("\"x\":")
    appendFiniteNumber(rect.left)
    append(",\"y\":")
    appendFiniteNumber(rect.top)
    append(",\"width\":")
    appendFiniteNumber(rect.width)
    append(",\"height\":")
    appendFiniteNumber(rect.height)
    append('}')
}

private fun StringBuilder.appendColor(color: SceneColor) {
    val value = color.argb
    append('{')
    append("\"r\":")
    append((value shr 16) and 0xFF)
    append(",\"g\":")
    append((value shr 8) and 0xFF)
    append(",\"b\":")
    append(value and 0xFF)
    append(",\"a\":")
    append((value shr 24) and 0xFF)
    append('}')
}

private fun StringBuilder.appendNumberArray(values: List<Float>) {
    append('[')
    values.forEachIndexed { index, value ->
        if (index > 0) append(',')
        appendFiniteNumber(value)
    }
    append(']')
}

private fun StringBuilder.appendFiniteNumber(value: Float) {
    if (value.isFinite()) {
        append(value)
    } else {
        append("null")
    }
}

private fun StringBuilder.appendJsonStringOrNull(value: String?) {
    if (value == null) {
        append("null")
    } else {
        appendJsonString(value)
    }
}

private fun StringBuilder.appendJsonString(value: String) {
    append('"')
    value.forEach { character ->
        when (character) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> {
                if (character.code < 0x20) {
                    append("\\u")
                    append(character.code.toString(16).padStart(4, '0'))
                } else {
                    append(character)
                }
            }
        }
    }
    append('"')
}
