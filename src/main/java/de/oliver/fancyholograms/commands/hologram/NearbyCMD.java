package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancyholograms.util.NumberHelper;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NearbyCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.nearby"))) {
            MessageHelper.error(player, "You don't have the required permission to see nearby holograms");
            return false;
        }

        if (!(player instanceof Player)) {
            MessageHelper.error(player, "This is a player only command.");
            return false;
        }


        if (args.length < 2) {
            MessageHelper.error(player, Constants.INVALID_NEARBY_RANGE);
            return false;
        }

        Optional<Integer> range = NumberHelper.parseInt(args[1]);

        if (range.isEmpty()) {
            MessageHelper.error(player, Constants.INVALID_NEARBY_RANGE);
            return false;
        }

        Location playerLocation = ((Player) player).getLocation().clone();

        List<Map.Entry<Hologram, Double>> nearby = FancyHologramsPlugin.get()
            .getHologramsManager()
            .getPersistentHolograms()
            .stream()
            .filter((holo) -> holo.getData().getLocation().getWorld() == playerLocation.getWorld())
            .map((holo) -> Map.entry(holo, holo.getData().getLocation().distance(playerLocation)))
            .filter((entry) -> entry.getValue() <= range.get())
            .sorted(Comparator.comparingInt(a -> a.getValue().intValue()))
            .toList();

        if (nearby.isEmpty()) {
            MessageHelper.error(player, "There are no nearby holograms in a radius of %s blocks.".formatted(range.get()));
            return true;
        }

        MessageHelper.info(player, "<b>Holograms nearby (%s radius)".formatted(range.get()));
        nearby.forEach((entry) -> {
            Hologram holo = entry.getKey();
            double distance = entry.getValue();

            final var location = holo.getData().getLocation();
            if (location == null || location.getWorld() == null) {
                return;
            }

            MessageHelper.info(player,
                "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'%s'> - %s (%s/%s/%s in %s, %s blocks away)</click></hover>"
                    .formatted(
                        "/hologram teleport " + holo.getData().getName(),
                        holo.getData().getName(),
                        Constants.DECIMAL_FORMAT.format(location.x()),
                        Constants.DECIMAL_FORMAT.format(location.y()),
                        Constants.DECIMAL_FORMAT.format(location.z()),
                        location.getWorld().getName(),
                        Constants.DECIMAL_FORMAT.format(distance)
                    ));
        });
        return true;
    }
}
