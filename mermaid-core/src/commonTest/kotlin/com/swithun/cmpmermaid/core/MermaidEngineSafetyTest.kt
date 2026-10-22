package com.swithun.cmpmermaid.core

import kotlinx.coroutines.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MermaidEngineSafetyTest {
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { TextMetrics(width = 8f, height = 18f) },
    )

    @Test
    fun convertsUnexpectedPluginExceptionToStructuredFailure() {
        val engine = MermaidEngine(
            plugins = listOf(
                plugin { _, _ ->
                    throw IndexOutOfBoundsException("index 4 is outside size 2")
                },
            ),
        )

        val result = engine.render("testDiagram\n  A", context)

        val error = assertIs<MermaidError.Unexpected>(
            assertIs<GMResult.Err<MermaidError>>(result).error,
        )
        assertEquals(MermaidRenderErrorType.UNEXPECTED_EXCEPTION, error.renderErrorType)
        assertEquals("IndexOutOfBoundsException", error.exceptionType)
        assertTrue(error.message.contains("index 4 is outside size 2"))
    }

    @Test
    fun preservesExpectedContentFailure() {
        val expected = MermaidError.Parse(
            line = 2,
            column = 3,
            message = "Unexpected token",
        )
        val engine = MermaidEngine(
            plugins = listOf(plugin { _, _ -> GMResult.Err(expected) }),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(
            engine.render("testDiagram\n  [", context),
        ).error

        assertEquals(expected, error)
        assertEquals(MermaidRenderErrorType.CONTENT_ERROR, error.renderErrorType)
    }

    @Test
    fun classifiesTextMetricProviderExceptionAsUnexpected() {
        val failingContext = context.copy(
            textMetrics = TextMetricProvider {
                throw IndexOutOfBoundsException("font table index")
            },
        )

        val error = assertIs<MermaidError.Unexpected>(
            assertIs<GMResult.Err<MermaidError>>(
                MermaidEngine().render("flowchart LR\n  A --> B", failingContext),
            ).error,
        )

        assertEquals(MermaidRenderErrorType.UNEXPECTED_EXCEPTION, error.renderErrorType)
        assertTrue(error.message.contains("Text measurement failed for 'A'"))
        assertTrue(error.message.contains("font table index"))
    }

    @Test
    fun doesNotConvertCoroutineCancellationToRenderFailure() {
        val engine = MermaidEngine(
            plugins = listOf(
                plugin { _, _ ->
                    throw CancellationException("render cancelled")
                },
            ),
        )

        assertFailsWith<CancellationException> {
            engine.render("testDiagram\n  A", context)
        }
    }

    @Test
    fun doesNotConvertFatalErrorToRenderFailure() {
        val engine = MermaidEngine(
            plugins = listOf(
                plugin { _, _ ->
                    throw AssertionError("fatal renderer failure")
                },
            ),
        )

        assertFailsWith<AssertionError> {
            engine.render("testDiagram\n  A", context)
        }
    }

    @Test
    fun mixedFormulaGeometryRejectsAggregateOverflow() {
        val result = renderMixedFormulaWidths(Float.MAX_VALUE, Float.MAX_VALUE)

        assertIs<MermaidError.Layout>(assertIs<GMResult.Err<MermaidError>>(result).error)
    }

    @Test
    fun mixedFormulaGeometryPreservesFiniteIntrinsicBoxes() {
        val scene = assertIs<GMResult.Ok<MermaidScene>>(
            renderMixedFormulaWidths(40f, 50f),
        ).value
        val label = assertIs<SceneText>(scene.elements.single())
        val layout = kotlin.test.assertNotNull(label.formulaLayout)

        assertEquals(90f, scene.width)
        assertEquals(20f, scene.height)
        assertEquals(listOf(SceneRect(0f, 0f, 40f, 20f), SceneRect(40f, 0f, 90f, 20f)),
            layout.items.map { it.bounds })
    }

    @Test
    fun invalidResolvedFormulaMetricsUsePendingTextMeasurement() {
        for (invalid in listOf(Float.NaN, Float.POSITIVE_INFINITY, 0f, -1f)) {
            val scene = assertIs<GMResult.Ok<MermaidScene>>(
                renderMixedFormulaWidths(invalid, 50f),
            ).value

            assertEquals(58f, scene.width, "invalid=$invalid")
            assertEquals(20f, scene.height, "invalid=$invalid")
        }
    }

    @Test
    fun mixedFormulaTextMeasurementPreservesFailureClassificationAndControlFlow() {
        for (failure in listOf(IllegalArgumentException("bad font"), CancellationException("cancel"))) {
            val failingContext = context.copy(textMetrics = TextMetricProvider { throw failure })
            val engine = MermaidEngine(listOf(mixedFormulaPlugin()))
            if (failure is CancellationException) {
                assertFailsWith<CancellationException> {
                    engine.render("testDiagram", failingContext, MermaidFormulaRenderContext())
                }
            } else {
                val result = engine.render("testDiagram", failingContext, MermaidFormulaRenderContext())
                val error = assertIs<MermaidError.Unexpected>(
                    assertIs<GMResult.Err<MermaidError>>(result).error,
                )
                assertEquals(MermaidRenderErrorType.UNEXPECTED_EXCEPTION, error.renderErrorType)
                assertTrue(error.message.contains("bad font"))
            }
        }
    }

    private fun renderMixedFormulaWidths(
        first: Float,
        second: Float,
    ): GMResult<MermaidScene, MermaidError> = MermaidEngine(listOf(mixedFormulaPlugin())).render(
        "testDiagram",
        context,
        MermaidFormulaRenderContext(metrics = listOf("x" to first, "y" to second).associate { (text, width) ->
            MermaidFormulaMeasureRequest(text, MermaidFormulaDisplayMode.Block, 200f, 16f, 1f, text) to
                MermaidFormulaMetrics(width, 20f, 15f)
        }),
    )

    private fun mixedFormulaPlugin(): MermaidDiagramPlugin = plugin { _, renderContext ->
        val spans = listOf(
            SceneTextSpan(0, 1).withFormula(MermaidFormulaSource("x")),
            SceneTextSpan(1, 2).withFormula(MermaidFormulaSource("y")),
        )
        val metrics = renderContext.textMetrics.measure(
            TextMetricsRequest("xy", 16f, 200f, spans = spans),
        )
        GMResult.Ok(MermaidScene(
            width = metrics.width,
            height = metrics.height,
            background = SceneColor(0xffffffff),
            elements = listOf(SceneText(
                text = "xy",
                bounds = SceneRect(0f, 0f, metrics.width, metrics.height),
                color = SceneColor(0xff000000),
                fontSize = 16f,
                spans = spans,
            ).withFormulaLayout(metrics.formulaLayout)),
        ))
    }

    private fun plugin(
        implementation: (
            source: String,
            context: MermaidRenderContext,
        ) -> GMResult<MermaidScene, MermaidError>,
    ): MermaidDiagramPlugin = object : MermaidDiagramPlugin {
        override val id: String = "test"
        override val headers: Set<String> = setOf("testdiagram")

        override fun compile(
            source: String,
            context: MermaidRenderContext,
        ): GMResult<MermaidScene, MermaidError> = implementation(source, context)
    }
}
