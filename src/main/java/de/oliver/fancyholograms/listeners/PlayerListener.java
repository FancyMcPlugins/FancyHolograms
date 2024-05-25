package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerListener implements Listener {

    @NotNull
    private final FancyHolograms plugin;

    public PlayerListener(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.checkAndUpdateShownStateForPlayer(event.getPlayer());
        }

        if (!this.plugin.getHologramConfiguration().areVersionNotificationsMuted() && event.getPlayer().hasPermission("fancyholograms.admin")) {
            FancyHolograms.get().getScheduler().runTaskAsynchronously(() -> {
                FancyHolograms.get().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        FancyHolograms.get().getScheduler().runTaskAsynchronously(() -> {
            for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.hideHologram(event.getPlayer());
                hologram.removeManualViewer(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.checkAndUpdateShownStateForPlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.checkAndUpdateShownStateForPlayer(event.getPlayer());
        }
    }

}
