![](fancyholograms_title.png)

#

![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyHolograms?style=flat-square)
[![Generic badge](https://img.shields.io/badge/folia-supported-green.svg)](https://shields.io/)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyHolograms/total?logo=GitHub&style=flat-square)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-108694.svg)](https://www.spigotmc.org/resources/fancy-holograms-1-19-4.108694/)
[![Downloads](https://img.shields.io/modrinth/dt/fancyholograms?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancyholograms/versions)

Simple, lightweight and fast hologram plugin using display entities.<br>
It is lightweight and fast (using [packets](https://wiki.vg/Protocol)).

PlaceholderAPI and MiniPlaceholders is supported.

**Only for minecraft server versions 1.19.4 - 1.20.4**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin

You can download the latest versions at the following places:

- https://hangar.papermc.io/Oliver/FancyHolograms
- https://modrinth.com/plugin/fancyholograms
- https://github.com/FancyMcPlugins/FancyHolograms/releases
- https://www.spigotmc.org/resources/fancyholograms.108694/
- Build from source (``gradlew shadowJar``)

## Features

With FancyHolograms you can create fancy holograms that take advantage of the display entities. You are able to create
text, item and block holograms.

Properties you can modify:

- scale
- billoard
- rotation (yaw & pitch)
- background color
- text shadow
- text alignment
- placeholders in the text
- and more ...

The holograms are directly sent to the player via packets, which makes FancyHologram very fast and flexible.

## Commands

You can find a list of all commands with description and permissions [here](docs/commands.md).

## API

You can find the basic API documentation [here](docs/api/getting-started.md).

If you have any questions about the API, feel free to ask in the [discord](https://discord.gg/ZUgYCEJUEx).

## Example images

![example1](exampleImages/example1.png)

![example2](exampleImages/example2.png)

![example3](exampleImages/example3.png)

![example4](exampleImages/example4.png)

![example5](exampleImages/example5.png)

## Developer API

```kotlin
// repo
maven("https://repo.fancyplugins.de/releases")

// dependency
implementation("de.oliver:FancyHolograms:<version>")
```