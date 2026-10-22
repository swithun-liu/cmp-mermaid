package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0: common.ts -> renderKatexUnsanitized;
 * createText.ts -> addHtmlSpan. Intrinsic provider metrics are injected so these
 * tests validate composition and graph geometry independently of a math parser.
 */
class MermaidMixedFormulaTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(request.text.length * 8f, request.fontSize, lineCount = 1)
        },
        options = MermaidRenderOptions(layout = "dagre"),
    )

    @Test
    fun measuresOrdinaryTextAndAllFormulasBeforeGraphLayout() {
        val scene = resolved("left \$\$a^2\$\$ right \$\$b^2\$\$ end")
        val text = scene.elements.filterIsInstance<SceneText>().single()
        val layout = assertNotNull(text.formulaLayout)
        assertEquals(listOf("left", "a^2", "right", "b^2", "end"), layout.items.map { it.text })
        assertEquals(listOf("a^2", "b^2"), scene.formulaRequests.map { it.source })
        assertEquals(32f + 30f + 40f + 50f + 24f, layout.width)
        assertEquals(40f, layout.height)
        assertEquals(layout.width, text.bounds.width)
        assertEquals(40f, text.bounds.height)
        assertEquals(9.5f, layout.items.first().bounds.top)
        assertEquals(5f, layout.items[1].bounds.top)
        assertEquals(0f, layout.items[3].bounds.top)
        layout.items.zipWithNext().forEach { (first, next) ->
            assertEquals(first.bounds.right, next.bounds.left)
        }
        assertEquals(emptyList(), scene.diagnostics)
    }

    @Test
    fun repeatedFormulaSourcesKeepSeparateItemsAndShareRequests() {
        val scene = resolved("\$\$a^2\$\$ + \$\$a^2\$\$")
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(1, scene.formulaRequests.size)
        assertEquals(2, layout.items.count { it.formula != null })
        assertEquals(68f, layout.width)
        assertEquals(30f, layout.height)
    }

    @Test
    fun brAndLiteralNewlineFollowTheOfficialNowrapOrder() {
        listOf("energy<br/>\$\$a^2\$\$", "energy\n\$\$a^2\$\$").forEach { label ->
            val scene = resolved(label)
            val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
            assertEquals(listOf("energy", "a^2"), layout.items.map { it.text })
            assertEquals(78f, layout.width)
            assertEquals(30f, layout.height)
            assertEquals(0f, layout.items.first().bounds.left)
            assertEquals(48f, layout.items.last().bounds.left)
        }
    }

    @Test
    fun whitespaceOnlyAnonymousItemsDoNotCreateLineBoxes() {
        val scene = resolved("\$\$a^2\$\$<br/>\$\$b^2\$\$")
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(2, layout.items.size)
        assertEquals(80f, layout.width)
        assertEquals(40f, layout.height)
    }

    @Test
    fun oneFailedFormulaFallsBackWithoutRemovingSuccessfulNeighbors() {
        val label = "left \$\$a^2\$\$ + \$\$b^2\$\$ end"
        val pending = render(label, MermaidFormulaRenderContext())
        val requests = pending.formulaRequests.associateBy { it.source }
        val scene = render(
            label,
            MermaidFormulaRenderContext(
                metrics = mapOf(requests.getValue("a^2") to MermaidFormulaMetrics(30f, 30f, 20f)),
                fallbacks = setOf(requests.getValue("b^2")),
            ),
        )
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(listOf("left", "a^2", "+", "b^2", "end"), layout.items.map { it.text })
        assertEquals("a^2", layout.items.single { it.formula != null }.formula?.source)
        assertNull(layout.items.single { it.text == "b^2" }.formula)
        assertEquals(MermaidDiagnosticFallback.PlainText,
            assertIs<MermaidDiagnostic.UnsupportedFeature>(scene.diagnostics.single()).fallback)
    }

    @Test
    fun noProviderPreservesReadableCompleteLabel() {
        val scene = assertIs<GMResult.Ok<MermaidScene>>(
            engine.render("flowchart LR\nA[\"left \$\$a^2\$\$ + \$\$b^2\$\$ end\"]", context),
        ).value
        val text = scene.elements.filterIsInstance<SceneText>().single()
        assertEquals("left a^2 + b^2 end", text.text)
        assertNull(text.formulaLayout)
        assertEquals(emptyList(), scene.formulaRequests)
        assertEquals(1, scene.diagnostics.size)
    }

    @Test
    fun directHtmlStylesRemainAttachedToOrdinaryFlexItems() {
        val scene = resolved("<b>left</b> \$\$a^2\$\$ <i>right</i>")
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(listOf("left", "a^2", "right"), layout.items.map { it.text })
        assertEquals(SceneTextWeight.Bold, layout.items.first().spans.single().weight)
        assertTrue(layout.items.last().spans.single().italic)
    }

    @Test
    fun neutralHtmlChildrenAndNestedStylesKeepTheirActualFlexBoundaries() {
        val scene = resolved("before<span> left <b>middle</b> right </span>after \$\$a^2\$\$")
        val layout = assertNotNull(scene.elements.filterIsInstance<SceneText>().single().formulaLayout)
        assertEquals(listOf("before", "left middle right", "after", "a^2"),
            layout.items.map { it.text })
        val span = layout.items[1].spans.single()
        assertEquals(5, span.start)
        assertEquals(11, span.end)
        assertEquals(SceneTextWeight.Bold, span.weight)
        assertEquals(136f, layout.items[1].bounds.width)
    }

    @Test
    fun adjacentHtmlElementsRemainSeparateEvenWithEqualStyles() {
        val layout = assertNotNull(
            resolved("<span> a </span><span> b </span>\$\$a^2\$\$")
                .elements.filterIsInstance<SceneText>().single().formulaLayout,
        )
        assertEquals(listOf("a", "b", "a^2"), layout.items.map { it.text })
        assertEquals(46f, layout.width)
    }

    @Test
    fun directSmallerElementUsesItsOwnCssLineBoxAndRelativeDescendantStyles() {
        val layout = assertNotNull(
            resolved("<small>tiny <b>bold</b></small> \$\$a^2\$\$")
                .elements.filterIsInstance<SceneText>().single().formulaLayout,
        )
        val item = layout.items.first()
        assertEquals(5f / 6f, item.fontSizeScale)
        assertEquals(17.5f, item.bounds.height, 0.001f)
        assertTrue(item.spans.all { it.fontSizeScale == 1f })
        assertEquals(SceneTextWeight.Bold, item.spans.last().weight)
    }

    @Test
    fun requirementFieldPrefixesParticipateInResolvedFormulaLayout() {
        val source = "requirementDiagram\nrequirement formula_label {\n" +
            "id: 1\ntext: \"value \$\$a^2\$\$ plus \$\$b^2\$\$\"\n" +
            "risk: low\nverifymethod: test\n}"
        val pending = assertIs<GMResult.Ok<MermaidScene>>(
            engine.render(source, context, MermaidFormulaRenderContext()),
        ).value
        val scene = assertIs<GMResult.Ok<MermaidScene>>(
            engine.render(source, context, MermaidFormulaRenderContext(
                metrics = pending.formulaRequests.associateWith {
                    MermaidFormulaMetrics(30f, 40f, 28f)
                },
            )),
        ).value
        val text = scene.elements.filterIsInstance<SceneText>().single { it.formulaLayout != null }
        val layout = assertNotNull(text.formulaLayout)
        assertEquals(listOf("Text: value", "a^2", "plus", "b^2"), layout.items.map { it.text })
        assertEquals(40f, text.bounds.height)
        assertEquals(180f, layout.width)
        assertTrue(scene.elements.filterIsInstance<SceneShape>().single().bounds.height > 40f)
        assertEquals(emptyList(), scene.diagnostics)
    }

    @Test
    fun untranslatedNestedMathAndBlockFormattingReturnStructuredUnsupported() {
        listOf("<b>left \$\$a^2\$\$ right</b>", "<p>left</p>\$\$a^2\$\$")
            .forEach { label ->
                val result = assertIs<GMResult.Err<MermaidError>>(
                    engine.render("flowchart LR\nA[\"$label\"]",
                        context, MermaidFormulaRenderContext()),
                )
                assertIs<MermaidError.UnsupportedFeature>(result.error)
            }
    }

    @Test
    fun compositionSurvivesSceneCopiesAndParticipatesInEquality() {
        val text = resolved("left \$\$a^2\$\$").elements.filterIsInstance<SceneText>().single()
        val layout = assertNotNull(text.formulaLayout)
        assertEquals(layout, text.copy(bounds = text.bounds.translate(10f, 20f)).formulaLayout)
        assertNotEquals(text, text.withFormulaLayout(null))
        val metrics = TextMetrics(layout.width, layout.height).withFormulaLayout(layout)
        assertEquals(layout, metrics.copy(width = 200f).formulaLayout)
        assertNotEquals(metrics, metrics.withFormulaLayout(null))
    }

    private fun resolved(label: String): MermaidScene {
        val pending = render(label, MermaidFormulaRenderContext())
        return render(
            label,
            MermaidFormulaRenderContext(metrics = pending.formulaRequests.associateWith {
                if (it.source == "b^2") MermaidFormulaMetrics(50f, 40f, 28f)
                else MermaidFormulaMetrics(30f, 30f, 20f)
            }),
        )
    }

    private fun render(label: String, formulas: MermaidFormulaRenderContext): MermaidScene =
        assertIs<GMResult.Ok<MermaidScene>>(
            engine.render("flowchart LR\nA[\"$label\"]", context, formulas),
        ).value
}
