package io.github.cmpmermaid.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidEngine
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.MermaidTheme
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneElement
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextAlignment
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricProvider
import io.github.cmpmermaid.core.TextMetrics
import io.github.cmpmermaid.core.TextMetricsRequest
import io.github.cmpmermaid.core.sceneShapeOutline
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun MermaidDiagram(
    source: String,
    modifier: Modifier = Modifier,
    theme: MermaidTheme = MermaidTheme(),
    options: MermaidRenderOptions = MermaidRenderOptions(),
    contentDescription: String = "Mermaid diagram",
) {
    val sceneResult = rememberMermaidScene(source, theme, options)
    when (sceneResult) {
        is GMResult.Ok -> MermaidSceneCanvas(
            scene = sceneResult.value,
            modifier = modifier,
            contentDescription = contentDescription,
        )
        is GMResult.Err -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = sceneResult.error.message,
                style = TextStyle(color = Color(0xFFB91C1C), fontSize = 13.sp),
            )
        }
    }
}

@Composable
fun rememberMermaidScene(
    source: String,
    theme: MermaidTheme = MermaidTheme(),
    options: MermaidRenderOptions = MermaidRenderOptions(),
    engine: MermaidEngine = remember { MermaidEngine() },
): GMResult<MermaidScene, io.github.cmpmermaid.core.MermaidError> {
    val textMeasurer = rememberTextMeasurer(cacheSize = 256)
    val density = LocalDensity.current
    val metrics = remember(textMeasurer, density) {
        TextMetricProvider { request ->
            val style = request.toTextStyle(density.density, density.fontScale)
            val result = textMeasurer.measure(
                text = AnnotatedString(request.text),
                style = style,
                softWrap = true,
                maxLines = 8,
                constraints = Constraints(maxWidth = request.maxWidth.roundToInt().coerceAtLeast(1)),
            )
            TextMetrics(result.size.width.toFloat(), result.size.height.toFloat())
        }
    }
    return remember(source, theme, options, engine, metrics) {
        engine.render(source, MermaidRenderContext(metrics, theme, options))
    }
}

