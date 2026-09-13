package io.github.cmpmermaid.core.flowchart.upstream.elk

import com.dokar.quickjs.QuickJs
import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Executes the locked elkjs worker without a DOM or WebView.
 *
 * Mermaid's adapter remains Kotlin. Only Eclipse ELK's generated worker runs
 * in QuickJS, matching Mermaid 12.0.0's direct elkjs dependency.
 */
internal actual object ElkJsRuntime {
    actual fun layout(graphJson: String): GMResult<String, MermaidError> {
        val result = try {
            val input = Json.encodeToString(graphJson)
            runBlocking {
                runtimeMutex.withLock {
                    withTimeout(LAYOUT_TIMEOUT_MILLIS) {
                        runtime().evaluate<String>(
                            code = "__cmpMermaidElkLayout($input)",
                            filename = "cmp-mermaid-elk-layout.js",
                        )
                    }
                }
            }
        } catch (failure: Throwable) {
            return GMResult.Err(
                MermaidError.Layout(
                    "elkjs ${ElkWorkerSource.VERSION} failed: " +
                        (failure.message ?: "unknown error"),
                ),
            )
        }
        return GMResult.Ok(result)
    }

    private suspend fun runtime(): QuickJs {
        runtimeInstance?.let { return it }
        return QuickJs.create(jobDispatcher = Dispatchers.Default).also { quickJs ->
            quickJs.memoryLimit = RUNTIME_MEMORY_LIMIT_BYTES
            quickJs.maxStackSize = RUNTIME_STACK_LIMIT_BYTES
            quickJs.evaluate<Any?>(
                code = ElkWorkerSource.source,
                filename = "elk-worker-${ElkWorkerSource.VERSION}.js",
            )
            runtimeInstance = quickJs
        }
    }

    private val runtimeMutex = Mutex()
    private var runtimeInstance: QuickJs? = null

    private const val RUNTIME_MEMORY_LIMIT_BYTES = 256L * 1024L * 1024L
    private const val RUNTIME_STACK_LIMIT_BYTES = 2L * 1024L * 1024L
    private const val LAYOUT_TIMEOUT_MILLIS = 10_000L
}
