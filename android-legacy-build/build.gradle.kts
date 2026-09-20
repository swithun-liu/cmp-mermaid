import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    base
    id("com.android.library") version "7.4.2" apply false
    kotlin("android") version "1.7.21" apply false
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
                    name = "release"
                    url = rootProject.layout.projectDirectory
                        .dir("../build/maven-repository")
                        .asFile
                        .toURI()
                }
            }
            publications.withType<MavenPublication>().configureEach {
                pom {
                    name.set(project.name)
                    description.set(
                        "Android artifact of CMP Mermaid compiled with Kotlin 1.7.21.",
                    )
                    url.set("https://github.com/swithun-liu/cmp-mermaid")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("swithun")
                            name.set("swithun")
                            url.set("https://github.com/swithun-liu")
                        }
                    }
                    scm {
                        url.set("https://github.com/swithun-liu/cmp-mermaid")
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

    pluginManager.withPlugin("signing") {
        val signingKey = providers.gradleProperty("signingInMemoryKey")
        val signingPassword = providers.gradleProperty("signingInMemoryKeyPassword")

        extensions.configure<SigningExtension> {
            if (signingKey.isPresent) {
                useInMemoryPgpKeys(
                    signingKey.get(),
                    signingPassword.orNull,
                )
            }
            sign(extensions.getByType<PublishingExtension>().publications)
        }
        tasks.withType<Sign>().configureEach {
            onlyIf { signingKey.isPresent }
        }
    }
}

val publishedModules = listOf(
    "mermaid-core-android-kotlin17",
    "mermaid-compose-android-kotlin17",
)

tasks.register("verifyLegacyPublicationCoordinates") {
    group = "verification"
    description = "Verifies the Kotlin 1.7 Android publication coordinates."
    dependsOn(
        publishedModules.map { module ->
            ":$module:generatePomFileForReleasePublication"
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
                .file("$module/build/publications/release/pom-default.xml")
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

tasks.register("publishLegacyToReleaseRepository") {
    group = "publishing"
    description = "Publishes both Kotlin 1.7 Android artifacts to the release repository."
    dependsOn(
        publishedModules.map { module ->
            ":$module:publishReleasePublicationToReleaseRepository"
        },
    )
}
