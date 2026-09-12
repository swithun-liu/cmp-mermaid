package io.github.cmpmermaid.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import com.caverock.androidsvg.SVG
import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.SceneAsset
import io.github.cmpmermaid.core.SceneAssetKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil
import kotlin.math.min

@Composable
internal actual fun rememberPlatformMermaidAssetProvider(): MermaidAssetProvider? =
    remember { AndroidMermaidAssetProvider() }

/**
 * Loads Android bitmap and SVG assets. Network sources require explicit opt-in.
 */
class AndroidMermaidAssetProvider(
    private val networkAccess: MermaidNetworkAccess = MermaidNetworkAccess.Disabled,
) : MermaidAssetProvider {
    override suspend fun resolve(
        asset: SceneAsset,
    ): GMResult<MermaidResolvedAsset, MermaidAssetError> = withContext(Dispatchers.IO) {
        if (asset.kind == SceneAssetKind.Icon) {
            return@withContext GMResult.Err(
                MermaidAssetError.UnsupportedSource(
                    source = asset.source,
                    message = "Icon pack '${asset.source.substringBefore(':')}' is not registered",
                ),
            )
        }
        val loaded = when (val bytesResult = loadBytes(asset.source)) {
            is GMResult.Ok -> bytesResult.value
            is GMResult.Err -> return@withContext bytesResult
        }
        decode(
            asset = asset,
            bytes = loaded.bytes,
            contentType = loaded.contentType,
        )
    }

    private fun loadBytes(
        source: String,
    ): GMResult<LoadedBytes, MermaidAssetError> {
        val normalized = source.trim()
        return when {
            normalized.startsWith("data:", ignoreCase = true) -> decodeDataUri(normalized)
            normalized.startsWith("https://", ignoreCase = true) ||
                normalized.startsWith("http://", ignoreCase = true) -> {
                if (networkAccess.allows(normalized)) {
                    loadHttp(normalized)
                } else {
                    GMResult.Err(
                        MermaidAssetError.UnsupportedSource(
                            source = source,
                            message = "Network image loading is disabled; inject an " +
                                "AndroidMermaidAssetProvider with HttpAndHttps access to enable it",
                        ),
                    )
                }
            }
            else -> GMResult.Err(MermaidAssetError.UnsupportedSource(source))
        }
    }

    private fun loadHttp(
        source: String,
    ): GMResult<LoadedBytes, MermaidAssetError> {
        val connection = try {
            URL(source).openConnection() as? HttpURLConnection
        } catch (failure: Throwable) {
            return loadFailure(source, failure.message)
        } ?: return GMResult.Err(MermaidAssetError.UnsupportedSource(source))
        return try {
            connection.instanceFollowRedirects = true
            connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.setRequestProperty("Accept", "image/*")
            val status = connection.responseCode
            if (status !in 200..299) {
                return loadFailure(source, "HTTP $status")
            }
            val finalProtocol = connection.url.protocol.lowercase()
            if (finalProtocol != "http" && finalProtocol != "https") {
                return GMResult.Err(MermaidAssetError.UnsupportedSource(connection.url.toString()))
            }
            val declaredLength = connection.contentLengthLong
            if (declaredLength > MAX_ASSET_BYTES) {
                return loadFailure(source, "Asset exceeds the ${MAX_ASSET_BYTES / 1_048_576} MiB limit")
            }
            val output = ByteArrayOutputStream(
                declaredLength.takeIf { it in 1..MAX_ASSET_BYTES }?.toInt() ?: DEFAULT_BUFFER_SIZE,
            )
            connection.inputStream.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var total = 0
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    total += count
                    if (total > MAX_ASSET_BYTES) {
                        return loadFailure(
                            source,
                            "Asset exceeds the ${MAX_ASSET_BYTES / 1_048_576} MiB limit",
                        )
                    }
                    output.write(buffer, 0, count)
                }
            }
            GMResult.Ok(
                LoadedBytes(
                    bytes = output.toByteArray(),
                    contentType = connection.contentType?.substringBefore(';'),
                ),
            )
        } catch (failure: Throwable) {
            loadFailure(source, failure.message)
        } finally {
            connection.disconnect()
        }
    }

    private fun decodeDataUri(
        source: String,
    ): GMResult<LoadedBytes, MermaidAssetError> {
        val comma = source.indexOf(',')
        if (comma <= DATA_PREFIX_LENGTH) {
            return loadFailure(source, "Malformed data URI")
        }
        val metadata = source.substring(DATA_PREFIX_LENGTH, comma)
        val payload = source.substring(comma + 1)
        val bytes = try {
            if (metadata.endsWith(";base64", ignoreCase = true)) {
                Base64.decode(payload, Base64.DEFAULT)
            } else {
                Uri.decode(payload).encodeToByteArray()
            }
        } catch (failure: Throwable) {
            return loadFailure(source, failure.message)
        }
        if (bytes.size > MAX_ASSET_BYTES) {
            return loadFailure(source, "Asset exceeds the ${MAX_ASSET_BYTES / 1_048_576} MiB limit")
        }
        return GMResult.Ok(
            LoadedBytes(
                bytes = bytes,
                contentType = metadata.substringBefore(';').takeIf(String::isNotBlank),
            ),
        )
    }

    private fun decode(
        asset: SceneAsset,
        bytes: ByteArray,
        contentType: String?,
    ): GMResult<MermaidResolvedAsset, MermaidAssetError> =
        if (isSvg(bytes, contentType, asset.source)) {
            decodeSvg(asset, bytes)
        } else {
            decodeBitmap(asset, bytes)
        }

    private fun decodeSvg(
        asset: SceneAsset,
        bytes: ByteArray,
    ): GMResult<MermaidResolvedAsset, MermaidAssetError> {
        val svg = try {
            SVG.getFromInputStream(ByteArrayInputStream(bytes))
        } catch (failure: Throwable) {
            return loadFailure(asset.source, failure.message)
        }
        val viewBox = svg.documentViewBox
        val intrinsicWidth = positiveDimension(svg.documentWidth)
            ?: positiveDimension(viewBox?.width())
            ?: asset.bounds.width
        val intrinsicHeight = positiveDimension(svg.documentHeight)
            ?: positiveDimension(viewBox?.height())
            ?: asset.bounds.height
        val decodeScale = min(
            1f,
            min(
                MAX_DECODE_DIMENSION / intrinsicWidth,
                MAX_DECODE_DIMENSION / intrinsicHeight,
            ),
        )
        val targetWidth = ceil(intrinsicWidth * decodeScale).toInt().coerceAtLeast(1)
        val targetHeight = ceil(intrinsicHeight * decodeScale).toInt().coerceAtLeast(1)
        val bitmap = try {
            Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888).also { target ->
                svg.setDocumentWidth(targetWidth.toFloat())
                svg.setDocumentHeight(targetHeight.toFloat())
                svg.renderToCanvas(Canvas(target))
            }
        } catch (failure: Throwable) {
            return loadFailure(asset.source, failure.message)
        }
        return GMResult.Ok(
            MermaidResolvedAsset(
                image = bitmap.asImageBitmap(),
                intrinsicWidth = intrinsicWidth.toInt().coerceAtLeast(1),
                intrinsicHeight = intrinsicHeight.toInt().coerceAtLeast(1),
            ),
        )
    }

    private fun decodeBitmap(
        asset: SceneAsset,
        bytes: ByteArray,
    ): GMResult<MermaidResolvedAsset, MermaidAssetError> {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return loadFailure(asset.source, "Unsupported or invalid image data")
        }
        val targetWidth = ceil(asset.bounds.width).toInt().coerceAtLeast(1)
        val targetHeight = ceil(asset.bounds.height).toInt().coerceAtLeast(1)
        var sampleSize = 1
        while (
            bounds.outWidth / (sampleSize * 2) >= targetWidth &&
            bounds.outHeight / (sampleSize * 2) >= targetHeight
        ) {
            sampleSize *= 2
        }
        val bitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            },
        ) ?: return loadFailure(asset.source, "Unsupported or invalid image data")
        return GMResult.Ok(
            MermaidResolvedAsset(
                image = bitmap.asImageBitmap(),
                intrinsicWidth = bounds.outWidth,
                intrinsicHeight = bounds.outHeight,
            ),
        )
    }

    private fun isSvg(
        bytes: ByteArray,
        contentType: String?,
        source: String,
    ): Boolean {
        if (contentType.equals("image/svg+xml", ignoreCase = true)) return true
        if (source.substringBefore('?').endsWith(".svg", ignoreCase = true)) return true
        val prefix = bytes.decodeToString(0, minOf(bytes.size, SVG_SNIFF_BYTES)).trimStart()
        return prefix.startsWith("<svg", ignoreCase = true) ||
            (prefix.startsWith("<?xml", ignoreCase = true) && "<svg" in prefix)
    }

    private fun positiveDimension(value: Float?): Float? =
        value?.takeIf { it.isFinite() && it > 0f }

    private fun <T> loadFailure(
        source: String,
        detail: String?,
    ): GMResult<T, MermaidAssetError> = GMResult.Err(
        MermaidAssetError.LoadFailed(
            source = source,
            message = detail?.takeIf(String::isNotBlank)
                ?.let { "Failed to load Mermaid asset '$source': $it" }
                ?: "Failed to load Mermaid asset '$source'",
        ),
    )

    private data class LoadedBytes(
        val bytes: ByteArray,
        val contentType: String?,
    )

    private companion object {
        const val DATA_PREFIX_LENGTH = 5
        const val CONNECT_TIMEOUT_MILLIS = 10_000
        const val READ_TIMEOUT_MILLIS = 15_000
        const val MAX_ASSET_BYTES = 8 * 1_048_576
        const val MAX_DECODE_DIMENSION = 4_096
        const val SVG_SNIFF_BYTES = 1_024
    }
}
