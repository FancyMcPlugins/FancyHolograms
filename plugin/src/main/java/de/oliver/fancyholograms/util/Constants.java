package de.oliver.fancyholograms.util;

import java.text.DecimalFormat;

public enum Constants {
    ;

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#########.##");

    public final static DecimalFormat COORDINATES_DECIMAL_FORMAT = new DecimalFormat("#########.##");


    public static final String HELP_TEXT = """
                                           <#6696e3><b>FancyHolograms commands help:<reset>
                                           <#6696e3>- /hologram help <dark_gray>- <white>Shows all (sub)commands
                                           <#6696e3>- /hologram list <dark_gray>- <white>Shows you a overview of all holograms
                                           <#6696e3>- /hologram teleport <name> <dark_gray>- <white>Teleports you to a hologram
                                           <#6696e3>- /hologram create <name> <dark_gray>- <white>Creates a new hologram
                                           <#6696e3>- /hologram remove <name> <dark_gray>- <white>Removes a hologram
                                           <#6696e3>- /hologram copy <hologram> <new name> <dark_gray>- <white>Copies a hologram
                                           <#6696e3>- /hologram edit <hologram> addLine <text ...> <dark_gray>- <white>Adds a line at the bottom
                                           <#6696e3>- /hologram edit <hologram> removeLine <dark_gray>- <white>Removes a line at the bottom
                                           <#6696e3>- /hologram edit <hologram> insertBefore <line number> <text ...> <dark_gray>- <white>Inserts a line before another
                                           <#6696e3>- /hologram edit <hologram> insertAfter <line number> <text ...> <dark_gray>- <white>Inserts a line after another
                                           <#6696e3>- /hologram edit <hologram> setLine <line number> <text ...> <dark_gray>- <white>Edits the line
                                           <#6696e3>- /hologram edit <hologram> position <dark_gray>- <white>Teleports the hologram to you
                                           <#6696e3>- /hologram edit <hologram> moveTo <x> <y> <z> [yaw] <dark_gray>- <white>Teleports the hologram to the coordinates
                                           <#6696e3>- /hologram edit <hologram> scale <factor> <dark_gray>- <white>Changes the scale of the hologram
                                           <#6696e3>- /hologram edit <hologram> billboard <center|fixed|horizontal|vertical> <factor> <dark_gray>- <white>Changes the billboard of the hologram
                                           <#6696e3>- /hologram edit <hologram> background <color> <dark_gray>- <white>Changes the background of the hologram
                                           <#6696e3>- /hologram edit <hologram> textShadow <true|false> <dark_gray>- <white>Enables/disables the text shadow
                                           <#6696e3>- /hologram edit <hologram> shadowRadius <value> <dark_gray>- <white>Changes the shadow radius of the hologram
                                           <#6696e3>- /hologram edit <hologram> shadowStrength <value> <dark_gray>- <white>Changes the shadow strength of the hologram
                                           <#6696e3>- /hologram edit <hologram> updateTextInterval <seconds> <dark_gray>- <white>Sets the interval for updating the text
                                           """;

    public static final String HELP_TEXT_NPCS = """
                                                <#6696e3>- /hologram edit <hologram> linkWithNpc <npc name> <dark_gray>- <white>Links the hologram with an NPC
                                                <#6696e3>- /hologram edit <hologram> unlinkWithNpc <dark_gray>- <white>Unlinks the hologram with an NPC
                                                """;

}
