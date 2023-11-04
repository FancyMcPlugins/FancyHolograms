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
import org.joml.Vector3f;

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

        if (Float.compare(scale, hologram.getData().getDisplayData().getScale().x()) == 0 &&
                Float.compare(scale, hologram.getData().getDisplayData().getScale().y()) == 0 &&
                Float.compare(scale, hologram.getData().getDisplayData().getScale().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setScale(new Vector3f(scale, scale, scale));

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SCALE)) {
            return false;
        }

        if (Float.compare(copied.getDisplayData().getScale().x(), hologram.getData().getDisplayData().getScale().x()) == 0 &&
                Float.compare(copied.getDisplayData().getScale().y(), hologram.getData().getDisplayData().getScale().y()) == 0 &&
                Float.compare(copied.getDisplayData().getScale().z(), hologram.getData().getDisplayData().getScale().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        hologram.getData().getDisplayData().setScale(new Vector3f(
                copied.getDisplayData().getScale().x(),
                copied.getDisplayData().getScale().y(),
                copied.getDisplayData().getScale().z()));

        MessageHelper.success(player, "Changed scale to " + scale);
        return true;
    }
}
