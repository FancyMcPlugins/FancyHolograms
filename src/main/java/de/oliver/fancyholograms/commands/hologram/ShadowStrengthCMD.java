package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Floats;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
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

public class ShadowStrengthCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.shadow_strength"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        final var strength = Floats.tryParse(args[3]);

        if (strength == null) {
            MessageHelper.error(player, "Could not parse shadow strength");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (Float.compare(strength, displayData.getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setShadowStrength(strength);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SHADOW_STRENGTH)) {
            return false;
        }

        if (Float.compare(copied.getShadowStrength(), displayData.getShadowStrength()) == 0) {
            MessageHelper.warning(player, "This hologram already has this shadow strength");
            return false;
        }

        displayData.setShadowStrength(copied.getShadowStrength());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getHologramStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed shadow strength");
        return true;
    }
}
