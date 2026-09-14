@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.cmpmermaid.sample.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.cmpmermaid.debugui.MermaidDebugApp
import io.github.cmpmermaid.debugui.MermaidDebugLaunchOptions
import io.github.cmpmermaid.debugui.MermaidDebugPreview
import kotlinx.browser.document
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    ComposeViewport(body) {
        MermaidDebugApp(
            MermaidDebugLaunchOptions(
                auditDemoId = queryParameter("auditDemoId"),
                auditPreview = MermaidDebugPreview.from(queryParameter("auditPreview")),
                auditLayout = queryParameter("auditLayout") ?: "elk",
                openPlayground = queryParameter("openPlayground") == "true",
                openLoadTest = queryParameter("openLoadTest") == "true",
                autoRunLoadTest = queryParameter("autoRunLoadTest") == "true",
            ),
        )
    }
}

@JsFun("(name) => new URLSearchParams(globalThis.location.search).get(name)")
private external fun queryParameter(name: String): String?
