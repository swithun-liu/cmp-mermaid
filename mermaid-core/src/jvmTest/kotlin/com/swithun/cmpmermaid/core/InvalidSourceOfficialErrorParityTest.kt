package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.generated.invalidSourceCorpusCases
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class InvalidSourceOfficialErrorParityTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = request.text.length * request.fontSize * 0.55f,
                height = request.fontSize * request.lineHeight,
            )
        },
    )

    @Test
    fun matchesMermaid12OfficialErrorMessagesForFullInvalidSourceCorpus() {
        val manifest = Json.parseToJsonElement(
            Files.readString(findManifest()),
        ).jsonObject
        val expectedMessages = manifest.getValue("cases")
            .jsonArray
            .associate { entry ->
                val record = entry.jsonObject
                record.getValue("id").jsonPrimitive.content to
                    record.getValue("officialErrorMessage").jsonPrimitive.content
            }

        assertEquals(invalidSourceCorpusCases.size, expectedMessages.size)
        val mismatches = invalidSourceCorpusCases.mapNotNull { case ->
            val result = assertIs<GMResult.Err<MermaidError>>(
                engine.render(case.source, context),
                "${case.id} unexpectedly rendered malformed source",
            )
            val actual = result.error.message
            val expected = expectedMessages.getValue(case.id)
            if (actual == expected) {
                null
            } else {
                Mismatch(
                    id = case.id,
                    diagramId = case.diagramId,
                    expected = expected,
                    actual = actual,
                )
            }
        }

        if (mismatches.isNotEmpty()) {
            val summary = mismatches.groupingBy(Mismatch::diagramId)
                .eachCount()
                .entries
                .joinToString { (diagramId, count) -> "$diagramId=$count" }
            val representatives = mismatches
                .distinctBy(Mismatch::diagramId)
                .joinToString("\n\n") { mismatch ->
                    "${mismatch.id}\nEXPECTED:\n${mismatch.expected}\n" +
                        "ACTUAL:\n${mismatch.actual}"
                }
            fail(
                "${mismatches.size}/${invalidSourceCorpusCases.size} Official error " +
                    "message mismatches ($summary):\n\n$representatives",
            )
        }
    }

    private fun findManifest(): Path {
        val relativePath = Path.of(
            "docs",
            "assets",
            "invalid-source-report",
            "invalid-source-manifest.json",
        )
        return generateSequence(Path.of(System.getProperty("user.dir")).toAbsolutePath()) {
            it.parent
        }
            .map { it.resolve(relativePath) }
            .firstOrNull(Files::isRegularFile)
            ?: fail("Cannot find $relativePath from ${System.getProperty("user.dir")}")
    }

    private data class Mismatch(
        val id: String,
        val diagramId: String,
        val expected: String,
        val actual: String,
    )
}
