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

public class VisibilityCMD implements Subcommand {

    public static final String VISIBILITY_COMMAND = "visibility";


    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return Arrays.stream(
                Visibility.values()
        ).map(Objects::toString).toList();
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var optionalVisibility = Visibility.byString(args[3]);
        if (hologram == null || optionalVisibility.isEmpty()) {
            return false;
        }
        final var visibility = optionalVisibility.get();

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setVisibility(visibility);


        if (hologram.getData().getDisplayData().getVisibility() == copied.getDisplayData().getVisibility()) {
            MessageHelper.warning(player, "This hologram already has visibility by default set to " + visibility);
            return false;
        }

        hologram.getData().getDisplayData().setVisibility(copied.getDisplayData().getVisibility());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed visibility by default to " + visibility);
        return true;
    }
}
