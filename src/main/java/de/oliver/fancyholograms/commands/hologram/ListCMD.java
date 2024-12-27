package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.main.FancyHolograms;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.list"))) {
            MessageHelper.error(player, "You don't have the required permission to list the holograms");
            return false;
        }

        final var holograms = FancyHolograms.get().getHologramsManager().getPersistentHolograms();

        if (holograms.isEmpty()) {
            MessageHelper.warning(player, "There are no holograms. Use '/hologram create' to create one");
        } else {
            int page;
            if (args.length < 2) {
                page = 1;
            } else {
                final var index = Ints.tryParse(args[1]);
                if (index == null) {
                    MessageHelper.error(player, "Could not parse page number");
                    return false;
                }
                page = index;
            }

            var pages = holograms.size() / 10 + 1;
            if (page > pages) {
                MessageHelper.error(player, "Page %s does not exist".formatted(page));
                return true;
            }
            MessageHelper.info(player, "<b>List of holograms:</b>");
            MessageHelper.info(player, "<b>Page %s/%s</b>".formatted(page, pages));
            holograms.stream()
                    .skip((page - 1) * 10)
                    .limit(10)
                    .forEach(holo -> {
                        final var location = holo.getData().getLocation();
                        if (location == null || location.getWorld() == null) {
                            return;
                        }

                        MessageHelper.info(player,
                                "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'%s'> - %s (%s/%s/%s in %s)</click></hover>"
                                        .formatted("/hologram teleport " + holo.getData().getName(),
                                                holo.getData().getName(),
                                                Constants.DECIMAL_FORMAT.format(location.x()),
                                                Constants.DECIMAL_FORMAT.format(location.y()),
                                                Constants.DECIMAL_FORMAT.format(location.z()),
                                                location.getWorld().getName()
                                        ));
                    });

        }

        return true;
    }
}
