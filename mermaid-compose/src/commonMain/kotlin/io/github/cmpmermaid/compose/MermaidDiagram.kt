package io.github.cmpmermaid.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    var zoom by remember(scene) { mutableFloatStateOf(1f) }
    var pan by remember(scene) { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .pointerInput(scene) {
                detectTransformGestures { _, panChange, zoomChange, _ ->
                    zoom = (zoom * zoomChange).coerceIn(1f, 5f)
                    pan += panChange
                }
            },
    ) {
        drawRect(scene.background.toComposeColor())
        if (scene.width <= 0f || scene.height <= 0f) {
            return@Canvas
        }
        val fitScale = min(size.width / scene.width, size.height / scene.height)
        val scale = fitScale * zoom
        val baseOffset = Offset(
            x = (size.width - scene.width * fitScale) / 2f,
            y = (size.height - scene.height * fitScale) / 2f,
        )

        withTransform({
            translate(baseOffset.x + pan.x, baseOffset.y + pan.y)
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
        SceneShapeKind.Stadium -> {
            val radius = CornerRadius(bounds.height / 2f, bounds.height / 2f)
            drawRoundRect(fill, bounds.topLeft, bounds.size, radius, style = Fill)
            drawRoundRect(stroke, bounds.topLeft, bounds.size, radius, style = strokeStyle)
        }
        SceneShapeKind.Circle -> {
            drawOval(fill, bounds.topLeft, bounds.size, style = Fill)
            drawOval(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
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
        SceneShapeKind.Cylinder -> drawCylinder(bounds, fill, stroke, strokeStyle)
        SceneShapeKind.Subroutine -> {
            drawRect(fill, bounds.topLeft, bounds.size, style = Fill)
            drawRect(stroke, bounds.topLeft, bounds.size, style = strokeStyle)
            drawLine(stroke, Offset(bounds.left + 10f, bounds.top), Offset(bounds.left + 10f, bounds.bottom), shape.strokeWidth)
            drawLine(stroke, Offset(bounds.right - 10f, bounds.top), Offset(bounds.right - 10f, bounds.bottom), shape.strokeWidth)
        }
        else -> {
            val path = shape.kind.toPath(bounds)
            drawPath(path, fill, style = Fill)
            drawPath(path, stroke, style = strokeStyle)
        }
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
        moveTo(element.points.first().x, element.points.first().y)
        element.points.drop(1).forEach { point -> lineTo(point.x, point.y) }
    }
    val pathEffect = when (element.strokePattern) {
        SceneStrokePattern.Solid -> null
        SceneStrokePattern.Dashed -> PathEffect.dashPathEffect(floatArrayOf(9f, 6f))
        SceneStrokePattern.Dotted -> PathEffect.dashPathEffect(floatArrayOf(2f, 6f))
    }
    drawPath(
        path = path,
        color = element.color.toComposeColor(),
        style = Stroke(
            width = element.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = pathEffect,
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
    val tipOffset = Offset(tip.x, tip.y)
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
        SceneArrowHead.Circle -> drawCircle(color, radius = 4.5f, center = tipOffset)
        SceneArrowHead.Cross -> {
            val radius = 5f
            drawLine(
                color,
                Offset(tip.x - radius, tip.y - radius),
                Offset(tip.x + radius, tip.y + radius),
                strokeWidth,
            )
            drawLine(
                color,
                Offset(tip.x - radius, tip.y + radius),
                Offset(tip.x + radius, tip.y - radius),
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
