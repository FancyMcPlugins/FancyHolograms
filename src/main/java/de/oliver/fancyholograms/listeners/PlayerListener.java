package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PlayerListener implements Listener {

    private final @NotNull FancyHologramsPlugin plugin;

    private final Map<UUID, List<UUID>> loadingResourcePacks;

    public PlayerListener(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
        this.loadingResourcePacks = new HashMap<>();
    }

    // For 1.20.2 and higher this method returns actual pack identifier, while for older versions, the identifier is a dummy UUID full of zeroes.
    // Versions prior 1.20.2 supports sending and receiving only one resource-pack and a dummy, constant identifier can be used as a key.
    private static @NotNull UUID getResourcePackID(final @NotNull PlayerResourcePackStatusEvent event) {
        try {
            event.getClass().getMethod("getID");
            return event.getID();
        } catch (final @NotNull NoSuchMethodException e) {
            return new UUID(0,0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        for (final var hologram : this.plugin.getRegistry().getAll()) {
            hologram.removeViewer(event.getPlayer().getUniqueId());
            FancyHologramsPlugin.get().getController().refreshHologram(hologram, event.getPlayer());
        }

        if (!this.plugin.getHologramConfiguration().areVersionNotificationsMuted() && event.getPlayer().hasPermission("fancyholograms.admin")) {
            FancyHologramsPlugin.get().getHologramThread().submit(() -> FancyHologramsPlugin.get().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        FancyHologramsPlugin.get().getHologramThread().submit(() -> {
            for (final var hologram : this.plugin.getRegistry().getAll()) {
                hologram.removeViewer(event.getPlayer().getUniqueId());
                FancyHologramsPlugin.get().getController().refreshHologram(hologram, event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        for (final Hologram hologram : this.plugin.getRegistry().getAll()) {
            FancyHologramsPlugin.get().getController().refreshHologram(hologram, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        for (final Hologram hologram : this.plugin.getRegistry().getAll()) {
            hologram.removeViewer(event.getPlayer().getUniqueId());
            FancyHologramsPlugin.get().getController().refreshHologram(hologram, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onResourcePackStatus(@NotNull final PlayerResourcePackStatusEvent event) {
        // Skipping event calls before player has fully loaded to the server.
        // This should fix NPE due to vanillaPlayer.connection being null when sending resource-packs in the configuration stage.
        if (!event.getPlayer().isOnline())
            return;
        final UUID playerUniqueId = event.getPlayer().getUniqueId();
        final UUID packUniqueId = getResourcePackID(event);
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
                for (final Hologram hologram : this.plugin.getRegistry().getAll()) {
                    FancyHologramsPlugin.get().getController().refreshHologram(hologram, event.getPlayer());
                }
            }
        }
    }

}
