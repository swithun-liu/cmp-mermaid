plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.swithun.cmpmermaid.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.swithun.cmpmermaid.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.7"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":mermaid-debug-ui"))
}
