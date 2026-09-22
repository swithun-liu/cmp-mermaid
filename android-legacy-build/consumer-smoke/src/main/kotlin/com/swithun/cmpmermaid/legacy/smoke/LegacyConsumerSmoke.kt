package com.swithun.cmpmermaid.legacy.smoke

import android.content.Context
import androidx.compose.runtime.Composable
import com.swithun.cmpmermaid.compose.CMPMermaidView
import com.swithun.cmpmermaid.compose.MermaidDiagram
import com.swithun.cmpmermaid.core.MermaidRenderErrorInfo

@Composable
fun LegacyConsumerSmoke() {
    MermaidDiagram(
        source = """
            flowchart LR
                Source --> Consumer
        """.trimIndent(),
        onError = ::consumeRenderError,
    )
}

@Composable
fun legacyTrailingLambdaConsumerSmoke() {
    MermaidDiagram(source = "flowchart LR\nSource --> LegacyCallback") { result ->
        @Suppress("UNUSED_VARIABLE")
        val renderResult = result
    }
}

fun legacyAndroidViewConsumer(context: Context): CMPMermaidView =
    CMPMermaidView(context).apply {
        setMermaidSource("flowchart LR\nSource --> ViewHost")
        setMermaidContentDescription("Mermaid consumer smoke")
        setMermaidErrorListener(::consumeRenderError)
    }

private fun consumeRenderError(error: MermaidRenderErrorInfo) {
    @Suppress("UNUSED_VARIABLE")
    val payload = Triple(error.type, error.message, error.source)
}
