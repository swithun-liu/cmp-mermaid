package com.swithun.cmpmermaid.compose

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidDiagnostic
import com.swithun.cmpmermaid.core.MermaidDiagnosticFallback
import com.swithun.cmpmermaid.core.MermaidDiagramPlugin
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidFormulaDisplayMode
import com.swithun.cmpmermaid.core.MermaidFormulaSource
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import com.swithun.cmpmermaid.core.SceneTextSpan
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import com.swithun.cmpmermaid.core.TextMetricsRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MermaidFormulaRendererTest {
    @Test
    fun selectsHostBeforeBuiltInWithoutRetryingAnotherRenderer() = runTest {
        var builtInCalls = 0
        val expected = MermaidFormulaError.Unsupported("Unsupported formula")
        val host = MermaidFormulaRenderer { GMResult.Err(expected) }
        val builtIn = MermaidFormulaRenderer {
            builtInCalls += 1
            GMResult.Ok(artifact())
        }

        val selected = selectMermaidFormulaRenderer(
            hostRenderer = host,
            builtInRenderer = builtIn,
            mode = MermaidFormulaRenderingMode.Automatic,
        )

        assertSame(host, selected)
        assertEquals(
            expected,
            assertIs<GMResult.Err<MermaidFormulaError>>(
                resolveMermaidFormula(request(), assertNotNull(selected)),
            ).error,
        )
        assertEquals(0, builtInCalls)
    }

    @Test
    fun plainTextModeBypassesEveryRenderer() {
        assertNull(
            selectMermaidFormulaRenderer(
                hostRenderer = MermaidFormulaRenderer { GMResult.Ok(artifact()) },
                builtInRenderer = MermaidFormulaRenderer { GMResult.Ok(artifact()) },
                mode = MermaidFormulaRenderingMode.PlainText,
            ),
        )
    }

    @Test
    fun cacheKeyIncludesTheCompleteFormulaRequest() = runTest {
        var calls = 0
        val cached = MermaidFormulaRenderer {
            calls += 1
            GMResult.Ok(artifact())
        }.cached()
        val first = request()
        val differentColor = first.copy(color = SceneColor(0xFFFF0000))

        cached.render(first)
        cached.render(first)
        cached.render(differentColor)

        assertEquals(2, calls)
    }

    @Test
    fun rendererFailuresRemainRetryable() = runTest {
        var calls = 0
        val cached = MermaidFormulaRenderer {
            calls += 1
            GMResult.Err(MermaidFormulaError.RenderFailed("temporary failure"))
        }.cached()

        cached.render(request())
        cached.render(request())

        assertEquals(2, calls)
    }

    @Test
    fun invalidArtifactsRemainRetryable() = runTest {
        var calls = 0
        val cached = MermaidFormulaRenderer {
            calls += 1
            GMResult.Ok(
                if (calls == 1) {
                    artifact(width = Float.NaN)
                } else {
                    artifact()
                },
            )
        }.cached()

        assertIs<GMResult.Err<MermaidFormulaError>>(
            resolveMermaidFormula(request(), cached),
        )
        assertIs<GMResult.Ok<MermaidFormulaArtifact>>(
            resolveMermaidFormula(request(), cached),
        )
        assertEquals(2, calls)
    }

    @Test
    fun convertsOrdinaryRendererExceptionsAndRejectsInvalidArtifacts() = runTest {
        val failed = resolveMermaidFormula(
            request = request(),
            renderer = MermaidFormulaRenderer {
                throw IllegalStateException("renderer failed")
            },
        )
        assertEquals(
            "renderer failed",
            assertIs<MermaidFormulaError.RenderFailed>(
                assertIs<GMResult.Err<MermaidFormulaError>>(failed).error,
            ).message,
        )

        val invalid = resolveMermaidFormula(
            request = request(),
            renderer = MermaidFormulaRenderer {
                GMResult.Ok(artifact().copy(width = Float.NaN))
            },
        )
        assertIs<MermaidFormulaError.InvalidArtifact>(
            assertIs<GMResult.Err<MermaidFormulaError>>(invalid).error,
        )
    }

    @Test
    fun doesNotConvertCoroutineCancellation() = runTest {
        assertFailsWith<CancellationException> {
            resolveMermaidFormula(
                request = request(),
                renderer = MermaidFormulaRenderer {
                    throw CancellationException("formula render cancelled")
                },
            )
        }
    }

    @Test
    fun resolvedDimensionsDriveTheFinalLayoutPass() = runTest {
        val renderedRequests = mutableListOf<MermaidFormulaRequest>()
        val prepared = prepareMermaidScene(
            source = formulaFlowchart(),
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(layout = "dagre"),
            engine = MermaidEngine(),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                renderedRequests += request
                GMResult.Ok(
                    artifact(
                        width = 96f,
                        height = 30f,
                        baseline = 22f,
                        accessibilityText = request.accessibilityText,
                    ),
                )
            },
            formulaRasterScale = 2f,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val formula = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == "x^2" }
        val paintRequest = assertNotNull(formula.formulaRequest())

        assertEquals(120f, formula.bounds.width)
        assertEquals(30f, formula.bounds.height)
        assertEquals(2f, paintRequest.rasterScale)
        assertNotNull(prepared.formulaArtifacts[paintRequest])
        assertEquals(96f, prepared.formulaArtifacts.getValue(paintRequest).width)
        assertEquals(renderedRequests.distinct(), renderedRequests)
        assertTrue(scene.diagnostics.isEmpty())
    }

    @Test
    fun rendererFailureFallsBackToPlainTextForTheSameFormula() = runTest {
        val expected = MermaidFormulaError.Unsupported("Unsupported command")
        val prepared = prepareMermaidScene(
            source = formulaFlowchart(),
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(layout = "dagre"),
            engine = MermaidEngine(),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { GMResult.Err(expected) },
            formulaRasterScale = 1f,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val formula = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == "x^2" }
        val diagnostic = assertIs<MermaidDiagnostic.UnsupportedFeature>(
            scene.diagnostics.single(),
        )

        assertNull(formula.formula)
        assertTrue(prepared.formulaArtifacts.isEmpty())
        assertEquals(expected, prepared.formulaFailures.values.single())
        assertEquals("KaTeX label", diagnostic.feature)
        assertEquals(MermaidDiagnosticFallback.PlainText, diagnostic.fallback)
    }

    @Test
    fun rendererFailureIsScopedToTheExactWidth() = runTest {
        val plugin = FormulaVariantsPlugin(
            variants = listOf(
                FormulaVariant(maxWidth = 100f),
                FormulaVariant(maxWidth = 200f),
            ),
        )
        val prepared = prepareMermaidScene(
            source = "formula-variants",
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(),
            engine = MermaidEngine(listOf(plugin)),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                if (request.maxWidth == 100f) {
                    GMResult.Err(MermaidFormulaError.Unsupported("Width is unsupported"))
                } else {
                    GMResult.Ok(artifact(accessibilityText = request.accessibilityText))
                }
            },
            formulaRasterScale = 1f,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val texts = scene.elements.filterIsInstance<SceneText>()
        val narrow = texts.single { text -> text.bounds.left == 0f }
        val wide = texts.single { text -> text.bounds.left == 100f }

        assertNull(narrow.formula)
        assertEquals(200f, assertNotNull(wide.formula).maxWidth)
        assertEquals(setOf(100f), prepared.formulaFailures.keys.mapTo(mutableSetOf()) { it.maxWidth })
        assertEquals(setOf(200f), prepared.formulaArtifacts.keys.mapTo(mutableSetOf()) { it.maxWidth })
    }

    @Test
    fun rendererFailureForOneColorDoesNotSuppressAnotherColor() = runTest {
        val failedColor = SceneColor(0xFF000000)
        val renderedColor = SceneColor(0xFFFF0000)
        val plugin = FormulaVariantsPlugin(
            variants = listOf(
                FormulaVariant(maxWidth = 200f, color = failedColor),
                FormulaVariant(maxWidth = 200f, color = renderedColor),
            ),
        )
        val prepared = prepareMermaidScene(
            source = "formula-variants",
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(),
            engine = MermaidEngine(listOf(plugin)),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                if (request.color == failedColor) {
                    GMResult.Err(MermaidFormulaError.Unsupported("Color is unsupported"))
                } else {
                    GMResult.Ok(artifact(accessibilityText = request.accessibilityText))
                }
            },
            formulaRasterScale = 1f,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val texts = scene.elements.filterIsInstance<SceneText>()

        assertNotNull(texts.single { text -> text.color == failedColor }.formula)
        assertNotNull(texts.single { text -> text.color == renderedColor }.formula)
        assertEquals(setOf(failedColor), prepared.formulaFailures.keys.mapTo(mutableSetOf()) { it.color })
        assertEquals(setOf(renderedColor), prepared.formulaArtifacts.keys.mapTo(mutableSetOf()) { it.color })
        assertEquals(1, scene.diagnostics.size)
    }

    @Test
    fun resolutionLimitFallsBackWhenFinalLayoutProducesANewWidth() = runTest {
        val plugin = NonConvergingFormulaPlugin()
        var rendererCalls = 0
        val prepared = prepareMermaidScene(
            source = "formula-test",
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(),
            engine = MermaidEngine(listOf(plugin)),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                rendererCalls += 1
                GMResult.Ok(
                    artifact(
                        width = request.maxWidth + 1f,
                        accessibilityText = request.accessibilityText,
                    ),
                )
            },
            formulaRasterScale = 1f,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val formula = scene.elements.filterIsInstance<SceneText>().single()
        val diagnostic = assertIs<MermaidDiagnostic.UnsupportedFeature>(
            scene.diagnostics.single(),
        )

        assertNull(formula.formula)
        assertEquals(8, rendererCalls)
        assertEquals(9, plugin.compileCalls)
        assertIs<MermaidFormulaError.ResolutionLimit>(
            prepared.formulaFailures.values.single(),
        )
        assertEquals(MermaidDiagnosticFallback.PlainText, diagnostic.fallback)
    }

    @Test
    fun formulaFlexContentRemainsCenteredUnderInheritedTextAlignment() {
        val artifact = artifact(width = 20f, height = 10f)
        val bounds = SceneRect(
            left = 10f,
            top = 20f,
            right = 110f,
            bottom = 60f,
        )

        fun element(alignment: SceneTextAlignment) = SceneText(
            text = "x^2",
            bounds = bounds,
            color = SceneColor(0xFF000000),
            fontSize = 16f,
            horizontalAlignment = alignment,
        )

        assertEquals(50f, formulaArtifactBounds(element(SceneTextAlignment.Start), artifact).left)
        assertEquals(50f, formulaArtifactBounds(element(SceneTextAlignment.Center), artifact).left)
        assertEquals(50f, formulaArtifactBounds(element(SceneTextAlignment.End), artifact).left)
        assertEquals(35f, formulaArtifactBounds(element(SceneTextAlignment.Center), artifact).top)
    }

    @Test
    fun mixedLabelsResolveEveryFormulaAndRelayoutTheCompleteContent() = runTest {
        val calls = mutableListOf<MermaidFormulaRequest>()
        val prepared = prepareMermaidScene(
            source = "flowchart LR\nA[\"energy \$\$x^2\$\$ + \$\$y^2\$\$ joules\"]",
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(layout = "dagre"),
            engine = MermaidEngine(),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                calls += request
                GMResult.Ok(artifact(width = 40f, height = 30f, baseline = 22f))
            },
            formulaRasterScale = 2f,
        )
        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val label = scene.elements.filterIsInstance<SceneText>().single()
        val layout = assertNotNull(label.formulaLayout)
        assertEquals(listOf("energy", "x^2", "+", "y^2", "joules"), layout.items.map { it.text })
        assertEquals(setOf("x^2", "y^2"), calls.mapTo(mutableSetOf()) { it.source })
        assertEquals(2, calls.size)
        assertEquals(2, prepared.formulaArtifacts.size)
        assertEquals(171f, layout.width)
        assertEquals(30f, label.bounds.height)
        assertEquals(layout.width, label.bounds.width)
        assertTrue(label.formulaRequests().all { prepared.formulaArtifacts.containsKey(it) })
        assertTrue(scene.diagnostics.isEmpty())
    }

    @Test
    fun aFailedMixedFormulaDoesNotDiscardOtherArtifactsOrOrdinaryText() = runTest {
        val prepared = prepareMermaidScene(
            source = "flowchart LR\nA[\"left \$\$x^2\$\$ + \$\$bad\$\$ right\"]",
            theme = MermaidTheme.FlowchartDefault,
            options = MermaidRenderOptions(layout = "dagre"),
            engine = MermaidEngine(),
            textMetrics = textMetrics(),
            assetMetrics = emptyMap(),
            formulaRenderer = MermaidFormulaRenderer { request ->
                if (request.source == "bad") GMResult.Err(MermaidFormulaError.Unsupported("invalid"))
                else GMResult.Ok(artifact(width = 40f, height = 30f, baseline = 22f))
            },
            formulaRasterScale = 1f,
        )
        val scene = assertIs<GMResult.Ok<MermaidScene>>(prepared.result).value
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(listOf("left", "x^2", "+", "bad", "right"), layout.items.map { it.text })
        assertEquals("x^2", layout.items.single { it.formula != null }.formula?.source)
        assertEquals(setOf("x^2"), prepared.formulaArtifacts.keys.mapTo(mutableSetOf()) { it.source })
        assertEquals(setOf("bad"), prepared.formulaFailures.keys.mapTo(mutableSetOf()) { it.source })
        assertEquals(1, scene.diagnostics.size)
    }

    @Test
    fun mixedFormulaCancellationPropagatesWithoutPublishingAPartialScene() = runTest {
        assertFailsWith<CancellationException> {
            prepareMermaidScene(
                source = "flowchart LR\nA[\"left \$\$x^2\$\$ right\"]",
                theme = MermaidTheme.FlowchartDefault,
                options = MermaidRenderOptions(layout = "dagre"),
                engine = MermaidEngine(),
                textMetrics = textMetrics(),
                assetMetrics = emptyMap(),
                formulaRenderer = MermaidFormulaRenderer { throw CancellationException("cancel") },
                formulaRasterScale = 1f,
            )
        }
    }

    private fun request() = MermaidFormulaRequest(
        source = "x^2",
        displayMode = MermaidFormulaDisplayMode.Block,
        maxWidth = 200f,
        fontSize = 16f,
        color = SceneColor(0xFF000000),
        rasterScale = 2f,
        accessibilityText = "x squared",
    )

    private fun artifact(
        width: Float = 40f,
        height: Float = 20f,
        baseline: Float = 15f,
        accessibilityText: String = "x squared",
    ) = MermaidFormulaArtifact(
        width = width,
        height = height,
        baseline = baseline,
        accessibilityText = accessibilityText,
        painter = MermaidFormulaPainter { _, _ -> },
    )

    private fun textMetrics() = TextMetricProvider { request ->
        TextMetrics(
            width = minOf(request.maxWidth, request.text.length * 7f),
            height = 18f,
            lineCount = 1,
        )
    }

    private fun formulaFlowchart(): String =
        "flowchart LR\n A[\"${'$'}${'$'}x^2${'$'}${'$'}\"] --> B[Done]"

    private data class FormulaVariant(
        val maxWidth: Float,
        val color: SceneColor = SceneColor(0xFF000000),
    )

    private class FormulaVariantsPlugin(
        private val variants: List<FormulaVariant>,
    ) : MermaidDiagramPlugin {
        override val id: String = "formula-variants"
        override val headers: Set<String> = setOf(id)

        override fun compile(
            source: String,
            context: MermaidRenderContext,
        ): GMResult<MermaidScene, MermaidError> {
            val text = "x^2"
            val formulaSpan = SceneTextSpan(
                start = 0,
                end = text.length,
            ).withFormula(MermaidFormulaSource(text))
            val elements = variants.mapIndexed { index, variant ->
                val metrics = context.textMetrics.measure(
                    TextMetricsRequest(
                        text = text,
                        fontSize = 16f,
                        maxWidth = variant.maxWidth,
                        spans = listOf(formulaSpan),
                    ),
                )
                val left = index * 100f
                SceneText(
                    text = text,
                    bounds = SceneRect(
                        left = left,
                        top = 0f,
                        right = left + metrics.width,
                        bottom = metrics.height,
                    ),
                    color = variant.color,
                    fontSize = 16f,
                    spans = listOf(formulaSpan),
                ).withFormula(metrics.formula)
            }
            return GMResult.Ok(
                MermaidScene(
                    width = 300f,
                    height = elements.maxOf { textElement -> textElement.bounds.height },
                    background = SceneColor(0xFFFFFFFF),
                    elements = elements,
                ),
            )
        }
    }

    private class NonConvergingFormulaPlugin : MermaidDiagramPlugin {
        override val id: String = "formula-test"
        override val headers: Set<String> = setOf(id)
        var compileCalls: Int = 0
            private set

        override fun compile(
            source: String,
            context: MermaidRenderContext,
        ): GMResult<MermaidScene, MermaidError> {
            compileCalls += 1
            val text = "x^2"
            val formulaSpan = SceneTextSpan(
                start = 0,
                end = text.length,
            ).withFormula(MermaidFormulaSource(text))
            val metrics = context.textMetrics.measure(
                TextMetricsRequest(
                    text = text,
                    fontSize = 16f,
                    maxWidth = 100f + compileCalls,
                    spans = listOf(formulaSpan),
                ),
            )
            val sceneText = SceneText(
                text = text,
                bounds = SceneRect(
                    left = 0f,
                    top = 0f,
                    right = metrics.width,
                    bottom = metrics.height,
                ),
                color = SceneColor(0xFF000000),
                fontSize = 16f,
                spans = listOf(formulaSpan),
            ).withFormula(metrics.formula)
            return GMResult.Ok(
                MermaidScene(
                    width = metrics.width.coerceAtLeast(1f),
                    height = metrics.height.coerceAtLeast(1f),
                    background = SceneColor(0xFFFFFFFF),
                    elements = listOf(sceneText),
                ),
            )
        }
    }
}
