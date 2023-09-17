package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Floats;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScaleCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var scale = Floats.tryParse(args[3]);

        if (scale == null) {
            MessageHelper.error(player, "Could not parse scale");
            return false;
        }

        if (Float.compare(scale, hologram.getData().getScale()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setScale(scale);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SCALE)) {
            return false;
        }

        if (Float.compare(copied.getScale(), hologram.getData().getScale()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        hologram.getData().setScale(copied.getScale());

        MessageHelper.success(player, "Changed scale to " + scale);
        return true;
    }
}
