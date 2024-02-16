package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VisibleByDefaultCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        var visibleByDefault = Boolean.parseBoolean(args[3]);
        if (hologram == null) {
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setVisibleByDefault(visibleByDefault);


        if (hologram.getData().getDisplayData().isVisibleByDefault() == copied.getDisplayData().isVisibleByDefault()) {
            MessageHelper.warning(player, "This hologram already has visibility by default set to " + visibleByDefault);
            return false;
        }

        hologram.getData().getDisplayData().setVisibleByDefault(copied.getDisplayData().isVisibleByDefault());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed visibility by default to " + visibleByDefault);
        return true;
    }
}
