import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    android {
        namespace = "com.swithun.cmpmermaid.compose"
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
            api(project(":mermaid-core"))
            api(compose.ui)
            implementation(compose.foundation)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.android.svg)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

compose.resources {
    packageOfResClass = "com.swithun.cmpmermaid.compose.generated.resources"
}
