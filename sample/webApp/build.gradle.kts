import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "cmp-mermaid.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain {
            resources.srcDir(
                rootProject.file("mermaid-debug-ui/src/androidMain/assets"),
            )
            dependencies {
                implementation(project(":mermaid-debug-ui"))
                implementation(compose.ui)
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3.1")
            }
        }
    }
}
