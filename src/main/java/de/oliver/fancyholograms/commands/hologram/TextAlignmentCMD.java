package de.oliver.fancyholograms.commands.hologram;

import com.google.common.base.Enums;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class TextAlignmentCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var alignment = Enums.getIfPresent(TextDisplay.TextAlignment.class, args[3].toUpperCase(Locale.ROOT)).orNull();

        if (alignment == null) {
            MessageHelper.error(player, "Could not parse text alignment");
            return false;
        }

        if (hologram.getData().getTextAlignment() == alignment) {
            MessageHelper.warning(player, "This hologram already has this text alignment");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setTextAlignment(alignment);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_ALIGNMENT)) {
            return false;
        }

        if (hologram.getData().getTextAlignment() == alignment) {
            MessageHelper.warning(player, "This hologram already has this text alignment");
            return false;
        }

        hologram.getData().setTextAlignment(copied.getTextAlignment());

        MessageHelper.success(player, "Changed text alignment");
        return true;
    }
}
