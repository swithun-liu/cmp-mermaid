package com.swithun.cmpmermaid.core.flowchart.upstream.elk

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidError
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSLock
import platform.JavaScriptCore.JSContext

/**
 * Executes Mermaid's locked elkjs worker in Apple's system JavaScriptCore.
 *
 * Using the system runtime keeps the iOS library free of a precompiled
 * QuickJS binary whose deployment target can exceed the consuming app's.
 */
@OptIn(ExperimentalForeignApi::class)
internal actual object ElkJsRuntime {
    actual fun layout(graphJson: String): GMResult<String, MermaidError> {
        runtimeLock.lock()
        return try {
            val jsContext = when (val result = runtime()) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
            val input = Json.encodeToString(graphJson)
            val output = jsContext.evaluateScript("__cmpMermaidElkLayout($input)")
            jsContext.takeExceptionMessage()?.let { message ->
                return GMResult.Err(layoutError(message))
            }
            val outputJson = output?.toString()
                ?: return GMResult.Err(layoutError("JavaScriptCore returned no result"))
            GMResult.Ok(outputJson)
        } catch (failure: Throwable) {
            GMResult.Err(layoutError(failure.message ?: "unknown error"))
        } finally {
            runtimeLock.unlock()
        }
    }

    private fun runtime(): GMResult<JSContext, MermaidError> {
        runtimeInstance?.let { return GMResult.Ok(it) }

        val jsContext = JSContext()
        jsContext.evaluateScript(ElkWorkerSource.source)
        jsContext.takeExceptionMessage()?.let { message ->
            return GMResult.Err(layoutError(message))
        }
        runtimeInstance = jsContext
        return GMResult.Ok(jsContext)
    }

    private fun JSContext.takeExceptionMessage(): String? {
        val currentException = exception ?: return null
        exception = null
        return currentException.toString()
    }

    private fun layoutError(message: String): MermaidError.Layout {
        return MermaidError.Layout("elkjs ${ElkWorkerSource.VERSION} failed: $message")
    }

    private val runtimeLock = NSLock()
    private var runtimeInstance: JSContext? = null
}
