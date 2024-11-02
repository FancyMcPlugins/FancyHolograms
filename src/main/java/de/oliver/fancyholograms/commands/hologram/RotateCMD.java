package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RotateCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(sender.hasPermission("fancyholograms.hologram.edit.rotate"))) {
            MessageHelper.error(sender, "You don't have the required permission to rotate a hologram");
            return true;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }

        final var yaw = MoveHereCMD.calculateCoordinate(args[3], hologram.getData().getLocation(), player.getLocation(), loc -> loc.getYaw() + 180f);
        Location location = hologram.getData().getLocation().clone();
        location.setYaw(yaw.floatValue() - 180f);

        return MoveHereCMD.setLocation(player, hologram, location, true);
    }
}
