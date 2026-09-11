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

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
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
    fun assignsExplicitMultiNodeEdgeIdOnlyToLastStartAndFirstEnd() {
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
        assertEquals(1, paths.count { it.id == "e1" })
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
    fun supportsMarkerCombinationsAcrossStrokeTypes() {
        val cases = listOf(
            "A x--x B" to Triple(SceneArrowHead.Cross, SceneArrowHead.Cross, SceneStrokePattern.Solid),
            "A o==o B" to Triple(SceneArrowHead.Circle, SceneArrowHead.Circle, SceneStrokePattern.Solid),
            "A <-.-> B" to Triple(SceneArrowHead.Triangle, SceneArrowHead.Triangle, SceneStrokePattern.Dotted),
            "A x-. label .-x B" to Triple(SceneArrowHead.Cross, SceneArrowHead.Cross, SceneStrokePattern.Dotted),
            "A o== label ==o B" to Triple(SceneArrowHead.Circle, SceneArrowHead.Circle, SceneStrokePattern.Solid),
            "A <-- label --> B" to Triple(SceneArrowHead.Triangle, SceneArrowHead.Triangle, SceneStrokePattern.Solid),
        )

        cases.forEach { (edgeSource, expected) ->
            val result = engine.render("flowchart LR\n  $edgeSource", context)
            val scene = assertIs<GMResult.Ok<MermaidScene>>(result, edgeSource).value
            val path = scene.elements.filterIsInstance<ScenePath>().single()
            assertEquals(expected.first, path.arrowStart, edgeSource)
            assertEquals(expected.second, path.arrowEnd, edgeSource)
            assertEquals(expected.third, path.strokePattern, edgeSource)
        }
    }

    @Test
    fun matchesMermaidDefaultEdgeWidths() {
        val result = engine.render(
            """
                flowchart LR
                  A --> B
                  B ==> C
            """.trimIndent(),
            context,
        )

        val paths = assertIs<GMResult.Ok<MermaidScene>>(result).value
            .elements
            .filterIsInstance<ScenePath>()

        assertEquals(1f, paths[0].strokeWidth)
        assertEquals(3.5f, paths[1].strokeWidth)
    }

    @Test
    fun balancesMinimumLengthSlackIntoOneMiddleLayer() {
        val result = engine.render(
            """
                flowchart TD
                  A --> B
                  A ---> C
                  A ----> D
                  B -.-> E
                  C -..-> E
                  D ====> E
            """.trimIndent(),
            context,
        )

        val nodes = assertIs<GMResult.Ok<MermaidScene>>(result).value
            .elements
            .filterIsInstance<SceneShape>()
            .filter { shape -> shape.id in setOf("A", "B", "C", "D", "E") }
            .associateBy(SceneShape::id)

        assertEquals(nodes.getValue("B").bounds.center.y, nodes.getValue("C").bounds.center.y)
        assertEquals(nodes.getValue("C").bounds.center.y, nodes.getValue("D").bounds.center.y)
        assertTrue(nodes.getValue("A").bounds.bottom < nodes.getValue("B").bounds.top)
        assertTrue(nodes.getValue("D").bounds.bottom < nodes.getValue("E").bounds.top)
    }

    @Test
    fun routesCrossedMultiNodeLinksThroughDistinctDoglegs() {
        val result = engine.render(
            """
                flowchart LR
                  A eAC@--> C
                  A eAD@--> D
                  B eBC@--> C
                  B eBD@--> D
            """.trimIndent(),
            context,
        )

        val paths = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
            .elements
            .filterIsInstance<ScenePath>()
            .associateBy(ScenePath::id)

        assertEquals(2, paths.getValue("eAC").points.size)
        assertEquals(2, paths.getValue("eBD").points.size)
        assertTrue(paths.getValue("eAD").points.size >= 4)
        assertTrue(paths.getValue("eBC").points.size >= 6)
        assertTrue(paths.getValue("eBC").bridges.isNotEmpty())
    }

    @Test
    fun keepsMixedEdgeLabelsSeparated() {
        val result = engine.render(
            """
                flowchart LR
                  A -- text --> B
                  A -->|pipe label| C
                  A -. dotted .-> D
                  A == thick ==> E
                  A -- open text --- F
            """.trimIndent(),
            context,
        )

        val labels = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
            .elements
            .filterIsInstance<SceneText>()
            .filter { text ->
                text.text in setOf("text", "pipe label", "dotted", "thick", "open text")
            }
            .sortedBy { text -> text.bounds.top }

        assertEquals(5, labels.size)
        labels.zipWithNext().forEach { (first, second) ->
            assertTrue(first.bounds.bottom <= second.bounds.top, "${first.text} overlaps ${second.text}")
        }
    }

    @Test
    fun supportsCssColorsAndTextStyles() {
        val result = engine.render(
            """
                flowchart LR
                  A --> B
                  style A fill:rgba(255,0,0,0.5),stroke:hsl(120,100%,25%),color:white,stroke-dasharray:5 5,font-size:20px,font-weight:bold
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val shape = scene.elements.filterIsInstance<SceneShape>().first { it.id == "A" }
        val text = scene.elements.filterIsInstance<SceneText>().first { it.text == "A" }
        assertEquals(SceneColor(0x80FF0000), shape.fill)
        assertEquals(SceneColor(0xFF008000), shape.stroke)
        assertEquals(SceneStrokePattern.Dashed, shape.strokePattern)
        assertEquals(20f, text.fontSize)
        assertEquals(SceneTextWeight.Bold, text.weight)
    }

    @Test
    fun routesParallelEdgesOnDistinctLanesWithoutLabelOverlap() {
        val result = engine.render(
            """
                flowchart LR
                  A -->|primary| B
                  A -.->|retry| B
                  A ==>|priority| B
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val labels = scene.elements
            .filterIsInstance<SceneText>()
            .filter { it.text in setOf("primary", "retry", "priority") }
            .sortedBy { it.bounds.top }

        assertEquals(3, labels.size)
        labels.zipWithNext().forEach { (first, second) ->
            assertTrue(
                first.bounds.bottom <= second.bounds.top,
                "${first.text} overlaps ${second.text}",
            )
        }
    }

    @Test
    fun placesHorizontalSelfLoopAboveItsNode() {
        val result = engine.render(
            """
                flowchart LR
                  A --> A
                  A --> B
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val node = scene.elements
            .filterIsInstance<SceneShape>()
            .first { shape -> shape.id == "A" }
        val loop = scene.elements
            .filterIsInstance<ScenePath>()
            .first { path -> path.id == "edge_0" }

        assertEquals(4, loop.points.size)
        assertEquals(node.bounds.top, loop.points.first().y)
        assertEquals(node.bounds.top, loop.points.last().y)
        assertTrue(loop.points.drop(1).dropLast(1).all { point -> point.y < node.bounds.top })
    }

    @Test
    fun addsBridgeToLaterPathAtOrthogonalCrossing() {
        val result = engine.render(
            """
                flowchart TB
                  Start --> A & B & C
                  A --> D
                  B --> E
                  C --> F
                  A --> F
                  C --> D
                  D & E & F --> Finish
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val paths = scene.elements.filterIsInstance<ScenePath>()
        assertTrue(
            paths.any { it.bridges.isNotEmpty() },
            "Expected at least one routed crossing to contain a bridge: " +
                paths.joinToString { "${it.id}=${it.points}" },
        )
    }

    @Test
    fun connectsEdgesToExpandedSubgraphBoundsWithoutFakeNodes() {
        val result = engine.render(
            """
                flowchart LR
                  Start --> group
                  subgraph group [Validation]
                    A --> B
                  end
                  group --> Finish
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val shapes = scene.elements.filterIsInstance<SceneShape>()
        assertTrue(shapes.any { it.id == "subgraph_group" })
        assertTrue(shapes.none { it.id == "group" })
        assertEquals(3, scene.elements.filterIsInstance<ScenePath>().size)
    }

    @Test
    fun honorsExplicitDirectionsInNestedSubgraphs() {
        val result = engine.render(
            """
                flowchart LR
                  subgraph TOP
                    direction TB
                    subgraph B1
                      direction RL
                      i1 --> f1
                    end
                    subgraph B2
                      direction BT
                      i2 --> f2
                    end
                  end
                  A --> TOP --> B
                  B1 --> B2
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val top = shapes.getValue("subgraph_TOP").bounds
        val firstGroup = shapes.getValue("subgraph_B1").bounds
        val secondGroup = shapes.getValue("subgraph_B2").bounds

        assertTrue(shapes.getValue("A").bounds.right < top.left)
        assertTrue(top.right < shapes.getValue("B").bounds.left)
        assertTrue(firstGroup.bottom < secondGroup.top)
        assertTrue(shapes.getValue("f1").bounds.right < shapes.getValue("i1").bounds.left)
        assertTrue(shapes.getValue("f2").bounds.bottom < shapes.getValue("i2").bounds.top)

        val paths = scene.elements.filterIsInstance<ScenePath>().associateBy(ScenePath::id)
        assertTrue(paths.getValue("edge_0").points.first().x > paths.getValue("edge_0").points.last().x)
        assertTrue(paths.getValue("edge_1").points.first().y > paths.getValue("edge_1").points.last().y)
        assertTrue(paths.getValue("edge_4").points.first().y < paths.getValue("edge_4").points.last().y)
    }

    @Test
    fun collapsedSubgraphBecomesSingleNodeAndRedirectsBoundaryEdges() {
        val result = engine.render(
            """
                flowchart LR
                  Start --> group
                  subgraph group [Validation]
                    A --> B --> C
                  end
                  group --> Finish
                  group@{ view: collapsed }
            """.trimIndent(),
            context,
        )

        val scene = assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
        val shapes = scene.elements.filterIsInstance<SceneShape>()
        assertTrue(shapes.any { it.id == "group" && it.kind == SceneShapeKind.CollapsedGroup })
        assertTrue(shapes.none { it.id in setOf("A", "B", "C", "subgraph_group") })
        assertEquals(2, scene.elements.filterIsInstance<ScenePath>().size)
    }

    @Test
    fun rendersEveryOfficialComparisonCase() {
        val failures = officialFlowchartCases.mapNotNull { case ->
            when (val result = engine.render(case.source, context)) {
                is GMResult.Ok -> when {
                    result.value.elements.filterIsInstance<SceneShape>().isEmpty() ->
                        "${case.id}: no shapes"
                    result.value.width <= 0f || result.value.height <= 0f ->
                        "${case.id}: invalid scene bounds"
                    else -> null
                }
                is GMResult.Err -> "${case.id}: ${result.error.message}"
            }
        }

        assertTrue(
            failures.isEmpty(),
            failures.joinToString(separator = "\n", prefix = "Official cases failed:\n"),
        )
    }
}
