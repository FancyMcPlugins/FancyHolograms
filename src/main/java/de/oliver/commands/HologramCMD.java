package de.oliver.commands;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import de.oliver.Npc;
import de.oliver.events.HologramCreateEvent;
import de.oliver.events.HologramModifyEvent;
import de.oliver.events.HologramRemoveEvent;
import de.oliver.utils.VersionFetcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Display;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class HologramCMD implements CommandExecutor, TabExecutor {

    private final static DecimalFormat COORDINATES_DECIMAL_FORMAT = new DecimalFormat("#########.##");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Stream.of("help", "version", "create", "remove", "edit", "copy").filter(input -> input.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        } else if(args.length == 3 && args[0].equalsIgnoreCase("edit")){
            boolean usingNpcs = FancyHolograms.getInstance().isUsingFancyNpcs();
            return Stream.of("position", "moveTo", "setLine", "addLine", "removeLine", "billboard", "scale", "background", "updateTextInterval", "shadowRadius", "shadowStrength", usingNpcs ? "linkWithNpc" : "", usingNpcs ? "unlinkWithNpc" : "").filter(input -> input.toLowerCase().startsWith(args[2].toLowerCase())).toList();
        }else if(args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) ){

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
            return de.oliver.FancyNpcs.getInstance()
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
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("help")){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green><b>NPC Plugin help:"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram help <dark_gray>- <white>Shows all (sub)commands"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram version <dark_gray>- <white>Shows the plugin version"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram create <name> <dark_gray>- <white>Creates a new hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram remove <name> <dark_gray>- <white>Removes a hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram copy <hologram> <new name> <dark_gray>- <white>Copies a hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> addLine <text ...> <dark_gray>- <white>Adds a line at the bottom"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> removeLine <dark_gray>- <white>Removes a line at the bottom"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> setLine <line number> <text ...> <dark_gray>- <white>Edits the line"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> position <dark_gray>- <white>Teleports the hologram to you"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> moveTo <x> <y> <z> [yaw] <dark_gray>- <white>Teleports the hologram to the coordinates"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> scale <factor> <dark_gray>- <white>Changes the scale of the hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> billboard <center|fixed|horizontal|vertical> <factor> <dark_gray>- <white>Changes the billboard of the hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> background <color> <dark_gray>- <white>Changes the background of the hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> shadowRadius <value> <dark_gray>- <white>Changes the shadow radius of the hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> shadowStrength <value> <dark_gray>- <white>Changes the shadow strength of the hologram"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> updateTextInterval <seconds> <dark_gray>- <white>Sets the interval for updating the text"));
            if(FancyHolograms.getInstance().isUsingFancyNpcs()){
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> linkWithNpc <npc name> <dark_gray>- <white>Links the hologram with an NPC"));
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_green> - <green>/hologram edit <hologram> unlinkWithNpc <dark_gray>- <white>Unlinks the hologram with an NPC"));
            }
            return true;
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("version")){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#54f790><i>Checking version, please wait...</i></color>"));
            new Thread(() -> {
                ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyHolograms.getInstance().getDescription().getVersion());
                if(newestVersion.compareTo(currentVersion) > 0){
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] You are using an outdated version of the FancyHolograms plugin.</color>"));
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + VersionFetcher.DOWNLOAD_URL + "'><u>click here</u></click>.</color>"));
                } else {
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#54f790>You are using the latest version of the FancyHolograms plugin (" + currentVersion + ").</color>"));
                }
            }).start();

            return true;
        }

        if(args.length < 2){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
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
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find hologram: '" + holoName + "'</red>"));
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
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find hologram: '" + holoName + "'</red>"));
                    return false;
                }

                if(args.length < 3){
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                    return false;
                }

                String newName = args[2];

                boolean success = copy(p, playerList, hologram, newName);
                if(!success){
                    return false;
                }
            }

            case "edit" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find hologram: '" + holoName + "'</red>"));
                    return false;
                }

                if(args.length < 3){
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                    return false;
                }

                String editAction = args[2];

                switch (editAction.toLowerCase()){
                    case "setline" -> {
                        if(args.length < 5){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse input to number</red>"));
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
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
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
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        int line;
                        try{
                            line = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse input to number</red>"));
                            return false;
                        }

                        boolean success = editSetLine(p, playerList, hologram, line - 1, "");
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
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
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
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse position</red>"));
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
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        Display.BillboardConstraints billboard = null;
                        for (Display.BillboardConstraints b : Display.BillboardConstraints.values()) {
                            if(b.getSerializedName().equalsIgnoreCase(args[3])){
                                billboard = b;
                            }
                        }

                        if(billboard == null){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse billboard</red>"));
                            return false;
                        }

                        if(hologram.getBillboard() == billboard){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>This billboard is already set</yellow>"));
                            return false;
                        }

                        boolean success = editBillboard(p, playerList, hologram, billboard);
                        if(!success){
                            return false;
                        }
                    }

                    case "scale" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        float scale;
                        try{
                            scale = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse scale</red>"));
                            return false;
                        }

                        boolean success = editScale(p, playerList, hologram, scale);
                        if(!success){
                            return false;
                        }
                    }

                    case "background" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
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
                                p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse background color</red>"));
                                return false;
                            }
                        }

                        boolean success = editBackground(p, playerList, hologram, background);
                        if(!success){
                            return false;
                        }
                    }

                    case "shadowradius" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        float radius;
                        try{
                            radius = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse shadow radius</red>"));
                            return false;
                        }

                        boolean success = editShadowRadius(p, playerList, hologram, radius);
                        if(!success){
                            return false;
                        }
                    }

                    case "shadowstrength" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        float strength;
                        try{
                            strength = Float.parseFloat(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse shadow strength</red>"));
                            return false;
                        }

                        boolean success = editShadowStrength(p, playerList, hologram, strength);
                        if(!success){
                            return false;
                        }
                    }

                    case "updatetextinterval" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        int interval;
                        try{
                            interval = Integer.parseInt(args[3]);
                        }catch (NumberFormatException e){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse interval</red>"));
                            return false;
                        }

                        boolean success = editUpdateTextInterval(p, playerList, hologram, interval);
                        if(!success){
                            return false;
                        }
                    }

                    case "linkwithnpc" -> {
                        if(args.length < 4){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Wrong usage: /hologram help</red>"));
                            return false;
                        }

                        if(!FancyHolograms.getInstance().isUsingFancyNpcs()){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>You need to install the FancyNpcs plugin for this functionality to work</color>"));
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>Download link: <click:open_url:'https://modrinth.com/plugin/npc-plugin/versions'><u>click here</u></click>.</color>"));
                            return false;
                        }

                        Npc npc = de.oliver.FancyNpcs.getInstance().getNpcManager().getNpc(args[3]);
                        if(npc == null){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find NPC</red>"));
                            return false;
                        }

                        boolean success = editLinkWithNpc(p, playerList, hologram, npc);
                        if(!success){
                            return false;
                        }
                    }

                    case "unlinkwithnpc" -> {
                         if(!FancyHolograms.getInstance().isUsingFancyNpcs()){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>You need to install the FancyNpcs plugin for this functionality to work</color>"));
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#ffca1c>Download link: <click:open_url:'https://modrinth.com/plugin/npc-plugin/versions'><u>click here</u></click>.</color>"));
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
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>There already exists a hologram with this name</red>"));
            return false;
        }

        List<String> lines = new ArrayList<>();
        lines.add("Edit this line with /hologram edit " + name);

        Hologram hologram = new Hologram(name, p.getLocation(), lines, Display.BillboardConstraints.CENTER, 1f, null, 0, 1, -1, null);

        HologramCreateEvent hologramCreateEvent = new HologramCreateEvent(hologram, p);
        hologramCreateEvent.callEvent();

        if(hologramCreateEvent.isCancelled()){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Creating the hologram was cancelled</red>"));
            return false;
        }

        hologram.create();
        for (ServerPlayer player : playerList.players) {
            hologram.spawn(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Created the hologram</color>"));
        return true;
    }

    private boolean remove(Player p, PlayerList playerList, Hologram hologram){
        HologramRemoveEvent hologramRemoveEvent = new HologramRemoveEvent(hologram, p);
        hologramRemoveEvent.callEvent();

        if(hologramRemoveEvent.isCancelled()){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Removing the hologram was cancelled</red>"));
            return false;
        }

        for (ServerPlayer player : playerList.players) {
            hologram.remove(player);
        }

        hologram.delete();

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Removed the hologram</color>"));
        return true;
    }

    private boolean copy(Player p, PlayerList playerList, Hologram hologram, String newName){
        if (FancyHolograms.getInstance().getHologramManager().getHologram(newName) != null) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>There already exists a hologram with this name</red>"));
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
                hologram.getLinkedNpc()
        );

        HologramCreateEvent hologramCreateEvent = new HologramCreateEvent(newHologram, p);
        hologramCreateEvent.callEvent();

        if(hologramCreateEvent.isCancelled()){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Creating the hologram was cancelled</red>"));
            return false;
        }

        newHologram.create();
        for (ServerPlayer player : playerList.players) {
            newHologram.spawn(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Copied the hologram</color>"));
        return true;
    }

    private boolean editSetLine(Player p, PlayerList playerList, Hologram hologram, int line, String text){
        if(line < 0){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid line index</red>"));
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
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setLines(lines);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed text for line " + line + "</color>"));
        return true;
    }

    private boolean editPosition(Player p, PlayerList playerList, Hologram hologram, Location pos){
        if(hologram.getLinkedNpc() != null){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>This hologram is linked with an NPC</red>"));
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>To unlink: /hologram edit " + hologram.getName() + " unlinkWithNpc</red>"));
            return false;
        }

        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.POSITION);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setLocation(pos);

        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Moved the hologram to " + COORDINATES_DECIMAL_FORMAT.format(pos.x()) + "/" + COORDINATES_DECIMAL_FORMAT.format(pos.y()) + "/" + COORDINATES_DECIMAL_FORMAT.format(pos.z()) + "</color>"));
        return true;
    }

    private boolean editBillboard(Player p, PlayerList playerList, Hologram hologram, Display.BillboardConstraints billboard){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.BILLBOARD);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setBillboard(billboard);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBillboard(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed the billboard to " + billboard.getSerializedName() + "</color>"));
        return true;
    }

    private boolean editScale(Player p, PlayerList playerList, Hologram hologram, float scale){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SCALE);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setScale(scale);

        for (ServerPlayer player : playerList.players) {
            hologram.updateScale(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed scale to " + scale +"</color>"));
        return true;
    }

    private boolean editBackground(Player p, PlayerList playerList, Hologram hologram, ChatFormatting background){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.BACKGROUND);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setBackground(background);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBackground(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed background color</color>"));
        return true;
    }

    private boolean editShadowRadius(Player p, PlayerList playerList, Hologram hologram, float radius){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SHADOW_RADIUS);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setShadowRadius(radius);

        for (ServerPlayer player : playerList.players) {
            hologram.updateShadow(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed shadow radius</color>"));
        return true;
    }

    private boolean editShadowStrength(Player p, PlayerList playerList, Hologram hologram, float strength){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.SHADOW_STRENGTH);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setShadowStrength(strength);

        for (ServerPlayer player : playerList.players) {
            hologram.updateShadow(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed shadow strength</color>"));
        return true;
    }

    private boolean editUpdateTextInterval(Player p, PlayerList playerList, Hologram hologram, int interval){
        HologramModifyEvent hologramModifyEvent = new HologramModifyEvent(hologram, p, HologramModifyEvent.HologramModification.UPDATE_TEXT_INTERVAL);
        hologramModifyEvent.callEvent();
        if (hologramModifyEvent.isCancelled()) {
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cancelled hologram modification</red>"));
            return false;
        }

        hologram.setUpdateTextInterval(interval);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed the update text interval</color>"));

        return true;
    }

    private boolean editLinkWithNpc(Player p, PlayerList playerList, Hologram hologram, Npc npc){
        if(hologram.getLinkedNpc() != null){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>This hologram is already linked with an NPC</red>"));
            return false;
        }
        hologram.setLinkedNpc(npc);

        hologram.syncWithNpc();
        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Linked hologram with NPC</color>"));
        return true;
    }

    private boolean editUnLinkWithNpc(Player p, Hologram hologram){
        if(hologram.getLinkedNpc() == null){
            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>This hologram is not linked with an NPC</red>"));
            return false;
        }

        hologram.getLinkedNpc().updateDisplayName(hologram.getLinkedNpc().getName());
        hologram.setLinkedNpc(null);

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Unlinked hologram with NPC</color>"));
        return true;
    }
}
