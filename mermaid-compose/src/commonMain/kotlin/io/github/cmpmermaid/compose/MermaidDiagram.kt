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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
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
import io.github.cmpmermaid.core.ScenePathCommand
import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapePaint
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextAlignment
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricProvider
import io.github.cmpmermaid.core.TextMetrics
import io.github.cmpmermaid.core.TextMetricsRequest
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt

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
                text = request.text.toAnnotatedString(request.spans),
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
    val geometry = shape.geometry
    if (geometry == null) {
        drawRect(fill, bounds.topLeft, bounds.size, style = Fill)
        drawRect(
            color = stroke,
            topLeft = bounds.topLeft,
            size = bounds.size,
            style = Stroke(
                width = shape.strokeWidth,
                cap = StrokeCap.Butt,
                join = StrokeJoin.Miter,
                pathEffect = shape.dashIntervals.toPathEffect()
                    ?: shape.strokePattern.toPathEffect(),
            ),
        )
        return
    }

    geometry.paths.forEach { primitive ->
        if (primitive.points.isEmpty()) {
            return@forEach
        }
        val path = Path().apply {
            primitive.points.forEachIndexed { index, point ->
                val x = bounds.center.x + point.x
                val y = bounds.center.y + point.y
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
            if (primitive.closed) close()
        }
        val opacity = primitive.opacity.coerceIn(0f, 1f)
        when (primitive.fill) {
            SceneShapePaint.None -> Unit
            SceneShapePaint.Fill -> drawPath(path, fill.copy(alpha = fill.alpha * opacity), style = Fill)
            SceneShapePaint.Stroke -> drawPath(path, stroke.copy(alpha = stroke.alpha * opacity), style = Fill)
        }
        when (primitive.stroke) {
            SceneShapePaint.None -> Unit
            SceneShapePaint.Fill -> drawPath(
                path,
                fill.copy(alpha = fill.alpha * opacity),
                style = primitive.strokeStyle(shape),
            )
            SceneShapePaint.Stroke -> drawPath(
                path,
                stroke.copy(alpha = stroke.alpha * opacity),
                style = primitive.strokeStyle(shape),
            )
        }
    }
}

private fun io.github.cmpmermaid.core.SceneShapePath.strokeStyle(
    shape: SceneShape,
): Stroke = Stroke(
    width = strokeWidth ?: shape.strokeWidth,
    cap = StrokeCap.Butt,
    join = StrokeJoin.Miter,
    pathEffect = when {
        dashIntervals.size >= 2 && dashIntervals.all { it.isFinite() && it > 0f } ->
            PathEffect.dashPathEffect(dashIntervals.toFloatArray())
        strokePattern != SceneStrokePattern.Solid -> strokePattern.toPathEffect()
        shape.dashIntervals.size >= 2 && shape.dashIntervals.all { it.isFinite() && it > 0f } ->
            PathEffect.dashPathEffect(shape.dashIntervals.toFloatArray())
        else -> shape.strokePattern.toPathEffect()
    },
)

private fun List<Float>.toPathEffect(): PathEffect? =
    takeIf { size >= 2 && all { interval -> interval.isFinite() && interval > 0f } }
        ?.let { PathEffect.dashPathEffect(it.toFloatArray()) }

private fun DrawScope.drawScenePath(element: ScenePath) {
    if (element.commands.isEmpty()) {
        return
    }
    val path = Path().apply {
        element.commands.forEach { command ->
            when (command) {
                is ScenePathCommand.MoveTo ->
                    moveTo(command.point.x, command.point.y)
                is ScenePathCommand.LineTo ->
                    lineTo(command.point.x, command.point.y)
                is ScenePathCommand.QuadraticTo ->
                    quadraticTo(
                        command.control.x,
                        command.control.y,
                        command.end.x,
                        command.end.y,
                    )
                is ScenePathCommand.CubicTo ->
                    cubicTo(
                        command.control1.x,
                        command.control1.y,
                        command.control2.x,
                        command.control2.y,
                        command.end.x,
                        command.end.y,
                    )
            }
        }
    }
    val useNeoMarkerMargin = element.look == "neo" && !element.animated
    val visiblePath = if (useNeoMarkerMargin) {
        path.withMermaidNeoMarkerGaps(element)
    } else {
        path
    }
    drawPath(
        path = visiblePath,
        color = element.color.toComposeColor(),
        style = Stroke(
            width = element.strokeWidth,
            cap = if (element.animated) StrokeCap.Round else StrokeCap.Butt,
            join = StrokeJoin.Miter,
            pathEffect = if (
                !useNeoMarkerMargin &&
                element.dashIntervals.size >= 2 &&
                element.dashIntervals.all { it.isFinite() && it > 0f }
            ) {
                PathEffect.dashPathEffect(element.dashIntervals.toFloatArray())
            } else if (!useNeoMarkerMargin) {
                element.strokePattern.toPathEffect()
            } else null,
        ),
    )
    drawArrowHead(
        type = element.arrowStart,
        commands = element.commands,
        position = MarkerPosition.Start,
        useMargin = useNeoMarkerMargin,
        color = element.color.toComposeColor(),
    )
    drawArrowHead(
        type = element.arrowEnd,
        commands = element.commands,
        position = MarkerPosition.End,
        useMargin = useNeoMarkerMargin,
        color = element.color.toComposeColor(),
    )
}

