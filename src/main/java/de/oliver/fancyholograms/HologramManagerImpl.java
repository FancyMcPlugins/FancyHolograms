package de.oliver.fancyholograms;

import com.google.common.cache.CacheBuilder;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * The FancyHologramsManager class is responsible for managing holograms in the FancyHolograms plugin.
 * It provides methods for adding, removing, and retrieving holograms, as well as other related operations.
 */
public final class HologramManagerImpl implements HologramManager {

    @NotNull
    private final FancyHolograms plugin;
    /**
     * The adapter function used to create holograms from hologram data.
     */
    @NotNull
    private final Function<HologramData, Hologram> adapter;

    /**
     * A map of hologram names to their corresponding hologram instances.
     */
    private final Map<String, Hologram> holograms = new HashMap<>();

    /*+
        If the holograms are loaded or not
     */
    private boolean isLoaded = false;


    HologramManagerImpl(@NotNull final FancyHolograms plugin, @NotNull final Function<HologramData, Hologram> adapter) {
        this.plugin = plugin;
        this.adapter = adapter;
    }


    /**
     * Returns a read-only view of the currently loaded holograms.
     *
     * @return A read-only collection of holograms.
     */
    @Override
    public @NotNull
    @UnmodifiableView Collection<Hologram> getHolograms() {
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
        removeHologram(hologram.getData().getName());
    }

    /**
     * Removes a hologram from this manager by name.
     *
     * @param name The name of the hologram to remove.
     * @return An optional containing the removed hologram, or empty if not found.
     */
    public @NotNull Optional<Hologram> removeHologram(@NotNull final String name) {
        FancyHolograms.get().getHologramsConfig().removeHologramFromConfig(HologramsConfig.HOLOGRAMS_CONFIG_FILE, name);

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

    public void saveHolograms() {
        if (!isLoaded) {
            return;
        }

        FancyHolograms.get().getHologramsConfig().writeHolograms(HologramsConfig.HOLOGRAMS_CONFIG_FILE, getHolograms());
    }

    public void loadHolograms() {
        // try to load holograms from config.yml but then remove from there
        YamlConfiguration pluginConfig = YamlConfiguration.loadConfiguration(HologramsConfig.PLUGIN_CONFIG_FILE);
        if (pluginConfig.isConfigurationSection("holograms")) {
            FancyHolograms.get().getHologramsConfig().readHolograms(HologramsConfig.PLUGIN_CONFIG_FILE).forEach(this::addHologram);
            pluginConfig.set("holograms", null);

            try {
                pluginConfig.save(HologramsConfig.PLUGIN_CONFIG_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        FancyHolograms.get().getHologramsConfig().readHolograms(HologramsConfig.HOLOGRAMS_CONFIG_FILE).forEach(this::addHologram);

        isLoaded = true;
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
                if (!(hologram.getData().getTypeData() instanceof TextHologramData textData)) {
                    continue;
                }

                final var interval = textData.getTextUpdateInterval();
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
        final var linkedNpcName = hologram.getData().getDisplayData().getLinkedNpcName();
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
        hologram.getData().getDisplayData().setLocation(location);
    }

    /**
     * Refreshes the hologram for players in the world associated with the hologram's location.
     *
     * @param hologram The hologram to refresh.
     */
    public void refreshHologramForPlayersInWorld(@NotNull final Hologram hologram) {
        final var players = ofNullable(hologram.getData().getDisplayData().getLocation())
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
