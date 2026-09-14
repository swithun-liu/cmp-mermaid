package com.swithun.cmpmermaid.debugui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun OfficialMermaidDiagram(
    source: String,
    layout: String,
    themeName: String?,
    modifier: Modifier,
    onRenderResult: (OfficialRenderResult) -> Unit,
) {
    UnavailableOfficialDiagram(
        message = "Official Mermaid.js comparison is available on Android and Web.",
        modifier = modifier,
        onRenderResult = onRenderResult,
    )
}

@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) = Unit
