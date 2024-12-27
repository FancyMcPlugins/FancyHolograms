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

public class ScaleCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.scale"))) {
            MessageHelper.error(player, "You don't have the required permission to change the scale of a hologram");
            return false;
        }

        final var scaleX = Floats.tryParse(args[3]);
        final var scaleY = args.length >= 6 ? Floats.tryParse(args[4]) : scaleX;
        final var scaleZ = args.length >= 6 ? Floats.tryParse(args[5]) : scaleX;

        if (scaleX == null || scaleY == null || scaleZ == null) {
            MessageHelper.error(player, "Could not parse scale");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (Float.compare(scaleX, displayData.getScale().x()) == 0 &&
                Float.compare(scaleY, displayData.getScale().y()) == 0 &&
                Float.compare(scaleZ, displayData.getScale().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setScale(new Vector3f(scaleX, scaleY, scaleZ));

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.SCALE)) {
            return false;
        }

        if (Float.compare(copied.getScale().x(), displayData.getScale().x()) == 0 &&
                Float.compare(copied.getScale().y(), displayData.getScale().y()) == 0 &&
                Float.compare(copied.getScale().z(), displayData.getScale().z()) == 0) {
            MessageHelper.warning(player, "This hologram is already at this scale");
            return false;
        }

        displayData.setScale(new Vector3f(
                copied.getScale().x(),
                copied.getScale().y(),
                copied.getScale().z()));

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Changed scale to " + scaleX + ", " + scaleY + ", " + scaleZ);
        return true;
    }
}
