pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cmp-mermaid"

include(":mermaid-core")
include(":mermaid-compose")
include(":mermaid-debug-ui")
include(":sample:androidApp")
include(":sample:desktopApp")
include(":sample:webApp")
