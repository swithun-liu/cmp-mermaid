package com.swithun.cmpmermaid.core

import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FlowElkLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            val charactersPerLine = (request.maxWidth / 8f).toInt().coerceAtLeast(1)
            val lineCount = ceil(
                request.text.length.toDouble() / charactersPerLine,
            ).toInt().coerceAtLeast(1)
            TextMetrics(
                width = minOf(request.maxWidth, request.text.length * 8f),
                height = lineCount * 18f,
            )
        },
        options = MermaidRenderOptions(layout = "elk"),
    )

    @Test
    fun laysOutSimpleLeftToRightFlowchart() {
        val scene = render(
            """
                flowchart LR
                  A[Parse] --> B[Layout] --> C[Render]
            """.trimIndent(),
        )
        val nodes = scene.nodeShapes()
        val paths = scene.elements.filterIsInstance<ScenePath>()

        assertTrue(nodes.getValue("A").bounds.right < nodes.getValue("B").bounds.left)
        assertTrue(nodes.getValue("B").bounds.right < nodes.getValue("C").bounds.left)
        assertTrue(paths.size == 2)
        assertTrue(paths.all { path -> path.points.size >= 2 })
    }

    @Test
    fun preservesVisibleOrderForMultiNodeLinks() {
        val scene = render(
            """
                flowchart LR
                  A:::source & B:::source --> C & D
                  classDef source fill:#e0f2fe,stroke:#0369a1
            """.trimIndent(),
        )
        val nodes = scene.nodeShapes()

        assertTrue(nodes.getValue("A").bounds.top < nodes.getValue("B").bounds.top)
        assertTrue(nodes.getValue("C").bounds.top < nodes.getValue("D").bounds.top)
    }

    @Test
    fun mapsNestedSubgraphCoordinatesToSceneCoordinates() {
        val scene = render(
            """
                flowchart TB
                  subgraph outer [Outer group]
                    subgraph inner [Inner group]
                      A[Start] --> B[Finish]
                    end
                  end
            """.trimIndent(),
        )
        val shapes = scene.nodeShapes()
        val outer = shapes.getValue("subgraph_outer").bounds
        val inner = shapes.getValue("subgraph_inner").bounds
        val first = shapes.getValue("A").bounds
        val second = shapes.getValue("B").bounds

        assertTrue(outer.contains(inner))
        assertTrue(inner.contains(first))
        assertTrue(inner.contains(second))
    }

    @Test
    fun routesEdgeAcrossSiblingSubgraphs() {
        val scene = render(
            """
                flowchart LR
                  subgraph left [Left]
                    A[Start]
                  end
                  subgraph right [Right]
                    B[Finish]
                  end
                  A -- transfer --> B
            """.trimIndent(),
        )
        val shapes = scene.nodeShapes()
        val path = scene.elements.filterIsInstance<ScenePath>().single()

        assertTrue(path.points.size >= 2)
        assertTrue(path.points.first().x >= shapes.getValue("A").bounds.left)
        assertTrue(path.points.last().x <= shapes.getValue("B").bounds.right)
        assertTrue(path.points.any { point -> point.x > shapes.getValue("subgraph_left").bounds.right })
    }

    @Test
    fun preservesElkSelfLoopRoute() {
        val scene = render(
            """
                flowchart TB
                  A[Retry] --> A
            """.trimIndent(),
        )
        val node = scene.nodeShapes().getValue("A").bounds
        val path = scene.elements.filterIsInstance<ScenePath>().single()

        assertTrue(path.points.size >= 4)
        assertTrue(
            path.points.any { point ->
                point.x < node.left || point.x > node.right ||
                    point.y < node.top || point.y > node.bottom
            },
        )
    }

    @Test
    fun runsEveryMermaid12RootElkAlgorithm() {
        val source = """
            flowchart LR
              A[Parse] --> B[Layout] --> C[Render]
        """.trimIndent()
        val layouts = listOf(
            "elk",
            "elk.stress",
            "elk.force",
            "elk.mrtree",
            "elk.sporeOverlap",
            "elk.box",
            "elk.rectpacking",
        )

        layouts.forEach { layout ->
            val scene = render(source, layout)

            assertEquals(3, scene.nodeShapes().size, layout)
            assertEquals(2, scene.elements.filterIsInstance<ScenePath>().size, layout)
        }
    }

    @Test
    fun acceptsEveryMermaid12ElkStrategyValue() {
        val source = """
            flowchart LR
              A --> B
              A --> C
              B --> D
              C --> D
        """.trimIndent()
        val variants = buildList {
            listOf("default", "legacy", "modelOrder", "depthFirst").forEach { value ->
                add(MermaidElkOptions(preset = value))
            }
            listOf("SIMPLE", "NETWORK_SIMPLEX", "LINEAR_SEGMENTS", "BRANDES_KOEPF")
                .forEach { value ->
                    add(MermaidElkOptions(nodePlacementStrategy = value))
                }
            listOf("NONE", "LEFTUP", "LEFTDOWN", "RIGHTUP", "RIGHTDOWN", "BALANCED")
                .forEach { value ->
                    add(MermaidElkOptions(nodePlacementAlignment = value))
                }
            listOf(
                "NETWORK_SIMPLEX",
                "LONGEST_PATH",
                "LONGEST_PATH_SOURCE",
                "COFFMAN_GRAHAM",
                "MIN_WIDTH",
                "STRETCH_WIDTH",
                "INTERACTIVE",
            ).forEach { value ->
                add(MermaidElkOptions(layeringStrategy = value))
            }
            listOf(
                "GREEDY",
                "DEPTH_FIRST",
                "INTERACTIVE",
                "MODEL_ORDER",
                "GREEDY_MODEL_ORDER",
            ).forEach { value ->
                add(MermaidElkOptions(cycleBreakingStrategy = value))
            }
            listOf("NONE", "NODES_AND_EDGES", "PREFER_EDGES", "PREFER_NODES")
                .forEach { value ->
                    add(MermaidElkOptions(considerModelOrder = value))
                }
            add(MermaidElkOptions(mergeEdges = true))
            add(MermaidElkOptions(forceNodeModelOrder = true))
            add(MermaidElkOptions(keepEntryNodeOnTop = true))
        }

        variants.forEach { elk ->
            val scene = render(
                source = source,
                options = context.options.copy(elk = elk),
            )

            assertEquals(4, scene.nodeShapes().size, elk.toString())
            assertEquals(4, scene.elements.filterIsInstance<ScenePath>().size, elk.toString())
        }
    }

    @Test
    fun laysOutGraphAtDefaultEdgeLimit() {
        val source = buildString {
            appendLine("flowchart LR")
            repeat(context.options.maxEdges) { index ->
                appendLine("N$index --> N${index + 1}")
            }
        }

        val scene = render(source)

        assertEquals(context.options.maxEdges + 1, scene.nodeShapes().size)
        assertEquals(
            context.options.maxEdges,
            scene.elements.filterIsInstance<ScenePath>().size,
        )
    }

    private fun render(
        source: String,
        layout: String = "elk",
    ): MermaidScene = render(
        source = source,
        options = context.options.copy(layout = layout),
    )

    private fun render(
        source: String,
        options: MermaidRenderOptions,
    ): MermaidScene {
        val result = engine.render(
            source,
            context.copy(options = options),
        )
        return assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
    }

    private fun MermaidScene.nodeShapes(): Map<String, SceneShape> =
        elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)

    private fun SceneRect.contains(other: SceneRect): Boolean =
        left <= other.left &&
            top <= other.top &&
            right >= other.right &&
            bottom >= other.bottom
}
