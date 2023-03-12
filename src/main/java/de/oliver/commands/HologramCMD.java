package de.oliver.commands;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HologramCMD implements CommandExecutor, TabExecutor {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return Arrays.asList("help", "create", "remove", "edit");
        } else if(args.length == 3 && args[0].equalsIgnoreCase("edit")){
            return Arrays.asList("position");
        }else if(args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) ){
            return FancyHolograms.getInstance().getHologramManager().getAllHolograms().stream().map(Hologram::getName).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player p)){
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command</red>"));
            return false;
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
                    case "position" -> {
                        boolean success = editPosition(p, playerList, hologram, p.getLocation());
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
        Hologram hologram = new Hologram(name, p.getLocation(), Arrays.asList("Edit this line with /hologram edit " + name));
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

    private boolean editPosition(Player p, PlayerList playerList, Hologram hologram, Location pos){
        hologram.setLocation(pos.subtract(0, 2, 0));

        for (ServerPlayer player : playerList.players) {
            hologram.updateLocation(player);
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<color:#1a9c3d>Moved the hologram to you</color>"));
        return true;
    }
}