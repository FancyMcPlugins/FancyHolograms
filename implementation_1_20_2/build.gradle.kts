plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.7"
}


val minecraftVersion = "1.20.2"


dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("de.oliver:FancyLib:36")
    compileOnly("com.viaversion:viaversion-api:5.3.0")
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