package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VisibilityDistanceCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        var visibilityDistance = Ints.tryParse(args[3]);

        if (visibilityDistance == null) {
            MessageHelper.error(player, "Could not parse visibility distance");
            return false;
        }

        if (visibilityDistance <= 0) {
            visibilityDistance = -1;
        }

        if (Ints.compare(visibilityDistance, hologram.getData().getDisplayData().getVisibilityDistance()) == 0) {
            MessageHelper.warning(player, "This hologram already has this visibility distance");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setVisibilityDistance(visibilityDistance);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.UPDATE_VISIBILITY_DISTANCE)) {
            return false;
        }

        if (Ints.compare(copied.getDisplayData().getVisibilityDistance(), hologram.getData().getDisplayData().getVisibilityDistance()) == 0) {
            MessageHelper.warning(player, "This hologram already has this visibility distance");
            return false;
        }

        hologram.getData().getDisplayData().setVisibilityDistance(copied.getDisplayData().getVisibilityDistance());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed visibility distance");
        return true;
    }
}
