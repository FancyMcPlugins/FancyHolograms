package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class TextShadowCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(hologram.getData().getTypeData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }


        final var enabled = switch (args[3].toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };

        if (enabled == null) {
            MessageHelper.error(player, "Could not parse text shadow flag");
            return false;
        }

        if (enabled == textData.isTextShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        final var copied = hologram.getData().copy();
        ((TextHologramData) copied.getTypeData()).setTextShadow(enabled);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_SHADOW)) {
            return false;
        }

        if (enabled == textData.isTextShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        textData.setTextShadow(((TextHologramData) copied.getTypeData()).isTextShadow());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed text shadow");
        return true;
    }
}
