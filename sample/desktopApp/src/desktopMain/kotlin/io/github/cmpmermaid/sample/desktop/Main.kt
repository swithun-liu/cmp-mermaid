package io.github.cmpmermaid.sample.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.github.cmpmermaid.debugui.MermaidDebugApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP Mermaid",
        state = WindowState(size = DpSize(1100.dp, 820.dp)),
    ) {
        MermaidDebugApp()
    }
}