private fun Path.withMermaidNeoMarkerGaps(element: ScenePath): Path {
    val measure = PathMeasure()
    measure.setPath(this, forceClosed = false)
    val length = measure.length
    val startOffset = element.arrowStart.neoMarkerOffset()
    val endOffset = element.arrowEnd.neoMarkerOffset()
    val dashIntervals = if (
        element.strokePattern == SceneStrokePattern.Dashed ||
        element.strokePattern == SceneStrokePattern.Dotted
    ) {
        val middleLength = length - startOffset - endOffset
        val pairCount = (middleLength / 4f).toInt().coerceAtLeast(0)
        buildList {
            add(0f)
            add(startOffset)
            repeat(pairCount) {
                add(2f)
                add(2f)
            }
            add(endOffset)
        }
    } else {
        listOf(0f, startOffset, (length - startOffset - endOffset).coerceAtLeast(0f), endOffset)
    }
    return measure.extractDashPattern(length, dashIntervals)
}

private fun PathMeasure.extractDashPattern(
    pathLength: Float,
    sourceIntervals: List<Float>,
): Path {
    val result = Path()
    if (pathLength <= 0f || sourceIntervals.isEmpty()) {
        return result
    }
    val intervals = if (sourceIntervals.size % 2 == 0) {
        sourceIntervals
    } else {
        sourceIntervals + sourceIntervals
    }
    if (intervals.none { it > 0f }) {
        return result
    }

    var distance = 0f
    var intervalIndex = 0
    var draw = true
    while (distance < pathLength) {
        val interval = intervals[intervalIndex]
        val nextDistance = min(pathLength, distance + interval)
        if (draw && nextDistance > distance) {
            getSegment(distance, nextDistance, result, startWithMoveTo = true)
        }
        distance = nextDistance
        draw = !draw
        intervalIndex = (intervalIndex + 1) % intervals.size
    }
    return result
}

private fun SceneArrowHead.neoMarkerOffset(): Float = when (this) {
    SceneArrowHead.None -> 0f
    SceneArrowHead.Triangle -> 4f
    SceneArrowHead.Circle,
    SceneArrowHead.Cross,
    -> 12.5f
}

private fun SceneStrokePattern.toPathEffect(): PathEffect? = when (this) {
    SceneStrokePattern.Solid -> null
    SceneStrokePattern.Dashed -> PathEffect.dashPathEffect(floatArrayOf(3f, 3f))
    SceneStrokePattern.Dotted -> PathEffect.dashPathEffect(floatArrayOf(2f, 2f))
}

private fun DrawScope.drawArrowHead(
    type: SceneArrowHead,
    commands: List<ScenePathCommand>,
    position: MarkerPosition,
    useMargin: Boolean,
    color: Color,
) {
    if (type == SceneArrowHead.None) {
        return
    }
    val tangent = commands.markerTangent(position) ?: return
    when (type) {
        SceneArrowHead.Triangle -> drawPointMarker(tangent, position, useMargin, color)
        SceneArrowHead.Circle -> drawCircleMarker(tangent, position, useMargin, color)
        SceneArrowHead.Cross -> drawCrossMarker(tangent, position, useMargin, color)
        SceneArrowHead.None -> Unit
    }
}

