package io.github.cmpmermaid.core

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Shared outline for native drawing and edge intersections. Shape semantics follow
 * Mermaid 12 rendering-elements/shapes; curved boundaries are sampled in scene units.
 */
fun sceneShapeOutline(kind: SceneShapeKind, bounds: SceneRect): List<ScenePoint> {
    val w = bounds.width
    val h = bounds.height
    fun points(vararg coordinates: Pair<Float, Float>) =
        coordinates.map { (x, y) -> ScenePoint(bounds.left + x, bounds.top + y) }
    fun arc(cx: Float, cy: Float, rx: Float, ry: Float, start: Float, end: Float) =
        (0..32).map { index ->
            val angle = (start + (end - start) * index / 32f) * PI.toFloat() / 180f
            ScenePoint(bounds.left + cx + rx * cos(angle), bounds.top + cy + ry * sin(angle))
        }
    val inset = w * 0.12f
    return when (kind) {
        SceneShapeKind.Diamond -> points(w / 2f to 0f, w to h / 2f, w / 2f to h, 0f to h / 2f)
        SceneShapeKind.Hexagon -> {
            val corner = w * 0.16f
            points(corner to 0f, w - corner to 0f, w to h / 2f, w - corner to h, corner to h, 0f to h / 2f)
        }
        SceneShapeKind.Parallelogram -> points(inset to 0f, w to 0f, w - inset to h, 0f to h)
        SceneShapeKind.ParallelogramAlt -> points(0f to 0f, w - inset to 0f, w to h, inset to h)
        SceneShapeKind.Trapezoid -> points(inset to 0f, w - inset to 0f, w to h, 0f to h)
        SceneShapeKind.TrapezoidAlt -> points(0f to 0f, w to 0f, w - inset to h, inset to h)
        SceneShapeKind.Asymmetric -> points(0f to 0f, w to 0f, w to h, 0f to h, h / 4f to h / 2f)
        SceneShapeKind.NotchedRectangle -> {
            val notch = min(12f, w / 6f)
            points(notch to 0f, w to 0f, w to h, 0f to h, 0f to notch)
        }
        SceneShapeKind.Hourglass -> points(0f to 0f, w to 0f, 0f to h, w to h)
        SceneShapeKind.Triangle -> points(w / 2f to 0f, w to h, 0f to h)
        SceneShapeKind.FlippedTriangle -> points(0f to 0f, w to 0f, w / 2f to h)
        SceneShapeKind.SlopedRectangle -> points(0f to h / 3f, w to 0f, w to h, 0f to h)
        SceneShapeKind.NotchedPentagon -> {
            val notch = w * 0.16f
            points(notch to 0f, w - notch to 0f, w to h * 0.35f, w to h, 0f to h, 0f to h * 0.35f)
        }
        SceneShapeKind.Circle, SceneShapeKind.DoubleCircle, SceneShapeKind.Ellipse,
        SceneShapeKind.SmallCircle, SceneShapeKind.FilledCircle, SceneShapeKind.FramedCircle,
        SceneShapeKind.CrossedCircle,
        -> arc(w / 2f, h / 2f, w / 2f, h / 2f, 0f, 360f)
        SceneShapeKind.Stadium, SceneShapeKind.RoundedRectangle, SceneShapeKind.CollapsedGroup -> {
            val r = if (kind == SceneShapeKind.Stadium) h / 2f else min(9f, min(w, h) / 2f)
            arc(w - r, r, r, r, -90f, 0f) +
                arc(w - r, h - r, r, r, 0f, 90f) +
                arc(r, h - r, r, r, 90f, 180f) +
                arc(r, r, r, r, 180f, 270f)
        }
        SceneShapeKind.CurvedTrapezoid ->
            points(h / 4f to 0f) + arc(w - h / 2f, h / 2f, h / 2f, h / 2f, -90f, 90f) +
                points(h / 4f to h, 0f to h / 2f)
        SceneShapeKind.Delay ->
            points(0f to 0f) + arc(w - h / 2f, h / 2f, h / 2f, h / 2f, -90f, 90f) +
                points(0f to h)
        SceneShapeKind.BowTieRectangle -> {
            val r = (h / 2f) / (2.5f + h / 50f)
            arc(r, h / 2f, r, h / 2f, 90f, 270f) +
                arc(w, h / 2f, r, h / 2f, 270f, 90f)
        }
        SceneShapeKind.Cylinder, SceneShapeKind.LinedCylinder -> {
            val r = min(14f, h / 3f) / 2f
            arc(w / 2f, r, w / 2f, r, 180f, 360f) +
                arc(w / 2f, h - r, w / 2f, r, 0f, 180f)
        }
        SceneShapeKind.DirectAccessStorage -> {
            val r = min(16f, w / 4f) / 2f
            arc(w - r, h / 2f, r, h / 2f, -90f, 90f) +
                arc(r, h / 2f, r, h / 2f, 90f, 270f)
        }
        SceneShapeKind.Document, SceneShapeKind.LinedDocument, SceneShapeKind.TaggedDocument -> {
            val wave = min(10f, h / 4f)
            points(0f to 0f, w to 0f) + (0..32).map { i ->
                val t = i / 32f
                val s = 1f - t
                val x = s * s * s * w + 3f * s * s * t * w * 0.75f + 3f * s * t * t * w * 0.25f
                val y = s * s * s * (h - wave) + 3f * s * s * t * (h - wave * 2f) +
                    3f * s * t * t * (h + wave) + t * t * t * (h - wave)
                ScenePoint(bounds.left + x, bounds.top + y)
            }
        }
        SceneShapeKind.PaperTape -> {
            val wave = min(9f, h / 4f)
            (0..32).map { i ->
                val x = w * i / 32f
                ScenePoint(bounds.left + x, bounds.top + wave - wave * sin(2f * PI.toFloat() * i / 32f))
            } + (32 downTo 0).map { i ->
                val x = w * i / 32f
                ScenePoint(bounds.left + x, bounds.bottom - wave - wave * sin(2f * PI.toFloat() * i / 32f))
            }
        }
        SceneShapeKind.Bolt -> points(
            w to 0f, w * 0.45f to h * 0.45f, w to h * 0.45f,
            0f to h, w * 0.55f to h * 0.55f, 0f to h * 0.55f,
        )
        else -> points(0f to 0f, w to 0f, w to h, 0f to h)
    }
}
