import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    android {
        namespace = "io.github.cmpmermaid.core"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()
    wasmJs {
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kaml)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain {
            kotlin.srcDir("src/quickJsMain/kotlin")
            dependencies {
                implementation(libs.quickjs)
            }
        }
        jvmMain {
            kotlin.srcDir("src/quickJsMain/kotlin")
            dependencies {
                implementation(libs.quickjs)
            }
        }
    }
}
