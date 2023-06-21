package de.oliver.fancyholograms;

import com.google.common.cache.CacheBuilder;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * The FancyHologramsManager class is responsible for managing holograms in the FancyHolograms plugin.
 * It provides methods for adding, removing, and retrieving holograms, as well as other related operations.
 */
public final class FancyHologramsManager {

    @NotNull
    private final FancyHologramsPlugin             plugin;
    /**
     * The adapter function used to create holograms from hologram data.
     */
    @NotNull
    private final Function<HologramData, Hologram> adapter;

    /**
     * A map of hologram names to their corresponding hologram instances.
     */
    private final Map<String, Hologram> holograms = new HashMap<>();


    FancyHologramsManager(@NotNull final FancyHologramsPlugin plugin, @NotNull final Function<HologramData, Hologram> adapter) {
        this.plugin  = plugin;
        this.adapter = adapter;
    }


    /**
     * Returns a read-only view of the currently loaded holograms.
     *
     * @return A read-only collection of holograms.
     */
    public @NotNull @UnmodifiableView Collection<Hologram> getHolograms() {
        return Collections.unmodifiableCollection(this.holograms.values());
    }


    /**
     * Finds a hologram by name.
     *
     * @param name The name of the hologram to lookup.
     * @return An optional containing the found hologram, or empty if not found.
     */
    public @NotNull Optional<Hologram> getHologram(@NotNull final String name) {
        return Optional.ofNullable(this.holograms.get(name.toLowerCase(Locale.ROOT)));
    }

    /**
     * Adds a hologram to this manager.
     *
     * @param hologram The hologram to add.
     */
    public void addHologram(@NotNull final Hologram hologram) {
        this.holograms.put(hologram.getData().getName().toLowerCase(Locale.ROOT), hologram);
    }

    /**
     * Removes a hologram from this manager.
     *
     * @param hologram The hologram to remove.
     */
    public void removeHologram(@NotNull final Hologram hologram) {
        this.holograms.remove(hologram.getData().getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Removes a hologram from this manager by name.
     *
     * @param name The name of the hologram to remove.
     * @return An optional containing the removed hologram, or empty if not found.
     */
    public @NotNull Optional<Hologram> removeHologram(@NotNull final String name) {
        return ofNullable(this.holograms.remove(name.toLowerCase(Locale.ROOT)));
    }


    /**
     * Creates a new hologram with the specified hologram data.
     *
     * @param data The hologram data for the new hologram.
     * @return The created hologram.
     */
    public @NotNull Hologram create(@NotNull final HologramData data) {
        return this.adapter.apply(data);
    }


    /**
     * Initializes tasks for managing holograms, such as loading and refreshing them.
     *
     * @apiNote This method is intended to be called internally by the plugin.
     */
    void initializeTasks() {
        this.plugin.getScheduler().runTaskLater(null, 20L * 6, () -> {
            loadHolograms();

            final var online = List.copyOf(Bukkit.getOnlinePlayers());

            getHolograms().forEach(hologram -> hologram.showHologram(online));
        });


        final var updateTimes = CacheBuilder.newBuilder()
                                            .expireAfterAccess(Duration.ofMinutes(5))
                                            .<String, Long>build();

        this.plugin.getScheduler().runTaskTimerAsynchronously(20L, 1L, () -> {
            final var time = System.currentTimeMillis();

            for (final var hologram : getHolograms()) {
                final var interval = hologram.getData().getTextUpdateInterval();
                if (interval < 1) {
                    continue; // doesn't update
                }

                final var lastUpdate = updateTimes.asMap().get(hologram.getData().getName());

                if (lastUpdate != null && time < (lastUpdate + interval)) {
                    continue;
                }

                refreshHologramForPlayersInWorld(hologram);

                updateTimes.put(hologram.getData().getName(), time);
            }
        });
    }


    /**
     * Loads holograms from the plugin's configuration and adds them to the manager.
     */
    public void loadHolograms() {
        for (final var data : this.plugin.getConfiguration().loadHolograms().values()) {
            addHologram(create(data));
        }
    }

    /**
     * Saves the holograms managed by this manager to the plugin's configuration.
     */
    public void saveHolograms() {
        this.plugin.getConfiguration()
                   .saveHolograms(getHolograms().stream()
                                                .map(Hologram::getData)
                                                .toList());
    }


    /**
     * Reloads holograms by clearing the existing holograms and loading them again from the plugin's configuration.
     */
    public void reloadHolograms() {
        clearHolograms();

        loadHolograms();
    }


    private void clearHolograms() {
        final var online = List.copyOf(Bukkit.getOnlinePlayers());

        final var holograms = Map.copyOf(this.holograms);

        this.holograms.clear();

        for (final var hologram : holograms.values()) {
            hologram.hideHologram(online);
        }
    }


    /**
     * Syncs a hologram with its linked NPC, if any.
     *
     * @param hologram The hologram to sync.
     */
    public void syncHologramWithNpc(@NotNull final Hologram hologram) {
        final var linkedNpcName = hologram.getData().getLinkedNpcName();
        if (linkedNpcName == null) {
            return;
        }

        final var npc = FancyNpcs.getInstance().getNpcManager().getNpc(linkedNpcName);
        if (npc == null) {
            return;
        }

        npc.updateDisplayName("<empty>");
        npc.updateShowInTab(false);

        final var location = npc.getLocation().clone().add(0, npc.getEyeHeight(), 0);
        hologram.getData().setLocation(location);
    }

    /**
     * Refreshes the hologram for players in the world associated with the hologram's location.
     *
     * @param hologram The hologram to refresh.
     */
    public void refreshHologramForPlayersInWorld(@NotNull final Hologram hologram) {
        final var players = ofNullable(hologram.getData().getLocation())
                .map(Location::getWorld)
                .map(World::getPlayers)
                .orElse(Collections.emptyList());

        hologram.refreshHologram(players);
    }

    /**
     * Refreshes the hologram for the players it is currently shown to.
     *
     * @param hologram The hologram to refresh.
     */
    public void refreshHologramForPlayersShownTo(@NotNull final Hologram hologram) {
        final var players = hologram.getShownToPlayers()
                                    .stream()
                                    .map(Bukkit::getPlayer)
                                    .toList();

        hologram.refreshHologram(players);
    }

}
