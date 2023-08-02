package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class PlayerListener implements Listener {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public PlayerListener(@NotNull final FancyHologramsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        final var visibilityDistance = this.plugin.getConfiguration().getVisibilityDistance();

        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            final var distance = hologram.distanceTo(event.getPlayer().getLocation());
            if (Double.isNaN(distance) || distance > visibilityDistance) {
                continue;
            }

            hologram.showHologram(event.getPlayer());
        }

        if (!this.plugin.getConfiguration().areVersionNotificationsMuted() && event.getPlayer().hasPermission("fancyholograms.admin")) {
            final var current = new ComparableVersion(plugin.getDescription().getVersion());

            supplyAsync(this.plugin.getVersionFetcher()::getNewestVersion)
                    .thenApply(Objects::requireNonNull)
                    .whenComplete((newest, error) -> {
                        if (error != null || newest.compareTo(current) <= 0) {
                            return; // could not get the newest version or already on latest
                        }

                        MessageHelper.warning(event.getPlayer(), """
                                <%warning_color%>You are using an outdated version of the FancyHolograms plugin (%s).
                                <%warning_color%>Please download the newest version (%s): <click:open_url:'%s'><u>click here</u></click>.</color>
                                """.replace("%warning_color%", MessageHelper.getWarningColor())
                                .formatted(current, newest, this.plugin.getVersionFetcher().getDownloadUrl()));
                    });
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

        final var visibilityDistance = this.plugin.getConfiguration().getVisibilityDistance();

        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            final var distance = hologram.distanceTo(event.getTo());
            if (Double.isNaN(distance)) {
                continue;
            }

            final var inRange = distance <= visibilityDistance;
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
        final var visibilityDistance = this.plugin.getConfiguration().getVisibilityDistance();

        for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
            final var distance = hologram.distanceTo(event.getPlayer().getLocation());
            if (Double.isNaN(distance) || distance > visibilityDistance) {
                continue;
            }

            hologram.showHologram(event.getPlayer());
        }
    }

}
