package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.ScenePathCommand
import com.swithun.cmpmermaid.core.ScenePoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MermaidEdgeLabelPortTest {
    // Mermaid.js 12.0.0: utils.spec.ts -> calculatePoint, all five upstream tests.
    @Test
    fun translatesCalculatePointUpstreamTests() {
        val straight = listOf(p(0, 0), p(0, 10), p(0, 20))
        listOf(0.0, 5.0, 10.0).forEach { distance ->
            assertEquals(p(0f, distance.toFloat()), calculated(straight, distance))
        }
        val slope = listOf(p(0, 0), p(10, 10), p(20, 20))
        assertEquals(p(0, 0), calculated(slope, 0.0))
        assertEquals(p(3.53553f, 3.53553f), calculated(slope, 5.0))
        assertEquals(p(7.07107f, 7.07107f), calculated(slope, 10.0))
        val negative = listOf(p(20, 20), p(10, 10), p(15, 15), p(0, 0))
        assertEquals(p(20, 20), calculated(negative, 0.0))
        assertEquals(p(16.46447f, 16.46447f), calculated(negative, 5.0))
        assertEquals(p(12.92893f, 12.92893f), calculated(negative, 10.0))
        val bent = listOf(p(0, 0), p(10, 10), p(20, 0))
        assertEquals(p(0, 0), calculated(bent, 0.0))
        assertEquals(p(10.6066f, 9.3934f), calculated(bent, 15.0))
        assertEquals(p(14.14214f, 5.85786f), calculated(bent, 20.0))
        val error = assertIs<GMResult.Err<MermaidError>>(
            MermaidEdgeLabelPort.calculatePoint(slope, 30.0),
        ).error
        assertEquals("Could not find a suitable point for the given distance", error.message)
    }

    @Test
    fun retainsDuplicateSegmentEarlyReturnAndSingletonAnchor() {
        val points = listOf(p(3, 7), p(3, 7), p(103, 7))
        assertEquals(p(3, 7), calculated(points, 50.0))
        assertEquals(p(3, 7), ok(MermaidEdgeLabelPort.calcLabelPosition(points)))
        assertEquals(p(17, 29), ok(MermaidEdgeLabelPort.calcLabelPosition(listOf(p(17, 29)))))
        assertIs<GMResult.Err<MermaidError>>(MermaidEdgeLabelPort.calcLabelPosition(emptyList()))
    }

    // Coordinates come from unchanged utils.ts bodies in the test-only oracle.
    @Test
    fun translatesAllTerminalPositionsAndMarkerSizesWithoutMutatingThePath() {
        val points = listOf(p(0, 0), p(100, 0))
        val expectedByMarkerSize = listOf(
            listOf(p(12.5f, -10f), p(12.5f, 10f), p(82.5f, -15f), p(82.5f, 5f)),
            listOf(p(17.5f, -15f), p(17.5f, 15f), p(77.5f, -20f), p(77.5f, 10f)),
        )
        listOf(0.0, 10.0).forEachIndexed { index, markerSize ->
            MermaidEdgeLabelPort.TerminalPosition.entries.forEachIndexed { positionIndex, position ->
                val actual = ok(MermaidEdgeLabelPort.calcTerminalLabelPosition(
                    markerSize, position, points,
                ))
                val expected = expectedByMarkerSize[index][positionIndex]
                assertEquals(expected.x, actual.x, 0.00001f)
                assertEquals(expected.y, actual.y, 0.00001f)
                assertEquals(listOf(p(0, 0), p(100, 0)), points)
            }
        }
    }

    @Test
    fun terminalTraversalRetainsDuplicateEarlyReturnAndShortPathFailures() {
        val duplicate = listOf(p(3, 7), p(3, 7), p(103, 7))
        val start = ok(MermaidEdgeLabelPort.calcTerminalLabelPosition(
            10.0, MermaidEdgeLabelPort.TerminalPosition.StartRight, duplicate,
        ))
        assertEquals(p(3, -8), start)
        listOf(emptyList(), listOf(p(17, 29)), listOf(p(0, 0), p(10, 0))).forEach { points ->
            MermaidEdgeLabelPort.TerminalPosition.entries.forEach { position ->
                val error = assertIs<GMResult.Err<MermaidError>>(
                    MermaidEdgeLabelPort.calcTerminalLabelPosition(10.0, position, points),
                ).error
                assertEquals("Could not find a suitable point for the given distance", error.message)
            }
        }
    }

    @Test
    fun terminalInterpolationMatchesTheUpstreamBentPathOracle() {
        val points = listOf(p(-12, 7), p(16, 25), p(90, 60))
        val expectedByMarkerSize = listOf(
            listOf(
                p(3.9223063f, 5.3476853f), p(-6.892846f, 22.171255f),
                p(77.975784f, 40.61562f), p(69.42456f, 58.69535f),
            ),
            listOf(
                p(10.805808f, 3.697545f), p(-5.256948f, 29.035025f),
                p(75.59365f, 33.957882f), p(62.76682f, 61.077477f),
            ),
        )
        listOf(0.0, 10.0).forEachIndexed { index, markerSize ->
            MermaidEdgeLabelPort.TerminalPosition.entries.forEachIndexed { positionIndex, position ->
                val actual = ok(MermaidEdgeLabelPort.calcTerminalLabelPosition(
                    markerSize, position, points,
                ))
                val expected = expectedByMarkerSize[index][positionIndex]
                assertEquals(expected.x, actual.x, 0.00001f)
                assertEquals(expected.y, actual.y, 0.00001f)
            }
        }
    }

    @Test
    fun preservesUpstreamSubstringAndSignedDecimalSemantics() {
        assertFalse(MermaidEdgeLabelPort.isLabelCoordinateInPath(p(-1.5f, 42f), "M-1.5,18L13,19"))
        assertTrue(MermaidEdgeLabelPort.isLabelCoordinateInPath(p(-1.5f, 42f), "M-1,18L13,19"))
        assertTrue(MermaidEdgeLabelPort.isLabelCoordinateInPath(p(12, 999), "M312,8L100,20"))
        assertFalse(MermaidEdgeLabelPort.isLabelCoordinateInPath(p(97, 57), "M1,2L3,4"))
    }

    @Test
    fun finalBasisPathCheckReplacesLayoutAnchorWithUpstreamPolylineMidpoint() {
        val points = listOf(p(23, 19), p(97, 57), p(163, 149))
        val commands = ok(MermaidEdgePathPort.generate(
            points, "basis", SceneArrowHead.None, SceneArrowHead.None,
        ))
        val d = ok(MermaidEdgeLabelPort.pathData(commands, "basis"))
        assertEquals(
            "M23,19L35.333,25.333C47.667,31.667,72.333,44.333,95.667,66" +
                "C119,87.667,141,118.333,152,133.667L163,149",
            d,
        )
        assertFalse(MermaidEdgeLabelPort.isLabelCoordinateInPath(points[1], d))
        assertEquals(p(105.75496f, 69.20388f), ok(MermaidEdgeLabelPort.positionEdgeLabel(
            p(97, 57), points, commands, "basis",
        )))
    }

    @Test
    fun preservesLayoutAnchorUnlessTheRenderedPathOrClusterClippingChangesIt() {
        val points = listOf(p(10, 20), p(10, 40), p(10, 80))
        val commands = points.mapIndexed { index, point ->
            if (index == 0) ScenePathCommand.MoveTo(point) else ScenePathCommand.LineTo(point)
        }
        assertEquals(p(10, 40), ok(MermaidEdgeLabelPort.positionEdgeLabel(
            p(10, 40), points, commands, "linear",
        )))
        assertEquals(p(10, 50), ok(MermaidEdgeLabelPort.positionEdgeLabel(
            p(10, 40), points, commands, "linear", pathUpdated = true,
        )))
        assertIs<GMResult.Err<MermaidError>>(MermaidEdgeLabelPort.positionEdgeLabel(
            p(0, 0), emptyList(), emptyList(), "linear",
        ))
    }

    private fun p(x: Int, y: Int) = p(x.toFloat(), y.toFloat())
    private fun p(x: Float, y: Float) = ScenePoint(x, y)
    private fun calculated(points: List<ScenePoint>, distance: Double) =
        ok(MermaidEdgeLabelPort.calculatePoint(points, distance))
    private fun <T> ok(result: GMResult<T, MermaidError>): T = assertIs<GMResult.Ok<T>>(result).value
}
