package io.github.cmpmermaid.core.flowchart.upstream.elk

import io.github.cmpmermaid.core.GMResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ElkJsRuntimeTest {
    @Test
    fun runsLockedElkjsLayeredLayout() {
        val graph = """
            {
              "id": "root",
              "layoutOptions": {
                "elk.algorithm": "elk.layered",
                "elk.direction": "RIGHT"
              },
              "children": [
                {"id": "A", "width": 100, "height": 50},
                {"id": "B", "width": 100, "height": 50}
              ],
              "edges": [
                {"id": "e1", "sources": ["A"], "targets": ["B"]}
              ]
            }
        """.trimIndent()

        val layoutResult = ElkJsRuntime.layout(graph)
        val result = assertIs<GMResult.Ok<String>>(layoutResult, layoutResult.toString()).value
        val laidOut = Json.parseToJsonElement(result).jsonObject
        val children = laidOut.getValue("children").jsonArray
            .associateBy { it.jsonObject.getValue("id").jsonPrimitive.content }
        val edge = laidOut.getValue("edges").jsonArray.single().jsonObject

        assertTrue(
            children.getValue("A").jsonObject.getValue("x").jsonPrimitive.double <
                children.getValue("B").jsonObject.getValue("x").jsonPrimitive.double,
        )
        assertTrue(edge.getValue("sections").jsonArray.isNotEmpty())
    }
}
