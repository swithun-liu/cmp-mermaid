package com.swithun.cmpmermaid.core.flowchart.upstream.elk

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError

/**
 * Executes Mermaid's locked elkjs worker on the current platform.
 */
internal expect object ElkJsRuntime {
    fun layout(graphJson: String): GMResult<String, MermaidError>
}
