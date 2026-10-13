package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mermaid.js 12.0.0:
 * - swimlanes/__tests__/phase0.helpers.spec.ts
 * - swimlanes/__tests__/phase1.cycles.spec.ts
 */
class Phase0AndCyclesTest {
    @Test
    fun emptyAndSingleNodeGraphsAreAcyclic() {
        val empty = normalizeGraph(testGraph(emptyList<String>()))
        assertTrue(isAcyclic(empty))
        assertEquals(emptyList(), topoSortIfAcyclic(empty))

        val single = normalizeGraph(testGraph(listOf("A")))
        assertTrue(isAcyclic(single))
        assertEquals(listOf("A"), topoSortIfAcyclic(single))
    }

    @Test
    fun parallelEdgesRemainAcyclicAndAreAllVisible() {
        val graph = normalizeGraph(
            testGraph(
                nodeIds = listOf("A", "B"),
                edgePairs = listOf("A" to "B", "A" to "B"),
            ),
        )

        assertTrue(isAcyclic(graph))
        assertEquals(listOf("A", "B"), topoSortIfAcyclic(graph))
        assertEquals(2, incoming(graph, "B").size)
        assertEquals(2, outgoing(graph, "A").size)
    }

    @Test
    fun selfLoopAndTwoNodeCycleAreCyclic() {
        val selfLoop = normalizeGraph(testGraph(listOf("A"), listOf("A" to "A")))
        assertFalse(isAcyclic(selfLoop))
        assertNull(topoSortIfAcyclic(selfLoop))

        val twoNode = normalizeGraph(
            testGraph(
                nodeIds = listOf("A", "B"),
                edgePairs = listOf("A" to "B", "B" to "A"),
            ),
        )
        assertFalse(isAcyclic(twoNode))
        assertNull(topoSortIfAcyclic(twoNode))
    }

    @Test
    fun disconnectedDagTopologicalOrderIncludesEveryNode() {
        val graph = normalizeGraph(
            testGraph(
                nodeIds = listOf("A", "B", "C", "D"),
                edgePairs = listOf("A" to "B", "C" to "D"),
            ),
        )

        val order = assertNotNull(topoSortIfAcyclic(graph))
        val positions = order.withIndex().associate { indexed -> indexed.value to indexed.index }
        assertEquals(4, order.size)
        assertTrue(positions.getValue("A") < positions.getValue("B"))
        assertTrue(positions.getValue("C") < positions.getValue("D"))
    }

    @Test
    fun cycleRemovalLeavesDagEdgesUnchanged() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("A", "B", "C"),
                edgePairs = listOf("A" to "B", "B" to "C"),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        assertTrue(result.reversed.isEmpty())
        assertEquals(
            listOf("A->B", "B->C"),
            result.acyclic.edges.map { edge -> "${edge.src}->${edge.dst}" },
        )
    }

    @Test
    fun cycleRemovalReversesTheClosingBackEdge() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("A", "B", "C"),
                edgePairs = listOf("A" to "B", "B" to "C", "C" to "A"),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        assertEquals(1, result.reversed.size)
        assertTrue(result.acyclic.edges.any { edge -> edge.src == "A" && edge.dst == "C" })
    }

    @Test
    fun twoNodeCycleReversesExactlyOneDeterministicEdge() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("A", "B"),
                edgePairs = listOf("A" to "B", "B" to "A"),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        assertEquals(listOf("B->A"), result.reversed.map { edge -> "${edge.src}->${edge.dst}" })
    }

    @Test
    fun cycleRemovalDoesNotChangeDisconnectedDagComponent() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("A", "B", "C", "X", "Y"),
                edgePairs = listOf("A" to "B", "B" to "A", "X" to "Y"),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        val edge = result.acyclic.edges.single { candidate -> candidate.id == "e2" }
        assertEquals("X", edge.src)
        assertEquals("Y", edge.dst)
    }

    @Test
    fun denseBidirectionalGraphBecomesAcyclic() {
        val nodeIds = listOf("A", "B", "C", "D")
        val pairs = buildList {
            for (source in nodeIds) {
                for (destination in nodeIds) {
                    if (source != destination) add(source to destination)
                }
            }
        }

        assertTrue(isAcyclic(removeCyclesDfs(testGraph(nodeIds, pairs)).acyclic))
    }

    @Test
    fun cycleRemovalSeedsFromTrueSources() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("start", "task", "fix", "decision"),
                edgePairs = listOf(
                    "start" to "task",
                    "task" to "decision",
                    "decision" to "fix",
                    "fix" to "task",
                ),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        assertEquals(
            listOf("fix->task"),
            result.reversed.map { edge -> "${edge.src}->${edge.dst}" },
        )
        assertEquals(
            listOf("start"),
            result.acyclic.nodes.filter { node ->
                result.acyclic.edges.none { edge -> edge.dst == node }
            },
        )
    }

    @Test
    fun cycleRemovalFallsBackToDeclarationOrderWithoutSources() {
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf("B", "A"),
                edgePairs = listOf("B" to "A", "A" to "B"),
            ),
        )

        assertTrue(isAcyclic(result.acyclic))
        assertEquals(
            listOf("A->B"),
            result.reversed.map { edge -> "${edge.src}->${edge.dst}" },
        )
    }

    @Test
    fun cycleRemovalUsesLocaleCompareOrderingForGeneratedLabelNodes() {
        val label13 = "edge-label-N1-N3-L_N1_N3_0"
        val label41 = "edge-label-N4-N1-L_N4_N1_0"
        val result = removeCyclesDfs(
            testGraph(
                nodeIds = listOf(
                    "Intake",
                    "Assessment",
                    "Execution",
                    "Closure",
                    "N1",
                    "N2",
                    "N3",
                    "N4",
                    label13,
                    label41,
                ),
                edgePairs = listOf(
                    "N1" to "N2",
                    "N2" to "N3",
                    "N3" to "N4",
                    "N4" to "N2",
                    "N1" to label13,
                    label13 to "N3",
                    "N4" to label41,
                    label41 to "N1",
                ),
            ),
        )

        assertTrue(compareMermaidIds(label13, "N2") < 0)
        assertEquals(
            listOf("$label41->N1", "N2->N3"),
            result.reversed.map { edge -> "${edge.src}->${edge.dst}" },
        )
    }
}