private fun DrawScope.drawPointMarker(
    tangent: MarkerTangent,
    position: MarkerPosition,
    useMargin: Boolean,
    color: Color,
) {
    val definition = when {
        useMargin && position == MarkerPosition.End -> MarkerPathDefinition(
            points = listOf(
                ScenePoint(0f, 0f),
                ScenePoint(11.5f, 7f),
                ScenePoint(0f, 14f),
            ),
            reference = ScenePoint(11.5f, 7f),
            scale = 10.5f / 11.5f,
            strokeWidth = 0f,
        )
        useMargin -> MarkerPathDefinition(
            points = listOf(
                ScenePoint(0f, 7f),
                ScenePoint(11.5f, 14f),
                ScenePoint(11.5f, 0f),
            ),
            reference = ScenePoint(1f, 7f),
            scale = 1f,
            strokeWidth = 0f,
        )
        position == MarkerPosition.End -> MarkerPathDefinition(
            points = listOf(
                ScenePoint(0f, 0f),
                ScenePoint(10f, 5f),
                ScenePoint(0f, 10f),
            ),
            reference = ScenePoint(5f, 5f),
            scale = 0.8f,
            strokeWidth = 0.8f,
        )
        else -> MarkerPathDefinition(
            points = listOf(
                ScenePoint(0f, 5f),
                ScenePoint(10f, 10f),
                ScenePoint(10f, 0f),
            ),
            reference = ScenePoint(4.5f, 5f),
            scale = 0.8f,
            strokeWidth = 0.8f,
        )
    }
    val path = Path().apply {
        definition.points.forEachIndexed { index, point ->
            val transformed = tangent.transform(
                point = point,
                reference = definition.reference,
                scale = definition.scale,
            )
            if (index == 0) {
                moveTo(transformed.x, transformed.y)
            } else {
                lineTo(transformed.x, transformed.y)
            }
        }
        close()
    }
    drawPath(path, color, style = Fill)
    if (definition.strokeWidth > 0f) {
        drawPath(
            path,
            color,
            style = Stroke(
                width = definition.strokeWidth,
                cap = StrokeCap.Butt,
                join = StrokeJoin.Miter,
            ),
        )
    }
}

private fun DrawScope.drawCircleMarker(
    tangent: MarkerTangent,
    position: MarkerPosition,
    useMargin: Boolean,
    color: Color,
) {
    val referenceX = when {
        useMargin && position == MarkerPosition.End -> 12.25f
        useMargin -> -2f
        position == MarkerPosition.End -> 11f
        else -> -1f
    }
    val scale = if (useMargin) 1.4f else 1.1f
    val center = tangent.transform(
        point = ScenePoint(5f, 5f),
        reference = ScenePoint(referenceX, 5f),
        scale = scale,
    )
    val radius = 5f * scale
    drawCircle(color = color, radius = radius, center = center, style = Fill)
    if (!useMargin) {
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = scale),
        )
    }
}

private fun DrawScope.drawCrossMarker(
    tangent: MarkerTangent,
    position: MarkerPosition,
    useMargin: Boolean,
    color: Color,
) {
    val definition = if (useMargin) {
        MarkerCrossDefinition(
            firstStart = ScenePoint(1f, 1f),
            firstEnd = ScenePoint(14f, 14f),
            secondStart = ScenePoint(1f, 14f),
            secondEnd = ScenePoint(14f, 1f),
            reference = ScenePoint(
                if (position == MarkerPosition.End) 17.7f else -3.5f,
                7.5f,
            ),
            scale = 0.8f,
            strokeWidth = 2f,
        )
    } else {
        MarkerCrossDefinition(
            firstStart = ScenePoint(1f, 1f),
            firstEnd = ScenePoint(10f, 10f),
            secondStart = ScenePoint(10f, 1f),
            secondEnd = ScenePoint(1f, 10f),
            reference = ScenePoint(
                if (position == MarkerPosition.End) 12f else -1f,
                5.2f,
            ),
            scale = 1f,
            strokeWidth = 2f,
        )
    }
    drawLine(
        color = color,
        start = tangent.transform(definition.firstStart, definition.reference, definition.scale),
        end = tangent.transform(definition.firstEnd, definition.reference, definition.scale),
        strokeWidth = definition.strokeWidth,
    )
    drawLine(
        color = color,
        start = tangent.transform(definition.secondStart, definition.reference, definition.scale),
        end = tangent.transform(definition.secondEnd, definition.reference, definition.scale),
        strokeWidth = definition.strokeWidth,
    )
}

