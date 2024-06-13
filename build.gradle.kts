import net.minecrell.pluginyml.paper.PaperPluginDescription
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

runPaper.folia.registerTask()

allprojects {
    group = "de.oliver"
    val buildId = System.getenv("BUILD_ID")
    version = "2.2.0" + (if (buildId != null) ".$buildId" else "")
    description = "Simple, lightweight and fast hologram plugin using display entities"

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven(url = "https://repo.fancyplugins.de/snapshots")
        maven(url = "https://repo.fancyplugins.de/releases")
        maven(url = "https://repo.smrt-1.com/releases")
        maven(url = "https://repo.viaversion.com/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation(project(":implementation_1_20_6"))
    implementation(project(":implementation_1_20_4", configuration = "reobf"))
    implementation(project(":implementation_1_20_2", configuration = "reobf"))
    implementation(project(":implementation_1_20_1", configuration = "reobf"))
    implementation(project(":implementation_1_19_4", configuration = "reobf"))

    implementation("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    implementation("de.oliver:FancySitula:${findProperty("fancySitulaVersion")}")

    compileOnly("de.oliver:FancyNpcs:${findProperty("fancyNpcsVersion")}")
    compileOnly("me.dave:ChatColorHandler:${findProperty("chatcolorhandlerVersion")}")
//    implementation("de.oliver.FancyAnalytics:api:${findProperty("fancyAnalyticsVersion")}")
//    implementation("org.incendo:cloud-core:${findProperty("cloudCoreVersion")}")
//    implementation("org.incendo:cloud-paper:${findProperty("cloudPaperVersion")}")
//    implementation("org.incendo:cloud-annotations:${findProperty("cloudAnnotationsVersion")}")
//    annotationProcessor("org.incendo:cloud-annotations:${findProperty("cloudAnnotationsVersion")}")
}

paper {
    main = "de.oliver.fancyholograms.FancyHolograms"
    bootstrapper = "de.oliver.fancyholograms.loaders.FancyHologramsBootstrapper"
    loader = "de.oliver.fancyholograms.loaders.FancyHologramsLoader"
    foliaSupported = true
    version = rootProject.version.toString()
    description = "Simple, lightweight and fast hologram plugin using display entities"
    apiVersion = "1.19"
    serverDependencies {
        register("FancyNpcs") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion(findProperty("minecraftVersion").toString())
//        minecraftVersion("1.20.4")

        downloadPlugins {
            modrinth("fancynpcs", "2.1.0")
            hangar("ViaVersion", "4.10.2")
            hangar("ViaBackwards", "4.10.2")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }

    shadowJar {
        archiveClassifier.set("")

        dependsOn(":api:shadowJar")
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
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(project.components["java"])
            }
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release = 21
        // For cloud-annotations, see https://cloud.incendo.org/annotations/#command-components
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
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

        filesMatching("paper-plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

tasks.publishAllPublicationsToHangar {
    dependsOn("shadowJar")
}

tasks.modrinth {
    dependsOn("shadowJar")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
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

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "FancyHolograms"
        channel = "Alpha"

        apiKey.set(System.getenv("HANGAR_PUBLISH_API_TOKEN"))

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6")
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_PUBLISH_API_TOKEN"))
    projectId.set("fancyholograms")
    versionNumber.set(project.version.toString())
    versionType.set("alpha")
    uploadFile.set(file("build/libs/${project.name}-${project.version}.jar"))
    gameVersions.addAll(listOf("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6"))
    loaders.add("paper")
}