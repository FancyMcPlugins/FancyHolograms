![Latest Version](https://img.shields.io/github/v/release/FancyMcPlugins/FancyHolograms?style=flat-square)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)
![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyHolograms/total?logo=GitHub&style=flat-square)
[![SpigotMC Downloads](https://badges.spiget.org/resources/downloads/spigotmc-orange-108694.svg)](https://www.spigotmc.org/resources/fancy-holograms-1-19-4.108694/)
[![Downloads](https://img.shields.io/modrinth/dt/fancyholograms?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancyholograms/versions)

# Fancy holograms
Create fancy looking holograms with the new 1.19.4 text display entities.<br>
It is lightweight and fast (using [packets](https://wiki.vg/Protocol)).

**Only for minecraft server version 1.19.4**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_


## Get the plugin
You can download the latest versions at the following places:

- https://hangar.papermc.io/Oliver/FancyHolograms
- https://modrinth.com/plugin/fancyholograms
- https://github.com/FancyMcPlugins/FancyHolograms/releases
- https://www.spigotmc.org/resources/fancyholograms.108694/
- Build from source (``gradlew reobfJar``)

## Commands
/hologram help<br>
/hologram version<br>
/hologram list<br>
/hologram teleport (name)<br>
/hologram create (name)<br>
/hologram remove (hologram name)<br>
/hologram copy (hologram name) (new name)<br>
/hologram edit (hologram name) addLine (text ...)<br>
/hologram edit (hologram name) removeLine (text ...)<br>
/hologram edit (hologram name) insertBefore (line number) (text ...)<br>
/hologram edit (hologram name) insertAfter (line number) (text ...)<br>
/hologram edit (hologram name) setLine (line number) (text ...)<br>
/hologram edit (hologram name) position<br>
/hologram edit (hologram name) moveTo (x) (y) (z) (optional: yaw)<br>
/hologram edit (hologram name) scale (factor)<br>
/hologram edit (hologram name) billboard (center|fixed|horizontal|vertical)<br>
/hologram edit (hologram name) background (color)<br>
/hologram edit (hologram name) shadowRadius (value)<br>
/hologram edit (hologram name) shadowStrength (value)<br>
/hologram edit (hologram name) updateTextInterval (seconds)<br>

/hologram edit (hologram) linkWithNpc (npc name)<br>
/hologram edit (hologram) unlinkWithNpc


## Permissions
For the /hologram command - ``FancyHolograms.admin``