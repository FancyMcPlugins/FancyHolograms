package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
            final var distance = hologram.distanceTo(event.getPlayer().getLocation());
            if (Double.isNaN(distance) || distance > hologram.getData().getDisplayData().getVisibilityDistance()) {
                continue;
            }

            hologram.showHologram(event.getPlayer());
        }

        if (!this.plugin.getConfiguration().areVersionNotificationsMuted() && event.getPlayer().hasPermission("fancyholograms.admin")) {
            FancyHolograms.get().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.hideHologram(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(@NotNull final PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return; // reduce checks we need to do
        }

        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            final var distance = hologram.distanceTo(event.getTo());
            if (Double.isNaN(distance)) {
                continue;
            }

            final var inRange = distance <= hologram.getData().getDisplayData().getVisibilityDistance();
            final var isShown = hologram.isShown(event.getPlayer());

            if (inRange && !isShown) {
                hologram.showHologram(event.getPlayer());
            } else if (!inRange && isShown) {
                hologram.hideHologram(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            final var distance = hologram.distanceTo(event.getPlayer().getLocation());
            if (Double.isNaN(distance) || distance > hologram.getData().getDisplayData().getVisibilityDistance()) {
                continue;
            }

            hologram.showHologram(event.getPlayer());
        }
    }

}
