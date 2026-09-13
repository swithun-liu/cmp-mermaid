package io.github.cmpmermaid.core.flowchart.upstream.elk

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError

/**
 * Executes Mermaid's locked elkjs worker on the current platform.
 */
internal expect object ElkJsRuntime {
    fun layout(graphJson: String): GMResult<String, MermaidError>
}
