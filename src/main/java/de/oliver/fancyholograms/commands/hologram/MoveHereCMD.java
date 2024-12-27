package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Doubles;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
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

    public static boolean setLocation(Player player, Hologram hologram, Location location, boolean applyRotation) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.move_here"))) {
            MessageHelper.error(player, "You don't have the required permission to move a hologram");
            return false;
        }

        final var copied = hologram.getData().copy(hologram.getName());
        final Location newLocation = (applyRotation)
                ? location
                : new Location(location.getWorld(), location.x(), location.y(), location.z(), copied.getLocation().getYaw(), copied.getLocation().getPitch());
        copied.setLocation(newLocation);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return false;
        }

        hologram.getData().setLocation(copied.getLocation());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Moved the hologram to %s/%s/%s %s\u00B0 %s\u00B0".formatted(
                Constants.COORDINATES_DECIMAL_FORMAT.format(newLocation.x()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(newLocation.y()),
                Constants.COORDINATES_DECIMAL_FORMAT.format(newLocation.z()),
                Constants.COORDINATES_DECIMAL_FORMAT.format((newLocation.getYaw() + 180f) % 360f),
                Constants.COORDINATES_DECIMAL_FORMAT.format((newLocation.getPitch()) % 360f)
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

        return setLocation(player, hologram, location, false);
    }
}
