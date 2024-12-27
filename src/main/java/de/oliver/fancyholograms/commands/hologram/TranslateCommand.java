package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Floats;
import de.oliver.fancyholograms.main.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class TranslateCommand implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.translate"))) {
            MessageHelper.error(player, "You don't have the required permission to change the translation of a hologram");
            return false;
        }

        final var translateX = Floats.tryParse(args[3]);
        final var translateY = args.length >= 6 ? Floats.tryParse(args[4]) : translateX;
        final var translateZ = args.length >= 6 ? Floats.tryParse(args[5]) : translateX;

        if (translateX == null || translateY == null || translateZ == null) {
            MessageHelper.error(player, "Could not parse translation");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (Float.compare(translateX, displayData.getTranslation().x()) == 0 &&
            Float.compare(translateY, displayData.getTranslation().y()) == 0 &&
            Float.compare(translateZ, displayData.getTranslation().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this translation");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setTranslation(new Vector3f(translateX, translateY, translateZ));

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TRANSLATION)) {
            return false;
        }

        if (Float.compare(copied.getTranslation().x(), displayData.getTranslation().x()) == 0 &&
            Float.compare(copied.getTranslation().y(), displayData.getTranslation().y()) == 0 &&
            Float.compare(copied.getTranslation().z(), displayData.getTranslation().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this translation");
            return false;
        }

        displayData.setTranslation(new Vector3f(
            copied.getTranslation().x(),
            copied.getTranslation().y(),
            copied.getTranslation().z()));

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed translation to " + translateX + ", " + translateY + ", " + translateZ);
        return true;
    }
}
