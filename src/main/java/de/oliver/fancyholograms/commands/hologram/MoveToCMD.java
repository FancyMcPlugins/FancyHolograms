package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoveToCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        if (hologram == null) {
            MessageHelper.error(sender, "Hologram not found");
            return false;
        }

        World world;
        int argOffset;
        if (sender instanceof Player player) {
            world = player.getWorld();
            argOffset = 0;
        } else {
            if (args.length < 4) {
                MessageHelper.error(sender, "You must specify the world when using this command from the console");
                return false;
            }
            world = Bukkit.getWorld(args[0]);
            if (world == null) {
                MessageHelper.error(sender, "World not found");
                return false;
            }
            argOffset = 1;
        }

        if (args.length < 3 + argOffset) {
            MessageHelper.error(sender, "Wrong usage: /hologram help");
            return false;
        }

        final var x = MoveHereCMD.calculateCoordinate(args[1 + argOffset], hologram.getData().getLocation(), null, Location::getX);
        final var y = MoveHereCMD.calculateCoordinate(args[2 + argOffset], hologram.getData().getLocation(), null, Location::getY);
        final var z = MoveHereCMD.calculateCoordinate(args[3 + argOffset], hologram.getData().getLocation(), null, Location::getZ);

        if (x == null || y == null || z == null) {
            MessageHelper.error(sender, "Could not parse position");
            return false;
        }

        final var location = new Location(world, x, y, z, hologram.getData().getLocation().getYaw(), hologram.getData().getLocation().getPitch());

        if (args.length > 4 + argOffset) {
            final var yaw = MoveHereCMD.calculateCoordinate(args[4 + argOffset], hologram.getData().getLocation(), null, loc -> loc.getYaw() + 180f);

            if (yaw == null) {
                MessageHelper.error(sender, "Could not parse yaw");
                return false;
            }

            location.setYaw(yaw.floatValue() - 180f);
        }

        if (args.length > 5 + argOffset) {
            final var pitch = MoveHereCMD.calculateCoordinate(args[5 + argOffset], hologram.getData().getLocation(), null, Location::getPitch);

            if (pitch == null) {
                MessageHelper.error(sender, "Could not parse pitch");
                return false;
            }

            location.setPitch(pitch.floatValue());
        }

        return MoveHereCMD.setLocation(sender, hologram, location, true);
    }
}
