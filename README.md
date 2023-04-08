[![Generic badge](https://img.shields.io/badge/version-1.0.4-orange.svg)](https://shields.io/)

# Fancy holograms
Create fancy looking holograms with the new 1.19.4 text display entities.<br>
It is lightweight and fast (using [packets](https://wiki.vg/Protocol)).

**Only for minecraft server version 1.19.4**<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_


## Get the plugin
You can download the latest versions at the following places:

- https://www.spigotmc.org/resources/fancyholograms.108694/
- https://modrinth.com/plugin/fancyholograms
- https://github.com/OliverSchlueter/FancyHologramsPlugin/releases
- Build from source (``gradlew reobfJar``)

## Commands
/hologram help<br>
/hologram version<br>
/hologram create (name)<br>
/hologram remove (hologram name)<br>
/hologram copy (hologram name) (new name)<br>
/hologram edit (hologram name) addLine (text ...)<br>
/hologram edit (hologram name) removeLine (text ...)<br>
/hologram edit (hologram name) setLine (line number) (text ...)<br>
/hologram edit (hologram name) position<br>
/hologram edit (hologram name) moveTo (x) (y) (z) (optional: yaw)<br>
/hologram edit (hologram name) scale (factor)<br>
/hologram edit (hologram name) billboard (center|fixed|horizontal|vertical)<br>
/hologram edit (hologram name) background<br>
/hologram edit (hologram name) updateTextInterval (seconds)<br>


## Permissions
For the /hologram command - ``FancyHolograms.admin``