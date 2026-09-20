import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.maven.publish)
}

val nativeDebugPathMap = listOf(
    rootProject.projectDir.absolutePath to ".",
    System.getProperty("user.home") to "/toolchain",
).joinToString(",") { (source, target) -> "$source=$target" }
val klibPathArguments = listOf(
    "-Xklib-normalize-absolute-path",
    "-Xklib-relative-path-base=${rootProject.projectDir.absolutePath}",
)
val nativeKlibPathArguments = klibPathArguments +
    "-Xdebug-prefix-map=$nativeDebugPathMap"

mavenPublishing {
    publishToMavenCentral()
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
    android {
        namespace = "com.swithun.cmpmermaid.core"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm()
    wasmJs {
        compilerOptions {
            freeCompilerArgs.addAll(klibPathArguments)
        }
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(nativeKlibPathArguments)
                }
            }
        }
        binaries.configureEach {
            freeCompilerArgs += "-Xdebug-prefix-map=$nativeDebugPathMap"
        }
    }

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
    }
}