private fun List<ScenePathCommand>.markerTangent(position: MarkerPosition): MarkerTangent? {
    var current: ScenePoint? = null
    var start: MarkerTangent? = null
    var end: MarkerTangent? = null
    forEach { command ->
        when (command) {
            is ScenePathCommand.MoveTo -> current = command.point
            is ScenePathCommand.LineTo -> {
                val from = current
                if (from != null) {
                    if (start == null) {
                        start = MarkerTangent.create(from, command.point, anchorAtStart = true)
                    }
                    end = MarkerTangent.create(from, command.point, anchorAtStart = false) ?: end
                }
                current = command.point
            }
            is ScenePathCommand.QuadraticTo -> {
                val from = current
                if (from != null) {
                    val startTangent = MarkerTangent.create(
                        from,
                        command.control,
                        anchorAtStart = true,
                    ) ?: MarkerTangent.create(from, command.end, anchorAtStart = true)
                    val endTangent = MarkerTangent.create(
                        command.control,
                        command.end,
                        anchorAtStart = false,
                    ) ?: MarkerTangent.create(from, command.end, anchorAtStart = false)
                    if (start == null && startTangent != null) start = startTangent
                    if (endTangent != null) end = endTangent
                }
                current = command.end
            }
            is ScenePathCommand.CubicTo -> {
                val from = current
                if (from != null) {
                    val startTangent = MarkerTangent.create(
                        from,
                        command.control1,
                        anchorAtStart = true,
                    ) ?: MarkerTangent.create(from, command.end, anchorAtStart = true)
                    val endTangent = MarkerTangent.create(
                        command.control2,
                        command.end,
                        anchorAtStart = false,
                    ) ?: MarkerTangent.create(from, command.end, anchorAtStart = false)
                    if (start == null && startTangent != null) start = startTangent
                    if (endTangent != null) end = endTangent
                }
                current = command.end
            }
        }
    }
    return if (position == MarkerPosition.Start) start else end
}

private enum class MarkerPosition {
    Start,
    End,
}

private data class MarkerTangent(
    val anchor: ScenePoint,
    val unitX: Float,
    val unitY: Float,
) {
    fun transform(
        point: ScenePoint,
        reference: ScenePoint,
        scale: Float,
    ): Offset {
        val localX = (point.x - reference.x) * scale
        val localY = (point.y - reference.y) * scale
        return Offset(
            x = anchor.x + unitX * localX - unitY * localY,
            y = anchor.y + unitY * localX + unitX * localY,
        )
    }

    companion object {
        fun create(
            from: ScenePoint,
            to: ScenePoint,
            anchorAtStart: Boolean,
        ): MarkerTangent? {
            val deltaX = to.x - from.x
            val deltaY = to.y - from.y
            val length = hypot(deltaX, deltaY)
            if (length <= 0.0001f) {
                return null
            }
            return MarkerTangent(
                anchor = if (anchorAtStart) from else to,
                unitX = deltaX / length,
                unitY = deltaY / length,
            )
        }
    }
}

private data class MarkerPathDefinition(
    val points: List<ScenePoint>,
    val reference: ScenePoint,
    val scale: Float,
    val strokeWidth: Float,
)

private data class MarkerCrossDefinition(
    val firstStart: ScenePoint,
    val firstEnd: ScenePoint,
    val secondStart: ScenePoint,
    val secondEnd: ScenePoint,
    val reference: ScenePoint,
    val scale: Float,
    val strokeWidth: Float,
)

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
        text = element.text.toAnnotatedString(element.spans),
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

private fun String.toAnnotatedString(
    spans: List<io.github.cmpmermaid.core.SceneTextSpan>,
): AnnotatedString = buildAnnotatedString {
    append(this@toAnnotatedString)
    spans.forEach { span ->
        if (span.start < 0 || span.end > length || span.start >= span.end) {
            return@forEach
        }
        addStyle(
            style = SpanStyle(
                fontWeight = span.weight?.toComposeWeight(),
                fontStyle = if (span.italic) FontStyle.Italic else null,
            ),
            start = span.start,
            end = span.end,
        )
    }
}

private fun SceneRect.toComposeRect(): Rect = Rect(left, top, right, bottom)

private fun SceneColor.toComposeColor(): Color = Color(argb.toInt())
