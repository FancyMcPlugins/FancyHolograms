package de.oliver.commands;

import de.oliver.FancyHolograms;
import de.oliver.Hologram;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
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
            return Arrays.asList("help", "create", "remove");
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
                Hologram hologram = new Hologram(holoName, p.getLocation(), Arrays.asList("Edit this line with /hologram edit " + holoName));
                hologram.create();
                for (ServerPlayer player : playerList.players) {
                    hologram.spawn(player);
                }
            }

            case "remove" -> {
                Hologram hologram = FancyHolograms.getInstance().getHologramManager().getHologram(holoName);
                if(hologram == null){
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<red>Could not find hologram: '" + holoName + "'</red>"));
                    return false;
                }

                for (ServerPlayer player : playerList.players) {
                    hologram.remove(player);
                }

                hologram.delete();
            }
        }

        return false;
    }
}