package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(sender.hasPermission("fancyholograms.hologram.teleport"))) {
            MessageHelper.error(sender, "You don't have the required permission to teleport you to a hologram");
            return true;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }

        final var location = hologram.getData().getLocation();

        if (location == null || location.getWorld() == null) {
            MessageHelper.error(player, "Could not teleport to the hologram");
            return false;
        }

        player.teleportAsync(location).thenAccept(success -> {
            if (success) MessageHelper.success(player, "Teleported you to the hologram");
            else MessageHelper.error(player, "Could not teleport to the hologram");
        });

        return true;
    }
}
