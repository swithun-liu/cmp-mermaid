package com.swithun.cmpmermaid.compose

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView

/**
 * Stable Android View entry point for hosts that do not compile Compose source.
 */
class CMPMermaidView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {
    private var sourceState by mutableStateOf("")
    private var contentDescriptionState by mutableStateOf("Mermaid diagram")

    fun setMermaidSource(source: String?) {
        sourceState = source.orEmpty()
    }

    fun setMermaidContentDescription(contentDescription: String?) {
        contentDescriptionState = contentDescription.orEmpty()
    }

    @Composable
    override fun Content() {
        MermaidDiagram(
            source = sourceState,
            modifier = Modifier.fillMaxSize(),
            contentDescription = contentDescriptionState,
        )
    }
}
