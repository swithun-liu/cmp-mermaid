package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.generated.swimlaneDdltCorpusCases
import kotlin.math.ceil
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SwimlaneDdltCorpusTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            val charactersPerLine = (request.maxWidth / (request.fontSize * 0.55f))
                .toInt()
                .coerceAtLeast(1)
            val lineCount = request.text.lineSequence().sumOf { line ->
                ceil(line.length.toDouble() / charactersPerLine)
                    .toInt()
                    .coerceAtLeast(1)
            }
            TextMetrics(
                width = min(
                    request.maxWidth,
                    request.text.lineSequence()
                        .maxOfOrNull(String::length)
                        .orZero() * request.fontSize * 0.55f,
                ),
                height = lineCount * request.fontSize * request.lineHeight,
                lineCount = lineCount,
            )
        },
    )

    @Test
    fun rendersEveryMermaidTwelveSwimlaneDdltSource() {
        assertEquals(14, swimlaneDdltCorpusCases.size)
        var structuredUnsupportedCount = 0
        val failures = swimlaneDdltCorpusCases.mapNotNull { case ->
            when (val result = engine.render(case.source, context)) {
                is GMResult.Err -> {
                    val error = result.error
                    if (
                        case.id == "ddlt_swimlanes_16_intake_cycle_lr" &&
                        error is MermaidError.UnsupportedFeature &&
                        error.feature == "handDrawn look"
                    ) {
                        structuredUnsupportedCount++
                        null
                    } else {
                        "${case.id}: ${error.message}"
                    }
                }
                is GMResult.Ok -> validate(case.id, result.value)
            }
        }

        assertEquals(
            1,
            structuredUnsupportedCount,
            "Only the upstream handDrawn DDLT fixture may be unsupported",
        )
        assertTrue(
            failures.isEmpty(),
            failures.joinToString(
                prefix = "Failed Mermaid 12.0.0 Swimlanes DDLT sources:\n",
                separator = "\n",
            ),
        )
    }

    private fun validate(caseId: String, scene: MermaidScene): String? {
        val lanes = scene.elements.filterIsInstance<SceneShape>()
            .filter { shape -> shape.id.startsWith("subgraph_") }
        val edges = scene.elements.filterIsInstance<ScenePath>()
        return when {
            !scene.width.isFinite() || scene.width <= 0f ->
                "$caseId: invalid scene width ${scene.width}"
            !scene.height.isFinite() || scene.height <= 0f ->
                "$caseId: invalid scene height ${scene.height}"
            lanes.isEmpty() -> "$caseId: missing swimlane containers"
            edges.isEmpty() -> "$caseId: missing rendered edges"
            edges.any { edge -> edge.points.size < 2 } ->
                "$caseId: edge has fewer than two route points"
            edges.flatMap(ScenePath::points).any { point ->
                !point.x.isFinite() || !point.y.isFinite()
            } -> "$caseId: edge route contains non-finite coordinates"
            else -> null
        }
    }

    private fun Int?.orZero(): Int = this ?: 0
}
