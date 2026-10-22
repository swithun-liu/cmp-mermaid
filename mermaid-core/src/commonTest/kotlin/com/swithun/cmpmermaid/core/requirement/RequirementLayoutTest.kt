package com.swithun.cmpmermaid.core.requirement

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidFormulaMetrics
import com.swithun.cmpmermaid.core.MermaidFormulaRenderContext
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneStrokePattern
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import com.swithun.cmpmermaid.core.SceneTextWeight
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import com.swithun.cmpmermaid.core.TextMetricsRequest
import kotlinx.coroutines.CancellationException
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RequirementLayoutTest {
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = minOf(request.maxWidth, request.text.length * request.fontSize * 0.5f),
                height = request.fontSize * request.lineHeight,
            )
        },
        options = MermaidRenderOptions(layout = "dagre", look = "classic"),
    )

    @Test
    fun rendersRequirementAndElementBoxesWithBodyDivider() {
        val scene = render(
            """
            requirementDiagram
              requirement checkout {
                id: "PAY-1"
                text: Online checkout
                risk: high
                verifyMethod: test
              }
              element gateway {
                type: service
                docRef: docs/payment
              }
            """.trimIndent(),
        )

        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        assertTrue(shapes.getValue("checkout").bounds.height > shapes.getValue("gateway").bounds.height)
        assertEquals(166f, shapes.getValue("checkout").bounds.height)
        assertEquals(124f, shapes.getValue("gateway").bounds.height)
        val text = scene.elements.filterIsInstance<SceneText>()
        assertTrue(text.any { it.text == "<<Requirement>>" })
        assertTrue(text.any { it.text == "checkout" })
        assertTrue(text.any { it.text == "ID: PAY-1" })
        assertTrue(text.any { it.text == "Verification: Test" })
        assertTrue(text.any { it.text == "<<Element>>" })
        assertTrue(text.any { it.text == "Doc Ref: docs/payment" })
        val dividers = scene.elements.filterIsInstance<ScenePath>()
            .filter { it.id.endsWith("-divider") }
        assertEquals(2, dividers.size)
    }

    @Test
    fun preservesOfficialHtmlLineBoxesWhenMetricsReturnOnlyGlyphHeight() {
        val glyphHeightContext = context.copy(
            textMetrics = TextMetricProvider { request ->
                TextMetrics(
                    width = minOf(
                        request.maxWidth,
                        request.text.length * request.fontSize * 0.5f,
                    ),
                    height = request.fontSize,
                )
            },
        )
        val scene = render(
            """
            requirementDiagram
              requirement checkout {
                id: "PAY-1"
                text: Online checkout
                risk: high
                verifyMethod: test
              }
              element gateway {
                type: service
                docRef: docs/payment
              }
            """.trimIndent(),
            glyphHeightContext,
        )

        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        assertEquals(166f, shapes.getValue("checkout").bounds.height)
        assertEquals(124f, shapes.getValue("gateway").bounds.height)
    }

    @Test
    fun preservesLongLabelWrappingBetweenMeasurementAndPainting() {
        val content = "a b c d ".repeat(12).trim()
        val input = "Text: $content"
        val source = """
            requirementDiagram
              requirement R {
                text: "$content"
              }
              style R font-size:50px
        """.trimIndent()
        for (htmlLabels in listOf(true, false)) {
            val requests = mutableListOf<TextMetricsRequest>()
            val renderContext = context.copy(
                textMetrics = TextMetricProvider { request ->
                    requests += request
                    wrappedMetrics(request)
                },
                options = context.options.copy(fontSize = 12f, htmlLabels = htmlLabels),
            )
            val scene = render(source, renderContext)
            val label = scene.elements.filterIsInstance<SceneText>().single { it.text == input }
            val measured = requests.single { it.text == input && it.maxWidth.isFinite() }
            val expectedWidth = input.length * 12f * 0.5f + 50f
            assertEquals(656f, expectedWidth)
            assertEquals(expectedWidth, measured.maxWidth)
            assertEquals(50f, measured.fontSize)
            assertEquals(4, wrappedMetrics(measured).lineCount)

            val painted = wrappedMetrics(
                measured.copy(
                    maxWidth = if (label.softWrap) {
                        label.wrappingWidth ?: Float.POSITIVE_INFINITY
                    } else {
                        Float.POSITIVE_INFINITY
                    },
                    htmlLabel = label.htmlLabel,
                ),
            )
            assertEquals(label.bounds.width, painted.width, 0.01f, "HTML mode: $htmlLabels")
            assertEquals(
                label.bounds.height,
                painted.height + if (htmlLabels) 0f else 6f,
                0.01f,
            )
            assertEquals(measured.maxWidth, label.wrappingWidth)
            assertEquals(measured.htmlLabel, label.htmlLabel)
            assertTrue(label.softWrap)
            val box = scene.elements.filterIsInstance<SceneShape>().single { it.id == "R" }
            assertEquals(label.bounds.width + 20f, box.bounds.width, 0.01f)
        }
    }

    @Test
    fun preservesMixedFormulaLabelMeasurementAndInlineStyles() {
        val content = "energy <b>value</b> \$\$a^2\$\$ plus \$\$b^2\$\$"
        val source = """
            requirementDiagram
              requirement R {
                text: "$content"
              }
              style R font-size:18px
        """.trimIndent()
        val renderContext = context.copy(
            textMetrics = TextMetricProvider(::wrappedMetrics),
            options = context.options.copy(fontSize = 12f),
        )
        val engine = MermaidEngine()
        val pending = assertIs<GMResult.Ok<MermaidScene>>(
            engine.render(source, renderContext, MermaidFormulaRenderContext()),
        ).value
        val resolved = assertIs<GMResult.Ok<MermaidScene>>(
            engine.render(
                source,
                renderContext,
                MermaidFormulaRenderContext(
                    metrics = pending.formulaRequests.associateWith { request ->
                        if (request.source == "a^2") MermaidFormulaMetrics(30f, 40f, 28f)
                        else MermaidFormulaMetrics(50f, 60f, 40f)
                    },
                ),
            ),
        ).value
        val label = resolved.elements.filterIsInstance<SceneText>()
            .single { it.formulaLayout != null }
        val layout = assertNotNull(label.formulaLayout)
        val expectedWidth = ("Text: $content").length * 12f * 0.5f + 50f
        assertEquals(listOf("a^2", "b^2"), resolved.formulaRequests.map { it.source })
        assertTrue(resolved.formulaRequests.all { it.maxWidth == expectedWidth })
        assertEquals(expectedWidth, label.wrappingWidth)
        assertTrue(label.htmlLabel)
        assertTrue(label.softWrap)
        assertEquals(
            listOf("Text: energy", "value", "a^2", "plus", "b^2"),
            layout.items.map { it.text },
        )
        assertTrue(layout.items[1].spans.any { it.weight == SceneTextWeight.Bold })
        assertEquals(30f, layout.items[2].bounds.width)
        assertEquals(50f, layout.items[4].bounds.width)
        assertEquals(60f, layout.height)
        assertEquals(layout.height, label.bounds.height)
        assertEquals(layout.width, label.bounds.width)
        assertTrue(layout.width < expectedWidth)
        layout.items.zipWithNext().forEach { (first, next) ->
            assertEquals(first.bounds.right, next.bounds.left)
        }
        assertEquals(emptyList(), resolved.diagnostics)
    }

    @Test
    fun preservesShortAndMultilineLabelContractsForAllRequirementFields() {
        val source = """
            requirementDiagram
              requirement first {
                text: "first<br/>second"
              }
              requirement second {
              }
              element implementation {
                type: component
                docRef: docs/link
              }
              first - traces -> second
        """.trimIndent()
        for (htmlLabels in listOf(true, false)) {
            val requests = mutableListOf<TextMetricsRequest>()
            val scene = render(
                source,
                context.copy(
                    textMetrics = TextMetricProvider { request ->
                        requests += request
                        wrappedMetrics(request)
                    },
                    options = context.options.copy(fontSize = 12f, htmlLabels = htmlLabels),
                ),
            )
            val labels = scene.elements.filterIsInstance<SceneText>()
            assertEquals(10, labels.size)
            assertTrue(labels.none { it.text.isEmpty() })
            labels.forEach { label ->
                val request = requests.first { it.text == label.text && it.maxWidth.isFinite() }
                assertEquals(request.maxWidth, label.wrappingWidth, label.text)
                assertEquals(request.htmlLabel, label.htmlLabel, label.text)
                assertTrue(label.softWrap, label.text)
                val metrics = wrappedMetrics(request)
                val edgePadding = if (label.zIndex == 7) 6f else 0f
                assertEquals(
                    metrics.height + if (htmlLabels) 0f else 6f,
                    label.bounds.height - edgePadding,
                    0.01f,
                    label.text,
                )
            }
            val field = labels.single { it.text.startsWith("Text:") }
            assertEquals("Text: first\nsecond", field.text)
            assertEquals(36f + if (htmlLabels) 0f else 6f, field.bounds.height)
            assertTrue(scene.elements.filterIsInstance<ScenePath>().none { it.id == "second-divider" })
        }
    }

    @Test
    fun propagatesFinalRequirementLabelMeasurementFailure() {
        val result = MermaidEngine().render(
            "requirementDiagram\nrequirement R {\ntext: unavailable\n}",
            context.copy(
                textMetrics = TextMetricProvider { request ->
                    if (request.text == "Text: unavailable" && request.maxWidth.isFinite()) {
                        throw IllegalStateException("Unavailable label metrics")
                    }
                    wrappedMetrics(request)
                },
            ),
        )
        val failure = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.Unexpected>(failure)
        assertTrue(failure.message.contains("Requirement text measurement failed"))
    }

    @Test
    fun propagatesFinalRequirementLabelMeasurementCancellation() {
        assertFailsWith<CancellationException> {
            MermaidEngine().render(
                "requirementDiagram\nrequirement R {\ntext: cancelled\n}",
                context.copy(
                    textMetrics = TextMetricProvider { request ->
                        if (request.text == "Text: cancelled" && request.maxWidth.isFinite()) {
                            throw CancellationException("Cancelled label metrics")
                        }
                        wrappedMetrics(request)
                    },
                ),
            )
        }
    }

    @Test
    fun positionsCurvedContainsLabelAtUpdatedPathHalfLength() {
        val scene = render(
            """
            requirementDiagram
              direction BT
              requirement root_goal {
                text: "Maintain a recoverable replica"
              }
              functionalRequirement child_goal {
                text: "Persist every accepted change"
              }
              requirement copy_goal {
                text: "Mirror the recovery policy"
              }
              performanceRequirement derived_goal {
                text: "Calculate a replication checkpoint"
              }
              interfaceRequirement refined_goal {
                text: "Define the recovery-point objective"
              }
              requirement traced_goal {
                text: "Record the originating change request"
              }
              element implementation {
                type: "Replication worker"
              }
              element verification_evidence {
                type: "Recovery validation suite"
              }
              root_goal - contains -> child_goal
              copy_goal <- copies - root_goal
              root_goal - derives -> derived_goal
              implementation - satisfies -> root_goal
              verification_evidence - verifies -> child_goal
              refined_goal <- refines - root_goal
              root_goal - traces -> traced_goal
            """.trimIndent(),
        )

        val edge = scene.elements.filterIsInstance<ScenePath>()
            .single { it.id == "root_goal-child_goal-0" }
        val label = scene.elements.filterIsInstance<SceneText>()
            .single { it.text == "<<contains>>" }
        val expected = edge.points.halfLengthPoint()

        assertEquals(expected.x, label.bounds.center.x, 0.01f)
        assertEquals(expected.y, label.bounds.center.y, 0.01f)
    }

    @Test
    fun rendersContainsAndDependencyRelationshipMarkers() {
        val scene = render(
            """
            requirementDiagram
              requirement parent {
              }
              requirement child {
              }
              element "test" {
              }
              parent - contains -> child
              "test" - verifies -> child
            """.trimIndent(),
        )

        val relations = scene.elements.filterIsInstance<ScenePath>()
            .filterNot { it.id.endsWith("-divider") }
        val contains = relations.single { it.id == "parent-child-0" }
        assertEquals(SceneStrokePattern.Solid, contains.strokePattern)
        assertEquals(SceneArrowHead.RequirementContains, contains.arrowStart)
        assertEquals(SceneArrowHead.None, contains.arrowEnd)

        val verifies = relations.single { it.id == "test-child-1" }
        assertEquals(SceneStrokePattern.Dashed, verifies.strokePattern)
        assertEquals(listOf(10f, 7f), verifies.dashIntervals)
        assertEquals(SceneArrowHead.None, verifies.arrowStart)
        assertEquals(SceneArrowHead.RequirementArrow, verifies.arrowEnd)
        assertTrue(
            scene.elements.filterIsInstance<SceneText>()
                .any { it.text == "<<verifies>>" },
        )
    }

    @Test
    fun positionsBodyTextAtTheLeftAfterOfficialLabelTranslationAndRejectsElk() {
        val source = """
            requirementDiagram
              requirement checkout {
                id: 1
              }
        """.trimIndent()

        val dagre = render(source)
        val elk = MermaidEngine().render(
            source,
            context.copy(options = context.options.copy(layout = "elk")),
        )

        assertEquals(
            SceneTextAlignment.Start,
            dagre.elements.filterIsInstance<SceneText>().single { it.text == "ID: 1" }
                .horizontalAlignment,
        )
        assertEquals(
            35f,
            dagre.elements.filterIsInstance<SceneText>().single { it.text == "ID: 1" }
                .bounds.width,
        )
        val elkError = assertIs<GMResult.Err<MermaidError.UnsupportedFeature>>(elk).error
        assertEquals("ELK layout", elkError.feature)
    }

    @Test
    fun appliesDirectStylesAndColorThemePalettes() {
        val styled = render(
            """
            requirementDiagram
              requirement first {
                id: 1
              }
              requirement second {
                id: 2
              }
              style first fill:#112233,stroke:#445566,color:#778899,stroke-width:4px
            """.trimIndent(),
            context.copy(theme = MermaidTheme.FlowchartDefault),
        )
        val shapes = styled.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val dividers = styled.elements.filterIsInstance<ScenePath>()
            .filter { it.id.endsWith("-divider") }
            .associateBy(ScenePath::id)
        assertEquals(SceneColor(0xFF112233), shapes.getValue("first").fill)
        assertEquals(SceneColor(0xFF445566), shapes.getValue("first").stroke)
        assertEquals(4f, shapes.getValue("first").strokeWidth)
        assertEquals(SceneColor(0xFF445566), dividers.getValue("first-divider").color)
        assertEquals(4f, dividers.getValue("first-divider").strokeWidth)
        assertEquals(
            MermaidTheme.FlowchartDefault.bkgColorArray[1],
            shapes.getValue("second").fill,
        )
        assertEquals(
            shapes.getValue("second").stroke,
            dividers.getValue("second-divider").color,
        )
        assertEquals(
            shapes.getValue("second").strokeWidth,
            dividers.getValue("second-divider").strokeWidth,
        )
        assertEquals(
            SceneColor(0xFF778899),
            styled.elements.filterIsInstance<SceneText>()
                .single { it.text == "first" }
                .color,
        )
    }

    @Test
    fun appliesRequirementScopedThemeAndLookWithoutChangingGlobalOptions() {
        val scene = render(
            """
            ---
            config:
              theme: dark
              look: neo
              requirement:
                theme: default
                look: classic
            ---
            requirementDiagram
              requirement req {
              }
            """.trimIndent(),
            context.copy(theme = MermaidTheme.Dark),
        )

        val shape = scene.elements.filterIsInstance<SceneShape>().single { it.id == "req" }
        assertEquals(MermaidTheme.MermaidDefault.requirement.background, shape.fill)
        assertEquals(MermaidTheme.MermaidDefault.requirement.borderColor, shape.stroke)
        assertEquals(null, shape.shadow)
    }

    @Test
    fun appliesRequirementScopedClassicLookBeforeRejectingGlobalHandDrawnLook() {
        val scene = render(
            """
            ---
            config:
              look: handDrawn
              requirement:
                look: classic
            ---
            requirementDiagram
              requirement req {
              }
            """.trimIndent(),
        )

        val shape = scene.elements.filterIsInstance<SceneShape>().single { it.id == "req" }
        assertEquals(null, shape.shadow)
    }

    @Test
    fun rejectsEffectiveRequirementHandDrawnLook() {
        val global = MermaidEngine().render(
            """
            ---
            config:
              look: handDrawn
            ---
            requirementDiagram
              requirement req {
              }
            """.trimIndent(),
            context,
        )
        val scoped = MermaidEngine().render(
            """
            ---
            config:
              look: classic
              requirement:
                look: handDrawn
            ---
            requirementDiagram
              requirement req {
              }
            """.trimIndent(),
            context,
        )

        assertIs<GMResult.Err<MermaidError.UnsupportedFeature>>(global)
        assertIs<GMResult.Err<MermaidError.UnsupportedFeature>>(scoped)
    }

    @Test
    fun carriesTitleAndAccessibilityMetadata() {
        val scene = render(
            """
            ---
            title: Checkout requirements
            ---
            requirementDiagram
              accTitle: Checkout model
              accDescr: Payment requirements and evidence
              requirement checkout {
              }
            """.trimIndent(),
        )

        assertEquals("Checkout requirements", scene.title)
        assertEquals("Checkout model", scene.accessibilityTitle)
        assertEquals("Payment requirements and evidence", scene.accessibilityDescription)
    }

    @Test
    fun rejectsUndefinedRelationshipEndpointsAndResourceOverflow() {
        val undefined = MermaidEngine().render(
            "requirementDiagram\nknown - traces -> missing\n",
            context,
        )
        assertIs<GMResult.Err<MermaidError.Layout>>(undefined)

        val limited = MermaidEngine().render(
            """
            requirementDiagram
              requirement a {
              }
              requirement b {
              }
              a - traces -> b
            """.trimIndent(),
            context.copy(options = context.options.copy(maxEdges = 0)),
        )
        assertIs<GMResult.Err<MermaidError.ResourceLimit>>(limited)
    }

    private fun wrappedMetrics(request: TextMetricsRequest): TextMetrics {
        val widths = request.text.split('\n').map { it.length * request.fontSize * 0.5f }
        val lineCount = widths.sumOf { width ->
            ceil((width / request.maxWidth).toDouble()).toInt().coerceAtLeast(1)
        }
        return TextMetrics(
            width = minOf(request.maxWidth, widths.maxOrNull() ?: 0f),
            height = lineCount * request.fontSize * request.lineHeight,
            lineCount = lineCount,
        )
    }

    private fun render(
        source: String,
        renderContext: MermaidRenderContext = context,
    ): MermaidScene {
        val result = MermaidEngine().render(source, renderContext)
        return assertIs<GMResult.Ok<MermaidScene>>(
            result,
            "Expected Requirement render success:\n$source\n$result",
        ).value
    }

    private fun List<com.swithun.cmpmermaid.core.ScenePoint>.halfLengthPoint():
        com.swithun.cmpmermaid.core.ScenePoint {
        val lengths = zipWithNext { first, second ->
            hypot(second.x - first.x, second.y - first.y)
        }
        val target = lengths.sum() / 2f
        var traversed = 0f
        lengths.forEachIndexed { index, length ->
            if (traversed + length >= target && length > 0f) {
                val ratio = (target - traversed) / length
                return com.swithun.cmpmermaid.core.ScenePoint(
                    x = this[index].x + (this[index + 1].x - this[index].x) * ratio,
                    y = this[index].y + (this[index + 1].y - this[index].y) * ratio,
                )
            }
            traversed += length
        }
        return last()
    }
}
