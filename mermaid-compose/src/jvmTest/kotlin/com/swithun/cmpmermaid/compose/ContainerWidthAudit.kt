@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneText
import java.io.File
import java.time.Instant
import kotlin.math.ceil
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*

/** Isolated audit of the public composable's parent-width input and effective look. */
object ContainerWidthAudit {
    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val directory = File(args.single())
        val fixture = Json.parseToJsonElement(File(directory, "official.json").readText()).jsonObject
        val cases = buildJsonArray {
            fixture.getValue("cases").jsonArray.forEach { value ->
                val entry = value.jsonObject
                val id = entry.getValue("id").jsonPrimitive.content
                val source = entry.getValue("source").jsonPrimitive.content
                val layout = entry.getValue("layout").jsonPrimitive.content
                val options = MermaidRenderOptions(
                    layout = layout,
                    look = entry.getValue("look").jsonPrimitive.content,
                    themeName = "redux-color",
                    fontFamily = "Arial",
                    ganttUseWidth = entry["useWidth"]?.jsonPrimitive?.float,
                ).let { it.copy(mindmap = it.mindmap.copy(layoutAlgorithm = layout)) }
                var result: GMResult<MermaidScene, MermaidError>? = null
                val parent = ImageComposeScene(entry.getValue("containerWidth").jsonPrimitive.int, 900)
                try {
                    parent.setContent {
                        MermaidDiagram(
                            source = source,
                            options = options,
                            modifier = Modifier.fillMaxSize(),
                            onError = null,
                            onRenderResult = { result = it },
                            formulaRenderer = null,
                        )
                    }
                    val deadline = System.nanoTime() + 30_000_000_000L
                    while (result == null && System.nanoTime() < deadline) {
                        parent.render().close()
                        delay(10)
                    }
                    add(buildJsonObject {
                        put("id", id)
                        put("source", source)
                        when (val resolved = result) {
                            is GMResult.Ok -> {
                                val scene = resolved.value
                                put("width", scene.width)
                                put("height", scene.height)
                                put("viewportPadding", scene.viewportPadding)
                                put("shapes", buildJsonArray {
                                    scene.elements.filterIsInstance<SceneShape>().forEach { shape ->
                                        add(buildJsonObject {
                                            put("id", shape.id)
                                            put("x", shape.bounds.left); put("y", shape.bounds.top)
                                            put("width", shape.bounds.width); put("height", shape.bounds.height)
                                            put("fill", shape.fill.argb); put("stroke", shape.stroke.argb)
                                            put("strokeWidth", shape.strokeWidth)
                                        })
                                    }
                                })
                                put("labels", buildJsonArray {
                                    scene.elements.filterIsInstance<SceneText>().forEach { label ->
                                        add(buildJsonObject {
                                            put("text", label.text)
                                            put("x", label.bounds.left); put("y", label.bounds.top)
                                            put("width", label.bounds.width); put("height", label.bounds.height)
                                        })
                                    }
                                })
                                put("edges", buildJsonArray {
                                    scene.elements.filterIsInstance<ScenePath>().forEach { edge ->
                                        add(buildJsonObject {
                                            put("id", edge.id)
                                            put("stroke", edge.color.argb); put("strokeWidth", edge.strokeWidth)
                                            put("points", buildJsonArray {
                                                edge.points.forEach { point ->
                                                    add(buildJsonArray { add(point.x); add(point.y) })
                                                }
                                            })
                                        })
                                    }
                                })
                                capture(scene, File(directory, "$id-native.png"))
                            }
                            is GMResult.Err -> put("error", resolved.error.toString())
                            null -> put("error", "Native scene preparation timed out")
                        }
                    })
                    println("$id: ${result?.javaClass?.simpleName}")
                } finally {
                    parent.close()
                }
            }
        }
        File(directory, "native.json").writeText(Json { prettyPrint = true }.encodeToString(
            JsonObject.serializer(), buildJsonObject {
                put("schemaVersion", 1); put("generatedAt", Instant.now().toString()); put("cases", cases)
            },
        ))
        kotlin.system.exitProcess(0)
    }

    private fun capture(scene: MermaidScene, destination: File) {
        val width = ceil(scene.width + scene.viewportPadding * 2).toInt().coerceAtLeast(1)
        val height = ceil(scene.height + scene.viewportPadding * 2).toInt().coerceAtLeast(1)
        val canvas = ImageComposeScene(width, height)
        try {
            canvas.setContent {
                MermaidSceneCanvas(scene, Modifier.fillMaxSize(), respectSourceViewportSizing = false)
            }
            repeat(3) { canvas.render().close() }
            val image = canvas.render()
            try {
                val data = image.encodeToData()
                if (data != null) {
                    try { destination.writeBytes(data.bytes) } finally { data.close() }
                }
            } finally { image.close() }
        } finally { canvas.close() }
    }
}
