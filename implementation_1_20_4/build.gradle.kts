plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.11"
}


val minecraftVersion = "1.20.4"


dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("de.oliver:FancyLib:1.0.5")
    compileOnly("com.viaversion:viaversion-api:4.7.0")
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