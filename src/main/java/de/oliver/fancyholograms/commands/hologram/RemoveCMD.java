package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.events.HologramDeleteEvent;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
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
        if (!new HologramDeleteEvent(hologram, player).callEvent()) {
            MessageHelper.error(player, "Removing the hologram was cancelled");
            return false;
        }

        FancyHologramsPlugin.get().getScheduler().runTaskAsynchronously(() -> {
            hologram.hideHologram(Bukkit.getOnlinePlayers());
            hologram.deleteHologram();

            FancyHolograms.get().getHologramsManager().removeHologram(hologram);

            MessageHelper.success(player, "Removed the hologram");
        });

        return true;
    }
}
