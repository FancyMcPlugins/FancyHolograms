package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Floats;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShadowRadiusCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var radius = Floats.tryParse(args[3]);

        if (radius == null) {
            MessageHelper.error(player, "Could not parse shadow radius");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (Float.compare(radius, displayData.getShadowRadius()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow radius");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setShadowRadius(radius);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SHADOW_RADIUS)) {
            return false;
        }

        if (Float.compare(copied.getShadowRadius(), displayData.getShadowRadius()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow radius");
            return false;
        }

        displayData.setShadowRadius(copied.getShadowRadius());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed shadow radius");
        return true;
    }
}
