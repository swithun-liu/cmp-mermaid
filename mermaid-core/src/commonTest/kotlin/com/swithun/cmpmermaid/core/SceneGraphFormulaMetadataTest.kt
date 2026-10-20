package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SceneGraphFormulaMetadataTest {
    @Test
    fun copyPreservesFormulaMetadataAcrossPublicSceneTypes() {
        val request = formulaRequest()
        val source = MermaidFormulaSource("x^2")
        val span = SceneTextSpan(
            start = 0,
            end = 3,
        ).withFormula(source)
        val text = SceneText(
            text = "x^2",
            bounds = SceneRect(0f, 0f, 20f, 10f),
            color = SceneColor(0xFF000000),
            fontSize = 16f,
            spans = listOf(span),
        ).withFormula(request)
        val scene = MermaidScene(
            width = 20f,
            height = 10f,
            background = SceneColor(0xFFFFFFFF),
            elements = listOf(text),
        ).withFormulaRequests(listOf(request))
        val metrics = TextMetrics(
            width = 20f,
            height = 10f,
            lineCount = 1,
        ).withFormula(request)

        assertEquals(source, span.copy(end = 4).formula)
        assertEquals(request, text.copy(bounds = SceneRect(1f, 2f, 21f, 12f)).formula)
        assertEquals(listOf(request), scene.copy(width = 21f).formulaRequests)
        assertEquals(request, metrics.copy(width = 21f).formula)
    }

    @Test
    fun formulaMetadataParticipatesInValueEquality() {
        val request = formulaRequest()
        val plain = TextMetrics(width = 20f, height = 10f, lineCount = 1)
        val formula = plain.withFormula(request)

        assertNotEquals(plain, formula)
    }

    private fun formulaRequest() = MermaidFormulaMeasureRequest(
        source = "x^2",
        displayMode = MermaidFormulaDisplayMode.Block,
        maxWidth = 200f,
        fontSize = 16f,
        rasterScale = 2f,
        accessibilityText = "x squared",
    )
}
