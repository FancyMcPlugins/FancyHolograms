package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoveToCMD implements Subcommand {

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

        final var x = MoveHereCMD.calculateCoordinate(args[3], hologram.getData().getDisplayData().getLocation(), player.getLocation(), Location::x);
        final var y = MoveHereCMD.calculateCoordinate(args[4], hologram.getData().getDisplayData().getLocation(), player.getLocation(), Location::y);
        final var z = MoveHereCMD.calculateCoordinate(args[5], hologram.getData().getDisplayData().getLocation(), player.getLocation(), Location::z);

        if (x == null || y == null || z == null) {
            MessageHelper.error(player, "Could not parse position");
            return false;
        }

        final var location = new Location(player.getWorld(), x, y, z);

        if (args.length > 6) {
            final var yaw = MoveHereCMD.calculateCoordinate(args[6], hologram.getData().getDisplayData().getLocation(), player.getLocation(), Location::getYaw);

            if (yaw == null) {
                MessageHelper.error(player, "Could not parse yaw");
                return false;
            }

            location.setYaw(yaw.floatValue());
        }

        if (args.length > 7) {
            final var pitch = MoveHereCMD.calculateCoordinate(args[7], hologram.getData().getDisplayData().getLocation(), player.getLocation(), Location::getPitch);

            if (pitch == null) {
                MessageHelper.error(player, "Could not parse pitch");
                return false;
            }

            location.setPitch(pitch.floatValue());
        }

        return MoveHereCMD.setLocation(player, hologram, location);
    }
}
