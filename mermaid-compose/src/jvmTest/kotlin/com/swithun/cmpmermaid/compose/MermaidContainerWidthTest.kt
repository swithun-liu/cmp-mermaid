@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidContainerWidthTest {
    @Test
    fun publicDiagramUsesContentWidthInLogicalPixelsAndRebuildsOnResize() = runBlocking {
        val width = mutableStateOf(360.dp)
        var result: GMResult<MermaidScene, MermaidError>? = null
        val canvas = ImageComposeScene(1800, 900, density = Density(2f))
        try {
            canvas.setContent {
                MermaidDiagram(
                    source = SOURCE,
                    modifier = Modifier.width(width.value).padding(8.dp),
                    onError = null,
                    onRenderResult = { result = it },
                    formulaRenderer = null,
                )
            }
            suspend fun await(expected: Float): MermaidScene {
                val deadline = System.nanoTime() + 10_000_000_000L
                while ((result as? GMResult.Ok)?.value?.width != expected &&
                    System.nanoTime() < deadline
                ) {
                    canvas.render().close()
                    delay(10)
                }
                val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
                assertEquals(expected, scene.width)
                return scene
            }
            val narrow = await(344f)
            width.value = 784.dp
            val wide = await(768f)
            assertEquals(narrow.height, wide.height)
            val task = wide.elements.filterIsInstance<SceneShape>()
                .single { it.id == "gantt-task-task" }
            assertEquals(693f, task.bounds.right)
        } finally {
            canvas.close()
        }
    }

    @Test
    fun explicitUseWidthWinsOverPublicDiagramContainer() = runBlocking {
        var result: GMResult<MermaidScene, MermaidError>? = null
        val canvas = ImageComposeScene(360, 400)
        try {
            canvas.setContent {
                MermaidDiagram(
                    source = SOURCE,
                    options = MermaidRenderOptions(ganttUseWidth = 1200f),
                    modifier = Modifier.width(360.dp).padding(8.dp),
                    onError = null,
                    onRenderResult = { result = it },
                    formulaRenderer = null,
                )
            }
            val deadline = System.nanoTime() + 10_000_000_000L
            while (result == null && System.nanoTime() < deadline) {
                canvas.render().close()
                delay(10)
            }
            assertEquals(1200f, assertIs<GMResult.Ok<MermaidScene>>(result).value.width)
        } finally {
            canvas.close()
        }
    }

    private companion object {
        const val SOURCE =
            "gantt\ndateFormat YYYY-MM-DD\ntodayMarker off\nTask :task, 2025-01-01, 2d"
    }
}
