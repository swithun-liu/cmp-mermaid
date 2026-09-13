package io.github.cmpmermaid.sample

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import org.json.JSONObject
import java.io.ByteArrayInputStream

private const val OFFICIAL_MERMAID_URL =
    "https://appassets.androidplatform.net/assets/official-mermaid.html"

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
internal fun OfficialMermaidDiagram(
    source: String,
    layout: String,
    modifier: Modifier = Modifier,
    onRenderResult: (OfficialRenderResult) -> Unit = {},
) {
    var rendererGeneration by remember { mutableIntStateOf(0) }
    val currentOnRenderResult by rememberUpdatedState(onRenderResult)
    key(rendererGeneration) {
        AndroidView(
            factory = { context ->
                OfficialMermaidWebView(
                    context = context,
                    onRenderResult = { result ->
                        currentOnRenderResult(result)
                    },
                    onRendererProcessGone = {
                        rendererGeneration += 1
                    },
                )
            },
            modifier = modifier,
            onReset = { webView ->
                webView.reset()
            },
            onRelease = { webView ->
                webView.release()
            },
            update = { webView ->
                webView.render(source, layout)
            },
        )
    }
}

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
private class OfficialMermaidWebView(
    context: Context,
    onRenderResult: (OfficialRenderResult) -> Unit,
    onRendererProcessGone: () -> Unit,
) : WebView(context) {
    private var pageReady = false
    private var released = false
    private var pendingSource = ""
    private var pendingLayout = "elk"
    private var dispatchedSource: String? = null
    private var dispatchedLayout: String? = null
    private val renderBridge = OfficialRenderBridge(onRenderResult)

    init {
        setBackgroundColor(Color.WHITE)
        // Avoid stale hardware-layer fragments when this WebView is clipped by the scroll container.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            allowContentAccess = false
            allowFileAccess = false
            disableFileUrlAccess()
            blockNetworkLoads = true
            blockNetworkImage = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = false
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }
        addJavascriptInterface(renderBridge, RENDER_BRIDGE_NAME)
        webViewClient = LocalAssetWebViewClient(
            context = context,
            onPageReady = {
                pageReady = true
                dispatchRender()
            },
            onRendererProcessGone = {
                release()
                onRendererProcessGone()
            },
        )
        loadUrl(OFFICIAL_MERMAID_URL)
    }

    fun render(
        source: String,
        layout: String,
    ) {
        if (released) {
            return
        }
        pendingSource = source
        pendingLayout = layout
        dispatchRender()
    }

    fun reset() {
        if (released) {
            return
        }
        pageReady = false
        dispatchedSource = null
        dispatchedLayout = null
        loadUrl(OFFICIAL_MERMAID_URL)
    }

    fun release() {
        if (released) {
            return
        }
        released = true
        pageReady = false
        stopLoading()
        removeJavascriptInterface(RENDER_BRIDGE_NAME)
        removeAllViews()
        destroy()
    }

    private fun dispatchRender() {
        if (!pageReady) {
            return
        }
        if (pendingSource == dispatchedSource && pendingLayout == dispatchedLayout) {
            return
        }
        dispatchedSource = pendingSource
        dispatchedLayout = pendingLayout
        renderBridge.loading()
        val sourceArgument = JSONObject.quote(pendingSource)
            .replace("\u2028", "\\u2028")
            .replace("\u2029", "\\u2029")
        val layoutArgument = JSONObject.quote(pendingLayout)
        evaluateJavascript(
            "window.renderDiagram($sourceArgument, $layoutArgument);",
            null,
        )
    }

    @Suppress("DEPRECATION")
    private fun WebSettings.disableFileUrlAccess() {
        allowFileAccessFromFileURLs = false
        allowUniversalAccessFromFileURLs = false
    }

    private companion object {
        const val RENDER_BRIDGE_NAME = "CmpMermaidBridge"
    }
}

private class OfficialRenderBridge(
    private val onRenderResult: (OfficialRenderResult) -> Unit,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun loading() {
        dispatch(OfficialRenderResult.Loading)
    }

    @JavascriptInterface
    fun onRenderReady(
        width: String,
        height: String,
    ) {
        val parsedWidth = width.toFloatOrNull()
        val parsedHeight = height.toFloatOrNull()
        val result = if (
            parsedWidth != null &&
            parsedWidth.isFinite() &&
            parsedWidth > 0f &&
            parsedHeight != null &&
            parsedHeight.isFinite() &&
            parsedHeight > 0f
        ) {
            OfficialRenderResult.Ready(parsedWidth, parsedHeight)
        } else {
            OfficialRenderResult.Error("Official Mermaid returned an invalid SVG size")
        }
        dispatch(result)
    }

    @JavascriptInterface
    fun onRenderError(message: String) {
        dispatch(OfficialRenderResult.Error(message))
    }

    private fun dispatch(result: OfficialRenderResult) {
        mainHandler.post {
            onRenderResult(result)
        }
    }
}

@SuppressLint("MissingOnRenderProcessGone")
private class LocalAssetWebViewClient(
    context: Context,
    private val onPageReady: () -> Unit,
    private val onRendererProcessGone: () -> Unit,
) : WebViewClient() {
    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler(
            "/assets/",
            WebViewAssetLoader.AssetsPathHandler(context),
        )
        .build()

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
    ): WebResourceResponse {
        if (!request.url.isTrustedAssetUrl()) {
            return blockedResponse()
        }
        return assetLoader.shouldInterceptRequest(request.url)
            ?: blockedResponse()
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean = request.url.toString() != OFFICIAL_MERMAID_URL

    override fun onPageFinished(
        view: WebView,
        url: String,
    ) {
        if (url == OFFICIAL_MERMAID_URL) {
            onPageReady()
        }
    }

    override fun onRenderProcessGone(
        view: WebView,
        detail: RenderProcessGoneDetail,
    ): Boolean {
        onRendererProcessGone()
        return true
    }

    private fun Uri.isTrustedAssetUrl(): Boolean =
        scheme == "https" &&
            host == WebViewAssetLoader.DEFAULT_DOMAIN &&
            path?.startsWith("/assets/") == true

    private fun blockedResponse(): WebResourceResponse =
        WebResourceResponse(
            "text/plain",
            Charsets.UTF_8.name(),
            ByteArrayInputStream(ByteArray(0)),
        )
}
