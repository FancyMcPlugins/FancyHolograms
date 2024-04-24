package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SeeThroughCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
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
            MessageHelper.error(player, "Could not parse see through flag");
            return false;
        }

        if (enabled == textData.isSeeThrough()) {
            MessageHelper.warning(player, "This hologram already has see through " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        final var copied = hologram.getData().copy();
        ((TextHologramData) copied.getTypeData()).setSeeThrough(enabled);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SEE_THROUGH)) {
            return false;
        }

        if (enabled == textData.isSeeThrough()) {
            MessageHelper.warning(player, "This hologram already has see through " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        textData.setSeeThrough(((TextHologramData) copied.getTypeData()).isSeeThrough());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed see through");
        return true;
    }
}
