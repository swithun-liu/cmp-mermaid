import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
}

val generatedCorpus = layout.buildDirectory.dir(
    "generated/corpus/kotlin",
)

val prepareDeviceAuditCorpus by tasks.registering(Sync::class) {
    from(
        layout.projectDirectory.file(
            "../../mermaid-debug-ui/src/commonMain/kotlin/" +
                "com/swithun/cmpmermaid/debugui/generated/StabilityCorpus.kt",
        ),
    )
    into(
        generatedCorpus.map {
            it.dir("com/swithun/cmpmermaid/debugui/generated")
        },
    )
}

android {
    namespace = "com.swithun.cmpmermaid.legacyaudit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.swithun.cmpmermaid.legacyaudit"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.13-kotlin17-audit"
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

kotlin {
    sourceSets.getByName("main").kotlin.srcDir(generatedCorpus)
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(prepareDeviceAuditCorpus)
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.7"
        apiVersion = "1.7"
    }
}

dependencies {
    implementation(project(":mermaid-compose-android-kotlin17"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.foundation:foundation:1.6.8")
}
