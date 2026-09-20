package com.swithun.cmpmermaid.debugui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

/**
 * Optional Android entry point intended for debugImplementation dependencies.
 */
class MermaidDebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = MermaidDebugLaunchOptions(
            auditDemoId = intent.getStringExtra(EXTRA_AUDIT_DEMO_ID),
            auditPreview = MermaidDebugPreview.from(
                intent.getStringExtra(EXTRA_AUDIT_PREVIEW),
            ),
            auditLayout = intent.getStringExtra(EXTRA_AUDIT_LAYOUT) ?: "elk",
            auditThemeName = intent.getStringExtra(EXTRA_AUDIT_THEME),
            openPlayground = intent.getBooleanExtra(EXTRA_OPEN_PLAYGROUND, false),
            playgroundDiagramId =
                intent.getStringExtra(EXTRA_PLAYGROUND_DIAGRAM_ID) ?: "flowchart",
            playgroundPreview = MermaidDebugPreview.from(
                intent.getStringExtra(EXTRA_PLAYGROUND_PREVIEW),
            ),
            openLoadTest = intent.getBooleanExtra(EXTRA_OPEN_LOAD_TEST, false),
            autoRunLoadTest = intent.getBooleanExtra(EXTRA_AUTO_RUN_LOAD_TEST, false),
        )
        setContent {
            MermaidDebugApp(options)
        }
    }

    companion object {
        const val EXTRA_AUDIT_DEMO_ID = "auditDemoId"
        const val EXTRA_AUDIT_PREVIEW = "auditPreview"
        const val EXTRA_AUDIT_LAYOUT = "auditLayout"
        const val EXTRA_AUDIT_THEME = "auditTheme"
        const val EXTRA_OPEN_PLAYGROUND = "openPlayground"
        const val EXTRA_PLAYGROUND_DIAGRAM_ID = "playgroundDiagramId"
        const val EXTRA_PLAYGROUND_PREVIEW = "playgroundPreview"
        const val EXTRA_OPEN_LOAD_TEST = "openLoadTest"
        const val EXTRA_AUTO_RUN_LOAD_TEST = "autoRunLoadTest"
    }
}

@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
