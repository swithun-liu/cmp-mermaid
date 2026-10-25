@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.ScenePathCommand
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneText
import java.io.File
import java.time.Instant
import kotlin.math.ceil
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*

/** Test-only Compose capture; the production libraries never execute JavaScript. */
object LabelCompatibilityAudit {
    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        val directory = File(args.single())
        val fixture = Json.parseToJsonElement(File(directory, "official.json").readText()).jsonObject
        val results = buildJsonArray {
            fixture.getValue("cases").jsonArray.forEach { value ->
                val entry = value.jsonObject
                val id = entry.getValue("id").jsonPrimitive.content
                val source = entry.getValue("source").jsonPrimitive.content
                var prepared: MermaidPreparedScene? = null
                val measurement = ImageComposeScene(1800, 1200)
                try {
                    measurement.setContent {
                        prepared = rememberMermaidPreparedScene(
                            source = source,
                            options = MermaidRenderOptions(fontFamily = "Arial"),
                            formulaRenderer = null,
                        ).value
                    }
                    val deadline = System.nanoTime() + 30_000_000_000L
                    while (prepared == null && System.nanoTime() < deadline) {
                        measurement.render().close()
                        delay(10)
                    }
                    add(buildJsonObject {
                        put("id", id)
                        put("source", source)
                        when (val result = prepared?.result) {
                            is GMResult.Ok -> {
                                val scene = result.value
                                put("sceneWidth", scene.width)
                                put("sceneHeight", scene.height)
                                put("viewportPadding", scene.viewportPadding)
                                put("diagnostics", scene.diagnostics.joinToString())
                                put("labels", buildJsonArray {
                                    scene.elements.filterIsInstance<SceneText>().forEach { label ->
                                        add(buildJsonObject {
                                            put("text", label.text)
                                            put("bounds", label.bounds.auditJson())
                                        })
                                    }
                                })
                                put("nodes", buildJsonArray {
                                    scene.elements.filterIsInstance<SceneShape>().forEach { node ->
                                        add(buildJsonObject {
                                            put("id", node.id)
                                            put("bounds", node.bounds.auditJson())
                                        })
                                    }
                                })
                                put("edges", buildJsonArray {
                                    scene.elements.filterIsInstance<ScenePath>().forEach { edge ->
                                        add(buildJsonObject {
                                            put("id", edge.id)
                                            put("points", buildJsonArray {
                                                edge.points.forEach { add(it.auditJson()) }
                                            })
                                            put("d", edge.commands.auditPath())
                                            put("arrowStart", edge.arrowStart.name)
                                            put("arrowEnd", edge.arrowEnd.name)
                                        })
                                    }
                                })
                                capture(scene, File(directory, "$id-native.png"))
                            }
                            is GMResult.Err -> put("error", result.error.toString())
                            null -> put("error", "Native scene resolution timed out")
                        }
                    })
                    println("$id: ${prepared?.result?.javaClass?.simpleName}")
                } finally {
                    measurement.close()
                }
            }
        }
        File(directory, "native.json").writeText(Json { prettyPrint = true }.encodeToString(
            JsonObject.serializer(), buildJsonObject {
                put("schemaVersion", 1)
                put("generatedAt", Instant.now().toString())
                put("cases", results)
            },
        ))
        // Compose's desktop dispatcher outlives the closed offscreen scenes.
        kotlin.system.exitProcess(0)
    }

    private fun capture(scene: MermaidScene, file: File) {
        val width = ceil(scene.width + 2 * scene.viewportPadding).toInt().coerceAtLeast(1)
        val height = ceil(scene.height + 2 * scene.viewportPadding).toInt().coerceAtLeast(1)
        val canvas = ImageComposeScene(width, height)
        try {
            canvas.setContent {
                MermaidSceneCanvas(scene, Modifier.fillMaxSize(), respectSourceViewportSizing = false)
            }
            repeat(3) { canvas.render().close() }
            val image = canvas.render()
            try {
                val png = image.encodeToData() ?: error("PNG encoding failed")
                try { file.writeBytes(png.bytes) } finally { png.close() }
            } finally { image.close() }
        } finally { canvas.close() }
    }
}

private fun SceneRect.auditJson(): JsonObject = buildJsonObject {
    put("x", left); put("y", top); put("width", width); put("height", height)
}

private fun ScenePoint.auditJson(): JsonArray = buildJsonArray { add(x); add(y) }

private fun List<ScenePathCommand>.auditPath(): String = joinToString(" ") { command ->
    fun ScenePoint.xy() = "$x,$y"
    when (command) {
        is ScenePathCommand.MoveTo -> "M${command.point.xy()}"
        is ScenePathCommand.LineTo -> "L${command.point.xy()}"
        is ScenePathCommand.QuadraticTo -> "Q${command.control.xy()} ${command.end.xy()}"
        is ScenePathCommand.CubicTo -> "C${command.control1.xy()} ${command.control2.xy()} ${command.end.xy()}"
        is ScenePathCommand.ArcTo -> "A${command.radius},${command.radius} 0 0,${if (command.clockwise) 1 else 0} ${command.end.xy()}"
    }
}
