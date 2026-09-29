package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidCompatibility
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.SceneShapePath
import com.swithun.cmpmermaid.core.SceneShapeViewportFit
import kotlin.math.min

internal const val MERMAID_ERROR_TITLE = "Syntax error in text"
internal const val MERMAID_ERROR_VERSION =
    "mermaid version ${MermaidCompatibility.BASELINE_VERSION}"

/**
 * Mermaid.js 12.0.0:
 * packages/mermaid/src/diagrams/error/errorRenderer.ts -> draw.
 */
@Composable
internal fun MermaidErrorDiagram(
    modifier: Modifier,
    theme: MermaidTheme,
    fontFamilyResolver: MermaidFontFamilyResolver?,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val effectiveFontFamilyResolver = rememberMermaidFontFamilyResolver(fontFamilyResolver)
    val fontFamily = effectiveFontFamilyResolver.resolve(theme.fontFamily)
        ?: FontFamily.Default
    val errorTextColor = Color(theme.errorText.argb.toInt())
    val errorBackgroundColor = Color(theme.errorBackground.argb.toInt())
    val titleStyle = TextStyle(
        color = errorTextColor,
        fontSize = (
            ERROR_TITLE_FONT_SIZE / density.density / density.fontScale
            ).sp,
        fontFamily = fontFamily,
    )
    val versionStyle = TextStyle(
        color = errorTextColor,
        fontSize = (
            ERROR_VERSION_FONT_SIZE / density.density / density.fontScale
            ).sp,
        fontFamily = fontFamily,
    )
    val titleLayout = remember(textMeasurer, titleStyle) {
        textMeasurer.measure(
            text = AnnotatedString(MERMAID_ERROR_TITLE),
            style = titleStyle,
            softWrap = false,
        )
    }
    val versionLayout = remember(textMeasurer, versionStyle) {
        textMeasurer.measure(
            text = AnnotatedString(MERMAID_ERROR_VERSION),
            style = versionStyle,
            softWrap = false,
        )
    }
    val iconPaths = remember { mermaidErrorIconPaths() }
    Canvas(
        modifier = modifier.semantics {
            contentDescription = "$MERMAID_ERROR_TITLE. $MERMAID_ERROR_VERSION"
        },
    ) {
        val scale = min(
            size.width / ERROR_NORMALIZED_WIDTH,
            size.height / ERROR_NORMALIZED_HEIGHT,
        )
        val left = (size.width - ERROR_NORMALIZED_WIDTH * scale) / 2f +
            ERROR_VIEWBOX_PADDING * scale
        val top = (size.height - ERROR_NORMALIZED_HEIGHT * scale) / 2f +
            ERROR_VIEWBOX_PADDING * scale
        withTransform({
            translate(left = left, top = top)
            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
        }) {
            iconPaths.forEach { path ->
                drawPath(
                    path = path,
                    color = errorBackgroundColor,
                    style = Fill,
                )
            }
            drawErrorText(
                layout = titleLayout,
                centerX = ERROR_TITLE_X,
                baselineY = ERROR_TITLE_Y,
                color = errorTextColor,
            )
            drawErrorText(
                layout = versionLayout,
                centerX = ERROR_VERSION_X,
                baselineY = ERROR_VERSION_Y,
                color = errorTextColor,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawErrorText(
    layout: androidx.compose.ui.text.TextLayoutResult,
    centerX: Float,
    baselineY: Float,
    color: Color,
) {
    val topLeft = Offset(
        x = centerX - layout.size.width / 2f,
        y = baselineY - layout.firstBaseline,
    )
    drawText(
        textLayoutResult = layout,
        color = color,
        topLeft = topLeft,
        drawStyle = Stroke(width = 1f),
    )
    drawText(
        textLayoutResult = layout,
        color = color,
        topLeft = topLeft,
    )
}

internal fun mermaidErrorIconPaths(): List<Path> {
    val sourceBounds = Rect(
        left = 0f,
        top = 0f,
        right = ERROR_VIEWBOX_WIDTH,
        bottom = ERROR_VIEWBOX_HEIGHT,
    )
    return ERROR_ICON_PATH_DATA.mapNotNull { pathData ->
        when (
            val parsed = SceneShapePath(pathData = pathData).toComposePath(
                bounds = sourceBounds,
                viewBox = sourceBounds,
                viewportFit = SceneShapeViewportFit.Stretch,
            )
        ) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> null
        }
    }
}

internal const val ERROR_VIEWBOX_WIDTH = 2412f
internal const val ERROR_VIEWBOX_HEIGHT = 512f
internal const val ERROR_VIEWBOX_PADDING = 12f
private const val ERROR_NORMALIZED_WIDTH = ERROR_VIEWBOX_WIDTH + ERROR_VIEWBOX_PADDING * 2f
private const val ERROR_NORMALIZED_HEIGHT = ERROR_VIEWBOX_HEIGHT + ERROR_VIEWBOX_PADDING * 2f
internal const val ERROR_TITLE_X = 1440f
internal const val ERROR_TITLE_Y = 250f
internal const val ERROR_VERSION_X = 1250f
internal const val ERROR_VERSION_Y = 400f
internal const val ERROR_TITLE_FONT_SIZE = 150f
internal const val ERROR_VERSION_FONT_SIZE = 100f

internal val ERROR_ICON_PATH_DATA = listOf(
    "m411.313,123.313c6.25-6.25 6.25-16.375 0-22.625s-16.375-6.25-22.625,0" +
        "l-32,32-9.375,9.375-20.688-20.688c-12.484-12.5-32.766-12.5-45.25,0l-16,16" +
        "c-1.261,1.261-2.304,2.648-3.31,4.051-21.739-8.561-45.324-13.426-70.065-13.426" +
        "-105.867,0-192,86.133-192,192s86.133,192 192,192 192-86.133 192-192" +
        "c0-24.741-4.864-48.327-13.426-70.065 1.402-1.007 2.79-2.049 4.051-3.31l16-16" +
        "c12.5-12.492 12.5-32.758 0-45.25l-20.688-20.688 9.375-9.375 32.001-31.999z" +
        "m-219.313,100.687c-52.938,0-96,43.063-96,96 0,8.836-7.164,16-16,16" +
        "s-16-7.164-16-16c0-70.578 57.422-128 128-128 8.836,0 16,7.164 16,16" +
        "s-7.164,16-16,16z",
    "m459.02,148.98c-6.25-6.25-16.375-6.25-22.625,0s-6.25,16.375 0,22.625l16,16" +
        "c3.125,3.125 7.219,4.688 11.313,4.688 4.094,0 8.188-1.563 11.313-4.688" +
        " 6.25-6.25 6.25-16.375 0-22.625l-16.001-16z",
    "m340.395,75.605c3.125,3.125 7.219,4.688 11.313,4.688 4.094,0 8.188-1.563" +
        " 11.313-4.688 6.25-6.25 6.25-16.375 0-22.625l-16-16c-6.25-6.25-16.375-6.25" +
        "-22.625,0s-6.25,16.375 0,22.625l15.999,16z",
    "m400,64c8.844,0 16-7.164 16-16v-32c0-8.836-7.156-16-16-16-8.844,0-16,7.164" +
        "-16,16v32c0,8.836 7.156,16 16,16z",
    "m496,96.586h-32c-8.844,0-16,7.164-16,16 0,8.836 7.156,16 16,16h32" +
        "c8.844,0 16-7.164 16-16 0-8.836-7.156-16-16-16z",
    "m436.98,75.605c3.125,3.125 7.219,4.688 11.313,4.688 4.094,0 8.188-1.563" +
        " 11.313-4.688l32-32c6.25-6.25 6.25-16.375 0-22.625s-16.375-6.25-22.625,0" +
        "l-32,32c-6.251,6.25-6.251,16.375-0.001,22.625z",
)
