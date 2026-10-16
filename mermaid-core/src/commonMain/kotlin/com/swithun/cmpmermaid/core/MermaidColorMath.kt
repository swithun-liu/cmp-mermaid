package com.swithun.cmpmermaid.core

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Mermaid: packages/mermaid/src/themes/theme-*.js -> updateColors, via Khroma operations.
 */
internal fun SceneColor.mermaidDarken(amount: Double): SceneColor =
    mermaidAdjustSaturationAndLightness(lightnessAmount = -amount)

internal fun SceneColor.mermaidLighten(amount: Double): SceneColor =
    mermaidAdjustSaturationAndLightness(lightnessAmount = amount)

internal fun SceneColor.mermaidAdjustSaturationAndLightness(
    saturationAmount: Double = 0.0,
    lightnessAmount: Double = 0.0,
): SceneColor = mermaidAdjustColor(
    saturationAmount = saturationAmount,
    lightnessAmount = lightnessAmount,
)

internal fun SceneColor.mermaidInvert(): SceneColor = SceneColor(
    argb = (
        (alpha.toLong() shl 24) or
            ((255 - red).toLong() shl 16) or
            ((255 - green).toLong() shl 8) or
            (255 - blue).toLong()
        ),
)

private fun SceneColor.mermaidAdjustColor(
    saturationAmount: Double,
    lightnessAmount: Double,
): SceneColor {
    val redUnit = red / 255.0
    val greenUnit = green / 255.0
    val blueUnit = blue / 255.0
    val maximum = max(redUnit, max(greenUnit, blueUnit))
    val minimum = min(redUnit, min(greenUnit, blueUnit))
    val lightness = (maximum + minimum) / 2.0
    val delta = maximum - minimum
    val saturation = when {
        delta == 0.0 -> 0.0
        lightness > 0.5 -> delta / (2.0 - maximum - minimum)
        else -> delta / (maximum + minimum)
    }
    val hue = when {
        delta == 0.0 -> 0.0
        maximum == redUnit ->
            ((greenUnit - blueUnit) / delta + if (greenUnit < blueUnit) 6.0 else 0.0) / 6.0
        maximum == greenUnit -> ((blueUnit - redUnit) / delta + 2.0) / 6.0
        else -> ((redUnit - greenUnit) / delta + 4.0) / 6.0
    }
    // Khroma adjustChannel() returns an HSL string through Color.stringify().
    // lang.round() serializes each HSL channel to 10 decimal places before the
    // browser converts that CSS color back to RGB.
    val adjustedHue = (hue * 360.0).khromaRound() / 360.0
    val adjustedSaturation = (saturation * 100.0 + saturationAmount)
        .coerceIn(0.0, 100.0).khromaRound() / 100.0
    val adjustedLightness = (lightness * 100.0 + lightnessAmount)
        .coerceIn(0.0, 100.0).khromaRound() / 100.0
    if (adjustedSaturation == 0.0) {
        val channel = (adjustedLightness * 255.0).roundToInt()
        return color(channel, channel, channel)
    }
    val upper = if (adjustedLightness < 0.5) {
        adjustedLightness * (1.0 + adjustedSaturation)
    } else {
        adjustedLightness + adjustedSaturation - adjustedLightness * adjustedSaturation
    }
    val lower = 2.0 * adjustedLightness - upper
    return color(
        red = (hueToRgb(lower, upper, adjustedHue + 1.0 / 3.0) * 255.0).roundToInt(),
        green = (hueToRgb(lower, upper, adjustedHue) * 255.0).roundToInt(),
        blue = (hueToRgb(lower, upper, adjustedHue - 1.0 / 3.0) * 255.0).roundToInt(),
    )
}

private fun Double.khromaRound(): Double =
    floor(this * KHROMA_ROUNDING_FACTOR + 0.5) / KHROMA_ROUNDING_FACTOR

private fun hueToRgb(
    lower: Double,
    upper: Double,
    sourceHue: Double,
): Double {
    val hue = when {
        sourceHue < 0.0 -> sourceHue + 1.0
        sourceHue > 1.0 -> sourceHue - 1.0
        else -> sourceHue
    }
    return when {
        hue < 1.0 / 6.0 -> lower + (upper - lower) * 6.0 * hue
        hue < 1.0 / 2.0 -> upper
        hue < 2.0 / 3.0 -> lower + (upper - lower) * (2.0 / 3.0 - hue) * 6.0
        else -> lower
    }
}

private fun SceneColor.color(
    red: Int,
    green: Int,
    blue: Int,
): SceneColor = SceneColor(
    argb = (
        (alpha.toLong() shl 24) or
            (red.coerceIn(0, 255).toLong() shl 16) or
            (green.coerceIn(0, 255).toLong() shl 8) or
            blue.coerceIn(0, 255).toLong()
        ),
)

private val SceneColor.alpha: Int get() = ((argb shr 24) and 0xFF).toInt()
private val SceneColor.red: Int get() = ((argb shr 16) and 0xFF).toInt()
private val SceneColor.green: Int get() = ((argb shr 8) and 0xFF).toInt()
private val SceneColor.blue: Int get() = (argb and 0xFF).toInt()

private const val KHROMA_ROUNDING_FACTOR = 10_000_000_000.0
