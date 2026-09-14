package com.swithun.cmpmermaid.sample.desktop

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.swithun.cmpmermaid.debugui.MermaidDebugApp
import com.swithun.cmpmermaid.debugui.MermaidDebugLaunchOptions

fun main(args: Array<String>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP Mermaid",
        state = WindowState(size = DpSize(1100.dp, 820.dp)),
    ) {
        MermaidDebugApp(
            MermaidDebugLaunchOptions(
                openLoadTest = "--load-test" in args,
                autoRunLoadTest = "--load-test" in args,
            ),
        )
    }
}
