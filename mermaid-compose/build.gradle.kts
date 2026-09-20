import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
}

val releaseVersion = providers.gradleProperty("VERSION_NAME").get()
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
        namespace = "com.swithun.cmpmermaid.compose"
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

    cocoapods {
        name = "CMPMermaid"
        summary = "Native Mermaid rendering for Kotlin and Compose Multiplatform."
        homepage = "https://github.com/swithun-liu/cmp-mermaid"
        authors = "swithun"
        license = "{ :type => 'MIT', :file => 'LICENSE' }"
        ios.deploymentTarget = "14.0"
        source = """
            { :http => 'https://github.com/swithun-liu/cmp-mermaid/releases/download/v$releaseVersion/CMPMermaid-$releaseVersion.zip' }
        """.trimIndent()
        framework {
            baseName = "CMPMermaid"
            isStatic = true
            export(project(":mermaid-core"))
        }
    }

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
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

compose.resources {
    packageOfResClass = "com.swithun.cmpmermaid.compose.generated.resources"
}
