package com.swithun.cmpmermaid.legacy.smoke

import android.content.Context
import androidx.compose.runtime.Composable
import com.swithun.cmpmermaid.compose.CMPMermaidView
import com.swithun.cmpmermaid.compose.MermaidDiagram

@Composable
fun LegacyConsumerSmoke() {
    MermaidDiagram(
        source = """
            flowchart LR
                Source --> Consumer
        """.trimIndent(),
    )
}

fun legacyAndroidViewConsumer(context: Context): CMPMermaidView =
    CMPMermaidView(context).apply {
        setMermaidSource("flowchart LR\nSource --> ViewHost")
        setMermaidContentDescription("Mermaid consumer smoke")
    }
