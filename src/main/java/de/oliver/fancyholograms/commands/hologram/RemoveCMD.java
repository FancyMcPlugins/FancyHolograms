package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.FancyHolograms;
import de.oliver.fancyholograms.api.events.HologramDeleteEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.remove"))) {
            MessageHelper.error(player, "You don't have the required permission to remove a hologram");
            return false;
        }

        if (!new HologramDeleteEvent(hologram, player).callEvent()) {
            MessageHelper.error(player, "Removing the hologram was cancelled");
            return false;
        }

        FancyHolograms.get().getHologramThread().submit(() -> {
            de.oliver.fancyholograms.main.FancyHolograms.get().getHologramsManager().removeHologram(hologram);
            MessageHelper.success(player, "Removed the hologram");
        });

        return true;
    }
}
