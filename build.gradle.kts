import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    group = providers.gradleProperty("GROUP").get()
    version = providers.gradleProperty("VERSION_NAME").get()
}

subprojects {
    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "build"
                    url = rootProject.layout.buildDirectory.dir(
                        "maven-repository",
                    ).get().asFile.toURI()
                }
            }
            publications.withType<MavenPublication>().configureEach {
                pom {
                    name.set(project.name)
                    description.set(
                        "Native Mermaid renderer for Kotlin and Compose Multiplatform.",
                    )
                    url.set("https://github.com/swithun-liu/cmp-mermaid")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set(
                                "https://opensource.org/licenses/MIT",
                            )
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("swithun")
                            name.set("swithun")
                        }
                    }
                    scm {
                        url.set(
                            "https://github.com/swithun-liu/cmp-mermaid",
                        )
                        connection.set(
                            "scm:git:https://github.com/swithun-liu/cmp-mermaid.git",
                        )
                        developerConnection.set(
                            "scm:git:ssh://git@github.com/swithun-liu/cmp-mermaid.git",
                        )
                    }
                }
            }
        }
    }
}

val publishedModules = listOf(
    "mermaid-core",
    "mermaid-compose",
    "mermaid-debug-ui",
)

tasks.register("verifyPublicationCoordinates") {
    group = "verification"
    description = "Verifies the root Maven coordinates for published modules."
    dependsOn(
        publishedModules.map { module ->
            ":$module:generatePomFileForKotlinMultiplatformPublication"
        },
    )

    doLast {
        val expectedGroup = providers.gradleProperty("GROUP").get()
        val expectedVersion = providers.gradleProperty("VERSION_NAME").get()
        val documentBuilder = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()

        publishedModules.forEach { module ->
            val pom = rootProject.layout.projectDirectory
                .file("$module/build/publications/kotlinMultiplatform/pom-default.xml")
                .asFile
            val document = documentBuilder.parse(pom)
            val actualGroup = document
                .getElementsByTagName("groupId")
                .item(0)
                .textContent
            val actualArtifact = document
                .getElementsByTagName("artifactId")
                .item(0)
                .textContent
            val actualVersion = document
                .getElementsByTagName("version")
                .item(0)
                .textContent
            val expected = "$expectedGroup:$module:$expectedVersion"
            val actual = "$actualGroup:$actualArtifact:$actualVersion"

            if (actual != expected) {
                throw GradleException(
                    "Expected publication $expected, found $actual",
                )
            }
            logger.lifecycle("Verified publication $actual")
        }
    }
}
