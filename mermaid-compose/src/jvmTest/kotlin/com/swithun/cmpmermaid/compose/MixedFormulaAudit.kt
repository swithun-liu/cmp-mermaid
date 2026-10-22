@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneText
import java.io.File
import java.time.Instant
import kotlin.math.ceil
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.jetbrains.skia.Image

/**
 * Isolated, same-source Native/Official audit. Formula pixels and intrinsic
 * metrics come from the official reference so the comparison tests Kotlin
 * createText/flex composition and Compose painting, independently of a provider.
 * Invoke with :mermaid-compose:captureMixedFormulaAudit after the JS capture.
 */
fun main(args: Array<String>) = runBlocking {
    val directory = File(args.single())
    val fixture = Json.parseToJsonElement(File(directory, "official.json").readText()).jsonObject
    val results = buildJsonArray {
        fixture.getValue("cases").jsonArray.forEach { value ->
            val entry = value.jsonObject
            val id = entry.getValue("id").jsonPrimitive.content
            val source = entry.getValue("source").jsonPrimitive.content
            val formulas = entry.getValue("formulas").jsonArray.map { it.jsonObject }
            val requests = mutableListOf<MermaidFormulaRequest>()
            val provider = MermaidFormulaRenderer { request ->
                requests += request
                val formula = formulas.firstOrNull {
                    it.getValue("source").jsonPrimitive.content == request.source &&
                        abs(it.getValue("fontSize").jsonPrimitive.float - request.fontSize) < 0.01f
                }
                if (formula == null) {
                    GMResult.Err(MermaidFormulaError.Unsupported(
                        "No matching official intrinsic metric for ${request.source} at ${request.fontSize}",
                    ))
                } else {
                    val rect = formula.getValue("rect").jsonObject
                    val width = rect.getValue("width").jsonPrimitive.float
                    val height = rect.getValue("height").jsonPrimitive.float
                    val referenceImage = Image.makeFromEncoded(
                        File(directory, formula.getValue("image").jsonPrimitive.content).readBytes(),
                    )
                    val image = try {
                        referenceImage.toComposeImageBitmap()
                    } finally { referenceImage.close() }
                    val offsetX = formula.getValue("imageOffsetX").jsonPrimitive.float
                    val offsetY = formula.getValue("imageOffsetY").jsonPrimitive.float
                    GMResult.Ok(MermaidFormulaArtifact(
                        width = width,
                        height = height,
                        baseline = height,
                        accessibilityText = request.accessibilityText,
                        painter = MermaidFormulaPainter { scope, bounds ->
                            with(scope) {
                                // Reference bitmap padding includes glyph overhang;
                                // it does not enlarge the intrinsic formula metric.
                                withTransform({
                                    translate(bounds.left - offsetX, bounds.top - offsetY)
                                }) { drawImage(image, Offset.Zero) }
                            }
                        },
                    ))
                }
            }
            var prepared: MermaidPreparedScene? = null
            val measurementScene = ImageComposeScene(1800, 900)
            try {
                measurementScene.setContent {
                    prepared = rememberMermaidPreparedScene(
                        source = source,
                        options = MermaidRenderOptions(fontFamily = "Arial"),
                        formulaRenderer = provider,
                    ).value
                }
                val deadline = System.nanoTime() + 30_000_000_000L
                while (prepared == null && System.nanoTime() < deadline) {
                    measurementScene.render().close()
                    delay(10)
                }
                val resolved = prepared
                add(buildJsonObject {
                    put("id", id)
                    put("source", source)
                    put("requests", buildJsonArray {
                        requests.distinct().forEach { request ->
                            add(buildJsonObject {
                                put("source", request.source)
                                put("fontSize", request.fontSize)
                                put("colorArgb", request.color.argb.toString(16))
                                put("maxWidth", request.maxWidth)
                            })
                        }
                    })
                    when (val result = resolved?.result) {
                        is GMResult.Ok -> {
                            val scene = result.value
                            put("sceneWidth", scene.width)
                            put("sceneHeight", scene.height)
                            put("providerFailures", resolved.formulaFailures.size)
                            put("diagnostics", scene.diagnostics.joinToString())
                            put("labels", labelGeometry(scene, resolved.formulaArtifacts))
                            put("nodes", buildJsonArray {
                                scene.elements.filterIsInstance<SceneShape>().forEach { node ->
                                    add(buildJsonObject {
                                        put("id", node.id)
                                        put("bounds", node.bounds.json())
                                    })
                                }
                            })
                            put("edges", buildJsonArray {
                                scene.elements.filterIsInstance<ScenePath>().filter {
                                    it.arrowStart != SceneArrowHead.None || it.arrowEnd != SceneArrowHead.None
                                }.forEach { edge ->
                                    add(buildJsonObject {
                                        put("id", edge.id)
                                        put("points", buildJsonArray {
                                            edge.points.forEach { point ->
                                                add(buildJsonArray { add(point.x); add(point.y) })
                                            }
                                        })
                                    })
                                }
                            })
                            val width = ceil(scene.width + 2 * scene.viewportPadding).toInt()
                            val height = ceil(scene.height + 2 * scene.viewportPadding).toInt()
                            val canvas = ImageComposeScene(width, height)
                            try {
                                canvas.setContent {
                                    MermaidSceneCanvas(
                                        scene = scene,
                                        modifier = Modifier.fillMaxSize(),
                                        formulaArtifacts = resolved.formulaArtifacts,
                                        respectSourceViewportSizing = false,
                                    )
                                }
                                repeat(3) { canvas.render().close() }
                                val image = canvas.render()
                                try {
                                    val png = image.encodeToData()
                                    if (png != null) {
                                        File(directory, "$id-native.png").writeBytes(png.bytes)
                                        png.close()
                                    }
                                } finally { image.close() }
                            } finally { canvas.close() }
                        }
                        is GMResult.Err -> put("error", result.error.toString())
                        null -> put("error", "Native scene resolution timed out")
                    }
                })
                println("$id: ${resolved?.result?.javaClass?.simpleName}")
            } finally { measurementScene.close() }
        }
    }
    File(directory, "native.json").writeText(Json { prettyPrint = true }.encodeToString(
        JsonObject.serializer(),
        buildJsonObject {
            put("schemaVersion", 1)
            put("generatedAt", Instant.now().toString())
            put("cases", results)
        },
    ))
}

