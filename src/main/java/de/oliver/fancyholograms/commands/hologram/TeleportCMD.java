package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull Player player, @Nullable Hologram hologram, @NotNull String[] args) {
        final var location = hologram.getData().getDisplayData().getLocation();

        if (location == null || location.getWorld() == null) {
            MessageHelper.error(player, "Could not teleport to the hologram");
            return false;
        }

        player.teleportAsync(location).thenAccept(success -> {
            if (success) MessageHelper.success(player, "Teleported you to the hologram");
            else MessageHelper.error(player, "Could not teleport to the hologram");
        });

        return true;
    }
}
