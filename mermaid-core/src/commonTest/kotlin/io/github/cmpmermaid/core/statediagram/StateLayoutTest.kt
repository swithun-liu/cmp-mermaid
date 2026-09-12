package io.github.cmpmermaid.core.statediagram

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidEngine
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.MermaidTheme
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricProvider
import io.github.cmpmermaid.core.TextMetrics
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StateLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = metrics(),
        theme = MermaidTheme.FlowchartDefault,
        options = MermaidRenderOptions(look = "neo"),
    )

    @Test
    fun rendersStartEndChoiceAndForkJoinShapes() {
        val scene = render(
            """
            stateDiagram-v2
                state branch <<choice>>
                state split <<fork>>
                state merge <<join>>
                [*] --> split
                split --> branch
                branch --> merge : accepted
                merge --> [*]
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)

        assertEquals(SceneShapeKind.SmallCircle, shapes.getValue("root_start").kind)
        assertEquals(SceneShapeKind.FramedCircle, shapes.getValue("root_end").kind)
        assertEquals(SceneShapeKind.Diamond, shapes.getValue("branch").kind)
        assertEquals(SceneShapeKind.ForkJoin, shapes.getValue("split").kind)
        assertEquals(SceneShapeKind.ForkJoin, shapes.getValue("merge").kind)
        assertTrue(
            scene.elements.filterIsInstance<ScenePath>()
                .filterNot { it.id.endsWith("-label-background") }
                .all { it.arrowEnd == SceneArrowHead.Triangle },
        )
    }

    @Test
    fun rendersDescriptionCompartmentsAndMarkdown() {
        val scene = render(
            """
            stateDiagram-v2
                Service : **Ready**
                Service : _Waiting_
                Service --> Done : **finish**
            """.trimIndent(),
        )
        val state = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id == "Service" }
        val text = scene.elements.filterIsInstance<SceneText>()

        assertEquals(2, state.geometry?.paths?.size)
        assertTrue(text.single { it.text == "Ready" }.spans.any {
            it.weight == SceneTextWeight.Bold
        })
        assertTrue(text.single { it.text == "Waiting" }.spans.any { it.italic })
        assertTrue(text.single { it.text == "finish" }.spans.any {
            it.weight == SceneTextWeight.Bold
        })
    }

    @Test
    fun rendersNestedCompositeAndConcurrencyRegions() {
        val scene = render(
            """
            stateDiagram-v2
                state Active {
                    [*] --> First
                    First --> [*]
                    --
                    [*] --> Second
                    Second --> [*]
                }
            """.trimIndent(),
        )
        val groups = scene.elements.filterIsInstance<SceneShape>()
            .filter { it.id.startsWith("state-group-") }

        assertTrue(groups.any { it.id == "state-group-Active" })
        assertEquals(
            2,
            groups.count {
                it.id.startsWith("state-group-divider") &&
                    it.strokePattern == SceneStrokePattern.Dashed
            },
        )
    }

    @Test
    fun rendersNotesWithUpstreamDirectedDashedConnectors() {
        val scene = render(
            """
            stateDiagram-v2
                One : First state
                note left of One : Left note
                Two : Second state
                note right of Two : Right note
                One --> Two
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val leftNote = shapes.values.single { it.id.startsWith("One----note") }
        val rightNote = shapes.values.single { it.id.startsWith("Two----note") }
        val dashedPaths = scene.elements.filterIsInstance<ScenePath>()
            .filter { it.strokePattern == SceneStrokePattern.Dashed }

        assertEquals(2, dashedPaths.size)
        assertTrue(dashedPaths.any { it.id == "${leftNote.id}-One" })
        assertTrue(dashedPaths.any { it.id == "Two-${rightNote.id}" })
    }

    @Test
    fun appliesStylesMetadataAndLinks() {
        val scene = render(
            """
            ---
            title: Order lifecycle
            ---
            stateDiagram-v2
                accTitle: Order state diagram
                accDescr: Tracks an order from draft to completion
                classDef active fill:#dcfce7,stroke:#16a34a,color:#14532d
                Draft:::active --> Done
                click Draft href "https://example.com" 
            """.trimIndent(),
        )
        val draft = scene.elements.filterIsInstance<SceneShape>().single { it.id == "Draft" }

        assertEquals(0xFFDCFCE7, draft.fill.argb)
        assertEquals(0xFF16A34A, draft.stroke.argb)
        assertEquals("Order lifecycle", scene.title)
        assertEquals("Order state diagram", scene.accessibilityTitle)
        assertEquals("Tracks an order from draft to completion", scene.accessibilityDescription)
        assertEquals("https://example.com", scene.interactions.single().link)
    }

    @Test
    fun rejectsStateKatexInsteadOfRenderingLiteralMarkup() {
        val result = engine.render(
            """
            stateDiagram-v2
                Math : \(x^2\)
            """.trimIndent(),
            context,
        )

        val error = assertIs<MermaidError.UnsupportedFeature>(
            assertIs<GMResult.Err<MermaidError>>(result).error,
        )
        assertEquals("KaTeX label", error.feature)
    }

    private fun render(
        source: String,
        options: MermaidRenderOptions = context.options,
    ): MermaidScene {
        val result = engine.render(source, context.copy(options = options))
        return assertIs<GMResult.Ok<MermaidScene>>(
            result,
            "Expected State render success:\n$source\n$result",
        ).value
    }

    private fun metrics(): TextMetricProvider = TextMetricProvider { request ->
        val charactersPerLine = (request.maxWidth / 8f).toInt().coerceAtLeast(1)
        val lines = request.text
            .split('\n')
            .sumOf { line ->
                ceil(line.length.toDouble() / charactersPerLine)
                    .toInt()
                    .coerceAtLeast(1)
            }
        TextMetrics(
            width = minOf(
                request.maxWidth,
                request.text.lineSequence().maxOfOrNull(String::length).orZero() * 8f,
            ),
            height = lines * request.fontSize * request.lineHeight,
        )
    }

    private fun Int?.orZero(): Int = this ?: 0
}
