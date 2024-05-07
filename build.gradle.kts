import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.github.goooler.shadow") version "8.1.7"
}

runPaper.folia.registerTask()

allprojects {
    group = "de.oliver"
    val buildId = System.getenv("BUILD_ID")
    version = "2.1.0-SNAPSHOT" + (if (buildId != null) ".$buildId" else "")
    description = "Simple, lightweight and fast hologram plugin using display entities"


    repositories {
        mavenCentral()

        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven(url = "https://repo.fancyplugins.de/snapshots")
        maven(url = "https://repo.smrt-1.com/releases")
        maven(url = "https://repo.viaversion.com/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":implementation_1_20_5", configuration = "reobf"))
    implementation(project(":implementation_1_20_4", configuration = "reobf"))
    implementation(project(":implementation_1_20_2", configuration = "reobf"))
    implementation(project(":implementation_1_20_1", configuration = "reobf"))
    implementation(project(":implementation_1_19_4", configuration = "reobf"))

    implementation("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")

    compileOnly("de.oliver:FancyNpcs:${findProperty("fancyNpcsVersion")}")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        dependsOn(":api:shadowJar")

        relocate("me.dave.chatcolorhandler", "de.oliver.fancyholograms.libs.chatcolorhandler")
        relocate("io.sentry", "de.oliver.fancyholograms.libs.sentry")
    }

    runServer {
        minecraftVersion(findProperty("minecraftVersion").toString())

        downloadPlugins {
            hangar("FancyNpcs", findProperty("fancyNpcsVersion").toString())
            hangar("PlaceholderAPI", "2.11.5")
            modrinth("miniplaceholders", "M6gjRuIx")

            hangar("ViaVersion", "4.9.3-SNAPSHOT+216")
            hangar("ViaBackwards", "4.9.2-SNAPSHOT+131")
        }
    }

    publishing {
        repositories {
            maven {
                name = "fancypluginsReleases"
                url = uri("https://repo.fancyplugins.de/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "fancypluginsSnapshots"
                url = uri("https://repo.fancyplugins.de/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = rootProject.name
                version = rootProject.version.toString()
                from(project.components["java"])
            }
        }
    }


    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything

        val props = mapOf(
            "description" to project.description,
            "version" to project.version,
            "hash" to getCurrentCommitHash(),
            "build" to (System.getenv("BUILD_ID") ?: "").ifEmpty { "undefined" }
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release = 21
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}