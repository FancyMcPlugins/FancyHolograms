package de.oliver.fancyholograms.commands.hologram;

import com.google.common.base.Enums;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class BillboardCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var billboard = Enums.getIfPresent(Display.Billboard.class, args[3].toUpperCase(Locale.ROOT)).orNull();

        if (billboard == null) {
            MessageHelper.error(player, "Could not parse billboard");
            return false;
        }

        if (billboard == hologram.getData().getDisplayData().getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        final var copied = hologram.getData().copy();
        copied.getDisplayData().setBillboard(billboard);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return false;
        }

        if (copied.getDisplayData().getBillboard() == hologram.getData().getDisplayData().getBillboard()) {
            MessageHelper.warning(player, "This billboard is already set");
            return false;
        }

        hologram.getData().getDisplayData().setBillboard(copied.getDisplayData().getBillboard());

        MessageHelper.success(player, "Changed the billboard to " + StringUtils.capitalize(billboard.name().toLowerCase(Locale.ROOT)));
        return true;
    }
}
