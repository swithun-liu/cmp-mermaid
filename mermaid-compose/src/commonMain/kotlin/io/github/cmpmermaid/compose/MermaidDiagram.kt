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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

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
            drawCircle(stroke, radius = 4f, center = bounds.center)
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
            val offset = 6f
            drawRect(
                color = fill,
                topLeft = bounds.topLeft + Offset(offset, -offset),
                size = bounds.size,
                style = Fill,
            )
            drawRect(
                color = stroke,
                topLeft = bounds.topLeft + Offset(offset, -offset),
                size = bounds.size,
                style = strokeStyle,
            )
            drawPathShape(shape.kind, bounds, fill, stroke, strokeStyle)
        }
        SceneShapeKind.MultiDocument -> {
            drawDocument(bounds.shifted(6f, -8f), fill, stroke, strokeStyle)
            drawDocument(bounds.shifted(3f, -4f), fill, stroke, strokeStyle)
            drawDocument(bounds, fill, stroke, strokeStyle)
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
            drawLine(
                stroke,
                Offset(bounds.right - 10f, bounds.top),
                Offset(bounds.right - 10f, bounds.bottom - 8f),
                shape.strokeWidth,
            )
        }
        SceneShapeKind.TaggedDocument -> {
            drawDocument(bounds, fill, stroke, strokeStyle)
            drawTag(bounds, stroke, shape.strokeWidth)
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

private fun Rect.shifted(dx: Float, dy: Float): Rect = Rect(
    left = left + dx,
    top = top + dy,
    right = right + dx,
    bottom = bottom + dy,
)

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
        topLeft = bounds.topLeft,
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
    val wave = min(10f, bounds.height / 4f)
    val path = Path().apply {
        moveTo(bounds.left, bounds.top)
        lineTo(bounds.right, bounds.top)
        lineTo(bounds.right, bounds.bottom - wave)
        cubicTo(
            bounds.right - bounds.width * 0.25f,
            bounds.bottom - wave * 2f,
            bounds.left + bounds.width * 0.25f,
            bounds.bottom + wave,
            bounds.left,
            bounds.bottom - wave,
        )
        close()
    }
    drawPath(path, fill, style = Fill)
    drawPath(path, stroke, style = strokeStyle)
}

