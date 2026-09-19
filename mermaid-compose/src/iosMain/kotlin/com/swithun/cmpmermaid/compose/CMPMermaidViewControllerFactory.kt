package com.swithun.cmpmermaid.compose

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Stable Swift/Objective-C entry point for the precompiled CMPMermaid framework.
 */
class CMPMermaidViewControllerFactory {
    fun makeViewController(
        source: String,
        contentDescription: String = "Mermaid diagram",
    ): UIViewController = ComposeUIViewController {
        MermaidDiagram(
            source = source,
            contentDescription = contentDescription,
        )
    }
}
