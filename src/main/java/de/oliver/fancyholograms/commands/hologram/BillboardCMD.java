package de.oliver.fancyholograms.commands.hologram;

import com.google.common.base.Enums;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class BillboardCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.billboard"))) {
            MessageHelper.error(player, "You don't have the required permission to change the billboard of a hologram");
            return false;
        }

        final var billboard = Enums.getIfPresent(Display.Billboard.class, args[3].toUpperCase(Locale.ROOT)).orNull();

        if (billboard == null) {
            MessageHelper.error(player, "Could not parse billboard");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (billboard == displayData.getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setBillboard(billboard);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (copied.getBillboard() == displayData.getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        displayData.setBillboard(copied.getBillboard());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed the billboard to " + StringUtils.capitalize(billboard.name().toLowerCase(Locale.ROOT)));
        return true;
    }
}
