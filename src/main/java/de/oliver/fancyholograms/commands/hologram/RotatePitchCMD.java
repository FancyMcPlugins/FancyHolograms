package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RotatePitchCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var pitch = MoveHereCMD.calculateCoordinate(args[3], hologram.getData().getLocation(), player.getLocation(), loc -> loc.getPitch() - 180f);
        Location location = hologram.getData().getLocation().clone();
        location.setPitch(pitch.floatValue());

        return MoveHereCMD.setLocation(player, hologram, location);
    }
}
