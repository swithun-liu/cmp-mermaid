package io.github.cmpmermaid.core.flowchart.upstream.mermaid

import io.github.cmpmermaid.core.SceneArrowHead

/**
 * Kotlin translation of Mermaid 12.0.0's marker offset tables in
 * rendering-util/rendering-elements/markers.js and utils/lineWithOffset.ts.
 */
internal object MermaidMarkerPort {
    fun pathOffset(arrow: SceneArrowHead): Float = when (arrow) {
        SceneArrowHead.None -> 0f
        SceneArrowHead.Triangle -> 4f
        SceneArrowHead.Circle,
        SceneArrowHead.Cross,
        SceneArrowHead.Open,
        SceneArrowHead.Async,
        SceneArrowHead.SequenceCross,
        SceneArrowHead.HalfTriangleTop,
        SceneArrowHead.HalfTriangleBottom,
        SceneArrowHead.HalfOpenTop,
        SceneArrowHead.HalfOpenBottom,
        -> 0f
    }

    fun neoGapOffset(arrow: SceneArrowHead): Float = when (arrow) {
        SceneArrowHead.None -> 0f
        SceneArrowHead.Triangle -> 4f
        SceneArrowHead.Circle,
        SceneArrowHead.Cross,
        -> 12.5f
        SceneArrowHead.Open,
        SceneArrowHead.Async,
        SceneArrowHead.SequenceCross,
        SceneArrowHead.HalfTriangleTop,
        SceneArrowHead.HalfTriangleBottom,
        SceneArrowHead.HalfOpenTop,
        SceneArrowHead.HalfOpenBottom,
        -> 0f
    }

    fun terminalSegmentOffset(arrow: SceneArrowHead): Float =
        maxOf(pathOffset(arrow), neoGapOffset(arrow))
}
