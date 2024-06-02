package de.oliver.fancyholograms;

import com.google.common.cache.CacheBuilder;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The FancyHologramsManager class is responsible for managing holograms in the FancyHolograms plugin.
 * It provides methods for adding, removing, and retrieving holograms, as well as other related operations.
 */
public final class HologramManagerImpl implements HologramManager {

    private final @NotNull FancyHolograms plugin;
    /**
     * The adapter function used to create holograms from hologram data.
     */
    private final @NotNull Function<HologramData, Hologram> adapter;
    /**
     * A map of hologram names to their corresponding hologram instances.
     */
    private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();
    /**
     * Whether holograms are loaded or not
     */
    private boolean isLoaded = false;

    HologramManagerImpl(@NotNull final FancyHolograms plugin, @NotNull final Function<HologramData, Hologram> adapter) {
        this.plugin = plugin;
        this.adapter = adapter;
    }

    /**
     * @return A read-only collection of loaded holograms.
     */
    @Override
    public @NotNull @UnmodifiableView Collection<Hologram> getHolograms() {
        return Collections.unmodifiableCollection(this.holograms.values());
    }

    /**
     * Returns a read-only view of the currently loaded persistent holograms.
     *
     * @return A read-only collection of holograms.
     */
    @Override
    public @NotNull
    @UnmodifiableView Collection<Hologram> getPersistentHolograms() {
        return this.holograms.values().stream().filter(hologram -> hologram.getData().isPersistent()).toList();
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
        removeHologram(hologram.getData().getName());
    }

    /**
     * Removes a hologram from this manager by name.
     *
     * @param name The name of the hologram to remove.
     * @return An optional containing the removed hologram, or empty if not found.
     */
    public @NotNull Optional<Hologram> removeHologram(@NotNull final String name) {
        Optional<Hologram> optionalHologram = Optional.ofNullable(this.holograms.remove(name.toLowerCase(Locale.ROOT)));

        optionalHologram.ifPresent(hologram -> {
                final var online = List.copyOf(Bukkit.getOnlinePlayers());
                hologram.hideHologram(online);

                FancyHolograms.get().getHologramThread().submit(() -> plugin.getHologramStorage().delete(hologram));
            }
        );

        return optionalHologram;
    }

    /**
     * Creates a new hologram with the specified hologram data.
     *
     * @param data The hologram data for the new hologram.
     * @return The created hologram.
     */
    public @NotNull Hologram create(@NotNull final HologramData data) {
        Hologram hologram = this.adapter.apply(data);
        hologram.createHologram();
        return hologram;
    }

    public void saveHolograms() {
        if (!isLoaded) {
            return;
        }

        plugin.getHologramStorage().saveBatch(getPersistentHolograms(), true);
    }

    public void loadHolograms() {
        plugin.getHologramStorage().loadAll().forEach(this::addHologram);
        isLoaded = true;
    }

    /**
     * Initializes tasks for managing holograms, such as loading and refreshing them.
     *
     * @apiNote This method is intended to be called internally by the plugin.
     */
    void initializeTasks() {
        ScheduledExecutorService hologramThread = plugin.getHologramThread();
        hologramThread.schedule(() -> {
            loadHolograms();

            hologramThread.scheduleAtFixedRate(() -> {
                for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        hologram.forceUpdateShownStateFor(player);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }, 6, TimeUnit.SECONDS);

        final var updateTimes = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(5))
            .<String, Long>build();

        hologramThread.scheduleAtFixedRate(() -> {
            final var time = System.currentTimeMillis();

            for (final var hologram : getHolograms()) {
                HologramData data = hologram.getData();
                if (data.hasChanges()) {
                    hologram.forceUpdate();
                    hologram.refreshForViewersInWorld();
                    data.setHasChanges(false);

                    if (data instanceof TextHologramData) {
                        updateTimes.put(hologram.getData().getName(), time);
                    }
                }
                else if (data instanceof TextHologramData textData) {
                    final var interval = textData.getTextUpdateInterval();
                    if (interval < 1) {
                        continue; // doesn't update
                    }

                    final var lastUpdate = updateTimes.asMap().get(data.getName());
                    if (lastUpdate != null && time < (lastUpdate + interval)) {
                        continue;
                    }

                    if (lastUpdate == null || time > (lastUpdate + interval)) {
                        hologram.refreshForViewersInWorld();
                        updateTimes.put(data.getName(), time);
                    }
                }
            }
        }, 50, 1000, TimeUnit.MILLISECONDS);
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

        for (final var hologram : this.getPersistentHolograms()) {
            this.holograms.remove(hologram.getName());
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

        final var npc = FancyNpcsPlugin.get().getNpcManager().getNpc(linkedNpcName);
        if (npc == null) {
            return;
        }

        npc.getData().setDisplayName("<empty>");
        npc.getData().setShowInTab(false);
        npc.updateForAll();

        final var location = npc.getData().getLocation().clone().add(0, npc.getEyeHeight() + 0.5, 0);
        hologram.getData().setLocation(location);
    }
}
