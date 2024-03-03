package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.events.HologramCreateEvent;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCMD implements Subcommand {

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

        HologramType type = HologramType.getByName(args[1]);
        if (type == null) {
            MessageHelper.error(player, "Could not find type: " + args[1]);
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

        DisplayHologramData displayData = DisplayHologramData.getDefault(player.getLocation().clone());

        Data typeData = null;
        switch (type) {
            case TEXT -> typeData = TextHologramData.getDefault(name);
            case ITEM -> {
                typeData = ItemHologramData.getDefault();
                displayData.setBillboard(Display.Billboard.FIXED);
            }
            case BLOCK -> {
                typeData = BlockHologramData.getDefault();
                displayData.setBillboard(Display.Billboard.FIXED);
            }
        }


        final var data = new HologramData(name, displayData, type, typeData);

        final var holo = FancyHolograms.get().getHologramsManager().create(data);

        if (!new HologramCreateEvent(holo, player).callEvent()) {
            MessageHelper.error(player, "Creating the hologram was cancelled");
            return false;
        }

        holo.createHologram();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            holo.checkAndUpdateShownStateForPlayer(onlinePlayer);
        }

        FancyHolograms.get().getHologramsManager().addHologram(holo);

        MessageHelper.success(player, "Created the hologram");
        return true;
    }
}
