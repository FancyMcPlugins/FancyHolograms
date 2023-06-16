package de.oliver.fancyholograms.commands;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.Hologram;
import de.oliver.fancyholograms.events.HologramCreateEvent;
import de.oliver.fancyholograms.events.HologramModifyEvent;
import de.oliver.fancyholograms.events.HologramRemoveEvent;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.Npc;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class HologramCMD implements CommandExecutor, TabExecutor {

    private final static DecimalFormat COORDINATES_DECIMAL_FORMAT = new DecimalFormat("#########.##");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Stream.of("help", "list", "teleport", "create", "remove", "edit", "copy").filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("edit")){
            boolean usingNpcs = FancyHolograms.getInstance().isUsingFancyNpcs();
            return Stream.of("position", "moveTo", "setLine", "addLine", "removeLine", "insertAfter", "insertBefore", "billboard", "scale", "background", "updateTextInterval", "shadowRadius", "shadowStrength", "textShadow", usingNpcs ? "linkWithNpc" : "", usingNpcs ? "unlinkWithNpc" : "").filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase())).toList();
        } else if(args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("copy") || args[0].equalsIgnoreCase("teleport"))){
            return FancyHolograms.getInstance().getHologramManager().getAllHolograms().stream().map(Hologram::getName).filter(input -> input.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        } else if(args.length == 4 && (args[2].equalsIgnoreCase("setLine") || args[2].equalsIgnoreCase("removeLine"))){
            return Arrays.asList("1", "2", "3");
        } else if(args.length == 4 && args[2].equalsIgnoreCase("billboard")){
            return Arrays.stream(Display.BillboardConstraints.values()).map(Display.BillboardConstraints::getSerializedName).filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase())).toList();
        } else if(args.length == 4 && args[2].equalsIgnoreCase("background")){
            List<String> suggestions = new ArrayList<>(Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(ChatFormatting::getName).toList());
            suggestions.add("reset");
            suggestions.add("transparent");
            return suggestions.stream().filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase())).toList();
        } else if(args.length == 4 && args[2].equalsIgnoreCase("textShadow")){
            return Arrays.asList("true", "false");
        } else if(args.length >= 4 && args[2].equalsIgnoreCase("moveTo")){
            if(!(sender instanceof Player p)){
                return null;
            }

            Block target = p.getTargetBlockExact(10);

            if(target == null){
                return null;
            }

            switch (args.length) {
                case 4 -> { return Arrays.asList(String.valueOf(target.getX())); }
                case 5 -> { return Arrays.asList(String.valueOf(target.getY())); }
                case 6 -> { return Arrays.asList(String.valueOf(target.getZ())); }
            }
        } else if(args.length == 4 && args[2].equalsIgnoreCase("linkWithNpc") && FancyHolograms.getInstance().isUsingFancyNpcs()){
            return FancyNpcs.getInstance()
                    .getNpcManager()
                    .getAllNpcs()
                    .stream()
                    .map(Npc::getName)
                    .filter(input -> input.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            MessageHelper.error(sender, "Only players can execute this command");
            return false;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
            MessageHelper.info(p, "<b>FancyHolograms commands help:");
            MessageHelper.info(p, "- /hologram help <dark_gray>- <white>Shows all (sub)commands");
            MessageHelper.info(p, "- /hologram list <dark_gray>- <white>Shows you a overview of all holograms");
            MessageHelper.info(p, "- /hologram teleport <name> <dark_gray>- <white>Teleports you to a hologram");
            MessageHelper.info(p, "- /hologram create <name> <dark_gray>- <white>Creates a new hologram");
            MessageHelper.info(p, "- /hologram remove <name> <dark_gray>- <white>Removes a hologram");
            MessageHelper.info(p, "- /hologram copy <hologram> <new name> <dark_gray>- <white>Copies a hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> addLine <text ...> <dark_gray>- <white>Adds a line at the bottom");
            MessageHelper.info(p, "- /hologram edit <hologram> removeLine <dark_gray>- <white>Removes a line at the bottom");
            MessageHelper.info(p, "- /hologram edit <hologram> insertBefore <line number> <text ...> <dark_gray>- <white>Inserts a line before another");
            MessageHelper.info(p, "- /hologram edit <hologram> insertAfter <line number> <text ...> <dark_gray>- <white>Inserts a line after another");
            MessageHelper.info(p, "- /hologram edit <hologram> setLine <line number> <text ...> <dark_gray>- <white>Edits the line");
            MessageHelper.info(p, "- /hologram edit <hologram> position <dark_gray>- <white>Teleports the hologram to you");
            MessageHelper.info(p, "- /hologram edit <hologram> moveTo <x> <y> <z> [yaw] <dark_gray>- <white>Teleports the hologram to the coordinates");
            MessageHelper.info(p, "- /hologram edit <hologram> scale <factor> <dark_gray>- <white>Changes the scale of the hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> billboard <center|fixed|horizontal|vertical> <factor> <dark_gray>- <white>Changes the billboard of the hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> background <color> <dark_gray>- <white>Changes the background of the hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> textShadow <true|false> <dark_gray>- <white>Enables/disables the text shadow");
            MessageHelper.info(p, "- /hologram edit <hologram> shadowRadius <value> <dark_gray>- <white>Changes the shadow radius of the hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> shadowStrength <value> <dark_gray>- <white>Changes the shadow strength of the hologram");
            MessageHelper.info(p, "- /hologram edit <hologram> updateTextInterval <seconds> <dark_gray>- <white>Sets the interval for updating the text");
            if(FancyHolograms.getInstance().isUsingFancyNpcs()){
                MessageHelper.info(p, " - /hologram edit <hologram> linkWithNpc <npc name> <dark_gray>- <white>Links the hologram with an NPC");
                MessageHelper.info(p, " - /hologram edit <hologram> unlinkWithNpc <dark_gray>- <white>Unlinks the hologram with an NPC");
            }
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("list")){
            Collection<Hologram> holograms = FancyHolograms.getInstance().getHologramManager().getAllHolograms();

            if(holograms.isEmpty()){
                MessageHelper.warning(sender, "There are no holograms. Use '/hologram create' to create one");
                return true;
            } else {
                final DecimalFormat df = new DecimalFormat("#########.##");
                MessageHelper.info(p, "<b>List of all holograms:</b>");
                for (Hologram hologram : holograms) {
                    MessageHelper.info(
                            sender,
                            "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'{tp_cmd}'> - {name} ({x}/{y}/{z})</click></hover>"
                                .replace("{name}", hologram.getName())
                                .replace("{x}", df.format(hologram.getLocation().x()))
                                .replace("{y}", df.format(hologram.getLocation().y()))
                                .replace("{z}", df.format(hologram.getLocation().z()))
                                .replace("{tp_cmd}", "/tp " + hologram.getLocation().x() + " " + hologram.getLocation().y() + " " + hologram.getLocation().z())
                            );
                }
                return true;
            }
        }

        if(args.length < 2){
            MessageHelper.error(p, "Wrong usage: /hologram help");
            return false;
        }

        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();

        String action = args[0];
        String holoName = args[1];

        switch (action.toLowerCase()){
            case "create" -> {
                boolean success = create(p, playerList, holoName);
                if(!success){
                    return false;
                }
            }

            case "remove" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    MessageHelper.error(p, "Could not find hologram: '" + holoName + "'");
                    return false;
                }

                boolean success = remove(p, playerList, hologram);
                if(!success){
                    return false;
                }
            }

            case "copy" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    MessageHelper.error(p, "Could not find hologram: '" + holoName + "'");
                    return false;
                }

                if(args.length < 3){
                    MessageHelper.error(p, "Wrong usage: /hologram help");
                    return false;
                }

                String newName = args[2];

                boolean success = copy(p, playerList, hologram, newName);
                if(!success){
                    return false;
                }
            }

            case "teleport" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    MessageHelper.error(p, "Could not find hologram: '" + holoName + "'");
                    return false;
                }

                boolean success = teleport(p, hologram);
                if(!success){
                    return false;
                }
            }

            case "edit" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    MessageHelper.error(p, "Could not find hologram: '" + holoName + "'");
                    return false;
                }

                if(args.length < 3){
                    MessageHelper.error(p, "Wrong usage: /hologram help");
                    return false;
                }

                String editAction = args[2];

                switch (editAction.toLowerCase()){
                    case "setline" -> {
                        if(args.length < 5){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse input to number");
                            return false;
                        }

                        String text = "";

                        for (int i = 4; i < args.length; i++) {
                            text += args[i] + " ";
                        }

                        text = text.substring(0, text.length() - 1);

                        boolean success = editSetLine(p, playerList, hologram, line - 1, text);
                        if(!success){
                            return false;
                        }
                    }

                    case "addline" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        String text = "";

                        for (int i = 3; i < args.length; i++) {
                            text += args[i] + " ";
                        }

                        text = text.substring(0, text.length() - 1);

                        boolean success = editSetLine(p, playerList, hologram, Integer.MAX_VALUE, text);
                        if(!success){
                            return false;
                        }
                    }

                    case "removeline" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse input to number");
                            return false;
                        }

                        boolean success = editSetLine(p, playerList, hologram, line - 1, "");
                        if(!success){
                            return false;
                        }
                    }

                    case "insertbefore" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse input to number");
                            return false;
                        }

                        String text = "";

                        for (int i = 4; i < args.length; i++) {
                            text += args[i] + " ";
                        }

                        text = text.substring(0, text.length() - 1);

                        boolean success = editInsertLine(p, playerList, hologram, line-1, text);
                        if(!success){
                            return false;
                        }
                    }

                    case "insertafter" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse input to number");
                            return false;
                        }

                        String text = "";

                        for (int i = 4; i < args.length; i++) {
                            text += args[i] + " ";
                        }

                        text = text.substring(0, text.length() - 1);

                        boolean success = editInsertLine(p, playerList, hologram, line, text);
                        if(!success){
                            return false;
                        }
                    }

                    case "position" -> {
                        boolean success = editPosition(p, playerList, hologram, p.getLocation());
                        if(!success){
                            return false;
                        }
                    }

                    case "moveto" -> {
                        if(args.length < 6){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        double x, y, z;
                        Float yaw = null;

                        try{
                            x = Double.parseDouble(args[3]);
                            y = Double.parseDouble(args[4]);
                            z = Double.parseDouble(args[5]);
                            if(args.length >= 7){
                                yaw = Float.parseFloat(args[6]);
                            }
                        } catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse position");
                            return false;
                        }

                        Location pos = new Location(p.getWorld(), x, y, z);
                        if(yaw != null){
                            pos.setYaw(yaw);
                        }

                        boolean success = editPosition(p, playerList, hologram, pos);
                        if(!success){
                            return false;
                        }
                    }

                    case "billboard" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        Display.BillboardConstraints billboard = null;
                        for (Display.BillboardConstraints b : Display.BillboardConstraints.values()) {
                            if(b.getSerializedName().equalsIgnoreCase(args[3])){
                                billboard = b;
                            }
                        }

                        if(billboard == null){
                            MessageHelper.error(p, "Could not parse billboard");
                            return false;
                        }

                        if(hologram.getBillboard() == billboard){
                            MessageHelper.warning(p, "This billboard is already set");
                            return false;
                        }

                        boolean success = editBillboard(p, playerList, hologram, billboard);
                        if(!success){
                            return false;
                        }
                    }

                    case "scale" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        float scale;
                        try{
                            scale = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse scale");
                            return false;
                        }

                        boolean success = editScale(p, playerList, hologram, scale);
                        if(!success){
                            return false;
                        }
                    }

                    case "background" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        ChatFormatting background;

                        if(args[3].equalsIgnoreCase("reset")){
                            background = ChatFormatting.RESET;
                        }else if(args[3].equalsIgnoreCase("transparent")){
                            background = ChatFormatting.ITALIC;
                        }else {
                            background = ChatFormatting.getByName(args[3]);

                            if(background == null || !background.isColor()){
                                MessageHelper.error(p, "Could not parse background color");
                                return false;
                            }
                        }

                        boolean success = editBackground(p, playerList, hologram, background);
                        if(!success){
                            return false;
                        }
                    }

                    case "textshadow" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        boolean value;

                        if(args[3].equalsIgnoreCase("true")){
                            value = true;
                        }else if(args[3].equalsIgnoreCase("false")){
                            value = false;
                        }else {
                            MessageHelper.error(p, "Could not parse text shadow flag");
                            return false;
                        }

                        boolean success = editTextShadow(p, playerList, hologram, value);
                        if(!success){
                            return false;
                        }
                    }

                    case "shadowradius" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        float radius;
                        try{
                            radius = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse shadow radius");
                            return false;
                        }

                        boolean success = editShadowRadius(p, playerList, hologram, radius);
                        if(!success){
                            return false;
                        }
                    }

                    case "shadowstrength" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        float strength;
                        try{
                            strength = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse shadow strength");
                            return false;
                        }

                        boolean success = editShadowStrength(p, playerList, hologram, strength);
                        if(!success){
                            return false;
                        }
                    }

                    case "updatetextinterval" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        int interval;
                        try{
                            interval = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            MessageHelper.error(p, "Could not parse interval");
                            return false;
                        }

                        boolean success = editUpdateTextInterval(p, playerList, hologram, interval);
                        if(!success){
                            return false;
                        }
                    }

                    case "linkwithnpc" -> {
                        if(args.length < 4){
                            MessageHelper.error(p, "Wrong usage: /hologram help");
                            return false;
                        }

                        if(!FancyHolograms.getInstance().isUsingFancyNpcs()){
                            MessageHelper.warning(p, "You need to install the FancyNpcs plugin for this functionality to work");
                            MessageHelper.warning(p, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
                            return false;
                        }

                        Npc npc = FancyNpcs.getInstance().getNpcManager().getNpc(args[3]);
                        if(npc == null){
                            MessageHelper.error(p, "Could not find NPC");
                            return false;
                        }

                        boolean success = editLinkWithNpc(p, playerList, hologram, npc);
                        if(!success){
                            return false;
                        }
                    }

                    case "unlinkwithnpc" -> {
                        if(!FancyHolograms.getInstance().isUsingFancyNpcs()){
                            MessageHelper.warning(p, "You need to install the FancyNpcs plugin for this functionality to work");
                            MessageHelper.warning(p, "Download link: <click:open_url:'https://modrinth.com/plugin/fancynpcs/versions'><u>click here</u></click>.");
                            return false;
                        }

                        boolean success = editUnLinkWithNpc(p, hologram);
                        if(!success){
                            return false;
                        }
                    }
                }

            }
        }

        return true;
    }

    private boolean create(Player p, PlayerList playerList, String name){
        if (FancyHolograms.getInstance().getHologramManager().getHologram(name) != null) {
            MessageHelper.error(p, "There already exists a hologram with this name");
            return false;
        }

        List<String> lines = new ArrayList<>();
        lines.add("Edit this line with /hologram edit " + name);

        Hologram hologram = new Hologram(name, p.getLocation(), lines, Display.BillboardConstraints.CENTER, 1f, null, 0, 1, -1, false, null);

        HologramCreateEvent hologramCreateEvent = new HologramCreateEvent(hologram, p);
        hologramCreateEvent.callEvent();

        if(hologramCreateEvent.isCancelled()){
            MessageHelper.error(p, "Creating the hologram was cancelled");
            return false;
        }

        hologram.create();
        for (ServerPlayer player : playerList.players) {
            hologram.spawn(player);
        }

        MessageHelper.success(p,"Created the hologram");
        return true;
    }

    private boolean remove(Player p, PlayerList playerList, Hologram hologram){
        HologramRemoveEvent hologramRemoveEvent = new HologramRemoveEvent(hologram, p);
        hologramRemoveEvent.callEvent();

        if(hologramRemoveEvent.isCancelled()){
            MessageHelper.error(p, "Removing the hologram was cancelled");
            return false;
        }

        for (ServerPlayer player : playerList.players) {
            hologram.remove(player);
        }

        hologram.delete();

        MessageHelper.success(p, "Removed the hologram");
        return true;
    }

    private boolean copy(Player p, PlayerList playerList, Hologram hologram, String newName){
        if (FancyHolograms.getInstance().getHologramManager().getHologram(newName) != null) {
            MessageHelper.error(p, "There already exists a hologram with this name");
            return false;
        }

        Hologram newHologram = new Hologram(
                newName,
                p.getLocation(),
                hologram.getLines(),
                hologram.getBillboard(),
                hologram.getScale(),
                hologram.getBackground(),
                hologram.getShadowRadius(),
                hologram.getShadowStrength(),
                hologram.getUpdateTextInterval(),
                hologram.hasTextShadow(),
                null
        );

        HologramCreateEvent hologramCreateEvent = new HologramCreateEvent(newHologram, p);
        hologramCreateEvent.callEvent();

        if(hologramCreateEvent.isCancelled()){
            MessageHelper.error(p, "Creating the hologram was cancelled");
            return false;
        }

        newHologram.create();
        for (ServerPlayer player : playerList.players) {
            newHologram.spawn(player);
        }

        MessageHelper.success(p, "Copied the hologram");
        return true;
    }

    private boolean teleport(Player p, Hologram hologram){
        p.teleport(hologram.getLocation());
        MessageHelper.success(p, "Teleport you to the hologram");
        return true;
    }

    private boolean editSetLine(Player p, PlayerList playerList, Hologram hologram, int line, String text){
        if(line < 0){
            MessageHelper.error(p, "Invalid line index");
            return false;
        }

        List<String> lines = new ArrayList<>(hologram.getLines());
        if(line >= lines.size()){
            line = lines.size();
            lines.add(text);
        } else {
            if(text.equals("")){
                lines.remove(line);
            } else {
                lines.set(line, text);
            }
        }

        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.TEXT);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setLines(lines);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        MessageHelper.success(p, "Changed text for line " + line);
        return true;
    }

    private boolean editInsertLine(Player p, PlayerList playerList, Hologram hologram, int line, String text){
        if(line < 0){
            MessageHelper.error(p, "Invalid line index");
            return false;
        }

        List<String> lines = new ArrayList<>(hologram.getLines());
        lines.add(Math.min(lines.size(), line), text);

        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.TEXT);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setLines(lines);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        MessageHelper.success(p, "Inserted line");
        return true;
    }

    private boolean editPosition(Player p, PlayerList playerList, Hologram hologram, Location pos){
        if(hologram.getLinkedNpc() != null){
            MessageHelper.error(p, "This hologram is linked with an NPC");
            MessageHelper.error(p, "To unlink: /hologram edit " + hologram.getName() + " unlinkWithNpc");
            return false;
        }

        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.POSITION);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setLocation(pos);

        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        MessageHelper.success(p, "Moved the hologram to " + COORDINATES_DECIMAL_FORMAT.format(pos.x()) + "/" + COORDINATES_DECIMAL_FORMAT.format(pos.y()) + "/" + COORDINATES_DECIMAL_FORMAT.format(pos.z()));
        return true;
    }

    private boolean editBillboard(Player p, PlayerList playerList, Hologram hologram, Display.BillboardConstraints billboard){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.BILLBOARD);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setBillboard(billboard);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBillboard(player);
        }

        MessageHelper.success(p, "Changed the billboard to " + billboard.getSerializedName());
        return true;
    }

    private boolean editScale(Player p, PlayerList playerList, Hologram hologram, float scale){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SCALE);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setScale(scale);

        for (ServerPlayer player : playerList.players) {
            hologram.updateScale(player);
        }

        MessageHelper.success(p, "Changed scale to " + scale);
        return true;
    }

    private boolean editBackground(Player p, PlayerList playerList, Hologram hologram, ChatFormatting background){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.BACKGROUND);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setBackground(background);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBackground(player);
        }

        MessageHelper.success(p, "Changed background color");
        return true;
    }

    private boolean editTextShadow(Player p, PlayerList playerList, Hologram hologram, boolean value){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.TEXT_SHADOW);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setTextShadow(value);

        for (ServerPlayer player : playerList.players) {
            hologram.updateTextShadow(player);
        }

        MessageHelper.success(p, "Changed text shadow");
        return true;
    }

    private boolean editShadowRadius(Player p, PlayerList playerList, Hologram hologram, float radius){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SHADOW_RADIUS);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setShadowRadius(radius);

        for (ServerPlayer player : playerList.players) {
            hologram.updateShadow(player);
        }

        MessageHelper.success(p, "Changed shadow radius");
        return true;
    }

    private boolean editShadowStrength(Player p, PlayerList playerList, Hologram hologram, float strength){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SHADOW_STRENGTH);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setShadowStrength(strength);

        for (ServerPlayer player : playerList.players) {
            hologram.updateShadow(player);
        }

        MessageHelper.success(p, "Changed shadow strength");
        return true;
    }

    private boolean editUpdateTextInterval(Player p, PlayerList playerList, Hologram hologram, int interval){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.UPDATE_TEXT_INTERVAL);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            MessageHelper.error(p, "Cancelled hologram modification");
            return false;
        }

        hologram.setUpdateTextInterval(interval);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        MessageHelper.success(p, "Changed the update text interval");

        return true;
    }

    private boolean editLinkWithNpc(Player p, PlayerList playerList, Hologram hologram, Npc npc){
        if(hologram.getLinkedNpc() != null){
            MessageHelper.error(p, "This hologram is already linked with an NPC");
            return false;
        }
        hologram.setLinkedNpc(npc);

        hologram.syncWithNpc();
        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        MessageHelper.success(p, "Linked hologram with NPC");
        return true;
    }

    private boolean editUnLinkWithNpc(Player p, Hologram hologram){
        if(hologram.getLinkedNpc() == null){
            MessageHelper.error(p, "This hologram is not linked with an NPC");
            return false;
        }

        hologram.getLinkedNpc().updateDisplayName(hologram.getLinkedNpc().getName());
        hologram.setLinkedNpc(null);

        MessageHelper.success(p, "Unlinked hologram with NPC");
        return true;
    }
}