@Composable
fun MermaidSceneCanvas(
    scene: MermaidScene,
    modifier: Modifier = Modifier,
    contentDescription: String = "Mermaid diagram",
) {
    val textMeasurer = rememberTextMeasurer(cacheSize = 256)
    val density = LocalDensity.current
    var viewport by remember(scene) { mutableStateOf(DiagramViewport()) }

    Canvas(
        modifier = modifier
            .clipToBounds()
            .semantics { this.contentDescription = contentDescription }
            .pointerInput(scene) {
                awaitEachGesture {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }
                        if (pressed.size >= 2) {
                            val centroid = pressed
                                .map { it.position }
                                .reduce(Offset::plus) / pressed.size.toFloat()
                            val fitScale = min(
                                size.width / scene.width,
                                size.height / scene.height,
                            )
                            viewport = viewport.applyGesture(
                                viewportWidth = size.width.toFloat(),
                                viewportHeight = size.height.toFloat(),
                                fittedContentWidth = scene.width * fitScale,
                                fittedContentHeight = scene.height * fitScale,
                                centroidX = centroid.x,
                                centroidY = centroid.y,
                                gesturePanX = event.calculatePan().x,
                                gesturePanY = event.calculatePan().y,
                                zoomChange = event.calculateZoom(),
                            )
                            event.changes.forEach { it.consume() }
                        }
                        if (event.changes.none { it.pressed }) {
                            break
                        }
                    }
                }
            },
    ) {
        drawRect(scene.background.toComposeColor())
        if (scene.width <= 0f || scene.height <= 0f) {
            return@Canvas
        }
        val fitScale = min(size.width / scene.width, size.height / scene.height)
        val scale = fitScale * viewport.zoom
        val contentWidth = scene.width * scale
        val contentHeight = scene.height * scale
        val baseOffset = Offset(
            x = (size.width - contentWidth) / 2f,
            y = (size.height - contentHeight) / 2f,
        )

        withTransform({
            translate(baseOffset.x + viewport.panX, baseOffset.y + viewport.panY)
            scale(scale, scale, Offset.Zero)
        }) {
            scene.elements.forEach { element ->
                when (element) {
                    is SceneShape -> drawSceneShape(element)
                    is ScenePath -> drawScenePath(element)
                    is SceneText -> drawSceneText(
                        element = element,
                        textMeasurer = textMeasurer,
                        density = density.density,
                        fontScale = density.fontScale,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSceneShape(shape: SceneShape) {
    val bounds = shape.bounds.toComposeRect()
    val fill = shape.fill.toComposeColor()
    val stroke = shape.stroke.toComposeColor()
    val strokeStyle = Stroke(
        width = shape.strokeWidth,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        pathEffect = shape.strokePattern.toPathEffect(),
    )

    when (shape.kind) {
        SceneShapeKind.Rectangle -> {
            drawRect(fill, bounds.topLeft, bounds.size, style = Fill)
            drawRect(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
        }
        SceneShapeKind.RoundedRectangle -> {
            val radius = CornerRadius(shape.cornerRadius, shape.cornerRadius)
            drawRoundRect(fill, bounds.topLeft, bounds.size, radius, style = Fill)
            drawRoundRect(stroke, bounds.topLeft, bounds.size, radius, style = strokeStyle)
        }
        SceneShapeKind.CollapsedGroup -> {
            val radius = CornerRadius(shape.cornerRadius * 1.5f, shape.cornerRadius * 1.5f)
            drawRoundRect(fill, bounds.topLeft, bounds.size, radius, style = Fill)
            drawRoundRect(stroke, bounds.topLeft, bounds.size, radius, style = strokeStyle)
            val decoration = stroke.copy(alpha = 0.45f)
            val separatorY = bounds.top + bounds.height * 0.62f
            drawLine(
                color = decoration,
                start = Offset(bounds.left + 18f, separatorY),
                end = Offset(bounds.right - 18f, separatorY),
                strokeWidth = shape.strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f)),
            )
            val dotsY = bounds.top + bounds.height * 0.8f
            listOf(-12f, 0f, 12f).forEach { offset ->
                drawCircle(
                    color = decoration,
                    radius = 3.5f,
                    center = Offset(bounds.center.x + offset, dotsY),
                )
            }
        }
        SceneShapeKind.Stadium -> {
            val radius = CornerRadius(bounds.height / 2f, bounds.height / 2f)
            drawRoundRect(fill, bounds.topLeft, bounds.size, radius, style = Fill)
            drawRoundRect(stroke, bounds.topLeft, bounds.size, radius, style = strokeStyle)
        }
        SceneShapeKind.Circle,
        SceneShapeKind.Ellipse,
        SceneShapeKind.SmallCircle,
        -> {
            drawOval(fill, bounds.topLeft, bounds.size, style = Fill)
            drawOval(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
        }
        SceneShapeKind.FilledCircle -> {
            drawOval(stroke, bounds.topLeft, bounds.size, style = Fill)
        }
        SceneShapeKind.DoubleCircle -> {
            drawOval(fill, bounds.topLeft, bounds.size, style = Fill)
            drawOval(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
            val inset = 5f
            drawOval(
                color = stroke,
                topLeft = bounds.topLeft + Offset(inset, inset),
                size = Size(bounds.width - inset * 2f, bounds.height - inset * 2f),
                style = strokeStyle,
            )
        }
        SceneShapeKind.FramedCircle -> {
            drawOval(fill, bounds.topLeft, bounds.size, style = Fill)
            drawOval(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
            drawCircle(stroke, radius = bounds.width / 4f, center = bounds.center, style = strokeStyle)
        }
        SceneShapeKind.CrossedCircle -> {
            drawOval(fill, bounds.topLeft, bounds.size, style = Fill)
            drawOval(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
            val radius = bounds.width.coerceAtMost(bounds.height) * 0.3f
            drawLine(
                stroke,
                bounds.center - Offset(radius, radius),
                bounds.center + Offset(radius, radius),
                shape.strokeWidth,
            )
            drawLine(
                stroke,
                bounds.center + Offset(-radius, radius),
                bounds.center + Offset(radius, -radius),
                shape.strokeWidth,
            )
        }
        SceneShapeKind.Cylinder -> drawCylinder(bounds, fill, stroke, strokeStyle)
        SceneShapeKind.LinedCylinder -> {
            drawCylinder(bounds, fill, stroke, strokeStyle)
            drawOval(
                color = stroke,
                topLeft = Offset(bounds.left, bounds.top + 5f),
                size = Size(bounds.width, 14f.coerceAtMost(bounds.height / 3f)),
                style = strokeStyle,
            )
        }
        SceneShapeKind.DirectAccessStorage -> drawHorizontalCylinder(bounds, fill, stroke, strokeStyle)
        SceneShapeKind.Subroutine -> {
            drawRect(fill, bounds.topLeft, bounds.size, style = Fill)
            drawRect(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
            drawLine(stroke, Offset(bounds.left + 10f, bounds.top), Offset(bounds.left + 10f, bounds.bottom), shape.strokeWidth)
            drawLine(stroke, Offset(bounds.right - 10f, bounds.top), Offset(bounds.right - 10f, bounds.bottom), shape.strokeWidth)
        }
        SceneShapeKind.TextBlock -> Unit
        SceneShapeKind.BraceLeft,
        SceneShapeKind.BraceRight,
        SceneShapeKind.Braces,
        -> drawBraces(shape.kind, bounds, stroke, strokeStyle)
        SceneShapeKind.MultiProcess -> {
            for (offset in listOf(12f, 6f, 0f)) {
                val topLeft = bounds.topLeft + Offset(offset, 12f - offset)
                val size = Size(bounds.width - 12f, bounds.height - 12f)
                drawRect(fill, topLeft, size, style = Fill)
                drawRect(stroke, topLeft, size, style = strokeStyle)
            }
        }
        SceneShapeKind.MultiDocument -> {
            for (offset in listOf(12f, 6f, 0f)) {
                val page = Rect(bounds.left + offset, bounds.top + 12f - offset,
                    bounds.right - 12f + offset, bounds.bottom - offset)
                drawDocument(page, fill, stroke, strokeStyle)
            }
        }
        SceneShapeKind.Document -> drawDocument(bounds, fill, stroke, strokeStyle)
        SceneShapeKind.LinedDocument -> {
            drawDocument(bounds, fill, stroke, strokeStyle)
            drawLine(
                stroke,
                Offset(bounds.left + 10f, bounds.top),
                Offset(bounds.left + 10f, bounds.bottom - 8f),
                shape.strokeWidth,
            )
        }
        SceneShapeKind.TaggedDocument -> {
            drawDocument(bounds, fill, stroke, strokeStyle)
            val lowerEdge = sceneShapeOutline(
                SceneShapeKind.Document,
                SceneRect(bounds.left, bounds.top, bounds.right, bounds.bottom),
            ).drop(2).takeWhile { it.x >= bounds.right - bounds.width * 0.2f }
            val tag = Path().apply {
                moveTo(lowerEdge.last().x, lowerEdge.last().y)
                lineTo(bounds.right, lowerEdge.first().y - bounds.height * 0.2f)
                lowerEdge.forEach { lineTo(it.x, it.y) }
                close()
            }
            drawPath(tag, fill, style = Fill)
            drawPath(tag, stroke, style = strokeStyle)
        }
        SceneShapeKind.LinedRectangle,
        SceneShapeKind.DividedRectangle,
        SceneShapeKind.WindowPane,
        SceneShapeKind.TaggedRectangle,
        -> {
            drawPathShape(shape.kind, bounds, fill, stroke, strokeStyle)
            drawInternalShapeLines(shape.kind, bounds, stroke, shape.strokeWidth)
        }
        else -> {
            drawPathShape(shape.kind, bounds, fill, stroke, strokeStyle)
        }
    }
}

private fun DrawScope.drawPathShape(
    kind: SceneShapeKind,
    bounds: Rect,
    fill: Color,
    stroke: Color,
    strokeStyle: Stroke,
) {
    val path = kind.toPath(bounds)
    drawPath(path, fill, style = Fill)
    drawPath(path, stroke, style = strokeStyle)
}

private fun DrawScope.drawHorizontalCylinder(
    bounds: Rect,
    fill: Color,
    stroke: Color,
    strokeStyle: Stroke,
) {
    val capWidth = min(16f, bounds.width / 4f)
    val body = Path().apply {
        moveTo(bounds.left + capWidth / 2f, bounds.top)
        lineTo(bounds.right - capWidth / 2f, bounds.top)
        cubicTo(bounds.right + capWidth / 2f, bounds.top, bounds.right + capWidth / 2f, bounds.bottom, bounds.right - capWidth / 2f, bounds.bottom)
        lineTo(bounds.left + capWidth / 2f, bounds.bottom)
        cubicTo(bounds.left - capWidth / 2f, bounds.bottom, bounds.left - capWidth / 2f, bounds.top, bounds.left + capWidth / 2f, bounds.top)
        close()
    }
    drawPath(body, fill, style = Fill)
    drawPath(body, stroke, style = strokeStyle)
    drawOval(
        color = stroke,
        topLeft = Offset(bounds.right - capWidth, bounds.top),
        size = Size(capWidth, bounds.height),
        style = strokeStyle,
    )
}

private fun DrawScope.drawDocument(
    bounds: Rect,
    fill: Color,
    stroke: Color,
    strokeStyle: Stroke,
) {
    drawPathShape(SceneShapeKind.Document, bounds, fill, stroke, strokeStyle)
}

private fun DrawScope.drawBraces(
    kind: SceneShapeKind,
    bounds: Rect,
    stroke: Color,
    strokeStyle: Stroke,
) {
    fun brace(x: Float, direction: Float): Path = Path().apply {
        val depth = 9f * direction
        moveTo(x + depth, bounds.top)
        cubicTo(x, bounds.top, x, bounds.top, x, bounds.top + 9f)
        lineTo(x, bounds.center.y - 9f)
        cubicTo(x, bounds.center.y, x, bounds.center.y, x - depth / 2f, bounds.center.y)
        cubicTo(x, bounds.center.y, x, bounds.center.y, x, bounds.center.y + 9f)
        lineTo(x, bounds.bottom - 9f)
        cubicTo(x, bounds.bottom, x, bounds.bottom, x + depth, bounds.bottom)
    }
    if (kind != SceneShapeKind.BraceRight) {
        drawPath(brace(bounds.left + 4f, 1f), stroke, style = strokeStyle)
    }
    if (kind != SceneShapeKind.BraceLeft) {
        drawPath(brace(bounds.right - 4f, -1f), stroke, style = strokeStyle)
    }
}

private fun DrawScope.drawTag(
    bounds: Rect,
    stroke: Color,
    strokeWidth: Float,
) {
    val inset = min(12f, bounds.width / 5f)
    drawLine(
        stroke,
        Offset(bounds.right, bounds.bottom - inset),
        Offset(bounds.right - inset, bounds.bottom),
        strokeWidth,
    )
}

private fun DrawScope.drawInternalShapeLines(
    kind: SceneShapeKind,
    bounds: Rect,
    stroke: Color,
    strokeWidth: Float,
) {
    when (kind) {
        SceneShapeKind.LinedRectangle -> {
            drawLine(stroke, Offset(bounds.left + 10f, bounds.top), Offset(bounds.left + 10f, bounds.bottom), strokeWidth)
        }
        SceneShapeKind.DividedRectangle -> {
            val y = bounds.top + bounds.height / 6f
            drawLine(stroke, Offset(bounds.left, y), Offset(bounds.right, y), strokeWidth)
        }
        SceneShapeKind.WindowPane -> {
            drawLine(stroke, Offset(bounds.left + 13f, bounds.top), Offset(bounds.left + 13f, bounds.bottom), strokeWidth)
            drawLine(stroke, Offset(bounds.left, bounds.top + 13f), Offset(bounds.right, bounds.top + 13f), strokeWidth)
        }
        SceneShapeKind.TaggedRectangle -> drawTag(bounds, stroke, strokeWidth)
        else -> Unit
    }
}

private fun DrawScope.drawCylinder(
    bounds: Rect,
    fill: Color,
    stroke: Color,
    strokeStyle: Stroke,
) {
    val ellipseHeight = min(14f, bounds.height / 3f)
    val body = Path().apply {
        moveTo(bounds.left, bounds.top + ellipseHeight / 2f)
        lineTo(bounds.left, bounds.bottom - ellipseHeight / 2f)
        cubicTo(
            bounds.left,
            bounds.bottom + ellipseHeight / 2f,
            bounds.right,
            bounds.bottom + ellipseHeight / 2f,
            bounds.right,
            bounds.bottom - ellipseHeight / 2f,
        )
        lineTo(bounds.right, bounds.top + ellipseHeight / 2f)
        close()
    }
    drawPath(body, fill, style = Fill)
    drawPath(body, stroke, style = strokeStyle)
    drawOval(
        color = fill,
        topLeft = bounds.topLeft,
        size = Size(bounds.width, ellipseHeight),
        style = Fill,
    )
    drawOval(
        color = stroke,
        topLeft = bounds.topLeft,
        size = Size(bounds.width, ellipseHeight),
        style = strokeStyle,
    )
}

private fun SceneShapeKind.toPath(bounds: Rect): Path = Path().apply {
    val outline = sceneShapeOutline(this@toPath, SceneRect(bounds.left, bounds.top, bounds.right, bounds.bottom))
    outline.forEachIndexed { index, point ->
        if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}

private fun DrawScope.drawScenePath(element: ScenePath) {
    if (element.points.size < 2) {
        return
    }
    val path = Path().apply {
        appendRoundedPolyline(
            points = element.points,
            cornerRadius = element.cornerRadius,
            bridges = element.bridges,
        )
    }
    drawPath(
        path = path,
        color = element.color.toComposeColor(),
        style = Stroke(
            width = element.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = if (element.dashIntervals.size >= 2 && element.dashIntervals.all { it.isFinite() && it > 0f }) {
                PathEffect.dashPathEffect(element.dashIntervals.toFloatArray())
            } else {
                element.strokePattern.toPathEffect()
            },
        ),
    )
    drawArrowHead(
        type = element.arrowStart,
        tip = element.points.first(),
        previous = element.points[1],
        color = element.color.toComposeColor(),
        strokeWidth = element.strokeWidth,
    )
    drawArrowHead(
        type = element.arrowEnd,
        tip = element.points.last(),
        previous = element.points[element.points.lastIndex - 1],
        color = element.color.toComposeColor(),
        strokeWidth = element.strokeWidth,
    )
}

private fun Path.appendRoundedPolyline(
    points: List<ScenePoint>,
    cornerRadius: Float,
    bridges: List<io.github.cmpmermaid.core.SceneBridge>,
) {
    moveTo(points.first().x, points.first().y)
    var segmentStart = points.first()
    points.zipWithNext().forEachIndexed { index, (start, end) ->
        val next = points.getOrNull(index + 2)
        if (next == null || cornerRadius <= 0f) {
            appendSegmentWithBridges(segmentStart, end, bridges)
            segmentStart = end
            return@forEachIndexed
        }

        val radius = min(
            cornerRadius,
            min(start.orthogonalDistanceTo(end), end.orthogonalDistanceTo(next)) / 2f,
        )
        if (radius <= 0f) {
            appendSegmentWithBridges(segmentStart, end, bridges)
            segmentStart = end
            return@forEachIndexed
        }

        val approach = end.moveToward(start, radius)
        val departure = end.moveToward(next, radius)
        appendSegmentWithBridges(segmentStart, approach, bridges)
        cubicTo(
            end.x,
            end.y,
            end.x,
            end.y,
            departure.x,
            departure.y,
        )
        segmentStart = departure
    }
}

private fun ScenePoint.orthogonalDistanceTo(other: ScenePoint): Float =
    abs(other.x - x) + abs(other.y - y)

private fun ScenePoint.moveToward(
    target: ScenePoint,
    distance: Float,
): ScenePoint {
    val total = orthogonalDistanceTo(target)
    if (total <= 0f) {
        return this
    }
    val ratio = (distance / total).coerceIn(0f, 1f)
    return ScenePoint(
        x = x + (target.x - x) * ratio,
        y = y + (target.y - y) * ratio,
    )
}

private fun SceneStrokePattern.toPathEffect(): PathEffect? = when (this) {
    SceneStrokePattern.Solid -> null
    SceneStrokePattern.Dashed -> PathEffect.dashPathEffect(floatArrayOf(3f, 3f))
    SceneStrokePattern.Dotted -> PathEffect.dashPathEffect(floatArrayOf(2f, 2f))
}

private fun Path.appendSegmentWithBridges(
    start: ScenePoint,
    end: ScenePoint,
    bridges: List<io.github.cmpmermaid.core.SceneBridge>,
) {
    val deltaX = end.x - start.x
    val deltaY = end.y - start.y
    val length = sqrt(deltaX * deltaX + deltaY * deltaY)
    if (length <= 0.0001f) {
        lineTo(end.x, end.y)
        return
    }
    val directionX = deltaX / length
    val directionY = deltaY / length

    val segmentBridges = bridges
        .mapNotNull { bridge ->
            val offsetX = bridge.center.x - start.x
            val offsetY = bridge.center.y - start.y
            val distance = offsetX * directionX + offsetY * directionY
            val projectedX = start.x + directionX * distance
            val projectedY = start.y + directionY * distance
            val projectionError = sqrt(
                (projectedX - bridge.center.x) * (projectedX - bridge.center.x) +
                    (projectedY - bridge.center.y) * (projectedY - bridge.center.y),
            )
            bridge.takeIf {
                projectionError < 0.5f &&
                    distance >= bridge.radius &&
                    distance <= length - bridge.radius
            }?.let { it to distance }
        }
        .sortedBy { it.second }

    segmentBridges.forEach { (bridge, _) ->
        val radius = bridge.radius
        var normalX = -directionY
        var normalY = directionX
        if (abs(deltaX) >= abs(deltaY)) {
            if (normalY > 0f) {
                normalX = -normalX
                normalY = -normalY
            }
        } else {
            if (normalX < 0f) {
                normalX = -normalX
                normalY = -normalY
            }
        }
        val beforeX = bridge.center.x - directionX * radius
        val beforeY = bridge.center.y - directionY * radius
        val afterX = bridge.center.x + directionX * radius
        val afterY = bridge.center.y + directionY * radius
        lineTo(beforeX, beforeY)
        cubicTo(
            beforeX + directionX * radius * 0.45f + normalX * radius,
            beforeY + directionY * radius * 0.45f + normalY * radius,
            afterX - directionX * radius * 0.45f + normalX * radius,
            afterY - directionY * radius * 0.45f + normalY * radius,
            afterX,
            afterY,
        )
    }
    lineTo(end.x, end.y)
}

private fun DrawScope.drawArrowHead(
    type: SceneArrowHead,
    tip: ScenePoint,
    previous: ScenePoint,
    color: Color,
    strokeWidth: Float,
) {
    if (type == SceneArrowHead.None) {
        return
    }
    val angle = atan2(tip.y - previous.y, tip.x - previous.x)
    fun insetCenter(radius: Float): Offset = Offset(
        x = tip.x - cos(angle) * radius,
        y = tip.y - sin(angle) * radius,
    )
    when (type) {
        SceneArrowHead.Triangle -> {
            val length = 10f
            val spread = 5f
            val baseX = tip.x - cos(angle) * length
            val baseY = tip.y - sin(angle) * length
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(baseX + cos(angle + PI.toFloat() / 2f) * spread, baseY + sin(angle + PI.toFloat() / 2f) * spread)
                lineTo(baseX + cos(angle - PI.toFloat() / 2f) * spread, baseY + sin(angle - PI.toFloat() / 2f) * spread)
                close()
            }
            drawPath(path, color, style = Fill)
        }
        SceneArrowHead.Circle -> {
            val radius = 5f
            drawCircle(color, radius = radius, center = insetCenter(radius))
        }
        SceneArrowHead.Cross -> {
            val radius = 5f
            val center = insetCenter(radius)
            drawLine(
                color,
                Offset(center.x - radius, center.y - radius),
                Offset(center.x + radius, center.y + radius),
                strokeWidth,
            )
            drawLine(
                color,
                Offset(center.x - radius, center.y + radius),
                Offset(center.x + radius, center.y - radius),
                strokeWidth,
            )
        }
        SceneArrowHead.None -> Unit
    }
}

private fun DrawScope.drawSceneText(
    element: SceneText,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    density: Float,
    fontScale: Float,
) {
    val style = TextStyle(
        color = element.color.toComposeColor(),
        fontSize = normalizedSp(element.fontSize, density, fontScale),
        fontWeight = element.weight.toComposeWeight(),
        textAlign = when (element.horizontalAlignment) {
            SceneTextAlignment.Start -> TextAlign.Start
            SceneTextAlignment.Center -> TextAlign.Center
            SceneTextAlignment.End -> TextAlign.End
        },
    )
    val layout = textMeasurer.measure(
        text = AnnotatedString(element.text),
        style = style,
        softWrap = true,
        maxLines = 8,
        constraints = Constraints(
            maxWidth = element.bounds.width.roundToInt().coerceAtLeast(1),
        ),
    )
    val x = when (element.horizontalAlignment) {
        SceneTextAlignment.Start -> element.bounds.left + 4f
        SceneTextAlignment.Center -> element.bounds.center.x - layout.size.width / 2f
        SceneTextAlignment.End -> element.bounds.right - layout.size.width - 4f
    }
    val y = element.bounds.center.y - layout.size.height / 2f
    drawText(layout, topLeft = Offset(x, y))
}

private fun TextMetricsRequest.toTextStyle(
    density: Float,
    fontScale: Float,
): TextStyle = TextStyle(
    fontSize = normalizedSp(fontSize, density, fontScale),
    fontWeight = weight.toComposeWeight(),
)

private fun normalizedSp(
    sceneUnits: Float,
    density: Float,
    fontScale: Float,
) = (sceneUnits / density / fontScale).sp

private fun SceneTextWeight.toComposeWeight(): FontWeight = when (this) {
    SceneTextWeight.Normal -> FontWeight.Normal
    SceneTextWeight.Medium -> FontWeight.Medium
    SceneTextWeight.Bold -> FontWeight.Bold
}

private fun SceneRect.toComposeRect(): Rect = Rect(left, top, right, bottom)

private fun SceneColor.toComposeColor(): Color = Color(argb.toInt())
