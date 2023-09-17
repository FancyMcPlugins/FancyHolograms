package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BackgroundCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var color = args[3].toLowerCase(Locale.ROOT);

        final TextColor background;

        if (color.equals("reset") || color.equals("default")) {
            background = null;
        } else {
            if (color.equals("transparent")) {
                background = Hologram.TRANSPARENT;
            } else if (color.startsWith("#")) {
                background = TextColor.fromHexString(color);
            } else {
                background = NamedTextColor.NAMES.value(color.replace(' ', '_'));
            }

            if (background == null) {
                MessageHelper.error(player, "Could not parse background color");
                return false;
            }
        }

        if (Objects.equals(background, hologram.getData().getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.setBackground(background);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BACKGROUND)) {
            return false;
        }

        if (Objects.equals(copied.getBackground(), hologram.getData().getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        hologram.getData().setBackground(copied.getBackground());

        MessageHelper.success(player, "Changed background color");
        return true;
    }
}
