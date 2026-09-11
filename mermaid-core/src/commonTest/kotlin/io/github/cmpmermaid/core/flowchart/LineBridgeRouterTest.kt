package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LineBridgeRouterTest {
    @Test
    fun laterPathBridgesOverInteriorCrossing() {
        val horizontal = path("horizontal", ScenePoint(0f, 50f), ScenePoint(100f, 50f))
        val vertical = path("vertical", ScenePoint(50f, 0f), ScenePoint(50f, 100f))

        val result = LineBridgeRouter.apply(listOf(horizontal, vertical))

        assertTrue(result[0].bridges.isEmpty())
        assertEquals(ScenePoint(50f, 50f), result[1].bridges.single().center)
    }

    @Test
    fun sharedEndpointsDoNotCreateBridges() {
        val horizontal = path("horizontal", ScenePoint(0f, 50f), ScenePoint(50f, 50f))
        val vertical = path("vertical", ScenePoint(50f, 50f), ScenePoint(50f, 100f))

        val result = LineBridgeRouter.apply(listOf(horizontal, vertical))

        assertTrue(result.all { it.bridges.isEmpty() })
    }

    private fun path(
        id: String,
        start: ScenePoint,
        end: ScenePoint,
    ) = ScenePath(
        id = id,
        points = listOf(start, end),
        color = SceneColor(0xFF000000),
        strokeWidth = 2f,
        arrowEnd = SceneArrowHead.None,
    )
}
