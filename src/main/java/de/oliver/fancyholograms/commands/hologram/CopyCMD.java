package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.events.HologramCreateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopyCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(sender.hasPermission("fancyholograms.hologram.copy"))) {
            MessageHelper.error(sender, "You don't have the required permission to clone a hologram");
            return false;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }


        if (args.length < 3) {
            MessageHelper.error(sender, "Wrong usage: /hologram help");
            return false;
        }

        String name = args[2];

        if (FancyHologramsPlugin.get().getHologramsManager().getHologram(name).isPresent()) {
            MessageHelper.error(sender, "There already exists a hologram with this name");
            return false;
        }

        if (name.contains(".")) {
            MessageHelper.error(sender, "The name of the hologram cannot contain a dot");
            return false;
        }

        final var data = hologram.getData().copy(name);
        Location originalLocation = data.getLocation();
        Location location = player.getLocation();
        location.setPitch(originalLocation.getPitch());
        location.setYaw(originalLocation.getYaw());
        data.setLocation(location);

        final var copy = FancyHologramsPlugin.get().getHologramsManager().create(data);

        if (!new HologramCreateEvent(copy, player).callEvent()) {
            MessageHelper.error(sender, "Creating the copied hologram was cancelled");
            return false;
        }

        copy.createHologram();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            copy.updateShownStateFor(onlinePlayer);
        }

        FancyHologramsPlugin.get().getHologramsManager().addHologram(copy);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(sender, "Copied the hologram");
        return true;

    }
}
