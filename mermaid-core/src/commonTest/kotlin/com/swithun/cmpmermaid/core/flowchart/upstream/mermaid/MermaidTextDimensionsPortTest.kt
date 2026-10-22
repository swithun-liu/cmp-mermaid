package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidTextDimensionsPortTest {
    @Test
    fun selectsTheConfiguredFontUnlessAllThreeSansDimensionsAreGreater() {
        val result = MermaidTextDimensionsPort.calculateTextWidth(
            "A<br/>B", MermaidRenderOptions(fontFamily = "Arial"),
            TextMetricProvider { request ->
                if (request.fontFamily == "sans-serif") TextMetrics(40f, 30f)
                else TextMetrics(50f, 15f)
            },
        )
        assertEquals(50f, assertIs<GMResult.Ok<Float>>(result).value)
    }

    @Test
    fun usesJavascriptRoundingBeforeComparingFontRecords() {
        val result = MermaidTextDimensionsPort.calculateTextWidth(
            "A<br/>B", MermaidRenderOptions(fontFamily = "Arial"),
            TextMetricProvider { request ->
                if (request.fontFamily == "sans-serif") TextMetrics(10.5f, 14.5f)
                else TextMetrics(10.1f, 14.1f)
            },
        )
        assertEquals(11f, assertIs<GMResult.Ok<Float>>(result).value)
    }

    @Test
    fun measuresLiteralSvgTextWithCollapsedWhitespaceForBothFonts() {
        val inputs = mutableListOf<String>()
        val result = MermaidTextDimensionsPort.calculateTextWidth(
            "  A \n B  ", MermaidRenderOptions(),
            TextMetricProvider { request ->
                inputs += request.text
                TextMetrics(12f, 14f)
            },
        )
        assertIs<GMResult.Ok<Float>>(result)
        assertEquals(listOf("A B", "A B"), inputs)
    }

    @Test
    fun emptyTextBypassesMeasurementAndInvalidOrThrowingMetricsAreStructured() {
        val throwing = TextMetricProvider { throw IllegalArgumentException("metric failure") }
        assertEquals(0f, assertIs<GMResult.Ok<Float>>(
            MermaidTextDimensionsPort.calculateTextWidth("", MermaidRenderOptions(), throwing),
        ).value)
        val exception = assertIs<GMResult.Err<MermaidError>>(
            MermaidTextDimensionsPort.calculateTextWidth("A", MermaidRenderOptions(), throwing),
        )
        assertIs<MermaidError.Unexpected>(exception.error)
        val invalid = assertIs<GMResult.Err<MermaidError>>(
            MermaidTextDimensionsPort.calculateTextWidth("A", MermaidRenderOptions(),
                TextMetricProvider { TextMetrics(Float.NaN, 10f) }),
        )
        assertIs<MermaidError.Layout>(invalid.error)
    }
}
