package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CenterCMD implements Subcommand {
    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        Location cloned = hologram.getData().getLocation().clone();

        cloned.set(
            Math.floor(cloned.x()) + 0.5,
            cloned.y(),
            Math.floor(cloned.z()) + 0.5
        );

        hologram.getData().setLocation(cloned);

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Centered the hologram to %s/%s/%s %s\u00B0 %s\u00B0".formatted(
            Constants.COORDINATES_DECIMAL_FORMAT.format(cloned.x()),
            Constants.COORDINATES_DECIMAL_FORMAT.format(cloned.y()),
            Constants.COORDINATES_DECIMAL_FORMAT.format(cloned.z()),
            Constants.COORDINATES_DECIMAL_FORMAT.format((cloned.getYaw() + 180f) % 360f),
            Constants.COORDINATES_DECIMAL_FORMAT.format((cloned.getPitch()) % 360f)
        ));
        return true;
    }
}
