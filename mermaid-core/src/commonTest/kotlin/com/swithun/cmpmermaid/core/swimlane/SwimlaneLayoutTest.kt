package com.swithun.cmpmermaid.core.swimlane

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SwimlaneLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = minOf(request.maxWidth, request.text.length * request.fontSize * 0.55f),
                height = request.fontSize * request.lineHeight,
            )
        },
    )

    @Test
    fun rendersTopToBottomLanesAsAlignedColumns() {
        val scene = render(
            """
                swimlane-beta TB
                  subgraph Customer
                    request[Request]
                    receive[Receive]
                  end
                  subgraph Support
                    triage[Triage]
                    answer[Answer]
                  end
                  request --> triage --> answer --> receive
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val customer = shapes.getValue("subgraph_Customer")
        val support = shapes.getValue("subgraph_Support")

        assertEquals(customer.bounds.height, support.bounds.height, 0.01f)
        assertEquals(customer.bounds.center.y, support.bounds.center.y, 0.01f)
        assertTrue(customer.bounds.right <= support.bounds.left + 0.01f)
        assertTrue(shapes.getValue("request").bounds.bottom < shapes.getValue("receive").bounds.top)
    }

    @Test
    fun rendersLeftToRightLanesAsAlignedRowsWithRotatedTitles() {
        val scene = render(
            """
                swimlane-beta LR
                  subgraph Customer
                    request[Request]
                    receive[Receive]
                  end
                  subgraph Support
                    triage[Triage]
                    answer[Answer]
                  end
                  request --> triage --> answer --> receive
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val customer = shapes.getValue("subgraph_Customer")
        val support = shapes.getValue("subgraph_Support")
        val titles = scene.elements.filterIsInstance<SceneText>()
            .filter { it.text in setOf("Customer", "Support") }

        assertEquals(customer.bounds.width, support.bounds.width, 0.01f)
        assertEquals(customer.bounds.center.x, support.bounds.center.x, 0.01f)
        assertTrue(customer.bounds.bottom <= support.bounds.top + 0.01f)
        assertTrue(shapes.getValue("request").bounds.right < shapes.getValue("receive").bounds.left)
        assertTrue(titles.all { it.rotationDegrees == -90f })
        titles.forEach { title ->
            val lane = shapes.getValue("subgraph_${title.text}")
            val pivot = assertNotNull(title.rotationPivot)
            assertEquals(lane.bounds.center.x, pivot.x, 0.01f)
            assertEquals(lane.bounds.center.y, pivot.y, 0.01f)
        }
    }

    @Test
    fun synthesizesDefaultLaneForLooseNodes() {
        val scene = render(
            """
                swimlane-beta
                  A[Loose] --> B[Still loose]
            """.trimIndent(),
        )

        assertTrue(
            scene.elements.filterIsInstance<SceneShape>()
                .any { it.id == "subgraph_${SwimlaneLayout.DefaultLaneId}" },
        )
    }

    @Test
    fun keepsLabelledEdgesOrthogonalAndVisible() {
        val scene = render(
            """
                swimlane-beta LR
                  subgraph Intake
                    A[Open]
                  end
                  subgraph Review
                    B{Ready?}
                  end
                  A -->|Needs review| B
            """.trimIndent(),
        )
        val edge = scene.elements.filterIsInstance<ScenePath>().single()
        val labels = scene.elements.filterIsInstance<SceneText>().map(SceneText::text)

        assertTrue(edge.points.size >= 2)
        edge.points.zipWithNext().forEach { (first, second) ->
            assertTrue(first.x == second.x || first.y == second.y)
        }
        assertTrue("Needs review" in labels)
    }

    @Test
    fun keepsCrossLaneLabelsFromExpandingLaneBounds() {
        val scene = render(
            """
                swimlane-beta TD
                  subgraph Intake
                    N1[Interpret]
                  end
                  subgraph Assessment
                    N2{Track?}
                  end
                  subgraph Execution
                    N3[Report]
                  end
                  subgraph Closure
                    N4[Attest]
                  end
                  N1 --> N2
                  N2 --> N3
                  N3 --> N4
                  N3 --> N4
                  N4 -.-> N2
                  N1 -->|accepted| N3
                  N4 -. revise .-> N1
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>().associateBy(SceneShape::id)
        val lanes = listOf("Intake", "Assessment", "Execution", "Closure")
            .map { id -> shapes.getValue("subgraph_$id").bounds }

        lanes.zipWithNext().forEach { (first, second) ->
            assertTrue(first.right <= second.left + 0.01f)
        }
        val edgeLabels = scene.elements.filterIsInstance<SceneText>()
            .filter { text -> text.text == "accepted" || text.text == "revise" }
            .associateBy(SceneText::text)
        assertEquals(setOf("accepted", "revise"), edgeLabels.keys)
        val accepted = edgeLabels.getValue("accepted").bounds
        val revise = edgeLabels.getValue("revise").bounds
        assertTrue(
            accepted.right <= revise.left ||
                revise.right <= accepted.left ||
                accepted.bottom <= revise.top ||
                revise.bottom <= accepted.top,
            "accepted=$accepted overlaps revise=$revise",
        )
    }

    @Test
    fun liftsTopLaneTitlesClearOfReturnRailLabels() {
        val scene = render(
            """
                swimlane-beta TD
                  subgraph Intake
                    N1[Service Ticket Report]
                  end
                  subgraph Assessment
                    N2{Service Ticket Analyze?}
                  end
                  subgraph Execution
                    N3[Service Ticket Approve]
                  end
                  subgraph Closure
                    N4[Service Ticket Credit]
                  end
                  N1 --> N2
                  N2 --> N3
                  N2 --> N4
                  N1 -->|accepted| N3
                  N4 -. revise .-> N1
            """.trimIndent(),
        )
        val titles = scene.elements.filterIsInstance<SceneShape>()
            .filter { shape ->
                shape.id in setOf(
                    "subgraph_Intake",
                    "subgraph_Assessment",
                    "subgraph_Execution",
                    "subgraph_Closure",
                )
            }
        val reviseBackground = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "L_N4_N1_0_label_background" }

        assertEquals(4, titles.size)
        assertTrue(
            titles.none { title ->
                title.bounds.left < reviseBackground.bounds.right &&
                    title.bounds.right > reviseBackground.bounds.left &&
                    title.bounds.top < reviseBackground.bounds.bottom &&
                    title.bounds.bottom > reviseBackground.bounds.top
            },
            "titles=${titles.map(SceneShape::bounds)} revise=${reviseBackground.bounds}",
        )
    }

    @Test
    fun writesNestedLaneBoundsAndPaintOrderLikeUpstreamSourceOrder() {
        val scene = render(
            """
                swimlane-beta TD
                  subgraph Intake
                    N1[Forecast]
                  end
                  subgraph Assessment
                    subgraph Specialist
                      N2[Prioritize]
                    end
                  end
                  N3[Assign]
                  N4[Release]
                  N1 --> N2 --> N3 --> N4
            """.trimIndent(),
        )
        val assessment = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "subgraph_Assessment" }
        val specialist = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "subgraph_Specialist" }
        val intakeBody = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "subgraph_Intake_body" }
        val defaultBody = scene.elements.filterIsInstance<SceneShape>()
            .single { shape ->
                shape.id == "subgraph_${SwimlaneLayout.DefaultLaneId}_body"
            }
        val assessmentTitleIndex = scene.elements.indexOfFirst { element ->
            element is SceneText && element.text == "Assessment"
        }
        val specialistIndex = scene.elements.indexOf(specialist)
        val intakeBodyIndex = scene.elements.indexOfFirst { element ->
            element is SceneShape && element.id == "subgraph_Intake_body"
        }

        assertTrue(assessment.bounds.width < specialist.bounds.width)
        assertEquals(defaultBody.bounds.width, intakeBody.bounds.width, 0.01f)
        assertEquals(defaultBody.bounds.right, intakeBody.bounds.left, 0.01f)
        assertTrue(assessmentTitleIndex >= 0)
        assertTrue(
            specialistIndex > assessmentTitleIndex,
            "assessmentTitle=$assessmentTitleIndex specialist=$specialistIndex " +
                "elements=${scene.elements.map { element -> element.describe() }}",
        )
        assertTrue(
            intakeBodyIndex > specialistIndex,
            "specialist=$specialistIndex intakeBody=$intakeBodyIndex",
        )
    }

    @Test
    fun splitsWeakerFanOutSiblingOntoSecondarySide() {
        val scene = render(
            """
                swimlane-beta LR
                  subgraph Intake
                    N1[Book]
                  end
                  subgraph Assessment
                    N2{Assign?}
                  end
                  subgraph Execution
                    N3[Track]
                  end
                  subgraph Closure
                    N4[Complete]
                  end
                  N1 --> N2
                  N2 --> N3
                  N2 --> N4
            """.trimIndent(),
        )
        val decision = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "N2" }
        val longBranch = scene.elements.filterIsInstance<ScenePath>()
            .single { path -> path.id == "L_N2_N4_0" }

        assertEquals(decision.bounds.right, longBranch.points.first().x, 0.01f)
        assertTrue(longBranch.points.maxOf { point -> point.x } > decision.bounds.right + 20f)
    }

    @Test
    fun separatesDiamondOutgoingEdgeFromOccupiedIncomingSide() {
        val scene = render(
            """
                swimlane-beta LR
                  subgraph Intake
                    N1[Detect]
                  end
                  subgraph Assessment
                    N2{Identify?}
                  end
                  subgraph Execution
                    N3[Treat]
                  end
                  subgraph Closure
                    N4[Monitor]
                  end
                  N1 --> N2
                  N2 --> N3
                  N3 --> N4
                  N3 --> N4
                  N4 -.-> N2
            """.trimIndent(),
        )
        val decision = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id == "N2" }
        val outgoing = scene.elements.filterIsInstance<ScenePath>()
            .single { path -> path.id == "L_N2_N3_0" }

        assertEquals(decision.bounds.right, outgoing.points.first().x, 0.01f)
        assertTrue(outgoing.points.maxOf { point -> point.x } > decision.bounds.right + 20f)
    }

    @Test
    fun scopedDagreOverrideUsesRegularFlowchartClusters() {
        val scene = render(
            """
                ---
                config:
                  swimlane:
                    layout: dagre
                    theme: default
                    look: classic
                ---
                swimlane-beta LR
                  A --> B
            """.trimIndent(),
        )

        assertTrue(
            scene.elements.filterIsInstance<SceneShape>()
                .none { it.id == "subgraph_${SwimlaneLayout.DefaultLaneId}" },
        )
    }

    @Test
    fun parsesAllSwimlaneSpecificConfiguration() {
        val scene = render(
            """
                ---
                config:
                  swimlane:
                    lineHops: gap
                    ignoreCrossLaneEdges: false
                    optimizeRanksByCrossings: false
                    automaticLaneOrdering: true
                ---
                swimlane-beta TB
                  subgraph A
                    one
                  end
                  subgraph B
                    two
                  end
                  one --> two
            """.trimIndent(),
        )

        assertTrue(scene.width > 0f)
        assertTrue(scene.height > 0f)
    }

    @Test
    fun preservesAccessibilityMetadataOutsideVisibleSceneText() {
        val scene = render(
            """
                swimlane-beta LR
                  accTitle: Accessible support flow
                  accDescr: A request moves from intake to resolution.
                  subgraph Intake
                    open[Open request]
                  end
                  subgraph Resolution
                    resolve[Resolve request]
                  end
                  open --> resolve
            """.trimIndent(),
        )

        assertEquals("Accessible support flow", scene.accessibilityTitle)
        assertEquals(
            "A request moves from intake to resolution.",
            scene.accessibilityDescription,
        )
    }

    private fun render(source: String): MermaidScene {
        val result = engine.render(source, context)
        return assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
    }

    private fun Any.describe(): String = when (this) {
        is SceneShape -> "shape:$id:$zIndex"
        is SceneText -> "text:$text:$zIndex"
        is ScenePath -> "path:$id:$zIndex"
        else -> toString()
    }
}
