import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    signing
}

val generatedLegacySources = layout.buildDirectory.dir(
    "generated/legacyMain/kotlin",
)

val prepareLegacySources by tasks.registering(Sync::class) {
    from(layout.projectDirectory.dir("../../mermaid-compose/src/commonMain/kotlin")) {
        exclude(
            "com/swithun/cmpmermaid/compose/MermaidFontFamilyResolver.kt",
        )
    }
    from(layout.projectDirectory.dir("../../mermaid-compose/src/androidMain/kotlin"))
    into(generatedLegacySources)
    includeEmptyDirs = false
}

android {
    namespace = "com.swithun.cmpmermaid.compose"
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

    sourceSets.getByName("main").resources.srcDir(
        "../../mermaid-compose/src/commonMain/composeResources",
    )
    sourceSets.getByName("main").res.srcDir(
        "../../mermaid-compose/src/commonMain/composeResources",
    )

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir(generatedLegacySources)
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(prepareLegacySources)
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
        apiVersion = "1.7"
        freeCompilerArgs = freeCompilerArgs + "-Xmulti-platform"
    }
}
tasks.matching { it.name == "sourceReleaseJar" }.configureEach {
    dependsOn(prepareLegacySources)
}

dependencies {
    api(project(":mermaid-core-android-kotlin17"))
    api("androidx.compose.runtime:runtime:1.6.8")
    api("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.animation:animation:1.6.8")
    implementation("androidx.compose.foundation:foundation:1.6.8")
    implementation("com.caverock:androidsvg-aar:1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = project.name
                artifact(javadocJar)
            }
        }
    }
}
