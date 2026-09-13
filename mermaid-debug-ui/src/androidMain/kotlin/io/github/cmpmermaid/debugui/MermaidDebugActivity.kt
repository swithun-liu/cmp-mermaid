package io.github.cmpmermaid.debugui

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
            openPlayground = intent.getBooleanExtra(EXTRA_OPEN_PLAYGROUND, false),
        )
        setContent {
            MermaidDebugApp(options)
        }
    }

    companion object {
        const val EXTRA_AUDIT_DEMO_ID = "auditDemoId"
        const val EXTRA_AUDIT_PREVIEW = "auditPreview"
        const val EXTRA_AUDIT_LAYOUT = "auditLayout"
        const val EXTRA_OPEN_PLAYGROUND = "openPlayground"
    }
}

@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
