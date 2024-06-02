package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Doubles;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class MoveHereCMD implements Subcommand {

    public static boolean setLocation(Player player, Hologram hologram, Location location) {
        final var copied = hologram.getData().copy(hologram.getName());
        copied.setLocation(location);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return false;
        }

        final var updatedLocation = copied.getLocation() == null ? location : copied.getLocation(); // note: maybe should fall back to original location?
        hologram.getData().setLocation(updatedLocation);

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Moved the hologram to %s/%s/%s %s\u00B0 %s\u00B0".formatted(
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.x()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.y()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(updatedLocation.z()),
                Constants.COORDINATES_DECIMAL_FORMAT.format((updatedLocation.getYaw() + 180f) % 360f),
                Constants.COORDINATES_DECIMAL_FORMAT.format((updatedLocation.getPitch()) % 360f)
        ));

        return true;
    }

    public static @Nullable Double calculateCoordinate(@NotNull final String text, @Nullable final Location originLocation, @NotNull final Location callerLocation, @NotNull final Function<Location, Number> extractor) {
        final var number = Doubles.tryParse(StringUtils.stripStart(text, "~"));
        final var target = text.startsWith("~~") ? callerLocation : text.startsWith("~") ? originLocation : null;

        if (number == null) {
            return target == null ? null : extractor.apply(target).doubleValue();
        }

        if (target == null) {
            return number;
        }

        return number + extractor.apply(target).doubleValue();
    }

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageHelper.error(sender, "You must be a sender to use this command");
            return false;
        }

        if (hologram.getData().getLinkedNpcName() != null) {
            MessageHelper.error(player, "This hologram is linked with an NPC");
            MessageHelper.error(player, "To unlink: /hologram edit " + hologram.getData().getName() + " unlinkWithNpc");
            return false;
        }

        final var location = player.getLocation();

        return setLocation(player, hologram, location);
    }
}
