package de.oliver.fancyholograms.util;

import de.oliver.fancylib.MessageHelper;

import java.text.DecimalFormat;

public enum Constants {
    ;

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#########.##");

    public final static DecimalFormat COORDINATES_DECIMAL_FORMAT = new DecimalFormat("#########.##");


    public static final String HELP_TEXT = """
            <%primary_color%><b>FancyHolograms commands help:<reset>
            <%primary_color%>- /hologram help <dark_gray>- <white>Shows all (sub)commands
            <%primary_color%>- /hologram list <dark_gray>- <white>Shows you a overview of all holograms
            <%primary_color%>- /hologram search <search term> <dark_gray>- <white>Searches for holograms
            <%primary_color%>- /hologram teleport <name> <dark_gray>- <white>Teleports you to a hologram
            <%primary_color%>- /hologram create <name> <dark_gray>- <white>Creates a new hologram
            <%primary_color%>- /hologram remove <name> <dark_gray>- <white>Removes a hologram
            <%primary_color%>- /hologram copy <hologram> <new name> <dark_gray>- <white>Copies a hologram
            <%primary_color%>- /hologram edit <hologram> addLine <text ...> <dark_gray>- <white>Adds a line at the bottom
            <%primary_color%>- /hologram edit <hologram> removeLine <dark_gray>- <white>Removes a line at the bottom
            <%primary_color%>- /hologram edit <hologram> insertBefore <line number> <text ...> <dark_gray>- <white>Inserts a line before another
            <%primary_color%>- /hologram edit <hologram> insertAfter <line number> <text ...> <dark_gray>- <white>Inserts a line after another
            <%primary_color%>- /hologram edit <hologram> setLine <line number> <text ...> <dark_gray>- <white>Edits the line
            <%primary_color%>- /hologram edit <hologram> position <dark_gray>- <white>Teleports the hologram to you
            <%primary_color%>- /hologram edit <hologram> moveTo <x> <y> <z> [yaw] [pitch] <dark_gray>- <white>Teleports the hologram to the coordinates
            <%primary_color%>- /hologram edit <hologram> rotate <degrees> <dark_gray>- <white>Rotates the hologram
            <%primary_color%>- /hologram edit <hologram> scale <factor> <dark_gray>- <white>Changes the scale of the hologram
            <%primary_color%>- /hologram edit <hologram> billboard <center|fixed|horizontal|vertical> <factor> <dark_gray>- <white>Changes the billboard of the hologram
            <%primary_color%>- /hologram edit <hologram> background <color> <dark_gray>- <white>Changes the background of the hologram
            <%primary_color%>- /hologram edit <hologram> textShadow <true|false> <dark_gray>- <white>Enables/disables the text shadow
            <%primary_color%>- /hologram edit <hologram> textAlignment <alignment> <dark_gray>- <white>Sets the text alignment
            <%primary_color%>- /hologram edit <hologram> shadowRadius <value> <dark_gray>- <white>Changes the shadow radius of the hologram
            <%primary_color%>- /hologram edit <hologram> shadowStrength <value> <dark_gray>- <white>Changes the shadow strength of the hologram
            <%primary_color%>- /hologram edit <hologram> updateTextInterval <seconds> <dark_gray>- <white>Sets the interval for updating the text
            """.replace("%primary_color%", MessageHelper.getPrimaryColor());

    public static final String HELP_TEXT_NPCS = """
            <%primary_color%>- /hologram edit <hologram> linkWithNpc <npc name> <dark_gray>- <white>Links the hologram with an NPC
            <%primary_color%>- /hologram edit <hologram> unlinkWithNpc <dark_gray>- <white>Unlinks the hologram with an NPC
            """.replace("%primary_color%", MessageHelper.getPrimaryColor());

}
