plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}


val minecraftVersion = "1.20.6"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION


dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":api"))
    compileOnly("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
    compileOnly("de.oliver:FancySitula:${findProperty("fancySitulaVersion")}")
    compileOnly("com.viaversion:viaversion-api:${findProperty("viaversionVersion")}")
}


tasks {
    named("assemble") {
        dependsOn(named("reobfJar"))
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(21)
    }
}