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
    from(layout.projectDirectory.dir("../../mermaid-core/src/commonMain/kotlin"))
    into(generatedLegacySources)
    includeEmptyDirs = false

    filesMatching("**/gantt/**/*.kt") {
        filter { line: String ->
            line
                .replace(
                    "import kotlin.time.Clock",
                    "import kotlinx.datetime.Clock",
                )
                .replace(
                    "import kotlin.time.Instant",
                    "import kotlinx.datetime.Instant",
                )
        }
    }
    filesMatching("**/GanttDatePort.kt") {
        filter { line: String ->
            line
                .replace(Regex("""\.day\b"""), ".dayOfMonth")
                .replace(
                    "                day = day,",
                    "                dayOfMonth = day,",
                )
        }
    }
}

android {
    namespace = "com.swithun.cmpmermaid.core"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

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
    }
}
tasks.matching { it.name == "sourceReleaseJar" }.configureEach {
    dependsOn(prepareLegacySources)
}

dependencies {
    implementation("com.charleskorn.kaml:kaml:0.49.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
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
