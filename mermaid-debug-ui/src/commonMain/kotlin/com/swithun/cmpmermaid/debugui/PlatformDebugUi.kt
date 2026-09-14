package com.swithun.cmpmermaid.debugui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal sealed interface OfficialRenderResult {
    data object Loading : OfficialRenderResult

    data class Ready(
        val width: Float,
        val height: Float,
    ) : OfficialRenderResult

    data class Error(
        val message: String,
    ) : OfficialRenderResult
}

@Composable
internal expect fun OfficialMermaidDiagram(
    source: String,
    layout: String,
    themeName: String? = null,
    modifier: Modifier = Modifier,
    onRenderResult: (OfficialRenderResult) -> Unit = {},
)

@Composable
internal expect fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)

@Composable
internal fun UnavailableOfficialDiagram(
    message: String,
    modifier: Modifier,
    onRenderResult: (OfficialRenderResult) -> Unit,
) {
    LaunchedEffect(message) {
        onRenderResult(OfficialRenderResult.Error(message))
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
