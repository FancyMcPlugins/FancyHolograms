package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public final class PlayerListener implements Listener {

    private final @NotNull FancyHolograms plugin;

    public PlayerListener(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.updateShownStateFor(event.getPlayer());
        }

        if (!this.plugin.getHologramConfiguration().areVersionNotificationsMuted() && event.getPlayer().hasPermission("fancyholograms.admin")) {
            FancyHolograms.get().getHologramThread().submit(() -> FancyHolograms.get().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.hideHologram(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.updateShownStateFor(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
            hologram.updateShownStateFor(event.getPlayer());
        }
    }

}
