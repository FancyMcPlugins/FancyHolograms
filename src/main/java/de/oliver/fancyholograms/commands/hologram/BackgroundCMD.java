package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BackgroundCMD implements Subcommand {

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

        final var color = args[3].toLowerCase(Locale.ROOT);

        final Color background;

        if (color.equals("reset") || color.equals("default")) {
            background = null;
        } else {
            if (color.equals("transparent")) {
                background = Hologram.TRANSPARENT;
            } else if (color.startsWith("#")) {
                Color parsed = Color.fromARGB((int)Long.parseLong(color.substring(1), 16));
                //make background solid color if RGB hex provided
                if (color.length() == 7) background = parsed.setAlpha(255);
                else background = parsed;
            } else {
                NamedTextColor named = NamedTextColor.NAMES.value(color.replace(' ', '_'));
                background = named == null ? null : Color.fromARGB(named.value());
            }

            if (background == null) {
                MessageHelper.error(player, "Could not parse background color");
                return false;
            }
        }

        if (Objects.equals(background, textData.getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        final var copied = hologram.getData().copy();
        ((TextHologramData) copied.getTypeData()).setBackground(background);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BACKGROUND)) {
            return false;
        }

        if (Objects.equals(((TextHologramData) copied.getTypeData()).getBackground(), textData.getBackground())) {
            MessageHelper.warning(player, "This hologram already has this background color");
            return false;
        }

        textData.setBackground(((TextHologramData) copied.getTypeData()).getBackground());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed background color");
        return true;
    }
}
