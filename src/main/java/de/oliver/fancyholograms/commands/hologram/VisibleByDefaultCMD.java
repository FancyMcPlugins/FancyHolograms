package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.property.visibility.Visibility;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VisibleByDefaultCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return Arrays.stream(
                Visibility.values()
        ).map(Objects::toString).toList();
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        var visibleByDefault = Visibility.byString(args[3]);
        if (hologram == null) {
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setVisibleByDefault(visibleByDefault);


        if (hologram.getData().getDisplayData().getVisibleByDefault() == copied.getDisplayData().getVisibleByDefault()) {
            MessageHelper.warning(player, "This hologram already has visibility by default set to " + visibleByDefault);
            return false;
        }

        hologram.getData().getDisplayData().setVisibleByDefault(copied.getDisplayData().getVisibleByDefault());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed visibility by default to " + visibleByDefault);
        return true;
    }
}
