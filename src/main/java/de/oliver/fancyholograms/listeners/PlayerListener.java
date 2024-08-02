package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.resource.ResourcePackStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public final class PlayerListener implements Listener {

    private final @NotNull FancyHolograms plugin;

    private final Map<UUID, List<UUID>> loadingResourcePacks;

    public PlayerListener(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
        this.loadingResourcePacks = new HashMap<>();
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onResourcePackStatus(@NotNull final PlayerResourcePackStatusEvent event) {
        final UUID playerUniqueId = event.getPlayer().getUniqueId();
        final UUID packUniqueId = event.getID();
        // Adding accepted resource-pack to the list of currently loading resource-packs for that player.
        if (event.getStatus() == Status.ACCEPTED)
            loadingResourcePacks.computeIfAbsent(playerUniqueId, (___) -> new ArrayList<>()).add(packUniqueId);
        // Once successfully loaded (or failed to download), removing resource-pack from the map.
        else if (event.getStatus() == Status.SUCCESSFULLY_LOADED || event.getStatus() == Status.FAILED_DOWNLOAD) {
            loadingResourcePacks.computeIfAbsent(playerUniqueId, (___) -> new ArrayList<>()).removeIf(uuid -> uuid.equals(packUniqueId));
            // Refreshing holograms once (possibly) all resource-packs are loaded.
            if (loadingResourcePacks.get(playerUniqueId) != null && loadingResourcePacks.get(playerUniqueId).isEmpty()) {
                // Removing player from the map, as they're no longer needed here.
                loadingResourcePacks.remove(playerUniqueId);
                // Refreshing holograms as to make sure custom textures are loaded.
                for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
                    hologram.refreshHologram(event.getPlayer());
                }
            }
        }
    }

}
