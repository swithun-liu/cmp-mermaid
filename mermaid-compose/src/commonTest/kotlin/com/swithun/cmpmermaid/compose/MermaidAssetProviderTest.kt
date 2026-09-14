package com.swithun.cmpmermaid.compose

import com.swithun.cmpmermaid.core.SceneAsset
import com.swithun.cmpmermaid.core.SceneAssetKind
import com.swithun.cmpmermaid.core.SceneRect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MermaidAssetProviderTest {
    @Test
    fun requiresExplicitNetworkAccess() {
        assertFalse(MermaidNetworkAccess.Disabled.allows("https://example.com/image.svg"))
        assertTrue(MermaidNetworkAccess.HttpAndHttps.allows("https://example.com/image.svg"))
        assertTrue(MermaidNetworkAccess.HttpAndHttps.allows("HTTP://example.com/image.png"))
        assertFalse(MermaidNetworkAccess.HttpAndHttps.allows("data:image/png;base64,payload"))
    }

    @Test
    fun reportsMissingProviderInsteadOfDroppingAsset() {
        runBlocking {
            val states = mutableMapOf<String, MermaidAssetState>()

            resolveMermaidAssets(
                assets = listOf(asset()),
                provider = null,
                onResolved = states::set,
            )

            val failure = assertIs<MermaidAssetState.Failed>(states.getValue("asset"))
            assertIs<MermaidAssetError.ProviderRequired>(failure.error)
        }
    }

    @Test
    fun convertsProviderExceptionToLoadFailure() {
        runBlocking {
            val states = mutableMapOf<String, MermaidAssetState>()

            resolveMermaidAssets(
                assets = listOf(asset()),
                provider = MermaidAssetProvider { throw IllegalStateException("network failed") },
                onResolved = states::set,
            )

            val failure = assertIs<MermaidAssetState.Failed>(states.getValue("asset"))
            assertEquals(
                "network failed",
                assertIs<MermaidAssetError.LoadFailed>(failure.error).message,
            )
        }
    }

    private fun asset() = SceneAsset(
        id = "asset",
        source = "https://example.com/image.svg",
        bounds = SceneRect(0f, 0f, 48f, 48f),
        kind = SceneAssetKind.Image,
    )
}
