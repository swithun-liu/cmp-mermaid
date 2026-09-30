package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.generated.InvalidSourceCorpusCase
import com.swithun.cmpmermaid.core.generated.StabilityCorpusCase
import com.swithun.cmpmermaid.core.generated.invalidSourceCasesPerDiagram
import com.swithun.cmpmermaid.core.generated.invalidSourceCasesPerMutation
import com.swithun.cmpmermaid.core.generated.invalidSourceCorpusCases
import com.swithun.cmpmermaid.core.generated.productionCorpusCases
import com.swithun.cmpmermaid.core.generated.visualParityCorpusCases
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class InvalidSourceCorpusTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = request.text.length * request.fontSize * 0.55f,
                height = request.fontSize * request.lineHeight,
            )
        },
    )

    @Test
    fun returnsStableResultsForAiLikeMutationsWithoutRuntimeErrors() {
        assertEquals(EXPECTED_DIAGRAM_IDS, invalidSourceCorpusCases.map { it.diagramId }.toSet())
        assertEquals(
            EXPECTED_DIAGRAM_IDS.size * invalidSourceCasesPerDiagram,
            invalidSourceCorpusCases.size,
        )
        assertEquals(
            invalidSourceCorpusCases.size,
            invalidSourceCorpusCases.map(InvalidSourceCorpusCase::id).toSet().size,
        )
        assertEquals(
            invalidSourceCorpusCases.size,
            invalidSourceCorpusCases.map(InvalidSourceCorpusCase::source).toSet().size,
        )
        invalidSourceCorpusCases.groupBy(InvalidSourceCorpusCase::diagramId)
            .forEach { (diagramId, cases) ->
                assertEquals(
                    invalidSourceCasesPerDiagram,
                    cases.size,
                    "$diagramId has the wrong malformed-source case count",
                )
                assertEquals(
                    (1..invalidSourceCasesPerDiagram).toSet(),
                    cases.map(InvalidSourceCorpusCase::caseIndex).toSet(),
                    "$diagramId has incomplete AI-mutation cases",
                )
                assertEquals(
                    invalidSourceCasesPerDiagram,
                    cases.map(InvalidSourceCorpusCase::baseCaseId).toSet().size,
                    "$diagramId does not use a distinct valid base for every mutation",
                )
                cases.groupBy(InvalidSourceCorpusCase::mutationId).forEach {
                    (mutationId, mutationCases) ->
                    assertEquals(
                        invalidSourceCasesPerMutation,
                        mutationCases.size,
                        "$diagramId/$mutationId has the wrong mutation count",
                    )
                }
            }

        val validSourcesById = visualParityCorpusCases.associateBy(
            StabilityCorpusCase::id,
        )
        invalidSourceCorpusCases.forEach { case ->
            assertEquals("oracle", case.expectedOutcome)
            assertNotEquals(
                validSourcesById.getValue(case.baseCaseId).source,
                case.source,
                "${case.id} did not modify its valid base source",
            )

            val first = engine.render(case.source, context)
            val second = engine.render(case.source, context)

            assertEquals(
                first,
                second,
                "${case.id} returned a non-deterministic mutation result",
            )
            if (first is GMResult.Err) {
                assertEquals(
                    MermaidRenderErrorType.CONTENT_ERROR,
                    first.error.renderErrorType,
                    "${case.id} converted an AI-like source mutation into " +
                        "${first.error.renderErrorType}",
                )
                assertTrue(
                    first.error.message.isNotBlank(),
                    "${case.id} returned an empty error message",
                )
            }
        }
    }

    @Test
    fun aiLikeMutationDoesNotPoisonFollowingValidRender() {
        val representatives = productionCorpusCases
            .groupBy(StabilityCorpusCase::diagramId)
            .mapValues { (_, cases) -> cases.first() }

        assertEquals(EXPECTED_DIAGRAM_IDS, representatives.keys)
        invalidSourceCorpusCases
            .filter { it.mutationVariant == 1 }
            .forEach { mutationCase ->
                engine.render(mutationCase.source, context)

                val validCase = representatives.getValue(mutationCase.diagramId)
                val validResult = engine.render(
                    source = validCase.source,
                    context = context.copy(
                        options = MermaidRenderOptions(layout = validCase.layout),
                    ),
                )
                assertIs<GMResult.Ok<MermaidScene>>(
                    validResult,
                    "${mutationCase.id} poisoned a following valid render: " +
                        "${(validResult as? GMResult.Err)?.error}",
                )
            }
    }

    private companion object {
        val EXPECTED_DIAGRAM_IDS: Set<String> = setOf(
            "flowchart",
            "swimlanes",
            "architecture",
            "c4",
            "railroad",
            "treeview",
            "xychart",
            "quadrant",
            "timeline",
            "kanban",
            "sequence",
            "class",
            "state",
            "er",
            "gantt",
            "pie",
            "journey",
            "requirement",
            "gitgraph",
            "mindmap",
            "packet",
            "radar",
            "sankey",
            "treemap",
            "venn",
            "ishikawa",
            "cynefin",
            "block",
            "eventmodeling",
            "agentflow",
            "usecase",
            "wardley",
            "zenuml",
        )
    }
}
