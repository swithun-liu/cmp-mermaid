package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.generated.StabilityCorpusCase
import com.swithun.cmpmermaid.core.generated.productionCorpusCases
import com.swithun.cmpmermaid.core.generated.visualStressCorpusSamples
import com.swithun.cmpmermaid.core.generated.visualStressSampleCount
import com.swithun.cmpmermaid.core.generated.visualStressSamplesPerDiagram
import com.swithun.cmpmermaid.core.generated.visualStressStructuralScenarioCount
import com.swithun.cmpmermaid.core.generated.visualStressStructuralScenarioCountsByDiagram
import com.swithun.cmpmermaid.core.generated.visualStructureCaseCount
import com.swithun.cmpmermaid.core.generated.visualStructureCasesPerDiagram
import com.swithun.cmpmermaid.core.generated.visualStructureCorpusCases
import kotlin.math.ceil
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ProductionCorpusTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            val charactersPerLine = (request.maxWidth / (request.fontSize * 0.55f))
                .toInt()
                .coerceAtLeast(1)
            val lineCount = request.text.lineSequence().sumOf { line ->
                ceil(line.length.toDouble() / charactersPerLine)
                    .toInt()
                    .coerceAtLeast(1)
            }
            TextMetrics(
                width = min(
                    request.maxWidth,
                    request.text.lineSequence()
                        .maxOfOrNull(String::length)
                        .orZero() * request.fontSize * 0.55f,
                ),
                height = lineCount * request.fontSize * request.lineHeight,
            )
        },
    )

    @Test
    fun rendersEveryProductionCaseWithValidSceneGeometryAndExpectedText() {
        assertEquals(441, productionCorpusCases.size)
        assertEquals(
            mapOf(
                "flowchart" to 14,
                "swimlanes" to 13,
                "architecture" to 13,
                "c4" to 13,
                "railroad" to 18,
                "treeview" to 13,
                "xychart" to 13,
                "quadrant" to 13,
                "timeline" to 13,
                "kanban" to 13,
                "sequence" to 14,
                "class" to 13,
                "state" to 13,
                "er" to 13,
                "gantt" to 13,
                "pie" to 13,
                "journey" to 13,
                "requirement" to 13,
                "gitgraph" to 13,
                "mindmap" to 13,
                "packet" to 13,
                "radar" to 13,
                "sankey" to 13,
                "treemap" to 13,
                "venn" to 13,
                "ishikawa" to 13,
                "cynefin" to 13,
                "block" to 13,
                "eventmodeling" to 13,
                "agentflow" to 13,
                "usecase" to 18,
                "wardley" to 13,
                "zenuml" to 13,
            ),
            productionCorpusCases.groupingBy(StabilityCorpusCase::diagramId).eachCount(),
        )
        val conformanceCases = productionCorpusCases.filter { case ->
            case.id.startsWith("prod_")
        }
        assertEquals(274, conformanceCases.size)
        assertTrue(conformanceCases.all { case -> case.expectedTexts.isNotEmpty() })
        assertTrue(conformanceCases.all { case -> case.features.isNotEmpty() })

        productionCorpusCases.forEach { case ->
            val scene = render(case)

            assertTrue(
                scene.width.isFinite() && scene.width in 1f..MAX_SCENE_SIZE,
                "${case.id} produced invalid width ${scene.width}",
            )
            assertTrue(
                scene.height.isFinite() && scene.height in 1f..MAX_SCENE_SIZE,
                "${case.id} produced invalid height ${scene.height}",
            )
            assertTrue(
                scene.elements.size in 1..MAX_SCENE_ELEMENTS,
                "${case.id} produced ${scene.elements.size} elements",
            )
            scene.elements.forEach { element ->
                assertFiniteGeometry(case.id, element)
            }

            val renderedText = buildList {
                scene.title?.let(::add)
                scene.accessibilityTitle?.let(::add)
                scene.accessibilityDescription?.let(::add)
                scene.elements.filterIsInstance<SceneText>().mapTo(this, SceneText::text)
            }
            case.expectedTexts.forEach { expectedText ->
                assertTrue(
                    renderedText.any { text ->
                        expectedText in text.replace(WHITESPACE, " ")
                    },
                    "${case.id} did not render expected text '$expectedText'; " +
                        "actual=$renderedText",
                )
            }
        }
    }

    @Test
    fun tracksTextLayoutStressSamplesWithoutCountingThemAsStructuralCoverage() {
        assertEquals(441, visualStressStructuralScenarioCount)
        assertEquals(8_448, visualStressSampleCount)
        assertEquals(visualStressSampleCount, visualStressCorpusSamples.size)
        assertEquals(
            visualStressStructuralScenarioCountsByDiagram.keys
                .associateWith { visualStressSamplesPerDiagram },
            visualStressCorpusSamples
                .groupingBy(StabilityCorpusCase::diagramId)
                .eachCount(),
        )
        assertEquals(
            visualStressSampleCount,
            visualStressCorpusSamples.map(StabilityCorpusCase::source).toSet().size,
        )
        assertEquals(
            visualStressStructuralScenarioCount,
            visualStressCorpusSamples
                .flatMap(StabilityCorpusCase::features)
                .filter { feature -> feature.startsWith(STRUCTURAL_SCENARIO_PREFIX) }
                .mapTo(mutableSetOf()) { feature ->
                    feature.removePrefix(STRUCTURAL_SCENARIO_PREFIX)
                }
                .size,
        )
    }

    @Test
    fun renders256IndependentScenariosForEveryDiagramFamily() {
        assertEquals(8_448, visualStructureCaseCount)
        assertEquals(visualStructureCaseCount, visualStructureCorpusCases.size)
        assertEquals(
            productionCorpusCases
                .map(StabilityCorpusCase::diagramId)
                .toSet()
                .associateWith { visualStructureCasesPerDiagram },
            visualStructureCorpusCases
                .groupingBy(StabilityCorpusCase::diagramId)
                .eachCount(),
        )
        assertTrue(
            visualStructureCorpusCases.all { case ->
                INDEPENDENT_SCENARIO_FEATURE in case.features
            },
        )
        visualStructureCorpusCases
            .groupBy(StabilityCorpusCase::diagramId)
            .forEach { (diagramId, cases) ->
                assertEquals(
                    visualStructureCasesPerDiagram,
                    cases.map(StabilityCorpusCase::title).toSet().size,
                    "$diagramId scenario titles",
                )
            }
        assertEquals(
            visualStructureCaseCount,
            visualStructureCorpusCases.map(StabilityCorpusCase::source).toSet().size,
        )
        assertEquals(
            visualStructureCaseCount,
            visualStructureCorpusCases
                .map { case ->
                    case.features.single { feature ->
                        feature.startsWith(STRUCTURAL_SIGNATURE_PREFIX)
                    }
                }
                .toSet()
                .size,
        )

        val renderFailures = mutableListOf<String>()
        visualStructureCorpusCases.forEach { case ->
            when (
                val result = engine.render(
                    source = case.source,
                    context = context.copy(
                        options = MermaidRenderOptions(layout = case.layout),
                    ),
                )
            ) {
                is GMResult.Err -> renderFailures += "${case.id}: ${result.error}"
                is GMResult.Ok -> {
                    val scene = result.value
                    assertTrue(
                        scene.width.isFinite() && scene.width in 1f..MAX_SCENE_SIZE,
                        "${case.id} produced invalid width ${scene.width}",
                    )
                    assertTrue(
                        scene.height.isFinite() && scene.height in 1f..MAX_SCENE_SIZE,
                        "${case.id} produced invalid height ${scene.height}",
                    )
                    assertTrue(
                        scene.elements.size in 1..MAX_SCENE_ELEMENTS,
                        "${case.id} produced ${scene.elements.size} elements",
                    )
                    scene.elements.forEach { element ->
                        assertFiniteGeometry(case.id, element)
                    }

                    val renderedText = buildList {
                        scene.title?.let(::add)
                        scene.accessibilityTitle?.let(::add)
                        scene.accessibilityDescription?.let(::add)
                        scene.elements
                            .filterIsInstance<SceneText>()
                            .mapTo(this, SceneText::text)
                    }
                    case.expectedTexts.forEach { expectedText ->
                        if (
                            renderedText.none { text ->
                                expectedText in text.replace(WHITESPACE, " ")
                            }
                        ) {
                            renderFailures +=
                                "${case.id}: missing '$expectedText'; actual=$renderedText"
                        }
                    }
                }
            }
        }
        assertTrue(
            renderFailures.isEmpty(),
            renderFailures.joinToString(
                prefix = "${renderFailures.size} structural cases failed:\n",
                separator = "\n",
            ),
        )
    }

    @Test
    fun rendersProductionCorpusDeterministically() {
        productionCorpusCases.forEach { case ->
            assertEquals(
                expected = render(case),
                actual = render(case),
                message = "${case.id} produced a non-deterministic SceneGraph",
            )
        }
    }

    @Test
    fun rendersEveryDiagramTypeAcrossEveryBuiltInTheme() {
        val representatives = productionCorpusCases
            .filter { case -> case.features.isNotEmpty() }
            .groupBy(StabilityCorpusCase::diagramId)
            .mapValues { (_, cases) -> cases.first() }

        assertEquals(33, representatives.size)
        representatives.forEach { (diagramId, case) ->
            MermaidThemePreset.entries.forEach { preset ->
                val result = engine.render(
                    source = case.source,
                    context = context.copy(
                        theme = MermaidTheme.preset(preset),
                        options = MermaidRenderOptions(layout = case.layout),
                    ),
                )
                val scene = assertIs<GMResult.Ok<MermaidScene>>(
                    result,
                    "$diagramId/${preset.configName} failed with " +
                        "${(result as? GMResult.Err)?.error}",
                ).value
                assertTrue(
                    scene.elements.isNotEmpty(),
                    "$diagramId/${preset.configName} produced an empty scene",
                )
            }
        }
    }

    private fun render(case: StabilityCorpusCase): MermaidScene {
        val result = engine.render(
            source = case.source,
            context = context.copy(
                options = MermaidRenderOptions(layout = case.layout),
            ),
        )
        return assertIs<GMResult.Ok<MermaidScene>>(
            result,
            "${case.id} failed with ${(result as? GMResult.Err)?.error}:\n${case.source}",
        ).value
    }

    private fun assertFiniteGeometry(
        caseId: String,
        element: SceneElement,
    ) {
        when (element) {
            is SceneAsset -> assertFiniteRect(caseId, element.id, element.bounds)
            is SceneShape -> {
                assertFiniteRect(caseId, element.id, element.bounds)
                element.geometry?.paths.orEmpty().forEachIndexed { index, path ->
                    path.points.forEach { point ->
                        assertFinitePoint(caseId, "${element.id}.shapePath[$index]", point)
                    }
                }
                element.geometry?.outline.orEmpty().forEach { point ->
                    assertFinitePoint(caseId, "${element.id}.outline", point)
                }
            }
            is SceneText -> assertFiniteRect(caseId, "text:${element.text}", element.bounds)
            is ScenePath -> {
                assertTrue(
                    element.strokeWidth.isFinite() && element.strokeWidth >= 0f,
                    "$caseId/${element.id} has invalid stroke width ${element.strokeWidth}",
                )
                element.points.forEach { point ->
                    assertFinitePoint(caseId, element.id, point)
                }
                element.commands.forEach { command ->
                    when (command) {
                        is ScenePathCommand.MoveTo ->
                            assertFinitePoint(caseId, element.id, command.point)
                        is ScenePathCommand.LineTo ->
                            assertFinitePoint(caseId, element.id, command.point)
                        is ScenePathCommand.QuadraticTo -> {
                            assertFinitePoint(caseId, element.id, command.control)
                            assertFinitePoint(caseId, element.id, command.end)
                        }
                        is ScenePathCommand.CubicTo -> {
                            assertFinitePoint(caseId, element.id, command.control1)
                            assertFinitePoint(caseId, element.id, command.control2)
                            assertFinitePoint(caseId, element.id, command.end)
                        }
                        is ScenePathCommand.ArcTo -> {
                            assertTrue(
                                command.radius.isFinite() && command.radius >= 0f,
                                "$caseId/${element.id} has invalid arc radius ${command.radius}",
                            )
                            assertFinitePoint(caseId, element.id, command.end)
                        }
                    }
                }
            }
        }
    }

    private fun assertFiniteRect(
        caseId: String,
        elementId: String,
        rect: SceneRect,
    ) {
        assertTrue(
            rect.left.isFinite() &&
                rect.top.isFinite() &&
                rect.right.isFinite() &&
                rect.bottom.isFinite() &&
                rect.right >= rect.left &&
                rect.bottom >= rect.top,
            "$caseId/$elementId has invalid bounds $rect",
        )
    }

    private fun assertFinitePoint(
        caseId: String,
        elementId: String,
        point: ScenePoint,
    ) {
        assertTrue(
            point.x.isFinite() && point.y.isFinite(),
            "$caseId/$elementId has invalid point $point",
        )
    }

    private fun Int?.orZero(): Int = this ?: 0

    private companion object {
        const val INDEPENDENT_SCENARIO_FEATURE = "independent-scenario-blueprint"
        const val STRUCTURAL_SCENARIO_PREFIX = "seed:"
        const val STRUCTURAL_SIGNATURE_PREFIX = "structural-signature:"
        const val MAX_SCENE_SIZE = 20_000f
        const val MAX_SCENE_ELEMENTS = 20_000
        val WHITESPACE = Regex("""\s+""")
    }
}
