package io.github.cmpmermaid.core.flowchart.upstream.elk

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError

/**
 * Browsers already provide a JavaScript runtime, so the Web target evaluates
 * the same locked worker source directly instead of embedding QuickJS.
 */
internal actual object ElkJsRuntime {
    private var initialized = false

    actual fun layout(graphJson: String): GMResult<String, MermaidError> {
        return try {
            if (!initialized) {
                evaluateWorker(ElkWorkerSource.source)
                initialized = true
            }
            GMResult.Ok(runLayout(graphJson))
        } catch (failure: Throwable) {
            GMResult.Err(
                MermaidError.Layout(
                    "elkjs ${ElkWorkerSource.VERSION} failed: " +
                        (failure.message ?: "unknown error"),
                ),
            )
        }
    }
}

@JsFun("(source) => globalThis.eval(source)")
private external fun evaluateWorker(source: String)

@JsFun("(input) => globalThis.__cmpMermaidElkLayout(input)")
private external fun runLayout(input: String): String