private fun labelGeometry(
    scene: MermaidScene,
    artifacts: Map<MermaidFormulaRequest, MermaidFormulaArtifact>,
): JsonArray = buildJsonArray {
    scene.elements.filterIsInstance<SceneText>().filter {
        it.formula != null || it.formulaLayout != null
    }.forEach { label ->
        add(buildJsonObject {
            put("text", label.text)
            put("bounds", label.bounds.json())
            put("fontSize", label.fontSize)
            val layout = label.formulaLayout
            val artifact = label.formulaRequest()?.let(artifacts::get)
            val width = layout?.width ?: artifact?.width ?: 0f
            val height = layout?.height ?: artifact?.height ?: 0f
            put("intrinsicWidth", width)
            put("intrinsicHeight", height)
            put("origin", buildJsonArray {
                add(label.bounds.center.x - width / 2f)
                add(label.bounds.center.y - height / 2f)
            })
            put("items", buildJsonArray {
                if (layout == null) {
                    add(buildJsonObject {
                        put("text", label.formula?.source.orEmpty())
                        put("kind", "formula")
                        put("bounds", SceneRect(0f, 0f, width, height).json())
                    })
                } else {
                    layout.items.forEach { item ->
                        add(buildJsonObject {
                            put("text", item.text)
                            put("kind", if (item.formula == null) "text" else "formula")
                            put("fontSizeScale", item.fontSizeScale)
                            put("bounds", item.bounds.json())
                        })
                    }
                }
            })
        })
    }
}

private fun SceneRect.json(): JsonObject = buildJsonObject {
    put("x", left); put("y", top); put("width", width); put("height", height)
}
