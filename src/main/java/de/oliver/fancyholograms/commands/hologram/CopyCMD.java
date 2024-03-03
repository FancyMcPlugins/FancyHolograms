package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.events.HologramCreateEvent;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopyCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (args.length < 3) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        String name = args[2];

        if (FancyHolograms.get().getHologramsManager().getHologram(name).isPresent()) {
            MessageHelper.error(player, "There already exists a hologram with this name");
            return false;
        }

        if (name.contains(".")) {
            MessageHelper.error(player, "The name of the hologram cannot contain a dot");
            return false;
        }

        final var data = new HologramData(name, hologram.getData());
        data.getDisplayData().setLocation(player.getLocation());

        final var copy = FancyHolograms.get().getHologramsManager().create(data);

        if (!new HologramCreateEvent(copy, player).callEvent()) {
            MessageHelper.error(player, "Creating the copied hologram was cancelled");
            return false;
        }

        copy.createHologram();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            copy.checkAndUpdateShownStateForPlayer(onlinePlayer);
        }

        FancyHolograms.get().getHologramsManager().addHologram(copy);

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Copied the hologram");
        return true;

    }
}
