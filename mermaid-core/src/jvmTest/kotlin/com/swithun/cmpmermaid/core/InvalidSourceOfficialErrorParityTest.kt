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
    fun matchesMermaid12OfficialOutcomesForFullAiMutationCorpus() {
        val manifest = Json.parseToJsonElement(
            Files.readString(findManifest()),
        ).jsonObject
        assertEquals(
            "256 distinct AI-like mutations from 256 valid bases per diagram family",
            manifest.getValue("variantModel").jsonPrimitive.content,
            "Regenerate the AI-mutation evidence before running this test",
        )
        val records = manifest.getValue("cases")
            .jsonArray
            .associate { entry ->
                val record = entry.jsonObject
                record.getValue("id").jsonPrimitive.content to record
            }

        assertEquals(invalidSourceCorpusCases.size, records.size)
        val mismatches = invalidSourceCorpusCases.mapNotNull { case ->
            val record = records.getValue(case.id)
            val expected = record.getValue("officialOutcome").jsonPrimitive.content
            val actual = when (engine.render(case.source, context)) {
                is GMResult.Ok -> "success"
                is GMResult.Err -> "error"
            }
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
            fail(
                "${mismatches.size}/${invalidSourceCorpusCases.size} Official/Native " +
                    "outcome mismatches ($summary): " +
                    mismatches.take(20).joinToString { mismatch ->
                        "${mismatch.id} expected=${mismatch.expected} " +
                            "actual=${mismatch.actual}"
                    },
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