private fun DrawScope.drawBraces(
    kind: SceneShapeKind,
    bounds: Rect,
    stroke: Color,
    strokeStyle: Stroke,
) {
    fun brace(x: Float, direction: Float): Path = Path().apply {
        val depth = 9f * direction
        moveTo(x, bounds.top)
        cubicTo(x + depth, bounds.top, x + depth, bounds.center.y - 6f, x, bounds.center.y)
        cubicTo(x + depth, bounds.center.y + 6f, x + depth, bounds.bottom, x, bounds.bottom)
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
        Offset(bounds.right - inset, bounds.top),
        Offset(bounds.right - inset, bounds.bottom - 4f),
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
            drawLine(stroke, Offset(bounds.right - 10f, bounds.top), Offset(bounds.right - 10f, bounds.bottom), strokeWidth)
        }
        SceneShapeKind.DividedRectangle -> {
            drawLine(stroke, Offset(bounds.left, bounds.center.y), Offset(bounds.right, bounds.center.y), strokeWidth)
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
    when (this@toPath) {
        SceneShapeKind.Diamond -> {
            moveTo(bounds.center.x, bounds.top)
            lineTo(bounds.right, bounds.center.y)
            lineTo(bounds.center.x, bounds.bottom)
            lineTo(bounds.left, bounds.center.y)
        }
        SceneShapeKind.Hexagon -> {
            val inset = bounds.width * 0.16f
            moveTo(bounds.left + inset, bounds.top)
            lineTo(bounds.right - inset, bounds.top)
            lineTo(bounds.right, bounds.center.y)
            lineTo(bounds.right - inset, bounds.bottom)
            lineTo(bounds.left + inset, bounds.bottom)
            lineTo(bounds.left, bounds.center.y)
        }
        SceneShapeKind.Parallelogram -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left + inset, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        SceneShapeKind.ParallelogramAlt -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right - inset, bounds.top)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left + inset, bounds.bottom)
        }
        SceneShapeKind.Trapezoid -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left + inset, bounds.top)
            lineTo(bounds.right - inset, bounds.top)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        SceneShapeKind.TrapezoidAlt -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.bottom)
            lineTo(bounds.left + inset, bounds.bottom)
        }
        SceneShapeKind.Asymmetric -> {
            val inset = bounds.width * 0.14f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.center.y)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        SceneShapeKind.NotchedRectangle -> {
            val notch = min(12f, bounds.width / 6f)
            moveTo(bounds.left + notch, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
            lineTo(bounds.left, bounds.top + notch)
        }
        SceneShapeKind.Hourglass -> {
            val inset = bounds.width * 0.18f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.center.y)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
            lineTo(bounds.left + inset, bounds.center.y)
        }
        SceneShapeKind.Bolt -> {
            moveTo(bounds.center.x + bounds.width * 0.08f, bounds.top)
            lineTo(bounds.left + bounds.width * 0.24f, bounds.center.y + bounds.height * 0.05f)
            lineTo(bounds.center.x - bounds.width * 0.03f, bounds.center.y + bounds.height * 0.05f)
            lineTo(bounds.center.x - bounds.width * 0.12f, bounds.bottom)
            lineTo(bounds.right - bounds.width * 0.2f, bounds.center.y - bounds.height * 0.08f)
            lineTo(bounds.center.x + bounds.width * 0.05f, bounds.center.y - bounds.height * 0.08f)
        }
        SceneShapeKind.Delay -> {
            val radius = bounds.height / 2f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right - radius, bounds.top)
            cubicTo(bounds.right, bounds.top, bounds.right, bounds.bottom, bounds.right - radius, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        SceneShapeKind.CurvedTrapezoid -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left + inset, bounds.top)
            lineTo(bounds.right - inset, bounds.top)
            cubicTo(bounds.right + inset, bounds.top, bounds.right + inset, bounds.bottom, bounds.right - inset, bounds.bottom)
            lineTo(bounds.left + inset, bounds.bottom)
            cubicTo(bounds.left - inset, bounds.bottom, bounds.left - inset, bounds.top, bounds.left + inset, bounds.top)
        }
        SceneShapeKind.Triangle -> {
            moveTo(bounds.center.x, bounds.top)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        SceneShapeKind.FlippedTriangle -> {
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.center.x, bounds.bottom)
        }
        SceneShapeKind.SlopedRectangle -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.bottom)
            lineTo(bounds.left + inset, bounds.bottom)
        }
        SceneShapeKind.NotchedPentagon -> {
            val notch = bounds.width * 0.16f
            moveTo(bounds.left + notch, bounds.top)
            lineTo(bounds.right - notch, bounds.top)
            lineTo(bounds.right, bounds.top + bounds.height * 0.35f)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
            lineTo(bounds.left, bounds.top + bounds.height * 0.35f)
        }
        SceneShapeKind.PaperTape -> {
            val wave = min(9f, bounds.height / 4f)
            moveTo(bounds.left, bounds.top + wave)
            cubicTo(
                bounds.left + bounds.width * 0.25f,
                bounds.top - wave,
                bounds.right - bounds.width * 0.25f,
                bounds.top + wave * 2f,
                bounds.right,
                bounds.top + wave,
            )
            lineTo(bounds.right, bounds.bottom - wave)
            cubicTo(
                bounds.right - bounds.width * 0.25f,
                bounds.bottom + wave,
                bounds.left + bounds.width * 0.25f,
                bounds.bottom - wave * 2f,
                bounds.left,
                bounds.bottom - wave,
            )
        }
        SceneShapeKind.BowTieRectangle -> {
            val inset = bounds.width * 0.12f
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right - inset, bounds.center.y)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
            lineTo(bounds.left + inset, bounds.center.y)
        }
        SceneShapeKind.TaggedRectangle,
        SceneShapeKind.TaggedDocument,
        -> {
            val tag = min(14f, bounds.width / 5f)
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right - tag, bounds.top)
            lineTo(bounds.right, bounds.center.y)
            lineTo(bounds.right - tag, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
        else -> {
            moveTo(bounds.left, bounds.top)
            lineTo(bounds.right, bounds.top)
            lineTo(bounds.right, bounds.bottom)
            lineTo(bounds.left, bounds.bottom)
        }
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
            pathEffect = element.strokePattern.toPathEffect(),
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
    val horizontal = abs(end.y - start.y) < 0.01f
    val vertical = abs(end.x - start.x) < 0.01f
    if (!horizontal && !vertical) {
        lineTo(end.x, end.y)
        return
    }

    val segmentBridges = bridges
        .filter { bridge ->
            if (horizontal) {
                abs(bridge.center.y - start.y) < 0.5f &&
                    bridge.center.x.isBetween(start.x, end.x, bridge.radius)
            } else {
                abs(bridge.center.x - start.x) < 0.5f &&
                    bridge.center.y.isBetween(start.y, end.y, bridge.radius)
            }
        }
        .sortedBy { bridge ->
            if (horizontal) {
                abs(bridge.center.x - start.x)
            } else {
                abs(bridge.center.y - start.y)
            }
        }

    segmentBridges.forEach { bridge ->
        if (horizontal) {
            val direction = if (end.x >= start.x) 1f else -1f
            val before = bridge.center.x - bridge.radius * direction
            val after = bridge.center.x + bridge.radius * direction
            lineTo(before, start.y)
            cubicTo(
                bridge.center.x - bridge.radius * 0.45f * direction,
                start.y - bridge.radius,
                bridge.center.x + bridge.radius * 0.45f * direction,
                start.y - bridge.radius,
                after,
                start.y,
            )
        } else {
            val direction = if (end.y >= start.y) 1f else -1f
            val before = bridge.center.y - bridge.radius * direction
            val after = bridge.center.y + bridge.radius * direction
            lineTo(start.x, before)
            cubicTo(
                start.x + bridge.radius,
                bridge.center.y - bridge.radius * 0.45f * direction,
                start.x + bridge.radius,
                bridge.center.y + bridge.radius * 0.45f * direction,
                start.x,
                after,
            )
        }
    }
    lineTo(end.x, end.y)
}

private fun Float.isBetween(
    first: Float,
    second: Float,
    clearance: Float,
): Boolean = this >= min(first, second) + clearance &&
    this <= maxOf(first, second) - clearance

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
    )
    val layout = textMeasurer.measure(
        text = AnnotatedString(element.text),
        style = style,
        softWrap = true,
        maxLines = 8,
        constraints = Constraints(
            maxWidth = (element.bounds.width - 8f).roundToInt().coerceAtLeast(1),
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
