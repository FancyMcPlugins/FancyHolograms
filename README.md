![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyHolograms?style=flat-square)
[![Generic badge](https://img.shields.io/badge/folia-supported-green.svg)](https://shields.io/)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyHolograms/total?logo=GitHub&style=flat-square)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-108694.svg)](https://www.spigotmc.org/resources/fancy-holograms-1-19-4.108694/)
[![Downloads](https://img.shields.io/modrinth/dt/fancyholograms?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancyholograms/versions)

# Fancy Holograms
Simple, lightweight and fast hologram plugin using display entities.<br>
It is lightweight and fast (using [packets](https://wiki.vg/Protocol)).

PlaceholderAPI and MiniPlaceholders is supported.

**Only for minecraft server version 1.20 & 1.20.1**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_


## Get the plugin
You can download the latest versions at the following places:

- https://hangar.papermc.io/Oliver/FancyHolograms
- https://modrinth.com/plugin/fancyholograms
- https://github.com/FancyMcPlugins/FancyHolograms/releases
- https://www.spigotmc.org/resources/fancyholograms.108694/
- Build from source (``gradlew reobfJar``)

## Commands
/FancyHolograms version - Shows you the current plugin version
/FancyHolograms save - Saves all holograms
/FancyHolograms reload - Reloads the config and holograms
/Hologram help - Shows a list of all subcommands
/Hologram list - Shows a list of all holograms
/Hologram create (name) - Creates a new hologram at your location
/Hologram remove (hologram) - Removes a certain hologram
/Hologram copy (hologram) (new name) - Creates a copy of a hologram
/Hologram edit (hologram) position - Teleports the hologram to you
/Hologram edit (hologram) moveTo (x) (y) (z) [yaw] - Teleports the hologram to the location
/Hologram edit (hologram) setLine (line) (text...) - Sets the content of the line
/Hologram edit (hologram) addLine (text...) - Adds a line at the bottom
/Hologram edit (hologram) removeLine (line) - Removes a line
/Hologram edit (hologram) insertBefore (line) (text...) - Adds a line after another
/Hologram edit (hologram) insertAfter (line) (text...) - Adds a line before another
/Hologram edit (hologram) background (color) - Sets the background color
/Hologram edit (hologram) scale (factor) - Sets the scale (can be floats)
/Hologram edit (hologram) billboard (billboard) - Sets the billboard
/Hologram edit (hologram) shadowStrength (strength) - Sets the shadow strength
/Hologram edit (hologram) shadowRadius (radius) - Sets the shadow radius
/Hologram edit (hologram) updateTextInterval (seconds) - Sets the interval for refreshing the text (useful for placeholders)
/Hologram edit (hologram) linkWithNpc (npc) - Links a hologram with an NPC
/Hologram edit (hologram) unlinkWithNpc - Unlinks the hologram from the NPC


## Permissions
For the /hologram command - ``FancyHolograms.admin``

## Example images

![example1](exampleImages/example1.png)

![example2](exampleImages/example2.png)

![example3](exampleImages/example3.png)

![example4](exampleImages/example4.png)

![example5](exampleImages/example5.png)
