package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShapeGeometry
import com.swithun.cmpmermaid.core.SceneSize
import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidShapeIntersectionPortTest {
    @Test
    fun rectangleIntersectionExtendsAnInteriorPointToTheBoundary() {
        val bounds = SceneRect(
            left = 1_357.2268f,
            top = 2_059f,
            right = 1_509.2268f,
            bottom = 2_125f,
        )

        val intersection = MermaidShapeIntersectionPort.intersect(
            shapeLayout = null,
            bounds = bounds,
            toward = ScenePoint(1_357.2268f, 2_071f),
        )

        assertEquals(bounds.left, intersection.x, absoluteTolerance = 1e-3f)
        assertEquals(2_071f, intersection.y, absoluteTolerance = 1e-3f)
    }

    @Test
    fun polygonIntersectionKeepsTheCrossingOnTheQueryAxis() {
        val diamond = MermaidShapeLayout(
            size = SceneSize(100f, 100f),
            labelOffset = ScenePoint(0f, 0f),
            geometry = SceneShapeGeometry(
                paths = emptyList(),
                outline = listOf(
                    ScenePoint(0f, -50f),
                    ScenePoint(50f, 0f),
                    ScenePoint(0f, 50f),
                    ScenePoint(-50f, 0f),
                ),
            ),
            showsLabel = true,
        )

        val intersection = MermaidShapeIntersectionPort.intersect(
            shapeLayout = diamond,
            bounds = SceneRect(150f, 150f, 250f, 250f),
            toward = ScenePoint(400f, 200f),
        )

        assertEquals(250f, intersection.x, absoluteTolerance = 1e-3f)
        assertEquals(200f, intersection.y, absoluteTolerance = 1e-3f)
    }

    @Test
    fun polygonIntersectionTranslatesPositiveShapeCoordinatesLikeMermaid() {
        val diamond = MermaidShapeLayout(
            size = SceneSize(100f, 100f),
            labelOffset = ScenePoint(0f, 0f),
            geometry = SceneShapeGeometry(
                paths = emptyList(),
                outline = listOf(
                    ScenePoint(50f, 0f),
                    ScenePoint(100f, 50f),
                    ScenePoint(50f, 100f),
                    ScenePoint(0f, 50f),
                ),
            ),
            showsLabel = true,
        )

        val intersection = MermaidShapeIntersectionPort.intersect(
            shapeLayout = diamond,
            bounds = SceneRect(150f, 150f, 250f, 250f),
            toward = ScenePoint(150f, 200f),
        )

        assertEquals(150f, intersection.x, absoluteTolerance = 1e-3f)
        assertEquals(200f, intersection.y, absoluteTolerance = 1e-3f)
    }
}
