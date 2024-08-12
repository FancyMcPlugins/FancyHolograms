<div align="center">

![Banner](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/banner.png?raw=true)

[![GitHub Release](https://img.shields.io/github/v/release/FancyMcPlugins/FancyHolograms?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/FancyMcPlugins/FancyHolograms/releases/latest)
[![Supports Folia](https://img.shields.io/badge/folia-supported-%23F9D879?labelColor=%2313154E&color=%234A44A6)](https://papermc.io/software/folia)
[![Discord](https://img.shields.io/discord/899740810956910683?cacheSeconds=3600&logo=discord&logoColor=white&label=%20&labelColor=%235865F2&color=%23707BF4)](https://discord.gg/ZUgYCEJUEx)
[![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyHolograms/total?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/FancyMcPlugins/FancyHolograms/releases/latest)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/fancyholograms?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/fancyholograms)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/FancyMcPlugins/FancyHolograms?logo=codefactor&logoColor=white&label=%20)](https://www.codefactor.io/repository/github/fancymcplugins/fancyholograms/issues/main)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/fancyholograms)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Oliver/FancyHolograms)

<br />

Simple, lightweight and feature-rich hologram plugin for **[Paper](https://papermc.io/software/paper)** (
and [Folia](https://papermc.io/software/folia)) servers using [display entities](https://minecraft.wiki/w/Display)
and packets.

</div>

## Features

With this plugin you can create holograms with customizable properties like:

- **Hologram Type** (text, item or block)
- **Position**, **Rotation** and **Scale**
- **Text Alignment**, **Background Color** and **Shadow**.
- **Billboard** (fixed, center, horizontal, vertical)
- **MiniMessage** formatting.
- Placeholders support through [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)
  and [MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders) integration.
- [FancyNpcs](ttps://github.com/FancyMcPlugins/FancyNpcs) integration.
- ...and much more!

Check out **[images section](#images)** down below.

<br />

## Installation

Paper **1.19.4** - **1.21.1** with **Java 21** (or higher) is required. Plugin should also work on **Paper** forks.

**Spigot** is **not** supported.

### Download (Stable)

- **[Hangar](https://hangar.papermc.io/Oliver/FancyHolograms)**
- **[Modrinth](https://modrinth.com/plugin/fancyholograms)**
- **[GitHub Releases](https://github.com/FancyMcPlugins/FancyHolograms/releases)**

### Download (Development Builds)

- **[Jenkins CI](https://jenkins.fancyplugins.de/job/FancyHolograms/)**
- **[FancyPlugins Website](https://fancyplugins.de/FancyHolograms/download)**

<br />

## Documentation

Official documentation is hosted **[here](https://fancyplugins.de/docs/fancyholograms.html)**. Quick reference:

- **[Getting Started](https://fancyplugins.de/docs/fh-getting-started.html)**
- **[Command Reference](https://fancyplugins.de/docs/fh-commands.html)**
- **[Using API](https://fancyplugins.de/docs/fh-api.html)**

**Have more questions?** Feel free to ask them on our **[Discord](https://discord.gg/ZUgYCEJUEx)** server.

<br />

## Developer API

More information can be found in **[Documentation](https://fancyplugins.de/docs/fh-api.html)**
and [Javadocs](https://fancyplugins.de/javadocs/fancyholograms/).

### Maven

```xml

<repository>
    <id>fancyplugins-releases</id>
    <name>FancyPlugins Repository</name>
    <url>https://repo.fancyplugins.de/releases</url>
</repository>
```

```xml

<dependency>
    <groupId>de.oliver</groupId>
    <artifactId>FancyHolograms</artifactId>
    <version>[VERSION]</version>
    <scope>provided</version>
</dependency>
```

### Gradle

```groovy
repositories {
    maven("https://repo.fancyplugins.de/releases")
}

dependencies {
    compileOnly("de.oliver:FancyHolograms:[VERSION]")
}
```

<br />

## Building

Follow these steps to build the plugin locally:

```shell
# Cloning repository.
$ git clone https://github.com/FancyMcPlugins/FancyHolograms.git
# Entering cloned repository.
$ cd FancyHolograms
# Compiling and building artifacts.
$ gradlew shadowJar
# Once successfully built, plugin .jar can be found in /build/libs directory.
```

<br />

## Images

Images showcasing the plugin, sent to us by our community.

![Screenshot 1](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example1.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 2](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example2.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 3](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example3.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 4](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example4.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 5](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example5.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>
