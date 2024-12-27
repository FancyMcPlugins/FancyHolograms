package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramCreateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHolograms;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(sender.hasPermission("fancyholograms.hologram.create"))) {
            MessageHelper.error(sender, "You don't have the required permission to create a hologram");
            return false;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }
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

        DisplayHologramData displayData = null;
        switch (type) {
            case TEXT -> displayData = new TextHologramData(name, player.getLocation());
            case ITEM -> {
                displayData = new ItemHologramData(name, player.getLocation());
                displayData.setBillboard(Display.Billboard.FIXED);
            }
            case BLOCK -> {
                displayData = new BlockHologramData(name, player.getLocation());
                displayData.setBillboard(Display.Billboard.FIXED);
            }
        }

        final var holo = FancyHolograms.get().getHologramsManager().create(displayData);
        if (!new HologramCreateEvent(holo, player).callEvent()) {
            MessageHelper.error(player, "Creating the hologram was cancelled");
            return false;
        }

        holo.createHologram();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            holo.updateShownStateFor(onlinePlayer);
        }

        FancyHolograms.get().getHologramsManager().addHologram(holo);

        MessageHelper.success(player, "Created the hologram");
        return true;
    }
}
