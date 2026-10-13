@file:OptIn(ExperimentalWasmJsInterop::class)

package com.swithun.cmpmermaid.sample.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.swithun.cmpmermaid.debugui.MermaidDebugApp
import com.swithun.cmpmermaid.debugui.MermaidDebugAuditSource
import com.swithun.cmpmermaid.debugui.MermaidDebugLaunchOptions
import com.swithun.cmpmermaid.debugui.MermaidDebugPreview
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.ExperimentalWasmJsInterop
import org.w3c.dom.events.Event

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    val initialOptions = MermaidDebugLaunchOptions(
        auditDemoId = queryParameter("auditDemoId"),
        auditSource = MermaidDebugAuditSource.from(queryParameter("auditSource")),
        auditPreview = MermaidDebugPreview.from(queryParameter("auditPreview")),
        auditLayout = queryParameter("auditLayout") ?: "elk",
        auditThemeName = queryParameter("auditTheme"),
        openPlayground = queryParameter("openPlayground") == "true",
        playgroundDiagramId = queryParameter("playgroundDiagramId") ?: "flowchart",
        playgroundPreview =
            MermaidDebugPreview.from(queryParameter("playgroundPreview")),
        openLoadTest = queryParameter("openLoadTest") == "true",
        autoRunLoadTest = queryParameter("autoRunLoadTest") == "true",
    )
    ComposeViewport(body) {
        var launchOptions by remember { mutableStateOf(initialOptions) }
        DisposableEffect(Unit) {
            val listener: (Event) -> Unit = { event ->
                val demoId = auditEventDetail(event, "demoId")
                val auditSource = auditEventDetail(event, "auditSource")
                val preview = auditEventDetail(event, "preview")
                val layout = auditEventDetail(event, "layout")
                val themeName = auditEventDetail(event, "themeName")
                launchOptions = launchOptions.copy(
                    auditDemoId = demoId ?: launchOptions.auditDemoId,
                    auditSource = auditSource
                        ?.let(MermaidDebugAuditSource::from)
                        ?: launchOptions.auditSource,
                    auditPreview = preview
                        ?.let(MermaidDebugPreview::from)
                        ?: launchOptions.auditPreview,
                    auditLayout = layout ?: launchOptions.auditLayout,
                    auditThemeName = themeName?.ifEmpty { null },
                )
            }
            window.addEventListener(AUDIT_CASE_EVENT, listener)
            onDispose {
                window.removeEventListener(AUDIT_CASE_EVENT, listener)
            }
        }
        MermaidDebugApp(launchOptions)
    }
}

@JsFun("(name) => new URLSearchParams(globalThis.location.search).get(name)")
private external fun queryParameter(name: String): String?

@JsFun("(event, key) => event.detail?.[key] ?? null")
private external fun auditEventDetail(event: Event, key: String): String?

private const val AUDIT_CASE_EVENT = "cmp-mermaid:set-audit-case"
