package de.oliver.commands;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramCMD implements CommandExecutor, TabExecutor {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Arrays.asList("help", "version", "create", "remove", "edit");
        } else if(args.length == 3 && args[0].equalsIgnoreCase("edit")){
            return Arrays.asList("position", "moveTo", "setLine", "addLine", "removeLine", "billboard", "scale", "background");
        }else if(args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) ){
            return FancyHolograms.getInstance().getHologramManager().getAllHolograms().stream().map(Hologram::getName).toList();
        } else if(args.length == 4 && (args[2].equalsIgnoreCase("setLine") || args[2].equalsIgnoreCase("removeLine"))){
            return Arrays.asList("1", "2", "3");
        } else if(args.length == 4 && args[2].equalsIgnoreCase("billboard")){
            return Arrays.stream(Display.BillboardConstraints.values()).map(Display.BillboardConstraints::getSerializedName).toList();
        } else if(args.length == 4 && args[2].equalsIgnoreCase("background")){
            return Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).map(ChatFormatting::getName).toList();
        } else if(args[2].equalsIgnoreCase("moveTo")){
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
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
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

                        ChatFormatting background = ChatFormatting.getByName(args[3]);

                        if(background == null || !background.isColor()){
                            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not parse background color</red>"));
                            return false;
                        }

                        boolean success = editBackground(p, playerList, hologram, background);
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

        Hologram hologram = new Hologram(name, p.getLocation(), lines, Display.BillboardConstraints.CENTER, 1f, null);
        hologram.create();
        for (ServerPlayer player : playerList.players) {
            hologram.spawn(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Created the hologram</color>"));
        return true;
    }

    private boolean remove(Player p, PlayerList playerList, Hologram hologram){
        for (ServerPlayer player : playerList.players) {
            hologram.remove(player);
        }

        hologram.delete();

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Removed the hologram</color>"));
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

        hologram.setLines(lines);

        for (ServerPlayer player : playerList.players) {
            hologram.updateText(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed text for line " + line + "</color>"));
        return true;
    }

    private boolean editPosition(Player p, PlayerList playerList, Hologram hologram, Location pos){
        hologram.setLocation(pos);

        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Moved the hologram to you</color>"));
        return true;
    }

    private boolean editBillboard(Player p, PlayerList playerList, Hologram hologram, Display.BillboardConstraints billboard){
        hologram.setBillboard(billboard);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBillboard(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed the billboard to " + billboard.getSerializedName() + "</color>"));
        return true;
    }

    private boolean editScale(Player p, PlayerList playerList, Hologram hologram, float scale){
        hologram.setScale(scale);

        for (ServerPlayer player : playerList.players) {
            hologram.updateScale(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed scale to " + scale +"</color>"));
        return true;
    }

    private boolean editBackground(Player p, PlayerList playerList, Hologram hologram, ChatFormatting background){
        hologram.setBackground(background);

        for (ServerPlayer player : playerList.players) {
            hologram.updateBackground(player);
        }



        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Changed background color</color>"));
        return true;
    }
}