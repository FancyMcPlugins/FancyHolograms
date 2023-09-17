package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
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

public class UpdateTextIntervalCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var text = args[3].toLowerCase(Locale.ROOT);

        Integer interval;

        if (text.equals("never") || text.equals("off") || text.equals("none")) {
            interval = -1;
        } else {

            var multiplier = 1;

            if (!text.isEmpty()) {
                switch (text.charAt(text.length() - 1)) {
                    case 's' -> multiplier = 1000;
                    case 'm' -> multiplier = 1000 * 60;
                }
            }

            final var time = Ints.tryParse(multiplier == 1 ? text : text.substring(0, text.length() - 1));

            if (time == null) {
                interval = null;
            } else {
                interval = time * multiplier;
            }
        }

        if (interval == null) {
            MessageHelper.error(player, "Could not parse text update interval");
            return false;
        }

        if (interval == hologram.getData().getTextUpdateInterval()) {
            MessageHelper.warning(player, "This hologram already has this text update interval");
            return false;
        }

        interval = Math.max(-1, interval);

        final var copied = hologram.getData().copy();
        copied.setTextUpdateInterval(interval);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.UPDATE_TEXT_INTERVAL)) {
            return false;
        }

        if (copied.getTextUpdateInterval() == hologram.getData().getTextUpdateInterval()) {
            MessageHelper.warning(player, "This hologram already has this text update interval");
            return false;
        }

        hologram.getData().setTextUpdateInterval(copied.getTextUpdateInterval());

        MessageHelper.success(player, "Changed the text update interval");
        return true;
    }
}
