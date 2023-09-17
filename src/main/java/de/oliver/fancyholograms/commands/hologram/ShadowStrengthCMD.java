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

public class ShadowStrengthCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var strength = Floats.tryParse(args[3]);

        if (strength == null) {
            MessageHelper.error(player, "Could not parse shadow strength");
            return false;
        }

        if (Float.compare(strength, hologram.getData().getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setShadowStrength(strength);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SHADOW_STRENGTH)) {
            return false;
        }

        if (Float.compare(copied.getShadowStrength(), hologram.getData().getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        hologram.getData().setShadowStrength(copied.getShadowStrength());

        MessageHelper.success(player, "Changed shadow strength");
        return true;
    }
}
