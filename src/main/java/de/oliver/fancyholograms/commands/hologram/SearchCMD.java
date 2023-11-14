package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancyholograms.util.Constants;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SearchCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var holograms = FancyHolograms.get().getHologramsManager().getHolograms();

        if (holograms.isEmpty()) {
            MessageHelper.warning(player, "There are no holograms. Use '/hologram create' to create one");
        } else {
            int page;
            String search = args[1];
            if (args.length < 3) {
                page = 1;
            } else {
                final var index = Ints.tryParse(args[2]);
                if (index == null) {
                    MessageHelper.error(player, "Could not parse page number");
                    return false;
                }
                page = index;
            }

            var pages = holograms.stream()
                    .filter(holo -> holo.getData().getName().contains(search))
                    .count() / 10 + 1;

            if (page > pages) {
                MessageHelper.error(player, "Page %s does not exist".formatted(page));
                return true;
            }
            MessageHelper.info(player, "<b>Search results:</b>");
            MessageHelper.info(player, "<b>Page %s/%s</b>".formatted(page, pages));
            holograms.stream()
                    .skip((page - 1) * 10)
                    .limit(10)
                    .filter(holo -> holo.getData().getName().contains(search))
                    .forEach(holo -> {
                        final var location = holo.getData().getDisplayData().getLocation();
                        if (location == null || location.getWorld() == null) {
                            return;
                        }

                        MessageHelper.info(player,
                                "<hover:show_text:'<gray><i>Click to teleport</i></gray>'><click:run_command:'%s'> - %s (%s/%s/%s)</click></hover>"
                                        .formatted("/hologram teleport " + holo.getData().getName(),
                                                holo.getData().getName(),
                                                Constants.DECIMAL_FORMAT.format(location.x()),
                                                Constants.DECIMAL_FORMAT.format(location.y()),
                                                Constants.DECIMAL_FORMAT.format(location.z())));
                    });

        }

        return true;
    }
}
