plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}


val minecraftVersion = "1.19.4"


dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("de.oliver:FancyLib:${findProperty("fancyLibVersion")}")
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

        options.release.set(17)
    }
}