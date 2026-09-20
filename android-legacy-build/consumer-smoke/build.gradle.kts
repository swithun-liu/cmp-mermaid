plugins {
    id("com.android.library") version "7.4.2"
    kotlin("android") version "1.7.21"
}

val cmpMermaidVersion = providers.gradleProperty("VERSION_NAME")
    .orElse("0.1.1")

android {
    namespace = "com.swithun.cmpmermaid.legacy.smoke"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0-alpha02"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
        apiVersion = "1.7"
    }
}

dependencies {
    implementation(
        "io.github.swithun-liu:mermaid-compose-android-kotlin17:" +
            cmpMermaidVersion.get(),
    )
}
