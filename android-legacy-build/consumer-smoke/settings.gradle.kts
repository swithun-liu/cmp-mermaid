import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("../../build/maven-repository")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "cmp-mermaid-kotlin17-consumer-smoke"
