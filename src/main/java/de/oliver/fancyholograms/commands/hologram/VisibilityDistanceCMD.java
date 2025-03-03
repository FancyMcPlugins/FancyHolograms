package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VisibilityDistanceCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.visibility_distance"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        var visibilityDistance = Ints.tryParse(args[3]);

        if (visibilityDistance == null) {
            MessageHelper.error(player, "Could not parse visibility distance");
            return false;
        }

        if (visibilityDistance <= 0) {
            visibilityDistance = -1;
        }

        if (Ints.compare(visibilityDistance, hologram.getData().getVisibilityDistance()) == 0) {
            MessageHelper.warning(player, "This hologram already has this visibility distance");
            return false;
        }

        final var copied = hologram.getData().copy(hologram.getData().getName());
        copied.setVisibilityDistance(visibilityDistance);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.UPDATE_VISIBILITY_DISTANCE)) {
            return false;
        }

        if (Ints.compare(copied.getVisibilityDistance(), hologram.getData().getVisibilityDistance()) == 0) {
            MessageHelper.warning(player, "This hologram already has this visibility distance");
            return false;
        }

        hologram.getData().setVisibilityDistance(copied.getVisibilityDistance());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed visibility distance");
        return true;
    }
}
