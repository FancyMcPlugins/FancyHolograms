package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
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
        final var enabled = switch (args[3].toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };

        if (enabled == null) {
            MessageHelper.error(player, "Could not parse text shadow flag");
            return false;
        }

        if (enabled == hologram.getData().isTextHasShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setTextHasShadow(enabled);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_SHADOW)) {
            return false;
        }

        if (enabled == hologram.getData().isTextHasShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        hologram.getData().setTextHasShadow(copied.isTextHasShadow());

        MessageHelper.success(player, "Changed text shadow");
        return true;
    }
}
