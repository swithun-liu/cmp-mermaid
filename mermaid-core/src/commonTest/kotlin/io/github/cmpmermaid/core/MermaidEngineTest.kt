package io.github.cmpmermaid.core

import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MermaidEngineTest {
    private val textMetrics = TextMetricProvider { request ->
        val charactersPerLine = (request.maxWidth / 8f).toInt().coerceAtLeast(1)
        val lineCount = ceil(request.text.length.toDouble() / charactersPerLine).toInt().coerceAtLeast(1)
        TextMetrics(
            width = minOf(request.maxWidth, request.text.length * 8f),
            height = lineCount * 18f,
        )
    }
    private val context = MermaidRenderContext(textMetrics)
    private val engine = MermaidEngine()

    @Test
    fun rendersCommonFlowchartSyntaxIntoSceneGraph() {
        val result = engine.render(
            """
                flowchart TB
                  subgraph intake [Question intake]
                    A([Student asks]) --> B{Enough context?}
                    B -- No --> C[Clarify]
                  end
                  B -- Yes --> D[(Answer cache)]
                  D --> E((Done))
                  classDef success fill:#DCFCE7,stroke:#15803D,color:#14532D
                  class E success
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val shapes = scene.elements.filterIsInstance<SceneShape>()
        val paths = scene.elements.filterIsInstance<ScenePath>()

        assertTrue(scene.width > 0f)
        assertTrue(scene.height > 0f)
        assertTrue(shapes.any { it.id == "A" && it.kind == SceneShapeKind.Stadium })
        assertTrue(shapes.any { it.id == "B" && it.kind == SceneShapeKind.Diamond })
        assertTrue(shapes.any { it.id == "D" && it.kind == SceneShapeKind.Cylinder })
        assertTrue(shapes.any { it.id == "E" && it.kind == SceneShapeKind.Circle })
        assertTrue(shapes.any { it.id == "subgraph_intake" })
        assertEquals(4, paths.size)
    }

    @Test
    fun laysOutLeftToRightWithoutNodeOverlap() {
        val result = engine.render(
            """
                graph LR
                  A[Parse] --> B[Layout] --> C[Render]
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val nodes = scene.elements
            .filterIsInstance<SceneShape>()
            .filter { it.id in setOf("A", "B", "C") }
            .associateBy(SceneShape::id)

        assertTrue(nodes.getValue("A").bounds.right < nodes.getValue("B").bounds.left)
        assertTrue(nodes.getValue("B").bounds.right < nodes.getValue("C").bounds.left)
    }

    @Test
    fun supportsFrontMatterAndExpandedShapeSyntax() {
        val result = engine.render(
            """
                ---
                title: Native diagram
                ---
                flowchart TB
                  A@{ shape: hex, label: "Validate" } --> B@{ shape: dbl-circ, label: "Done" }
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val nodes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        assertEquals(SceneShapeKind.Hexagon, nodes.getValue("A").kind)
        assertEquals(SceneShapeKind.DoubleCircle, nodes.getValue("B").kind)
    }

    @Test
    fun distinguishesParallelogramAndTrapezoidDelimiters() {
        val result = engine.render(
            """
                flowchart LR
                  A[/Input/] --> B[/Batch\]
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val nodes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        assertEquals(SceneShapeKind.Parallelogram, nodes.getValue("A").kind)
        assertEquals(SceneShapeKind.Trapezoid, nodes.getValue("B").kind)
    }

    @Test
    fun returnsStructuredErrorForUnsupportedDiagram() {
        val result = engine.render(
            """
                sequenceDiagram
                  Alice->>Bob: Hello
            """.trimIndent(),
            context,
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.UnsupportedDiagram>(error)
    }

    @Test
    fun returnsStructuredErrorForInvalidColor() {
        val result = engine.render(
            """
                flowchart TB
                  A --> B
                  style A fill:not-a-color
            """.trimIndent(),
            context,
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.Parse>(error)
    }

    @Test
    fun supportsMultiNodeChainsDirectClassesAndEdgeIds() {
        val result = engine.render(
            """
                flowchart LR
                  A:::source & B:::source e1@--> C & D
                  e1@{ animate: true }
                  classDef source fill:#e0f2fe,stroke:#0369a1
                  class e1 source
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val nodes = scene.elements.filterIsInstance<SceneShape>().filter { it.id in setOf("A", "B", "C", "D") }
        val paths = scene.elements.filterIsInstance<ScenePath>()
        assertEquals(4, nodes.size)
        assertEquals(4, paths.size)
        assertTrue(paths.all { it.id == "e1" })
    }

    @Test
    fun supportsLongInvisibleAndBidirectionalEdges() {
        val result = engine.render(
            """
                flowchart LR
                  A ----> B
                  B ~~~ C
                  C o--x D
                  D <==> E
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val paths = scene.elements.filterIsInstance<ScenePath>()
        assertEquals(3, paths.size)
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.Circle && it.arrowEnd == SceneArrowHead.Cross })
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.Triangle && it.arrowEnd == SceneArrowHead.Triangle })
    }

    @Test
    fun rendersEveryOfficialComparisonCase() {
        officialFlowchartCases.forEach { case ->
            val result = engine.render(case.source, context)
            val scene = assertIs<GMResult.Ok<MermaidScene>>(
                result,
                "Official comparison case '${case.id}' failed: $result",
            ).value
            assertTrue(scene.elements.filterIsInstance<SceneShape>().isNotEmpty(), case.id)
            assertTrue(scene.width > 0f && scene.height > 0f, case.id)
        }
    }
}
